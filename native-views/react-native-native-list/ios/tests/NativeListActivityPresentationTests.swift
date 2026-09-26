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
}
