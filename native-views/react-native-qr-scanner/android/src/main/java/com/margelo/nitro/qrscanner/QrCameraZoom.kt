package com.margelo.nitro.qrscanner

/** Pinch zoom rules for the live scanner, kept pure for unit tests. */
object QrCameraZoom {
  /**
   * Zoom never goes past this ratio: beyond it the digital crop is too soft
   * for dense codes. CameraX ratio 1.0 is the default (wide) lens.
   */
  const val MAX_RATIO = 8f

  fun clampRatio(requested: Float, minRatio: Float, maxRatio: Float): Float {
    val upper = maxOf(minRatio, minOf(maxRatio, MAX_RATIO))
    return requested.coerceIn(minRatio, upper)
  }
}
