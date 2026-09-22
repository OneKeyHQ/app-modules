package com.margelo.nitro.onekeyimage

import org.junit.Assert.assertEquals
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
