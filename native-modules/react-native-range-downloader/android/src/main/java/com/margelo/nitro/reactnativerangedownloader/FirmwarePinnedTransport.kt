package com.margelo.nitro.reactnativerangedownloader

import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.net.Inet6Address
import java.net.InetAddress
import java.net.Proxy
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

internal object FirmwarePinnedTransport {
  private val dispatcher = Dispatcher().apply {
    maxRequests = 64
    maxRequestsPerHost = 64
  }
  private val connectionPool = ConnectionPool()

  fun createClient(hostname: String, resolvedIp: String): OkHttpClient {
    if (!FirmwarePinnedRouteValidation.isValid(hostname, resolvedIp)) {
      throw FirmwareArtifactStoreException.invalidMetadata()
    }
    val normalizedHost = hostname.lowercase()
    return OkHttpClient.Builder()
      .dispatcher(dispatcher)
      .connectionPool(connectionPool)
      .proxy(Proxy.NO_PROXY)
      .protocols(listOf(Protocol.HTTP_1_1))
      .connectTimeout(0, TimeUnit.MILLISECONDS)
      .readTimeout(0, TimeUnit.MILLISECONDS)
      .writeTimeout(0, TimeUnit.MILLISECONDS)
      .callTimeout(0, TimeUnit.MILLISECONDS)
      .followRedirects(false)
      .followSslRedirects(false)
      .hostnameVerifier { requestedHost, session ->
        requestedHost.equals(normalizedHost, ignoreCase = true) &&
          HttpsURLConnection.getDefaultHostnameVerifier()
            .verify(normalizedHost, session)
      }
      .dns(createPinnedDns(normalizedHost, resolvedIp))
      .build()
  }

  internal fun createPinnedDns(hostname: String, resolvedIp: String): Dns {
    val address = FirmwarePinnedRouteValidation.literalAddress(resolvedIp)
    return object : Dns {
      override fun lookup(requestedHost: String): List<InetAddress> {
        if (!requestedHost.equals(hostname, ignoreCase = true)) {
          throw UnknownHostException(
            "Unexpected host for pinned firmware request"
          )
        }
        return listOf(address)
      }
    }
  }
}

internal object FirmwarePinnedRouteValidation {
  private val hostnamePattern = Regex(
    "^(?=.{1,253}\$)" +
      "([A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)" +
      "(\\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*\$"
  )
  private val ipv4Pattern =
    Regex("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\$")

  fun isValid(hostname: String, resolvedIp: String): Boolean {
    return isValidHostname(hostname) && isPublicIp(resolvedIp)
  }

  fun literalAddress(ip: String): InetAddress {
    val octets = ipv4Octets(ip)
    if (octets != null) {
      return InetAddress.getByAddress(
        byteArrayOf(
          octets[0].toByte(),
          octets[1].toByte(),
          octets[2].toByte(),
          octets[3].toByte(),
        )
      )
    }
    return InetAddress.getByName(ip)
  }

  private fun isValidHostname(hostname: String): Boolean {
    if (hostname.isEmpty() ||
      hostname.length > 253 ||
      !hostnamePattern.matches(hostname) ||
      ipv4Octets(hostname) != null ||
      hostname.contains(':')
    ) {
      return false
    }
    return true
  }

  private fun isPublicIp(ip: String): Boolean {
    if (ip.isEmpty() ||
      ip.trim() != ip ||
      ip.contains('[') ||
      ip.contains(']') ||
      ip.contains('%')
    ) {
      return false
    }
    ipv4Octets(ip)?.let { octets ->
      return !isForbiddenIpv4(octets)
    }
    if (!ip.contains(':')) {
      return false
    }
    val address = try {
      InetAddress.getByName(ip)
    } catch (_: Exception) {
      return false
    }
    return address is Inet6Address && !isForbiddenIpv6(address.address)
  }

  private fun ipv4Octets(ip: String): List<Int>? {
    val octets = ipv4Pattern.matchEntire(ip)
      ?.groupValues
      ?.drop(1)
      ?.map(String::toInt)
      ?: return null
    return octets.takeIf { values -> values.all { it <= 255 } }
  }

  private fun isForbiddenIpv4(octets: List<Int>): Boolean {
    val first = octets[0]
    val second = octets[1]
    val third = octets[2]
    return when {
      first == 0 || first == 10 || first == 127 -> true
      first == 100 && (second and 0xC0) == 0x40 -> true
      first == 169 && second == 254 -> true
      first == 172 && second in 16..31 -> true
      first == 192 && second == 168 -> true
      first == 192 && second == 0 && (third == 0 || third == 2) -> true
      first == 198 && (second == 18 || second == 19) -> true
      first == 198 && second == 51 && third == 100 -> true
      first == 203 && second == 0 && third == 113 -> true
      first >= 224 -> true
      else -> false
    }
  }

  private fun isForbiddenIpv6(bytes: ByteArray): Boolean {
    if (bytes.all { unsigned(it) == 0 }) return true
    if ((0..14).all { unsigned(bytes[it]) == 0 } &&
      unsigned(bytes[15]) == 1
    ) {
      return true
    }
    if (unsigned(bytes[0]) == 0xFF) return true
    if (unsigned(bytes[0]) == 0xFE &&
      (unsigned(bytes[1]) and 0xC0) == 0x80
    ) {
      return true
    }
    if ((unsigned(bytes[0]) and 0xFE) == 0xFC) return true
    if (unsigned(bytes[0]) == 0x01 &&
      unsigned(bytes[1]) == 0x00 &&
      (2..7).all { unsigned(bytes[it]) == 0 }
    ) {
      return true
    }
    if (unsigned(bytes[0]) == 0x20 && unsigned(bytes[1]) == 0x01) {
      if (unsigned(bytes[2]) == 0x00 &&
        unsigned(bytes[3]) == 0x00
      ) {
        return true
      }
      if (unsigned(bytes[2]) == 0x00 &&
        (unsigned(bytes[3]) and 0xF0) == 0x10
      ) {
        return true
      }
      if (unsigned(bytes[2]) == 0x00 &&
        unsigned(bytes[3]) == 0x02
      ) {
        return true
      }
      if (unsigned(bytes[2]) == 0x0D &&
        unsigned(bytes[3]) == 0xB8
      ) {
        return true
      }
    }
    if (unsigned(bytes[0]) == 0x20 && unsigned(bytes[1]) == 0x02) {
      return true
    }
    if (isNat64WellKnown(bytes)) {
      return isForbiddenIpv4(embeddedIpv4(bytes))
    }
    if (isNat64LocalUse(bytes)) return true
    if ((0..11).all { unsigned(bytes[it]) == 0 }) return true
    if ((0..9).all { unsigned(bytes[it]) == 0 } &&
      unsigned(bytes[10]) == 0xFF &&
      unsigned(bytes[11]) == 0xFF
    ) {
      return isForbiddenIpv4(embeddedIpv4(bytes))
    }
    return false
  }

  private fun isNat64WellKnown(bytes: ByteArray): Boolean =
    unsigned(bytes[0]) == 0x00 &&
      unsigned(bytes[1]) == 0x64 &&
      unsigned(bytes[2]) == 0xFF &&
      unsigned(bytes[3]) == 0x9B &&
      (4..11).all { unsigned(bytes[it]) == 0 }

  private fun isNat64LocalUse(bytes: ByteArray): Boolean =
    unsigned(bytes[0]) == 0x00 &&
      unsigned(bytes[1]) == 0x64 &&
      unsigned(bytes[2]) == 0xFF &&
      unsigned(bytes[3]) == 0x9B &&
      unsigned(bytes[4]) == 0x00 &&
      unsigned(bytes[5]) == 0x01

  private fun embeddedIpv4(bytes: ByteArray): List<Int> =
    (12..15).map { unsigned(bytes[it]) }

  private fun unsigned(byte: Byte): Int = byte.toInt() and 0xFF
}
