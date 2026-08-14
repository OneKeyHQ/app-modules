package com.margelo.nitro.autosizeinput

import kotlin.math.floor

internal class TextUpdateEventCounter {
  companion object {
    // Valid acknowledgements are exact, non-negative Int values; anything
    // else from the JS Double prop (NaN, infinities, fractional, negative,
    // beyond Int.MAX_VALUE) degrades to null = uncounted. Must stay in
    // lockstep with the iOS validation so the same prop value can never be
    // stale on one platform and current on the other.
    fun sanitizeAcknowledgement(value: Double?): Int? {
      if (value == null || !value.isFinite()) return null
      if (value < 0 || value > Int.MAX_VALUE.toDouble()) return null
      if (value != floor(value)) return null
      return value.toInt()
    }
  }
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
