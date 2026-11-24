package com.margelo.nitro.qrscanner

import android.graphics.Bitmap
import android.graphics.Color
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

private class RecordingEngine(
  override val name: String,
  private val answer: (callIndex: Int, bitmap: Bitmap) -> String?,
) : QrImageEngine {
  val sizes = ArrayList<Pair<Int, Int>>()

  override fun decode(bitmap: Bitmap): String? {
    sizes += bitmap.width to bitmap.height
    return answer(sizes.size - 1, bitmap)
  }
}

@RunWith(RobolectricTestRunner::class)
// Same SDK as the other packages' Robolectric tests (SDK 36 needs Java 21).
@Config(sdk = [34])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class QrImageDecodingPipelineTest {
  private fun solid(width: Int, height: Int, color: Int): Bitmap =
    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply { eraseColor(color) }

  private fun pngFile(bitmap: Bitmap): File =
    File.createTempFile("qrfixture", ".png").apply {
      outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
      deleteOnExit()
    }

  @Test
  fun passesRunInOrderAndStopAtFirstHit() {
    // Hit on the third image: original, padded, grayscale.
    val engine = RecordingEngine("zxing") { index, _ -> if (index == 2) "found" else null }
    val hit = QrImageDecodingPipeline(listOf(engine)).decodeBitmap(solid(400, 300, Color.BLUE))
    assertEquals(QrImageHit("found", "zxing", "grayscale"), hit)
    assertEquals(listOf(400 to 300, 480 to 380, 400 to 300), engine.sizes)
  }

  @Test
  fun platformEngineRunsBeforeFallbackWithinEachPass() {
    val order = ArrayList<String>()
    val first = RecordingEngine("mlkit") { _, _ -> order += "mlkit"; null }
    val second = RecordingEngine("zxing") { _, _ -> order += "zxing"; null }
    assertNull(QrImageDecodingPipeline(listOf(first, second)).decodeBitmap(solid(10, 10, Color.WHITE)))
    assertEquals(List(4) { listOf("mlkit", "zxing") }.flatten(), order)
  }

  @Test
  fun paddedAddsWhiteBorderAndGrayscaleCanInvert() {
    val padded = QrImageDecodingPipeline.padded(solid(400, 300, Color.BLACK))
    assertEquals(480, padded.width)
    assertEquals(Color.WHITE, padded.getPixel(0, 0))
    assertEquals(Color.BLACK, padded.getPixel(240, 190))
    val dark = QrImageDecodingPipeline.grayscale(solid(1, 1, Color.rgb(40, 40, 40)), inverted = false)
    val inverted = QrImageDecodingPipeline.grayscale(solid(1, 1, Color.rgb(40, 40, 40)), inverted = true)
    assertTrue(Color.red(dark.getPixel(0, 0)) < 40)
    assertTrue(Color.red(inverted.getPixel(0, 0)) > 215)
  }

  @Test
  fun filesAreDownscaledToPixelBudget() {
    val engine = RecordingEngine("mlkit") { _, _ -> null }
    QrImageDecodingPipeline(listOf(engine)).decode(pngFile(solid(400, 300, Color.GRAY)), 10_000)
    val (width, height) = engine.sizes.first()
    assertTrue("$width x $height", width * height <= 10_000)
    assertEquals(4.0 / 3.0, width.toDouble() / height, 0.05)
  }

  @Test
  fun unreadableFilesAreInvalidImage() {
    val pipeline = QrImageDecodingPipeline(emptyList())
    val corrupt = File.createTempFile("corrupt", ".png").apply { writeText("not an image"); deleteOnExit() }
    for (file in listOf(corrupt, File("/nonexistent/qr.png"))) {
      val error = assertThrows(QrImageDecodeException::class.java) { pipeline.decode(file, 1_000) }
      assertEquals("invalid-image", error.code)
    }
  }

  @Test
  fun localFileAcceptsPathsAndFileUrlsOnly() {
    assertEquals("/data/x.png", HybridQrImageDecoder.localFile("/data/x.png")?.path)
    assertEquals("/data/x.png", HybridQrImageDecoder.localFile("file:///data/x.png")?.path)
    assertNull(HybridQrImageDecoder.localFile("content://media/1"))
  }
}
