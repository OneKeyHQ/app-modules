package com.reactnativepagerview.event

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.RCTEventEmitter

class NativeHeaderPressEvent(
  viewTag: Int,
  private val position: Int,
  private val key: String,
  private val name: String,
) : Event<NativeHeaderPressEvent>(viewTag) {
  override fun getEventName() = name

  override fun canCoalesce() = false

  override fun dispatch(rctEventEmitter: RCTEventEmitter) {
    rctEventEmitter.receiveEvent(viewTag, eventName, serializeEventData())
  }

  private fun serializeEventData(): WritableMap = Arguments.createMap().apply {
    putInt("position", position)
    putString("key", key)
  }

  companion object {
    const val TAB_EVENT_NAME = "topNativeTabPress"
    const val SUB_HEADER_EVENT_NAME = "topNativeSubHeaderPress"
  }
}
