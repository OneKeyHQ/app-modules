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
}

/**
 * ZXing-C++ fallback. The AAR ships arm64-v8a and armeabi-v7a only; on other
 * ABIs the library does not load and this engine is left out.
 */
class ZXingQrEngine private constructor(private val reader: BarcodeReader) : QrImageEngine {
  override val name = "zxing"

  override fun decode(bitmap: Bitmap): String? =
    reader.read(bitmap, Rect(), 0).firstNotNullOfOrNull { it.text?.takeIf(String::isNotEmpty) }

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
      ZXingQrEngine(BarcodeReader(options))
    } catch (error: UnsatisfiedLinkError) {
      Log.w("QrScanner", "ZXing-C++ is unavailable on this ABI; using ML Kit only")
      null
    }
  }
}
