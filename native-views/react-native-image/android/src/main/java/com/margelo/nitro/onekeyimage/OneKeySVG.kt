package com.margelo.nitro.onekeyimage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.BitmapFactory
import com.caverock.androidsvg.SVG
import java.io.InputStream
import java.io.IOException
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.SAXParserFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.SAXException
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

/** Synchronous per-request document; loaders retain scheduling and cancellation. */
object OneKeySVG {
  fun decode(source: InputStream): OneKeySVGDocument = OneKeySVGDocument(parse(source))
  internal fun parse(source: InputStream): SVG {
    val data = OneKeyLimitedInputStream(source).readBytes()
    val normalized = normalize(data) { png ->
      val decoded = BitmapFactory.decodeByteArray(png, 0, png.size)
        ?: throw IOException("Invalid PNG in SVG foreignObject")
      decoded.recycle()
    }
    return SVG.getFromInputStream(normalized.inputStream())
  }

  @OptIn(ExperimentalEncodingApi::class)
  internal fun normalize(data: ByteArray, validatePNG: (ByteArray) -> Unit): ByteArray {
    val xml = when {
      data.size >= 2 && ((data[0] == 0xff.toByte() && data[1] == 0xfe.toByte()) ||
        (data[0] == 0xfe.toByte() && data[1] == 0xff.toByte())) -> String(data, Charsets.UTF_16)
      else -> String(data, Charsets.UTF_8)
    }
    if (!xml.contains("foreignObject")) return data
    fun unsupported(): Nothing = throw IOException("Unsupported SVG foreignObject")
    if (data.size > 8 * 1024 * 1024 || xml.contains("<!DOCTYPE", ignoreCase = true) ||
      xml.contains("<!ENTITY", ignoreCase = true)) unsupported()
    var elementCount = 0
    var elementDepth = 0
    SAXParserFactory.newInstance().newSAXParser().parse(data.inputStream(), object : DefaultHandler() {
      override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
        if (++elementCount > 10000 || ++elementDepth > 64) unsupported()
      }
      override fun endElement(uri: String?, localName: String?, qName: String?) { elementDepth-- }
    })
    val factory = DocumentBuilderFactory.newInstance().apply {
      isNamespaceAware = true
      isExpandEntityReferences = false
    }
    val builder = factory.newDocumentBuilder()
    builder.setEntityResolver { _, _ -> throw SAXException("External SVG entities are disabled") }
    val document = builder.parse(data.inputStream())
    if (document.documentElement.localName != "svg") unsupported()
    var nodes = 0
    var objects = 0
    var imageBytes = 0L
    var imagePixels = 0L
    val allowed = setOf("x", "y", "width", "height", "transform", "opacity", "clip-path", "mask", "id")
    val imageAllowed = setOf("xmlns", "src", "width", "height", "alt")
    fun children(node: Node) = (0 until node.childNodes.length).map { node.childNodes.item(it) }
    fun validateStyle(element: Element) {
      val css = children(element).joinToString("") {
        if (it.nodeType != Node.TEXT_NODE && it.nodeType != Node.CDATA_SECTION_NODE) unsupported()
        it.nodeValue
      }
      val body = Regex("^\\s*img\\s*\\{([^{}]*)\\}\\s*$").matchEntire(css)?.groupValues?.get(1)
        ?: unsupported()
      for (declaration in body.split(';').map(String::trim).filter(String::isNotEmpty)) {
        val parts = declaration.split(':').map(String::trim)
        if (parts.size != 2) unsupported()
        val (key, value) = parts
        if (key in setOf("width", "height") && value == "100%") continue
        if (key == "image-rendering" && value in setOf("optimizeSpeed", "-moz-crisp-edges",
          "-o-crisp-edges", "-webkit-optimize-contrast", "optimize-contrast", "crisp-edges", "pixelated")) continue
        if (key == "-ms-interpolation-mode" && value == "nearest-neighbor") continue
        unsupported()
      }
    }
    fun convert(node: Node, depth: Int) {
      if (depth > 64) unsupported()
      if (node.nodeType == Node.PROCESSING_INSTRUCTION_NODE) unsupported()
      if (node !is Element) return
      if (++nodes > 10000) unsupported()
      for (key in listOf("fill", "stroke", "stop-color", "flood-color", "lighting-color", "color")) {
        val value = node.getAttribute(key)
        if (!Regex("#[0-9a-fA-F]{8}").matches(value)) continue
        val rgba = value.substring(1).toLong(16)
        val alpha = (rgba and 255) / 255.0
        node.setAttribute(key, "rgba(${(rgba shr 24) and 255},${(rgba shr 16) and 255},${(rgba shr 8) and 255},$alpha)")
      }
      if (node.localName == "style") { validateStyle(node); return }
      if (node.localName != "foreignObject") {
        children(node).forEach { convert(it, depth + 1) }
        return
      }
      if (++objects > 16) unsupported()
      for (index in 0 until node.attributes.length) {
        if (node.attributes.item(index).nodeName !in allowed) unsupported()
      }
      for (key in listOf("width", "height")) {
        val number = node.getAttribute(key).toDoubleOrNull() ?: unsupported()
        if (!number.isFinite() || number <= 0 || number > 32768) unsupported()
      }
      val content = children(node).filter {
        when (it.nodeType) {
          Node.COMMENT_NODE -> false
          Node.TEXT_NODE, Node.CDATA_SECTION_NODE -> {
            if (it.nodeValue.isNotBlank()) unsupported()
            false
          }
          else -> true
        }
      }
      val image = content.singleOrNull() as? Element ?: unsupported()
      if (image.tagName != "img" || image.namespaceURI != "http://www.w3.org/1999/xhtml") unsupported()
      for (index in 0 until image.attributes.length) {
        if (image.attributes.item(index).nodeName !in imageAllowed) unsupported()
      }
      for (key in listOf("width", "height")) {
        if (image.hasAttribute(key) && image.getAttribute(key) != "100%") unsupported()
      }
      if (children(image).any { it.nodeType != Node.TEXT_NODE || it.nodeValue.isNotBlank() }) unsupported()
      val source = image.getAttribute("src")
      val prefix = "data:image/png;base64,"
      if (!source.startsWith(prefix, ignoreCase = true) || source.length - prefix.length > 11184812) unsupported()
      val png = try { Base64.Default.decode(source.substring(prefix.length)) }
      catch (_: IllegalArgumentException) { unsupported() }
      imageBytes += png.size
      if (imageBytes > 8 * 1024 * 1024 || png.size < 24 ||
        !png.copyOfRange(0, 8).contentEquals(byteArrayOf(137.toByte(), 80, 78, 71, 13, 10, 26, 10))) unsupported()
      val metadata = OneKeyEncodedImageInspector.inspect(png.inputStream())
      val width = metadata.width.toLong()
      val height = metadata.height.toLong()
      imagePixels += width * height
      if (metadata.animated || width <= 0 || height <= 0 || width > 8192 || height > 8192 ||
        imagePixels > 4194304) unsupported()
      validatePNG(png)
      val replacement = document.createElementNS("http://www.w3.org/2000/svg", "image")
      for (index in 0 until node.attributes.length) {
        val attribute = node.attributes.item(index)
        replacement.setAttribute(attribute.nodeName, attribute.nodeValue)
      }
      replacement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xlink", "http://www.w3.org/1999/xlink")
      replacement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", prefix + source.substring(prefix.length))
      replacement.setAttribute("preserveAspectRatio", "none")
      replacement.setAttribute("image-rendering", "optimizeSpeed")
      node.parentNode.replaceChild(replacement, node)
    }
    children(document).forEach { convert(it, 1) }
    val output = StringWriter()
    TransformerFactory.newInstance().newTransformer().apply {
      setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
    }.transform(DOMSource(document), StreamResult(output))
    return output.toString().toByteArray(Charsets.UTF_8)
  }
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
