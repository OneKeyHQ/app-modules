package com.margelo.nitro.onekeyimage

import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.signature.ObjectKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.MessageDigest

class OneKeyImageSafetyTest {
  @Test
  fun contentLengthAllowsBoundaryAndRejectsNextByte() {
    OneKeyImageSafety.requireEncodedLength(OneKeyImageSafety.MAX_ENCODED_BYTES)

    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyImageSafety.requireEncodedLength(OneKeyImageSafety.MAX_ENCODED_BYTES + 1L)
    }
  }

  @Test
  fun limitedStreamChecksActualReadBytes() {
    val stream = OneKeyLimitedInputStream(
      ByteArrayInputStream(ByteArray(6)),
      maximumBytes = 4L,
    )
    val firstChunk = ByteArray(4)

    assertEquals(4, stream.read(firstChunk))
    assertThrows(OneKeyImageSafetyException::class.java) {
      stream.read()
    }
  }

  @Test
  fun limitedStreamCountsSkippedBytes() {
    val stream = OneKeyLimitedInputStream(
      ByteArrayInputStream(ByteArray(6)),
      maximumBytes = 4L,
    )

    assertEquals(4L, stream.skip(4L))
    assertThrows(OneKeyImageSafetyException::class.java) {
      stream.skip(1L)
    }
  }

  @Test
  fun dataUriByteCountHandlesPaddingWithoutDecoding() {
    val padded = "data:image/png;base64,QUJDRA=="
    val unpadded = "data:image/png;base64,QUJDRA"

    assertEquals(4L, OneKeyImageSafety.dataUriDecodedByteCount(padded, padded.indexOf(',') + 1))
    assertEquals(4L, OneKeyImageSafety.dataUriDecodedByteCount(unpadded, unpadded.indexOf(',') + 1))
  }

  @Test
  fun dataUriRejectsEncodedTextThatCouldAllocateBeyondDecodedLimit() {
    val maximumEncodedCharacters =
      ((OneKeyImageSafety.MAX_DATA_URI_DECODED_BYTES + 2L) / 3L * 4L).toInt()
    val prefix = "data:image/png;base64,"
    val oversized = prefix + "A".repeat(maximumEncodedCharacters + 1)

    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyImageSafety.dataUriDecodedByteCount(oversized, prefix.length)
    }
  }

  @Test
  fun staticDimensionGuardChecksPixelsAndSides() {
    OneKeyImageSafety.requireStaticDimensions(10_000, 10_000)

    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyImageSafety.requireStaticDimensions(10_001, 10_000)
    }
    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyImageSafety.requireStaticDimensions(32_769, 1)
    }
  }

  @Test
  fun animatedGuardsBoundTheUnavoidableCanvasAndFrameCount() {
    OneKeyImageSafety.requireAnimatedDimensions(2048, 2048)
    OneKeyImageSafety.requireAnimationFrameCount(1000)

    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyImageSafety.requireAnimatedDimensions(2049, 2048)
    }
    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyImageSafety.requireAnimationFrameCount(1001)
    }
  }

  @Test
  fun avifBrandIsRejectedFromFtypBox() {
    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyEncodedImageInspector.inspect(
        ByteArrayInputStream(bmffBox("ftyp", ftypPayload("avif"))),
      )
    }

    val nonAvif = OneKeyEncodedImageInspector.inspect(
      ByteArrayInputStream(bmffBox("ftyp", ftypPayload("isom"))),
    )
    assertFalse(nonAvif.animated)
  }

  @Test
  fun avifCannotHideBehindLeadingOrExtendedBmffBoxes() {
    val leadingFree = bmffBox("free", byteArrayOf()) +
      bmffBox("ftyp", ftypPayload("mif1", "avif"))
    val leadingExtended = bmffBox("free", byteArrayOf(), extended = true) +
      bmffBox("ftyp", ftypPayload("avis"))
    val leadingUnknown = bmffBox("junk", byteArrayOf()) +
      bmffBox("ftyp", ftypPayload("avif"))

    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyEncodedImageInspector.inspect(ByteArrayInputStream(leadingFree))
    }
    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyEncodedImageInspector.inspect(ByteArrayInputStream(leadingExtended))
    }
    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyEncodedImageInspector.inspect(ByteArrayInputStream(leadingUnknown))
    }
  }

  @Test
  fun ambiguousOrOverlongBmffFailsClosed() {
    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyEncodedImageInspector.inspect(
        ByteArrayInputStream(bmffBox("free", byteArrayOf())),
      )
    }
    val overlong = ByteArrayOutputStream().apply {
      writeIntBe(this, 300_000)
      write("free".toByteArray())
    }.toByteArray()
    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyEncodedImageInspector.inspect(ByteArrayInputStream(overlong))
    }
    val unknownOverlong = ByteArrayOutputStream().apply {
      writeIntBe(this, 300_000)
      write("junk".toByteArray())
    }.toByteArray()
    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyEncodedImageInspector.inspect(ByteArrayInputStream(unknownOverlong))
    }
  }

  @Test
  fun xmlAndSvgHeadersAreNotMisclassifiedAsBmff() {
    val xml = OneKeyEncodedImageInspector.inspect(
      ByteArrayInputStream("<?xml version=\"1.0\"?><svg/>".toByteArray()),
    )
    val svg = OneKeyEncodedImageInspector.inspect(
      ByteArrayInputStream("<svg xmlns=\"http://www.w3.org/2000/svg\"/>".toByteArray()),
    )

    assertFalse(xml.animated)
    assertFalse(svg.animated)
  }

  @Test
  fun gifMetadataIsAnimatedAndUnsafeCanvasIsRejected() {
    val safe = gifHeader(2048, 2048)
    val metadata = OneKeyEncodedImageInspector.inspect(ByteArrayInputStream(safe))

    assertTrue(metadata.animated)
    assertEquals(2048, metadata.width)
    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyEncodedImageInspector.inspect(ByteArrayInputStream(gifHeader(3000, 2000)))
    }
  }

  @Test
  fun webpRequiresAnimationFlagAndChecksVp8xCanvas() {
    val animated = OneKeyEncodedImageInspector.inspect(
      ByteArrayInputStream(webpVp8x(2048, 2048, animated = true)),
    )
    val static = OneKeyEncodedImageInspector.inspect(
      ByteArrayInputStream(webpVp8x(2048, 2048, animated = false)),
    )

    assertTrue(animated.animated)
    assertFalse(static.animated)
    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyEncodedImageInspector.inspect(
        ByteArrayInputStream(webpVp8x(3000, 2000, animated = true)),
      )
    }
    val ambiguous = webpVp8x(32, 32, animated = false).also {
      "JUNK".toByteArray().copyInto(it, 12)
    }
    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyEncodedImageInspector.inspect(ByteArrayInputStream(ambiguous))
    }
  }

  @Test
  fun apngMetadataChecksCanvasAndFrameCountBeforeIdat() {
    val metadata = OneKeyEncodedImageInspector.inspect(
      ByteArrayInputStream(png(2048, 2048, frameCount = 12)),
    )

    assertTrue(metadata.animated)
    assertEquals(12, metadata.frameCount)
    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyEncodedImageInspector.inspect(
        ByteArrayInputStream(png(2048, 2048, frameCount = 1001)),
      )
    }
    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyEncodedImageInspector.inspect(
        ByteArrayInputStream(png(3000, 2000, frameCount = 2)),
      )
    }
  }

  @Test
  fun pngWithOverlongMetadataFailsClosed() {
    val image = ByteArrayOutputStream().apply {
      write(png(32, 32, frameCount = null).copyOfRange(0, 33))
      writeIntBe(this, 300_000)
      write("tEXt".toByteArray())
    }.toByteArray()

    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyEncodedImageInspector.inspect(ByteArrayInputStream(image))
    }
  }

  @Test
  fun safetyVersionParticipatesInMemoryAndDiskCacheIdentity() {
    assertEquals("onekey-image-safety-v2", OneKeyImageSafetyVersionedKey.CACHE_VERSION)
    val original = ObjectKey("custom-cache-key")
    val first = OneKeyImageSafetyVersionedKey(original)
    val same = OneKeyImageSafetyVersionedKey(ObjectKey("custom-cache-key"))
    val originalDigest = MessageDigest.getInstance("SHA-256").apply {
      original.updateDiskCacheKey(this)
    }.digest()
    val versionedDigest = MessageDigest.getInstance("SHA-256").apply {
      first.updateDiskCacheKey(this)
    }.digest()

    assertEquals(first, same)
    assertNotEquals(first, original)
    assertFalse(originalDigest.contentEquals(versionedDigest))
  }

  @Test
  fun safetyFailureIsFoundInsideGlideRootsAndOrdinaryFailureIsNot() {
    val safety = GlideException(
      "request failed",
      listOf(OneKeyImageSafetyException("too large")),
    )

    assertTrue(OneKeyImageSafety.isSafetyFailure(safety))
    assertFalse(OneKeyImageSafety.isSafetyFailure(IOException("corrupt image")))
  }

  private fun gifHeader(width: Int, height: Int): ByteArray = ByteArray(10).also { bytes ->
    "GIF89a".toByteArray().copyInto(bytes)
    bytes[6] = width.toByte()
    bytes[7] = (width ushr 8).toByte()
    bytes[8] = height.toByte()
    bytes[9] = (height ushr 8).toByte()
  }

  private fun webpVp8x(width: Int, height: Int, animated: Boolean): ByteArray =
    ByteArray(30).also { bytes ->
      "RIFF".toByteArray().copyInto(bytes, 0)
      "WEBP".toByteArray().copyInto(bytes, 8)
      "VP8X".toByteArray().copyInto(bytes, 12)
      bytes[16] = 10
      bytes[20] = if (animated) 0x02 else 0x00
      write24Le(bytes, 24, width - 1)
      write24Le(bytes, 27, height - 1)
    }

  private fun png(width: Int, height: Int, frameCount: Int?): ByteArray =
    ByteArrayOutputStream().apply {
      write(byteArrayOf(0x89.toByte(), 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a))
      val ihdr = ByteArrayOutputStream().apply {
        writeIntBe(this, width)
        writeIntBe(this, height)
        write(byteArrayOf(8, 6, 0, 0, 0))
      }.toByteArray()
      writeChunk(this, "IHDR", ihdr)
      if (frameCount != null) {
        val animation = ByteArrayOutputStream().apply {
          writeIntBe(this, frameCount)
          writeIntBe(this, 0)
        }.toByteArray()
        writeChunk(this, "acTL", animation)
      } else {
        writeChunk(this, "IDAT", byteArrayOf())
      }
    }.toByteArray()

  private fun writeChunk(output: ByteArrayOutputStream, type: String, data: ByteArray) {
    writeIntBe(output, data.size)
    output.write(type.toByteArray())
    output.write(data)
    output.write(byteArrayOf(0, 0, 0, 0))
  }

  private fun writeIntBe(output: ByteArrayOutputStream, value: Int) {
    output.write((value ushr 24) and 0xff)
    output.write((value ushr 16) and 0xff)
    output.write((value ushr 8) and 0xff)
    output.write(value and 0xff)
  }

  private fun write24Le(bytes: ByteArray, offset: Int, value: Int) {
    bytes[offset] = value.toByte()
    bytes[offset + 1] = (value ushr 8).toByte()
    bytes[offset + 2] = (value ushr 16).toByte()
  }

  private fun ftypPayload(majorBrand: String, vararg compatibleBrands: String): ByteArray =
    ByteArrayOutputStream().apply {
      write(majorBrand.toByteArray())
      write(byteArrayOf(0, 0, 0, 0))
      compatibleBrands.forEach { write(it.toByteArray()) }
    }.toByteArray()

  private fun bmffBox(type: String, payload: ByteArray, extended: Boolean = false): ByteArray =
    ByteArrayOutputStream().apply {
      if (extended) {
        writeIntBe(this, 1)
        write(type.toByteArray())
        writeIntBe(this, 0)
        writeIntBe(this, 16 + payload.size)
      } else {
        writeIntBe(this, 8 + payload.size)
        write(type.toByteArray())
      }
      write(payload)
    }.toByteArray()
}
