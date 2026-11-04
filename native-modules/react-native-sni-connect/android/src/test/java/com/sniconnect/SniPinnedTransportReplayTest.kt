package com.sniconnect

import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.net.ConnectException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.SocketTimeoutException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.util.Collections
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPOutputStream
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import kotlin.concurrent.thread
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Dns
import okhttp3.EventListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Real TLS/HTTP tests of the production transport factory. Only the local test
 * destination, certificate trust, JVM hostname verifier and deadlines differ.
 * These tests do not exercise the Android bridge or production IP/port guards.
 */
class SniPinnedTransportReplayTest {
  @Test
  fun pooledPostMustNotReplayAfterServerConsumesBodyAndDropsResponse() {
    runScenario(Mode.DROP_RESPONSE, warm = true).use { result ->
      assertEquals(result.trace, 1, result.server.postCount.get())
      assertEquals(result.trace, 1, result.events.bodyEnds.get())
      assertEquals(result.trace, 0, result.events.connectStarts.get())
    }
  }

  @Test
  fun consumedPostMustNotReconnectAfterResponseLoss() {
    runScenario(Mode.CLOSE_LISTENER, warm = true).use { result ->
      assertEquals(result.trace, 1, result.server.postCount.get())
      assertEquals(result.trace, 1, result.events.bodyEnds.get())
      assertEquals(result.trace, 0, result.events.connectStarts.get())
      assertNotNull(result.trace, result.failure)
      assertFalse(result.trace, result.failure is ConnectException)
    }
  }

  @Test
  fun freshSingleRoutePostHasNoUnusedRouteAfterItsResponseIsLost() {
    runScenario(Mode.DROP_RESPONSE, warm = false).use { result ->
      assertEquals(result.trace, 1, result.server.postCount.get())
      assertNotNull(result.trace, result.failure)
    }
  }

  @Test
  fun disablingConnectionRetryPreventsThePooledPostRecovery() {
    runScenario(Mode.DROP_RESPONSE, warm = true, retry = false).use { result ->
      assertEquals(result.trace, 1, result.server.postCount.get())
      assertNotNull(result.trace, result.failure)
    }
  }

  @Test
  fun pooledGetMustNotReplayWhenTheResponseIsLost() = assertSingleLoss("GET")

  @Test
  fun pooledHeadMustNotReplayWhenTheResponseIsLost() = assertSingleLoss("HEAD")

  @Test
  fun pooledOptionsMustNotReplayWhenTheResponseIsLost() = assertSingleLoss("OPTIONS")

  @Test
  fun pooledDeleteMustNotReplayWhenTheResponseIsLost() = assertSingleLoss("DELETE")

  @Test
  fun pooledPutMustNotReplayWhenTheResponseIsLost() = assertSingleLoss("PUT")

  @Test
  fun pooledPatchMustNotReplayWhenTheResponseIsLost() = assertSingleLoss("PATCH")

  private fun assertSingleLoss(method: String) {
    runScenario(Mode.DROP_RESPONSE, warm = true, method = method).use { result ->
      assertEquals(result.trace, 1, result.server.targetCount.get())
      assertEquals(result.trace, 1, result.events.entries.count { it == "requestHeadersEnd" })
      assertNotNull(result.trace, result.failure)
    }
  }

  @Test
  fun getMustNotReplayAfterRetryable503() = assertSingleStatus("GET", Mode.RETRYABLE_503, 503)

  @Test
  fun getMustNotReplayAfterRetryable408() = assertSingleStatus("GET", Mode.RETRYABLE_408, 408)

  @Test
  fun disablingConnectionRetryMustAlsoPreventGet503FollowUp() =
    assertSingleStatus("GET", Mode.RETRYABLE_503, 503, retry = false)

  private fun assertSingleStatus(method: String, mode: Mode, status: Int, retry: Boolean? = null) {
    runScenario(mode, warm = true, method = method, retry = retry).use { result ->
      assertEquals(result.trace, 1, result.server.targetCount.get())
      assertEquals(result.trace, status, result.status)
      assertEquals(result.trace, "ok", result.responseBody)
      assertEquals(result.trace, "0", result.retryAfter)
    }
  }

  @Test
  fun retryable503PreservesDuplicateHeadersAndNetworkMetadata() {
    runScenario(Mode.RETRYABLE_503_DUPLICATE, warm = true, method = "GET").use { result ->
      assertEquals(result.trace, 1, result.server.targetCount.get())
      assertEquals(result.trace, 503, result.status)
      assertEquals(result.trace, "ok", result.responseBody)
      val expected = listOf(
        "Content-Length" to "2", "Retry-After" to "7",
        "rEtRy-AfTeR" to "0", "X-Fixture" to "preserved",
      )
      assertEquals(result.trace, expected, result.responses.single().first.headers.toList())
      assertEquals(result.trace, expected, result.responses.single().first.networkResponse!!.headers.toList())
    }
  }

  @Test
  fun retryable503PreservesTransparentGzipDecoding() {
    runScenario(Mode.RETRYABLE_503_GZIP, warm = true, method = "GET").use { result ->
      assertEquals(result.trace, 1, result.server.targetCount.get())
      assertEquals(result.trace, 503, result.status)
      assertEquals(result.trace, "ok", result.responseBody)
      val response = result.responses.single().first
      assertEquals(
        result.trace,
        listOf("Retry-After" to "0", "X-Fixture" to "preserved"),
        response.headers.toList(),
      )
      assertEquals(result.trace, "gzip", response.networkResponse!!.header("Content-Encoding"))
      assertEquals(result.trace, "0", response.networkResponse!!.header("Retry-After"))
      assertNotNull(result.trace, response.networkResponse!!.header("Content-Length"))
    }
  }

  @Test
  fun concurrentRequestsDoNotShareRetryAfterState() {
    runScenario(Mode.RETRYABLE_503, warm = true, method = "GET", concurrentCalls = 2).use { result ->
      assertEquals(result.trace, 2, result.server.targetCount.get())
      assertEquals(result.trace, listOf(200, 503), result.responses.map { it.first.code }.sorted())
      result.responses.forEach { (response, body) ->
        assertEquals(result.trace, "ok", body)
        val expected = if (response.code == 503) "0" else null
        assertEquals(result.trace, expected, response.header("Retry-After"))
        assertEquals(result.trace, expected, response.networkResponse!!.header("Retry-After"))
      }
    }
  }

  @Test
  fun postMustNotReplayAfterRetryable503() {
    runScenario(Mode.RETRYABLE_503, warm = true).use { result ->
      assertEquals(result.trace, 1, result.server.postCount.get())
      assertEquals(result.trace, 503, result.status)
    }
  }

  @Test
  fun disablingConnectionRetryMustAlsoPreventPost503FollowUp() {
    runScenario(Mode.RETRYABLE_503, warm = true, retry = false).use { result ->
      assertEquals(result.trace, 1, result.server.postCount.get())
      assertEquals(result.trace, 503, result.status)
    }
  }

  @Test
  fun postMustNotReplayAfterRetryable408() {
    runScenario(Mode.RETRYABLE_408, warm = true).use { result ->
      assertEquals(result.trace, 1, result.server.postCount.get())
      assertEquals(result.trace, 408, result.status)
    }
  }

  @Test
  fun streamedFilePostMustNotReplayAfterResponseLoss() {
    runScenario(Mode.DROP_RESPONSE, warm = true, fileBody = true).use { result ->
      assertEquals(result.trace, 1, result.server.postCount.get())
      assertEquals(result.trace, 1, result.events.bodyEnds.get())
      assertEquals(result.trace, 0, result.events.connectStarts.get())
      assertNotNull(result.trace, result.failure)
    }
  }

  @Test
  fun emptyPostMustNotReplayAfterResponseLoss() {
    runScenario(Mode.DROP_RESPONSE, warm = true, payload = "").use { result ->
      assertEquals(result.trace, 1, result.server.postCount.get())
      assertEquals(result.trace, 1, result.events.bodyEnds.get())
      assertEquals(result.trace, 0, result.events.connectStarts.get())
      assertNotNull(result.trace, result.failure)
    }
  }

  private fun runScenario(
    mode: Mode,
    warm: Boolean,
    retry: Boolean? = null,
    method: String = "POST",
    fileBody: Boolean = false,
    payload: String = "demo",
    concurrentCalls: Int = 1,
  ): Result {
    val server = LocalTlsServer(
      mode, if (method in setOf("POST", "PUT", "PATCH")) payload else "",
    )
    val events = RequestEvents()
    val factoryClient = SniPinnedTransport.createClient("93.184.216.34", TEST_HOST)
    val builder = factoryClient.newBuilder()
      .dns(object : Dns {
        override fun lookup(hostname: String) = listOf(InetAddress.getLoopbackAddress())
      })
      .sslSocketFactory(server.clientContext.socketFactory, server.trustManager)
      .hostnameVerifier { hostname, session ->
        hostname == TEST_HOST &&
          session.peerCertificates.firstOrNull() == server.certificate &&
          server.certificate.subjectAlternativeNames.any {
            it[0] == 2 && it[1] == TEST_HOST
          }
      }
      .callTimeout(5, TimeUnit.SECONDS)
      .connectTimeout(2, TimeUnit.SECONDS)
      .readTimeout(2, TimeUnit.SECONDS)
      .writeTimeout(2, TimeUnit.SECONDS)
      .eventListener(events)
    if (retry != null) builder.retryOnConnectionFailure(retry)
    val client = builder.build()
    val result = Result(server, client, events)
    try {
      if (warm) {
        client.newCall(Request.Builder().url(server.url("/warmup")).build())
          .execute().use { response ->
            assertEquals(200, response.code)
            response.body!!.string()
          }
      }
      val request = Request.Builder().url(server.url("/target"))
        .apply {
          method(method, if (method in setOf("POST", "PUT", "PATCH")) {
            if (fileBody) server.fileBody() else payload.toRequestBody()
          } else null)
        }
        .build()
      val calls = (1..concurrentCalls).map {
        thread(name = "sni-fixture-call", isDaemon = true) {
          try {
            client.newCall(request).execute().use { response ->
              result.responses += response to response.body?.string()
            }
          } catch (error: IOException) {
            result.failure = error
          }
        }
      }
      calls.forEach { it.join(6000) }
      assertFalse("all fixture calls must finish", calls.any { it.isAlive })
      if (concurrentCalls == 1 && result.responses.isNotEmpty()) {
        val (response, body) = result.responses.single()
        result.status = response.code
        result.responseBody = body
        result.retryAfter = response.header("Retry-After")
      }
      assertTrue("target request must reach the local server", server.targetCount.get() > 0)
      assertTrue(
        "server must receive the requested method: ${server.received}",
        server.received.all { it.startsWith("$method:") },
      )
      if (warm) {
        assertTrue("target must reuse the warmed connection: ${events.entries}", events.reusedWarmConnection)
      }
      println(result.trace)
      return result
    } catch (error: Throwable) {
      result.close()
      throw error
    }
  }

  private enum class Mode {
    DROP_RESPONSE, CLOSE_LISTENER, RETRYABLE_503, RETRYABLE_408,
    RETRYABLE_503_DUPLICATE, RETRYABLE_503_GZIP,
  }

  private class RequestEvents : EventListener() {
    val entries = Collections.synchronizedList(mutableListOf<String>())
    val bodyEnds = AtomicInteger()
    val connectStarts = AtomicInteger()
    private var warmConnection: Connection? = null
    var reusedWarmConnection = false
      private set

    override fun connectionAcquired(call: Call, connection: Connection) {
      if (call.request().url.encodedPath == "/warmup") warmConnection = connection
      else if (connection === warmConnection) reusedWarmConnection = true
    }

    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: java.net.Proxy) {
      if (call.request().url.encodedPath == "/target") {
        connectStarts.incrementAndGet()
        entries += "connectStart"
      }
    }

    override fun requestHeadersEnd(call: Call, request: Request) {
      if (call.request().url.encodedPath == "/target") entries += "requestHeadersEnd"
    }

    override fun requestBodyEnd(call: Call, byteCount: Long) {
      if (call.request().url.encodedPath == "/target") {
        bodyEnds.incrementAndGet()
        entries += "requestBodyEnd:$byteCount"
      }
    }

    override fun responseFailed(call: Call, ioe: IOException) {
      if (call.request().url.encodedPath == "/target") entries += "responseFailed:${ioe.javaClass.simpleName}"
    }
  }

  private class Result(
    val server: LocalTlsServer,
    private val client: OkHttpClient,
    val events: RequestEvents,
  ) : Closeable {
    var status: Int? = null
    var responseBody: String? = null
    var retryAfter: String? = null
    @Volatile var failure: IOException? = null
    val responses = Collections.synchronizedList(mutableListOf<Pair<okhttp3.Response, String?>>())
    val trace: String
      get() = "mode=${server.mode}, requests=${server.received}, POST=${server.postCount}, targetGET=${server.targetGetCount}, " +
        "status=$status, error=${failure?.javaClass?.simpleName}, " +
        "suppressed=${failure?.suppressed?.map { it.javaClass.simpleName }}, events=${events.entries}"

    override fun close() {
      client.connectionPool.evictAll()
      client.dispatcher.executorService.shutdownNow()
      server.close()
    }
  }

  private class LocalTlsServer(val mode: Mode, private val expectedBody: String) : Closeable {
    private val directory = Files.createTempDirectory("sni-replay-tls-").toFile()
    private val password = "local-test-only".toCharArray()
    val certificate: X509Certificate
    val trustManager: X509TrustManager
    val clientContext: SSLContext
    private val listener: SSLServerSocket
    private val stopped = AtomicBoolean()
    private val sockets = Collections.synchronizedList(mutableListOf<SSLSocket>())
    val postCount = AtomicInteger()
    val targetGetCount = AtomicInteger()
    val targetCount = AtomicInteger()
    val received = Collections.synchronizedList(mutableListOf<String>())
    private val connectionWorkers = Collections.synchronizedList(mutableListOf<Thread>())
    private val worker: Thread

    init {
      val keyStoreFile = directory.resolve("server.p12")
      val keytool = java.io.File(System.getProperty("java.home"), "bin/keytool").path
      val process = ProcessBuilder(
        keytool, "-genkeypair", "-alias", "server", "-keyalg", "RSA", "-keysize", "2048",
        "-dname", "CN=$TEST_HOST", "-ext", "SAN=dns:$TEST_HOST", "-validity", "2",
        "-storetype", "PKCS12", "-keystore", keyStoreFile.path,
        "-storepass", String(password), "-keypass", String(password), "-noprompt",
      ).redirectErrorStream(true).start()
      assertTrue("keytool must finish", process.waitFor(15, TimeUnit.SECONDS))
      assertEquals("keytool: ${process.inputStream.bufferedReader().readText()}", 0, process.exitValue())
      val keys = KeyStore.getInstance("PKCS12").apply {
        keyStoreFile.inputStream().use { load(it, password) }
      }
      certificate = keys.getCertificate("server") as X509Certificate
      val keyManagers = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
        init(keys, password)
      }
      val trustStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
        load(null, null)
        setCertificateEntry("server", certificate)
      }
      val trustManagers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
        init(trustStore)
      }
      trustManager = trustManagers.trustManagers.filterIsInstance<X509TrustManager>().single()
      clientContext = SSLContext.getInstance("TLS").apply {
        init(null, arrayOf(trustManager), null)
      }
      val serverContext = SSLContext.getInstance("TLS").apply {
        init(keyManagers.keyManagers, null, null)
      }
      listener = (serverContext.serverSocketFactory.createServerSocket(
        0, 8, InetAddress.getLoopbackAddress(),
      ) as SSLServerSocket).apply { soTimeout = 250 }
      worker = thread(name = "sni-local-tls", isDaemon = true) { serve() }
    }

    fun url(path: String) = "https://$TEST_HOST:${listener.localPort}$path"

    fun fileBody(): SniUploadFileRequestBody = SniUploadFileRequestBody(
      directory.resolve("body.bin").apply { writeText(expectedBody) },
      null,
    )

    private fun serve() {
      while (!stopped.get() && !listener.isClosed) {
        val socket = try {
          listener.accept() as SSLSocket
        } catch (_: SocketTimeoutException) {
          continue
        } catch (_: IOException) {
          break
        }
        sockets += socket
        connectionWorkers += thread(name = "sni-local-tls-connection", isDaemon = true) {
          try {
            socket.soTimeout = 2000
            socket.use { serveConnection(it) }
          } catch (_: IOException) {
            // A client may close an idle connection while the fixture is shutting down.
          }
        }
      }
    }

    private fun serveConnection(socket: SSLSocket) {
      val input = socket.inputStream
      while (!stopped.get()) {
        val requestLine = readLine(input) ?: return
        val fields = requestLine.split(' ')
        val headers = mutableMapOf<String, String>()
        while (true) {
          val line = readLine(input) ?: throw EOFException("incomplete request headers")
          if (line.isEmpty()) break
          val colon = line.indexOf(':')
          require(colon > 0)
          headers[line.substring(0, colon).lowercase()] = line.substring(colon + 1).trim()
        }
        val length = headers["content-length"]?.toInt() ?: 0
        require(length in 0..1024)
        val body = ByteArray(length)
        var read = 0
        while (read < length) {
          val count = input.read(body, read, length - read)
          if (count == -1) throw EOFException("incomplete request body")
          read += count
        }
        val method = fields[0]
        val target = fields[1] == "/target"
        val attempt = if (target) {
          check(String(body, StandardCharsets.UTF_8) == expectedBody)
          check(headers["host"] == "$TEST_HOST:${listener.localPort}")
          received += "$method:${body.size}bytes"
          targetCount.incrementAndGet()
        } else 0
        if (target && method == "POST") postCount.incrementAndGet()
        if (target && method == "GET") targetGetCount.incrementAndGet()
        if (target && attempt == 1) {
          when (mode) {
            Mode.DROP_RESPONSE -> return
            Mode.CLOSE_LISTENER -> { listener.close(); return }
            Mode.RETRYABLE_503, Mode.RETRYABLE_503_DUPLICATE, Mode.RETRYABLE_503_GZIP -> {
              respond(socket, 503, method, retryAfter = true)
              continue
            }
            Mode.RETRYABLE_408 -> { respond(socket, 408, method, retryAfter = true); continue }
          }
        }
        respond(socket, 200, method)
      }
    }

    private fun respond(socket: SSLSocket, status: Int, method: String, retryAfter: Boolean = false) {
      val gzip = status == 503 && mode == Mode.RETRYABLE_503_GZIP
      val bytes = if (gzip) {
        ByteArrayOutputStream().also { output ->
          GZIPOutputStream(output).use { it.write("ok".toByteArray(StandardCharsets.US_ASCII)) }
        }.toByteArray()
      } else "ok".toByteArray(StandardCharsets.US_ASCII)
      val response = "HTTP/1.1 $status Test\r\nContent-Length: ${bytes.size}\r\n" +
        (if (gzip) "Content-Encoding: gzip\r\n" else "") +
        (if (retryAfter && mode == Mode.RETRYABLE_503_DUPLICATE) "Retry-After: 7\r\nrEtRy-AfTeR: 0\r\n" else if (retryAfter) "Retry-After: 0\r\n" else "") +
        (if (status == 503) "X-Fixture: preserved\r\n" else "") + "\r\n"
      socket.outputStream.write(response.toByteArray(StandardCharsets.US_ASCII))
      if (method != "HEAD") socket.outputStream.write(bytes)
      socket.outputStream.flush()
    }

    private fun readLine(input: InputStream): String? {
      val line = StringBuilder()
      while (line.length < 8192) {
        val value = input.read()
        if (value == -1) return if (line.isEmpty()) null else throw EOFException("incomplete line")
        if (value == '\n'.code) return line.toString().removeSuffix("\r")
        line.append(value.toChar())
      }
      throw IOException("request line too long")
    }

    override fun close() {
      stopped.set(true)
      listener.close()
      synchronized(sockets) { sockets.forEach { it.close() } }
      worker.join(3000)
      synchronized(connectionWorkers) { connectionWorkers.forEach { it.join(3000) } }
      assertFalse("local TLS connection threads must stop", connectionWorkers.any { it.isAlive })
      assertFalse("local TLS server thread must stop", worker.isAlive)
      directory.deleteRecursively()
    }
  }

  companion object {
    private const val TEST_HOST = "node.test"
  }
}
