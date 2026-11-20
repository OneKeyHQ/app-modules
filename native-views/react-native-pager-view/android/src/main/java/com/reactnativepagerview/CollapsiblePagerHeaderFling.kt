package com.reactnativepagerview

// A platform ScrollView reports its release velocity through nested pre-fling
// and then flings its content without further nested callbacks. A release that
// leaves the shared header between its ends would therefore move the content
// under a partly collapsed header. The coordinator carries such a release on
// the header itself; these helpers hold its decisions.

/** 1 collapses the header, -1 expands it, 0 leaves the release to the scroller. */
internal fun collapsiblePagerHeaderFlingDirection(
  headerOffset: Int,
  headerRange: Int,
  velocityY: Float,
  contentAwayFromTop: Boolean,
): Int = when {
  headerRange <= 0 -> 0
  velocityY > 0f && headerOffset < headerRange -> 1
  // Content returns to its top before the header expands.
  velocityY < 0f && headerOffset > 0 && !contentAwayFromTop -> -1
  else -> 0
}

/**
 * Whether the scroller keeps its own fling. `reach` is the signed travel the
 * release can cover. A collapse that covers the rest of the header leaves the
 * scroller to fling its content from the first frame, so its momentum is one
 * uninterrupted run; a shorter release and every expansion stay on the header.
 */
internal fun collapsiblePagerHeaderFlingJoinsContent(
  headerOffset: Int,
  headerRange: Int,
  direction: Int,
  reach: Int,
): Boolean = direction > 0 && reach.toLong() >= headerRange.toLong() - headerOffset

internal data class CollapsiblePagerHeaderFlingStep(val offset: Int, val ended: Boolean)

/**
 * Applies one frame of fling travel to the header offset. `finished` is the
 * fling's own end; its last frame still carries the travel to apply.
 */
internal fun collapsiblePagerHeaderFlingStep(
  headerOffset: Int,
  headerRange: Int,
  travel: Int,
  finished: Boolean,
): CollapsiblePagerHeaderFlingStep {
  val range = headerRange.coerceAtLeast(0)
  val offset = (headerOffset.toLong() + travel).coerceIn(0L, range.toLong()).toInt()
  val ended = finished || (travel > 0 && offset >= range) || (travel < 0 && offset <= 0)
  return CollapsiblePagerHeaderFlingStep(offset, ended)
}
