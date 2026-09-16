package com.rcttabview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import androidx.appcompat.view.ContextThemeWrapper
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.transition.TransitionManager
import android.util.Log
import android.util.Size
import android.util.TypedValue
import android.view.Choreographer
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEachIndexed
import coil3.ImageLoader
import coil3.asDrawable
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import coil3.size.Precision
import coil3.size.Size as CoilSize
import coil3.size.Scale
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.assets.ReactFontManager
import com.facebook.react.modules.core.ReactChoreographer
import com.facebook.react.uimanager.PointerEvents
import com.facebook.react.uimanager.ReactPointerEventsView
import com.facebook.react.views.text.ReactTypefaceUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView.LABEL_VISIBILITY_AUTO
import com.google.android.material.navigation.NavigationBarView.LABEL_VISIBILITY_LABELED
import com.google.android.material.navigation.NavigationBarView.LABEL_VISIBILITY_UNLABELED
import com.google.android.material.transition.platform.MaterialFadeThrough
import kotlin.math.abs
import kotlin.math.roundToInt

private fun getMaterialContext(context: Context): Context {
  return ContextThemeWrapper(context, com.google.android.material.R.style.Theme_MaterialComponents_DayNight)
}

class ExtendedBottomNavigationView(context: Context) : BottomNavigationView(getMaterialContext(context)) {

  fun setIgnoreBottomInsets(ignore: Boolean) {
    if (ignore) {
      setOnApplyWindowInsetsListener { v, insets -> insets }
      setPadding(paddingLeft, paddingTop, paddingRight, 0)
    } else {
      setOnApplyWindowInsetsListener(null)
      requestApplyInsets()
    }
  }

  override fun getMaxItemCount(): Int {
    return 100
  }
}

// OneKey patch: RN hit-testing traverses INVISIBLE native wrappers unless pointer events opt out.
private class TabSceneContainer(context: Context) : FrameLayout(context), ReactPointerEventsView {
  override var pointerEvents: PointerEvents = PointerEvents.NONE
}

private enum class AndroidTabBarStyle {
  DEFAULT,
  FLOATING,
}

class ReactBottomNavigationView(context: Context) : LinearLayout(context) {
  private var bottomNavigation = ExtendedBottomNavigationView(context)
  val layoutHolder = FrameLayout(context)
  private val floatingRoot = FrameLayout(context)
  private val floatingBar = FrameLayout(context)
  private val floatingDragHalo = View(context)
  private val floatingIndicator = View(context)

  var onTabSelectedListener: ((key: String) -> Unit)? = null
  var onTabLongPressedListener: ((key: String) -> Unit)? = null
  var onNativeLayoutListener: ((width: Double, height: Double) -> Unit)? = null
  var onTabBarMeasuredListener: ((height: Int) -> Unit)? = null
  var disablePageAnimations = false
    set(value) {
      if (field == value) {
        return
      }
      field = value
      layoutHolder.forEachIndexed { _, view ->
        if (view.visibility != VISIBLE) {
          view.visibility = if (value) INVISIBLE else GONE
        }
      }
      layoutHolder.requestLayout()
    }
  var items: MutableList<TabInfo> = mutableListOf()
  private val iconSources: MutableMap<Int, ImageSource> = mutableMapOf()
  private val drawableCache: MutableMap<ImageSource, Drawable> = mutableMapOf()

  private var isLayoutEnqueued = false
  private var selectedItem: String? = null
  private var activeTintColor: Int? = null
  private var inactiveTintColor: Int? = null
  private val checkedStateSet = intArrayOf(android.R.attr.state_checked)
  private val uncheckedStateSet = intArrayOf(-android.R.attr.state_checked)
  // OneKey patch: rely on View.isHapticFeedbackEnabled as the single source of truth.
  // private var hapticFeedbackEnabled = false
  private var fontSize: Int? = null
  private var fontFamily: String? = null
  private var fontWeight: Int? = null
  private var labeled: Boolean? = null
  private var lastReportedSize: Size? = null
  private var hasCustomAppearance = false
  private var uiModeConfiguration: Int = Configuration.UI_MODE_NIGHT_UNDEFINED
  private var androidTabBarStyle = AndroidTabBarStyle.DEFAULT
  private var ignoreBottomInsets = false
  private var isTabBarHidden = false
  private var floatingBottomInset = 0
  private var floatingLeftInset = 0
  private var floatingRightInset = 0
  private var floatingIndicatorPositioned = false
  private var floatingDragActive = false
  private var floatingIndicatorSettling = false
  private var floatingDragOriginalItemId = View.NO_ID
  private var floatingDragCandidateItemId = View.NO_ID
  private var floatingTouchView: View? = null
  private var floatingTouchStartRawX = 0f
  private var floatingTouchStartRawY = 0f
  private var floatingTouchMovedBeyondSlop = false
  private val floatingTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
  private val floatingBarLocationOnScreen = IntArray(2)
  private var barTintColor: Int? = null
  private var activeIndicatorColor: ColorStateList? = null
  private var rippleColor: ColorStateList? = null
  private var defaultItemBackground = bottomNavigation.itemBackground
  private var defaultBackgroundTintList = bottomNavigation.backgroundTintList
  private var defaultElevation = bottomNavigation.elevation
  private var defaultActiveIndicatorEnabled = bottomNavigation.isItemActiveIndicatorEnabled
  private var defaultActiveIndicatorColor = bottomNavigation.itemActiveIndicatorColor
  private var defaultMinimumHeight = bottomNavigation.minimumHeight
  private var defaultPaddingLeft = bottomNavigation.paddingLeft
  private var defaultPaddingTop = bottomNavigation.paddingTop
  private var defaultPaddingRight = bottomNavigation.paddingRight
  private var defaultPaddingBottom = bottomNavigation.paddingBottom

  private val imageLoader = ImageLoader.Builder(context)
    .components {
      add(SvgDecoder.Factory())
    }
    .build()

  init {
    orientation = VERTICAL
    layoutHolder.isSaveEnabled = false
    floatingRoot.clipChildren = false
    floatingRoot.clipToPadding = false
    floatingBar.clipChildren = true
    floatingBar.clipToPadding = false
    floatingBar.clipToOutline = true
    floatingBar.addOnLayoutChangeListener { _, left, _, right, _, oldLeft, _, oldRight, _ ->
      if (right - left != oldRight - oldLeft) {
        updateFloatingIndicator(animate = false)
      }
    }

    attachDefaultLayout()
    uiModeConfiguration = resources.configuration.uiMode

    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
      val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
      floatingBottomInset = navigationBars.bottom
      floatingLeftInset = navigationBars.left
      floatingRightInset = navigationBars.right
      if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) {
        updateFloatingBarLayout()
      }
      insets
    }

    post {
      addOnLayoutChangeListener { _, left, top, right, bottom,
                                  _, _, _, _ ->
        val newWidth = right - left
        val newHeight = bottom - top

        // Notify about tab bar height.
        onTabBarMeasuredListener?.invoke(
          Utils.convertPixelsToDp(context, getTabBarMeasuredHeight()).toInt()
        )

        if (newWidth != lastReportedSize?.width || newHeight != lastReportedSize?.height) {
          val dpWidth = Utils.convertPixelsToDp(context, layoutHolder.width)
          val dpHeight = Utils.convertPixelsToDp(context, layoutHolder.height)

          onNativeLayoutListener?.invoke(dpWidth, dpHeight)
          lastReportedSize = Size(newWidth, newHeight)
        }
      }
    }
  }

  private fun dp(value: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics).roundToInt()

  private fun removeFromParent(view: View) {
    (view.parent as? ViewGroup)?.removeView(view)
  }

  private fun attachDefaultLayout() {
    resetFloatingGesture()
    removeFromParent(layoutHolder)
    removeFromParent(bottomNavigation)
    removeFromParent(floatingRoot)
    removeFromParent(floatingBar)
    removeFromParent(floatingDragHalo)
    removeFromParent(floatingIndicator)
    super.removeAllViews()

    super.addView(
      layoutHolder,
      LayoutParams(LayoutParams.MATCH_PARENT, 0).apply { weight = 1f },
    )
    super.addView(
      bottomNavigation,
      LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT),
    )

    bottomNavigation.minimumHeight = defaultMinimumHeight
    bottomNavigation.setPadding(
      defaultPaddingLeft,
      defaultPaddingTop,
      defaultPaddingRight,
      defaultPaddingBottom,
    )
    bottomNavigation.setIgnoreBottomInsets(ignoreBottomInsets)
    bottomNavigation.elevation = defaultElevation
    bottomNavigation.isItemActiveIndicatorEnabled = defaultActiveIndicatorEnabled
    bottomNavigation.itemActiveIndicatorColor =
      activeIndicatorColor ?: defaultActiveIndicatorColor
    if (barTintColor != null) {
      applyDefaultBarBackground()
    } else {
      bottomNavigation.itemBackground = defaultItemBackground
      bottomNavigation.backgroundTintList = defaultBackgroundTintList
    }
    bottomNavigation.visibility = if (isTabBarHidden) GONE else VISIBLE
  }

  private fun attachFloatingLayout() {
    resetFloatingGesture()
    removeFromParent(layoutHolder)
    removeFromParent(bottomNavigation)
    removeFromParent(floatingRoot)
    removeFromParent(floatingBar)
    removeFromParent(floatingDragHalo)
    removeFromParent(floatingIndicator)
    super.removeAllViews()
    floatingRoot.removeAllViews()
    floatingBar.removeAllViews()

    floatingRoot.addView(
      layoutHolder,
      FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT,
      ),
    )
    floatingBar.addView(
      floatingDragHalo,
      FrameLayout.LayoutParams(0, dp(56f), Gravity.TOP or Gravity.START).apply {
        topMargin = dp(4f)
      },
    )
    floatingBar.addView(
      floatingIndicator,
      FrameLayout.LayoutParams(0, dp(56f), Gravity.TOP or Gravity.START).apply {
        topMargin = dp(4f)
      },
    )
    floatingBar.addView(
      bottomNavigation,
      FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        dp(64f),
      ),
    )
    floatingRoot.addView(
      floatingBar,
      FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        dp(64f),
        Gravity.BOTTOM,
      ),
    )
    super.addView(
      floatingRoot,
      LayoutParams(LayoutParams.MATCH_PARENT, 0).apply { weight = 1f },
    )

    bottomNavigation.minimumHeight = 0
    bottomNavigation.setPadding(0, 0, 0, 0)
    bottomNavigation.setIgnoreBottomInsets(true)
    bottomNavigation.isItemActiveIndicatorEnabled = false
    bottomNavigation.itemActiveIndicatorColor = ColorStateList.valueOf(Color.TRANSPARENT)
    bottomNavigation.itemBackground = ColorDrawable(Color.TRANSPARENT)
    bottomNavigation.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
    bottomNavigation.elevation = 0f
    bottomNavigation.visibility = VISIBLE
    floatingBar.elevation = dp(8f).toFloat()
    floatingBar.visibility = if (isTabBarHidden) GONE else VISIBLE
    floatingDragHalo.visibility = INVISIBLE
    floatingDragHalo.alpha = 0f
    floatingIndicatorPositioned = false
    updateFloatingAppearance()
    updateFloatingBarLayout()
    post { updateFloatingIndicator(animate = false) }
    ViewCompat.requestApplyInsets(this)
  }

  private fun updateFloatingBarLayout() {
    if (androidTabBarStyle != AndroidTabBarStyle.FLOATING) {
      return
    }
    val sideMargin = dp(16f)
    val params = floatingBar.layoutParams as? FrameLayout.LayoutParams ?: return
    params.leftMargin = sideMargin + floatingLeftInset
    params.rightMargin = sideMargin + floatingRightInset
    params.bottomMargin = dp(8f) + floatingBottomInset
    floatingBar.layoutParams = params
    floatingRoot.requestLayout()
  }

  private fun getTabBarMeasuredHeight(): Int {
    if (isTabBarHidden) {
      return 0
    }
    if (androidTabBarStyle != AndroidTabBarStyle.FLOATING) {
      return bottomNavigation.height
    }
    val params = floatingBar.layoutParams as? FrameLayout.LayoutParams
    return floatingBar.height + (params?.bottomMargin ?: 0)
  }

  private fun blendColor(base: Int, overlay: Int, amount: Float): Int {
    val inverse = 1f - amount
    return Color.argb(
      Color.alpha(base),
      (Color.red(base) * inverse + Color.red(overlay) * amount).roundToInt(),
      (Color.green(base) * inverse + Color.green(overlay) * amount).roundToInt(),
      (Color.blue(base) * inverse + Color.blue(overlay) * amount).roundToInt(),
    )
  }

  private fun isDarkColor(color: Int): Boolean {
    val luminance =
      (Color.red(color) * 0.299f) +
        (Color.green(color) * 0.587f) +
        (Color.blue(color) * 0.114f)
    return luminance < 128f
  }

  private fun roundedBackground(color: Int, radius: Float): Drawable =
    GradientDrawable().apply {
      shape = GradientDrawable.RECTANGLE
      cornerRadius = dp(radius).toFloat()
      setColor(color)
    }

  private fun outlinedBackground(color: Int, radius: Float): Drawable =
    GradientDrawable().apply {
      shape = GradientDrawable.RECTANGLE
      cornerRadius = dp(radius).toFloat()
      setColor(Color.TRANSPARENT)
      setStroke(dp(1f), color)
    }

  private fun resolvedBarTintColor(): Int =
    barTintColor
      ?: Utils.getDefaultColorFor(context, android.R.attr.colorBackground)
      ?: Color.BLACK

  private fun applyDefaultBarBackground() {
    val color = resolvedBarTintColor()
    bottomNavigation.itemBackground = ColorDrawable(color)
    bottomNavigation.backgroundTintList = ColorStateList.valueOf(color)
  }

  private fun updateFloatingAppearance() {
    if (androidTabBarStyle != AndroidTabBarStyle.FLOATING) {
      return
    }
    val baseColor = resolvedBarTintColor()
    val contrastColor = if (isDarkColor(baseColor)) Color.WHITE else Color.BLACK
    val surfaceColor = blendColor(baseColor, contrastColor, 0.10f)
    val indicatorColor = activeIndicatorColor?.defaultColor
      ?: blendColor(surfaceColor, contrastColor, 0.12f)
    val haloColor = blendColor(surfaceColor, contrastColor, 0.36f)
    floatingBar.background = roundedBackground(surfaceColor, 32f)
    floatingDragHalo.background = outlinedBackground(haloColor, 29f)
    floatingIndicator.background = roundedBackground(indicatorColor, 28f)
  }

  private fun updateFloatingIndicator(animate: Boolean) {
    if (
      androidTabBarStyle != AndroidTabBarStyle.FLOATING ||
      isTabBarHidden ||
      floatingDragActive ||
      floatingIndicatorSettling
    ) {
      return
    }
    val selectedKey = selectedItem ?: return
    val selectedIndex = items.indexOfFirst { it.key == selectedKey }
    if (selectedIndex == -1) {
      return
    }
    val itemView = bottomNavigation.findViewById<View>(selectedIndex) ?: return
    if (itemView.width == 0 || floatingBar.width == 0) {
      floatingIndicator.visibility = INVISIBLE
      return
    }

    val horizontalInset = dp(4f)
    val targetX = getFloatingIndicatorTargetX(selectedIndex) ?: return
    val targetWidth = (itemView.width - horizontalInset * 2).coerceAtLeast(dp(48f))
    updateFloatingIndicatorWidth(targetWidth)
    floatingIndicator.visibility = VISIBLE

    if (!animate || !floatingIndicatorPositioned) {
      floatingIndicator.animate().cancel()
      floatingIndicator.x = targetX
      floatingIndicator.scaleX = 1f
      floatingIndicator.scaleY = 1f
      floatingDragHalo.x = targetX
      floatingIndicatorPositioned = true
      return
    }

    val movingRight = targetX >= floatingIndicator.x
    floatingIndicator.animate().cancel()
    floatingIndicator.pivotX = if (movingRight) 0f else floatingIndicator.width.toFloat()
    floatingIndicator.pivotY = floatingIndicator.height / 2f
    floatingIndicator.animate()
      .x(targetX)
      .scaleX(1.16f)
      .scaleY(0.94f)
      .setDuration(220L)
      .setInterpolator(DecelerateInterpolator())
      .withEndAction {
        floatingIndicator.pivotX = floatingIndicator.width / 2f
        floatingIndicator.animate()
          .scaleX(1f)
          .scaleY(1f)
          .setDuration(180L)
          .setInterpolator(OvershootInterpolator(0.8f))
          .start()
      }
      .start()
  }

  private fun updateFloatingIndicatorWidth(width: Int) {
    listOf(floatingIndicator, floatingDragHalo).forEach { view ->
      val params = view.layoutParams as? FrameLayout.LayoutParams ?: return@forEach
      if (params.width != width) {
        params.width = width
        view.layoutParams = params
      }
    }
  }

  private fun getFloatingIndicatorTargetX(itemId: Int): Float? {
    val menuView = bottomNavigation.getChildAt(0) as? ViewGroup ?: return null
    val itemView = bottomNavigation.findViewById<View>(itemId) ?: return null
    if (itemView.width == 0 || itemView.visibility != VISIBLE) {
      return null
    }
    return bottomNavigation.x + menuView.x + itemView.x + dp(4f)
  }

  private fun getNearestVisibleItemId(centerX: Float): Int? {
    val menuView = bottomNavigation.getChildAt(0) as? ViewGroup ?: return null
    var nearestItemId: Int? = null
    var nearestDistance = Float.MAX_VALUE
    items.indices.forEach { itemId ->
      val itemView = bottomNavigation.findViewById<View>(itemId) ?: return@forEach
      if (itemView.visibility != VISIBLE || itemView.width == 0) {
        return@forEach
      }
      val itemCenterX = bottomNavigation.x + menuView.x + itemView.x + itemView.width / 2f
      val distance = abs(itemCenterX - centerX)
      if (distance < nearestDistance) {
        nearestDistance = distance
        nearestItemId = itemId
      }
    }
    return nearestItemId
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun handleFloatingTabTouch(view: View, item: MenuItem, event: MotionEvent): Boolean {
    if (androidTabBarStyle != AndroidTabBarStyle.FLOATING) {
      return false
    }

    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        if (item.itemId != bottomNavigation.selectedItemId) {
          return false
        }
        resetFloatingGesture()
        floatingTouchView = view
        floatingTouchStartRawX = event.rawX
        floatingTouchStartRawY = event.rawY
        floatingTouchMovedBeyondSlop = false
        floatingDragOriginalItemId = item.itemId
        floatingDragCandidateItemId = item.itemId
        view.isPressed = true
        beginFloatingDrag(item.itemId)
        return true
      }
      MotionEvent.ACTION_MOVE -> {
        if (floatingTouchView !== view) {
          return true
        }
        if (!floatingTouchMovedBeyondSlop) {
          val deltaX = event.rawX - floatingTouchStartRawX
          val deltaY = event.rawY - floatingTouchStartRawY
          if (deltaX * deltaX + deltaY * deltaY > floatingTouchSlop * floatingTouchSlop) {
            floatingTouchMovedBeyondSlop = true
            view.isPressed = false
            emitHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
          } else {
            return true
          }
        }
        updateFloatingDrag(event.rawX)
        return true
      }
      MotionEvent.ACTION_UP -> {
        view.isPressed = false
        if (floatingDragActive) {
          if (floatingTouchMovedBeyondSlop) {
            updateFloatingDrag(event.rawX)
            finishFloatingDrag(cancelled = false)
          } else {
            finishFloatingDrag(cancelled = true)
          }
        }
        if (!floatingTouchMovedBeyondSlop) {
          view.performClick()
        }
        clearFloatingTouchTracking()
        return true
      }
      MotionEvent.ACTION_CANCEL -> {
        view.isPressed = false
        if (floatingDragActive) {
          finishFloatingDrag(cancelled = true)
        }
        clearFloatingTouchTracking()
        return true
      }
    }
    return true
  }

  private fun beginFloatingDrag(itemId: Int) {
    if (
      itemId != bottomNavigation.selectedItemId ||
      androidTabBarStyle != AndroidTabBarStyle.FLOATING
    ) {
      return
    }
    updateFloatingIndicator(animate = false)
    if (!floatingIndicatorPositioned || floatingIndicator.visibility != VISIBLE) {
      return
    }

    floatingDragActive = true
    floatingIndicatorSettling = false
    floatingDragOriginalItemId = itemId
    floatingDragCandidateItemId = itemId
    floatingTouchView?.isPressed = false
    floatingIndicator.animate().cancel()
    floatingDragHalo.animate().cancel()
    floatingIndicator.pivotX = floatingIndicator.width / 2f
    floatingIndicator.pivotY = floatingIndicator.height / 2f
    floatingDragHalo.x = floatingIndicator.x
    floatingDragHalo.pivotX = floatingDragHalo.width / 2f
    floatingDragHalo.pivotY = floatingDragHalo.height / 2f
    floatingDragHalo.alpha = 0f
    floatingDragHalo.scaleX = 0.94f
    floatingDragHalo.scaleY = 0.94f
    floatingDragHalo.visibility = VISIBLE
    floatingIndicator.animate()
      .scaleX(1.08f)
      .scaleY(1.08f)
      .setDuration(160L)
      .setInterpolator(OvershootInterpolator(0.7f))
      .start()
    floatingDragHalo.animate()
      .alpha(0.72f)
      .scaleX(1.16f)
      .scaleY(1.14f)
      .setDuration(180L)
      .setInterpolator(OvershootInterpolator(0.55f))
      .start()
  }

  private fun updateFloatingDrag(rawX: Float) {
    if (!floatingDragActive || floatingBar.width == 0 || floatingIndicator.width == 0) {
      return
    }

    floatingBar.getLocationOnScreen(floatingBarLocationOnScreen)
    val halfWidth = floatingIndicator.width / 2f
    val edgeInset = dp(4f).toFloat()
    val centerX = (rawX - floatingBarLocationOnScreen[0])
      .coerceIn(halfWidth + edgeInset, floatingBar.width - halfWidth - edgeInset)
    val targetX = centerX - halfWidth
    val deltaX = targetX - floatingIndicator.x
    val nextX = floatingIndicator.x + deltaX * 0.62f
    val stretch = (abs(deltaX) / floatingIndicator.width).coerceAtMost(1f) * 0.18f

    floatingIndicator.animate().cancel()
    floatingDragHalo.animate().cancel()
    floatingIndicator.x = nextX
    floatingIndicator.scaleX = 1.08f + stretch
    floatingIndicator.scaleY = 1.08f - stretch * 0.35f
    floatingDragHalo.x = nextX
    floatingDragHalo.alpha = 0.72f
    floatingDragHalo.scaleX = 1.16f + stretch
    floatingDragHalo.scaleY = 1.14f - stretch * 0.25f

    getNearestVisibleItemId(centerX)?.let { itemId ->
      if (itemId != floatingDragCandidateItemId) {
        floatingDragCandidateItemId = itemId
      }
    }
    floatingBar.postInvalidateOnAnimation()
  }

  private fun finishFloatingDrag(cancelled: Boolean) {
    if (!floatingDragActive) {
      return
    }
    val originalItemId = floatingDragOriginalItemId
    val requestedItemId = floatingDragCandidateItemId.takeIf { it != View.NO_ID }
      ?: originalItemId
    val requestedItem = items.getOrNull(requestedItemId)
    val targetItemId = if (cancelled || requestedItem?.preventsDefault == true) {
      originalItemId
    } else {
      requestedItemId
    }

    floatingDragActive = false
    floatingIndicatorSettling = true
    if (!cancelled && requestedItemId != originalItemId) {
      bottomNavigation.menu.findItem(requestedItemId)?.let { menuItem ->
        onTabSelected(menuItem, emitHaptic = false)
      }
    }

    val targetX = getFloatingIndicatorTargetX(targetItemId) ?: floatingIndicator.x
    floatingIndicator.animate().cancel()
    floatingDragHalo.animate().cancel()
    floatingDragHalo.animate()
      .x(targetX)
      .alpha(0f)
      .scaleX(1f)
      .scaleY(1f)
      .setDuration(160L)
      .setInterpolator(DecelerateInterpolator())
      .start()
    floatingIndicator.animate()
      .x(targetX)
      .scaleX(1f)
      .scaleY(1f)
      .setDuration(220L)
      .setInterpolator(OvershootInterpolator(0.72f))
      .withEndAction {
        floatingDragHalo.visibility = INVISIBLE
        floatingDragHalo.alpha = 0f
        floatingDragOriginalItemId = View.NO_ID
        floatingDragCandidateItemId = View.NO_ID
        floatingIndicatorSettling = false
        updateFloatingIndicator(animate = false)
      }
      .start()
  }

  private fun clearFloatingTouchTracking() {
    floatingTouchView = null
    floatingTouchMovedBeyondSlop = false
    floatingTouchStartRawX = 0f
    floatingTouchStartRawY = 0f
  }

  private fun resetFloatingGesture() {
    floatingTouchView?.isPressed = false
    clearFloatingTouchTracking()
    floatingIndicator.animate().cancel()
    floatingDragHalo.animate().cancel()
    floatingIndicator.scaleX = 1f
    floatingIndicator.scaleY = 1f
    floatingDragHalo.alpha = 0f
    floatingDragHalo.scaleX = 1f
    floatingDragHalo.scaleY = 1f
    floatingDragHalo.visibility = INVISIBLE
    floatingDragActive = false
    floatingIndicatorSettling = false
    floatingDragOriginalItemId = View.NO_ID
    floatingDragCandidateItemId = View.NO_ID
  }

  private val layoutCallback = Choreographer.FrameCallback {
    isLayoutEnqueued = false
    refreshLayout()
  }

  private fun refreshLayout() {
    measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
    )
    layout(left, top, right, bottom)
  }

  override fun requestLayout() {
    super.requestLayout()
    @Suppress("SENSELESS_COMPARISON") // layoutCallback can be null here since this method can be called in init

    if (!isLayoutEnqueued && layoutCallback != null) {
      isLayoutEnqueued = true
      // we use NATIVE_ANIMATED_MODULE choreographer queue because it allows us to catch the current
      // looper loop instead of enqueueing the update in the next loop causing a one frame delay.
      ReactChoreographer
        .getInstance()
        .postFrameCallback(
          ReactChoreographer.CallbackType.NATIVE_ANIMATED_MODULE,
          layoutCallback,
        )
    }
  }

  fun setSelectedItem(value: String) {
    selectedItem = value
    syncSelectedItem()
  }

  private fun syncSelectedItem() {
    val selectedKey = selectedItem ?: return
    val selectedIndex = items.indexOfFirst { it.key == selectedKey }
    if (selectedIndex == -1 || selectedIndex >= layoutHolder.childCount) {
      return
    }
    setSelectedIndex(selectedIndex)
  }

  override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
    if (child === layoutHolder || child === bottomNavigation || child === floatingRoot) {
      super.addView(child, index, params)
      return
    }

    val container = createContainer()
    container.addView(child, params)
    layoutHolder.addView(container, index)

    if (index in items.indices && selectedItem == items[index].key) {
      syncSelectedItem()
      refreshLayout()
    }
  }

  private fun createContainer(): FrameLayout {
    val container = TabSceneContainer(context).apply {
      layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT
      )
      isSaveEnabled = false
      visibility = if (disablePageAnimations) INVISIBLE else GONE
      isEnabled = false
    }
    return container
  }

  private fun setSelectedIndex(itemId: Int) {
    val selectionChanged = bottomNavigation.selectedItemId != itemId
    bottomNavigation.selectedItemId = itemId
    if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) {
      post { updateFloatingIndicator(animate = selectionChanged) }
    }
    if (disablePageAnimations) {
      // INVISIBLE scenes stay laid out, so this visibility swap is atomic at
      // the next draw boundary and does not expose an empty container frame.
      toggleViewVisibility(layoutHolder.getChildAt(itemId), true)
      layoutHolder.forEachIndexed { index, view ->
        if (itemId != index) {
          toggleViewVisibility(view, false)
        }
      }
      layoutHolder.invalidate()
      return
    }
    if (layoutHolder.getChildAt(itemId)?.visibility == VISIBLE) {
      return
    }
    val fadeThrough = MaterialFadeThrough()
    TransitionManager.beginDelayedTransition(layoutHolder, fadeThrough)
    // Apply every visibility change to the transition's captured frame.
    layoutHolder.forEachIndexed { index, view ->
      if (itemId == index) {
        toggleViewVisibility(view, true)
      } else {
        toggleViewVisibility(view, false)
      }
    }

    layoutHolder.requestLayout()
    layoutHolder.invalidate()
  }

  private fun toggleViewVisibility(view: View, isVisible: Boolean) {
    check(view is TabSceneContainer) { "Native component tree is corrupted." }

    view.pointerEvents = if (isVisible) PointerEvents.AUTO else PointerEvents.NONE
    view.visibility = if (isVisible) {
      VISIBLE
    } else if (disablePageAnimations) {
      INVISIBLE
    } else {
      GONE
    }
    view.isEnabled = isVisible
  }

  private fun onTabSelected(item: MenuItem, emitHaptic: Boolean = true) {
    val selectedItem = items[item.itemId]
    selectedItem.let {
      if (!selectedItem.preventsDefault) {
        setSelectedItem(selectedItem.key)
      }
      onTabSelectedListener?.invoke(selectedItem.key)
      if (emitHaptic) {
        emitHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
      }
    }
  }

  private fun onTabLongPressed(item: MenuItem) {
    val longPressedItem = items[item.itemId]
    longPressedItem.let {
      onTabLongPressedListener?.invoke(longPressedItem.key)
      emitHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
  }

  fun setIgnoreBottomInsets(ignore: Boolean) {
    ignoreBottomInsets = ignore
    bottomNavigation.setIgnoreBottomInsets(
      if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) true else ignore
    )
  }

  fun setAndroidTabBarStyle(value: String?) {
    val nextStyle = if (value == "floating") {
      AndroidTabBarStyle.FLOATING
    } else {
      AndroidTabBarStyle.DEFAULT
    }
    if (nextStyle == androidTabBarStyle) {
      return
    }
    androidTabBarStyle = nextStyle
    if (nextStyle == AndroidTabBarStyle.FLOATING) {
      attachFloatingLayout()
    } else {
      attachDefaultLayout()
    }
    requestLayout()
  }

  fun setTabBarHidden(isHidden: Boolean) {
    isTabBarHidden = isHidden
    if (isHidden) {
      resetFloatingGesture()
    }
    if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) {
      floatingBar.visibility = if (isHidden) GONE else VISIBLE
      bottomNavigation.visibility = VISIBLE
    } else {
      bottomNavigation.visibility = if (isHidden) GONE else VISIBLE
    }

    // Force re-measure and notify JS about the new layout dimensions
    post {
      val dpWidth = Utils.convertPixelsToDp(context, layoutHolder.width)
      val dpHeight = Utils.convertPixelsToDp(context, layoutHolder.height)
      onNativeLayoutListener?.invoke(dpWidth, dpHeight)

      // Update tab bar height measurement (0 when hidden)
      val tabBarHeight = Utils.convertPixelsToDp(context, getTabBarMeasuredHeight()).toInt()
      onTabBarMeasuredListener?.invoke(tabBarHeight)
      if (!isHidden) {
        updateFloatingIndicator(animate = false)
      }
    }
  }

  fun updateItems(items: MutableList<TabInfo>) {
    // If an item got removed, let's re-add all items
    if (items.size < this.items.size) {
      bottomNavigation.menu.clear()
    }
    this.items = items
    items.forEachIndexed { index, item ->
      val menuItem = getOrCreateItem(index, item.title)
      if (item.title != menuItem.title) {
        menuItem.title = item.title
      }

      menuItem.isVisible = !item.hidden
      if (iconSources.containsKey(index)) {
        getDrawable(iconSources[index]!!) {
          menuItem.icon = it
        }
      }

      if (item.badge?.isNotEmpty() == true) {
        val badge = bottomNavigation.getOrCreateBadge(index)
        badge.isVisible = true
        // Set the badge text only if it's different than an empty space to show a small badge.
        // More context: https://github.com/callstackincubator/react-native-bottom-tabs/issues/422
        if (item.badge != " ") {
          badge.text = item.badge
        }
        // Apply badge colors if provided (Material will use its default theme colors otherwise)
        item.badgeBackgroundColor?.let { badge.backgroundColor = it }
        item.badgeTextColor?.let { badge.badgeTextColor = it }
      } else {
        bottomNavigation.removeBadge(index)
      }
      post {
        val itemView = bottomNavigation.findViewById<View>(menuItem.itemId)
        itemView?.let { view ->
          view.setOnLongClickListener {
            onTabLongPressed(menuItem)
            true
          }
          view.setOnClickListener {
            onTabSelected(menuItem)
          }
          view.setOnTouchListener { touchedView, event ->
            handleFloatingTabTouch(touchedView, menuItem, event)
          }

          item.testID?.let { testId ->
            view.findViewById<View>(com.google.android.material.R.id.navigation_bar_item_content_container)
              ?.apply {
                tag = testId
              }
          }
        }
      }
    }

    syncSelectedItem()

    // Update tint colors and text appearance after updating all items.
    post {
      updateTextAppearance()
      updateTintColors()
      updateFloatingIndicator(animate = false)
    }
  }

  private fun getOrCreateItem(index: Int, title: String): MenuItem {
    return bottomNavigation.menu.findItem(index) ?: bottomNavigation.menu.add(0, index, 0, title)
  }

  fun setIcons(icons: ReadableArray?) {
    if (icons == null || icons.size() == 0) {
      return
    }

    for (idx in 0 until icons.size()) {
      val source = icons.getMap(idx)
      val uri = source?.getString("uri")
      if (uri.isNullOrEmpty()) {
        continue
      }

      val imageSource = ImageSource(context, uri)
      this.iconSources[idx] = imageSource

      // Update existing item if exists.
      bottomNavigation.menu.findItem(idx)?.let { menuItem ->
        getDrawable(imageSource) {
          menuItem.icon = it
        }
      }
    }
  }

  fun setLabeled(labeled: Boolean?) {
    this.labeled = labeled
    bottomNavigation.labelVisibilityMode = when (labeled) {
      false -> {
        LABEL_VISIBILITY_UNLABELED
      }
      true -> {
        LABEL_VISIBILITY_LABELED
      }
      else -> {
        LABEL_VISIBILITY_AUTO
      }
    }
  }

  fun setRippleColor(color: ColorStateList) {
    rippleColor = color
    bottomNavigation.itemRippleColor = color
  }

  @SuppressLint("CheckResult")
  private fun getDrawable(imageSource: ImageSource, onDrawableReady: (Drawable?) -> Unit) {
    drawableCache[imageSource]?.let {
      onDrawableReady(it)
      return
    }
    val iconSizePx = bottomNavigation.itemIconSize
    val request = ImageRequest.Builder(context)
      .data(imageSource.getUri(context))
      .size(CoilSize(iconSizePx, iconSizePx))
      .scale(Scale.FILL)
      .precision(Precision.EXACT)
      .target { drawable ->
        post {
          val stateDrawable = drawable.asDrawable(context.resources)
          drawableCache[imageSource] = stateDrawable
          onDrawableReady(stateDrawable)
        }
      }
      .listener(
        onError = { _, result ->
          Log.e("RCTTabView", "Error loading image: ${imageSource.uri}", result.throwable)
        }
      )
      .build()

    imageLoader.enqueue(request)
  }

  fun setBarTintColor(color: Int?) {
    // Set the color, either using the active background color or a default color.
    val backgroundColor =
      color ?: Utils.getDefaultColorFor(context, android.R.attr.colorPrimary) ?: return
    barTintColor = backgroundColor

    if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) {
      updateFloatingAppearance()
    } else {
      bottomNavigation.itemBackground = ColorDrawable(backgroundColor)
      bottomNavigation.backgroundTintList = ColorStateList.valueOf(backgroundColor)
    }
    hasCustomAppearance = true
  }

  fun setActiveTintColor(color: Int?) {
    activeTintColor = color
    updateTintColors()
  }

  fun setInactiveTintColor(color: Int?) {
    inactiveTintColor = color
    updateTintColors()
  }

  fun setActiveIndicatorColor(color: ColorStateList) {
    activeIndicatorColor = color
    if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) {
      updateFloatingAppearance()
    } else {
      bottomNavigation.itemActiveIndicatorColor = color
    }
  }

  fun setFontSize(size: Int) {
    fontSize = size
    updateTextAppearance()
  }

  fun setFontFamily(family: String?) {
    fontFamily = family
    updateTextAppearance()
  }

  fun setFontWeight(weight: String?) {
    val fontWeight = ReactTypefaceUtils.parseFontWeight(weight)
    this.fontWeight = fontWeight
    updateTextAppearance()
  }

  fun onDropViewInstance() {
    resetFloatingGesture()
    imageLoader.shutdown()
  }

  private fun updateTextAppearance() {
    // Early return if there is no custom text appearance
    if (fontSize == null && fontFamily == null && fontWeight == null) {
      return
    }

    val typeface = if (fontFamily != null || fontWeight != null) {
      ReactFontManager.getInstance().getTypeface(
        fontFamily ?: "",
        Utils.getTypefaceStyle(fontWeight),
        context.assets
      )
    } else null
    val size = fontSize?.toFloat()?.takeIf { it > 0 }

    val menuView = bottomNavigation.getChildAt(0) as? ViewGroup ?: return
    for (i in 0 until menuView.childCount) {
      val item = menuView.getChildAt(i)
      val largeLabel =
        item.findViewById<TextView>(com.google.android.material.R.id.navigation_bar_item_large_label_view)
      val smallLabel =
        item.findViewById<TextView>(com.google.android.material.R.id.navigation_bar_item_small_label_view)

      listOf(largeLabel, smallLabel).forEach { label ->
        label?.apply {
          size?.let { size ->
            setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
          }
          typeface?.let { setTypeface(it) }
        }
      }
    }
  }

  private fun emitHapticFeedback(feedbackConstants: Int) {
    // OneKey patch: use the Fabric-populated View flag on every supported API level.
    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && hapticFeedbackEnabled) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isHapticFeedbackEnabled) {
      this.performHapticFeedback(feedbackConstants)
    }
  }

  private fun updateTintColors() {
    // First let's check current item color.
    val currentItemTintColor = items.firstOrNull { it.key == selectedItem }?.activeTintColor

    // getDefaultColor will always return a valid color but to satisfy the compiler we need to check for null
    val colorPrimary = currentItemTintColor ?: activeTintColor ?: Utils.getDefaultColorFor(
      context,
      android.R.attr.colorPrimary
    ) ?: return
    val colorSecondary =
      inactiveTintColor ?: Utils.getDefaultColorFor(context, android.R.attr.textColorSecondary)
      ?: return
    val states = arrayOf(uncheckedStateSet, checkedStateSet)
    val colors = intArrayOf(colorSecondary, colorPrimary)

    ColorStateList(states, colors).apply {
      this@ReactBottomNavigationView.bottomNavigation.itemTextColor = this
      this@ReactBottomNavigationView.bottomNavigation.itemIconTintList = this
    }
  }

  override fun onConfigurationChanged(newConfig: Configuration?) {
    super.onConfigurationChanged(newConfig)
    if (uiModeConfiguration == newConfig?.uiMode || hasCustomAppearance) {
      return
    }

    // User has hidden the bottom navigation bar, don't re-attach it.
    if (isTabBarHidden) {
      return
    }

    // If appearance wasn't changed re-create the bottom navigation view when configuration changes.
    // React Native opts out ouf Activity re-creation when configuration changes, this workarounds that.
    // We also opt-out of this recreation when custom styles are used.
    removeFromParent(bottomNavigation)
    bottomNavigation = ExtendedBottomNavigationView(context)
    defaultItemBackground = bottomNavigation.itemBackground
    defaultBackgroundTintList = bottomNavigation.backgroundTintList
    defaultElevation = bottomNavigation.elevation
    defaultActiveIndicatorEnabled = bottomNavigation.isItemActiveIndicatorEnabled
    defaultActiveIndicatorColor = bottomNavigation.itemActiveIndicatorColor
    defaultMinimumHeight = bottomNavigation.minimumHeight
    defaultPaddingLeft = bottomNavigation.paddingLeft
    defaultPaddingTop = bottomNavigation.paddingTop
    defaultPaddingRight = bottomNavigation.paddingRight
    defaultPaddingBottom = bottomNavigation.paddingBottom
    if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) {
      attachFloatingLayout()
    } else {
      attachDefaultLayout()
    }
    updateItems(items)
    setLabeled(this.labeled)
    rippleColor?.let { bottomNavigation.itemRippleColor = it }
    this.selectedItem?.let { setSelectedItem(it) }
    uiModeConfiguration = newConfig?.uiMode ?: uiModeConfiguration
  }
}
