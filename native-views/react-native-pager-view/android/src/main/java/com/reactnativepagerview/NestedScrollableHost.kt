package com.reactnativepagerview

import android.content.Context
import android.util.AttributeSet
import android.view.Choreographer
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewConfiguration
import android.widget.HorizontalScrollView
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import com.facebook.react.uimanager.events.NativeGestureUtil
import com.margelo.nitro.nativelogger.OneKeyLog
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * Layout to wrap a scrollable component inside a ViewPager2. Provided as a solution to the problem
 * where pages of ViewPager2 have nested scrollable elements that scroll in the same direction as
 * ViewPager2. The scrollable element needs to be the immediate and only child of this host layout.
 *
 * Supports multiple levels of nested PagerViews by re-asserting
 * requestDisallowInterceptTouchEvent after child dispatch.
 */
// OneKey patch: Let the collapsible pager reuse same-direction nested pager coordination.
// Original: class NestedScrollableHost : FrameLayout {
open class NestedScrollableHost : FrameLayout {
  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  public var initialIndex: Int? = null
  public var didSetInitialIndex = false
  public var pendingRefreshFrameCallback: Choreographer.FrameCallback? = null
  private var touchSlop = 0
  private var initialX = 0f
  private var initialY = 0f
  private var smoothTouchActive = false
  private var smoothTouchAxis = 0
  private var smoothDownX = 0f
  private var smoothDownY = 0f
  private var nestedHorizontalOwner: View? = null
  protected var nestedNativeGestureStarted: Boolean = false
  private val parentViewPager: ViewPager2?
    get() {
      var v: View? = parent as? View
      while (v != null && v !is ViewPager2) {
        v = v.parent as? View
      }
      return v as? ViewPager2
    }

  private val child: View? get() = if (childCount > 0) getChildAt(0) else null

  init {
    touchSlop = ViewConfiguration.get(context).scaledTouchSlop
  }

  private fun canChildScroll(orientation: Int, delta: Float): Boolean {
    val direction = -delta.sign.toInt()
    return when (orientation) {
      0 -> child?.canScrollHorizontally(direction) ?: false
      1 -> child?.canScrollVertically(direction) ?: false
      else -> throw IllegalArgumentException()
    }
  }

  private fun hasSmoothHeaderOwner(): Boolean {
    var ancestor: View? = this
    while (ancestor != null) {
      if (ancestor is CollapsiblePagerHost && ancestor.nativeSmoothHeaderScrollEnabled) return true
      ancestor = ancestor.parent as? View
    }
    return false
  }

  private fun horizontalOwnerAt(view: View, x: Float, y: Float): View? {
    if (view is ViewGroup) {
      for (index in view.childCount - 1 downTo 0) {
        val nested = view.getChildAt(index)
        if (nested.visibility != View.VISIBLE || nested.alpha <= 0f) continue
        val localX = x + view.scrollX - nested.left - nested.translationX
        val localY = y + view.scrollY - nested.top - nested.translationY
        if (localX < 0 || localY < 0 || localX >= nested.width || localY >= nested.height) continue
        horizontalOwnerAt(nested, localX, localY)?.let { return it }
        break
      }
    }
    // Exclude this host's own pager; only an inner horizontal viewport has priority.
    if (view.parent !== child && (view is HorizontalScrollView || view is RecyclerView) &&
      (view.canScrollHorizontally(-1) || view.canScrollHorizontally(1))) return view
    return null
  }

  private fun coordinateSmoothTouch(e: MotionEvent) {
    val pager = child as? ViewPager2 ?: return
    if (e.actionMasked == MotionEvent.ACTION_DOWN) {
      smoothTouchActive = hasSmoothHeaderOwner()
      smoothTouchAxis = 0
      smoothDownX = e.x
      smoothDownY = e.y
      nestedHorizontalOwner = if (smoothTouchActive) horizontalOwnerAt(this, e.x, e.y) else null
    }
    if (!smoothTouchActive) return
    val dx = e.x - smoothDownX
    val dy = e.y - smoothDownY
    if (e.actionMasked == MotionEvent.ACTION_MOVE && smoothTouchAxis == 0) {
      smoothTouchAxis = when {
        dy.absoluteValue > touchSlop && dy.absoluteValue > dx.absoluteValue -> 1
        dx.absoluteValue >= 32f * resources.displayMetrics.density && dx.absoluteValue > dy.absoluteValue -> 2
        else -> 0
      }
      if (smoothTouchAxis != 0) {
        OneKeyLog.debug("CollapsiblePager", "content-direction owner=${if (smoothTouchAxis == 1) "list" else "horizontal"} dx=$dx dy=$dy nested=${nestedHorizontalOwner != null}")
      }
    }
    val innerCanScroll = nestedHorizontalOwner?.canScrollHorizontally(-dx.sign.toInt()) == true
    // Preserve DOWN in the real pager, but defer horizontal interception while
    // intent is pending. Ancestor vertical lists must still be able to intercept.
    (pager.getChildAt(0) as? RecyclerView)?.requestDisallowInterceptTouchEvent(smoothTouchAxis != 2 || innerCanScroll)
    parent?.requestDisallowInterceptTouchEvent(false)
  }

  override fun dispatchTouchEvent(e: MotionEvent): Boolean {
    coordinateSmoothTouch(e)
    val handled = super.dispatchTouchEvent(e)
    if (smoothTouchActive && (e.actionMasked == MotionEvent.ACTION_UP || e.actionMasked == MotionEvent.ACTION_CANCEL)) {
      if (nestedNativeGestureStarted) {
        NativeGestureUtil.notifyNativeGestureEnded(this, e)
        nestedNativeGestureStarted = false
      }
      smoothTouchActive = false
      smoothTouchAxis = 0
      nestedHorizontalOwner = null
    }

    // After the full child dispatch cycle, a deeply-nested NestedScrollableHost may
    // have called requestDisallowInterceptTouchEvent(false) which propagates all the
    // way up, overriding our earlier requestDisallowInterceptTouchEvent(true).
    // Re-assert here if our own ViewPager2 can still scroll in the gesture direction.
    if (e.action == MotionEvent.ACTION_MOVE) {
      val orientation = parentViewPager?.orientation ?: return handled
      val dx = e.x - initialX
      val dy = e.y - initialY
      val isVpHorizontal = orientation == ORIENTATION_HORIZONTAL
      val scaledDx = dx.absoluteValue * if (isVpHorizontal) .5f else 1f
      val scaledDy = dy.absoluteValue * if (isVpHorizontal) 1f else .5f

      if (scaledDx > touchSlop || scaledDy > touchSlop) {
        if (isVpHorizontal != (scaledDy > scaledDx)) {
          // Parallel gesture — re-assert if our VP can scroll
          if (canChildScroll(orientation, if (isVpHorizontal) dx else dy)) {
            parent.requestDisallowInterceptTouchEvent(true)
          }
        }
      }
    }

    return handled
  }

  override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
    handleInterceptTouchEvent(e)
    return super.onInterceptTouchEvent(e)
  }

  private fun handleInterceptTouchEvent(e: MotionEvent) {
    val orientation = parentViewPager?.orientation

    if (e.action == MotionEvent.ACTION_DOWN) {
      initialX = e.x
      initialY = e.y
      if (orientation != null) {
        parent.requestDisallowInterceptTouchEvent(true)
      }
    } else if (e.action == MotionEvent.ACTION_MOVE) {
      val dx = e.x - initialX
      val dy = e.y - initialY
      val isVpHorizontal = orientation == ORIENTATION_HORIZONTAL

      // assuming ViewPager2 touch-slop is 2x touch-slop of child
      val scaledDx = dx.absoluteValue * if (isVpHorizontal) .5f else 1f
      val scaledDy = dy.absoluteValue * if (isVpHorizontal) 1f else .5f

      if (scaledDx > touchSlop || scaledDy > touchSlop) {
        NativeGestureUtil.notifyNativeGestureStarted(this, e)
        nestedNativeGestureStarted = true

        if (orientation == null) return
        if (isVpHorizontal == (scaledDy > scaledDx)) {
          // Gesture is perpendicular, allow all parents to intercept
          parent.requestDisallowInterceptTouchEvent(false)
        } else {
          // Gesture is parallel, query child if movement in that direction is possible
          if (canChildScroll(orientation, if (isVpHorizontal) dx else dy)) {
            // Child can scroll, disallow all parents to intercept
            parent.requestDisallowInterceptTouchEvent(true)
          } else {
            // Child cannot scroll, allow all parents to intercept
            parent.requestDisallowInterceptTouchEvent(false)
          }
        }
      }
    }
  }

  override fun onTouchEvent(e: MotionEvent): Boolean {
    if (e.actionMasked == MotionEvent.ACTION_UP) {
      if (nestedNativeGestureStarted) {
        NativeGestureUtil.notifyNativeGestureEnded(this, e)
        nestedNativeGestureStarted = false
      }
    }
    return super.onTouchEvent(e)
  }

  override fun onDetachedFromWindow() {
    pendingRefreshFrameCallback?.let { callback ->
      Choreographer.getInstance().removeFrameCallback(callback)
      pendingRefreshFrameCallback = null
    }
    super.onDetachedFromWindow()
  }
}
