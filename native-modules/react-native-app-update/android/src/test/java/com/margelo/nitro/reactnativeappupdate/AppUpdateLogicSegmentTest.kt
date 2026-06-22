package com.margelo.nitro.reactnativeappupdate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pin tests for the extracted pure unit:
 *   "CONCURRENT_SEGMENT_COUNT constant (segment-count + segment-path derivation)".
 *
 * These assert directly against the REAL extracted code in [AppUpdateLogic]
 * (never a re-implementation): the byte-identical-move constant and the
 * faithful capture of the inlined "<partial>.seg<i>" string template that the
 * adapter previously duplicated across 6 sites.
 */
class AppUpdateLogicSegmentTest {

  /**
   * Pins the must-equal-core-default invariant.
   *
   * COUPLING NOTE: [AppUpdateLogic.CONCURRENT_SEGMENT_COUNT] must equal the
   * shared `ConcurrentRangeDownloader`'s default `segmentCount = 8`. That core
   * default is a PRIVATE constructor parameter default, not a public constant,
   * so this test cannot reference it directly — it can only pin the documented
   * magic number 8. If the core default ever changes, this hard-coded 8 is the
   * tripwire that forces a human to re-sync both sides (otherwise the Phase-2
   * scan would miss in-flight segment files written past index 7, or scan a
   * dead range).
   */
  @Test
  fun concurrentSegmentCountEqualsCoreDefaultEight() {
    assertEquals(8, AppUpdateLogic.CONCURRENT_SEGMENT_COUNT)
  }

  /** segmentFileName is the verbatim "<partial>.seg<index>" template. */
  @Test
  fun segmentFileNameAppendsDotSegIndex() {
    assertEquals(
      "x.apk.partial.seg0",
      AppUpdateLogic.segmentFileName("x.apk.partial", 0),
    )
    assertEquals(
      "x.apk.partial.seg7",
      AppUpdateLogic.segmentFileName("x.apk.partial", 7),
    )
    // The helper is a dumb string template: it does not validate the index, so
    // any integer flows straight through (mirrors the inlined call sites).
    assertEquals(
      "x.apk.partial.seg42",
      AppUpdateLogic.segmentFileName("x.apk.partial", 42),
    )
  }

  /** segmentFileNames returns exactly CONCURRENT_SEGMENT_COUNT names, seg0..seg7. */
  @Test
  fun segmentFileNamesYieldsEightOrderedNamesSeg0ToSeg7() {
    val names = AppUpdateLogic.segmentFileNames("x.apk.partial")

    assertEquals(AppUpdateLogic.CONCURRENT_SEGMENT_COUNT, names.size)
    assertEquals(8, names.size)
    assertEquals("x.apk.partial.seg0", names.first())
    assertEquals("x.apk.partial.seg7", names.last())
  }

  /**
   * The generated names are distinct and strictly ordered seg0..seg${N-1},
   * each equal to the single-name helper for the same index. This guards the
   * Phase-2 scan range: a duplicate or out-of-order name would make the
   * in-flight-download detection scan the wrong sibling files.
   */
  @Test
  fun segmentFileNamesAreDistinctAndOrderedAndMatchSingleHelper() {
    val partial = "/data/user/0/app/files/update.apk.partial"
    val names = AppUpdateLogic.segmentFileNames(partial)

    // distinct
    assertEquals(names.size, names.toSet().size)

    // ordered seg0..seg${N-1}, each consistent with segmentFileName(index)
    for (i in 0 until AppUpdateLogic.CONCURRENT_SEGMENT_COUNT) {
      assertEquals(AppUpdateLogic.segmentFileName(partial, i), names[i])
      assertTrue(
        "name at $i must end with .seg$i",
        names[i].endsWith(".seg$i"),
      )
    }
  }
}
