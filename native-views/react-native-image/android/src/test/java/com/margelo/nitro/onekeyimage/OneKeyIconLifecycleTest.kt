package com.margelo.nitro.onekeyimage

import android.app.Activity
import android.graphics.Bitmap
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
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
class OneKeyIconLifecycleTest {
  private val controller = Robolectric.buildActivity(Activity::class.java)
  private lateinit var context: ThemedReactContext
  private lateinit var container: FrameLayout

  @Before
  fun setUp() {
    ReactNativeFeatureFlagsForTests.setUp()
    ReactNativeFeatureFlags.override(object : ReactNativeFeatureFlagsDefaults() {
      override fun enableViewRecycling() = true
    })
    val activity = controller.setup().visible().get()
    DisplayMetricsHolder.initDisplayMetrics(activity)
    container = FrameLayout(activity)
    activity.setContentView(container)
    context = ThemedReactContext(BridgeReactContext(activity.application), activity, "test", 1)

    // Seed a decoded tintable icon so binding exercises the actual view without worker timing.
    OneKeyNamedSVG.resetStartupForTesting(cacheDir = activity.cacheDir)
    OneKeyNamedSVG.setManifestFallbackForTesting(mapOf(
      "TestIcon" to OneKeyNamedSVG.ManifestEntry(
        digest = "test-digest",
        mode = OneKeyNamedSVG.RecolorMode.SINGLE,
      ),
    ))
    OneKeyNamedSVG.diskPngDecoderOverrideForTesting = { _, width, height ->
      Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }
    val scale = context.resources.displayMetrics.density
    val raster = requireNotNull(OneKeyNamedSVG.diskRasterFile("TestIcon|test-digest|32|32|$scale|SINGLE|"))
    raster.parentFile?.mkdirs()
    raster.writeBytes(byteArrayOf(1))
    assertNotNull(OneKeyNamedSVG.diskRender(activity.assets, "TestIcon", "rgba(0,0,0,1)", 32, 32, scale))
  }

  @After
  fun tearDown() {
    controller.pause().stop().destroy()
    ReactNativeFeatureFlags.dangerouslyReset()
    OneKeyNamedSVG.diskPngDecoderOverrideForTesting = null
    OneKeyNamedSVG.setManifestFallbackForTesting(null)
    OneKeyNamedSVG.clearMemoryCacheForTesting()
    OneKeyNamedSVG.resetStartupForTesting()
    OneKeyMainThreadDecode.resetForTesting()
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
    assertNotSame(outgoing, manager.createView(2, context, null, null, null))

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

  @Test
  fun explicitRecycleClearsAnAttachedDroppedIconAndAllowsImmediateRebind() {
    val manager = HybridOneKeyIconManager()
    val view = manager.createView(1, context, null, null, null) as ImageView
    attach(view)
    bind(view)
    val icon = hybridIcon(view)
    manager.onDropViewInstance(view)
    assertNotNull(view.drawable)

    icon.prepareForRecycle()

    assertTrue(view.isAttachedToWindow)
    assertNull(icon.iconName)
    assertNull(icon.resolvedColor)
    assertNull(view.drawable)
    assertNull(view.colorFilter)

    bind(view, color = "rgba(255,0,0,1)")
    assertNotNull(view.colorFilter)
  }

  @Test
  fun disposeClearsAnAttachedDroppedIconAndDisablesLaterUpdates() {
    val manager = HybridOneKeyIconManager()
    val view = manager.createView(1, context, null, null, null) as ImageView
    attach(view)
    bind(view)
    val icon = hybridIcon(view)
    manager.onDropViewInstance(view)
    assertNotNull(view.drawable)

    icon.dispose()

    assertTrue(view.isAttachedToWindow)
    assertNull(view.drawable)
    assertNull(view.colorFilter)

    icon.iconName = "TestIcon"
    icon.resolvedColor = "rgba(0,0,0,1)"
    view.layout(0, 0, 0, 0)
    view.layout(0, 0, 32, 32)
    assertNull(view.drawable)
    icon.afterUpdate()
    assertNull(view.drawable)
  }

  private fun bind(view: ImageView, color: String = "rgba(0,0,0,1)") {
    val icon = hybridIcon(view)
    icon.iconName = "TestIcon"
    icon.resolvedColor = color
    icon.afterUpdate()
    assertNotNull(view.drawable)
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
