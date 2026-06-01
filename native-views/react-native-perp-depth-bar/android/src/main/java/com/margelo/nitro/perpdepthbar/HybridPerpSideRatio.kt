package com.margelo.nitro.perpdepthbar

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.view.animation.PathInterpolator
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext

/** Two horizontal segments whose widths animate to the bid/ask ratio. */
@DoNotStrip
class HybridPerpSideRatio(val context: ThemedReactContext) : HybridPerpSideRatioSpec() {

  private val density = context.resources.displayMetrics.density
  private val bidPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val askPaint = Paint(Paint.ANTI_ALIAS_FLAG)

  // Animated split fraction (bid share of the available width), 0..1.
  private var currentSplit = 0.5
  private var startSplit = 0.5
  private var targetSplit = 0.5
  private var hasDrawn = false
  private var isDisposed = false
  private var animator: ValueAnimator? = null

  override val view: View = object : View(context) {
    override fun onDraw(canvas: Canvas) {
      drawSegments(canvas)
    }
  }.apply {
    isClickable = false
    isFocusable = false
  }

  override var bidPercentage: Double = 50.0
    get() = field
    set(value) { field = value }

  override var askPercentage: Double = 50.0
    get() = field
    set(value) { field = value }

  override var segmentHeight: Double = 4.0
    get() = field
    set(value) { if (!isDisposed) { field = value; view.invalidate() } }

  override var cornerRadius: Double = 999.0
    get() = field
    set(value) { if (!isDisposed) { field = value; view.invalidate() } }

  override var gap: Double = 2.0
    get() = field
    set(value) { if (!isDisposed) { field = value; view.invalidate() } }

  override var reducedMotion: Boolean = false
    get() = field
    set(value) { field = value }

  override var longColor: String = ""
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      bidPaint.color = PerpColorParser.parse(value)
      view.invalidate()
    }

  override var shortColor: String = ""
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      askPaint.color = PerpColorParser.parse(value)
      view.invalidate()
    }

  override fun afterUpdate() {
    super.afterUpdate()
    if (isDisposed) return
    val bid = maxOf(bidPercentage, 1.0)
    val ask = maxOf(askPercentage, 1.0)
    val split = bid / (bid + ask)
    val snap = !hasDrawn || reducedMotion
    if (snap) {
      animator?.cancel()
      currentSplit = split
      targetSplit = split
      hasDrawn = true
      view.invalidate()
      return
    }
    startSplit = currentSplit
    targetSplit = split
    animator?.cancel()
    animator = ValueAnimator.ofFloat(0f, 1f).apply {
      duration = DURATION_MS
      interpolator = EASE_OUT_CUBIC
      addUpdateListener { a ->
        if (isDisposed) return@addUpdateListener
        currentSplit = startSplit + (targetSplit - startSplit) * a.animatedFraction
        view.invalidate()
      }
      start()
    }
  }

  private fun drawSegments(canvas: Canvas) {
    val w = view.width.toFloat()
    if (w <= 0f) return
    val h = (segmentHeight * density).toFloat()
    val g = (gap * density).toFloat()
    val y = (view.height - h) / 2f
    val available = (w - g).coerceAtLeast(0f)
    val bidW = available * currentSplit.toFloat()
    val askW = available - bidW
    val r = (cornerRadius * density).toFloat().coerceAtMost(h / 2f)

    if (bidW > 0f) {
      canvas.drawRoundRect(RectF(0f, y, bidW, y + h), r, r, bidPaint)
    }
    if (askW > 0f) {
      val askLeft = bidW + g
      canvas.drawRoundRect(RectF(askLeft, y, askLeft + askW, y + h), r, r, askPaint)
    }
  }

  override fun dispose() {
    isDisposed = true
    animator?.cancel()
    animator = null
  }

  companion object {
    private const val DURATION_MS = 300L
    private val EASE_OUT_CUBIC = PathInterpolator(0.33f, 1f, 0.68f, 1f)
  }
}
