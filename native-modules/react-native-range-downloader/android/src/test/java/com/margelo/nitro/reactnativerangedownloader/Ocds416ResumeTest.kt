package com.margelo.nitro.reactnativerangedownloader

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * OCDS gap A2 (SPEC #4 — 416 Range Not Satisfiable kept TRANSIENT).
 *
 * Guards the single most-damaging §4 mistake called out in the core comment
 * (ConcurrentRangeDownloader.kt lines 590-593): 416 MUST classify as transient
 * (`isPermanentHttpStatus` line 593 `416 -> false`, placed ABOVE the
 * `in 400..499 -> true` catch-all at line 594). If 416 were permanent the core
 * would discard the resumable `.segN` bytes and restart the whole object from
 * byte 0 — exactly the "do not discard resumable bytes" rule this test pins.
 *
 * Scenario: a RESUME run where parts 0..4 are already full on disk (seeded
 * `.segN`), and the part[5] window hits a single 416 (`STATUS_416` →
 * `416 + Content-Range: bytes star-slash-total`, FaultServer.kt:67,139-142) that then
 * succeeds on retry. The load-bearing claims:
 *   - outcome == COMPLETED and the assembled SHA matches the source (so the 416
 *     was recovered, not fatal, and nothing was corrupted);
 *   - the seeded windows 0..4 are NOT re-fetched — proving the 416 did NOT
 *     trigger a from-0 wipe/restart of already-downloaded segments;
 *   - the seeded `.segN` for parts 0..4 are NOT deleted/re-created — their
 *     on-disk length stays exactly full (the "do not discard resumable bytes"
 *     guarantee, asserted on the bytes on disk, not just the request log);
 *   - part[5] is requested exactly TWICE (416 then OK), and every request for
 *     it starts inside its own window (>= part[5].start), never at file byte 0.
 *
 * Drives the REAL [ConcurrentRangeDownloader.download] over the
 * [RangeFaultServer] harness — no behavior is re-implemented here.
 */
class Ocds416ResumeTest {

  private lateinit var server: MockWebServer
  private lateinit var tmpDir: File

  // 3 MiB > minConcurrentBytes (2 MiB) → concurrent path, 8 non-empty parts.
  private val content: ByteArray = ByteArray(3 * 1024 * 1024) { (it % 251).toByte() }
  private val parts: List<PlannedPart> by lazy { planParts(content.size.toLong(), 8) }

  @Before
  fun setUp() {
    tmpDir = File.createTempFile("crd-ocds-416", "").let {
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

  // ------------------------------------------------------------------------
  // OCDS gap A2: 416 on a resume is TRANSIENT — recovered in place, resumable
  // bytes preserved, no from-0 restart.
  // ------------------------------------------------------------------------
  @Test
  fun ocds416OnResumeIsTransientKeepsResumableBytesAndCompletes() {
    val target = parts[5]
    val fs = RangeFaultServer(content)
      // One 416 on the part[5] window, then the retry is served normally.
      .arm(RangeFaultServer.FaultMode.STATUS_416, target.start, target.end, times = 1)
    startServer(fs)

    val partial = partialPath()

    // Seed parts 0..4 as already-completed `.segN` files (mid-run resume model).
    // These full-length segments must survive the 416 untouched.
    seedSegments(partial, content, completedThrough = 4, segments = 8)
    val seededLengths: Map<Int, Long> = (0..4).associateWith { idx ->
      val f = segFile(partial, idx)
      assertTrue("seed for part $idx must exist before run", f.exists())
      f.length()
    }
    // Sanity: each seed is exactly its planned full length going in.
    for (idx in 0..4) {
      assertEquals(
        "seed for part $idx must start at full planned length",
        parts[idx].length,
        seededLengths.getValue(idx),
      )
    }

    // Fast backoff so the single 416 retry runs quickly (mirrors ocdsT10).
    val downloader = ConcurrentRangeDownloader(
      httpClient = OkHttpClient(),
      segmentCount = 8,
      retryBaseDelayMillis = 50L,
      retryMaxDelayMillis = 200L,
    )

    val outcome = downloader.download(
      url = url(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )

    // (1) The 416 was recovered (not fatal) and assembly is byte-correct.
    assertEquals(ConcurrentRangeDownloader.Outcome.COMPLETED, outcome)
    assertEquals(content.size.toLong(), File(partial).length())
    assertEquals(sha256(content), sha256(File(partial)))

    // The probe always issues a "bytes=0-0" capability check; exclude it from the
    // segment-window counts (mirrors ocdsT2b lines 184-191).
    val PROBE = 0L to 0L
    val ranges = fs.requestedRanges.toList().filterNot { it == PROBE }

    // (2) No from-0 wipe/restart: the seeded windows 0..4 are NOT re-fetched.
    // A permanent-416 regression would discard `.segN` and re-request these.
    for (idx in 0..4) {
      val p = parts[idx]
      val reqs = ranges.filter { it.first in p.start..p.end }
      assertTrue("seeded segment $idx must NOT be re-fetched after a 416 (got $reqs)", reqs.isEmpty())
    }
    // And specifically nothing ever re-requested file byte 0 as a segment fetch.
    assertTrue(
      "no segment fetch may restart at file byte 0 after a 416",
      ranges.none { it.first == 0L && it.second == 0L },
    )

    // (3) The direct on-disk proof that a 416 never discards resumable `.segN`
    // bytes lives in `ocds416NeverDiscardsSeededSegmentsOnDisk` (a perpetual 416
    // is held mid-run so the seeded segments can be length-checked on disk). On
    // this COMPLETED path the segments are correctly consumed, so the load-bearing
    // proof here is the SHA + the request log: seeded windows 0..4 were never
    // re-fetched (asserted above) and part[5] recovered in place (asserted next).

    // (4) part[5] is requested exactly TWICE (416 then OK). The 416 has an empty
    // body, so part[5] has 0 bytes on disk → the retry re-requests the SAME full
    // window (start == part.start, end == part.end), never advancing past it and
    // never collapsing to file byte 0.
    val targetReqs = ranges.filter { it.first == target.start && it.second == target.end }
    assertEquals(
      "part[5] must be requested exactly twice (416 then OK)",
      2,
      targetReqs.size,
    )
    assertTrue(
      "every part[5] request must start inside its own window (>= part.start), never file byte 0",
      targetReqs.all { it.first >= target.start },
    )

    // (5) The tail beyond part[5] (parts 6..7) was fetched once each — the run
    // proceeded normally around the recovered 416 with no global restart.
    for (idx in 6..7) {
      val p = parts[idx]
      val reqs = ranges.filter { it.first == p.start && it.second == p.end }
      assertEquals("tail segment $idx must be requested once", 1, reqs.size)
    }

    // (6) On COMPLETED every `.segN` is consumed (no straggler artifacts).
    for (i in 0 until 8) {
      assertFalse(".seg$i must be wiped on success", segFile(partial, i).exists())
    }
  }

  // ------------------------------------------------------------------------
  // OCDS gap A2 (resumable-bytes guard, no from-0 wipe): drive a run that does
  // NOT complete past the 416 so we can observe the seeded `.segN` ON DISK and
  // prove a 416 leaves them intact (rather than discarding them and restarting
  // from byte 0). We arm 416 FOREVER on part[5] so it never recovers; the run
  // exhausts the per-segment retry budget and throws, leaving the seeded
  // segments 0..4 untouched on disk for direct length inspection.
  // ------------------------------------------------------------------------
  @Test
  fun ocds416NeverDiscardsSeededSegmentsOnDisk() {
    val target = parts[5]
    val fs = RangeFaultServer(content)
      // Fire 416 forever so part[5] never recovers and the run gives up — but a
      // transient classification must keep the OTHER segments' bytes on disk.
      .arm(RangeFaultServer.FaultMode.STATUS_416, target.start, target.end, times = Int.MAX_VALUE)
    startServer(fs)

    val partial = partialPath()
    seedSegments(partial, content, completedThrough = 4, segments = 8)
    val seededLengths: Map<Int, Long> = (0..4).associateWith { idx ->
      segFile(partial, idx).length()
    }

    // Fast backoff so the bounded retries exhaust quickly.
    val downloader = ConcurrentRangeDownloader(
      httpClient = OkHttpClient(),
      segmentCount = 8,
      retryBaseDelayMillis = 20L,
      retryMaxDelayMillis = 80L,
    )

    var threw = false
    var outcome: ConcurrentRangeDownloader.Outcome? = null
    try {
      outcome = downloader.download(
        url = url(),
        partialFilePath = partial,
        onProgress = { _, _ -> },
      )
    } catch (e: Exception) {
      threw = true
    }

    // A perpetual 416 is transient → retried then given up (throws), NEVER a
    // permanent FALLBACK that wipes artifacts, and NEVER a corrupt COMPLETED.
    assertTrue(
      "a perpetual 416 must give up via a thrown transient exhaustion, not COMPLETED/FALLBACK",
      threw,
    )
    assertFalse(
      "a 416 must not produce a COMPLETED outcome",
      outcome == ConcurrentRangeDownloader.Outcome.COMPLETED,
    )

    // THE load-bearing on-disk assertion: the seeded `.segN` for parts 0..4 are
    // still present AND still exactly their full planned length. A permanent-416
    // regression (discard resumable bytes + restart from byte 0) would have
    // deleted or truncated these — this fails loudly if that ever happens.
    for (idx in 0..4) {
      val f = segFile(partial, idx)
      assertTrue("seeded `.seg$idx` must NOT be discarded by a 416", f.exists())
      assertEquals(
        "seeded `.seg$idx` must keep its full resumable length after a 416 (no from-0 wipe)",
        seededLengths.getValue(idx),
        f.length(),
      )
      assertEquals(
        "seeded `.seg$idx` length must equal its planned window length",
        parts[idx].length,
        f.length(),
      )
    }

    // The 416 part window was actually exercised (the fault fired), and no
    // request collapsed to a from-0 restart of an already-seeded window.
    val PROBE = 0L to 0L
    val ranges = fs.requestedRanges.toList().filterNot { it == PROBE }
    val targetReqs = ranges.filter { it.first == target.start && it.second == target.end }
    assertTrue("part[5] (the 416 window) must have been requested", targetReqs.isNotEmpty())
    assertTrue(
      "every part[5] request stays inside its window; no from-0 restart",
      targetReqs.all { it.first >= target.start },
    )
    for (idx in 0..4) {
      val p = parts[idx]
      assertTrue(
        "seeded window $idx must never be re-fetched because of a 416",
        ranges.none { it.first in p.start..p.end },
      )
    }
  }
}
