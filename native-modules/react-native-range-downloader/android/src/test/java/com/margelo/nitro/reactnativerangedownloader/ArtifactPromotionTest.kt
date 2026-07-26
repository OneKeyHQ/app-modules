package com.margelo.nitro.reactnativerangedownloader

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory

class ArtifactPromotionTest {
  private lateinit var root: File
  private lateinit var store: FirmwareArtifactStore

  @Before
  fun setUp() {
    root = createTempDirectory("firmware-artifact-promotion-").toFile()
    store = FirmwareArtifactStore(root)
  }

  @After
  fun tearDown() {
    root.deleteRecursively()
  }

  @Test
  fun finalAppearsOnlyAfterSizeAndDigestVerification() {
    val payload = "verified firmware".toByteArray()
    val metadata = createArtifact(payload)
    val staging = store.partialFile(metadata.artifactRef)
    staging.writeBytes(payload)

    val promoted = store.promoteVerifiedStaging(
      artifactRef = metadata.artifactRef,
      maxBytes = payload.size.toLong(),
    )

    assertFalse(staging.exists())
    assertTrue(store.finalFile(metadata.artifactRef).isFile)
    assertEquals(payload.size.toLong(), promoted.actualSize)
    assertEquals(metadata.expectedSha256, promoted.actualSha256)
    assertNotNull(promoted.immutableToken)
    assertEquals(ArtifactRetentionClass.VERIFIED_FINAL, promoted.retentionClass)
  }

  @Test
  fun digestMismatchQuarantinesStagingWithoutExposingFinal() {
    val payload = "expected firmware".toByteArray()
    val metadata = createArtifact(payload)
    val staging = store.partialFile(metadata.artifactRef)
    staging.writeBytes("corrupt firmware!".toByteArray())

    val error = assertThrows(FirmwareArtifactStoreException::class.java) {
      store.promoteVerifiedStaging(
        artifactRef = metadata.artifactRef,
        maxBytes = payload.size.toLong(),
      )
    }

    assertEquals("ARTIFACT_DIGEST_MISMATCH", error.code)
    assertFalse(staging.exists())
    assertFalse(store.finalFile(metadata.artifactRef).exists())
  }

  @Test
  fun maxBytesMustCoverExpectedSizeBeforePromotion() {
    val payload = "firmware payload".toByteArray()
    val metadata = createArtifact(payload)
    val staging = store.partialFile(metadata.artifactRef)
    staging.writeBytes(payload)

    val error = assertThrows(FirmwareArtifactStoreException::class.java) {
      store.promoteVerifiedStaging(
        artifactRef = metadata.artifactRef,
        maxBytes = payload.size.toLong() - 1,
      )
    }

    assertEquals("ARTIFACT_INVALID_METADATA", error.code)
    assertTrue(staging.exists())
    assertFalse(store.finalFile(metadata.artifactRef).exists())
  }

  private fun createArtifact(payload: ByteArray): StoredArtifactMetadata {
    val digestFile = File(root, "digest-${System.nanoTime()}").apply {
      writeBytes(payload)
    }
    val digest = requireNotNull(RangeDownloadLogic.calculateSHA256(digestFile))
    digestFile.delete()
    return store.createArtifact(
      artifactId = "firmware-main",
      canonicalUrl = "https://downloads.example.com/firmware.bin",
      hostname = "downloads.example.com",
      route = StoredArtifactRoute.DOMAIN,
      expectedSize = payload.size.toLong(),
      expectedSha256 = digest,
      retentionClass = ArtifactRetentionClass.STAGING,
    )
  }
}
