package com.margelo.nitro.segmentslider

import android.graphics.Color

/**
 * Parses color strings from JS. Supports `#rgb`/`#rrggbb`/`#rrggbbaa` and
 * `rgb(...)`/`rgba(...)` (alpha 0..1). Returns [fallback] on failure.
 *
 * Note: Android's [Color.parseColor] expects `#AARRGGBB`, while web/JS emit
 * `#RRGGBBAA`, so 8-digit hex is reordered here before parsing.
 */
object SegmentSliderColorParser {
  fun parse(value: String?, fallback: Int = Color.TRANSPARENT): Int {
    val raw = value?.trim().orEmpty()
    if (raw.isEmpty()) return fallback
    return try {
      when {
        raw.startsWith("#") -> parseHex(raw) ?: fallback
        raw.startsWith("rgb", ignoreCase = true) -> parseRgb(raw) ?: fallback
        else -> fallback
      }
    } catch (e: Exception) {
      fallback
    }
  }

  private fun parseHex(hex: String): Int? {
    var s = hex.substring(1)
    if (s.length == 3) {
      s = s.map { "$it$it" }.joinToString("")
    }
    return when (s.length) {
      6 -> Color.parseColor("#$s")
      8 -> {
        // #RRGGBBAA (JS) -> #AARRGGBB (Android)
        val rgb = s.substring(0, 6)
        val a = s.substring(6, 8)
        Color.parseColor("#$a$rgb")
      }
      else -> null
    }
  }

  private fun parseRgb(value: String): Int? {
    val open = value.indexOf('(')
    val close = value.indexOf(')')
    if (open < 0 || close < 0 || close <= open) return null
    val parts = value.substring(open + 1, close).split(",").map { it.trim() }
    if (parts.size < 3) return null
    val r = parts[0].toFloatOrNull()?.toInt() ?: return null
    val g = parts[1].toFloatOrNull()?.toInt() ?: return null
    val b = parts[2].toFloatOrNull()?.toInt() ?: return null
    val a = if (parts.size >= 4) ((parts[3].toFloatOrNull() ?: 1f) * 255f).toInt() else 255
    return Color.argb(a.coerceIn(0, 255), r.coerceIn(0, 255), g.coerceIn(0, 255), b.coerceIn(0, 255))
  }
}
