package com.onekey.nativeoverlay

import android.app.Activity
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import java.util.WeakHashMap

/** Mirrors `OVERLAY_LEVEL_ORDER`. */
internal enum class NativeOverlayLevel(val order: Int) {
  MODAL(100),
  HARDWARE(200),
  SECURE(300),
  TOAST(400),
  LOCK(500),
  DEBUG(900);

  companion object {
    fun from(value: String?): NativeOverlayLevel =
      entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MODAL
  }
}

private class BackInterceptingCallback(
  private val delegate: Window.Callback,
  private val host: NativeOverlayHost,
) : Window.Callback by delegate {
  override fun dispatchKeyEvent(event: KeyEvent): Boolean {
    if (event.keyCode == KeyEvent.KEYCODE_BACK && host.handleBackKey(event)) return true
    return delegate.dispatchKeyEvent(event)
  }
}

/**
 * One per activity: a FrameLayout added after the ReactRootView inside
 * `android.R.id.content`, with one child container per level. Overlays are
 * views, not windows, so order between levels is plain child order and
 * touches that nothing consumes fall through to the React root below.
 */
internal class NativeOverlayHost private constructor(
  private val activity: Activity,
) : FrameLayout(activity) {
  private val levelContainers = sortedMapOf<NativeOverlayLevel, FrameLayout>(
    compareBy { it.order },
  )
  private val backCallback = object : OnBackPressedCallback(false) {
    override fun handleOnBackPressed() {
      topBackEntry()?.let { entry ->
        if (entry.dismissOnBackPress) entry.onRequestDismiss?.invoke("back")
      }
    }
  }
  private val accessibilityIsolation = NativeOverlayAccessibilityIsolation()

  init {
    clipChildren = false
  }

  /**
   * Called for KEYCODE_BACK before the activity sees it. ReactActivity's
   * `onBackPressed` forwards to JS BackHandler (react-navigation pops the
   * screen) before the OnBackPressedDispatcher, and the app opts out of
   * predictive back, so the dispatcher alone never runs first.
   */
  fun handleBackKey(event: KeyEvent): Boolean {
    val entry = topBackEntry() ?: return false
    if (event.action == KeyEvent.ACTION_UP && !event.isCanceled && entry.dismissOnBackPress) {
      entry.onRequestDismiss?.invoke("back")
    }
    return true
  }

  internal fun installBackInterceptor() {
    val window = activity.window ?: return
    val current = window.callback ?: return
    if (current !is BackInterceptingCallback) {
      window.callback = BackInterceptingCallback(current, this)
    }
  }

  fun attach(entry: NativeOverlayEntryRootView, level: NativeOverlayLevel) {
    installBackInterceptor()
    val content = activity.findViewById<ViewGroup>(android.R.id.content) ?: return
    if (parent !== content) {
      (parent as? ViewGroup)?.removeView(this)
      content.addView(this, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    } else if (content.getChildAt(content.childCount - 1) !== this) {
      // Something (e.g. a dev overlay) was added later; stay on top.
      bringToFront()
    }
    val container = levelContainer(level)
    if (entry.parent !== container) {
      (entry.parent as? ViewGroup)?.removeView(entry)
      val index = (0 until container.childCount).firstOrNull { i ->
        (container.getChildAt(i) as? NativeOverlayEntryRootView)?.let { it.stackOrder > entry.stackOrder } == true
      } ?: container.childCount
      container.addView(entry, index, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }
    onEntriesChanged()
  }

  fun detach(entry: NativeOverlayEntryRootView) {
    (entry.parent as? ViewGroup)?.removeView(entry)
    onEntriesChanged()
  }

  fun onEntriesChanged() {
    val hasBackEntry = topBackEntry() != null
    if (hasBackEntry && !backCallback.isEnabled) {
      // The most recently added callback wins; re-add so ours runs before
      // ReactActivity's and react-native-screens' callbacks.
      backCallback.remove()
      (activity as? ComponentActivity)?.onBackPressedDispatcher?.addCallback(backCallback)
    }
    backCallback.isEnabled = hasBackEntry
    updateAccessibility()
  }

  private fun levelContainer(level: NativeOverlayLevel): FrameLayout =
    levelContainers.getOrPut(level) {
      val container = FrameLayout(activity).apply { clipChildren = false }
      val index = levelContainers.keys.count { it.order < level.order }
      addView(container, index, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
      container
    }

  private fun orderedEntries(): List<NativeOverlayEntryRootView> =
    levelContainers.values.flatMap { container ->
      (0 until container.childCount).mapNotNull { container.getChildAt(it) as? NativeOverlayEntryRootView }
    }

  /** Global overlays win; otherwise the topmost visible page overlay. */
  private fun topBackEntry(): NativeOverlayEntryRootView? =
    orderedEntries().lastOrNull { (it.blocking || it.dismissOnBackPress) && it.isShownForInput }
      ?: shownPageHosts()
        .flatMap { it.shownEntries() }
        .lastOrNull { it.blocking || it.dismissOnBackPress }

  private fun shownPageHosts(): List<NativeOverlayPageHostView> =
    NativeOverlayPageHostView.allHosts().filter {
      it.isAttachedToWindow && it.isShown && it.rootView === activity.window.decorView
    }

  /** TalkBack only reads the topmost blocking overlay and what is above it. */
  private fun updateAccessibility() {
    val entries = orderedEntries()
    val topIndex = entries.indexOfLast { it.blocking && it.isShownForInput }
    val content = activity.findViewById<ViewGroup>(android.R.id.content) ?: return
    if (topIndex >= 0) {
      accessibilityIsolation.update(content, this, entries.take(topIndex), this)
      return
    }
    val pageEntry = shownPageHosts().flatMap { it.shownEntries() }.lastOrNull { it.blocking }
    val pageHost = pageEntry?.parent as? NativeOverlayPageHostView
    val below = pageHost?.shownEntries()?.takeWhile { it !== pageEntry }.orEmpty()
    accessibilityIsolation.update(content, pageHost, below, this)
  }

  companion object {
    private val hosts = WeakHashMap<Activity, NativeOverlayHost>()

    fun of(activity: Activity): NativeOverlayHost =
      hosts.getOrPut(activity) { NativeOverlayHost(activity) }
  }
}
