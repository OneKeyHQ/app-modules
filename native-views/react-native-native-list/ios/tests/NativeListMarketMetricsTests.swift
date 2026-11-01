import UIKit
import XCTest
@testable import NativeListModule

@MainActor
final class NativeListMarketMetricsTests: XCTestCase {
  private func item(_ metrics: [[String: Any]]? = nil, price: String = "$42") throws -> NativeListItem {
    var data: [String: Any] = [
      "key": "market-eth", "type": "market", "variant": "token", "height": 72,
      "leading": ["kind": "token", "fallbackIcon": ["name": "CryptoCoinOutline"]],
      "leadingAction": ["kind": "icon", "name": "StarOutline", "actionKey": "favorite"],
      "title": "ETH", "subtitle": "Volume", "price": price,
      "change": ["text": "+2%", "tone": "positive"],
      "badges": [["key": "community", "text": "Verified", "actionKey": "info"]],
      "pressActionKey": "detail", "pressInActionKey": "prewarm",
      "longPressActionKey": "menu",
      "style": ["horizontalPadding": 20, "leadingGap": 14, "trailingGap": 8],
    ]
    if let metrics { data["metrics"] = metrics }
    return try NativeListItem(data: data)
  }

  private var two: [[String: Any]] { [
    ["key": "price", "text": "$42", "alignment": "end"],
    ["key": "change", "text": "+2%", "tone": "positive", "alignment": "end"],
  ] }

  func testMetricCountAndCompactReuseClearInactiveViews() throws {
    let cell = NativeListMarketCell(frame: CGRect(x: 0, y: 0, width: 800, height: 72))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 800, height: 200))
    window.addSubview(cell)
    let bind: (NativeListItem) -> Void = { item in
      cell.bind(item: item, theme: nil, layout: "linear", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
      cell.layoutIfNeeded()
    }

    bind(try item(two))
    XCTAssertEqual(cell.root.arrangedSubviews.count, 5)
    let identity = try XCTUnwrap(cell.root.arrangedSubviews[2] as? UIStackView)
    let firstMetric = try XCTUnwrap(cell.root.arrangedSubviews[3] as? UIStackView)
    XCTAssertEqual(identity.bounds.width, firstMetric.bounds.width * 2, accuracy: 1)
    XCTAssertEqual(firstMetric.frame.minX - identity.frame.maxX, 8, accuracy: 1)
    let metricStack = try XCTUnwrap(cell.root.arrangedSubviews.last as? UIStackView)
    let metricLabel = try XCTUnwrap(metricStack.arrangedSubviews.first as? UILabel)
    XCTAssertEqual(metricLabel.text, "+2%")

    bind(try item(two + [["key": "volume", "text": "$1B", "secondaryText": "24h"]]))
    XCTAssertEqual(cell.root.arrangedSubviews.count, 6)
    bind(try item())
    XCTAssertEqual(cell.root.arrangedSubviews.count, 4)
    XCTAssertFalse(cell.root.arrangedSubviews.contains(metricStack))
    bind(try item(two))
    XCTAssertEqual(cell.root.arrangedSubviews.count, 5)
    XCTAssertEqual(metricLabel.text, "+2%")
  }

  func testQuoteUpdatesKeepMetricViewsAndActionTargetsStable() throws {
    let cell = NativeListMarketCell(frame: CGRect(x: 0, y: 0, width: 800, height: 72))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 800, height: 200))
    window.addSubview(cell)
    cell.bind(item: try item(two), theme: nil, layout: "linear", selected: false,
      checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
    cell.layoutIfNeeded()
    let metricStack = try XCTUnwrap(cell.root.arrangedSubviews[3] as? UIStackView)
    let metricLabel = try XCTUnwrap(metricStack.arrangedSubviews.first as? UILabel)
    let leadingAction = try XCTUnwrap(cell.root.arrangedSubviews.first as? UIButton)
    var actions: [String] = []
    cell.onAction = { _, key, _, _ in actions.append(key) }

    for tick in 0..<50 {
      var updated = two
      updated[0]["text"] = "$\(tick)"
      cell.updateMarketQuote(try item(updated, price: "$\(tick)"), theme: nil)
      XCTAssertEqual(cell.root.arrangedSubviews.count, 5)
      XCTAssertTrue(cell.root.arrangedSubviews[3] === metricStack)
      XCTAssertTrue(cell.root.arrangedSubviews.first === leadingAction)
    }
    XCTAssertEqual((metricStack.arrangedSubviews.first as? UILabel)?.text, "$49")
    let changeStack = try XCTUnwrap(cell.root.arrangedSubviews.last as? UIStackView)
    XCTAssertEqual((changeStack.arrangedSubviews.first as? UILabel)?.text, "+2%")
    XCTAssertTrue(leadingAction.allTargets.contains(cell))
    let action = try XCTUnwrap(
      leadingAction.actions(forTarget: cell, forControlEvent: .touchUpInside)?.first)
    let selector = NSSelectorFromString(action)
    XCTAssertTrue(cell.responds(to: selector))
    _ = cell.perform(selector)
    XCTAssertEqual(actions, ["favorite"])
    XCTAssertEqual(metricLabel.text, "$49")
  }
}
