package com.textinput

import android.R
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.bridge.NativeModule
import com.facebook.react.uimanager.DisplayMetricsHolder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowContentResolver
import so.onekey.app.wallet.pasteinput.PasteWatcher

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TextInputViewPasteTest {
  @Test
  fun textPasteReportsAndRunsNativePasteForBothMenuActions() {
    for (action in listOf(R.id.paste, R.id.pasteAsPlainText)) {
      val context = context()
      clipboard(context).setPrimaryClip(ClipData.newPlainText("text", "copied"))
      val events = mutableListOf<Pair<String, String>>()
      val view = view(context, events)

      assertTrue(view.onTextContextMenuItem(action))
      assertEquals(listOf(ClipDescription.MIMETYPE_TEXT_PLAIN to "copied"), events)
      assertEquals("copied", view.text.toString())
    }
  }

  @Test
  fun uriPasteReportsResolvedMime() {
    val context = context()
    val uri = Uri.parse("content://text-input-image/item")
    ShadowContentResolver.registerProviderInternal("text-input-image", MimeProvider("image/png"))
    clipboard(context).setPrimaryClip(uriClip(uri))
    val events = mutableListOf<Pair<String, String>>()
    val view = view(context, events)

    assertTrue(view.onTextContextMenuItem(R.id.paste))
    assertEquals(listOf("image/png" to uri.toString()), events)
  }

  @Test
  fun uriWithoutResolvedMimeDoesNotReport() {
    val context = context()
    val uri = Uri.parse("content://text-input-no-mime/item")
    ShadowContentResolver.registerProviderInternal("text-input-no-mime", MimeProvider(null))
    clipboard(context).setPrimaryClip(uriClip(uri))
    val events = mutableListOf<Pair<String, String>>()
    val view = view(context, events)

    assertTrue(view.onTextContextMenuItem(R.id.paste))
    assertTrue(events.isEmpty())
  }

  @Test
  fun removedWatcherDoesNotReportButStillRunsNativePaste() {
    val context = context()
    clipboard(context).setPrimaryClip(ClipData.newPlainText("text", "copied"))
    val events = mutableListOf<Pair<String, String>>()
    val view = view(context, events)
    view.setPasteWatcher(null)

    assertTrue(view.onTextContextMenuItem(R.id.paste))
    assertTrue(events.isEmpty())
    assertEquals("copied", view.text.toString())
  }

  private fun context(): TestReactContext {
    val application = RuntimeEnvironment.getApplication()
    DisplayMetricsHolder.initDisplayMetricsIfNotInitialized(application)
    return TestReactContext(application)
  }

  private fun clipboard(context: Context) =
    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

  private fun view(context: Context, events: MutableList<Pair<String, String>>) =
    TextInputView(context).apply {
      setPasteWatcher(object : PasteWatcher {
        override fun onPaste(type: String, data: String) {
          events += type to data
        }
      })
    }

  private fun uriClip(uri: Uri) =
    ClipData(ClipDescription("uri", arrayOf("text/uri-list")), ClipData.Item(uri))

  private class TestReactContext(base: Context) : BridgeReactContext(base) {
    override fun <T : NativeModule?> getNativeModule(nativeModuleInterface: Class<T>): T? = null
  }

  private class MimeProvider(private val mime: String?) : ContentProvider() {
    override fun onCreate() = true
    override fun getType(uri: Uri): String? = mime
    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
      selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?,
      selectionArgs: Array<out String>?): Int = 0
  }
}
