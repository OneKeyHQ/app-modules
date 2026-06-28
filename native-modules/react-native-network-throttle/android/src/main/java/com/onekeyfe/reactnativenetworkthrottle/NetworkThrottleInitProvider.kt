package com.onekeyfe.reactnativenetworkthrottle

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class NetworkThrottleInitProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        context?.let { NetworkThrottle.install(it) }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0
}
