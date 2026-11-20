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

  // A strong upward release reaches full collapse and hands the rest to the content.
  var offset = 100
  var step = collapsiblePagerHeaderFlingStep(offset, range, 0, running = true)
  check(step == CollapsiblePagerHeaderFlingStep(100, CollapsiblePagerHeaderFlingOutcome.CONTINUE))
  step = collapsiblePagerHeaderFlingStep(offset, range, 20, running = true)
  check(step == CollapsiblePagerHeaderFlingStep(120, CollapsiblePagerHeaderFlingOutcome.CONTINUE))
  offset = step.offset
  step = collapsiblePagerHeaderFlingStep(offset, range, 19, running = true)
  check(step == CollapsiblePagerHeaderFlingStep(range, CollapsiblePagerHeaderFlingOutcome.HAND_OVER))
  // Reaching it on the fling's last frame still reports the collapse.
  step = collapsiblePagerHeaderFlingStep(120, range, 6, running = false)
  check(step == CollapsiblePagerHeaderFlingStep(range, CollapsiblePagerHeaderFlingOutcome.HAND_OVER))

  // A weak release stops between the ends, as a nested fling would.
  step = collapsiblePagerHeaderFlingStep(60, range, 4, running = true)
  check(step == CollapsiblePagerHeaderFlingStep(64, CollapsiblePagerHeaderFlingOutcome.CONTINUE))
  step = collapsiblePagerHeaderFlingStep(64, range, 1, running = false)
  check(step == CollapsiblePagerHeaderFlingStep(65, CollapsiblePagerHeaderFlingOutcome.END))

  // A downward release expands and stops at the top without touching the content.
  step = collapsiblePagerHeaderFlingStep(40, range, -15, running = true)
  check(step == CollapsiblePagerHeaderFlingStep(25, CollapsiblePagerHeaderFlingOutcome.CONTINUE))
  step = collapsiblePagerHeaderFlingStep(25, range, -60, running = true)
  check(step == CollapsiblePagerHeaderFlingStep(0, CollapsiblePagerHeaderFlingOutcome.END))

  // A header measured shorter mid-flight clamps to the new range and is collapsed.
  step = collapsiblePagerHeaderFlingStep(100, 90, 3, running = true)
  check(step == CollapsiblePagerHeaderFlingStep(90, CollapsiblePagerHeaderFlingOutcome.HAND_OVER))
  // A header that disappears ends the fling at zero.
  step = collapsiblePagerHeaderFlingStep(40, 0, -3, running = true)
  check(step == CollapsiblePagerHeaderFlingStep(0, CollapsiblePagerHeaderFlingOutcome.END))
  // Extreme travel cannot overflow the offset.
  step = collapsiblePagerHeaderFlingStep(100, range, Int.MAX_VALUE, running = true)
  check(step == CollapsiblePagerHeaderFlingStep(range, CollapsiblePagerHeaderFlingOutcome.HAND_OVER))
  step = collapsiblePagerHeaderFlingStep(100, range, Int.MIN_VALUE, running = true)
  check(step == CollapsiblePagerHeaderFlingStep(0, CollapsiblePagerHeaderFlingOutcome.END))

  println("Collapsible pager header fling: eligible releases, hand-over, weak release, expansion, range change and bounds passed")
}
