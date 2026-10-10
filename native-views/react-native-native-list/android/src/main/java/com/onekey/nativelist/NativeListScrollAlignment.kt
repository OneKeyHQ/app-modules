package com.margelo.nitro.nativelist

import kotlin.math.roundToInt

internal fun nativeListAlignedStart(
  viewportStart: Int,
  viewportLength: Int,
  itemLength: Int,
  viewPosition: Double,
  viewOffset: Int,
  nearest: Boolean,
): Int {
  val alignmentSpace = viewportLength - itemLength
  val resolvedSpace = if (nearest) alignmentSpace.coerceAtLeast(0) else alignmentSpace
  return viewportStart + (viewPosition * resolvedSpace).roundToInt() + viewOffset
}

internal fun nativeListProvisionalStart(
  viewportLength: Int,
  viewPosition: Double,
  viewOffset: Int,
  nearest: Boolean,
): Int {
  val provisionalOffset = (viewPosition * viewportLength).roundToInt() + viewOffset
  // Keep an explicit target mounted until its measured size can be aligned.
  return if (nearest) provisionalOffset else {
    provisionalOffset.coerceIn(0, (viewportLength - 1).coerceAtLeast(0))
  }
}
