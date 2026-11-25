package com.margelo.nitro.qrscanner

import android.app.Activity
import android.content.Context
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class QrScannerContainerTest {
  /**
   * Simulates a React Native host view that suppresses Android's native
   * requestLayout() traversal after Yoga performs the initial layout.
   */
  private class SwallowingHost(context: Context) : FrameLayout(context) {
    var suppressNativeRelayout = false
    var swallowedRequestCount = 0

    override fun requestLayout() {
      if (suppressNativeRelayout) {
        swallowedRequestCount++
        return
      }
      super.requestLayout()
    }
  }

  private fun mountInSwallowingHost(
    container: QrScannerContainer,
    width: Int,
    height: Int,
  ): SwallowingHost {
    val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
    val host = SwallowingHost(activity)
    host.addView(container, FrameLayout.LayoutParams(width, height))
    activity.setContentView(host)
    host.measure(
      View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY),
    )
    host.layout(0, 0, width, height)
    shadowOf(Looper.getMainLooper()).idle()
    host.suppressNativeRelayout = true
    return host
  }

  @Test
  fun reMeasuresDynamicallyReplacedChildAfterInitialLayout() {
    val activity = Robolectric.buildActivity(Activity::class.java).create().get()
    val container = QrScannerContainer(activity)
    val initialPreview = FrameLayout(activity)
    container.addView(
      initialPreview,
      FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT,
      ),
    )
    val host = mountInSwallowingHost(container, 400, 300)
    assertEquals(400, initialPreview.width)
    assertEquals(300, initialPreview.height)

    val replacementPreview = FrameLayout(activity)
    container.removeView(initialPreview)
    container.addView(
      replacementPreview,
      0,
      FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT,
      ),
    )
    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(host.swallowedRequestCount > 0)
    assertEquals(400, replacementPreview.width)
    assertEquals(300, replacementPreview.height)
  }

  @Test
  fun reMeasuresGrandchildAddedInsidePreviewChild() {
    val activity = Robolectric.buildActivity(Activity::class.java).create().get()
    val container = QrScannerContainer(activity)
    val previewHost = FrameLayout(activity)
    container.addView(
      previewHost,
      FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT,
      ),
    )
    val host = mountInSwallowingHost(container, 360, 360)

    // Simulates PreviewView adding its internal TextureView when CameraX binds.
    val textureChild = View(activity)
    previewHost.addView(
      textureChild,
      FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT,
      ),
    )
    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(host.swallowedRequestCount > 0)
    assertEquals(360, textureChild.width)
    assertEquals(360, textureChild.height)
  }
}
