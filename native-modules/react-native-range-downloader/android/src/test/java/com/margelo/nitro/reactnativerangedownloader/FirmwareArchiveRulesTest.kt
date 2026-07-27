package com.margelo.nitro.reactnativerangedownloader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class FirmwareArchiveRulesTest {
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
}
