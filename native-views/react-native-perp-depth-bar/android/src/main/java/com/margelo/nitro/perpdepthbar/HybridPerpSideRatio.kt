package com.margelo.nitro.perpdepthbar

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.Choreographer
import android.view.View
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext
import kotlin.math.abs
import kotlin.math.exp

/**
 * Two horizontal segments whose widths ease to the bid/ask ratio.
 *
 * Animation model mirrors the depth bars: a [Choreographer] frame loop
 * continuously eases the split fraction toward its latest target (short fixed
 * exponential smoothing). New data just retargets; the loop stops once settled.
 */
@DoNotStrip
class HybridPerpSideRatio(val context: ThemedReactContext) : HybridPerpSideRatioSpec() {

  private val density = context.resources.displayMetrics.density
  private val bidPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val askPaint = Paint(Paint.ANTI_ALIAS_FLAG)

  // Animated split fraction (bid share of the available width), 0..1.
  private var currentSplit = 0.5
  private var targetSplit = 0.5
  private var hasDrawn = false
  private var isDisposed = false
  // Set once the caller drives the ratio through `setRatio`. The
  // `bidPercentage`/`askPercentage` props are then ignored in `afterUpdate`,
  // otherwise every prop commit would reset the split back to the (static)
  // initial prop values (REACT-NATIVE-1JZ).
  private var imperativeRatio = false

  private val choreographer = Choreographer.getInstance()
  private var frameScheduled = false
  private var lastFrameNanos = 0L
  private val frameCallback = Choreographer.FrameCallback { onFrame(it) }

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

  /** Kept for OS "reduce motion" accessibility only — NOT a caller on/off knob. */
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

  /**
   * Imperative ratio update — bypasses the high-frequency
   * `bidPercentage`/`askPercentage` prop write path (props/JSICache lock
   * contention, REACT-NATIVE-1JZ). Companion to [HybridPerpDepthBars.setDepth].
   * The two args carry the SAME 0..100 semantics as the props of the same name;
   * we store them exactly like the prop setters and then route through the
   * identical [afterUpdate] clamp/split/frame-loop logic the prop path uses —
   * only the data source differs.
   */
  override fun setRatio(bidPercentage: Double, askPercentage: Double) {
    if (isDisposed) return
    imperativeRatio = true
    // Apply the args directly — do NOT route through the `bidPercentage`/
    // `askPercentage` prop fields, which a later prop commit could overwrite with
    // the static initial values.
    applyRatio(bidPercentage, askPercentage)
  }

  override fun afterUpdate() {
    super.afterUpdate()
    if (isDisposed) return
    // In imperative mode the ratio comes from `setRatio`; ignore the (static)
    // props so a prop commit can't reset the bar to the initial values.
    if (imperativeRatio) return
    applyRatio(bidPercentage, askPercentage)
  }

  private fun applyRatio(bidPercentage: Double, askPercentage: Double) {
    val bid = maxOf(bidPercentage, 1.0)
    val ask = maxOf(askPercentage, 1.0)
    val split = bid / (bid + ask)
    val snap = !hasDrawn || reducedMotion
    if (snap) {
      cancelFrameLoop()
      currentSplit = split
      targetSplit = split
      hasDrawn = true
      view.invalidate()
      return
    }
    targetSplit = split
    scheduleFrameLoop()
  }

  // MARK: - Continuous easing (Choreographer)
  private fun scheduleFrameLoop() {
    if (frameScheduled || isDisposed) return
    if (abs(targetSplit - currentSplit) <= 0.0005) { view.invalidate(); return }
    frameScheduled = true
    lastFrameNanos = 0L
    choreographer.postFrameCallback(frameCallback)
  }

  private fun cancelFrameLoop() {
    if (frameScheduled) {
      choreographer.removeFrameCallback(frameCallback)
      frameScheduled = false
    }
    lastFrameNanos = 0L
  }

  private fun onFrame(now: Long) {
    if (isDisposed) { frameScheduled = false; return }
    val dt = if (lastFrameNanos > 0L) (now - lastFrameNanos) / 1e9 else 1.0 / 60.0
    lastFrameNanos = now
    val alpha = 1.0 - exp(-maxOf(dt, 0.0) / TAU_SECONDS)

    val d = targetSplit - currentSplit
    if (abs(d) <= 0.0005) {
      currentSplit = targetSplit
      view.invalidate()
      frameScheduled = false
      lastFrameNanos = 0L
      return
    }
    currentSplit += d * alpha
    view.invalidate()
    choreographer.postFrameCallback(frameCallback)
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
    cancelFrameLoop()
  }

  companion object {
    /**
     * Exponential-smoothing time constant (seconds); mirror of
     * PerpTiming.sideRatioSmoothingTauSeconds on iOS.
     */
    private const val TAU_SECONDS = 0.12
  }
}
