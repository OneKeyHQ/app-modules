import NitroModules
import UIKit
import XCTest

@testable import PerpDepthBar

@MainActor
final class HybridPerpDepthBarsTests: XCTestCase {
  func testOffMainUpdatesWaitForMainAndSnapshotBufferBytes() throws {
    let depth = makeDepthView()
    depth.percents = [10]
    depth.prices = ["old price"]
    depth.sizes = ["old size"]
    depth.view.layoutIfNeeded()

    let buffer = makeBuffer([25, 75])
    runOffMain {
      try! depth.setDepth(buffer: buffer)
      try! depth.setText(prices: ["25", "75"], sizes: ["2.5"])
    }

    // The worker returned while the main queue was blocked by runOffMain.
    XCTAssertEqual(depth.percents, [10])
    XCTAssertEqual(depth.prices, ["old price"])
    XCTAssertEqual(depth.sizes, ["old size"])

    write([90, 90], into: buffer)
    drainMainQueue()
    XCTAssertEqual(depth.percents, [25, 75])
    XCTAssertEqual(depth.prices, ["25", "75"])
    XCTAssertEqual(depth.sizes, ["2.5"])

    depth.view.layoutIfNeeded()
    let bars = barLayers(in: depth)
    XCTAssertEqual(bars.count, 2)
    XCTAssertEqual(Double(bars[0].transform.m11), 0.25, accuracy: 0.001)
    XCTAssertEqual(Double(bars[1].transform.m11), 0.75, accuracy: 0.001)
    XCTAssertEqual(textStrings(in: depth), ["25", "2.5", "75", ""])
  }

  func testOffMainEmptyDepthClearsOnlyAfterMainPublication() throws {
    let depth = makeDepthView()
    depth.percents = [60]
    depth.prices = ["60"]
    depth.sizes = ["6"]
    depth.placeholderText = "--"
    depth.placeholderRows = 2
    depth.view.layoutIfNeeded()

    let emptyBuffer = makeBuffer([])
    runOffMain {
      try! depth.setDepth(buffer: emptyBuffer)
      try! depth.setText(prices: [], sizes: [])
    }
    XCTAssertEqual(depth.percents, [60])
    XCTAssertEqual(depth.prices, ["60"])
    XCTAssertEqual(depth.sizes, ["6"])

    drainMainQueue()
    depth.view.layoutIfNeeded()
    XCTAssertTrue(depth.percents.isEmpty)
    XCTAssertTrue(depth.prices.isEmpty)
    XCTAssertTrue(depth.sizes.isEmpty)
    XCTAssertEqual(barLayers(in: depth).count, 2)
    XCTAssertEqual(textStrings(in: depth), ["--", "--", "--", "--"])
    XCTAssertTrue(barLayers(in: depth).allSatisfy { $0.transform.m11 == 0 })
  }

  func testRapidGrowAndShrinkPublishInOrderWithRealLayers() throws {
    let depth = makeDepthView()
    depth.percents = [5]
    depth.prices = ["initial"]
    depth.sizes = ["initial"]

    let grow = makeBuffer([10, 20, 30])
    let shrink = makeBuffer([40])
    let growAgain = makeBuffer([70, 80])
    runOffMain {
      try! depth.setDepth(buffer: grow)
      try! depth.setText(prices: ["a", "b", "c"], sizes: ["1", "2", "3"])
      try! depth.setDepth(buffer: shrink)
      try! depth.setText(prices: ["d"], sizes: ["4"])
      try! depth.setDepth(buffer: growAgain)
      try! depth.setText(prices: ["e", "f"], sizes: ["7", "8"])
    }
    XCTAssertEqual(depth.percents, [5])
    XCTAssertEqual(depth.prices, ["initial"])
    XCTAssertEqual(depth.sizes, ["initial"])

    drainMainQueue()
    depth.view.layoutIfNeeded()
    XCTAssertEqual(depth.percents, [70, 80])
    XCTAssertEqual(depth.prices, ["e", "f"])
    XCTAssertEqual(depth.sizes, ["7", "8"])
    XCTAssertEqual(barLayers(in: depth).count, 2)
    XCTAssertEqual(textStrings(in: depth), ["e", "7", "f", "8"])
  }

  func testMainThreadUpdatesRemainSynchronous() throws {
    let depth = makeDepthView()
    try depth.setDepth(buffer: makeBuffer([30, 50]))
    try depth.setText(prices: ["p1", "p2"], sizes: ["s1", "s2"])

    XCTAssertEqual(depth.percents, [30, 50])
    XCTAssertEqual(depth.prices, ["p1", "p2"])
    XCTAssertEqual(depth.sizes, ["s1", "s2"])
    depth.view.layoutIfNeeded()
    XCTAssertEqual(barLayers(in: depth).count, 2)
    XCTAssertEqual(textStrings(in: depth), ["p1", "s1", "p2", "s2"])
  }

  func testImperativeDepthIgnoresLaterPropsWithoutTakingTextOwnership() throws {
    let depth = makeDepthView()
    depth.percents = [10]
    depth.percents = [20]
    XCTAssertEqual(depth.percents, [20])

    try depth.setDepth(buffer: makeBuffer([35]))
    depth.percents = []
    depth.percents = [99, 99]
    depth.prices = ["prop price"]
    depth.sizes = ["prop size"]
    depth.view.layoutIfNeeded()

    XCTAssertEqual(depth.percents, [35])
    XCTAssertEqual(barLayers(in: depth).count, 1)
    XCTAssertEqual(Double(barLayers(in: depth)[0].transform.m11), 0.35, accuracy: 0.001)
    XCTAssertEqual(textStrings(in: depth), ["prop price", "prop size"])

    try depth.setDepth(buffer: makeBuffer([]))
    depth.percents = [99]
    depth.view.layoutIfNeeded()
    XCTAssertTrue(depth.percents.isEmpty)
    XCTAssertTrue(barLayers(in: depth).isEmpty)
  }

  func testImperativeTextIgnoresLaterPropsWithoutTakingDepthOwnership() throws {
    let depth = makeDepthView()
    depth.prices = ["initial"]
    depth.sizes = ["initial"]
    depth.prices = ["updated"]
    depth.sizes = ["updated"]
    XCTAssertEqual(depth.prices, ["updated"])
    XCTAssertEqual(depth.sizes, ["updated"])

    runOffMain {
      try! depth.setText(prices: ["imperative price"], sizes: ["imperative size"])
    }
    drainMainQueue()
    depth.prices = []
    depth.sizes = []
    depth.prices = ["stale price"]
    depth.sizes = ["stale size"]
    depth.percents = [40]
    depth.view.layoutIfNeeded()

    XCTAssertEqual(depth.percents, [40])
    XCTAssertEqual(textStrings(in: depth), ["imperative price", "imperative size"])

    try depth.setText(prices: [], sizes: [])
    depth.prices = ["stale"]
    depth.sizes = ["stale"]
    depth.view.layoutIfNeeded()
    XCTAssertTrue(depth.prices.isEmpty)
    XCTAssertTrue(depth.sizes.isEmpty)
    XCTAssertEqual(textStrings(in: depth), ["", ""])
  }

  func testOffMainDepthSurvivesLaterFabricPropWrites() {
    let depth = makeDepthView()
    let buffer = makeBuffer([25, 75])
    runOffMain {
      try! depth.setDepth(buffer: buffer)
    }
    drainMainQueue()
    depth.percents = []
    depth.view.layoutIfNeeded()
    XCTAssertEqual(depth.percents, [25, 75])
    XCTAssertEqual(barLayers(in: depth).count, 2)
  }

  func testRatioPairPublishesTogetherOnMainWithRealLayers() throws {
    let ratio = HybridPerpSideRatio()
    ratio.view.frame = CGRect(x: 0, y: 0, width: 202, height: 20)
    ratio.reducedMotion = true
    ratio.view.layoutIfNeeded()

    runOffMain {
      try! ratio.setRatio(bidPercentage: 20, askPercentage: 80)
      try! ratio.setRatio(bidPercentage: 70, askPercentage: 30)
    }
    XCTAssertEqual(ratio.bidPercentage, 50)
    XCTAssertEqual(ratio.askPercentage, 50)
    drainMainQueue()
    ratio.view.layoutIfNeeded()
    XCTAssertEqual(ratio.bidPercentage, 70)
    XCTAssertEqual(ratio.askPercentage, 30)
    let layers = try XCTUnwrap(ratio.view.layer.sublayers)
    XCTAssertEqual(layers[0].frame.width, 140, accuracy: 0.001)
    XCTAssertEqual(layers[1].frame.width, 60, accuracy: 0.001)

    try ratio.setRatio(bidPercentage: 30, askPercentage: 70)
    XCTAssertEqual(ratio.bidPercentage, 30)
    XCTAssertEqual(ratio.askPercentage, 70)
  }

  func testQueuedRatioUpdateDoesNotRetainDisposedView() {
    var ratio: HybridPerpSideRatio? = HybridPerpSideRatio()
    weak var weakRatio = ratio
    runOffMain {
      try! ratio?.setRatio(bidPercentage: 20, askPercentage: 80)
    }
    ratio = nil
    XCTAssertNil(weakRatio)
    drainMainQueue()
    XCTAssertNil(weakRatio)
  }

  func testQueuedUpdateDoesNotRetainDisposedView() {
    final class OwnerBox {
      var owner: HybridPerpDepthBars?

      init(_ owner: HybridPerpDepthBars) {
        self.owner = owner
      }
    }

    let box = OwnerBox(makeDepthView())
    weak var weakOwner: HybridPerpDepthBars?
    weakOwner = box.owner
    let buffer = makeBuffer([35])
    runOffMain {
      let owner = box.owner
      try! owner?.setDepth(buffer: buffer)
      try! owner?.setText(prices: ["35"], sizes: ["3.5"])
    }

    box.owner = nil
    XCTAssertNil(weakOwner)
    drainMainQueue()
    XCTAssertNil(weakOwner)
  }

  private func makeDepthView() -> HybridPerpDepthBars {
    let depth = HybridPerpDepthBars()
    depth.view.frame = CGRect(x: 0, y: 0, width: 240, height: 160)
    depth.rowHeight = 28
    depth.rowMarginTop = 4
    depth.reducedMotion = true
    return depth
  }

  private func makeBuffer(_ values: [Float]) -> ArrayBuffer {
    let buffer = ArrayBuffer.allocate(size: values.count * MemoryLayout<UInt32>.size)
    write(values, into: buffer)
    return buffer
  }

  private func write(_ values: [Float], into buffer: ArrayBuffer) {
    precondition(buffer.size == values.count * MemoryLayout<UInt32>.size)
    for (index, value) in values.enumerated() {
      var bits = value.bitPattern.littleEndian
      withUnsafeBytes(of: &bits) { bytes in
        for byte in 0..<MemoryLayout<UInt32>.size {
          buffer.data[index * MemoryLayout<UInt32>.size + byte] = bytes[byte]
        }
      }
    }
  }

  private func runOffMain(_ update: @escaping () -> Void) {
    let finished = DispatchSemaphore(value: 0)
    DispatchQueue.global(qos: .userInitiated).async {
      update()
      finished.signal()
    }
    XCTAssertEqual(finished.wait(timeout: .now() + 2), .success)
  }

  private func drainMainQueue() {
    let published = expectation(description: "main-queue depth publication")
    DispatchQueue.main.async { published.fulfill() }
    wait(for: [published], timeout: 2)
  }

  private func barLayers(in depth: HybridPerpDepthBars) -> [CALayer] {
    (depth.view.layer.sublayers ?? []).filter { !($0 is CATextLayer) }
  }

  private func textStrings(in depth: HybridPerpDepthBars) -> [String] {
    (depth.view.layer.sublayers ?? [])
      .compactMap { $0 as? CATextLayer }
      .compactMap { $0.string as? String }
  }
}
