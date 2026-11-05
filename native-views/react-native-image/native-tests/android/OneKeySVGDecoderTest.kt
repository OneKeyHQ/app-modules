package com.margelo.nitro.onekeyimage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.PictureDrawable
import android.test.InstrumentationTestCase
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.io.IOException

@Suppress("DEPRECATION")
class OneKeySVGDecoderTest : InstrumentationTestCase() {
  private fun document(css: String, side: Int = 2): String {
    val raster = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888)
    val pixels = if (side == 2) intArrayOf(Color.RED, Color.BLUE, Color.TRANSPARENT,
      Color.argb(128, 255, 0, 0)) else IntArray(side * side) {
        if ((it / side + it % side) % 2 == 0) Color.RED else Color.BLUE
      }
    raster.setPixels(pixels, 0, side, 0, 0, side, side)
    val output = ByteArrayOutputStream()
    raster.compress(Bitmap.CompressFormat.PNG, 100, output)
    raster.recycle()
    val source = Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
    return "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'>" +
      "<rect width='24' height='24' fill='#00ff00'/>" +
      "<foreignObject width='24' height='24'><img xmlns='http://www.w3.org/1999/xhtml' " +
      "src='data:image/png;base64,$source'/></foreignObject>$css</svg>"
  }

  private fun displayed(source: String, decodeSize: Int, displaySize: Int): Bitmap {
    // This is the same SVG -> PictureDrawable -> ImageView chain used by Glide.
    val svg = OneKeySVG.parse(source.byteInputStream())
    svg.setDocumentWidth(decodeSize.toFloat())
    svg.setDocumentHeight(decodeSize.toFloat())
    val drawable = PictureDrawable(svg.renderToPicture())
    val output = Bitmap.createBitmap(displaySize, displaySize, Bitmap.Config.ARGB_8888)
    instrumentation.runOnMainSync {
      val view = ImageView(instrumentation.targetContext)
      view.scaleType = ImageView.ScaleType.FIT_CENTER
      view.setImageDrawable(drawable)
      view.layout(0, 0, displaySize, displaySize)
      view.draw(Canvas(output))
    }
    return output
  }

  fun testPixelatedPictureDrawableKeepsEdgesAlphaAndBackground() {
    val source = document("<style>img { image-rendering:pixelated; }</style>")
    for (size in listOf(32, 37, 192, 480)) {
      val bitmap = displayed(source, 192, size)
      try {
        val left = size / 2 - if (size % 2 == 0) 1 else 2
        val right = size / 2 + if (size % 2 == 0) 0 else 1
        assertEquals("size=$size left", Color.RED, bitmap.getPixel(left, size / 4))
        assertEquals("size=$size right", Color.BLUE, bitmap.getPixel(right, size / 4))
        assertEquals(Color.GREEN, bitmap.getPixel(size / 4, size * 3 / 4))
        val alpha = bitmap.getPixel(size * 3 / 4, size * 3 / 4)
        assertTrue(kotlin.math.abs(Color.red(alpha) - 128) <= 1)
        assertTrue(kotlin.math.abs(Color.green(alpha) - 127) <= 1)
        assertEquals(255, Color.alpha(alpha))
      } finally { bitmap.recycle() }
    }
  }

  fun testSmoothDeclarationOverridesPixelatedInPictureDrawable() {
    for (css in listOf("", "<style>img { image-rendering:pixelated; image-rendering:auto; }</style>")) {
      val bitmap = displayed(document(css), 192, 32)
      try {
        val edge = bitmap.getPixel(15, 8)
        assertTrue(Color.red(edge) in 1..254)
        assertTrue(Color.blue(edge) in 1..254)
      } finally { bitmap.recycle() }
    }
  }

  fun testPixelGeometryBudgetIsSharedAcrossTheDocument() {
    val css = "<style>img { image-rendering:pixelated; }</style>"
    val source = document(css, 100)
    val foreign = "<foreignObject" + source.substringAfter("<foreignObject").substringBefore("</foreignObject>") + "</foreignObject>"
    for (oversized in listOf(document(css, 129), source.replace("</foreignObject>", "</foreignObject>$foreign"))) {
      try {
        OneKeySVG.parse(oversized.byteInputStream())
        fail("Excessive pixel geometry must fail decoding")
      } catch (_: IOException) { }
    }
  }
}
