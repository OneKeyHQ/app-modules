package com.margelo.nitro.reactnativerangedownloader

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

internal data class FirmwareArchiveMaterializedEntry(
  val entryId: String,
  val logicalName: String,
  val artifact: StoredArtifactMetadata,
)

internal class FirmwareArchiveMaterializerException private constructor(
  val code: String,
) : Exception(code) {
  companion object {
    fun invalidArchive() =
      FirmwareArchiveMaterializerException("ARTIFACT_ARCHIVE_INVALID")
    fun integrityFailure() =
      FirmwareArchiveMaterializerException("ARTIFACT_ARCHIVE_INTEGRITY_FAILED")
    fun busy() =
      FirmwareArchiveMaterializerException("ARTIFACT_ARCHIVE_BUSY")
  }
}

internal class FirmwareArchiveMaterializer(
  private val store: FirmwareArtifactStore,
) {
  private val activeLock = Any()
  private val activeArchives = mutableSetOf<String>()

  fun materialize(
    leaseRef: String,
    parentArtifactId: String,
    archiveArtifactRef: String,
    archiveImmutableToken: String,
    materializationPolicy: String,
  ): List<FirmwareArchiveMaterializedEntry> {
    synchronized(activeLock) {
      if (!activeArchives.add(archiveArtifactRef.lowercase())) {
        throw FirmwareArchiveMaterializerException.busy()
      }
    }
    try {
      return materializeOnce(
        leaseRef = leaseRef,
        parentArtifactId = parentArtifactId,
        archiveArtifactRef = archiveArtifactRef,
        archiveImmutableToken = archiveImmutableToken,
        materializationPolicy = materializationPolicy,
      )
    } finally {
      synchronized(activeLock) {
        activeArchives.remove(archiveArtifactRef.lowercase())
      }
    }
  }

  private fun materializeOnce(
    leaseRef: String,
    parentArtifactId: String,
    archiveArtifactRef: String,
    archiveImmutableToken: String,
    materializationPolicy: String,
  ): List<FirmwareArchiveMaterializedEntry> {
    val archiveActivity = store.beginActivity(
      artifactRef = archiveArtifactRef,
      kind = ArtifactActivityKind.MATERIALIZER,
    )
    try {
      val archive = requireVerifiedArchive(
        artifactRef = archiveActivity.artifactRef,
        parentArtifactId = parentArtifactId,
        immutableToken = archiveImmutableToken,
      )
      store.retainArtifact(leaseRef, archive.artifactRef)
      val archiveFile = store.finalFile(archive.artifactRef)
      val scratchDirectory = requireNotNull(archiveFile.parentFile)
      cleanupScratchFiles(scratchDirectory)
      try {
        val facts = FirmwareZipCentralDirectoryScanner.scan(archiveFile)
        val validated = FirmwareArchiveRules.validate(
          entries = facts,
          materializationPolicy = materializationPolicy,
        )
        val integrityEntries =
          FirmwareArchiveRules.entriesRequiringIntegrityValidation(validated)
        val validatedByName = integrityEntries.associateBy { it.facts.name }
        val children = mutableListOf<FirmwareArchiveMaterializedEntry>()
        val seenNames = mutableSetOf<String>()
        var localIndex = 0

        ZipInputStream(
          BufferedInputStream(
            FileInputStream(archiveFile),
            FirmwareArchiveRules.EXTRACTION_BUFFER_BYTES,
          ),
          StandardCharsets.UTF_8,
        ).use { zip ->
          while (true) {
            val zipEntry = try {
              zip.nextEntry
            } catch (error: Exception) {
              throw FirmwareArchiveMaterializerException.invalidArchive()
            } ?: break
            val validatedEntry = validatedByName[zipEntry.name]
              ?: throw FirmwareArchiveRuleException.invalidEntry()
            if (!seenNames.add(zipEntry.name) ||
              zipEntry.method != validatedEntry.facts.compressionMethod ||
              zipEntry.isDirectory !=
              (validatedEntry.disposition ==
                FirmwareArchiveEntryDisposition.IGNORE_DIRECTORY) ||
              (zipEntry.size >= 0 &&
                zipEntry.size != validatedEntry.facts.uncompressedSize) ||
              (zipEntry.compressedSize >= 0 &&
                zipEntry.compressedSize != validatedEntry.facts.compressedSize)
            ) {
              throw FirmwareArchiveRuleException.invalidEntry()
            }

            when (validatedEntry.disposition) {
              FirmwareArchiveEntryDisposition.IGNORE_DIRECTORY,
              FirmwareArchiveEntryDisposition.IGNORE_METADATA -> {
                drainEntry(zip, validatedEntry.facts.uncompressedSize)
                closeAndValidateEntry(
                  zip,
                  zipEntry,
                  validatedEntry.facts.crc32,
                )
              }
              FirmwareArchiveEntryDisposition.MATERIALIZE -> {
                val scratch = File(
                  scratchDirectory,
                  "$SCRATCH_PREFIX${UUID.randomUUID().toString().lowercase()}" +
                    SCRATCH_SUFFIX,
                )
                try {
                  val digest = extractEntry(
                    zip = zip,
                    outputFile = scratch,
                    expectedSize = validatedEntry.facts.uncompressedSize,
                  )
                  closeAndValidateEntry(
                    zip,
                    zipEntry,
                    validatedEntry.facts.crc32,
                  )
                  val child = resolveChild(
                    leaseRef = leaseRef,
                    parentArtifactId = parentArtifactId,
                    size = validatedEntry.facts.uncompressedSize,
                    sha256 = digest,
                    scratch = scratch,
                  )
                  children += FirmwareArchiveMaterializedEntry(
                    entryId = validatedEntry.entryId,
                    logicalName = validatedEntry.logicalName,
                    artifact = child,
                  )
                } finally {
                  scratch.delete()
                }
              }
            }
            localIndex += 1
          }
        }

        val expectedChildren = validated.count {
          it.disposition == FirmwareArchiveEntryDisposition.MATERIALIZE
        }
        if (seenNames != validatedByName.keys ||
          localIndex != integrityEntries.size ||
          children.size != expectedChildren
        ) {
          throw FirmwareArchiveRuleException.invalidEntry()
        }

        val committedByRef = children
          .map { it.artifact.artifactRef }
          .toSet()
          .associateWith { artifactRef ->
            store.updateArtifact(artifactRef) {
              it.retentionClass = ArtifactRetentionClass.MATERIALIZED_CHILD
            }
          }
        store.updateArtifact(archive.artifactRef) {
          it.retentionClass = ArtifactRetentionClass.ARCHIVE
        }
        return children.map { child ->
          FirmwareArchiveMaterializedEntry(
            entryId = child.entryId,
            logicalName = child.logicalName,
            artifact = requireNotNull(
              committedByRef[child.artifact.artifactRef]
            ),
          )
        }
      } finally {
        cleanupScratchFiles(scratchDirectory)
      }
    } finally {
      archiveActivity.close()
    }
  }

  private fun requireVerifiedArchive(
    artifactRef: String,
    parentArtifactId: String,
    immutableToken: String,
  ): StoredArtifactMetadata {
    val metadata = store.loadArtifact(artifactRef)
    val actualSize = metadata.actualSize
    val storedToken = metadata.immutableToken
    if (parentArtifactId.isBlank() ||
      parentArtifactId.toByteArray(Charsets.UTF_8).size > 256 ||
      '\u0000' in parentArtifactId ||
      metadata.artifactId != parentArtifactId ||
      metadata.tombstonedAtMillis != null ||
      metadata.retentionClass !in setOf(
        ArtifactRetentionClass.VERIFIED_FINAL,
        ArtifactRetentionClass.ARCHIVE,
      ) ||
      actualSize == null ||
      actualSize <= 0 ||
      actualSize != metadata.expectedSize ||
      metadata.actualSha256 == null ||
      !RangeDownloadLogic.secureHexEquals(
        requireNotNull(metadata.actualSha256),
        metadata.expectedSha256,
      ) ||
      storedToken == null ||
      !store.finalFile(artifactRef).isFile ||
      store.finalFile(artifactRef).length() != actualSize
    ) {
      throw FirmwareArchiveMaterializerException.invalidArchive()
    }
    if (storedToken != immutableToken) {
      throw FirmwareArtifactReaderException.immutableTokenMismatch()
    }
    return metadata
  }

  private fun extractEntry(
    zip: ZipInputStream,
    outputFile: File,
    expectedSize: Long,
  ): String {
    val digest = MessageDigest.getInstance("SHA-256")
    FileOutputStream(outputFile).buffered(
      FirmwareArchiveRules.EXTRACTION_BUFFER_BYTES
    ).use { output ->
      val buffer = ByteArray(FirmwareArchiveRules.EXTRACTION_BUFFER_BYTES)
      var actualSize = 0L
      while (true) {
        val read = try {
          zip.read(buffer)
        } catch (error: Exception) {
          throw FirmwareArchiveMaterializerException.integrityFailure()
        }
        if (read < 0) {
          break
        }
        if (read == 0) {
          continue
        }
        if (read.toLong() > expectedSize - actualSize) {
          throw FirmwareArchiveRuleException.limitExceeded()
        }
        output.write(buffer, 0, read)
        digest.update(buffer, 0, read)
        actualSize += read
      }
      if (actualSize != expectedSize) {
        throw FirmwareArchiveMaterializerException.integrityFailure()
      }
    }
    return digest.digest().joinToString("") { byte -> "%02x".format(byte) }
  }

  private fun drainEntry(zip: ZipInputStream, expectedSize: Long) {
    val buffer = ByteArray(FirmwareArchiveRules.EXTRACTION_BUFFER_BYTES)
    var actualSize = 0L
    while (true) {
      val read = try {
        zip.read(buffer)
      } catch (error: Exception) {
        throw FirmwareArchiveMaterializerException.integrityFailure()
      }
      if (read < 0) {
        break
      }
      if (read == 0) {
        continue
      }
      if (read.toLong() > expectedSize - actualSize) {
        throw FirmwareArchiveRuleException.limitExceeded()
      }
      actualSize += read
    }
    if (actualSize != expectedSize) {
      throw FirmwareArchiveMaterializerException.integrityFailure()
    }
  }

  private fun closeAndValidateEntry(
    zip: ZipInputStream,
    entry: ZipEntry,
    expectedCrc32: Long,
  ) {
    try {
      zip.closeEntry()
    } catch (error: Exception) {
      throw FirmwareArchiveMaterializerException.integrityFailure()
    }
    if (entry.crc < 0 || entry.crc != expectedCrc32) {
      throw FirmwareArchiveMaterializerException.integrityFailure()
    }
  }

  private fun resolveChild(
    leaseRef: String,
    parentArtifactId: String,
    size: Long,
    sha256: String,
    scratch: File,
  ): StoredArtifactMetadata {
    val taskId = FirmwareArchiveRules.childTaskId(
      parentArtifactId = parentArtifactId,
      sha256 = sha256,
      size = size,
    )
    val child = store.findArtifactForTask(
      leaseRef = leaseRef,
      taskId = taskId,
    )?.also { existing ->
      if (existing.artifactId != taskId ||
        existing.taskIdHash != StoredArtifactMetadata.sha256(taskId) ||
        existing.expectedSize != size ||
        !RangeDownloadLogic.secureHexEquals(
          existing.expectedSha256,
          sha256,
        )
      ) {
        throw FirmwareArchiveMaterializerException.integrityFailure()
      }
      store.retainArtifact(leaseRef, existing.artifactRef)
      if (isReusableFinalChild(existing, size, sha256)) {
        return existing
      }
      if (existing.retentionClass != ArtifactRetentionClass.STAGING ||
        existing.actualSize != null ||
        existing.actualSha256 != null ||
        existing.immutableToken != null
      ) {
        throw FirmwareArchiveMaterializerException.integrityFailure()
      }
    } ?: run {
      val created = store.createArtifact(
        artifactId = taskId,
        canonicalUrl = "onekey-artifact://materialized/$taskId",
        hostname = "native-artifact",
        route = StoredArtifactRoute.DOMAIN,
        expectedSize = size,
        expectedSha256 = sha256,
        retentionClass = ArtifactRetentionClass.STAGING,
      )
      val prepared = store.updateArtifact(created.artifactRef) {
        it.taskIdHash = StoredArtifactMetadata.sha256(taskId)
      }
      store.retainArtifact(leaseRef, prepared.artifactRef)
      prepared
    }

    val childActivity = store.beginActivity(
      artifactRef = child.artifactRef,
      kind = ArtifactActivityKind.MATERIALIZER,
    )
    try {
      moveScratchToStaging(
        scratch = scratch,
        staging = store.partialFile(child.artifactRef),
      )
      val promoted = store.promoteVerifiedStaging(
        artifactRef = child.artifactRef,
        maxBytes = size,
      )
      return store.updateArtifact(promoted.artifactRef) {
        it.retentionClass = ArtifactRetentionClass.STAGING
      }
    } finally {
      childActivity.close()
    }
  }

  private fun isReusableFinalChild(
    metadata: StoredArtifactMetadata,
    size: Long,
    sha256: String,
  ): Boolean =
    metadata.retentionClass in setOf(
      ArtifactRetentionClass.STAGING,
      ArtifactRetentionClass.VERIFIED_FINAL,
      ArtifactRetentionClass.MATERIALIZED_CHILD,
    ) &&
      metadata.actualSize == size &&
      metadata.actualSha256?.let {
        RangeDownloadLogic.secureHexEquals(it, sha256)
      } == true &&
      metadata.immutableToken != null &&
      store.finalFile(metadata.artifactRef).isFile &&
      store.finalFile(metadata.artifactRef).length() == size

  private fun moveScratchToStaging(scratch: File, staging: File) {
    try {
      Files.move(
        scratch.toPath(),
        staging.toPath(),
        StandardCopyOption.ATOMIC_MOVE,
        StandardCopyOption.REPLACE_EXISTING,
      )
    } catch (error: AtomicMoveNotSupportedException) {
      try {
        Files.move(
          scratch.toPath(),
          staging.toPath(),
          StandardCopyOption.REPLACE_EXISTING,
        )
      } catch (fallbackError: Exception) {
        throw FirmwareArchiveMaterializerException.integrityFailure()
      }
    } catch (error: Exception) {
      throw FirmwareArchiveMaterializerException.integrityFailure()
    }
  }

  private fun cleanupScratchFiles(directory: File) {
    directory.listFiles()
      ?.filter {
        it.name.startsWith(SCRATCH_PREFIX) &&
          it.name.endsWith(SCRATCH_SUFFIX)
      }
      ?.forEach { it.delete() }
  }

  companion object {
    private const val SCRATCH_PREFIX = "materialization-"
    private const val SCRATCH_SUFFIX = ".partial"

    @Volatile
    private var processMaterializer: FirmwareArchiveMaterializer? = null

    fun processInstance(store: FirmwareArtifactStore): FirmwareArchiveMaterializer =
      processMaterializer ?: synchronized(this) {
        processMaterializer ?: FirmwareArchiveMaterializer(store).also {
          processMaterializer = it
        }
      }
  }
}

private object FirmwareZipCentralDirectoryScanner {
  private const val END_OF_CENTRAL_DIRECTORY_SIGNATURE = 0x06054b50L
  private const val CENTRAL_DIRECTORY_SIGNATURE = 0x02014b50L
  private const val END_OF_CENTRAL_DIRECTORY_BYTES = 22
  private const val MAXIMUM_ZIP_COMMENT_BYTES = 65_535
  private const val CENTRAL_DIRECTORY_FIXED_BYTES = 46
  private const val ZIP64_SENTINEL = 0xffff_ffffL

  fun scan(file: File): List<FirmwareArchiveEntryFacts> {
    if (!file.isFile || file.length() < END_OF_CENTRAL_DIRECTORY_BYTES) {
      throw FirmwareArchiveMaterializerException.invalidArchive()
    }
    RandomAccessFile(file, "r").use { input ->
      val tailSize = minOf(
        file.length(),
        (END_OF_CENTRAL_DIRECTORY_BYTES + MAXIMUM_ZIP_COMMENT_BYTES).toLong(),
      ).toInt()
      val tail = ByteArray(tailSize)
      input.seek(file.length() - tailSize)
      input.readFully(tail)
      val eocdIndex = findSignatureBackward(
        tail,
        END_OF_CENTRAL_DIRECTORY_SIGNATURE,
      )
      if (eocdIndex < 0 ||
        eocdIndex + END_OF_CENTRAL_DIRECTORY_BYTES > tail.size
      ) {
        throw FirmwareArchiveMaterializerException.invalidArchive()
      }
      val commentLength = uint16(tail, eocdIndex + 20)
      if (eocdIndex + END_OF_CENTRAL_DIRECTORY_BYTES + commentLength !=
        tail.size
      ) {
        throw FirmwareArchiveMaterializerException.invalidArchive()
      }
      val diskNumber = uint16(tail, eocdIndex + 4)
      val centralDirectoryDisk = uint16(tail, eocdIndex + 6)
      val entriesOnDisk = uint16(tail, eocdIndex + 8)
      val totalEntries = uint16(tail, eocdIndex + 10)
      val centralDirectorySize = uint32(tail, eocdIndex + 12)
      val centralDirectoryOffset = uint32(tail, eocdIndex + 16)
      if (diskNumber != 0 ||
        centralDirectoryDisk != 0 ||
        entriesOnDisk != totalEntries ||
        totalEntries <= 0 ||
        totalEntries > FirmwareArchiveRules.MAXIMUM_ENTRY_COUNT ||
        centralDirectoryOffset == ZIP64_SENTINEL ||
        centralDirectorySize == ZIP64_SENTINEL ||
        centralDirectoryOffset + centralDirectorySize >
        file.length() - tailSize + eocdIndex
      ) {
        throw FirmwareArchiveMaterializerException.invalidArchive()
      }

      input.seek(centralDirectoryOffset)
      val entries = ArrayList<FirmwareArchiveEntryFacts>(totalEntries)
      repeat(totalEntries) { index ->
        val fixed = ByteArray(CENTRAL_DIRECTORY_FIXED_BYTES)
        input.readFully(fixed)
        if (uint32(fixed, 0) != CENTRAL_DIRECTORY_SIGNATURE) {
          throw FirmwareArchiveMaterializerException.invalidArchive()
        }
        val compressedSize = uint32(fixed, 20)
        val uncompressedSize = uint32(fixed, 24)
        val localHeaderOffset = uint32(fixed, 42)
        if (compressedSize == ZIP64_SENTINEL ||
          uncompressedSize == ZIP64_SENTINEL ||
          localHeaderOffset == ZIP64_SENTINEL ||
          localHeaderOffset >= centralDirectoryOffset
        ) {
          throw FirmwareArchiveMaterializerException.invalidArchive()
        }
        val nameLength = uint16(fixed, 28)
        val extraLength = uint16(fixed, 30)
        val commentLengthForEntry = uint16(fixed, 32)
        if (nameLength <= 0 ||
          nameLength > FirmwareArchiveRules.MAXIMUM_NAME_BYTES
        ) {
          throw FirmwareArchiveRuleException.invalidEntry()
        }
        val nameBytes = ByteArray(nameLength)
        input.readFully(nameBytes)
        val name = decodeStrictUtf8(nameBytes)
        val extra = ByteArray(extraLength)
        input.readFully(extra)
        if (containsZip64Extra(extra)) {
          throw FirmwareArchiveMaterializerException.invalidArchive()
        }
        input.seek(input.filePointer + commentLengthForEntry.toLong())
        entries += FirmwareArchiveEntryFacts(
          index = index,
          name = name,
          compressedSize = compressedSize,
          uncompressedSize = uncompressedSize,
          crc32 = uint32(fixed, 16),
          compressionMethod = uint16(fixed, 10),
          generalPurposeFlags = uint16(fixed, 8),
          versionMadeBy = uint16(fixed, 4),
          externalAttributes = uint32(fixed, 38),
          diskNumberStart = uint16(fixed, 34),
          usesZip64 = false,
        )
      }
      if (input.filePointer != centralDirectoryOffset + centralDirectorySize) {
        throw FirmwareArchiveMaterializerException.invalidArchive()
      }
      return entries
    }
  }

  private fun decodeStrictUtf8(bytes: ByteArray): String {
    return try {
      StandardCharsets.UTF_8.newDecoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT)
        .decode(ByteBuffer.wrap(bytes))
        .toString()
    } catch (error: Exception) {
      throw FirmwareArchiveRuleException.invalidEntry()
    }
  }

  private fun containsZip64Extra(bytes: ByteArray): Boolean {
    var offset = 0
    while (offset < bytes.size) {
      if (offset + 4 > bytes.size) {
        throw FirmwareArchiveMaterializerException.invalidArchive()
      }
      val headerId = uint16(bytes, offset)
      val dataSize = uint16(bytes, offset + 2)
      offset += 4
      if (offset + dataSize > bytes.size) {
        throw FirmwareArchiveMaterializerException.invalidArchive()
      }
      if (headerId == 0x0001) {
        return true
      }
      offset += dataSize
    }
    return false
  }

  private fun findSignatureBackward(bytes: ByteArray, signature: Long): Int {
    for (index in bytes.size - 4 downTo 0) {
      if (uint32(bytes, index) == signature) {
        return index
      }
    }
    return -1
  }

  private fun uint16(bytes: ByteArray, offset: Int): Int {
    if (offset < 0 || offset + 2 > bytes.size) {
      throw FirmwareArchiveMaterializerException.invalidArchive()
    }
    return (bytes[offset].toInt() and 0xff) or
      ((bytes[offset + 1].toInt() and 0xff) shl 8)
  }

  private fun uint32(bytes: ByteArray, offset: Int): Long {
    if (offset < 0 || offset + 4 > bytes.size) {
      throw FirmwareArchiveMaterializerException.invalidArchive()
    }
    return (bytes[offset].toLong() and 0xff) or
      ((bytes[offset + 1].toLong() and 0xff) shl 8) or
      ((bytes[offset + 2].toLong() and 0xff) shl 16) or
      ((bytes[offset + 3].toLong() and 0xff) shl 24)
  }
}
