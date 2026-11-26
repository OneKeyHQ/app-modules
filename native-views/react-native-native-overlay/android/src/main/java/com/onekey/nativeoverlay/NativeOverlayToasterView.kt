package com.onekey.nativeoverlay

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.database.ContentObserver
import android.graphics.Matrix
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.window.java.layout.WindowInfoTrackerCallbackAdapter
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.Event
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// RareUI-inspired motion and gesture constants, in dp / ms.
private object ToasterMetrics {
  /** The first toast sits this far under the top inset. */
  const val TOP_OFFSET = 16f
  const val GUTTER = 10f
  const val VISIBLE_TOASTS = 3
  const val STACK_SCALE_STEP = 0.05f
  const val SPRING_MS = 407L
  const val ENTER_FADE_MS = 220L
  const val EXIT_MS = 200L
  const val AUTO_EXIT_MS = 190L
  const val OBSTRUCTION_MS = 220L
  /** Keep leaving cards mounted long enough for the exit transition and close callback. */
  const val REMOVE_DELAY_MS = 220L
  const val ENTER_OVERSHOOT = 32f
  const val HIDDEN_SCALE = 0.96f
  const val SWIPE_EXIT_SCALE = 0.98f
  const val SWIPE_EXIT_MARGIN = 48f
  const val AUTO_EXIT_OFFSET = 16f
  const val AUTO_EXIT_SCALE = 0.95f
  const val COLLAPSED_BACK_EXIT_OFFSET = 12f
  const val COLLAPSED_LEAVE_SCALE = 0.90f
  const val FALLBACK_HEIGHT = 56f
  const val SWIPE_START = 10f
  const val SWIPE_DISTANCE = 64f
  const val SWIPE_FLICK_DISTANCE = 20f
  const val SWIPE_FLICK_VELOCITY = 500f
  const val SWIPE_OPACITY_RANGE = 260f
  const val SWIPE_MIN_OPACITY = 0.35f
  /** Pull away from the top edge that expands a collapsed stack. */
  const val EXPAND_PULL_DISTANCE = 40f
  /** Push toward the top edge that collapses an expanded stack. */
  const val COLLAPSE_PUSH_DISTANCE = 50f
  const val RUBBER_DIMENSION = 30f
  const val EXPAND_RUBBER_DIMENSION = 70f
  /** Extra pull per depth while expanding, so back cards fan out under the finger. */
  const val EXPAND_SPREAD_STEP = 0.35f
  const val FALLBACK_MARGIN = 64f
  const val FALLBACK_MAX_PAGE_WIDTH = 640f
  const val LANE_MAX_WIDTH = 360f
  const val LANE_MARGIN = 32.0
  const val CARD_PADDING_HORIZONTAL = 10f
  const val MIN_SIDE_INSET = 16f
}

/** Rounds half up, the way the replaced layout placed a node in its parent. */
private fun snapHalfUp(value: Double): Int = kotlin.math.floor(value + 0.5 + 1e-4).toInt()

/** Spring timing shared with Web and iOS: mass 1, stiffness 460, damping 36. */
private val toastSpringInterpolator = TimeInterpolator { input ->
  if (input >= 1f) 1f else toastSpringProgress(460.0, 36.0, input * ToasterMetrics.SPRING_MS)
}

/** Smooth ease-out fade for card entrance and visibility transitions. */
private val toastEnterFadeInterpolator = PathInterpolator(0f, 0f, 0.58f, 1f)

/** Brisk ease-out curve for interactive swipe exit and obstruction shifts. */
private val toastExitInterpolator = PathInterpolator(0.32f, 0.72f, 0f, 1f)

/** Gentle ease-out curve for non-interactive timeout / programmatic exit. */
private val toastAutoExitInterpolator = PathInterpolator(0.2f, 0f, 0f, 1f)

/** Chooses a region in stage coordinates from separating, window-relative folds. */
internal fun toastAvailableBounds(
  safeBounds: Rect,
  features: List<FoldingFeature>,
  windowX: Int,
  windowY: Int,
  isRtl: Boolean,
): Rect {
  val region = Rect(safeBounds)
  for (feature in features) {
    if (!feature.isSeparating) continue
    val fold = Rect(feature.bounds).apply { offset(-windowX, -windowY) }
    if (feature.orientation == FoldingFeature.Orientation.VERTICAL &&
      fold.left > region.left && fold.right < region.right &&
      fold.top < region.bottom && fold.bottom > region.top
    ) {
      val leftWidth = fold.left - region.left
      val rightWidth = region.right - fold.right
      if (rightWidth > leftWidth || (rightWidth == leftWidth && isRtl)) {
        region.left = fold.right
      } else {
        region.right = fold.left
      }
    } else if (feature.orientation == FoldingFeature.Orientation.HORIZONTAL &&
      fold.top > region.top && fold.bottom < region.bottom &&
      fold.left < region.right && fold.right > region.left
    ) {
      region.bottom = fold.top
    }
  }
  return region
}

internal fun toastContentWidth(availableDp: Float, requestedDp: Float?): Float {
  val maxContent = ToasterMetrics.FALLBACK_MAX_PAGE_WIDTH - ToasterMetrics.FALLBACK_MARGIN
  val availableContent = availableDp -
    2 * (ToasterMetrics.MIN_SIDE_INSET + ToasterMetrics.CARD_PADDING_HORIZONTAL)
  return (requestedDp?.takeIf { it.isFinite() } ?: (availableDp - ToasterMetrics.FALLBACK_MARGIN))
    .coerceIn(0f, min(maxContent, availableContent).coerceAtLeast(0f))
}

internal fun toastStackScale(availablePx: Float, stackHeightPx: Int): Float =
  if (stackHeightPx > 0) (availablePx / stackHeightPx).coerceIn(0f, 1f) else 1f

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

/** Full-window toast stage that passes outside touches through while reporting them to collapse an expanded stack. */
private class ToastStageLayout(context: Context) : FrameLayout(context) {
  var onOutsideTouch: (() -> Unit)? = null
  val cards = FrameLayout(context).apply {
    clipChildren = false
    clipToPadding = false
  }

  init {
    addView(cards, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
  }

  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    val handled = super.dispatchTouchEvent(ev)
    if (!handled && ev.actionMasked == MotionEvent.ACTION_DOWN) {
      onOutsideTouch?.invoke()
    }
    return handled
  }
}

/**
 * Hosts one card: forwards presses and drags to the presenter. A horizontal
 * drag moves this card here; a vertical drag is reported in window pixels and
 * the presenter moves every card it involves.
 */
@SuppressLint("ViewConstructor")
internal class ToastSlot(context: Context, val card: NativeOverlayToastCardView) : FrameLayout(context) {
  var onPressChanged: (() -> Unit)? = null
  var onDragStart: ((vertical: Boolean, dx: Float, dy: Float) -> Unit)? = null
  var onVerticalDrag: ((dy: Float) -> Unit)? = null
  var onRelease: (
    (translationX: Float, translationY: Float, velocityX: Float, velocityY: Float, cancelled: Boolean) -> Unit
  )? = null
  var onTap: (() -> Unit)? = null
  var allowDrag = true
    set(value) {
      if (field && !value && dragging) {
        cancelDrag()
        moved = true
        onRelease?.invoke(0f, 0f, 0f, 0f, true)
      }
      field = value
    }
  var expansionOnly = false

  private var downX = 0f
  private var downY = 0f
  private var windowDragX = 0f
  var dragging = false
    private set
  /** The locked axis of the current drag. */
  var dragVertical = false
    private set
  private var moved = false
  private val slop get() = ToasterMetrics.SWIPE_START * resources.displayMetrics.density
  var hasActivePress = false
    private set
  private var tracker: VelocityTracker? = null
  private val slotToWindow = Matrix()

  init {
    clipChildren = false
    clipToPadding = false
    addView(card, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
  }

  private fun cancelDrag() {
    dragging = false
    dragVertical = false
    windowDragX = 0f
    tracker?.recycle()
    tracker = null
  }

  fun clearTouch() {
    resetPress()
    cancelDrag()
  }

  private fun resetPress() {
    if (hasActivePress) {
      hasActivePress = false
      onPressChanged?.invoke()
    }
  }

  override fun dispatchTouchEvent(event: MotionEvent): Boolean {
    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
      if (!isEnabled || event.x < card.x || event.x >= card.x + card.width ||
        event.y < card.y || event.y >= card.y + card.currentDisplayHeightPx
      ) return false
      hasActivePress = true
      onPressChanged?.invoke()
    }
    val handled = isEnabled && super.dispatchTouchEvent(event)
    if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL ||
      (event.actionMasked == MotionEvent.ACTION_DOWN && !handled)
    ) clearTouch()
    return handled
  }

  private fun track(event: MotionEvent) {
    val current = tracker ?: VelocityTracker.obtain().also { tracker = it }
    // Ancestors only translate. Undo the slot's scale and translation for
    // the entire batch so a pane resize cannot introduce a jump in velocity.
    slotToWindow.setScale(scaleX, scaleY)
    slotToWindow.postTranslate(event.rawX - event.x * scaleX, event.rawY - event.y * scaleY)
    val windowEvent = MotionEvent.obtain(event)
    windowEvent.transform(slotToWindow)
    current.addMovement(windowEvent)
    windowEvent.recycle()
  }

  fun reprojectDrag() {
    if (dragging && !dragVertical && scaleX > 0f) {
      card.translationX = windowDragX / scaleX
    }
  }

  /** Locks the axis once the drag passes the slop. */
  private fun startDragIfNeeded(dx: Float, dy: Float): Boolean {
    if (dragging || (abs(dx) <= slop && abs(dy) <= slop)) return false
    dragging = true
    dragVertical = abs(dy) > abs(dx)
    onDragStart?.invoke(dragVertical, dx, dy)
    return true
  }

  override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        downX = event.rawX
        downY = event.rawY
        moved = false
        cancelDrag()
        if (expansionOnly) return true
        if (allowDrag) track(event)
      }
      MotionEvent.ACTION_MOVE -> {
        if (!allowDrag) return false
        track(event)
        val dy = event.rawY - downY
        val dx = event.rawX - downX
        if (startDragIfNeeded(dx, dy)) {
          parent?.requestDisallowInterceptTouchEvent(true)
        }
      }
    }
    return dragging
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (!isEnabled) return false
    when (event.actionMasked) {
      MotionEvent.ACTION_MOVE -> {
        val dy = event.rawY - downY
        val dx = event.rawX - downX
        if (abs(dy) > slop || abs(dx) > slop) moved = true
        if (allowDrag) {
          track(event)
          startDragIfNeeded(dx, dy)
          if (dragging && dragVertical) {
            onVerticalDrag?.invoke(dy)
          } else if (dragging) {
            windowDragX = dx
            card.animate().cancel()
            reprojectDrag()
            card.alpha = max(
              ToasterMetrics.SWIPE_MIN_OPACITY,
              1f - abs(dx / resources.displayMetrics.density) / ToasterMetrics.SWIPE_OPACITY_RANGE,
            )
          }
        }
      }
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        if (allowDrag) track(event)
        val cancelled = event.actionMasked == MotionEvent.ACTION_CANCEL
        if (dragging) {
          tracker?.computeCurrentVelocity(1000)
          onRelease?.invoke(
            event.rawX - downX, event.rawY - downY,
            tracker?.xVelocity ?: 0f, tracker?.yVelocity ?: 0f, cancelled,
          )
        } else if (!cancelled && !moved &&
          abs(event.rawX - downX) <= slop && abs(event.rawY - downY) <= slop
        ) {
          onTap?.invoke()
        }
        cancelDrag()
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
  var collapsedBackExit = false
  var reason = "programmatic"
  /** Milliseconds of lifetime left, counted while not paused. */
  var remaining: Long? = null
  var startedAt = 0L
  var timer: Runnable? = null
  var removal: Runnable? = null
  var alphaAnimator: ObjectAnimator? = null
  var entered = false
  var lastActiveIndex = 0
  var swipeExitSign = 1f
  /** Left with its stack past the top edge rather than off to a side. */
  var swipeExitUp = false
  /** The body was faded behind the front card when the dismissal began. */
  var exitBodyHidden = false
  var lastTargetY = Float.NaN
  var lastTargetScale = 1f
  var lastPaneScale = 1f
  var dragStart = 0f
}

private enum class ToastDragMode {
  /** One card, sideways. */
  HORIZONTAL,
  /** The collapsed stack toward the top edge, dismissing it. */
  STACK,
  /** The collapsed stack away from the top edge, expanding it. */
  EXPAND,
  /** The expanded stack toward the top edge, collapsing it. */
  COLLAPSE,
  /** Nothing to do in that direction: damped, then settles. */
  RUBBER,
}

private class ToastDrag(val mode: ToastDragMode, val owner: ToastEntry, val participants: List<ToastEntry>)

/** Damped displacement past a limit, as a scroll view's edge bounce. */
private fun rubberBand(offset: Float, dimension: Float): Float {
  val magnitude = dimension * (1f - 1f / (abs(offset) * 0.55f / dimension + 1f))
  return if (offset < 0f) -magnitude else magnitude
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
  private var stage: ToastStageLayout? = null
  private var stopTrackingFolds: (() -> Unit)? = null
  private var foldingFeatures: List<FoldingFeature> = emptyList()
  private val windowLocation = IntArray(2)
  private var entries = mutableListOf<ToastEntry>() // newest first
  private var config = ToasterConfig()
  private var expanded = false
  private var obstructionShift = 0f
  private var drag: ToastDrag? = null
  /** Toasts first shown during a drag, oldest first; they enter when it ends. */
  private val deferredAdds = mutableListOf<ToastItem>()
  private val motionObserver = object : ContentObserver(handler) {
    override fun onChange(selfChange: Boolean) {
      layout(animated = false)
    }
  }

  private val activeEntries: List<ToastEntry>
    get() = entries.filter { !it.leaving }

  private val reduceMotion: Boolean
    get() = config.respectReducedMotion &&
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
      !ValueAnimator.areAnimatorsEnabled()

  private fun dp(value: Float): Int = (value * density).roundToInt()

  private val activity: Activity?
    get() = (context as? ThemedReactContext)?.currentActivity

  // region Sync

  fun sync(items: List<ToastItem>, next: ToasterConfig) {
    val configChanged = next.cardColor != config.cardColor || next.cardBorderColor != config.cardBorderColor
    config = next
    val present = items.map { it.spec.id }.toSet()
    deferredAdds.removeAll { it.spec.id !in present }
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
        when {
          item.visible && drag != null -> deferAdd(item)
          item.visible -> add(item)
          else -> {
            deferredAdds.removeAll { it.spec.id == item.spec.id }
            onEvent?.invoke(item.spec.id, item.generation, "close", "programmatic")
          }
        }
        continue
      }
      if (entry.item.revision != item.revision || configChanged) {
        entry.slot.card.apply(
          item.spec, config.cardColor, config.cardBorderColor,
          settleStatus = item.visible && entry.entered && !reduceMotion &&
            entry.item.generation != item.generation &&
            (item.spec.icon as? ToastIcon.Status)?.status != "loading",
        )
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
    val departing = entries.toList()
    entries.clear()
    departing.forEach { entry ->
      cancelTimer(entry)
      entry.removal?.let(handler::removeCallbacks)
      entry.alphaAnimator?.cancel()
      entry.alphaAnimator = null
      entry.slot.onPressChanged = null
      entry.slot.clearTouch()
      entry.slot.card.cancelStatusMotion()
      entry.slot.card.cancelLoads()
    }
    deferredAdds.clear()
    drag = null
    expanded = false
    releaseStage()
  }

  private fun deferAdd(item: ToastItem) {
    val index = deferredAdds.indexOfFirst { it.spec.id == item.spec.id }
    if (index >= 0) deferredAdds[index] = item else deferredAdds.add(item)
  }

  /** Draws the toasts that arrived while a card was being dragged. */
  private fun flushDeferredAdds() {
    if (drag != null || deferredAdds.isEmpty()) return
    val pending = deferredAdds.toList()
    deferredAdds.clear()
    for (item in pending) {
      if (entries.none { it.id == item.spec.id }) add(item)
    }
    layout(animated = true)
    syncTimers()
  }

  private fun lifetime(spec: ToastSpec): Long? =
    if (spec.duration >= 0) spec.duration.toLong() else null

  // endregion

  // region Stage

  private fun ensureStage(): ToastStageLayout? {
    stage?.let { if (it.parent != null) return it }
    val activity = activity ?: return null
    val layer = stage ?: ToastStageLayout(context).apply {
      clipChildren = false
      clipToPadding = false
      onOutsideTouch = {
        handler.post {
          if (expanded) {
            expanded = false
            layout(animated = true)
            syncTimers()
          }
        }
      }
      addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        if (right - left != oldRight - oldLeft || bottom - top != oldBottom - oldTop) {
          // A new window size moves every card; wait for the layout pass to end.
          handler.post { layout(animated = false) }
        }
      }
      context.contentResolver.registerContentObserver(
        Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE), false, motionObserver,
      )
    }
    stage = layer
    NativeOverlayHost.of(activity).attachToastLayer(layer)
    trackFolds(activity, layer)
    ViewCompat.setOnApplyWindowInsetsListener(layer) { _, insets ->
      handler.post { layout(animated = true) }
      insets
    }
    return layer
  }

  @Suppress("DEPRECATION") // Window 1.5 provides a callback adapter for this view-owned listener.
  private fun trackFolds(activity: Activity, layer: ToastStageLayout) {
    stopTrackingFolds?.invoke()
    foldingFeatures = emptyList()
    val adapter = WindowInfoTrackerCallbackAdapter(WindowInfoTracker.getOrCreate(activity))
    val consumer = Consumer<WindowLayoutInfo> { info ->
      if (stage === layer) {
        val next = info.displayFeatures.filterIsInstance<FoldingFeature>()
        if (next != foldingFeatures) {
          foldingFeatures = next
          layout(animated = false)
        }
      }
    }
    adapter.addWindowLayoutInfoListener(activity, ContextCompat.getMainExecutor(activity), consumer)
    stopTrackingFolds = { adapter.removeWindowLayoutInfoListener(consumer) }
  }

  private fun releaseStage() {
    val layer = stage ?: return
    stage = null
    stopTrackingFolds?.invoke()
    stopTrackingFolds = null
    foldingFeatures = emptyList()
    context.contentResolver.unregisterContentObserver(motionObserver)
    layer.cards.removeAllViews()
    activity?.let { NativeOverlayHost.of(it).detachToastLayer(layer) }
      ?: (layer.parent as? ViewGroup)?.removeView(layer)
    obstructionShift = 0f
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
    card.apply(item.spec, config.cardColor, config.cardBorderColor)
    slot.onPressChanged = { syncTimers() }
    slot.onDragStart = { vertical, _, dy -> beginDrag(entry, vertical, dy) }
    slot.onVerticalDrag = { dy -> drag?.takeIf { it.owner === entry }?.let { applyVerticalDrag(it, dy) } }
    slot.onRelease = { translationX, translationY, velocityX, velocityY, cancelled ->
      val current = drag?.takeIf { it.owner === entry }
      drag = null
      if (current != null) {
        finishDrag(current, translationX, translationY, velocityX, velocityY, cancelled)
      }
      flushDeferredAdds()
    }
    val press = {
      if (!entry.leaving) {
        if (!expanded && activeEntries.size > 1) {
          expanded = true
          layout(animated = true)
          syncTimers()
        } else if (entry.item.spec.pressable) {
          onEvent?.invoke(entry.id, entry.item.generation, "press", "")
        }
      }
    }
    card.onPress = press
    slot.onTap = press
    entries.add(0, entry)
    ensureStage()?.let { attach(entry, it) }
  }

  private fun attach(entry: ToastEntry, layer: ToastStageLayout) {
    entry.slot.alpha = 0f
    layer.cards.addView(
      entry.slot,
      FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT),
    )
    if (layer.importantForAccessibility != View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS) {
      entry.slot.announceForAccessibility(entry.slot.card.announcement)
    }
  }

  private fun settle(card: View) {
    if (reduceMotion) {
      resetDrag(card)
      return
    }
    card.animate().translationX(0f).translationY(0f).alpha(1f).setDuration(ToasterMetrics.SPRING_MS)
      .setInterpolator(toastSpringInterpolator).start()
  }

  private fun resetDrag(card: View) {
    card.translationX = 0f
    card.translationY = 0f
    card.alpha = 1f
  }

  // region Drag

  private fun beginDrag(entry: ToastEntry, vertical: Boolean, dy: Float) {
    if (entry.leaving || drag != null) return
    val active = activeEntries
    val mode: ToastDragMode
    val participants: List<ToastEntry>
    when {
      !vertical -> {
        mode = ToastDragMode.HORIZONTAL
        participants = listOf(entry)
      }
      dy < 0f -> {
        mode = if (expanded) ToastDragMode.COLLAPSE else ToastDragMode.STACK
        participants = active
      }
      !expanded && active.size > 1 -> {
        mode = ToastDragMode.EXPAND
        participants = active
      }
      else -> {
        mode = ToastDragMode.RUBBER
        participants = listOf(entry)
      }
    }
    participants.forEach { it.slot.card.animate().cancel() }
    val next = ToastDrag(mode, entry, participants)
    drag = next
    if (vertical) applyVerticalDrag(next, dy)
  }

  /** [dy] is the drag in window pixels. */
  private fun applyVerticalDrag(drag: ToastDrag, dy: Float) {
    val rubber = ToasterMetrics.RUBBER_DIMENSION * density
    when (drag.mode) {
      ToastDragMode.HORIZONTAL -> Unit
      ToastDragMode.STACK -> {
        val offset = if (dy < 0f) dy else rubberBand(dy, rubber)
        drag.participants.forEach { setDragOffset(it, offset) }
      }
      ToastDragMode.EXPAND -> {
        val offset = if (dy > 0f) rubberBand(dy, ToasterMetrics.EXPAND_RUBBER_DIMENSION * density) else rubberBand(dy, rubber)
        drag.participants.forEachIndexed { index, participant ->
          setDragOffset(participant, offset * (1f + min(index, 2) * ToasterMetrics.EXPAND_SPREAD_STEP))
        }
      }
      ToastDragMode.COLLAPSE -> {
        val offset = if (dy < 0f) dy * 0.5f else rubberBand(dy, rubber)
        drag.participants.forEach { setDragOffset(it, offset) }
      }
      ToastDragMode.RUBBER -> setDragOffset(drag.owner, rubberBand(dy, rubber))
    }
  }

  /** Offsets a card inside its slot; the slot carries the depth and pane scale. */
  private fun setDragOffset(entry: ToastEntry, offset: Float) {
    if (entry.leaving) return
    val scale = entry.slot.scaleY.takeIf { it > 0.01f } ?: 1f
    entry.slot.card.translationY = offset / scale
  }

  /**
   * Ends a drag whose owner left or went away without a release, settling the
   * other cards it moved and letting deferred toasts in.
   */
  private fun abandonDrag(owner: ToastEntry) {
    val current = drag?.takeIf { it.owner === owner } ?: return
    drag = null
    current.participants.filter { it !== owner && !it.leaving }.forEach { settle(it.slot.card) }
    handler.post { flushDeferredAdds() }
  }

  private fun passesSwipe(distanceDp: Float, speedDp: Float): Boolean =
    distanceDp >= ToasterMetrics.SWIPE_DISTANCE ||
      (distanceDp >= ToasterMetrics.SWIPE_FLICK_DISTANCE && speedDp >= ToasterMetrics.SWIPE_FLICK_VELOCITY)

  private fun finishDrag(
    drag: ToastDrag,
    translationX: Float,
    translationY: Float,
    velocityX: Float,
    velocityY: Float,
    cancelled: Boolean,
  ) {
    val remaining = drag.participants.filter { !it.leaving }
    if (cancelled) {
      remaining.forEach { settle(it.slot.card) }
      return
    }
    val dyDp = translationY / density
    val vyDp = velocityY / density
    when (drag.mode) {
      ToastDragMode.HORIZONTAL -> {
        val entry = drag.owner
        if (entry.leaving) return
        val speed = velocityX / density * (if (translationX >= 0f) 1f else -1f)
        if (passesSwipe(abs(translationX) / density, speed)) {
          entry.swipeExitSign = if (translationX >= 0f) 1f else -1f
          entry.swipeExitUp = false
          dismiss(entry, "swipe", report = true)
        } else {
          settle(entry.slot.card)
        }
      }
      ToastDragMode.STACK -> {
        if (passesSwipe(-dyDp, -vyDp)) {
          remaining.forEach { it.swipeExitUp = true }
          dismiss(remaining, "swipe", report = true)
        } else {
          remaining.forEach { settle(it.slot.card) }
        }
      }
      ToastDragMode.EXPAND -> {
        remaining.forEach { settle(it.slot.card) }
        val pulled = dyDp >= ToasterMetrics.EXPAND_PULL_DISTANCE || vyDp >= ToasterMetrics.SWIPE_FLICK_VELOCITY
        if (pulled && !expanded && activeEntries.size > 1) {
          expanded = true
          layout(animated = true)
          syncTimers()
        }
      }
      ToastDragMode.COLLAPSE -> {
        remaining.forEach { settle(it.slot.card) }
        val pushed = -dyDp >= ToasterMetrics.COLLAPSE_PUSH_DISTANCE || -vyDp >= ToasterMetrics.SWIPE_FLICK_VELOCITY
        if (pushed && expanded) {
          expanded = false
          layout(animated = true)
          syncTimers()
        }
      }
      ToastDragMode.RUBBER -> remaining.forEach { settle(it.slot.card) }
    }
  }

  // endregion

  private fun dismiss(entry: ToastEntry, reason: String, report: Boolean) {
    dismiss(listOf(entry), reason, report)
  }

  /**
   * Starts every exit before laying out once, so a group leaves together and
   * no survivor is promoted between two of its cards.
   */
  private fun dismiss(group: List<ToastEntry>, reason: String, report: Boolean) {
    val leaving = group.filter { !it.leaving }
    if (leaving.isEmpty()) return
    for (entry in leaving) {
      abandonDrag(entry)
      entry.collapsedBackExit = !expanded && entry.lastActiveIndex > 0 && reason != "swipe"
      entry.exitBodyHidden = !expanded && entry.lastActiveIndex > 0
      entry.leaving = true
      entry.slot.onPressChanged = null
      entry.slot.clearTouch()
      entry.slot.card.cancelStatusMotion()
      entry.reason = reason
      cancelTimer(entry)
      if (report) onEvent?.invoke(entry.id, entry.item.generation, "dismiss", reason)
    }
    if (reduceMotion) {
      leaving.forEach { remove(it, report = true) }
      layout(animated = false)
      syncTimers()
      return
    }
    for (entry in leaving) {
      val removal = Runnable {
        remove(entry, report = true)
        layout(animated = true)
        syncTimers()
      }
      entry.removal = removal
      handler.postDelayed(removal, ToasterMetrics.REMOVE_DELAY_MS)
    }
    if (activeEntries.size <= 1) {
      expanded = false
    }
    layout(animated = true)
    syncTimers()
  }

  private fun remove(entry: ToastEntry, report: Boolean) {
    abandonDrag(entry)
    entries.remove(entry)
    cancelTimer(entry)
    entry.removal?.let(handler::removeCallbacks)
    entry.alphaAnimator?.cancel()
    entry.alphaAnimator = null
    entry.slot.onPressChanged = null
    entry.slot.clearTouch()
    entry.slot.card.cancelStatusMotion()
    entry.slot.card.cancelLoads()
    entry.slot.animate().cancel()
    (entry.slot.parent as? ViewGroup)?.removeView(entry.slot)
    if (activeEntries.size <= 1) {
      expanded = false
    }
    if (report) onEvent?.invoke(entry.id, entry.item.generation, "close", entry.reason)
    if (entries.isEmpty()) {
      expanded = false
      releaseStage()
    }
  }

  // endregion

  // region Layout

  private fun computeObstructionShift(topInset: Int, usableBottom: Int): Float {
    if (config.topObstruction <= 0f) return 0f
    val toastTop = topInset + dp(ToasterMetrics.TOP_OFFSET)
    val top = min(
      dp(config.topObstruction + config.topObstructionGap),
      usableBottom - dp(config.topObstructionRoom),
    )
    return max(0, top - toastTop).toFloat()
  }

  private fun layout(animated: Boolean) {
    val layer = stage ?: return
    if (entries.isEmpty() || layer.width == 0) return
    val cards = layer.cards
    val insets = ViewCompat.getRootWindowInsets(layer)
    val bars = insets?.getInsets(
      WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
    )
    val ime = insets?.getInsets(WindowInsetsCompat.Type.ime())
    layer.getLocationInWindow(windowLocation)
    val safeBottom = layer.height - (bars?.bottom ?: 0)
    val region = toastAvailableBounds(
      Rect(
        bars?.left ?: 0,
        bars?.top ?: 0,
        layer.width - (bars?.right ?: 0),
        safeBottom,
      ),
      foldingFeatures, windowLocation[0], windowLocation[1],
      layer.layoutDirection == View.LAYOUT_DIRECTION_RTL,
    )
    val usableBottom = min(region.bottom, layer.height - (ime?.bottom ?: 0))
    val animate = animated && !reduceMotion

    val shift = computeObstructionShift(region.top, usableBottom)
    if (shift != obstructionShift) {
      obstructionShift = shift
      if (animate) {
        cards.animate().translationY(shift).setDuration(ToasterMetrics.OBSTRUCTION_MS)
          .setInterpolator(toastExitInterpolator).start()
      } else {
        cards.animate().cancel()
        cards.translationY = shift
      }
    }

    val available = region.width().toDouble()
    val contentWidth = toastContentWidth(region.width() / density, config.contentWidth)
    val contentMaxHeight = config.contentMaxHeight ?: 0f
    val top = region.top + dp(ToasterMetrics.TOP_OFFSET)
    // Keep the existing half-up rounding of the lane and card offsets.
    val lane = min(available / density - ToasterMetrics.LANE_MARGIN, ToasterMetrics.LANE_MAX_WIDTH.toDouble())
    val laneLeft = (available - lane * density) / 2
    val cardOffset = (lane - (contentWidth + 2 * ToasterMetrics.CARD_PADDING_HORIZONTAL)) * density / 2
    val originX = region.left + laneLeft + cardOffset
    val drawnX = region.left + snapHalfUp(laneLeft) + snapHalfUp(cardOffset)
    val originY = region.top + (ToasterMetrics.TOP_OFFSET * density).toDouble()
    val active = activeEntries
    if (active.size <= 1) {
      expanded = false
    }
    val isCollapsed = !expanded && active.size > 1

    // Measure every card's natural size first so collapsed back cards know frontHeight.
    for (entry in entries) {
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
    }

    val frontHeight = active.firstOrNull()?.slot?.card?.naturalHeightPx?.takeIf { it > 0 }
      ?: dp(ToasterMetrics.FALLBACK_HEIGHT)

    // Keep newest card at the highest z-order (last child of FrameLayout).
    val ordered = entries.asReversed()
    var zMismatch = cards.childCount != ordered.size
    if (!zMismatch) {
      for (i in ordered.indices) {
        if (cards.getChildAt(i) !== ordered[i].slot) {
          zMismatch = true
          break
        }
      }
    }
    if (zMismatch) {
      for (entry in ordered) {
        entry.slot.bringToFront()
      }
    }

    val gutterPx = dp(ToasterMetrics.GUTTER)
    val paneScale = if (region.bottom < safeBottom) {
      val visible = active.take(ToasterMetrics.VISIBLE_TOASTS)
      val height = if (isCollapsed) frontHeight else visible.sumOf { entry ->
        entry.slot.card.naturalHeightPx.takeIf { it > 0 } ?: entry.slot.measuredHeight
      }
      val stackHeight = height + max(0, visible.size - 1) * gutterPx
      toastStackScale(usableBottom - top - shift - gutterPx, stackHeight)
    } else 1f
    var activeIndex = 0
    var expandedOffset = 0
    for (entry in entries) {
      val slot = entry.slot
      val card = slot.card
      val naturalHeight = card.naturalHeightPx.takeIf { it > 0 } ?: slot.measuredHeight
      val displayHeight: Int
      val targetY: Float
      val targetScale: Float
      val targetAlpha: Float
      val hideBody: Boolean
      val interactive: Boolean
      val allowDrag: Boolean

      val swipeExit = entry.leaving && entry.reason == "swipe"
      val cardPaneScale = if (entry.leaving) entry.lastPaneScale else paneScale
      var swipeExitTranslationX: Float? = null
      var swipeExitTranslationY: Float? = null

      if (entry.leaving) {
        displayHeight = card.currentDisplayHeightPx.takeIf { it > 0 } ?: naturalHeight
        val collapsedBackExit = entry.collapsedBackExit
        val baseY = if (entry.lastTargetY.isNaN()) top.toFloat() else entry.lastTargetY
        if (swipeExit && entry.swipeExitUp) {
          // Leaves with its stack: depth scale kept, travel measured in the
          // slot's scaled space so every card clears the top edge.
          targetY = baseY
          targetScale = entry.lastTargetScale
          val travel = baseY + obstructionShift + displayHeight + dp(ToasterMetrics.SWIPE_EXIT_MARGIN)
          swipeExitTranslationY = -travel / max(targetScale * cardPaneScale, 0.01f)
        } else if (swipeExit) {
          targetY = baseY
          targetScale = ToasterMetrics.SWIPE_EXIT_SCALE
          val cardWidthPx = slot.measuredWidth.takeIf { it > 0 }?.toFloat() ?: dp(ToasterMetrics.LANE_MAX_WIDTH).toFloat()
          val exitDistance = max(
            cardWidthPx + dp(ToasterMetrics.SWIPE_EXIT_MARGIN),
            layer.width * 0.5f + cardWidthPx * 0.5f,
          )
          swipeExitTranslationX = entry.swipeExitSign * exitDistance / max(cardPaneScale, 0.01f)
        } else if (collapsedBackExit) {
          targetY = baseY - dp(ToasterMetrics.COLLAPSED_BACK_EXIT_OFFSET)
          targetScale = ToasterMetrics.COLLAPSED_LEAVE_SCALE
        } else {
          targetY = baseY - dp(ToasterMetrics.AUTO_EXIT_OFFSET)
          targetScale = ToasterMetrics.AUTO_EXIT_SCALE
        }
        targetAlpha = 0f
        hideBody = collapsedBackExit || (swipeExit && entry.swipeExitUp && entry.exitBodyHidden)
        interactive = false
        allowDrag = false
      } else {
        val index = activeIndex
        entry.lastActiveIndex = index
        activeIndex += 1
        val isVisible = index < ToasterMetrics.VISIBLE_TOASTS
        if (isCollapsed) {
          displayHeight = if (index == 0) naturalHeight else frontHeight
          val clampedIndex = min(index, ToasterMetrics.VISIBLE_TOASTS)
          targetY = top + clampedIndex * gutterPx * cardPaneScale
          targetScale = max(0.85f, 1f - clampedIndex * ToasterMetrics.STACK_SCALE_STEP)
          hideBody = index > 0
          allowDrag = index == 0
        } else {
          displayHeight = naturalHeight
          targetY = top + expandedOffset * cardPaneScale
          targetScale = 1f
          hideBody = false
          allowDrag = isVisible
        }
        entry.lastTargetY = targetY
        entry.lastTargetScale = targetScale
        entry.lastPaneScale = cardPaneScale
        if (isVisible) {
          expandedOffset += naturalHeight + gutterPx
        }
        targetAlpha = if (isVisible) 1f else 0f
        interactive = isVisible && cardPaneScale > 0f
      }

      slot.translationX = drawnX.toFloat()
      slot.isEnabled = interactive
      slot.allowDrag = allowDrag && interactive
      slot.expansionOnly = hideBody
      slot.importantForAccessibility = if (interactive && !hideBody) {
        View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
      } else View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
      card.canExpandStack = isCollapsed && entry === active.firstOrNull()
      slot.pivotX = slot.measuredWidth / 2f
      slot.pivotY = if (cardPaneScale < 1f) 0f else displayHeight / 2f
      val finalScale = targetScale * cardPaneScale

      val wasEntered = entry.entered
      if (!wasEntered) {
        entry.entered = true
        val hidden = -(if (displayHeight > 0) displayHeight else dp(ToasterMetrics.FALLBACK_HEIGHT)) -
          region.top - dp(ToasterMetrics.ENTER_OVERSHOOT)
        card.setDisplayHeight(displayHeight, animated = false, durationMs = 0L, interpolator = toastSpringInterpolator)
        card.setBodyHidden(hideBody, animated = false)
        slot.translationY = (top + hidden).toFloat()
        slot.scaleX = ToasterMetrics.HIDDEN_SCALE
        slot.scaleY = ToasterMetrics.HIDDEN_SCALE
        if (animate && !entry.leaving) card.animateMessageStatusEntrance()
      } else {
        card.setBodyHidden(hideBody, animated = animate)
        card.setDisplayHeight(
          displayHeight,
          animated = animate && !entry.leaving,
          durationMs = ToasterMetrics.SPRING_MS,
          interpolator = toastSpringInterpolator,
        )
      }

      if (!entry.leaving && !reduceMotion) card.animateMessageStatusRing()
      if (reduceMotion) card.cancelStatusMotion()

      if (animate) {
        val exitDuration = if (swipeExit) ToasterMetrics.EXIT_MS else ToasterMetrics.AUTO_EXIT_MS
        val exitInterpolator = if (swipeExit) toastExitInterpolator else toastAutoExitInterpolator
        slot.animate().translationY(targetY).scaleX(finalScale).scaleY(finalScale)
          .setDuration(if (entry.leaving) exitDuration else ToasterMetrics.SPRING_MS)
          .setInterpolator(if (entry.leaving) exitInterpolator else toastSpringInterpolator)
          .setUpdateListener { slot.reprojectDrag() }
          .start()
        if (swipeExitTranslationX != null) {
          card.animate().translationX(swipeExitTranslationX)
            .setDuration(ToasterMetrics.EXIT_MS)
            .setInterpolator(toastExitInterpolator)
            .start()
        } else if (swipeExitTranslationY != null) {
          card.animate().translationY(swipeExitTranslationY)
            .setDuration(ToasterMetrics.EXIT_MS)
            .setInterpolator(toastExitInterpolator)
            .start()
        }
        if (slot.alpha != targetAlpha || entry.alphaAnimator?.isRunning == true) {
          entry.alphaAnimator?.cancel()
          entry.alphaAnimator = ObjectAnimator.ofFloat(slot, View.ALPHA, targetAlpha).apply {
            duration = if (entry.leaving) exitDuration else ToasterMetrics.ENTER_FADE_MS
            interpolator = if (entry.leaving) exitInterpolator else toastEnterFadeInterpolator
            start()
          }
        }
      } else {
        slot.animate().cancel()
        card.animate().cancel()
        entry.alphaAnimator?.cancel()
        entry.alphaAnimator = null
        slot.translationY = targetY
        val held = slot.dragging || drag?.participants?.contains(entry) == true
        if (swipeExitTranslationX != null) {
          card.translationX = swipeExitTranslationX
        } else if (swipeExitTranslationY != null) {
          card.translationY = swipeExitTranslationY
        } else if (!entry.leaving && !held) {
          resetDrag(card)
        }
        slot.alpha = targetAlpha
        slot.scaleX = finalScale
        slot.scaleY = finalScale
        slot.reprojectDrag()
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
    val paused = expanded || entries.any { !it.leaving && it.slot.hasActivePress }
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
