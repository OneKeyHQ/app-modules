package com.rncloudfs

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.facebook.react.module.annotations.ReactModule
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import java.util.Collections

@ReactModule(name = RNCloudFsModule.NAME)
class RNCloudFsModule(private val reactContext: ReactApplicationContext) :
    NativeCloudFsSpec(reactContext), LifecycleEventListener, ActivityEventListener {

    private var mDriveServiceHelper: DriveServiceHelper? = null
    private var signInPromise: Promise? = null
    private var mPendingPromise: Promise? = null
    private var mPendingOptions: ReadableMap? = null
    private var mPendingOperation: String? = null

    init {
        reactContext.addLifecycleEventListener(this)
        reactContext.addActivityEventListener(this)
    }

    companion object {
        const val NAME = "RNCloudFs"
        private const val TAG = "RNCloudFs"
        private const val REQUEST_CODE_SIGN_IN = 1
        private const val REQUEST_AUTHORIZATION = 11
        private const val COPY_TO_CLOUD = "CopyToCloud"
        private const val LIST_FILES = "ListFiles"
    }

    override fun getName(): String = NAME

    // region iOS-only stubs

    override fun isAvailable(promise: Promise) {
        promise.resolve(false)
    }

    override fun createFile(options: ReadableMap, promise: Promise) {
        promise.reject("NOT_AVAILABLE", "iCloud is not available on Android")
    }

    override fun getIcloudDocument(filename: String, promise: Promise) {
        promise.reject("NOT_AVAILABLE", "iCloud is not available on Android")
    }

    override fun syncCloud(promise: Promise) {
        promise.reject("NOT_AVAILABLE", "iCloud is not available on Android")
    }

    // endregion

    // region Google Sign-In

    override fun loginIfNeeded(promise: Promise) {
        if (mDriveServiceHelper == null) {
            val account = GoogleSignIn.getLastSignedInAccount(reactContext)
            if (account == null) {
                signInPromise = promise
                requestSignIn()
            } else {
                val credential = GoogleAccountCredential.usingOAuth2(
                    reactContext, Collections.singleton(DriveScopes.DRIVE_APPDATA)
                )
                credential.selectedAccount = account.account
                val googleDriveService = Drive.Builder(
                    NetHttpTransport(),
                    GsonFactory(),
                    credential
                ).build()
                mDriveServiceHelper = DriveServiceHelper(googleDriveService)
                promise.resolve(true)
            }
        } else {
            promise.resolve(true)
        }
    }

    override fun logout(promise: Promise) {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        val client: GoogleSignInClient = GoogleSignIn.getClient(reactContext, signInOptions)
        mDriveServiceHelper = null
        client.signOut()
            .addOnSuccessListener { promise.resolve(true) }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Couldn't log out.", exception)
                promise.reject(exception)
            }
    }

    override fun getCurrentlySignedInUserData(promise: Promise) {
        val account = GoogleSignIn.getLastSignedInAccount(reactContext)
        if (account == null) {
            promise.resolve(null)
        } else {
            val photoUrl: Uri? = account.photoUrl
            val resultData = WritableNativeMap()
            resultData.putString("email", account.email)
            resultData.putString("name", account.displayName)
            resultData.putString("avatarUrl", photoUrl?.toString())
            promise.resolve(resultData)
        }
    }

    private fun requestSignIn() {
        Log.d(TAG, "Requesting sign-in")
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        val client: GoogleSignInClient = GoogleSignIn.getClient(reactContext, signInOptions)
        reactContext.startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN, null)
    }

    // endregion

    // region Google Drive operations

    override fun fileExists(options: ReadableMap, promise: Promise) {
        val helper = mDriveServiceHelper
        if (helper != null) {
            val fileId = options.getString("fileId") ?: ""
            Log.d(TAG, "Checking file $fileId")
            helper.checkIfFileExists(fileId)
                .addOnSuccessListener { exists -> promise.resolve(exists) }
                .addOnFailureListener { exception ->
                    try {
                        val e = exception as UserRecoverableAuthIOException
                        reactContext.startActivityForResult(e.intent, REQUEST_AUTHORIZATION, null)
                    } catch (e: Exception) {
                        Log.e(TAG, "Couldn't check file.", exception)
                        promise.reject(exception)
                    }
                }
        } else {
            promise.reject("NOT_LOGGED_IN", "Google Drive not initialized. Call loginIfNeeded first.")
        }
    }

    override fun deleteFromCloud(item: ReadableMap, promise: Promise) {
        val helper = mDriveServiceHelper
        if (helper != null) {
            val fileId = item.getString("id") ?: ""
            Log.d(TAG, "Deleting file $fileId")
            helper.deleteFile(fileId)
                .addOnSuccessListener { deleted -> promise.resolve(deleted) }
                .addOnFailureListener { exception ->
                    try {
                        val e = exception as UserRecoverableAuthIOException
                        reactContext.startActivityForResult(e.intent, REQUEST_AUTHORIZATION, null)
                    } catch (e: Exception) {
                        Log.e(TAG, "Couldn't delete file.", exception)
                        promise.reject(exception)
                    }
                }
        } else {
            promise.reject("NOT_LOGGED_IN", "Google Drive not initialized. Call loginIfNeeded first.")
        }
    }

    override fun listFiles(options: ReadableMap, promise: Promise) {
        val helper = mDriveServiceHelper
        if (helper != null) {
            Log.d(TAG, "Querying for files.")
            val useDocumentsFolder = if (options.hasKey("scope")) {
                options.getString("scope")?.lowercase() == "visible"
            } else {
                true
            }
            try {
                helper.queryFiles(useDocumentsFolder)
                    .addOnSuccessListener { fileList ->
                        val files = WritableNativeArray()
                        for (file in fileList.files) {
                            val fileInfo = WritableNativeMap()
                            fileInfo.putString("name", file.name)
                            fileInfo.putString("id", file.id)
                            fileInfo.putString("lastModified", file.modifiedTime.toString())
                            files.pushMap(fileInfo)
                        }
                        val result = WritableNativeMap()
                        result.putArray("files", files)
                        promise.resolve(result)
                        clearPendingOperations()
                    }
                    .addOnFailureListener { exception ->
                        clearPendingOperations()
                        try {
                            Log.e(TAG, "Unable to query files: ${exception.cause?.message}")
                            val e = exception as UserRecoverableAuthIOException
                            mPendingPromise = promise
                            mPendingOptions = options
                            mPendingOperation = LIST_FILES
                            reactContext.startActivityForResult(e.intent, REQUEST_AUTHORIZATION, null)
                        } catch (e: Exception) {
                            promise.reject(e)
                        }
                    }
            } catch (exception: Exception) {
                try {
                    val e = exception as java.util.concurrent.ExecutionException
                    mPendingPromise = promise
                    mPendingOptions = options
                    mPendingOperation = LIST_FILES
                    val intent = (e.cause as UserRecoverableAuthIOException).intent
                    reactContext.startActivityForResult(intent, REQUEST_AUTHORIZATION, null)
                } catch (e: Exception) {
                    promise.reject(exception)
                    Log.e(TAG, "Unable to query files: ${exception.cause?.message}")
                }
            }
        } else {
            promise.reject("NOT_LOGGED_IN", "Google Drive not initialized. Call loginIfNeeded first.")
        }
    }

    override fun copyToCloud(options: ReadableMap, promise: Promise) {
        val helper = mDriveServiceHelper
        if (helper != null) {
            if (!options.hasKey("sourcePath")) {
                promise.reject("error", "sourcePath not specified")
                return
            }
            val source = options.getMap("sourcePath")
            var uriOrPath = source?.getString("uri")
            if (uriOrPath == null) {
                uriOrPath = source?.getString("path")
            }
            if (uriOrPath == null) {
                promise.reject("no path", "no source uri or path was specified")
                return
            }
            if (!options.hasKey("targetPath")) {
                promise.reject("error", "targetPath not specified")
                return
            }
            val destinationPath = options.getString("targetPath") ?: ""
            val mimeType = if (options.hasKey("mimetype")) options.getString("mimetype") else null
            val useDocumentsFolder = if (options.hasKey("scope")) {
                options.getString("scope")?.lowercase() == "visible"
            } else {
                true
            }
            val actualMimeType = mimeType ?: guessMimeType(uriOrPath)

            try {
                helper.saveFile(uriOrPath, destinationPath, actualMimeType, useDocumentsFolder)
                    .addOnSuccessListener { fileId ->
                        Log.d(TAG, "Saving $fileId")
                        promise.resolve(fileId)
                        clearPendingOperations()
                    }
                    .addOnFailureListener { exception ->
                        clearPendingOperations()
                        try {
                            val e = exception as UserRecoverableAuthIOException
                            reactContext.startActivityForResult(e.intent, REQUEST_AUTHORIZATION, null)
                        } catch (e: Exception) {
                            Log.e(TAG, "Couldn't create file.", exception)
                        }
                        promise.reject(exception)
                    }
            } catch (exception: Exception) {
                try {
                    val e = exception as java.util.concurrent.ExecutionException
                    mPendingPromise = promise
                    mPendingOptions = options
                    mPendingOperation = COPY_TO_CLOUD
                    val intent = (e.cause as UserRecoverableAuthIOException).intent
                    reactContext.startActivityForResult(intent, REQUEST_AUTHORIZATION, null)
                } catch (e: Exception) {
                    promise.reject(exception)
                    Log.e(TAG, "Couldn't create file.", exception)
                }
            }
        } else {
            promise.reject("NOT_LOGGED_IN", "Google Drive not initialized. Call loginIfNeeded first.")
        }
    }

    override fun getGoogleDriveDocument(fileId: String, promise: Promise) {
        val helper = mDriveServiceHelper
        if (helper != null) {
            Log.d(TAG, "Reading file $fileId")
            helper.readFile(fileId)
                .addOnSuccessListener { content -> promise.resolve(content) }
                .addOnFailureListener { exception ->
                    try {
                        val e = exception as UserRecoverableAuthIOException
                        reactContext.startActivityForResult(e.intent, REQUEST_AUTHORIZATION, null)
                    } catch (e: Exception) {
                        Log.e(TAG, "Couldn't read file.", exception)
                        promise.reject(exception)
                    }
                }
        } else {
            promise.reject("NOT_LOGGED_IN", "Google Drive not initialized. Call loginIfNeeded first.")
        }
    }

    // endregion

    // region Activity result handling

    private fun clearPendingOperations() {
        mPendingOperation = null
        mPendingPromise = null
        mPendingOptions = null
    }

    override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
            REQUEST_AUTHORIZATION -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val copiedPendingOperation = mPendingOperation
                    if (copiedPendingOperation != null) {
                        reactContext.runOnNativeModulesQueueThread {
                            mPendingOperation = null
                            when (copiedPendingOperation) {
                                COPY_TO_CLOUD -> {
                                    try {
                                        mPendingOptions?.let { copyToCloud(it, mPendingPromise!!) }
                                    } catch (e: Exception) {
                                        mPendingPromise?.reject(e)
                                        clearPendingOperations()
                                    }
                                }
                                LIST_FILES -> {
                                    try {
                                        mPendingOptions?.let { listFiles(it, mPendingPromise!!) }
                                    } catch (e: Exception) {
                                        mPendingPromise?.reject(e)
                                        clearPendingOperations()
                                    }
                                }
                            }
                        }
                    }
                } else if (resultCode == Activity.RESULT_CANCELED && mPendingPromise != null) {
                    mPendingPromise?.reject("canceled", "User canceled")
                } else if (mPendingPromise != null) {
                    mPendingPromise?.reject(
                        "unknown error",
                        "Operation failed: $mPendingOperation result code $resultCode"
                    )
                }
            }
        }
    }

    private fun handleSignInResult(completedTask: com.google.android.gms.tasks.Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount>) {
        try {
            val googleAccount = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, "Signed in as ${googleAccount.email}")

            val credential = GoogleAccountCredential.usingOAuth2(
                reactContext, Collections.singleton(DriveScopes.DRIVE_APPDATA)
            )
            credential.selectedAccount = googleAccount.account
            val googleDriveService = Drive.Builder(
                NetHttpTransport(),
                GsonFactory(),
                credential
            ).build()

            mDriveServiceHelper = DriveServiceHelper(googleDriveService)

            signInPromise?.resolve(true)
            signInPromise = null
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=${e.statusCode}")
            signInPromise?.reject("signInResult:${e.statusCode}", e.message)
            signInPromise = null
        }
    }

    // endregion

    // region Lifecycle

    override fun onHostResume() {}
    override fun onHostPause() {}
    override fun onHostDestroy() {}
    override fun onNewIntent(intent: Intent) {}

    // endregion

    private fun guessMimeType(url: String): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        return if (extension != null) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        } else {
            null
        }
    }
}
