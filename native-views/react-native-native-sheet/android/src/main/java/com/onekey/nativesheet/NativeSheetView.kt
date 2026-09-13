package com.onekey.nativesheet

import android.annotation.SuppressLint
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.facebook.react.common.annotations.UnstableReactNativeAPI
import com.facebook.react.config.ReactFeatureFlags
import com.facebook.react.uimanager.JSPointerDispatcher
import com.facebook.react.uimanager.JSTouchDispatcher
import com.facebook.react.uimanager.RootView
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.EventDispatcher
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

internal object NativeSheetStack {
  private val active = mutableListOf<WeakReference<NativeSheetView>>()
  private val pending = mutableListOf<WeakReference<NativeSheetView>>()
  private var presenting: WeakReference<NativeSheetView>? = null
  private val deferredDismissals =
    mutableListOf<Triple<WeakReference<NativeSheetView>, String, Boolean>>()
  private var transitioning = false

  private fun compact() {
    active.removeAll { it.get() == null }
    pending.removeAll { it.get() == null }
    deferredDismissals.removeAll { it.first.get() == null }
  }

  fun present(view: NativeSheetView) {
    compact()
    if (active.any { it.get() === view } || pending.any { it.get() === view }) {
      return
    }
    pending.add(WeakReference(view))
    processPending()
  }

  fun dismiss(view: NativeSheetView, reason: String, animated: Boolean) {
    pending.removeAll { it.get() == null || it.get() === view }
    if (transitioning) {
      deferredDismissals.removeAll { it.first.get() == null || it.first.get() === view }
      deferredDismissals.add(Triple(WeakReference(view), reason, animated))
      return
    }
    compact()
    val index = active.indexOfFirst { it.get() === view }
    if (index < 0) {
      view.dismissDialogInternal(reason, animated) { processDeferredDismissalOrPending() }
      return
    }

    val targets = active.subList(index, active.size).mapNotNull { it.get() }.asReversed()
    active.subList(index, active.size).clear()
    dismissNext(targets, view, reason, animated)
  }

  fun didDismiss(view: NativeSheetView) {
    active.removeAll { it.get() == null || it.get() === view }
  }

  fun isTop(view: NativeSheetView): Boolean {
    compact()
    return (presenting?.get() ?: active.lastOrNull()?.get()) === view
  }

  private fun dismissNext(
    targets: List<NativeSheetView>,
    requestedView: NativeSheetView,
    reason: String,
    animated: Boolean,
  ) {
    val target = targets.firstOrNull()
    if (target == null) {
      transitioning = false
      processDeferredDismissalOrPending()
      return
    }
    val targetReason = when {
      target === requestedView -> reason
      reason == "security" -> reason
      else -> "system"
    }
    transitioning = true
    target.dismissDialogInternal(targetReason, animated) {
      transitioning = false
      dismissNext(targets.drop(1), requestedView, reason, animated)
    }
  }

  private fun processPending() {
    compact()
    if (transitioning) return
    val view = pending.firstOrNull()?.get() ?: return
    pending.removeAt(0)
    transitioning = true
    presenting = WeakReference(view)
    if (view.showDialogInternal {
        presenting = null
        transitioning = false
        processDeferredDismissalOrPending()
      }) {
      active.add(WeakReference(view))
      return
    }
    presenting = null
    view.finishFailedPresentation()
    transitioning = false
    processDeferredDismissalOrPending()
  }

  private fun processDeferredDismissalOrPending() {
    compact()
    if (transitioning) return
    while (deferredDismissals.isNotEmpty()) {
      val request = deferredDismissals.removeAt(0)
      val view = request.first.get() ?: continue
      dismiss(view, request.second, request.third)
      return
    }
    processPending()
  }
}

/**
 * React children mounted in a Dialog are outside their original ReactRootView.
 * This root mirrors React Native Modal's touch dispatch boundary so Pressability,
 * pointer events, and native gesture cancellation keep working after reparenting.
 */
private class NativeSheetDialogRootView(
  private val reactContext: ThemedReactContext,
) : FrameLayout(reactContext), RootView {
  var eventDispatcher: EventDispatcher? = null

  private val touchDispatcher = JSTouchDispatcher(this)
  private val pointerDispatcher = if (ReactFeatureFlags.dispatchPointerEvents) {
    JSPointerDispatcher(this)
  } else {
    null
  }

  override fun handleException(t: Throwable) {
    reactContext.reactApplicationContext.handleException(RuntimeException(t))
  }

  override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
    eventDispatcher?.let { dispatcher ->
      touchDispatcher.handleTouchEvent(event, dispatcher, reactContext)
      pointerDispatcher?.handleMotionEvent(event, dispatcher, true)
    }
    return super.onInterceptTouchEvent(event)
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    eventDispatcher?.let { dispatcher ->
      touchDispatcher.handleTouchEvent(event, dispatcher, reactContext)
      pointerDispatcher?.handleMotionEvent(event, dispatcher, false)
    }
    super.onTouchEvent(event)
    return true
  }

  override fun onInterceptHoverEvent(event: MotionEvent): Boolean {
    eventDispatcher?.let { pointerDispatcher?.handleMotionEvent(event, it, true) }
    return super.onInterceptHoverEvent(event)
  }

  override fun onHoverEvent(event: MotionEvent): Boolean {
    eventDispatcher?.let { pointerDispatcher?.handleMotionEvent(event, it, false) }
    return super.onHoverEvent(event)
  }

  @OptIn(UnstableReactNativeAPI::class)
  override fun onChildStartedNativeGesture(childView: View?, ev: MotionEvent) {
    eventDispatcher?.let { dispatcher ->
      touchDispatcher.onChildStartedNativeGesture(ev, dispatcher, reactContext)
      pointerDispatcher?.onChildStartedNativeGesture(childView, ev, dispatcher)
    }
  }

  override fun onChildEndedNativeGesture(childView: View, ev: MotionEvent) {
    eventDispatcher?.let { touchDispatcher.onChildEndedNativeGesture(ev, it) }
    pointerDispatcher?.onChildEndedNativeGesture()
  }

  override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit
}

class NativeSheetView(
  private val reactContext: ThemedReactContext,
) : FrameLayout(reactContext) {
  var open: Boolean = false
  var sheetHeight: Double = 0.0
  var securityBlocked: Boolean = false
  var dismissOnPanDown: Boolean = true
  var dismissOnBackdropPress: Boolean = false
  var dismissOnBackPress: Boolean = true
  var showHandle: Boolean = true
  var cornerRadius: Double = 32.0
  var dimAmount: Double = 0.4
  var sheetBackgroundColor: Int? = null
  var onDismiss: ((String) -> Unit)? = null
  var onPresented: ((Double) -> Unit)? = null
  var eventDispatcher: EventDispatcher? = null
    set(value) {
      field = value
      dialogContent?.eventDispatcher = value
    }

  private var contentChild: View? = null
  private var dialog: BottomSheetDialog? = null
  private var dialogContent: NativeSheetDialogRootView? = null
  private var committedOpen = false
  private var dismissedForCurrentOpen = false
  private var pendingDismissReason = "system"
  private var dismissNotified = false
  private var dismissalCompletion: (() -> Unit)? = null
  private var dimAnimator: ValueAnimator? = null
  private var heightAnimator: ValueAnimator? = null
  private var dialogConfigured = false
  private var presentedHeightPx = 0
  private var navigationBarInsetPx = 0
  private var imeInsetPx = 0
  private var reportedImeInsetPx = 0
  private var visibleFrameImeInsetPx = 0
  private var imeAnimationRunning = false
  private var extendsIntoNavigationBar = false
  private var windowInsetHost: View? = null
  private var imeGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
  private var imeInsetFallbackRunnable: Runnable? = null
  private var navigationBarBackgroundView: View? = null
  private var navigationBarBackgroundColor = Color.TRANSPARENT
  private var preImeBottomSheetBottomPx: Int? = null

  init {
    visibility = INVISIBLE
    importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
  }

  fun addReactChild(child: View, index: Int) {
    contentChild?.let { existing ->
      if (existing !== child) removeReactChild(existing)
    }
    contentChild = child
    attachChild(dialogContent ?: this, child, index)
  }

  fun getReactChildCount(): Int = if (contentChild == null) 0 else 1

  fun getReactChildAt(index: Int): View? = if (index == 0) contentChild else null

  fun removeReactChild(child: View) {
    if (contentChild !== child) return
    (child.parent as? ViewGroup)?.removeView(child)
    contentChild = null
  }

  fun removeAllReactChildren() {
    contentChild?.let(::removeReactChild)
  }

  fun commitConfiguration() {
    val openChanged = committedOpen != open
    committedOpen = open
    if (openChanged && open) dismissNotified = false
    if (!open) dismissedForCurrentOpen = false

    if (securityBlocked) {
      dismissedForCurrentOpen = open
      NativeSheetStack.dismiss(this, "security", false)
      return
    }

    if (!open) {
      NativeSheetStack.dismiss(this, "programmatic", true)
    } else if ((openChanged || dialog == null) && !dismissedForCurrentOpen) {
      NativeSheetStack.present(this)
    } else if (!dismissedForCurrentOpen) {
      updatePresentedHeight(true)
    }
  }

  fun hasDialog(): Boolean = dialog != null

  internal fun showDialogInternal(presentationCompletion: () -> Unit): Boolean {
    if (dialog != null || securityBlocked || !open) return false
    val activity = reactContext.currentActivity ?: return false
    if (activity.isFinishing || activity.isDestroyed) return false

    val heightPx = dpToPx(sheetHeight.coerceAtLeast(1.0))
    val resolvedSheetBackgroundColor = sheetBackgroundColor ?: Color.WHITE
    val content = NativeSheetDialogRootView(reactContext).apply {
      eventDispatcher = this@NativeSheetView.eventDispatcher
      layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx)
      background = createSheetBackground(resolvedSheetBackgroundColor)
      clipToOutline = true
    }
    contentChild?.let { attachChild(content, it, 0) }
    if (showHandle) content.addView(createHandle(), createHandleLayoutParams())

    pendingDismissReason = "system"
    dismissNotified = false
    dialogConfigured = false
    presentedHeightPx = heightPx
    extendsIntoNavigationBar = Color.alpha(resolvedSheetBackgroundColor) > 0
    navigationBarBackgroundColor = resolvedSheetBackgroundColor
    val nextDialog = BottomSheetDialog(activity).apply {
      setContentView(content)
      setCancelable(dismissOnPanDown || dismissOnBackPress || dismissOnBackdropPress)
      setCanceledOnTouchOutside(false)
      dismissWithAnimation = true
      window?.apply {
        setDimAmount(0f)
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          isNavigationBarContrastEnforced = false
        }
        configureEdgeToEdgeWindow(this)
      }
      setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
          if (hideImeIfVisible()) return@setOnKeyListener true
          if (!dismissOnBackPress) return@setOnKeyListener true
          NativeSheetStack.dismiss(this@NativeSheetView, "back", true)
          return@setOnKeyListener true
        }
        false
      }
      setOnDismissListener {
        handleDialogDismissed()
      }
      setOnShowListener {
        configureShownDialog(
          this,
          resolvedSheetBackgroundColor,
          presentationCompletion,
        )
      }
    }

    dialogContent = content
    dialog = nextDialog
    nextDialog.show()
    return true
  }

  private fun configureShownDialog(
    sheetDialog: BottomSheetDialog,
    resolvedSheetBackgroundColor: Int,
    presentationCompletion: () -> Unit,
  ) {
    sheetDialog.window?.apply {
      setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
      setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
      configureEdgeToEdgeWindow(this)
    }
    animateDimAmount(
      sheetDialog,
      dimAmount.coerceIn(0.0, 1.0).toFloat(),
      true,
    ) {
      if (open && !securityBlocked) {
        onPresented?.invoke(pxToDp(presentedHeightPx))
      }
      presentationCompletion()
    }
    sheetDialog
      .findViewById<View>(com.google.android.material.R.id.touch_outside)
      ?.setOnClickListener {
        if (dismissOnBackdropPress) {
          NativeSheetStack.dismiss(this, "backdrop", true)
        }
      }
    val bottomSheet = sheetDialog.findViewById<FrameLayout>(
      com.google.android.material.R.id.design_bottom_sheet,
    ) ?: return
    bottomSheet.clipChildren = true
    bottomSheet.clipToPadding = true
    bottomSheet.setPadding(0, 0, 0, 0)
    ViewCompat.setOnApplyWindowInsetsListener(bottomSheet) { view, insets ->
      view.setPadding(0, 0, 0, 0)
      insets
    }
    val container = sheetDialog.findViewById<FrameLayout>(
      com.google.android.material.R.id.container,
    )
    val coordinator = sheetDialog.findViewById<View>(
      com.google.android.material.R.id.coordinator,
    )
    container?.fitsSystemWindows = false
    coordinator?.fitsSystemWindows = false
    val insetHost = sheetDialog.window?.decorView ?: container ?: bottomSheet
    clearWindowInsetsObservation()
    windowInsetHost = insetHost
    navigationBarInsetPx = getNavigationBarInset(insetHost)
    updateNavigationBarBackground(sheetDialog)
    ViewCompat.setOnApplyWindowInsetsListener(insetHost) { _, insets ->
      applyNavigationBarInset(sheetDialog, bottomSheet, insets)
      scheduleImeInsetFallback(insetHost, bottomSheet)
      insets
    }
    ViewCompat.setWindowInsetsAnimationCallback(
      insetHost,
      object : WindowInsetsAnimationCompat.Callback(
        WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE,
      ) {
        override fun onPrepare(animation: WindowInsetsAnimationCompat) {
          if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
            cancelImeInsetFallback()
            imeAnimationRunning = true
          }
          super.onPrepare(animation)
        }

        override fun onProgress(
          insets: WindowInsetsCompat,
          runningAnimations: MutableList<WindowInsetsAnimationCompat>,
        ): WindowInsetsCompat {
          cancelImeInsetFallback()
          applyNavigationBarInset(sheetDialog, bottomSheet, insets)
          applyImeInset(bottomSheet, insets)
          return insets
        }

        override fun onEnd(animation: WindowInsetsAnimationCompat) {
          super.onEnd(animation)
          if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
            cancelImeInsetFallback()
            imeAnimationRunning = false
            ViewCompat.getRootWindowInsets(insetHost)?.let { insets ->
              applyNavigationBarInset(sheetDialog, bottomSheet, insets)
              applyImeInset(bottomSheet, insets)
            }
          }
        }
      },
    )
    imeGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
      scheduleImeInsetFallback(insetHost, bottomSheet)
    }.also { listener ->
      insetHost.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }
    ViewCompat.requestApplyInsets(insetHost)
    bottomSheet.background = createSheetBackground(resolvedSheetBackgroundColor)
    sheetDialog.behavior.apply {
      isFitToContents = true
      isHideable = dismissOnPanDown
      isDraggable = dismissOnPanDown
      skipCollapsed = true
      state = BottomSheetBehavior.STATE_EXPANDED
      addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
          if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            pendingDismissReason = "pan"
          }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
          if (slideOffset < 1f) {
            dimAnimator?.cancel()
            sheetDialog.window?.setDimAmount(
              dimAmount.coerceIn(0.0, 1.0).toFloat() * slideOffset.coerceIn(0f, 1f),
            )
          }
        }
      })
    }
    dialogConfigured = true
    applyPresentedHeight(
      sheetDialog,
      bottomSheet,
      dpToPx(sheetHeight.coerceAtLeast(1.0)),
    )
  }

  private fun updatePresentedHeight(animated: Boolean) {
    val sheetDialog = dialog ?: return
    val targetHeightPx = dpToPx(sheetHeight.coerceAtLeast(1.0))
    if (!dialogConfigured) {
      presentedHeightPx = targetHeightPx
      dialogContent?.let { content ->
        content.layoutParams = content.layoutParams.apply {
          height = targetHeightPx
        }
      }
      return
    }
    val bottomSheet = sheetDialog.findViewById<FrameLayout>(
      com.google.android.material.R.id.design_bottom_sheet,
    ) ?: return
    val currentHeightPx = presentedHeightPx
    if (currentHeightPx == targetHeightPx) {
      applyPresentedHeight(sheetDialog, bottomSheet, targetHeightPx)
      return
    }

    heightAnimator?.cancel()
    if (!animated) {
      applyPresentedHeight(sheetDialog, bottomSheet, targetHeightPx)
      return
    }
    heightAnimator = ValueAnimator.ofInt(currentHeightPx, targetHeightPx).apply {
      duration = 280
      interpolator = FastOutSlowInInterpolator()
      addUpdateListener { animator ->
        applyPresentedHeight(sheetDialog, bottomSheet, animator.animatedValue as Int)
      }
      addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          if (heightAnimator === animation) {
            heightAnimator = null
          }
        }
      })
      start()
    }
  }

  private fun applyPresentedHeight(
    sheetDialog: BottomSheetDialog,
    bottomSheet: FrameLayout,
    heightPx: Int,
  ) {
    val clampedHeightPx = heightPx.coerceAtLeast(1)
    presentedHeightPx = clampedHeightPx
    dialogContent?.let { content ->
      content.layoutParams = content.layoutParams.apply {
        height = clampedHeightPx
      }
    }
    val navigationBarExtensionPx = if (extendsIntoNavigationBar) {
      navigationBarInsetPx
    } else {
      0
    }
    bottomSheet.layoutParams = bottomSheet.layoutParams.apply {
      height = clampedHeightPx + navigationBarExtensionPx
    }
    sheetDialog.behavior.peekHeight = clampedHeightPx + navigationBarExtensionPx
    dialogContent?.requestLayout()
    bottomSheet.requestLayout()
    applyImeOffset(bottomSheet)
  }

  private fun getNavigationBarInset(view: View): Int =
    ViewCompat.getRootWindowInsets(view)
      ?.let { insets ->
        maxOf(
          insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom,
          insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars()).bottom,
        )
      }
      ?: 0

  private fun applyNavigationBarInset(
    sheetDialog: BottomSheetDialog,
    bottomSheet: FrameLayout,
    insets: WindowInsetsCompat,
  ) {
    val nextNavigationBarInsetPx = maxOf(
      insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom,
      insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars()).bottom,
    )
    if (navigationBarInsetPx == nextNavigationBarInsetPx) return
    navigationBarInsetPx = nextNavigationBarInsetPx
    updateNavigationBarBackground(sheetDialog)
    applyPresentedHeight(sheetDialog, bottomSheet, presentedHeightPx)
    applyImeOffset(bottomSheet)
  }

  private fun updateNavigationBarBackground(sheetDialog: BottomSheetDialog) {
    if (!extendsIntoNavigationBar || navigationBarInsetPx <= 0) {
      navigationBarBackgroundView?.let { view ->
        (view.parent as? ViewGroup)?.removeView(view)
      }
      navigationBarBackgroundView = null
      return
    }
    val decorView = sheetDialog.window?.decorView as? ViewGroup ?: return
    val backgroundView = navigationBarBackgroundView ?: View(reactContext).apply {
      isClickable = false
      isFocusable = false
      importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
      decorView.addView(this)
      navigationBarBackgroundView = this
    }
    backgroundView.setBackgroundColor(navigationBarBackgroundColor)
    backgroundView.layoutParams = FrameLayout.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      navigationBarInsetPx,
      Gravity.BOTTOM,
    )
    backgroundView.bringToFront()
  }

  private fun applyImeInset(
    bottomSheet: FrameLayout,
    insets: WindowInsetsCompat?,
  ) {
    val insetHost = windowInsetHost ?: return
    reportedImeInsetPx = insets
      ?.getInsets(WindowInsetsCompat.Type.ime())
      ?.bottom
      ?: 0
    val visibleFrame = Rect()
    insetHost.getWindowVisibleDisplayFrame(visibleFrame)
    val hostLocation = IntArray(2)
    insetHost.getLocationOnScreen(hostLocation)
    val obscuredBottomPx =
      (hostLocation[1] + insetHost.height - visibleFrame.bottom).coerceAtLeast(0)
    val minimumKeyboardInsetPx = dpToPx(100.0)
    visibleFrameImeInsetPx = if (obscuredBottomPx >= minimumKeyboardInsetPx) {
      obscuredBottomPx
    } else {
      0
    }
    val imeVisible =
      insets?.isVisible(WindowInsetsCompat.Type.ime()) == true ||
        reportedImeInsetPx >= minimumKeyboardInsetPx ||
        visibleFrameImeInsetPx > 0
    imeInsetPx = if (imeVisible) {
      maxOf(reportedImeInsetPx, visibleFrameImeInsetPx)
    } else {
      0
    }
    applyImeOffset(bottomSheet)
  }

  private fun applyImeOffset(bottomSheet: FrameLayout) {
    val sheetWindow = dialog?.window
    val decorView = sheetWindow?.decorView
    val isTop = NativeSheetStack.isTop(this)
    val behavior = BottomSheetBehavior.from(bottomSheet)
    if (decorView == null || !isTop || imeInsetPx <= 0) {
      bottomSheet.translationY = 0f
      val originalBottomPx = preImeBottomSheetBottomPx
      if (originalBottomPx != null) {
        val layoutHeight = bottomSheet.layoutParams.height.takeIf { it > 0 }
          ?: bottomSheet.height
        val restoredTop = originalBottomPx - layoutHeight
        if (isTop && imeAnimationRunning) {
          behavior.isFitToContents = false
          behavior.expandedOffset = restoredTop.coerceAtLeast(0)
          behavior.state = BottomSheetBehavior.STATE_EXPANDED
          ViewCompat.offsetTopAndBottom(bottomSheet, restoredTop - bottomSheet.top)
          bottomSheet.invalidate()
          return
        }
        ViewCompat.offsetTopAndBottom(bottomSheet, restoredTop - bottomSheet.top)
        preImeBottomSheetBottomPx = null
      }
      if (isTop || originalBottomPx != null) {
        behavior.isFitToContents = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheet.requestLayout()
      }
      return
    }

    val layoutHeight = bottomSheet.layoutParams.height.takeIf { it > 0 }
      ?: bottomSheet.height
    val originalBottomPx = preImeBottomSheetBottomPx
      ?: (bottomSheet.top + layoutHeight).also {
        preImeBottomSheetBottomPx = it
      }
    val restingTop = (originalBottomPx - layoutHeight).coerceAtLeast(0)
    val keyboardOcclusionPx =
      (imeInsetPx - navigationBarInsetPx).coerceAtLeast(0)
    val desiredTop = (restingTop - keyboardOcclusionPx).coerceAtLeast(0)
    behavior.isFitToContents = false
    behavior.expandedOffset = desiredTop
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
    bottomSheet.translationY = 0f
    ViewCompat.offsetTopAndBottom(bottomSheet, desiredTop - bottomSheet.top)
    bottomSheet.invalidate()
  }

  private fun clearWindowInsetsObservation() {
    cancelImeInsetFallback()
    val insetHost = windowInsetHost
    val globalLayoutListener = imeGlobalLayoutListener
    if (insetHost != null) {
      ViewCompat.setOnApplyWindowInsetsListener(insetHost, null)
      ViewCompat.setWindowInsetsAnimationCallback(insetHost, null)
      if (globalLayoutListener != null && insetHost.viewTreeObserver.isAlive) {
        insetHost.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
      }
    }
    windowInsetHost = null
    imeGlobalLayoutListener = null
  }

  private fun scheduleImeInsetFallback(
    insetHost: View,
    bottomSheet: FrameLayout,
  ) {
    cancelImeInsetFallback()
    val fallback = Runnable {
      if (windowInsetHost !== insetHost || imeAnimationRunning) return@Runnable
      imeInsetFallbackRunnable = null
      applyImeInset(
        bottomSheet,
        ViewCompat.getRootWindowInsets(insetHost),
      )
    }
    imeInsetFallbackRunnable = fallback
    insetHost.postDelayed(fallback, 48)
  }

  private fun cancelImeInsetFallback() {
    val fallback = imeInsetFallbackRunnable ?: return
    windowInsetHost?.removeCallbacks(fallback)
    imeInsetFallbackRunnable = null
  }

  private fun configureEdgeToEdgeWindow(window: Window) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.addFlags(
      WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      window.attributes = window.attributes.apply {
        setFitInsetsTypes(0)
        setFitInsetsSides(0)
      }
    }
  }

  private fun hideImeIfVisible(): Boolean {
    val insetHost = dialog?.window?.decorView ?: return false
    val insets = ViewCompat.getRootWindowInsets(insetHost) ?: return false
    if (!insets.isVisible(WindowInsetsCompat.Type.ime())) return false
    val window = dialog?.window ?: return false
    WindowCompat.getInsetsController(window, insetHost)
      .hide(WindowInsetsCompat.Type.ime())
    return true
  }

  internal fun dismissDialogInternal(
    reason: String,
    animated: Boolean,
    completion: () -> Unit,
  ) {
    val currentDialog = dialog
    if (currentDialog == null) {
      completion()
      return
    }
    dismissalCompletion = completion
    pendingDismissReason = reason
    heightAnimator?.cancel()
    heightAnimator = null
    if (animated) {
      currentDialog.dismissWithAnimation = true
    } else {
      currentDialog.dismissWithAnimation = false
    }
    animateDimAmount(currentDialog, 0f, animated)
    currentDialog.dismiss()
  }

  private fun animateDimAmount(
    sheetDialog: BottomSheetDialog,
    target: Float,
    animated: Boolean,
    completion: () -> Unit = {},
  ) {
    val window = sheetDialog.window
    if (window == null) {
      completion()
      return
    }
    dimAnimator?.cancel()
    if (!animated) {
      window.setDimAmount(target)
      completion()
      return
    }
    dimAnimator = ValueAnimator.ofFloat(window.attributes.dimAmount, target).apply {
      duration = 250
      interpolator = AccelerateDecelerateInterpolator()
      addUpdateListener { animator ->
        window.setDimAmount(animator.animatedValue as Float)
      }
      addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          completion()
        }
      })
      start()
    }
  }

  private fun handleDialogDismissed() {
    val child = contentChild
    clearWindowInsetsObservation()
    navigationBarBackgroundView?.let { view ->
      (view.parent as? ViewGroup)?.removeView(view)
    }
    navigationBarBackgroundView = null
    dialogContent = null
    dialog = null
    dialogConfigured = false
    presentedHeightPx = 0
    navigationBarInsetPx = 0
    imeInsetPx = 0
    reportedImeInsetPx = 0
    visibleFrameImeInsetPx = 0
    imeAnimationRunning = false
    extendsIntoNavigationBar = false
    navigationBarBackgroundColor = Color.TRANSPARENT
    preImeBottomSheetBottomPx = null
    dimAnimator?.cancel()
    dimAnimator = null
    heightAnimator?.cancel()
    heightAnimator = null
    child?.let { attachChild(this, it, 0) }
    NativeSheetStack.didDismiss(this)
    if (!dismissNotified) {
      dismissNotified = true
      dismissedForCurrentOpen = committedOpen
      onDismiss?.invoke(pendingDismissReason)
    }
    val completion = dismissalCompletion
    dismissalCompletion = null
    completion?.invoke()
  }

  internal fun finishFailedPresentation() {
    if (dismissNotified) return
    dismissNotified = true
    dismissedForCurrentOpen = committedOpen
    val reason = when {
      securityBlocked -> "security"
      !open -> "programmatic"
      else -> "system"
    }
    onDismiss?.invoke(reason)
  }

  fun onDropViewInstance() {
    NativeSheetStack.dismiss(this, "system", false)
    onDismiss = null
    onPresented = null
    eventDispatcher = null
  }

  private fun createHandle(): View = View(reactContext).apply {
    background = GradientDrawable().apply {
      shape = GradientDrawable.RECTANGLE
      cornerRadius = dpToPx(2.5).toFloat()
      setColor(0x33000000)
    }
    importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
  }

  private fun createSheetBackground(color: Int) = GradientDrawable().apply {
    shape = GradientDrawable.RECTANGLE
    setColor(color)
    cornerRadii = floatArrayOf(
      dpToPx(this@NativeSheetView.cornerRadius).toFloat(),
      dpToPx(this@NativeSheetView.cornerRadius).toFloat(),
      dpToPx(this@NativeSheetView.cornerRadius).toFloat(),
      dpToPx(this@NativeSheetView.cornerRadius).toFloat(),
      0f,
      0f,
      0f,
      0f,
    )
  }

  private fun createHandleLayoutParams() = LayoutParams(dpToPx(36.0), dpToPx(5.0)).apply {
    gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
    topMargin = dpToPx(8.0)
  }

  private fun attachChild(parent: ViewGroup, child: View, index: Int) {
    if (child.parent === parent) return
    (child.parent as? ViewGroup)?.removeView(child)
    val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    parent.addView(child, index.coerceIn(0, parent.childCount), params)
  }

  private fun dpToPx(value: Double): Int =
    (value * resources.displayMetrics.density).roundToInt()

  private fun pxToDp(value: Int): Double = value / resources.displayMetrics.density.toDouble()

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    if (!isAttachedToWindow) NativeSheetStack.dismiss(this, "system", false)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    commitConfiguration()
  }
}
