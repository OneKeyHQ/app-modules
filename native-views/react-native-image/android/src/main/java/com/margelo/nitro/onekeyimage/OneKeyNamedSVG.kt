package com.margelo.nitro.onekeyimage

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.roundToInt

/** Process-owned named resources; no React runtime or Activity is retained. */
object OneKeyNamedSVG {
  enum class RecolorMode { FIXED, SINGLE, MULTIPLE, RASTER }
  data class IconMetadata(
    val width: Float,
    val height: Float,
    val mode: RecolorMode,
  )
  data class RenderResult(
    val bitmap: Bitmap,
    val mode: RecolorMode,
    val sourceIdentity: String,
    val maskAlpha: Int,
  ) {
    fun canTint(color: String): Boolean {
      val value = parseColor(color) ?: return false
      return mode == RecolorMode.FIXED || mode == RecolorMode.SINGLE ||
        (mode == RecolorMode.MULTIPLE && ((value ushr 24) and 0xff) == maskAlpha)
    }
    fun tintColor(color: String): Int? {
      val value = parseColor(color) ?: return null
      return when (mode) {
        RecolorMode.SINGLE -> value
        RecolorMode.MULTIPLE -> value or (0xff shl 24)
        else -> null
      }
    }
    internal fun finalBitmap(color: String): Bitmap {
      val tint = tintColor(color) ?: return bitmap
      if (tint == Color.BLACK) return bitmap
      return try {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply { colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN) }
        Canvas(output).drawBitmap(bitmap, 0f, 0f, paint)
        output
      } catch (_: Throwable) {
        bitmap
      }
    }
  }

  internal data class StartupEntry(
    val name: String,
    val color: String,
    val width: Int,
    val height: Int,
    val scale: Float,
    val launch: Int = 0,
  ) {
    val identity: String
      get() = "$name|$color|$width|$height|$scale"
  }

  internal data class StartupPrewarmMetrics(
    val entryCount: Int = 0,
    val decodedCount: Int = 0,
    val manifestMs: Double = 0.0,
    val totalMs: Double = 0.0,
  )

  private const val STARTUP_MAX_ENTRIES = 96
  private const val STARTUP_RECORD_DURATION_MS = 6_000L
  private const val STARTUP_MAX_DURATION_MS = 60_000L
  private const val STARTUP_MIN_SETTLED_COUNT = 8
  private const val STARTUP_MAX_LAUNCH_AGE = 3
  private const val STARTUP_FILE_NAME = "onekey-named-svg-startup-v1.json"
  private const val DISK_RASTER_DIR_NAME = "onekey-named-svg-v1"
  private const val MAX_DISK_RASTER_BYTES = 67_108_864L

  private data class Request(
    val assets: AssetManager?,
    val name: String,
    val color: String,
    val width: Int,
    val height: Int,
    val scale: Float,
    val finalColor: Boolean,
    val complete: (RenderResult?) -> Unit,
  )

  private val lock = Any()
  private val pending = LinkedHashMap<Any, Request>()
  private val cache = LinkedHashMap<String, RenderResult>(128, 0.75f, true)
  private var cacheBytes = 0L
  private var activeWorkers = 0
  @Volatile
  private var manifest: JSONObject? = null
  @Volatile
  private var manifestFallbackMap: Map<String, ManifestEntry>? = null
  private val manifestLock = Any()
  private val manifestAccessLock = ReentrantLock()
  private val workers = Executors.newFixedThreadPool(2)
  private val diskExecutor = Executors.newSingleThreadScheduledExecutor { runnable ->
    Thread(runnable, "onekey-named-svg-disk").apply { isDaemon = true }
  }
  private val main = try {
    Looper.getMainLooper()?.let { Handler(it) }
  } catch (_: Throwable) {
    null
  }
  private val colorPattern = Regex("rgba\\(\\d{1,3},\\d{1,3},\\d{1,3},(?:0(?:\\.\\d+)?|1(?:\\.0+)?)\\)")

  private var manifestMissingLogged = false

  private val startupLock = Any()
  private val startupRecorded = ArrayList<StartupEntry>()
  private val startupRecordedKeys = HashSet<String>()
  private var startupRecordStartTimeMs: Long? = null
  private var startupRecordingClosed = false
  private var startupFlushScheduled = false
  private var startupDebounceFuture: ScheduledFuture<*>? = null
  private var startupFileOverride: File? = null
  @Volatile
  private var baseCacheDir: File? = null
  private var lastPrewarmMetrics = StartupPrewarmMetrics()

  internal data class ManifestEntry(
    val digest: String,
    val mode: RecolorMode,
    val width: Float = 24f,
    val height: Float = 24f,
  )

  @Volatile
  internal var diskPngDecoderOverrideForTesting: ((File, Int, Int) -> Bitmap?)? = null

  @Volatile
  internal var bitmapByteCountOverrideForTesting: ((Bitmap) -> Long)? = null

  internal fun setCacheDirIfNeeded(cacheDir: File?) {
    if (cacheDir != null && baseCacheDir == null) {
      baseCacheDir = cacheDir
    }
  }

  internal fun startupMetricsSnapshot(): StartupPrewarmMetrics =
    synchronized(startupLock) { lastPrewarmMetrics }

  fun cancel(owner: Any) {
    synchronized(lock) { pending.remove(owner) }
  }

  /** Caller holds manifestLock. Hosts own the resources, so a miss is logged once. */
  private fun loadManifestLocked(assets: AssetManager?): JSONObject? {
    manifest?.let { return it }
    if (assets == null) return null
    manifestAccessLock.lock()
    return try {
      manifest?.let { return it }
      val raw = assets.open("icons/manifest.json").bufferedReader().use { it.readText() }
      JSONObject(raw).also { manifest = it }
    } catch (error: Exception) {
      if (!manifestMissingLogged) {
        manifestMissingLogged = true
        try {
          Log.w("OneKeyIcon", "assets/icons/manifest.json is missing or invalid; named icons will not render", error)
        } catch (_: Throwable) {
        }
      }
      null
    } finally {
      manifestAccessLock.unlock()
    }
  }

  private fun modeOf(metadata: JSONObject): RecolorMode = try {
    RecolorMode.valueOf(metadata.optString("recolorMode", "raster").uppercase(Locale.ROOT))
  } catch (_: IllegalArgumentException) {
    RecolorMode.RASTER
  }

  private fun lookupManifestEntryNonBlocking(name: String): ManifestEntry? {
    if (!manifestAccessLock.tryLock()) return null
    return try {
      manifestFallbackMap?.get(name)?.let { return it }
      val metadata = manifest?.optJSONObject(name) ?: return null
      val digest = metadata.optString("digest").takeIf { it.isNotEmpty() } ?: return null
      ManifestEntry(digest = digest, mode = modeOf(metadata))
    } finally {
      manifestAccessLock.unlock()
    }
  }

  private fun lookupManifestEntryBlocking(assets: AssetManager?, name: String): ManifestEntry? {
    synchronized(manifestLock) {
      manifestFallbackMap?.get(name)?.let { return it }
      val metadata = loadManifestLocked(assets)?.optJSONObject(name) ?: return null
      val digest = metadata.optString("digest").takeIf { it.isNotEmpty() } ?: return null
      return ManifestEntry(digest = digest, mode = modeOf(metadata))
    }
  }

  private data class RenderKey(val identity: String, val key: String, val maskAlpha: Int)

  private fun renderKey(
    name: String,
    digest: String,
    width: Int,
    height: Int,
    scale: Float,
    mode: RecolorMode,
    requestColor: String,
    color: Int,
  ): RenderKey {
    val identity = "$name|$digest|$width|$height|$scale"
    val maskAlpha = if (mode == RecolorMode.MULTIPLE) ((color ushr 24) and 0xff) else 255
    val colorKey = when (mode) {
      RecolorMode.RASTER -> requestColor
      RecolorMode.MULTIPLE -> maskAlpha.toString()
      else -> ""
    }
    return RenderKey(identity, "$identity|$mode|$colorKey", maskAlpha)
  }

  /**
   * Main-thread fast path: returns an already-decoded result without asset I/O or
   * manifest loading, so remounted and recycled views paint in the same frame.
   */
  fun cachedRender(name: String, color: String, width: Int, height: Int, scale: Float): RenderResult? =
    cachedRenderInternal(name, color, width, height, scale, recordStartup = true)

  private fun cachedRenderInternal(
    name: String,
    color: String,
    width: Int,
    height: Int,
    scale: Float,
    recordStartup: Boolean,
  ): RenderResult? {
    if (recordStartup) {
      noteStartupRequest(name, color, width, height, scale)
    }
    val value = parseColor(color) ?: return null
    val entry = lookupManifestEntryNonBlocking(name) ?: return null
    val key = renderKey(name, entry.digest, width, height, scale, entry.mode, color, value).key
    return synchronized(lock) { cache[key] }
  }

  /**
   * Synchronous fallback for memory cache misses: loads an already-rasterized icon
   * from the disk PNG cache when within the main-thread decode limits and frame budget.
   */
  fun diskRender(
    assets: AssetManager?,
    name: String,
    color: String,
    width: Int,
    height: Int,
    scale: Float,
  ): RenderResult? {
    if (
      width !in 1..OneKeyMainThreadDecode.MAX_ICON_PIXEL_SIZE ||
      height !in 1..OneKeyMainThreadDecode.MAX_ICON_PIXEL_SIZE ||
      !scale.isFinite() ||
      scale <= 0f ||
      !isValidColor(color)
    ) {
      return null
    }
    return OneKeyMainThreadDecode.run {
      if (!manifestAccessLock.tryLock()) {
        OneKeyMainThreadDecode.noteLockContention()
        return@run null
      }
      val hasManifest = try {
        manifest != null || manifestFallbackMap != null
      } finally {
        manifestAccessLock.unlock()
      }
      if (!hasManifest) return@run null
      decode(Request(assets, name, color, width, height, scale, false) {}, diskOnly = true)
    }
  }

  fun iconMetadata(assets: AssetManager, name: String): IconMetadata? {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return null
    synchronized(manifestLock) {
      manifestFallbackMap?.get(trimmed)?.let { entry ->
        return IconMetadata(width = entry.width, height = entry.height, mode = entry.mode)
      }
    }
    val metadata = try {
      synchronized(manifestLock) {
        loadManifestLocked(assets)?.optJSONObject(trimmed)
      }
    } catch (_: Exception) {
      null
    } ?: return null
    val mode = modeOf(metadata)
    val viewBox = metadata.optJSONArray("viewBox")
    val vbWidth = if (viewBox != null && viewBox.length() >= 4) viewBox.optDouble(2, 24.0).toFloat() else 24f
    val vbHeight = if (viewBox != null && viewBox.length() >= 4) viewBox.optDouble(3, 24.0).toFloat() else 24f
    val width = if (vbWidth > 0f) vbWidth else 24f
    val height = if (vbHeight > 0f) vbHeight else 24f
    return IconMetadata(width = width, height = height, mode = mode)
  }

  fun request(
    owner: Any,
    assets: AssetManager,
    name: String,
    color: String,
    width: Int,
    height: Int,
    scale: Float,
    complete: (Bitmap?) -> Unit,
  ) {
    noteStartupRequest(name, color, width, height, scale)
    if (OneKeyMainThreadDecode.isOnMainThread()) {
      val hit = cachedRenderInternal(name, color, width, height, scale, recordStartup = false)
        ?: diskRender(assets, name, color, width, height, scale)
      if (hit != null) {
        cancel(owner)
        complete(hit.finalBitmap(color))
        return
      }
    }
    enqueue(owner, assets, name, color, width, height, scale, true) { complete(it?.bitmap) }
  }

  fun requestRender(
    owner: Any,
    assets: AssetManager,
    name: String,
    color: String,
    width: Int,
    height: Int,
    scale: Float,
    complete: (RenderResult?) -> Unit,
  ) {
    noteStartupRequest(name, color, width, height, scale)
    if (OneKeyMainThreadDecode.isOnMainThread()) {
      val hit = cachedRenderInternal(name, color, width, height, scale, recordStartup = false)
        ?: diskRender(assets, name, color, width, height, scale)
      if (hit != null) {
        cancel(owner)
        complete(hit)
        return
      }
    }
    enqueue(owner, assets, name, color, width, height, scale, false, complete)
  }

  private fun enqueue(
    owner: Any,
    assets: AssetManager,
    name: String,
    color: String,
    width: Int,
    height: Int,
    scale: Float,
    finalColor: Boolean,
    complete: (RenderResult?) -> Unit,
  ) {
    synchronized(lock) {
      pending[owner] = Request(assets, name, color, width, height, scale, finalColor, complete)
      while (activeWorkers < 2 && activeWorkers < pending.size) {
        activeWorkers++
        workers.execute { drain() }
      }
    }
  }

  private fun parseColor(color: String): Int? {
    if (!colorPattern.matches(color)) return null
    val channels = color.removePrefix("rgba(").removeSuffix(")").split(',')
    val rgb = channels.take(3).map { it.toInt() }
    if (rgb.any { it !in 0..255 }) return null
    val alpha = (channels[3].toDouble() * 255).roundToInt().coerceIn(0, 255)
    return (alpha shl 24) or ((rgb[0] and 0xff) shl 16) or ((rgb[1] and 0xff) shl 8) or (rgb[2] and 0xff)
  }

  fun isValidColor(color: String): Boolean = parseColor(color) != null

  private fun drain() {
    while (true) {
      val request = synchronized(lock) {
        val first = pending.entries.firstOrNull()
        if (first == null) {
          activeWorkers--
          null
        } else {
          pending.remove(first.key)
          first.value
        }
      } ?: return
      val bitmap = try {
        decode(request)?.let { result ->
          if (request.finalColor) {
            result.copy(bitmap = result.finalBitmap(request.color), mode = RecolorMode.RASTER)
          } else {
            result
          }
        }
      } catch (_: Throwable) {
        null
      } // OOM must not strand activeWorkers.
      val handler = main
      if (handler != null) {
        handler.post { request.complete(bitmap) }
      } else {
        request.complete(bitmap)
      }
    }
  }

  private fun decode(request: Request, diskOnly: Boolean = false): RenderResult? {
    if (
      request.width !in 1..4096 ||
      request.height !in 1..4096 ||
      request.width.toLong() * request.height > 4_194_304L ||
      !request.scale.isFinite() ||
      request.scale <= 0f
    ) {
      return null
    }
    val color = parseColor(request.color) ?: return null
    val manifestEntry = if (diskOnly) {
      lookupManifestEntryNonBlocking(request.name)
    } else {
      lookupManifestEntryBlocking(request.assets, request.name)
    } ?: return null
    val mode = manifestEntry.mode
    val (sourceIdentity, key, maskAlpha) = renderKey(
      request.name,
      manifestEntry.digest,
      request.width,
      request.height,
      request.scale,
      mode,
      request.color,
      color,
    )
    synchronized(lock) { cache[key]?.let { return it } }

    val diskFile = diskRasterFile(key)
    if (diskFile != null) {
      val fileExists = diskFile.isFile
      if (diskOnly && !fileExists) {
        OneKeyMainThreadDecode.noteDiskMiss()
        return null
      }
      if (fileExists) {
        val length = diskFile.length()
        if (diskOnly && (length <= 0L || length > OneKeyMainThreadDecode.MAX_ENCODED_BYTES)) {
          OneKeyMainThreadDecode.noteLimitRejection()
          return null
        }
        val decodedBitmap = decodeDiskPng(diskFile, request.width, request.height)
        if (decodedBitmap != null) {
          val result = RenderResult(decodedBitmap, mode, sourceIdentity, maskAlpha)
          insertMemoryCache(key, result)
          return result
        } else if (diskOnly) {
          OneKeyMainThreadDecode.noteDecodeFailure()
          return null
        }
      }
    }
    if (diskOnly) return null

    val assets = request.assets ?: return null
    val paintColor = when (mode) {
      RecolorMode.FIXED, RecolorMode.SINGLE -> Color.BLACK
      RecolorMode.MULTIPLE -> Color.argb(maskAlpha, 0, 0, 0)
      RecolorMode.RASTER -> color
    }
    // AndroidSVG rgba quantizes with 256; SVG hex alpha preserves the exact CSS 8-bit channel.
    val svgColor = listOf(
      Color.red(paintColor),
      Color.green(paintColor),
      Color.blue(paintColor),
      Color.alpha(paintColor),
    ).joinToString(separator = "", prefix = "#") { it.toString(16).padStart(2, '0') }
    val xml = assets.open("icons/${request.name}.svg").bufferedReader().use { it.readText() }
      .replace(Regex("((?:fill|stroke)\\s*=\\s*[\"'])currentColor([\"'])"), "$1${svgColor}$2")
    val bitmap = OneKeySVG.decode(xml.byteInputStream()).render(request.width, request.height)
    val result = RenderResult(bitmap, mode, sourceIdentity, maskAlpha)
    insertMemoryCache(key, result)
    if (diskFile != null) {
      writeDiskRasterAsync(diskFile, bitmap)
    }
    return result
  }

  private fun insertMemoryCache(key: String, result: RenderResult) {
    val bytes = estimateBitmapBytes(result.bitmap)
    if (bytes <= 8_388_608L) {
      synchronized(lock) {
        cache.put(key, result)?.let { cacheBytes -= estimateBitmapBytes(it.bitmap) }
        cacheBytes += bytes
        while (cache.size > 128 || cacheBytes > 8_388_608L) {
          val first = cache.entries.first()
          cacheBytes -= estimateBitmapBytes(first.value.bitmap)
          cache.remove(first.key)
        }
      }
    }
  }

  private fun estimateBitmapBytes(bitmap: Bitmap): Long {
    bitmapByteCountOverrideForTesting?.let { return it(bitmap) }
    return try {
      bitmap.allocationByteCount.toLong()
    } catch (_: Throwable) {
      4096L
    }
  }

  private fun diskRasterDir(): File? {
    val root = startupFileOverride?.parentFile ?: baseCacheDir ?: return null
    return File(root, DISK_RASTER_DIR_NAME)
  }

  internal fun diskRasterFile(key: String): File? {
    val dir = diskRasterDir() ?: return null
    return File(dir, "${sha256Hex(key.toByteArray(Charsets.UTF_8))}.png")
  }

  private fun decodeDiskPng(file: File, expectedWidth: Int, expectedHeight: Int): Bitmap? {
    diskPngDecoderOverrideForTesting?.let { return it(file, expectedWidth, expectedHeight) }
    return try {
      val bitmap = BitmapFactory.decodeFile(
        file.absolutePath,
        BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 },
      ) ?: return null
      if (bitmap.width == expectedWidth && bitmap.height == expectedHeight) bitmap else null
    } catch (_: Throwable) {
      null
    }
  }

  private fun writeDiskRasterAsync(file: File, bitmap: Bitmap) {
    diskExecutor.execute {
      try {
        val dir = file.parentFile ?: return@execute
        if (!dir.exists()) dir.mkdirs()
        val tmp = File(dir, "${file.name}.tmp")
        FileOutputStream(tmp).use { out ->
          bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        if (!tmp.renameTo(file)) {
          tmp.copyTo(file, overwrite = true)
          tmp.delete()
        }
        pruneDiskRasterDir(dir)
      } catch (_: Throwable) {
      }
    }
  }

  private fun pruneDiskRasterDir(dir: File) {
    val files = dir.listFiles()?.filter { it.isFile && it.name.endsWith(".png") } ?: return
    var total = files.sumOf { it.length() }
    if (total <= MAX_DISK_RASTER_BYTES) return
    val sorted = files.sortedBy { it.lastModified() }
    for (f in sorted) {
      val len = f.length()
      if (f.delete()) {
        total -= len
        if (total <= MAX_DISK_RASTER_BYTES) break
      }
    }
  }

  private fun noteStartupRequest(name: String, color: String, width: Int, height: Int, scale: Float) {
    if (
      name.isEmpty() ||
      width !in 1..4096 ||
      height !in 1..4096 ||
      !scale.isFinite() ||
      scale <= 0f ||
      !isValidColor(color)
    ) {
      return
    }
    val entry = StartupEntry(
      name = name,
      color = color,
      width = width,
      height = height,
      scale = scale,
      launch = 0,
    )
    val now = System.currentTimeMillis()
    synchronized(startupLock) {
      if (startupRecordingClosed) return
      val start = startupRecordStartTimeMs ?: now.also { startupRecordStartTimeMs = it }
      if (now - start >= STARTUP_MAX_DURATION_MS && startupRecorded.isNotEmpty()) {
        startupRecordingClosed = true
        scheduleStartupPersistLocked(delayMs = 0L)
        return
      }
      if (startupRecorded.size < STARTUP_MAX_ENTRIES && startupRecordedKeys.add(entry.identity)) {
        startupRecorded.add(entry)
      }
      scheduleStartupPersistLocked(delayMs = STARTUP_RECORD_DURATION_MS)
    }
  }

  private fun scheduleStartupPersistLocked(delayMs: Long) {
    startupDebounceFuture?.cancel(false)
    startupFlushScheduled = true
    startupDebounceFuture = diskExecutor.schedule({
      startupDebounceFired()
    }, delayMs, TimeUnit.MILLISECONDS)
  }

  private fun startupDebounceFired() {
    val entries: List<StartupEntry>
    val file: File?
    val now = System.currentTimeMillis()
    synchronized(startupLock) {
      if (startupRecordingClosed && !startupFlushScheduled) return
      val start = startupRecordStartTimeMs ?: now
      if (
        !startupRecordingClosed &&
        startupRecorded.size < STARTUP_MIN_SETTLED_COUNT &&
        now - start < STARTUP_MAX_DURATION_MS
      ) {
        scheduleStartupPersistLocked(delayMs = STARTUP_RECORD_DURATION_MS)
        return
      }
      startupRecordingClosed = true
      startupFlushScheduled = false
      startupDebounceFuture = null
      entries = ArrayList(startupRecorded)
      file = startupFile()
    }
    if (file != null) {
      persistStartupEntries(entries, file)
    }
  }

  private fun startupFile(): File? {
    startupFileOverride?.let { return it }
    val dir = baseCacheDir ?: return null
    return File(dir, STARTUP_FILE_NAME)
  }

  private fun persistStartupEntries(entries: List<StartupEntry>, file: File) {
    if (entries.isEmpty()) return
    val existing = readStartupEntries(file)
    val maxExistingLaunch = existing.maxOfOrNull { it.launch } ?: 0
    val currentLaunch = maxExistingLaunch + 1

    val stampedCurrent = entries.map {
      StartupEntry(
        name = it.name,
        color = it.color,
        width = it.width,
        height = it.height,
        scale = it.scale,
        launch = currentLaunch,
      )
    }
    val seen = stampedCurrent.mapTo(HashSet()) { it.identity }
    val merged = ArrayList<StartupEntry>(STARTUP_MAX_ENTRIES)
    merged.addAll(stampedCurrent.take(STARTUP_MAX_ENTRIES))

    for (entry in existing) {
      if (merged.size >= STARTUP_MAX_ENTRIES) break
      if (
        entry.name.isEmpty() ||
        entry.width !in 1..4096 ||
        entry.height !in 1..4096 ||
        !entry.scale.isFinite() ||
        entry.scale <= 0f ||
        !isValidColor(entry.color)
      ) {
        continue
      }
      val age = currentLaunch - entry.launch
      if (age >= STARTUP_MAX_LAUNCH_AGE) continue
      if (seen.add(entry.identity)) {
        merged.add(entry)
      }
    }

    if (
      existing.size == merged.size &&
      existing.indices.all { i ->
        existing[i].identity == merged[i].identity &&
          (currentLaunch - existing[i].launch) < 1
      }
    ) {
      return
    }

    try {
      file.parentFile?.mkdirs()
      val json = encodeStartupEntriesJson(merged)
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
    diskExecutor.execute {
      prewarmStartupSet(appContext.assets, appContext.cacheDir)
    }
  }

  fun prewarmStartupSet(assets: AssetManager? = null, cacheDir: File? = baseCacheDir) {
    val startNs = System.nanoTime()
    if (cacheDir != null) setCacheDirIfNeeded(cacheDir)
    val manifestStartNs = System.nanoTime()
    if (assets != null) {
      synchronized(manifestLock) {
        loadManifestLocked(assets)
      }
    }
    val manifestMs = (System.nanoTime() - manifestStartNs) / 1_000_000.0
    val file = synchronized(startupLock) { startupFile() } ?: return
    val entries = readStartupEntries(file).take(STARTUP_MAX_ENTRIES)
    if (entries.isEmpty()) {
      val totalMs = (System.nanoTime() - startNs) / 1_000_000.0
      synchronized(startupLock) {
        lastPrewarmMetrics = StartupPrewarmMetrics(0, 0, manifestMs, totalMs)
      }
      return
    }

    var decodedCount = 0
    for (entry in entries) {
      val result = decode(
        Request(
          assets = assets,
          name = entry.name,
          color = entry.color,
          width = entry.width,
          height = entry.height,
          scale = entry.scale,
          finalColor = false,
          complete = {},
        ),
        diskOnly = assets == null,
      )
      if (result != null) decodedCount += 1
    }

    val totalMs = (System.nanoTime() - startNs) / 1_000_000.0
    synchronized(startupLock) {
      lastPrewarmMetrics = StartupPrewarmMetrics(
        entryCount = entries.size,
        decodedCount = decodedCount,
        manifestMs = manifestMs,
        totalMs = totalMs,
      )
    }
    try {
      Log.i(
        "OneKeyImage",
        String.format(
          Locale.ROOT,
          "[OneKeyImageStartupMetrics] iconPrewarm entries=%d decoded=%d manifestMs=%.3f totalMs=%.3f",
          entries.size,
          decodedCount,
          manifestMs,
          totalMs,
        ),
      )
    } catch (_: Throwable) {
    }
  }

  internal fun encodeStartupEntriesJson(entries: List<StartupEntry>): String {
    val array = JSONArray()
    for (entry in entries) {
      array.put(
        JSONObject()
          .put("name", entry.name)
          .put("color", entry.color)
          .put("width", entry.width)
          .put("height", entry.height)
          .put("scale", entry.scale.toDouble())
          .put("launch", entry.launch),
      )
    }
    return array.toString()
  }

  internal fun decodeStartupEntriesJson(text: String): List<StartupEntry> {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return emptyList()
    val array = JSONArray(trimmed)
    val result = ArrayList<StartupEntry>(array.length())
    for (i in 0 until array.length()) {
      val obj = array.optJSONObject(i) ?: continue
      val name = obj.optString("name", "").takeIf { it.isNotEmpty() } ?: continue
      val color = obj.optString("color", "").takeIf { it.isNotEmpty() } ?: continue
      val width = obj.optInt("width", 0)
      val height = obj.optInt("height", 0)
      val scale = obj.optDouble("scale", Double.NaN).toFloat()
      if (width <= 0 || height <= 0 || !scale.isFinite() || scale <= 0f) continue
      val launch = obj.optInt("launch", 0)
      result.add(
        StartupEntry(
          name = name,
          color = color,
          width = width,
          height = height,
          scale = scale,
          launch = launch,
        ),
      )
    }
    return result
  }

  internal fun readStartupEntries(file: File): List<StartupEntry> {
    if (!file.isFile) return emptyList()
    return try {
      decodeStartupEntriesJson(file.readText(Charsets.UTF_8))
    } catch (_: Exception) {
      emptyList()
    }
  }

  private fun sha256Hex(bytes: ByteArray): String =
    MessageDigest.getInstance("SHA-256")
      .digest(bytes)
      .joinToString("") { "%02x".format(Locale.ROOT, it.toInt() and 0xff) }

  internal fun clearMemoryCacheForTesting() {
    synchronized(lock) {
      cache.clear()
      cacheBytes = 0L
    }
  }

  internal fun setManifestFallbackForTesting(entries: Map<String, ManifestEntry>?) {
    manifestAccessLock.lock()
    try {
      manifestFallbackMap = entries
    } finally {
      manifestAccessLock.unlock()
    }
  }

  internal fun tryAcquireManifestAccessLockForTesting(): Boolean = manifestAccessLock.tryLock()

  internal fun unlockManifestAccessLockForTesting() {
    manifestAccessLock.unlock()
  }

  internal fun resetStartupForTesting(file: File? = null, cacheDir: File? = null) {
    synchronized(startupLock) {
      startupDebounceFuture?.cancel(false)
      startupDebounceFuture = null
      startupRecorded.clear()
      startupRecordedKeys.clear()
      startupRecordStartTimeMs = null
      startupRecordingClosed = false
      startupFlushScheduled = false
      startupFileOverride = file
      baseCacheDir = cacheDir ?: file?.parentFile
      lastPrewarmMetrics = StartupPrewarmMetrics()
    }
  }

  internal fun flushStartupForTesting(forceClose: Boolean = true) {
    val entries: List<StartupEntry>
    val file: File?
    synchronized(startupLock) {
      startupDebounceFuture?.cancel(false)
      startupDebounceFuture = null
      if (!forceClose && startupRecorded.size < STARTUP_MIN_SETTLED_COUNT) {
        return
      }
      startupRecordingClosed = true
      startupFlushScheduled = false
      entries = ArrayList(startupRecorded)
      file = startupFile()
    }
    if (file != null) {
      persistStartupEntries(entries, file)
    }
  }
}
