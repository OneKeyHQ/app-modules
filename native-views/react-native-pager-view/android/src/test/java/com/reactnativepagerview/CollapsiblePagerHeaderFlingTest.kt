package com.reactnativepagerview

/** Standalone JVM contract regression for a platform ScrollView's release on the shared header. */
fun main() {
  val range = 126

  // Which releases the coordinator carries.
  check(collapsiblePagerHeaderFlingDirection(100, range, 1600f, false) == 1)
  // A release can end before the header has moved at all.
  check(collapsiblePagerHeaderFlingDirection(0, range, 1600f, false) == 1)
  // Content that already moved under a partly collapsed header is repaired the same way.
  check(collapsiblePagerHeaderFlingDirection(100, range, 1600f, true) == 1)
  check(collapsiblePagerHeaderFlingDirection(40, range, -1600f, false) == -1)
  check(collapsiblePagerHeaderFlingDirection(range, range, -1600f, false) == -1)
  // Fully collapsed and moving on: an ordinary content fling.
  check(collapsiblePagerHeaderFlingDirection(range, range, 1600f, false) == 0)
  // Content returns to its top before the header expands.
  check(collapsiblePagerHeaderFlingDirection(range, range, -1600f, true) == 0)
  check(collapsiblePagerHeaderFlingDirection(40, range, -1600f, true) == 0)
  // Nothing to expand, no velocity, no collapsible header.
  check(collapsiblePagerHeaderFlingDirection(0, range, -1600f, false) == 0)
  check(collapsiblePagerHeaderFlingDirection(40, range, 0f, false) == 0)
  check(collapsiblePagerHeaderFlingDirection(0, 0, 1600f, false) == 0)

  // The content gets only what the header leaves of a collapsing release.
  check(collapsiblePagerHeaderFlingContentTravel(100, range, 1, 25) == 0)
  check(collapsiblePagerHeaderFlingContentTravel(100, range, 1, 26) == 0)
  check(collapsiblePagerHeaderFlingContentTravel(100, range, 1, 27) == 1)
  check(collapsiblePagerHeaderFlingContentTravel(100, range, 1, 900) == 874)
  check(collapsiblePagerHeaderFlingContentTravel(0, range, 1, 125) == 0)
  // An expansion never moves the content, however far it could travel.
  check(collapsiblePagerHeaderFlingContentTravel(40, range, -1, -900) == 0)
  check(collapsiblePagerHeaderFlingContentTravel(40, range, 0, 900) == 0)
  // A header measured shorter than its offset leaves the whole release to the content.
  check(collapsiblePagerHeaderFlingContentTravel(100, 90, 1, 30) == 30)
  // Neither the remaining range nor the travel can overflow.
  check(collapsiblePagerHeaderFlingContentTravel(Int.MIN_VALUE, Int.MAX_VALUE, 1, Int.MAX_VALUE) == 0)
  check(collapsiblePagerHeaderFlingContentTravel(Int.MAX_VALUE, 0, 1, Int.MAX_VALUE) == Int.MAX_VALUE)

  // A fling travels farther the faster it starts; the shape is the platform's own.
  val reach = { speed: Int -> (speed.toLong() * speed / 4000L).toInt() }
  // The content fling is the fastest one that stays within its share.
  for (travel in listOf(1, 2, 26, 399, 400, 401, 874, 5000)) {
    val speed = collapsiblePagerHeaderFlingContentVelocity(6000, travel, reach)
    check(speed in 1..6000) { "travel=$travel speed=$speed" }
    check(reach(speed) <= travel) { "travel=$travel speed=$speed" }
    check(reach(speed + 1) > travel) { "travel=$travel speed=$speed" }
  }
  // Nothing left for the content, or no release to split.
  check(collapsiblePagerHeaderFlingContentVelocity(6000, 0, reach) == 0)
  check(collapsiblePagerHeaderFlingContentVelocity(6000, -5, reach) == 0)
  check(collapsiblePagerHeaderFlingContentVelocity(0, 400, reach) == 0)
  check(collapsiblePagerHeaderFlingContentVelocity(-6000, 400, reach) == 0)
  // A share no smaller than the release keeps the release speed and never exceeds it.
  check(collapsiblePagerHeaderFlingContentVelocity(6000, 9000, reach) == 6000)
  check(collapsiblePagerHeaderFlingContentVelocity(6000, Int.MAX_VALUE, reach) == 6000)
  // No speed fits a share smaller than the shortest fling.
  check(collapsiblePagerHeaderFlingContentVelocity(6000, 400) { 500 } == 0)
  check(collapsiblePagerHeaderFlingContentVelocity(1, 400) { 0 } == 1)

  // The header and the content together travel what the release can cover, on
  // either side of the speed that just reaches the end of the header. One step
  // of speed changes the sample fling by less than `rounding`.
  val rounding = 4
  for (offset in listOf(0, 60, 100, 125)) {
    val remaining = range - offset
    var previousTotal = 0
    for (velocity in 0..6000) {
      val released = reach(velocity)
      val share = collapsiblePagerHeaderFlingContentTravel(offset, range, 1, released)
      val content = reach(collapsiblePagerHeaderFlingContentVelocity(velocity, share, reach))
      val total = minOf(released, remaining) + content
      check(total <= released) { "offset=$offset velocity=$velocity total=$total released=$released" }
      check(released - total < rounding) { "offset=$offset velocity=$velocity total=$total released=$released" }
      // A release that ends on the header leaves the content alone.
      if (released <= remaining) check(content == 0) { "offset=$offset velocity=$velocity" }
      // A slightly faster release never moves the page by a header's worth more.
      check(total - previousTotal in 0 until 2 * rounding) {
        "offset=$offset velocity=$velocity total=$total previous=$previousTotal"
      }
      previousTotal = total
    }
  }

  // A collapse runs frame by frame and ends at the full range.
  var step = collapsiblePagerHeaderFlingStep(100, range, 1, 0, finished = false, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(100, ended = false))
  step = collapsiblePagerHeaderFlingStep(100, range, 1, 20, finished = false, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(120, ended = false))
  step = collapsiblePagerHeaderFlingStep(step.offset, range, 1, 19, finished = false, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(range, ended = true))
  // The content moving beside it changes nothing while the collapse still has travel.
  step = collapsiblePagerHeaderFlingStep(100, range, 1, 20, finished = false, contentAwayFromTop = true)
  check(step == CollapsiblePagerHeaderFlingStep(120, ended = false))

  // A fling that ran out while frames were missed arrives as one finished frame:
  // its travel is applied and nothing follows.
  step = collapsiblePagerHeaderFlingStep(100, range, 1, 5000, finished = true, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(range, ended = true))
  step = collapsiblePagerHeaderFlingStep(60, range, 1, 9, finished = true, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(69, ended = true))

  // A weak release stops between the ends, as a nested fling would.
  step = collapsiblePagerHeaderFlingStep(60, range, 1, 4, finished = false, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(64, ended = false))
  step = collapsiblePagerHeaderFlingStep(64, range, 1, 1, finished = true, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(65, ended = true))

  // A header that grew mid-flight: at 100 of 126 with 30 of travel the content
  // was flung too, and the travel runs out at 130 of the new 150. The header
  // may not rest there above moved content.
  step = collapsiblePagerHeaderFlingStep(100, 150, 1, 30, finished = false, contentAwayFromTop = true)
  check(step == CollapsiblePagerHeaderFlingStep(130, ended = false))
  step = collapsiblePagerHeaderFlingStep(step.offset, 150, 1, 0, finished = true, contentAwayFromTop = true)
  check(step == CollapsiblePagerHeaderFlingStep(150, ended = true))
  // The same on a single late frame.
  step = collapsiblePagerHeaderFlingStep(100, 150, 1, 30, finished = true, contentAwayFromTop = true)
  check(step == CollapsiblePagerHeaderFlingStep(150, ended = true))
  // Content still at its top lets the header rest where the travel ends.
  step = collapsiblePagerHeaderFlingStep(100, 150, 1, 30, finished = true, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(130, ended = true))

  // A downward release expands and ends at the top.
  step = collapsiblePagerHeaderFlingStep(40, range, -1, -15, finished = false, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(25, ended = false))
  step = collapsiblePagerHeaderFlingStep(25, range, -1, -60, finished = false, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(0, ended = true))
  // Content that leaves its top under an expansion, as a snapping ScrollView
  // does on its own after a release, stops the expansion where it is.
  step = collapsiblePagerHeaderFlingStep(25, range, -1, -10, finished = false, contentAwayFromTop = true)
  check(step == CollapsiblePagerHeaderFlingStep(25, ended = true))
  step = collapsiblePagerHeaderFlingStep(25, range, -1, -10, finished = true, contentAwayFromTop = true)
  check(step == CollapsiblePagerHeaderFlingStep(25, ended = true))

  // A header measured shorter mid-flight clamps to the new range and ends there.
  step = collapsiblePagerHeaderFlingStep(100, 90, 1, 3, finished = false, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(90, ended = true))
  // Already past the new range with no travel on this frame.
  step = collapsiblePagerHeaderFlingStep(100, 90, 1, 0, finished = false, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(90, ended = true))
  // A header that disappears ends the motion at zero.
  step = collapsiblePagerHeaderFlingStep(40, 0, -1, -3, finished = false, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(0, ended = true))
  step = collapsiblePagerHeaderFlingStep(40, 0, 1, 3, finished = false, contentAwayFromTop = true)
  check(step == CollapsiblePagerHeaderFlingStep(0, ended = true))
  // Extreme travel cannot overflow the offset.
  step = collapsiblePagerHeaderFlingStep(100, range, 1, Int.MAX_VALUE, finished = false, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(range, ended = true))
  step = collapsiblePagerHeaderFlingStep(100, range, -1, Int.MIN_VALUE, finished = false, contentAwayFromTop = false)
  check(step == CollapsiblePagerHeaderFlingStep(0, ended = true))

  // A drag that takes over a running collapse finds the header fully collapsed
  // above moved content, and where it is when the content is back at its top.
  check(collapsiblePagerHeaderFlingOffsetUnderDrag(110, range, 1, contentAwayFromTop = true) == range)
  check(collapsiblePagerHeaderFlingOffsetUnderDrag(110, range, 1, contentAwayFromTop = false) == 110)
  check(collapsiblePagerHeaderFlingOffsetUnderDrag(110, 150, 1, contentAwayFromTop = true) == 150)
  // An expansion is left where the drag finds it.
  check(collapsiblePagerHeaderFlingOffsetUnderDrag(30, range, -1, contentAwayFromTop = true) == 30)
  check(collapsiblePagerHeaderFlingOffsetUnderDrag(30, range, -1, contentAwayFromTop = false) == 30)
  check(collapsiblePagerHeaderFlingOffsetUnderDrag(130, range, 1, contentAwayFromTop = false) == range)
  check(collapsiblePagerHeaderFlingOffsetUnderDrag(40, 0, 1, contentAwayFromTop = true) == 0)

  println(
    "Collapsible pager header fling: eligible releases, content share on both sides of the header end, " +
      "finished frame, weak release, grown and shrunk range, expansion, drag take-over and bounds passed",
  )
}
