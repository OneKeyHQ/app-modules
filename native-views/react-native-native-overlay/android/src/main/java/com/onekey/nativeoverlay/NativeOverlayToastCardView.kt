package com.onekey.nativeoverlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
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
import android.view.accessibility.AccessibilityNodeInfo
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

// The card geometry of the layouts this replaces, in dp.
private object ToastMetrics {
  const val CARD_PADDING_HORIZONTAL = 16f
  const val CARD_PADDING_VERTICAL = 12f
  const val CARD_RADIUS = 8f
  const val CARD_ELEVATION = 7f
  const val MESSAGE_ICON_GAP = 8f
  /** The status icon sits this far under the top of its box. */
  const val MESSAGE_ICON_PADDING_TOP = 2f
  const val MESSAGE_TEXT_GAP = 4f
  const val NOTIFICATION_GAP = 8f
  const val NOTIFICATION_TEXT_GAP = 2f
  const val ACTIONS_GAP = 8f
  const val ACTIONS_PADDING_TOP = 12f
  const val ACTIONS_PADDING_END = 2f
  const val ACTIONS_PADDING_BOTTOM = 2f
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
 * registry the icon stays blank.
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
  private val setCacheDir by lazy {
    registry?.javaClass?.methods?.firstOrNull { it.name == "setCacheDirIfNeeded" }
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
      setCacheDir?.invoke(target, appContext.cacheDir)
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
internal class ToastIconView(context: Context, val icon: ToastIcon) : FrameLayout(context) {
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

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    if (icon is ToastIcon.Image && cancelled.get() && imageView.drawable == null) {
      load(icon.uri)
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
        MotionEvent.ACTION_DOWN -> surface.setColor(action.pressedBackgroundColor ?: action.backgroundColor)
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> surface.setColor(action.backgroundColor)
      }
    }
    return super.onTouchEvent(event)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    setMeasuredDimension(frame.width(), frame.height())
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    surface.radius = minOf(action.borderRadius * resources.displayMetrics.density, (bottom - top) / 2f)
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
  /** Width of the content box in dp; the card adds its padding around it. */
  var contentWidth = 0f
  /** Tallest the content box grows, in dp; zero leaves it unbounded. */
  var contentMaxHeight = 0f
  /** The card's unrounded position in the window, in px; rounding follows it. */
  var originX = 0.0
  var originY = 0.0

  private val surface = GradientDrawable()
  private var iconView: ToastIconView? = null
  private var badge: View? = null
  private var imageView: ToastIconView? = null
  private var titles: List<TextView> = emptyList()
  private var messages: List<TextView> = emptyList()
  private var buttons: List<ToastActionButton> = emptyList()
  private val frames = HashMap<View, Rect>()
  private var size = Rect()

  init {
    surface.cornerRadius = context.dp(ToastMetrics.CARD_RADIUS).toFloat()
    background = surface
    elevation = context.dp(ToastMetrics.CARD_ELEVATION).toFloat()
    clipChildren = true
    clipToPadding = true
  }

  fun cancelLoads() {
    iconView?.cancelLoad()
    imageView?.cancelLoad()
    buttons.forEach { it.iconView?.cancelLoad() }
  }

  fun apply(spec: ToastSpec, cardColor: Int) {
    this.spec = spec
    surface.setColor(cardColor)
    spec.testID?.let { tag = it }
    isClickable = spec.pressable
    isFocusable = spec.pressable
    importantForAccessibility = if (spec.pressable) {
      IMPORTANT_FOR_ACCESSIBILITY_YES
    } else {
      IMPORTANT_FOR_ACCESSIBILITY_AUTO
    }
    cancelLoads()
    removeAllViews()
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
    iconView = spec.icon?.let { ToastIconView(context, it).also(::addView) }
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
    requestLayout()
  }

  /** What TalkBack reads when the card appears. */
  val announcement: String
    get() = (titles + messages).map { it.text.toString() }.filter { it.isNotEmpty() }.joinToString(". ")

  override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
    super.onInitializeAccessibilityNodeInfo(info)
    if (spec?.pressable == true) {
      info.isClickable = true
      info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)
      if (info.contentDescription.isNullOrEmpty()) {
        info.contentDescription = announcement
      }
    }
  }

  override fun performAccessibilityAction(action: Int, arguments: Bundle?): Boolean {
    if (action == AccessibilityNodeInfo.ACTION_CLICK && spec?.pressable == true) {
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

  /** Lays the content box out from [content]; returns its height in px. */
  private fun placeContent(spec: ToastSpec, content: Node, width: Double): Double {
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
    val columnX = icon?.let { px(it.size) + px(ToastMetrics.MESSAGE_ICON_GAP) } ?: 0.0
    iconView?.let { view ->
      val size = px(view.icon.size)
      frames[view] = content.child(0.0, px(ToastMetrics.MESSAGE_ICON_PADDING_TOP)).frame(size, size)
    }
    val columnWidth = max(width - columnX, 0.0)
    val column = content.child(columnX, 0.0)
    val gap = px(ToastMetrics.MESSAGE_TEXT_GAP)
    var y = 0.0
    var hasBlock = false
    for (block in listOf(titles, messages)) {
      if (block.isEmpty()) continue
      if (hasBlock) y += gap
      hasBlock = true
      y += placeParagraphs(block, column.child(0.0, y), columnWidth)
    }
    if (buttons.isNotEmpty()) {
      if (hasBlock) y += gap
      y += placeActions(
        column.child(0.0, y), columnWidth, ToastMetrics.ACTIONS_PADDING_END, ToastMetrics.ACTIONS_PADDING_BOTTOM,
      )
    }
    val iconBox = icon?.let { px(it.size + ToastMetrics.MESSAGE_ICON_PADDING_TOP) } ?: 0.0
    return max(y, iconBox)
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
    setMeasuredDimension(size.width(), size.height())
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
