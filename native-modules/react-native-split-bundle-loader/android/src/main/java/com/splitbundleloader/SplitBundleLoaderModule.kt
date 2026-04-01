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
        } catch (e: Exception) {
            promise.reject("SPLIT_BUNDLE_CONTEXT_ERROR", e.message, e)
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
        try {
            val segId = segmentId.toInt()

            val absolutePath = resolveSegmentPath(relativePath)
            if (absolutePath == null) {
                promise.reject(
                    "SPLIT_BUNDLE_NOT_FOUND",
                    "Segment file not found: $relativePath (key=$segmentKey)"
                )
                return
            }

            // Register segment via CatalystInstance
            val reactContext = reactApplicationContext
            if (reactContext.hasCatalystInstance()) {
                reactContext.catalystInstance.registerSegment(segId, absolutePath)
                SBLLogger.info("Loaded segment $segmentKey (id=$segId)")
                promise.resolve(null)
            } else {
                promise.reject(
                    "SPLIT_BUNDLE_NO_INSTANCE",
                    "CatalystInstance not available"
                )
            }
        } catch (e: Exception) {
            promise.reject("SPLIT_BUNDLE_LOAD_ERROR", e.message, e)
        }
    }

    // -----------------------------------------------------------------------
    // Path resolution helpers
    // -----------------------------------------------------------------------

    private fun resolveSegmentPath(relativePath: String): String? {
        // 1. Try OTA bundle directory first
        val otaBundlePath = getOtaBundlePath()
        if (!otaBundlePath.isNullOrEmpty()) {
            val otaRoot = File(otaBundlePath).parentFile
            if (otaRoot != null) {
                val candidate = File(otaRoot, relativePath)
                if (candidate.exists()) {
                    return candidate.absolutePath
                }
            }
        }

        // 2. Try builtin: extract from assets if needed
        return extractBuiltinSegmentIfNeeded(relativePath)
    }

    /**
     * For Android builtin segments, APK assets can't be passed directly as file paths.
     * Extract the asset to the extract cache directory on first access.
     */
    private fun extractBuiltinSegmentIfNeeded(relativePath: String): String? {
        val context = reactApplicationContext
        val nativeVersion = try {
            context.packageManager
                .getPackageInfo(context.packageName, 0).versionName ?: "unknown"
        } catch (_: Exception) {
            "unknown"
        }

        val extractDir = File(context.filesDir, "$BUILTIN_EXTRACT_DIR/$nativeVersion")
        val extractedFile = File(extractDir, relativePath)

        // Already extracted
        if (extractedFile.exists()) {
            return extractedFile.absolutePath
        }

        // Extract from assets
        val assets: AssetManager = context.assets
        return try {
            assets.open(relativePath).use { input ->
                extractedFile.parentFile?.let { parent ->
                    if (!parent.exists()) parent.mkdirs()
                }
                FileOutputStream(extractedFile).use { output ->
                    val buffer = ByteArray(8192)
                    var len: Int
                    while (input.read(buffer).also { len = it } != -1) {
                        output.write(buffer, 0, len)
                    }
                }
            }
            extractedFile.absolutePath
        } catch (_: IOException) {
            null
        }
    }

    private fun getOtaBundlePath(): String? {
        return try {
            val bundleUpdateStore = Class.forName(
                "expo.modules.onekeybundleupdate.BundleUpdateStore"
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
