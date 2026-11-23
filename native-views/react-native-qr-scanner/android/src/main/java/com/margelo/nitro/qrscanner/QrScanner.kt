package com.margelo.nitro.qrscanner

import android.graphics.Color
import android.view.View
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext

/**
 * Placeholder until the CameraX + ML Kit backend lands (see docs/SPEC.md).
 * Activating it reports `decoder-unavailable` so callers fall back instead of
 * showing a silent black preview.
 */
@DoNotStrip
class HybridQrScanner(val context: ThemedReactContext) : HybridQrScannerSpec() {

  override val view: View = View(context).apply { setBackgroundColor(Color.BLACK) }

  private var generation = 0

  override var active: Boolean = false
    set(value) {
      if (value && !field) {
        generation += 1
        onError?.invoke(
          "decoder-unavailable",
          "The Android QR scanner backend is not implemented yet",
          generation.toDouble(),
        )
      }
      field = value
    }

  override var onReady: ((generation: Double) -> Unit)? = null
  override var onFrameText: ((text: String, generation: Double) -> Unit)? = null
  override var onError: ((code: String, message: String, generation: Double) -> Unit)? = null
}
