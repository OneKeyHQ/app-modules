package com.margelo.nitro.reactnativeappupdate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-JVM coverage for the "Content-Range / 416 header parsing" unit extracted
 * out of ReactNativeAppUpdate.downloadAPK into [AppUpdateLogic].
 *
 * These exercise the REAL extracted code (regex literals copied character-for-
 * character from the adapter — the 416 `bytes * /<total>` branch and the 206
 * `bytes start-end/total` sanity check), never a re-implementation. The adapter
 * keeps all File I/O / response.close / throw; only the pure string-parse and
 * the two comparison predicates moved here, so this guards the parse step is
 * byte-identical and the CDN-misalignment / 416-complete guards stay total.
 */
class AppUpdateContentRangeTest {

  // ---------------------------------------------------------------------------
  // parse416Total — `Content-Range: bytes * /<total>` (the 416 branch, line 742)
  // ---------------------------------------------------------------------------

  @Test
  fun parse416Total_extractsTotalFromUnsatisfiableRange() {
    assertEquals(12345L, AppUpdateLogic.parse416Total("bytes */12345"))
  }

  @Test
  fun parse416Total_isTolerantOfWhitespaceAroundStarAndSlash() {
    // The verbatim regex is `bytes\s+\*\s*/\s*(\d+)`, so spaces around `*` and
    // `/` are all allowed.
    assertEquals(99L, AppUpdateLogic.parse416Total("bytes * / 99"))
    assertEquals(99L, AppUpdateLogic.parse416Total("bytes  *  /99"))
  }

  @Test
  fun parse416Total_rejectsNormalContentRange() {
    // A satisfiable `bytes 0-1/2` is NOT the unsatisfiable form → no total.
    assertNull(AppUpdateLogic.parse416Total("bytes 0-1/2"))
    assertNull(AppUpdateLogic.parse416Total("bytes 100-199/500"))
  }

  @Test
  fun parse416Total_nullAndGarbageYieldNull() {
    assertNull(AppUpdateLogic.parse416Total(null))
    assertNull(AppUpdateLogic.parse416Total(""))
    assertNull(AppUpdateLogic.parse416Total("garbage"))
    assertNull(AppUpdateLogic.parse416Total("bytes */")) // no digits after slash
  }

  // ---------------------------------------------------------------------------
  // parse206ContentRange — `bytes start-end/total` (the 206 sanity check, l.808)
  // ---------------------------------------------------------------------------

  @Test
  fun parse206ContentRange_parsesStartEndTotal() {
    assertEquals(
      Triple(100L, 199L, 500L),
      AppUpdateLogic.parse206ContentRange("bytes 100-199/500"),
    )
  }

  @Test
  fun parse206ContentRange_starTotalYieldsNullTotal() {
    // `total` is `*` (server doesn't know the full length) → total parses null,
    // but start/end are still recovered.
    assertEquals(
      Triple(0L, 99L, null),
      AppUpdateLogic.parse206ContentRange("bytes 0-99/*"),
    )
  }

  @Test
  fun parse206ContentRange_isTolerantOfWhitespace() {
    // Verbatim regex `bytes\s+(\d+)\s*-\s*(\d+)\s*/\s*(\d+|\*)`.
    assertEquals(
      Triple(100L, 199L, 500L),
      AppUpdateLogic.parse206ContentRange("bytes  100 - 199 / 500"),
    )
  }

  @Test
  fun parse206ContentRange_garbageAndNullYieldNull() {
    assertNull(AppUpdateLogic.parse206ContentRange("garbage"))
    assertNull(AppUpdateLogic.parse206ContentRange(null))
    assertNull(AppUpdateLogic.parse206ContentRange(""))
    // The 416 unsatisfiable form is not a valid 206 range → no match.
    assertNull(AppUpdateLogic.parse206ContentRange("bytes */12345"))
  }

  // ---------------------------------------------------------------------------
  // is416Complete — the 416-recovery predicate (adapter comparison, line 743)
  // ---------------------------------------------------------------------------

  @Test
  fun is416Complete_trueOnlyWhenTotalEqualsPartial() {
    // Server says the whole APK is exactly what we already have → recover, don't wipe.
    assertTrue(AppUpdateLogic.is416Complete(99L, 99L))
    // Sizes differ → partial is stale/corrupt → not complete.
    assertFalse(AppUpdateLogic.is416Complete(99L, 50L))
    assertFalse(AppUpdateLogic.is416Complete(50L, 99L))
    // No parseable total → cannot claim completeness.
    assertFalse(AppUpdateLogic.is416Complete(null, 0L))
    assertFalse(AppUpdateLogic.is416Complete(null, 99L))
  }

  // ---------------------------------------------------------------------------
  // is206StartAligned — the CDN-misalignment guard (adapter check, lines 814)
  // ---------------------------------------------------------------------------

  @Test
  fun is206StartAligned_trueOnlyWhenStartEqualsPartial() {
    // 206 body starts exactly where we asked to resume → safe to append.
    assertTrue(AppUpdateLogic.is206StartAligned(100L, 100L))
    // Start != requested offset (CDN/proxy rewrote the range) → mis-aligned slice.
    assertFalse(AppUpdateLogic.is206StartAligned(100L, 50L))
    assertFalse(AppUpdateLogic.is206StartAligned(50L, 100L))
    // Missing/unparseable start → demote to full restart, never append.
    assertFalse(AppUpdateLogic.is206StartAligned(null, 0L))
    assertFalse(AppUpdateLogic.is206StartAligned(null, 100L))
  }

  // ---------------------------------------------------------------------------
  // End-to-end: parse → predicate, mirroring how the adapter chains them.
  // ---------------------------------------------------------------------------

  @Test
  fun parse416ThenIs416Complete_matchesAdapterRecoveryDecision() {
    // (a) total == partialBytes → recover.
    val total = AppUpdateLogic.parse416Total("bytes */2048")
    assertTrue(AppUpdateLogic.is416Complete(total, 2048L))
    // (b) anything else → wipe.
    assertFalse(AppUpdateLogic.is416Complete(total, 1024L))
  }

  @Test
  fun parse206ThenIs206StartAligned_matchesAdapterStartCheck() {
    val (start, _, _) = AppUpdateLogic.parse206ContentRange("bytes 4096-8191/8192")!!
    assertTrue(AppUpdateLogic.is206StartAligned(start, 4096L))
    assertFalse(AppUpdateLogic.is206StartAligned(start, 0L))
  }

  // ---------------------------------------------------------------------------
  // Sibling-segment invariant: segmentCount == 8 (the concurrent downloader's
  // default that the 416/206 cleanup loops scan). Asserted over the REAL const.
  // ---------------------------------------------------------------------------

  @Test
  fun concurrentSegmentCountIsEight() {
    assertEquals(8, AppUpdateLogic.CONCURRENT_SEGMENT_COUNT)
    assertEquals(8, AppUpdateLogic.segmentFileNames("/tmp/app.apk.partial").size)
    assertEquals(
      "/tmp/app.apk.partial.seg0",
      AppUpdateLogic.segmentFileNames("/tmp/app.apk.partial").first(),
    )
    assertEquals(
      "/tmp/app.apk.partial.seg7",
      AppUpdateLogic.segmentFileNames("/tmp/app.apk.partial").last(),
    )
  }
}
