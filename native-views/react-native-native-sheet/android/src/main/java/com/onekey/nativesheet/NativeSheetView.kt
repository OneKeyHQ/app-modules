package com.onekey.nativesheet

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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

  private fun compact() {
    active.removeAll { it.get() == null }
  }

  fun present(view: NativeSheetView) {
    compact()
    if (active.any { it.get() === view }) return
    view.showDialogInternal()
    if (view.hasDialog()) active.add(WeakReference(view))
  }

  fun dismiss(view: NativeSheetView, reason: String, animated: Boolean) {
    compact()
    val index = active.indexOfFirst { it.get() === view }
    if (index < 0) {
      view.dismissDialogInternal(reason, animated)
      return
    }

    val targets = active.subList(index, active.size).mapNotNull { it.get() }.asReversed()
    active.subList(index, active.size).clear()
    targets.forEach { target ->
      val targetReason = if (target === view) reason else if (reason == "security") reason else "system"
      target.dismissDialogInternal(targetReason, animated)
    }
  }

  fun didDismiss(view: NativeSheetView) {
    active.removeAll { it.get() == null || it.get() === view }
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
    }
  }

  fun hasDialog(): Boolean = dialog != null

  internal fun showDialogInternal() {
    if (dialog != null || securityBlocked || !open) return
    val activity = reactContext.currentActivity ?: return
    if (activity.isFinishing || activity.isDestroyed) return

    val heightPx = dpToPx(sheetHeight.coerceAtLeast(1.0))
    val content = NativeSheetDialogRootView(reactContext).apply {
      eventDispatcher = this@NativeSheetView.eventDispatcher
      layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx)
      background = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(sheetBackgroundColor ?: Color.WHITE)
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
      clipToOutline = true
    }
    contentChild?.let { attachChild(content, it, 0) }
    if (showHandle) content.addView(createHandle(), createHandleLayoutParams())

    pendingDismissReason = "system"
    dismissNotified = false
    val nextDialog = BottomSheetDialog(activity).apply {
      setContentView(content)
      setCancelable(dismissOnPanDown || dismissOnBackPress || dismissOnBackdropPress)
      setCanceledOnTouchOutside(dismissOnBackdropPress)
      dismissWithAnimation = true
      setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
          if (!dismissOnBackPress) return@setOnKeyListener true
          pendingDismissReason = "back"
        }
        false
      }
      setOnCancelListener {
        if (pendingDismissReason == "system") pendingDismissReason = "backdrop"
      }
      setOnDismissListener {
        handleDialogDismissed()
      }
      setOnShowListener {
        configureShownDialog(this, heightPx)
      }
    }

    dialogContent = content
    dialog = nextDialog
    nextDialog.show()
  }

  private fun configureShownDialog(sheetDialog: BottomSheetDialog, heightPx: Int) {
    sheetDialog.window?.apply {
      setDimAmount(dimAmount.coerceIn(0.0, 1.0).toFloat())
      setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
    val bottomSheet = sheetDialog.findViewById<FrameLayout>(
      com.google.android.material.R.id.design_bottom_sheet,
    ) ?: return
    bottomSheet.layoutParams = bottomSheet.layoutParams.apply { height = heightPx }
    bottomSheet.setBackgroundColor(Color.TRANSPARENT)
    sheetDialog.behavior.apply {
      peekHeight = heightPx
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

        override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
      })
    }
    onPresented?.invoke(pxToDp(heightPx))
  }

  internal fun dismissDialogInternal(reason: String, animated: Boolean) {
    val currentDialog = dialog ?: return
    pendingDismissReason = reason
    if (animated) {
      currentDialog.dismissWithAnimation = true
    } else {
      currentDialog.dismissWithAnimation = false
    }
    currentDialog.dismiss()
  }

  private fun handleDialogDismissed() {
    val child = contentChild
    dialogContent = null
    dialog = null
    child?.let { attachChild(this, it, 0) }
    NativeSheetStack.didDismiss(this)
    if (!dismissNotified) {
      dismissNotified = true
      dismissedForCurrentOpen = committedOpen
      onDismiss?.invoke(pendingDismissReason)
    }
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
