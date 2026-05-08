package com.margelo.nitro.reactnativeperfstats

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * Auto-initialises [Overlay] before Application.onCreate().
 *
 * ContentProvider.onCreate() runs between Application.attachBaseContext()
 * and Application.onCreate(), so registering ActivityLifecycleCallbacks
 * here guarantees we capture the launcher Activity's first onResumed
 * event. Without this hook, JS code calling showOverlay() after the React
 * tree mounts would arrive too late and `currentActivity` would stay null.
 */
class PerfStatsInitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        val app = context?.applicationContext as? Application ?: return true
        Overlay.bootstrap(app)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?,
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?,
    ): Int = 0
}
