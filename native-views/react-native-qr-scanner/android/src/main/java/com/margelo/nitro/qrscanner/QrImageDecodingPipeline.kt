package com.margelo.nitro.qrscanner

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import androidx.exifinterface.media.ExifInterface
import java.io.File
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sqrt

class QrImageDecodeException(val code: String, reason: String) : Exception("$code: $reason")

data class QrImageHit(val text: String, val engine: String, val pass: String)

interface QrImageEngine {
  /** `mlkit` or `zxing`. */
  val name: String
  fun decode(bitmap: Bitmap): String?
}

/**
 * Android counterpart of ios/QrImageDecodingPipeline.swift: EXIF orientation,
 * downscale to the pixel budget, then the passes original, padded, grayscale
 * and inverted; within a pass every engine in order; first hit wins.
 */
class QrImageDecodingPipeline(private val engines: List<QrImageEngine>) {
  companion object {
    const val MAX_SOURCE_PIXELS = 200_000_000L
    const val PADDING_RATIO = 0.1
    private const val MIN_PADDING = 16
    private const val CONTRAST = 1.3f

    /** Size scaled so width * height <= maxPixels, keeping the aspect ratio. */
    fun fitToPixelBudget(width: Int, height: Int, maxPixels: Long): Pair<Int, Int> {
      val pixels = width.toLong() * height
      if (pixels <= maxPixels) return width to height
      val scale = sqrt(maxPixels.toDouble() / pixels)
      return max(1, floor(width * scale).toInt()) to max(1, floor(height * scale).toInt())
    }

    fun padded(source: Bitmap): Bitmap {
      val margin = max(MIN_PADDING, (max(source.width, source.height) * PADDING_RATIO).toInt())
      val out = Bitmap.createBitmap(source.width + margin * 2, source.height + margin * 2, Bitmap.Config.ARGB_8888)
      Canvas(out).apply {
        drawColor(Color.WHITE)
        drawBitmap(source, margin.toFloat(), margin.toFloat(), null)
      }
      return out
    }

    fun grayscale(source: Bitmap, inverted: Boolean): Bitmap {
      val matrix = ColorMatrix().apply { setSaturation(0f) }
      val translate = 128f * (1 - CONTRAST)
      matrix.postConcat(
        ColorMatrix(
          floatArrayOf(
            CONTRAST, 0f, 0f, 0f, translate,
            0f, CONTRAST, 0f, 0f, translate,
            0f, 0f, CONTRAST, 0f, translate,
            0f, 0f, 0f, 1f, 0f,
          ),
        ),
      )
      if (inverted) {
        matrix.postConcat(
          ColorMatrix(
            floatArrayOf(
              -1f, 0f, 0f, 0f, 255f,
              0f, -1f, 0f, 0f, 255f,
              0f, 0f, -1f, 0f, 255f,
              0f, 0f, 0f, 1f, 0f,
            ),
          ),
        )
      }
      val out = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
      Canvas(out).drawBitmap(source, 0f, 0f, Paint().apply { colorFilter = ColorMatrixColorFilter(matrix) })
      return out
    }
  }

  fun decode(file: File, maxPixels: Long): QrImageHit? {
    val image = loadOriented(file, maxPixels)
    try {
      return decodeBitmap(image)
    } finally {
      image.recycle()
    }
  }

  fun decodeBitmap(image: Bitmap): QrImageHit? {
    val passes: List<Pair<String, () -> Bitmap>> = listOf(
      "original" to { image },
      "padded" to { padded(image) },
      "grayscale" to { grayscale(image, inverted = false) },
      "inverted" to { grayscale(image, inverted = true) },
    )
    for ((pass, make) in passes) {
      val variant = make()
      try {
        for (engine in engines) {
          val text = engine.decode(variant)
          if (!text.isNullOrEmpty()) return QrImageHit(text, engine.name, pass)
        }
      } finally {
        if (variant !== image) variant.recycle()
      }
    }
    return null
  }

  private fun loadOriented(file: File, maxPixels: Long): Bitmap {
    if (!file.isFile) throw QrImageDecodeException("invalid-image", "unreadable or unsupported image")
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(file.path, bounds)
    val width = bounds.outWidth
    val height = bounds.outHeight
    if (width <= 0 || height <= 0) {
      throw QrImageDecodeException("invalid-image", "unreadable or unsupported image")
    }
    if (width.toLong() * height > MAX_SOURCE_PIXELS) {
      throw QrImageDecodeException("resource-limit", "image has ${width}x$height pixels")
    }
    // Decode at the largest power-of-two reduction that still covers the budget.
    var sample = 1
    while (width.toLong() / (sample * 2) * (height.toLong() / (sample * 2)) >= maxPixels) sample *= 2
    val decoded = BitmapFactory.decodeFile(
      file.path,
      BitmapFactory.Options().apply {
        inSampleSize = sample
        inPreferredConfig = Bitmap.Config.ARGB_8888
      },
    ) ?: throw QrImageDecodeException("invalid-image", "image could not be decoded")

    val (targetWidth, targetHeight) = fitToPixelBudget(decoded.width, decoded.height, maxPixels)
    val matrix = Matrix()
    if (targetWidth != decoded.width) {
      matrix.postScale(targetWidth.toFloat() / decoded.width, targetHeight.toFloat() / decoded.height)
    }
    applyExifOrientation(file, matrix)
    if (matrix.isIdentity) return decoded
    val oriented = Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
    if (oriented !== decoded) decoded.recycle()
    return oriented
  }

  private fun applyExifOrientation(file: File, matrix: Matrix) {
    val orientation = try {
      ExifInterface(file).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    } catch (_: Exception) {
      ExifInterface.ORIENTATION_NORMAL
    }
    when (orientation) {
      ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
      ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
      ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
      ExifInterface.ORIENTATION_TRANSPOSE -> { matrix.postRotate(90f); matrix.postScale(-1f, 1f) }
      ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
      ExifInterface.ORIENTATION_TRANSVERSE -> { matrix.postRotate(270f); matrix.postScale(-1f, 1f) }
      ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
    }
  }
}
