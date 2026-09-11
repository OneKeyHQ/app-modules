package com.reactnativepagerview.event

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.RCTEventEmitter

class CollapsibleStateChangedEvent(
  viewTag: Int,
  private val position: Int,
  private val headerOffset: Double,
  private val nativePageCount: Int,
  private val attachedPageCount: Int,
  private val observedScrollableCount: Int,
  private val retainedPages: String,
  private val reason: String,
) : Event<CollapsibleStateChangedEvent>(viewTag) {
  override fun getEventName() = EVENT_NAME

  override fun canCoalesce() = false

  override fun dispatch(rctEventEmitter: RCTEventEmitter) {
    rctEventEmitter.receiveEvent(viewTag, eventName, serializeEventData())
  }

  private fun serializeEventData(): WritableMap = Arguments.createMap().apply {
    putInt("position", position)
    putDouble("headerOffset", headerOffset)
    putInt("nativePageCount", nativePageCount)
    putInt("attachedPageCount", attachedPageCount)
    putInt("observedScrollableCount", observedScrollableCount)
    putString("retainedPages", retainedPages)
    putString("reason", reason)
  }

  companion object {
    const val EVENT_NAME = "topCollapsibleStateChanged"
  }
}
