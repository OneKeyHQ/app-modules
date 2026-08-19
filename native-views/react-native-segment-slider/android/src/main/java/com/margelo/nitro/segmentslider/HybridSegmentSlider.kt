package com.margelo.nitro.segmentslider

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext

/**
 * Native segmented slider. Draws the track, fill bar, segment marks, thumb and
 * value bubble in [View.onDraw] and handles the drag in [View.onTouchEvent], so
 * a drag never crosses the JS bridge per frame. Mirrors the web variant
 * (`composite/SegmentSlider/index.tsx`) pixel-for-pixel.
 */
@DoNotStrip
class HybridSegmentSlider(val context: ThemedReactContext) : HybridSegmentSliderSpec() {

  private val density = context.resources.displayMetrics.density

  // Geometry (dp), identical to the web constants.
  private val thumbSizePx = 16f * density
  private val markSizePx = 10f * density
  private val insetPx = thumbSizePx / 2f
  private val haloRingPx = 4f * density // translucent halo width beyond each mark

  // Paints.
  private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
  private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
  private val thumbFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
  private val thumbStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.STROKE; strokeWidth = density
  }
  private val markFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
  private val markStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.STROKE; strokeWidth = density
  }
  // Translucent highlight around the node the thumb is over while dragging.
  private val markHaloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
  private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
  private val bubbleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    textAlign = Paint.Align.CENTER
    textSize = 12f * density
    isFakeBoldText = true
  }
  private val rect = RectF()

  // Cached colors.
  private var cFill = 0
  private var cTrack = 0
  private var cThumb = 0
  private var cThumbBorder = 0
  private var cMarkActive = 0
  private var cMarkInactive = 0
  private var cMarkBorder = 0

  // Interaction state.
  private var currentValue = 0.0
  private var lastEmitted = Double.NaN
  private var isDragging = false
  private var thumbScale = 1f
  private var thumbScaleAnimator: ValueAnimator? = null
  private var isDisposed = false
  // Segment node the thumb currently sits on (-1 = none). Drives the halo +
  // a light haptic tick fired once each time the thumb enters a new node.
  private var hoverMarkIndex = -1
  // Tap-vs-drag tracking for `snapTapToSegment`. A gesture starts as a tap
  // candidate; once the finger travels past `tapMoveThresholdPx` it becomes a
  // drag and the release no longer snaps.
  private var touchStartX = 0f
  private var lastTouchX = 0f
  private var hasDragged = false
  private val tapMoveThresholdPx = 8f * density
  // A tap only snaps when it's quick. Holding longer (without dragging) is a
  // long-press and does nothing — matching the competitor's tap window.
  private val tapMaxDurationMs = 300L

  override val view: View = object : View(context) {
    override fun onDraw(canvas: Canvas) {
      drawSlider(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
      if (disabled || isDisposed) return false
      // Use actionMasked so multi-touch pointer events (ACTION_POINTER_UP /
      // pointer-index > 0) still resolve to the right action and never strand
      // the drag state or the disallow-intercept flag.
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
          isDragging = true
          hasDragged = false
          touchStartX = event.x
          lastTouchX = event.x
          onSlideStart?.invoke()
          animateThumbScale(1.15f)
          // Tap-snap mode: defer the value commit until we know this is a tap or
          // a drag, so a tap never jumps to the raw touch point first.
          if (!snapTapsActive) commitDragValue(valueFromX(event.x))
          parent?.requestDisallowInterceptTouchEvent(true)
          return true
        }
        MotionEvent.ACTION_MOVE -> {
          if (isDragging) {
            lastTouchX = event.x
            if (!hasDragged && Math.abs(event.x - touchStartX) > tapMoveThresholdPx) {
              hasDragged = true
            }
            // Still a tap candidate in snap mode → keep the thumb put.
            if (!(snapTapsActive && !hasDragged)) commitDragValue(valueFromX(event.x))
          }
          return true
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
          if (isDragging) {
            isDragging = false
            // A quick tap (no drag) lands directly on the nearest segment node
            // in a single move — no detour through the raw touch point. A long
            // press (held past tapMaxDurationMs without dragging) does nothing.
            // A drag keeps its free value.
            if (snapTapsActive && !hasDragged &&
              (event.eventTime - event.downTime) <= tapMaxDurationMs
            ) {
              commitDragValue(snapValueToNearestMark(valueFromX(lastTouchX)))
            }
            hoverMarkIndex = -1
            animateThumbScale(1f)
            view.invalidate()
            onSlideComplete?.invoke()
          }
          parent?.requestDisallowInterceptTouchEvent(false)
          return true
        }
      }
      return super.onTouchEvent(event)
    }
  }.apply {
    isClickable = true
    isFocusable = false
    isHapticFeedbackEnabled = true
  }

  // MARK: - Props
  // Guards the apply-once semantics of [defaultValue]: only the first prop
  // commit seeds the initial value. After that the view is uncontrolled and JS
  // drives it via the imperative [setValue] method.
  private var didApplyDefault = false

  override var defaultValue: Double = 0.0
    get() = field
    set(v) {
      field = v
      if (!didApplyDefault) {
        didApplyDefault = true
        syncToValue(v)
      }
    }

  override var min: Double = 0.0
    get() = field
    set(v) { field = v; invalidateIfAlive() }

  override var max: Double = 100.0
    get() = field
    set(v) { field = v; invalidateIfAlive() }

  override var segments: Double = 0.0
    get() = field
    set(v) { field = v; invalidateIfAlive() }

  override var sliderHeight: Double = 4.0
    get() = field
    set(v) { field = v; invalidateIfAlive() }

  override var disabled: Boolean = false
    get() = field
    set(v) {
      field = v
      view.alpha = if (v) 0.5f else 1f
      invalidateIfAlive()
    }

  override var showBubble: Boolean = true
    get() = field
    set(v) { field = v; invalidateIfAlive() }

  override var centerOrigin: Boolean = false
    get() = field
    set(v) { field = v; invalidateIfAlive() }

  // Gates tap snapping only; no visual change on its own.
  override var snapTapToSegment: Boolean = false
    get() = field
    set(v) { field = v }

  override var alignSegmentMarksToIntegerValues: Boolean = false
    get() = field
    set(v) { field = v; invalidateIfAlive() }

  override var fillColor: String = ""
    get() = field
    set(v) { field = v; cFill = parse(v); fillPaint.color = cFill; invalidateIfAlive() }

  override var trackColor: String = ""
    get() = field
    set(v) { field = v; cTrack = parse(v); trackPaint.color = cTrack; invalidateIfAlive() }

  override var thumbColor: String = ""
    get() = field
    set(v) { field = v; cThumb = parse(v); thumbFillPaint.color = cThumb; invalidateIfAlive() }

  override var thumbBorderColor: String = ""
    get() = field
    set(v) { field = v; cThumbBorder = parse(v); thumbStrokePaint.color = cThumbBorder; invalidateIfAlive() }

  override var markActiveColor: String = ""
    get() = field
    set(v) { field = v; cMarkActive = parse(v); invalidateIfAlive() }

  override var markInactiveColor: String = ""
    get() = field
    set(v) { field = v; cMarkInactive = parse(v); invalidateIfAlive() }

  override var markBorderColor: String = ""
    get() = field
    set(v) { field = v; cMarkBorder = parse(v); invalidateIfAlive() }

  override var bubbleColor: String = ""
    get() = field
    set(v) { field = v; bubblePaint.color = parse(v); invalidateIfAlive() }

  override var bubbleTextColor: String = ""
    get() = field
    set(v) { field = v; bubbleTextPaint.color = parse(v); invalidateIfAlive() }

  // MARK: - Callbacks
  override var onChange: ((value: Double) -> Unit)? = null
  override var onSlideStart: (() -> Unit)? = null
  override var onSlideComplete: (() -> Unit)? = null

  // MARK: - Imperative methods
  // Set the value from JS without a Fabric prop commit. Called on the JS
  // thread, so hop to the UI thread before touching the view / drawing state
  // (mirrors react-native-perp-depth-bar's imperative methods). Does NOT fire
  // onChange — syncToValue updates lastEmitted so the caller's own value never
  // echoes back.
  override fun setValue(value: Double) {
    view.post {
      if (!isDisposed) syncToValue(value)
    }
  }

  private fun parse(v: String) = SegmentSliderColorParser.parse(v)

  private fun invalidateIfAlive() {
    if (!isDisposed) view.invalidate()
  }

  // MARK: - Geometry helpers
  private val trackWidth: Float get() = (view.width - thumbSizePx).coerceAtLeast(0f)
  private val rangeSafe: Double get() = (max - min).let { if (it == 0.0) 1.0 else it }
  private val markCount: Int get() = if (segments >= 1) segments.toInt() + 1 else 0

  private fun segmentValue(index: Int): Double {
    if (markCount <= 1) return min
    val fraction = index.toDouble() / (markCount - 1)
    val value = min + fraction * (max - min)
    return if (alignSegmentMarksToIntegerValues) Math.rint(value) else value
  }

  private fun markFraction(index: Int): Float = valueToFrac(segmentValue(index))

  private fun nearestMarkIndex(value: Double): Int {
    val valueFraction = valueToFrac(value)
    var nearestIndex = 0
    var nearestDistance = Float.POSITIVE_INFINITY
    for (index in 0 until markCount) {
      val distance = Math.abs(valueFraction - markFraction(index))
      if (distance <= nearestDistance) {
        nearestDistance = distance
        nearestIndex = index
      }
    }
    return nearestIndex
  }

  /** True when tap-snapping should defer the value commit: enabled with marks to snap to. */
  private val snapTapsActive: Boolean get() = snapTapToSegment && markCount > 1

  private fun valueToFrac(v: Double): Float {
    val clamped = v.coerceIn(min, max)
    return ((clamped - min) / rangeSafe).toFloat()
  }

  private fun valueFromX(x: Float): Double {
    val w = trackWidth
    if (w <= 0f) return currentValue
    val clampedX = x.coerceIn(insetPx, insetPx + w)
    val frac = ((clampedX - insetPx) / w).toDouble()
    return min + frac * (max - min)
  }

  /** Snaps a value to the value of the nearest segment mark. Caller guarantees `markCount > 1`. */
  private fun snapValueToNearestMark(v: Double): Double {
    return segmentValue(nearestMarkIndex(v))
  }

  /** Hard re-sync: snap the thumb to [v], overriding any in-flight drag. */
  private fun syncToValue(v: Double) {
    if (isDisposed) return
    // An imperative setValue (or the initial defaultValue) wins over the
    // current gesture: drop the drag so a queued ACTION_MOVE can't immediately
    // re-overwrite the thumb, and clear the transient hover/halo so the snapped
    // position renders clean.
    isDragging = false
    hoverMarkIndex = -1
    currentValue = v
    lastEmitted = v
    view.invalidate()
  }

  private fun commitDragValue(raw: Double) {
    val rounded = raw.coerceIn(min, max).let { Math.round(it).toDouble() }
    currentValue = rounded
    updateHover()
    view.invalidate()
    if (rounded != lastEmitted) {
      lastEmitted = rounded
      onChange?.invoke(rounded)
    }
  }

  /// Highlights the node the thumb is over and fires a light haptic tick once
  /// per node entered. Only while dragging; does NOT snap (parity with web/perp).
  private fun updateHover() {
    val tw = trackWidth
    if (!isDragging || markCount <= 0 || tw <= 0f) {
      hoverMarkIndex = -1
      return
    }
    val thumbX = insetPx + valueToFrac(currentValue) * tw
    val threshold = 12f * density // half of the 24dp mark hit area
    var best = -1
    var bestDist = threshold
    for (i in 0 until markCount) {
      val mf = markFraction(i)
      val mx = insetPx + mf * tw
      val d = Math.abs(thumbX - mx)
      if (d <= bestDist) { bestDist = d; best = i }
    }
    if (best != hoverMarkIndex) {
      hoverMarkIndex = best
      if (best != -1) {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
      }
    }
  }

  private fun animateThumbScale(target: Float) {
    thumbScaleAnimator?.cancel()
    thumbScaleAnimator = ValueAnimator.ofFloat(thumbScale, target).apply {
      duration = 120L
      addUpdateListener { a ->
        if (isDisposed) return@addUpdateListener
        thumbScale = a.animatedValue as Float
        view.invalidate()
      }
      start()
    }
  }

  // MARK: - Drawing
  private fun drawSlider(canvas: Canvas) {
    val w = view.width.toFloat()
    if (w <= 0f) return
    val h = (sliderHeight * density).toFloat()
    val midY = view.height / 2f
    val trackTop = midY - h / 2f
    val tw = trackWidth
    val frac = valueToFrac(currentValue)
    val radius = h / 2f

    // Track.
    rect.set(insetPx, trackTop, insetPx + tw, trackTop + h)
    canvas.drawRoundRect(rect, radius, radius, trackPaint)

    // Fill.
    if (centerOrigin) {
      if (currentValue != 0.0) {
        val centerFrac = valueToFrac(0.0)
        val leftFrac = minOf(frac, centerFrac)
        val widthFrac = Math.abs(frac - centerFrac)
        rect.set(insetPx + leftFrac * tw, trackTop, insetPx + (leftFrac + widthFrac) * tw, trackTop + h)
        canvas.drawRoundRect(rect, radius, radius, fillPaint)
      }
    } else if (frac > 0f) {
      rect.set(insetPx, trackTop, insetPx + frac * tw, trackTop + h)
      canvas.drawRoundRect(rect, radius, radius, fillPaint)
    }

    // Thumb (drawn BELOW the marks so a node under the thumb shows through its
    // centre — the "white ring + dark centre" bullseye, matching web where the
    // mark zIndex 3 sits above the thumb zIndex 2).
    val thumbX = insetPx + frac * tw
    val thumbR = thumbSizePx / 2f * thumbScale
    canvas.drawCircle(thumbX, midY, thumbR, thumbFillPaint)
    canvas.drawCircle(thumbX, midY, thumbR - density / 2f, thumbStrokePaint)

    // Node halo (drag-time highlight) — drawn under the marks so the node dot
    // stays crisp on top. Translucent markActive color (25% alpha).
    if (isDragging && hoverMarkIndex in 0 until markCount) {
      val mf = markFraction(hoverMarkIndex)
      val hx = insetPx + mf * tw
      markHaloPaint.color = (cMarkActive and 0x00FFFFFF) or (0x3F shl 24)
      canvas.drawCircle(hx, midY, markSizePx / 2f + haloRingPx, markHaloPaint)
    }

    // Marks (on top of the thumb).
    val centerFrac = valueToFrac(0.0)
    val markR = markSizePx / 2f
    for (i in 0 until markCount) {
      val markFrac = markFraction(i)
      val cx = insetPx + markFrac * tw
      val active = when {
        centerOrigin && currentValue == 0.0 -> false
        centerOrigin && currentValue > 0 -> markFrac > centerFrac && markFrac <= frac
        centerOrigin && currentValue < 0 -> markFrac >= frac && markFrac < centerFrac
        else -> markFrac <= frac
      }
      markFillPaint.color = if (active) cMarkActive else cMarkInactive
      markStrokePaint.color = if (active) cMarkActive else cMarkBorder
      canvas.drawCircle(cx, midY, markR - density / 2f, markFillPaint)
      canvas.drawCircle(cx, midY, markR - density / 2f, markStrokePaint)
    }

    // Bubble.
    if (isDragging && showBubble) {
      val text = "${Math.round(currentValue)}%"
      val textW = bubbleTextPaint.measureText(text)
      val padH = 8f * density
      val bw = textW + padH * 2
      val bh = 20f * density
      val left = thumbX - bw / 2f
      val top = trackTop - 8f * density - bh
      rect.set(left, top, left + bw, top + bh)
      canvas.drawRoundRect(rect, 4f * density, 4f * density, bubblePaint)
      val baseline = top + bh / 2f - (bubbleTextPaint.descent() + bubbleTextPaint.ascent()) / 2f
      canvas.drawText(text, thumbX, baseline, bubbleTextPaint)
    }
  }

  override fun afterUpdate() {
    super.afterUpdate()
    invalidateIfAlive()
  }

  override fun dispose() {
    isDisposed = true
    thumbScaleAnimator?.cancel()
    thumbScaleAnimator = null
  }
}
