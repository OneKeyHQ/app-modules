package com.margelo.nitro.reactnativebundleupdate

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise
import com.margelo.nitro.NitroModules
import com.margelo.nitro.nativelogger.OneKeyLog
import com.tencent.mmkv.MMKV
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection
import org.bouncycastle.openpgp.PGPUtil
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.json.JSONArray
import org.json.JSONObject

private data class BundleListener(
    val id: Double,
    val callback: (BundleDownloadEvent) -> Unit
)

// OneKey GPG public key for signature verification
private const val GPG_PUBLIC_KEY = """-----BEGIN PGP PUBLIC KEY BLOCK-----

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
P2Q5dClenjjjVA==
=F0np
-----END PGP PUBLIC KEY BLOCK-----"""

// Public static store for CustomReactNativeHost access (called before JS starts)
object BundleUpdateStoreAndroid {
    private val bcProvider = BouncyCastleProvider()
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
        val editor = prefs.edit()
        editor.putString(CURRENT_BUNDLE_VERSION_KEY, version)
        // Remove old signature key from prefs (legacy cleanup)
        if (!currentVersion.isNullOrEmpty()) {
            editor.remove(currentVersion)
        }
        editor.apply()

        // Store signature to file
        if (!signature.isNullOrEmpty()) {
            writeSignatureFile(context, version, signature)
        }
    }

    fun clearUpdateBundleData(context: Context) {
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
            OneKeyLog.error("BundleUpdate", "Error parsing metadata JSON: ${e.message}")
            throw Exception("Failed to parse metadata.json: ${e.message}")
        }
        if (metadata.isEmpty()) {
            throw Exception("metadata.json is empty or contains no file entries")
        }
        return metadata
    }

    fun readMetadataFileSha256(signature: String?): String? {
        if (signature.isNullOrEmpty()) return null

        // GPG cleartext signature verification is required
        val gpgResult = verifyGPGAndExtractSha256(signature)
        if (gpgResult != null) return gpgResult

        OneKeyLog.error("BundleUpdate", "readMetadataFileSha256: GPG verification failed, rejecting unsigned content")
        return null
    }

    /** Constant-time comparison to prevent timing attacks on hash values */
    fun secureCompare(a: String, b: String): Boolean {
        val aBytes = a.toByteArray(Charsets.UTF_8)
        val bBytes = b.toByteArray(Charsets.UTF_8)
        if (aBytes.size != bBytes.size) return false
        var result = 0
        for (i in aBytes.indices) {
            result = result or (aBytes[i].toInt() xor bBytes[i].toInt())
        }
        return result == 0
    }

    fun isSafeVersionString(version: String): Boolean {
        return version.isNotEmpty() && version.all { it.isLetterOrDigit() || it == '.' || it == '-' || it == '_' }
    }

    /**
     * Verify a PGP cleartext-signed message using BouncyCastle and extract the sha256.
     * Returns null if verification fails or the signature is not a PGP cleartext message.
     */
    fun verifyGPGAndExtractSha256(signature: String): String? {
        if (!signature.contains("-----BEGIN PGP SIGNED MESSAGE-----")) return null

        return try {
            // Parse the cleartext signed message
            val inputStream = signature.byteInputStream()
            val pgpFactory = JcaPGPObjectFactory(PGPUtil.getDecoderStream(inputStream))

            // Parse the public key
            val pubKeyStream = PGPUtil.getDecoderStream(GPG_PUBLIC_KEY.byteInputStream())
            val pgpPubKeyRingCollection = PGPPublicKeyRingCollection(pubKeyStream, JcaKeyFingerprintCalculator())

            // Extract cleartext and signature from the PGP signed message manually
            // BouncyCastle's cleartext handling requires manual parsing
            val lines = signature.lines()
            val hashHeaderIdx = lines.indexOfFirst { it.startsWith("Hash:") }
            val sigStartIdx = lines.indexOfFirst { it == "-----BEGIN PGP SIGNATURE-----" }
            val sigEndIdx = lines.indexOfFirst { it == "-----END PGP SIGNATURE-----" }

            if (hashHeaderIdx < 0 || sigStartIdx < 0 || sigEndIdx < 0) {
                OneKeyLog.error("BundleUpdate", "Invalid PGP cleartext signed message format")
                return null
            }

            // The cleartext body is between the Hash header blank line and the PGP SIGNATURE block
            val bodyStartIdx = hashHeaderIdx + 2 // skip Hash: line and the blank line after it
            val bodyLines = lines.subList(bodyStartIdx, sigStartIdx)
            // Remove trailing empty line that PGP adds
            val cleartextBody = bodyLines.joinToString("\r\n").trimEnd()

            // The signature block
            val sigBlock = lines.subList(sigStartIdx, sigEndIdx + 1).joinToString("\n")

            // Decode the signature
            val sigInputStream = PGPUtil.getDecoderStream(sigBlock.byteInputStream())
            val sigFactory = JcaPGPObjectFactory(sigInputStream)
            val signatureList = sigFactory.nextObject()

            if (signatureList !is org.bouncycastle.openpgp.PGPSignatureList || signatureList.isEmpty) {
                OneKeyLog.error("BundleUpdate", "No PGP signature found in message")
                return null
            }

            val pgpSignature = signatureList[0]
            val keyId = pgpSignature.keyID

            // Find the public key
            val publicKey = pgpPubKeyRingCollection.getPublicKey(keyId)
            if (publicKey == null) {
                OneKeyLog.error("BundleUpdate", "Public key not found for keyId: ${java.lang.Long.toHexString(keyId)}")
                return null
            }

            // Verify the signature
            pgpSignature.init(JcaPGPContentVerifierBuilderProvider().setProvider(bcProvider), publicKey)

            // Dash-unescape the cleartext per RFC 4880 Section 7.1
            val unescapedLines = cleartextBody.lines().map { line ->
                if (line.startsWith("- ")) line.substring(2) else line
            }
            val dataToVerify = unescapedLines.joinToString("\r\n").toByteArray(Charsets.UTF_8)
            pgpSignature.update(dataToVerify)

            if (!pgpSignature.verify()) {
                OneKeyLog.error("BundleUpdate", "GPG signature verification failed")
                return null
            }

            // Signature verified, parse the cleartext JSON
            val json = JSONObject(cleartextBody.trim())
            val sha256 = json.optString("sha256", "")
            if (sha256.isEmpty()) {
                OneKeyLog.error("BundleUpdate", "No sha256 field in signed cleartext JSON")
                return null
            }

            OneKeyLog.info("BundleUpdate", "GPG verification succeeded, sha256: $sha256")
            sha256
        } catch (e: Exception) {
            OneKeyLog.error("BundleUpdate", "GPG verification error: ${e.message}")
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
        return secureCompare(calculated, extractedSha256)
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
                if (!secureCompare(expectedSHA256, actualSHA256)) {
                    OneKeyLog.error("BundleUpdate", "[bundle-verify] SHA256 mismatch for $relativePath")
                    return false
                }
            }
        }
        return true
    }

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

    fun getCurrentBundleMainJSBundle(context: Context): String? {
        return try {
            val currentAppVersion = getAppVersion(context)
            val currentBundleVersion = getCurrentBundleVersion(context) ?: run {
                OneKeyLog.warn("BundleUpdate", "getJsBundlePath: no currentBundleVersion stored")
                return null
            }

            OneKeyLog.info("BundleUpdate", "currentAppVersion: $currentAppVersion, currentBundleVersion: $currentBundleVersion")

            val prevNativeVersion = getNativeVersion(context)
            if (prevNativeVersion.isEmpty()) {
                OneKeyLog.warn("BundleUpdate", "getJsBundlePath: prevNativeVersion is empty")
                return ""
            }

            if (currentAppVersion != prevNativeVersion) {
                OneKeyLog.info("BundleUpdate", "currentAppVersion is not equal to prevNativeVersion $currentAppVersion $prevNativeVersion")
                clearUpdateBundleData(context)
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

            val devSettingsEnabled = if (BuildConfig.ALLOW_SKIP_GPG_VERIFICATION) isDevSettingsEnabled(context) else false
            if (devSettingsEnabled) {
                OneKeyLog.warn("BundleUpdate", "Startup SHA256 validation skipped (DevSettings enabled)")
            }
            if (!devSettingsEnabled && !validateMetadataFileSha256(context, currentBundleVersion, signature)) {
                OneKeyLog.warn("BundleUpdate", "getJsBundlePath: validateMetadataFileSha256 failed, signatureLength=${signature.length}")
                return null
            }

            val metadataContent = getMetadataFileContent(context, currentBundleVersion) ?: run {
                OneKeyLog.warn("BundleUpdate", "getJsBundlePath: getMetadataFileContent returned null")
                return null
            }
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

    private fun deleteDirectory(directory: File) {
        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) deleteDirectory(file) else file.delete()
            }
            directory.delete()
        }
    }

    fun deleteDir(dir: File) = deleteDirectory(dir)

    private const val MAX_UNZIPPED_SIZE = 512L * 1024 * 1024  // 512 MB limit

    fun unzipFile(zipFilePath: String, destDirectory: String) {
        val destDir = File(destDirectory)
        if (!destDir.exists()) destDir.mkdirs()

        val destDirPath: Path = Paths.get(destDir.canonicalPath)
        var totalBytesWritten = 0L
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
                        val buffer = ByteArray(8192)
                        var length: Int
                        while (zipIn.read(buffer).also { length = it } > 0) {
                            totalBytesWritten += length
                            if (totalBytesWritten > MAX_UNZIPPED_SIZE) {
                                throw java.io.IOException("Decompression bomb detected: extracted size exceeds ${MAX_UNZIPPED_SIZE / 1024 / 1024} MB")
                            }
                            fos.write(buffer, 0, length)
                        }
                    }
                    // Validate no symlink was created (zip slip via symlink attack)
                    if (Files.isSymbolicLink(outFile.toPath())) {
                        outFile.delete()
                        throw java.io.IOException("Symlink detected in zip entry: ${entry.name}")
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

    override fun downloadBundle(params: BundleDownloadParams): Promise<BundleDownloadResult> {
        return Promise.async {
            if (isDownloading.getAndSet(true)) {
                OneKeyLog.warn("BundleUpdate", "downloadBundle: rejected, already downloading")
                throw Exception("Already downloading")
            }

            try {
            val context = getContext()
            val appVersion = params.latestVersion
            val bundleVersion = params.bundleVersion
            val downloadUrl = params.downloadUrl
            val sha256 = params.sha256

            OneKeyLog.info("BundleUpdate", "downloadBundle: appVersion=$appVersion, bundleVersion=$bundleVersion, fileSize=${params.fileSize}, url=$downloadUrl")

            if (!BundleUpdateStoreAndroid.isSafeVersionString(appVersion) ||
                !BundleUpdateStoreAndroid.isSafeVersionString(bundleVersion)) {
                OneKeyLog.error("BundleUpdate", "downloadBundle: invalid version string format: appVersion=$appVersion, bundleVersion=$bundleVersion")
                throw Exception("Invalid version string format")
            }

            if (!downloadUrl.startsWith("https://")) {
                OneKeyLog.error("BundleUpdate", "downloadBundle: URL is not HTTPS: $downloadUrl")
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

            OneKeyLog.info("BundleUpdate", "downloadBundle: filePath=$filePath")

            val downloadedFile = File(filePath)
            if (downloadedFile.exists()) {
                OneKeyLog.info("BundleUpdate", "downloadBundle: file already exists, verifying SHA256...")
                if (verifyBundleSHA256(filePath, sha256)) {
                    OneKeyLog.info("BundleUpdate", "downloadBundle: existing file SHA256 valid, skipping download")
                    isDownloading.set(false)
                    Thread.sleep(1000)
                    sendEvent("update/complete")
                    return@async result
                } else {
                    OneKeyLog.warn("BundleUpdate", "downloadBundle: existing file SHA256 mismatch, re-downloading")
                    downloadedFile.delete()
                }
            }

            sendEvent("update/start")
            OneKeyLog.info("BundleUpdate", "downloadBundle: starting download...")

            val request = Request.Builder().url(downloadUrl).build()
            val response = httpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                OneKeyLog.error("BundleUpdate", "downloadBundle: HTTP error, statusCode=${response.code}")
                sendEvent("update/error", message = "HTTP ${response.code}")
                throw Exception("HTTP ${response.code}")
            }

            val body = response.body ?: throw Exception("Empty response body")
            val fileSize = if (params.fileSize > 0) params.fileSize.toLong() else body.contentLength()
            OneKeyLog.info("BundleUpdate", "downloadBundle: HTTP 200, contentLength=$fileSize, downloading...")

            // Ensure parent directory exists before writing
            val parentDir = File(filePath).parentFile
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs()
                OneKeyLog.info("BundleUpdate", "downloadBundle: created parent directory: ${parentDir.absolutePath}")
            }

            var totalBytesRead = 0L
            body.byteStream().use { inputStream ->
                FileOutputStream(filePath).use { outputStream ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int

                    var prevProgress = 0
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        if (fileSize > 0) {
                            val progress = ((totalBytesRead * 100) / fileSize).toInt()
                            if (progress != prevProgress) {
                                sendEvent("update/downloading", progress = progress)
                                OneKeyLog.info("BundleUpdate", "download progress: $progress% ($totalBytesRead/$fileSize)")
                                prevProgress = progress
                            }
                        }
                    }
                }
            }

            val downloadedFileAfter = File(filePath)
            OneKeyLog.info("BundleUpdate", "downloadBundle: download finished, totalBytesRead=$totalBytesRead, fileExists=${downloadedFileAfter.exists()}, fileSize=${if (downloadedFileAfter.exists()) downloadedFileAfter.length() else -1}, verifying SHA256...")
            if (!verifyBundleSHA256(filePath, sha256)) {
                File(filePath).delete()
                OneKeyLog.error("BundleUpdate", "downloadBundle: SHA256 verification failed after download")
                sendEvent("update/error", message = "Bundle signature verification failed")
                throw Exception("Bundle signature verification failed")
            }

            sendEvent("update/complete")
            OneKeyLog.info("BundleUpdate", "downloadBundle: completed successfully, appVersion=$appVersion, bundleVersion=$bundleVersion")
            result
            } catch (e: Exception) {
                OneKeyLog.error("BundleUpdate", "downloadBundle: failed: ${e.javaClass.simpleName}: ${e.message}")
                sendEvent("update/error", message = "${e.javaClass.simpleName}: ${e.message}")
                throw e
            } finally {
                isDownloading.set(false)
            }
        }
    }

    private fun verifyBundleSHA256(bundlePath: String, sha256: String): Boolean {
        val calculated = BundleUpdateStoreAndroid.calculateSHA256(bundlePath)
        if (calculated == null) {
            OneKeyLog.error("BundleUpdate", "verifyBundleSHA256: failed to calculate SHA256 for: $bundlePath")
            return false
        }
        val isValid = BundleUpdateStoreAndroid.secureCompare(calculated, sha256)
        OneKeyLog.debug("BundleUpdate", "verifyBundleSHA256: path=$bundlePath, expected=${sha256.take(16)}..., calculated=${calculated.take(16)}..., valid=$isValid")
        return isValid
    }

    override fun downloadBundleASC(params: BundleDownloadASCParams): Promise<Unit> {
        return Promise.async {
            val context = getContext()
            val appVersion = params.latestVersion
            val bundleVersion = params.bundleVersion
            val signature = params.signature

            OneKeyLog.info("BundleUpdate", "downloadBundleASC: appVersion=$appVersion, bundleVersion=$bundleVersion, signatureLength=${signature.length}")

            val storageKey = "$appVersion-$bundleVersion"
            BundleUpdateStoreAndroid.writeSignatureFile(context, storageKey, signature)

            OneKeyLog.info("BundleUpdate", "downloadBundleASC: stored signature for key=$storageKey")
        }
    }

    override fun verifyBundleASC(params: BundleVerifyASCParams): Promise<Unit> {
        return Promise.async {
            val context = getContext()
            val filePath = params.downloadedFile
            val sha256 = params.sha256
            val appVersion = params.latestVersion
            val bundleVersion = params.bundleVersion
            val signature = params.signature

            OneKeyLog.info("BundleUpdate", "verifyBundleASC: appVersion=$appVersion, bundleVersion=$bundleVersion, file=$filePath, signatureLength=${signature.length}")

            // GPG verification skipped only when both DevSettings and skip-GPG toggle are enabled
            val skipGPG = BuildConfig.ALLOW_SKIP_GPG_VERIFICATION && isDevSettingsEnabled() && isSkipGPGEnabled()
            OneKeyLog.info("BundleUpdate", "verifyBundleASC: GPG check: skipGPG=$skipGPG")

            if (!skipGPG) {
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: verifying SHA256 of downloaded file...")
                if (!verifyBundleSHA256(filePath, sha256)) {
                    OneKeyLog.error("BundleUpdate", "verifyBundleASC: SHA256 verification failed for file=$filePath")
                    throw Exception("Bundle signature verification failed")
                }
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: SHA256 verified OK")
            } else {
                OneKeyLog.warn("BundleUpdate", "verifyBundleASC: SHA256 + GPG verification skipped (DevSettings enabled)")
            }

            val folderName = "$appVersion-$bundleVersion"
            val destination = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName).absolutePath
            val destinationDir = File(destination)

            try {
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: extracting zip to $destination...")
                BundleUpdateStoreAndroid.unzipFile(filePath, destination)
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: extraction completed")

                val metadataFile = File(destination, "metadata.json")
                if (!metadataFile.exists()) {
                    OneKeyLog.error("BundleUpdate", "verifyBundleASC: metadata.json not found after extraction")
                    BundleUpdateStoreAndroid.deleteDir(destinationDir)
                    throw Exception("Failed to read metadata.json")
                }

                val currentBundleVersion = "$appVersion-$bundleVersion"
                if (!skipGPG) {
                    OneKeyLog.info("BundleUpdate", "verifyBundleASC: validating GPG signature for metadata...")
                    if (!BundleUpdateStoreAndroid.validateMetadataFileSha256(context, currentBundleVersion, signature)) {
                        OneKeyLog.error("BundleUpdate", "verifyBundleASC: GPG signature verification failed")
                        BundleUpdateStoreAndroid.deleteDir(destinationDir)
                        throw Exception("Bundle signature verification failed")
                    }
                    OneKeyLog.info("BundleUpdate", "verifyBundleASC: GPG signature verified OK")
                }

                OneKeyLog.info("BundleUpdate", "verifyBundleASC: validating all extracted files against metadata...")
                val metadataContent = BundleUpdateStoreAndroid.readFileContent(metadataFile)
                val metadata = BundleUpdateStoreAndroid.parseMetadataJson(metadataContent)
                if (!BundleUpdateStoreAndroid.validateAllFilesInDir(context, destination, metadata, appVersion, bundleVersion)) {
                    OneKeyLog.error("BundleUpdate", "verifyBundleASC: file integrity check failed")
                    BundleUpdateStoreAndroid.deleteDir(destinationDir)
                    throw Exception("Extracted files verification against metadata failed")
                }

                OneKeyLog.info("BundleUpdate", "verifyBundleASC: all verifications passed, appVersion=$appVersion, bundleVersion=$bundleVersion")
            } catch (e: Exception) {
                if (destinationDir.exists()) {
                    BundleUpdateStoreAndroid.deleteDir(destinationDir)
                }
                throw e
            }
        }
    }

    override fun verifyBundle(params: BundleVerifyParams): Promise<Unit> {
        return Promise.async {
            val filePath = params.downloadedFile
            val sha256 = params.sha256
            val appVersion = params.latestVersion
            val bundleVersion = params.bundleVersion

            OneKeyLog.info("BundleUpdate", "verifyBundle: appVersion=$appVersion, bundleVersion=$bundleVersion, file=$filePath")

            // Verify SHA256 of the downloaded file
            val calculated = BundleUpdateStoreAndroid.calculateSHA256(filePath)
            if (calculated == null) {
                OneKeyLog.error("BundleUpdate", "verifyBundle: failed to calculate SHA256 for file=$filePath")
                throw Exception("Failed to calculate SHA256")
            }
            if (!BundleUpdateStoreAndroid.secureCompare(calculated, sha256)) {
                OneKeyLog.error("BundleUpdate", "verifyBundle: SHA256 mismatch, expected=${sha256.take(16)}..., got=${calculated.take(16)}...")
                throw Exception("SHA256 verification failed")
            }

            OneKeyLog.info("BundleUpdate", "verifyBundle: SHA256 verified OK for appVersion=$appVersion, bundleVersion=$bundleVersion")
        }
    }

    override fun installBundle(params: BundleInstallParams): Promise<Unit> {
        return Promise.async {
            val context = getContext()
            val appVersion = params.latestVersion
            val bundleVersion = params.bundleVersion
            val signature = params.signature

            OneKeyLog.info("BundleUpdate", "installBundle: appVersion=$appVersion, bundleVersion=$bundleVersion, signatureLength=${signature.length}")

            // GPG verification skipped only when both DevSettings and skip-GPG toggle are enabled
            val skipGPG = BuildConfig.ALLOW_SKIP_GPG_VERIFICATION && isDevSettingsEnabled() && isSkipGPGEnabled()
            OneKeyLog.info("BundleUpdate", "installBundle: GPG check: skipGPG=$skipGPG")

            val folderName = "$appVersion-$bundleVersion"
            val currentFolderName = BundleUpdateStoreAndroid.getCurrentBundleVersion(context)
            OneKeyLog.info("BundleUpdate", "installBundle: target=$folderName, current=$currentFolderName")

            // Verify bundle directory exists
            val bundleDirPath = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            if (!bundleDirPath.exists()) {
                OneKeyLog.error("BundleUpdate", "installBundle: bundle directory not found: ${bundleDirPath.absolutePath}")
                throw Exception("Bundle directory not found: $folderName")
            }

            val currentSignature = if (currentFolderName != null) BundleUpdateStoreAndroid.readSignatureFile(context, currentFolderName) else ""

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
                        BundleUpdateStoreAndroid.deleteSignatureFile(context, shiftFolderName)
                    }
                }

                BundleUpdateStoreAndroid.writeFallbackUpdateBundleDataFile(fallbackData, context)
                OneKeyLog.info("BundleUpdate", "installBundle: completed successfully, installed version=$folderName, fallbackCount=${fallbackData.size}")
            } catch (e: Exception) {
                OneKeyLog.error("BundleUpdate", "installBundle: fallbackUpdateBundleData error: ${e.message}")
            }
        }
    }

    override fun clearBundle(): Promise<Unit> {
        return Promise.async {
            OneKeyLog.info("BundleUpdate", "clearBundle: clearing download directory...")
            val context = getContext()
            val downloadDir = File(BundleUpdateStoreAndroid.getDownloadBundleDir(context))
            if (downloadDir.exists()) {
                BundleUpdateStoreAndroid.deleteDir(downloadDir)
                OneKeyLog.info("BundleUpdate", "clearBundle: download directory deleted")
            } else {
                OneKeyLog.info("BundleUpdate", "clearBundle: download directory does not exist, skipping")
            }
            isDownloading.set(false)
            OneKeyLog.info("BundleUpdate", "clearBundle: completed")
        }
    }

    override fun resetToBuiltInBundle(): Promise<Unit> {
        return Promise.async {
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
        return Promise.async {
            OneKeyLog.info("BundleUpdate", "clearAllJSBundleData: starting...")
            val context = getContext()
            val downloadDir = File(BundleUpdateStoreAndroid.getDownloadBundleDir(context))
            if (downloadDir.exists()) {
                BundleUpdateStoreAndroid.deleteDir(downloadDir)
                OneKeyLog.info("BundleUpdate", "clearAllJSBundleData: deleted download dir")
            }
            val bundleDir = File(BundleUpdateStoreAndroid.getBundleDir(context))
            if (bundleDir.exists()) {
                BundleUpdateStoreAndroid.deleteDir(bundleDir)
                OneKeyLog.info("BundleUpdate", "clearAllJSBundleData: deleted bundle dir")
            }
            BundleUpdateStoreAndroid.clearUpdateBundleData(context)

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
        return Promise.async {
            val context = getContext()
            val bundleVersion = "${params.appVersion}-${params.bundleVersion}"
            OneKeyLog.info("BundleUpdate", "setCurrentUpdateBundleData: switching to $bundleVersion")

            // Verify the bundle directory actually exists
            val bundleDirPath = File(BundleUpdateStoreAndroid.getBundleDir(context), bundleVersion)
            if (!bundleDirPath.exists()) {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: bundle directory not found: ${bundleDirPath.absolutePath}")
                throw Exception("Bundle directory not found")
            }

            // Verify GPG signature is valid (skipped when both DevSettings and skip-GPG toggle are enabled)
            val skipGPGSwitch = BuildConfig.ALLOW_SKIP_GPG_VERIFICATION && isDevSettingsEnabled() && isSkipGPGEnabled()
            OneKeyLog.info("BundleUpdate", "setCurrentUpdateBundleData: GPG check: skipGPG=$skipGPGSwitch")
            if (!skipGPGSwitch) {
                if (params.signature.isEmpty() ||
                    !BundleUpdateStoreAndroid.validateMetadataFileSha256(context, bundleVersion, params.signature)) {
                    OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: GPG signature verification failed")
                    throw Exception("Bundle signature verification failed")
                }
                OneKeyLog.info("BundleUpdate", "setCurrentUpdateBundleData: GPG signature verified OK")
            } else {
                OneKeyLog.warn("BundleUpdate", "setCurrentUpdateBundleData: GPG signature verification skipped (DevSettings + skip-GPG enabled)")
            }

            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString("currentBundleVersion", bundleVersion)
                .apply()
            if (params.signature.isNotEmpty()) {
                BundleUpdateStoreAndroid.writeSignatureFile(context, bundleVersion, params.signature)
            }
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

    override fun getNativeAppVersion(): Promise<String> {
        return Promise.async {
            val context = getContext()
            val version = BundleUpdateStoreAndroid.getAppVersion(context) ?: ""
            OneKeyLog.info("BundleUpdate", "getNativeAppVersion: $version")
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
            val result = BundleUpdateStoreAndroid.verifyGPGAndExtractSha256(testSignature)
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
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val bundlePath = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            val exists = bundlePath.exists()
            OneKeyLog.info("BundleUpdate", "isBundleExists: appVersion=$appVersion, bundleVersion=$bundleVersion, exists=$exists")
            exists
        }
    }

    override fun verifyExtractedBundle(appVersion: String, bundleVersion: String): Promise<Unit> {
        return Promise.async {
            OneKeyLog.info("BundleUpdate", "verifyExtractedBundle: appVersion=$appVersion, bundleVersion=$bundleVersion")
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val bundlePath = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            if (!bundlePath.exists()) {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: bundle directory not found: ${bundlePath.absolutePath}")
                throw Exception("Bundle directory not found")
            }
            val metadataFile = File(bundlePath, "metadata.json")
            if (!metadataFile.exists()) {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: metadata.json not found in ${bundlePath.absolutePath}")
                throw Exception("metadata.json not found")
            }

            OneKeyLog.info("BundleUpdate", "verifyExtractedBundle: parsing metadata and validating files...")
            val metadataContent = BundleUpdateStoreAndroid.readFileContent(metadataFile)
            val metadata = BundleUpdateStoreAndroid.parseMetadataJson(metadataContent)
            if (!BundleUpdateStoreAndroid.validateAllFilesInDir(context, bundlePath.absolutePath, metadata, appVersion, bundleVersion)) {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: file integrity check failed")
                throw Exception("File integrity check failed")
            }
            OneKeyLog.info("BundleUpdate", "verifyExtractedBundle: all files verified OK, fileCount=${metadata.size}")
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
            if (!resolvedPath.startsWith(bundleDir) && !resolvedPath.startsWith(downloadDir)) {
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
            OneKeyLog.info("BundleUpdate", "testDeleteJsBundle: appVersion=$appVersion, bundleVersion=$bundleVersion")
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val jsBundlePath = File(File(BundleUpdateStoreAndroid.getBundleDir(context), folderName), "main.jsbundle.hbc")

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
            OneKeyLog.info("BundleUpdate", "testDeleteJsRuntimeDir: appVersion=$appVersion, bundleVersion=$bundleVersion")
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val dir = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)

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
            OneKeyLog.info("BundleUpdate", "testDeleteMetadataJson: appVersion=$appVersion, bundleVersion=$bundleVersion")
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val metadataFile = File(File(BundleUpdateStoreAndroid.getBundleDir(context), folderName), "metadata.json")

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
            OneKeyLog.info("BundleUpdate", "testWriteEmptyMetadataJson: appVersion=$appVersion, bundleVersion=$bundleVersion")
            val context = getContext()
            val folderName = "$appVersion-$bundleVersion"
            val jsRuntimeDir = File(BundleUpdateStoreAndroid.getBundleDir(context), folderName)
            val metadataPath = File(jsRuntimeDir, "metadata.json")

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
