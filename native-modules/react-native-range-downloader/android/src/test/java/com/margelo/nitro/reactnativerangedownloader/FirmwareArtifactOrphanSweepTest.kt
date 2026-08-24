package com.margelo.nitro.reactnativerangedownloader

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FirmwareArtifactOrphanSweepTest {
  @get:Rule
  val temporaryFolder = TemporaryFolder()

  @Test
  fun removesOnlyStaleRootScratchEntriesWithExactNamesAndTypes() {
    val root = temporaryFolder.newFolder("firmware-artifact-sweep")
    val nowMs = 2_000_000_000_000L
    val staleAt = nowMs - FIRMWARE_ARTIFACT_SCRATCH_GRACE_MS - 1
    val freshAt = nowMs - FIRMWARE_ARTIFACT_SCRATCH_GRACE_MS + 1
    val staleArchive = File(
      root,
      "archive-00000000-0000-4000-8000-000000000001",
    ).apply {
      assertTrue(mkdirs())
      File(this, "0.entry").writeText("archive")
    }
    val stalePromote = File(
      root,
      ".promote-00000000-0000-4000-8000-000000000002",
    ).apply { writeText("promote") }
    val freshArchive = File(
      root,
      "archive-00000000-0000-4000-8000-000000000003",
    ).apply { assertTrue(mkdirs()) }
    val freshPromote = File(
      root,
      ".promote-00000000-0000-4000-8000-000000000004",
    ).apply { writeText("promote") }
    val malformedArchive = File(
      root,
      "archive-00000000-0000-4000-8000-000000000005.extra",
    ).apply { assertTrue(mkdirs()) }
    val malformedPromote = File(
      root,
      ".promote-00000000-0000-4000-8000-000000000006.tmp",
    ).apply { writeText("promote") }
    val archiveNamedFile = File(
      root,
      "archive-00000000-0000-4000-8000-000000000007",
    ).apply { writeText("promote") }
    val promoteNamedDirectory = File(
      root,
      ".promote-00000000-0000-4000-8000-000000000008",
    ).apply { assertTrue(mkdirs()) }
    val nestedArchive = File(
      File(root, "nested"),
      "archive-00000000-0000-4000-8000-000000000009",
    ).apply { assertTrue(mkdirs()) }

    for (
      entry in listOf(
        staleArchive,
        stalePromote,
        malformedArchive,
        malformedPromote,
        archiveNamedFile,
        promoteNamedDirectory,
        nestedArchive,
      )
    ) {
      assertTrue(entry.setLastModified(staleAt))
    }
    for (entry in listOf(freshArchive, freshPromote)) {
      assertTrue(entry.setLastModified(freshAt))
    }

    val result = sweepFirmwareArtifactOrphansAtRoot(
      root = root,
      retainedSha256 = emptySet(),
      activeSha256 = emptySet(),
      openPaths = emptySet(),
      nowMs = nowMs,
    )

    assertEquals(2, result.first)
    assertEquals(14L, result.second)
    assertFalse(staleArchive.exists())
    assertFalse(stalePromote.exists())
    for (
      retainedEntry in listOf(
        freshArchive,
        freshPromote,
        malformedArchive,
        malformedPromote,
        archiveNamedFile,
        promoteNamedDirectory,
        nestedArchive,
      )
    ) {
      assertTrue(
        "Unexpectedly removed ${retainedEntry.name}",
        retainedEntry.exists(),
      )
    }
  }
}
