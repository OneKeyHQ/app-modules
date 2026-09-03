package com.margelo.nitro.onekeyimage

import android.content.Context
import android.net.Uri
import com.bumptech.glide.Priority
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer
import java.io.File
import java.io.FilterInputStream
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.ArrayDeque
import java.util.Collections
import java.util.IdentityHashMap
import java.security.MessageDigest

internal class OneKeyImageSafetyException(message: String) : IOException(message)

internal object OneKeyImageSafety {
  const val MAX_ENCODED_BYTES = 32L * 1024L * 1024L
  const val MAX_DATA_URI_DECODED_BYTES = 8L * 1024L * 1024L
  const val MAX_ANIMATED_ENCODED_BYTES = 16L * 1024L * 1024L
  const val MAX_STATIC_PIXELS = 100_000_000L
  const val MAX_STATIC_SIDE = 32_768
  const val MAX_ANIMATED_CANVAS_PIXELS = 4_194_304L
  const val MAX_ANIMATED_SIDE = 8_192
  const val MAX_ANIMATION_FRAMES = 1_000
  const val MAX_ANIMATION_DURATION_MS = 60_000L

  fun requireEncodedLength(length: Long, maximum: Long = MAX_ENCODED_BYTES) {
    if (length > maximum) {
      throw OneKeyImageSafetyException(
        "Encoded image exceeds the ${maximum / (1024L * 1024L)} MiB limit",
      )
    }
  }

  fun requireStaticDimensions(width: Int, height: Int) =
    requireStaticDimensions(width.toLong(), height.toLong())

  fun requireStaticDimensions(width: Long, height: Long) {
    requireDimensions(
      width,
      height,
      MAX_STATIC_SIDE,
      MAX_STATIC_PIXELS,
      "Static image",
    )
  }

  fun requireAnimatedDimensions(width: Int, height: Int) =
    requireAnimatedDimensions(width.toLong(), height.toLong())

  fun requireAnimatedDimensions(width: Long, height: Long) {
    if (width <= 0L || height <= 0L) {
      throw OneKeyImageSafetyException("Animated image dimensions are invalid")
    }
    requireDimensions(
      width,
      height,
      MAX_ANIMATED_SIDE,
      MAX_ANIMATED_CANVAS_PIXELS,
      "Animated image",
    )
  }

  fun requireAnimationFrameCount(frameCount: Long) {
    if (frameCount > MAX_ANIMATION_FRAMES) {
      throw OneKeyImageSafetyException(
        "Animated image exceeds the $MAX_ANIMATION_FRAMES frame limit",
      )
    }
  }

  /** Returns the decoded byte count without allocating the decoded payload. */
  fun dataUriDecodedByteCount(dataUri: String, payloadStart: Int): Long {
    require(payloadStart in 0..dataUri.length) { "Invalid data URI payload" }
    val rawLength = dataUri.length - payloadStart
    val maximumEncodedCharacters = ((MAX_DATA_URI_DECODED_BYTES + 2L) / 3L) * 4L
    if (rawLength.toLong() > maximumEncodedCharacters) {
      throw OneKeyImageSafetyException("Base64 image data exceeds the 8 MiB decoded limit")
    }

    var characterCount = 0L
    var secondLast = '\u0000'
    var last = '\u0000'
    for (index in payloadStart until dataUri.length) {
      val character = dataUri[index]
      if (character == ' ' || character == '\t' || character == '\r' || character == '\n') {
        continue
      }
      characterCount += 1L
      secondLast = last
      last = character
    }

    val completeGroups = characterCount / 4L
    val remainder = (characterCount % 4L).toInt()
    var decodedBytes = completeGroups * 3L
    decodedBytes += when (remainder) {
      0 -> 0L
      2 -> 1L
      3 -> 2L
      else -> 3L // Invalid Base64 will fail in the decoder; never under-estimate it here.
    }
    if (remainder == 0) {
      if (last == '=') decodedBytes -= 1L
      if (secondLast == '=') decodedBytes -= 1L
    }
    return decodedBytes.coerceAtLeast(0L)
  }

  fun isSafetyFailure(error: Throwable?): Boolean {
    if (error == null) return false
    val seen = Collections.newSetFromMap(IdentityHashMap<Throwable, Boolean>())
    val pending = ArrayDeque<Throwable>()
    pending.add(error)
    while (pending.isNotEmpty()) {
      val current = pending.removeFirst()
      if (!seen.add(current)) continue
      if (current is OneKeyImageSafetyException) return true
      current.cause?.let(pending::addLast)
      if (current is GlideException) {
        current.rootCauses.forEach(pending::addLast)
      }
    }
    return false
  }

  private fun requireDimensions(
    width: Long,
    height: Long,
    maximumSide: Int,
    maximumPixels: Long,
    label: String,
  ) {
    if (width <= 0 || height <= 0) return
    if (
      width > maximumSide ||
      height > maximumSide ||
      width * height > maximumPixels
    ) {
      throw OneKeyImageSafetyException("$label dimensions exceed the safety limit")
    }
  }

}

internal data class OneKeyEncodedImageMetadata(
  val animated: Boolean,
  val width: Int = 0,
  val height: Int = 0,
  val frameCount: Int? = null,
  val animatedFormat: OneKeyAnimatedFormat? = null,
)

internal enum class OneKeyAnimatedFormat {
  GIF,
  WEBP,
  APNG,
}

internal object OneKeyEncodedImageInspector {
  private const val MAX_METADATA_BYTES = 256 * 1024
  private val BMFF_LEADING_BOXES = setOf(
    "ftyp", "free", "skip", "wide", "uuid", "styp", "sidx", "meta", "moov", "mdat",
  )
  private val PNG_SIGNATURE = byteArrayOf(
    0x89.toByte(), 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a,
  )

  fun inspect(input: InputStream): OneKeyEncodedImageMetadata {
    require(input.markSupported()) { "Image metadata input must support mark/reset" }
    input.mark(MAX_METADATA_BYTES + 1)
    return try {
      val header = ByteArray(64)
      val headerLength = readUpTo(input, header)
      input.reset()
      when {
        looksLikeBmff(header, headerLength) -> inspectBmff(input)
        isGif(header, headerLength) -> inspectGif(header, headerLength)
        isWebP(header, headerLength) -> inspectWebP(header, headerLength)
        startsWith(header, headerLength, PNG_SIGNATURE) -> inspectPng(input)
        else -> OneKeyEncodedImageMetadata(animated = false)
      }
    } finally {
      input.reset()
    }
  }

  private fun inspectBmff(input: InputStream): OneKeyEncodedImageMetadata {
    val reader = BoundedMetadataReader(input, MAX_METADATA_BYTES)
    while (true) {
      val size32 = reader.readUnsignedIntBe()
      val boxType = reader.readAscii4()
      var headerSize = 8L
      val boxSize = when (size32) {
        0L -> throw OneKeyImageSafetyException("Unbounded BMFF metadata is rejected")
        1L -> {
          headerSize = 16L
          reader.readBoundedUnsignedLongBe()
        }
        else -> size32
      }
      if (boxSize < headerSize) {
        throw OneKeyImageSafetyException("BMFF box size is invalid")
      }
      val payloadLength = boxSize - headerSize
      if (payloadLength > reader.remainingLimit()) {
        throw OneKeyImageSafetyException("BMFF metadata exceeds the scan limit")
      }
      if (boxType == "ftyp") {
        if (payloadLength < 8L || (payloadLength - 8L) % 4L != 0L) {
          throw OneKeyImageSafetyException("BMFF ftyp metadata is invalid")
        }
        val payload = reader.readExact(payloadLength.toInt())
        if (containsAvifBrand(payload)) {
          throw OneKeyImageSafetyException(
            "AVIF is rejected because Glide 5.0.5 cannot enforce the target decode size",
          )
        }
        return OneKeyEncodedImageMetadata(animated = false)
      }
      reader.skipExact(payloadLength)
    }
  }

  private fun containsAvifBrand(ftypPayload: ByteArray): Boolean {
    if (ascii(ftypPayload, 0) == "avif" || ascii(ftypPayload, 0) == "avis") return true
    var offset = 8
    while (offset + 4 <= ftypPayload.size) {
      val brand = ascii(ftypPayload, offset)
      if (brand == "avif" || brand == "avis") return true
      offset += 4
    }
    return false
  }

  private fun looksLikeBmff(bytes: ByteArray, length: Int): Boolean {
    if (length < 8 || !isAsciiBoxType(bytes, 4)) return false
    val boxType = ascii(bytes, 4)
    val size = unsignedIntBe(bytes, 0)
    return boxType in BMFF_LEADING_BOXES ||
      size == 0L ||
      size == 1L ||
      size in 8L..OneKeyImageSafety.MAX_ENCODED_BYTES
  }

  private fun isAsciiBoxType(bytes: ByteArray, offset: Int): Boolean =
    (offset until offset + 4).all { (bytes[it].toInt() and 0xff) in 0x20..0x7e }

  private fun inspectGif(header: ByteArray, length: Int): OneKeyEncodedImageMetadata {
    if (length < 10) throw OneKeyImageSafetyException("GIF metadata is incomplete")
    val width = unsignedShortLe(header, 6)
    val height = unsignedShortLe(header, 8)
    OneKeyImageSafety.requireAnimatedDimensions(width, height)
    return OneKeyEncodedImageMetadata(
      animated = true,
      width = width,
      height = height,
      animatedFormat = OneKeyAnimatedFormat.GIF,
    )
  }

  private fun inspectWebP(header: ByteArray, length: Int): OneKeyEncodedImageMetadata {
    if (length < 16) throw OneKeyImageSafetyException("WebP metadata is incomplete")
    val chunkType = ascii(header, 12)
    if (chunkType != "VP8X") {
      if (chunkType != "VP8 " && chunkType != "VP8L") {
        throw OneKeyImageSafetyException("WebP metadata is ambiguous")
      }
      return OneKeyEncodedImageMetadata(animated = false)
    }
    if (length < 30 || unsignedIntLe(header, 16) < 10L) {
      throw OneKeyImageSafetyException("WebP VP8X metadata is incomplete")
    }
    val animated = header[20].toInt() and 0x02 != 0
    if (!animated) return OneKeyEncodedImageMetadata(animated = false)
    val width = unsigned24Le(header, 24) + 1
    val height = unsigned24Le(header, 27) + 1
    OneKeyImageSafety.requireAnimatedDimensions(width, height)
    return OneKeyEncodedImageMetadata(
      animated = true,
      width = width,
      height = height,
      animatedFormat = OneKeyAnimatedFormat.WEBP,
    )
  }

  private fun inspectPng(input: InputStream): OneKeyEncodedImageMetadata {
    val reader = BoundedMetadataReader(input, MAX_METADATA_BYTES)
    reader.readExact(8) // signature
    val ihdrLength = reader.readUnsignedIntBe()
    val ihdrType = reader.readAscii4()
    if (ihdrType != "IHDR" || ihdrLength != 13L) {
      throw OneKeyImageSafetyException("PNG IHDR metadata is invalid")
    }
    val width = reader.readUnsignedIntBe()
    val height = reader.readUnsignedIntBe()
    reader.skipExact(5L + 4L) // remaining IHDR payload and CRC

    while (true) {
      val chunkLength = reader.readUnsignedIntBe()
      val chunkType = reader.readAscii4()
      when (chunkType) {
        "acTL" -> {
          if (chunkLength != 8L) {
            throw OneKeyImageSafetyException("APNG acTL metadata is invalid")
          }
          val frameCount = reader.readUnsignedIntBe()
          reader.skipExact(4L + 4L) // play count and CRC
          if (frameCount == 0L) {
            throw OneKeyImageSafetyException("APNG frame count exceeds the safety limit")
          }
          OneKeyImageSafety.requireAnimationFrameCount(frameCount)
          OneKeyImageSafety.requireAnimatedDimensions(width, height)
          return OneKeyEncodedImageMetadata(
            animated = true,
            width = width.toInt(),
            height = height.toInt(),
            frameCount = frameCount.toInt(),
            animatedFormat = OneKeyAnimatedFormat.APNG,
          )
        }
        "IDAT", "IEND" -> {
          OneKeyImageSafety.requireStaticDimensions(width, height)
          if (width > Int.MAX_VALUE || height > Int.MAX_VALUE) {
            throw OneKeyImageSafetyException("PNG dimensions exceed the safety limit")
          }
          return OneKeyEncodedImageMetadata(
            animated = false,
            width = width.toInt(),
            height = height.toInt(),
          )
        }
        else -> reader.skipExact(chunkLength + 4L)
      }
    }
  }

  private fun isGif(bytes: ByteArray, length: Int): Boolean =
    length >= 6 && (ascii(bytes, 0, 6) == "GIF87a" || ascii(bytes, 0, 6) == "GIF89a")

  private fun isWebP(bytes: ByteArray, length: Int): Boolean =
    length >= 12 && ascii(bytes, 0) == "RIFF" && ascii(bytes, 8) == "WEBP"

  private fun startsWith(bytes: ByteArray, length: Int, prefix: ByteArray): Boolean {
    if (length < prefix.size) return false
    return prefix.indices.all { bytes[it] == prefix[it] }
  }

  private fun ascii(bytes: ByteArray, offset: Int, length: Int = 4): String =
    String(bytes, offset, length, Charsets.US_ASCII)

  private fun unsignedShortLe(bytes: ByteArray, offset: Int): Int =
    (bytes[offset].toInt() and 0xff) or ((bytes[offset + 1].toInt() and 0xff) shl 8)

  private fun unsigned24Le(bytes: ByteArray, offset: Int): Int =
    (bytes[offset].toInt() and 0xff) or
      ((bytes[offset + 1].toInt() and 0xff) shl 8) or
      ((bytes[offset + 2].toInt() and 0xff) shl 16)

  private fun unsignedIntLe(bytes: ByteArray, offset: Int): Long =
    (bytes[offset].toLong() and 0xffL) or
      ((bytes[offset + 1].toLong() and 0xffL) shl 8) or
      ((bytes[offset + 2].toLong() and 0xffL) shl 16) or
      ((bytes[offset + 3].toLong() and 0xffL) shl 24)

  private fun unsignedIntBe(bytes: ByteArray, offset: Int): Long =
    ((bytes[offset].toLong() and 0xffL) shl 24) or
      ((bytes[offset + 1].toLong() and 0xffL) shl 16) or
      ((bytes[offset + 2].toLong() and 0xffL) shl 8) or
      (bytes[offset + 3].toLong() and 0xffL)

  private fun readUpTo(input: InputStream, bytes: ByteArray): Int {
    var total = 0
    while (total < bytes.size) {
      val count = input.read(bytes, total, bytes.size - total)
      if (count <= 0) break
      total += count
    }
    return total
  }

  private class BoundedMetadataReader(
    private val input: InputStream,
    private val maximumBytes: Int,
  ) {
    private var consumedBytes = 0L

    fun readExact(length: Int): ByteArray {
      requireWithinLimit(length.toLong())
      val bytes = ByteArray(length)
      var offset = 0
      while (offset < length) {
        val count = input.read(bytes, offset, length - offset)
        if (count <= 0) throw OneKeyImageSafetyException("PNG metadata is truncated")
        offset += count
      }
      consumedBytes += length.toLong()
      return bytes
    }

    fun readUnsignedIntBe(): Long {
      val bytes = readExact(4)
      return ((bytes[0].toLong() and 0xffL) shl 24) or
        ((bytes[1].toLong() and 0xffL) shl 16) or
        ((bytes[2].toLong() and 0xffL) shl 8) or
        (bytes[3].toLong() and 0xffL)
    }

    fun readAscii4(): String = String(readExact(4), Charsets.US_ASCII)

    fun readBoundedUnsignedLongBe(): Long {
      val high = readUnsignedIntBe()
      val low = readUnsignedIntBe()
      if (high != 0L || low > maximumBytes.toLong()) {
        throw OneKeyImageSafetyException("Extended BMFF box exceeds the scan limit")
      }
      return low
    }

    fun remainingLimit(): Long = maximumBytes.toLong() - consumedBytes

    fun skipExact(length: Long) {
      requireWithinLimit(length)
      var remaining = length
      while (remaining > 0L) {
        val skipped = input.skip(remaining)
        if (skipped > 0L) {
          remaining -= skipped
        } else if (input.read() >= 0) {
          remaining -= 1L
        } else {
          throw OneKeyImageSafetyException("PNG metadata is truncated")
        }
      }
      consumedBytes += length
    }

    private fun requireWithinLimit(additionalBytes: Long) {
      if (additionalBytes < 0L || consumedBytes + additionalBytes > maximumBytes.toLong()) {
        throw OneKeyImageSafetyException("PNG animation metadata exceeds the scan limit")
      }
    }
  }
}

internal object OneKeyAnimationTimelineInspector {
  fun validate(
    format: OneKeyAnimatedFormat,
    bytes: ByteArray,
    length: Int = bytes.size,
  ) {
    if (length !in 1..bytes.size) fail("Animated image data is empty or invalid")
    when (format) {
      OneKeyAnimatedFormat.GIF -> validateGif(bytes, length)
      OneKeyAnimatedFormat.WEBP -> validateWebP(bytes, length)
      OneKeyAnimatedFormat.APNG -> validateApng(bytes, length)
    }
  }

  private fun validateGif(bytes: ByteArray, length: Int) {
    val cursor = ByteCursor(bytes, length)
    val signature = cursor.ascii(6)
    if (signature != "GIF87a" && signature != "GIF89a") fail("GIF signature is invalid")
    val canvasWidth = cursor.u16Le()
    val canvasHeight = cursor.u16Le()
    OneKeyImageSafety.requireAnimatedDimensions(canvasWidth, canvasHeight)
    val packed = cursor.u8()
    cursor.skip(2) // background index and pixel aspect ratio
    if (packed and 0x80 != 0) cursor.skip(colorTableBytes(packed))

    var frameCount = 0L
    var totalDurationMs = 0L
    var currentFrameDurationMs = 0L
    var trailerFound = false
    while (cursor.hasRemaining()) {
      when (cursor.u8()) {
        0x3b -> {
          trailerFound = true
          break
        }
        0x21 -> {
          when (cursor.u8()) {
            0xf9 -> {
              if (cursor.u8() != 4) fail("GIF graphic control extension is invalid")
              cursor.u8() // packed fields
              val delayCentiseconds = cursor.u16Le()
              cursor.u8() // transparent color index
              if (cursor.u8() != 0) fail("GIF graphic control extension is truncated")
              val effectiveDelayCentiseconds = if (delayCentiseconds == 0) 10 else delayCentiseconds
              currentFrameDurationMs = effectiveDelayCentiseconds.toLong() * 10L
            }
            else -> cursor.skipSubBlocks()
          }
        }
        0x2c -> {
          val frameX = cursor.u16Le()
          val frameY = cursor.u16Le()
          val frameWidth = cursor.u16Le()
          val frameHeight = cursor.u16Le()
          if (
            frameWidth <= 0 ||
            frameHeight <= 0 ||
            frameX.toLong() + frameWidth.toLong() > canvasWidth.toLong() ||
            frameY.toLong() + frameHeight.toLong() > canvasHeight.toLong()
          ) {
            fail("GIF frame dimensions are invalid")
          }
          val framePacked = cursor.u8()
          if (framePacked and 0x80 != 0) cursor.skip(colorTableBytes(framePacked))
          val minimumCodeSize = cursor.u8()
          if (minimumCodeSize !in 2..12) fail("GIF LZW metadata is invalid")
          cursor.skipSubBlocks()
          frameCount += 1L
          OneKeyImageSafety.requireAnimationFrameCount(frameCount)
          totalDurationMs = safeDurationAdd(totalDurationMs, currentFrameDurationMs)
        }
        else -> fail("GIF block marker is invalid")
      }
    }
    if (!trailerFound || frameCount == 0L) fail("GIF animation is truncated")
  }

  private fun validateWebP(bytes: ByteArray, length: Int) {
    val cursor = ByteCursor(bytes, length)
    if (cursor.ascii(4) != "RIFF") fail("WebP RIFF signature is invalid")
    val declaredSize = cursor.u32Le()
    if (declaredSize + 8L != length.toLong()) fail("WebP RIFF size is invalid")
    if (cursor.ascii(4) != "WEBP") fail("WebP signature is invalid")

    var canvasWidth = 0L
    var canvasHeight = 0L
    var animationFlag = false
    var frameCount = 0L
    var totalDurationMs = 0L
    while (cursor.hasRemaining()) {
      if (cursor.remaining() < 8) fail("WebP chunk header is truncated")
      val chunkType = cursor.ascii(4)
      val chunkSize = cursor.u32Le()
      if (chunkSize > cursor.remaining().toLong()) fail("WebP chunk is truncated")
      val payloadStart = cursor.position
      when (chunkType) {
        "VP8X" -> {
          if (chunkSize < 10L) fail("WebP VP8X metadata is invalid")
          val flags = cursor.u8()
          animationFlag = flags and 0x02 != 0
          cursor.skip(3)
          canvasWidth = cursor.u24Le().toLong() + 1L
          canvasHeight = cursor.u24Le().toLong() + 1L
          OneKeyImageSafety.requireAnimatedDimensions(canvasWidth, canvasHeight)
        }
        "ANMF" -> {
          if (!animationFlag || canvasWidth <= 0L || canvasHeight <= 0L || chunkSize < 16L) {
            fail("Animated WebP frame metadata is invalid")
          }
          val frameX = cursor.u24Le().toLong() * 2L
          val frameY = cursor.u24Le().toLong() * 2L
          val frameWidth = cursor.u24Le().toLong() + 1L
          val frameHeight = cursor.u24Le().toLong() + 1L
          val durationMs = cursor.u24Le().toLong()
          cursor.u8() // blend/dispose flags
          if (
            frameX + frameWidth > canvasWidth ||
            frameY + frameHeight > canvasHeight
          ) {
            fail("Animated WebP frame exceeds its canvas")
          }
          frameCount += 1L
          OneKeyImageSafety.requireAnimationFrameCount(frameCount)
          totalDurationMs = safeDurationAdd(totalDurationMs, if (durationMs == 0L) 100L else durationMs)
        }
      }
      cursor.position = checkedEnd(payloadStart, chunkSize, length)
      if (chunkSize and 1L != 0L) cursor.skip(1)
    }
    if (!animationFlag || canvasWidth <= 0L || canvasHeight <= 0L || frameCount == 0L) {
      fail("Animated WebP metadata is incomplete")
    }
  }

  private fun validateApng(bytes: ByteArray, length: Int) {
    val cursor = ByteCursor(bytes, length)
    val signature = byteArrayOf(
      0x89.toByte(), 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a,
    )
    if (!cursor.bytesEqual(signature)) fail("PNG signature is invalid")

    var canvasWidth = 0L
    var canvasHeight = 0L
    var declaredFrames = 0L
    var frameCount = 0L
    var totalDurationMs = 0L
    var sawIhdr = false
    var sawActl = false
    var sawIdat = false
    var sawIend = false
    while (cursor.hasRemaining()) {
      if (cursor.remaining() < 12) fail("PNG chunk header is truncated")
      val chunkSize = cursor.u32Be()
      val chunkType = cursor.ascii(4)
      if (chunkSize + 4L > cursor.remaining().toLong()) fail("PNG chunk is truncated")
      val payloadStart = cursor.position
      when (chunkType) {
        "IHDR" -> {
          if (sawIhdr || payloadStart != 16 || chunkSize != 13L) fail("PNG IHDR is invalid")
          canvasWidth = cursor.u32Be()
          canvasHeight = cursor.u32Be()
          OneKeyImageSafety.requireAnimatedDimensions(canvasWidth, canvasHeight)
          sawIhdr = true
        }
        "acTL" -> {
          if (!sawIhdr || sawActl || sawIdat || chunkSize != 8L) fail("APNG acTL is invalid")
          declaredFrames = cursor.u32Be()
          cursor.u32Be() // play count
          if (declaredFrames == 0L) fail("APNG frame count is invalid")
          OneKeyImageSafety.requireAnimationFrameCount(declaredFrames)
          sawActl = true
        }
        "fcTL" -> {
          if (!sawActl || chunkSize != 26L) fail("APNG fcTL is invalid")
          cursor.u32Be() // sequence number
          val frameWidth = cursor.u32Be()
          val frameHeight = cursor.u32Be()
          val frameX = cursor.u32Be()
          val frameY = cursor.u32Be()
          val delayNumerator = cursor.u16Be()
          val rawDelayDenominator = cursor.u16Be()
          val delayDenominator = if (rawDelayDenominator == 0) 100 else rawDelayDenominator
          cursor.u8()
          cursor.u8()
          if (
            frameWidth <= 0L ||
            frameHeight <= 0L ||
            frameX + frameWidth > canvasWidth ||
            frameY + frameHeight > canvasHeight
          ) {
            fail("APNG frame dimensions are invalid")
          }
          frameCount += 1L
          OneKeyImageSafety.requireAnimationFrameCount(frameCount)
          val decodedDurationMs = delayNumerator.toLong() * 1000L / delayDenominator.toLong()
          // penfeizhou stores these fields as signed shorts and normalizes sub-10ms
          // frame durations to 100ms. Ambiguous signed values use the larger duration
          // so the safety accounting never undercounts the decoder's result.
          val signedValueIsAmbiguous =
            delayNumerator > Short.MAX_VALUE ||
              (rawDelayDenominator != 0 && rawDelayDenominator > Short.MAX_VALUE)
          val effectiveDurationMs =
            if (decodedDurationMs < 10L || signedValueIsAmbiguous) {
              maxOf(decodedDurationMs, 100L)
            } else {
              decodedDurationMs
            }
          totalDurationMs = safeDurationAdd(totalDurationMs, effectiveDurationMs)
        }
        "IDAT" -> {
          if (!sawActl || frameCount == 0L) fail("APNG IDAT appears before frame metadata")
          sawIdat = true
        }
        "IEND" -> {
          if (chunkSize != 0L) fail("PNG IEND is invalid")
          sawIend = true
        }
      }
      cursor.position = checkedEnd(payloadStart, chunkSize, length)
      cursor.skip(4) // CRC
      if (sawIend) break
    }
    if (
      !sawIhdr ||
      !sawActl ||
      !sawIdat ||
      !sawIend ||
      frameCount == 0L ||
      frameCount != declaredFrames ||
      cursor.hasRemaining()
    ) {
      fail("APNG animation is truncated or inconsistent")
    }
  }

  private fun colorTableBytes(packed: Int): Int = 3 * (1 shl ((packed and 0x07) + 1))

  private fun checkedEnd(start: Int, size: Long, limit: Int): Int {
    if (size < 0L || size > Int.MAX_VALUE.toLong()) fail("Image chunk size is invalid")
    val end = start.toLong() + size
    if (end > limit.toLong()) fail("Image chunk is truncated")
    return end.toInt()
  }

  private fun safeDurationAdd(currentMs: Long, additionalMs: Long): Long {
    if (additionalMs < 0L || currentMs > Long.MAX_VALUE - additionalMs) {
      fail("Animation duration overflows")
    }
    val result = currentMs + additionalMs
    if (result > OneKeyImageSafety.MAX_ANIMATION_DURATION_MS) {
      fail("Animated image exceeds the 60 second limit")
    }
    return result
  }

  private fun fail(message: String): Nothing = throw OneKeyImageSafetyException(message)

  private class ByteCursor(
    private val bytes: ByteArray,
    private val limit: Int,
  ) {
    var position: Int = 0
      set(value) {
        if (value !in 0..limit) fail("Image metadata position is invalid")
        field = value
      }

    fun hasRemaining(): Boolean = position < limit
    fun remaining(): Int = limit - position

    fun u8(): Int {
      requireBytes(1)
      return bytes[position++].toInt() and 0xff
    }

    fun u16Le(): Int = u8() or (u8() shl 8)
    fun u16Be(): Int = (u8() shl 8) or u8()
    fun u24Le(): Int = u8() or (u8() shl 8) or (u8() shl 16)
    fun u32Le(): Long =
      u8().toLong() or
        (u8().toLong() shl 8) or
        (u8().toLong() shl 16) or
        (u8().toLong() shl 24)

    fun u32Be(): Long =
      (u8().toLong() shl 24) or
        (u8().toLong() shl 16) or
        (u8().toLong() shl 8) or
        u8().toLong()

    fun ascii(length: Int): String {
      requireBytes(length)
      return String(bytes, position, length, Charsets.US_ASCII).also { position += length }
    }

    fun skip(length: Int) {
      requireBytes(length)
      position += length
    }

    fun skipSubBlocks() {
      while (true) {
        val blockLength = u8()
        if (blockLength == 0) return
        skip(blockLength)
      }
    }

    fun bytesEqual(expected: ByteArray): Boolean {
      requireBytes(expected.size)
      for (index in expected.indices) {
        if (bytes[position + index] != expected[index]) return false
      }
      position += expected.size
      return true
    }

    private fun requireBytes(count: Int) {
      if (count < 0 || count > remaining()) fail("Animated image metadata is truncated")
    }
  }
}

internal class OneKeyImageSafetyVersionedKey(
  private val delegate: Key,
) : Key {
  override fun updateDiskCacheKey(messageDigest: MessageDigest) {
    delegate.updateDiskCacheKey(messageDigest)
    messageDigest.update(VERSION_BYTES)
  }

  override fun equals(other: Any?): Boolean =
    other is OneKeyImageSafetyVersionedKey && delegate == other.delegate

  override fun hashCode(): Int = 31 * delegate.hashCode() + CACHE_VERSION.hashCode()

  companion object {
    internal const val CACHE_VERSION = "onekey-image-safety-v2"
    private val VERSION_BYTES = CACHE_VERSION.toByteArray(Key.CHARSET)
  }
}

internal class OneKeyImageRemoteSourceKey(
  private val requestUrl: String,
  private val headersDigest: String?,
) : Key {
  override fun updateDiskCacheKey(messageDigest: MessageDigest) {
    val urlBytes = requestUrl.toByteArray(Key.CHARSET)
    messageDigest.update(REMOTE_KEY_MARKER_BYTES)
    messageDigest.update(ByteBuffer.allocate(Int.SIZE_BYTES).putInt(urlBytes.size).array())
    messageDigest.update(urlBytes)
    if (headersDigest != null) {
      messageDigest.update(HEADERS_MARKER_BYTES)
      messageDigest.update(headersDigest.toByteArray(Key.CHARSET))
    }
  }

  override fun equals(other: Any?): Boolean =
    other is OneKeyImageRemoteSourceKey &&
      requestUrl == other.requestUrl &&
      headersDigest == other.headersDigest

  override fun hashCode(): Int = 31 * requestUrl.hashCode() + headersDigest.hashCode()

  companion object {
    private val REMOTE_KEY_MARKER_BYTES = "onekey-remote-source-url".toByteArray(Key.CHARSET)
    private val HEADERS_MARKER_BYTES = "onekey-remote-headers-sha256".toByteArray(Key.CHARSET)
  }
}

internal class OneKeyLimitedInputStream(
  private val source: InputStream,
  private val maximumBytes: Long = OneKeyImageSafety.MAX_ENCODED_BYTES,
) : FilterInputStream(source) {
  private var position = 0L
  private var furthestPosition = 0L
  private var markedPosition = 0L

  override fun read(): Int {
    val value = source.read()
    if (value >= 0) advance(1L)
    return value
  }

  override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
    val count = source.read(buffer, offset, length)
    if (count > 0) advance(count.toLong())
    return count
  }

  override fun skip(byteCount: Long): Long {
    val skipped = source.skip(byteCount)
    if (skipped > 0L) advance(skipped)
    return skipped
  }

  override fun mark(readLimit: Int) {
    source.mark(readLimit)
    markedPosition = position
  }

  override fun reset() {
    source.reset()
    position = markedPosition
  }

  private fun advance(byteCount: Long) {
    position += byteCount
    furthestPosition = maxOf(furthestPosition, position)
    OneKeyImageSafety.requireEncodedLength(furthestPosition, maximumBytes)
  }
}

internal class OneKeyImageRemoteModelLoaderFactory(
  callFactory: Call.Factory,
) : ModelLoaderFactory<OneKeyImageRemoteModel, InputStream> {
  private val delegateFactory = OkHttpUrlLoader.Factory(callFactory)

  override fun build(
    multiFactory: MultiModelLoaderFactory,
  ): ModelLoader<OneKeyImageRemoteModel, InputStream> =
    OneKeyImageRemoteModelLoader(delegateFactory.build(multiFactory))

  override fun teardown() = delegateFactory.teardown()
}

private class OneKeyImageRemoteModelLoader(
  private val delegate: ModelLoader<com.bumptech.glide.load.model.GlideUrl, InputStream>,
) : ModelLoader<OneKeyImageRemoteModel, InputStream> {
  override fun handles(model: OneKeyImageRemoteModel): Boolean = delegate.handles(model.glideUrl)

  override fun buildLoadData(
    model: OneKeyImageRemoteModel,
    width: Int,
    height: Int,
    options: Options,
  ): ModelLoader.LoadData<InputStream>? {
    val loadData = delegate.buildLoadData(model.glideUrl, width, height, options) ?: return null
    val sourceKey = OneKeyImageSafetyVersionedKey(
      OneKeyImageRemoteSourceKey(model.glideUrl.cacheKey, model.headersDigest),
    )
    return ModelLoader.LoadData(
      sourceKey,
      emptyList(),
      OneKeySafetyInspectingFetcher(loadData.fetcher, limitInput = false),
    )
  }
}

internal class OneKeyImageLocalModelLoaderFactory(
  context: Context,
) : ModelLoaderFactory<OneKeyImageLocalModel, InputStream> {
  private val appContext = context.applicationContext

  override fun build(
    multiFactory: MultiModelLoaderFactory,
  ): ModelLoader<OneKeyImageLocalModel, InputStream> = OneKeyImageLocalModelLoader(
    appContext,
    multiFactory.build(Uri::class.java, InputStream::class.java),
  )

  override fun teardown() = Unit
}

private class OneKeyImageLocalModelLoader(
  private val context: Context,
  private val delegate: ModelLoader<Uri, InputStream>,
) : ModelLoader<OneKeyImageLocalModel, InputStream> {
  override fun handles(model: OneKeyImageLocalModel): Boolean = delegate.handles(model.uri)

  override fun buildLoadData(
    model: OneKeyImageLocalModel,
    width: Int,
    height: Int,
    options: Options,
  ): ModelLoader.LoadData<InputStream>? {
    val loadData = delegate.buildLoadData(model.uri, width, height, options) ?: return null
    return ModelLoader.LoadData(
      OneKeyImageSafetyVersionedKey(loadData.sourceKey),
      loadData.alternateKeys.map(::OneKeyImageSafetyVersionedKey),
      OneKeyLimitedLocalFetcher(context, model.uri, loadData.fetcher),
    )
  }
}

private class OneKeyLimitedLocalFetcher(
  private val context: Context,
  private val uri: Uri,
  private val delegate: DataFetcher<InputStream>,
) : DataFetcher<InputStream> {
  override fun cleanup() = delegate.cleanup()
  override fun cancel() = delegate.cancel()
  override fun getDataClass(): Class<InputStream> = InputStream::class.java
  override fun getDataSource(): DataSource = delegate.dataSource

  override fun loadData(
    priority: Priority,
    callback: DataFetcher.DataCallback<in InputStream>,
  ) {
    val knownLength = knownLocalLength(context, uri)
    if (knownLength > OneKeyImageSafety.MAX_ENCODED_BYTES) {
      callback.onLoadFailed(
        OneKeyImageSafetyException("Encoded image exceeds the 32 MiB limit"),
      )
      return
    }
    delegate.loadData(priority, object : DataFetcher.DataCallback<InputStream> {
      override fun onDataReady(data: InputStream?) {
        if (data == null) {
          callback.onDataReady(null)
          return
        }
        try {
          callback.onDataReady(inspectInput(data, limitInput = true))
        } catch (error: IOException) {
          callback.onLoadFailed(error)
        }
      }

      override fun onLoadFailed(error: Exception) = callback.onLoadFailed(error)
    })
  }

  private fun knownLocalLength(context: Context, uri: Uri): Long = try {
    when (uri.scheme) {
      "file" -> {
        val path = uri.path
        if (path != null && path.startsWith("/android_asset/")) {
          context.assets.openFd(path.removePrefix("/android_asset/")).use { it.length }
        } else {
          path?.let(::File)?.takeIf(File::isFile)?.length() ?: -1L
        }
      }
      "content", "android.resource" ->
        context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: -1L
      else -> -1L
    }
  } catch (_: Exception) {
    -1L
  }
}

private class OneKeySafetyInspectingFetcher(
  private val delegate: DataFetcher<InputStream>,
  private val limitInput: Boolean,
) : DataFetcher<InputStream> {
  override fun cleanup() = delegate.cleanup()
  override fun cancel() = delegate.cancel()
  override fun getDataClass(): Class<InputStream> = InputStream::class.java
  override fun getDataSource(): DataSource = delegate.dataSource

  override fun loadData(
    priority: Priority,
    callback: DataFetcher.DataCallback<in InputStream>,
  ) {
    delegate.loadData(priority, object : DataFetcher.DataCallback<InputStream> {
      override fun onDataReady(data: InputStream?) {
        if (data == null) {
          callback.onDataReady(null)
          return
        }
        try {
          callback.onDataReady(inspectInput(data, limitInput))
        } catch (error: IOException) {
          callback.onLoadFailed(error)
        }
      }

      override fun onLoadFailed(error: Exception) = callback.onLoadFailed(error)
    })
  }
}

private fun inspectInput(input: InputStream, limitInput: Boolean): InputStream {
  val source = if (limitInput) OneKeyLimitedInputStream(input) else input
  val buffered = BufferedInputStream(source)
  return try {
    val metadata = OneKeyEncodedImageInspector.inspect(buffered)
    if (metadata.animated) bufferReplayableAnimation(buffered, metadata) else buffered
  } catch (error: IOException) {
    buffered.close()
    throw error
  }
}

private fun bufferReplayableAnimation(
  input: InputStream,
  metadata: OneKeyEncodedImageMetadata,
): InputStream {
  val output = OneKeyReplayBuffer()
  val buffer = ByteArray(16 * 1024)
  while (true) {
    val count = input.read(buffer)
    if (count < 0) break
    if (count == 0) continue
    OneKeyImageSafety.requireEncodedLength(
      output.size().toLong() + count.toLong(),
      OneKeyImageSafety.MAX_ANIMATED_ENCODED_BYTES,
    )
    output.write(buffer, 0, count)
  }
  val format = metadata.animatedFormat
    ?: throw OneKeyImageSafetyException("Animated image format is unavailable")
  output.validate(format)
  return output.inputStream()
}

private class OneKeyReplayBuffer : ByteArrayOutputStream(16 * 1024) {
  fun validate(format: OneKeyAnimatedFormat) {
    OneKeyAnimationTimelineInspector.validate(format, buf, count)
  }

  fun inputStream(): InputStream = ByteArrayInputStream(buf, 0, count)
}

internal class OneKeyImageEncodedDataInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
    val response = chain.proceed(chain.request())
    val body = response.body ?: return response
    try {
      OneKeyImageSafety.requireEncodedLength(body.contentLength())
    } catch (error: OneKeyImageSafetyException) {
      response.close()
      throw error
    }
    return response.newBuilder()
      .body(OneKeyLimitedResponseBody(body))
      .build()
  }
}

private class OneKeyLimitedResponseBody(
  private val delegate: ResponseBody,
) : ResponseBody() {
  private val limitedSource: BufferedSource by lazy {
    object : ForwardingSource(delegate.source()) {
      private var receivedBytes = 0L

      override fun read(sink: Buffer, byteCount: Long): Long {
        val count = super.read(sink, byteCount)
        if (count > 0L) {
          receivedBytes += count
          OneKeyImageSafety.requireEncodedLength(receivedBytes)
        }
        return count
      }
    }.buffer()
  }

  override fun contentType(): MediaType? = delegate.contentType()
  override fun contentLength(): Long = delegate.contentLength()
  override fun source(): BufferedSource = limitedSource
}
