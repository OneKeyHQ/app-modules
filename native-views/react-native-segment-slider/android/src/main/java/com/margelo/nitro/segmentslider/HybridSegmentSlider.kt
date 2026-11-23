package com.margelo.nitro.segmentslider

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction
import android.widget.SeekBar
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext

/**
 * Native segmented slider. Draws the track, fill bar, segment marks, thumb and
 * value bubble in [View.onDraw] and handles the drag in [View.onTouchEvent], so
 * a drag never crosses the JS bridge per frame. Follows the web variant's
 * semantics, with extra endpoint space for the enlarged drag thumb.
 */
@DoNotStrip
class HybridSegmentSlider(val context: ThemedReactContext) : HybridSegmentSliderSpec() {

  private val density = context.resources.displayMetrics.density

  // Geometry (dp).
  private val thumbSizePx = 16f * density
  private val activeThumbScale = 1.15f
  private val markSizePx = 10f * density
  // Keep the enlarged thumb and node halo inside the horizontal view bounds.
  private val insetPx = 12f * density
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
  private var interactionId = 0.0
  private var thumbScale = 1f
  private var thumbScaleAnimator: ValueAnimator? = null
  private var isDisposed = false
  // Segment node the thumb currently sits on (-1 = none). Drives the halo +
  // a light haptic tick fired once each time the thumb enters a new node.
  private var hoverMarkIndex = -1
  // Per-step tick for `hapticStep`: the step index the value last ticked in. A
  // mark tick in the same commit replaces the step tick.
  private var lastHapticBucket = Double.NaN
  private var markTickedThisCommit = false
  // Step ticks only follow real finger movement, never the touch-down jump.
  private var stepTicksArmed = false
  // Tap-vs-drag tracking for `snapTapToSegment`. A gesture starts as a tap
  // candidate; once the finger travels past `tapMoveThresholdPx` it becomes a
  // drag and the release no longer snaps.
  private var touchStartX = 0f
  private var lastTouchX = 0f
  private var hasDragged = false
  private var hasSelectedValue = false
  private val tapMoveThresholdPx = 8f * density
  // A tap only snaps when it's quick. Holding longer (without dragging) is a
  // long-press and does nothing — matching the competitor's tap window.
  private val tapMaxDurationMs = 300L
  // A tap's press is ~100ms, too short to read the bubble, so after a tap the
  // bubble stays up for this long before fading. Drags still hide on release.
  private val tapBubbleLingerMs = 800L
  // Bubble opacity (0..1), animated so it fades like iOS instead of popping.
  private var pendingTapBubble = false
  private var bubbleAlpha = 0f
  private var bubbleAlphaTarget = 0f
  private var bubbleAlphaAnimator: ValueAnimator? = null
  private val hideBubbleRunnable = Runnable { setBubbleVisible(false) }
  private var accessibilityUpdatePending = false
  private val dispatchAccessibilityUpdate = Runnable {
    accessibilityUpdatePending = false
    if (!isDisposed && view.isAttachedToWindow) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
      } else if (context.getSystemService(AccessibilityManager::class.java)?.isEnabled == true) {
        // TalkBack reads the focused SeekBar's RangeInfo for state-description changes.
        val event = AccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
        event.contentChangeTypes = AccessibilityEvent.CONTENT_CHANGE_TYPE_STATE_DESCRIPTION
        view.sendAccessibilityEventUnchecked(event)
      }
    }
  }

  override val view: View = object : View(context) {
    override fun onDraw(canvas: Canvas) {
      drawSlider(canvas)
    }

    override fun getAccessibilityClassName(): CharSequence = SeekBar::class.java.name

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
      super.onInitializeAccessibilityNodeInfo(info)
      val lower = Math.ceil(min)
      val upper = Math.floor(max)
      if (lower > upper) return

      val value = currentValue.coerceIn(lower, upper)
      info.rangeInfo = AccessibilityNodeInfo.RangeInfo.obtain(
        AccessibilityNodeInfo.RangeInfo.RANGE_TYPE_INT,
        lower.toFloat(),
        upper.toFloat(),
        value.toFloat()
      )
      if (disabled || isDisposed) return

      if (value < upper) info.addAction(AccessibilityAction.ACTION_SCROLL_FORWARD)
      if (value > lower) info.addAction(AccessibilityAction.ACTION_SCROLL_BACKWARD)
      if (lower < upper) info.addAction(AccessibilityAction.ACTION_SET_PROGRESS)
    }

    override fun performAccessibilityAction(action: Int, arguments: Bundle?): Boolean {
      val value = when (action) {
        AccessibilityAction.ACTION_SCROLL_FORWARD.id -> return stepValue(forward = true)
        AccessibilityAction.ACTION_SCROLL_BACKWARD.id -> return stepValue(forward = false)
        AccessibilityAction.ACTION_SET_PROGRESS.id ->
          arguments?.getFloat(AccessibilityNodeInfo.ACTION_ARGUMENT_PROGRESS_VALUE, Float.NaN)
            ?.toDouble() ?: return false
        else -> return super.performAccessibilityAction(action, arguments)
      }
      return adjustValue(value)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
      if (event.hasNoModifiers()) {
        val adjusted = when (keyCode) {
          KeyEvent.KEYCODE_DPAD_LEFT -> stepValue(forward = isRtl)
          KeyEvent.KEYCODE_DPAD_RIGHT -> stepValue(forward = !isRtl)
          KeyEvent.KEYCODE_MOVE_HOME -> adjustValue(min)
          KeyEvent.KEYCODE_MOVE_END -> adjustValue(max)
          else -> false
        }
        if (adjusted) return true
      }
      return super.onKeyDown(keyCode, event)
    }

    override fun onRtlPropertiesChanged(layoutDirection: Int) {
      super.onRtlPropertiesChanged(layoutDirection)
      invalidate()
    }

    override fun onDetachedFromWindow() {
      hideBubbleNow()
      cancelAccessibilityUpdate()
      super.onDetachedFromWindow()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
      if (disabled || isDisposed) return false
      // Use actionMasked so multi-touch pointer events (ACTION_POINTER_UP /
      // pointer-index > 0) still resolve to the right action and never strand
      // the drag state or the disallow-intercept flag.
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
          interactionId += 1
          isDragging = true
          hasDragged = false
          touchStartX = event.x
          lastTouchX = event.x
          stepTicksArmed = false
          onSlideStart?.invoke()
          animateThumbScale(activeThumbScale)
          // Tap-snap mode: defer the value commit until we know this is a tap or
          // a drag, so a tap never jumps to the raw touch point first. The
          // bubble waits too, and a previous tap's bubble is hidden: either
          // would show the old value.
          hasSelectedValue = !snapTapsActive
          if (hasSelectedValue) {
            setBubbleVisible(true)
            commitDragValue(valueFromX(event.x))
          } else {
            hideBubbleNow()
          }
          lastHapticBucket = hapticBucket(currentValue)
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
            if (!(snapTapsActive && !hasDragged)) {
              hasSelectedValue = true
              if (showBubble && bubbleAlphaTarget == 0f) setBubbleVisible(true)
              stepTicksArmed = true
              commitDragValue(valueFromX(event.x))
            }
          }
          return true
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
          if (isDragging) {
            isDragging = false
            val shouldSnap = snapTapsActive
            val isTap = !hasDragged && event.actionMasked == MotionEvent.ACTION_UP &&
              (!shouldSnap || event.eventTime - event.downTime <= tapMaxDurationMs)
            if (isTap && shouldSnap) {
              // Snapping may replace a value already shown during a continuous press.
              if (hasSelectedValue) hideBubbleNow()
              hasSelectedValue = true
              commitDragValue(snapValueToNearestMark(valueFromX(lastTouchX)))
            }
            hoverMarkIndex = -1
            animateThumbScale(1f)
            setBubbleVisible(false)
            pendingTapBubble = isTap && hasSelectedValue && showBubble
            view.invalidate()
            onSlideComplete?.invoke(interactionId, currentValue)
          }
          parent?.requestDisallowInterceptTouchEvent(false)
          return true
        }
      }
      return super.onTouchEvent(event)
    }
  }.apply {
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    isFocusable = true
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
    set(v) {
      if (field == v) return
      field = v
      invalidateIfAlive()
      scheduleAccessibilityUpdate()
    }

  override var max: Double = 100.0
    get() = field
    set(v) {
      if (field == v) return
      field = v
      invalidateIfAlive()
      scheduleAccessibilityUpdate()
    }

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
      if (v) {
        pendingTapBubble = false
        cancelInteraction()
      }
      view.alpha = if (v) 0.5f else 1f
      view.isEnabled = !v
      invalidateIfAlive()
    }

  override var sliderAccessibilityLabel: String = ""
    set(value) {
      field = value
      view.contentDescription = value.ifEmpty { null }
    }

  override var showBubble: Boolean = true
    get() = field
    set(v) {
      field = v
      if (!v) {
        // Drop a visible or lingering bubble so re-enabling never resurrects it.
        hideBubbleNow()
      } else if (isDragging && hasSelectedValue) {
        // A drag already under way shows its bubble again; a snap-mode tap
        // candidate stays hidden until it becomes a drag or lands.
        setBubbleVisible(true)
      }
      invalidateIfAlive()
    }

  override var centerOrigin: Boolean = false
    get() = field
    set(v) { field = v; invalidateIfAlive() }

  // Gates tap snapping only; no visual change on its own.
  override var snapTapToSegment: Boolean = false
    get() = field
    set(v) { field = v }

  // Gates the per-step tick only; no visual change.
  override var hapticStep: Double = 0.0
    get() = field
    set(v) { field = v }

  /** Test seam (not a Nitro prop): receives "mark" or "step" for each tick. */
  internal var hapticTickRecorder: ((String) -> Unit)? = null

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
  override var onSlideComplete: ((interactionId: Double, finalValue: Double) -> Unit)? = null

  // MARK: - Imperative methods
  // Set the value from JS without a Fabric prop commit. Called on the JS
  // thread, so hop to the UI thread before touching the view / drawing state
  // (mirrors react-native-perp-depth-bar's imperative methods). Does NOT fire
  // onChange — syncToValue updates lastEmitted so the caller's own value never
  // echoes back.
  override fun setValue(value: Double, interactionId: Double) {
    view.post {
      if (!isDisposed && !isDragging && this.interactionId == interactionId) {
        val acceptedTap = pendingTapBubble && value == currentValue
        pendingTapBubble = false
        syncToValue(value)
        if (acceptedTap) lingerThenHideBubble()
      }
    }
  }

  private fun parse(v: String) = SegmentSliderColorParser.parse(v)

  private fun invalidateIfAlive() {
    if (!isDisposed) view.invalidate()
  }

  // MARK: - Geometry helpers
  private val isRtl: Boolean get() = view.layoutDirection == View.LAYOUT_DIRECTION_RTL
  private val trackWidth: Float get() = (view.width - insetPx * 2f).coerceAtLeast(0f)
  private val rangeSafe: Double get() = (max - min).let { if (it == 0.0) 1.0 else it }
  private val markCount: Int get() = if (segments >= 1) segments.toInt() + 1 else 0

  /** True when tap-snapping should defer the value commit: enabled with marks to snap to. */
  private val snapTapsActive: Boolean get() = snapTapToSegment && markCount > 1

  private fun valueToFrac(v: Double): Float {
    val clamped = v.coerceIn(min, max)
    return ((clamped - min) / rangeSafe).toFloat()
  }

  private fun xFromFraction(fraction: Float): Float =
    insetPx + (if (isRtl) 1f - fraction else fraction) * trackWidth

  private fun valueFromX(x: Float): Double {
    val w = trackWidth
    if (w <= 0f) return currentValue
    val clampedX = x.coerceIn(insetPx, insetPx + w)
    val frac = ((clampedX - insetPx) / w).toDouble()
    return min + (if (isRtl) 1.0 - frac else frac) * (max - min)
  }

  /** Snaps a value to the value of the nearest segment mark. Caller guarantees `markCount > 1`. */
  private fun snapValueToNearestMark(v: Double): Double {
    val frac = valueToFrac(v).toDouble()
    val idx = Math.round(frac * (markCount - 1)).toDouble()
    val snappedFrac = idx / (markCount - 1)
    return min + snappedFrac * (max - min)
  }

  private fun scheduleAccessibilityUpdate() {
    if (isDisposed || !view.isAttachedToWindow || accessibilityUpdatePending) return
    accessibilityUpdatePending = view.postDelayed(dispatchAccessibilityUpdate, 100L)
  }

  private fun cancelAccessibilityUpdate() {
    view.removeCallbacks(dispatchAccessibilityUpdate)
    accessibilityUpdatePending = false
  }

  /** Updates the idle view without emitting an interaction callback. */
  private fun syncToValue(v: Double) {
    if (isDisposed) return
    hoverMarkIndex = -1
    val changed = currentValue != v
    // A tap's lingering bubble shows that tap's value; an unrelated update ends it.
    if (changed) dropIdleBubble()
    currentValue = v
    lastEmitted = v
    view.invalidate()
    if (changed) scheduleAccessibilityUpdate()
  }

  private fun stepValue(forward: Boolean): Boolean {
    val next = if (forward) Math.floor(currentValue) + 1 else Math.ceil(currentValue) - 1
    if (next < Math.ceil(min) || next > Math.floor(max)) return false
    return adjustValue(next)
  }

  private fun adjustValue(raw: Double): Boolean {
    if (disabled || isDisposed || isDragging || !raw.isFinite() ||
      Math.ceil(min) > Math.floor(max)
    ) return false

    val value = roundToRange(raw)
    if (value == currentValue) return false
    dropIdleBubble()
    interactionId += 1
    onSlideStart?.invoke()
    commitDragValue(value)
    onSlideComplete?.invoke(interactionId, currentValue)
    return true
  }

  private fun commitDragValue(raw: Double) {
    val rounded = roundToRange(raw)
    val changed = currentValue != rounded
    currentValue = rounded
    if (changed) scheduleAccessibilityUpdate()
    markTickedThisCommit = false
    updateHover()
    tickStepIfNeeded(rounded)
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
    val thumbX = xFromFraction(valueToFrac(currentValue))
    val threshold = 12f * density // half of the 24dp mark hit area
    var best = -1
    var bestDist = threshold
    for (i in 0 until markCount) {
      val mf = if (markCount > 1) i.toFloat() / (markCount - 1) else 0f
      val mx = xFromFraction(mf)
      val d = Math.abs(thumbX - mx)
      if (d <= bestDist) { bestDist = d; best = i }
    }
    if (best != hoverMarkIndex) {
      hoverMarkIndex = best
      if (best != -1) {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        hapticTickRecorder?.invoke("mark")
        markTickedThisCommit = true
      }
    }
  }

  /// Rounds to an integer inside the range. With fractional bounds the result
  /// stays within [ceil(min), floor(max)] so it never passes a bound.
  private fun roundToRange(raw: Double): Double {
    val rounded = Math.round(raw.coerceIn(min, max)).toDouble()
    val lo = Math.ceil(min)
    val hi = Math.floor(max)
    if (lo > hi) return rounded
    return rounded.coerceIn(lo, hi)
  }

  /// Ends an active drag without a release (e.g. the view became disabled):
  /// no tap snap, visuals reset, parent interception restored, and
  /// onSlideComplete fires so the JS wrapper resumes value sync.
  private fun cancelInteraction() {
    if (!isDragging) return
    isDragging = false
    stepTicksArmed = false
    hoverMarkIndex = -1
    animateThumbScale(1f)
    hideBubbleNow()
    view.parent?.requestDisallowInterceptTouchEvent(false)
    view.invalidate()
    onSlideComplete?.invoke(interactionId, currentValue)
  }

  /// Step index of `v` for `hapticStep`; NaN when per-step ticks are off.
  private fun hapticBucket(v: Double): Double =
    if (hapticStep > 0) Math.floor((v - min) / hapticStep) else Double.NaN

  /// One light tick when a drag moves the value into a new step bucket. Skipped
  /// when the mark tick already fired for this value (the mark tick wins).
  private fun tickStepIfNeeded(v: Double) {
    if (!isDragging || !stepTicksArmed || hapticStep <= 0) return
    val bucket = hapticBucket(v)
    if (bucket == lastHapticBucket) return
    lastHapticBucket = bucket
    if (markTickedThisCommit) return
    val constant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      HapticFeedbackConstants.TEXT_HANDLE_MOVE
    } else {
      HapticFeedbackConstants.CLOCK_TICK
    }
    view.performHapticFeedback(constant)
    hapticTickRecorder?.invoke("step")
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

  private fun cancelBubbleHide() {
    view.removeCallbacks(hideBubbleRunnable)
  }

  /// Removes the bubble at once, without a fade.
  private fun hideBubbleNow() {
    pendingTapBubble = false
    cancelBubbleHide()
    bubbleAlphaAnimator?.cancel()
    bubbleAlpha = 0f
    bubbleAlphaTarget = 0f
    invalidateIfAlive()
  }

  /// An idle bubble belongs to the tap that showed it (lingering or fading).
  /// Remove it before another value is drawn so it never shows that value.
  private fun dropIdleBubble() {
    if (!isDragging) hideBubbleNow()
  }

  /// Shows the bubble (with the value the tap landed on) and fades it out
  /// after [tapBubbleLingerMs]. A new touch cancels the pending fade.
  private fun lingerThenHideBubble() {
    if (!showBubble || isDisposed) return
    setBubbleVisible(true)
    view.postDelayed(hideBubbleRunnable, tapBubbleLingerMs)
  }

  private fun setBubbleVisible(visible: Boolean) {
    pendingTapBubble = false
    cancelBubbleHide()
    if (isDisposed) return
    val target = if (visible && showBubble) 1f else 0f
    if (target == bubbleAlphaTarget) return
    bubbleAlphaTarget = target
    bubbleAlphaAnimator?.cancel()
    bubbleAlphaAnimator = ValueAnimator.ofFloat(bubbleAlpha, target).apply {
      duration = 150L
      addUpdateListener { a ->
        if (isDisposed) return@addUpdateListener
        bubbleAlpha = a.animatedValue as Float
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
    val thumbX = xFromFraction(frac)
    val originX = xFromFraction(if (centerOrigin) valueToFrac(0.0) else 0f)
    if (originX != thumbX) {
      rect.set(minOf(originX, thumbX), trackTop, maxOf(originX, thumbX), trackTop + h)
      canvas.drawRoundRect(rect, radius, radius, fillPaint)
    }

    // Node halo (drag-time highlight) under the marks and thumb, so the pressed
    // thumb stays plain (web parity). Translucent markActive color (25% alpha).
    if (isDragging && hoverMarkIndex in 0 until markCount) {
      val mf = if (markCount > 1) hoverMarkIndex.toFloat() / (markCount - 1) else 0f
      val hx = xFromFraction(mf)
      markHaloPaint.color = (cMarkActive and 0x00FFFFFF) or (0x3F shl 24)
      canvas.drawCircle(hx, midY, markSizePx / 2f + haloRingPx, markHaloPaint)
    }

    // Marks (under the thumb).
    val centerFrac = valueToFrac(0.0)
    val markR = markSizePx / 2f
    for (i in 0 until markCount) {
      val markFrac = if (markCount > 1) i.toFloat() / (markCount - 1) else 0f
      val cx = xFromFraction(markFrac)
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

    // Thumb (drawn ABOVE the marks so it covers the node it sits on, matching
    // web where the thumb zIndex 4 sits above the mark zIndex 3).
    val thumbR = thumbSizePx / 2f * thumbScale
    canvas.drawCircle(thumbX, midY, thumbR, thumbFillPaint)
    canvas.drawCircle(thumbX, midY, thumbR - density / 2f, thumbStrokePaint)

    // Bubble.
    if (showBubble && bubbleAlpha > 0f) {
      val text = "${Math.round(currentValue)}%"
      val textW = bubbleTextPaint.measureText(text)
      val padH = 8f * density
      val bw = textW + padH * 2
      val bh = 20f * density
      val left = (thumbX - bw / 2f).coerceIn(0f, (w - bw).coerceAtLeast(0f))
      val top = trackTop - 8f * density - bh
      rect.set(left, top, left + bw, top + bh)
      // Fade through an offscreen layer only while animating, so the bubble
      // and its text fade as one and the paints keep their own colors.
      val fading = bubbleAlpha < 1f
      if (fading) canvas.saveLayerAlpha(rect, Math.round(bubbleAlpha * 255))
      canvas.drawRoundRect(rect, 4f * density, 4f * density, bubblePaint)
      val baseline = top + bh / 2f - (bubbleTextPaint.descent() + bubbleTextPaint.ascent()) / 2f
      canvas.drawText(text, left + bw / 2f, baseline, bubbleTextPaint)
      if (fading) canvas.restore()
    }
  }

  override fun afterUpdate() {
    super.afterUpdate()
    invalidateIfAlive()
  }

  override fun dispose() {
    isDisposed = true
    cancelAccessibilityUpdate()
    thumbScaleAnimator?.cancel()
    thumbScaleAnimator = null
    cancelBubbleHide()
    bubbleAlphaAnimator?.cancel()
    bubbleAlphaAnimator = null
  }
}
