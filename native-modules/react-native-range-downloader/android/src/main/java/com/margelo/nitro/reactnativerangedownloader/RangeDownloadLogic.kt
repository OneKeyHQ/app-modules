package com.margelo.nitro.reactnativerangedownloader

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

// Dependency-free RangeDownloader adapter logic.
//
// This file holds the DETERMINISTIC, dependency-light pieces of the Nitro
// adapter (ReactNativeRangeDownloader): the run-key derivation that pins
// single-flight semantics, the CAS gate that keeps progress monotonic and
// de-duped, and the per-segment artifact sweep. They were extracted VERBATIM
// (bodies unchanged) from `ReactNativeRangeDownloader.kt` so they can be
// compiled and unit-tested WITHOUT the NitroModules / OneKeyLog dependencies
// or the HybridObject JNI base class (which is device-only).
//
// `ReactNativeRangeDownloader` keeps owning the Nitro wiring, OkHttp client,
// Promise.async flows, sendEvent, SHA/promote — it only DELEGATES these three
// behaviors via `RangeDownloadLogic.<fn>`. Mirrors the iOS `RangeDownloadLogic`
// extraction. Everything here is pure Kotlin + java.io.File.
object RangeDownloadLogic {

  fun sameArtifactObjectIdentity(
    leftSupportsRange: Boolean,
    leftTotal: Long,
    leftStrongETag: String?,
    leftLastModified: String?,
    rightSupportsRange: Boolean,
    rightTotal: Long,
    rightStrongETag: String?,
    rightLastModified: String?,
  ): Boolean {
    if (leftSupportsRange != rightSupportsRange || leftTotal != rightTotal) {
      return false
    }
    if (leftStrongETag != null || rightStrongETag != null) {
      return leftStrongETag == rightStrongETag
    }
    return leftLastModified?.trim()?.takeIf(String::isNotEmpty) ==
      rightLastModified?.trim()?.takeIf(String::isNotEmpty)
  }

  /**
   * Serializes both the high-water-mark update and the callback invocation.
   *
   * An AtomicInteger alone keeps the stored maximum monotonic, but it does not
   * order callbacks after a successful CAS: a thread that wins 40 can pause
   * before emitting while another thread wins and emits 50 first. Keeping the
   * callback inside this monitor makes the observable event stream monotonic.
   */
  class MonotonicProgressEmitter(
    private val emit: (Int) -> Unit,
  ) {
    private val monitor = Any()
    private var previousProgress = -1

    fun publish(transferred: Long, total: Long) {
      val progress = progressPercent(transferred, total) ?: return
      synchronized(monitor) {
        if (progress <= previousProgress) return
        previousProgress = progress
        emit(progress)
      }
    }
  }

  // Single-flight key: active downloads are keyed by "channel|taskId" so
  // cancel/discardArtifacts can flip the abort flag + stop the worker pool
  // BEFORE deleting files, instead of racing live workers that would resurrect
  // a just-deleted .partial. Channel is identified by its enum name.
  fun runKey(channelName: String, taskId: String): String = "$channelName|$taskId"

  // Single-flight registry of in-flight downloads keyed by "channel|taskId".
  //
  // Extracted VERBATIM from the adapter's inline `activeDownloads`
  // ConcurrentHashMap usage so the keyed single-flight invariants can be unit
  // tested WITHOUT the Nitro / JNI / OkHttp dependencies. The adapter delegates
  // to one instance; behaviour is byte-for-byte the same:
  //  - [start] registers a fresh handle under the key, overwriting any prior
  //    one (dedup: `map[key] = handle`, adapter:87).
  //  - [finish] does an IDENTITY-checked `map.remove(key, handle)` so a
  //    concurrent [cancel] that already replaced the handle is NOT clobbered
  //    (adapter:112 invariant: "only deregister our own handle").
  //  - [cancel] atomically removes + `.cancel()`s the live handle if present
  //    (adapter:206).
  //
  // ConcurrentHashMap gives the atomic put / identity-remove / remove primitives;
  // no extra lock is needed.
  class RunRegistry {
    private val active =
      ConcurrentHashMap<String, ConcurrentRangeDownloader.CancelHandle>()

    /** Live entry count — for assertions/diagnostics (no leaked keys on success/cancel). */
    val size: Int get() = active.size

    /** Register a fresh handle for [channelName]|[taskId], overwriting any prior. */
    fun start(channelName: String, taskId: String): ConcurrentRangeDownloader.CancelHandle {
      val handle = ConcurrentRangeDownloader.CancelHandle()
      active[runKey(channelName, taskId)] = handle
      return handle
    }

    /**
     * Deregister [handle] for the key — but ONLY if it is still the live handle.
     * A concurrent [cancel] that replaced it must not be clobbered.
     */
    fun finish(channelName: String, taskId: String, handle: ConcurrentRangeDownloader.CancelHandle) {
      active.remove(runKey(channelName, taskId), handle)
    }

    /** Remove + cancel the live handle for the key, if any. No-op when absent. */
    fun cancel(channelName: String, taskId: String) {
      active.remove(runKey(channelName, taskId))?.cancel()
    }
  }

  // Converts byte progress to a bounded percentage. Observable event ordering
  // is owned by [MonotonicProgressEmitter].
  fun progressPercent(transferred: Long, total: Long): Int? {
    if (total <= 0) return null
    return ((transferred * 100) / total).toInt().coerceIn(0, 100)
  }

  fun calculateSHA256(file: File): String? {
    if (!file.isFile) return null
    return try {
      val digest = MessageDigest.getInstance("SHA-256")
      FileInputStream(file).use { input ->
        val buffer = ByteArray(8 * 1024)
        while (true) {
          val read = input.read(buffer)
          if (read < 0) break
          if (read > 0) digest.update(buffer, 0, read)
        }
      }
      digest.digest().joinToString("") { byte -> "%02x".format(byte) }
    } catch (_: Exception) {
      null
    }
  }

  fun secureHexEquals(lhs: String, rhs: String): Boolean {
    val left = lhs.lowercase().toByteArray(Charsets.US_ASCII)
    val right = rhs.lowercase().toByteArray(Charsets.US_ASCII)
    return MessageDigest.isEqual(left, right)
  }

  // Delete every sibling artifact for [destFilePath]: all `<dest>.partial.seg<N>`
  // segment files (matched by filename prefix, so any segmentCount is swept, not
  // just the shipped default) plus the concatenated `<dest>.partial` itself.
  // Glob by filename prefix so a future resume can't re-trust stale bytes (no
  // `.progress` manifest exists anymore in the segmented model).
  fun sweepPartialArtifacts(destFilePath: String) {
    val partial = File("$destFilePath.partial")
    partial.parentFile
      ?.listFiles { f -> f.name.startsWith(partial.name + ".seg") }
      ?.forEach { it.delete() }
    partial.delete()
  }
}
