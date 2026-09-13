package com.onekey.nativesheet

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event

internal class NativeSheetDismissEvent(
  surfaceId: Int,
  viewTag: Int,
  private val reason: String,
) : Event<NativeSheetDismissEvent>(surfaceId, viewTag) {
  override fun getEventName(): String = EVENT_NAME

  override fun getEventData(): WritableMap = Arguments.createMap().apply {
    putString("reason", reason)
  }

  companion object {
    const val EVENT_NAME = "topDismiss"
  }
}

internal class NativeSheetPresentedEvent(
  surfaceId: Int,
  viewTag: Int,
  private val height: Double,
) : Event<NativeSheetPresentedEvent>(surfaceId, viewTag) {
  override fun getEventName(): String = EVENT_NAME

  override fun getEventData(): WritableMap = Arguments.createMap().apply {
    putDouble("height", height)
  }

  companion object {
    const val EVENT_NAME = "topPresented"
  }
}
