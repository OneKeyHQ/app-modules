package com.onekey.nativeoverlay

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Looper
import android.view.View
import androidx.activity.ComponentActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.uimanager.ThemedReactContext
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NativeOverlayMotionTest {
  @Test
  fun sheetAnimationMovesCoordinatorWithoutTakingMaterialsSheetTranslation() {
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
    val reactContext = BridgeReactContext(activity)
    reactContext.onHostResume(activity)
    val context = ThemedReactContext(reactContext, activity, "OverlayMotionTest", 1)
    val entry = NativeOverlayEntryRootView(context)
    assertSame(entry.contentView, entry.animatedView)

    entry.configureSheet(500, 16f, Color.WHITE, showHandle = true, draggable = true)
    val sheet = entry.sheetView!!
    val animated = entry.animatedView
    assertTrue(animated is CoordinatorLayout)
    assertSame(animated, sheet.parent)
    assertNotSame(sheet, animated)
    assertTrue((sheet.layoutParams as CoordinatorLayout.LayoutParams).behavior is BottomSheetBehavior<*>)

    animated.translationY = 42f
    assertEquals(0f, sheet.translationY)
    sheet.translationY = 7f
    assertEquals(42f, animated.translationY)
    assertEquals(7f, sheet.translationY)
  }

  @Test
  fun quickMatchesMeasuredCriticalResponseWithoutChangingUnderdampedResponse() {
    val quick = OverlayMotion.QUICK
    assertEquals(292L, quick.durationMs())
    assertEquals(0.8238140348, SpringInterpolator.progressAt(quick, 0.1), 0.000001)
    assertEquals(0.9990003945, SpringInterpolator.progressAt(quick, 0.292), 0.000001)

    val underdamped = OverlayMotion.Spring(1.0, 100.0, 5.0)
    assertEquals(0.3929451508, SpringInterpolator.progressAt(underdamped, 0.1), 0.000001)
    val timing = OverlayMotion.Timing(250L, 0.25f, 0.1f, 0.25f, 1f)
    assertEquals(250L, timing.durationMs())
  }

  @Test
  fun reopeningTheSameEntryDuringExitRetargetsItsAnimation() {
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().visible().get()
    val reactContext = BridgeReactContext(activity)
    reactContext.onHostResume(activity)
    val context = ThemedReactContext(reactContext, activity, "OverlayMotionTest", 1)
    val child = View(context)
    val overlay = NativeOverlayView(context).apply {
      presentation = "sheet"
      sheetHeight = 200.0
      addReactChild(child)
    }
    activity.setContentView(overlay)
    var dismissals = 0
    overlay.onDismissed = { dismissals++ }
    overlay.visible = true
    overlay.commitConfiguration()

    val entryField = NativeOverlayView::class.java.getDeclaredField("entry").apply { isAccessible = true }
    val original = entryField.get(overlay) as NativeOverlayEntryRootView
    Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(350))
    overlay.visible = false
    overlay.commitConfiguration()
    assertTrue(original.isClosing)
    val animatorField = NativeOverlayView::class.java.getDeclaredField("contentAnimator").apply { isAccessible = true }
    val exitAnimator = animatorField.get(overlay) as ValueAnimator
    assertTrue(exitAnimator.isStarted)
    assertEquals(292L, exitAnimator.duration)
    exitAnimator.currentPlayTime = exitAnimator.duration / 3
    val alphaBeforeReopen = original.animatedView.alpha
    assertTrue(alphaBeforeReopen > 0f && alphaBeforeReopen < 1f)
    assertTrue(original.isClosing)

    overlay.visible = true
    overlay.commitConfiguration()
    assertSame(original, entryField.get(overlay))
    assertSame(child, original.contentView.getChildAt(0))
    assertEquals(alphaBeforeReopen, original.animatedView.alpha, 0.01f)
    assertTrue(original.isShownForInput)
    assertFalse(original.isClosing)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, original.contentView.importantForAccessibility)
    Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(350))
    assertEquals(0, dismissals)

    overlay.visible = false
    overlay.commitConfiguration()
    Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(350))
    assertEquals(1, dismissals)
  }

  @Test
  fun finishingExitMovesTransitioningContentBackToStagingView() {
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().visible().get()
    val reactContext = BridgeReactContext(activity)
    reactContext.onHostResume(activity)
    val context = ThemedReactContext(reactContext, activity, "OverlayMotionTest", 1)
    val child = View(context)
    val overlay = NativeOverlayView(context).apply {
      addReactChild(child)
    }
    activity.setContentView(overlay)
    var dismissals = 0
    overlay.onDismissed = { dismissals++ }

    overlay.visible = true
    overlay.commitConfiguration()
    val entryField = NativeOverlayView::class.java.getDeclaredField("entry").apply { isAccessible = true }
    val entry = entryField.get(overlay) as NativeOverlayEntryRootView
    Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(350))

    entry.contentView.startViewTransition(child)
    overlay.visible = false
    overlay.commitConfiguration()
    Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(350))
    assertSame(overlay, child.parent)
    assertEquals(0, entry.contentView.childCount)
    assertEquals(1, overlay.childCount)
    assertSame(child, overlay.getChildAt(0))
    entry.contentView.endViewTransition(child)
    assertSame(overlay, child.parent)
    assertEquals(1, dismissals)
  }

  @Test
  fun presentingMovesTransitioningContentIntoEntry() {
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().visible().get()
    val reactContext = BridgeReactContext(activity)
    reactContext.onHostResume(activity)
    val context = ThemedReactContext(reactContext, activity, "OverlayMotionTest", 1)
    val child = View(context)
    val overlay = NativeOverlayView(context).apply {
      addReactChild(child)
    }
    activity.setContentView(overlay)

    overlay.startViewTransition(child)
    overlay.visible = true
    overlay.commitConfiguration()

    val entryField = NativeOverlayView::class.java.getDeclaredField("entry").apply { isAccessible = true }
    val entry = entryField.get(overlay) as NativeOverlayEntryRootView
    assertSame(entry.contentView, child.parent)
    assertEquals(0, overlay.childCount)
    assertEquals(1, entry.contentView.childCount)
    assertSame(child, entry.contentView.getChildAt(0))
    overlay.endViewTransition(child)
    assertSame(entry.contentView, child.parent)
  }
}
