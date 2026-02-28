package com.margelo.nitro.nativelogger

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * Auto-initialises [OneKeyLog] before Application.onCreate().
 *
 * ContentProvider.onCreate() is invoked by the system between
 * Application.attachBaseContext() and Application.onCreate(),
 * so the logger is ready for the earliest app-level code.
 */
class OneKeyLogInitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        context?.let { OneKeyLog.init(it) }
        return true
    }

    override fun query(uri: Uri, proj: Array<String>?, sel: String?, selArgs: Array<String>?, sort: String?): Cursor? = null
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, sel: String?, selArgs: Array<String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, sel: String?, selArgs: Array<String>?): Int = 0
}
