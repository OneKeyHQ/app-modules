package com.sniconnect

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.cert.CertificateException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

class SniConnectValidationTest {

  @Test
  fun acceptsValidRequestBoundaryValues() {
    SniConnectValidation.validateRequestId("req-1")
    SniConnectValidation.validateTimeout(120_000)
    SniConnectValidation.validateBody("a".repeat(1024 * 1024))
    SniConnectValidation.validatePublicIp("93.184.216.34")
    SniConnectValidation.validatePublicIp("2001:4860:4860::8888")
    SniConnectValidation.validateHostname("api.example.com")

    assertEquals("GET", SniConnectValidation.normalizeMethod(" get "))
    assertEquals("/", SniConnectValidation.normalizePath(""))
    assertEquals("/v1?q=1", SniConnectValidation.normalizePath("v1?q=1"))
  }

  @Test
  fun loggerRedactsIpLiteralsFromStructuredLogs() {
    val log = SniConnectLogger.event(
      "sni_request_result",
      "errorMessage" to "connect ECONNREFUSED 93.184.216.34:443",
      "ipv6Error" to "connect [2001:4860:4860::8888]:443",
      "timestamp" to "10:12:35",
    )

    assertTrue(log.contains("errorMessage=connect_ECONNREFUSED_<ip>:443"))
    assertTrue(log.contains("ipv6Error=connect_<ip6>:443"))
    assertTrue(log.contains("timestamp=10:12:35"))
    assertFalse(log.contains("93.184.216.34"))
    assertFalse(log.contains("2001:4860:4860::8888"))
  }

  @Test
  fun rejectsIpLiteralHostnames() {
    assertValidationFails { SniConnectValidation.validateHostname("93.184.216.34") }
    assertValidationFails { SniConnectValidation.validateHostname("2001:4860:4860::8888") }
  }

  @Test
  fun rejectsMalformedHostnames() {
    listOf(
      "",
      "-example.com",
      "example-.com",
      "example..com",
      "bad_host.example",
      "https://example.com",
      "example.com:443",
      "${"a".repeat(64)}.example.com",
      "${"a".repeat(250)}.com",
    ).forEach { hostname ->
      assertValidationFails { SniConnectValidation.validateHostname(hostname) }
    }
  }

  @Test
  fun rejectsUnsafeIpv4Destinations() {
    listOf(
      "example.com",
      "93.184.216.34:443",
      " 93.184.216.34",
      "10.0.0.1",
      "127.0.0.1",
      "100.64.0.1",
      "169.254.169.254",
      "172.16.0.1",
      "192.168.1.1",
      "192.0.2.1",
      "198.18.0.1",
      "198.51.100.1",
      "203.0.113.1",
      "224.0.0.1",
      "255.255.255.255",
    ).forEach { ip ->
      assertValidationFails { SniConnectValidation.validatePublicIp(ip) }
    }
  }

  @Test
  fun rejectsUnsafeIpv6DestinationsAndTransitionForms() {
    listOf(
      "::",
      "::1",
      "fe80::1",
      "fc00::1",
      "ff00::1",
      "100::1",
      "2001::1",
      "2001:2::1",
      "2001:db8::1",
      "2002:0a00:0001::1",
      "::ffff:10.0.0.1",
      "64:ff9b::10.0.0.1",
      "64:ff9b:1::1",
      "2001:4860:4860::8888%en0",
      "[2001:4860:4860::8888]",
    ).forEach { ip ->
      assertValidationFails { SniConnectValidation.validatePublicIp(ip) }
    }
  }

  @Test
  fun rejectsUnsupportedMethodsAndUnsafePaths() {
    listOf("TRACE", "CONNECT", "", "GET\n").forEach { method ->
      assertValidationFails { SniConnectValidation.normalizeMethod(method) }
    }

    listOf(
      "https://example.com",
      "http://example.com",
      "//example.com/path",
      "javascript:alert(1)",
      "/path\nInjected: yes",
      "/${"a".repeat(8192)}",
    ).forEach { path ->
      assertValidationFails { SniConnectValidation.normalizePath(path) }
    }
  }

  @Test
  fun enforcesRequestIdTimeoutAndBodyLimits() {
    assertValidationFails { SniConnectValidation.validateRequestId("") }
    assertValidationFails { SniConnectValidation.validateRequestId("x".repeat(129)) }
    assertValidationFails { SniConnectValidation.validateRequestId("req\n1") }
    assertValidationFails { SniConnectValidation.validateTimeout(0) }
    assertValidationFails { SniConnectValidation.validateTimeout(120_001) }
    assertEquals(1L, SniConnectValidation.parseTimeoutMillis(1.0))
    assertEquals(120_000L, SniConnectValidation.parseTimeoutMillis(120_000.0))
    assertValidationFails { SniConnectValidation.parseTimeoutMillis(Double.NaN) }
    assertValidationFails { SniConnectValidation.parseTimeoutMillis(Double.POSITIVE_INFINITY) }
    assertValidationFails { SniConnectValidation.parseTimeoutMillis(Double.NEGATIVE_INFINITY) }
    assertValidationFails { SniConnectValidation.parseTimeoutMillis(0.0) }
    assertValidationFails { SniConnectValidation.parseTimeoutMillis(0.5) }
    assertValidationFails { SniConnectValidation.parseTimeoutMillis(120_000.1) }
    assertValidationFails { SniConnectValidation.validateBody("a".repeat(1024 * 1024 + 1)) }
  }

  @Test
  fun filtersModuleOwnedHeadersAndRejectsUnsafeHeaders() {
    val normalized = SniConnectValidation.normalizeHeaders(
      mapOf(
        "Host" to "evil.example",
        "Content-Length" to "9999",
        "Accept-Encoding" to "gzip",
        "x-emascurl-config-id" to "evil",
        "X-Test" to "ok",
      )
    )

    assertFalse(normalized.keys.any { it.equals("host", ignoreCase = true) })
    assertFalse(normalized.keys.any { it.equals("content-length", ignoreCase = true) })
    assertFalse(normalized.keys.any { it.equals("accept-encoding", ignoreCase = true) })
    assertEquals("ok", normalized["X-Test"])

    listOf(
      mapOf("Connection" to "close"),
      mapOf("Proxy-Authorization" to "secret"),
      mapOf("Transfer-Encoding" to "chunked"),
      mapOf("Expect" to "100-continue"),
      mapOf(":authority" to "evil.example"),
      mapOf("Bad Header" to "x"),
      mapOf("X-Test" to "line\nbreak"),
      mapOf("X-Test" to "x".repeat(8 * 1024 + 1)),
    ).forEach { headers ->
      assertValidationFails { SniConnectValidation.normalizeHeaders(headers) }
    }

    assertValidationFails {
      SniConnectValidation.normalizeHeaders((0..64).associate { "X-$it" to "v" })
    }
    assertValidationFails {
      SniConnectValidation.normalizeHeaders((0..4).associate { "X-$it" to "x".repeat(7 * 1024) })
    }
  }

  @Test
  fun rejectsAmbiguousMethodBodyCombinations() {
    assertValidationFails { SniConnectValidation.validateMethodBody("GET", "") }
    assertValidationFails { SniConnectValidation.validateMethodBody("HEAD", "payload") }
    assertValidationFails { SniConnectValidation.validateMethodBody("POST", null) }
    assertValidationFails { SniConnectValidation.validateMethodBody("PUT", null) }
    assertValidationFails { SniConnectValidation.validateMethodBody("PATCH", null) }

    SniConnectValidation.validateMethodBody("POST", "")
    SniConnectValidation.validateMethodBody("DELETE", null)
    SniConnectValidation.validateMethodBody("OPTIONS", null)
  }

  @Test
  fun requestLimiterEnforcesGlobalAndPerDestinationLimits() {
    val limiter = SniConnectRequestLimiter(
      maxActiveRequests = 2,
      maxActiveRequestsPerPair = 1,
    )

    val firstToken = limiter.acquire("Example.com", "93.184.216.34")
    assertValidationFails {
      limiter.acquire("example.com", "93.184.216.34")
    }

    val secondToken = limiter.acquire("example.com", "93.184.216.35")
    assertValidationFails {
      limiter.acquire("example.net", "93.184.216.36")
    }

    firstToken.release()
    val replacementToken = limiter.acquire("example.com", "93.184.216.34")
    firstToken.release()
    secondToken.release()
    replacementToken.release()

    assertTrue(true)
  }

  @Test
  fun classifiesSecurityFailuresAsFailClosedErrorCodes() {
    assertEquals(
      "SNI_CERT_FAILED",
      classifySniFailureCode(SSLPeerUnverifiedException("hostname mismatch")),
    )
    assertEquals(
      "SNI_CERT_FAILED",
      classifySniFailureCode(SSLHandshakeException("bad cert").apply {
        initCause(CertificateException("expired"))
      }),
    )
    assertEquals(
      "SNI_TLS_FAILED",
      classifySniFailureCode(SSLHandshakeException("handshake failed")),
    )
    assertEquals(
      "SNI_SECURITY_POLICY_FAILED",
      classifySniFailureCode(UnknownHostException("Unexpected host for pinned SNI request")),
    )
    assertEquals(
      "SNI_REQUEST_TIMEOUT",
      classifySniFailureCode(SocketTimeoutException("timeout")),
    )
    assertEquals(
      "SNI_REQUEST_TIMEOUT",
      classifySniFailureCode(IOException("call timeout").apply {
        initCause(InterruptedIOException("timeout"))
      }),
    )
    assertEquals(
      "SNI_REQUEST_FAILED",
      classifySniFailureCode(IOException("connection reset")),
    )
  }

  private fun assertValidationFails(block: () -> Unit) {
    try {
      block()
    } catch (_: SniConnectValidation.ValidationException) {
      return
    }
    throw AssertionError("Expected SNI validation failure")
  }
}
