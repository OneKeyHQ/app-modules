package com.margelo.nitro.nativelist

/**
 * Widths for identity title runs (docs/SPEC.md, "Identity title segments and
 * leading title icon"). A run's base width is its natural width under its cap;
 * the one shrinking run takes what the others leave; runs that do not shrink
 * and still overflow are cut from the end of the line.
 */
internal fun nativeListTitleSegmentWidths(
  natural: List<Float>,
  shrink: List<Boolean>,
  maxWidthRatio: List<Float?>,
  available: Float,
): List<Float> {
  val line = available.coerceAtLeast(0f)
  val widths =
    natural.indices
      .map { index ->
        val width = natural[index].coerceAtLeast(0f)
        val ratio = maxWidthRatio.getOrNull(index)
        if (ratio == null) width else minOf(width, ratio * line)
      }
      .toMutableList()
  val shrinking = shrink.indexOf(true)
  if (shrinking in widths.indices) {
    val others = widths.indices.filter { it != shrinking }.sumOf { widths[it].toDouble() }.toFloat()
    widths[shrinking] = minOf(widths[shrinking], line - others).coerceAtLeast(0f)
  }
  var overflow = widths.sum() - line
  var index = widths.lastIndex
  while (overflow > 0f && index >= 0) {
    val cut = minOf(widths[index], overflow)
    widths[index] -= cut
    overflow -= cut
    index -= 1
  }
  return widths
}

/** Wallet sidebar rows keep the plain title: no runs and no leading glyph (docs/SPEC.md). */
internal fun nativeListAllowsTitleDecorations(presentation: String): Boolean =
  presentation != "walletSidebar"

/**
 * Whether the runs replace `title`: not where `title` itself is highlighted
 * (`titleMatch` indexes it), and never on a wallet sidebar row.
 */
internal fun nativeListShowsTitleRuns(presentation: String, runCount: Int, matchCount: Int): Boolean =
  runCount > 0 && matchCount == 0 && nativeListAllowsTitleDecorations(presentation)
