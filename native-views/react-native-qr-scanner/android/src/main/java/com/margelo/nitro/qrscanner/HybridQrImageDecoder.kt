package com.margelo.nitro.qrscanner

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise

/** Placeholder until the ML Kit + ZXing-C++ image backend lands. */
@DoNotStrip
class HybridQrImageDecoder : HybridQrImageDecoderSpec() {
  override fun decodeImage(uri: String, maxPixels: Double): Promise<QrImageDecodeResult> {
    val promise = Promise<QrImageDecodeResult>()
    promise.reject(
      UnsupportedOperationException(
        "decoder-unavailable: the Android image decoder is not implemented yet",
      ),
    )
    return promise
  }
}
