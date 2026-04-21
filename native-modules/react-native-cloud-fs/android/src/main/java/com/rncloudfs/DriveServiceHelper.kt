package com.rncloudfs

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.FileList
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.Executors

class DriveServiceHelper(private val driveService: Drive) {

    private val executor = Executors.newSingleThreadExecutor()

    fun saveFile(
        sourcePath: String,
        destinationPath: String,
        mimeType: String?,
        useDocumentsFolder: Boolean
    ): Task<String> {
        var existingFileId: String? = null
        val fileList = Tasks.await(queryFiles(useDocumentsFolder))
        for (file in fileList.files) {
            if (file.name.equals(destinationPath, ignoreCase = true)) {
                existingFileId = file.id
            }
        }
        return createFile(sourcePath, destinationPath, mimeType, useDocumentsFolder, existingFileId)
    }

    fun createFile(
        sourcePath: String,
        destinationPath: String,
        mimeType: String?,
        useDocumentsFolder: Boolean,
        fileId: String?
    ): Task<String> {
        return Tasks.call(executor) {
            try {
                val sourceFile = java.io.File(sourcePath)
                val mediaContent = FileContent(mimeType, sourceFile)
                val parentFolder = listOf(if (useDocumentsFolder) "root" else "appDataFolder")
                val metadata = com.google.api.services.drive.model.File()
                    .setMimeType(mimeType)
                    .setName(destinationPath)
                if (fileId == null) {
                    metadata.parents = parentFolder
                }

                val googleFile = if (fileId != null) {
                    driveService.files().update(fileId, metadata, mediaContent).execute()
                } else {
                    driveService.files().create(metadata, mediaContent).execute()
                }

                googleFile?.id ?: throw java.io.IOException("Null result when requesting file creation.")
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                throw e
            }
        }
    }

    fun checkIfFileExists(fileId: String): Task<Boolean> {
        return Tasks.call(executor) {
            val metadata = driveService.files().get(fileId).execute()
            metadata != null
        }
    }

    fun deleteFile(fileId: String): Task<Boolean> {
        return Tasks.call(executor) {
            driveService.files().delete(fileId).execute()
            true
        }
    }

    fun readFile(fileId: String): Task<String> {
        return Tasks.call(executor) {
            driveService.files().get(fileId).executeMediaAsInputStream().use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    val sb = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        sb.append(line)
                    }
                    sb.toString()
                }
            }
        }
    }

    fun queryFiles(useDocumentsFolder: Boolean): Task<FileList> {
        return Tasks.call(executor) {
            driveService.files().list()
                .setSpaces(if (useDocumentsFolder) "drive" else "appDataFolder")
                .setFields("nextPageToken, files(id, name, modifiedTime)")
                .setPageSize(100)
                .execute()
        }
    }

    companion object {
        private const val TAG = "DriveServiceHelper"
    }
}
