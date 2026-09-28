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

  val firstOwner = Any()
  val secondOwner = Any()
  val translation = NativeScrollerRefreshTranslation(7.5f)
  check(translation.isEmpty)
  var current = translation.update(7.5f, firstOwner, 230f)
  check(current == 237.5f)
  check(!translation.isEmpty)
  current = translation.update(current, firstOwner, 230f)
  check(current == 237.5f)
  current = translation.update(current, firstOwner, 358f)
  check(current == 365.5f)
  current = translation.update(current, secondOwner, 64f)
  check(current == 429.5f)
  current = translation.update(current, firstOwner, null)
  check(current == 71.5f)
  check(!translation.isEmpty)
  current = translation.update(current, secondOwner, null)
  check(current == 7.5f)
  check(translation.isEmpty)

  // Reversing release order must restore the same original translation.
  current = translation.update(current, firstOwner, 230f)
  current = translation.update(current, secondOwner, 64f)
  current = translation.update(current, secondOwner, null)
  check(current == 237.5f)
  current = translation.update(current, firstOwner, null)
  check(current == 7.5f)
  check(translation.isEmpty)

  // A caller's absolute translation update replaces the baseline, not its inset.
  current = translation.update(current, firstOwner, 230f)
  check(current == 237.5f)
  current = translation.update(-12.5f, firstOwner, 230f)
  check(current == 217.5f)
  current = translation.update(current, secondOwner, 64f)
  check(current == 281.5f)
  current = translation.update(current, firstOwner, null)
  check(current == 51.5f)
  current = translation.update(current, secondOwner, null)
  check(current == -12.5f)
  check(translation.isEmpty)

  // Zero is a real owned contribution; only null releases ownership.
  current = translation.update(current, firstOwner, 0f)
  check(current == -12.5f)
  check(!translation.isEmpty)
  current = translation.update(current, secondOwner, 0f)
  current = translation.update(current, firstOwner, null)
  check(current == -12.5f)
  check(!translation.isEmpty)
  current = translation.update(current, secondOwner, null)
  check(current == -12.5f)
  check(translation.isEmpty)
  current = translation.update(9f, firstOwner, null)
  check(current == 9f)
  check(translation.isEmpty)
  println("NativeScroller refresh translation: repeat, resize, nested release orders, external baseline, zero contribution, and final restoration passed")
}
