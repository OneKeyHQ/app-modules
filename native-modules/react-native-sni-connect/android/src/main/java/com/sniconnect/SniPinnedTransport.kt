package com.sniconnect

import java.io.File
import java.io.IOException
import java.net.InetAddress
import java.net.Proxy
import java.net.UnknownHostException
import java.util.Locale
import java.util.concurrent.TimeUnit
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.Dns
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.source
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

/** Reads from disk only during transmission, with a fixed, bounded body length. */
internal class SniUploadFileRequestBody(
  private val file: File,
  private val mediaType: MediaType?,
) : RequestBody() {
  private val length = file.length().also {
    if (it <= 0 || it > SniConnectValidation.MAX_UPLOAD_FILE_BYTES) {
      throw IOException("Upload file size is out of range")
    }
  }

  override fun contentType(): MediaType? = mediaType

  override fun contentLength(): Long = length

  override fun writeTo(sink: BufferedSink) {
    file.source().use { source ->
      val buffer = Buffer()
      var totalBytes = 0L
      while (true) {
        // One extra byte detects growth without ever writing beyond the bound.
        val read = source.read(buffer, minOf(8 * 1024L, length - totalBytes + 1))
        if (read == -1L) break
        if (totalBytes + read > length) {
          throw IOException("Upload file grew during transmission")
        }
        sink.write(buffer, read)
        totalBytes += read
      }
      if (totalBytes != length) {
        throw IOException("Upload file was truncated during transmission")
      }
    }
  }
}
