package com.onekey.bottomtabs

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.SurfaceView
import android.view.TextureView
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.AbsSeekBar
import android.widget.EditText
import android.widget.CompoundButton
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.PointerEvents
import com.facebook.react.uimanager.ReactPointerEventsView
import com.facebook.react.uimanager.events.NativeGestureUtil
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt
import java.lang.ref.WeakReference

private class DrawerContent(context: Context) : FrameLayout(context), ReactPointerEventsView {
  override var pointerEvents = PointerEvents.AUTO
}

private class DrawerForeground(context: Context) : FrameLayout(context) {
  var cornerRadius = 0f
  private val path = Path()
  private val rect = RectF()
  override fun dispatchDraw(canvas: Canvas) {
    val save = canvas.save()
    rect.set(0f, 0f, width.toFloat(), height.toFloat())
    val r = cornerRadius
    path.reset()
    path.addRoundRect(rect, floatArrayOf(r, r, 0f, 0f, 0f, 0f, r, r), Path.Direction.CW)
    canvas.clipPath(path)
    super.dispatchDraw(canvas)
    canvas.restoreToCount(save)
  }
}

/** React owns two stable slots; translation, input and settling stay on the UI thread. */
class BottomTabsDrawerView(context: Context) : FrameLayout(context) {
  private val menu = DrawerContent(context)
  private val menuShade = View(context)
  private val foreground = DrawerForeground(context)
  private val content = DrawerContent(context)
  private val dim = View(context)
  private val slots = mutableListOf<View>()
  var onDrawerOpenChange: ((Boolean) -> Unit)? = null
  var swipeEnabled = false
  var drawerHapticFeedbackEnabled = false
  var bottomBarHeight = 0f
  private var drawerEnabled = false
  private var requestedOpen = false
  private var stableOpen = false
  private var progress = 0f
  private var animator: ValueAnimator? = null
  private var animationTarget = false
  private var animationUser = false
  private var downX = 0f
  private var downY = 0f
  private var downProgress = 0f
  private var gestureReturnOpen = false
  private var eligible = false
  private var touchedView: WeakReference<View>? = null
  private val touchMatrix = Matrix()
  private val touchPoint = FloatArray(2)
  private var dragging = false
  private var tracker: VelocityTracker? = null
  private var lastNativeEvent: MotionEvent? = null
  private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
  private val density = resources.displayMetrics.density
  private var backRegistered = false
  private var detached = true
  private var initialized = false
  private val backCallback = object : OnBackPressedCallback(false) {
    override fun handleOnBackPressed() { closeFromUser() }
  }

  init {
    clipChildren = true
    menuShade.setBackgroundColor(Color.BLACK)
    menuShade.isEnabled = false
    menuShade.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    dim.setBackgroundColor(Color.rgb(28, 28, 28))
    dim.contentDescription = "Close menu"
    dim.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
    dim.setOnClickListener { closeFromUser() }
    menu.addView(menuShade, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    foreground.addView(content, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    foreground.addView(dim, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    super.addView(menu, LayoutParams(0, LayoutParams.MATCH_PARENT))
    super.addView(foreground, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    initialized = true
    setProgress(0f)
  }

  fun setDrawerEnabled(value: Boolean) {
    if (drawerEnabled == value) return
    cancelTouch()
    stopAnimation()
    drawerEnabled = value
    stableOpen = false
    setProgress(0f)
    if (value && requestedOpen) settle(true, 0f, false)
  }

  fun setDrawerOpen(value: Boolean) {
    if (requestedOpen == value) return
    requestedOpen = value
    cancelTouch()
    settle(drawerEnabled && value, 0f, false)
  }

  fun addReactChild(child: View, index: Int) {
    require(index in 0..slots.size && slots.size < 2)
    slots.add(index, child)
    updateSlots()
  }

  fun getReactChildCount() = slots.size
  fun getReactChild(index: Int): View = slots[index]
  fun removeReactChild(index: Int) {
    val child = slots.removeAt(index)
    (child.parent as? ViewGroup)?.removeView(child)
    updateSlots()
  }

  private fun updateSlots() {
    slots.forEachIndexed { index, child ->
      (child.parent as? ViewGroup)?.removeView(child)
      (if (index == 0) menu else content).addView(
        child, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
      )
    }
    requestLayout()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    menu.measure(MeasureSpec.makeMeasureSpec((measuredWidth * .78f).roundToInt(), MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY))
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    menu.layout(0, 0, (width * .78f).roundToInt(), height)
    foreground.layout(0, 0, width, height)
    setProgress(progress)
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    val target = if (animator != null) animationTarget else if (dragging) gestureReturnOpen else stableOpen
    val user = animator != null && animationUser && oldw != 0
    cancelTouch()
    stopAnimation()
    finish(drawerEnabled && if (oldw == 0) requestedOpen else target, user)
  }

  override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        cancelTouch()
        downX = event.x
        downY = event.y
        downProgress = progress
        touchedView = touchedViewAt(this, event.x, event.y)?.let(::WeakReference)
        val systemLeft = ViewCompat.getRootWindowInsets(this)
          ?.getInsets(WindowInsetsCompat.Type.systemGestures())?.left ?: 0
        eligible = drawerEnabled && event.y < height - bottomBarHeight * density &&
          (if (progress > 0f) event.x >= width * .78f * progress
          else swipeEnabled && event.x >= systemLeft && !touchPathHasExclusiveControl())
        if (eligible) tracker = VelocityTracker.obtain().also { it.addMovement(event) }
      }
      MotionEvent.ACTION_POINTER_DOWN -> {
        val wasDragging = dragging
        cancelTouch()
        if (wasDragging) settle(gestureReturnOpen, 0f, false)
      }
      MotionEvent.ACTION_MOVE -> {
        if (dragging) return true
        if (!eligible) return false
        tracker?.addMovement(event)
        val dx = event.x - downX
        val dy = event.y - downY
        if (abs(dy) > touchSlop && abs(dy) >= abs(dx)) {
          eligible = false
          return false
        }
        if (abs(dx) > touchSlop && abs(dx) > abs(dy) && progress == 0f &&
          (dx < 0f || touchPathCanScrollRight())) {
          // This decision remains locked even if the child reaches its boundary.
          eligible = false
          return false
        }
        if (abs(dx) > touchSlop && abs(dx) > abs(dy) && (progress > 0f || dx > 0f)) {
          gestureReturnOpen = if (animator != null && !animationUser) animationTarget else stableOpen
          stopAnimation()
          downProgress = progress
          dragging = true
          parent?.requestDisallowInterceptTouchEvent(true)
          NativeGestureUtil.notifyNativeGestureStarted(this, event)
          rememberNativeEvent(event)
          return true
        }
      }
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (!dragging) cancelTouch()
    }
    return dragging
  }

  override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    // Nested pagers may disallow on DOWN, before direction/boundary is known.
    // Once the child wins (eligible=false), preserve its normal ownership.
    if (initialized && eligible && !dragging && disallowIntercept) return
    super.requestDisallowInterceptTouchEvent(disallowIntercept)
  }

  override fun dispatchTouchEvent(event: MotionEvent): Boolean {
    val handled = super.dispatchTouchEvent(event)
    if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) cancelTouch()
    return handled
  }

  private fun touchedViewAt(view: View, x: Float, y: Float): View? {
    var pointerEvents = (view as? ReactPointerEventsView)?.pointerEvents ?: PointerEvents.AUTO
    if (!view.isEnabled) pointerEvents = when (pointerEvents) {
      PointerEvents.AUTO -> PointerEvents.BOX_NONE
      PointerEvents.BOX_ONLY -> PointerEvents.NONE
      else -> pointerEvents
    }
    if (pointerEvents == PointerEvents.NONE) return null
    if (view is ViewGroup && pointerEvents != PointerEvents.BOX_ONLY) {
      // Fabric already orders native children for React zIndex. Native elevation
      // can additionally reorder siblings; examine frontmost candidates first.
      val order = (0 until view.childCount).sortedWith(
        compareByDescending<Int> { view.getChildAt(it).z }.thenByDescending { it },
      )
      for (index in order) {
        val child = view.getChildAt(index)
        if (child.visibility != VISIBLE || child.alpha <= 0f) continue
        touchPoint[0] = x + view.scrollX - child.left
        touchPoint[1] = y + view.scrollY - child.top
        if (!child.matrix.isIdentity) {
          if (!child.matrix.invert(touchMatrix)) continue
          touchMatrix.mapPoints(touchPoint)
        }
        val localX = touchPoint[0]
        val localY = touchPoint[1]
        if (localX >= 0 && localY >= 0 && localX < child.width && localY < child.height) {
          touchedViewAt(child, localX, localY)?.let { return it }
        }
      }
    }
    return if (pointerEvents == PointerEvents.BOX_NONE) null else view
  }

  private fun touchPathHasExclusiveControl(): Boolean {
    var view = touchedView?.get()
    while (view != null && view !== this) {
      if (view is AbsSeekBar || view is EditText || view is CompoundButton || view is WebView ||
        view is SurfaceView || view is TextureView) return true
      view = view.parent as? View
    }
    return false
  }

  private fun touchPathCanScrollRight(): Boolean {
    var view = touchedView?.get()
    while (view != null && view !== this) {
      // ViewPager2 reports its scroll range even when user input is disabled.
      // Skip only that pager and its direct RecyclerView, leaving any enabled
      // horizontal list inside the page eligible to own its own gesture.
      val pager = (view as? ViewPager2) ?: (view.parent as? ViewPager2)
      if (pager?.isUserInputEnabled != false && view.canScrollHorizontally(-1)) return true
      view = view.parent as? View
    }
    return false
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (!dragging) {
      // A disabled pager or an empty native page may decline DOWN entirely.
      // Retain that eligible stream and apply the same first-direction decision
      // when Android routes later events straight to this host's onTouchEvent.
      if (event.actionMasked == MotionEvent.ACTION_DOWN) return eligible || super.onTouchEvent(event)
      val pending = eligible
      if (pending) onInterceptTouchEvent(event)
      if (!dragging) return pending || super.onTouchEvent(event)
    }
    rememberNativeEvent(event)
    tracker?.addMovement(event)
    when (event.actionMasked) {
      MotionEvent.ACTION_MOVE -> setProgress(downProgress + (event.x - downX) / maxOf(1f, width * .78f))
      MotionEvent.ACTION_UP -> {
        setProgress(downProgress + (event.x - downX) / maxOf(1f, width * .78f))
        tracker?.computeCurrentVelocity(1000)
        val velocity = ((tracker?.xVelocity ?: 0f) / maxOf(1f, width * .78f)).coerceIn(-8f, 8f)
        val target = progress + velocity * .12f >= .5f
        cancelTouch()
        settle(target, velocity, true)
      }
      MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_POINTER_DOWN -> {
        cancelTouch()
        settle(gestureReturnOpen, 0f, false)
      }
    }
    return true
  }

  private fun rememberNativeEvent(event: MotionEvent) {
    lastNativeEvent?.recycle()
    lastNativeEvent = MotionEvent.obtain(event)
  }

  private fun cancelTouch() {
    if (dragging) {
      lastNativeEvent?.let { NativeGestureUtil.notifyNativeGestureEnded(this, it) }
      parent?.requestDisallowInterceptTouchEvent(false)
    }
    dragging = false
    eligible = false
    touchedView = null
    tracker?.recycle()
    tracker = null
    lastNativeEvent?.recycle()
    lastNativeEvent = null
  }

  private fun closeFromUser() {
    cancelTouch()
    settle(false, 0f, true)
  }

  private fun setProgress(value: Float) {
    progress = (if (drawerEnabled) value else 0f).coerceIn(0f, 1f)
    foreground.translationX = width * .78f * progress
    foreground.cornerRadius = 64f * density * progress
    foreground.invalidate()
    dim.alpha = .7f * progress
    dim.visibility = if (progress > 0f) VISIBLE else GONE
    content.pointerEvents = if (progress > 0f) PointerEvents.NONE else PointerEvents.AUTO
    content.importantForAccessibility = if (progress > 0f) IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
      else IMPORTANT_FOR_ACCESSIBILITY_AUTO
    menu.translationX = -.12f * width * .78f * (1f - progress)
    menuShade.alpha = .12f * (1f - progress)
    menu.visibility = if (progress > 0f) VISIBLE else INVISIBLE
    menu.pointerEvents = if (progress > 0f) PointerEvents.AUTO else PointerEvents.NONE
    menu.importantForAccessibility = if (progress > 0f) IMPORTANT_FOR_ACCESSIBILITY_AUTO
      else IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    updateBackCallback()
  }

  private fun settle(open: Boolean, velocity: Float, user: Boolean) {
    stopAnimation()
    val target = drawerEnabled && open
    if (width == 0 || !isAttachedToWindow) {
      stableOpen = target
      setProgress(if (target) 1f else 0f)
      return
    }
    if (progress == if (target) 1f else 0f) {
      finish(target, user)
      return
    }
    val end = if (target) 1f else 0f
    val a = progress - end
    val b = velocity.coerceIn(-8f, 8f) + 22f * a
    animationTarget = target
    animationUser = user
    val next = ValueAnimator.ofFloat(0f, .5f)
    next.duration = 500
    next.interpolator = LinearInterpolator()
    next.addUpdateListener {
      val time = it.animatedValue as Float
      setProgress(end + (a + b * time) * exp(-22f * time))
    }
    next.addListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator) {
        if (animator !== animation) return
        animator = null
        finish(target, user)
      }
    })
    animator = next
    next.start()
  }

  private fun finish(target: Boolean, user: Boolean) {
    val changed = stableOpen != target
    stableOpen = target
    setProgress(if (target) 1f else 0f)
    if (user && (changed || target != requestedOpen)) {
      if (drawerHapticFeedbackEnabled && changed) performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
      onDrawerOpenChange?.invoke(target)
    }
  }

  private fun stopAnimation() {
    animator?.removeAllListeners()
    animator?.removeAllUpdateListeners()
    animator?.cancel()
    animator = null
  }

  private fun updateBackCallback() {
    val active = !detached && isAttachedToWindow && isShown && windowVisibility == VISIBLE && drawerEnabled && progress > 0f
    if (active && !backRegistered) {
      val owner = findActivity(context) as? OnBackPressedDispatcherOwner
      if (owner != null) {
        owner.onBackPressedDispatcher.addCallback(backCallback)
        backRegistered = true
      }
    } else if (!active && backRegistered) {
      backCallback.remove()
      backRegistered = false
    }
    backCallback.isEnabled = active
  }

  private fun findActivity(context: Context): Activity? = when (context) {
    is Activity -> context
    is ReactContext -> context.currentActivity
    is ContextWrapper -> findActivity(context.baseContext)
    else -> null
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    detached = false
    updateBackCallback()
    if (drawerEnabled && stableOpen != requestedOpen) settle(requestedOpen, 0f, false)
  }

  override fun onVisibilityChanged(changedView: View, visibility: Int) {
    super.onVisibilityChanged(changedView, visibility)
    if (initialized) updateBackCallback()
  }

  override fun onWindowVisibilityChanged(visibility: Int) {
    super.onWindowVisibilityChanged(visibility)
    if (initialized) updateBackCallback()
  }

  override fun onDetachedFromWindow() {
    dispose()
    super.onDetachedFromWindow()
  }

  fun dispose() {
    detached = true
    cancelTouch()
    stopAnimation()
    backCallback.remove()
    backRegistered = false
    setProgress(if (stableOpen && drawerEnabled) 1f else 0f)
  }
}
