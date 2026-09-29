package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Test

class NativeListEndAlignmentTest {
  @Test fun tallFooterEndsAtViewportBottom() {
    val viewportHeight = 600
    val footerHeight = 1000
    val start = nativeListEndAlignmentOffset(viewportHeight, footerHeight)
    assertEquals(-400, start)
    assertEquals(viewportHeight, start + footerHeight)
  }

  @Test fun tallEmptySlotUsesTheSameEndAlignmentWithPadding() {
    val viewportTop = 80
    val viewportBottom = 680
    val emptyHeight = 900
    val start = viewportTop + nativeListEndAlignmentOffset(viewportBottom - viewportTop, emptyHeight)
    assertEquals(viewportBottom, start + emptyHeight)
  }

  @Test fun animatedCorrectionMovesDecoratedBottomToViewportBottom() {
    val decoratedBottom = 1240
    val viewportBottom = 680
    val correction = nativeListEndAlignmentOffset(viewportBottom, decoratedBottom)
    assertEquals(-560, correction)
    assertEquals(viewportBottom, decoratedBottom + correction)
  }

  @Test fun shortFooterRetainsBottomAlignment() {
    assertEquals(480, nativeListEndAlignmentOffset(600, 120))
  }
}
