package com.margelo.nitro.onekeyimage

import android.app.Activity
import android.graphics.Bitmap
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlags
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlagsDefaults
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlagsForTests
import com.facebook.react.uimanager.DisplayMetricsHolder
import com.facebook.react.uimanager.ThemedReactContext
import com.margelo.nitro.R.id.associated_hybrid_view_tag
import com.margelo.nitro.onekeyimage.views.HybridOneKeyIconManager
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OneKeyIconLifecycleTest {
  private val controller = Robolectric.buildActivity(Activity::class.java)
  private lateinit var context: ThemedReactContext
  private lateinit var container: FrameLayout

  @Before
  fun setUp() {
    ReactNativeFeatureFlagsForTests.setUp()
    // The app keeps React Native's default: Fabric view recycling disabled.
    ReactNativeFeatureFlags.override(object : ReactNativeFeatureFlagsDefaults() {
      override fun enableViewRecycling() = false
    })
    val activity = controller.setup().visible().get()
    DisplayMetricsHolder.initDisplayMetrics(activity)
    container = FrameLayout(activity)
    activity.setContentView(container)
    context = ThemedReactContext(BridgeReactContext(activity.application), activity, "test", 1)
  }

  @After
  fun tearDown() {
    controller.pause().stop().destroy()
    ReactNativeFeatureFlags.dangerouslyReset()
  }

  @Test
  fun fabricDropKeepsTheIconDrawnUntilTheOutgoingScreenDetaches() {
    val manager = HybridOneKeyIconManager()
    val outgoing = manager.createView(1, context, null, null, null) as ImageView
    attach(outgoing)
    bind(outgoing)
    val drawable = outgoing.drawable

    // ScreenStack keeps drawing a removed screen until its exit transition ends.
    container.startViewTransition(outgoing)
    container.removeView(outgoing)
    assertTrue(outgoing.isAttachedToWindow)
    manager.onDropViewInstance(outgoing)

    assertEquals(32, outgoing.width)
    assertEquals(32, outgoing.height)
    assertSame(drawable, outgoing.drawable)

    container.endViewTransition(outgoing)
    assertFalse(outgoing.isAttachedToWindow)
    assertNull(outgoing.drawable)
  }

  @Test
  fun dropAfterDetachClearsImmediately() {
    val manager = HybridOneKeyIconManager()
    val view = manager.createView(1, context, null, null, null) as ImageView
    attach(view)
    bind(view)

    container.removeView(view)
    manager.onDropViewInstance(view)

    assertNull(view.drawable)
  }

  /** Binds props like Fabric, then stands in for the decoded frame. */
  private fun bind(view: ImageView) {
    val icon = hybridIcon(view)
    icon.iconName = "TestIcon"
    icon.resolvedColor = "rgba(0,0,0,1)"
    icon.afterUpdate()
    // Let the asset-less render settle before painting the stand-in frame.
    repeat(20) {
      shadowOf(Looper.getMainLooper()).idle()
      Thread.sleep(10)
    }
    view.setImageBitmap(Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888))
  }

  private fun hybridIcon(view: View): HybridOneKeyIcon {
    val holder = view.getTag(associated_hybrid_view_tag)
    return holder.javaClass.getDeclaredField("hybridView").run {
      isAccessible = true
      get(holder) as HybridOneKeyIcon
    }
  }

  private fun attach(view: View) {
    container.addView(view, FrameLayout.LayoutParams(32, 32))
    view.layout(0, 0, 32, 32)
  }
}
