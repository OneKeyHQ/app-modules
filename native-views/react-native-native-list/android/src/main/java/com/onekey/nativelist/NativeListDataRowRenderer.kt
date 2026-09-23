package com.margelo.nitro.nativelist

import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.facebook.react.uimanager.ThemedReactContext
import kotlin.math.roundToInt
import org.json.JSONArray
import org.json.JSONObject

internal class NativeListDataRowView(context: ThemedReactContext) :
  NativeListRendererRowView(context) {
  private val visual = NativeListLeadingVisual(context)
  private val favorite = OneKeyIconView(context)
  private val accessories = NativeListAccessoryStack(context)
  private val columns =
    LinearLayout(context).apply {
      orientation = HORIZONTAL
      gravity = Gravity.CENTER_VERTICAL
    }
  private val cells = List(4) { NativeListTableColumnView(context) }

  override fun unselectedBackground(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    itemIndex: Int?,
  ): Int {
    val index = if (item.json.has("index")) item.json.optInt("index") else itemIndex
    return if (layout == "table" && index?.rem(2) == 0)
      parseNativeListColor(theme?.optString("subduedBackground", "#F9F9F9") ?: "#F9F9F9")
    else super.unselectedBackground(item, theme, layout, itemIndex)
  }

  override fun backgroundGroupPosition(
    item: NativeListItem,
    layout: String,
    itemIndex: Int?,
    selected: Boolean,
  ): String {
    val index = if (item.json.has("index")) item.json.optInt("index") else itemIndex
    return if (layout == "table" && index?.rem(2) == 0 && !selected) ""
    else super.backgroundGroupPosition(item, layout, itemIndex, selected)
  }

  override val assetFields = listOf("leading")

  init {
    cells.forEach(columns::addView)
    addView(favorite)
    addView(visual)
    addView(accessories)
    addView(columns, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
    accessories.onAction = { key, view, slot, target ->
      emitAction(key, view, "trailingAccessory", slot, target)
    }
  }

  override fun defaultHeight(item: NativeListItem, layout: String): Int {
    val values = item.json.optJSONArray("columns") ?: JSONArray()
    val secondary =
      (0 until values.length()).any {
        values.getJSONObject(it).optString("secondaryText").isNotEmpty()
      }
    return if (layout == "table") if (secondary) 60 else 52 else if (secondary) 64 else 56
  }

  override fun bindContent(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    val data = item.json
    val style = data.optJSONObject("style") ?: JSONObject()
    val image = style.optJSONObject("image") ?: JSONObject()
    orientation = HORIZONTAL
    gravity =
      when (style.optJSONObject("container")?.optString("contentVerticalAlignment")) {
        "top" -> Gravity.TOP
        "bottom" -> Gravity.BOTTOM
        else -> Gravity.CENTER_VERTICAL
      }
    val hp =
      if (style.has("horizontalPadding")) stylePx(style.optDouble("horizontalPadding"))
      else dp(if (layout == "table") 16 else 12)
    val vp =
      if (style.has("verticalPadding")) stylePx(style.optDouble("verticalPadding")) else dp(8)
    setPadding(hp, vp, hp, vp)
    val active = data.optBoolean("favoriteActive")
    favorite.visibility = if (active || data.optBoolean("favorite")) VISIBLE else GONE
    favorite.iconName = if (active) "StarSolid" else "StarOutline"
    favorite.useSourceScale = sourceScale
    favorite.tintColor =
      parseNativeListColor(
        theme?.optString(
          if (active) "icon" else "iconSubdued",
          if (active) "#0000009B" else "#00000072",
        ) ?: if (active) "#0000009B" else "#00000072"
      )
    favorite.layoutParams =
      LayoutParams(dp(20), dp(20)).apply { marginEnd = dp(if (layout == "table") 8 else 12) }
    val leading = data.optJSONObject("leading")
    visual.visibility = if (leading == null) GONE else VISIBLE
    visual.layoutParams =
      LayoutParams(
          if (image.has("width")) stylePx(image.optDouble("width")) else dp(40),
          if (image.has("height")) stylePx(image.optDouble("height")) else dp(40),
        )
        .apply {
          marginEnd =
            if (style.has("leadingGap")) stylePx(style.optDouble("leadingGap"))
            else dp(if (layout == "table") 10 else 12)
        }
    if (leading != null) visual.bind(leading, image, item.key, theme, false, sourceScale)
    else visual.recycle()
    val descriptors = JSONArray()
    if (data.has("index"))
      descriptors.put(
        JSONObject().put("kind", "value").put("text", data.optInt("index").toString())
      )
    data.optJSONObject("checkbox")?.let(descriptors::put)
    val accessoryStyle = JSONObject().put("value", style.optJSONObject("index"))
    accessories.bind(item, descriptors, theme, accessoryStyle, sourceScale, checkboxState)
    accessories.visibility = if (descriptors.length() == 0) GONE else VISIBLE
    accessories.layoutParams =
      LayoutParams(dp(32), LayoutParams.WRAP_CONTENT).apply { marginEnd = dp(10) }
    val values = data.getJSONArray("columns")
    cells.forEachIndexed { index, cell ->
      cell.visibility = if (index < values.length()) VISIBLE else GONE
      if (index < values.length()) {
        val value = values.getJSONObject(index)
        cell.bind(
          value,
          if (index == 0) data.optJSONArray("badges") else null,
          dataColor(value.optString("tone"), theme),
          dataColor(value.optString("secondaryTone", "secondary"), theme),
          parseNativeListColor(theme?.optString("info", "#006DCBF2") ?: "#006DCBF2"),
          if (layout == "table") 14f else 16f,
          style,
        )
        cell.layoutParams =
          LayoutParams(0, LayoutParams.WRAP_CONTENT, value.optInt("weight", 1).toFloat())
      } else cell.reset()
    }
  }

  private fun dataColor(tone: String, theme: JSONObject?): Int {
    val token =
      when (tone) {
        "positive" -> "positive"
        "negative" -> "negative"
        "secondary" -> "secondaryText"
        "disabled" -> "disabledText"
        "caution" -> "caution"
        else -> "primaryText"
      }
    val fallback =
      when (token) {
        "positive" -> "#00713FDE"
        "negative" -> "#C40006D3"
        "secondaryText" -> "#0000009B"
        "disabledText" -> "#00000072"
        "caution" -> "#AB6400"
        else -> "#000000DF"
      }
    return parseNativeListColor(theme?.optString(token, fallback) ?: fallback)
  }

  override fun bindSelectionContent(
    item: NativeListItem,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    accessories.updateSelection(item, checkboxState)
  }

  override fun recycleContent() {
    visual.recycle()
    accessories.reset()
    cells.forEach { it.reset() }
  }

  override fun disposeContent() {
    visual.dispose()
    accessories.reset()
  }
}

private class NativeListTableColumnView(context: android.content.Context) : LinearLayout(context) {
  private val primaryLine = LinearLayout(context)
  private val primary = NativeListTextView(context)
  private val badges = LinearLayout(context)
  private val secondaryLine = LinearLayout(context)
  private val secondaryLeading = NativeListTextView(context)
  private val secondary = NativeListTextView(context)

  init {
    orientation = VERTICAL
    primaryLine.orientation = HORIZONTAL
    primaryLine.gravity = Gravity.CENTER_VERTICAL
    badges.orientation = HORIZONTAL
    badges.gravity = Gravity.CENTER_VERTICAL
    primary.includeFontPadding = false
    primary.fontFeatureSettings = "tnum"
    primary.maxLines = 1
    primary.ellipsize = TextUtils.TruncateAt.END
    primary.textSize = sp(14f)
    primary.typeface = NativeListFonts.medium(context)
    TextViewCompat.setLineHeight(primary, dp(20))
    secondaryLine.orientation = HORIZONTAL
    secondaryLine.gravity = Gravity.CENTER_VERTICAL
    listOf(secondaryLeading, secondary).forEach { label ->
      label.includeFontPadding = false
      label.fontFeatureSettings = "tnum"
      label.maxLines = 1
      label.ellipsize = TextUtils.TruncateAt.END
      label.textSize = sp(12f)
      label.typeface = NativeListFonts.regular(context)
      TextViewCompat.setLineHeight(label, dp(16))
    }
    secondaryLeading.maxWidth = dp(120)
    secondaryLine.addView(
      secondaryLeading,
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT),
    )
    secondaryLine.addView(
      secondary,
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
        marginStart = dp(4)
      },
    )
    primaryLine.addView(primary, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    primaryLine.addView(
      badges,
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
        marginStart = dp(6)
      },
    )
    addView(primaryLine, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    addView(
      secondaryLine,
      LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) },
    )
  }

  fun reset() {
    primaryLine.layoutParams.width = LayoutParams.WRAP_CONTENT
    primary.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    secondary.layoutParams =
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
        marginStart = dp(4)
      }
    (secondaryLine.layoutParams as MarginLayoutParams).topMargin = dp(4)
    (badges.layoutParams as MarginLayoutParams).marginStart = dp(6)
    primary.text = ""
    secondaryLeading.text = ""
    secondary.text = ""
    secondaryLeading.visibility = GONE
    secondary.visibility = GONE
    secondaryLine.visibility = GONE
    badges.removeAllViews()
    badges.visibility = GONE
  }

  fun bind(
    column: JSONObject,
    rowBadges: JSONArray?,
    primaryColor: Int,
    secondaryColor: Int,
    infoColor: Int,
    primarySize: Float,
    style: JSONObject,
  ) {
    reset()
    gravity =
      when (column.optString("alignment", "start")) {
        "center" -> Gravity.CENTER_HORIZONTAL
        "end" -> Gravity.END
        else -> Gravity.START
      }
    fun bindText(
      view: NativeListTextView,
      value: String,
      slot: String,
      size: Float,
      weight: String,
      color: Int,
      line: Int,
    ) {
      val resolved =
        JSONObject(style.optJSONObject(slot)?.toString() ?: "{}").apply {
          if (!has("alignment")) put("alignment", column.optString("alignment", "start"))
        }
      NativeListResolvedText.resolve(context, value, resolved, size, weight, color, line, 1, false)
        .bind(view)
      if (resolved.optString("truncate") != "clip") view.ellipsize = TextUtils.TruncateAt.END
    }
    bindText(primary, column.optString("text"), "columns", primarySize, "medium", primaryColor, 20)
    bindText(
      secondaryLeading,
      column.optString("secondaryLeadingText"),
      "columnSecondary",
      12f,
      "regular",
      secondaryColor,
      16,
    )
    bindText(
      secondary,
      column.optString("secondaryText"),
      "columnSecondary",
      12f,
      "regular",
      secondaryColor,
      16,
    )
    if (rowBadges != null && rowBadges.length() > 0) {
      badges.visibility = VISIBLE
      for (index in 0 until minOf(2, rowBadges.length())) {
        val badge =
          TextView(context).apply {
            includeFontPadding = false
            fontFeatureSettings = "tnum"
            gravity = Gravity.CENTER
            text = rowBadges.getJSONObject(index).optString("text")
            textSize = sp(10f)
            typeface = NativeListFonts.regular(context)
            setTextColor(infoColor)
            setPadding(dp(6), 0, dp(6), 0)
            background =
              GradientDrawable().apply {
                setColor(parseNativeListColor("#008FF519"))
                cornerRadius = dp(4).toFloat()
              }
          }
        badges.addView(
          badge,
          LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            if (index > 0) marginStart = dp(4)
          },
        )
      }
    }
    val secondaryLeadingText = column.optString("secondaryLeadingText")
    if (secondaryLeadingText.isNotEmpty()) {
      secondaryLeading.visibility = VISIBLE
      secondaryLine.visibility = VISIBLE
    }
    val secondaryText = column.optString("secondaryText")
    if (secondaryText.isNotEmpty()) {
      secondary.visibility = VISIBLE
      secondaryLine.visibility = VISIBLE
    }
    if (style.length() > 0) applyStyle(style)
  }

  private fun applyStyle(style: JSONObject) {
    style.optJSONObject("columns")?.let {
      if (it.has("alignment")) {
        primaryLine.layoutParams.width = LayoutParams.MATCH_PARENT
        primary.layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
      }
    }
    style.optJSONObject("columnSecondary")?.let {
      if (it.has("alignment"))
        secondary.layoutParams =
          LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply { marginStart = dp(4) }
    }
    (secondaryLine.layoutParams as MarginLayoutParams).topMargin =
      style.optDouble("lineGap", 4.0).let { (it * resources.displayMetrics.density).roundToInt() }
    (badges.layoutParams as MarginLayoutParams).marginStart =
      style.optDouble("titleBadgeGap", 6.0).let {
        (it * resources.displayMetrics.density).roundToInt()
      }
  }

  private fun dp(value: Int): Int = NativeListScale.dp(resources, value)

  private fun sp(value: Float): Float = NativeListScale.font(resources, value)
}
