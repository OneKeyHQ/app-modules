package com.margelo.nitro.nativelist

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.LineHeightSpan
import android.util.TypedValue
import android.view.Gravity
import kotlin.math.roundToInt
import org.json.JSONObject

internal class NativeListLineHeightSpan(private val height: Int) : LineHeightSpan {
  override fun chooseHeight(
    text: CharSequence,
    start: Int,
    end: Int,
    spanstartv: Int,
    v: Int,
    fm: Paint.FontMetricsInt,
  ) {
    val leading = height - (fm.descent - fm.ascent)
    fm.ascent -= kotlin.math.ceil(leading / 2.0).toInt()
    fm.descent += kotlin.math.floor(leading / 2.0).toInt()
    if (start == 0) fm.top = fm.ascent
    if (end == text.length) fm.bottom = fm.descent
  }
}

internal data class NativeListResolvedText(
  val text: String,
  val fontSize: Float,
  val typeface: Typeface,
  val color: Int,
  val lineHeight: Int,
  val explicitLineHeight: Boolean,
  val lines: Int,
  val explicitTruncation: Boolean,
  val clip: Boolean,
  val gravity: Int,
  val offset: Float,
) {
  fun bind(view: NativeListTextView) {
    view.includeFontPadding = false
    view.fontFeatureSettings = "tnum"
    view.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
    view.typeface = typeface
    view.setTextColor(color)
    view.letterSpacing = 0f
    view.gravity = gravity
    view.opticalOffsetY = offset
    view.maxLines = lines
    view.setHorizontallyScrolling(lines == 1 && explicitTruncation)
    view.ellipsize = if (explicitTruncation && !clip) TextUtils.TruncateAt.END else null
    view.setLineSpacing(0f, 1f)
    if (explicitLineHeight) {
      view.text =
        SpannableStringBuilder(text).apply {
          setSpan(
            NativeListLineHeightSpan(lineHeight),
            0,
            length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
          )
        }
    } else {
      view.text = text
      if (lineHeight > 0) androidx.core.widget.TextViewCompat.setLineHeight(view, lineHeight)
    }
    view.visibility = if (text.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
  }

  companion object {
    fun resolve(
      context: Context,
      text: String,
      style: JSONObject?,
      size: Float,
      weight: String,
      color: Int,
      lineHeight: Int,
      lines: Int,
      sourceScale: Boolean,
    ): NativeListResolvedText {
      val density = context.resources.displayMetrics.density
      val maxLines = (style?.optInt("lines", lines) ?: lines).coerceIn(1, 3)
      val truncation = style?.let { it.has("lines") || it.has("truncate") } == true
      val fontSize =
        if (style?.has("fontSize") == true) (style.optDouble("fontSize") * density).toFloat()
        else
          (if (sourceScale) size else NativeListScale.font(context.resources, size)) *
            context.resources.displayMetrics.scaledDensity
      val face =
        when (style?.optString("fontWeight", weight) ?: weight) {
          "medium" -> NativeListFonts.medium(context)
          "semibold" -> NativeListFonts.semibold(context)
          "bold" -> NativeListFonts.bold(context)
          else -> NativeListFonts.regular(context)
        }
      val line =
        if (style?.has("lineHeight") == true) (style.optDouble("lineHeight") * density).roundToInt()
        else if (sourceScale) (lineHeight * density).roundToInt()
        else NativeListScale.dp(context.resources, lineHeight)
      val horizontal =
        when (style?.optString("alignment")) {
          "center" -> Gravity.CENTER_HORIZONTAL
          "end" -> Gravity.END
          else -> Gravity.START
        }
      val vertical =
        when (style?.optString("verticalAlignment")) {
          "center" -> Gravity.CENTER_VERTICAL
          "bottom" -> Gravity.BOTTOM
          else -> Gravity.TOP
        }
      return NativeListResolvedText(
        if (maxLines == 1 && truncation) text.replace(Regex("\\r\\n|[\\r\\n]"), " ") else text,
        fontSize,
        face,
        style
          ?.optString("color")
          ?.takeIf { it.isNotEmpty() }
          ?.let { runCatching { parseNativeListColor(it) }.getOrDefault(color) } ?: color,
        line,
        style?.has("lineHeight") == true,
        maxLines,
        truncation,
        style?.optString("truncate") == "clip",
        horizontal or vertical,
        ((style?.optDouble("offsetY", 0.0) ?: 0.0) * density).toFloat(),
      )
    }
  }
}
