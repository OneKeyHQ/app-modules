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
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

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
        return File(path.replace("file:///", "/"))
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
            // GPG signature verification is not yet implemented.
            // Reject to prevent silently accepting unverified APKs.
            throw Exception("GPG signature verification is not yet implemented. Cannot verify ASC for: ${params.filePath}")
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
