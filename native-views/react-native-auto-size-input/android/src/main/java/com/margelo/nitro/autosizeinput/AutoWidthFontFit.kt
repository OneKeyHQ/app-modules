package com.margelo.nitro.autosizeinput

// Font fit for contentAutoWidth mode. The input text, prefix,
// suffix and their safety padding all scale with the font, so every candidate
// size must be measured as a whole. Measuring the side labels once at the
// previous font size under-reports their width whenever the font grows (e.g.
// the send-amount fiat toggle adds a "$" prefix and drops the token suffix) and
// the layout pass then hands the input a slot narrower than its text, which
// scrolls the leading digits out of view.
internal object AutoWidthFontFit {
  fun findLargestFittingSize(
    minSize: Float,
    maxSize: Float,
    availableWidth: Float,
    measureAt: (Float) -> Float
  ): Float {
    if (availableWidth <= 0f || maxSize <= minSize) return minSize
    var low = minSize
    var high = maxSize
    while (high - low > 0.5f) {
      val mid = (low + high) / 2f
      if (measureAt(mid) <= availableWidth) {
        low = mid
      } else {
        high = mid
      }
    }
    return low
  }
}
