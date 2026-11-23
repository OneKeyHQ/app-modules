package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Test

class NativeListLeadingSlotFrameTest {
  // 40dp box, 16dp badge hanging 2dp past the frame, 2dp pair ring.
  private fun frame(index: Int, count: Int, network: Int? = null, pair: Int? = null) =
    nativeListLeadingSlotFrame(
      index = index,
      sourceCount = count,
      networkIndex = network,
      pairIndex = pair,
      width = 40,
      height = 40,
      badgeSize = 16,
      badgeOverhang = 2,
      pairRing = 2,
    )

  private val full = NativeListSlotFrame(0, 0, 40, 40)
  private val badge = NativeListSlotFrame(26, 26, 16, 16)
  private val primary = NativeListSlotFrame(0, 0, 24, 24)
  private val secondary = NativeListSlotFrame(14, 14, 24, 24)

  @Test
  fun loneTokenFillsTheBox() {
    assertEquals(full, frame(0, 1))
  }

  @Test
  fun tokenWithNetworkBadgeKeepsTheCornerBadge() {
    assertEquals(listOf(full, badge), listOf(frame(0, 2, network = 1), frame(1, 2, network = 1)))
  }

  // OK-66071: the history swap rows bind [send, network, receive]. Android laid
  // the receive token over the whole box, hiding the send token and the network
  // badge; iOS drew it in the badge's corner slot over the badge.
  @Test
  fun activityPairStepsTheSecondaryToTheBottomRightUnderTheBadge() {
    assertEquals(
      listOf(primary, badge, secondary),
      (0 until 3).map { frame(it, 3, network = 1, pair = 2) },
    )
  }

  @Test
  fun activityPairWithoutNetworkBadge() {
    assertEquals(listOf(primary, secondary), listOf(frame(0, 2, pair = 1), frame(1, 2, pair = 1)))
  }

  @Test
  fun pairMemberIsSixTenthsOfTheBox() {
    assertEquals(listOf(24, 19, 29), listOf(40, 32, 48).map(::nativeListLeadingPairMemberSize))
  }

  @Test
  fun stackedImagesStepDiagonally() {
    assertEquals(
      listOf(
        NativeListSlotFrame(0, 5, 29, 29),
        NativeListSlotFrame(6, 5, 29, 29),
        NativeListSlotFrame(11, 5, 29, 29),
      ),
      (0 until 3).map { frame(it, 3) },
    )
  }
}
