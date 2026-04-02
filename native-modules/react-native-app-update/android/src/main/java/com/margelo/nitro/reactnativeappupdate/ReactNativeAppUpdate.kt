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
import okio.buffer
import okio.sink
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.security.MessageDigest
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection
import org.bouncycastle.openpgp.PGPSignatureList
import org.bouncycastle.openpgp.PGPUtil
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider
import org.bouncycastle.jce.provider.BouncyCastleProvider

// OneKey GPG public key for signature verification
private const val GPG_PUBLIC_KEY = """-----BEGIN PGP PUBLIC KEY BLOCK-----

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
=HlTQ
-----END PGP PUBLIC KEY BLOCK-----"""

private data class Listener(
    val id: Double,
    val callback: (DownloadEvent) -> Unit
)

@DoNotStrip
class ReactNativeAppUpdate : HybridReactNativeAppUpdateSpec() {

    companion object {
        private const val CHANNEL_ID = "updateApp"
        private const val NOTIFICATION_ID = 1
        // Use our own BouncyCastle provider instance to avoid Android's stripped-down built-in "BC"
        private val bcProvider = BouncyCastleProvider()
    }

    private val listeners = CopyOnWriteArrayList<Listener>()
    private val nextListenerId = AtomicLong(1)
    private val isDownloading = AtomicBoolean(false)
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

    private fun getApkCacheDir(): File {
        val context = NitroModules.applicationContext
            ?: throw SecurityException("Application context unavailable")
        val apkDir = File(context.cacheDir, "apks")
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
            File(getApkCacheDir(), stripped)
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

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun computeSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        BufferedInputStream(FileInputStream(file)).use { bis ->
            val buffer = ByteArray(8192)
            var count: Int
            while (bis.read(buffer).also { count = it } > 0) {
                digest.update(buffer, 0, count)
            }
        }
        return bytesToHex(digest.digest())
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
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return null
        val content = StringBuilder()
        val maxAscSize = 10 * 1024
        val body = response.body ?: return null
        BufferedReader(InputStreamReader(body.byteStream())).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                content.append(line).append("\n")
                if (content.length > maxAscSize) return null
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
     */
    private fun verifyAscContentAndExtractSha256(ascContent: String): String? {
        if (!ascContent.contains("-----BEGIN PGP SIGNED MESSAGE-----")) return null

        val lines = ascContent.lines()
        val hashHeaderIdx = lines.indexOfFirst { it.startsWith("Hash:") }
        val sigStartIdx = lines.indexOfFirst { it == "-----BEGIN PGP SIGNATURE-----" }
        val sigEndIdx = lines.indexOfFirst { it == "-----END PGP SIGNATURE-----" }
        if (hashHeaderIdx < 0 || sigStartIdx < 0 || sigEndIdx < 0) return null

        val bodyStartIdx = hashHeaderIdx + 2
        val bodyLines = lines.subList(bodyStartIdx, sigStartIdx)
        val cleartextBody = bodyLines.joinToString("\r\n").trimEnd()
        val sigBlock = lines.subList(sigStartIdx, sigEndIdx + 1).joinToString("\n")

        // Verify GPG signature
        val sigInputStream = PGPUtil.getDecoderStream(sigBlock.byteInputStream())
        val sigFactory = JcaPGPObjectFactory(sigInputStream)
        val signatureList = sigFactory.nextObject()
        if (signatureList !is PGPSignatureList || signatureList.isEmpty) return null

        val pgpSignature = signatureList[0]
        val pubKeyStream = PGPUtil.getDecoderStream(GPG_PUBLIC_KEY.byteInputStream())
        val pgpPubKeyRingCollection = PGPPublicKeyRingCollection(pubKeyStream, JcaKeyFingerprintCalculator())
        val publicKey = pgpPubKeyRingCollection.getPublicKey(pgpSignature.keyID) ?: return null

        pgpSignature.init(JcaPGPContentVerifierBuilderProvider().setProvider(bcProvider), publicKey)
        val unescapedLines = cleartextBody.lines().map { line ->
            if (line.startsWith("- ")) line.substring(2) else line
        }
        val dataToVerify = unescapedLines.joinToString("\r\n").toByteArray(Charsets.UTF_8)
        pgpSignature.update(dataToVerify)
        if (!pgpSignature.verify()) return null

        // Extract SHA256 from verified cleartext
        val sha256 = cleartextBody.trim().split("\\s+".toRegex())[0].lowercase()
        if (sha256.length != 64 || !sha256.all { it in '0'..'9' || it in 'a'..'f' }) return null
        return sha256
    }

    private fun verifyAscAndExtractSha256(ascFile: File): String? {
        return verifyAscContentAndExtractSha256(ascFile.readText())
    }

    /** Constant-time comparison to prevent timing attacks on hash values */
    private fun secureCompare(a: String, b: String): Boolean {
        val aBytes = a.toByteArray(Charsets.UTF_8)
        val bBytes = b.toByteArray(Charsets.UTF_8)
        if (aBytes.size != bBytes.size) return false
        var result = 0
        for (i in aBytes.indices) {
            result = result or (aBytes[i].toInt() xor bBytes[i].toInt())
        }
        return result == 0
    }

    /**
     * Check if an existing APK is valid by verifying GPG signature and SHA256 against the ASC file.
     * Downloads ASC if not present. Returns true if APK is valid.
     */
    private fun tryVerifyExistingApk(url: String, filePath: String, apkFile: File): Boolean {
        return try {
            val ascFilePath = "$filePath.SHA256SUMS.asc"
            val ascFile = buildFile(ascFilePath)

            if (!ascFile.exists()) {
                val ascUrl = "$url.SHA256SUMS.asc"
                OneKeyLog.info("AppUpdate", "tryVerifyExistingApk: ASC not found, downloading from $ascUrl")
                if (downloadAscFile(ascUrl, ascFile) == null) {
                    OneKeyLog.warn("AppUpdate", "tryVerifyExistingApk: ASC download failed")
                    return false
                }
                OneKeyLog.info("AppUpdate", "tryVerifyExistingApk: ASC downloaded to ${ascFile.absolutePath}")
            }

            val expectedSha256 = verifyAscAndExtractSha256(ascFile)
            if (expectedSha256 == null) {
                OneKeyLog.warn("AppUpdate", "tryVerifyExistingApk: GPG verification or SHA256 extraction failed")
                return false
            }

            OneKeyLog.info("AppUpdate", "tryVerifyExistingApk: computing SHA256 of existing APK (size=${apkFile.length()})...")
            val actualSha256 = computeSha256(apkFile)

            if (secureCompare(actualSha256, expectedSha256)) {
                OneKeyLog.info("AppUpdate", "tryVerifyExistingApk: SHA256 matches, APK is valid")
                true
            } else {
                OneKeyLog.warn("AppUpdate", "tryVerifyExistingApk: SHA256 mismatch, expected=${expectedSha256.take(16)}..., got=${actualSha256.take(16)}...")
                false
            }
        } catch (e: Exception) {
            OneKeyLog.warn("AppUpdate", "tryVerifyExistingApk: failed: ${e.javaClass.simpleName}: ${e.message}")
            false
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

                val downloadedFile = buildFile(filePath)
                if (downloadedFile.exists()) {
                    OneKeyLog.info("AppUpdate", "downloadAPK: existing APK found (size=${downloadedFile.length()}), verifying...")
                    if (tryVerifyExistingApk(url, filePath, downloadedFile)) {
                        OneKeyLog.info("AppUpdate", "downloadAPK: existing APK is valid, skipping download")
                        sendEvent("update/downloaded")
                        return@async
                    }
                    OneKeyLog.info("AppUpdate", "downloadAPK: existing APK invalid, deleting and re-downloading...")
                    downloadedFile.delete()
                }

                val client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .followRedirects(false)
                    .followSslRedirects(false)
                    .build()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    OneKeyLog.error("AppUpdate", "downloadAPK: HTTP error, statusCode=${response.code}")
                    sendEvent("update/error", message = response.code.toString())
                    throw Exception(response.code.toString())
                }

                val body = response.body ?: throw Exception("Empty response body")
                val contentLength = if (fileSize > 0) fileSize else body.contentLength()
                OneKeyLog.info("AppUpdate", "downloadAPK: HTTP 200, contentLength=$contentLength, starting download...")
                val source = body.source()
                val sink = downloadedFile.sink().buffer()
                val sinkBuffer = sink.buffer

                var totalBytesRead = 0L
                val bufferSize = 8 * 1024L
                sendEvent("update/start")
                var prevProgress = 0

                try {
                    while (true) {
                        val bytesRead = source.read(sinkBuffer, bufferSize)
                        if (bytesRead == -1L) break
                        sink.emit()
                        totalBytesRead += bytesRead
                        if (contentLength > 0) {
                            val progress = ((totalBytesRead * 100) / contentLength).toInt()
                            if (prevProgress != progress) {
                                sendEvent("update/downloading", progress = progress)
                                OneKeyLog.info("AppUpdate", "download progress: $progress%")
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
                } finally {
                    sink.flush()
                    sink.close()
                    source.close()
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
            val response = client.newCall(request).execute()

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

            val ascContent = content.toString()
            if (ascContent.isEmpty()) {
                OneKeyLog.error("AppUpdate", "downloadASC: ASC content is empty")
                throw Exception("Empty ASC file")
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

            if (!ascContent.contains("-----BEGIN PGP SIGNED MESSAGE-----")) {
                OneKeyLog.error("AppUpdate", "verifyASC: ASC file missing PGP signed message header")
                throw Exception("ASC file does not contain a PGP signed message")
            }

            // Parse the cleartext signed message
            val lines = ascContent.lines()
            val hashHeaderIdx = lines.indexOfFirst { it.startsWith("Hash:") }
            val sigStartIdx = lines.indexOfFirst { it == "-----BEGIN PGP SIGNATURE-----" }
            val sigEndIdx = lines.indexOfFirst { it == "-----END PGP SIGNATURE-----" }

            if (hashHeaderIdx < 0 || sigStartIdx < 0 || sigEndIdx < 0) {
                OneKeyLog.error("AppUpdate", "verifyASC: invalid cleartext format (hashHeader=$hashHeaderIdx, sigStart=$sigStartIdx, sigEnd=$sigEndIdx)")
                throw Exception("Invalid PGP cleartext signed message format")
            }

            OneKeyLog.info("AppUpdate", "verifyASC: parsed cleartext message structure OK")

            val bodyStartIdx = hashHeaderIdx + 2
            val bodyLines = lines.subList(bodyStartIdx, sigStartIdx)
            val cleartextBody = bodyLines.joinToString("\r\n").trimEnd()

            val sigBlock = lines.subList(sigStartIdx, sigEndIdx + 1).joinToString("\n")

            // Parse signature
            OneKeyLog.info("AppUpdate", "verifyASC: parsing PGP signature...")
            val sigInputStream = PGPUtil.getDecoderStream(sigBlock.byteInputStream())
            val sigFactory = JcaPGPObjectFactory(sigInputStream)
            val signatureList = sigFactory.nextObject()

            if (signatureList !is PGPSignatureList || signatureList.isEmpty) {
                OneKeyLog.error("AppUpdate", "verifyASC: no PGP signature found in ASC file")
                throw Exception("No PGP signature found in ASC file")
            }

            val pgpSignature = signatureList[0]
            val keyId = pgpSignature.keyID
            OneKeyLog.info("AppUpdate", "verifyASC: signature keyID=${java.lang.Long.toHexString(keyId).uppercase()}")

            // Parse public key
            OneKeyLog.info("AppUpdate", "verifyASC: loading GPG public key...")
            val pubKeyStream = PGPUtil.getDecoderStream(GPG_PUBLIC_KEY.byteInputStream())
            val pgpPubKeyRingCollection = PGPPublicKeyRingCollection(pubKeyStream, JcaKeyFingerprintCalculator())
            val publicKey = pgpPubKeyRingCollection.getPublicKey(keyId)
            if (publicKey == null) {
                OneKeyLog.error("AppUpdate", "verifyASC: GPG public key not found for keyID=${java.lang.Long.toHexString(keyId).uppercase()}")
                throw Exception("GPG public key not found for signature verification")
            }
            OneKeyLog.info("AppUpdate", "verifyASC: public key matched, verifying signature...")

            // Verify signature
            pgpSignature.init(JcaPGPContentVerifierBuilderProvider().setProvider(bcProvider), publicKey)

            val unescapedLines = cleartextBody.lines().map { line ->
                if (line.startsWith("- ")) line.substring(2) else line
            }
            val dataToVerify = unescapedLines.joinToString("\r\n").toByteArray(Charsets.UTF_8)
            pgpSignature.update(dataToVerify)

            if (!pgpSignature.verify()) {
                OneKeyLog.error("AppUpdate", "verifyASC: GPG signature verification FAILED")
                throw Exception("GPG signature verification failed for ASC file")
            }
            OneKeyLog.info("AppUpdate", "verifyASC: GPG signature verified OK")

            // Extract SHA256 from cleartext (format: "<sha256hash>  <filename>\n" or just "<sha256hash>")
            val sha256 = cleartextBody.trim().split("\\s+".toRegex())[0].lowercase()
            OneKeyLog.info("AppUpdate", "verifyASC: extracted SHA256=${sha256.take(16)}...")

            if (sha256.length != 64 || !sha256.all { it in '0'..'9' || it in 'a'..'f' }) {
                OneKeyLog.error("AppUpdate", "verifyASC: invalid SHA256 hash format (length=${sha256.length})")
                throw Exception("Invalid SHA256 hash format in ASC file")
            }

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

    override fun clearCache(): Promise<Unit> {
        return Promise.async {
            OneKeyLog.info("AppUpdate", "clearCache: starting cleanup...")
            isDownloading.set(false)
            val verifiedCount = verifiedFiles.size
            verifiedFiles.clear()
            OneKeyLog.info("AppUpdate", "clearCache: reset download state, cleared $verifiedCount verified file entries")

            // Clean up downloaded APK and ASC files from cacheDir/apks/ directory
            val context = NitroModules.applicationContext
            if (context != null) {
                val apkDir = File(context.cacheDir, "apks")
                if (apkDir.exists()) {
                    val filesToDelete = apkDir.listFiles() ?: emptyArray()
                    OneKeyLog.info("AppUpdate", "clearCache: found ${filesToDelete.size} cached file(s) to delete in ${apkDir.absolutePath}")
                    var deletedCount = 0
                    filesToDelete.forEach { file ->
                        val size = file.length()
                        if (file.delete()) {
                            OneKeyLog.debug("AppUpdate", "clearCache: deleted ${file.name} (${size} bytes)")
                            deletedCount++
                        } else {
                            OneKeyLog.warn("AppUpdate", "clearCache: failed to delete ${file.name}")
                        }
                    }
                    OneKeyLog.info("AppUpdate", "clearCache: completed, deleted $deletedCount/${filesToDelete.size} files")
                } else {
                    OneKeyLog.info("AppUpdate", "clearCache: apks cache directory does not exist, nothing to clean")
                }
            } else {
                OneKeyLog.warn("AppUpdate", "clearCache: application context unavailable, skipping file cleanup")
            }
        }
    }
}
