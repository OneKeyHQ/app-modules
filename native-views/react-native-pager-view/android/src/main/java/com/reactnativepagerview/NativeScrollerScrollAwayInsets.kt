package com.reactnativepagerview

import java.util.WeakHashMap

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

internal class NativeScrollerRefreshTranslation(initialTranslation: Float) {
  private val contributions = WeakHashMap<Any, Float>()
  private var baseline = initialTranslation
  private var lastApplied = initialTranslation

  val isEmpty: Boolean
    get() = contributions.isEmpty()

  fun update(currentTranslation: Float, owner: Any, inset: Float?): Float {
    // Native refresh layout owns top/scale, while an external translation is
    // an absolute baseline rather than another copy of our previous inset.
    if (currentTranslation != lastApplied) baseline = currentTranslation
    if (inset == null) contributions.remove(owner) else contributions[owner] = inset
    lastApplied = baseline + contributions.values.sum()
    return lastApplied
  }
}
