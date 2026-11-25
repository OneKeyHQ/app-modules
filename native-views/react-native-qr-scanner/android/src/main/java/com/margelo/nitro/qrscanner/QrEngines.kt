package com.margelo.nitro.qrscanner

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import zxingcpp.BarcodeReader

/** Bundled ML Kit model: works without Google Play Services. */
class MlKitQrEngine : QrImageEngine {
  override val name = "mlkit"

  private val scanner = BarcodeScanning.getClient(
    BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build(),
  )

  override fun decode(bitmap: Bitmap): String? {
    val barcodes = Tasks.await(scanner.process(InputImage.fromBitmap(bitmap, 0)))
    return barcodes.firstNotNullOfOrNull { it.rawValue?.takeIf(String::isNotEmpty) }
  }

  override fun decodeSymbols(bitmap: Bitmap): List<QrPipelineSymbol> {
    val barcodes = Tasks.await(scanner.process(InputImage.fromBitmap(bitmap, 0)))
    val width = bitmap.width.toDouble()
    val height = bitmap.height.toDouble()
    return barcodes.mapNotNull { barcode ->
      val text = barcode.rawValue?.takeIf(String::isNotEmpty) ?: return@mapNotNull null
      val box = barcode.boundingBox
      if (box != null && width > 0 && height > 0) {
        QrPipelineSymbol(
          text = text,
          centerX = box.exactCenterX() / width,
          centerY = box.exactCenterY() / height,
          boundsX = box.left / width,
          boundsY = box.top / height,
          boundsWidth = box.width() / width,
          boundsHeight = box.height() / height,
        )
      } else {
        QrPipelineSymbol(text = text, centerX = 0.5, centerY = 0.5)
      }
    }
  }
}

/**
 * ZXing-C++ fallback. The AAR ships arm64-v8a and armeabi-v7a only; on other
 * ABIs the library does not load and this engine is left out.
 */
class ZXingQrEngine private constructor(
  private val reader: BarcodeReader,
  private val multiReader: BarcodeReader,
) : QrImageEngine {
  override val name = "zxing"

  override fun decode(bitmap: Bitmap): String? =
    reader.read(bitmap, Rect(), 0).firstNotNullOfOrNull { it.text?.takeIf(String::isNotEmpty) }

  override fun decodeSymbols(bitmap: Bitmap): List<QrPipelineSymbol> {
    val width = bitmap.width.toDouble()
    val height = bitmap.height.toDouble()
    return multiReader.read(bitmap, Rect(), 0).mapNotNull { result ->
      val text = result.text?.takeIf(String::isNotEmpty) ?: return@mapNotNull null
      val pos = result.position
      if (width > 0 && height > 0) {
        val xs = doubleArrayOf(
          pos.topLeft.x.toDouble(),
          pos.topRight.x.toDouble(),
          pos.bottomRight.x.toDouble(),
          pos.bottomLeft.x.toDouble(),
        )
        val ys = doubleArrayOf(
          pos.topLeft.y.toDouble(),
          pos.topRight.y.toDouble(),
          pos.bottomRight.y.toDouble(),
          pos.bottomLeft.y.toDouble(),
        )
        val minX = xs.min()
        val maxX = xs.max()
        val minY = ys.min()
        val maxY = ys.max()
        QrPipelineSymbol(
          text = text,
          centerX = xs.sum() / (4.0 * width),
          centerY = ys.sum() / (4.0 * height),
          boundsX = minX / width,
          boundsY = minY / height,
          boundsWidth = (maxX - minX).coerceAtLeast(0.0) / width,
          boundsHeight = (maxY - minY).coerceAtLeast(0.0) / height,
        )
      } else {
        QrPipelineSymbol(text = text, centerX = 0.5, centerY = 0.5)
      }
    }
  }

  companion object {
    fun createOrNull(): ZXingQrEngine? = try {
      val options = BarcodeReader.Options().apply {
        formats = setOf(BarcodeReader.Format.QR_CODE)
        tryHarder = true
        tryRotate = true
        // The pipeline has its own inverted pass.
        tryInvert = false
        maxNumberOfSymbols = 1
      }
      val multiOptions = BarcodeReader.Options().apply {
        formats = setOf(BarcodeReader.Format.QR_CODE)
        tryHarder = true
        tryRotate = true
        tryInvert = false
        maxNumberOfSymbols = 8
      }
      ZXingQrEngine(BarcodeReader(options), BarcodeReader(multiOptions))
    } catch (error: UnsatisfiedLinkError) {
      Log.w("QrScanner", "ZXing-C++ is unavailable on this ABI; using ML Kit only")
      null
    }
  }
}
