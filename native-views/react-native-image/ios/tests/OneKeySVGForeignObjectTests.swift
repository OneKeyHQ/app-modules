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
