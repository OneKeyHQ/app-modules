import UIKit
import XCTest
@testable import NativeListModule

@MainActor
final class NativeListActivityPresentationTests: XCTestCase {
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
    view.bind(item, theme: nil)
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
      view.bind(item, theme: nil)
      container.layoutIfNeeded()
      container.layoutIfNeeded()
      let rendered = ceil(view.frame.height) + vertical * 2
      XCTAssertEqual(NativeListActivityDetailsView.measure(data, width: width), rendered, accuracy: 1, name)
    }
  }
}
