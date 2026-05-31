package com.margelo.nitro.reactnativerangedownloader

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise
import com.margelo.nitro.nativelogger.OneKeyLog
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong

// P1: faithful migration of the Android concurrent multi-range downloader.
//
// The core algorithm (.partial preallocation + .progress manifest resume +
// 8-segment thread pool + If-Range/200 fallback) lives unchanged in the
// in-module ConcurrentRangeDownloader helper, copied byte-for-byte from
// react-native-bundle-update. This class is the Nitro adapter: it builds the
// HTTPS-only OkHttpClient, drives the helper, finalizes on COMPLETED (promote
// .partial -> dest + optional SHA256 self-check), and bridges progress to the
// shared listener registry as RangeDownloadEvent (tagged with channel/taskId).
//
// Android has no background-session concept: `channel` is only an event label
// + artifact-directory tag and does not change the download mechanism. The
// on-disk format (.partial + .progress) is kept exactly as shipped so existing
// interrupted downloads resume cleanly.
@DoNotStrip
class ReactNativeRangeDownloader : HybridReactNativeRangeDownloaderSpec() {

  private class Listener(val id: Double, val callback: (RangeDownloadEvent) -> Unit)

  private val listeners = CopyOnWriteArrayList<Listener>()
  private val nextListenerId = AtomicLong(1)

  // HTTPS-only client: reject any redirect to a non-HTTPS hop. Mirrors the
  // existing react-native-bundle-update configuration verbatim.
  private val httpClient = okhttp3.OkHttpClient.Builder()
    .addNetworkInterceptor { chain ->
      val req = chain.request()
      if (!req.url.isHttps) {
        throw java.io.IOException("Redirect to non-HTTPS URL is not allowed: ${req.url}")
      }
      chain.proceed(req)
    }
    .build()

  override fun download(params: RangeDownloadParams): Promise<RangeDownloadResult> {
    return Promise.async {
      val channel = params.channel
      val taskId = params.taskId
      val downloadUrl = params.url
      val destFilePath = params.destFilePath
      val expectedSha256 = params.expectedSha256

      // HTTPS-only, same gate as the source module.
      if (!downloadUrl.startsWith("https://")) {
        OneKeyLog.error("RangeDownloader", "download: URL is not HTTPS: $downloadUrl")
        sendEvent(channel, taskId, type = "error", message = "Download URL must use HTTPS")
        throw Exception("Download URL must use HTTPS")
      }

      // Resume support: download to <dest>.partial, promote to <dest> on success.
      val partialFilePath = "$destFilePath.partial"
      val destFile = File(destFilePath)

      // Optional tuning knobs, defaulting to the shipped behavior (8 segments / 2MB).
      val segmentCount = params.segmentCount?.toInt()?.takeIf { it > 0 } ?: 8
      val minConcurrentBytes = params.minConcurrentBytes?.toLong()?.takeIf { it > 0 }
        ?: (2L * 1024 * 1024)

      sendEvent(channel, taskId, type = "start")

      var lastProgress = -1
      val outcome = ConcurrentRangeDownloader(
        httpClient = httpClient,
        segmentCount = segmentCount,
        minConcurrentBytes = minConcurrentBytes,
        log = { msg -> OneKeyLog.info("RangeDownloader", msg) },
      ).download(downloadUrl, partialFilePath) { transferred, total ->
        if (total > 0) {
          val p = ((transferred * 100) / total).toInt().coerceIn(0, 100)
          if (p != lastProgress) {
            sendEvent(channel, taskId, type = "progress", progress = p)
            lastProgress = p
          }
        }
      }

      if (outcome == ConcurrentRangeDownloader.Outcome.FALLBACK) {
        // Concurrency unusable. The helper has already cleaned up its own
        // artifacts where appropriate; the caller runs its single-stream path.
        OneKeyLog.info("RangeDownloader", "download: concurrent not used, returning fallback")
        sendEvent(channel, taskId, type = "fallback", message = "concurrent unavailable")
        return@async RangeDownloadResult(
          outcome = RangeDownloadOutcome.FALLBACK,
          filePath = destFilePath,
          fallbackReason = "concurrent unavailable (range unsupported / 200 / too small / single-stream partial)",
        )
      }

      // COMPLETED: `.partial` is fully on disk. Promote -> final, then run the
      // optional in-module SHA256 self-check (mirrors the source finalize path).
      if (destFile.exists()) destFile.delete()
      if (!File(partialFilePath).renameTo(destFile)) {
        OneKeyLog.error("RangeDownloader", "download: rename .partial -> final failed")
        sendEvent(channel, taskId, type = "error", message = "Failed to finalize download")
        throw Exception("Failed to finalize download")
      }

      if (!expectedSha256.isNullOrEmpty()) {
        OneKeyLog.info("RangeDownloader", "download: concurrent finished, verifying SHA256...")
        if (!verifySHA256(destFilePath, expectedSha256)) {
          destFile.delete()
          OneKeyLog.error("RangeDownloader", "download: SHA256 verification failed")
          sendEvent(channel, taskId, type = "error", message = "SHA256 verification failed")
          throw Exception("SHA256 verification failed")
        }
      }

      sendEvent(channel, taskId, type = "complete", progress = 100)
      OneKeyLog.info("RangeDownloader", "download: completed channel=$channel taskId=$taskId")
      RangeDownloadResult(
        outcome = RangeDownloadOutcome.COMPLETED,
        filePath = destFilePath,
        fallbackReason = null,
      )
    }
  }

  override fun discardArtifacts(
    channel: DownloadChannel,
    taskId: String,
    destFilePath: String
  ): Promise<Unit> {
    return Promise.async {
      // Manifest first so it never outlives the partial it describes.
      File("$destFilePath.partial.progress").delete()
      File("$destFilePath.partial").delete()
      OneKeyLog.info("RangeDownloader", "discardArtifacts: channel=$channel taskId=$taskId")
      Unit
    }
  }

  override fun addDownloadListener(callback: (event: RangeDownloadEvent) -> Unit): Double {
    val id = nextListenerId.getAndIncrement().toDouble()
    listeners.add(Listener(id, callback))
    OneKeyLog.debug("RangeDownloader", "addDownloadListener: id=$id, totalListeners=${listeners.size}")
    return id
  }

  override fun removeDownloadListener(id: Double) {
    listeners.removeAll { it.id == id }
    OneKeyLog.debug("RangeDownloader", "removeDownloadListener: id=$id, totalListeners=${listeners.size}")
  }

  // App cache directory — an app-owned, writable absolute path resolved at
  // runtime (no hardcoded sandbox path).
  override fun getDownloadsDir(): String {
    val ctx = NitroModules.applicationContext ?: return ""
    return ctx.cacheDir.absolutePath
  }

  // Broadcast one event to every registered listener. Listeners filter by
  // channel/taskId on their side (shared registry, per the design).
  private fun sendEvent(
    channel: DownloadChannel,
    taskId: String,
    type: String,
    progress: Int = 0,
    message: String = "",
  ) {
    val event = RangeDownloadEvent(
      channel = channel,
      taskId = taskId,
      type = type,
      progress = progress.toDouble(),
      message = message,
    )
    for (listener in listeners) {
      try {
        listener.callback(event)
      } catch (e: Exception) {
        OneKeyLog.error("RangeDownloader", "Error sending event: ${e.message}")
      }
    }
  }

  private fun verifySHA256(filePath: String, expected: String): Boolean {
    val calculated = calculateSHA256(filePath) ?: return false
    return secureCompare(calculated, expected)
  }

  private fun calculateSHA256(filePath: String): String? {
    val file = File(filePath)
    if (!file.exists()) {
      OneKeyLog.error("RangeDownloader", "calculateSHA256: file not found: $filePath")
      return null
    }
    return try {
      val digest = MessageDigest.getInstance("SHA-256")
      java.io.BufferedInputStream(java.io.FileInputStream(filePath)).use { bis ->
        val buffer = ByteArray(8192)
        var count: Int
        while (bis.read(buffer).also { count = it } > 0) {
          digest.update(buffer, 0, count)
        }
      }
      bytesToHex(digest.digest())
    } catch (e: Exception) {
      OneKeyLog.error("RangeDownloader", "calculateSHA256: ${e.javaClass.simpleName}: ${e.message}")
      null
    }
  }

  private fun bytesToHex(bytes: ByteArray): String {
    val sb = StringBuilder(bytes.size * 2)
    for (b in bytes) {
      sb.append(String.format("%02x", b))
    }
    return sb.toString()
  }

  // Constant-time comparison of two hex SHA256 strings (lower-cased).
  private fun secureCompare(a: String, b: String): Boolean {
    val x = a.lowercase()
    val y = b.lowercase()
    if (x.length != y.length) return false
    var result = 0
    for (i in x.indices) {
      result = result or (x[i].code xor y[i].code)
    }
    return result == 0
  }
}
