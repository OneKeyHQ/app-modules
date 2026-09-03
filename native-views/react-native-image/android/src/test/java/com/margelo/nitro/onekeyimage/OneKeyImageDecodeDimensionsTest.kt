package com.margelo.nitro.onekeyimage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Modifier

class OneKeyImageDecodeDimensionsTest {
  @Test
  fun keepsRenderDimensionsWithinSixteenMiB() {
    val dimensions = OneKeyImageDecodeDimensions.forRender(1200, 800)

    assertEquals(1200, dimensions.width)
    assertEquals(800, dimensions.height)
    assertTrue(dimensions.estimatedBytes <= OneKeyImageDecodeDimensions.MAX_DECODE_BYTES)
  }

  @Test
  fun constrainsLargeRenderDimensionsAndPreservesAspectRatio() {
    val dimensions = OneKeyImageDecodeDimensions.forRender(4000, 2000)

    assertTrue(dimensions.estimatedBytes <= OneKeyImageDecodeDimensions.MAX_DECODE_BYTES)
    assertEquals(2.0, dimensions.width.toDouble() / dimensions.height, 0.01)
  }

  @Test
  fun constrainsExtremeAspectRatiosWithoutOverflow() {
    val dimensions = OneKeyImageDecodeDimensions.forRender(Int.MAX_VALUE, 1)

    assertTrue(dimensions.estimatedBytes <= OneKeyImageDecodeDimensions.MAX_DECODE_BYTES)
    assertEquals(1, dimensions.height)
  }

  @Test
  fun preloadUsesBothLogicalDimensionsAndPhysicalPixelRatio() {
    val dimensions = OneKeyImageDecodeDimensions.forPreload(40.0, 64.0, 2.5)

    assertEquals(100, dimensions.width)
    assertEquals(160, dimensions.height)
  }

  @Test
  fun unsizedPreloadStillHasAHardDecodeCap() {
    val dimensions = OneKeyImageDecodeDimensions.forPreload(null, null, 3.0)

    assertEquals(2048, dimensions.width)
    assertEquals(2048, dimensions.height)
    assertEquals(
      OneKeyImageDecodeDimensions.MAX_DECODE_BYTES,
      dimensions.estimatedBytes,
    )
  }

  @Test
  fun animatedDecoderUsesAPowerOfTwoSampleWithinTheCap() {
    val desired = OneKeyImageDecodeDimensions.forAnimatedDecoder(8000, 4000, 2048, 2048)

    requireNotNull(desired)
    assertEquals(2000, desired.width)
    assertEquals(1000, desired.height)
    assertTrue(desired.estimatedBytes <= OneKeyImageDecodeDimensions.MAX_DECODE_BYTES)
  }

  @Test
  fun staticDownsampleStrategyCapsExtremeAspectRatioArea() {
    val scale = OneKeyImageDecodeDimensions.safeStaticScaleFactor(8000, 1000, 2048, 2048)
    val decodedBytes = 8000.0 * scale * 1000.0 * scale * 4.0

    assertTrue(decodedBytes <= OneKeyImageDecodeDimensions.MAX_DECODE_BYTES)
  }

  @Test
  fun safeDownsampleStrategyIsSingletonForStableMemoryCacheIdentity() {
    val strategyClass = Class.forName(
      "com.margelo.nitro.onekeyimage.OneKeyImageSafeDownsampleStrategy",
      false,
      javaClass.classLoader,
    )

    assertTrue(
      strategyClass.declaredFields.any { field ->
        field.name == "INSTANCE" && Modifier.isStatic(field.modifiers)
      },
    )
  }
}
