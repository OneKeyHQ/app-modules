package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NativeListTitleSegmentLayoutTest {
  // Wallet / Account as recipient rows send it: the wallet shrinks, the
  // account is capped at 70% of the line.
  private fun walletFirst(wallet: Float, account: Float, line: Float) =
    nativeListTitleSegmentWidths(
      listOf(wallet, 20f, account),
      listOf(true, false, false),
      listOf(null, null, 0.7f),
      line,
    )

  private fun assertWidths(expected: List<Float>, actual: List<Float>) {
    assertEquals(expected.size, actual.size)
    expected.zip(actual).forEach { (want, got) -> assertEquals(want, got, 0.001f) }
  }

  @Test
  fun keepsNaturalWidthsWhenTheyFit() = assertWidths(listOf(80f, 20f, 90f), walletFirst(80f, 90f, 300f))

  @Test
  fun theShrinkingRunGivesWayFirst() = assertWidths(listOf(130f, 20f, 100f), walletFirst(200f, 100f, 250f))

  @Test
  fun aCappedRunStopsAtItsShareOfTheLine() = assertWidths(listOf(70f, 20f, 210f), walletFirst(100f, 400f, 300f))

  @Test
  fun theAccountShrinksWhenTheWalletMustStayWhole() =
    assertWidths(
      listOf(210f, 20f, 70f),
      nativeListTitleSegmentWidths(
        listOf(300f, 20f, 150f),
        listOf(false, false, true),
        listOf(0.7f, null, null),
        300f,
      ),
    )

  @Test
  fun runsThatDoNotShrinkAreCutFromTheEnd() {
    assertWidths(
      listOf(100f, 50f, 0f),
      nativeListTitleSegmentWidths(
        listOf(100f, 100f, 100f),
        listOf(false, false, true),
        listOf(null, null, null),
        150f,
      ),
    )
    assertWidths(
      listOf(100f, 20f),
      nativeListTitleSegmentWidths(listOf(100f, 100f), listOf(false, false), listOf(null, null), 120f),
    )
  }

  @Test
  fun aNonPositiveLineLeavesNothing() {
    assertWidths(listOf(0f, 0f, 0f), walletFirst(80f, 90f, 0f))
    assertWidths(listOf(0f, 0f, 0f), walletFirst(80f, 90f, -10f))
  }

  // Runs replace `title` except where `title` is highlighted; wallet sidebar
  // rows keep the plain title without runs or a leading glyph.
  @Test
  fun titleDecorationsFollowTheRowContract() {
    assertTrue(nativeListShowsTitleRuns("", 2, 0))
    assertTrue(nativeListShowsTitleRuns("accountSelector", 2, 0))
    assertFalse(nativeListShowsTitleRuns("", 0, 0))
    assertFalse(nativeListShowsTitleRuns("", 2, 1))
    assertFalse(nativeListShowsTitleRuns("walletSidebar", 2, 0))
    assertTrue(nativeListAllowsTitleDecorations(""))
    assertFalse(nativeListAllowsTitleDecorations("walletSidebar"))
  }

  // Two runs that do not shrink and one line that holds only the first: the
  // first is drawn in full, so it has to carry the ellipsis.
  @Test
  fun aWholeRunCutFromTheEndPutsTheEllipsisOnTheLastRunShown() {
    val natural = listOf(100f, 100f)
    val widths = nativeListTitleSegmentWidths(natural, listOf(false, false), listOf(null, null), 100f)
    assertWidths(listOf(100f, 0f), widths)
    assertEquals(0, nativeListTitleSegmentOverflowIndex(natural, listOf(false, false), widths))
  }

  // The shrinking run may give up its whole width: the runs that stay whole
  // fit, so none of them is cut for an ellipsis.
  @Test
  fun noEllipsisIsAddedWhenOnlyTheShrinkingRunDisappears() {
    val natural = listOf(100f, 60f)
    val shrink = listOf(false, true)
    val widths = nativeListTitleSegmentWidths(natural, shrink, listOf(null, null), 100f)
    assertWidths(listOf(100f, 0f), widths)
    assertNull(nativeListTitleSegmentOverflowIndex(natural, shrink, widths))
    // A fixed run cut behind the shrinking one still counts.
    assertEquals(
      0,
      nativeListTitleSegmentOverflowIndex(
        listOf(100f, 50f, 100f),
        listOf(false, true, false),
        listOf(100f, 0f, 0f),
      ),
    )
  }

  @Test
  fun noEllipsisIsAddedWhenTheLineFitsOrIsCutInsideARun() {
    val fixed = listOf(false, false, false)
    assertNull(nativeListTitleSegmentOverflowIndex(listOf(80f, 20f, 90f), fixed, listOf(80f, 20f, 90f)))
    // Cut inside a run: that run truncates by itself.
    assertNull(nativeListTitleSegmentOverflowIndex(listOf(100f, 100f), fixed, listOf(100f, 40f)))
    assertNull(
      nativeListTitleSegmentOverflowIndex(listOf(100f, 100f, 100f), fixed, listOf(100f, 40f, 0f))
    )
    assertNull(nativeListTitleSegmentOverflowIndex(listOf(100f, 100f), fixed, listOf(0f, 0f)))
  }

  @Test
  fun aRunFoldsLineBreaksIntoSpaces() {
    assertEquals("Main Wallet", nativeListTitleRunText("Main\nWallet"))
    assertEquals("a b c", nativeListTitleRunText("a\r\nb\rc"))
    assertEquals("Account #1", nativeListTitleRunText("Account #1"))
  }
}
