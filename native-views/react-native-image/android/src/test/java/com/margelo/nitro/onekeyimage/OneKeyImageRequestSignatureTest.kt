package com.margelo.nitro.onekeyimage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class OneKeyImageRequestSignatureTest {
  @Test
  fun contentFitParticipatesInRequestIdentity() {
    val cover = signature(OneKeyImageContentFit.COVER)
    val contain = signature(OneKeyImageContentFit.CONTAIN)

    assertNotEquals(cover, contain)
  }

  @Test
  fun nullContentFitUsesCoverIdentity() {
    assertEquals(signature(OneKeyImageContentFit.COVER), signature(null))
  }

  @Test
  fun resizeWidthParticipatesInRequestIdentity() {
    assertNotEquals(
      signature(OneKeyImageContentFit.COVER, 32.0),
      signature(OneKeyImageContentFit.COVER, 64.0),
    )
  }

  @Test
  fun roundParticipatesInRequestIdentity() {
    assertNotEquals(
      signature(OneKeyImageContentFit.COVER, round = false),
      signature(OneKeyImageContentFit.COVER, round = true),
    )
  }

  private fun signature(
    contentFit: OneKeyImageContentFit?,
    resizeWidth: Double? = null,
    round: Boolean = false,
  ): String = oneKeyImageRequestSignature(
    rawUrl = "https://example.com/image.png",
    sourceHeadersJson = null,
    recyclingKey = null,
    cachePolicy = OneKeyImageCachePolicy.MEMORY_DISK,
    contentFit = contentFit,
    optimizeTos = true,
    overscan = 1.1,
    round = round,
    width = 200,
    height = 100,
    density = 2f,
    resizeWidth = resizeWidth,
  )
}
