package com.margelo.nitro.nativelist

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.PathParser
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

internal class OneKeyIconView(context: android.content.Context) : View(context) {
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
    val drawSize =
      minOf(
        minOf(width, height).toFloat(),
        glyphSizeDp?.let {
          if (useSourceScale) (it * resources.displayMetrics.density).roundToInt().toFloat()
          else NativeListScale.dp(resources, it).toFloat()
        } ?: Float.MAX_VALUE,
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
    private val selectorIconColors: Map<String, List<Int?>> =
      mapOf(
        "GlobusOutline" to listOf(null),
        "LockSolid" to listOf(null),
        "GoogleIllus" to
          listOf(
            parseNativeListColor("#4285F4"),
            parseNativeListColor("#34A853"),
            parseNativeListColor("#FBBC05"),
            parseNativeListColor("#EA4335"),
          ),
        "AppleBrand" to listOf(null),
        "BotIllus" to
          listOf(
            parseNativeListColor("#8897A5"),
            parseNativeListColor("#3FA9F5"),
            parseNativeListColor("#8897A5"),
            parseNativeListColor("#8897A5"),
            parseNativeListColor("#10243E"),
            parseNativeListColor("#10243E"),
            parseNativeListColor("#10243E"),
          ),
        "AllNetworksSolid" to listOf(null, null),
        "CrossedSmallSolid" to listOf(null),
        "AccountErrorCustom" to listOf(Color.argb(0x72, 0, 0, 0), Color.argb(0x72, 0, 0, 0)),
        "Circle" to listOf(null),
      )
    private val selectorIconViewBoxes =
      mapOf(
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
    private val actionIconFillTypes =
      mapOf(
        "GlobusOutline" to listOf(Path.FillType.EVEN_ODD),
        "LockSolid" to listOf(Path.FillType.EVEN_ODD),
        "GoogleIllus" to
          listOf(
            Path.FillType.WINDING,
            Path.FillType.WINDING,
            Path.FillType.WINDING,
            Path.FillType.WINDING,
          ),
        "AppleBrand" to listOf(Path.FillType.WINDING),
        "BotIllus" to
          listOf(
            Path.FillType.WINDING,
            Path.FillType.WINDING,
            Path.FillType.WINDING,
            Path.FillType.WINDING,
            Path.FillType.WINDING,
            Path.FillType.WINDING,
            Path.FillType.WINDING,
          ),
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
    private val iconPaths =
      mapOf(
        // OneKey patch: official selector SVG path geometry.
        "GlobusOutline" to
          listOf(
            "M12 2c5.185 0 9.448 3.947 9.95 9H22v2h-.05c-.502 5.053-4.765 9-9.95 9s-9.448-3.947-9.95-9H2v-2h.05C2.552 5.947 6.815 2 12 2M9.523 13c.09 1.982.438 3.726.934 5.002.29.746.612 1.282.917 1.614.304.331.517.384.626.384s.322-.053.626-.384c.305-.332.627-.868.917-1.614.496-1.276.845-3.02.934-5.002zm-5.459 0a8 8 0 0 0 4.8 6.36 10 10 0 0 1-.271-.633C7.994 17.187 7.61 15.189 7.52 13zm12.416 0c-.09 2.189-.474 4.187-1.073 5.727a10 10 0 0 1-.271.633 8 8 0 0 0 4.8-6.36zM8.863 4.639A8 8 0 0 0 4.064 11h3.457c.09-2.189.473-4.187 1.072-5.727q.127-.327.27-.634M12 4c-.109 0-.322.053-.626.384-.305.332-.627.868-.917 1.614-.496 1.276-.844 3.02-.934 5.002h4.954c-.09-1.982-.438-3.726-.934-5.002-.29-.746-.612-1.282-.917-1.614C12.322 4.053 12.109 4 12 4m3.136.639q.144.307.271.634c.599 1.54.982 3.538 1.073 5.727h3.456a8 8 0 0 0-4.8-6.361"
          ),
        "LockSolid" to
          listOf(
            "M12 2a5 5 0 0 1 5 5v2h3v13H4V9h3V7a5 5 0 0 1 5-5m-1 11v5h2v-5zm1-9a3 3 0 0 0-3 3v2h6V7a3 3 0 0 0-3-3"
          ),
        "GoogleIllus" to
          listOf(
            "M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09",
            "M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23",
            "M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22z",
            "M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53",
          ),
        "AppleBrand" to
          listOf(
            "M11.67.834c.117 1.074-.315 2.153-.955 2.928-.64.773-1.692 1.378-2.718 1.298-.14-1.054.38-2.151.971-2.836C9.63 1.45 10.746.872 11.67.834M14.994 7.093c-.176.108-1.992 1.224-1.972 3.482.025 2.769 2.428 3.693 2.46 3.705l-.004.015a10.1 10.1 0 0 1-1.264 2.593c-.764 1.116-1.556 2.229-2.806 2.254-.598.011-1-.162-1.416-.343-.437-.19-.891-.386-1.609-.386-.751 0-1.226.203-1.683.398-.397.169-.78.333-1.32.354-1.208.047-2.124-1.207-2.895-2.32C.909 14.57-.294 10.414 1.322 7.612c.803-1.395 2.237-2.275 3.794-2.298.671-.014 1.32.244 1.89.47.434.172.821.326 1.135.326.282 0 .659-.149 1.099-.323.692-.273 1.539-.607 2.41-.518.599.026 2.276.24 3.354 1.818z"
          ),
        "BotIllus" to
          listOf(
            "M11 2a1 1 0 1 1 2 0v1.8l1.6 1.6a1 1 0 1 1-1.4 1.4L12 5.6l-1.2 1.2a1 1 0 0 1-1.4-1.4L11 3.8z",
            "M8.0 6.0h8.0a5.0 5.0 0 0 1 5.0 5.0v4.0a5.0 5.0 0 0 1 -5.0 5.0h-8.0a5.0 5.0 0 0 1 -5.0 -5.0v-4.0a5.0 5.0 0 0 1 5.0 -5.0z",
            "M3.0 10.0h0.0a1.5 1.5 0 0 1 1.5 1.5v3.0a1.5 1.5 0 0 1 -1.5 1.5h0.0a1.5 1.5 0 0 1 -1.5 -1.5v-3.0a1.5 1.5 0 0 1 1.5 -1.5z",
            "M21.0 10.0h0.0a1.5 1.5 0 0 1 1.5 1.5v3.0a1.5 1.5 0 0 1 -1.5 1.5h0.0a1.5 1.5 0 0 1 -1.5 -1.5v-3.0a1.5 1.5 0 0 1 1.5 -1.5z",
            "M7.5 12.0a1.5 1.5 0 1 0 3.0 0a1.5 1.5 0 1 0 -3.0 0",
            "M13.5 12.0a1.5 1.5 0 1 0 3.0 0a1.5 1.5 0 1 0 -3.0 0",
            "M8.5 15.4c.9.8 2.08 1.2 3.5 1.2s2.6-.4 3.5-1.2c.24-.2.6-.18.8.06.2.23.17.6-.06.8-1.14.98-2.58 1.46-4.24 1.46s-3.1-.48-4.24-1.46a.58.58 0 0 1-.06-.8c.2-.24.56-.26.8-.06",
          ),
        "AllNetworksSolid" to
          listOf(
            "M15.333 13.998a1.335 1.335 0 1 1 0 2.67 1.335 1.335 0 0 1 0-2.67",
            "M12 0c6.627 0 12 5.373 12 12s-5.373 12-12 12S0 18.627 0 12 5.373 0 12 0M8 12.668A2 2 0 0 0 6 14.666V16c0 1.103.895 1.997 1.998 1.998h1.334A2 2 0 0 0 11.33 16v-1.334a2 2 0 0 0-1.998-1.998zm7.333 0a2.665 2.665 0 1 0 0 5.33 2.665 2.665 0 0 0 0-5.33M7.999 6.001A2 2 0 0 0 6.001 8v1.334c0 1.103.895 1.998 1.998 1.998h1.334a2 2 0 0 0 1.998-1.998V7.999a2 2 0 0 0-1.998-1.998zm6.667 0A2 2 0 0 0 12.668 8v1.334c0 1.103.895 1.998 1.998 1.998H16a2 2 0 0 0 1.998-1.998V7.999A2 2 0 0 0 16 6.001z",
          ),
        "CrossedSmallSolid" to
          listOf(
            "M17.87 8.25 14.12 12l3.75 3.75-2.12 2.121-3.75-3.75-3.75 3.75-2.121-2.121L9.879 12l-3.75-3.75 2.12-2.121L12 9.879l3.75-3.75 2.122 2.121Z"
          ),
        "AccountErrorCustom" to
          listOf(
            "M12.5 12.75a1.25 1.25 0 1 0 0-2.5 1.25 1.25 0 0 0 0 2.5",
            "M0 3.5A3.5 3.5 0 0 1 3.5 0h8.088A2.41 2.41 0 0 1 14 2.412V5h1a3 3 0 0 1 3 3v7a3 3 0 0 1-3 3H4a4 4 0 0 1-4-4zm2 3.163V14a2 2 0 0 0 2 2h11a1 1 0 0 0 1-1V8a1 1 0 0 0-1-1H3.5c-.537 0-1.045-.12-1.5-.337M2 3.5A1.5 1.5 0 0 0 3.5 5H12V2.412A.41.41 0 0 0 11.588 2H3.5A1.5 1.5 0 0 0 2 3.5",
          ),
        "Circle" to listOf("M0 12a12 12 0 1 0 24 0a12 12 0 1 0 -24 0"),
        "BadgeVerifiedSolid" to
          listOf(
            "M9.483 11.458v3.5h-1v-3.5z M10.467 2.698a2.03 2.03 0 0 1 3.065 0l1.358 1.564a.03.03 0 0 0 .028.01l2.046-.325a2.03 2.03 0 0 1 2.347 1.971l.037 2.07q0 .016.014.026l1.776 1.066a2.03 2.03 0 0 1 .532 3.019l-1.304 1.609a.03.03 0 0 0-.005.03l.675 1.956a2.03 2.03 0 0 1-1.533 2.656l-2.033.394a.03.03 0 0 0-.023.019l-.741 1.933a2.03 2.03 0 0 1-2.88 1.05l-1.811-1.006a.03.03 0 0 0-.03 0l-1.811 1.005a2.03 2.03 0 0 1-2.88-1.049l-.742-1.933a.03.03 0 0 0-.023-.019l-2.033-.394a2.03 2.03 0 0 1-1.532-2.656l.675-1.957a.03.03 0 0 0-.005-.029l-1.304-1.61a2.03 2.03 0 0 1 .532-3.018l1.776-1.066a.03.03 0 0 0 .014-.026l.035-2.07a2.03 2.03 0 0 1 2.349-1.97l2.045.324a.03.03 0 0 0 .028-.01zm1.516 3.76a.5.5 0 0 0-.447.276l-1.861 3.724H8.483a1 1 0 0 0-1 1v3.5a1 1 0 0 0 1 1h6.692a2 2 0 0 0 1.981-1.73l.341-2.5a2 2 0 0 0-1.982-2.27h-1.939l.197-1.269a1.5 1.5 0 0 0-1.481-1.731z"
          ),
        "ArrowBottomOutline" to
          listOf("m13 17.586 5-5L19.414 14 12 21.414 4.586 14 6 12.586l5 5V3h2z"),
        "ArrowTopOutline" to
          listOf("M19.414 10 18 11.414l-5-5V21h-2V6.414l-5 5L4.586 10 12 2.586z"),
        "ChartTrendingUpOutline" to
          listOf("M22 13h-2V9.414l-7 7-4-4-6 6L1.586 17 9 9.586l4 4L18.586 8H15V6h7z"),
        "SwapHorOutline" to
          listOf(
            "M21 16H6.914l2.293 2.293-1.414 1.414L2.086 14H21zm.914-6H3V8h14.086l-2.293-2.293 1.414-1.414z"
          ),
        "ShieldExclamationOutline" to
          listOf(
            "M12 12.25a1.25 1.25 0 1 1 0 2.5 1.25 1.25 0 0 1 0-2.5m0-4.75a1 1 0 0 1 1 1v2a1 1 0 0 1-2 0v-2a1 1 0 0 1 1-1",
            "M11.352 2.223c.42-.145.876-.145 1.296 0l6.98 2.4a1.995 1.995 0 0 1 1.347 1.886v5.432c0 2.799-1.146 4.817-2.805 6.387-1.61 1.525-3.735 2.652-5.696 3.71a1 1 0 0 1-.947 0c-1.961-1.058-4.086-2.185-5.697-3.71-1.659-1.57-2.805-3.588-2.805-6.386V6.508c0-.852.542-1.61 1.347-1.887l6.98-2.4ZM5.02 6.509v5.432c0 2.16.848 3.676 2.18 4.938 1.272 1.204 2.957 2.149 4.799 3.145 1.842-.996 3.527-1.94 4.799-3.145 1.332-1.262 2.181-2.778 2.181-4.938V6.51L12 4.109z",
          ),
        "InfoCircleOutline" to
          listOf(
            "M13 17h-2v-5h-1v-2h3zm0-8h-2V7h2z",
            "M12 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2m0 2a8 8 0 1 0 0 16 8 8 0 0 0 0-16",
          ),
        "SpeakerPromoteOutline" to
          listOf(
            "M21.996 11a4 4 0 0 1-3 3.874v5.495l-5.361-1.711A3.998 3.998 0 0 1 5.996 17v-.779l-4-1.276v-7.89l17-5.424v5.495c1.725.444 3 2.01 3 3.874m-2 0c0-.74-.402-1.385-1-1.731v3.46c.597-.345 1-.989 1-1.729M7.998 7.24v7.521l8.998 2.871V4.368l-8.998 2.87ZM3.996 8.516v4.967l2.002.638V7.877zm4 8.484a2 2 0 0 0 3.706 1.041L7.996 16.86z"
          ),
        "PlusCircleOutline" to
          listOf(
            "M13 11h4v2h-4v4h-2v-4l-4 .001v-2L11 11V7h2z",
            "M12 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2m0 2a8 8 0 1 0 0 16 8 8 0 0 0 0-16",
          ),
        "PlusSmallOutline" to listOf("M13 11h5v2h-5v5h-2v-5H6v-2h5V6h2z"),
        "DotHorOutline" to listOf("M6 14H2v-4h4zm8 0h-4v-4h4zm8 0h-4v-4h4z"),
        "MinusCircleOutline" to
          listOf(
            "M17 13H7v-2h10z",
            "M12 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2m0 2a8 8 0 1 0 0 16 8 8 0 0 0 0-16",
          ),
        "MinusCircleSolid" to
          listOf(
            "M12 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2M7 13h10v-2H7z"
          ),
        "ErrorSolid" to listOf("M23.256 20H.742L12 1.041zM11 15v2h2v-2zm0-6v5h2V9z"),
        "QuestionmarkSolid" to
          listOf(
            "M12 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2m-1 15h2v-2h-2zM9 7v3h2V9h2v1l-2 1.5V14h2v-1.5l2-1.5V7z"
          ),
        "PencilOutline" to
          listOf(
            "M22.414 7.5 7.914 22H2v-5.914l14.5-14.5zM4 16.914V20h3.086l9.5-9.5L13.5 7.414zM14.914 6 18 9.086 19.586 7.5 16.5 4.414z"
          ),
        "ChevronRightSmallOutline" to listOf("M15.414 12 10 17.414 8.586 16l4-4-4-4L10 6.586z"),
        "DragOutline" to
          listOf("M11 21H7v-4h4zm6 0h-4v-4h4zm-6-7H7v-4h4zm6 0h-4v-4h4zm-6-7H7V3h4zm6 0h-4V3h4z"),
        "StarOutline" to
          listOf(
            "m15.455 7.243 7.729 1.123-5.592 5.45 1.32 7.698L12 17.879l-6.911 3.635 1.32-7.698-5.592-5.45 7.728-1.123L12 .24zM9.872 9.071l-4.759.69 3.444 3.358-.814 4.738L12 15.62l.465.245 3.791 1.993-.813-4.739 3.443-3.357-4.758-.69L12 4.758z"
          ),
        "StarSolid" to
          listOf(
            "m15.405 7.313 7.84 1.034-5.735 5.443 1.44 7.774L12 17.793l-6.948 3.771 1.44-7.774L.756 8.347l7.839-1.034L12 .178z"
          ),
        "ChevronGrabberVerOutline" to
          listOf(
            "M17.414 15 12 20.414 6.586 15 8 13.586l4 4 4-4zm0-6L16 10.414l-4-4-4 4L6.586 9 12 3.586z"
          ),
        "ChevronBottomOutline" to
          listOf("M20.707 9.707 12 18.414 3.293 9.707l1.414-1.414L12 15.586l7.293-7.293z"),
        "ChevronTopOutline" to
          listOf("m20.707 14.293-1.414 1.414L12 8.414l-7.293 7.293-1.414-1.414L12 5.586z"),
        "ImageSquareWavesOutline" to
          listOf(
            "M14.25 7a2 2 0 1 1 0 4 2 2 0 0 1 0-4",
            "M21 21H3V3h18zM5 16.414V19h12.586L14 15.414l-2 2-4-4zm0-2.828 3-3 4 4 2-2 5 5V5H5z",
          ),
      )
  }
}

internal class OneKeyCheckboxView(context: android.content.Context) : View(context) {
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
    val pathData =
      when (state) {
        "checked" ->
          "M12.204 5.043a1 1 0 0 1 0 1.414l-4.5 4.5a1 1 0 0 1-1.414 0l-2-2a1 1 0 1 1 1.414-1.414l1.293 1.293 3.793-3.793a1 1 0 0 1 1.414 0"
        "indeterminate" -> "M4 8a1 1 0 0 1 1-1h6a1 1 0 0 1 0 2H5a1 1 0 0 1-1-1"
        else -> return
      }
    // OneKey patch: the source icon is a 16dp child after the 2dp border, not 80% of a rounded
    // frame.
    val drawSize =
      if (usesSelectorGeometry) (16 * resources.displayMetrics.density).roundToInt().toFloat()
      else minOf(width, height) * 0.8f
    val borderOffset = (2 * resources.displayMetrics.density).roundToInt().toFloat()
    canvas.save()
    canvas.translate(
      if (usesSelectorGeometry) borderOffset else (width - drawSize) / 2f,
      if (usesSelectorGeometry) borderOffset else (height - drawSize) / 2f,
    )
    canvas.scale(drawSize / 16f, drawSize / 16f)
    PathParser.createPathFromPathData(pathData)?.let { canvas.drawPath(it, glyphPaint) }
    canvas.restore()
  }
}
