package com.margelo.nitro.onekeyimage

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.util.zip.CRC32
import java.util.zip.DeflaterOutputStream
import java.util.concurrent.CancellationException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class OneKeyBlockieAvatarTest {
  @Test
  fun percentDecodePreservesJavaScriptNormalizedUtf16WithoutRecasing() {
    assertEquals("i\u0307中🙂/#+", OneKeyBlockieAvatar.decodeSeed(
      "onekey-avatar://blockie/v1/i%CC%87%E4%B8%AD%F0%9F%99%82%2F%23%2B",
    ))
    assertEquals("İ", OneKeyBlockieAvatar.decodeSeed("onekey-avatar://blockie/v1/%C4%B0"))
    assertEquals("\u0000", OneKeyBlockieAvatar.decodeSeed("onekey-avatar://blockie/v1/%00"))
  }

  @Test
  fun invalidProtocolPercentEncodingAndUtf8AreRejected() {
    listOf(
      "onekey-avatar://blockie/v2/seed", "onekey-avatar://blockie/v1/",
      "onekey-avatar://blockie/v1/%", "onekey-avatar://blockie/v1/%GG",
      "onekey-avatar://blockie/v1/%C0%AF", "onekey-avatar://blockie/v1/%ED%A0%80",
      "onekey-avatar://blockie/v1/seed?query", "onekey-avatar://blockie/v1/a/b",
      "onekey-avatar://blockie/v1/a+b", "onekey-avatar://blockie/v1/中",
    ).forEach { uri ->
      try {
        OneKeyBlockieAvatar.decodeSeed(uri)
        fail("Invalid avatar URI was accepted")
      } catch (_: Exception) { }
    }
  }

  @Test
  fun generationIsDeterministicAndEmits128pxIndexedPng() {
    val uri = "onekey-avatar://blockie/v1/0x1234"
    val first = OneKeyBlockieAvatar.png(uri)
    assertArrayEquals(first, OneKeyBlockieAvatar.png(uri))
    assertArrayEquals(byteArrayOf(-119, 80, 78, 71, 13, 10, 26, 10), first.copyOfRange(0, 8))
    assertArrayEquals(byteArrayOf(0, 0, 0, -128, 0, 0, 0, -128), first.copyOfRange(16, 24))
    assertTrue(first.size < 1024)
  }

  @Test(expected = CancellationException::class)
  fun cancellationStopsBeforeGeneration() {
    OneKeyBlockieAvatar.png("onekey-avatar://blockie/v1/seed") { true }
  }

  @Test(expected = CancellationException::class)
  fun cancellationAlsoStopsLargeSeedDecoding() {
    val checks = AtomicInteger()
    OneKeyBlockieAvatar.png("onekey-avatar://blockie/v1/" + "a".repeat(8192)) {
      checks.incrementAndGet() > 3
    }
  }

  private fun replaceChunk(png: ByteArray, type: String, data: ByteArray): ByteArray {
    val input = ByteBuffer.wrap(png)
    input.position(8)
    while (input.hasRemaining()) {
      val start = input.position()
      val length = input.int
      val chunkType = ByteArray(4).also(input::get).toString(Charsets.US_ASCII)
      val end = input.position() + length + 4
      if (chunkType == type) {
        val chunk = ByteArrayOutputStream()
        DataOutputStream(chunk).use {
          val name = type.toByteArray(Charsets.US_ASCII)
          it.writeInt(data.size)
          it.write(name)
          it.write(data)
          it.writeInt(CRC32().apply { update(name); update(data) }.value.toInt())
        }
        return png.copyOfRange(0, start) + chunk.toByteArray() + png.copyOfRange(end, png.size)
      }
      input.position(end)
    }
    throw IllegalArgumentException("Test PNG chunk missing")
  }

  private fun compressedPixels(size: Int, invalidFilter: Boolean = false): ByteArray {
    val result = ByteArrayOutputStream()
    DeflaterOutputStream(result).use { output ->
      repeat(size) { index -> output.write(if (invalidFilter && index == 0) 1 else 0) }
    }
    return result.toByteArray()
  }

  @Test
  fun completeFixedFormatPngPassesIntegrityValidation() {
    listOf("seed", "0x1234", "%E4%B8%AD%F0%9F%99%82").forEach {
      assertTrue(OneKeyBlockieAvatar.isValidPng(OneKeyBlockieAvatar.png(OneKeyBlockieAvatar.URI_PREFIX + it)))
    }
  }

  @Test
  fun corruptedOrTruncatedChunksAndTrailingBytesAreRejected() {
    val png = OneKeyBlockieAvatar.png(OneKeyBlockieAvatar.URI_PREFIX + "seed")
    assertFalse(OneKeyBlockieAvatar.isValidPng(png.copyOf(png.size - 1)))
    assertFalse(OneKeyBlockieAvatar.isValidPng(png + byteArrayOf(0)))
    assertFalse(OneKeyBlockieAvatar.isValidPng(png.copyOf().also { it[45] = (it[45].toInt() xor 1).toByte() }))
    assertFalse(OneKeyBlockieAvatar.isValidPng(png.copyOf().also { ByteBuffer.wrap(it).putInt(8, Int.MAX_VALUE) }))
  }

  @Test
  fun validCrcCannotHideWrongDimensionsOrMalformedCompressedData() {
    val png = OneKeyBlockieAvatar.png(OneKeyBlockieAvatar.URI_PREFIX + "seed")
    val header = png.copyOfRange(16, 29).also { it[3] = 96 }
    assertFalse(OneKeyBlockieAvatar.isValidPng(replaceChunk(png, "IHDR", header)))
    assertFalse(OneKeyBlockieAvatar.isValidPng(replaceChunk(png, "IDAT", byteArrayOf(1, 2, 3))))
    assertFalse(OneKeyBlockieAvatar.isValidPng(replaceChunk(png, "IDAT", compressedPixels(128 * 33 - 1))))
    assertFalse(OneKeyBlockieAvatar.isValidPng(replaceChunk(png, "IDAT", compressedPixels(128 * 33, true))))
  }

  @Test
  fun decompressionIsBoundedEvenWhenEveryChunkCrcIsValid() {
    val png = OneKeyBlockieAvatar.png(OneKeyBlockieAvatar.URI_PREFIX + "seed")
    val bomb = replaceChunk(png, "IDAT", compressedPixels(1024 * 1024))
    assertTrue(bomb.size < OneKeyBlockieAvatar.MAX_PNG_BYTES)
    assertFalse(OneKeyBlockieAvatar.isValidPng(bomb))
  }

  @Test(expected = CancellationException::class)
  fun cancellationStopsCachedPngValidation() {
    OneKeyBlockieAvatar.isValidPng(OneKeyBlockieAvatar.png(OneKeyBlockieAvatar.URI_PREFIX + "seed")) { true }
  }

  @Test
  fun overlappingSizeRequestsShareOneGenerationAndDoNotRetainCompletedImages() {
    val generated = AtomicInteger()
    val started = CountDownLatch(1)
    val complete = CountDownLatch(1)
    val requests = OneKeyAvatarInFlight { _, _ ->
      generated.incrementAndGet()
      started.countDown()
      check(complete.await(3, TimeUnit.SECONDS))
      byteArrayOf(1, 2, 3)
    }
    val leases = List(8) { requests.acquire("uri") }
    val executor = Executors.newFixedThreadPool(8)
    try {
      val values = leases.map { lease -> executor.submit<ByteArray> { lease.bytes() } }
      assertTrue(started.await(3, TimeUnit.SECONDS))
      complete.countDown()
      values.forEach { assertArrayEquals(byteArrayOf(1, 2, 3), it.get(3, TimeUnit.SECONDS)) }
      assertEquals(1, generated.get())
      leases.forEach { it.release() }
      val fresh = requests.acquire("uri")
      assertArrayEquals(byteArrayOf(1, 2, 3), fresh.bytes())
      fresh.release()
      assertEquals(2, generated.get())
    } finally {
      complete.countDown()
      leases.forEach { it.release() }
      executor.shutdownNow()
    }
  }

  @Test
  fun cancellingOneConsumerKeepsTheOtherConsumerAlive() {
    val requests = OneKeyAvatarInFlight { _, cancelled ->
      assertFalse(cancelled())
      byteArrayOf(7)
    }
    val cancelled = requests.acquire("uri")
    val active = requests.acquire("uri")
    cancelled.release()
    cancelled.release()
    assertArrayEquals(byteArrayOf(7), active.bytes())
    active.release()
  }

  @Test
  fun cancellingEveryConsumerStopsWorkAndAllowsAFreshRequest() {
    val started = CountDownLatch(1)
    val stopped = CountDownLatch(1)
    val generated = AtomicInteger()
    val requests = OneKeyAvatarInFlight { _, cancelled ->
      if (generated.incrementAndGet() == 1) {
        started.countDown()
        while (!cancelled()) Thread.yield()
        stopped.countDown()
        throw CancellationException("Cancelled")
      }
      byteArrayOf(9)
    }
    val lease = requests.acquire("uri")
    val executor = Executors.newSingleThreadExecutor()
    try {
      executor.submit { try { lease.bytes() } catch (_: CancellationException) { } }
      assertTrue(started.await(3, TimeUnit.SECONDS))
      lease.release()
      assertTrue(stopped.await(3, TimeUnit.SECONDS))
      val fresh = requests.acquire("uri")
      assertArrayEquals(byteArrayOf(9), fresh.bytes())
      fresh.release()
    } finally {
      lease.release()
      executor.shutdownNow()
    }
  }
}
