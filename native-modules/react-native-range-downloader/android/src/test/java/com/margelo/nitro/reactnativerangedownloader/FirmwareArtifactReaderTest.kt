package com.margelo.nitro.reactnativerangedownloader

import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.UUID
import kotlin.io.path.createTempDirectory

class FirmwareArtifactReaderTest {
  private lateinit var root: File
  private lateinit var store: FirmwareArtifactStore
  private lateinit var reader: FirmwareArtifactReader

  @Before
  fun setUp() {
    root = createTempDirectory("firmware-artifact-reader-").toFile()
    store = FirmwareArtifactStore(root)
    reader = FirmwareArtifactReader(store)
  }

  @After
  fun tearDown() {
    reader.close()
    root.deleteRecursively()
  }

  @Test
  fun readsBoundedBinaryChunksAndAllowsEmptyReadOnlyAtEof() {
    val payload = ByteArray(255) { it.toByte() }
    val artifact = createVerifiedArtifact(payload)
    val info = reader.open(
      artifactRef = artifact.artifactRef,
      immutableToken = requireNotNull(artifact.immutableToken),
    )

    assertArrayEquals(
      payload.copyOfRange(7, 38),
      reader.read(info.readerId, offset = 7.0, length = 31.0),
    )
    assertArrayEquals(
      ByteArray(0),
      reader.read(
        info.readerId,
        offset = payload.size.toDouble(),
        length = 0.0,
      ),
    )
    val error = assertThrows(FirmwareArtifactReaderException::class.java) {
      reader.read(info.readerId, offset = 0.0, length = 0.0)
    }
    assertEquals("ARTIFACT_READ_EMPTY", error.code)
  }

  @Test
  fun rejectsStaleTokenUnsafeNumbersOversizedAndOutOfBoundsReads() {
    val payload = "verified firmware reader".toByteArray()
    val artifact = createVerifiedArtifact(payload)

    var error = assertThrows(FirmwareArtifactReaderException::class.java) {
      reader.open(
        artifactRef = artifact.artifactRef,
        immutableToken = UUID.randomUUID().toString().lowercase(),
      )
    }
    assertEquals("ARTIFACT_IMMUTABLE_TOKEN_MISMATCH", error.code)

    val info = reader.open(
      artifactRef = artifact.artifactRef,
      immutableToken = requireNotNull(artifact.immutableToken),
    )
    error = assertThrows(FirmwareArtifactReaderException::class.java) {
      reader.read(info.readerId, offset = 0.5, length = 1.0)
    }
    assertEquals("ARTIFACT_READ_INVALID_RANGE", error.code)
    error = assertThrows(FirmwareArtifactReaderException::class.java) {
      reader.read(
        info.readerId,
        offset = 0.0,
        length = (FirmwareArtifactReader.MAX_READ_BYTES + 1).toDouble(),
      )
    }
    assertEquals("ARTIFACT_READ_LIMIT_EXCEEDED", error.code)
    error = assertThrows(FirmwareArtifactReaderException::class.java) {
      reader.read(
        info.readerId,
        offset = (payload.size - 1).toDouble(),
        length = 2.0,
      )
    }
    assertEquals("ARTIFACT_READ_INVALID_RANGE", error.code)
  }

  @Test
  fun closeIsIdempotentAndReleasesDeletionProtection() {
    val artifact = createVerifiedArtifact("firmware".toByteArray())
    val info = reader.open(
      artifactRef = artifact.artifactRef,
      immutableToken = requireNotNull(artifact.immutableToken),
    )

    val leased = assertThrows(FirmwareArtifactStoreException::class.java) {
      store.discardArtifact(artifact.artifactRef)
    }
    assertEquals("ARTIFACT_LEASED", leased.code)
    reader.close(info.readerId)
    reader.close(info.readerId)
    store.discardArtifact(artifact.artifactRef)
  }

  private fun createVerifiedArtifact(payload: ByteArray): StoredArtifactMetadata {
    val digestFixture = File(root, "digest-${System.nanoTime()}").apply {
      writeBytes(payload)
    }
    val digest = requireNotNull(RangeDownloadLogic.calculateSHA256(digestFixture))
    digestFixture.delete()
    val metadata = store.createArtifact(
      artifactId = "firmware-main",
      canonicalUrl = "https://downloads.example.com/firmware.bin",
      hostname = "downloads.example.com",
      route = StoredArtifactRoute.DOMAIN,
      expectedSize = payload.size.toLong(),
      expectedSha256 = digest,
      retentionClass = ArtifactRetentionClass.STAGING,
    )
    store.partialFile(metadata.artifactRef).writeBytes(payload)
    return store.promoteVerifiedStaging(
      artifactRef = metadata.artifactRef,
      maxBytes = payload.size.toLong(),
    )
  }
}
