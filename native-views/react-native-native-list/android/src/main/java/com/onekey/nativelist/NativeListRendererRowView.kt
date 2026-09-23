package com.margelo.nitro.nativelist

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import com.facebook.react.uimanager.ThemedReactContext
import kotlin.math.roundToInt
import org.json.JSONObject

internal enum class NativeListRendererUpdate {
  UNCHANGED,
  CONTENT,
  ASSETS,
  REPLACE,
}

// Shared chrome and binding lifetime; each renderer owns its content views.
internal abstract class NativeListRendererRowView(protected val reactContext: ThemedReactContext) :
  NativeListRowHost(reactContext) {
  protected abstract fun bindContent(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  )

  protected open fun bindSelectionContent(
    item: NativeListItem,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {}

  protected abstract fun recycleContent()

  protected abstract fun disposeContent()

  protected open fun usesSourceScale(item: NativeListItem, provided: Boolean) = provided

  protected open fun onRowTouch(event: MotionEvent) {}

  protected open fun consumesRowClick() = false

  protected fun setTouchPressed(value: Boolean) {
    touchPressed = value
  }

  protected fun retainBoundItem(item: NativeListItem): Boolean {
    if (current?.key != item.key) return false
    current = item
    tag = item
    return true
  }

  protected open fun accessibilityText(item: NativeListItem) =
    item.json.optString("accessibilityLabel", item.json.optString("title"))

  protected open val assetFields: List<String> = emptyList()

  private fun update(old: NativeListItem?, next: NativeListItem): NativeListRendererUpdate {
    if (old == null || old.key != next.key || old.rendererKey != next.rendererKey)
      return NativeListRendererUpdate.REPLACE
    if (old.content == next.content) return NativeListRendererUpdate.UNCHANGED
    if (
      assetFields.any {
        nativeListCanonical(old.json.opt(it)) != nativeListCanonical(next.json.opt(it))
      }
    )
      return NativeListRendererUpdate.ASSETS
    return NativeListRendererUpdate.CONTENT
  }

  protected open fun defaultHeight(item: NativeListItem, layout: String) = 0

  protected open fun minimumContentHeight(item: NativeListItem, layout: String, sizeDelta: Int) =
    dp((defaultHeight(item, layout) + sizeDelta).coerceAtLeast(0))

  protected open fun modelHeight(item: NativeListItem, layout: String): Int? =
    if (item.json.has("height"))
      when (item.json.optString("heightRounding")) {
        "floor" -> (item.json.optDouble("height") * resources.displayMetrics.density).toInt()
        "nearest" -> stylePx(item.json.optDouble("height"))
        else -> dp(item.json.optInt("height"))
      }
    else null

  protected open fun horizontalWidth(item: NativeListItem) = dp(280)

  protected open val defaultCornerRadius = 0
  protected open val defaultSeparatorInset = 12
  protected open val pressChangesBackground = true

  protected open fun pressContent(pressed: Boolean) {}

  protected open val showsSelection = true
  protected open val pressedColorName = "rowPressedBackground"
  protected open val pressedColorFallback = "#00000017"
  private var current: NativeListItem? = null
  private var theme: JSONObject? = null
  private var listLayout = "linear"
  private var itemIndex: Int? = null

  protected open fun unselectedBackground(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    itemIndex: Int?,
  ) = color(theme?.optString("rowBackground", "#FFFFFF") ?: "#FFFFFF")

  protected open fun backgroundGroupPosition(
    item: NativeListItem,
    layout: String,
    itemIndex: Int?,
    selected: Boolean,
  ) = item.json.optString("groupPosition")

  private var selected = false
  protected val rowSelected
    get() = selected

  protected var sourceScale = false
  private var inputSignature = ""
  private var explicitHeight: Int? = null
  private var touchPressed = false
  private var reorderActive = false
  private val separator = Paint(Paint.ANTI_ALIAS_FLAG)

  protected fun dp(value: Int) =
    if (sourceScale) (value * resources.displayMetrics.density).roundToInt()
    else NativeListScale.dp(resources, value)

  protected fun stylePx(value: Double) = (value * resources.displayMetrics.density).roundToInt()

  private fun color(value: String, fallback: Int = Color.TRANSPARENT) =
    runCatching { parseNativeListColor(value) }.getOrDefault(fallback)

  init {
    orientation = HORIZONTAL
    setOnClickListener {
      if (consumesRowClick()) return@setOnClickListener
      current
        ?.takeIf { it.isRowPressEnabled }
        ?.let { onRowPress?.invoke(it, NativeListActionOrigin(this, this, bindingEpoch, "row")) }
    }
    setOnTouchListener { _, event ->
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> touchPressed = current?.isRowPressEnabled == true
        MotionEvent.ACTION_UP,
        MotionEvent.ACTION_CANCEL -> touchPressed = false
      }
      onRowTouch(event)
      appearance()
      false
    }
  }

  override fun bind(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    listOrientation: String,
    itemIndex: Int?,
    selected: Boolean,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
    useSourceScale: Boolean,
  ) {
    val update = update(current, item)
    val signature =
      nativeListCanonical(
        JSONObject()
          .put("theme", theme)
          .put("layout", layout)
          .put("orientation", listOrientation)
          .put("sourceScale", useSourceScale)
          .put("listStyle", listStyle)
      )
    sourceScale = usesSourceScale(item, useSourceScale)
    if (update != NativeListRendererUpdate.UNCHANGED || signature != inputSignature) {
      if (current != null) onBindingInvalidated?.invoke(this, bindingEpoch)
      bindingEpoch++
      if (update == NativeListRendererUpdate.REPLACE) {
        recycleContent()
        touchPressed = false
      }
      bindContent(item, theme, layout, checkboxState)
      inputSignature = signature
    }
    current = item
    tag = item
    this.theme = theme
    listLayout = layout
    this.itemIndex = itemIndex
    this.selected = selected
    explicitHeight = item.styledHeight?.let(::stylePx) ?: modelHeight(item, layout)
    val sizeDelta =
      when (item.json.optString("size")) {
        "small" -> -8
        "large" -> 12
        else -> 0
      }
    minimumHeight = if (explicitHeight == null) minimumContentHeight(item, layout, sizeDelta) else 0
    layoutParams =
      (layoutParams
          ?: android.view.ViewGroup.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT,
          ))
        .apply {
          width =
            if (listOrientation == "horizontal") horizontalWidth(item)
            else LayoutParams.MATCH_PARENT
          height = LayoutParams.WRAP_CONTENT
        }
    contentDescription = accessibilityText(item)
    setTag(
      com.facebook.react.R.id.react_test_id,
      item.json.optString("testID").takeIf { it.isNotEmpty() },
    )
    isEnabled = !item.json.optBoolean("disabled")
    isClickable = item.isWholeRowInteractive
    isFocusable = isClickable
    if (!isEnabled) touchPressed = false
    bindSelectionContent(item, checkboxState)
    appearance()
    requestLayout()
  }

  override fun bindSelection(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    itemIndex: Int?,
    selected: Boolean,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    if (current?.key != item.key) return
    current = item
    tag = item
    this.theme = theme
    this.selected = selected
    listLayout = layout
    this.itemIndex = itemIndex
    bindSelectionContent(item, checkboxState)
    appearance()
  }

  protected fun emitAction(
    key: String,
    view: View,
    source: String,
    slot: Int? = null,
    target: NativeSelectionTarget? = null,
    anchorInset: Int = 0,
  ) {
    val item = current ?: return
    if (key.isNotEmpty() && !item.json.optBoolean("disabled"))
      onAction?.invoke(
        item,
        key,
        target,
        NativeListActionOrigin(view, this, bindingEpoch, source, slot, anchorInset),
      )
  }

  private fun appearance() {
    val item = current ?: return
    val pressed = touchPressed || reorderActive
    pressContent(pressed)
    val container = item.json.optJSONObject("style")?.optJSONObject("container") ?: JSONObject()
    val showSelected =
      showsSelection && selected && (listLayout != "sectioned" || item.json.optBoolean("selected"))
    val backgroundName =
      if (pressed && pressChangesBackground) pressedColorName
      else if (showSelected) "rowSelectedBackground" else "rowBackground"
    val backgroundFallback =
      if (pressed && pressChangesBackground) pressedColorFallback
      else if (showSelected) "#0000000F" else "#FFFFFF"
    var fill =
      if (!pressed && !showSelected) unselectedBackground(item, theme, listLayout, itemIndex)
      else color(theme?.optString(backgroundName, backgroundFallback) ?: backgroundFallback)
    if (!pressed || !pressChangesBackground) {
      if (item.json.has("backgroundColor"))
        fill = color(item.json.optString("backgroundColor"), fill)
      if (container.has("backgroundColor"))
        fill = color(container.optString("backgroundColor"), fill)
    }
    val position = backgroundGroupPosition(item, listLayout, itemIndex, showSelected)
    val radius =
      if (container.has("cornerRadius")) stylePx(container.optDouble("cornerRadius")).toFloat()
      else if (defaultCornerRadius > 0) dp(defaultCornerRadius).toFloat()
      else if (listStyle?.has("groupCornerRadius") == true)
        stylePx(listStyle!!.optDouble("groupCornerRadius")).toFloat()
      else dp(12).toFloat()
    val top =
      container.has("cornerRadius") ||
        defaultCornerRadius > 0 ||
        position in setOf("first", "single")
    val bottom =
      container.has("cornerRadius") ||
        defaultCornerRadius > 0 ||
        position in setOf("last", "single")
    val radii = FloatArray(8) { if (it < 4 && top || it >= 4 && bottom) radius else 0f }
    outlineProvider = ViewOutlineProvider.BACKGROUND
    clipToOutline = container.has("cornerRadius")
    background =
      GradientDrawable().apply {
        setColor(fill)
        cornerRadii = radii
      }
    foreground =
      if (container.has("borderWidth"))
        GradientDrawable().apply {
          setColor(Color.TRANSPARENT)
          cornerRadii = radii
          setStroke(
            stylePx(container.optDouble("borderWidth")),
            color(container.optString("borderColor", "#00000000")),
          )
        }
      else null
    alpha =
      container.optDouble("opacity", item.json.optDouble("opacity", 1.0)).toFloat() *
        if (isEnabled) 1f else 0.5f
    separator.color =
      color(
        listStyle
          ?.optJSONObject("separator")
          ?.optString("color", theme?.optString("separator", "#0000001F") ?: "#0000001F")
          ?: theme?.optString("separator", "#0000001F")
          ?: "#0000001F"
      )
    separator.strokeWidth = 1f
    invalidate()
  }

  override fun setReorderActive(active: Boolean) {
    reorderActive = active
    appearance()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    // Android's native measurement consumes the exact resolved text/image views.
    super.onMeasure(
      widthMeasureSpec,
      explicitHeight?.let { MeasureSpec.makeMeasureSpec(it, MeasureSpec.EXACTLY) }
        ?: heightMeasureSpec,
    )
  }

  override fun dispatchDraw(canvas: Canvas) {
    super.dispatchDraw(canvas)
    current?.takeIf(::nativeListLegacyShowsSeparator)?.let {
      val start =
        listStyle
          ?.optJSONObject("separator")
          ?.takeIf { it.has("inset") }
          ?.let { stylePx(it.optDouble("inset")) } ?: dp(12)
      canvas.drawLine(start.toFloat(), height - 1f, width.toFloat(), height - 1f, separator)
    }
  }

  override fun onInitializeAccessibilityNodeInfo(
    info: android.view.accessibility.AccessibilityNodeInfo
  ) {
    super.onInitializeAccessibilityNodeInfo(info)
    info.viewIdResourceName = getTag(com.facebook.react.R.id.react_test_id) as? String
  }

  override fun recycle() {
    if (current != null) onBindingInvalidated?.invoke(this, bindingEpoch)
    bindingEpoch++
    current = null
    tag = null
    inputSignature = ""
    touchPressed = false
    reorderActive = false
    recycleContent()
  }

  override fun dispose() {
    recycle()
    disposeContent()
  }
}
