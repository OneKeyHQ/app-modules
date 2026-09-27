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
                checkboxState: { _, _, fallback in fallback })
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
                checkboxState: { _, _, fallback in fallback })
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
