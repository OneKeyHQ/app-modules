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
import okhttp3.Headers
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
      .retryOnConnectionFailure(false)
      .followRedirects(false)
      .followSslRedirects(false)
      .addInterceptor { chain ->
        val state = ReplayPolicyState()
        val request = chain.request().newBuilder()
          .tag(ReplayPolicyState::class.java, state)
          .build()
        val response = chain.proceed(request)
        val original = state.originalRetryAfter
        if (original == null) response else {
          val restored = response.newBuilder()
            .headers(restoreRetryAfter(response.headers, original))
          response.networkResponse?.let {
            restored.networkResponse(
              it.newBuilder().headers(restoreRetryAfter(it.headers, original)).build()
            )
          }
          restored.build()
        }
      }
      .addNetworkInterceptor { chain ->
        val response = chain.proceed(chain.request())
        val state = chain.request().tag(ReplayPolicyState::class.java)
        val headers = response.headers
        val index = (headers.size - 1 downTo 0).firstOrNull {
          headers.name(it).equals("Retry-After", ignoreCase = true)
        }
        // OkHttp 4.12.0 follows 503/Retry-After: 0 even with connection retries
        // disabled. Affect only its internal follow-up decision; the application
        // interceptor restores the original root/network response metadata.
        if (response.code == 503 && state != null && index != null &&
          headers.value(index).matches(Regex("0+"))) {
          state.originalRetryAfter = headers.name(index) to headers.value(index)
          response.newBuilder()
            .headers(replaceHeaderAt(headers, index, headers.name(index), "1"))
            .build()
        } else response
      }
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

private class ReplayPolicyState {
  var originalRetryAfter: Pair<String, String>? = null
}

private fun replaceHeaderAt(
  headers: Headers,
  index: Int,
  name: String,
  value: String,
): Headers =
  Headers.Builder().apply {
    for (i in 0 until headers.size) {
      if (i == index) addUnsafeNonAscii(name, value)
      else addUnsafeNonAscii(headers.name(i), headers.value(i))
    }
  }.build()

private fun restoreRetryAfter(headers: Headers, original: Pair<String, String>): Headers {
  val index = (headers.size - 1 downTo 0).first {
    headers.name(it).equals("Retry-After", ignoreCase = true)
  }
  return replaceHeaderAt(headers, index, original.first, original.second)
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
