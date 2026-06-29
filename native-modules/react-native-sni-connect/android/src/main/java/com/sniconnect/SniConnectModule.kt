package com.sniconnect

import android.content.Context
import android.net.ConnectivityManager
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
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import java.io.IOException
import java.net.InetAddress
import java.net.Proxy
import java.net.ProxySelector
import java.net.URI
import java.net.UnknownHostException
import java.nio.charset.StandardCharsets
import java.security.cert.CertificateException
import java.util.Collections
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLException
import javax.net.ssl.SSLPeerUnverifiedException

private const val TAG = "SniConnect"

internal fun classifySniFailureCode(error: Throwable): String {
  if (hasCause(error, SSLPeerUnverifiedException::class.java) ||
    hasCause(error, CertificateException::class.java)
  ) {
    return "SNI_CERT_FAILED"
  }
  if (hasCause(error, UnknownHostException::class.java)) {
    return "SNI_SECURITY_POLICY_FAILED"
  }
  if (hasCause(error, SSLException::class.java)) {
    return "SNI_TLS_FAILED"
  }
  return "SNI_REQUEST_FAILED"
}

private fun hasCause(error: Throwable, type: Class<out Throwable>): Boolean {
  var current: Throwable? = error
  while (current != null) {
    if (type.isInstance(current)) return true
    current = current.cause
  }
  return false
}

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
    private val sharedDispatcher = Dispatcher().apply {
      maxRequests = 64
      maxRequestsPerHost = 64
    }
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
  private val allActiveCalls = Collections.newSetFromMap(ConcurrentHashMap<Call, Boolean>())
  private val activeCallsLock = Any()
  private val requestLimiter = SniConnectRequestLimiter()

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
    val call = synchronized(activeCallsLock) {
      activeCalls.remove(requestId)
    }
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
    val calls = synchronized(activeCallsLock) {
      val snapshot = allActiveCalls.toList()
      activeCalls.clear()
      allActiveCalls.clear()
      snapshot
    }
    calls.forEach { call -> call.cancel() }
    SniConnectLogger.info("Cancelled ${calls.size} active requests")
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

  @ReactMethod
  override fun isProxyActiveForUrl(url: String, promise: Promise) {
    try {
      promise.resolve(isProxyActiveForUrl(url))
    } catch (error: Exception) {
      promise.reject("SNI_INVALID_URL", error.message, error)
    }
  }

  private fun performRequest(config: RequestConfig, promise: Promise) {
    var requestSlot: SniConnectRequestLimiter.Token? = null
    var registeredCall: Call? = null
    try {
      requestSlot = requestLimiter.acquire(config.hostname, config.ip)
      val client = getOrCreateClient(config)
      val request = buildRequest(config)
      val call = client.newCall(request)

      // Apply per-request timeout
      call.timeout().timeout(config.timeoutMillis, TimeUnit.MILLISECONDS)

      registerCall(config.requestId, call)
      registerActiveCall(call)
      registeredCall = call

      // Guard against double-settling the promise (RN hard-crashes otherwise).
      val settled = AtomicBoolean(false)

      call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          unregisterCall(config.requestId, call)
          unregisterActiveCall(call)
          requestSlot?.release()
          if (!settled.compareAndSet(false, true)) return

          if (call.isCanceled()) {
            promise.reject("SNI_CANCELLED", "Request cancelled", null)
          } else {
            SniConnectLogger.error("Request failed: ${e.message}")
            promise.reject(classifySniFailureCode(e), e.message, e)
          }
        }

        override fun onResponse(call: Call, response: Response) {
          try {
            response.use { currentResponse ->
              val bodyString = currentResponse.body.safeString()
              val headerMaps = headersToMaps(currentResponse.headers)
              Arguments.createMap().apply {
                putString("data", bodyString)
                putInt("status", currentResponse.code)
                putString("statusText", currentResponse.message)
                putMap("headers", headerMaps.singleValueHeaders.toWritableMap())
                putMap("multiValueHeaders", headerMaps.multiValueHeaders.toWritableArrayMap())
              }.also { result ->
                if (currentResponse.code >= 400) {
                  SniConnectLogger.warn("HTTP ${currentResponse.code} for ${config.hostname}")
                }
                if (settled.compareAndSet(false, true)) {
                  promise.resolve(result)
                }
              }
            }
          } catch (error: Exception) {
            if (!settled.compareAndSet(false, true)) return
            SniConnectLogger.error("Response processing failed: ${error.message}")
            promise.reject("SNI_RESPONSE_FAILED", error.message, error)
          } finally {
            unregisterCall(config.requestId, call)
            unregisterActiveCall(call)
            requestSlot?.release()
          }
        }
      })
    } catch (error: Exception) {
      registeredCall?.let { call ->
        unregisterCall(config.requestId, call)
        unregisterActiveCall(call)
      }
      requestSlot?.release()
      SniConnectLogger.error("Request setup failed: ${error.message}")
      val code = if (error is SniConnectValidation.ValidationException) {
        "SNI_RESOURCE_LIMIT"
      } else {
        "SNI_REQUEST_FAILED"
      }
      promise.reject(code, error.message, error)
    }
  }

  private fun registerCall(requestId: String?, call: Call) {
    if (requestId == null) return
    val previousCall = synchronized(activeCallsLock) {
      activeCalls.put(requestId, call)
    }
    if (previousCall != null && previousCall != call) {
      previousCall.cancel()
      SniConnectLogger.warn("Cancelled previous request with duplicate ID: $requestId")
    }
  }

  private fun registerActiveCall(call: Call) {
    synchronized(activeCallsLock) {
      allActiveCalls.add(call)
    }
  }

  private fun unregisterCall(requestId: String?, call: Call) {
    if (requestId == null) return
    synchronized(activeCallsLock) {
      activeCalls.remove(requestId, call)
    }
  }

  private fun unregisterActiveCall(call: Call) {
    synchronized(activeCallsLock) {
      allActiveCalls.remove(call)
    }
  }

  private fun getOrCreateClient(config: RequestConfig): OkHttpClient {
    val normalizedHost = config.hostname.lowercase(Locale.US)
    val key = ClientKey(normalizedHost, config.ip)

    synchronized(clientCache) {
      clientCache[key]?.let { return it }

      val client = OkHttpClient.Builder()
        .dispatcher(sharedDispatcher)
        .connectionPool(sharedConnectionPool)
        .proxy(Proxy.NO_PROXY)
        .protocols(listOf(Protocol.HTTP_1_1))
        .connectTimeout(0, TimeUnit.MILLISECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .writeTimeout(0, TimeUnit.MILLISECONDS)
        .callTimeout(0, TimeUnit.MILLISECONDS)
        .followRedirects(false)
        .followSslRedirects(false)
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
          throw UnknownHostException("Unexpected host for pinned SNI request: $requestedHost")
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
      builder.addHeader(key, value)
    }
    builder.header("Host", config.hostname)
    builder.header("Accept-Encoding", "identity")

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
      val source = source()
      val buffer = Buffer()
      var totalBytes = 0L
      while (true) {
        val read = source.read(buffer, 8 * 1024)
        if (read == -1L) break
        totalBytes += read
        if (totalBytes > SniConnectValidation.MAX_RESPONSE_BODY_BYTES) {
          throw IOException("Response body too large")
        }
      }
      val charset = contentType()?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
      buffer.readString(charset)
    } catch (error: IOException) {
      throw IOException("Failed to read response body", error)
    }
  }

  private fun headersToMaps(headers: Headers): HeaderMaps {
    val singleValueHeaders = linkedMapOf<String, String>()
    val multiValueHeaders = linkedMapOf<String, MutableList<String>>()

    for (index in 0 until headers.size) {
      val name = headers.name(index).lowercase(Locale.US)
      val value = headers.value(index)
      singleValueHeaders[name] = value
      multiValueHeaders.getOrPut(name) { mutableListOf() }.add(value)
    }

    return HeaderMaps(singleValueHeaders, multiValueHeaders)
  }

  private fun Map<String, String>.toWritableMap(): WritableMap {
    return Arguments.createMap().apply {
      forEach { (key, value) -> putString(key, value) }
    }
  }

  private fun Map<String, List<String>>.toWritableArrayMap(): WritableMap {
    return Arguments.createMap().apply {
      forEach { (key, values) ->
        val array = Arguments.createArray()
        values.forEach { value -> array.pushString(value) }
        putArray(key, array)
      }
    }
  }

  private fun ReadableMap.toRequestConfig(): RequestConfig {
    val rawHeadersMap = if (hasKey("headers") && !isNull("headers")) {
      getMap("headers")?.toHashMap()
        ?.mapValues { (_, value) -> value?.toString() ?: "" }
        ?: emptyMap()
    } else {
      emptyMap()
    }

    val timeoutMillis = if (hasKey("timeout") && !isNull("timeout")) {
      SniConnectValidation.parseTimeoutMillis(getDouble("timeout"))
    } else {
      30_000L
    }

    val requestId = if (hasKey("requestId") && !isNull("requestId")) getString("requestId") else null

    val ip = getString("ip") ?: throw IllegalArgumentException("ip is required")
    val hostname = getString("hostname") ?: throw IllegalArgumentException("hostname is required")
    val method = getString("method") ?: "GET"
    val path = getString("path") ?: "/"
    val body = if (hasKey("body") && !isNull("body")) getString("body") else null

    // Validate every caller-controlled field at the boundary.
    SniConnectValidation.validateRequestId(requestId)
    SniConnectValidation.validatePublicIp(ip)
    SniConnectValidation.validateHostname(hostname)
    val headersMap = SniConnectValidation.normalizeHeaders(rawHeadersMap)
    val normalizedMethod = SniConnectValidation.normalizeMethod(method)
    val normalizedPath = SniConnectValidation.normalizePath(path)
    SniConnectValidation.validateTimeout(timeoutMillis)
    SniConnectValidation.validateBody(body)

    return RequestConfig(
      requestId = requestId,
      ip = ip,
      hostname = hostname,
      method = normalizedMethod,
      path = normalizedPath,
      headers = headersMap,
      body = body,
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

  private data class HeaderMaps(
    val singleValueHeaders: Map<String, String>,
    val multiValueHeaders: Map<String, List<String>>,
  )

  private fun isProxyActiveForUrl(url: String): Boolean {
    val uri = URI(url)
    val scheme = uri.scheme?.lowercase(Locale.US)
      ?: throw IllegalArgumentException("URL must include a scheme")
    if (scheme != "http" && scheme != "https") {
      throw IllegalArgumentException("Only http and https URLs are supported")
    }
    if (uri.host.isNullOrBlank()) {
      throw IllegalArgumentException("URL must include a host")
    }

    val selector = ProxySelector.getDefault()
    val selectorHasProxy = selector?.select(uri)
      ?.any { proxy -> proxy != Proxy.NO_PROXY && proxy.type() != Proxy.Type.DIRECT }
      ?: false
    if (selectorHasProxy) return true

    val connectivityManager = reactApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
      as? ConnectivityManager
      ?: return false
    val activeNetworkProxy = connectivityManager.activeNetwork
      ?.let { network -> connectivityManager.getLinkProperties(network)?.httpProxy }
    val proxyInfo = activeNetworkProxy ?: connectivityManager.defaultProxy
    return proxyInfo?.host?.isNotBlank() == true && proxyInfo.port > 0
  }
}
