package com.margelo.nitro.reactnativebundlecrypto

import java.io.File
import java.io.IOException

/** Verify the walked tree using bundle-relative manifest keys. */
fun verifyBundleDirectory(
    directory: File,
    metadata: Map<String, String>,
    fileSystem: BundleFileSystem,
    verifyHash: (File, String) -> Boolean,
    logError: (String) -> Unit,
    bundleRelativePrefix: String = "",
): Boolean {
    val rootType = try { fileSystem.lstat(directory)?.type } catch (_: IOException) { return false }
    if (rootType != BundleFileType.DIRECTORY) return false

    val remaining = metadata.toMutableMap()
    val verified = walkRegularBundleFiles(directory, bundleRelativePrefix, fileSystem) { file, relativePath ->
        val expected = remaining[relativePath]
        if (expected == null && isUnlistedBundleControlFile(file, relativePath)) {
            return@walkRegularBundleFiles true
        }
        if (expected == null || !verifyHash(file, expected)) {
            logError("Missing hash or SHA256 mismatch for $relativePath")
            return@walkRegularBundleFiles false
        }
        remaining.remove(relativePath)
        true
    }
    if (!verified) {
        logError("Unable to verify regular files under ${directory.absolutePath}")
        return false
    }
    if (remaining.isNotEmpty()) {
        logError("No verified file for metadata entry: ${remaining.keys.first()}")
        return false
    }
    return true
}

internal fun isUnlistedBundleControlFile(file: File, relativePath: String): Boolean =
    relativePath == "metadata.json" || file.name == ".DS_Store"

internal fun walkRegularBundleFiles(
    directory: File,
    prefix: String,
    fileSystem: BundleFileSystem,
    visit: (File, String) -> Boolean,
): Boolean {
    val files = directory.listFiles() ?: return false
    for (file in files) {
        val relativePath = prefix + file.name
        val type = try { fileSystem.lstat(file)?.type } catch (_: IOException) { return false }
        when (type) {
            BundleFileType.DIRECTORY -> if (!walkRegularBundleFiles(file, "$relativePath/", fileSystem, visit)) return false
            BundleFileType.REGULAR -> if (!visit(file, relativePath)) return false
            else -> return false
        }
    }
    return true
}
