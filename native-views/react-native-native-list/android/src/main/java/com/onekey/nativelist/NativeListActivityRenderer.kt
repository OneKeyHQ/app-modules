package com.margelo.nitro.nativelist

import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.widget.LinearLayout
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONObject

internal class NativeListActivityRowView(context: ThemedReactContext) :
  NativeListRendererRowView(context) {
  private val details = NativeListActivityDetailsView(context)
  private val content =
    LinearLayout(context).apply {
      orientation = HORIZONTAL
      gravity = Gravity.CENTER_VERTICAL
    }
  private val visual = NativeListLeadingVisual(context)
  private val column = LinearLayout(context).apply { orientation = VERTICAL }
  private val titleLine =
    LinearLayout(context).apply {
      orientation = HORIZONTAL
      gravity = Gravity.CENTER_VERTICAL
    }
  private val title = NativeListTextView(context)
  private val description = NativeListTextView(context)
  private val status = NativeListTextView(context)
  private val failure = NativeListTextView(context)
  private val amounts =
    LinearLayout(context).apply {
      orientation = VERTICAL
      gravity = Gravity.END
    }
  private val values = List(2) { NativeListTextView(context) }
  private val actions = LinearLayout(context).apply { orientation = HORIZONTAL }
  private val buttons = List(3) { NativeListTextView(context) }
  override val assetFields = listOf("leading", "secondaryLeading", "amounts")

  init {
    titleLine.addView(title, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
    titleLine.addView(failure)
    column.addView(titleLine)
    column.addView(description)
    column.addView(status)
    values.forEach(amounts::addView)
    buttons.forEach(actions::addView)
    content.addView(visual)
    content.addView(column, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
    content.addView(amounts)
    addView(content, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    addView(actions)
    addView(details, LayoutParams(-1, -2))
    details.visibility = GONE
    details.onAction = { key, view, source, slot -> emitAction(key, view, source, slot) }
  }

  override fun defaultHeight(item: NativeListItem, layout: String) =
    if (item.json.has("amounts")) 0 else if ((item.json.optJSONArray("footerActions")?.length() ?: 0) > 0) 104 else 60

  override fun bindContent(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    val data = item.json
    val rich = data.has("amounts")
    details.visibility = if (rich) VISIBLE else GONE
    content.visibility = if (rich) GONE else VISIBLE
    actions.visibility = if (rich) GONE else VISIBLE
    if (rich) {
      orientation = VERTICAL
      val style = data.optJSONObject("style") ?: JSONObject()
      val hp = if (style.has("horizontalPadding")) stylePx(style.optDouble("horizontalPadding")) else dp(if (data.optString("presentation") == "table") 16 else 12)
      val vp = if (style.has("verticalPadding")) stylePx(style.optDouble("verticalPadding")) else dp(8)
      setPadding(hp, vp, hp, vp)
      visual.recycle()
      details.bind(item, theme, sourceScale)
      return
    }
    details.recycle()
    val style = data.optJSONObject("style") ?: JSONObject()
    val image = style.optJSONObject("image") ?: JSONObject()
    fun color(name: String, fallback: String) =
      parseNativeListColor(theme?.optString(name, fallback) ?: fallback)
    fun fill(value: Int, radius: Int) =
      GradientDrawable().apply {
        setColor(value)
        cornerRadius = dp(radius).toFloat()
      }
    fun bind(
      view: NativeListTextView,
      value: String,
      slot: String,
      size: Float,
      weight: String = "regular",
      ink: Int = color("secondaryText", "#0000009B"),
      line: Int = 0,
      lines: Int = 1,
      align: String = "start",
    ) {
      val textStyle =
        JSONObject(style.optJSONObject(slot)?.toString() ?: "{}").apply {
          if (!has("alignment")) put("alignment", align)
        }
      NativeListResolvedText.resolve(
          context,
          value,
          textStyle,
          size,
          weight,
          ink,
          line,
          lines,
          sourceScale,
        )
        .bind(view)
      view.ellipsize =
        if (textStyle.optString("truncate") == "clip") null else TextUtils.TruncateAt.END
    }
    orientation = VERTICAL
    val hasFooterActions = (data.optJSONArray("footerActions")?.length() ?: 0) > 0
    gravity =
      when (style.optJSONObject("container")?.optString("contentVerticalAlignment")) {
        "bottom" -> Gravity.BOTTOM
        "top" -> Gravity.TOP
        "center" -> Gravity.CENTER_VERTICAL
        // Legacy: the footer-action column is top/start aligned.
        else -> if (hasFooterActions) Gravity.START else Gravity.CENTER_VERTICAL
      }
    content.gravity =
      when (style.optJSONObject("container")?.optString("contentVerticalAlignment")) {
        "bottom" -> Gravity.BOTTOM
        "top" -> Gravity.TOP
        else -> Gravity.CENTER_VERTICAL
      }
    val hp =
      if (style.has("horizontalPadding")) stylePx(style.optDouble("horizontalPadding"))
      else dp(if (layout == "table") 16 else 12)
    val vp =
      if (style.has("verticalPadding")) stylePx(style.optDouble("verticalPadding")) else dp(8)
    setPadding(hp, vp, hp, vp)
    val iw = if (image.has("width")) stylePx(image.optDouble("width")) else dp(40)
    val ih = if (image.has("height")) stylePx(image.optDouble("height")) else dp(40)
    val gap = if (style.has("leadingGap")) stylePx(style.optDouble("leadingGap")) else dp(12)
    visual.layoutParams = LayoutParams(iw, ih).apply { marginEnd = gap }
    visual.bind(
      // Legacy: a missing visual still reserves an empty, transparent slot.
      data.optJSONObject("leading") ?: JSONObject().put("backgroundColor", "#00000000"),
      image,
      item.key,
      theme,
      false,
      sourceScale,
      data.optJSONObject("secondaryLeading"),
    )
    bind(
      title,
      data.optString("title"),
      "title",
      16f,
      "medium",
      color("primaryText", "#000000DF"),
      24,
    )
    bind(description, data.optString("description"), "description", 14f, line = 20, lines = 2)
    val failed = data.optString("status") == "Failed"
    bind(status, if (failed) "" else data.optString("status"), "status", 12f)
    bind(
      failure,
      if (failed) "Failed" else "",
      "status",
      12f,
      "medium",
      color("negative", "#C40006D3"),
    )
    failure.background = fill(color("criticalBackground", "#F3000D14"), 4)
    failure.setPadding(dp(8), dp(2), dp(8), dp(2))
    (failure.layoutParams as LayoutParams).apply {
      marginStart =
        if (style.has("titleBadgeGap")) stylePx(style.optDouble("titleBadgeGap")) else 0
    }
    for (view in listOf(description, status)) (view.layoutParams as LayoutParams).apply {
      topMargin = if (style.has("lineGap")) stylePx(style.optDouble("lineGap")) else 0
    }
    for ((index, slot) in listOf("primaryAmount", "secondaryAmount").withIndex()) {
      val value = data.optString(slot)
      bind(
        values[index],
        value,
        slot,
        if (index == 0) 16f else 14f,
        if (index == 0) "medium" else "regular",
        if (index == 0)
          color(
            if (value.startsWith("+")) "positive" else "primaryText",
            if (value.startsWith("+")) "#00713FDE" else "#000000DF",
          )
        // Legacy: both amounts default to primaryText; only "+" amounts are positive.
        else color("primaryText", "#000000DF"),
        line = if (index == 0) 24 else 20,
        align = "end",
      )
      values[index].layoutParams =
        LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
          if (index > 0)
            topMargin = if (style.has("trailingGap")) stylePx(style.optDouble("trailingGap")) else 0
        }
    }
    val descriptors = data.optJSONArray("footerActions")
    val count = minOf(3, descriptors?.length() ?: 0)
    actions.visibility = if (count == 0) GONE else VISIBLE
    actions.layoutParams =
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
        marginStart = if (style.has("leadingGap") || image.has("width")) iw + gap else dp(52)
        topMargin = if (style.has("lineGap")) stylePx(style.optDouble("lineGap")) else dp(8)
        bottomMargin = dp(4)
      }
    for ((index, button) in buttons.withIndex()) {
      button.visibility = if (index < count) VISIBLE else GONE
      button.setOnClickListener(null)
      if (index >= count) continue
      val action = descriptors!!.getJSONObject(index)
      NativeListResolvedText.resolve(
          context,
          action.optString("label"),
          null,
          // Legacy footer action: 14sp semibold on a 20dp line.
          14f,
          "semibold",
          color(
            if (action.optString("tone") == "danger") "negative" else "primaryText",
            if (action.optString("tone") == "danger") "#C40006D3" else "#000000DF",
          ),
          20,
          1,
          sourceScale,
        )
        .bind(button)
      button.background = fill(color("strongBackground", "#0000000F"), 8)
      button.setPadding(dp(8), dp(4), dp(8), dp(4))
      button.isEnabled = !data.optBoolean("disabled") && !action.optBoolean("disabled")
      button.layoutParams =
        LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
          marginEnd = dp(8)
        }
      button.setOnClickListener {
        emitAction(action.optString("key"), button, "footerAction", index)
      }
    }
  }

  override fun recycleContent() {
    visual.recycle()
    details.recycle()
    buttons.forEach { it.setOnClickListener(null) }
  }

  override fun disposeContent() {
    visual.dispose()
    details.dispose()
    buttons.forEach { it.setOnClickListener(null) }
  }
}
