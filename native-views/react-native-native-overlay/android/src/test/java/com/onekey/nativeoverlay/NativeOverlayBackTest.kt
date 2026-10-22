package com.onekey.nativeoverlay

import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.uimanager.PointerEvents
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.TouchTargetHelper
import com.facebook.react.views.view.ReactViewGroup
import java.lang.ref.WeakReference
import java.util.WeakHashMap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NativeOverlayBackTest {
  private lateinit var activity: ComponentActivity
  private lateinit var context: ThemedReactContext
  private lateinit var host: NativeOverlayHost
  private val dismissals = mutableListOf<String>()

  @Before
  fun setUp() {
    activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().visible().get()
    val reactContext = BridgeReactContext(activity)
    reactContext.onHostResume(activity)
    context = ThemedReactContext(reactContext, activity, "OverlayBackTest", 1)
    host = NativeOverlayHost.of(activity)
  }

  private fun entry(name: String, blocking: Boolean, dismissible: Boolean, order: Int = 0) =
    NativeOverlayEntryRootView(context).apply {
      this.blocking = blocking
      dismissOnBackPress = dismissible
      stackOrder = order
      isShownForInput = true
      onRequestDismiss = { dismissals.add("$name:$it") }
    }

  private fun back(action: Int = KeyEvent.ACTION_UP, flags: Int = 0) =
    host.handleBackKey(KeyEvent(0L, 0L, action, KeyEvent.KEYCODE_BACK, 0, 0, 0, 0, flags))

  @Test
  fun dismissibleNonBlockingGlobalEntryWinsOverLowerBlocker() {
    host.attach(entry("lower", true, true), NativeOverlayLevel.MODAL)
    host.attach(entry("top", false, true), NativeOverlayLevel.TOAST)
    assertTrue(back(KeyEvent.ACTION_DOWN))
    assertEquals(emptyList<String>(), dismissals)
    assertTrue(back())
    assertEquals(listOf("top:back"), dismissals)
  }

  @Test
  fun dispatcherHandlesNonBlockingEntryAndKeepsPageAccessible() {
    val page = View(activity).apply { importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES }
    activity.setContentView(page)
    val top = entry("top", false, true)
    host.attach(top, NativeOverlayLevel.MODAL)
    activity.onBackPressedDispatcher.onBackPressed()
    assertEquals(listOf("top:back"), dismissals)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_YES, page.importantForAccessibility)
    host.detach(top)
    assertFalse(back())
  }

  @Test
  fun nonDismissibleBlockerConsumesBackWithoutClosingLowerEntry() {
    host.attach(entry("lower", false, true), NativeOverlayLevel.MODAL)
    host.attach(entry("lock", true, false), NativeOverlayLevel.LOCK)
    assertTrue(back())
    assertEquals(emptyList<String>(), dismissals)
  }

  @Test
  fun passiveOrNotInputReadyEntriesDoNotTakeBack() {
    host.attach(entry("passive", false, false), NativeOverlayLevel.TOAST)
    host.attach(entry("hidden", true, true).apply { isShownForInput = false }, NativeOverlayLevel.LOCK)
    assertFalse(back())
    assertEquals(emptyList<String>(), dismissals)
  }

  @Test
  fun canceledKeyUpDoesNotDismiss() {
    host.attach(entry("top", false, true), NativeOverlayLevel.MODAL)
    assertTrue(back(flags = KeyEvent.FLAG_CANCELED))
    assertEquals(emptyList<String>(), dismissals)
  }

  @Test
  fun dismissiblePageEntryWinsOverLowerPageBlockerAndSuspensionSkipsIt() {
    val pageHost = NativeOverlayPageHostView(context).apply { hostKey = "page-back-test" }
    activity.setContentView(pageHost)
    val lower = entry("lower", true, true).apply { ownerKey = "owner" }
    val top = entry("top", false, true, 1).apply { ownerKey = "owner" }
    pageHost.attach(lower)
    pageHost.attach(top)
    assertTrue(back())
    assertEquals(listOf("top:back"), dismissals)
    pageHost.setSuspendedOwners("owner")
    assertFalse(back())
    pageHost.invalidateHost()
  }

  @Test
  fun globalEntryTakesBackBeforePageEntryAndDismissalRestoresPageTarget() {
    val pageHost = NativeOverlayPageHostView(context).apply { hostKey = "page-global-test" }
    activity.setContentView(FrameLayout(activity).apply { addView(pageHost) })
    pageHost.attach(entry("page", false, true))
    val global = entry("global", false, true)
    host.attach(global, NativeOverlayLevel.MODAL)
    assertTrue(back())
    host.detach(global)
    assertTrue(back())
    assertEquals(listOf("global:back", "page:back"), dismissals)
    pageHost.invalidateHost()
  }

  @Test
  fun closingGlobalBlockerKeepsTouchBackAndAccessibilityOwnership() {
    var pageTouches = 0
    val page = View(activity).apply {
      setOnTouchListener { _, _ ->
        pageTouches += 1
        true
      }
    }
    activity.setContentView(page)
    val lower = entry("lower", true, true)
    val top = entry("top", true, true, 1)
    host.attach(lower, NativeOverlayLevel.MODAL)
    host.attach(top, NativeOverlayLevel.MODAL)
    top.isClosing = true
    host.onEntriesChanged()

    assertEquals(PointerEvents.BOX_ONLY, top.pointerEvents)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS, top.contentView.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS, page.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS, lower.importantForAccessibility)
    assertTrue(back())
    assertEquals(emptyList<String>(), dismissals)

    val content = activity.findViewById<FrameLayout>(android.R.id.content)
    val size = View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.EXACTLY)
    content.measure(size, size)
    content.layout(0, 0, 300, 300)
    val down = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_DOWN, 150f, 150f, 0)
    try {
      assertTrue(content.dispatchTouchEvent(down))
      assertEquals(0, pageTouches)
    } finally {
      down.recycle()
    }

    top.isClosing = false
    host.onEntriesChanged()
    assertEquals(PointerEvents.AUTO, top.pointerEvents)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, top.contentView.importantForAccessibility)
    assertTrue(back())
    assertEquals(listOf("top:back"), dismissals)
  }

  @Test
  fun closingPageBlockerStillTakesBack() {
    val pageHost = NativeOverlayPageHostView(context).apply { hostKey = "closing-page" }
    activity.setContentView(pageHost)
    val top = entry("page", true, true)
    pageHost.attach(top)
    top.isClosing = true
    host.onEntriesChanged()

    assertEquals(PointerEvents.BOX_ONLY, top.pointerEvents)
    assertTrue(back())
    assertEquals(emptyList<String>(), dismissals)
    top.isClosing = false
    host.onEntriesChanged()
    assertEquals(PointerEvents.AUTO, top.pointerEvents)
    assertTrue(back())
    assertEquals(listOf("page:back"), dismissals)
    pageHost.invalidateHost()
  }

  @Test
  fun pageEntryHitTestingKeepsClosingContentAndLowerPageSeparate() {
    val root = ReactViewGroup(context).apply { id = 101 }
    val below = ReactViewGroup(context).apply { id = 102 }
    val child = ReactViewGroup(context).apply { id = 103 }
    val pageHost = NativeOverlayPageHostView(context).apply { hostKey = "page-hit-test" }
    root.addView(below)
    root.addView(pageHost)
    activity.setContentView(root)
    val overlay = entry("page", true, true)
    overlay.contentView.addView(child)
    pageHost.attach(overlay)

    val size = View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.EXACTLY)
    root.measure(size, size)
    root.layout(0, 0, 300, 300)
    below.layout(0, 0, 300, 300)
    pageHost.layout(0, 0, 300, 300)
    child.layout(0, 0, 300, 300)

    fun target() = TouchTargetHelper.findTargetTagForTouch(150f, 150f, root)
    assertEquals(child.id, target())
    overlay.isClosing = true
    assertEquals(root.id, target())
    overlay.updateBlocking(false)
    assertEquals(below.id, target())
    overlay.updateBlocking(true)
    overlay.isClosing = false
    assertEquals(child.id, target())
    pageHost.invalidateHost()
  }

  @Test
  fun reopeningCoveredClosingEntryRestoresItsContentAccessibility() {
    activity.setContentView(View(activity))
    val first = entry("first", true, true)
    val second = entry("second", true, true)
    host.attach(first, NativeOverlayLevel.MODAL)
    first.isClosing = true
    host.onEntriesChanged()
    host.attach(second, NativeOverlayLevel.SECURE)

    first.isClosing = false
    host.onEntriesChanged()
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS, first.importantForAccessibility)
    host.detach(second)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, first.importantForAccessibility)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, first.contentView.importantForAccessibility)
  }

  @Test
  fun closingNonBlockingEntryPassesTouchAndBackToLowerBlocker() {
    val lower = entry("lower", true, true)
    val toast = entry("toast", false, true)
    host.attach(lower, NativeOverlayLevel.MODAL)
    host.attach(toast, NativeOverlayLevel.TOAST)
    toast.isClosing = true
    host.onEntriesChanged()

    assertEquals(PointerEvents.NONE, toast.pointerEvents)
    val down = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_DOWN, 150f, 150f, 0)
    try {
      assertFalse(toast.dispatchTouchEvent(down))
    } finally {
      down.recycle()
    }
    assertTrue(back())
    assertEquals(listOf("lower:back"), dismissals)
  }

  @Test
  fun hostCacheReusesLiveHostAndSeparatesActivities() {
    assertSame(host, NativeOverlayHost.of(activity))
    val otherActivity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
    assertNotSame(host, NativeOverlayHost.of(otherActivity))
  }

  @Test
  fun hostCacheRebuildsAfterWeakValueIsCleared() {
    val field = NativeOverlayHost::class.java.getDeclaredField("hosts").apply {
      isAccessible = true
    }
    val cache = field.get(null) as WeakHashMap<*, *>
    (cache[activity] as WeakReference<*>).clear()
    val replacement = NativeOverlayHost.of(activity)
    assertNotSame(host, replacement)
    assertSame(replacement, NativeOverlayHost.of(activity))
  }

  @Test
  fun pageOnlyBackInterceptorRetainsItsHost() {
    val pageHost = NativeOverlayPageHostView(context).apply { hostKey = "page-only" }
    activity.setContentView(pageHost)
    pageHost.attach(entry("page", false, true))
    host.installBackInterceptor()
    assertTrue(activity.window.callback.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK)))
    assertEquals(listOf("page:back"), dismissals)
    assertSame(host, NativeOverlayHost.of(activity))
    pageHost.invalidateHost()
  }
}
