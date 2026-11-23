package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Test

class NativeListScrollAlignmentTest {
  @Test
  fun oversizedGroupUsesMeasuredSignedSpaceForParentMiddleAndLastMember() {
    // Complete-app measurements: usable viewport 1377px, decorated group 4648px.
    // The caller supplies offsets for the parent and two hidden-wallet members.
    val offsets = listOf(2205, 105, -1995)
    assertEquals(
      listOf(591, -1509, -3609),
      offsets.map { nativeListAlignedStart(21, 1377, 4648, 0.5, it, false) },
    )
  }

  @Test
  fun explicitStartCenterAndEndPreservePaddingAndSignedOffsets() {
    assertEquals(8, nativeListAlignedStart(8, 400, 870, 0.0, 0, false))
    assertEquals(-227, nativeListAlignedStart(8, 400, 870, 0.5, 0, false))
    assertEquals(-462, nativeListAlignedStart(8, 400, 870, 1.0, 0, false))
    assertEquals(-547, nativeListAlignedStart(8, 400, 870, 0.5, -320, false))
    assertEquals(93, nativeListAlignedStart(8, 400, 870, 0.5, 320, false))
  }

  @Test
  fun shortAndEqualRowsKeepTheirExistingAlignment() {
    assertEquals(8, nativeListAlignedStart(8, 400, 68, 0.0, 0, false))
    assertEquals(174, nativeListAlignedStart(8, 400, 68, 0.5, 0, false))
    assertEquals(340, nativeListAlignedStart(8, 400, 68, 1.0, 0, false))
    assertEquals(28, nativeListAlignedStart(8, 400, 400, 0.5, 20, false))
  }

  @Test
  fun nearestRetainsLegacyOversizedAndShortRowBehavior() {
    assertEquals(21, nativeListAlignedStart(21, 1377, 4648, 0.0, 0, true))
    assertEquals(126, nativeListAlignedStart(21, 1377, 4648, 1.0, 105, true))
    assertEquals(174, nativeListAlignedStart(8, 400, 68, 0.5, 0, true))
  }

  @Test
  fun unmountedExplicitTargetStartsInsideViewportUntilMeasured() {
    assertEquals(1376, nativeListProvisionalStart(1377, 0.5, 2205, false))
    assertEquals(794, nativeListProvisionalStart(1377, 0.5, 105, false))
    assertEquals(0, nativeListProvisionalStart(1377, 0.5, -1995, false))
    assertEquals(1376, nativeListProvisionalStart(1377, 1.0, 0, false))
  }

  @Test
  fun provisionalBoundaryIsDefinedForEmptyAndOnePixelViewports() {
    assertEquals(0, nativeListProvisionalStart(0, 0.5, 2205, false))
    assertEquals(0, nativeListProvisionalStart(1, 0.5, -1995, false))
    assertEquals(0, nativeListProvisionalStart(1, 1.0, 2205, false))
  }

  @Test
  fun nearestKeepsItsExistingUnboundedProvisionalPosition() {
    assertEquals(2894, nativeListProvisionalStart(1377, 0.5, 2205, true))
    assertEquals(-1306, nativeListProvisionalStart(1377, 0.5, -1995, true))
  }
}
