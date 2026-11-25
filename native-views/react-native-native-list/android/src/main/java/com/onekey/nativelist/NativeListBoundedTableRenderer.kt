package com.margelo.nitro.nativelist

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONObject

internal class NativeListBoundedTableView(context: ThemedReactContext, fonts: NativeListFonts) :
  NativeListMarketGestureRowView(context, fonts) {
  private class Cell(context: ThemedReactContext, fonts: NativeListFonts) : LinearLayout(context) {
    val visual = NativeListLeadingVisual(context, fonts)
    val labels = NativeListTableColumnView(context, fonts, wrapSecondary = true)
    init {
      orientation = HORIZONTAL
      addView(visual)
      addView(labels, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    }
  }

  override fun defaultHeight(item: NativeListItem, layout: String) = 56

  override fun bindContent(item: NativeListItem, theme: JSONObject?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String) {
    resetMarketGesture()
    val style = JSONObject(item.json.optJSONObject("style")?.toString() ?: "{}")
    if (item.type == "market") {
      style.put("columns", style.optJSONObject("price"))
      style.put("columnSecondary", style.optJSONObject("subtitle"))
    }
    if (!style.has("lineGap")) style.put("lineGap", 0)
    val hp = stylePx(style.optDouble("horizontalPadding", 0.0))
    val vp = stylePx(style.optDouble("verticalPadding", 0.0))
    setPadding(hp, vp, hp, vp)
    orientation = HORIZONTAL
    gravity = Gravity.CENTER_VERTICAL or Gravity.START
    val columns = item.json.getJSONArray("tableColumns")
    val count = minOf(columns.length(), if (item.type == "market") 7 else 6)
    while (childCount > count) {
      (getChildAt(childCount - 1) as Cell).visual.dispose()
      removeViewAt(childCount - 1)
    }
    val imageStyle = style.optJSONObject("image") ?: JSONObject()
    for (index in 0 until count) {
      val cell = if (index < childCount) getChildAt(index) as Cell
        else Cell(reactContext, fonts).also { addView(it) }
      val column = columns.getJSONObject(index)
      val geometry = if (column.has("width"))
        LayoutParams(stylePx(column.getDouble("width")), LayoutParams.MATCH_PARENT)
        else LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
      geometry.marginStart = if (index == 0) 0 else stylePx(style.optDouble("columnGap", 8.0))
      cell.layoutParams = geometry
      val vertical = when (style.optJSONObject("container")?.optString("contentVerticalAlignment")) {
        "top" -> Gravity.TOP
        "bottom" -> Gravity.BOTTOM
        else -> Gravity.CENTER_VERTICAL
      }
      cell.gravity = vertical or when (column.optString("alignment")) {
        "end" -> Gravity.END
        "center" -> Gravity.CENTER_HORIZONTAL
        else -> Gravity.START
      }
      val leading = column.optJSONObject("leading")
      if (leading != null) {
        cell.visual.layoutParams = LayoutParams(stylePx(imageStyle.optDouble("width", 24.0)),
          stylePx(imageStyle.optDouble("height", 24.0))).apply {
          marginEnd = stylePx(style.optDouble("leadingGap", 4.0))
        }
        cell.visual.bind(leading, imageStyle, item.key, theme, false, sourceScale)
        cell.visual.visibility = View.VISIBLE
      } else {
        cell.visual.recycle()
        cell.visual.visibility = View.GONE
      }
      cell.labels.bind(column, null, columnColor(column.optString("tone"), theme),
        columnColor(column.optString("secondaryTone", "secondary"), theme),
        parseNativeListColor(theme?.optString("info", "#006DCBF2") ?: "#006DCBF2"),
        14f, style)
      val action = column.optString("actionKey")
      cell.setOnClickListener(null)
      cell.isClickable = action.isNotEmpty() && !item.json.optBoolean("disabled")
      cell.isFocusable = cell.isClickable
      cell.contentDescription = column.optString("accessibilityLabel").takeIf { it.isNotEmpty() }
      if (cell.isClickable) cell.setOnClickListener { emitAction(action, cell, "leadingAction", index) }
    }
  }

  private fun columnColor(tone: String, theme: JSONObject?): Int {
    val (key, fallback) = when (tone) {
      "positive" -> "positive" to "#00713FDE"
      "negative" -> "negative" to "#C40006D3"
      "secondary" -> "secondaryText" to "#00000072"
      else -> "primaryText" to "#000000DF"
    }
    return parseNativeListColor(theme?.optString(key, fallback) ?: fallback)
  }

  override fun accessibilityText(item: NativeListItem): String {
    if (item.json.has("accessibilityLabel")) return item.json.getString("accessibilityLabel")
    val columns = item.json.getJSONArray("tableColumns")
    return (0 until columns.length()).flatMap { index ->
      val cell = columns.getJSONObject(index)
      listOf(cell.optString("text"), cell.optString("secondaryText"))
    }.filter { it.isNotEmpty() }.joinToString(", ")
  }

  override fun recycleContent() {
    resetMarketGesture()
    for (index in 0 until childCount) {
      val cell = getChildAt(index) as Cell
      cell.visual.recycle()
      cell.labels.reset()
      cell.setOnClickListener(null)
      cell.isClickable = false
    }
  }

  override fun disposeContent() {
    resetMarketGesture()
    for (index in 0 until childCount) (getChildAt(index) as Cell).visual.dispose()
    removeAllViews()
  }
}
