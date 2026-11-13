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
  // line used to open the description action. Only the action text opens it
  // now; the blank rest of the line belongs to the row press (OK-67219). Where
  // that text starts is the caller's `descriptionActionStart`, never something
  // read out of the string.
  func testDescriptionActionTakesTouchesOnItsTextOnly() throws {
    let details = NativeListActivityDetailsView()
    // Wide enough that the label keeps blank space beside its text even when
    // the amounts take their full half of the row.
    let container = UIView(frame: CGRect(x: 0, y: 0, width: 600, height: 300))
    details.translatesAutoresizingMaskIntoConstraints = false
    container.addSubview(details)
    NSLayoutConstraint.activate([
      details.leadingAnchor.constraint(equalTo: container.leadingAnchor),
      details.trailingAnchor.constraint(equalTo: container.trailingAnchor),
      details.topAnchor.constraint(equalTo: container.topAnchor),
    ])
    func bind(description: String, actionKey: String?, actionStart: Int? = nil) throws -> NativeListTextLabel {
      var data: [String: Any] = [
        "type": "activity", "key": "tx", "title": "Swap", "description": description,
        "leading": ["kind": "icon", "name": "ArrowTopOutline"],
        "amounts": [["key": "send", "text": "-1"]],
      ]
      if let actionKey { data["descriptionActionKey"] = actionKey }
      if let actionStart { data["descriptionActionStart"] = actionStart }
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

    // Without a start the whole text is the action, also a label holding the
    // separator a time prefix would use.
    let label = try bind(description: "Router • V2", actionKey: "address-info")
    let labelText = try XCTUnwrap(label.touchableTextRegions.first)
    XCTAssertEqual(label.touchableTextStart, 0)
    XCTAssertEqual(label.touchableTextRegions.count, 1)
    XCTAssertGreaterThan(label.bounds.width, labelText.maxX + 40)
    XCTAssertTrue(hit(label, x: 4))
    XCTAssertTrue(hit(label, x: labelText.maxX - 12))
    XCTAssertFalse(hit(label, x: label.bounds.maxX - 4))

    // With a start only the text from there on is the action: the time is
    // not, the complete label behind it is.
    let prefix = "08:48 • "
    let timed = try bind(
      description: prefix + "Router • V2", actionKey: "address-info",
      actionStart: (prefix as NSString).length)
    let timedText = try XCTUnwrap(timed.touchableTextRegions.first)
    XCTAssertEqual(timed.touchableTextRegions.count, 1)
    XCTAssertGreaterThan(timed.bounds.width, timedText.maxX + 40)
    // The same label, less the slop on the side where the time is.
    XCTAssertEqual(timedText.width, labelText.width - 8, accuracy: 1)
    XCTAssertFalse(hit(timed, x: 4))
    XCTAssertFalse(hit(timed, x: timedText.minX - 2))
    XCTAssertTrue(hit(timed, x: timedText.minX + 2))
    XCTAssertTrue(hit(timed, x: timedText.maxX - 12))
    XCTAssertFalse(hit(timed, x: timed.bounds.maxX - 4))

    // The start counts UTF-16 units: the emoji takes two.
    let emojiPrefix = "🕘 08:48 • "
    XCTAssertEqual((emojiPrefix as NSString).length, 11)
    let emoji = try bind(
      description: emojiPrefix + "Router • V2", actionKey: "address-info", actionStart: 11)
    let emojiText = try XCTUnwrap(emoji.touchableTextRegions.first)
    XCTAssertEqual(emojiText.width, labelText.width - 8, accuracy: 1)
    XCTAssertFalse(hit(emoji, x: 4))
    XCTAssertFalse(hit(emoji, x: emojiText.minX - 2))
    XCTAssertTrue(hit(emoji, x: emojiText.minX + 2))
    XCTAssertTrue(hit(emoji, x: emojiText.maxX - 12))

    // A start at the end of the text, or clamped to it, leaves nothing to tap.
    // 4294967296 is a safe integer past 32 bits; it must clamp, not wrap to 0.
    for start in [(prefix as NSString).length, 99, 4_294_967_296] {
      let ended = try bind(description: prefix, actionKey: "address-info", actionStart: start)
      XCTAssertTrue(ended.touchableTextRegions.isEmpty)
      XCTAssertFalse(ended.isUserInteractionEnabled)
      XCTAssertFalse(hit(ended, x: 4))
    }

    // The reused view keeps no state: a rebind without a start covers the
    // whole text again, one without the action restores the label's own hit
    // testing even when a start is still supplied, and so does recycling.
    let rebound = try bind(description: "Router • V2", actionKey: "address-info")
    XCTAssertEqual(rebound.touchableTextStart, 0)
    XCTAssertTrue(rebound.isUserInteractionEnabled)
    XCTAssertTrue(hit(rebound, x: 4))
    let plain = try bind(description: "Router • V2", actionKey: nil, actionStart: 3)
    XCTAssertNil(plain.touchableTextStart)
    XCTAssertFalse(plain.isUserInteractionEnabled)
    XCTAssertTrue(hit(plain, x: plain.bounds.maxX - 4))
    let recycled = try bind(
      description: prefix + "Router", actionKey: "address-info",
      actionStart: (prefix as NSString).length)
    XCTAssertEqual(recycled.touchableTextStart, (prefix as NSString).length)
    details.recycle()
    XCTAssertNil(recycled.touchableTextStart)
  }

  // A narrow column wraps "time • address". The time stays outside the action
  // on its line, while the address is hit on both lines, also at the x the
  // time occupies one line above. The label is sized directly so the wrap does
  // not depend on how the row shares its width.
  func testWrappedDescriptionKeepsTheTimeOutsideTheAction() throws {
    let description = "08:48 • Uniswap Router"
    let label = NativeListTextLabel()
    NativeListResolvedText(
      description, style: nil, size: 14, color: .label, lineHeight: 20, lines: 2,
      fonts: NativeListFontFamilies()
    ).bind(label)
    label.frame = CGRect(x: 0, y: 0, width: 142, height: 40)
    label.touchableTextStart = ("08:48 • " as NSString).length
    func hit(_ x: CGFloat, _ y: CGFloat) -> Bool {
      label.point(inside: CGPoint(x: x, y: y), with: nil)
    }

    let lines = label.touchableTextRegions
    XCTAssertEqual(lines.count, 2)
    guard lines.count == 2 else { return }
    XCTAssertFalse(hit(4, lines[0].midY))
    XCTAssertTrue(hit(lines[0].maxX - 12, lines[0].midY))
    XCTAssertTrue(hit(4, lines[1].midY))
    XCTAssertFalse(hit(label.bounds.maxX - 2, lines[1].midY))
  }

  // The first line holds only the time, so it takes no touches, and the slop
  // of the address line below it must not reach up into the time.
  func testAddressStartingOnTheSecondLineLeavesTheTimeLineAlone() throws {
    let prefix = "08:48 • "
    let address = "UniswapUniversalRouter"
    func text(_ value: String) -> NativeListResolvedText {
      NativeListResolvedText(
        value, style: nil, size: 14, color: .label, lineHeight: 20, lines: 2,
        fonts: NativeListFontFamilies())
    }
    let label = NativeListTextLabel()
    text(prefix + address).bind(label)
    // Wide enough for the address alone, too narrow for the time beside it.
    label.frame = CGRect(x: 0, y: 0, width: text(address).measureWidth() + 4, height: 40)
    label.touchableTextStart = (prefix as NSString).length
    func hit(y: CGFloat) -> Bool { label.point(inside: CGPoint(x: 4, y: y), with: nil) }

    let line = try XCTUnwrap(label.touchableTextRegions.first)
    XCTAssertEqual(label.touchableTextRegions.count, 1)
    XCTAssertEqual(line.minY, 20, accuracy: 0.5)
    XCTAssertFalse(hit(y: 10))
    XCTAssertFalse(hit(y: line.minY - 2))
    XCTAssertTrue(hit(y: line.minY + 2))
    // The last line of the layout still takes its slop below the text.
    XCTAssertTrue(hit(y: label.bounds.maxY + 4))
  }

  private func descriptionText(_ value: String, style: [String: Any]? = nil) -> NativeListResolvedText {
    NativeListResolvedText(
      value, style: style, size: 14, color: .label, lineHeight: 20, lines: 1,
      fonts: NativeListFontFamilies())
  }

  /// Width the description font gives `value` on its own.
  private func descriptionWidth(_ value: String) -> CGFloat {
    descriptionText(value).attributed(direction: .leftToRight).size().width
  }

  private func descriptionLabel(
    _ value: String, start: Int, width: CGFloat = 300, style: [String: Any]? = nil
  ) -> NativeListTextLabel {
    let label = NativeListTextLabel()
    descriptionText(value, style: style).bind(label)
    label.frame = CGRect(x: 0, y: 0, width: width, height: 20)
    label.touchableTextStart = start
    return label
  }

  private func hit(_ label: NativeListTextLabel, x: CGFloat) -> Bool {
    label.point(inside: CGPoint(x: x, y: label.bounds.midY), with: nil)
  }

  // The slop is for blank space. It stops at plain text beside the action
  // text, or a prefix narrower than the slop, like the "i" of "iRouter", would
  // open an action it is not part of.
  func testSlopStopsAtPlainTextBesideTheActionText() throws {
    let short = descriptionLabel("iRouter", start: 1)
    let i = descriptionWidth("i")
    XCTAssertLessThan(i, 8)
    let region = try XCTUnwrap(short.touchableTextRegions.first)
    XCTAssertEqual(short.touchableTextRegions.count, 1)
    XCTAssertEqual(region.minX, i, accuracy: 0.5)
    XCTAssertFalse(hit(short, x: i / 2))
    XCTAssertFalse(hit(short, x: -3))
    XCTAssertTrue(hit(short, x: i + 2))
    // Into the blank after the text the slop still reaches.
    XCTAssertTrue(hit(short, x: descriptionWidth("iRouter") + 6))
    XCTAssertFalse(hit(short, x: descriptionWidth("iRouter") + 10))
    // And into the blank before a line that starts with the action text.
    XCTAssertTrue(hit(descriptionLabel("Router", start: 0), x: -6))
  }

  // Mixed-direction text can split one logical suffix into several visual
  // runs. From offset 6 of "ABC אבג XYZ" the suffix is the last Hebrew letter
  // and " XYZ"; the Hebrew run is drawn right to left, so that letter sits
  // left of the two plain ones. Each run is its own region and the plain text
  // between them takes no touches.
  func testMixedDirectionSuffixFollowsTheLaidOutRuns() {
    let label = descriptionLabel("ABC \u{05D0}\u{05D1}\u{05D2} XYZ", start: 6)
    let abc = descriptionWidth("ABC ")
    let last = descriptionWidth("\u{05D2}")
    let plain = descriptionWidth("\u{05D1}\u{05D0}")
    let xyz = descriptionWidth(" XYZ")
    let regions = label.touchableTextRegions.sorted { $0.minX < $1.minX }
    XCTAssertEqual(regions.count, 2)
    guard regions.count == 2 else { return }
    XCTAssertEqual(regions[0].minX, abc, accuracy: 1)
    XCTAssertEqual(regions[0].maxX, abc + last, accuracy: 1)
    XCTAssertEqual(regions[1].minX, abc + last + plain, accuracy: 1)
    XCTAssertFalse(hit(label, x: abc / 2))
    XCTAssertTrue(hit(label, x: abc + last / 2))
    XCTAssertFalse(hit(label, x: abc + last + plain / 2))
    XCTAssertTrue(hit(label, x: abc + last + plain + xyz / 2))
    // Only the outer edge of the last run faces blank space.
    XCTAssertTrue(hit(label, x: abc + last + plain + xyz + 5))
  }

  // A one-line description that does not fit ends in an ellipsis. Action text
  // it only shortens stays touchable up to and including the ellipsis. Once it
  // hides the action text altogether the ellipsis also stands for plain text,
  // and nothing is left to touch.
  func testTruncationCanHideTheActionText() throws {
    let cut = descriptionLabel(
      "08:48 • UniswapUniversalRouterWithAVeryLongName", start: 8, width: 150,
      style: ["lines": 1])
    let region = try XCTUnwrap(cut.touchableTextRegions.first)
    XCTAssertEqual(cut.touchableTextRegions.count, 1)
    XCTAssertEqual(region.minX, descriptionWidth("08:48 • "), accuracy: 1)
    XCTAssertFalse(hit(cut, x: 20))
    XCTAssertTrue(hit(cut, x: 100))
    XCTAssertTrue(hit(cut, x: 145))

    let prefix = "A very long plain prefix that fills the row • "
    let hidden = descriptionLabel(
      prefix + "Router", start: (prefix as NSString).length, width: 150, style: ["lines": 1])
    XCTAssertTrue(hidden.touchableTextRegions.isEmpty)
    XCTAssertFalse(hit(hidden, x: 145))
  }

  // A one-line description folds each line break into one space, so the
  // caller's offset is mapped onto the displayed text and kept inside it.
  func testDescriptionActionStartFollowsTheDisplayedText() {
    let source = "08:48\r\n• Router"
    func text(_ style: [String: Any]?) -> NativeListResolvedText {
      NativeListResolvedText(
        source, style: style, size: 14, color: .label, lineHeight: 20, lines: 2,
        fonts: NativeListFontFamilies())
    }
    let oneLine = text(["lines": 1])
    XCTAssertEqual(oneLine.text, "08:48 • Router")
    XCTAssertEqual(oneLine.displayOffset(9, in: source), 8)
    XCTAssertEqual(oneLine.displayOffset(-3, in: source), 0)
    XCTAssertEqual(oneLine.displayOffset(99, in: source), 14)
    let wrapping = text(nil)
    XCTAssertEqual(wrapping.text, source)
    XCTAssertEqual(wrapping.displayOffset(9, in: source), 9)
    XCTAssertEqual(wrapping.displayOffset(99, in: source), 15)
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
