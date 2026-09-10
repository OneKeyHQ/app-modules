package com.margelo.nitro.nativelist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.facebook.react.uimanager.ThemedReactContext
// OneKey patch: selector checkboxes share the source React Native border/background renderer.
import com.facebook.react.uimanager.BackgroundStyleApplicator
import com.facebook.react.uimanager.LengthPercentage
import com.facebook.react.uimanager.LengthPercentageType
import com.facebook.react.uimanager.style.BorderRadiusProp
import com.facebook.react.uimanager.style.LogicalEdge
import com.margelo.nitro.onekeyimage.OneKeyImageReusableView
import androidx.core.graphics.PathParser
import androidx.core.widget.TextViewCompat
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

// Market/TokenListSkeleton: source geometry and the native Skeleton's 3s shimmer.
private class NativeListMarketSkeleton(context: android.content.Context, backgroundColor: Int) : View(context) {
  private val marks = Array(5) { RectF() }
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val matrix = Matrix()
  private val shaders = arrayOfNulls<LinearGradient>(5)
  private val dark = Color.red(backgroundColor) * 0.299 + Color.green(backgroundColor) * 0.587 + Color.blue(backgroundColor) * 0.114 < 128
  private val colors = intArrayOf(
    Color.parseColor(if (dark) "#111111" else "#FAFAFA"),
    Color.parseColor(if (dark) "#333333" else "#CDCDCD"),
    Color.parseColor(if (dark) "#111111" else "#FAFAFA"),
  )
  private var phase = 0f
  private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
    duration = 3000
    repeatCount = ValueAnimator.INFINITE
    interpolator = LinearInterpolator()
    addUpdateListener { phase = it.animatedValue as Float; invalidate() }
  }

  private fun dp(value: Float) = NativeListScale.dp(resources, value)

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    marks[0].set(0f, 0f, dp(32f), dp(32f))
    marks[1].set(dp(44f), 0f, dp(124f), dp(16f))
    marks[2].set(dp(44f), dp(20f), dp(104f), dp(32f))
    marks[3].set(w - dp(168f), dp(7f), w - dp(88f), dp(25f))
    marks[4].set(w - dp(80f), dp(7f), w.toFloat(), dp(25f))
    marks.forEachIndexed { index, mark ->
      shaders[index] = LinearGradient(0f, 0f, mark.width(), 0f, colors, floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
    }
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    marks.forEachIndexed { index, mark ->
      matrix.setTranslate(mark.left - mark.width() + phase * mark.width() * 3f, 0f)
      shaders[index]?.setLocalMatrix(matrix)
      paint.shader = shaders[index]
      val radius = dp(if (index == 0) 16f else 8f)
      canvas.drawRoundRect(mark, radius, radius, paint)
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    if (windowVisibility == VISIBLE) animator.start()
  }

  override fun onDetachedFromWindow() {
    animator.cancel()
    super.onDetachedFromWindow()
  }

  override fun onWindowVisibilityChanged(visibility: Int) {
    super.onWindowVisibilityChanged(visibility)
    if (visibility == VISIBLE && isAttachedToWindow) {
      if (!animator.isStarted) animator.start()
    } else animator.cancel()
  }
}

internal data class NativeListActionOrigin(
  val sourceView: View,
  val ownerRowView: NativeListRowView,
  val bindingEpoch: Long,
  val source: String,
  val slot: Int? = null,
  val anchorInsetPixels: Int = 0,
)

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

// OneKey patch: match React Native's CustomLineHeightSpan for first/last line bounds.
private class SelectorLineHeightSpan(private val lineHeight: Int) : android.text.style.LineHeightSpan {
  override fun chooseHeight(text: CharSequence, start: Int, end: Int, spanstartv: Int, v: Int, fm: Paint.FontMetricsInt) {
    val leading = lineHeight - (fm.descent - fm.ascent)
    fm.ascent -= kotlin.math.ceil(leading / 2.0).toInt()
    fm.descent += kotlin.math.floor(leading / 2.0).toInt()
    if (start == 0) fm.top = fm.ascent
    if (end == text.length) fm.bottom = fm.descent
  }
}

private class DottedUnderlineTextView(context: android.content.Context) : TextView(context) {
  var useSourceScale = false
  private fun scaledDp(value: Float) = if (useSourceScale) value * resources.displayMetrics.density else NativeListScale.dp(resources, value)
  var showsDottedUnderline = false
  var dottedUnderlineColor = Color.TRANSPARENT
  private val dottedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (!showsDottedUnderline || text.isEmpty()) return
    dottedPaint.color = dottedUnderlineColor
    val radius = scaledDp(0.75f)
    val spacing = scaledDp(4f)
    val lineWidth = paint.measureText(text.toString()).coerceAtMost(width.toFloat())
    val y = height - radius
    var x = scaledDp(1f)
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
    val widthMode = MeasureSpec.getMode(widthMeasureSpec)
    val widthSize = MeasureSpec.getSize(widthMeasureSpec)
    var accessoryWidth = 0
    for (index in 1 until childCount) {
      val child = getChildAt(index)
      if (child.visibility == GONE) continue
      measureChildWithMargins(
        child,
        widthMeasureSpec,
        paddingLeft + paddingRight + accessoryWidth,
        heightMeasureSpec,
        paddingTop + paddingBottom,
      )
      val margins = child.layoutParams as MarginLayoutParams
      accessoryWidth += child.measuredWidth + margins.leftMargin + margins.rightMargin
    }
    val titleMargins = title.layoutParams as MarginLayoutParams
    if (widthMode != MeasureSpec.UNSPECIFIED) {
      (title as TextView).maxWidth = (widthSize - paddingLeft - paddingRight - accessoryWidth -
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

// OneKey patch: fit subtitle segments at intrinsic width, shrinking text only when necessary.
private class SelectorSubtitleLayout(context: android.content.Context) : LinearLayout(context) {
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val available = MeasureSpec.getSize(widthMeasureSpec)
    val labels = mutableListOf<Pair<TextView, Int>>()
    var fixedWidth = 0
    for (index in 0 until childCount) {
      val child = getChildAt(index)
      val params = child.layoutParams as LayoutParams
      if (child is TextView) {
        child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), heightMeasureSpec)
        labels.add(child to child.measuredWidth)
      } else fixedWidth += params.width + params.leftMargin + params.rightMargin
    }
    val desired = labels.sumOf { it.second }
    val textWidth = (available - fixedWidth).coerceAtLeast(0)
    labels.forEach { (label, width) ->
      (label.layoutParams as LayoutParams).width = if (desired <= textWidth) width else (width.toLong() * textWidth / desired.coerceAtLeast(1)).toInt()
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
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
  // OneKey patch: reset selector fragments and corner decorations on every bind.
  private val selectorViews = mutableListOf<View>()
  private var selectorUsesSourceScale = false
  private val selectorAccessibilityDelegate = object : View.AccessibilityDelegate() {
    override fun onInitializeAccessibilityNodeInfo(host: View, info: android.view.accessibility.AccessibilityNodeInfo) {
      super.onInitializeAccessibilityNodeInfo(host, info)
      info.viewIdResourceName = host.getTag(com.facebook.react.R.id.react_test_id) as? String
    }
  }
  private val selectorOriginalFontFeatures = mutableMapOf<TextView, String?>()
  private val selectorOriginalPaintFlags = mutableMapOf<TextView, Int>()
  private val selectorLineHeights = mutableMapOf<TextView, Int>()
  private val selectorFontSizes = mutableMapOf<TextView, Float>()
  private val selectorImages = mutableListOf<OneKeyImageReusableView>()
  private var selectorHeight: Int? = null
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
  private val marketBadgeViews = List(3) { LinearLayout(context) }
  private val marketBadgeLabels = List(3) { TextView(context) }
  private val marketBadgeImages = List(3) { OneKeyImageReusableView(reactContext) }
  private val marketBadgeGlyphs = List(3) { OneKeyIconView(context) }
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
  private val walletGroupRows = mutableListOf<NativeListRowView>()
  private val walletGroupDragBadgeBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val walletGroupDragBadgeBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.STROKE
  }
  private val walletGroupDragBadgeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    textAlign = Paint.Align.CENTER
    typeface = NativeListFonts.semibold(context)
  }
  private var walletGroupDragChildCount = 0
  private var walletGroupExpandedHeightPx = 0
  private var walletGroupExpandAnimator: ValueAnimator? = null
  private var isMediaTile = false
  private var boundKey: String? = null
  // OneKey patch: delayed retries cannot survive cell rebinding or recycling.
  private val selectorImageRetries = mutableMapOf<OneKeyImageReusableView, Runnable>()
  var bindingEpoch: Long = 0
    private set
  private var boundCheckboxData: JSONObject? = null
  private var currentLayout = "linear"
  private var restingRowBackground: Drawable? = null
  private var pressedRowBackground: Drawable? = null
  // OneKey patch: preserve a held row independently from RecyclerView snapshot rebinding.
  private var touchPressed = false
  private val marketLongPressHandler = Handler(Looper.getMainLooper())
  private var marketLongPressRunnable: Runnable? = null
  private var marketTouchStartX = 0f
  private var marketTouchStartY = 0f
  private var marketLongPressFired = false
  private var reorderActive = false
  private var checkboxCheckedColor = Color.rgb(32, 32, 32)
  private var checkboxUncheckedColor = Color.rgb(252, 252, 252)
  private var checkboxBorderColor = Color.rgb(206, 206, 206)
  private var checkboxIconColor = Color.rgb(252, 252, 252)
  private var checkboxUsesSelectorStyle = false
  private var iconSubduedColor = Color.rgb(141, 141, 141)
  private var visualBackdropColor = Color.WHITE
  private val circleOutlineProvider = object : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
      outline.setOval(0, 0, view.width, view.height)
    }
  }
  private val separatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private var showsSeparator = false

  var onRowPress: ((NativeListItem, NativeListActionOrigin) -> Unit)? = null
  var onAction: ((NativeListItem, String, NativeSelectionTarget?, NativeListActionOrigin?) -> Unit)? = null
  var onBindingInvalidated: ((NativeListRowView, Long) -> Unit)? = null

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
    marketBadgeViews.forEachIndexed { index, badge ->
      badge.orientation = HORIZONTAL
      badge.gravity = Gravity.CENTER_VERTICAL
      badge.isClickable = true
      marketBadgeGlyphs[index].visibility = GONE
      marketBadgeImages[index].visibility = GONE
      marketBadgeLabels[index].apply {
        includeFontPadding = false
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
        textSize = sp(11f)
        typeface = NativeListFonts.medium(context)
      }
      badge.addView(marketBadgeGlyphs[index], LayoutParams(dp(16), dp(16)))
      badge.addView(marketBadgeImages[index], LayoutParams(dp(14), dp(14)))
      badge.addView(marketBadgeLabels[index], wrap())
    }
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
          touchPressed = true
          marketLongPressFired = false
          val item = tag as? NativeListItem
          if (item?.type == "market") {
            marketTouchStartX = event.x
            marketTouchStartY = event.y
            item.json.optString("pressInActionKey").takeIf(String::isNotEmpty)?.let { actionKey ->
              emitAction(item, actionKey, null, this, "row")
            }
            item.json.optString("longPressActionKey").takeIf(String::isNotEmpty)?.let { actionKey ->
              val expectedKey = item.key
              val runnable = Runnable {
                val current = tag as? NativeListItem
                if (touchPressed && current?.key == expectedKey && current.type == "market") {
                  marketLongPressRunnable = null
                  marketLongPressFired = true
                  emitAction(current, actionKey, null, this, "row")
                }
              }
              marketLongPressRunnable = runnable
              marketLongPressHandler.postDelayed(runnable, 800L)
            }
          }
          if ((tag as? NativeListItem)?.type == "mediaTile") {
            leadingFrame.alpha = 0.8f
          } else {
            background = pressedRowBackground
          }
        }
        MotionEvent.ACTION_MOVE -> {
          val outside = event.x < 0 || event.y < 0 || event.x >= width || event.y >= height
          val movedMarket = (tag as? NativeListItem)?.type == "market" &&
            (kotlin.math.abs(event.x - marketTouchStartX) > dp(10) ||
              kotlin.math.abs(event.y - marketTouchStartY) > dp(10))
          if (outside || movedMarket) {
            cancelMarketLongPress()
            touchPressed = false
            restoreRestingBackground()
          }
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
          cancelMarketLongPress()
          touchPressed = false
          restoreRestingBackground()
        }
      }
      false
    }
    setOnClickListener { view ->
      (view.tag as? NativeListItem)?.let { item ->
        if (item.type == "market" && marketLongPressFired) {
          marketLongPressFired = false
          return@let
        }
        // OneKey patch: allow create-address accessories when whole-row press is gated.
        if (!item.json.optBoolean("pressDisabled", false)) onRowPress?.invoke(item, actionOrigin(view, "row"))
      }
    }
    setWillNotDraw(false)
  }

  override fun dispatchDraw(canvas: Canvas) {
    super.dispatchDraw(canvas)
    if (showsSeparator) {
      val start = if ((tag as? NativeListItem)?.type == "identity") dp(60).toFloat() else dp(12).toFloat()
      canvas.drawLine(start, height - 1f, width.toFloat(), height - 1f, separatorPaint)
    }
    if (reorderActive && (tag as? NativeListItem)?.type == "walletGroup" && walletGroupDragChildCount > 0) {
      val label = "+$walletGroupDragChildCount"
      val badgeHeight = dp(24).toFloat()
      val badgeWidth = maxOf(
        dp(24).toFloat(),
        walletGroupDragBadgeTextPaint.measureText(label) + dp(12),
      )
      val right = width - dp(4).toFloat()
      val bottom = height - dp(4).toFloat()
      val left = right - badgeWidth
      val top = bottom - badgeHeight
      canvas.drawRoundRect(
        left,
        top,
        right,
        bottom,
        badgeHeight / 2f,
        badgeHeight / 2f,
        walletGroupDragBadgeBackgroundPaint,
      )
      canvas.drawRoundRect(
        left,
        top,
        right,
        bottom,
        dp(12).toFloat(),
        dp(12).toFloat(),
        walletGroupDragBadgeBorderPaint,
      )
      val metrics = walletGroupDragBadgeTextPaint.fontMetrics
      val baseline = (top + bottom - metrics.ascent - metrics.descent) / 2f
      canvas.drawText(label, (left + right) / 2f, baseline, walletGroupDragBadgeTextPaint)
    }
  }

  // OneKey patch: RecyclerView may replace item delegates during a selection update.
  override fun onInitializeAccessibilityNodeInfo(info: android.view.accessibility.AccessibilityNodeInfo) {
    super.onInitializeAccessibilityNodeInfo(info)
    info.viewIdResourceName = getTag(com.facebook.react.R.id.react_test_id) as? String
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    if (isMediaTile) {
      val availableWidth = (MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight)
        .coerceAtLeast(0)
      leadingFrame.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, availableWidth)
    }
    // OneKey patch: RecyclerView must use the adapter's measured selector height.
    super.onMeasure(widthMeasureSpec, selectorHeight?.let { MeasureSpec.makeMeasureSpec(it, MeasureSpec.EXACTLY) } ?: heightMeasureSpec)
    val item = tag as? NativeListItem ?: return
    if (item.json.has("height") && item.json.optString("presentation") == "networkSelector" && item.type in setOf("identity", "sectionHeader") && checkbox.visibility == VISIBLE && trailingColumn.parent === this) {
      // OneKey patch: Yoga snaps the compound accessory before its children; their rounded edges can overflow it.
      val density = resources.displayMetrics.density
      val visibleValues = trailingViews.filter { it.visibility == VISIBLE }
      val sourceTrailingWidth = visibleValues.sumOf { it.measuredWidth } + (20 + 12 * visibleValues.size) * density
      val sourceTrailingLeft = (measuredWidth - 12 * density - sourceTrailingWidth).roundToInt()
      val measuredTrailingLeft = measuredWidth - paddingRight - trailingColumn.measuredWidth
      val mainWidth = (mainColumn.measuredWidth + sourceTrailingLeft - measuredTrailingLeft).coerceAtLeast(0)
      mainColumn.measure(MeasureSpec.makeMeasureSpec(mainWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mainColumn.measuredHeight, MeasureSpec.EXACTLY))
    }
  }

  // OneKey patch: Yoga rounds a half-pixel text center upward; LinearLayout truncates it.
  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    val item = tag as? NativeListItem ?: return
    val accessory = item.json.optJSONArray("trailing")?.optJSONObject(0)
    if (item.type == "identity" && item.json.has("height") && item.json.optString("presentation") == "accountSelector" && accessory?.optString("kind") == "icon" && accessory.optString("name") == "PlusSmallOutline") {
      // OneKey patch: the borderless Plus retains the source's fixed top18/negative7 slot.
      val icon = trailingIcons[0]
      trailingColumn.offsetTopAndBottom(dp(18) - dp(7) - trailingColumn.top - icon.top)
    }
    if (item.type == "action" && item.json.has("height") && item.json.optString("presentation") == "accountSelector" && leadingIcon.visibility == VISIBLE) {
      // OneKey patch: Yoga rounds the 4dp padding inside the 32dp Add account icon upward.
      leadingIcon.offsetLeftAndRight((leadingFrame.width - leadingIcon.width + 1) / 2 - leadingIcon.left)
      leadingIcon.offsetTopAndBottom((leadingFrame.height - leadingIcon.height + 1) / 2 - leadingIcon.top)
    }
    if (!item.json.has("height")) return
    val isNetworkIdentity = item.type == "identity" && item.json.optString("presentation") == "networkSelector"
    val isAccountAction = item.type == "action" && item.json.optString("presentation") == "accountSelector"
    val isNetworkSummary = item.type == "sectionHeader" && item.json.optString("presentation") == "networkSelector" && item.json.optString("variant") == "summary"
    val centeredColumns = when {
      isNetworkIdentity || isAccountAction -> listOf(mainColumn, trailingColumn)
      isNetworkSummary -> listOf(trailingColumn)
      else -> return
    }
    for (column in centeredColumns) {
      if (column.parent !== this || column.visibility == GONE) continue
      val margins = column.layoutParams as MarginLayoutParams
      val available = height - paddingTop - paddingBottom - margins.topMargin - margins.bottomMargin
      val desiredTop = paddingTop + margins.topMargin + (available - column.height + 1) / 2
      column.offsetTopAndBottom(desiredTop - column.top)
    }
  }

  fun bind(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    listOrientation: String,
    itemIndex: Int?,
    selected: Boolean,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
    useSourceScale: Boolean = false,
  ) {
    val shouldRestorePressed = touchPressed && boundKey == item.key
    cancelMarketLongPress()
    marketLongPressFired = false
    invalidateCurrentBinding()
    bindingEpoch += 1
    boundKey = item.key
    touchPressed = shouldRestorePressed
    currentLayout = layout
    tag = item
    selectorUsesSourceScale = item.type == "market" || item.usesSelectorSourceScale || useSourceScale
    reorderActive = false
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
    checkboxIconColor = checkboxUncheckedColor
    checkboxUsesSelectorStyle = item.json.optString("presentation") == "networkSelector"
    checkboxBorderColor = Color.argb(
      0x31,
      0,
      0,
      0,
    )
    if (item.json.optString("presentation") == "networkSelector") {
      checkboxCheckedColor = color(theme, "checkboxBackground", "#202020")
      checkboxBorderColor = color(theme, "checkboxBorder", "#00000031")
      checkboxIconColor = color(theme, "checkboxIcon", "#FFFFFF")
      // OneKey patch: the V1 checkbox fills even its unchecked body with iconInverse.
      checkboxUncheckedColor = checkboxIconColor
    }
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
    background = if (touchPressed || reorderActive) pressedRowBackground else restingRowBackground
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
    if (!isEnabled) touchPressed = false
    background = if (touchPressed || reorderActive) pressedRowBackground else restingRowBackground
    // OneKey patch: deprecation dims the row without disabling menu controls.
    // alpha = if (isEnabled) 1f else 0.5f
    alpha = item.json.optDouble("opacity", 1.0).toFloat() * (if (isEnabled) 1f else 0.5f)
    contentDescription = item.json.optString("accessibilityLabel", item.json.optString("title"))
    // OneKey patch: retain stable original selector test identifiers.
    setTag(com.facebook.react.R.id.react_test_id, item.json.optString("testID").takeIf { it.isNotEmpty() })

    when (item.type) {
      "walletGroup" -> bindWalletGroup(item, theme, layout, listOrientation, checkboxState)
      "identity" -> bindIdentity(item, theme, selected, checkboxState)
      "rail" -> bindRail(item, theme)
      "activity" -> bindActivity(item, theme)
      "message" -> bindMessage(item, theme)
      "dataRow" -> bindDataRow(item, theme, checkboxState)
      "market" -> bindMarket(item, theme)
      "mediaTile" -> bindMediaTile(item, theme)
      "metricCard" -> bindMetricCard(item, theme)
      "sectionHeader" -> bindSectionHeader(item, theme, checkboxState)
      "action" -> bindAction(item, theme, checkboxState)
      "system" -> bindSystem(item, theme)
    }
    applySize(item)
    if (item.type == "system" && item.json.optString("variant") == "warning") {
      title.typeface = NativeListFonts.medium(context)
      title.textSize = sp(14f)
      subtitle.textSize = sp(14f)
      TextViewCompat.setLineHeight(title, dp(20))
      TextViewCompat.setLineHeight(subtitle, dp(20))
      minimumHeight = 0
    }
    applyListOrientation(item, listOrientation)
    applySelectorTypography(item)
  }

  // OneKey patch: keep a small idle member pool after reuse or a direct rebind.
  // The compact proxy/expansion keeps every member until it leaves that state.
  private fun trimWalletGroupRows(required: Int) {
    val retained = maxOf(8, required)
    while (walletGroupRows.size > retained) {
      val row = walletGroupRows.removeAt(walletGroupRows.lastIndex)
      (row.parent as? ViewGroup)?.removeView(row)
      row.dispose()
      row.onRowPress = null
      row.onAction = null
      row.onBindingInvalidated = null
    }
  }

  fun recycle() {
    cancelMarketLongPress()
    marketLongPressFired = false
    touchPressed = false
    restoreRestingBackground()
    invalidateCurrentBinding()
    boundKey = null
    leadingImages.forEach(OneKeyImageReusableView::prepareForReuse)
    secondaryImage.prepareForReuse()
    mediaNetworkImage.prepareForReuse()
    metricVisualImages.forEach(OneKeyImageReusableView::prepareForReuse)
    walletGroupRows.forEach { row ->
      row.visibility = VISIBLE
      row.alpha = 1f
      row.recycle()
    }
    if (!reorderActive && walletGroupExpandAnimator?.isRunning != true) trimWalletGroupRows(0)
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
    // OneKey patch: keep row callbacks and checkbox fallback state current without resetting images.
    tag = item
    if (item.type == "walletGroup") {
      val members = buildList {
        add(item.json.getJSONObject("parent"))
        item.json.getJSONArray("children").let { children ->
          for (index in 0 until children.length()) add(children.getJSONObject(index))
        }
      }
      members.forEachIndexed { index, memberJson ->
        walletGroupRows.getOrNull(index)?.bindSelection(
          NativeListItem.parse(memberJson),
          theme,
          layout,
          null,
          memberJson.optBoolean("selected", false),
          checkboxState,
        )
      }
      // The group owns the subdued backdrop. Selection belongs to a member;
      // applying the translucent selected color to both levels produces dark
      // bands in the 12dp gaps and double-composites the parent highlight.
      background = restingRowBackground
      return
    }
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
    // OneKey patch: the comparator permits state changes only on these existing checkbox slots.
    // boundCheckboxData?.let { bindCheckbox(item, it, checkboxState) }
    if (boundCheckboxData != null) {
      val latestCheckbox = if (item.type == "identity") {
        item.json.optJSONArray("trailing")?.let { trailing ->
          (0 until trailing.length()).mapNotNull { trailing.optJSONObject(it) }
            .lastOrNull { it.optString("kind") == "checkbox" }
        }
      } else item.json.optJSONObject("checkbox")
      boundCheckboxData = latestCheckbox ?: boundCheckboxData
      boundCheckboxData?.let { bindCheckbox(item, it, checkboxState) }
    }
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
    applySelectorTypography(item)
  }

  fun dispose() {
    cancelMarketLongPress()
    invalidateCurrentBinding()
    restoreRestingBackground()
    selectorImages.forEach(OneKeyImageReusableView::dispose)
    selectorImages.clear()
    leadingImages.forEach(OneKeyImageReusableView::dispose)
    secondaryImage.dispose()
    mediaNetworkImage.dispose()
    metricVisualImages.forEach(OneKeyImageReusableView::dispose)
    marketBadgeImages.forEach(OneKeyImageReusableView::dispose)
    walletGroupRows.forEach(NativeListRowView::dispose)
  }

  private fun actionOrigin(
    sourceView: View,
    source: String,
    slot: Int? = null,
  ) = NativeListActionOrigin(sourceView, this, bindingEpoch, source, slot,
    if ((tag as? NativeListItem)?.json?.optString("presentation") == "accountSelector" && sourceView in trailingIcons && (sourceView.layoutParams as MarginLayoutParams).marginStart < 0) dp(7) else 0)

  private fun emitAction(
    item: NativeListItem,
    actionKey: String,
    target: NativeSelectionTarget?,
    sourceView: View,
    source: String,
    slot: Int? = null,
  ) {
    onAction?.invoke(item, actionKey, target, actionOrigin(sourceView, source, slot))
  }

  // OneKey patch: match SizableText TABULAR_NUMS on dynamic labels without replacing their typeface.
  private fun applySelectorTypography(item: NativeListItem) {
    val usesSelectorTypography = item.json.optString("presentation") in setOf("accountSelector", "networkSelector", "walletSidebar") || item.type == "system" && item.json.optString("variant") == "warning"
    fun visit(view: View) {
      if (view is OneKeyIconView) view.useSourceScale = selectorUsesSourceScale
      if (view is DottedUnderlineTextView) view.useSourceScale = selectorUsesSourceScale
      if (view is TextView && usesSelectorTypography) {
        val selectorLineHeight = selectorLineHeights.getOrPut(view) { view.lineHeight }
        val original = view.fontFeatureSettings
        if (!selectorOriginalFontFeatures.containsKey(view)) selectorOriginalFontFeatures[view] = original
        view.fontFeatureSettings = if (original.isNullOrEmpty()) "tnum" else if (original.contains("tnum")) original else "$original, 'tnum' 1"
        // OneKey patch: SizableText disables font scaling and rounds font sizes to whole pixels.
        val sourceTypography = selectorUsesSourceScale || item.type == "system" && item.json.optString("variant") == "warning"
        if (sourceTypography) {
          // OneKey patch: React Native CustomStyleSpan disables hinting and preserves fractional advances.
          selectorOriginalPaintFlags.putIfAbsent(view, view.paintFlags)
          view.paintFlags = view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG or Paint.LINEAR_TEXT_FLAG
          val originalSize = selectorFontSizes.getOrPut(view) { view.textSize }
          val sourceSize = originalSize * resources.displayMetrics.density / resources.displayMetrics.scaledDensity
          view.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, kotlin.math.ceil(sourceSize.toDouble()).toFloat())
          view.letterSpacing = 0f
        }
        if (sourceTypography && view.text.isNotEmpty()) {
          val text = SpannableStringBuilder(view.text)
          text.getSpans(0, text.length, SelectorLineHeightSpan::class.java).forEach(text::removeSpan)
          text.setSpan(SelectorLineHeightSpan(selectorLineHeight), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
          view.setLineSpacing(0f, 1f)
          view.text = text
        }
      }
      if (view is ViewGroup) for (index in 0 until view.childCount) visit(view.getChildAt(index))
    }
    visit(this)
  }

  private fun invalidateCurrentBinding() {
    selectorImageRetries.forEach { (image, retry) -> image.removeCallbacks(retry) }
    selectorImageRetries.clear()
    if (boundKey == null) return
    onBindingInvalidated?.invoke(this, bindingEpoch)
    bindingEpoch += 1
  }

  private fun resetViews() {
    clipChildren = true
    clipToPadding = true
    selectorOriginalFontFeatures.forEach { (view, original) -> view.fontFeatureSettings = original }
    selectorOriginalFontFeatures.clear()
    selectorOriginalPaintFlags.forEach { (view, flags) -> view.paintFlags = flags }
    selectorOriginalPaintFlags.clear()
    selectorLineHeights.clear()
    selectorFontSizes.clear()
    // OneKey patch: selector-only views cannot survive a recycled binding.
    selectorViews.forEach { (it.parent as? ViewGroup)?.removeView(it) }
    selectorViews.clear()
    selectorImages.forEach(OneKeyImageReusableView::dispose)
    selectorImages.clear()
    selectorHeight = null
    marketBadgeViews.forEachIndexed { index, badge ->
      (badge.parent as? ViewGroup)?.removeView(badge)
      badge.visibility = GONE
      badge.background = null
      badge.setPadding(0, 0, 0, 0)
      badge.setOnClickListener(null)
      badge.contentDescription = null
      marketBadgeLabels[index].apply {
        text = ""
        setTextColor(color(null, "secondaryText", "#0000009B"))
      }
      marketBadgeImages[index].prepareForReuse()
      marketBadgeImages[index].visibility = GONE
      marketBadgeGlyphs[index].apply {
        visibility = GONE
        iconName = ""
        tintColor = color(null, "secondaryText", "#0000009B")
      }
    }
    title.setOnClickListener(null)
    title.isClickable = false
    walletGroupRows.forEach { it.invalidateCurrentBinding() }
    walletGroupExpandAnimator?.removeAllListeners()
    walletGroupExpandAnimator?.cancel()
    walletGroupExpandAnimator = null
    layoutParams?.takeIf { it.height != ViewGroup.LayoutParams.WRAP_CONTENT }?.let { params ->
      params.height = ViewGroup.LayoutParams.WRAP_CONTENT
      layoutParams = params
    }
    restoreRestingBackground()
    walletGroupRows.forEach { row ->
      row.visibility = VISIBLE
      row.alpha = 1f
      row.recycle()
    }
    activityContentRow.removeAllViews()
    (actionLine.parent as? ViewGroup)?.removeView(actionLine)
    removeAllViews()
    // OneKey patch: the old animator is cancelled and old children are detached.
    // Do not trim currently needed members when re-binding an expanded group.
    val nextItem = tag as? NativeListItem
    val required = if (nextItem?.type == "walletGroup") (nextItem.json.optJSONArray("children")?.length() ?: 0) + 1 else 0
    trimWalletGroupRows(required)
    orientation = HORIZONTAL
    gravity = Gravity.CENTER_VERTICAL
    minimumHeight = 0
    setPadding(dp(12), dp(8), dp(12), dp(8))
    isMediaTile = false
    walletGroupDragChildCount = 0
    walletGroupExpandedHeightPx = 0
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
      it.setTag(com.facebook.react.R.id.react_test_id, null)
      it.gravity = Gravity.END
      it.maxLines = 1
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
    setOnClickListener { view ->
      (view.tag as? NativeListItem)?.let { item ->
        if (item.type == "market" && marketLongPressFired) {
          marketLongPressFired = false
          return@let
        }
        // OneKey patch: allow create-address accessories when whole-row press is gated.
        if (!item.json.optBoolean("pressDisabled", false)) onRowPress?.invoke(item, actionOrigin(view, "row"))
      }
    }
  }

  private fun cancelMarketLongPress() {
    marketLongPressRunnable?.let(marketLongPressHandler::removeCallbacks)
    marketLongPressRunnable = null
  }

  private fun restoreRestingBackground() {
    background = if (reorderActive) pressedRowBackground else restingRowBackground
    leadingFrame.alpha = 1f
  }

  fun setReorderActive(active: Boolean) {
    if ((tag as? NativeListItem)?.type == "walletGroup" && reorderActive == active) return
    reorderActive = active
    if ((tag as? NativeListItem)?.type == "walletGroup") {
      walletGroupExpandAnimator?.removeAllListeners()
      walletGroupExpandAnimator?.cancel()
      walletGroupExpandAnimator = null
      walletGroupRows.forEachIndexed { index, row ->
        row.alpha = 1f
        row.visibility = if (active && index > 0) GONE else VISIBLE
        if (index == 0) row.setReorderActive(active)
      }
      minimumHeight = if (active) dp(68) else walletGroupExpandedHeightPx
      layoutParams = layoutParams?.apply {
        height = ViewGroup.LayoutParams.WRAP_CONTENT
      }
      background = restingRowBackground
      requestLayout()
      invalidate()
      return
    }
    restoreRestingBackground()
  }

  fun finishWalletGroupReorder(
    durationMs: Long,
    interpolator: TimeInterpolator,
    completion: (() -> Unit)? = null,
  ) {
    if ((tag as? NativeListItem)?.type != "walletGroup") {
      setReorderActive(false)
      completion?.invoke()
      return
    }
    reorderActive = false
    walletGroupRows.firstOrNull()?.setReorderActive(false)
    val startHeight = height.coerceAtLeast(dp(68))
    val targetHeight = walletGroupExpandedHeightPx
    minimumHeight = startHeight
    layoutParams = layoutParams?.apply { height = startHeight }
    walletGroupRows.drop(1).forEach { row ->
      row.visibility = VISIBLE
      row.alpha = 0f
    }
    background = restingRowBackground
    if (targetHeight <= startHeight) {
      walletGroupRows.forEach { row ->
        row.visibility = VISIBLE
        row.alpha = 1f
      }
      minimumHeight = targetHeight
      layoutParams = layoutParams?.apply { height = ViewGroup.LayoutParams.WRAP_CONTENT }
      requestLayout()
      invalidate()
      completion?.invoke()
      return
    }
    walletGroupExpandAnimator = ValueAnimator.ofInt(startHeight, targetHeight).apply {
      duration = durationMs
      this.interpolator = interpolator
      addUpdateListener { animator ->
        val progress = animator.animatedFraction
        layoutParams = layoutParams?.apply { height = animator.animatedValue as Int }
        walletGroupRows.drop(1).forEach { it.alpha = progress }
        requestLayout()
        invalidate()
      }
      addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          walletGroupExpandAnimator = null
          walletGroupRows.forEach { row ->
            row.visibility = VISIBLE
            row.alpha = 1f
          }
          minimumHeight = targetHeight
          layoutParams = layoutParams?.apply { height = ViewGroup.LayoutParams.WRAP_CONTENT }
          requestLayout()
          invalidate()
          postOnAnimation {
            if (!reorderActive) {
              walletGroupRows.forEach { row ->
                row.visibility = VISIBLE
                row.alpha = 1f
              }
              requestLayout()
              invalidate()
            }
          }
          completion?.invoke()
        }
      })
      start()
    }
  }

  private fun bindWalletGroup(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    listOrientation: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    orientation = VERTICAL
    gravity = Gravity.CENTER_HORIZONTAL
    setPadding(0, 0, 0, 0)
    isClickable = false
    setOnClickListener(null)
    val members = mutableListOf<JSONObject>()
    members.add(item.json.getJSONObject("parent"))
    item.json.getJSONArray("children").let { children ->
      for (index in 0 until children.length()) {
        members.add(children.getJSONObject(index))
      }
    }
    if (members.first().has("height")) setPadding(dp(1), dp(1), dp(1), dp(1))
    walletGroupDragChildCount = members.size - 1
    walletGroupExpandedHeightPx =
      // OneKey patch: expanded groups include individual wallet badge heights.
      // dp(members.size * 68 + walletGroupDragChildCount * 12)
      dp(members.sumOf { it.optInt("height", if ((it.optJSONArray("badges")?.length() ?: 0) > 0) 92 else 68) } + walletGroupDragChildCount * 12 + if (members.first().has("height")) 2 else 0)
    walletGroupDragBadgeBackgroundPaint.color = color(
      theme,
      "inverseBackground",
      "#000000DF",
    )
    walletGroupDragBadgeBorderPaint.color = color(
      theme,
      "rowBackground",
      "#FFFFFF",
    )
    walletGroupDragBadgeBorderPaint.strokeWidth = dp(1).toFloat()
    walletGroupDragBadgeTextPaint.color = color(
      theme,
      "inverseText",
      "#FCFCFC",
    )
    walletGroupDragBadgeTextPaint.textSize = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_SP,
      sp(12f),
      resources.displayMetrics,
    )
    while (walletGroupRows.size < members.size) {
      walletGroupRows.add(NativeListRowView(reactContext))
    }
    members.forEachIndexed { index, memberJson ->
      val member = NativeListItem.parse(memberJson)
      val memberRow = walletGroupRows[index]
      memberRow.onRowPress = { item, origin -> onRowPress?.invoke(item, origin) }
      memberRow.onAction = { source, actionKey, target, origin ->
        onAction?.invoke(source, actionKey, target, origin)
      }
      memberRow.onBindingInvalidated = { row, epoch ->
        onBindingInvalidated?.invoke(row, epoch)
      }
      memberRow.bind(
        member,
        theme,
        layout,
        listOrientation,
        null,
        memberJson.optBoolean("selected", false),
        checkboxState,
        useSourceScale = selectorUsesSourceScale,
      )
      // OneKey patch: member geometry matches the outer group height calculation.
      // memberRow.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp(68)).apply {
      memberRow.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp(memberJson.optInt("height", if ((memberJson.optJSONArray("badges")?.length() ?: 0) > 0) 92 else 68))).apply {
        if (index > 0) topMargin = dp(12)
      }
      addView(memberRow)
    }
    val fill = color(theme, "subduedBackground", "#F9F9F9")
    val stroke = color(theme, "separator", "#0000001F")
    restingRowBackground = GradientDrawable().apply {
      setColor(fill)
      setStroke(dp(1), stroke)
      cornerRadius = dp(12).toFloat()
    }
    pressedRowBackground = restingRowBackground
    background = restingRowBackground
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
        ),
      )
      addView(
        mainColumn,
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
          // OneKey patch: snap the source 4 + 40 + 4 sequence once instead of rounding each gap.
          topMargin = if (item.json.has("height")) dp(48) - dp(44) else dp(4)
        },
      )
      // OneKey patch: wallet tags are a centered line below the wallet name.
      item.json.optJSONArray("badges")?.takeIf { it.length() > 0 }?.let { badges ->
        val isSelector = item.json.has("height")
        val badgeLineHeight = if (isSelector) 14 else 16
        val badgeHeight = badgeLineHeight + 4
        val line = LinearLayout(context).apply { orientation = HORIZONTAL; gravity = Gravity.CENTER }
        for (index in 0 until badges.length()) {
          val badge = TextView(context).apply {
            text = badges.getJSONObject(index).optString("text")
            textSize = sp(if (isSelector) 11f else 12f)
            typeface = NativeListFonts.regular(context)
            includeFontPadding = false
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            val warning = isSelector && badges.getJSONObject(index).optString("tone") == "warning"
            setTextColor(color(theme, if (warning) "caution" else "secondaryText", if (warning) "#AB6400" else "#0000009B"))
            background = roundedFill(color(theme, if (warning) "cautionBackground" else if (isSelector) "subduedBackground" else "strongBackground", if (warning) "#FFF4D5" else "#00000006"), 4f)
            setPadding(dp(if (isSelector) 6 else 4), dp(2), dp(if (isSelector) 6 else 4), dp(2))
          }
          TextViewCompat.setLineHeight(badge, dp(badgeLineHeight))
          line.addView(badge, LayoutParams(LayoutParams.WRAP_CONTENT, dp(badgeHeight)).apply { if (index > 0) marginStart = dp(4) })
        }
        mainColumn.addView(line, LayoutParams(LayoutParams.WRAP_CONTENT, dp(badgeHeight)).apply { topMargin = dp(4) })
        selectorViews.add(line)
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
      leadingActionIcon.tintColor = safeColor(
        action.optString("tintColor"),
        color(theme, "icon", "#0000009B"),
      )
      leadingActionIcon.isEnabled = !action.optBoolean("disabled", false)
      leadingActionIcon.alpha = if (leadingActionIcon.isEnabled) 1f else 0.4f
      leadingActionIcon.setOnClickListener {
        emitAction(
          item,
          action.optString("actionKey"),
          null,
          leadingActionIcon,
          "leadingAction",
        )
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
    // OneKey patch: custom network initials retain LetterAvatar typography.
    if (item.json.optString("presentation") == "networkSelector" && leading?.optJSONObject("image") == null && leading?.optJSONObject("fallbackIcon") == null && !leading?.optString("fallbackText").isNullOrEmpty()) {
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
    item.json.optJSONArray("subtitleSegments")?.takeIf { it.length() > 0 }?.let { segments ->
      subtitle.visibility = GONE
      val line = SelectorSubtitleLayout(context).apply { orientation = HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
      for (index in 0 until segments.length()) {
        val segment = segments.getJSONObject(index)
        if (segment.optBoolean("separatorBefore", false)) {
          val dot = View(context).apply { background = roundedFill(color(theme, "disabledText", "#00000072"), 2f) }
          line.addView(dot, LayoutParams(dp(4), dp(4)).apply { marginStart = dp(6); marginEnd = dp(6) })
        }
        val label = TextView(context).apply {
          text = segment.optString("text")
          typeface = NativeListFonts.regular(context)
          textSize = sp(14f)
          includeFontPadding = false
          maxLines = 1
          ellipsize = TextUtils.TruncateAt.END
          val toneKey = when (segment.optString("tone")) { "primary" -> "primaryText"; "disabled" -> "disabledText"; "caution" -> "caution"; "positive" -> "positive"; "negative" -> "negative"; else -> "secondaryText" }
          setTextColor(color(theme, toneKey, if (toneKey == "caution") "#AB6400" else "#0000009B"))
        }
        TextViewCompat.setLineHeight(label, dp(20))
        applyValueSegments(label, segment.optJSONArray("textSegments"), 14, 20, false)
        line.addView(label, LayoutParams(LayoutParams.WRAP_CONTENT, dp(20)))
      }
      mainColumn.addView(line, 2, LayoutParams(LayoutParams.MATCH_PARENT, dp(20)))
      selectorViews.add(line)
    }
    item.json.optJSONArray("titleMatch")?.takeIf { it.length() > 0 }?.let { matches ->
      val highlighted = SpannableStringBuilder(title.text)
      for (index in 0 until matches.length()) {
        val match = matches.getJSONObject(index)
        val start = match.optInt("start")
        val end = match.optInt("end")
        if (start >= 0 && end > start && end <= highlighted.length) highlighted.setSpan(ForegroundColorSpan(color(theme, "info", "#0D74CE")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
      }
      title.text = highlighted
    }
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
    if (item.json.has("height") && item.json.optString("presentation") == "networkSelector" && accessories.hasAccessory("checkbox")) {
      // OneKey patch: retain ListItem's title-to-accessory gap when measuring truncation.
      (mainColumn.layoutParams as LayoutParams).marginEnd = dp(12)
    }
    if (accessories.hasAccessory("checkbox") && accessories.hasAccessory("value")) {
      trailingColumn.orientation = HORIZONTAL
      trailingColumn.gravity = Gravity.END or Gravity.CENTER_VERTICAL
      checkbox.layoutParams = LayoutParams(dp(20), dp(20)).apply { marginStart = dp(12) }
    } else if (accessories.isValuePairMenu()) {
      trailingColumn.orientation = HORIZONTAL
      trailingColumn.gravity = Gravity.END or Gravity.CENTER_VERTICAL
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
        actionView.setOnClickListener {
          emitAction(item, action.optString("key"), null, actionView, "footerAction", index)
        }
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

  private fun marketTypeface(weight: String, fallback: String): Typeface = when (
    weight.ifEmpty { fallback }
  ) {
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
    view.textSize = sp(style?.optDouble("fontSize", defaultSize.toDouble())?.toFloat() ?: defaultSize)
    view.typeface = marketTypeface(style?.optString("fontWeight").orEmpty(), defaultWeight)
    view.setTextColor(safeColor(style?.optString("color"), defaultColor))
    val alignment = style?.optString("alignment", defaultAlignment) ?: defaultAlignment
    view.gravity = Gravity.CENTER_VERTICAL or when (alignment) {
      "center" -> Gravity.CENTER_HORIZONTAL
      "end" -> Gravity.END
      else -> Gravity.START
    }
    view.maxLines = style?.optInt("lines", 1)?.coerceIn(1, 2) ?: 1
    view.ellipsize = TextUtils.TruncateAt.END
    TextViewCompat.setLineHeight(
      view,
      dp(style?.optDouble("lineHeight", defaultLineHeight.toDouble())?.roundToInt() ?: defaultLineHeight),
    )
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

  private fun bindMarket(item: NativeListItem, theme: JSONObject?) {
    // Network badges extend beyond the leading image's frame.
    clipChildren = false
    val variant = item.json.optString("variant")
    val style = item.json.optJSONObject("style")
    val imageStyle = style?.optJSONObject("image")
    val imageWidth = imageStyle?.optDouble("width", if (variant == "stock") 40.0 else 32.0)?.roundToInt()
      ?: if (variant == "stock") 40 else 32
    val imageHeight = imageStyle?.optDouble("height", if (variant == "stock") 40.0 else 32.0)?.roundToInt()
      ?: if (variant == "stock") 40 else 32
    val horizontalPadding = style?.optDouble("horizontalPadding", if (variant == "perp") 16.0 else 20.0)?.roundToInt()
      ?: if (variant == "perp") 16 else 20
    val verticalPadding = style?.optDouble("verticalPadding", 12.0)?.roundToInt() ?: 12
    val leadingGap = style?.optDouble("leadingGap", if (variant == "perp") 8.0 else 14.0)?.roundToInt()
      ?: if (variant == "perp") 8 else 14
    setPadding(dp(horizontalPadding), dp(verticalPadding), dp(horizontalPadding), dp(verticalPadding))

    val leading = JSONObject(item.json.getJSONObject("leading").toString())
    imageStyle?.optString("shape")?.takeIf(String::isNotEmpty)?.let { leading.put("shape", it) }
    imageStyle?.optString("contentFit")?.takeIf(String::isNotEmpty)?.let { contentFit ->
      leading.optJSONObject("image")?.put("contentFit", contentFit)
    }
    val shape = imageStyle?.optString("shape", leading.optString("shape", "circle"))
      ?: leading.optString("shape", "circle")
    val cornerRadius = imageStyle?.takeIf { it.has("cornerRadius") }?.optDouble("cornerRadius")?.toFloat()
      ?: when (shape) {
        "square" -> 0f
        "rounded" -> 8f
        else -> minOf(imageWidth, imageHeight) / 2f
      }
    addLeading(
      leading,
      sizeDp = imageWidth,
      spacingDp = leadingGap,
      heightDp = imageHeight,
      cornerRadiusDp = cornerRadius,
    )

    addView(mainColumn, weighted())
    titleLine.packsChildrenAtStart = true
    showText(title, item.json.optString("title"), style?.optJSONObject("title")?.optInt("lines", 1) ?: 1)
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
        val hasGlyph = badge.optString("iconName") == "verified"
        val remoteIcon = badge.optJSONObject("icon")
        val hasIcon = hasGlyph || remoteIcon != null
        val text = badge.optString("text")
        val toneColor = when (badge.optString("tone")) {
          "success" -> color(theme, "positive", "#218358")
          "danger" -> color(theme, "negative", "#CE2C31")
          "info" -> color(theme, "info", "#0D74CE")
          "warning" -> color(theme, "primaryText", "#202020")
          else -> color(theme, "secondaryText", "#646464")
        }
        val foreground = safeColor(badge.optString("textColor"), toneColor)
        badgeView.visibility = VISIBLE
        val iconOnly = hasIcon && text.isEmpty()
        badgeView.setPadding(dp(if (iconOnly) 0 else if (hasIcon) 2 else 5), 0, dp(if (iconOnly) 0 else 5), 0)
        badgeView.background = roundedFill(
          safeColor(
            badge.optString("backgroundColor"),
            if (hasIcon && text.isEmpty()) Color.TRANSPARENT else color(theme, "strongBackground", "#0000000F"),
          ),
          4f,
        )
        badgeLabel.text = text
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
          bindImage(icon, marketBadgeImages[index], item.key, 20 + index, "generic")
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
          LayoutParams(LayoutParams.WRAP_CONTENT, dp(18)).apply { marginStart = dp(titleBadgeGap) },
        )
      }
    }
    subtitle.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
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
      subtitle.text = marketText(
        subtitleText,
        subtitleSegments,
        style?.optJSONObject("subtitle")?.optDouble("fontSize", 14.0)?.toFloat() ?: 14f,
      )
    }
    trailingColumn.orientation = HORIZONTAL
    trailingColumn.gravity = Gravity.END or Gravity.CENTER_VERTICAL
    addView(trailingColumn, wrap())
    bindMarketQuote(item, theme)
    item.json.optJSONObject("diagnostics")?.optString("imageBindActionKey")
      ?.takeIf(String::isNotEmpty)
      ?.takeIf { leading.optJSONObject("image") != null || leading.optJSONObject("networkImage") != null }
      ?.let { actionKey -> onAction?.invoke(item, actionKey, null, null) }
  }

  fun bindMarketQuote(item: NativeListItem, theme: JSONObject?) {
    if (boundKey != item.key || item.type != "market") return
    tag = item
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
    price.text = marketText(
      item.json.optString("price"),
      item.json.optJSONArray("priceSegments"),
      priceStyle?.optDouble("fontSize", 16.0)?.toFloat() ?: 16f,
    )
    val trailingGap = style?.optDouble("trailingGap", 8.0)?.roundToInt() ?: 8
    price.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
      marginEnd = dp(trailingGap)
    }

    val changeData = item.json.getJSONObject("change")
    val changeStyle = style?.optJSONObject("change")
    val change = trailingViews[1]
    change.isClickable = false
    change.isLongClickable = false
    val toneColor = when (changeData.optString("tone")) {
      "positive" -> color(theme, "positive", "#218358")
      "negative" -> color(theme, "negative", "#CE2C31")
      else -> color(theme, "secondaryText", "#8D8D8D")
    }
    val textColor = safeColor(
      changeData.optString("textColor"),
      color(theme, "inverseText", "#FFFFFF"),
    )
    change.visibility = VISIBLE
    applyMarketTextStyle(change, changeStyle, 14f, 20, "medium", textColor, "center")
    change.text = marketText(
      changeData.optString("text"),
      changeData.optJSONArray("textSegments"),
      changeStyle?.optDouble("fontSize", 14.0)?.toFloat() ?: 14f,
    )
    change.background = roundedFill(
      safeColor(changeData.optString("backgroundColor"), toneColor),
      style?.optDouble("changeCornerRadius", 8.0)?.toFloat() ?: 8f,
    )
    change.layoutParams = LayoutParams(
      dp(style?.optDouble("changeWidth", 80.0)?.roundToInt() ?: 80),
      dp(style?.optDouble("changeHeight", 32.0)?.roundToInt() ?: 32),
    )
    contentDescription = item.json.optString(
      "accessibilityLabel",
      listOf(
        item.json.optString("title"),
        item.json.optString("subtitle"),
        item.json.optString("price"),
        changeData.optString("text"),
      ).filter(String::isNotEmpty).joinToString(", "),
    )
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
      close.setOnClickListener {
        emitAction(item, actionKey, null, close, "mediaClose")
      }
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
    // OneKey patch: title help emits its own frame instead of toggling the section.
    item.json.optString("titleActionKey").takeIf { it.isNotEmpty() }?.let { key ->
      title.setOnClickListener { emitAction(item, key, null, title, "leadingAction") }
    }
    val isNetworkSelector = item.json.optString("presentation") == "networkSelector"
    val isHistory = variant == "history" || item.sectionKey?.startsWith("history-") == true
    val isTokenManager = item.sectionKey in setOf("linear-tokens", "action-tokens")
    val isExplicitNetworkHeader = isNetworkSelector && item.json.has("height")
    val hasDottedTitle = isSummary || (isNetworkSelector && (!isExplicitNetworkHeader || item.json.optString("titleActionKey").isNotEmpty())) ||
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
    if (isNetworkSelector) {
      val verticalInset = if (isExplicitNetworkHeader && item.json.optString("titleActionKey").isEmpty()) 8 else 12
      setPadding(dp(headerHorizontalInset), dp(verticalInset), dp(headerHorizontalInset), dp(verticalInset))
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
      trailingViews[0].setTag(com.facebook.react.R.id.react_test_id, item.json.optString("valueActionTestID").takeIf { it.isNotEmpty() })
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
    applyValueSegments(trailingViews[0], item.json.optJSONArray("valueSegments"))
  }

  // OneKey patch: preserve compact zero-count digits without changing their baseline.
  private fun applyValueSegments(view: TextView, segments: JSONArray?, fontSize: Int = 16, lineHeight: Int = 24, medium: Boolean = true) {
    if (segments == null || segments.length() == 0) return
    val value = SpannableStringBuilder()
    for (index in 0 until segments.length()) {
      val segment = segments.getJSONObject(index)
      val start = value.length
      value.append(segment.optString("text"))
      if (segment.optString("style") == "subscript") {
        val size = kotlin.math.ceil(fontSize * 0.6).toFloat()
        val span = if (selectorUsesSourceScale) AbsoluteSizeSpan(kotlin.math.ceil((size * resources.displayMetrics.density).toDouble()).toInt(), false) else AbsoluteSizeSpan(sp(size).roundToInt(), true)
        value.setSpan(span, start, value.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
      }
    }
    view.textSize = sp(fontSize.toFloat())
    view.typeface = if (medium) NativeListFonts.medium(context) else NativeListFonts.regular(context)
    TextViewCompat.setLineHeight(view, dp(lineHeight))
    view.text = value
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
      if (isAccountSelector) leadingFrame.background = roundedFill(safeColor(icon.optString("backgroundColor"), color(theme, "strongBackground", "#0000000F")), 8f)
    }
    addView(mainColumn, weighted())
    showText(title, item.json.optString("title"), 1)
    if (isAccountSelector) {
      title.typeface = if (item.json.has("icon")) NativeListFonts.medium(context) else NativeListFonts.regular(context)
      title.setTextColor(color(theme, if (item.json.optString("tone") == "primary") "primaryText" else "secondaryText", "#0000009B"))
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
    setOnClickListener {
      emitAction(item, item.json.optString("actionKey"), null, this, "row")
    }
  }

  private fun bindSystem(item: NativeListItem, theme: JSONObject?) {
    val variant = item.json.optString("variant")
    val isMarket = item.json.optString("presentation") == "market"
    if (isMarket) setPadding(dp(20), dp(12), dp(20), dp(12))
    if (variant == "loading" && item.json.optString("loadingStyle") == "skeleton") {
      setPadding(dp(20), dp(12), dp(20), dp(12))
      addView(
        NativeListMarketSkeleton(context, color(theme, "background", "#FFFFFF")),
        LayoutParams(LayoutParams.MATCH_PARENT, dp(32)),
      )
      return
    }
    if (variant == "loading" && item.json.optString("loadingStyle") == "spinner") {
      gravity = Gravity.CENTER
      setPadding(0, dp(16), 0, dp(16))
      addView(
        ProgressBar(context, null, android.R.attr.progressBarStyleSmall).apply {
          isIndeterminate = true
          indeterminateTintList = android.content.res.ColorStateList.valueOf(color(theme, "icon", "#0000009B"))
        },
        LayoutParams(dp(20), dp(20)),
      )
      return
    }
    if (isMarket && variant == "noMatch") {
      gravity = Gravity.CENTER
      setPadding(dp(32), dp(32), dp(32), dp(32))
      title.textSize = sp(16f)
      title.typeface = NativeListFonts.regular(context)
      title.gravity = Gravity.CENTER
      title.setTextColor(color(theme, "secondaryText", "#0000009B"))
      TextViewCompat.setLineHeight(title, dp(24))
      showText(title, item.json.optString("message"), 1)
      addView(mainColumn, weighted())
      return
    }
    // OneKey patch: warning title/description wrap inside the actual scroll content.
    if (variant == "warning") {
      setPadding(dp(12), dp(14), dp(12), dp(14))
      addView(mainColumn, weighted())
      showText(title, item.json.optString("title"), Int.MAX_VALUE)
      showText(subtitle, item.json.optString("message"), Int.MAX_VALUE)
      title.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
      subtitle.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) }
      titleLine.packsChildrenAtStart = false
      return
    }
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
      addLeading(JSONObject().put("kind", "skeleton"), if (isMarket) 32 else 40)
      leadingFallback.text = ""
      leadingFallback.background = roundedFill(
        color(theme, "strongBackground", "#0000000F"),
        if (isMarket) 16f else 20f,
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
      setOnClickListener {
        emitAction(item, item.json.optString("actionKey"), null, this, "row")
      }
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
    heightDp: Int = sizeDp,
    cornerRadiusDp: Float? = null,
  ) {
    leadingFrame.visibility = VISIBLE
    leadingFrame.layoutParams = LayoutParams(dp(sizeDp), dp(heightDp)).apply {
      val item = tag as? NativeListItem
      // OneKey patch: Yoga rounds cumulative selector edges, not each 12dp gap separately.
      marginEnd = if (item?.json?.has("height") == true && item.json.optString("presentation") in setOf("accountSelector", "networkSelector")) dp(12 + sizeDp + spacingDp) - dp(12) - dp(sizeDp) else dp(spacingDp)
    }
    addView(leadingFrame)
    leadingFallback.layoutParams = FrameLayout.LayoutParams(dp(sizeDp), dp(heightDp))
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
        cornerRadiusDp ?: leadingCornerRadius(shape, minOf(sizeDp, heightDp)),
      )
    }
    leadingFallback.background = roundedFill(
      visualBackground,
      cornerRadiusDp ?: leadingCornerRadius(shape, minOf(sizeDp, heightDp)),
    )
    leadingFallback.visibility = if (!isIcon && sources.isEmpty()) VISIBLE else GONE
    if (isIcon) {
      leadingFrame.background = GradientDrawable().apply {
        setColor(visualBackground)
        setStroke(1, parseNativeListColor("#0000001F"))
        cornerRadius = scaledDp(cornerRadiusDp ?: leadingCornerRadius(shape, minOf(sizeDp, heightDp)))
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
    val hasAccountSelectorBadge =
      kind == "account" &&
        (tag as? NativeListItem)?.json?.optString("presentation") == "accountSelector" &&
        (visual.optJSONArray("overlays")?.length() ?: 0) > 0
    if (tokenPair || hasAccountSelectorBadge) {
      // Network badges intentionally extend past the avatar frame.
      clipChildren = false
    }
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
        heightDp = heightDp,
        tokenPair = tokenPair,
      )
      image.outlineProvider = when {
        tokenPair && index == 1 -> circleOutlineProvider
        cornerRadiusDp != null -> roundedOutlineProvider(cornerRadiusDp)
        else -> leadingOutlineProvider(shape)
      }
      image.clipToOutline = true
      if ((tag as? NativeListItem)?.type == "market" && index == 0 && visual.optString("borderColor").isNotEmpty()) {
        val inset = dp(1)
        val radius = scaledDp(cornerRadiusDp ?: leadingCornerRadius(shape, minOf(sizeDp, heightDp)))
        leadingFrame.background = GradientDrawable().apply {
          setColor(visualBackground)
          setStroke(inset, safeColor(visual.optString("borderColor"), Color.TRANSPARENT))
          this.cornerRadius = radius
        }
        image.layoutParams = FrameLayout.LayoutParams(dp(sizeDp) - inset * 2, dp(heightDp) - inset * 2).apply {
          leftMargin = inset
          topMargin = inset
        }
        image.outlineProvider = object : ViewOutlineProvider() {
          override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(-inset, -inset, view.width + inset, view.height + inset, radius)
          }
        }
      }
      val fallbackIcon = if (index == 0) visual.optJSONObject("fallbackIcon") else null
      val expectedEpoch = bindingEpoch
      bindImage(source, image, boundKey ?: "", index, variant,
        onLoad = if (fallbackIcon == null) null else ({
          if (bindingEpoch == expectedEpoch) { image.visibility = VISIBLE; leadingIcon.visibility = GONE }
        }),
        onError = if (fallbackIcon == null) null else ({
          if (bindingEpoch == expectedEpoch) {
            image.visibility = GONE
            leadingFallback.visibility = GONE
            leadingIcon.iconName = fallbackIcon.optString("name")
            leadingIcon.tintColor = safeColor(fallbackIcon.optString("tintColor"), parseNativeListColor("#0000009B"))
            leadingIcon.visibility = VISIBLE
          }
        }),
      )
    }
    // OneKey patch: wallet overlays retain source images, provider colors and QR text.
    visual.optJSONArray("overlays")?.let { overlays ->
      for (index in 0 until overlays.length()) {
        val overlay = overlays.getJSONObject(index)
        val size = overlay.optInt("size", 20)
        val inset = dp(overlay.optInt("padding", 0))
        val isWalletText = selectorUsesSourceScale && (tag as? NativeListItem)?.json?.optString("presentation") == "walletSidebar" && overlay.optString("text").isNotEmpty() && overlay.optJSONObject("image") == null && overlay.optString("name").isEmpty()
        val width = overlay.optInt("width", size)
        val height = overlay.optInt("height", if (isWalletText) 16 else size)
        val offsetX = dp(overlay.optInt("offsetX", overlay.optInt("offset", 2)))
        val offsetY = dp(overlay.optInt("offsetY", overlay.optInt("offset", 2)))
        val frame = FrameLayout(context).apply {
          setPadding(if (isWalletText) dp(2) else inset, if (isWalletText) 0 else inset, if (isWalletText) dp(2) else inset, if (isWalletText) 0 else inset)
          background = roundedFill(safeColor(overlay.optString("backgroundColor"), Color.TRANSPARENT), minOf(width, height) / 2f)
          outlineProvider = ViewOutlineProvider.BACKGROUND
          clipToOutline = true
        }
        val image = overlay.optJSONObject("image")
        val view = when {
          image != null -> OneKeyImageReusableView(reactContext).also {
            bindImage(image, it, boundKey ?: "", 10 + index, "generic")
            selectorImages.add(it)
          }
          overlay.optString("text").isNotEmpty() -> TextView(context).apply {
            text = overlay.optString("text")
            textSize = sp(if (isWalletText) 12f else 10f)
            typeface = if (isWalletText) NativeListFonts.regular(context) else NativeListFonts.medium(context)
            includeFontPadding = false
            gravity = Gravity.CENTER
            if (isWalletText) TextViewCompat.setLineHeight(this, dp(16))
            setTextColor(safeColor(overlay.optString("tintColor"), color(null, "secondaryText", "#0000009B")))
          }
          else -> OneKeyIconView(context).apply {
            iconName = overlay.optString("name")
            tintColor = safeColor(overlay.optString("tintColor"), parseNativeListColor("#0000009B"))
          }
        }
        frame.addView(view, FrameLayout.LayoutParams(if (isWalletText && !overlay.has("width")) FrameLayout.LayoutParams.WRAP_CONTENT else FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        val topLeft = overlay.optString("position") == "topLeft"
        leadingFrame.addView(frame, FrameLayout.LayoutParams(if (isWalletText && !overlay.has("width")) FrameLayout.LayoutParams.WRAP_CONTENT else dp(width), dp(height), if (topLeft) Gravity.START or Gravity.TOP else Gravity.END or Gravity.BOTTOM).apply {
          if (topLeft) { marginStart = -offsetX; topMargin = -offsetY } else { marginEnd = -offsetX; bottomMargin = -offsetY }
        })
        selectorViews.add(frame)
      }
    }
    visual.optJSONObject("fallbackIcon")?.takeIf { sources.isEmpty() }?.let { fallbackIcon ->
      leadingFallback.visibility = GONE
      leadingIcon.visibility = VISIBLE
      leadingIcon.iconName = fallbackIcon.optString("name")
      leadingIcon.tintColor = safeColor(fallbackIcon.optString("tintColor"), parseNativeListColor("#0000009B"))
      if (selectorUsesSourceScale && (tag as? NativeListItem)?.json?.optString("presentation") == "walletSidebar" && leadingIcon.iconName == "PlusSmallOutline") {
        leadingIcon.layoutParams = FrameLayout.LayoutParams(dp(24), dp(24), Gravity.CENTER)
        leadingIcon.glyphSizeDp = 24
      }
      if (selectorUsesSourceScale && (tag as? NativeListItem)?.json?.optString("presentation") == "walletSidebar" && leadingIcon.iconName == "LockSolid") {
        leadingIcon.layoutParams = FrameLayout.LayoutParams(dp(40), dp(40), Gravity.CENTER)
        leadingIcon.glyphSizeDp = 40
      }
    }
    if (visual.optString("borderStyle") == "dashed") {
      leadingFrame.background = GradientDrawable().apply {
        setColor(visualBackground)
        cornerRadius = dp(sizeDp / 2).toFloat()
        setStroke(dp(if (selectorUsesSourceScale && (tag as? NativeListItem)?.json?.optString("presentation") == "walletSidebar") 1 else 2), safeColor(visual.optString("borderColor"), parseNativeListColor("#00000072")), dp(4).toFloat(), dp(4).toFloat())
      }
      leadingFallback.background = null
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
    heightDp: Int,
    tokenPair: Boolean,
  ): FrameLayout.LayoutParams {
    if (count == 1 || tokenPair && index == 0) {
      return FrameLayout.LayoutParams(dp(sizeDp), dp(heightDp))
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
        "value" -> {
          showTrailing(textIndex, accessory.optString("text"), !accessory.optBoolean("secondary", false))
          applyValueSegments(trailingViews[textIndex], accessory.optJSONArray("textSegments"))
          textIndex++
        }
        "valuePair" -> showTrailingValuePair(textIndex++, accessory, theme)
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
        "menu" -> showTrailingMenu(textIndex++, accessory.optString("actionKey"))
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
    checkbox.setState(state, checkboxIconColor)
    val usesSourceCheckboxGeometry = checkboxUsesSelectorStyle && item.json.has("height")
    checkbox.usesSelectorGeometry = usesSourceCheckboxGeometry
    if (usesSourceCheckboxGeometry) {
      // OneKey patch: source Yoga children may round into padding; retain their complete border.
      clipToPadding = false
      // OneKey patch: even a transparent source border changes RN's background clipping path.
      checkbox.background = null
      BackgroundStyleApplicator.setBackgroundColor(checkbox, if (state == "unchecked") checkboxUncheckedColor else checkboxCheckedColor)
      BackgroundStyleApplicator.setBorderWidth(checkbox, LogicalEdge.ALL, 2f)
      BackgroundStyleApplicator.setBorderColor(checkbox, LogicalEdge.ALL, if (state == "unchecked") checkboxBorderColor else Color.TRANSPARENT)
      BackgroundStyleApplicator.setBorderRadius(checkbox, BorderRadiusProp.BORDER_RADIUS, LengthPercentage(4f, LengthPercentageType.POINT))
    } else {
      checkbox.background = if (state == "unchecked") {
        if (checkboxUsesSelectorStyle) GradientDrawable().apply { setColor(checkboxUncheckedColor); setStroke(dp(2), checkboxBorderColor); cornerRadius = scaledDp(4f) }
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
      // OneKey patch: explicit selector rows preserve the v1 ListItem corner radius.
      item.type == "identity" && item.json.optString("presentation") in setOf("accountSelector", "networkSelector") && item.json.has("height") -> "single"
      else -> when (item.type) {
      "metricCard" -> "single"
      "rail" -> "rail"
      else -> item.json.optString("groupPosition")
      }
    }
    var backgroundGroupPosition = if (item.type == "mediaTile") "mediaTile" else groupPosition
    if (layout == "sectioned" && !item.json.optBoolean("selected", false)) {
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
    // OneKey patch: section heading backgrounds are independent of list rows.
    if (item.json.has("backgroundColor")) rowBackground = safeColor(item.json.optString("backgroundColor"), rowBackground)
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
    view.maxLines = 1
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
          ?.let { item ->
            emitAction(item, actionKey, null, view, "trailingAccessory", index)
          }
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
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp(16f), resources.displayMetrics).roundToInt(),
      ),
      primaryStart,
      value.length,
      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
    )
    if (primary.isNotEmpty() && secondary.isNotEmpty()) value.append('\n')
    val secondaryStart = value.length
    value.append(secondary)
    value.setSpan(
      ForegroundColorSpan(accessoryTextColor(accessory.optString("secondaryTone"), "secondary", theme)),
      secondaryStart,
      value.length,
      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
    )
    value.setSpan(
      AbsoluteSizeSpan(
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp(14f), resources.displayMetrics).roundToInt(),
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
      layoutParams = LayoutParams(dp(24), dp(24)).apply {
        if (trailingColumn.orientation == HORIZONTAL && index > 0) marginStart = dp(8)
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
    // OneKey patch: preserve the original 38dp press target around its 24dp layout slot.
    icon.setTag(com.facebook.react.R.id.react_test_id, data.optString("testID").takeIf { it.isNotEmpty() })
    icon.accessibilityDelegate = selectorAccessibilityDelegate
    icon.contentDescription = data.optString("accessibilityLabel").takeIf { it.isNotEmpty() }
    if (item.json.optString("presentation") == "accountSelector") {
      icon.glyphSizeDp = 24
      val isSourceMenu = item.json.has("height") && icon.iconName == "DotHorOutline"
      val size = if (isSourceMenu) 24 else if (item.json.has("height") && icon.iconName == "PlusSmallOutline") 36 else 38
      icon.layoutParams = LayoutParams(dp(size), dp(size)).apply {
        gravity = Gravity.CENTER_VERTICAL
        if (!isSourceMenu) {
          marginStart = -dp(7)
          marginEnd = -dp(7)
        }
      }
      if (isSourceMenu) {
        // OneKey patch: the native ActionList trigger measures 24dp; Yoga rounds its trailing edge cumulatively.
        setPadding(paddingLeft, paddingTop, (12 * resources.displayMetrics.density).toInt(), paddingBottom)
      }
    } else if (icon.iconName == "ChevronRightSmallOutline") {
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
      icon.setOnClickListener {
        emitAction(item, actionKey, null, icon, "trailingAccessory", index)
      }
    }
  }

  private fun applySize(item: NativeListItem) {
    if (item.type == "market") {
      val style = item.json.optJSONObject("style")
      val imageHeight = style?.optJSONObject("image")?.optDouble(
        "height",
        if (item.json.optString("variant") == "stock") 40.0 else 32.0,
      ) ?: if (item.json.optString("variant") == "stock") 40.0 else 32.0
      val verticalPadding = style?.optDouble("verticalPadding", 12.0) ?: 12.0
      val defaultHeight = if (item.json.optString("variant") == "stock") 72.0 else 68.0
      minimumHeight = dp(
        item.json.optDouble(
          "height",
          maxOf(defaultHeight, imageHeight + verticalPadding * 2),
        ).roundToInt(),
      )
      return
    }
    val isWalletSidebar =
      item.type == "identity" && item.json.optString("presentation") == "walletSidebar"
    val isAccountSelectorIdentity =
      item.type == "identity" && item.json.optString("presentation") == "accountSelector"
    val isNetworkSelectorIdentity =
      item.type == "identity" && item.json.optString("presentation") == "networkSelector"
    val isAccountSelectorAction =
      item.type == "action" && item.json.optString("presentation") == "accountSelector"
    val isNetworkSelectorSection =
      item.type == "sectionHeader" && item.json.optString("presentation") == "networkSelector"
    title.textSize = sp(
      if (isWalletSidebar) {
        12f
      } else {
        when (item.type) {
          "message" -> 14f
          "sectionHeader" -> when {
            isNetworkSelectorSection && item.json.optString("variant") != "summary" -> 14f
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
    title.typeface = if (isAccountSelectorAction && item.json.has("icon")) {
      NativeListFonts.medium(context)
    } else if (isWalletSidebar || isAccountSelectorIdentity || isAccountSelectorAction) {
      NativeListFonts.regular(context)
    } else {
      when (item.type) {
        "sectionHeader" -> when {
          isNetworkSelectorSection && item.json.has("height") && item.json.optString("variant") != "summary" && (item.json.optJSONObject("checkbox") != null || item.json.optString("titleActionKey").isEmpty()) -> NativeListFonts.semibold(context)
          isNetworkSelectorSection -> NativeListFonts.medium(context)
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
    if (isNetworkSelectorSection && item.json.optString("variant") != "summary") {
      TextViewCompat.setLineHeight(title, dp(20))
    } else if (item.type == "sectionHeader" && item.json.optString("variant") == "gallery") {
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
    // OneKey patch: exact selector row dimensions override template minimums.
    selectorHeight = if (item.json.has("height")) dp(item.json.optInt("height")) else null
    // OneKey patch: React Native's section spacers and letter blocks truncate physical heights.
    val isSelectorLetter = isNetworkSelectorSection && item.json.has("height") &&
      item.json.optString("variant") != "summary" && item.json.optString("titleActionKey").isEmpty() &&
      item.json.optJSONObject("checkbox") == null
    val isSelectorSectionSpacer = selectorUsesSourceScale && currentLayout == "sectioned" &&
      item.type == "system" && item.json.optString("variant") == "spacer"
    if (isSelectorLetter || isSelectorSectionSpacer) {
      selectorHeight = (item.json.optInt("height") * resources.displayMetrics.density).toInt()
    }
    if (isSelectorLetter) {
      val inset = (20 * resources.displayMetrics.density).toInt() - dp(8)
      // OneKey patch: SectionHeader has a fixed height and centered text, without vertical padding.
      setPadding(inset, 0, inset, 0)
    }
    if (item.json.has("height") && isNetworkSelectorSection && item.json.optString("variant") != "summary" && item.json.optString("titleActionKey").isNotEmpty() && item.json.optJSONObject("checkbox") == null) {
      // OneKey patch: the text-and-underline header's fractional measured height rounds up in React Native.
      selectorHeight = kotlin.math.ceil((item.json.optInt("height") * (if (selectorUsesSourceScale) 1f else NativeListScale.factor(resources)) * resources.displayMetrics.density).toDouble()).toInt()
    }
    // OneKey patch: an explicit per-row policy preserves each source list's measured heights.
    if (item.json.has("height")) {
      when (item.json.optString("heightRounding")) {
        "floor" -> selectorHeight = (item.json.optInt("height") * resources.displayMetrics.density).toInt()
        "nearest" -> selectorHeight = (item.json.optInt("height") * resources.displayMetrics.density).roundToInt()
      }
    }
    val baseHeight = when {
      item.json.has("height") -> item.json.optInt("height")
      item.type == "system" && item.json.optString("variant") == "spacer" -> item.json.optInt("height", 0)
      item.type == "walletGroup" -> {
        val childCount = item.json.optJSONArray("children")?.length() ?: 0
        // OneKey patch: include badge heights in the group layout.
        // (childCount + 1) * 68 + childCount * 12
        val members = listOf(item.json.getJSONObject("parent")) + (0 until childCount).map { item.json.getJSONArray("children").getJSONObject(it) }
        members.sumOf { it.optInt("height", if ((it.optJSONArray("badges")?.length() ?: 0) > 0) 92 else 68) } + childCount * 12 + if (members.first().has("height")) 2 else 0
      }
      isNetworkSelectorIdentity -> 47
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
          isNetworkSelectorSection -> 44
          currentLayout == "table" -> 28
          item.json.optString("variant") == "summary" -> 80
          item.json.optString("variant") == "gallery" -> 32
          item.json.optString("variant") == "history" || item.sectionKey?.startsWith("history-") == true -> 16
          item.sectionKey in setOf("linear-tokens", "action-tokens") -> 30
          item.json.optString("value").isNotEmpty() && item.json.optJSONObject("checkbox") != null -> 40
          else -> 36
        }
        "system" -> when (item.json.optString("variant")) {
          "loading" -> when (item.json.optString("loadingStyle")) {
            "skeleton" -> 56
            "spinner" -> 52
            else -> if (item.json.optString("presentation") == "market") 68 else 56
          }
          "noMatch", "retry" -> if (item.json.optString("presentation") == "market") 44 else if (item.json.optString("variant") == "noMatch") 36 else 44
          "warning" -> 0
          "end" -> if (item.json.optString("presentation") == "market") 44 else 36
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
          item.type == "identity" && item.json.optString("presentation") == "walletSidebar" -> if ((item.json.optJSONArray("badges")?.length() ?: 0) > 0) 92 else 68
          item.type == "identity" && item.json.optString("tertiary").isNotEmpty() -> 72
          item.type == "identity" && item.json.optString("subtitle").isNotEmpty() -> 60
          else -> 56
        }
      }
    }
    val modifier = if (item.json.has("height") || isNetworkSelectorIdentity) {
      0
    } else if (
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
        "rounded" -> {
          // OneKey patch: the account avatar has an 8-point radius at its 32-point size.
          // outline.setRoundRect(0, 0, view.width, view.height, dp(10).toFloat())
          val radius = if ((tag as? NativeListItem)?.json?.optString("presentation") == "accountSelector" && (tag as? NativeListItem)?.json?.has("height") == true) dp(8).toFloat() else dp(10).toFloat()
          outline.setRoundRect(0, 0, view.width, view.height, radius)
        }
        else -> outline.setOval(0, 0, view.width, view.height)
      }
    }
  }

  private fun roundedOutlineProvider(radiusDp: Float) = object : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
      outline.setRoundRect(0, 0, view.width, view.height, scaledDp(radiusDp))
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

  private fun JSONArray?.isValuePairMenu(): Boolean =
    this != null && length() == 2 &&
      optJSONObject(0)?.optString("kind") == "valuePair" &&
      optJSONObject(1)?.optString("kind") == "menu"

  private fun bindImage(
    source: JSONObject,
    imageView: OneKeyImageReusableView,
    token: String,
    slot: Int,
    variant: String,
    onLoad: (() -> Unit)? = null,
    onError: (() -> Unit)? = null,
    retryAttempt: Int = 0,
  ) {
    selectorImageRetries.remove(imageView)?.let(imageView::removeCallbacks)
    val expectedEpoch = bindingEpoch
    val retryLimit = source.optInt("retryTimes", 0).coerceAtLeast(0)
    val uri = source.optString("uri").trim().takeIf(String::isNotEmpty)
    imageView.configure(
      sourceUri = uri,
      sourceHeadersJson = source.optJSONObject("headers")?.toString(),
      variant = variant,
      contentFit = source.optString("contentFit", "cover"),
      cachePolicy = source.optString("cachePolicy", "memory-disk"),
      autoplay = source.optBoolean("autoplay", false),
      recyclingKey = if (retryAttempt == 0) "$token:$slot" else "$token:$slot:retry:$retryAttempt",
      optimizeTos = retryAttempt == 0 && source.optBoolean("optimizeTos", true),
      overscan = source.optDouble("overscan", 1.1),
      loadingStrategy = source.optString("loadingStrategy", "static"),
      onLoad = if (retryLimit == 0) onLoad else ({
        if (bindingEpoch == expectedEpoch) {
          selectorImageRetries.remove(imageView)?.let(imageView::removeCallbacks)
          onLoad?.invoke()
        }
      }),
      onError = if (retryLimit == 0) onError else ({
        if (bindingEpoch == expectedEpoch) {
          if (retryAttempt >= retryLimit) onError?.invoke()
          else if (!selectorImageRetries.containsKey(imageView)) {
            val retry = Runnable {
              if (bindingEpoch == expectedEpoch) {
                selectorImageRetries.remove(imageView)
                bindImage(source, imageView, token, slot, variant, onLoad, onError, retryAttempt + 1)
              }
            }
            selectorImageRetries[imageView] = retry
            imageView.postDelayed(retry, kotlin.random.Random.nextLong(3) * 1000L)
          }
        }
      }),
    )
  }

  private fun weighted() = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
  private fun wrap() = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
  private fun dp(value: Int): Int = if (selectorUsesSourceScale) (value * resources.displayMetrics.density).roundToInt() else NativeListScale.dp(resources, value)
  private fun scaledDp(value: Float): Float = if (selectorUsesSourceScale) (value * resources.displayMetrics.density).roundToInt().toFloat() else NativeListScale.dp(resources, value)
  private fun sp(value: Float): Float = if (selectorUsesSourceScale) value else NativeListScale.font(resources, value)

  private fun color(theme: JSONObject?, key: String, fallback: String): Int =
    safeColor(theme?.optString(key, fallback), parseNativeListColor(fallback))

  private fun safeColor(value: String?, fallback: Int): Int = try {
    if (value.isNullOrEmpty()) fallback else parseNativeListColor(value)
  } catch (_: IllegalArgumentException) {
    fallback
  }

  private fun roundedFill(color: Int, radiusDp: Float) = GradientDrawable().apply {
    setColor(color)
    cornerRadius = scaledDp(radiusDp)
  }

  private fun roundedStroke(stroke: Int, fill: Int, radiusDp: Float) = GradientDrawable().apply {
    setColor(fill)
    setStroke(dp(2), stroke)
    cornerRadius = scaledDp(radiusDp)
  }

  private fun roundedHairlineStroke(stroke: Int, radiusDp: Float) = GradientDrawable().apply {
    setColor(Color.TRANSPARENT)
    setStroke(1, stroke)
    cornerRadius = scaledDp(radiusDp)
  }

  private fun groupedBackground(position: String, color: Int) = GradientDrawable().apply {
    setColor(color)
    val radius = scaledDp(12f)
    cornerRadii = when (position) {
      "first" -> floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f)
      "last" -> floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius)
      "single" -> FloatArray(8) { radius }
      "rail" -> FloatArray(8) { scaledDp(8f) }
      "mediaTile" -> FloatArray(8) { scaledDp(16f) }
      else -> FloatArray(8)
    }
  }

}

private class OneKeyIconView(context: android.content.Context) : View(context) {
  var useSourceScale = false
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
      glyphSizeDp?.let { if (useSourceScale) (it * resources.displayMetrics.density).roundToInt().toFloat() else NativeListScale.dp(resources, it).toFloat() } ?: Float.MAX_VALUE,
    )
    // OneKey patch: custom account-error artwork uses an 18-point viewBox.
    // val scale = drawSize / 24f
    val scale = drawSize / (selectorIconViewBoxes[iconName] ?: 24f)
    fill.color = tintColor
    canvas.save()
    canvas.translate((width - drawSize) / 2f, (height - drawSize) / 2f)
    canvas.scale(scale, scale)
    val sourceFillTypes = actionIconFillTypes[iconName]
    pathData.forEachIndexed { index, data ->
      PathParser.createPathFromPathData(data)?.let { path ->
        path.fillType = sourceFillTypes?.getOrNull(index) ?: Path.FillType.EVEN_ODD
        // OneKey patch: provider illustration colors are part of their source asset.
        fill.color = selectorIconColors[iconName]?.getOrNull(index) ?: tintColor
        canvas.drawPath(path, fill)
      }
    }
    canvas.restore()
  }

  companion object {
    // OneKey patch: preserve provider colors and non-24 viewBoxes.
    private val selectorIconColors: Map<String, List<Int?>> = mapOf(
      "GlobusOutline" to listOf(null),
      "LockSolid" to listOf(null),
      "GoogleIllus" to listOf(parseNativeListColor("#4285F4"), parseNativeListColor("#34A853"), parseNativeListColor("#FBBC05"), parseNativeListColor("#EA4335")),
      "AppleBrand" to listOf(null),
      "BotIllus" to listOf(parseNativeListColor("#8897A5"), parseNativeListColor("#3FA9F5"), parseNativeListColor("#8897A5"), parseNativeListColor("#8897A5"), parseNativeListColor("#10243E"), parseNativeListColor("#10243E"), parseNativeListColor("#10243E")),
      "AllNetworksSolid" to listOf(null, null),
      "CrossedSmallSolid" to listOf(null),
      "AccountErrorCustom" to listOf(Color.argb(0x72, 0, 0, 0), Color.argb(0x72, 0, 0, 0)),
      "Circle" to listOf(null),
    )
    private val selectorIconViewBoxes = mapOf(
      "GlobusOutline" to 24f,
      "LockSolid" to 24f,
      "GoogleIllus" to 24f,
      "AppleBrand" to 16f,
      "BotIllus" to 24f,
      "AllNetworksSolid" to 24f,
      "CrossedSmallSolid" to 24f,
      "AccountErrorCustom" to 18f,
      "Circle" to 24f,
    )
    // Keep the SVG fill-rule used by each Action-row source path. React Native
    // SVG defaults to nonzero (WINDING); only paths declaring fillRule="evenodd"
    // use EVEN_ODD. Other existing icons retain their prior rendering behavior.
    private val actionIconFillTypes = mapOf(
      "GlobusOutline" to listOf(Path.FillType.EVEN_ODD),
      "LockSolid" to listOf(Path.FillType.EVEN_ODD),
      "GoogleIllus" to listOf(Path.FillType.WINDING, Path.FillType.WINDING, Path.FillType.WINDING, Path.FillType.WINDING),
      "AppleBrand" to listOf(Path.FillType.WINDING),
      "BotIllus" to listOf(Path.FillType.WINDING, Path.FillType.WINDING, Path.FillType.WINDING, Path.FillType.WINDING, Path.FillType.WINDING, Path.FillType.WINDING, Path.FillType.WINDING),
      "AllNetworksSolid" to listOf(Path.FillType.WINDING, Path.FillType.EVEN_ODD),
      "CrossedSmallSolid" to listOf(Path.FillType.WINDING),
      "AccountErrorCustom" to listOf(Path.FillType.WINDING, Path.FillType.EVEN_ODD),
      "Circle" to listOf(Path.FillType.WINDING),
      "BadgeVerifiedSolid" to listOf(Path.FillType.EVEN_ODD),
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
      // OneKey patch: official selector SVG path geometry.
      "GlobusOutline" to listOf("M12 2c5.185 0 9.448 3.947 9.95 9H22v2h-.05c-.502 5.053-4.765 9-9.95 9s-9.448-3.947-9.95-9H2v-2h.05C2.552 5.947 6.815 2 12 2M9.523 13c.09 1.982.438 3.726.934 5.002.29.746.612 1.282.917 1.614.304.331.517.384.626.384s.322-.053.626-.384c.305-.332.627-.868.917-1.614.496-1.276.845-3.02.934-5.002zm-5.459 0a8 8 0 0 0 4.8 6.36 10 10 0 0 1-.271-.633C7.994 17.187 7.61 15.189 7.52 13zm12.416 0c-.09 2.189-.474 4.187-1.073 5.727a10 10 0 0 1-.271.633 8 8 0 0 0 4.8-6.36zM8.863 4.639A8 8 0 0 0 4.064 11h3.457c.09-2.189.473-4.187 1.072-5.727q.127-.327.27-.634M12 4c-.109 0-.322.053-.626.384-.305.332-.627.868-.917 1.614-.496 1.276-.844 3.02-.934 5.002h4.954c-.09-1.982-.438-3.726-.934-5.002-.29-.746-.612-1.282-.917-1.614C12.322 4.053 12.109 4 12 4m3.136.639q.144.307.271.634c.599 1.54.982 3.538 1.073 5.727h3.456a8 8 0 0 0-4.8-6.361"),
      "LockSolid" to listOf("M12 2a5 5 0 0 1 5 5v2h3v13H4V9h3V7a5 5 0 0 1 5-5m-1 11v5h2v-5zm1-9a3 3 0 0 0-3 3v2h6V7a3 3 0 0 0-3-3"),
      "GoogleIllus" to listOf("M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09", "M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23", "M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22z", "M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53"),
      "AppleBrand" to listOf("M11.67.834c.117 1.074-.315 2.153-.955 2.928-.64.773-1.692 1.378-2.718 1.298-.14-1.054.38-2.151.971-2.836C9.63 1.45 10.746.872 11.67.834M14.994 7.093c-.176.108-1.992 1.224-1.972 3.482.025 2.769 2.428 3.693 2.46 3.705l-.004.015a10.1 10.1 0 0 1-1.264 2.593c-.764 1.116-1.556 2.229-2.806 2.254-.598.011-1-.162-1.416-.343-.437-.19-.891-.386-1.609-.386-.751 0-1.226.203-1.683.398-.397.169-.78.333-1.32.354-1.208.047-2.124-1.207-2.895-2.32C.909 14.57-.294 10.414 1.322 7.612c.803-1.395 2.237-2.275 3.794-2.298.671-.014 1.32.244 1.89.47.434.172.821.326 1.135.326.282 0 .659-.149 1.099-.323.692-.273 1.539-.607 2.41-.518.599.026 2.276.24 3.354 1.818z"),
      "BotIllus" to listOf("M11 2a1 1 0 1 1 2 0v1.8l1.6 1.6a1 1 0 1 1-1.4 1.4L12 5.6l-1.2 1.2a1 1 0 0 1-1.4-1.4L11 3.8z", "M8.0 6.0h8.0a5.0 5.0 0 0 1 5.0 5.0v4.0a5.0 5.0 0 0 1 -5.0 5.0h-8.0a5.0 5.0 0 0 1 -5.0 -5.0v-4.0a5.0 5.0 0 0 1 5.0 -5.0z", "M3.0 10.0h0.0a1.5 1.5 0 0 1 1.5 1.5v3.0a1.5 1.5 0 0 1 -1.5 1.5h0.0a1.5 1.5 0 0 1 -1.5 -1.5v-3.0a1.5 1.5 0 0 1 1.5 -1.5z", "M21.0 10.0h0.0a1.5 1.5 0 0 1 1.5 1.5v3.0a1.5 1.5 0 0 1 -1.5 1.5h0.0a1.5 1.5 0 0 1 -1.5 -1.5v-3.0a1.5 1.5 0 0 1 1.5 -1.5z", "M7.5 12.0a1.5 1.5 0 1 0 3.0 0a1.5 1.5 0 1 0 -3.0 0", "M13.5 12.0a1.5 1.5 0 1 0 3.0 0a1.5 1.5 0 1 0 -3.0 0", "M8.5 15.4c.9.8 2.08 1.2 3.5 1.2s2.6-.4 3.5-1.2c.24-.2.6-.18.8.06.2.23.17.6-.06.8-1.14.98-2.58 1.46-4.24 1.46s-3.1-.48-4.24-1.46a.58.58 0 0 1-.06-.8c.2-.24.56-.26.8-.06"),
      "AllNetworksSolid" to listOf("M15.333 13.998a1.335 1.335 0 1 1 0 2.67 1.335 1.335 0 0 1 0-2.67", "M12 0c6.627 0 12 5.373 12 12s-5.373 12-12 12S0 18.627 0 12 5.373 0 12 0M8 12.668A2 2 0 0 0 6 14.666V16c0 1.103.895 1.997 1.998 1.998h1.334A2 2 0 0 0 11.33 16v-1.334a2 2 0 0 0-1.998-1.998zm7.333 0a2.665 2.665 0 1 0 0 5.33 2.665 2.665 0 0 0 0-5.33M7.999 6.001A2 2 0 0 0 6.001 8v1.334c0 1.103.895 1.998 1.998 1.998h1.334a2 2 0 0 0 1.998-1.998V7.999a2 2 0 0 0-1.998-1.998zm6.667 0A2 2 0 0 0 12.668 8v1.334c0 1.103.895 1.998 1.998 1.998H16a2 2 0 0 0 1.998-1.998V7.999A2 2 0 0 0 16 6.001z"),
      "CrossedSmallSolid" to listOf("M17.87 8.25 14.12 12l3.75 3.75-2.12 2.121-3.75-3.75-3.75 3.75-2.121-2.121L9.879 12l-3.75-3.75 2.12-2.121L12 9.879l3.75-3.75 2.122 2.121Z"),
      "AccountErrorCustom" to listOf("M12.5 12.75a1.25 1.25 0 1 0 0-2.5 1.25 1.25 0 0 0 0 2.5", "M0 3.5A3.5 3.5 0 0 1 3.5 0h8.088A2.41 2.41 0 0 1 14 2.412V5h1a3 3 0 0 1 3 3v7a3 3 0 0 1-3 3H4a4 4 0 0 1-4-4zm2 3.163V14a2 2 0 0 0 2 2h11a1 1 0 0 0 1-1V8a1 1 0 0 0-1-1H3.5c-.537 0-1.045-.12-1.5-.337M2 3.5A1.5 1.5 0 0 0 3.5 5H12V2.412A.41.41 0 0 0 11.588 2H3.5A1.5 1.5 0 0 0 2 3.5"),
      "Circle" to listOf("M0 12a12 12 0 1 0 24 0a12 12 0 1 0 -24 0"),
      "BadgeVerifiedSolid" to listOf("M9.483 11.458v3.5h-1v-3.5z M10.467 2.698a2.03 2.03 0 0 1 3.065 0l1.358 1.564a.03.03 0 0 0 .028.01l2.046-.325a2.03 2.03 0 0 1 2.347 1.971l.037 2.07q0 .016.014.026l1.776 1.066a2.03 2.03 0 0 1 .532 3.019l-1.304 1.609a.03.03 0 0 0-.005.03l.675 1.956a2.03 2.03 0 0 1-1.533 2.656l-2.033.394a.03.03 0 0 0-.023.019l-.741 1.933a2.03 2.03 0 0 1-2.88 1.05l-1.811-1.006a.03.03 0 0 0-.03 0l-1.811 1.005a2.03 2.03 0 0 1-2.88-1.049l-.742-1.933a.03.03 0 0 0-.023-.019l-2.033-.394a2.03 2.03 0 0 1-1.532-2.656l.675-1.957a.03.03 0 0 0-.005-.029l-1.304-1.61a2.03 2.03 0 0 1 .532-3.018l1.776-1.066a.03.03 0 0 0 .014-.026l.035-2.07a2.03 2.03 0 0 1 2.349-1.97l2.045.324a.03.03 0 0 0 .028-.01zm1.516 3.76a.5.5 0 0 0-.447.276l-1.861 3.724H8.483a1 1 0 0 0-1 1v3.5a1 1 0 0 0 1 1h6.692a2 2 0 0 0 1.981-1.73l.341-2.5a2 2 0 0 0-1.982-2.27h-1.939l.197-1.269a1.5 1.5 0 0 0-1.481-1.731z"),
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
  var usesSelectorGeometry = false
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
    // OneKey patch: the source icon is a 16dp child after the 2dp border, not 80% of a rounded frame.
    val drawSize = if (usesSelectorGeometry) (16 * resources.displayMetrics.density).roundToInt().toFloat() else minOf(width, height) * 0.8f
    val borderOffset = (2 * resources.displayMetrics.density).roundToInt().toFloat()
    canvas.save()
    canvas.translate(if (usesSelectorGeometry) borderOffset else (width - drawSize) / 2f, if (usesSelectorGeometry) borderOffset else (height - drawSize) / 2f)
    canvas.scale(drawSize / 16f, drawSize / 16f)
    PathParser.createPathFromPathData(pathData)?.let { canvas.drawPath(it, glyphPaint) }
    canvas.restore()
  }
}

internal class NativeListViewHolder(val rowView: NativeListRowView) :
  androidx.recyclerview.widget.RecyclerView.ViewHolder(rowView)
