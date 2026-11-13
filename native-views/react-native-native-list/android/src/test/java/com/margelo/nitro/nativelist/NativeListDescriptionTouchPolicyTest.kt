package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NativeListDescriptionTouchPolicyTest {
  // The description view spans the identity column, so a tap anywhere on its
  // line used to open the description action. Only the text opens it now; the
  // blank rest of the line belongs to the row press (OK-67219).
  @Test
  fun onlyTheDrawnTextTakesTouches() {
    val span = nativeListTouchableTextSpan(
      lineLeft = 0f, lineRight = 60f, textStart = null, isRtl = false, slop = 8f,
    )

    assertTrue(30f in span)
    assertTrue(66f in span)
    assertFalse(120f in span)
  }

  // Of `time • address` only the address opens the action, as on Web.
  @Test
  fun aLeadingTimeIsNotPartOfTheAction() {
    assertEquals(0, nativeListDescriptionActionTextStart("0x35e6cb…cd1f0e"))
    assertEquals(8, nativeListDescriptionActionTextStart("08:48 • Uniswap"))

    val ltr = nativeListTouchableTextSpan(
      lineLeft = 0f, lineRight = 160f, textStart = 50f, isRtl = false, slop = 8f,
    )
    assertFalse(20f in ltr)
    assertTrue(100f in ltr)

    val rtl = nativeListTouchableTextSpan(
      lineLeft = 40f, lineRight = 200f, textStart = 150f, isRtl = true, slop = 8f,
    )
    assertTrue(100f in rtl)
    assertFalse(180f in rtl)
  }
}
