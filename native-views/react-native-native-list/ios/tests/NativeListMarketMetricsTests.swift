import UIKit
import XCTest
@testable import NativeListModule

@MainActor
final class NativeListMarketMetricsTests: XCTestCase {
  private func item(
    _ metrics: [[String: Any]]? = nil, price: String = "$42", accessibilityLabel: String? = nil
  ) throws -> NativeListItem {
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
    data["accessibilityLabel"] = accessibilityLabel
    return try NativeListItem(data: data)
  }

  private var two: [[String: Any]] { [
    ["key": "price", "text": "$42", "alignment": "end"],
    ["key": "change", "text": "+2%", "tone": "positive", "alignment": "end"],
  ] }

  func testBoundedTableRebindsWidthAndActionWithoutDispatchingRowPress() throws {
    let source = try item()
    var data = source.data
    var columns: [[String: Any]] = (0..<7).map { ["key": "c" + String($0), "text": String($0)] }
    columns[0]["width"] = 44
    columns[0]["actionKey"] = "favorite"
    columns[0]["accessibilityLabel"] = "Toggle favorite"
    data["tableColumns"] = columns
    let model = try NativeListItem(data: data)
    XCTAssertEqual(model.rendererKey, .boundedTable)
    let cell = try XCTUnwrap(NativeListRendererRegistry.create(model.rendererKey) as? NativeListBoundedTableCell)
    cell.frame = CGRect(x: 0, y: 0, width: 700, height: 56)
    cell.bind(item: model, theme: nil, layout: "linear", selected: false,
      checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
    cell.layoutIfNeeded()
    let strip = try XCTUnwrap(cell.root.arrangedSubviews.first)
    XCTAssertEqual(strip.subviews.count, 7)
    XCTAssertEqual(strip.subviews[0].bounds.width, 44, accuracy: 1)
    XCTAssertEqual(strip.subviews[1].frame.minX - strip.subviews[0].frame.maxX, 8, accuracy: 1)
    let button = try XCTUnwrap(strip.subviews[0].subviews.compactMap { $0 as? UIButton }.first)
    var actions: [String] = []
    cell.onAction = { _, key, _, _ in actions.append(key) }
    let target = try XCTUnwrap(button.allTargets.first as? NSObject)
    let selectorName = try XCTUnwrap(button.actions(forTarget: target, forControlEvent: .touchUpInside)?.first)
    let selector = NSSelectorFromString(selectorName)
    XCTAssertTrue(button.hitTest(CGPoint(x: button.bounds.midX, y: button.bounds.midY), with: nil) === button)
    _ = target.perform(selector)
    XCTAssertEqual(actions, ["favorite"])
    for disabled in [true, false] {
      data["disabled"] = disabled
      cell.bind(item: try NativeListItem(data: data), theme: nil, layout: "linear", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
      XCTAssertEqual(button.isEnabled, !disabled)
      XCTAssertEqual(button.accessibilityTraits.contains(.notEnabled), disabled)
    }
    data["tableColumns"] = Array(columns.suffix(2))
    cell.bind(item: try NativeListItem(data: data), theme: nil, layout: "linear", selected: false,
      checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
    XCTAssertEqual(strip.subviews.count, 2)
    XCTAssertTrue(button.isHidden)
    _ = target.perform(selector)
    XCTAssertEqual(actions, ["favorite"])
  }

  func testBoundedTableInlineActionsAndReuse() throws {
    let cell = NativeListBoundedTableCell(frame: CGRect(x: 0, y: 0, width: 320, height: 56))
    var data = try item().data
    let actions = [["name": "Copy3Outline", "actionKey": "copy", "accessibilityLabel": "Copy"],
                   ["name": "OpenOutline", "actionKey": "open", "accessibilityLabel": "Explorer"]]
    var received: [String] = []
    cell.onAction = { _, key, _, _ in received.append(key) }
    func bind(_ placement: String, _ actions: [[String: String]]) throws {
      data["tableColumns"] = [["key":"address", "text":"Address", "actions": actions, "actionPlacement":placement], ["key":"value","text":"42"]]
      cell.bind(item: try NativeListItem(data: data), theme: nil, layout: "linear", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
      cell.layoutIfNeeded()
    }
    for placement in ["inline", "below"] {
      try bind(placement, actions)
      let column = try XCTUnwrap(cell.root.arrangedSubviews.first?.subviews.first)
      column.layoutIfNeeded()
      let labels = try XCTUnwrap(column.subviews.compactMap { $0 as? NativeListTableColumnView }.first)
      let buttons = column.subviews.compactMap { $0 as? NativeListIconButton }
      XCTAssertEqual(buttons.compactMap { $0.accessibilityLabel }, ["Copy","Explorer"])
      let first = try XCTUnwrap(buttons.first)
      if placement == "below" { XCTAssertGreaterThan(first.frame.minY, labels.frame.maxY) }
      else { XCTAssertGreaterThan(first.frame.minX, labels.frame.maxX) }
      let second = try XCTUnwrap(buttons.last)
      let nearCopy = CGPoint(x: first.frame.maxX + 1, y: first.frame.midY)
      let nearOpen = CGPoint(x: second.frame.minX - 1, y: second.frame.midY)
      XCTAssertTrue(column.hitTest(nearCopy, with: nil) === first)
      XCTAssertTrue(column.hitTest(nearOpen, with: nil) === second)
      XCTAssertTrue(column.hitTest(CGPoint(x: first.center.x, y: first.frame.minY - 3), with: nil) === first)
      for button in buttons {
        let target = try XCTUnwrap(button.allTargets.first as? NSObject)
        let selector = try XCTUnwrap(button.actions(forTarget: target, forControlEvent: .touchUpInside)?.first)
        _ = target.perform(NSSelectorFromString(selector), with: button)
      }
    }
    XCTAssertEqual(received, ["copy","open","copy","open"])
    let column = try XCTUnwrap(cell.root.arrangedSubviews.first?.subviews.first)
    let pressed = try XCTUnwrap(column.subviews.compactMap { $0 as? NativeListIconButton }.first)
    let target = try XCTUnwrap(pressed.allTargets.first as? NSObject)
    let name = try XCTUnwrap(pressed.actions(forTarget: target, forControlEvent: .touchUpInside)?.first)
    let release = NSSelectorFromString(name)
    received.removeAll()
    data["price"] = "$43"
    try bind("below", actions)
    XCTAssertTrue(pressed.superview === column)
    _ = target.perform(release, with: pressed)
    XCTAssertEqual(received, ["copy"])
    let originalX = pressed.frame.midX
    try bind("below", Array(actions.reversed()))
    XCTAssertGreaterThan(pressed.frame.midX, originalX)
    XCTAssertTrue(column.hitTest(pressed.center, with: nil) === pressed)
    _ = target.perform(release, with: pressed)
    XCTAssertEqual(received, ["copy", "copy"])
    var replaced = actions
    replaced[0]["actionKey"] = "explorer"
    try bind("below", replaced)
    XCTAssertNil(pressed.superview)
    _ = target.perform(release, with: pressed)
    XCTAssertEqual(received, ["copy", "copy"])
    try bind("inline", [])
    XCTAssertFalse(cell.root.arrangedSubviews.first!.subviews[0].subviews.contains { $0 is NativeListIconButton })
  }

  func testDefaultTableHeightContainsBelowActionsAndWrappedLabels() throws {
    var data = try item().data
    data.removeValue(forKey: "height")
    data["style"] = ["verticalPadding": 12, "price": ["lines": 2],
                     "container": ["contentVerticalAlignment": "top"]]
    var address: [String: Any] = ["key": "address", "width": 80, "text": "One two three four five",
      "secondaryText": "Holder", "actions": [["name": "Copy3Outline", "actionKey": "copy", "accessibilityLabel": "Copy"]]]
    let view = NativeListView(frame: CGRect(x: 0, y: 0, width: 320, height: 400))
    defer { view.dispose() }
    let collection = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    data["size"] = "small"
    for (index, placement) in ["inline", "below"].enumerated() {
      address["actionPlacement"] = placement
      data["tableColumns"] = [address, ["key": "value", "text": "42"]]
      let model = try NativeListItem(data: data)
      let fonts = NativeListFontFamilies()
      let snapshot: [String: Any] = ["schemaVersion": 1, "generation": index + 1,
        "layout": ["kind": "linear"], "rows": [data]]
      view.applySnapshotJson(String(decoding: try JSONSerialization.data(withJSONObject: snapshot), as: UTF8.self))
      view.layoutIfNeeded()
      let height = view.collectionView(collection, layout: collection.collectionViewLayout,
        sizeForItemAt: IndexPath(item: 0, section: 0)).height
      if placement == "inline" {
        XCTAssertEqual(height, 48)
        continue
      }
      let cell = NativeListBoundedTableCell(frame: CGRect(x: 0, y: 0, width: 320, height: height))
      cell.bind(item: model, theme: nil, layout: "linear", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: fonts)
      cell.layoutIfNeeded()
      let column = try XCTUnwrap(cell.root.arrangedSubviews.first?.subviews.first)
      let icon = try XCTUnwrap(column.subviews.compactMap { $0 as? NativeListIconButton }.first)
      XCTAssertGreaterThan(height, 56)
      XCTAssertEqual(column.bounds.height, height - 24, accuracy: 1)
      XCTAssertTrue(column.bounds.contains(icon.frame))
      XCTAssertTrue(column.hitTest(icon.center, with: nil) === icon)
    }
  }

  func testBoundedTableAnnouncesCompleteSubscriptAmountsAndExplicitLabel() throws {
    var data = try item().data
    data["tableColumns"] = [
      [
        "key": "asset",
        "text": "fallback",
        "textSegments": [["text": "E"], ["text": "", "style": "subscript"], ["text": "TH"]],
      ],
      [
        "key": "price",
        "text": "$0.000001",
        "textSegments": [["text": "$0.0"], ["text": "5", "style": "subscript"], ["text": "1"]],
        "secondaryText": "0.000002 ETH",
        "secondaryTextSegments": [["text": "0.0"], ["text": "5", "style": "subscript"], ["text": "2 ETH"]],
      ],
      [
        "key": "change",
        "text": "24h",
        "secondaryTextSegments": [["text": "+"], ["text": "2%"], ["text": "", "style": "subscript"]],
      ],
    ]
    let cell = NativeListBoundedTableCell(frame: .zero)
    for label in [nil, "My quote"] as [String?] {
      data["accessibilityLabel"] = label
      cell.bind(item: try NativeListItem(data: data), theme: nil, layout: "linear", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
      XCTAssertEqual(cell.accessibilityLabel, label ?? "ETH, $0.000001, 0.000002 ETH, 24h, +2%")
    }
  }

  func testBoundedTextWrapsAtColumnWidthAndHonorsVerticalAlignment() throws {
    var data = try item().data
    data["tableColumns"] = [
      ["key": "price", "width": 70, "text": "One two three four five six seven"],
      ["key": "change", "text": "+2%"],
    ]
    let cell = NativeListBoundedTableCell(frame: CGRect(x: 0, y: 0, width: 320, height: 100))
    for alignment in ["top", "bottom"] {
      data["style"] = ["price": ["lines": 2], "container": ["contentVerticalAlignment": alignment]]
      cell.bind(item: try NativeListItem(data: data), theme: nil, layout: "linear", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
      cell.layoutIfNeeded()
      let column = try XCTUnwrap(cell.root.arrangedSubviews.first?.subviews.first)
      let labels = try XCTUnwrap(column.subviews.compactMap { $0 as? NativeListTableColumnView }.first)
      XCTAssertGreaterThan(labels.bounds.height, 26)
      if alignment == "top" { XCTAssertEqual(labels.frame.minY, 0, accuracy: 1) }
      else { XCTAssertEqual(labels.frame.maxY, column.bounds.height, accuracy: 1) }
    }
  }

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

  func testAccessibilityLabelFollowsVisibleMetricsAndExplicitOverride() throws {
    let cell = NativeListMarketCell(frame: .zero)
    let bind: (NativeListItem) -> Void = { item in
      cell.bind(item: item, theme: nil, layout: "linear", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
    }
    var metrics: [[String: Any]] = [
      ["key": "volume", "text": "$5M", "secondaryText": "24h"],
      ["key": "cap", "text": "$8M"],
    ]
    bind(try item(metrics))
    XCTAssertEqual(cell.accessibilityLabel, "ETH, Volume, $5M, 24h, $8M")

    metrics[0]["textSegments"] = [["text": "$"], ["text": "6M"]]
    cell.updateMarketQuote(try item(metrics), theme: nil)
    XCTAssertEqual(cell.accessibilityLabel, "ETH, Volume, $6M, 24h, $8M")
    cell.updateMarketQuote(try item(metrics, accessibilityLabel: "Market summary"), theme: nil)
    XCTAssertEqual(cell.accessibilityLabel, "Market summary")

    bind(try item())
    XCTAssertEqual(cell.accessibilityLabel, "ETH, Volume, $42, +2%")
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
