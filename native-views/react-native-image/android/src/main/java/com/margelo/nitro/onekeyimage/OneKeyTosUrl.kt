package com.margelo.nitro.onekeyimage

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.math.ceil

internal object OneKeyTosUrl {
  internal val widthBuckets = intArrayOf(
    32, 40, 48, 64, 96, 128, 160, 200, 256, 320, 480, 640, 960, 1280,
  )

  fun optimized(
    rawUrl: String,
    displaySize: Int,
    density: Float,
    overscan: Double,
    hasCustomIdentity: Boolean,
  ): String {
    if (hasCustomIdentity || displaySize <= 0) return rawUrl
    val uri = try {
      URI(rawUrl)
    } catch (_: Exception) {
      return rawUrl
    }
    val host = uri.host?.lowercase() ?: return rawUrl
    if (host !in ALLOWED_HOSTS) return rawUrl
    if (unsupportedExtension(uri.path.orEmpty())) return rawUrl
    if (hasProtectedQuery(uri.rawQuery)) return rawUrl

    val bucket = selectWidthBucket(displaySize, density, overscan)
    val fragmentIndex = rawUrl.indexOf('#')
    val base = if (fragmentIndex >= 0) rawUrl.substring(0, fragmentIndex) else rawUrl
    val fragment = if (fragmentIndex >= 0) rawUrl.substring(fragmentIndex) else ""
    val separator = when {
      uri.rawQuery == null -> "?"
      base.endsWith("?") || base.endsWith("&") -> ""
      else -> "&"
    }
    return "$base${separator}x-tos-process=image%2Fresize%2Cw_$bucket$fragment"
  }

  internal fun selectWidthBucket(displaySize: Int, density: Float, overscan: Double): Int {
    val normalizedDensity = if (density.isFinite()) density.coerceIn(1f, MAX_DPR) else 1f
    val normalizedOverscan = if (overscan.isFinite()) overscan.coerceAtLeast(1.0) else 1.0
    val requestedPixels = ceil(displaySize * normalizedDensity * normalizedOverscan)
    if (!requestedPixels.isFinite() || requestedPixels >= widthBuckets.last()) {
      return widthBuckets.last()
    }
    val requested = requestedPixels.toInt()
    return widthBuckets.firstOrNull { it >= requested } ?: widthBuckets.last()
  }

  private fun unsupportedExtension(path: String): Boolean {
    val extension = path.substringAfterLast('.', "").lowercase()
    return extension in UNSUPPORTED_EXTENSIONS
  }

  internal fun hasProtectedQuery(rawQuery: String?): Boolean {
    if (rawQuery.isNullOrEmpty()) return false
    return rawQuery.split('&').any { pair ->
      val rawName = pair.substringBefore('=')
      val name = try {
        URLDecoder.decode(rawName, StandardCharsets.UTF_8.name())
      } catch (_: Exception) {
        rawName
      }.lowercase()
      name in PROTECTED_QUERY_KEYS || PROTECTED_QUERY_PREFIXES.any(name::startsWith)
    }
  }

  private val ALLOWED_HOSTS = setOf(
    "app-assets.onekey.so",
    "common.onekey-asset.com",
    "asset.onekey-asset.com",
    "uni.onekey-asset.com",
    "uni-test.onekey-asset.com",
  )

  private val PROTECTED_QUERY_KEYS = setOf(
    "expires",
    "policy",
    "signature",
    "token",
    "auth_key",
    "accesskeyid",
    "ossaccesskeyid",
    "security-token",
  )
  private val PROTECTED_QUERY_PREFIXES = arrayOf("x-amz-", "x-oss-", "x-tos-")
  private val UNSUPPORTED_EXTENSIONS = setOf("svg", "mp4", "webm", "m4v", "mov", "avi")
  private const val MAX_DPR = 3f
}
