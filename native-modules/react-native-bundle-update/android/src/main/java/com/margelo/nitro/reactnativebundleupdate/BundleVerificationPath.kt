package com.margelo.nitro.reactnativebundleupdate

import com.margelo.nitro.reactnativebundlecrypto.BundleFileSystem
import com.margelo.nitro.reactnativebundlecrypto.BundleFileType
import java.io.File

/**
 * Path helpers shared by the extracted-file verifiers.
 *
 * Metadata keys are bundle-relative paths such as
 * `segments-background/core.chains.all.CoreChainImported.seg.hbc`. They must be
 * derived from the directory that is actually walked, because Android
 * `context.filesDir` can be a symlinked path: `File(...).canonicalPath` and
 * `File(...).absolutePath` may name the same directory with different strings
 * (for example `/data/data/...` vs `/data/user/0/...` on some ROMs). Rebuilding
 * the expected root from Context inside a verifier instead of using the walked
 * root therefore produced absolute-path keys and failed every file on those
 * devices, which surfaced as an INTEGRITY_FAILED `verifyASC` error.
 */

/**
 * True when both paths refer to the same existing directory once symlinks are
 * resolved. Missing paths and resolution errors compare as "not the same".
 */
internal fun isSameDirectory(first: File, second: File): Boolean {
    return try {
        first.exists() && second.exists() && first.canonicalPath == second.canonicalPath
    } catch (e: Exception) {
        false
    }
}

/**
 * Returns [file]'s path relative to [rootDir] using the same string form the
 * walk was built from. Returns null when [file] is not strictly inside
 * [rootDir], so callers fail closed with a real diagnostic instead of looking a
 * file up by an absolute path that can never exist in metadata.
 */
internal fun bundleRelativePath(rootDir: File, file: File): String? {
    val filePath = file.absolutePath
    val rootPath = rootDir.absolutePath
    val rootPrefix = if (rootPath.endsWith(File.separator)) rootPath else rootPath + File.separator
    if (filePath.length <= rootPrefix.length || !filePath.startsWith(rootPrefix)) {
        return null
    }
    return filePath.substring(rootPrefix.length)
}

internal fun resolveManagedBundlePath(root: File, file: File, fileSystem: BundleFileSystem): String? {
    val managedRoot = root.absoluteFile
    val entries = generateSequence(file.absoluteFile) { it.parentFile }
        .takeWhile { it != managedRoot }
        .toList()
    if (entries.lastOrNull()?.parentFile != managedRoot) return null

    // A missing entry or a non-directory cannot contain a deeper symlink.
    for (entry in listOf(managedRoot) + entries.asReversed()) {
        when (fileSystem.lstat(entry)?.type) {
            BundleFileType.SYMLINK -> return null
            BundleFileType.DIRECTORY -> continue
            else -> break
        }
    }

    val rootPrefix = root.canonicalPath.trimEnd(File.separatorChar) + File.separator
    return file.canonicalPath.takeIf { it.startsWith(rootPrefix) && it != rootPrefix }
}

/** Match every expected path to a regular file that passed its hash check. */
internal fun verifyBundleFiles(
    root: File,
    subtree: File,
    metadata: Map<String, String>,
    fileSystem: BundleFileSystem,
    verifyHash: (File, String) -> Boolean,
    logError: (String) -> Unit,
): Boolean {
    val remaining = metadata.toMutableMap()

    fun walk(directory: File): Boolean {
        val files = directory.listFiles() ?: run {
            logError("Failed to list directory: ${directory.absolutePath}")
            return false
        }
        for (file in files) {
            val relativePath = bundleRelativePath(root, file)
            val type = fileSystem.lstat(file)?.type
            if (relativePath == null || type == BundleFileType.SYMLINK) {
                logError("Unsafe bundle path: ${file.absolutePath}")
                return false
            }
            if (type == BundleFileType.DIRECTORY) {
                if (!walk(file)) return false
                continue
            }
            if (type != BundleFileType.REGULAR) {
                logError("Bundle path is not a regular file: $relativePath")
                return false
            }
            val expected = remaining[relativePath]
            if (expected == null && (relativePath == "metadata.json" || file.name == ".DS_Store")) {
                continue
            }
            if (expected == null || !verifyHash(file, expected)) {
                logError("Missing hash or SHA256 mismatch for $relativePath")
                return false
            }
            remaining.remove(relativePath)
        }
        return true
    }

    if (fileSystem.lstat(subtree)?.type != BundleFileType.DIRECTORY || !walk(subtree)) return false
    if (remaining.isNotEmpty()) {
        logError("No verified file for metadata entry: ${remaining.keys.first()}")
        return false
    }
    return true
}
