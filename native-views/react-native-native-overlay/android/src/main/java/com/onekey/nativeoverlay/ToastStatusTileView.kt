package com.onekey.nativeoverlay

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/** Analytical unit-mass spring step response matching Web `springProgressAt` and iOS `CASpringAnimation`. */
internal fun toastSpringProgress(stiffness: Double, damping: Double, elapsedMs: Float): Float {
  if (elapsedMs <= 0f) return 0f
  val t = elapsedMs / 1000.0
  val w0 = sqrt(stiffness)
  val zeta = damping / (2.0 * w0)
  val progress = when {
    zeta < 1.0 -> {
      val wd = w0 * sqrt(1.0 - zeta * zeta)
      1.0 - exp(-zeta * w0 * t) * (cos(wd * t) + ((zeta * w0) / wd) * sin(wd * t))
    }
    zeta == 1.0 -> 1.0 - exp(-w0 * t) * (1.0 + w0 * t)
    else -> {
      val root = sqrt(zeta * zeta - 1.0)
      val r1 = -w0 * (zeta - root)
      val r2 = -w0 * (zeta + root)
      1.0 - (r2 * exp(r1 * t) - r1 * exp(r2 * t)) / (r2 - r1)
    }
  }
  return progress.toFloat()
}

@SuppressLint("ViewConstructor")
internal class ToastStatusTileView(context: Context, private var status: ToastIcon.Status) : FrameLayout(context) {
  private val surface = GradientDrawable()
  private val colors = ArgbEvaluator()
  private val easeOut = PathInterpolator(0f, 0f, .58f, 1f)
  private val easeIn = PathInterpolator(.42f, 0f, 1f, 1f)
  private val tintEase = PathInterpolator(.4f, 0f, .2f, 1f)
  private var glyph = makeGlyph(status)
  private var departing: ToastStatusGlyphView? = null
  private var animation: ValueAnimator? = null
  private var tileColor = status.backgroundColor
  private var tinting = false

  init {
    importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    clipToOutline = true
    background = surface
    applyTileColor(tileColor)
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    surface.cornerRadius = w * 11f / 36f
  }

  private fun makeGlyph(icon: ToastIcon.Status) = ToastStatusGlyphView(context, icon).also {
    addView(it, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
  }

  private fun applyTileColor(color: Int) {
    tileColor = color
    surface.setColor(color)
  }

  private fun fade(view: View, opacity: Float, scale: Float) {
    view.alpha = opacity.coerceIn(0f, 1f)
    view.scaleX = scale
    view.scaleY = scale
  }

  private fun drawAt(elapsed: Float, popStart: Float) {
    // Snappy POP spring: mass 1, stiffness 920, damping 40 (same ~6.4% overshoot, ~35% faster).
    val pop = toastSpringProgress(920.0, 40.0, elapsed - popStart)
    fade(glyph, pop, mix(.4f, 1f, pop))
    glyph.strokeProgress = easeOut.getInterpolation(progress(elapsed, popStart + 55f, 180f))
  }

  private fun animateFor(duration: Long, frame: (Float) -> Unit) {
    animation?.cancel()
    frame(0f)
    var cancelled = false
    animation = ValueAnimator.ofFloat(0f, duration.toFloat()).apply {
      this.duration = duration
      interpolator = LinearInterpolator()
      addUpdateListener { frame(it.animatedValue as Float) }
      addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationCancel(animator: Animator) {
          cancelled = true
        }

        override fun onAnimationEnd(animator: Animator) {
          if (!cancelled) {
            tinting = false
            removeDeparting()
            fade(this@ToastStatusTileView, 1f, 1f)
            fade(glyph, 1f, 1f)
            glyph.strokeProgress = 1f
            applyTileColor(status.backgroundColor)
          }
        }
      })
      start()
    }
  }

  fun enter() {
    // Tile spring (110 ms, 620/50) + glyph POP spring (155 ms, 920/40) + stroke draw (210..390 ms).
    animateFor(510L) { elapsed ->
      val tile = toastSpringProgress(620.0, 50.0, elapsed - 110f)
      fade(this, tile, mix(.85f, 1f, tile))
      drawAt(elapsed, 155f)
    }
  }

  fun apply(next: ToastIcon.Status, settle: Boolean) {
    val previous = status
    status = next
    if (previous.status == next.status) {
      glyph.color = next.color
      if (previous.backgroundColor != next.backgroundColor && !tinting) {
        applyTileColor(next.backgroundColor)
      }
      return
    }

    val eligible = settle && previous.status != next.status
    val old = glyph
    val oldOpacity = old.alpha
    val oldScale = old.scaleX
    val tileOpacity = alpha
    val tileScale = scaleX
    val oldColor = tileColor
    removeDeparting()
    glyph = makeGlyph(next)
    val leave = if (eligible && oldOpacity > 0f) 90f else 0f
    if (leave > 0f) {
      departing = old
    } else {
      old.stopRing()
      removeView(old)
    }
    if (!eligible) {
      cancelMotion()
      return
    }

    animateFor((leave + 360f).toLong()) { elapsed ->
      val leaving = easeIn.getInterpolation(progress(elapsed, 0f, leave.coerceAtLeast(1f)))
      departing?.let {
        fade(it, mix(oldOpacity, 0f, leaving), mix(oldScale, .5f, leaving))
        if (elapsed >= leave) removeDeparting()
      }
      val bump = progress(elapsed, leave, 260f)
      val bumpScale = if (bump <= .4f) {
        mix(tileScale, 1.07f, easeOut.getInterpolation(bump / .4f))
      } else {
        mix(1.07f, 1f, easeOut.getInterpolation((bump - .4f) / .6f))
      }
      fade(this, mix(tileOpacity, 1f, easeOut.getInterpolation(progress(bump, 0f, .4f))), bumpScale)
      val tint = tintEase.getInterpolation(progress(elapsed, 0f, 140f))
      tinting = tint < 1f
      applyTileColor(colors.evaluate(tint, oldColor, status.backgroundColor) as Int)
      drawAt(elapsed, leave)
    }
  }

  private fun removeDeparting() {
    departing?.let {
      it.stopRing()
      removeView(it)
    }
    departing = null
  }

  fun cancelMotion() {
    animation?.cancel()
    animation = null
    tinting = false
    removeDeparting()
    fade(this, 1f, 1f)
    fade(glyph, 1f, 1f)
    glyph.strokeProgress = 1f
    glyph.stopRing()
    applyTileColor(status.backgroundColor)
  }

  fun startRing() = glyph.startRing()

  override fun onDetachedFromWindow() {
    cancelMotion()
    super.onDetachedFromWindow()
  }

  private fun progress(elapsed: Float, start: Float, duration: Float) =
    ((elapsed - start) / duration).coerceIn(0f, 1f)

  private fun mix(from: Float, to: Float, fraction: Float) = from + (to - from) * fraction
}

@SuppressLint("ViewConstructor")
internal class ToastStatusGlyphView(context: Context, icon: ToastIcon.Status) : View(context) {
  private val recipe = icon.glyph
  private val contours = recipe.contours.filter { it.size >= 2 }.map { points ->
    val path = Path().apply {
      moveTo(points.first().first, points.first().second)
      points.drop(1).forEach { (x, y) -> lineTo(x, y) }
    }
    PathMeasure(path, false)
  }
  private val segment = Path()
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    strokeWidth = recipe.strokeWidth
    strokeCap = Paint.Cap.ROUND
    strokeJoin = Paint.Join.ROUND
  }
  private var spin: ObjectAnimator? = null
  var color = icon.color
    set(value) {
      field = value
      invalidate()
    }
  var strokeProgress = 1f
    set(value) {
      field = value
      invalidate()
    }

  fun startRing() {
    if (recipe.ring == null || spin != null) return
    spin = ObjectAnimator.ofFloat(this, ROTATION, 0f, 360f).apply {
      duration = 850L
      repeatCount = ValueAnimator.INFINITE
      interpolator = LinearInterpolator()
      start()
    }
  }

  fun stopRing() {
    spin?.cancel()
    spin = null
    rotation = 0f
  }

  override fun onDraw(canvas: Canvas) {
    val scale = minOf(width, height) / 48f
    canvas.save()
    canvas.translate(width / 2f - 12f * scale, height / 2f - 12f * scale)
    canvas.scale(scale, scale)
    paint.color = color
    paint.style = Paint.Style.STROKE
    if (strokeProgress > 0) {
      contours.forEach { contour ->
        segment.rewind()
        contour.getSegment(0f, contour.length * strokeProgress, segment, true)
        canvas.drawPath(segment, paint)
      }
    }
    recipe.ring?.let { ring ->
      paint.alpha = (Color.alpha(color) * ring.trackOpacity).roundToInt()
      canvas.drawCircle(12f, 12f, ring.radius, paint)
      paint.color = color
      val low = 12f - ring.radius
      val high = 12f + ring.radius
      canvas.drawArc(low, low, high, high, -90f, 360f * ring.arcFraction, false, paint)
    }
    recipe.dot?.let { dot ->
      paint.style = Paint.Style.FILL
      canvas.drawCircle(dot.x, dot.y, dot.radius, paint)
    }
    canvas.restore()
  }
}
