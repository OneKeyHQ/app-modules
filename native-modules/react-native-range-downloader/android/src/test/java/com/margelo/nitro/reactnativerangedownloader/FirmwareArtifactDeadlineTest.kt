package com.margelo.nitro.reactnativerangedownloader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class FirmwareArtifactDeadlineTest {
  @Test
  fun defaultsToABoundedThreeMinuteDeadline() {
    assertEquals(180.0, validateFirmwareDownloadDeadlineSeconds(null), 0.0)
  }

  @Test
  fun preservesFractionalDeadlines() {
    assertEquals(2.75, validateFirmwareDownloadDeadlineSeconds(2.75), 0.0)
  }

  @Test
  fun rejectsUnboundedOrInvalidDeadlines() {
    listOf(0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY, 86_400.1).forEach {
      assertThrows(IllegalArgumentException::class.java) {
        validateFirmwareDownloadDeadlineSeconds(it)
      }
    }
  }
}
