package com.reactnativepagerview

/** Standalone JVM contract regression for Fabric scroller inset ownership. */
fun main() {
  val initialTop = 24
  val firstHeader = 182 + 48
  val initialApplied = nativeScrollerScrollAwayTop(initialTop, 0, firstHeader)
  check(initialApplied == 254)
  check(nativeScrollerScrollAwayTop(initialApplied, firstHeader, firstHeader) == initialApplied)
  val measuredHeader = 310 + 48
  val resized = nativeScrollerScrollAwayTop(initialApplied, firstHeader, measuredHeader)
  check(resized == initialTop + measuredHeader)
  // A second pager's contribution survives the first pager's detach.
  val nested = nativeScrollerScrollAwayTop(resized, 0, 100)
  val outerDetached = nativeScrollerScrollAwayTop(nested, measuredHeader, 0)
  check(outerDetached == initialTop + 100)
  check(nativeScrollerScrollAwayTop(outerDetached, 100, 0) == initialTop)
  // Fabric may reset state while replacing its content root; do not apply a negative baseline.
  check(nativeScrollerScrollAwayTop(0, measuredHeader, firstHeader) == firstHeader)
  // Caller L/R/B = 16/16/40 must survive RN's setter replay after our 230px inset.
  val caller = intArrayOf(16, 0, 16, 40)
  val rnReplay = intArrayOf(0, 0, 0, 230)
  val keepCaller = nativeScrollerPaddingWasResetByScrollAway(
    230, rnReplay[0], rnReplay[1], rnReplay[2], rnReplay[3], 230, 0,
  )
  check(keepCaller)
  val restored = if (keepCaller) caller else rnReplay
  check(restored.contentEquals(intArrayOf(16, 0, 16, 40)))
  // Nonzero native bottom state is part of RN's reset signature too.
  check(nativeScrollerPaddingWasResetByScrollAway(230, 0, 0, 0, 278, 254, 24))
  // Actual caller updates, including clearing all padding, remain observable.
  check(!nativeScrollerPaddingWasResetByScrollAway(230, 24, 0, 24, 290, 230, 0))
  check(!nativeScrollerPaddingWasResetByScrollAway(230, 0, 0, 0, 0, 230, 0))
  // Initial discovery must capture caller values before this pager owns an inset.
  check(!nativeScrollerPaddingWasResetByScrollAway(0, 0, 0, 0, 230, 230, 0))
  println("NativeScroller ScrollAway ownership: initial, repeat, measured resize, nested detach, Fabric reset, RN padding replay, and caller updates passed")
}
