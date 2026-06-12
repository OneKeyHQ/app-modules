package com.margelo.nitro.reactnativerangedownloader

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise
import com.margelo.nitro.nativelogger.OneKeyLog
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong

// P1: Nitro adapter for the Android concurrent multi-range downloader.
//
// The core algorithm lives in the in-module ConcurrentRangeDownloader helper:
// each of the N segments streams into its own sibling file
// `<dest>.partial.seg0` .. `<dest>.partial.segN-1` (no whole-file preallocation,
// no `.progress` manifest), and on success the segments are concatenated in
// order into `<dest>.partial`. Resume re-uses whatever bytes each `.segN`
// already holds. An 8-segment thread pool plus a 200-to-a-Range fallback round
// it out (object identity is not pinned — no ETag/If-Range; the optional
// whole-file SHA256 self-check below is the correctness backstop). This class
// builds the HTTPS-only OkHttpClient, drives the helper,
// finalizes on COMPLETED (promote .partial -> dest + optional SHA256
// self-check), and bridges progress to the shared listener registry as
// RangeDownloadEvent (tagged with channel/taskId).
//
// Android has no background-session concept: `channel` is only an event label
// + artifact-directory tag and does not change the download mechanism.
@DoNotStrip
class ReactNativeRangeDownloader : HybridReactNativeRangeDownloaderSpec() {

  private class Listener(val id: Double, val callback: (RangeDownloadEvent) -> Unit)

  private val listeners = CopyOnWriteArrayList<Listener>()
  private val nextListenerId = AtomicLong(1)

  // Active downloads keyed by "channel|taskId" so cancel/discardArtifacts can
  // flip the abort flag + stop the worker pool BEFORE deleting files, instead of
  // racing live workers that would resurrect a just-deleted .partial.
  private val activeDownloads =
    ConcurrentHashMap<String, ConcurrentRangeDownloader.CancelHandle>()

  private fun runKey(channel: DownloadChannel, taskId: String): String =
    "${channel.name}|$taskId"

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

      val runKey = runKey(channel, taskId)
      val cancelHandle = ConcurrentRangeDownloader.CancelHandle()
      activeDownloads[runKey] = cancelHandle

      // The progress callback is invoked concurrently by the helper's worker
      // threads, so guard lastProgress with an AtomicInteger + CAS: only the
      // thread that advances the percentage to a strictly higher value wins the
      // CAS and emits the event, which keeps progress monotonic and de-duped
      // without a lock (this only affects event ordering, never file bytes).
      val lastProgress = java.util.concurrent.atomic.AtomicInteger(-1)
      val outcome = try {
        ConcurrentRangeDownloader(
          httpClient = httpClient,
          segmentCount = segmentCount,
          minConcurrentBytes = minConcurrentBytes,
          log = { msg -> OneKeyLog.info("RangeDownloader", msg) },
        ).download(downloadUrl, partialFilePath, cancelHandle) { transferred, total ->
          if (total > 0) {
            val p = ((transferred * 100) / total).toInt().coerceIn(0, 100)
            val prev = lastProgress.get()
            if (p > prev && lastProgress.compareAndSet(prev, p)) {
              sendEvent(channel, taskId, type = "progress", progress = p)
            }
          }
        }
      } finally {
        // Only deregister our own handle (a concurrent cancel may have replaced it).
        activeDownloads.remove(runKey, cancelHandle)
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
      // Use an atomic move so a kill mid-finalize never leaves NEITHER file:
      // the destination is replaced in one step, preserving the previous file on
      // failure (vs. the old delete-then-rename, which had a window with both
      // gone if the rename then failed).
      if (!promoteAtomically(File(partialFilePath), destFile)) {
        OneKeyLog.error("RangeDownloader", "download: promote .partial -> final failed")
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
      // Cancel-then-delete: stop any live workers for this task before removing
      // files, otherwise a still-running segment could re-create the .partial we
      // just deleted.
      cancelActive(channel, taskId)
      // Sweep the per-segment `.segN` files plus the concatenated `.partial` so a
      // future resume can't re-trust stale bytes (no `.progress` manifest exists
      // anymore in the segmented model). Glob by filename prefix so any custom
      // segmentCount is fully cleared, not just the shipped default of 8.
      sweepPartialArtifacts(destFilePath)
      OneKeyLog.info("RangeDownloader", "discardArtifacts: channel=$channel taskId=$taskId")
      Unit
    }
  }

  override fun cancel(
    channel: DownloadChannel,
    taskId: String,
    destFilePath: String
  ): Promise<Unit> {
    return Promise.async {
      // Stop workers first, then delete artifacts so nothing resurrects them.
      cancelActive(channel, taskId)
      // Same segmented-artifact sweep as discardArtifacts: glob every per-segment
      // `.segN` file by prefix plus the concatenated `.partial`.
      sweepPartialArtifacts(destFilePath)
      OneKeyLog.info("RangeDownloader", "cancel: channel=$channel taskId=$taskId")
      Unit
    }
  }

  // Flip the abort flag + shutdown the pool for an in-flight download (if any).
  private fun cancelActive(channel: DownloadChannel, taskId: String) {
    activeDownloads.remove(runKey(channel, taskId))?.cancel()
  }

  // Delete every sibling artifact for [destFilePath]: all `<dest>.partial.seg<N>`
  // segment files (matched by filename prefix, so any segmentCount is swept, not
  // just the shipped default) plus the concatenated `<dest>.partial` itself.
  private fun sweepPartialArtifacts(destFilePath: String) {
    val partial = File("$destFilePath.partial")
    partial.parentFile
      ?.listFiles { f -> f.name.startsWith(partial.name + ".seg") }
      ?.forEach { it.delete() }
    partial.delete()
  }

  // Atomically replace [dest] with [src] so a kill mid-finalize never leaves
  // NEITHER file. On API 26+ uses Files.move with ATOMIC_MOVE/REPLACE_EXISTING
  // (single-step rename onto the destination). On older APIs (java.nio.file is
  // API 26+) File.renameTo onto an existing dest is itself an atomic rename on a
  // POSIX filesystem (the kernel replaces the inode in one step), which gives
  // the same "old file preserved until the new one lands" guarantee.
  private fun promoteAtomically(src: File, dest: File): Boolean {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      try {
        java.nio.file.Files.move(
          src.toPath(), dest.toPath(),
          java.nio.file.StandardCopyOption.ATOMIC_MOVE,
          java.nio.file.StandardCopyOption.REPLACE_EXISTING,
        )
        return true
      } catch (e: Exception) {
        // ATOMIC_MOVE may be unsupported across the source/dest (e.g. different
        // stores) — retry a plain replace, still single-step on one filesystem.
        try {
          java.nio.file.Files.move(
            src.toPath(), dest.toPath(),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING,
          )
          return true
        } catch (e2: Exception) {
          OneKeyLog.error(
            "RangeDownloader",
            "promoteAtomically: Files.move failed (${e2.javaClass.simpleName}), falling back to renameTo",
          )
        }
      }
    }
    // API < 26 (or Files.move unsupported): rename directly onto the dest. On a
    // POSIX filesystem this replaces the destination atomically and keeps the old
    // file until the rename lands. Do NOT pre-delete the dest — that reintroduces
    // the both-files-gone window we are fixing.
    return src.renameTo(dest)
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
