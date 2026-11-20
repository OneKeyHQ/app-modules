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

internal enum class CollapsiblePagerHeaderFlingOutcome {
  CONTINUE,
  // The header is fully collapsed; what is left of the fling belongs to the content.
  HAND_OVER,
  END,
}

internal data class CollapsiblePagerHeaderFlingStep(
  val offset: Int,
  val outcome: CollapsiblePagerHeaderFlingOutcome,
)

/** Applies one frame of fling travel to the header offset. */
internal fun collapsiblePagerHeaderFlingStep(
  headerOffset: Int,
  headerRange: Int,
  travel: Int,
  running: Boolean,
): CollapsiblePagerHeaderFlingStep {
  val range = headerRange.coerceAtLeast(0)
  val offset = (headerOffset.toLong() + travel).coerceIn(0L, range.toLong()).toInt()
  val outcome = when {
    travel > 0 && offset >= range -> CollapsiblePagerHeaderFlingOutcome.HAND_OVER
    travel < 0 && offset <= 0 -> CollapsiblePagerHeaderFlingOutcome.END
    !running -> CollapsiblePagerHeaderFlingOutcome.END
    else -> CollapsiblePagerHeaderFlingOutcome.CONTINUE
  }
  return CollapsiblePagerHeaderFlingStep(offset, outcome)
}
