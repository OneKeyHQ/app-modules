package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NativeListDescriptionTouchPolicyTest {
  private fun hit(regions: List<NativeListTouchRegion>, x: Float, y: Float) = regions.any { it.contains(x, y) }

  // The description view spans the identity column, so a tap anywhere on its
  // line used to open the description action. Only the text opens it now; the
  // blank rest of the line belongs to the row press (OK-67219).
  @Test
  fun onlyTheDrawnTextTakesTouches() {
    val regions = nativeListTouchableTextRegions(
      lines = listOf(NativeListTextLine(start = 0, end = 7, left = 0f, top = 0f, right = 60f, bottom = 20f)),
      textStart = 0, textStartX = 0f, slop = 8f,
    )

    assertTrue(hit(regions, 30f, 10f))
    assertTrue(hit(regions, 66f, 10f))
    assertFalse(hit(regions, 120f, 10f))
  }

  // The caller states where the action text starts; the text before it, a
  // time for one, is not part of the action.
  @Test
  fun textBeforeTheStartIsNotPartOfTheAction() {
    val ltr = nativeListTouchableTextRegions(
      lines = listOf(NativeListTextLine(start = 0, end = 15, left = 0f, top = 0f, right = 160f, bottom = 20f)),
      textStart = 8, textStartX = 50f, slop = 8f,
    )
    assertFalse(hit(ltr, 20f, 10f))
    assertTrue(hit(ltr, 100f, 10f))

    val rtl = nativeListTouchableTextRegions(
      lines = listOf(
        NativeListTextLine(start = 0, end = 15, left = 40f, top = 0f, right = 200f, bottom = 20f, isRtl = true),
      ),
      textStart = 8, textStartX = 150f, slop = 8f,
    )
    assertTrue(hit(rtl, 100f, 10f))
    assertFalse(hit(rtl, 180f, 10f))
  }

  // A narrow row wraps the description. The time stays outside the action on
  // its line, while the address is hit on both lines, also at the x the time
  // occupies one line above.
  @Test
  fun aWrappedDescriptionKeepsTheTimeOutsideTheAction() {
    val wrapped = nativeListTouchableTextRegions(
      lines = listOf(
        NativeListTextLine(start = 0, end = 16, left = 0f, top = 0f, right = 110f, bottom = 20f),
        NativeListTextLine(start = 16, end = 32, left = 0f, top = 20f, right = 100f, bottom = 40f),
      ),
      textStart = 8, textStartX = 55f, slop = 8f,
    )
    assertFalse(hit(wrapped, 20f, 10f))
    assertTrue(hit(wrapped, 80f, 10f))
    assertTrue(hit(wrapped, 20f, 30f))
    assertFalse(hit(wrapped, 130f, 30f))

    // The whole time sits on the first line: that line takes no touches at all.
    val timeOnItsOwnLine = nativeListTouchableTextRegions(
      lines = listOf(
        NativeListTextLine(start = 0, end = 8, left = 0f, top = 0f, right = 55f, bottom = 20f),
        NativeListTextLine(start = 8, end = 24, left = 0f, top = 20f, right = 100f, bottom = 40f),
      ),
      textStart = 8, textStartX = 0f, slop = 8f,
    )
    assertFalse(hit(timeOnItsOwnLine, 20f, 10f))
    // Nor does the address line reach up into it.
    assertFalse(hit(timeOnItsOwnLine, 20f, 19f))
    assertTrue(hit(timeOnItsOwnLine, 20f, 30f))
  }

  // A start at the end of the text leaves nothing to touch.
  @Test
  fun aStartAtTheEndOfTheTextLeavesNoRegion() {
    val regions = nativeListTouchableTextRegions(
      lines = listOf(NativeListTextLine(start = 0, end = 8, left = 0f, top = 0f, right = 55f, bottom = 20f)),
      textStart = 8, textStartX = 0f, slop = 8f,
    )

    assertTrue(regions.isEmpty())
  }

  // `descriptionActionStart` is a UTF-16 offset into the caller's string. It is
  // kept within the text, and follows the line breaks a one-line view folds.
  @Test
  fun theCallersStartIsMappedOntoTheDisplayedText() {
    val label = "Router • V2"
    assertEquals(0, nativeListDisplayOffset(label, 0, foldsLineBreaks = false))
    assertEquals(8, nativeListDisplayOffset("08:48 • $label", 8, foldsLineBreaks = false))
    assertEquals(0, nativeListDisplayOffset(label, -3, foldsLineBreaks = false))
    assertEquals(label.length, nativeListDisplayOffset(label, 99, foldsLineBreaks = false))

    // The emoji takes two UTF-16 units, as in the JavaScript string.
    val emojiPrefix = "\uD83D\uDD58 08:48 • "
    assertEquals(11, emojiPrefix.length)
    assertEquals(label, "$emojiPrefix$label".substring(nativeListDisplayOffset("$emojiPrefix$label", 11, foldsLineBreaks = false)))

    val broken = "08:48\r\n• Router"
    assertEquals("08:48 • Router", nativeListFoldLineBreaks(broken))
    assertEquals(8, nativeListDisplayOffset(broken, 9, foldsLineBreaks = true))
    assertEquals(9, nativeListDisplayOffset(broken, 9, foldsLineBreaks = false))
    assertEquals(14, nativeListDisplayOffset(broken, 99, foldsLineBreaks = true))
  }
}
