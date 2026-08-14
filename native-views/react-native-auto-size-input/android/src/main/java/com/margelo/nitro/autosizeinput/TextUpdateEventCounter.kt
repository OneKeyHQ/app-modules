package com.margelo.nitro.autosizeinput

internal class TextUpdateEventCounter {
  var nativeEventCount: Int = 0
    private set

  // null = the caller never supplied mostRecentEventCount. Such callers do not
  // participate in stale-update filtering and their JS text updates always
  // apply (programmatic clears, sanitization, form restores). Only callers
  // that echo the event count back opt into the comparison.
  var mostRecentEventCount: Int? = null

  fun recordNativeChange() {
    nativeEventCount += 1
  }

  fun canApplyJsUpdate(): Boolean {
    val acknowledged = mostRecentEventCount ?: return true
    return acknowledged >= nativeEventCount
  }
}
