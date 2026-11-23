import XCTest

@testable import QrScanner

final class QrFrameTextGateTests: XCTestCase {
  func testNothingIsReportedWhileStopped() {
    let gate = QrFrameTextGate()
    gate.nextGeneration()
    XCTAssertEqual(gate.filter(["a"], now: 0), [])
  }

  func testRepeatsAreThrottledButNewTextPassesImmediately() {
    let gate = QrFrameTextGate(repeatInterval: 1.0)
    let generation = gate.nextGeneration()
    gate.setCapturing(true)

    XCTAssertEqual(gate.filter(["a"], now: 0).map(\.text), ["a"])
    XCTAssertEqual(gate.filter(["a"], now: 0.5).map(\.text), [])
    XCTAssertEqual(gate.filter(["b"], now: 0.6).map(\.text), ["b"])
    XCTAssertEqual(gate.filter(["b"], now: 1.7).map(\.text), ["b"])
    XCTAssertEqual(gate.filter(["c"], now: 1.8).first?.generation, generation)
  }

  func testStopAndRestartStartNewGenerationAndClearRepeatState() {
    let gate = QrFrameTextGate(repeatInterval: 10)
    let first = gate.nextGeneration()
    gate.setCapturing(true)
    XCTAssertEqual(gate.filter(["a"], now: 0).count, 1)

    // stop(): new generation, capture off.
    gate.nextGeneration()
    gate.setCapturing(false)
    XCTAssertEqual(gate.filter(["late"], now: 0.1), [])

    // start(): another generation; the same code is reported again at once.
    let restarted = gate.nextGeneration()
    gate.setCapturing(true)
    XCTAssertGreaterThan(restarted, first)
    XCTAssertEqual(gate.filter(["a"], now: 0.2), [.init(text: "a", generation: restarted)])
  }

  /// An animated code shows a new part every ~100 ms and loops. Every distinct
  /// part must reach JS on first sight, with nothing dropped by the throttle.
  func testAnimatedUrPartsAtTenFramesPerSecondAllPass() {
    let gate = QrFrameTextGate(repeatInterval: 1.0)
    gate.nextGeneration()
    gate.setCapturing(true)
    let parts = (1...12).map { "ur:crypto-psbt/\($0)-12/payload\($0)" }
    var reported: [String] = []
    var now: TimeInterval = 0
    // Two loops; the camera sees each part on ~3 consecutive frames (30 fps).
    for _ in 0..<2 {
      for part in parts {
        for _ in 0..<3 {
          reported += gate.filter([part], now: now).map(\.text)
          now += 1.0 / 30
        }
      }
    }
    XCTAssertEqual(Array(reported.prefix(12)), parts)
    // The second loop arrives > 1 s later, so each part is reported once more.
    XCTAssertEqual(reported.count, 24)
  }

  func testEmptyTextIsIgnored() {
    let gate = QrFrameTextGate()
    gate.nextGeneration()
    gate.setCapturing(true)
    XCTAssertEqual(gate.filter(["", "x"], now: 0).map(\.text), ["x"])
  }

  func testConcurrentStartStopAndDetectionsStayConsistent() {
    let gate = QrFrameTextGate(repeatInterval: 0)
    gate.nextGeneration()
    gate.setCapturing(true)
    DispatchQueue.concurrentPerform(iterations: 2_000) { index in
      if index % 50 == 0 {
        gate.nextGeneration()
        gate.setCapturing(index % 100 == 0)
      } else {
        for report in gate.filter(["t\(index)"], now: Double(index)) {
          XCTAssertLessThanOrEqual(report.generation, gate.currentGeneration)
        }
      }
    }
  }
}
