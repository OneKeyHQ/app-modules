package com.onekey.nativeoverlay

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlagsForTests
import com.facebook.react.uimanager.ThemedReactContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NativeOverlaySheetInsetTest {
  private fun newEntry(): NativeOverlayEntryRootView {
    ReactNativeFeatureFlagsForTests.setUp()
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().visible().get()
    val reactContext = BridgeReactContext(activity)
    val context = ThemedReactContext(reactContext, activity, "SheetInsetTest", 1)
    return NativeOverlayEntryRootView(context)
  }

  @Test
  fun paintsTheNavigationBarStripBehindTheContent() {
    val entry = newEntry()
    // Platform-painted sheet: the platform background reaches the inset.
    entry.configureSheet(200, 24f, Color.WHITE, false, true)
    assertNull(entry.sheetBottomInsetPaintView)
    val sheet = entry.sheetView!!
    val content = entry.contentView

    entry.updateSheetBottomInsetPaint(48, Color.WHITE, 12f)

    val paint = entry.sheetBottomInsetPaintView!!
    assertTrue(sheet.indexOfChild(paint) < sheet.indexOfChild(content))
    val params = paint.layoutParams as FrameLayout.LayoutParams
    assertEquals(Gravity.BOTTOM, params.gravity)
    assertEquals(12, params.leftMargin)
    assertEquals(12, params.rightMargin)
    // The inset plus a small overlap over the fractional content height.
    assertTrue(params.height >= 48)
    assertEquals(Color.WHITE, (paint.background as ColorDrawable).color)
  }

  @Test
  fun clearsTheStripWhenThePlatformSheetPaintsTheSurface() {
    val entry = newEntry()
    entry.configureSheet(200, 24f, Color.TRANSPARENT, false, true)
    val sheet = entry.sheetView!!
    entry.updateSheetBottomInsetPaint(48, Color.WHITE, 0f)
    assertTrue(sheet.childCount == 3)

    entry.updateSheetBottomInsetPaint(48, null, 0f)

    assertEquals(2, sheet.childCount)
    assertNull(entry.sheetBottomInsetPaintView)
  }

  @Test
  fun keepsTheStripSquareWhenTheSurfaceIsNarrowerThanTheSheet() {
    val entry = newEntry()
    entry.configureSheet(200, 24f, Color.TRANSPARENT, false, true)
    entry.updateSheetBottomInsetPaint(48, Color.BLACK, 40f)

    val params = entry.sheetBottomInsetPaintView!!.layoutParams as FrameLayout.LayoutParams
    assertEquals(40, params.leftMargin)
    assertEquals(40, params.rightMargin)
    assertEquals(Gravity.BOTTOM, params.gravity)
  }
}
