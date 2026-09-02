package com.margelo.nitro.onekeyimage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class OneKeyTerminalEventPairTest {
  @Test
  fun terminalPrimaryAndSnapshottedEndStayPairedDuringReentry() {
    val events = mutableListOf<String>()
    var currentEnd: () -> Unit = { events += "old-end" }
    val snapshottedEnd = currentEnd

    OneKeyTerminalEventPair.dispatch(
      primary = {
        events += "load"
        currentEnd = { events += "new-end" }
      },
      onLoadEnd = snapshottedEnd,
    )

    assertEquals(listOf("load", "old-end"), events)
  }

  @Test
  fun terminalEndStillRunsExactlyOnceWhenPrimaryThrows() {
    var endCount = 0

    assertThrows(IllegalStateException::class.java) {
      OneKeyTerminalEventPair.dispatch(
        primary = { throw IllegalStateException("callback failed") },
        onLoadEnd = { endCount += 1 },
      )
    }

    assertEquals(1, endCount)
  }
}
