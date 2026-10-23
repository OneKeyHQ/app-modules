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
      XCTAssertGreaterThan(mainStack.bounds.width, 20)
      XCTAssertEqual(trailing.frame.maxX, cell.root.bounds.maxX, accuracy: 0.5)
      if capped {
        XCTAssertLessThanOrEqual(mainStack.bounds.width, cell.root.bounds.width * 0.6 - 52 + 0.5)
      }
    }

    try bind(base)
    XCTAssertGreaterThan(titleLabel.bounds.width, 20)
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
