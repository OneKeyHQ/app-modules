package com.margelo.nitro.perpdepthbar

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.view.animation.PathInterpolator
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext

/**
 * Renders an entire column of order-book depth bars for one side. Draws N
 * rects whose horizontal fill fraction animates on the UI thread via a single
 * [ValueAnimator], replacing N reanimated `DepthBar` instances.
 */
@DoNotStrip
class HybridPerpDepthBars(val context: ThemedReactContext) : HybridPerpDepthBarsSpec() {

  private val density = context.resources.displayMetrics.density
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

  private var current = DoubleArray(0)
  private var start = DoubleArray(0)
  private var target = DoubleArray(0)

  private var lastEpoch = Double.NaN
  private var hasDrawn = false
  private var isDisposed = false
  private var animator: ValueAnimator? = null

  override val view: View = object : View(context) {
    override fun onDraw(canvas: Canvas) {
      drawBars(canvas)
    }
  }.apply {
    isClickable = false
    isFocusable = false
  }

  // MARK: - Props
  override var percents: DoubleArray = DoubleArray(0)
    get() = field
    set(value) { if (!isDisposed) { field = value; view.invalidate() } }

  override var rowHeight: Double = 0.0
    get() = field
    set(value) { if (!isDisposed) { field = value; view.invalidate() } }

  override var rowMarginTop: Double = 0.0
    get() = field
    set(value) { if (!isDisposed) { field = value; view.invalidate() } }

  override var barInset: Double = 0.0
    get() = field
    set(value) { if (!isDisposed) { field = value; view.invalidate() } }

  override var origin: String = "left"
    get() = field
    set(value) { if (!isDisposed) { field = value; view.invalidate() } }

  override var reducedMotion: Boolean = false
    get() = field
    set(value) { field = value }

  override var epoch: Double = 0.0
    get() = field
    set(value) { field = value }

  override var color: String = ""
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      paint.color = PerpColorParser.parse(value)
      view.invalidate()
    }

  /**
   * Called by Nitro once after a prop-update transaction. Recompute targets and
   * (re)start the animation here so a multi-prop update animates once.
   */
  override fun afterUpdate() {
    super.afterUpdate()
    if (isDisposed) return
    syncTargets()
  }

  private fun syncTargets() {
    val count = percents.size
    val epochChanged = epoch != lastEpoch
    val countChanged = target.size != count
    val snap = !hasDrawn || reducedMotion || epochChanged || countChanged

    val newTarget = DoubleArray(count) { clampFrac(percents[it]) }

    if (snap) {
      animator?.cancel()
      current = newTarget.copyOf()
      target = newTarget
      lastEpoch = epoch
      hasDrawn = true
      view.invalidate()
      return
    }

    // Animate each row from its current value to the new target.
    if (current.size != count) current = current.copyOf(count)
    start = current.copyOf()
    target = newTarget
    lastEpoch = epoch

    animator?.cancel()
    animator = ValueAnimator.ofFloat(0f, 1f).apply {
      duration = DURATION_MS
      interpolator = EASE_OUT_CUBIC
      addUpdateListener { a ->
        if (isDisposed) return@addUpdateListener
        val f = a.animatedFraction
        for (i in current.indices) {
          val s = if (i < start.size) start[i] else 0.0
          val t = if (i < target.size) target[i] else 0.0
          current[i] = s + (t - s) * f
        }
        view.invalidate()
      }
      start()
    }
  }

  private fun drawBars(canvas: Canvas) {
    val w = view.width.toFloat()
    if (w <= 0f) return
    val rh = (rowHeight * density).toFloat()
    val mt = (rowMarginTop * density).toFloat()
    val inset = (barInset * density).toFloat()
    val barH = (rh - inset * 2f).coerceAtLeast(0f)
    val isRight = origin == "right"

    for (i in current.indices) {
      val rowTop = mt + i * (rh + mt)
      val top = rowTop + inset
      val bottom = top + barH
      val fillW = w * current[i].toFloat()
      if (fillW <= 0f) continue
      val left = if (isRight) w - fillW else 0f
      val right = if (isRight) w else fillW
      canvas.drawRect(left, top, right, bottom, paint)
    }
  }

  private fun clampFrac(percent: Double): Double =
    (percent.coerceIn(0.0, 100.0)) / 100.0

  override fun dispose() {
    isDisposed = true
    animator?.cancel()
    animator = null
  }

  companion object {
    private const val DURATION_MS = 260L
    private val EASE_OUT_CUBIC = PathInterpolator(0.33f, 1f, 0.68f, 1f)
  }
}
