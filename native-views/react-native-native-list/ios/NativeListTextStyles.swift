import CoreText
import UIKit

// Apply semantic overrides after a renderer has restored its resolved defaults.
enum NativeListTextStyles {
  static func applyStyledText(_ label: UILabel, _ style: [String: Any]) {
    let text = label.attributedText?.string ?? label.text ?? ""
    guard !text.isEmpty, !style.isEmpty else { return }
    let originalText = label.attributedText
    let textLabel = label as? NativeListTextLabel
    let baseFont =
      (originalText?.attribute(.font, at: 0, effectiveRange: nil) as? UIFont) ?? label.font
      ?? nativeListFont(ofSize: 14)
    let size = CGFloat(style.double("fontSize", default: Double(baseFont.pointSize)))
    let font: UIFont
    if let weightName = style["fontWeight"] as? String {
      font = nativeListFont(
        ofSize: size,
        weight: marketFontWeight(weightName, fallback: .regular)
      )
    } else {
      font = baseFont.withSize(size)
    }
    let color =
      (style["color"] as? String)
      .map { UIColor(nativeListHex: $0, fallback: label.textColor ?? .black) }
      ?? label.textColor ?? .black
    if style["lines"] != nil {
      label.numberOfLines = min(3, max(1, style.int("lines", default: 1)))
      label.lineBreakMode = .byTruncatingTail
    }
    if let alignmentName = style["alignment"] as? String {
      label.textAlignment = marketTextAlignment(
        alignmentName, direction: label.effectiveUserInterfaceLayoutDirection)
    }
    if style["truncate"] != nil || style["lines"] != nil {
      label.lineBreakMode = styledLineBreakMode(style, lines: label.numberOfLines)
    }
    if let alignment = style["verticalAlignment"] as? String {
      textLabel?.rowVerticalAlignment = alignment
    }
    if style["offsetY"] != nil { textLabel?.rowOffsetY = CGFloat(style.double("offsetY")) }
    label.font = font
    label.textColor = color
    let attributed =
      originalText.map { NSMutableAttributedString(attributedString: $0) }
      ?? NSMutableAttributedString(string: text)
    let paragraph =
      (attributed.attribute(.paragraphStyle, at: 0, effectiveRange: nil)
      as? NSParagraphStyle)?.mutableCopy() as? NSMutableParagraphStyle ?? NSMutableParagraphStyle()
    if style["alignment"] != nil { paragraph.alignment = label.textAlignment }
    if style["lines"] != nil || style["truncate"] != nil {
      paragraph.lineBreakMode = label.lineBreakMode
    }
    var attributes: [NSAttributedString.Key: Any] = [:]
    applyStyledFont(attributed, style, fallback: baseFont)
    if style["color"] != nil { attributes[.foregroundColor] = color }
    if style["alignment"] != nil || style["lines"] != nil || style["truncate"] != nil
      || style["lineHeight"] != nil
    {
      attributes[.paragraphStyle] = paragraph
    }
    if style["lineHeight"] != nil {
      let box = CGFloat(style.double("lineHeight"))
      paragraph.minimumLineHeight = box
      paragraph.maximumLineHeight = box
      // React Native centers font metrics inside an explicit line height.
      attributes[.baselineOffset] = max(0, (box - font.lineHeight) / 2)
    }
    attributed.addAttributes(attributes, range: NSRange(location: 0, length: attributed.length))
    if label.numberOfLines == 1 && (style["lines"] != nil || style["truncate"] != nil) {
      normalizeSingleLine(attributed)
    }
    label.attributedText = attributed
  }

  static func applyStyledButton(_ button: UIButton, _ style: [String: Any]) {
    let text =
      button.attributedTitle(for: .normal)?.string
      ?? button.title(for: .normal)
      ?? ""
    guard !text.isEmpty, !style.isEmpty else { return }
    let originalText = button.attributedTitle(for: .normal)
    let accessory = button as? NativeListAccessoryButton
    let baseFont =
      (originalText?.attribute(.font, at: 0, effectiveRange: nil) as? UIFont) ?? button.titleLabel?
      .font ?? nativeListFont(ofSize: 14)
    let size = CGFloat(style.double("fontSize", default: Double(baseFont.pointSize)))
    let font: UIFont
    if let weightName = style["fontWeight"] as? String {
      font = nativeListFont(
        ofSize: size,
        weight: marketFontWeight(weightName, fallback: .regular)
      )
    } else {
      font = baseFont.withSize(size)
    }
    let fallbackColor = button.titleColor(for: .normal) ?? .black
    let color =
      (style["color"] as? String)
      .map { UIColor(nativeListHex: $0, fallback: fallbackColor) }
      ?? fallbackColor
    let attributed =
      button.attributedTitle(for: .normal)
      .map { NSMutableAttributedString(attributedString: $0) }
      ?? NSMutableAttributedString(string: text)
    let paragraph =
      (attributed.attribute(.paragraphStyle, at: 0, effectiveRange: nil)
      as? NSParagraphStyle)?.mutableCopy() as? NSMutableParagraphStyle ?? NSMutableParagraphStyle()
    if let alignmentName = style["alignment"] as? String {
      paragraph.alignment = marketTextAlignment(
        alignmentName, direction: button.effectiveUserInterfaceLayoutDirection)
      button.contentHorizontalAlignment =
        alignmentName == "start"
        ? .leading
        : alignmentName == "end" ? .trailing : .center
    }
    var attributes: [NSAttributedString.Key: Any] = [:]
    applyStyledFont(attributed, style, fallback: baseFont)
    if style["color"] != nil { attributes[.foregroundColor] = color }
    if style["alignment"] != nil || style["lineHeight"] != nil || style["lines"] != nil
      || style["truncate"] != nil
    {
      attributes[.paragraphStyle] = paragraph
    }
    if style["lineHeight"] != nil {
      let box = CGFloat(style.double("lineHeight"))
      paragraph.minimumLineHeight = box
      paragraph.maximumLineHeight = box
      attributes[.baselineOffset] = max(0, (box - font.lineHeight) / 2)
      if accessory?.marketLineHeight != nil { accessory?.marketLineHeight = box }
      if accessory?.selectorSummaryLineHeight != nil { accessory?.selectorSummaryLineHeight = box }
    }
    if style["lines"] != nil {
      button.titleLabel?.numberOfLines = min(3, max(1, style.int("lines", default: 1)))
    }
    if style["lines"] != nil || style["truncate"] != nil {
      let mode = styledLineBreakMode(style, lines: button.titleLabel?.numberOfLines ?? 1)
      button.titleLabel?.lineBreakMode = mode
      paragraph.lineBreakMode = mode
      if button.titleLabel?.numberOfLines == 1 { normalizeSingleLine(attributed) }
    }
    if let alignment = style["verticalAlignment"] as? String {
      button.contentVerticalAlignment =
        alignment == "top" ? .top : alignment == "bottom" ? .bottom : .center
    }
    if style["offsetY"] != nil {
      (button as? NativeListAccessoryButton)?.rowTextOffsetY = CGFloat(style.double("offsetY"))
    }
    button.titleLabel?.font = font
    attributed.addAttributes(attributes, range: NSRange(location: 0, length: attributed.length))
    button.setAttributedTitle(attributed, for: .normal)
  }

  static func applyStyledFont(
    _ text: NSMutableAttributedString, _ style: [String: Any], fallback: UIFont
  ) {
    guard style["fontSize"] != nil || style["fontWeight"] != nil else { return }
    text.enumerateAttribute(.font, in: NSRange(location: 0, length: text.length)) {
      value, range, _ in
      let original = (value as? UIFont) ?? fallback
      let size = CGFloat(style.double("fontSize", default: Double(original.pointSize)))
      let font =
        style["fontWeight"] == nil
        ? original.withSize(size)
        : nativeListFont(
          ofSize: size, weight: marketFontWeight(style.string("fontWeight"), fallback: .regular))
      text.addAttribute(.font, value: font, range: range)
    }
  }

  static func normalizeSingleLine(_ text: NSMutableAttributedString) {
    let pattern = try! NSRegularExpression(pattern: "\\r\\n|[\\r\\n]")
    for match in pattern.matches(in: text.string, range: NSRange(location: 0, length: text.length))
      .reversed()
    {
      text.replaceCharacters(in: match.range, with: " ")
    }
  }

  static func styledLineBreakMode(_ style: [String: Any], lines: Int) -> NSLineBreakMode {
    if style.string("truncate", default: "tail") != "clip" { return .byTruncatingTail }
    // Clipping disables wrapping; multi-line clip must wrap without adding an ellipsis.
    return lines == 1 ? .byClipping : .byWordWrapping
  }

  static func marketFontWeight(_ value: String, fallback: NativeListFontWeight)
    -> NativeListFontWeight
  {
    switch value {
    case "regular": return .regular
    case "semibold": return .semibold
    case "bold": return .bold
    case "medium": return .medium
    default: return fallback
    }
  }

  /// Legacy selector/warning typography: monospaced digits and zero kerning on the label.
  static func applyTabularDigits(_ label: UILabel) {
    guard let font = label.font else { return }
    label.font = tabularDigitsFont(font)
    guard let original = label.attributedText else { return }
    let result = NSMutableAttributedString(attributedString: original)
    let range = NSRange(location: 0, length: result.length)
    result.addAttribute(.kern, value: 0, range: range)
    original.enumerateAttribute(.font, in: range) { value, range, _ in
      if let font = value as? UIFont {
        result.addAttribute(.font, value: tabularDigitsFont(font), range: range)
      }
    }
    label.attributedText = result
  }

  static func tabularDigitsFont(_ font: UIFont) -> UIFont {
    var settings =
      font.fontDescriptor.fontAttributes[.featureSettings] as? [[UIFontDescriptor.FeatureKey: Int]]
      ?? []
    settings.removeAll { $0[.type] == kNumberSpacingType }
    settings.append([.type: kNumberSpacingType, .selector: kMonospacedNumbersSelector])
    return UIFont(
      descriptor: font.fontDescriptor.addingAttributes([.featureSettings: settings]),
      size: font.pointSize)
  }

  static func marketTextAlignment(_ value: String, direction: UIUserInterfaceLayoutDirection)
    -> NSTextAlignment
  {
    switch value {
    case "center": return .center
    case "start": return direction == .rightToLeft ? .right : .left
    case "end": return direction == .rightToLeft ? .left : .right
    default: return .natural
    }
  }
}
