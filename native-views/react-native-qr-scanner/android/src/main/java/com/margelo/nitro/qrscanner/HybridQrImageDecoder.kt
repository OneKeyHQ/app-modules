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
}
