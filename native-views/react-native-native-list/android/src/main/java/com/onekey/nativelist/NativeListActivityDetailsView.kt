package com.margelo.nitro.nativelist

import android.graphics.drawable.GradientDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.facebook.react.uimanager.ThemedReactContext
import kotlin.math.roundToInt
import org.json.JSONObject

/** Bounded rich activity fields; no wallet or transaction semantics live here. */
internal class NativeListActivityDetailsView(private val reactContext: ThemedReactContext) : LinearLayout(reactContext) {
  var onAction: ((String, View, String, Int) -> Unit)? = null
  private var sourceScale = false
  private val main = LinearLayout(context).apply { orientation = HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
  private val visual = NativeListLeadingVisual(reactContext)
  private val identity = LinearLayout(context).apply { orientation = VERTICAL }
  private val titleLine = LinearLayout(context).apply { orientation = HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
  private val title = NativeListTextView(context)
  private val description = NativeListTextView(context)
  private val status = NativeListTextView(context)
  private val amounts = LinearLayout(context).apply { orientation = VERTICAL }
  private val fee = LinearLayout(context).apply { orientation = VERTICAL; gravity = Gravity.END }
  private val feeLabel = NativeListTextView(context)
  private val feeLine = LinearLayout(context).apply { orientation = HORIZONTAL; gravity = Gravity.END }
  private val feePrimary = NativeListTextView(context)
  private val feeSecondary = NativeListTextView(context)
  private val actions = LinearLayout(context).apply { orientation = HORIZONTAL }
  private val amountViews = mutableListOf<AmountView>()
  private val badges = mutableListOf<View>()

  init {
    orientation = VERTICAL
    titleLine.addView(title, LayoutParams(0, -2, 1f))
    identity.addView(titleLine)
    identity.addView(description)
    identity.addView(status)
    feeLine.addView(feePrimary); feeLine.addView(feeSecondary)
    fee.addView(feeLabel); fee.addView(feeLine)
    main.addView(visual)
    main.addView(identity, LayoutParams(0, -2, 1f))
    main.addView(amounts)
    main.addView(fee)
    addView(main, LayoutParams(-1, -2))
    addView(actions, LayoutParams(-1, -2))
  }
  private fun dp(value: Int) = if (sourceScale) (value * resources.displayMetrics.density).roundToInt() else NativeListScale.dp(resources, value)
  private fun px(value: Double) = (value * resources.displayMetrics.density).roundToInt()
  private fun color(tone: String, theme: JSONObject?): Int {
    val (key, fallback) = when (tone) {
      "positive", "success" -> "positive" to "#218358"
      "negative", "danger" -> "negative" to "#CE2C31"
      "secondary" -> "secondaryText" to "#646464"
      "warning" -> "warning" to "#AB6400"
      "info" -> "info" to "#007BEF"
      else -> "primaryText" to "#202020"
    }
    return parseNativeListColor(theme?.optString(key, fallback) ?: fallback)
  }
  private fun text(label: NativeListTextView, value: String, style: JSONObject?, size: Float,
                   ink: Int, line: Int, weight: String = "regular", lines: Int = 1) {
    NativeListResolvedText.resolve(context, value, style, size, weight, ink, line, lines, sourceScale).bind(label)
  }
  fun bind(item: NativeListItem, theme: JSONObject?, sourceScale: Boolean) {
    this.sourceScale = sourceScale
    val data = item.json
    val style = data.optJSONObject("style") ?: JSONObject()
    val image = style.optJSONObject("image") ?: JSONObject()
    val table = data.optString("presentation") == "table"
    val gap = if (style.has("leadingGap")) px(style.optDouble("leadingGap")) else dp(12)
    val iw = if (image.has("width")) px(image.optDouble("width")) else dp(40)
    visual.layoutParams = LayoutParams(iw, if (image.has("height")) px(image.optDouble("height")) else dp(40)).apply { marginEnd = gap }
    visual.bind(data.optJSONObject("leading") ?: JSONObject(), image, item.key, theme, false, sourceScale, data.optJSONObject("secondaryLeading"))
    amounts.layoutParams = if (table) LayoutParams(0, -2, 1f).apply { marginStart = gap } else LayoutParams(-2, -2).apply { marginStart = gap }
    text(title, data.optString("title"), style.optJSONObject("title"), 16f, color("primary", theme), 24, "medium")
    text(description, data.optString("description"), style.optJSONObject("description"), 14f, color("secondary", theme), 20, lines = 2)
    text(status, data.optString("status"), style.optJSONObject("status"), 12f, color("secondary", theme), 16)
    description.setOnClickListener(null)
    val descriptionKey = data.optString("descriptionActionKey")
    description.isClickable = descriptionKey.isNotEmpty()
    if (descriptionKey.isNotEmpty()) description.setOnClickListener { onAction?.invoke(descriptionKey, description, "description", 0) }
    for (view in listOf(description, status)) (view.layoutParams as LayoutParams).topMargin = if (style.has("lineGap")) px(style.optDouble("lineGap")) else 0
    badges.forEach(titleLine::removeView); badges.clear()
    val badgeData = data.optJSONArray("badges")
    for (index in 0 until (badgeData?.length() ?: 0)) {
      val descriptor = badgeData!!.getJSONObject(index)
      val label = NativeListTextView(context)
      val ink = color(descriptor.optString("tone"), theme)
      text(label, descriptor.optString("text"), null, 12f, ink, 16, "medium")
      label.setPadding(dp(4), 0, dp(4), 0)
      label.background = GradientDrawable().apply { setColor((ink and 0x00ffffff) or 0x19000000); cornerRadius = dp(4).toFloat() }
      titleLine.addView(label, LayoutParams(-2, -2).apply { marginStart = if (style.has("titleBadgeGap")) px(style.optDouble("titleBadgeGap")) else dp(4) })
      badges.add(label)
    }
    val amountData = data.optJSONArray("amounts")
    val count = amountData?.length() ?: 0
    while (amountViews.size > count) { val child = amountViews.removeAt(amountViews.lastIndex); child.recycle(); amounts.removeView(child) }
    while (amountViews.size < count) { val child = AmountView(); amountViews.add(child); amounts.addView(child) }
    for (index in 0 until count) {
      val descriptor = amountData!!.getJSONObject(index)
      amountViews[index].bind(descriptor, "${item.key}:${descriptor.optString("key")}", style, theme, table)
      amountViews[index].layoutParams = LayoutParams(-1, -2).apply { topMargin = if (index == 0) 0 else if (style.has("trailingGap")) px(style.optDouble("trailingGap")) else dp(2) }
    }
    val feeData = data.optJSONObject("fee")
    fee.visibility = if (feeData == null) GONE else VISIBLE
    fee.alpha = if (feeData?.optBoolean("hidden") == true) 0f else 1f
    fee.layoutParams = LayoutParams(-2, -2).apply { marginStart = gap }
    text(feeLabel, feeData?.optString("label") ?: "", style.optJSONObject("secondaryAmount"), 12f, color("secondary", theme), 16)
    text(feePrimary, feeData?.optString("primary") ?: "", style.optJSONObject("secondaryAmount"), 14f, color("primary", theme), 20)
    text(feeSecondary, feeData?.optString("secondary") ?: "", style.optJSONObject("secondaryAmount"), 12f, color("secondary", theme), 16)
    (feeSecondary.layoutParams as LayoutParams).marginStart = dp(4)
    applySegments(feePrimary, feeData?.optJSONArray("primaryTextSegments"), style.optJSONObject("secondaryAmount")?.optDouble("fontSize", 14.0) ?: 14.0)
    applySegments(feeSecondary, feeData?.optJSONArray("secondaryTextSegments"), style.optJSONObject("secondaryAmount")?.optDouble("fontSize", 12.0) ?: 12.0)
    actions.removeAllViews()
    val actionData = data.optJSONArray("footerActions")
    actions.visibility = if ((actionData?.length() ?: 0) == 0) GONE else VISIBLE
    actions.layoutParams = LayoutParams(-1, -2).apply { marginStart = iw + gap; topMargin = dp(8) }
    for (index in 0 until (actionData?.length() ?: 0)) {
      val descriptor = actionData!!.getJSONObject(index)
      val button = NativeListTextView(context)
      text(button, descriptor.optString("label"), null, 14f, color(descriptor.optString("tone"), theme), 20, "medium")
      button.setPadding(dp(8), dp(4), dp(8), dp(4))
      button.background = GradientDrawable().apply { setColor(parseNativeListColor(theme?.optString("strongBackground", "#F0F0F0") ?: "#F0F0F0")); cornerRadius = dp(8).toFloat() }
      button.isEnabled = !data.optBoolean("disabled") && !descriptor.optBoolean("disabled")
      button.setOnClickListener { onAction?.invoke(descriptor.optString("key"), button, "footerAction", index) }
      actions.addView(button, LayoutParams(-2, -2).apply { marginEnd = dp(8) })
    }
  }
  private fun applySegments(label: NativeListTextView, segments: org.json.JSONArray?, fontSize: Double) {
    if (segments == null || segments.length() == 0) return
    val fullText = (0 until segments.length()).joinToString("") { segments.getJSONObject(it).optString("text") }
    val styled = if (label.text.toString() == fullText) SpannableStringBuilder(label.text) else SpannableStringBuilder(fullText)
    var start = 0
    for (index in 0 until segments.length()) {
      val segment = segments.getJSONObject(index)
      val end = start + segment.optString("text").length
      if (segment.optString("style") == "subscript") {
        // Match NumberSizeableText: shrink the zero count without moving its baseline.
        val size = kotlin.math.ceil(fontSize * 0.6) * label.textSize / fontSize
        styled.setSpan(AbsoluteSizeSpan(size.roundToInt()), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
      }
      start = end
    }
    label.text = styled
  }
  fun recycle() { visual.recycle(); amountViews.forEach { it.recycle() }; description.setOnClickListener(null); actions.removeAllViews() }
  fun dispose() { recycle(); visual.dispose(); amountViews.forEach { it.dispose() } }

  private inner class AmountView : LinearLayout(context) {
    private val leading = NativeListLeadingVisual(reactContext)
    private val labels = LinearLayout(context).apply { orientation = VERTICAL }
    private val primary = NativeListTextView(context)
    private val secondary = NativeListTextView(context)
    init {
      orientation = HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
      labels.addView(primary); labels.addView(secondary)
      addView(leading); addView(labels, LayoutParams(0, -2, 1f))
    }
    fun bind(data: JSONObject, key: String, style: JSONObject, theme: JSONObject?, table: Boolean) {
      val visualData = data.optJSONObject("leading")
      leading.visibility = if (visualData == null) GONE else VISIBLE
      leading.layoutParams = LayoutParams(dp(20), dp(20)).apply { marginEnd = dp(6) }
      if (visualData != null) leading.bind(visualData, JSONObject().put("width", 20).put("height", 20), key, theme, false, sourceScale) else leading.recycle()
      val primaryStyle = JSONObject(style.optJSONObject("primaryAmount")?.toString() ?: "{}")
      if (!primaryStyle.has("alignment")) primaryStyle.put("alignment", if (table) "start" else "end")
      text(primary, data.optString("text"), primaryStyle, 16f, color(data.optString("tone"), theme), 24, "medium")
      applySegments(primary, data.optJSONArray("textSegments"), primaryStyle.optDouble("fontSize", 16.0))
      val secondaryStyle = JSONObject(style.optJSONObject("secondaryAmount")?.toString() ?: "{}")
      if (!secondaryStyle.has("alignment")) secondaryStyle.put("alignment", if (table) "start" else "end")
      text(secondary, data.optString("secondaryText"), secondaryStyle, 14f, color("secondary", theme), 20)
      applySegments(secondary, data.optJSONArray("secondaryTextSegments"), secondaryStyle.optDouble("fontSize", 14.0))
    }
    fun recycle() { leading.recycle() }
    fun dispose() { leading.dispose() }
  }
}
