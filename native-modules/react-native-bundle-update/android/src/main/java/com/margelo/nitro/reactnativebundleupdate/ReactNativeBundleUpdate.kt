package com.margelo.nitro.reactnativebundleupdate

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise
import com.margelo.nitro.NitroModules
import com.margelo.nitro.nativelogger.OneKeyLog
import com.margelo.nitro.reactnativebundlecrypto.verifyBundleDirectory
import com.margelo.nitro.reactnativebundlecrypto.AndroidBundleFileSystem
import com.margelo.nitro.reactnativebundlecrypto.BundleCryptoCore
import com.margelo.nitro.reactnativebundlecrypto.BundleFileSystemException
import com.margelo.nitro.reactnativebundlecrypto.BundleFileType
import com.margelo.nitro.reactnativerangedownloader.ConcurrentRangeDownloader
import com.tencent.mmkv.MMKV
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.http2.StreamResetException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import org.json.JSONArray
import org.json.JSONObject

private data class BundleListener(
    val id: Double,
    val callback: (BundleDownloadEvent) -> Unit
)

private class BundleUpdateError(
    val code: String,
    val step: String,
    val httpStatus: Int? = null,
) : Exception(buildString {
    append("ONEKEY_UPDATE_ERROR:")
    append(JSONObject().put("code", code).put("step", step).apply {
        if (code == "HTTP_ERROR" && httpStatus != null && httpStatus in 100..599) {
            put("httpStatus", httpStatus)
        }
    }.toString())
}) {
    override fun printStackTrace(writer: java.io.PrintWriter) { writer.print(message.orEmpty()) }
    override fun printStackTrace(stream: java.io.PrintStream) { stream.print(message.orEmpty()) }
}

private fun bundleUpdateError(error: Exception, step: String): BundleUpdateError {
    if (error is BundleUpdateError) {
        return if (error.step == step) error else BundleUpdateError(error.code, step, error.httpStatus)
    }
    val code = when {
        error is BundleUpdateOperations.AdmissionException ->
            if (error.reason == BundleUpdateOperations.Refusal.CANCELLED) "CANCELLED" else "INVALID_ARGUMENT"
        error is ConcurrentRangeDownloader.PermanentHttpException ||
            error is ConcurrentRangeDownloader.TransientHttpException -> "HTTP_ERROR"
        error is java.net.SocketTimeoutException ||
            error is java.io.InterruptedIOException -> "TIMEOUT"
        error is javax.net.ssl.SSLException -> "SECURITY_REJECTED"
        error is java.io.FileNotFoundException ||
            error is BundleFileSystemException -> "STORAGE_ERROR"
        error is StreamResetException || BundleDiffReconstruction.isRetryableNetworkIOException(error) -> "NETWORK_ERROR"
        error is IllegalArgumentException -> "INVALID_ARGUMENT"
        error is SecurityException -> "SECURITY_REJECTED"
        else -> "UNKNOWN"
    }
    val status = when (error) {
        is ConcurrentRangeDownloader.PermanentHttpException -> error.code
        is ConcurrentRangeDownloader.TransientHttpException -> error.code
        else -> null
    }
    return BundleUpdateError(code, step, status)
}

// Public static store for CustomReactNativeHost access (called before JS starts)
object BundleUpdateStoreAndroid {
    internal val activationLock = Any()

    internal fun isInstalled(context: Context, appVersion: String, bundleVersion: String): Boolean =
        getCurrentBundleVersion(context) == "$appVersion-$bundleVersion" ||
            readFallbackUpdateBundleDataFile(context).any {
                it["appVersion"] == appVersion && it["bundleVersion"] == bundleVersion
            }

    private const val PREFS_NAME = "BundleUpdatePrefs"
    internal const val NATIVE_VERSION_PREFS_NAME = "NativeVersionPrefs"
    private const val CURRENT_BUNDLE_VERSION_KEY = "currentBundleVersion"
    private const val MAIN_JS_BUNDLE_FILE_NAME = "main.jsbundle.hbc"
    private const val BACKGROUND_BUNDLE_FILE_NAME = "background.bundle"
    private const val COMMON_BUNDLE_FILE_NAME = "common.bundle"
    private const val METADATA_REQUIRES_BACKGROUND_BUNDLE_KEY = "requiresBackgroundBundle"
    private const val METADATA_BACKGROUND_PROTOCOL_VERSION_KEY = "backgroundProtocolVersion"
    private const val METADATA_REQUIRES_COMMON_BUNDLE_KEY = "requiresCommonBundle"
    private const val METADATA_BUNDLE_FORMAT_KEY = "bundleFormat"
    private const val BUNDLE_FORMAT_THREE_BUNDLE = "three-bundle"
    private const val SUPPORTED_BACKGROUND_PROTOCOL_VERSION = "2"

    // In-memory cache for getValidatedCurrentBundleInfo. Without this, every
    // bundleURL / common / main / background path getter on startup re-runs
    // the whole validation pipeline. Cache key is currentBundleVersion;
    // invalidated on any mutation of the current bundle.
    @Volatile private var cachedValidatedBundleInfo: ValidatedBundleInfo? = null

    // Lazy per-version cache for the web-embed subtree. getWebEmbedPath is
    // called every time a WebView is created, but the subtree contents are
    // immutable for a given currentBundleVersion. Stores the version that
    // most recently passed full sha256 sweep over web-embed/**; invalidated
    // alongside cachedValidatedBundleInfo on any bundle mutation.
    @Volatile private var cachedWebEmbedVerifiedVersion: String? = null

    @Synchronized
    fun invalidateValidatedBundleInfoCache() {
        cachedValidatedBundleInfo = null
        cachedWebEmbedVerifiedVersion = null
    }

    /**
     * True when the current bundle's metadata declares the three-bundle /
     * split-thread layout (or explicitly opts in via requiresCommonBundle).
     * SplitBundleLoader queries this reflectively to decide whether an empty
     * per-segment sha256 is a back-compat skip (older formats) or a hard
     * fail (three-bundle, which always ships per-segment hashes).
     */
    @JvmStatic
    fun currentBundleRequiresPerSegmentHash(context: Context): Boolean {
        val info = getValidatedCurrentBundleInfo(context) ?: return false
        return metadataRequiresCommonBundle(info.metadata)
    }

    fun getDownloadBundleDir(context: Context): String {
        val dir = File(context.filesDir, "onekey-bundle-download")
        if (!dir.exists()) dir.mkdirs()
        return dir.absolutePath
    }

    fun getBundleDir(context: Context): String {
        val dir = File(context.filesDir, "onekey-bundle")
        if (!dir.exists()) dir.mkdirs()
        return dir.absolutePath
    }

    fun getAscDir(context: Context): String {
        val dir = File(getBundleDir(context), "asc")
        if (!dir.exists()) dir.mkdirs()
        return dir.absolutePath
    }

    fun getSignatureFilePath(context: Context, version: String): String {
        return File(getAscDir(context), "$version-signature.asc").absolutePath
    }

    fun writeSignatureFile(context: Context, version: String, signature: String) {
        val file = File(getSignatureFilePath(context, version))
        val existed = file.exists()
        file.parentFile?.mkdirs()
        file.writeText(signature, Charsets.UTF_8)
        OneKeyLog.info("BundleUpdate", "writeSignatureFile: version=$version, existed=$existed, size=${file.length()}, path=${file.absolutePath}")
    }

    fun readSignatureFile(context: Context, version: String): String {
        val file = File(getSignatureFilePath(context, version))
        if (!file.exists()) {
            OneKeyLog.debug("BundleUpdate", "readSignatureFile: not found for version=$version")
            return ""
        }
        return try {
            val content = file.readText(Charsets.UTF_8)
            OneKeyLog.debug("BundleUpdate", "readSignatureFile: version=$version, size=${content.length}")
            content
        } catch (e: Exception) {
            OneKeyLog.error("BundleUpdate", "readSignatureFile: failed to read $version: ${e.message}")
            ""
        }
    }

    fun deleteSignatureFile(context: Context, version: String) {
        val file = File(getSignatureFilePath(context, version))
        if (file.exists()) file.delete()
    }

    fun getCurrentBundleVersion(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(CURRENT_BUNDLE_VERSION_KEY, null)
    }

    fun setCurrentBundleVersionAndSignature(context: Context, version: String, signature: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentVersion = prefs.getString(CURRENT_BUNDLE_VERSION_KEY, "")

        // Write the signature file BEFORE flipping the current-version pointer,
        // and commit the pref synchronously (commit() not apply()). This matches
        // the atomic intent of the commit() pre-launch path and guarantees that
        // a crash can never leave the version pointing at a bundle whose
        // signature is missing — which would make a perfectly valid bundle fail
        // verification and get discarded on next launch. Either the pointer flip
        // hasn't happened (old bundle stays active, new bundle untouched on disk)
        // or both signature + version are durably persisted together.
        if (!signature.isNullOrEmpty()) {
            writeSignatureFile(context, version, signature)
        }

        val editor = prefs.edit()
        editor.putString(CURRENT_BUNDLE_VERSION_KEY, version)
        // Remove old signature key from prefs (legacy cleanup)
        if (!currentVersion.isNullOrEmpty()) {
            editor.remove(currentVersion)
        }
        editor.commit()
    }

    fun clearUpdateBundleData(context: Context) {
        if (!didProcessPreLaunchTask) {
            synchronized(activationLock) { didProcessPreLaunchTask = true }
        }
        invalidateValidatedBundleInfoCache()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        // Clear all signature files
        val ascDir = File(getAscDir(context))
        if (ascDir.exists()) {
            ascDir.listFiles()?.forEach { it.delete() }
        }
    }

    fun getCurrentBundleDir(context: Context, currentBundleVersion: String?): String? {
        if (currentBundleVersion == null) return null
        return File(getBundleDir(context), currentBundleVersion).absolutePath
    }

    fun getAppVersion(context: Context): String? {
        return try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(context.packageName, 0)
            pi.versionName
        } catch (e: Exception) {
            OneKeyLog.error("BundleUpdate", "Error getting package info: ${e.message}")
            null
        }
    }

    fun getNativeVersion(context: Context): String {
        val prefs = context.getSharedPreferences(NATIVE_VERSION_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("nativeVersion", "") ?: ""
    }

    fun setNativeVersion(context: Context, nativeVersion: String) {
        val prefs = context.getSharedPreferences(NATIVE_VERSION_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("nativeVersion", nativeVersion).apply()
    }

    fun getBuildNumber(context: Context): String {
        return try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(context.packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                pi.longVersionCode.toString()
            } else {
                @Suppress("DEPRECATION")
                pi.versionCode.toString()
            }
        } catch (e: Exception) {
            OneKeyLog.error("BundleUpdate", "Error getting build number: ${e.message}")
            ""
        }
    }

    fun getNativeBuildNumber(context: Context): String {
        val prefs = context.getSharedPreferences(NATIVE_VERSION_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("nativeBuildNumber", "") ?: ""
    }

    fun setNativeBuildNumber(context: Context, buildNumber: String) {
        val prefs = context.getSharedPreferences(NATIVE_VERSION_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("nativeBuildNumber", buildNumber).apply()
    }

    fun clearNativeVersionPrefs(context: Context) {
        val prefs = context.getSharedPreferences(NATIVE_VERSION_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove("nativeVersion").remove("nativeBuildNumber").apply()
    }

    fun getBuiltinBundleVersion(context: Context): String {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, android.content.pm.PackageManager.GET_META_DATA)
            appInfo.metaData?.get("BUNDLE_VERSION")?.toString() ?: ""
        } catch (e: Exception) {
            OneKeyLog.error("BundleUpdate", "Error getting builtin bundle version: ${e.message}")
            ""
        }
    }

    // Pre-launch pending task processing

    @Volatile
    private var didProcessPreLaunchTask = false

    /**
     * Checks MMKV for a pending bundle-switch task and applies it before JS runtime starts.
     * Only handles the happy path (status=pending, bundle exists, env matches).
     * All complex logic (retry, download, error handling) remains in JS layer.
     */
    private fun failPendingTask(mmkv: MMKV, taskObj: JSONObject, reason: String) {
        try {
            taskObj.put("status", "failed")
            taskObj.put("lastError", reason)
            mmkv.encode("onekey_pending_install_task", taskObj.toString())
        } catch (e: Exception) {
            OneKeyLog.warn("BundleUpdate", "processPreLaunchPendingTask: failed to update MMKV task failure: ${e.message}")
        }
    }

    fun processPreLaunchPendingTask(context: Context) {
        if (didProcessPreLaunchTask) return
        synchronized(activationLock) {
            if (didProcessPreLaunchTask) return
            applyPreLaunchPendingTask(context)
            didProcessPreLaunchTask = true
        }
    }

    private fun applyPreLaunchPendingTask(context: Context) {
        try {
            // 1. Read MMKV
            MMKV.initialize(context)
            val mmkv = MMKV.mmkvWithID("onekey-app-setting") ?: return
            val taskJson = mmkv.decodeString("onekey_pending_install_task") ?: return
            val taskObj = JSONObject(taskJson)

            // 2. Validate task fields
            if (taskObj.optString("status") != "pending") return
            if (taskObj.optString("action") != "switch-bundle") return
            if (taskObj.optString("type") != "jsbundle-switch") return

            val now = System.currentTimeMillis()
            if (taskObj.optLong("expiresAt", 0) <= now) return
            val nextRetryAt = taskObj.optLong("nextRetryAt", 0)
            if (nextRetryAt > 0 && nextRetryAt > now) return

            // 3. Verify scheduledEnv matches current state (including buildNumber)
            val currentAppVersion = getAppVersion(context) ?: return
            if (taskObj.optString("scheduledEnvAppVersion") != currentAppVersion) return

            val scheduledBuildNumber = taskObj.optString("scheduledEnvBuildNumber", "")
            val currentBuildNumber = getBuildNumber(context)
            if (scheduledBuildNumber.isNotEmpty() && currentBuildNumber.isNotEmpty() && scheduledBuildNumber != currentBuildNumber) {
                OneKeyLog.info("BundleUpdate", "processPreLaunchPendingTask: buildNumber changed from $scheduledBuildNumber to $currentBuildNumber, skipping stale task")
                return
            }

            val currentBV = getCurrentBundleVersion(context)
            val currentBundleVersionStr = if (currentBV != null) {
                bundleVersionInFolder(currentBV, currentAppVersion) ?: return
            } else getBuiltinBundleVersion(context)
            if (taskObj.optString("scheduledEnvBundleVersion") != currentBundleVersionStr) return

            // 4. Extract payload
            val payload = taskObj.optJSONObject("payload") ?: return
            val appVersion = payload.optString("appVersion")
            val bundleVersion = payload.optString("bundleVersion")
            val signature = payload.optString("signature")
            if (appVersion.isEmpty() || bundleVersion.isEmpty() || signature.isEmpty()) return
            if (!isSafeVersionString(appVersion) || !isSafeVersionString(bundleVersion)) {
                failPendingTask(mmkv, taskObj, "ERR_VERIFY_FAILED:Invalid version string")
                return
            }
            if (appVersion != currentAppVersion) {
                OneKeyLog.warn("BundleUpdate", "processPreLaunchPendingTask: target appVersion ($appVersion) != currentAppVersion ($currentAppVersion)")
                failPendingTask(mmkv, taskObj, "ERR_ENV_CHANGED:Target appVersion mismatch")
                return
            }

            // 5. Verify bundle directory, canonical path confinement, and required files
            val folderName = "$appVersion-$bundleVersion"
            val baseBundleDir = File(getBundleDir(context)).canonicalPath
            val bundleDirPath = File(getBundleDir(context), folderName)
            if (AndroidBundleFileSystem.isSymbolicLink(bundleDirPath) ||
                !bundleDirPath.canonicalPath.startsWith(baseBundleDir + File.separator)
            ) {
                OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask: bundle path outside bundle directory: ${bundleDirPath.canonicalPath}")
                failPendingTask(mmkv, taskObj, "ERR_VERIFY_FAILED:Bundle path outside bundle directory")
                return
            }
            val sigFile = File(getSignatureFilePath(context, folderName))
            val baseAscDir = File(getAscDir(context)).canonicalPath
            if (AndroidBundleFileSystem.isSymbolicLink(sigFile) ||
                !sigFile.canonicalPath.startsWith(baseAscDir + File.separator)
            ) {
                OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask: signature path outside asc directory: ${sigFile.canonicalPath}")
                failPendingTask(mmkv, taskObj, "ERR_VERIFY_FAILED:Signature path outside asc directory")
                return
            }
            if (!bundleDirPath.exists()) return
            val entryFile = File(bundleDirPath, "main.jsbundle.hbc")
            val metadataFile = File(bundleDirPath, "metadata.json")
            if (!entryFile.exists() || !metadataFile.exists()) {
                OneKeyLog.warn("BundleUpdate", "processPreLaunchPendingTask: bundle dir exists but required files missing: ${bundleDirPath.absolutePath}")
                failPendingTask(mmkv, taskObj, "ERR_VERIFY_FAILED:Required bundle files missing")
                return
            }

            val skipGPG = BuildConfig.ALLOW_SKIP_GPG_VERIFICATION && isDevSettingsEnabled(context) && isSkipGPGEnabled(context)
            if (!skipGPG && !validateMetadataFileSha256(context, folderName, signature, appVersion, bundleVersion)) {
                OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask: GPG signature verification failed for $folderName")
                failPendingTask(mmkv, taskObj, "ERR_VERIFY_FAILED:Bundle signature verification failed")
                return
            }

            val metadata = parseMetadataJson(readFileContent(metadataFile))
            val verified = try {
                validateAllFilesInDir(context, bundleDirPath.absolutePath, metadata, appVersion, bundleVersion)
            } catch (e: BundleFileSystemException) {
                OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask: unable to inspect extracted files for $folderName: ${e.message}")
                failPendingTask(mmkv, taskObj, "ERR_VERIFY_FAILED:Unable to inspect extracted bundle files")
                return
            }
            if (!verified) {
                OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask: extracted file verification failed for $folderName")
                failPendingTask(mmkv, taskObj, "ERR_VERIFY_FAILED:Extracted file verification failed")
                return
            }
            if (!validateBundlePairCompatibility(context, bundleDirPath.absolutePath, metadata)) {
                OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask: bundle pair compatibility check failed for $folderName")
                failPendingTask(mmkv, taskObj, "ERR_VERIFY_FAILED:Bundle pair compatibility check failed")
                return
            }

            // 6. Apply (same as installBundle) — write signature BEFORE flipping
            //    currentBundleVersion, and use commit() for synchronous writes.
            writeSignatureFile(context, folderName, signature)
            val bundlePrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val versionPrefs = context.getSharedPreferences(NATIVE_VERSION_PREFS_NAME, Context.MODE_PRIVATE)
            val currentVersion = bundlePrefs.getString(CURRENT_BUNDLE_VERSION_KEY, "")
            bundlePrefs.edit()
                .putString(CURRENT_BUNDLE_VERSION_KEY, folderName)
                .apply {
                    if (!currentVersion.isNullOrEmpty()) {
                        remove(currentVersion) // legacy signature key cleanup
                    }
                }
                .commit()
            versionPrefs.edit()
                .putString("nativeVersion", currentAppVersion)
                .putString("nativeBuildNumber", getBuildNumber(context))
                .commit()

            // 7. Update MMKV task status → applied_waiting_verify
            // Do NOT set runningStartedAt — a falsy value lets JS skip the
            // 10-minute grace period and verify alignment immediately on boot.
            taskObj.put("status", "applied_waiting_verify")
            taskObj.remove("runningStartedAt")
            mmkv.encode("onekey_pending_install_task", taskObj.toString())

            OneKeyLog.info("BundleUpdate", "processPreLaunchPendingTask: switched to $folderName")
        } catch (e: Exception) {
            OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask failed: ${e.message}")
        }
    }

    private val lastSHA256Failure = ThreadLocal<String?>()

    /**
     * Subtype of the most recent calculateSHA256 failure on this thread, or
     * null if the last call succeeded. Surfaces the specific reason —
     * FILE_NOT_FOUND / FILE_DISAPPEARED / FILE_TRUNCATED /
     * PERMISSION_DENIED / OOM / IO_<class> / UNEXPECTED_<class> — so
     * analytics can split the previously opaque "Failed to calculate
     * SHA256" bucket (mixpanel: 91.3 percent of verifyPackage failures)
     * into actionable categories. Keep this list in sync with the
     * lastSHA256Failure.set(...) call sites in calculateSHA256 below.
     *
     * Note: 0-byte files are NOT treated as a failure. They hash to the
     * well-known empty-content SHA-256 and the caller's expected/actual
     * comparison handles legitimate vs. corrupt-empty cases. Rejecting
     * empty files here would make any OTA bundle that legitimately
     * contains a 0-byte file (touched marker, blank locale fallback)
     * fail validateAllFilesInDir / validateWebEmbedSha256 / launch entry
     * verification — all of which share this calculator.
     */
    fun lastSHA256FailureReason(): String? = lastSHA256Failure.get()

    // Thin delegate over the shared BundleCryptoCore.sha256OfFile. The actual
    // streaming MessageDigest implementation and its failureReason taxonomy
    // (FILE_NOT_FOUND / FILE_DISAPPEARED / FILE_TRUNCATED / PERMISSION_DENIED /
    // OOM / IO_<class> / UNEXPECTED_<class>) now live in react-native-bundle-crypto.
    // We re-publish that reason into the existing per-thread lastSHA256Failure
    // ThreadLocal so lastSHA256FailureReason() keeps working unchanged for all
    // existing analytics call sites.
    fun calculateSHA256(filePath: String): String? {
        lastSHA256Failure.set(null)
        val result = BundleCryptoCore.sha256OfFile(filePath)
        if (result.sha256 == null) {
            lastSHA256Failure.set(result.failureReason)
            return null
        }
        return result.sha256
    }

    fun getMetadataFilePath(context: Context, currentBundleVersion: String?): String? {
        if (currentBundleVersion == null) return null
        val file = File(File(getBundleDir(context), currentBundleVersion), "metadata.json")
        return if (file.exists()) file.absolutePath else null
    }

    fun getMetadataFileContent(context: Context, currentBundleVersion: String?): String? {
        val path = getMetadataFilePath(context, currentBundleVersion) ?: return null
        return readFileContent(File(path))
    }

    fun parseMetadataJson(jsonContent: String): Map<String, String> {
        val metadata = mutableMapOf<String, String>()
        try {
            val obj = JSONObject(jsonContent)
            val keys = obj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                // Only accept string-valued entries. Any nested object / array
                // / numeric value is silently skipped so the metadata can carry
                // non-string scalars (e.g. runtimeGraphVersion) or future V2
                // descriptors without breaking file-level SHA verification.
                val value = obj.opt(key)
                if (value is String) {
                    metadata[key] = value
                } else if (value is Boolean || value is Number) {
                    metadata[key] = value.toString()
                }
            }
        } catch (e: Exception) {
            // org.json's exception messages occasionally embed file paths or
            // partial JSON content. Keep the rich detail in OneKeyLog (local
            // only), but throw a class-tag-only message so the JS analytics
            // layer cannot reflect arbitrary inner content. Mirrors the
            // SHA256_<reason>/IO_<class> convention used elsewhere.
            OneKeyLog.error("BundleUpdate", "Error parsing metadata JSON: ${e.javaClass.simpleName}: ${e.message}")
            throw Exception("Failed to parse metadata.json: IO_${e.javaClass.simpleName}")
        }
        if (metadata.isEmpty()) {
            throw Exception("metadata.json is empty or contains no file entries")
        }
        return metadata
    }

    private fun isReservedMetadataKey(key: String): Boolean {
        return key == METADATA_REQUIRES_BACKGROUND_BUNDLE_KEY ||
            key == METADATA_BACKGROUND_PROTOCOL_VERSION_KEY ||
            key == METADATA_REQUIRES_COMMON_BUNDLE_KEY ||
            key == METADATA_BUNDLE_FORMAT_KEY ||
            key == BundleStorageSchemaPolicy.METADATA_KEY
    }

    internal fun getFileMetadataEntries(metadata: Map<String, String>): Map<String, String> {
        return metadata.filterKeys { key -> !isReservedMetadataKey(key) }
    }

    private fun metadataRequiresBackgroundBundle(metadata: Map<String, String>): Boolean {
        return metadata[METADATA_REQUIRES_BACKGROUND_BUNDLE_KEY]
            ?.lowercase()
            ?.let { value -> value == "1" || value == "true" || value == "yes" }
            ?: false
    }

    private fun metadataRequiresCommonBundle(metadata: Map<String, String>): Boolean {
        // OR semantics matching iOS: an explicit `requiresCommonBundle=false`
        // does NOT suppress the `bundleFormat=three-bundle` signal. Both
        // signals are treated as opt-ins; either one being true is enough.
        val explicit = metadata[METADATA_REQUIRES_COMMON_BUNDLE_KEY]
            ?.lowercase()
            ?.let { value -> value == "1" || value == "true" || value == "yes" }
            ?: false
        if (explicit) return true
        return metadata[METADATA_BUNDLE_FORMAT_KEY]?.lowercase() == BUNDLE_FORMAT_THREE_BUNDLE
    }

    private fun metadataBackgroundProtocolVersion(metadata: Map<String, String>): String {
        return metadata[METADATA_BACKGROUND_PROTOCOL_VERSION_KEY] ?: ""
    }

    private fun validateStorageSchemaCompatibility(context: Context, metadata: Map<String, String>): Boolean {
        val compatible = BundleStorageSchemaPolicy.isCompatible(metadata[BundleStorageSchemaPolicy.METADATA_KEY]) { key ->
            context.getSharedPreferences(BundleStorageSchemaPolicy.LEDGER_PREFS_NAME, Context.MODE_PRIVATE)
                .getString(key, null)
        }
        if (!compatible) {
            OneKeyLog.error("BundleUpdate", "Bundle storage schema is incompatible with the native migration state")
        }
        return compatible
    }

    fun bundleVersionInFolder(folder: String, appVersion: String): String? {
        if (!isSafeVersionString(appVersion)) return null
        val prefix = "$appVersion-"
        if (!folder.startsWith(prefix)) return null
        val bundleVersion = folder.removePrefix(prefix)
        return bundleVersion.takeIf { isSafeVersionString(it) }
    }

    @JvmOverloads
    fun readMetadataFileSha256(
        signature: String?,
        expectedAppVersion: String? = null,
        expectedBundleVersion: String? = null,
        expectedAppType: String? = "android",
        expectedFileName: String = "metadata.json",
    ): String? {
        if (signature.isNullOrEmpty()) return null

        // GPG cleartext signature verification is required
        val gpgResult = verifyGPGAndExtractSha256(
            signature,
            expectedAppVersion = expectedAppVersion,
            expectedBundleVersion = expectedBundleVersion,
            expectedAppType = expectedAppType,
            expectedFileName = expectedFileName,
        )
        if (gpgResult != null) return gpgResult

        OneKeyLog.error("BundleUpdate", "readMetadataFileSha256: GPG verification failed, rejecting unsigned content")
        return null
    }

    /**
     * Constant-time comparison to prevent timing attacks on hash values.
     * Delegates to the shared BundleCryptoCore.secureEqualHex (same byte-wise
     * constant-time algorithm).
     */
    fun secureCompare(a: String, b: String): Boolean {
        return BundleCryptoCore.secureEqualHex(a, b)
    }

    fun isSafeVersionString(version: String): Boolean {
        return BundleDiffReconstruction.isSafeVersionComponent(version)
    }

    /**
     * Verify a PGP cleartext-signed message and extract the sha256.
     * Returns null if verification fails or the signature is not a PGP cleartext
     * message.
     *
     * Delegates to the shared BundleCryptoCore.verifyGpgCleartext (the
     * BouncyCastle verify logic + the OneKey GPG public key now live there as
     * the single source of truth). Behavior is preserved: any non-valid result
     * (NOT_PGP_SIGNED_MESSAGE / INVALID_FORMAT / NO_SIGNATURE / PUBKEY_NOT_FOUND
     * / SIGNATURE_INVALID / NO_SHA256_FIELD / ERROR_*) maps to null.
     */
    @JvmOverloads
    fun verifyGPGAndExtractSha256(
        signature: String,
        expectedAppVersion: String? = null,
        expectedBundleVersion: String? = null,
        expectedAppType: String? = "android",
        expectedFileName: String = "metadata.json",
    ): String? {
        val result = BundleCryptoCore.verifyGpgCleartext(
            signature,
            expectedAppVersion = expectedAppVersion,
            expectedBundleVersion = expectedBundleVersion,
            expectedAppType = expectedAppType,
            expectedFileName = expectedFileName,
        )
        if (!result.valid) {
            OneKeyLog.error("BundleUpdate", "GPG verification failed: ${result.reason}")
            return null
        }
        return result.sha256
    }

    fun validateMetadataFileSha256(context: Context, currentBundleVersion: String, signature: String?, expectedAppVersion: String, expectedBundleVersion: String): Boolean {
        val metadataFilePath = getMetadataFilePath(context, currentBundleVersion) ?: run {
            OneKeyLog.debug("BundleUpdate", "metadataFilePath is null")
            return false
        }
        if (!isSafeVersionString(expectedAppVersion) || !isSafeVersionString(expectedBundleVersion) ||
            currentBundleVersion != "$expectedAppVersion-$expectedBundleVersion") return false
        val extractedSha256 = readMetadataFileSha256(
            signature,
            expectedAppVersion = expectedAppVersion,
            expectedBundleVersion = expectedBundleVersion,
            expectedAppType = "android",
        )
        if (extractedSha256.isNullOrEmpty()) return false
        val calculated = calculateSHA256(metadataFilePath) ?: return false
        return secureCompare(calculated, extractedSha256)
    }

    @JvmOverloads
    fun validateAllFilesInDir(
        context: Context,
        dirPath: String,
        metadata: Map<String, String>,
        appVersion: String,
        bundleVersion: String,
        allowStaging: Boolean = false,
    ): Boolean {
        val dir = File(dirPath)
        if (!dir.exists() || !dir.isDirectory) return false

        // The walked directory must be the managed extraction root for this
        // identity (or an isolated per-run directory under .diff-staging during diff reconstruction).
        val expectedDir = File(getBundleDir(context), "$appVersion-$bundleVersion")
        val stagingRootDir = File(getBundleDir(context), BundleDiffReconstruction.STAGING_ROOT_DIR_NAME)
        val isValidStaging = allowStaging &&
            dir.parentFile != null &&
            isSameDirectory(dir.parentFile!!, stagingRootDir) &&
            dir.name.startsWith("$appVersion-$bundleVersion-")
        if (!isSameDirectory(dir, expectedDir) && !isValidStaging) {
            OneKeyLog.error(
                "BundleUpdate",
                "[bundle-verify] unexpected bundle directory: ${dir.absolutePath}",
            )
            return false
        }

        return verifyExtractedFiles(dir, getFileMetadataEntries(metadata), "bundle-verify")
    }

    private fun verifyExtractedFiles(
        subtree: File,
        metadata: Map<String, String>,
        logPrefix: String,
        bundleRelativePrefix: String = "",
    ): Boolean = verifyBundleDirectory(
        directory = subtree,
        bundleRelativePrefix = bundleRelativePrefix,
        metadata = metadata,
        fileSystem = AndroidBundleFileSystem,
        verifyHash = { file, expected ->
            val actual = calculateSHA256(file.absolutePath)
            actual != null && secureCompare(expected, actual)
        },
        logError = { OneKeyLog.error("BundleUpdate", "[$logPrefix] $it") },
    )

    fun readFileContent(file: File): String {
        return file.readText(Charsets.UTF_8)
    }

    fun getFallbackUpdateBundleDataFile(context: Context): File {
        val path = File(getBundleDir(context), "fallbackUpdateBundleData.json")
        if (!path.exists()) {
            try { path.createNewFile() } catch (e: Exception) {
                OneKeyLog.error("BundleUpdate", "getFallbackUpdateBundleDataFile: ${e.message}")
            }
        }
        return path
    }

    fun readFallbackUpdateBundleDataFile(context: Context): MutableList<MutableMap<String, String>> {
        val file = getFallbackUpdateBundleDataFile(context)
        val content = try { readFileContent(file) } catch (e: Exception) { "" }
        if (content.isEmpty()) return mutableListOf()
        return try {
            val arr = JSONArray(content)
            val result = mutableListOf<MutableMap<String, String>>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val map = mutableMapOf<String, String>()
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    map[key] = obj.getString(key)
                }
                result.add(map)
            }
            result
        } catch (e: Exception) {
            OneKeyLog.error("BundleUpdate", "readFallbackUpdateBundleDataFile: ${e.message}")
            mutableListOf()
        }
    }

    fun writeFallbackUpdateBundleDataFile(data: List<Map<String, String>>, context: Context) {
        val file = getFallbackUpdateBundleDataFile(context)
        val arr = JSONArray()
        for (map in data) {
            val obj = JSONObject()
            for ((k, v) in map) obj.put(k, v)
            arr.put(obj)
        }
        try {
            // Atomic write: write to temp file then rename to avoid partial writes
            val tmpFile = File(file.parent, "${file.name}.tmp")
            FileOutputStream(tmpFile).use { fos ->
                fos.write(arr.toString().toByteArray(Charsets.UTF_8))
                fos.fd.sync()
            }
            if (!tmpFile.renameTo(file)) {
                tmpFile.delete()
                throw Exception("Failed to rename temp file to ${file.name}")
            }
        } catch (e: Exception) {
            OneKeyLog.error("BundleUpdate", "writeFallbackUpdateBundleDataFile: ${e.message}")
        }
    }

    fun validateBundlePairCompatibility(
        context: Context,
        bundleDir: String,
        metadata: Map<String, String>,
    ): Boolean {
        return validateStorageSchemaCompatibility(context, metadata) &&
            validateBundlePairCompatibility(bundleDir, metadata)
    }

    // Retain the original layout-validator signature for existing native callers.
    // Startup and extracted-bundle validation use the context overload.
    fun validateBundlePairCompatibility(bundleDir: String, metadata: Map<String, String>): Boolean {
        val mainBundleFile = File(bundleDir, MAIN_JS_BUNDLE_FILE_NAME)
        if (!mainBundleFile.exists()) {
            OneKeyLog.error(
                "BundleUpdate",
                "bundle pair invalid: main.jsbundle.hbc is missing at ${mainBundleFile.absolutePath}",
            )
            return false
        }

        // Three-bundle (union / split thread) mode requires a common.bundle
        // shipped alongside main.jsbundle.hbc. Without it, entry-only main
        // bundle references moduleIds that live in the common bundle and the
        // runtime crashes on first require().
        if (metadataRequiresCommonBundle(metadata)) {
            val commonBundleFile = File(bundleDir, COMMON_BUNDLE_FILE_NAME)
            if (!commonBundleFile.exists()) {
                OneKeyLog.error(
                    "BundleUpdate",
                    "requiresCommonBundle is true but common.bundle is missing at ${commonBundleFile.absolutePath}",
                )
                return false
            }
        }

        if (!metadataRequiresBackgroundBundle(metadata)) {
            return true
        }

        val protocolVersion = metadataBackgroundProtocolVersion(metadata)
        if (protocolVersion.isEmpty() || protocolVersion != SUPPORTED_BACKGROUND_PROTOCOL_VERSION) {
            OneKeyLog.error(
                "BundleUpdate",
                "backgroundProtocolVersion mismatch: expected=$SUPPORTED_BACKGROUND_PROTOCOL_VERSION, actual=$protocolVersion",
            )
            return false
        }

        val backgroundBundleFile = File(bundleDir, BACKGROUND_BUNDLE_FILE_NAME)
        if (!backgroundBundleFile.exists()) {
            OneKeyLog.error(
                "BundleUpdate",
                "requiresBackgroundBundle is true but background.bundle is missing at ${backgroundBundleFile.absolutePath}",
            )
            return false
        }

        return true
    }

    private data class ValidatedBundleInfo(
        val bundleDir: String,
        val currentBundleVersion: String,
        val metadata: Map<String, String>,
    )

    private fun getValidatedCurrentBundleInfo(context: Context): ValidatedBundleInfo? {
        processPreLaunchPendingTask(context)
        return try {
            val currentAppVersion = getAppVersion(context) ?: return null
            val currentBundleVersion = getCurrentBundleVersion(context) ?: run {
                OneKeyLog.warn("BundleUpdate", "getJsBundlePath: no currentBundleVersion stored")
                invalidateValidatedBundleInfoCache()
                return null
            }

            // Memo cache: avoid re-running signature + entry-bundle sha256 for
            // every bundleURL / common / main / background path getter on
            // startup.
            cachedValidatedBundleInfo?.let { cached ->
                if (cached.currentBundleVersion == currentBundleVersion) {
                    // Migration state can change while the native bundle cache survives
                    // a JS runtime reload. Recheck the ledger without repeating hashes.
                    if (!validateStorageSchemaCompatibility(context, cached.metadata)) {
                        invalidateValidatedBundleInfoCache()
                        return null
                    }
                    return cached
                }
            }

            OneKeyLog.info("BundleUpdate", "currentAppVersion: $currentAppVersion, currentBundleVersion: $currentBundleVersion")

            val prevNativeVersion = getNativeVersion(context)
            if (prevNativeVersion.isEmpty()) {
                OneKeyLog.warn("BundleUpdate", "getJsBundlePath: prevNativeVersion is empty")
                return null
            }

            if (currentAppVersion != prevNativeVersion) {
                OneKeyLog.info("BundleUpdate", "currentAppVersion is not equal to prevNativeVersion $currentAppVersion $prevNativeVersion")
                clearUpdateBundleData(context)
                clearNativeVersionPrefs(context)
                return null
            }

            val currentBuildNumber = getBuildNumber(context)
            val prevBuildNumber = getNativeBuildNumber(context)
            if (currentBuildNumber.isNotEmpty() && prevBuildNumber.isNotEmpty() && currentBuildNumber != prevBuildNumber) {
                OneKeyLog.info("BundleUpdate", "buildNumber changed from $prevBuildNumber to $currentBuildNumber, clearing bundle data")
                clearUpdateBundleData(context)
                clearNativeVersionPrefs(context)
                return null
            }

            val bundleDir = getCurrentBundleDir(context, currentBundleVersion) ?: run {
                OneKeyLog.warn("BundleUpdate", "getJsBundlePath: getCurrentBundleDir returned null")
                return null
            }
            if (!File(bundleDir).exists()) {
                OneKeyLog.info("BundleUpdate", "currentBundleDir does not exist: $bundleDir")
                return null
            }

            val signature = readSignatureFile(context, currentBundleVersion)
            OneKeyLog.debug("BundleUpdate", "getJsBundlePath: signatureLength=${signature.length}")

            val skipGPG = BuildConfig.ALLOW_SKIP_GPG_VERIFICATION && isDevSettingsEnabled(context) && isSkipGPGEnabled(context)
            if (skipGPG) {
                OneKeyLog.warn("BundleUpdate", "Startup SHA256 validation skipped (DevSettings + skip-GPG enabled)")
            }
            val currentBundleRelease = bundleVersionInFolder(currentBundleVersion, currentAppVersion) ?: return null
            if (!skipGPG && !validateMetadataFileSha256(context, currentBundleVersion, signature, currentAppVersion, currentBundleRelease)) {
                OneKeyLog.warn("BundleUpdate", "getJsBundlePath: validateMetadataFileSha256 failed, signatureLength=${signature.length}")
                return null
            }

            val metadataContent = getMetadataFileContent(context, currentBundleVersion) ?: run {
                OneKeyLog.warn("BundleUpdate", "getJsBundlePath: getMetadataFileContent returned null")
                return null
            }
            val metadata = parseMetadataJson(metadataContent)

            // Startup hot path: only verify entry-bundle SHA-256
            // (main + common + background as required by metadata flags). The
            // full-tree sha256 sweep already runs at install time
            // (validateAllFilesInDir in installBundle), so re-doing it on every
            // launch costs hundreds of ms per startup with no security gain
            // (sandboxed app data + signed metadata.json bind every file's
            // expected hash). Per-segment integrity is checked at loadSegment
            // time by SplitBundleLoader.
            if (!validateEntryBundlesSha256(bundleDir, metadata)) {
                OneKeyLog.info("BundleUpdate", "validateEntryBundlesSha256 failed on startup")
                return null
            }

            if (!validateBundlePairCompatibility(context, bundleDir, metadata)) {
                return null
            }

            val info = ValidatedBundleInfo(
                bundleDir = bundleDir,
                currentBundleVersion = currentBundleVersion,
                metadata = metadata,
            )
            cachedValidatedBundleInfo = info
            info
        } catch (e: Exception) {
            OneKeyLog.error("BundleUpdate", "Error getting bundle: ${e.message}")
            null
        }
    }

    /**
     * Verifies SHA-256 of just the entry bundles required to boot the JS
     * runtime (main + common + background, gated by metadata flags). Runs in
     * place of the legacy validateAllFilesInDir on the startup hot path.
     */
    fun validateEntryBundlesSha256(bundleDir: String, metadata: Map<String, String>): Boolean {
        val fileEntries = getFileMetadataEntries(metadata)
        val entriesToCheck = mutableListOf(MAIN_JS_BUNDLE_FILE_NAME)
        if (metadataRequiresCommonBundle(metadata)) {
            entriesToCheck += COMMON_BUNDLE_FILE_NAME
        }
        if (metadataRequiresBackgroundBundle(metadata)) {
            entriesToCheck += BACKGROUND_BUNDLE_FILE_NAME
        }
        for (entry in entriesToCheck) {
            val file = File(bundleDir, entry)
            if (!file.exists()) {
                OneKeyLog.error("BundleUpdate", "[entry-verify] missing entry file: $entry")
                return false
            }
            val expected = fileEntries[entry]
            if (expected.isNullOrEmpty()) {
                OneKeyLog.error("BundleUpdate", "[entry-verify] no metadata sha256 for entry: $entry")
                return false
            }
            val actual = calculateSHA256(file.absolutePath)
            // secureCompare is byte-wise (case-sensitive); lowercase both
            // sides so an uppercase-hex manifest doesn't false-mismatch
            // against `calculateSHA256`'s lowercase output. Matches the
            // semantic of the previous `equals(actual, ignoreCase = true)`
            // while preserving the constant-time guarantee against timing
            // attacks on hash comparisons (consistent with every other
            // sha256 comparison in this file: validateFilesRecursive,
            // validateWebEmbedRecursive, iOS validateEntryBundlesSha256).
            if (actual == null || !secureCompare(expected.lowercase(), actual.lowercase())) {
                OneKeyLog.error("BundleUpdate", "[entry-verify] sha256 mismatch for entry: $entry")
                return false
            }
        }
        return true
    }

    fun getCurrentBundleEntryPath(context: Context, entryFileName: String): String? {
        val bundleInfo = getValidatedCurrentBundleInfo(context) ?: return null
        val entryFile = File(bundleInfo.bundleDir, entryFileName)
        if (!entryFile.exists()) {
            OneKeyLog.info("BundleUpdate", "$entryFileName does not exist")
            return null
        }
        return entryFile.absolutePath
    }

    // @JvmStatic exposes these entry-point getters as true JVM-static methods
    // so SplitBundleLoaderModule (and any future external module) can call them
    // via Class.getMethod(...).invoke(null, ctx) reflection. Without it, Kotlin
    // `object` members are INSTANCE methods on the singleton, the null-receiver
    // invoke throws NPE, and the OTA segment lookup silently falls back to APK
    // builtin assets — see #SBL-OTA-FALLBACK regression report.
    @JvmStatic
    fun getCurrentBundleMainJSBundle(context: Context): String? {
        return getCurrentBundleEntryPath(context, MAIN_JS_BUNDLE_FILE_NAME)
    }

    @JvmStatic
    fun getCurrentBundleBackgroundJSBundle(context: Context): String? {
        return getCurrentBundleEntryPath(context, BACKGROUND_BUNDLE_FILE_NAME)
    }

    @JvmStatic
    fun getCurrentBundleCommonJSBundle(context: Context): String? {
        return getCurrentBundleEntryPath(context, COMMON_BUNDLE_FILE_NAME)
    }

    fun getWebEmbedPath(context: Context): String {
        val bundleInfo = getValidatedCurrentBundleInfo(context) ?: return ""
        // Lazy full-tree sha256 sweep for web-embed/**. The startup hot path
        // only validates the JS entry bundles (validateEntryBundlesSha256), so
        // without this, a tampered web-embed asset would slip through. Result
        // is cached per currentBundleVersion and invalidated on any bundle
        // mutation, so cost is paid once per (re)install.
        if (!ensureWebEmbedVerified(
                bundleDir = bundleInfo.bundleDir,
                currentBundleVersion = bundleInfo.currentBundleVersion,
                metadata = bundleInfo.metadata,
            )) {
            return ""
        }
        return File(bundleInfo.bundleDir, "web-embed").absolutePath
    }

    @Synchronized
    private fun ensureWebEmbedVerified(
        bundleDir: String,
        currentBundleVersion: String,
        metadata: Map<String, String>,
    ): Boolean {
        if (cachedWebEmbedVerifiedVersion == currentBundleVersion) {
            return true
        }
        if (!validateWebEmbedSha256(bundleDir, metadata)) {
            OneKeyLog.error("BundleUpdate", "validateWebEmbedSha256 failed for $currentBundleVersion")
            return false
        }
        cachedWebEmbedVerifiedVersion = currentBundleVersion
        return true
    }

    /**
     * Walks every file under bundleDir/web-embed/ recursively and verifies
     * each file's sha256 against the metadata entry (key is the bundle-relative
     * path). Also rejects files on disk that aren't listed in metadata, and
     * metadata entries whose backing file is missing. Returns true when
     * web-embed is absent from both disk and metadata (bundles without
     * web-embed).
     */
    fun validateWebEmbedSha256(bundleDir: String, metadata: Map<String, String>): Boolean {
        val webEmbedRoot = File(bundleDir, "web-embed")
        val fileEntries = getFileMetadataEntries(metadata)
        val webEmbedEntries = fileEntries.filterKeys { it.startsWith("web-embed/") }

        if (!webEmbedRoot.exists() || !webEmbedRoot.isDirectory) {
            if (webEmbedEntries.isNotEmpty()) {
                OneKeyLog.error(
                    "BundleUpdate",
                    "[web-embed-verify] metadata lists web-embed entries but directory is missing",
                )
                return false
            }
            return true
        }

        return try {
            verifyExtractedFiles(webEmbedRoot, webEmbedEntries, "web-embed-verify", "web-embed/")
        } catch (e: BundleFileSystemException) {
            OneKeyLog.error("BundleUpdate", "[web-embed-verify] unable to inspect extracted files: ${e.message}")
            false
        }
    }

    /**
     * Returns true if the OneKey developer mode (DevSettings) is enabled.
     * Reads the persisted value from MMKV storage (key: onekey_developer_mode_enabled,
     * instance: onekey-app-dev-setting) written by the JS ServiceDevSetting layer.
     */
    fun isDevSettingsEnabled(context: Context): Boolean {
        return try {
            MMKV.initialize(context)
            val mmkv = MMKV.mmkvWithID("onekey-app-dev-setting") ?: return false
            mmkv.decodeBool("onekey_developer_mode_enabled", false)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Returns true if the skip-GPG-verification toggle is enabled in developer settings.
     * Reads the persisted value from MMKV storage (key: onekey_bundle_skip_gpg_verification,
     * instance: onekey-app-dev-setting).
     * Gated by BuildConfig.ALLOW_SKIP_GPG_VERIFICATION — always returns false in production builds.
     */
    fun isSkipGPGEnabled(context: Context): Boolean {
        if (!BuildConfig.ALLOW_SKIP_GPG_VERIFICATION) return false
        return try {
            MMKV.initialize(context)
            val mmkv = MMKV.mmkvWithID("onekey-app-dev-setting") ?: return false
            mmkv.decodeBool("onekey_bundle_skip_gpg_verification", false)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Recursively deletes [directory], returning true only when nothing is left
     * behind. An already-missing entry counts as success (tolerates concurrent
     * removal); a child that fails to delete makes the whole call return false
     * so callers never report a half-deleted tree as a clean delete.
     */
    private fun deleteDirectory(directory: File): Boolean {
        if (!directory.exists()) return true
        var allDeleted = true
        directory.listFiles()?.forEach { file ->
            val ok = if (file.isDirectory) deleteDirectory(file) else (!file.exists() || file.delete())
            if (!ok) allDeleted = false
        }
        // delete() on a non-empty dir returns false, so a child failure above
        // naturally propagates here too.
        val dirDeleted = !directory.exists() || directory.delete()
        return allDeleted && dirDeleted
    }

    fun deleteDir(dir: File): Boolean = deleteDirectory(dir)

    fun unzipFile(zipFilePath: String, destDirectory: String) {
        BundleDiffReconstruction.unzipFile(zipFilePath, destDirectory, AndroidBundleFileSystem)
    }
}

@DoNotStrip
class ReactNativeBundleUpdate : HybridReactNativeBundleUpdateSpec() {

    companion object {
        private const val PREFS_NAME = "BundleUpdatePrefs"

        // Number of concurrent segment files (`<partial>.seg0..seg{N-1}`) the
        // ConcurrentRangeDownloader produces. MUST equal the segmentCount passed
        // to ConcurrentRangeDownloader (currently the default 8). Every place
        // that cleans up `.segN` files must iterate `0 until CONCURRENT_SEGMENT_COUNT`.
        private const val CONCURRENT_SEGMENT_COUNT = 8
        private val operations = BundleUpdateOperations()
        private val diffFallbackDigests = ConcurrentHashMap<ReleaseIdentity, String>()
        private val fullBundleParamsByRelease = ConcurrentHashMap<ReleaseIdentity, FullBundleParams>()
        private val diffVerifiedMetadataByRelease = ConcurrentHashMap<ReleaseIdentity, BundleDiffReconstruction.VerifiedDiffMetadata>()
    }

    private data class ReleaseIdentity(val appVersion: String, val bundleVersion: String) {
        val key: String get() = "$appVersion-$bundleVersion"
    }

    private data class FullBundleParams(
        val downloadUrl: String,
        val fileSize: Double,
        val sha256: String,
    )

    private val listeners = CopyOnWriteArrayList<BundleListener>()
    private val nextListenerId = AtomicLong(1)

    private fun startOperation(
        context: Context,
        release: ReleaseIdentity,
        handle: ConcurrentRangeDownloader.CancelHandle? = null,
    ): BundleUpdateOperations.Operation {
        return operations.start(File(context.filesDir.canonicalFile, release.key).path) { handle?.requestCancel() }
    }

    private val httpClient = OkHttpClient.Builder()
        // OCDS §5.4 timeouts. `readTimeout` is the primary inter-byte stall
        // window: OkHttp raises SocketTimeoutException when no bytes arrive within
        // it, which the segment loop classifies transient and retries. We do NOT
        // set callTimeout — it bounds the WHOLE call and would cut a legitimately
        // slow large segment mid-stream; the overall run deadline is owned by the
        // shared-JS retry budget (ServiceAppUpdate), not this socket layer.
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addNetworkInterceptor { chain ->
            val req = chain.request()
            if (!req.url.isHttps) {
                throw SecurityException("Redirect to non-HTTPS URL is not allowed: ${req.url}")
            }
            chain.proceed(req)
        }
        .build()

    private fun sendEvent(type: String, progress: Int = 0, message: String = "") {
        val event = BundleDownloadEvent(type = type, progress = progress.toDouble(), message = message)
        for (listener in listeners) {
            try {
                listener.callback(event)
            } catch (e: Exception) {
                OneKeyLog.error("BundleUpdate", "Error sending event: ${e.message}")
            }
        }
    }

    /** Keep raw exceptions in local logs and send only the fixed update wire to JS. */
    private fun <T> updatePromise(step: String, work: suspend () -> T): Promise<T> {
        return Promise.async {
            try {
                work()
            } catch (e: Exception) {
                OneKeyLog.error("BundleUpdate", "$step failed: ${e.javaClass.simpleName}: ${e.message}")
                throw bundleUpdateError(e, step)
            }
        }
    }

    override fun addDownloadListener(callback: (BundleDownloadEvent) -> Unit): Double {
        val id = nextListenerId.getAndIncrement().toDouble()
        listeners.add(BundleListener(id, callback))
        OneKeyLog.debug("BundleUpdate", "addDownloadListener: id=$id, totalListeners=${listeners.size}")
        return id
    }

    override fun removeDownloadListener(id: Double) {
        listeners.removeAll { it.id == id }
        OneKeyLog.debug("BundleUpdate", "removeDownloadListener: id=$id, totalListeners=${listeners.size}")
    }

    private fun getContext(): Context {
        return NitroModules.applicationContext
            ?: throw Exception("Application context unavailable")
    }

    private fun isDebuggable(): Boolean {
        val context = NitroModules.applicationContext ?: return false
        return (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    /** Returns true if OneKey developer mode (DevSettings) is enabled via MMKV storage. */
    private fun isDevSettingsEnabled(): Boolean {
        return try {
            val context = NitroModules.applicationContext ?: return false
            BundleUpdateStoreAndroid.isDevSettingsEnabled(context)
        } catch (e: Exception) {
            false
        }
    }

    /** Returns true if the skip-GPG-verification toggle is enabled via MMKV storage.
     *  Gated by BuildConfig.ALLOW_SKIP_GPG_VERIFICATION — always returns false in production builds. */
    private fun isSkipGPGEnabled(): Boolean {
        if (!BuildConfig.ALLOW_SKIP_GPG_VERIFICATION) return false
        return try {
            val context = NitroModules.applicationContext ?: return false
            BundleUpdateStoreAndroid.isSkipGPGEnabled(context)
        } catch (e: Exception) {
            false
        }
    }

    private fun cleanupDownloadArtifacts(filePath: String) {
        val file = File(filePath)
        if (file.exists()) file.delete()
        val metaFile = File("$filePath.meta.json")
        if (metaFile.exists()) metaFile.delete()
        val partialPath = "$filePath.partial"
        val partialFile = File(partialPath)
        if (partialFile.exists()) partialFile.delete()
        File("$partialPath.progress").delete()
        for (i in 0 until CONCURRENT_SEGMENT_COUNT) File("$partialPath.seg$i").delete()
    }

    private fun downloadFileWithResumeAndVerify(
        downloadUrl: String,
        filePath: String,
        expectedSize: Long,
        sha256: String,
        handle: ConcurrentRangeDownloader.CancelHandle,
        step: String,
    ) {
        fun ensureNotCancelled() {
            if (handle.aborted.get()) throw BundleUpdateError("CANCELLED", step)
        }

        BundleDiffReconstruction.requireArchiveBytes(expectedSize)
        val partialFilePath = "$filePath.partial"
        val downloadedFile = File(filePath)
        val partialFile = File(partialFilePath)

        fun promotePartial() {
            ensureNotCancelled()
            if (!partialFile.renameTo(downloadedFile)) {
                throw BundleUpdateError("STORAGE_ERROR", step)
            }
        }

        fun verifyAndPromote() {
            ensureNotCancelled()
            BundleDiffReconstruction.requireArchiveBytes(partialFile.length())
            val calculated = BundleUpdateStoreAndroid.calculateSHA256(partialFilePath)
                ?: throw BundleUpdateError("STORAGE_ERROR", step)
            ensureNotCancelled()
            if (!BundleUpdateStoreAndroid.secureCompare(calculated, sha256)) {
                partialFile.delete()
                throw BundleUpdateError("INTEGRITY_FAILED", step)
            }
            promotePartial()
        }

        ensureNotCancelled()
        sendEvent("update/start")
        run {
            val concurrentProgress = AtomicInteger(-1)
            val concurrentOutcome = ConcurrentRangeDownloader(
                httpClient = httpClient,
                log = { msg -> OneKeyLog.info("BundleUpdate", msg) },
            ).download(downloadUrl, partialFilePath, handle, maxTotalBytes = BundleDiffReconstruction.MAX_BUNDLE_ARCHIVE_BYTES) { transferred, total ->
                if (handle.aborted.get()) return@download
                if (total > 0) {
                    val p = ((transferred * 100) / total).toInt().coerceIn(0, 100)
                    val prev = concurrentProgress.get()
                    if (p > prev && concurrentProgress.compareAndSet(prev, p)) {
                        sendEvent("update/downloading", progress = p)
                    }
                }
            }
            ensureNotCancelled()
            if (concurrentOutcome == ConcurrentRangeDownloader.Outcome.COMPLETED) {
                verifyAndPromote()
                return
            }
            OneKeyLog.info("BundleUpdate", "downloadBundle: concurrent not used, falling back to single-stream")
        }

        var partialBytes = 0L
        val hasConcurrentSegments = (0 until CONCURRENT_SEGMENT_COUNT).any { File("$partialFilePath.seg$it").exists() }
        if (partialFile.exists() && !hasConcurrentSegments) {
            val partialSize = partialFile.length()
            BundleDiffReconstruction.requireArchiveBytes(partialSize)
            when {
                expectedSize > 0 && partialSize == expectedSize -> {
                    OneKeyLog.info("BundleUpdate", "downloadBundle: partial matches expected size ($partialSize), verifying before promotion")
                    if (verifyBundleSHA256(partialFilePath, sha256)) {
                        promotePartial()
                        ensureNotCancelled()
                        OneKeyLog.info("BundleUpdate", "downloadBundle: recovered crashed-before-rename bundle, skipping download")
                        Thread.sleep(1000)
                        ensureNotCancelled()
                        return
                    } else {
                        OneKeyLog.warn("BundleUpdate", "downloadBundle: partial verification failed, discarding")
                        partialFile.delete()
                    }
                }
                expectedSize > 0 && partialSize > expectedSize -> {
                    OneKeyLog.warn("BundleUpdate", "downloadBundle: stale partial (>expected), discarding: $partialSize/$expectedSize")
                    partialFile.delete()
                    for (i in 0 until CONCURRENT_SEGMENT_COUNT) File("$partialFilePath.seg$i").delete()
                }
                partialSize > 0 -> {
                    partialBytes = partialSize
                    OneKeyLog.info("BundleUpdate", "downloadBundle: resuming from $partialBytes bytes (expected=$expectedSize)")
                }
                else -> partialFile.delete()
            }
        }

        OneKeyLog.info("BundleUpdate", "downloadBundle: starting single-stream download (resume=${partialBytes > 0})...")
        val requestBuilder = Request.Builder().url(downloadUrl)
        if (partialBytes > 0) {
            requestBuilder.addHeader("Range", "bytes=$partialBytes-")
        }
        handle.execute(httpClient.newCall(requestBuilder.build())) { response ->
            ensureNotCancelled()

            if (response.code == 416) {
                val contentRange = response.header("Content-Range")
                val totalFromHeader = contentRange
                    ?.let { Regex("""bytes\s+\*\s*/\s*(\d+)""").find(it)?.groupValues?.getOrNull(1)?.toLongOrNull() }
                totalFromHeader?.let { BundleDiffReconstruction.requireArchiveBytes(it) }
                if (totalFromHeader != null && totalFromHeader == partialBytes && partialFile.exists()) {
                    OneKeyLog.info("BundleUpdate", "downloadBundle: HTTP 416 with total==$totalFromHeader matches partial, verifying before promotion")
                    if (verifyBundleSHA256(partialFilePath, sha256)) {
                        promotePartial()
                        ensureNotCancelled()
                        OneKeyLog.info("BundleUpdate", "downloadBundle: 416 recovery succeeded, skipping download")
                        return@execute
                    }
                    OneKeyLog.warn("BundleUpdate", "downloadBundle: 416 recovery failed verify, discarding")
                    partialFile.delete()
                }
                OneKeyLog.warn("BundleUpdate", "downloadBundle: HTTP 416 (range not satisfiable), discarding partial and failing this attempt")
                if (partialFile.exists()) partialFile.delete()
                for (i in 0 until CONCURRENT_SEGMENT_COUNT) File("$partialFilePath.seg$i").delete()
                throw BundleUpdateError("HTTP_ERROR", step, 416)
            }

            val expectsResume = partialBytes > 0
            val isPartialResponse = response.code == 206

            if (!response.isSuccessful || (response.code != 200 && response.code != 206)) {
                OneKeyLog.error("BundleUpdate", "downloadBundle: HTTP error, statusCode=${response.code}")
                throw BundleUpdateError("HTTP_ERROR", step, response.code)
            }

            if (expectsResume && !isPartialResponse) {
                OneKeyLog.warn("BundleUpdate", "downloadBundle: requested Range but server returned 200, restarting from scratch")
                if (partialFile.exists()) partialFile.delete()
                for (i in 0 until CONCURRENT_SEGMENT_COUNT) File("$partialFilePath.seg$i").delete()
            }

            val offset = if (isPartialResponse) partialBytes else 0L
            val rangeTotal = if (isPartialResponse) {
                BundleDiffReconstruction.singleStreamRangeTotal(response.header("Content-Range"), offset) ?: run {
                    OneKeyLog.warn("BundleUpdate", "downloadBundle: invalid 206 range at offset $offset, discarding partial")
                    if (partialFile.exists()) partialFile.delete()
                    for (i in 0 until CONCURRENT_SEGMENT_COUNT) File("$partialFilePath.seg$i").delete()
                    throw BundleUpdateError("INTEGRITY_FAILED", step)
                }
            } else null

            val body = response.body ?: run {
                throw BundleUpdateError("NETWORK_ERROR", step)
            }
            val contentLength = body.contentLength()
            BundleDiffReconstruction.requireArchiveBytes(offset, contentLength.coerceAtLeast(0L))
            val totalSize = rangeTotal ?: if (contentLength > 0) contentLength else expectedSize
            BundleDiffReconstruction.requireArchiveBytes(totalSize)
            OneKeyLog.info(
                "BundleUpdate",
                "downloadBundle: HTTP ${response.code}, contentLength=$contentLength, totalSize=$totalSize, offset=$offset, downloading..."
            )

            val parentDir = File(partialFilePath).parentFile
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs()
            }

            body.byteStream().use { inputStream ->
                FileOutputStream(partialFile, offset > 0L).use { outputStream ->
                    var totalBytesRead = outputStream.channel.size()
                    if (totalBytesRead != offset) {
                        OneKeyLog.warn("BundleUpdate", "downloadBundle: partial file length changed before writing")
                        partialFile.delete()
                        throw BundleUpdateError("STORAGE_ERROR", step)
                    }
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var prevProgress = -1
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        ensureNotCancelled()
                        totalBytesRead = BundleDiffReconstruction.requireArchiveBytes(totalBytesRead, bytesRead.toLong())
                        outputStream.write(buffer, 0, bytesRead)
                        if (totalSize > 0) {
                            val progress = ((totalBytesRead * 100) / totalSize).toInt().coerceIn(0, 100)
                            if (progress != prevProgress) {
                                sendEvent("update/downloading", progress = progress)
                                OneKeyLog.info("BundleUpdate", "download progress: $progress% ($totalBytesRead/$totalSize)")
                                prevProgress = progress
                            }
                        }
                    }
                }
            }

            verifyAndPromote()
        }
    }

    override fun downloadBundle(params: BundleDownloadParams): Promise<BundleDownloadResult> {
        return updatePromise("download") {
            var operation: BundleUpdateOperations.Operation? = null
            val handle = ConcurrentRangeDownloader.CancelHandle()

            try {
                val context = getContext()
                val appVersion = params.latestVersion
                val bundleVersion = params.bundleVersion
                val downloadUrl = params.downloadUrl
                val sha256 = BundleDiffReconstruction.normalizeSha256(params.sha256) ?: params.sha256.lowercase()

                OneKeyLog.info("BundleUpdate", "downloadBundle: appVersion=$appVersion, bundleVersion=$bundleVersion, fileSize=${params.fileSize}, url=$downloadUrl")

                if (!BundleUpdateStoreAndroid.isSafeVersionString(appVersion) ||
                    !BundleUpdateStoreAndroid.isSafeVersionString(bundleVersion)) {
                    OneKeyLog.error("BundleUpdate", "downloadBundle: invalid version string format: appVersion=$appVersion, bundleVersion=$bundleVersion")
                    throw BundleUpdateError("INVALID_ARGUMENT", "download")
                }

                if (!downloadUrl.startsWith("https://")) {
                    OneKeyLog.error("BundleUpdate", "downloadBundle: URL is not HTTPS: $downloadUrl")
                    throw BundleUpdateError("SECURITY_REJECTED", "download")
                }

                val release = ReleaseIdentity(appVersion, bundleVersion)
                val run = startOperation(context, release, handle)
                operation = run
                val bundleKey = release.key
                val downloadDir = File(BundleUpdateStoreAndroid.getDownloadBundleDir(context))
                val fullFile = File(downloadDir, "$bundleKey.zip")
                resolveManagedFile(downloadDir, fullFile, "download")
                val filePath = fullFile.absolutePath
                val partialFilePath = "$filePath.partial"
                run.ensureActive()

                fun complete(result: BundleDownloadResult, publish: () -> Unit = {}): BundleDownloadResult {
                    operations.publish(run, publish)
                    sendEvent("update/complete")
                    return result
                }

                fullBundleParamsByRelease[release] = FullBundleParams(
                    downloadUrl = downloadUrl,
                    fileSize = params.fileSize,
                    sha256 = sha256,
                )

                val fullResult = BundleDownloadResult(
                    downloadUrl = downloadUrl,
                    latestVersion = appVersion,
                    bundleVersion = bundleVersion,
                    sha256 = sha256,
                    diffDownloadUrl = null,
                    diffFileSize = null,
                    diffSha256 = null,
                    diffSignature = null,
                )

                OneKeyLog.info("BundleUpdate", "downloadBundle: filePath=$filePath")

                val downloadedFile = File(filePath)
                val partialFile = File(partialFilePath)
                if (downloadedFile.exists()) {
                    OneKeyLog.info("BundleUpdate", "downloadBundle: file already exists, verifying SHA256...")
                    if (verifyBundleSHA256(filePath, sha256)) {
                        run.ensureActive()
                        OneKeyLog.info("BundleUpdate", "downloadBundle: existing file SHA256 valid, skipping download")
                        if (partialFile.exists()) partialFile.delete()
                        File("$partialFilePath.progress").delete()
                        for (i in 0 until CONCURRENT_SEGMENT_COUNT) File("$partialFilePath.seg$i").delete()
                        Thread.sleep(1000)
                        run.ensureActive()
                        return@updatePromise complete(fullResult)
                    } else {
                        OneKeyLog.warn("BundleUpdate", "downloadBundle: existing file SHA256 mismatch, re-downloading")
                        downloadedFile.delete()
                        if (partialFile.exists()) partialFile.delete()
                        for (i in 0 until CONCURRENT_SEGMENT_COUNT) File("$partialFilePath.seg$i").delete()
                    }
                }

                val diffDir = File(downloadDir, BundleDiffReconstruction.DIFF_DOWNLOAD_DIR_NAME)
                val diffFilePath = File(diffDir, "$bundleKey${BundleDiffReconstruction.DIFF_ZIP_EXTENSION}").absolutePath
                val diffMetaFile = File(diffDir, "$bundleKey${BundleDiffReconstruction.DIFF_META_EXTENSION}")
                resolveManagedFile(downloadDir, File(diffFilePath), "download")
                resolveManagedFile(downloadDir, diffMetaFile, "download")
                val existingDesc = BundleDiffReconstruction.readDescriptor(diffMetaFile, appVersion, bundleVersion)
                val isFallbackMarked = diffFallbackDigests[release] == sha256 ||
                    (existingDesc?.fallbackRequired == true &&
                        BundleDiffReconstruction.constantTimeEqualHex(existingDesc.sha256, sha256))

                // Priority 1: Incremental diff download if usable diff params are provided
                val usableDiff = BundleDiffReconstruction.isUsableDiffParams(
                    diffDownloadUrl = params.diffDownloadUrl,
                    diffSha256 = params.diffSha256,
                    diffSignature = params.diffSignature,
                    isFallbackMarked = isFallbackMarked,
                )
                if (usableDiff) {
                    if (!diffDir.exists()) diffDir.mkdirs()
                    val diffUrl = params.diffDownloadUrl!!
                    val diffSha256 = BundleDiffReconstruction.normalizeSha256(params.diffSha256)!!
                    val diffSignature = params.diffSignature!!
                    val diffFile = File(diffFilePath)
                    val diffDescriptor = BundleDiffReconstruction.descriptorPreservingVerifiedSignature(
                        newDescriptor = BundleDiffReconstruction.DiffArchiveDescriptor(
                            appVersion = appVersion,
                            bundleVersion = bundleVersion,
                            downloadUrl = downloadUrl,
                            fileSize = params.fileSize,
                            sha256 = sha256,
                            diffSha256 = diffSha256,
                            signature = null,
                        ),
                        existingDescriptor = existingDesc,
                    )
                    val diffResult = BundleDownloadResult(
                        downloadUrl = downloadUrl,
                        latestVersion = appVersion,
                        bundleVersion = bundleVersion,
                        sha256 = sha256,
                        diffDownloadUrl = diffUrl,
                        diffFileSize = params.diffFileSize,
                        diffSha256 = diffSha256,
                        diffSignature = diffSignature,
                    )

                    if (diffFile.exists()) {
                        if (verifyBundleSHA256(diffFilePath, diffSha256)) {
                            run.ensureActive()
                            OneKeyLog.info("BundleUpdate", "downloadBundle: existing diff file SHA256 valid, skipping download")
                            val diffPartial = "$diffFilePath.partial"
                            File(diffPartial).delete()
                            File("$diffPartial.progress").delete()
                            for (i in 0 until CONCURRENT_SEGMENT_COUNT) File("$diffPartial.seg$i").delete()
                            Thread.sleep(1000)
                            return@updatePromise complete(diffResult) {
                                if (!BundleDiffReconstruction.writeDescriptor(diffDescriptor, diffMetaFile, AndroidBundleFileSystem)) {
                                    throw BundleUpdateError("STORAGE_ERROR", "download")
                                }
                            }
                        } else {
                            cleanupDownloadArtifacts(diffFilePath)
                        }
                    }

                    try {
                        val expectedDiffSize = (params.diffFileSize ?: 0.0).let { if (it > 0) it.toLong() else 0L }
                        downloadFileWithResumeAndVerify(
                            downloadUrl = diffUrl,
                            filePath = diffFilePath,
                            expectedSize = expectedDiffSize,
                            sha256 = diffSha256,
                            handle = handle,
                            step = "download",
                        )
                        OneKeyLog.info("BundleUpdate", "downloadBundle: diff download completed successfully, appVersion=$appVersion, bundleVersion=$bundleVersion")
                        return@updatePromise complete(diffResult) {
                            if (!BundleDiffReconstruction.writeDescriptor(diffDescriptor, diffMetaFile, AndroidBundleFileSystem)) {
                                throw BundleUpdateError("STORAGE_ERROR", "download")
                            }
                        }
                    } catch (e: Exception) {
                        run.ensureActive()
                        val updateError = bundleUpdateError(e, "download")
                        if (updateError.code == "CANCELLED" || updateError.code == "STORAGE_ERROR") throw updateError
                        if (BundleDiffReconstruction.isRetryableDiffDownloadError(updateError.code)) {
                            OneKeyLog.warn("BundleUpdate", "downloadBundle: transient diff download interruption for $bundleKey, preserving resume progress: ${updateError.code}")
                            BundleDiffReconstruction.writeDescriptor(diffDescriptor, diffMetaFile, AndroidBundleFileSystem)
                            throw updateError
                        }
                        OneKeyLog.warn("BundleUpdate", "downloadBundle: diff download failed for $bundleKey, falling back to full bundle: ${e.message}")
                        diffFallbackDigests[release] = sha256
                        cleanupDownloadArtifacts(diffFilePath)
                    }
                }

                // Priority 2: Full bundle download
                val expectedSize = if (params.fileSize > 0) params.fileSize.toLong() else 0L
                downloadFileWithResumeAndVerify(
                    downloadUrl = downloadUrl,
                    filePath = filePath,
                    expectedSize = expectedSize,
                    sha256 = sha256,
                    handle = handle,
                    step = "download",
                )
                OneKeyLog.info("BundleUpdate", "downloadBundle: completed successfully, appVersion=$appVersion, bundleVersion=$bundleVersion")
                complete(fullResult)
            } catch (e: Exception) {
                OneKeyLog.error("BundleUpdate", "downloadBundle: failed: ${e.javaClass.simpleName}: ${e.message}")
                val intentionallyCancelled = operation?.cancelled == true ||
                    (e is BundleUpdateOperations.AdmissionException && e.reason == BundleUpdateOperations.Refusal.CANCELLED)
                val updateError = if (intentionallyCancelled) {
                    BundleUpdateError("CANCELLED", "download")
                } else {
                    bundleUpdateError(e, "download")
                }
                if (e !is BundleUpdateOperations.AdmissionException && !intentionallyCancelled) {
                    sendEvent("update/error", message = updateError.message.orEmpty())
                }
                throw updateError
            } finally {
                operation?.let(operations::finish)
            }
        }
    }

    /**
     * Returns true on hash match. On false, callers may inspect
     * BundleUpdateStoreAndroid.lastSHA256FailureReason() to distinguish a
     * computation failure (FILE_TRUNCATED / OOM / IO_*) from a clean hash
     * mismatch (reason == null).
     */
    private fun verifyBundleSHA256(bundlePath: String, sha256: String): Boolean {
        if (File(bundlePath).length() > BundleDiffReconstruction.MAX_BUNDLE_ARCHIVE_BYTES) return false
        val calculated = BundleUpdateStoreAndroid.calculateSHA256(bundlePath)
        if (calculated == null) {
            val reason = BundleUpdateStoreAndroid.lastSHA256FailureReason() ?: "UNKNOWN"
            val fileSize = try { File(bundlePath).length() } catch (_: Exception) { -1L }
            OneKeyLog.error("BundleUpdate", "verifyBundleSHA256: failed to calculate SHA256 for: $bundlePath, reason=$reason, fileSize=$fileSize")
            return false
        }
        val isValid = BundleUpdateStoreAndroid.secureCompare(calculated, sha256)
        OneKeyLog.debug("BundleUpdate", "verifyBundleSHA256: path=$bundlePath, expected=${sha256.take(16)}..., calculated=${calculated.take(16)}..., valid=$isValid")
        return isValid
    }

    private fun isSafeVersionString(version: String): Boolean {
        return BundleUpdateStoreAndroid.isSafeVersionString(version)
    }

    override fun downloadBundleASC(params: BundleDownloadASCParams): Promise<Unit> {
        return updatePromise("downloadASC") {
            val appVersion = params.latestVersion
            val bundleVersion = params.bundleVersion
            val signature = params.signature
            if (!isSafeVersionString(appVersion) || !isSafeVersionString(bundleVersion)) {
                throw BundleUpdateError("INVALID_ARGUMENT", "downloadASC")
            }
            if (signature.toByteArray(Charsets.UTF_8).size > 64 * 1024) {
                throw BundleUpdateError("INVALID_ARGUMENT", "downloadASC")
            }

            val context = getContext()
            OneKeyLog.info("BundleUpdate", "downloadBundleASC: appVersion=$appVersion, bundleVersion=$bundleVersion, signatureLength=${signature.length}")

            val storageKey = "$appVersion-$bundleVersion"
            val ascBaseCanonical = File(BundleUpdateStoreAndroid.getAscDir(context)).canonicalPath
            val signatureFile = File(BundleUpdateStoreAndroid.getSignatureFilePath(context, storageKey))
            if (AndroidBundleFileSystem.isSymbolicLink(signatureFile) ||
                !signatureFile.canonicalPath.startsWith(ascBaseCanonical + File.separator)
            ) {
                throw BundleUpdateError("SECURITY_REJECTED", "downloadASC")
            }
        }
    }

    private fun verifyAndExtractFullZip(
        context: Context,
        resolvedFilePath: String,
        verificationDir: File,
        release: ReleaseIdentity,
        sha256: String,
        signature: String,
        isInstalled: Boolean,
        skipGPG: Boolean,
        operation: BundleUpdateOperations.Operation,
    ) {
        operation.ensureActive()
        if (File(resolvedFilePath).length() > BundleDiffReconstruction.MAX_BUNDLE_ARCHIVE_BYTES) {
            throw BundleUpdateError("SECURITY_REJECTED", "verifyASC")
        }
        val expectedMetadataSha = if (skipGPG) null else {
            val metadataSha = BundleUpdateStoreAndroid.readMetadataFileSha256(
                signature,
                expectedAppVersion = release.appVersion,
                expectedBundleVersion = release.bundleVersion,
                expectedAppType = "android",
            ) ?: throw BundleUpdateError("INTEGRITY_FAILED", "verifyASC")
            val actualZipSha = BundleUpdateStoreAndroid.calculateSHA256(resolvedFilePath)
                ?: throw BundleUpdateError("STORAGE_ERROR", "verifyASC")
            if (!BundleUpdateStoreAndroid.secureCompare(actualZipSha, sha256)) {
                throw BundleUpdateError("INTEGRITY_FAILED", "verifyASC")
            }
            metadataSha
        }

        operation.ensureActive()
        val verificationPath = verificationDir.canonicalPath
        if (!isInstalled) BundleUpdateStoreAndroid.unzipFile(resolvedFilePath, verificationPath)
        operation.ensureActive()

        val metadataFile = File(verificationDir, "metadata.json")
        if (!metadataFile.isFile) throw BundleUpdateError("PACKAGE_MISSING", "verifyASC")
        if (expectedMetadataSha != null) {
            val actualMetadataSha = BundleUpdateStoreAndroid.calculateSHA256(metadataFile.absolutePath)
                ?: throw BundleUpdateError("INTEGRITY_FAILED", "verifyASC")
            if (!BundleUpdateStoreAndroid.secureCompare(actualMetadataSha, expectedMetadataSha)) {
                throw BundleUpdateError("INTEGRITY_FAILED", "verifyASC")
            }
        }

        val metadata = BundleUpdateStoreAndroid.parseMetadataJson(BundleUpdateStoreAndroid.readFileContent(metadataFile))
        if (!BundleUpdateStoreAndroid.validateAllFilesInDir(
                context, verificationPath, metadata, release.appVersion, release.bundleVersion, allowStaging = !isInstalled,
            )
        ) {
            throw BundleUpdateError("INTEGRITY_FAILED", "verifyASC")
        }
        if (!BundleUpdateStoreAndroid.validateBundlePairCompatibility(context, verificationPath, metadata)) {
            throw BundleUpdateError(if (isInstalled) "INTEGRITY_FAILED" else "INCOMPATIBLE", "verifyASC")
        }
    }

    private fun collectCandidateOtaDirs(
        context: Context,
        appVersion: String,
        excludingFolder: String,
    ): List<File> {
        val bundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context))
        val candidates = ArrayList<File>()
        val seen = HashSet<String>()

        val activeFolder = BundleUpdateStoreAndroid.getCurrentBundleVersion(context)
        if (!activeFolder.isNullOrEmpty() &&
            activeFolder != excludingFolder &&
            activeFolder != BundleDiffReconstruction.STAGING_ROOT_DIR_NAME &&
            BundleUpdateStoreAndroid.bundleVersionInFolder(activeFolder, appVersion) != null
        ) {
            val activeDir = File(bundleDir, activeFolder)
            if (activeDir.exists() && activeDir.isDirectory) {
                candidates.add(activeDir)
                seen.add(activeFolder)
            }
        }

        val children = bundleDir.listFiles()
            ?.filter { it.isDirectory }
            ?.sortedByDescending { it.name }
            .orEmpty()
        for (child in children) {
            val name = child.name
            if (name == "asc" ||
                name == BundleDiffReconstruction.STAGING_ROOT_DIR_NAME ||
                name == excludingFolder ||
                seen.contains(name)
            ) {
                continue
            }
            if (BundleUpdateStoreAndroid.bundleVersionInFolder(name, appVersion) == null) {
                continue
            }
            candidates.add(child)
            seen.add(name)
        }
        return candidates
    }

    private fun copyBuiltinAssetFile(
        context: Context,
        relativePath: String,
        destinationFile: File,
        expectedHash: String,
    ): Boolean {
        val candidateAssetPaths = BundleDiffReconstruction.builtinCandidateAssetPaths(relativePath)
        for (assetPath in candidateAssetPaths) {
            val ok = try {
                context.assets.open(assetPath).use { input ->
                    FileOutputStream(destinationFile).use { output ->
                        val buffer = ByteArray(8192)
                        while (true) {
                            val count = input.read(buffer)
                            if (count <= 0) break
                            output.write(buffer, 0, count)
                        }
                    }
                }
                val hash = BundleUpdateStoreAndroid.calculateSHA256(destinationFile.absolutePath)
                if (hash != null && BundleDiffReconstruction.constantTimeEqualHex(hash, expectedHash)) {
                    true
                } else {
                    if (destinationFile.exists()) destinationFile.delete()
                    false
                }
            } catch (_: Exception) {
                if (destinationFile.exists()) destinationFile.delete()
                false
            }
            if (ok) return true
        }

        val apkResEntries = BundleDiffReconstruction.builtinCandidateApkResZipEntries(relativePath)
        if (apkResEntries.isNotEmpty()) {
            val appInfo = try { context.applicationInfo } catch (_: Exception) { null }
            val apkPaths = BundleDiffReconstruction.collectInstalledApkPaths(
                sourceDir = appInfo?.sourceDir,
                splitSourceDirs = appInfo?.splitSourceDirs,
            )
            if (BundleDiffReconstruction.copyMatchingZipEntryFromApks(
                    apkPaths = apkPaths,
                    candidateEntryNames = apkResEntries,
                    destinationFile = destinationFile,
                    expectedHash = expectedHash,
                    sha256OfFile = { BundleUpdateStoreAndroid.calculateSHA256(it) },
                )
            ) {
                return true
            }
        }
        return false
    }

    private fun reconstructAndVerifyDiffZip(
        context: Context,
        resolvedDiffZipPath: String,
        stagingDir: File,
        stagingPath: String,
        destinationDir: File,
        release: ReleaseIdentity,
        diffSha256: String,
        diffSignature: String,
        signature: String,
        isInstalled: Boolean,
        skipGPG: Boolean,
        operation: BundleUpdateOperations.Operation,
    ): String {
        operation.ensureActive()
        val appVersion = release.appVersion
        val bundleVersion = release.bundleVersion
        val folderName = "$appVersion-$bundleVersion"
        var expectedDiffManifestSha256: String? = null
        var expectedMetadataSha256: String? = null

        if (!skipGPG) {
            expectedDiffManifestSha256 = BundleUpdateStoreAndroid.readMetadataFileSha256(
                diffSignature,
                expectedAppVersion = appVersion,
                expectedBundleVersion = bundleVersion,
                expectedAppType = "android",
                expectedFileName = "diff-manifest.json",
            )?.lowercase()
            if (expectedDiffManifestSha256.isNullOrEmpty()) {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: diffSignature GPG verification failed")
                throw BundleUpdateError("INTEGRITY_FAILED", "verifyASC")
            }
            expectedMetadataSha256 = BundleUpdateStoreAndroid.readMetadataFileSha256(
                signature,
                expectedAppVersion = appVersion,
                expectedBundleVersion = bundleVersion,
                expectedAppType = "android",
                expectedFileName = "metadata.json",
            )?.lowercase()
            if (expectedMetadataSha256.isNullOrEmpty()) {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: metadata signature GPG verification failed")
                throw BundleUpdateError("INTEGRITY_FAILED", "verifyASC")
            }
        }

        // a. Verify .diff.zip SHA-256 before checking isInstalled so a corrupted
        // managed diff archive cannot pass ASC verification.
        if (File(resolvedDiffZipPath).length() > BundleDiffReconstruction.MAX_BUNDLE_ARCHIVE_BYTES) {
            throw BundleUpdateError("SECURITY_REJECTED", "verifyASC")
        }
        if (!skipGPG) {
            val actualDiffZipSha = BundleUpdateStoreAndroid.calculateSHA256(resolvedDiffZipPath)
            if (actualDiffZipSha == null || !BundleDiffReconstruction.constantTimeEqualHex(actualDiffZipSha, diffSha256)) {
                throw BundleDiffReconstructionException.FallbackRequired("diff zip sha256 mismatch")
            }
        }

        if (isInstalled) {
            if (!skipGPG && !BundleUpdateStoreAndroid.validateMetadataFileSha256(context, folderName, signature, appVersion, bundleVersion)) {
                throw BundleUpdateError("INTEGRITY_FAILED", "verifyASC")
            }
            val destination = destinationDir.canonicalPath
            val installedMetaFile = File(destination, "metadata.json")
            val installedMetaSha = BundleUpdateStoreAndroid.calculateSHA256(installedMetaFile.absolutePath)
                ?: throw BundleUpdateError("INTEGRITY_FAILED", "verifyASC")
            val metadata = BundleUpdateStoreAndroid.parseMetadataJson(
                BundleUpdateStoreAndroid.readFileContent(installedMetaFile)
            )
            if (!BundleUpdateStoreAndroid.validateAllFilesInDir(context, destination, metadata, appVersion, bundleVersion) ||
                !BundleUpdateStoreAndroid.validateBundlePairCompatibility(context, destination, metadata)
            ) {
                throw BundleUpdateError("INTEGRITY_FAILED", "verifyASC")
            }
            return installedMetaSha.lowercase()
        }

        // b. Extract .diff.zip into isolated staging directory (enforces entry count, size, symlink, traversal checks)
        stagingDir.parentFile?.mkdirs()
        try {
            BundleUpdateStoreAndroid.unzipFile(resolvedDiffZipPath, stagingPath)
        } catch (e: SecurityException) {
            throw BundleDiffReconstructionException.SecurityRejected(e.message ?: "diff zip security violation")
        } catch (e: Exception) {
            throw BundleDiffReconstructionException.FallbackRequired("diff zip extraction failed: ${e.message}")
        }

        operation.ensureActive()

        if (!BundleCryptoCore.validateExtractedPathSafety(stagingPath)) {
            throw BundleDiffReconstructionException.SecurityRejected("symlink or path traversal in diff staging")
        }

        // c. Verify diff-manifest.json byte cap and streaming SHA-256 BEFORE reading into memory
        val diffManifestFile = File(stagingDir, "diff-manifest.json")
        if (!diffManifestFile.exists() || !diffManifestFile.isFile) {
            throw BundleDiffReconstructionException.FallbackRequired("diff-manifest.json missing")
        }
        if (diffManifestFile.length() > BundleDiffReconstruction.MAX_CONTROL_JSON_BYTES) {
            throw BundleDiffReconstructionException.SecurityRejected("diff-manifest.json exceeds size cap")
        }
        if (!skipGPG && expectedDiffManifestSha256 != null) {
            val actualManifestSha = BundleUpdateStoreAndroid.calculateSHA256(diffManifestFile.absolutePath)
            if (actualManifestSha == null || !BundleDiffReconstruction.constantTimeEqualHex(actualManifestSha, expectedDiffManifestSha256)) {
                throw BundleDiffReconstructionException.IntegrityFailed("diff-manifest.json sha256 mismatch")
            }
        }

        // d & e. Parse & validate diff-manifest.json, verify metadata.json streaming SHA-256 BEFORE reading into memory
        val manifestContent = BundleUpdateStoreAndroid.readFileContent(diffManifestFile)
        val manifest = BundleDiffReconstruction.parseAndValidateManifest(
            jsonContent = manifestContent,
            expectedAppType = "android",
            expectedAppVersion = appVersion,
            expectedBundleVersion = bundleVersion,
        )

        val stagedMetadataFile = File(stagingDir, "metadata.json")
        if (!stagedMetadataFile.exists() || !stagedMetadataFile.isFile) {
            throw BundleDiffReconstructionException.FallbackRequired("staged metadata.json missing")
        }
        if (stagedMetadataFile.length() > BundleDiffReconstruction.MAX_CONTROL_JSON_BYTES) {
            throw BundleDiffReconstructionException.SecurityRejected("staged metadata.json exceeds size cap")
        }
        val actualMetadataSha = BundleUpdateStoreAndroid.calculateSHA256(stagedMetadataFile.absolutePath)?.lowercase()
            ?: throw BundleDiffReconstructionException.FallbackRequired("failed to hash staged metadata.json")
        if (!skipGPG && expectedMetadataSha256 != null) {
            if (!BundleDiffReconstruction.constantTimeEqualHex(actualMetadataSha, expectedMetadataSha256)) {
                throw BundleDiffReconstructionException.IntegrityFailed("metadata.json gpg sha256 mismatch")
            }
        }
        if (!BundleDiffReconstruction.constantTimeEqualHex(actualMetadataSha, manifest.targetMetadataSha256)) {
            throw BundleDiffReconstructionException.IntegrityFailed("metadata.json targetMetadataSha256 mismatch")
        }

        val targetMetadata = try {
            BundleUpdateStoreAndroid.parseMetadataJson(
                BundleUpdateStoreAndroid.readFileContent(stagedMetadataFile),
            )
        } catch (e: Exception) {
            throw BundleDiffReconstructionException.FallbackRequired("failed to parse staged metadata.json: ${e.message}")
        }
        val targetFileEntries = BundleUpdateStoreAndroid.getFileMetadataEntries(targetMetadata)
        BundleDiffReconstruction.validateManifestAgainstTargetMetadata(manifest, targetFileEntries)

        operation.ensureActive()

        // f & g. Verify changedFiles and reconstruct reusedFiles
        val candidateDirs = collectCandidateOtaDirs(context, appVersion, folderName)
        BundleDiffReconstruction.reconstruct(
            stagingDir = stagingDir,
            manifest = manifest,
            candidateBundleDirs = candidateDirs,
            sha256OfFile = { BundleUpdateStoreAndroid.calculateSHA256(it) },
            fileSystem = AndroidBundleFileSystem,
            builtinCopy = { relPath, destFile, expectedHash ->
                copyBuiltinAssetFile(context, relPath, destFile, expectedHash)
            },
        )

        operation.ensureActive()

        // h. Delete diff-manifest.json before full directory validation
        diffManifestFile.delete()

        // i. Validate all files in .diff-staging and bundle compatibility
        if (!BundleCryptoCore.validateExtractedPathSafety(stagingPath)) {
            throw BundleDiffReconstructionException.SecurityRejected("path safety check failed after reconstruction")
        }
        if (!BundleUpdateStoreAndroid.validateAllFilesInDir(context, stagingPath, targetMetadata, appVersion, bundleVersion, allowStaging = true)) {
            throw BundleDiffReconstructionException.FallbackRequired("final diff-staging file verification failed")
        }
        if (!BundleUpdateStoreAndroid.validateBundlePairCompatibility(context, stagingPath, targetMetadata)) {
            throw BundleUpdateError("INCOMPATIBLE", "verifyASC")
        }

        return actualMetadataSha
    }

    override fun verifyBundleASC(params: BundleVerifyASCParams): Promise<Unit> {
        return updatePromise("verifyASC") {
            val appVersion = params.latestVersion
            val bundleVersion = params.bundleVersion
            if (!isSafeVersionString(appVersion) || !isSafeVersionString(bundleVersion)) {
                throw BundleUpdateError("INVALID_ARGUMENT", "verifyASC")
            }

            val sha256 = BundleDiffReconstruction.normalizeSha256(params.sha256) ?: params.sha256.lowercase()
            val signature = params.signature
            val context = getContext()
            val release = ReleaseIdentity(appVersion, bundleVersion)
            val folderName = release.key
            val handle = ConcurrentRangeDownloader.CancelHandle()
            val operation = startOperation(context, release, handle)
            var retainedStaging: File? = null
            try {
                operation.ensureActive()
                val downloadDir = File(BundleUpdateStoreAndroid.getDownloadBundleDir(context))
                val fullFile = File(downloadDir, "$folderName.zip")
                val diffDir = File(downloadDir, BundleDiffReconstruction.DIFF_DOWNLOAD_DIR_NAME)
                val diffFile = File(diffDir, "$folderName${BundleDiffReconstruction.DIFF_ZIP_EXTENSION}")
                val diffMetaFile = File(diffDir, "$folderName${BundleDiffReconstruction.DIFF_META_EXTENSION}")
                val resolvedFullPath = resolveManagedFile(downloadDir, fullFile, "verifyASC")

                val isInstalled = synchronized(BundleUpdateStoreAndroid.activationLock) {
                    BundleUpdateStoreAndroid.isInstalled(context, appVersion, bundleVersion)
                }
                val bundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context))
                val destinationDir = File(bundleDir, folderName)
                resolveManagedFile(bundleDir, destinationDir, "verifyASC")
                val stagingRootDir = File(bundleDir, BundleDiffReconstruction.STAGING_ROOT_DIR_NAME)
                val runId = java.util.UUID.randomUUID().toString().replace("-", "")
                val stagingDir = File(stagingRootDir, "$folderName-$runId")
                val stagingPath = resolveManagedFile(bundleDir, stagingDir, "verifyASC")
                val skipGPG = BuildConfig.ALLOW_SKIP_GPG_VERIFICATION && isDevSettingsEnabled() && isSkipGPGEnabled()

                BundleDiffReconstruction.sweepStagingDirectory(stagingRootDir, AndroidBundleFileSystem)
                BundleDiffReconstruction.retainStagingDirectory(stagingDir)
                retainedStaging = stagingDir

                fun verifyFullZip(downloadedFallback: Boolean = false) {
                    verifyAndExtractFullZip(
                        context = context,
                        resolvedFilePath = resolvedFullPath,
                        verificationDir = if (isInstalled) destinationDir else stagingDir,
                        release = release,
                        sha256 = sha256,
                        signature = signature,
                        isInstalled = isInstalled,
                        skipGPG = skipGPG,
                        operation = operation,
                    )
                    publishASC(operation, context, release, promote = !isInstalled) {
                        if (!isInstalled) {
                            BundleCryptoCore.safeRename(stagingPath, destinationDir.absolutePath)
                        }
                        diffMetaFile.delete()
                        diffVerifiedMetadataByRelease.remove(release)
                    }
                    if (downloadedFallback) sendEvent("update/complete")
                }

                // The completed full archive takes precedence over any diff state.
                if (File(resolvedFullPath).isFile) {
                    verifyFullZip()
                    return@updatePromise
                }

                val resolvedDiffPath = resolveManagedFile(downloadDir, diffFile, "verifyASC")
                resolveManagedFile(downloadDir, diffMetaFile, "verifyASC")
                val normalizedDiffSha = BundleDiffReconstruction.normalizeSha256(params.diffSha256)
                val descriptor = BundleDiffReconstruction.readDescriptor(diffMetaFile, appVersion, bundleVersion)

                fun downloadAndVerifyFullFallback() {
                    operation.ensureActive()
                    val inMemory = fullBundleParamsByRelease[release]?.let {
                        Triple(it.downloadUrl, it.fileSize, it.sha256)
                    }
                    val fallbackParams = BundleDiffReconstruction.resolveFallbackDownloadParams(
                        expectedFullSha256 = sha256,
                        expectedDiffSha256 = normalizedDiffSha,
                        explicitDownloadUrl = params.downloadUrl,
                        explicitFileSize = params.fileSize,
                        inMemoryFullParams = inMemory,
                        descriptor = descriptor,
                    ) ?: throw BundleUpdateError("PACKAGE_MISSING", "verifyASC")
                    val matchingDescriptor = descriptor?.takeIf { it.sha256 == sha256 && it.diffSha256 == normalizedDiffSha }
                    val fallbackDescriptor = BundleDiffReconstruction.DiffArchiveDescriptor(
                        appVersion = appVersion,
                        bundleVersion = bundleVersion,
                        downloadUrl = fallbackParams.first,
                        fileSize = fallbackParams.second,
                        sha256 = sha256,
                        diffSha256 = normalizedDiffSha ?: descriptor?.takeIf { it.sha256 == sha256 }?.diffSha256 ?: sha256,
                        signature = matchingDescriptor?.signature,
                        fallbackRequired = true,
                    )
                    if (!BundleDiffReconstruction.writeDescriptor(fallbackDescriptor, diffMetaFile, AndroidBundleFileSystem)) {
                        throw BundleUpdateError("STORAGE_ERROR", "verifyASC")
                    }
                    operation.ensureActive()
                    diffFallbackDigests[release] = sha256
                    diffFile.delete()
                    val diffPartial = "${diffFile.absolutePath}.partial"
                    File(diffPartial).delete()
                    File("$diffPartial.progress").delete()
                    for (i in 0 until CONCURRENT_SEGMENT_COUNT) File("$diffPartial.seg$i").delete()

                    BundleDiffReconstruction.releaseStagingDirectory(stagingDir, AndroidBundleFileSystem)
                    BundleDiffReconstruction.retainStagingDirectory(stagingDir)
                    operation.ensureActive()
                    downloadFileWithResumeAndVerify(
                        downloadUrl = fallbackParams.first,
                        filePath = fullFile.absolutePath,
                        expectedSize = fallbackParams.second.toLong().coerceAtLeast(0L),
                        sha256 = sha256,
                        handle = handle,
                        step = "verifyASC",
                    )
                    verifyFullZip(downloadedFallback = true)
                }

                val diffAvailable = File(resolvedDiffPath).isFile
                if (diffAvailable && normalizedDiffSha != null && (skipGPG || !params.diffSignature.isNullOrEmpty())) {
                    val matchingDescriptor = descriptor?.takeIf {
                        BundleDiffReconstruction.resolveExpectedDiffSha256ForPackageVerify(sha256, normalizedDiffSha, it) != null
                    }
                    if (matchingDescriptor == null) {
                        downloadAndVerifyFullFallback()
                        return@updatePromise
                    }
                    try {
                        val metadataSha = reconstructAndVerifyDiffZip(
                            context = context,
                            resolvedDiffZipPath = resolvedDiffPath,
                            stagingDir = stagingDir,
                            stagingPath = stagingPath,
                            destinationDir = destinationDir,
                            release = release,
                            diffSha256 = normalizedDiffSha,
                            diffSignature = params.diffSignature.orEmpty(),
                            signature = signature,
                            isInstalled = isInstalled,
                            skipGPG = skipGPG,
                            operation = operation,
                        )
                        publishASC(operation, context, release, promote = !isInstalled) {
                            if (!skipGPG && !BundleDiffReconstruction.writeDescriptor(
                                    matchingDescriptor.copy(signature = signature), diffMetaFile, AndroidBundleFileSystem,
                                )
                            ) {
                                throw BundleUpdateError("STORAGE_ERROR", "verifyASC")
                            }
                            if (!isInstalled) {
                                try {
                                    BundleCryptoCore.safeRename(stagingPath, destinationDir.absolutePath)
                                } catch (e: Exception) {
                                    throw BundleDiffReconstructionException.FallbackRequired("Failed to promote diff staging: ${e.message}")
                                }
                            }
                            if (skipGPG) {
                                diffVerifiedMetadataByRelease.remove(release)
                            } else {
                                diffVerifiedMetadataByRelease[release] = BundleDiffReconstruction.VerifiedDiffMetadata(
                                    fullSha256 = sha256,
                                    diffSha256 = normalizedDiffSha,
                                    metadataSha256 = metadataSha,
                                )
                            }
                        }
                        return@updatePromise
                    } catch (e: BundleDiffReconstructionException.IntegrityFailed) {
                        OneKeyLog.error("BundleUpdate", "verifyBundleASC: diff integrity failed: ${e.message}")
                        throw BundleUpdateError("INTEGRITY_FAILED", "verifyASC")
                    } catch (e: BundleDiffReconstructionException.SecurityRejected) {
                        OneKeyLog.error("BundleUpdate", "verifyBundleASC: diff security rejected: ${e.message}")
                        throw BundleUpdateError("SECURITY_REJECTED", "verifyASC")
                    } catch (e: BundleDiffReconstructionException.FallbackRequired) {
                        OneKeyLog.warn("BundleUpdate", "verifyBundleASC: diff reconstruction failed (${e.message}), falling back to full bundle")
                        downloadAndVerifyFullFallback()
                        return@updatePromise
                    }
                }

                if (diffFallbackDigests[release] == sha256 ||
                    (descriptor?.fallbackRequired == true && descriptor.sha256 == sha256)
                ) {
                    downloadAndVerifyFullFallback()
                    return@updatePromise
                }
                throw BundleUpdateError("PACKAGE_MISSING", "verifyASC")
            } catch (e: Exception) {
                operation.ensureActive()
                throw e
            } finally {
                try {
                    retainedStaging?.let { BundleDiffReconstruction.releaseStagingDirectory(it, AndroidBundleFileSystem) }
                } finally {
                    operations.finish(operation)
                }
            }
        }
    }

    private fun publishASC(
        operation: BundleUpdateOperations.Operation,
        context: Context,
        release: ReleaseIdentity,
        promote: Boolean,
        publish: () -> Unit,
    ) = synchronized(BundleUpdateStoreAndroid.activationLock) {
        operations.publish(operation) {
            if (promote && BundleUpdateStoreAndroid.isInstalled(context, release.appVersion, release.bundleVersion)) {
                throw BundleUpdateError("INCOMPATIBLE", "verifyASC")
            }
            publish()
        }
    }

    private fun activationPromise(work: () -> Unit): Promise<Unit> = updatePromise("install") {
        synchronized(BundleUpdateStoreAndroid.activationLock, work)
    }

    private fun resolveManagedFile(root: File, file: File, step: String): String =
        resolveManagedBundlePath(root, file, AndroidBundleFileSystem)
            ?: throw BundleUpdateError("SECURITY_REJECTED", step)

    override fun verifyBundle(params: BundleVerifyParams): Promise<Unit> {
        return updatePromise("verifyPackage") {
            val sha256 = BundleDiffReconstruction.normalizeSha256(params.sha256) ?: params.sha256.lowercase()
            val appVersion = params.latestVersion
            val bundleVersion = params.bundleVersion

            if (!isSafeVersionString(appVersion) || !isSafeVersionString(bundleVersion)) {
                throw BundleUpdateError("INVALID_ARGUMENT", "verifyPackage")
            }

            val context = getContext()
            val release = ReleaseIdentity(appVersion, bundleVersion)
            val folderName = release.key
            val operation = startOperation(context, release)
            try {
                operation.ensureActive()
                val downloadDir = File(BundleUpdateStoreAndroid.getDownloadBundleDir(context))
                val filePath = File(downloadDir, "$folderName.zip").absolutePath
                val resolvedFilePath = resolveManagedFile(downloadDir, File(filePath), "verifyPackage")
                if (File(resolvedFilePath).isFile) {
                    if (File(resolvedFilePath).length() > BundleDiffReconstruction.MAX_BUNDLE_ARCHIVE_BYTES) {
                        throw BundleUpdateError("SECURITY_REJECTED", "verifyPackage")
                    }
                    OneKeyLog.info("BundleUpdate", "verifyBundle: appVersion=$appVersion, bundleVersion=$bundleVersion, file=$filePath")

                    val calculated = BundleUpdateStoreAndroid.calculateSHA256(resolvedFilePath)
                    if (calculated == null) {
                        OneKeyLog.error("BundleUpdate", "verifyBundle: failed to calculate SHA256 for file=$filePath")
                        throw BundleUpdateError("STORAGE_ERROR", "verifyPackage")
                    }
                    if (!BundleUpdateStoreAndroid.secureCompare(calculated.lowercase(), sha256)) {
                        OneKeyLog.error("BundleUpdate", "verifyBundle: SHA256 mismatch, expected=${sha256.take(16)}..., got=${calculated.take(16)}...")
                        throw BundleUpdateError("INTEGRITY_FAILED", "verifyPackage")
                    }

                    OneKeyLog.info("BundleUpdate", "verifyBundle: SHA256 verified OK for appVersion=$appVersion, bundleVersion=$bundleVersion")
                    operations.publish(operation)
                    return@updatePromise
                }

                val diffDir = File(downloadDir, BundleDiffReconstruction.DIFF_DOWNLOAD_DIR_NAME)
                val diffFilePath = File(diffDir, "$folderName${BundleDiffReconstruction.DIFF_ZIP_EXTENSION}").absolutePath
                val diffMetaFile = File(diffDir, "$folderName${BundleDiffReconstruction.DIFF_META_EXTENSION}")
                val resolvedDiffPath = resolveManagedFile(downloadDir, File(diffFilePath), "verifyPackage")
                val diffZip = File(resolvedDiffPath)
                if (!diffZip.isFile) {
                    throw BundleUpdateError("PACKAGE_MISSING", "verifyPackage")
                }
                if (diffZip.length() > BundleDiffReconstruction.MAX_BUNDLE_ARCHIVE_BYTES) {
                    throw BundleUpdateError("SECURITY_REJECTED", "verifyPackage")
                }
                resolveManagedFile(downloadDir, diffMetaFile, "verifyPackage")
                val descriptor = BundleDiffReconstruction.readDescriptor(diffMetaFile, appVersion, bundleVersion)
                val bundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context))
                val bundleDirPath = File(bundleDir, folderName)
                resolveManagedFile(bundleDir, bundleDirPath, "verifyPackage")
                val metadataFile = File(bundleDirPath, "metadata.json")
                resolveManagedFile(bundleDir, metadataFile, "verifyPackage")
                val metadataSize = try {
                    BundleDiffReconstruction.targetMetadataSizeIfPresent(bundleDirPath, metadataFile, AndroidBundleFileSystem)
                } catch (_: BundleDiffReconstructionException.IntegrityFailed) {
                    throw BundleUpdateError("INTEGRITY_FAILED", "verifyPackage")
                } catch (_: IOException) {
                    throw BundleUpdateError("STORAGE_ERROR", "verifyPackage")
                }
                val skipGPG = BuildConfig.ALLOW_SKIP_GPG_VERIFICATION && isDevSettingsEnabled() && isSkipGPGEnabled()

                if (metadataSize != null) {
                    if (metadataSize > BundleDiffReconstruction.MAX_CONTROL_JSON_BYTES) {
                        throw BundleUpdateError("SECURITY_REJECTED", "verifyPackage")
                    }
                    val liveRecord = diffVerifiedMetadataByRelease[release]
                    val matchingDescriptor = descriptor?.takeIf {
                        BundleDiffReconstruction.resolveExpectedDiffSha256ForPackageVerify(sha256, params.diffSha256, it) != null
                    }
                    if (liveRecord?.matches(sha256, params.diffSha256) != true && matchingDescriptor == null) {
                        throw BundleUpdateError("INTEGRITY_FAILED", "verifyPackage")
                    }
                    if (!skipGPG) {
                        val expectedMetaSha = BundleDiffReconstruction.resolveTrustedMetadataSha256ForPackageVerify(
                            suppliedFullSha256 = sha256,
                            suppliedSignature = params.signature,
                            suppliedDiffSha256 = params.diffSha256,
                            inMemoryVerified = liveRecord,
                            descriptor = matchingDescriptor,
                            persistedAscSignature = BundleUpdateStoreAndroid.readSignatureFile(context, folderName),
                            verifySignatureAndExtractMetadataSha256 = { sig ->
                                BundleUpdateStoreAndroid.readMetadataFileSha256(
                                    sig,
                                    expectedAppVersion = appVersion,
                                    expectedBundleVersion = bundleVersion,
                                    expectedAppType = "android",
                                    expectedFileName = "metadata.json",
                                )
                            },
                        ) ?: run {
                            OneKeyLog.error("BundleUpdate", "verifyBundle: missing or invalid trust anchor for reconstructed bundle $folderName")
                            throw BundleUpdateError("INTEGRITY_FAILED", "verifyPackage")
                        }
                        val actualMetaSha = BundleUpdateStoreAndroid.calculateSHA256(metadataFile.absolutePath)
                        if (actualMetaSha == null || !BundleDiffReconstruction.constantTimeEqualHex(actualMetaSha, expectedMetaSha)) {
                            throw BundleUpdateError("INTEGRITY_FAILED", "verifyPackage")
                        }
                    }
                    val metadata = BundleUpdateStoreAndroid.parseMetadataJson(
                        BundleUpdateStoreAndroid.readFileContent(metadataFile)
                    )
                    if (!BundleCryptoCore.validateExtractedPathSafety(bundleDirPath.canonicalPath) ||
                        !BundleUpdateStoreAndroid.validateAllFilesInDir(context, bundleDirPath.canonicalPath, metadata, appVersion, bundleVersion)
                    ) {
                        throw BundleUpdateError("INTEGRITY_FAILED", "verifyPackage")
                    }
                    if (!BundleUpdateStoreAndroid.validateBundlePairCompatibility(context, bundleDirPath.canonicalPath, metadata)) {
                        throw BundleUpdateError("INCOMPATIBLE", "verifyPackage")
                    }
                    OneKeyLog.info("BundleUpdate", "verifyBundle: diff-reconstructed bundle verified OK for appVersion=$appVersion, bundleVersion=$bundleVersion")
                    operations.publish(operation)
                    return@updatePromise
                }

                // Before reconstruction, verify the downloaded archive against its record.
                val expectedDiffSha = BundleDiffReconstruction.resolveExpectedDiffSha256ForPackageVerify(
                    suppliedFullSha256 = sha256,
                    suppliedDiffSha256 = params.diffSha256,
                    descriptor = descriptor,
                ) ?: run {
                    OneKeyLog.error("BundleUpdate", "verifyBundle: missing or mismatched expected diffSha256 for $folderName")
                    throw BundleUpdateError("INTEGRITY_FAILED", "verifyPackage")
                }
                val calculatedDiffSha = BundleUpdateStoreAndroid.calculateSHA256(resolvedDiffPath)
                    ?: throw BundleUpdateError("STORAGE_ERROR", "verifyPackage")
                if (!BundleDiffReconstruction.constantTimeEqualHex(calculatedDiffSha, expectedDiffSha)) {
                    throw BundleUpdateError("INTEGRITY_FAILED", "verifyPackage")
                }
                OneKeyLog.info("BundleUpdate", "verifyBundle: downloaded diff archive verified OK for $folderName")
                operations.publish(operation)
            } catch (e: Exception) {
                operation.ensureActive()
                throw e
            } finally {
                operations.finish(operation)
            }
        }
    }

    override fun installBundle(params: BundleInstallParams): Promise<Unit> {
        // Invalidate up front so any concurrent reader misses the cache while
        // the install is running. We invalidate again after the async body
        // commits so a same-version reinstall (or any reader that re-cached
        // pre-commit data inside the race window) can't leave stale entries.
        BundleUpdateStoreAndroid.invalidateValidatedBundleInfoCache()
        return activationPromise {
            val appVersion = params.latestVersion
            val bundleVersion = params.bundleVersion
            val signature = params.signature

            if (!isSafeVersionString(appVersion) || !isSafeVersionString(bundleVersion)) {
                throw BundleUpdateError("INVALID_ARGUMENT", "install")
            }

            val context = getContext()
            OneKeyLog.info("BundleUpdate", "installBundle: appVersion=$appVersion, bundleVersion=$bundleVersion, signatureLength=${signature.length}")

            // GPG verification skipped only when both DevSettings and skip-GPG toggle are enabled
            val skipGPG = BuildConfig.ALLOW_SKIP_GPG_VERIFICATION && isDevSettingsEnabled() && isSkipGPGEnabled()
            OneKeyLog.info("BundleUpdate", "installBundle: GPG check: skipGPG=$skipGPG")

            val folderName = "$appVersion-$bundleVersion"
            // Verify bundle directory exists and is canonically confined inside getBundleDir(context)
            val bundleBaseCanonical = File(BundleUpdateStoreAndroid.getBundleDir(context)).canonicalPath
            val bundleDirPath = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            if (AndroidBundleFileSystem.isSymbolicLink(bundleDirPath) ||
                !bundleDirPath.canonicalPath.startsWith(bundleBaseCanonical + File.separator)
            ) {
                throw BundleUpdateError("SECURITY_REJECTED", "install")
            }
            val ascBaseCanonical = File(BundleUpdateStoreAndroid.getAscDir(context)).canonicalPath
            val sigFile = File(BundleUpdateStoreAndroid.getSignatureFilePath(context, folderName))
            if (AndroidBundleFileSystem.isSymbolicLink(sigFile) ||
                !sigFile.canonicalPath.startsWith(ascBaseCanonical + File.separator)
            ) {
                throw BundleUpdateError("SECURITY_REJECTED", "install")
            }
            if (!bundleDirPath.exists()) {
                OneKeyLog.error("BundleUpdate", "installBundle: bundle directory not found: ${bundleDirPath.absolutePath}")
                throw BundleUpdateError("PACKAGE_MISSING", "install")
            }

            val mainBundleFile = File(bundleDirPath, "main.jsbundle.hbc")
            val metadataFile = File(bundleDirPath, "metadata.json")
            if (!mainBundleFile.exists() || !metadataFile.exists()) {
                OneKeyLog.error("BundleUpdate", "installBundle: required bundle files missing in: ${bundleDirPath.absolutePath}")
                throw BundleUpdateError("PACKAGE_MISSING", "install")
            }

            if (!skipGPG) {
                if (signature.isEmpty() || !BundleUpdateStoreAndroid.validateMetadataFileSha256(context, folderName, signature, appVersion, bundleVersion)) {
                    OneKeyLog.error("BundleUpdate", "installBundle: GPG signature verification failed for $folderName")
                    throw BundleUpdateError("INTEGRITY_FAILED", "install")
                }
            }

            val metadata = BundleUpdateStoreAndroid.parseMetadataJson(BundleUpdateStoreAndroid.readFileContent(metadataFile))
            if (!BundleUpdateStoreAndroid.validateAllFilesInDir(context, bundleDirPath.absolutePath, metadata, appVersion, bundleVersion)) {
                OneKeyLog.error("BundleUpdate", "installBundle: extracted file verification failed for $folderName")
                throw BundleUpdateError("INTEGRITY_FAILED", "install")
            }
            if (!BundleUpdateStoreAndroid.validateBundlePairCompatibility(context, bundleDirPath.absolutePath, metadata)) {
                OneKeyLog.error("BundleUpdate", "installBundle: bundle pair compatibility check failed for $folderName")
                throw BundleUpdateError("INCOMPATIBLE", "install")
            }

            val currentFolderName = BundleUpdateStoreAndroid.getCurrentBundleVersion(context)
            val currentSignature = if (currentFolderName != null) BundleUpdateStoreAndroid.readSignatureFile(context, currentFolderName) else ""

            BundleUpdateStoreAndroid.setCurrentBundleVersionAndSignature(context, folderName, signature)
            val nativeVersion = BundleUpdateStoreAndroid.getAppVersion(context) ?: ""
            BundleUpdateStoreAndroid.setNativeVersion(context, nativeVersion)
            val buildNumber = BundleUpdateStoreAndroid.getBuildNumber(context)
            BundleUpdateStoreAndroid.setNativeBuildNumber(context, buildNumber)

            // Manage fallback data
            try {
                val previousVersion = currentFolderName?.let {
                    BundleUpdateStoreAndroid.bundleVersionInFolder(it, appVersion)
                }
                val previous = if (currentFolderName != folderName && previousVersion != null && currentSignature.isNotEmpty()) {
                    mapOf("appVersion" to appVersion, "bundleVersion" to previousVersion, "signature" to currentSignature)
                } else {
                    null
                }
                val (fallbackData, evicted) = planFallbackBundles(
                    BundleUpdateStoreAndroid.readFallbackUpdateBundleDataFile(context), appVersion, bundleVersion, previous,
                )
                BundleUpdateStoreAndroid.writeFallbackUpdateBundleDataFile(fallbackData, context)
                for (entry in evicted) {
                    val shiftApp = entry["appVersion"] ?: continue
                    val shiftBundle = entry["bundleVersion"] ?: continue
                    val shiftFolderName = "$shiftApp-$shiftBundle"
                    if (shiftFolderName != folderName && shiftFolderName != currentFolderName) {
                        val oldDir = File(BundleUpdateStoreAndroid.getBundleDir(context), shiftFolderName)
                        if (oldDir.exists()) {
                            BundleUpdateStoreAndroid.deleteDir(oldDir)
                        }
                        BundleUpdateStoreAndroid.deleteSignatureFile(context, shiftFolderName)
                    }
                }
                OneKeyLog.info("BundleUpdate", "installBundle: completed successfully, installed version=$folderName, fallbackCount=${fallbackData.size}")
            } catch (e: Exception) {
                OneKeyLog.error("BundleUpdate", "installBundle: fallbackUpdateBundleData error: ${e.message}")
            }
            // Second invalidate: closes the race window between the up-front
            // invalidate and the actual currentBundleVersion write. Without
            // this, a reader entering getValidatedCurrentBundleInfo() between
            // the two could re-cache pre-install state and leave it stale.
            BundleUpdateStoreAndroid.invalidateValidatedBundleInfoCache()
        }
    }

    private fun clearDownloadFiles(context: Context, includeInstalled: Boolean, afterClear: () -> Unit = {}) {
        val cleared = operations.cleanup(65, TimeUnit.SECONDS) {
            diffFallbackDigests.clear()
            fullBundleParamsByRelease.clear()
            if (includeInstalled) diffVerifiedMetadataByRelease.clear()

            val downloadDir = File(BundleUpdateStoreAndroid.getDownloadBundleDir(context))
            if (downloadDir.exists() && !BundleUpdateStoreAndroid.deleteDir(downloadDir)) {
                throw BundleUpdateError("STORAGE_ERROR", "cleanup")
            }
            val bundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context))
            if (includeInstalled) {
                if (bundleDir.exists() && !BundleUpdateStoreAndroid.deleteDir(bundleDir)) {
                    throw BundleUpdateError("STORAGE_ERROR", "cleanup")
                }
            } else {
                val stagingDir = File(bundleDir, BundleDiffReconstruction.STAGING_ROOT_DIR_NAME)
                if (stagingDir.exists()) BundleUpdateStoreAndroid.deleteDir(stagingDir)
            }
            afterClear()
        }
        if (!cleared) throw BundleUpdateError("UNKNOWN", "cleanup")
    }

    override fun clearDownload(): Promise<Unit> {
        return updatePromise("cleanup") {
            OneKeyLog.info("BundleUpdate", "clearDownload: clearing download directory...")
            val context = getContext()
            clearDownloadFiles(context, includeInstalled = false)
            OneKeyLog.info("BundleUpdate", "clearDownload: completed")
        }
    }

    override fun clearBundle(): Promise<Unit> {
        BundleUpdateStoreAndroid.invalidateValidatedBundleInfoCache()
        return updatePromise("cleanup") {
            OneKeyLog.info("BundleUpdate", "clearBundle: clearing download and bundle directories...")
            val context = getContext()
            clearDownloadFiles(context, includeInstalled = true)
            OneKeyLog.info("BundleUpdate", "clearBundle: completed")
        }
    }

    /**
     * Prunes every artifact whose appVersion != the running native binary
     * version: stale onekey-bundle/<v> dirs, onekey-bundle-download/<v> stages
     * (.zip / .partial / .progress / .resume), orphan asc signatures, and
     * lingering fallback entries. Hard-refuses to delete the current
     * appVersion's artifacts and the active currentBundleVersion. Tolerates
     * already-missing files. Returns the count of deleted version directories.
     *
     * The running app version is matched as an exact prefix before best-effort
     * classification of non-current stems.
     */
    override fun pruneStaleAppVersionBundles(): Promise<Double> {
        BundleUpdateStoreAndroid.invalidateValidatedBundleInfoCache()
        return Promise.async {
            val context = getContext()
            val currentAppV = BundleUpdateStoreAndroid.getAppVersion(context) ?: ""
            // Safety net: never delete the bundle backing the active pointer.
            val currentBundleVersion = BundleUpdateStoreAndroid.getCurrentBundleVersion(context)
            OneKeyLog.info("BundleUpdate", "pruneStaleAppVersionBundles: currentAppV=$currentAppV, currentBundleVersion=$currentBundleVersion")

            // Without a known native version we cannot decide what is stale;
            // bail out rather than risk deleting the wrong artifacts.
            if (currentAppV.isEmpty()) {
                OneKeyLog.warn("BundleUpdate", "pruneStaleAppVersionBundles: empty currentAppV, skipping")
                return@async 0.0
            }

            // Parses an "{appVersion}-{bundleVersion}" stem into its appVersion
            // component using the same last-dash split as listLocalBundles.
            // Returns null for stems without a dash or with an empty appVersion.
            fun appVersionFromStem(stem: String): String? {
                val lastDash = stem.lastIndexOf('-')
                if (lastDash <= 0) return null
                val appV = stem.substring(0, lastDash)
                return if (appV.isEmpty()) null else appV
            }

            // True when this entry stem must be kept: its appVersion matches the
            // running binary, it IS the active currentBundleVersion, or it is
            // unparseable (leave foreign names alone).
            fun shouldKeep(stem: String): Boolean {
                if (currentBundleVersion != null && stem == currentBundleVersion) return true
                if (BundleUpdateStoreAndroid.bundleVersionInFolder(stem, currentAppV) != null) return true
                val appV = appVersionFromStem(stem) ?: return true
                return appV == currentAppV
            }

            var deletedDirCount = 0

            // 1. onekey-bundle/* extracted dirs
            val bundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context))
            if (bundleDir.exists() && bundleDir.isDirectory) {
                bundleDir.listFiles()?.forEach { child ->
                    if (!child.isDirectory) return@forEach
                    val name = child.name
                    // Skip non-version entries (asc dir, fallback json, etc.)
                    if (name == "asc" || name == "fallbackUpdateBundleData.json") return@forEach
                    if (name == BundleDiffReconstruction.STAGING_ROOT_DIR_NAME) {
                        BundleDiffReconstruction.sweepStagingDirectory(child, AndroidBundleFileSystem)
                        return@forEach
                    }
                    if (shouldKeep(name)) return@forEach
                    try {
                        if (BundleUpdateStoreAndroid.deleteDir(child)) {
                            deletedDirCount++
                            OneKeyLog.info("BundleUpdate", "pruneStaleAppVersionBundles: deleted stale bundle dir $name")
                        } else {
                            OneKeyLog.warn("BundleUpdate", "pruneStaleAppVersionBundles: incomplete delete of bundle dir $name (left behind)")
                        }
                    } catch (e: Exception) {
                        OneKeyLog.warn("BundleUpdate", "pruneStaleAppVersionBundles: failed to delete bundle dir $name: ${e.message}")
                    }
                }
            }

            // 2. onekey-bundle-download/* stages (zip / partial / progress / resume) and .diff/* stages
            val downloadDir = File(BundleUpdateStoreAndroid.getDownloadBundleDir(context))
            if (downloadDir.exists() && downloadDir.isDirectory) {
                downloadDir.listFiles()?.forEach { file ->
                    val name = file.name
                    if (name == BundleDiffReconstruction.DIFF_DOWNLOAD_DIR_NAME) {
                        val type = try {
                            AndroidBundleFileSystem.lstat(file)?.type
                        } catch (e: IOException) {
                            OneKeyLog.warn("BundleUpdate", "pruneStaleAppVersionBundles: failed to inspect diff directory: ${e.message}")
                            return@forEach
                        }
                        if (type != BundleFileType.DIRECTORY) return@forEach
                        file.listFiles()?.forEach { diffFile ->
                            try {
                                BundleDiffReconstruction.pruneDiffDownload(diffFile, AndroidBundleFileSystem, ::shouldKeep)
                            } catch (e: Exception) {
                                OneKeyLog.warn("BundleUpdate", "pruneStaleAppVersionBundles: failed to delete diff download ${diffFile.name}: ${e.message}")
                            }
                        }
                        return@forEach
                    }
                    // Strip the trailing extension chain to recover the
                    // "{appV}-{bV}" stem (e.g. "6.3.0-123.zip.partial").
                    var stem = name
                    // Concurrent segment files end in ".segN" — peel that off
                    // first so the rest of the chain strips as usual.
                    stem = stem.replace(Regex("""\.seg\d+$"""), "")
                    for (suffix in listOf(".resume", ".progress", ".partial", ".zip")) {
                        if (stem.endsWith(suffix)) {
                            stem = stem.substring(0, stem.length - suffix.length)
                        }
                    }
                    if (shouldKeep(stem)) return@forEach
                    try {
                        val deleted = if (file.isDirectory) {
                            BundleUpdateStoreAndroid.deleteDir(file)
                        } else {
                            !file.exists() || file.delete()
                        }
                        if (deleted) {
                            OneKeyLog.info("BundleUpdate", "pruneStaleAppVersionBundles: deleted stale download $name")
                        } else {
                            OneKeyLog.warn("BundleUpdate", "pruneStaleAppVersionBundles: failed to delete download $name")
                        }
                    } catch (e: Exception) {
                        OneKeyLog.warn("BundleUpdate", "pruneStaleAppVersionBundles: failed to delete download $name: ${e.message}")
                    }
                }
            }

            // 3. onekey-bundle/asc/*-signature.asc orphan signatures
            val ascDir = File(BundleUpdateStoreAndroid.getAscDir(context))
            if (ascDir.exists() && ascDir.isDirectory) {
                val suffix = "-signature.asc"
                ascDir.listFiles()?.forEach { file ->
                    val name = file.name
                    if (!name.endsWith(suffix)) return@forEach
                    val stem = name.substring(0, name.length - suffix.length)
                    if (shouldKeep(stem)) return@forEach
                    try {
                        file.delete()
                        OneKeyLog.info("BundleUpdate", "pruneStaleAppVersionBundles: deleted orphan asc $name")
                    } catch (e: Exception) {
                        OneKeyLog.warn("BundleUpdate", "pruneStaleAppVersionBundles: failed to delete asc $name: ${e.message}")
                    }
                }
            }

            // 4. Persisted fallback list: drop entries whose appVersion != currentAppV.
            // Fixes the latent leak where stale fallback entries linger after a
            // native upgrade. Reuses the existing read/write helpers.
            try {
                val fallbackData = BundleUpdateStoreAndroid.readFallbackUpdateBundleDataFile(context)
                val prunedFallback = fallbackData.filter { entry ->
                    val appV = entry["appVersion"]
                    appV.isNullOrEmpty() || appV == currentAppV
                }
                if (prunedFallback.size != fallbackData.size) {
                    BundleUpdateStoreAndroid.writeFallbackUpdateBundleDataFile(prunedFallback, context)
                    OneKeyLog.info("BundleUpdate", "pruneStaleAppVersionBundles: pruned fallback entries ${fallbackData.size} -> ${prunedFallback.size}")
                }
            } catch (e: Exception) {
                OneKeyLog.warn("BundleUpdate", "pruneStaleAppVersionBundles: fallback prune error: ${e.message}")
            }

            OneKeyLog.info("BundleUpdate", "pruneStaleAppVersionBundles: completed, deletedDirCount=$deletedDirCount")
            deletedDirCount.toDouble()
        }
    }

    override fun resetToBuiltInBundle(): Promise<Unit> {
        BundleUpdateStoreAndroid.invalidateValidatedBundleInfoCache()
        return updatePromise("cleanup") {
            OneKeyLog.info("BundleUpdate", "resetToBuiltInBundle: clearing currentBundleVersion preference...")
            val context = getContext()
            val prefs = context.getSharedPreferences("BundleUpdatePrefs", Context.MODE_PRIVATE)
            val currentVersion = prefs.getString("currentBundleVersion", null)
            if (currentVersion != null) {
                prefs.edit().remove("currentBundleVersion").apply()
                OneKeyLog.info("BundleUpdate", "resetToBuiltInBundle: removed currentBundleVersion=$currentVersion")
            } else {
                OneKeyLog.info("BundleUpdate", "resetToBuiltInBundle: no currentBundleVersion set, already using built-in bundle")
            }
            OneKeyLog.info("BundleUpdate", "resetToBuiltInBundle: completed, app will use built-in bundle on next restart")
        }
    }

    override fun clearAllJSBundleData(): Promise<TestResult> {
        BundleUpdateStoreAndroid.invalidateValidatedBundleInfoCache()
        return updatePromise("cleanup") {
            OneKeyLog.info("BundleUpdate", "clearAllJSBundleData: starting...")
            val context = getContext()
            clearDownloadFiles(context, includeInstalled = true) {
                BundleUpdateStoreAndroid.clearUpdateBundleData(context)
                BundleUpdateStoreAndroid.clearNativeVersionPrefs(context)
            }

            OneKeyLog.info("BundleUpdate", "clearAllJSBundleData: completed successfully")
            TestResult(success = true, message = "Successfully cleared all JS bundle data")
        }
    }

    override fun getFallbackUpdateBundleData(): Promise<Array<FallbackBundleInfo>> {
        return Promise.async {
            val context = getContext()
            val data = BundleUpdateStoreAndroid.readFallbackUpdateBundleDataFile(context)
            val result = data.mapNotNull { map ->
                val appVersion = map["appVersion"] ?: return@mapNotNull null
                val bundleVersion = map["bundleVersion"] ?: return@mapNotNull null
                val signature = map["signature"] ?: return@mapNotNull null
                FallbackBundleInfo(appVersion = appVersion, bundleVersion = bundleVersion, signature = signature)
            }.toTypedArray()
            OneKeyLog.info("BundleUpdate", "getFallbackUpdateBundleData: found ${result.size} fallback entries")
            result
        }
    }

    override fun setCurrentUpdateBundleData(params: BundleSwitchParams): Promise<Unit> {
        BundleUpdateStoreAndroid.invalidateValidatedBundleInfoCache()
        return activationPromise {
            if (!isSafeVersionString(params.appVersion) || !isSafeVersionString(params.bundleVersion)) {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: invalid version string format: appVersion=${params.appVersion}, bundleVersion=${params.bundleVersion}")
                throw BundleUpdateError("INVALID_ARGUMENT", "install")
            }

            val context = getContext()
            val bundleVersion = "${params.appVersion}-${params.bundleVersion}"
            OneKeyLog.info("BundleUpdate", "setCurrentUpdateBundleData: switching to $bundleVersion")

            val baseBundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context)).canonicalPath
            val bundleDirPath = File(BundleUpdateStoreAndroid.getBundleDir(context), bundleVersion)
            if (AndroidBundleFileSystem.isSymbolicLink(bundleDirPath) ||
                !bundleDirPath.canonicalPath.startsWith(baseBundleDir + File.separator)
            ) {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: bundle path outside bundle directory: ${bundleDirPath.canonicalPath}")
                throw BundleUpdateError("SECURITY_REJECTED", "install")
            }
            val sigFile = File(BundleUpdateStoreAndroid.getSignatureFilePath(context, bundleVersion))
            val baseAscDir = File(BundleUpdateStoreAndroid.getAscDir(context)).canonicalPath
            if (AndroidBundleFileSystem.isSymbolicLink(sigFile) ||
                !sigFile.canonicalPath.startsWith(baseAscDir + File.separator)
            ) {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: signature path outside asc directory: ${sigFile.canonicalPath}")
                throw BundleUpdateError("SECURITY_REJECTED", "install")
            }

            // Verify the bundle directory actually exists
            if (!bundleDirPath.exists()) {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: bundle directory not found: ${bundleDirPath.absolutePath}")
                throw BundleUpdateError("PACKAGE_MISSING", "install")
            }

            // Verify GPG signature is valid (skipped when both DevSettings and skip-GPG toggle are enabled)
            val skipGPGSwitch = BuildConfig.ALLOW_SKIP_GPG_VERIFICATION && isDevSettingsEnabled() && isSkipGPGEnabled()
            OneKeyLog.info("BundleUpdate", "setCurrentUpdateBundleData: GPG check: skipGPG=$skipGPGSwitch")
            if (!skipGPGSwitch) {
                if (params.signature.isEmpty() ||
                    !BundleUpdateStoreAndroid.validateMetadataFileSha256(context, bundleVersion, params.signature, params.appVersion, params.bundleVersion)) {
                    OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: GPG signature verification failed")
                    throw BundleUpdateError("INTEGRITY_FAILED", "install")
                }
                OneKeyLog.info("BundleUpdate", "setCurrentUpdateBundleData: GPG signature verified OK")
            } else {
                OneKeyLog.warn("BundleUpdate", "setCurrentUpdateBundleData: GPG signature verification skipped (DevSettings + skip-GPG enabled)")
            }

            val metadataFile = File(bundleDirPath, "metadata.json")
            if (!metadataFile.exists()) {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: metadata.json not found in ${bundleDirPath.absolutePath}")
                throw BundleUpdateError("PACKAGE_MISSING", "install")
            }
            val metadata = BundleUpdateStoreAndroid.parseMetadataJson(BundleUpdateStoreAndroid.readFileContent(metadataFile))
            if (!BundleUpdateStoreAndroid.validateAllFilesInDir(context, bundleDirPath.absolutePath, metadata, params.appVersion, params.bundleVersion)) {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: extracted file verification failed")
                throw BundleUpdateError("INTEGRITY_FAILED", "install")
            }
            if (!BundleUpdateStoreAndroid.validateBundlePairCompatibility(context, bundleDirPath.absolutePath, metadata)) {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: bundle pair compatibility check failed")
                throw BundleUpdateError("INCOMPATIBLE", "install")
            }

            BundleUpdateStoreAndroid.setCurrentBundleVersionAndSignature(context, bundleVersion, params.signature)
            val currentNativeVersion = BundleUpdateStoreAndroid.getAppVersion(context) ?: ""
            BundleUpdateStoreAndroid.setNativeVersion(context, currentNativeVersion)
            val currentBuildNumber = BundleUpdateStoreAndroid.getBuildNumber(context)
            BundleUpdateStoreAndroid.setNativeBuildNumber(context, currentBuildNumber)
            BundleUpdateStoreAndroid.invalidateValidatedBundleInfoCache()
            OneKeyLog.info("BundleUpdate", "setCurrentUpdateBundleData: switched to $bundleVersion")
        }
    }

    override fun getWebEmbedPath(): String {
        val context = NitroModules.applicationContext ?: return ""
        val path = BundleUpdateStoreAndroid.getWebEmbedPath(context)
        OneKeyLog.debug("BundleUpdate", "getWebEmbedPath: $path")
        return path
    }

    override fun getWebEmbedPathAsync(): Promise<String> {
        return Promise.async {
            val context = getContext()
            val path = BundleUpdateStoreAndroid.getWebEmbedPath(context)
            OneKeyLog.debug("BundleUpdate", "getWebEmbedPathAsync: $path")
            path
        }
    }

    override fun getJsBundlePath(): String {
        val context = NitroModules.applicationContext ?: return ""
        val path = BundleUpdateStoreAndroid.getCurrentBundleMainJSBundle(context) ?: ""
        OneKeyLog.debug("BundleUpdate", "getJsBundlePath: ${if (path.isEmpty()) "(empty/no bundle)" else path}")
        return path
    }

    override fun getJsBundlePathAsync(): Promise<String> {
        return Promise.async {
            val context = getContext()
            val path = BundleUpdateStoreAndroid.getCurrentBundleMainJSBundle(context) ?: ""
            OneKeyLog.info("BundleUpdate", "getJsBundlePathAsync: ${if (path.isEmpty()) "(empty/no bundle)" else path}")
            path
        }
    }

    override fun getBackgroundJsBundlePath(): String {
        val context = NitroModules.applicationContext ?: return ""
        val path = BundleUpdateStoreAndroid.getCurrentBundleBackgroundJSBundle(context) ?: ""
        OneKeyLog.debug("BundleUpdate", "getBackgroundJsBundlePath: ${if (path.isEmpty()) "(empty/no bundle)" else path}")
        return path
    }

    override fun getBackgroundJsBundlePathAsync(): Promise<String> {
        return Promise.async {
            val context = getContext()
            val path = BundleUpdateStoreAndroid.getCurrentBundleBackgroundJSBundle(context) ?: ""
            OneKeyLog.info("BundleUpdate", "getBackgroundJsBundlePathAsync: ${if (path.isEmpty()) "(empty/no bundle)" else path}")
            path
        }
    }

    override fun getNativeAppVersion(): Promise<String> {
        return Promise.async {
            val context = getContext()
            val version = BundleUpdateStoreAndroid.getAppVersion(context) ?: ""
            OneKeyLog.info("BundleUpdate", "getNativeAppVersion: $version")
            version
        }
    }

    override fun getNativeBuildNumber(): Promise<String> {
        return Promise.async {
            val context = getContext()
            val buildNumber = BundleUpdateStoreAndroid.getBuildNumber(context)
            OneKeyLog.info("BundleUpdate", "getNativeBuildNumber: $buildNumber")
            buildNumber
        }
    }

    override fun getBuiltinBundleVersion(): Promise<String> {
        return Promise.async {
            val context = getContext()
            val version = BundleUpdateStoreAndroid.getBuiltinBundleVersion(context)
            OneKeyLog.info("BundleUpdate", "getBuiltinBundleVersion: $version")
            version
        }
    }

    override fun testVerification(): Promise<Boolean> {
        return Promise.async {
            val testSignature = """-----BEGIN PGP SIGNED MESSAGE-----
Hash: SHA256

{
  "fileName": "metadata.json",
  "sha256": "2ada9c871104fc40649fa3de67a7d8e33faadc18e9abd587e8bb85be0a003eba",
  "size": 158590,
  "generatedAt": "2025-09-19T07:49:13.000Z"
}
-----BEGIN PGP SIGNATURE-----

iQJCBAEBCAAsFiEE62iuVE8f3YzSZGJPs2mmepC/OHsFAmjNJ1IOHGRldkBvbmVr
ZXkuc28ACgkQs2mmepC/OHs6Rw/9FKHl5aNsE7V0IsFf/l+h16BYKFwVsL69alMk
CFLna8oUn0+tyECF6wKBKw5pHo5YR27o2pJfYbAER6dygDF6WTZ1lZdf5QcBMjGA
LCeXC0hzUBzSSOH4bKBTa3fHp//HdSV1F2OnkymbXqYN7WXvuQPLZ0nV6aU88hCk
HgFifcvkXAnWKoosUtj0Bban/YBRyvmQ5C2akxUPEkr4Yck1QXwzJeNRd7wMXHjH
JFK6lJcuABiB8wpJDXJkFzKs29pvHIK2B2vdOjU2rQzKOUwaKHofDi5C4+JitT2b
2pSeYP3PAxXYw6XDOmKTOiC7fPnfLjtcPjNYNFCezVKZT6LKvZW9obnW8Q9LNJ4W
okMPgHObkabv3OqUaTA9QNVfI/X9nvggzlPnaKDUrDWTf7n3vlrdexugkLtV/tJA
uguPlI5hY7Ue5OW7ckWP46hfmq1+UaIdeUY7dEO+rPZDz6KcArpaRwBiLPBhneIr
/X3KuMzS272YbPbavgCZGN9xJR5kZsEQE5HhPCbr6Nf0qDnh+X8mg0tAB/U6F+ZE
o90sJL1ssIaYvST+VWVaGRr4V5nMDcgHzWSF9Q/wm22zxe4alDaBdvOlUseW0iaM
n2DMz6gqk326W6SFynYtvuiXo7wG4Cmn3SuIU8xfv9rJqunpZGYchMd7nZektmEJ
91Js0rQ=
=A/Ii
-----END PGP SIGNATURE-----"""
            val result = BundleUpdateStoreAndroid.verifyGPGAndExtractSha256(testSignature, expectedAppType = null)
            val isValid = result == "2ada9c871104fc40649fa3de67a7d8e33faadc18e9abd587e8bb85be0a003eba"
            OneKeyLog.info("BundleUpdate", "testVerification: GPG verification result: $isValid")
            isValid
        }
    }

    override fun testSkipVerification(): Promise<Boolean> {
        return Promise.async {
            val result = if (BuildConfig.ALLOW_SKIP_GPG_VERIFICATION) {
                isDevSettingsEnabled() && isSkipGPGEnabled()
            } else {
                false
            }
            OneKeyLog.info("BundleUpdate", "testSkipVerification: result=$result")
            result
        }
    }

    override fun isSkipGpgVerificationAllowed(): Boolean {
        val result = BuildConfig.ALLOW_SKIP_GPG_VERIFICATION
        OneKeyLog.info("BundleUpdate", "isSkipGpgVerificationAllowed: result=$result")
        return result
    }

    override fun isBundleExists(appVersion: String, bundleVersion: String): Promise<Boolean> {
        return Promise.async {
            if (!isSafeVersionString(appVersion) || !isSafeVersionString(bundleVersion)) {
                OneKeyLog.error("BundleUpdate", "isBundleExists: invalid version string format: appVersion=$appVersion, bundleVersion=$bundleVersion")
                throw SecurityException("Invalid version string")
            }
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val baseBundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context)).canonicalPath
            val bundlePath = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            if (!bundlePath.canonicalPath.startsWith(baseBundleDir + File.separator)) {
                throw SecurityException("Bundle path outside bundle directory")
            }
            val exists = bundlePath.exists()
            OneKeyLog.info("BundleUpdate", "isBundleExists: appVersion=$appVersion, bundleVersion=$bundleVersion, exists=$exists")
            exists
        }
    }

    override fun verifyExtractedBundle(appVersion: String, bundleVersion: String): Promise<Unit> {
        return updatePromise("verifyPackage") {
            OneKeyLog.info("BundleUpdate", "verifyExtractedBundle: appVersion=$appVersion, bundleVersion=$bundleVersion")
            if (!isSafeVersionString(appVersion) || !isSafeVersionString(bundleVersion)) {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: invalid version string format: appVersion=$appVersion, bundleVersion=$bundleVersion")
                throw BundleUpdateError("INVALID_ARGUMENT", "verifyPackage")
            }
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val baseBundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context)).canonicalPath
            val bundlePath = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            if (!bundlePath.canonicalPath.startsWith(baseBundleDir + File.separator)) {
                throw BundleUpdateError("SECURITY_REJECTED", "verifyPackage")
            }
            if (!bundlePath.exists()) {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: bundle directory not found: ${bundlePath.absolutePath}")
                throw BundleUpdateError("PACKAGE_MISSING", "verifyPackage")
            }
            val metadataFile = File(bundlePath, "metadata.json")
            if (!metadataFile.exists()) {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: metadata.json not found in ${bundlePath.absolutePath}")
                throw BundleUpdateError("PACKAGE_MISSING", "verifyPackage")
            }

            OneKeyLog.info("BundleUpdate", "verifyExtractedBundle: parsing metadata and validating files...")
            val metadataContent = BundleUpdateStoreAndroid.readFileContent(metadataFile)
            val metadata = BundleUpdateStoreAndroid.parseMetadataJson(metadataContent)
            if (!BundleUpdateStoreAndroid.validateAllFilesInDir(context, bundlePath.absolutePath, metadata, appVersion, bundleVersion)) {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: file integrity check failed")
                throw BundleUpdateError("INTEGRITY_FAILED", "verifyPackage")
            }
            if (!BundleUpdateStoreAndroid.validateBundlePairCompatibility(context, bundlePath.absolutePath, metadata)) {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: bundle pair compatibility check failed")
                throw BundleUpdateError("INCOMPATIBLE", "verifyPackage")
            }
            OneKeyLog.info("BundleUpdate", "verifyExtractedBundle: all files verified OK, fileCount=${metadata.size}")
        }
    }

    override fun listLocalBundles(): Promise<Array<LocalBundleInfo>> {
        return Promise.async {
            val context = getContext()
            val appVersion = BundleUpdateStoreAndroid.getAppVersion(context) ?: return@async emptyArray<LocalBundleInfo>()
            val bundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context))
            val results = mutableListOf<LocalBundleInfo>()
            if (bundleDir.exists() && bundleDir.isDirectory) {
                bundleDir.listFiles()?.forEach { child ->
                    if (!child.isDirectory) return@forEach
                    val name = child.name
                    if (name == BundleDiffReconstruction.STAGING_ROOT_DIR_NAME) return@forEach
                    val bundleVersion = BundleUpdateStoreAndroid.bundleVersionInFolder(name, appVersion) ?: return@forEach
                    results.add(LocalBundleInfo(appVersion = appVersion, bundleVersion = bundleVersion))
                }
            }
            OneKeyLog.info("BundleUpdate", "listLocalBundles: found ${results.size} bundles")
            results.toTypedArray()
        }
    }

    override fun listAscFiles(): Promise<Array<AscFileInfo>> {
        return Promise.async {
            val context = getContext()
            val ascDir = File(BundleUpdateStoreAndroid.getAscDir(context))
            val results = mutableListOf<AscFileInfo>()
            if (ascDir.exists() && ascDir.isDirectory) {
                ascDir.listFiles()?.forEach { file ->
                    if (file.isFile) {
                        results.add(AscFileInfo(fileName = file.name, filePath = file.absolutePath, fileSize = file.length().toDouble()))
                    }
                }
            }
            OneKeyLog.info("BundleUpdate", "listAscFiles: found ${results.size} files")
            results.toTypedArray()
        }
    }

    override fun getSha256FromFilePath(filePath: String): Promise<String> {
        return Promise.async {
            OneKeyLog.info("BundleUpdate", "getSha256FromFilePath: filePath=$filePath")
            if (filePath.isEmpty()) {
                OneKeyLog.warn("BundleUpdate", "getSha256FromFilePath: empty filePath")
                return@async ""
            }

            // Restrict to bundle-related directories only
            val context = getContext()
            val resolvedPath = File(filePath).canonicalPath
            val bundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context)).canonicalPath
            val downloadDir = File(BundleUpdateStoreAndroid.getDownloadBundleDir(context)).canonicalPath
            // Enforce a separator boundary so e.g. "onekey-bundle-evil/x" is not
            // accepted as confined under "onekey-bundle". The base dir itself is allowed.
            fun isConfinedTo(base: String): Boolean =
                resolvedPath == base || resolvedPath.startsWith(base + File.separator)
            if (!isConfinedTo(bundleDir) && !isConfinedTo(downloadDir)) {
                OneKeyLog.error("BundleUpdate", "getSha256FromFilePath: path outside allowed directories: $resolvedPath")
                throw Exception("File path outside allowed bundle directories")
            }

            val sha256 = BundleUpdateStoreAndroid.calculateSHA256(filePath) ?: ""
            OneKeyLog.info("BundleUpdate", "getSha256FromFilePath: sha256=${if (sha256.isEmpty()) "(empty)" else sha256.take(16) + "..."}")
            sha256
        }
    }

    override fun testDeleteJsBundle(appVersion: String, bundleVersion: String): Promise<TestResult> {
        return Promise.async {
            if (!BuildConfig.DEBUG) {
                throw SecurityException("Test fault-injection methods are disabled in release builds")
            }
            if (!isSafeVersionString(appVersion) || !isSafeVersionString(bundleVersion)) {
                throw SecurityException("Invalid version string")
            }
            OneKeyLog.info("BundleUpdate", "testDeleteJsBundle: appVersion=$appVersion, bundleVersion=$bundleVersion")
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val baseBundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context)).canonicalPath
            val bundleDirPath = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            if (!bundleDirPath.canonicalPath.startsWith(baseBundleDir + File.separator)) {
                throw SecurityException("Bundle path outside bundle directory")
            }
            val jsBundlePath = File(bundleDirPath, "main.jsbundle.hbc")
            if (!jsBundlePath.canonicalPath.startsWith(baseBundleDir + File.separator)) {
                throw SecurityException("Bundle path outside bundle directory")
            }

            if (jsBundlePath.exists()) {
                val success = jsBundlePath.delete()
                if (success) {
                    OneKeyLog.info("BundleUpdate", "testDeleteJsBundle: deleted ${jsBundlePath.absolutePath}")
                    TestResult(success = true, message = "Deleted jsBundle: ${jsBundlePath.absolutePath}")
                } else {
                    OneKeyLog.error("BundleUpdate", "testDeleteJsBundle: failed to delete ${jsBundlePath.absolutePath}")
                    throw Exception("Failed to delete jsBundle: ${jsBundlePath.absolutePath}")
                }
            } else {
                OneKeyLog.warn("BundleUpdate", "testDeleteJsBundle: file not found: ${jsBundlePath.absolutePath}")
                TestResult(success = false, message = "jsBundle not found: ${jsBundlePath.absolutePath}")
            }
        }
    }

    override fun testDeleteJsRuntimeDir(appVersion: String, bundleVersion: String): Promise<TestResult> {
        return Promise.async {
            if (!BuildConfig.DEBUG) {
                throw SecurityException("Test fault-injection methods are disabled in release builds")
            }
            if (!isSafeVersionString(appVersion) || !isSafeVersionString(bundleVersion)) {
                throw SecurityException("Invalid version string")
            }
            OneKeyLog.info("BundleUpdate", "testDeleteJsRuntimeDir: appVersion=$appVersion, bundleVersion=$bundleVersion")
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val baseBundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context)).canonicalPath
            val dir = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            if (!dir.canonicalPath.startsWith(baseBundleDir + File.separator)) {
                throw SecurityException("Bundle path outside bundle directory")
            }

            if (dir.exists()) {
                BundleUpdateStoreAndroid.deleteDir(dir)
                if (!dir.exists()) {
                    OneKeyLog.info("BundleUpdate", "testDeleteJsRuntimeDir: deleted ${dir.absolutePath}")
                    TestResult(success = true, message = "Deleted js runtime directory: ${dir.absolutePath}")
                } else {
                    OneKeyLog.error("BundleUpdate", "testDeleteJsRuntimeDir: failed to delete ${dir.absolutePath}")
                    throw Exception("Failed to delete js runtime directory: ${dir.absolutePath}")
                }
            } else {
                OneKeyLog.warn("BundleUpdate", "testDeleteJsRuntimeDir: directory not found: ${dir.absolutePath}")
                TestResult(success = false, message = "js runtime directory not found: ${dir.absolutePath}")
            }
        }
    }

    override fun testDeleteMetadataJson(appVersion: String, bundleVersion: String): Promise<TestResult> {
        return Promise.async {
            if (!BuildConfig.DEBUG) {
                throw SecurityException("Test fault-injection methods are disabled in release builds")
            }
            if (!isSafeVersionString(appVersion) || !isSafeVersionString(bundleVersion)) {
                throw SecurityException("Invalid version string")
            }
            OneKeyLog.info("BundleUpdate", "testDeleteMetadataJson: appVersion=$appVersion, bundleVersion=$bundleVersion")
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val baseBundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context)).canonicalPath
            val bundleDirPath = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            if (!bundleDirPath.canonicalPath.startsWith(baseBundleDir + File.separator)) {
                throw SecurityException("Bundle path outside bundle directory")
            }
            val metadataFile = File(bundleDirPath, "metadata.json")
            if (!metadataFile.canonicalPath.startsWith(baseBundleDir + File.separator)) {
                throw SecurityException("Bundle path outside bundle directory")
            }

            if (metadataFile.exists()) {
                val success = metadataFile.delete()
                if (success) {
                    OneKeyLog.info("BundleUpdate", "testDeleteMetadataJson: deleted ${metadataFile.absolutePath}")
                    TestResult(success = true, message = "Deleted metadata.json: ${metadataFile.absolutePath}")
                } else {
                    OneKeyLog.error("BundleUpdate", "testDeleteMetadataJson: failed to delete ${metadataFile.absolutePath}")
                    throw Exception("Failed to delete metadata.json: ${metadataFile.absolutePath}")
                }
            } else {
                OneKeyLog.warn("BundleUpdate", "testDeleteMetadataJson: file not found: ${metadataFile.absolutePath}")
                TestResult(success = false, message = "metadata.json not found: ${metadataFile.absolutePath}")
            }
        }
    }

    override fun testWriteEmptyMetadataJson(appVersion: String, bundleVersion: String): Promise<TestResult> {
        return Promise.async {
            if (!BuildConfig.DEBUG) {
                throw SecurityException("Test fault-injection methods are disabled in release builds")
            }
            if (!isSafeVersionString(appVersion) || !isSafeVersionString(bundleVersion)) {
                throw SecurityException("Invalid version string")
            }
            OneKeyLog.info("BundleUpdate", "testWriteEmptyMetadataJson: appVersion=$appVersion, bundleVersion=$bundleVersion")
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val baseBundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context)).canonicalPath
            val jsRuntimeDir = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            if (!jsRuntimeDir.canonicalPath.startsWith(baseBundleDir + File.separator)) {
                throw SecurityException("Bundle path outside bundle directory")
            }
            val metadataPath = File(jsRuntimeDir, "metadata.json")
            if (!metadataPath.canonicalPath.startsWith(baseBundleDir + File.separator)) {
                throw SecurityException("Bundle path outside bundle directory")
            }

            if (!jsRuntimeDir.exists()) {
                if (!jsRuntimeDir.mkdirs()) {
                    OneKeyLog.error("BundleUpdate", "testWriteEmptyMetadataJson: failed to create dir: ${jsRuntimeDir.absolutePath}")
                    throw Exception("Failed to create directory: ${jsRuntimeDir.absolutePath}")
                }
            }

            val emptyJson = JSONObject()
            FileOutputStream(metadataPath).use { fos ->
                fos.write(emptyJson.toString(2).toByteArray(Charsets.UTF_8))
                fos.flush()
            }

            OneKeyLog.info("BundleUpdate", "testWriteEmptyMetadataJson: created ${metadataPath.absolutePath}")
            TestResult(success = true, message = "Created empty metadata.json: ${metadataPath.absolutePath}")
        }
    }
}

