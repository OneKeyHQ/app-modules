package com.reactnativepagerview

// Replace only this pager's contribution; another native owner may share the scroller.
internal fun nativeScrollerScrollAwayTop(currentTop: Int, previousInset: Int, nextInset: Int): Int =
  (currentTop - previousInset).coerceAtLeast(0) + nextInset

// ReactScrollView's ScrollAway setter always writes this exact padding shape.
internal fun nativeScrollerPaddingWasResetByScrollAway(
  appliedInset: Int,
  left: Int,
  top: Int,
  right: Int,
  bottom: Int,
  scrollAwayTop: Int,
  scrollAwayBottom: Int,
): Boolean = appliedInset > 0 && left == 0 && top == 0 && right == 0 &&
  bottom == scrollAwayTop + scrollAwayBottom
