package com.margelo.nitro.reactnativerangedownloader

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory

class ArtifactObjectIdentityTest {
  private lateinit var root: File
  private lateinit var store: FirmwareArtifactStore

  @Before
  fun setUp() {
    root = createTempDirectory("firmware-identity-").toFile()
    store = FirmwareArtifactStore(root)
  }

  @After
  fun tearDown() {
    root.deleteRecursively()
  }

  @Test
  fun changedStrongValidatorCannotReuseExistingSegments() {
    val lease = store.createLease("transaction")
    val claimed = store.beginTransferAttempt(
      leaseRef = lease.leaseRef,
      taskId = "task",
      artifactId = "main",
      canonicalUrl = "https://downloads.example.com/main.bin",
      hostname = "downloads.example.com",
      route = StoredArtifactRoute.PINNED_IP,
      expectedSize = 1024,
      expectedSha256 = "a".repeat(64),
      initialDeadlineAtMillis = 10_000,
      maxRunAttempts = 8,
      nowMillis = 1,
    )
    val first = store.prepareArtifactForDownload(
      artifactRef = claimed.artifactRef,
      leaseRef = lease.leaseRef,
      taskId = "task",
      artifactId = "main",
      canonicalUrl = "https://downloads.example.com/main.bin",
      hostname = "downloads.example.com",
      route = StoredArtifactRoute.PINNED_IP,
      expectedSize = 1024,
      expectedSha256 = "a".repeat(64),
      strongETag = "\"v1\"",
      lastModified = null,
      ranges = listOf(0L..1023L),
    )
    store.segmentFile(first.artifactRef, 0).writeBytes(ByteArray(128))

    val changed = store.prepareArtifactForDownload(
      artifactRef = claimed.artifactRef,
      leaseRef = lease.leaseRef,
      taskId = "task",
      artifactId = "main",
      canonicalUrl = "https://downloads.example.com/main.bin",
      hostname = "downloads.example.com",
      route = StoredArtifactRoute.PINNED_IP,
      expectedSize = 1024,
      expectedSha256 = "a".repeat(64),
      strongETag = "\"v2\"",
      lastModified = null,
      ranges = listOf(0L..1023L),
    )

    assertEquals(first.artifactRef, changed.artifactRef)
    assertEquals(0, store.segmentFile(changed.artifactRef, 0).length())
  }

  @Test
  fun candidateIpIsNotPartOfStoredObjectIdentity() {
    val metadata = StoredArtifactMetadata.create(
      artifactRef = "12d6cc28-9f8d-49ad-a4e1-7d67a5fed3f8",
      artifactId = "main",
      canonicalUrl = "https://downloads.example.com/main.bin",
      hostname = "downloads.example.com",
      route = StoredArtifactRoute.PINNED_IP,
      expectedSize = 1024,
      expectedSha256 = "a".repeat(64),
      retentionClass = ArtifactRetentionClass.PARTIAL,
      nowMillis = 1,
    )
    assertSame(StoredArtifactRoute.PINNED_IP, metadata.route)
  }

  @Test
  fun reprobeRetainsOnlyStableObjectIdentity() {
    assertTrue(
      RangeDownloadLogic.sameArtifactObjectIdentity(
        leftSupportsRange = true,
        leftTotal = 1024,
        leftStrongETag = "\"v1\"",
        leftLastModified = null,
        rightSupportsRange = true,
        rightTotal = 1024,
        rightStrongETag = "\"v1\"",
        rightLastModified = null,
      ),
    )
    assertFalse(
      RangeDownloadLogic.sameArtifactObjectIdentity(
        leftSupportsRange = true,
        leftTotal = 1024,
        leftStrongETag = "\"v1\"",
        leftLastModified = null,
        rightSupportsRange = true,
        rightTotal = 1024,
        rightStrongETag = "\"v2\"",
        rightLastModified = null,
      ),
    )
    assertFalse(
      RangeDownloadLogic.sameArtifactObjectIdentity(
        leftSupportsRange = true,
        leftTotal = 1024,
        leftStrongETag = null,
        leftLastModified = "Mon, 01 Jan 2024 00:00:00 GMT",
        rightSupportsRange = true,
        rightTotal = 2048,
        rightStrongETag = null,
        rightLastModified = "Mon, 01 Jan 2024 00:00:00 GMT",
      ),
    )
  }
}
