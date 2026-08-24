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
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import java.io.IOException
import java.io.InterruptedIOException
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
  if (hasCause(error, InterruptedIOException::class.java)) {
    return "SNI_REQUEST_TIMEOUT"
  }
  if (hasCause(error, SSLException::class.java)) {
    return "SNI_TLS_FAILED"
  }
  return "SNI_REQUEST_FAILED"
}

internal fun classifySniResponseFailureCode(
  error: Throwable,
  explicitlyCancelled: Boolean,
): String = when {
  explicitlyCancelled -> "SNI_CANCELLED"
  hasCause(error, InterruptedIOException::class.java) -> "SNI_REQUEST_TIMEOUT"
  else -> "SNI_RESPONSE_FAILED"
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

    // Native resources are process-shared across the main and background RN runtimes.
    private val sharedDispatcher = Dispatcher().apply {
      maxRequests = SniConnectValidation.MAX_ACTIVE_REQUESTS
      maxRequestsPerHost = SniConnectValidation.MAX_ACTIVE_REQUESTS
    }
    private val sharedAdmission = SniConnectRequestAdmission()
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
      val shouldEvict = size > MAX_CLIENTS
      if (shouldEvict) {
        SniConnectLogger.info(
          SniConnectLogger.event(
            "sni_cache_evict",
            "hostname" to eldest.key.hostname,
            "ipHash" to SniConnectLogger.shortHash(eldest.key.ip),
            "cacheSize" to size,
            "limit" to MAX_CLIENTS,
            "reason" to "max_cached_clients",
          ),
        )
      }
      return shouldEvict
    }
  }

  private class ManagedRequest(
    val call: Call,
    val settled: AtomicBoolean,
  ) {
    lateinit var admissionTicket: SniConnectRequestAdmission.Ticket
    private val explicitlyCancelled = AtomicBoolean(false)

    fun cancel() {
      explicitlyCancelled.set(true)
      if (!admissionTicket.cancelPending()) {
        call.cancel()
      }
    }

    fun release() {
      admissionTicket.release()
    }

    fun wasExplicitlyCancelled(): Boolean = explicitlyCancelled.get()
  }

  // Cancellation ownership is per module, so cancelAllRequests only affects its RN runtime.
  private val activeCalls = ConcurrentHashMap<String, ManagedRequest>()
  private val allActiveCalls = Collections.newSetFromMap(
    ConcurrentHashMap<ManagedRequest, Boolean>(),
  )
  private val activeCallsLock = Any()
  private var invalidated = false

  override fun getName(): String = NAME

  override fun invalidate() {
    val cancelledCount = cancelOwnedRequests(markInvalidated = true)
    SniConnectLogger.info(
      SniConnectLogger.event(
        "sni_lifecycle",
        "action" to "runtime_invalidate",
        "cancelledCount" to cancelledCount,
        "success" to true,
      ),
    )
    super.invalidate()
  }

  override fun request(config: ReadableMap, promise: Promise) {
    try {
      val requestConfig = config.toRequestConfig()
      performRequest(requestConfig, promise)
    } catch (error: Exception) {
      SniConnectLogger.error(
        SniConnectLogger.event(
          "sni_request_result",
          "result" to "error",
          "code" to "SNI_INVALID_CONFIG",
          "nativeErrorClass" to error.javaClass.simpleName,
          "stage" to "parse_config",
        ),
      )
      promise.reject("SNI_INVALID_CONFIG", error.message, error)
    }
  }

  @ReactMethod
  override fun cancelRequest(requestId: String, promise: Promise) {
    val request = synchronized(activeCallsLock) {
      activeCalls.remove(requestId)
    }
    if (request != null) {
      request.cancel()
      SniConnectLogger.info(
        SniConnectLogger.event(
          "sni_cancel",
          "requestIdHash" to SniConnectLogger.shortHash(requestId),
          "success" to true,
        ),
      )
      promise.resolve(Arguments.createMap().apply { putBoolean("success", true) })
    } else {
      SniConnectLogger.warn(
        SniConnectLogger.event(
          "sni_cancel",
          "requestIdHash" to SniConnectLogger.shortHash(requestId),
          "success" to false,
        ),
      )
      promise.resolve(Arguments.createMap().apply { putBoolean("success", false) })
    }
  }

  @ReactMethod
  override fun cancelAllRequests(promise: Promise) {
    val cancelledCount = cancelOwnedRequests()
    SniConnectLogger.info(
      SniConnectLogger.event(
        "sni_cancel_all",
        "cancelledCount" to cancelledCount,
        "success" to true,
      ),
    )
    promise.resolve(Arguments.createMap().apply { putBoolean("success", true) })
  }

  @ReactMethod
  override fun clearDNSCache(promise: Promise) {
    synchronized(clientCache) {
      clientCache.clear()
    }
    // Drop pinned-IP connections from the shared pool.
    sharedConnectionPool.evictAll()
    SniConnectLogger.info(
      SniConnectLogger.event(
        "sni_lifecycle",
        "action" to "clear_dns_cache",
        "success" to true,
      ),
    )
    promise.resolve(Arguments.createMap().apply { putBoolean("success", true) })
  }

  @ReactMethod
  override fun getDebugSnapshot(target: ReadableMap, promise: Promise) {
    try {
      val ip = target.getString("ip") ?: throw IllegalArgumentException("ip is required")
      val hostname = target.getString("hostname")
        ?: throw IllegalArgumentException("hostname is required")
      val canonicalIp = SniConnectValidation.canonicalizePublicIp(ip)
      SniConnectValidation.validateHostname(hostname)
      val snapshot = sharedAdmission.snapshot(hostname, canonicalIp)

      promise.resolve(Arguments.createMap().apply {
        putInt("activeRequests", snapshot.activeRequests)
        putInt("activeRequestsForPair", snapshot.activeRequestsForPair)
        putInt("pendingRequests", snapshot.pendingRequests)
        putInt("pendingRequestsForPair", snapshot.pendingRequestsForPair)
        putArray("activeRequestIdsForPair", snapshot.activeRequestIdsForPair.toWritableArray())
        putArray("pendingRequestIdsForPair", snapshot.pendingRequestIdsForPair.toWritableArray())
      })
    } catch (error: Exception) {
      promise.reject("SNI_INVALID_CONFIG", error.message, error)
    }
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
    val startedAtMs = android.os.SystemClock.elapsedRealtime()
    val startedAtNanos = System.nanoTime()
    var registeredRequest: ManagedRequest? = null
    try {
      val client = getOrCreateClient(config)
      val request = buildRequest(config)
      val call = client.newCall(request)
      val settled = AtomicBoolean(false)
      lateinit var managedRequest: ManagedRequest

      SniConnectLogger.info(
        SniConnectLogger.event(
          "sni_request_start",
          "requestIdHash" to SniConnectLogger.shortHash(config.requestId),
          "hostname" to config.hostname.lowercase(Locale.US),
          "ipHash" to SniConnectLogger.shortHash(config.ip),
          "ipFamily" to SniConnectLogger.ipFamily(config.ip),
          "method" to config.method,
          "timeoutMs" to config.timeoutMillis,
          "headerCount" to config.headers.size,
          "bodyBytes" to (config.body?.toByteArray(StandardCharsets.UTF_8)?.size ?: 0),
        ),
      )

      val callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          unregisterRequest(config.requestId, managedRequest)
          managedRequest.release()
          if (!settled.compareAndSet(false, true)) return

          if (managedRequest.wasExplicitlyCancelled()) {
            promise.reject("SNI_CANCELLED", "Request cancelled", null)
          } else {
            val code = classifySniFailureCode(e)
            SniConnectLogger.error(
              SniConnectLogger.event(
                "sni_request_result",
                "result" to "error",
                "code" to code,
                "nativeErrorClass" to e.javaClass.simpleName,
                "requestIdHash" to SniConnectLogger.shortHash(config.requestId),
                "hostname" to config.hostname.lowercase(Locale.US),
                "ipHash" to SniConnectLogger.shortHash(config.ip),
                "ipFamily" to SniConnectLogger.ipFamily(config.ip),
                "method" to config.method,
                "timeoutMs" to config.timeoutMillis,
                "elapsedMs" to SniConnectLogger.elapsedMs(startedAtMs),
              ),
            )
            promise.reject(code, e.message, e)
          }
        }

        override fun onResponse(call: Call, response: Response) {
          try {
            response.use { currentResponse ->
              val bodyString = currentResponse.body.safeString()
              val headerMaps = headersToMaps(currentResponse.headers)
              val resultLog = SniConnectLogger.event(
                "sni_request_result",
                "result" to "response",
                "status" to currentResponse.code,
                "requestIdHash" to SniConnectLogger.shortHash(config.requestId),
                "hostname" to config.hostname.lowercase(Locale.US),
                "ipHash" to SniConnectLogger.shortHash(config.ip),
                "ipFamily" to SniConnectLogger.ipFamily(config.ip),
                "method" to config.method,
                "timeoutMs" to config.timeoutMillis,
                "responseBytes" to bodyString.toByteArray(StandardCharsets.UTF_8).size,
                "elapsedMs" to SniConnectLogger.elapsedMs(startedAtMs),
              )
              Arguments.createMap().apply {
                putString("data", bodyString)
                putInt("status", currentResponse.code)
                putString("statusText", currentResponse.message)
                putMap("headers", headerMaps.singleValueHeaders.toWritableMap())
                putMap("multiValueHeaders", headerMaps.multiValueHeaders.toWritableArrayMap())
              }.also { result ->
                if (currentResponse.code >= 400) {
                  SniConnectLogger.warn(resultLog)
                } else {
                  SniConnectLogger.info(resultLog)
                }
                if (settled.compareAndSet(false, true)) {
                  if (managedRequest.wasExplicitlyCancelled()) {
                    promise.reject("SNI_CANCELLED", "Request cancelled", null)
                  } else {
                    promise.resolve(result)
                  }
                }
              }
            }
          } catch (error: Exception) {
            if (!settled.compareAndSet(false, true)) return
            val code = classifySniResponseFailureCode(
              error,
              managedRequest.wasExplicitlyCancelled(),
            )
            if (code == "SNI_CANCELLED") {
              promise.reject(code, "Request cancelled", null)
              return
            }
            SniConnectLogger.error(
              SniConnectLogger.event(
                "sni_request_result",
                "result" to "error",
                "code" to code,
                "nativeErrorClass" to error.javaClass.simpleName,
                "requestIdHash" to SniConnectLogger.shortHash(config.requestId),
                "hostname" to config.hostname.lowercase(Locale.US),
                "ipHash" to SniConnectLogger.shortHash(config.ip),
                "ipFamily" to SniConnectLogger.ipFamily(config.ip),
                "method" to config.method,
                "timeoutMs" to config.timeoutMillis,
                "elapsedMs" to SniConnectLogger.elapsedMs(startedAtMs),
              ),
            )
            promise.reject(code, error.message, error)
          } finally {
            unregisterRequest(config.requestId, managedRequest)
            managedRequest.release()
          }
        }
      }

      val timeoutBeforeAdmission = remainingTimeoutMillis(
        config.timeoutMillis,
        startedAtNanos,
      )
      val admissionTicket = sharedAdmission.createTicket(
        hostname = config.hostname,
        ip = config.ip,
        requestId = config.requestId,
        timeoutMillis = timeoutBeforeAdmission,
        onAdmitted = { remainingTimeoutMillis ->
          try {
            call.timeout().timeout(remainingTimeoutMillis, TimeUnit.MILLISECONDS)
            call.enqueue(callback)
          } catch (error: Exception) {
            unregisterRequest(config.requestId, managedRequest)
            managedRequest.release()
            if (settled.compareAndSet(false, true)) {
              promise.reject("SNI_REQUEST_FAILED", error.message, error)
            }
          }
        },
        onPendingFailure = { code, message ->
          unregisterRequest(config.requestId, managedRequest)
          if (settled.compareAndSet(false, true)) {
            promise.reject(code, message, null)
          }
        },
      )
      managedRequest = ManagedRequest(call, settled).apply {
        this.admissionTicket = admissionTicket
      }
      registerRequest(config.requestId, managedRequest)
      registeredRequest = managedRequest
      admissionTicket.submit()
    } catch (error: Exception) {
      registeredRequest?.let { request ->
        unregisterRequest(config.requestId, request)
        request.release()
      }
      val code = when {
        error is SniConnectValidation.ValidationException -> "SNI_RESOURCE_LIMIT"
        hasCause(error, InterruptedIOException::class.java) -> "SNI_REQUEST_TIMEOUT"
        else -> "SNI_REQUEST_FAILED"
      }
      SniConnectLogger.error(
        SniConnectLogger.event(
          "sni_request_result",
          "result" to "error",
          "code" to code,
          "nativeErrorClass" to error.javaClass.simpleName,
          "requestIdHash" to SniConnectLogger.shortHash(config.requestId),
          "hostname" to config.hostname.lowercase(Locale.US),
          "ipHash" to SniConnectLogger.shortHash(config.ip),
          "ipFamily" to SniConnectLogger.ipFamily(config.ip),
          "method" to config.method,
          "timeoutMs" to config.timeoutMillis,
          "elapsedMs" to SniConnectLogger.elapsedMs(startedAtMs),
        ),
      )
      val shouldReject = registeredRequest?.settled?.compareAndSet(false, true) ?: true
      if (shouldReject) {
        promise.reject(code, error.message, error)
      }
    }
  }

  private fun registerRequest(requestId: String?, request: ManagedRequest) {
    var shouldCancel = false
    val previousRequest = synchronized(activeCallsLock) {
      if (invalidated) {
        shouldCancel = true
        return@synchronized null
      }
      val previous = requestId?.let { activeCalls.put(it, request) }
      allActiveCalls.add(request)
      previous
    }
    if (shouldCancel) {
      request.cancel()
      return
    }
    if (previousRequest != null && previousRequest != request) {
      previousRequest.cancel()
      SniConnectLogger.warn(
        SniConnectLogger.event(
          "sni_duplicate_request_id",
          "requestIdHash" to SniConnectLogger.shortHash(requestId),
          "action" to "cancel_previous",
        ),
      )
    }
  }

  private fun unregisterRequest(requestId: String?, request: ManagedRequest) {
    synchronized(activeCallsLock) {
      if (requestId != null) {
        activeCalls.remove(requestId, request)
      }
      allActiveCalls.remove(request)
    }
  }

  private fun cancelOwnedRequests(markInvalidated: Boolean = false): Int {
    val requests = synchronized(activeCallsLock) {
      if (markInvalidated) {
        invalidated = true
      }
      val snapshot = allActiveCalls.toList()
      activeCalls.clear()
      allActiveCalls.clear()
      snapshot
    }
    requests.forEach { request -> request.cancel() }
    return requests.size
  }

  private fun remainingTimeoutMillis(totalTimeoutMillis: Long, startedAtNanos: Long): Long {
    val elapsedNanos = (System.nanoTime() - startedAtNanos).coerceAtLeast(0L)
    val remainingNanos = TimeUnit.MILLISECONDS.toNanos(totalTimeoutMillis) - elapsedNanos
    if (remainingNanos <= 0L) {
      throw java.net.SocketTimeoutException("Request timed out before admission")
    }
    return ((remainingNanos + 999_999L) / 1_000_000L).coerceAtLeast(1L)
  }

  private fun getOrCreateClient(config: RequestConfig): OkHttpClient {
    val normalizedHost = config.hostname.lowercase(Locale.US)
    val key = ClientKey(
      normalizedHost,
      SniConnectValidation.canonicalizePublicIp(config.ip),
    )

    synchronized(clientCache) {
      clientCache[key]?.let { return it }

      val client = SniPinnedTransport.createClient(
        ip = config.ip,
        hostname = config.hostname,
        dispatcher = sharedDispatcher,
        connectionPool = sharedConnectionPool,
      )

      clientCache[key] = client
      SniConnectLogger.info(
        SniConnectLogger.event(
          "sni_transport_config",
          "hostname" to key.hostname,
          "ipHash" to SniConnectLogger.shortHash(key.ip),
          "ipFamily" to SniConnectLogger.ipFamily(key.ip),
          "proxyMode" to "no_proxy",
          "pinnedResolver" to true,
          "protocol" to "http1",
          "followRedirects" to false,
          "cacheSize" to clientCache.size,
        ),
      )
      return client
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
    val requestBody = config.body?.let { bodyContent ->
      val mediaType = config.headers.entries
        .firstOrNull { it.key.equals("Content-Type", ignoreCase = true) }
        ?.value
        ?.toMediaTypeOrNull()
      bodyContent.toRequestBody(mediaType)
    }

    when (method) {
      "GET" -> builder.get()
      "HEAD" -> builder.head()
      "DELETE" -> {
        if (requestBody == null) {
          builder.delete()
        } else {
          builder.delete(requestBody)
        }
      }
      else -> {
        when (method) {
          "POST" -> builder.post(requireNotNull(requestBody))
          "PUT" -> builder.put(requireNotNull(requestBody))
          "PATCH" -> builder.patch(requireNotNull(requestBody))
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

  private fun List<String>.toWritableArray() = Arguments.createArray().apply {
    forEach { value -> pushString(value) }
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

    val rawIp = getString("ip") ?: throw IllegalArgumentException("ip is required")
    val hostname = getString("hostname") ?: throw IllegalArgumentException("hostname is required")
    val method = getString("method") ?: "GET"
    val path = getString("path") ?: "/"
    val body = if (hasKey("body") && !isNull("body")) getString("body") else null

    // Validate every caller-controlled field at the boundary.
    SniConnectValidation.validateRequestId(requestId)
    val ip = SniConnectValidation.canonicalizePublicIp(rawIp)
    SniConnectValidation.validateHostname(hostname)
    val headersMap = SniConnectValidation.normalizeHeaders(rawHeadersMap)
    val normalizedMethod = SniConnectValidation.normalizeMethod(method)
    val normalizedPath = SniConnectValidation.normalizePath(path)
    SniConnectValidation.validateTimeout(timeoutMillis)
    SniConnectValidation.validateBody(body)
    SniConnectValidation.validateMethodBody(normalizedMethod, body)

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
    val startedAtMs = android.os.SystemClock.elapsedRealtime()
    val uri = try {
      URI(url)
    } catch (error: Exception) {
      SniConnectLogger.warn(
        SniConnectLogger.event(
          "proxy_preflight",
          "platform" to "android",
          "scheme" to "unknown",
          "host" to "unknown",
          "result" to "invalid_url",
          "source" to "validation",
          "proxyTypeCount" to 0,
          "elapsedMs" to SniConnectLogger.elapsedMs(startedAtMs),
        ),
      )
      throw error
    }
    val scheme = uri.scheme?.lowercase(Locale.US)
      ?: run {
        SniConnectLogger.warn(
          SniConnectLogger.event(
            "proxy_preflight",
            "platform" to "android",
            "scheme" to "unknown",
            "host" to "unknown",
            "result" to "invalid_url",
            "source" to "validation",
            "proxyTypeCount" to 0,
            "elapsedMs" to SniConnectLogger.elapsedMs(startedAtMs),
          ),
        )
        throw IllegalArgumentException("URL must include a scheme")
      }
    if (scheme != "http" && scheme != "https") {
      SniConnectLogger.warn(
        SniConnectLogger.event(
          "proxy_preflight",
          "platform" to "android",
          "scheme" to scheme,
          "host" to "unknown",
          "result" to "invalid_url",
          "source" to "validation",
          "proxyTypeCount" to 0,
          "elapsedMs" to SniConnectLogger.elapsedMs(startedAtMs),
        ),
      )
      throw IllegalArgumentException("Only http and https URLs are supported")
    }
    val host = uri.host
    if (host.isNullOrBlank()) {
      SniConnectLogger.warn(
        SniConnectLogger.event(
          "proxy_preflight",
          "platform" to "android",
          "scheme" to scheme,
          "host" to "unknown",
          "result" to "invalid_url",
          "source" to "validation",
          "proxyTypeCount" to 0,
          "elapsedMs" to SniConnectLogger.elapsedMs(startedAtMs),
        ),
      )
      throw IllegalArgumentException("URL must include a host")
    }

    val selector = ProxySelector.getDefault()
    val selectorProxies = selector?.select(uri).orEmpty()
    val selectorHasProxy = selectorProxies.any { proxy ->
      proxy != Proxy.NO_PROXY && proxy.type() != Proxy.Type.DIRECT
    }
    if (selectorHasProxy) {
      SniConnectLogger.info(
        SniConnectLogger.event(
          "proxy_preflight",
          "platform" to "android",
          "scheme" to scheme,
          "host" to host,
          "result" to true,
          "source" to "ProxySelector",
          "proxyTypeCount" to selectorProxies.map { proxy -> proxy.type().name }.toSet().size,
          "elapsedMs" to SniConnectLogger.elapsedMs(startedAtMs),
        ),
      )
      return true
    }

    val connectivityManager = reactApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
      as? ConnectivityManager
      ?: run {
        SniConnectLogger.info(
          SniConnectLogger.event(
            "proxy_preflight",
            "platform" to "android",
            "scheme" to scheme,
            "host" to host,
            "result" to false,
            "source" to "none",
            "proxyTypeCount" to selectorProxies.size,
            "elapsedMs" to SniConnectLogger.elapsedMs(startedAtMs),
          ),
        )
        return false
      }
    val activeNetworkProxy = connectivityManager.activeNetwork
      ?.let { network -> connectivityManager.getLinkProperties(network)?.httpProxy }
    if (activeNetworkProxy?.host?.isNotBlank() == true && activeNetworkProxy.port > 0) {
      SniConnectLogger.info(
        SniConnectLogger.event(
          "proxy_preflight",
          "platform" to "android",
          "scheme" to scheme,
          "host" to host,
          "result" to true,
          "source" to "LinkProperties",
          "proxyTypeCount" to selectorProxies.size,
          "elapsedMs" to SniConnectLogger.elapsedMs(startedAtMs),
        ),
      )
      return true
    }

    val defaultProxy = connectivityManager.defaultProxy
    if (defaultProxy?.host?.isNotBlank() == true && defaultProxy.port > 0) {
      SniConnectLogger.info(
        SniConnectLogger.event(
          "proxy_preflight",
          "platform" to "android",
          "scheme" to scheme,
          "host" to host,
          "result" to true,
          "source" to "defaultProxy",
          "proxyTypeCount" to selectorProxies.size,
          "elapsedMs" to SniConnectLogger.elapsedMs(startedAtMs),
        ),
      )
      return true
    }

    SniConnectLogger.info(
      SniConnectLogger.event(
        "proxy_preflight",
        "platform" to "android",
        "scheme" to scheme,
        "host" to host,
        "result" to false,
        "source" to "none",
        "proxyTypeCount" to selectorProxies.size,
        "elapsedMs" to SniConnectLogger.elapsedMs(startedAtMs),
      ),
    )
    return false
  }
}
