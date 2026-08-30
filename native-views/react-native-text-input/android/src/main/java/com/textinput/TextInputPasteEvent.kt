package com.textinput

import androidx.annotation.Nullable
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableArray
import com.facebook.react.uimanager.common.ViewUtil
import com.facebook.react.uimanager.events.Event

/**
 * Event emitted by EditText native view when clipboard content is pasted
 */
class TextInputPasteEvent : Event<TextInputPasteEvent> {

    companion object {
        private const val EVENT_NAME = "topPaste"
    }

    private val mType: String
    private val mData: String

    @Deprecated("Use the constructor with surfaceId")
    constructor(viewId: Int, type: String, data: String) : this(ViewUtil.NO_SURFACE_ID, viewId, type, data)

    constructor(surfaceId: Int, viewId: Int, type: String, data: String) : super(surfaceId, viewId) {
        mType = type
        mData = data
    }

    override fun getEventName(): String = EVENT_NAME

    override fun canCoalesce(): Boolean = false

    @Nullable
    override fun getEventData(): WritableMap? {
        val eventData = Arguments.createMap()

        val items = Arguments.createArray()
        val item = Arguments.createMap()
        item.putString("type", mType)
        item.putString("data", mData)
        items.pushMap(item)

        eventData.putArray("items", items)

        return eventData
    }
}
