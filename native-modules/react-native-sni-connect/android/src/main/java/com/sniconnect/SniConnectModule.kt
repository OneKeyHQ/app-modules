package com.sniconnect

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.module.annotations.ReactModule
import okhttp3.Call
import okhttp3.Callback
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
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
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.HttpsURLConnection

private const val TAG = "SniConnect"

@ReactModule(name = SniConnectModule.NAME)
class SniConnectModule(reactContext: ReactApplicationContext) :
  NativeSniConnectSpec(reactContext) {

  companion object {
    const val NAME = "SniConnect"

    // Upper bound on cached OkHttpClient instances to prevent unbounded growth
    // from JS-controlled host/IP pairs (e.g. speed-testing many endpoints).
    private const val MAX_CLIENTS = 32

    // A single dispatcher + connection pool shared across all cached clients so we
    // don't spawn a thread pool / connection pool per (hostname, ip) pair.
    private val sharedDispatcher = Dispatcher()
    private val sharedConnectionPool = ConnectionPool()
  }

  /**
   * Cache key for OkHttpClient instances.
   * Uses hostname:IP so different IPs for the same hostname stay isolated (accurate
   * speed testing) while the same hostname+IP reuses connections. Timeout is NOT part
   * of the key — it is applied per-call via `call.timeout()`.
   */
  private data class ClientKey(
    val hostname: String,
    val ip: String,
  )

  // Bounded LRU: access-ordered, evicts the eldest client when capacity is exceeded.
  // Idle connections are reclaimed by the shared ConnectionPool's own keep-alive, so
  // we do NOT evictAll here (that pool is shared by every client). Synchronized because
  // LinkedHashMap is not thread-safe.
  private val clientCache = object : LinkedHashMap<ClientKey, OkHttpClient>(16, 0.75f, true) {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<ClientKey, OkHttpClient>): Boolean {
      return size > MAX_CLIENTS
    }
  }

  private val activeCalls = ConcurrentHashMap<String, Call>()

  override fun getName(): String = NAME

  override fun request(config: ReadableMap, promise: Promise) {
    try {
      val requestConfig = config.toRequestConfig()
      performRequest(requestConfig, promise)
    } catch (error: Exception) {
      SniConnectLogger.error("Config parsing failed: ${error.message}")
      promise.reject("SNI_INVALID_CONFIG", error.message, error)
    }
  }

  @ReactMethod
  override fun cancelRequest(requestId: String, promise: Promise) {
    val call = activeCalls.remove(requestId)
    if (call != null) {
      call.cancel()
      SniConnectLogger.info("Cancelled request: $requestId")
      promise.resolve(Arguments.createMap().apply { putBoolean("success", true) })
    } else {
      promise.resolve(Arguments.createMap().apply { putBoolean("success", false) })
    }
  }

  @ReactMethod
  override fun cancelAllRequests(promise: Promise) {
    val count = activeCalls.size
    activeCalls.forEach { (_, call) -> call.cancel() }
    activeCalls.clear()
    SniConnectLogger.info("Cancelled $count active requests")
    promise.resolve(Arguments.createMap().apply { putBoolean("success", true) })
  }

  @ReactMethod
  override fun clearDNSCache(promise: Promise) {
    synchronized(clientCache) {
      clientCache.clear()
    }
    // Drop pinned-IP connections from the shared pool.
    sharedConnectionPool.evictAll()
    SniConnectLogger.info("DNS cache cleared")
    promise.resolve(Arguments.createMap().apply { putBoolean("success", true) })
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
      }

      // Guard against double-settling the promise (RN hard-crashes otherwise).
      val settled = AtomicBoolean(false)

      call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          config.requestId?.let { activeCalls.remove(it) }
          if (!settled.compareAndSet(false, true)) return

          if (call.isCanceled()) {
            promise.reject("SNI_CANCELLED", "Request cancelled", null)
          } else {
            SniConnectLogger.error("Request failed: ${e.message}")
            promise.reject("SNI_REQUEST_FAILED", e.message, e)
          }
        }

        override fun onResponse(call: Call, response: Response) {
          config.requestId?.let { activeCalls.remove(it) }

          val result: WritableMap = try {
            response.use {
              val bodyString = response.body.safeString()
              val headerMap = headersToMap(response.headers)
              Arguments.createMap().apply {
                putString("data", bodyString)
                putInt("status", response.code)
                putString("statusText", response.message)
                putMap("headers", headerMap.toWritableMap())
              }
            }
          } catch (error: Exception) {
            if (!settled.compareAndSet(false, true)) return
            SniConnectLogger.error("Response processing failed: ${error.message}")
            promise.reject("SNI_RESPONSE_FAILED", error.message, error)
            return
          }

          if (response.code >= 400) {
            SniConnectLogger.warn("HTTP ${response.code} for ${config.hostname}")
          }
          if (settled.compareAndSet(false, true)) {
            promise.resolve(result)
          }
        }
      })
    } catch (error: Exception) {
      SniConnectLogger.error("Request setup failed: ${error.message}")
      promise.reject("SNI_REQUEST_FAILED", error.message, error)
    }
  }

  private fun getOrCreateClient(config: RequestConfig): OkHttpClient {
    val normalizedHost = config.hostname.lowercase(Locale.US)
    val key = ClientKey(normalizedHost, config.ip)

    synchronized(clientCache) {
      clientCache[key]?.let { return it }

      // 60s defaults at the client level; the real deadline is the per-call timeout.
      val defaultTimeout = 60_000L

      val client = OkHttpClient.Builder()
        .dispatcher(sharedDispatcher)
        .connectionPool(sharedConnectionPool)
        .connectTimeout(defaultTimeout, TimeUnit.MILLISECONDS)
        .readTimeout(defaultTimeout, TimeUnit.MILLISECONDS)
        .writeTimeout(defaultTimeout, TimeUnit.MILLISECONDS)
        .callTimeout(0, TimeUnit.MILLISECONDS)
        // TLS is validated normally: cert chain via the default trust manager and
        // hostname verification against the REAL hostname (not the pinned IP).
        .hostnameVerifier { _, session ->
          HttpsURLConnection.getDefaultHostnameVerifier().verify(config.hostname, session)
        }
        .dns(createPinnedDns(config.ip, config.hostname))
        .build()

      clientCache[key] = client
      return client
    }
  }

  private fun createPinnedDns(ip: String, hostname: String): Dns =
    object : Dns {
      private val expectedHost = hostname.lowercase(Locale.US)
      // Resolve the literal IP once up front (validated; never triggers DNS).
      private val pinnedAddress: InetAddress = SniConnectValidation.literalToInetAddress(ip)

      override fun lookup(requestedHost: String): List<InetAddress> {
        return if (requestedHost.lowercase(Locale.US) == expectedHost) {
          listOf(pinnedAddress)
        } else {
          Dns.SYSTEM.lookup(requestedHost)
        }
      }
    }

  /**
   * Build the request. Always `https://<hostname><path>` on the implicit port 443 —
   * `path` has been validated as relative, so scheme/host/port cannot be overridden.
   */
  private fun buildRequest(config: RequestConfig): Request {
    val url = "https://${config.hostname}${config.path}"
    val builder = Request.Builder().url(url)

    config.headers.forEach { (key, value) ->
      if (!key.equals("host", ignoreCase = true)) {
        builder.addHeader(key, value)
      }
    }
    builder.header("Host", config.hostname)

    val method = config.method
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
    if (this == null) return ""
    return try {
      this.string()
    } catch (error: IOException) {
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

  private fun Map<String, String>.toWritableMap(): WritableMap {
    return Arguments.createMap().apply {
      forEach { (key, value) -> putString(key, value) }
    }
  }

  private fun ReadableMap.toRequestConfig(): RequestConfig {
    val headersMap = if (hasKey("headers") && !isNull("headers")) {
      getMap("headers")?.toHashMap()
        ?.mapValues { (_, value) -> value?.toString() ?: "" }
        ?: emptyMap()
    } else {
      emptyMap()
    }

    val timeoutMillis = if (hasKey("timeout") && !isNull("timeout")) {
      getDouble("timeout").toLong().coerceAtLeast(1L)
    } else {
      30_000L
    }

    val requestId = if (hasKey("requestId") && !isNull("requestId")) getString("requestId") else null

    val ip = getString("ip") ?: throw IllegalArgumentException("ip is required")
    val hostname = getString("hostname") ?: throw IllegalArgumentException("hostname is required")
    val method = getString("method") ?: "GET"
    val path = getString("path") ?: "/"

    // Validate every caller-controlled field at the boundary.
    SniConnectValidation.validatePublicIp(ip)
    SniConnectValidation.validateHostname(hostname)
    SniConnectValidation.validateHeaders(headersMap)
    val normalizedMethod = SniConnectValidation.normalizeMethod(method)
    val normalizedPath = SniConnectValidation.normalizePath(path)

    return RequestConfig(
      requestId = requestId,
      ip = ip,
      hostname = hostname,
      method = normalizedMethod,
      path = normalizedPath,
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
