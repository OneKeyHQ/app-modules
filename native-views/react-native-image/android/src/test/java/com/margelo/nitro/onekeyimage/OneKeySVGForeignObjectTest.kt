package com.margelo.nitro.onekeyimage

import java.io.IOException
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class OneKeySVGForeignObjectTest {
  private val png = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR4nGP4z8DwHwAFAAH/iZk9HQAAAABJRU5ErkJggg=="
  private val image = "<img xmlns=\"http://www.w3.org/1999/xhtml\" src=\"data:image/png;base64,$png\" width=\"100%\" height=\"100%\"></img>"
  private fun foreign(body: String, attributes: String = "") =
    "<foreignObject width=\"24\" height=\"24\" $attributes>$body</foreignObject>"
  private fun document(body: String) =
    "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\">$body</svg>".toByteArray()

  @Test
  fun embeddedPNGConvertsToAndroidSVGImageWithoutChangingItsSource() {
    val source = document("<style>img { width:100%; height:100%; }</style>" + foreign(image))
    var validatedImages = 0
    val normalized = OneKeySVG.normalize(source) { _, _ -> validatedImages++; null }.toString(Charsets.UTF_8)
    assertFalse(normalized.contains("foreignObject"))
    assertTrue(normalized.contains("xlink:href=\"data:image/png;base64,$png\""))
    assertTrue(normalized.contains("preserveAspectRatio=\"none\""))
    assertTrue(validatedImages == 1)
  }

  @Test
  fun ordinarySVGKeepsItsExistingDecoderInput() {
    val source = document("<rect width=\"24\" height=\"24\" fill=\"red\"/>")
    assertArrayEquals(source, OneKeySVG.normalize(source) { _, _ -> throw AssertionError("Ordinary SVG must not decode embedded PNGs") })
    val commented = document("<!-- foreignObject is not used --><style>rect { fill:red; }</style><rect width=\"24\" height=\"24\"/>")
    assertArrayEquals(commented, OneKeySVG.normalize(commented) { _, _ -> null })
  }

  @Test
  fun imageRenderingUsesDocumentOrderAndKeepsSmoothImagesSmooth() {
    for ((css, expected) in listOf(
      "" to "optimizeQuality",
      "<style>img { image-rendering:pixelated; }</style>" to "optimizeSpeed",
      "<style>img { image-rendering:pixelated; image-rendering:auto; -ms-interpolation-mode:nearest-neighbor; }</style>" to "optimizeQuality",
      "<style>img { image-rendering:pixelated; }</style><style>img { image-rendering:smooth; }</style>" to "optimizeQuality",
      "<style>img { -ms-interpolation-mode:nearest-neighbor; }</style>" to "optimizeSpeed",
    )) {
      val converted = OneKeySVG.normalize(document(foreign(image) + css)) { _, nearest ->
        if (nearest) intArrayOf(0xffff0000.toInt()) else null
      }.toString(Charsets.UTF_8)
      assertTrue(converted.contains("image-rendering=\"$expected\""))
    }
  }

  @Test
  fun surroundingSVGStylesArePreservedWithoutChangingImageSampling() {
    for ((css, nearest) in listOf(
      "<style>rect, circle { fill:red; }</style>" to false,
      "<style>rect, circle { fill:red; } img { image-rendering:pixelated; }</style>" to true,
      "<style>img { image-rendering:pixelated; } rect { fill:red; }</style><style>img { image-rendering:smooth; }</style>" to false,
    )) {
      val output = OneKeySVG.normalize(document(css + foreign(image))) { _, actual ->
        assertTrue(actual == nearest)
        if (actual) intArrayOf(0xffff0000.toInt()) else null
      }.toString(Charsets.UTF_8)
      assertTrue(output.contains("fill:red;"))
      assertTrue(output.contains("image-rendering=\"${if (nearest) "optimizeSpeed" else "optimizeQuality"}\""))
    }
  }

  @Test
  fun stylesThatCouldAffectConvertedContentStillFail() {
    for (selector in listOf("*", ".art", "#art", "g", "svg", "image", "path", "img, rect", "rect img", "rect:hover")) {
      assertThrows(IOException::class.java) {
        OneKeySVG.normalize(document("<style>$selector { fill:red; }</style>" + foreign(image))) { _, _ -> null }
      }
    }
    for (css in listOf("@media all { rect { fill:red; } }", "@import 'external.css'; rect { fill:red; }")) {
      assertThrows(IOException::class.java) {
        OneKeySVG.normalize(document("<style>$css</style>" + foreign(image))) { _, _ -> null }
      }
    }
  }

  @Test
  fun embeddedRasterDecodeFailureIsNotAConvertedSuccess() {
    assertThrows(IOException::class.java) {
      OneKeySVG.normalize(document(foreign(image))) { _, _ -> throw IOException("Corrupt PNG pixels") }
    }
  }

  @Test
  fun unsupportedHTMLExternalSourcesAndLayoutFail() {
    for (body in listOf(
      foreign("<div xmlns=\"http://www.w3.org/1999/xhtml\">Unsupported</div>"),
      foreign("<img xmlns=\"http://www.w3.org/1999/xhtml\" src=\"https://example.com/a.png\"/>"),
      foreign(image + image),
      foreign(image, "style=\"display:none\""),
      foreign(image.replace("100%", "50%")),
      "<style>img { object-fit:cover; }</style>" + foreign(image),
      foreign("<img xmlns=\"http://www.w3.org/1999/xhtml\" src=\"data:image/png;base64,AAAA\"/>"),
    )) assertThrows(IOException::class.java) { OneKeySVG.normalize(document(body)) { _, _ -> null } }
  }

  @Test
  fun boundsAndEntitiesFailBeforeNativeRendering() {
    val nested = "<g>".repeat(64) + foreign(image) + "</g>".repeat(64)
    assertThrows(IOException::class.java) { OneKeySVG.normalize(document(nested)) { _, _ -> null } }
    assertThrows(IOException::class.java) { OneKeySVG.normalize(document(foreign(image).repeat(17))) { _, _ -> null } }
    val entity = "<!DOCTYPE svg [<!ENTITY image 'test'>]>".toByteArray() + document(foreign(image))
    assertThrows(IOException::class.java) { OneKeySVG.normalize(entity) { _, _ -> null } }
  }
}
