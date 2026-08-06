package com.margelo.nitro.reactnativerangedownloader

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FirmwareArchiveRulesTest {
  @get:Rule
  val temporaryFolder = TemporaryFolder()

  private fun entry(
    artifactId: String = "resource-entry",
    entryName: String = "assets/icon.png",
    expectedSize: Double = 128.0,
    expectedSha256: String = "a".repeat(64),
  ) = FirmwareArchiveExpectedEntry(
    artifactId = artifactId,
    entryName = entryName,
    expectedSize = expectedSize,
    expectedSha256 = expectedSha256,
  )

  @Test
  fun acceptsAnExactPortableAllowlist() {
    val requirements = FirmwareArchiveRules.validateRequirements(
      arrayOf(
        entry(),
        entry(
          artifactId = "resource-entry-2",
          entryName = "assets/sub/icon-2.png",
          expectedSha256 = "b".repeat(64),
        ),
      ),
    )

    assertEquals(2, requirements.size)
  }

  @Test
  fun rejectsTraversalAndNestedArchives() {
    for (entryName in listOf("../icon.png", "assets/../icon.png", "assets.zip")) {
      assertThrows(IllegalArgumentException::class.java) {
        FirmwareArchiveRules.validateRequirements(
          arrayOf(entry(entryName = entryName)),
        )
      }
    }
  }

  @Test
  fun rejectsCaseFoldedDuplicateNames() {
    assertThrows(IllegalArgumentException::class.java) {
      FirmwareArchiveRules.validateRequirements(
        arrayOf(
          entry(entryName = "assets/Icon.png"),
          entry(
            artifactId = "resource-entry-2",
            entryName = "assets/icon.png",
          ),
        ),
      )
    }
  }

  @Test
  fun rejectsNonIntegralSizesAndInvalidDigests() {
    assertThrows(IllegalArgumentException::class.java) {
      FirmwareArchiveRules.validateRequirements(
        arrayOf(entry(expectedSize = 1.5)),
      )
    }
    assertThrows(IllegalArgumentException::class.java) {
      FirmwareArchiveRules.validateRequirements(
        arrayOf(entry(expectedSha256 = "not-a-digest")),
      )
    }
  }

  @Test
  fun acceptsPortableEntriesWithoutExpectedIntegrityMetadata() {
    val archive = createArchive("assets/icon.png", byteArrayOf(1, 2, 3))

    val entries = FirmwareArchiveRules.validateCentralDirectory(archive, null)

    assertEquals(1, entries.size)
    assertEquals("assets/icon.png", entries.single().name)
    assertEquals(3, entries.single().uncompressedSize)
  }

  @Test
  fun rejectsTraversalWithoutExpectedIntegrityMetadata() {
    val archive = createArchive("../icon.png", byteArrayOf(1))

    assertThrows(IllegalArgumentException::class.java) {
      FirmwareArchiveRules.validateCentralDirectory(archive, null)
    }
  }

  private fun createArchive(entryName: String, content: ByteArray): File {
    val archive = temporaryFolder.newFile("firmware.zip")
    ZipOutputStream(FileOutputStream(archive)).use { zip ->
      zip.putNextEntry(ZipEntry(entryName))
      zip.write(content)
      zip.closeEntry()
    }
    return archive
  }
}
