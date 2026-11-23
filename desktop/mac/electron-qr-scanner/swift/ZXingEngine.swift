import CoreGraphics
import Foundation

/// ZXing-C++ fallback (built by the Rust `zxing-cpp` crate). Renders the image
/// into the tightly packed RGBA buffer the reader expects.
final class ZXingQrEngine: QrImageEngine {
  let name = "zxing"

  func decode(_ image: CGImage) -> String? {
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
    guard drawn else { return nil }
    var text: UnsafeMutablePointer<CChar>?
    let found = pixels.withUnsafeBufferPointer { buffer in
      oqs_zxing_decode(buffer.baseAddress, Int32(width), Int32(height), &text)
    }
    guard found == 1, let text else { return nil }
    defer { oqs_zxing_free(text) }
    return String(cString: text)
  }
}
