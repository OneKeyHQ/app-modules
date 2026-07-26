package com.margelo.nitro.reactnativerangedownloader

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FirmwareArtifactLeaseTest {

  @get:Rule
  val temporaryFolder = TemporaryFolder()

  private lateinit var rootDirectory: File
  private lateinit var store: FirmwareArtifactStore

  @Before
  fun setUp() {
    rootDirectory = temporaryFolder.newFolder("firmware-artifacts")
    store = FirmwareArtifactStore(rootDirectory)
    FirmwareArtifactStore.resetProcessInstanceForTests()
  }

  @After
  fun tearDown() {
    FirmwareArtifactStore.resetProcessInstanceForTests()
  }

  @Test
  fun `active lease blocks discard and release only makes artifact eligible`() {
    val artifact = makeArtifact(nowMillis = 1)
    val lease = store.createLease(transactionId = "transaction-1", nowMillis = 2)
    store.retainArtifact(
      leaseRef = lease.leaseRef,
      artifactRef = artifact.artifactRef,
      nowMillis = 3,
    )

    val leased = assertThrows(FirmwareArtifactStoreException::class.java) {
      store.discardArtifact(artifact.artifactRef, nowMillis = 4)
    }
    assertEquals("ARTIFACT_LEASED", leased.code)

    store.releaseLease(
      leaseRef = lease.leaseRef,
      disposition = StoredLeaseDisposition.COMPLETED,
      nowMillis = 5,
    )
    assertNotNull(store.loadArtifact(artifact.artifactRef))
    store.discardArtifact(artifact.artifactRef, nowMillis = 6)
    val missing = assertThrows(FirmwareArtifactStoreException::class.java) {
      store.loadArtifact(artifact.artifactRef)
    }
    assertEquals("ARTIFACT_NOT_FOUND", missing.code)
  }

  @Test
  fun `reader activity blocks deletion until native handle closes`() {
    val artifact = makeArtifact(nowMillis = 1)
    val reader = store.beginActivity(
      artifactRef = artifact.artifactRef,
      kind = ArtifactActivityKind.READER,
    )

    val leased = assertThrows(FirmwareArtifactStoreException::class.java) {
      store.discardArtifact(artifact.artifactRef, nowMillis = 2)
    }
    assertEquals("ARTIFACT_LEASED", leased.code)

    reader.close()
    store.discardArtifact(artifact.artifactRef, nowMillis = 3)
  }

  @Test
  fun `integrity quarantine detaches an inactive artifact from its active lease`() {
    val artifact = makeArtifact(nowMillis = 1)
    val lease = store.createLease(transactionId = "transaction-1", nowMillis = 2)
    store.retainArtifact(
      leaseRef = lease.leaseRef,
      artifactRef = artifact.artifactRef,
      nowMillis = 3,
    )

    store.quarantineArtifact(artifact.artifactRef, nowMillis = 4)

    val missing = assertThrows(FirmwareArtifactStoreException::class.java) {
      store.loadArtifact(artifact.artifactRef)
    }
    assertEquals("ARTIFACT_NOT_FOUND", missing.code)
    store.releaseLease(
      leaseRef = lease.leaseRef,
      disposition = StoredLeaseDisposition.SAFE_ABANDONED,
      nowMillis = 5,
    )
  }

  @Test
  fun `sidecar stores canonical url hash without signed query`() {
    val signedQuery = "token=secret-value"
    val artifact = store.createArtifact(
      artifactId = "firmware-main",
      canonicalUrl = "https://downloads.example.com/firmware.bin?$signedQuery",
      hostname = "downloads.example.com",
      route = StoredArtifactRoute.PINNED_IP,
      expectedSize = 1024,
      expectedSha256 = "a".repeat(64),
      retentionClass = ArtifactRetentionClass.PARTIAL,
      nowMillis = 1,
    )
    val sidecar = File(
      File(File(rootDirectory, "artifacts"), artifact.artifactRef),
      "metadata.properties",
    ).readText()

    assertFalse(sidecar.contains(signedQuery))
    assertFalse(sidecar.contains("secret-value"))
    assertTrue(sidecar.contains(artifact.canonicalUrlHash))
  }

  @Test
  fun `partial identity uses validators and not socket route`() {
    val artifact = makeArtifact(nowMillis = 1).apply {
      strongETag = "\"version-1\""
    }

    assertTrue(
      artifact.canReusePartial(
        canonicalUrl = "https://downloads.example.com/firmware.bin",
        hostname = "downloads.example.com",
        route = StoredArtifactRoute.DOMAIN,
        expectedSize = 1024,
        expectedSha256 = "a".repeat(64),
        strongETag = "\"version-1\"",
        lastModified = null,
      ),
    )
    assertFalse(
      artifact.canReusePartial(
        canonicalUrl = "https://downloads.example.com/firmware.bin",
        hostname = "downloads.example.com",
        route = StoredArtifactRoute.PINNED_IP,
        expectedSize = 1024,
        expectedSha256 = "a".repeat(64),
        strongETag = "\"version-2\"",
        lastModified = null,
      ),
    )
  }

  @Test
  fun `process store is shared for both js runtimes`() {
    val first = FirmwareArtifactStore.processInstance(rootDirectory)
    val second = FirmwareArtifactStore.processInstance(rootDirectory)

    assertSame(first, second)
  }

  private fun makeArtifact(nowMillis: Long): StoredArtifactMetadata =
    store.createArtifact(
      artifactId = "firmware-main",
      canonicalUrl = "https://downloads.example.com/firmware.bin",
      hostname = "downloads.example.com",
      route = StoredArtifactRoute.PINNED_IP,
      expectedSize = 1024,
      expectedSha256 = "a".repeat(64),
      retentionClass = ArtifactRetentionClass.PARTIAL,
      nowMillis = nowMillis,
    )
}
