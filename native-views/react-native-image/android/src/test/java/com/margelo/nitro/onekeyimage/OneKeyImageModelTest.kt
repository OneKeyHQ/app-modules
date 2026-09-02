package com.margelo.nitro.onekeyimage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import com.bumptech.glide.load.Key
import java.security.MessageDigest

class OneKeyImageModelTest {
  @Test
  fun dataUriUsesDedicatedModelWithoutBuiltInStringFallback() {
    val uri = "data:image/png;base64,AA=="
    val model = OneKeyImageModel.build(uri, null)

    assertTrue(model is OneKeyImageDataUriModel)
    assertFalse(model is String)
    assertEquals(uri, (model as OneKeyImageDataUriModel).dataUri)
  }

  @Test
  fun headerDigestIsStableAndDoesNotContainPlaintext() {
    val first = OneKeyImageModel.remoteHeadersDigest(
      linkedMapOf("X-Mode" to "private-value", "Authorization" to "token"),
    )
    val same = OneKeyImageModel.remoteHeadersDigest(
      linkedMapOf("authorization" to "token", "x-mode" to "private-value"),
    )

    assertEquals(first, same)
    assertFalse(first!!.contains("token"))
    assertFalse(first.contains("private-value"))
    assertEquals(64, first.length)
    assertNull(OneKeyImageModel.remoteHeadersDigest(emptyMap()))
  }

  @Test
  fun remoteSourceKeyIncludesUrlAndHashedHeadersInMemoryAndDiskIdentity() {
    val url = "https://example.com/image.png"
    val otherUrl = "https://example.com/other.png"
    val firstHeaders = OneKeyImageModel.remoteHeadersDigest(mapOf("Authorization" to "first"))
    val secondHeaders = OneKeyImageModel.remoteHeadersDigest(mapOf("Authorization" to "second"))
    val first = OneKeyImageRemoteSourceKey(url, firstHeaders)
    val same = OneKeyImageRemoteSourceKey("https://example.com/image.png", firstHeaders)
    val differentHeaders = OneKeyImageRemoteSourceKey(url, secondHeaders)
    val differentUrl = OneKeyImageRemoteSourceKey(otherUrl, firstHeaders)
    val withoutHeaders = OneKeyImageRemoteSourceKey(url, null)

    assertEquals(first, same)
    assertEquals(first.hashCode(), same.hashCode())
    assertTrue(diskDigest(first).contentEquals(diskDigest(same)))
    assertNotEquals(first, differentHeaders)
    assertFalse(diskDigest(first).contentEquals(diskDigest(differentHeaders)))
    assertNotEquals(first, differentUrl)
    assertFalse(diskDigest(first).contentEquals(diskDigest(differentUrl)))
    assertNotEquals(first, withoutHeaders)
    assertFalse(diskDigest(first).contentEquals(diskDigest(withoutHeaders)))
  }

  private fun diskDigest(key: Key): ByteArray = MessageDigest.getInstance("SHA-256").apply {
    key.updateDiskCacheKey(this)
  }.digest()
}
