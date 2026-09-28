package com.reactnativepagerview

/** Standalone JVM regression: compile together with the production helper. */
fun main() {
  val restore = NativeScrollerPendingRestore()

  // Reachable immediately: nothing remains pending and layout keeps the current anchor.
  restore.request(target = 640, reached = 640)
  check(restore.target == null)
  check(restore.anchor(320) == 320)

  // Content height has not reached native: NestedScrollView clamped the request to 0.
  restore.request(target = 640, reached = 0)
  check(restore.target == 640)
  check(restore.anchor(0) == 640)
  // Partial growth keeps retrying the original target, not the partially clamped offset.
  restore.settle(reached = 300)
  check(restore.target == 640)
  check(restore.anchor(300) == 640)
  // Once reached, later layouts keep the live position again.
  restore.settle(reached = 640)
  check(restore.target == null)
  check(restore.anchor(900) == 900)

  // User touch, commands and content replacement cancel a pending target.
  restore.request(target = 480, reached = 120)
  restore.cancel()
  check(restore.target == null)
  check(restore.anchor(120) == 120)
  println("Native scroller pending restore: clamp retry, partial growth, settle and cancel passed")
}
