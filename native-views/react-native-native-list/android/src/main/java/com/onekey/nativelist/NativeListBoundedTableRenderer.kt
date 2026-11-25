package com.margelo.nitro.nativelist

import android.graphics.Rect
import android.view.Gravity
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import android.widget.LinearLayout
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONObject
import kotlin.math.roundToInt

internal class NativeListBoundedTableView(context: ThemedReactContext, fonts: NativeListFonts) :
  NativeListMarketGestureRowView(context, fonts) {
  internal class Cell(context: ThemedReactContext, fonts: NativeListFonts) : LinearLayout(context) {
    val visual = NativeListLeadingVisual(context, fonts)
    val labels = NativeListTableColumnView(context, fonts, wrapSecondary = true)
    val content = LinearLayout(context).apply {
      orientation = HORIZONTAL
      gravity = Gravity.CENTER_VERTICAL
      addView(visual)
      addView(labels, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    }
    private val actions = LinearLayout(context)
    private class IconDelegate(bounds: Rect, val icon: View) : TouchDelegate(bounds, icon)
    private fun removeIcon(index: Int) {
      val icon = actions.getChildAt(index)
      if ((touchDelegate as? IconDelegate)?.icon === icon) touchDelegate = null
      icon.cancelPendingInputEvents()
      icon.setOnClickListener(null)
      actions.removeViewAt(index)
    }
    fun clearActions() {
      while (actions.childCount > 0) removeIcon(actions.childCount - 1)
    }
    private var inlineWidth = 0
    fun bindActions(column: JSONObject, disabled: Boolean, color: Int, onAction: (String, View) -> Unit) {
      val descriptors = column.optJSONArray("actions")
      val count = minOf(2, descriptors?.length() ?: 0)
      val below = column.optString("actionPlacement") == "below"
      val size = NativeListScale.dp(resources, 16)
      val gap = NativeListScale.dp(resources, 6)
      inlineWidth = if (below) 0 else count * (size + gap)
      while (actions.childCount > count) removeIcon(actions.childCount - 1)
      val parent = if (below) labels else content
      if (actions.parent !== parent || count == 0) {
        (actions.parent as? LinearLayout)?.removeView(actions)
        if (count > 0) parent.addView(actions, LayoutParams(LayoutParams.WRAP_CONTENT, size).apply {
          if (below) topMargin = gap else marginStart = gap
        })
      }
      for (i in 0 until count) {
        val descriptor = descriptors!!.getJSONObject(i)
        val key = descriptor.getString("actionKey")
        val previous = actions.getChildAt(i)
        if (previous != null && previous.tag != key) removeIcon(i)
        val icon = previous?.takeIf { it.tag == key } as? OneKeyIconView ?: OneKeyIconView(context).also {
          it.tag = key
          actions.addView(it, i, LayoutParams(size, size).apply { if (i > 0) marginStart = gap })
        }
        icon.iconName = descriptor.getString("name")
        icon.glyphSizeDp = 16
        icon.tintColor = color
        icon.contentDescription = descriptor.getString("accessibilityLabel")
        icon.isEnabled = !disabled
        icon.isFocusable = true
        icon.setOnClickListener { if (!disabled) onAction(key, icon) }
      }
    }
    // Coordinates are local to the column, so the hit area can extend past the icon dock.
    fun actionAt(x: Int, y: Int, bounds: Rect): View? {
      val slop = (8 * resources.displayMetrics.density).roundToInt()
      var closest: View? = null
      var distance = Int.MAX_VALUE
      val candidate = Rect()
      for (i in 0 until actions.childCount) {
        val icon = actions.getChildAt(i)
        if (!icon.isEnabled) continue
        icon.getDrawingRect(candidate)
        offsetDescendantRectToMyCoords(icon, candidate)
        val dx = kotlin.math.abs(x - candidate.centerX())
        candidate.inset(-slop, -slop)
        if (candidate.contains(x, y) && dx < distance) {
          closest = icon
          distance = dx
          bounds.set(candidate)
        }
      }
      return closest
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
      if (event.actionMasked == MotionEvent.ACTION_DOWN && actions.childCount > 0) {
        val bounds = Rect()
        touchDelegate = actionAt(event.x.toInt(), event.y.toInt(), bounds)?.let { IconDelegate(bounds, it) }
      }
      val handled = super.onTouchEvent(event)
      if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL)
        touchDelegate = null
      return handled
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
      labels.maxTableWidth = if (inlineWidth > 0 && MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED) {
        val lp = visual.layoutParams as? MarginLayoutParams
        val leading = if (visual.visibility == GONE || lp == null) 0 else lp.width + lp.marginEnd
        (MeasureSpec.getSize(widthMeasureSpec) - leading - inlineWidth).coerceAtLeast(0)
      } else null
      super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
    init {
      addView(content, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    }
  }

  override fun defaultHeight(item: NativeListItem, layout: String) = 56

  override fun bindContent(item: NativeListItem, theme: JSONObject?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String) {
    bindMarketGesture(item)
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
      val defaultIconTint = 0x72000000
      val iconTint = if ((column.optJSONArray("actions")?.length() ?: 0) > 0 && theme?.has("iconSubdued") == true)
        color(theme.optString("iconSubdued"), defaultIconTint) else defaultIconTint
      cell.bindActions(column, item.json.optBoolean("disabled"), iconTint) { key, view ->
        emitAction(key, view, "leadingAction", index)
      }
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
    fun spokenLine(column: JSONObject, field: String): String {
      val segments = column.optJSONArray("${field}Segments") ?: return column.optString(field)
      val indices = 0 until segments.length()
      if (indices.isEmpty() || indices.any {
          val run = segments.getJSONObject(it)
          run.optString("style") == "subscript" && run.optString("text").isNotEmpty()
        }) {
        return column.optString(field)
      }
      return indices.joinToString("") { segments.getJSONObject(it).optString("text") }
    }
    val columns = item.json.getJSONArray("tableColumns")
    return (0 until columns.length()).flatMap { index ->
      val cell = columns.getJSONObject(index)
      listOf(spokenLine(cell, "text"), spokenLine(cell, "secondaryText"))
    }.filter { it.isNotEmpty() }.joinToString(", ")
  }

  override fun recycleContent() {
    resetMarketGesture()
    for (index in 0 until childCount) {
      val cell = getChildAt(index) as Cell
      cell.visual.recycle()
      cell.labels.reset()
      cell.clearActions()
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
