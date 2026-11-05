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
    val source = document("<style>img { width:100%; height:100%; image-rendering:pixelated; }</style>" + foreign(image))
    var validatedImages = 0
    val normalized = OneKeySVG.normalize(source) { validatedImages++ }.toString(Charsets.UTF_8)
    assertFalse(normalized.contains("foreignObject"))
    assertTrue(normalized.contains("xlink:href=\"data:image/png;base64,$png\""))
    assertTrue(normalized.contains("preserveAspectRatio=\"none\""))
    assertTrue(validatedImages == 1)
  }

  @Test
  fun ordinarySVGKeepsItsExistingDecoderInput() {
    val source = document("<rect width=\"24\" height=\"24\" fill=\"red\"/>")
    assertArrayEquals(source, OneKeySVG.normalize(source) { throw AssertionError("Ordinary SVG must not decode embedded PNGs") })
  }

  @Test
  fun embeddedRasterDecodeFailureIsNotAConvertedSuccess() {
    assertThrows(IOException::class.java) {
      OneKeySVG.normalize(document(foreign(image))) { throw IOException("Corrupt PNG pixels") }
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
    )) assertThrows(IOException::class.java) { OneKeySVG.normalize(document(body)) {} }
  }

  @Test
  fun boundsAndEntitiesFailBeforeNativeRendering() {
    val nested = "<g>".repeat(64) + foreign(image) + "</g>".repeat(64)
    assertThrows(IOException::class.java) { OneKeySVG.normalize(document(nested)) {} }
    assertThrows(IOException::class.java) { OneKeySVG.normalize(document(foreign(image).repeat(17))) {} }
    val entity = "<!DOCTYPE svg [<!ENTITY image 'test'>]>".toByteArray() + document(foreign(image))
    assertThrows(IOException::class.java) { OneKeySVG.normalize(entity) {} }
  }
}
