package com.margelo.nitro.onekeyimage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OneKeyTosUrlTest {
  @Test
  fun commonIconsShareRenditionsAfterApplyingScreenDensity() {
    for (size in listOf(32, 40, 48)) {
      for (density in listOf(1f, 2f)) {
        assertEquals(96, OneKeyTosUrl.selectWidthBucket(size, density, 1.1))
      }
      for (density in listOf(2.625f, 3f)) {
        assertEquals(160, OneKeyTosUrl.selectWidthBucket(size, density, 1.1))
      }
    }
    assertEquals(192, OneKeyTosUrl.selectWidthBucket(64, 2f, 1.1))
    assertEquals(320, OneKeyTosUrl.selectWidthBucket(64, 3f, 1.1))
  }

  @Test
  fun emitsTheCanonicalEncodedURLAndPreservesSourceQuery() {
    assertEquals(
      "https://common.onekey-asset.com/token.png?foo=a%20b&x-tos-process=image%2Fresize%2Cw_160#preview",
      OneKeyTosUrl.optimized(
        "https://common.onekey-asset.com/token.png?foo=a%20b#preview", 32, 3f, 1.1, false,
      ),
    )
  }

  @Test
  fun selectsLayoutTierBeforeDensityAndKeepsOverscanWithinIt() {
    for ((size, standard, highDensity) in listOf(
      Triple(48, 96, 160), Triple(49, 192, 320), Triple(96, 192, 320),
      Triple(97, 384, 640), Triple(192, 384, 640), Triple(193, 768, 1280),
      Triple(384, 768, 1280), Triple(385, 1280, 1280),
    )) {
      assertEquals(standard, OneKeyTosUrl.selectWidthBucket(size, 2f, 1.1))
      assertEquals(highDensity, OneKeyTosUrl.selectWidthBucket(size, 2.625f, 1.1))
    }
    assertEquals(160, OneKeyTosUrl.selectWidthBucket(48, 2f, 2.0))
    assertEquals(384, OneKeyTosUrl.selectWidthBucket(160, 1f, 1.0))
  }

  @Test
  fun capsDprAndSelectionAtLargestBucket() {
    assertEquals(640, OneKeyTosUrl.selectWidthBucket(100, 5f, 1.1))
    assertEquals(1280, OneKeyTosUrl.selectWidthBucket(2000, 3f, 1.1))
  }

  @Test
  fun normalizesNonFiniteDensityAndOverscan() {
    assertEquals(384, OneKeyTosUrl.selectWidthBucket(100, Float.NaN, 1.1))
    assertEquals(384, OneKeyTosUrl.selectWidthBucket(100, Float.POSITIVE_INFINITY, 1.1))
    assertEquals(384, OneKeyTosUrl.selectWidthBucket(100, 2f, Double.NaN))
    assertEquals(384, OneKeyTosUrl.selectWidthBucket(100, 2f, Double.POSITIVE_INFINITY))
    assertEquals(640, OneKeyTosUrl.selectWidthBucket(100, 2f, Double.MAX_VALUE))
  }

  @Test
  fun optimizesOnlyApprovedOneKeyHosts() {
    val allowedHosts = listOf(
      "app-assets.onekey.so",
      "uni.onekey-asset.com",
      "uni-test.onekey-asset.com",
      "common.onekey-asset.com",
      "asset.onekey-asset.com",
    )
    allowedHosts.forEach { host ->
      val result = OneKeyTosUrl.optimized(
        "https://$host/token.png",
        100,
        2f,
        1.1,
        false,
      )
      assertTrue(result.contains("x-tos-process="))
      assertTrue(result.contains("w_384"))
    }
    assertEquals(
      "https://web.onekey-asset.com/token.png",
      OneKeyTosUrl.optimized(
        "https://web.onekey-asset.com/token.png",
        100,
        2f,
        1.1,
        false,
      ),
    )
  }

  @Test
  fun skipsProtectedQueriesAndUnsupportedMedia() {
    val protectedKeys = listOf(
      "expires", "policy", "signature", "token", "auth_key", "accesskeyid",
      "ossaccesskeyid", "security-token", "x-amz-signature", "x-oss-signature",
      "x-tos-process",
    )
    protectedKeys.forEach { key ->
      val raw = "https://common.onekey-asset.com/token.png?$key=value"
      assertEquals(raw, OneKeyTosUrl.optimized(raw, 100, 2f, 1.1, false))
    }
    listOf("svg", "mp4", "webm", "m4v", "mov", "avi").forEach { extension ->
      val raw = "https://common.onekey-asset.com/media.$extension"
      assertEquals(raw, OneKeyTosUrl.optimized(raw, 100, 2f, 1.1, false))
    }
    val customIdentity = "https://common.onekey-asset.com/token.png"
    assertEquals(
      customIdentity,
      OneKeyTosUrl.optimized(customIdentity, 100, 2f, 1.1, true),
    )
  }
}
