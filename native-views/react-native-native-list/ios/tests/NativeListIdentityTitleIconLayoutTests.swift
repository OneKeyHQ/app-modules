import UIKit
import XCTest
@testable import NativeListModule

@MainActor
final class NativeListIdentityTitleIconLayoutTests: XCTestCase {
  private func child<T>(_ cell: NativeListIdentityCell, _ label: String, as type: T.Type) throws -> T {
    try XCTUnwrap(Mirror(reflecting: cell).children.first { $0.label == label }?.value as? T)
  }

  // OK-66041: the Home native-token row carries a GasSolid `titleIcon`. The
  // glyph must sit right after the title text (4pt gap), as Android's
  // PackedTitleLineLayout and the Web flex row already do. iOS let the title
  // label stretch across the whole title row, so the icon landed next to the
  // trailing value column instead of beside "ETH".
  func testTitleIconFollowsTheTitleTextInsteadOfTheRowEnd() throws {
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 390, height: 60))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 390, height: 200))
    window.addSubview(cell)
    let titleIcon = try child(cell, "titleIconView", as: UIImageView.self)
    let titleLabel = try child(cell, "titleLabel", as: UILabel.self)
    let titleStack = try child(cell, "titleRowStack", as: UIStackView.self)
    let item = try NativeListItem(data: [
      "key": "eth", "type": "identity", "title": "ETH", "height": 60,
      "leading": ["kind": "token", "fallbackIcon": ["name": "CryptoCoinOutline"]],
      "titleIcon": ["name": "GasSolid"],
      "subtitleSegments": [
        ["text": "$2,330.23", "tone": "secondary"],
        ["text": "+0.23%", "tone": "positive", "separatorBefore": true],
      ],
      "trailing": [["kind": "valuePair", "primary": "0.00001417", "secondary": "$0.03"]],
    ])
    cell.bind(item: item, theme: nil, layout: "linear", selected: false,
      checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
    cell.layoutIfNeeded()

    XCTAssertFalse(titleIcon.isHidden)
    // The title keeps its intrinsic width; the slack goes to the end of the row.
    XCTAssertEqual(
      titleLabel.bounds.width, titleLabel.intrinsicContentSize.width, accuracy: 1,
      "title label must not stretch across the title row")
    let labelFrame = titleLabel.convert(titleLabel.bounds, to: cell)
    let iconFrame = titleIcon.convert(titleIcon.bounds, to: cell)
    XCTAssertEqual(iconFrame.minX, labelFrame.maxX + 4, accuracy: 1,
      "gas icon must follow the title text with the 4pt gap")
    let iconInRow = titleIcon.convert(titleIcon.bounds, to: titleStack)
    XCTAssertLessThan(iconInRow.maxX, titleStack.bounds.width / 2,
      "gas icon must stay in the leading half of the title row, not at the value column")
  }

  // A long title still truncates in front of the icon instead of pushing it
  // out of the row, and the badge keeps following the icon.
  func testLongTitleTruncatesAndKeepsIconAndBadgeAdjacent() throws {
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 320, height: 60))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
    window.addSubview(cell)
    let titleIcon = try child(cell, "titleIconView", as: UIImageView.self)
    let titleLabel = try child(cell, "titleLabel", as: UILabel.self)
    let badge = try child(cell, "badgeLabel", as: UILabel.self)
    let item = try NativeListItem(data: [
      "key": "long", "type": "identity",
      "title": "A very long token name that cannot possibly fit on one line", "height": 60,
      "leading": ["kind": "token", "fallbackIcon": ["name": "CryptoCoinOutline"]],
      "titleIcon": ["name": "GasSolid"],
      "badges": [["key": "native", "text": "Native"]],
      "trailing": [["kind": "valuePair", "primary": "1.00", "secondary": "$1.00"]],
    ])
    cell.bind(item: item, theme: nil, layout: "linear", selected: false,
      checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
    cell.layoutIfNeeded()

    let labelFrame = titleLabel.convert(titleLabel.bounds, to: cell)
    let iconFrame = titleIcon.convert(titleIcon.bounds, to: cell)
    let badgeFrame = badge.convert(badge.bounds, to: cell)
    XCTAssertEqual(titleIcon.bounds.width, 20, accuracy: 0.5)
    XCTAssertEqual(iconFrame.minX, labelFrame.maxX + 4, accuracy: 1)
    XCTAssertFalse(badge.isHidden)
    XCTAssertGreaterThan(badgeFrame.minX, iconFrame.maxX)
    XCTAssertLessThanOrEqual(badgeFrame.maxX, cell.bounds.width)
  }

  // An address book row: a plain title after the 16pt `titleLeadingIcon`. A
  // long name truncates after the glyph instead of squeezing it to nothing.
  func testLongPlainTitleTruncatesAfterTheLeadingIcon() throws {
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 320, height: 60))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
    window.addSubview(cell)
    let leadingIcon = try child(cell, "titleLeadingIconView", as: UIImageView.self)
    let titleLabel = try child(cell, "titleLabel", as: UILabel.self)
    let item = try NativeListItem(data: [
      "key": "book", "type": "identity",
      "title": "A very long address book name that cannot possibly fit on one line",
      "height": 60,
      "leading": ["kind": "token", "fallbackIcon": ["name": "CryptoCoinOutline"]],
      "titleLeadingIcon": ["name": "BookOpenOutline"],
      "subtitle": "0x1234...abcd",
    ])
    cell.bind(item: item, theme: nil, layout: "linear", selected: false,
      checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
    cell.layoutIfNeeded()

    let iconFrame = leadingIcon.convert(leadingIcon.bounds, to: cell)
    let labelFrame = titleLabel.convert(titleLabel.bounds, to: cell)
    XCTAssertFalse(leadingIcon.isHidden)
    XCTAssertEqual(leadingIcon.bounds.width, 16, accuracy: 0.5,
      "the leading glyph keeps its 16pt width")
    XCTAssertEqual(labelFrame.minX, iconFrame.maxX + 6, accuracy: 1,
      "the title follows the glyph with the 6pt gap")
    XCTAssertLessThan(titleLabel.bounds.width, titleLabel.intrinsicContentSize.width,
      "the title truncates instead of the glyph")
    XCTAssertLessThanOrEqual(labelFrame.maxX, cell.bounds.width)
  }

  // A wallet sidebar row keeps the plain title even when a production row
  // carries runs and a leading glyph, also after the cell drew both for a
  // recipient row.
  func testWalletSidebarRowIgnoresTitleRunsAndLeadingIcon() throws {
    let cell = NativeListIdentityCell(frame: CGRect(x: 0, y: 0, width: 320, height: 68))
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
    window.addSubview(cell)
    let titleLabel = try child(cell, "titleLabel", as: UILabel.self)
    let runs = try child(cell, "titleSegmentsLabel", as: UIView.self)
    let leadingIcon = try child(cell, "titleLeadingIconView", as: UIImageView.self)
    let decorations: [String: Any] = [
      "titleSegments": [
        ["text": "Main", "shrink": true],
        ["text": " / ", "tone": "secondary"],
        ["text": "Account 1"],
      ],
      "titleLeadingIcon": ["name": "BookOpenOutline"],
    ]
    func bind(_ data: [String: Any]) throws {
      var row: [String: Any] = [
        "key": "wallet", "type": "identity", "title": "Main / Account 1", "height": 68,
      ]
      data.forEach { row[$0.key] = $0.value }
      cell.bind(item: try NativeListItem(data: row), theme: nil, layout: "linear",
        selected: false, checkboxState: { _, _, fallback in fallback },
        fonts: NativeListFontFamilies())
      cell.layoutIfNeeded()
    }

    try bind(decorations)
    XCTAssertFalse(runs.isHidden)
    XCTAssertFalse(leadingIcon.isHidden)

    try bind(decorations.merging(["presentation": "walletSidebar"]) { $1 })
    XCTAssertTrue(runs.isHidden, "wallet sidebar rows draw no runs")
    XCTAssertTrue(leadingIcon.isHidden, "wallet sidebar rows draw no leading glyph")
    XCTAssertFalse(titleLabel.isHidden)
    XCTAssertEqual(titleLabel.text, "Main / Account 1")
  }

  // `titleMatch` keeps the plain highlighted title on screen, so an
  // auto-height row is measured for that title and its `titleLines`, not for
  // one line of runs.
  func testHighlightedTitleIsMeasuredWithItsOwnLines() throws {
    func height(_ data: [String: Any]) throws -> CGFloat {
      var row: [String: Any] = [
        "key": "match", "type": "identity", "contentHeight": "auto",
        "title": "A very long recipient name that needs a second line to show",
        "titleLines": 2, "titleMatch": [["start": 0, "end": 6]],
        "subtitle": "0x1234...abcd",
      ]
      data.forEach { row[$0.key] = $0.value }
      return try XCTUnwrap(NativeListIdentityCell.measure(
        try NativeListItem(data: row), width: 240, theme: nil, layout: "linear",
        fonts: NativeListFontFamilies()))
    }
    let runs: [[String: Any]] = [["text": "A very long", "shrink": true], ["text": " recipient"]]

    let plain = try height([:])
    XCTAssertEqual(try height(["titleSegments": runs]), plain, accuracy: 0.5)
    XCTAssertGreaterThan(plain, try height(["titleLines": 1]))
  }
}
