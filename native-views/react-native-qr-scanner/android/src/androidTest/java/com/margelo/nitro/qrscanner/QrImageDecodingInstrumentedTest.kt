package com.margelo.nitro.qrscanner

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Real-engine Android instrumentation tests exercising bundled ML Kit and
 * ZXing-C++ against all 4 multi-QR / animated-UR fixtures (a, b, c, d) with
 * zero camera dependency.
 */
@RunWith(AndroidJUnit4::class)
class QrImageDecodingInstrumentedTest {
  private fun copyAssetToCache(name: String): File {
    val testContext = InstrumentationRegistry.getInstrumentation().context
    val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
    val outFile = File(targetContext.cacheDir, name)
    testContext.assets.open(name).use { input ->
      outFile.outputStream().use { output -> input.copyTo(output) }
    }
    return outFile
  }

  private fun imageEngines(): List<QrImageEngine> {
    val mlkit = MlKitQrEngine()
    val zxing = checkNotNull(ZXingQrEngine.createOrNull()) {
      "ZXing-C++ must load in the instrumentation APK"
    }
    return listOf(mlkit, zxing)
  }

  private fun extractVideoFrames(videoFile: File): List<Bitmap> {
    val retriever = MediaMetadataRetriever()
    val frames = ArrayList<Bitmap>()
    try {
      retriever.setDataSource(videoFile.absolutePath)
      val count = retriever
        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT)
        ?.toIntOrNull()
        ?: 0
      if (count > 0) {
        for (index in 0 until count) {
          retriever.getFrameAtIndex(index)?.let(frames::add)
        }
      } else {
        val durationMs = retriever
          .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
          ?.toLongOrNull()
          ?: 0L
        var timeUs = 0L
        while (timeUs <= durationMs * 1000L) {
          retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST)?.let(frames::add)
          timeUs += 125_000L // 8 fps
        }
      }
    } finally {
      retriever.release()
    }
    return frames
  }

  @Test
  fun caseA_singleStaticImageSymbols() {
    val expected = "ethereum:0x71C7656EC7ab88b098defB751B7401B5f6d8976F"
    val file = copyAssetToCache("a-single-static.png")
    for (engine in imageEngines()) {
      val hit = QrImageDecodingPipeline(listOf(engine)).decodeSymbols(file, 12_000_000L)
      assertEquals("${engine.name} symbol count", 1, hit.symbols.size)
      assertEquals("${engine.name} text", expected, hit.symbols[0].text)
      assertEquals("${engine.name} centerX", 0.5, hit.symbols[0].centerX, 0.03)
      assertEquals("${engine.name} centerY", 0.5, hit.symbols[0].centerY, 0.03)
    }
  }

  @Test
  fun caseB_multiStaticImageSymbols() {
    val expected = listOf(
      Triple(
        "wc:8a3f9c2d1b0e4a5f6c7d8e9f0a1b2c3d@2?relay-protocol=irn&symKey=abcdef123456",
        (540.0 + 180.0) / 960.0,
        (60.0 + 180.0) / 960.0,
      ),
      Triple(
        "onekey://market/detail?symbol=ETH",
        (90.0 + 130.0) / 960.0,
        (350.0 + 130.0) / 960.0,
      ),
      Triple(
        "ethereum:0x71C7656EC7ab88b098defB751B7401B5f6d8976F",
        (580.0 + 140.0) / 960.0,
        (610.0 + 140.0) / 960.0,
      ),
    )
    val file = copyAssetToCache("b-multi-static.png")
    for (engine in imageEngines()) {
      val hit = QrImageDecodingPipeline(listOf(engine)).decodeSymbols(file, 12_000_000L)
      assertEquals("${engine.name} symbol count", 3, hit.symbols.size)
      for ((text, ex, ey) in expected) {
        val match = hit.symbols.firstOrNull { it.text == text }
        assertNotNull("${engine.name} missing $text", match)
        assertEquals("${engine.name} X for $text", ex, match!!.centerX, 0.03)
        assertEquals("${engine.name} Y for $text", ey, match.centerY, 0.03)
      }
    }
  }

  @Test
  fun caseC_animatedUrVideoFrames() {
    val frames = extractVideoFrames(copyAssetToCache("c-animated-ur.mp4"))
    assertTrue("expected >= 17 frames, got ${frames.size}", frames.size >= 17)
    val mlkit = MlKitQrEngine()
    val urParts = HashSet<String>()
    try {
      for (frame in frames) {
        val symbols = mlkit.decodeSymbols(frame)
        assertEquals(1, symbols.size)
        val text = symbols[0].text
        assertTrue("unexpected text: $text", text.uppercase().startsWith("UR:CRYPTO-PSBT/"))
        urParts += text
      }
    } finally {
      frames.forEach(Bitmap::recycle)
    }
    assertTrue("expected >= 17 distinct UR parts, got ${urParts.size}", urParts.size >= 17)
  }

  @Test
  fun caseD_animatedUrPlusMultiStaticVideoFrames() {
    val frames = extractVideoFrames(copyAssetToCache("d-animated-ur-plus-multi.mp4"))
    assertTrue("expected >= 17 frames, got ${frames.size}", frames.size >= 17)
    val mlkit = MlKitQrEngine()
    val urParts = HashSet<String>()
    try {
      val firstSymbols = mlkit.decodeSymbols(frames[0])
      assertEquals("frame 0 should detect all 3 codes", 3, firstSymbols.size)
      val firstUr = firstSymbols.firstOrNull { it.text.uppercase().startsWith("UR:CRYPTO-PSBT/") }
      val firstEth = firstSymbols.firstOrNull {
        it.text == "ethereum:0x71C7656EC7ab88b098defB751B7401B5f6d8976F"
      }
      val firstMarket = firstSymbols.firstOrNull { it.text == "onekey://market/detail?symbol=ETH" }
      assertNotNull("frame 0 missing UR", firstUr)
      assertNotNull("frame 0 missing ETH", firstEth)
      assertNotNull("frame 0 missing Market", firstMarket)

      assertEquals((40.0 + 230.0) / 960.0, firstUr!!.centerX, 0.03)
      assertEquals((250.0 + 230.0) / 960.0, firstUr.centerY, 0.03)
      assertEquals((580.0 + 150.0) / 960.0, firstEth!!.centerX, 0.03)
      assertEquals((100.0 + 150.0) / 960.0, firstEth.centerY, 0.03)
      assertEquals((580.0 + 150.0) / 960.0, firstMarket!!.centerX, 0.03)
      assertEquals((560.0 + 150.0) / 960.0, firstMarket.centerY, 0.03)

      for (frame in frames) {
        val symbols = mlkit.decodeSymbols(frame)
        symbols.firstOrNull { it.text.uppercase().startsWith("UR:CRYPTO-PSBT/") }?.let {
          urParts += it.text
        }
      }
    } finally {
      frames.forEach(Bitmap::recycle)
    }
    assertTrue("expected >= 17 distinct UR parts, got ${urParts.size}", urParts.size >= 17)
  }
}
