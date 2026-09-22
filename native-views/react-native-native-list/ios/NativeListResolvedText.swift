import UIKit

struct NativeListResolvedText {
  let text: String
  let font: UIFont
  let color: UIColor
  let lineHeight: CGFloat
  let lines: Int
  let alignment: NSTextAlignment
  let breakMode: NSLineBreakMode
  let verticalAlignment: String?
  let offsetY: CGFloat
  let explicitLineHeight: Bool
  let explicitTruncation: Bool

  init(
    _ value: String, style: [String: Any]?, size: CGFloat, weight: NativeListFontWeight = .regular,
    color: UIColor, lineHeight: CGFloat, lines: Int, breakMode: NSLineBreakMode = .byTruncatingTail
  ) {
    let style = style ?? [:]
    self.lines = min(3, max(1, style.int("lines", default: lines)))
    explicitTruncation = style["lines"] != nil || style["truncate"] != nil
    text =
      self.lines == 1 && explicitTruncation
      ? value.replacingOccurrences(of: "\r\n|[\r\n]", with: " ", options: .regularExpression)
      : value
    let resolvedWeight: NativeListFontWeight
    switch style.string("fontWeight") {
    case "regular": resolvedWeight = .regular
    case "medium": resolvedWeight = .medium
    case "semibold": resolvedWeight = .semibold
    case "bold": resolvedWeight = .bold
    default: resolvedWeight = weight
    }
    font = nativeListFont(
      ofSize: CGFloat(style.double("fontSize", default: Double(size))), weight: resolvedWeight)
    self.color =
      (style["color"] as? String).map { UIColor(nativeListHex: $0, fallback: color) } ?? color
    self.lineHeight = CGFloat(style.double("lineHeight", default: Double(lineHeight)))
    explicitLineHeight = style["lineHeight"] != nil
    alignment =
      style.string("alignment") == "center"
      ? .center : style.string("alignment") == "end" ? .right : .natural
    self.breakMode =
      explicitTruncation
      ? (style.string("truncate", default: "tail") == "clip"
        ? (self.lines == 1 ? .byClipping : .byWordWrapping) : .byTruncatingTail) : breakMode
    verticalAlignment = style["verticalAlignment"] as? String
    offsetY = CGFloat(style.double("offsetY"))
  }

  var attributed: NSAttributedString {
    let paragraph = NSMutableParagraphStyle()
    paragraph.minimumLineHeight = lineHeight
    paragraph.maximumLineHeight = lineHeight
    paragraph.alignment = alignment
    if explicitTruncation { paragraph.lineBreakMode = breakMode }
    var attributes: [NSAttributedString.Key: Any] = [
      .font: font, .foregroundColor: color, .paragraphStyle: paragraph,
    ]
    if explicitLineHeight {
      attributes[.baselineOffset] = max(0, (lineHeight - font.lineHeight) / 2)
    }
    return NSAttributedString(string: text, attributes: attributes)
  }

  func bind(_ label: NativeListTextLabel) {
    label.font = font
    label.textColor = color
    label.numberOfLines = lines
    label.textAlignment = alignment
    label.lineBreakMode = breakMode
    label.rowVerticalAlignment = verticalAlignment
    label.rowOffsetY = offsetY
    label.attributedText = attributed
    label.isHidden = text.isEmpty
  }

  func measure(width: CGFloat) -> CGFloat {
    guard !text.isEmpty else { return 0 }
    // UILabel uses the same attributed metrics and line cap as the bound view.
    let label = NativeListTextLabel()
    bind(label)
    return ceil(
      label.sizeThatFits(CGSize(width: max(1, width), height: .greatestFiniteMagnitude)).height)
  }
}
