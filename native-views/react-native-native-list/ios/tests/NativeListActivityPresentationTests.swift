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

  // The description label spans the identity column, so a tap anywhere on its
  // line used to open the description action. Only the text opens it now; the
  // blank rest of the line belongs to the row press (OK-67219).
  func testDescriptionActionTakesTouchesOnItsTextOnly() throws {
    let details = NativeListActivityDetailsView()
    let container = UIView(frame: CGRect(x: 0, y: 0, width: 366, height: 300))
    details.translatesAutoresizingMaskIntoConstraints = false
    container.addSubview(details)
    NSLayoutConstraint.activate([
      details.leadingAnchor.constraint(equalTo: container.leadingAnchor),
      details.trailingAnchor.constraint(equalTo: container.trailingAnchor),
      details.topAnchor.constraint(equalTo: container.topAnchor),
    ])
    func bind(description: String, actionKey: String?) throws -> NativeListTextLabel {
      var data: [String: Any] = [
        "type": "activity", "key": "tx", "title": "Swap", "description": description,
        "leading": ["kind": "icon", "name": "ArrowTopOutline"],
        "amounts": [["key": "send", "text": "-1"]],
      ]
      if let actionKey { data["descriptionActionKey"] = actionKey }
      details.bind(try NativeListItem(data: data), theme: nil, fonts: NativeListFontFamilies())
      container.layoutIfNeeded()
      func find(_ view: UIView) -> NativeListTextLabel? {
        if let label = view as? NativeListTextLabel, label.text == description { return label }
        return view.subviews.lazy.compactMap(find).first
      }
      return try XCTUnwrap(find(details))
    }
    func hit(_ label: NativeListTextLabel, x: CGFloat) -> Bool {
      label.point(inside: CGPoint(x: x, y: label.bounds.midY), with: nil)
    }

    let address = try bind(description: "Uniswap", actionKey: "address-info")
    XCTAssertGreaterThan(address.bounds.width, address.touchableTextBounds.maxX + 40)
    XCTAssertTrue(hit(address, x: 4))
    XCTAssertFalse(hit(address, x: address.bounds.maxX - 4))

    // Of "time • address" only the address opens the action, as on Web.
    let timed = try bind(description: "08:48 • Uniswap", actionKey: "address-info")
    XCTAssertFalse(hit(timed, x: 4))
    XCTAssertTrue(hit(timed, x: timed.touchableTextBounds.maxX - 12))
    XCTAssertFalse(hit(timed, x: timed.bounds.maxX - 4))

    // A reused view without the action takes no special touch handling.
    let plain = try bind(description: "Uniswap", actionKey: nil)
    XCTAssertFalse(plain.touchesTextOnly)
    XCTAssertTrue(hit(plain, x: plain.bounds.maxX - 4))
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

  func testTableColumnsStayStableAcrossFeeLeadingAndAmountReuseWithForcedRendererDirection() throws {
    let width: CGFloat = 900
    let horizontal: CGFloat = 16
    let contentWidth = width - horizontal * 2
    let gap: CGFloat = 12
    let container = UIView(frame: CGRect(x: 0, y: 0, width: contentWidth, height: 4000))
    let details = NativeListActivityDetailsView()
    details.translatesAutoresizingMaskIntoConstraints = false
    container.addSubview(details)
    NSLayoutConstraint.activate([
      details.leadingAnchor.constraint(equalTo: container.leadingAnchor),
      details.trailingAnchor.constraint(equalTo: container.trailingAnchor),
      details.topAnchor.constraint(equalTo: container.topAnchor),
    ])
    let main = try XCTUnwrap(details.arrangedSubviews.first as? UIStackView)
    let amounts = try XCTUnwrap(main.arrangedSubviews[2] as? UIStackView)
    let fee = try XCTUnwrap(main.arrangedSubviews[3] as? UIStackView)
    for rtl in [false, true] {
      let direction: UIUserInterfaceLayoutDirection = rtl ? .rightToLeft : .leftToRight
      // This fixture covers renderer geometry; App-level RTL propagation is separate.
      func applyDirection(_ view: UIView) {
        view.semanticContentAttribute = rtl ? .forceRightToLeft : .forceLeftToRight
        view.subviews.forEach(applyDirection)
      }
      for hasFee in [true, false, true] {
        var previousHeight: CGFloat = 0
        for count in [1, 2, 6, 32] {
          for hideLeading in [false, true, false] {
            for feeKind in 0...2 {
              var data: [String: Any] = [
                "type": "activity", "key": "table", "title": "Send",
                "description": "Contract interaction with a long description that wraps",
                "leading": ["kind": "icon", "name": "ArrowTopOutline"],
                "hideLeading": hideLeading,
                "amounts": (0..<count).map { index in [
                  "key": "amount-\(index)", "text": index % 2 == 0 ? "-123456789012345678901234567890.123456789 ETH" : "+1 USDC",
                  "secondaryText": "$1,000.00",
                  "leading": ["kind": "icon", "name": "ArrowTopOutline"],
                ] as [String: Any] },
              ]
              if hasFee {
                data["fee"] = ["label": "Network fee",
                  "primary": feeKind == 1 ? "0.000000012345678 ETH" : "1 ETH",
                  "secondary": "$0.20", "hidden": feeKind == 2]
              }
              // Create amount views before applying the fixture direction; each transition binds once.
              details.bind(try NativeListItem(data: data), theme: nil, fonts: NativeListFontFamilies())
              applyDirection(details)
              for presentation in ["table", "stacked", "table"] {
                data["presentation"] = presentation
                details.bind(try NativeListItem(data: data), theme: nil, fonts: NativeListFontFamilies())
                XCTAssertEqual(main.effectiveUserInterfaceLayoutDirection, direction)
                XCTAssertEqual(amounts.effectiveUserInterfaceLayoutDirection, direction)
                XCTAssertEqual(fee.effectiveUserInterfaceLayoutDirection, direction)
                container.setNeedsLayout()
                container.layoutIfNeeded()
                guard presentation == "table" else { continue }
                let expected = (contentWidth - gap * (hasFee ? 2 : 1)) / (hasFee ? 2.6 : 2)
                XCTAssertEqual(amounts.frame.width, expected, accuracy: 1)
                let start = rtl ? contentWidth - expected * 2 - gap : expected + gap
                XCTAssertEqual(amounts.frame.minX, start, accuracy: 1)
                XCTAssertEqual(amounts.frame.midX, start + expected / 2, accuracy: 1)
                XCTAssertEqual(amounts.arrangedSubviews.count, count)
                for amount in amounts.arrangedSubviews {
                  XCTAssertEqual(amount.frame.minX, 0, accuracy: 1)
                  XCTAssertEqual(amount.frame.width, expected, accuracy: 1)
                  let row = try XCTUnwrap(amount as? UIStackView)
                  let leading = row.arrangedSubviews[0]
                  let labels = row.arrangedSubviews[1]
                  if rtl { XCTAssertLessThanOrEqual(labels.frame.maxX, leading.frame.minX) }
                  else { XCTAssertLessThanOrEqual(leading.frame.maxX, labels.frame.minX) }
                  XCTAssertGreaterThanOrEqual(labels.frame.minX, 0)
                  XCTAssertLessThanOrEqual(labels.frame.maxX, amount.frame.width + 1)
                }
                if hasFee {
                  XCTAssertEqual(fee.frame.width, expected * 0.6, accuracy: 1)
                  XCTAssertEqual(fee.alpha, feeKind == 2 ? 0 : 1)
                  for child in fee.arrangedSubviews {
                    XCTAssertGreaterThanOrEqual(child.frame.minX, -1)
                    XCTAssertLessThanOrEqual(child.frame.maxX, fee.frame.width + 1)
                    if rtl { XCTAssertEqual(child.frame.minX, 0, accuracy: 1) }
                    else { XCTAssertEqual(child.frame.maxX, fee.frame.width, accuracy: 1) }
                  }
                }
                XCTAssertEqual(
                  NativeListActivityDetailsView.measure(data, width: width, fonts: NativeListFontFamilies()),
                  ceil(details.frame.height) + 16, accuracy: 1)
              }
            }
          }
          XCTAssertGreaterThan(details.frame.height, previousHeight)
          previousHeight = details.frame.height
        }
      }
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
