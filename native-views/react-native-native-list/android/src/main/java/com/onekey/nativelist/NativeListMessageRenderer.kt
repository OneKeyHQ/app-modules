package com.margelo.nitro.nativelist

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import com.facebook.react.uimanager.ThemedReactContext
import kotlin.math.roundToInt
import org.json.JSONObject

internal object NativeListMessageRenderer {
  enum class Update {
    UNCHANGED,
    CONTENT,
    ASSETS,
    REPLACE,
  }

  data class Resolved(
    val title: NativeListResolvedText,
    val body: NativeListResolvedText,
    val time: NativeListResolvedText,
    val horizontalPadding: Int,
    val verticalPadding: Int,
    val bodyGap: Int,
    val timeGap: Int,
    val leadingGap: Int,
    val imageWidth: Int,
    val imageHeight: Int,
    val imageStyle: JSONObject,
    val leading: JSONObject?,
    val thumbnail: JSONObject?,
    val unread: Boolean,
    val gravity: Int,
  )

  fun resolve(
    context: ThemedReactContext,
    item: NativeListItem,
    theme: JSONObject?,
    sourceScale: Boolean,
  ): Resolved {
    val style = item.json.optJSONObject("style") ?: JSONObject()
    val density = context.resources.displayMetrics.density
    fun dp(value: Int) =
      if (sourceScale) (value * density).roundToInt()
      else NativeListScale.dp(context.resources, value)
    fun dimension(source: JSONObject, name: String, default: Int) =
      if (source.has(name)) (source.optDouble(name) * density).roundToInt() else dp(default)
    fun color(name: String, fallback: String) =
      parseNativeListColor(theme?.optString(name, fallback) ?: fallback)
    fun text(name: String, size: Float, weight: String, color: Int, height: Int, lines: Int) =
      NativeListResolvedText.resolve(
        context,
        item.json.optString(name),
        style.optJSONObject(name),
        size,
        weight,
        color,
        height,
        lines,
        sourceScale,
      )
    val image = style.optJSONObject("image") ?: JSONObject()
    return Resolved(
      text("title", 14f, "semibold", color("primaryText", "#000000DF"), 20, 2),
      text(
        "body",
        14f,
        "regular",
        color("secondaryText", "#0000009B"),
        20,
        item.json.optInt("bodyLines", 3),
      ),
      text("time", 12f, "regular", color("disabledText", "#00000072"), 16, 1),
      dimension(style, "horizontalPadding", 12),
      dimension(style, "verticalPadding", 16),
      dimension(style, "lineGap", 2),
      dimension(style, "lineGap", 4),
      dimension(style, "leadingGap", 12),
      dimension(image, "width", 28),
      dimension(image, "height", 28),
      image,
      item.json.optJSONObject("leading"),
      item.json.optJSONObject("thumbnail"),
      item.json.optBoolean("unread"),
      when (style.optJSONObject("container")?.optString("contentVerticalAlignment")) {
        "bottom" -> Gravity.BOTTOM
        "center" -> Gravity.CENTER_VERTICAL
        else -> Gravity.TOP
      },
    )
  }

  fun update(old: NativeListItem?, next: NativeListItem): Update {
    if (old == null || old.key != next.key || old.rendererKey != next.rendererKey)
      return Update.REPLACE
    if (old.content == next.content) return Update.UNCHANGED
    if (
      listOf("leading", "thumbnail").any {
        nativeListCanonical(old.json.opt(it)) != nativeListCanonical(next.json.opt(it))
      }
    )
      return Update.ASSETS
    return Update.CONTENT
  }

  class Views(private val context: ThemedReactContext) {
    val column = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
    val title = NativeListTextView(context)
    val body = NativeListTextView(context)
    val time = NativeListTextView(context)
    private var leading: NativeListLeadingVisual? = null
    private var thumbnail: NativeListImageSlot? = null

    init {
      listOf(title, body, time).forEach {
        column.addView(
          it,
          LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
          ),
        )
      }
    }

    fun bind(
      root: LinearLayout,
      resolved: Resolved,
      item: NativeListItem,
      theme: JSONObject?,
      sourceScale: Boolean,
    ) {
      resolved.title.bind(title)
      resolved.body.bind(body)
      resolved.time.bind(time)
      root.gravity = resolved.gravity
      root.clipChildren =
        !(resolved.leading?.optString("kind") == "token" && resolved.leading.has("networkImage"))
      root.setPadding(
        resolved.horizontalPadding,
        resolved.verticalPadding,
        resolved.horizontalPadding,
        resolved.verticalPadding,
      )
      resolved.leading?.let { visual ->
        val host = leading ?: NativeListLeadingVisual(context).also { leading = it }
        host.bind(visual, resolved.imageStyle, item.key, theme, resolved.unread, sourceScale)
        val params =
          LinearLayout.LayoutParams(resolved.imageWidth, resolved.imageHeight).apply {
            marginEnd = resolved.leadingGap
          }
        if (host.parent == null) root.addView(host, 0, params) else host.layoutParams = params
      }
        ?: leading?.let {
          it.recycle()
          root.removeView(it)
        }
      if (column.parent == null)
        root.addView(
          column,
          LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
        )
      body.layoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
          )
          .apply { topMargin = resolved.bodyGap }
      time.layoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
          )
          .apply { topMargin = resolved.timeGap }
      resolved.thumbnail?.let { image ->
        val slot = thumbnail ?: NativeListImageSlot(context).also { thumbnail = it }
        val dp: (Int) -> Int = {
          if (sourceScale) (it * context.resources.displayMetrics.density).roundToInt()
          else NativeListScale.dp(context.resources, it)
        }
        slot.view.outlineProvider =
          object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
              outline.setRoundRect(0, 0, view.width, view.height, dp(6).toFloat())
            }
          }
        slot.view.clipToOutline = true
        slot.view.foreground =
          GradientDrawable().apply {
            setColor(Color.TRANSPARENT)
            cornerRadius = dp(6).toFloat()
            setStroke(
              1,
              parseNativeListColor(theme?.optString("strongBackground", "#0000000F") ?: "#0000000F"),
            )
          }
        val params = LinearLayout.LayoutParams(dp(64), dp(64)).apply { marginStart = dp(12) }
        if (slot.view.parent == null) root.addView(slot.view, params)
        else slot.view.layoutParams = params
        slot.bind(
          image,
          "${item.key}:thumbnail",
          placeholder = theme?.optString("strongBackground", "#0000000F") ?: "#0000000F",
        )
      }
        ?: thumbnail?.let {
          it.recycle()
          root.removeView(it.view)
        }
    }

    fun recycle() {
      leading?.recycle()
      thumbnail?.recycle()
    }

    fun dispose() {
      leading?.dispose()
      thumbnail?.dispose()
    }
  }
}

internal class NativeListMessageRowView(private val reactContext: ThemedReactContext) :
  NativeListRowHost(reactContext) {
  private val views = NativeListMessageRenderer.Views(reactContext)
  private var current: NativeListItem? = null
  private var theme: JSONObject? = null
  private var listLayout = "linear"
  private var selected = false
  private var sourceScale = false
  private var inputSignature = ""
  private var explicitHeight: Int? = null
  private var touchPressed = false
  private val separator = Paint(Paint.ANTI_ALIAS_FLAG)

  private fun dp(value: Int) =
    if (sourceScale) (value * resources.displayMetrics.density).roundToInt()
    else NativeListScale.dp(resources, value)

  private fun stylePx(value: Double) = (value * resources.displayMetrics.density).roundToInt()

  private fun color(value: String, fallback: Int = Color.TRANSPARENT) =
    runCatching { parseNativeListColor(value) }.getOrDefault(fallback)

  init {
    orientation = HORIZONTAL
    setOnClickListener {
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
    val update = NativeListMessageRenderer.update(current, item)
    val signature =
      nativeListCanonical(
        JSONObject()
          .put("theme", theme)
          .put("layout", layout)
          .put("orientation", listOrientation)
          .put("sourceScale", useSourceScale)
          .put("listStyle", listStyle)
      )
    sourceScale = useSourceScale
    if (update != NativeListMessageRenderer.Update.UNCHANGED || signature != inputSignature) {
      if (current != null) onBindingInvalidated?.invoke(this, bindingEpoch)
      bindingEpoch++
      if (update == NativeListMessageRenderer.Update.REPLACE) {
        views.recycle()
        touchPressed = false
      }
      views.bind(
        this,
        NativeListMessageRenderer.resolve(reactContext, item, theme, sourceScale),
        item,
        theme,
        sourceScale,
      )
      inputSignature = signature
    }
    current = item
    tag = item
    this.theme = theme
    listLayout = layout
    this.selected = selected
    explicitHeight =
      item.styledHeight?.let(::stylePx)
        ?: if (item.json.has("height"))
          when (item.json.optString("heightRounding")) {
            "floor" -> (item.json.optDouble("height") * resources.displayMetrics.density).toInt()
            "nearest" -> stylePx(item.json.optDouble("height"))
            else -> dp(item.json.optInt("height"))
          }
        else null
    minimumHeight =
      if (explicitHeight == null && item.json.optString("size") == "large") dp(12) else 0
    layoutParams =
      (layoutParams
          ?: android.view.ViewGroup.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT,
          ))
        .apply {
          width = if (listOrientation == "horizontal") dp(280) else LayoutParams.MATCH_PARENT
          height = LayoutParams.WRAP_CONTENT
        }
    contentDescription = item.json.optString("accessibilityLabel", item.json.optString("title"))
    setTag(
      com.facebook.react.R.id.react_test_id,
      item.json.optString("testID").takeIf { it.isNotEmpty() },
    )
    isEnabled = !item.json.optBoolean("disabled")
    isClickable = item.isWholeRowInteractive
    isFocusable = isClickable
    if (!isEnabled) touchPressed = false
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
    appearance()
  }

  private fun appearance() {
    val item = current ?: return
    val container = item.json.optJSONObject("style")?.optJSONObject("container") ?: JSONObject()
    val showSelected = selected && (listLayout != "sectioned" || item.json.optBoolean("selected"))
    val backgroundName =
      if (touchPressed) "rowPressedBackground"
      else if (showSelected) "rowSelectedBackground" else "rowBackground"
    val backgroundFallback =
      if (touchPressed) "#00000017" else if (showSelected) "#0000000F" else "#FFFFFF"
    var fill = color(theme?.optString(backgroundName, backgroundFallback) ?: backgroundFallback)
    if (!touchPressed) {
      if (item.json.has("backgroundColor"))
        fill = color(item.json.optString("backgroundColor"), fill)
      if (container.has("backgroundColor"))
        fill = color(container.optString("backgroundColor"), fill)
    }
    val position = item.json.optString("groupPosition")
    val radius =
      if (container.has("cornerRadius")) stylePx(container.optDouble("cornerRadius")).toFloat()
      else if (listStyle?.has("groupCornerRadius") == true)
        stylePx(listStyle!!.optDouble("groupCornerRadius")).toFloat()
      else dp(12).toFloat()
    val top = container.has("cornerRadius") || position in setOf("first", "single")
    val bottom = container.has("cornerRadius") || position in setOf("last", "single")
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
    views.recycle()
  }

  override fun dispose() {
    recycle()
    views.dispose()
  }
}
