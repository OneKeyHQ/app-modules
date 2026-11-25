package com.onekey.nativeoverlay

import android.graphics.Rect
import androidx.window.layout.FoldingFeature
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NativeOverlayToastBoundsTest {
  @Test
  fun separatingVerticalFoldUsesWiderPaneAndBreaksTiesByDirection() {
    val safe = Rect(0, 30, 800, 900)
    val centeredFold = fold(Rect(380, 0, 420, 900))
    assertEquals(Rect(0, 30, 380, 900), toastAvailableBounds(safe, listOf(centeredFold), 0, 0, false))
    assertEquals(Rect(420, 30, 800, 900), toastAvailableBounds(safe, listOf(centeredFold), 0, 0, true))

    // Stage is inset 50 window pixels from the left.
    assertEquals(Rect(270, 30, 800, 900), toastAvailableBounds(safe, listOf(fold(Rect(300, 0, 320, 900))), 50, 0, false))
  }

  @Test
  fun horizontalFoldUsesUpperPaneAndContinuousFoldKeepsWindow() {
    val safe = Rect(12, 30, 788, 880)
    val horizontal = fold(Rect(0, 430, 800, 460), FoldingFeature.Orientation.HORIZONTAL)
    assertEquals(Rect(12, 30, 788, 430), toastAvailableBounds(safe, listOf(horizontal), 0, 0, false))
    assertEquals(safe, toastAvailableBounds(safe, listOf(fold(Rect(400, 0, 400, 900), separates = false)), 0, 0, false))
  }

  @Test
  fun requestedAndDefaultWidthsFitPaneAndRespectMaximum() {
    assertEquals(576f, toastContentWidth(900f, 900f), 0f)
    assertEquals(576f, toastContentWidth(900f, null), 0f)
    assertEquals(288f, toastContentWidth(340f, 596f), 0f)
    assertEquals(276f, toastContentWidth(340f, null), 0f)
    assertEquals(200f, toastContentWidth(340f, 200f), 0f)
  }

  private fun fold(
    rect: Rect,
    foldOrientation: FoldingFeature.Orientation = FoldingFeature.Orientation.VERTICAL,
    separates: Boolean = true,
  ) = object : FoldingFeature {
    override val bounds = rect
    override val orientation = foldOrientation
    override val isSeparating = separates
    override val state = FoldingFeature.State.FLAT
    override val occlusionType = FoldingFeature.OcclusionType.FULL
  }
}
