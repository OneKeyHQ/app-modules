package com.margelo.nitro.reactnativebundlecrypto

import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.NoSuchFileException
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes

internal object JvmBundleFileSystem : BundleFileSystem {
  override fun lstat(file: File): BundleFileStat? {
    val stat = try {
      Files.readAttributes(file.toPath(), BasicFileAttributes::class.java, LinkOption.NOFOLLOW_LINKS)
    } catch (_: NoSuchFileException) {
      return null
    }
    val type = when {
      stat.isRegularFile -> BundleFileType.REGULAR
      stat.isDirectory -> BundleFileType.DIRECTORY
      stat.isSymbolicLink -> BundleFileType.SYMLINK
      else -> BundleFileType.OTHER
    }
    return BundleFileStat(type, stat.size())
  }

  override fun rename(source: File, destination: File) {
    Files.move(source.toPath(), destination.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
  }
}
