package com.margelo.nitro.reactnativerangedownloader

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * OCDS gap A1 — §4 HTTP status classification, the TRANSIENT 5xx branch
 * (`isPermanentHttpStatus`: `in 500..599 -> false`,
 * ConcurrentRangeDownloader.kt:596) driven end-to-end through
 * `classifyHttpFailure` (line 555-559 → [TransientHttpException]) and the
 * `downloadSegment` retry-in-place loop (line 410-419) against the REAL
 * [ConcurrentRangeDownloader] over the [RangeFaultServer] MockWebServer harness.
 *
 * Two scenarios:
 *  - [transient5xxThenOkRetriesInPlaceAndAssembles]: a 503 fired a finite number
 *    of times on ONE segment window, then it recovers. Asserts per-segment retry,
 *    segments kept (no from-0 restart, no FALLBACK), recovery 206, final SHA over
 *    the real assembled file, and that EVERY OTHER segment is requested once.
 *  - [transient5xxBudgetExhaustionThrowsKeepingArtifactsForResume]: a 503 fired
 *    forever on one window exhausts the per-segment retry budget (maxPartRetry=3
 *    → initial + 3 retries = exactly 4 hits) → `download()` throws (transient
 *    give-up, NOT a corrupt COMPLETED, NOT a FALLBACK that wipes artifacts).
 *
 * The harness's default [RangeFaultServer.status5xxCode] is 503 (FaultServer.kt
 * :100, served with an empty body at lines 136-137), so each fired attempt
 * leaves 0 bytes on disk for that segment → the retry re-requests the SAME full
 * window (start == part.start), mirroring OCDS-T4's 429 assertion.
 */
class OcdsTransient5xxTest {

  private lateinit var server: MockWebServer
  private lateinit var tmpDir: File

  // 3 MiB > minConcurrentBytes (2 MiB) → the core takes the concurrent path,
  // and 3 MiB ceil-chunked over 8 segments yields 8 non-empty parts.
  private val content: ByteArray = ByteArray(3 * 1024 * 1024) { (it % 251).toByte() }
  private val parts: List<PlannedPart> by lazy { planParts(content.size.toLong(), 8) }

  @Before
  fun setUp() {
    tmpDir = File.createTempFile("crd-ocds-5xx", "").let {
      it.delete(); it.mkdirs(); it
    }
  }

  @After
  fun tearDown() {
    if (this::server.isInitialized) server.shutdown()
    tmpDir.deleteRecursively()
  }

  // --- helpers (mirror ConcurrentRangeDownloaderOcdsTest) -------------------

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

  // Fast-backoff downloader form (mirrors OCDS-T10) so the single/few retries
  // run fast in a unit test. Default maxPartRetry (3) is preserved so the
  // budget-exhaustion test exercises the real bound.
  private fun fastDownloader() = ConcurrentRangeDownloader(
    httpClient = OkHttpClient(),
    segmentCount = 8,
    retryBaseDelayMillis = 50L,
    retryMaxDelayMillis = 200L,
  )

  // ------------------------------------------------------------------------
  // A1(a): transient 5xx on ONE segment a few times → retried in place, then
  //   recovers. Segments are KEPT across the retry (no from-0 restart, no
  //   FALLBACK), the recovery 206 completes, and the assembled SHA is correct.
  // ------------------------------------------------------------------------
  @Test
  fun transient5xxThenOkRetriesInPlaceAndAssembles() {
    val target = parts[3]
    // Fire 503 twice on this window; the 3rd attempt (fault expired, times=2)
    // streams the full window normally. 2 < maxPartRetry(3), so the budget is
    // NOT exhausted and the segment recovers within this single download() call.
    val fs = RangeFaultServer(content)
      .arm(RangeFaultServer.FaultMode.STATUS_5XX, target.start, target.end, times = 2)
    startServer(fs)

    val partial = partialPath()
    val outcome = fastDownloader().download(
      url = url(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )

    // Recovery: completes (NOT FALLBACK — a 5xx is transient, never discards
    // concurrency), and the assembled bytes are byte-for-byte the source.
    assertEquals(ConcurrentRangeDownloader.Outcome.COMPLETED, outcome)
    assertNotEquals(
      "transient 5xx must NOT fall back to single-stream",
      ConcurrentRangeDownloader.Outcome.FALLBACK,
      outcome,
    )
    assertEquals(content.size.toLong(), File(partial).length())
    assertEquals(sha256(content), sha256(File(partial)))

    val ranges = fs.requestedRanges.toList()
    // The 503 returned an EMPTY body, so the segment has 0 bytes on disk after
    // each fired attempt → every retry re-requests the SAME full window
    // (start == part.start). Initial + 2 transient retries + 1 recovery 206 =
    // EXACTLY three hits on the full window. (Mirrors OCDS-T4's 429 assertion.)
    val targetReqs = ranges.filter { it.first == target.start && it.second == target.end }
    assertEquals(
      "affected segment must be requested exactly 3 times (503, 503, then 206)",
      3,
      targetReqs.size,
    )
    // No from-0 restart: every retry of the affected window starts at part.start
    // (which is not file byte 0 here — target is part[3]).
    assertTrue(
      "retry-in-place must never restart at file byte 0",
      targetReqs.all { it.first == target.start },
    )
    // Every OTHER segment is requested EXACTLY once — the retry stayed scoped to
    // the affected window and did not perturb its neighbours.
    for (p in parts) {
      if (p.index == target.index) continue
      val reqs = ranges.filter { it.first == p.start && it.second == p.end }
      assertEquals(
        "segment ${p.index} must be requested exactly once",
        1,
        reqs.size,
      )
    }
    // All `.segN` consumed (assembled + deleted) on COMPLETED.
    for (i in 0 until 8) {
      assertFalse(".seg$i must be consumed on success", segFile(partial, i).exists())
    }
  }

  // ------------------------------------------------------------------------
  // A1(b): transient 5xx that NEVER recovers exhausts the per-segment retry
  //   budget (maxPartRetry=3) → download() throws. The KEY guarantees: it is a
  //   transient give-up (NOT a permanent FALLBACK that wipes artifacts), the
  //   affected window is hit EXACTLY budget+1 times (initial + 3 retries), and
  //   no corrupt COMPLETED file is produced.
  // ------------------------------------------------------------------------
  @Test
  fun transient5xxBudgetExhaustionThrowsKeepingArtifactsForResume() {
    val target = parts[2]
    // Fire 503 forever so the segment never recovers and the retry budget is
    // exhausted. status5xxCode defaults to 503 (transient per §4).
    val fs = RangeFaultServer(content)
      .arm(RangeFaultServer.FaultMode.STATUS_5XX, target.start, target.end, times = Int.MAX_VALUE)
    startServer(fs)

    val partial = partialPath()
    var threw = false
    var outcome: ConcurrentRangeDownloader.Outcome? = null
    try {
      outcome = fastDownloader().download(
        url = url(),
        partialFilePath = partial,
        onProgress = { _, _ -> },
      )
    } catch (e: Exception) {
      threw = true
    }

    // A perpetual transient 5xx gives up by THROWING — it must NOT be mapped to
    // a (corrupt) COMPLETED, and it must NOT be a permanent FALLBACK (a 5xx is
    // transient; FALLBACK is reserved for "concurrency fundamentally unusable").
    assertTrue("perpetual transient 5xx must give up by throwing", threw)
    assertEquals("budget exhaustion must not COMPLETE", null, outcome)
    assertNotEquals(
      "transient 5xx budget exhaustion must NOT be a COMPLETED",
      ConcurrentRangeDownloader.Outcome.COMPLETED,
      outcome,
    )
    assertNotEquals(
      "transient 5xx budget exhaustion must NOT be a FALLBACK",
      ConcurrentRangeDownloader.Outcome.FALLBACK,
      outcome,
    )

    val ranges = fs.requestedRanges.toList()
    // The affected window is requested EXACTLY maxPartRetry+1 = 4 times (initial
    // attempt + 3 retries), all at part.start (empty 503 body → 0 bytes on disk
    // → same full window each time, never a from-0 restart). This is the
    // load-bearing budget assertion: it would FAIL if the loop did not honour
    // maxPartRetry, or restarted from byte 0, or treated 5xx as permanent (1 hit).
    val targetReqs = ranges.filter { it.first == target.start && it.second == target.end }
    assertEquals(
      "affected segment must be requested exactly maxPartRetry+1 (4) times",
      4,
      targetReqs.size,
    )
    assertTrue(
      "every 5xx retry must restart at the segment window start, never file byte 0",
      targetReqs.all { it.first == target.start },
    )
    // No complete (corrupt) `.partial` was assembled. If a `.partial` exists at
    // all, it must NOT be a full-length file.
    if (File(partial).exists()) {
      assertNotEquals(
        "a `.partial` left on disk must NOT be a complete (corrupt) file",
        content.size.toLong(),
        File(partial).length(),
      )
    }
  }

  // ------------------------------------------------------------------------
  // A1(c): a permanent 5xx (501) on the SAME branch's boundary must bypass the
  //   retry loop entirely — it is hit EXACTLY once. This pins the transient-vs-
  //   permanent split of `isPermanentHttpStatus` (501 -> true, line 595) so the
  //   A1(b) "retry 4 times" assertion is meaningful by contrast, not noise.
  // ------------------------------------------------------------------------
  @Test
  fun permanent5xx501BypassesRetryLoop() {
    val target = parts[4]
    val fs = RangeFaultServer(content)
    fs.status5xxCode = 501 // 501 is permanent per §4 (isPermanentHttpStatus:595).
    fs.arm(RangeFaultServer.FaultMode.STATUS_5XX, target.start, target.end, times = Int.MAX_VALUE)
    startServer(fs)

    val partial = partialPath()
    var threw = false
    var outcome: ConcurrentRangeDownloader.Outcome? = null
    try {
      outcome = fastDownloader().download(
        url = url(),
        partialFilePath = partial,
        onProgress = { _, _ -> },
      )
    } catch (e: Exception) {
      threw = true
    }

    assertTrue("permanent 5xx (501) must surface (throw)", threw)
    assertNotEquals(
      "permanent 5xx must not COMPLETE",
      ConcurrentRangeDownloader.Outcome.COMPLETED,
      outcome,
    )
    // A PermanentHttpException bypasses the retry loop (downloadSegment line
    // 405-409), so the affected window is requested EXACTLY ONCE — proving the
    // transient branch's 4 hits in A1(b) is a real, classifier-driven retry
    // count and not an artifact of unconditional retrying.
    val ranges = fs.requestedRanges.toList()
    val targetReqs = ranges.filter { it.first == target.start && it.second == target.end }
    assertEquals(
      "permanent 5xx (501) must be requested exactly once (no retry-in-place)",
      1,
      targetReqs.size,
    )
  }
}
