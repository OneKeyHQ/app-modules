import UIKit
import XCTest
@testable import NativeListModule

@MainActor
final class NativeListLifecycleTests: XCTestCase {
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
    view.mountContainerSlot(overlay, atIndex: 3)
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
    visibleButtons()[0].sendActions(for: .touchUpInside)
    XCTAssertEqual(actions.last?.rowKey, "row")
    XCTAssertEqual(actions.last?.key, "second")
    XCTAssertEqual(actions.last?.source, "trailingAccessory")
    XCTAssertEqual(actions.last?.slot, 1)
    let selection = try XCTUnwrap(trailing.arrangedSubviews.compactMap { $0 as? UIButton }
      .first { !($0 is NativeListAccessoryButton) && !$0.isHidden })
    selection.sendActions(for: .touchUpInside)
    XCTAssertEqual(actions.last?.key, "selection")
    XCTAssertNil(actions.last?.slot)

    try bind([first, second])
    XCTAssertEqual(visibleButtons().count, 2)
    visibleButtons()[0].sendActions(for: .touchUpInside)
    XCTAssertEqual(actions.last?.key, "first")
    XCTAssertEqual(actions.last?.slot, 0)
    visibleButtons()[1].sendActions(for: .touchUpInside)
    XCTAssertEqual(actions.last?.key, "second")
    XCTAssertEqual(actions.last?.slot, 1)

    try bind([second], key: "replacement")
    XCTAssertEqual(visibleButtons().count, 1)
    visibleButtons()[0].sendActions(for: .touchUpInside)
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
    first.sendActions(for: .touchUpInside)
    XCTAssertEqual(actions.last?.rowKey, "tertiary-first")
    XCTAssertEqual(actions.last?.source, "trailingAccessory")
    XCTAssertEqual(actions.last?.slot, 1)
    XCTAssertEqual(actions.last?.inset, 7)
    let firstOrigin = try XCTUnwrap(actions.last)
    XCTAssertEqual(firstOrigin.width - 2 * firstOrigin.inset, 24, accuracy: 0.5)

    let normal = try bind([ordinary], key: "ordinary")
    XCTAssertEqual(normal.bounds.width, 36, accuracy: 0.5)
    normal.sendActions(for: .touchUpInside)
    XCTAssertEqual(actions.last?.rowKey, "ordinary")
    XCTAssertEqual(actions.last?.slot, 0)
    XCTAssertEqual(actions.last?.inset, 0)
    XCTAssertEqual(actions.last?.width, 36)

    let value = try bind([["kind": "value", "text": "Value"]], key: "value")
    value.bounds.size.width = 38
    XCTAssertEqual(trailing.anchorInset(for: value), 0)

    let rebound = try bind([tertiary], key: "tertiary-again")
    rebound.sendActions(for: .touchUpInside)
    XCTAssertEqual(actions.last?.rowKey, "tertiary-again")
    XCTAssertEqual(actions.last?.slot, 0)
    XCTAssertEqual(actions.last?.inset, 7)
    let reboundOrigin = try XCTUnwrap(actions.last)
    XCTAssertEqual(reboundOrigin.width - 2 * reboundOrigin.inset, 24, accuracy: 0.5)

    let account = try bind([ordinary], key: "account", presentation: "accountSelector")
    XCTAssertEqual(account.bounds.width, 38, accuracy: 0.5)
    account.sendActions(for: .touchUpInside)
    XCTAssertEqual(actions.last?.rowKey, "account")
    XCTAssertEqual(actions.last?.slot, 0)
    XCTAssertEqual(actions.last?.inset, 7)
    let accountOrigin = try XCTUnwrap(actions.last)
    XCTAssertEqual(accountOrigin.width - 2 * accountOrigin.inset, 24, accuracy: 0.5)

    let finalOrdinary = try bind([ordinary], key: "ordinary-again")
    finalOrdinary.sendActions(for: .touchUpInside)
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

    view.setContainerSlotHeightsJson("[44,44,60]")
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

  private func makeScrollingList() throws -> NativeListView {
    let view = NativeListView(frame: CGRect(x: 0, y: 0, width: 320, height: 708))
    for index in 0..<3 { view.mountContainerSlot(UIView(), atIndex: index) }
    view.setContainerSlotHeightsJson("[44,44,44]")
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
