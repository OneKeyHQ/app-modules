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

/**
 * The run that has to end with an ellipsis because the line was cut exactly
 * after it: a run that does not shrink lost its whole width, and this one is
 * drawn in full, so nothing else would show that the title goes on. Null when
 * the line is not cut, when it is cut inside a run, which then truncates by
 * itself, and when only the shrinking run gave up its width, which it may.
 */
internal fun nativeListTitleSegmentOverflowIndex(
  natural: List<Float>,
  shrink: List<Boolean>,
  widths: List<Float>,
): Int? {
  val last = widths.indexOfLast { it > 0f }
  if (last !in natural.indices) return null
  val cutsAFixedRun =
    natural.indices.any { it > last && natural[it] > 0f && shrink.getOrNull(it) != true }
  return if (cutsAFixedRun && widths[last] >= natural[last]) last else null
}

/** A run is one line: a hard break folds into one space, as in a one-line title. */
internal fun nativeListTitleRunText(text: String): String = text.replace(Regex("\\r\\n|[\\r\\n]"), " ")
