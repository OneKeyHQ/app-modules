package com.reactnativepagerview

// A platform ScrollView reports its release velocity through nested pre-fling
// and then flings its content without further nested callbacks. A release that
// leaves the shared header between its ends would therefore move the content
// under a partly collapsed header. The coordinator spends such a release on
// the header first and hands the scroller only what is left; these helpers
// hold its decisions.

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
 * How far the content may travel: the part of a collapsing release that the
 * header does not use. `reach` is the signed travel the release can cover.
 * An expansion never moves the content.
 */
internal fun collapsiblePagerHeaderFlingContentTravel(
  headerOffset: Int,
  headerRange: Int,
  direction: Int,
  reach: Int,
): Int {
  if (direction <= 0) return 0
  val remaining = (headerRange.toLong() - headerOffset).coerceAtLeast(0L)
  return (reach.toLong() - remaining).coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
}

/**
 * The fastest fling, no faster than the release, that travels no farther than
 * `travel`. `reach` gives the travel of a fling at a speed and never decreases
 * with it. The header's share and the content's share therefore add up to the
 * release itself, and a release that barely passes the end of the header moves
 * the content barely at all.
 */
internal fun collapsiblePagerHeaderFlingContentVelocity(
  velocity: Int,
  travel: Int,
  reach: (Int) -> Int,
): Int {
  if (velocity <= 0 || travel <= 0) return 0
  if (reach(velocity) <= travel) return velocity
  var within = 0
  var beyond = velocity
  while (beyond - within > 1) {
    val middle = within + (beyond - within) / 2
    if (reach(middle) <= travel) within = middle else beyond = middle
  }
  return within
}

/**
 * Whether a fling handed to the owner keeps the direction it is given. On
 * Android 9 (API 28) React Native replaces the sign of every ScrollView fling
 * with the direction of the last content scroll it recorded, and that record
 * can be neither read nor set from outside: when the header took the whole
 * drag it may point the other way, and a first scroll written to it overflows
 * to a negative one. The content's share cannot be handed over there, so the
 * release is left to the owner as it was.
 */
internal fun collapsiblePagerHeaderFlingOwnerKeepsDirection(
  sdkInt: Int,
  reactNativeOwner: Boolean,
): Boolean = !(reactNativeOwner && sdkInt == 28)

internal data class CollapsiblePagerHeaderFlingStep(val offset: Int, val ended: Boolean)

/**
 * Applies one frame of fling travel to the header offset. `finished` is the
 * fling's own end; its last frame still carries the travel to apply.
 */
internal fun collapsiblePagerHeaderFlingStep(
  headerOffset: Int,
  headerRange: Int,
  direction: Int,
  travel: Int,
  finished: Boolean,
  contentAwayFromTop: Boolean,
): CollapsiblePagerHeaderFlingStep {
  val range = headerRange.coerceAtLeast(0)
  // An expansion needs the content at its top for as long as it runs.
  if (direction < 0 && contentAwayFromTop) {
    return CollapsiblePagerHeaderFlingStep(headerOffset.coerceIn(0, range), ended = true)
  }
  // Out of travel above moved content: the header may not rest partly
  // collapsed there, whatever the range has become since the release.
  if (direction > 0 && finished && contentAwayFromTop) {
    return CollapsiblePagerHeaderFlingStep(range, ended = true)
  }
  val offset = (headerOffset.toLong() + travel).coerceIn(0L, range.toLong()).toInt()
  val atEnd = if (direction > 0) offset >= range else offset <= 0
  return CollapsiblePagerHeaderFlingStep(offset, finished || atEnd)
}

/**
 * Where a touch-down leaves the header when it ends a running motion: fully
 * collapsed above moved content, where it is otherwise.
 */
internal fun collapsiblePagerHeaderFlingOffsetAtTouch(
  headerOffset: Int,
  headerRange: Int,
  direction: Int,
  contentAwayFromTop: Boolean,
): Int {
  val range = headerRange.coerceAtLeast(0)
  return if (direction > 0 && contentAwayFromTop) range else headerOffset.coerceIn(0, range)
}

/**
 * How far a platform ScrollView can still scroll down. Its viewport is its
 * height less its padding, which `canScrollVertically` leaves out: a page a
 * little shorter than the view can still scroll by its bottom padding.
 */
internal fun collapsiblePagerScrollViewRoomBelow(
  scrollY: Int,
  contentHeight: Int,
  viewHeight: Int,
  paddingTop: Int,
  paddingBottom: Int,
): Int {
  val viewport = viewHeight.toLong() - paddingTop - paddingBottom
  val range = (contentHeight.toLong() - viewport).coerceAtLeast(0L)
  return (range - scrollY).coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
}
