package com.margelo.nitro.reactnativerangedownloader

import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.createTempDirectory

class FirmwareArchiveMaterializerTest {
  private lateinit var root: File
  private lateinit var store: FirmwareArtifactStore
  private lateinit var materializer: FirmwareArchiveMaterializer

  @Before
  fun setUp() {
    root = createTempDirectory("firmware-archive-materializer-").toFile()
    store = FirmwareArtifactStore(root)
    materializer = FirmwareArchiveMaterializer(store)
  }

  @After
  fun tearDown() {
    root.deleteRecursively()
  }

  @Test
  fun discoversAndStreamsFilesIntoVerifiedOpaqueArtifacts() {
    val payloads = linkedMapOf(
      "firmware/main.bin" to ByteArray(128 * 1024) { (it % 251).toByte() },
      "resources/ble.bin" to "ble firmware".toByteArray(),
      "__MACOSX/firmware/main.bin" to "ignored metadata".toByteArray(),
    )
    val lease = store.createLease("transaction-1")
    val archive = createArchive(
      payloads = payloads,
      directoryNames = listOf("firmware/", "__MACOSX/"),
    )

    val result = materializer.materialize(
      leaseRef = lease.leaseRef,
      parentArtifactId = archive.artifactId,
      archiveArtifactRef = archive.artifactRef,
      archiveImmutableToken = requireNotNull(archive.immutableToken),
      materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
    )

    assertEquals(
      listOf("firmware/main.bin", "resources/ble.bin"),
      result.map { it.entryId },
    )
    assertEquals(listOf("main.bin", "ble.bin"), result.map { it.logicalName })
    result.forEach { child ->
      val expectedPayload = payloads.getValue(child.entryId)
      val expectedDigest = digest(expectedPayload)
      val expectedTaskId = FirmwareArchiveRules.childTaskId(
        parentArtifactId = archive.artifactId,
        sha256 = expectedDigest,
        size = expectedPayload.size.toLong(),
      )
      assertEquals(
        ArtifactRetentionClass.MATERIALIZED_CHILD,
        child.artifact.retentionClass,
      )
      assertEquals(expectedTaskId, child.artifact.artifactId)
      assertEquals(
        StoredArtifactMetadata.sha256(expectedTaskId),
        child.artifact.taskIdHash,
      )
      assertArrayEquals(
        expectedPayload,
        store.finalFile(child.artifact.artifactRef).readBytes(),
      )
    }
  }

  @Test
  fun aliasesSameParentAndContentButNotDifferentParents() {
    val sharedPayload = "same firmware".toByteArray()
    val lease = store.createLease("transaction-2")
    val firstArchive = createArchive(
      payloads = linkedMapOf(
        "one/main.bin" to sharedPayload,
        "two/ble.bin" to sharedPayload,
      ),
      artifactId = "firmware-parent-a",
    )

    val aliases = materializer.materialize(
      leaseRef = lease.leaseRef,
      parentArtifactId = firstArchive.artifactId,
      archiveArtifactRef = firstArchive.artifactRef,
      archiveImmutableToken = requireNotNull(firstArchive.immutableToken),
      materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
    )
    assertEquals(
      aliases.first().artifact.artifactRef,
      aliases.last().artifact.artifactRef,
    )

    val secondArchive = createArchive(
      payloads = linkedMapOf("three/resource.bin" to sharedPayload),
      artifactId = "firmware-parent-b",
    )
    store.retainArtifact(lease.leaseRef, secondArchive.artifactRef)
    val differentParent = materializer.materialize(
      leaseRef = lease.leaseRef,
      parentArtifactId = secondArchive.artifactId,
      archiveArtifactRef = secondArchive.artifactRef,
      archiveImmutableToken = requireNotNull(secondArchive.immutableToken),
      materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
    )
    assertNotEquals(
      aliases.first().artifact.artifactRef,
      differentParent.single().artifact.artifactRef,
    )
  }

  @Test
  fun rejectsParentIdentityMismatchBeforeCreatingChildren() {
    val lease = store.createLease("transaction-3")
    val archive = createArchive(
      linkedMapOf("firmware/main.bin" to "main".toByteArray())
    )

    assertThrows(FirmwareArchiveMaterializerException::class.java) {
      materializer.materialize(
        leaseRef = lease.leaseRef,
        parentArtifactId = "different-parent",
        archiveArtifactRef = archive.artifactRef,
        archiveImmutableToken = requireNotNull(archive.immutableToken),
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }
    assertEquals(
      1,
      requireNotNull(File(root, "artifacts").listFiles()).size,
    )
  }

  @Test
  fun materializesMoreThanLegacyThirtyTwoEntryLimit() {
    val payloads = linkedMapOf<String, ByteArray>()
    repeat(64) { index ->
      payloads["resources/resource-$index.bin"] =
        "resource-$index".toByteArray()
    }
    val lease = store.createLease("transaction-4")
    val archive = createArchive(payloads)

    val result = materializer.materialize(
      leaseRef = lease.leaseRef,
      parentArtifactId = archive.artifactId,
      archiveArtifactRef = archive.artifactRef,
      archiveImmutableToken = requireNotNull(archive.immutableToken),
      materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
    )

    assertEquals(64, result.size)
  }

  @Test
  fun rejectsCorruptIgnoredMetadataCrc() {
    val lease = store.createLease("transaction-5")
    val archive = createArchive(
      payloads = linkedMapOf(
        "firmware/main.bin" to "main firmware".toByteArray(),
        "__MACOSX/._main.bin" to "ignored metadata".toByteArray(),
      ),
      mutateArchive = {
        corruptCentralDirectoryCrc(it, "__MACOSX/._main.bin")
      },
    )

    val error = assertThrows(FirmwareArchiveMaterializerException::class.java) {
      materializer.materialize(
        leaseRef = lease.leaseRef,
        parentArtifactId = archive.artifactId,
        archiveArtifactRef = archive.artifactRef,
        archiveImmutableToken = requireNotNull(archive.immutableToken),
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }

    assertEquals("ARTIFACT_ARCHIVE_INTEGRITY_FAILED", error.code)
  }

  @Test
  fun rejectsZip64SentinelBeforeMaterializing() {
    val lease = store.createLease("transaction-6")
    val archive = createArchive(
      payloads = linkedMapOf(
        "firmware/main.bin" to "main firmware".toByteArray(),
      ),
      mutateArchive = {
        overwriteCentralDirectoryField(
          it,
          "firmware/main.bin",
          fieldOffset = 20,
          value = 0xff,
        )
      },
    )

    val error = assertThrows(FirmwareArchiveMaterializerException::class.java) {
      materializer.materialize(
        leaseRef = lease.leaseRef,
        parentArtifactId = archive.artifactId,
        archiveArtifactRef = archive.artifactRef,
        archiveImmutableToken = requireNotNull(archive.immutableToken),
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }

    assertEquals("ARTIFACT_ARCHIVE_INVALID", error.code)
  }

  @Test
  fun rejectsRedundantZip64ExtraWithoutSentinel() {
    val lease = store.createLease("transaction-7")
    val archive = createArchive(
      payloads = linkedMapOf(
        "firmware/main.bin" to "main firmware".toByteArray(),
      ),
      entryExtras = mapOf(
        "firmware/main.bin" to sizeExtra(0xfeca, 13),
      ),
      mutateArchive = {
        overwriteExtraHeaderId(it, "firmware/main.bin", 0x0001)
      },
    )

    val error = assertThrows(FirmwareArchiveMaterializerException::class.java) {
      materializer.materialize(
        leaseRef = lease.leaseRef,
        parentArtifactId = archive.artifactId,
        archiveArtifactRef = archive.artifactRef,
        archiveImmutableToken = requireNotNull(archive.immutableToken),
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }

    assertEquals("ARTIFACT_ARCHIVE_INVALID", error.code)
  }

  private fun createArchive(
    payloads: LinkedHashMap<String, ByteArray>,
    directoryNames: List<String> = emptyList(),
    artifactId: String = "firmware-archive",
    entryExtras: Map<String, ByteArray> = emptyMap(),
    mutateArchive: (File) -> Unit = {},
  ): StoredArtifactMetadata {
    val zip = File(root, "fixture-${System.nanoTime()}.zip")
    ZipOutputStream(zip.outputStream().buffered()).use { output ->
      directoryNames.forEach { name ->
        output.putNextEntry(ZipEntry(name))
        output.closeEntry()
      }
      payloads.forEach { (name, payload) ->
        val entry = ZipEntry(name)
        entryExtras[name]?.let { entry.extra = it }
        output.putNextEntry(entry)
        output.write(payload)
        output.closeEntry()
      }
    }
    mutateArchive(zip)
    val metadata = store.createArtifact(
      artifactId = artifactId,
      canonicalUrl = "https://downloads.example.com/firmware.zip",
      hostname = "downloads.example.com",
      route = StoredArtifactRoute.DOMAIN,
      expectedSize = zip.length(),
      expectedSha256 = requireNotNull(
        RangeDownloadLogic.calculateSHA256(zip)
      ),
      retentionClass = ArtifactRetentionClass.STAGING,
    )
    zip.copyTo(store.partialFile(metadata.artifactRef))
    zip.delete()
    return store.promoteVerifiedStaging(
      artifactRef = metadata.artifactRef,
      maxBytes = metadata.expectedSize,
    )
  }

  private fun corruptCentralDirectoryCrc(zip: File, entryName: String) {
    overwriteCentralDirectoryField(
      zip,
      entryName,
      fieldOffset = 16,
      value = 0x01,
    )
  }

  private fun overwriteCentralDirectoryField(
    zip: File,
    entryName: String,
    fieldOffset: Int,
    value: Int,
  ) {
    val bytes = zip.readBytes()
    val name = entryName.toByteArray(Charsets.UTF_8)
    val nameOffset = (46..bytes.size - name.size).firstOrNull { offset ->
      bytes.copyOfRange(offset, offset + name.size).contentEquals(name) &&
        readUInt32(bytes, offset - 46) == 0x02014b50L
    } ?: error("Central directory entry not found: $entryName")
    val valueOffset = nameOffset - 46 + fieldOffset
    repeat(4) { index ->
      bytes[valueOffset + index] = value.toByte()
    }
    zip.writeBytes(bytes)
  }

  private fun overwriteExtraHeaderId(
    zip: File,
    entryName: String,
    headerId: Int,
  ) {
    val bytes = zip.readBytes()
    val name = entryName.toByteArray(Charsets.UTF_8)
    val centralNameOffset = (46..bytes.size - name.size).firstOrNull { offset ->
      bytes.copyOfRange(offset, offset + name.size).contentEquals(name) &&
        readUInt32(bytes, offset - 46) == 0x02014b50L
    } ?: error("Central directory entry not found: $entryName")
    val centralHeaderOffset = centralNameOffset - 46
    val localHeaderOffset =
      readUInt32(bytes, centralHeaderOffset + 42).toInt()
    val localNameLength = readUInt16(bytes, localHeaderOffset + 26)
    val extraOffsets = listOf(
      centralNameOffset + name.size,
      localHeaderOffset + 30 + localNameLength,
    )
    extraOffsets.forEach { offset ->
      bytes[offset] = headerId.toByte()
      bytes[offset + 1] = (headerId shr 8).toByte()
    }
    zip.writeBytes(bytes)
  }

  private fun sizeExtra(headerId: Int, size: Long): ByteArray =
    ByteBuffer.allocate(20)
      .order(ByteOrder.LITTLE_ENDIAN)
      .putShort(headerId.toShort())
      .putShort(16.toShort())
      .putLong(size)
      .putLong(size)
      .array()

  private fun readUInt16(bytes: ByteArray, offset: Int): Int =
    (bytes[offset].toInt() and 0xff) or
      ((bytes[offset + 1].toInt() and 0xff) shl 8)

  private fun readUInt32(bytes: ByteArray, offset: Int): Long =
    (bytes[offset].toLong() and 0xff) or
      ((bytes[offset + 1].toLong() and 0xff) shl 8) or
      ((bytes[offset + 2].toLong() and 0xff) shl 16) or
      ((bytes[offset + 3].toLong() and 0xff) shl 24)

  private fun digest(payload: ByteArray): String =
    MessageDigest.getInstance("SHA-256")
      .digest(payload)
      .joinToString("") { byte -> "%02x".format(byte) }
}
