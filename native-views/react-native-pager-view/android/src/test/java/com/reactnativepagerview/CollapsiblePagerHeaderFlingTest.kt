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

  // The scroller keeps its own fling only when the collapse covers the rest of the header.
  check(collapsiblePagerHeaderFlingJoinsContent(100, range, 1, 26))
  check(collapsiblePagerHeaderFlingJoinsContent(100, range, 1, 900))
  check(!collapsiblePagerHeaderFlingJoinsContent(100, range, 1, 25))
  check(!collapsiblePagerHeaderFlingJoinsContent(0, range, 1, 125))
  // An expansion never moves the content, however far it could travel.
  check(!collapsiblePagerHeaderFlingJoinsContent(40, range, -1, -900))
  check(!collapsiblePagerHeaderFlingJoinsContent(40, range, 0, 900))
  // The remaining range cannot overflow.
  check(!collapsiblePagerHeaderFlingJoinsContent(Int.MIN_VALUE, Int.MAX_VALUE, 1, Int.MAX_VALUE))

  // A collapse runs frame by frame and ends at the full range.
  var step = collapsiblePagerHeaderFlingStep(100, range, 0, finished = false)
  check(step == CollapsiblePagerHeaderFlingStep(100, ended = false))
  step = collapsiblePagerHeaderFlingStep(100, range, 20, finished = false)
  check(step == CollapsiblePagerHeaderFlingStep(120, ended = false))
  step = collapsiblePagerHeaderFlingStep(step.offset, range, 19, finished = false)
  check(step == CollapsiblePagerHeaderFlingStep(range, ended = true))

  // A fling that ran out while frames were missed arrives as one finished frame:
  // its travel is applied and nothing follows.
  step = collapsiblePagerHeaderFlingStep(100, range, 5000, finished = true)
  check(step == CollapsiblePagerHeaderFlingStep(range, ended = true))
  step = collapsiblePagerHeaderFlingStep(60, range, 9, finished = true)
  check(step == CollapsiblePagerHeaderFlingStep(69, ended = true))

  // A weak release stops between the ends, as a nested fling would.
  step = collapsiblePagerHeaderFlingStep(60, range, 4, finished = false)
  check(step == CollapsiblePagerHeaderFlingStep(64, ended = false))
  step = collapsiblePagerHeaderFlingStep(64, range, 1, finished = true)
  check(step == CollapsiblePagerHeaderFlingStep(65, ended = true))

  // A downward release expands and ends at the top.
  step = collapsiblePagerHeaderFlingStep(40, range, -15, finished = false)
  check(step == CollapsiblePagerHeaderFlingStep(25, ended = false))
  step = collapsiblePagerHeaderFlingStep(25, range, -60, finished = false)
  check(step == CollapsiblePagerHeaderFlingStep(0, ended = true))

  // A header measured shorter mid-flight clamps to the new range and ends there.
  step = collapsiblePagerHeaderFlingStep(100, 90, 3, finished = false)
  check(step == CollapsiblePagerHeaderFlingStep(90, ended = true))
  // A header that disappears ends the motion at zero.
  step = collapsiblePagerHeaderFlingStep(40, 0, -3, finished = false)
  check(step == CollapsiblePagerHeaderFlingStep(0, ended = true))
  // Extreme travel cannot overflow the offset.
  step = collapsiblePagerHeaderFlingStep(100, range, Int.MAX_VALUE, finished = false)
  check(step == CollapsiblePagerHeaderFlingStep(range, ended = true))
  step = collapsiblePagerHeaderFlingStep(100, range, Int.MIN_VALUE, finished = false)
  check(step == CollapsiblePagerHeaderFlingStep(0, ended = true))

  println("Collapsible pager header fling: eligible releases, content hand-off, finished frame, weak release, expansion, range change and bounds passed")
}
