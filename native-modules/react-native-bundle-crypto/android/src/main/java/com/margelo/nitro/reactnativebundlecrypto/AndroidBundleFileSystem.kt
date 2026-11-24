package com.margelo.nitro.reactnativebundlecrypto

import android.system.ErrnoException
import android.system.Os
import android.system.OsConstants
import java.io.File

object AndroidBundleFileSystem : BundleFileSystem {
  override fun lstat(file: File): BundleFileStat? {
    val stat = try {
      Os.lstat(file.absolutePath)
    } catch (error: ErrnoException) {
      if (error.errno == OsConstants.ENOENT) return null
      throw BundleFileSystemException(error)
    }
    val type = when {
      OsConstants.S_ISREG(stat.st_mode) -> BundleFileType.REGULAR
      OsConstants.S_ISDIR(stat.st_mode) -> BundleFileType.DIRECTORY
      OsConstants.S_ISLNK(stat.st_mode) -> BundleFileType.SYMLINK
      else -> BundleFileType.OTHER
    }
    return BundleFileStat(type, stat.st_size)
  }

  override fun rename(source: File, destination: File) {
    try {
      Os.rename(source.absolutePath, destination.absolutePath)
    } catch (error: ErrnoException) {
      throw BundleFileSystemException(error)
    }
  }
}
