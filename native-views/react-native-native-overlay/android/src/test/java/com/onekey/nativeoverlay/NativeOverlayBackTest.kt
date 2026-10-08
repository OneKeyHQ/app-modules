package com.onekey.nativeoverlay

import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.uimanager.ThemedReactContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
}
