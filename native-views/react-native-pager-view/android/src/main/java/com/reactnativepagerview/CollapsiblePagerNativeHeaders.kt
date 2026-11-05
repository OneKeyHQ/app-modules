package com.reactnativepagerview

import android.content.Context
import android.graphics.Color
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.widget.ImageView
import com.margelo.nitro.onekeyimage.OneKeyNamedSVG
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
import com.facebook.react.common.assets.ReactFontManager
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

private const val DEFAULT_HEADER_FONT_FAMILY = "sans-serif-medium"

// Resolve fonts the way React Native Text does. ReactFontManager reads bundled
// assets/fonts/<family>.ttf and registered custom fonts; Typeface.create only
// knows system families and silently falls back to the default font.
private fun resolveHeaderTypeface(context: Context, fontFamily: String?): Typeface =
  ReactFontManager.getInstance().getTypeface(
    fontFamily?.takeIf(String::isNotEmpty) ?: DEFAULT_HEADER_FONT_FAMILY,
    Typeface.NORMAL,
    context.assets,
  )

private class CollapsiblePagerHorizontalItemsView(
  context: Context,
  private val showsProgressIndicator: Boolean,
) : HorizontalScrollView(context) {
  private val density = resources.displayMetrics.density
  private val content = FrameLayout(context)
  private val track = View(context)
  private val indicator = View(context)
  private val trackBackground = GradientDrawable().apply {
    shape = GradientDrawable.RECTANGLE
  }
  private val indicatorBackground = GradientDrawable().apply {
    shape = GradientDrawable.RECTANGLE
  }
  private val buttons = ArrayList<TextView>()
  private val itemFrames = ArrayList<IntArray>()
  private var items: List<CollapsiblePagerNativeHeaderItem> = emptyList()
  private var selectedKey = ""
  private var rowHeightPx = dp(if (showsProgressIndicator) 44.0 else 42.0)
  private var contentPaddingPx = dp(20.0)
  private var contentPaddingVerticalPx = 0
  private var segmented = false
  private var textVariant = false
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
  private var mappedSource: Int? = null
  private var mappedDestination: Int? = null
  private var mappedFraction = 0f
  private var activeFontFamily: String? = null
  private var controlledSelectedKey: String? = null
  private var validControlledPageMapping = false
  private var centerSelectionWhenIdle = true
  private var isTouchDragging = false

  var onItemPress: ((Int, String) -> Unit)? = null

  init {
    isHorizontalScrollBarEnabled = false
    isVerticalScrollBarEnabled = false
    isFillViewport = true
    overScrollMode = OVER_SCROLL_NEVER
    clipToPadding = false
    track.background = trackBackground
    indicator.background = indicatorBackground
    content.addView(track, FrameLayout.LayoutParams(0, 0))
    content.addView(indicator, FrameLayout.LayoutParams(0, 0))
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
    variant: String = "underline",
    contentPaddingVertical: Double = 0.0,
  ) {
    rowHeightPx = max(1, dp(rowHeight))
    contentPaddingPx = max(0, dp(contentPadding))
    contentPaddingVerticalPx = max(0, dp(contentPaddingVertical))
    val wasSegmented = segmented
    segmented = showsProgressIndicator && variant == "segmented"
    textVariant = showsProgressIndicator && variant == "text"
    itemSpacingPx = max(0, dp(itemSpacing))
    this.fontSize = max(1.0, fontSize)
    this.fontFamily = fontFamily
    if (showsProgressIndicator) {
      indicatorHeightPx = max(0, dp(indicatorHeight))
      indicatorBottomPx = max(0, dp(indicatorBottom))
      indicatorBackground.cornerRadius = if (segmented) dp(14.0).toFloat() else indicatorHeightPx / 2f
    }
    track.visibility = if (segmented) VISIBLE else GONE
    if (segmented && !wasSegmented) scrollTo(0, 0)
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
    setBackgroundColor(if (segmented) Color.TRANSPARENT else backgroundColor)
    content.setBackgroundColor(if (segmented) Color.TRANSPARENT else backgroundColor)
    trackBackground.setColor(backgroundColor)
    this.activeTextColor = activeTextColor
    this.inactiveTextColor = inactiveTextColor
    this.selectedBackgroundColor = selectedBackgroundColor
    this.indicatorColor = indicatorColor
    indicatorBackground.setColor(indicatorColor)
    updatePresentation()
  }

  fun setControlledSelectedKey(value: String?, validPageMapping: Boolean) {
    controlledSelectedKey = value
    validControlledPageMapping = validPageMapping
    updatePresentation()
  }

  fun usesTextVariant(): Boolean = textVariant

  fun setActiveFontFamily(value: String?) { activeFontFamily = value; requestLayout() }

  fun setProgress(value: Float, source: Int? = null, destination: Int? = null, fraction: Float = 0f) {
    mappedSource = source
    mappedDestination = destination
    mappedFraction = fraction
    if (!showsProgressIndicator || items.isEmpty()) return
    val next = value.coerceIn(0f, (items.size - 1).toFloat())
    if (kotlin.math.abs(progress - next) >= 0.001f) centerSelectionWhenIdle = true
    progress = next
    updatePresentation()
  }

  private fun selectedTypeface(): Typeface = activeFontFamily?.takeIf(String::isNotEmpty)?.let { resolveHeaderTypeface(context, it) }
    ?: if (android.os.Build.VERSION.SDK_INT >= 28) Typeface.create(Typeface.create("sans-serif", Typeface.NORMAL), 600, false)
      else Typeface.create("sans-serif", Typeface.BOLD)

  private fun visualFontOwnerIndex(semanticIndex: Int): Int {
    val source = mappedSource
    val destination = mappedDestination
    if (textVariant && controlledSelectedKey != null && validControlledPageMapping && source != null && destination != null) {
      return if (mappedFraction < 0.5f) source else destination
    }
    return semanticIndex
  }

  private fun applyTypeface() {
    val typeface = resolveHeaderTypeface(context, fontFamily)
    val fontOwner = visualFontOwnerIndex(buttons.indexOfFirst { it.isSelected })
    buttons.forEachIndexed { index, button ->
      button.typeface = if (textVariant && index == fontOwner) selectedTypeface() else typeface
      button.setPadding(dp(if (textVariant) 10.0 else 8.0), 0, dp(if (textVariant) 10.0 else 8.0), 0)
      button.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
    }
  }

  private fun selectedBackground(selected: Boolean, height: Int) = GradientDrawable().apply {
    shape = GradientDrawable.RECTANGLE
    setColor(if (selected) selectedBackgroundColor else Color.TRANSPARENT)
    // OneKey patch: match the pill shape of the React category filters.
    // cornerRadius = dp(10.0).toFloat()
    cornerRadius = height / 2f
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val height = when (MeasureSpec.getMode(heightMeasureSpec)) {
      MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
      else -> rowHeightPx
    }
    applyTypeface()
    itemFrames.clear()
    if (segmented) {
      val actualPaddingHorizontal = min(contentPaddingPx, width / 2)
      val actualPaddingVertical = min(contentPaddingVerticalPx, height / 2)
      val trackLeft = actualPaddingHorizontal
      val trackRight = max(trackLeft, width - contentPaddingPx)
      val trackTop = actualPaddingVertical
      val trackBottom = max(trackTop, height - contentPaddingVerticalPx)
      val trackWidth = trackRight - trackLeft
      val trackHeight = trackBottom - trackTop
      trackBackground.cornerRadius = trackHeight / 2f
      indicatorBackground.cornerRadius = max(0, trackHeight - dp(4.0)) / 2f
      track.layoutParams = FrameLayout.LayoutParams(trackWidth, trackHeight).apply {
        gravity = Gravity.TOP or Gravity.LEFT
        leftMargin = trackLeft
        topMargin = trackTop
      }
      buttons.forEachIndexed { index, button ->
        val count = buttons.size
        val first = trackLeft + trackWidth * index / count
        val last = trackLeft + trackWidth * (index + 1) / count
        val start = if (layoutDirection == View.LAYOUT_DIRECTION_RTL) trackRight - (last - trackLeft) else first
        val end = if (layoutDirection == View.LAYOUT_DIRECTION_RTL) trackRight - (first - trackLeft) else last
        val buttonWidth = max(0, end - start)
        val thumbInset = dp(2.0)
        itemFrames.add(intArrayOf(start, buttonWidth, start + thumbInset, max(0, buttonWidth - 2 * thumbInset)))
        button.layoutParams = FrameLayout.LayoutParams(buttonWidth, trackHeight).apply {
          gravity = Gravity.TOP or Gravity.LEFT
          leftMargin = start
          topMargin = trackTop
        }
      }
      content.layoutParams = LayoutParams(width, height)
      content.measure(
        MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
      )
      setMeasuredDimension(width, height)
      return
    }
    track.layoutParams = FrameLayout.LayoutParams(0, 0)
    var x = contentPaddingPx
    buttons.forEachIndexed { index, button ->
      var textWidth = ceil(button.paint.measureText(button.text.toString()).toDouble()).toInt()
      if (textVariant) {
        val original = button.paint.typeface
        button.paint.typeface = resolveHeaderTypeface(context, fontFamily)
        textWidth = ceil(button.paint.measureText(button.text.toString()).toDouble()).toInt()
        button.paint.typeface = selectedTypeface()
        textWidth = max(textWidth, ceil(button.paint.measureText(button.text.toString()).toDouble()).toInt())
        button.paint.typeface = original
      }
      // Reuse the rounded padding: dp(16) can be one pixel less than two dp(8) insets.
      val horizontalPadding = if (showsProgressIndicator) {
        button.compoundPaddingLeft + button.compoundPaddingRight
      } else {
        dp(20.0)
      }
      val buttonWidth = max(dp(44.0), textWidth + horizontalPadding)
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

  override fun onInterceptTouchEvent(event: MotionEvent): Boolean =
    if (segmented) false else super.onInterceptTouchEvent(event)

  private fun updatePresentation() {
    if (buttons.isEmpty() || itemFrames.size != buttons.size) return
    val selectedIndex: Int
    val scrollTargetCenterX: Float
    if (showsProgressIndicator) {
      val clamped = progress.coerceIn(0f, buttons.lastIndex.toFloat())
      val lower = floor(clamped).toInt()
      val upper = min(lower + 1, buttons.lastIndex)
      val fraction = clamped - lower
      val from = itemFrames[lower]
      val to = itemFrames[upper]
      val interpolatedIndicatorX = from[2] + (to[2] - from[2]) * fraction
      val interpolatedIndicatorWidth = from[3] + (to[3] - from[3]) * fraction
      val indicatorX = interpolatedIndicatorX.roundToInt()
      val indicatorWidth = interpolatedIndicatorWidth.roundToInt()
      val actualPaddingVertical = min(contentPaddingVerticalPx, measuredHeight / 2)
      val indicatorHeight = if (segmented) max(0, measuredHeight - 2 * actualPaddingVertical - dp(4.0)) else indicatorHeightPx
      val indicatorTop = if (segmented) actualPaddingVertical + dp(2.0) else max(0, measuredHeight - indicatorBottomPx - indicatorHeightPx)
      indicator.layout(
        indicatorX,
        indicatorTop,
        indicatorX + indicatorWidth,
        indicatorTop + indicatorHeight,
      )
      indicator.visibility = if (!textVariant && indicatorHeight > 0) VISIBLE else GONE
      scrollTargetCenterX = interpolatedIndicatorX + interpolatedIndicatorWidth / 2f
      selectedIndex = if (controlledSelectedKey != null) items.indexOfFirst { it.key == controlledSelectedKey } else clamped.roundToInt()
      val fontOwner = visualFontOwnerIndex(selectedIndex)
      buttons.forEachIndexed { index, button ->
        val emphasis = if (mappedSource != null && mappedDestination != null) {
          (if (index == mappedSource) 1f - mappedFraction else 0f) + (if (index == mappedDestination) mappedFraction else 0f)
        } else 1f - min(1f, kotlin.math.abs(clamped - index))
        button.setTextColor(ColorUtils.blendARGB(inactiveTextColor, activeTextColor, emphasis))
        button.background = null
        button.isSelected = index == selectedIndex
        if (textVariant) button.typeface = if (index == fontOwner) selectedTypeface() else resolveHeaderTypeface(context, fontFamily)
      }
    } else {
      selectedIndex = items.indexOfFirst { item -> item.key == selectedKey }
      val frame = itemFrames.getOrNull(selectedIndex)
      scrollTargetCenterX = frame?.let { it[0] + it[1] / 2f } ?: 0f
      indicator.visibility = GONE
      buttons.forEachIndexed { index, button ->
        val selected = index == selectedIndex
        button.setTextColor(if (selected) activeTextColor else inactiveTextColor)
        button.background = selectedBackground(selected, button.height)
        button.isSelected = selected
      }
    }

    if (centerSelectionWhenIdle && !segmented && !isTouchDragging && width > 0 && selectedIndex in itemFrames.indices) {
      val desired = (scrollTargetCenterX - width / 2f).roundToInt()
        .coerceIn(0, max(0, content.measuredWidth - width))
      scrollTo(desired, 0)
      centerSelectionWhenIdle = false
    }
  }
}

internal class CollapsiblePagerNativeTabBarView(context: Context) : FrameLayout(context) {
  private val row = CollapsiblePagerHorizontalItemsView(context, true)
  private var heightPx = 0
  private val toolbar = ImageView(context)
  private var config = JSONObject()
  private var items = emptyList<CollapsiblePagerNativeHeaderItem>()
  private var selectedKey = ""
  private var iconOwner: Any = Any()
  private var iconIdentity = ""
  private var loadedIconIdentity = ""
  private var iconRequestPending = false
  private var readyToolbarImage: Bitmap? = null
  private var readyToolbarIdentity = ""
  private var failedIconIdentity = ""
  private var toolbarTouchKey: String? = null
  private var toolbarTouchStarted = false
  private var toolbarKeyboardKey: String? = null
  private var lastNativeProgress = 0f
  private var hasNativeProgress = false
  private var toolbarReservedWidth = 0.0
  private var toolbarPagerBusy = true
  private var toolbarPageIndex = -1
  private var committedToolbarKey: String? = null
  private var committedToolbarEntry: JSONObject? = null
  private var desiredToolbarKey: String? = null
  private var desiredToolbarEntry: JSONObject? = null
  private var loadedToolbarImage: Bitmap? = null
  private var toolbarPresenceAnimating = false
  private var toolbarPresenceInterrupted = false
  private var toolbarVisible = false
  private var toolbarAnimationGeneration = 0
  private var toolbarSize = 38.0
  private var toolbarIconSize = 24.0
  private var toolbarContentWidth = 24.0
  private var toolbarTrailingInset = 20.0
  var onToolbarPress: ((String, DoubleArray) -> Unit)? = null

  private fun dp(value: Number) = (value.toDouble() * resources.displayMetrics.density).roundToInt()

  fun updateConfigJSON(value: String?) {
    config = runCatching { JSONObject(value ?: "{}") }.getOrDefault(JSONObject())
    row.setActiveFontFamily(config.optString("activeFontFamily").takeIf(String::isNotEmpty))
    selectedKey = if (config.optBoolean("controlled")) config.optString("selectedKey") else items.getOrNull(lastNativeProgress.roundToInt())?.key ?: ""
    row.setControlledSelectedKey(if (config.optBoolean("controlled")) selectedKey else null, controlledPageIndices() != null)
    if (hasNativeProgress) setProgress(lastNativeProgress)
    else {
      val index = items.indexOfFirst { it.key == selectedKey }
      if (index >= 0) row.setProgress(index.toFloat())
    }
    updateToolbar()
  }

  private fun ownsSettledToolbar(): Boolean = config.optBoolean("controlled") && config.optJSONObject("toolbar") != null

  internal fun usesSettledToolbarOwner(): Boolean = ownsSettledToolbar()

  internal fun needsToolbarPagerSettlement(pageIndex: Int): Boolean =
    ownsSettledToolbar() && (toolbarPagerBusy || toolbarPageIndex != pageIndex)

  private fun toolbarCanCommit(): Boolean {
    if (!isAttachedToWindow) return false
    if (!ownsSettledToolbar()) return true
    if (toolbarPagerBusy || toolbarPageIndex < 0) return false
    return controlledPageIndices() == null || config.optJSONArray("pageKeys")?.optString(toolbarPageIndex) == selectedKey
  }

  fun updateToolbarPagerState(pageIndex: Int, busy: Boolean) {
    if (toolbarPagerBusy == busy && (busy || toolbarPageIndex == pageIndex)) return
    if (!busy) toolbarPageIndex = pageIndex
    toolbarPagerBusy = busy
    updateToolbar()
  }

  private fun cancelToolbarPending() {
    if (iconRequestPending || readyToolbarImage != null) {
      OneKeyNamedSVG.cancel(iconOwner)
      iconOwner = Any()
    }
    iconRequestPending = false
    readyToolbarImage = null
    readyToolbarIdentity = ""
  }

  private fun setToolbarInteractive(enabled: Boolean) {
    if (!enabled) {
      toolbarKeyboardKey = null
      if (toolbarTouchStarted) toolbarTouchKey = null
      toolbar.isPressed = false
      toolbar.isHovered = false
      toolbar.clearFocus()
    }
    toolbar.isEnabled = enabled
    toolbar.isClickable = enabled
    toolbar.isFocusable = enabled
    toolbar.importantForAccessibility = if (enabled) View.IMPORTANT_FOR_ACCESSIBILITY_AUTO else View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
  }

  private fun suspendToolbar() {
    setToolbarInteractive(false)
    cancelToolbarPending()
    desiredToolbarKey = null
    desiredToolbarEntry = null
    if (toolbarPresenceAnimating) {
      toolbarAnimationGeneration++
      toolbar.animate().cancel()
      toolbarPresenceAnimating = false
      toolbarPresenceInterrupted = true
    }
  }

  private fun setToolbarVisible(visible: Boolean) {
    if (toolbarVisible == visible && !toolbarPresenceInterrupted) return
    toolbarVisible = visible
    toolbarPresenceInterrupted = false
    val generation = ++toolbarAnimationGeneration
    toolbar.animate().cancel()
    if (visible) toolbar.visibility = VISIBLE
    if (!isAttachedToWindow) {
      toolbar.alpha = if (visible) 1f else 0f
      toolbar.visibility = if (visible) VISIBLE else INVISIBLE
      toolbarPresenceAnimating = false
      return
    }
    toolbarPresenceAnimating = true
    toolbar.animate().alpha(if (visible) 1f else 0f).setDuration(100).withEndAction {
      if (toolbarAnimationGeneration == generation) {
        toolbarPresenceAnimating = false
        if (!visible) toolbar.visibility = INVISIBLE
      }
    }.start()
  }

  private fun hideToolbar(failed: Boolean) {
    setToolbarInteractive(false)
    committedToolbarKey = null
    committedToolbarEntry = null
    toolbar.contentDescription = null
    toolbar.setTag(com.facebook.react.R.id.react_test_id, null)
    if (failed) toolbar.setImageDrawable(null)
    setToolbarVisible(false)
  }

  private fun commitToolbar(key: String, descriptor: JSONObject, image: Bitmap, identity: String) {
    if (!toolbarCanCommit() || key != selectedKey) return
    if (committedToolbarKey != key && toolbarTouchStarted) toolbarTouchKey = null
    toolbarSize = descriptor.optDouble("size", 38.0).coerceIn(1.0, 128.0)
    toolbarIconSize = descriptor.optDouble("iconSize", min(24.0, toolbarSize)).coerceIn(1.0, toolbarSize)
    toolbarContentWidth = descriptor.optDouble("contentWidth", min(24.0, toolbarSize)).coerceIn(0.0, toolbarSize)
    toolbarTrailingInset = descriptor.optDouble("trailingInset", 20.0).coerceIn(0.0, 128.0)
    val inset = max(0, (dp(toolbarSize) - dp(toolbarIconSize)) / 2)
    toolbar.setPadding(inset, inset, inset, inset)
    fun color(name: String, fallback: Int) = (descriptor.opt(name) as? Number)?.toInt() ?: fallback
    val base = if (descriptor.optBoolean("selected")) color("selectedBackgroundColor", Color.TRANSPARENT) else Color.TRANSPARENT
    fun fill(value: Int) = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(value) }
    toolbar.background = StateListDrawable().apply {
      addState(intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed), fill(color("pressedBackgroundColor", base)))
      addState(intArrayOf(android.R.attr.state_enabled, android.R.attr.state_hovered), fill(color("hoverBackgroundColor", base)))
      addState(intArrayOf(), fill(base))
    }
    toolbar.setImageBitmap(image)
    toolbar.contentDescription = descriptor.optString("accessibilityLabel")
    toolbar.setTag(com.facebook.react.R.id.react_test_id, descriptor.optString("testID"))
    committedToolbarKey = key
    committedToolbarEntry = descriptor
    loadedToolbarImage = image
    loadedIconIdentity = identity
    iconIdentity = identity
    iconRequestPending = false
    readyToolbarImage = null
    readyToolbarIdentity = ""
    setToolbarVisible(true)
    setToolbarInteractive(true)
    requestLayout()
  }

  private fun applyReadyToolbarImage() {
    val image = readyToolbarImage ?: return
    val key = desiredToolbarKey ?: return
    val entry = desiredToolbarEntry ?: return
    if (readyToolbarIdentity != iconIdentity || !toolbarCanCommit() || key != selectedKey) return
    commitToolbar(key, entry, image, iconIdentity)
  }

  private fun updateToolbar() {
    val entries = config.optJSONObject("toolbar")
    toolbarReservedWidth = if (entries == null) 0.0 else 44.0
    if (entries != null && entries.length() > 0) {
      toolbarReservedWidth = 0.0
      entries.keys().forEach { key ->
        val entry = entries.optJSONObject(key) ?: JSONObject()
        val size = entry.optDouble("size", 38.0).coerceIn(1.0, 128.0)
        toolbarReservedWidth = max(toolbarReservedWidth, entry.optDouble("contentWidth", min(24.0, size)).coerceIn(0.0, size) + entry.optDouble("trailingInset", 20.0).coerceIn(0.0, 128.0))
      }
    }
    if (!toolbarCanCommit()) { suspendToolbar(); requestLayout(); return }
    val entry = entries?.optJSONObject(selectedKey)
    if (entry == null || entry.optString("iconName").isEmpty()) {
      cancelToolbarPending()
      desiredToolbarKey = null
      desiredToolbarEntry = null
      hideToolbar(false)
      requestLayout()
      return
    }
    val descriptor = JSONObject(entry.toString())
    desiredToolbarKey = selectedKey
    desiredToolbarEntry = descriptor
    val size = descriptor.optDouble("size", 38.0).coerceIn(1.0, 128.0)
    val iconSize = descriptor.optDouble("iconSize", min(24.0, size)).coerceIn(1.0, size)
    val name = descriptor.optString("iconName")
    val tint = descriptor.optString("iconColor", "#000000")
    val identity = "$name|$tint|${resources.displayMetrics.density}|$iconSize"
    val loaded = loadedToolbarImage
    if (identity == loadedIconIdentity && loaded != null) {
      cancelToolbarPending()
      commitToolbar(selectedKey, descriptor, loaded, identity)
      return
    }
    if (identity == iconIdentity && iconRequestPending) { applyReadyToolbarImage(); return }
    cancelToolbarPending()
    setToolbarInteractive(false)
    iconIdentity = identity
    if (identity == failedIconIdentity) { hideToolbar(true); return }
    iconOwner = Any()
    val owner = iconOwner
    iconRequestPending = true
    OneKeyNamedSVG.request(owner, context.applicationContext.assets, name, tint,
      dp(iconSize), dp(iconSize), resources.displayMetrics.density) { bitmap ->
      if (iconOwner === owner && iconIdentity == identity && isAttachedToWindow) {
        if (bitmap == null) {
          iconRequestPending = false
          failedIconIdentity = identity
          hideToolbar(true)
        } else {
          readyToolbarImage = bitmap
          readyToolbarIdentity = identity
          applyReadyToolbarImage()
        }
      }
    }
    requestLayout()
  }

  private fun performToolbarPress(fromPointer: Boolean): Boolean {
    val key = if (fromPointer) toolbarTouchKey.takeIf { toolbarTouchStarted } else committedToolbarKey
    if (fromPointer) { toolbarTouchStarted = false; toolbarTouchKey = null }
    if (key == null || key != committedToolbarKey || key != selectedKey || committedToolbarEntry == null ||
      !toolbarCanCommit() || !toolbar.isEnabled || toolbar.visibility != VISIBLE) return false
    val location = IntArray(2)
    toolbar.getLocationInWindow(location)
    val density = resources.displayMetrics.density.toDouble()
    onToolbarPress?.invoke(key, doubleArrayOf(location[0] / density, location[1] / density, toolbar.width / density, toolbar.height / density))
    return true
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    toolbarPagerBusy = true
    updateToolbar()
  }

  override fun onDetachedFromWindow() {
    suspendToolbar()
    toolbarPagerBusy = true
    toolbarPageIndex = -1
    toolbarTouchStarted = false
    toolbarTouchKey = null
    failedIconIdentity = ""
    super.onDetachedFromWindow()
  }

  var onItemPress: ((Int, String) -> Unit)?
    get() = row.onItemPress
    set(value) {
      row.onItemPress = value
    }

  init {
    addView(row, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    addView(toolbar)
    toolbar.scaleType = ImageView.ScaleType.CENTER_INSIDE
    toolbar.setPadding(dp(6), dp(6), dp(6), dp(6))
    toolbar.isClickable = true
    toolbar.isFocusable = true
    toolbar.visibility = INVISIBLE
    toolbar.alpha = 0f
    toolbar.isEnabled = false
    toolbar.isClickable = false
    toolbar.isFocusable = false
    toolbar.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    toolbar.setOnTouchListener { _, event ->
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> { toolbarTouchStarted = true; toolbarTouchKey = committedToolbarKey.takeIf { toolbar.isEnabled && toolbarCanCommit() } }
        MotionEvent.ACTION_CANCEL -> { toolbarTouchKey = null }
      }
      false
    }
    toolbar.setOnClickListener { performToolbarPress(true) }
    toolbar.accessibilityDelegate = object : View.AccessibilityDelegate() {
      override fun performAccessibilityAction(host: View, action: Int, args: android.os.Bundle?): Boolean {
        if (action == android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK) return performToolbarPress(false)
        return super.performAccessibilityAction(host, action, args)
      }
    }
    toolbar.setOnKeyListener { _, keyCode, event ->
      if (keyCode != android.view.KeyEvent.KEYCODE_ENTER && keyCode != android.view.KeyEvent.KEYCODE_DPAD_CENTER && keyCode != android.view.KeyEvent.KEYCODE_SPACE) false
      else if (event.action == android.view.KeyEvent.ACTION_DOWN) {
        if (event.repeatCount == 0) toolbarKeyboardKey = committedToolbarKey.takeIf { toolbar.isEnabled && toolbarCanCommit() }
        toolbar.isPressed = toolbarKeyboardKey != null
        toolbarKeyboardKey != null
      } else if (event.action == android.view.KeyEvent.ACTION_UP) {
        val valid = toolbarKeyboardKey != null && toolbarKeyboardKey == committedToolbarKey
        toolbarKeyboardKey = null
        toolbar.isPressed = false
        valid && performToolbarPress(false)
      } else false
    }
    visibility = GONE
  }

  fun updateItemsJSON(value: String?) {
    items = parseHeaderItems(value)
    row.updateItemsJSON(value)
    visibility = row.visibility
    updateConfigJSON(config.toString())
  }

  fun updateStyle(
    height: Double,
    variant: String,
    contentPadding: Double,
    contentPaddingVertical: Double,
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
      variant,
      contentPaddingVertical,
    )
    requestLayout()
  }

  fun updateColors(
    backgroundColor: Int,
    activeTextColor: Int,
    inactiveTextColor: Int,
    indicatorColor: Int,
  ) {
    val text = row.usesTextVariant()
    setBackgroundColor(if (text) backgroundColor else Color.TRANSPARENT)
    row.updateColors(
      if (text) Color.TRANSPARENT else backgroundColor,
      activeTextColor,
      inactiveTextColor,
      Color.TRANSPARENT,
      indicatorColor,
    )
  }

  private fun controlledPageIndices(): List<Int>? {
    val pageKeys = config.optJSONArray("pageKeys") ?: return null
    val names = List(pageKeys.length()) { pageKeys.optString(it) }
    val indices = names.map { name -> items.indexOfFirst { it.key == name } }
    if (names.isEmpty() || selectedKey !in names || names.toSet().size != names.size ||
      indices.any { it < 0 } || items.map { it.key }.toSet().size != items.size) return null
    return indices
  }

  fun usesControlledTextProgress(): Boolean {
    return row.usesTextVariant() && config.optBoolean("controlled") && controlledPageIndices() != null
  }

  fun setProgress(progress: Float) {
    lastNativeProgress = progress
    hasNativeProgress = true
    var mapped = progress
    var source: Int? = null
    var destination: Int? = null
    var fraction = 0f
    if (config.optBoolean("controlled")) {
      val pageKeys = config.optJSONArray("pageKeys") ?: return
      val indices = controlledPageIndices()
      if (indices == null) {
        val fallback = items.indexOfFirst { it.key == config.optString("selectedKey") }
        if (fallback >= 0) { row.setProgress(fallback.toFloat()); selectedKey = items[fallback].key; updateToolbar() }
        return
      }
      val bounded = progress.coerceIn(0f, (pageKeys.length() - 1).toFloat())
      val lower = floor(bounded).toInt().coerceAtLeast(0)
      val upper = min(lower + 1, pageKeys.length() - 1)
      val from = indices[lower]
      val to = indices[upper]
      if (from < 0 || to < 0) return
      fraction = bounded - lower
      source = from
      destination = to
      mapped = from + (to - from) * fraction
    }
    row.setProgress(mapped, source, destination, fraction)
    if (config.optBoolean("controlled")) return
    val selectedIndex = mapped.roundToInt()
    val nextKey = items.getOrNull(selectedIndex)?.key ?: return
    if (nextKey != selectedKey) { toolbarTouchKey = null; toolbar.isPressed = false; selectedKey = nextKey; updateToolbar() }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val height = MeasureSpec.getSize(heightMeasureSpec)
    val reserved = dp(toolbarReservedWidth)
    row.measure(MeasureSpec.makeMeasureSpec(max(0, width - reserved), MeasureSpec.EXACTLY), heightMeasureSpec)
    toolbar.measure(MeasureSpec.makeMeasureSpec(dp(toolbarSize), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(dp(toolbarSize), MeasureSpec.EXACTLY))
    setMeasuredDimension(width, height)
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    val width = right - left
    val height = bottom - top
    val reserved = dp(toolbarReservedWidth)
    val rtl = layoutDirection == View.LAYOUT_DIRECTION_RTL
    row.layout(if (rtl) reserved else 0, 0, if (rtl) width else width - reserved, height)
    val inset = dp(toolbarTrailingInset) - (dp(toolbarSize) - dp(toolbarContentWidth)) / 2
    val x = if (rtl) inset else width - inset - dp(toolbarSize)
    toolbar.layout(x, (height - dp(toolbarSize)) / 2, x + dp(toolbarSize), (height + dp(toolbarSize)) / 2)
  }

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
    val typeface = resolveHeaderTypeface(context, fontFamily)
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
