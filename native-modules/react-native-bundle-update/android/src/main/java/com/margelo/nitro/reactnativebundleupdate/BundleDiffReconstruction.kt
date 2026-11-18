package com.margelo.nitro.reactnativebundleupdate

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files

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
    const val MAX_BUNDLE_UNCOMPRESSED_BYTES = 512L * 1024 * 1024
    const val MAX_CONTROL_JSON_BYTES = 16L * 1024 * 1024
    const val STAGING_ROOT_DIR_NAME = ".diff-staging"
    const val DIFF_ZIP_EXTENSION = ".diff.zip"
    const val DIFF_META_EXTENSION = ".diff.zip.meta.json"

    internal data class DiffArchiveDescriptor(
        val appVersion: String,
        val bundleVersion: String,
        val downloadUrl: String,
        val fileSize: Double,
        val sha256: String,
        val diffSha256: String,
        val signature: String? = null,
    )

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
        if (!version.all { it.isLetterOrDigit() || it == '.' || it == '-' || it == '_' }) {
            return false
        }
        val lower = version.lowercase()
        if (lower.endsWith(".diff") ||
            lower.endsWith("-diff") ||
            lower.endsWith(".diff-staging") ||
            lower.endsWith("-diff-staging")
        ) {
            return false
        }
        return true
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

    fun writeDescriptor(descriptor: DiffArchiveDescriptor, file: File) {
        try {
            val parent = file.parentFile
            if (parent != null && !parent.exists()) parent.mkdirs()
            val tmpFile = File(file.parentFile, "${file.name}.tmp")
            val json = buildString {
                append('{')
                append("\"appVersion\":").append(jsonEscape(descriptor.appVersion)).append(',')
                append("\"bundleVersion\":").append(jsonEscape(descriptor.bundleVersion)).append(',')
                append("\"downloadUrl\":").append(jsonEscape(descriptor.downloadUrl)).append(',')
                append("\"fileSize\":").append(descriptor.fileSize.toLong()).append(',')
                append("\"sha256\":").append(jsonEscape(descriptor.sha256.lowercase())).append(',')
                append("\"diffSha256\":").append(jsonEscape(descriptor.diffSha256.lowercase()))
                if (!descriptor.signature.isNullOrEmpty()) {
                    append(",\"signature\":").append(jsonEscape(descriptor.signature))
                }
                append('}')
            }
            FileOutputStream(tmpFile).use { fos ->
                fos.write(json.toByteArray(Charsets.UTF_8))
                fos.fd.sync()
            }
            if (file.exists()) file.delete()
            if (!tmpFile.renameTo(file)) {
                tmpFile.delete()
            }
        } catch (_: Exception) {
            // Best-effort sidecar persistence
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
        return DiffArchiveDescriptor(
            appVersion = appVersion,
            bundleVersion = bundleVersion,
            downloadUrl = downloadUrl,
            fileSize = fileSize,
            sha256 = sha256,
            diffSha256 = diffSha256,
            signature = signature,
        )
    }

    fun resolveExpectedDiffSha256ForPackageVerify(
        suppliedFullSha256: String,
        suppliedDiffSha256: String?,
        descriptor: DiffArchiveDescriptor?,
    ): String? {
        val normalizedFull = normalizeSha256(suppliedFullSha256) ?: return null
        if (descriptor != null && !constantTimeEqualHex(descriptor.sha256, normalizedFull)) {
            return null
        }
        if (!suppliedDiffSha256.isNullOrEmpty()) {
            val normalizedDiff = normalizeSha256(suppliedDiffSha256) ?: return null
            if (descriptor != null && !constantTimeEqualHex(descriptor.diffSha256, normalizedDiff)) {
                return null
            }
            return normalizedDiff
        }
        return descriptor?.diffSha256
    }

    fun resolveTrustedMetadataSha256ForPackageVerify(
        suppliedFullSha256: String,
        suppliedSignature: String?,
        inMemoryVerified: Pair<String, String>?,
        descriptor: DiffArchiveDescriptor?,
        persistedAscSignature: String?,
        verifySignatureAndExtractMetadataSha256: (String) -> String?,
    ): String? {
        val normalizedFull = normalizeSha256(suppliedFullSha256) ?: return null
        if (inMemoryVerified != null) {
            if (!constantTimeEqualHex(inMemoryVerified.first, normalizedFull)) return null
            return normalizeSha256(inMemoryVerified.second)
        }
        if (descriptor != null && !constantTimeEqualHex(descriptor.sha256, normalizedFull)) {
            return null
        }
        if (suppliedSignature.isNullOrEmpty() && descriptor == null) {
            return null
        }
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
        expectedDiffSha256: String,
        explicitDownloadUrl: String?,
        explicitFileSize: Double?,
        inMemoryFullParams: Triple<String, Double, String>?,
        descriptor: DiffArchiveDescriptor?,
    ): Pair<String, Double>? {
        val normFull = normalizeSha256(expectedFullSha256) ?: return null
        val normDiff = normalizeSha256(expectedDiffSha256) ?: return null
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
            constantTimeEqualHex(descriptor.diffSha256, normDiff)
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
        if (relPath == "metadata.json" || relPath == "diff-manifest.json" || relPath == ".DS_Store") {
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

    fun confinedExistingFile(baseDir: File, relativePath: String): File? {
        if (!isSafeRelativeBundlePath(relativePath)) return null
        val resolvedBase = try { baseDir.canonicalPath } catch (_: Exception) { return null }
        var current = baseDir
        for (component in relativePath.split('/')) {
            current = File(current, component)
            if (Files.isSymbolicLink(current.toPath())) return null
        }
        if (!current.exists() || !current.isFile) return null
        val resolvedFile = try { current.canonicalPath } catch (_: Exception) { return null }
        if (!resolvedFile.startsWith(resolvedBase + File.separator)) return null
        return current
    }

    fun confinedDestinationFile(baseDir: File, relativePath: String): File? {
        if (!isSafeRelativeBundlePath(relativePath)) return null
        val resolvedBase = try { baseDir.canonicalPath } catch (_: Exception) { return null }
        val components = relativePath.split('/')
        var current = baseDir
        for (idx in components.indices) {
            current = File(current, components[idx])
            if (current.exists() && Files.isSymbolicLink(current.toPath())) return null
            if (idx < components.lastIndex && current.exists() && !current.isDirectory) return null
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
            val stagedFile = confinedExistingFile(stagingDir, relPath)
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
            val destFile = confinedDestinationFile(stagingDir, relPath)
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
                val sourceFile = confinedExistingFile(candidateDir, relPath) ?: continue
                val sourceSize = sourceFile.length().coerceAtLeast(0L)
                if (sourceSize > maxTotalBytes - cumulativeBytes) {
                    throw BundleDiffReconstructionException.SecurityRejected(
                        "reconstructed bundle size exceeds maximum allowed ($maxTotalBytes)",
                    )
                }
                val sourceHash = sha256OfFile(sourceFile.absolutePath) ?: continue
                if (!constantTimeEqualHex(sourceHash, expectedHash)) continue
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
