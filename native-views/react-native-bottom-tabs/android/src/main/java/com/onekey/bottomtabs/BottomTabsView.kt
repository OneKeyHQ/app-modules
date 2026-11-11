package com.onekey.bottomtabs

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import androidx.appcompat.view.ContextThemeWrapper
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.SystemClock
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
import android.view.ViewOutlineProvider
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import coil3.ImageLoader
import coil3.asDrawable
import coil3.request.ImageRequest
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
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationBarView.LABEL_VISIBILITY_AUTO
import com.google.android.material.navigation.NavigationBarView.LABEL_VISIBILITY_LABELED
import com.google.android.material.navigation.NavigationBarView.LABEL_VISIBILITY_UNLABELED
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.margelo.nitro.onekeyimage.OneKeyNamedSVG
import kotlin.math.abs
import kotlin.math.roundToInt

private const val ONEKEY_ICON_URI_PREFIX = "onekey-icon://"

private fun getMaterialContext(context: Context): Context {
  return ContextThemeWrapper(context, com.google.android.material.R.style.Theme_MaterialComponents_DayNight)
}

class ExtendedBottomNavigationView(context: Context) : BottomNavigationView(getMaterialContext(context)) {
  private val basePaddingStart = paddingStart
  private val basePaddingTop = paddingTop
  private val basePaddingEnd = paddingEnd
  private val basePaddingBottom = paddingBottom
  private var ignoreBottomInsets = false
  private var ignoreKeyboardInsets = true
  // Floating mode lays the bar out inside an inset container that already
  // accounts for system bars and the keyboard, so the menu keeps fixed padding.
  private var floatingHorizontalPadding: Int? = null

  init {
    // OneKey patch: Material pads the bar with getSystemWindowInsetBottom(), which
    // includes the IME height only while the window uses adjustResize (switched on by
    // react-native-keyboard-controller hooks), so whether the bar rose above the
    // keyboard depended on the screen. Pad for system bars, and add the IME only when
    // ignoreKeyboardInsets is turned off.
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
      floatingHorizontalPadding?.let { padding ->
        view.setPaddingRelative(padding, 0, padding, 0)
        return@setOnApplyWindowInsetsListener insets
      }
      val barInsets = insets.getInsets(
        WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
      )
      val navigationBarBottom = if (ignoreBottomInsets) 0 else barInsets.bottom
      val keyboardBottom = if (ignoreKeyboardInsets) {
        0
      } else {
        insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
      }
      val isRtl = view.layoutDirection == View.LAYOUT_DIRECTION_RTL
      view.setPaddingRelative(
        basePaddingStart + if (isRtl) barInsets.right else barInsets.left,
        basePaddingTop,
        basePaddingEnd + if (isRtl) barInsets.left else barInsets.right,
        basePaddingBottom + maxOf(navigationBarBottom, keyboardBottom),
      )
      insets
    }
  }

  fun setIgnoreBottomInsets(ignore: Boolean) {
    if (ignoreBottomInsets == ignore) {
      return
    }
    ignoreBottomInsets = ignore
    requestApplyInsets()
  }

  fun setIgnoreKeyboardInsets(ignore: Boolean) {
    if (ignoreKeyboardInsets == ignore) {
      return
    }
    ignoreKeyboardInsets = ignore
    requestApplyInsets()
  }

  fun setFloatingHorizontalPadding(padding: Int?) {
    if (floatingHorizontalPadding == padding) {
      return
    }
    floatingHorizontalPadding = padding
    if (padding != null) {
      setPaddingRelative(padding, 0, padding, 0)
    } else {
      setPaddingRelative(basePaddingStart, basePaddingTop, basePaddingEnd, basePaddingBottom)
    }
    requestApplyInsets()
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

class BottomTabsView(context: Context) : LinearLayout(context) {
  private var bottomNavigation = ExtendedBottomNavigationView(context)
  val layoutHolder = FrameLayout(context)
  private val floatingRoot = object : FrameLayout(context) {
    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
      if (child === floatingBar && usesBottomBlur) {
        val translationY = floatingBar.translationY.roundToInt()
        drawBlurBackdrop(
          canvas,
          floatingBar.left,
          floatingBar.top + translationY,
          floatingBar.right,
          floatingBar.bottom + translationY,
          floatingBar.height / 2f,
        ) { recording ->
          super.drawChild(recording, layoutHolder, drawingTime)
        }
      }
      return super.drawChild(canvas, child, drawingTime)
    }
  }
  private val floatingBar = FrameLayout(context)
  private val floatingBarBackground = View(context)
  private val floatingIndicator = View(context)
  private val floatingIndicatorDrawable = GradientDrawable()

  var onTabSelectedListener: ((key: String) -> Unit)? = null
  var onTabDoublePressedListener: ((key: String, didScrollToTop: Boolean) -> Unit)? = null
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
  private val selectedIconSources: MutableMap<Int, ImageSource> = mutableMapOf()
  private val drawableCache: MutableMap<ImageSource, Drawable> = mutableMapOf()
  private val iconRequestTokens: MutableMap<Int, Any> = mutableMapOf()
  private val selectedIconRequestTokens: MutableMap<Int, Any> = mutableMapOf()

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
  private var lastReportedTabBarHeight = -1
  private var hasCustomAppearance = false
  private var uiModeConfiguration: Int = Configuration.UI_MODE_NIGHT_UNDEFINED
  private var androidTabBarStyle = AndroidTabBarStyle.DEFAULT
  private var ignoreBottomInsets = false
  private var ignoreKeyboardInsets = true
  private var isTabBarHidden = false
  private var floatingBottomInset = 0
  private var floatingLeftInset = 0
  private var floatingRightInset = 0
  private var floatingIndicatorPositioned = false
  // Lens state: 0 is the resting selection pill, 1 is the lifted glass lens.
  private var floatingLift = 0f
  private var floatingStretch = 0f
  private var floatingLiftAnimator: ValueAnimator? = null
  private var floatingSlideAnimator: ValueAnimator? = null
  private var floatingSlideTargetX = Float.NaN
  private var floatingVisibilityCallback: Choreographer.FrameCallback? = null
  private var floatingTouchView: View? = null
  private var floatingTouchItemId = View.NO_ID
  private var floatingCandidateItemId = View.NO_ID
  private var floatingDragging = false
  private var floatingTouchStartRawX = 0f
  private var floatingTouchStartRawY = 0f
  private var floatingLastMoveRawX = 0f
  private var floatingLastMoveTime = 0L
  private var floatingRestingFillColor = Color.TRANSPARENT
  private var floatingLensFillColor = Color.TRANSPARENT
  private var floatingLensStrokeColor = Color.TRANSPARENT
  private val floatingTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
  private val floatingBarLocationOnScreen = IntArray(2)
  private var lastPressedItemId = View.NO_ID
  private var lastPressedTime = 0L
  // Looked up by name: the pager lives in a package this one does not depend on.
  private val collapsiblePagerClass: Class<*>? by lazy {
    runCatching { Class.forName("com.reactnativepagerview.CollapsiblePagerHost") }.getOrNull()
  }
  private var defaultLabelTextSizes: Pair<Float, Float>? = null
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
  private var defaultItemGravity = bottomNavigation.itemGravity
  private var defaultItemPaddingTop = bottomNavigation.itemPaddingTop
  private var defaultItemPaddingBottom = bottomNavigation.itemPaddingBottom
  private var defaultItemIconSize = bottomNavigation.itemIconSize
  private var defaultItemRippleColor = bottomNavigation.itemRippleColor

  private val imageLoader = ImageLoader.Builder(context)
    .components {
      add(SharedSvgDecoder.Factory())
    }
    .build()

  private var androidBottomBlurEnabled = false
  private var bottomBarBlur: BottomBarBlurRenderer? = null
  private var blurAppearanceApplied = false
  private var floatingBlurAppearanceApplied = false
  private var blurSurfaceColor = Color.WHITE
  private var savedBarTint: ColorStateList? = null
  private var savedItemBackground: Drawable? = null
  private var savedBarElevation = 0f

  private val usesBottomBlur: Boolean
    get() = androidBottomBlurEnabled && Build.VERSION.SDK_INT >= 31 &&
      isHardwareAccelerated && (!isTabBarHidden || floatingVisibilityCallback != null)

  init {
    orientation = VERTICAL
    layoutHolder.isSaveEnabled = false
    floatingRoot.clipChildren = false
    floatingRoot.clipToPadding = false
    // The pressed lens grows past the bar edges, so the bar does not clip; its
    // shadow comes from a capsule outline instead.
    floatingBar.clipChildren = false
    floatingBar.clipToPadding = false
    floatingBar.outlineProvider = object : ViewOutlineProvider() {
      override fun getOutline(view: View, outline: Outline) {
        outline.setRoundRect(0, 0, view.width, view.height, view.height / 2f)
      }
    }
    floatingIndicatorDrawable.shape = GradientDrawable.RECTANGLE
    floatingIndicator.background = floatingIndicatorDrawable
    floatingBar.addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
      if (right - left != oldRight - oldLeft || bottom - top != oldBottom - oldTop) {
        floatingBar.invalidateOutline()
        updateFloatingIndicator(animate = false)
      }
    }

    attachDefaultLayout()
    uiModeConfiguration = resources.configuration.uiMode

    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
      val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
      val keyboardBottom = if (ignoreKeyboardInsets) {
        0
      } else {
        insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
      }
      floatingBottomInset = maxOf(if (ignoreBottomInsets) 0 else navigationBars.bottom, keyboardBottom)
      floatingLeftInset = navigationBars.left
      floatingRightInset = navigationBars.right
      if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) {
        updateFloatingBarLayout()
      }
      insets
    }


  }

  private fun dp(value: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics).roundToInt()

  private fun removeFromParent(view: View) {
    (view.parent as? ViewGroup)?.removeView(view)
  }

  private fun cancelFloatingVisibilityAnimation() {
    val callback = floatingVisibilityCallback ?: return
    floatingVisibilityCallback = null
    Choreographer.getInstance().removeFrameCallback(callback)
  }

  private fun getFloatingBarSlideOffset(): Float {
    val params = floatingBar.layoutParams as? FrameLayout.LayoutParams
    val barHeightPx = floatingBar.height.takeIf { it > 0 } ?: dp(FloatingTabBarMetrics.BAR_HEIGHT)
    val bottomMarginPx = params?.bottomMargin
      ?: (maxOf(floatingBottomInset, dp(FloatingTabBarMetrics.BAR_MIN_BOTTOM_GAP)) +
        dp(FloatingTabBarMetrics.BAR_BOTTOM_EXTRA))
    return (barHeightPx + bottomMarginPx + dp(4f)).toFloat()
  }

  private fun animateFloatingVisibility(fromY: Float, toY: Float, hiding: Boolean) {
    cancelFloatingVisibilityAnimation()
    if (Build.VERSION.SDK_INT >= 26 && !ValueAnimator.areAnimatorsEnabled()) {
      floatingBar.translationY = 0f
      floatingBar.visibility = if (hiding) GONE else VISIBLE
      updateBlurAppearance()
      floatingRoot.invalidate()
      return
    }
    val durationMs = 140f
    val maxFrameStepMs = 24f
    val interpolator = DecelerateInterpolator(if (hiding) 1.2f else 1.4f)
    var elapsedMs = 0f
    var lastFrameTimeNanos = -1L
    val callback = object : Choreographer.FrameCallback {
      override fun doFrame(frameTimeNanos: Long) {
        if (floatingVisibilityCallback !== this) return
        val deltaMs = if (lastFrameTimeNanos < 0L) {
          16f
        } else {
          ((frameTimeNanos - lastFrameTimeNanos) / 1_000_000f).coerceIn(1f, maxFrameStepMs)
        }
        lastFrameTimeNanos = frameTimeNanos
        elapsedMs = (elapsedMs + deltaMs).coerceAtMost(durationMs)
        val fraction = interpolator.getInterpolation((elapsedMs / durationMs).coerceIn(0f, 1f))
        floatingBar.translationY = fromY + (toY - fromY) * fraction
        floatingRoot.invalidate()
        if (elapsedMs < durationMs) {
          Choreographer.getInstance().postFrameCallback(this)
        } else {
          floatingVisibilityCallback = null
          if (hiding && isTabBarHidden) {
            floatingBar.translationY = 0f
            floatingBar.visibility = GONE
            updateBlurAppearance()
          } else if (!hiding && !isTabBarHidden) {
            floatingBar.translationY = 0f
          }
          floatingRoot.invalidate()
        }
      }
    }
    floatingVisibilityCallback = callback
    Choreographer.getInstance().postFrameCallback(callback)
  }

  private fun detachTabBarViews() {
    cancelFloatingVisibilityAnimation()
    floatingBar.translationY = 0f
    releaseBottomBlur()
    removeFromParent(layoutHolder)
    removeFromParent(bottomNavigation)
    removeFromParent(floatingRoot)
    removeFromParent(floatingBar)
    removeFromParent(floatingBarBackground)
    removeFromParent(floatingIndicator)
    super.removeAllViews()
    floatingRoot.removeAllViews()
    floatingBar.removeAllViews()
  }

  private fun attachDefaultLayout() {
    resetFloatingGesture()
    detachTabBarViews()

    super.addView(
      layoutHolder,
      LayoutParams(LayoutParams.MATCH_PARENT, 0).apply { weight = 1f },
    )
    super.addView(
      bottomNavigation,
      LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT),
    )

    bottomNavigation.setFloatingHorizontalPadding(null)
    bottomNavigation.minimumHeight = defaultMinimumHeight
    bottomNavigation.setPadding(
      defaultPaddingLeft,
      defaultPaddingTop,
      defaultPaddingRight,
      defaultPaddingBottom,
    )
    bottomNavigation.setIgnoreBottomInsets(ignoreBottomInsets)
    bottomNavigation.setIgnoreKeyboardInsets(ignoreKeyboardInsets)
    bottomNavigation.elevation = defaultElevation
    bottomNavigation.isItemActiveIndicatorEnabled = defaultActiveIndicatorEnabled
    bottomNavigation.itemActiveIndicatorColor =
      activeIndicatorColor ?: defaultActiveIndicatorColor
    bottomNavigation.itemGravity = defaultItemGravity
    bottomNavigation.itemPaddingTop = defaultItemPaddingTop
    bottomNavigation.itemPaddingBottom = defaultItemPaddingBottom
    (bottomNavigation.getChildAt(0) as? ViewGroup)?.isMotionEventSplittingEnabled = true
    setNavigationIconSize(defaultItemIconSize)
    if (barTintColor != null) {
      applyDefaultBarBackground()
    } else {
      bottomNavigation.itemBackground = defaultItemBackground
      bottomNavigation.backgroundTintList = defaultBackgroundTintList
    }
    bottomNavigation.itemRippleColor = rippleColor ?: defaultItemRippleColor
    bottomNavigation.visibility = if (isTabBarHidden) GONE else VISIBLE
    ViewCompat.requestApplyInsets(this)
  }

  private fun attachFloatingLayout() {
    resetFloatingGesture()
    detachTabBarViews()

    val barHeight = dp(FloatingTabBarMetrics.BAR_HEIGHT)
    val indicatorInset = dp(FloatingTabBarMetrics.INDICATOR_INSET)
    floatingRoot.addView(
      layoutHolder,
      FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT,
      ),
    )
    floatingBar.addView(
      floatingBarBackground,
      FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT,
      ),
    )
    floatingBar.addView(
      floatingIndicator,
      FrameLayout.LayoutParams(0, barHeight - indicatorInset * 2, Gravity.TOP or Gravity.START).apply {
        topMargin = indicatorInset
      },
    )
    floatingBar.addView(
      bottomNavigation,
      FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT,
      ),
    )
    floatingRoot.addView(
      floatingBar,
      FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, barHeight, Gravity.BOTTOM),
    )
    super.addView(
      floatingRoot,
      LayoutParams(LayoutParams.MATCH_PARENT, 0).apply { weight = 1f },
    )

    bottomNavigation.setFloatingHorizontalPadding(dp(FloatingTabBarMetrics.BAR_CONTENT_PADDING))
    bottomNavigation.minimumHeight = 0
    bottomNavigation.setIgnoreBottomInsets(true)
    bottomNavigation.setIgnoreKeyboardInsets(true)
    bottomNavigation.isItemActiveIndicatorEnabled = false
    bottomNavigation.itemActiveIndicatorColor = ColorStateList.valueOf(Color.TRANSPARENT)
    bottomNavigation.itemBackground = ColorDrawable(Color.TRANSPARENT)
    bottomNavigation.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
    bottomNavigation.itemRippleColor = ColorStateList.valueOf(Color.TRANSPARENT)
    bottomNavigation.itemGravity = NavigationBarView.ITEM_GRAVITY_CENTER
    bottomNavigation.itemPaddingTop = 0
    bottomNavigation.itemPaddingBottom = 0
    bottomNavigation.elevation = 0f
    bottomNavigation.visibility = VISIBLE
    // One finger owns the lens; a second finger must not retarget it mid-drag.
    (bottomNavigation.getChildAt(0) as? ViewGroup)?.isMotionEventSplittingEnabled = false
    setNavigationIconSize(dp(FloatingTabBarMetrics.ICON_SIZE))
    floatingBar.elevation = dp(FloatingTabBarMetrics.SHADOW_ELEVATION).toFloat()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      floatingBar.outlineAmbientShadowColor = Color.argb(38, 0, 0, 0)
      floatingBar.outlineSpotShadowColor = Color.argb(64, 0, 0, 0)
    }
    floatingBar.visibility = if (isTabBarHidden) GONE else VISIBLE
    floatingIndicatorPositioned = false
    updateFloatingAppearance()
    updateFloatingBarLayout()
    post { updateFloatingIndicator(animate = false) }
    ViewCompat.requestApplyInsets(this)
  }

  private fun setNavigationIconSize(size: Int) {
    if (bottomNavigation.itemIconSize == size) {
      return
    }
    bottomNavigation.itemIconSize = size
    // Icons are rasterized at the icon size, so reload them for the new size.
    drawableCache.clear()
    iconSources.forEach { (index, source) ->
      bottomNavigation.menu.findItem(index)?.let { menuItem ->
        loadDrawable(index, source, isSelectedIcon = false) { menuItem.icon = it }
      }
    }
  }

  private fun updateFloatingBarLayout() {
    if (androidTabBarStyle != AndroidTabBarStyle.FLOATING) {
      return
    }
    val sideMargin = dp(FloatingTabBarMetrics.BAR_SIDE_MARGIN)
    val params = floatingBar.layoutParams as? FrameLayout.LayoutParams ?: return
    params.leftMargin = sideMargin + floatingLeftInset
    params.rightMargin = sideMargin + floatingRightInset
    params.bottomMargin = maxOf(floatingBottomInset, dp(FloatingTabBarMetrics.BAR_MIN_BOTTOM_GAP)) +
      dp(FloatingTabBarMetrics.BAR_BOTTOM_EXTRA)
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

  private fun withAlpha(color: Int, alpha: Float): Int =
    Color.argb((alpha * 255f).roundToInt(), Color.red(color), Color.green(color), Color.blue(color))

  private fun isDarkColor(color: Int): Boolean {
    val luminance =
      (Color.red(color) * 0.299f) +
        (Color.green(color) * 0.587f) +
        (Color.blue(color) * 0.114f)
    return luminance < 128f
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
    // With a live backdrop, keep only a light surface tint over the blur.
    // The existing rim, shadow, and pressed lens keep their own sharp layers.
    val baseColor = resolvedBarTintColor()
    val isDark = isDarkColor(baseColor)
    val contrastColor = if (isDark) Color.WHITE else Color.BLACK
    val surfaceColor = if (isDark) blendColor(baseColor, Color.WHITE, 0.08f) else baseColor
    floatingBarBackground.background = GradientDrawable().apply {
      shape = GradientDrawable.RECTANGLE
      cornerRadius = dp(FloatingTabBarMetrics.BAR_HEIGHT / 2f).toFloat()
      setColor(withAlpha(surfaceColor, if (usesBottomBlur) 0.12f else 0.95f))
      setStroke(maxOf(1, dp(0.5f)), withAlpha(contrastColor, if (isDark) 0.14f else 0.07f))
    }
    floatingRestingFillColor = activeIndicatorColor?.defaultColor
      ?: withAlpha(contrastColor, if (isDark) 0.14f else 0.07f)
    // The lens reads as clear glass: a light fill that brightens what it covers,
    // outlined by a rim where it extends past the bar.
    floatingLensFillColor = withAlpha(Color.WHITE, if (isDark) 0.18f else 0.55f)
    floatingLensStrokeColor = withAlpha(contrastColor, if (isDark) 0.3f else 0.1f)
    applyFloatingIndicatorState()
  }

  private fun applyFloatingIndicatorState() {
    val colorFraction = floatingLift.coerceIn(0f, 1f)
    floatingIndicatorDrawable.cornerRadius = dp(
      (FloatingTabBarMetrics.BAR_HEIGHT - FloatingTabBarMetrics.INDICATOR_INSET * 2) / 2f
    ).toFloat()
    floatingIndicatorDrawable.setColor(
      ColorUtils.blendARGB(floatingRestingFillColor, floatingLensFillColor, colorFraction)
    )
    floatingIndicatorDrawable.setStroke(
      dp(1f),
      ColorUtils.blendARGB(
        withAlpha(floatingLensStrokeColor, 0f),
        floatingLensStrokeColor,
        colorFraction,
      ),
    )
    floatingIndicator.scaleX =
      1f + (FloatingTabBarMetrics.LENS_SCALE_X - 1f) * floatingLift + floatingStretch
    floatingIndicator.scaleY =
      1f + (FloatingTabBarMetrics.LENS_SCALE_Y - 1f) * floatingLift - floatingStretch * 0.4f
  }

  private val floatingLiftSettle = Runnable {
    if (floatingTouchItemId == View.NO_ID) {
      animateFloatingLift(0f)
    }
  }

  private fun cancelFloatingSlideAnimator() {
    floatingSlideAnimator?.cancel()
    floatingSlideAnimator = null
    floatingSlideTargetX = Float.NaN
  }

  private fun animateFloatingLift(target: Float) {
    floatingBar.removeCallbacks(floatingLiftSettle)
    floatingLiftAnimator?.cancel()
    val start = floatingLift
    if (start == target) {
      applyFloatingIndicatorState()
      return
    }
    val lifting = target > start
    floatingLiftAnimator = ValueAnimator.ofFloat(start, target).apply {
      duration = if (lifting) {
        FloatingTabBarMetrics.LIFT_UP_DURATION_MS
      } else {
        FloatingTabBarMetrics.LIFT_SETTLE_DURATION_MS
      }
      interpolator = SpringInterpolator(
        dampingRatio = if (lifting) 0.72f else 0.8f,
        angularFrequency = if (lifting) 9f else 7.8f,
      )
      addUpdateListener {
        floatingLift = it.animatedValue as Float
        applyFloatingIndicatorState()
      }
      start()
    }
  }

  private fun scheduleFloatingLiftSettle(cancelled: Boolean) {
    floatingBar.removeCallbacks(floatingLiftSettle)
    if (cancelled) {
      animateFloatingLift(0f)
      return
    }
    val slideAnimator = floatingSlideAnimator
    if (slideAnimator != null && slideAnimator.isRunning) {
      val remainingFraction = (1f - slideAnimator.animatedFraction).coerceIn(0f, 1f)
      val delayMs = (FloatingTabBarMetrics.LIFT_HOLD_DURING_SLIDE_MS * remainingFraction)
        .roundToInt()
        .toLong()
      if (delayMs > 16L) {
        floatingBar.postDelayed(floatingLiftSettle, delayMs)
        return
      }
    }
    animateFloatingLift(0f)
  }

  private fun animateFloatingIndicatorTo(
    targetX: Float,
    durationMs: Long = FloatingTabBarMetrics.SLIDE_DURATION_MS,
  ) {
    val activeAnimator = floatingSlideAnimator
    if (
      activeAnimator != null &&
      activeAnimator.isRunning &&
      !floatingSlideTargetX.isNaN() &&
      abs(floatingSlideTargetX - targetX) < 0.5f
    ) {
      return
    }
    cancelFloatingSlideAnimator()
    val startX = floatingIndicator.x
    if (abs(targetX - startX) < 0.5f) {
      floatingIndicator.x = targetX
      floatingStretch = 0f
      applyFloatingIndicatorState()
      return
    }
    var previousX = startX
    var previousTimeMs = SystemClock.uptimeMillis()
    floatingSlideTargetX = targetX
    val animator = ValueAnimator.ofFloat(startX, targetX)
    floatingSlideAnimator = animator
    animator.apply {
      duration = durationMs
      interpolator = SpringInterpolator(
        dampingRatio = FloatingTabBarMetrics.SLIDE_SPRING_DAMPING,
        angularFrequency = FloatingTabBarMetrics.SLIDE_SPRING_FREQUENCY,
      )
      addUpdateListener {
        val nowMs = SystemClock.uptimeMillis()
        val elapsedMs = (nowMs - previousTimeMs).coerceIn(4L, 32L).toFloat()
        previousTimeMs = nowMs
        val x = it.animatedValue as Float
        val frameDelta = abs(x - previousX)
        previousX = x
        floatingIndicator.x = x
        // Normalize velocity to a 16ms reference step so 60Hz and 120Hz displays
        // produce identical liquid stretch during the spring slide.
        val normalizedStepDelta = frameDelta * (16f / elapsedMs)
        val rawStretch = (normalizedStepDelta / floatingIndicator.width.coerceAtLeast(1) * 1.4f)
          .coerceAtMost(FloatingTabBarMetrics.LENS_MAX_STRETCH)
        floatingStretch = floatingStretch * 0.62f + rawStretch * 0.38f
        applyFloatingIndicatorState()
      }
      doOnEnd {
        if (floatingSlideAnimator === animator) {
          floatingSlideAnimator = null
          floatingSlideTargetX = Float.NaN
          floatingStretch = 0f
          applyFloatingIndicatorState()
        }
      }
      start()
    }
  }

  private fun updateFloatingIndicator(animate: Boolean) {
    if (
      androidTabBarStyle != AndroidTabBarStyle.FLOATING ||
      isTabBarHidden ||
      floatingTouchItemId != View.NO_ID
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

    updateFloatingIndicatorWidth(itemView.width)
    val targetX = getFloatingItemX(selectedIndex) ?: return
    floatingIndicator.visibility = VISIBLE

    if (!floatingIndicatorPositioned) {
      cancelFloatingSlideAnimator()
      floatingIndicator.x = targetX
      floatingStretch = 0f
      floatingIndicatorPositioned = true
      applyFloatingIndicatorState()
      return
    }
    val activeAnimator = floatingSlideAnimator
    if (
      activeAnimator != null &&
      activeAnimator.isRunning &&
      !floatingSlideTargetX.isNaN() &&
      abs(floatingSlideTargetX - targetX) < 0.5f
    ) {
      // Keep an in-flight slide to the same target running smoothly even when
      // a late layout/item sync arrives with animate = false.
      return
    }
    if (!animate) {
      cancelFloatingSlideAnimator()
      floatingIndicator.x = targetX
      floatingStretch = 0f
      applyFloatingIndicatorState()
      return
    }
    animateFloatingIndicatorTo(targetX)
  }

  private fun updateFloatingIndicatorWidth(width: Int) {
    val params = floatingIndicator.layoutParams as? FrameLayout.LayoutParams ?: return
    if (params.width != width) {
      params.width = width
      floatingIndicator.layoutParams = params
    }
  }

  private fun getFloatingItemX(itemId: Int): Float? {
    val menuView = bottomNavigation.getChildAt(0) as? ViewGroup ?: return null
    val itemView = bottomNavigation.findViewById<View>(itemId) ?: return null
    if (itemView.width == 0 || itemView.visibility != VISIBLE) {
      return null
    }
    return bottomNavigation.x + menuView.x + itemView.x
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

  private fun setFloatingItemMagnified(magnifiedItemId: Int) {
    items.indices.forEach { itemId ->
      val itemView = bottomNavigation.findViewById<View>(itemId) ?: return@forEach
      val targetScale = if (itemId == magnifiedItemId) {
        FloatingTabBarMetrics.ITEM_PRESSED_SCALE
      } else {
        1f
      }
      if (itemView.scaleX == targetScale && itemView.scaleY == targetScale) {
        return@forEach
      }
      itemView.animate()
        .scaleX(targetScale)
        .scaleY(targetScale)
        .setDuration(260L)
        .setInterpolator(SpringInterpolator(dampingRatio = 0.64f))
        .start()
    }
  }

  // Mirrors the iOS 26 tab bar: touching any tab lifts a glass lens onto it,
  // dragging scrubs the lens across tabs, and release selects the tab under it.
  @SuppressLint("ClickableViewAccessibility")
  private fun handleFloatingTabTouch(view: View, item: MenuItem, event: MotionEvent): Boolean {
    if (androidTabBarStyle != AndroidTabBarStyle.FLOATING) {
      return false
    }

    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        if (isTabBarHidden || floatingBar.width == 0) {
          return false
        }
        clearFloatingTouchTracking()
        floatingTouchView = view
        floatingTouchItemId = item.itemId
        floatingCandidateItemId = item.itemId
        floatingTouchStartRawX = event.rawX
        floatingTouchStartRawY = event.rawY
        floatingLastMoveRawX = event.rawX
        floatingLastMoveTime = event.eventTime
        view.parent?.requestDisallowInterceptTouchEvent(true)
        beginFloatingPress(item.itemId)
        return true
      }
      MotionEvent.ACTION_MOVE -> {
        if (floatingTouchView !== view) {
          return true
        }
        if (!floatingDragging) {
          val deltaX = event.rawX - floatingTouchStartRawX
          val deltaY = event.rawY - floatingTouchStartRawY
          if (deltaX * deltaX + deltaY * deltaY <= floatingTouchSlop * floatingTouchSlop) {
            return true
          }
          floatingDragging = true
        }
        updateFloatingDrag(event.rawX, event.eventTime)
        return true
      }
      MotionEvent.ACTION_UP -> {
        if (floatingTouchView !== view) {
          return true
        }
        val deltaX = event.rawX - floatingTouchStartRawX
        val deltaY = event.rawY - floatingTouchStartRawY
        if (deltaX * deltaX + deltaY * deltaY > floatingTouchSlop * floatingTouchSlop) {
          floatingDragging = true
        }
        if (floatingDragging) {
          updateFloatingDrag(event.rawX, event.eventTime)
        }
        finishFloatingPress(view, cancelled = false)
        return true
      }
      MotionEvent.ACTION_CANCEL -> {
        if (floatingTouchView === view) {
          finishFloatingPress(view, cancelled = true)
        }
        return true
      }
    }
    return true
  }

  private fun beginFloatingPress(itemId: Int) {
    val itemView = bottomNavigation.findViewById<View>(itemId) ?: return
    val targetX = getFloatingItemX(itemId) ?: return
    updateFloatingIndicatorWidth(itemView.width)
    floatingIndicator.visibility = VISIBLE
    if (floatingIndicatorPositioned) {
      animateFloatingIndicatorTo(targetX, durationMs = FloatingTabBarMetrics.SLIDE_DURATION_MS)
    } else {
      floatingIndicator.x = targetX
      floatingIndicatorPositioned = true
    }
    animateFloatingLift(1f)
    setFloatingItemMagnified(itemId)
  }

  private fun updateFloatingDrag(rawX: Float, eventTime: Long) {
    if (floatingBar.width == 0 || floatingIndicator.width == 0) {
      return
    }

    floatingBar.getLocationOnScreen(floatingBarLocationOnScreen)
    val halfWidth = floatingIndicator.width / 2f
    val contentPadding = dp(FloatingTabBarMetrics.BAR_CONTENT_PADDING).toFloat()
    val minCenter = contentPadding + halfWidth
    val maxCenter = floatingBar.width - contentPadding - halfWidth
    val fingerCenter = rawX - floatingBarLocationOnScreen[0]
    // Past either end the lens follows with resistance instead of stopping hard.
    val centerX = when {
      fingerCenter < minCenter ->
        minCenter - (minCenter - fingerCenter) * FloatingTabBarMetrics.DRAG_EDGE_RESISTANCE
      fingerCenter > maxCenter ->
        maxCenter + (fingerCenter - maxCenter) * FloatingTabBarMetrics.DRAG_EDGE_RESISTANCE
      else -> fingerCenter
    }

    val elapsed = (eventTime - floatingLastMoveTime).coerceAtLeast(1L)
    val velocityDpPerMs = abs(rawX - floatingLastMoveRawX) / elapsed / resources.displayMetrics.density
    floatingLastMoveRawX = rawX
    floatingLastMoveTime = eventTime
    val targetStretch = (velocityDpPerMs * 0.06f).coerceAtMost(FloatingTabBarMetrics.LENS_MAX_STRETCH)
    floatingStretch = floatingStretch * 0.6f + targetStretch * 0.4f

    cancelFloatingSlideAnimator()
    floatingIndicator.x = centerX - halfWidth
    applyFloatingIndicatorState()
    floatingBar.removeCallbacks(floatingStretchDecay)
    floatingBar.postDelayed(floatingStretchDecay, 64L)

    getNearestVisibleItemId(centerX)?.let { itemId ->
      if (itemId != floatingCandidateItemId) {
        floatingCandidateItemId = itemId
        setFloatingItemMagnified(itemId)
        emitHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
      }
    }
  }

  private val floatingStretchDecay = Runnable {
    if (floatingTouchItemId == View.NO_ID || floatingStretch == 0f) {
      return@Runnable
    }
    ValueAnimator.ofFloat(floatingStretch, 0f).apply {
      duration = 180L
      interpolator = DecelerateInterpolator()
      addUpdateListener {
        if (floatingTouchItemId != View.NO_ID) {
          floatingStretch = it.animatedValue as Float
          applyFloatingIndicatorState()
        }
      }
      start()
    }
  }

  private fun finishFloatingPress(view: View, cancelled: Boolean) {
    val requestedItemId = if (floatingDragging) floatingCandidateItemId else floatingTouchItemId
    val wasDragging = floatingDragging
    clearFloatingTouchTracking()
    setFloatingItemMagnified(View.NO_ID)

    if (!cancelled && requestedItemId != View.NO_ID) {
      if (!wasDragging) {
        view.performClick()
      } else if (requestedItemId != bottomNavigation.selectedItemId) {
        bottomNavigation.menu.findItem(requestedItemId)?.let { menuItem ->
          onTabSelected(menuItem)
        }
      }
    }
    // Settles on the selected tab, which is the old one for cancelled or
    // preventsDefault presses.
    updateFloatingIndicator(animate = true)
    scheduleFloatingLiftSettle(cancelled = cancelled || wasDragging)
  }

  private fun clearFloatingTouchTracking() {
    floatingBar.removeCallbacks(floatingStretchDecay)
    floatingTouchView = null
    floatingTouchItemId = View.NO_ID
    floatingCandidateItemId = View.NO_ID
    floatingDragging = false
    floatingTouchStartRawX = 0f
    floatingTouchStartRawY = 0f
  }

  private fun resetFloatingGesture() {
    clearFloatingTouchTracking()
    floatingBar.removeCallbacks(floatingLiftSettle)
    floatingLiftAnimator?.cancel()
    cancelFloatingSlideAnimator()
    floatingLift = 0f
    floatingStretch = 0f
    items.indices.forEach { itemId ->
      bottomNavigation.findViewById<View>(itemId)?.apply {
        animate().cancel()
        scaleX = 1f
        scaleY = 1f
      }
    }
    applyFloatingIndicatorState()
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

  fun setAndroidBottomBlurEnabled(enabled: Boolean) {
    if (androidBottomBlurEnabled == enabled) return
    androidBottomBlurEnabled = enabled
    updateBlurAppearance()
    requestLayout()
    floatingRoot.invalidate()
    invalidate()
  }

  private fun updateBlurAppearance() {
    if (!usesBottomBlur) releaseBottomBlur()
    val enabled = usesBottomBlur && androidTabBarStyle == AndroidTabBarStyle.DEFAULT
    if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) {
      blurSurfaceColor = resolvedBarTintColor()
      if (floatingBlurAppearanceApplied != usesBottomBlur) {
        floatingBlurAppearanceApplied = usesBottomBlur
        updateFloatingAppearance()
      }
    } else if (enabled) {
      blurSurfaceColor = barTintColor ?: Utils.getDefaultColorFor(
        bottomNavigation.context, com.google.android.material.R.attr.colorSurface,
      ) ?: if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
        Color.BLACK
      } else Color.WHITE
    }
    if (enabled == blurAppearanceApplied) return
    if (enabled) {
      savedBarTint = bottomNavigation.backgroundTintList
      savedItemBackground = bottomNavigation.itemBackground
      savedBarElevation = bottomNavigation.elevation
      val ripple = bottomNavigation.itemRippleColor
      bottomNavigation.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
      bottomNavigation.itemBackground = null
      bottomNavigation.itemRippleColor = ripple
      bottomNavigation.elevation = 0f
    } else {
      bottomNavigation.backgroundTintList = savedBarTint
      bottomNavigation.itemBackground = savedItemBackground
      bottomNavigation.elevation = savedBarElevation
      releaseBottomBlur()
    }
    blurAppearanceApplied = enabled
  }

  private fun releaseBottomBlur() {
    if (Build.VERSION.SDK_INT >= 31) bottomBarBlur?.release()
    bottomBarBlur = null
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    updateBlurAppearance()
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    if (usesBottomBlur && androidTabBarStyle == AndroidTabBarStyle.DEFAULT) {
      layoutHolder.measure(
        MeasureSpec.makeMeasureSpec(maxOf(0, measuredWidth - paddingLeft - paddingRight), MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(maxOf(0, measuredHeight - paddingTop - paddingBottom), MeasureSpec.EXACTLY),
      )
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    if (usesBottomBlur && androidTabBarStyle == AndroidTabBarStyle.DEFAULT) {
      layoutHolder.layout(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)
      bottomNavigation.layout(
        paddingLeft, height - paddingBottom - bottomNavigation.measuredHeight,
        width - paddingRight, height - paddingBottom,
      )
    }
    val barHeight = Utils.convertPixelsToDp(context, getTabBarMeasuredHeight()).toInt()
    if (barHeight != lastReportedTabBarHeight) {
      onTabBarMeasuredListener?.invoke(barHeight)
      lastReportedTabBarHeight = barHeight
    }
    // Scene bounds change when blur toggles even if the host bounds stay fixed.
    val sceneSize = Size(layoutHolder.width, layoutHolder.height)
    if (sceneSize != lastReportedSize) {
      onNativeLayoutListener?.invoke(
        Utils.convertPixelsToDp(context, sceneSize.width),
        Utils.convertPixelsToDp(context, sceneSize.height),
      )
      lastReportedSize = sceneSize
    }
  }

  private fun drawBlurBackdrop(
    canvas: Canvas,
    left: Int,
    top: Int,
    right: Int,
    bottom: Int,
    cornerRadius: Float,
    drawScenes: (Canvas) -> Unit,
  ) {
    if (!usesBottomBlur || !canvas.isHardwareAccelerated || Build.VERSION.SDK_INT < 31) return
    val blur = bottomBarBlur ?: BottomBarBlurRenderer(resources.displayMetrics.density).also {
      bottomBarBlur = it
    }
    blur.draw(canvas, left, top, right, bottom, cornerRadius, blurSurfaceColor, drawScenes)
  }

  override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
    if (child === bottomNavigation && androidTabBarStyle == AndroidTabBarStyle.DEFAULT) {
      drawBlurBackdrop(
        canvas,
        bottomNavigation.left,
        bottomNavigation.top,
        bottomNavigation.right,
        bottomNavigation.bottom,
        0f,
      ) { recording ->
        super.drawChild(recording, layoutHolder, drawingTime)
      }
    }
    return super.drawChild(canvas, child, drawingTime)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    requestLayout()
  }

  override fun onDetachedFromWindow() {
    cancelFloatingVisibilityAnimation()
    floatingBar.translationY = 0f
    if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) {
      floatingBar.visibility = if (isTabBarHidden) GONE else VISIBLE
    }
    releaseBottomBlur()
    super.onDetachedFromWindow()
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
    if (selectionChanged) {
      updateMenuItemIconsForSelection()
    }
    if (
      androidTabBarStyle == AndroidTabBarStyle.FLOATING &&
      (selectionChanged || !floatingIndicatorPositioned)
    ) {
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
      emitDoublePressIfNeeded(item.itemId, selectedItem.key)
    }
  }

  private fun emitDoublePressIfNeeded(itemId: Int, key: String) {
    val now = SystemClock.uptimeMillis()
    if (
      lastPressedItemId == itemId &&
      now - lastPressedTime <= ViewConfiguration.getDoubleTapTimeout()
    ) {
      lastPressedItemId = View.NO_ID
      val listener = onTabDoublePressedListener ?: return
      val didScrollToTop = scrollSelectedTabToTop(key) ?: return
      listener(key, didScrollToTop)
      return
    }
    lastPressedItemId = itemId
    lastPressedTime = now
  }

  /**
   * Animates every visible vertical scroller of the selected tab back to its
   * top. Returns false when all of them were already there, which tells JS the
   * double press may leave the page instead. A rejected pager request with no
   * accepted scroll returns null instead of implying that the page is at top.
   */
  private fun scrollSelectedTabToTop(pressedKey: String): Boolean? {
    if (pressedKey != selectedItem) return false
    val scrollers = mutableListOf<View>()
    val pagers = mutableListOf<View>()
    collectScrolledViews(layoutHolder, scrollers, pagers)
    scrollers.forEach { view ->
      when (view) {
        is RecyclerView -> view.smoothScrollToPosition(0)
        is NestedScrollView -> view.smoothScrollTo(view.scrollX, 0)
        is ScrollView -> view.smoothScrollTo(view.scrollX, 0)
      }
    }
    var didScrollToTop = scrollers.isNotEmpty()
    var rejectedPagerRequest = false
    pagers.forEach { pager ->
      if (scrollPagerToTop(pager)) didScrollToTop = true
      else rejectedPagerRequest = true
    }
    return when {
      didScrollToTop -> true
      rejectedPagerRequest -> null
      else -> false
    }
  }

  private fun collectScrolledViews(
    view: View,
    scrollers: MutableList<View>,
    pagers: MutableList<View>,
  ) {
    // Hidden tabs and pager siblings stay attached without being on screen.
    if (!view.isShown || !view.getGlobalVisibleRect(Rect())) return
    if (collapsiblePagerClass?.isInstance(view) == true) {
      // The pager keeps header state for its pages, so moving one of its
      // scrollers directly leaves the header out of sync. It scrolls itself.
      val pagerScrollers = mutableListOf<View>()
      val nestedPagers = mutableListOf<View>()
      (view as ViewGroup).forEach { child ->
        collectScrolledViews(child, pagerScrollers, nestedPagers)
      }
      if (pagerScrollers.isNotEmpty() || nestedPagers.isNotEmpty() || isPagerAwayFromTop(view)) {
        pagers.add(view)
      }
      return
    }
    if (
      (view is RecyclerView || view is NestedScrollView || view is ScrollView) &&
      view.canScrollVertically(-1)
    ) {
      scrollers.add(view)
    }
    if (view is ViewGroup) {
      view.forEach { child -> collectScrolledViews(child, scrollers, pagers) }
    }
  }

  private fun isPagerAwayFromTop(pager: View): Boolean =
    runCatching { pager.javaClass.getMethod("isAwayFromPageTop").invoke(pager) as Boolean }
      .onFailure { Log.w("BottomTabs", "Unable to read the collapsible pager top state", it) }
      .getOrDefault(false)

  private fun scrollPagerToTop(pager: View): Boolean =
    runCatching {
      pager.javaClass
        .getMethod("tryScrollToTop", Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)
        .invoke(pager, 0, true) as Boolean
    }.onFailure { Log.w("BottomTabs", "Unable to scroll the collapsible pager to top", it) }
      .getOrDefault(false)

  fun setIgnoreBottomInsets(ignore: Boolean) {
    ignoreBottomInsets = ignore
    bottomNavigation.setIgnoreBottomInsets(
      if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) true else ignore
    )
    ViewCompat.requestApplyInsets(this)
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
    // Restore the docked appearance before reparenting, then capture the new
    // style only after its normal appearance has been installed.
    if (blurAppearanceApplied) updateBlurAppearance()
    if (nextStyle == AndroidTabBarStyle.FLOATING) {
      attachFloatingLayout()
    } else {
      attachDefaultLayout()
    }
    updateBlurAppearance()
    updateTextAppearance()
    requestLayout()
  }

  fun setIgnoreKeyboardInsets(ignore: Boolean) {
    ignoreKeyboardInsets = ignore
    bottomNavigation.setIgnoreKeyboardInsets(
      if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) true else ignore
    )
    ViewCompat.requestApplyInsets(this)
  }

  fun setTabBarHidden(isHidden: Boolean) {
    if (isTabBarHidden == isHidden) {
      return
    }
    isTabBarHidden = isHidden
    if (isHidden) {
      resetFloatingGesture()
    }
    val shouldAnimateFloating =
      androidTabBarStyle == AndroidTabBarStyle.FLOATING &&
        isAttachedToWindow &&
        width > 0 &&
        height > 0

    cancelFloatingVisibilityAnimation()

    if (!shouldAnimateFloating) {
      floatingBar.translationY = 0f
      if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) {
        floatingBar.visibility = if (isHidden) GONE else VISIBLE
        bottomNavigation.visibility = VISIBLE
      } else {
        bottomNavigation.visibility = if (isHidden) GONE else VISIBLE
      }
      updateBlurAppearance()
      requestLayout()
      floatingRoot.invalidate()
      invalidate()
      if (!isHidden) post { updateFloatingIndicator(animate = false) }
      return
    }

    val slideOffset = getFloatingBarSlideOffset()
    bottomNavigation.visibility = VISIBLE
    if (isHidden) {
      val startY = if (floatingBar.visibility == VISIBLE) floatingBar.translationY else slideOffset
      floatingBar.visibility = VISIBLE
      floatingBar.translationY = startY
      animateFloatingVisibility(startY, slideOffset, hiding = true)
      updateBlurAppearance()
      requestLayout()
      floatingRoot.invalidate()
      invalidate()
      return
    }

    val wasGone = floatingBar.visibility != VISIBLE
    val startY = if (wasGone) slideOffset else floatingBar.translationY
    floatingBar.visibility = VISIBLE
    floatingBar.translationY = startY
    animateFloatingVisibility(startY, 0f, hiding = false)
    updateBlurAppearance()
    requestLayout()
    floatingRoot.invalidate()
    invalidate()
    post { updateFloatingIndicator(animate = false) }
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
      applyMenuItemIcon(index, menuItem)

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
          view.setOnClickListener {
            if (!isTabBarHidden) {
              onTabSelected(menuItem)
            }
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

  private fun isItemSelected(index: Int): Boolean {
    val selectedKey = selectedItem
    return if (selectedKey != null) {
      items.getOrNull(index)?.key == selectedKey
    } else {
      bottomNavigation.selectedItemId == index
    }
  }

  private fun activeIconSourceForIndex(index: Int): Pair<ImageSource, Boolean>? {
    val selected = isItemSelected(index)
    if (selected) {
      selectedIconSources[index]?.let { return it to true }
    }
    iconSources[index]?.let { return it to false }
    selectedIconSources[index]?.let { return it to true }
    return null
  }

  private fun applyMenuItemIcon(index: Int, menuItem: MenuItem) {
    val (source, isSelectedSource) = activeIconSourceForIndex(index) ?: return
    drawableCache[source]?.let { cached ->
      if (menuItem.icon !== cached) {
        menuItem.icon = cached
      }
      return
    }
    loadDrawable(index, source, isSelectedSource) { drawable ->
      val currentSource = activeIconSourceForIndex(index)?.first
      if (currentSource == source && drawable != null) {
        menuItem.icon = drawable
      }
    }
  }

  private fun updateMenuItemIconsForSelection() {
    items.indices.forEach { index ->
      bottomNavigation.menu.findItem(index)?.let { menuItem ->
        applyMenuItemIcon(index, menuItem)
      }
    }
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

      loadDrawable(idx, imageSource, isSelectedIcon = false) {
        bottomNavigation.menu.findItem(idx)?.let { menuItem ->
          applyMenuItemIcon(idx, menuItem)
        }
      }
    }
  }

  fun setSelectedIcons(icons: ReadableArray?) {
    if (icons == null || icons.size() == 0) {
      selectedIconRequestTokens.values.forEach { OneKeyNamedSVG.cancel(it) }
      selectedIconRequestTokens.clear()
      selectedIconSources.clear()
      updateMenuItemIconsForSelection()
      return
    }

    for (idx in 0 until icons.size()) {
      val source = icons.getMap(idx)
      val uri = source?.getString("uri")
      if (uri.isNullOrEmpty()) {
        selectedIconSources.remove(idx)
        continue
      }

      val imageSource = ImageSource(context, uri)
      this.selectedIconSources[idx] = imageSource

      // Pre-warm the selected icon into drawableCache so tab switches swap synchronously.
      loadDrawable(idx, imageSource, isSelectedIcon = true) {
        bottomNavigation.menu.findItem(idx)?.let { menuItem ->
          applyMenuItemIcon(idx, menuItem)
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
    // Floating mode replaces the ripple with the pressed lens.
    if (androidTabBarStyle != AndroidTabBarStyle.FLOATING) {
      bottomNavigation.itemRippleColor = color
      if (blurAppearanceApplied) savedItemBackground = bottomNavigation.itemBackground
    }
  }

  @SuppressLint("CheckResult")
  private fun loadDrawable(
    index: Int,
    imageSource: ImageSource,
    isSelectedIcon: Boolean,
    onDrawableReady: (Drawable?) -> Unit,
  ) {
    val tokenMap = if (isSelectedIcon) selectedIconRequestTokens else iconRequestTokens
    val sourceMap = if (isSelectedIcon) selectedIconSources else iconSources
    tokenMap.remove(index)?.let { OneKeyNamedSVG.cancel(it) }
    drawableCache[imageSource]?.let {
      onDrawableReady(it)
      return
    }
    val rawSource = imageSource.uri.orEmpty()
    if (rawSource.startsWith(ONEKEY_ICON_URI_PREFIX)) {
      val iconName = rawSource.removePrefix(ONEKEY_ICON_URI_PREFIX).trim()
      if (iconName.isEmpty()) {
        onDrawableReady(null)
        return
      }
      val token = Any()
      tokenMap[index] = token
      val iconSizePx = bottomNavigation.itemIconSize.coerceAtLeast(1)
      val density = resources.displayMetrics.density
      OneKeyNamedSVG.request(
        owner = token,
        assets = context.applicationContext.assets,
        name = iconName,
        color = "rgba(0,0,0,1)",
        width = iconSizePx,
        height = iconSizePx,
        scale = density,
      ) { bitmap ->
        if (tokenMap[index] !== token) {
          return@request
        }
        tokenMap.remove(index)
        if (bitmap == null) {
          onDrawableReady(null)
          return@request
        }
        val stateDrawable = BitmapDrawable(context.resources, bitmap)
        drawableCache[imageSource] = stateDrawable
        if (sourceMap[index] == imageSource) {
          onDrawableReady(stateDrawable)
        }
      }
      return
    }
    val token = Any()
    tokenMap[index] = token
    val iconSizePx = bottomNavigation.itemIconSize
    val request = ImageRequest.Builder(context)
      .data(imageSource.getUri(context))
      .size(CoilSize(iconSizePx, iconSizePx))
      .scale(Scale.FILL)
      .precision(Precision.EXACT)
      .target { drawable ->
        post {
          if (tokenMap[index] !== token) {
            return@post
          }
          tokenMap.remove(index)
          val stateDrawable = drawable.asDrawable(context.resources)
          drawableCache[imageSource] = stateDrawable
          if (sourceMap[index] == imageSource) {
            onDrawableReady(stateDrawable)
          }
        }
      }
      .listener(
        onError = { _, result ->
          Log.e("BottomTabs", "Error loading image: ${imageSource.uri}", result.throwable)
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
    blurSurfaceColor = backgroundColor

    if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) {
      updateFloatingAppearance()
    } else if (blurAppearanceApplied) {
      savedItemBackground = ColorDrawable(backgroundColor)
      savedBarTint = ColorStateList.valueOf(backgroundColor)
    } else {
      bottomNavigation.itemBackground = ColorDrawable(backgroundColor)
      bottomNavigation.backgroundTintList = ColorStateList.valueOf(backgroundColor)
    }
    hasCustomAppearance = true
    floatingRoot.invalidate()
    invalidate()
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
    iconRequestTokens.values.forEach { OneKeyNamedSVG.cancel(it) }
    iconRequestTokens.clear()
    selectedIconRequestTokens.values.forEach { OneKeyNamedSVG.cancel(it) }
    selectedIconRequestTokens.clear()
    androidBottomBlurEnabled = false
    updateBlurAppearance()
    releaseBottomBlur()
    resetFloatingGesture()
    imageLoader.shutdown()
  }

  private fun updateTextAppearance() {
    val isFloating = androidTabBarStyle == AndroidTabBarStyle.FLOATING
    val customSize = fontSize?.toFloat()?.takeIf { it > 0 }
    // Floating labels use one compact size for both states, as on iOS. Material's
    // own sizes are remembered so leaving floating mode can restore them.
    val restoreLabelSizes = if (!isFloating && customSize == null) defaultLabelTextSizes else null
    if (
      !isFloating &&
      restoreLabelSizes == null &&
      fontSize == null &&
      fontFamily == null &&
      fontWeight == null
    ) {
      return
    }

    val typeface = if (fontFamily != null || fontWeight != null) {
      ReactFontManager.getInstance().getTypeface(
        fontFamily ?: "",
        Utils.getTypefaceStyle(fontWeight),
        context.assets
      )
    } else null
    val size = customSize ?: if (isFloating) FloatingTabBarMetrics.LABEL_SIZE_SP else null

    val menuView = bottomNavigation.getChildAt(0) as? ViewGroup ?: return
    for (i in 0 until menuView.childCount) {
      val item = menuView.getChildAt(i)
      val largeLabel =
        item.findViewById<TextView>(com.google.android.material.R.id.navigation_bar_item_large_label_view)
      val smallLabel =
        item.findViewById<TextView>(com.google.android.material.R.id.navigation_bar_item_small_label_view)
      if (isFloating && defaultLabelTextSizes == null && largeLabel != null && smallLabel != null) {
        defaultLabelTextSizes = largeLabel.textSize to smallLabel.textSize
      }
      if (restoreLabelSizes != null) {
        largeLabel?.setTextSize(TypedValue.COMPLEX_UNIT_PX, restoreLabelSizes.first)
        smallLabel?.setTextSize(TypedValue.COMPLEX_UNIT_PX, restoreLabelSizes.second)
      }

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
      this@BottomTabsView.bottomNavigation.itemTextColor = this
      this@BottomTabsView.bottomNavigation.itemIconTintList = this
    }
  }

  override fun onConfigurationChanged(newConfig: Configuration?) {
    super.onConfigurationChanged(newConfig)
    releaseBottomBlur()
    if (androidBottomBlurEnabled) {
      requestLayout()
      floatingRoot.invalidate()
      invalidate()
    }
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
    blurAppearanceApplied = false
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
    defaultItemGravity = bottomNavigation.itemGravity
    defaultItemPaddingTop = bottomNavigation.itemPaddingTop
    defaultItemPaddingBottom = bottomNavigation.itemPaddingBottom
    defaultItemIconSize = bottomNavigation.itemIconSize
    defaultItemRippleColor = bottomNavigation.itemRippleColor
    if (androidTabBarStyle == AndroidTabBarStyle.FLOATING) {
      attachFloatingLayout()
    } else {
      attachDefaultLayout()
    }
    updateItems(items)
    setLabeled(this.labeled)
    this.selectedItem?.let { setSelectedItem(it) }
    uiModeConfiguration = newConfig?.uiMode ?: uiModeConfiguration
  }
}
