package com.margelo.nitro.reactnativebundlecrypto

import java.io.File
import java.io.IOException

enum class BundleFileType { REGULAR, DIRECTORY, SYMLINK, OTHER }

data class BundleFileStat(val type: BundleFileType, val size: Long)

class BundleFileSystemException(cause: Throwable) : IOException(cause)

interface BundleFileSystem {
  fun lstat(file: File): BundleFileStat?

  fun isSymbolicLink(file: File): Boolean = lstat(file)?.type == BundleFileType.SYMLINK

  fun rename(source: File, destination: File)
}
