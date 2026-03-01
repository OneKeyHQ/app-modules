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

// OneKey GPG public key for signature verification
private const val GPG_PUBLIC_KEY = """-----BEGIN PGP PUBLIC KEY BLOCK-----

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
P2Q5dClenjjjVA==
=F0np
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

    private fun buildFile(path: String): File {
        val file = File(path.replace("file:///", "/"))
        // Validate the resolved path is within the app's cache or files directory
        val context = NitroModules.applicationContext
        if (context != null) {
            val canonicalPath = file.canonicalPath
            val cacheDir = context.cacheDir.canonicalPath
            val filesDir = context.filesDir.canonicalPath
            val externalCacheDir = context.externalCacheDir?.canonicalPath
            val externalFilesDir = context.getExternalFilesDir(null)?.canonicalPath
            val allowed = canonicalPath.startsWith(cacheDir) ||
                    canonicalPath.startsWith(filesDir) ||
                    (externalCacheDir != null && canonicalPath.startsWith(externalCacheDir)) ||
                    (externalFilesDir != null && canonicalPath.startsWith(externalFilesDir))
            if (!allowed) {
                throw SecurityException("Path traversal blocked: $canonicalPath is outside allowed directories")
            }
        }
        return file
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }

    override fun downloadAPK(params: AppUpdateDownloadParams): Promise<Void> {
        return Promise.async {
            if (isDownloading.getAndSet(true)) return@async

            try {
                val url = params.downloadUrl
                val filePath = params.filePath
                val notificationTitle = params.notificationTitle
                val fileSize = params.fileSize.toLong()

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

                val downloadedFile = buildFile(filePath)
                if (downloadedFile.exists()) downloadedFile.delete()

                val client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    sendEvent("error", message = response.code.toString())
                    throw Exception(response.code.toString())
                }

                val body = response.body ?: throw Exception("Empty response body")
                val contentLength = if (fileSize > 0) fileSize else body.contentLength()
                val source = body.source()
                val sink = downloadedFile.sink().buffer()
                val sinkBuffer = sink.buffer

                var totalBytesRead = 0L
                val bufferSize = 8 * 1024L
                sendEvent("start")
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
                                sendEvent("downloading", progress = progress)
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
                sendEvent("downloaded")

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
            } finally {
                isDownloading.set(false)
            }
        }
    }

    override fun downloadASC(params: AppUpdateFileParams): Promise<Void> {
        return Promise.async {
            val url = params.downloadUrl
            val filePath = params.filePath
            val ascFileUrl = "$url.SHA256SUMS.asc"
            val ascFilePath = "$filePath.SHA256SUMS.asc"

            val client = OkHttpClient()
            val request = Request.Builder().url(ascFileUrl).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw Exception(response.code.toString())
            }

            val content = StringBuilder()
            val body = response.body ?: throw Exception("Empty ASC response body")
            BufferedReader(InputStreamReader(body.byteStream())).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    content.append(line).append("\n")
                }
            }

            val ascContent = content.toString()
            if (ascContent.isEmpty()) throw Exception("Empty ASC file")

            OneKeyLog.info("AppUpdate", "Downloaded ASC file")

            val ascFile = buildFile(ascFilePath)
            if (ascFile.exists()) ascFile.delete()
            FileOutputStream(ascFile).use { fos ->
                fos.write(ascContent.toByteArray())
            }
        }
    }

    override fun verifyASC(params: AppUpdateFileParams): Promise<Void> {
        return Promise.async {
            val filePath = params.filePath
            val ascFilePath = "$filePath.SHA256SUMS.asc"
            val ascFile = buildFile(ascFilePath)
            if (!ascFile.exists()) throw Exception("ASC file not found: $ascFilePath")

            val ascContent = ascFile.readText()
            if (!ascContent.contains("-----BEGIN PGP SIGNED MESSAGE-----")) {
                throw Exception("ASC file does not contain a PGP signed message")
            }

            // Parse the cleartext signed message
            val lines = ascContent.lines()
            val hashHeaderIdx = lines.indexOfFirst { it.startsWith("Hash:") }
            val sigStartIdx = lines.indexOfFirst { it == "-----BEGIN PGP SIGNATURE-----" }
            val sigEndIdx = lines.indexOfFirst { it == "-----END PGP SIGNATURE-----" }

            if (hashHeaderIdx < 0 || sigStartIdx < 0 || sigEndIdx < 0) {
                throw Exception("Invalid PGP cleartext signed message format")
            }

            val bodyStartIdx = hashHeaderIdx + 2
            val bodyLines = lines.subList(bodyStartIdx, sigStartIdx)
            val cleartextBody = bodyLines.joinToString("\r\n").trimEnd()

            val sigBlock = lines.subList(sigStartIdx, sigEndIdx + 1).joinToString("\n")

            // Parse signature
            val sigInputStream = PGPUtil.getDecoderStream(sigBlock.byteInputStream())
            val sigFactory = JcaPGPObjectFactory(sigInputStream)
            val signatureList = sigFactory.nextObject()

            if (signatureList !is PGPSignatureList || signatureList.isEmpty) {
                throw Exception("No PGP signature found in ASC file")
            }

            val pgpSignature = signatureList[0]
            val keyId = pgpSignature.keyID

            // Parse public key
            val pubKeyStream = PGPUtil.getDecoderStream(GPG_PUBLIC_KEY.byteInputStream())
            val pgpPubKeyRingCollection = PGPPublicKeyRingCollection(pubKeyStream, JcaKeyFingerprintCalculator())
            val publicKey = pgpPubKeyRingCollection.getPublicKey(keyId)
                ?: throw Exception("Public key not found for keyId: ${java.lang.Long.toHexString(keyId)}")

            // Verify signature
            pgpSignature.init(JcaPGPContentVerifierBuilderProvider().setProvider("BC"), publicKey)

            val unescapedLines = cleartextBody.lines().map { line ->
                if (line.startsWith("- ")) line.substring(2) else line
            }
            val dataToVerify = unescapedLines.joinToString("\r\n").toByteArray(Charsets.UTF_8)
            pgpSignature.update(dataToVerify)

            if (!pgpSignature.verify()) {
                throw Exception("GPG signature verification failed for ASC file")
            }

            // Extract SHA256 from cleartext (format: "<sha256hash>  <filename>\n" or just "<sha256hash>")
            val sha256 = cleartextBody.trim().split("\\s+".toRegex())[0]
            if (sha256.length != 64) {
                throw Exception("Invalid SHA256 hash in ASC file: $sha256")
            }

            // Verify APK file SHA256
            val apkFile = buildFile(filePath)
            if (!apkFile.exists()) throw Exception("APK file not found: $filePath")

            val digest = MessageDigest.getInstance("SHA-256")
            BufferedInputStream(FileInputStream(apkFile)).use { bis ->
                val buffer = ByteArray(8192)
                var count: Int
                while (bis.read(buffer).also { count = it } > 0) {
                    digest.update(buffer, 0, count)
                }
            }
            val fileSha256 = bytesToHex(digest.digest())

            if (fileSha256 != sha256) {
                throw Exception("SHA256 mismatch: expected $sha256, got $fileSha256")
            }

            OneKeyLog.info("AppUpdate", "GPG signature and SHA256 verification passed for APK")
        }
    }

    override fun verifyAPK(params: AppUpdateFileParams): Promise<Void> {
        return Promise.async {
            val file = buildFile(params.filePath)
            if (!file.exists()) throw Exception("NOT_FOUND_PACKAGE")

            val context = NitroModules.applicationContext
                ?: throw Exception("Application context unavailable")
            val pm = context.packageManager
            val info = pm.getPackageArchiveInfo(file.absolutePath, 0)
            if (info?.packageName == null) {
                throw Exception("INVALID_PACKAGE")
            }
            if (info.packageName != context.packageName) {
                throw Exception("PACKAGE_NAME_MISMATCH")
            }
            OneKeyLog.info("AppUpdate", "APK verified: ${info.packageName}")
        }
    }

    override fun installAPK(params: AppUpdateFileParams): Promise<Void> {
        return Promise.async {
            val context = NitroModules.applicationContext
                ?: throw Exception("Application context unavailable")
            val file = buildFile(params.filePath)
            if (!file.exists()) throw Exception("NOT_FOUND_PACKAGE")

            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val apkUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
            }
            context.startActivity(intent)
        }
    }

    override fun clearCache(): Promise<Void> {
        return Promise.async {
            isDownloading.set(false)
        }
    }
}
