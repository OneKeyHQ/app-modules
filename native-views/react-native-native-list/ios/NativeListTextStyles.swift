import CoreText
import UIKit

// Apply semantic overrides after a renderer has restored its resolved defaults.
enum NativeListTextStyles {
  static func applyStyledText(
    _ label: UILabel, _ style: [String: Any], fonts: NativeListFontFamilies
  ) {
    let text = label.attributedText?.string ?? label.text ?? ""
    guard !text.isEmpty, !style.isEmpty else { return }
    let originalText = label.attributedText
    let textLabel = label as? NativeListTextLabel
    let baseFont =
      (originalText?.attribute(.font, at: 0, effectiveRange: nil) as? UIFont) ?? label.font
      ?? nativeListFont(ofSize: 14, fonts: fonts)
    let size = CGFloat(style.double("fontSize", default: Double(baseFont.pointSize)))
    let font: UIFont
    if let weightName = style["fontWeight"] as? String {
      font = nativeListFont(
        ofSize: size,
        weight: marketFontWeight(weightName, fallback: .regular),
        fonts: fonts)
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
    applyStyledFont(attributed, style, fallback: baseFont, fonts: fonts)
    if let tabular = style["tabularNumbers"] as? Bool {
      applyDigitSpacing(attributed, tabular: tabular, fallback: font)
    }
    if style["color"] != nil { attributes[.foregroundColor] = color }
    // An explicit 0 still sets kern, which disables pair kerning like React Native.
    if style["letterSpacing"] != nil { attributes[.kern] = CGFloat(style.double("letterSpacing")) }
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

  static func applyStyledButton(
    _ button: UIButton, _ style: [String: Any], fonts: NativeListFontFamilies
  ) {
    let text =
      button.attributedTitle(for: .normal)?.string
      ?? button.title(for: .normal)
      ?? ""
    guard !text.isEmpty, !style.isEmpty else { return }
    let originalText = button.attributedTitle(for: .normal)
    let accessory = button as? NativeListAccessoryButton
    let baseFont =
      (originalText?.attribute(.font, at: 0, effectiveRange: nil) as? UIFont) ?? button.titleLabel?
      .font ?? nativeListFont(ofSize: 14, fonts: fonts)
    let size = CGFloat(style.double("fontSize", default: Double(baseFont.pointSize)))
    let font: UIFont
    if let weightName = style["fontWeight"] as? String {
      font = nativeListFont(
        ofSize: size,
        weight: marketFontWeight(weightName, fallback: .regular),
        fonts: fonts)
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
    applyStyledFont(attributed, style, fallback: baseFont, fonts: fonts)
    if let tabular = style["tabularNumbers"] as? Bool {
      applyDigitSpacing(attributed, tabular: tabular, fallback: font)
    }
    if style["color"] != nil { attributes[.foregroundColor] = color }
    if style["letterSpacing"] != nil { attributes[.kern] = CGFloat(style.double("letterSpacing")) }
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
    _ text: NSMutableAttributedString, _ style: [String: Any], fallback: UIFont,
    fonts: NativeListFontFamilies
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
          ofSize: size, weight: marketFontWeight(style.string("fontWeight"), fallback: .regular),
          fonts: fonts)
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

  /// Legacy selector/warning defaults: monospaced digits and zero tracking.
  static func applyTabularDigits(_ label: UILabel) {
    guard let font = label.font else { return }
    label.font = digitSpacingFont(font, tabular: true)
    if let original = label.attributedText {
      label.attributedText = selectorText(original, explicit: nil, fallback: font)
    }
  }

  static func selectorText(
    _ original: NSAttributedString, explicit: [String: Any]?, fallback: UIFont
  ) -> NSAttributedString {
    let result = NSMutableAttributedString(attributedString: original)
    if explicit?["letterSpacing"] == nil {
      result.addAttribute(.kern, value: 0, range: NSRange(location: 0, length: result.length))
    }
    if explicit?["tabularNumbers"] == nil {
      applyDigitSpacing(result, tabular: true, fallback: fallback)
    }
    return result
  }

  /// Apply the feature to each run without changing its weight or size.
  static func applyDigitSpacing(
    _ text: NSMutableAttributedString, tabular: Bool, fallback: UIFont
  ) {
    let range = NSRange(location: 0, length: text.length)
    var runs: [(NSRange, UIFont)] = []
    text.enumerateAttribute(.font, in: range) { value, run, _ in
      runs.append((run, (value as? UIFont) ?? fallback))
    }
    for (run, font) in runs {
      text.addAttribute(.font, value: digitSpacingFont(font, tabular: tabular), range: run)
    }
  }

  static func digitSpacingFont(_ font: UIFont, tabular: Bool) -> UIFont {
    var settings =
      font.fontDescriptor.fontAttributes[.featureSettings] as? [[UIFontDescriptor.FeatureKey: Int]]
      ?? []
    settings.removeAll { $0[.type] == kNumberSpacingType }
    settings.append([
      .type: kNumberSpacingType,
      .selector: tabular ? kMonospacedNumbersSelector : kProportionalNumbersSelector,
    ])
    return UIFont(
      descriptor: font.fontDescriptor.addingAttributes([.featureSettings: settings]),
      size: font.pointSize)
  }

  static func applySelectorDefaults(
    in root: UIView, explicitStyle: (UIView) -> [String: Any]?
  ) -> [() -> Void] {
    var restorers: [() -> Void] = []
    func visit(_ view: UIView, inherited: [String: Any]? = nil) {
      let style = explicitStyle(view) ?? inherited
      if let button = view as? UIButton,
         let original = button.attributedTitle(for: .normal),
         let font = button.titleLabel?.font {
        restorers.append { button.setAttributedTitle(original, for: .normal) }
        button.setAttributedTitle(selectorText(original, explicit: style, fallback: font), for: .normal)
      } else if let label = view as? UILabel, let font = label.font {
        let original = label.attributedText
        restorers.append {
          label.font = font
          label.attributedText = original
        }
        if style?["tabularNumbers"] == nil { label.font = digitSpacingFont(font, tabular: true) }
        if let original {
          label.attributedText = selectorText(original, explicit: style, fallback: font)
        }
      }
      for child in view.subviews { visit(child, inherited: style) }
    }
    visit(root)
    return restorers
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
