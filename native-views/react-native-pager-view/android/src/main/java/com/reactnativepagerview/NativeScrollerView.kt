package com.reactnativepagerview

import com.margelo.nitro.nativelogger.OneKeyLog

import android.content.Context
import android.graphics.Color
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.StateWrapper
import com.facebook.react.uimanager.PixelUtil
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.NativeGestureUtil
import java.util.WeakHashMap
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

/** Fabric owns the React child; this host owns only its native viewport and extent. */
class NativeScrollerView(context: Context) : FrameLayout(context) {
  internal val viewport = NativeScrollerViewport(context, this)
  private val refresh = NativeScrollerRefreshLayout(context)
  private val contentOriginFrame = object : FrameLayout(context) {
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
      setMeasuredDimension(MeasureSpec.getSize(widthSpec), MeasureSpec.getSize(heightSpec))
    }
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) = Unit
  }
  private val contentFrame = object : FrameLayout(context) {
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
      setMeasuredDimension(
        max(MeasureSpec.getSize(widthSpec), contentWidthPx + insetLeft + insetRight),
        contentHeightPx + contentTop + insetBottom,
      )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
      // Only the native origin layer moves. Fabric owns its React child's layout/transform.
      contentOriginFrame.measure(
        MeasureSpec.makeMeasureSpec(contentWidthPx, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(contentHeightPx, MeasureSpec.EXACTLY),
      )
      contentOriginFrame.layout(insetLeft, contentTop, insetLeft + contentWidthPx, contentTop + contentHeightPx)
    }
  }
  private val pagerInsets = WeakHashMap<View, Int>()
  private var reactChild: View? = null
  private var fabricState: StateWrapper? = null
  private var lastOriginX: Int? = null
  private var lastOriginY: Int? = null
  private var lastAncestorY: Int? = null
  private var lastContentViewportHeight: Int? = null
  private val consumedOffsetListener = Runnable { publishContentOrigin() }
  private var lastScrollEvent = 0L
  private var laidOut = false
  private var requestedRefresh = false
  private var refreshStatePending = false
  private val pendingRestore = NativeScrollerPendingRestore()
  private var refreshOffset = 0
  private var appliedRefreshOffset: Int? = null
  private var appliedRefreshSize = SwipeRefreshLayout.DEFAULT
  private var layoutPosted = false
  internal var contentWidthPx = 0
  internal var contentHeightPx = 0
  internal var insetTop = 0
  internal var insetRight = 0
  internal var insetBottom = 0
  internal var insetLeft = 0
  internal var scrollEventThrottle = 0.0
  internal var keyboardDismissMode = "none"
  internal var keyboardShouldPersistTaps = "never"
  internal var refreshEnabled = false
    set(value) {
      field = value
      refresh.isEnabled = value && viewport.scrollEnabled
    }
  internal val contentTop: Int
    get() = insetTop + pagerInsets.values.sum()

  init {
    clipChildren = true
    contentFrame.clipChildren = false
    contentOriginFrame.clipChildren = false
    contentFrame.addView(contentOriginFrame)
    viewport.isFillViewport = false
    viewport.isSmoothScrollingEnabled = false
    viewport.clipToPadding = false
    viewport.addView(contentFrame, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    refresh.addView(viewport, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    super.addView(refresh, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    refresh.isEnabled = false
    refresh.setColorSchemeColors(Color.GRAY)
    refresh.setOnChildScrollUpCallback { _, _ -> viewport.canScrollVertically(-1) }
    refresh.setOnRefreshListener { emit("topRefresh", Arguments.createMap()) }
    refresh.onTouchStream = { event -> viewport.recordTouchEvent(event) }
    refresh.onTouchStreamFinished = { viewport.releaseTouchTracking() }
    refresh.onGestureStart = { event -> viewport.beginDrag(event) }
    refresh.onGestureEnd = { event -> viewport.endDrag(event) }
  }

  internal fun setFabricState(wrapper: StateWrapper?) {
    fabricState = wrapper
    if (wrapper == null) {
      lastOriginX = null
      lastOriginY = null
      lastAncestorY = null
      lastContentViewportHeight = null
    } else {
      val state = wrapper.stateData
      if (state == null || !state.hasKey("contentOriginX") || !state.hasKey("contentOriginY") ||
        !state.hasKey("ancestorOffsetX") || !state.hasKey("ancestorOffsetY") ||
        lastOriginX == null || lastOriginY == null || lastAncestorY == null ||
        abs(state.getDouble("contentOriginX") - px(lastOriginX!!)) > 0.001 ||
        abs(state.getDouble("contentOriginY") - px(lastOriginY!!)) > 0.001 ||
        abs(state.getDouble("ancestorOffsetX")) > 0.001 ||
        abs(state.getDouble("ancestorOffsetY") - px(lastAncestorY!!)) > 0.001) {
        lastOriginX = null
        lastOriginY = null
        lastAncestorY = null
      }
      publishContentOrigin()
    }
  }

  internal fun publishContentOrigin() {
    val wrapper = fabricState ?: return
    val ancestors = viewport.getTag(R.id.onekey_native_scroll_coordinator_consumed_offsets)
      as? NativeScrollCoordinatorContributions
    val x = insetLeft - viewport.scrollX
    val y = contentTop - viewport.scrollY
    val ancestorY = -(ancestors?.values?.sum() ?: 0)
    if (lastOriginX == x && lastOriginY == y && lastAncestorY == ancestorY) return
    lastOriginX = x
    lastOriginY = y
    lastAncestorY = ancestorY
    wrapper.updateState(Arguments.createMap().apply {
      putDouble("contentOriginX", px(x))
      putDouble("contentOriginY", px(y))
      putDouble("ancestorOffsetX", 0.0)
      putDouble("ancestorOffsetY", px(ancestorY))
    })
  }

  internal fun setContentSize(width: Int? = null, height: Int? = null) {
    val nextWidth = width?.coerceAtLeast(0) ?: contentWidthPx
    val nextHeight = height?.coerceAtLeast(0) ?: contentHeightPx
    if (nextWidth == contentWidthPx && nextHeight == contentHeightPx) return
    contentWidthPx = nextWidth
    contentHeightPx = nextHeight
    scheduleLayout()
  }

  internal fun setPagerInset(owner: View, inset: Int?) {
    val next = inset?.coerceAtLeast(0)
    if (pagerInsets[owner] == next) return
    if (next == null) pagerInsets.remove(owner) else pagerInsets[owner] = next
    scheduleLayout()
  }

  internal fun addReactChild(child: View, index: Int) {
    require(index == 0 && reactChild == null) { "NativeScroller requires one React content view" }
    viewport.stopMotion()
    reactChild = child
    contentOriginFrame.addView(child)
    scheduleLayout()
  }

  internal fun removeReactChild() {
    viewport.stopMotion()
    pendingRestore.cancel()
    reactChild?.let(contentOriginFrame::removeView)
    reactChild = null
    contentWidthPx = 0
    contentHeightPx = 0
    scheduleLayout()
  }

  internal fun reactChildCount() = if (reactChild == null) 0 else 1
  internal fun reactChildAt(index: Int): View {
    require(index == 0)
    return requireNotNull(reactChild)
  }

  internal fun scheduleLayout() {
    if (layoutPosted) return
    layoutPosted = true
    post {
      layoutPosted = false
      if (isAttachedToWindow && width > 0 && height > 0) layoutContents()
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
    refresh.measure(
      MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY),
    )
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    layoutContents()
  }

  private fun layoutContents() {
    val x = viewport.scrollX
    val y = viewport.scrollY
    contentFrame.forceLayout()
    viewport.forceLayout()
    refresh.forceLayout()
    refresh.measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
    )
    refresh.layout(0, 0, width, height)
    // Reapply the current anchor after content shrink; NestedScrollView clamps it.
    // A pager restoration clamped before content reached native resumes as it grows.
    viewport.scrollTo(x, pendingRestore.anchor(y))
    pendingRestore.settle(viewport.scrollY)
    laidOut = true
    publishContentViewport()
    publishContentOrigin()
    applyRefreshOffset()
    // Apply only a prop received before the first layout. Reapplying it on later
    // layouts would hide a gesture-started spinner before JS commits refreshing.
    if (refreshStatePending) {
      refreshStatePending = false
      if (refresh.isRefreshing != requestedRefresh) refresh.isRefreshing = requestedRefresh
    }
  }

  internal fun setRefreshing(value: Boolean) {
    requestedRefresh = value
    if (laidOut) refresh.isRefreshing = value else refreshStatePending = true
  }

  /** Scrolls to a pager-owned saved offset, retrying while content is too short to reach it. */
  internal fun restoreScrollOffset(y: Int) {
    val target = y.coerceAtLeast(0)
    viewport.scrollTo(0, target)
    pendingRestore.request(target, viewport.scrollY)
  }

  internal fun cancelPendingScrollRestore() = pendingRestore.cancel()

  internal fun setRefreshOffset(value: Int) {
    refreshOffset = value
    applyRefreshOffset()
  }

  internal fun setRefreshSize(value: String?) {
    val size = if (value == "large") SwipeRefreshLayout.LARGE else SwipeRefreshLayout.DEFAULT
    if (appliedRefreshSize == size) return
    appliedRefreshSize = size
    refresh.setSize(size)
    appliedRefreshOffset = null
    applyRefreshOffset()
  }

  private fun applyRefreshOffset() {
    if (!laidOut || appliedRefreshOffset == refreshOffset) return
    val diameter = refresh.progressCircleDiameter
    val wasRefreshing = refresh.isRefreshing
    refresh.setProgressViewOffset(
      false, refreshOffset - diameter, refreshOffset + dip(64.0) - diameter,
    )
    appliedRefreshOffset = refreshOffset
    // This component reserves content insets, not refresh-control insets.
    refresh.setTag(R.id.onekey_native_scroll_coordinator_refresh_inset, 0)
    if (wasRefreshing) refresh.isRefreshing = true
  }

  internal fun setRefreshColors(colors: IntArray) {
    refresh.setColorSchemeColors(*(if (colors.isEmpty()) intArrayOf(Color.GRAY) else colors))
  }
  internal fun setRefreshBackground(color: Int) = refresh.setProgressBackgroundColorSchemeColor(color)
  internal fun setScrollEnabled(value: Boolean) {
    viewport.scrollEnabled = value
    refresh.isEnabled = refreshEnabled && value
    if (!value) viewport.stopMotion()
  }

  internal fun scrollToPosition(x: Int, y: Int, animated: Boolean) {
    viewport.stopMotion()
    pendingRestore.cancel()
    if (animated) viewport.scrollToAnimated(y)
    else viewport.scrollTo(0, y.coerceAtLeast(0))
    emitScroll(force = true)
  }

  internal fun scrollToEnd(animated: Boolean) = scrollToPosition(
    0, (contentHeightPx + contentTop + insetBottom - viewport.height).coerceAtLeast(0), animated,
  )

  internal fun emitScroll(force: Boolean = false) {
    if (!isAttachedToWindow) return
    val now = SystemClock.uptimeMillis()
    if (!force && scrollEventThrottle > 16 && now - lastScrollEvent < scrollEventThrottle) return
    lastScrollEvent = now
    emit("topScroll", scrollPayload())
  }

  internal fun emitLifecycle(name: String, velocity: Double = 0.0) {
    if (!isAttachedToWindow) return
    emit(name, scrollPayload(velocity))
  }

  private fun scrollPayload(velocity: Double = 0.0): WritableMap = Arguments.createMap().apply {
    fun pair(first: String, firstValue: Double, second: String, secondValue: Double) =
      Arguments.createMap().apply { putDouble(first, firstValue); putDouble(second, secondValue) }
    putMap("contentOffset", pair("x", px(viewport.scrollX), "y", px(viewport.scrollY)))
    putMap("contentSize", pair("width", px(contentWidthPx), "height", px(contentHeightPx)))
    putMap("layoutMeasurement", pair("width", px(width), "height", px(height)))
    putMap("velocity", pair("x", 0.0, "y", velocity))
    putMap("targetContentOffset", pair("x", px(viewport.scrollX), "y", px(viewport.scrollY)))
    putMap("contentInset", Arguments.createMap().apply {
      putDouble("top", px(insetTop)); putDouble("right", px(insetRight))
      putDouble("bottom", px(insetBottom)); putDouble("left", px(insetLeft))
    })
    putDouble("zoomScale", 1.0)
  }

  private fun publishContentViewport() {
    val availableHeight = (viewport.height - contentTop - insetBottom).coerceAtLeast(0)
    if (lastContentViewportHeight == availableHeight) return
    if (emit("topContentViewportChange", Arguments.createMap().apply {
        putDouble("height", px(availableHeight))
      })) {
      lastContentViewportHeight = availableHeight
    }
  }

  private fun emit(name: String, payload: WritableMap): Boolean {
    if (!isAttachedToWindow || id == View.NO_ID) return false
    val reactContext = context as? ReactContext ?: return false
    val dispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, id) ?: return false
    dispatcher.dispatchEvent(
      NativeScrollerEvent(UIManagerHelper.getSurfaceId(this), id, name, payload),
    )
    return true
  }

  internal fun dismissKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
      ?.hideSoftInputFromWindow(windowToken, 0)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    viewport.setTag(R.id.onekey_native_scroll_coordinator_offset_listener, consumedOffsetListener)
    publishContentOrigin()
    scheduleLayout()
  }

  override fun onDetachedFromWindow() {
    lastContentViewportHeight = null
    viewport.stopMotion()
    viewport.setTag(R.id.onekey_native_scroll_coordinator_offset_listener, null)
    super.onDetachedFromWindow()
  }

  internal fun dip(value: Double) = PixelUtil.toPixelFromDIP(value).roundToInt()
  private fun px(value: Int) = PixelUtil.toDIPFromPixel(value.toFloat()).toDouble()
}

internal class NativeScrollerViewport(context: Context, internal val host: NativeScrollerView) : NestedScrollView(context) {
  var scrollEnabled = true
  private var dragging = false
  private var momentum = false
  private var stopping = false
  private var startingProgrammaticScroll = false
  private var downY = 0f
  private var directionDownX = 0f
  private var directionDownY = 0f
  private var directionPointerId = MotionEvent.INVALID_POINTER_ID
  private var smoothPagerTouch = false
  private var touchAxis = 0
  private var velocityTracker: VelocityTracker? = null
  private var activePointerId = MotionEvent.INVALID_POINTER_ID
  private val maximumVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
  private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

  init {
    isNestedScrollingEnabled = true
    isVerticalScrollBarEnabled = true
    overScrollMode = OVER_SCROLL_NEVER
  }

  private fun updateTouchDirection(event: MotionEvent) {
    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
      directionPointerId = event.getPointerId(0)
      directionDownX = event.x
      directionDownY = event.y
      downY = event.y
      touchAxis = 0
      smoothPagerTouch = false
      var ancestor = host.parent as? View
      while (ancestor != null) {
        if (ancestor is CollapsiblePagerHost && ancestor.nativeSmoothHeaderScrollEnabled) {
          smoothPagerTouch = true
          break
        }
        ancestor = ancestor.parent as? View
      }
      OneKeyLog.debug("NativeScroller", "viewport-down slop=$touchSlop density=${resources.displayMetrics.density} scrollY=$scrollY smooth=$smoothPagerTouch")
    } else if (event.actionMasked == MotionEvent.ACTION_POINTER_UP &&
      event.getPointerId(event.actionIndex) == directionPointerId) {
      val replacement = if (event.actionIndex == 0) 1 else 0
      directionPointerId = if (replacement < event.pointerCount) event.getPointerId(replacement)
        else MotionEvent.INVALID_POINTER_ID
      if (replacement < event.pointerCount) {
        directionDownX = event.getX(replacement)
        directionDownY = event.getY(replacement)
      }
    } else if (smoothPagerTouch && touchAxis == 0 && event.actionMasked == MotionEvent.ACTION_MOVE) {
      val pointerIndex = event.findPointerIndex(directionPointerId)
      if (pointerIndex < 0) return
      val dx = abs(event.getX(pointerIndex) - directionDownX)
      val dy = abs(event.getY(pointerIndex) - directionDownY)
      if (dy > touchSlop && dy > dx) touchAxis = 1
      else if (dx >= 32f * resources.displayMetrics.density && dx > dy) touchAxis = 2
      if (touchAxis != 0) {
        OneKeyLog.debug("NativeScroller", "viewport-direction axis=$touchAxis dx=$dx dy=$dy scrollY=$scrollY")
      }
    }
  }

  private var childDisallowsIntercept = false
  private var loggedBlockedVertical = false

  override fun dispatchTouchEvent(event: MotionEvent): Boolean {
    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
      childDisallowsIntercept = false
      loggedBlockedVertical = false
    }
    if (scrollEnabled) updateTouchDirection(event)
    if (smoothPagerTouch && touchAxis == 1 && childDisallowsIntercept &&
      event.actionMasked == MotionEvent.ACTION_MOVE) {
      childDisallowsIntercept = false
      OneKeyLog.debug("NativeScroller", "viewport-recover-vertical scrollY=$scrollY")
      super.requestDisallowInterceptTouchEvent(false)
    }
    if (smoothPagerTouch && event.actionMasked == MotionEvent.ACTION_MOVE && !loggedBlockedVertical) {
      val pointerIndex = event.findPointerIndex(directionPointerId)
      if (pointerIndex >= 0) {
        val dx = abs(event.getX(pointerIndex) - directionDownX)
        val dy = abs(event.getY(pointerIndex) - directionDownY)
        if (dy > touchSlop && dy > dx) {
          loggedBlockedVertical = true
          OneKeyLog.debug("NativeScroller", "viewport-dispatch-vertical dx=$dx dy=$dy axis=$touchAxis disallow=$childDisallowsIntercept scrollY=$scrollY")
        }
      }
    }
    try { return super.dispatchTouchEvent(event) } finally {
      if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
        releaseTouchDirection()
      }
    }
  }

  override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    childDisallowsIntercept = disallowIntercept
    if (smoothPagerTouch) OneKeyLog.debug("NativeScroller", "viewport-child-disallow value=$disallowIntercept axis=$touchAxis scrollY=$scrollY")
    super.requestDisallowInterceptTouchEvent(disallowIntercept)
  }

  override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
    if (!scrollEnabled) return false
    updateTouchDirection(event)
    if (smoothPagerTouch && event.actionMasked == MotionEvent.ACTION_MOVE && touchAxis != 1) return false
    val intercepted = super.onInterceptTouchEvent(event)
    if (intercepted && event.actionMasked == MotionEvent.ACTION_MOVE) {
      OneKeyLog.debug("NativeScroller", "viewport-intercept dy=${event.y - downY} slop=$touchSlop scrollY=$scrollY")
      beginDrag(event)
    }
    return intercepted
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (!scrollEnabled) return false
    updateTouchDirection(event)
    if (smoothPagerTouch && touchAxis != 1) {
      if (event.actionMasked == MotionEvent.ACTION_MOVE) return true
      if (event.actionMasked == MotionEvent.ACTION_UP) {
        val cancel = MotionEvent.obtain(event)
        cancel.action = MotionEvent.ACTION_CANCEL
        try { return super.onTouchEvent(cancel) } finally {
          cancel.recycle()
          releaseTouchDirection()
        }
      }
    }
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> downY = event.y
      MotionEvent.ACTION_MOVE -> {
        val pointerIndex = event.findPointerIndex(activePointerId)
        if (pointerIndex >= 0 && abs(event.getY(pointerIndex) - downY) > touchSlop) beginDrag(event)
      }
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> endDrag(event)
    }
    try { return super.onTouchEvent(event) } finally {
      if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
        releaseTouchDirection()
      }
    }
  }

  private fun releaseTouchDirection() {
    childDisallowsIntercept = false
    loggedBlockedVertical = false
    directionPointerId = MotionEvent.INVALID_POINTER_ID
    smoothPagerTouch = false
    touchAxis = 0
  }

  // Record at the refresh boundary so interception does not truncate the pointer history.
  internal fun recordTouchEvent(event: MotionEvent) {
    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
      host.cancelPendingScrollRestore()
      stopMotion()
      velocityTracker = VelocityTracker.obtain()
      activePointerId = event.getPointerId(0)
      downY = event.y
    } else if (event.actionMasked == MotionEvent.ACTION_POINTER_DOWN) {
      activePointerId = event.getPointerId(event.actionIndex)
      downY = event.getY(event.actionIndex)
    } else if (event.actionMasked == MotionEvent.ACTION_POINTER_UP &&
      event.getPointerId(event.actionIndex) == activePointerId) {
      val replacement = if (event.actionIndex == 0) 1 else 0
      activePointerId = if (replacement < event.pointerCount) event.getPointerId(replacement)
        else MotionEvent.INVALID_POINTER_ID
      if (replacement < event.pointerCount) downY = event.getY(replacement)
      velocityTracker?.clear()
    }
    velocityTracker?.addMovement(event)
  }

  internal fun releaseTouchTracking() {
    releaseTouchDirection()
    velocityTracker?.recycle()
    velocityTracker = null
    activePointerId = MotionEvent.INVALID_POINTER_ID
  }

  internal fun beginDrag(event: MotionEvent) {
    if (dragging) return
    dragging = true
    OneKeyLog.debug("NativeScroller", "viewport-begin-drag dy=${event.y - downY} slop=$touchSlop scrollY=$scrollY")
    // Header-forwarded touches bypass the refresh boundary's touch stream.
    host.cancelPendingScrollRestore()
    if (host.keyboardDismissMode != "none") host.dismissKeyboard()
    NativeGestureUtil.notifyNativeGestureStarted(host, event)
    host.emitLifecycle("topScrollBeginDrag")
  }

  internal fun endDrag(event: MotionEvent) {
    if (!dragging) return
    dragging = false
    host.emitScroll(force = true)
    val tracker = velocityTracker
    val velocity = if (event.actionMasked == MotionEvent.ACTION_UP && tracker != null &&
      activePointerId != MotionEvent.INVALID_POINTER_ID) {
      tracker.computeCurrentVelocity(1000, maximumVelocity.toFloat())
      tracker.getYVelocity(activePointerId) / 1000.0 / resources.displayMetrics.density
    } else 0.0
    host.emitLifecycle("topScrollEndDrag", velocity)
    NativeGestureUtil.notifyNativeGestureEnded(host, event)
  }

  override fun fling(velocityY: Int) {
    finishMomentum()
    momentum = true
    host.emitLifecycle("topMomentumScrollBegin", velocityY / 1000.0 / resources.displayMetrics.density)
    // AndroidX uses one unbounded trajectory with pre/content/post nested consumption.
    super.fling(velocityY)
  }

  internal fun scrollToAnimated(y: Int) {
    val child = getChildAt(0) ?: return
    val margins = child.layoutParams as FrameLayout.LayoutParams
    val range = (child.height + margins.topMargin + margins.bottomMargin -
      height + paddingTop + paddingBottom).coerceAtLeast(0)
    val target = y.coerceIn(0, range)
    if (target == scrollY) return
    momentum = true
    host.emitLifecycle("topMomentumScrollBegin")
    startingProgrammaticScroll = true
    try {
      // AndroidX stops nested scrolling while starting its programmatic animation.
      super.smoothScrollTo(0, target)
    } finally {
      startingProgrammaticScroll = false
    }
    // Commands within AndroidX's animation gap execute immediately instead of animating.
    if (scrollY == target) finishMomentum()
  }

  override fun stopNestedScroll(type: Int) {
    super.stopNestedScroll(type)
    if (type == ViewCompat.TYPE_NON_TOUCH && !stopping && !startingProgrammaticScroll) finishMomentum()
  }

  internal fun stopMotion() {
    if (dragging) {
      val now = SystemClock.uptimeMillis()
      val cancel = MotionEvent.obtain(now, now, MotionEvent.ACTION_CANCEL, 0f, 0f, 0)
      try { endDrag(cancel) } finally { cancel.recycle() }
    }
    releaseTouchTracking()
    // An idle view has nothing to stop. A zero fling would still start and stop a
    // non-touch nested scroll, which ancestors observe as a finished gesture.
    if (momentum || hasNestedScrollingParent(ViewCompat.TYPE_NON_TOUCH)) {
      stopping = true
      try {
        // Replace the same AndroidX trajectory with zero velocity without touching its smooth-scroll clock.
        super.fling(0)
        super.stopNestedScroll(ViewCompat.TYPE_NON_TOUCH)
      } finally {
        stopping = false
      }
    }
    finishMomentum()
  }

  private fun finishMomentum() {
    if (!momentum) return
    momentum = false
    host.emitScroll(force = true)
    host.emitLifecycle("topMomentumScrollEnd")
  }

  override fun onScrollChanged(x: Int, y: Int, oldX: Int, oldY: Int) {
    super.onScrollChanged(x, y, oldX, oldY)
    host.publishContentOrigin()
    host.emitScroll()
  }

  internal fun flashIndicators() = awakenScrollBars()
}

private class NativeScrollerRefreshLayout(context: Context) : SwipeRefreshLayout(context) {
  var onTouchStream: ((MotionEvent) -> Unit)? = null
  var onTouchStreamFinished: (() -> Unit)? = null
  var onGestureStart: ((MotionEvent) -> Unit)? = null
  var onGestureEnd: ((MotionEvent) -> Unit)? = null
  private var downX = 0f
  private var downY = 0f
  private var horizontal = false
  private var vertical = false
  private var smoothPagerGesture = false
  private var intercepted = false
  private val slop = ViewConfiguration.get(context).scaledTouchSlop

  override fun dispatchTouchEvent(event: MotionEvent): Boolean {
    onTouchStream?.invoke(event)
    try {
      return super.dispatchTouchEvent(event)
    } finally {
      if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
        onTouchStreamFinished?.invoke()
      }
    }
  }

  override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    super.requestDisallowInterceptTouchEvent(disallowIntercept)
    parent?.requestDisallowInterceptTouchEvent(disallowIntercept)
  }

  override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
      downX = event.x
      downY = event.y
      horizontal = false
      vertical = false
      smoothPagerGesture = false
      var ancestor = parent as? View
      while (ancestor != null) {
        if (ancestor is CollapsiblePagerHost && ancestor.nativeSmoothHeaderScrollEnabled) {
          smoothPagerGesture = true
          break
        }
        ancestor = ancestor.parent as? View
      }
      intercepted = false
    } else if (event.actionMasked == MotionEvent.ACTION_MOVE && (!smoothPagerGesture || !vertical)) {
      val dx = abs(event.x - downX)
      val dy = abs(event.y - downY)
      if (smoothPagerGesture && dy > slop && dy > dx) {
        vertical = true
        horizontal = false
      } else if (dx > (if (smoothPagerGesture) 32f * resources.displayMetrics.density else slop.toFloat()) && dx > dy) {
        horizontal = true
      }
    }
    if (horizontal) return false
    val result = super.onInterceptTouchEvent(event)
    if (result && !intercepted) {
      intercepted = true
      onGestureStart?.invoke(event)
    }
    return result
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    val result = super.onTouchEvent(event)
    if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
      if (intercepted) onGestureEnd?.invoke(event)
      intercepted = false
    }
    return result
  }
}

private class NativeScrollerEvent(
  surface: Int, tag: Int, private val name: String, private val payload: WritableMap,
) : Event<NativeScrollerEvent>(surface, tag) {
  override fun getEventName() = name
  override fun canCoalesce() = name == "topScroll"
  override fun getEventData() = payload
}
