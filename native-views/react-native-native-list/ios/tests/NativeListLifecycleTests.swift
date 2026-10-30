import UIKit
import XCTest
@testable import NativeListModule

@MainActor
final class NativeListLifecycleTests: XCTestCase {
  func testAcceptedRowIndexReusesStableShapeAndRejectsLateInvalidPatch() throws {
    let view = NativeListView(frame: CGRect(x: 0, y: 0, width: 320, height: 480))
    func snapshot(_ keys: [String], generation: Int, title: String = "Old") throws -> String {
      let rows: [[String: Any]] = keys.map { key in
        ["key": key, "type": "action", "title": title, "actionKey": "open"]
      }
      let value: [String: Any] = ["schemaVersion": 1, "generation": generation,
        "layout": ["kind": "linear"], "rows": rows]
      return String(decoding: try JSONSerialization.data(withJSONObject: value), as: UTF8.self)
    }
    func index() throws -> (ObjectIdentifier, [String: Int]) {
      let optional = try XCTUnwrap(Mirror(reflecting: view).descendant("acceptedRowIndex"))
      let value = try XCTUnwrap(Mirror(reflecting: optional).children.first?.value)
      let byKey = try XCTUnwrap(Mirror(reflecting: value).descendant("byKey") as? [String: Int])
      return (ObjectIdentifier(value as AnyObject), byKey)
    }
    func title(_ key: String) throws -> String {
      let items = try XCTUnwrap(Mirror(reflecting: view).descendant("itemsByKey")
        as? [String: NativeListItem])
      return try XCTUnwrap(items[key]?.data["title"] as? String)
    }
    func patch(_ rows: [[String: Any]]) throws {
      view.applyPatchesJson(String(decoding: try JSONSerialization.data(withJSONObject: rows),
        as: UTF8.self))
    }

    view.applySnapshotJson(try snapshot(["alpha", "beta"], generation: 1))
    let initial = try index()
    XCTAssertEqual(initial.1, ["alpha": 0, "beta": 1])
    view.applySnapshotJson(try snapshot(["alpha", "beta"], generation: 1, title: "New"))
    XCTAssertEqual(try index().0, initial.0)
    try patch([["key": "alpha", "type": "action", "changes": ["title": "Patched"]],
      ["key": "missing", "type": "action", "changes": ["title": "Invalid"]]])
    XCTAssertEqual(try title("alpha"), "New")
    XCTAssertEqual(try title("beta"), "New")
    XCTAssertEqual(try index().0, initial.0)
    try patch([["key": "alpha", "type": "action", "changes": ["title": "Patched"]]])
    XCTAssertEqual(try title("alpha"), "Patched")
    XCTAssertEqual(try index().0, initial.0)

    view.applySnapshotJson(try snapshot(["beta", "alpha"], generation: 1))
    let reordered = try index()
    XCTAssertNotEqual(reordered.0, initial.0)
    XCTAssertEqual(reordered.1, ["beta": 0, "alpha": 1])
    try patch([["key": "alpha", "type": "action", "changes": ["title": "After reorder"]]])
    XCTAssertEqual(try title("alpha"), "After reorder")
    view.applySnapshotJson(try snapshot(["beta", "gamma", "alpha"], generation: 1))
    let inserted = try index()
    XCTAssertNotEqual(inserted.0, reordered.0)
    XCTAssertEqual(inserted.1, ["beta": 0, "gamma": 1, "alpha": 2])
    try patch([["key": "alpha", "type": "action", "changes": ["title": "After insert"]]])
    XCTAssertEqual(try title("alpha"), "After insert")
    view.applySnapshotJson(try snapshot(["beta", "alpha"], generation: 1))
    let deleted = try index()
    XCTAssertNotEqual(deleted.0, inserted.0)
    XCTAssertEqual(deleted.1, ["beta": 0, "alpha": 1])
    try patch([["key": "alpha", "type": "action", "changes": ["title": "After delete"]]])
    XCTAssertEqual(try title("alpha"), "After delete")
    view.applySnapshotJson(try snapshot(["beta", "alpha"], generation: 2))
    XCTAssertNotEqual(try index().0, deleted.0)
    let beforeTypeChange = try index().0
    let typeChanged: [String: Any] = ["schemaVersion": 1, "generation": 2,
      "layout": ["kind": "linear"], "rows": [
        ["key": "beta", "type": "identity", "title": "Beta",
          "leading": ["kind": "icon", "name": "coin"]],
        ["key": "alpha", "type": "action", "title": "Alpha", "actionKey": "open"],
      ]]
    view.applySnapshotJson(String(decoding: try JSONSerialization.data(withJSONObject: typeChanged),
      as: UTF8.self))
    XCTAssertNotEqual(try index().0, beforeTypeChange)
    view.dispose()
    XCTAssertEqual(Mirror(reflecting: try XCTUnwrap(Mirror(reflecting: view)
      .descendant("acceptedRowIndex"))).children.count, 0)
  }

  func testScrollEdgeStylesPreserveOverlayGeometryAndRestoreOriginalStyle() throws {
    guard #available(iOS 26.0, *) else {
      throw XCTSkip("Scroll-edge styles require iOS 26 or newer")
    }
    let view = try makeScrollingList()
    let window = UIWindow(frame: view.frame)
    window.addSubview(view)
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    let originalStyle = list.topEdgeEffect.style
    let originalInset = list.contentInset.top
    let overlay = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 64))
    for index in 0..<6 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.mountContainerSlot(overlay, atIndex: 6)
    view.setTopScrollEdge(effect: .hard, overlayHeight: 64)
    XCTAssertEqual(list.topEdgeEffect.style, .hard)
    XCTAssertEqual(list.contentInset.top, originalInset + 64, accuracy: 0.01)
    let offset = list.contentOffset
    view.setTopScrollEdge(effect: .soft, overlayHeight: 64)
    XCTAssertEqual(list.topEdgeEffect.style, .soft)
    XCTAssertEqual(list.contentInset.top, originalInset + 64, accuracy: 0.01)
    XCTAssertEqual(list.contentOffset, offset)
    XCTAssertNotNil(overlay.superview)
    view.setTopScrollEdge(effect: .none, overlayHeight: 0)
    XCTAssertEqual(list.topEdgeEffect.style, originalStyle)
    XCTAssertEqual(list.contentInset.top, originalInset, accuracy: 0.01)
    view.unmountContainerSlot(overlay)
    view.removeFromSuperview()
  }

  func testInvalidCommandsPreserveAcceptedAnimationCompletion() throws {
    for invalid in 0..<3 {
      let view = try makeScrollingList()
      let window = UIWindow(frame: view.frame)
      let controller = UIViewController()
      window.rootViewController = controller
      controller.view.addSubview(view)
      window.isHidden = false
      defer { window.isHidden = true }
      window.layoutIfNeeded()
      view.layoutIfNeeded()
      drainMainQueue()
      let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
      XCTAssertNotNil(view.window)
      XCTAssertGreaterThan(list.contentSize.height - list.bounds.height +
        list.adjustedContentInset.top + list.adjustedContentInset.bottom, 800)
      let phases = NSMutableArray()
      let observer = NotificationCenter.default.addObserver(
        forName: Notification.Name("OneKeyNativeListScrollCommandAccepted"),
        object: list, queue: nil
      ) { notification in
        if let phase = notification.userInfo?["phase"] as? Int { phases.add(phase) }
      }
      defer { NotificationCenter.default.removeObserver(observer) }
      view.scrollToOffset(800, animated: true)
      XCTAssertEqual(phases as? [Int], [0, 1])
      switch invalid {
      case 0: view.scrollToKey("missing-fixture-key", animated: true, alignment: "start")
      case 1: view.scrollToIndex(-1, animated: true, alignment: "start")
      default: view.scrollToOffset(.nan, animated: true)
      }
      XCTAssertEqual(phases as? [Int], [0, 1], "Invalid input must not accept or end another command")
      // Exercise the actual delegate terminal callback, not a copied state helper.
      view.scrollViewDidEndScrollingAnimation(list)
      XCTAssertEqual(phases as? [Int], [0, 1, 2])
      view.scrollViewDidEndScrollingAnimation(list)
      XCTAssertEqual(phases as? [Int], [0, 1, 2], "Completion is emitted once")
    }
  }

  func testInvalidKeyPreservesValidPendingScrollUntilGeometryArrives() throws {
    let view = try makeScrollingList()
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    view.frame.size.height = 0
    view.setNeedsLayout()
    view.layoutIfNeeded()
    XCTAssertEqual(list.bounds.height, 0)
    view.scrollToOffset(500, animated: false)
    view.scrollToKey("missing-fixture-key", animated: true, alignment: "start")
    view.frame.size.height = 708
    view.setNeedsLayout()
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    XCTAssertEqual(list.contentOffset.y, 500 - list.adjustedContentInset.top, accuracy: 0.5)
  }

  func testSectionLoadingStopsOnRebindReuseAndDetach() throws {
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 480))
    let controller = UIViewController()
    window.rootViewController = controller
    window.isHidden = false
    defer { window.isHidden = true }
    let cell = NativeListSectionHeaderCell(frame: CGRect(x: 0, y: 0, width: 320, height: 40))
    controller.view.addSubview(cell)
    func bind(loading: Bool) throws {
      let item = try NativeListItem(data: [
        "key": "section", "type": "sectionHeader", "sectionKey": "pending",
        "title": "Confirming", "variant": "history", "titleLoading": loading,
        "style": ["title": ["color": "#FFAA00"]],
      ])
      cell.bind(item: item, theme: nil, layout: "sectioned", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
    }
    try bind(loading: true)
    let spinner = try XCTUnwrap(cell.root.arrangedSubviews.first as? UIActivityIndicatorView)
    XCTAssertTrue(spinner.isAnimating)
    XCTAssertFalse(spinner.isAccessibilityElement)
    XCTAssertEqual(spinner.color, UIColor(nativeListHex: "#FFAA00", fallback: .clear))

    try bind(loading: false)
    XCTAssertFalse(spinner.isAnimating)
    XCTAssertNil(spinner.superview)
    try bind(loading: true)
    XCTAssertTrue(spinner.isAnimating)
    cell.removeFromSuperview()
    XCTAssertFalse(spinner.isAnimating)
    controller.view.addSubview(cell)
    XCTAssertTrue(spinner.isAnimating)
    cell.prepareForReuse()
    XCTAssertFalse(spinner.isAnimating)
  }

  func testRebindingAndReuseInvalidateThePreviousActionEpoch() throws {
    let cell = NativeListActionCell(frame: CGRect(x: 0, y: 0, width: 320, height: 60))
    let first = try NativeListItem(data: ["key": "row", "type": "action", "title": "First"])
    let changed = try NativeListItem(data: ["key": "row", "type": "action", "title": "Changed"])
    var invalidatedEpochs: [Int] = []
    cell.onBindingInvalidated = { _, epoch in invalidatedEpochs.append(epoch) }

    func bind(_ item: NativeListItem) {
      cell.bind(item: item, theme: nil, layout: "linear", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
    }

    bind(first)
    let originalEpoch = cell.rowActionOrigin().bindingEpoch
    bind(first)
    XCTAssertEqual(cell.bindingEpoch, originalEpoch, "Identical content should retain its action origin")
    XCTAssertTrue(invalidatedEpochs.isEmpty)

    bind(changed)
    XCTAssertEqual(invalidatedEpochs, [originalEpoch])
    XCTAssertNotEqual(cell.rowActionOrigin().bindingEpoch, originalEpoch)

    let changedEpoch = cell.bindingEpoch
    cell.prepareForReuse()
    XCTAssertEqual(invalidatedEpochs, [originalEpoch, changedEpoch])
    XCTAssertNotEqual(cell.bindingEpoch, changedEpoch)
  }

  func testRiskIdentityTitleIconAndButtonResetOnProductionCellBind() throws {
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 320, height: 56))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
    window.addSubview(cell)
    let titleIcon = try XCTUnwrap(
      Mirror(reflecting: cell).children.first { $0.label == "titleIconView" }?.value as? UIImageView)
    let titleStack = try XCTUnwrap(
      Mirror(reflecting: cell).children.first { $0.label == "titleRowStack" }?.value as? UIStackView)
    let titleLabel = try XCTUnwrap(
      Mirror(reflecting: cell).children.first { $0.label == "titleLabel" }?.value as? UILabel)
    let trailing = try XCTUnwrap(
      Mirror(reflecting: cell).children.first { $0.label == "trailingStack" }?.value
        as? NativeListAccessoryStack)
    func bind(_ data: [String: Any]) throws {
      let item = try NativeListItem(data: data)
      cell.bind(item: item, theme: nil, layout: "linear", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
      cell.layoutIfNeeded()
    }
    let base: [String: Any] = ["key": "risk", "type": "identity", "title": "Risk",
      "height": 56, "leading": ["kind": "token",
        "fallbackIcon": ["name": "CryptoCoinOutline"]]]
    try bind(base)
    XCTAssertTrue(titleIcon.isHidden)
    XCTAssertLessThanOrEqual(titleIcon.bounds.width, 0.5)
    try bind(base.merging(["titleIcon": ["name": "GasSolid"],
      "trailing": [["kind": "button", "label": "Show", "actionKey": "show",
        "backgroundColor": "#DDDDDD", "pressedBackgroundColor": "#AAAAAA"]]]) { _, newer in newer })
    XCTAssertFalse(titleIcon.isHidden)
    XCTAssertNotNil(titleIcon.image)
    XCTAssertEqual(titleIcon.bounds.width, 20, accuracy: 0.5)
    XCTAssertEqual(titleStack.customSpacing(after: titleLabel), 4, accuracy: 0.1)
    let button = try XCTUnwrap(trailing.arrangedSubviews.compactMap { $0 as? NativeListAccessoryButton }
      .first { !$0.isHidden })
    XCTAssertEqual(button.intrinsicContentSize.height, 30, accuracy: 0.5)
    let normal = button.backgroundColor
    button.isHighlighted = true
    XCTAssertNotEqual(button.backgroundColor, normal)
    button.isHighlighted = false
    XCTAssertEqual(button.backgroundColor, normal)
    try bind(base)
    XCTAssertTrue(titleIcon.isHidden)
    XCTAssertLessThanOrEqual(titleIcon.bounds.width, 0.5)
    XCTAssertTrue(trailing.arrangedSubviews.compactMap { $0 as? NativeListAccessoryButton }
      .allSatisfy { $0.isHidden })
  }

  func testIdentityTitleSuffixesUsePlainLabelsAndClearOnRebind() throws {
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 320, height: 60))
    let titleStack = try XCTUnwrap(
      Mirror(reflecting: cell).children.first { $0.label == "titleRowStack" }?.value as? UIStackView)
    func bind(_ suffixes: [[String: Any]]?) throws {
      var data: [String: Any] = [
        "key": "recent", "type": "identity", "title": "Alice",
        "leading": ["kind": "account", "fallbackText": "A"],
      ]
      if let suffixes { data["titleSuffixes"] = suffixes }
      let item = try NativeListItem(data: data)
      cell.bind(item: item, theme: nil, layout: "linear", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
      cell.layoutIfNeeded()
    }
    try bind([
      ["text": "EVM", "tone": "secondary", "maxWidth": 128],
      ["text": "2 days ago", "tone": "disabled"],
    ])
    let suffixes = titleStack.arrangedSubviews.compactMap { $0 as? UILabel }
      .filter { ["EVM", "2 days ago"].contains($0.text ?? "") }
    XCTAssertEqual(suffixes.map(\.text), ["EVM", "2 days ago"])
    XCTAssertTrue(suffixes.allSatisfy { $0.font.pointSize == 12 && $0.numberOfLines == 1 })
    XCTAssertFalse(suffixes.contains { $0 is NativeListInsetLabel })
    try bind(nil)
    XCTAssertFalse(titleStack.arrangedSubviews.compactMap { $0 as? UILabel }
      .contains { ["EVM", "2 days ago"].contains($0.text ?? "") })
  }

  func testDataRowTitleIconsClearWhenCellIsRebound() throws {
    let cell = NativeListDataRowCell(frame: CGRect(x: 0, y: 0, width: 380, height: 60))
    func bind(_ column: [String: Any]) throws {
      let item = try NativeListItem(data: [
        "key": "asset", "type": "dataRow", "columns": [column],
      ])
      cell.bind(item: item, theme: nil, layout: "table", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
      cell.layoutIfNeeded()
    }
    func titleIcons(in view: UIView) -> [UIImageView] {
      let current = (view as? UIImageView).map { [$0] } ?? []
      return current + view.subviews.flatMap(titleIcons)
    }
    try bind([
      "key": "asset", "text": "ETH",
      "titleIcon": ["name": "GasSolid", "accessibilityLabel": "Gas help"],
      "secondaryTitleIcon": ["name": "InfoCircleOutline", "accessibilityLabel": "Info help"],
    ])
    let visible = titleIcons(in: cell).filter { !$0.isHidden && $0.accessibilityLabel != nil }
    XCTAssertEqual(visible.compactMap(\.accessibilityLabel), ["Gas help", "Info help"])
    XCTAssertTrue(visible.allSatisfy { $0.image != nil })

    try bind(["key": "asset", "text": "USDC"])
    XCTAssertTrue(titleIcons(in: cell).allSatisfy {
      $0.accessibilityLabel != "Gas help" && $0.accessibilityLabel != "Info help"
    })
  }

  func testDataRowLinearRebindKeepsSizeConstraintsAndClearsOldContent() throws {
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 700, height: 100))
    let cell = NativeListDataRowCell(frame: CGRect(x: 0, y: 0, width: 700, height: 60))
    window.addSubview(cell)
    window.isHidden = false
    defer { window.isHidden = true }
    func bind(_ title: String, secondary: String, layout: String = "linear",
      visualWidth: Int, trailing: String?, icon: Bool = false, badge: Bool = false,
      key: String = "asset") throws {
      var column: [String: Any] = ["key": "asset", "text": title, "secondaryText": secondary]
      if icon { column["titleIcon"] = ["name": "GasSolid", "accessibilityLabel": "Gas help"] }
      let item = try NativeListItem(data: [
        "key": key, "type": "dataRow", "height": 60,
        "leading": ["kind": "token", "fallbackIcon": ["name": "CryptoCoinOutline"]],
        "columns": [column], "badges": badge ? [["text": "Badge"]] : [],
        "trailing": trailing.map { [["kind": "button", "label": $0, "actionKey": "swap"]] } ?? [],
        "style": ["image": ["width": visualWidth, "height": visualWidth]],
      ])
      cell.bind(item: item, theme: nil, layout: layout, selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
      window.layoutIfNeeded()
      cell.layoutIfNeeded()
    }
    func property<T>(_ name: String, in object: Any, as type: T.Type) throws -> T {
      try XCTUnwrap(Mirror(reflecting: object).children.first { $0.label == name }?.value as? T)
    }
    let columns = try XCTUnwrap(cell.root.arrangedSubviews[3] as? UIStackView)
    let firstColumn = columns.arrangedSubviews[0]
    let primary: UILabel = try property("primaryLabel", in: firstColumn, as: UILabel.self)
    let secondary: UILabel = try property("secondaryLabel", in: firstColumn, as: UILabel.self)
    let secondaryLine: UIStackView = try property("secondaryLine", in: firstColumn,
      as: UIStackView.self)
    let titleIcon: UIImageView = try property("titleIconView", in: firstColumn,
      as: UIImageView.self)
    let badges: UIStackView = try property("badgesStack", in: firstColumn,
      as: UIStackView.self)

    try bind("ETH", secondary: "Ethereum", visualWidth: 32, trailing: "Swap",
      icon: true, badge: true)
    let sizeConstraints: [NSLayoutConstraint] = try property("rowConstraints", in: cell,
      as: [NSLayoutConstraint].self)
    XCTAssertEqual(sizeConstraints.count, 4)
    let trailingStack = cell.root.arrangedSubviews[4]
    let trailingWidth = try XCTUnwrap(trailingStack.constraints.first {
      $0.firstAttribute == .width && ($0.firstItem as? UIView) === trailingStack
    })
    XCTAssertTrue(trailingWidth.isActive)
    let shortTrailingWidth = trailingWidth.constant
    XCTAssertEqual(sizeConstraints[2].constant, 32, accuracy: 0.1)
    XCTAssertTrue(primary.attributedText?.string.contains("Badge") ?? false)
    XCTAssertFalse(titleIcon.isHidden)
    XCTAssertEqual(badges.arrangedSubviews.count, 0)

    try bind("ETH", secondary: "Ethereum", visualWidth: 32, trailing: "Manage asset")
    XCTAssertGreaterThan(trailingWidth.constant, shortTrailingWidth + 1)
    let columnsFrame = columns.convert(columns.bounds, to: cell)
    let trailingFrame = trailingStack.convert(trailingStack.bounds, to: cell)
    XCTAssertGreaterThanOrEqual(trailingFrame.minX, columnsFrame.maxX - 0.5)
    try bind("ETH", secondary: "Ethereum", visualWidth: 32, trailing: "Swap")
    XCTAssertEqual(trailingWidth.constant, shortTrailingWidth, accuracy: 1)

    try bind("", secondary: "", visualWidth: 48, trailing: nil)
    let reboundConstraints: [NSLayoutConstraint] = try property("rowConstraints", in: cell,
      as: [NSLayoutConstraint].self)
    XCTAssertEqual(reboundConstraints.count, sizeConstraints.count)
    XCTAssertTrue(zip(sizeConstraints, reboundConstraints).allSatisfy { $0 === $1 })
    XCTAssertEqual(sizeConstraints[2].constant, 48, accuracy: 0.1)
    XCTAssertFalse(trailingWidth.isActive)
    XCTAssertEqual(primary.attributedText?.string, "")
    XCTAssertNil(secondary.attributedText)
    XCTAssertTrue(secondaryLine.isHidden)
    XCTAssertTrue(titleIcon.isHidden)
    XCTAssertNil(titleIcon.accessibilityLabel)
    XCTAssertTrue(badges.arrangedSubviews.isEmpty)

    try bind("Table", secondary: "New", layout: "table", visualWidth: 40, trailing: "Swap")
    XCTAssertTrue(trailingWidth.isActive)
    XCTAssertEqual(sizeConstraints[2].constant, 40, accuracy: 0.1)
    XCTAssertEqual(primary.attributedText?.string, "Table")
    XCTAssertEqual(secondary.attributedText?.string, "New")
    try bind("Linear", secondary: "", visualWidth: 40, trailing: nil)
    XCTAssertEqual(primary.attributedText?.string, "Linear")
    XCTAssertNil(secondary.attributedText)
    cell.prepareForReuse()
    XCTAssertNil(primary.attributedText)
    XCTAssertTrue(titleIcon.isHidden)
    try bind("Replacement", secondary: "", visualWidth: 40, trailing: nil, key: "next")
    XCTAssertEqual(primary.attributedText?.string, "Replacement")
  }

  func testIdentityTrailingActionUsesDescriptorSlotAfterCheckboxAndRebind() throws {
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 386, height: 200))
    let controller = UIViewController()
    window.rootViewController = controller
    window.isHidden = false
    defer { window.isHidden = true }
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 386, height: 56))
    controller.view.addSubview(cell)
    let trailing = try XCTUnwrap(
      Mirror(reflecting: cell).children.first { $0.label == "trailingStack" }?.value
        as? NativeListAccessoryStack)
    var actions: [(rowKey: String, key: String, source: String, slot: Int?)] = []
    cell.onAction = { item, key, _, origin in
      actions.append((item.key, key, origin?.source ?? "", origin?.slot))
    }
    func bind(_ trailingData: [[String: Any]], key: String = "row") throws {
      let item = try NativeListItem(data: [
        "key": key, "type": "identity", "title": "Row", "trailing": trailingData,
      ])
      cell.bind(item: item, theme: nil, layout: "linear", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
      window.layoutIfNeeded()
      cell.layoutIfNeeded()
    }
    func visibleButtons() -> [NativeListAccessoryButton] {
      trailing.arrangedSubviews.compactMap { $0 as? NativeListAccessoryButton }
        .filter { !$0.isHidden }
    }
    let checkbox: [String: Any] = [
      "kind": "checkbox", "state": "unchecked", "actionKey": "selection",
    ]
    let first: [String: Any] = ["kind": "button", "label": "First", "actionKey": "first"]
    let second: [String: Any] = ["kind": "button", "label": "Second", "actionKey": "second"]

    try bind([checkbox, second])
    XCTAssertEqual(visibleButtons().count, 1)
    XCTAssertTrue(visibleButtons()[0].isEnabled)
    XCTAssertTrue(visibleButtons()[0].isUserInteractionEnabled)
    XCTAssertTrue(visibleButtons()[0].allTargets.contains(trailing))
    XCTAssertNotNil(trailing.onAction)
    try dispatchAccessoryTouchUpInside(visibleButtons()[0], on: trailing)
    XCTAssertEqual(actions.last?.rowKey, "row")
    XCTAssertEqual(actions.last?.key, "second")
    XCTAssertEqual(actions.last?.source, "trailingAccessory")
    XCTAssertEqual(actions.last?.slot, 1)
    let selection = try XCTUnwrap(trailing.arrangedSubviews.compactMap { $0 as? UIButton }
      .first { !($0 is NativeListAccessoryButton) && !$0.isHidden })
    try dispatchAccessoryTouchUpInside(selection, on: trailing)
    XCTAssertEqual(actions.last?.key, "selection")
    XCTAssertNil(actions.last?.slot)

    try bind([first, second])
    XCTAssertEqual(visibleButtons().count, 2)
    try dispatchAccessoryTouchUpInside(visibleButtons()[0], on: trailing)
    XCTAssertEqual(actions.last?.key, "first")
    XCTAssertEqual(actions.last?.slot, 0)
    try dispatchAccessoryTouchUpInside(visibleButtons()[1], on: trailing)
    XCTAssertEqual(actions.last?.key, "second")
    XCTAssertEqual(actions.last?.slot, 1)

    try bind([second], key: "replacement")
    XCTAssertEqual(visibleButtons().count, 1)
    try dispatchAccessoryTouchUpInside(visibleButtons()[0], on: trailing)
    XCTAssertEqual(actions.last?.rowKey, "replacement")
    XCTAssertEqual(actions.last?.key, "second")
    XCTAssertEqual(actions.last?.slot, 0)
    XCTAssertEqual(actions.count, 5)
  }

  func testIdentityTertiaryIconAnchorInsetSurvivesAccessoryRebind() throws {
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 386, height: 200))
    let controller = UIViewController()
    window.rootViewController = controller
    window.isHidden = false
    defer { window.isHidden = true }
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 386, height: 88))
    controller.view.addSubview(cell)
    let trailing = try XCTUnwrap(
      Mirror(reflecting: cell).children.first { $0.label == "trailingStack" }?.value
        as? NativeListAccessoryStack)
    var actions: [(rowKey: String, source: String, slot: Int?, inset: CGFloat,
      width: CGFloat)] = []
    cell.onAction = { item, _, _, origin in
      actions.append((item.key, origin?.source ?? "", origin?.slot, origin?.anchorInset ?? -1,
        origin?.sourceView?.bounds.width ?? -1))
    }
    func bind(_ descriptors: [[String: Any]], key: String,
      presentation: String? = nil) throws -> NativeListAccessoryButton {
      var data: [String: Any] = [
        "key": key, "type": "identity", "title": "Row", "height": 88,
        "trailing": descriptors,
      ]
      if let presentation { data["presentation"] = presentation }
      cell.bind(item: try NativeListItem(data: data), theme: nil, layout: "linear",
        selected: false, checkboxState: { _, _, fallback in fallback },
        fonts: NativeListFontFamilies())
      window.layoutIfNeeded()
      cell.layoutIfNeeded()
      return try XCTUnwrap(trailing.firstVisibleControl as? NativeListAccessoryButton)
    }
    let tertiary: [String: Any] = [
      "kind": "icon", "name": "CryptoCoinOutline", "buttonStyle": "tertiary",
      "actionKey": "menu",
    ]
    let ordinary: [String: Any] = [
      "kind": "icon", "name": "CryptoCoinOutline", "actionKey": "menu",
    ]

    let first = try bind([
      ["kind": "checkbox", "state": "unchecked", "actionKey": "selection"], tertiary,
    ], key: "tertiary-first")
    XCTAssertEqual(first.bounds.width, 38, accuracy: 0.5)
    try dispatchAccessoryTouchUpInside(first, on: trailing)
    XCTAssertEqual(actions.last?.rowKey, "tertiary-first")
    XCTAssertEqual(actions.last?.source, "trailingAccessory")
    XCTAssertEqual(actions.last?.slot, 1)
    XCTAssertEqual(actions.last?.inset, 7)
    let firstOrigin = try XCTUnwrap(actions.last)
    XCTAssertEqual(firstOrigin.width - 2 * firstOrigin.inset, 24, accuracy: 0.5)

    let normal = try bind([ordinary], key: "ordinary")
    XCTAssertEqual(normal.bounds.width, 36, accuracy: 0.5)
    try dispatchAccessoryTouchUpInside(normal, on: trailing)
    XCTAssertEqual(actions.last?.rowKey, "ordinary")
    XCTAssertEqual(actions.last?.slot, 0)
    XCTAssertEqual(actions.last?.inset, 0)
    XCTAssertEqual(actions.last?.width, 36)

    let value = try bind([["kind": "value", "text": "Value"]], key: "value")
    value.bounds.size.width = 38
    XCTAssertEqual(trailing.anchorInset(for: value), 0)

    let rebound = try bind([tertiary], key: "tertiary-again")
    try dispatchAccessoryTouchUpInside(rebound, on: trailing)
    XCTAssertEqual(actions.last?.rowKey, "tertiary-again")
    XCTAssertEqual(actions.last?.slot, 0)
    XCTAssertEqual(actions.last?.inset, 7)
    let reboundOrigin = try XCTUnwrap(actions.last)
    XCTAssertEqual(reboundOrigin.width - 2 * reboundOrigin.inset, 24, accuracy: 0.5)

    let account = try bind([ordinary], key: "account", presentation: "accountSelector")
    XCTAssertEqual(account.bounds.width, 38, accuracy: 0.5)
    try dispatchAccessoryTouchUpInside(account, on: trailing)
    XCTAssertEqual(actions.last?.rowKey, "account")
    XCTAssertEqual(actions.last?.slot, 0)
    XCTAssertEqual(actions.last?.inset, 7)
    let accountOrigin = try XCTUnwrap(actions.last)
    XCTAssertEqual(accountOrigin.width - 2 * accountOrigin.inset, 24, accuracy: 0.5)

    let finalOrdinary = try bind([ordinary], key: "ordinary-again")
    try dispatchAccessoryTouchUpInside(finalOrdinary, on: trailing)
    XCTAssertEqual(actions.last?.rowKey, "ordinary-again")
    XCTAssertEqual(actions.last?.inset, 0)
    XCTAssertEqual(actions.count, 5)
  }

  func testValuePairSegmentsStyleAndPlainRebindOnProductionAccessoryStack() throws {
    let stack = NativeListAccessoryStack(frame: CGRect(x: 0, y: 0, width: 180, height: 48))
    let item = try NativeListItem(data: [
      "key": "pair", "type": "identity", "title": "Pair",
      "leading": ["kind": "icon", "name": "coin"],
    ])
    let pair: [String: Any] = [
      "kind": "valuePair", "primary": "0.001", "secondary": "$0.01",
      "primaryTextSegments": [["text": "0.00"], ["text": "1", "style": "subscript"]],
      "secondaryTextSegments": [["text": "$0.0"], ["text": "1", "style": "subscript"]],
    ]
    func bind(_ descriptor: [String: Any], size: Int? = nil) {
      let style: [String: Any] = size.map { ["value": ["fontSize": $0]] } ?? [:]
      stack.bind(item, descriptors: [descriptor], theme: nil, style: style,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
    }
    func fontSize(_ title: NSAttributedString, at index: Int) throws -> CGFloat {
      try XCTUnwrap(title.attribute(.font, at: index, effectiveRange: nil) as? UIFont).pointSize
    }
    bind(pair)
    let button = try XCTUnwrap(stack.arrangedSubviews.compactMap { $0 as? UIButton }
      .first { !$0.isHidden })
    let rich = try XCTUnwrap(button.attributedTitle(for: .normal))
    XCTAssertEqual(rich.string, "0.001\n$0.01")
    XCTAssertEqual(try fontSize(rich, at: 0), 16)
    XCTAssertEqual(try fontSize(rich, at: 4), 10)
    XCTAssertEqual(try fontSize(rich, at: 6), 14)
    XCTAssertEqual(try fontSize(rich, at: 10), 9)
    bind(pair, size: 20)
    let styled = try XCTUnwrap(button.attributedTitle(for: .normal))
    XCTAssertEqual(try fontSize(styled, at: 0), 20)
    XCTAssertEqual(try fontSize(styled, at: 4), 12)
    XCTAssertEqual(try fontSize(styled, at: 10), 12)
    bind(["kind": "valuePair", "primary": "10", "secondary": "$2"])
    let plain = try XCTUnwrap(button.attributedTitle(for: .normal))
    XCTAssertEqual(plain.string, "10\n$2")
    XCTAssertEqual(try fontSize(plain, at: 0), 16)
    XCTAssertEqual(try fontSize(plain, at: 3), 14)
    bind(["kind": "valuePair", "primary": "10", "secondary": "$2",
      "primaryTextSegments": [], "secondaryTextSegments": []])
    XCTAssertEqual(button.attributedTitle(for: .normal)?.string, "10\n$2")
    stack.reset()
    XCTAssertNil(button.title(for: .normal))
    XCTAssertNil(button.attributedTitle(for: .normal))
    XCTAssertTrue(button.isHidden)
    bind(["kind": "value", "text": "Updated"])
    XCTAssertEqual(button.title(for: .normal), "Updated")
    XCTAssertEqual(button.titleLabel?.text, "Updated")
    XCTAssertNil(button.attributedTitle(for: .normal))
  }

  func testDecorativeRefreshKeepsSixteenPointFrameAcrossActionRebind() throws {
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 386, height: 56))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 386, height: 80))
    window.addSubview(cell)
    let stack = try XCTUnwrap(Mirror(reflecting: cell).children.first {
      $0.label == "trailingStack"
    }?.value as? NativeListAccessoryStack)
    func bind(actionKey: String? = nil) throws {
      var icon: [String: Any] = ["kind": "icon", "name": "RefreshCcwOutline"]
      if let actionKey { icon["actionKey"] = actionKey }
      let item = try NativeListItem(data: [
        "key": "refresh", "type": "identity", "title": "Network", "height": 56,
        "leading": ["kind": "icon", "name": "coin"], "trailing": [icon],
      ])
      cell.bind(item: item, theme: nil, layout: "linear", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
      window.layoutIfNeeded()
      cell.layoutIfNeeded()
    }
    try bind()
    let button = try XCTUnwrap(stack.firstVisibleControl as? UIButton)
    XCTAssertEqual(button.bounds.width, 16, accuracy: 0.5)
    XCTAssertEqual(button.bounds.height, 16, accuracy: 0.5)
    XCTAssertNotNil(button.image(for: .normal))
    XCTAssertEqual(button.image(for: .normal)?.size.width, 16)
    XCTAssertEqual(button.image(for: .normal)?.size.height, 16)
    XCTAssertFalse(button.isUserInteractionEnabled)
    try bind(actionKey: "refresh")
    XCTAssertEqual(button.bounds.width, 36, accuracy: 0.5)
    XCTAssertEqual(button.bounds.height, 36, accuracy: 0.5)
    XCTAssertTrue(button.isUserInteractionEnabled)
    try bind()
    XCTAssertEqual(button.bounds.width, 16, accuracy: 0.5)
    XCTAssertEqual(button.bounds.height, 16, accuracy: 0.5)
    XCTAssertEqual(button.image(for: .normal)?.size.width, 16)
    XCTAssertEqual(button.image(for: .normal)?.size.height, 16)
    XCTAssertFalse(button.isUserInteractionEnabled)
  }

  func testLowValueAndOverviewAccessoryLayoutsOnProductionIdentityCell() throws {
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 386, height: 60))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 386, height: 180))
    window.addSubview(cell)
    let main = try XCTUnwrap(Mirror(reflecting: cell).children.first { $0.label == "mainStack" }?
      .value as? UIStackView)
    let trailing = try XCTUnwrap(Mirror(reflecting: cell).children.first {
      $0.label == "trailingStack"
    }?.value as? NativeListAccessoryStack)
    func bind(_ data: [String: Any], height: CGFloat) throws {
      cell.frame.size.height = height
      cell.bind(item: try NativeListItem(data: data), theme: nil, layout: "linear",
        selected: false, checkboxState: { _, _, fallback in fallback },
        fonts: NativeListFontFamilies())
      window.layoutIfNeeded()
      cell.layoutIfNeeded()
    }
    let pair: [String: Any] = ["kind": "valuePair", "primary": "1.0193B",
      "secondary": "$1,018,849,363.70"]
    let lowValue: [String: Any] = ["key": "low", "type": "identity", "height": 60,
      "title": "FRAX", "subtitleSegments": [
        ["text": "$0.9918", "tone": "secondary", "textSegments": [
          ["text": "$0.99"], ["text": "18", "style": "subscript"]]],
        ["text": "\u{00a0}+0.05%", "tone": "positive"]],
      "leading": ["kind": "token", "fallbackIcon": ["name": "CryptoCoinOutline"]],
      "trailing": [pair], "style": ["horizontalPadding": 12, "verticalPadding": 8,
        "leadingGap": 12, "image": ["width": 40, "height": 40]]]
    try bind(lowValue, height: 60)
    let priceLabels = try XCTUnwrap(Mirror(reflecting: cell).children.first {
      $0.label == "semanticSubtitleLabels"
    }?.value as? [UILabel])
    XCTAssertEqual(priceLabels.count, 2)
    XCTAssertGreaterThan(main.bounds.width, 150)
    XCTAssertTrue(priceLabels.allSatisfy { $0.bounds.width > 0 && !$0.isHidden })
    XCTAssertLessThanOrEqual(priceLabels.last?.frame.maxX ?? .infinity, main.bounds.width + 0.5)
    XCTAssertEqual(trailing.bounds.width, trailing.firstVisibleControl?.bounds.width ?? -1,
      accuracy: 0.5)
    let detail: [String: Any] = ["key": "detail", "type": "identity", "height": 60,
      "title": "Ethereum", "subtitle": "100.0%",
      "leading": ["kind": "network", "fallbackIcon": ["name": "GlobusOutline"]],
      "trailing": [pair, ["kind": "chevron"]],
      "style": ["horizontalPadding": 12, "verticalPadding": 8, "leadingGap": 12,
        "trailingGap": 12, "image": ["width": 32, "height": 32]]]
    try bind(detail, height: 60)
    XCTAssertEqual(trailing.axis, .horizontal)
    let detailControls = trailing.arrangedSubviews.filter { !$0.isHidden }
    XCTAssertEqual(detailControls.count, 2)
    let detailRects = detailControls.map { $0.convert($0.bounds, to: cell) }
    XCTAssertTrue(detailRects.allSatisfy { $0.minY >= 0 && $0.maxY <= 60 })
    XCTAssertEqual(detailRects[0].midY, detailRects[1].midY, accuracy: 1)
    XCTAssertEqual(detailRects[1].minX - detailRects[0].maxX, 12, accuracy: 0.5)
    XCTAssertEqual(detailRects[1].maxX, cell.bounds.maxX - 12, accuracy: 0.5)
    let missing: [String: Any] = ["key": "missing", "type": "identity", "height": 48,
      "title": "Ethereum", "leading": ["kind": "network",
        "fallbackIcon": ["name": "GlobusOutline"]],
      "trailing": [["kind": "icon", "name": "RefreshCcwOutline"], ["kind": "chevron"]],
      "style": ["horizontalPadding": 12, "verticalPadding": 8, "leadingGap": 12,
        "trailingGap": 12, "image": ["width": 32, "height": 32]]]
    try bind(missing, height: 48)
    XCTAssertEqual(trailing.axis, .horizontal)
    let missingControls = trailing.arrangedSubviews.filter { !$0.isHidden }
    XCTAssertEqual(missingControls.count, 2)
    XCTAssertEqual(missingControls[0].bounds.width, 16, accuracy: 0.5)
    XCTAssertEqual(missingControls[1].bounds.width, 24, accuracy: 0.5)
    let missingRects = missingControls.map { $0.convert($0.bounds, to: cell) }
    XCTAssertTrue(missingRects.allSatisfy { $0.minY >= 0 && $0.maxY <= 48 })
    XCTAssertEqual(missingRects[0].midY, missingRects[1].midY, accuracy: 1)
    XCTAssertEqual(missingRects[1].minX - missingRects[0].maxX, 12, accuracy: 0.5)
    XCTAssertEqual(missingRects[1].maxX, cell.bounds.maxX - 12, accuracy: 0.5)
    try bind(detail, height: 60)
    XCTAssertEqual(trailing.axis, .horizontal)
    try bind(["key": "plain", "type": "identity", "title": "Plain", "height": 56,
      "leading": ["kind": "token", "fallbackIcon": ["name": "CryptoCoinOutline"]]],
      height: 56)
    let remainingConstraints = try XCTUnwrap(Mirror(reflecting: cell).children.first {
      $0.label == "selectorConstraints"
    }?.value as? [NSLayoutConstraint])
    XCTAssertTrue(remainingConstraints.isEmpty)
  }

  func testSwapValuePairAndTertiaryMenuIconFitAndKeepActionState() throws {
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 386, height: 60))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 386, height: 120))
    window.addSubview(cell)
    defer { cell.removeFromSuperview() }
    let trailing = try XCTUnwrap(Mirror(reflecting: cell).children.first {
      $0.label == "trailingStack"
    }?.value as? NativeListAccessoryStack)
    var actions: [(String, Int?)] = []
    cell.onAction = { _, action, _, origin in actions.append((action, origin?.slot)) }
    func bind(disabled: Bool) throws {
      let item = try NativeListItem(data: [
        "key": "swap-token", "type": "identity", "height": 60,
        "title": "ETH", "subtitle": "Ethereum",
        "leading": ["kind": "token", "fallbackIcon": ["name": "CryptoCoinOutline"]],
        "trailing": [
          ["kind": "valuePair", "primary": "0.001328", "secondary": "$3.08"],
          ["kind": "icon", "name": "DotVerSolid", "buttonStyle": "tertiary",
           "actionKey": "menu", "disabled": disabled],
        ],
        "style": ["horizontalPadding": 20, "verticalPadding": 8,
          "leadingGap": 12, "trailingGap": 8,
          "image": ["width": 40, "height": 40]],
      ])
      cell.bind(item: item, theme: nil, layout: "linear", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
      window.layoutIfNeeded()
      cell.layoutIfNeeded()
    }
    try bind(disabled: false)
    XCTAssertEqual(trailing.axis, .horizontal)
    let controls = trailing.arrangedSubviews.compactMap { !$0.isHidden ? $0 as? UIButton : nil }
    XCTAssertEqual(controls.count, 2)
    let rects = controls.map { $0.convert($0.bounds, to: cell) }
    XCTAssertTrue(rects.allSatisfy { $0.minY >= 0 && $0.maxY <= cell.bounds.height })
    let amountLabel = try XCTUnwrap(controls[0].titleLabel)
    let amountRect = amountLabel.convert(amountLabel.bounds, to: cell)
    XCTAssertEqual(amountLabel.numberOfLines, 2)
    XCTAssertGreaterThan(amountRect.height, 30)
    XCTAssertGreaterThanOrEqual(amountRect.minY, 0)
    XCTAssertLessThanOrEqual(amountRect.maxY, cell.bounds.height)
    XCTAssertEqual(rects[0].midY, rects[1].midY, accuracy: 1)
    XCTAssertEqual(rects[1].minX - rects[0].maxX, 8, accuracy: 0.5)
    XCTAssertEqual(controls[1].bounds.height, 38, accuracy: 0.5)
    XCTAssertTrue(controls[1].isEnabled)
    try dispatchAccessoryTouchUpInside(controls[1], on: trailing)
    XCTAssertEqual(actions.count, 1)
    let action = try XCTUnwrap(actions.first)
    XCTAssertEqual(action.0, "menu")
    XCTAssertEqual(action.1, 1)

    try bind(disabled: true)
    let disabledIcon = try XCTUnwrap(trailing.arrangedSubviews.filter { !$0.isHidden }.last as? UIButton)
    XCTAssertFalse(disabledIcon.isEnabled)
    try dispatchAccessoryTouchUpInside(disabledIcon, on: trailing)
    XCTAssertEqual(actions.count, 1)
  }

  func testRiskIdentityRatioKeepsTitleVisibleWithAndWithoutButton() throws {
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 386, height: 56))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 386, height: 200))
    window.addSubview(cell)
    let titleLabel = try XCTUnwrap(
      Mirror(reflecting: cell).children.first { $0.label == "titleLabel" }?.value as? UILabel)
    let mainStack = try XCTUnwrap(
      Mirror(reflecting: cell).children.first { $0.label == "mainStack" }?.value as? UIStackView)
    let trailing = try XCTUnwrap(
      Mirror(reflecting: cell).children.first { $0.label == "trailingStack" }?.value
        as? NativeListAccessoryStack)
    let base: [String: Any] = [
      "key": "risk", "type": "identity", "title": "XSR", "height": 56,
      "leading": ["kind": "token", "fallbackIcon": ["name": "CryptoCoinOutline"]],
      "style": [
        "horizontalPadding": 12, "verticalPadding": 8, "leadingGap": 12,
        "leadingContentMaxWidthRatio": 0.6,
        "image": ["width": 40, "height": 40, "shape": "circle"],
        "title": ["fontSize": 16, "lineHeight": 24, "fontWeight": "medium", "lines": 1],
      ],
    ]

    func bind(_ data: [String: Any], capped: Bool = true) throws {
      cell.bind(item: try NativeListItem(data: data), theme: nil, layout: "linear",
        selected: false, checkboxState: { _, _, fallback in fallback },
        fonts: NativeListFontFamilies())
      window.layoutIfNeeded()
      cell.layoutIfNeeded()
      XCTAssertFalse(titleLabel.isHidden)
      XCTAssertGreaterThan(titleLabel.bounds.width, 0.5,
        "A visible token title must not collapse when the row reserves trailing space")
      XCTAssertEqual(titleLabel.bounds.height, 24, accuracy: 0.5)
      let intrinsicWidth = titleLabel.intrinsicContentSize.width
      let capWidth = cell.root.bounds.width * 0.6 - 52
      let requiredTitleWidth = capped ? min(intrinsicWidth, capWidth) : intrinsicWidth
      XCTAssertGreaterThanOrEqual(titleLabel.bounds.width, requiredTitleWidth - 0.5,
        "title=\(data["title"] ?? "") intrinsic=\(intrinsicWidth) "
          + "rendered=\(titleLabel.bounds.width) root=\(cell.root.bounds.width) "
          + "trailing=\(trailing.bounds.width)")
      XCTAssertGreaterThanOrEqual(mainStack.bounds.width, titleLabel.bounds.width - 0.5)
      XCTAssertEqual(trailing.frame.maxX, cell.root.bounds.maxX, accuracy: 0.5)
      if capped {
        XCTAssertLessThanOrEqual(mainStack.bounds.width, cell.root.bounds.width * 0.6 - 52 + 0.5)
      }
    }

    try bind(base)
    XCTAssertNil(trailing.firstVisibleControl)
    try bind(base.merging(["trailing": [[
      "kind": "button", "label": "Show on home", "actionKey": "show",
    ]]]) { _, newer in newer })
    let button = try XCTUnwrap(trailing.firstVisibleControl)
    XCTAssertGreaterThan(button.bounds.width, 100)
    XCTAssertEqual(trailing.frame.minX + button.frame.maxX, cell.root.bounds.maxX, accuracy: 0.5)
    cell.frame.size.height = 88
    try bind(base.merging(["trailing": [
      ["kind": "button", "label": "A", "actionKey": "first"],
      ["kind": "button", "label": "Longer action", "actionKey": "second"],
    ], "height": 88]) { _, newer in newer })
    let visibleButtons = trailing.arrangedSubviews.compactMap { $0 as? NativeListAccessoryButton }
      .filter { !$0.isHidden }
    XCTAssertEqual(visibleButtons.count, 2)
    XCTAssertGreaterThan(visibleButtons[1].bounds.width, visibleButtons[0].bounds.width)
    XCTAssertEqual(trailing.frame.minX + visibleButtons[1].frame.maxX,
      cell.root.bounds.maxX, accuracy: 0.5)
    cell.frame.size.height = 56
    try bind(base.merging(["title": "A very long token symbol that must truncate within the cap"])
      { _, newer in newer })
    XCTAssertLessThan(titleLabel.bounds.width, titleLabel.intrinsicContentSize.width)
    try bind(base)
    XCTAssertNil(trailing.firstVisibleControl)
    try bind(base.merging(["key": "searched-risk", "title": "rkl"]) { _, newer in newer })
    XCTAssertNil(trailing.firstVisibleControl)
    try bind(base)
    var unrestricted = base
    var unrestrictedStyle = try XCTUnwrap(base["style"] as? [String: Any])
    unrestrictedStyle.removeValue(forKey: "leadingContentMaxWidthRatio")
    unrestricted["style"] = unrestrictedStyle
    try bind(unrestricted, capped: false)
    XCTAssertEqual(cell.root.arrangedSubviews.count, 3)
    XCTAssertEqual(mainStack.contentHuggingPriority(for: .horizontal).rawValue, 1, accuracy: 0.1)
    let spacing = cell.root.customSpacing(after: mainStack)
    XCTAssertTrue(spacing == UIStackView.spacingUseDefault || spacing == cell.root.spacing)
  }

  func testAutoHeightIdentityReservesMeasuredTrailingWidth() throws {
    let subtitle = "A long recipient address that must wrap within the space left beside its values and controls"
    func item(
      trailing: [[String: Any]], horizontalPadding: Double? = nil,
      presentation: String = ""
    ) throws -> NativeListItem {
      var style: [String: Any] = ["image": ["width": 40, "height": 40]]
      if let horizontalPadding { style["horizontalPadding"] = horizontalPadding }
      return try NativeListItem(data: [
        "key": "recipient", "type": "identity", "title": "Recipient", "subtitle": subtitle,
        "subtitleLines": 0, "contentHeight": "auto", "style": style,
        "presentation": presentation,
        "trailing": trailing,
      ])
    }
    func height(width: CGFloat, item: NativeListItem) throws -> CGFloat {
      return try XCTUnwrap(NativeListIdentityCell.measure(
        item, width: width, theme: nil, layout: "linear", fonts: NativeListFontFamilies()))
    }

    let pair: [[String: Any]] = [[
      "kind": "valuePair", "primary": "$123,456.78", "secondary": "12345 TOKEN",
    ]]
    let pairAndMenu = pair + [["kind": "menu", "actionKey": "menu"]]
    let pairItem = try item(trailing: pair)
    let pairHeight = try height(width: 240, item: pairItem)
    XCTAssertGreaterThan(pairHeight, try height(width: 240, item: item(trailing: [])))
    XCTAssertGreaterThanOrEqual(try height(width: 240, item: item(trailing: pairAndMenu)),
      pairHeight)
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 240, height: pairHeight))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 240, height: pairHeight))
    window.addSubview(cell)
    cell.bind(item: pairItem, theme: nil, layout: "linear", selected: false,
      checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
    window.layoutIfNeeded()
    cell.layoutIfNeeded()
    let subtitleLabel = try XCTUnwrap(Mirror(reflecting: cell).children.first {
      $0.label == "subtitleLabel"
    }?.value as? UILabel)
    XCTAssertGreaterThan(subtitleLabel.bounds.width, 0.5)
    XCTAssertGreaterThanOrEqual(subtitleLabel.bounds.height + 0.5,
      subtitleLabel.sizeThatFits(CGSize(
        width: subtitleLabel.bounds.width, height: .greatestFiniteMagnitude)).height)
    let accountIcon: [[String: Any]] = [[
      "kind": "icon", "name": "DotHorOutline", "actionKey": "menu",
    ]]
    XCTAssertGreaterThanOrEqual(
      try height(width: 220, item: item(trailing: accountIcon,
        horizontalPadding: 28, presentation: "accountSelector")),
      try height(width: 220, item: item(trailing: accountIcon,
        presentation: "accountSelector")))
  }

  func testFixedFooterReplacesRendererAndKeepsActionRouting() throws {
    let view = NativeListView(frame: CGRect(x: 0, y: 0, width: 320, height: 480))
    var actions: [[String: Any]] = []
    view.onRowAction = { payload in
      let data = Data(payload.utf8)
      if let action = (try? JSONSerialization.jsonObject(with: data)) as? [String: Any] {
        actions.append(action)
      }
    }

    view.applySnapshotJson(try snapshot(footer: [
      "key": "footer", "type": "action", "title": "Continue", "actionKey": "continue",
    ]))
    let first = try XCTUnwrap(footerCell(in: view))
    XCTAssertTrue(first is NativeListActionCell)
    XCTAssertFalse(first.isHidden)
    XCTAssertEqual(first.accessibilityLabel, "Continue")
    let gestureCount = first.gestureRecognizers?.count

    view.applySnapshotJson(try snapshot(footer: [
      "key": "footer", "type": "system", "variant": "retry", "title": "Retry",
      "actionKey": "retry",
    ]))
    let second = try XCTUnwrap(footerCell(in: view))
    XCTAssertTrue(second is NativeListSystemCell)
    XCTAssertFalse(second === first)
    XCTAssertNil(first.superview)
    XCTAssertFalse(second.isHidden)
    XCTAssertEqual(second.accessibilityLabel, "Retry")
    XCTAssertEqual(second.gestureRecognizers?.count, gestureCount)

    second.onAction?(try NativeListItem(data: [
      "key": "footer", "type": "system", "variant": "retry", "title": "Retry",
      "actionKey": "retry",
    ]), "retry", nil, nil)
    XCTAssertEqual(actions.last?["actionKey"] as? String, "retry")

    view.applySnapshotJson(try snapshot(footer: nil))
    XCTAssertTrue(second.isHidden)
  }

  func testFontResolversMeasureBindResetAndKeepIndependentInstances() throws {
    let first = NativeListFontFamilies(names: ["regular": "Courier", "bold": "Courier-Bold"])
    let second = NativeListFontFamilies(names: ["regular": "Georgia", "bold": "Georgia-Bold"])
    XCTAssertNotNil(UIFont(name: "Courier", size: 16))
    XCTAssertNotNil(UIFont(name: "Georgia", size: 16))
    func resolve(_ fonts: NativeListFontFamilies) -> NativeListResolvedText {
      NativeListResolvedText(
        "WWWWiiii", style: nil, size: 16, color: .label,
        lineHeight: 24, lines: 1, fonts: fonts)
    }
    let a = resolve(first)
    let b = resolve(second)
    let label = NativeListTextLabel()
    a.bind(label)
    XCTAssertEqual(label.font.fontName, a.font.fontName)
    XCTAssertEqual(a.measureWidth(), ceil(a.attributed(direction: .leftToRight).size().width))
    XCTAssertNotEqual(a.font.fontName, b.font.fontName)
    XCTAssertNotEqual(a.measureWidth(), b.measureWidth())
    b.bind(label)
    XCTAssertEqual(label.font.fontName, "Georgia")
    resolve(NativeListFontFamilies()).bind(label)
    XCTAssertEqual(label.font.fontName, UIFont.systemFont(ofSize: 16).fontName)
    XCTAssertEqual(a.font.fontName, "Courier")
  }

  func testUnstyledLeadingVisualAndFooterRebindConfiguredFonts() throws {
    let first = NativeListFontFamilies(names: ["regular": "Courier", "bold": "Courier-Bold"])
    let second = NativeListFontFamilies(names: ["regular": "Georgia", "bold": "Georgia-Bold"])
    let visual = NativeListLeadingVisual()
    for fonts in [first, second] {
      visual.bind(
        ["kind": "account", "fallbackText": "A"], style: [:], key: "a", theme: nil,
        isUnread: false, fonts: fonts)
      XCTAssertEqual(
        visual.fallbackTextView.font.fontName, fonts.font(ofSize: 13, weight: .bold).fontName)
    }
    let view = NativeListView(frame: CGRect(x: 0, y: 0, width: 320, height: 480))
    view.setFontFamiliesJson("{\"regular\":\"Courier\",\"medium\":\"Courier\"}")
    view.applySnapshotJson(
      try snapshot(footer: [
        "key": "footer", "type": "action", "title": "Continue", "actionKey": "continue",
      ]))
    let footer = try XCTUnwrap(footerCell(in: view))
    func labels(_ view: UIView) -> [UILabel] {
      (view as? UILabel).map { [$0] } ?? view.subviews.flatMap(labels)
    }
    XCTAssertEqual(labels(footer).first { $0.text == "Continue" }?.font.fontName, "Courier")
    view.setFontFamiliesJson("{\"regular\":\"Georgia\",\"medium\":\"Georgia\"}")
    XCTAssertEqual(labels(footer).first { $0.text == "Continue" }?.font.fontName, "Georgia")
    XCTAssertEqual(footer.accessibilityLabel, "Continue")
  }

  func testPaddedSquareNetworkOverlayRoundsInnerImageAndResetsOnRebind() throws {
    let visual = NativeListLeadingVisual(frame: CGRect(x: 0, y: 0, width: 40, height: 40))
    func bind(_ overlay: [String: Any]) {
      visual.bind(["kind": "token", "overlays": [overlay]], style: [:], key: "overlay",
        theme: nil, isUnread: false, fonts: NativeListFontFamilies())
      visual.layoutIfNeeded()
    }
    let image: [String: Any] = ["uri": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAusB9Y9xvY0AAAAASUVORK5CYII="]
    bind(["position": "bottomRight", "size": 20, "width": 20, "height": 20,
      "padding": 2, "image": image, "fallbackIcon": ["name": "GlobusOutline"]])
    let slots = try XCTUnwrap(Mirror(reflecting: visual).children.first {
      $0.label == "overlaySlots"
    }?.value as? [NativeListImageSlot])
    let slot = try XCTUnwrap(slots.first?.view)
    XCTAssertEqual(slot.bounds.size, CGSize(width: 16, height: 16))
    XCTAssertEqual(slot.layer.cornerRadius, 8, accuracy: 0.5)
    XCTAssertTrue(slot.clipsToBounds)
    bind(["position": "bottomRight", "width": 20, "height": 14,
      "padding": 2, "image": image])
    XCTAssertEqual(slot.layer.cornerRadius, 0)
    XCTAssertFalse(slot.clipsToBounds)
    bind(["position": "bottomRight", "size": 20, "padding": 2, "text": "E"])
    XCTAssertEqual(slot.layer.cornerRadius, 0)
    XCTAssertFalse(slot.clipsToBounds)
    bind(["position": "bottomRight", "size": 20, "padding": 2, "image": image])
    XCTAssertEqual(slot.layer.cornerRadius, 8, accuracy: 0.5)
    visual.recycle()
  }

  func testNestedWalletMembersAndDragBadgeFollowEachBindMapping() throws {
    let group = NativeListWalletGroupCell(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
    let item = try NativeListItem(data: [
      "type": "walletGroup", "key": "group",
      "parent": ["type": "identity", "key": "parent", "title": "Parent", "variant": "wallet"],
      "children": [["type": "identity", "key": "child", "title": "Child", "variant": "wallet"]],
    ])
    func labels(_ view: UIView) -> [UILabel] {
      (view as? UILabel).map { [$0] } ?? view.subviews.flatMap(labels)
    }
    for face in ["Courier", "Georgia"] {
      let fonts = NativeListFontFamilies(names: ["regular": face, "medium": face, "semibold": face])
      group.bind(
        item: item, theme: nil, layout: "linear", selected: false,
        checkboxState: { _, _, fallback in fallback }, fonts: fonts)
      let rendered = labels(group)
      let members = rendered.filter { ["Parent", "Child"].contains($0.text ?? "") }
      XCTAssertGreaterThanOrEqual(members.count, 2)
      XCTAssertTrue(members.allSatisfy { $0.font.fontName == face })
      XCTAssertEqual(rendered.first { $0.text == "+1" }?.font.fontName, face)
    }
  }

  private func snapshot(footer: [String: Any]?) throws -> String {
    var value: [String: Any] = [
      "schemaVersion": 1, "generation": 1, "layout": ["kind": "linear"], "rows": [],
    ]
    if let footer { value["fixedFooter"] = footer }
    let data = try JSONSerialization.data(withJSONObject: value, options: [.sortedKeys])
    return String(decoding: data, as: UTF8.self)
  }

  func testContentShrinkCorrectsRestoredOffsetAfterPagerInsetsAndUpdatesObserver() throws {
    let view = try makeScrollingList()
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInset = UIEdgeInsets(top: 108, left: 0, bottom: 0, right: 0)
    list.contentOffset.y = 600
    var savedLogicalOffset = list.contentOffset.y + list.contentInset.top
    let offsetObserver = list.observe(\.contentOffset, options: [.new]) { list, _ in
      savedLogicalOffset = list.contentOffset.y + list.contentInset.top
    }
    // Model the pager's synchronous content-size observer and cached-offset restoration.
    let sizeObserver = list.observe(\.contentSize, options: [.old, .new]) { list, change in
      if change.oldValue != change.newValue, list.contentSize.height < 200 {
        list.contentInset.bottom = max(0, list.bounds.height - 44 - list.contentSize.height)
        list.contentOffset.y = 600
      }
    }
    view.applySnapshotJson(try scrollingSnapshot(count: 0))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    XCTAssertEqual(list.contentSize.height, 132, accuracy: 0.5)
    XCTAssertEqual(list.contentOffset.y, -44, accuracy: 0.5)
    XCTAssertEqual(savedLogicalOffset, 64, accuracy: 0.5)
    withExtendedLifetime((offsetObserver, sizeObserver)) {}
  }

  func testContentShrinkPreservesInRangeAndNegativePullOffsets() throws {
    for offset in [CGFloat(120), CGFloat(-160)] {
      let view = try makeScrollingList()
      let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
      list.contentInset.top = 108
      view.applySnapshotJson(try scrollingSnapshot(count: 20))
      view.layoutIfNeeded()
      list.layoutIfNeeded()
      // Preserve a valid offset or a refresh pull while shrink correction is queued.
      list.contentOffset.y = offset
      drainMainQueue()
      XCTAssertEqual(list.contentOffset.y, offset, accuracy: 0.5)
    }
  }

  func testContentShrinkWaitsForRefreshCompletion() throws {
    let view = try makeScrollingList()
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    view.applySnapshotJson(try scrollingSnapshot(count: 0, refreshing: true))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    list.contentOffset.y = 600
    drainMainQueue()
    XCTAssertEqual(list.contentOffset.y, 600, accuracy: 0.5)
    view.setRefreshing(false)
    drainMainQueue()
    let inset = list.adjustedContentInset
    let maximum = max(-inset.top, list.contentSize.height - list.bounds.height + inset.bottom)
    XCTAssertEqual(list.contentOffset.y, maximum, accuracy: 0.5)
  }

  func testReactSlotFramesCannotMoveSlotsOutOfNativePlacement() throws {
    let view = NativeListView(frame: CGRect(x: 0, y: 0, width: 320, height: 708))
    // Fabric assigns each root the frame React computed: horizontal inset, top 0.
    let slots = (0..<3).map { _ in UIView(frame: CGRect(x: 16, y: 0, width: 288, height: 44)) }
    for (index, slot) in slots.enumerated() { view.mountContainerSlot(slot, atIndex: index) }
    view.applySnapshotJson(try scrollingSnapshot(count: 3))
    view.layoutIfNeeded()
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.layoutIfNeeded()
    let header = try XCTUnwrap(slots[0].superview)
    XCTAssertTrue(header.clipsToBounds)
    XCTAssertEqual(header.bounds.height, 0, "An unmeasured header cannot draw over the first rows")

    view.setContainerSlotHeightsJson("[44,44,60,0]")
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    XCTAssertEqual(slots[0].convert(CGPoint.zero, to: list), CGPoint(x: 16, y: 0))
    let footerY = 44 + 3 * 80 as CGFloat
    XCTAssertEqual(slots[2].convert(CGPoint.zero, to: list).y, footerY)
    // A React height update rewrites the root frame before the new height reaches native.
    slots[2].frame = CGRect(x: 16, y: 0, width: 288, height: 90)
    XCTAssertEqual(slots[2].convert(CGPoint.zero, to: list), CGPoint(x: 16, y: footerY))
    XCTAssertTrue(slots[1].superview?.isHidden == true, "The empty slot stays hidden with rows")

    view.unmountContainerSlot(slots[2])
    XCTAssertNil(slots[2].superview)
  }

  func testSidecarCentersRowsWhenConfiguredBeforeFirstWidth() throws {
    try assertHomeSidecarGeometry(sidecarBeforeFirstLayout: true)
  }

  func testSidecarCentersRowsWhenConfiguredAfterFirstWidth() throws {
    try assertHomeSidecarGeometry(sidecarBeforeFirstLayout: false)
  }

  func testHomeLinearColumnsStayInsideTheirStackAcrossEmptyMiddleAndReuse() throws {
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 1376, height: 900))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    for index in 0..<4 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.setContainerSlotHeightsJson("[0,0,0,1200]")
    let sidecar = "{\"width\":400,\"gap\":24,\"maxContentWidth\":1140}"
    view.setSidecarLayoutJson(sidecar)

    func columns(middle: String?, two: Bool = false) -> [[String: Any]] {
      let first: [String: Any] = ["key": "asset", "text": "USD1",
        "secondaryText": "Public fixture", "weight": 1]
      let balance: [String: Any] = ["key": "balance", "text": middle ?? "",
        "secondaryText": middle == nil ? "" : "$0.01", "alignment": "end", "weight": 1]
      let price: [String: Any] = ["key": "price", "text": "$1.00",
        "secondaryText": "0.01%", "alignment": "end", "weight": 1]
      return two ? [first, price] : [first, balance, price]
    }
    let rows: [[String: Any]] = (0..<30).map { index in
      ["key": "row-\(index)", "type": "dataRow", "height": 60,
       "leading": ["kind": "token", "fallbackIcon": ["name": "CryptoCoinOutline"]],
       "columns": index == 20 ? columns(middle: "0.01") : homeSidecarColumns(index, updated: false),
       "trailing": [["kind": "button", "label": "Swap", "actionKey": "swap"]],
       "style": ["horizontalPadding": 12, "verticalPadding": 8, "leadingGap": 12,
         "columns": ["token": "$bodyLg", "fontWeight": "medium", "lines": 1],
         "columnSecondary": ["token": "$bodyMd", "lines": 1],
         "image": ["width": 32, "height": 32]]]
    }
    let snapshot: [String: Any] = ["schemaVersion": 1, "generation": 1,
      "layout": ["kind": "linear", "contentPaddingHorizontal": 8], "rows": rows]
    view.applySnapshotJson(String(decoding: try JSONSerialization.data(withJSONObject: snapshot),
      as: UTF8.self))
    list.contentInsetAdjustmentBehavior = .never
    func layout() {
      window.layoutIfNeeded()
      view.layoutIfNeeded()
      list.layoutIfNeeded()
    }
    func check(_ phase: String, count: Int, width: CGFloat, weights: [Int]? = nil) throws {
      layout()
      let cell = try XCTUnwrap(list.cellForItem(at: IndexPath(item: 20, section: 0))
        as? NativeListDataRowCell, phase)
      let stack = try XCTUnwrap(cell.root.arrangedSubviews[3] as? UIStackView, phase)
      let visible = stack.arrangedSubviews.filter { !$0.isHidden }
      XCTAssertEqual(stack.arrangedSubviews.count, 4, phase)
      XCTAssertEqual(stack.arrangedSubviews.map(\.isHidden),
        (0..<4).map { $0 >= count }, phase)
      XCTAssertEqual(visible.count, count, phase)
      let cellFrame = cell.convert(cell.bounds, to: list)
      let stackFrame = stack.convert(stack.bounds, to: list)
      let attributes = try XCTUnwrap(list.layoutAttributesForItem(
        at: IndexPath(item: 20, section: 0)), phase)
      XCTAssertEqual(attributes.frame.width, width, accuracy: 1, phase)
      XCTAssertEqual(cellFrame.width, width, accuracy: 1,
        "\(phase) window=\(window.frame) controller=\(controller.view.frame) view=\(view.frame) list=\(list.frame) bounds=\(list.bounds) attributes=\(attributes.frame)")
      let rowConstraints = Mirror(reflecting: cell).children.first {
        $0.label == "rowConstraints"
      }?.value as? [NSLayoutConstraint] ?? []
      let columnWidthConstraints = Mirror(reflecting: cell).children.first {
        $0.label == "columnWidthConstraints"
      }?.value as? [NSLayoutConstraint] ?? []
      let constraints = rowConstraints + columnWidthConstraints
      let constraintState = constraints.map { constraint in
        let first = stack.arrangedSubviews.firstIndex {
          $0 === constraint.firstItem as? UIView
        } ?? -1
        let second = stack.arrangedSubviews.firstIndex {
          $0 === constraint.secondItem as? UIView
        } ?? -1
        return "\(first):\(second):\(constraint.multiplier):\(constraint.isActive)"
      }.joined(separator: ",")
      let hidingState = stack.arrangedSubviews.enumerated().map { index, column in
        let hiding = (stack.constraints + column.constraints).filter {
          $0.isActive && $0.identifier == "UISV-hiding"
        }.map { "\($0.constant)" }.joined(separator: ",")
        return "\(index):\(column.isHidden):\(hiding)"
      }.joined(separator: ",")
      let geometry = "\(phase) model=\(count) cell=\(cellFrame) stack=\(stackFrame) "
        + "columns=\(stack.arrangedSubviews.map { "\($0.isHidden):\($0.convert($0.bounds, to: list))" }) "
        + "constraints=\(constraintState) hiding=\(hidingState)"
      XCTAssertGreaterThanOrEqual(stackFrame.minX, cellFrame.minX - 0.5, geometry)
      XCTAssertLessThanOrEqual(stackFrame.maxX, cellFrame.maxX + 0.5, geometry)
      for column in visible {
        let columnFrame = column.convert(column.bounds, to: list)
        XCTAssertGreaterThan(columnFrame.width, 0, geometry)
        XCTAssertGreaterThanOrEqual(columnFrame.minX, stackFrame.minX - 0.5, geometry)
        XCTAssertLessThanOrEqual(columnFrame.maxX, stackFrame.maxX + 0.5, geometry)
        let primaryLine = (column as? UIStackView)?.arrangedSubviews.first
        if let primaryLine {
          let lineFrame = primaryLine.convert(primaryLine.bounds, to: list)
          XCTAssertGreaterThanOrEqual(lineFrame.minX, columnFrame.minX - 0.5, geometry)
          XCTAssertLessThanOrEqual(lineFrame.maxX, columnFrame.maxX + 0.5, geometry)
        }
        for label in labels(in: column) where !label.isHidden {
          let labelFrame = label.convert(label.bounds, to: list)
          XCTAssertGreaterThanOrEqual(labelFrame.minX, columnFrame.minX - 0.5,
            "\(geometry) label=\(labelFrame)")
          XCTAssertLessThanOrEqual(labelFrame.maxX, columnFrame.maxX + 0.5,
            "\(geometry) label=\(labelFrame)")
        }
      }
      if count >= 2 {
        let expectedWeights = weights ?? Array(repeating: 1, count: count)
        XCTAssertEqual(expectedWeights.count, count, geometry)
        for index in 1..<count {
          XCTAssertEqual(visible[index].bounds.width,
            visible[0].bounds.width * CGFloat(expectedWeights[index])
              / CGFloat(expectedWeights[0]),
            accuracy: 1, geometry)
          let previous = visible[index - 1].convert(visible[index - 1].bounds, to: stack)
          let current = visible[index].convert(visible[index].bounds, to: stack)
          XCTAssertGreaterThanOrEqual(current.minX, previous.maxX - 0.5, geometry)
          XCTAssertEqual(current.minX - previous.maxX, stack.spacing,
            accuracy: 0.5, geometry)
        }
      }
    }
    func patch(_ next: [[String: Any]]) throws {
      let patches: [[String: Any]] = [["key": "row-20", "type": "dataRow",
        "changes": ["columns": next]]]
      view.applyPatchesJson(String(decoding: try JSONSerialization.data(withJSONObject: patches),
        as: UTF8.self))
    }
    func widthConstraints() throws -> [NSLayoutConstraint] {
      let cell = try XCTUnwrap(list.cellForItem(at: IndexPath(item: 20, section: 0))
        as? NativeListDataRowCell)
      return try XCTUnwrap(Mirror(reflecting: cell).children.first {
        $0.label == "columnWidthConstraints"
      }?.value as? [NSLayoutConstraint])
    }
    func sizeConstraints() throws -> [NSLayoutConstraint] {
      let cell = try XCTUnwrap(list.cellForItem(at: IndexPath(item: 20, section: 0))
        as? NativeListDataRowCell)
      return try XCTUnwrap(Mirror(reflecting: cell).children.first {
        $0.label == "rowConstraints"
      }?.value as? [NSLayoutConstraint])
    }
    layout()
    list.setContentOffset(CGPoint(x: 0, y: 18 * 60), animated: false)
    try check("three populated", count: 3, width: 700)
    try patch(columns(middle: nil))
    try check("three empty middle", count: 3, width: 700)
    try patch(columns(middle: "0.02"))
    try check("three restored", count: 3, width: 700)
    try patch(columns(middle: nil, two: true))
    try check("two columns", count: 2, width: 700)
    try patch(columns(middle: nil))
    try check("three after two", count: 3, width: 700)
    let stableConstraints = try widthConstraints()
    let stableSizeConstraints = try sizeConstraints()
    XCTAssertEqual(stableConstraints.count, 2)
    XCTAssertEqual(stableSizeConstraints.count, 4)
    for update in 0..<10 {
      var next = columns(middle: nil)
      next[2]["text"] = "$1.\(update)"
      try patch(next)
      try check("same-shape price \(update)", count: 3, width: 700)
      let retained = try widthConstraints()
      XCTAssertEqual(retained.count, stableConstraints.count)
      XCTAssertTrue(zip(stableConstraints, retained).allSatisfy { $0 === $1 })
      XCTAssertTrue(retained.allSatisfy(\.isActive))
      let size = try sizeConstraints()
      XCTAssertEqual(size.count, stableSizeConstraints.count)
      XCTAssertTrue(zip(stableSizeConstraints, size).allSatisfy { $0 === $1 })
      XCTAssertTrue(size.allSatisfy(\.isActive))
    }
    var equivalentWeights = columns(middle: nil)
    for index in equivalentWeights.indices { equivalentWeights[index]["weight"] = 2 }
    try patch(equivalentWeights)
    try check("equivalent weights", count: 3, width: 700, weights: [2, 2, 2])
    let equivalentConstraints = try widthConstraints()
    XCTAssertEqual(equivalentConstraints.count, stableConstraints.count)
    XCTAssertTrue(zip(stableConstraints, equivalentConstraints).allSatisfy { $0 === $1 })
    var unequalWeights = columns(middle: nil)
    unequalWeights[0]["weight"] = 2
    try patch(unequalWeights)
    try check("unequal weights", count: 3, width: 700, weights: [2, 1, 1])
    let unequalConstraints = try widthConstraints()
    XCTAssertEqual(unequalConstraints.count, 2)
    XCTAssertTrue(stableConstraints.allSatisfy { !$0.isActive })
    try patch(columns(middle: nil, two: true))
    try check("two after weights", count: 2, width: 700)
    let twoConstraints = try widthConstraints()
    XCTAssertEqual(twoConstraints.count, 1)
    XCTAssertTrue(unequalConstraints.allSatisfy { !$0.isActive })
    try patch([])
    try check("zero columns", count: 0, width: 700)
    XCTAssertTrue((try widthConstraints()).isEmpty)
    XCTAssertTrue(twoConstraints.allSatisfy { !$0.isActive })
    try patch(columns(middle: nil))
    try check("three after zero", count: 3, width: 700)
    list.setContentOffset(.zero, animated: false)
    layout()
    XCTAssertFalse(list.indexPathsForVisibleItems.contains(IndexPath(item: 20, section: 0)))
    list.setContentOffset(CGPoint(x: 0, y: 18 * 60), animated: false)
    try check("after scroll reuse", count: 3, width: 700)
    window.frame.size.width = 1032
    controller.view.frame = window.bounds
    view.frame = window.bounds
    view.setSidecarLayoutJson("")
    drainMainQueue()
    try check("narrow no sidecar", count: 3, width: 1016)
    window.frame.size.width = 1376
    controller.view.frame = window.bounds
    view.frame = window.bounds
    view.setSidecarLayoutJson(sidecar)
    drainMainQueue()
    try check("wide sidecar restored", count: 3, width: 700)
    func animatedPatch(_ next: [[String: Any]], phase: String, count: Int) throws {
      let patches: [[String: Any]] = [["key": "row-20", "type": "dataRow",
        "changes": ["columns": next]]]
      let json = String(decoding: try JSONSerialization.data(withJSONObject: patches), as: UTF8.self)
      let finished = expectation(description: "Home column animation: \(phase)")
      UIView.animate(withDuration: 0.2, delay: 0, options: .beginFromCurrentState) {
        view.applyPatchesJson(json)
        view.layoutIfNeeded()
        list.layoutIfNeeded()
      } completion: { _ in finished.fulfill() }
      wait(for: [finished], timeout: 1)
      try check(phase, count: count, width: 700)
      waitForStructuralFrames(0.1)
      try check("\(phase) stable", count: count, width: 700)
    }
    try animatedPatch(columns(middle: nil, two: true), phase: "animated two", count: 2)
    try animatedPatch(columns(middle: nil), phase: "animated three", count: 3)
    try animatedPatch(columns(middle: "0.06"), phase: "animated nonempty", count: 3)
  }

  func testStructuralShrinkUsesTheTallerFinalColumnForOffsetClamping() throws {
    let cases: [(name: String, sidecar: Int, offset: CGFloat, height: CGFloat,
      zeroInset: Bool)] = [
      ("sidecar stays taller", 2200, 1450, 2200, false),
      ("left column stays taller", 1000, 1100, 1560, false),
      ("left column zero inset", 1000, 1100, 1560, true),
      ("sidecar becomes taller", 1700, 1050, 1700, false),
    ]
    for testCase in cases {
      let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 1000, height: 600))
      let controller = UIViewController()
      window.rootViewController = controller
      let view = NativeListView(frame: window.bounds)
      controller.view.addSubview(view)
      window.isHidden = false
      defer {
        view.dispose()
        view.removeFromSuperview()
        window.isHidden = true
        window.rootViewController = nil
      }
      let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
      // Keep the viewport insets fixed while testing structural offset clamping.
      let explicitInset = testCase.zeroInset ? UIEdgeInsets.zero :
        UIEdgeInsets(top: 62, left: 0, bottom: 34, right: 0)
      list.contentInsetAdjustmentBehavior = .never
      list.contentInset = explicitInset
      view.mountContainerSlot(UIView(), atIndex: 0)
      view.mountContainerSlot(UIView(), atIndex: 1)
      let footer = UIView()
      view.mountContainerSlot(footer, atIndex: 2)
      let sidecar = UIView()
      view.mountContainerSlot(sidecar, atIndex: 3)
      view.setContainerSlotHeightsJson("[0,0,60,\(testCase.sidecar)]")
      view.setSidecarLayoutJson("{\"width\":300,\"gap\":24,\"maxContentWidth\":1000}")
      view.applySnapshotJson(try structuralRowsSnapshot(count: 30))
      view.layoutIfNeeded()
      list.layoutIfNeeded()
      drainMainQueue()
      XCTAssertEqual(list.adjustedContentInset, explicitInset, testCase.name)
      XCTAssertEqual(list.contentSize.height, max(1860, CGFloat(testCase.sidecar)),
        accuracy: 1, testCase.name)
      list.contentOffset.y = testCase.offset
      list.layoutIfNeeded()
      XCTAssertEqual(list.contentOffset.y, testCase.offset, accuracy: 1, testCase.name)

      view.applySnapshotJson(try structuralRowsSnapshot(count: 25, transitionID: testCase.name))
      let completed = expectation(description: "Native structural display link: \(testCase.name)")
      DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) { completed.fulfill() }
      wait(for: [completed], timeout: 1)
      list.layoutIfNeeded()
      XCTAssertEqual(list.adjustedContentInset, explicitInset, testCase.name)
      XCTAssertEqual(list.contentSize.height, testCase.height, accuracy: 1, testCase.name)
      let inset = list.adjustedContentInset
      let finalMaximum = max(-inset.top, testCase.height - list.bounds.height + inset.bottom)
      let footerTop: CGFloat = 1500
      let footerBottom: CGFloat = footerTop + 60
      let footerVisibleMinimum = footerBottom - list.bounds.height + inset.bottom
      let footerVisibleMaximum = footerTop - inset.top
      let expectedOffset = min(max(testCase.offset, footerVisibleMinimum),
        min(footerVisibleMaximum, finalMaximum))
      XCTAssertEqual(list.contentOffset.y, expectedOffset, accuracy: 1, testCase.name)
      XCTAssertNotNil(sidecar.superview, testCase.name)
      XCTAssertEqual(footer.convert(.zero, to: list).y, 1500, accuracy: 1, testCase.name)
      waitForStructuralFrames(0.1)
      XCTAssertEqual(list.adjustedContentInset, explicitInset, testCase.name)
      XCTAssertEqual(list.contentSize.height, testCase.height, accuracy: 1, testCase.name)
      XCTAssertEqual(list.contentOffset.y, expectedOffset, accuracy: 1, testCase.name)
    }
  }

  func testStructuralTransitionFromGridOrHorizontalCommitsAtomically() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 400, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    view.applySnapshotJson(try structuralRowsSnapshot(
      count: 6, generation: 1, layoutKind: "grid"))
    view.layoutIfNeeded()
    view.applySnapshotJson(try structuralRowsSnapshot(
      count: 8, generation: 2, transitionID: "from-grid", layoutKind: "linear"))
    XCTAssertNil(Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink)
    XCTAssertEqual(list.numberOfItems(inSection: 0), 8)

    view.applySnapshotJson(try structuralRowsSnapshot(
      count: 6, generation: 3, orientation: "horizontal"))
    view.layoutIfNeeded()
    view.applySnapshotJson(try structuralRowsSnapshot(
      count: 8, generation: 4, transitionID: "from-horizontal", layoutKind: "table"))
    XCTAssertNil(Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink)
    XCTAssertEqual(list.numberOfItems(inSection: 0), 8)
  }

  func testVisibleNativeCellsRemainBoundedAsRowsGrowTenfold() throws {
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer { window.isHidden = true }
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    func sampleCells(count: Int) throws -> [(visible: Int, attached: Int)] {
      view.applySnapshotJson(try structuralRowsSnapshot(count: count))
      view.layoutIfNeeded()
      list.layoutIfNeeded()
      let lastOffset = CGFloat(count * 60 - 600)
      return [CGFloat(0), lastOffset / 2, lastOffset].map { offset in
        list.setContentOffset(CGPoint(x: 0, y: offset), animated: false)
        list.layoutIfNeeded()
        return (list.visibleCells.count,
          list.subviews.compactMap { $0 as? NativeListRowHost }.count)
      }
    }
    let hundredCells = try sampleCells(count: 100)
    let thousandCells = try sampleCells(count: 1000)
    print("NativeList 100 visible/attached first/mid/tail: \(hundredCells)")
    print("NativeList 1000 visible/attached first/mid/tail: \(thousandCells)")
    XCTAssertTrue(hundredCells.allSatisfy {
      $0.visible > 0 && $0.visible <= 24 && $0.attached > 0 && $0.attached <= 24
    })
    XCTAssertTrue(thousandCells.allSatisfy {
      $0.visible > 0 && $0.visible <= 24 && $0.attached > 0 && $0.attached <= 24
    })
    XCTAssertLessThanOrEqual(thousandCells.map(\.attached).max() ?? 0,
      (hundredCells.map(\.attached).max() ?? 0) + 4)
  }

  func testStructuralTransitionRapidReversalKeepsRetiringContentAtNaturalHeight() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer { view.dispose(); view.removeFromSuperview(); window.isHidden = true }
    let footer = UIView()
    for index in 0..<2 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.mountContainerSlot(footer, atIndex: 2)
    view.setContainerSlotHeightsJson("[0,0,60,0]")
    view.applySnapshotJson(try structuralRowsSnapshot(count: 25, generation: 1))
    view.layoutIfNeeded()
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.layoutIfNeeded()
    drainMainQueue()
    list.contentOffset.y = 900
    list.layoutIfNeeded()

    view.applySnapshotJson(try structuralRowsSnapshot(count: 30, generation: 2,
      transitionID: "expand-first", durationMs: 400))
    waitForStructuralFrames(0.08)
    list.contentOffset.y = 1100
    list.layoutIfNeeded()
    view.applySnapshotJson(try structuralRowsSnapshot(count: 25, generation: 3,
      transitionID: "collapse-middle", durationMs: 400))
    waitForStructuralFrames(0.08)
    list.layoutIfNeeded()
    let source = try XCTUnwrap(list.dataSource as? UICollectionViewDiffableDataSource<Int, String>)
    let flow = try XCTUnwrap(list.collectionViewLayout as? NativeListFlowLayout)
    let clip = try XCTUnwrap(flow.tailClip)
    let retiring = try XCTUnwrap(list.cellForItem(at: IndexPath(item: 25, section: 0)) as? NativeListRowHost)
    XCTAssertGreaterThan(clip.endY, 1500)
    XCTAssertLessThan(clip.endY, 1800)
    XCTAssertEqual(retiring.bounds.height, 60, accuracy: 1)
    XCTAssertEqual(retiring.contentView.bounds.height, 60, accuracy: 1)
    XCTAssertNil(retiring.structuralContentHeight)
    XCTAssertEqual(retiring.alpha, 1, accuracy: 0.01)
    XCTAssertFalse(retiring.isUserInteractionEnabled)
    XCTAssertTrue(retiring.accessibilityElementsHidden)
    XCTAssertEqual(footer.convert(.zero, to: list).y,
      clip.endY, accuracy: 1)

    view.applySnapshotJson(try structuralRowsSnapshot(count: 30, generation: 4,
      transitionID: "expand-last", durationMs: 400))
    waitForStructuralFrames(0.5)
    list.layoutIfNeeded()
    let keys = source.snapshot().itemIdentifiers
    XCTAssertEqual(keys, (0..<30).map { "row-\($0)" })
    XCTAssertEqual(Set(keys).count, 30)
    XCTAssertEqual(list.contentSize.height, 1860, accuracy: 1)
    XCTAssertEqual(footer.convert(.zero, to: list).y, 1800, accuracy: 1)
    XCTAssertGreaterThanOrEqual(list.contentOffset.y, -list.adjustedContentInset.top - 1)
    XCTAssertLessThanOrEqual(list.contentOffset.y,
      list.contentSize.height - list.bounds.height + list.adjustedContentInset.bottom + 1)
    for case let cell as NativeListRowHost in list.visibleCells {
      XCTAssertEqual(cell.alpha, 1, accuracy: 0.01)
      XCTAssertNil(cell.structuralContentHeight)
    }
  }

  func testHybridSettlementListenerFollowsRegistration() throws {
    let hybrid = HybridNativeList()
    let host = try XCTUnwrap(hybrid.view as? NativeListView)
    var first: [String] = []
    var second: [String] = []
    XCTAssertNil(host.onStructuralTransitionSettled)

    hybrid.onStructuralTransitionSettled = { first.append($0) }
    host.onStructuralTransitionSettled?("first")
    XCTAssertEqual(first, ["first"])

    hybrid.onStructuralTransitionSettled = { second.append($0) }
    host.onStructuralTransitionSettled?("second")
    XCTAssertEqual(first, ["first"])
    XCTAssertEqual(second, ["second"])

    hybrid.onStructuralTransitionSettled = nil
    XCTAssertNil(host.onStructuralTransitionSettled)
    hybrid.dispose()
  }

  func testDisclosureWithoutListenerStillSettlesWithoutReplay() throws {
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    for index in 0..<6 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.setContainerSlotHeightsJson("[0,0,60,0,100,40]")
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 1,
      disclosureExpanded: false))
    view.layoutIfNeeded()

    let first = try structuralRowsSnapshot(count: 6, generation: 2,
      transitionID: "without-listener", durationMs: 0, disclosureExpanded: true)
    view.applySnapshotJson(first)
    drainMainQueue()
    XCTAssertEqual(Mirror(reflecting: view).descendant("pendingDisclosureSettlement")
      .map { Mirror(reflecting: $0).children.count }, 0)

    var settlements: [[String: Any]] = []
    view.onStructuralTransitionSettled = { payload in
      guard let data = payload.data(using: .utf8),
            let value = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
        return
      }
      settlements.append(value)
    }
    view.applySnapshotJson(first)
    drainMainQueue()
    XCTAssertTrue(settlements.isEmpty)

    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 3,
      transitionID: "with-listener", durationMs: 0, disclosureExpanded: false))
    drainMainQueue()
    XCTAssertEqual(settlements.count, 1)
    XCTAssertEqual(settlements.first?["transitionId"] as? String, "with-listener")
    XCTAssertEqual(settlements.first?["status"] as? String, "completed")
  }

  func testDisclosureInitialEndpoints() throws {
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    let slots = (0..<6).map { _ in UIView() }
    for (index, slot) in slots.enumerated() { view.mountContainerSlot(slot, atIndex: index) }
    view.setContainerSlotHeightsJson("[0,0,140,0,100,40]")
    var settlements: [String] = []
    view.onStructuralTransitionSettled = { settlements.append($0) }
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInsetAdjustmentBehavior = .never
    list.contentInset = .zero

    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 1,
      disclosureExpanded: false))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    let footerHost = try XCTUnwrap(slots[2].superview)
    let contentHost = try XCTUnwrap(slots[4].superview)
    let toggleHost = try XCTUnwrap(slots[5].superview)
    XCTAssertEqual(contentHost.frame.minY, 360, accuracy: 1)
    XCTAssertEqual(contentHost.bounds.height, 100, accuracy: 1)
    XCTAssertTrue(contentHost.isHidden)
    XCTAssertFalse(contentHost.isUserInteractionEnabled)
    XCTAssertTrue(contentHost.accessibilityElementsHidden)
    XCTAssertEqual(toggleHost.frame.minY, 360, accuracy: 1)
    XCTAssertEqual(footerHost.frame.minY, 400, accuracy: 1)
    XCTAssertEqual(list.contentSize.height, 540, accuracy: 1)
    XCTAssertTrue(toggleHost.point(inside: CGPoint(x: 10, y: 10), with: nil))
    XCTAssertTrue(settlements.isEmpty)

    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 2,
      disclosureExpanded: true))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    XCTAssertEqual(contentHost.frame.minY, 360, accuracy: 1)
    XCTAssertEqual(contentHost.bounds.height, 100, accuracy: 1)
    XCTAssertFalse(contentHost.isHidden)
    XCTAssertTrue(contentHost.isUserInteractionEnabled)
    XCTAssertFalse(contentHost.accessibilityElementsHidden)
    XCTAssertEqual(toggleHost.frame.minY, 460, accuracy: 1)
    XCTAssertEqual(footerHost.frame.minY, 520, accuracy: 1)
    XCTAssertEqual(list.contentSize.height, 660, accuracy: 1)
    XCTAssertTrue(settlements.isEmpty)
  }

  func testDisclosureCollapseClock() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    let slots = (0..<6).map { _ in UIView() }
    let expandedChild = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 100))
    slots[4].addSubview(expandedChild)
    let toggle = UIButton(frame: CGRect(x: 0, y: 0, width: 320, height: 40))
    for index in 0..<6 {
      view.mountContainerSlot(index == 5 ? toggle : slots[index], atIndex: index)
    }
    view.setContainerSlotHeightsJson("[0,0,140,0,100,40]")
    var settlements: [[String: Any]] = []
    view.onStructuralTransitionSettled = { payload in
      guard let data = payload.data(using: .utf8),
            let object = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
        return
      }
      settlements.append(object)
    }
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInsetAdjustmentBehavior = .never
    list.contentInset = .zero
    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 1,
      disclosureExpanded: true))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    list.contentOffset.y = 2400
    list.layoutIfNeeded()

    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 2,
      transitionID: "disclosure-collapse", durationMs: 800,
      disclosureExpanded: false))
    waitForStructuralFrames(0.16)
    list.layoutIfNeeded()
    let flow = try XCTUnwrap(list.collectionViewLayout as? NativeListFlowLayout)
    let clip = try XCTUnwrap(flow.tailClip)
    let contentHost = try XCTUnwrap(slots[4].superview)
    let toggleHost = try XCTUnwrap(toggle.superview)
    let footerHost = try XCTUnwrap(slots[2].superview)
    XCTAssertEqual(contentHost.bounds.height, 100, accuracy: 1)
    XCTAssertEqual(contentHost.frame.minY, clip.endY, accuracy: 1)
    let contentVisible = toggleHost.frame.minY - contentHost.frame.minY
    XCTAssertGreaterThan(contentVisible, 0)
    XCTAssertLessThan(contentVisible, 100)
    XCTAssertEqual(try XCTUnwrap(contentHost.layer.mask).frame.height,
      contentVisible, accuracy: 1)
    XCTAssertFalse(contentHost.isUserInteractionEnabled)
    XCTAssertTrue(contentHost.accessibilityElementsHidden)
    XCTAssertTrue(toggleHost.isUserInteractionEnabled)
    XCTAssertFalse(toggleHost.accessibilityElementsHidden)
    XCTAssertTrue(toggleHost.point(inside: CGPoint(x: 10, y: 10), with: nil))
    XCTAssertEqual(toggleHost.frame.minY, clip.endY + contentVisible, accuracy: 1)
    let inset = footerHost.frame.minY - toggleHost.frame.maxY
    XCTAssertGreaterThan(inset, 0)
    XCTAssertLessThan(inset, 20)
    XCTAssertTrue(settlements.isEmpty)

    waitForStructuralFrames(0.75)
    list.layoutIfNeeded()
    XCTAssertNil(flow.tailClip)
    XCTAssertEqual(toggleHost.frame.minY, 360, accuracy: 1)
    XCTAssertEqual(footerHost.frame.minY, 400, accuracy: 1)
    XCTAssertTrue(contentHost.isHidden)
    XCTAssertEqual(settlements.count, 1)
    XCTAssertEqual(settlements.first?["transitionId"] as? String, "disclosure-collapse")
    XCTAssertEqual(settlements.first?["status"] as? String, "completed")
    XCTAssertEqual(settlements.first?["settledExpanded"] as? Bool, false)

    let roots = (0..<6).map { $0 == 5 ? toggle : slots[$0] }
    let hosts = roots.map(\.superview)
    XCTAssertTrue(hosts.allSatisfy { $0 != nil })
    let toggleWindowOrigin = toggleHost.convert(CGPoint.zero, to: window)
    let footerWindowOrigin = footerHost.convert(CGPoint.zero, to: window)
    let settledOffset = list.contentOffset
    let settledSize = list.contentSize
    expandedChild.removeFromSuperview()
    view.setContainerSlotHeightsJson("[0,0,140,0,0,40]")
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    for (root, host) in zip(roots, hosts) {
      XCTAssertTrue(root.superview === host)
      XCTAssertTrue(root.window === window)
    }
    XCTAssertEqual(toggleHost.convert(CGPoint.zero, to: window).x, toggleWindowOrigin.x, accuracy: 1)
    XCTAssertEqual(toggleHost.convert(CGPoint.zero, to: window).y, toggleWindowOrigin.y, accuracy: 1)
    XCTAssertEqual(footerHost.convert(CGPoint.zero, to: window).x, footerWindowOrigin.x, accuracy: 1)
    XCTAssertEqual(footerHost.convert(CGPoint.zero, to: window).y, footerWindowOrigin.y, accuracy: 1)
    XCTAssertEqual(list.contentOffset.x, settledOffset.x, accuracy: 1)
    XCTAssertEqual(list.contentOffset.y, settledOffset.y, accuracy: 1)
    XCTAssertEqual(list.contentSize.width, settledSize.width, accuracy: 1)
    XCTAssertEqual(list.contentSize.height, settledSize.height, accuracy: 1)
    XCTAssertEqual(settlements.count, 1)
  }

  func testDisclosureAckLatest() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    let slots = (0..<6).map { _ in UIView() }
    for (index, slot) in slots.enumerated() { view.mountContainerSlot(slot, atIndex: index) }
    view.setContainerSlotHeightsJson("[0,0,140,0,100,40]")
    var settlements: [(String, String, Bool)] = []
    let latestSettled = expectation(description: "Latest disclosure transition settled")
    view.onStructuralTransitionSettled = { payload in
      guard let data = payload.data(using: .utf8),
            let object = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
            let id = object["transitionId"] as? String,
            let status = object["status"] as? String,
            let expanded = object["settledExpanded"] as? Bool else { return }
      settlements.append((id, status, expanded))
      if id == "collapse-latest" { latestSettled.fulfill() }
    }
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 1,
      disclosureExpanded: false))
    view.layoutIfNeeded()
    drainMainQueue()
    XCTAssertTrue(settlements.isEmpty)

    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 2,
      transitionID: "same-rows", durationMs: 400, disclosureExpanded: false))
    drainMainQueue()
    XCTAssertEqual(settlements.map(\.0), ["same-rows"])
    XCTAssertEqual(settlements.first?.1, "completed")
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 3,
      transitionID: "same-rows", durationMs: 400, disclosureExpanded: false))
    drainMainQueue()
    XCTAssertEqual(settlements.count, 1)

    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 4,
      transitionID: "expand-superseded", durationMs: 400,
      disclosureExpanded: true))
    // Supersede on the same main-thread turn: a timed wait can outlast the
    // expansion on a busy simulator and legitimately deliver its settlement.
    XCTAssertNotNil(Mirror(reflecting: view).descendant("structuralDisplayLink")
      as? CADisplayLink)
    XCTAssertEqual(settlements.map(\.0), ["same-rows"])
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 5,
      transitionID: "collapse-latest", durationMs: 400,
      disclosureExpanded: false))
    wait(for: [latestSettled], timeout: 2)
    XCTAssertEqual(settlements.map(\.0), ["same-rows", "collapse-latest"])
    XCTAssertEqual(settlements.last?.1, "completed")
    XCTAssertEqual(settlements.last?.2, false)
    XCTAssertTrue(slots[4].superview?.isHidden ?? false)

    view.applySnapshotJson(try structuralRowsSnapshot(count: 0, generation: 6,
      transitionID: "empty-immediate", durationMs: 0,
      disclosureExpanded: false))
    drainMainQueue()
    XCTAssertEqual(settlements.map(\.0),
      ["same-rows", "collapse-latest", "empty-immediate"])
    XCTAssertEqual(settlements.last?.1, "completed")
  }

  func testDisclosureGeometryFallbackUsesLatestSnapshot() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    let slots = (0..<6).map { _ in UIView() }
    for (index, slot) in slots.enumerated() { view.mountContainerSlot(slot, atIndex: index) }
    view.setContainerSlotHeightsJson("[0,0,140,0,100,40]")
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInsetAdjustmentBehavior = .never
    var settlements: [(String, String)] = []
    view.onStructuralTransitionSettled = { payload in
      guard let data = payload.data(using: .utf8),
            let object = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
            let id = object["transitionId"] as? String,
            let status = object["status"] as? String else { return }
      settlements.append((id, status))
    }
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 1,
      disclosureExpanded: false))
    view.layoutIfNeeded()
    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 2,
      transitionID: "expand", durationMs: 800, disclosureExpanded: true))
    waitForStructuralFrames(0.08)
    XCTAssertNotNil(Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink)

    view.setContainerSlotHeightsJson("[0,0,160,0,100,40]", followedBySnapshot: true)
    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 3,
      transitionID: "expand", durationMs: 800, disclosureExpanded: true))
    drainMainQueue()
    XCTAssertNil(Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink)
    XCTAssertNil((list.collectionViewLayout as? NativeListFlowLayout)?.tailClip)
    let contentHost = try XCTUnwrap(slots[4].superview)
    let toggleHost = try XCTUnwrap(slots[5].superview)
    let footerHost = try XCTUnwrap(slots[2].superview)
    XCTAssertEqual(contentHost.bounds.height, 100, accuracy: 1)
    XCTAssertEqual(toggleHost.frame.minY, 3_100, accuracy: 1)
    XCTAssertEqual(footerHost.frame.minY, 3_160, accuracy: 1)
    XCTAssertEqual(footerHost.bounds.height, 160, accuracy: 1)
    XCTAssertEqual(settlements.map(\.0), ["expand"])
    XCTAssertEqual(settlements.last?.1, "interrupted")

    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 4,
      transitionID: "collapse", durationMs: 800, disclosureExpanded: false))
    waitForStructuralFrames(0.08)
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 5,
      transitionID: "collapse", durationMs: 800))
    drainMainQueue()
    XCTAssertNil(Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink)
    XCTAssertNil((list.collectionViewLayout as? NativeListFlowLayout)?.tailClip)
    XCTAssertEqual(settlements.map(\.0), ["expand"],
      "Removing disclosure without a new command cannot acknowledge the old target")
  }

  func testDisclosureLateCompletionCannotSettleNewCommand() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    for index in 0..<6 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.setContainerSlotHeightsJson("[0,0,140,0,100,40]")
    var settled: [String] = []
    view.onStructuralTransitionSettled = { payload in
      guard let data = payload.data(using: .utf8),
            let object = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
            let id = object["transitionId"] as? String else { return }
      settled.append(id)
    }
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 1,
      disclosureExpanded: false))
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 2,
      transitionID: "old-direct", disclosureExpanded: false))
    let oldCompletedBeforeReplacement = settled.contains("old-direct")
    XCTAssertFalse(oldCompletedBeforeReplacement,
      "The older diffable completion must remain pending when its replacement starts")
    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 3,
      transitionID: "latest-expand", durationMs: 800, disclosureExpanded: true))
    drainMainQueue()
    XCTAssertNotNil(Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink)
    XCTAssertTrue(settled.isEmpty,
      "An older diffable completion must not settle the active newer transition")
    waitForStructuralFrames(0.9)
    XCTAssertEqual(settled, ["latest-expand"])
  }

  func testDisclosureContentGrowthAndDragSettlement() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    let slots = (0..<6).map { _ in UIView() }
    for (index, slot) in slots.enumerated() { view.mountContainerSlot(slot, atIndex: index) }
    view.setContainerSlotHeightsJson("[0,0,140,0,100,40]")
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInsetAdjustmentBehavior = .never
    let contentHost = try XCTUnwrap(slots[4].superview)
    var settlements: [(String, String)] = []
    let growSettled = expectation(description: "Disclosure growth settles")
    view.onStructuralTransitionSettled = { payload in
      guard let data = payload.data(using: .utf8),
            let object = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
            let id = object["transitionId"] as? String,
            let status = object["status"] as? String else { return }
      settlements.append((id, status))
      if id == "grow" && status == "completed" { growSettled.fulfill() }
    }
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 1,
      disclosureExpanded: false))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    XCTAssertTrue(contentHost.isHidden, "Initial collapsed content must be hidden")
    XCTAssertNil(Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink)

    let growthPhase = expectation(description: "Disclosure growth presents a partial content mask")
    var growthObserved = false
    var growthLast = "no frame sampled"
    let growthStartedAt = CACurrentMediaTime()
    let growthProbe = Timer(timeInterval: 1.0 / 60, repeats: true) { _ in
      let clock = Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink
      let tail = Mirror(reflecting: view).descendant("structuralTail")
      let tailActive = tail.map { !Mirror(reflecting: $0).children.isEmpty } ?? false
      let maskHeight = contentHost.layer.mask?.frame.height
      growthLast = "clock=\(clock != nil) tail=\(tailActive) "
        + "mask=\(String(describing: maskHeight)) bounds=\(contentHost.bounds.height) "
        + "elapsed=\(CACurrentMediaTime() - growthStartedAt) settlements=\(settlements)"
      guard !growthObserved, let clock, tailActive, let maskHeight,
            maskHeight > 0, maskHeight < 100 else { return }
      growthObserved = true
      let animationStart = Mirror(reflecting: view).descendant("structuralAnimationStart")
        as? CFTimeInterval
      let animationDuration = Mirror(reflecting: view).descendant("structuralAnimationDuration")
        as? CFTimeInterval
      XCTAssertNotNil(animationStart, "The active disclosure clock must have a start time")
      XCTAssertNotNil(animationDuration, "The active disclosure clock must have a duration")
      view.setContainerSlotHeightsJson("[0,0,140,0,140,40]")
      XCTAssertTrue(Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink
        === clock, "Content growth must retain the active display link")
      XCTAssertEqual(Mirror(reflecting: view).descendant("structuralAnimationStart")
        as? CFTimeInterval, animationStart, "Content growth must keep the original deadline")
      XCTAssertEqual(Mirror(reflecting: view).descendant("structuralAnimationDuration")
        as? CFTimeInterval, animationDuration, "Content growth must keep the original duration")
      XCTAssertEqual(contentHost.layer.mask?.frame.height ?? -1, maskHeight, accuracy: 1,
        "Content growth must not jump the visible mask")
      growthPhase.fulfill()
    }
    RunLoop.main.add(growthProbe, forMode: .common)
    defer { growthProbe.invalidate() }
    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 2,
      transitionID: "grow", durationMs: 800, disclosureExpanded: true))
    wait(for: [growthPhase], timeout: 1.5)
    guard growthObserved else {
      XCTFail("No partial disclosure growth frame observed: \(growthLast)")
      return
    }
    growthProbe.invalidate()
    wait(for: [growSettled], timeout: 1.5)
    XCTAssertEqual(contentHost.bounds.height, 140, accuracy: 1)
    XCTAssertNil(contentHost.layer.mask)
    XCTAssertTrue(contentHost.isUserInteractionEnabled)
    XCTAssertEqual(settlements.map(\.0), ["grow"], "Growth must settle exactly once")
    XCTAssertEqual(settlements.last?.1, "completed")

    let collapsePhase = expectation(description: "Disclosure collapse presents a partial content mask")
    var collapseObserved = false
    var collapseLast = "no frame sampled"
    let collapseStartedAt = CACurrentMediaTime()
    let collapseProbe = Timer(timeInterval: 1.0 / 60, repeats: true) { _ in
      let clock = Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink
      let tail = Mirror(reflecting: view).descendant("structuralTail")
      let tailActive = tail.map { !Mirror(reflecting: $0).children.isEmpty } ?? false
      let maskHeight = contentHost.layer.mask?.frame.height
      collapseLast = "clock=\(clock != nil) tail=\(tailActive) "
        + "mask=\(String(describing: maskHeight)) bounds=\(contentHost.bounds.height) "
        + "elapsed=\(CACurrentMediaTime() - collapseStartedAt) settlements=\(settlements)"
      guard !collapseObserved, clock != nil, tailActive, let maskHeight,
            maskHeight > 0, maskHeight < 140 else { return }
      collapseObserved = true
      view.scrollViewWillBeginDragging(list)
      XCTAssertNil(Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink,
        "Dragging must stop the active disclosure display link")
      let remainingTail = Mirror(reflecting: view).descendant("structuralTail")
      XCTAssertNotNil(remainingTail, "The structural tail field must remain observable")
      XCTAssertFalse(remainingTail.map { !Mirror(reflecting: $0).children.isEmpty } ?? true,
        "Dragging must clear the temporary structural tail")
      XCTAssertTrue(contentHost.isHidden, "Dragging a collapse must hide content")
      XCTAssertEqual(settlements.map(\.0), ["grow", "drag"],
        "Dragging must settle the collapse exactly once")
      XCTAssertEqual(settlements.last?.1, "interrupted")
      collapsePhase.fulfill()
    }
    RunLoop.main.add(collapseProbe, forMode: .common)
    defer { collapseProbe.invalidate() }
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 3,
      transitionID: "drag", durationMs: 800, disclosureExpanded: false))
    wait(for: [collapsePhase], timeout: 1.5)
    guard collapseObserved else {
      XCTFail("No partial disclosure collapse frame observed: \(collapseLast)")
      return
    }
    collapseProbe.invalidate()
  }

  func testDisclosureDirectCollapseKeepsToggleVisible() throws {
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 1376, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    let slots = (0..<6).map { _ in UIView() }
    for (index, slot) in slots.enumerated() { view.mountContainerSlot(slot, atIndex: index) }
    view.setSidecarLayoutJson("{\"width\":400,\"gap\":24,\"maxContentWidth\":1140}")
    view.setContainerSlotHeightsJson("[0,0,900,2200,100,40]")
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInsetAdjustmentBehavior = .never
    list.contentInset = .zero
    var settlements: [[String: Any]] = []
    view.onStructuralTransitionSettled = { payload in
      guard let data = payload.data(using: .utf8),
            let object = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
        return
      }
      settlements.append(object)
    }
    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 1,
      disclosureExpanded: true))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    list.contentOffset.y = 2_600
    list.layoutIfNeeded()
    XCTAssertEqual(list.contentOffset.y, 2_600, accuracy: 1)

    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 2,
      transitionID: "direct-collapse", durationMs: 0, disclosureExpanded: false))
    drainMainQueue()
    list.layoutIfNeeded()
    let toggle = try XCTUnwrap(slots[5].superview)
    XCTAssertEqual(toggle.frame.minY, 360, accuracy: 1)
    XCTAssertEqual(list.contentSize.height, 2_200, accuracy: 1)
    XCTAssertEqual(list.contentOffset.y, 360, accuracy: 1,
      "A direct collapsed endpoint must expose the stable toggle despite a taller sidecar")
    XCTAssertEqual(settlements.count, 1)
    XCTAssertEqual(settlements.first?["transitionId"] as? String, "direct-collapse")
    XCTAssertEqual(settlements.first?["status"] as? String, "completed")

    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 3,
      disclosureExpanded: true))
    drainMainQueue()
    view.frame.size.height = 0
    view.layoutIfNeeded()
    XCTAssertEqual(list.bounds.height, 0)
    view.scrollToOffset(500, animated: false)
    let pending = try XCTUnwrap(Mirror(reflecting: view).descendant("pendingScrollRequest"))
    XCTAssertEqual(Mirror(reflecting: pending).children.count, 1,
      "The explicit command must still be queued when the direct collapse is accepted")
    view.frame.size.height = 600
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 4,
      transitionID: "direct-collapse-after-pending-scroll", durationMs: 0,
      disclosureExpanded: false))
    drainMainQueue()
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    XCTAssertEqual(list.contentOffset.y, 500, accuracy: 1,
      "A direct collapse must not replace an already pending explicit scroll")
    XCTAssertEqual(settlements.count, 2)
    XCTAssertEqual(settlements.last?["transitionId"] as? String,
      "direct-collapse-after-pending-scroll")
  }

  func testStructuralTailKeepsMiddleRowsNaturalAndFooterAttachedDuringCollapse() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    var retiredActions = 0
    view.onRowAction = { _ in retiredActions += 1 }
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    for index in 0..<2 { view.mountContainerSlot(UIView(), atIndex: index) }
    let footer = UIView()
    view.mountContainerSlot(footer, atIndex: 2)
    view.setContainerSlotHeightsJson("[0,0,60,0]")
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInsetAdjustmentBehavior = .never
    list.contentInset = .zero
    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 1))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    list.contentOffset.y = 2400
    list.layoutIfNeeded()
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 2,
      transitionID: "tail-50-to-6", durationMs: 800))
    waitForStructuralFrames(0.16)
    list.layoutIfNeeded()
    let source = try XCTUnwrap(list.dataSource as? UICollectionViewDiffableDataSource<Int, String>)
    XCTAssertEqual(source.snapshot().itemIdentifiers, (0..<50).map { "row-\($0)" })
    let flow = try XCTUnwrap(list.collectionViewLayout as? NativeListFlowLayout)
    let clip = try XCTUnwrap(flow.tailClip)
    XCTAssertEqual(clip.firstIndex, 6)
    XCTAssertGreaterThan(clip.endY, 360)
    XCTAssertLessThan(clip.endY, 3000)
    let tailCells = list.visibleCells.compactMap { cell -> NativeListRowHost? in
      guard let path = list.indexPath(for: cell), path.item >= 6 else { return nil }
      return cell as? NativeListRowHost
    }
    XCTAssertFalse(tailCells.isEmpty)
    XCTAssertTrue(tailCells.allSatisfy { abs($0.bounds.height - 60) <= 1 && $0.alpha == 1 })
    XCTAssertTrue(tailCells.allSatisfy { !$0.isUserInteractionEnabled && $0.accessibilityElementsHidden })
    XCTAssertFalse(view.collectionView(list, shouldSelectItemAt: IndexPath(item: 25, section: 0)))
    view.collectionView(list, didSelectItemAt: IndexPath(item: 25, section: 0))
    XCTAssertEqual(retiredActions, 0)
    XCTAssertEqual(footer.convert(.zero, to: list).y, clip.endY, accuracy: 1)
    let footerBottomBeforeResize = footer.convert(.zero, to: list).y + 60
    view.setContainerSlotHeightsJson("[0,0,80,0]")
    XCTAssertEqual(footer.convert(.zero, to: list).y + 80,
      footerBottomBeforeResize, accuracy: 1)
    let footerHost = try XCTUnwrap(footer.superview)
    let hiddenTop = try XCTUnwrap(flow.tailClip).endY - footerHost.frame.minY
    if hiddenTop > 1 {
      XCTAssertFalse(footerHost.point(inside: CGPoint(x: 10, y: hiddenTop / 2), with: nil))
      XCTAssertTrue(footerHost.point(inside: CGPoint(x: 10, y: hiddenTop + 1), with: nil))
    }
    waitForStructuralFrames(0.06)
    list.layoutIfNeeded()
    XCTAssertEqual(footer.convert(.zero, to: list).y, try XCTUnwrap(flow.tailClip).endY,
      accuracy: 1)
    let footerBottomBeforeSecondResize = footer.convert(.zero, to: list).y + 80
    view.setContainerSlotHeightsJson("[0,0,100,0]")
    XCTAssertEqual(footer.convert(.zero, to: list).y + 100,
      footerBottomBeforeSecondResize, accuracy: 1)
    waitForStructuralFrames(0.7)
    list.layoutIfNeeded()
    XCTAssertNil(flow.tailClip)
    XCTAssertEqual(source.snapshot().itemIdentifiers, (0..<6).map { "row-\($0)" })
    XCTAssertEqual(list.contentSize.height, 460, accuracy: 1)
    XCTAssertEqual(footer.convert(.zero, to: list).y, 360, accuracy: 1)
    XCTAssertEqual(list.contentOffset.y, 0, accuracy: 1)
  }

  func testStructuralTailCollapsePaintsEveryExpectedViewportRow() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 1000, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    for index in 0..<2 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.mountContainerSlot(UIView(), atIndex: 2)
    view.mountContainerSlot(UIView(), atIndex: 3)
    view.setContainerSlotHeightsJson("[0,0,60,3000]")
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInsetAdjustmentBehavior = .never
    list.contentInset = .zero
    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 1))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    list.contentOffset.y = 2400
    list.layoutIfNeeded()
    let flow = try XCTUnwrap(list.collectionViewLayout as? NativeListFlowLayout)
    var checkedFrames = 0
    var failures: [String] = []
    let probe = Timer(timeInterval: 1.0 / 60, repeats: true) { _ in
      guard let clip = flow.tailClip else { return }
      list.layoutIfNeeded()
      let viewportTop = list.contentOffset.y
      let viewportBottom = viewportTop + list.bounds.height
      var expectedCount = 0
      var attributesCount = 0
      var attachedCount = 0
      var visibleCellsCount = 0
      var firstFailure: String?
      let attachedCells = list.subviews.compactMap { $0 as? NativeListRowHost }
      let visibleCells = list.visibleCells
      for index in 6..<50 {
        let rowTop = CGFloat(index * 60)
        let rowBottom = rowTop + 60
        let expectedTop = max(rowTop, viewportTop)
        let expectedBottom = min(rowBottom, viewportBottom, clip.endY)
        guard expectedBottom - expectedTop >= 8 else { continue }
        expectedCount += 1
        let path = IndexPath(item: index, section: 0)
        let attributes = flow.layoutAttributesForItem(at: path)
        if attributes != nil { attributesCount += 1 }
        let cell = attachedCells.first { list.indexPath(for: $0) == path }
        if cell != nil { attachedCount += 1 }
        let inVisibleCells = cell.map { visibleCells.contains($0) } ?? false
        if inVisibleCells { visibleCellsCount += 1 }
        let maskEnd = cell?.layer.mask?.frame.maxY ?? 60
        let neededMaskEnd = expectedBottom - rowTop
        if cell == nil || cell?.isHidden == true || cell?.alpha != 1
          || maskEnd + 1 < neededMaskEnd {
          if firstFailure == nil {
            let hidden = cell.map { String($0.isHidden) } ?? "nil"
            let frame = cell.map { String(describing: $0.frame) } ?? "nil"
            firstFailure = "row=\(index) offset=\(Int(viewportTop)) clip=\(Int(clip.endY)) "
              + "attached=\(cell != nil) visibleCells=\(inVisibleCells) hidden=\(hidden) "
              + "frame=\(frame) attributes=\(String(describing: attributes?.frame)) "
              + "maskEnd=\(Int(maskEnd)) alpha=\(cell?.alpha ?? -1)"
          }
        }
      }
      if expectedCount > 0 { checkedFrames += 1 }
      if let firstFailure, failures.count < 8 {
        failures.append("expected=\(expectedCount) attrs=\(attributesCount) "
          + "attached=\(attachedCount) visibleCells=\(visibleCellsCount) " + firstFailure)
      }
    }
    RunLoop.main.add(probe, forMode: .common)
    defer { probe.invalidate() }
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 2,
      transitionID: "tail-viewport-paint", durationMs: 800))
    waitForStructuralFrames(0.82)
    XCTAssertGreaterThanOrEqual(checkedFrames, 5)
    XCTAssertTrue(failures.isEmpty, failures.joined(separator: "\n"))
  }

  func testStructuralTailGeometryChangeWithSameIDCommitsFinalLayout() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    for index in 0..<3 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.setContainerSlotHeightsJson("[0,0,60,0]")
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInsetAdjustmentBehavior = .never
    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 1))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 2,
      transitionID: "same-id-geometry", durationMs: 400))
    waitForStructuralFrames(0.08)
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 3,
      transitionID: "same-id-geometry", durationMs: 400, firstRowHeight: 70))
    let source = try XCTUnwrap(list.dataSource as? UICollectionViewDiffableDataSource<Int, String>)
    let flow = try XCTUnwrap(list.collectionViewLayout as? NativeListFlowLayout)
    list.layoutIfNeeded()
    XCTAssertNil(flow.tailClip)
    XCTAssertEqual(source.snapshot().itemIdentifiers, (0..<6).map { "row-\($0)" })
    XCTAssertEqual(list.contentSize.height, 430, accuracy: 1)
    waitForStructuralFrames(0.42)
    XCTAssertEqual(list.contentSize.height, 430, accuracy: 1)
  }

  func testStructuralTailSameIDOrientationChangeStopsVerticalTimeline() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 1))
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 2,
      transitionID: "same-id-orientation", durationMs: 400))
    waitForStructuralFrames(0.08)
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 3,
      transitionID: "same-id-orientation", durationMs: 400, orientation: "horizontal"))
    let flow = try XCTUnwrap(list.collectionViewLayout as? NativeListFlowLayout)
    XCTAssertNil(flow.tailClip)
    XCTAssertEqual(flow.scrollDirection, .horizontal)
    waitForStructuralFrames(0.42)
    XCTAssertEqual(flow.scrollDirection, .horizontal)
    XCTAssertNil(flow.tailClip)
  }

  func testStructuralTailHostLimitFallsBackToAtomicSnapshot() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    for index in 0..<3 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.setContainerSlotHeightsJson("[0,0,60,0]")
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInsetAdjustmentBehavior = .never
    view.applySnapshotJson(try structuralRowsSnapshot(count: 1000, generation: 1,
      uniformHeight: 1))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    var phases: [Int] = []
    let observer = NotificationCenter.default.addObserver(
      forName: Notification.Name("OneKeyNativeListScrollCommandAccepted"),
      object: list, queue: nil
    ) { notification in
      if let phase = notification.userInfo?["phase"] as? Int { phases.append(phase) }
    }
    defer { NotificationCenter.default.removeObserver(observer) }
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 2,
      transitionID: "tail-host-limit", durationMs: 400, uniformHeight: 1))
    list.layoutIfNeeded()
    let source = try XCTUnwrap(list.dataSource as? UICollectionViewDiffableDataSource<Int, String>)
    let flow = try XCTUnwrap(list.collectionViewLayout as? NativeListFlowLayout)
    XCTAssertEqual(source.snapshot().itemIdentifiers, (0..<6).map { "row-\($0)" })
    XCTAssertNil(flow.tailClip)
    XCTAssertTrue(phases.isEmpty)
  }

  func testStructuralTailExpansionKeepsOffsetWithTallerInitialSidecar() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 1000, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    for index in 0..<4 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.setContainerSlotHeightsJson("[0,0,60,2200]")
    view.setSidecarLayoutJson("{\"width\":300,\"gap\":24,\"maxContentWidth\":1000}")
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInsetAdjustmentBehavior = .never
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 1))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    XCTAssertEqual(list.contentSize.height, 2200, accuracy: 1)
    list.contentOffset.y = 1000
    list.layoutIfNeeded()
    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 2,
      transitionID: "tail-6-to-50", durationMs: 400))
    waitForStructuralFrames(0.16)
    list.layoutIfNeeded()
    let source = try XCTUnwrap(list.dataSource as? UICollectionViewDiffableDataSource<Int, String>)
    let flow = try XCTUnwrap(list.collectionViewLayout as? NativeListFlowLayout)
    XCTAssertEqual(source.snapshot().itemIdentifiers, (0..<50).map { "row-\($0)" })
    XCTAssertGreaterThan(try XCTUnwrap(flow.tailClip).endY, 360)
    XCTAssertLessThan(try XCTUnwrap(flow.tailClip).endY, 3000)
    let suffixCells = list.visibleCells.filter {
      (list.indexPath(for: $0)?.item ?? -1) >= 6
    }
    XCTAssertFalse(suffixCells.isEmpty)
    XCTAssertTrue(suffixCells.allSatisfy {
      !$0.isUserInteractionEnabled && $0.accessibilityElementsHidden
    })
    XCTAssertEqual(list.contentOffset.y, 1000, accuracy: 1)
    waitForStructuralFrames(0.3)
    list.layoutIfNeeded()
    XCTAssertEqual(source.snapshot().itemIdentifiers, (0..<50).map { "row-\($0)" })
    XCTAssertEqual(list.contentSize.height, 3060, accuracy: 1)
    XCTAssertEqual(list.contentOffset.y, 1000, accuracy: 1)
    XCTAssertNil(flow.tailClip)
  }

  func testStructuralTailDifferentPrefixDuringExpansionCommitsAtomicSnapshot() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 1000, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    for index in 0..<4 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.setContainerSlotHeightsJson("[0,0,60,1600]")
    view.setSidecarLayoutJson("{\"width\":300,\"gap\":24,\"maxContentWidth\":1000}")
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInsetAdjustmentBehavior = .never
    list.contentInset = .zero
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 1))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    XCTAssertEqual(list.adjustedContentInset.top, 0, accuracy: 0.01)
    XCTAssertEqual(list.adjustedContentInset.bottom, 0, accuracy: 0.01)
    list.contentOffset.y = 900
    list.layoutIfNeeded()

    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 2,
      transitionID: "tail-first-six", durationMs: 1200))
    waitForStructuralFrames(0.12)
    let source = try XCTUnwrap(list.dataSource as? UICollectionViewDiffableDataSource<Int, String>)
    let flow = try XCTUnwrap(list.collectionViewLayout as? NativeListFlowLayout)
    XCTAssertEqual(source.snapshot().itemIdentifiers, (0..<50).map { "row-\($0)" })
    XCTAssertGreaterThan(try XCTUnwrap(flow.tailClip).endY, 360)

    view.applySnapshotJson(try structuralRowsSnapshot(count: 10, generation: 3,
      transitionID: "tail-next-ten", durationMs: 1200))
    list.layoutIfNeeded()
    XCTAssertEqual(source.snapshot().itemIdentifiers, (0..<10).map { "row-\($0)" })
    XCTAssertNil(flow.tailClip)
    XCTAssertNil(Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink)
    XCTAssertEqual(Mirror(reflecting: view).descendant("structuralSpacerHeight") as? CGFloat ?? -1,
      0, accuracy: 1)
    XCTAssertEqual(list.contentSize.height, 1600, accuracy: 1)
    XCTAssertEqual(list.contentOffset.y, 900, accuracy: 1)
    waitForStructuralFrames(1.25)
    list.layoutIfNeeded()
    XCTAssertEqual(source.snapshot().itemIdentifiers, (0..<10).map { "row-\($0)" })
    XCTAssertEqual(list.contentSize.height, 1600, accuracy: 1)
    XCTAssertEqual(list.contentOffset.y, 900, accuracy: 1)
  }

  func testStructuralTailInterruptedExpansionRestoresBoundaryCellInteraction() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 1000, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    for index in 0..<2 { view.mountContainerSlot(UIView(), atIndex: index) }
    let footer = UIView()
    view.mountContainerSlot(footer, atIndex: 2)
    view.mountContainerSlot(UIView(), atIndex: 3)
    view.setContainerSlotHeightsJson("[0,0,60,2200]")
    view.setSidecarLayoutJson("{\"width\":300,\"gap\":24,\"maxContentWidth\":1000}")
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInsetAdjustmentBehavior = .never
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 1))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    list.contentOffset.y = 300
    list.layoutIfNeeded()
    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 2,
      transitionID: "interrupt-boundary", durationMs: 1200))
    let footerBottomBeforeResize = footer.convert(.zero, to: list).y + 60
    view.setContainerSlotHeightsJson("[0,0,80,2200]")
    XCTAssertEqual(footer.convert(.zero, to: list).y + 80,
      footerBottomBeforeResize, accuracy: 1)
    let flow = try XCTUnwrap(list.collectionViewLayout as? NativeListFlowLayout)
    let prefixBottom = try XCTUnwrap(flow.layoutAttributesForItem(
      at: IndexPath(item: 5, section: 0))).frame.maxY
    let footerHost = try XCTUnwrap(footer.superview)
    let hiddenTop = prefixBottom - footerHost.frame.minY
    XCTAssertGreaterThan(hiddenTop, 0)
    XCTAssertFalse(footerHost.point(inside: CGPoint(x: 10, y: hiddenTop / 2), with: nil))
    XCTAssertTrue(footerHost.point(inside: CGPoint(x: 10, y: hiddenTop + 1), with: nil))
    var boundaryCell: NativeListRowHost?
    let deadline = Date().addingTimeInterval(1)
    while boundaryCell == nil && Date() < deadline {
      if let clip = flow.tailClip {
        for case let cell as NativeListRowHost in list.visibleCells {
          guard let path = list.indexPath(for: cell), path.item >= 6,
                let mask = cell.layer.mask else { continue }
          let visibleHeight = clip.endY - cell.frame.minY
          if clip.endY > cell.frame.minY + 2 &&
             clip.endY < cell.frame.maxY - 2 &&
             mask.frame.height > 2 &&
             mask.frame.height < cell.bounds.height - 2 &&
             abs(mask.frame.height - visibleHeight) <= 1 {
            boundaryCell = cell
            break
          }
        }
      }
      if boundaryCell == nil { RunLoop.main.run(until: Date().addingTimeInterval(0.01)) }
    }
    let boundary = try XCTUnwrap(boundaryCell)
    let displayLink = try XCTUnwrap(
      Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink)
    displayLink.isPaused = true
    defer { displayLink.isPaused = false }
    let mask = try XCTUnwrap(boundary.layer.mask)
    XCTAssertGreaterThan(mask.frame.height, 2)
    XCTAssertLessThan(mask.frame.height, boundary.bounds.height - 2)
    XCTAssertFalse(boundary.isUserInteractionEnabled)
    XCTAssertTrue(boundary.accessibilityElementsHidden)
    let offsetBeforeInterruption = list.contentOffset.y
    view.scrollViewWillBeginDragging(list)
    list.layoutIfNeeded()
    XCTAssertNil(Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink)
    XCTAssertNil(boundary.layer.mask)
    XCTAssertFalse(boundary.isHidden)
    XCTAssertTrue(boundary.isUserInteractionEnabled)
    XCTAssertFalse(boundary.accessibilityElementsHidden)
    XCTAssertTrue(footerHost.point(inside: CGPoint(x: 10, y: 1), with: nil))
    XCTAssertEqual(list.contentOffset.y, offsetBeforeInterruption, accuracy: 1)
    XCTAssertEqual(list.contentOffset.y, 300, accuracy: 1)
  }

  func testStructuralTailExplicitScrollTakesOwnershipFromExpansion() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    for index in 0..<3 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.setContainerSlotHeightsJson("[0,0,60,0]")
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInsetAdjustmentBehavior = .never
    view.applySnapshotJson(try structuralRowsSnapshot(count: 6, generation: 1))
    view.layoutIfNeeded()
    list.layoutIfNeeded()
    drainMainQueue()
    view.applySnapshotJson(try structuralRowsSnapshot(count: 50, generation: 2,
      transitionID: "explicit-scroll-over-expansion", durationMs: 400))
    waitForStructuralFrames(0.08)
    view.scrollToIndex(40, animated: false, alignment: "start")
    drainMainQueue()
    list.layoutIfNeeded()
    let source = try XCTUnwrap(list.dataSource as? UICollectionViewDiffableDataSource<Int, String>)
    XCTAssertEqual(source.snapshot().itemIdentifiers, (0..<50).map { "row-\($0)" })
    XCTAssertEqual(list.contentOffset.y, 2400, accuracy: 1)
    waitForStructuralFrames(0.4)
    XCTAssertEqual(list.contentOffset.y, 2400, accuracy: 1)
    XCTAssertTrue(list.visibleCells.allSatisfy {
      $0.isUserInteractionEnabled && !$0.accessibilityElementsHidden && $0.layer.mask == nil
    })
  }

  func testStructuralTransitionSurvivesVisibleDataRowPatch() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer { view.dispose(); view.removeFromSuperview(); window.isHidden = true }
    let footer = UIView()
    for index in 0..<2 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.mountContainerSlot(footer, atIndex: 2)
    view.setContainerSlotHeightsJson("[0,0,60,0]")
    view.applySnapshotJson(try structuralRowsSnapshot(count: 30, generation: 1, dataRows: true))
    view.layoutIfNeeded()
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.layoutIfNeeded()
    drainMainQueue()
    list.contentOffset.y = 1100
    list.layoutIfNeeded()

    view.applySnapshotJson(try structuralRowsSnapshot(count: 25, generation: 2,
      transitionID: "collapse-with-patch", durationMs: 800, dataRows: true))
    waitForStructuralFrames(0.3)
    let source = try XCTUnwrap(list.dataSource as? UICollectionViewDiffableDataSource<Int, String>)
    let flow = try XCTUnwrap(list.collectionViewLayout as? NativeListFlowLayout)
    let heightBeforePatch = try XCTUnwrap(flow.tailClip).endY
    XCTAssertGreaterThan(heightBeforePatch, 1500)
    let retiring = try XCTUnwrap(list.cellForItem(at: IndexPath(item: 25, section: 0)) as? NativeListRowHost)
    XCTAssertEqual(retiring.bounds.height, 60, accuracy: 1)
    func storedContent(_ key: String) throws -> String {
      let items = try XCTUnwrap(Mirror(reflecting: view).descendant("itemsByKey")
        as? [String: NativeListItem])
      return try XCTUnwrap(items[key]).content
    }
    let oldAcceptedContent = try storedContent("row-20")
    let retiredContent = try storedContent("row-25")
    let patch: [[String: Any]] = [[
      "key": "row-20", "type": "dataRow", "changes": [
        "revision": 2,
        "columns": [["key": "asset", "text": "Asset"],
          ["key": "price", "text": "$123.45"]],
      ],
    ]]
    let invalidRetiredPatch: [String: Any] = ["key": "row-25", "type": "dataRow",
      "changes": ["revision": 2]]
    view.applyPatchesJson(String(decoding: try JSONSerialization.data(
      withJSONObject: patch + [invalidRetiredPatch]), as: UTF8.self))
    XCTAssertEqual(try storedContent("row-20"), oldAcceptedContent)
    XCTAssertEqual(try storedContent("row-25"), retiredContent)
    view.applyPatchesJson(String(decoding: try JSONSerialization.data(withJSONObject: patch),
      as: UTF8.self))
    XCTAssertNotEqual(try storedContent("row-20"), oldAcceptedContent)
    XCTAssertEqual(try storedContent("row-25"), retiredContent)
    list.layoutIfNeeded()
    let patched = try XCTUnwrap(list.cellForItem(at: IndexPath(item: 20, section: 0)))
    XCTAssertTrue(labels(in: patched).contains { $0.text == "$123.45" })
    waitForStructuralFrames(0.05)
    list.layoutIfNeeded()
    XCTAssertLessThan(try XCTUnwrap(flow.tailClip).endY, heightBeforePatch)
    waitForStructuralFrames(0.55)
    list.layoutIfNeeded()
    XCTAssertEqual(source.snapshot().itemIdentifiers, (0..<25).map { "row-\($0)" })
    XCTAssertEqual(list.contentSize.height, 1560, accuracy: 1)
    XCTAssertEqual(footer.convert(.zero, to: list).y, 1500, accuracy: 1)
    let finalPatched = try XCTUnwrap(list.cellForItem(at: IndexPath(item: 20, section: 0)))
    XCTAssertTrue(labels(in: finalPatched).contains { $0.text == "$123.45" })
  }

  func testStructuralTransitionDoesNotOverrideUserDrag() throws {
    try requireStructuralAnimation()
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer { view.dispose(); view.removeFromSuperview(); window.isHidden = true }
    for index in 0..<3 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.setContainerSlotHeightsJson("[0,0,60,0]")
    view.applySnapshotJson(try structuralRowsSnapshot(count: 30, generation: 1))
    view.layoutIfNeeded()
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.layoutIfNeeded()
    drainMainQueue()
    list.contentOffset.y = 1100
    view.applySnapshotJson(try structuralRowsSnapshot(count: 25, generation: 2,
      transitionID: "collapse-drag", durationMs: 400))
    waitForStructuralFrames(0.08)
    view.scrollViewWillBeginDragging(list)
    XCTAssertNil(Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink)
    XCTAssertEqual(list.contentSize.height, 1560, accuracy: 1)
    list.contentOffset.y = 600
    list.layoutIfNeeded()
    waitForStructuralFrames(0.15)
    XCTAssertEqual(list.contentOffset.y, 600, accuracy: 1)
    waitForStructuralFrames(0.35)
    list.layoutIfNeeded()
    XCTAssertEqual(list.contentOffset.y, 600, accuracy: 1)
    let source = try XCTUnwrap(list.dataSource as? UICollectionViewDiffableDataSource<Int, String>)
    XCTAssertEqual(source.snapshot().itemIdentifiers, (0..<25).map { "row-\($0)" })
    XCTAssertEqual(list.contentSize.height, 1560, accuracy: 1)
    XCTAssertTrue(list.visibleCells.allSatisfy { abs($0.alpha - 1) < 0.01 })
  }

  func testStructuralTransitionDetachAndDisposeReleaseView() throws {
    try requireStructuralAnimation()
    for dispose in [false, true] {
      var callbacks = 0
      let reference = try detachStructuralTransition(dispose: dispose) { callbacks += 1 }
      if dispose {
        drainMainQueue()
        XCTAssertNil(reference.view)
        let countAtDispose = callbacks
        waitForStructuralFrames(0.5)
        XCTAssertEqual(callbacks, countAtDispose)
      } else {
        waitForStructuralFrames(0.5)
        XCTAssertNil(reference.view)
      }
    }
  }

  private final class WeakListReference {
    weak var view: NativeListView?
  }

  private func detachStructuralTransition(
    dispose: Bool, onCallback: @escaping () -> Void
  ) throws -> WeakListReference {
    try autoreleasepool {
      let reference = WeakListReference()
      let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 600))
      let controller = UIViewController()
      window.rootViewController = controller
      let view = NativeListView(frame: window.bounds)
      controller.view.addSubview(view)
      window.isHidden = false
      for index in 0..<3 { view.mountContainerSlot(UIView(), atIndex: index) }
      view.setContainerSlotHeightsJson("[0,0,60,0]")
      view.applySnapshotJson(try structuralRowsSnapshot(count: 30, generation: 1))
      view.layoutIfNeeded()
      let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
      view.onVisibleRangeChanged = { _ in onCallback() }
      view.onEndReached = { _ in onCallback() }
      list.layoutIfNeeded()
      drainMainQueue()
      view.applySnapshotJson(try structuralRowsSnapshot(count: 25, generation: 2,
        transitionID: dispose ? "dispose-mid-transition" : "detach-mid-transition",
        durationMs: 400))
      // Interrupt an active transition without racing a wall-clock delay on a busy runner.
      XCTAssertNotNil(Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink)
      reference.view = view
      if dispose {
        view.dispose()
        XCTAssertNil(list.delegate)
        XCTAssertNil(list.dataSource)
      }
      view.removeFromSuperview()
      XCTAssertNil(Mirror(reflecting: view).descendant("structuralDisplayLink") as? CADisplayLink)
      XCTAssertNil((list.collectionViewLayout as? NativeListFlowLayout)?.tailClip)
      window.isHidden = true
      window.rootViewController = nil
      return reference
    }
  }

  private func dispatchAccessoryTouchUpInside(
    _ control: UIButton, on stack: NativeListAccessoryStack,
    file: StaticString = #filePath, line: UInt = #line
  ) throws {
    guard control.allTargets.contains(stack) else {
      XCTFail("Accessory control is not attached to its stack", file: file, line: line)
      return
    }
    let actions = try XCTUnwrap(
      control.actions(forTarget: stack, forControlEvent: .touchUpInside),
      file: file, line: line)
    guard !actions.isEmpty else {
      XCTFail("Accessory control has no touch-up action", file: file, line: line)
      return
    }
    // This pod XCTest has no UIApplication to dispatch UIControl.sendActions.
    guard control.isEnabled else { return }
    for action in actions {
      let selector = NSSelectorFromString(action)
      guard stack.responds(to: selector) else {
        XCTFail("Accessory action selector is missing: \(action)", file: file, line: line)
        continue
      }
      if action.hasSuffix(":") { _ = stack.perform(selector, with: control) }
      else { _ = stack.perform(selector) }
    }
  }

  private func requireStructuralAnimation() throws {
    if UIAccessibility.isReduceMotionEnabled {
      throw XCTSkip("Structural animation is disabled by Reduce Motion")
    }
  }

  private func waitForStructuralFrames(_ seconds: TimeInterval) {
    let completed = expectation(description: "Native structural display link ran for \(seconds)s")
    DispatchQueue.main.asyncAfter(deadline: .now() + seconds) { completed.fulfill() }
    wait(for: [completed], timeout: seconds + 1)
  }

  private func labels(in view: UIView) -> [UILabel] {
    let current = (view as? UILabel).map { [$0] } ?? []
    return current + view.subviews.flatMap(labels(in:))
  }

  private func structuralRowsSnapshot(
    count: Int, generation: Int? = nil, transitionID: String? = nil,
    durationMs: Int = 80, dataRows: Bool = false, firstRowHeight: Int? = nil,
    uniformHeight: Int = 60, orientation: String = "vertical", layoutKind: String = "linear",
    disclosureExpanded: Bool? = nil, disclosureInset: Int = 20
  ) throws -> String {
    let rows: [[String: Any]] = (0..<count).map {
      let height = $0 == 0 ? firstRowHeight ?? uniformHeight : uniformHeight
      if dataRows {
        return ["key": "row-\($0)", "type": "dataRow", "height": height,
          "columns": [["key": "asset", "text": "Asset"],
            ["key": "price", "text": "$1.00"]]]
      }
      return ["key": "row-\($0)", "type": "action", "title": "Row", "height": height]
    }
    var layout: [String: Any] = ["kind": layoutKind, "orientation": orientation]
    if layoutKind == "grid" { layout["gridColumns"] = 2 }
    var value: [String: Any] = [
      "schemaVersion": 1, "generation": generation ?? count + 1,
      "layout": layout, "rows": rows,
    ]
    if let transitionID {
      value["structuralTransition"] = [
        "id": transitionID, "durationMs": durationMs, "anchorKey": "row-24",
      ]
    }
    if let disclosureExpanded {
      value["disclosure"] = ["expanded": disclosureExpanded,
        "expandedTrailingInset": disclosureInset]
    }
    return String(decoding: try JSONSerialization.data(withJSONObject: value), as: UTF8.self)
  }

  private func assertHomeSidecarGeometry(sidecarBeforeFirstLayout: Bool) throws {
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 1376, height: 900))
    let controller = UIViewController()
    window.rootViewController = controller
    let view = NativeListView(frame: window.bounds)
    controller.view.addSubview(view)
    window.isHidden = false
    defer {
      view.dispose()
      view.removeFromSuperview()
      window.isHidden = true
      window.rootViewController = nil
    }
    for index in 0..<4 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.setContainerSlotHeightsJson("[0,0,0,1200]")
    let sidecar = "{\"width\":400,\"gap\":24,\"maxContentWidth\":1140}"
    if sidecarBeforeFirstLayout { view.setSidecarLayoutJson(sidecar) }
    view.applySnapshotJson(try homeSidecarSnapshot())
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.contentInsetAdjustmentBehavior = .never
    func layout() {
      window.layoutIfNeeded()
      view.layoutIfNeeded()
      list.layoutIfNeeded()
    }
    func assertRows(x: CGFloat, width: CGFloat) {
      let cells = list.visibleCells.compactMap { $0 as? NativeListDataRowCell }
      XCTAssertFalse(cells.isEmpty)
      for cell in cells {
        let frame = cell.convert(cell.bounds, to: list)
        XCTAssertEqual(frame.minX, x, accuracy: 1)
        XCTAssertEqual(frame.width, width, accuracy: 1)
        if let path = list.indexPath(for: cell),
          let attributes = list.layoutAttributesForItem(at: path)
        {
          XCTAssertEqual(attributes.frame.minX, x, accuracy: 1)
          XCTAssertEqual(attributes.frame.width, width, accuracy: 1)
        }
        let root = cell.root.convert(cell.root.bounds, to: list)
        XCTAssertGreaterThanOrEqual(root.minX, frame.minX - 1)
        XCTAssertLessThanOrEqual(root.maxX, frame.maxX + 1)
      }
    }
    layout()
    if !sidecarBeforeFirstLayout {
      assertRows(x: 8, width: 1360)
      view.setSidecarLayoutJson(sidecar)
      layout()
    }
    assertRows(x: 126, width: 700)
    list.contentOffset.y = 18 * 60
    layout()
    assertRows(x: 126, width: 700)
    let patch: [[String: Any]] = [[
      "key": "row-20", "type": "dataRow",
      "changes": ["columns": homeSidecarColumns(20, updated: true),
        "trailing": [["kind": "button", "label": "Swap", "actionKey": "swap"]]],
    ]]
    view.applyPatchesJson(String(decoding: try JSONSerialization.data(withJSONObject: patch),
      as: UTF8.self))
    layout()
    assertRows(x: 126, width: 700)
    window.frame.size.width = 1024
    controller.view.frame = window.bounds
    view.frame = window.bounds
    layout()
    assertRows(x: 8, width: 584)
    window.frame.size.width = 1376
    controller.view.frame = window.bounds
    view.frame = window.bounds
    layout()
    assertRows(x: 126, width: 700)
    view.setSidecarLayoutJson("")
    layout()
    assertRows(x: 8, width: 1360)
  }

  private func homeSidecarSnapshot() throws -> String {
    let rows: [[String: Any]] = (0..<30).map { index in
      ["key": "row-\(index)", "type": "dataRow", "height": 60,
       "leading": ["kind": "token", "fallbackIcon": ["name": "CryptoCoinOutline"]],
       "columns": homeSidecarColumns(index, updated: false),
       "trailing": [["kind": "button", "label": "Swap", "actionKey": "swap"]],
       "style": ["horizontalPadding": 12, "verticalPadding": 8, "leadingGap": 12,
         "image": ["width": 32, "height": 32]]]
    }
    let snapshot: [String: Any] = [
      "schemaVersion": 1, "generation": 1,
      "layout": ["kind": "linear", "contentPaddingHorizontal": 8], "rows": rows,
    ]
    return String(decoding: try JSONSerialization.data(withJSONObject: snapshot), as: UTF8.self)
  }

  private func homeSidecarColumns(_ index: Int, updated: Bool) -> [[String: Any]] {
    [
      ["key": "asset", "text": "Asset \(index)", "secondaryText": "Public fixture", "weight": 1],
      ["key": "balance", "text": updated ? "0.2966" : "0.000175",
       "secondaryText": "$0.13", "alignment": "end", "weight": 1],
      ["key": "price", "text": updated ? "$739.22" : "$0.9989",
       "secondaryText": "-0.04%", "alignment": "end", "weight": 1],
    ]
  }

  private func makeScrollingList() throws -> NativeListView {
    let view = NativeListView(frame: CGRect(x: 0, y: 0, width: 320, height: 708))
    for index in 0..<3 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.setContainerSlotHeightsJson("[44,44,44,0]")
    view.applySnapshotJson(try scrollingSnapshot(count: 40))
    view.layoutIfNeeded()
    let list = try XCTUnwrap(view.subviews.compactMap { $0 as? UICollectionView }.first)
    list.layoutIfNeeded()
    drainMainQueue()
    return view
  }

  private func scrollingSnapshot(count: Int, refreshing: Bool = false) throws -> String {
    let rows: [[String: Any]] = (0..<count).map {
      ["key": "row-\($0)", "type": "action", "title": "Row", "height": 80]
    }
    let value: [String: Any] = [
      "schemaVersion": 1, "generation": count + 1,
      "layout": ["kind": "linear"], "rows": rows,
      "capabilities": ["pullToRefresh": true, "refreshing": refreshing],
    ]
    return String(decoding: try JSONSerialization.data(withJSONObject: value), as: UTF8.self)
  }

  private func drainMainQueue() {
    let settled = expectation(description: "Deferred content shrink correction")
    DispatchQueue.main.async { settled.fulfill() }
    wait(for: [settled], timeout: 1)
  }

  private func footerCell(in view: NativeListView) -> NativeListRowHost? {
    view.subviews.lazy.compactMap { container in
      container.subviews.compactMap { $0 as? NativeListRowHost }.first
    }.first
  }
}
