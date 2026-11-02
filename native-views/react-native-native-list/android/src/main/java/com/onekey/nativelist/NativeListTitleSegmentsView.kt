package com.margelo.nitro.nativelist

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.MetricAffectingSpan

/**
 * Identity title runs on one line. The text is rebuilt for each measured width
 * from the widths `nativeListTitleSegmentWidths` allocates, so a run gives up
 * width without the others; accessibility reads the row's `title` instead.
 */
internal class NativeListTitleSegmentsView(context: android.content.Context) :
  NativeListTextView(context) {
  class Run(
    val text: String,
    // Null keeps the view's typeface, which follows the title's.
    val typeface: Typeface?,
    val color: Int,
    val shrink: Boolean,
    val maxWidthRatio: Float?,
  )

  private var runs: List<Run> = emptyList()
  private var builtForWidth = -1

  fun setRuns(runs: List<Run>, spokenText: String) {
    this.runs = runs
    builtForWidth = -1
    contentDescription = spokenText.takeIf(String::isNotEmpty)
    text = runs.joinToString("") { it.text }
    requestLayout()
  }

  fun clearRuns() {
    runs = emptyList()
    builtForWidth = -1
    contentDescription = null
    text = ""
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    if (runs.isEmpty()) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec)
      return
    }
    var outer =
      if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) Int.MAX_VALUE
      else MeasureSpec.getSize(widthMeasureSpec)
    if (maxWidth in 0 until outer) outer = maxWidth
    val available =
      if (outer == Int.MAX_VALUE) outer
      else (outer - compoundPaddingLeft - compoundPaddingRight).coerceAtLeast(0)
    if (available != builtForWidth) {
      builtForWidth = available
      // The same width rebuilds the same text, so the relayout this asks for
      // measures once more and stops.
      setText(build(available), BufferType.SPANNABLE)
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    // The runs fill what the title line leaves, so a ratio is a share of it.
    if (outer != Int.MAX_VALUE) setMeasuredDimension(outer, measuredHeight)
  }

  private fun build(available: Int): CharSequence {
    val paints = runs.map { run -> TextPaint(paint).apply { run.typeface?.let { typeface = it } } }
    val natural = runs.mapIndexed { index, run -> paints[index].measureText(run.text) }
    val widths =
      nativeListTitleSegmentWidths(
        natural,
        runs.map { it.shrink },
        runs.map { it.maxWidthRatio },
        if (available == Int.MAX_VALUE) Float.MAX_VALUE else available.toFloat(),
      )
    val built = SpannableStringBuilder()
    runs.forEachIndexed { index, run ->
      val width = widths[index]
      if (width <= 0f) return@forEachIndexed
      val shown =
        if (width >= natural[index]) run.text
        else TextUtils.ellipsize(run.text, paints[index], width, TextUtils.TruncateAt.END)
      if (shown.isEmpty()) return@forEachIndexed
      val start = built.length
      built.append(shown)
      built.setSpan(ForegroundColorSpan(run.color), start, built.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
      run.typeface?.let {
        built.setSpan(TypefaceRunSpan(it), start, built.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
      }
    }
    return built
  }
}

private class TypefaceRunSpan(private val typeface: Typeface) : MetricAffectingSpan() {
  override fun updateMeasureState(paint: TextPaint) {
    paint.typeface = typeface
  }

  override fun updateDrawState(paint: TextPaint) {
    paint.typeface = typeface
  }
}
