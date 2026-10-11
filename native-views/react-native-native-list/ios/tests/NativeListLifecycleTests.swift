import UIKit
import XCTest
@testable import NativeListModule

@MainActor
final class NativeListLifecycleTests: XCTestCase {
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
