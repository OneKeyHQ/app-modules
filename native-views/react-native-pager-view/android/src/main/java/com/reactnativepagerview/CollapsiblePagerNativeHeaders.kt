package com.reactnativepagerview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal data class CollapsiblePagerNativeHeaderItem(
  val key: String,
  val title: String,
  val accessibilityLabel: String,
  val testID: String?,
)

private fun parseHeaderItems(value: String?): List<CollapsiblePagerNativeHeaderItem> {
  if (value.isNullOrEmpty()) return emptyList()
  return runCatching {
    val array = JSONArray(value)
    List(array.length()) { index ->
      val item = array.optJSONObject(index) ?: JSONObject()
      val title = item.optString("title")
      CollapsiblePagerNativeHeaderItem(
        key = item.optString("key"),
        title = title,
        accessibilityLabel = item.optString("accessibilityLabel", title),
        testID = item.optString("testID").takeIf(String::isNotEmpty),
      )
    }
  }.getOrDefault(emptyList())
}

private class CollapsiblePagerHorizontalItemsView(
  context: Context,
  private val showsProgressIndicator: Boolean,
) : HorizontalScrollView(context) {
  private val density = resources.displayMetrics.density
  private val content = FrameLayout(context)
  private val indicator = View(context)
  private val buttons = ArrayList<TextView>()
  private val itemFrames = ArrayList<IntArray>()
  private var items: List<CollapsiblePagerNativeHeaderItem> = emptyList()
  private var selectedKey = ""
  private var rowHeightPx = dp(if (showsProgressIndicator) 44.0 else 42.0)
  private var contentPaddingPx = dp(20.0)
  private var itemSpacingPx = dp(8.0)
  private var fontSize = if (showsProgressIndicator) 16.0 else 14.0
  private var fontFamily: String? = null
  private var activeTextColor = Color.BLACK
  private var inactiveTextColor = Color.GRAY
  private var selectedBackgroundColor = Color.TRANSPARENT
  private var indicatorColor = Color.BLACK
  private var indicatorHeightPx = dp(2.0)
  private var indicatorBottomPx = 0
  private var progress = 0f
  private var centerSelectionWhenIdle = true
  private var isTouchDragging = false

  var onItemPress: ((Int, String) -> Unit)? = null

  init {
    isHorizontalScrollBarEnabled = false
    isVerticalScrollBarEnabled = false
    isFillViewport = true
    overScrollMode = OVER_SCROLL_NEVER
    clipToPadding = false
    content.addView(indicator)
    addView(
      content,
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT),
    )
  }

  private fun dp(value: Double): Int = (value * density).roundToInt()

  fun updateItemsJSON(value: String?) {
    val next = parseHeaderItems(value)
    if (items == next) return
    items = next
    buttons.forEach(content::removeView)
    buttons.clear()
    itemFrames.clear()
    items.forEachIndexed { index, item ->
      val button = TextView(context).apply {
        text = item.title
        contentDescription = item.accessibilityLabel
        setTag(com.facebook.react.R.id.react_test_id, item.testID)
        gravity = Gravity.CENTER
        isSingleLine = true
        ellipsize = TextUtils.TruncateAt.END
        isClickable = true
        isFocusable = true
        setPadding(dp(8.0), 0, dp(8.0), 0)
        setOnClickListener { onItemPress?.invoke(index, item.key) }
      }
      buttons.add(button)
      content.addView(button)
    }
    visibility = if (items.isEmpty()) GONE else VISIBLE
    centerSelectionWhenIdle = true
    requestLayout()
  }

  fun updateSelectedKey(value: String) {
    if (selectedKey == value) return
    selectedKey = value
    centerSelectionWhenIdle = true
    updatePresentation()
  }

  fun updateStyle(
    rowHeight: Double,
    contentPadding: Double,
    itemSpacing: Double,
    fontSize: Double,
    fontFamily: String?,
    indicatorHeight: Double = 0.0,
    indicatorBottom: Double = 0.0,
  ) {
    rowHeightPx = max(1, dp(rowHeight))
    contentPaddingPx = max(0, dp(contentPadding))
    itemSpacingPx = max(0, dp(itemSpacing))
    this.fontSize = max(1.0, fontSize)
    this.fontFamily = fontFamily
    if (showsProgressIndicator) {
      indicatorHeightPx = max(0, dp(indicatorHeight))
      indicatorBottomPx = max(0, dp(indicatorBottom))
    }
    applyTypeface()
    requestLayout()
  }

  fun updateColors(
    backgroundColor: Int,
    activeTextColor: Int,
    inactiveTextColor: Int,
    selectedBackgroundColor: Int,
    indicatorColor: Int,
  ) {
    setBackgroundColor(backgroundColor)
    content.setBackgroundColor(backgroundColor)
    this.activeTextColor = activeTextColor
    this.inactiveTextColor = inactiveTextColor
    this.selectedBackgroundColor = selectedBackgroundColor
    this.indicatorColor = indicatorColor
    indicator.setBackgroundColor(indicatorColor)
    updatePresentation()
  }

  fun setProgress(value: Float) {
    if (!showsProgressIndicator || items.isEmpty()) return
    val next = value.coerceIn(0f, (items.size - 1).toFloat())
    if (kotlin.math.abs(progress - next) >= 0.001f) centerSelectionWhenIdle = true
    progress = next
    updatePresentation()
  }

  private fun applyTypeface() {
    val typeface = Typeface.create(fontFamily ?: "sans-serif-medium", Typeface.NORMAL)
    buttons.forEach { button ->
      button.typeface = typeface
      button.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
    }
  }

  private fun selectedBackground(selected: Boolean) = GradientDrawable().apply {
    shape = GradientDrawable.RECTANGLE
    setColor(if (selected) selectedBackgroundColor else Color.TRANSPARENT)
    cornerRadius = dp(10.0).toFloat()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val height = when (MeasureSpec.getMode(heightMeasureSpec)) {
      MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
      else -> rowHeightPx
    }
    applyTypeface()
    itemFrames.clear()
    var x = contentPaddingPx
    buttons.forEachIndexed { index, button ->
      val textWidth = ceil(button.paint.measureText(button.text.toString()).toDouble()).toInt()
      val buttonWidth = max(dp(44.0), textWidth + dp(if (showsProgressIndicator) 16.0 else 20.0))
      val indicatorWidth = textWidth
      val indicatorX = x + (buttonWidth - indicatorWidth) / 2
      itemFrames.add(intArrayOf(x, buttonWidth, indicatorX, indicatorWidth))
      val itemHeight = if (showsProgressIndicator) height else min(dp(32.0), height)
      val top = if (showsProgressIndicator) 0 else max(0, (height - itemHeight) / 2)
      button.layoutParams = FrameLayout.LayoutParams(buttonWidth, itemHeight).apply {
        leftMargin = x
        topMargin = top
      }
      x += buttonWidth
      if (index < buttons.lastIndex) x += itemSpacingPx
    }
    val contentWidth = max(width, x + contentPaddingPx)
    content.layoutParams = LayoutParams(contentWidth, height)
    content.measure(
      MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
    )
    setMeasuredDimension(width, height)
    updatePresentation()
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    updatePresentation()
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> isTouchDragging = true
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        isTouchDragging = false
        post(::updatePresentation)
      }
    }
    return super.onTouchEvent(event)
  }

  private fun updatePresentation() {
    if (buttons.isEmpty() || itemFrames.size != buttons.size) return
    val selectedIndex: Int
    if (showsProgressIndicator) {
      val clamped = progress.coerceIn(0f, buttons.lastIndex.toFloat())
      val lower = floor(clamped).toInt()
      val upper = min(lower + 1, buttons.lastIndex)
      val fraction = clamped - lower
      val from = itemFrames[lower]
      val to = itemFrames[upper]
      val indicatorX = (from[2] + (to[2] - from[2]) * fraction).roundToInt()
      val indicatorWidth = (from[3] + (to[3] - from[3]) * fraction).roundToInt()
      indicator.layoutParams = FrameLayout.LayoutParams(indicatorWidth, indicatorHeightPx).apply {
        leftMargin = indicatorX
        topMargin = max(0, measuredHeight - indicatorBottomPx - indicatorHeightPx)
      }
      indicator.visibility = if (indicatorHeightPx > 0) VISIBLE else GONE
      indicator.background = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(indicatorColor)
        cornerRadius = indicatorHeightPx / 2f
      }
      selectedIndex = clamped.roundToInt()
      buttons.forEachIndexed { index, button ->
        val emphasis = 1f - min(1f, kotlin.math.abs(clamped - index))
        button.setTextColor(ColorUtils.blendARGB(inactiveTextColor, activeTextColor, emphasis))
        button.background = null
        button.isSelected = index == selectedIndex
      }
    } else {
      selectedIndex = items.indexOfFirst { item -> item.key == selectedKey }
      indicator.visibility = GONE
      buttons.forEachIndexed { index, button ->
        val selected = index == selectedIndex
        button.setTextColor(if (selected) activeTextColor else inactiveTextColor)
        button.background = selectedBackground(selected)
        button.isSelected = selected
      }
    }

    if (centerSelectionWhenIdle && !isTouchDragging && width > 0 && selectedIndex in itemFrames.indices) {
      val frame = itemFrames[selectedIndex]
      val desired = (frame[0] + frame[1] / 2 - width / 2)
        .coerceIn(0, max(0, content.measuredWidth - width))
      scrollTo(desired, 0)
      centerSelectionWhenIdle = false
    }
  }
}

internal class CollapsiblePagerNativeTabBarView(context: Context) : FrameLayout(context) {
  private val row = CollapsiblePagerHorizontalItemsView(context, true)
  private var heightPx = 0

  var onItemPress: ((Int, String) -> Unit)?
    get() = row.onItemPress
    set(value) {
      row.onItemPress = value
    }

  init {
    addView(row, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    visibility = GONE
  }

  fun updateItemsJSON(value: String?) {
    row.updateItemsJSON(value)
    visibility = row.visibility
  }

  fun updateStyle(
    height: Double,
    contentPadding: Double,
    itemSpacing: Double,
    fontSize: Double,
    fontFamily: String?,
    indicatorHeight: Double,
    indicatorBottom: Double,
  ) {
    heightPx = max(1, (height * resources.displayMetrics.density).roundToInt())
    row.updateStyle(
      height,
      contentPadding,
      itemSpacing,
      fontSize,
      fontFamily,
      indicatorHeight,
      indicatorBottom,
    )
    requestLayout()
  }

  fun updateColors(
    backgroundColor: Int,
    activeTextColor: Int,
    inactiveTextColor: Int,
    indicatorColor: Int,
  ) = row.updateColors(
    backgroundColor,
    activeTextColor,
    inactiveTextColor,
    Color.TRANSPARENT,
    indicatorColor,
  )

  fun setProgress(progress: Float) = row.setProgress(progress)

  fun preferredHeightPx(): Int = heightPx
}

internal class CollapsiblePagerNativeSubHeaderView(context: Context) : ViewGroup(context) {
  private val density = resources.displayMetrics.density
  private val tabs = CollapsiblePagerHorizontalItemsView(context, false)
  private val columns = FrameLayout(context)
  private val leading = TextView(context)
  private val middle = TextView(context)
  private val trailing = TextView(context)
  private var configuredHeightPx = dp(74.0)
  private var tabsHeightPx = dp(42.0)
  private var contentPaddingPx = dp(20.0)
  private var itemSpacing = 8.0
  private var itemFontSize = 14.0
  private var columnFontSize = 12.0
  private var trailingColumnWidthPx = dp(80.0)
  private var columnGapPx = dp(8.0)
  private var backgroundColorValue = Color.TRANSPARENT
  private var activeTextColor = Color.BLACK
  private var inactiveTextColor = Color.GRAY
  private var selectedBackgroundColor = Color.TRANSPARENT
  private var fontFamily: String? = null

  var onItemPress: ((Int, String) -> Unit)?
    get() = tabs.onItemPress
    set(value) {
      tabs.onItemPress = value
    }

  init {
    addView(tabs)
    addView(columns)
    columns.addView(leading)
    columns.addView(middle)
    columns.addView(trailing)
    listOf(leading, middle, trailing).forEach { label ->
      label.gravity = Gravity.CENTER_VERTICAL
      label.isSingleLine = true
      label.ellipsize = TextUtils.TruncateAt.END
    }
    visibility = GONE
  }

  private fun dp(value: Double): Int = (value * density).roundToInt()

  fun updateConfigJSON(value: String?) {
    val config = runCatching { JSONObject(value ?: "{}") }.getOrDefault(JSONObject())
    val style = config.optJSONObject("style") ?: JSONObject()
    val columnsConfig = config.optJSONObject("columns") ?: JSONObject()
    val itemArray = config.optJSONArray("items") ?: JSONArray()
    tabs.updateItemsJSON(itemArray.toString())
    tabs.updateSelectedKey(config.optString("selectedKey"))
    configuredHeightPx = max(1, dp(style.optDouble("height", 74.0)))
    tabsHeightPx = max(0, dp(style.optDouble("tabsHeight", 42.0)))
    contentPaddingPx = max(0, dp(style.optDouble("contentPaddingHorizontal", 20.0)))
    itemSpacing = max(0.0, style.optDouble("itemSpacing", 8.0))
    itemFontSize = max(1.0, style.optDouble("fontSize", 14.0))
    columnFontSize = max(1.0, style.optDouble("columnFontSize", 12.0))
    trailingColumnWidthPx = max(0, dp(style.optDouble("trailingColumnWidth", 80.0)))
    columnGapPx = max(0, dp(style.optDouble("columnGap", 8.0)))
    leading.text = columnsConfig.optString("leading")
    middle.text = columnsConfig.optString("middle")
    trailing.text = columnsConfig.optString("trailing")
    visibility = if (itemArray.length() == 0) GONE else VISIBLE
    applyStyle()
    requestLayout()
  }

  fun updateColors(
    backgroundColor: Int,
    activeTextColor: Int,
    inactiveTextColor: Int,
    selectedBackgroundColor: Int,
    fontFamily: String?,
  ) {
    backgroundColorValue = backgroundColor
    this.activeTextColor = activeTextColor
    this.inactiveTextColor = inactiveTextColor
    this.selectedBackgroundColor = selectedBackgroundColor
    this.fontFamily = fontFamily
    applyStyle()
  }

  private fun applyStyle() {
    setBackgroundColor(backgroundColorValue)
    columns.setBackgroundColor(backgroundColorValue)
    tabs.updateStyle(
      tabsHeightPx / density.toDouble(),
      contentPaddingPx / density.toDouble(),
      itemSpacing,
      itemFontSize,
      fontFamily,
    )
    tabs.updateColors(
      backgroundColorValue,
      activeTextColor,
      inactiveTextColor,
      selectedBackgroundColor,
      activeTextColor,
    )
    val typeface = Typeface.create(fontFamily ?: "sans-serif-medium", Typeface.NORMAL)
    listOf(leading, middle, trailing).forEach { label ->
      label.typeface = typeface
      label.setTextSize(TypedValue.COMPLEX_UNIT_SP, columnFontSize.toFloat())
      label.setTextColor(inactiveTextColor)
    }
  }

  fun preferredHeightPx(): Int = configuredHeightPx

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val height = MeasureSpec.getSize(heightMeasureSpec)
    val actualTabsHeight = min(height, tabsHeightPx)
    tabs.measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(actualTabsHeight, MeasureSpec.EXACTLY),
    )
    val columnsHeight = max(0, height - actualTabsHeight)
    columns.measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(columnsHeight, MeasureSpec.EXACTLY),
    )
    setMeasuredDimension(width, height)
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    val width = right - left
    val height = bottom - top
    val actualTabsHeight = min(height, tabsHeightPx)
    tabs.layout(0, 0, width, actualTabsHeight)
    columns.layout(0, actualTabsHeight, width, height)
    val columnsHeight = height - actualTabsHeight
    val half = width / 2
    val isRtl = layoutDirection == View.LAYOUT_DIRECTION_RTL
    if (!isRtl) {
      leading.gravity = Gravity.START or Gravity.CENTER_VERTICAL
      middle.gravity = Gravity.END or Gravity.CENTER_VERTICAL
      trailing.gravity = Gravity.END or Gravity.CENTER_VERTICAL
      val trailingX = width - contentPaddingPx - trailingColumnWidthPx
      leading.layout(contentPaddingPx, 0, half, columnsHeight)
      middle.layout(half, 0, max(half, trailingX - columnGapPx), columnsHeight)
      trailing.layout(trailingX, 0, width - contentPaddingPx, columnsHeight)
    } else {
      leading.gravity = Gravity.END or Gravity.CENTER_VERTICAL
      middle.gravity = Gravity.START or Gravity.CENTER_VERTICAL
      trailing.gravity = Gravity.START or Gravity.CENTER_VERTICAL
      leading.layout(half, 0, width - contentPaddingPx, columnsHeight)
      middle.layout(
        contentPaddingPx + trailingColumnWidthPx + columnGapPx,
        0,
        half,
        columnsHeight,
      )
      trailing.layout(contentPaddingPx, 0, contentPaddingPx + trailingColumnWidthPx, columnsHeight)
    }
  }
}
