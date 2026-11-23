package com.onekey.nativeoverlay

import android.graphics.Color
import android.os.Looper
import android.view.View
import androidx.activity.ComponentActivity
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlagsForTests
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.views.view.ReactViewGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NativeOverlayPageHostLayoutTest {
  @Test
  fun pageSheetTracksHeightChangesAfterAttachInsideFabricParent() {
    ReactNativeFeatureFlagsForTests.setUp()
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().visible().get()
    val reactContext = BridgeReactContext(activity)
    val context = ThemedReactContext(reactContext, activity, "PageHostLayoutTest", 1)
    val parent = ReactViewGroup(context)
    val host = NativeOverlayPageHostView(context)
    parent.addView(host)
    activity.setContentView(parent)
    val widthSpec = View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY)
    val heightSpec = View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY)
    host.measure(widthSpec, heightSpec)
    host.layout(0, 0, 400, 1000)
    val entry = NativeOverlayEntryRootView(context)
    entry.configureSheet(200, 24f, Color.WHITE, false, true)

    host.attach(entry)
    Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(100))
    val sheet = entry.sheetView!!
    assertEquals(entry.sheetHeightPx(), sheet.height)
    assertEquals(1000 - entry.sheetHeightPx(), sheet.top)

    entry.configureSheet(600, 24f, Color.WHITE, false, true)
    Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(100))
    assertEquals(entry.sheetHeightPx(), sheet.height)
    assertEquals(1000 - entry.sheetHeightPx(), sheet.top)
    assertFalse(host.isLayoutRequested)

    entry.configureSheet(320, 24f, Color.WHITE, false, true)
    Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(100))
    assertEquals(entry.sheetHeightPx(), sheet.height)
    assertEquals(1000 - entry.sheetHeightPx(), sheet.top)
  }
}
