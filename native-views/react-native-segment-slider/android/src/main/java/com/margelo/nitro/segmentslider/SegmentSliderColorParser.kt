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
    // A malformed channel degrades to gray rather than the invisible TRANSPARENT
    // fallback: a wrong-but-visible element is far easier to notice and debug
    // than one that silently disappears. Percentage forms (`rgb(50%, ...)`) are
    // accepted and scaled to 0..255.
    val r = parseRgbChannel(parts[0]) ?: return DEFAULT_VISIBLE
    val g = parseRgbChannel(parts[1]) ?: return DEFAULT_VISIBLE
    val b = parseRgbChannel(parts[2]) ?: return DEFAULT_VISIBLE
    val a = if (parts.size >= 4) ((parts[3].toFloatOrNull() ?: 1f) * 255f).toInt() else 255
    return Color.argb(a.coerceIn(0, 255), r.coerceIn(0, 255), g.coerceIn(0, 255), b.coerceIn(0, 255))
  }

  /** Parses an rgb() channel, supporting both `0..255` and `0%..100%` forms. */
  private fun parseRgbChannel(part: String): Int? {
    if (part.endsWith("%")) {
      val pct = part.dropLast(1).trim().toFloatOrNull() ?: return null
      return Math.round(pct / 100f * 255f)
    }
    return part.toFloatOrNull()?.toInt()
  }

  /** Opaque mid-gray: a conservative, always-visible stand-in for a bad rgb(). */
  private val DEFAULT_VISIBLE = Color.argb(255, 128, 128, 128)
}
