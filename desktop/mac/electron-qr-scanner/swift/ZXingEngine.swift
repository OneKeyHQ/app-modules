import CoreGraphics
import Foundation

/// ZXing-C++ fallback (built by the Rust `zxing-cpp` crate). Renders the image
/// into the tightly packed RGBA buffer the reader expects.
final class ZXingQrEngine: QrImageEngine {
  let name = "zxing"

  private struct BarcodeText: Decodable {
    let text: String?
    let humanReadable: String?

    enum CodingKeys: String, CodingKey {
      case text = "Text"
      case humanReadable = "HRI"
    }
  }

  private struct DecodedEntry: Decodable {
    let meta: BarcodeText
    let cx: Double
    let cy: Double
    let x: Double
    let y: Double
    let w: Double
    let h: Double
  }

  private func renderRgba(_ image: CGImage) -> [UInt8]? {
    let width = image.width
    let height = image.height
    guard width > 0, height > 0 else { return nil }
    var pixels = [UInt8](repeating: 0, count: width * height * 4)
    let drawn = pixels.withUnsafeMutableBytes { buffer -> Bool in
      guard
        let context = CGContext(
          data: buffer.baseAddress, width: width, height: height, bitsPerComponent: 8,
          bytesPerRow: width * 4, space: CGColorSpaceCreateDeviceRGB(),
          bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue)
      else { return false }
      context.draw(image, in: CGRect(x: 0, y: 0, width: width, height: height))
      return true
    }
    return drawn ? pixels : nil
  }

  private func extractText(_ decoded: BarcodeText) -> String? {
    if let raw = decoded.text, raw.contains("\0") { return raw }
    return decoded.humanReadable ?? decoded.text
  }

  func decode(_ image: CGImage) throws -> String? {
    guard let pixels = renderRgba(image) else { return nil }
    var text: UnsafeMutablePointer<UInt8>?
    var length = 0
    let found = pixels.withUnsafeBufferPointer { buffer in
      oqs_zxing_decode(
        buffer.baseAddress, Int32(image.width), Int32(image.height), &text, &length)
    }
    guard found == 1, let text else { return nil }
    defer { oqs_zxing_free(text, length) }
    let bytes = Data(bytesNoCopy: text, count: length, deallocator: .none)
    let decoded = try JSONDecoder().decode(BarcodeText.self, from: bytes)
    return extractText(decoded)
  }

  func decodeSymbols(_ image: CGImage) throws -> [QrPipelineSymbol] {
    guard let pixels = renderRgba(image) else { return [] }
    var jsonPtr: UnsafeMutablePointer<UInt8>?
    var length = 0
    let found = pixels.withUnsafeBufferPointer { buffer in
      oqs_zxing_decode_symbols(
        buffer.baseAddress, Int32(image.width), Int32(image.height), &jsonPtr, &length)
    }
    guard found == 1, let jsonPtr else { return [] }
    defer { oqs_zxing_free(jsonPtr, length) }
    let bytes = Data(bytesNoCopy: jsonPtr, count: length, deallocator: .none)
    let entries = try JSONDecoder().decode([DecodedEntry].self, from: bytes)
    return entries.compactMap { entry in
      guard let value = extractText(entry.meta), !value.isEmpty else { return nil }
      return QrPipelineSymbol(
        text: value,
        centerX: entry.cx,
        centerY: entry.cy,
        boundsX: entry.x,
        boundsY: entry.y,
        boundsWidth: entry.w,
        boundsHeight: entry.h)
    }
  }
}
