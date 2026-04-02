package com.rnziparchive

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.module.annotations.ReactModule
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

@ReactModule(name = RNZipArchiveModule.NAME)
class RNZipArchiveModule(reactContext: ReactApplicationContext) :
    NativeRNZipArchiveSpec(reactContext) {

    companion object {
        const val NAME = "RNZipArchive"
        private const val BUFFER_SIZE = 8192
    }

    override fun getName(): String = NAME

    override fun isPasswordProtected(file: String, promise: Promise) {
        Thread {
            try {
                // java.util.zip does not support password-protected zips natively
                // Return false as a safe default
                promise.resolve(false)
            } catch (e: Exception) {
                promise.reject("ZIP_ERROR", e.message, e)
            }
        }.start()
    }

    override fun unzip(from: String, to: String, promise: Promise) {
        Thread {
            try {
                val destDir = File(to)
                if (!destDir.exists()) destDir.mkdirs()

                ZipInputStream(BufferedInputStream(FileInputStream(from))).use { zis ->
                    var entry: ZipEntry? = zis.nextEntry
                    while (entry != null) {
                        val outFile = File(destDir, entry.name)
                        if (entry.isDirectory) {
                            outFile.mkdirs()
                        } else {
                            outFile.parentFile?.mkdirs()
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
                promise.resolve(to)
            } catch (e: Exception) {
                promise.reject("ZIP_ERROR", e.message, e)
            }
        }.start()
    }

    override fun unzipWithPassword(from: String, to: String, password: String, promise: Promise) {
        Thread {
            // java.util.zip does not support password-protected zip extraction
            promise.reject("ZIP_ERROR", "Password-protected zip extraction is not supported on Android")
        }.start()
    }

    override fun zipFolder(from: String, to: String, promise: Promise) {
        Thread {
            try {
                val sourceDir = File(from)
                FileOutputStream(to).use { fos ->
                    ZipOutputStream(BufferedOutputStream(fos)).use { zos ->
                        zipDirectory(sourceDir, sourceDir.name, zos)
                    }
                }
                promise.resolve(to)
            } catch (e: Exception) {
                promise.reject("ZIP_ERROR", e.message, e)
            }
        }.start()
    }

    override fun zipFiles(files: ReadableArray, to: String, promise: Promise) {
        Thread {
            try {
                FileOutputStream(to).use { fos ->
                    ZipOutputStream(BufferedOutputStream(fos)).use { zos ->
                        for (i in 0 until files.size()) {
                            val filePath = files.getString(i)
                            val file = File(filePath)
                            if (file.exists()) {
                                addFileToZip(file, file.name, zos)
                            }
                        }
                    }
                }
                promise.resolve(to)
            } catch (e: Exception) {
                promise.reject("ZIP_ERROR", e.message, e)
            }
        }.start()
    }

    override fun getUncompressedSize(path: String, promise: Promise) {
        Thread {
            try {
                var totalSize = 0L
                ZipFile(path).use { zipFile ->
                    val entries = zipFile.entries()
                    while (entries.hasMoreElements()) {
                        totalSize += entries.nextElement().size
                    }
                }
                promise.resolve(totalSize.toDouble())
            } catch (e: Exception) {
                promise.reject("ZIP_ERROR", e.message, e)
            }
        }.start()
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
