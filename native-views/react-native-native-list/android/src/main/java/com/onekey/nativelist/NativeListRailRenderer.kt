package com.margelo.nitro.nativelist

import android.view.Gravity
import android.widget.LinearLayout
import com.facebook.react.uimanager.ThemedReactContext
import kotlin.math.roundToInt
import org.json.JSONObject

internal class NativeListRailRowView(context: ThemedReactContext) :
  NativeListRendererRowView(context) {
  private val textLine =
    LinearLayout(context).apply {
      orientation = HORIZONTAL
      gravity = Gravity.CENTER_VERTICAL
    }
  private val title = NativeListTextView(context)
  private val badge = NativeListTextView(context)
  private val status = NativeListTextView(context)
  private var visual: NativeListLeadingVisual? = null
  private var leadingWidth = 0
  private var leadingGap = 0
  private var badgeGap = 0
  private var trailingGap = 0
  override val defaultCornerRadius = 8
  override val pressedColorName = "strongBackground"
  override val pressedColorFallback = "#0000000F"
  override val assetFields = listOf("visual")
  override val showsSelection = false

  override fun defaultHeight(item: NativeListItem, layout: String) = 28

  init {
    listOf(title, badge, status).forEach {
      textLine.addView(it, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    }
    addView(textLine, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
  }

  override fun bindContent(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    val style = item.json.optJSONObject("style") ?: JSONObject()
    fun dimension(name: String, fallback: Int) =
      if (style.has(name)) stylePx(style.optDouble(name)) else dp(fallback)
    fun color(name: String, fallback: String) =
      parseNativeListColor(theme?.optString(name, fallback) ?: fallback)
    fun text(
      view: NativeListTextView,
      field: String,
      value: String,
      weight: String,
      tint: Int,
      lineHeight: Int,
    ) {
      NativeListResolvedText.resolve(
          reactContext,
          value,
          style.optJSONObject(field),
          12f,
          weight,
          tint,
          lineHeight,
          1,
          sourceScale,
        )
        .bind(view)
    }
    text(
      title,
      "title",
      item.json.optString("title"),
      "medium",
      color("primaryText", "#000000DF"),
      16,
    )
    val badgeData = item.json.optJSONObject("badge")
    val tone = badgeData?.optString("tone")
    val badgeColor =
      when (tone) {
        "success" -> color("positive", "#00713FDE")
        "danger" -> color("negative", "#C40006D3")
        else -> color("secondaryText", "#0000009B")
      }
    text(badge, "badge", badgeData?.optString("text") ?: "", "medium", badgeColor, 16)
    val statusValue = item.json.optString("status").takeUnless { it == "none" } ?: ""
    text(status, "status", statusValue, "regular", color("secondaryText", "#0000009B"), 0)
    val horizontal = dimension("horizontalPadding", 4)
    val vertical = dimension("verticalPadding", 4)
    setPadding(horizontal, vertical, horizontal, vertical)
    gravity =
      when (style.optJSONObject("container")?.optString("contentVerticalAlignment")) {
        "top" -> Gravity.TOP
        "bottom" -> Gravity.BOTTOM
        else -> Gravity.CENTER_VERTICAL
      }
    leadingGap = dimension("leadingGap", 6)
    badgeGap = dimension("titleBadgeGap", 6)
    trailingGap = dimension("trailingGap", 0)
    badge.layoutParams =
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
        marginStart = badgeGap
      }
    status.layoutParams =
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
        marginStart = trailingGap
      }
    val image = style.optJSONObject("image") ?: JSONObject()
    leadingWidth = if (image.has("width")) stylePx(image.optDouble("width")) else dp(20)
    val imageHeight = if (image.has("height")) stylePx(image.optDouble("height")) else dp(20)
    val source = item.json.optJSONObject("visual")
    if (source != null) {
      val view = visual ?: NativeListLeadingVisual(reactContext).also { visual = it }
      view.bind(source, image, item.key, theme, false, sourceScale)
      val params = LayoutParams(leadingWidth, imageHeight).apply { marginEnd = leadingGap }
      if (view.parent == null) addView(view, 0, params) else view.layoutParams = params
    } else
      visual?.let {
        it.recycle()
        removeView(it)
      }
  }

  // Retain the legacy 6-unit trailing allowance when a status is present.
  override fun horizontalWidth(item: NativeListItem): Int =
    (paddingLeft +
        paddingRight +
        leadingWidth +
        leadingGap +
        title.paint.measureText(title.text.toString()) +
        (if (badge.visibility == VISIBLE) badgeGap + badge.paint.measureText(badge.text.toString())
        else 0f) +
        (if (status.visibility == VISIBLE)
          trailingGap + dp(6) + status.paint.measureText(status.text.toString())
        else 0f))
      .roundToInt()

  override fun recycleContent() {
    visual?.recycle()
  }

  override fun disposeContent() {
    visual?.dispose()
  }
}
