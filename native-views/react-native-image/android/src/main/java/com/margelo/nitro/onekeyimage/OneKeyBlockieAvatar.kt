// OneKey patch: Render the versioned local avatar URI without a JS PNG payload.
// Algorithm ported from ethereum-blockies-base64 1.0.2 by MyCrypto (MIT):
// https://github.com/MyCryptoHQ/ethereum-blockies-base64
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package com.margelo.nitro.onekeyimage

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.util.concurrent.CancellationException
import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicBoolean
import java.util.zip.CRC32
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.DataFormatException
import java.util.zip.Inflater
import kotlin.math.floor

internal data class OneKeyBlockieAvatarModel(val uri: String)

internal object OneKeyBlockieAvatar {
  const val URI_PREFIX = "onekey-avatar://blockie/v1/"
  const val SIZE = 128
  const val MAX_PNG_BYTES = SIZE * (SIZE / 4 + 1) + 128

  fun isAvatarUri(uri: String): Boolean = uri.startsWith("onekey-avatar:")

  fun decodeSeed(uri: String, isCancelled: () -> Boolean = { false }): String {
    require(uri.startsWith(URI_PREFIX)) { "Unsupported avatar URI version" }
    val encoded = uri.substring(URI_PREFIX.length)
    require(encoded.isNotEmpty()) { "Avatar seed is empty" }
    val bytes = ByteArrayOutputStream(encoded.length)
    var index = 0
    while (index < encoded.length) {
      if (index % 256 == 0 && isCancelled()) throw CancellationException("Avatar generation cancelled")
      val char = encoded[index]
      if (char == '%') {
        require(index + 2 < encoded.length) { "Invalid avatar percent encoding" }
        val high = encoded[index + 1].digitToIntOrNull(16)
        val low = encoded[index + 2].digitToIntOrNull(16)
        require(high != null && low != null) { "Invalid avatar percent encoding" }
        bytes.write((high shl 4) or low)
        index += 3
      } else {
        require(char in 'a'..'z' || char in 'A'..'Z' || char in '0'..'9' || char in "-_.!~*'()") {
          "Invalid avatar URI component"
        }
        bytes.write(char.code)
        index += 1
      }
    }
    // The app already applies JavaScript lowercase; JVM Unicode casing differs.
    return Charsets.UTF_8.newDecoder()
      .onMalformedInput(CodingErrorAction.REPORT)
      .onUnmappableCharacter(CodingErrorAction.REPORT)
      .decode(ByteBuffer.wrap(bytes.toByteArray()))
      .toString()
  }

  // Only our fixed v1 encoder format is accepted, never arbitrary user PNG data.
  fun isValidPng(bytes: ByteArray, isCancelled: () -> Boolean = { false }): Boolean {
    if (isCancelled()) throw CancellationException("Avatar cache read cancelled")
    if (bytes.size !in 93..MAX_PNG_BYTES) return false
    val input = ByteBuffer.wrap(bytes)
    if (input.long != 0x89504e470d0a1a0aUL.toLong()) return false
    fun chunk(expectedType: String, expectedLength: Int? = null): ByteArray? {
      if (input.remaining() < 12) return null
      val length = input.int
      if (length < 0 || length > input.remaining() - 8) return null
      if (expectedLength != null && length != expectedLength) return null
      val type = ByteArray(4).also(input::get)
      if (!type.contentEquals(expectedType.toByteArray(Charsets.US_ASCII))) return null
      val data = ByteArray(length).also(input::get)
      val crc = CRC32().apply { update(type); update(data) }
      if (input.int != crc.value.toInt()) return null
      return data
    }
    val header = chunk("IHDR", 13) ?: return false
    if (!header.contentEquals(byteArrayOf(0, 0, 0, -128, 0, 0, 0, -128, 2, 3, 0, 0, 0))) return false
    chunk("PLTE", 9) ?: return false
    if (chunk("tRNS", 3)?.contentEquals(byteArrayOf(-1, -1, -1)) != true) return false
    val compressed = chunk("IDAT") ?: return false
    chunk("IEND", 0) ?: return false
    if (input.hasRemaining()) return false

    val expectedBytes = SIZE * (SIZE / 4 + 1)
    val pixels = ByteArray(expectedBytes + 1)
    val inflater = Inflater()
    var count = 0
    try {
      inflater.setInput(compressed)
      while (!inflater.finished() && count <= expectedBytes) {
        if (isCancelled()) throw CancellationException("Avatar cache read cancelled")
        val decoded = inflater.inflate(pixels, count, pixels.size - count)
        if (decoded == 0) return false
        count += decoded
      }
      if (!inflater.finished() || inflater.remaining != 0 || count != expectedBytes) return false
    } catch (_: DataFormatException) {
      return false
    } finally {
      inflater.end()
    }
    repeat(SIZE) { row ->
      if (isCancelled()) throw CancellationException("Avatar cache read cancelled")
      val offset = row * 33
      if (pixels[offset] != 0.toByte()) return false
      for (index in 1..32) {
        val value = pixels[offset + index].toInt() and 0xff
        if (
          (value and 3) == 3 || ((value shr 2) and 3) == 3 ||
          ((value shr 4) and 3) == 3 || ((value shr 6) and 3) == 3
        ) return false
      }
    }
    return true
  }

  fun png(uri: String, isCancelled: () -> Boolean = { false }): ByteArray {
    fun checkCancelled() {
      if (isCancelled()) throw CancellationException("Avatar generation cancelled")
    }
    checkCancelled()
    val seed = decodeSeed(uri, isCancelled)
    val state = IntArray(4)
    seed.forEachIndexed { index, char ->
      if (index % 256 == 0) checkCancelled()
      val slot = index % 4
      // Kotlin Int overflow and signed shr preserve JavaScript's bitwise PRNG.
      state[slot] = (state[slot] shl 5) - state[slot] + char.code
    }
    fun random(): Double {
      val t = state[0] xor (state[0] shl 11)
      state[0] = state[1]
      state[1] = state[2]
      state[2] = state[3]
      state[3] = state[3] xor (state[3] shr 19) xor t xor (t shr 8)
      return (state[3].toLong() and 0xffffffffL).toDouble() / 2147483648.0
    }
    fun hue(p: Double, q: Double, value: Double): Double {
      var t = value
      if (t < 0) t += 1
      if (t > 1) t -= 1
      return when {
        t < 1.0 / 6.0 -> p + (q - p) * 6 * t
        t < 1.0 / 2.0 -> q
        t < 2.0 / 3.0 -> p + (q - p) * (2.0 / 3.0 - t) * 6
        else -> p
      }
    }
    fun color(): ByteArray {
      val h = floor(random() * 360) / 360
      val saturation = (random() * 60 + 40) / 100
      val lightness = ((random() + random() + random() + random()) * 25) / 100
      val q = if (lightness < 0.5) lightness * (1 + saturation)
        else lightness + saturation - lightness * saturation
      val p = 2 * lightness - q
      return doubleArrayOf(
        hue(p, q, h + 1.0 / 3.0), hue(p, q, h), hue(p, q, h - 1.0 / 3.0),
      ).map { floor(it * 255 + 0.5).toInt().toByte() }.toByteArray()
    }
    val foreground = color()
    val background = color()
    val spot = color()
    val pixels = ByteArray(SIZE * 33)
    repeat(8) { row ->
      checkCancelled()
      val line = ByteArray(33)
      repeat(4) { column ->
        val value = floor(random() * 2.3).toInt()
        val paletteIndex = if (value == 0) 0 else if (value == 1) 1 else 2
        val packed = (paletteIndex * 0x55).toByte()
        line.fill(packed, 1 + column * 4, 1 + (column + 1) * 4)
        line.fill(packed, 1 + (7 - column) * 4, 1 + (8 - column) * 4)
      }
      repeat(16) { line.copyInto(pixels, (row * 16 + it) * 33) }
    }
    val compressed = ByteArrayOutputStream()
    val deflater = Deflater(Deflater.BEST_SPEED)
    try {
      DeflaterOutputStream(compressed, deflater).use { it.write(pixels) }
    } finally {
      deflater.end()
    }
    checkCancelled()
    val result = ByteArrayOutputStream()
    DataOutputStream(result).use { output ->
      output.write(byteArrayOf(137.toByte(), 80, 78, 71, 13, 10, 26, 10))
      fun chunk(type: String, data: ByteArray) {
        val typeBytes = type.toByteArray(Charsets.US_ASCII)
        output.writeInt(data.size)
        output.write(typeBytes)
        output.write(data)
        val crc = CRC32().apply { update(typeBytes); update(data) }
        output.writeInt(crc.value.toInt())
      }
      chunk("IHDR", byteArrayOf(0, 0, 0, 128.toByte(), 0, 0, 0, 128.toByte(), 2, 3, 0, 0, 0))
      chunk("PLTE", background + foreground + spot)
      chunk("tRNS", byteArrayOf(-1, -1, -1))
      chunk("IDAT", compressed.toByteArray())
      chunk("IEND", byteArrayOf())
    }
    return result.toByteArray()
  }
}

/** Only overlapping source fetches are retained; Glide owns all lasting caches. */
internal class OneKeyAvatarInFlight(
  private val generate: (String, () -> Boolean) -> ByteArray,
) {
  internal class Work(uri: String, generate: (String, () -> Boolean) -> ByteArray) {
    val cancelled = AtomicBoolean(false)
    val task = FutureTask { generate(uri, cancelled::get) }
    var references = 0
  }

  private val pending = mutableMapOf<String, Work>()

  internal inner class Lease(private val uri: String, internal val work: Work) {
    private val released = AtomicBoolean(false)

    fun bytes(): ByteArray {
      work.task.run()
      return work.task.get()
    }

    fun release() {
      if (!released.compareAndSet(false, true)) return
      synchronized(pending) {
        work.references -= 1
        if (work.references == 0) {
          if (pending[uri] === work) pending.remove(uri)
          if (!work.task.isDone) {
            work.cancelled.set(true)
            work.task.cancel(false)
          }
        }
      }
    }
  }

  fun acquire(uri: String): Lease = synchronized(pending) {
    val work = pending.getOrPut(uri) { Work(uri, generate) }
    work.references += 1
    Lease(uri, work)
  }
}
