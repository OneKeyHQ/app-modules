package com.margelo.nitro.onekeyimage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OneKeyTosUrlTest {
  @Test
  fun selectsFirstBucketCoveringPhysicalOverscannedWidth() {
    assertEquals(256, OneKeyTosUrl.selectWidthBucket(100, 2f, 1.1))
    assertEquals(160, OneKeyTosUrl.selectWidthBucket(160, 1f, 1.0))
  }

  @Test
  fun capsDprAndSelectionAtLargestBucket() {
    assertEquals(480, OneKeyTosUrl.selectWidthBucket(100, 5f, 1.1))
    assertEquals(1280, OneKeyTosUrl.selectWidthBucket(2000, 3f, 1.1))
  }

  @Test
  fun normalizesNonFiniteDensityAndOverscan() {
    assertEquals(128, OneKeyTosUrl.selectWidthBucket(100, Float.NaN, 1.1))
    assertEquals(128, OneKeyTosUrl.selectWidthBucket(100, Float.POSITIVE_INFINITY, 1.1))
    assertEquals(200, OneKeyTosUrl.selectWidthBucket(100, 2f, Double.NaN))
    assertEquals(200, OneKeyTosUrl.selectWidthBucket(100, 2f, Double.POSITIVE_INFINITY))
    assertEquals(1280, OneKeyTosUrl.selectWidthBucket(100, 2f, Double.MAX_VALUE))
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
      assertTrue(result.contains("w_256"))
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
