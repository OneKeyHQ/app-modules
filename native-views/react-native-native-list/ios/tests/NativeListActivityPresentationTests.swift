import UIKit
import XCTest
@testable import NativeListModule

@MainActor
final class NativeListActivityPresentationTests: XCTestCase {
  func testActivityLeadingVisibilityAndFooterAlignmentRestoreOnRebind() throws {
    func item(rich: Bool, hidden: Bool) throws -> NativeListItem {
      var data: [String: Any] = [
        "type": "activity", "key": "tx", "title": "Send", "description": "To Alice",
        "leading": ["kind": "icon", "name": "ArrowTopOutline"],
        "hideLeading": hidden,
        "footerActions": [["key": "speed-up", "label": "Speed up"]],
      ]
      if rich { data["amounts"] = [["key": "send", "text": "••••"]] }
      else { data["primaryAmount"] = "••••" }
      return try NativeListItem(data: data)
    }
    let details = NativeListActivityDetailsView()
    let main = try XCTUnwrap(details.arrangedSubviews.first as? UIStackView)
    let richVisual = try XCTUnwrap(main.arrangedSubviews.first)
    let richActions = try XCTUnwrap(details.arrangedSubviews.last as? UIStackView)
    let container = UIView(frame: CGRect(x: 0, y: 0, width: 366, height: 300))
    details.translatesAutoresizingMaskIntoConstraints = false
    container.addSubview(details)
    NSLayoutConstraint.activate([
      details.leadingAnchor.constraint(equalTo: container.leadingAnchor),
      details.trailingAnchor.constraint(equalTo: container.trailingAnchor),
      details.topAnchor.constraint(equalTo: container.topAnchor),
    ])
    for hidden in [false, true, false] {
      let current = try item(rich: true, hidden: hidden)
      details.bind(current, theme: nil, fonts: NativeListFontFamilies())
      container.layoutIfNeeded()
      XCTAssertEqual(richVisual.isHidden, hidden)
      XCTAssertEqual(richActions.directionalLayoutMargins.leading, hidden ? 0 : 52)
      XCTAssertEqual(
        NativeListActivityDetailsView.measure(
          current.data, width: 390,
          fonts: NativeListFontFamilies()),
        ceil(details.frame.height) + 16, accuracy: 1)
      func hasAmount(_ view: UIView) -> Bool {
        if (view as? UILabel)?.text == "••••" { return true }
        return view.subviews.contains(where: hasAmount)
      }
      XCTAssertTrue(hasAmount(details))
    }

    let cell = NativeListActivityCell(frame: CGRect(x: 0, y: 0, width: 390, height: 100))
    let legacyVisual = try XCTUnwrap(cell.root.arrangedSubviews.first)
    let column = try XCTUnwrap(cell.root.arrangedSubviews[1] as? UIStackView)
    let legacyActions = try XCTUnwrap(column.arrangedSubviews.last as? UIStackView)
    for hidden in [false, true, false] {
      cell.bind(item: try item(rich: false, hidden: hidden), theme: nil,
        layout: "linear", selected: false, checkboxState: { _, _, fallback in fallback },
        fonts: NativeListFontFamilies())
      XCTAssertEqual(legacyVisual.isHidden, hidden)
      XCTAssertFalse(legacyActions.isHidden)
      XCTAssertEqual((legacyActions.arrangedSubviews.first as? UIButton)?.currentTitle, "Speed up")
    }
  }

  func testSmallNumberRunsPreserveBaselineAcrossAmountsFiatAndFee() throws {
    func segments(_ prefix: String, _ suffix: String) -> [[String: Any]] {
      [["text": prefix], ["text": "5", "style": "subscript"], ["text": suffix]]
    }
    let item = try NativeListItem(data: [
      "type": "activity", "key": "transfer", "title": "Transfer",
      "leading": ["kind": "icon", "name": "ArrowTopOutline"],
      "amounts": [[
        "key": "receive", "text": "0.051 ETH", "textSegments": segments("0.0", "1 ETH"),
        "secondaryText": "$0.051", "secondaryTextSegments": segments("$0.0", "1"),
      ]],
      "fee": [
        "primary": "0.051 GAS", "primaryTextSegments": segments("0.0", "1 GAS"),
        "secondary": "€0.051", "secondaryTextSegments": segments("€0.0", "1"),
      ],
    ])
    let view = NativeListActivityDetailsView()
    view.bind(item, theme: nil, fonts: NativeListFontFamilies())
    func labels(in view: UIView) -> [UILabel] {
      (view as? UILabel).map { [$0] } ?? view.subviews.flatMap { labels(in: $0) }
    }
    let cases: [(String, Int, CGFloat)] = [
      ("0.051 ETH", 3, 16), ("$0.051", 4, 14),
      ("0.051 GAS", 3, 14), ("€0.051", 4, 12),
    ]
    let rendered = labels(in: view)
    for (text, countIndex, parentSize) in cases {
      let label = try XCTUnwrap(rendered.first { $0.attributedText?.string == text })
      let value = try XCTUnwrap(label.attributedText)
      let font = try XCTUnwrap(value.attribute(.font, at: countIndex, effectiveRange: nil) as? UIFont)
      XCTAssertEqual(font.pointSize, ceil(parentSize * 0.6))
      let baseline = value.attribute(.baselineOffset, at: 0, effectiveRange: nil) as? NSNumber
      let countBaseline = value.attribute(.baselineOffset, at: countIndex, effectiveRange: nil) as? NSNumber
      XCTAssertEqual(countBaseline?.doubleValue ?? 0, baseline?.doubleValue ?? 0)
    }
  }

  func testAutomaticHeightMatchesRenderedStacks() throws {
    let amount: [String: Any] = ["key": "send", "text": "-0.5 ETH", "secondaryText": "$1,000.00"]
    let cases: [(String, [String: Any])] = [
      ("stacked single-line", [
        "presentation": "stacked", "description": "Jan 1 • 0x1234…abcd", "amounts": [amount],
        "style": ["horizontalPadding": 20, "verticalPadding": 12, "lineGap": 0,
          "title": ["fontSize": 16, "lineHeight": 24, "fontWeight": "medium", "lines": 1],
          "description": ["fontSize": 14, "lineHeight": 20, "lines": 1]],
      ]),
      ("table line gap without secondary amount", [
        "presentation": "table", "description": "0x1234…abcd",
        "amounts": [["key": "approve", "text": "Unlimited USDC"]],
        "fee": ["label": "Network fee", "primary": "0.0001 ETH", "secondary": "$0.20"],
        "badges": [["key": "risk", "text": "Scam", "tone": "danger"]],
        "style": ["horizontalPadding": 20, "verticalPadding": 12, "lineGap": 4,
          "title": ["fontSize": 14, "lineHeight": 20, "fontWeight": "medium", "lines": 1],
          "description": ["fontSize": 14, "lineHeight": 20, "lines": 1]],
      ]),
      ("wrapped description, status and actions", [
        "description": String(repeating: "Contract interaction with a long label ", count: 3),
        "status": "Pending", "amounts": [amount, ["key": "receive", "text": "+1 USDC"]],
        "footerActions": [["key": "speedUp", "label": "Speed up"], ["key": "cancel", "label": "Cancel"]],
        "style": ["lineGap": 6],
      ]),
    ]
    for (name, fields) in cases {
      var data: [String: Any] = [
        "type": "activity", "key": name, "title": "Send",
        "leading": ["kind": "icon", "name": "ArrowTopOutline"],
      ]
      fields.forEach { data[$0.key] = $0.value }
      let item = try NativeListItem(data: data)
      let width: CGFloat = 390
      let style = data["style"] as? [String: Any] ?? [:]
      let table = data["presentation"] as? String == "table"
      let horizontal = CGFloat(style.double("horizontalPadding", default: table ? 16 : 12))
      let vertical = CGFloat(style.double("verticalPadding", default: 8))
      let container = UIView(frame: CGRect(x: 0, y: 0, width: width - horizontal * 2, height: 1000))
      let view = NativeListActivityDetailsView()
      view.translatesAutoresizingMaskIntoConstraints = false
      container.addSubview(view)
      NSLayoutConstraint.activate([
        view.leadingAnchor.constraint(equalTo: container.leadingAnchor),
        view.trailingAnchor.constraint(equalTo: container.trailingAnchor),
        view.topAnchor.constraint(equalTo: container.topAnchor),
      ])
      view.bind(item, theme: nil, fonts: NativeListFontFamilies())
      container.layoutIfNeeded()
      container.layoutIfNeeded()
      let rendered = ceil(view.frame.height) + vertical * 2
      XCTAssertEqual(
        NativeListActivityDetailsView.measure(data, width: width, fonts: NativeListFontFamilies()),
        rendered, accuracy: 1, name)
    }
  }
}
