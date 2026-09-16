package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NativeListSourceFallbackStateKeyTest {
  private val uri = "https://example.com/token.png"

  @Test
  fun keyIsADigestThatDoesNotRetainHeaderValues() {
    val key = nativeListSourceFallbackStateKey(uri, mapOf("Authorization" to "Bearer secret-token"))

    assertTrue(key.orEmpty().matches(Regex("[0-9a-f]{64}")))
    assertFalse(key.orEmpty().contains("secret-token"))
  }

  @Test
  fun keyIgnoresHeaderOrderAndNameCase() {
    assertEquals(
      nativeListSourceFallbackStateKey(uri, linkedMapOf("Authorization" to "Bearer a", "X-Trace" to "1")),
      nativeListSourceFallbackStateKey(uri, linkedMapOf("x-trace" to "1", "authorization" to "Bearer a")),
    )
  }

  @Test
  fun keyDistinguishesSourcesThatDifferOnlyByHeaders() {
    val keys = listOf(
      nativeListSourceFallbackStateKey(uri, emptyMap()),
      nativeListSourceFallbackStateKey(uri, mapOf("Authorization" to "Bearer a")),
      nativeListSourceFallbackStateKey(uri, mapOf("Authorization" to "Bearer b")),
    )

    assertEquals(keys.size, keys.toSet().size)
  }

  @Test
  fun blankUriHasNoKey() {
    assertNull(nativeListSourceFallbackStateKey("  ", mapOf("Authorization" to "Bearer a")))
  }
}
