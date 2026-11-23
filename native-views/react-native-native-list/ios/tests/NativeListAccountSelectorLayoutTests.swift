import UIKit
import XCTest
@testable import NativeListModule

@MainActor
final class NativeListAccountSelectorLayoutTests: XCTestCase {
  private func child<T>(_ cell: NativeListIdentityCell, _ label: String, as type: T.Type) throws -> T {
    try XCTUnwrap(Mirror(reflecting: cell).children.first { $0.label == label }?.value as? T)
  }

  private func bind(
    _ cell: NativeListIdentityCell, title: String = "A", balance: String = "$16.30",
    trailing: [[String: Any]] = []
  ) throws {
    let item = try NativeListItem(data: [
      "key": "account", "type": "identity", "presentation": "accountSelector",
      "title": title, "height": 60,
      "leading": ["kind": "account", "fallbackText": "A"],
      "subtitleSegments": [
        ["text": balance, "tone": "secondary"],
        ["text": "0x1234...5678", "tone": "secondary", "separatorBefore": true],
      ],
      "trailing": trailing,
    ])
    cell.bind(item: item, theme: nil, layout: "linear", selected: false,
      checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
    cell.setNeedsLayout()
    cell.layoutIfNeeded()
  }

  private func assertSubtitleFits(
    _ cell: NativeListIdentityCell, file: StaticString = #filePath, line: UInt = #line
  ) throws {
    let labels = try child(cell, "semanticSubtitleLabels", as: [UILabel].self)
    XCTAssertEqual(labels.count, 2, file: file, line: line)
    for label in labels {
      XCTAssertGreaterThanOrEqual(
        label.bounds.width + 0.5, label.intrinsicContentSize.width,
        "The balance and address must fit when the row has spare width", file: file, line: line)
    }
  }

  func testEmptyTrailingDoesNotCollapseBalanceToShortTitleWidth() throws {
    for width: CGFloat in [320, 390] {
      let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: width, height: 60))
      let window = UIWindow(frame: CGRect(x: 0, y: 0, width: width, height: 200))
      window.addSubview(cell)
      for title in ["A", "返佣5", "Pendle account"] {
        for balance in ["$16.30", "$0", "****"] {
          try bind(cell, title: title, balance: balance)
          window.layoutIfNeeded()
          try assertSubtitleFits(cell)
        }
      }
    }
  }

  func testSpinnerPreservesItsIntrinsicWidthAndSubtitleSpace() throws {
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 320, height: 60))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
    window.addSubview(cell)
    try bind(cell, trailing: [["kind": "spinner"]])
    window.layoutIfNeeded()

    try assertSubtitleFits(cell)
    let trailing = try child(cell, "trailingStack", as: NativeListAccessoryStack.self)
    let spinner = try XCTUnwrap(trailing.arrangedSubviews.compactMap { $0 as? UIActivityIndicatorView }.first)
    XCTAssertTrue(spinner.isAnimating)
    XCTAssertFalse(spinner.isHidden)
    XCTAssertGreaterThanOrEqual(spinner.bounds.width + 0.5, spinner.intrinsicContentSize.width)
    XCTAssertGreaterThanOrEqual(trailing.bounds.width + 0.5, spinner.bounds.width)
  }

  func testRebindingBetweenIconEmptyAndSpinnerKeepsControlAndSubtitleWidths() throws {
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 320, height: 60))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
    window.addSubview(cell)
    for icon in ["DotHorOutline", "PlusSmallOutline"] {
      let descriptor: [String: Any] = ["kind": "icon", "name": icon, "actionKey": "action"]
      let states: [[[String: Any]]] = [[descriptor], [], [["kind": "spinner"]], [descriptor], []]
      for accessories in states {
        try bind(cell, trailing: accessories)
        window.layoutIfNeeded()
        try assertSubtitleFits(cell)
        let trailing = try child(cell, "trailingStack", as: NativeListAccessoryStack.self)
        if accessories.first?["kind"] as? String == "icon" {
          let control = try XCTUnwrap(trailing.firstVisibleControl)
          XCTAssertGreaterThan(control.bounds.width, 0)
          XCTAssertEqual(trailing.bounds.width, control.bounds.width, accuracy: 0.5)
        } else {
          XCTAssertNil(trailing.firstVisibleControl)
        }
      }
    }
  }
}
