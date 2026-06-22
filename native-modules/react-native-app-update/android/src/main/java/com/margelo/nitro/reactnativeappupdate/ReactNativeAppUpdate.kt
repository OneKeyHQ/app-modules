package com.margelo.nitro.reactnativeappupdate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise
import com.margelo.nitro.NitroModules
import com.margelo.nitro.nativelogger.OneKeyLog
import com.tencent.mmkv.MMKV
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import com.margelo.nitro.reactnativebundlecrypto.BundleCryptoCore
// Shared 8-range concurrent downloader (segment-file model, no whole-file
// pre-allocation). Previously app-update bundled its own private copy; it now
// consumes the single shared implementation in react-native-range-downloader
// so a fix lands once for both APK and JS-bundle downloads.
import com.margelo.nitro.reactnativerangedownloader.ConcurrentRangeDownloader

private data class Listener(
    val id: Double,
    val callback: (DownloadEvent) -> Unit
)

@DoNotStrip
class ReactNativeAppUpdate : HybridReactNativeAppUpdateSpec() {

    companion object {
        private const val CHANNEL_ID = "updateApp"
        private const val NOTIFICATION_ID = 1

        // Must match ConcurrentRangeDownloader's default segmentCount: the
        // concurrent downloader writes sibling segment files
        // "<partial>.seg0".."<partial>.seg${N-1}", and Phase 2 below scans this
        // range to detect an in-flight concurrent download.
        private const val CONCURRENT_SEGMENT_COUNT = 8
    }

    private val listeners = CopyOnWriteArrayList<Listener>()
    private val nextListenerId = AtomicLong(1)
    private val isDownloading = AtomicBoolean(false)
    // The in-flight concurrent download's cancel handle, so clearCache can stop
    // its workers (shutdownNow + awaitTermination) BEFORE deleting .segN files.
    // Without this a still-running worker resurrects a just-deleted segment / writes
    // to a deleted FD — the cancel-then-delete race OCDS §5.8 forbids. Mirrors
    // react-native-bundle-update's activeDownloads cancel wiring.
    private val activeDownload =
        java.util.concurrent.atomic.AtomicReference<ConcurrentRangeDownloader.CancelHandle?>(null)
    // downloadThread removed: downloads use coroutine-based Promise.async, not raw threads

    private fun sendEvent(type: String, progress: Int = 0, message: String = "") {
        val event = DownloadEvent(type = type, progress = progress.toDouble(), message = message)
        for (listener in listeners) {
            try {
                listener.callback(event)
            } catch (e: Exception) {
                OneKeyLog.error("AppUpdate", "Error sending event: ${e.message}")
            }
        }
    }

    override fun addDownloadListener(callback: (DownloadEvent) -> Unit): Double {
        val id = nextListenerId.getAndIncrement().toDouble()
        listeners.add(Listener(id, callback))
        return id
    }

    override fun removeDownloadListener(id: Double) {
        listeners.removeAll { it.id == id }
    }

    /**
     * Directory for downloaded APK artifacts (.apk / .partial / .segN / .asc).
     *
     * Uses filesDir, NOT cacheDir: cacheDir is system-reclaimable — under
     * storage pressure Android can purge it or make it unwritable mid-download,
     * surfacing as EROFS/ENOSPC when opening a new segment file. filesDir is
     * persistent app data and is the same location react-native-bundle-update
     * downloads to. It is still installable: the APK is handed to the system
     * installer via FileProvider, whose <paths> exposes this dir
     * (see res/xml/app_update_file_paths.xml: <files-path name="apks_files" .../>).
     */
    private fun getApkDownloadDir(): File {
        val context = NitroModules.applicationContext
            ?: throw SecurityException("Application context unavailable")
        val apkDir = File(context.filesDir, "apks")
        if (!apkDir.exists()) apkDir.mkdirs()
        return apkDir
    }

    /**
     * Derive a local file name from a download URL by extracting the last path segment.
     * e.g. "https://example.com/path/to/app-1.0.apk" -> "app-1.0.apk"
     */
    private fun filePathFromUrl(url: String): String {
        val path = Uri.parse(url).lastPathSegment
        if (path.isNullOrBlank()) {
            throw Exception("Cannot derive file name from URL: $url")
        }
        return path
    }

    private fun buildFile(path: String): File {
        val stripped = path.removePrefix("file:///")
        val context = NitroModules.applicationContext
            ?: throw SecurityException("Application context unavailable, cannot validate file path")

        // Resolve relative paths against cacheDir/apk/
        val file = if (stripped.startsWith("/")) {
            File(stripped)
        } else {
            File(getApkDownloadDir(), stripped)
        }

        // Validate the resolved path is within the app's cache or files directory
        val canonicalPath = file.canonicalPath
        val cacheDir = context.cacheDir.canonicalPath
        val filesDir = context.filesDir.canonicalPath
        val externalCacheDir = context.externalCacheDir?.canonicalPath
        val externalFilesDir = context.getExternalFilesDir(null)?.canonicalPath
        val allowed = canonicalPath.startsWith(cacheDir + File.separator) || canonicalPath == cacheDir ||
                canonicalPath.startsWith(filesDir + File.separator) || canonicalPath == filesDir ||
                (externalCacheDir != null && (canonicalPath.startsWith(externalCacheDir + File.separator) || canonicalPath == externalCacheDir)) ||
                (externalFilesDir != null && (canonicalPath.startsWith(externalFilesDir + File.separator) || canonicalPath == externalFilesDir))
        if (!allowed) {
            OneKeyLog.error("AppUpdate", "buildFile: path outside allowed directories, " +
                "input=$path, resolved=$canonicalPath, " +
                "cacheDir=$cacheDir, filesDir=$filesDir, " +
                "externalCacheDir=$externalCacheDir, externalFilesDir=$externalFilesDir")
            throw SecurityException("Path outside allowed directories: $canonicalPath")
        }
        OneKeyLog.debug("AppUpdate", "buildFile: input=$path, resolved=$canonicalPath")
        return file
    }

    /**
     * Thin delegate over the shared BundleCryptoCore.sha256OfFile. The streaming
     * MessageDigest implementation now lives in react-native-bundle-crypto.
     * Preserves the original throw-on-failure contract: callers here expect a
     * non-null hex String or an exception, so a null sha256 (with its
     * failureReason taxonomy) is surfaced as a thrown exception rather than a
     * silent empty string.
     */
    private fun computeSha256(file: File): String {
        val result = BundleCryptoCore.sha256OfFile(file.absolutePath)
        return result.sha256
            ?: throw java.io.IOException("SHA256 computation failed: ${result.failureReason}")
    }

    /**
     * Download ASC file for the given URL and save to the given path.
     * Returns the saved file, or null on failure.
     */
    private fun downloadAscFile(ascUrl: String, ascFile: File): File? {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .followRedirects(false)
            .followSslRedirects(false)
            .build()
        val request = Request.Builder().url(ascUrl).build()
        val content = StringBuilder()
        val maxAscSize = 10 * 1024
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val body = response.body ?: return null
            BufferedReader(InputStreamReader(body.byteStream())).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    content.append(line).append("\n")
                    if (content.length > maxAscSize) return null
                }
            }
        }
        val ascContent = content.toString()
        if (ascContent.isEmpty()) return null
        ascFile.parentFile?.mkdirs()
        FileOutputStream(ascFile).use { fos -> fos.write(ascContent.toByteArray()) }
        return ascFile
    }

    /**
     * Verify GPG signature of an ASC file and extract the SHA256 hash.
     * Returns the SHA256 hash if signature is valid, null otherwise.
     */
    /**
     * Verify GPG signature of ASC content string and extract the SHA256 hash.
     * Returns the SHA256 hash if signature is valid, null otherwise.
     *
     * Thin delegate over the shared BundleCryptoCore.verifyDetachedAsc — the
     * BouncyCastle cleartext-signed-message parsing/verify and the OneKey GPG
     * public key now live there as the single source of truth. Behavior is
     * preserved: any non-valid result (NOT_PGP_SIGNED_MESSAGE / INVALID_FORMAT /
     * NO_SIGNATURE / PUBKEY_NOT_FOUND / SIGNATURE_INVALID / SHA256_TOKEN_INVALID
     * / ERROR_*) maps to null.
     */
    private fun verifyAscContentAndExtractSha256(ascContent: String): String? {
        val result = BundleCryptoCore.verifyDetachedAsc(ascContent)
        if (!result.valid) {
            OneKeyLog.error("AppUpdate", "ASC verification failed: ${result.reason}")
            return null
        }
        return result.sha256
    }

    private fun verifyAscAndExtractSha256(ascFile: File): String? {
        return verifyAscContentAndExtractSha256(ascFile.readText())
    }

    /**
     * Constant-time comparison to prevent timing attacks on hash values.
     * Delegates to the shared BundleCryptoCore.secureEqualHex (same byte-wise
     * constant-time algorithm).
     */
    private fun secureCompare(a: String, b: String): Boolean {
        return BundleCryptoCore.secureEqualHex(a, b)
    }

    /**
     * Outcome of verifying an existing APK against its detached SHA256SUMS.asc.
     * Three states matter: a clean pass, a real hash mismatch (file is stale and
     * must be discarded), and "we can't tell" — typically because the ASC was
     * unfetchable (no network) or the local ASC is unparseable. The previous
     * Boolean collapsed Indeterminate into "invalid" and the caller deleted a
     * perfectly-good partial download on every transient network blip.
     */
    private sealed class ApkVerifyOutcome {
        object Valid : ApkVerifyOutcome()
        object HashMismatch : ApkVerifyOutcome()
        object Indeterminate : ApkVerifyOutcome()
    }

    /**
     * Check the apkFile against its detached SHA256SUMS.asc and return a
     * tri-state outcome. Callers must NOT delete the APK on Indeterminate —
     * the bytes on disk may still be the right ones; the next online retry can
     * decide. Downloads ASC if not present.
     */
    private fun verifyExistingApk(url: String, filePath: String, apkFile: File): ApkVerifyOutcome {
        return try {
            val ascFilePath = "$filePath.SHA256SUMS.asc"
            val ascFile = buildFile(ascFilePath)

            if (!ascFile.exists()) {
                val ascUrl = "$url.SHA256SUMS.asc"
                OneKeyLog.info("AppUpdate", "verifyExistingApk: ASC not found, downloading from $ascUrl")
                val downloaded = try {
                    downloadAscFile(ascUrl, ascFile)
                } catch (e: Exception) {
                    // OkHttp throws IOException family on offline / DNS / connection-reset.
                    // Treat as Indeterminate so caller preserves the partial.
                    OneKeyLog.warn("AppUpdate", "verifyExistingApk: ASC download threw ${e.javaClass.simpleName}: ${e.message}")
                    null
                }
                if (downloaded == null) {
                    OneKeyLog.warn("AppUpdate", "verifyExistingApk: ASC unavailable, indeterminate")
                    return ApkVerifyOutcome.Indeterminate
                }
                OneKeyLog.info("AppUpdate", "verifyExistingApk: ASC downloaded to ${ascFile.absolutePath}")
            }

            val expectedSha256 = verifyAscAndExtractSha256(ascFile)
            if (expectedSha256 == null) {
                // Local ASC failed parsing/GPG. Could be a corrupted cache from
                // an earlier interrupted write — drop it so the next round
                // re-downloads cleanly. Don't condemn the APK on this alone.
                OneKeyLog.warn("AppUpdate", "verifyExistingApk: GPG verification or SHA256 extraction failed, discarding local ASC")
                ascFile.delete()
                return ApkVerifyOutcome.Indeterminate
            }

            OneKeyLog.info("AppUpdate", "verifyExistingApk: computing SHA256 of existing APK (size=${apkFile.length()})...")
            val actualSha256 = computeSha256(apkFile)

            if (secureCompare(actualSha256, expectedSha256)) {
                OneKeyLog.info("AppUpdate", "verifyExistingApk: SHA256 matches, APK is valid")
                ApkVerifyOutcome.Valid
            } else {
                OneKeyLog.warn("AppUpdate", "verifyExistingApk: SHA256 mismatch, expected=${expectedSha256.take(16)}..., got=${actualSha256.take(16)}...")
                ApkVerifyOutcome.HashMismatch
            }
        } catch (e: Exception) {
            OneKeyLog.warn("AppUpdate", "verifyExistingApk: unexpected failure: ${e.javaClass.simpleName}: ${e.message}")
            ApkVerifyOutcome.Indeterminate
        }
    }

    /**
     * Thrown when we cannot decide whether the bytes on disk are still
     * the right APK (typically: ASC unreachable due to offline). Extends
     * IOException so existing IOException-aware callers still match it,
     * but a dedicated type lets JS branch on `IOException`/class name
     * instead of substring-matching the message — which would silently
     * rot the moment we tweak the wording.
     */
    private class ApkVerificationDeferredException :
        java.io.IOException(DEFERRED_VERIFICATION_MESSAGE) {
        companion object {
            const val DEFERRED_VERIFICATION_MESSAGE = "APK verification deferred: ASC unavailable"
        }
    }

    /**
     * Move bytes from the final path back into the .partial slot. Tries
     * an atomic rename first; on the rare same-fs rename failure, falls
     * back to a stream copy so a transient FS hiccup cannot destroy the
     * one and only copy of an already-downloaded payload (the exact
     * regression this PR is trying to avoid). Returns true if the bytes
     * are at partialFile by the time we return.
     */
    private fun rollbackFinalToPartial(downloadedFile: File, partialFile: File): Boolean {
        if (!downloadedFile.exists()) return false
        if (partialFile.exists()) partialFile.delete()
        if (downloadedFile.renameTo(partialFile)) return true
        OneKeyLog.warn("AppUpdate", "rollbackFinalToPartial: rename failed, falling back to byte copy (size=${downloadedFile.length()})")
        return try {
            FileInputStream(downloadedFile).use { input ->
                FileOutputStream(partialFile).use { output ->
                    val buffer = ByteArray(8192)
                    var n: Int
                    while (input.read(buffer).also { n = it } != -1) {
                        output.write(buffer, 0, n)
                    }
                }
            }
            val ok = partialFile.length() == downloadedFile.length()
            if (ok) {
                downloadedFile.delete()
                OneKeyLog.info("AppUpdate", "rollbackFinalToPartial: byte copy fallback succeeded (size=${partialFile.length()})")
                true
            } else {
                OneKeyLog.error("AppUpdate", "rollbackFinalToPartial: byte copy size mismatch (partial=${partialFile.length()}, final=${downloadedFile.length()}), discarding copy")
                partialFile.delete()
                false
            }
        } catch (e: Exception) {
            OneKeyLog.error("AppUpdate", "rollbackFinalToPartial: byte copy fallback failed: ${e.javaClass.simpleName}: ${e.message}")
            if (partialFile.exists()) partialFile.delete()
            false
        }
    }

    /**
     * Outcome of promoting a fully-sized .partial to the final path and
     * running the GPG/SHA verifier against it.
     */
    private sealed class PromoteOutcome {
        object Valid : PromoteOutcome()
        object HashMismatch : PromoteOutcome()
        object Deferred : PromoteOutcome()     // verifier Indeterminate; bytes restored to .partial
        object RenameFailed : PromoteOutcome() // promotion rename failed; bytes preserved at .partial
    }

    /**
     * Promote .partial -> final, run the verifier, and dispatch the
     * tri-state outcome. On Indeterminate the bytes are rolled back into
     * .partial (with copy fallback if rename fails), so callers can
     * retry later. On promotion-rename failure the .partial is kept
     * intact so Phase 3 can Range-resume on the next pass.
     *
     * This consolidates the previously three-times-duplicated
     * promote+verify+dispatch logic in downloadAPK. Any future tweak to
     * promotion semantics happens once here, not in three drift-prone
     * sites.
     */
    private fun tryPromoteAndVerify(
        url: String,
        filePath: String,
        partialFile: File,
        downloadedFile: File
    ): PromoteOutcome {
        if (downloadedFile.exists()) downloadedFile.delete()
        if (!partialFile.renameTo(downloadedFile)) {
            OneKeyLog.warn("AppUpdate", "tryPromoteAndVerify: rename .partial -> final failed, preserving .partial for Range resume")
            // renameTo is atomic on same fs: either partial moved to
            // final or it didn't. On failure the bytes are still at
            // partialFile; leave them so Phase 3 can Range-resume.
            if (downloadedFile.exists()) downloadedFile.delete()
            return PromoteOutcome.RenameFailed
        }
        return when (verifyExistingApk(url, filePath, downloadedFile)) {
            ApkVerifyOutcome.Valid -> PromoteOutcome.Valid
            ApkVerifyOutcome.HashMismatch -> {
                downloadedFile.delete()
                PromoteOutcome.HashMismatch
            }
            ApkVerifyOutcome.Indeterminate -> {
                if (!rollbackFinalToPartial(downloadedFile, partialFile)) {
                    OneKeyLog.error("AppUpdate", "tryPromoteAndVerify: rollback failed for both rename and byte copy; bytes lost")
                }
                PromoteOutcome.Deferred
            }
        }
    }

    private fun isDebuggable(): Boolean {
        val context = NitroModules.applicationContext ?: return false
        return (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    /** Returns true if OneKey developer mode (DevSettings) is enabled via MMKV storage. */
    private fun isDevSettingsEnabled(): Boolean {
        return try {
            val context = NitroModules.applicationContext ?: return false
            MMKV.initialize(context)
            val mmkv = MMKV.mmkvWithID("onekey-app-dev-setting") ?: return false
            mmkv.decodeBool("onekey_developer_mode_enabled", false)
        } catch (e: Exception) {
            false
        }
    }

    /** Returns true if the skip-GPG-verification toggle is enabled via MMKV storage. */
    private fun isSkipGPGEnabled(): Boolean {
        if (!BuildConfig.ALLOW_SKIP_GPG_VERIFICATION) return false
        return try {
            val context = NitroModules.applicationContext ?: return false
            MMKV.initialize(context)
            val mmkv = MMKV.mmkvWithID("onekey-app-dev-setting") ?: return false
            mmkv.decodeBool("onekey_bundle_skip_gpg_verification", false)
        } catch (e: Exception) {
            false
        }
    }

    override fun downloadAPK(params: AppUpdateDownloadParams): Promise<Unit> {
        return Promise.async {
            if (isDownloading.getAndSet(true)) {
                OneKeyLog.warn("AppUpdate", "downloadAPK: rejected, already downloading")
                throw Exception("Download already in progress")
            }

            try {
                val url = params.downloadUrl
                val filePath = filePathFromUrl(url)
                val notificationTitle = params.notificationTitle
                val fileSize = params.fileSize.toLong()

                OneKeyLog.info("AppUpdate", "downloadAPK: url=$url, filePath=$filePath, fileSize=$fileSize")

                val context = NitroModules.applicationContext
                    ?: throw Exception("Application context unavailable")

                val notifyManager = NotificationManagerCompat.from(context)
                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(notificationTitle)
                    .setContentText("")
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setSmallIcon(android.R.drawable.stat_sys_download)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        CHANNEL_ID, "updateApp", NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notifyManager.createNotificationChannel(channel)
                }

                if (!url.startsWith("https://")) {
                    OneKeyLog.error("AppUpdate", "downloadAPK: URL is not HTTPS: $url")
                    throw Exception("Download URL must use HTTPS")
                }

                // Resume model (mirrors react-native-bundle-update's downloadBundle):
                //   * Bytes in flight live in <filePath>.partial.
                //   * The "final" filePath only ever holds a fully transferred APK
                //     (verified or about-to-be-verified). This keeps the
                //     "exists at filePath -> already valid" cache check below
                //     immune to the previous bug where a half-baked APK at the
                //     final path looked complete to clearCache / installAPK callers.
                val partialFilePath = "$filePath.partial"
                val downloadedFile = buildFile(filePath)
                val partialFile = buildFile(partialFilePath)
                val expectedSize = if (fileSize > 0) fileSize else 0L

                // Phase 1 — adopt anything already at the final path.
                // Old builds (pre-resume) wrote partial bytes here directly, so
                // a small file at filePath is most likely a stalled download
                // from an earlier app version: promote it to .partial so we can
                // Range-resume instead of starting over. A correctly-sized file
                // goes through the tri-state verifier and only gets deleted on
                // a real hash mismatch — never on an unfetchable ASC.
                //
                // Caveat: when expectedSize == 0 (caller didn't pass fileSize)
                // we cannot distinguish a complete cached APK from a pre-resume
                // half-baked one — both flow into the verifier, and a stale
                // half-baked file that happens to be online will hit
                // HashMismatch and get deleted (correct, but loses the bytes).
                // Always pass fileSize from JS to unlock the size-based
                // pre-resume migration path.
                if (downloadedFile.exists()) {
                    val existingSize = downloadedFile.length()
                    OneKeyLog.info("AppUpdate", "downloadAPK: existing APK at final path (size=$existingSize, expected=$expectedSize)")
                    when {
                        expectedSize > 0 && existingSize > expectedSize -> {
                            OneKeyLog.warn("AppUpdate", "downloadAPK: existing APK larger than expected, deleting")
                            downloadedFile.delete()
                            // Oversized final means local state is untrustworthy. A stale
                            // single-stream .partial and any sibling .segN left by an earlier
                            // concurrent run could otherwise be picked up below and reused —
                            // wipe them so this restarts cleanly from byte zero.
                            if (partialFile.exists()) partialFile.delete()
                            for (i in 0 until CONCURRENT_SEGMENT_COUNT) buildFile("$partialFilePath.seg$i").delete()
                        }
                        expectedSize > 0 && existingSize < expectedSize -> {
                            OneKeyLog.info("AppUpdate", "downloadAPK: existing APK smaller than expected, promoting to .partial for resume")
                            if (partialFile.exists()) partialFile.delete()
                            // Drop any sibling .segN BEFORE the promotion. The promoted final
                            // must become a clean single-stream resume cursor: if .segN
                            // survived, Phase 2's hasConcurrentSegments check would fire and
                            // the concurrent downloader would treat this legacy .partial as a
                            // concat committed-cursor, risking a mixed file. With no .segN,
                            // Phase 2 takes the single-stream size-based path and Range-resumes
                            // from the partial's length.
                            for (i in 0 until CONCURRENT_SEGMENT_COUNT) buildFile("$partialFilePath.seg$i").delete()
                            if (!downloadedFile.renameTo(partialFile)) {
                                OneKeyLog.warn("AppUpdate", "downloadAPK: rename to .partial failed, deleting stale final")
                                downloadedFile.delete()
                            }
                        }
                        else -> {
                            when (verifyExistingApk(url, filePath, downloadedFile)) {
                                ApkVerifyOutcome.Valid -> {
                                    OneKeyLog.info("AppUpdate", "downloadAPK: existing APK is valid, skipping download")
                                    sendEvent("update/downloaded")
                                    return@async
                                }
                                ApkVerifyOutcome.HashMismatch -> {
                                    OneKeyLog.info("AppUpdate", "downloadAPK: existing APK hash mismatch, deleting and re-downloading")
                                    downloadedFile.delete()
                                    if (partialFile.exists()) partialFile.delete()
                                    // Final was stale → start from byte zero. Wipe any
                                    // sibling .segN left by an earlier concurrent run so
                                    // it can't be mistaken for trustworthy in-flight bytes.
                                    for (i in 0 until CONCURRENT_SEGMENT_COUNT) buildFile("$partialFilePath.seg$i").delete()
                                }
                                ApkVerifyOutcome.Indeterminate -> {
                                    // ASC could not be fetched (offline) or could
                                    // not be parsed. The on-disk APK might still
                                    // be the right one — surface a transient
                                    // error so the JS retry layer waits for
                                    // network and re-runs verify, instead of
                                    // wiping the bytes preemptively.
                                    OneKeyLog.warn("AppUpdate", "downloadAPK: cannot verify existing APK (ASC unavailable); preserving file and aborting this attempt")
                                    throw ApkVerificationDeferredException()
                                }
                            }
                        }
                    }
                }

                // Phase 2 — pick up an in-flight partial.
                //
                // The concurrent (multi-range) downloader stores in-flight bytes in
                // sibling segment files "<partial>.seg0".."<partial>.segN" — NOT in
                // the .partial (the .partial only ever holds real, fully-assembled
                // bytes, written by the concurrent downloader's final concat or by
                // the single-stream path). When any segment file exists, an
                // interrupted concurrent download owns the slot: skip the size-based
                // promote/discard branches entirely and let the concurrent
                // downloader below resume (it picks up each .segN, or returns
                // FALLBACK and hands the bytes back to single-stream). The segment
                // path must match exactly what ConcurrentRangeDownloader writes:
                // File("$partialFilePath.seg$i").
                val hasConcurrentSegments =
                    (0 until CONCURRENT_SEGMENT_COUNT).any { buildFile("$partialFilePath.seg$it").exists() }
                var partialBytes = 0L
                if (partialFile.exists() && !hasConcurrentSegments) {
                    val partialSize = partialFile.length()
                    when {
                        expectedSize > 0 && partialSize == expectedSize -> {
                            // Full body on disk but the previous run was killed
                            // before promotion. Try promote + verify before
                            // re-downloading.
                            OneKeyLog.info("AppUpdate", "downloadAPK: partial matches expected size ($partialSize), trying promote+verify")
                            when (tryPromoteAndVerify(url, filePath, partialFile, downloadedFile)) {
                                PromoteOutcome.Valid -> {
                                    OneKeyLog.info("AppUpdate", "downloadAPK: recovered crashed-before-rename APK, skipping download")
                                    sendEvent("update/downloaded")
                                    return@async
                                }
                                PromoteOutcome.HashMismatch -> {
                                    OneKeyLog.warn("AppUpdate", "downloadAPK: promoted partial failed hash check, discarding")
                                    // Helper already deleted final; partial slot empty.
                                    // Fall through: Phase 3 fetches from byte zero.
                                }
                                PromoteOutcome.Deferred -> {
                                    throw ApkVerificationDeferredException()
                                }
                                PromoteOutcome.RenameFailed -> {
                                    // Bytes preserved at .partial: Phase 3 will Range-resume.
                                    OneKeyLog.warn("AppUpdate", "downloadAPK: promote rename failed, will Range-resume from .partial")
                                    partialBytes = partialFile.length()
                                }
                            }
                        }
                        expectedSize > 0 && partialSize > expectedSize -> {
                            OneKeyLog.warn("AppUpdate", "downloadAPK: stale partial (>expected $partialSize/$expectedSize), discarding")
                            partialFile.delete()
                            // Partial bytes are untrustworthy → restart from zero; drop any sibling .segN too.
                            for (i in 0 until CONCURRENT_SEGMENT_COUNT) buildFile("$partialFilePath.seg$i").delete()
                        }
                        partialSize > 0 -> {
                            partialBytes = partialSize
                            OneKeyLog.info("AppUpdate", "downloadAPK: resuming from $partialBytes bytes (expected=$expectedSize)")
                        }
                        else -> partialFile.delete()
                    }
                }

                // === Concurrent multi-range download (P2) ===
                // Try the 8-range concurrent path first. On COMPLETED the
                // .partial is fully on disk, so we promote + emit downloaded
                // here (SHA/GPG verification still happens later via verifyAPK,
                // same as the single-stream path). On FALLBACK we fall through
                // to single-stream Phase 3 below. Transient errors bubble to the
                // outer catch (the downloader keeps its partial + manifest).
                sendEvent("update/start")
                run {
                    val concurrentClient = OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .followRedirects(false)
                        .followSslRedirects(false)
                        .build()
                    // onProgress is invoked concurrently from all 8 worker
                    // threads. concurrentProgress must be an AtomicInteger so the
                    // "did the percent advance?" check is race-free, and the
                    // NotificationCompat.Builder (shared, NOT thread-safe) plus the
                    // notify() call must be guarded by a single lock so two threads
                    // never mutate/build it at once. We emit at most once per
                    // percent step: getAndSet returns the previous value, and only
                    // the thread that actually moved the percent forward proceeds.
                    val concurrentProgress = AtomicInteger(-1)
                    val notifyLock = Any()
                    // Wire a cancel handle so clearCache can stop these workers
                    // before deleting .segN (OCDS §5.8). Cleared in the finally below.
                    val cancelHandle = ConcurrentRangeDownloader.CancelHandle()
                    activeDownload.set(cancelHandle)
                    val concurrentOutcome = ConcurrentRangeDownloader(
                        httpClient = concurrentClient,
                        log = { msg -> OneKeyLog.info("AppUpdate", msg) },
                        // MUST be the ABSOLUTE path. partialFilePath is just a
                        // file NAME ("<apk>.partial"); the downloader does
                        // File(path) on it, which resolves a relative name
                        // against the process CWD ("/") and writes the segment
                        // files to the read-only root fs → EROFS. Resolve it to
                        // the real apks dir (filesDir/apks) first, exactly like
                        // react-native-bundle-update passes an absolute path.
                    ).download(url, partialFile.absolutePath, cancelHandle) { transferred, total ->
                        if (total > 0) {
                            val p = ((transferred * 100) / total).toInt().coerceIn(0, 100)
                            // Only the thread that advances the percent emits; a
                            // stale/lower p (out-of-order delivery) is dropped.
                            val prev = concurrentProgress.get()
                            if (p > prev && concurrentProgress.compareAndSet(prev, p)) {
                                sendEvent("update/downloading", progress = p)
                                synchronized(notifyLock) {
                                    builder.setProgress(100, p, false)
                                    if (ActivityCompat.checkSelfPermission(
                                            context, android.Manifest.permission.POST_NOTIFICATIONS
                                        ) == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        notifyManager.notify(NOTIFICATION_ID, builder.build())
                                    }
                                }
                            }
                        }
                    }
                    if (concurrentOutcome == ConcurrentRangeDownloader.Outcome.COMPLETED) {
                        if (downloadedFile.exists()) downloadedFile.delete()
                        if (!partialFile.renameTo(downloadedFile)) {
                            OneKeyLog.error("AppUpdate", "downloadAPK: concurrent rename .partial -> final failed")
                            throw Exception("Failed to finalize download")
                        }
                        OneKeyLog.info("AppUpdate", "downloadAPK: concurrent download completed")
                        sendEvent("update/downloaded")
                        notifyManager.cancel(NOTIFICATION_ID)
                        builder.setContentText("")
                            .setProgress(0, 0, false)
                            .setOngoing(false)
                            .setAutoCancel(true)
                        if (ActivityCompat.checkSelfPermission(
                                context, android.Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            notifyManager.notify(NOTIFICATION_ID, builder.build())
                        }
                        return@async
                    }
                    OneKeyLog.info("AppUpdate", "downloadAPK: concurrent not used, falling back to single-stream")
                }

                // Phase 3 — fetch (with Range header iff resuming).
                val client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .followRedirects(false)
                    .followSslRedirects(false)
                    .build()
                val requestBuilder = Request.Builder().url(url)
                if (partialBytes > 0) {
                    requestBuilder.addHeader("Range", "bytes=$partialBytes-")
                }
                // update/start already emitted before the concurrent attempt above.
                OneKeyLog.info("AppUpdate", "downloadAPK: starting single-stream download (resume=${partialBytes > 0})...")

                val response = client.newCall(requestBuilder.build()).execute()

                // 416 Range Not Satisfiable: server says our offset is past the
                // file length. Two sub-cases distinguishable from
                // `Content-Range: bytes */<total>`:
                //   (a) total == partialBytes → file is exactly complete on
                //       server; our partial IS the whole APK and just needs
                //       SHA verify + rename. Recover instead of wipe.
                //   (b) anything else → partial is corrupt or build changed.
                //       Wipe and bubble up.
                if (response.code == 416) {
                    val contentRange = response.header("Content-Range")
                    response.close()
                    val totalFromHeader = contentRange
                        ?.let { Regex("""bytes\s+\*\s*/\s*(\d+)""").find(it)?.groupValues?.getOrNull(1)?.toLongOrNull() }
                    if (totalFromHeader != null && totalFromHeader == partialBytes && partialFile.exists()) {
                        OneKeyLog.info("AppUpdate", "downloadAPK: HTTP 416 with total=$totalFromHeader matches partial, attempting promote+verify")
                        when (tryPromoteAndVerify(url, filePath, partialFile, downloadedFile)) {
                            PromoteOutcome.Valid -> {
                                OneKeyLog.info("AppUpdate", "downloadAPK: 416 recovery succeeded, skipping download")
                                sendEvent("update/downloaded")
                                return@async
                            }
                            PromoteOutcome.HashMismatch -> {
                                // Server says "you have it all" AND sizes match,
                                // but SHA doesn't: the upstream build was
                                // replaced after we started downloading. That's
                                // the actual failure — raw "HTTP 416" misleads
                                // anyone reading the error.
                                OneKeyLog.warn("AppUpdate", "downloadAPK: 416 recovery hash mismatch — server build changed mid-download")
                                if (partialFile.exists()) partialFile.delete()
                                // Server build changed → these bytes are worthless; drop sibling .segN too.
                                for (i in 0 until CONCURRENT_SEGMENT_COUNT) buildFile("$partialFilePath.seg$i").delete()
                                throw java.io.IOException("Server build changed mid-download (size matches but hash differs)")
                            }
                            PromoteOutcome.Deferred -> {
                                throw ApkVerificationDeferredException()
                            }
                            PromoteOutcome.RenameFailed -> {
                                // Bytes still at .partial; surface a transient
                                // error so caller retries (and we'll try the
                                // promotion again next pass).
                                OneKeyLog.warn("AppUpdate", "downloadAPK: 416 recovery rename failed, retry later")
                                throw java.io.IOException("Failed to finalize 416 recovery (rename failed)")
                            }
                        }
                    }
                    OneKeyLog.warn("AppUpdate", "downloadAPK: HTTP 416 (range not satisfiable), discarding partial and failing attempt")
                    if (partialFile.exists()) partialFile.delete()
                    // Partial discarded as unusable → drop sibling .segN too so the next attempt starts clean.
                    for (i in 0 until CONCURRENT_SEGMENT_COUNT) buildFile("$partialFilePath.seg$i").delete()
                    throw Exception("HTTP 416 (range not satisfiable)")
                }

                if (!response.isSuccessful || (response.code != 200 && response.code != 206)) {
                    OneKeyLog.error("AppUpdate", "downloadAPK: HTTP error, statusCode=${response.code}")
                    response.close()
                    sendEvent("update/error", message = response.code.toString())
                    throw Exception(response.code.toString())
                }

                val expectsResume = partialBytes > 0
                var serverWillResume = response.code == 206

                // Server may legally ignore Range and reply 200 with the full
                // body. Drop the stale partial and restart from byte zero.
                if (expectsResume && !serverWillResume) {
                    OneKeyLog.warn("AppUpdate", "downloadAPK: requested Range but server returned 200, restarting from scratch")
                    if (partialFile.exists()) partialFile.delete()
                    // Restarting from byte zero → drop any sibling .segN too.
                    for (i in 0 until CONCURRENT_SEGMENT_COUNT) buildFile("$partialFilePath.seg$i").delete()
                    partialBytes = 0L
                }

                // 206 sanity check: the server MUST tell us where its body
                // starts. If `Content-Range: bytes start-end/total` is missing
                // or `start != partialBytes` (CDN bug / proxy rewrite), we'd
                // be appending mis-aligned bytes and only catching it later
                // at the SHA step — with the partial now corrupted. Demote to
                // a full restart instead.
                val rangeRegex = Regex("""bytes\s+(\d+)\s*-\s*(\d+)\s*/\s*(\d+|\*)""")
                val rangeMatch = if (serverWillResume) {
                    response.header("Content-Range")?.let { rangeRegex.find(it) }
                } else null
                if (serverWillResume) {
                    val rangeStart = rangeMatch?.groupValues?.getOrNull(1)?.toLongOrNull()
                    if (rangeStart == null || rangeStart != partialBytes) {
                        // This 206 body is a slice starting at the wrong offset (CDN bug /
                        // proxy rewrite). It is NOT a full file, so we must not consume it as
                        // one — doing so would write mis-aligned bytes (caught only later at
                        // SHA/GPG, after a bogus "downloaded" event). Wipe the partial + any
                        // .segN so the next attempt starts clean from byte zero, close the
                        // bad response, and throw a retryable error. The outer
                        // catch(Exception) emits update/error and rethrows; finally resets
                        // isDownloading, so this won't wedge.
                        val contentRangeHeader = response.header("Content-Range")
                        OneKeyLog.warn("AppUpdate", "downloadAPK: 206 Content-Range start mismatch (header='$contentRangeHeader', requested=$partialBytes); discarding partial and retrying from scratch")
                        if (partialFile.exists()) partialFile.delete()
                        for (i in 0 until CONCURRENT_SEGMENT_COUNT) buildFile("$partialFilePath.seg$i").delete()
                        response.close()
                        throw java.io.IOException("206 Content-Range start mismatch (header='$contentRangeHeader', requested=$partialBytes); discarded partial, retry from scratch")
                    }
                }

                val body = response.body ?: run {
                    response.close()
                    throw Exception("Empty response body")
                }
                val contentLength = body.contentLength()
                val totalSize: Long = if (serverWillResume) {
                    val parsedTotal = rangeMatch?.groupValues?.getOrNull(3)?.toLongOrNull()
                    parsedTotal ?: (partialBytes + contentLength.coerceAtLeast(0L))
                } else {
                    if (contentLength > 0) contentLength else expectedSize
                }
                val isPartialResponse = serverWillResume
                OneKeyLog.info("AppUpdate", "downloadAPK: HTTP ${response.code}, contentLength=$contentLength, totalSize=$totalSize, partialBytes=$partialBytes, downloading...")

                val parentDir = partialFile.parentFile
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs()
                    OneKeyLog.info("AppUpdate", "downloadAPK: created parent directory: ${parentDir.absolutePath}")
                }

                // Append iff server granted us a 206. On a 200 (full body
                // restart) we truncate the partial file.
                val appendMode = isPartialResponse
                var totalBytesRead = if (isPartialResponse) partialBytes else 0L
                // -1 sentinel (vs the old 0) so 0% emits exactly once on a
                // fresh start. Listeners just set state from event.progress,
                // so this is a benign improvement (no double-fire on resume —
                // a resumed download's first event is already >0%).
                var prevProgress = -1

                body.byteStream().use { inputStream ->
                    FileOutputStream(partialFile.absolutePath, appendMode).use { outputStream ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                            if (totalSize > 0) {
                                val progress = ((totalBytesRead * 100) / totalSize).toInt().coerceIn(0, 100)
                                if (progress != prevProgress) {
                                    sendEvent("update/downloading", progress = progress)
                                    OneKeyLog.info("AppUpdate", "download progress: $progress% ($totalBytesRead/$totalSize)")
                                    builder.setProgress(100, progress, false)
                                    if (ActivityCompat.checkSelfPermission(
                                            context, android.Manifest.permission.POST_NOTIFICATIONS
                                        ) == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        notifyManager.notify(NOTIFICATION_ID, builder.build())
                                    }
                                    prevProgress = progress
                                }
                            }
                        }
                    }
                }

                OneKeyLog.info("AppUpdate", "downloadAPK: download finished, totalBytesRead=$totalBytesRead, finalizing...")

                // Promote .partial -> final ONLY after the full transfer. Doing
                // it before would mean a SHA mismatch leaves a half-baked
                // filePath that the next call would mistake for a cached good
                // APK.
                if (downloadedFile.exists()) downloadedFile.delete()
                if (!partialFile.renameTo(downloadedFile)) {
                    OneKeyLog.error("AppUpdate", "downloadAPK: rename .partial -> final failed")
                    throw Exception("Failed to finalize download")
                }

                OneKeyLog.info("AppUpdate", "Download completed")
                sendEvent("update/downloaded")

                notifyManager.cancel(NOTIFICATION_ID)
                builder.setContentText("")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                    .setAutoCancel(true)
                if (ActivityCompat.checkSelfPermission(
                        context, android.Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    notifyManager.notify(NOTIFICATION_ID, builder.build())
                }
            } catch (e: Exception) {
                OneKeyLog.error("AppUpdate", "downloadAPK: failed: ${e.javaClass.simpleName}: ${e.message}")
                sendEvent("update/error", message = "${e.javaClass.simpleName}: ${e.message}")
                throw e
            } finally {
                activeDownload.set(null)
                isDownloading.set(false)
            }
        }
    }

    override fun downloadASC(params: AppUpdateFileParams): Promise<Unit> {
        return Promise.async {
            val url = params.downloadUrl
            val filePath = filePathFromUrl(url)

            OneKeyLog.info("AppUpdate", "downloadASC: url=$url, filePath=$filePath")

            if (!url.startsWith("https://")) {
                OneKeyLog.error("AppUpdate", "downloadASC: URL is not HTTPS: $url")
                throw Exception("Download URL must use HTTPS")
            }

            val ascFileUrl = "$url.SHA256SUMS.asc"
            val ascFilePath = "$filePath.SHA256SUMS.asc"
            OneKeyLog.info("AppUpdate", "downloadASC: ascFileUrl=$ascFileUrl")

            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .followRedirects(false)
                .followSslRedirects(false)
                .build()
            val request = Request.Builder().url(ascFileUrl).build()
            // Wrap in .use so the Response (and its connection) is released on every
            // exit path — including the !isSuccessful / empty-body / oversize throws,
            // which previously leaked the connection back to the pool unclosed.
            val ascContent = client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    OneKeyLog.error("AppUpdate", "downloadASC: HTTP error, statusCode=${response.code}")
                    throw Exception(response.code.toString())
                }

                OneKeyLog.info("AppUpdate", "downloadASC: HTTP 200, reading ASC content...")

                val content = StringBuilder()
                val maxAscSize = 10 * 1024 // 10 KB max for ASC files
                val body = response.body ?: throw Exception("Empty ASC response body")
                BufferedReader(InputStreamReader(body.byteStream())).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        content.append(line).append("\n")
                        if (content.length > maxAscSize) {
                            OneKeyLog.error("AppUpdate", "downloadASC: ASC file exceeds max size ($maxAscSize bytes)")
                            throw Exception("ASC file exceeds maximum allowed size")
                        }
                    }
                }

                val parsed = content.toString()
                if (parsed.isEmpty()) {
                    OneKeyLog.error("AppUpdate", "downloadASC: ASC content is empty")
                    throw Exception("Empty ASC file")
                }
                parsed
            }

            OneKeyLog.info("AppUpdate", "downloadASC: ASC content size=${ascContent.length} bytes")

            val ascFile = buildFile(ascFilePath)
            if (ascFile.exists()) {
                OneKeyLog.info("AppUpdate", "downloadASC: existing ASC file found, deleting...")
                ascFile.delete()
            }
            FileOutputStream(ascFile).use { fos ->
                fos.write(ascContent.toByteArray())
            }
            OneKeyLog.info("AppUpdate", "downloadASC: saved ASC file to $ascFilePath")
        }
    }

    override fun verifyASC(params: AppUpdateFileParams): Promise<Unit> {
        return Promise.async {
            val filePath = filePathFromUrl(params.downloadUrl)
            OneKeyLog.info("AppUpdate", "verifyASC: filePath=$filePath")

            // Skip GPG verification only when BuildConfig allows it AND both DevSettings and skip-GPG toggle are enabled
            val devSettings = if (BuildConfig.ALLOW_SKIP_GPG_VERIFICATION) isDevSettingsEnabled() else false
            val skipGPGToggle = if (BuildConfig.ALLOW_SKIP_GPG_VERIFICATION) isSkipGPGEnabled() else false
            OneKeyLog.info("AppUpdate", "verifyASC: GPG check: devSettings=$devSettings, skipGPGToggle=$skipGPGToggle")
            if (BuildConfig.ALLOW_SKIP_GPG_VERIFICATION && devSettings && skipGPGToggle) {
                OneKeyLog.warn("AppUpdate", "verifyASC: GPG verification skipped (DevSettings + skip-GPG enabled)")
                return@async
            }

            try {

            val ascFilePath = "$filePath.SHA256SUMS.asc"
            val ascFile = buildFile(ascFilePath)
            if (!ascFile.exists()) {
                OneKeyLog.error("AppUpdate", "verifyASC: ASC file not found at $ascFilePath")
                throw Exception("ASC file not found")
            }

            val ascContent = ascFile.readText()
            OneKeyLog.info("AppUpdate", "verifyASC: ASC file loaded, size=${ascContent.length} bytes")

            // Verify the detached SHA256SUMS.asc cleartext signature and extract
            // the expected APK sha256. The BouncyCastle parse/verify, the OneKey
            // GPG public key, and the sha256-token validation now live in the
            // shared BundleCryptoCore.verifyDetachedAsc (single source of truth).
            // On any non-valid result we throw, preserving this method's
            // throw-on-failure contract; the reason taxonomy
            // (NOT_PGP_SIGNED_MESSAGE / INVALID_FORMAT / NO_SIGNATURE /
            // PUBKEY_NOT_FOUND / SIGNATURE_INVALID / SHA256_TOKEN_INVALID /
            // ERROR_*) is logged for diagnostics.
            OneKeyLog.info("AppUpdate", "verifyASC: verifying detached ASC signature via BundleCryptoCore...")
            val ascResult = BundleCryptoCore.verifyDetachedAsc(ascContent)
            // Capture in a local val so Kotlin can smart-cast to non-null below
            // (sha256 is a cross-module data-class property, which is not
            // smart-castable directly).
            val sha256 = ascResult.sha256
            if (!ascResult.valid || sha256 == null) {
                OneKeyLog.error("AppUpdate", "verifyASC: ASC verification FAILED: ${ascResult.reason}")
                throw Exception("ASC signature verification failed: ${ascResult.reason}")
            }
            OneKeyLog.info("AppUpdate", "verifyASC: ASC verified OK, extracted SHA256=${sha256.take(16)}...")

            // Verify APK file SHA256
            val apkFile = buildFile(filePath)
            if (!apkFile.exists()) {
                OneKeyLog.error("AppUpdate", "verifyASC: APK file not found at $filePath")
                throw Exception("APK file not found: $filePath")
            }

            OneKeyLog.info("AppUpdate", "verifyASC: computing SHA256 of APK file (size=${apkFile.length()} bytes)...")
            val fileSha256 = computeSha256(apkFile)
            OneKeyLog.info("AppUpdate", "verifyASC: APK SHA256=${fileSha256.take(16)}...")

            if (!secureCompare(fileSha256, sha256)) {
                OneKeyLog.error("AppUpdate", "verifyASC: SHA256 MISMATCH — expected=${sha256.take(16)}..., got=${fileSha256.take(16)}...")
                throw Exception("SHA256 mismatch for APK file")
            }

            OneKeyLog.info("AppUpdate", "verifyASC: GPG signature + SHA256 verification passed")

            } catch (e: Exception) {
                OneKeyLog.error("AppUpdate", "verifyASC: failed: ${e.javaClass.simpleName}: ${e.message}")
                throw e
            }
        }
    }

    // Track verified files with their SHA-256 hash to prevent TOCTOU attacks
    private data class VerifiedFile(val canonicalPath: String, val sha256: String)
    private val verifiedFiles = java.util.Collections.synchronizedMap(mutableMapOf<String, String>())

    override fun verifyAPK(params: AppUpdateFileParams): Promise<Unit> {
        return Promise.async {
            val filePath = filePathFromUrl(params.downloadUrl)
            OneKeyLog.info("AppUpdate", "verifyAPK: filePath=$filePath")

            val file = buildFile(filePath)
            if (!file.exists()) {
                OneKeyLog.error("AppUpdate", "verifyAPK: APK file not found at $filePath")
                throw Exception("NOT_FOUND_PACKAGE")
            }
            OneKeyLog.info("AppUpdate", "verifyAPK: APK file found, size=${file.length()} bytes")

            val context = NitroModules.applicationContext
                ?: throw Exception("Application context unavailable")
            val pm = context.packageManager

            // Check package name
            OneKeyLog.info("AppUpdate", "verifyAPK: parsing APK package info...")
            val info = pm.getPackageArchiveInfo(file.absolutePath, 0)
            if (info?.packageName == null) {
                OneKeyLog.error("AppUpdate", "verifyAPK: failed to parse APK package info (INVALID_PACKAGE)")
                throw Exception("INVALID_PACKAGE")
            }
            OneKeyLog.info("AppUpdate", "verifyAPK: APK packageName=${info.packageName}, versionName=${info.versionName}, versionCode=${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) info.longVersionCode else @Suppress("DEPRECATION") info.versionCode}")

            val debugBuild = isDebuggable()

            // Check package name
            if (info.packageName != context.packageName) {
                OneKeyLog.error("AppUpdate", "verifyAPK: package name mismatch — APK=${info.packageName}, installed=${context.packageName}")
                if (!debugBuild) throw Exception("PACKAGE_NAME_MISMATCH")
                OneKeyLog.warn("AppUpdate", "verifyAPK: DEBUG build — ignoring package name mismatch")
            } else {
                OneKeyLog.info("AppUpdate", "verifyAPK: package name matches installed app")
            }

            // Verify APK signing certificate matches the installed app
            OneKeyLog.info("AppUpdate", "verifyAPK: verifying APK signing certificate (API level=${Build.VERSION.SDK_INT})...")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val apkInfo = pm.getPackageArchiveInfo(file.absolutePath, PackageManager.GET_SIGNING_CERTIFICATES)
                val installedInfo = pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                val apkSigners = apkInfo?.signingInfo?.apkContentsSigners
                val installedSigners = installedInfo?.signingInfo?.apkContentsSigners
                if (apkSigners == null || installedSigners == null) {
                    OneKeyLog.error("AppUpdate", "verifyAPK: signing info unavailable (apkSigners=${apkSigners != null}, installedSigners=${installedSigners != null})")
                    if (!debugBuild) throw Exception("SIGNATURE_UNAVAILABLE")
                    OneKeyLog.warn("AppUpdate", "verifyAPK: DEBUG build — ignoring unavailable signatures")
                } else {
                    OneKeyLog.info("AppUpdate", "verifyAPK: APK signers count=${apkSigners.size}, installed signers count=${installedSigners.size}")
                    if (apkSigners.toSet() != installedSigners.toSet()) {
                        OneKeyLog.error("AppUpdate", "verifyAPK: signing certificate MISMATCH")
                        if (!debugBuild) throw Exception("SIGNATURE_MISMATCH")
                        OneKeyLog.warn("AppUpdate", "verifyAPK: DEBUG build — ignoring certificate mismatch")
                    } else {
                        OneKeyLog.info("AppUpdate", "verifyAPK: signing certificate verified OK")
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val apkInfo = pm.getPackageArchiveInfo(file.absolutePath, PackageManager.GET_SIGNATURES)
                @Suppress("DEPRECATION")
                val installedInfo = pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
                val apkSignatures = apkInfo?.signatures
                val installedSignatures = installedInfo?.signatures
                if (apkSignatures == null || installedSignatures == null) {
                    OneKeyLog.error("AppUpdate", "verifyAPK: legacy signatures unavailable")
                    if (!debugBuild) throw Exception("SIGNATURE_UNAVAILABLE")
                    OneKeyLog.warn("AppUpdate", "verifyAPK: DEBUG build — ignoring unavailable signatures")
                } else {
                    OneKeyLog.info("AppUpdate", "verifyAPK: APK signatures count=${apkSignatures.size}, installed signatures count=${installedSignatures.size}")
                    if (apkSignatures.toSet() != installedSignatures.toSet()) {
                        OneKeyLog.error("AppUpdate", "verifyAPK: legacy signing certificate MISMATCH")
                        if (!debugBuild) throw Exception("SIGNATURE_MISMATCH")
                        OneKeyLog.warn("AppUpdate", "verifyAPK: DEBUG build — ignoring certificate mismatch")
                    } else {
                        OneKeyLog.info("AppUpdate", "verifyAPK: signing certificate verified OK")
                    }
                }
            }

            // Compute SHA-256 hash of the verified file for TOCTOU protection
            OneKeyLog.info("AppUpdate", "verifyAPK: computing SHA-256 hash for TOCTOU protection...")
            val fileHash = computeSha256(file)
            verifiedFiles[file.canonicalPath] = fileHash
            OneKeyLog.info("AppUpdate", "verifyAPK: APK verified successfully, hash=${fileHash.take(16)}..., stored for install verification")
        }
    }

    override fun installAPK(params: AppUpdateFileParams): Promise<Unit> {
        return Promise.async {
            val filePath = filePathFromUrl(params.downloadUrl)
            OneKeyLog.info("AppUpdate", "installAPK: filePath=$filePath")

            val context = NitroModules.applicationContext
                ?: throw Exception("Application context unavailable")
            val file = buildFile(filePath)
            if (!file.exists()) {
                OneKeyLog.error("AppUpdate", "installAPK: APK file not found at $filePath")
                throw Exception("NOT_FOUND_PACKAGE")
            }
            OneKeyLog.info("AppUpdate", "installAPK: APK file found, size=${file.length()} bytes")

            // Ensure verifyAPK was called before installation
            val debugBuild = isDebuggable()
            val expectedHash = verifiedFiles.remove(file.canonicalPath)
            if (expectedHash == null) {
                OneKeyLog.error("AppUpdate", "installAPK: APK was not verified before installation (no hash in verifiedFiles)")
                if (!debugBuild) throw Exception("APK must be verified before installation")
                OneKeyLog.warn("AppUpdate", "installAPK: DEBUG build — ignoring missing verification")
            }

            // Re-verify file hash to prevent TOCTOU attacks (file swapped after verification)
            if (expectedHash != null) {
                OneKeyLog.info("AppUpdate", "installAPK: expected hash=${expectedHash.take(16)}..., re-verifying TOCTOU...")
                val currentHash = computeSha256(file)
                if (currentHash != expectedHash) {
                    OneKeyLog.error("AppUpdate", "installAPK: TOCTOU check FAILED — file was modified after verification (current=${currentHash.take(16)}..., expected=${expectedHash.take(16)}...)")
                    if (!debugBuild) throw Exception("APK file was modified after verification")
                    OneKeyLog.warn("AppUpdate", "installAPK: DEBUG build — ignoring TOCTOU mismatch")
                } else {
                    OneKeyLog.info("AppUpdate", "installAPK: TOCTOU check passed, hash matches")
                }
            }

            // Parse APK info for logging
            val pm = context.packageManager
            val apkInfo = pm.getPackageArchiveInfo(file.absolutePath, 0)
            if (apkInfo != null) {
                val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) apkInfo.longVersionCode else @Suppress("DEPRECATION") apkInfo.versionCode.toLong()
                OneKeyLog.info("AppUpdate", "installAPK: APK package=${apkInfo.packageName}, versionName=${apkInfo.versionName}, versionCode=$versionCode")
            }

            OneKeyLog.info("AppUpdate", "installAPK: creating install intent (API level=${Build.VERSION.SDK_INT})...")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val apkUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.appupdate.fileprovider",
                    file
                )
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                OneKeyLog.info("AppUpdate", "installAPK: using FileProvider URI=$apkUri")
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
                OneKeyLog.info("AppUpdate", "installAPK: using file URI")
            }
            context.startActivity(intent)
            OneKeyLog.info("AppUpdate", "installAPK: install intent launched, awaiting user confirmation")
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
            val result = verifyAscContentAndExtractSha256(testSignature)
            val isValid = result == "2ada9c871104fc40649fa3de67a7d8e33faadc18e9abd587e8bb85be0a003eba"
            OneKeyLog.info("AppUpdate", "testVerification: GPG verification result: $isValid")
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
            OneKeyLog.info("AppUpdate", "testSkipVerification: result=$result")
            result
        }
    }

    override fun isSkipGpgVerificationAllowed(): Boolean {
        val result = BuildConfig.ALLOW_SKIP_GPG_VERIFICATION
        OneKeyLog.info("AppUpdate", "isSkipGpgVerificationAllowed: result=$result")
        return result
    }

    /**
     * Delete every file in cacheDir/apks/ (.apk / .partial / .progress /
     * .SHA256SUMS.asc). Tolerates a missing directory and per-file delete
     * failures (e.g. the OS reclaimed the cache mid-pass — see
     * verifyExistingApk's Indeterminate handling for the same defensive
     * stance). Pure file I/O: does NOT touch download/verification state, so
     * it is safe to share between clearCache (full reset) and clearApkCache
     * (JS-gated, no in-flight download to cancel). The [tag] flows into the
     * log lines so the two callers stay distinguishable in logcat.
     */
    private fun wipeApkCacheFiles(tag: String, protectedPaths: Set<String> = emptySet()) {
        // OCDS §5.8: stop any in-flight concurrent download (shutdownNow +
        // awaitTermination) BEFORE deleting its .segN, so a still-running worker
        // cannot resurrect a just-deleted segment or write to a deleted FD. Only
        // the two external cleanup paths (clearCache / clearApkCache) call this;
        // it is never invoked mid-download.
        activeDownload.getAndSet(null)?.let {
            OneKeyLog.info("AppUpdate", "$tag: cancelling in-flight download before wipe")
            it.cancel()
        }
        val context = NitroModules.applicationContext
        if (context == null) {
            OneKeyLog.warn("AppUpdate", "$tag: application context unavailable, skipping file cleanup")
            return
        }
        // Must match getApkDownloadDir() (filesDir/apks).
        val apkDir = File(context.filesDir, "apks")
        if (!apkDir.exists()) {
            OneKeyLog.info("AppUpdate", "$tag: apks cache directory does not exist, nothing to clean")
            return
        }
        val filesToDelete = apkDir.listFiles() ?: emptyArray()
        OneKeyLog.info("AppUpdate", "$tag: found ${filesToDelete.size} cached file(s) to delete in ${apkDir.absolutePath}")
        var deletedCount = 0
        var skippedCount = 0
        filesToDelete.forEach { file ->
            // Never delete a verified, pending-install APK (its canonical path is
            // still in verifiedFiles). Protects the install flow from a racing
            // cleanup that slipped past the JS status gate.
            if (protectedPaths.isNotEmpty() && file.canonicalPath in protectedPaths) {
                OneKeyLog.info("AppUpdate", "$tag: skipping verified pending-install file ${file.name}")
                skippedCount++
                return@forEach
            }
            val size = file.length()
            // The file may already be gone (concurrent OS cache reclaim);
            // treat a non-existent file as nothing to do, not a failure.
            if (!file.exists() || file.delete()) {
                OneKeyLog.debug("AppUpdate", "$tag: deleted ${file.name} (${size} bytes)")
                deletedCount++
            } else {
                OneKeyLog.warn("AppUpdate", "$tag: failed to delete ${file.name}")
            }
        }
        OneKeyLog.info("AppUpdate", "$tag: completed, deleted $deletedCount/${filesToDelete.size} files (skipped $skippedCount protected)")
    }

    override fun clearCache(): Promise<Unit> {
        return Promise.async {
            OneKeyLog.info("AppUpdate", "clearCache: starting cleanup...")
            isDownloading.set(false)
            val verifiedCount = verifiedFiles.size
            verifiedFiles.clear()
            OneKeyLog.info("AppUpdate", "clearCache: reset download state, cleared $verifiedCount verified file entries")

            // Clean up downloaded APK and ASC files from cacheDir/apks/ directory
            wipeApkCacheFiles("clearCache")
        }
    }

    override fun clearApkCache(): Promise<Unit> {
        return Promise.async {
            // Wipe downloaded APK artifacts from cacheDir/apks/ only. The JS
            // layer gates on app-update status before calling, but that read is
            // not atomic with this delete, so guard natively too:
            //  - bail entirely if a download is in flight (would yank its
            //    .partial/.progress out from under the writer);
            //  - never delete a verified, pending-install APK (still tracked in
            //    verifiedFiles) so an open installer keeps a readable file.
            // Deliberately do NOT touch isDownloading / verifiedFiles state here
            // (that's clearCache's job). Missing dir/files are tolerated.
            if (isDownloading.get()) {
                OneKeyLog.info("AppUpdate", "clearApkCache: download in progress, skipping APK cache cleanup")
                return@async
            }
            val protectedPaths = synchronized(verifiedFiles) { verifiedFiles.keys.toSet() }
            OneKeyLog.info(
                "AppUpdate",
                "clearApkCache: starting stale APK cache cleanup (protecting ${protectedPaths.size} verified file(s))...",
            )
            wipeApkCacheFiles("clearApkCache", protectedPaths)
        }
    }
}
