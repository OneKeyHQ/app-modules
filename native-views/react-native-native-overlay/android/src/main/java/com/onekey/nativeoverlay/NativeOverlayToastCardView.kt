package com.onekey.nativeoverlay

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.LineHeightSpan
import android.text.style.MetricAffectingSpan
import android.util.Base64
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.accessibility.AccessibilityNodeInfo
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.facebook.react.common.assets.ReactFontManager
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// RareUI-inspired concentric geometry in dp: cardRadius = tileRadius (11) + padding (10).
private object ToastMetrics {
  const val CARD_PADDING_HORIZONTAL = 10f
  const val CARD_PADDING_VERTICAL = 10f
  const val CARD_RADIUS = 21f
  const val CARD_ELEVATION = 4f
  const val MESSAGE_ICON_GAP = 12f
  const val MESSAGE_TEXT_GAP = 2f
  const val NO_ICON_PADDING_START = 6f
  const val TILE_MIN_HEIGHT = 36f
  const val INLINE_ACTION_HEIGHT = 36f
  const val INLINE_ACTION_RADIUS = 11f
  const val INLINE_TEXT_MIN_WIDTH = 96f
  const val INLINE_ACTION_MAX_LABEL = 16
  const val INLINE_ACTION_MAX_MESSAGE = 72
  const val NOTIFICATION_GAP = 10f
  const val NOTIFICATION_TEXT_GAP = 2f
  const val ACTIONS_GAP = 8f
  const val ACTIONS_PADDING_TOP = 10f
  const val ACTIONS_PADDING_END = 2f
  const val ACTIONS_PADDING_BOTTOM = 2f
  const val CONTENT_SWAP_MS = 160L
  const val BODY_FADE_MS = 240L
}

private const val IMAGE_POOL_THREADS = 4
private const val IMAGE_TIMEOUT_MS = 5_000

private val imageExecutor = Executors.newFixedThreadPool(IMAGE_POOL_THREADS) { runnable ->
  Thread(runnable, "onekey-toast-image").apply { isDaemon = true }
}
private val mainHandler = Handler(Looper.getMainLooper())

private fun fetchBitmap(
  uri: String,
  cancelled: AtomicBoolean,
  connectionRef: AtomicReference<URLConnection?>,
  targetRef: WeakReference<ImageView>,
) {
  if (cancelled.get() || Thread.currentThread().isInterrupted) return
  val bitmap = runCatching {
    if (uri.startsWith("data:")) {
      val bytes = Base64.decode(uri.substringAfter(","), Base64.DEFAULT)
      BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } else {
      val connection = URL(uri).openConnection().apply {
        connectTimeout = IMAGE_TIMEOUT_MS
        readTimeout = IMAGE_TIMEOUT_MS
      }
      connectionRef.set(connection)
      try {
        if (cancelled.get() || Thread.currentThread().isInterrupted) {
          (connection as? HttpURLConnection)?.disconnect()
          null
        } else {
          connection.getInputStream().use(BitmapFactory::decodeStream)
        }
      } finally {
        connectionRef.compareAndSet(connection, null)
      }
    }
  }.getOrNull()
  if (!cancelled.get() && !Thread.currentThread().isInterrupted) {
    mainHandler.post {
      if (!cancelled.get()) {
        targetRef.get()?.setImageBitmap(bitmap)
      }
    }
  }
}

private fun Context.dp(value: Float): Int = (value * resources.displayMetrics.density).roundToInt()

/**
 * React Native draws text unhinted at subpixel positions and rounds a font
 * size up to whole pixels; the advances, and so the line breaks, follow.
 */
private fun TextView.matchReactNativeText(fontSize: Float) {
  paintFlags = paintFlags or Paint.SUBPIXEL_TEXT_FLAG or Paint.LINEAR_TEXT_FLAG
  setTextSize(TypedValue.COMPLEX_UNIT_PX, ceil(fontSize * resources.displayMetrics.density))
  includeFontPadding = false
  hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
  breakStrategy = Layout.BREAK_STRATEGY_HIGH_QUALITY
}

private fun exactly(size: Int) = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)

/** Gives every line the same height, with the glyphs centred in it. */
private class FixedLineHeightSpan(private val height: Int) : LineHeightSpan {
  override fun chooseHeight(
    text: CharSequence?,
    start: Int,
    end: Int,
    spanstartv: Int,
    lineHeight: Int,
    fm: Paint.FontMetricsInt,
  ) {
    val leading = height - (fm.descent - fm.ascent)
    fm.ascent -= ceil(leading / 2f).toInt()
    fm.descent += floor(leading / 2f).toInt()
    // Interior line boxes keep their own top/bottom so tall glyphs may overlap.
    if (start == 0) fm.top = fm.ascent
    if (end == text?.length) fm.bottom = fm.descent
  }
}

private class TypefaceSpan(private val typeface: Typeface) : MetricAffectingSpan() {
  override fun updateDrawState(paint: TextPaint) {
    paint.typeface = typeface
  }

  override fun updateMeasureState(paint: TextPaint) {
    paint.typeface = typeface
  }
}

internal object ToastFont {
  /**
   * The face for a weight: a per-weight font name when the typeface ships one
   * (resolved from `assets/fonts`), the family at that weight otherwise.
   */
  fun make(context: Context, family: String?, faces: Map<String, String>, weight: String): Typeface {
    val assets = context.assets
    faces[weight]?.let { name ->
      return ReactFontManager.getInstance().getTypeface(name, Typeface.NORMAL, assets)
    }
    val numeric = weight.toIntOrNull() ?: 400
    if (!family.isNullOrEmpty()) {
      return ReactFontManager.getInstance().getTypeface(family, numeric, false, assets)
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      Typeface.create(Typeface.DEFAULT, numeric, false)
    } else {
      Typeface.create(Typeface.DEFAULT, if (numeric >= 600) Typeface.BOLD else Typeface.NORMAL)
    }
  }
}

/**
 * Named icons come from the image package's process-wide registry. It is
 * reached reflectively so this module needs no dependency on it; without the
 * registry the icon stays blank. The image package's consumer R8 rules keep
 * `OneKeyNamedSVG.INSTANCE`, `request` and `cancel` unrenamed for this lookup;
 * its own startup provider sets the cache directory.
 */
private object ToastNamedIcon {
  private val registry: Any? by lazy {
    runCatching {
      Class.forName("com.margelo.nitro.onekeyimage.OneKeyNamedSVG").getField("INSTANCE").get(null)
    }.getOrNull()
  }
  private val request by lazy {
    registry?.javaClass?.methods?.firstOrNull { it.name == "request" && it.parameterTypes.size == 8 }
  }
  private val cancel by lazy {
    registry?.javaClass?.methods?.firstOrNull { it.name == "cancel" && it.parameterTypes.size == 1 }
  }

  fun request(
    owner: Any,
    context: Context,
    name: String,
    color: String,
    width: Int,
    height: Int,
    scale: Float,
    complete: (Bitmap?) -> Unit,
  ) {
    val target = registry
    val method = request
    if (target == null || method == null) {
      complete(null)
      return
    }
    val appContext = context.applicationContext ?: context
    runCatching {
      val callback: (Bitmap?) -> Unit = { bitmap -> mainHandler.post { complete(bitmap) } }
      method.invoke(target, owner, appContext.assets as AssetManager, name, color, width, height, scale, callback)
    }.onFailure { complete(null) }
  }

  fun cancel(owner: Any) {
    val target = registry ?: return
    runCatching { cancel?.invoke(target, owner) }
  }
}

@SuppressLint("ViewConstructor")
internal class ToastIconView(context: Context, icon: ToastIcon) : FrameLayout(context) {
  var icon = icon
    private set
  val statusTile = (icon as? ToastIcon.Status)?.let { ToastStatusTileView(context, it) }
  private val owner = Any()
  private val imageView = ImageView(context)
  /** The size the named icon was last drawn at; it renders at its frame. */
  private var requestedSize: Pair<Int, Int>? = null
  private var imageFuture: Future<*>? = null
  private var cancelled = AtomicBoolean(false)
  private val activeConnection = AtomicReference<URLConnection?>(null)

  init {
    when (icon) {
      is ToastIcon.Spinner -> {
        val spinner = ProgressBar(context)
        spinner.isIndeterminate = true
        spinner.indeterminateTintList = ColorStateList.valueOf(icon.color)
        addView(spinner, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
      }
      is ToastIcon.Status -> {
        addView(statusTile, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
      }
      is ToastIcon.Image -> {
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        if (icon.radius > 0) {
          clipToOutline = true
          background = GradientDrawable().apply {
            setColor(Color.TRANSPARENT)
            cornerRadius = context.dp(icon.radius).toFloat()
          }
        }
        addView(imageView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        load(icon.uri)
      }
      is ToastIcon.Named -> {
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        addView(imageView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
      }
    }
  }

  fun applyStatus(status: ToastIcon.Status, settle: Boolean) {
    icon = status
    statusTile?.apply(status, settle)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    val currentIcon = icon
    if (currentIcon is ToastIcon.Image && cancelled.get() && imageView.drawable == null) {
      load(currentIcon.uri)
    }
    requestIcon()
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    requestIcon()
  }

  private fun requestIcon() {
    val named = icon as? ToastIcon.Named ?: return
    if (!isAttachedToWindow || width <= 0 || height <= 0) return
    val size = width to height
    if (requestedSize == size) return
    requestedSize = size
    ToastNamedIcon.cancel(owner)
    ToastNamedIcon.request(
      owner, context, named.name, named.colorString, width, height, resources.displayMetrics.density,
    ) { bitmap -> imageView.setImageBitmap(bitmap) }
  }

  fun cancelLoad() {
    ToastNamedIcon.cancel(owner)
    requestedSize = null
    cancelled.set(true)
    imageFuture?.cancel(true)
    imageFuture = null
    (activeConnection.getAndSet(null) as? HttpURLConnection)?.let { conn ->
      Thread { runCatching { conn.disconnect() } }.apply { isDaemon = true }.start()
    }
  }

  override fun onDetachedFromWindow() {
    cancelLoad()
    super.onDetachedFromWindow()
  }

  private fun load(uri: String) {
    cancelLoad()
    val token = AtomicBoolean(false)
    cancelled = token
    val targetRef = WeakReference(imageView)
    val connectionRef = activeConnection
    imageFuture = imageExecutor.submit {
      fetchBitmap(uri, token, connectionRef, targetRef)
    }
  }
}

/** Marks a run React Native would underline; [ToastTextView] draws it. */
private class ToastUnderline

/**
 * Draws underlines the way React Native's text does: a stroke of the font's
 * underline thickness at baseline + thickness + 1px, in the run's colour.
 * The platform underline sits elsewhere and is drawn thinner.
 */
private class ToastTextView(context: Context) : TextView(context) {
  private val decorationPaint = Paint()

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val layout = layout ?: return
    val spanned = layout.text as? Spanned ?: return
    val marks = spanned.getSpans(0, spanned.length, ToastUnderline::class.java)
    if (marks.isEmpty()) return
    val paint = layout.paint
    val thickness = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      paint.underlineThickness
    } else {
      paint.fontMetrics.descent * 0.1f
    }
    canvas.save()
    canvas.translate(totalPaddingLeft.toFloat(), totalPaddingTop.toFloat())
    for (mark in marks) {
      val start = spanned.getSpanStart(mark)
      val end = spanned.getSpanEnd(mark)
      val foreground = spanned.getSpans(start, start + 1, ForegroundColorSpan::class.java)
      decorationPaint.set(paint)
      decorationPaint.color = foreground.lastOrNull()?.foregroundColor ?: currentTextColor
      decorationPaint.strokeWidth = thickness
      decorationPaint.style = Paint.Style.STROKE
      decorationPaint.isAntiAlias = true
      val startLine = layout.getLineForOffset(start)
      val endLine = layout.getLineForOffset(end)
      for (line in startLine..endLine) {
        val y = layout.getLineBaseline(line) + thickness + 1f
        val x1 = if (line == startLine) layout.getPrimaryHorizontal(start) else layout.getLineLeft(line)
        val x2 = if (line == endLine) layout.getPrimaryHorizontal(end) else layout.getLineRight(line)
        canvas.drawLine(min(x1, x2), y, max(x1, x2), y, decorationPaint)
      }
    }
    canvas.restore()
  }
}

/**
 * The background React Native gives a rounded view with a border: the fill
 * is inset by 0.8px so the border can overlap it.
 */
private class ToastPillDrawable(private val inset: Float) : Drawable() {
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
  private val rect = RectF()
  var radius = 0f
    set(value) {
      field = value
      invalidateSelf()
    }

  fun setColor(color: Int) {
    paint.color = color
    invalidateSelf()
  }

  override fun draw(canvas: Canvas) {
    rect.set(bounds)
    rect.inset(inset, inset)
    canvas.drawRoundRect(rect, radius, radius, paint)
  }

  override fun setAlpha(alpha: Int) = Unit

  override fun setColorFilter(colorFilter: ColorFilter?) = Unit

  @Deprecated("Deprecated in Java")
  override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}

private fun makeParagraph(
  context: Context,
  text: ToastText,
  runs: List<ToastTextRun>,
  onLink: (String) -> Unit,
): TextView {
  val density = context.resources.displayMetrics.density
  val view = ToastTextView(context)
  view.matchReactNativeText(text.fontSize)
  view.setTextColor(text.color)
  view.typeface = ToastFont.make(context, text.fontFamily, text.fontFaces, text.fontWeight)
  if (text.letterSpacing != 0f) {
    view.letterSpacing = text.letterSpacing / text.fontSize
  }
  if (text.tabularNumbers) {
    view.fontFeatureSettings = "'tnum'"
  }
  if (text.numberOfLines > 0) {
    view.maxLines = text.numberOfLines
    view.ellipsize = TextUtils.TruncateAt.END
  }
  val builder = SpannableStringBuilder()
  var hasLink = false
  for (run in runs) {
    val start = builder.length
    builder.append(run.text)
    val end = builder.length
    if (start == end) continue
    val flags = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    run.link?.let { link ->
      hasLink = true
      builder.setSpan(
        object : ClickableSpan() {
          override fun onClick(widget: View) = onLink(link)

          // The run keeps its own colour and underline.
          override fun updateDrawState(paint: TextPaint) = Unit
        },
        start, end, flags,
      )
    }
    run.color?.let { builder.setSpan(ForegroundColorSpan(it), start, end, flags) }
    run.fontWeight?.let {
      builder.setSpan(
        TypefaceSpan(ToastFont.make(context, text.fontFamily, text.fontFaces, it)), start, end, flags,
      )
    }
    run.fontSize?.let {
      builder.setSpan(AbsoluteSizeSpan(ceil(it * density).toInt()), start, end, flags)
    }
    if (run.underline) builder.setSpan(ToastUnderline(), start, end, flags)
  }
  if (builder.isNotEmpty()) {
    builder.setSpan(
      // React Native rounds a line height up to whole pixels.
      FixedLineHeightSpan(ceil(text.lineHeight * density).toInt()),
      0, builder.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE,
    )
  }
  view.text = builder
  if (hasLink) {
    view.movementMethod = LinkMovementMethod.getInstance()
    view.highlightColor = Color.TRANSPARENT
  }
  return view
}

/**
 * The pixel-grid rounding of the layout engine the replaced cards were laid
 * out with (Yoga): a node's offset from its parent is rounded half up, text
 * offsets are floored, and a size is the difference of the rounded absolute
 * edges. Matching it keeps every edge on the same pixel as before.
 */
private object PixelGrid {
  private fun same(a: Double, b: Double) = abs(a - b) < 0.0001

  fun snap(value: Double, forceCeil: Boolean = false, forceFloor: Boolean = false): Double {
    var fraction = value % 1.0
    if (fraction < 0) fraction += 1.0
    return when {
      same(fraction, 0.0) -> value - fraction
      same(fraction, 1.0) -> value - fraction + 1.0
      forceCeil -> value - fraction + 1.0
      forceFloor -> value - fraction
      else -> value - fraction + if (fraction > 0.5 || same(fraction, 0.5)) 1.0 else 0.0
    }
  }

  fun isFractional(value: Double): Boolean {
    val fraction = value % 1.0
    return !same(fraction, 0.0) && !same(fraction, 1.0)
  }
}

/**
 * A node of the replaced layout: its unrounded absolute origin and its drawn
 * origin relative to the card, both in px.
 */
private class Node(val absX: Double, val absY: Double, val x: Int, val y: Int) {
  fun child(relX: Double, relY: Double, text: Boolean = false) = Node(
    absX + relX,
    absY + relY,
    x + PixelGrid.snap(relX, forceFloor = text).toInt(),
    y + PixelGrid.snap(relY, forceFloor = text).toInt(),
  )

  fun width(size: Double, text: Boolean = false): Int = extent(absX, size, text)

  fun height(size: Double, text: Boolean = false): Int = extent(absY, size, text)

  fun frame(width: Double, height: Double, text: Boolean = false) =
    Rect(x, y, x + width(width, text), y + height(height, text))

  private fun extent(origin: Double, size: Double, text: Boolean): Int {
    val fractional = PixelGrid.isFractional(size)
    val end = PixelGrid.snap(origin + size, forceCeil = text && fractional, forceFloor = text && !fractional)
    return (end - PixelGrid.snap(origin, forceFloor = text)).toInt()
  }
}

private fun View.layoutAt(frame: Rect, originX: Int = 0, originY: Int = 0) {
  measure(exactly(frame.width()), exactly(frame.height()))
  layout(frame.left - originX, frame.top - originY, frame.right - originX, frame.bottom - originY)
}

@SuppressLint("ViewConstructor")
private class ToastActionButton(context: Context, val action: ToastAction) : ViewGroup(context) {
  private val surface = ToastPillDrawable(if (action.borderWidth > 0) 0.8f else 0f)
  val label = TextView(context)
  val iconView = action.icon?.let { ToastIconView(context, it) }
  var isInline = false
  /** Child frames in the card's coordinates; the card lays the button out. */
  var frame = Rect()
  var labelFrame = Rect()
  var iconFrame = Rect()

  init {
    surface.setColor(action.backgroundColor)
    background = surface
    isClickable = !action.disabled
    isEnabled = !action.disabled
    alpha = if (action.disabled) action.disabledOpacity else 1f
    contentDescription = action.label
    action.testID?.let { tag = it }
    val density = resources.displayMetrics.density
    label.matchReactNativeText(action.fontSize)
    label.setTextColor(action.color)
    label.typeface = ToastFont.make(context, action.fontFamily, action.fontFaces, action.fontWeight)
    label.text = SpannableStringBuilder(action.label).apply {
      setSpan(
        FixedLineHeightSpan(ceil(action.lineHeight * density).toInt()),
        0, length, Spanned.SPAN_INCLUSIVE_INCLUSIVE,
      )
    }
    label.maxLines = 1
    iconView?.let(::addView)
    addView(label)
  }

  /** The label's natural width and height in px. */
  fun measureLabel(): Pair<Int, Int> {
    label.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
    return label.measuredWidth to label.measuredHeight
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (isEnabled) {
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
          surface.setColor(action.pressedBackgroundColor ?: action.backgroundColor)
          animate().scaleX(0.95f).scaleY(0.95f).setDuration(120L).start()
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
          surface.setColor(action.backgroundColor)
          animate().scaleX(1f).scaleY(1f).setDuration(120L).start()
        }
      }
    }
    return super.onTouchEvent(event)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    setMeasuredDimension(frame.width(), frame.height())
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    val radiusDp = if (isInline) ToastMetrics.INLINE_ACTION_RADIUS else action.borderRadius
    surface.radius = minOf(radiusDp * resources.displayMetrics.density, (bottom - top) / 2f)
    label.layoutAt(labelFrame, frame.left, frame.top)
    iconView?.layoutAt(iconFrame, frame.left, frame.top)
  }
}

/** One toast card, drawn with Android views from a spec. */
@SuppressLint("ViewConstructor")
internal class NativeOverlayToastCardView(context: Context) : ViewGroup(context) {
  var spec: ToastSpec? = null
    private set
  var onAction: ((String) -> Unit)? = null
  var onLink: ((String) -> Unit)? = null
  var onPress: (() -> Unit)? = null
  var canExpandStack = false
    set(value) {
      if (field == value) return
      field = value
      updateAccessibilityImportance()
    }
  /** Width of the content box in dp; the card adds its padding around it. */
  var contentWidth = 0f
  /** Tallest the content box grows, in dp; zero leaves it unbounded. */
  var contentMaxHeight = 0f
  /** The card's unrounded position in the window, in px; rounding follows it. */
  var originX = 0.0
  var originY = 0.0

  private val surface = GradientDrawable()
  private val contentSwapInterpolator = PathInterpolator(0.2f, 0f, 0f, 1f)
  private val bodyFadeInterpolator = PathInterpolator(0f, 0f, 0.58f, 1f)
  private var iconView: ToastIconView? = null
  private var badge: View? = null
  private var imageView: ToastIconView? = null
  private var titles: List<TextView> = emptyList()
  private var messages: List<TextView> = emptyList()
  private var buttons: List<ToastActionButton> = emptyList()
  private val frames = HashMap<View, Rect>()
  private var size = Rect()
  private var bodyHidden = false
  private var displayHeightPx = -1
  private var targetDisplayHeightPx = -1
  private var heightAnimator: ValueAnimator? = null

  val naturalHeightPx: Int get() = size.height()
  val currentDisplayHeightPx: Int
    get() = when {
      displayHeightPx > 0 -> displayHeightPx
      size.height() > 0 -> size.height()
      else -> measuredHeight
    }

  init {
    setWillNotDraw(false)
    surface.cornerRadius = context.dp(ToastMetrics.CARD_RADIUS).toFloat()
    elevation = context.dp(ToastMetrics.CARD_ELEVATION).toFloat()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      outlineAmbientShadowColor = Color.argb(46, 0, 0, 0)
      outlineSpotShadowColor = Color.argb(56, 0, 0, 0)
    }
    outlineProvider = object : ViewOutlineProvider() {
      override fun getOutline(view: View, outline: Outline) {
        val h = if (displayHeightPx > 0) displayHeightPx else view.height
        outline.setRoundRect(0, 0, view.width, max(h, 0), context.dp(ToastMetrics.CARD_RADIUS).toFloat())
      }
    }
    clipToOutline = true
    clipChildren = true
    clipToPadding = true
  }

  override fun onDraw(canvas: Canvas) {
    val h = if (displayHeightPx > 0) displayHeightPx else height
    surface.setBounds(0, 0, width, max(h, 0))
    surface.draw(canvas)
  }

  override fun onDetachedFromWindow() {
    heightAnimator?.cancel()
    heightAnimator = null
    super.onDetachedFromWindow()
  }

  fun cancelLoads() {
    heightAnimator?.cancel()
    heightAnimator = null
    iconView?.cancelLoad()
    imageView?.cancelLoad()
    buttons.forEach { it.iconView?.cancelLoad() }
  }

  private fun childRestAlpha(view: View): Float =
    if (view is ToastActionButton && view.action.disabled) view.action.disabledOpacity else 1f

  fun setBodyHidden(hidden: Boolean, animated: Boolean) {
    if (hidden == bodyHidden) return
    bodyHidden = hidden
    for (index in 0 until childCount) {
      val child = getChildAt(index)
      val target = if (hidden) 0f else childRestAlpha(child)
      if (animated) {
        child.animate().alpha(target).setDuration(ToastMetrics.BODY_FADE_MS)
          .setInterpolator(bodyFadeInterpolator).start()
      } else {
        child.animate().cancel()
        child.alpha = target
      }
    }
  }

  fun setDisplayHeight(targetPx: Int, animated: Boolean, durationMs: Long, interpolator: TimeInterpolator) {
    val clamped = max(targetPx, 0)
    val natural = size.height()
    if (clamped == 0 || natural == 0) return
    val isNatural = clamped == natural
    val current = if (displayHeightPx > 0) displayHeightPx else natural
    if (!animated || current == clamped) {
      heightAnimator?.cancel()
      heightAnimator = null
      displayHeightPx = if (isNatural) -1 else clamped
      targetDisplayHeightPx = if (isNatural) -1 else clamped
      if (measuredHeight < clamped || (isNatural && measuredHeight != natural)) {
        requestLayout()
      }
      invalidateOutline()
      invalidate()
      return
    }
    if (targetDisplayHeightPx == clamped && heightAnimator?.isRunning == true) return
    heightAnimator?.cancel()
    targetDisplayHeightPx = clamped
    if (measuredHeight < max(current, clamped)) {
      requestLayout()
    }
    var cancelled = false
    heightAnimator = ValueAnimator.ofInt(current, clamped).apply {
      duration = durationMs
      this.interpolator = interpolator
      addUpdateListener { anim ->
        displayHeightPx = anim.animatedValue as Int
        invalidateOutline()
        invalidate()
      }
      addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationCancel(animation: Animator) {
          cancelled = true
        }

        override fun onAnimationEnd(animation: Animator) {
          if (!cancelled && isNatural) {
            displayHeightPx = -1
            targetDisplayHeightPx = -1
            if (measuredHeight != size.height()) {
              requestLayout()
            }
            invalidateOutline()
            invalidate()
          }
        }
      })
      start()
    }
  }

  fun apply(spec: ToastSpec, cardColor: Int, cardBorderColor: Int? = null, settleStatus: Boolean = false) {
    val nextStatus = spec.icon as? ToastIcon.Status
    val retained = iconView?.takeIf {
      this.spec?.isNotification == false && !spec.isNotification &&
        nextStatus != null && it.statusTile != null
    }
    this.spec = spec
    surface.setColor(cardColor)
    surface.setStroke(max(1, context.dp(1f)), cardBorderColor ?: Color.argb(15, 0, 0, 0))
    spec.testID?.let { tag = it }
    isClickable = spec.pressable
    isFocusable = spec.pressable
    updateAccessibilityImportance()
    cancelLoads()
    for (index in childCount - 1 downTo 0) {
      if (getChildAt(index) !== retained) removeViewAt(index)
    }
    val link: (String) -> Unit = { onLink?.invoke(it) }
    badge = if (spec.isNotification) {
      View(context).also { view ->
        view.background = GradientDrawable().apply {
          shape = GradientDrawable.OVAL
          setColor(spec.badgeColor)
        }
        addView(view)
      }
    } else {
      null
    }
    iconView = if (retained != null && nextStatus != null) {
      retained.applyStatus(nextStatus, settleStatus)
      retained
    } else {
      spec.icon?.let { ToastIconView(context, it).also(::addView) }
    }
    imageView = spec.image?.let { ToastIconView(context, it).also(::addView) }
    titles = spec.title?.let { text ->
      text.paragraphs.map { makeParagraph(context, text, it, link).also(::addView) }
    }.orEmpty()
    messages = spec.message?.let { text ->
      text.paragraphs.map { makeParagraph(context, text, it, link).also(::addView) }
    }.orEmpty()
    buttons = spec.actions.map { action ->
      ToastActionButton(context, action).also { button ->
        button.setOnClickListener { onAction?.invoke(action.id) }
        addView(button)
      }
    }
    if (bodyHidden) {
      for (index in 0 until childCount) {
        getChildAt(index).alpha = 0f
      }
    } else if (settleStatus && retained != null) {
      val shiftPx = context.dp(3f).toFloat()
      val swapped: List<View> = titles + messages + buttons
      for (view in swapped) {
        view.alpha = 0f
        view.translationY = shiftPx
        view.animate()
          .alpha(childRestAlpha(view))
          .translationY(0f)
          .setDuration(ToastMetrics.CONTENT_SWAP_MS)
          .setInterpolator(contentSwapInterpolator)
          .start()
      }
    }
    requestLayout()
    invalidate()
  }

  fun animateMessageStatusEntrance() {
    if (spec?.isNotification == false) iconView?.statusTile?.enter()
  }

  fun animateMessageStatusRing() {
    if (spec?.isNotification == false) iconView?.statusTile?.startRing()
  }

  fun cancelStatusMotion() {
    iconView?.statusTile?.cancelMotion()
  }

  /** What TalkBack reads when the card appears. */
  val announcement: String
    get() = (titles + messages).map { it.text.toString() }.filter { it.isNotEmpty() }.joinToString(". ")

  private fun updateAccessibilityImportance() {
    importantForAccessibility = if (canExpandStack || spec?.pressable == true) {
      IMPORTANT_FOR_ACCESSIBILITY_YES
    } else {
      IMPORTANT_FOR_ACCESSIBILITY_AUTO
    }
  }

  override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
    super.onInitializeAccessibilityNodeInfo(info)
    if (canExpandStack || spec?.pressable == true) {
      info.isClickable = true
      val click = AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK
      info.addAction(if (canExpandStack) {
        AccessibilityNodeInfo.AccessibilityAction(click.id, "Expand notifications")
      } else click)
      if (info.contentDescription.isNullOrEmpty()) {
        info.contentDescription = announcement
      }
    }
  }

  override fun performAccessibilityAction(action: Int, arguments: Bundle?): Boolean {
    if (action == AccessibilityNodeInfo.ACTION_CLICK && (canExpandStack || spec?.pressable == true)) {
      onPress?.invoke()
      return true
    }
    return super.performAccessibilityAction(action, arguments)
  }

  override fun performClick(): Boolean {
    super.performClick()
    if (spec?.pressable == true) {
      onPress?.invoke()
      return true
    }
    return false
  }

  private val density: Double get() = resources.displayMetrics.density.toDouble()

  private fun px(dp: Float): Double = dp * density

  /** Lays text out at its frame's width; returns the height it takes, in px. */
  private fun textHeight(view: TextView, width: Int): Int {
    if (view.text.isEmpty()) return 0
    view.measure(exactly(width), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
    return view.measuredHeight
  }

  /**
   * Stacks text views in [parent] from [top] down; returns their total height.
   * Each view is a text node of its own.
   */
  private fun placeParagraphs(views: List<TextView>, parent: Node, width: Double, top: Double = 0.0): Double {
    var y = 0.0
    for (view in views) {
      val node = parent.child(0.0, top + y, text = true)
      val drawnWidth = node.width(width, text = true)
      val height = textHeight(view, drawnWidth).toDouble()
      frames[view] = node.frame(width, height, text = true)
      y += height
    }
    return y
  }

  /**
   * Places the action row with [row] as its origin; returns the row's height.
   * The row is as wide as [width] and keeps [paddingEnd] clear at its end.
   */
  private fun placeActions(row: Node, width: Double, paddingEnd: Float, paddingBottom: Float): Double {
    if (buttons.isEmpty()) return 0.0
    val gap = px(ToastMetrics.ACTIONS_GAP)
    val sizes = buttons.map { button ->
      val action = button.action
      val (labelWidth, labelHeight) = button.measureLabel()
      val iconSize = action.icon?.let { px(it.size) } ?: 0.0
      val iconExtent = if (action.icon != null) iconSize + px(action.iconGap) else 0.0
      val contentHeight = max(labelHeight.toDouble(), iconSize)
      val insetX = px(action.paddingHorizontal + action.borderWidth)
      val insetY = px(action.paddingVertical + action.borderWidth)
      doubleArrayOf(labelWidth + iconExtent + 2 * insetX, contentHeight + 2 * insetY)
    }
    val extents = buttons.mapIndexed { index, button ->
      sizes[index][0] + 2 * px(button.action.marginHorizontal)
    }
    val rowWidth = extents.sum() + gap * (buttons.size - 1)
    var cursor = if (spec?.actionsAlignLeft == true) 0.0 else width - px(paddingEnd) - rowWidth
    val paddingTop = px(ToastMetrics.ACTIONS_PADDING_TOP)
    var rowHeight = 0.0
    buttons.forEachIndexed { index, button ->
      val action = button.action
      val (buttonWidth, buttonHeight) = sizes[index].let { it[0] to it[1] }
      val marginX = px(action.marginHorizontal)
      val marginY = px(action.marginVertical)
      val node = row.child(cursor + marginX, paddingTop + marginY)
      button.frame = node.frame(buttonWidth, buttonHeight)
      frames[button] = button.frame
      val insetX = px(action.paddingHorizontal + action.borderWidth)
      val insetY = px(action.paddingVertical + action.borderWidth)
      val contentHeight = buttonHeight - 2 * insetY
      var labelX = insetX
      button.iconView?.let {
        val iconSize = px(action.icon!!.size)
        button.iconFrame = node.child(insetX, insetY + (contentHeight - iconSize) / 2).frame(iconSize, iconSize)
        labelX += iconSize + px(action.iconGap)
      }
      val labelHeight = button.label.measuredHeight.toDouble()
      button.labelFrame = node.child(labelX, insetY + (contentHeight - labelHeight) / 2, text = true)
        .frame(button.label.measuredWidth.toDouble(), labelHeight, text = true)
      rowHeight = max(rowHeight, buttonHeight + 2 * marginY)
      cursor += extents[index] + gap
    }
    return paddingTop + rowHeight + px(paddingBottom)
  }

  private fun shouldUseInlineAction(spec: ToastSpec): Boolean {
    if (buttons.size != 1 || spec.actionsAlignLeft) return false
    val action = buttons.first().action
    if (action.label.length > ToastMetrics.INLINE_ACTION_MAX_LABEL) return false
    if (action.marginHorizontal != 0f || action.marginVertical != 0f) return false
    val message = spec.message ?: return true
    if (message.paragraphs.size > 1) return false
    val length = message.paragraphs.sumOf { runs -> runs.sumOf { it.text.length } }
    return length <= ToastMetrics.INLINE_ACTION_MAX_MESSAGE
  }

  /** Lays the content box out from [content]; returns its height in px. */
  private fun placeContent(spec: ToastSpec, content: Node, width: Double): Double {
    buttons.forEach { it.isInline = false }
    if (spec.isNotification) {
      val badgeSize = px(spec.badgeSize)
      val badgeNode = content.child(0.0, 0.0)
      badge?.let { frames[it] = badgeNode.frame(badgeSize, badgeSize) }
      iconView?.let { view ->
        val size = px(view.icon.size)
        frames[view] = badgeNode.child((badgeSize - size) / 2, (badgeSize - size) / 2).frame(size, size)
      }
      val gap = px(ToastMetrics.NOTIFICATION_GAP)
      val imageSize = spec.image?.let { px(it.size) } ?: 0.0
      imageView?.let { frames[it] = content.child(width - imageSize, 0.0).frame(imageSize, imageSize) }
      val columnX = badgeSize + gap
      val columnWidth = max(width - columnX - (if (spec.image != null) imageSize + gap else 0.0), 0.0)
      val column = content.child(columnX, 0.0)
      val textGap = px(ToastMetrics.NOTIFICATION_TEXT_GAP)
      var y = 0.0
      (titles + messages).forEachIndexed { index, view ->
        if (index > 0) y += textGap
        y += placeParagraphs(listOf(view), column, columnWidth, top = y)
      }
      if (buttons.isNotEmpty()) {
        y += textGap
        y += placeActions(column.child(0.0, y), columnWidth, 0f, 0f)
      }
      return maxOf(y, badgeSize, imageSize)
    }
    val icon = spec.icon
    val iconSize = icon?.let { px(it.size) } ?: 0.0
    val columnX = icon?.let { iconSize + px(ToastMetrics.MESSAGE_ICON_GAP) } ?: px(ToastMetrics.NO_ICON_PADDING_START)
    val gap = px(ToastMetrics.MESSAGE_TEXT_GAP)
    val measureColumn = content.child(columnX, 0.0)
    val textBlocks = listOf(titles, messages).filter { it.isNotEmpty() }
    fun measureText(columnWidth: Double): Double {
      var height = 0.0
      textBlocks.forEachIndexed { index, block ->
        if (index > 0) height += gap
        for (view in block) {
          val node = measureColumn.child(0.0, height, text = true)
          height += textHeight(view, node.width(columnWidth, text = true))
        }
      }
      return height
    }
    var inlineAction = false
    var inlineWidth = 0.0
    var inlineHeight = 0.0
    if (shouldUseInlineAction(spec)) {
      val button = buttons.first()
      val action = button.action
      val (labelWidth, labelHeight) = button.measureLabel()
      val actionIconSize = action.icon?.let { px(it.size) } ?: 0.0
      val iconExtent = if (action.icon != null) actionIconSize + px(action.iconGap) else 0.0
      val insetX = px(max(action.paddingHorizontal, 14f) + action.borderWidth)
      val insetY = px(action.paddingVertical + action.borderWidth)
      inlineWidth = labelWidth + iconExtent + 2 * insetX
      val minTextWidth = if (textBlocks.isEmpty()) 0.0 else px(ToastMetrics.INLINE_TEXT_MIN_WIDTH)
      val inlineTextWidth = width - columnX - px(ToastMetrics.MESSAGE_ICON_GAP) - inlineWidth
      val fittedHeight = max(labelHeight.toDouble(), actionIconSize) + 2 * insetY
      val candidateHeight = max(fittedHeight, px(ToastMetrics.INLINE_ACTION_HEIGHT))
      inlineAction = inlineTextWidth >= minTextWidth
      if (!inlineAction && contentMaxHeight > 0f && inlineTextWidth > 0.0) {
        val heightCap = px(contentMaxHeight)
        val lowerButtonBottom = measureText(max(width - columnX, 0.0)) + gap +
          px(ToastMetrics.ACTIONS_PADDING_TOP) + fittedHeight
        if (lowerButtonBottom > heightCap) {
          val inlineTextHeight = measureText(inlineTextWidth)
          inlineAction = maxOf(inlineTextHeight, candidateHeight, iconSize, px(ToastMetrics.TILE_MIN_HEIGHT)) <= heightCap
        }
      }
      button.isInline = inlineAction
      if (inlineAction) inlineHeight = candidateHeight
    }
    val trailing = if (inlineAction) inlineWidth + px(ToastMetrics.MESSAGE_ICON_GAP) else 0.0
    val columnWidth = max(width - columnX - trailing, 0.0)
    var textHeight = measureText(columnWidth)
    var bottomActionsHeight = 0.0
    if (buttons.isNotEmpty() && !inlineAction) {
      if (textBlocks.isNotEmpty()) textHeight += gap
      bottomActionsHeight = placeActions(
        measureColumn.child(0.0, textHeight), columnWidth, ToastMetrics.ACTIONS_PADDING_END, ToastMetrics.ACTIONS_PADDING_BOTTOM,
      )
    }
    val rowHeight = maxOf(textHeight + bottomActionsHeight, iconSize, inlineHeight, px(ToastMetrics.TILE_MIN_HEIGHT))
    val textTop = if (bottomActionsHeight > 0.0) 0.0 else (rowHeight - textHeight) / 2.0
    val textColumn = content.child(columnX, textTop)
    var y = 0.0
    var placedBlock = false
    for (block in listOf(titles, messages)) {
      if (block.isEmpty()) continue
      if (placedBlock) y += gap
      placedBlock = true
      y += placeParagraphs(block, textColumn.child(0.0, y), columnWidth)
    }
    iconView?.let { view ->
      frames[view] = content.child(0.0, (rowHeight - iconSize) / 2.0).frame(iconSize, iconSize)
    }
    if (inlineAction) {
      val button = buttons.first()
      val action = button.action
      val node = content.child(width - inlineWidth, (rowHeight - inlineHeight) / 2.0)
      button.frame = node.frame(inlineWidth, inlineHeight)
      frames[button] = button.frame
      val insetX = px(max(action.paddingHorizontal, 14f) + action.borderWidth)
      var labelX = insetX
      button.iconView?.let {
        val actionIconSize = px(action.icon!!.size)
        button.iconFrame = node.child(insetX, (inlineHeight - actionIconSize) / 2.0).frame(actionIconSize, actionIconSize)
        labelX += actionIconSize + px(action.iconGap)
      }
      val labelHeight = button.label.measuredHeight.toDouble()
      button.labelFrame = node.child(labelX, (inlineHeight - labelHeight) / 2.0, text = true)
        .frame(button.label.measuredWidth.toDouble(), labelHeight, text = true)
    }
    return rowHeight
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    frames.clear()
    val spec = spec
    val card = Node(originX, originY, 0, 0)
    val width = px(contentWidth)
    var height = 0.0
    if (spec != null) {
      val content = card.child(px(ToastMetrics.CARD_PADDING_HORIZONTAL), px(ToastMetrics.CARD_PADDING_VERTICAL))
      height = placeContent(spec, content, width)
      if (contentMaxHeight > 0) height = min(height, px(contentMaxHeight))
    }
    size = card.frame(
      width + 2 * px(ToastMetrics.CARD_PADDING_HORIZONTAL),
      height + 2 * px(ToastMetrics.CARD_PADDING_VERTICAL),
    )
    val measuredH = max(size.height(), max(displayHeightPx, targetDisplayHeightPx))
    setMeasuredDimension(size.width(), measuredH)
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    for ((view, frame) in frames) {
      if (view is ToastActionButton) {
        view.measure(exactly(frame.width()), exactly(frame.height()))
        view.layout(frame.left, frame.top, frame.right, frame.bottom)
      } else {
        view.layoutAt(frame)
      }
    }
  }
}
