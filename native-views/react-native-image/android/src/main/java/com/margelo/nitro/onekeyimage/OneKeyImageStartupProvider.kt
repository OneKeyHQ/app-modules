package com.margelo.nitro.onekeyimage

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import java.util.concurrent.atomic.AtomicBoolean

internal object OneKeyImageStartupTrigger {
  private val started = AtomicBoolean(false)

  fun startIfNeeded(context: Context) {
    val appContext = context.applicationContext ?: context
    OneKeyNamedSVG.setCacheDirIfNeeded(appContext.cacheDir)
    OneKeyImageStartupPrewarm.setCacheDirIfNeeded(appContext.cacheDir)
    if (!started.compareAndSet(false, true)) return
    OneKeyNamedSVG.startPrewarmAsync(appContext)
    OneKeyImageStartupPrewarm.startPrewarmAsync(appContext)
  }
}

/**
 * Triggers asynchronous icon and image startup prewarming during process
 * initialization before `Application.onCreate` or React Native startup.
 */
class OneKeyImageStartupProvider : ContentProvider() {
  override fun onCreate(): Boolean {
    val appContext = context?.applicationContext ?: return false
    OneKeyImageStartupTrigger.startIfNeeded(appContext)
    return true
  }

  override fun query(
    uri: Uri,
    projection: Array<out String>?,
    selection: String?,
    selectionArgs: Array<out String>?,
    sortOrder: String?,
  ): Cursor? = null

  override fun getType(uri: Uri): String? = null

  override fun insert(uri: Uri, values: ContentValues?): Uri? = null

  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

  override fun update(
    uri: Uri,
    values: ContentValues?,
    selection: String?,
    selectionArgs: Array<out String>?,
  ): Int = 0
}
