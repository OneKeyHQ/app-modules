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

  val isConfigured: Boolean get() = start != null && end != null

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

/**
 * Content extent above [first]: whole grid lines (tallest item per line) that end before the
 * line containing [first]. Span sizes follow GridLayoutManager's default line breaking.
 */
internal fun nativeListScrollPrefixPx(
  first: Int,
  spanCount: Int,
  spanSize: (Int) -> Int,
  extent: (Int) -> Int,
): Long {
  val columns = spanCount.coerceAtLeast(1)
  var prefix = 0L
  var lineExtent = 0
  var lineSpans = 0
  for (position in 0 until first) {
    val span = spanSize(position).coerceIn(1, columns)
    if (lineSpans + span > columns) {
      prefix += lineExtent
      lineExtent = 0
      lineSpans = 0
    }
    lineSpans += span
    lineExtent = maxOf(lineExtent, extent(position))
  }
  if (first > 0 && lineSpans + spanSize(first).coerceIn(1, columns) > columns) prefix += lineExtent
  return prefix
}
