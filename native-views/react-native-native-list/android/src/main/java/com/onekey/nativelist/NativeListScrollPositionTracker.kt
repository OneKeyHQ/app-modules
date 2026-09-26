package com.margelo.nitro.nativelist

/** A per-view hysteresis observer. Null means no bridge event is needed. */
internal class NativeListScrollPositionTracker {
  private var start: Double? = null
  private var end: Double? = null
  private var previous: Boolean? = null

  fun configure(start: Double?, end: Double?) {
    this.start = null
    this.end = null
    previous = null
    if (start == null || end == null || !start.isFinite() || !end.isFinite() || start < 0 || end <= start) return
    this.start = start
    this.end = end
  }

  fun resetDelivery() { previous = null }

  fun update(offset: Double): Boolean? {
    val start = start ?: return null
    val end = end ?: return null
    if (!offset.isFinite()) return null
    val next = if (previous == true) offset > start else offset >= end
    if (previous == next) return null
    previous = next
    return next
  }
}
