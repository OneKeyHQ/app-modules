package com.margelo.nitro.reactnativebundleupdate

import com.margelo.nitro.reactnativebundlecrypto.BundleFileSystem
import com.margelo.nitro.reactnativebundlecrypto.BundleFileType
import java.io.File

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
