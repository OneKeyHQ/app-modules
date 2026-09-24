package com.margelo.nitro.nativelist

import android.view.Gravity
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONArray
import org.json.JSONObject

internal class NativeListActionRowView(context: ThemedReactContext) :
  NativeListRendererRowView(context) {
  private val visual = NativeListLeadingVisual(context)
  private val title = NativeListTextView(context)
  private val accessories = NativeListAccessoryStack(context)
  override val assetFields = listOf("icon")

  init {
    visual.glyphSize = 24
    visual.iconBorder = false
    orientation = HORIZONTAL
    gravity = Gravity.CENTER_VERTICAL
    addView(visual)
    addView(title, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
    addView(accessories)
    accessories.onAction = { key, view, slot, target ->
      emitAction(key, view, "trailingAccessory", slot, target, accessories.anchorInset(view))
    }
  }

  private val titlePaintFlags = title.paintFlags
  private val fallbackPaintFlags = visual.fallbackTextView.paintFlags

  override fun defaultHeight(item: NativeListItem, layout: String) =
    when {
      item.json.optString("presentation") == "accountSelector" -> 48
      item.json.has("icon") -> 60
      else -> 44
    }

  override fun bindContent(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    val style = item.json.optJSONObject("style") ?: JSONObject()
    val icon = item.json.optJSONObject("icon")
    val alignment = style.optJSONObject("container")?.optString("contentVerticalAlignment")
    gravity =
      when (alignment) {
        "top" -> Gravity.TOP
        "bottom" -> Gravity.BOTTOM
        else -> Gravity.CENTER_VERTICAL
      }
    val selector = item.json.optString("presentation") == "accountSelector"
    visual.roundedGlyphOrigin = selector && item.hasExplicitHeight
    val descriptors =
      item.json.optJSONObject("checkbox")?.let { JSONArray().put(it) }
        ?: item.json.optJSONArray("trailing")
        ?: JSONArray()
    accessories.bind(item, descriptors, theme, style, sourceScale, checkboxState)
    // Legacy: overhanging selector icons/checkboxes must not be clipped by row padding.
    clipToPadding = !accessories.requestsUnclippedHost
    val hp =
      if (style.has("horizontalPadding")) stylePx(style.optDouble("horizontalPadding"))
      else dp(if (layout == "table") 16 else 12)
    val vp =
      if (style.has("verticalPadding")) stylePx(style.optDouble("verticalPadding")) else dp(8)
    setPadding(hp, vp, if (style.has("horizontalPadding")) hp else accessories.endInset ?: hp, vp)
    val titleParams = title.layoutParams as LayoutParams
    // Legacy: the weighted title abuts the trailing accessories without a gap.
    titleParams.marginEnd = 0
    title.layoutParams = titleParams
    val tone = item.json.optString("tone")
    val token =
      if (selector && tone != "primary") "secondaryText"
      else if (!selector && tone == "danger") "negative" else "primaryText"
    val fallback =
      if (token == "negative") "#C40006D3"
      else if (selector || token == "secondaryText") "#0000009B" else "#000000DF"
    NativeListResolvedText.resolve(
        context,
        item.json.optString("title"),
        style.optJSONObject("title"),
        16f,
        if (selector && icon == null) "regular" else "medium",
        parseNativeListColor(theme?.optString(token, fallback) ?: fallback),
        24,
        1,
        sourceScale,
      )
      .bind(title)
    title.paintFlags = titlePaintFlags
    // Legacy selector typography covered every text in the row, including the
    // trailing accessory text; explicit style fontSize/lineHeight stay as set.
    if (selector && sourceScale) {
      applyNativeListSourceTypography(title, style.optJSONObject("title"))
      accessories.applySourceTypography(style)
    }
    visual.visibility = if (icon == null) GONE else VISIBLE
    // Legacy: the 1px icon outline is kept only for a non-selector icon with an
    // explicit background; selector icons use a plain 8dp rounded fill.
    visual.iconBorder = !selector && icon?.has("backgroundColor") == true
    if (icon != null) {
      val source = JSONObject(icon.toString())
      if (!source.has("backgroundColor"))
        source.put(
          "backgroundColor",
          if (selector) theme?.optString("strongBackground", "#0000000F") ?: "#0000000F"
          else "#00000000",
        )
      val image = JSONObject(style.optJSONObject("image")?.toString() ?: "{}")
      if (selector && !image.has("cornerRadius") && !image.has("shape"))
        image.put("cornerRadius", scaledDp(8f) / resources.displayMetrics.density)
      visual.layoutParams =
        LayoutParams(
            if (image.has("width")) stylePx(image.optDouble("width"))
            else dp(if (selector) 32 else 40),
            if (image.has("height")) stylePx(image.optDouble("height"))
            else dp(if (selector) 32 else 40),
          )
          .apply {
            marginEnd =
              if (style.has("leadingGap")) stylePx(style.optDouble("leadingGap"))
              // Legacy: Yoga rounds the cumulative selector edges, not each gap.
              else if (selector && item.hasExplicitHeight) dp(56) - dp(12) - dp(32)
              else dp(12)
          }
      visual.bind(source, image, item.key, theme, false, sourceScale)
    } else visual.recycle()
    visual.fallbackTextView.paintFlags = fallbackPaintFlags
    // Legacy selector typography also covered the leading fallback text.
    if (selector && sourceScale && icon != null)
      applyNativeListSourceTypography(visual.fallbackTextView, null)
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    super.onLayout(changed, l, t, r, b)
    val item = tag as? NativeListItem ?: return
    if (
      item.hasExplicitHeight &&
        item.json.optString("presentation") == "accountSelector" &&
        item.json
          .optJSONObject("style")
          ?.optJSONObject("container")
          ?.has("contentVerticalAlignment") != true
    ) {
      for (view in listOf(title, accessories)) if (view.visibility != GONE)
        view.offsetTopAndBottom(
          paddingTop + (height - paddingTop - paddingBottom - view.height + 1) / 2 - view.top
        )
    }
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
  }

  override fun disposeContent() {
    visual.dispose()
    accessories.reset()
  }
}
