package com.margelo.nitro.reactnativerangedownloader

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Unit coverage for the dependency-free adapter logic extracted from
 * [ReactNativeRangeDownloader]: the single-flight run-key, the CAS progress
 * gate, and the per-segment artifact sweep. Pure JVM JUnit — no Nitro /
 * HybridObject / Context, mirroring the established test pattern in this module
 * (see IsPermanentHttpStatusTest).
 */
class RangeDownloadLogicTest {

  private lateinit var tmpDir: File

  @Before
  fun setUp() {
    tmpDir = File.createTempFile("crd-logic", "").let {
      it.delete(); it.mkdirs(); it
    }
  }

  @After
  fun tearDown() {
    tmpDir.deleteRecursively()
  }

  // ---- runKey (single-flight semantics) ----

  @Test
  fun runKeyJoinsChannelAndTaskWithPipe() {
    assertEquals("MAIN|task-1", RangeDownloadLogic.runKey("MAIN", "task-1"))
  }

  @Test
  fun runKeyDistinguishesChannelAndTask() {
    // Different channels for the same task must not collide.
    assertFalse(
      RangeDownloadLogic.runKey("MAIN", "t") == RangeDownloadLogic.runKey("OTHER", "t"),
    )
    // Different tasks on the same channel must not collide.
    assertFalse(
      RangeDownloadLogic.runKey("MAIN", "a") == RangeDownloadLogic.runKey("MAIN", "b"),
    )
  }

  // ---- progressPercent (CAS gate input) ----

  @Test
  fun progressPercentComputesFlooredPercentage() {
    assertEquals(0, RangeDownloadLogic.progressPercent(0, 100))
    assertEquals(50, RangeDownloadLogic.progressPercent(50, 100))
    // Integer floor, never rounds up.
    assertEquals(33, RangeDownloadLogic.progressPercent(1, 3))
    assertEquals(100, RangeDownloadLogic.progressPercent(100, 100))
  }

  @Test
  fun progressPercentClampsToHundred() {
    // transferred can momentarily exceed total across segments; clamp.
    assertEquals(100, RangeDownloadLogic.progressPercent(150, 100))
  }

  @Test
  fun progressPercentReturnsNullForNonPositiveTotal() {
    assertNull(RangeDownloadLogic.progressPercent(10, 0))
    assertNull(RangeDownloadLogic.progressPercent(10, -1))
  }

  /**
   * The CAS gate the adapter wraps around [progressPercent]: only a strictly
   * higher percentage emits, so the stream stays monotonic and de-duped. This
   * reproduces the adapter's `p > prev && compareAndSet` loop over a sequence
   * of out-of-order, repeating worker callbacks.
   */
  @Test
  fun casGateKeepsProgressMonotonicAndDeduped() {
    var prev = -1
    val emitted = mutableListOf<Int>()
    // total fixed at 100; transferred arrives out of order with repeats.
    for (transferred in listOf(10L, 10L, 25L, 25L, 20L, 50L, 50L, 49L, 100L)) {
      val p = RangeDownloadLogic.progressPercent(transferred, 100) ?: continue
      if (p > prev) {
        prev = p
        emitted.add(p)
      }
    }
    assertEquals(listOf(10, 25, 50, 100), emitted)
  }

  // ---- sweepPartialArtifacts ----

  @Test
  fun sweepRemovesPartialAndEverySegmentByPrefix() {
    val dest = File(tmpDir, "asset.bin").absolutePath
    val partial = File("$dest.partial")
    partial.writeText("p")
    // Custom (non-default) segment count: 12 segments must all be swept.
    val segs = (0 until 12).map { File("$dest.partial.seg$it").apply { writeText("$it") } }
    // An unrelated sibling must be left untouched.
    val unrelated = File(tmpDir, "asset.bin.keep").apply { writeText("k") }

    RangeDownloadLogic.sweepPartialArtifacts(dest)

    assertFalse("partial should be deleted", partial.exists())
    segs.forEach { assertFalse("${it.name} should be deleted", it.exists()) }
    assertTrue("unrelated sibling must survive", unrelated.exists())
  }

  @Test
  fun sweepIsNoOpWhenNothingExists() {
    val dest = File(tmpDir, "missing.bin").absolutePath
    // Must not throw when there are no artifacts to remove.
    RangeDownloadLogic.sweepPartialArtifacts(dest)
    assertFalse(File("$dest.partial").exists())
  }
}
