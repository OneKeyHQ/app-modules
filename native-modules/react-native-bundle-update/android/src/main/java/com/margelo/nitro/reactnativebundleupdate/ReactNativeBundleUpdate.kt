package com.margelo.nitro.reactnativebundleupdate

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise
import com.margelo.nitro.NitroModules
import com.margelo.nitro.nativelogger.OneKeyLog
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import org.json.JSONArray
import org.json.JSONObject

private data class BundleListener(
    val id: Double,
    val callback: (BundleDownloadEvent) -> Unit
)

// Public static store for CustomReactNativeHost access (called before JS starts)
object BundleUpdateStoreAndroid {
    private const val PREFS_NAME = "BundleUpdatePrefs"
    private const val NATIVE_VERSION_PREFS_NAME = "NativeVersionPrefs"
    private const val CURRENT_BUNDLE_VERSION_KEY = "currentBundleVersion"

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

    fun getCurrentBundleVersion(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(CURRENT_BUNDLE_VERSION_KEY, null)
    }

    fun setCurrentBundleVersionAndSignature(context: Context, version: String, signature: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentVersion = prefs.getString(CURRENT_BUNDLE_VERSION_KEY, "")
        val editor = prefs.edit()
        editor.putString(CURRENT_BUNDLE_VERSION_KEY, version)
        if (signature != null) {
            editor.putString(version, signature)
        }
        if (!currentVersion.isNullOrEmpty()) {
            editor.remove(currentVersion)
        }
        editor.apply()
    }

    fun clearUpdateBundleData(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
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

    fun calculateSHA256(filePath: String): String? {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            BufferedInputStream(FileInputStream(filePath)).use { bis ->
                val buffer = ByteArray(8192)
                var count: Int
                while (bis.read(buffer).also { count = it } > 0) {
                    digest.update(buffer, 0, count)
                }
            }
            bytesToHex(digest.digest())
        } catch (e: Exception) {
            OneKeyLog.error("BundleUpdate", "Error calculating SHA256: ${e.message}")
            null
        }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
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
                metadata[key] = obj.getString(key)
            }
        } catch (e: Exception) {
            OneKeyLog.error("BundleUpdate", "Error parsing JSON: ${e.message}")
        }
        return metadata
    }

    fun readMetadataFileSha256(signature: String?): String? {
        if (signature.isNullOrEmpty()) return null
        return try {
            val json = JSONObject(signature)
            val sha256 = json.optString("sha256", "")
            if (sha256.isEmpty()) null else sha256
        } catch (e: Exception) {
            OneKeyLog.debug("BundleUpdate", "readMetadataFileSha256: Error extracting SHA256: ${e.message}")
            null
        }
    }

    fun validateMetadataFileSha256(context: Context, currentBundleVersion: String, signature: String?): Boolean {
        val metadataFilePath = getMetadataFilePath(context, currentBundleVersion) ?: run {
            OneKeyLog.debug("BundleUpdate", "metadataFilePath is null")
            return false
        }
        val extractedSha256 = readMetadataFileSha256(signature)
        if (extractedSha256.isNullOrEmpty()) return false
        val calculated = calculateSHA256(metadataFilePath) ?: return false
        return calculated == extractedSha256
    }

    fun validateAllFilesInDir(context: Context, dirPath: String, metadata: Map<String, String>, appVersion: String, bundleVersion: String): Boolean {
        val dir = File(dirPath)
        if (!dir.exists() || !dir.isDirectory) return false

        val parentBundleDir = getBundleDir(context)
        val folderName = "$appVersion-$bundleVersion"
        val jsBundleDir = File(parentBundleDir, folderName).absolutePath + "/"

        if (!validateFilesRecursive(dir, metadata, jsBundleDir)) return false

        // Verify completeness
        for (entry in metadata.entries) {
            val expectedFile = File(jsBundleDir + entry.key)
            if (!expectedFile.exists()) {
                OneKeyLog.error("BundleUpdate", "[bundle-verify] File listed in metadata but missing on disk: ${entry.key}")
                return false
            }
        }
        return true
    }

    private fun validateFilesRecursive(dir: File, metadata: Map<String, String>, jsBundleDir: String): Boolean {
        val files = dir.listFiles() ?: return true
        for (file in files) {
            if (file.isDirectory) {
                if (!validateFilesRecursive(file, metadata, jsBundleDir)) return false
            } else {
                if (file.name.contains("metadata.json") || file.name.contains(".DS_Store")) continue
                val relativePath = file.absolutePath.replace(jsBundleDir, "")
                val expectedSHA256 = metadata[relativePath]
                if (expectedSHA256 == null) {
                    OneKeyLog.error("BundleUpdate", "[bundle-verify] File on disk not found in metadata: $relativePath")
                    return false
                }
                val actualSHA256 = calculateSHA256(file.absolutePath)
                if (actualSHA256 == null) {
                    OneKeyLog.error("BundleUpdate", "[bundle-verify] Failed to calculate SHA256 for file: $relativePath")
                    return false
                }
                if (expectedSHA256 != actualSHA256) {
                    OneKeyLog.error("BundleUpdate", "[bundle-verify] SHA256 mismatch for $relativePath")
                    return false
                }
            }
        }
        return true
    }

    fun readFileContent(file: File): String {
        val sb = StringBuilder()
        BufferedInputStream(FileInputStream(file)).use { bis ->
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (bis.read(buffer).also { bytesRead = it } != -1) {
                sb.append(String(buffer, 0, bytesRead, Charsets.UTF_8))
            }
        }
        return sb.toString()
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
            FileOutputStream(file).use { fos ->
                fos.write(arr.toString().toByteArray(Charsets.UTF_8))
            }
        } catch (e: Exception) {
            OneKeyLog.error("BundleUpdate", "writeFallbackUpdateBundleDataFile: ${e.message}")
        }
    }

    fun getCurrentBundleMainJSBundle(context: Context): String? {
        return try {
            val currentAppVersion = getAppVersion(context)
            val currentBundleVersion = getCurrentBundleVersion(context) ?: return null

            OneKeyLog.info("BundleUpdate", "currentAppVersion: $currentAppVersion, currentBundleVersion: $currentBundleVersion")

            val prevNativeVersion = getNativeVersion(context)
            if (prevNativeVersion.isEmpty()) return ""

            if (currentAppVersion != prevNativeVersion) {
                OneKeyLog.info("BundleUpdate", "currentAppVersion is not equal to prevNativeVersion $currentAppVersion $prevNativeVersion")
                clearUpdateBundleData(context)
                return null
            }

            val bundleDir = getCurrentBundleDir(context, currentBundleVersion) ?: return null
            if (!File(bundleDir).exists()) {
                OneKeyLog.info("BundleUpdate", "currentBundleDir does not exist")
                return null
            }

            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val signature = prefs.getString(currentBundleVersion, "") ?: ""

            if (!validateMetadataFileSha256(context, currentBundleVersion, signature)) return null

            val metadataContent = getMetadataFileContent(context, currentBundleVersion) ?: return null
            val metadata = parseMetadataJson(metadataContent)

            val lastDashIndex = currentBundleVersion.lastIndexOf("-")
            if (lastDashIndex > 0) {
                val appVer = currentBundleVersion.substring(0, lastDashIndex)
                val bundleVer = currentBundleVersion.substring(lastDashIndex + 1)
                if (!validateAllFilesInDir(context, bundleDir, metadata, appVer, bundleVer)) {
                    OneKeyLog.info("BundleUpdate", "validateAllFilesInDir failed on startup")
                    return null
                }
            }

            val mainJSBundleFile = File(bundleDir, "main.jsbundle.hbc")
            val mainJSBundlePath = mainJSBundleFile.absolutePath
            OneKeyLog.info("BundleUpdate", "mainJSBundlePath: $mainJSBundlePath")
            if (!mainJSBundleFile.exists()) {
                OneKeyLog.info("BundleUpdate", "mainJSBundleFile does not exist")
                return null
            }
            mainJSBundlePath
        } catch (e: Exception) {
            OneKeyLog.error("BundleUpdate", "Error getting bundle: ${e.message}")
            null
        }
    }

    fun getWebEmbedPath(context: Context): String {
        val currentBundleDir = getCurrentBundleDir(context, getCurrentBundleVersion(context)) ?: return ""
        return File(currentBundleDir, "web-embed").absolutePath
    }

    private fun deleteDirectory(directory: File) {
        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) deleteDirectory(file) else file.delete()
            }
            directory.delete()
        }
    }

    fun deleteDir(dir: File) = deleteDirectory(dir)

    fun unzipFile(zipFilePath: String, destDirectory: String) {
        val destDir = File(destDirectory)
        if (!destDir.exists()) destDir.mkdirs()

        val destDirPath: Path = Paths.get(destDir.canonicalPath)
        ZipInputStream(FileInputStream(zipFilePath)).use { zipIn ->
            var entry: ZipEntry? = zipIn.nextEntry
            while (entry != null) {
                val outFile = File(destDir, entry.name)
                val outPath: Path = Paths.get(outFile.canonicalPath)
                if (!outPath.startsWith(destDirPath)) {
                    throw java.io.IOException("Entry is outside of the target dir: ${entry.name}")
                }
                if (!entry.isDirectory) {
                    outFile.parentFile?.mkdirs()
                    FileOutputStream(outFile).use { fos ->
                        val buffer = ByteArray(1024)
                        var length: Int
                        while (zipIn.read(buffer).also { length = it } > 0) {
                            fos.write(buffer, 0, length)
                        }
                    }
                } else {
                    outFile.mkdirs()
                }
                zipIn.closeEntry()
                entry = zipIn.nextEntry
            }
        }
    }
}

@DoNotStrip
class ReactNativeBundleUpdate : HybridReactNativeBundleUpdateSpec() {

    companion object {
        private const val PREFS_NAME = "BundleUpdatePrefs"
    }

    private val listeners = CopyOnWriteArrayList<BundleListener>()
    private val nextListenerId = AtomicLong(1)
    private val isDownloading = AtomicBoolean(false)
    private val httpClient = OkHttpClient.Builder()
        .addNetworkInterceptor { chain ->
            val req = chain.request()
            if (!req.url.isHttps) {
                throw java.io.IOException("Redirect to non-HTTPS URL is not allowed: ${req.url}")
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

    override fun addDownloadListener(callback: (BundleDownloadEvent) -> Unit): Double {
        val id = nextListenerId.getAndIncrement().toDouble()
        listeners.add(BundleListener(id, callback))
        return id
    }

    override fun removeDownloadListener(id: Double) {
        listeners.removeAll { it.id == id }
    }

    private fun getContext(): Context {
        return NitroModules.applicationContext
            ?: throw Exception("Application context unavailable")
    }

    override fun downloadBundle(params: BundleDownloadParams): Promise<BundleDownloadResult> {
        return Promise.async {
            if (isDownloading.getAndSet(true)) {
                throw Exception("Already downloading")
            }

            val context = getContext()
            val appVersion = params.latestVersion
            val bundleVersion = params.bundleVersion
            val downloadUrl = params.downloadUrl
            val sha256 = params.sha256

            if (!downloadUrl.startsWith("https://")) {
                isDownloading.set(false)
                throw Exception("Bundle download URL must use HTTPS")
            }

            val fileName = "$appVersion-$bundleVersion.zip"
            val filePath = File(BundleUpdateStoreAndroid.getDownloadBundleDir(context), fileName).absolutePath

            val result = BundleDownloadResult(
                downloadedFile = filePath,
                downloadUrl = downloadUrl,
                latestVersion = appVersion,
                bundleVersion = bundleVersion,
                sha256 = sha256
            )

            OneKeyLog.info("BundleUpdate", "downloadBundle: filePath: $filePath")

            val downloadedFile = File(filePath)
            if (downloadedFile.exists()) {
                if (verifyBundleSHA256(filePath, sha256)) {
                    isDownloading.set(false)
                    Thread.sleep(1000)
                    sendEvent("update/complete")
                    return@async result
                } else {
                    downloadedFile.delete()
                }
            }

            sendEvent("update/start")

            val request = Request.Builder().url(downloadUrl).build()
            val response = httpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                isDownloading.set(false)
                sendEvent("update/error", message = response.code.toString())
                throw Exception("HTTP ${response.code}")
            }

            val body = response.body ?: throw Exception("Empty response body")
            val fileSize = if (params.fileSize > 0) params.fileSize.toLong() else body.contentLength()

            body.byteStream().use { inputStream ->
                FileOutputStream(filePath).use { outputStream ->
                    val buffer = ByteArray(8192)
                    var totalBytesRead = 0L
                    var bytesRead: Int

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        if (fileSize > 0) {
                            val progress = ((totalBytesRead * 100) / fileSize).toInt()
                            sendEvent("update/downloading", progress = progress)
                        }
                    }
                }
            }

            if (!verifyBundleSHA256(filePath, sha256)) {
                File(filePath).delete()
                isDownloading.set(false)
                sendEvent("update/error", message = "Bundle signature verification failed")
                throw Exception("Bundle signature verification failed")
            }

            sendEvent("update/complete")
            OneKeyLog.info("BundleUpdate", "Download completed")
            isDownloading.set(false)
            result
        }
    }

    private fun verifyBundleSHA256(bundlePath: String, sha256: String): Boolean {
        val calculated = BundleUpdateStoreAndroid.calculateSHA256(bundlePath) ?: return false
        val isValid = calculated == sha256
        OneKeyLog.debug("BundleUpdate", "verifyBundleSHA256: Valid: $isValid")
        return isValid
    }

    override fun downloadBundleASC(params: BundleDownloadASCParams): Promise<Void> {
        return Promise.async {
            val context = getContext()
            val appVersion = params.latestVersion
            val bundleVersion = params.bundleVersion
            val signature = params.signature

            val storageKey = "$appVersion-$bundleVersion"
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(storageKey, signature).apply()

            OneKeyLog.info("BundleUpdate", "downloadBundleASC: Stored signature for key: $storageKey")
        }
    }

    override fun verifyBundleASC(params: BundleVerifyASCParams): Promise<Void> {
        return Promise.async {
            val context = getContext()
            val filePath = params.downloadedFile
            val sha256 = params.sha256
            val appVersion = params.latestVersion
            val bundleVersion = params.bundleVersion
            val signature = params.signature
            val skipGPG = params.skipGPGVerification

            if (!skipGPG) {
                if (!verifyBundleSHA256(filePath, sha256)) {
                    throw Exception("Bundle signature verification failed")
                }
            }

            val folderName = "$appVersion-$bundleVersion"
            val destination = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName).absolutePath
            val destinationDir = File(destination)

            try {
                BundleUpdateStoreAndroid.unzipFile(filePath, destination)

                val metadataFile = File(destination, "metadata.json")
                if (!metadataFile.exists()) {
                    BundleUpdateStoreAndroid.deleteDir(destinationDir)
                    throw Exception("Failed to read metadata.json")
                }

                val currentBundleVersion = "$appVersion-$bundleVersion"
                if (!skipGPG) {
                    if (!BundleUpdateStoreAndroid.validateMetadataFileSha256(context, currentBundleVersion, signature)) {
                        BundleUpdateStoreAndroid.deleteDir(destinationDir)
                        throw Exception("Bundle signature verification failed")
                    }
                }

                val metadataContent = BundleUpdateStoreAndroid.readFileContent(metadataFile)
                val metadata = BundleUpdateStoreAndroid.parseMetadataJson(metadataContent)
                if (!BundleUpdateStoreAndroid.validateAllFilesInDir(context, destination, metadata, appVersion, bundleVersion)) {
                    BundleUpdateStoreAndroid.deleteDir(destinationDir)
                    throw Exception("Extracted files verification against metadata failed")
                }
            } catch (e: Exception) {
                if (destinationDir.exists()) {
                    BundleUpdateStoreAndroid.deleteDir(destinationDir)
                }
                throw e
            }
        }
    }

    override fun verifyBundle(params: BundleVerifyParams): Promise<Void> {
        return Promise.async {
            val context = getContext()
            val filePath = params.downloadedFile
            val sha256 = params.sha256
            val appVersion = params.latestVersion
            val bundleVersion = params.bundleVersion

            if (!verifyBundleSHA256(filePath, sha256)) {
                throw Exception("Bundle signature verification failed")
            }

            val folderName = "$appVersion-$bundleVersion"
            val destination = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName).absolutePath
            val metadataFile = File(destination, "metadata.json")
            if (!metadataFile.exists()) {
                throw Exception("Failed to read metadata.json")
            }

            val metadataContent = BundleUpdateStoreAndroid.readFileContent(metadataFile)
            val metadata = BundleUpdateStoreAndroid.parseMetadataJson(metadataContent)
            if (!BundleUpdateStoreAndroid.validateAllFilesInDir(context, destination, metadata, appVersion, bundleVersion)) {
                throw Exception("Bundle signature verification failed")
            }
        }
    }

    override fun installBundle(params: BundleInstallParams): Promise<Void> {
        return Promise.async {
            val context = getContext()
            val appVersion = params.latestVersion
            val bundleVersion = params.bundleVersion
            val signature = params.signature
            val skipGPG = params.skipGPGVerification

            val folderName = "$appVersion-$bundleVersion"
            val currentFolderName = BundleUpdateStoreAndroid.getCurrentBundleVersion(context)

            // Prevent version downgrade
            if (!skipGPG && !currentFolderName.isNullOrEmpty()) {
                val dashIndex = currentFolderName.lastIndexOf("-")
                if (dashIndex > 0) {
                    try {
                        val currentBundleVer = currentFolderName.substring(dashIndex + 1)
                        val currentVerNum = currentBundleVer.toLong()
                        val newVerNum = bundleVersion.toLong()
                        if (newVerNum < currentVerNum) {
                            throw Exception("Bundle version downgrade rejected: $bundleVersion < $currentBundleVer")
                        }
                    } catch (e: NumberFormatException) {
                        OneKeyLog.warn("BundleUpdate", "Could not parse bundle versions for downgrade check")
                    }
                }
            }

            // Verify bundle directory exists
            val bundleDirPath = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            if (!bundleDirPath.exists()) {
                throw Exception("Bundle directory not found: $folderName")
            }

            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val currentSignature = if (currentFolderName != null) prefs.getString(currentFolderName, "") ?: "" else ""

            BundleUpdateStoreAndroid.setCurrentBundleVersionAndSignature(context, folderName, signature)
            val nativeVersion = BundleUpdateStoreAndroid.getAppVersion(context) ?: ""
            BundleUpdateStoreAndroid.setNativeVersion(context, nativeVersion)

            // Manage fallback data
            try {
                val fallbackData = BundleUpdateStoreAndroid.readFallbackUpdateBundleDataFile(context)

                if (!currentFolderName.isNullOrEmpty()) {
                    val lastDashIndex = currentFolderName.lastIndexOf("-")
                    if (lastDashIndex > 0) {
                        val curAppVersion = currentFolderName.substring(0, lastDashIndex)
                        val curBundleVersion = currentFolderName.substring(lastDashIndex + 1)
                        if (currentSignature.isNotEmpty()) {
                            fallbackData.add(mutableMapOf(
                                "appVersion" to curAppVersion,
                                "bundleVersion" to curBundleVersion,
                                "signature" to currentSignature
                            ))
                        }
                    }
                }

                if (fallbackData.size > 3) {
                    val shifted = fallbackData.removeAt(0)
                    val shiftApp = shifted["appVersion"]
                    val shiftBundle = shifted["bundleVersion"]
                    if (shiftApp != null && shiftBundle != null) {
                        val shiftFolderName = "$shiftApp-$shiftBundle"
                        val oldDir = File(BundleUpdateStoreAndroid.getBundleDir(context), shiftFolderName)
                        if (oldDir.exists()) {
                            BundleUpdateStoreAndroid.deleteDir(oldDir)
                        }
                    }
                }

                BundleUpdateStoreAndroid.writeFallbackUpdateBundleDataFile(fallbackData, context)
            } catch (e: Exception) {
                OneKeyLog.error("BundleUpdate", "installBundle fallbackUpdateBundleData error: ${e.message}")
            }
        }
    }

    override fun clearBundle(): Promise<Void> {
        return Promise.async {
            val context = getContext()
            val downloadDir = File(BundleUpdateStoreAndroid.getDownloadBundleDir(context))
            if (downloadDir.exists()) {
                BundleUpdateStoreAndroid.deleteDir(downloadDir)
            }
            isDownloading.set(false)
        }
    }

    override fun clearAllJSBundleData(): Promise<TestResult> {
        return Promise.async {
            val context = getContext()
            val downloadDir = File(BundleUpdateStoreAndroid.getDownloadBundleDir(context))
            if (downloadDir.exists()) {
                BundleUpdateStoreAndroid.deleteDir(downloadDir)
            }
            val bundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context))
            if (bundleDir.exists()) {
                BundleUpdateStoreAndroid.deleteDir(bundleDir)
            }
            BundleUpdateStoreAndroid.clearUpdateBundleData(context)

            OneKeyLog.info("BundleUpdate", "clearAllJSBundleData: Successfully cleared all JS bundle data")
            TestResult(success = true, message = "Successfully cleared all JS bundle data")
        }
    }

    override fun getFallbackUpdateBundleData(): Promise<Array<FallbackBundleInfo>> {
        return Promise.async {
            val context = getContext()
            val data = BundleUpdateStoreAndroid.readFallbackUpdateBundleDataFile(context)
            data.mapNotNull { map ->
                val appVersion = map["appVersion"] ?: return@mapNotNull null
                val bundleVersion = map["bundleVersion"] ?: return@mapNotNull null
                val signature = map["signature"] ?: return@mapNotNull null
                FallbackBundleInfo(appVersion = appVersion, bundleVersion = bundleVersion, signature = signature)
            }.toTypedArray()
        }
    }

    override fun setCurrentUpdateBundleData(params: BundleSwitchParams): Promise<Void> {
        return Promise.async {
            val context = getContext()
            val bundleVersion = "${params.appVersion}-${params.bundleVersion}"
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString("currentBundleVersion", bundleVersion)
                .putString(bundleVersion, params.signature)
                .apply()
        }
    }

    override fun getWebEmbedPath(): String {
        val context = NitroModules.applicationContext ?: return ""
        return BundleUpdateStoreAndroid.getWebEmbedPath(context)
    }

    override fun getWebEmbedPathAsync(): Promise<String> {
        return Promise.async {
            val context = getContext()
            val path = BundleUpdateStoreAndroid.getWebEmbedPath(context)
            OneKeyLog.debug("BundleUpdate", "getWebEmbedPathAsync: $path")
            path
        }
    }

    override fun getJsBundlePath(): Promise<String> {
        return Promise.async {
            val context = getContext()
            BundleUpdateStoreAndroid.getCurrentBundleMainJSBundle(context) ?: ""
        }
    }

    override fun getNativeAppVersion(): Promise<String> {
        return Promise.async {
            val context = getContext()
            BundleUpdateStoreAndroid.getAppVersion(context) ?: ""
        }
    }

    override fun testVerification(): Promise<Boolean> {
        return Promise.async {
            // TODO: Integrate Gopenpgp for full GPG verification testing
            OneKeyLog.info("BundleUpdate", "testVerification: GPG verification pending integration")
            true
        }
    }

    override fun isBundleExists(appVersion: String, bundleVersion: String): Promise<Boolean> {
        return Promise.async {
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val bundlePath = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            bundlePath.exists()
        }
    }

    override fun verifyExtractedBundle(appVersion: String, bundleVersion: String): Promise<Void> {
        return Promise.async {
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val bundlePath = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            if (!bundlePath.exists()) throw Exception("Bundle directory not found")
            val metadataFile = File(bundlePath, "metadata.json")
            if (!metadataFile.exists()) throw Exception("metadata.json not found")

            val metadataContent = BundleUpdateStoreAndroid.readFileContent(metadataFile)
            val metadata = BundleUpdateStoreAndroid.parseMetadataJson(metadataContent)
            if (!BundleUpdateStoreAndroid.validateAllFilesInDir(context, bundlePath.absolutePath, metadata, appVersion, bundleVersion)) {
                throw Exception("File integrity check failed")
            }
        }
    }

    override fun listLocalBundles(): Promise<Array<LocalBundleInfo>> {
        return Promise.async {
            val context = getContext()
            val bundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context))
            val results = mutableListOf<LocalBundleInfo>()
            if (bundleDir.exists() && bundleDir.isDirectory) {
                bundleDir.listFiles()?.forEach { child ->
                    if (!child.isDirectory) return@forEach
                    val name = child.name
                    val lastDash = name.lastIndexOf('-')
                    if (lastDash <= 0) return@forEach
                    val appVer = name.substring(0, lastDash)
                    val bundleVer = name.substring(lastDash + 1)
                    if (appVer.isNotEmpty() && bundleVer.isNotEmpty()) {
                        results.add(LocalBundleInfo(appVersion = appVer, bundleVersion = bundleVer))
                    }
                }
            }
            results.toTypedArray()
        }
    }

    override fun getSha256FromFilePath(filePath: String): Promise<String> {
        return Promise.async {
            OneKeyLog.debug("BundleUpdate", "getSha256FromFilePath: $filePath")
            if (filePath.isEmpty()) return@async ""
            BundleUpdateStoreAndroid.calculateSHA256(filePath) ?: ""
        }
    }

    override fun testDeleteJsBundle(appVersion: String, bundleVersion: String): Promise<TestResult> {
        return Promise.async {
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val jsBundlePath = File(File(BundleUpdateStoreAndroid.getBundleDir(context), folderName), "main.jsbundle.hbc")

            if (jsBundlePath.exists()) {
                val success = jsBundlePath.delete()
                if (success) {
                    TestResult(success = true, message = "Deleted jsBundle: ${jsBundlePath.absolutePath}")
                } else {
                    throw Exception("Failed to delete jsBundle: ${jsBundlePath.absolutePath}")
                }
            } else {
                TestResult(success = false, message = "jsBundle not found: ${jsBundlePath.absolutePath}")
            }
        }
    }

    override fun testDeleteJsRuntimeDir(appVersion: String, bundleVersion: String): Promise<TestResult> {
        return Promise.async {
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val dir = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)

            if (dir.exists()) {
                BundleUpdateStoreAndroid.deleteDir(dir)
                if (!dir.exists()) {
                    TestResult(success = true, message = "Deleted js runtime directory: ${dir.absolutePath}")
                } else {
                    throw Exception("Failed to delete js runtime directory: ${dir.absolutePath}")
                }
            } else {
                TestResult(success = false, message = "js runtime directory not found: ${dir.absolutePath}")
            }
        }
    }

    override fun testDeleteMetadataJson(appVersion: String, bundleVersion: String): Promise<TestResult> {
        return Promise.async {
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val metadataFile = File(File(BundleUpdateStoreAndroid.getBundleDir(context), folderName), "metadata.json")

            if (metadataFile.exists()) {
                val success = metadataFile.delete()
                if (success) {
                    TestResult(success = true, message = "Deleted metadata.json: ${metadataFile.absolutePath}")
                } else {
                    throw Exception("Failed to delete metadata.json: ${metadataFile.absolutePath}")
                }
            } else {
                TestResult(success = false, message = "metadata.json not found: ${metadataFile.absolutePath}")
            }
        }
    }

    override fun testWriteEmptyMetadataJson(appVersion: String, bundleVersion: String): Promise<TestResult> {
        return Promise.async {
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val jsRuntimeDir = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            val metadataPath = File(jsRuntimeDir, "metadata.json")

            if (!jsRuntimeDir.exists()) {
                if (!jsRuntimeDir.mkdirs()) {
                    throw Exception("Failed to create directory: ${jsRuntimeDir.absolutePath}")
                }
            }

            val emptyJson = JSONObject()
            FileOutputStream(metadataPath).use { fos ->
                fos.write(emptyJson.toString(2).toByteArray(Charsets.UTF_8))
                fos.flush()
            }

            TestResult(success = true, message = "Created empty metadata.json: ${metadataPath.absolutePath}")
        }
    }
}
