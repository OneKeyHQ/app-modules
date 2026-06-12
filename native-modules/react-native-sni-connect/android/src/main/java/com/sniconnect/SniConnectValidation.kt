package com.sniconnect

import java.net.Inet6Address
import java.net.InetAddress
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

  private val ALLOWED_METHODS =
    setOf("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS")

  private val HOSTNAME_REGEX = Regex(
    "^(?=.{1,253}\$)([A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)(\\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*\$"
  )
  private val IPV4_REGEX = Regex("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\$")
  private val SCHEME_REGEX = Regex("^[A-Za-z][A-Za-z0-9+.-]*:")

  fun normalizeMethod(method: String): String {
    val upper = method.trim().uppercase(Locale.US)
    if (upper !in ALLOWED_METHODS) {
      throw ValidationException("Invalid method: $method")
    }
    return upper
  }

  fun validateHostname(hostname: String) {
    if (hostname.isEmpty() || hostname.length > 253 || !HOSTNAME_REGEX.matches(hostname)) {
      throw ValidationException("Invalid hostname: $hostname")
    }
  }

  /** Must be a relative path/query only — reject absolute/protocol-relative URLs and control chars. */
  fun normalizePath(path: String): String {
    val trimmed = path.trim()
    if (containsControlChars(trimmed)) {
      throw ValidationException("Invalid path")
    }
    if (trimmed.contains("://") || trimmed.startsWith("//") || SCHEME_REGEX.containsMatchIn(trimmed.take(64).substringBefore('/'))) {
      throw ValidationException("Invalid path: absolute URLs are not allowed")
    }
    if (trimmed.isEmpty()) return "/"
    return if (trimmed.startsWith("/")) trimmed else "/$trimmed"
  }

  fun validateHeaders(headers: Map<String, String>) {
    for ((key, value) in headers) {
      if (key.isEmpty() || containsControlChars(key) || containsControlChars(value)) {
        throw ValidationException("Invalid header: $key")
      }
    }
  }

  private fun containsControlChars(s: String): Boolean =
    s.any { it.code < 0x20 || it.code == 0x7F }

  /**
   * Validate `ip` is a literal IPv4/IPv6 address (never a hostname) routing to a
   * public/global-unicast destination. Rejects loopback, private, link-local
   * (incl. 169.254.169.254 metadata), CGNAT, multicast and reserved ranges.
   */
  fun validatePublicIp(ip: String) {
    val octets = IPV4_REGEX.matchEntire(ip)?.groupValues?.drop(1)?.map { it.toInt() }
    if (octets != null) {
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
    if ((bytes[0].toInt() and 0xFE) == 0xFC) return true
    // IPv4-mapped ::ffff:a.b.c.d — validate the embedded IPv4
    val mappedPrefixZero = (0..9).all { bytes[it].toInt() == 0 }
    if (mappedPrefixZero && (bytes[10].toInt() and 0xFF) == 0xFF && (bytes[11].toInt() and 0xFF) == 0xFF) {
      return isForbiddenIpv4(
        listOf(
          bytes[12].toInt() and 0xFF,
          bytes[13].toInt() and 0xFF,
          bytes[14].toInt() and 0xFF,
          bytes[15].toInt() and 0xFF,
        )
      )
    }
    return false
  }

  /** Parse a validated IPv4/IPv6 literal into an InetAddress without DNS resolution. */
  fun literalToInetAddress(ip: String): InetAddress {
    val octets = IPV4_REGEX.matchEntire(ip)?.groupValues?.drop(1)?.map { it.toInt().toByte() }
    if (octets != null) {
      return InetAddress.getByAddress(byteArrayOf(octets[0], octets[1], octets[2], octets[3]))
    }
    return InetAddress.getByName(ip) // safe: already validated as an IPv6 literal
  }
}
