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

  // Records the JS acknowledgement and reports whether the cached controlled
  // text must be reapplied now: when JS sanitizes an edit back to the SAME
  // string, React re-sends only the count (unchanged props are not re-sent),
  // so catching up on a count-only update is the signal to roll the view back
  // to the last requested JS text.
  fun acknowledge(count: Int?): Boolean {
    mostRecentEventCount = count
    return count != null && canApplyJsUpdate()
  }
}
