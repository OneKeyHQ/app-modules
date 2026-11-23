package com.margelo.nitro.onekeyimage

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.request.target.Target
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlags
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlagsDefaults
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlagsForTests
import com.facebook.react.uimanager.DisplayMetricsHolder
import com.facebook.react.uimanager.ThemedReactContext
import com.margelo.nitro.R.id.associated_hybrid_view_tag
import com.margelo.nitro.onekeyimage.views.HybridOneKeyImageManager
import java.io.File
import java.nio.ByteBuffer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
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
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], shadows = [PngOnlyAvifDecoder::class])
class OneKeyImageLifecycleTest {
  private val controller = Robolectric.buildActivity(Activity::class.java)
  private lateinit var context: ThemedReactContext
  private lateinit var container: FrameLayout
  private lateinit var source: File

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
    source = File.createTempFile("image-lifecycle", ".png", activity.cacheDir)
    val bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.GREEN) }
    source.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    bitmap.recycle()
  }

  @After
  fun tearDown() {
    controller.pause().stop().destroy()
    source.delete()
    ReactNativeFeatureFlags.dangerouslyReset()
  }

  @Test
  fun fabricDropRetainsTransitionFrameAndNextBindingUsesFreshHost() {
    val manager = HybridOneKeyImageManager()
    val outgoing = manager.createView(1, context, null, null, null) as ImageView
    val image = hybridImage(outgoing)
    attach(outgoing)
    image.round = true
    loadImage(image)
    val drawable = outgoing.drawable
    val request = requireNotNull(target(image)?.request)

    container.startViewTransition(outgoing)
    container.removeView(outgoing)
    assertTrue(outgoing.isAttachedToWindow)
    manager.onDropViewInstance(outgoing)

    assertEquals(32, outgoing.width)
    assertEquals(32, outgoing.height)
    assertTrue(outgoing.clipToOutline)
    assertSame(drawable, outgoing.drawable)
    assertFalse(request.isCleared)

    val next = manager.createView(2, context, null, null, null) as ImageView
    assertNotSame(outgoing, next)
    val rebound = hybridImage(next)
    attach(next)
    rebound.round = false
    rebound.sourceUri = ""
    rebound.afterUpdate()
    assertFalse(next.clipToOutline)
    assertNull(next.drawable)

    container.endViewTransition(outgoing)
    assertFalse(outgoing.isAttachedToWindow)
    assertFalse(outgoing.clipToOutline)
    assertNull(outgoing.drawable)
    assertTrue(request.isCleared)
    assertNull(target(image))

    loadImage(rebound)
    container.removeView(next)
    manager.onDropViewInstance(next)
    assertNull(next.drawable)
  }

  @Test
  fun nativeReuseClearsRoundClipBeforeAnEmptySourceAndCanLoadAgain() {
    val image = HybridOneKeyImage(context).apply { usesApplicationRequestManager = true }
    val host = image.view as ImageView
    attach(host)
    image.round = true
    loadImage(image)
    val request = requireNotNull(target(image)?.request)
    assertTrue(host.clipToOutline)

    image.prepareForReuse()
    image.round = false
    image.sourceUri = ""
    image.afterUpdate()
    assertFalse(host.clipToOutline)
    assertNull(host.drawable)
    assertTrue(request.isCleared)

    loadImage(image)
    val nextRequest = requireNotNull(target(image)?.request)
    image.onDropView()
    assertTrue(host.isAttachedToWindow)
    assertNull(host.drawable)
    assertTrue(nextRequest.isCleared)
  }

  private fun attach(view: View) {
    container.addView(view, FrameLayout.LayoutParams(32, 32))
    view.layout(0, 0, 32, 32)
  }

  private fun loadImage(image: HybridOneKeyImage) {
    image.sourceUri = source.toURI().toString()
    image.afterUpdate()
    val host = image.view as ImageView
    val deadline = System.nanoTime() + 10_000_000_000L
    while (host.drawable == null && System.nanoTime() < deadline) {
      shadowOf(Looper.getMainLooper()).idle()
      Thread.sleep(10)
    }
    checkNotNull(host.drawable) { "The local PNG must finish decoding" }
  }

  private fun hybridImage(view: View): HybridOneKeyImage {
    val holder = view.getTag(associated_hybrid_view_tag)
    return holder.javaClass.getDeclaredField("hybridView").run {
      isAccessible = true
      get(holder) as HybridOneKeyImage
    }
  }

  private fun target(image: HybridOneKeyImage): Target<*>? =
    HybridOneKeyImage::class.java.getDeclaredField("currentTarget").run {
      isAccessible = true
      get(image) as? Target<*>
    }
}

// PNG fixtures do not require Glide's Android-only AVIF JNI library.
@Implements(className = "org.aomedia.avif.android.AvifDecoder", isInAndroidSdk = false)
class PngOnlyAvifDecoder {
  companion object {
    @JvmStatic
    @Implementation
    fun __staticInitializer__() = Unit

    @JvmStatic
    @Implementation
    fun isAvifImage(data: ByteBuffer): Boolean {
      check(data.limit() >= 4 && data.getInt(0) == 0x89504E47.toInt())
      return false
    }
  }
}
