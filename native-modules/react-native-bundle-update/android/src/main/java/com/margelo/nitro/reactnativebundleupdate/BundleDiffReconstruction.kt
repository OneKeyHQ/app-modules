package com.margelo.nitro.reactnativebundleupdate

import com.margelo.nitro.reactnativebundlecrypto.BundleFileSystem
import com.margelo.nitro.reactnativebundlecrypto.BundleFileType
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

internal sealed class BundleDiffReconstructionException(message: String) : Exception(message) {
    class SecurityRejected(message: String) : BundleDiffReconstructionException(message)
    class IntegrityFailed(message: String) : BundleDiffReconstructionException(message)
    class FallbackRequired(message: String) : BundleDiffReconstructionException(message)
}

internal data class BundleDiffManifest(
    val schemaVersion: Int,
    val appType: String,
    val appVersion: String,
    val baseAppVersion: String,
    val baseMetadataSha256: String,
    val targetBundleVersion: String,
    val targetMetadataSha256: String,
    val hashAlgorithm: String,
    val changedFiles: Map<String, String>,
    val reusedFiles: Map<String, String>,
    val deletedFiles: List<String>,
)

internal object BundleDiffReconstruction {
    const val EXPECTED_SCHEMA_VERSION = 1
    const val EXPECTED_HASH_ALGORITHM = "sha256"
    const val MAX_BUNDLE_ENTRIES = 4096
    const val MAX_BUNDLE_ARCHIVE_BYTES = 512L * 1024 * 1024
    const val MAX_BUNDLE_UNCOMPRESSED_BYTES = 512L * 1024 * 1024
    const val MAX_CONTROL_JSON_BYTES = 16L * 1024 * 1024
    const val STAGING_ROOT_DIR_NAME = ".diff-staging"
    const val DIFF_DOWNLOAD_DIR_NAME = ".diff"
    const val DIFF_ZIP_EXTENSION = ".diff.zip"
    const val DIFF_META_EXTENSION = ".diff.zip.meta.json"
    private const val DESCRIPTOR_TEMP_PREFIX = "diff-descriptor-"
    private const val DESCRIPTOR_TEMP_SUFFIX = ".tmp"

    // Temporary file to versioned destination, shared by native module instances.
    private val activeDescriptorWrites = mutableMapOf<File, File>()
    private val diffSegmentSuffix = Regex("""\.seg\d+$""")
    private val diffStageSuffixes = listOf(".meta.json", ".resume", ".progress", ".partial", DIFF_ZIP_EXTENSION)

    class ArchiveSizeLimitExceeded : SecurityException("Bundle archive exceeds size limit")

    fun requireArchiveBytes(existingBytes: Long, incomingBytes: Long = 0L): Long {
        if (existingBytes > MAX_BUNDLE_ARCHIVE_BYTES - incomingBytes) throw ArchiveSizeLimitExceeded()
        return existingBytes + incomingBytes
    }

    private val singleStreamContentRange = Regex("""bytes\s+(\d+)-(\d+)/(\d+)""", RegexOption.IGNORE_CASE)

    fun singleStreamRangeTotal(header: String?, offset: Long): Long? {
        val bounds = header?.let(singleStreamContentRange::matchEntire) ?: return null
        val start = bounds.groupValues[1].toLongOrNull() ?: return null
        val end = bounds.groupValues[2].toLongOrNull() ?: return null
        val total = bounds.groupValues[3].toLongOrNull() ?: return null
        return total.takeIf { start == offset && end >= start && end < total }
    }

    fun targetMetadataSizeIfPresent(targetDir: File, metadataFile: File, fileSystem: BundleFileSystem): Long? {
        val target = fileSystem.lstat(targetDir) ?: return null
        if (target.type != BundleFileType.DIRECTORY) {
            throw BundleDiffReconstructionException.IntegrityFailed("Bundle target is not a directory")
        }
        val metadata = fileSystem.lstat(metadataFile)
        if (metadata?.type != BundleFileType.REGULAR) {
            throw BundleDiffReconstructionException.IntegrityFailed("Bundle target has no regular metadata control file")
        }
        return metadata.size
    }

    // Shared by all native module instances in this process.
    private val liveStagingDirectories = mutableSetOf<File>()

    fun retainStagingDirectory(directory: File) {
        synchronized(liveStagingDirectories) {
            liveStagingDirectories.add(directory.absoluteFile)
        }
    }

    fun releaseStagingDirectory(directory: File, fileSystem: BundleFileSystem) {
        synchronized(liveStagingDirectories) {
            deleteEntryWithoutFollowingLinks(directory, fileSystem)
            liveStagingDirectories.remove(directory.absoluteFile)
        }
    }

    fun sweepStagingDirectory(root: File, fileSystem: BundleFileSystem) {
        synchronized(liveStagingDirectories) {
            val rootType = try { fileSystem.lstat(root)?.type } catch (_: IOException) { return }
            if (rootType != BundleFileType.DIRECTORY) return
            root.listFiles()?.forEach { entry ->
                if (entry.absoluteFile !in liveStagingDirectories) deleteEntryWithoutFollowingLinks(entry, fileSystem)
            }
        }
    }

    private fun deleteEntryWithoutFollowingLinks(entry: File, fileSystem: BundleFileSystem) {
        try {
            // Walk without following links, including an abandoned symlink entry.
            val pending = ArrayDeque<Pair<File, Boolean>>()
            pending.addLast(entry to false)
            while (pending.isNotEmpty()) {
                val (file, visited) = pending.removeLast()
                if (!visited && fileSystem.lstat(file)?.type == BundleFileType.DIRECTORY) {
                    val children = file.listFiles() ?: continue
                    pending.addLast(file to true)
                    for (child in children) pending.addLast(child to false)
                } else {
                    file.delete()
                }
            }
        } catch (_: IOException) {
            // Missing entries and cleanup failures are safe to retry at the next sweep.
        }
    }

    fun unzipFile(
        zipFilePath: String,
        destDirectory: String,
        fileSystem: BundleFileSystem,
        maxUncompressedBytes: Long = MAX_BUNDLE_UNCOMPRESSED_BYTES,
        maxEntries: Int = MAX_BUNDLE_ENTRIES,
    ) {
        val destDir = File(destDirectory)
        if (!destDir.exists()) destDir.mkdirs()

        val canonicalDestDir = destDir.canonicalPath
        val destDirPrefix = canonicalDestDir.trimEnd(File.separatorChar) + File.separator
        var totalBytesWritten = 0L
        var entryCount = 0
        val zipStream = object : ZipInputStream(FileInputStream(zipFilePath)) {
            override fun createZipEntry(name: String): ZipEntry {
                // Android 14 checks these paths after creating the entry and reports a generic ZipException.
                if (name.startsWith('/') || name.split('/').any { it == ".." }) {
                    throw SecurityException("Unsafe zip entry path")
                }
                return super.createZipEntry(name)
            }
        }
        zipStream.use { zipIn ->
            var entry: ZipEntry? = zipIn.nextEntry
            while (entry != null) {
                entryCount++
                if (entryCount > maxEntries) {
                    throw SecurityException("Zip entry count exceeds maximum allowed ($maxEntries)")
                }
                val outFile = File(destDir, entry.name)
                if (fileSystem.isSymbolicLink(outFile)) {
                    throw SecurityException("Symlink detected at zip entry destination: ${entry.name}")
                }
                val outPath = outFile.canonicalPath
                val isConfined = if (outPath == canonicalDestDir) entry.isDirectory else outPath.startsWith(destDirPrefix)
                if (!isConfined) {
                    throw SecurityException("Entry is outside of the target dir: ${entry.name}")
                }
                if (!entry.isDirectory) {
                    outFile.parentFile?.mkdirs()
                    FileOutputStream(outFile).use { fos ->
                        val buffer = ByteArray(8192)
                        var length: Int
                        while (zipIn.read(buffer).also { length = it } > 0) {
                            totalBytesWritten += length
                            if (totalBytesWritten > maxUncompressedBytes) {
                                throw SecurityException("Zip extracted size exceeds maximum allowed ($maxUncompressedBytes bytes)")
                            }
                            fos.write(buffer, 0, length)
                        }
                    }
                } else {
                    if (zipIn.read() != -1) {
                        throw SecurityException("Zip directory entry contains payload data")
                    }
                    outFile.mkdirs()
                }
                zipIn.closeEntry()
                entry = zipIn.nextEntry
            }
        }
    }

    internal data class DiffArchiveDescriptor(
        val appVersion: String,
        val bundleVersion: String,
        val downloadUrl: String,
        val fileSize: Double,
        val sha256: String,
        val diffSha256: String,
        val signature: String? = null,
        val fallbackRequired: Boolean = false,
    )

    fun isRetryableDiffDownloadError(code: String): Boolean {
        return code == "NETWORK_ERROR" || code == "TIMEOUT"
    }

    fun isRetryableNetworkIOException(error: Throwable): Boolean {
        return error is java.net.SocketTimeoutException ||
            error is java.net.UnknownHostException ||
            error is java.net.ConnectException ||
            error is java.net.ProtocolException ||
            error is java.net.SocketException ||
            error is java.io.EOFException ||
            error is java.io.InterruptedIOException
    }

    fun rangeTaskId(bundleKey: String, isDiff: Boolean): String {
        return if (isDiff) "diff:$bundleKey" else "full:$bundleKey"
    }

    fun rangeTaskIdForFilePath(filePath: String): String {
        val file = File(filePath)
        val fileName = file.name
        val parentName = file.parentFile?.name.orEmpty()
        if (parentName == DIFF_DOWNLOAD_DIR_NAME && fileName.endsWith(DIFF_ZIP_EXTENSION)) {
            val bundleKey = fileName.removeSuffix(DIFF_ZIP_EXTENSION)
            return rangeTaskId(bundleKey, isDiff = true)
        }
        if (fileName.endsWith(".zip")) {
            val bundleKey = fileName.removeSuffix(".zip")
            return rangeTaskId(bundleKey, isDiff = false)
        }
        return rangeTaskId(file.nameWithoutExtension, isDiff = false)
    }

    fun descriptorPreservingVerifiedSignature(
        newDescriptor: DiffArchiveDescriptor,
        existingDescriptor: DiffArchiveDescriptor?,
    ): DiffArchiveDescriptor {
        if (existingDescriptor == null ||
            existingDescriptor.appVersion != newDescriptor.appVersion ||
            existingDescriptor.bundleVersion != newDescriptor.bundleVersion ||
            !constantTimeEqualHex(existingDescriptor.sha256, newDescriptor.sha256) ||
            !constantTimeEqualHex(existingDescriptor.diffSha256, newDescriptor.diffSha256)
        ) {
            return newDescriptor
        }
        val mergedSignature = newDescriptor.signature?.takeIf { it.isNotEmpty() }
            ?: existingDescriptor.signature?.takeIf { it.isNotEmpty() }
        return newDescriptor.copy(
            fileSize = if (newDescriptor.fileSize > 0.0) newDescriptor.fileSize else existingDescriptor.fileSize,
            signature = mergedSignature,
            fallbackRequired = newDescriptor.fallbackRequired || existingDescriptor.fallbackRequired,
        )
    }

    fun isHex64(value: String?): Boolean {
        if (value == null || value.length != 64) return false
        return value.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
    }

    fun normalizeSha256(value: String?): String? {
        val trimmed = value?.trim() ?: return null
        if (!isHex64(trimmed)) return null
        return trimmed.lowercase()
    }

    fun isSafeVersionComponent(version: String): Boolean {
        if (version.isEmpty() || version.startsWith(".") || version.endsWith(".")) {
            return false
        }
        return version.all { it.isLetterOrDigit() || it == '.' || it == '-' || it == '_' }
    }

    private fun jsonEscape(value: String): String {
        val sb = StringBuilder(value.length + 16)
        sb.append('"')
        for (ch in value) {
            when (ch) {
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '\b' -> sb.append("\\b")
                '\u000C' -> sb.append("\\f")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> {
                    if (ch.code < 0x20) {
                        sb.append("\\u%04x".format(ch.code))
                    } else {
                        sb.append(ch)
                    }
                }
            }
        }
        sb.append('"')
        return sb.toString()
    }

    fun writeDescriptor(descriptor: DiffArchiveDescriptor, file: File, fileSystem: BundleFileSystem): Boolean {
        return try {
            val parent = file.absoluteFile.parentFile?.canonicalFile ?: return false
            val target = File(parent, file.name)
            parent.mkdirs()
            val json = buildString {
                append('{')
                append("\"appVersion\":").append(jsonEscape(descriptor.appVersion)).append(',')
                append("\"bundleVersion\":").append(jsonEscape(descriptor.bundleVersion)).append(',')
                append("\"downloadUrl\":").append(jsonEscape(descriptor.downloadUrl)).append(',')
                append("\"fileSize\":").append(descriptor.fileSize.toLong()).append(',')
                append("\"sha256\":").append(jsonEscape(descriptor.sha256.lowercase())).append(',')
                append("\"diffSha256\":").append(jsonEscape(descriptor.diffSha256.lowercase())).append(',')
                append("\"fallbackRequired\":").append(if (descriptor.fallbackRequired) "true" else "false")
                if (!descriptor.signature.isNullOrEmpty()) {
                    append(",\"signature\":").append(jsonEscape(descriptor.signature))
                }
                append('}')
            }
            val tmpFile = synchronized(activeDescriptorWrites) {
                File.createTempFile(DESCRIPTOR_TEMP_PREFIX, DESCRIPTOR_TEMP_SUFFIX, parent).also {
                    activeDescriptorWrites[it] = target
                }
            }
            try {
                FileOutputStream(tmpFile).use { fos ->
                    fos.write(json.toByteArray(Charsets.UTF_8))
                    fos.fd.sync()
                }
                // A failed rename leaves the prior sidecar intact.
                fileSystem.rename(tmpFile, target)
                true
            } finally {
                synchronized(activeDescriptorWrites) {
                    try {
                        tmpFile.delete()
                    } finally {
                        activeDescriptorWrites.remove(tmpFile)
                    }
                }
            }
        } catch (_: Exception) {
            false
        }
    }

    fun pruneDiffDownload(file: File, fileSystem: BundleFileSystem, shouldKeepVersion: (String) -> Boolean) {
        val name = file.name
        val isDescriptorTemp = name.endsWith(DESCRIPTOR_TEMP_SUFFIX) &&
            (name.startsWith(DESCRIPTOR_TEMP_PREFIX) || name.endsWith(DIFF_META_EXTENSION + DESCRIPTOR_TEMP_SUFFIX))
        if (!isDescriptorTemp) {
            var stem = name.replace(diffSegmentSuffix, "")
            for (suffix in diffStageSuffixes) stem = stem.removeSuffix(suffix)
            if (shouldKeepVersion(stem)) return
        }

        if (isDescriptorTemp || name.endsWith(DIFF_META_EXTENSION)) {
            val entry = File(file.absoluteFile.parentFile?.canonicalFile ?: return, name)
            synchronized(activeDescriptorWrites) {
                if (entry !in activeDescriptorWrites && entry !in activeDescriptorWrites.values) file.delete()
            }
        } else {
            deleteEntryWithoutFollowingLinks(file, fileSystem)
        }
    }

    fun readDescriptor(
        file: File,
        expectedAppVersion: String,
        expectedBundleVersion: String,
    ): DiffArchiveDescriptor? {
        if (!file.exists() || !file.isFile || file.length() > 256L * 1024) return null
        val root = try {
            StrictJsonParser(file.readText(Charsets.UTF_8)).parseRootObject()
        } catch (_: Exception) {
            return null
        }
        val appVersion = root["appVersion"] as? String ?: return null
        val bundleVersion = root["bundleVersion"] as? String ?: return null
        if (appVersion != expectedAppVersion || bundleVersion != expectedBundleVersion) return null
        val downloadUrl = root["downloadUrl"] as? String ?: return null
        if (!downloadUrl.startsWith("https://")) return null
        val sha256 = normalizeSha256(root["sha256"] as? String) ?: return null
        val diffSha256 = normalizeSha256(root["diffSha256"] as? String) ?: return null
        val fileSize = (root["fileSize"] as? Number)?.toDouble() ?: 0.0
        val signature = (root["signature"] as? String)?.takeIf { it.isNotEmpty() }
        val fallbackRequired = (root["fallbackRequired"] as? Boolean) ?: false
        return DiffArchiveDescriptor(
            appVersion = appVersion,
            bundleVersion = bundleVersion,
            downloadUrl = downloadUrl,
            fileSize = fileSize,
            sha256 = sha256,
            diffSha256 = diffSha256,
            signature = signature,
            fallbackRequired = fallbackRequired,
        )
    }

    fun resolveExpectedDiffSha256ForPackageVerify(
        suppliedFullSha256: String,
        suppliedDiffSha256: String?,
        descriptor: DiffArchiveDescriptor?,
    ): String? {
        val normalizedFull = normalizeSha256(suppliedFullSha256) ?: return null
        if (descriptor == null || descriptor.fallbackRequired ||
            !constantTimeEqualHex(descriptor.sha256, normalizedFull)
        ) return null
        if (suppliedDiffSha256 != null) {
            val normalizedDiff = normalizeSha256(suppliedDiffSha256) ?: return null
            if (!constantTimeEqualHex(descriptor.diffSha256, normalizedDiff)) return null
        }
        return descriptor.diffSha256
    }

    data class VerifiedDiffMetadata(val fullSha256: String, val diffSha256: String, val metadataSha256: String) {
        fun matches(fullSha256: String, diffSha256: String?): Boolean =
            constantTimeEqualHex(this.fullSha256, fullSha256) &&
                (diffSha256 == null || normalizeSha256(diffSha256) == this.diffSha256)
    }

    fun resolveTrustedMetadataSha256ForPackageVerify(
        suppliedFullSha256: String,
        suppliedSignature: String?,
        inMemoryVerified: VerifiedDiffMetadata?,
        descriptor: DiffArchiveDescriptor?,
        persistedAscSignature: String?,
        suppliedDiffSha256: String? = null,
        verifySignatureAndExtractMetadataSha256: (String) -> String?,
    ): String? {
        val normalizedFull = normalizeSha256(suppliedFullSha256) ?: return null
        if (inMemoryVerified?.matches(normalizedFull, suppliedDiffSha256) == true) {
            normalizeSha256(inMemoryVerified.metadataSha256)?.let { return it }
        }
        if (resolveExpectedDiffSha256ForPackageVerify(normalizedFull, suppliedDiffSha256, descriptor) == null) return null
        val candidateSignatures = listOfNotNull(
            suppliedSignature?.takeIf { it.isNotEmpty() },
            descriptor?.signature?.takeIf { it.isNotEmpty() },
            persistedAscSignature?.takeIf { it.isNotEmpty() },
        )
        for (sig in candidateSignatures) {
            val extracted = normalizeSha256(verifySignatureAndExtractMetadataSha256(sig))
            if (extracted != null) return extracted
        }
        return null
    }

    fun resolveFallbackDownloadParams(
        expectedFullSha256: String,
        expectedDiffSha256: String?,
        explicitDownloadUrl: String?,
        explicitFileSize: Double?,
        inMemoryFullParams: Triple<String, Double, String>?,
        descriptor: DiffArchiveDescriptor?,
    ): Pair<String, Double>? {
        val normFull = normalizeSha256(expectedFullSha256) ?: return null
        val normDiff = if (!expectedDiffSha256.isNullOrEmpty()) {
            normalizeSha256(expectedDiffSha256) ?: return null
        } else {
            null
        }
        if (!explicitDownloadUrl.isNullOrEmpty() && explicitDownloadUrl.startsWith("https://")) {
            return explicitDownloadUrl to (explicitFileSize ?: 0.0)
        }
        if (inMemoryFullParams != null &&
            inMemoryFullParams.first.startsWith("https://") &&
            constantTimeEqualHex(inMemoryFullParams.third, normFull)
        ) {
            return inMemoryFullParams.first to inMemoryFullParams.second
        }
        if (descriptor != null &&
            descriptor.downloadUrl.startsWith("https://") &&
            constantTimeEqualHex(descriptor.sha256, normFull) &&
            (normDiff == null || constantTimeEqualHex(descriptor.diffSha256, normDiff))
        ) {
            return descriptor.downloadUrl to descriptor.fileSize
        }
        return null
    }

    fun isUsableDiffParams(
        diffDownloadUrl: String?,
        diffSha256: String?,
        diffSignature: String?,
        isFallbackMarked: Boolean,
    ): Boolean {
        if (isFallbackMarked) return false
        if (diffDownloadUrl.isNullOrEmpty() || !diffDownloadUrl.startsWith("https://")) return false
        val afterScheme = diffDownloadUrl.removePrefix("https://")
        val authority = afterScheme.substringBefore('/').substringBefore('?').substringBefore('#')
        if (authority.isEmpty()) return false
        if (!isHex64(diffSha256)) return false
        if (diffSignature.isNullOrEmpty()) return false
        return true
    }

    fun isSafeRelativeBundlePath(relPath: String): Boolean {
        if (relPath.isEmpty() || relPath.startsWith("/") || relPath.contains('\\') || relPath.contains('\u0000')) {
            return false
        }
        if (relPath == "metadata.json" || relPath == "diff-manifest.json") {
            return false
        }
        val components = relPath.split('/')
        if (components.isEmpty()) return false
        for (component in components) {
            if (component.isEmpty() || component == "." || component == "..") {
                return false
            }
        }
        return true
    }

    fun constantTimeEqualHex(a: String, b: String): Boolean {
        val aBytes = a.lowercase().toByteArray(Charsets.UTF_8)
        val bBytes = b.lowercase().toByteArray(Charsets.UTF_8)
        if (aBytes.size != bBytes.size) return false
        var diff = 0
        for (i in aBytes.indices) {
            diff = diff or (aBytes[i].toInt() xor bBytes[i].toInt())
        }
        return diff == 0
    }

    /**
     * Maps an OTA metadata.json key to ordered built-in Android `context.assets` candidate paths:
     * 1. `main.jsbundle.hbc` -> `["main.jsbundle.hbc", "index.android.bundle"]`
     * 2. `assets/<subpath>` -> `["assets/<subpath>", "<subpath>", "assets/assets/<subpath>"]`
     * 3. `web-embed/<subpath>` -> `["web-embed/<subpath>"]` (packaged from `app/src/main/assets/web-embed`)
     */
    fun builtinCandidateAssetPaths(relativePath: String): List<String> {
        if (!isSafeRelativeBundlePath(relativePath)) return emptyList()
        if (relativePath == "main.jsbundle.hbc") {
            return listOf("main.jsbundle.hbc", "index.android.bundle")
        }
        if (relativePath.startsWith("assets/")) {
            val stripped = relativePath.removePrefix("assets/")
            val nested = "assets/$relativePath"
            val result = mutableListOf(relativePath)
            if (isSafeRelativeBundlePath(stripped) && !result.contains(stripped)) {
                result.add(stripped)
            }
            if (isSafeRelativeBundlePath(nested) && !result.contains(nested)) {
                result.add(nested)
            }
            return result
        }
        return listOf(relativePath)
    }

    /**
     * Maps an OTA `assets/<folder>/<fileName>` key (e.g. `assets/drawable-mdpi/icon.png` or
     * `assets/raw/sound.mp3`) to built-in APK `res/` zip entry paths inside `applicationInfo.sourceDir`
     * when Metro assets are packaged as Android resources rather than `context.assets`.
     */
    fun builtinCandidateApkResZipEntries(relativePath: String): List<String> {
        if (!isSafeRelativeBundlePath(relativePath) || !relativePath.startsWith("assets/")) {
            return emptyList()
        }
        val stripped = relativePath.removePrefix("assets/")
        val parts = stripped.split('/')
        if (parts.size != 2) return emptyList()
        val folder = parts[0]
        val fileName = parts[1]
        if (!folder.startsWith("drawable") && folder != "raw") return emptyList()
        return listOf("res/$folder/$fileName", "res/$folder-v4/$fileName")
    }

    /**
     * Collects ordered, deduplicated APK archive paths for the running app:
     * base APK (`sourceDir`) followed by any installed split/configuration APKs (`splitSourceDirs`).
     */
    fun collectInstalledApkPaths(
        sourceDir: String?,
        splitSourceDirs: Array<String>?,
    ): List<String> {
        val result = ArrayList<String>()
        val seen = HashSet<String>()
        if (!sourceDir.isNullOrEmpty() && seen.add(sourceDir)) {
            result.add(sourceDir)
        }
        if (splitSourceDirs != null) {
            for (splitPath in splitSourceDirs) {
                if (splitPath.isNotEmpty() && seen.add(splitPath)) {
                    result.add(splitPath)
                }
            }
        }
        return result
    }

    fun copyMatchingZipEntryFromApks(
        apkPaths: List<String>,
        candidateEntryNames: List<String>,
        destinationFile: File,
        expectedHash: String,
        sha256OfFile: (String) -> String?,
    ): Boolean {
        if (apkPaths.isEmpty() || candidateEntryNames.isEmpty()) return false
        for (apkPath in apkPaths) {
            val apkFile = File(apkPath)
            if (!apkFile.isFile) continue
            try {
                java.util.zip.ZipFile(apkFile).use { zipFile ->
                    for (entryName in candidateEntryNames) {
                        val entry = zipFile.getEntry(entryName) ?: continue
                        val ok = try {
                            zipFile.getInputStream(entry).use { input ->
                                FileOutputStream(destinationFile).use { output ->
                                    val buffer = ByteArray(8192)
                                    while (true) {
                                        val count = input.read(buffer)
                                        if (count <= 0) break
                                        output.write(buffer, 0, count)
                                    }
                                }
                            }
                            val hash = sha256OfFile(destinationFile.absolutePath)
                            if (hash != null && constantTimeEqualHex(hash, expectedHash)) {
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
                }
            } catch (_: Exception) {
                if (destinationFile.exists()) destinationFile.delete()
            }
        }
        return false
    }

    fun parseAndValidateManifest(
        jsonContent: String,
        expectedAppType: String,
        expectedAppVersion: String,
        expectedBundleVersion: String,
    ): BundleDiffManifest {
        val root = try {
            StrictJsonParser(jsonContent).parseRootObject()
        } catch (e: Exception) {
            throw BundleDiffReconstructionException.FallbackRequired("diff-manifest.json is not valid JSON object")
        }

        val schemaRaw = root["schemaVersion"]
        val schemaVersion = (schemaRaw as? Number)?.let { num ->
            if (num is Long && num == EXPECTED_SCHEMA_VERSION.toLong()) EXPECTED_SCHEMA_VERSION else null
        } ?: throw BundleDiffReconstructionException.FallbackRequired("unsupported diff-manifest schemaVersion")

        val appType = root["appType"] as? String
        if (appType != expectedAppType) {
            throw BundleDiffReconstructionException.IntegrityFailed("diff-manifest appType mismatch")
        }

        val appVersion = root["appVersion"] as? String
        if (appVersion != expectedAppVersion) {
            throw BundleDiffReconstructionException.IntegrityFailed("diff-manifest appVersion mismatch")
        }

        val targetBundleVersion = root["targetBundleVersion"] as? String
        if (targetBundleVersion != expectedBundleVersion) {
            throw BundleDiffReconstructionException.IntegrityFailed("diff-manifest targetBundleVersion mismatch")
        }

        val hashAlgorithm = root["hashAlgorithm"] as? String
        if (hashAlgorithm != EXPECTED_HASH_ALGORITHM) {
            throw BundleDiffReconstructionException.FallbackRequired("unsupported diff-manifest hashAlgorithm")
        }

        val targetMetadataSha256 = root["targetMetadataSha256"] as? String
        if (!isHex64(targetMetadataSha256)) {
            throw BundleDiffReconstructionException.IntegrityFailed("invalid diff-manifest targetMetadataSha256")
        }

        val baseAppVersion = root["baseAppVersion"] as? String ?: ""
        val baseMetadataSha256 = (root["baseMetadataSha256"] as? String ?: "").lowercase()

        val changedFiles = parseHashMap(root["changedFiles"], "changedFiles")
        val reusedFiles = parseHashMap(root["reusedFiles"], "reusedFiles")

        for (relPath in changedFiles.keys) {
            if (reusedFiles.containsKey(relPath)) {
                throw BundleDiffReconstructionException.FallbackRequired(
                    "overlapping path in changedFiles and reusedFiles: $relPath",
                )
            }
        }

        val deletedFiles = mutableListOf<String>()
        if (root.containsKey("deletedFiles")) {
            val rawDeleted = root["deletedFiles"] as? List<*>
                ?: throw BundleDiffReconstructionException.FallbackRequired("invalid deletedFiles array")
            for (item in rawDeleted) {
                val path = item as? String
                    ?: throw BundleDiffReconstructionException.FallbackRequired("non-string entry in deletedFiles")
                if (!isSafeRelativeBundlePath(path)) {
                    throw BundleDiffReconstructionException.SecurityRejected("unsafe path in deletedFiles: $path")
                }
                deletedFiles.add(path)
            }
        }

        return BundleDiffManifest(
            schemaVersion = schemaVersion,
            appType = appType,
            appVersion = appVersion,
            baseAppVersion = baseAppVersion,
            baseMetadataSha256 = baseMetadataSha256,
            targetBundleVersion = targetBundleVersion,
            targetMetadataSha256 = targetMetadataSha256!!.lowercase(),
            hashAlgorithm = hashAlgorithm,
            changedFiles = changedFiles,
            reusedFiles = reusedFiles,
            deletedFiles = deletedFiles,
        )
    }

    private fun parseHashMap(rawValue: Any?, fieldName: String): Map<String, String> {
        val rawMap = rawValue as? Map<*, *>
            ?: throw BundleDiffReconstructionException.FallbackRequired("missing or invalid $fieldName")
        val result = LinkedHashMap<String, String>(rawMap.size)
        for ((k, v) in rawMap) {
            val relPath = k as? String
                ?: throw BundleDiffReconstructionException.FallbackRequired("non-string key in $fieldName")
            if (!isSafeRelativeBundlePath(relPath)) {
                throw BundleDiffReconstructionException.SecurityRejected("unsafe relative path in $fieldName: $relPath")
            }
            val hash = v as? String
            if (!isHex64(hash)) {
                throw BundleDiffReconstructionException.FallbackRequired("invalid sha256 in $fieldName for $relPath")
            }
            result[relPath] = hash!!.lowercase()
        }
        return result
    }

    private fun confinedExistingFile(baseDir: File, relativePath: String, fileSystem: BundleFileSystem): File? {
        if (!isSafeRelativeBundlePath(relativePath)) return null
        val resolvedBase = try { baseDir.canonicalPath } catch (_: Exception) { return null }
        var current = baseDir
        val components = relativePath.split('/')
        for (index in components.indices) {
            current = File(current, components[index])
            val expectedType = if (index == components.lastIndex) BundleFileType.REGULAR else BundleFileType.DIRECTORY
            if (fileSystem.lstat(current)?.type != expectedType) return null
        }
        val resolvedFile = try { current.canonicalPath } catch (_: Exception) { return null }
        if (!resolvedFile.startsWith(resolvedBase + File.separator)) return null
        return current
    }

    private fun confinedDestinationFile(baseDir: File, relativePath: String, fileSystem: BundleFileSystem): File? {
        if (!isSafeRelativeBundlePath(relativePath)) return null
        val resolvedBase = try { baseDir.canonicalPath } catch (_: Exception) { return null }
        val components = relativePath.split('/')
        var current = baseDir
        for (idx in components.indices) {
            current = File(current, components[idx])
            val type = fileSystem.lstat(current)?.type
            if (type == BundleFileType.SYMLINK) return null
            if (idx < components.lastIndex && type != null && type != BundleFileType.DIRECTORY) return null
        }
        val resolvedFile = try { current.canonicalPath } catch (_: Exception) { return null }
        if (!resolvedFile.startsWith(resolvedBase + File.separator)) return null
        return current
    }

    fun validateManifestAgainstTargetMetadata(
        manifest: BundleDiffManifest,
        targetFileEntries: Map<String, String>,
        maxEntries: Int = MAX_BUNDLE_ENTRIES,
    ) {
        val totalFiles = manifest.changedFiles.size + manifest.reusedFiles.size
        if (totalFiles > maxEntries || targetFileEntries.size > maxEntries) {
            throw BundleDiffReconstructionException.SecurityRejected(
                "bundle entry count exceeds maximum allowed ($maxEntries)",
            )
        }
        if (totalFiles != targetFileEntries.size) {
            throw BundleDiffReconstructionException.FallbackRequired(
                "diff-manifest file count ($totalFiles) != metadata.json file count (${targetFileEntries.size})",
            )
        }
        for ((relPath, expectedHash) in targetFileEntries) {
            if (!isSafeRelativeBundlePath(relPath)) {
                throw BundleDiffReconstructionException.SecurityRejected("unsafe relative path in metadata.json: $relPath")
            }
            if (!isHex64(expectedHash)) {
                throw BundleDiffReconstructionException.FallbackRequired("invalid sha256 in metadata.json for $relPath")
            }
            val manifestHash = manifest.changedFiles[relPath] ?: manifest.reusedFiles[relPath]
            if (manifestHash == null || !constantTimeEqualHex(manifestHash, expectedHash)) {
                throw BundleDiffReconstructionException.FallbackRequired("diff-manifest partition mismatch for $relPath")
            }
        }
        for (deletedPath in manifest.deletedFiles) {
            if (targetFileEntries.containsKey(deletedPath) ||
                manifest.changedFiles.containsKey(deletedPath) ||
                manifest.reusedFiles.containsKey(deletedPath)
            ) {
                throw BundleDiffReconstructionException.FallbackRequired("deletedFiles entry present in target bundle: $deletedPath")
            }
        }
    }

    fun reconstruct(
        stagingDir: File,
        manifest: BundleDiffManifest,
        candidateBundleDirs: List<File>,
        sha256OfFile: (String) -> String?,
        builtinCopy: (relativePath: String, destinationFile: File, expectedHash: String) -> Boolean,
        fileSystem: BundleFileSystem,
        maxEntries: Int = MAX_BUNDLE_ENTRIES,
        maxTotalBytes: Long = MAX_BUNDLE_UNCOMPRESSED_BYTES,
    ) {
        val totalDeclaredEntries = manifest.changedFiles.size + manifest.reusedFiles.size
        if (totalDeclaredEntries > maxEntries) {
            throw BundleDiffReconstructionException.SecurityRejected(
                "reconstructed entry count exceeds maximum allowed ($maxEntries)",
            )
        }

        var cumulativeEntries = 0
        var cumulativeBytes = 0L

        val metadataFile = File(stagingDir, "metadata.json")
        if (metadataFile.exists() && metadataFile.isFile) {
            cumulativeEntries += 1
            if (cumulativeEntries > maxEntries) {
                throw BundleDiffReconstructionException.SecurityRejected(
                    "reconstructed entry count exceeds maximum allowed ($maxEntries)",
                )
            }
            val metaSize = metadataFile.length().coerceAtLeast(0L)
            if (metaSize > maxTotalBytes - cumulativeBytes) {
                throw BundleDiffReconstructionException.SecurityRejected(
                    "reconstructed bundle size exceeds maximum allowed ($maxTotalBytes)",
                )
            }
            cumulativeBytes += metaSize
        }

        // 1. Verify every changedFile exists in stagingDir and matches expected sha256.
        for ((relPath, expectedHash) in manifest.changedFiles) {
            cumulativeEntries += 1
            if (cumulativeEntries > maxEntries) {
                throw BundleDiffReconstructionException.SecurityRejected(
                    "reconstructed entry count exceeds maximum allowed ($maxEntries)",
                )
            }
            val stagedFile = confinedExistingFile(stagingDir, relPath, fileSystem)
                ?: throw BundleDiffReconstructionException.FallbackRequired("changed file missing or unsafe: $relPath")
            val fileSize = stagedFile.length().coerceAtLeast(0L)
            if (fileSize > maxTotalBytes - cumulativeBytes) {
                throw BundleDiffReconstructionException.SecurityRejected(
                    "reconstructed bundle size exceeds maximum allowed ($maxTotalBytes)",
                )
            }
            cumulativeBytes += fileSize
            val actualHash = sha256OfFile(stagedFile.absolutePath)
            if (actualHash == null || !constantTimeEqualHex(actualHash, expectedHash)) {
                throw BundleDiffReconstructionException.FallbackRequired("changed file sha256 mismatch: $relPath")
            }
        }

        // 2. Reconstruct every reusedFile from local OTA dirs or built-in app bundle.
        for ((relPath, expectedHash) in manifest.reusedFiles) {
            cumulativeEntries += 1
            if (cumulativeEntries > maxEntries) {
                throw BundleDiffReconstructionException.SecurityRejected(
                    "reconstructed entry count exceeds maximum allowed ($maxEntries)",
                )
            }
            val destFile = confinedDestinationFile(stagingDir, relPath, fileSystem)
                ?: throw BundleDiffReconstructionException.SecurityRejected(
                    "unsafe destination path for reused file: $relPath",
                )
            if (destFile.exists()) {
                destFile.delete()
            }
            val parent = destFile.parentFile
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw BundleDiffReconstructionException.FallbackRequired("failed to create staging dir for $relPath")
            }

            var copied = false
            for (candidateDir in candidateBundleDirs) {
                val sourceFile = confinedExistingFile(candidateDir, relPath, fileSystem) ?: continue
                val sourceHash = sha256OfFile(sourceFile.absolutePath) ?: continue
                if (!constantTimeEqualHex(sourceHash, expectedHash)) continue
                val sourceSize = sourceFile.length().coerceAtLeast(0L)
                if (sourceSize > maxTotalBytes - cumulativeBytes) {
                    throw BundleDiffReconstructionException.SecurityRejected(
                        "reconstructed bundle size exceeds maximum allowed ($maxTotalBytes)",
                    )
                }
                if (copyFileAndVerify(sourceFile, destFile, expectedHash, sha256OfFile)) {
                    copied = true
                    break
                }
            }

            if (!copied) {
                if (builtinCopy(relPath, destFile, expectedHash)) {
                    val copiedHash = sha256OfFile(destFile.absolutePath)
                    if (copiedHash != null && constantTimeEqualHex(copiedHash, expectedHash)) {
                        copied = true
                    } else {
                        destFile.delete()
                    }
                } else if (destFile.exists()) {
                    destFile.delete()
                }
            }

            if (!copied) {
                throw BundleDiffReconstructionException.FallbackRequired(
                    "reused file unavailable or hash mismatch: $relPath",
                )
            }

            val copiedSize = destFile.length().coerceAtLeast(0L)
            if (copiedSize > maxTotalBytes - cumulativeBytes) {
                destFile.delete()
                throw BundleDiffReconstructionException.SecurityRejected(
                    "reconstructed bundle size exceeds maximum allowed ($maxTotalBytes)",
                )
            }
            cumulativeBytes += copiedSize
        }
    }

    private fun copyFileAndVerify(
        source: File,
        destination: File,
        expectedHash: String,
        sha256OfFile: (String) -> String?,
    ): Boolean {
        return try {
            FileInputStream(source).use { input ->
                FileOutputStream(destination).use { output ->
                    val buffer = ByteArray(8192)
                    while (true) {
                        val count = input.read(buffer)
                        if (count <= 0) break
                        output.write(buffer, 0, count)
                    }
                }
            }
            val copiedHash = sha256OfFile(destination.absolutePath)
            if (copiedHash != null && constantTimeEqualHex(copiedHash, expectedHash)) {
                true
            } else {
                destination.delete()
                false
            }
        } catch (_: Exception) {
            destination.delete()
            false
        }
    }

    private class StrictJsonParser(private val input: String) {
        private var pos = 0

        fun parseRootObject(): Map<String, Any?> {
            skipWhitespace()
            val obj = parseObject()
            skipWhitespace()
            if (pos != input.length) {
                throw IllegalArgumentException("Trailing characters after JSON object")
            }
            return obj
        }

        private fun parseValue(): Any? {
            skipWhitespace()
            if (pos >= input.length) throw IllegalArgumentException("Unexpected EOF")
            return when (input[pos]) {
                '{' -> parseObject()
                '[' -> parseArray()
                '"' -> parseString()
                't' -> parseLiteral("true", true)
                'f' -> parseLiteral("false", false)
                'n' -> parseLiteral("null", null)
                '-', in '0'..'9' -> parseNumber()
                else -> throw IllegalArgumentException("Unexpected char at $pos")
            }
        }

        private fun parseObject(): Map<String, Any?> {
            expect('{')
            skipWhitespace()
            val map = LinkedHashMap<String, Any?>()
            if (peek() == '}') {
                pos++
                return map
            }
            while (true) {
                skipWhitespace()
                val key = parseString()
                skipWhitespace()
                expect(':')
                val value = parseValue()
                map[key] = value
                skipWhitespace()
                when (peek()) {
                    ',' -> pos++
                    '}' -> {
                        pos++
                        return map
                    }
                    else -> throw IllegalArgumentException("Expected , or } at $pos")
                }
            }
        }

        private fun parseArray(): List<Any?> {
            expect('[')
            skipWhitespace()
            val list = ArrayList<Any?>()
            if (peek() == ']') {
                pos++
                return list
            }
            while (true) {
                list.add(parseValue())
                skipWhitespace()
                when (peek()) {
                    ',' -> pos++
                    ']' -> {
                        pos++
                        return list
                    }
                    else -> throw IllegalArgumentException("Expected , or ] at $pos")
                }
            }
        }

        private fun parseString(): String {
            expect('"')
            val sb = StringBuilder()
            while (pos < input.length) {
                val c = input[pos++]
                when {
                    c == '"' -> return sb.toString()
                    c == '\\' -> {
                        if (pos >= input.length) throw IllegalArgumentException("Unterminated escape")
                        val esc = input[pos++]
                        when (esc) {
                            '"' -> sb.append('"')
                            '\\' -> sb.append('\\')
                            '/' -> sb.append('/')
                            'b' -> sb.append('\b')
                            'f' -> sb.append('\u000C')
                            'n' -> sb.append('\n')
                            'r' -> sb.append('\r')
                            't' -> sb.append('\t')
                            'u' -> {
                                if (pos + 4 > input.length) throw IllegalArgumentException("Invalid unicode escape")
                                val hex = input.substring(pos, pos + 4)
                                sb.append(hex.toInt(16).toChar())
                                pos += 4
                            }
                            else -> throw IllegalArgumentException("Invalid escape: \\$esc")
                        }
                    }
                    c.code < 0x20 -> throw IllegalArgumentException("Unescaped control character in string")
                    else -> sb.append(c)
                }
            }
            throw IllegalArgumentException("Unterminated string")
        }

        private fun parseNumber(): Number {
            val start = pos
            if (input[pos] == '-') pos++
            if (pos >= input.length || input[pos] !in '0'..'9') {
                throw IllegalArgumentException("Invalid number at $start")
            }
            if (input[pos] == '0') {
                pos++
            } else {
                while (pos < input.length && input[pos] in '0'..'9') pos++
            }
            var isFloat = false
            if (pos < input.length && input[pos] == '.') {
                isFloat = true
                pos++
                if (pos >= input.length || input[pos] !in '0'..'9') {
                    throw IllegalArgumentException("Invalid fraction at $start")
                }
                while (pos < input.length && input[pos] in '0'..'9') pos++
            }
            if (pos < input.length && (input[pos] == 'e' || input[pos] == 'E')) {
                isFloat = true
                pos++
                if (pos < input.length && (input[pos] == '+' || input[pos] == '-')) pos++
                if (pos >= input.length || input[pos] !in '0'..'9') {
                    throw IllegalArgumentException("Invalid exponent at $start")
                }
                while (pos < input.length && input[pos] in '0'..'9') pos++
            }
            val raw = input.substring(start, pos)
            return if (isFloat) raw.toDouble() else raw.toLong()
        }

        private fun <T> parseLiteral(literal: String, value: T): T {
            if (!input.startsWith(literal, pos)) {
                throw IllegalArgumentException("Invalid literal at $pos")
            }
            pos += literal.length
            return value
        }

        private fun skipWhitespace() {
            while (pos < input.length && input[pos] in charArrayOf(' ', '\t', '\n', '\r')) {
                pos++
            }
        }

        private fun expect(ch: Char) {
            if (pos >= input.length || input[pos] != ch) {
                throw IllegalArgumentException("Expected $ch at $pos")
            }
            pos++
        }

        private fun peek(): Char? = if (pos < input.length) input[pos] else null
    }
}
