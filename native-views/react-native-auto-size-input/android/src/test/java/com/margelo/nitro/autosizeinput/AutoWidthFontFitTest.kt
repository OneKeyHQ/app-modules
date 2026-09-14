package com.margelo.nitro.autosizeinput

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoWidthFontFitTest {
  // Synthetic metrics: ten digits plus a "$" prefix whose safety padding
  // scales with the font, mirroring measureTextViewWidthPx.
  private fun contentWidthAt(size: Float): Float {
    val text = 10 * 0.58f * size
    val prefix = 0.55f * size + 0.5f * size
    return text + prefix
  }

  @Test
  fun scalesSideLabelsWithTheCandidateSize() {
    val available = 400f
    val size = AutoWidthFontFit.findLargestFittingSize(16f, 67f, available, ::contentWidthAt)

    assertTrue(contentWidthAt(size) <= available)
    assertTrue(contentWidthAt(size + 0.5f) > available)
  }

  @Test
  fun regressionPrefixMeasuredAtOldSizeOverflows() {
    // Before the fix the prefix was measured once at the previous font size
    // (20sp here) and only the text was fitted, so the chosen size overflowed
    // once applyFontSize scaled the prefix up as well.
    val available = 400f
    val stalePrefix = 0.55f * 20f + 0.5f * 20f
    val staleFit = AutoWidthFontFit.findLargestFittingSize(16f, 67f, available - stalePrefix) { 10 * 0.58f * it }
    assertTrue(contentWidthAt(staleFit) > available)

    val fit = AutoWidthFontFit.findLargestFittingSize(16f, 67f, available, ::contentWidthAt)
    assertTrue(contentWidthAt(fit) <= available)
    assertTrue(fit < staleFit)
  }

  @Test
  fun returnsMinSizeWhenNothingFits() {
    assertEquals(16f, AutoWidthFontFit.findLargestFittingSize(16f, 67f, 0f, ::contentWidthAt), 0f)
    assertEquals(16f, AutoWidthFontFit.findLargestFittingSize(16f, 67f, 10f, ::contentWidthAt), 0f)
  }
}
