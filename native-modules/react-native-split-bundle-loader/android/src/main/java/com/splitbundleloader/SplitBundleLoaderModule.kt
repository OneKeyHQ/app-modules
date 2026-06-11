package com.splitbundleloader

import android.content.Context
import android.content.res.AssetManager
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.common.annotations.FrameworkAPI
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.turbomodule.core.CallInvokerHolderImpl
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicBoolean
import android.os.Handler
import android.os.Looper

/**
 * TurboModule entry point for SplitBundleLoader.
 *
 * Provides two methods to JS:
 * 1. getRuntimeBundleContext() — Returns current runtime's bundle paths and source kind
 * 2. loadSegment(params) — Registers a HBC segment with the current Hermes runtime
 *
 * Mirrors iOS SplitBundleLoader.mm.
 */
@ReactModule(name = SplitBundleLoaderModule.NAME)
class SplitBundleLoaderModule(reactContext: ReactApplicationContext) :
    NativeSplitBundleLoaderSpec(reactContext) {

    /**
     * Completion contract invoked by the native (JNI) side AFTER the segment
     * has been evaluated into the runtime. Called from the JS thread.
     *
     * @param error null on success; a non-empty message on failure. A failure
     *   message prefixed with "IO_ERROR:" denotes a segment file read/mmap
     *   failure (mapped to SPLIT_BUNDLE_IO_ERROR); any other message denotes a
     *   segment JS/Hermes eval throw (mapped to SPLIT_BUNDLE_EVAL_ERROR).
     */
    fun interface SegmentEvalCallback {
        fun onComplete(error: String?)
    }

    companion object {
        const val NAME = "SplitBundleLoader"
        private const val BUILTIN_EXTRACT_DIR = "onekey-builtin-segments"

        // Bounded watchdog: if the JS thread is wedged and the segment eval
        // never runs, reject rather than leaving the JS promise pending forever.
        // Generous because a cold JS thread under load can legitimately take a
        // while to drain to our scheduled eval.
        private const val SEGMENT_EVAL_TIMEOUT_MS = 30_000L

        // Loads the JNI library that provides nativeEvaluateSegment. Wrapped so
        // a missing/failed load is detectable: loadSegment then fail-closes with
        // SPLIT_BUNDLE_NATIVE_UNAVAILABLE instead of crashing (we deliberately do
        // NOT fall back to the legacy registerSegment path).
        @JvmStatic
        @Volatile
        var nativeLibLoaded: Boolean = false
            private set

        init {
            nativeLibLoaded = try {
                System.loadLibrary("splitbundleloader")
                true
            } catch (e: Throwable) {
                SBLLogger.warn("[SplitBundle] failed to load native lib 'splitbundleloader': ${e.message}")
                false
            }
        }

        /**
         * JNI entry point. Schedules evaluation of the segment at [segmentPath]
         * onto the JS thread via the bridgeless CallInvoker and invokes
         * [callback] from inside that same JS-thread block, strictly AFTER the
         * segment's `__d(...)` module definitions have run. This is the ordering
         * guarantee that fixes the "Requiring unknown module" race.
         *
         * Returns immediately; [callback] fires later on the JS thread.
         */
        @JvmStatic
        @OptIn(FrameworkAPI::class)
        external fun nativeEvaluateSegment(
            callInvokerHolder: CallInvokerHolderImpl,
            segmentPath: String,
            sourceURL: String,
            callback: SegmentEvalCallback
        )
        // #18: Limit concurrent asset extractions to avoid I/O contention
        private const val MAX_CONCURRENT_EXTRACTS = 2
        private val extractSemaphore = Semaphore(MAX_CONCURRENT_EXTRACTS)

        // Wipe-on-APK-replace: avoids stale extracted HBC after overwrite install.
        // `lastUpdateTime` changes on every APK replacement (adb install -r,
        // Play Store upgrade, sideload, TestFlight-equivalent). If it differs
        // from what we persisted, nuke the whole extract tree so the new APK's
        // assets get extracted fresh on first load.
        private const val PREFS_NAME = "split_bundle_loader"
        private const val KEY_LAST_INSTALL_STAMP = "last_install_stamp"
        // Double-checked locking: loser threads must BLOCK until the wipe
        // finishes, not just skip. AtomicBoolean.compareAndSet would let
        // losers race ahead and read half-nuked state.
        @Volatile private var wipeCheckDone: Boolean = false
        private val wipeLock = Any()
    }

    override fun getName(): String = NAME

    // -----------------------------------------------------------------------
    // Wipe extract dir when APK install/upgrade detected.
    //
    // Without this, an overwrite install (adb install -r, Play Store upgrade,
    // etc.) leaves last-run's extracted HBC files in /data/.../files/, and
    // extractBuiltinSegmentIfNeeded reuses them because its size check can
    // pass even when Metro module IDs drifted. Nuking the tree on every
    // install-stamp change forces the new APK's assets to be re-extracted.
    //
    // Atomic gate means this runs at most once per process regardless of how
    // many entry points call it.
    // -----------------------------------------------------------------------

    private fun ensureExtractDirFreshForCurrentInstall(context: Context) {
        // Fast path: already done, no locking needed (volatile read).
        if (wipeCheckDone) return
        synchronized(wipeLock) {
            // Re-check inside the lock: another thread may have finished
            // the wipe while we were waiting to enter.
            if (wipeCheckDone) return

            val currentStamp = try {
                context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
            } catch (e: Exception) {
                SBLLogger.warn("[install-stamp] failed to read lastUpdateTime: ${e.message}")
                // Mark done even on failure: retrying every call won't help and
                // would defeat the gate. Conservative default is to NOT wipe.
                wipeCheckDone = true
                return
            }

            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedStamp = prefs.getLong(KEY_LAST_INSTALL_STAMP, -1L)
            if (savedStamp != currentStamp) {
                val baseDir = File(context.filesDir, BUILTIN_EXTRACT_DIR)
                if (baseDir.exists()) {
                    SBLLogger.info(
                        "[install-stamp] changed saved=$savedStamp current=$currentStamp, wiping ${baseDir.absolutePath}"
                    )
                    // rename-then-delete so the baseDir is gone atomically
                    // before we release the lock; waiters never see a half-nuked dir.
                    val tomb = File(
                        baseDir.parentFile,
                        ".${BUILTIN_EXTRACT_DIR}.stale-${System.nanoTime()}"
                    )
                    if (baseDir.renameTo(tomb)) {
                        tomb.deleteRecursively()
                    } else {
                        baseDir.deleteRecursively()
                    }
                } else {
                    SBLLogger.info(
                        "[install-stamp] first seen current=$currentStamp (no extract dir yet)"
                    )
                }
                // Persist synchronously (commit) so that a crash mid-wipe doesn't
                // leave us with a stale stamp + still-wiped dir next launch.
                prefs.edit().putLong(KEY_LAST_INSTALL_STAMP, currentStamp).commit()
            }
            // Publish via volatile write; fast path on other threads now sees true.
            wipeCheckDone = true
        }
    }

    // -----------------------------------------------------------------------
    // getRuntimeBundleContext
    // -----------------------------------------------------------------------

    override fun getRuntimeBundleContext(promise: Promise) {
        try {
            val context = reactApplicationContext
            ensureExtractDirFreshForCurrentInstall(context)
            val runtimeKind = "main"
            var sourceKind = "builtin"
            var bundleRoot = ""
            var nativeVersion = ""
            val bundleVersion = ""

            try {
                nativeVersion = context.packageManager
                    .getPackageInfo(context.packageName, 0).versionName ?: ""
            } catch (_: Exception) {
            }

            // Check OTA bundle path
            val otaBundlePath = getOtaBundlePath()
            if (!otaBundlePath.isNullOrEmpty()) {
                val otaFile = File(otaBundlePath)
                if (otaFile.exists()) {
                    sourceKind = "ota"
                    bundleRoot = otaFile.parent ?: ""
                }
            }

            val builtinExtractRoot = File(
                context.filesDir,
                "$BUILTIN_EXTRACT_DIR/$nativeVersion"
            ).absolutePath

            val result = Arguments.createMap()
            result.putString("runtimeKind", runtimeKind)
            result.putString("sourceKind", sourceKind)
            result.putString("bundleRoot", bundleRoot)
            result.putString("builtinExtractRoot", builtinExtractRoot)
            result.putString("nativeVersion", nativeVersion)
            result.putString("bundleVersion", bundleVersion)

            promise.resolve(result)

            // #17: Clean up old version extract directories asynchronously
            cleanupOldExtractDirs(context, nativeVersion)
        } catch (e: Exception) {
            promise.reject("SPLIT_BUNDLE_CONTEXT_ERROR", e.message, e)
        }
    }

    // -----------------------------------------------------------------------
    // resolveSegmentPath (Phase 3)
    // -----------------------------------------------------------------------

    override fun resolveSegmentPath(relativePath: String, sha256: String, promise: Promise) {
        try {
            ensureExtractDirFreshForCurrentInstall(reactApplicationContext)
            val absolutePath = resolveSegmentPath(relativePath, sha256)
            if (absolutePath != null && !verifySha256(absolutePath, sha256)) {
                promise.reject(
                    "SPLIT_BUNDLE_SHA256_MISMATCH",
                    "Segment SHA-256 mismatch: $relativePath"
                )
                return
            }
            if (absolutePath != null) {
                promise.resolve(absolutePath)
            } else {
                promise.reject(
                    "SPLIT_BUNDLE_NOT_FOUND",
                    "Segment file not found: $relativePath"
                )
            }
        } catch (e: Exception) {
            promise.reject("SPLIT_BUNDLE_RESOLVE_ERROR", e.message, e)
        }
    }

    // -----------------------------------------------------------------------
    // loadSegment
    // -----------------------------------------------------------------------

    @OptIn(FrameworkAPI::class)
    override fun loadSegment(
        segmentId: Double,
        segmentKey: String,
        relativePath: String,
        sha256: String,
        promise: Promise
    ) {
        try {
            ensureExtractDirFreshForCurrentInstall(reactApplicationContext)
            val segId = segmentId.toInt()

            val absolutePath = resolveSegmentPath(relativePath, sha256)
            if (absolutePath == null) {
                promise.reject(
                    "SPLIT_BUNDLE_NOT_FOUND",
                    "Segment file not found: $relativePath (key=$segmentKey)"
                )
                return
            }

            // Per-segment integrity check at load time. Replaces the legacy
            // assumption that install-time validation is sufficient: now
            // BundleUpdate skips full-tree sha256 on startup hot path, so
            // segment integrity is enforced lazily here.
            if (!verifySha256(absolutePath, sha256)) {
                promise.reject(
                    "SPLIT_BUNDLE_SHA256_MISMATCH",
                    "Segment SHA-256 mismatch: $relativePath (key=$segmentKey)"
                )
                return
            }

            val reactContext = reactApplicationContext
            val segStart = System.nanoTime()

            // PRIMARY PATH (fixes the "Requiring unknown module" race):
            // Evaluate the segment OURSELVES on the JS thread via the bridgeless
            // CallInvoker and resolve ONLY after eval completes. We intentionally
            // do NOT use ReactContext.registerSegment here: that resolves its
            // callback immediately (on Task.IMMEDIATE_EXECUTOR) while the actual
            // segment eval is merely ENQUEUED onto the RuntimeScheduler, so the
            // promise resolves BEFORE the segment's __d(...) module definitions
            // run — Metro's import().then(() => __r(moduleId)) can then hit a
            // fatal "Requiring unknown module". nativeEvaluateSegment collapses
            // eval + resolve into one JS-thread block (mirrors the iOS
            // callFunctionOnBufferedRuntimeExecutor: fix).
            val callInvokerHolder =
                reactContext.jsCallInvokerHolder as? CallInvokerHolderImpl

            if (nativeLibLoaded && callInvokerHolder != null) {
                val sourceURL = File(absolutePath).name
                // One-shot guard: native success/error AND the watchdog can each
                // try to settle the promise; only the first wins.
                val settled = AtomicBoolean(false)

                // Bounded watchdog: if the JS thread never drains to our eval,
                // reject instead of hanging the JS promise forever.
                val watchdog = Handler(Looper.getMainLooper())
                val timeoutRunnable = Runnable {
                    if (settled.compareAndSet(false, true)) {
                        SBLLogger.warn("[SplitBundle] segment $segmentKey (id=$segId) eval timed out after ${SEGMENT_EVAL_TIMEOUT_MS}ms")
                        // D: Reject with the RETRYABLE timeout code from the shared
                        // contract (NOT SPLIT_BUNDLE_EVAL_TIMEOUT). A wedged JS
                        // thread is a transient condition; using the retryable
                        // SPLIT_BUNDLE_TIMEOUT lets the JS loader re-attempt rather
                        // than caching this segment as a permanent failure.
                        promise.reject(
                            "SPLIT_BUNDLE_TIMEOUT",
                            "Segment eval timed out: $segmentKey (id=$segId)"
                        )
                    }
                }
                watchdog.postDelayed(timeoutRunnable, SEGMENT_EVAL_TIMEOUT_MS)

                // Catch Throwable, NOT just Exception, around the native call.
                // nativeEvaluateSegment is `external`: if the symbol is
                // registered-but-broken (or the lib half-loaded) the JVM raises
                // UnsatisfiedLinkError, which is a java.lang.Error — it would
                // sail past the outer `catch (e: Exception)` and leave the JS
                // promise unsettled forever (the watchdog would eventually fire,
                // but only after a 30s hang). Settle exactly once here via the
                // same AtomicBoolean one-shot guard and cancel the watchdog.
                try {
                    nativeEvaluateSegment(
                        callInvokerHolder,
                        absolutePath,
                        sourceURL
                    ) { error ->
                        if (settled.compareAndSet(false, true)) {
                            watchdog.removeCallbacks(timeoutRunnable)
                            if (error == null) {
                                val segMs = (System.nanoTime() - segStart) / 1_000_000.0
                                SBLLogger.info("[SplitBundle] segment $segmentKey (id=$segId) evaluated in ${String.format("%.1f", segMs)}ms (eval-complete)")
                                promise.resolve(null)
                            } else {
                                // The native side prefixes I/O failures (file
                                // read/mmap) with "IO_ERROR:" so we can map them to
                                // SPLIT_BUNDLE_IO_ERROR (non-retryable). Everything
                                // else is a segment JS/Hermes eval throw →
                                // SPLIT_BUNDLE_EVAL_ERROR (also non-retryable, a real
                                // bug in the segment's own code).
                                if (error.startsWith("IO_ERROR:")) {
                                    val msg = error.removePrefix("IO_ERROR:")
                                    SBLLogger.warn("[SplitBundle] segment $segmentKey (id=$segId) IO failed: $msg")
                                    promise.reject("SPLIT_BUNDLE_IO_ERROR", msg)
                                } else {
                                    SBLLogger.warn("[SplitBundle] segment $segmentKey (id=$segId) eval failed: $error")
                                    promise.reject("SPLIT_BUNDLE_EVAL_ERROR", error)
                                }
                            }
                        }
                    }
                } catch (t: Throwable) {
                    // UnsatisfiedLinkError / any native dispatch failure. The
                    // callback never fired (the native side never got far enough
                    // to invoke it), so settle the promise ourselves — exactly
                    // once — as a fatal, non-retryable native fault.
                    if (settled.compareAndSet(false, true)) {
                        watchdog.removeCallbacks(timeoutRunnable)
                        SBLLogger.error("[SplitBundle] FATAL: nativeEvaluateSegment threw for $segmentKey (id=$segId): ${t.message}")
                        promise.reject(
                            "SPLIT_BUNDLE_NATIVE_UNAVAILABLE",
                            "Native segment eval threw: $segmentKey (id=$segId): ${t.message}"
                        )
                    }
                }
            } else {
                // B: FAIL CLOSED. On this app (RN 0.81, NewArch / bridgeless),
                // jsCallInvokerHolder is always a CallInvokerHolderImpl and the
                // JNI lib ships in the AAR, so reaching here means a genuine
                // native fault (loadLibrary failed or the holder cast failed) —
                // NOT a benign legacy-bridge mode (this app never runs the old
                // bridge). We deliberately do NOT fall back to the legacy
                // ReactContext.registerSegment path: that resolves before eval
                // and reintroduces the intermittent, uncatchable "Requiring
                // unknown module" native crash. Failing closed (segment load
                // unavailable → JS error boundary) is strictly better than an
                // intermittent crash, so we reject with the dedicated
                // NATIVE_UNAVAILABLE code (non-retryable; the JS loader will not
                // hammer-retry a structurally broken native primitive).
                SBLLogger.error("[SplitBundle] FATAL: native eval primitive unavailable (libLoaded=$nativeLibLoaded, holderCast=${callInvokerHolder != null}) for $segmentKey (id=$segId); failing closed instead of using the race-prone legacy registerSegment fallback")
                promise.reject(
                    "SPLIT_BUNDLE_NATIVE_UNAVAILABLE",
                    "Native segment eval unavailable (libLoaded=$nativeLibLoaded, holderCast=${callInvokerHolder != null}): $segmentKey (id=$segId)"
                )
            }
        } catch (e: Exception) {
            promise.reject("SPLIT_BUNDLE_LOAD_ERROR", e.message, e)
        }
    }

    // -----------------------------------------------------------------------
    // Path resolution helpers
    // -----------------------------------------------------------------------

    /**
     * Verify resolved path stays within the expected root directory (#45).
     * Prevents path traversal via ".." components in relativePath.
     */
    private fun isPathWithinRoot(root: File, resolved: File): Boolean {
        return resolved.canonicalPath.startsWith(root.canonicalPath + File.separator) ||
            resolved.canonicalPath == root.canonicalPath
    }

    private fun resolveSegmentPath(relativePath: String, expectedSha256: String): String? {
        // Path traversal guard (#45)
        if (relativePath.contains("..")) {
            SBLLogger.warn("Path traversal rejected: $relativePath")
            return null
        }

        // 1. Try OTA bundle directory first
        val otaBundlePath = getOtaBundlePath()
        if (!otaBundlePath.isNullOrEmpty()) {
            val otaRoot = File(otaBundlePath).parentFile
            if (otaRoot != null) {
                val candidate = File(otaRoot, relativePath)
                val exists = candidate.exists()
                val withinRoot = isPathWithinRoot(otaRoot, candidate)
                SBLLogger.info("[resolveSeg] rel=$relativePath ota root=${otaRoot.absolutePath} cand=${candidate.absolutePath} exists=$exists withinRoot=$withinRoot")
                if (exists && withinRoot) {
                    return candidate.absolutePath
                }
            } else {
                SBLLogger.info("[resolveSeg] rel=$relativePath otaBundlePath=$otaBundlePath parentFile=null")
            }
        } else {
            SBLLogger.info("[resolveSeg] rel=$relativePath otaBundlePath=(empty) — skipping OTA")
        }

        // 2. Try builtin: extract from assets if needed
        val result = extractBuiltinSegmentIfNeeded(relativePath, expectedSha256)
        if (result == null) {
            SBLLogger.warn("[resolveSeg] rel=$relativePath → null (builtin extract failed)")
        } else {
            SBLLogger.info("[resolveSeg] rel=$relativePath → builtin $result")
        }
        return result
    }

    /**
     * For Android builtin segments, APK assets can't be passed directly as file paths.
     * Extract the asset to the extract cache directory on first access.
     *
     * #16: Validates extracted file size against the asset to detect truncated extractions.
     * #18: Uses semaphore to limit concurrent extractions.
     */
    private fun extractBuiltinSegmentIfNeeded(relativePath: String, expectedSha256: String): String? {
        val context = reactApplicationContext
        ensureExtractDirFreshForCurrentInstall(context)
        val nativeVersion = try {
            context.packageManager
                .getPackageInfo(context.packageName, 0).versionName ?: "unknown"
        } catch (_: Exception) {
            "unknown"
        }

        val extractDir = File(context.filesDir, "$BUILTIN_EXTRACT_DIR/$nativeVersion")
        val extractedFile = File(extractDir, relativePath)

        // #16: If file exists, verify it's not truncated by checking size against asset
        if (extractedFile.exists()) {
            val assetSize = getAssetSize(context.assets, relativePath)
            if (assetSize >= 0 && extractedFile.length() == assetSize) {
                return extractedFile.absolutePath
            }
            // Truncated or size mismatch — delete and re-extract
            SBLLogger.warn("Extracted file size mismatch for $relativePath, re-extracting")
            extractedFile.delete()
        }

        // #18: Limit concurrent extractions
        extractSemaphore.acquire()
        try {
            // Double-check after acquiring semaphore (another thread may have extracted)
            if (extractedFile.exists()) {
                return extractedFile.absolutePath
            }

            val assets: AssetManager = context.assets
            return try {
                // Extract to temp file first, then atomically rename
                val tempFile = File(extractedFile.parentFile, "${extractedFile.name}.tmp")
                assets.open(relativePath).use { input ->
                    extractedFile.parentFile?.let { parent ->
                        if (!parent.exists()) parent.mkdirs()
                    }
                    FileOutputStream(tempFile).use { output ->
                        val buffer = ByteArray(8192)
                        var len: Int
                        while (input.read(buffer).also { len = it } != -1) {
                            output.write(buffer, 0, len)
                        }
                    }
                }
                // Atomic rename prevents partial file observation
                if (tempFile.renameTo(extractedFile)) {
                    SBLLogger.info("[extractBuiltin] extracted $relativePath → ${extractedFile.absolutePath} (${extractedFile.length()} bytes)")
                    extractedFile.absolutePath
                } else {
                    SBLLogger.warn("[extractBuiltin] rename failed for $relativePath: ${tempFile.absolutePath} → ${extractedFile.absolutePath}")
                    tempFile.delete()
                    null
                }
            } catch (e: IOException) {
                SBLLogger.warn("[extractBuiltin] IOException for $relativePath: ${e.javaClass.simpleName}: ${e.message}")
                null
            }
        } finally {
            extractSemaphore.release()
        }
    }

    /**
     * Returns the size of an asset file, or -1 if it can't be determined.
     */
    private fun getAssetSize(assets: AssetManager, assetPath: String): Long {
        return try {
            assets.openFd(assetPath).use { it.length }
        } catch (_: IOException) {
            // Asset may be compressed; fall back to reading the stream
            try {
                assets.open(assetPath).use { input ->
                    var size = 0L
                    val buffer = ByteArray(8192)
                    var len: Int
                    while (input.read(buffer).also { len = it } != -1) {
                        size += len
                    }
                    size
                }
            } catch (_: IOException) {
                -1
            }
        }
    }

    /**
     * #17: Asynchronously clean up extract directories from previous native versions.
     */
    private fun cleanupOldExtractDirs(context: Context, currentVersion: String) {
        Thread {
            try {
                val baseDir = File(context.filesDir, BUILTIN_EXTRACT_DIR)
                if (!baseDir.exists() || !baseDir.isDirectory) return@Thread
                val dirs = baseDir.listFiles() ?: return@Thread
                for (dir in dirs) {
                    if (dir.isDirectory && dir.name != currentVersion) {
                        SBLLogger.info("Cleaning up old extract dir: ${dir.name}")
                        dir.deleteRecursively()
                    }
                }
            } catch (e: Exception) {
                SBLLogger.warn("Failed to cleanup old extract dirs: ${e.message}")
            }
        }.start()
    }

    private fun getOtaBundlePath(): String? {
        return try {
            val bundleUpdateStore = Class.forName(
                "com.margelo.nitro.reactnativebundleupdate.BundleUpdateStoreAndroid"
            )
            val method = bundleUpdateStore.getMethod(
                "getCurrentBundleMainJSBundle",
                Context::class.java
            )
            val result = method.invoke(null, reactApplicationContext)
            result?.toString()
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Reflectively asks bundle-update whether the current bundle's manifest
     * is required to ship per-segment SHA-256s (three-bundle / split-thread
     * format). When this returns true, an empty [expected] in [verifySha256]
     * is treated as a hard fail instead of the back-compat skip.
     *
     * Returns false (back-compat allowed) if the symbol is absent — older
     * bundle-update versions don't expose this and we don't want to wedge
     * the loader on an upgrade race.
     */
    private fun currentBundleRequiresPerSegmentHash(): Boolean {
        return try {
            val bundleUpdateStore = Class.forName(
                "com.margelo.nitro.reactnativebundleupdate.BundleUpdateStoreAndroid"
            )
            val method = bundleUpdateStore.getMethod(
                "currentBundleRequiresPerSegmentHash",
                Context::class.java
            )
            // Kotlin `object` instance methods need INSTANCE; @JvmStatic
            // exposes a static method with the same name. Try the static
            // call first, then fall back to INSTANCE so we work either way
            // if the bundle-update side toggles @JvmStatic in the future.
            val result = try {
                method.invoke(null, reactApplicationContext)
            } catch (_: NullPointerException) {
                val instance = bundleUpdateStore.getField("INSTANCE").get(null)
                method.invoke(instance, reactApplicationContext)
            } catch (_: IllegalArgumentException) {
                val instance = bundleUpdateStore.getField("INSTANCE").get(null)
                method.invoke(instance, reactApplicationContext)
            }
            (result as? Boolean) ?: false
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Streams [path] and compares its SHA-256 against [expected] (hex,
     * case-insensitive). When [expected] is empty, defers to bundle-update:
     * three-bundle format manifests must ship per-segment hashes (fail
     * closed); older formats are allowed to omit them (back-compat).
     */
    private fun verifySha256(path: String, expected: String): Boolean {
        if (expected.isEmpty()) {
            return !currentBundleRequiresPerSegmentHash()
        }
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            FileInputStream(path).use { input ->
                val buf = ByteArray(64 * 1024)
                while (true) {
                    val n = input.read(buf)
                    if (n <= 0) break
                    md.update(buf, 0, n)
                }
            }
            val actual = md.digest().joinToString("") { "%02x".format(it) }
            actual.equals(expected, ignoreCase = true)
        } catch (e: Exception) {
            SBLLogger.warn("verifySha256 failed for $path: ${e.message}")
            false
        }
    }
}
