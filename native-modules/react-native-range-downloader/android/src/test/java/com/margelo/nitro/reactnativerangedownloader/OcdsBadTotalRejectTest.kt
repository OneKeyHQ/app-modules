package com.margelo.nitro.reactnativerangedownloader

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import okhttp3.mockwebserver.MockWebServer
import java.io.File

/**
 * OCDS gap A4 (SPEC #5 — OCDS §5): a 206 whose `Content-Range` total (the
 * `/<total>` tail) DISAGREES with the probe total → PERMANENT FALLBACK
 * (single-stream), never silent corruption. This is the "object changed size
 * behind us" case: the byte window we asked for is served correctly, but the
 * server now advertises a different whole-object size, so concurrency is
 * unreconcilable and the core must bail to single-stream rather than splice in
 * bytes from a different build.
 *
 * Drives the REAL [ConcurrentRangeDownloader.download] against the
 * [RangeFaultServer] MockWebServer harness, using the same conventions as
 * [ConcurrentRangeDownloaderOcdsTest] (newDownloader(), planParts, arm(...),
 * sha256(File), assertNoArtifactsLeft).
 *
 * Core path under test (ConcurrentRangeDownloader.kt):
 *  - parseContentRangeBounds (542-548) returns Triple(start, end, total+1);
 *  - the bounds check at 494 PASSES (start/end are correct);
 *  - the total check at 506 (`parsedTotal != total`) FIRES → FallbackException;
 *  - the worker sets fallback=true (234-237), download() wipeArtifacts() (252)
 *    and returns Outcome.FALLBACK (253).
 *
 * The harness BAD_TOTAL_206 fault (FaultServer.kt:155-164) is the only mode that
 * produces this: it keeps the correct start/end bounds and the correct body, but
 * advertises `/<total+1>` in the `/<total>` tail — so it exercises the total
 * branch in isolation (the bounds branch never trips).
 */
class OcdsBadTotalRejectTest {

  private lateinit var server: MockWebServer
  private lateinit var tmpDir: File

  // 3 MiB > minConcurrentBytes (2 MiB) → the core takes the concurrent path,
  // and 3 MiB ceil-chunked over 8 segments yields 8 non-empty parts.
  private val content: ByteArray = ByteArray(3 * 1024 * 1024) { (it % 251).toByte() }
  private val parts: List<PlannedPart> by lazy { planParts(content.size.toLong(), 8) }

  @Before
  fun setUp() {
    tmpDir = File.createTempFile("crd-ocds-a4", "").let {
      it.delete(); it.mkdirs(); it
    }
  }

  @After
  fun tearDown() {
    if (this::server.isInitialized) server.shutdown()
    tmpDir.deleteRecursively()
  }

  // --- helpers (mirror ConcurrentRangeDownloaderOcdsTest) ------------------

  private fun startServer(faultServer: RangeFaultServer) {
    server = MockWebServer()
    server.dispatcher = faultServer.dispatcher
    server.start()
  }

  private fun url() = server.url("/asset.bin").toString()

  private fun partialPath() = File(tmpDir, "asset.bin.partial").absolutePath

  private fun segFile(partial: String, index: Int) = File("$partial.seg$index")

  private fun assertNoArtifactsLeft(partial: String) {
    assertFalse(".partial must be wiped", File(partial).exists())
    for (i in 0 until 8) {
      assertFalse(".seg$i must be wiped", segFile(partial, i).exists())
    }
  }

  // ------------------------------------------------------------------------
  // A4: a single segment's 206 advertises a Content-Range total of (total+1).
  //   The probe window (bytes=0-0) is unarmed, so the probe still reports the
  //   true total and supportsRange=true → the concurrent path runs. When the
  //   armed segment returns its (correct-window, correct-body) 206 with the
  //   wrong `/<total>` tail, the core's total check fires FallbackException →
  //   Outcome.FALLBACK and every artifact is wiped. No corrupt assembly.
  // ------------------------------------------------------------------------
  @Test
  fun ocdsA4_badContentRangeTotalFallsBackAndWipesArtifacts() {
    val target = parts[3]
    val fs = RangeFaultServer(content)
      // Fire forever so the bad total never "expires" into a good 206 — the
      // window must be permanently unusable for concurrency.
      .arm(RangeFaultServer.FaultMode.BAD_TOTAL_206, target.start, target.end, times = Int.MAX_VALUE)
    startServer(fs)

    val partial = partialPath()
    val outcome = newDownloader().download(
      url = url(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )

    // Load-bearing: a disagreeing concrete total is permanent for this window,
    // so the core must signal single-stream fallback (NOT COMPLETED, NOT a
    // transient throw). This assertion fails if the line-506 total check is
    // removed/weakened or mis-classified as transient.
    assertEquals(
      "Content-Range total mismatch must yield FALLBACK",
      ConcurrentRangeDownloader.Outcome.FALLBACK,
      outcome,
    )
    assertNotEquals(
      "bad-total 206 must NOT complete the concurrent download",
      ConcurrentRangeDownloader.Outcome.COMPLETED,
      outcome,
    )

    // Load-bearing: no corruption — the fallback path wipes every `.partial` /
    // `.segN`, so the caller's single-stream retry starts from a clean slate and
    // no half-written segment from a differently-sized object survives.
    assertNoArtifactsLeft(partial)

    // Defensive: even if a `.partial` were (incorrectly) left behind, it must
    // never be a full-length (i.e. silently-assembled / corrupt) file.
    if (File(partial).exists()) {
      assertNotEquals(
        "a leftover `.partial` must NOT be a complete (corrupt) file",
        content.size.toLong(),
        File(partial).length(),
      )
    }

    // Load-bearing: the offending window WAS actually fetched (so the fallback
    // is provably driven by the bad-total 206, not by an earlier probe bail).
    // The probe issues bytes=0-0; the target window 3 is a real segment fetch.
    val ranges = fs.requestedRanges.toList()
    val targetReqs = ranges.filter { it.first == target.start && it.second == target.end }
    assertTrue(
      "the bad-total segment window must have been requested (got ${targetReqs.size})",
      targetReqs.isNotEmpty(),
    )
  }
}
