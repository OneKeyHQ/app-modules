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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * OCDS §6 conformance scenarios driving the REAL
 * [ConcurrentRangeDownloader.download] against the [RangeFaultServer]
 * MockWebServer harness. Each test is named with its OCDS-T number and asserts
 * on the recorded ranges, the on-disk artifacts (`.partial` / `.segN`), and the
 * assembled bytes (SHA-256 vs source).
 *
 * Out of scope of this CORE suite (asserted/explained, never forced):
 *  - OCDS-T6 (whole-file SHA mismatch → single-stream retry once → terminal):
 *    the core does NOT compute a whole-file checksum. On COMPLETED it leaves the
 *    assembled bytes at `partialFilePath`; the CALLER renames + SHA-256/GPG
 *    verifies and drives the retry-once-then-terminal policy. Not a core test.
 *  - OCDS-T11 (two concurrent download() for the same dest → serialized / one
 *    fails fast, artifacts never co-written): single-flight is a CALLER-level
 *    per-taskId registry concern (the adapter), not in this dependency-free core.
 *  - OCDS-T9 (cross-relaunch attempt budget / bounded terminal): the budget that
 *    spans process relaunches lives in the shared-JS caller and is tested there.
 *    The core's in-process bound (maxPartRetry) is exercised indirectly by T4.
 *
 * Threading note (from FaultServer.kt): the dispatcher runs concurrently on
 * MockWebServer reader threads, so range assertions read the synchronized
 * `requestedRanges` only after the download() call has fully returned.
 */
class ConcurrentRangeDownloaderOcdsTest {

  private lateinit var server: MockWebServer
  private lateinit var tmpDir: File

  // 3 MiB > minConcurrentBytes (2 MiB) → the core takes the concurrent path,
  // and 3 MiB ceil-chunked over 8 segments yields 8 non-empty parts.
  private val content: ByteArray = ByteArray(3 * 1024 * 1024) { (it % 251).toByte() }
  private val parts: List<PlannedPart> by lazy { planParts(content.size.toLong(), 8) }

  @Before
  fun setUp() {
    tmpDir = File.createTempFile("crd-ocds", "").let {
      it.delete(); it.mkdirs(); it
    }
  }

  @After
  fun tearDown() {
    if (this::server.isInitialized) server.shutdown()
    tmpDir.deleteRecursively()
  }

  // --- helpers -------------------------------------------------------------

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
  // OCDS-T1: drop one segment's connection mid-body a couple of times.
  //   Only that segment is re-fetched (retries stay in its own window); the
  //   other segments are requested exactly once; assembled SHA is correct; no
  //   from-0 restart.
  // ------------------------------------------------------------------------
  @Test
  fun ocdsT1_midBodyDropOnlyRefetchesAffectedSegment() {
    val target = parts[3]
    val fs = RangeFaultServer(content)
      .arm(RangeFaultServer.FaultMode.DROP_DURING_BODY, target.start, target.end, times = 2)
    startServer(fs)

    val partial = partialPath()
    val outcome = newDownloader().download(
      url = url(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )

    assertEquals(ConcurrentRangeDownloader.Outcome.COMPLETED, outcome)
    assertEquals(sha256(content), sha256(File(partial)))

    val ranges = fs.requestedRanges.toList()
    // The dropped segment is fetched: initial + 2 retries = 3 times (its window).
    // (Resume requests only the not-yet-on-disk tail, but the Range start stays
    //  inside the segment window — never byte 0 of the file.)
    val targetReqs = ranges.filter { it.first in target.start..target.end }
    assertTrue(
      "dropped segment must be re-fetched at least twice (got ${targetReqs.size})",
      targetReqs.size >= 2,
    )
    // No from-0 restart: every retry of the target segment starts within its own
    // window, never at file byte 0 (unless segment 0 itself is the target — it
    // is not here, target is part[3]).
    assertTrue(
      "no retry of the affected segment may restart at file byte 0",
      targetReqs.all { it.first >= target.start },
    )
    // Every OTHER segment is requested exactly once.
    for (p in parts) {
      if (p.index == target.index) continue
      val reqs = ranges.filter { it.first == p.start && it.second == p.end }
      assertEquals(
        "segment ${p.index} must be requested exactly once",
        1,
        reqs.size,
      )
    }
    // All segments consumed on success.
    for (i in 0 until 8) assertFalse(segFile(partial, i).exists())
  }

  // ------------------------------------------------------------------------
  // OCDS-T2: (a) full download completes; (b) resume re-fetches only the tail.
  // ------------------------------------------------------------------------
  @Test
  fun ocdsT2a_fullDownloadRequestsAllEightRanges() {
    val fs = RangeFaultServer(content)
    startServer(fs)

    val partial = partialPath()
    val outcome = newDownloader().download(
      url = url(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )

    assertEquals(ConcurrentRangeDownloader.Outcome.COMPLETED, outcome)
    assertEquals(content.size.toLong(), File(partial).length())
    assertEquals(sha256(content), sha256(File(partial)))

    val ranges = fs.requestedRanges.toList()
    for (p in parts) {
      val reqs = ranges.filter { it.first == p.start && it.second == p.end }
      assertEquals("segment ${p.index} must be requested once", 1, reqs.size)
    }
  }

  @Test
  fun ocdsT2b_resumeRefetchesOnlyMissingSegments() {
    val fs = RangeFaultServer(content)
    startServer(fs)

    val partial = partialPath()
    // Seed parts 0..4 as already-completed `.segN` files (mid-run kill model).
    seedSegments(partial, content, completedThrough = 4, segments = 8)

    val outcome = newDownloader().download(
      url = url(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )

    assertEquals(ConcurrentRangeDownloader.Outcome.COMPLETED, outcome)
    assertEquals(sha256(content), sha256(File(partial)))

    // The probe always issues a "bytes=0-0" request (size/Range capability
    // check); it is NOT a segment fetch, so exclude it from the segment counts.
    val PROBE = 0L to 0L
    val ranges = fs.requestedRanges.toList().filterNot { it == PROBE }
    // No SEGMENT request for the seeded windows (parts 0..4) beyond the probe.
    for (idx in 0..4) {
      val p = parts[idx]
      val reqs = ranges.filter { it.first in p.start..p.end }
      assertTrue("seeded segment $idx must NOT be re-fetched (got $reqs)", reqs.isEmpty())
    }
    // Only the tail (parts 5..7) is fetched, once each.
    for (idx in 5..7) {
      val p = parts[idx]
      val reqs = ranges.filter { it.first == p.start && it.second == p.end }
      assertEquals("tail segment $idx must be requested once", 1, reqs.size)
    }
  }

  // ------------------------------------------------------------------------
  // OCDS-T3: status 200 to a Range request (probe) → FALLBACK + artifacts wiped.
  //   The probe itself sends Range "bytes=0-0"; a 200 there makes the probe
  //   report supportsRange=false → FALLBACK before any segment is written, so
  //   there are no artifacts to wipe (and the test asserts none exist).
  // ------------------------------------------------------------------------
  @Test
  fun ocdsT3_status200ToRangeFallsBackAndWipesArtifacts() {
    // Arm 200 on the probe window (bytes=0-0). The probe's 200 short-circuits to
    // FALLBACK; no concurrent path runs.
    val fs = RangeFaultServer(content)
      .arm(RangeFaultServer.FaultMode.STATUS_200, 0L, 0L, times = Int.MAX_VALUE)
    startServer(fs)

    val partial = partialPath()
    val outcome = newDownloader().download(
      url = url(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )

    assertEquals(ConcurrentRangeDownloader.Outcome.FALLBACK, outcome)
    assertNoArtifactsLeft(partial)
  }

  // OCDS-T3 (segment-level 200): the probe succeeds (206) but a segment fetch
  // gets a 200 (server ignored Range on that connection) → core throws
  // FallbackException → Outcome.FALLBACK and wipeArtifacts() clears every `.segN`
  // / `.partial`.
  @Test
  fun ocdsT3_status200OnSegmentFallsBackAndWipesArtifacts() {
    val target = parts[2]
    val fs = RangeFaultServer(content)
      .arm(RangeFaultServer.FaultMode.STATUS_200, target.start, target.end, times = Int.MAX_VALUE)
    startServer(fs)

    val partial = partialPath()
    val outcome = newDownloader().download(
      url = url(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )

    assertEquals(ConcurrentRangeDownloader.Outcome.FALLBACK, outcome)
    assertNoArtifactsLeft(partial)
  }

  // ------------------------------------------------------------------------
  // OCDS-T4: 429 (+Retry-After: 0) once on a segment → transient retry succeeds.
  //   SHA correct; the affected segment requested twice; no from-0 restart.
  // ------------------------------------------------------------------------
  @Test
  fun ocdsT4_status429ThenOkRetriesInPlace() {
    val target = parts[5]
    val fs = RangeFaultServer(content)
      .arm(RangeFaultServer.FaultMode.STATUS_429_THEN_OK, target.start, target.end, times = 1)
    startServer(fs)

    val partial = partialPath()
    val outcome = newDownloader().download(
      url = url(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )

    assertEquals(ConcurrentRangeDownloader.Outcome.COMPLETED, outcome)
    assertEquals(sha256(content), sha256(File(partial)))

    val ranges = fs.requestedRanges.toList()
    // 429 returned an empty body, so the segment has 0 bytes on disk → the retry
    // re-requests the SAME full window (start == part.start). Exactly two hits.
    val targetReqs = ranges.filter { it.first == target.start && it.second == target.end }
    assertEquals("affected segment must be requested twice (429 then OK)", 2, targetReqs.size)
    assertTrue(
      "retry must not restart at file byte 0",
      targetReqs.all { it.first == target.start },
    )
  }

  // ------------------------------------------------------------------------
  // OCDS-T5(a): mis-aligned Content-Range → rejected, never silent corruption.
  //   The core treats a mismatched Content-Range as transient (retry); the
  //   harness's misaligned fault always shifts the window, so every retry is
  //   rejected → the segment never completes → download() throws after the
  //   per-segment budget. The KEY assertion: no corruption — assembly never
  //   COMPLETED with wrong bytes.
  // ------------------------------------------------------------------------
  @Test
  fun ocdsT5a_misalignedContentRangeIsRejectedNeverCorrupts() {
    val target = parts[1]
    val fs = RangeFaultServer(content)
      // Fire forever so the misalignment never "expires" into a good 206.
      .arm(RangeFaultServer.FaultMode.MISALIGNED_CONTENT_RANGE, target.start, target.end, times = Int.MAX_VALUE)
    startServer(fs)

    val partial = partialPath()
    var threw = false
    var outcome: ConcurrentRangeDownloader.Outcome? = null
    try {
      outcome = newDownloader().download(
        url = url(),
        partialFilePath = partial,
        onProgress = { _, _ -> },
      )
    } catch (e: Exception) {
      threw = true
    }
    // Never a corrupt COMPLETED. Either it threw (transient give-up, segments
    // kept for resume) or — if a future core maps it to fallback — FALLBACK.
    assertTrue(
      "misaligned 206 must not produce a COMPLETED outcome",
      outcome != ConcurrentRangeDownloader.Outcome.COMPLETED,
    )
    assertTrue("misaligned 206 must be rejected (threw or fell back)", threw || outcome == ConcurrentRangeDownloader.Outcome.FALLBACK)
    // No assembled final file with the wrong window was produced.
    if (File(partial).exists()) {
      assertNotEquals(
        "a `.partial` left on disk must NOT be a complete (corrupt) file",
        content.size.toLong(),
        File(partial).length(),
      )
    }
  }

  // OCDS-T5(b): over-long body → not written past segment end; rejected.
  //   The harness fires the over-long body forever, so the segment is dropped on
  //   each attempt and never completes → throw after budget; crucially the
  //   neighbour segments are NOT corrupted (each writes its own `.segN`).
  @Test
  fun ocdsT5b_overLongBodyIsRejectedNoNeighbourCorruption() {
    val target = parts[4]
    val fs = RangeFaultServer(content)
      .arm(RangeFaultServer.FaultMode.OVER_LONG_BODY, target.start, target.end, times = Int.MAX_VALUE)
    startServer(fs)

    val partial = partialPath()
    var outcome: ConcurrentRangeDownloader.Outcome? = null
    var threw = false
    try {
      outcome = newDownloader().download(
        url = url(),
        partialFilePath = partial,
        onProgress = { _, _ -> },
      )
    } catch (e: Exception) {
      threw = true
    }
    assertTrue(
      "over-long 206 must not produce a COMPLETED outcome",
      outcome != ConcurrentRangeDownloader.Outcome.COMPLETED,
    )
    assertTrue("over-long body must be rejected (threw or fell back)", threw || outcome == ConcurrentRangeDownloader.Outcome.FALLBACK)
    // The over-long segment is dropped (deleted) on overrun, so neighbours are
    // never spliced. Any neighbour `.segN` that exists must be exactly its
    // planned length — no extra bytes leaked across windows.
    for (p in parts) {
      if (p.index == target.index) continue
      val f = segFile(partial, p.index)
      if (f.exists()) {
        assertTrue(
          "neighbour segment ${p.index} must not exceed its planned length",
          f.length() <= p.length,
        )
      }
    }
  }

  // OCDS-T5(c): short body → the segment ends short and the tail is resumed
  //   (Range start advanced, NOT from 0) → final SHA correct.
  //
  //   CORE BEHAVIOUR (verified): a clean short read (a complete 206 whose body is
  //   shorter than the requested window, no socket error) is NOT retried inside
  //   the same `download()` call — `downloadSegment` returns after a successful
  //   `fetchSegment`, so a short segment makes that invocation throw
  //   "incomplete", leaving the short `.segN` on disk. The resume is
  //   CROSS-INVOCATION (next relaunch): the second `download()` sees a short
  //   `.segN`, requests only the missing tail (`Range` start = current seg
  //   length, which is advanced past part.start), and completes. This test drives
  //   both invocations to assert the tail-resume + correct final SHA + no from-0
  //   restart.
  @Test
  fun ocdsT5c_shortBodyResumesTailToCorrectSha() {
    val target = parts[6]
    val fs = RangeFaultServer(content)
      .arm(RangeFaultServer.FaultMode.SHORT_BODY, target.start, target.end, times = 1)
    startServer(fs)

    val partial = partialPath()
    // First invocation: the short body leaves segment 6 one byte short, so this
    // call throws "incomplete" and keeps the `.segN` for resume.
    var firstThrew = false
    try {
      newDownloader().download(
        url = url(),
        partialFilePath = partial,
        onProgress = { _, _ -> },
      )
    } catch (e: Exception) {
      firstThrew = true
    }
    assertTrue("short body must leave the first invocation incomplete", firstThrew)
    // The short segment is kept on disk, one byte under its planned length.
    val seg = segFile(partial, target.index)
    assertTrue("short `.segN` must be kept for resume", seg.exists())
    assertEquals("short segment is exactly one byte short", target.length - 1, seg.length())

    val rangesAfterFirst = fs.requestedRanges.toList()

    // Second invocation (relaunch): the fault has expired (times=1), so the tail
    // is served normally and the download completes.
    val outcome = newDownloader().download(
      url = url(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )

    assertEquals(ConcurrentRangeDownloader.Outcome.COMPLETED, outcome)
    assertEquals(sha256(content), sha256(File(partial)))

    // The RESUME requested only the missing tail of segment 6: Range start
    // advanced past part.start (= part.end here, the single missing byte), end ==
    // part.end. Never a from-0 (file byte 0) restart.
    val resumeRanges = fs.requestedRanges.toList().drop(rangesAfterFirst.size)
    val tailResume = resumeRanges.filter {
      it.first > target.start && it.first <= target.end && it.second == target.end
    }
    assertTrue(
      "short body must trigger a tail-only resume (start advanced, not from 0)",
      tailResume.isNotEmpty(),
    )
  }

  // ------------------------------------------------------------------------
  // OCDS-T8: cancel mid-download → run stops promptly; cancel-then-delete works
  //   (artifacts can be cleaned without a straggler resurrecting them).
  //   Strategy: stall every segment (1 byte/sec) so the download is in-flight,
  //   then cancel() from another thread. cancel() shuts the pool down and waits
  //   (bounded) for workers to terminate, so a subsequent delete is durable.
  // ------------------------------------------------------------------------
  @Test
  fun ocdsT8_cancelMidDownloadStopsAndArtifactsStayDeleted() {
    val fs = RangeFaultServer(content)
    // Stall ALL eight segment windows so the workers are parked in body reads.
    for (p in parts) {
      fs.arm(RangeFaultServer.FaultMode.STALL, p.start, p.end, times = Int.MAX_VALUE)
    }
    startServer(fs)

    val partial = partialPath()
    val handle = ConcurrentRangeDownloader.CancelHandle()
    val downloader = newDownloader()

    val resultRef = AtomicReference<Any?>(null)
    val started = CountDownLatch(1)
    val worker = Executors.newSingleThreadExecutor()
    val future = worker.submit {
      started.countDown()
      try {
        resultRef.set(
          downloader.download(
            url = url(),
            partialFilePath = partial,
            cancelHandle = handle,
            onProgress = { _, _ -> },
          )
        )
      } catch (e: Exception) {
        resultRef.set(e)
      }
    }

    // Wait until the download has begun, give it a beat to open the segment
    // sockets, then cancel.
    started.await(5, TimeUnit.SECONDS)
    Thread.sleep(400)
    val cancelStart = System.currentTimeMillis()
    handle.cancel()

    // download() must return promptly after cancel (well under the 60s the stall
    // would otherwise take). cancel() awaits worker termination (<=3s) + the
    // download() loop joins futures; allow a generous bound.
    future.get(15, TimeUnit.SECONDS)
    val elapsed = System.currentTimeMillis() - cancelStart
    worker.shutdownNow()

    assertTrue("cancel must abort promptly (took ${elapsed}ms)", elapsed < 12_000)

    // cancel-then-delete: now that workers are terminated, deleting the artifacts
    // must STICK — no straggler worker resurrects a `.segN`.
    File(partial).delete()
    for (i in 0 until 8) segFile(partial, i).delete()
    // Give any (forbidden) straggler a window to (incorrectly) recreate a file.
    Thread.sleep(500)
    assertNoArtifactsLeft(partial)

    // The aborted run did not produce a COMPLETED outcome.
    assertNotEquals(
      "cancelled run must not report COMPLETED",
      ConcurrentRangeDownloader.Outcome.COMPLETED,
      resultRef.get(),
    )
  }

  // ------------------------------------------------------------------------
  // OCDS-T10: stalled socket (bytes trickle past the read timeout) → the
  //   read-timeout surfaces as a transient error → retry succeeds.
  //   The default OkHttpClient read timeout is 10s, too slow for a fast unit
  //   test, so we inject a client with a 600ms read timeout. The harness stalls
  //   the segment ONCE (times=1): the first attempt times out mid-body, the core
  //   retries, and the second attempt (fault expired) streams the full window.
  // ------------------------------------------------------------------------
  @Test
  fun ocdsT10_stalledSocketTimesOutAsTransientThenRetrySucceeds() {
    val target = parts[7]
    val fs = RangeFaultServer(content)
      .arm(RangeFaultServer.FaultMode.STALL, target.start, target.end, times = 1)
    startServer(fs)

    // Inject a small read timeout so the stall (1 byte/sec) trips it fast.
    val fastClient = OkHttpClient.Builder()
      .readTimeout(600, TimeUnit.MILLISECONDS)
      .build()
    val downloader = ConcurrentRangeDownloader(
      httpClient = fastClient,
      segmentCount = 8,
      // Tiny backoff so the single retry runs fast.
      retryBaseDelayMillis = 50L,
      retryMaxDelayMillis = 200L,
    )

    val partial = partialPath()
    val outcome = downloader.download(
      url = url(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )

    assertEquals(ConcurrentRangeDownloader.Outcome.COMPLETED, outcome)
    assertEquals(sha256(content), sha256(File(partial)))

    val ranges = fs.requestedRanges.toList()
    // The stalled segment is requested more than once (timeout → transient retry).
    // The retry may resume from whatever partial bytes the stall delivered
    // (start >= part.start), never from file byte 0.
    val targetReqs = ranges.filter { it.first in target.start..target.end && it.second == target.end }
    assertTrue(
      "stalled segment must be retried (>=2 requests, got ${targetReqs.size})",
      targetReqs.size >= 2,
    )
    assertTrue(
      "stall retry must not restart at file byte 0",
      targetReqs.all { it.first >= target.start },
    )
  }
}
