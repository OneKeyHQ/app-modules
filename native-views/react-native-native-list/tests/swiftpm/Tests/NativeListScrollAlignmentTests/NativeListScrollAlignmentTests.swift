import Foundation
import XCTest
@testable import NativeListScrollAlignment

final class NativeListScrollAlignmentTests: XCTestCase {
  private func offset(
    start: CGFloat = 2328,
    inset: CGFloat = 8,
    viewport: CGFloat = 400,
    length: CGFloat = 870,
    position: CGFloat = 0.5,
    offset: CGFloat = 0,
    nearest: Bool = false
  ) -> CGFloat {
    nativeListAlignedScrollOffset(
      itemStart: start, startInset: inset, viewportLength: viewport,
      itemLength: length, viewPosition: position, viewOffset: offset, nearest: nearest
    )
  }

  func testActualUIKitMeasuredOversizedGroupForParentMiddleAndLast() {
    // Full iOS application measurements, including attributes for an unmounted group.
    let offsets: [CGFloat] = [840, 40, -760]
    let expected: [CGFloat] = [1697.166667, 2497.166667, 3297.166667]
    for (viewOffset, target) in zip(offsets, expected) {
      XCTAssertEqual(offset(start: 2008, inset: 0, viewport: 2075 / 3,
                            length: 1750, offset: viewOffset), target, accuracy: 0.001)
    }
  }

  func testOversizedExplicitStartCenterEndAndFractionalPositions() {
    XCTAssertEqual(offset(position: 0), 2320)
    XCTAssertEqual(offset(), 2555)
    XCTAssertEqual(offset(position: 1), 2790)
    XCTAssertEqual(offset(position: 0.25), 2437.5)
  }

  func testSignedOffsetsPreserveTheirDirection() {
    XCTAssertEqual(offset(offset: 320), 2235)
    XCTAssertEqual(offset(offset: -320), 2875)
  }

  func testNearestPreservesLegacyOversizedAlignment() {
    XCTAssertEqual(offset(position: 0, nearest: true), 2320)
    XCTAssertEqual(offset(position: 1, nearest: true), 2320)
    XCTAssertEqual(offset(offset: -320, nearest: true), 2640)
  }

  func testShortAndEqualRowsKeepExistingAlignment() {
    XCTAssertEqual(offset(start: 2008, length: 68, position: 0), 2000)
    XCTAssertEqual(offset(start: 2008, length: 68), 1834)
    XCTAssertEqual(offset(start: 2008, length: 68, position: 1), 1668)
    XCTAssertEqual(offset(start: 2008, length: 400, offset: 20), 1980)
    XCTAssertEqual(offset(start: 2008, length: 68, nearest: true), 1834)
  }

  func testFinalClampUsesFullViewportAndBothContentInsets() {
    func clamp(_ requested: CGFloat) -> CGFloat {
      nativeListClampedScrollOffset(requested, viewportLength: 400,
                                   contentLength: 4000, startInset: 8, endInset: 12)
    }
    XCTAssertEqual(clamp(-100), -8)
    XCTAssertEqual(clamp(4500), 3612)
    XCTAssertEqual(clamp(2555), 2555)
  }

  func testShortAndEmptyContentRetainTheMinimumOffset() {
    for contentLength: CGFloat in [0, 68, 380] {
      XCTAssertEqual(nativeListClampedScrollOffset(100, viewportLength: 400,
                                                 contentLength: contentLength,
                                                 startInset: 8, endInset: 12), -8)
    }
  }
}
