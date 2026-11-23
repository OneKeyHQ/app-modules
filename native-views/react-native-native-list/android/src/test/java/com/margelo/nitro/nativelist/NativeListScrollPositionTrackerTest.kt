package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NativeListScrollPositionTrackerTest {
  @Test fun hysteresisAndDisabledLifecycle() {
    val tracker = NativeListScrollPositionTracker()
    assertNull(tracker.update(200.0))
    tracker.configure(48.0, 160.0)
    assertEquals(false, tracker.update(0.0))
    assertNull(tracker.update(159.0))
    assertEquals(true, tracker.update(160.0))
    assertNull(tracker.update(100.0))
    assertEquals(false, tracker.update(48.0))
    assertNull(tracker.update(49.0))
    tracker.configure(null, null)
    assertNull(tracker.update(200.0))
    tracker.configure(48.0, 160.0)
    assertEquals(true, tracker.update(200.0))
    tracker.resetDelivery()
    assertEquals(true, tracker.update(200.0))
    assertNull(tracker.update(Double.NaN))
    tracker.configure(48.0, 48.0)
    assertNull(tracker.update(48.0))
  }

  @Test fun measuredPrefixMatchesLinearAndGridLines() {
    val heights = intArrayOf(30, 80, 80, 80, 40, 90, 60)
    // Linear: every item before the first visible one.
    assertEquals(0L, nativeListScrollPrefixPx(0, 1, { 1 }, heights::get))
    assertEquals(30L + 80 + 80, nativeListScrollPrefixPx(3, 1, { 1 }, heights::get))
    // Grid of 3 with a full-span header at 0: lines [0], [1,2,3], [4,5,6].
    val span = { position: Int -> if (position == 0) 3 else 1 }
    assertEquals(30L, nativeListScrollPrefixPx(1, 3, span, heights::get))
    assertEquals(30L, nativeListScrollPrefixPx(2, 3, span, heights::get))
    assertEquals(30L + 80, nativeListScrollPrefixPx(4, 3, span, heights::get))
    assertEquals(30L + 80, nativeListScrollPrefixPx(6, 3, span, heights::get))
    // The tallest cell defines a grid line.
    val uneven = intArrayOf(10, 20, 50, 5)
    assertEquals(50L, nativeListScrollPrefixPx(3, 3, { 1 }, uneven::get))
  }
}
