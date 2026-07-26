package com.margelo.nitro.reactnativerangedownloader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ArtifactMaxBytesTest {
  @Test
  fun publicValidationRequiresMaxBytesToCoverExpectedSize() {
    val params = FirmwareArtifactDownloadParams(
      taskId = "task",
      leaseRef = "12d6cc28-9f8d-49ad-a4e1-7d67a5fed3f8",
      artifactId = "main",
      url = "https://downloads.example.com/main.bin",
      routeType = "domain",
      resolvedIp = null,
      expectedSize = 2048.0,
      expectedSha256 = "a".repeat(64),
      maxBytes = 1024.0,
      segmentCount = null,
      overallDeadlineSeconds = null,
    )

    assertThrows(FirmwareArtifactStoreException::class.java) {
      FirmwareArtifactDownloader.validate(params)
    }
  }

  @Test
  fun publicValidationRequiresAPositiveBoundedOverallDeadline() {
    val params = FirmwareArtifactDownloadParams(
      taskId = "task",
      leaseRef = "12d6cc28-9f8d-49ad-a4e1-7d67a5fed3f8",
      artifactId = "main",
      url = "https://downloads.example.com/main.bin",
      routeType = "domain",
      resolvedIp = null,
      expectedSize = 1024.0,
      expectedSha256 = "a".repeat(64),
      maxBytes = 1024.0,
      segmentCount = null,
      overallDeadlineSeconds = 0.0,
    )

    assertThrows(FirmwareArtifactStoreException::class.java) {
      FirmwareArtifactDownloader.validate(params)
    }
  }

  @Test
  fun publicValidationRejectsSegmentCountsThatOverflowInt() {
    val params = FirmwareArtifactDownloadParams(
      taskId = "task",
      leaseRef = "12d6cc28-9f8d-49ad-a4e1-7d67a5fed3f8",
      artifactId = "main",
      url = "https://downloads.example.com/main.bin",
      routeType = "domain",
      resolvedIp = null,
      expectedSize = 1024.0,
      expectedSha256 = "a".repeat(64),
      maxBytes = 1024.0,
      segmentCount = 4_294_967_304.0,
      overallDeadlineSeconds = null,
    )

    assertThrows(FirmwareArtifactStoreException::class.java) {
      FirmwareArtifactDownloader.validate(params)
    }
  }

  @Test
  fun publicValidationRejectsCrossPlatformAdmissionLimitViolations() {
    val base = FirmwareArtifactDownloadParams(
      taskId = "task",
      leaseRef = "12d6cc28-9f8d-49ad-a4e1-7d67a5fed3f8",
      artifactId = "main",
      url = "https://downloads.example.com/main.bin",
      routeType = "domain",
      resolvedIp = null,
      expectedSize = 1024.0,
      expectedSha256 = "a".repeat(64),
      maxBytes = 1024.0,
      segmentCount = null,
      overallDeadlineSeconds = null,
    )
    val tooLarge = (512L * 1024L * 1024L + 1).toDouble()
    listOf(
      base.copy(url = "https://downloads.example.com:444/main.bin"),
      base.copy(artifactId = "é".repeat(129)),
      base.copy(expectedSize = tooLarge, maxBytes = tooLarge),
    ).forEach { params ->
      val error = assertThrows(FirmwareArtifactStoreException::class.java) {
        FirmwareArtifactDownloader.validate(params)
      }
      assertEquals("ARTIFACT_INVALID_REQUEST", error.code)
    }
  }
}
