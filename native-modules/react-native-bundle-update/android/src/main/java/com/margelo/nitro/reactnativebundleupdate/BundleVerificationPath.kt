package com.margelo.nitro.reactnativebundleupdate

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
