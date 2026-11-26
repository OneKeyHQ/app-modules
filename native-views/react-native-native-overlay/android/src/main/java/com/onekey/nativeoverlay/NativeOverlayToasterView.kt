package com.onekey.nativeoverlay

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.Event
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// The motion and gesture constants of the toaster this replaces, in dp / ms.
private object ToasterMetrics {
  /** The first toast sits this far under the top inset. */
  const val TOP_OFFSET = 16f
  const val GUTTER = 8f
  const val ANIMATION_MS = 300L
  const val OBSTRUCTION_MS = 220L
  /** A dismissed card is reported closed when the previous renderer unmounted it. */
  const val REMOVE_DELAY_MS = 1000L
  const val ENTER_OVERSHOOT = 50f
  const val FALLBACK_HEIGHT = 50f
  const val SWIPE_START = 10f
  const val SWIPE_DISTANCE = 32f
  const val SWIPE_FLICK_DISTANCE = 12f
  const val SWIPE_FLICK_VELOCITY = 600f
  /** Dragging against the dismiss direction moves the card a quarter as far. */
  const val SWIPE_RESISTANCE = 4f
  const val FALLBACK_MARGIN = 64f
  const val FALLBACK_MAX_PAGE_WIDTH = 640f
  const val LANE_MAX_WIDTH = 360f
  const val LANE_MARGIN = 32.0
  const val CARD_PADDING_HORIZONTAL = 16f
}

/** Rounds half up, the way the replaced layout placed a node in its parent. */
private fun snapHalfUp(value: Double): Int = kotlin.math.floor(value + 0.5 + 1e-4).toInt()

/** Quadratic ease-in-out, the timing curve of the animations this replaces. */
private val toastInterpolator = PathInterpolator(0.455f, 0.03f, 0.515f, 0.955f)

internal class NativeOverlayToastEvent(
  surfaceId: Int,
  viewTag: Int,
  private val toastId: String,
  private val generation: Int,
  private val type: String,
  private val value: String,
) : Event<NativeOverlayToastEvent>(surfaceId, viewTag) {
  override fun getEventName(): String = NAME

  // Every toast event must reach JS; never coalesce them.
  override fun canCoalesce(): Boolean = false

  override fun getEventData(): WritableMap = Arguments.createMap().apply {
    putString("toastId", toastId)
    putInt("generation", generation)
    putString("type", type)
    putString("value", value)
  }

  companion object {
    const val NAME = "topToastEvent"
  }
}

/** Hosts one card: forwards presses and vertical drags to the presenter. */
@SuppressLint("ViewConstructor")
private class ToastSlot(context: Context, val card: NativeOverlayToastCardView) : FrameLayout(context) {
  var onPressChanged: ((Boolean) -> Unit)? = null
  var onDrag: ((Float) -> Unit)? = null
  var onRelease: ((translationY: Float, translationX: Float, velocityY: Float, cancelled: Boolean) -> Unit)? = null
  var onTap: (() -> Unit)? = null

  private var downX = 0f
  private var downY = 0f
  private var dragging = false
  private var tracker: VelocityTracker? = null

  init {
    clipChildren = false
    clipToPadding = false
    addView(card, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
  }

  override fun dispatchTouchEvent(event: MotionEvent): Boolean {
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> onPressChanged?.invoke(true)
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> onPressChanged?.invoke(false)
    }
    return super.dispatchTouchEvent(event)
  }

  private fun track(event: MotionEvent) {
    val current = tracker ?: VelocityTracker.obtain().also { tracker = it }
    current.addMovement(event)
  }

  override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        downX = event.rawX
        downY = event.rawY
        dragging = false
        tracker?.recycle()
        tracker = null
        track(event)
      }
      MotionEvent.ACTION_MOVE -> {
        track(event)
        val dy = event.rawY - downY
        val dx = event.rawX - downX
        val slop = ToasterMetrics.SWIPE_START * resources.displayMetrics.density
        if (!dragging && abs(dy) > slop && abs(dy) > abs(dx)) {
          dragging = true
          parent?.requestDisallowInterceptTouchEvent(true)
        }
      }
    }
    return dragging
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    when (event.actionMasked) {
      MotionEvent.ACTION_MOVE -> {
        track(event)
        val dy = event.rawY - downY
        val dx = event.rawX - downX
        val slop = ToasterMetrics.SWIPE_START * resources.displayMetrics.density
        if (!dragging && abs(dy) > slop && abs(dy) > abs(dx)) dragging = true
        if (dragging) onDrag?.invoke(dy)
      }
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        track(event)
        val cancelled = event.actionMasked == MotionEvent.ACTION_CANCEL
        if (dragging) {
          tracker?.computeCurrentVelocity(1000)
          onRelease?.invoke(event.rawY - downY, event.rawX - downX, tracker?.yVelocity ?: 0f, cancelled)
        } else if (!cancelled) {
          onTap?.invoke()
        }
        dragging = false
        tracker?.recycle()
        tracker = null
      }
    }
    // Claim the gesture so a press on the card's own surface can become a drag.
    return true
  }

  override fun performClick(): Boolean {
    super.performClick()
    onTap?.invoke()
    return true
  }
}

private class ToastEntry(val id: String, var item: ToastItem, val slot: ToastSlot) {
  var leaving = false
  var reason = "programmatic"
  /** Milliseconds of lifetime left, counted while not paused. */
  var remaining: Long? = null
  var startedAt = 0L
  var timer: Runnable? = null
  var removal: Runnable? = null
  var entered = false
  var dragStart = 0f
}

/**
 * Draws the message toasts in the activity's `toast` overlay layer: stacking,
 * timers, swipe and motion all live here, on the main thread.
 */
internal class NativeOverlayToastPresenter(private val hostView: View) {
  var onEvent: ((id: String, generation: Int, type: String, value: String) -> Unit)? = null

  private val handler = Handler(Looper.getMainLooper())
  private val context: Context get() = hostView.context
  private val density: Float get() = context.resources.displayMetrics.density
  /** Rides under whatever hangs from the top of the window. */
  private var stage: FrameLayout? = null
  private var entries = mutableListOf<ToastEntry>() // newest first
  private var config = ToasterConfig()
  private var pressed = 0
  private var obstructionShift = 0f

  private val reduceMotion: Boolean
    get() = config.respectReducedMotion &&
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
      !ValueAnimator.areAnimatorsEnabled()

  private fun dp(value: Float): Int = (value * density).roundToInt()

  private val activity: Activity?
    get() = (context as? ThemedReactContext)?.currentActivity

  // region Sync

  fun sync(items: List<ToastItem>, next: ToasterConfig) {
    val configChanged = next.cardColor != config.cardColor
    config = next
    val present = items.map { it.spec.id }.toSet()
    // Cards the list no longer knows leave without ceremony.
    entries.filter { it.id !in present }.forEach { remove(it, report = false) }
    // Oldest first, so each new card joins in front of the previous one.
    for (item in items.asReversed()) {
      var entry = entries.firstOrNull { it.id == item.spec.id }
      if (entry != null && entry.leaving && item.visible) {
        // Shown again while the previous card was leaving.
        remove(entry, report = false)
        entry = null
      }
      if (entry == null) {
        if (item.visible) add(item) else onEvent?.invoke(item.spec.id, item.generation, "close", "programmatic")
        continue
      }
      if (entry.item.revision != item.revision || configChanged) {
        entry.slot.card.apply(item.spec, config.cardColor)
      }
      if (entry.item.generation != item.generation) {
        // Shown again: the toast starts its lifetime over.
        cancelTimer(entry)
        entry.remaining = lifetime(item.spec)
      }
      entry.item = item
      if (!item.visible) dismiss(entry, "programmatic", report = false)
    }
    layout(animated = true)
    syncTimers()
  }

  fun invalidate() {
    entries.forEach { entry ->
      cancelTimer(entry)
      entry.removal?.let(handler::removeCallbacks)
      entry.slot.card.cancelLoads()
    }
    entries.clear()
    releaseStage()
  }

  private fun lifetime(spec: ToastSpec): Long? =
    if (spec.duration >= 0) spec.duration.toLong() else null

  // endregion

  // region Stage

  private fun ensureStage(): FrameLayout? {
    stage?.let { if (it.parent != null) return it }
    val activity = activity ?: return null
    val layer = stage ?: FrameLayout(context).apply {
      clipChildren = false
      clipToPadding = false
      addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        if (right - left != oldRight - oldLeft || bottom - top != oldBottom - oldTop) {
          // A new window size moves every card; wait for the layout pass to end.
          handler.post { layout(animated = false) }
        }
      }
    }
    stage = layer
    NativeOverlayHost.of(activity).attachToastLayer(layer)
    ViewCompat.setOnApplyWindowInsetsListener(layer) { _, insets ->
      handler.post { layout(animated = true) }
      insets
    }
    return layer
  }

  private fun releaseStage() {
    val layer = stage ?: return
    layer.removeAllViews()
    activity?.let { NativeOverlayHost.of(it).detachToastLayer(layer) }
      ?: (layer.parent as? ViewGroup)?.removeView(layer)
    obstructionShift = 0f
    layer.translationY = 0f
  }

  /** The host joined a window: draw the toasts that were waiting for one. */
  fun hostAttached() {
    if (entries.isEmpty()) return
    val layer = ensureStage() ?: return
    entries.asReversed().filter { it.slot.parent == null }.forEach { attach(it, layer) }
    layout(animated = true)
    syncTimers()
  }

  // endregion

  // region Cards

  private fun add(item: ToastItem) {
    val card = NativeOverlayToastCardView(context)
    val slot = ToastSlot(context, card)
    val entry = ToastEntry(item.spec.id, item, slot)
    entry.remaining = lifetime(item.spec)
    card.onAction = { actionId -> if (!entry.leaving) onEvent?.invoke(entry.id, entry.item.generation, "action", actionId) }
    card.onLink = { link -> if (!entry.leaving) onEvent?.invoke(entry.id, entry.item.generation, "link", link) }
    card.apply(item.spec, config.cardColor)
    slot.onPressChanged = { down ->
      pressed = if (down) pressed + 1 else max(0, pressed - 1)
      syncTimers()
    }
    slot.onDrag = { translation ->
      if (!entry.leaving) {
        // Toasts land at the top, so the dismiss direction is up.
        card.translationY =
          if (translation <= 0) translation else translation / ToasterMetrics.SWIPE_RESISTANCE
      }
    }
    slot.onRelease = { translationY, translationX, velocityY, cancelled ->
      if (!entry.leaving) {
        val distance = -translationY / density
        val velocity = -velocityY / density
        val dismissed = !cancelled && translationY < 0 && abs(translationY) > abs(translationX) &&
          (distance >= ToasterMetrics.SWIPE_DISTANCE ||
            (distance >= ToasterMetrics.SWIPE_FLICK_DISTANCE && velocity >= ToasterMetrics.SWIPE_FLICK_VELOCITY))
        if (dismissed) dismiss(entry, "swipe", report = true) else settle(card)
      }
    }
    val press = {
      if (!entry.leaving && entry.item.spec.pressable) {
        onEvent?.invoke(entry.id, entry.item.generation, "press", "")
      }
    }
    card.onPress = press
    slot.onTap = press
    entries.add(0, entry)
    ensureStage()?.let { attach(entry, it) }
  }

  private fun attach(entry: ToastEntry, layer: FrameLayout) {
    entry.slot.alpha = 0f
    layer.addView(
      entry.slot,
      FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT),
    )
    if (layer.importantForAccessibility != View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS) {
      entry.slot.announceForAccessibility(entry.slot.card.announcement)
    }
  }

  private fun settle(card: View) {
    if (reduceMotion) {
      card.translationY = 0f
      return
    }
    card.animate().translationY(0f).setDuration(ToasterMetrics.ANIMATION_MS)
      .setInterpolator(toastInterpolator).start()
  }

  private fun dismiss(entry: ToastEntry, reason: String, report: Boolean) {
    if (entry.leaving) return
    entry.leaving = true
    entry.reason = reason
    cancelTimer(entry)
    if (report) onEvent?.invoke(entry.id, entry.item.generation, "dismiss", reason)
    val removal = Runnable {
      remove(entry, report = true)
      layout(animated = true)
    }
    entry.removal = removal
    handler.postDelayed(removal, ToasterMetrics.REMOVE_DELAY_MS)
    layout(animated = true)
  }

  private fun remove(entry: ToastEntry, report: Boolean) {
    cancelTimer(entry)
    entry.removal?.let(handler::removeCallbacks)
    entry.slot.card.cancelLoads()
    entry.slot.animate().cancel()
    (entry.slot.parent as? ViewGroup)?.removeView(entry.slot)
    entries.remove(entry)
    if (report) onEvent?.invoke(entry.id, entry.item.generation, "close", entry.reason)
    if (entries.isEmpty()) {
      pressed = 0
      releaseStage()
    }
  }

  // endregion

  // region Layout

  private fun computeObstructionShift(layer: View, topInset: Int, bottomInset: Int): Float {
    if (config.topObstruction <= 0f) return 0f
    val toastTop = topInset + dp(ToasterMetrics.TOP_OFFSET)
    val usableBottom = layer.height - bottomInset
    val top = min(
      dp(config.topObstruction + config.topObstructionGap),
      usableBottom - dp(config.topObstructionRoom),
    )
    return max(0, top - toastTop).toFloat()
  }

  private fun layout(animated: Boolean) {
    val layer = stage ?: return
    if (entries.isEmpty() || layer.width == 0) return
    val insets = ViewCompat.getRootWindowInsets(layer)
    val bars = insets?.getInsets(
      WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
    )
    val ime = insets?.getInsets(WindowInsetsCompat.Type.ime())
    val topInset = bars?.top ?: 0
    // The keyboard's top edge while it is up, the system bar's otherwise.
    val bottomInset = max(bars?.bottom ?: 0, ime?.bottom ?: 0)
    val animate = animated && !reduceMotion

    val shift = computeObstructionShift(layer, topInset, bottomInset)
    if (shift != obstructionShift) {
      obstructionShift = shift
      if (animate) {
        layer.animate().translationY(shift).setDuration(ToasterMetrics.OBSTRUCTION_MS)
          .setInterpolator(toastInterpolator).start()
      } else {
        layer.animate().cancel()
        layer.translationY = shift
      }
    }

    val contentWidth = config.contentWidth
      ?: (min(layer.width / density, ToasterMetrics.FALLBACK_MAX_PAGE_WIDTH) - ToasterMetrics.FALLBACK_MARGIN)
    val contentMaxHeight = config.contentMaxHeight ?: 0f
    val top = topInset + dp(ToasterMetrics.TOP_OFFSET)
    val left = bars?.left ?: 0
    val right = bars?.right ?: 0
    // The replaced toaster centred a lane of at most 360dp and centred the
    // card in it; both offsets were rounded on their own.
    val available = (layer.width - left - right).toDouble()
    val lane = min(available / density - ToasterMetrics.LANE_MARGIN, ToasterMetrics.LANE_MAX_WIDTH.toDouble())
    val laneLeft = (available - lane * density) / 2
    val cardOffset = (lane - (contentWidth + 2 * ToasterMetrics.CARD_PADDING_HORIZONTAL)) * density / 2
    val originX = left + laneLeft + cardOffset
    val drawnX = left + snapHalfUp(laneLeft) + snapHalfUp(cardOffset)
    val originY = topInset + (ToasterMetrics.TOP_OFFSET * density).toDouble()
    // Oldest at the top, newest under it: a toast keeps its place when a newer
    // one arrives and moves up when an older one leaves.
    var offset = 0
    for (entry in entries.asReversed()) {
      val slot = entry.slot
      val card = slot.card
      if (card.contentWidth != contentWidth || card.contentMaxHeight != contentMaxHeight ||
        card.originX != originX || card.originY != originY
      ) {
        card.contentWidth = contentWidth
        card.contentMaxHeight = contentMaxHeight
        card.originX = originX
        card.originY = originY
        card.requestLayout()
      }
      slot.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
      val height = slot.measuredHeight
      // Where the card rests before it enters and after it leaves.
      val hidden = -(if (height > 0) height else dp(ToasterMetrics.FALLBACK_HEIGHT)) -
        topInset - dp(ToasterMetrics.ENTER_OVERSHOOT)
      val targetY = (top + if (entry.leaving) hidden else offset).toFloat()
      val targetAlpha = if (entry.leaving) 0f else 1f
      if (!entry.leaving) offset += height + dp(ToasterMetrics.GUTTER)
      slot.translationX = drawnX.toFloat()
      if (!entry.entered) {
        entry.entered = true
        slot.translationY = (top + hidden).toFloat()
      }
      if (animate) {
        slot.animate().translationY(targetY).alpha(targetAlpha)
          .setDuration(ToasterMetrics.ANIMATION_MS).setInterpolator(toastInterpolator).start()
      } else {
        slot.animate().cancel()
        slot.translationY = targetY
        slot.alpha = targetAlpha
      }
    }
  }

  // endregion

  // region Timers

  private fun cancelTimer(entry: ToastEntry) {
    entry.timer?.let(handler::removeCallbacks)
    entry.timer = null
  }

  private fun syncTimers() {
    val paused = pressed > 0
    val now = SystemClock.uptimeMillis()
    for (entry in entries) {
      if (entry.leaving) continue
      val remaining = entry.remaining ?: continue
      if (paused) {
        if (entry.timer != null) {
          entry.remaining = max(0L, remaining - (now - entry.startedAt))
          cancelTimer(entry)
        }
        continue
      }
      if (entry.timer != null) continue
      entry.startedAt = now
      val timer = Runnable {
        entry.timer = null
        dismiss(entry, "timeout", report = true)
      }
      entry.timer = timer
      handler.postDelayed(timer, max(remaining, 0L))
    }
  }

  // endregion
}

/**
 * The zero-size host mounted by `OverlayToastHost`. It only carries the toast
 * list and settings across the bridge; the cards are drawn in the activity's
 * `toast` overlay layer.
 */
@SuppressLint("ViewConstructor")
class NativeOverlayToasterView(context: ThemedReactContext) : View(context) {
  var onToastEvent: ((id: String, generation: Int, type: String, value: String) -> Unit)? = null

  private val presenter = NativeOverlayToastPresenter(this).also { presenter ->
    presenter.onEvent = { id, generation, type, value -> onToastEvent?.invoke(id, generation, type, value) }
  }
  private var toasts: String? = null
  private var config: String? = null
  private var dirty = false

  fun setToasts(value: String?) {
    if (value != toasts) {
      toasts = value
      dirty = true
    }
  }

  fun setConfig(value: String?) {
    if (value != config) {
      config = value
      dirty = true
    }
  }

  /** Called once per props transaction. */
  fun commit() {
    if (!dirty) return
    dirty = false
    presenter.sync(ToastItem.parseList(toasts), ToasterConfig.parse(config))
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    presenter.hostAttached()
  }

  fun invalidateHost() {
    presenter.invalidate()
  }
}
