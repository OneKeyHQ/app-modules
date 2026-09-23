package com.margelo.nitro.nativelist

import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.facebook.react.uimanager.ThemedReactContext
import kotlin.math.roundToInt
import org.json.JSONArray
import org.json.JSONObject

internal class NativeListSectionHeaderRowView(context: ThemedReactContext) :
  NativeListRendererRowView(context) {
  private val mainColumn = LinearLayout(context)
  private val titleLine = PackedTitleLineLayout(context)
  private val title = DottedUnderlineTextView(context)
  private val subtitle = NativeListTextView(context)
  private val trailingColumn = LinearLayout(context)
  private val trailingViews = listOf(NativeListTextView(context))
  private val checkboxControl = NativeListAccessoryStack(context)
  private val headerTitleIcon = OneKeyIconView(context)
  private val headerValueIcon = OneKeyIconView(context)
  private var currentItem: NativeListItem? = null
  private var currentTheme: JSONObject? = null
  private var currentLayout = "linear"
  private val selectorTextMetrics = mutableMapOf<TextView, Pair<Float, Int>>()
  private val paintDefaults = mutableMapOf<TextView, Int>()
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

  override fun usesSourceScale(item: NativeListItem, provided: Boolean) =
    item.usesSelectorSourceScale || provided

  override fun backgroundGroupPosition(
    item: NativeListItem,
    layout: String,
    itemIndex: Int?,
    selected: Boolean,
  ) =
    if (layout == "sectioned" && !item.json.optBoolean("selected")) ""
    else item.json.optString("groupPosition")

  override fun defaultHeight(item: NativeListItem, layout: String) =
    when {
      item.json.optString("presentation") == "networkSelector" -> 44
      layout == "table" -> 28
      item.json.optString("variant") == "summary" -> 80
      item.json.optString("variant") == "gallery" -> 32
      item.json.optString("variant") == "history" ||
        item.sectionKey?.startsWith("history-") == true -> 16
      item.sectionKey in setOf("linear-tokens", "action-tokens") -> 30
      item.json.optString("value").isNotEmpty() && item.json.optJSONObject("checkbox") != null -> 40
      else -> 36
    }

  override fun minimumContentHeight(item: NativeListItem, layout: String, sizeDelta: Int) =
    dp(
      (defaultHeight(item, layout) +
          if (item.json.optString("variant") in setOf("summary", "gallery")) 0 else sizeDelta)
        .coerceAtLeast(0)
    )

  override fun modelHeight(item: NativeListItem, layout: String): Int? {
    if (!item.json.has("height")) return null
    if (item.json.optString("heightRounding") in setOf("floor", "nearest"))
      return super.modelHeight(item, layout)
    if (
      item.json.optString("presentation") == "networkSelector" &&
        item.json.optString("variant") != "summary" &&
        item.json.optJSONObject("checkbox") == null
    ) {
      val px = item.json.optDouble("height") * resources.displayMetrics.density
      return if (item.json.optString("titleActionKey").isEmpty()) px.toInt()
      else
        kotlin.math
          .ceil(px * (if (sourceScale) 1.0 else NativeListScale.factor(resources).toDouble()))
          .toInt()
    }
    return super.modelHeight(item, layout)
  }

  init {
    mainColumn.orientation = VERTICAL
    mainColumn.gravity = Gravity.CENTER_VERTICAL
    titleLine.orientation = HORIZONTAL
    titleLine.gravity = Gravity.CENTER_VERTICAL
    titleLine.addView(title, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
    mainColumn.addView(titleLine)
    mainColumn.addView(subtitle)
    trailingColumn.addView(trailingViews[0])
    trailingColumn.addView(checkboxControl)
    checkboxControl.onAction = { key, view, slot, target ->
      emitAction(key, view, "trailingAccessory", slot, target)
    }
  }

  private fun color(theme: JSONObject?, key: String, fallback: String) =
    parseNativeListColor(theme?.optString(key, fallback) ?: fallback)

  private fun safeColor(value: String?, fallback: Int) =
    runCatching { parseNativeListColor(value ?: "") }.getOrDefault(fallback)

  private fun roundedFill(color: Int, radius: Float) =
    GradientDrawable().apply {
      setColor(color)
      cornerRadius =
        if (sourceScale) radius * resources.displayMetrics.density
        else NativeListScale.dp(resources, radius)
    }

  private fun sp(value: Float) = if (sourceScale) value else NativeListScale.font(resources, value)

  private fun wrap() = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

  private fun weighted() = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)

  private fun showText(view: TextView, text: String, lines: Int) {
    view.text = text
    view.maxLines = lines
    view.visibility = if (text.isEmpty()) GONE else VISIBLE
  }

  private fun emitAction(
    item: NativeListItem,
    key: String,
    target: NativeSelectionTarget?,
    view: View,
    source: String,
    slot: Int? = null,
  ) {
    emitAction(key, view, source, slot, target)
  }

  override fun bindContent(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    selectorTextMetrics.clear()
    currentItem = item
    currentTheme = theme
    currentLayout = layout
    removeAllViews()
    titleLine.removeView(headerTitleIcon)
    trailingColumn.removeView(headerValueIcon)
    paintDefaults.forEach { (view, flags) -> view.paintFlags = flags }
    paintDefaults.clear()
    orientation = HORIZONTAL
    gravity = Gravity.CENTER_VERTICAL
    setPadding(
      dp(if (layout == "table") 16 else 12),
      dp(8),
      dp(if (layout == "table") 16 else 12),
      dp(8),
    )
    titleLine.packsChildrenAtStart = false
    titleLine.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    title.layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
    for (view in listOf(title, subtitle) + trailingViews) {
      view.text = ""
      view.visibility = GONE
      view.setLineSpacing(0f, 1f)
      view.maxLines = 1
      view.ellipsize = null
      view.setHorizontallyScrolling(false)
      view.opticalOffsetY = 0f
      view.gravity = Gravity.START
      view.letterSpacing = 0f
      view.fontFeatureSettings = "tnum"
      view.includeFontPadding = false
      view.background = null
      view.setPadding(0, 0, 0, 0)
      view.setOnClickListener(null)
      view.isClickable = false
    }
    title.useSourceScale = sourceScale
    title.showsDottedUnderline = false
    subtitle.textSize = sp(14f)
    subtitle.typeface = NativeListFonts.regular(context)
    subtitle.setTextColor(color(theme, "secondaryText", "#0000009B"))
    subtitle.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    trailingViews[0].apply {
      typeface = NativeListFonts.medium(context)
      textSize = sp(16f)
      gravity = Gravity.END or Gravity.CENTER_VERTICAL
      setTextColor(color(theme, "primaryText", "#000000DF"))
      setTag(com.facebook.react.R.id.react_test_id, null)
      layoutParams = wrap()
    }
    trailingColumn.orientation = VERTICAL
    trailingColumn.gravity = Gravity.END or Gravity.CENTER_VERTICAL
    checkboxControl.reset()
    checkboxControl.visibility = GONE
    checkboxControl.layoutParams = wrap()
    headerTitleIcon.useSourceScale = sourceScale
    headerValueIcon.useSourceScale = sourceScale
    bindSectionHeader(item, theme, checkboxState)
    val variant = item.json.optString("variant")
    val network = item.json.optString("presentation") == "networkSelector"
    title.textSize =
      sp(
        when {
          network && variant != "summary" -> 14f
          variant == "gallery" -> 18f
          variant == "summary" -> 16f
          layout == "table" -> 11f
          variant == "history" || item.sectionKey?.startsWith("history-") == true -> 12f
          else -> 14f
        }
      )
    title.typeface =
      when {
        network &&
          item.json.has("height") &&
          variant != "summary" &&
          (item.json.optJSONObject("checkbox") != null ||
            item.json.optString("titleActionKey").isEmpty()) -> NativeListFonts.semibold(context)
        network -> NativeListFonts.medium(context)
        layout == "table" -> NativeListFonts.regular(context)
        variant == "summary" -> NativeListFonts.medium(context)
        item.sectionKey in setOf("linear-tokens", "action-tokens") ->
          NativeListFonts.regular(context)
        else -> NativeListFonts.semibold(context)
      }
    TextViewCompat.setLineHeight(
      title,
      dp(
        when {
          network && variant != "summary" -> 20
          variant in setOf("gallery", "summary") -> 24
          layout == "table" -> 14
          variant == "history" || item.sectionKey?.startsWith("history-") == true -> 16
          else -> 20
        }
      ),
    )
    if (layout == "table") TextViewCompat.setLineHeight(trailingViews[0], dp(14))
    if (
      network &&
        item.json.has("height") &&
        variant != "summary" &&
        item.json.optString("titleActionKey").isEmpty() &&
        item.json.optJSONObject("checkbox") == null
    ) {
      val inset = (20 * resources.displayMetrics.density).toInt() - dp(8)
      setPadding(inset, 0, inset, 0)
    }
    val style = item.json.optJSONObject("style") ?: JSONObject()
    if (style.has("horizontalPadding")) {
      val hp = stylePx(style.optDouble("horizontalPadding"))
      setPadding(hp, paddingTop, hp, paddingBottom)
    }
    if (style.has("verticalPadding")) {
      val vp = stylePx(style.optDouble("verticalPadding"))
      setPadding(paddingLeft, vp, paddingRight, vp)
    }
    if (style.has("lineGap"))
      (subtitle.layoutParams as LayoutParams).topMargin = stylePx(style.optDouble("lineGap"))
    if (style.has("trailingGap")) {
      var previous = false
      for (i in 0 until trailingColumn.childCount) {
        val child = trailingColumn.getChildAt(i)
        if (child.visibility == GONE) continue
        val params = child.layoutParams as LayoutParams
        if (trailingColumn.orientation == HORIZONTAL)
          params.marginStart = if (previous) stylePx(style.optDouble("trailingGap")) else 0
        else params.topMargin = if (previous) stylePx(style.optDouble("trailingGap")) else 0
        previous = true
      }
    }
    applyTextStyle(item)
    style
      .optJSONObject("container")
      ?.optString("contentVerticalAlignment")
      ?.takeIf(String::isNotEmpty)
      ?.let {
        gravity =
          when (it) {
            "top" -> Gravity.TOP
            "bottom" -> Gravity.BOTTOM
            else -> Gravity.CENTER_VERTICAL
          }
      }
    applySelectorTypography(item)
  }

  private fun applyTextStyle(item: NativeListItem) {
    val style = item.json.optJSONObject("style") ?: return
    for ((slot, view) in
      listOf("title" to title, "subtitle" to subtitle, "value" to trailingViews[0])) style
      .optJSONObject(slot)
      ?.let { applyStyledText(view, it) }
  }

  private fun bindCheckbox(
    item: NativeListItem,
    data: JSONObject,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    checkboxControl.bind(
      item,
      JSONArray().put(data),
      currentTheme,
      JSONObject(),
      sourceScale,
      checkboxState,
    )
  }

  override fun bindSelectionContent(
    item: NativeListItem,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    currentItem = item
    checkboxControl.updateSelection(item, checkboxState)
  }

  override fun bindStableSummary(item: NativeListItem) {
    if (item.json.optString("variant") != "summary" || !retainBoundItem(item)) return
    currentItem = item
    contentDescription = accessibilityText(item)
    title.text = item.json.optString("title")
    trailingViews[0].text = item.json.optString("value")
    applyValueSegments(trailingViews[0], item.json.optJSONArray("valueSegments"))
    applyTextStyle(item)
    applySelectorTypography(item)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val item = currentItem ?: return
    if (
      item.json.has("height") &&
        item.json.optString("presentation") == "networkSelector" &&
        checkboxControl.visibility == VISIBLE &&
        trailingColumn.parent === this
    ) {
      val density = resources.displayMetrics.density
      val values = trailingViews.filter { it.visibility == VISIBLE }
      val trailing = values.sumOf { it.measuredWidth } + (20 + 12 * values.size) * density
      val sourceLeft = (measuredWidth - 12 * density - trailing).roundToInt()
      val measuredLeft = measuredWidth - paddingRight - trailingColumn.measuredWidth
      mainColumn.measure(
        MeasureSpec.makeMeasureSpec(
          (mainColumn.measuredWidth + sourceLeft - measuredLeft).coerceAtLeast(0),
          MeasureSpec.EXACTLY,
        ),
        MeasureSpec.makeMeasureSpec(mainColumn.measuredHeight, MeasureSpec.EXACTLY),
      )
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    val item = currentItem ?: return
    if (
      item.json.has("height") &&
        item.json.optString("presentation") == "networkSelector" &&
        item.json.optString("variant") == "summary" &&
        item.json
          .optJSONObject("style")
          ?.optJSONObject("container")
          ?.has("contentVerticalAlignment") != true
    ) {
      val margins = trailingColumn.layoutParams as MarginLayoutParams
      val available = height - paddingTop - paddingBottom - margins.topMargin - margins.bottomMargin
      trailingColumn.offsetTopAndBottom(
        paddingTop + margins.topMargin + (available - trailingColumn.height + 1) / 2 -
          trailingColumn.top
      )
    }
  }

  override fun recycleContent() {
    checkboxControl.reset()
    currentItem = null
  }

  override fun disposeContent() {
    recycleContent()
  }

  private fun bindSectionHeader(
    item: NativeListItem,
    theme: JSONObject?,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    val variant = item.json.optString("variant")
    val isSummary = variant == "summary"
    val isGallery = variant == "gallery"
    val isTable = currentLayout == "table"
    // OneKey patch: title help emits its own frame instead of toggling the section.
    item.json
      .optString("titleActionKey")
      .takeIf { it.isNotEmpty() }
      ?.let { key ->
        title.setOnClickListener { emitAction(item, key, null, title, "leadingAction") }
      }
    val isNetworkSelector = item.json.optString("presentation") == "networkSelector"
    val isHistory = variant == "history" || item.sectionKey?.startsWith("history-") == true
    val isTokenManager = item.sectionKey in setOf("linear-tokens", "action-tokens")
    val isExplicitNetworkHeader = isNetworkSelector && item.json.has("height")
    val hasDottedTitle =
      isSummary ||
        (isNetworkSelector &&
          (!isExplicitNetworkHeader || item.json.optString("titleActionKey").isNotEmpty())) ||
        (item.json.optString("value").isNotEmpty() && item.json.optJSONObject("checkbox") != null)
    // Linear/sectioned snapshots reserve the ListItem mx=8 at RecyclerView
    // level, so header-local insets below are source px minus that outer inset.
    val headerHorizontalInset = 12
    addView(mainColumn, weighted())
    showText(title, item.json.optString("title"), 1)
    if (hasDottedTitle) {
      title.showsDottedUnderline = true
      title.dottedUnderlineColor = color(theme, "secondaryText", "#0000009B")
      title.setPadding(0, 0, 0, dp(3))
    }
    title.setTextColor(
      color(
        theme,
        if (isSummary || isGallery) "primaryText" else "secondaryText",
        if (isSummary || isGallery) "#000000DF" else "#0000009B",
      )
    )
    if (isNetworkSelector) {
      val verticalInset =
        if (isExplicitNetworkHeader && item.json.optString("titleActionKey").isEmpty()) 8 else 12
      setPadding(
        dp(headerHorizontalInset),
        dp(verticalInset),
        dp(headerHorizontalInset),
        dp(verticalInset),
      )
      title.textSize = sp(14f)
      title.typeface = NativeListFonts.medium(context)
      TextViewCompat.setLineHeight(title, dp(20))
    } else if (isHistory) {
      setPadding(0, 0, 0, 0)
      title.text = item.json.optString("title").uppercase()
      title.textSize = sp(12f)
      title.typeface = NativeListFonts.semibold(context)
      title.letterSpacing = 0.8f / sp(12f)
      TextViewCompat.setLineHeight(title, dp(16))
    } else if (isTokenManager) {
      setPadding(dp(headerHorizontalInset), dp(10), dp(headerHorizontalInset), 0)
      title.textSize = sp(14f)
      title.typeface = NativeListFonts.regular(context)
      TextViewCompat.setLineHeight(title, dp(20))
    } else if (!isTable && !isGallery && !isSummary) {
      // SectionList.SectionHeader: h=36, px=20, headingSm 14/20 semibold.
      setPadding(dp(headerHorizontalInset), dp(8), dp(headerHorizontalInset), dp(8))
      TextViewCompat.setLineHeight(title, dp(20))
    } else if (isTable) {
      setPadding(dp(16), dp(12), dp(16), dp(2))
      title.includeFontPadding = false
      title.layoutParams = wrap()
      titleLine.layoutParams = wrap()
      item.json.optJSONObject("titleIcon")?.let { icon ->
        headerTitleIcon.visibility = VISIBLE
        headerTitleIcon.iconName = icon.optString("name")
        headerTitleIcon.tintColor =
          safeColor(icon.optString("tintColor"), color(theme, "iconSubdued", "#00000072"))
        titleLine.addView(
          headerTitleIcon,
          LayoutParams(dp(12), dp(12)).apply { marginStart = dp(4) },
        )
        if (icon.optString("name") != "ChevronGrabberVerOutline") {
          title.setTextColor(color(theme, "primaryText", "#000000DF"))
        }
      }
    }
    showText(subtitle, item.json.optString("subtitle"), 1)
    if (isGallery) {
      setPadding(dp(12), 0, dp(12), dp(8))
    } else if (isSummary) {
      addView(trailingColumn, wrap())
      // NetworkListHeader: outer mt=16/pb=12, inner XStack px=20/py=8.
      setPadding(dp(headerHorizontalInset), dp(24), dp(headerHorizontalInset), dp(20))
      showTrailing(
        0,
        item.json.optString("value"),
        true,
        item.json.optString("valueActionKey"),
        color(theme, "secondaryText", "#0000009B"),
      )
      trailingViews[0].setTag(
        com.facebook.react.R.id.react_test_id,
        item.json.optString("valueActionTestID").takeIf { it.isNotEmpty() },
      )
      trailingViews[0].accessibilityDelegate = selectorAccessibilityDelegate
      trailingViews[0].textSize = sp(16f)
      trailingViews[0].typeface = NativeListFonts.medium(context)
      TextViewCompat.setLineHeight(trailingViews[0], dp(24))
      // OneKey patch: the migrated media button shares the original 24-point text box.
      if (isExplicitNetworkHeader) trailingViews[0].setPadding(0, 0, 0, 0)
      else trailingViews[0].setPadding(dp(14), dp(6), dp(14), dp(6))
      trailingViews[0].layoutParams = wrap()
    } else if (!isGallery && !isHistory && !isTokenManager) {
      val value = item.json.optString("value")
      val checkboxData = item.json.optJSONObject("checkbox")
      if (isExplicitNetworkHeader && checkboxData != null) {
        // OneKey patch: the asset section title reserves its original 8dp trailing margin.
        (mainColumn.layoutParams as LayoutParams).marginEnd = dp(8)
      }
      if (checkboxData != null && value.isNotEmpty()) {
        // Value and checkbox share the trailing edge as one compound accessory.
        trailingColumn.orientation = HORIZONTAL
        trailingColumn.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        trailingViews[0].layoutParams = wrap()
        checkboxControl.layoutParams = LayoutParams(dp(20), dp(20)).apply { marginStart = dp(12) }
      }
      addView(trailingColumn, wrap())
      showTrailing(0, value, true)
      if (isTable) {
        trailingColumn.orientation = HORIZONTAL
        trailingColumn.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        trailingViews[0].includeFontPadding = false
        trailingViews[0].textSize = sp(11f)
        trailingViews[0].typeface = NativeListFonts.regular(context)
        trailingViews[0].setTextColor(color(theme, "secondaryText", "#0000009B"))
        item.json.optJSONObject("valueIcon")?.let { icon ->
          headerValueIcon.visibility = VISIBLE
          headerValueIcon.iconName = icon.optString("name")
          headerValueIcon.tintColor =
            safeColor(icon.optString("tintColor"), color(theme, "iconSubdued", "#00000072"))
          trailingColumn.addView(
            headerValueIcon,
            LayoutParams(dp(12), dp(12)).apply { marginStart = dp(4) },
          )
          if (icon.optString("name") != "ChevronGrabberVerOutline") {
            trailingViews[0].setTextColor(color(theme, "primaryText", "#000000DF"))
          }
        }
      }
      checkboxData?.let { bindCheckbox(item, it, checkboxState) }
    }
    applyValueSegments(trailingViews[0], item.json.optJSONArray("valueSegments"))
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
        currentItem
          ?.takeUnless { item -> item.json.optBoolean("disabled", false) }
          ?.let { item -> emitAction(item, actionKey, null, view, "trailingAccessory", index) }
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
          if (sourceScale)
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
      view.typeface =
        when (it) {
          "medium" -> NativeListFonts.medium(context)
          "semibold" -> NativeListFonts.semibold(context)
          "bold" -> NativeListFonts.bold(context)
          else -> NativeListFonts.regular(context)
        }
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
        NativeListLineHeightSpan(stylePx(style.optDouble("lineHeight"))),
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

  private fun applySelectorTypography(item: NativeListItem) {
    if (
      item.json.optString("presentation") !in
        setOf("accountSelector", "networkSelector", "walletSidebar")
    )
      return
    fun visit(view: View) {
      if (view is TextView) {
        view.fontFeatureSettings = "tnum"
        if (sourceScale) {
          paintDefaults.putIfAbsent(view, view.paintFlags)
          view.paintFlags = view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG or Paint.LINEAR_TEXT_FLAG
          val (fontSize, line) =
            selectorTextMetrics.getOrPut(view) { view.textSize to view.lineHeight }
          view.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            kotlin.math
              .ceil(
                (fontSize * resources.displayMetrics.density /
                    resources.displayMetrics.scaledDensity)
                  .toDouble()
              )
              .toFloat(),
          )
          view.letterSpacing = 0f
          if (view.text.isNotEmpty()) {
            val text = SpannableStringBuilder(view.text)
            text
              .getSpans(0, text.length, NativeListLineHeightSpan::class.java)
              .forEach(text::removeSpan)
            text.setSpan(
              NativeListLineHeightSpan(line),
              0,
              text.length,
              Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
            view.setLineSpacing(0f, 1f)
            view.text = text
          }
        }
      }
      if (view is ViewGroup) for (i in 0 until view.childCount) visit(view.getChildAt(i))
    }
    visit(this)
  }
}
