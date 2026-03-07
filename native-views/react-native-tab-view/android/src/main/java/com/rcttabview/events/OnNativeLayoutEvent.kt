package com.rcttabview.events

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event

class OnNativeLayoutEvent(surfaceId: Int, viewTag: Int, private val width: Double, private val height: Double) : Event<OnNativeLayoutEvent>(surfaceId, viewTag) {
  override fun getEventName(): String = EVENT_NAME

  override fun getEventData(): WritableMap {
    return Arguments.createMap().apply {
      putDouble("width", width)
      putDouble("height", height)
    }
  }

  companion object {
    const val EVENT_NAME = "onNativeLayout"
  }
}
