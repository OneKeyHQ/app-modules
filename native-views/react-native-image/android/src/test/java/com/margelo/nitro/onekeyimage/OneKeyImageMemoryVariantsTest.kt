package com.margelo.nitro.onekeyimage

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class OneKeyImageMemoryVariantsTest {
  @Test
  fun choosesNearestLargerThenNearestSmallerVariant() {
    val target = variant(96)
    val candidates = OneKeyImageMemoryVariantRegistry.orderedCandidates(
      variants = listOf(variant(48), variant(72), variant(120), variant(144)),
      target = target,
    )

    assertEquals(listOf(variant(120), variant(72)), candidates)
  }

  @Test
  fun ignoresExactAndIncompatibleAspectRatios() {
    val target = variant(96)
    val candidates = OneKeyImageMemoryVariantRegistry.orderedCandidates(
      variants = listOf(
        target,
        OneKeyImageMemoryVariant("wide", 120, 60, round = false),
        variant(120),
      ),
      target = target,
    )

    assertEquals(listOf(variant(120)), candidates)
  }

  @Test
  fun roundTargetCanPreviewUntransformedResourceButSquareTargetCannotUseRoundResource() {
    val untransformed = variant(120, round = false)
    val transformed = variant(120, round = true)

    assertEquals(
      listOf(untransformed),
      OneKeyImageMemoryVariantRegistry.orderedCandidates(
        variants = listOf(untransformed),
        target = variant(96, round = true),
      ),
    )
    assertEquals(
      emptyList<OneKeyImageMemoryVariant>(),
      OneKeyImageMemoryVariantRegistry.orderedCandidates(
        variants = listOf(transformed),
        target = variant(96, round = false),
      ),
    )
  }

  @Test
  fun crossSizeCenterPreviewUsesTemporaryScaling() {
    assertTrue(
      shouldScaleCenterMemoryPreview(
        contentFit = OneKeyImageContentFit.CENTER,
        candidate = variant(48),
        target = variant(96),
      ),
    )
    assertTrue(
      shouldScaleCenterMemoryPreview(
        contentFit = OneKeyImageContentFit.CENTER,
        candidate = variant(144),
        target = variant(96),
      ),
    )
    assertFalse(
      shouldScaleCenterMemoryPreview(
        contentFit = OneKeyImageContentFit.CENTER,
        candidate = variant(96),
        target = variant(96),
      ),
    )
    assertFalse(
      shouldScaleCenterMemoryPreview(
        contentFit = OneKeyImageContentFit.COVER,
        candidate = variant(48),
        target = variant(96),
      ),
    )
  }

  @Test
  fun missingPreviewDrawableAllowsSameFamilyProbeRetry() {
    assertTrue(
      shouldProbeMemoryPreview(
        hasTarget = true,
        sameFamily = true,
        hasDrawable = false,
      ),
    )
    assertFalse(
      shouldProbeMemoryPreview(
        hasTarget = true,
        sameFamily = true,
        hasDrawable = true,
      ),
    )
  }

  @Test
  fun replacingMemoryPreviewSkipsWholeViewFade() {
    assertFalse(
      shouldAnimateLoadedImageTransition(
        cacheType = OneKeyImageCacheType.DISK,
        fadeDelayElapsed = true,
        animationsEnabled = true,
        replacingMemoryPreview = true,
      ),
    )
    assertTrue(
      shouldAnimateLoadedImageTransition(
        cacheType = OneKeyImageCacheType.DISK,
        fadeDelayElapsed = true,
        animationsEnabled = true,
        replacingMemoryPreview = false,
      ),
    )
  }

  @Test
  fun memoryProbeKeepsMemoryEnabledAndDisablesDiskReads() {
    val options = memoryProbeRequestOptions(
      RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC),
    )

    assertSame(DiskCacheStrategy.NONE, options.diskCacheStrategy)
    assertTrue(options.onlyRetrieveFromCache)
    assertTrue(options.isMemoryCacheable)
  }

  private fun variant(
    size: Int,
    round: Boolean = false,
  ) = OneKeyImageMemoryVariant(
    requestUrl = "https://example.com/image-$size.png",
    width = size,
    height = size,
    round = round,
  )
}
