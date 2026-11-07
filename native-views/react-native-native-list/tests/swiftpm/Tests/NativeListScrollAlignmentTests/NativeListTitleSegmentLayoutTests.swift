import Foundation
import XCTest
@testable import NativeListScrollAlignment

final class NativeListTitleSegmentLayoutTests: XCTestCase {
  // Wallet / Account as recipient rows send it: the wallet shrinks, the
  // account is capped at 70% of the line.
  private func walletFirst(_ wallet: CGFloat, _ account: CGFloat, line: CGFloat) -> [CGFloat] {
    nativeListTitleSegmentWidths(
      natural: [wallet, 20, account], shrink: [true, false, false],
      maxWidthRatio: [nil, nil, 0.7], available: line)
  }

  func testKeepsNaturalWidthsWhenTheyFit() {
    XCTAssertEqual(walletFirst(80, 90, line: 300), [80, 20, 90])
  }

  func testTheShrinkingRunGivesWayFirst() {
    XCTAssertEqual(walletFirst(200, 100, line: 250), [130, 20, 100])
  }

  func testACappedRunStopsAtItsShareOfTheLine() {
    XCTAssertEqual(walletFirst(100, 400, line: 300), [70, 20, 210])
  }

  func testTheAccountShrinksWhenTheWalletMustStayWhole() {
    let widths = nativeListTitleSegmentWidths(
      natural: [300, 20, 150], shrink: [false, false, true],
      maxWidthRatio: [0.7, nil, nil], available: 300)
    XCTAssertEqual(widths, [210, 20, 70])
  }

  func testRunsThatDoNotShrinkAreCutFromTheEnd() {
    let widths = nativeListTitleSegmentWidths(
      natural: [100, 100, 100], shrink: [false, false, true],
      maxWidthRatio: [nil, nil, nil], available: 150)
    XCTAssertEqual(widths, [100, 50, 0])
    XCTAssertEqual(
      nativeListTitleSegmentWidths(
        natural: [100, 100], shrink: [false, false], maxWidthRatio: [nil, nil], available: 120),
      [100, 20])
  }

  func testANonPositiveLineLeavesNothing() {
    XCTAssertEqual(walletFirst(80, 90, line: 0), [0, 0, 0])
    XCTAssertEqual(walletFirst(80, 90, line: -10), [0, 0, 0])
  }
}
