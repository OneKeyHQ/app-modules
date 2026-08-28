package com.textinput

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.text.Spanned
import androidx.annotation.Nullable
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.views.textinput.ReactEditText
import so.onekey.app.wallet.pasteinput.PasteWatcher
import android.view.Gravity

class TextInputView(context: Context) : ReactEditText(context) {

    private var mPasteWatcher: PasteWatcher? = null

    init {
        includeFontPadding = false
    }

    override fun scrollTo(x: Int, y: Int) {
        if (gravity and Gravity.VERTICAL_GRAVITY_MASK == Gravity.CENTER_VERTICAL) {
            super.scrollTo(x, 0)
        } else {
            super.scrollTo(x, y)
        }
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        if (id == android.R.id.paste || id == android.R.id.pasteAsPlainText) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val newId = android.R.id.pasteAsPlainText
                mPasteWatcher?.let { pasteWatcher ->
                    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = clipboardManager.primaryClip
                    var type: String? = null
                    var data: String? = null
                    if (clipData?.description?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true) {
                        type = ClipDescription.MIMETYPE_TEXT_PLAIN
                        data = clipData.getItemAt(0).text.toString()
                    } else {
                        val itemUri = clipData?.getItemAt(0)?.uri
                        itemUri?.let {
                            val cr = UIManagerHelper.getReactContext(this)?.contentResolver
                            type = cr?.getType(it)
                            data = it.toString()
                        }
                    }
                    if (type != null && data != null) {
                        pasteWatcher.onPaste(type!!, data!!)
                    }
                }
                return super.onTextContextMenuItem(newId)
            } else {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val previousClipData = clipboard.primaryClip
                previousClipData?.let {
                    for (i in 0 until it.itemCount) {
                        val text = it.getItemAt(i).coerceToText(context)
                        val paste = if (text is Spanned) text.toString() else text
                        paste?.let { pasteText ->
                            val clipData = ClipData.newPlainText(null, pasteText)
                            clipboard.setPrimaryClip(clipData)
                        }
                    }
                    val actionPerformed = super.onTextContextMenuItem(id)
                    clipboard.setPrimaryClip(previousClipData)
                    return actionPerformed
                }
            }
        }
        return super.onTextContextMenuItem(id)
    }

    fun setPasteWatcher(@Nullable pasteWatcher: PasteWatcher?) {
        mPasteWatcher = pasteWatcher
    }
}
