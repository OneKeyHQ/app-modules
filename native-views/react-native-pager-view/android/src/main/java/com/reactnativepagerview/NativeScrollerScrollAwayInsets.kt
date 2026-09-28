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

// The refresh controller publishes its actual combined ancestor compensation.
internal fun nativeRefreshForegroundTop(indicatorTop: Int, appliedInset: Float): Float =
  indicatorTop - appliedInset

internal class NativeRefreshForegroundAlpha(initialAlpha: Float) {
  private val owners = WeakHashMap<Any, Unit>()
  private var baseline = initialAlpha
  private var lastApplied = initialAlpha

  val isEmpty: Boolean
    get() = owners.isEmpty()
  val drawingAlpha: Float
    get() = baseline

  fun update(currentAlpha: Float, owner: Any, acquire: Boolean): Float {
    if (currentAlpha != lastApplied) baseline = currentAlpha
    if (acquire) owners[owner] = Unit else owners.remove(owner)
    lastApplied = if (owners.isEmpty()) baseline else 0f
    return lastApplied
  }
}
