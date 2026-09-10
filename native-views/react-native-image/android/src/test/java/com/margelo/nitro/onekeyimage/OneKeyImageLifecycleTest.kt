package com.margelo.nitro.onekeyimage

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Looper
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.request.target.Target
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.uimanager.ThemedReactContext
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], shadows = [PngOnlyAvifDecoder::class])
class OneKeyImageLifecycleTest {
  private val controller = Robolectric.buildActivity(Activity::class.java)
  private lateinit var container: FrameLayout
  private lateinit var image: HybridOneKeyImage
  private lateinit var source: File

  @Before
  fun setUp() {
    val activity = controller.setup().visible().get()
    container = FrameLayout(activity)
    activity.setContentView(container)
    val context = ThemedReactContext(BridgeReactContext(activity.application), activity, "test", 1)
    image = HybridOneKeyImage(context)
    source = File.createTempFile("image-lifecycle", ".png", activity.cacheDir)
    val bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.GREEN) }
    source.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    bitmap.recycle()
    container.addView(image.view, FrameLayout.LayoutParams(32, 32))
    image.view.layout(0, 0, 32, 32)
  }

  @After
  fun tearDown() {
    image.dispose()
    controller.pause().stop().destroy()
    source.delete()
  }

  private fun loadImage() {
    image.sourceUri = source.toURI().toString()
    val deadline = System.nanoTime() + 10_000_000_000L
    while ((image.view as ImageView).drawable == null && System.nanoTime() < deadline) {
      shadowOf(Looper.getMainLooper()).idle()
      Thread.sleep(10)
    }
    assertNotNull("The local image must finish decoding", (image.view as ImageView).drawable)
  }

  private fun target(): Target<*>? =
    HybridOneKeyImage::class.java.getDeclaredField("currentTarget").run {
      isAccessible = true
      get(image) as? Target<*>
    }

  @Test
  fun droppedImageRetainsItsGlideResourceUntilRemovalTransitionEnds() {
    loadImage()
    val drawable = (image.view as ImageView).drawable
    val request = target()!!.request!!
    container.startViewTransition(image.view)
    container.removeView(image.view)
    assertTrue(image.view.isAttachedToWindow)

    image.onDropView()

    assertSame(drawable, (image.view as ImageView).drawable)
    assertFalse("Glide must still own the bitmap while it is drawn", request.isCleared)
    container.endViewTransition(image.view)
    assertFalse(image.view.isAttachedToWindow)
    assertNull((image.view as ImageView).drawable)
    assertTrue(request.isCleared)
    assertNull(target())
  }

  @Test
  fun detachedDropAndExplicitNativeOwnerDisposalReleaseImmediately() {
    loadImage()
    val request = target()!!.request!!
    container.removeView(image.view)
    image.onDropView()
    assertTrue(request.isCleared)
    assertNull((image.view as ImageView).drawable)

    image.prepareForRecycle()
    image.usesApplicationRequestManager = true
    container.addView(image.view, FrameLayout.LayoutParams(32, 32))
    image.view.layout(0, 0, 32, 32)
    loadImage()
    val nativeRequest = target()!!.request!!
    image.onDropView()
    assertTrue(nativeRequest.isCleared)
    assertNull((image.view as ImageView).drawable)
  }

  @Test
  fun dropCancelsPendingLoadsAndRecyclingAllowsANewSource() {
    var callbacks = 0
    image.onLoadStart = { callbacks++ }
    image.onLoad = { _, _, _ -> callbacks++ }
    image.onDisplay = { callbacks++ }
    image.sourceUri = source.toURI().toString()
    image.onDropView()
    shadowOf(Looper.getMainLooper()).idle()
    assertEquals(0, callbacks)
    assertNull(target())

    image.prepareForRecycle()
    image.onLoad = { _, _, _ -> callbacks++ }
    loadImage()
    assertEquals(1, callbacks)
  }

  @Test
  fun sourceOrRecyclingKeyChangesStillClearThePreviousImageImmediately() {
    loadImage()
    image.recyclingKey = "another-account"
    assertNull((image.view as ImageView).drawable)
    loadImage()
    image.sourceUri = null
    assertNull((image.view as ImageView).drawable)
  }
}
