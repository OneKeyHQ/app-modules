package com.margelo.nitro.nativelist

import android.graphics.Color
import android.graphics.Outline
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONArray
import org.json.JSONObject

internal class NativeListMetricCardRowView(context: ThemedReactContext) :
  NativeListRendererRowView(context) {
  private val visual = NativeListLeadingVisual(context)
  private val column = LinearLayout(context).apply { orientation = VERTICAL }
  private val titleLine =
    LinearLayout(context).apply {
      orientation = HORIZONTAL
      gravity = Gravity.CENTER_VERTICAL
    }
  private val title = NativeListTextView(context)
  private val label = NativeListTextView(context)
  private val trend = NativeListTextView(context)
  private val detail = NativeListTextView(context)
  private val badge = NativeListTextView(context)
  private val metricImages = List(5) { NativeListImageSlot(context) }
  private val usedImages = mutableSetOf<Int>()
  private var rowKey = ""
  private var compositeMetricTitle: TextView? = null
  override val assetFields = listOf("visual", "metrics")
  override val defaultCornerRadius = 12

  override fun unselectedBackground(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    itemIndex: Int?,
  ) =
    if (layout == "sectioned" && !item.json.optBoolean("selected"))
      super.unselectedBackground(item, theme, layout, itemIndex)
    else color(theme, "subduedBackground", "#F9F9F9")

  override fun defaultHeight(item: NativeListItem, layout: String) =
    if (item.json.optString("variant") in setOf("activity", "performance")) 0 else 132

  init {
    titleLine.addView(title, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
    titleLine.addView(badge)
    column.addView(titleLine)
    column.addView(label)
    column.addView(trend)
    column.addView(detail)
  }

  private fun color(theme: JSONObject?, name: String, fallback: String) =
    parseNativeListColor(theme?.optString(name, fallback) ?: fallback)

  private fun safeColor(value: String, fallback: Int) =
    runCatching { parseNativeListColor(value) }.getOrDefault(fallback)

  private fun roundedFill(color: Int, radius: Float) =
    GradientDrawable().apply {
      setColor(color)
      cornerRadius = dp(radius.toInt()).toFloat()
    }

  private fun sp(value: Float) = if (sourceScale) value else NativeListScale.font(resources, value)

  private fun wrap() = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

  private val circleOutlineProvider =
    object : ViewOutlineProvider() {
      override fun getOutline(view: View, outline: Outline) {
        outline.setOval(0, 0, view.width, view.height)
      }
    }

  private fun visualSources(visual: JSONObject): List<Pair<JSONObject, String>> {
    val kind = visual.optString("kind")
    val variant =
      when (kind) {
        "token" -> "token"
        "network" -> "network"
        "account",
        "wallet" -> "avatar"
        else -> "generic"
      }
    if (kind == "icon") return emptyList()
    if (kind == "stackedImages") {
      val images = visual.optJSONArray("images") ?: return emptyList()
      return (0 until images.length()).map { images.getJSONObject(it) to "generic" }
    }
    return visual.optJSONObject("image")?.let { listOf(it to variant) } ?: emptyList()
  }

  private fun dataTextColor(tone: String, theme: JSONObject?): Int =
    when (tone) {
      "positive" -> color(theme, "positive", "#00713FDE")
      "negative" -> color(theme, "negative", "#C40006D3")
      "secondary" -> color(theme, "secondaryText", "#0000009B")
      else -> color(theme, "primaryText", "#000000DF")
    }

  override fun bindContent(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    removeAllViews()
    usedImages.clear()
    rowKey = item.key
    compositeMetricTitle = null
    orientation = VERTICAL
    gravity = Gravity.START
    val data = item.json
    val style = data.optJSONObject("style") ?: JSONObject()
    val image = style.optJSONObject("image") ?: JSONObject()
    val hp =
      if (style.has("horizontalPadding")) stylePx(style.optDouble("horizontalPadding")) else dp(14)
    val vp =
      if (style.has("verticalPadding")) stylePx(style.optDouble("verticalPadding")) else dp(14)
    setPadding(hp, vp, hp, vp)
    fun text(
      view: NativeListTextView,
      value: String,
      slot: String,
      size: Float,
      weight: String = "regular",
      ink: Int = color(theme, "secondaryText", "#0000009B"),
    ) {
      NativeListResolvedText.resolve(
          context,
          value,
          style.optJSONObject(slot),
          size,
          weight,
          ink,
          0,
          1,
          sourceScale || slot == "subtitle",
        )
        .bind(view)
      if (style.optJSONObject(slot)?.optString("truncate") != "clip")
        view.ellipsize = TextUtils.TruncateAt.END
    }
    if (data.optString("variant") in setOf("activity", "performance")) {
      visual.recycle()
      bindCompositeMetricCard(item, theme, data.optString("variant"))
      val heading = compositeMetricTitle as? NativeListTextView
      if (heading != null) {
        val spacing = heading.letterSpacing
        NativeListResolvedText.resolve(
            context,
            data.optString("title").uppercase(),
            style.optJSONObject("title"),
            11f,
            "regular",
            color(theme, "disabledText", "#00000072"),
            14,
            1,
            sourceScale,
          )
          .bind(heading)
        heading.letterSpacing = spacing
        // Legacy metricText headings always ellipsize unless a style clips them.
        if (style.optJSONObject("title")?.optString("truncate") != "clip")
          heading.ellipsize = TextUtils.TruncateAt.END
      }
      if (style.has("lineGap")) {
        val gap = stylePx(style.optDouble("lineGap"))
        for (index in 1 until childCount) {
          val params = getChildAt(index).layoutParams as LayoutParams
          params.topMargin = gap
          params.bottomMargin = 0
          getChildAt(index).layoutParams = params
        }
      }
    } else {
      data.optJSONObject("visual")?.let { descriptor ->
        (visual.parent as? android.view.ViewGroup)?.removeView(visual)
        visual.bind(descriptor, image, item.key, theme, false, sourceScale)
        addView(
          visual,
          LayoutParams(
              if (image.has("width")) stylePx(image.optDouble("width")) else dp(32),
              if (image.has("height")) stylePx(image.optDouble("height")) else dp(32),
            )
            .apply {
              marginEnd = dp(12)
              bottomMargin =
                if (style.has("leadingGap")) stylePx(style.optDouble("leadingGap"))
                else if (style.has("lineGap")) stylePx(style.optDouble("lineGap")) else 0
            },
        )
      } ?: visual.recycle()
      text(
        title,
        data.optString("value"),
        "value",
        if (data.optString("size") == "large") 24f else 18f,
        "semibold",
        color(theme, "primaryText", "#000000DF"),
      )
      text(
        label,
        data.optString("title"),
        "title",
        11f,
        ink = color(theme, "disabledText", "#00000072"),
      )
      text(
        trend,
        data.optString("trend"),
        "trend",
        12f,
        ink =
          when (data.optString("trendTone")) {
            "positive" -> color(theme, "positive", "#00713FDE")
            "negative" -> color(theme, "negative", "#C40006D3")
            else -> color(theme, "secondaryText", "#0000009B")
          },
      )
      text(detail, data.optString("subtitle"), "subtitle", 14f)
      NativeListResolvedText.resolve(
          context,
          data.optJSONObject("badge")?.optString("text") ?: "",
          null,
          12f,
          "medium",
          color(theme, "accent", "#0D8200FC"),
          0,
          1,
          sourceScale,
        )
        .bind(badge)
      // Legacy: the badge follows the weighted title without an extra margin.
      (badge.layoutParams as LayoutParams).marginStart = 0
      for (view in listOf(label, trend, detail)) (view.layoutParams as LayoutParams).topMargin =
        if (style.has("lineGap")) stylePx(style.optDouble("lineGap")) else 0
      (column.parent as? android.view.ViewGroup)?.removeView(column)
      addView(column, weightedWidth())
    }
    for (index in metricImages.indices) if (index !in usedImages) metricImages[index].recycle()
    style
      .optJSONObject("container")
      ?.optString("contentVerticalAlignment")
      ?.takeIf { it.isNotEmpty() }
      ?.let {
        gravity =
          Gravity.START or
            when (it) {
              "top" -> Gravity.TOP
              "bottom" -> Gravity.BOTTOM
              else -> Gravity.CENTER_VERTICAL
            }
      }
  }

  override fun recycleContent() {
    visual.recycle()
    metricImages.forEach { it.recycle() }
    removeAllViews()
  }

  override fun disposeContent() {
    visual.dispose()
    metricImages.forEach { it.dispose() }
    removeAllViews()
  }

  private fun bindCompositeMetricCard(item: NativeListItem, theme: JSONObject?, variant: String) {
    val metrics = item.json.optJSONArray("metrics") ?: JSONArray()
    addView(
      metricText(
          value = item.json.optString("title").uppercase(),
          size = 11f,
          lineHeight = 14,
          typeface = NativeListFonts.regular(context),
          textColor = color(theme, "disabledText", "#00000072"),
          letterSpacingDp = 1.2f,
        )
        .also { compositeMetricTitle = it },
      weightedWidth(),
    )
    if (variant == "activity") {
      addView(activityHeroMetrics(metrics, theme), weightedWidth().apply { topMargin = dp(14) })
      addView(
        View(context).apply { setBackgroundColor(color(theme, "separator", "#0000001F")) },
        LayoutParams(LayoutParams.MATCH_PARENT, 1).apply {
          topMargin = dp(14)
          bottomMargin = dp(14)
        },
      )
      addView(activityCompactMetrics(metrics, theme), weightedWidth())
    } else {
      val winRateRow =
        LinearLayout(context).apply {
          orientation = HORIZONTAL
          gravity = Gravity.BOTTOM
          if (metrics.length() > 0) {
            addView(
              makeMetricColumn(
                metric = metrics.getJSONObject(0),
                visualSlot = 0,
                theme = theme,
                alignment = Gravity.START,
                valueSize = 18f,
                valueLineHeight = 24,
                valueTypeface = NativeListFonts.semibold(context),
              ),
              LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f),
            )
          }
          if (metrics.length() > 1) {
            addView(
              makeMetricColumn(
                metric = metrics.getJSONObject(1),
                visualSlot = 1,
                theme = theme,
                alignment = Gravity.END,
                valueSize = 14f,
                valueLineHeight = 20,
                valueTypeface = NativeListFonts.semibold(context),
              ),
              LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f),
            )
          }
        }
      addView(winRateRow, weightedWidth().apply { topMargin = dp(14) })
      val progressValue = item.json.optDouble("progress", 0.0).coerceIn(0.0, 1.0)
      val progress =
        LinearLayout(context).apply {
          orientation = HORIZONTAL
          clipToOutline = true
          outlineProvider =
            object : ViewOutlineProvider() {
              override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, dp(2).toFloat())
              }
            }
          if (progressValue > 0.0) {
            addView(
              View(context).apply { setBackgroundColor(parseNativeListColor("#22AB15")) },
              LayoutParams(0, LayoutParams.MATCH_PARENT, progressValue.toFloat()),
            )
          }
          if (progressValue < 1.0) {
            addView(
              View(context).apply { setBackgroundColor(parseNativeListColor("#E5484D")) },
              LayoutParams(0, LayoutParams.MATCH_PARENT, (1.0 - progressValue).toFloat()),
            )
          }
        }
      addView(progress, LayoutParams(LayoutParams.MATCH_PARENT, dp(4)).apply { topMargin = dp(8) })
      addView(
        performanceAverageMetrics(metrics, theme),
        weightedWidth().apply { topMargin = dp(14) },
      )
    }
  }

  private fun activityHeroMetrics(metrics: JSONArray, theme: JSONObject?) =
    LinearLayout(context).apply {
      orientation = HORIZONTAL
      gravity = Gravity.BOTTOM
      if (metrics.length() > 0) {
        addView(
          makeMetricColumn(
            metric = metrics.getJSONObject(0),
            visualSlot = 0,
            theme = theme,
            alignment = Gravity.START,
            valueSize = 16f,
            valueLineHeight = 24,
            valueTypeface = NativeListFonts.semibold(context),
          ),
          LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f),
        )
      }
      if (metrics.length() > 1) {
        addView(
          makeMetricColumn(
            metric = metrics.getJSONObject(1),
            visualSlot = 1,
            theme = theme,
            alignment = Gravity.END,
            valueSize = 14f,
            valueLineHeight = 20,
            valueTypeface = NativeListFonts.semibold(context),
          ),
          LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f),
        )
      }
    }

  private fun activityCompactMetrics(metrics: JSONArray, theme: JSONObject?) =
    LinearLayout(context).apply {
      orientation = HORIZONTAL
      gravity = Gravity.CENTER_VERTICAL
      for (index in 2 until metrics.length()) {
        val alignment =
          when (index) {
            2 -> Gravity.START
            metrics.length() - 1 -> Gravity.END
            else -> Gravity.CENTER_HORIZONTAL
          }
        addView(
          makeMetricColumn(
            metric = metrics.getJSONObject(index),
            visualSlot = index,
            theme = theme,
            alignment = alignment,
            valueSize = 14f,
            valueLineHeight = 20,
            valueTypeface = NativeListFonts.medium(context),
          ),
          LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f),
        )
      }
    }

  private fun performanceAverageMetrics(metrics: JSONArray, theme: JSONObject?) =
    LinearLayout(context).apply {
      orientation = HORIZONTAL
      val indices =
        listOf(2, metrics.length() - 1).distinct().filter { it in 0 until metrics.length() }
      indices.forEachIndexed { position, index ->
        val metric = metrics.getJSONObject(index)
        val card =
          makeMetricColumn(
              metric = metric,
              visualSlot = index,
              theme = theme,
              alignment = Gravity.START,
              valueSize = 14f,
              valueLineHeight = 20,
              valueTypeface = NativeListFonts.medium(context),
            )
            .apply {
              setPadding(dp(10), dp(10), dp(10), dp(10))
              background = roundedFill(color(theme, "strongBackground", "#0000000F"), 8f)
            }
        addView(
          card,
          LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply {
            if (position > 0) marginStart = dp(8)
          },
        )
      }
    }

  private fun makeMetricColumn(
    metric: JSONObject,
    visualSlot: Int,
    theme: JSONObject?,
    alignment: Int,
    valueSize: Float,
    valueLineHeight: Int,
    valueTypeface: Typeface,
  ) =
    LinearLayout(context).apply {
      orientation = VERTICAL
      gravity = alignment
      addView(
        metricText(
          value = metric.optString("label"),
          size = 11f,
          lineHeight = 14,
          typeface = NativeListFonts.regular(context),
          textColor = color(theme, "disabledText", "#00000072"),
          gravity = alignment,
        )
      )
      val valueText =
        metricText(
          value = metric.optString("value"),
          size = valueSize,
          lineHeight = valueLineHeight,
          typeface = valueTypeface,
          textColor = dataTextColor(metric.optString("tone"), theme),
          gravity = alignment,
        )
      val valueVisual = metric.optJSONObject("visual")
      val valueView =
        if (valueVisual != null && visualSlot in metricImages.indices) {
          LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            val slot = metricImages[visualSlot]
            usedImages.add(visualSlot)
            val image = slot.view
            (image.parent as? android.view.ViewGroup)?.removeView(image)
            image.background = null
            image.visibility = VISIBLE
            image.outlineProvider = circleOutlineProvider
            image.clipToOutline = true
            valueVisual.optString("backgroundColor").takeIf(String::isNotEmpty)?.let {
              backgroundColor ->
              image.background = roundedFill(safeColor(backgroundColor, Color.WHITE), 8f)
            }
            visualSources(valueVisual).firstOrNull()?.let { (source, variant) ->
              slot.bind(source, rowKey + ":" + visualSlot + ":" + metric.optString("key"), variant)
            } ?: slot.recycle()
            addView(image, LayoutParams(dp(16), dp(16)).apply { marginEnd = dp(6) })
            addView(valueText, wrap())
          }
        } else {
          valueText
        }
      addView(
        valueView,
        LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
          topMargin = dp(2)
        },
      )
    }

  private fun metricText(
    value: String,
    size: Float,
    lineHeight: Int,
    typeface: Typeface,
    textColor: Int,
    letterSpacingDp: Float = 0f,
    gravity: Int = Gravity.START,
  ) =
    NativeListTextView(context).apply {
      includeFontPadding = false
      text = value
      textSize = sp(size)
      this.typeface = typeface
      setTextColor(textColor)
      this.gravity = gravity
      maxLines = 1
      ellipsize = TextUtils.TruncateAt.END
      fontFeatureSettings = "tnum"
      TextViewCompat.setLineHeight(this, dp(lineHeight))
      if (letterSpacingDp > 0f) {
        letterSpacing = letterSpacingDp / sp(size)
      }
    }

  private fun weightedWidth() = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
}
