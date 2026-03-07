package com.rcttabview.events

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event

class OnTabBarMeasuredEvent(surfaceId: Int, viewTag: Int, private val height: Int) : Event<OnTabBarMeasuredEvent>(surfaceId, viewTag) {
  override fun getEventName(): String = EVENT_NAME

  override fun getEventData(): WritableMap {
    return Arguments.createMap().apply {
      putInt("height", height)
    }
  }

  companion object {
    const val EVENT_NAME = "onTabBarMeasured"
  }
}
