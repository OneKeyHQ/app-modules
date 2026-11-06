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
import java.nio.charset.Charset

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

  private fun displayed(source: String, decodeSize: Int, displaySize: Int, charset: Charset = Charsets.UTF_8): Bitmap {
    // This is the same SVG -> PictureDrawable -> ImageView chain used by Glide.
    val svg = OneKeySVG.parse(source.byteInputStream(charset))
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
    val raster = Bitmap.createBitmap(intArrayOf(0, Color.RED, 0, Color.RED), 2, 2, Bitmap.Config.ARGB_8888)
    val encoded = ByteArrayOutputStream()
    raster.compress(Bitmap.CompressFormat.PNG, 100, encoded)
    raster.recycle()
    val data = Base64.encodeToString(encoded.toByteArray(), Base64.NO_WRAP)
    val clippedSource = source.replace(Regex("src='data:image/png;base64,[^']+'"), "src='data:image/png;base64,$data'")
      .replace("<foreignObject", "<defs><clipPath id='box' clipPathUnits='objectBoundingBox'><rect width='0.5' height='1'/></clipPath></defs><foreignObject clip-path='url(#box)'")
    val clipped = displayed(clippedSource, 192, 32)
    try { assertEquals(Color.GREEN, clipped.getPixel(20, 8)) }
    finally { clipped.recycle() }
  }

  fun testPixelPathsIgnoreInheritedStrokeAndKeepSiblingStroke() {
    val source = document("<style>img { image-rendering:pixelated; }</style>")
      .replace("<rect width='24' height='24' fill='#00ff00'/>", "")
      .replace("<foreignObject width='24' height='24'>", "<foreignObject x='4' y='4' width='16' height='16'>")
    val sibling = "<path d='M1 1H23' fill='none'/>"
    val plain = displayed(source, 96, 96)
    try {
      for (inherited in listOf(
        source.replace("viewBox=", "stroke='green' stroke-width='0.2' viewBox=").replace("</svg>", "$sibling</svg>"),
        source.replace("<foreignObject", "<g stroke='green' stroke-width='0.2'><foreignObject")
          .replace("</foreignObject>", "</foreignObject>$sibling</g>"),
      )) {
        val output = displayed(inherited, 96, 96)
        try {
          for (y in 16 until 80) for (x in 16 until 80) {
            assertEquals("x=$x y=$y", plain.getPixel(x, y), output.getPixel(x, y))
          }
          assertTrue(Color.green(output.getPixel(48, 4)) > 0)
          assertEquals(Color.TRANSPARENT, plain.getPixel(48, 4))
        } finally { output.recycle() }
      }
    } finally { plain.recycle() }
  }

  fun testSurroundingSVGStylesAndImageSamplingRenderTogether() {
    for (css in listOf(
      "<style>rect, circle { fill:red; }</style>",
      "<style>rect, circle { fill:red; } img { image-rendering:pixelated; }</style>",
      "<style>img { image-rendering:pixelated; } rect { fill:red; }</style><style>img { image-rendering:smooth; }</style>",
    )) {
      val source = document(css).replace(" fill='#00ff00'", " fill='green'")
      val output = displayed(source, 192, 96)
      try {
        // The transparent PNG quadrant exposes the SVG rectangle's CSS fill.
        assertEquals(Color.RED, output.getPixel(24, 72))
        assertEquals(Color.RED, output.getPixel(24, 24))
        assertEquals(Color.BLUE, output.getPixel(72, 24))
        val edge = output.getPixel(47, 24)
        if (css.contains("smooth") || !css.contains("pixelated")) {
          assertTrue(Color.red(edge) in 1..254)
        } else assertEquals(Color.RED, edge)
      } finally { output.recycle() }
    }
  }

  fun testUTF16ForeignObjectsMatchUTF8Pixels() {
    for (css in listOf("", "<style>img { image-rendering:pixelated; }</style>")) {
      val source = document(css)
      val expected = displayed(source, 192, 32)
      try {
        for (charset in listOf(Charsets.UTF_16LE, Charsets.UTF_16BE)) {
          for (bom in listOf("", "\uFEFF")) {
            val encoded = bom + "<?xml version='1.0' encoding='${charset.name()}'?>" + source
            val actual = displayed(encoded, 192, 32, charset)
            try { assertTrue(actual.sameAs(expected)) }
            finally { actual.recycle() }
          }
        }
      } finally { expected.recycle() }
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
