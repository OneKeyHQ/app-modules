package com.margelo.nitro.onekeyimage

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

internal object OneKeyTosUrl {
  // Select by layout size first. Density can only choose between this tier's renditions.
  private val sizeTiers = arrayOf(
    Triple(48, 96, 160), Triple(96, 192, 320), Triple(192, 384, 640),
    Triple(384, 768, 1280), Triple(Int.MAX_VALUE, 1280, 1280),
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
    val tier = sizeTiers.first { displaySize <= it.first }
    val normalizedDensity = if (density.isFinite()) density.coerceIn(1f, MAX_DPR).toDouble() else 1.0
    val normalizedOverscan = if (overscan.isFinite()) overscan.coerceAtLeast(1.0) else 1.1
    // Fixed renditions already account for the default margin. Custom margins stay within the tier.
    val requestedDensity = normalizedDensity * (normalizedOverscan / 1.1)
    return if (requestedDensity > 2) tier.third else tier.second
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
