package com.margelo.nitro.qrscanner

import android.net.Uri
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise
import java.io.File
import java.util.concurrent.Executors

@DoNotStrip
class HybridQrImageDecoder : HybridQrImageDecoderSpec() {
  companion object {
    /** Hard ceiling on the caller's `maxPixels`. */
    private const val MAX_PIXELS_CEILING = 50_000_000L

    // One decode at a time: each holds several full-size bitmaps.
    private val executor = Executors.newSingleThreadExecutor { runnable ->
      Thread(runnable, "QrImageDecoder").apply { isDaemon = true }
    }
    private val pipeline by lazy {
      QrImageDecodingPipeline(listOfNotNull(MlKitQrEngine(), ZXingQrEngine.createOrNull()))
    }

    internal fun localFile(uri: String): File? = when {
      uri.startsWith("/") -> File(uri)
      uri.startsWith("file://") -> Uri.parse(uri).path?.let(::File)
      else -> null
    }
  }

  override fun decodeImage(uri: String, maxPixels: Double): Promise<QrImageDecodeResult> {
    val promise = Promise<QrImageDecodeResult>()
    val file = localFile(uri)
    if (file == null) {
      promise.reject(QrImageDecodeException("invalid-image", "not a local file URI"))
      return promise
    }
    if (!maxPixels.isFinite() || maxPixels < 1) {
      promise.reject(QrImageDecodeException("invalid-image", "maxPixels must be at least 1"))
      return promise
    }
    val budget = minOf(maxPixels.toLong(), MAX_PIXELS_CEILING)
    executor.execute {
      try {
        val hit = pipeline.decode(file, budget)
        promise.resolve(
          QrImageDecodeResult(
            text = hit?.text,
            engine = when (hit?.engine) {
              "mlkit" -> QrDecodeEngine.MLKIT
              "zxing" -> QrDecodeEngine.ZXING
              else -> null
            },
            pass = hit?.pass?.let { QrDecodePass.valueOf(it.uppercase()) },
          ),
        )
      } catch (error: QrImageDecodeException) {
        promise.reject(error)
      } catch (error: OutOfMemoryError) {
        promise.reject(QrImageDecodeException("resource-limit", "out of memory decoding the image"))
      } catch (error: Exception) {
        promise.reject(QrImageDecodeException("invalid-image", error.message ?: error.toString()))
      }
    }
    return promise
  }

  override fun decodeImageSymbols(uri: String, maxPixels: Double): Promise<QrImageSymbolsDecodeResult> {
    val promise = Promise<QrImageSymbolsDecodeResult>()
    val file = localFile(uri)
    if (file == null) {
      promise.reject(QrImageDecodeException("invalid-image", "not a local file URI"))
      return promise
    }
    if (!maxPixels.isFinite() || maxPixels < 1) {
      promise.reject(QrImageDecodeException("invalid-image", "maxPixels must be at least 1"))
      return promise
    }
    val budget = minOf(maxPixels.toLong(), MAX_PIXELS_CEILING)
    executor.execute {
      try {
        val hit = pipeline.decodeSymbols(file, budget)
        val symbols = hit.symbols.map { sym ->
          val bounds = if (
            sym.boundsX != null && sym.boundsY != null &&
            sym.boundsWidth != null && sym.boundsHeight != null
          ) {
            QrSymbolBounds(sym.boundsX, sym.boundsY, sym.boundsWidth, sym.boundsHeight)
          } else {
            null
          }
          QrDetectedSymbol(
            text = sym.text,
            centerX = sym.centerX,
            centerY = sym.centerY,
            bounds = bounds,
          )
        }.toTypedArray()
        promise.resolve(
          QrImageSymbolsDecodeResult(
            width = hit.width.toDouble(),
            height = hit.height.toDouble(),
            symbols = symbols,
            engine = when (hit.engine) {
              "mlkit" -> QrDecodeEngine.MLKIT
              "zxing" -> QrDecodeEngine.ZXING
              else -> null
            },
            pass = hit.pass?.let { QrDecodePass.valueOf(it.uppercase()) },
          ),
        )
      } catch (error: QrImageDecodeException) {
        promise.reject(error)
      } catch (error: OutOfMemoryError) {
        promise.reject(QrImageDecodeException("resource-limit", "out of memory decoding the image"))
      } catch (error: Exception) {
        promise.reject(QrImageDecodeException("invalid-image", error.message ?: error.toString()))
      }
    }
    return promise
  }
}
