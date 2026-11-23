import Foundation
import NitroModules

final class ZXingQrEngine: QrImageEngine {
  let name = "zxing"
  private let reader = QrZXingReader()

  func decode(_ image: CGImage) -> String? {
    reader.decode(image)
  }
}

class HybridQrImageDecoder: HybridQrImageDecoderSpec {
  /// Hard ceiling on the caller's `maxPixels`.
  private static let maxPixelsCeiling = 50_000_000

  // One decode at a time: each one holds several full-size bitmaps.
  private static let queue = DispatchQueue(
    label: "so.onekey.qrscanner.image-decoder", qos: .userInitiated)
  private static let pipeline = QrImageDecodingPipeline(engines: [
    VisionQrEngine(), ZXingQrEngine(),
  ])

  func decodeImage(uri: String, maxPixels: Double) throws -> Promise<QrImageDecodeResult> {
    guard let url = Self.localFileURL(uri) else {
      throw RuntimeError.error(withMessage: "invalid-image: not a local file URI")
    }
    guard maxPixels.isFinite, maxPixels >= 1 else {
      throw RuntimeError.error(withMessage: "invalid-image: maxPixels must be at least 1")
    }
    let pixelBudget = min(Int(maxPixels), Self.maxPixelsCeiling)
    let promise = Promise<QrImageDecodeResult>()
    Self.queue.async {
      do {
        let hit = try Self.pipeline.decode(url: url, maxPixels: pixelBudget)
        promise.resolve(
          withResult: QrImageDecodeResult(
            text: hit?.text,
            engine: hit.flatMap { QrDecodeEngine(fromString: $0.engine) },
            pass: hit.flatMap { QrDecodePass(fromString: $0.pass) }))
      } catch let error as QrImageDecodeError {
        promise.reject(withError: RuntimeError.error(withMessage: error.codedMessage))
      } catch {
        promise.reject(
          withError: RuntimeError.error(withMessage: "invalid-image: \(error.localizedDescription)"))
      }
    }
    return promise
  }

  private static func localFileURL(_ uri: String) -> URL? {
    if uri.hasPrefix("/") {
      return URL(fileURLWithPath: uri)
    }
    guard let url = URL(string: uri), url.isFileURL else { return nil }
    return url
  }
}
