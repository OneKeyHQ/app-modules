package com.margelo.nitro.reactnativerangedownloader

import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Harness smoke test: confirms the [RangeFaultServer] dispatcher + MockWebServer
 * + a real [ConcurrentRangeDownloader] over a plain OkHttpClient compile and run
 * a clean full download. A green run here proves the JVM test harness works end
 * to end (8 parallel range requests served concurrently, assembled `.partial`
 * byte-identical to the source).
 */
class SmokeTest {

  private lateinit var server: MockWebServer
  private lateinit var tmpDir: File

  // 3 MiB > minConcurrentBytes (2 MiB) so the core uses the concurrent path.
  private val content: ByteArray = ByteArray(3 * 1024 * 1024) { (it % 251).toByte() }

  @Before
  fun setUp() {
    server = MockWebServer()
    server.dispatcher = RangeFaultServer(content).dispatcher
    server.start()
    tmpDir = File.createTempFile("crd-smoke", "").let {
      it.delete(); it.mkdirs(); it
    }
  }

  @After
  fun tearDown() {
    server.shutdown()
    tmpDir.deleteRecursively()
  }

  @Test
  fun fullDownloadCompletesWithMatchingSha() {
    val partial = File(tmpDir, "asset.bin.partial").absolutePath
    val outcome = newDownloader().download(
      url = server.url("/asset.bin").toString(),
      partialFilePath = partial,
      onProgress = { _, _ -> },
    )

    assertEquals(ConcurrentRangeDownloader.Outcome.COMPLETED, outcome)
    val partialFile = File(partial)
    assertTrue("partial should exist", partialFile.exists())
    assertEquals(content.size.toLong(), partialFile.length())
    assertEquals(
      "assembled .partial SHA must match source",
      sha256(content),
      sha256(partialFile),
    )
    // Every `.segN` is consumed by concatenate() on success.
    for (i in 0 until 8) {
      assertTrue(".seg$i should be gone", !File("$partial.seg$i").exists())
    }
  }
}
