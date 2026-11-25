package com.margelo.nitro.qrscanner

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import androidx.exifinterface.media.ExifInterface
import java.io.File
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sqrt

class QrImageDecodeException(val code: String, reason: String) : Exception("$code: $reason")

data class QrImageHit(val text: String, val engine: String, val pass: String)

data class QrPipelineSymbol(
  val text: String,
  val centerX: Double,
  val centerY: Double,
  val boundsX: Double? = null,
  val boundsY: Double? = null,
  val boundsWidth: Double? = null,
  val boundsHeight: Double? = null,
)

data class QrImageSymbolsHit(
  val width: Int,
  val height: Int,
  val symbols: List<QrPipelineSymbol>,
  val engine: String? = null,
  val pass: String? = null,
)

interface QrImageEngine {
  /** `mlkit` or `zxing`. */
  val name: String
  fun decode(bitmap: Bitmap): String?
  fun decodeSymbols(bitmap: Bitmap): List<QrPipelineSymbol> {
    val text = decode(bitmap)?.takeIf(String::isNotEmpty) ?: return emptyList()
    return listOf(QrPipelineSymbol(text = text, centerX = 0.5, centerY = 0.5))
  }
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
      val budget = maxPixels.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
      return floor(width * scale).toInt().coerceIn(1, budget) to
        floor(height * scale).toInt().coerceIn(1, budget)
    }

    fun padded(source: Bitmap, maxPixels: Long = 50_000_000): Bitmap {
      val margin = max(MIN_PADDING, (max(source.width, source.height) * PADDING_RATIO).toInt())
      val expandedWidth = source.width + margin * 2
      val expandedHeight = source.height + margin * 2
      val (width, height) = fitToPixelBudget(expandedWidth, expandedHeight, maxPixels)
      val out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
      val insetX = margin.toFloat() * width / expandedWidth
      val insetY = margin.toFloat() * height / expandedHeight
      Canvas(out).apply {
        drawColor(Color.WHITE)
        val destination = RectF(insetX, insetY, width - insetX, height - insetY)
        drawBitmap(source, null, destination, Paint(Paint.FILTER_BITMAP_FLAG))
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
      return decodeBitmap(image, maxPixels)
    } finally {
      image.recycle()
    }
  }

  fun decodeBitmap(image: Bitmap, maxPixels: Long = 50_000_000): QrImageHit? {
    val passes: List<Pair<String, () -> Bitmap>> = listOf(
      "original" to { image },
      "padded" to { padded(image, maxPixels) },
      "grayscale" to { grayscale(image, inverted = false) },
      "inverted" to { grayscale(image, inverted = true) },
    )
    var attempted = false
    var decoded = false
    for ((pass, make) in passes) {
      val variant = make()
      try {
        for (engine in engines) {
          attempted = true
          try {
            val text = engine.decode(variant)
            decoded = true
            if (!text.isNullOrEmpty()) return QrImageHit(text, engine.name, pass)
          } catch (error: InterruptedException) {
            Thread.currentThread().interrupt()
            throw error
          } catch (_: Exception) {
            // A failed pass leaves the remaining engines and passes available.
          }
        }
      } finally {
        if (variant !== image) variant.recycle()
      }
    }
    if (attempted && !decoded) throw QrImageDecodeException("decoder-unavailable", "A QR decoder failed")
    return null
  }

  fun decodeSymbols(file: File, maxPixels: Long): QrImageSymbolsHit {
    val image = loadOriented(file, maxPixels)
    try {
      return decodeBitmapSymbols(image, maxPixels)
    } finally {
      image.recycle()
    }
  }

  fun decodeBitmapSymbols(image: Bitmap, maxPixels: Long = 50_000_000): QrImageSymbolsHit {
    val maxImageSymbols = 8
    val passes: List<Pair<String, () -> Bitmap>> = listOf(
      "original" to { image },
      "padded" to { padded(image, maxPixels) },
      "grayscale" to { grayscale(image, inverted = false) },
      "inverted" to { grayscale(image, inverted = true) },
    )
    var attempted = false
    var decoded = false
    val merged = mutableListOf<QrPipelineSymbol>()
    val seen = LinkedHashSet<String>()
    var firstEngine: String? = null
    var firstPass: String? = null

    for ((pass, make) in passes) {
      if (merged.size >= maxImageSymbols) break
      val variant = try {
        make()
      } catch (error: OutOfMemoryError) {
        if (merged.isNotEmpty()) break
        throw error
      } catch (error: RuntimeException) {
        if (merged.isNotEmpty()) continue
        throw error
      }
      try {
        for (engine in engines) {
          if (merged.size >= maxImageSymbols) break
          attempted = true
          try {
            val symbols = engine.decodeSymbols(variant)
            decoded = true
            if (symbols.isNotEmpty()) {
              val mapped = if (pass == "padded") {
                symbols.map { unpadSymbol(it, image, variant) }
              } else {
                symbols
              }
              for (sym in mapped) {
                if (sym.text.isEmpty() || !seen.add(sym.text)) continue
                merged.add(sym)
                if (firstEngine == null) {
                  firstEngine = engine.name
                  firstPass = pass
                }
                if (merged.size >= maxImageSymbols) break
              }
            }
          } catch (error: InterruptedException) {
            Thread.currentThread().interrupt()
            throw error
          } catch (_: Exception) {
            // A failed pass leaves the remaining engines and passes available.
          }
        }
      } finally {
        if (variant !== image) variant.recycle()
      }
    }
    if (merged.isNotEmpty()) {
      return QrImageSymbolsHit(
        width = image.width,
        height = image.height,
        symbols = merged,
        engine = firstEngine,
        pass = firstPass,
      )
    }
    if (attempted && !decoded) throw QrImageDecodeException("decoder-unavailable", "A QR decoder failed")
    return QrImageSymbolsHit(width = image.width, height = image.height, symbols = emptyList())
  }

  private fun unpadSymbol(
    symbol: QrPipelineSymbol,
    original: Bitmap,
    padded: Bitmap,
  ): QrPipelineSymbol {
    val margin = max(MIN_PADDING, (max(original.width, original.height) * PADDING_RATIO).toInt())
    val expandedWidth = original.width + margin * 2
    val expandedHeight = original.height + margin * 2
    val insetX = margin.toDouble() * padded.width / expandedWidth
    val insetY = margin.toDouble() * padded.height / expandedHeight
    val innerWidth = padded.width - insetX * 2
    val innerHeight = padded.height - insetY * 2
    if (innerWidth <= 0 || innerHeight <= 0) return symbol
    val mapX: (Double) -> Double = { (it * padded.width - insetX) / innerWidth }
    val mapY: (Double) -> Double = { (it * padded.height - insetY) / innerHeight }
    return QrPipelineSymbol(
      text = symbol.text,
      centerX = mapX(symbol.centerX),
      centerY = mapY(symbol.centerY),
      boundsX = symbol.boundsX?.let(mapX),
      boundsY = symbol.boundsY?.let(mapY),
      boundsWidth = symbol.boundsWidth?.let { (it * padded.width) / innerWidth },
      boundsHeight = symbol.boundsHeight?.let { (it * padded.height) / innerHeight },
    )
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
    if (targetWidth != decoded.width || targetHeight != decoded.height) {
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
