package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NativeListDescriptionTouchPolicyTest {
  private fun hit(regions: List<NativeListTouchRegion>, x: Float, y: Float) = regions.any { it.contains(x, y) }

  private fun run(left: Float, right: Float) = NativeListTextRun(left, right)

  private fun regions(vararg lines: NativeListTextLine) = nativeListTouchableTextRegions(lines.toList(), slop = 8f)

  // The description view spans the identity column, so a tap anywhere on its
  // line used to open the description action. Only the text opens it now; the
  // blank rest of the line belongs to the row press (OK-67219).
  @Test
  fun onlyTheDrawnTextTakesTouches() {
    val text = regions(NativeListTextLine(top = 0f, bottom = 20f, touchable = listOf(run(0f, 60f))))

    assertTrue(hit(text, 30f, 10f))
    // The slop reaches into the blank on both sides of the text.
    assertTrue(hit(text, 66f, 10f))
    assertTrue(hit(text, -6f, 10f))
    assertFalse(hit(text, 120f, 10f))
  }

  // The caller states where the action text starts; the text before it, a
  // time for one, is not part of the action.
  @Test
  fun textBeforeTheStartIsNotPartOfTheAction() {
    val ltr = regions(
      NativeListTextLine(top = 0f, bottom = 20f, touchable = listOf(run(50f, 160f)), plain = listOf(run(0f, 50f))),
    )
    assertFalse(hit(ltr, 20f, 10f))
    assertTrue(hit(ltr, 100f, 10f))

    // Right to left the plain text sits on the right of the touchable text.
    val rtl = regions(
      NativeListTextLine(top = 0f, bottom = 20f, touchable = listOf(run(40f, 150f)), plain = listOf(run(150f, 200f))),
    )
    assertTrue(hit(rtl, 100f, 10f))
    assertFalse(hit(rtl, 180f, 10f))
  }

  // The slop is for blank space. It must not reach into plain text beside the
  // touchable text, or a prefix narrower than the slop, like the "i" of
  // "iRouter", would open the action it is not part of.
  @Test
  fun theSlopStopsAtPlainTextBesideTheTouchableText() {
    val short = regions(
      NativeListTextLine(top = 0f, bottom = 20f, touchable = listOf(run(3f, 46f)), plain = listOf(run(0f, 3f))),
    )
    assertFalse(hit(short, 1.5f, 10f))
    assertFalse(hit(short, -4f, 10f))
    assertTrue(hit(short, 4f, 10f))
    assertTrue(hit(short, 52f, 10f))
    assertEquals(3f, short.single().left)
    assertEquals(54f, short.single().right)

    // Neighbouring runs read back from pixel regions may be a pixel apart.
    val apart = regions(
      NativeListTextLine(top = 0f, bottom = 20f, touchable = listOf(run(4f, 46f)), plain = listOf(run(0f, 3f))),
    )
    assertEquals(3f, apart.single().left)
  }

  // Mixed-direction text can split one logical suffix into several visual
  // runs with plain text between them: "ABC אבג XYZ" from the last Hebrew
  // letter on draws that letter left of the two plain ones. Each run is its
  // own region, and the plain text between them takes no touches.
  @Test
  fun aSuffixSplitByMixedDirectionTextIsSeveralRuns() {
    val mixed = regions(
      NativeListTextLine(
        top = 0f, bottom = 20f,
        touchable = listOf(run(32f, 38f), run(55f, 86f)),
        plain = listOf(run(0f, 32f), run(38f, 55f)),
      ),
    )

    assertEquals(2, mixed.size)
    assertFalse(hit(mixed, 16f, 10f))
    assertTrue(hit(mixed, 35f, 10f))
    assertFalse(hit(mixed, 46f, 10f))
    assertTrue(hit(mixed, 70f, 10f))
    // Only the outer edge of the last run faces blank space.
    assertTrue(hit(mixed, 92f, 10f))
    assertEquals(32f, mixed[0].left)
    assertEquals(38f, mixed[0].right)
    assertEquals(55f, mixed[1].left)
  }

  // A narrow row wraps the description. The time stays outside the action on
  // its line, while the address is hit on both lines, also at the x the time
  // occupies one line above.
  @Test
  fun aWrappedDescriptionKeepsTheTimeOutsideTheAction() {
    val wrapped = regions(
      NativeListTextLine(top = 0f, bottom = 20f, touchable = listOf(run(55f, 110f)), plain = listOf(run(0f, 55f))),
      NativeListTextLine(top = 20f, bottom = 40f, touchable = listOf(run(0f, 100f))),
    )
    assertFalse(hit(wrapped, 20f, 10f))
    assertTrue(hit(wrapped, 80f, 10f))
    assertTrue(hit(wrapped, 20f, 30f))
    assertFalse(hit(wrapped, 130f, 30f))

    // The whole time sits on the first line: that line takes no touches at all.
    val timeOnItsOwnLine = regions(
      NativeListTextLine(top = 0f, bottom = 20f, touchable = emptyList(), plain = listOf(run(0f, 55f))),
      NativeListTextLine(top = 20f, bottom = 40f, touchable = listOf(run(0f, 100f))),
    )
    assertFalse(hit(timeOnItsOwnLine, 20f, 10f))
    // Nor does the address line reach up into it.
    assertFalse(hit(timeOnItsOwnLine, 20f, 19f))
    assertTrue(hit(timeOnItsOwnLine, 20f, 30f))
  }

  // A start at the end of the text leaves nothing to touch.
  @Test
  fun aLineWithoutTouchableTextLeavesNoRegion() {
    val none = regions(NativeListTextLine(top = 0f, bottom = 20f, touchable = emptyList(), plain = listOf(run(0f, 55f))))

    assertTrue(none.isEmpty())
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
    // Any safe integer arrives over JSON. One past Int range still clamps to
    // the end; narrowed to Int first it would wrap to 0, the whole text.
    assertEquals(label.length, nativeListDisplayOffset(label, 4_294_967_296L, foldsLineBreaks = false))
    assertEquals(label.length, nativeListDisplayOffset(label, Long.MAX_VALUE, foldsLineBreaks = true))

    // The emoji takes two UTF-16 units, as in the JavaScript string.
    val emojiPrefix = "🕘 08:48 • "
    assertEquals(11, emojiPrefix.length)
    assertEquals(label, "$emojiPrefix$label".substring(nativeListDisplayOffset("$emojiPrefix$label", 11, foldsLineBreaks = false)))

    val broken = "08:48\r\n• Router"
    assertEquals("08:48 • Router", nativeListFoldLineBreaks(broken))
    assertEquals(8, nativeListDisplayOffset(broken, 9, foldsLineBreaks = true))
    assertEquals(9, nativeListDisplayOffset(broken, 9, foldsLineBreaks = false))
    assertEquals(14, nativeListDisplayOffset(broken, 99, foldsLineBreaks = true))
  }
}
