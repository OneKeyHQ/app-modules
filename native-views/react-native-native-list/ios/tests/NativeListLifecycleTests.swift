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

  private func footerCell(in view: NativeListView) -> NativeListRowHost? {
    view.subviews.lazy.compactMap { container in
      container.subviews.compactMap { $0 as? NativeListRowHost }.first
    }.first
  }
}
