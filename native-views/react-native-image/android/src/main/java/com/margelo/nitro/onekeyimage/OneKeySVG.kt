package com.margelo.nitro.onekeyimage

import android.graphics.Bitmap
import android.graphics.Canvas
import com.caverock.androidsvg.SVG
import java.io.InputStream

/** Synchronous per-request document; loaders retain scheduling and cancellation. */
object OneKeySVG {
  fun decode(source: InputStream): OneKeySVGDocument = OneKeySVGDocument(parse(source))
  internal fun parse(source: InputStream): SVG = SVG.getFromInputStream(source)
}

class OneKeySVGDocument internal constructor(private val svg: SVG) {
  val viewBoxWidth: Float? = svg.documentViewBox?.width()
  val viewBoxHeight: Float? = svg.documentViewBox?.height()
  val intrinsicWidth: Float = svg.documentWidth
  val intrinsicHeight: Float = svg.documentHeight

  fun render(width: Int, height: Int): Bitmap {
    require(width > 0 && height > 0)
    if (svg.documentViewBox == null && intrinsicWidth > 0 && intrinsicHeight > 0) {
      svg.setDocumentViewBox(0f, 0f, intrinsicWidth, intrinsicHeight)
    }
    svg.setDocumentWidth(width.toFloat())
    svg.setDocumentHeight(height.toFloat())
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    svg.renderToCanvas(Canvas(bitmap))
    return bitmap
  }
}
