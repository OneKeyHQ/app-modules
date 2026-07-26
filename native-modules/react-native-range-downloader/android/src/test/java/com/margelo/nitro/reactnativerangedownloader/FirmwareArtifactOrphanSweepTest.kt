package com.margelo.nitro.reactnativerangedownloader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FirmwareArtifactOrphanSweepTest {

  @get:Rule
  val temporaryFolder = TemporaryFolder()

  private lateinit var rootDirectory: File
  private lateinit var store: FirmwareArtifactStore

  @Before
  fun setUp() {
    rootDirectory = temporaryFolder.newFolder("firmware-artifacts")
    store = FirmwareArtifactStore(rootDirectory)
  }

  @Test
  fun `sweep requires journal reconciliation first`() {
    makeArtifact(nowMillis = 0)

    val error = assertThrows(FirmwareArtifactStoreException::class.java) {
      store.sweepOrphans(
        nowMillis = FirmwareArtifactStore.RetentionPolicy.PARTIAL_TTL_MILLIS + 1,
      )
    }
    assertEquals("ARTIFACT_RECONCILIATION_REQUIRED", error.code)
  }

  @Test
  fun `retention boundary deletes at ttl but not before`() {
    val artifact = makeArtifact(nowMillis = 0)
    store.finalFile(artifact.artifactRef).writeBytes(ByteArray(32) { 0x5A })
    store.reconcileLeases(activeLeaseRefs = emptyList(), nowMillis = 1)

    val beforeBoundary = store.sweepOrphans(
      nowMillis = FirmwareArtifactStore.RetentionPolicy.PARTIAL_TTL_MILLIS - 1,
    )
    assertEquals(StoredArtifactSweepSummary(0, 0), beforeBoundary)
    assertNotNull(store.loadArtifact(artifact.artifactRef))

    val atBoundary = store.sweepOrphans(
      nowMillis = FirmwareArtifactStore.RetentionPolicy.PARTIAL_TTL_MILLIS,
    )
    assertTrue(atBoundary.deletedFiles >= 2)
    assertTrue(atBoundary.deletedBytes > 0)
    val missing = assertThrows(FirmwareArtifactStoreException::class.java) {
      store.loadArtifact(artifact.artifactRef)
    }
    assertEquals("ARTIFACT_NOT_FOUND", missing.code)
  }

  @Test
  fun `active lease survives ttl and zero byte budget`() {
    val artifact = makeArtifact(nowMillis = 0)
    val lease = store.createLease(transactionId = "transaction-1", nowMillis = 0)
    store.retainArtifact(
      leaseRef = lease.leaseRef,
      artifactRef = artifact.artifactRef,
      nowMillis = 0,
    )
    store.reconcileLeases(activeLeaseRefs = listOf(lease.leaseRef), nowMillis = 1)

    val result = store.sweepOrphans(
      nowMillis = FirmwareArtifactStore.RetentionPolicy.PARTIAL_TTL_MILLIS * 2,
      byteBudget = 0,
    )
    assertEquals(StoredArtifactSweepSummary(0, 0), result)
    assertNotNull(store.loadArtifact(artifact.artifactRef))
  }

  @Test
  fun `metadata less orphan is deleted only after grace period`() {
    makeArtifact(nowMillis = 0)
    val orphanRef = java.util.UUID.randomUUID().toString().lowercase()
    val orphanDirectory = File(File(rootDirectory, "artifacts"), orphanRef)
    assertTrue(orphanDirectory.mkdirs())
    File(orphanDirectory, "payload.partial").writeBytes(ByteArray(64) { 0x2A })
    assertTrue(orphanDirectory.setLastModified(0))
    store.reconcileLeases(activeLeaseRefs = emptyList(), nowMillis = 1)

    val result = store.sweepOrphans(
      nowMillis = FirmwareArtifactStore.RetentionPolicy.GRACE_PERIOD_MILLIS,
    )
    assertTrue(result.deletedFiles >= 1)
    assertFalse(orphanDirectory.exists())
  }

  @Test
  fun `sweep cancels and waits for orphan worker before deleting`() {
    val artifact = makeArtifact(nowMillis = 0)
    lateinit var worker: FirmwareArtifactActivityToken
    worker = store.beginActivity(
      artifactRef = artifact.artifactRef,
      kind = ArtifactActivityKind.WORKER,
      cancel = { worker.close() },
    )
    store.reconcileLeases(activeLeaseRefs = emptyList(), nowMillis = 1)

    val result = store.sweepOrphans(
      nowMillis = FirmwareArtifactStore.RetentionPolicy.PARTIAL_TTL_MILLIS,
    )
    assertTrue(result.deletedFiles >= 1)
    val missing = assertThrows(FirmwareArtifactStoreException::class.java) {
      store.loadArtifact(artifact.artifactRef)
    }
    assertEquals("ARTIFACT_NOT_FOUND", missing.code)
  }

  private fun makeArtifact(nowMillis: Long): StoredArtifactMetadata =
    store.createArtifact(
      artifactId = "firmware-main",
      canonicalUrl = "https://downloads.example.com/firmware.bin",
      hostname = "downloads.example.com",
      route = StoredArtifactRoute.DOMAIN,
      expectedSize = 1024,
      expectedSha256 = "b".repeat(64),
      retentionClass = ArtifactRetentionClass.PARTIAL,
      nowMillis = nowMillis,
    )
}
