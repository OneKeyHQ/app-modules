package com.margelo.nitro.autosizeinput

internal class TextUpdateEventCounter {
  var nativeEventCount: Int = 0
    private set

  var mostRecentEventCount: Int = 0

  fun recordNativeChange() {
    nativeEventCount += 1
  }

  fun canApplyJsUpdate(): Boolean = mostRecentEventCount >= nativeEventCount
}
