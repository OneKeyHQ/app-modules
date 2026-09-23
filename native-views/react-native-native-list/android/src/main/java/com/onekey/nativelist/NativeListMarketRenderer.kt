package com.margelo.nitro.nativelist

import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.facebook.react.uimanager.ThemedReactContext
import kotlin.math.roundToInt
import org.json.JSONArray
import org.json.JSONObject

internal class NativeListMarketRowView(context: ThemedReactContext) :
  NativeListRendererRowView(context) {
  private val leadingFrame = NativeListLeadingVisual(context)
  private val leadingActionIcon = OneKeyIconView(context)
  private val mainColumn = LinearLayout(context)
  private val titleLine = PackedTitleLineLayout(context)
  private val title = NativeListTextView(context)
  private val subtitle = NativeListTextView(context)
  private val tertiary = NativeListTextView(context)
  private val marketSubtitleLine = PackedTitleLineLayout(context)
  private val trailingColumn = LinearLayout(context)
  private val trailingViews = List(2) { NativeListTextView(context) }
  private val marketBadgeViews = List(3) { LinearLayout(context) }
  private val marketBadgeLabels = List(3) { TextView(context) }
  private val badgeSlots = List(3) { NativeListImageSlot(context) }
  private val marketBadgeImages
    get() = badgeSlots.map { it.view }

  private val marketBadgeGlyphs = List(3) { OneKeyIconView(context) }
  private val marketOriginalPaintFlags = mutableMapOf<TextView, Int>()
  private var imageSignature = ""
  private var longPress: Runnable? = null
  private val handler = Handler(Looper.getMainLooper())
  private var held = false
  private var longPressFired = false
  private var startX = 0f
  private var startY = 0f
  private var touchX = 0f
  private var touchY = 0f
  override val assetFields = listOf("leading", "badges")

  override fun usesSourceScale(item: NativeListItem, provided: Boolean) = true

  override fun modelHeight(item: NativeListItem, layout: String) =
    if (item.json.has("height")) stylePx(item.json.optDouble("height")) else null

  override fun minimumContentHeight(item: NativeListItem, layout: String, sizeDelta: Int): Int {
    val style = item.json.optJSONObject("style")
    val stock = item.json.optString("variant") == "stock"
    return stylePx(
      maxOf(
        if (stock) 72.0 else 68.0,
        (style?.optJSONObject("image")?.optDouble("height", if (stock) 40.0 else 32.0)
          ?: if (stock) 40.0 else 32.0) + 2 * (style?.optDouble("verticalPadding", 12.0) ?: 12.0),
      )
    )
  }

  init {
    sourceScale = true
    gravity = Gravity.CENTER_VERTICAL
    clipChildren = false
    clipToPadding = false
    mainColumn.orientation = VERTICAL
    mainColumn.gravity = Gravity.CENTER_VERTICAL
    titleLine.orientation = HORIZONTAL
    titleLine.gravity = Gravity.CENTER_VERTICAL
    titleLine.addView(title, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
    mainColumn.addView(titleLine)
    mainColumn.addView(subtitle)
    mainColumn.addView(tertiary)
    trailingViews.forEach(trailingColumn::addView)
    leadingActionIcon.useSourceScale = true
    marketBadgeViews.forEachIndexed { index, badge ->
      badge.orientation = HORIZONTAL
      badge.gravity = Gravity.CENTER_VERTICAL
      marketBadgeGlyphs[index].useSourceScale = true
      marketBadgeLabels[index].apply {
        includeFontPadding = false
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
      }
      badge.addView(marketBadgeGlyphs[index], LayoutParams(dp(16), dp(16)))
      badge.addView(marketBadgeImages[index], LayoutParams(dp(14), dp(14)))
      badge.addView(marketBadgeLabels[index], wrap())
    }
  }

  private val circleOutlineProvider =
    object : ViewOutlineProvider() {
      override fun getOutline(view: View, outline: Outline) {
        outline.setOval(0, 0, view.width, view.height)
      }
    }

  private fun color(theme: JSONObject?, key: String, fallback: String) =
    parseNativeListColor(theme?.optString(key, fallback) ?: fallback)

  private fun safeColor(value: String?, fallback: Int) =
    runCatching { parseNativeListColor(value ?: "") }.getOrDefault(fallback)

  private fun roundedFill(color: Int, radius: Float) =
    GradientDrawable().apply {
      setColor(color)
      cornerRadius = radius * resources.displayMetrics.density
    }

  private fun sp(value: Float) = value

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

  override fun accessibilityText(item: NativeListItem) =
    item.json.optString(
      "accessibilityLabel",
      listOf(
          item.json.optString("title"),
          item.json.optString("subtitle"),
          item.json.optString("price"),
          item.json.optJSONObject("change")?.optString("text") ?: "",
        )
        .filter(String::isNotEmpty)
        .joinToString(", "),
    )

  override fun bindContent(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    cancelMarketHold()
    longPressFired = false
    removeAllViews()
    marketOriginalPaintFlags.forEach { (view, flags) -> view.paintFlags = flags }
    marketOriginalPaintFlags.clear()
    marketSubtitleLine.removeAllViews()
    mainColumn.removeAllViews()
    marketBadgeViews.forEachIndexed { index, badge ->
      (badge.parent as? android.view.ViewGroup)?.removeView(badge)
      badge.visibility = GONE
      badge.setOnClickListener(null)
      marketBadgeGlyphs[index].visibility = GONE
      marketBadgeImages[index].visibility = GONE
      marketBadgeLabels[index].setLineSpacing(0f, 1f)
      marketBadgeLabels[index].layoutParams = wrap()
      if (item.json.optJSONArray("badges")?.optJSONObject(index)?.optJSONObject("icon") == null)
        badgeSlots[index].recycle()
    }
    for (view in listOf(title, subtitle, tertiary) + trailingViews) {
      view.text = ""
      view.visibility = GONE
      view.maxLines = 1
      view.ellipsize = TextUtils.TruncateAt.END
      view.setLineSpacing(0f, 1f)
      view.setHorizontallyScrolling(false)
      view.opticalOffsetY = 0f
      view.gravity = Gravity.CENTER_VERTICAL or Gravity.START
    }
    title.layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
    mainColumn.addView(titleLine)
    mainColumn.addView(subtitle)
    mainColumn.addView(tertiary)
    leadingActionIcon.iconName = ""
    leadingActionIcon.setOnClickListener(null)
    gravity = Gravity.CENTER_VERTICAL
    bindMarket(item, theme)
    item.json
      .optJSONObject("style")
      ?.optJSONObject("container")
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
  }

  override fun bindMarketQuote(item: NativeListItem, theme: JSONObject?) {
    if (retainBoundItem(item)) bindQuote(item, theme)
  }

  private fun cancelMarketHold() {
    longPress?.let(handler::removeCallbacks)
    longPress = null
  }

  override fun onRowTouch(event: MotionEvent) {
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        val item = tag as? NativeListItem
        if (item?.isRowPressEnabled == true) {
          held = true
          longPressFired = false
          startX = event.x
          startY = event.y
          touchX = event.x
          touchY = event.y
          item.json.optString("pressInActionKey").takeIf(String::isNotEmpty)?.let {
            emitAction(it, this, "row")
          }
          item.json.optString("longPressActionKey").takeIf(String::isNotEmpty)?.let { key ->
            val epoch = bindingEpoch
            val task = Runnable {
              val current = tag as? NativeListItem
              if (held && current?.key == item.key && bindingEpoch == epoch) {
                longPress = null
                longPressFired = true
                val location = IntArray(2)
                getLocationInWindow(location)
                onAction?.invoke(
                  current,
                  key,
                  null,
                  NativeListActionOrigin(
                    this,
                    this,
                    bindingEpoch,
                    "row",
                    windowPointPixels =
                      android.graphics.PointF(location[0] + touchX, location[1] + touchY),
                  ),
                )
              }
            }
            longPress = task
            handler.postDelayed(task, 800)
          }
        }
      }
      MotionEvent.ACTION_MOVE -> {
        touchX = event.x
        touchY = event.y
        if (
          event.x < 0 ||
            event.y < 0 ||
            event.x >= width ||
            event.y >= height ||
            kotlin.math.abs(event.x - startX) > dp(10) ||
            kotlin.math.abs(event.y - startY) > dp(10)
        ) {
          cancelMarketHold()
          held = false
          setTouchPressed(false)
        }
      }
      MotionEvent.ACTION_UP,
      MotionEvent.ACTION_CANCEL -> {
        cancelMarketHold()
        held = false
      }
    }
  }

  override fun consumesRowClick(): Boolean {
    val value = longPressFired
    longPressFired = false
    return value
  }

  override fun recycleContent() {
    cancelMarketHold()
    held = false
    longPressFired = false
    leadingFrame.recycle()
    badgeSlots.forEach { it.recycle() }
    imageSignature = ""
  }

  override fun disposeContent() {
    recycleContent()
    leadingFrame.dispose()
    badgeSlots.forEach { it.dispose() }
  }

  private fun marketTypeface(weight: String, fallback: String): Typeface =
    when (weight.ifEmpty { fallback }) {
      "regular" -> NativeListFonts.regular(context)
      "semibold" -> NativeListFonts.semibold(context)
      "bold" -> NativeListFonts.bold(context)
      else -> NativeListFonts.medium(context)
    }

  private fun applyMarketTextStyle(
    view: TextView,
    style: JSONObject?,
    defaultSize: Float,
    defaultLineHeight: Int,
    defaultWeight: String,
    defaultColor: Int,
    defaultAlignment: String,
  ) {
    view.includeFontPadding = false
    view.fontFeatureSettings = "tnum"
    view.textSize =
      sp(style?.optDouble("fontSize", defaultSize.toDouble())?.toFloat() ?: defaultSize)
    view.typeface = marketTypeface(style?.optString("fontWeight").orEmpty(), defaultWeight)
    view.setTextColor(safeColor(style?.optString("color"), defaultColor))
    val alignment = style?.optString("alignment", defaultAlignment) ?: defaultAlignment
    view.gravity =
      Gravity.CENTER_VERTICAL or
        when (alignment) {
          "center" -> Gravity.CENTER_HORIZONTAL
          "end" -> Gravity.END
          else -> Gravity.START
        }
    view.maxLines = style?.optInt("lines", 1)?.coerceIn(1, 3) ?: 1
    view.ellipsize = TextUtils.TruncateAt.END
    TextViewCompat.setLineHeight(
      view,
      dp(
        style?.optDouble("lineHeight", defaultLineHeight.toDouble())?.roundToInt()
          ?: defaultLineHeight
      ),
    )
  }

  // OneKey patch: opt-in Market text uses the same pixel rounding and line box as RN.
  private fun applyMarketTextMetrics(view: TextView, style: JSONObject?) {
    if (style == null || view.visibility != VISIBLE || view.text.isEmpty()) return
    marketOriginalPaintFlags.putIfAbsent(view, view.paintFlags)
    view.paintFlags = view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG or Paint.LINEAR_TEXT_FLAG
    if (style.has("fontSize")) {
      val sourceSize = style.optDouble("fontSize").toFloat() * resources.displayMetrics.density
      view.setTextSize(
        TypedValue.COMPLEX_UNIT_PX,
        kotlin.math.ceil(sourceSize.toDouble()).toFloat(),
      )
    }
    val text = SpannableStringBuilder(view.text)
    if (style.has("fontSize"))
      text.getSpans(0, text.length, AbsoluteSizeSpan::class.java).forEach(text::removeSpan)
    text.getSpans(0, text.length, NativeListLineHeightSpan::class.java).forEach(text::removeSpan)
    if (style.has("lineHeight")) {
      val lineHeight = stylePx(style.optDouble("lineHeight"))
      text.setSpan(
        NativeListLineHeightSpan(lineHeight),
        0,
        text.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
      )
      view.setLineSpacing(0f, 1f)
    }
    view.text = text
    val layout = JSONObject()
    listOf("lines", "truncate", "verticalAlignment", "offsetY").forEach { key ->
      if (style.has(key)) layout.put(key, style.get(key))
    }
    if (layout.length() > 0) {
      applyStyledText(view, layout)
    }
  }

  private fun marketText(value: String, segments: JSONArray?, fontSize: Float): CharSequence {
    if (segments == null || segments.length() == 0) return value
    val result = SpannableStringBuilder()
    for (index in 0 until segments.length()) {
      val segment = segments.getJSONObject(index)
      val start = result.length
      result.append(segment.optString("text"))
      if (segment.optString("style") == "subscript") {
        result.setSpan(
          AbsoluteSizeSpan(sp(kotlin.math.ceil(fontSize * 0.6f)).roundToInt(), true),
          start,
          result.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
        )
      }
    }
    return result
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

  private fun bindMarket(item: NativeListItem, theme: JSONObject?) {
    // Network badges extend beyond the leading image's frame.
    clipChildren = false
    val variant = item.json.optString("variant")
    val style = item.json.optJSONObject("style")
    val imageStyle = style?.optJSONObject("image")
    val imageWidth =
      imageStyle?.optDouble("width", if (variant == "stock") 40.0 else 32.0)?.roundToInt()
        ?: if (variant == "stock") 40 else 32
    val imageHeight =
      imageStyle?.optDouble("height", if (variant == "stock") 40.0 else 32.0)?.roundToInt()
        ?: if (variant == "stock") 40 else 32
    val horizontalPadding =
      style?.optDouble("horizontalPadding", if (variant == "perp") 16.0 else 20.0)?.roundToInt()
        ?: if (variant == "perp") 16 else 20
    val verticalPadding = style?.optDouble("verticalPadding", 12.0)?.roundToInt() ?: 12
    val leadingGap =
      style?.optDouble("leadingGap", if (variant == "perp") 8.0 else 14.0)?.roundToInt()
        ?: if (variant == "perp") 8 else 14
    setPadding(
      dp(horizontalPadding),
      dp(verticalPadding),
      dp(horizontalPadding),
      dp(verticalPadding),
    )

    item.json.optJSONObject("leadingAction")?.let { action ->
      leadingActionIcon.visibility = VISIBLE
      leadingActionIcon.iconName = action.optString("name")
      leadingActionIcon.glyphSizeDp = 24
      leadingActionIcon.tintColor =
        safeColor(action.optString("tintColor"), color(theme, "icon", "#0000009B"))
      leadingActionIcon.isEnabled = !action.optBoolean("disabled", false)
      leadingActionIcon.alpha = if (leadingActionIcon.isEnabled) 1f else 0.4f
      leadingActionIcon.setTag(
        com.facebook.react.R.id.react_test_id,
        action.optString("testID").takeIf(String::isNotEmpty),
      )
      leadingActionIcon.contentDescription = action.optString("accessibilityLabel")
      leadingActionIcon.setOnClickListener {
        emitAction(item, action.optString("actionKey"), null, leadingActionIcon, "leadingAction")
      }
      addView(leadingActionIcon, LayoutParams(dp(36), dp(36)).apply { marginEnd = dp(5) })
    }

    val leading = JSONObject(item.json.getJSONObject("leading").toString())
    imageStyle?.optString("shape")?.takeIf(String::isNotEmpty)?.let { leading.put("shape", it) }
    imageStyle?.optString("contentFit")?.takeIf(String::isNotEmpty)?.let { contentFit ->
      leading.optJSONObject("image")?.put("contentFit", contentFit)
    }
    val shape =
      imageStyle?.optString("shape", leading.optString("shape", "circle"))
        ?: leading.optString("shape", "circle")
    val cornerRadius =
      imageStyle?.takeIf { it.has("cornerRadius") }?.optDouble("cornerRadius")?.toFloat()
        ?: when (shape) {
          "square" -> 0f
          "rounded" -> 8f
          else -> minOf(imageWidth, imageHeight) / 2f
        }
    val resolved = JSONObject(imageStyle?.toString() ?: "{}")
    resolved.put("cornerRadius", cornerRadius.toDouble())
    leadingFrame.bitmapBorderWidth = if (leading.optString("borderColor").isEmpty()) 0 else dp(1)
    leadingFrame.bind(leading, resolved, item.key, theme, false, true)
    addView(
      leadingFrame,
      LayoutParams(
          if (imageStyle?.has("width") == true) stylePx(imageStyle.optDouble("width"))
          else dp(imageWidth),
          if (imageStyle?.has("height") == true) stylePx(imageStyle.optDouble("height"))
          else dp(imageHeight),
        )
        .apply {
          marginEnd =
            if (style?.has("leadingGap") == true) stylePx(style.optDouble("leadingGap"))
            else dp(leadingGap)
        },
    )
    // OneKey patch: retain the source gap without changing other templates.
    // addView(mainColumn, weighted())
    addView(
      mainColumn,
      weighted().apply {
        marginEnd = dp(style?.optDouble("contentTrailingGap", 0.0)?.roundToInt() ?: 0)
      },
    )
    titleLine.packsChildrenAtStart = true
    showText(
      title,
      item.json.optString("title"),
      style?.optJSONObject("title")?.optInt("lines", 1) ?: 1,
    )
    applyMarketTextStyle(
      title,
      style?.optJSONObject("title"),
      16f,
      24,
      "medium",
      color(theme, "primaryText", "#202020"),
      "start",
    )
    val badges = item.json.optJSONArray("badges")
    val titleBadgeGap = style?.optDouble("titleBadgeGap", 4.0)?.roundToInt() ?: 4
    if (badges != null) {
      for (index in 0 until minOf(marketBadgeViews.size, badges.length())) {
        val badge = badges.getJSONObject(index)
        val badgeView = marketBadgeViews[index]
        val badgeLabel = marketBadgeLabels[index]
        val badgeStyle = badge.optJSONObject("style")
        val hasGlyph = badge.optString("iconName") == "verified"
        val remoteIcon = badge.optJSONObject("icon")
        val hasIcon = hasGlyph || remoteIcon != null
        val text = badge.optString("text")
        val toneColor =
          when (badge.optString("tone")) {
            "success" -> color(theme, "positive", "#218358")
            "danger" -> color(theme, "negative", "#CE2C31")
            "info" -> color(theme, "info", "#0D74CE")
            "warning" -> color(theme, "primaryText", "#202020")
            else -> color(theme, "secondaryText", "#646464")
          }
        val foreground = safeColor(badge.optString("textColor"), toneColor)
        badgeView.visibility = VISIBLE
        val iconOnly = hasIcon && text.isEmpty()
        // OneKey patch: Market callers can request the original badge padding.
        val padding = badgeStyle?.optDouble("horizontalPadding", 5.0)?.roundToInt() ?: 5
        val leftPadding =
          if (badgeStyle?.has("horizontalPadding") == true) padding else if (hasIcon) 2 else 5
        // badgeView.setPadding(dp(if (iconOnly) 0 else if (hasIcon) 2 else 5), 0, dp(if (iconOnly)
        // 0 else 5), 0)
        badgeView.setPadding(
          dp(if (iconOnly) 0 else leftPadding),
          0,
          dp(if (iconOnly) 0 else padding),
          0,
        )
        badgeView.background =
          roundedFill(
              safeColor(
                badge.optString("backgroundColor"),
                if (hasIcon && text.isEmpty()) Color.TRANSPARENT
                else color(theme, "strongBackground", "#0000000F"),
              ),
              4f,
            )
            .apply {
              if (badgeStyle != null) this.cornerRadius = 4f * resources.displayMetrics.density
            }
        badgeLabel.text = text
        // OneKey patch: match SizableText's tabular numerals only for explicit Market metrics.
        badgeLabel.fontFeatureSettings = if (badgeStyle != null) "tnum" else null
        badgeLabel.textSize = sp(badgeStyle?.optDouble("fontSize", 11.0)?.toFloat() ?: 11f)
        badgeLabel.typeface =
          marketTypeface(badgeStyle?.optString("fontWeight").orEmpty(), "medium")
        if (badgeStyle?.has("lineHeight") == true) {
          TextViewCompat.setLineHeight(
            badgeLabel,
            dp(badgeStyle.optDouble("lineHeight").roundToInt()),
          )
        }
        badgeLabel.setTextColor(foreground)
        badgeLabel.visibility = if (text.isEmpty()) GONE else VISIBLE
        if (hasGlyph) {
          marketBadgeGlyphs[index].apply {
            iconName = "BadgeVerifiedSolid"
            tintColor = foreground
            visibility = VISIBLE
          }
        }
        remoteIcon?.let { icon ->
          marketBadgeImages[index].visibility = VISIBLE
          marketBadgeImages[index].outlineProvider = circleOutlineProvider
          marketBadgeImages[index].clipToOutline = true
          badgeSlots[index].bind(icon, item.key + ":badge:" + index, "generic")
        }
        val actionKey = badge.optString("actionKey")
        badgeView.isClickable = actionKey.isNotEmpty()
        if (actionKey.isNotEmpty()) {
          badgeView.setOnClickListener {
            emitAction(item, actionKey, null, badgeView, "marketBadge", index)
          }
        }
        badgeView.contentDescription = badge.optString("accessibilityLabel", text)
        titleLine.addView(
          badgeView,
          // OneKey patch: preserve 18dp unless the Market row opts into source metrics.
          // LayoutParams(LayoutParams.WRAP_CONTENT, dp(18)).apply { marginStart = dp(titleBadgeGap)
          // },
          LayoutParams(
              LayoutParams.WRAP_CONTENT,
              dp(badgeStyle?.optDouble("height", 18.0)?.roundToInt() ?: 18),
            )
            .apply { marginStart = dp(titleBadgeGap) },
        )
      }
    }
    subtitle.layoutParams =
      LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
        topMargin = dp(style?.optDouble("lineGap", 0.0)?.roundToInt() ?: 0)
      }
    val subtitleText = item.json.optString("subtitle")
    val subtitleSegments = item.json.optJSONArray("subtitleSegments")
    if (subtitleText.isNotEmpty() || (subtitleSegments?.length() ?: 0) > 0) {
      subtitle.visibility = VISIBLE
      applyMarketTextStyle(
        subtitle,
        style?.optJSONObject("subtitle"),
        14f,
        20,
        "regular",
        color(theme, "secondaryText", "#646464"),
        "start",
      )
      subtitle.text =
        marketText(
          subtitleText,
          subtitleSegments,
          style?.optJSONObject("subtitle")?.optDouble("fontSize", 14.0)?.toFloat() ?: 14f,
        )
    }
    // OneKey patch: preserve independent name metrics, truncation, and volume.
    val subtitlePrefix = item.json.optJSONObject("subtitlePrefix")
    val subtitlePadding = style?.optDouble("subtitleTrailingPadding", 0.0)?.roundToInt() ?: 0
    if (subtitlePrefix != null || subtitlePadding > 0) {
      mainColumn.removeView(subtitle)
      mainColumn.removeView(tertiary)
      marketSubtitleLine.orientation = HORIZONTAL
      marketSubtitleLine.gravity = Gravity.CENTER_VERTICAL
      marketSubtitleLine.packsChildrenAtStart = true
      marketSubtitleLine.leadingTextMaxWidth =
        subtitlePrefix
          ?.takeIf { it.has("maxWidth") }
          ?.optDouble("maxWidth")
          ?.roundToInt()
          ?.let(::dp) ?: Int.MAX_VALUE
      marketSubtitleLine.setPadding(0, 0, dp(subtitlePadding), 0)
      showText(tertiary, subtitlePrefix?.optString("text") ?: "", 1)
      applyMarketTextStyle(
        tertiary,
        subtitlePrefix?.optJSONObject("style"),
        12f,
        16,
        "regular",
        color(theme, "secondaryText", "#646464"),
        "start",
      )
      marketSubtitleLine.addView(tertiary, wrap())
      marketSubtitleLine.addView(
        subtitle,
        wrap().apply {
          if (tertiary.visibility == VISIBLE && subtitle.visibility == VISIBLE) {
            marginStart = dp(subtitlePrefix?.optDouble("gap", 4.0)?.roundToInt() ?: 4)
          }
        },
      )
      mainColumn.addView(
        marketSubtitleLine,
        1,
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
          topMargin = dp(style?.optDouble("lineGap", 0.0)?.roundToInt() ?: 0)
        },
      )
      marketSubtitleLine.visibility =
        if (tertiary.visibility == VISIBLE || subtitle.visibility == VISIBLE) VISIBLE else GONE
    }
    trailingColumn.orientation = HORIZONTAL
    trailingColumn.gravity = Gravity.END or Gravity.CENTER_VERTICAL
    addView(trailingColumn, wrap())
    bindQuote(item, theme)
    val nextImageSignature =
      nativeListCanonical(
        JSONObject()
          .put("key", item.key)
          .put("leading", leading)
          .put("fit", resolved.opt("contentFit"))
      )
    item.json
      .optJSONObject("diagnostics")
      ?.optString("imageBindActionKey")
      ?.takeIf(String::isNotEmpty)
      ?.takeIf { nextImageSignature != imageSignature }
      ?.takeIf {
        leading.optJSONObject("image") != null || leading.optJSONObject("networkImage") != null
      }
      ?.let { actionKey -> onAction?.invoke(item, actionKey, null, null) }
    imageSignature = nextImageSignature
  }

  private fun bindQuote(item: NativeListItem, theme: JSONObject?) {
    val style = item.json.optJSONObject("style")
    val priceStyle = style?.optJSONObject("price")
    val price = trailingViews[0]
    price.isClickable = false
    price.isLongClickable = false
    price.visibility = VISIBLE
    price.maxWidth = dp(112)
    applyMarketTextStyle(
      price,
      priceStyle,
      16f,
      24,
      "medium",
      color(theme, "primaryText", "#202020"),
      "end",
    )
    price.text =
      marketText(
        item.json.optString("price"),
        item.json.optJSONArray("priceSegments"),
        priceStyle?.optDouble("fontSize", 16.0)?.toFloat() ?: 16f,
      )
    val trailingGap = style?.optDouble("trailingGap", 8.0)?.roundToInt() ?: 8
    price.layoutParams =
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
        marginEnd = dp(trailingGap)
      }

    val changeData = item.json.getJSONObject("change")
    val changeStyle = style?.optJSONObject("change")
    val change = trailingViews[1]
    change.isClickable = false
    change.isLongClickable = false
    val toneColor =
      when (changeData.optString("tone")) {
        "positive" -> color(theme, "positive", "#218358")
        "negative" -> color(theme, "negative", "#CE2C31")
        else -> color(theme, "secondaryText", "#8D8D8D")
      }
    val textColor =
      safeColor(changeData.optString("textColor"), color(theme, "inverseText", "#FFFFFF"))
    change.visibility = VISIBLE
    applyMarketTextStyle(change, changeStyle, 14f, 20, "medium", textColor, "center")
    change.text =
      marketText(
        changeData.optString("text"),
        changeData.optJSONArray("textSegments"),
        changeStyle?.optDouble("fontSize", 14.0)?.toFloat() ?: 14f,
      )
    change.background =
      roundedFill(
        safeColor(changeData.optString("backgroundColor"), toneColor),
        style?.optDouble("changeCornerRadius", 8.0)?.toFloat() ?: 8f,
      )
    change.layoutParams =
      LayoutParams(
        dp(style?.optDouble("changeWidth", 80.0)?.roundToInt() ?: 80),
        dp(style?.optDouble("changeHeight", 32.0)?.roundToInt() ?: 32),
      )
    applyMarketTextMetrics(title, style?.optJSONObject("title"))
    applyMarketTextMetrics(subtitle, style?.optJSONObject("subtitle"))
    applyMarketTextMetrics(
      tertiary,
      item.json.optJSONObject("subtitlePrefix")?.optJSONObject("style"),
    )
    applyMarketTextMetrics(price, priceStyle)
    applyMarketTextMetrics(change, changeStyle)
    val badges = item.json.optJSONArray("badges")
    marketBadgeLabels.forEachIndexed { index, label ->
      val badge = badges?.optJSONObject(index)
      val badgeStyle = badge?.optJSONObject("style")
      applyMarketTextMetrics(label, badgeStyle)
      if (
        badge != null &&
          label.visibility == VISIBLE &&
          badgeStyle?.has("horizontalPadding") == true &&
          badge.optJSONObject("icon") == null &&
          badge.optString("iconName").isEmpty()
      ) {
        // OneKey patch: match RN's intrinsic text width and one rounded padding pair.
        label.layoutParams =
          wrap().apply { width = label.paint.measureText(label.text.toString()).roundToInt() }
        val padding = badgeStyle.optDouble("horizontalPadding") * resources.displayMetrics.density
        marketBadgeViews[index].setPadding(
          kotlin.math.floor(padding).toInt(),
          0,
          (padding * 2).roundToInt() - kotlin.math.floor(padding).toInt(),
          0,
        )
      }
    }
    contentDescription =
      item.json.optString(
        "accessibilityLabel",
        listOf(
            item.json.optString("title"),
            item.json.optString("subtitle"),
            item.json.optString("price"),
            changeData.optString("text"),
          )
          .filter(String::isNotEmpty)
          .joinToString(", "),
      )
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val marketItem = (tag as? NativeListItem)?.takeIf { it.type == "market" }
    val marketStyle = marketItem?.json?.optJSONObject("style")
    if (marketStyle?.has("horizontalPadding") == true) {
      // OneKey patch: Yoga rounds the two absolute edges, not both padding values.
      val sourcePadding =
        marketStyle.optDouble("horizontalPadding") * resources.displayMetrics.density
      val width = MeasureSpec.getSize(widthMeasureSpec)
      val rowHeight = marketItem.styledHeight?.let(::stylePx) ?: modelHeight(marketItem, "linear")
      val sourceVerticalPadding =
        marketStyle
          .takeIf { it.has("verticalPadding") && rowHeight != null }
          ?.optDouble("verticalPadding")
          ?.times(resources.displayMetrics.density)
      setPadding(
        sourcePadding.roundToInt(),
        sourceVerticalPadding?.roundToInt() ?: paddingTop,
        width - (width - sourcePadding).roundToInt(),
        if (sourceVerticalPadding != null && rowHeight != null)
          rowHeight - (rowHeight - sourceVerticalPadding).roundToInt()
        else paddingBottom,
      )
    }

    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    val item = tag as? NativeListItem ?: return
    val hasContainerAlignment =
      item.json
        .optJSONObject("style")
        ?.optJSONObject("container")
        ?.has("contentVerticalAlignment") == true
    if (item.type == "market" && item.json.optJSONObject("style") != null) {
      // OneKey patch: preserve Yoga's half-pixel centering for Market columns and badges.
      for (column in listOf(leadingFrame, mainColumn, trailingColumn)) {
        if (!hasContainerAlignment && column.parent === this && column.visibility != GONE) {
          column.offsetTopAndBottom((height - column.height + 1) / 2 - column.top)
        }
      }
      // OneKey patch: token avatars stay centered in the row when text rounding adds a pixel.
      if (
        !hasContainerAlignment &&
          item.json.optString("variant") != "token" &&
          leadingFrame.parent === this &&
          mainColumn.parent === this
      ) {
        leadingFrame.offsetTopAndBottom(
          mainColumn.top + (mainColumn.height - leadingFrame.height + 1) / 2 - leadingFrame.top
        )
      }
      // OneKey patch: preserve Yoga's absolute-edge rounding for the Market network badge.
      if (
        item.json.optJSONObject("leading")?.optJSONObject("networkImage") != null &&
          item.json.optString("variant") == "token"
      ) {
        val recycler = parent as? RecyclerView
        val adapter = recycler?.adapter as? NativeListAdapter
        val position = recycler?.getChildAdapterPosition(this) ?: RecyclerView.NO_POSITION
        if (
          recycler != null &&
            adapter != null &&
            position != RecyclerView.NO_POSITION &&
            adapter.itemAt(position)?.key == item.key
        ) {
          val density = resources.displayMetrics.density.toDouble()
          val rowTop = adapter.marketSourceEdgePx(position)
          val rowHeight = adapter.marketSourceEdgePx(position + 1) - rowTop
          val imageHeight =
            item.json.optJSONObject("style")?.optJSONObject("image")?.optDouble("height", 32.0)
              ?: 32.0
          val contentTop = (recycler.paddingTop / density).roundToInt() * density
          val badgeTop =
            contentTop +
              rowTop +
              (rowHeight - imageHeight * density) / 2 +
              (imageHeight - 16) * density
          val badgeHeight = (badgeTop + 20 * density).roundToInt() - badgeTop.roundToInt()
          val top = ((imageHeight - 16) * density).roundToInt()
          leadingFrame.layoutNetworkBackdrop(top, badgeHeight)
        }
      }
      for (badge in marketBadgeViews) {
        if (badge.parent === titleLine && badge.visibility != GONE) {
          badge.offsetTopAndBottom((titleLine.height - badge.height + 1) / 2 - badge.top)
        }
      }
      // OneKey patch: preserve fractional text advances and gaps until the final edge.
      val style = item.json.optJSONObject("style")
      for ((line, gap) in
        listOf(
          titleLine to style.optDouble("titleBadgeGap", 4.0),
          marketSubtitleLine to
            (item.json.optJSONObject("subtitlePrefix")?.optDouble("gap", 4.0) ?: 0.0),
        )) {
        var cursor = 0f
        var hasPrevious = false
        for (index in 0 until line.childCount) {
          val child = line.getChildAt(index)
          if (child.visibility == GONE) continue
          if (hasPrevious) {
            cursor += (gap * resources.displayMetrics.density).toFloat()
            child.offsetLeftAndRight(cursor.roundToInt() - child.left)
          }
          val advance =
            if (index == 0 && child is TextView) {
              val textWidth =
                if (line === titleLine) child.paint.measureText(child.text.toString())
                else child.layout?.getLineWidth(0)
              textWidth?.coerceAtMost(child.width.toFloat()) ?: child.width.toFloat()
            } else child.width.toFloat()
          cursor = if (hasPrevious) cursor + advance else child.left + advance
          hasPrevious = true
        }
      }
    }
  }
}
