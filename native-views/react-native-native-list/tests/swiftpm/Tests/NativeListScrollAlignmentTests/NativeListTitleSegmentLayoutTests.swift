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

  // Runs replace `title` except where `title` is highlighted; wallet sidebar
  // rows keep the plain title without runs or a leading glyph.
  func testTitleDecorationsFollowTheRowContract() {
    XCTAssertTrue(nativeListShowsTitleRuns(presentation: "", runCount: 2, matchCount: 0))
    XCTAssertTrue(nativeListShowsTitleRuns(presentation: "accountSelector", runCount: 2, matchCount: 0))
    XCTAssertFalse(nativeListShowsTitleRuns(presentation: "", runCount: 0, matchCount: 0))
    XCTAssertFalse(nativeListShowsTitleRuns(presentation: "", runCount: 2, matchCount: 1))
    XCTAssertFalse(nativeListShowsTitleRuns(presentation: "walletSidebar", runCount: 2, matchCount: 0))
    XCTAssertTrue(nativeListAllowsTitleDecorations(presentation: ""))
    XCTAssertFalse(nativeListAllowsTitleDecorations(presentation: "walletSidebar"))
  }

  // Two runs that do not shrink and one line that holds only the first: the
  // first is drawn in full, so it has to carry the ellipsis.
  func testAWholeRunCutFromTheEndPutsTheEllipsisOnTheLastRunShown() {
    let natural: [CGFloat] = [100, 100]
    let widths = nativeListTitleSegmentWidths(
      natural: natural, shrink: [false, false], maxWidthRatio: [nil, nil], available: 100)
    XCTAssertEqual(widths, [100, 0])
    XCTAssertEqual(
      nativeListTitleSegmentOverflowIndex(natural: natural, shrink: [false, false], widths: widths), 0)
  }

  // The shrinking run may give up its whole width: the runs that stay whole
  // fit, so none of them is cut for an ellipsis.
  func testNoEllipsisIsAddedWhenOnlyTheShrinkingRunDisappears() {
    let natural: [CGFloat] = [100, 60]
    let shrink = [false, true]
    let widths = nativeListTitleSegmentWidths(
      natural: natural, shrink: shrink, maxWidthRatio: [nil, nil], available: 100)
    XCTAssertEqual(widths, [100, 0])
    XCTAssertNil(nativeListTitleSegmentOverflowIndex(natural: natural, shrink: shrink, widths: widths))
    // A fixed run cut behind the shrinking one still counts.
    XCTAssertEqual(
      nativeListTitleSegmentOverflowIndex(
        natural: [100, 50, 100], shrink: [false, true, false], widths: [100, 0, 0]), 0)
  }

  func testNoEllipsisIsAddedWhenTheLineFitsOrIsCutInsideARun() {
    let fixed = [false, false, false]
    XCTAssertNil(
      nativeListTitleSegmentOverflowIndex(natural: [80, 20, 90], shrink: fixed, widths: [80, 20, 90]))
    // Cut inside a run: that run truncates by itself.
    XCTAssertNil(
      nativeListTitleSegmentOverflowIndex(natural: [100, 100], shrink: fixed, widths: [100, 40]))
    XCTAssertNil(
      nativeListTitleSegmentOverflowIndex(natural: [100, 100, 100], shrink: fixed, widths: [100, 40, 0]))
    XCTAssertNil(
      nativeListTitleSegmentOverflowIndex(natural: [100, 100], shrink: fixed, widths: [0, 0]))
  }

  func testTheLeadingIconTakesItsWidthOnlyWhereItShows() {
    XCTAssertEqual(nativeListTitleLeadingIconWidth(presentation: "", hasIcon: true), 22)
    XCTAssertEqual(nativeListTitleLeadingIconWidth(presentation: "accountSelector", hasIcon: true), 22)
    XCTAssertEqual(nativeListTitleLeadingIconWidth(presentation: "", hasIcon: false), 0)
    XCTAssertEqual(nativeListTitleLeadingIconWidth(presentation: "walletSidebar", hasIcon: true), 0)
  }

  func testARunFoldsLineBreaksIntoSpaces() {
    XCTAssertEqual(nativeListTitleRunText("Main\nWallet"), "Main Wallet")
    XCTAssertEqual(nativeListTitleRunText("a\r\nb\rc"), "a b c")
    XCTAssertEqual(nativeListTitleRunText("Account #1"), "Account #1")
  }
}
