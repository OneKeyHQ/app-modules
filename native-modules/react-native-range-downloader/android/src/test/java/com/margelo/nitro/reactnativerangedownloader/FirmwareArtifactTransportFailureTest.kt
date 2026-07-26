package com.margelo.nitro.reactnativerangedownloader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import javax.net.ssl.SSLHandshakeException

class FirmwareArtifactTransportFailureTest {

  @Test
  fun `tls failures are fatal and keep resumable staging`() {
    val error = classifyFirmwareArtifactTransportFailure(
      IOException("request failed", SSLHandshakeException("untrusted")),
    )

    assertEquals("ARTIFACT_TLS_FAILED", error.code)
    assertFalse(error.retryable)
    assertFalse(error.discardStaging)
  }

  @Test
  fun `ordinary io failures remain retryable reachability failures`() {
    val error = classifyFirmwareArtifactTransportFailure(
      IOException("connection refused"),
    )

    assertEquals("ARTIFACT_NETWORK_FAILED", error.code)
    assertTrue(error.retryable)
    assertFalse(error.discardStaging)
  }

  @Test
  fun `storage failures are fatal for route selection and keep staging`() {
    val error = firmwareArtifactStorageFailure(
      "write failed",
      IOException("disk full"),
    )

    assertEquals("ARTIFACT_STORAGE_FAILED", error.code)
    assertFalse(error.retryable)
    assertFalse(error.discardStaging)
  }

  @Test
  fun `not implemented and version unsupported are permanent`() {
    assertFalse(isRetryableFirmwareArtifactHttpStatus(501))
    assertFalse(isRetryableFirmwareArtifactHttpStatus(505))
    assertTrue(isRetryableFirmwareArtifactHttpStatus(500))
    assertTrue(isRetryableFirmwareArtifactHttpStatus(503))
  }
}
