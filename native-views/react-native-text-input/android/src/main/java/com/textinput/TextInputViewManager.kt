package com.textinput

import android.text.InputType
import android.view.ViewGroup
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.EventDispatcher
import com.facebook.react.views.textinput.ReactEditText
import com.facebook.react.views.textinput.ReactTextInputManager
import so.onekey.app.wallet.pasteinput.PasteWatcher

@ReactModule(name = TextInputViewManager.REACT_CLASS)
class TextInputViewManager : ReactTextInputManager() {

    companion object {
        const val REACT_CLASS = "OneKeyTextInput"
    }

    override fun getName(): String = REACT_CLASS

    override fun createViewInstance(context: ThemedReactContext): ReactEditText {
        val editText = TextInputView(context)
        val inputType = editText.inputType
        editText.inputType = inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE.inv()
        editText.returnKeyType = "done"
        editText.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return editText
    }

    // Fix return type to match ReactTextInputManager's definition, which is Map<String, Any>
    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> {
        val baseEventTypeConstants =
            (super.getExportedCustomDirectEventTypeConstants() as? MutableMap<String, Any>)
                ?: mutableMapOf()
        baseEventTypeConstants["topPaste"] = MapBuilder.of("registrationName", "onPaste")
        return baseEventTypeConstants
    }

    @ReactProp(name = "onPaste", defaultBoolean = false)
    fun setOnPaste(view: TextInputView, onPaste: Boolean) {
        if (onPaste) {
            view.setPasteWatcher(ReactPasteWatcher(view))
        } else {
            view.setPasteWatcher(null)
        }
    }

    private class ReactPasteWatcher(editText: TextInputView) : PasteWatcher {
        private val mReactEditText: TextInputView = editText
        private val mEventDispatcher: EventDispatcher
        private val mSurfaceId: Int

        init {
            val reactContext = UIManagerHelper.getReactContext(editText)
            mEventDispatcher =
                UIManagerHelper.getEventDispatcherForReactTag(reactContext, editText.id)!!
            mSurfaceId = UIManagerHelper.getSurfaceId(reactContext)
        }

        override fun onPaste(type: String, data: String) {
            mEventDispatcher.dispatchEvent(
                TextInputPasteEvent(mSurfaceId, mReactEditText.id, type, data)
            )
        }
    }
}
