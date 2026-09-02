package com.margelo.nitro.onekeyimage

import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.ByteArrayOutputStream

class OneKeyAnimationTimelineInspectorTest {
  @Test
  fun acceptsFrameAndDurationBoundariesForEveryAnimatedFormat() {
    validate(OneKeyAnimatedFormat.GIF, gif(1000, delayCentiseconds = 6))
    validate(OneKeyAnimatedFormat.WEBP, webp(1000, durationMs = 60))
    validate(OneKeyAnimatedFormat.APNG, apng(1000, delayNumerator = 6))
  }

  @Test
  fun rejectsFrameCountsAboveLimitForEveryAnimatedFormat() {
    assertUnsafe(OneKeyAnimatedFormat.GIF, gif(1001, delayCentiseconds = 1))
    assertUnsafe(OneKeyAnimatedFormat.WEBP, webp(1001, durationMs = 1))
    assertUnsafe(OneKeyAnimatedFormat.APNG, apng(1001, delayNumerator = 1))
  }

  @Test
  fun zeroDelayUsesDecoderMinimumForDurationLimit() {
    assertUnsafe(OneKeyAnimatedFormat.GIF, gif(601, delayCentiseconds = 0))
    assertUnsafe(
      OneKeyAnimatedFormat.GIF,
      gif(601, delayCentiseconds = 0, writeControlEveryFrame = false),
    )
    assertUnsafe(OneKeyAnimatedFormat.WEBP, webp(601, durationMs = 0))
    assertUnsafe(OneKeyAnimatedFormat.APNG, apng(601, delayNumerator = 0))
  }

  @Test
  fun rejectsDurationsAboveLimitForEveryAnimatedFormat() {
    assertUnsafe(OneKeyAnimatedFormat.GIF, gif(1000, delayCentiseconds = 7))
    assertUnsafe(OneKeyAnimatedFormat.WEBP, webp(1000, durationMs = 61))
    assertUnsafe(OneKeyAnimatedFormat.APNG, apng(1000, delayNumerator = 7))
  }

  @Test
  fun rejectsTruncatedDataForEveryAnimatedFormat() {
    val gif = gif(1, delayCentiseconds = 1)
    val webp = webp(1, durationMs = 1)
    val apng = apng(1, delayNumerator = 1)

    assertUnsafe(OneKeyAnimatedFormat.GIF, gif.copyOf(gif.size - 1))
    assertUnsafe(OneKeyAnimatedFormat.WEBP, webp.copyOf(webp.size - 1))
    assertUnsafe(OneKeyAnimatedFormat.APNG, apng.copyOf(apng.size - 1))
  }

  @Test
  fun rejectsInvalidCanvasesAndAcceptsMinimumAnimatedWebpCanvas() {
    assertUnsafe(OneKeyAnimatedFormat.GIF, gif(1, 1, width = 0, height = 1))
    assertUnsafe(OneKeyAnimatedFormat.APNG, apng(1, 1, width = 1, height = 0))
    validate(OneKeyAnimatedFormat.WEBP, webp(1, 1, width = 1, height = 1))
    assertUnsafe(OneKeyAnimatedFormat.WEBP, webp(1, 1, width = 8193, height = 1))
  }

  private fun validate(format: OneKeyAnimatedFormat, bytes: ByteArray) {
    OneKeyAnimationTimelineInspector.validate(format, bytes)
  }

  private fun assertUnsafe(format: OneKeyAnimatedFormat, bytes: ByteArray) {
    assertThrows(OneKeyImageSafetyException::class.java) {
      OneKeyAnimationTimelineInspector.validate(format, bytes)
    }
  }

  private fun gif(
    frameCount: Int,
    delayCentiseconds: Int,
    width: Int = 2,
    height: Int = 2,
    writeControlEveryFrame: Boolean = true,
  ): ByteArray = ByteArrayOutputStream().apply {
    write("GIF89a".toByteArray())
    writeU16Le(this, width)
    writeU16Le(this, height)
    write(byteArrayOf(0, 0, 0))
    repeat(frameCount) { index ->
      if (writeControlEveryFrame || index == 0) {
        write(byteArrayOf(0x21, 0xf9.toByte(), 4, 0))
        writeU16Le(this, delayCentiseconds)
        write(byteArrayOf(0, 0))
      }
      write(0x2c)
      writeU16Le(this, 0)
      writeU16Le(this, 0)
      writeU16Le(this, width)
      writeU16Le(this, height)
      write(0)
      write(byteArrayOf(2, 1, 0, 0))
    }
    write(0x3b)
  }.toByteArray()

  private fun webp(
    frameCount: Int,
    durationMs: Int,
    width: Int = 2,
    height: Int = 2,
  ): ByteArray {
    val chunks = ByteArrayOutputStream().apply {
      val vp8x = ByteArray(10).also { payload ->
        payload[0] = 0x02
        writeU24Le(payload, 4, width - 1)
        writeU24Le(payload, 7, height - 1)
      }
      writeWebpChunk(this, "VP8X", vp8x)
      writeWebpChunk(this, "ANIM", ByteArray(6))
      repeat(frameCount) {
        val frame = ByteArray(16).also { payload ->
          writeU24Le(payload, 6, width - 1)
          writeU24Le(payload, 9, height - 1)
          writeU24Le(payload, 12, durationMs)
        }
        writeWebpChunk(this, "ANMF", frame)
      }
    }.toByteArray()
    val body = ByteArrayOutputStream().apply {
      write("WEBP".toByteArray())
      write(chunks)
    }.toByteArray()
    return ByteArrayOutputStream().apply {
      write("RIFF".toByteArray())
      writeU32Le(this, body.size.toLong())
      write(body)
    }.toByteArray()
  }

  private fun apng(
    frameCount: Int,
    delayNumerator: Int,
    width: Int = 2,
    height: Int = 2,
  ): ByteArray = ByteArrayOutputStream().apply {
    write(byteArrayOf(0x89.toByte(), 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a))
    val ihdr = ByteArrayOutputStream().apply {
      writeU32Be(this, width.toLong())
      writeU32Be(this, height.toLong())
      write(byteArrayOf(8, 6, 0, 0, 0))
    }.toByteArray()
    writePngChunk(this, "IHDR", ihdr)
    val actl = ByteArrayOutputStream().apply {
      writeU32Be(this, frameCount.toLong())
      writeU32Be(this, 0)
    }.toByteArray()
    writePngChunk(this, "acTL", actl)
    repeat(frameCount) { index ->
      val fctl = ByteArrayOutputStream().apply {
        writeU32Be(this, (index * 2).toLong())
        writeU32Be(this, width.toLong())
        writeU32Be(this, height.toLong())
        writeU32Be(this, 0)
        writeU32Be(this, 0)
        writeU16Be(this, delayNumerator)
        writeU16Be(this, 100)
        write(byteArrayOf(0, 0))
      }.toByteArray()
      writePngChunk(this, "fcTL", fctl)
      if (index == 0) {
        writePngChunk(this, "IDAT", byteArrayOf())
      } else {
        val fdat = ByteArrayOutputStream().apply {
          writeU32Be(this, (index * 2 + 1).toLong())
        }.toByteArray()
        writePngChunk(this, "fdAT", fdat)
      }
    }
    writePngChunk(this, "IEND", byteArrayOf())
  }.toByteArray()

  private fun writeWebpChunk(output: ByteArrayOutputStream, type: String, payload: ByteArray) {
    output.write(type.toByteArray())
    writeU32Le(output, payload.size.toLong())
    output.write(payload)
    if (payload.size and 1 != 0) output.write(0)
  }

  private fun writePngChunk(output: ByteArrayOutputStream, type: String, payload: ByteArray) {
    writeU32Be(output, payload.size.toLong())
    output.write(type.toByteArray())
    output.write(payload)
    output.write(byteArrayOf(0, 0, 0, 0))
  }

  private fun writeU16Le(output: ByteArrayOutputStream, value: Int) {
    output.write(value and 0xff)
    output.write((value ushr 8) and 0xff)
  }

  private fun writeU16Be(output: ByteArrayOutputStream, value: Int) {
    output.write((value ushr 8) and 0xff)
    output.write(value and 0xff)
  }

  private fun writeU24Le(bytes: ByteArray, offset: Int, value: Int) {
    bytes[offset] = value.toByte()
    bytes[offset + 1] = (value ushr 8).toByte()
    bytes[offset + 2] = (value ushr 16).toByte()
  }

  private fun writeU32Le(output: ByteArrayOutputStream, value: Long) {
    repeat(4) { shift -> output.write(((value ushr (shift * 8)) and 0xffL).toInt()) }
  }

  private fun writeU32Be(output: ByteArrayOutputStream, value: Long) {
    for (shift in 3 downTo 0) output.write(((value ushr (shift * 8)) and 0xffL).toInt())
  }
}
