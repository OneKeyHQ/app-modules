package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Test

class NativeListLeadingVisualClipPolicyTest {
  // OK-65694: the account selector's network badge is laid out past the leading
  // frame. Android clips a child to its own bounds while its parent clips
  // children, so a host row must stop clipping exactly when a bound badge
  // overhangs; a plain image keeps the default clipping.
  @Test
  fun hostStopsClippingOnlyForBadgesLaidOutPastTheFrame() {
    data class Case(
      val kind: String,
      val sourceCount: Int = 1,
      val hasCornerIcon: Boolean = false,
      val overlayCount: Int = 0,
      val expected: Boolean,
    )

    val cases = listOf(
      Case(kind = "account", expected = false),
      Case(kind = "account", overlayCount = 1, expected = true),
      Case(kind = "wallet", overlayCount = 2, expected = true),
      Case(kind = "token", sourceCount = 1, expected = false),
      Case(kind = "token", sourceCount = 2, expected = true),
      Case(kind = "stackedImages", sourceCount = 3, expected = false),
      Case(kind = "network", hasCornerIcon = true, expected = true),
      Case(kind = "icon", sourceCount = 0, expected = false),
    )

    assertEquals(
      cases.map(Case::expected),
      cases.map { case ->
        nativeListLeadingVisualOverhangsFrame(
          kind = case.kind,
          sourceCount = case.sourceCount,
          hasCornerIcon = case.hasCornerIcon,
          overlayCount = case.overlayCount,
        )
      },
    )
  }
}
