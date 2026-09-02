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

  private fun signature(contentFit: OneKeyImageContentFit?): String = oneKeyImageRequestSignature(
    rawUrl = "https://example.com/image.png",
    sourceHeadersJson = null,
    recyclingKey = null,
    cachePolicy = OneKeyImageCachePolicy.MEMORY_DISK,
    contentFit = contentFit,
    optimizeTos = true,
    overscan = 1.1,
    width = 200,
    height = 100,
    density = 2f,
  )
}
