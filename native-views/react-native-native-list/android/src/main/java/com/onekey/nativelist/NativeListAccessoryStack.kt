package com.margelo.nitro.nativelist

// OneKey patch: selector checkboxes share the source React Native border/background renderer.
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.facebook.react.uimanager.BackgroundStyleApplicator
import com.facebook.react.uimanager.LengthPercentage
import com.facebook.react.uimanager.LengthPercentageType
import com.facebook.react.uimanager.style.BorderRadiusProp
import com.facebook.react.uimanager.style.LogicalEdge
import kotlin.math.roundToInt
import org.json.JSONArray
import org.json.JSONObject

// Bounded controls only. Event ownership and epochs stay with the row host.
internal class NativeListAccessoryStack(context: android.content.Context) : LinearLayout(context) {
  private val trailingViews = List(2) { NativeListTextView(context) }
  private val trailingIcons = List(2) { OneKeyIconView(context) }
  private val checkbox = OneKeyCheckboxView(context)
  private val spinner = ProgressBar(context)
  private var boundCheckboxData: JSONObject? = null
  private val semanticValueViews = mutableListOf<TextView>()
  private var selectorUsesSourceScale = false
  private var checkboxUsesSelectorStyle = false
  private var checkboxCheckedColor = Color.BLACK
  private var checkboxUncheckedColor = Color.WHITE
  private var checkboxIconColor = Color.WHITE
  private var checkboxBorderColor = Color.LTGRAY
  private var iconSubduedColor = Color.GRAY
  val firstVisibleIcon: View?
    get() = trailingIcons.firstOrNull { it.visibility == VISIBLE }

  val hasCheckbox: Boolean
    get() = checkbox.visibility == VISIBLE

  val visibleValues: List<TextView>
    get() = trailingViews.filter { it.visibility == VISIBLE }

  var endInset: Int? = null
  var onAction: ((String, View, Int?, NativeSelectionTarget?) -> Unit)? = null
  private val selectorAccessibilityDelegate =
    object : View.AccessibilityDelegate() {
      override fun onInitializeAccessibilityNodeInfo(
        host: View,
        info: android.view.accessibility.AccessibilityNodeInfo,
      ) {
        super.onInitializeAccessibilityNodeInfo(host, info)
        info.viewIdResourceName = host.getTag(com.facebook.react.R.id.react_test_id) as? String
      }
    }

  init {
    orientation = VERTICAL
    gravity = Gravity.END or Gravity.CENTER_VERTICAL
    trailingViews.forEach { addView(it) }
    trailingIcons.forEach { addView(it) }
    addView(checkbox)
    addView(spinner)
  }

  fun bind(
    item: NativeListItem,
    descriptors: JSONArray,
    theme: JSONObject?,
    style: JSONObject,
    sourceScale: Boolean,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
    horizontal: Boolean = false,
  ) {
    selectorUsesSourceScale = sourceScale
    reset()
    tag = item
    checkboxCheckedColor = color(theme, "primaryText", "#1D1D1D")
    checkboxUncheckedColor = color(theme, "inverseText", "#FCFCFC")
    checkboxIconColor = checkboxUncheckedColor
    checkboxBorderColor = Color.argb(0x31, 0, 0, 0)
    iconSubduedColor = color(theme, "iconSubdued", "#00000072")
    checkboxUsesSelectorStyle = item.json.optString("presentation") == "networkSelector"
    if (checkboxUsesSelectorStyle) {
      checkboxCheckedColor = color(theme, "checkboxBackground", "#202020")
      checkboxBorderColor = color(theme, "checkboxBorder", "#00000031")
      checkboxIconColor = color(theme, "checkboxIcon", "#FFFFFF")
      checkboxUncheckedColor = checkboxIconColor
    }
    val kinds =
      (0 until descriptors.length()).map { descriptors.getJSONObject(it).optString("kind") }
    orientation = if (horizontal) HORIZONTAL else VERTICAL
    gravity = Gravity.END or Gravity.CENTER_VERTICAL
    trailingViews.forEach { it.setTextColor(checkboxCheckedColor) }
    bindAccessories(item, descriptors, theme, checkboxState)
    if (horizontal && "checkbox" in kinds)
      (checkbox.layoutParams as LayoutParams).marginStart = dp(12)
    for ((index, key) in listOf("value", "valueSecondary").withIndex()) {
      style.optJSONObject(key)?.let { value ->
        semanticValueViews.getOrNull(index)?.let { applyStyledText(it, value) }
      }
    }
    if (style.has("trailingGap")) {
      var previous = false
      for (i in 0 until childCount) {
        val v = getChildAt(i)
        if (v.visibility != GONE) {
          val params = v.layoutParams as LayoutParams
          if (orientation == HORIZONTAL)
            params.marginStart = if (previous) styleDp(style.optDouble("trailingGap")) else 0
          else params.topMargin = if (previous) styleDp(style.optDouble("trailingGap")) else 0
          previous = true
        }
      }
    }
    visibility =
      if ((0 until childCount).any { getChildAt(it).visibility != GONE }) VISIBLE else GONE
  }

  fun updateSelection(
    item: NativeListItem,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    tag = item
    if (boundCheckboxData != null) {
      val descriptors = item.json.optJSONArray("trailing")
      val latest =
        (0 until (descriptors?.length() ?: 0))
          .mapNotNull { descriptors?.optJSONObject(it) }
          .lastOrNull { it.optString("kind") == "checkbox" } ?: item.json.optJSONObject("checkbox")
      latest?.let { bindCheckbox(item, it, checkboxState) }
    }
  }

  fun anchorInset(view: View): Int =
    if (
      (tag as? NativeListItem)?.json?.optString("presentation") == "accountSelector" &&
        view in trailingIcons &&
        (view.layoutParams as MarginLayoutParams).marginStart < 0
    )
      dp(7)
    else 0

  fun reset() {
    tag = null
    endInset = null
    semanticValueViews.clear()
    boundCheckboxData = null
    trailingViews.forEach { v ->
      v.visibility = GONE
      v.text = ""
      v.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
      v.setOnClickListener(null)
      v.background = null
      v.setPadding(0, 0, 0, 0)
      v.gravity = Gravity.END or Gravity.CENTER_VERTICAL
      v.opticalOffsetY = 0f
      v.maxLines = 1
      v.setHorizontallyScrolling(false)
      v.ellipsize = TextUtils.TruncateAt.END
      v.setLineSpacing(0f, 1f)
      v.letterSpacing = 0f
    }
    trailingIcons.forEach {
      it.visibility = GONE
      it.setOnClickListener(null)
      it.background = null
      it.useSourceScale = selectorUsesSourceScale
      it.glyphSizeDp = null
      it.alpha = 1f
    }
    checkbox.visibility = GONE
    checkbox.setOnClickListener(null)
    checkbox.alpha = 1f
    checkbox.layoutParams = LayoutParams(dp(20), dp(20))
    spinner.visibility = GONE
    spinner.alpha = 1f
    spinner.layoutParams = LayoutParams(dp(20), dp(20))
  }

  private fun emitAction(
    item: NativeListItem,
    key: String,
    target: NativeSelectionTarget?,
    view: View,
    source: String,
    slot: Int? = null,
  ) {
    if (!item.json.optBoolean("disabled")) onAction?.invoke(key, view, slot, target)
  }

  private fun dp(value: Int) =
    if (selectorUsesSourceScale) (value * resources.displayMetrics.density).roundToInt()
    else NativeListScale.dp(resources, value)

  private fun scaledDp(value: Float) =
    if (selectorUsesSourceScale) value * resources.displayMetrics.density
    else NativeListScale.dp(resources, value)

  private fun sp(value: Float) =
    if (selectorUsesSourceScale) value else NativeListScale.font(resources, value)

  private fun styleDp(value: Double) = (value * resources.displayMetrics.density).roundToInt()

  private fun bindAccessories(
    item: NativeListItem,
    accessories: JSONArray?,
    theme: JSONObject?,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    if (accessories == null) return
    var textIndex = 0
    for (index in 0 until minOf(2, accessories.length())) {
      val accessory = accessories.getJSONObject(index)
      when (accessory.optString("kind")) {
        "value" -> {
          showTrailing(
            textIndex,
            accessory.optString("text"),
            !accessory.optBoolean("secondary", false),
          )
          semanticValueViews.add(trailingViews[textIndex])
          applyValueSegments(trailingViews[textIndex], accessory.optJSONArray("textSegments"))
          textIndex++
        }
        "valuePair" -> {
          showTrailingValuePair(textIndex, accessory, theme)
          semanticValueViews.add(trailingViews[textIndex++])
        }
        "checkbox" -> bindCheckbox(item, accessory, checkboxState)
        "radio" ->
          showTrailing(
            textIndex++,
            if (accessory.optBoolean("checked")) "●" else "○",
            true,
            accessory.optString("actionKey").takeUnless { accessory.optBoolean("disabled", false) },
          )
        "switch" ->
          showTrailing(
            textIndex++,
            if (accessory.optBoolean("value")) "ON" else "OFF",
            true,
            accessory.optString("actionKey").takeUnless { accessory.optBoolean("disabled", false) },
          )
        "chevron" ->
          showTrailingIcon(
            textIndex++,
            item,
            JSONObject(accessory.toString()).apply {
              put("name", "ChevronRightSmallOutline")
              if (optString("actionKey").isEmpty()) put("actionKey", "press")
            },
            theme,
          )
        "menu" -> showTrailingMenu(textIndex++, accessory.optString("actionKey"))
        "drag" ->
          showTrailingIcon(
            textIndex++,
            item,
            JSONObject(accessory.toString()).apply { put("name", "DragOutline") },
            theme,
          )
        "icon" -> showTrailingIcon(textIndex++, item, accessory, theme)
        "spinner" -> spinner.visibility = VISIBLE
        "progress" ->
          showTrailing(textIndex++, "${(accessory.optDouble("value") * 100).toInt()}%", false)
      }
    }
  }

  private fun bindCheckbox(
    item: NativeListItem,
    json: JSONObject,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    boundCheckboxData = json
    val targetJson = json.optJSONObject("target")
    val scope = targetJson?.optString("scope", "row") ?: "row"
    val target =
      NativeSelectionTarget(
        scope,
        when (scope) {
          "section" -> targetJson?.optString("sectionKey")
          "row" -> item.key
          else -> null
        },
      )
    val state = checkboxState(item, target, json.optString("state", "unchecked"))
    val accessoryDisabled = json.optBoolean("disabled", false)
    if (json.optBoolean("loading", false)) {
      checkbox.visibility = GONE
      checkbox.setOnClickListener(null)
      spinner.visibility = VISIBLE
      spinner.alpha =
        if (!item.json.optBoolean("disabled", false) && accessoryDisabled) 0.5f else 1f
      return
    }
    checkbox.visibility = VISIBLE
    checkbox.setState(state, checkboxIconColor)
    val usesSourceCheckboxGeometry = checkboxUsesSelectorStyle && item.json.has("height")
    checkbox.usesSelectorGeometry = usesSourceCheckboxGeometry
    if (usesSourceCheckboxGeometry) {
      // OneKey patch: source Yoga children may round into padding; retain their complete border.
      clipToPadding = false
      // OneKey patch: even a transparent source border changes RN's background clipping path.
      checkbox.background = null
      BackgroundStyleApplicator.setBackgroundColor(
        checkbox,
        if (state == "unchecked") checkboxUncheckedColor else checkboxCheckedColor,
      )
      BackgroundStyleApplicator.setBorderWidth(checkbox, LogicalEdge.ALL, 2f)
      BackgroundStyleApplicator.setBorderColor(
        checkbox,
        LogicalEdge.ALL,
        if (state == "unchecked") checkboxBorderColor else Color.TRANSPARENT,
      )
      BackgroundStyleApplicator.setBorderRadius(
        checkbox,
        BorderRadiusProp.BORDER_RADIUS,
        LengthPercentage(4f, LengthPercentageType.POINT),
      )
    } else {
      checkbox.background =
        if (state == "unchecked") {
          if (checkboxUsesSelectorStyle)
            GradientDrawable().apply {
              setColor(checkboxUncheckedColor)
              setStroke(dp(2), checkboxBorderColor)
              cornerRadius = scaledDp(4f)
            }
          else roundedStroke(checkboxBorderColor, checkboxUncheckedColor, 4f)
        } else {
          roundedFill(checkboxCheckedColor, 4f)
        }
    }
    // Row-level disabled opacity already applies to this child. Only apply a
    // local 0.5 when the accessory alone is disabled, never 0.5 * 0.5.
    checkbox.alpha = if (!item.json.optBoolean("disabled", false) && accessoryDisabled) 0.5f else 1f
    checkbox.isEnabled =
      !item.json.optBoolean("disabled", false) &&
        !accessoryDisabled &&
        !json.optBoolean("loading", false)
    checkbox.setOnClickListener {
      emitAction(
        item,
        json.optString("actionKey", "selection"),
        target,
        checkbox,
        "trailingAccessory",
      )
    }
  }

  private fun showTrailing(
    index: Int,
    value: String,
    primary: Boolean,
    actionKey: String? = null,
    textColor: Int? = null,
  ) {
    if (index !in trailingViews.indices || value.isEmpty()) return
    val view = trailingViews[index]
    view.maxLines = 1
    view.text = value
    view.textSize = sp(if (primary) 16f else 14f)
    view.typeface =
      if (primary) NativeListFonts.medium(context) else NativeListFonts.regular(context)
    view.includeFontPadding = false
    view.fontFeatureSettings = "tnum"
    TextViewCompat.setLineHeight(view, dp(if (primary) 24 else 20))
    textColor?.let(view::setTextColor)
    view.visibility = VISIBLE
    if (!actionKey.isNullOrEmpty()) {
      view.setOnClickListener {
        (tag as? NativeListItem)
          ?.takeUnless { item -> item.json.optBoolean("disabled", false) }
          ?.let { item -> emitAction(item, actionKey, null, view, "trailingAccessory", index) }
      }
    }
  }

  private fun showTrailingValuePair(index: Int, accessory: JSONObject, theme: JSONObject?) {
    if (index !in trailingViews.indices) return
    val primary = accessory.optString("primary")
    val secondary = accessory.optString("secondary")
    if (primary.isEmpty() && secondary.isEmpty()) return
    val value = SpannableStringBuilder()
    val primaryStart = value.length
    value.append(primary)
    value.setSpan(
      ForegroundColorSpan(accessoryTextColor(accessory.optString("primaryTone"), "primary", theme)),
      primaryStart,
      value.length,
      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
    )
    value.setSpan(
      AbsoluteSizeSpan(
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp(16f), resources.displayMetrics)
          .roundToInt()
      ),
      primaryStart,
      value.length,
      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
    )
    if (primary.isNotEmpty() && secondary.isNotEmpty()) value.append('\n')
    val secondaryStart = value.length
    value.append(secondary)
    value.setSpan(
      ForegroundColorSpan(
        accessoryTextColor(accessory.optString("secondaryTone"), "secondary", theme)
      ),
      secondaryStart,
      value.length,
      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
    )
    value.setSpan(
      AbsoluteSizeSpan(
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp(14f), resources.displayMetrics)
          .roundToInt()
      ),
      secondaryStart,
      value.length,
      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
    )
    val view = trailingViews[index]
    view.text = value
    view.maxLines = 2
    view.gravity = Gravity.END
    view.includeFontPadding = false
    view.typeface = NativeListFonts.regular(context)
    view.fontFeatureSettings = "tnum"
    TextViewCompat.setLineHeight(view, dp(20))
    view.visibility = VISIBLE
  }

  private fun showTrailingMenu(index: Int, actionKey: String) {
    showTrailing(index, "⋮", true, actionKey)
    if (index !in trailingViews.indices) return
    trailingViews[index].apply {
      gravity = Gravity.CENTER
      textSize = sp(20f)
      layoutParams =
        LayoutParams(dp(24), dp(24)).apply {
          if (this@NativeListAccessoryStack.orientation == HORIZONTAL && index > 0)
            marginStart = dp(8)
        }
    }
  }

  private fun accessoryTextColor(tone: String, defaultTone: String, theme: JSONObject?): Int =
    when (tone.ifEmpty { defaultTone }) {
      "positive" -> color(theme, "positive", "#00713FDE")
      "negative" -> color(theme, "negative", "#C40006D3")
      "secondary" -> color(theme, "secondaryText", "#0000009B")
      else -> color(theme, "primaryText", "#000000DF")
    }

  private fun showTrailingIcon(
    index: Int,
    item: NativeListItem,
    data: JSONObject,
    theme: JSONObject?,
  ) {
    if (index !in trailingIcons.indices) return
    val icon = trailingIcons[index]
    icon.iconName = data.optString("name")
    icon.tintColor = safeColor(data.optString("tintColor"), iconSubduedColor)
    // OneKey patch: preserve the original 38dp press target around its 24dp layout slot.
    icon.setTag(
      com.facebook.react.R.id.react_test_id,
      data.optString("testID").takeIf { it.isNotEmpty() },
    )
    icon.accessibilityDelegate = selectorAccessibilityDelegate
    icon.contentDescription = data.optString("accessibilityLabel").takeIf { it.isNotEmpty() }
    if (item.json.optString("presentation") == "accountSelector") {
      icon.glyphSizeDp = 24
      val isSourceMenu = item.json.has("height") && icon.iconName == "DotHorOutline"
      val size = if (item.json.has("height") && icon.iconName == "PlusSmallOutline") 36 else 38
      icon.layoutParams =
        LayoutParams(dp(size), dp(size)).apply {
          gravity = Gravity.CENTER_VERTICAL
          marginStart = -dp(7)
          marginEnd = -dp(7)
        }
      // The frame overhangs its 24dp layout slot by 7dp on each side. The column
      // clips children and the row clips to padding by default, which would cut
      // the pressed circle down to a 24dp-wide pill.
      this@NativeListAccessoryStack.clipChildren = false
      clipToPadding = false
      if (isSourceMenu && data.optString("actionKey").isNotEmpty()) {
        icon.background =
          StateListDrawable().apply {
            addState(
              intArrayOf(android.R.attr.state_pressed),
              roundedFill(color(theme, "rowPressedBackground", "#00000017"), 19f),
            )
            addState(intArrayOf(), roundedFill(Color.TRANSPARENT, 19f))
          }
      }
    } else if (icon.iconName == "ChevronRightSmallOutline") {
      icon.glyphSizeDp = null
      // ListItem.DrillIn is a 24dp icon with mx=-6, for a 12dp layout footprint.
      icon.layoutParams =
        LayoutParams(dp(24), dp(24)).apply {
          marginStart = -dp(6)
          marginEnd = -dp(6)
        }
    } else {
      // ListItem.IconButton medium keeps a 24dp glyph in a 36dp frame. Its
      // m=-7 moves the last frame 7dp through the row's trailing padding. For
      // the Bookmark XStack, gap=$6 plus both negative margins leaves 10dp
      // between the physical frames (46dp between glyph centers).
      icon.glyphSizeDp = 24
      endInset = dp(5)
      icon.layoutParams =
        LayoutParams(dp(36), dp(36)).apply {
          gravity = Gravity.END
          if (this@NativeListAccessoryStack.orientation == HORIZONTAL && index > 0) {
            marginStart = dp(10)
          }
        }
    }
    icon.visibility = VISIBLE
    icon.isEnabled = !data.optBoolean("disabled", false)
    icon.alpha = if (icon.isEnabled) 1f else 0.4f
    val actionKey = data.optString("actionKey")
    if (icon.isEnabled && actionKey.isNotEmpty()) {
      icon.setOnClickListener {
        emitAction(item, actionKey, null, icon, "trailingAccessory", index)
      }
    }
  }

  private fun applyValueSegments(
    view: TextView,
    segments: JSONArray?,
    fontSize: Int = 16,
    lineHeight: Int = 24,
    medium: Boolean = true,
  ) {
    if (segments == null || segments.length() == 0) return
    val value = SpannableStringBuilder()
    for (index in 0 until segments.length()) {
      val segment = segments.getJSONObject(index)
      val start = value.length
      value.append(segment.optString("text"))
      if (segment.optString("style") == "subscript") {
        val size = kotlin.math.ceil(fontSize * 0.6).toFloat()
        val span =
          if (selectorUsesSourceScale)
            AbsoluteSizeSpan(
              kotlin.math.ceil((size * resources.displayMetrics.density).toDouble()).toInt(),
              false,
            )
          else AbsoluteSizeSpan(sp(size).roundToInt(), true)
        value.setSpan(span, start, value.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
      }
    }
    view.textSize = sp(fontSize.toFloat())
    view.typeface =
      if (medium) NativeListFonts.medium(context) else NativeListFonts.regular(context)
    TextViewCompat.setLineHeight(view, dp(lineHeight))
    view.text = value
  }

  private fun applyStyledText(view: TextView, style: JSONObject) {
    if (style.has("fontSize")) {
      view.setTextSize(
        TypedValue.COMPLEX_UNIT_PX,
        (style.optDouble("fontSize") * resources.displayMetrics.density).toFloat(),
      )
      val text = SpannableStringBuilder(view.text)
      text.getSpans(0, text.length, AbsoluteSizeSpan::class.java).forEach(text::removeSpan)
      view.text = text
    }
    style.optString("fontWeight").takeIf(String::isNotEmpty)?.let {
      view.typeface = marketTypeface(it, "regular")
    }
    style.optString("color").takeIf(String::isNotEmpty)?.let {
      val color = safeColor(it, view.currentTextColor)
      view.setTextColor(color)
      val text = SpannableStringBuilder(view.text)
      text.getSpans(0, text.length, ForegroundColorSpan::class.java).forEach(text::removeSpan)
      view.text = text
    }
    if (style.has("lineHeight")) {
      val text = SpannableStringBuilder(view.text)
      text.getSpans(0, text.length, NativeListLineHeightSpan::class.java).forEach(text::removeSpan)
      text.setSpan(
        NativeListLineHeightSpan(styleDp(style.optDouble("lineHeight"))),
        0,
        text.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
      )
      view.setLineSpacing(0f, 1f)
      view.text = text
    }
    if (style.has("lines")) {
      view.maxLines = style.optInt("lines").coerceIn(1, 3)
      view.ellipsize = TextUtils.TruncateAt.END
    }
    if (style.has("truncate") || style.has("lines")) {
      view.setHorizontallyScrolling(view.maxLines == 1)
      view.ellipsize =
        if (style.optString("truncate", "tail") == "clip") null else TextUtils.TruncateAt.END
      if (view.maxLines == 1) {
        val text = SpannableStringBuilder(view.text)
        Regex("\\r\\n|[\\r\\n]").findAll(text).toList().asReversed().forEach { match ->
          text.replace(match.range.first, match.range.last + 1, " ")
        }
        view.text = text
      }
    }
    if (style.has("verticalAlignment")) {
      view.gravity =
        (view.gravity and Gravity.VERTICAL_GRAVITY_MASK.inv()) or
          when (style.optString("verticalAlignment")) {
            "top" -> Gravity.TOP
            "bottom" -> Gravity.BOTTOM
            else -> Gravity.CENTER_VERTICAL
          }
    }
    if (style.has("offsetY"))
      (view as? NativeListTextView)?.opticalOffsetY =
        (style.optDouble("offsetY") * resources.displayMetrics.density).toFloat()
    style.optString("alignment").takeIf(String::isNotEmpty)?.let {
      view.gravity =
        (view.gravity and Gravity.VERTICAL_GRAVITY_MASK) or
          when (it) {
            "center" -> Gravity.CENTER_HORIZONTAL
            "end" -> Gravity.END
            else -> Gravity.START
          }
    }
  }

  // OneKey patch: keep a small idle member pool after reuse or a direct rebind.
  // The compact proxy/expansion keeps every member until it leaves that state.

  private fun marketTypeface(weight: String, fallback: String): Typeface =
    when (weight.ifEmpty { fallback }) {
      "regular" -> NativeListFonts.regular(context)
      "semibold" -> NativeListFonts.semibold(context)
      "bold" -> NativeListFonts.bold(context)
      else -> NativeListFonts.medium(context)
    }

  private fun safeColor(value: String?, fallback: Int): Int =
    try {
      if (value.isNullOrEmpty()) fallback else parseNativeListColor(value)
    } catch (_: IllegalArgumentException) {
      fallback
    }

  private fun color(theme: JSONObject?, key: String, fallback: String): Int =
    safeColor(theme?.optString(key, fallback), parseNativeListColor(fallback))

  private fun roundedFill(color: Int, radiusDp: Float) =
    GradientDrawable().apply {
      setColor(color)
      cornerRadius = scaledDp(radiusDp)
    }

  private fun roundedStroke(stroke: Int, fill: Int, radiusDp: Float) =
    GradientDrawable().apply {
      setColor(fill)
      setStroke(dp(2), stroke)
      cornerRadius = scaledDp(radiusDp)
    }
}
