package com.margelo.nitro.reactnativerangedownloader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

class FirmwareArchiveRulesTest {
  @Test
  fun discoversRegularFilesAndIgnoresSafeDirectoryAndMacMetadata() {
    val validated = FirmwareArchiveRules.validate(
      entries = listOf(
        directoryFacts(index = 0, name = "firmware/"),
        facts(index = 1, name = "firmware/main.bin", size = 1_024),
        facts(index = 2, name = "resources/ble.bin", size = 512),
        directoryFacts(index = 3, name = "__MACOSX/"),
        facts(
          index = 4,
          name = "__MACOSX/firmware/main.bin",
          size = 32,
        ),
      ),
      materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
    )

    val materialized = validated.filter {
      it.disposition == FirmwareArchiveEntryDisposition.MATERIALIZE
    }
    assertEquals(
      listOf("firmware/main.bin", "resources/ble.bin"),
      materialized.map { it.entryId },
    )
    assertEquals(
      listOf("main.bin", "ble.bin"),
      materialized.map { it.logicalName },
    )
    assertEquals(
      2,
      validated.count {
        it.disposition == FirmwareArchiveEntryDisposition.IGNORE_DIRECTORY
      },
    )
    assertEquals(
      1,
      validated.count {
        it.disposition == FirmwareArchiveEntryDisposition.IGNORE_METADATA
      },
    )
    assertEquals(
      listOf(0, 1, 2, 3, 4),
      FirmwareArchiveRules.entriesRequiringIntegrityValidation(validated)
        .map { it.facts.index },
    )
  }

  @Test
  fun rejectsTraversalWindowsDeviceAdsAndPortableCollisions() {
    val unsafeNames = listOf(
      "../main.bin",
      "/main.bin",
      "\\\\server\\share\\main.bin",
      "\\\\?\\C:\\main.bin",
      "C:\\main.bin",
      "firmware\\main.bin",
      "main.bin:stream",
      "CON",
      "firmware/con.bin",
      "firmware/AUX.txt",
      "firmware/COM1",
      "firmware/lpt9.bin",
      "firmware/NUL",
      "firmware//main.bin",
      "firmware/./main.bin",
      "firmware/main.bin.",
      "firmware/main.bin ",
      "firmware/cafe\u0301.bin",
    )
    unsafeNames.forEach { name ->
      assertFalse(name, FirmwareArchiveRules.isCanonicalPortableName(name))
    }

    assertThrows(FirmwareArchiveRuleException::class.java) {
      FirmwareArchiveRules.validate(
        entries = listOf(
          facts(index = 0, name = "one/main.bin", size = 1_024),
          facts(index = 1, name = "two/MAIN.bin", size = 512),
        ),
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }
    assertThrows(FirmwareArchiveRuleException::class.java) {
      FirmwareArchiveRules.validate(
        entries = listOf(
          facts(index = 0, name = "one/Straße.bin", size = 1_024),
          facts(index = 1, name = "two/STRASSE.bin", size = 512),
        ),
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }
    assertThrows(FirmwareArchiveRuleException::class.java) {
      FirmwareArchiveRules.validate(
        entries = listOf(
          facts(index = 0, name = "one/ß.bin", size = 1_024),
          facts(index = 1, name = "two/ss.bin", size = 512),
        ),
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }
    assertThrows(FirmwareArchiveRuleException::class.java) {
      FirmwareArchiveRules.validate(
        entries = listOf(
          facts(
            index = 0,
            name = "firmware/${"é".repeat(63)}.bin",
            size = 512,
          ),
        ),
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }
  }

  @Test
  fun rejectsInvalidPolicyEncryptedLinkNestedArchiveAndOversize() {
    val regular = listOf(
      facts(index = 0, name = "firmware/main.bin", size = 1_024),
    )
    assertThrows(FirmwareArchiveRuleException::class.java) {
      FirmwareArchiveRules.validate(
        entries = regular,
        materializationPolicy = "v1-allow-list",
      )
    }
    assertThrows(FirmwareArchiveRuleException::class.java) {
      FirmwareArchiveRules.validate(
        entries = listOf(
          facts(index = 0, name = "firmware/main.bin", size = 1_024, flags = 1),
        ),
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }
    assertThrows(FirmwareArchiveRuleException::class.java) {
      FirmwareArchiveRules.validate(
        entries = listOf(
          facts(
            index = 0,
            name = "firmware/main.bin",
            size = 1_024,
            externalAttributes = 0b1010_0001_1111_1111L shl 16,
          ),
        ),
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }
    assertThrows(FirmwareArchiveRuleException::class.java) {
      FirmwareArchiveRules.validate(
        entries = listOf(
          facts(
            index = 0,
            name = "__MACOSX/update.zip",
            size = 1_024,
          ),
        ),
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }
    assertThrows(FirmwareArchiveRuleException::class.java) {
      FirmwareArchiveRules.validate(
        entries = listOf(
          facts(
            index = 0,
            name = "firmware/main.bin",
            size = FirmwareArchiveRules.MAXIMUM_ENTRY_UNCOMPRESSED_BYTES + 1,
          ),
        ),
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }
  }

  @Test
  fun rejectsZip64Entries() {
    assertThrows(FirmwareArchiveRuleException::class.java) {
      FirmwareArchiveRules.validate(
        entries = listOf(
          facts(
            index = 0,
            name = "firmware/main.bin",
            size = 1_024,
            usesZip64 = true,
          ),
        ),
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }
  }

  @Test
  fun countsEveryCentralRecordAndCapsTotalExpansion() {
    val tooMany = (0..FirmwareArchiveRules.MAXIMUM_ENTRY_COUNT).map {
      directoryFacts(index = it, name = "directory-$it/")
    }
    assertThrows(FirmwareArchiveRuleException::class.java) {
      FirmwareArchiveRules.validate(
        entries = tooMany,
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }

    val tooLarge = (0 until 5).map {
      facts(
        index = it,
        name = "firmware/file-$it.bin",
        size = FirmwareArchiveRules.MAXIMUM_ENTRY_UNCOMPRESSED_BYTES,
      )
    }
    assertThrows(FirmwareArchiveRuleException::class.java) {
      FirmwareArchiveRules.validate(
        entries = tooLarge,
        materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
      )
    }
  }

  @Test
  fun acceptsProScaleArchiveEntryCount() {
    val entries = (0 until 1_201).map {
      facts(
        index = it,
        name = "resources/resource-$it.bin",
        size = 1,
      )
    }
    val validated = FirmwareArchiveRules.validate(
      entries = entries,
      materializationPolicy = FirmwareArchiveRules.MATERIALIZATION_POLICY,
    )

    assertEquals(1_201, validated.size)
  }

  @Test
  fun childTaskIdentityMatchesAppContract() {
    assertEquals(
      "fw-child-7211b23ee276863b96ee8461fb17f28d146e8c40ba49376869d853904f4cf98a",
      FirmwareArchiveRules.childTaskId(
        parentArtifactId = "firmware-archive",
        sha256 = "A".repeat(64),
        size = 1_024,
      ),
    )
  }

  private fun facts(
    index: Int,
    name: String,
    size: Long,
    flags: Int = 0,
    externalAttributes: Long = 0b1000_0001_1010_0100L shl 16,
    usesZip64: Boolean = false,
  ): FirmwareArchiveEntryFacts =
    FirmwareArchiveEntryFacts(
      index = index,
      name = name,
      compressedSize = maxOf(1, size / 2),
      uncompressedSize = size,
      crc32 = 0,
      compressionMethod = 8,
      generalPurposeFlags = flags,
      versionMadeBy = 3 shl 8,
      externalAttributes = externalAttributes,
      diskNumberStart = 0,
      usesZip64 = usesZip64,
    )

  private fun directoryFacts(
    index: Int,
    name: String,
  ): FirmwareArchiveEntryFacts =
    FirmwareArchiveEntryFacts(
      index = index,
      name = name,
      compressedSize = 0,
      uncompressedSize = 0,
      crc32 = 0,
      compressionMethod = 0,
      generalPurposeFlags = 0,
      versionMadeBy = 3 shl 8,
      externalAttributes = 0b0100_0001_1110_1101L shl 16,
      diskNumberStart = 0,
    )
}
