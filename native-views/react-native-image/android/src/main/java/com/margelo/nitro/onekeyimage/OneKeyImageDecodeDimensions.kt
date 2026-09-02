package com.margelo.nitro.onekeyimage

import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Physical decode dimensions constrained to one 16 MiB ARGB_8888 bitmap.
 *
 * The same calculation is shared by render and preload so Glide's size-dependent
 * cache identity cannot drift between the two paths.
 */
internal data class OneKeyImageDecodeDimensions(
  val width: Int,
  val height: Int,
) {
  val estimatedBytes: Long
    get() = width.toLong() * height.toLong() * BYTES_PER_PIXEL

  companion object {
    internal const val MAX_DECODE_BYTES = 16L * 1024L * 1024L
    internal const val MAX_SQUARE_SIDE = 2048
    private const val BYTES_PER_PIXEL = 4L
    private const val MAX_PIXELS = MAX_DECODE_BYTES / BYTES_PER_PIXEL

    fun forRender(width: Int, height: Int): OneKeyImageDecodeDimensions =
      constrained(width.coerceAtLeast(1), height.coerceAtLeast(1))

    fun forPreload(
      logicalWidth: Double?,
      logicalHeight: Double?,
      pixelRatio: Double,
    ): OneKeyImageDecodeDimensions {
      val width = logicalWidth?.takeIf { it.isFinite() && it > 0.0 }
      val height = logicalHeight?.takeIf { it.isFinite() && it > 0.0 }
      val density = pixelRatio.takeIf { it.isFinite() }?.coerceAtLeast(1.0) ?: 1.0
      val physicalWidth = width?.let { physicalPixels(it, density) }
      val physicalHeight = height?.let { physicalPixels(it, density) }

      return when {
        physicalWidth != null && physicalHeight != null ->
          constrained(physicalWidth, physicalHeight)
        physicalWidth != null -> constrained(physicalWidth, physicalWidth)
        physicalHeight != null -> constrained(physicalHeight, physicalHeight)
        else -> OneKeyImageDecodeDimensions(MAX_SQUARE_SIDE, MAX_SQUARE_SIDE)
      }
    }

    /**
     * Desired size passed to APNG4Android's uniform power-of-two sampler.
     * Returns null only when the source aspect ratio is so extreme that the
     * decoder cannot satisfy the cap without reducing one dimension below 1px.
     */
    fun forAnimatedDecoder(
      sourceWidth: Int,
      sourceHeight: Int,
      targetWidth: Int,
      targetHeight: Int,
    ): OneKeyImageDecodeDimensions? {
      if (sourceWidth <= 0 || sourceHeight <= 0) return null
      val safeTarget = forRender(targetWidth, targetHeight)
      val widthRatio = sourceWidth / safeTarget.width
      val heightRatio = sourceHeight / safeTarget.height
      var sampleSize = highestPowerOfTwoAtMost(min(widthRatio, heightRatio).coerceAtLeast(1))

      while (
        ceil(sourceWidth / sampleSize.toDouble()) *
          ceil(sourceHeight / sampleSize.toDouble()) > MAX_PIXELS
      ) {
        if (sampleSize > Int.MAX_VALUE / 2) return null
        sampleSize *= 2
      }
      if (sampleSize > min(sourceWidth, sourceHeight)) return null
      return OneKeyImageDecodeDimensions(
        (sourceWidth / sampleSize).coerceAtLeast(1),
        (sourceHeight / sampleSize).coerceAtLeast(1),
      )
    }

    fun safeStaticScaleFactor(
      sourceWidth: Int,
      sourceHeight: Int,
      requestedWidth: Int,
      requestedHeight: Int,
    ): Float {
      if (sourceWidth <= 0 || sourceHeight <= 0) return 1f
      OneKeyImageSafety.requireStaticDimensions(sourceWidth, sourceHeight)
      val targetScale = max(
        requestedWidth.toDouble() / sourceWidth,
        requestedHeight.toDouble() / sourceHeight,
      ).coerceAtMost(1.0)
      val sourcePixels = sourceWidth.toDouble() * sourceHeight.toDouble()
      val safetyScale = sqrt(MAX_PIXELS.toDouble() / sourcePixels).coerceAtMost(1.0)
      val resolvedScale = min(targetScale, safetyScale)
      return if (resolvedScale < 1.0) {
        // A Double -> Float conversion may round upward and cross the hard
        // byte boundary by a few pixels.
        java.lang.Math.nextDown(resolvedScale.toFloat())
      } else {
        1f
      }
    }

    private fun highestPowerOfTwoAtMost(value: Int): Int {
      var result = 1
      while (result <= value / 2) result *= 2
      return result
    }

    private fun physicalPixels(logicalSize: Double, pixelRatio: Double): Int {
      val pixels = ceil(logicalSize * pixelRatio)
      return when {
        !pixels.isFinite() || pixels >= Int.MAX_VALUE.toDouble() -> Int.MAX_VALUE
        else -> pixels.toInt().coerceAtLeast(1)
      }
    }

    private fun constrained(width: Int, height: Int): OneKeyImageDecodeDimensions {
      val area = width.toDouble() * height.toDouble()
      if (area <= MAX_PIXELS.toDouble()) {
        return OneKeyImageDecodeDimensions(width, height)
      }

      val scale = sqrt(MAX_PIXELS.toDouble() / area)
      var constrainedWidth = floor(width * scale).toInt().coerceAtLeast(1)
      var constrainedHeight = floor(height * scale).toInt().coerceAtLeast(1)

      // Floating-point rounding should already stay below the limit. Keep this
      // final guard so the hard cap remains true for every Int input.
      if (constrainedWidth.toLong() * constrainedHeight.toLong() > MAX_PIXELS) {
        if (constrainedWidth >= constrainedHeight) {
          constrainedWidth = (MAX_PIXELS / constrainedHeight).toInt().coerceAtLeast(1)
        } else {
          constrainedHeight = (MAX_PIXELS / constrainedWidth).toInt().coerceAtLeast(1)
        }
      }
      return OneKeyImageDecodeDimensions(constrainedWidth, constrainedHeight)
    }
  }
}

/**
 * Downsamples static bitmaps for the target while independently enforcing the
 * 16 MiB area cap. `override()` alone cannot enforce an area cap for very wide
 * or tall sources because Glide's default strategy follows target edges.
 */
internal class OneKeyImageSafeDownsampleStrategy : DownsampleStrategy() {
  override fun getScaleFactor(
    sourceWidth: Int,
    sourceHeight: Int,
    requestedWidth: Int,
    requestedHeight: Int,
  ): Float = OneKeyImageDecodeDimensions.safeStaticScaleFactor(
    sourceWidth,
    sourceHeight,
    requestedWidth,
    requestedHeight,
  )

  override fun getSampleSizeRounding(
    sourceWidth: Int,
    sourceHeight: Int,
    requestedWidth: Int,
    requestedHeight: Int,
  ): SampleSizeRounding = SampleSizeRounding.MEMORY
}
