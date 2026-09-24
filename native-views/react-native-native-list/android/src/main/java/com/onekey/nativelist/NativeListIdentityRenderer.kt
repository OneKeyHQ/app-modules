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

internal class NativeListIdentityRowView(context: ThemedReactContext) :
  NativeListRendererRowView(context) {
  private val leadingFrame = NativeListLeadingVisual(context)
  private val leadingFallback
    get() = leadingFrame.fallbackTextView

  private val leadingActionIcon = OneKeyIconView(context)
  private val mainColumn = LinearLayout(context)
  private val titleLine = PackedTitleLineLayout(context)
  private val title = NativeListTextView(context)
  private val subtitle = NativeListTextView(context)
  private val tertiary = NativeListTextView(context)
  private val badgeLine = NativeListTextView(context)
  private val trailingColumn = NativeListAccessoryStack(context)
  private val semanticBadgeLabels = mutableListOf<TextView>()
  private val semanticSubtitleLabels = mutableListOf<TextView>()
  private var walletBadgeLine: View? = null
  private var currentItem: NativeListItem? = null
  private var currentTheme: JSONObject? = null
  private val paintDefaults = mutableMapOf<TextView, Int>()

  override fun backgroundGroupPosition(
    item: NativeListItem,
    layout: String,
    itemIndex: Int?,
    selected: Boolean,
  ) =
    if (
      item.json.optString("presentation") == "walletSidebar" ||
        item.json.has("height") &&
          item.json.optString("presentation") in setOf("accountSelector", "networkSelector")
    )
      "single"
    else item.json.optString("groupPosition")

  override val assetFields = listOf("leading")

  override fun usesSourceScale(item: NativeListItem, provided: Boolean) =
    item.usesSelectorSourceScale || provided

  override fun defaultHeight(item: NativeListItem, layout: String) =
    when {
      item.json.optString("presentation") == "walletSidebar" ->
        if ((item.json.optJSONArray("badges")?.length() ?: 0) > 0) 92 else 68
      item.json.optString("presentation") == "networkSelector" -> 47
      item.json.optString("tertiary").isNotEmpty() -> 72
      item.json.optString("subtitle").isNotEmpty() -> 60
      else -> 56
    }

  override fun minimumContentHeight(item: NativeListItem, layout: String, sizeDelta: Int) =
    dp(
      (defaultHeight(item, layout) +
          if (item.json.optString("presentation") == "networkSelector") 0 else sizeDelta)
        .coerceAtLeast(0)
    )

  override val defaultSeparatorInset = 60

  init {
    mainColumn.orientation = VERTICAL
    mainColumn.gravity = Gravity.CENTER_VERTICAL
    titleLine.orientation = HORIZONTAL
    titleLine.gravity = Gravity.CENTER_VERTICAL
    titleLine.addView(title, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
    titleLine.addView(badgeLine)
    trailingColumn.onAction = { key, view, slot, target ->
      emitAction(key, view, "trailingAccessory", slot, target, trailingColumn.anchorInset(view))
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

  private fun JSONArray?.hasAccessory(kind: String) =
    this != null && (0 until length()).any { optJSONObject(it)?.optString("kind") == kind }

  override fun bindContent(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    currentItem = item
    currentTheme = theme
    removeAllViews()
    mainColumn.removeAllViews()
    semanticBadgeLabels.clear()
    semanticSubtitleLabels.clear()
    walletBadgeLine = null
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
    clipChildren = true
    clipToPadding = true
    mainColumn.gravity = Gravity.CENTER_VERTICAL
    titleLine.gravity = Gravity.CENTER_VERTICAL
    titleLine.packsChildrenAtStart = false
    titleLine.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    title.layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
    for (view in listOf(title, subtitle, tertiary, badgeLine)) {
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
    }
    title.typeface =
      if (item.json.optString("presentation") in setOf("accountSelector", "walletSidebar"))
        NativeListFonts.regular(context)
      else NativeListFonts.medium(context)
    title.textSize = sp(if (item.json.optString("presentation") == "walletSidebar") 12f else 16f)
    title.setTextColor(color(theme, "primaryText", "#000000DF"))
    subtitle.typeface = NativeListFonts.regular(context)
    subtitle.textSize = sp(14f)
    subtitle.setTextColor(color(theme, "secondaryText", "#0000009B"))
    subtitle.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    tertiary.typeface = NativeListFonts.regular(context)
    tertiary.textSize = sp(14f)
    tertiary.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    badgeLine.typeface = NativeListFonts.medium(context)
    badgeLine.textSize = sp(12f)
    // Legacy: tertiary always ellipsizes at the end.
    tertiary.ellipsize = TextUtils.TruncateAt.END
    mainColumn.addView(titleLine)
    mainColumn.addView(subtitle)
    mainColumn.addView(tertiary)
    trailingColumn.orientation = VERTICAL
    trailingColumn.gravity = Gravity.END or Gravity.CENTER_VERTICAL
    leadingActionIcon.useSourceScale = sourceScale
    leadingActionIcon.setOnClickListener(null)
    bindIdentity(item, theme, item.json.optBoolean("selected"), checkboxState)
    TextViewCompat.setLineHeight(
      title,
      dp(if (item.json.optString("presentation") == "walletSidebar") 16 else 24),
    )
    TextViewCompat.setLineHeight(subtitle, dp(20))
    TextViewCompat.setLineHeight(tertiary, dp(20))
    val style = item.json.optJSONObject("style") ?: JSONObject()
    if (style.has("horizontalPadding")) {
      val hp = stylePx(style.optDouble("horizontalPadding"))
      setPadding(hp, paddingTop, hp, paddingBottom)
    }
    if (style.has("verticalPadding")) {
      val vp = stylePx(style.optDouble("verticalPadding"))
      setPadding(paddingLeft, vp, paddingRight, vp)
    }
    if (style.has("lineGap")) styleGap(mainColumn, stylePx(style.optDouble("lineGap")))
    if (style.has("leadingGap") && leadingFrame.parent === this) {
      if (orientation == VERTICAL)
        (mainColumn.layoutParams as LayoutParams).topMargin = stylePx(style.optDouble("leadingGap"))
      else
        (leadingFrame.layoutParams as LayoutParams).marginEnd =
          stylePx(style.optDouble("leadingGap"))
    }
    if (style.has("titleBadgeGap")) {
      val gap = stylePx(style.optDouble("titleBadgeGap"))
      if (walletBadgeLine != null) (walletBadgeLine!!.layoutParams as LayoutParams).topMargin = gap
      else (badgeLine.layoutParams as LayoutParams).marginStart = gap
    }
    for ((slot, targets) in
      listOf(
        "title" to listOf(title),
        "subtitle" to semanticSubtitleLabels.ifEmpty { listOf(subtitle) },
        "tertiary" to listOf(tertiary),
        "badge" to semanticBadgeLabels.ifEmpty { listOf(badgeLine) },
      )) {
      style.optJSONObject(slot)?.let { text -> targets.forEach { applyStyledText(it, text) } }
    }
    val alignment = style.optJSONObject("container")?.optString("contentVerticalAlignment")
    if (!alignment.isNullOrEmpty())
      gravity =
        (gravity and Gravity.VERTICAL_GRAVITY_MASK.inv()) or
          when (alignment) {
            "top" -> Gravity.TOP
            "bottom" -> Gravity.BOTTOM
            else -> Gravity.CENTER_VERTICAL
          }
    applySelectorTypography(item)
    // Legacy: overhanging selector icons/checkboxes must not be clipped by row padding.
    if (trailingColumn.parent === this && trailingColumn.requestsUnclippedHost) clipToPadding = false
  }

  private fun styleGap(column: LinearLayout, gap: Int) {
    var previous = false
    for (i in 0 until column.childCount) {
      val child = column.getChildAt(i)
      if (child.visibility == GONE) continue
      (child.layoutParams as LayoutParams).topMargin = if (previous) gap else 0
      previous = true
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
          val line = view.lineHeight
          view.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            kotlin.math
              .ceil(
                (view.textSize * resources.displayMetrics.density /
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

  private fun addLeading(visual: JSONObject?, sizeDp: Int, spacingDp: Int = 12) {
    // Legacy: a missing visual still reserves an empty, transparent leading slot.
    val descriptor = visual ?: JSONObject().put("backgroundColor", "#00000000")
    if (visual == null) leadingFrame.recycle()
    val item = currentItem!!
    val style = item.json.optJSONObject("style")?.optJSONObject("image") ?: JSONObject()
    val wallet = item.json.optString("presentation") == "walletSidebar" && item.json.has("height")
    // Legacy wallet-sidebar glyph/overlay sizing applies with source scaling.
    val walletSource = item.json.optString("presentation") == "walletSidebar" && sourceScale
    val fallbackIconName =
      descriptor.optJSONObject("fallbackIcon")?.takeIf { descriptor.optJSONObject("image") == null }?.optString("name")
    leadingFrame.glyphSize =
      if (walletSource && fallbackIconName == "LockSolid") 40
      else if (walletSource && fallbackIconName == "PlusSmallOutline") 24
      else 18
    leadingFrame.walletTextOverlays = walletSource
    leadingFrame.roundedImageRadius =
      if (item.json.optString("presentation") == "accountSelector" && item.json.has("height")) 8
      else 10
    leadingFrame.dashedBorderWidth = if (wallet) 1 else 2
    leadingFrame.overlayTextFontSize = if (wallet) 12f else 10f
    leadingFrame.overlayTextLineHeight = if (wallet) 16 else null
    leadingFrame.bind(descriptor, style, item.key, currentTheme, false, sourceScale)
    leadingFallback.typeface = NativeListFonts.bold(context)
    leadingFallback.textSize = sp(13f)
    leadingFallback.setLineSpacing(0f, 1f)
    addView(
      leadingFrame,
      LayoutParams(
          if (style.has("width")) stylePx(style.optDouble("width")) else dp(sizeDp),
          if (style.has("height")) stylePx(style.optDouble("height")) else dp(sizeDp),
        )
        .apply {
          // Legacy: Yoga rounds cumulative selector edges, not each gap separately.
          marginEnd =
            if (
              item.json.has("height") &&
                item.json.optString("presentation") in setOf("accountSelector", "networkSelector")
            )
              dp(12 + sizeDp + spacingDp) - dp(12) - dp(sizeDp)
            else dp(spacingDp)
        },
    )
  }

  override fun bindSelectionContent(
    item: NativeListItem,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    currentItem = item
    trailingColumn.updateSelection(item, checkboxState)
    if (item.json.optString("presentation") == "walletSidebar") {
      title.setTextColor(
        color(
          currentTheme,
          if (rowSelected) "primaryText" else "secondaryText",
          if (rowSelected) "#FFFFFFED" else "#FFFFFFAF",
        )
      )
      item.json
        .optJSONObject("style")
        ?.optJSONObject("title")
        ?.optString("color")
        ?.takeIf(String::isNotEmpty)
        ?.let { title.setTextColor(safeColor(it, title.currentTextColor)) }
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val item = currentItem ?: return
    if (
      item.json.has("height") &&
        item.json.optString("presentation") == "networkSelector" &&
        trailingColumn.hasCheckbox &&
        trailingColumn.parent === this
    ) {
      val density = resources.displayMetrics.density
      val values = trailingColumn.visibleValues
      val width = values.sumOf { it.measuredWidth } + (20 + 12 * values.size) * density
      val sourceLeft = (measuredWidth - 12 * density - width).roundToInt()
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
      item.json
        .optJSONObject("style")
        ?.optJSONObject("container")
        ?.has("contentVerticalAlignment") == true
    )
      return
    if (!item.json.has("height")) return
    val accessory = item.json.optJSONArray("trailing")?.optJSONObject(0)
    if (
      item.json.optString("presentation") == "accountSelector" &&
        accessory?.optString("kind") == "icon" &&
        accessory.optString("name") == "PlusSmallOutline"
    ) {
      trailingColumn.firstVisibleIcon?.let {
        trailingColumn.offsetTopAndBottom(dp(18) - dp(7) - trailingColumn.top - it.top)
      }
    }
    if (item.json.optString("presentation") == "networkSelector") {
      for (column in listOf(mainColumn, trailingColumn)) {
        if (column.parent !== this || column.visibility == GONE) continue
        val margins = column.layoutParams as MarginLayoutParams
        val available =
          height - paddingTop - paddingBottom - margins.topMargin - margins.bottomMargin
        column.offsetTopAndBottom(
          paddingTop + margins.topMargin + (available - column.height + 1) / 2 - column.top
        )
      }
    }
  }

  override fun recycleContent() {
    leadingFrame.recycle()
    trailingColumn.reset()
    currentItem = null
  }

  override fun disposeContent() {
    recycleContent()
    leadingFrame.dispose()
  }

  private fun bindIdentity(
    item: NativeListItem,
    theme: JSONObject?,
    selected: Boolean,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    if (item.json.optString("presentation") == "walletSidebar") {
      orientation = VERTICAL
      gravity = Gravity.CENTER
      setPadding(dp(4), dp(4), dp(4), dp(4))
      addLeading(item.json.optJSONObject("leading"), 40, spacingDp = 0)
      leadingFallback.textSize = sp(28f)
      leadingFallback.typeface = NativeListFonts.regular(context)
      mainColumn.gravity = Gravity.CENTER
      titleLine.gravity = Gravity.CENTER
      titleLine.packsChildrenAtStart = false
      titleLine.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
      // OneKey patch: the original wallet title ellipsizes within the full inner row width.
      title.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
      title.gravity = Gravity.CENTER
      // OneKey patch: this branch returns before the common identity ellipsis setup.
      if (item.json.has("height")) title.ellipsize = TextUtils.TruncateAt.END
      showText(title, item.json.optString("title"), 1)
      title.setTextColor(
        color(
          theme,
          if (selected) "primaryText" else "secondaryText",
          if (selected) "#FFFFFFED" else "#FFFFFFAF",
        )
      )
      addView(
        mainColumn,
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
          // OneKey patch: snap the source 4 + 40 + 4 sequence once instead of rounding each gap.
          topMargin = if (item.json.has("height")) dp(48) - dp(44) else dp(4)
        },
      )
      // OneKey patch: wallet tags are a centered line below the wallet name.
      item.json
        .optJSONArray("badges")
        ?.takeIf { it.length() > 0 }
        ?.let { badges ->
          val isSelector = item.json.has("height")
          val badgeLineHeight = if (isSelector) 14 else 16
          val badgeHeight = badgeLineHeight + 4
          val line =
            LinearLayout(context).apply {
              orientation = HORIZONTAL
              gravity = Gravity.CENTER
            }
          for (index in 0 until badges.length()) {
            val badge =
              NativeListTextView(context).apply {
                text = badges.getJSONObject(index).optString("text")
                textSize = sp(if (isSelector) 11f else 12f)
                typeface = NativeListFonts.regular(context)
                includeFontPadding = false
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
                val warning =
                  isSelector && badges.getJSONObject(index).optString("tone") == "warning"
                setTextColor(
                  color(
                    theme,
                    if (warning) "caution" else "secondaryText",
                    if (warning) "#AB6400" else "#0000009B",
                  )
                )
                background =
                  roundedFill(
                    color(
                      theme,
                      if (warning) "cautionBackground"
                      else if (isSelector) "subduedBackground" else "strongBackground",
                      if (warning) "#FFF4D5" else "#00000006",
                    ),
                    4f,
                  )
                setPadding(dp(if (isSelector) 6 else 4), dp(2), dp(if (isSelector) 6 else 4), dp(2))
              }
            TextViewCompat.setLineHeight(badge, dp(badgeLineHeight))
            semanticBadgeLabels.add(badge)
            line.addView(
              badge,
              LayoutParams(LayoutParams.WRAP_CONTENT, dp(badgeHeight)).apply {
                if (index > 0) marginStart = dp(4)
              },
            )
          }
          walletBadgeLine = line
          mainColumn.addView(
            line,
            LayoutParams(LayoutParams.WRAP_CONTENT, dp(badgeHeight)).apply { topMargin = dp(4) },
          )
        }
      val accessories = item.json.optJSONArray("trailing") ?: JSONArray()
      if (accessories.length() > 0) {
        addView(trailingColumn, wrap().apply { topMargin = dp(4) })
        trailingColumn.bind(
          item,
          accessories,
          theme,
          item.json.optJSONObject("style") ?: JSONObject(),
          sourceScale,
          checkboxState,
          horizontal = true,
        )
      }
      return
    }
    if (item.json.optString("presentation") == "networkSelector") {
      // OneKey patch: the explicit 48-point row centers its 32-point network icon.
      // setPadding(dp(12), dp(7), dp(12), dp(8))
      setPadding(dp(12), dp(if (item.json.has("height")) 8 else 7), dp(12), dp(8))
    }
    val leading = item.json.optJSONObject("leading")
    item.json.optJSONObject("leadingAction")?.let { action ->
      leadingActionIcon.visibility = VISIBLE
      leadingActionIcon.iconName = action.optString("name")
      leadingActionIcon.glyphSizeDp = 24
      leadingActionIcon.tintColor =
        safeColor(action.optString("tintColor"), color(theme, "icon", "#0000009B"))
      leadingActionIcon.isEnabled = !action.optBoolean("disabled", false)
      leadingActionIcon.alpha = if (leadingActionIcon.isEnabled) 1f else 0.4f
      leadingActionIcon.setOnClickListener {
        emitAction(item, action.optString("actionKey"), null, leadingActionIcon, "leadingAction")
      }
      // ListItem.IconButton is 36dp with 6dp inner padding and m=-7. Keep the
      // full button frame but absorb its leading negative margin into the row
      // padding and its trailing margin into the following gap.
      setPadding(dp(5), paddingTop, paddingRight, paddingBottom)
      addView(leadingActionIcon, LayoutParams(dp(36), dp(36)).apply { marginEnd = dp(5) })
    }
    addLeading(
      leading,
      if (
        leading?.optString("kind") == "network" ||
          item.json.optString("presentation") == "accountSelector"
      )
        32
      else 40,
    )
    // OneKey patch: custom network initials retain LetterAvatar typography.
    if (
      item.json.optString("presentation") == "networkSelector" &&
        leading?.optJSONObject("image") == null &&
        leading?.optJSONObject("fallbackIcon") == null &&
        !leading?.optString("fallbackText").isNullOrEmpty()
    ) {
      leadingFallback.textSize = sp(19f)
      leadingFallback.typeface = NativeListFonts.semibold(context)
      leadingFallback.setTextColor(color(theme, "inverseText", "#FCFCFC"))
      TextViewCompat.setLineHeight(leadingFallback, dp(27))
    }
    addView(mainColumn, weighted())
    titleLine.packsChildrenAtStart = true
    title.ellipsize = TextUtils.TruncateAt.END
    showText(title, item.json.optString("title"), item.json.optInt("titleLines", 1))
    if (item.json.optString("presentation") == "accountSelector") {
      title.typeface = NativeListFonts.regular(context)
    }
    showText(subtitle, item.json.optString("subtitle"), item.json.optInt("subtitleLines", 2))
    // OneKey patch: use separate labels for independently truncated balance/address.
    item.json
      .optJSONArray("subtitleSegments")
      ?.takeIf { it.length() > 0 }
      ?.let { segments ->
        subtitle.visibility = GONE
        val line =
          SelectorSubtitleLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
          }
        for (index in 0 until segments.length()) {
          val segment = segments.getJSONObject(index)
          if (segment.optBoolean("separatorBefore", false)) {
            val dot =
              View(context).apply {
                background = roundedFill(color(theme, "disabledText", "#00000072"), 2f)
              }
            line.addView(
              dot,
              LayoutParams(dp(4), dp(4)).apply {
                marginStart = dp(6)
                marginEnd = dp(6)
              },
            )
          }
          val label =
            NativeListTextView(context).apply {
              text = segment.optString("text")
              typeface = NativeListFonts.regular(context)
              textSize = sp(14f)
              includeFontPadding = false
              maxLines = 1
              ellipsize = TextUtils.TruncateAt.END
              val toneKey =
                when (segment.optString("tone")) {
                  "primary" -> "primaryText"
                  "disabled" -> "disabledText"
                  "caution" -> "caution"
                  "positive" -> "positive"
                  "negative" -> "negative"
                  else -> "secondaryText"
                }
              setTextColor(
                color(theme, toneKey, if (toneKey == "caution") "#AB6400" else "#0000009B")
              )
            }
          TextViewCompat.setLineHeight(label, dp(20))
          applyValueSegments(label, segment.optJSONArray("textSegments"), 14, 20, false)
          semanticSubtitleLabels.add(label)
          line.addView(label, LayoutParams(LayoutParams.WRAP_CONTENT, dp(20)))
        }
        mainColumn.addView(line, 2, LayoutParams(LayoutParams.MATCH_PARENT, dp(20)))
      }
    item.json
      .optJSONArray("titleMatch")
      ?.takeIf { it.length() > 0 }
      ?.let { matches ->
        val highlighted = SpannableStringBuilder(title.text)
        for (index in 0 until matches.length()) {
          val match = matches.getJSONObject(index)
          val start = match.optInt("start")
          val end = match.optInt("end")
          if (start >= 0 && end > start && end <= highlighted.length)
            highlighted.setSpan(
              ForegroundColorSpan(color(theme, "info", "#0D74CE")),
              start,
              end,
              Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
        }
        title.text = highlighted
      }
    showText(tertiary, item.json.optString("tertiary"), 1)
    tertiary.setTextColor(
      color(
        theme,
        if (item.json.optString("tertiaryTone") == "info") "info" else "secondaryText",
        if (item.json.optString("tertiaryTone") == "info") "#006DCBF2" else "#0000009B",
      )
    )
    val badges = item.json.optJSONArray("badges")
    if (badges != null && badges.length() > 0) {
      val texts =
        (0 until minOf(2, badges.length())).map { badges.getJSONObject(it).optString("text") }
      showText(badgeLine, texts.joinToString("  "), 1)
      badgeLine.layoutParams = wrap().apply { marginStart = dp(8) }
      badgeLine.setTextColor(color(theme, "secondaryText", "#0000009B"))
      badgeLine.background = roundedFill(color(theme, "strongBackground", "#0000000F"), 4f)
      badgeLine.setPadding(dp(8), dp(2), dp(8), dp(2))
      TextViewCompat.setLineHeight(badgeLine, dp(16))
    }
    addView(trailingColumn, wrap())
    val accessories = item.json.optJSONArray("trailing") ?: JSONArray()
    if (
      item.json.has("height") &&
        item.json.optString("presentation") == "networkSelector" &&
        accessories.hasAccessory("checkbox")
    ) {
      // OneKey patch: retain ListItem's title-to-accessory gap when measuring truncation.
      (mainColumn.layoutParams as LayoutParams).marginEnd = dp(12)
    }
    trailingColumn.bind(
      item,
      accessories,
      theme,
      item.json.optJSONObject("style") ?: JSONObject(),
      sourceScale,
      checkboxState,
      horizontal =
        (0 until accessories.length())
          .map { accessories.getJSONObject(it).optString("kind") }
          .let { kinds ->
            kinds.containsAll(listOf("value", "checkbox")) ||
              kinds == listOf("valuePair", "menu") ||
              (kinds == listOf("icon", "icon") &&
                accessories.getJSONObject(0).optString("name") == "PencilOutline" &&
                accessories.getJSONObject(1).optString("name") == "DragOutline")
          },
    )
    trailingColumn.endInset?.let { setPadding(paddingLeft, paddingTop, it, paddingBottom) }
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
}
