package com.margelo.nitro.nativelist

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.facebook.react.uimanager.ThemedReactContext
import kotlin.math.ceil
import kotlin.math.roundToInt
import org.json.JSONObject

/** System owns status content; placement and actions remain in the list host. */
internal class NativeListSystemRowView(context: ThemedReactContext) :
  NativeListRendererRowView(context) {
  private val title = NativeListTextView(context)
  private val message = NativeListTextView(context)
  private val action = NativeListTextView(context)
  private val column = LinearLayout(context).apply { orientation = VERTICAL }
  private val leading = View(context)
  private val bars = List(2) { View(context) }

  override fun defaultHeight(item: NativeListItem, layout: String): Int =
    when (item.json.optString("variant")) {
      "warning",
      "spacer" -> 0
      "loading" ->
        when (item.json.optString("loadingStyle")) {
          "skeleton" -> 56
          "spinner" -> 52
          else -> if (item.json.optString("presentation") == "market") 68 else 56
        }
      "noMatch",
      "end" -> if (item.json.optString("presentation") == "market") 44 else 36
      "retry" -> 44
      else -> 56
    }

  override fun minimumContentHeight(item: NativeListItem, layout: String, sizeDelta: Int): Int {
    val variant = item.json.optString("variant")
    if (variant == "warning") return 0
    if (item.json.optString("presentation") == "market" && variant in setOf("retry", "noMatch")) {
      return stylePx(
        if (variant == "noMatch") 88.0
        else if (item.json.optString("message").isEmpty()) 52.0 else 120.0
      )
    }
    return super.minimumContentHeight(item, layout, sizeDelta)
  }

  override fun modelHeight(item: NativeListItem, layout: String): Int? {
    // Legacy market retry/noMatch: round(height * density).
    if (
      item.json.has("height") &&
        item.json.optString("presentation") == "market" &&
        item.json.optString("variant") in setOf("retry", "noMatch")
    )
      return stylePx(item.json.optDouble("height"))
    if (
      sourceScale &&
        layout == "sectioned" &&
        item.json.optString("variant") == "spacer" &&
        !item.json.has("heightRounding")
    )
      return (item.json.optDouble("height") * resources.displayMetrics.density).toInt()
    return super.modelHeight(item, layout)
  }

  private fun color(theme: JSONObject?, name: String, fallback: String) =
    parseNativeListColor(theme?.optString(name, fallback) ?: fallback)

  private fun fill(color: Int, radius: Int) =
    GradientDrawable().apply {
      setColor(color)
      cornerRadius = dp(radius).toFloat()
    }

  private fun wrap() = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

  // Legacy: market retry rows always use source (unscaled) dimensions.
  override fun usesSourceScale(item: NativeListItem, provided: Boolean) =
    (item.json.optString("presentation") == "market" && item.json.optString("variant") == "retry") ||
      super.usesSourceScale(item, provided)

  override fun bindContent(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    recycleContent()
    val data = item.json
    val variant = data.optString("variant")
    val market = data.optString("presentation") == "market"
    val style = data.optJSONObject("style") ?: JSONObject()
    orientation = HORIZONTAL
    gravity = Gravity.CENTER_VERTICAL
    val hp = dp(if (layout == "table") 16 else 12)
    setPadding(hp, dp(8), hp, dp(8))
    if (market) setPadding(dp(20), dp(12), dp(20), dp(12))
    column.gravity = Gravity.CENTER_VERTICAL
    val primary = color(theme, "primaryText", "#000000DF")
    val secondary = color(theme, "secondaryText", "#0000009B")
    fun text(
      view: NativeListTextView,
      value: String,
      slot: String,
      size: Float = 14f,
      weight: String = "regular",
      line: Int = 20,
      lines: Int = 2,
      alignment: String = "start",
      foreground: Int = secondary,
      physical: Boolean = false,
    ) {
      val override = style.optJSONObject(slot)
      val merged =
        JSONObject(override?.toString() ?: "{}").apply {
          if (!has("alignment")) put("alignment", alignment)
        }
      var resolved =
        NativeListResolvedText.resolve(
          context,
          value,
          merged,
          size,
          weight,
          foreground,
          line,
          lines,
          sourceScale,
        )
      // Warning defaults wrap without a cap; explicit style line limits remain bounded.
      if (override?.has("lines") != true) resolved = resolved.copy(lines = lines)
      if (physical)
        resolved =
          resolved.copy(
            fontSize =
              if (override?.has("fontSize") == true) resolved.fontSize
              else
                ceil(
                    (resolved.fontSize * resources.displayMetrics.density /
                        resources.displayMetrics.scaledDensity)
                      .toDouble()
                  )
                  .toFloat(),
            explicitLineHeight = true,
          )
      resolved.bind(view)
      view.paintFlags =
        if (physical) view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG or Paint.LINEAR_TEXT_FLAG
        else view.paintFlags and Paint.LINEAR_TEXT_FLAG.inv()
      view.layoutParams = wrap()
    }
    when {
      variant == "loading" && data.optString("loadingStyle") == "skeleton" -> {
        setPadding(dp(20), dp(12), dp(20), dp(12))
        addView(
          NativeListMarketSkeleton(context, color(theme, "background", "#FFFFFF")),
          LayoutParams(LayoutParams.MATCH_PARENT, dp(32)),
        )
      }
      variant == "loading" && data.optString("loadingStyle") == "spinner" -> {
        gravity = Gravity.CENTER
        setPadding(0, dp(16), 0, dp(16))
        addView(
          ProgressBar(context, null, android.R.attr.progressBarStyleSmall).apply {
            isIndeterminate = true
            indeterminateTintList =
              android.content.res.ColorStateList.valueOf(color(theme, "icon", "#0000009B"))
          },
          LayoutParams(dp(20), dp(20)),
        )
      }
      market && variant == "retry" -> {
        val value = data.optString("message")
        orientation = VERTICAL
        gravity = Gravity.CENTER
        setPadding(
          dp(32),
          dp(if (value.isEmpty()) 11 else 32),
          dp(32),
          dp(if (value.isEmpty()) 11 else 27),
        )
        if (value.isNotEmpty()) {
          // Legacy market text: one line, ellipsized unless style sets lines/truncate.
          text(title, value, "message", 16f, line = 24, lines = 1, alignment = "center", physical = true)
          if (!hasExplicitTruncation(style, "message"))
            title.ellipsize = android.text.TextUtils.TruncateAt.END
          column.addView(title)
          addView(
            column,
            wrap().apply { bottomMargin = ceil(7.0 * resources.displayMetrics.density).toInt() },
          )
        }
        text(
          action,
          data.optString("actionText", "Retry"),
          "actionText",
          14f,
          "medium",
          20,
          1,
          "center",
          physical = true,
        )
        // Legacy retry button ellipsizes; an explicit style truncate/lines wins.
        if (!hasExplicitTruncation(style, "actionText"))
          action.ellipsize = android.text.TextUtils.TruncateAt.END
        val density = resources.displayMetrics.density
        val width =
          (ceil(action.paint.measureText(action.text.toString()).toDouble()) + 18 * density)
            .roundToInt()
        val height = ceil(ceil(20.0 * density) + 10 * density).toInt()
        action.gravity = action.gravity or Gravity.CENTER_VERTICAL
        addView(action, LayoutParams(width, height))
      }
      market && variant == "noMatch" -> {
        gravity = Gravity.CENTER
        setPadding(dp(32), dp(32), dp(32), dp(32))
        text(
          title,
          data.optString("message"),
          "message",
          16f,
          line = 24,
          lines = 1,
          alignment = "center",
          physical = true,
        )
        if (!hasExplicitTruncation(style, "message"))
          title.ellipsize = android.text.TextUtils.TruncateAt.END
        column.addView(title)
        addView(column, wrap())
      }
      variant == "warning" -> {
        setPadding(dp(12), dp(14), dp(12), dp(14))
        text(
          title,
          data.optString("title"),
          "title",
          14f,
          "medium",
          lines = Int.MAX_VALUE,
          foreground = primary,
          physical = true,
        )
        text(message, data.optString("message"), "message", lines = Int.MAX_VALUE, physical = true)
        column.addView(title, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        column.addView(
          message,
          LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            topMargin = if (style.has("lineGap")) stylePx(style.optDouble("lineGap")) else dp(4)
          },
        )
        addView(column, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
      }
      variant == "spacer" -> Unit
      variant == "end" -> {
        gravity = Gravity.CENTER
        setPadding(0, dp(16), 0, dp(16))
        val ink = color(theme, "separator", "#0000001F")
        listOf(80, 4, 80).forEach { w ->
          addView(
            View(context).apply { background = fill(ink, if (w == 4) 2 else 0) },
            LayoutParams(dp(w), if (w == 4) dp(4) else 1).apply {
              if (w == 4) {
                marginStart = dp(8)
                marginEnd = dp(8)
              }
            },
          )
        }
      }
      else -> {
        gravity = Gravity.CENTER
        if (variant == "loading") {
          val size = if (market) 32 else 40
          leading.background = fill(color(theme, "strongBackground", "#0000000F"), size / 2)
          addView(
            leading,
            LayoutParams(dp(size), dp(size)).apply {
              marginEnd =
                if (style.has("leadingGap")) stylePx(style.optDouble("leadingGap")) else dp(12)
            },
          )
        }
        text(
          title,
          data.optString("message"),
          "message",
          alignment = if (variant in setOf("retry", "noMatch")) "start" else "center",
          foreground = if (variant == "loading") primary else secondary,
        )
        column.addView(title)
        if (variant == "loading")
          bars.forEachIndexed { index, view ->
            view.background = fill(color(theme, "strongBackground", "#0000000F"), 6)
            column.addView(
              view,
              LayoutParams(dp(if (index == 0) 120 else 80), dp(12)).apply {
                if (index == 0)
                  bottomMargin =
                    if (style.has("lineGap")) stylePx(style.optDouble("lineGap")) else dp(8)
              },
            )
          }
        if (variant == "noMatch") setPadding(dp(12), 0, dp(12), 0)
        if (variant == "retry") setPadding(dp(12), dp(8), dp(12), dp(8))
        addView(
          column,
          if (variant in setOf("retry", "noMatch")) LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
          else wrap(),
        )
        if (variant == "retry") {
          text(
            action,
            // Legacy: only the market retry reads actionText; this label is fixed.
            "Retry",
            "actionText",
            14f,
            "medium",
            20,
            1,
            "end",
            primary,
          )
          action.background = fill(color(theme, "strongBackground", "#0000000F"), 16)
          action.setPadding(dp(10), dp(4), dp(10), dp(4))
          addView(action, wrap().apply { marginStart = dp(12) })
        }
      }
    }
    action.setOnClickListener {
      // Legacy Retry was a text accessory: gated by the row's disabled state.
      if (!data.optBoolean("disabled")) emitAction(data.optString("actionKey"), action, "trailingAccessory", 0)
    }
    if (style.has("horizontalPadding")) {
      val padding = stylePx(style.optDouble("horizontalPadding"))
      setPadding(padding, paddingTop, padding, paddingBottom)
    }
    if (style.has("verticalPadding")) {
      val padding = stylePx(style.optDouble("verticalPadding"))
      setPadding(paddingLeft, padding, paddingRight, padding)
    }
    style
      .optJSONObject("container")
      ?.optString("contentVerticalAlignment")
      ?.takeIf { it.isNotEmpty() }
      ?.let { alignment ->
        gravity =
          (gravity and Gravity.VERTICAL_GRAVITY_MASK.inv()) or
            when (alignment) {
              "top" -> Gravity.TOP
              "bottom" -> Gravity.BOTTOM
              else -> Gravity.CENTER_VERTICAL
            }
      }
  }

  override fun recycleContent() {
    removeAllViews()
    column.removeAllViews()
    action.background = null
    action.setPadding(0, 0, 0, 0)
    action.setOnClickListener(null)
  }

  override fun disposeContent() = recycleContent()
}

// Market/TokenListSkeleton: source geometry and the native Skeleton's 3s shimmer.
private class NativeListMarketSkeleton(context: android.content.Context, backgroundColor: Int) :
  View(context) {
  private val marks = Array(5) { RectF() }
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val matrix = Matrix()
  private val shaders = arrayOfNulls<LinearGradient>(5)
  private val dark =
    Color.red(backgroundColor) * 0.299 +
      Color.green(backgroundColor) * 0.587 +
      Color.blue(backgroundColor) * 0.114 < 128
  private val colors =
    intArrayOf(
      Color.parseColor(if (dark) "#111111" else "#FAFAFA"),
      Color.parseColor(if (dark) "#333333" else "#CDCDCD"),
      Color.parseColor(if (dark) "#111111" else "#FAFAFA"),
    )
  private var phase = 0f
  private val animator =
    ValueAnimator.ofFloat(0f, 1f).apply {
      duration = 3000
      repeatCount = ValueAnimator.INFINITE
      interpolator = LinearInterpolator()
      addUpdateListener {
        phase = it.animatedValue as Float
        invalidate()
      }
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
      shaders[index] =
        LinearGradient(
          0f,
          0f,
          mark.width(),
          0f,
          colors,
          floatArrayOf(0f, 0.5f, 1f),
          Shader.TileMode.CLAMP,
        )
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

/** True when the caller's text style for [slot] sets its own truncation (`lines` / `truncate`). */
private fun hasExplicitTruncation(style: JSONObject, slot: String): Boolean =
  style.optJSONObject(slot)?.let { it.has("lines") || it.has("truncate") } == true
