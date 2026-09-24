import UIKit

struct NativeListResolvedText {
  let text: String
  let font: UIFont
  let color: UIColor
  let lineHeight: CGFloat
  let lines: Int
  /// Semantic `start | center | end`; nil keeps the legacy `.natural` default.
  let alignmentName: String?
  let breakMode: NSLineBreakMode
  let verticalAlignment: String?
  let offsetY: CGFloat
  let explicitLineHeight: Bool
  let explicitTruncation: Bool
  let tabular: Bool
  /// Keep the label's break mode in the attributed paragraph (legacy plain-text labels kept
  /// their tail ellipsis; attributed paragraphs otherwise default to word wrapping).
  let preservesBreakMode: Bool

  init(
    _ value: String, style: [String: Any]?, size: CGFloat, weight: NativeListFontWeight = .regular,
    color: UIColor, lineHeight: CGFloat, lines: Int, breakMode: NSLineBreakMode = .byTruncatingTail,
    tabular: Bool = false, preservesBreakMode: Bool = false
  ) {
    let style = style ?? [:]
    self.tabular = tabular
    self.preservesBreakMode = preservesBreakMode
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
    let resolvedSize = CGFloat(style.double("fontSize", default: Double(size)))
    font =
      tabular
      ? nativeListTabularFont(ofSize: resolvedSize, weight: resolvedWeight)
      : nativeListFont(ofSize: resolvedSize, weight: resolvedWeight)
    self.color =
      (style["color"] as? String).map { UIColor(nativeListHex: $0, fallback: color) } ?? color
    self.lineHeight = CGFloat(style.double("lineHeight", default: Double(lineHeight)))
    explicitLineHeight = style["lineHeight"] != nil
    alignmentName = style["alignment"] as? String
    self.breakMode =
      explicitTruncation
      ? (style.string("truncate", default: "tail") == "clip"
        ? (self.lines == 1 ? .byClipping : .byWordWrapping) : .byTruncatingTail) : breakMode
    verticalAlignment = style["verticalAlignment"] as? String
    offsetY = CGFloat(style.double("offsetY"))
  }

  /// Start/end follow the host view's layout direction, as legacy `marketTextAlignment` did.
  func alignment(for direction: UIUserInterfaceLayoutDirection) -> NSTextAlignment {
    guard let alignmentName else { return .natural }
    return NativeListTextStyles.marketTextAlignment(alignmentName, direction: direction)
  }

  var attributed: NSAttributedString {
    attributed(direction: UIView.userInterfaceLayoutDirection(for: .unspecified))
  }

  func attributed(direction: UIUserInterfaceLayoutDirection) -> NSAttributedString {
    let paragraph = NSMutableParagraphStyle()
    paragraph.minimumLineHeight = lineHeight
    paragraph.maximumLineHeight = lineHeight
    paragraph.alignment = alignment(for: direction)
    if explicitTruncation || preservesBreakMode { paragraph.lineBreakMode = breakMode }
    var attributes: [NSAttributedString.Key: Any] = [
      .font: font, .foregroundColor: color, .paragraphStyle: paragraph,
    ]
    if explicitLineHeight {
      attributes[.baselineOffset] = max(0, (lineHeight - font.lineHeight) / 2)
    }
    return NSAttributedString(string: text, attributes: attributes)
  }

  func bind(_ label: NativeListTextLabel) {
    let direction = label.effectiveUserInterfaceLayoutDirection
    label.font = font
    label.textColor = color
    label.numberOfLines = lines
    label.textAlignment = alignment(for: direction)
    label.lineBreakMode = breakMode
    label.rowVerticalAlignment = verticalAlignment
    label.rowOffsetY = offsetY
    label.attributedText = attributed(direction: direction)
    label.isHidden = text.isEmpty
  }

  // Measurement runs for every row on every snapshot. It reuses one sizing label (the same
  // UILabel metrics, line cap and truncation as the bound view) and memoizes by the inputs
  // that affect height; alignment and color do not.
  private static let sizingLabel = NativeListTextLabel()
  private static let heightCache: NSCache<NSString, NSNumber> = {
    let cache = NSCache<NSString, NSNumber>()
    cache.countLimit = 4096
    return cache
  }()

  func measure(width: CGFloat) -> CGFloat {
    guard !text.isEmpty else { return 0 }
    let width = max(1, width)
    let key =
      "\(width)|\(lines)|\(lineHeight)|\(explicitLineHeight)|\(explicitTruncation)|\(breakMode.rawValue)|\(preservesBreakMode)|\(font.fontName)|\(font.pointSize)|\(tabular)|\(text)"
      as NSString
    if let cached = Self.heightCache.object(forKey: key) { return CGFloat(cached.doubleValue) }
    let label = Thread.isMainThread ? Self.sizingLabel : NativeListTextLabel()
    bind(label)
    let height = ceil(
      label.sizeThatFits(CGSize(width: width, height: .greatestFiniteMagnitude)).height)
    if label === Self.sizingLabel { label.attributedText = nil }
    Self.heightCache.setObject(NSNumber(value: Double(height)), forKey: key)
    return height
  }
}
