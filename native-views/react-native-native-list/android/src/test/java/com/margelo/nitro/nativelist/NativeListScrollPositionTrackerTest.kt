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
}
