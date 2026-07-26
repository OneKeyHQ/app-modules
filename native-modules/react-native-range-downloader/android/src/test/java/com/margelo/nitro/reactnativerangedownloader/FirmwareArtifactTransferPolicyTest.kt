package com.margelo.nitro.reactnativerangedownloader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class FirmwareArtifactTransferPolicyTest {
  @get:Rule
  val temporaryFolder = TemporaryFolder()

  @Test
  fun `restart cannot extend deadline or reset attempts by rotating task id`() {
    val root = temporaryFolder.newFolder("transfer-policy")
    val store = FirmwareArtifactStore(root)
    val lease = store.createLease("transaction", nowMillis = 10)
    val first = store.beginTransferAttempt(
      leaseRef = lease.leaseRef,
      taskId = "task-1",
      artifactId = "main",
      canonicalUrl = URL,
      hostname = HOST,
      route = StoredArtifactRoute.DOMAIN,
      expectedSize = 1024,
      expectedSha256 = SHA,
      initialDeadlineAtMillis = 100,
      maxRunAttempts = 2,
      nowMillis = 20,
    )

    val restarted = FirmwareArtifactStore(root)
    val second = restarted.beginTransferAttempt(
      leaseRef = lease.leaseRef,
      taskId = "task-2",
      artifactId = "main",
      canonicalUrl = URL,
      hostname = HOST,
      route = StoredArtifactRoute.PINNED_IP,
      expectedSize = 1024,
      expectedSha256 = SHA,
      initialDeadlineAtMillis = 25,
      maxRunAttempts = 2,
      nowMillis = 30,
    )

    assertEquals(first.artifactRef, second.artifactRef)
    assertEquals(100L, second.transferDeadlineAtMillis)
    assertEquals(2, second.transferRunAttempts)
    val error = assertThrows(FirmwareArtifactStoreException::class.java) {
      restarted.beginTransferAttempt(
        leaseRef = lease.leaseRef,
        taskId = "task-3",
        artifactId = "main",
        canonicalUrl = URL,
        hostname = HOST,
        route = StoredArtifactRoute.DOMAIN,
        expectedSize = 1024,
        expectedSha256 = SHA,
        initialDeadlineAtMillis = 2_000,
        maxRunAttempts = 2,
        nowMillis = 40,
      )
    }
    assertEquals("ARTIFACT_ATTEMPT_LIMIT_EXCEEDED", error.code)
  }

  @Test
  fun `restart rejects an expired persisted deadline`() {
    val root = temporaryFolder.newFolder("expired-policy")
    val store = FirmwareArtifactStore(root)
    val lease = store.createLease("transaction", nowMillis = 10)
    store.beginTransferAttempt(
      leaseRef = lease.leaseRef,
      taskId = "task-1",
      artifactId = "main",
      canonicalUrl = URL,
      hostname = HOST,
      route = StoredArtifactRoute.DOMAIN,
      expectedSize = 1024,
      expectedSha256 = SHA,
      initialDeadlineAtMillis = 100,
      maxRunAttempts = 8,
      nowMillis = 20,
    )

    val error = assertThrows(FirmwareArtifactStoreException::class.java) {
      FirmwareArtifactStore(root).beginTransferAttempt(
        leaseRef = lease.leaseRef,
        taskId = "task-2",
        artifactId = "main",
        canonicalUrl = URL,
        hostname = HOST,
        route = StoredArtifactRoute.DOMAIN,
        expectedSize = 1024,
        expectedSha256 = SHA,
        initialDeadlineAtMillis = 1_000,
        maxRunAttempts = 8,
        nowMillis = 101,
      )
    }
    assertEquals("ARTIFACT_DEADLINE_EXCEEDED", error.code)
  }

  @Test
  fun `different tasks fail fast while one owns the artifact`() {
    val registry = FirmwareArtifactRegistry()
    val entered = CountDownLatch(1)
    val release = CountDownLatch(1)
    val executor = Executors.newSingleThreadExecutor()
    val owner = executor.submit<StoredArtifactMetadata> {
      registry.runOrAttach(
        leaseRef = "lease-1",
        taskId = "task-1",
        artifactId = "main",
        fingerprint = "fingerprint",
        expectedSize = 1,
      ) { _, _ ->
        entered.countDown()
        assertTrue(release.await(5, TimeUnit.SECONDS))
        metadata()
      }
    }
    assertTrue(entered.await(5, TimeUnit.SECONDS))

    val error = assertThrows(FirmwareArtifactTransferException::class.java) {
      registry.runOrAttach(
        leaseRef = "lease-2",
        taskId = "task-2",
        artifactId = "main",
        fingerprint = "fingerprint",
        expectedSize = 1,
      ) { _, _ -> metadata() }
    }

    assertEquals("ARTIFACT_BUSY", error.code)
    assertTrue(error.retryable)
    release.countDown()
    owner.get(5, TimeUnit.SECONDS)
    executor.shutdownNow()
  }

  @Test
  fun `durable partial without a live worker requires resume`() {
    val store = FirmwareArtifactStore(temporaryFolder.newFolder("resume-status"))
    val lease = store.createLease("transaction", nowMillis = 10)
    store.beginTransferAttempt(
      leaseRef = lease.leaseRef,
      taskId = "task",
      artifactId = "main",
      canonicalUrl = URL,
      hostname = HOST,
      route = StoredArtifactRoute.DOMAIN,
      expectedSize = 1024,
      expectedSha256 = SHA,
      initialDeadlineAtMillis = 100,
      maxRunAttempts = 8,
      nowMillis = 20,
    )

    val status = FirmwareArtifactDownloader(
      store = store,
      registry = FirmwareArtifactRegistry(),
    ).status(lease.leaseRef, "task")

    assertEquals(FirmwareArtifactRegistryState.FAILED, status.state)
    assertEquals("ARTIFACT_RESUME_REQUIRED", status.errorCode)
    assertEquals(true, status.retryable)
  }

  private fun metadata(): StoredArtifactMetadata =
    StoredArtifactMetadata.create(
      artifactRef = UUID.randomUUID().toString().lowercase(),
      artifactId = "main",
      canonicalUrl = URL,
      hostname = HOST,
      route = StoredArtifactRoute.DOMAIN,
      expectedSize = 1,
      expectedSha256 = SHA,
      retentionClass = ArtifactRetentionClass.PARTIAL,
      nowMillis = 1,
    )

  companion object {
    private const val URL = "https://downloads.example.com/main.bin"
    private const val HOST = "downloads.example.com"
    private val SHA = "a".repeat(64)
  }
}
