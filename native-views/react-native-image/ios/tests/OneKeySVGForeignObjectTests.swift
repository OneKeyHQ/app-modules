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
  }
}
