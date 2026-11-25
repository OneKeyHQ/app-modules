import AVFoundation
import CoreGraphics
import XCTest

@testable import QrScanner

/// Records every image it sees and answers from a closure, so pass order and
/// downscaling can be checked without depending on a real detector.
private final class RecordingEngine: QrImageEngine {
  let name: String
  private(set) var calls: [(width: Int, height: Int)] = []
  private let answer: (Int) -> String?

  init(name: String, answer: @escaping (_ callIndex: Int) -> String?) {
    self.name = name
    self.answer = answer
  }

  func decode(_ image: CGImage) -> String? {
    calls.append((image.width, image.height))
    return answer(calls.count - 1)
  }
}

final class QrImageDecodingPipelineTests: XCTestCase {
  private static let fixtureBundle = Bundle(for: QrImageDecodingPipelineTests.self)

  private func fixture(_ name: String) throws -> URL {
    let parts = name.split(separator: ".", maxSplits: 1).map(String.init)
    return try XCTUnwrap(
      Self.fixtureBundle.url(forResource: parts[0], withExtension: parts[1]),
      "missing fixture \(name)")
  }

  /// The real, expired BTCPay demo invoice from OK-67798: lower-case
  /// `lightning:` prefix with an upper-case BOLT11 invoice (461 characters).
  private func lightningPayload() throws -> String {
    try String(contentsOf: fixture("lightning-payload.txt"), encoding: .utf8)
  }

  private func makePipeline() -> QrImageDecodingPipeline {
    QrImageDecodingPipeline(engines: [VisionQrEngine(), ZXingQrEngine()])
  }

  // MARK: - Real engines

  func testDecodesRealisticLightningFixtures() throws {
    let payload = try lightningPayload()
    XCTAssertEqual(payload.count, 461)
    let pipeline = makePipeline()
    for name in [
      "lightning-clean.png",
      // BTCPay's own rendering: no QR margin, page padding, center icon, dark page.
      "lightning-btcpay-dark.png",
      "lightning-inverted.png",
      "lightning-exif-rotated.jpg",
    ] {
      let hit = try pipeline.decode(url: fixture(name), maxPixels: 12_000_000)
      XCTAssertEqual(hit?.text, payload, name)
    }
  }

  /// Stress cases that macOS Vision decodes but the iOS 27.2 simulator does
  /// not, through every pass and both engines. Kept as known gaps so an
  /// improvement shows up as an unexpected pass rather than going unnoticed.
  func testStressFixturesAreKnownGaps() throws {
    let payload = try lightningPayload()
    let pipeline = makePipeline()
    for name in [
      // Code drawn edge to edge on a dark page: no quiet zone anywhere.
      "lightning-no-margin.png",
      // Center icon about 37% of the code width, past error-correction reach.
      "lightning-large-icon.png",
    ] {
      let options = XCTExpectedFailure.Options()
      options.isStrict = false
      XCTExpectFailure("known gap: \(name)", options: options) {
        let hit = try? pipeline.decode(url: fixture(name), maxPixels: 12_000_000)
        XCTAssertEqual(hit?.text, payload, name)
      }
    }
  }

  func testImageWithoutCodeReturnsNil() throws {
    XCTAssertNil(try makePipeline().decode(url: fixture("no-qr.png"), maxPixels: 12_000_000))
  }

  func testCorruptAndMissingFilesAreInvalidImage() throws {
    for url in [try fixture("corrupt.png"), URL(fileURLWithPath: "/nonexistent/qr.png")] {
      XCTAssertThrowsError(try makePipeline().decode(url: url, maxPixels: 12_000_000)) { error in
        guard case QrImageDecodeError.invalidImage = error else {
          return XCTFail("expected invalidImage for \(url.lastPathComponent), got \(error)")
        }
      }
    }
  }

  func testZXingIsLinkedAndReadsCleanAndInvertedCodes() throws {
    let payload = try lightningPayload()
    let zxing = ZXingQrEngine()
    let clean = try XCTUnwrap(loadImage(fixture("lightning-clean.png")))
    let inverted = try XCTUnwrap(loadImage(fixture("lightning-inverted.png")))

    XCTAssertEqual(zxing.decode(clean), payload)
    // zxing-cpp 2.2.0 reads light-on-dark QR codes even with tryInvert off.
    XCTAssertEqual(zxing.decode(inverted), payload)
  }

  func testZXingOnlyPipelineDecodesOnFirstPass() throws {
    let pipeline = QrImageDecodingPipeline(engines: [ZXingQrEngine()])
    let hit = try pipeline.decode(url: fixture("lightning-btcpay-dark.png"), maxPixels: 12_000_000)
    XCTAssertEqual(hit?.text, try lightningPayload())
    XCTAssertEqual(hit?.engine, "zxing")
    XCTAssertEqual(hit?.pass, "original")
  }

  // MARK: - 4-scenario multi-symbol & animated UR coverage (a, b, c, d)

  func testCaseASingleStaticImageSymbols() throws {
    let expected = "ethereum:0x71C7656EC7ab88b098defB751B7401B5f6d8976F"
    let url = try fixture("a-single-static.png")
    let hit = try makePipeline().decodeSymbols(url: url, maxPixels: 12_000_000)
    XCTAssertEqual(hit.symbols.count, 1)
    XCTAssertEqual(hit.symbols.first?.text, expected)
    XCTAssertEqual(hit.symbols.first?.centerX ?? -1, 0.5, accuracy: 0.03)
    XCTAssertEqual(hit.symbols.first?.centerY ?? -1, 0.5, accuracy: 0.03)
  }

  func testCaseBMultiStaticImageSymbols() throws {
    let expected: [(text: String, x: Double, y: Double)] = [
      (
        "wc:8a3f9c2d1b0e4a5f6c7d8e9f0a1b2c3d@2?relay-protocol=irn&symKey=abcdef123456",
        (540.0 + 180.0) / 960.0,
        (60.0 + 180.0) / 960.0
      ),
      (
        "onekey://market/detail?symbol=ETH",
        (90.0 + 130.0) / 960.0,
        (350.0 + 130.0) / 960.0
      ),
      (
        "ethereum:0x71C7656EC7ab88b098defB751B7401B5f6d8976F",
        (580.0 + 140.0) / 960.0,
        (610.0 + 140.0) / 960.0
      ),
    ]
    let url = try fixture("b-multi-static.png")
    let hit = try makePipeline().decodeSymbols(url: url, maxPixels: 12_000_000)
    XCTAssertEqual(hit.symbols.count, 3)
    for target in expected {
      let match = try XCTUnwrap(
        hit.symbols.first { $0.text == target.text },
        "missing \(target.text)")
      XCTAssertEqual(match.centerX, target.x, accuracy: 0.03, "X for \(target.text)")
      XCTAssertEqual(match.centerY, target.y, accuracy: 0.03, "Y for \(target.text)")
    }
  }

  private func decodeFrameSymbolsWithFallback(_ frame: CGImage) -> [QrPipelineSymbol] {
    if let symbols = try? VisionQrEngine().decodeSymbols(frame), !symbols.isEmpty {
      return symbols
    }
    return ZXingQrEngine().decodeSymbols(frame)
  }

  private func readVideoFrames(_ url: URL) throws -> [CGImage] {
    let asset = AVURLAsset(url: url)
    guard let track = asset.tracks(withMediaType: .video).first else {
      throw NSError(domain: "QrScannerTests", code: 1)
    }
    let reader = try AVAssetReader(asset: asset)
    let output = AVAssetReaderTrackOutput(
      track: track,
      outputSettings: [kCVPixelBufferPixelFormatTypeKey as String: kCVPixelFormatType_32BGRA])
    reader.add(output)
    XCTAssertTrue(reader.startReading())
    let ciContext = CIContext(options: [.cacheIntermediates: false])
    var frames: [CGImage] = []
    while let sample = output.copyNextSampleBuffer(),
      let buffer = CMSampleBufferGetImageBuffer(sample)
    {
      let ciImage = CIImage(cvPixelBuffer: buffer)
      if let cg = ciContext.createCGImage(ciImage, from: ciImage.extent) {
        frames.append(cg)
      }
    }
    return frames
  }

  func testCaseCAnimatedUrVideoFrames() throws {
    let frames = try readVideoFrames(fixture("c-animated-ur.mp4"))
    XCTAssertGreaterThanOrEqual(frames.count, 17)
    var urParts = Set<String>()
    for frame in frames {
      let symbols = decodeFrameSymbolsWithFallback(frame)
      XCTAssertEqual(symbols.count, 1)
      let text = try XCTUnwrap(symbols.first?.text)
      XCTAssertTrue(text.uppercased().hasPrefix("UR:CRYPTO-PSBT/"), text)
      urParts.insert(text)
    }
    XCTAssertGreaterThanOrEqual(urParts.count, 17)
  }

  func testCaseDAnimatedUrPlusMultiStaticVideoFrames() throws {
    let frames = try readVideoFrames(fixture("d-animated-ur-plus-multi.mp4"))
    XCTAssertGreaterThanOrEqual(frames.count, 17)

    // First frame must detect all 3 codes simultaneously (1 UR + 2 static) with accurate centers.
    let firstSymbols = decodeFrameSymbolsWithFallback(frames[0])
    XCTAssertEqual(firstSymbols.count, 3)
    let firstUr = try XCTUnwrap(firstSymbols.first { $0.text.uppercased().hasPrefix("UR:CRYPTO-PSBT/") })
    let firstEth = try XCTUnwrap(firstSymbols.first {
      $0.text == "ethereum:0x71C7656EC7ab88b098defB751B7401B5f6d8976F"
    })
    let firstMarket = try XCTUnwrap(firstSymbols.first {
      $0.text == "onekey://market/detail?symbol=ETH"
    })
    XCTAssertEqual(firstUr.centerX, (40.0 + 230.0) / 960.0, accuracy: 0.03)
    XCTAssertEqual(firstUr.centerY, (250.0 + 230.0) / 960.0, accuracy: 0.03)
    XCTAssertEqual(firstEth.centerX, (580.0 + 150.0) / 960.0, accuracy: 0.03)
    XCTAssertEqual(firstEth.centerY, (100.0 + 150.0) / 960.0, accuracy: 0.03)
    XCTAssertEqual(firstMarket.centerX, (580.0 + 150.0) / 960.0, accuracy: 0.03)
    XCTAssertEqual(firstMarket.centerY, (560.0 + 150.0) / 960.0, accuracy: 0.03)

    // Across the video, multi-symbol extraction collects >= 17 distinct UR parts despite competing static QRs.
    var urParts = Set<String>()
    for frame in frames {
      let symbols = decodeFrameSymbolsWithFallback(frame)
      if let ur = symbols.first(where: { $0.text.uppercased().hasPrefix("UR:CRYPTO-PSBT/") }) {
        urParts.insert(ur.text)
      }
    }
    XCTAssertGreaterThanOrEqual(urParts.count, 17)
  }

  // MARK: - Pass order and limits (fake engines)

  func testPassesRunInOrderAndStopAtFirstHit() throws {
    // Hit on the third image the engine sees: original, padded, grayscale.
    let engine = RecordingEngine(name: "zxing") { $0 == 2 ? "found" : nil }
    let hit = try QrImageDecodingPipeline(engines: [engine])
      .decode(url: fixture("no-qr.png"), maxPixels: 12_000_000)

    XCTAssertEqual(hit, QrImageHit(text: "found", engine: "zxing", pass: "grayscale"))
    XCTAssertEqual(engine.calls.count, 3, "the inverted pass must not run after a hit")
    // The padded image adds 10% of the longer side (400 px) on every edge.
    XCTAssertEqual(engine.calls[0].width, 400)
    XCTAssertEqual(engine.calls[1].width, 480)
    XCTAssertEqual(engine.calls[1].height, 380)
  }

  func testPlatformEngineRunsBeforeFallbackWithinEachPass() throws {
    var order: [String] = []
    let first = RecordingEngine(name: "vision") { _ in order.append("vision"); return nil }
    let second = RecordingEngine(name: "zxing") { _ in order.append("zxing"); return nil }
    XCTAssertNil(
      try QrImageDecodingPipeline(engines: [first, second])
        .decode(url: fixture("no-qr.png"), maxPixels: 12_000_000))
    XCTAssertEqual(order, Array(repeating: ["vision", "zxing"], count: 4).flatMap { $0 })
  }

  func testImagesAreDownscaledToPixelBudget() throws {
    let engine = RecordingEngine(name: "vision") { _ in nil }
    _ = try QrImageDecodingPipeline(engines: [engine])
      .decode(url: fixture("no-qr.png"), maxPixels: 10_000)
    let original = try XCTUnwrap(engine.calls.first)
    XCTAssertTrue(engine.calls.allSatisfy { $0.width * $0.height <= 10_000 })
    XCTAssertEqual(engine.calls.count, 4, "padding is scaled to retain the quiet-zone pass")
    // Aspect ratio 4:3 is kept.
    XCTAssertEqual(Double(original.width) / Double(original.height), 4.0 / 3.0, accuracy: 0.05)
  }

  // MARK: - Helpers

  private func loadImage(_ url: URL) -> CGImage? {
    guard let source = CGImageSourceCreateWithURL(url as CFURL, nil) else { return nil }
    return CGImageSourceCreateImageAtIndex(source, 0, nil)
  }
}
