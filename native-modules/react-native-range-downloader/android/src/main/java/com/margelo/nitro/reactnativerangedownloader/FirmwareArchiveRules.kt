package com.margelo.nitro.reactnativerangedownloader

import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets
import java.text.Normalizer
import java.util.Locale

internal data class FirmwareArchiveCentralEntry(
  val name: String,
  val compressedSize: Long,
  val uncompressedSize: Long,
  val crc32: Long,
  val flags: Int,
  val compressionMethod: Int,
  val versionMadeBy: Int,
  val externalAttributes: Long,
  val diskNumber: Int,
)

internal object FirmwareArchiveRules {
  private const val MAX_EOCD_SEARCH_BYTES = 65_557
  private const val EOCD_SIGNATURE = 0x06054b50L
  private const val CENTRAL_ENTRY_SIGNATURE = 0x02014b50L
  private const val MAX_ARCHIVE_ENTRIES = 4096
  private const val MAX_ARCHIVE_ENTRY_BYTES = 128L * 1024 * 1024
  private const val MAX_ARCHIVE_EXPANDED_BYTES = 512L * 1024 * 1024
  private val nestedArchiveExtensions = setOf(
    ".zip",
    ".7z",
    ".rar",
    ".tar",
    ".gz",
    ".tgz",
  )
  private val sha256Pattern = Regex("^[a-fA-F0-9]{64}$")

  fun validateRequirements(
    expectedEntries: Array<FirmwareArchiveExpectedEntry>,
  ): List<FirmwareArchiveExpectedEntry> {
    require(expectedEntries.isNotEmpty() && expectedEntries.size <= MAX_ARCHIVE_ENTRIES) {
      "Firmware archive expected entry count is invalid"
    }
    val names = mutableSetOf<String>()
    val canonicalNames = mutableSetOf<String>()
    var totalSize = 0L
    expectedEntries.forEach { entry ->
      require(
        entry.artifactId.isNotEmpty() &&
          entry.artifactId.length <= 160 &&
          entry.artifactId.all { it.isLetterOrDigit() || it in "._-" }
      ) {
        "Firmware archive artifactId is invalid"
      }
      require(
        names.add(entry.entryName) &&
          validatePortableName(entry.entryName, canonicalNames)
      ) {
        "Firmware archive expected entry name is invalid"
      }
      val expectedSize = entry.expectedSize.toExactPositiveLong("entry size")
      require(expectedSize <= MAX_ARCHIVE_ENTRY_BYTES) {
        "Firmware archive entry exceeds size limits"
      }
      totalSize = Math.addExact(totalSize, expectedSize)
      require(totalSize <= MAX_ARCHIVE_EXPANDED_BYTES) {
        "Firmware archive expected entries exceed size limits"
      }
      require(sha256Pattern.matches(entry.expectedSha256)) {
        "Firmware archive entry SHA-256 is invalid"
      }
    }
    return expectedEntries.toList()
  }

  fun validateCentralDirectory(
    file: File,
    requirements: List<FirmwareArchiveExpectedEntry>?,
  ): List<FirmwareArchiveCentralEntry> {
    val entries = scanCentralDirectory(file)
    require(requirements == null || entries.size == requirements.size) {
      "Firmware archive has missing or extra entries"
    }
    val requirementsByName = requirements?.associateBy { it.entryName }.orEmpty()
    val names = mutableSetOf<String>()
    val canonicalNames = mutableSetOf<String>()
    var totalSize = 0L
    entries.forEach { entry ->
      val requirement = requirementsByName[entry.name]
      require(requirements == null || requirement != null) {
        "Firmware archive contains an unexpected entry"
      }
      val expectedSize = requirement?.expectedSize?.toLong()
        ?: entry.uncompressedSize
      totalSize = Math.addExact(totalSize, entry.uncompressedSize)
      require(
        names.add(entry.name) &&
          validatePortableName(entry.name, canonicalNames) &&
          entry.uncompressedSize == expectedSize &&
          entry.uncompressedSize > 0 &&
          entry.uncompressedSize <= MAX_ARCHIVE_ENTRY_BYTES &&
          totalSize <= MAX_ARCHIVE_EXPANDED_BYTES &&
          entry.compressedSize in 0..MAX_ARCHIVE_EXPANDED_BYTES &&
          entry.uncompressedSize <=
          Math.multiplyExact(entry.compressedSize.coerceAtLeast(1), 1000) &&
          entry.diskNumber == 0 &&
          entry.flags and 1 == 0 &&
          (entry.compressionMethod == 0 || entry.compressionMethod == 8) &&
          isRegularEntry(entry)
      ) {
        "Firmware archive entry metadata is invalid"
      }
    }
    return entries
  }

  private fun scanCentralDirectory(file: File): List<FirmwareArchiveCentralEntry> {
    require(file.isFile && file.length() >= 22) {
      "Firmware archive is invalid"
    }
    RandomAccessFile(file, "r").use { archive ->
      val tailSize = minOf(file.length(), MAX_EOCD_SEARCH_BYTES.toLong()).toInt()
      val tail = ByteArray(tailSize)
      archive.seek(file.length() - tailSize)
      archive.readFully(tail)
      val eocdIndex = findSignatureBackwards(tail, EOCD_SIGNATURE)
      require(eocdIndex >= 0 && eocdIndex + 22 <= tail.size) {
        "Firmware archive EOCD is missing"
      }
      val eocd = ByteBuffer.wrap(tail, eocdIndex, tail.size - eocdIndex)
        .order(ByteOrder.LITTLE_ENDIAN)
      require(eocd.int.toUnsignedLong() == EOCD_SIGNATURE) {
        "Firmware archive EOCD is invalid"
      }
      val diskNumber = eocd.short.toUnsignedInt()
      val centralDisk = eocd.short.toUnsignedInt()
      val entriesOnDisk = eocd.short.toUnsignedInt()
      val totalEntries = eocd.short.toUnsignedInt()
      val centralSize = eocd.int.toUnsignedLong()
      val centralOffset = eocd.int.toUnsignedLong()
      val commentLength = eocd.short.toUnsignedInt()
      require(
        diskNumber == 0 &&
          centralDisk == 0 &&
          entriesOnDisk == totalEntries &&
          totalEntries in 1..MAX_ARCHIVE_ENTRIES &&
          totalEntries != 0xffff &&
          centralSize != 0xffff_ffffL &&
          centralOffset != 0xffff_ffffL &&
          eocdIndex + 22 + commentLength == tail.size &&
          centralOffset <= file.length() - centralSize &&
          centralOffset + centralSize <= file.length() - tailSize + eocdIndex
      ) {
        "Firmware archive is multi-disk, ZIP64, or malformed"
      }

      archive.seek(centralOffset)
      val entries = ArrayList<FirmwareArchiveCentralEntry>(totalEntries)
      repeat(totalEntries) {
        val header = ByteArray(46)
        archive.readFully(header)
        val buffer = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)
        require(buffer.int.toUnsignedLong() == CENTRAL_ENTRY_SIGNATURE) {
          "Firmware archive central entry is malformed"
        }
        val versionMadeBy = buffer.short.toUnsignedInt()
        buffer.short
        val flags = buffer.short.toUnsignedInt()
        val method = buffer.short.toUnsignedInt()
        buffer.position(buffer.position() + 4)
        val crc32 = buffer.int.toUnsignedLong()
        val compressedSize = buffer.int.toUnsignedLong()
        val uncompressedSize = buffer.int.toUnsignedLong()
        val nameLength = buffer.short.toUnsignedInt()
        val extraLength = buffer.short.toUnsignedInt()
        val entryCommentLength = buffer.short.toUnsignedInt()
        val entryDisk = buffer.short.toUnsignedInt()
        buffer.short
        val externalAttributes = buffer.int.toUnsignedLong()
        val localHeaderOffset = buffer.int.toUnsignedLong()
        require(
          nameLength in 1..4096 &&
            compressedSize != 0xffff_ffffL &&
            uncompressedSize != 0xffff_ffffL &&
            entryDisk != 0xffff &&
            localHeaderOffset != 0xffff_ffffL &&
            localHeaderOffset < centralOffset
        ) {
          "Firmware archive ZIP64 entry is not supported"
        }
        val nameBytes = ByteArray(nameLength)
        archive.readFully(nameBytes)
        val name = decodeEntryName(nameBytes, flags)
        val skipLength = Math.addExact(extraLength, entryCommentLength)
        archive.seek(Math.addExact(archive.filePointer, skipLength.toLong()))
        entries += FirmwareArchiveCentralEntry(
          name = name,
          compressedSize = compressedSize,
          uncompressedSize = uncompressedSize,
          crc32 = crc32,
          flags = flags,
          compressionMethod = method,
          versionMadeBy = versionMadeBy,
          externalAttributes = externalAttributes,
          diskNumber = entryDisk,
        )
      }
      require(archive.filePointer == centralOffset + centralSize) {
        "Firmware archive central directory size mismatch"
      }
      return entries
    }
  }

  private fun decodeEntryName(bytes: ByteArray, flags: Int): String {
    if (flags and (1 shl 11) == 0) {
      require(bytes.all { it.toInt() and 0xff < 0x80 }) {
        "Firmware archive entry name must be UTF-8 or ASCII"
      }
      return String(bytes, StandardCharsets.US_ASCII)
    }
    val decoder = StandardCharsets.UTF_8.newDecoder()
      .onMalformedInput(CodingErrorAction.REPORT)
      .onUnmappableCharacter(CodingErrorAction.REPORT)
    return decoder.decode(ByteBuffer.wrap(bytes)).toString()
  }

  private fun validatePortableName(
    name: String,
    canonicalNames: MutableSet<String>,
  ): Boolean {
    val normalized = Normalizer.normalize(name, Normalizer.Form.NFC)
    val folded = normalized.lowercase(Locale.ROOT)
    val components = name.split('/')
    return name.isNotEmpty() &&
      name.length <= 512 &&
      name == normalized &&
      !name.startsWith("/") &&
      !name.startsWith("\\") &&
      '\\' !in name &&
      ':' !in name &&
      name.none { it.code < 0x20 || it.code == 0x7f } &&
      components.all {
        it.isNotEmpty() &&
          it != "." &&
          it != ".." &&
          !it.endsWith(".") &&
          !it.endsWith(" ")
      } &&
      nestedArchiveExtensions.none { folded.endsWith(it) } &&
      canonicalNames.add(folded)
  }

  private fun isRegularEntry(entry: FirmwareArchiveCentralEntry): Boolean {
    val hostSystem = entry.versionMadeBy ushr 8
    if (hostSystem == 3 || hostSystem == 19) {
      val fileType = (entry.externalAttributes ushr 16) and 0xf000
      return fileType == 0L || fileType == 0x8000L
    }
    return entry.externalAttributes and 0x10 == 0L
  }

  private fun findSignatureBackwards(bytes: ByteArray, signature: Long): Int {
    for (index in bytes.size - 4 downTo 0) {
      val value = (bytes[index].toLong() and 0xff) or
        ((bytes[index + 1].toLong() and 0xff) shl 8) or
        ((bytes[index + 2].toLong() and 0xff) shl 16) or
        ((bytes[index + 3].toLong() and 0xff) shl 24)
      if (value == signature) return index
    }
    return -1
  }

  private fun Int.toUnsignedLong(): Long = toLong() and 0xffff_ffffL

  private fun Short.toUnsignedInt(): Int = toInt() and 0xffff

  private fun Double.toExactPositiveLong(label: String): Long {
    require(isFinite() && this > 0 && this <= Long.MAX_VALUE.toDouble()) {
      "Invalid firmware archive $label"
    }
    val converted = toLong()
    require(converted.toDouble() == this) {
      "Invalid firmware archive $label"
    }
    return converted
  }
}
