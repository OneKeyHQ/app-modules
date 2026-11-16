import UIKit
import XCTest
@testable import NativeListModule

@MainActor
final class NativeListDataRowTitleIconLayoutTests: XCTestCase {
  private func child<T>(_ owner: Any, _ name: String, as type: T.Type) throws -> T {
    try XCTUnwrap(Mirror(reflecting: owner).children.first { $0.label == name }?.value as? T)
  }

  private func bind(
    _ cell: NativeListDataRowCell, title: String = "XMR", subtitle: String = "Syncing",
    icon: Bool = true, secondIcon: Bool = false, badges: [[String: Any]] = [],
    alignment: String = "start", primaryStyle: [String: Any] = [:],
    secondaryStyle: [String: Any] = [:]
  ) throws {
    var asset: [String: Any] = [
      "key": "asset", "text": title, "secondaryText": subtitle, "weight": 2,
      "alignment": alignment,
    ]
    if icon { asset["titleIcon"] = ["name": "GasSolid"] }
    if secondIcon { asset["secondaryTitleIcon"] = ["name": "InfoCircleOutline"] }
    let item = try NativeListItem(data: [
      "key": "xmr", "type": "dataRow", "height": 76,
      "columns": [asset, [
        "key": "balance", "text": "0.123", "secondaryText": "$12.34",
        "alignment": "end", "weight": 1,
      ]],
      "badges": badges, "trailing": [["kind": "spinner"]],
      "style": [
        "columns": (["fontSize": 16, "lineHeight": 24, "fontWeight": "medium"] as [String: Any])
          .merging(primaryStyle) { _, new in new },
        "columnSecondary": (["fontSize": 14, "lineHeight": 20] as [String: Any])
          .merging(secondaryStyle) { _, new in new }, "lineGap": 0,
      ],
    ])
    cell.bind(item: item, theme: nil, layout: "linear", selected: false,
      checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
    cell.setNeedsLayout()
    cell.layoutIfNeeded()
  }

  private func frame(_ view: UIView, in cell: NativeListDataRowCell) -> CGRect {
    view.convert(view.bounds, to: cell)
  }

  func testLongSubtitleDoesNotMoveIconAwayFromShortTitle() throws {
    for width: CGFloat in [320, 390] {
      let cell = NativeListDataRowCell(frame: CGRect(x: 0, y: 0, width: width, height: 76))
      let window = UIWindow(frame: CGRect(x: 0, y: 0, width: width, height: 200))
      window.addSubview(cell)
      try bind(cell, subtitle: "Syncing a much longer subtitle")
      let columns = try child(cell, "cells", as: [UIStackView].self)
      let title = try child(columns[0], "primaryLabel", as: UILabel.self)
      let subtitle = try child(columns[0], "secondaryLabel", as: UILabel.self)
      let icon = try child(columns[0], "titleIconView", as: UIImageView.self)

      XCTAssertEqual(title.attributedText?.string, "XMR")
      XCTAssertFalse(subtitle.isHidden)
      XCTAssertEqual(title.bounds.width, title.intrinsicContentSize.width, accuracy: 1)
      XCTAssertEqual(frame(icon, in: cell).minX, frame(title, in: cell).maxX + 4, accuracy: 1)
      XCTAssertEqual(frame(icon, in: cell).midY, frame(title, in: cell).midY, accuracy: 1)
      XCTAssertGreaterThanOrEqual(frame(subtitle, in: cell).minY, frame(title, in: cell).maxY - 0.5)
      XCTAssertEqual(title.font.pointSize, 16)
      XCTAssertEqual(subtitle.font.pointSize, 14)
      XCTAssertEqual(columns[0].spacing, 0)
      let paragraph = title.attributedText?.attribute(.paragraphStyle, at: 0, effectiveRange: nil)
        as? NSParagraphStyle
      XCTAssertEqual(paragraph?.minimumLineHeight, 24)
      let balance = try child(columns[1], "primaryLabel", as: UILabel.self)
      XCTAssertEqual(balance.textAlignment, .right)
      XCTAssertEqual(balance.attributedText?.string, "0.123\n$12.34")
      let trailing = try child(cell, "trailingAccessories", as: NativeListAccessoryStack.self)
    let spinner = try XCTUnwrap(
      trailing.arrangedSubviews.compactMap { $0 as? UIActivityIndicatorView }.first
    )
      XCTAssertTrue(spinner.isAnimating)
      XCTAssertGreaterThanOrEqual(frame(spinner, in: cell).minX, frame(columns[1], in: cell).maxX)
    }
  }

  func testLongTitleKeepsBothIconsAndBadgeInOrder() throws {
    let cell = NativeListDataRowCell(frame: CGRect(x: 0, y: 0, width: 390, height: 76))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 390, height: 200))
    window.addSubview(cell)
    try bind(cell, title: "A token title too long to fit", secondIcon: true,
      badges: [["key": "native", "text": "Native"]])
    let column = try child(cell, "cells", as: [UIStackView].self)[0]
    let title = try child(column, "primaryLabel", as: UILabel.self)
    let icon = try child(column, "titleIconView", as: UIImageView.self)
    let second = try child(column, "secondaryTitleIconView", as: UIImageView.self)
    let badges = try child(column, "badgesStack", as: UIStackView.self)
    XCTAssertLessThan(title.bounds.width, title.intrinsicContentSize.width)
    XCTAssertEqual(frame(icon, in: cell).minX, frame(title, in: cell).maxX + 4, accuracy: 1)
    XCTAssertEqual(frame(second, in: cell).minX, frame(icon, in: cell).maxX + 4, accuracy: 1)
    XCTAssertGreaterThanOrEqual(frame(badges, in: cell).minX, frame(second, in: cell).maxX)
    XCTAssertLessThanOrEqual(frame(badges, in: cell).maxX, frame(column, in: cell).maxX + 0.5)
  }

  func testReuseRestoresLegacyTextAndClearsOldIconsAndBadges() throws {
    let cell = NativeListDataRowCell(frame: CGRect(x: 0, y: 0, width: 390, height: 76))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 390, height: 200))
    window.addSubview(cell)
    for iconEnabled in [true, false, true, false] {
      try bind(cell, subtitle: "Monero", icon: iconEnabled,
        badges: [["key": "status", "text": "Syncing"]])
      let column = try child(cell, "cells", as: [UIStackView].self)[0]
      let title = try child(column, "primaryLabel", as: UILabel.self)
      let subtitle = try child(column, "secondaryLine", as: UIStackView.self)
      let icon = try child(column, "titleIconView", as: UIImageView.self)
      let badges = try child(column, "badgesStack", as: UIStackView.self)
      XCTAssertEqual(icon.isHidden, !iconEnabled)
      XCTAssertEqual(subtitle.isHidden, !iconEnabled)
      XCTAssertEqual(badges.arrangedSubviews.count, iconEnabled ? 1 : 0)
      XCTAssertEqual(title.attributedText?.string, iconEnabled ? "XMR" : "XMR  Syncing \nMonero")
    }
    try bind(cell, subtitle: "", icon: false)
    let column = try child(cell, "cells", as: [UIStackView].self)[0]
    XCTAssertTrue(try child(column, "secondaryLine", as: UIStackView.self).isHidden)
    XCTAssertEqual(try child(column, "primaryLabel", as: UILabel.self).attributedText?.string, "XMR")
  }

  func testEndAlignedIconGroupKeepsItsTrailingEdge() throws {
    let cell = NativeListDataRowCell(frame: CGRect(x: 0, y: 0, width: 390, height: 76))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 390, height: 200))
    window.addSubview(cell)
    try bind(cell, alignment: "end")
    let column = try child(cell, "cells", as: [UIStackView].self)[0]
    let icon = try child(column, "titleIconView", as: UIImageView.self)
    XCTAssertEqual(frame(icon, in: cell).maxX, frame(column, in: cell).maxX, accuracy: 1)
  }

  func testPrimaryAndSecondaryStylesRemainIndependent() throws {
    let cell = NativeListDataRowCell(frame: CGRect(x: 0, y: 0, width: 390, height: 76))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 390, height: 200))
    window.addSubview(cell)
    try bind(cell, primaryStyle: ["alignment": "end"])
    let column = try child(cell, "cells", as: [UIStackView].self)[0]
    let title = try child(column, "primaryLabel", as: NativeListTextLabel.self)
    let subtitle = try child(column, "secondaryLabel", as: NativeListTextLabel.self)
    XCTAssertEqual(title.textAlignment, .right)
    XCTAssertEqual(subtitle.textAlignment, .left)

    try bind(cell, secondaryStyle: ["alignment": "end", "offsetY": 3, "truncate": "clip"])
    XCTAssertEqual(title.textAlignment, .left)
    XCTAssertEqual(subtitle.textAlignment, .right)
    XCTAssertEqual(title.rowOffsetY, 0)
    XCTAssertEqual(subtitle.rowOffsetY, 3)
    XCTAssertEqual(title.lineBreakMode, .byTruncatingTail)
    XCTAssertEqual(subtitle.lineBreakMode, .byClipping)
    XCTAssertEqual(frame(subtitle, in: cell).maxX, frame(column, in: cell).maxX, accuracy: 1)
  }

  func testCenteredPrimarySpacesAreClearedWhenReused() throws {
    let cell = NativeListDataRowCell(frame: CGRect(x: 0, y: 0, width: 390, height: 76))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 390, height: 200))
    window.addSubview(cell)
    try bind(cell, alignment: "center")
    let column = try child(cell, "cells", as: [UIStackView].self)[0]
    let leading = try child(column, "primaryLeadingSpace", as: UIView.self)
    let trailing = try child(column, "primaryTrailingSpace", as: UIView.self)
    let equalSpaces = try child(column, "equalPrimarySpaces", as: NSLayoutConstraint?.self)
    XCTAssertFalse(leading.isHidden)
    XCTAssertFalse(trailing.isHidden)
    XCTAssertEqual(leading.bounds.width, trailing.bounds.width, accuracy: 1)
    XCTAssertEqual(equalSpaces?.isActive, true)
    try bind(cell, icon: false)
    XCTAssertTrue(leading.isHidden)
    XCTAssertTrue(trailing.isHidden)
    XCTAssertEqual(equalSpaces?.isActive, false)
  }
}
