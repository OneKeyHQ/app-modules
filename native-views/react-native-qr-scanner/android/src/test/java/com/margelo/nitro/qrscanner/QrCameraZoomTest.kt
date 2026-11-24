package com.margelo.nitro.qrscanner

import org.junit.Assert.assertEquals
import org.junit.Test

class QrCameraZoomTest {
  @Test
  fun capsZoomAtEightTimes() {
    assertEquals(8f, QrCameraZoom.clampRatio(30f, 1f, 10f), 0f)
  }

  @Test
  fun keepsTheDeviceRange() {
    // Logical cameras with an ultra-wide lens allow ratios below 1.
    assertEquals(0.6f, QrCameraZoom.clampRatio(0.2f, 0.6f, 10f), 0f)
    assertEquals(4f, QrCameraZoom.clampRatio(6f, 1f, 4f), 0f)
    assertEquals(2.5f, QrCameraZoom.clampRatio(2.5f, 1f, 10f), 0f)
  }

  @Test
  fun aFixedFocalLengthCameraStaysAtOne() {
    assertEquals(1f, QrCameraZoom.clampRatio(3f, 1f, 1f), 0f)
  }
}
