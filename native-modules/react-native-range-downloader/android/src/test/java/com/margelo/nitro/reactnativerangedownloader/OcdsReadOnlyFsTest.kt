package com.margelo.nitro.reactnativerangedownloader

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
 * OCDS gap A6 (SPEC §5.2 disk) — the segment-file design exists SPECIFICALLY to
 * survive near-full / read-only filesystems: there is NO `RandomAccessFile.
 * setLength(total)` pre-allocation (which fails EROFS/ENOSPC up front on f2fs),
 * only plain O_WRONLY sequential appends + O_RDONLY reads. This suite drives the
 * REAL [ConcurrentRangeDownloader.download] against a directory we flip to
 * read-only with [File.setWritable] and asserts the download fails CLEANLY:
 *
 *   - it throws / returns a NON-COMPLETED outcome (never a corrupt COMPLETED),
 *   - it leaves NO partial/corrupt assembled artifact (`.partial` is not created
 *     in the read-only dir, so nothing half-written can be promoted),
 *   - it does NOT data-loss the resumable `.segN` inputs (the read-only dir
 *     blocks NEW-file creation but the already-present segment files survive,
 *     readable, full-length — so a later attempt on a writable dir resumes), and
 *   - it does NOT crash-loop: once the dir is writable again the SAME inputs
 *     assemble idempotently to the correct whole-file SHA-256.
 *
 * Conventions mirror [ConcurrentRangeDownloaderOcdsTest]: 3 MiB content over 8
 * segments, [newDownloader] / [planParts] / [seedSegments] / [RangeFaultServer]
 * / [sha256] / [assertNoArtifactsLeft]. The probe (`bytes=0-0`) is READ-only, so
 * a read-only dir does not block it — the failure must come from the concat
 * write, exactly as §5.2 describes.
 *
 * Out of scope (asserted/explained, never forced): the fsync DURABILITY guarantee
 * itself (concatenate(): `out.fd.sync()`) is not directly observable from the JVM
 * — a JVM test cannot prove the bytes reached the platter, only that the assemble
 * → size-guard → (would-be) rename pipeline is correct and crash-safe. What §5.2
 * IS testable for is asserted below: concat over a read-only dir fails cleanly,
 * and committed-prefix concat is idempotent.
 */
class OcdsReadOnlyFsTest {

  private lateinit var server: MockWebServer
  private lateinit var tmpDir: File

  // Same shape as the core OCDS suite: 3 MiB > minConcurrentBytes (2 MiB) so the
  // concurrent path is taken, ceil-chunked over 8 non-empty segments.
  private val content: ByteArray = ByteArray(3 * 1024 * 1024) { (it % 251).toByte() }
  private val parts: List<PlannedPart> by lazy { planParts(content.size.toLong(), 8) }

  @Before
  fun setUp() {
    tmpDir = File.createTempFile("crd-a6-rofs", "").let {
      it.delete(); it.mkdirs(); it
    }
  }

  @After
  fun tearDown() {
    // Always restore writability so deleteRecursively can clean up even if a test
    // body asserts before flipping it back.
    tmpDir.setWritable(true)
    if (this::server.isInitialized) server.shutdown()
    tmpDir.deleteRecursively()
  }

  // --- helpers (identical conventions to ConcurrentRangeDownloaderOcdsTest) ----

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
  // A6(a): concat into a READ-ONLY directory fails CLEANLY.
  //   Seed all 8 `.segN` (so download() skips fetching and goes straight to the
  //   concat — concatenate() at :332), then flip the dir read-only. The concat's
  //   `FileOutputStream(partialFile, append=true)` (:344) cannot CREATE the
  //   `.partial` in a read-only dir → FileNotFoundException propagates out of
  //   download(). Load-bearing assertions: it THREW, NO COMPLETED outcome, and NO
  //   `.partial` artifact exists (nothing half-written to promote). Crucially the
  //   resumable `.segN` inputs survive full-length (a read-only dir blocks NEW
  //   files, not reads of existing ones), so no data is lost.
  // ------------------------------------------------------------------------
  @Test
  fun a6a_concatIntoReadOnlyDirFailsCleanlyNoArtifactNoDataLoss() {
    val fs = RangeFaultServer(content)
    startServer(fs)

    val partial = partialPath()
    // All 8 segments present and full-length → planning marks none pending, the
    // worker pool never runs, and download() proceeds directly to concatenate().
    seedSegments(partial, content, completedThrough = 7, segments = 8)
    for (i in 0 until 8) {
      assertTrue("seeded segment $i must exist before RO flip", segFile(partial, i).exists())
    }

    // Make the destination directory read-only. The probe (bytes=0-0) is a pure
    // read, so it still succeeds; the ONLY thing that fails is the concat write.
    assertTrue("test precondition: setWritable(false) must succeed", tmpDir.setWritable(false))
    assertFalse("test precondition: dir must now be read-only", tmpDir.canWrite())

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

    // CLEAN failure: a read-only filesystem must surface as a thrown error, never
    // a corrupt success. (No setLength pre-allocation crash; the plain O_WRONLY
    // open simply fails and the exception propagates.)
    assertTrue("read-only concat must throw (clean failure, not a crash-loop)", threw)
    assertNotEquals(
      "read-only filesystem must NOT yield a COMPLETED outcome",
      ConcurrentRangeDownloader.Outcome.COMPLETED,
      outcome,
    )
    // NO partial/corrupt assembled artifact: the read-only dir refuses to create
    // the `.partial`, so there is nothing half-written for the caller to promote.
    assertFalse(
      "no `.partial` may be created in a read-only dir",
      File(partial).exists(),
    )
    // NO data loss: the resumable inputs survive, each at its exact planned
    // length, so a later attempt on a writable dir resumes (no from-0 restart).
    for (p in parts) {
      val seg = segFile(partial, p.index)
      assertTrue("segment ${p.index} must survive the read-only failure", seg.exists())
      assertEquals(
        "surviving segment ${p.index} must keep its full planned length",
        p.length,
        seg.length(),
      )
    }

    // No crash-loop: restoring writability lets the SAME inputs assemble cleanly.
    // This drives the REAL concat over the surviving `.segN` and proves the prior
    // read-only failure left a perfectly resumable state.
    assertTrue("restore: setWritable(true) must succeed", tmpDir.setWritable(true))
    val retryOutcome = newDownloader().download(
      url = url(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )
    assertEquals(
      "retry on a writable dir must COMPLETE from the surviving segments",
      ConcurrentRangeDownloader.Outcome.COMPLETED,
      retryOutcome,
    )
    // Load-bearing: a REAL SHA-256 over the REAL assembled file vs the source. A
    // from-0 restart, a corrupt concat, or a misordered splice would fail here.
    assertEquals(sha256(content), sha256(File(partial)))
    // Concat consumed every segment on success.
    for (i in 0 until 8) assertFalse(segFile(partial, i).exists())
  }

  // ------------------------------------------------------------------------
  // A6(b): COMMITTED-PREFIX idempotent concat (§5.2 "assemble is idempotent /
  //   committed-prefix resume"). Model a concat that was interrupted PART-WAY:
  //   all 8 `.segN` are present AND the `.partial` already holds the first K
  //   segments' combined extent (a mid-concat-interrupt snapshot). On a writable
  //   dir, download() must NOT re-append the already-committed prefix (append
  //   writes at EOF; concatenate() skips bytes below `written` at :348-352) and
  //   must finish to the exact correct whole-file SHA — i.e. running concat over a
  //   partially-committed `.partial` is idempotent and never double-writes the
  //   prefix or restarts from 0.
  // ------------------------------------------------------------------------
  @Test
  fun a6b_committedPrefixConcatIsIdempotentToCorrectSha() {
    val fs = RangeFaultServer(content)
    startServer(fs)

    val partial = partialPath()
    // All 8 segments full-length on disk (a clean "all fetched" state).
    seedSegments(partial, content, completedThrough = 7, segments = 8)

    // Pre-write a `.partial` equal to the first 3 segments' combined extent — the
    // exact byte image an interrupted concat would have left (the first K segments
    // already appended, in order, into `.partial`). Use the genuine source bytes
    // so the committed prefix is correct; concatenate() must extend, not rewrite.
    val k = 3
    val committedExtent = parts.take(k).sumOf { it.length }
    File(partial).writeBytes(content.copyOfRange(0, committedExtent.toInt()))
    assertEquals(
      "precondition: pre-committed `.partial` spans the first $k segments",
      committedExtent,
      File(partial).length(),
    )
    // The committed-prefix model also allows the consumed segments' `.segN` to be
    // gone (an interrupted concat deletes each seg after appending it). Delete the
    // first K to exercise the "committed prefix segment whose `.segN` is gone is
    // still DONE" branch (:201-223) — it must NOT be re-fetched (the probe is the
    // only network the server should see beyond none).
    for (i in 0 until k) segFile(partial, i).delete()

    val outcome = newDownloader().download(
      url = url(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )

    assertEquals(ConcurrentRangeDownloader.Outcome.COMPLETED, outcome)
    // Load-bearing: idempotent concat must land EXACTLY the source bytes. A
    // double-appended prefix would overshoot total (caught by the :376 size guard
    // → delete+throw, so we would NOT be COMPLETED) or, if it slipped through,
    // mismatch this SHA.
    assertEquals(content.size.toLong(), File(partial).length())
    assertEquals(sha256(content), sha256(File(partial)))

    // The committed-prefix segments (whose `.segN` we deleted) must NOT have been
    // re-fetched: the only request the server should ever see is the probe
    // (bytes=0-0). Any real segment fetch here would prove the committed prefix
    // was wrongly treated as missing (a from-disk restart, the §4 cardinal sin).
    val PROBE = 0L to 0L
    val segmentRequests = fs.requestedRanges.toList().filterNot { it == PROBE }
    assertTrue(
      "no segment may be re-fetched when all are on disk / committed (got $segmentRequests)",
      segmentRequests.isEmpty(),
    )
    // Concat consumed every remaining segment on success. (The `.partial` is the
    // assembled result the caller promotes — it is NOT wiped on COMPLETED; only
    // the `.segN` inputs are consumed. Its full-length correctness is already
    // asserted above via the size + SHA-256 checks.)
    for (i in 0 until 8) {
      assertFalse(".seg$i must be consumed on success", segFile(partial, i).exists())
    }
  }
}
