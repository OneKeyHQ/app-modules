package com.splitbundleloader

import android.content.Context
import android.content.res.AssetManager
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.Semaphore

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

    companion object {
        const val NAME = "SplitBundleLoader"
        private const val BUILTIN_EXTRACT_DIR = "onekey-builtin-segments"
        // #18: Limit concurrent asset extractions to avoid I/O contention
        private const val MAX_CONCURRENT_EXTRACTS = 2
        private val extractSemaphore = Semaphore(MAX_CONCURRENT_EXTRACTS)
    }

    override fun getName(): String = NAME

    // -----------------------------------------------------------------------
    // getRuntimeBundleContext
    // -----------------------------------------------------------------------

    override fun getRuntimeBundleContext(promise: Promise) {
        try {
            val context = reactApplicationContext
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
            val absolutePath = resolveSegmentPath(relativePath, sha256)
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

    override fun loadSegment(
        segmentId: Double,
        segmentKey: String,
        relativePath: String,
        sha256: String,
        promise: Promise
    ) {
        // NOTE (#44): sha256 param is not verified at load time by design.
        // Per §6.4.1, runtime trusts that OTA install has already verified
        // segment integrity. Builtin segments are signed as part of the APK/IPA.
        // If runtime SHA-256 verification is needed, add it here.
        try {
            val segId = segmentId.toInt()

            val absolutePath = resolveSegmentPath(relativePath, sha256)
            if (absolutePath == null) {
                promise.reject(
                    "SPLIT_BUNDLE_NOT_FOUND",
                    "Segment file not found: $relativePath (key=$segmentKey)"
                )
                return
            }

            // #19: Try CatalystInstance first (bridge mode), fall back to
            // ReactHost registerSegment if available (bridgeless / new arch).
            val reactContext = reactApplicationContext
            val segStart = System.nanoTime()
            if (reactContext.hasCatalystInstance()) {
                reactContext.catalystInstance.registerSegment(segId, absolutePath)
                val segMs = (System.nanoTime() - segStart) / 1_000_000.0
                SBLLogger.info("[SplitBundle] segment $segmentKey (id=$segId) registered in ${String.format("%.1f", segMs)}ms")
                promise.resolve(null)
            } else {
                // Bridgeless: try ReactHost via reflection
                val registered = tryRegisterViaBridgeless(segId, absolutePath)
                val segMs = (System.nanoTime() - segStart) / 1_000_000.0
                if (registered) {
                    SBLLogger.info("[SplitBundle] segment $segmentKey (id=$segId) registered via bridgeless in ${String.format("%.1f", segMs)}ms")
                    promise.resolve(null)
                } else {
                    promise.reject(
                        "SPLIT_BUNDLE_NO_INSTANCE",
                        "Neither CatalystInstance nor ReactHost available"
                    )
                }
            }
        } catch (e: Exception) {
            promise.reject("SPLIT_BUNDLE_LOAD_ERROR", e.message, e)
        }
    }

    // -----------------------------------------------------------------------
    // Bridgeless support (#19)
    // -----------------------------------------------------------------------

    private fun tryRegisterViaBridgeless(segmentId: Int, path: String): Boolean {
        return try {
            val appContext = reactApplicationContext.applicationContext
            val appClass = appContext.javaClass
            val hostMethod = appClass.getMethod("getReactHost")
            val host = hostMethod.invoke(appContext) ?: return false
            val registerMethod = host.javaClass.getMethod(
                "registerSegment", Int::class.java, String::class.java
            )
            registerMethod.invoke(host, segmentId, path)
            true
        } catch (_: Exception) {
            false
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
                if (candidate.exists() && isPathWithinRoot(otaRoot, candidate)) {
                    return candidate.absolutePath
                }
            }
        }

        // 2. Try builtin: extract from assets if needed
        return extractBuiltinSegmentIfNeeded(relativePath, expectedSha256)
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
                    extractedFile.absolutePath
                } else {
                    tempFile.delete()
                    null
                }
            } catch (_: IOException) {
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
}
