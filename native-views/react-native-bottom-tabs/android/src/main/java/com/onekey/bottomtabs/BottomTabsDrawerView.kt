package com.onekey.bottomtabs

import android.animation.TimeAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.os.Build
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.SurfaceView
import android.view.TextureView
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
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import java.lang.ref.WeakReference

// Shared with iOS: measured frame by frame from the reference recording.
private const val MENU_WIDTH_FRACTION = .8f
private const val PAGE_CORNER_RADIUS_DP = 62f
private const val MENU_MIN_SCALE = .97f
private const val SPRING_FREQUENCY = 25.5f
private const val SPRING_DAMPING = .84f
private const val PAGE_SHADOW_SIGMA_DP = 18f
private val PAGE_HAIRLINE_COLOR = Color.argb(43, 255, 255, 255)

private class DrawerContent(context: Context) : FrameLayout(context), ReactPointerEventsView {
  override var pointerEvents = PointerEvents.AUTO
}

private class DrawerForeground(context: Context) : FrameLayout(context) {
  var cornerRadius = 0f
  var hairlineColor = Color.TRANSPARENT
  private val path = Path()
  private val rect = RectF()
  // Centered on the clip edge, so one physical pixel shows inside, like a layer border.
  private val hairline = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.STROKE
    strokeWidth = 2f
  }
  override fun dispatchDraw(canvas: Canvas) {
    val save = canvas.save()
    rect.set(0f, 0f, width.toFloat(), height.toFloat())
    val r = cornerRadius
    path.reset()
    path.addRoundRect(rect, floatArrayOf(r, r, 0f, 0f, 0f, 0f, r, r), Path.Direction.CW)
    canvas.clipPath(path)
    super.dispatchDraw(canvas)
    if (Color.alpha(hairlineColor) > 0) {
      hairline.color = hairlineColor
      canvas.drawPath(path, hairline)
    }
    canvas.restoreToCount(save)
  }
}

/** React owns two stable slots; translation, input and settling stay on the UI thread. */
class BottomTabsDrawerView(context: Context) : FrameLayout(context) {
  private val menu = DrawerContent(context)
  private val foreground = DrawerForeground(context)
  private val content = DrawerContent(context)
  private val dim = View(context)
  private val slots = mutableListOf<View>()
  var onDrawerOpenChange: ((Boolean) -> Unit)? = null
  var onDrawerProgress: ((Float) -> Unit)? = null
  var swipeEnabled = false
  var drawerHapticFeedbackEnabled = false
  var bottomBarHeight = 0f
  /** "light" or "dark" forces that palette; anything else follows the configuration's night mode. */
  var colorScheme = "system"
    set(value) {
      if (field == value) return
      field = value
      applyAppearance()
      setProgress(appliedValue, appliedOvershoot)
    }
  var progressEventsEnabled = false
    set(value) {
      if (field == value) return
      field = value
      // A new subscriber starts from the current position.
      reportedProgress = -1f
      if (value) reportProgress()
    }
  private var drawerEnabled = false
  private var requestedOpen = false
  private var stableOpen = false
  private var progress = 0f
  // The last requested position, kept so a layout pass mid-settle does not
  // clip the spring's overshoot back to fully open.
  private var appliedValue = 0f
  private var appliedOvershoot = false
  private var reportedProgress = -1f
  private var animator: TimeAnimator? = null
  private var animationTarget = false
  private var animationUser = false
  private var animationFrom = 0f
  private var animationVelocity = 0f
  private var animationDuration = 0f
  // Running speed of the current animation, for handing off to a retarget.
  private var trackedValue = 0f
  private var trackedTime = 0f
  private var trackedVelocity = 0f
  private var dark = false
  private var scrimMaxAlpha = .58f
  private var pageShadowOpacity = 0f
  private val shadowPaint = Paint()
  private var shadowEdge: Shader? = null
  private var shadowTopCorner: Shader? = null
  private var shadowBottomCorner: Shader? = null
  private var shadowRadius = -1f
  private var shadowHeight = -1
  private var shadowOpacity = -1f
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
  private val menuWidth get() = width * MENU_WIDTH_FRACTION

  init {
    clipChildren = true
    dim.contentDescription = "Close menu"
    dim.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
    dim.setOnClickListener { closeFromUser() }
    foreground.addView(content, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    foreground.addView(dim, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    super.addView(menu, LayoutParams(0, LayoutParams.MATCH_PARENT))
    super.addView(foreground, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    initialized = true
    applyAppearance()
    setProgress(0f)
  }

  private fun applyAppearance() {
    dark = when (colorScheme) {
      "dark" -> true
      "light" -> false
      else -> (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
    // The page washes toward white in light mode and toward gray in dark mode.
    dim.setBackgroundColor(if (dark) Color.rgb(36, 36, 36) else Color.WHITE)
    scrimMaxAlpha = if (dark) .58f else .5f
    pageShadowOpacity = if (dark) 0f else .16f
  }

  override fun onConfigurationChanged(newConfig: Configuration?) {
    super.onConfigurationChanged(newConfig)
    if (colorScheme == "light" || colorScheme == "dark") return
    applyAppearance()
    setProgress(appliedValue, appliedOvershoot)
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
    if (width > 0 && height > 0) layoutSlots()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    menu.measure(MeasureSpec.makeMeasureSpec((measuredWidth * MENU_WIDTH_FRACTION).roundToInt(), MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY))
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    layoutSlots()
    setProgress(appliedValue, appliedOvershoot)
  }

  private fun layoutSlots() {
    val exactHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
    val menuWidth = (width * MENU_WIDTH_FRACTION).roundToInt()
    menu.measure(MeasureSpec.makeMeasureSpec(menuWidth, MeasureSpec.EXACTLY), exactHeight)
    foreground.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), exactHeight)
    menu.layout(0, 0, menuWidth, height)
    foreground.layout(0, 0, width, height)
    // ReactViewGroup can absorb descendant requestLayout calls. A newly mounted
    // slot must receive native bounds during this mounting transaction itself.
    slots.forEachIndexed { index, child ->
      val childWidth = if (index == 0) menuWidth else width
      child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), exactHeight)
      child.layout(0, 0, childWidth, height)
    }
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
          (if (progress > 0f) event.x >= menuWidth * progress
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
      MotionEvent.ACTION_MOVE -> setProgress(downProgress + (event.x - downX) / maxOf(1f, menuWidth))
      MotionEvent.ACTION_UP -> {
        setProgress(downProgress + (event.x - downX) / maxOf(1f, menuWidth))
        tracker?.computeCurrentVelocity(1000)
        val velocity = ((tracker?.xVelocity ?: 0f) / maxOf(1f, menuWidth)).coerceIn(-12f, 12f)
        val target = progress + velocity * .12f >= .5f
        cancelTouch()
        settle(target, velocity, true, tween = false)
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

  private fun setProgress(value: Float, overshoot: Boolean = false) {
    appliedValue = value
    appliedOvershoot = overshoot
    val raw = if (drawerEnabled) value else 0f
    progress = raw.coerceIn(0f, 1f)
    val visible = progress > 0f
    // Only the settle spring may carry the page a few pixels past open.
    val travel = if (overshoot) raw.coerceIn(0f, 1.03f) else progress
    foreground.translationX = menuWidth * travel
    // Rounds in over the first 10% so screens with other corner radii never pop.
    foreground.cornerRadius = PAGE_CORNER_RADIUS_DP * density * min(1f, progress / .1f)
    foreground.hairlineColor = if (visible && dark) PAGE_HAIRLINE_COLOR else Color.TRANSPARENT
    foreground.invalidate()
    // The page shadow is drawn by this host, beneath the foreground.
    invalidate()
    dim.alpha = scrimMaxAlpha * progress
    dim.visibility = if (visible) VISIBLE else INVISIBLE
    dim.isEnabled = visible
    content.pointerEvents = if (visible) PointerEvents.NONE else PointerEvents.AUTO
    content.importantForAccessibility = if (visible) IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
      else IMPORTANT_FOR_ACCESSIBILITY_AUTO
    // The menu grows from its left-center edge and fades in over whatever is
    // behind the host, fully visible by 60% open.
    val minScale = if (animationsEnabled()) MENU_MIN_SCALE else 1f
    val scale = minScale + (1f - minScale) * progress
    menu.pivotX = 0f
    menu.pivotY = menu.height / 2f
    menu.scaleX = scale
    menu.scaleY = scale
    menu.alpha = min(1f, progress / .6f)
    // A partially transparent menu composites from one cached layer per frame.
    val layer = if (menu.alpha > 0f && menu.alpha < 1f) LAYER_TYPE_HARDWARE else LAYER_TYPE_NONE
    if (menu.layerType != layer) menu.setLayerType(layer, null)
    menu.visibility = if (visible) VISIBLE else INVISIBLE
    menu.pointerEvents = if (visible) PointerEvents.AUTO else PointerEvents.NONE
    menu.importantForAccessibility = if (visible) IMPORTANT_FOR_ACCESSIBILITY_AUTO
      else IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    updateBackCallback()
    reportProgress()
  }

  private fun reportProgress() {
    if (!progressEventsEnabled) return
    if (progress != reportedProgress && (abs(progress - reportedProgress) >= .002f || progress == 0f || progress == 1f)) {
      reportedProgress = progress
      onDrawerProgress?.invoke(progress)
    }
  }

  override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
    if (child === foreground && progress > 0f && pageShadowOpacity > 0f) drawPageShadow(canvas)
    return super.drawChild(canvas, child, drawingTime)
  }

  // A Gaussian shadow (no offset) around the page's left edge and corners,
  // matching the iOS layer shadow without an offscreen blur.
  private fun drawPageShadow(canvas: Canvas) {
    val sigma = PAGE_SHADOW_SIGMA_DP * density
    val extent = sigma * 3f
    val r = foreground.cornerRadius
    if (r != shadowRadius || height != shadowHeight || pageShadowOpacity != shadowOpacity) {
      shadowRadius = r
      shadowHeight = height
      shadowOpacity = pageShadowOpacity
      val stops = 8
      val positions = FloatArray(stops) { it / (stops - 1f) }
      // Outermost stop first: alpha at distance d outside the edge.
      val edgeColors = IntArray(stops) { shadowColor(extent * (1f - positions[it]), sigma) }
      shadowEdge = LinearGradient(-extent, 0f, 0f, 0f, edgeColors, positions, Shader.TileMode.CLAMP)
      val outer = r + extent
      val cornerPositions = FloatArray(stops) { (r + extent * it / (stops - 1f)) / outer }
      val cornerColors = IntArray(stops) { shadowColor(extent * it / (stops - 1f), sigma) }
      shadowTopCorner = RadialGradient(r, r, outer, cornerColors, cornerPositions, Shader.TileMode.CLAMP)
      shadowBottomCorner = RadialGradient(r, height - r, outer, cornerColors, cornerPositions, Shader.TileMode.CLAMP)
    }
    val save = canvas.save()
    canvas.translate(foreground.left + foreground.translationX, foreground.top.toFloat())
    shadowPaint.shader = shadowEdge
    canvas.drawRect(-extent, r, 0f, height - r, shadowPaint)
    if (r > 0f) {
      shadowPaint.shader = shadowTopCorner
      canvas.drawRect(-extent, -extent, r, r, shadowPaint)
      shadowPaint.shader = shadowBottomCorner
      canvas.drawRect(-extent, height - r, r, height + extent, shadowPaint)
    }
    canvas.restoreToCount(save)
  }

  private fun shadowColor(distance: Float, sigma: Float): Int {
    val z = distance / sigma
    // 1 - Φ(z), the share of a Gaussian blur that spills past an edge.
    val t = 1f / (1f + .2316419f * z)
    val tail = .3989423f * exp(-z * z / 2f) *
      t * (.31938153f + t * (-.356563782f + t * (1.781477937f + t * (-1.821255978f + t * 1.330274429f))))
    return Color.argb((pageShadowOpacity * tail * 255f).roundToInt().coerceIn(0, 255), 0, 0, 0)
  }

  private fun animationsEnabled(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ValueAnimator.areAnimatorsEnabled()
    else Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) != 0f

  // cubic-bezier(0.55, 0.6, 0.2, 1), fitted to the reference tap open and close.
  private fun pageCurve(x: Float): Float {
    fun bezier(t: Float, p1: Float, p2: Float): Float {
      val u = 1f - t
      return 3f * u * u * t * p1 + 3f * u * t * t * p2 + t * t * t
    }
    var low = 0f
    var high = 1f
    repeat(24) {
      val mid = (low + high) / 2f
      if (bezier(mid, .55f, .2f) < x) low = mid else high = mid
    }
    return bezier((low + high) / 2f, .6f, 1f)
  }

  // Taps and programmatic changes ease over a fixed time; a released drag
  // keeps its finger velocity through a slightly underdamped spring.
  private fun settle(open: Boolean, velocity: Float, user: Boolean, tween: Boolean = true) {
    val target = drawerEnabled && open
    // An animation already heading there keeps running; the newer request
    // still decides whether its completion is a user event.
    if (animator != null && animationTarget == target) {
      animationUser = user
      return
    }
    // Reversing mid-flight continues from the page's on-screen position and
    // speed, so the motion bends instead of stopping dead.
    val retarget = animator != null
    val from = if (retarget) appliedValue else progress
    val carried = if (retarget) trackedVelocity else velocity
    stopAnimation()
    if (width == 0 || !isAttachedToWindow) {
      stableOpen = target
      setProgress(if (target) 1f else 0f)
      return
    }
    if (!retarget && progress == if (target) 1f else 0f) {
      finish(target, user)
      return
    }
    // With system animations removed, the drawer lands without moving.
    if (!animationsEnabled()) {
      finish(target, user)
      return
    }
    animationFrom = from
    animationVelocity = carried.coerceIn(-12f, 12f)
    animationTarget = target
    animationUser = user
    // Closing runs about 20% shorter than opening.
    val fullDuration = if (target) .36f else .29f
    animationDuration = if (tween && !retarget) max(.12f, fullDuration * abs((if (target) 1f else 0f) - from)) else 0f
    trackedValue = from
    trackedTime = 0f
    trackedVelocity = animationVelocity
    val next = TimeAnimator()
    next.setTimeListener { animation, totalTime, _ -> tick(animation, totalTime / 1000f) }
    animator = next
    next.start()
  }

  private fun tick(animation: TimeAnimator, elapsed: Float) {
    if (animator !== animation) return
    val end = if (animationTarget) 1f else 0f
    val spring = animationDuration == 0f
    val value: Float
    var tweenDone = false
    if (!spring) {
      val x = min(1f, elapsed / animationDuration)
      value = animationFrom + (end - animationFrom) * pageCurve(x)
      tweenDone = x >= 1f
    } else {
      val w = SPRING_FREQUENCY
      val z = SPRING_DAMPING
      val a = animationFrom - end
      val wd = w * sqrt(1f - z * z)
      val decay = exp(-z * w * elapsed)
      value = end + decay * (a * cos(wd * elapsed) + (animationVelocity + z * w * a) / wd * sin(wd * elapsed))
    }
    if (elapsed > trackedTime) trackedVelocity = (value - trackedValue) / (elapsed - trackedTime)
    trackedValue = value
    trackedTime = elapsed
    setProgress(value, spring)
    // A spring ends once it is visually at rest, not after a fixed time.
    val done = if (spring) (abs(value - end) < .0005f && abs(trackedVelocity) < .02f) || elapsed >= .6f
      else tweenDone
    if (done) {
      val target = animationTarget
      val user = animationUser
      stopAnimation()
      finish(target, user)
    }
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
    animator?.setTimeListener(null)
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
