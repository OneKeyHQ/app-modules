package com.margelo.nitro.nativelist

import android.graphics.Color
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.facebook.react.uimanager.ThemedReactContext
import com.margelo.nitro.onekeyimage.OneKeyImageReusableView
import androidx.core.graphics.PathParser
import androidx.core.widget.TextViewCompat
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

/** React Native color strings use CSS #RRGGBBAA ordering; Android expects #AARRGGBB. */
internal fun parseNativeListColor(value: String): Int {
  val normalized = value.trim()
  val androidValue = if (
    normalized.length == 9 && normalized[0] == '#' &&
    normalized.drop(1).all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
  ) {
    "#${normalized.takeLast(2)}${normalized.substring(1, 7)}"
  } else {
    normalized
  }
  return Color.parseColor(androidValue)
}

internal object NativeListScale {
  private const val STANDARD_DP_WIDTH_THRESHOLD = 400

  fun factor(resources: android.content.res.Resources): Float {
    val widthDp = resources.configuration.screenWidthDp
    return if (widthDp in 1 until STANDARD_DP_WIDTH_THRESHOLD) 0.9f else 1f
  }

  fun dp(resources: android.content.res.Resources, value: Int): Int =
    (value * factor(resources) * resources.displayMetrics.density).roundToInt()

  fun dp(resources: android.content.res.Resources, value: Float): Float =
    (value * factor(resources) * resources.displayMetrics.density).roundToInt().toFloat()

  fun font(resources: android.content.res.Resources, value: Float): Float =
    if (factor(resources) == 1f) value else (value * factor(resources)).roundToInt().toFloat()
}

internal object NativeListFonts {
  private val cache = mutableMapOf<String, Typeface>()

  private fun load(context: android.content.Context, weight: String, fallback: String): Typeface =
    cache.getOrPut(weight) {
      try {
        Typeface.createFromAsset(context.assets, "fonts/Roobert-$weight.ttf")
      } catch (_: RuntimeException) {
        Typeface.create(fallback, Typeface.NORMAL)
      }
    }

  fun regular(context: android.content.Context) = load(context, "Regular", "sans-serif")
  fun medium(context: android.content.Context) = load(context, "Medium", "sans-serif-medium")
  fun semibold(context: android.content.Context) = load(context, "SemiBold", "sans-serif-medium")
  fun bold(context: android.content.Context) = load(context, "Bold", "sans-serif")
}

internal data class NativeSelectionTarget(val scope: String, val key: String?)

private class DottedUnderlineTextView(context: android.content.Context) : TextView(context) {
  var showsDottedUnderline = false
  var dottedUnderlineColor = Color.TRANSPARENT
  private val dottedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (!showsDottedUnderline || text.isEmpty()) return
    dottedPaint.color = dottedUnderlineColor
    val radius = NativeListScale.dp(resources, 0.75f)
    val spacing = NativeListScale.dp(resources, 4f)
    val lineWidth = paint.measureText(text.toString()).coerceAtMost(width.toFloat())
    val y = height - radius
    var x = NativeListScale.dp(resources, 1f)
    while (x <= lineWidth - radius) {
      canvas.drawCircle(x, y, radius, dottedPaint)
      x += spacing
    }
  }
}

private class PackedTitleLineLayout(context: android.content.Context) : LinearLayout(context) {
  var packsChildrenAtStart = false

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    if (!packsChildrenAtStart || childCount < 2) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec)
      return
    }

    val title = getChildAt(0)
    val badge = getChildAt(1)
    val widthMode = MeasureSpec.getMode(widthMeasureSpec)
    val widthSize = MeasureSpec.getSize(widthMeasureSpec)
    if (badge.visibility != GONE) {
      measureChildWithMargins(
        badge,
        widthMeasureSpec,
        paddingLeft + paddingRight,
        heightMeasureSpec,
        paddingTop + paddingBottom,
      )
    }

    val badgeMargins = badge.layoutParams as MarginLayoutParams
    val badgeWidth = if (badge.visibility == GONE) 0 else {
      badge.measuredWidth + badgeMargins.leftMargin + badgeMargins.rightMargin
    }
    val titleMargins = title.layoutParams as MarginLayoutParams
    if (widthMode != MeasureSpec.UNSPECIFIED) {
      (title as TextView).maxWidth = (widthSize - paddingLeft - paddingRight - badgeWidth -
        titleMargins.leftMargin - titleMargins.rightMargin).coerceAtLeast(0)
    }
    (title.layoutParams as LayoutParams).apply {
      width = LayoutParams.WRAP_CONTENT
      weight = 0f
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    if (widthMode != MeasureSpec.UNSPECIFIED) {
      setMeasuredDimension(widthSize, measuredHeight)
    }
  }
}

private class NativeListTableColumnView(context: android.content.Context) : LinearLayout(context) {
  private val primaryLine = LinearLayout(context)
  private val primary = TextView(context)
  private val badges = LinearLayout(context)
  private val secondaryLine = LinearLayout(context)
  private val secondaryLeading = TextView(context)
  private val secondary = TextView(context)

  init {
    orientation = VERTICAL
    primaryLine.orientation = HORIZONTAL
    primaryLine.gravity = Gravity.CENTER_VERTICAL
    badges.orientation = HORIZONTAL
    badges.gravity = Gravity.CENTER_VERTICAL
    primary.includeFontPadding = false
    primary.fontFeatureSettings = "tnum"
    primary.maxLines = 1
    primary.ellipsize = TextUtils.TruncateAt.END
    primary.textSize = sp(14f)
    primary.typeface = NativeListFonts.medium(context)
    TextViewCompat.setLineHeight(primary, dp(20))
    secondaryLine.orientation = HORIZONTAL
    secondaryLine.gravity = Gravity.CENTER_VERTICAL
    listOf(secondaryLeading, secondary).forEach { label ->
      label.includeFontPadding = false
      label.fontFeatureSettings = "tnum"
      label.maxLines = 1
      label.ellipsize = TextUtils.TruncateAt.END
      label.textSize = sp(12f)
      label.typeface = NativeListFonts.regular(context)
      TextViewCompat.setLineHeight(label, dp(16))
    }
    secondaryLeading.maxWidth = dp(120)
    secondaryLine.addView(secondaryLeading, LayoutParams(LayoutParams.WRAP_CONTENT, dp(16)))
    secondaryLine.addView(secondary, LayoutParams(LayoutParams.WRAP_CONTENT, dp(16)).apply {
      marginStart = dp(4)
    })
    primaryLine.addView(primary, LayoutParams(LayoutParams.WRAP_CONTENT, dp(20)))
    primaryLine.addView(badges, LayoutParams(LayoutParams.WRAP_CONTENT, dp(16)).apply {
      marginStart = dp(6)
    })
    addView(primaryLine, LayoutParams(LayoutParams.WRAP_CONTENT, dp(20)))
    addView(secondaryLine, LayoutParams(LayoutParams.MATCH_PARENT, dp(16)).apply {
      topMargin = dp(4)
    })
  }

  fun reset() {
    primary.text = ""
    secondaryLeading.text = ""
    secondary.text = ""
    secondaryLeading.visibility = GONE
    secondary.visibility = GONE
    secondaryLine.visibility = GONE
    badges.removeAllViews()
    badges.visibility = GONE
  }

  fun bind(
    column: JSONObject,
    rowBadges: JSONArray?,
    primaryColor: Int,
    secondaryColor: Int,
    infoColor: Int,
  ) {
    reset()
    gravity = when (column.optString("alignment", "start")) {
      "center" -> Gravity.CENTER_HORIZONTAL
      "end" -> Gravity.END
      else -> Gravity.START
    }
    primary.gravity = gravity
    secondaryLeading.gravity = gravity
    secondary.gravity = gravity
    primary.text = column.optString("text")
    primary.setTextColor(primaryColor)
    if (rowBadges != null && rowBadges.length() > 0) {
      badges.visibility = VISIBLE
      for (index in 0 until minOf(2, rowBadges.length())) {
        val badge = TextView(context).apply {
          includeFontPadding = false
          fontFeatureSettings = "tnum"
          gravity = Gravity.CENTER
          text = rowBadges.getJSONObject(index).optString("text")
          textSize = sp(10f)
          typeface = NativeListFonts.regular(context)
          setTextColor(infoColor)
          setPadding(dp(6), 0, dp(6), 0)
          background = GradientDrawable().apply {
            setColor(parseNativeListColor("#008FF519"))
            cornerRadius = dp(4).toFloat()
          }
        }
        badges.addView(badge, LayoutParams(LayoutParams.WRAP_CONTENT, dp(16)).apply {
          if (index > 0) marginStart = dp(4)
        })
      }
    }
    val secondaryLeadingText = column.optString("secondaryLeadingText")
    if (secondaryLeadingText.isNotEmpty()) {
      secondaryLeading.text = secondaryLeadingText
      secondaryLeading.setTextColor(secondaryColor)
      secondaryLeading.visibility = VISIBLE
      secondaryLine.visibility = VISIBLE
    }
    val secondaryText = column.optString("secondaryText")
    if (secondaryText.isNotEmpty()) {
      secondary.text = secondaryText
      secondary.setTextColor(secondaryColor)
      secondary.visibility = VISIBLE
      secondaryLine.visibility = VISIBLE
    }
  }

  private fun dp(value: Int): Int = NativeListScale.dp(resources, value)
  private fun sp(value: Float): Float = NativeListScale.font(resources, value)
}

internal class NativeListRowView(
  private val reactContext: ThemedReactContext,
) : LinearLayout(reactContext) {
  private val leadingFrame = FrameLayout(context)
  private val leadingImages = List(3) { OneKeyImageReusableView(reactContext) }
  private val leadingOverlayBackground = View(context)
  private val leadingCornerIconFrame = FrameLayout(context)
  private val leadingCornerIcon = OneKeyIconView(context)
  private val leadingFallback = TextView(context)
  private val leadingIcon = OneKeyIconView(context)
  private val favoriteIcon = OneKeyIconView(context)
  private val headerTitleIcon = OneKeyIconView(context)
  private val headerValueIcon = OneKeyIconView(context)
  private val leadingActionIcon = OneKeyIconView(context)
  private val secondaryImage = OneKeyImageReusableView(reactContext)
  private val mediaNetworkImage = OneKeyImageReusableView(reactContext)
  private val metricVisualImages = List(5) { OneKeyImageReusableView(reactContext) }
  private val mainColumn = LinearLayout(context)
  private val mediaMetadataRow = LinearLayout(context)
  private val titleLine = PackedTitleLineLayout(context)
  private val title = DottedUnderlineTextView(context)
  private val subtitle = TextView(context)
  private val tertiary = TextView(context)
  private val status = TextView(context)
  private val metricSubtitle = TextView(context)
  private val badgeLine = TextView(context)
  private val activityContentRow = LinearLayout(context)
  private val actionLine = LinearLayout(context)
  private val actionViews = List(3) { TextView(context) }
  private val trailingColumn = LinearLayout(context)
  private val trailingViews = List(2) { TextView(context) }
  private val trailingIcons = List(2) { OneKeyIconView(context) }
  private val checkbox = OneKeyCheckboxView(context)
  private val spinner = ProgressBar(context)
  private val dataContainer = LinearLayout(context)
  private val dataColumns = List(4) { TextView(context) }
  private val tableDataContainer = LinearLayout(context)
  private val tableDataColumns = List(4) { NativeListTableColumnView(context) }
  private val unreadDot = View(context)
  private val mediaBadge = TextView(context)
  private val skeletonPrimary = View(context)
  private val skeletonSecondary = View(context)
  private var isMediaTile = false
  private var boundKey: String? = null
  private var boundCheckboxData: JSONObject? = null
  private var currentLayout = "linear"
  private var restingRowBackground: Drawable? = null
  private var pressedRowBackground: Drawable? = null
  private var checkboxCheckedColor = Color.rgb(32, 32, 32)
  private var checkboxUncheckedColor = Color.rgb(252, 252, 252)
  private var checkboxBorderColor = Color.rgb(206, 206, 206)
  private var iconSubduedColor = Color.rgb(141, 141, 141)
  private var visualBackdropColor = Color.WHITE
  private val circleOutlineProvider = object : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
      outline.setOval(0, 0, view.width, view.height)
    }
  }
  private val separatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private var showsSeparator = false

  var onRowPress: ((NativeListItem) -> Unit)? = null
  var onAction: ((NativeListItem, String, NativeSelectionTarget?) -> Unit)? = null

  init {
    gravity = Gravity.CENTER_VERTICAL
    isClickable = true
    isFocusable = true

    leadingFallback.gravity = Gravity.CENTER
    leadingFallback.typeface = NativeListFonts.bold(context)
    leadingFrame.addView(leadingFallback)
    leadingFrame.addView(leadingIcon)
    leadingFrame.addView(leadingImages[0])
    leadingFrame.addView(leadingOverlayBackground)
    leadingFrame.addView(leadingImages[1])
    leadingFrame.addView(leadingImages[2])
    leadingCornerIconFrame.addView(leadingCornerIcon)
    leadingFrame.addView(leadingCornerIconFrame)
    leadingFrame.addView(unreadDot)
    leadingFrame.addView(mediaBadge)
    leadingFrame.clipChildren = false
    leadingFrame.clipToPadding = false
    leadingIcon.layoutParams = FrameLayout.LayoutParams(dp(18), dp(18), Gravity.CENTER)
    favoriteIcon.layoutParams = LayoutParams(dp(24), dp(24))
    leadingActionIcon.layoutParams = LayoutParams(dp(24), dp(24))
    leadingCornerIcon.layoutParams = FrameLayout.LayoutParams(dp(18), dp(18), Gravity.CENTER)
    unreadDot.layoutParams = FrameLayout.LayoutParams(dp(8), dp(8), Gravity.TOP or Gravity.END)
    mainColumn.orientation = VERTICAL
    mainColumn.gravity = Gravity.CENTER_VERTICAL
    title.typeface = NativeListFonts.regular(context)
    subtitle.typeface = NativeListFonts.regular(context)
    tertiary.typeface = NativeListFonts.regular(context)
    status.typeface = NativeListFonts.regular(context)
    metricSubtitle.typeface = NativeListFonts.regular(context)
    badgeLine.typeface = NativeListFonts.medium(context)
    listOf(title, subtitle, tertiary, status, metricSubtitle, badgeLine).forEach {
      it.includeFontPadding = false
      it.fontFeatureSettings = "tnum"
    }
    subtitle.maxLines = 2
    status.maxLines = 1
    metricSubtitle.maxLines = 1
    badgeLine.maxLines = 1
    titleLine.orientation = HORIZONTAL
    titleLine.gravity = Gravity.CENTER_VERTICAL
    titleLine.addView(title, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
    titleLine.addView(badgeLine, wrap())
    mainColumn.addView(titleLine)
    mainColumn.addView(subtitle)
    mainColumn.addView(tertiary)
    mainColumn.addView(status)
    mainColumn.addView(metricSubtitle)

    actionLine.orientation = HORIZONTAL
    actionViews.forEach { action ->
      action.typeface = NativeListFonts.semibold(context)
      action.includeFontPadding = false
      action.textSize = sp(14f)
      TextViewCompat.setLineHeight(action, dp(20))
      action.setPadding(dp(8), dp(4), dp(8), dp(4))
      actionLine.addView(action)
    }
    mainColumn.addView(actionLine)

    trailingColumn.orientation = VERTICAL
    trailingColumn.gravity = Gravity.END or Gravity.CENTER_VERTICAL
    trailingViews.forEach { trailingColumn.addView(it) }
    trailingIcons.forEach { trailingColumn.addView(it) }
    trailingColumn.addView(checkbox)
    trailingColumn.addView(spinner)

    dataContainer.orientation = HORIZONTAL
    dataContainer.gravity = Gravity.CENTER_VERTICAL
    dataColumns.forEach { dataContainer.addView(it) }
    tableDataContainer.orientation = HORIZONTAL
    tableDataContainer.gravity = Gravity.CENTER_VERTICAL
    tableDataColumns.forEach { tableDataContainer.addView(it) }

    mediaBadge.gravity = Gravity.CENTER
    mediaBadge.typeface = NativeListFonts.regular(context)
    secondaryImage.outlineProvider = object : ViewOutlineProvider() {
      override fun getOutline(view: View, outline: Outline) {
        outline.setRoundRect(0, 0, view.width, view.height, dp(6).toFloat())
      }
    }
    secondaryImage.clipToOutline = true

    setOnTouchListener { _, event ->
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> if (isEnabled) {
          if ((tag as? NativeListItem)?.type == "mediaTile") {
            leadingFrame.alpha = 0.8f
          } else {
            background = pressedRowBackground
          }
        }
        MotionEvent.ACTION_MOVE -> if (
          event.x < 0 || event.y < 0 || event.x >= width || event.y >= height
        ) {
          restoreRestingBackground()
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> restoreRestingBackground()
      }
      false
    }
    setOnClickListener { view -> (view.tag as? NativeListItem)?.let { onRowPress?.invoke(it) } }
    setWillNotDraw(false)
  }

  override fun dispatchDraw(canvas: Canvas) {
    super.dispatchDraw(canvas)
    if (showsSeparator) {
      val start = if ((tag as? NativeListItem)?.type == "identity") dp(60).toFloat() else dp(12).toFloat()
      canvas.drawLine(start, height - 1f, width.toFloat(), height - 1f, separatorPaint)
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    if (isMediaTile) {
      val availableWidth = (MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight)
        .coerceAtLeast(0)
      leadingFrame.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, availableWidth)
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
  }

  fun bind(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    listOrientation: String,
    itemIndex: Int?,
    selected: Boolean,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    boundKey = item.key
    currentLayout = layout
    tag = item
    leadingImages.forEach(OneKeyImageReusableView::prepareForReuse)
    secondaryImage.prepareForReuse()
    mediaNetworkImage.prepareForReuse()
    metricVisualImages.forEach(OneKeyImageReusableView::prepareForReuse)
    resetViews()

    val primary = color(theme, "primaryText", "#000000DF")
    val secondary = color(theme, "secondaryText", "#0000009B")
    val accent = color(theme, "accent", "#0D8200FC")
    checkboxCheckedColor = primary
    checkboxUncheckedColor = color(theme, "inverseText", "#FCFCFC")
    checkboxBorderColor = Color.argb(
      0x31,
      0,
      0,
      0,
    )
    iconSubduedColor = color(theme, "iconSubdued", "#00000072")
    visualBackdropColor = color(theme, "rowBackground", "#FFFFFF")
    unreadDot.background = roundedFill(
      parseNativeListColor("#E5484D"),
      4f,
    )
    applySelectionState(item, theme, layout, itemIndex, selected)
    pressedRowBackground = groupedBackground(
      when (item.type) {
        "rail" -> "rail"
        "mediaTile" -> "mediaTile"
        else -> "single"
      },
      color(
        theme,
        if (item.type == "rail") "strongBackground" else "rowPressedBackground",
        if (item.type == "rail") "#0000000F" else "#00000017",
      ),
    )
    background = restingRowBackground
    if (layout == "table") {
      if (item.type == "dataRow") {
        setPadding(dp(20), dp(10), dp(20), dp(10))
      } else {
        setPadding(dp(16), dp(8), dp(16), dp(8))
      }
    }
    title.setTextColor(primary)
    subtitle.setTextColor(secondary)
    tertiary.setTextColor(secondary)
    status.setTextColor(secondary)
    metricSubtitle.setTextColor(secondary)
    badgeLine.setTextColor(accent)
    separatorPaint.color = color(theme, "separator", "#0000001F")
    separatorPaint.strokeWidth = 1f
    showsSeparator = item.json.optBoolean("separator", false) &&
      !item.key.startsWith("token-") &&
      !item.key.startsWith("balance-token-") &&
      item.key != "linear-custom-token"
    invalidate()
    trailingViews.forEach { it.setTextColor(primary) }
    isEnabled = !item.json.optBoolean("disabled", false)
    alpha = if (isEnabled) 1f else 0.5f
    contentDescription = item.json.optString("accessibilityLabel", item.json.optString("title"))

    when (item.type) {
      "identity" -> bindIdentity(item, theme, selected, checkboxState)
      "rail" -> bindRail(item, theme)
      "activity" -> bindActivity(item, theme)
      "message" -> bindMessage(item, theme)
      "dataRow" -> bindDataRow(item, theme, checkboxState)
      "mediaTile" -> bindMediaTile(item, theme)
      "metricCard" -> bindMetricCard(item, theme)
      "sectionHeader" -> bindSectionHeader(item, theme, checkboxState)
      "action" -> bindAction(item, theme, checkboxState)
      "system" -> bindSystem(item, theme)
    }
    applySize(item)
    applyListOrientation(item, listOrientation)
  }

  fun recycle() {
    restoreRestingBackground()
    boundKey = null
    leadingImages.forEach(OneKeyImageReusableView::prepareForReuse)
    secondaryImage.prepareForReuse()
    mediaNetworkImage.prepareForReuse()
    metricVisualImages.forEach(OneKeyImageReusableView::prepareForReuse)
  }

  fun bindSelection(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    itemIndex: Int?,
    selected: Boolean,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    if (boundKey != item.key) return
    applySelectionState(item, theme, layout, itemIndex, selected)
    if (item.type == "identity" && item.json.optString("presentation") == "walletSidebar") {
      title.setTextColor(
        color(
          theme,
          if (selected) "primaryText" else "secondaryText",
          if (selected) "#FFFFFFED" else "#FFFFFFAF",
        ),
      )
    }
    boundCheckboxData?.let { bindCheckbox(item, it, checkboxState) }
  }

  fun bindStableSummary(item: NativeListItem) {
    if (
      boundKey != item.key ||
      item.type != "sectionHeader" ||
      item.json.optString("variant") != "summary"
    ) {
      return
    }
    tag = item
    contentDescription = item.json.optString("accessibilityLabel", item.json.optString("title"))
    title.text = item.json.optString("title")
    trailingViews[0].text = item.json.optString("value")
  }

  fun dispose() {
    restoreRestingBackground()
    leadingImages.forEach(OneKeyImageReusableView::dispose)
    secondaryImage.dispose()
    mediaNetworkImage.dispose()
    metricVisualImages.forEach(OneKeyImageReusableView::dispose)
  }

  private fun resetViews() {
    restoreRestingBackground()
    activityContentRow.removeAllViews()
    (actionLine.parent as? ViewGroup)?.removeView(actionLine)
    removeAllViews()
    orientation = HORIZONTAL
    gravity = Gravity.CENTER_VERTICAL
    minimumHeight = 0
    setPadding(dp(12), dp(8), dp(12), dp(8))
    isMediaTile = false
    boundCheckboxData = null
    mainColumn.orientation = VERTICAL
    mainColumn.gravity = Gravity.CENTER_VERTICAL
    mediaMetadataRow.removeView(subtitle)
    mediaMetadataRow.removeView(mediaNetworkImage)
    mainColumn.removeView(mediaMetadataRow)
    mainColumn.removeView(titleLine)
    mainColumn.removeView(subtitle)
    mainColumn.removeView(tertiary)
    mainColumn.addView(titleLine, 0)
    mainColumn.addView(subtitle, 1)
    mainColumn.addView(tertiary, 2)
    mainColumn.addView(actionLine)
    titleLine.packsChildrenAtStart = false
    titleLine.removeView(headerTitleIcon)
    trailingColumn.removeView(headerValueIcon)
    title.gravity = Gravity.START
    title.ellipsize = null
    title.maxWidth = Int.MAX_VALUE
    title.setHorizontallyScrolling(false)
    title.text = ""
    subtitle.text = ""
    tertiary.text = ""
    status.text = ""
    metricSubtitle.text = ""
    badgeLine.text = ""
    title.setLineSpacing(0f, 1f)
    subtitle.setLineSpacing(0f, 1f)
    title.letterSpacing = 0f
    title.showsDottedUnderline = false
    title.setPadding(0, 0, 0, 0)
    subtitle.letterSpacing = 0f
    status.letterSpacing = 0f
    badgeLine.letterSpacing = 0f
    status.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    subtitle.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    title.layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
    badgeLine.layoutParams = wrap()
    titleLine.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    title.visibility = GONE
    subtitle.visibility = GONE
    tertiary.visibility = GONE
    status.visibility = GONE
    metricSubtitle.visibility = GONE
    badgeLine.visibility = GONE
    badgeLine.background = null
    badgeLine.setPadding(0, 0, 0, 0)
    actionLine.visibility = GONE
    actionViews.forEach {
      it.visibility = GONE
      it.background = null
      it.setOnClickListener(null)
    }
    trailingColumn.gravity = Gravity.END or Gravity.CENTER_VERTICAL
    trailingColumn.orientation = VERTICAL
    trailingColumn.layoutParams = wrap()
    trailingViews.forEach {
      it.visibility = GONE
      it.gravity = Gravity.END
      it.layoutParams = wrap()
      it.background = null
      it.setPadding(0, 0, 0, 0)
      it.setOnClickListener(null)
    }
    trailingIcons.forEach {
      it.visibility = GONE
      it.layoutParams = LayoutParams(dp(24), dp(24)).apply { gravity = Gravity.END }
      it.glyphSizeDp = null
      it.alpha = 1f
      it.isEnabled = true
      it.setOnClickListener(null)
    }
    checkbox.visibility = GONE
    checkbox.layoutParams = LayoutParams(dp(20), dp(20)).apply { gravity = Gravity.END }
    checkbox.alpha = 1f
    checkbox.setOnClickListener(null)
    spinner.visibility = GONE
    spinner.layoutParams = LayoutParams(dp(20), dp(20)).apply { gravity = Gravity.END }
    spinner.alpha = 1f
    leadingFrame.visibility = GONE
    leadingFrame.background = null
    leadingFrame.clipChildren = false
    leadingFrame.clipToPadding = false
    secondaryImage.visibility = GONE
    secondaryImage.foreground = null
    mediaNetworkImage.visibility = GONE
    metricVisualImages.forEach {
      (it.parent as? ViewGroup)?.removeView(it)
      it.visibility = GONE
      it.background = null
      it.clipToOutline = false
      it.outlineProvider = ViewOutlineProvider.BACKGROUND
    }
    unreadDot.visibility = GONE
    mediaBadge.visibility = GONE
    mediaBadge.text = ""
    leadingIcon.visibility = GONE
    leadingIcon.iconName = ""
    leadingIcon.layoutParams = FrameLayout.LayoutParams(dp(18), dp(18), Gravity.CENTER)
    favoriteIcon.visibility = GONE
    favoriteIcon.iconName = ""
    headerTitleIcon.visibility = GONE
    headerTitleIcon.iconName = ""
    headerValueIcon.visibility = GONE
    headerValueIcon.iconName = ""
    leadingActionIcon.visibility = GONE
    leadingActionIcon.iconName = ""
    leadingActionIcon.glyphSizeDp = 24
    leadingActionIcon.setOnClickListener(null)
    mainColumn.visibility = VISIBLE
    dataContainer.visibility = GONE
    dataColumns.forEach { it.visibility = GONE }
    tableDataContainer.visibility = GONE
    tableDataColumns.forEach {
      it.reset()
      it.visibility = GONE
    }
    leadingOverlayBackground.visibility = GONE
    leadingCornerIconFrame.visibility = GONE
    leadingCornerIcon.iconName = ""
    leadingImages.forEach {
      it.visibility = GONE
      it.clipToOutline = false
      it.outlineProvider = ViewOutlineProvider.BACKGROUND
    }
    leadingFallback.text = ""
    leadingFallback.textSize = sp(13f)
    leadingFallback.typeface = NativeListFonts.bold(context)
    leadingFallback.visibility = GONE
    leadingFallback.background = null
    showsSeparator = false
    mainColumn.removeView(skeletonPrimary)
    mainColumn.removeView(skeletonSecondary)
    setOnClickListener { view -> (view.tag as? NativeListItem)?.let { onRowPress?.invoke(it) } }
  }

  private fun restoreRestingBackground() {
    background = restingRowBackground
    leadingFrame.alpha = 1f
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
      title.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
      title.gravity = Gravity.CENTER
      showText(title, item.json.optString("title"), 1)
      title.setTextColor(
        color(
          theme,
          if (selected) "primaryText" else "secondaryText",
          if (selected) "#FFFFFFED" else "#FFFFFFAF",
        ),
      )
      addView(
        mainColumn,
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
          topMargin = dp(4)
        },
      )
      return
    }
    val leading = item.json.optJSONObject("leading")
    item.json.optJSONObject("leadingAction")?.let { action ->
      leadingActionIcon.visibility = VISIBLE
      leadingActionIcon.iconName = action.optString("name")
      leadingActionIcon.glyphSizeDp = 24
      leadingActionIcon.tintColor = safeColor(
        action.optString("tintColor"),
        color(theme, "icon", "#0000009B"),
      )
      leadingActionIcon.isEnabled = !action.optBoolean("disabled", false)
      leadingActionIcon.alpha = if (leadingActionIcon.isEnabled) 1f else 0.4f
      leadingActionIcon.setOnClickListener {
        onAction?.invoke(item, action.optString("actionKey"), null)
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
      ) 32 else 40,
    )
    addView(mainColumn, weighted())
    titleLine.packsChildrenAtStart = true
    title.ellipsize = TextUtils.TruncateAt.END
    showText(title, item.json.optString("title"), item.json.optInt("titleLines", 1))
    if (item.json.optString("presentation") == "accountSelector") {
      title.typeface = NativeListFonts.regular(context)
    }
    showText(subtitle, item.json.optString("subtitle"), item.json.optInt("subtitleLines", 2))
    showText(tertiary, item.json.optString("tertiary"), 1)
    tertiary.setTextColor(
      color(
        theme,
        if (item.json.optString("tertiaryTone") == "info") "info" else "secondaryText",
        if (item.json.optString("tertiaryTone") == "info") "#006DCBF2" else "#0000009B",
      ),
    )
    val badges = item.json.optJSONArray("badges")
    if (badges != null && badges.length() > 0) {
      val texts = (0 until minOf(2, badges.length())).map { badges.getJSONObject(it).optString("text") }
      showText(badgeLine, texts.joinToString("  "), 1)
      badgeLine.layoutParams = wrap().apply { marginStart = dp(8) }
      badgeLine.setTextColor(color(theme, "secondaryText", "#0000009B"))
      badgeLine.background = roundedFill(color(theme, "strongBackground", "#0000000F"), 4f)
      badgeLine.setPadding(dp(8), dp(2), dp(8), dp(2))
      TextViewCompat.setLineHeight(badgeLine, dp(16))
    }
    addView(trailingColumn, wrap())
    val accessories = item.json.optJSONArray("trailing")
    if (accessories.hasAccessory("checkbox") && accessories.hasAccessory("value")) {
      trailingColumn.orientation = HORIZONTAL
      trailingColumn.gravity = Gravity.END or Gravity.CENTER_VERTICAL
      checkbox.layoutParams = LayoutParams(dp(20), dp(20)).apply { marginStart = dp(12) }
    } else if (accessories.isBookmarkEditActions()) {
      trailingColumn.orientation = HORIZONTAL
      trailingColumn.gravity = Gravity.END or Gravity.CENTER_VERTICAL
    }
    bindAccessories(item, accessories, theme, checkboxState)
  }

  private fun bindRail(item: NativeListItem, theme: JSONObject?) {
    setPadding(dp(4), dp(4), dp(4), dp(4))
    addLeading(item.json.optJSONObject("visual"), 20, spacingDp = 6)
    showText(title, item.json.optString("title"), 1)
    TextViewCompat.setLineHeight(title, dp(16))
    title.fontFeatureSettings = "tnum"
    title.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    titleLine.layoutParams = wrap()
    mainColumn.orientation = HORIZONTAL
    mainColumn.gravity = Gravity.CENTER_VERTICAL
    mainColumn.layoutParams = wrap()
    title.setHorizontallyScrolling(true)
    item.json.optJSONObject("badge")?.let { badge ->
      showText(badgeLine, badge.optString("text"), 1)
      badgeLine.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
        marginStart = dp(6)
      }
      badgeLine.setTextColor(
        when (badge.optString("tone")) {
          "success" -> color(theme, "positive", "#00713FDE")
          "danger" -> color(theme, "negative", "#C40006D3")
          else -> color(theme, "secondaryText", "#0000009B")
        },
      )
      badgeLine.typeface = NativeListFonts.medium(context)
      badgeLine.fontFeatureSettings = "tnum"
      TextViewCompat.setLineHeight(badgeLine, dp(16))
    }
    item.json.optString("status").takeUnless { it.isEmpty() || it == "none" }
      ?.let { showText(status, it, 1) }
    addView(mainColumn, wrap())
  }

  private fun bindActivity(item: NativeListItem, theme: JSONObject?) {
    addLeading(
      item.json.optJSONObject("leading"),
      secondaryVisual = item.json.optJSONObject("secondaryLeading"),
    )
    addView(mainColumn, weighted())
    showText(title, item.json.optString("title"), 1)
    showText(subtitle, item.json.optString("description"), 2)
    TextViewCompat.setLineHeight(title, dp(24))
    TextViewCompat.setLineHeight(subtitle, dp(20))
    if (item.json.optString("status") == "Failed") {
      showText(badgeLine, "Failed", 1)
      badgeLine.setTextColor(color(theme, "negative", "#C40006D3"))
      badgeLine.background = roundedFill(
        color(theme, "criticalBackground", "#F3000D14"),
        4f,
      )
      badgeLine.setPadding(dp(8), dp(2), dp(8), dp(2))
    } else {
      showText(status, item.json.optString("status"), 1)
    }
    addView(trailingColumn, wrap())
    showTrailing(0, item.json.optString("primaryAmount"), true)
    showTrailing(1, item.json.optString("secondaryAmount"), false)
    if (item.json.optString("primaryAmount").startsWith("+")) {
      trailingViews[0].setTextColor(color(theme, "positive", "#00713FDE"))
    }
    val actions = item.json.optJSONArray("footerActions")
    if (actions != null && actions.length() > 0) {
      actionLine.visibility = VISIBLE
      for (index in 0 until minOf(3, actions.length())) {
        val action = actions.getJSONObject(index)
        val actionView = actionViews[index]
        actionView.text = action.optString("label")
        actionView.visibility = VISIBLE
        actionView.isEnabled = isEnabled && !action.optBoolean("disabled", false)
        actionView.background = roundedFill(color(theme, "strongBackground", "#0000000F"), 8f)
        actionView.setTextColor(
          if (action.optString("tone") == "danger") {
            color(theme, "negative", "#C40006D3")
          } else {
            color(theme, "primaryText", "#000000DF")
          },
        )
        actionView.layoutParams = LayoutParams(
          LayoutParams.WRAP_CONTENT,
          LayoutParams.WRAP_CONTENT,
        ).apply { marginEnd = dp(8) }
        actionView.setOnClickListener { onAction?.invoke(item, action.optString("key"), null) }
      }
      // TxActionCommonListView is one column ListItem with an 8dp gap between
      // its content XStack and the pending action footer.
      removeView(leadingFrame)
      removeView(mainColumn)
      removeView(trailingColumn)
      mainColumn.removeView(actionLine)
      activityContentRow.orientation = HORIZONTAL
      activityContentRow.gravity = Gravity.CENTER_VERTICAL
      activityContentRow.addView(leadingFrame)
      activityContentRow.addView(mainColumn)
      activityContentRow.addView(trailingColumn)
      orientation = VERTICAL
      gravity = Gravity.START
      addView(activityContentRow, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
      addView(actionLine, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
        marginStart = if (leadingFrame.visibility == VISIBLE) dp(52) else 0
        topMargin = dp(8)
        bottomMargin = dp(4)
      })
    }
  }

  private fun bindMessage(item: NativeListItem, theme: JSONObject?) {
    gravity = Gravity.TOP
    setPadding(dp(12), dp(16), dp(12), dp(16))
    item.json.optJSONObject("leading")?.let { addLeading(it, 28) }
    unreadDot.visibility = if (item.json.optBoolean("unread", false)) VISIBLE else GONE
    addView(mainColumn, weighted())
    showText(title, item.json.optString("title"), 2)
    showText(subtitle, item.json.optString("body"), item.json.optInt("bodyLines", 3).coerceIn(1, 3))
    showText(status, item.json.optString("time"), 1)
    TextViewCompat.setLineHeight(title, dp(20))
    TextViewCompat.setLineHeight(subtitle, dp(20))
    TextViewCompat.setLineHeight(status, dp(16))
    subtitle.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
      topMargin = dp(2)
    }
    status.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
      topMargin = dp(4)
    }
    status.setTextColor(color(theme, "disabledText", "#00000072"))
    item.json.optJSONObject("thumbnail")?.let { source ->
      secondaryImage.visibility = VISIBLE
      secondaryImage.foreground = roundedHairlineStroke(
        color(theme, "strongBackground", "#0000000F"),
        6f,
      )
      addView(secondaryImage, LayoutParams(dp(64), dp(64)).apply { marginStart = dp(12) })
      bindImage(source, secondaryImage, item.key, 0, "generic")
    }
  }

  private fun bindDataRow(
    item: NativeListItem,
    theme: JSONObject?,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    if (
      item.json.optBoolean("favorite", false) ||
      item.json.optBoolean("favoriteActive", false)
    ) {
      val favoriteActive = item.json.optBoolean("favoriteActive", false)
      favoriteIcon.visibility = VISIBLE
      favoriteIcon.iconName = if (favoriteActive) "StarSolid" else "StarOutline"
      favoriteIcon.tintColor = color(
        theme,
        if (favoriteActive) "icon" else "iconSubdued",
        if (favoriteActive) "#0000009B" else "#00000072",
      )
      addView(favoriteIcon, LayoutParams(dp(20), dp(20)).apply {
        marginEnd = dp(if (currentLayout == "table") 8 else 12)
      })
    }
    item.json.optJSONObject("leading")?.let {
      addLeading(it, 40, spacingDp = if (currentLayout == "table") 10 else 12)
    }
    var hasLeadingAccessory = false
    item.json.optJSONObject("checkbox")?.let {
      bindCheckbox(item, it, checkboxState)
      hasLeadingAccessory = true
    }
    if (item.json.has("index")) {
      val indexView = trailingViews[0]
      indexView.text = item.json.optInt("index").toString()
      indexView.visibility = VISIBLE
      hasLeadingAccessory = true
    }
    if (hasLeadingAccessory) {
      addView(trailingColumn, LayoutParams(dp(32), LayoutParams.WRAP_CONTENT).apply { marginEnd = dp(10) })
    }
    val columns = item.json.getJSONArray("columns")
    val rowBadges = item.json.optJSONArray("badges")
    if (currentLayout == "table") {
      tableDataContainer.visibility = VISIBLE
      for (index in 0 until minOf(4, columns.length())) {
        val column = columns.getJSONObject(index)
        tableDataColumns[index].visibility = VISIBLE
        tableDataColumns[index].bind(
          column = column,
          rowBadges = if (index == 0) rowBadges else null,
          primaryColor = dataTextColor(column.optString("tone"), theme),
          secondaryColor = dataTextColor(
            column.optString("secondaryTone", "secondary"),
            theme,
          ),
          infoColor = color(theme, "info", "#006DCBF2"),
        )
        tableDataColumns[index].layoutParams = LayoutParams(
          0,
          dp(40),
          column.optInt("weight", 1).toFloat(),
        )
      }
      addView(tableDataContainer, weighted())
      return
    }

    dataContainer.visibility = VISIBLE
    for (index in 0 until minOf(4, columns.length())) {
      val column = columns.getJSONObject(index)
      val view = dataColumns[index]
      view.text = styledDataText(column, if (index == 0) rowBadges else null, theme)
      view.maxLines = if (column.optString("secondaryText").isEmpty()) 1 else 2
      view.gravity = when (column.optString("alignment", "start")) {
        "center" -> Gravity.CENTER
        "end" -> Gravity.END
        else -> Gravity.START
      }
      view.visibility = VISIBLE
      view.setTextColor(
        when (column.optString("tone", "primary")) {
          "secondary" -> color(theme, "secondaryText", "#0000009B")
          "positive" -> color(theme, "positive", "#00713FDE")
          "negative" -> color(theme, "negative", "#C40006D3")
          else -> color(theme, "primaryText", "#000000DF")
        },
      )
      view.layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, column.optInt("weight", 1).toFloat())
    }
    addView(dataContainer, weighted())
  }

  private fun styledDataText(
    column: JSONObject,
    badges: JSONArray?,
    theme: JSONObject?,
  ): CharSequence {
    val result = SpannableStringBuilder(column.optString("text"))
    result.setSpan(
      ForegroundColorSpan(dataTextColor(column.optString("tone"), theme)),
      0,
      result.length,
      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
    )
    if (badges != null) {
      for (index in 0 until minOf(2, badges.length())) {
        val start = result.length
        result.append("  ${badges.getJSONObject(index).optString("text")} ")
        result.setSpan(
          ForegroundColorSpan(color(theme, "info", "#006DCBF2")),
          start,
          result.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
        )
        result.setSpan(
          BackgroundColorSpan(parseNativeListColor("#008FF519")),
          start,
          result.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
        )
      }
    }
    column.optString("secondaryText").takeIf(String::isNotEmpty)?.let { secondary ->
      val start = result.length
      result.append("\n$secondary")
      result.setSpan(
        ForegroundColorSpan(dataTextColor(column.optString("secondaryTone", "secondary"), theme)),
        start,
        result.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
      )
    }
    return result
  }

  private fun dataTextColor(tone: String, theme: JSONObject?): Int = when (tone.ifEmpty { "primary" }) {
    "secondary" -> color(theme, "secondaryText", "#0000009B")
    "positive" -> color(theme, "positive", "#00713FDE")
    "negative" -> color(theme, "negative", "#C40006D3")
    else -> color(theme, "primaryText", "#000000DF")
  }

  private fun bindMediaTile(item: NativeListItem, theme: JSONObject?) {
    isMediaTile = true
    orientation = VERTICAL
    gravity = Gravity.START
    setPadding(dp(10), dp(10), dp(10), dp(10))
    val imageState = item.json.optString("imageState", "loaded")
    when (imageState) {
      "empty" -> {
        addLeading(null, 160)
        leadingFrame.background = roundedFill(Color.WHITE, 10f)
      }
      "error" -> {
        addLeading(null, 160)
        leadingFallback.visibility = VISIBLE
        leadingFallback.background = roundedFill(
          color(theme, "strongBackground", "#0000000F"),
          10f,
        )
        leadingIcon.visibility = VISIBLE
        leadingIcon.iconName = "ImageSquareWavesOutline"
        leadingIcon.tintColor = parseNativeListColor("#00000044")
        leadingIcon.layoutParams = FrameLayout.LayoutParams(dp(24), dp(24), Gravity.CENTER)
      }
      else -> {
        addLeading(
          JSONObject().put("kind", "image").put("image", item.json.getJSONObject("image")),
          160,
        )
      }
    }
    leadingFrame.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp(160))
    leadingFallback.layoutParams = FrameLayout.LayoutParams(
      LayoutParams.MATCH_PARENT,
      LayoutParams.MATCH_PARENT,
    )
    leadingImages[0].layoutParams = FrameLayout.LayoutParams(
      LayoutParams.MATCH_PARENT,
      LayoutParams.MATCH_PARENT,
    )
    leadingImages[0].outlineProvider = object : ViewOutlineProvider() {
      override fun getOutline(view: View, outline: Outline) {
        outline.setRoundRect(0, 0, view.width, view.height, dp(10).toFloat())
      }
    }
    leadingImages[0].clipToOutline = true
    mainColumn.removeView(titleLine)
    mainColumn.removeView(subtitle)
    mediaMetadataRow.orientation = HORIZONTAL
    mediaMetadataRow.gravity = Gravity.CENTER_VERTICAL
    mediaMetadataRow.addView(subtitle, weighted().apply { marginEnd = dp(8) })
    item.json.optJSONObject("networkImage")?.let { networkImage ->
      mediaNetworkImage.visibility = VISIBLE
      mediaNetworkImage.outlineProvider = circleOutlineProvider
      mediaNetworkImage.clipToOutline = true
      mediaMetadataRow.addView(mediaNetworkImage, LayoutParams(dp(14), dp(14)))
      bindImage(networkImage, mediaNetworkImage, item.key, 2, "network")
    }
    mainColumn.addView(mediaMetadataRow, 0)
    mainColumn.addView(titleLine, 1)
    showText(subtitle, item.json.optString("subtitle"), 1)
    showText(title, item.json.optString("title"), 1)
    item.json.optJSONObject("badge")?.optString("text")?.let { value ->
      mediaBadge.text = value
      mediaBadge.setTextColor(parseNativeListColor("#FCFCFC"))
      mediaBadge.textSize = sp(14f)
      mediaBadge.background = roundedStroke(
        Color.WHITE,
        parseNativeListColor("#000000DF"),
        10f,
      )
      mediaBadge.setPadding(dp(8), 0, dp(8), 0)
      mediaBadge.layoutParams = FrameLayout.LayoutParams(
        LayoutParams.WRAP_CONTENT,
        dp(24),
        Gravity.END or Gravity.BOTTOM,
      )
      mediaBadge.visibility = VISIBLE
    }
    addView(mainColumn, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
      topMargin = dp(8)
    })
    item.json.optString("closeActionKey").takeIf { it.isNotEmpty() }?.let { actionKey ->
      val close = trailingViews[0]
      close.text = "×"
      close.textSize = sp(22f)
      close.gravity = Gravity.END
      close.visibility = VISIBLE
      close.setOnClickListener { onAction?.invoke(item, actionKey, null) }
      addView(trailingColumn, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    }
  }

  private fun bindMetricCard(item: NativeListItem, theme: JSONObject?) {
    orientation = VERTICAL
    gravity = Gravity.START
    setPadding(dp(14), dp(14), dp(14), dp(14))
    val variant = item.json.optString("variant", "standard")
    if (variant == "activity" || variant == "performance") {
      bindCompositeMetricCard(item, theme, variant)
      return
    }
    item.json.optJSONObject("visual")?.let {
      addLeading(it, 32)
      leadingFrame.clipChildren = true
      leadingFrame.clipToPadding = true
    }
    showText(subtitle, item.json.optString("title"), 1)
    subtitle.setTextColor(color(theme, "disabledText", "#00000072"))
    showText(title, item.json.optString("value"), 1)
    title.textSize = sp(if (item.json.optString("size") == "large") 24f else 18f)
    title.typeface = NativeListFonts.semibold(context)
    showText(status, item.json.optString("trend"), 1)
    status.setTextColor(
      when (item.json.optString("trendTone", "neutral")) {
        "positive" -> color(theme, "positive", "#00713FDE")
        "negative" -> color(theme, "negative", "#C40006D3")
        else -> color(theme, "secondaryText", "#0000009B")
      },
    )
    showText(metricSubtitle, item.json.optString("subtitle"), 1)
    item.json.optJSONObject("badge")?.optString("text")?.let { showText(badgeLine, it, 1) }
    addView(mainColumn, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
  }

  private fun bindCompositeMetricCard(item: NativeListItem, theme: JSONObject?, variant: String) {
    val metrics = item.json.optJSONArray("metrics") ?: JSONArray()
    addView(
      metricText(
        value = item.json.optString("title").uppercase(),
        size = 11f,
        lineHeight = 14,
        typeface = NativeListFonts.regular(context),
        textColor = color(theme, "disabledText", "#00000072"),
        letterSpacingDp = 1.2f,
      ),
      weightedWidth(),
    )
    if (variant == "activity") {
      addView(activityHeroMetrics(metrics, theme), weightedWidth().apply {
        topMargin = dp(14)
      })
      addView(View(context).apply {
        setBackgroundColor(color(theme, "separator", "#0000001F"))
      }, LayoutParams(LayoutParams.MATCH_PARENT, 1).apply {
        topMargin = dp(14)
        bottomMargin = dp(14)
      })
      addView(activityCompactMetrics(metrics, theme), weightedWidth())
    } else {
      val winRateRow = LinearLayout(context).apply {
        orientation = HORIZONTAL
        gravity = Gravity.BOTTOM
        if (metrics.length() > 0) {
          addView(
            makeMetricColumn(
              metric = metrics.getJSONObject(0),
              visualSlot = 0,
              theme = theme,
              alignment = Gravity.START,
              valueSize = 18f,
              valueLineHeight = 24,
              valueTypeface = NativeListFonts.semibold(context),
            ),
            LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f),
          )
        }
        if (metrics.length() > 1) {
          addView(
            makeMetricColumn(
              metric = metrics.getJSONObject(1),
              visualSlot = 1,
              theme = theme,
              alignment = Gravity.END,
              valueSize = 14f,
              valueLineHeight = 20,
              valueTypeface = NativeListFonts.semibold(context),
            ),
            LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f),
          )
        }
      }
      addView(winRateRow, weightedWidth().apply { topMargin = dp(14) })
      val progressValue = item.json.optDouble("progress", 0.0).coerceIn(0.0, 1.0)
      val progress = LinearLayout(context).apply {
        orientation = HORIZONTAL
        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
          override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, dp(2).toFloat())
          }
        }
        if (progressValue > 0.0) {
          addView(View(context).apply {
            setBackgroundColor(parseNativeListColor("#22AB15"))
          }, LayoutParams(0, LayoutParams.MATCH_PARENT, progressValue.toFloat()))
        }
        if (progressValue < 1.0) {
          addView(View(context).apply {
            setBackgroundColor(parseNativeListColor("#E5484D"))
          }, LayoutParams(0, LayoutParams.MATCH_PARENT, (1.0 - progressValue).toFloat()))
        }
      }
      addView(progress, LayoutParams(LayoutParams.MATCH_PARENT, dp(4)).apply {
        topMargin = dp(8)
      })
      addView(performanceAverageMetrics(metrics, theme), weightedWidth().apply {
        topMargin = dp(14)
      })
    }
  }

  private fun activityHeroMetrics(metrics: JSONArray, theme: JSONObject?) =
    LinearLayout(context).apply {
      orientation = HORIZONTAL
      gravity = Gravity.BOTTOM
      if (metrics.length() > 0) {
        addView(
          makeMetricColumn(
            metric = metrics.getJSONObject(0),
            visualSlot = 0,
            theme = theme,
            alignment = Gravity.START,
            valueSize = 16f,
            valueLineHeight = 24,
            valueTypeface = NativeListFonts.semibold(context),
          ),
          LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f),
        )
      }
      if (metrics.length() > 1) {
        addView(
          makeMetricColumn(
            metric = metrics.getJSONObject(1),
            visualSlot = 1,
            theme = theme,
            alignment = Gravity.END,
            valueSize = 14f,
            valueLineHeight = 20,
            valueTypeface = NativeListFonts.semibold(context),
          ),
          LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f),
        )
      }
    }

  private fun activityCompactMetrics(metrics: JSONArray, theme: JSONObject?) =
    LinearLayout(context).apply {
      orientation = HORIZONTAL
      gravity = Gravity.CENTER_VERTICAL
      for (index in 2 until metrics.length()) {
        val alignment = when (index) {
          2 -> Gravity.START
          metrics.length() - 1 -> Gravity.END
          else -> Gravity.CENTER_HORIZONTAL
        }
        addView(
          makeMetricColumn(
            metric = metrics.getJSONObject(index),
            visualSlot = index,
            theme = theme,
            alignment = alignment,
            valueSize = 14f,
            valueLineHeight = 20,
            valueTypeface = NativeListFonts.medium(context),
          ),
          LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f),
        )
      }
    }

  private fun performanceAverageMetrics(metrics: JSONArray, theme: JSONObject?) =
    LinearLayout(context).apply {
      orientation = HORIZONTAL
      val indices = listOf(2, metrics.length() - 1).distinct().filter { it in 0 until metrics.length() }
      indices.forEachIndexed { position, index ->
        val metric = metrics.getJSONObject(index)
        val card = makeMetricColumn(
          metric = metric,
          visualSlot = index,
          theme = theme,
          alignment = Gravity.START,
          valueSize = 14f,
          valueLineHeight = 20,
          valueTypeface = NativeListFonts.medium(context),
        ).apply {
          setPadding(dp(10), dp(10), dp(10), dp(10))
          background = roundedFill(color(theme, "strongBackground", "#0000000F"), 8f)
        }
        addView(card, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply {
          if (position > 0) marginStart = dp(8)
        })
      }
    }

  private fun makeMetricColumn(
    metric: JSONObject,
    visualSlot: Int,
    theme: JSONObject?,
    alignment: Int,
    valueSize: Float,
    valueLineHeight: Int,
    valueTypeface: Typeface,
  ) = LinearLayout(context).apply {
    orientation = VERTICAL
    gravity = alignment
    addView(
      metricText(
        value = metric.optString("label"),
        size = 11f,
        lineHeight = 14,
        typeface = NativeListFonts.regular(context),
        textColor = color(theme, "disabledText", "#00000072"),
        gravity = alignment,
      ),
    )
    val valueText = metricText(
      value = metric.optString("value"),
      size = valueSize,
      lineHeight = valueLineHeight,
      typeface = valueTypeface,
      textColor = dataTextColor(metric.optString("tone"), theme),
      gravity = alignment,
    )
    val valueVisual = metric.optJSONObject("visual")
    val valueView = if (valueVisual != null && visualSlot in metricVisualImages.indices) {
      LinearLayout(context).apply {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        val image = metricVisualImages[visualSlot]
        image.visibility = VISIBLE
        image.outlineProvider = circleOutlineProvider
        image.clipToOutline = true
        valueVisual.optString("backgroundColor").takeIf(String::isNotEmpty)?.let { backgroundColor ->
          image.background = roundedFill(
            safeColor(backgroundColor, Color.WHITE),
            8f,
          )
        }
        visualSources(valueVisual).firstOrNull()?.let { (source, variant) ->
          bindImage(source, image, boundKey ?: "metric", visualSlot, variant)
        }
        addView(image, LayoutParams(dp(16), dp(16)).apply { marginEnd = dp(6) })
        addView(valueText, wrap())
      }
    } else {
      valueText
    }
    addView(
      valueView,
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
        topMargin = dp(2)
      },
    )
  }

  private fun metricText(
    value: String,
    size: Float,
    lineHeight: Int,
    typeface: Typeface,
    textColor: Int,
    letterSpacingDp: Float = 0f,
    gravity: Int = Gravity.START,
  ) = TextView(context).apply {
    includeFontPadding = false
    text = value
    textSize = sp(size)
    this.typeface = typeface
    setTextColor(textColor)
    this.gravity = gravity
    maxLines = 1
    ellipsize = TextUtils.TruncateAt.END
    fontFeatureSettings = "tnum"
    TextViewCompat.setLineHeight(this, dp(lineHeight))
    if (letterSpacingDp > 0f) {
      letterSpacing = letterSpacingDp / sp(size)
    }
  }

  private fun weightedWidth() = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

  private fun bindSectionHeader(
    item: NativeListItem,
    theme: JSONObject?,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    val variant = item.json.optString("variant")
    val isSummary = variant == "summary"
    val isGallery = variant == "gallery"
    val isTable = currentLayout == "table"
    val isHistory = variant == "history" || item.sectionKey?.startsWith("history-") == true
    val isTokenManager = item.sectionKey in setOf("linear-tokens", "action-tokens")
    val hasDottedTitle = isSummary ||
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
      ),
    )
    if (isHistory) {
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
        headerTitleIcon.tintColor = safeColor(
          icon.optString("tintColor"),
          color(theme, "iconSubdued", "#00000072"),
        )
        titleLine.addView(headerTitleIcon, LayoutParams(dp(12), dp(12)).apply {
          marginStart = dp(4)
        })
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
      trailingViews[0].textSize = sp(16f)
      trailingViews[0].typeface = NativeListFonts.medium(context)
      TextViewCompat.setLineHeight(trailingViews[0], dp(24))
      trailingViews[0].setPadding(dp(14), dp(6), dp(14), dp(6))
      trailingViews[0].layoutParams = wrap()
    } else if (!isGallery && !isHistory && !isTokenManager) {
      val value = item.json.optString("value")
      val checkboxData = item.json.optJSONObject("checkbox")
      if (checkboxData != null && value.isNotEmpty()) {
        // Value and checkbox share the trailing edge as one compound accessory.
        trailingColumn.orientation = HORIZONTAL
        trailingColumn.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        trailingViews[0].layoutParams = wrap()
        checkbox.layoutParams = LayoutParams(dp(20), dp(20)).apply { marginStart = dp(12) }
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
          headerValueIcon.tintColor = safeColor(
            icon.optString("tintColor"),
            color(theme, "iconSubdued", "#00000072"),
          )
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
  }

  private fun bindAction(
    item: NativeListItem,
    theme: JSONObject?,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    val isAccountSelector = item.json.optString("presentation") == "accountSelector"
    item.json.optJSONObject("icon")?.let { icon ->
      addLeading(icon, if (isAccountSelector) 32 else 40)
      leadingIcon.layoutParams = FrameLayout.LayoutParams(dp(24), dp(24), Gravity.CENTER)
      if (!icon.has("backgroundColor")) leadingFrame.background = null
    }
    addView(mainColumn, weighted())
    showText(title, item.json.optString("title"), 1)
    if (isAccountSelector) {
      title.typeface = NativeListFonts.regular(context)
      title.setTextColor(color(theme, "secondaryText", "#0000009B"))
    } else if (item.json.optString("tone") == "danger") {
      title.setTextColor(color(theme, "negative", "#C40006D3"))
    }
    item.json.optJSONObject("checkbox")?.let {
      bindCheckbox(item, it, checkboxState)
      addView(trailingColumn, wrap())
    } ?: item.json.optJSONArray("trailing")?.let { accessories ->
      if (accessories.length() > 0) {
        addView(trailingColumn, wrap())
        bindAccessories(item, accessories, theme, checkboxState)
      }
    }
    setOnClickListener { onAction?.invoke(item, item.json.optString("actionKey"), null) }
  }

  private fun bindSystem(item: NativeListItem, theme: JSONObject?) {
    val variant = item.json.optString("variant")
    if (variant == "spacer") {
      minimumHeight = dp(item.json.optInt("height", 0))
      return
    }
    if (variant == "end") {
      // components/ListEndIndicator: py=16, gap=8, 80x1 lines and a 4dp dot.
      gravity = Gravity.CENTER
      setPadding(0, dp(16), 0, dp(16))
      val indicatorColor = color(theme, "separator", "#0000001F")
      addView(View(context).apply { setBackgroundColor(indicatorColor) }, LayoutParams(dp(80), 1))
      addView(
        View(context).apply { background = roundedFill(indicatorColor, 2f) },
        LayoutParams(dp(4), dp(4)).apply {
          marginStart = dp(8)
          marginEnd = dp(8)
        },
      )
      addView(View(context).apply { setBackgroundColor(indicatorColor) }, LayoutParams(dp(80), 1))
      return
    }
    gravity = Gravity.CENTER
    if (variant == "loading") {
      addLeading(JSONObject().put("kind", "skeleton"), 40)
      leadingFallback.text = ""
      leadingFallback.background = roundedFill(
        color(theme, "strongBackground", "#0000000F"),
        20f,
      )
      addSkeleton(skeletonPrimary, 120, 12, theme, bottomMarginDp = 8)
      addSkeleton(skeletonSecondary, 80, 12, theme)
    }
    showText(title, item.json.optString("message"), 2)
    title.gravity = if (variant == "retry" || variant == "noMatch") {
      Gravity.START
    } else {
      Gravity.CENTER
    }
    mainColumn.gravity = Gravity.CENTER_VERTICAL
    if (variant == "noMatch") {
      setPadding(dp(12), 0, dp(12), 0)
      title.setTextColor(color(theme, "secondaryText", "#0000009B"))
    } else if (variant == "retry") {
      setPadding(dp(12), dp(8), dp(12), dp(8))
      title.setTextColor(color(theme, "secondaryText", "#0000009B"))
    }
    addView(
      mainColumn,
      if (variant == "retry" || variant == "noMatch") weighted() else wrap(),
    )
    if (variant == "retry") {
      setOnClickListener { onAction?.invoke(item, item.json.optString("actionKey"), null) }
      showTrailing(0, "Retry", true, item.json.optString("actionKey"))
      trailingViews[0].textSize = sp(14f)
      trailingViews[0].background = roundedFill(color(theme, "strongBackground", "#0000000F"), 16f)
      trailingViews[0].setPadding(dp(10), dp(4), dp(10), dp(4))
      TextViewCompat.setLineHeight(trailingViews[0], dp(20))
      addView(trailingColumn, wrap().apply { marginStart = dp(12) })
    }
  }

  private fun addLeading(
    visual: JSONObject?,
    sizeDp: Int = 40,
    secondaryVisual: JSONObject? = null,
    spacingDp: Int = 12,
  ) {
    leadingFrame.visibility = VISIBLE
    leadingFrame.layoutParams = LayoutParams(dp(sizeDp), dp(sizeDp)).apply { marginEnd = dp(spacingDp) }
    addView(leadingFrame)
    leadingFallback.layoutParams = FrameLayout.LayoutParams(dp(sizeDp), dp(sizeDp))
    if (visual == null) return
    val kind = visual.optString("kind")
    val shape = visual.optString(
      "shape",
      if (kind == "image" || isMediaTile) "rounded" else "circle",
    )
    val sources = visualSources(visual).toMutableList()
    secondaryVisual?.let { sources.addAll(visualSources(it).take(1)) }
    val isIcon = kind == "icon"
    val cornerIconData = visual.optJSONObject("cornerIcon")
    val fallback = visual.optString("fallbackText").take(2)
    leadingFallback.text = fallback
    leadingFallback.setTextColor(parseNativeListColor("#00000072"))
    val visualBackground = safeColor(
      visual.optString("backgroundColor"),
      parseNativeListColor(if (isIcon) "#0000000F" else "#E0E0E0"),
    )
    if (!isIcon && visual.optString("backgroundColor").isNotEmpty()) {
      leadingFrame.background = roundedFill(
        visualBackground,
        leadingCornerRadius(shape, sizeDp),
      )
    }
    leadingFallback.background = roundedFill(visualBackground, leadingCornerRadius(shape, sizeDp))
    leadingFallback.visibility = if (!isIcon && sources.isEmpty()) VISIBLE else GONE
    if (isIcon) {
      leadingFrame.background = GradientDrawable().apply {
        setColor(visualBackground)
        setStroke(1, parseNativeListColor("#0000001F"))
        cornerRadius = NativeListScale.dp(resources, leadingCornerRadius(shape, sizeDp))
      }
      leadingIcon.iconName = visual.optString("name")
      leadingIcon.tintColor = safeColor(
        visual.optString("tintColor"),
        parseNativeListColor("#0000009B"),
      )
      leadingIcon.visibility = VISIBLE
    }
    val visibleSources = sources.take(leadingImages.size)
    val tokenPair = kind == "token" && visibleSources.size > 1
    if (tokenPair) {
      leadingOverlayBackground.visibility = VISIBLE
      leadingOverlayBackground.background = roundedFill(visualBackdropColor, 10f)
      leadingOverlayBackground.layoutParams = FrameLayout.LayoutParams(
        dp(20),
        dp(20),
        Gravity.END or Gravity.BOTTOM,
      ).apply {
        marginEnd = -dp(4)
        bottomMargin = -dp(4)
      }
    }
    if (cornerIconData != null) {
      leadingCornerIconFrame.visibility = VISIBLE
      leadingCornerIconFrame.background = roundedFill(
        safeColor(cornerIconData.optString("backgroundColor"), visualBackdropColor),
        10f,
      )
      leadingCornerIconFrame.layoutParams = FrameLayout.LayoutParams(
        dp(20),
        dp(20),
        Gravity.END or Gravity.BOTTOM,
      ).apply {
        marginEnd = -dp(4)
        bottomMargin = -dp(4)
      }
      leadingCornerIcon.iconName = cornerIconData.optString("name")
      leadingCornerIcon.tintColor = safeColor(
        cornerIconData.optString("tintColor"),
        parseNativeListColor("#0000009B"),
      )
    }
    visibleSources.forEachIndexed { index, (source, variant) ->
      val image = leadingImages[index]
      image.visibility = VISIBLE
      image.layoutParams = leadingImageLayout(
        index = index,
        count = visibleSources.size,
        sizeDp = sizeDp,
        tokenPair = tokenPair,
      )
      image.outlineProvider = when {
        tokenPair && index == 1 -> circleOutlineProvider
        else -> leadingOutlineProvider(shape)
      }
      image.clipToOutline = true
      bindImage(source, image, boundKey ?: "", index, variant)
    }
  }

  private fun visualSources(visual: JSONObject): List<Pair<JSONObject, String>> {
    return when (val kind = visual.optString("kind")) {
      "stackedImages" -> {
        val images = visual.optJSONArray("images") ?: return emptyList()
        (0 until minOf(3, images.length())).mapNotNull { index ->
          images.optJSONObject(index)?.let { it to "generic" }
        }
      }
      "icon" -> emptyList()
      else -> buildList {
        visual.optJSONObject("image")?.let {
          val variant = when (kind) {
            "token" -> "token"
            "network" -> "network"
            "account", "wallet" -> "avatar"
            else -> "generic"
          }
          add(it to variant)
        }
        if (kind == "token") {
          visual.optJSONObject("networkImage")?.let { add(it to "network") }
        }
      }
    }
  }

  private fun leadingImageLayout(
    index: Int,
    count: Int,
    sizeDp: Int,
    tokenPair: Boolean,
  ): FrameLayout.LayoutParams {
    if (count == 1 || tokenPair && index == 0) {
      return FrameLayout.LayoutParams(dp(sizeDp), dp(sizeDp))
    }
    if (tokenPair && index == 1) {
      return FrameLayout.LayoutParams(dp(16), dp(16), Gravity.END or Gravity.BOTTOM).apply {
        marginEnd = -dp(2)
        bottomMargin = -dp(2)
      }
    }
    val imageSize = (sizeDp * 0.72f).toInt()
    return FrameLayout.LayoutParams(dp(imageSize), dp(imageSize), Gravity.START or Gravity.CENTER_VERTICAL).apply {
      marginStart = dp(index * (sizeDp - imageSize) / maxOf(1, count - 1))
    }
  }

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
        "value" -> showTrailing(textIndex++, accessory.optString("text"), !accessory.optBoolean("secondary", false))
        "valuePair" -> {
          showTrailing(
            textIndex++,
            accessory.optString("primary"),
            true,
            textColor = accessoryTextColor(accessory.optString("primaryTone"), "primary", theme),
          )
          if (textIndex < trailingViews.size) {
            showTrailing(
              textIndex++,
              accessory.optString("secondary"),
              false,
              textColor = accessoryTextColor(accessory.optString("secondaryTone"), "secondary", theme),
            )
          }
        }
        "checkbox" -> bindCheckbox(item, accessory, checkboxState)
        "radio" -> showTrailing(
          textIndex++,
          if (accessory.optBoolean("checked")) "●" else "○",
          true,
          accessory.optString("actionKey").takeUnless { accessory.optBoolean("disabled", false) },
        )
        "switch" -> showTrailing(
          textIndex++,
          if (accessory.optBoolean("value")) "ON" else "OFF",
          true,
          accessory.optString("actionKey").takeUnless { accessory.optBoolean("disabled", false) },
        )
        "chevron" -> showTrailingIcon(
          textIndex++,
          item,
          JSONObject(accessory.toString()).apply {
            put("name", "ChevronRightSmallOutline")
            if (optString("actionKey").isEmpty()) put("actionKey", "press")
          },
        )
        "menu" -> showTrailing(textIndex++, "•••", false, accessory.optString("actionKey"))
        "drag" -> showTrailingIcon(
          textIndex++,
          item,
          JSONObject(accessory.toString()).apply { put("name", "DragOutline") },
        )
        "icon" -> showTrailingIcon(textIndex++, item, accessory)
        "spinner" -> spinner.visibility = VISIBLE
        "progress" -> showTrailing(textIndex++, "${(accessory.optDouble("value") * 100).toInt()}%", false)
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
    val target = NativeSelectionTarget(
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
      spinner.alpha = if (!item.json.optBoolean("disabled", false) && accessoryDisabled) 0.5f else 1f
      return
    }
    checkbox.visibility = VISIBLE
    checkbox.setState(state, checkboxUncheckedColor)
    checkbox.background = if (state == "unchecked") {
      roundedStroke(checkboxBorderColor, checkboxUncheckedColor, 4f)
    } else {
      roundedFill(checkboxCheckedColor, 4f)
    }
    // Row-level disabled opacity already applies to this child. Only apply a
    // local 0.5 when the accessory alone is disabled, never 0.5 * 0.5.
    checkbox.alpha = if (!item.json.optBoolean("disabled", false) && accessoryDisabled) 0.5f else 1f
    checkbox.isEnabled =
      !item.json.optBoolean("disabled", false) &&
      !accessoryDisabled &&
      !json.optBoolean("loading", false)
    checkbox.setOnClickListener {
      onAction?.invoke(item, json.optString("actionKey", "selection"), target)
    }
  }

  private fun applySelectionState(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    itemIndex: Int?,
    selected: Boolean,
  ) {
    var rowBackground = color(
      theme,
      if (selected) "rowSelectedBackground" else "rowBackground",
      if (selected) "#0000000F" else "#FFFFFF",
    )
    if (item.type == "rail" || item.type == "mediaTile") {
      // The source FavoriteTokenItem has no active/resting fill, and the NFT
      // tile changes only the image opacity while pressed.
      rowBackground = color(theme, "rowBackground", "#FFFFFF")
    } else if (item.type == "metricCard" && !selected) {
      rowBackground = color(theme, "subduedBackground", "#F9F9F9")
    }
    val groupPosition = when {
      item.type == "identity" && item.json.optString("presentation") == "walletSidebar" -> "single"
      else -> when (item.type) {
      "metricCard" -> "single"
      "rail" -> "rail"
      else -> item.json.optString("groupPosition")
      }
    }
    var backgroundGroupPosition = if (item.type == "mediaTile") "mediaTile" else groupPosition
    if (layout == "sectioned") {
      // Selection in sectioned lists is represented by the OneKey checkbox,
      // matching iOS and the app-monorepo network selector.
      rowBackground = color(theme, "rowBackground", "#FFFFFF")
      if (item.type == "sectionHeader") backgroundGroupPosition = ""
    } else if (
      layout == "table" &&
      item.type == "dataRow" &&
      (if (item.json.has("index")) item.json.optInt("index") else itemIndex)?.rem(2) == 0 &&
      !selected
    ) {
      rowBackground = color(theme, "subduedBackground", "#F9F9F9")
      backgroundGroupPosition = ""
    }
    restingRowBackground = groupedBackground(backgroundGroupPosition, rowBackground)
    background = restingRowBackground
  }

  private fun showText(view: TextView, value: String, lines: Int) {
    if (value.isEmpty()) return
    view.text = value
    view.maxLines = lines
    view.visibility = VISIBLE
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
    view.text = value
    view.textSize = sp(if (primary) 16f else 14f)
    view.typeface = if (primary) NativeListFonts.medium(context) else NativeListFonts.regular(context)
    view.includeFontPadding = false
    view.fontFeatureSettings = "tnum"
    TextViewCompat.setLineHeight(view, dp(if (primary) 24 else 20))
    textColor?.let(view::setTextColor)
    view.visibility = VISIBLE
    if (!actionKey.isNullOrEmpty()) {
      view.setOnClickListener {
        (tag as? NativeListItem)
          ?.takeUnless { item -> item.json.optBoolean("disabled", false) }
          ?.let { item -> onAction?.invoke(item, actionKey, null) }
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

  private fun showTrailingIcon(index: Int, item: NativeListItem, data: JSONObject) {
    if (index !in trailingIcons.indices) return
    val icon = trailingIcons[index]
    icon.iconName = data.optString("name")
    icon.tintColor = safeColor(data.optString("tintColor"), iconSubduedColor)
    if (icon.iconName == "ChevronRightSmallOutline") {
      icon.glyphSizeDp = null
      // ListItem.DrillIn is a 24dp icon with mx=-6, for a 12dp layout footprint.
      icon.layoutParams = LayoutParams(dp(24), dp(24)).apply {
        marginStart = -dp(6)
        marginEnd = -dp(6)
      }
    } else {
      // ListItem.IconButton medium keeps a 24dp glyph in a 36dp frame. Its
      // m=-7 moves the last frame 7dp through the row's trailing padding. For
      // the Bookmark XStack, gap=$6 plus both negative margins leaves 10dp
      // between the physical frames (46dp between glyph centers).
      icon.glyphSizeDp = 24
      setPadding(paddingLeft, paddingTop, dp(5), paddingBottom)
      icon.layoutParams = LayoutParams(dp(36), dp(36)).apply {
        gravity = Gravity.END
        if (trailingColumn.orientation == HORIZONTAL && index > 0) {
          marginStart = dp(10)
        }
      }
    }
    icon.visibility = VISIBLE
    icon.isEnabled = !data.optBoolean("disabled", false)
    icon.alpha = if (icon.isEnabled) 1f else 0.4f
    val actionKey = data.optString("actionKey")
    if (icon.isEnabled && actionKey.isNotEmpty()) {
      icon.setOnClickListener { onAction?.invoke(item, actionKey, null) }
    }
  }

  private fun applySize(item: NativeListItem) {
    val isWalletSidebar =
      item.type == "identity" && item.json.optString("presentation") == "walletSidebar"
    val isAccountSelectorIdentity =
      item.type == "identity" && item.json.optString("presentation") == "accountSelector"
    val isAccountSelectorAction =
      item.type == "action" && item.json.optString("presentation") == "accountSelector"
    title.textSize = sp(
      if (isWalletSidebar) {
        12f
      } else {
        when (item.type) {
          "message" -> 14f
          "sectionHeader" -> when {
            item.json.optString("variant") == "gallery" -> 18f
            item.json.optString("variant") == "summary" -> 16f
            currentLayout == "table" -> 11f
            item.json.optString("variant") == "history" || item.sectionKey?.startsWith("history-") == true -> 12f
            else -> 14f
          }
          "rail" -> 12f
          "system" -> 14f
          "metricCard" -> if (item.json.optString("size") == "large") 24f else 18f
          else -> 16f
        }
      },
    )
    title.typeface = if (isWalletSidebar || isAccountSelectorIdentity || isAccountSelectorAction) {
      NativeListFonts.regular(context)
    } else {
      when (item.type) {
        "sectionHeader" -> when {
          currentLayout == "table" -> NativeListFonts.regular(context)
          item.json.optString("variant") == "summary" -> NativeListFonts.medium(context)
          item.sectionKey in setOf("linear-tokens", "action-tokens") -> NativeListFonts.regular(context)
          else -> NativeListFonts.semibold(context)
        }
        "system" -> NativeListFonts.regular(context)
        "message", "metricCard" -> NativeListFonts.semibold(context)
        else -> NativeListFonts.medium(context)
      }
    }
    subtitle.textSize = sp(when (item.type) {
      "mediaTile" -> 12f
      "metricCard" -> 11f
      else -> 14f
    })
    tertiary.textSize = sp(14f)
    if (item.type == "sectionHeader" && item.json.optString("variant") == "gallery") {
      TextViewCompat.setLineHeight(title, dp(24))
    } else if (item.type == "sectionHeader" && item.json.optString("variant") == "summary") {
      TextViewCompat.setLineHeight(title, dp(24))
    } else if (item.type == "sectionHeader" && currentLayout == "table") {
      TextViewCompat.setLineHeight(title, dp(14))
      TextViewCompat.setLineHeight(trailingViews[0], dp(14))
    } else if (
      item.type == "sectionHeader" &&
      (item.json.optString("variant") == "history" || item.sectionKey?.startsWith("history-") == true)
    ) {
      TextViewCompat.setLineHeight(title, dp(16))
    } else if (item.type == "sectionHeader" && item.sectionKey in setOf("linear-tokens", "action-tokens")) {
      TextViewCompat.setLineHeight(title, dp(20))
    } else if (item.type == "identity" && item.json.optString("presentation") == "walletSidebar") {
      TextViewCompat.setLineHeight(title, dp(16))
    } else if (item.type == "identity") {
      TextViewCompat.setLineHeight(title, dp(24))
      TextViewCompat.setLineHeight(subtitle, dp(20))
      TextViewCompat.setLineHeight(tertiary, dp(20))
    } else if (item.type == "action") {
      TextViewCompat.setLineHeight(title, dp(24))
    } else if (item.type == "system") {
      TextViewCompat.setLineHeight(title, dp(20))
    } else if (item.type == "mediaTile") {
      TextViewCompat.setLineHeight(title, dp(24))
      TextViewCompat.setLineHeight(subtitle, dp(16))
    }
    status.textSize = sp(12f)
    badgeLine.textSize = sp(12f)
    dataColumns.forEach { column ->
      column.textSize = sp(16f)
      column.typeface = NativeListFonts.medium(context)
      column.fontFeatureSettings = "tnum"
    }
    val baseHeight = when {
      item.type == "system" && item.json.optString("variant") == "spacer" -> item.json.optInt("height", 0)
      else -> when (item.type) {
        "rail" -> 28
        "activity" -> if ((item.json.optJSONArray("footerActions")?.length() ?: 0) > 0) 104 else 60
        "message" -> 0
        "mediaTile" -> 0
        "metricCard" -> when (item.json.optString("variant")) {
          "activity" -> 0
          "performance" -> 0
          else -> 132
        }
        "sectionHeader" -> when {
          currentLayout == "table" -> 28
          item.json.optString("variant") == "summary" -> 80
          item.json.optString("variant") == "gallery" -> 32
          item.json.optString("variant") == "history" || item.sectionKey?.startsWith("history-") == true -> 16
          item.sectionKey in setOf("linear-tokens", "action-tokens") -> 30
          item.json.optString("value").isNotEmpty() && item.json.optJSONObject("checkbox") != null -> 40
          else -> 36
        }
        "system" -> when (item.json.optString("variant")) {
          "noMatch", "end" -> 36
          "retry" -> 44
          else -> 56
        }
        "action" -> when {
          item.json.optString("presentation") == "accountSelector" -> 48
          item.json.has("icon") -> 60
          else -> 44
        }
        "dataRow" -> if (currentLayout == "table") {
          60
        } else if ((0 until item.json.getJSONArray("columns").length()).any {
          item.json.getJSONArray("columns").getJSONObject(it).optString("secondaryText").isNotEmpty()
        }) {
          64
        } else {
          56
        }
        else -> when {
          item.type == "identity" && item.json.optString("presentation") == "walletSidebar" -> 68
          item.type == "identity" && item.json.optString("tertiary").isNotEmpty() -> 72
          item.type == "identity" && item.json.optString("subtitle").isNotEmpty() -> 60
          else -> 56
        }
      }
    }
    val modifier = if (
      item.type == "sectionHeader" && item.json.optString("variant") in listOf("summary", "gallery")
    ) {
      0
    } else {
      when (item.json.optString("size", "medium")) { "small" -> -8; "large" -> 12; else -> 0 }
    }
    val sectionSpacing = 0
    val hasSecondaryColumn = item.type == "dataRow" &&
      (0 until item.json.getJSONArray("columns").length()).any {
        item.json.getJSONArray("columns").getJSONObject(it).optString("secondaryText").isNotEmpty()
      }
    val tableAdjustment = if (item.type == "dataRow" && currentLayout == "table" && !hasSecondaryColumn) -8 else 0
    minimumHeight = dp((baseHeight + modifier + sectionSpacing + tableAdjustment).coerceAtLeast(0))
  }

  private fun addSkeleton(
    view: View,
    widthDp: Int,
    heightDp: Int,
    theme: JSONObject?,
    bottomMarginDp: Int = 0,
  ) {
    view.background = roundedFill(color(theme, "strongBackground", "#0000000F"), 6f)
    mainColumn.addView(
      view,
      LayoutParams(dp(widthDp), dp(heightDp)).apply { bottomMargin = dp(bottomMarginDp) },
    )
  }

  private fun applyListOrientation(item: NativeListItem, listOrientation: String) {
    val params = layoutParams ?: return
    if (listOrientation == "horizontal") {
      params.width = when (item.type) {
        "rail" -> railWidth()
        "mediaTile" -> dp(200)
        else -> dp(280)
      }
      params.height = ViewGroup.LayoutParams.WRAP_CONTENT
    } else {
      params.width = ViewGroup.LayoutParams.MATCH_PARENT
      params.height = ViewGroup.LayoutParams.WRAP_CONTENT
    }
    layoutParams = params
  }

  private fun railWidth(): Int {
    var width = dp(4 + 20 + 6).toFloat() + title.paint.measureText(title.text.toString())
    if (badgeLine.visibility == VISIBLE) {
      width += dp(6) + badgeLine.paint.measureText(badgeLine.text.toString())
    }
    if (status.visibility == VISIBLE) {
      width += dp(6) + status.paint.measureText(status.text.toString())
    }
    width += dp(4)
    return width.roundToInt()
  }

  private fun leadingCornerRadius(shape: String, sizeDp: Int): Float = when (shape) {
    "square" -> 0f
    "rounded" -> minOf(10f, sizeDp / 4f)
    else -> sizeDp / 2f
  }

  private fun leadingOutlineProvider(shape: String) = object : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
      when (shape) {
        "square" -> outline.setRect(0, 0, view.width, view.height)
        "rounded" -> outline.setRoundRect(0, 0, view.width, view.height, dp(10).toFloat())
        else -> outline.setOval(0, 0, view.width, view.height)
      }
    }
  }

  private fun JSONArray?.hasAccessory(kind: String): Boolean {
    if (this == null) return false
    return (0 until length()).any { optJSONObject(it)?.optString("kind") == kind }
  }

  private fun JSONArray?.isBookmarkEditActions(): Boolean =
    this != null && length() == 2 &&
      optJSONObject(0)?.optString("kind") == "icon" &&
      optJSONObject(0)?.optString("name") == "PencilOutline" &&
      optJSONObject(1)?.optString("kind") == "icon" &&
      optJSONObject(1)?.optString("name") == "DragOutline"

  private fun bindImage(
    source: JSONObject,
    imageView: OneKeyImageReusableView,
    token: String,
    slot: Int,
    variant: String,
  ) {
    val uri = source.optString("uri").trim().takeIf(String::isNotEmpty)
    imageView.configure(
      sourceUri = uri,
      sourceHeadersJson = source.optJSONObject("headers")?.toString(),
      variant = variant,
      contentFit = source.optString("contentFit", "cover"),
      cachePolicy = source.optString("cachePolicy", "memory-disk"),
      autoplay = source.optBoolean("autoplay", false),
      recyclingKey = "$token:$slot",
      optimizeTos = source.optBoolean("optimizeTos", true),
      overscan = source.optDouble("overscan", 1.1),
      loadingStrategy = source.optString("loadingStrategy", "static"),
    )
  }

  private fun weighted() = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
  private fun wrap() = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
  private fun dp(value: Int): Int = NativeListScale.dp(resources, value)
  private fun sp(value: Float): Float = NativeListScale.font(resources, value)

  private fun color(theme: JSONObject?, key: String, fallback: String): Int =
    safeColor(theme?.optString(key, fallback), parseNativeListColor(fallback))

  private fun safeColor(value: String?, fallback: Int): Int = try {
    if (value.isNullOrEmpty()) fallback else parseNativeListColor(value)
  } catch (_: IllegalArgumentException) {
    fallback
  }

  private fun roundedFill(color: Int, radiusDp: Float) = GradientDrawable().apply {
    setColor(color)
    cornerRadius = NativeListScale.dp(resources, radiusDp)
  }

  private fun roundedStroke(stroke: Int, fill: Int, radiusDp: Float) = GradientDrawable().apply {
    setColor(fill)
    setStroke(dp(2), stroke)
    cornerRadius = NativeListScale.dp(resources, radiusDp)
  }

  private fun roundedHairlineStroke(stroke: Int, radiusDp: Float) = GradientDrawable().apply {
    setColor(Color.TRANSPARENT)
    setStroke(1, stroke)
    cornerRadius = NativeListScale.dp(resources, radiusDp)
  }

  private fun groupedBackground(position: String, color: Int) = GradientDrawable().apply {
    setColor(color)
    val radius = NativeListScale.dp(resources, 12f)
    cornerRadii = when (position) {
      "first" -> floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f)
      "last" -> floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius)
      "single" -> FloatArray(8) { radius }
      "rail" -> FloatArray(8) { NativeListScale.dp(resources, 8f) }
      "mediaTile" -> FloatArray(8) { NativeListScale.dp(resources, 16f) }
      else -> FloatArray(8)
    }
  }

}

private class OneKeyIconView(context: android.content.Context) : View(context) {
  var iconName: String = ""
    set(value) {
      field = value
      invalidate()
    }
  var tintColor: Int = Color.rgb(100, 100, 100)
    set(value) {
      field = value
      invalidate()
    }
  var glyphSizeDp: Int? = null
    set(value) {
      field = value
      invalidate()
    }

  private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val pathData = iconPaths[iconName] ?: return
    val drawSize = minOf(
      minOf(width, height).toFloat(),
      glyphSizeDp?.let { NativeListScale.dp(resources, it).toFloat() } ?: Float.MAX_VALUE,
    )
    val scale = drawSize / 24f
    fill.color = tintColor
    canvas.save()
    canvas.translate((width - drawSize) / 2f, (height - drawSize) / 2f)
    canvas.scale(scale, scale)
    val sourceFillTypes = actionIconFillTypes[iconName]
    pathData.forEachIndexed { index, data ->
      PathParser.createPathFromPathData(data)?.let { path ->
        path.fillType = sourceFillTypes?.getOrNull(index) ?: Path.FillType.EVEN_ODD
        canvas.drawPath(path, fill)
      }
    }
    canvas.restore()
  }

  companion object {
    // Keep the SVG fill-rule used by each Action-row source path. React Native
    // SVG defaults to nonzero (WINDING); only paths declaring fillRule="evenodd"
    // use EVEN_ODD. Other existing icons retain their prior rendering behavior.
    private val actionIconFillTypes = mapOf(
      "ChevronRightSmallOutline" to listOf(Path.FillType.WINDING),
      "MinusCircleOutline" to listOf(Path.FillType.WINDING, Path.FillType.EVEN_ODD),
      "PlusCircleOutline" to listOf(Path.FillType.WINDING, Path.FillType.EVEN_ODD),
      "PlusSmallOutline" to listOf(Path.FillType.WINDING),
      "DotHorOutline" to listOf(Path.FillType.WINDING),
      "MinusCircleSolid" to listOf(Path.FillType.EVEN_ODD),
      "PencilOutline" to listOf(Path.FillType.EVEN_ODD),
      "DragOutline" to listOf(Path.FillType.WINDING),
      "StarSolid" to listOf(Path.FillType.WINDING),
      "ChevronGrabberVerOutline" to listOf(Path.FillType.WINDING),
      "ChevronBottomOutline" to listOf(Path.FillType.WINDING),
      "ChevronTopOutline" to listOf(Path.FillType.WINDING),
      "ImageSquareWavesOutline" to listOf(Path.FillType.WINDING, Path.FillType.EVEN_ODD),
    )

    // Exact 24x24 paths from app-monorepo packages/components Icon sources.
    private val iconPaths = mapOf(
      "ArrowBottomOutline" to listOf("m13 17.586 5-5L19.414 14 12 21.414 4.586 14 6 12.586l5 5V3h2z"),
      "ArrowTopOutline" to listOf("M19.414 10 18 11.414l-5-5V21h-2V6.414l-5 5L4.586 10 12 2.586z"),
      "ChartTrendingUpOutline" to listOf("M22 13h-2V9.414l-7 7-4-4-6 6L1.586 17 9 9.586l4 4L18.586 8H15V6h7z"),
      "SwapHorOutline" to listOf("M21 16H6.914l2.293 2.293-1.414 1.414L2.086 14H21zm.914-6H3V8h14.086l-2.293-2.293 1.414-1.414z"),
      "ShieldExclamationOutline" to listOf(
        "M12 12.25a1.25 1.25 0 1 1 0 2.5 1.25 1.25 0 0 1 0-2.5m0-4.75a1 1 0 0 1 1 1v2a1 1 0 0 1-2 0v-2a1 1 0 0 1 1-1",
        "M11.352 2.223c.42-.145.876-.145 1.296 0l6.98 2.4a1.995 1.995 0 0 1 1.347 1.886v5.432c0 2.799-1.146 4.817-2.805 6.387-1.61 1.525-3.735 2.652-5.696 3.71a1 1 0 0 1-.947 0c-1.961-1.058-4.086-2.185-5.697-3.71-1.659-1.57-2.805-3.588-2.805-6.386V6.508c0-.852.542-1.61 1.347-1.887l6.98-2.4ZM5.02 6.509v5.432c0 2.16.848 3.676 2.18 4.938 1.272 1.204 2.957 2.149 4.799 3.145 1.842-.996 3.527-1.94 4.799-3.145 1.332-1.262 2.181-2.778 2.181-4.938V6.51L12 4.109z",
      ),
      "InfoCircleOutline" to listOf(
        "M13 17h-2v-5h-1v-2h3zm0-8h-2V7h2z",
        "M12 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2m0 2a8 8 0 1 0 0 16 8 8 0 0 0 0-16",
      ),
      "SpeakerPromoteOutline" to listOf("M21.996 11a4 4 0 0 1-3 3.874v5.495l-5.361-1.711A3.998 3.998 0 0 1 5.996 17v-.779l-4-1.276v-7.89l17-5.424v5.495c1.725.444 3 2.01 3 3.874m-2 0c0-.74-.402-1.385-1-1.731v3.46c.597-.345 1-.989 1-1.729M7.998 7.24v7.521l8.998 2.871V4.368l-8.998 2.87ZM3.996 8.516v4.967l2.002.638V7.877zm4 8.484a2 2 0 0 0 3.706 1.041L7.996 16.86z"),
      "PlusCircleOutline" to listOf(
        "M13 11h4v2h-4v4h-2v-4l-4 .001v-2L11 11V7h2z",
        "M12 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2m0 2a8 8 0 1 0 0 16 8 8 0 0 0 0-16",
      ),
      "PlusSmallOutline" to listOf("M13 11h5v2h-5v5h-2v-5H6v-2h5V6h2z"),
      "DotHorOutline" to listOf("M6 14H2v-4h4zm8 0h-4v-4h4zm8 0h-4v-4h4z"),
      "MinusCircleOutline" to listOf(
        "M17 13H7v-2h10z",
        "M12 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2m0 2a8 8 0 1 0 0 16 8 8 0 0 0 0-16",
      ),
      "MinusCircleSolid" to listOf("M12 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2M7 13h10v-2H7z"),
      "ErrorSolid" to listOf("M23.256 20H.742L12 1.041zM11 15v2h2v-2zm0-6v5h2V9z"),
      "QuestionmarkSolid" to listOf("M12 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2m-1 15h2v-2h-2zM9 7v3h2V9h2v1l-2 1.5V14h2v-1.5l2-1.5V7z"),
      "PencilOutline" to listOf("M22.414 7.5 7.914 22H2v-5.914l14.5-14.5zM4 16.914V20h3.086l9.5-9.5L13.5 7.414zM14.914 6 18 9.086 19.586 7.5 16.5 4.414z"),
      "ChevronRightSmallOutline" to listOf("M15.414 12 10 17.414 8.586 16l4-4-4-4L10 6.586z"),
      "DragOutline" to listOf("M11 21H7v-4h4zm6 0h-4v-4h4zm-6-7H7v-4h4zm6 0h-4v-4h4zm-6-7H7V3h4zm6 0h-4V3h4z"),
      "StarOutline" to listOf("m15.455 7.243 7.729 1.123-5.592 5.45 1.32 7.698L12 17.879l-6.911 3.635 1.32-7.698-5.592-5.45 7.728-1.123L12 .24zM9.872 9.071l-4.759.69 3.444 3.358-.814 4.738L12 15.62l.465.245 3.791 1.993-.813-4.739 3.443-3.357-4.758-.69L12 4.758z"),
      "StarSolid" to listOf("m15.405 7.313 7.84 1.034-5.735 5.443 1.44 7.774L12 17.793l-6.948 3.771 1.44-7.774L.756 8.347l7.839-1.034L12 .178z"),
      "ChevronGrabberVerOutline" to listOf("M17.414 15 12 20.414 6.586 15 8 13.586l4 4 4-4zm0-6L16 10.414l-4-4-4 4L6.586 9 12 3.586z"),
      "ChevronBottomOutline" to listOf("M20.707 9.707 12 18.414 3.293 9.707l1.414-1.414L12 15.586l7.293-7.293z"),
      "ChevronTopOutline" to listOf("m20.707 14.293-1.414 1.414L12 8.414l-7.293 7.293-1.414-1.414L12 5.586z"),
      "ImageSquareWavesOutline" to listOf(
        "M14.25 7a2 2 0 1 1 0 4 2 2 0 0 1 0-4",
        "M21 21H3V3h18zM5 16.414V19h12.586L14 15.414l-2 2-4-4zm0-2.828 3-3 4 4 2-2 5 5V5H5z",
      ),
    )
  }
}

private class OneKeyCheckboxView(context: android.content.Context) : View(context) {
  private val glyphPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
  private var state = "unchecked"

  fun setState(value: String, color: Int) {
    state = value
    glyphPaint.color = color
    invalidate()
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val pathData = when (state) {
      "checked" -> "M12.204 5.043a1 1 0 0 1 0 1.414l-4.5 4.5a1 1 0 0 1-1.414 0l-2-2a1 1 0 1 1 1.414-1.414l1.293 1.293 3.793-3.793a1 1 0 0 1 1.414 0"
      "indeterminate" -> "M4 8a1 1 0 0 1 1-1h6a1 1 0 0 1 0 2H5a1 1 0 0 1-1-1"
      else -> return
    }
    val drawSize = minOf(width, height) * 0.8f
    canvas.save()
    canvas.translate((width - drawSize) / 2f, (height - drawSize) / 2f)
    canvas.scale(drawSize / 16f, drawSize / 16f)
    PathParser.createPathFromPathData(pathData)?.let { canvas.drawPath(it, glyphPaint) }
    canvas.restore()
  }
}

internal class NativeListViewHolder(val rowView: NativeListRowView) :
  androidx.recyclerview.widget.RecyclerView.ViewHolder(rowView)
