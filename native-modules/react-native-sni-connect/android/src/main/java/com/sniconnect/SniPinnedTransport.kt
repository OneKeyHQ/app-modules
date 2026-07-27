package com.sniconnect

import java.net.InetAddress
import java.net.Proxy
import java.net.UnknownHostException
import java.util.Locale
import java.util.concurrent.TimeUnit
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Protocol
import javax.net.ssl.HttpsURLConnection

object SniPinnedTransport {
  @JvmStatic
  fun createClient(
    ip: String,
    hostname: String,
    dispatcher: Dispatcher = Dispatcher(),
    connectionPool: ConnectionPool = ConnectionPool(),
  ): OkHttpClient {
    SniConnectValidation.validatePublicIp(ip)
    SniConnectValidation.validateHostname(hostname)
    val normalizedHostname = hostname.lowercase(Locale.US)
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
      .hostnameVerifier { _, session ->
        HttpsURLConnection.getDefaultHostnameVerifier().verify(normalizedHostname, session)
      }
      .dns(createPinnedDns(ip, normalizedHostname))
      .build()
  }

  private fun createPinnedDns(ip: String, hostname: String): Dns =
    object : Dns {
      private val pinnedAddress: InetAddress =
        SniConnectValidation.literalToInetAddress(ip)

      override fun lookup(requestedHost: String): List<InetAddress> {
        if (requestedHost.lowercase(Locale.US) == hostname) {
          return listOf(pinnedAddress)
        }
        throw UnknownHostException(
          "Unexpected host for pinned SNI request: $requestedHost"
        )
      }
    }
}
