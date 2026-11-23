package com.onekey.bottomtabs.events

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event

class TabDoublePressEvent(
  surfaceId: Int,
  viewTag: Int,
  private val key: String,
  private val didScrollToTop: Boolean,
) : Event<TabDoublePressEvent>(surfaceId, viewTag) {
  override fun getEventName(): String = EVENT_NAME

  override fun getEventData(): WritableMap {
    return Arguments.createMap().apply {
      putString("key", key)
      putBoolean("didScrollToTop", didScrollToTop)
    }
  }

  companion object {
    const val EVENT_NAME = "onTabDoublePress"
  }
}
