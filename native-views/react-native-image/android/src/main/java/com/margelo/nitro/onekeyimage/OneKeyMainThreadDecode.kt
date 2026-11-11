package com.margelo.nitro.onekeyimage

import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.os.Trace
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.File
import java.util.Base64
import java.util.Locale

internal object OneKeyMainThreadDecode {
  const val MAX_ENCODED_BYTES = 32 * 1024
  const val MAX_SOURCE_PIXEL_SIZE = 512
  const val MAX_THUMBNAIL_PIXEL_SIZE = 256
  const val MAX_ICON_PIXEL_SIZE = 192
  const val FRAME_BUDGET_NS = 4_000_000L
  const val FRAME_WINDOW_NS = 16_666_667L
  private const val METRICS_REPORT_DELAY_MS = 3_500L

  data class Metrics(
    var attempts: Int = 0,
    var hits: Int = 0,
    var misses: Int = 0,
    var budgetRejections: Int = 0,
    var lockContentionMisses: Int = 0,
    var diskMisses: Int = 0,
    var limitRejections: Int = 0,
    var decodeFailures: Int = 0,
    var windows: Int = 0,
    var maxWindowAllowed: Int = 0,
    var maxWindowRejected: Int = 0,
    var totalDurationMs: Double = 0.0,
    var p50DurationMs: Double = 0.0,
    var p90DurationMs: Double = 0.0,
    var maxDurationMs: Double = 0.0,
  ) {
    val hitRate: Double
      get() = if (attempts > 0) hits.toDouble() / attempts.toDouble() else 0.0
  }

  private val metricsLock = Any()
  private var windowStartNs = 0L
  private var spentNs = 0L
  private var currentWindowAllowed = 0
  private var currentWindowRejected = 0
  private var frameBudgetNs = FRAME_BUDGET_NS
  private var frameWindowNs = FRAME_WINDOW_NS
  private val durationsNs = ArrayList<Long>(64)
  private var metrics = Metrics()
  private var metricsReportScheduled = false

  @Volatile
  var isEnabled: Boolean =
    System.getProperty("OneKeyImageSyncDecodeDisabled")?.equals("true", ignoreCase = true) != true

  @Volatile
  internal var mainThreadCheckOverrideForTesting: (() -> Boolean)? = null

  internal fun isOnMainThread(): Boolean {
    mainThreadCheckOverrideForTesting?.let { return it() }
    return try {
      val mainLooper = Looper.getMainLooper()
      mainLooper != null && Looper.myLooper() === mainLooper
    } catch (_: Throwable) {
      false
    }
  }

  fun metricsSnapshot(): Metrics = synchronized(metricsLock) {
    val snapshot = metrics.copy()
    if (durationsNs.isNotEmpty()) {
      val sorted = durationsNs.sorted()
      val totalNs = sorted.sum()
      snapshot.totalDurationMs = totalNs / 1_000_000.0
      snapshot.p50DurationMs = percentile(sorted, 0.50) / 1_000_000.0
      snapshot.p90DurationMs = percentile(sorted, 0.90) / 1_000_000.0
      snapshot.maxDurationMs = (sorted.lastOrNull() ?: 0L) / 1_000_000.0
    }
    snapshot
  }

  fun resetForTesting() {
    synchronized(metricsLock) {
      windowStartNs = 0L
      spentNs = 0L
      currentWindowAllowed = 0
      currentWindowRejected = 0
      frameBudgetNs = FRAME_BUDGET_NS
      frameWindowNs = FRAME_WINDOW_NS
      durationsNs.clear()
      metrics = Metrics()
      metricsReportScheduled = false
    }
  }

  fun setBudgetForTesting(budgetNs: Long, windowNs: Long = FRAME_WINDOW_NS) {
    synchronized(metricsLock) {
      frameBudgetNs = maxOf(0L, budgetNs)
      frameWindowNs = maxOf(1L, windowNs)
      windowStartNs = 0L
      spentNs = 0L
      currentWindowAllowed = 0
      currentWindowRejected = 0
    }
  }

  internal fun noteLockContention() {
    synchronized(metricsLock) { metrics.lockContentionMisses += 1 }
  }

  internal fun noteDiskMiss() {
    synchronized(metricsLock) { metrics.diskMisses += 1 }
  }

  internal fun noteLimitRejection() {
    synchronized(metricsLock) { metrics.limitRejections += 1 }
  }

  internal fun noteDecodeFailure() {
    synchronized(metricsLock) { metrics.decodeFailures += 1 }
  }

  fun <T> run(work: () -> T?): T? {
    if (!isEnabled || !isOnMainThread()) return null
    val start = System.nanoTime()
    val allowed = synchronized(metricsLock) {
      if (start - windowStartNs >= frameWindowNs) {
        windowStartNs = start
        spentNs = 0L
        currentWindowAllowed = 0
        currentWindowRejected = 0
        metrics.windows += 1
      }
      if (spentNs >= frameBudgetNs) {
        metrics.budgetRejections += 1
        currentWindowRejected += 1
        if (currentWindowRejected > metrics.maxWindowRejected) {
          metrics.maxWindowRejected = currentWindowRejected
        }
        false
      } else {
        metrics.attempts += 1
        currentWindowAllowed += 1
        if (currentWindowAllowed > metrics.maxWindowAllowed) {
          metrics.maxWindowAllowed = currentWindowAllowed
        }
        true
      }
    }
    scheduleMetricsReportIfNeeded()
    if (!allowed) return null

    try {
      Trace.beginSection("OneKeyMainThreadDecode")
    } catch (_: Throwable) {
    }
    val result = try {
      work()
    } finally {
      try {
        Trace.endSection()
      } catch (_: Throwable) {
      }
    }
    val elapsed = (System.nanoTime() - start).coerceAtLeast(0L)
    synchronized(metricsLock) {
      spentNs += elapsed
      if (durationsNs.size < 512) {
        durationsNs.add(elapsed)
      }
      if (result != null) {
        metrics.hits += 1
      } else {
        metrics.misses += 1
      }
    }
    return result
  }

  private fun scheduleMetricsReportIfNeeded() {
    val shouldSchedule = synchronized(metricsLock) {
      if (metricsReportScheduled) false
      else {
        metricsReportScheduled = true
        true
      }
    }
    if (!shouldSchedule) return
    try {
      val mainLooper = Looper.getMainLooper() ?: return
      Handler(mainLooper).postDelayed({
        val s = metricsSnapshot()
        try {
          Log.i(
            "OneKeyImage",
            String.format(
              Locale.ROOT,
              "[OneKeyImageStartupMetrics] syncDecode attempts=%d hits=%d misses=%d hitRate=%.2f budgetRejected=%d lockContended=%d diskMiss=%d limitRejected=%d decodeFailed=%d windows=%d maxWindowAllowed=%d maxWindowRejected=%d totalMs=%.3f p50Ms=%.3f p90Ms=%.3f maxMs=%.3f",
              s.attempts,
              s.hits,
              s.misses,
              s.hitRate,
              s.budgetRejections,
              s.lockContentionMisses,
              s.diskMisses,
              s.limitRejections,
              s.decodeFailures,
              s.windows,
              s.maxWindowAllowed,
              s.maxWindowRejected,
              s.totalDurationMs,
              s.p50DurationMs,
              s.p90DurationMs,
              s.maxDurationMs,
            ),
          )
        } catch (_: Throwable) {
        }
      }, METRICS_REPORT_DELAY_MS)
    } catch (_: Throwable) {
    }
  }

  private fun percentile(sorted: List<Long>, p: Double): Long {
    if (sorted.isEmpty()) return 0L
    val index = minOf(sorted.size - 1, maxOf(0, ((sorted.size - 1) * p).toInt()))
    return sorted[index]
  }

  /**
   * Decodes a small local or disk-cached static image into the shared startup memory
   * cache on the UI thread when all size and safety bounds are satisfied.
   */
  fun promoteToMemory(
    cacheDir: File?,
    url: String,
    rawUrl: String,
    width: Int,
    height: Int,
    round: Boolean,
  ): Boolean {
    if (width <= 0 || height <= 0 || maxOf(width, height) > MAX_THUMBNAIL_PIXEL_SIZE) {
      noteLimitRejection()
      return false
    }
    val keyHash = OneKeyImageStartupPrewarm.keyHash(url, width, height, round)
    if (OneKeyImageStartupPrewarm.cachedBitmap(keyHash) != null) {
      return true
    }
    val bytes = encodedBytes(cacheDir, url) ?: return false
    val bounds = inspectSmallStaticImage(bytes) ?: return false
    val bitmap = OneKeyImageStartupPrewarm.decodeStaticBitmap(
      bytes = bytes,
      sourceWidth = bounds.first,
      sourceHeight = bounds.second,
      targetWidth = width,
      targetHeight = height,
      round = round,
    )
    if (bitmap == null) {
      noteDecodeFailure()
      return false
    }
    OneKeyImageStartupPrewarm.putMemoryBitmap(keyHash, bitmap)
    if (cacheDir != null) {
      OneKeyImageStartupPrewarm.ensureFallbackDiskBytesAsync(cacheDir, url, bytes)
    }
    return true
  }

  internal fun encodedBytes(cacheDir: File?, url: String): ByteArray? {
    if (url.startsWith("file://")) {
      val rawPath = url.removePrefix("file://").substringBefore('?').substringBefore('#')
      if (rawPath.isEmpty() || rawPath.startsWith("/android_asset/")) {
        noteDiskMiss()
        return null
      }
      val file = File(rawPath)
      if (!file.isFile) {
        noteDiskMiss()
        return null
      }
      val length = file.length()
      if (length <= 0L) {
        noteDiskMiss()
        return null
      }
      if (length > MAX_ENCODED_BYTES) {
        noteLimitRejection()
        return null
      }
      val bytes = try {
        file.readBytes()
      } catch (_: Exception) {
        noteDiskMiss()
        return null
      }
      if (bytes.isEmpty() || bytes.size > MAX_ENCODED_BYTES) {
        noteLimitRejection()
        return null
      }
      return bytes
    }

    if (url.startsWith("data:")) {
      if (url.length > 65_536) {
        noteLimitRejection()
        return null
      }
      val comma = url.indexOf(',')
      if (comma < 0 || !url.substring(0, comma).lowercase(Locale.ROOT).contains(";base64")) {
        noteDecodeFailure()
        return null
      }
      val payloadStart = comma + 1
      val estimated = try {
        OneKeyImageSafety.dataUriDecodedByteCount(url, payloadStart)
      } catch (_: Exception) {
        noteLimitRejection()
        return null
      }
      if (estimated <= 0L || estimated > MAX_ENCODED_BYTES) {
        noteLimitRejection()
        return null
      }
      val decoded = try {
        Base64.getMimeDecoder().decode(url.substring(payloadStart))
      } catch (_: Exception) {
        noteDecodeFailure()
        return null
      }
      if (decoded.isEmpty() || decoded.size > MAX_ENCODED_BYTES) {
        noteLimitRejection()
        return null
      }
      return decoded
    }

    if (
      url.startsWith("http://") ||
      url.startsWith("https://") ||
      OneKeyBlockieAvatar.isAvatarUri(url)
    ) {
      if (cacheDir == null) {
        noteDiskMiss()
        return null
      }
      val safeKey = OneKeyImageStartupPrewarm.glideDataSafeKey(url)
      if (safeKey == null) {
        noteDiskMiss()
        return null
      }
      val diskFile = OneKeyImageStartupPrewarm.findDiskCacheFile(cacheDir, safeKey)
      if (diskFile == null) {
        noteDiskMiss()
        return null
      }
      val length = diskFile.length()
      if (length <= 0L) {
        noteDiskMiss()
        return null
      }
      if (length > MAX_ENCODED_BYTES) {
        noteLimitRejection()
        return null
      }
      val bytes = try {
        diskFile.readBytes()
      } catch (_: Exception) {
        noteDiskMiss()
        return null
      }
      if (bytes.isEmpty() || bytes.size > MAX_ENCODED_BYTES) {
        noteLimitRejection()
        return null
      }
      if (OneKeyBlockieAvatar.isAvatarUri(url) && !OneKeyBlockieAvatar.isValidPng(bytes)) {
        noteDecodeFailure()
        return null
      }
      return bytes
    }

    noteDiskMiss()
    return null
  }

  internal fun inspectSmallStaticImage(bytes: ByteArray): Pair<Int, Int>? {
    val metadata = try {
      OneKeyEncodedImageInspector.inspect(ByteArrayInputStream(bytes))
    } catch (_: Exception) {
      noteDecodeFailure()
      return null
    }
    if (metadata.animated) {
      noteLimitRejection()
      return null
    }
    val bounds = if (metadata.width > 0 && metadata.height > 0) {
      metadata.width to metadata.height
    } else {
      extractStaticImageBounds(bytes)
    }
    if (bounds == null) {
      noteDecodeFailure()
      return null
    }
    if (bounds.first <= 0 || bounds.second <= 0) {
      noteDecodeFailure()
      return null
    }
    try {
      OneKeyImageSafety.requireStaticDimensions(bounds.first, bounds.second)
    } catch (_: Exception) {
      noteLimitRejection()
      return null
    }
    if (maxOf(bounds.first, bounds.second) > MAX_SOURCE_PIXEL_SIZE) {
      noteLimitRejection()
      return null
    }
    return bounds
  }

  internal fun extractStaticImageBounds(bytes: ByteArray): Pair<Int, Int>? {
    // 1. PNG IHDR
    if (
      bytes.size >= 24 &&
      bytes[0] == 0x89.toByte() &&
      bytes[1] == 0x50.toByte() &&
      bytes[2] == 0x4e.toByte() &&
      bytes[3] == 0x47.toByte()
    ) {
      val width = u32Be(bytes, 16)
      val height = u32Be(bytes, 20)
      if (width > 0 && height > 0) return width to height
    }
    // 2. WebP (VP8 / VP8L / VP8X)
    if (
      bytes.size >= 25 &&
      bytes[0] == 'R'.code.toByte() &&
      bytes[1] == 'I'.code.toByte() &&
      bytes[2] == 'F'.code.toByte() &&
      bytes[3] == 'F'.code.toByte() &&
      bytes[8] == 'W'.code.toByte() &&
      bytes[9] == 'E'.code.toByte() &&
      bytes[10] == 'B'.code.toByte() &&
      bytes[11] == 'P'.code.toByte()
    ) {
      val chunk = String(bytes, 12, 4, Charsets.US_ASCII)
      when (chunk) {
        "VP8X" -> if (bytes.size >= 30) {
          val w = u24Le(bytes, 24) + 1
          val h = u24Le(bytes, 27) + 1
          return w to h
        }
        "VP8 " -> if (
          bytes.size >= 30 &&
          (bytes[23].toInt() and 0xff) == 0x9d &&
          (bytes[24].toInt() and 0xff) == 0x01 &&
          (bytes[25].toInt() and 0xff) == 0x2a
        ) {
          val w = u16Le(bytes, 26) and 0x3fff
          val h = u16Le(bytes, 28) and 0x3fff
          if (w > 0 && h > 0) return w to h
        }
        "VP8L" -> if (bytes.size >= 25 && (bytes[20].toInt() and 0xff) == 0x2f) {
          val b0 = bytes[21].toInt() and 0xff
          val b1 = bytes[22].toInt() and 0xff
          val b2 = bytes[23].toInt() and 0xff
          val b3 = bytes[24].toInt() and 0xff
          val bits = b0 or (b1 shl 8) or (b2 shl 16) or (b3 shl 24)
          val w = (bits and 0x3fff) + 1
          val h = ((bits ushr 14) and 0x3fff) + 1
          return w to h
        }
      }
    }
    // 3. JPEG SOF markers
    if (bytes.size >= 4 && (bytes[0].toInt() and 0xff) == 0xff && (bytes[1].toInt() and 0xff) == 0xd8) {
      var pos = 2
      while (pos + 4 <= bytes.size) {
        if ((bytes[pos].toInt() and 0xff) != 0xff) {
          pos += 1
          continue
        }
        var marker = bytes[pos + 1].toInt() and 0xff
        pos += 2
        while (marker == 0xff && pos < bytes.size) {
          marker = bytes[pos].toInt() and 0xff
          pos += 1
        }
        if (marker == 0xd8 || marker == 0xd9 || marker in 0xd0..0xd7 || marker == 0x01) {
          continue
        }
        if (pos + 2 > bytes.size) break
        val segmentLength = u16Be(bytes, pos)
        if (segmentLength < 2 || pos + segmentLength > bytes.size) break
        if (marker in 0xc0..0xc3 || marker in 0xc5..0xc7 || marker in 0xc9..0xcb || marker in 0xcd..0xcf) {
          if (segmentLength >= 7) {
            val h = u16Be(bytes, pos + 3)
            val w = u16Be(bytes, pos + 5)
            if (w > 0 && h > 0) return w to h
          }
          break
        }
        pos += segmentLength
      }
    }
    // 4. Platform BitmapFactory bounds fallback
    return try {
      val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
      BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
      if (options.outWidth > 0 && options.outHeight > 0) {
        options.outWidth to options.outHeight
      } else null
    } catch (_: Throwable) {
      null
    }
  }

  private fun u16Le(bytes: ByteArray, offset: Int): Int =
    (bytes[offset].toInt() and 0xff) or ((bytes[offset + 1].toInt() and 0xff) shl 8)

  private fun u16Be(bytes: ByteArray, offset: Int): Int =
    ((bytes[offset].toInt() and 0xff) shl 8) or (bytes[offset + 1].toInt() and 0xff)

  private fun u24Le(bytes: ByteArray, offset: Int): Int =
    (bytes[offset].toInt() and 0xff) or
      ((bytes[offset + 1].toInt() and 0xff) shl 8) or
      ((bytes[offset + 2].toInt() and 0xff) shl 16)

  private fun u32Be(bytes: ByteArray, offset: Int): Int =
    ((bytes[offset].toInt() and 0xff) shl 24) or
      ((bytes[offset + 1].toInt() and 0xff) shl 16) or
      ((bytes[offset + 2].toInt() and 0xff) shl 8) or
      (bytes[offset + 3].toInt() and 0xff)
}
