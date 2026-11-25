import CoreGraphics
import CoreImage
import Foundation
import ImageIO
import Vision

// Kept free of UIKit and NitroModules so it compiles and runs on macOS for
// decoding checks against fixture images.

enum QrImageDecodeError: Error {
  case invalidImage(String)
  case resourceLimit(String)
  case decoderUnavailable

  var codedMessage: String {
    switch self {
    case .invalidImage(let reason): return "invalid-image: \(reason)"
    case .resourceLimit(let reason): return "resource-limit: \(reason)"
    case .decoderUnavailable: return "decoder-unavailable: A QR decoder failed"
    }
  }
}

struct QrImageHit: Equatable {
  let text: String
  /// `vision`, `mlkit` or `zxing`.
  let engine: String
  /// `original`, `padded`, `grayscale` or `inverted`.
  let pass: String
}

struct QrPipelineSymbol: Equatable {
  let text: String
  let centerX: Double
  let centerY: Double
  let boundsX: Double?
  let boundsY: Double?
  let boundsWidth: Double?
  let boundsHeight: Double?
}

struct QrImageSymbolsHit: Equatable {
  let width: Int
  let height: Int
  let symbols: [QrPipelineSymbol]
  let engine: String?
  let pass: String?
}

protocol QrImageEngine {
  var name: String { get }
  func decode(_ image: CGImage) throws -> String?
  func decodeSymbols(_ image: CGImage) throws -> [QrPipelineSymbol]
}

extension QrImageEngine {
  func decodeSymbols(_ image: CGImage) throws -> [QrPipelineSymbol] {
    guard let text = try decode(image), !text.isEmpty else { return [] }
    return [
      QrPipelineSymbol(
        text: text,
        centerX: 0.5,
        centerY: 0.5,
        boundsX: nil,
        boundsY: nil,
        boundsWidth: nil,
        boundsHeight: nil)
    ]
  }
}

final class VisionQrEngine: QrImageEngine {
  let name = "vision"

  func decode(_ image: CGImage) throws -> String? {
    let request = VNDetectBarcodesRequest()
    request.symbologies = [.qr]
    let handler = VNImageRequestHandler(cgImage: image, options: [:])
    try handler.perform([request])
    return request.results?
      .lazy
      .compactMap { $0.payloadStringValue }
      .first { !$0.isEmpty }
  }

  func decodeSymbols(_ image: CGImage) throws -> [QrPipelineSymbol] {
    let request = VNDetectBarcodesRequest()
    request.symbologies = [.qr]
    let handler = VNImageRequestHandler(cgImage: image, options: [:])
    try handler.perform([request])
    return (request.results ?? []).compactMap { observation in
      guard let text = observation.payloadStringValue, !text.isEmpty else { return nil }
      // Vision boundingBox uses a bottom-left origin; flip Y to top-left [0, 1].
      let box = observation.boundingBox
      return QrPipelineSymbol(
        text: text,
        centerX: Double(box.midX),
        centerY: Double(1.0 - box.midY),
        boundsX: Double(box.minX),
        boundsY: Double(1.0 - box.maxY),
        boundsWidth: Double(box.width),
        boundsHeight: Double(box.height))
    }
  }
}

/// Decodes one local image with several engines over several image variants,
/// returning the first hit. Callers serialize calls; instances hold no
/// per-call state.
final class QrImageDecodingPipeline {
  /// Sources above this pixel count are refused before decoding.
  static let maxSourcePixels = 200_000_000
  /// White border added by the padded pass, as a share of the longer side.
  static let paddingRatio = 0.1

  private let engines: [QrImageEngine]
  private let ciContext = CIContext(options: [.cacheIntermediates: false])

  init(engines: [QrImageEngine]) {
    self.engines = engines
  }

  func decode(url: URL, maxPixels: Int) throws -> QrImageHit? {
    let image = try loadOriented(url: url, maxPixels: maxPixels)
    let passes: [(String, () -> CGImage?)] = [
      ("original", { image }),
      ("padded", { self.padded(image, maxPixels: maxPixels) }),
      ("grayscale", { self.grayscale(image, inverted: false) }),
      ("inverted", { self.grayscale(image, inverted: true) }),
    ]
    var attempted = false
    var decoded = false
    for (pass, makeVariant) in passes {
      let hit: QrImageHit? = autoreleasepool {
        guard let variant = makeVariant() else { return nil }
        for engine in engines {
          attempted = true
          do {
            let text = try engine.decode(variant)
            decoded = true
            if let text {
              return QrImageHit(text: text, engine: engine.name, pass: pass)
            }
          } catch {
            // A failed pass leaves the remaining engines and passes available.
          }
        }
        return nil
      }
      if let hit { return hit }
    }
    if attempted && !decoded { throw QrImageDecodeError.decoderUnavailable }
    return nil
  }

  static let maxImageSymbols = 8

  func decodeSymbols(url: URL, maxPixels: Int) throws -> QrImageSymbolsHit {
    let image = try loadOriented(url: url, maxPixels: maxPixels)
    let passes: [(String, () -> CGImage?)] = [
      ("original", { image }),
      ("padded", { self.padded(image, maxPixels: maxPixels) }),
      ("grayscale", { self.grayscale(image, inverted: false) }),
      ("inverted", { self.grayscale(image, inverted: true) }),
    ]
    var attempted = false
    var decoded = false
    var merged: [QrPipelineSymbol] = []
    var seen = Set<String>()
    var firstEngine: String?
    var firstPass: String?

    for (pass, makeVariant) in passes {
      if merged.count >= Self.maxImageSymbols { break }
      autoreleasepool {
        guard let variant = makeVariant() else { return }
        for engine in engines {
          if merged.count >= Self.maxImageSymbols { break }
          attempted = true
          do {
            let symbols = try engine.decodeSymbols(variant)
            decoded = true
            if !symbols.isEmpty {
              let mapped =
                pass == "padded"
                ? symbols.map { self.unpadSymbol($0, original: image, padded: variant) }
                : symbols
              for sym in mapped {
                if sym.text.isEmpty || seen.contains(sym.text) { continue }
                seen.insert(sym.text)
                merged.append(sym)
                if firstEngine == nil {
                  firstEngine = engine.name
                  firstPass = pass
                }
                if merged.count >= Self.maxImageSymbols { break }
              }
            }
          } catch {
            // A failed pass leaves the remaining engines and passes available.
          }
        }
      }
    }
    if !merged.isEmpty {
      return QrImageSymbolsHit(
        width: image.width,
        height: image.height,
        symbols: merged,
        engine: firstEngine,
        pass: firstPass)
    }
    if attempted && !decoded { throw QrImageDecodeError.decoderUnavailable }
    return QrImageSymbolsHit(
      width: image.width,
      height: image.height,
      symbols: [],
      engine: nil,
      pass: nil)
  }

  private func unpadSymbol(
    _ symbol: QrPipelineSymbol,
    original: CGImage,
    padded: CGImage
  ) -> QrPipelineSymbol {
    let margin = max(16, Int(Double(max(original.width, original.height)) * Self.paddingRatio))
    let expandedWidth = original.width + margin * 2
    let expandedHeight = original.height + margin * 2
    let insetX = Double(margin) * Double(padded.width) / Double(expandedWidth)
    let insetY = Double(margin) * Double(padded.height) / Double(expandedHeight)
    let innerWidth = Double(padded.width) - insetX * 2
    let innerHeight = Double(padded.height) - insetY * 2
    guard innerWidth > 0, innerHeight > 0 else { return symbol }
    let mapX: (Double) -> Double = { ($0 * Double(padded.width) - insetX) / innerWidth }
    let mapY: (Double) -> Double = { ($0 * Double(padded.height) - insetY) / innerHeight }
    return QrPipelineSymbol(
      text: symbol.text,
      centerX: mapX(symbol.centerX),
      centerY: mapY(symbol.centerY),
      boundsX: symbol.boundsX.map(mapX),
      boundsY: symbol.boundsY.map(mapY),
      boundsWidth: symbol.boundsWidth.map { ($0 * Double(padded.width)) / innerWidth },
      boundsHeight: symbol.boundsHeight.map { ($0 * Double(padded.height)) / innerHeight })
  }

  /// Applies EXIF orientation and downscales to at most `maxPixels`.
  private func loadOriented(url: URL, maxPixels: Int) throws -> CGImage {
    let sourceOptions = [kCGImageSourceShouldCache: false] as CFDictionary
    guard let source = CGImageSourceCreateWithURL(url as CFURL, sourceOptions),
      CGImageSourceGetCount(source) > 0
    else {
      throw QrImageDecodeError.invalidImage("unreadable or unsupported image")
    }
    guard
      let properties = CGImageSourceCopyPropertiesAtIndex(source, 0, nil)
        as? [CFString: Any],
      let width = properties[kCGImagePropertyPixelWidth] as? Int,
      let height = properties[kCGImagePropertyPixelHeight] as? Int,
      width > 0, height > 0
    else {
      throw QrImageDecodeError.invalidImage("missing image dimensions")
    }
    let sourcePixels = Double(width) * Double(height)
    if sourcePixels > Double(Self.maxSourcePixels) {
      throw QrImageDecodeError.resourceLimit("image has \(width)x\(height) pixels")
    }
    let scale = min(1, (Double(maxPixels) / sourcePixels).squareRoot())
    let scaledSide = Int((Double(max(width, height)) * scale).rounded(.down))
    let maxSide = max(1, min(maxPixels, scaledSide))
    let thumbnailOptions =
      [
        kCGImageSourceCreateThumbnailFromImageAlways: true,
        kCGImageSourceCreateThumbnailWithTransform: true,
        kCGImageSourceShouldCacheImmediately: true,
        kCGImageSourceThumbnailMaxPixelSize: maxSide,
      ] as CFDictionary
    guard let image = CGImageSourceCreateThumbnailAtIndex(source, 0, thumbnailOptions)
    else {
      throw QrImageDecodeError.invalidImage("image could not be decoded")
    }
    return image
  }

  /// Adds a white quiet zone; codes rendered edge to edge (no margin) often
  /// fail to locate without one.
  private func padded(_ image: CGImage, maxPixels: Int) -> CGImage? {
    let margin = max(16, Int(Double(max(image.width, image.height)) * Self.paddingRatio))
    let expandedWidth = image.width + margin * 2
    let expandedHeight = image.height + margin * 2
    let scale = min(1, sqrt(Double(maxPixels) / (Double(expandedWidth) * Double(expandedHeight))))
    let width = max(1, Int(Double(expandedWidth) * scale))
    let height = max(1, Int(Double(expandedHeight) * scale))
    guard let context = CGContext(
        data: nil, width: width, height: height, bitsPerComponent: 8,
        bytesPerRow: 0, space: CGColorSpaceCreateDeviceRGB(),
        bitmapInfo: CGImageAlphaInfo.noneSkipLast.rawValue)
    else { return nil }
    context.setFillColor(CGColor(red: 1, green: 1, blue: 1, alpha: 1))
    context.fill(CGRect(x: 0, y: 0, width: width, height: height))
    let insetX = CGFloat(margin) * CGFloat(width) / CGFloat(expandedWidth)
    let insetY = CGFloat(margin) * CGFloat(height) / CGFloat(expandedHeight)
    context.draw(image, in: CGRect(x: 0, y: 0, width: width, height: height)
      .insetBy(dx: insetX, dy: insetY))
    return context.makeImage()
  }

  private func grayscale(_ image: CGImage, inverted: Bool) -> CGImage? {
    var output = CIImage(cgImage: image).applyingFilter(
      "CIColorControls",
      parameters: [kCIInputSaturationKey: 0, kCIInputContrastKey: 1.3])
    if inverted {
      output = output.applyingFilter("CIColorInvert")
    }
    return ciContext.createCGImage(output, from: output.extent)
  }
}
