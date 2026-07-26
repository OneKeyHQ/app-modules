package com.margelo.nitro.reactnativerangedownloader

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.ArrayBuffer
import com.margelo.nitro.core.Promise
import com.margelo.nitro.nativelogger.OneKeyLog
import java.io.File
import java.security.MessageDigest
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
  // racing live workers that would resurrect a just-deleted .partial. The keyed
  // single-flight logic lives in RangeDownloadLogic.RunRegistry (dependency-free,
  // unit-tested); this class only supplies the channel/taskId.
  private val activeDownloads = RangeDownloadLogic.RunRegistry()

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

      val cancelHandle = activeDownloads.start(channel.name, taskId)

      // Worker callbacks are concurrent. Keep the high-water update and event
      // emission in one serialized critical section so the observed callback
      // order cannot regress after a lower-percentage thread is descheduled.
      val progressEmitter = RangeDownloadLogic.MonotonicProgressEmitter { progress ->
        sendEvent(channel, taskId, type = "progress", progress = progress)
      }
      val outcome = try {
        ConcurrentRangeDownloader(
          httpClient = httpClient,
          segmentCount = segmentCount,
          minConcurrentBytes = minConcurrentBytes,
          log = { msg -> OneKeyLog.info("RangeDownloader", msg) },
        ).download(downloadUrl, partialFilePath, cancelHandle) { transferred, total ->
          progressEmitter.publish(transferred, total)
        }
      } finally {
        // Only deregister our own handle (a concurrent cancel may have replaced it).
        activeDownloads.finish(channel.name, taskId, cancelHandle)
      }

      if (outcome == ConcurrentRangeDownloader.Outcome.FALLBACK) {
        // OCDS §4 wire mapping (parity with the iOS shim). On Android the core
        // helper splits the two §4 classes by RETURN vs THROW: a RESUMABLE
        // (transient) interruption — network drop / incomplete segment — is thrown
        // and surfaces below as a promise rejection (the `.segN`/`.partial` are
        // kept). `Outcome.FALLBACK` is therefore the PERMANENT class only: range
        // unsupported / a 200 to a Range request / the object is too small / a
        // single-stream leftover. The helper has already wiped its concurrent
        // artifacts on this path, so it maps to `FALLBACKPERMANENT` with the
        // `RANGEUNSUPPORTED` sub-kind, and the caller restarts single-stream.
        OneKeyLog.info("RangeDownloader", "download: concurrent not used, returning permanent fallback")
        sendEvent(channel, taskId, type = "fallback", message = "concurrent unavailable")
        return@async RangeDownloadResult(
          outcome = RangeDownloadOutcome.FALLBACKPERMANENT,
          filePath = destFilePath,
          fallbackReason = "concurrent unavailable (range unsupported / 200 / too small / single-stream partial)",
          fallbackKind = RangeFallbackKind.RANGEUNSUPPORTED,
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
        fallbackKind = null,
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
    activeDownloads.cancel(channel.name, taskId)
  }

  // Delete every sibling artifact for [destFilePath]: all `<dest>.partial.seg<N>`
  // segment files (matched by filename prefix, so any segmentCount is swept, not
  // just the shipped default) plus the concatenated `<dest>.partial` itself.
  private fun sweepPartialArtifacts(destFilePath: String) =
    RangeDownloadLogic.sweepPartialArtifacts(destFilePath)

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

  override fun getFirmwareArtifactCapabilities(): FirmwareArtifactCapabilities =
    FirmwareArtifactCapabilities(
      firmwareArtifactProtocolVersion = 2.0,
      supportedRouteTypes = arrayOf(
        "domain",
        "pinnedIp",
      ),
      supportsArchiveMaterialization = true,
      maxReadBytes = (256 * 1024).toDouble(),
    )

  override fun downloadFirmwareArtifact(
    params: FirmwareArtifactDownloadParams,
  ): Promise<FirmwareArtifactReceipt> =
    Promise.async {
      val metadata = firmwareArtifactDownloader().download(
        FirmwareArtifactDownloader.validate(params)
      )
      metadata.toFirmwareArtifactReceipt()
    }

  override fun getFirmwareArtifactStatus(
    params: FirmwareArtifactStatusParams,
  ): Promise<FirmwareArtifactStatus> =
    Promise.async {
      val snapshot = firmwareArtifactDownloader().status(
        leaseRef = params.leaseRef,
        taskId = params.taskId,
      )
      FirmwareArtifactStatus(
        state = snapshot.state.toWireState(),
        downloadedBytes = snapshot.downloadedBytes.toDouble(),
        expectedSize = snapshot.expectedSize.takeIf { it > 0 }?.toDouble(),
        receipt = snapshot.artifact?.toFirmwareArtifactReceipt(),
        errorCode = snapshot.errorCode,
        errorMessage = snapshot.errorMessage,
        retryable = snapshot.retryable,
      )
    }

  override fun cancelFirmwareArtifact(
    params: FirmwareArtifactTaskParams,
  ): Promise<Unit> =
    Promise.async {
      firmwareArtifactDownloader().cancel(
        leaseRef = params.leaseRef,
        taskId = params.taskId,
      )
      Unit
    }

  override fun discardFirmwareArtifact(
    params: FirmwareArtifactRefParams,
  ): Promise<Unit> =
    Promise.async {
      firmwareArtifactStore().discardArtifact(params.artifactRef)
      Unit
    }

  override fun quarantineFirmwareArtifact(
    params: FirmwareArtifactRefParams,
  ): Promise<Unit> =
    Promise.async {
      firmwareArtifactStore().quarantineArtifact(params.artifactRef)
      Unit
    }

  override fun openFirmwareArtifact(
    params: FirmwareArtifactReaderOpenParams,
  ): Promise<FirmwareArtifactReaderInfo> =
    Promise.async {
      val info = firmwareArtifactReader().open(
        artifactRef = params.artifactRef,
        immutableToken = params.immutableToken,
      )
      FirmwareArtifactReaderInfo(
        readerId = info.readerId,
        size = info.size.toDouble(),
        immutableToken = info.immutableToken,
        maxReadBytes = FirmwareArtifactReader.MAX_READ_BYTES.toDouble(),
      )
    }

  override fun readFirmwareArtifact(
    params: FirmwareArtifactReaderReadParams,
  ): Promise<ArrayBuffer> =
    Promise.async {
      ArrayBuffer.copy(
        firmwareArtifactReader().read(
          readerId = params.readerId,
          offset = params.offset,
          length = params.length,
        )
      )
    }

  override fun closeFirmwareArtifact(
    params: FirmwareArtifactReaderCloseParams,
  ): Promise<Unit> =
    Promise.async {
      firmwareArtifactReader().close(params.readerId)
      Unit
    }

  override fun materializeFirmwareArchive(
    params: FirmwareArchiveMaterializeParams,
  ): Promise<FirmwareArchiveMaterializeResult> =
    Promise.async {
      val entries = firmwareArchiveMaterializer().materialize(
        leaseRef = params.leaseRef,
        parentArtifactId = params.parentArtifactId,
        archiveArtifactRef = params.archiveArtifactRef,
        archiveImmutableToken = params.archiveImmutableToken,
        materializationPolicy = params.materializationPolicy,
      )
      FirmwareArchiveMaterializeResult(
        artifacts = entries.map { entry ->
          FirmwareArchiveMaterializedArtifact(
            entryId = entry.entryId,
            logicalName = entry.logicalName,
            receipt = entry.artifact.toFirmwareArtifactReceipt(),
          )
        }.toTypedArray(),
      )
    }

  override fun createFirmwareArtifactLease(
    params: FirmwareArtifactLeaseCreateParams,
  ): Promise<FirmwareArtifactLease> =
    Promise.async {
      val lease = firmwareArtifactStore().createLease(params.transactionId)
      FirmwareArtifactLease(leaseRef = lease.leaseRef)
    }

  override fun retainFirmwareArtifact(
    params: FirmwareArtifactRetainParams,
  ): Promise<Unit> =
    Promise.async {
      firmwareArtifactStore().retainArtifact(
        leaseRef = params.leaseRef,
        artifactRef = params.artifactRef,
      )
      Unit
    }

  override fun releaseFirmwareArtifactLease(
    params: FirmwareArtifactLeaseReleaseParams,
  ): Promise<Unit> =
    Promise.async {
      val disposition = when (params.disposition) {
        FirmwareArtifactLeaseDisposition.COMPLETED -> StoredLeaseDisposition.COMPLETED
        FirmwareArtifactLeaseDisposition.SAFECANCELLED -> StoredLeaseDisposition.SAFE_CANCELLED
        FirmwareArtifactLeaseDisposition.SAFEABANDONED -> StoredLeaseDisposition.SAFE_ABANDONED
      }
      firmwareArtifactStore().releaseLease(
        leaseRef = params.leaseRef,
        disposition = disposition,
      )
      Unit
    }

  override fun reconcileFirmwareArtifactLeases(
    params: FirmwareArtifactLeaseReconcileParams,
  ): Promise<Unit> =
    Promise.async {
      firmwareArtifactStore().reconcileLeases(params.activeLeaseRefs.toList())
      Unit
    }

  override fun sweepFirmwareArtifactOrphans(): Promise<FirmwareArtifactSweepResult> =
    Promise.async {
      val result = firmwareArtifactStore().sweepOrphans()
      FirmwareArtifactSweepResult(
        deletedFiles = result.deletedFiles.toDouble(),
        deletedBytes = result.deletedBytes.toDouble(),
      )
    }

  private fun firmwareArtifactStore(): FirmwareArtifactStore {
    val context = NitroModules.applicationContext
      ?: throw IllegalStateException("ARTIFACT_APPLICATION_CONTEXT_UNAVAILABLE")
    return FirmwareArtifactStore.processInstance(
      File(context.filesDir, "onekey-firmware-artifacts-v1"),
    )
  }

  private fun firmwareArtifactDownloader(): FirmwareArtifactDownloader =
    FirmwareArtifactDownloader(firmwareArtifactStore())

  private fun firmwareArtifactReader(): FirmwareArtifactReader =
    FirmwareArtifactReader.processInstance(firmwareArtifactStore())

  private fun firmwareArchiveMaterializer(): FirmwareArchiveMaterializer =
    FirmwareArchiveMaterializer.processInstance(firmwareArtifactStore())

  private fun StoredArtifactMetadata.toFirmwareArtifactReceipt(): FirmwareArtifactReceipt {
    val size = actualSize ?: throw FirmwareArtifactStoreException.invalidMetadata()
    val digest = actualSha256 ?: throw FirmwareArtifactStoreException.invalidMetadata()
    val token = immutableToken ?: throw FirmwareArtifactStoreException.invalidMetadata()
    return FirmwareArtifactReceipt(
      artifactRef = artifactRef,
      size = size.toDouble(),
      sha256 = digest,
      immutableToken = token,
    )
  }

  private fun FirmwareArtifactRegistryState.toWireState(): FirmwareArtifactStatusState =
    when (this) {
      FirmwareArtifactRegistryState.NOT_FOUND -> FirmwareArtifactStatusState.NOTFOUND
      FirmwareArtifactRegistryState.QUEUED -> FirmwareArtifactStatusState.QUEUED
      FirmwareArtifactRegistryState.DOWNLOADING -> FirmwareArtifactStatusState.DOWNLOADING
      FirmwareArtifactRegistryState.VERIFYING -> FirmwareArtifactStatusState.VERIFYING
      FirmwareArtifactRegistryState.COMPLETED -> FirmwareArtifactStatusState.COMPLETED
      FirmwareArtifactRegistryState.CANCELLED -> FirmwareArtifactStatusState.CANCELLED
      FirmwareArtifactRegistryState.FAILED -> FirmwareArtifactStatusState.FAILED
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
