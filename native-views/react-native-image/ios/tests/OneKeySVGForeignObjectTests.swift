import SDWebImage
import UIKit
import XCTest

@testable import OneKeyImage

final class OneKeySVGForeignObjectTests: XCTestCase {
  private func document(_ body: String) -> Data {
    Data("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\">\(body)</svg>".utf8)
  }

  private func embeddedImage() -> String {
    let format = UIGraphicsImageRendererFormat()
    format.scale = 1
    let png = UIGraphicsImageRenderer(size: CGSize(width: 2, height: 2), format: format).image { context in
      UIColor.red.setFill()
      context.fill(CGRect(x: 0, y: 0, width: 2, height: 2))
    }.pngData()!
    return "<img xmlns=\"http://www.w3.org/1999/xhtml\" src=\"data:image/png;base64,\(png.base64EncodedString())\" width=\"100%\" height=\"100%\"></img>"
  }

  private func foreign(_ content: String, attributes: String = "") -> String {
    "<foreignObject width=\"24\" height=\"24\" \(attributes)>\(content)</foreignObject>"
  }

  private func pixelImage(side: Int = 2) -> String {
    let format = UIGraphicsImageRendererFormat()
    format.scale = 1
    let png = UIGraphicsImageRenderer(size: CGSize(width: side, height: side), format: format).image { context in
      for y in 0..<side {
        for x in 0..<side {
          let color: UIColor = (x + y).isMultiple(of: 2) ? .red : .blue
          color.setFill()
          context.fill(CGRect(x: x, y: y, width: 1, height: 1))
        }
      }
    }.pngData()!
    return "<img xmlns=\"http://www.w3.org/1999/xhtml\" src=\"data:image/png;base64,\(png.base64EncodedString())\"/>"
  }

  private func pixels(_ source: Data, size: Int) throws -> [UInt8] {
    let image = try XCTUnwrap(OneKeyImageCoderBridge.decodeSVG(
      source, pixelSize: CGSize(width: size, height: size)))
    let cgImage = try XCTUnwrap(image.cgImage)
    XCTAssertEqual(cgImage.width, size)
    XCTAssertEqual(cgImage.height, size)
    var bytes = [UInt8](repeating: 0, count: size * size * 4)
    let context = try XCTUnwrap(CGContext(data: &bytes, width: size, height: size,
      bitsPerComponent: 8, bytesPerRow: size * 4, space: CGColorSpaceCreateDeviceRGB(),
      bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue | CGBitmapInfo.byteOrder32Big.rawValue))
    context.interpolationQuality = .none
    context.draw(cgImage, in: CGRect(x: 0, y: 0, width: size, height: size))
    return bytes
  }

  func testPixelatedPNGKeepsHardEdgesAtIntegerAndFractionalSizes() throws {
    // The stylesheet follows the image to exercise whole-document CSS resolution.
    let source = document(foreign(pixelImage()) + "<style>img { image-rendering:pixelated; }</style>")
    for size in [32, 37, 192, 480] {
      let bytes = try pixels(source, size: size)
      let left = size / 2 - (size.isMultiple(of: 2) ? 1 : 2)
      let right = size / 2 + (size.isMultiple(of: 2) ? 0 : 1)
      for (x, expected) in [(left, [255, 0, 0, 255]), (right, [0, 0, 255, 255])] {
        let offset = (size / 4 * size + x) * 4
        XCTAssertEqual(Array(bytes[offset..<offset + 4]), expected.map(UInt8.init), "size=\(size) x=\(x)")
      }
    }
  }

  func testPixelPathsIgnoreInheritedStrokeAndKeepSiblingStroke() throws {
    let content = foreign(pixelImage(), attributes: "x=\"4\" y=\"4\" width=\"16\" height=\"16\"")
      .replacingOccurrences(of: "width=\"24\" height=\"24\"", with: "")
    let css = "<style>img { image-rendering:pixelated; }</style>"
    let sibling = "<path d=\"M1 1H23\" fill=\"none\"/>"
    let plain = try pixels(document(content + css), size: 96)
    for inherited in [
      Data(String(decoding: document(content + sibling + css), as: UTF8.self).replacingOccurrences(of: "viewBox=", with: "stroke=\"green\" stroke-width=\"0.2\" viewBox=").utf8),
      document("<g stroke=\"green\" stroke-width=\"0.2\">" + content + sibling + "</g>" + css),
    ] {
      let output = try pixels(inherited, size: 96)
      for y in 16..<80 {
        for x in 16..<80 {
          let offset = (y * 96 + x) * 4
          XCTAssertEqual(Array(output[offset..<offset + 4]), Array(plain[offset..<offset + 4]))
        }
      }
      XCTAssertGreaterThan(output[(4 * 96 + 48) * 4 + 1], 0)
      XCTAssertEqual(plain[(4 * 96 + 48) * 4 + 3], 0)
    }
  }

  func testSurroundingSVGStylesAndImageSamplingRenderTogether() throws {
    let content = foreign(pixelImage(), attributes: "x=\"4\" y=\"4\" width=\"16\" height=\"16\"")
      .replacingOccurrences(of: "width=\"24\" height=\"24\"", with: "")
    for css in [
      "<style>rect, circle { fill:red; }</style>",
      "<style>rect, circle { fill:red; } img { image-rendering:pixelated; }</style>",
      "<style>img { image-rendering:pixelated; } rect { fill:red; }</style><style>img { image-rendering:smooth; }</style>",
    ] {
      let output = try pixels(document(css + "<rect width=\"24\" height=\"24\"/>" + content), size: 96)
      for (x, y, color) in [(8, 8, [255, 0, 0, 255]), (24, 24, [255, 0, 0, 255]), (64, 24, [0, 0, 255, 255])] {
        let offset = (y * 96 + x) * 4
        XCTAssertEqual(Array(output[offset..<offset + 4]), color.map(UInt8.init), css)
      }
      let edge = (24 * 96 + 47) * 4
      if css.contains("smooth") || !css.contains("pixelated") {
        XCTAssertTrue(output[edge] > 0 && output[edge] < 255)
      } else {
        XCTAssertEqual(Array(output[edge..<edge + 4]), [255, 0, 0, 255])
      }
    }
  }

  func testStylesThatCouldAffectConvertedContentStillFail() {
    for selector in ["*", ".art", "#art", "g", "svg", "image", "path", "img, rect", "rect img", "rect:hover"] {
      XCTAssertNil(OneKeyImageCoderBridge.decodeSVG(document("<style>\(selector) { fill:red; }</style>" + foreign(embeddedImage())), pixelSize: CGSize(width: 96, height: 96)))
    }
    for css in ["@media all { rect { fill:red; } }", "@import 'external.css'; rect { fill:red; }"] {
      XCTAssertNil(OneKeyImageCoderBridge.decodeSVG(document("<style>\(css)</style>" + foreign(embeddedImage())), pixelSize: CGSize(width: 96, height: 96)))
    }
  }

  func testSmoothDefaultAndLastStyleDeclarationAreRetained() throws {
    for css in ["", "<style>img { image-rendering:pixelated; image-rendering:auto; -ms-interpolation-mode:nearest-neighbor; }</style>",
      "<style>img { image-rendering:pixelated; }</style><style>img { image-rendering:smooth; }</style>"] {
      let bytes = try pixels(document(foreign(pixelImage()) + css), size: 32)
      let offset = (8 * 32 + 15) * 4
      XCTAssertTrue(bytes[offset] > 0 && bytes[offset] < 255)
      XCTAssertTrue(bytes[offset + 2] > 0 && bytes[offset + 2] < 255)
    }
  }

  func testPixelRunsKeepGeometryClippingOpacityAndTransparentBackground() throws {
    let foreground = foreign(embeddedImage(),
      attributes: "x=\"6\" y=\"6\" transform=\"translate(1 0)\" opacity=\"0.5\" clip-path=\"url(#clip)\"")
    let layers = "<defs><clipPath id=\"clip\"><rect width=\"12\" height=\"24\"/></clipPath></defs><rect width=\"24\" height=\"24\" fill=\"green\"/>" + foreground
    let sharp = try pixels(document(layers + "<style>img { image-rendering:pixelated; }</style>"), size: 96)
    let smooth = try pixels(document(layers), size: 96)
    XCTAssertTrue(zip(sharp, smooth).allSatisfy { abs(Int($0) - Int($1)) <= 1 })

    let format = UIGraphicsImageRendererFormat()
    format.scale = 1
    let png = UIGraphicsImageRenderer(size: CGSize(width: 2, height: 2), format: format).image { context in
      UIColor.red.withAlphaComponent(0.5).setFill()
      context.fill(CGRect(x: 1, y: 0, width: 1, height: 2))
    }.pngData()!
    let content = "<img xmlns=\"http://www.w3.org/1999/xhtml\" src=\"data:image/png;base64,\(png.base64EncodedString())\"/>"
    let output = try pixels(document("<rect width=\"24\" height=\"24\" fill=\"#00ff00\"/>" + foreign(content) + "<style>img { image-rendering:pixelated; }</style>"), size: 32)
    let clear = (8 * 32 + 8) * 4, alpha = (8 * 32 + 24) * 4
    XCTAssertEqual(Array(output[clear..<clear + 4]), [0, 255, 0, 255])
    XCTAssertTrue(abs(Int(output[alpha]) - 128) <= 1)
    XCTAssertTrue(abs(Int(output[alpha + 1]) - 127) <= 1)
    XCTAssertEqual(output[alpha + 3], 255)
    let clipped = "<defs><clipPath id=\"box\" clipPathUnits=\"objectBoundingBox\"><rect width=\"0.5\" height=\"1\"/></clipPath></defs><rect width=\"24\" height=\"24\" fill=\"#00ff00\"/>" + foreign(content, attributes: "clip-path=\"url(#box)\"")
    let clippedSharp = try pixels(document(clipped + "<style>img { image-rendering:pixelated; }</style>"), size: 32)
    let outsideClip = (8 * 32 + 20) * 4
    XCTAssertEqual(Array(clippedSharp[outsideClip..<outsideClip + 4]), [0, 255, 0, 255])
  }

  func testGeneratedPixelGeometryIsBoundedAcrossTheDocument() {
    let css = "<style>img { image-rendering:pixelated; }</style>"
    XCTAssertNil(OneKeyImageCoderBridge.decodeSVG(document(foreign(pixelImage(side: 129)) + css), pixelSize: CGSize(width: 64, height: 64)))
    let repeated = foreign(pixelImage(side: 100)) + foreign(pixelImage(side: 100))
    XCTAssertNil(OneKeyImageCoderBridge.decodeSVG(document(repeated + css), pixelSize: CGSize(width: 64, height: 64)))
  }

  func testEmbeddedPNGUsesTheRegisteredImageDecoderAndRendersPixels() throws {
    let manager = SDImageCodersManager()
    OneKeyImageCoderBridge.addCoders(to: manager)
    let source = document(foreign(embeddedImage()))
    let image = try XCTUnwrap(manager.decodedImage(with: source, options: [
      .decodeThumbnailPixelSize: NSValue(cgSize: CGSize(width: 64, height: 64)),
    ]))
    let cgImage = try XCTUnwrap(image.cgImage)
    XCTAssertEqual(cgImage.width, 64)
    XCTAssertEqual(cgImage.height, 64)
    var pixel = [UInt8](repeating: 0, count: 4)
    let context = try XCTUnwrap(CGContext(data: &pixel, width: 1, height: 1,
      bitsPerComponent: 8, bytesPerRow: 4, space: CGColorSpaceCreateDeviceRGB(),
      bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue))
    context.draw(cgImage, in: CGRect(x: 0, y: 0, width: 1, height: 1))
    XCTAssertEqual(pixel, [255, 0, 0, 255])
  }

  func testUnsupportedHTMLAndExternalImagesFailInsteadOfReturningBackground() {
    for content in [
      "<div xmlns=\"http://www.w3.org/1999/xhtml\">Unsupported</div>",
      "<img xmlns=\"http://www.w3.org/1999/xhtml\" src=\"https://example.com/image.png\"/>",
      embeddedImage() + embeddedImage(),
    ] {
      let source = document("<rect width=\"24\" height=\"24\" fill=\"black\"/>" + foreign(content))
      XCTAssertNil(OneKeyImageCoderBridge.decodeSVG(source, pixelSize: CGSize(width: 64, height: 64)))
    }
  }

  func testUTF16ForeignObjectsMatchUTF8AndRejectEntities() throws {
    for css in ["", "<style>img { image-rendering:pixelated; }</style>"] {
      let source = String(decoding: document(foreign(pixelImage()) + css), as: UTF8.self)
      let expected = try pixels(Data(source.utf8), size: 32)
      for (encoding, name, bom) in [
        (String.Encoding.utf16LittleEndian, "UTF-16LE", Data([0xff, 0xfe])),
        (String.Encoding.utf16BigEndian, "UTF-16BE", Data([0xfe, 0xff])),
      ] {
        let declaration = "<?xml version='1.0' encoding='\(name)'?>"
        for prefix in [Data(), bom] {
          let encoded = try XCTUnwrap((declaration + source).data(using: encoding))
          XCTAssertEqual(try pixels(prefix + encoded, size: 32), expected)
          let entity = try XCTUnwrap((declaration + "<!DOCTYPE svg [<!ENTITY image 'test'>]>" + source).data(using: encoding))
          XCTAssertNil(OneKeyImageCoderBridge.decodeSVG(prefix + entity, pixelSize: CGSize(width: 32, height: 32)))
        }
      }
    }
  }

  func testDeeplyNestedLargeSmoothPNGMatchesShallowRendering() throws {
    let side = 512
    var bytes = [UInt8](repeating: 255, count: side * side * 4)
    var seed: UInt64 = 51059
    for index in bytes.indices where index % 4 != 3 {
      seed ^= seed << 13
      seed ^= seed >> 7
      seed ^= seed << 17
      bytes[index] = UInt8(truncatingIfNeeded: seed >> 24)
    }
    let provider = try XCTUnwrap(CGDataProvider(data: Data(bytes) as CFData))
    let image = try XCTUnwrap(CGImage(width: side, height: side, bitsPerComponent: 8,
      bitsPerPixel: 32, bytesPerRow: side * 4, space: CGColorSpaceCreateDeviceRGB(),
      bitmapInfo: CGBitmapInfo(rawValue: CGImageAlphaInfo.noneSkipLast.rawValue),
      provider: provider, decode: nil, shouldInterpolate: true, intent: .defaultIntent))
    let png = try XCTUnwrap(UIImage(cgImage: image).pngData())
    XCTAssertGreaterThan(png.count, 512 * 1024)
    let content = foreign("<img xmlns=\"http://www.w3.org/1999/xhtml\" src=\"data:image/png;base64,\(png.base64EncodedString())\"/>")
    let expected = try pixels(document(content), size: 64)
    let nested = String(repeating: "<g>", count: 60) + content + String(repeating: "</g>", count: 60)
    XCTAssertEqual(try pixels(document(nested), size: 64), expected)
  }

  func testConvertedThumbnailsBoundOutputCanvas() throws {
    let image = try XCTUnwrap(OneKeyImageCoderBridge.decodeSVG(
      document(foreign(embeddedImage())), pixelSize: CGSize(width: 4096, height: 4096)))
    let pixels = try XCTUnwrap(image.cgImage)
    XCTAssertEqual(pixels.width, 2048)
    XCTAssertEqual(pixels.height, 2048)
  }

  func testUnsupportedLayoutStylesAndMalformedPNGFail() {
    for body in [
      foreign(embeddedImage(), attributes: "style=\"display:none\""),
      "<style>img { object-fit: cover; }</style>" + foreign(embeddedImage()),
      foreign("<img xmlns=\"http://www.w3.org/1999/xhtml\" src=\"data:image/png;base64,AAAA\"/>"),
      foreign(embeddedImage().replacingOccurrences(of: "100%", with: "50%")),
    ] {
      XCTAssertNil(OneKeyImageCoderBridge.decodeSVG(document(body), pixelSize: CGSize(width: 64, height: 64)))
    }
  }

  func testDocumentBoundsAndOrdinarySVG() {
    let nested = String(repeating: "<g>", count: 64) + foreign(embeddedImage()) + String(repeating: "</g>", count: 64)
    XCTAssertNil(OneKeyImageCoderBridge.decodeSVG(document(nested), pixelSize: .zero))
    let tooMany = String(repeating: foreign(embeddedImage()), count: 17)
    XCTAssertNil(OneKeyImageCoderBridge.decodeSVG(document(tooMany), pixelSize: .zero))
    let entity = Data("<!DOCTYPE svg [<!ENTITY image 'test'>]>".utf8) + document(foreign(embeddedImage()))
    XCTAssertNil(OneKeyImageCoderBridge.decodeSVG(entity, pixelSize: .zero))
    XCTAssertNotNil(OneKeyImageCoderBridge.decodeSVG(document("<rect width=\"24\" height=\"24\" fill=\"red\"/>"), pixelSize: CGSize(width: 64, height: 64)))
    XCTAssertNotNil(OneKeyImageCoderBridge.decodeSVG(document("<!-- foreignObject is not used --><style>rect { fill:red; }</style><rect width=\"24\" height=\"24\"/>"), pixelSize: CGSize(width: 64, height: 64)))
  }
}
