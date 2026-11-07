package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
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
}
