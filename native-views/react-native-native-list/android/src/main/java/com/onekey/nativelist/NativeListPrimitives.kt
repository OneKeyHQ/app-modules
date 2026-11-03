package com.margelo.nitro.nativelist

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.react.common.assets.ReactFontManager
import kotlin.math.roundToInt

/** React Native color strings use CSS #RRGGBBAA ordering; Android expects #AARRGGBB. */
internal fun parseNativeListColor(value: String): Int {
  val normalized = value.trim()
  val androidValue =
    if (
      normalized.length == 9 &&
        normalized[0] == '#' &&
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

internal class NativeListFonts {
  var generation = 0
    private set
  private var families: Map<String, String> = emptyMap()

  fun configure(json: String): Boolean {
    val objectValue = runCatching { org.json.JSONObject(json) }.getOrNull() ?: return false
    val next = linkedMapOf<String, String>()
    val weights = setOf("regular", "medium", "semibold", "bold")
    for (key in objectValue.keys()) {
      val name = objectValue.opt(key) as? String ?: return false
      if (key !in weights || name.isEmpty() || name.length > 128 || name.any { it.code < 32 || it.code == 127 }) return false
      next[key] = name
    }
    if (families == next) return false
    families = next.toMap()
    generation++
    return true
  }

  private fun load(context: android.content.Context, weight: String, systemWeight: Int): Typeface {
    val family = families[weight]
    return if (family != null) ReactFontManager.getInstance().getTypeface(family, Typeface.NORMAL, context.assets)
    else if (android.os.Build.VERSION.SDK_INT >= 28) Typeface.create(Typeface.DEFAULT, systemWeight, false)
    else Typeface.create(if (systemWeight in 500..600) "sans-serif-medium" else "sans-serif", if (systemWeight >= 700) Typeface.BOLD else Typeface.NORMAL)
  }

  fun regular(context: android.content.Context) = load(context, "regular", 400)
  fun medium(context: android.content.Context) = load(context, "medium", 500)
  fun semibold(context: android.content.Context) = load(context, "semibold", 600)
  fun bold(context: android.content.Context) = load(context, "bold", 700)
}

internal class DottedUnderlineTextView(context: android.content.Context) :
  NativeListTextView(context) {
  var useSourceScale = false

  private fun scaledDp(value: Float) =
    if (useSourceScale) value * resources.displayMetrics.density
    else NativeListScale.dp(resources, value)

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

internal class PackedTitleLineLayout(context: android.content.Context) : LinearLayout(context) {
  var packsChildrenAtStart = false
  // OneKey patch: optional cap for the Market subtitle's localized name.
  var leadingTextMaxWidth = Int.MAX_VALUE

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
      (title as TextView).maxWidth =
        (widthSize -
            paddingLeft -
            paddingRight -
            accessoryWidth -
            titleMargins.leftMargin -
            titleMargins.rightMargin)
          .coerceAtLeast(0)
          .coerceAtMost(leadingTextMaxWidth)
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
internal class SelectorSubtitleLayout(context: android.content.Context) : LinearLayout(context) {
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
      (label.layoutParams as LayoutParams).width =
        if (desired <= textWidth) width
        else (width.toLong() * textWidth / desired.coerceAtLeast(1)).toInt()
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
  }
}

internal open class OneKeyIconView(context: android.content.Context) : View(context) {
  var useSourceScale = false
    set(value) { if (field == value) return; field = value; refresh() }
  var iconName: String = ""
    set(value) { if (field == value) return; field = value; refresh() }
  var tintColor: Int = Color.rgb(100, 100, 100)
    set(value) { if (field == value) return; field = value; refresh() }
  var glyphSizeDp: Int? = null
    set(value) { if (field == value) return; field = value; refresh() }
  private val owner = Any()
  private var generation = 0L
  private var identity: String? = null
  private var bitmap: android.graphics.Bitmap? = null
  private var displayed: com.margelo.nitro.onekeyimage.OneKeyNamedSVG.RenderResult? = null
  private var displayedGeometry: String? = null
  private val fill = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
  private var updateDepth = 0
  private var initialized = true
  private var attached = false

  internal fun beginIconUpdate() { updateDepth++ }
  internal fun endIconUpdate() { updateDepth--; if (updateDepth == 0) refresh() }

  protected open fun metadata(): NativeListIconMetadata? {
    nativeListGeneratedIcons[iconName]?.let { return it }
    if (iconName.isEmpty()) return null
    val meta = com.margelo.nitro.onekeyimage.OneKeyNamedSVG.iconMetadata(context.applicationContext.assets, iconName) ?: return null
    return NativeListIconMetadata(iconName, meta.width, meta.height)
  }
  protected open fun glyphRect(icon: NativeListIconMetadata): android.graphics.RectF {
    val size = minOf(minOf(width,height).toFloat(), glyphSizeDp?.let {
      if (useSourceScale) (it * resources.displayMetrics.density).roundToInt().toFloat()
      else NativeListScale.dp(resources,it).toFloat()
    } ?: Float.MAX_VALUE)
    val left = (width-size)/2f
    val top = (height-size)/2f
    return android.graphics.RectF(left,top,left+size,top+size*icon.height/icon.width)
  }

  protected fun refresh(force: Boolean = false) {
    if (updateDepth > 0) return
    val icon = metadata()
    val rect = icon?.let { glyphRect(it) }
    val next = "$iconName|$tintColor|$rect|${resources.displayMetrics.density}|$attached|$isShown"
    if (!force && next == identity) return
    identity = next; generation++
    val revision = generation
    com.margelo.nitro.onekeyimage.OneKeyNamedSVG.cancel(owner)
    fun clear() { bitmap = null; displayed = null; displayedGeometry = null; fill.colorFilter = null; invalidate() }
    if (!attached || !isShown || icon == null || rect == null || rect.isEmpty || icon.name == "Circle") { clear(); return }
    val w = kotlin.math.ceil(rect.width()).toInt()
    val h = kotlin.math.ceil(rect.height()).toInt()
    if (w !in 1..4096 || h !in 1..4096 || w.toLong() * h > 4194304) { clear(); return }
    val color = "rgba(${Color.red(tintColor)},${Color.green(tintColor)},${Color.blue(tintColor)},${Color.alpha(tintColor)/255.0})"
    val geometry = "${icon.name}|$rect|${resources.displayMetrics.density}"
    val ready = displayed
    if (displayedGeometry == geometry && ready != null && bitmap != null) {
      if (ready.canTint(color)) {
        fill.colorFilter = ready.tintColor(color)?.let { android.graphics.PorterDuffColorFilter(it, android.graphics.PorterDuff.Mode.SRC_IN) }
        invalidate(); return
      }
    } else clear()
    val weakView = java.lang.ref.WeakReference(this)
    com.margelo.nitro.onekeyimage.OneKeyNamedSVG.requestRender(owner, context.applicationContext.assets,
      icon.name,color,w,h,resources.displayMetrics.density) { result ->
      val view = weakView.get()
      if (view != null && view.generation == revision && view.attached && view.isShown) {
        view.displayed = result
        view.displayedGeometry = if (result != null) geometry else null
        view.bitmap = result?.bitmap
        view.fill.colorFilter = result?.tintColor(color)?.let { android.graphics.PorterDuffColorFilter(it, android.graphics.PorterDuff.Mode.SRC_IN) }
        view.invalidate()
      }
    }
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val icon = metadata() ?: return
    val rect = glyphRect(icon)
    if (icon.name == "Circle") { fill.colorFilter = null; fill.color = tintColor; canvas.drawOval(rect,fill); return }
    fill.alpha = 255
    bitmap?.let { canvas.drawBitmap(it,null,rect,fill) }
  }
  override fun onAttachedToWindow() { super.onAttachedToWindow(); attached = true; refresh() }
  override fun onDetachedFromWindow() {
    attached = false
    super.onDetachedFromWindow()
    refresh()
  }
  override fun onVisibilityChanged(changedView: View, visibility: Int) {
    super.onVisibilityChanged(changedView, visibility)
    if (initialized) refresh()
  }
  override fun onSizeChanged(w: Int,h: Int,oldw: Int,oldh: Int) { super.onSizeChanged(w,h,oldw,oldh); refresh() }
  override fun onConfigurationChanged(config: android.content.res.Configuration) { super.onConfigurationChanged(config); refresh(true) }
}

internal class OneKeyCheckboxView(context: android.content.Context) : OneKeyIconView(context) {
  var usesSelectorGeometry = false
    set(value) { if (field == value) return; field = value; refresh() }
  fun setState(value: String,color: Int) {
    tintColor = color
    iconName = when(value) {
      "checked" -> "CheckboxCheckedCustom"
      "indeterminate" -> "CheckboxIndeterminateCustom"
      else -> ""
    }
  }
  override fun metadata(): NativeListIconMetadata? = when(iconName) {
    "CheckboxCheckedCustom", "CheckboxIndeterminateCustom" -> NativeListIconMetadata(iconName,16f,16f)
    else -> null
  }
  override fun glyphRect(icon: NativeListIconMetadata): android.graphics.RectF {
    val size = if (usesSelectorGeometry) (16 * resources.displayMetrics.density).roundToInt().toFloat() else minOf(width,height)*0.8f
    val border = (2 * resources.displayMetrics.density).roundToInt().toFloat()
    val left = if(usesSelectorGeometry) border else (width-size)/2f
    val top = if(usesSelectorGeometry) border else (height-size)/2f
    return android.graphics.RectF(left,top,left+size,top+size)
  }
}

/** Captures existing glyph slots only; nested synchronous binds share their final identity. */
internal class NativeListIconUpdate(root: View) {
  private val icons = mutableListOf<OneKeyIconView>()
  init {
    fun collect(view: View) {
      if (view is OneKeyIconView) icons.add(view)
      if (view is android.view.ViewGroup) for (index in 0 until view.childCount) collect(view.getChildAt(index))
    }
    collect(root)
    icons.forEach { it.beginIconUpdate() }
  }
  fun finish() { icons.forEach { it.endIconUpdate() } }
}
