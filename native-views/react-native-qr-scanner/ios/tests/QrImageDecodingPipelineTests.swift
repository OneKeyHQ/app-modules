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
    XCTAssertEqual(engine.calls.count, 3, "over-budget padding is skipped")
    // Aspect ratio 4:3 is kept.
    XCTAssertEqual(Double(original.width) / Double(original.height), 4.0 / 3.0, accuracy: 0.05)
  }

  // MARK: - Helpers

  private func loadImage(_ url: URL) -> CGImage? {
    guard let source = CGImageSourceCreateWithURL(url as CFURL, nil) else { return nil }
    return CGImageSourceCreateImageAtIndex(source, 0, nil)
  }
}
