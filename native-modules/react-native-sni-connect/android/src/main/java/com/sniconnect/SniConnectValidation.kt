package com.sniconnect

import java.net.Inet6Address
import java.net.InetAddress
import java.nio.charset.StandardCharsets
import java.util.Locale

/**
 * Boundary validation/normalization for SNI request inputs.
 *
 * The module connects to a caller-supplied IP while preserving the TLS SNI/Host of
 * `hostname`. Because the connect target is caller-controlled, every field that
 * reaches the network layer is validated here to prevent SSRF, scheme/host/port
 * override, cleartext downgrade and CR/LF header injection.
 */
internal object SniConnectValidation {

  class ValidationException(message: String) : IllegalArgumentException(message)

  const val MAX_REQUEST_ID_BYTES = 128
  const val MAX_TIMEOUT_MILLIS = 120_000L
  const val MAX_PATH_BYTES = 8 * 1024
  const val MAX_REQUEST_BODY_BYTES = 1024 * 1024
  const val MAX_RESPONSE_BODY_BYTES = 10 * 1024 * 1024L
  const val MAX_HEADER_COUNT = 64
  const val MAX_HEADER_NAME_BYTES = 128
  const val MAX_HEADER_VALUE_BYTES = 8 * 1024
  const val MAX_TOTAL_HEADER_BYTES = 32 * 1024
  const val MAX_ACTIVE_REQUESTS = 64
  const val MAX_ACTIVE_REQUESTS_PER_PAIR = 16
  const val MAX_PENDING_REQUESTS = 256

  private val ALLOWED_METHODS =
    setOf("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS")

  private val MODULE_OWNED_HEADERS = setOf(
    "host",
    "content-length",
    "accept-encoding",
    "x-emascurl-config-id",
  )
  private val UNSAFE_HEADERS = setOf(
    "connection",
    "keep-alive",
    "te",
    "trailer",
    "transfer-encoding",
    "upgrade",
    "expect",
  )

  private val HOSTNAME_REGEX = Regex(
    "^(?=.{1,253}\$)([A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)(\\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*\$"
  )
  private val IPV4_REGEX = Regex("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\$")
  private val SCHEME_REGEX = Regex("^[A-Za-z][A-Za-z0-9+.-]*:")
  private val HEADER_TOKEN_REGEX = Regex("^[!#$%&'*+.^_`|~0-9A-Za-z-]+\$")

  fun validateRequestId(requestId: String?) {
    if (requestId == null) return
    if (requestId.isEmpty() || containsControlChars(requestId) || byteSize(requestId) > MAX_REQUEST_ID_BYTES) {
      throw ValidationException("Invalid requestId")
    }
  }

  fun validateTimeout(timeoutMillis: Long) {
    if (timeoutMillis < 1L || timeoutMillis > MAX_TIMEOUT_MILLIS) {
      throw ValidationException("Invalid timeout: $timeoutMillis")
    }
  }

  fun parseTimeoutMillis(rawTimeoutMillis: Double): Long {
    if (!rawTimeoutMillis.isFinite() ||
      rawTimeoutMillis < 1.0 ||
      rawTimeoutMillis > MAX_TIMEOUT_MILLIS.toDouble()
    ) {
      throw ValidationException("Invalid timeout: $rawTimeoutMillis")
    }
    return rawTimeoutMillis.toLong()
  }

  fun validateBody(body: String?) {
    if (body != null && byteSize(body) > MAX_REQUEST_BODY_BYTES) {
      throw ValidationException("Request body too large")
    }
  }

  fun validateMethodBody(method: String, body: String?) {
    if ((method == "GET" || method == "HEAD") && body != null) {
      throw ValidationException("Body not allowed for method: $method")
    }
    if ((method == "POST" || method == "PUT" || method == "PATCH") && body == null) {
      throw ValidationException("Body required for method: $method")
    }
  }

  fun normalizeMethod(method: String): String {
    if (containsControlChars(method)) {
      throw ValidationException("Invalid method: $method")
    }
    val upper = method.trim().uppercase(Locale.US)
    if (upper !in ALLOWED_METHODS) {
      throw ValidationException("Invalid method: $method")
    }
    return upper
  }

  fun validateHostname(hostname: String) {
    if (
      hostname.isEmpty() ||
      hostname.length > 253 ||
      !HOSTNAME_REGEX.matches(hostname) ||
      isIpLiteral(hostname)
    ) {
      throw ValidationException("Invalid hostname: $hostname")
    }
  }

  /** Must be a relative path/query only — reject absolute/protocol-relative URLs and control chars. */
  fun normalizePath(path: String): String {
    val trimmed = path.trim()
    if (containsControlChars(trimmed)) {
      throw ValidationException("Invalid path")
    }
    if (byteSize(trimmed) > MAX_PATH_BYTES) {
      throw ValidationException("Path too large")
    }
    if (trimmed.contains("://") || trimmed.startsWith("//") || SCHEME_REGEX.containsMatchIn(trimmed.take(64).substringBefore('/'))) {
      throw ValidationException("Invalid path: absolute URLs are not allowed")
    }
    if (trimmed.isEmpty()) return "/"
    return if (trimmed.startsWith("/")) trimmed else "/$trimmed"
  }

  fun validateHeaders(headers: Map<String, String>) {
    normalizeHeaders(headers)
  }

  fun normalizeHeaders(headers: Map<String, String>): Map<String, String> {
    if (headers.size > MAX_HEADER_COUNT) {
      throw ValidationException("Too many headers")
    }

    var totalBytes = 0
    val normalizedHeaders = linkedMapOf<String, String>()
    for ((key, value) in headers) {
      val keyBytes = byteSize(key)
      val valueBytes = byteSize(value)
      totalBytes += keyBytes + valueBytes

      if (
        key.isEmpty() ||
        containsControlChars(key) ||
        containsControlChars(value) ||
        keyBytes > MAX_HEADER_NAME_BYTES ||
        valueBytes > MAX_HEADER_VALUE_BYTES ||
        !HEADER_TOKEN_REGEX.matches(key)
      ) {
        throw ValidationException("Invalid header: $key")
      }

      val lowerKey = key.lowercase(Locale.US)
      if (lowerKey.startsWith(":") || lowerKey.startsWith("proxy-") || lowerKey in UNSAFE_HEADERS) {
        throw ValidationException("Unsafe header: $key")
      }
      if (lowerKey in MODULE_OWNED_HEADERS) {
        continue
      }
      normalizedHeaders[key] = value
    }
    if (totalBytes > MAX_TOTAL_HEADER_BYTES) {
      throw ValidationException("Headers too large")
    }
    return normalizedHeaders
  }

  private fun containsControlChars(s: String): Boolean =
    s.any { it.code < 0x20 || it.code == 0x7F }

  private fun byteSize(s: String): Int = s.toByteArray(StandardCharsets.UTF_8).size

  private fun isIpLiteral(value: String): Boolean {
    val octets = IPV4_REGEX.matchEntire(value)?.groupValues?.drop(1)?.map { it.toInt() }
    if (octets != null && octets.all { it <= 255 }) return true
    if (!value.contains(':')) return false
    return try {
      InetAddress.getByName(value) is Inet6Address
    } catch (_: Exception) {
      false
    }
  }

  /**
   * Validate `ip` is a literal IPv4/IPv6 address (never a hostname) routing to a
   * public/global-unicast destination. Rejects loopback, private, link-local
   * (incl. 169.254.169.254 metadata), CGNAT, multicast and reserved ranges.
   */
  fun validatePublicIp(ip: String) {
    if (
      ip.isEmpty() ||
      ip.trim() != ip ||
      ip.contains('[') ||
      ip.contains(']') ||
      ip.contains('%')
    ) {
      throw ValidationException("Invalid IP: $ip")
    }
    val octetStrings = IPV4_REGEX.matchEntire(ip)?.groupValues?.drop(1)
    if (octetStrings != null) {
      if (octetStrings.any { it.length > 1 && it.startsWith('0') }) {
        throw ValidationException("Invalid IP: $ip")
      }
      val octets = octetStrings.map { it.toInt() }
      if (octets.any { it > 255 }) throw ValidationException("Invalid IP: $ip")
      if (isForbiddenIpv4(octets)) throw ValidationException("Forbidden IP: $ip")
      return
    }
    // IPv6: only treat as literal if it contains ':' (avoids any DNS lookup).
    if (ip.contains(':')) {
      val addr: InetAddress = try {
        InetAddress.getByName(ip)
      } catch (e: Exception) {
        throw ValidationException("Invalid IP: $ip")
      }
      if (addr !is Inet6Address) throw ValidationException("Invalid IP: $ip")
      if (isForbiddenIpv6(addr)) throw ValidationException("Forbidden IP: $ip")
      return
    }
    throw ValidationException("Invalid IP: $ip")
  }

  fun canonicalizePublicIp(ip: String): String {
    validatePublicIp(ip)
    return literalToInetAddress(ip).hostAddress
  }

  private fun isForbiddenIpv4(o: List<Int>): Boolean {
    val a = o[0]; val b = o[1]; val c = o[2]; val d = o[3]
    return when {
      a == 0 -> true                              // 0.0.0.0/8
      a == 10 -> true                             // 10/8 private
      a == 127 -> true                            // 127/8 loopback
      a == 100 && (b and 0xC0) == 0x40 -> true     // 100.64/10 CGNAT
      a == 169 && b == 254 -> true                // 169.254/16 link-local + metadata
      a == 172 && b in 16..31 -> true             // 172.16/12 private
      a == 192 && b == 168 -> true                // 192.168/16 private
      a == 192 && b == 0 && c == 0 -> true         // 192.0.0/24
      a == 192 && b == 0 && c == 2 -> true         // 192.0.2/24 TEST-NET-1
      a == 198 && (b == 18 || b == 19) -> true     // 198.18/15 benchmarking
      a == 198 && b == 51 && c == 100 -> true      // 198.51.100/24 TEST-NET-2
      a == 203 && b == 0 && c == 113 -> true       // 203.0.113/24 TEST-NET-3
      a >= 224 -> true                            // 224/4 multicast + 240/4 reserved + broadcast
      else -> false
    }
  }

  private fun isForbiddenIpv6(addr: Inet6Address): Boolean {
    if (addr.isAnyLocalAddress || addr.isLoopbackAddress || addr.isLinkLocalAddress ||
      addr.isSiteLocalAddress || addr.isMulticastAddress
    ) {
      return true
    }
    val bytes = addr.address
    // Unique local fc00::/7
    if ((u(bytes[0]) and 0xFE) == 0xFC) return true
    // Discard-only 100::/64
    if (u(bytes[0]) == 0x01 && u(bytes[1]) == 0x00 && (2..7).all { u(bytes[it]) == 0 }) return true
    // IETF protocol assignments that should not be accepted as public endpoints.
    if (u(bytes[0]) == 0x20 && u(bytes[1]) == 0x01) {
      if (u(bytes[2]) == 0x00 && u(bytes[3]) == 0x00) return true // 2001::/32 Teredo
      if (u(bytes[2]) == 0x00 && (u(bytes[3]) and 0xF0) == 0x10) return true // 2001:10::/28 ORCHID
      if (u(bytes[2]) == 0x00 && u(bytes[3]) == 0x02) return true // 2001:2::/48 benchmarking
      if (u(bytes[2]) == 0x0D && u(bytes[3]) == 0xB8) return true // 2001:db8::/32 docs
    }
    // 6to4 embeds an IPv4 route target and is deprecated; reject it outright.
    if (u(bytes[0]) == 0x20 && u(bytes[1]) == 0x02) return true // 2002::/16
    // NAT64 well-known prefix. Allow only when the embedded IPv4 is public.
    if (isNat64WellKnown(bytes)) return isForbiddenIpv4(embeddedIpv4(bytes, 12))
    // NAT64 local-use prefix can route through operator-specific private policy.
    if (isNat64LocalUse(bytes)) return true
    // Deprecated IPv4-compatible IPv6 addresses.
    if (isIpv4Compatible(bytes)) return true
    // IPv4-mapped ::ffff:a.b.c.d — validate the embedded IPv4
    if (isIpv4Mapped(bytes)) {
      return isForbiddenIpv4(embeddedIpv4(bytes, 12))
    }
    return false
  }

  private fun u(byte: Byte): Int = byte.toInt() and 0xFF

  private fun embeddedIpv4(bytes: ByteArray, offset: Int): List<Int> =
    listOf(u(bytes[offset]), u(bytes[offset + 1]), u(bytes[offset + 2]), u(bytes[offset + 3]))

  private fun isIpv4Mapped(bytes: ByteArray): Boolean =
    (0..9).all { u(bytes[it]) == 0 } && u(bytes[10]) == 0xFF && u(bytes[11]) == 0xFF

  private fun isIpv4Compatible(bytes: ByteArray): Boolean =
    (0..11).all { u(bytes[it]) == 0 }

  private fun isNat64WellKnown(bytes: ByteArray): Boolean =
    u(bytes[0]) == 0x00 &&
      u(bytes[1]) == 0x64 &&
      u(bytes[2]) == 0xFF &&
      u(bytes[3]) == 0x9B &&
      (4..11).all { u(bytes[it]) == 0 }

  private fun isNat64LocalUse(bytes: ByteArray): Boolean =
    u(bytes[0]) == 0x00 &&
      u(bytes[1]) == 0x64 &&
      u(bytes[2]) == 0xFF &&
      u(bytes[3]) == 0x9B &&
      u(bytes[4]) == 0x00 &&
      u(bytes[5]) == 0x01

  /** Parse a validated IPv4/IPv6 literal into an InetAddress without DNS resolution. */
  fun literalToInetAddress(ip: String): InetAddress {
    val octets = IPV4_REGEX.matchEntire(ip)?.groupValues?.drop(1)?.map { it.toInt().toByte() }
    if (octets != null) {
      return InetAddress.getByAddress(byteArrayOf(octets[0], octets[1], octets[2], octets[3]))
    }
    return InetAddress.getByName(ip) // safe: already validated as an IPv6 literal
  }
}
