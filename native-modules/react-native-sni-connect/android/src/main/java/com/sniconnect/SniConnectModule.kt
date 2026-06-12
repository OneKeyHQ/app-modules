package com.sniconnect

import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.module.annotations.ReactModule
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Dns
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

private const val TAG = "SniConnect"

@ReactModule(name = SniConnectModule.NAME)
class SniConnectModule(reactContext: ReactApplicationContext) :
  NativeSniConnectSpec(reactContext) {

  companion object {
    const val NAME = "SniConnect"
    private const val EVENT_NAME = "SniConnectLog"
  }

  /**
   * Cache key for OkHttpClient instances
   * Uses hostname:IP combination to ensure:
   * - Different IPs for the same hostname are isolated (accurate speed testing)
   * - Same hostname+IP combination can reuse connections (performance optimization)
   * Note: timeout is NOT part of the key to allow connection reuse across different timeout values
   */
  private data class ClientKey(
    val hostname: String,
    val ip: String,
  )

  private val clientCache = ConcurrentHashMap<ClientKey, OkHttpClient>()
  private val activeCalls = ConcurrentHashMap<String, Call>()
  private var listenerCount = 0

  override fun getName(): String = NAME

  // Event emitter methods required for NativeEventEmitter
  @ReactMethod
  override fun addListener(eventType: String) {
    listenerCount += 1
  }

  @ReactMethod
  override fun removeListeners(count: Double) {
    listenerCount -= count.toInt()
    if (listenerCount < 0) {
      listenerCount = 0
    }
  }

  private fun sendLogEvent(level: String, message: String) {
    if (listenerCount > 0) {
      val logData = Arguments.createMap().apply {
        putString("level", level)
        putString("message", message)
        putDouble("timestamp", System.currentTimeMillis().toDouble())
      }

      reactApplicationContext
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
        ?.emit(EVENT_NAME, logData)
    }
  }

  override fun request(config: ReadableMap, promise: Promise) {
    try {
      val requestConfig = config.toRequestConfig()
      // Log module initialization
      sendLogEvent("info", "SniConnect module initialized successfully")
      performRequest(requestConfig, promise)
    } catch (error: Exception) {
      Log.e(TAG, "[SniConnect] Request failed", error)
      sendLogEvent("error", "Config parsing failed: ${error.message}")
      promise.reject("SNI_REQUEST_FAILED", error.message, error)
    }
  }

  @ReactMethod
  override fun cancelRequest(requestId: String, promise: Promise) {
    val call = activeCalls.remove(requestId)
    if (call != null) {
      call.cancel()
      sendLogEvent("info", "Cancelled request: $requestId")
      promise.resolve(Arguments.createMap().apply {
        putBoolean("success", true)
      })
    } else {
      sendLogEvent("info", "Request not found: $requestId")
      promise.resolve(Arguments.createMap().apply {
        putBoolean("success", false)
      })
    }
  }

  @ReactMethod
  override fun cancelAllRequests(promise: Promise) {
    val count = activeCalls.size
    activeCalls.forEach { (_, call) ->
      call.cancel()
    }
    activeCalls.clear()
    sendLogEvent("info", "Cancelled $count active requests")
    promise.resolve(Arguments.createMap().apply {
      putBoolean("success", true)
    })
  }

  @ReactMethod
  override fun clearDNSCache(promise: Promise) {
    clientCache.clear()
    sendLogEvent("info", "DNS cache cleared")
    promise.resolve(Arguments.createMap().apply {
      putBoolean("success", true)
    })
  }

  private fun performRequest(config: RequestConfig, promise: Promise) {
    try {
      val client = getOrCreateClient(config)
      val request = buildRequest(config)
      val call = client.newCall(request)

      // Apply per-request timeout
      call.timeout().timeout(config.timeoutMillis, TimeUnit.MILLISECONDS)

      // Register the call if requestId is provided
      config.requestId?.let { requestId ->
        activeCalls[requestId] = call
        sendLogEvent("info", "Registered request: $requestId, total active: ${activeCalls.size}")
      }

      call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          // Unregister the call
          config.requestId?.let { activeCalls.remove(it) }

          if (call.isCanceled()) {
            sendLogEvent("info", "Request cancelled")
            promise.reject("SNI_CANCELLED", "Request cancelled", null)
          } else {
            Log.e(TAG, "[SniConnect] Request failed", e)
            sendLogEvent("error", "Request failed: ${e.message}")
            promise.reject("SNI_REQUEST_FAILED", e.message, e)
          }
        }

        override fun onResponse(call: Call, response: Response) {
          // Unregister the call
          config.requestId?.let { activeCalls.remove(it) }

          try {
            response.use {
              val bodyString = response.body.safeString()
              val headerMap = headersToMap(response.headers)

              val result: WritableMap = Arguments.createMap().apply {
                putString("data", bodyString)
                putInt("status", response.code)
                putString("statusText", response.message)
                putMap("headers", headerMap.toWritableMap())
              }

              promise.resolve(result)
            }
          } catch (error: Exception) {
            Log.e(TAG, "[SniConnect] Response processing failed", error)
            sendLogEvent("error", "Response processing failed: ${error.message}")
            promise.reject("SNI_RESPONSE_FAILED", error.message, error)
          }
        }
      })
    } catch (error: Exception) {
      Log.e(TAG, "[SniConnect] Request setup failed", error)
      sendLogEvent("error", "Request setup failed: ${error.message}")
      promise.reject("SNI_REQUEST_FAILED", error.message, error)
    }
  }

  private fun getOrCreateClient(config: RequestConfig): OkHttpClient {
    val normalizedHost = config.hostname.lowercase(Locale.US)
    val key = ClientKey(normalizedHost, config.ip)

    return clientCache.getOrPut(key) {
      // Note: timeout is not part of the cache key to allow connection reuse
      // Use reasonable default timeouts for the client
      // Per-request timeout is applied via call.timeout() in performRequest
      val defaultTimeout = 60_000L // 60 seconds

      OkHttpClient.Builder()
        .connectTimeout(defaultTimeout, TimeUnit.MILLISECONDS)
        .readTimeout(defaultTimeout, TimeUnit.MILLISECONDS)
        .writeTimeout(defaultTimeout, TimeUnit.MILLISECONDS)
        .callTimeout(0, TimeUnit.MILLISECONDS) // Disable client-level call timeout
        .hostnameVerifier { _, session ->
          HttpsURLConnection.getDefaultHostnameVerifier().verify(config.hostname, session)
        }
        .dns(createPinnedDns(config.ip, config.hostname))
        .build()
    }
  }

  private fun createPinnedDns(ip: String, hostname: String): Dns =
    object : Dns {
      private val expectedHost = hostname.lowercase(Locale.US)

      override fun lookup(requestedHost: String): List<InetAddress> {
        return if (requestedHost.lowercase(Locale.US) == expectedHost) {
          listOf(resolveIp(ip))
        } else {
          Dns.SYSTEM.lookup(requestedHost)
        }
      }
    }

  private fun buildRequest(config: RequestConfig): Request {
    val normalizedPath = if (config.path.startsWith("http")) {
      config.path
    } else {
      val prefix = if (config.path.startsWith("/")) "" else "/"
      "https://${config.hostname}$prefix${config.path}"
    }

    val builder = Request.Builder().url(normalizedPath)

    config.headers.forEach { (key, value) ->
      if (!key.equals("host", ignoreCase = true)) {
        builder.addHeader(key, value)
      }
    }
    builder.header("Host", config.hostname)

    val method = config.method.uppercase(Locale.US)
    val bodyContent = config.body ?: ""
    val mediaType = config.headers.entries
      .firstOrNull { it.key.equals("Content-Type", ignoreCase = true) }
      ?.value
      ?.toMediaTypeOrNull()
      ?: "application/json; charset=utf-8".toMediaTypeOrNull()

    when (method) {
      "GET" -> builder.get()
      "HEAD" -> builder.head()
      else -> {
        val requestBody = bodyContent.toRequestBody(mediaType)
        when (method) {
          "POST" -> builder.post(requestBody)
          "PUT" -> builder.put(requestBody)
          "PATCH" -> builder.patch(requestBody)
          "DELETE" -> builder.delete(requestBody)
          else -> builder.method(method, requestBody)
        }
      }
    }

    return builder.build()
  }

  private fun ResponseBody?.safeString(): String {
    if (this == null) {
      return ""
    }
    return try {
      this.string()
    } catch (error: IOException) {
      Log.e(TAG, "[SniConnect] Failed to read response body", error)
      throw IOException("Failed to read response body", error)
    }
  }

  private fun headersToMap(headers: Headers): Map<String, String> {
    val map = mutableMapOf<String, String>()
    for (name in headers.names()) {
      map[name] = headers[name] ?: ""
    }
    return map
  }

  private fun resolveIp(ip: String): InetAddress {
    return try {
      InetAddress.getByName(ip)
    } catch (error: UnknownHostException) {
      throw IOException("Invalid IP address: $ip", error)
    }
  }

  private fun Map<String, String>.toWritableMap(): WritableMap {
    return Arguments.createMap().apply {
      forEach { (key, value) ->
        putString(key, value)
      }
    }
  }

  private fun ReadableMap.toRequestConfig(): RequestConfig {
    val headersMap = if (hasKey("headers") && !isNull("headers")) {
      val headersReadable = getMap("headers")
      headersReadable?.toHashMap()
        ?.mapValues { (_, value) -> value?.toString() ?: "" }
        ?: emptyMap()
    } else {
      emptyMap()
    }

    val timeoutMillis = if (hasKey("timeout")) {
      (getDouble("timeout") * 1.0).toLong()
    } else {
      30_000L
    }

    val requestId = if (hasKey("requestId") && !isNull("requestId")) {
      getString("requestId")
    } else {
      null
    }

    return RequestConfig(
      requestId = requestId,
      ip = getString("ip") ?: throw IllegalArgumentException("ip is required"),
      hostname = getString("hostname") ?: throw IllegalArgumentException("hostname is required"),
      method = getString("method") ?: "GET",
      path = getString("path") ?: "/",
      headers = headersMap,
      body = if (hasKey("body") && !isNull("body")) getString("body") else null,
      timeoutMillis = timeoutMillis,
    )
  }

  private data class RequestConfig(
    val requestId: String?,
    val ip: String,
    val hostname: String,
    val method: String,
    val path: String,
    val headers: Map<String, String>,
    val body: String?,
    val timeoutMillis: Long,
  )
}
