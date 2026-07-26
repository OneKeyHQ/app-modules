package com.margelo.nitro.reactnativerangedownloader

import java.text.Normalizer

internal data class FirmwareArchiveEntryFacts(
  val index: Int,
  val name: String,
  val compressedSize: Long,
  val uncompressedSize: Long,
  val crc32: Long,
  val compressionMethod: Int,
  val generalPurposeFlags: Int,
  val versionMadeBy: Int,
  val externalAttributes: Long,
  val diskNumberStart: Int,
  val usesZip64: Boolean = false,
)

internal enum class FirmwareArchiveEntryDisposition {
  MATERIALIZE,
  IGNORE_DIRECTORY,
  IGNORE_METADATA,
}

internal data class FirmwareArchiveValidatedEntry(
  val facts: FirmwareArchiveEntryFacts,
  val entryId: String,
  val logicalName: String,
  val disposition: FirmwareArchiveEntryDisposition,
)

internal class FirmwareArchiveRuleException private constructor(
  val code: String,
) : Exception(code) {
  companion object {
    fun invalidPolicy() =
      FirmwareArchiveRuleException("ARTIFACT_ARCHIVE_INVALID_POLICY")
    fun invalidEntry() =
      FirmwareArchiveRuleException("ARTIFACT_ARCHIVE_INVALID_ENTRY")
    fun unsupportedEntry() =
      FirmwareArchiveRuleException("ARTIFACT_ARCHIVE_UNSUPPORTED_ENTRY")
    fun limitExceeded() =
      FirmwareArchiveRuleException("ARTIFACT_ARCHIVE_LIMIT_EXCEEDED")
  }
}

internal object FirmwareArchiveRules {
  const val MATERIALIZATION_POLICY = "v3-resource-files-v1"
  const val MAXIMUM_ENTRY_COUNT = 4_096
  const val MAXIMUM_NAME_BYTES = 1_024
  const val MAXIMUM_LOGICAL_NAME_BYTES = 128
  const val MAXIMUM_ENTRY_UNCOMPRESSED_BYTES = 128L * 1024L * 1024L
  const val MAXIMUM_TOTAL_UNCOMPRESSED_BYTES = 512L * 1024L * 1024L
  const val MAXIMUM_COMPRESSION_RATIO = 1_000L
  const val EXTRACTION_BUFFER_BYTES = 64 * 1024

  private enum class EntryKind {
    FILE,
    DIRECTORY,
  }

  private const val ENCRYPTED_FLAG = 1
  private const val UNIX_FILE_TYPE_MASK = 0b1111_0000_0000_0000
  private const val UNIX_REGULAR_FILE = 0b1000_0000_0000_0000
  private const val UNIX_DIRECTORY = 0b0100_0000_0000_0000
  private const val DOS_DIRECTORY_FLAG = 0x10L
  private val unixHostSystems = setOf(3, 19)
  private val nestedArchiveExtensions = setOf(
    "7z", "bz2", "cab", "gz", "rar", "tar", "tgz", "xz", "zip",
  )
  private val windowsDeviceNames = setOf(
    "AUX", "CON", "NUL", "PRN",
    "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
    "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9",
  )

  fun validate(
    entries: List<FirmwareArchiveEntryFacts>,
    materializationPolicy: String,
  ): List<FirmwareArchiveValidatedEntry> {
    if (materializationPolicy != MATERIALIZATION_POLICY) {
      throw FirmwareArchiveRuleException.invalidPolicy()
    }
    if (entries.isEmpty()) {
      throw FirmwareArchiveRuleException.invalidEntry()
    }
    if (entries.size > MAXIMUM_ENTRY_COUNT) {
      throw FirmwareArchiveRuleException.limitExceeded()
    }

    val seenIndexes = mutableSetOf<Int>()
    val seenNames = mutableSetOf<String>()
    val pathCollisionKeys = mutableSetOf<String>()
    val logicalNameCollisionKeys = mutableSetOf<String>()
    var totalDeclared = 0L
    var materializedCount = 0
    val validated = ArrayList<FirmwareArchiveValidatedEntry>(entries.size)

    entries.forEach { facts ->
      val kind = entryKind(facts)
      val entryId = canonicalEntryId(
        facts.name,
        isDirectory = kind == EntryKind.DIRECTORY,
      )
      val logicalName = entryId.substringAfterLast('/')
      if (facts.index < 0 ||
        !seenIndexes.add(facts.index) ||
        !seenNames.add(facts.name) ||
        !pathCollisionKeys.add(portableCollisionKey(entryId))
      ) {
        throw FirmwareArchiveRuleException.invalidEntry()
      }
      if (facts.diskNumberStart != 0 ||
        facts.usesZip64 ||
        facts.generalPurposeFlags and ENCRYPTED_FLAG != 0 ||
        facts.compressionMethod !in setOf(0, 8)
      ) {
        throw FirmwareArchiveRuleException.unsupportedEntry()
      }

      val disposition = when (kind) {
        EntryKind.DIRECTORY -> {
          val hasValidEncoding =
            (facts.compressionMethod == 0 && facts.compressedSize == 0L) ||
              (facts.compressionMethod == 8 &&
                facts.compressedSize in 0L..64L)
          if (facts.uncompressedSize != 0L || !hasValidEncoding) {
            throw FirmwareArchiveRuleException.invalidEntry()
          }
          FirmwareArchiveEntryDisposition.IGNORE_DIRECTORY
        }
        EntryKind.FILE -> {
          if (isNestedArchive(entryId)) {
            throw FirmwareArchiveRuleException.unsupportedEntry()
          }
          if (facts.compressedSize < 0 || facts.uncompressedSize <= 0) {
            throw FirmwareArchiveRuleException.invalidEntry()
          }
          if (facts.uncompressedSize > MAXIMUM_ENTRY_UNCOMPRESSED_BYTES) {
            throw FirmwareArchiveRuleException.limitExceeded()
          }
          validateCompressionRatio(
            compressedSize = facts.compressedSize,
            uncompressedSize = facts.uncompressedSize,
          )
          totalDeclared = checkedAdd(
            totalDeclared,
            facts.uncompressedSize,
            MAXIMUM_TOTAL_UNCOMPRESSED_BYTES,
          )
          if (isIgnoredMetadata(entryId)) {
            FirmwareArchiveEntryDisposition.IGNORE_METADATA
          } else {
            if (
              logicalName.toByteArray(Charsets.UTF_8).size >
              MAXIMUM_LOGICAL_NAME_BYTES
            ) {
              throw FirmwareArchiveRuleException.invalidEntry()
            }
            if (!logicalNameCollisionKeys.add(
                portableCollisionKey(logicalName)
              )
            ) {
              throw FirmwareArchiveRuleException.invalidEntry()
            }
            materializedCount += 1
            FirmwareArchiveEntryDisposition.MATERIALIZE
          }
        }
      }

      validated += FirmwareArchiveValidatedEntry(
        facts = facts,
        entryId = entryId,
        logicalName = logicalName,
        disposition = disposition,
      )
    }

    if (materializedCount == 0) {
      throw FirmwareArchiveRuleException.invalidEntry()
    }
    return validated
  }

  fun entriesRequiringIntegrityValidation(
    entries: List<FirmwareArchiveValidatedEntry>,
  ): List<FirmwareArchiveValidatedEntry> =
    entries.sortedBy { it.facts.index }

  fun isCanonicalPortableName(name: String): Boolean {
    if (name.isEmpty() ||
      name.toByteArray(Charsets.UTF_8).size > MAXIMUM_NAME_BYTES ||
      name != Normalizer.normalize(name, Normalizer.Form.NFC) ||
      name.startsWith("/") ||
      name.startsWith("\\") ||
      "\\" in name ||
      name.any { it == '\u0000' || it.code < 0x20 || it.code == 0x7f } ||
      ":" in name
    ) {
      return false
    }
    val components = name.split('/')
    return components.isNotEmpty() && components.all { component ->
      component.isNotEmpty() &&
        component != "." &&
        component != ".." &&
        !component.endsWith(".") &&
        !component.endsWith(" ") &&
        !isWindowsDeviceName(component)
    }
  }

  fun childTaskId(
    parentArtifactId: String,
    sha256: String,
    size: Long,
  ): String {
    val identity = "$parentArtifactId\u0000${sha256.lowercase()}\u0000$size"
    return "fw-child-${StoredArtifactMetadata.sha256(identity)}"
  }

  private fun canonicalEntryId(
    name: String,
    isDirectory: Boolean,
  ): String {
    if (name.toByteArray(Charsets.UTF_8).size > MAXIMUM_NAME_BYTES) {
      throw FirmwareArchiveRuleException.invalidEntry()
    }
    val logicalName = if (isDirectory) {
      if (!name.endsWith("/") || name.endsWith("//")) {
        throw FirmwareArchiveRuleException.invalidEntry()
      }
      name.dropLast(1)
    } else {
      if (name.endsWith("/")) {
        throw FirmwareArchiveRuleException.invalidEntry()
      }
      name
    }
    if (!isCanonicalPortableName(logicalName)) {
      throw FirmwareArchiveRuleException.invalidEntry()
    }
    return logicalName
  }

  private fun entryKind(facts: FirmwareArchiveEntryFacts): EntryKind {
    val hostSystem = facts.versionMadeBy ushr 8 and 0xff
    val hasDirectoryName = facts.name.endsWith("/")
    val hasDosDirectoryFlag =
      facts.externalAttributes and DOS_DIRECTORY_FLAG != 0L

    if (hostSystem in unixHostSystems) {
      val mode = facts.externalAttributes.ushr(16).toInt() and
        UNIX_FILE_TYPE_MASK
      return when (mode) {
        UNIX_DIRECTORY -> {
          if (!hasDirectoryName) {
            throw FirmwareArchiveRuleException.invalidEntry()
          }
          EntryKind.DIRECTORY
        }
        UNIX_REGULAR_FILE -> {
          if (hasDirectoryName || hasDosDirectoryFlag) {
            throw FirmwareArchiveRuleException.invalidEntry()
          }
          EntryKind.FILE
        }
        0 -> {
          if (hasDirectoryName || hasDosDirectoryFlag) {
            EntryKind.DIRECTORY
          } else {
            EntryKind.FILE
          }
        }
        else -> throw FirmwareArchiveRuleException.unsupportedEntry()
      }
    }
    return if (hasDirectoryName || hasDosDirectoryFlag) {
      EntryKind.DIRECTORY
    } else {
      EntryKind.FILE
    }
  }

  private fun isWindowsDeviceName(component: String): Boolean =
    component.substringBefore('.').uppercase() in windowsDeviceNames

  private fun portableCollisionKey(name: String): String =
    Normalizer.normalize(
      Normalizer.normalize(name, Normalizer.Form.NFC)
        .uppercase()
        .lowercase(),
      Normalizer.Form.NFC,
    )

  private fun isIgnoredMetadata(name: String): Boolean {
    return name.split('/').any {
      portableCollisionKey(it) == "__macosx"
    }
  }

  private fun isNestedArchive(name: String): Boolean {
    val extension = name
      .split('/')
      .lastOrNull()
      ?.substringAfterLast('.', "")
      ?.lowercase()
      .orEmpty()
    return extension in nestedArchiveExtensions
  }

  private fun validateCompressionRatio(
    compressedSize: Long,
    uncompressedSize: Long,
  ) {
    if (compressedSize <= 0) {
      throw FirmwareArchiveRuleException.limitExceeded()
    }
    if (compressedSize <= Long.MAX_VALUE / MAXIMUM_COMPRESSION_RATIO &&
      uncompressedSize > compressedSize * MAXIMUM_COMPRESSION_RATIO
    ) {
      throw FirmwareArchiveRuleException.limitExceeded()
    }
  }

  private fun checkedAdd(left: Long, right: Long, limit: Long): Long {
    if (left < 0 || right < 0 || right > limit - left) {
      throw FirmwareArchiveRuleException.limitExceeded()
    }
    return left + right
  }
}
