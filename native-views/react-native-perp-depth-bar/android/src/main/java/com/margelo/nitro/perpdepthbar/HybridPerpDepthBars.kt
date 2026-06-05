package com.margelo.nitro.perpdepthbar

import android.graphics.Canvas
import android.graphics.Paint
import android.view.Choreographer
import android.view.MotionEvent
import android.view.View
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext
import com.margelo.nitro.core.ArrayBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.exp

/**
 * Renders an entire column of order-book depth bars for one side. Draws N rects
 * whose horizontal fill fraction is eased on the UI thread.
 *
 * Animation model: a [Choreographer] frame loop continuously eases every row's
 * on-screen fill toward its latest target (short fixed exponential smoothing).
 * New data just retargets — the loop keeps gliding and only stops once every row
 * has settled. Updates chain into continuous motion and a fluctuating row count
 * no longer freezes the column (only a coin/tick switch snaps, via `epoch`).
 */
@DoNotStrip
class HybridPerpDepthBars(val context: ThemedReactContext) : HybridPerpDepthBarsSpec() {

  private val density = context.resources.displayMetrics.density
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val pricePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.LEFT }
  private val sizePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.RIGHT }

  private var current = DoubleArray(0) // on-screen fill fraction per row (0..1)
  private var target = DoubleArray(0)  // latest target fill fraction per row

  private var lastEpoch = Double.NaN
  private var hasDrawn = false
  private var isDisposed = false

  private val choreographer = Choreographer.getInstance()
  private var frameScheduled = false
  private var lastFrameNanos = 0L
  private val frameCallback = Choreographer.FrameCallback { onFrame(it) }

  override val view: View = object : View(context) {
    override fun onDraw(canvas: Canvas) {
      drawBars(canvas)
      drawTexts(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
      when (event.action) {
        MotionEvent.ACTION_DOWN -> return true // claim the gesture
        MotionEvent.ACTION_UP -> {
          handleTap(event.y)
          return true
        }
      }
      return super.onTouchEvent(event)
    }
  }.apply {
    isClickable = true
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

  /** Kept for OS "reduce motion" accessibility only — NOT a caller on/off knob. */
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

  // MARK: - Text props
  override var prices: Array<String> = emptyArray()
    get() = field
    set(value) { if (!isDisposed) { field = value; view.invalidate() } }

  override var sizes: Array<String> = emptyArray()
    get() = field
    set(value) { if (!isDisposed) { field = value; view.invalidate() } }

  override var priceColor: String = ""
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      pricePaint.color = PerpColorParser.parse(value)
      view.invalidate()
    }

  override var sizeColor: String = ""
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      sizePaint.color = PerpColorParser.parse(value)
      view.invalidate()
    }

  override var priceFontSize: Double = 11.0
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      pricePaint.textSize = (value * density).toFloat()
      view.invalidate()
    }

  override var sizeFontSize: Double = 11.0
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      sizePaint.textSize = (value * density).toFloat()
      view.invalidate()
    }

  override var textInset: Double = 0.0
    get() = field
    set(value) { if (!isDisposed) { field = value; view.invalidate() } }

  override var onRowPress: ((rowIndex: Double) -> Unit)? = null

  private fun handleTap(y: Float) {
    if (isDisposed) return
    val step = ((rowHeight + rowMarginTop) * density).toFloat()
    if (step <= 0f) return
    val mt = (rowMarginTop * density).toFloat()
    val i = ((y - mt) / step).toInt()
    val count = maxOf(percents.size, maxOf(prices.size, sizes.size))
    if (i in 0 until count) onRowPress?.invoke(i.toDouble())
  }

  /**
   * Called by Nitro once after a prop-update transaction. Recompute targets and
   * (re)start the easing loop here so a multi-prop update retargets once.
   */
  override fun afterUpdate() {
    super.afterUpdate()
    if (isDisposed) return
    syncTargets(percents)
  }

  /**
   * Imperative depth update — bypasses the high-frequency `percents` prop write
   * path (props/JSICache lock contention, REACT-NATIVE-1JZ). `buffer` is a
   * packed little-endian `Float32Array`: one float per row, each the row's depth
   * percentage with the SAME 0..100 semantics as the `percents` prop. The parsed
   * values flow through the identical [syncTargets] clamp/target/frame-loop logic
   * the prop path uses — only the data source differs.
   */
  override fun setDepth(buffer: ArrayBuffer) {
    if (isDisposed) return
    val byteBuffer = buffer.getBuffer(false)
    val count = buffer.size / 4
    val floats = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer()
    val depths = DoubleArray(count) { floats.get(it).toDouble() }
    syncTargets(depths)
  }

  private fun syncTargets(depths: DoubleArray) {
    val count = depths.size
    val epochChanged = epoch != lastEpoch
    val snap = !hasDrawn || reducedMotion || epochChanged
    val newTarget = DoubleArray(count) { clampFrac(depths[it]) }

    if (snap) {
      cancelFrameLoop()
      current = newTarget.copyOf()
      target = newTarget
      lastEpoch = epoch
      hasDrawn = true
      view.invalidate()
      return
    }

    // Keep existing rows easing; brand-new rows appear at their target (no
    // grow-from-zero flash). Then retarget and let the frame loop glide.
    if (current.size != count) {
      current = DoubleArray(count) { i -> if (i < current.size) current[i] else newTarget[i] }
    }
    target = newTarget
    lastEpoch = epoch
    scheduleFrameLoop()
  }

  // MARK: - Continuous easing (Choreographer)
  private fun scheduleFrameLoop() {
    if (frameScheduled || isDisposed) return
    val n = minOf(current.size, target.size)
    var needs = false
    for (i in 0 until n) {
      if (abs(target[i] - current[i]) > 0.001) { needs = true; break }
    }
    if (!needs) { view.invalidate(); return }
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

    var settled = true
    val n = minOf(current.size, target.size)
    for (i in 0 until n) {
      val d = target[i] - current[i]
      if (abs(d) <= 0.001) {
        current[i] = target[i]
      } else {
        current[i] += d * alpha
        settled = false
      }
    }
    view.invalidate()

    if (settled) {
      frameScheduled = false
      lastFrameNanos = 0L
    } else {
      choreographer.postFrameCallback(frameCallback)
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

  private fun drawTexts(canvas: Canvas) {
    if (prices.isEmpty() && sizes.isEmpty()) return
    val w = view.width.toFloat()
    if (w <= 0f) return
    val rh = (rowHeight * density).toFloat()
    val mt = (rowMarginTop * density).toFloat()
    val pad = (textInset * density).toFloat()
    val rightX = w - pad
    val rows = maxOf(prices.size, sizes.size)
    for (i in 0 until rows) {
      val rowTop = mt + i * (rh + mt)
      // Vertically center single-line text within the row.
      if (i < prices.size && prices[i].isNotEmpty()) {
        val baseline = rowTop + rh / 2f - (pricePaint.descent() + pricePaint.ascent()) / 2f
        canvas.drawText(prices[i], pad, baseline, pricePaint)
      }
      if (i < sizes.size && sizes[i].isNotEmpty()) {
        val baseline = rowTop + rh / 2f - (sizePaint.descent() + sizePaint.ascent()) / 2f
        canvas.drawText(sizes[i], rightX, baseline, sizePaint)
      }
    }
  }

  private fun clampFrac(percent: Double): Double =
    (percent.coerceIn(0.0, 100.0)) / 100.0

  override fun dispose() {
    isDisposed = true
    cancelFrameLoop()
  }

  companion object {
    /**
     * Exponential-smoothing time constant (seconds); mirror of
     * PerpTiming.depthBarSmoothingTauSeconds on iOS.
     */
    private const val TAU_SECONDS = 0.10
  }
}
