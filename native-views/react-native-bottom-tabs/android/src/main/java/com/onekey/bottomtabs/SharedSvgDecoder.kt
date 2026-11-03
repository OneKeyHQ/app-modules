package com.onekey.bottomtabs

import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.DecodeUtils
import coil3.decode.Decoder
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.request.maxBitmapSize
import com.margelo.nitro.onekeyimage.OneKeySVG
import kotlinx.coroutines.runInterruptible
import okio.ByteString.Companion.encodeUtf8
import kotlin.math.roundToInt

/** Coil retains fetching, cancellation and sizing; image owns SVG parsing/rendering. */
internal class SharedSvgDecoder(private val result: SourceFetchResult, private val options: Options) : Decoder {
  override suspend fun decode(): DecodeResult = runInterruptible {
    val document = result.source.source().inputStream().use { OneKeySVG.decode(it) }
    val width = document.viewBoxWidth ?: document.intrinsicWidth
    val height = document.viewBoxHeight ?: document.intrinsicHeight
    val destination = DecodeUtils.computeDstSize(
      if (width > 0) width.roundToInt() else 512,
      if (height > 0) height.roundToInt() else 512,
      options.size, options.scale, options.maxBitmapSize)
    val dstWidth = destination.first
    val dstHeight = destination.second
    val (outputWidth, outputHeight) = if (width > 0 && height > 0) {
      val multiplier = DecodeUtils.computeSizeMultiplier(width, height, dstWidth.toFloat(), dstHeight.toFloat(), options.scale)
      (width * multiplier).toInt() to (height * multiplier).toInt()
    } else dstWidth to dstHeight
    DecodeResult(document.render(outputWidth, outputHeight).asImage(), isSampled = true)
  }

  class Factory : Decoder.Factory {
    override fun create(result: SourceFetchResult, options: Options, imageLoader: ImageLoader): Decoder? {
      if (result.mimeType != "image/svg+xml") {
        val source = result.source.source()
        if (!source.rangeEquals(0, "<".encodeUtf8())) return null
        val tag = "<svg".encodeUtf8()
        var found = false
        for (index in 0L until (1024L - tag.size)) {
          if (!source.request(index + tag.size)) break
          if (source.rangeEquals(index, tag)) { found = true; break }
        }
        if (!found) return null
      }
      return SharedSvgDecoder(result, options)
    }
  }
}
