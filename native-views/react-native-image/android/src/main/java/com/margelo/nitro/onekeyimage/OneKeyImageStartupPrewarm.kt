package com.margelo.nitro.onekeyimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import com.bumptech.glide.load.Key
import com.bumptech.glide.signature.ObjectKey
import java.io.ByteArrayInputStream
import java.io.File
import java.security.MessageDigest
import java.util.Base64
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.floor
import kotlin.math.min

/**
 * Records the first-screen image working set and warms decoded bitmaps into a
 * bounded process-wide memory cache at process startup.
 *
 * Persisted entries store only hashed cache identities (`keyHash`, `diskFile`,
 * optional `rawDiskFile`) and target pixel dimensions — never plaintext URLs,
 * `onekey-avatar://` wallet addresses, file paths, or `data:` payloads.
 */
internal object OneKeyImageStartupPrewarm {
  internal data class StartupEntry(
    val keyHash: String,
    val diskFile: String,
    val rawDiskFile: String? = null,
    val width: Int,
    val height: Int,
    val round: Boolean = false,
    val launch: Int = 0,
  ) {
    val identity: String
      get() = "$keyHash|$width|$height|$round"
  }

  internal data class PrewarmMetrics(
    val entryCount: Int = 0,
    val decodedCount: Int = 0,
    val durationMs: Double = 0.0,
  )

  private const val MAX_ENTRIES = 48
  private const val RECORD_DURATION_MS = 3_000L
  private const val MAX_DURATION_MS = 60_000L
  private const val MIN_SETTLED_COUNT = 2
  private const val MAX_DATA_URL_LENGTH = 65_536
  private const val MAX_LAUNCH_AGE = 3
  private const val MAX_MEMORY_ENTRIES = 64
  private const val MAX_MEMORY_BYTES = 16L * 1024L * 1024L
  private const val FILE_NAME = "onekey-image-startup-v1.json"
  internal const val GLIDE_DISK_CACHE_DIR = "image_manager_disk_cache"
  internal const val FALLBACK_DISK_CACHE_DIR = "onekey-image-startup-disk-v1"

  private val stateLock = Any()
  private val memoryLock = Any()
  private val recorded = ArrayList<StartupEntry>()
  private val recordedKeys = HashSet<String>()
  private val pendingFallbackSources = HashMap<String, String>()
  private var recordStartTimeMs: Long? = null
  private var recordingClosed = false
  private var flushScheduled = false
  private var debounceFuture: ScheduledFuture<*>? = null
  private var startupFileOverride: File? = null
  @Volatile
  private var baseCacheDir: File? = null
  private var lastPrewarmMetrics = PrewarmMetrics()

  private val memoryCache = LinkedHashMap<String, Bitmap>(MAX_MEMORY_ENTRIES, 0.75f, true)
  private var memoryCacheBytes = 0L

  private val scheduler = Executors.newSingleThreadScheduledExecutor { runnable ->
    Thread(runnable, "onekey-image-startup-record").apply { isDaemon = true }
  }
  private val prewarmExecutor = Executors.newSingleThreadExecutor { runnable ->
    Thread(runnable, "onekey-image-startup-prewarm").apply { isDaemon = true }
  }

  @Volatile
  internal var bitmapDecoderOverrideForTesting:
    ((ByteArray, Int, Int, Int, Int, Boolean) -> Bitmap?)? = null

  @Volatile
  internal var bitmapByteCountOverrideForTesting: ((Bitmap) -> Long)? = null

  internal fun setCacheDirIfNeeded(cacheDir: File?) {
    if (cacheDir != null && baseCacheDir == null) {
      baseCacheDir = cacheDir
    }
  }

  internal fun currentCacheDir(context: Context? = null): File? =
    startupFileOverride?.parentFile ?: context?.applicationContext?.cacheDir ?: baseCacheDir

  fun metricsSnapshot(): PrewarmMetrics = synchronized(stateLock) { lastPrewarmMetrics }

  fun keyHash(requestUrl: String, width: Int, height: Int, round: Boolean): String =
    sha256Hex("$requestUrl|$width|$height|$round".toByteArray(Charsets.UTF_8))

  fun glideDataSafeKey(uri: String): String? {
    if (uri.isBlank()) return null
    val sourceKey: Key = when {
      OneKeyBlockieAvatar.isAvatarUri(uri) ->
        OneKeyImageSafetyVersionedKey(ObjectKey(OneKeyBlockieAvatarModel(uri)))
      uri.startsWith("data:") ->
        OneKeyImageSafetyVersionedKey(ObjectKey(OneKeyImageDataUriModel(uri)))
      uri.startsWith("http://") || uri.startsWith("https://") ->
        OneKeyImageSafetyVersionedKey(OneKeyImageRemoteSourceKey(uri, null))
      uri.startsWith("file://") ->
        OneKeyImageSafetyVersionedKey(ObjectKey(uri))
      else -> return null
    }
    val digest = MessageDigest.getInstance("SHA-256")
    sourceKey.updateDiskCacheKey(digest)
    return digest.digest().joinToString("") { "%02x".format(Locale.ROOT, it.toInt() and 0xff) }
  }

  internal fun findDiskCacheFile(cacheDir: File, diskFile: String): File? {
    if (!isSafeToken(diskFile)) return null
    val glideFile = File(File(cacheDir, GLIDE_DISK_CACHE_DIR), "$diskFile.0")
    if (glideFile.isFile) return glideFile
    val fallbackFile = File(File(cacheDir, FALLBACK_DISK_CACHE_DIR), diskFile)
    if (fallbackFile.isFile) return fallbackFile
    return null
  }

  internal fun ensureFallbackDiskBytesAsync(cacheDir: File, uri: String, bytes: ByteArray) {
    val safeKey = glideDataSafeKey(uri) ?: return
    if (findDiskCacheFile(cacheDir, safeKey) != null) return
    scheduler.execute {
      writeFallbackDiskBytes(cacheDir, safeKey, bytes)
    }
  }

  internal fun writeFallbackDiskBytes(cacheDir: File, safeKey: String, bytes: ByteArray) {
    if (!isSafeToken(safeKey) || bytes.isEmpty()) return
    try {
      val dir = File(cacheDir, FALLBACK_DISK_CACHE_DIR)
      if (!dir.exists()) dir.mkdirs()
      val target = File(dir, safeKey)
      if (target.isFile && target.length() == bytes.size.toLong()) return
      val tmp = File(dir, "$safeKey.tmp")
      tmp.writeBytes(bytes)
      if (!tmp.renameTo(target)) {
        tmp.copyTo(target, overwrite = true)
        tmp.delete()
      }
    } catch (_: Exception) {
    }
  }

  fun cachedBitmap(keyHash: String): Bitmap? = synchronized(memoryLock) {
    memoryCache[keyHash]
  }

  fun cachedBitmap(requestUrl: String, width: Int, height: Int, round: Boolean): Bitmap? =
    cachedBitmap(keyHash(requestUrl, width, height, round))

  fun putMemoryBitmap(keyHash: String, bitmap: Bitmap) {
    val bytes = estimateBitmapBytes(bitmap)
    if (bytes > MAX_MEMORY_BYTES) return
    synchronized(memoryLock) {
      memoryCache.put(keyHash, bitmap)?.let { previous ->
        memoryCacheBytes = (memoryCacheBytes - estimateBitmapBytes(previous)).coerceAtLeast(0L)
      }
      memoryCacheBytes += bytes
      while (memoryCache.size > MAX_MEMORY_ENTRIES || memoryCacheBytes > MAX_MEMORY_BYTES) {
        val eldest = memoryCache.entries.firstOrNull() ?: break
        memoryCacheBytes = (memoryCacheBytes - estimateBitmapBytes(eldest.value)).coerceAtLeast(0L)
        memoryCache.remove(eldest.key)
      }
    }
  }

  fun clearMemoryCache() {
    synchronized(memoryLock) {
      memoryCache.clear()
      memoryCacheBytes = 0L
    }
  }

  private fun estimateBitmapBytes(bitmap: Bitmap): Long {
    bitmapByteCountOverrideForTesting?.let { return it(bitmap) }
    return try {
      val count = bitmap.allocationByteCount.toLong()
      if (count > 0L) count else (bitmap.width.toLong() * bitmap.height.toLong() * 4L).coerceAtLeast(1L)
    } catch (_: Throwable) {
      4096L
    }
  }

  fun note(
    cacheDir: File?,
    requestUrl: String,
    rawUrl: String,
    headersJson: String?,
    cachePolicy: OneKeyImageCachePolicy?,
    width: Int,
    height: Int,
    round: Boolean,
  ) {
    if (cacheDir != null) setCacheDirIfNeeded(cacheDir)
    val policy = cachePolicy ?: OneKeyImageCachePolicy.MEMORY_DISK
    if (policy != OneKeyImageCachePolicy.MEMORY_DISK) return
    if (OneKeyImageModel.headers(headersJson).isNotEmpty()) return
    if (width <= 0 || height <= 0) return
    if (!isSupportedStartupUri(requestUrl) || !isSupportedStartupUri(rawUrl)) return
    if (requestUrl.startsWith("data:") && requestUrl.length > MAX_DATA_URL_LENGTH) return
    if (rawUrl.startsWith("data:") && rawUrl.length > MAX_DATA_URL_LENGTH) return

    val entryKeyHash = keyHash(requestUrl, width, height, round)
    val diskFile = glideDataSafeKey(requestUrl) ?: return
    val rawDiskFile = if (requestUrl != rawUrl) glideDataSafeKey(rawUrl) else null
    val entry = StartupEntry(
      keyHash = entryKeyHash,
      diskFile = diskFile,
      rawDiskFile = rawDiskFile,
      width = width,
      height = height,
      round = round,
      launch = 0,
    )

    val now = System.currentTimeMillis()
    synchronized(stateLock) {
      if (recordingClosed) return
      val start = recordStartTimeMs ?: now.also { recordStartTimeMs = it }
      if (now - start >= MAX_DURATION_MS && recorded.isNotEmpty()) {
        recordingClosed = true
        schedulePersistLocked(delayMs = 0L)
        return
      }
      if (recorded.size < MAX_ENTRIES && recordedKeys.add(entry.identity)) {
        recorded.add(entry)
      }
      if (requestUrl.startsWith("file://") || requestUrl.startsWith("data:") || OneKeyBlockieAvatar.isAvatarUri(requestUrl)) {
        pendingFallbackSources.putIfAbsent(diskFile, requestUrl)
      }
      schedulePersistLocked(delayMs = RECORD_DURATION_MS)
    }
  }

  private fun isSupportedStartupUri(uri: String): Boolean =
    uri.startsWith("http://") ||
      uri.startsWith("https://") ||
      OneKeyBlockieAvatar.isAvatarUri(uri) ||
      uri.startsWith("file://") ||
      uri.startsWith("data:")

  private fun schedulePersistLocked(delayMs: Long) {
    debounceFuture?.cancel(false)
    flushScheduled = true
    debounceFuture = scheduler.schedule({
      debounceFired()
    }, delayMs, TimeUnit.MILLISECONDS)
  }

  private fun debounceFired() {
    val entries: List<StartupEntry>
    val fallbackMap: Map<String, String>
    val file: File?
    val cacheDir: File?
    val now = System.currentTimeMillis()
    synchronized(stateLock) {
      if (recordingClosed && !flushScheduled) return
      val start = recordStartTimeMs ?: now
      if (
        !recordingClosed &&
        recorded.size < MIN_SETTLED_COUNT &&
        now - start < MAX_DURATION_MS
      ) {
        schedulePersistLocked(delayMs = RECORD_DURATION_MS)
        return
      }
      recordingClosed = true
      flushScheduled = false
      debounceFuture = null
      entries = ArrayList(recorded)
      fallbackMap = HashMap(pendingFallbackSources)
      pendingFallbackSources.clear()
      file = startupFile()
      cacheDir = currentCacheDir()
    }
    if (cacheDir != null && fallbackMap.isNotEmpty()) {
      materializeFallbackSources(cacheDir, fallbackMap)
    }
    if (file != null) {
      persist(entries, file)
    }
  }

  private fun materializeFallbackSources(cacheDir: File, sources: Map<String, String>) {
    for ((diskFile, uri) in sources) {
      if (findDiskCacheFile(cacheDir, diskFile) != null) continue
      val bytes = try {
        when {
          uri.startsWith("file://") -> {
            val rawPath = uri.removePrefix("file://").substringBefore('?').substringBefore('#')
            val f = File(rawPath)
            if (f.isFile && f.length() in 1L..OneKeyImageSafety.MAX_ENCODED_BYTES) f.readBytes() else null
          }
          uri.startsWith("data:") -> {
            val comma = uri.indexOf(',')
            if (comma > 0 && uri.substring(0, comma).lowercase(Locale.ROOT).contains(";base64")) {
              val payloadStart = comma + 1
              val count = OneKeyImageSafety.dataUriDecodedByteCount(uri, payloadStart)
              if (count in 1L..OneKeyImageSafety.MAX_DATA_URI_DECODED_BYTES) {
                Base64.getMimeDecoder().decode(uri.substring(payloadStart))
              } else null
            } else null
          }
          OneKeyBlockieAvatar.isAvatarUri(uri) -> OneKeyBlockieAvatar.png(uri)
          else -> null
        }
      } catch (_: Exception) {
        null
      }
      if (bytes != null && bytes.isNotEmpty()) {
        writeFallbackDiskBytes(cacheDir, diskFile, bytes)
      }
    }
  }

  private fun startupFile(): File? {
    startupFileOverride?.let { return it }
    val dir = baseCacheDir ?: return null
    return File(dir, FILE_NAME)
  }

  private fun persist(entries: List<StartupEntry>, file: File) {
    if (entries.isEmpty()) return
    val existing = readEntries(file)
    val maxExistingLaunch = existing.maxOfOrNull { it.launch } ?: 0
    val currentLaunch = maxExistingLaunch + 1

    val stampedCurrent = entries.map {
      StartupEntry(
        keyHash = it.keyHash,
        diskFile = it.diskFile,
        rawDiskFile = it.rawDiskFile,
        width = it.width,
        height = it.height,
        round = it.round,
        launch = currentLaunch,
      )
    }
    val seen = stampedCurrent.mapTo(HashSet()) { it.identity }
    val merged = ArrayList<StartupEntry>(MAX_ENTRIES)
    merged.addAll(stampedCurrent.take(MAX_ENTRIES))

    for (entry in existing) {
      if (merged.size >= MAX_ENTRIES) break
      if (!isValidEntry(entry)) continue
      val age = currentLaunch - entry.launch
      if (age >= MAX_LAUNCH_AGE) continue
      if (seen.add(entry.identity)) {
        merged.add(entry)
      }
    }

    if (
      existing.size == merged.size &&
      existing.indices.all { i ->
        existing[i].identity == merged[i].identity &&
          existing[i].diskFile == merged[i].diskFile &&
          existing[i].rawDiskFile == merged[i].rawDiskFile &&
          (currentLaunch - existing[i].launch) < 1
      }
    ) {
      return
    }

    try {
      file.parentFile?.mkdirs()
      val json = encodeEntriesJson(merged)
      val tmp = File(file.parentFile, "${file.name}.tmp")
      tmp.writeText(json, Charsets.UTF_8)
      if (!tmp.renameTo(file)) {
        tmp.copyTo(file, overwrite = true)
        tmp.delete()
      }
    } catch (_: Exception) {
    }
  }

  fun startPrewarmAsync(context: Context) {
    val appContext = context.applicationContext ?: context
    setCacheDirIfNeeded(appContext.cacheDir)
    prewarmExecutor.execute {
      prewarmStartupSet(appContext.cacheDir)
    }
  }

  fun prewarmStartupSet(cacheDir: File? = currentCacheDir()) {
    val startNs = System.nanoTime()
    if (cacheDir != null) setCacheDirIfNeeded(cacheDir)
    val file = synchronized(stateLock) { startupFile() } ?: return
    val resolvedCacheDir = cacheDir ?: file.parentFile ?: return
    val entries = readEntries(file)
      .filter { isValidEntry(it) }
      .take(MAX_ENTRIES)
    if (entries.isEmpty()) {
      val elapsedMs = (System.nanoTime() - startNs) / 1_000_000.0
      synchronized(stateLock) {
        lastPrewarmMetrics = PrewarmMetrics(0, 0, elapsedMs)
      }
      return
    }

    var decodedCount = 0
    for (entry in entries) {
      if (cachedBitmap(entry.keyHash) != null) {
        decodedCount += 1
        continue
      }
      val bytes = loadCachedDiskBytes(resolvedCacheDir, entry) ?: continue
      val metadata = try {
        OneKeyEncodedImageInspector.inspect(ByteArrayInputStream(bytes))
      } catch (_: Exception) {
        continue
      }
      if (metadata.animated) continue
      val bounds = if (metadata.width > 0 && metadata.height > 0) {
        metadata.width to metadata.height
      } else {
        OneKeyMainThreadDecode.extractStaticImageBounds(bytes)
      } ?: continue
      try {
        OneKeyImageSafety.requireStaticDimensions(bounds.first, bounds.second)
      } catch (_: Exception) {
        continue
      }
      val bitmap = decodeStaticBitmap(
        bytes = bytes,
        sourceWidth = bounds.first,
        sourceHeight = bounds.second,
        targetWidth = entry.width,
        targetHeight = entry.height,
        round = entry.round,
      ) ?: continue
      putMemoryBitmap(entry.keyHash, bitmap)
      decodedCount += 1
    }

    val totalMs = (System.nanoTime() - startNs) / 1_000_000.0
    synchronized(stateLock) {
      lastPrewarmMetrics = PrewarmMetrics(
        entryCount = entries.size,
        decodedCount = decodedCount,
        durationMs = totalMs,
      )
    }
    try {
      Log.i(
        "OneKeyImage",
        String.format(
          Locale.ROOT,
          "[OneKeyImageStartupMetrics] imagePrewarm entries=%d decoded=%d totalMs=%.3f",
          entries.size,
          decodedCount,
          totalMs,
        ),
      )
    } catch (_: Throwable) {
    }
  }

  private fun loadCachedDiskBytes(cacheDir: File, entry: StartupEntry): ByteArray? {
    val candidates = if (!entry.rawDiskFile.isNullOrEmpty() && entry.rawDiskFile != entry.diskFile) {
      listOf(entry.diskFile, entry.rawDiskFile)
    } else {
      listOf(entry.diskFile)
    }
    for (token in candidates) {
      val file = findDiskCacheFile(cacheDir, token) ?: continue
      val length = file.length()
      if (length <= 0L || length > OneKeyImageSafety.MAX_ENCODED_BYTES) continue
      val bytes = try {
        file.readBytes()
      } catch (_: Exception) {
        continue
      }
      if (bytes.isNotEmpty() && bytes.size <= OneKeyImageSafety.MAX_ENCODED_BYTES) {
        return bytes
      }
    }
    return null
  }

  internal fun decodeStaticBitmap(
    bytes: ByteArray,
    sourceWidth: Int,
    sourceHeight: Int,
    targetWidth: Int,
    targetHeight: Int,
    round: Boolean,
  ): Bitmap? {
    val safeTarget = OneKeyImageDecodeDimensions.forRender(targetWidth, targetHeight)
    bitmapDecoderOverrideForTesting?.let { override ->
      return override(bytes, sourceWidth, sourceHeight, safeTarget.width, safeTarget.height, round)
    }
    return try {
      val scaleFactor = OneKeyImageDecodeDimensions.safeStaticScaleFactor(
        sourceWidth = sourceWidth,
        sourceHeight = sourceHeight,
        requestedWidth = safeTarget.width,
        requestedHeight = safeTarget.height,
      )
      val options = BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.ARGB_8888
        if (scaleFactor < 1f) {
          inScaled = true
          inDensity = 10_000
          inTargetDensity = (scaleFactor * 10_000f).toInt().coerceAtLeast(1)
        }
      }
      var decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options) ?: return null
      if (decoded.width <= 0 || decoded.height <= 0) return null
      if (decoded.allocationByteCount.toLong() > OneKeyImageDecodeDimensions.MAX_DECODE_BYTES) {
        val targetW = floor(sourceWidth * scaleFactor).toInt().coerceAtLeast(1)
        val targetH = floor(sourceHeight * scaleFactor).toInt().coerceAtLeast(1)
        val scaled = Bitmap.createScaledBitmap(decoded, targetW, targetH, true)
        if (scaled !== decoded) decoded.recycle()
        decoded = scaled
      }
      if (decoded.allocationByteCount.toLong() > OneKeyImageDecodeDimensions.MAX_DECODE_BYTES) {
        return null
      }
      if (round) circleCropBitmap(decoded) else decoded
    } catch (_: Throwable) {
      null
    }
  }

  private fun circleCropBitmap(source: Bitmap): Bitmap {
    val size = min(source.width, source.height).coerceAtLeast(1)
    val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val radius = size / 2f
    canvas.drawCircle(radius, radius, radius, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    val left = (size - source.width) / 2f
    val top = (size - source.height) / 2f
    canvas.drawBitmap(source, left, top, paint)
    return output
  }

  private fun isSafeToken(token: String): Boolean =
    token.isNotEmpty() &&
      !token.contains('/') &&
      !token.contains('\\') &&
      !token.contains("..")

  private fun isValidEntry(entry: StartupEntry): Boolean =
    entry.keyHash.isNotEmpty() &&
      isSafeToken(entry.diskFile) &&
      (entry.rawDiskFile == null || isSafeToken(entry.rawDiskFile)) &&
      entry.width > 0 &&
      entry.height > 0 &&
      entry.width.toLong() * entry.height.toLong() <= 4_194_304L

  internal fun sha256Hex(bytes: ByteArray): String =
    MessageDigest.getInstance("SHA-256")
      .digest(bytes)
      .joinToString("") { "%02x".format(Locale.ROOT, it.toInt() and 0xff) }

  internal fun encodeEntriesJson(entries: List<StartupEntry>): String {
    val sb = StringBuilder()
    sb.append('[')
    entries.forEachIndexed { index, entry ->
      if (index > 0) sb.append(',')
      sb.append('{')
      sb.append("\"keyHash\":\"").append(escapeJson(entry.keyHash)).append("\",")
      sb.append("\"diskFile\":\"").append(escapeJson(entry.diskFile)).append("\",")
      if (!entry.rawDiskFile.isNullOrEmpty()) {
        sb.append("\"rawDiskFile\":\"").append(escapeJson(entry.rawDiskFile)).append("\",")
      }
      sb.append("\"width\":").append(entry.width).append(',')
      sb.append("\"height\":").append(entry.height).append(',')
      sb.append("\"round\":").append(entry.round).append(',')
      sb.append("\"launch\":").append(entry.launch)
      sb.append('}')
    }
    sb.append(']')
    return sb.toString()
  }

  internal fun decodeEntriesJson(text: String): List<StartupEntry> {
    val result = ArrayList<StartupEntry>()
    val objectPattern = Regex("\\{([^{}]*)\\}")
    val stringFieldPattern = Regex("\"([A-Za-z0-9_]+)\"\\s*:\\s*\"([^\"]*)\"")
    val numberFieldPattern = Regex("\"([A-Za-z0-9_]+)\"\\s*:\\s*(-?\\d+)")
    val boolFieldPattern = Regex("\"([A-Za-z0-9_]+)\"\\s*:\\s*(true|false)")

    for (match in objectPattern.findAll(text)) {
      val body = match.groupValues[1]
      val strings = HashMap<String, String>()
      val numbers = HashMap<String, Int>()
      val bools = HashMap<String, Boolean>()
      for (sm in stringFieldPattern.findAll(body)) {
        strings[sm.groupValues[1]] = sm.groupValues[2]
      }
      for (nm in numberFieldPattern.findAll(body)) {
        nm.groupValues[2].toIntOrNull()?.let { numbers[nm.groupValues[1]] = it }
      }
      for (bm in boolFieldPattern.findAll(body)) {
        bools[bm.groupValues[1]] = bm.groupValues[2] == "true"
      }
      val keyHash = strings["keyHash"] ?: continue
      val diskFile = strings["diskFile"] ?: continue
      val rawDiskFile = strings["rawDiskFile"]?.takeIf { it.isNotEmpty() }
      val width = numbers["width"] ?: continue
      val height = numbers["height"] ?: continue
      val round = bools["round"] ?: false
      val launch = numbers["launch"] ?: 0
      result.add(
        StartupEntry(
          keyHash = keyHash,
          diskFile = diskFile,
          rawDiskFile = rawDiskFile,
          width = width,
          height = height,
          round = round,
          launch = launch,
        ),
      )
    }
    return result
  }

  private fun escapeJson(value: String): String =
    value.replace("\\", "\\\\").replace("\"", "\\\"")

  internal fun readEntries(file: File): List<StartupEntry> {
    if (!file.isFile) return emptyList()
    return try {
      decodeEntriesJson(file.readText(Charsets.UTF_8))
    } catch (_: Exception) {
      emptyList()
    }
  }

  internal fun resetForTesting(file: File? = null, cacheDir: File? = null) {
    synchronized(stateLock) {
      debounceFuture?.cancel(false)
      debounceFuture = null
      recorded.clear()
      recordedKeys.clear()
      pendingFallbackSources.clear()
      recordStartTimeMs = null
      recordingClosed = false
      flushScheduled = false
      startupFileOverride = file
      baseCacheDir = cacheDir ?: file?.parentFile
      lastPrewarmMetrics = PrewarmMetrics()
    }
    clearMemoryCache()
  }

  internal fun flushForTesting(forceClose: Boolean = true) {
    val entries: List<StartupEntry>
    val fallbackMap: Map<String, String>
    val file: File?
    val cacheDir: File?
    synchronized(stateLock) {
      debounceFuture?.cancel(false)
      debounceFuture = null
      if (!forceClose && recorded.size < MIN_SETTLED_COUNT) {
        return
      }
      recordingClosed = true
      flushScheduled = false
      entries = ArrayList(recorded)
      fallbackMap = HashMap(pendingFallbackSources)
      pendingFallbackSources.clear()
      file = startupFile()
      cacheDir = currentCacheDir()
    }
    if (cacheDir != null && fallbackMap.isNotEmpty()) {
      materializeFallbackSources(cacheDir, fallbackMap)
    }
    if (file != null) {
      persist(entries, file)
    }
  }
}
