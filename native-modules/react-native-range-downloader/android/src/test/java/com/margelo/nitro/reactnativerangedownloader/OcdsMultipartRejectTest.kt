package com.margelo.nitro.reactnativerangedownloader

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import okhttp3.mockwebserver.MockWebServer
import java.io.File

/**
 * OCDS §5 conformance — gap A3 (SPEC #5): a `multipart/byteranges` body served
 * to a SINGLE Range request must be REJECTED as a PERMANENT fallback (never
 * spliced raw into the segment file), and every on-disk artifact must be wiped.
 *
 * This is the multipart TWIN of [ConcurrentRangeDownloaderOcdsTest]'s
 * `ocdsT3_status200OnSegmentFallsBackAndWipesArtifacts`: same
 * FallbackException → wipeArtifacts → Outcome.FALLBACK contract, different
 * trigger. Here the core's branch at ConcurrentRangeDownloader.kt:480-483
 * (Content-Type startsWith "multipart/byteranges" → FallbackException) is the
 * one under test.
 *
 * It drives the REAL [ConcurrentRangeDownloader.download] against the
 * [RangeFaultServer] MockWebServer harness — no re-implementation of core
 * logic. The probe (bytes=0-0) is NOT armed, so it returns a normal 206 and the
 * concurrent path actually starts; only the targeted segment window is armed
 * with [RangeFaultServer.FaultMode.MULTIPART_BYTERANGES], so the worker hits the
 * multipart body, throws FallbackException, and the run falls back + wipes.
 */
class OcdsMultipartRejectTest {

  private lateinit var server: MockWebServer
  private lateinit var tmpDir: File

  // 3 MiB > minConcurrentBytes (2 MiB) → the core takes the concurrent path,
  // and 3 MiB ceil-chunked over 8 segments yields 8 non-empty parts.
  private val content: ByteArray = ByteArray(3 * 1024 * 1024) { (it % 251).toByte() }
  private val parts: List<PlannedPart> by lazy { planParts(content.size.toLong(), 8) }

  @Before
  fun setUp() {
    tmpDir = File.createTempFile("crd-ocds-multipart", "").let {
      it.delete(); it.mkdirs(); it
    }
  }

  @After
  fun tearDown() {
    if (this::server.isInitialized) server.shutdown()
    tmpDir.deleteRecursively()
  }

  // --- helpers (identical conventions to ConcurrentRangeDownloaderOcdsTest) --

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
  // OCDS §5 / gap A3: multipart/byteranges body on a SEGMENT fetch.
  //   The probe succeeds (206), the concurrent path starts, then the armed
  //   segment fetch receives a 206 whose Content-Type is
  //   "multipart/byteranges; boundary=THISISABOUNDARY". The core rejects it as
  //   a PERMANENT fallback (ConcurrentRangeDownloader.kt:480-483 →
  //   FallbackException), the worker sets fallback=true + aborted=true
  //   (lines 234-237), and after the futures join fallback.get() is true →
  //   wipeArtifacts (lines 250-253) → Outcome.FALLBACK. No boundary bytes are
  //   spliced into any segment; every `.segN` / `.partial` is wiped.
  // ------------------------------------------------------------------------
  @Test
  fun ocdsA3_multipartByterangesOnSegmentFallsBackAndWipesArtifacts() {
    val target = parts[2]
    val fs = RangeFaultServer(content)
      // Fire forever so the multipart body never "expires" into a good 206 —
      // the segment can never complete, the only resolution is fallback.
      .arm(RangeFaultServer.FaultMode.MULTIPART_BYTERANGES, target.start, target.end, times = Int.MAX_VALUE)
    startServer(fs)

    val partial = partialPath()
    val outcome = newDownloader().download(
      url = url(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )

    // Load-bearing: PERMANENT fallback (not COMPLETED, not a transient throw).
    // If the multipart branch regressed (e.g. the body was streamed raw into
    // the segment file), this would assemble corrupt bytes and report COMPLETED
    // instead — the equality fails.
    assertEquals(ConcurrentRangeDownloader.Outcome.FALLBACK, outcome)

    // Load-bearing: no corruption left behind. The `.partial` and ALL eight
    // `.segN` files MUST be wiped. A raw multipart body spliced into `.seg2`
    // (the regression) would leave a boundary-contaminated artifact here, and
    // the assembled `.partial` (if it ever formed) would survive — both would
    // fail assertNoArtifactsLeft.
    assertNoArtifactsLeft(partial)
  }
}
