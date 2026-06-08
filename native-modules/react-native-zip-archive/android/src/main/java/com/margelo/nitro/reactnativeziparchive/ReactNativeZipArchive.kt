package com.margelo.nitro.reactnativeziparchive

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Nitro HybridObject implementation of the OneKey zip-archive module.
 *
 * This is a 1:1 port of the previous TurboModule `RNZipArchiveModule.kt`. The
 * underlying archive engine is unchanged: it still uses `java.util.zip`
 * (`ZipInputStream` / `ZipOutputStream` / `ZipFile`) so the on-disk
 * extraction/compression behaviour stays byte-for-byte identical.
 */
@DoNotStrip
class ReactNativeZipArchive : HybridReactNativeZipArchiveSpec() {

  companion object {
    private const val BUFFER_SIZE = 8192
  }

  override fun isPasswordProtected(file: String): Promise<Boolean> {
    return Promise.async {
      // java.util.zip does not support password-protected zips natively.
      // Return false as a safe default (matches the previous implementation).
      false
    }
  }

  override fun unzip(from: String, to: String): Promise<String> {
    return Promise.async {
      val destDir = File(to)
      if (!destDir.exists() && !destDir.mkdirs()) {
        throw Exception("Failed to create unzip destination: $to")
      }
      val canonicalDestDir = destDir.canonicalFile

      ZipInputStream(BufferedInputStream(FileInputStream(from))).use { zis ->
        var entry: ZipEntry? = zis.nextEntry
        while (entry != null) {
          val outFile = safeZipEntryFile(canonicalDestDir, entry)
          if (entry.isDirectory) {
            if (!outFile.exists() && !outFile.mkdirs()) {
              throw Exception("Failed to create unzip directory: ${outFile.absolutePath}")
            }
          } else {
            val parent = outFile.parentFile
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
              throw Exception("Failed to create unzip directory: ${parent.absolutePath}")
            }
            FileOutputStream(outFile).use { fos ->
              val buffer = ByteArray(BUFFER_SIZE)
              var len: Int
              while (zis.read(buffer).also { len = it } > 0) {
                fos.write(buffer, 0, len)
              }
            }
          }
          zis.closeEntry()
          entry = zis.nextEntry
        }
      }
      to
    }
  }

  private fun safeZipEntryFile(destDir: File, entry: ZipEntry): File {
    val outFile = File(destDir, entry.name).canonicalFile
    val destPath = destDir.path + File.separator
    if (outFile.path != destDir.path && !outFile.path.startsWith(destPath)) {
      throw Exception("Unsafe zip entry path: ${entry.name}")
    }
    return outFile
  }

  override fun unzipWithPassword(from: String, to: String, password: String): Promise<String> {
    return Promise.async {
      // java.util.zip does not support password-protected zip extraction.
      throw Exception("Password-protected zip extraction is not supported on Android")
    }
  }

  override fun zipFolder(from: String, to: String): Promise<String> {
    return Promise.async {
      val sourceDir = File(from)
      FileOutputStream(to).use { fos ->
        ZipOutputStream(BufferedOutputStream(fos)).use { zos ->
          zipDirectory(sourceDir, sourceDir.name, zos)
        }
      }
      to
    }
  }

  override fun zipFiles(files: Array<String>, to: String): Promise<String> {
    return Promise.async {
      FileOutputStream(to).use { fos ->
        ZipOutputStream(BufferedOutputStream(fos)).use { zos ->
          for (filePath in files) {
            val file = File(filePath)
            if (file.exists()) {
              addFileToZip(file, file.name, zos)
            }
          }
        }
      }
      to
    }
  }

  override fun getUncompressedSize(path: String): Promise<Double> {
    return Promise.async {
      var totalSize = 0L
      ZipFile(path).use { zipFile ->
        val entries = zipFile.entries()
        while (entries.hasMoreElements()) {
          totalSize += entries.nextElement().size
        }
      }
      totalSize.toDouble()
    }
  }

  private fun zipDirectory(dir: File, baseName: String, zos: ZipOutputStream) {
    val files = dir.listFiles() ?: return
    for (file in files) {
      val entryName = "$baseName/${file.name}"
      if (file.isDirectory) {
        zipDirectory(file, entryName, zos)
      } else {
        addFileToZip(file, entryName, zos)
      }
    }
  }

  private fun addFileToZip(file: File, entryName: String, zos: ZipOutputStream) {
    val entry = ZipEntry(entryName)
    zos.putNextEntry(entry)
    FileInputStream(file).use { fis ->
      val buffer = ByteArray(BUFFER_SIZE)
      var len: Int
      while (fis.read(buffer).also { len = it } > 0) {
        zos.write(buffer, 0, len)
      }
    }
    zos.closeEntry()
  }
}
