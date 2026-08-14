package com.margelo.nitro.autosizeinput

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TextUpdateEventCounterTest {
  @Test
  fun allowsJsUpdatesBeforeNativeInput() {
    val counter = TextUpdateEventCounter()

    assertTrue(counter.canApplyJsUpdate())
  }

  @Test
  fun allowsUncountedProgrammaticResetAfterNativeEdits() {
    // Regression: callers that never supply mostRecentEventCount must keep
    // full control — a native edit followed by an uncounted reset (clear,
    // sanitization, form restore) has to apply.
    val counter = TextUpdateEventCounter()
    repeat(3) {
      counter.recordNativeChange()
    }

    assertTrue(counter.canApplyJsUpdate())
  }

  @Test
  fun optsIntoStaleFilteringOnlyOnceACountIsSupplied() {
    val counter = TextUpdateEventCounter()
    counter.recordNativeChange()

    assertTrue(counter.canApplyJsUpdate())

    counter.mostRecentEventCount = 0
    assertFalse(counter.canApplyJsUpdate())

    counter.mostRecentEventCount = 1
    assertTrue(counter.canApplyJsUpdate())
  }

  @Test
  fun rejectsJsUpdatesThatAreOlderThanNativeInput() {
    val counter = TextUpdateEventCounter()
    counter.recordNativeChange()
    counter.recordNativeChange()
    counter.mostRecentEventCount = 1

    assertFalse(counter.canApplyJsUpdate())
  }

  @Test
  fun allowsTheLatestAcknowledgedJsUpdate() {
    val counter = TextUpdateEventCounter()
    counter.recordNativeChange()
    counter.recordNativeChange()
    counter.mostRecentEventCount = 2

    assertTrue(counter.canApplyJsUpdate())
  }

  @Test
  fun tracksEveryNativeTextChange() {
    val counter = TextUpdateEventCounter()

    repeat(10) {
      counter.recordNativeChange()
    }

    assertEquals(10, counter.nativeEventCount)
  }

  @Test
  fun allowsAnAcknowledgedDeletionWithoutNewerInput() {
    val counter = TextUpdateEventCounter()
    repeat(4) {
      counter.recordNativeChange()
    }
    counter.mostRecentEventCount = 4
    counter.recordNativeChange()
    counter.mostRecentEventCount = 5

    assertTrue(counter.canApplyJsUpdate())
  }

  @Test
  fun rejectsAStaleDeletionUpdateAfterTheNextDigit() {
    val counter = TextUpdateEventCounter()
    repeat(4) {
      counter.recordNativeChange()
    }
    counter.mostRecentEventCount = 4
    counter.recordNativeChange()
    counter.recordNativeChange()
    counter.mostRecentEventCount = 5

    assertFalse(counter.canApplyJsUpdate())

    counter.mostRecentEventCount = 6
    assertTrue(counter.canApplyJsUpdate())
  }
}
