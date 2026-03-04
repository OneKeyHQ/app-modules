import Foundation
import UIKit

class HybridAutoSizeInput: HybridAutoSizeInputSpec, UITextFieldDelegate, UITextViewDelegate {

  // MARK: - Subviews
  private let prefixLabel = UILabel()
  private let suffixLabel = UILabel()
  private let singleLineInput = UITextField()
  private let multiLineInput = UITextView()

  // MARK: - State
  private var isUpdatingFromJS = false
  private var isRecalculating = false
  private var currentFontSize: CGFloat = 48

  // MARK: - HybridView
  var view: UIView = UIView()

  // MARK: - Props
  var text: String? {
    didSet {
      guard !isUpdatingFromJS else { return }
      isUpdatingFromJS = true
      if multiline == true {
        multiLineInput.text = text ?? ""
      } else {
        singleLineInput.text = text ?? ""
      }
      isUpdatingFromJS = false
      recalculateFontSize()
    }
  }

  var prefix: String? {
    didSet {
      prefixLabel.text = prefix
      prefixLabel.isHidden = (prefix ?? "").isEmpty
      recalculateFontSize()
    }
  }

  var suffix: String? {
    didSet {
      suffixLabel.text = suffix
      suffixLabel.isHidden = (suffix ?? "").isEmpty
      recalculateFontSize()
    }
  }

  var placeholder: String? {
    didSet {
      updatePlaceholder()
    }
  }

  var fontSize: Double? {
    didSet {
      recalculateFontSize()
    }
  }

  var minFontSize: Double? {
    didSet {
      recalculateFontSize()
    }
  }

  var multiline: Bool? {
    didSet {
      updateInputMode()
    }
  }

  var maxNumberOfLines: Double? {
    didSet {
      recalculateFontSize()
    }
  }

  var textColor: String? {
    didSet {
      let color = colorFromHex(textColor) ?? .label
      singleLineInput.textColor = color
      multiLineInput.textColor = color
      // Update prefix/suffix if they don't have custom colors
      if prefixColor == nil { prefixLabel.textColor = color }
      if suffixColor == nil { suffixLabel.textColor = color }
    }
  }

  var prefixColor: String? {
    didSet {
      prefixLabel.textColor = colorFromHex(prefixColor) ?? colorFromHex(textColor) ?? .label
    }
  }

  var suffixColor: String? {
    didSet {
      suffixLabel.textColor = colorFromHex(suffixColor) ?? colorFromHex(textColor) ?? .label
    }
  }

  var placeholderColor: String? {
    didSet {
      updatePlaceholder()
    }
  }

  var textAlign: String? {
    didSet {
      let alignment = textAlignmentFrom(textAlign)
      singleLineInput.textAlignment = alignment
      multiLineInput.textAlignment = alignment
      prefixLabel.textAlignment = .left
      suffixLabel.textAlignment = .right
    }
  }

  var fontFamily: String? {
    didSet {
      recalculateFontSize()
    }
  }

  var fontWeight: String? {
    didSet {
      recalculateFontSize()
    }
  }

  var editable: Bool? {
    didSet {
      let isEditable = editable ?? true
      singleLineInput.isEnabled = isEditable
      multiLineInput.isEditable = isEditable
    }
  }

  var keyboardType: String? {
    didSet {
      let kt = keyboardTypeFrom(keyboardType)
      singleLineInput.keyboardType = kt
      multiLineInput.keyboardType = kt
    }
  }

  var returnKeyType: String? {
    didSet {
      let rkt = returnKeyTypeFrom(returnKeyType)
      singleLineInput.returnKeyType = rkt
      multiLineInput.returnKeyType = rkt
    }
  }

  var autoCorrect: Bool? {
    didSet {
      let type: UITextAutocorrectionType = (autoCorrect == true) ? .yes : .no
      singleLineInput.autocorrectionType = type
      multiLineInput.autocorrectionType = type
    }
  }

  var autoCapitalize: String? {
    didSet {
      let type = autoCapitalizeTypeFrom(autoCapitalize)
      singleLineInput.autocapitalizationType = type
      multiLineInput.autocapitalizationType = type
    }
  }

  var selectionColor: String? {
    didSet {
      let color = colorFromHex(selectionColor) ?? .tintColor
      singleLineInput.tintColor = color
      multiLineInput.tintColor = color
    }
  }

  var prefixMarginRight: Double? {
    didSet {
      view.setNeedsLayout()
    }
  }

  var suffixMarginLeft: Double? {
    didSet {
      view.setNeedsLayout()
    }
  }

  var onChangeText: ((String) -> Void)?
  var onFocus: (() -> Void)?
  var onBlur: (() -> Void)?

  // MARK: - Init

  override init() {
    super.init()
    setupView()
  }

  private func setupView() {
    view.clipsToBounds = true

    // Configure prefix label
    prefixLabel.isHidden = true
    prefixLabel.setContentHuggingPriority(.required, for: .horizontal)
    prefixLabel.setContentCompressionResistancePriority(.required, for: .horizontal)

    // Configure suffix label
    suffixLabel.isHidden = true
    suffixLabel.setContentHuggingPriority(.required, for: .horizontal)
    suffixLabel.setContentCompressionResistancePriority(.required, for: .horizontal)

    // Configure single-line input
    singleLineInput.borderStyle = .none
    singleLineInput.delegate = self
    singleLineInput.addTarget(self, action: #selector(textFieldDidChange(_:)), for: .editingChanged)

    // Configure multi-line input
    multiLineInput.delegate = self
    multiLineInput.textContainerInset = .zero
    multiLineInput.textContainer.lineFragmentPadding = 0
    multiLineInput.backgroundColor = .clear
    multiLineInput.isScrollEnabled = false
    multiLineInput.isHidden = true

    view.addSubview(prefixLabel)
    view.addSubview(singleLineInput)
    view.addSubview(multiLineInput)
    view.addSubview(suffixLabel)

    // Override layoutSubviews
    let layoutView = LayoutView()
    layoutView.layoutCallback = { [weak self] in
      self?.performLayout()
    }
    // Replace view with our layout-aware view
    let oldView = view
    view = layoutView
    view.clipsToBounds = true
    for subview in oldView.subviews {
      subview.removeFromSuperview()
      view.addSubview(subview)
    }
  }

  private func performLayout() {
    let bounds = view.bounds
    guard bounds.width > 0 && bounds.height > 0 else { return }

    let prefixW: CGFloat
    if prefixLabel.isHidden {
      prefixW = 0
    } else {
      prefixW = prefixLabel.sizeThatFits(bounds.size).width
    }

    let suffixW: CGFloat
    if suffixLabel.isHidden {
      suffixW = 0
    } else {
      suffixW = suffixLabel.sizeThatFits(bounds.size).width
    }

    let prefixGap = prefixLabel.isHidden ? 0 : CGFloat(prefixMarginRight ?? 0)
    let suffixGap = suffixLabel.isHidden ? 0 : CGFloat(suffixMarginLeft ?? 0)

    let inputX = prefixW + prefixGap
    let inputW = bounds.width - inputX - suffixW - suffixGap

    prefixLabel.frame = CGRect(x: 0, y: 0, width: prefixW, height: bounds.height)
    suffixLabel.frame = CGRect(x: bounds.width - suffixW, y: 0, width: suffixW, height: bounds.height)

    let activeInput: UIView = (multiline == true) ? multiLineInput : singleLineInput
    activeInput.frame = CGRect(x: inputX, y: 0, width: max(inputW, 0), height: bounds.height)

    recalculateFontSize()
  }

  private func updateInputMode() {
    let isMulti = multiline == true
    singleLineInput.isHidden = isMulti
    multiLineInput.isHidden = !isMulti

    // Transfer text between inputs
    if isMulti {
      multiLineInput.text = singleLineInput.text
    } else {
      singleLineInput.text = multiLineInput.text
    }

    view.setNeedsLayout()
  }

  // MARK: - Font Size Calculation

  private func recalculateFontSize() {
    guard !isRecalculating else { return }
    let bounds = view.bounds
    guard bounds.width > 0 && bounds.height > 0 else { return }
    isRecalculating = true
    defer { isRecalculating = false }

    let maxSize = CGFloat(fontSize ?? 48)
    let minSize = CGFloat(minFontSize ?? 16)

    let prefixText = prefix ?? ""
    let suffixText = suffix ?? ""
    let inputText: String
    if multiline == true {
      inputText = multiLineInput.text ?? ""
    } else {
      inputText = singleLineInput.text ?? ""
    }

    // If no text, use max font size
    let displayText = inputText.isEmpty ? (placeholder ?? "") : inputText
    let fullText = prefixText + displayText + suffixText

    guard !fullText.isEmpty else {
      applyFontSize(maxSize)
      return
    }

    let prefixGap = prefixLabel.isHidden ? 0 : CGFloat(prefixMarginRight ?? 0)
    let suffixGap = suffixLabel.isHidden ? 0 : CGFloat(suffixMarginLeft ?? 0)
    let totalGap = prefixGap + suffixGap
    let availableWidth = bounds.width - totalGap

    let optimalSize: CGFloat
    if multiline == true {
      let maxLines = Int(maxNumberOfLines ?? 1)
      optimalSize = findOptimalFontSizeMultiline(
        fullText: fullText,
        availableWidth: availableWidth,
        availableHeight: bounds.height,
        maxLines: maxLines,
        minSize: minSize,
        maxSize: maxSize
      )
    } else {
      optimalSize = findOptimalFontSizeSingleLine(
        fullText: fullText,
        availableWidth: availableWidth,
        minSize: minSize,
        maxSize: maxSize
      )
    }

    applyFontSize(optimalSize)
  }

  private func findOptimalFontSizeSingleLine(
    fullText: String,
    availableWidth: CGFloat,
    minSize: CGFloat,
    maxSize: CGFloat
  ) -> CGFloat {
    var low = minSize
    var high = maxSize

    while high - low > 0.5 {
      let mid = (low + high) / 2
      let font = makeFont(size: mid)
      let textWidth = (fullText as NSString).size(withAttributes: [.font: font]).width
      if textWidth <= availableWidth {
        low = mid
      } else {
        high = mid
      }
    }

    return low
  }

  private func findOptimalFontSizeMultiline(
    fullText: String,
    availableWidth: CGFloat,
    availableHeight: CGFloat,
    maxLines: Int,
    minSize: CGFloat,
    maxSize: CGFloat
  ) -> CGFloat {
    var low = minSize
    var high = maxSize

    while high - low > 0.5 {
      let mid = (low + high) / 2
      let font = makeFont(size: mid)
      let constraintSize = CGSize(width: availableWidth, height: .greatestFiniteMagnitude)
      let boundingRect = (fullText as NSString).boundingRect(
        with: constraintSize,
        options: [.usesLineFragmentOrigin, .usesFontLeading],
        attributes: [.font: font],
        context: nil
      )
      let lineHeight = font.lineHeight
      let lineCount = max(1, Int(ceil(boundingRect.height / lineHeight)))

      if lineCount <= maxLines && boundingRect.height <= availableHeight {
        low = mid
      } else {
        high = mid
      }
    }

    return low
  }

  private func applyFontSize(_ size: CGFloat) {
    currentFontSize = size
    let font = makeFont(size: size)
    singleLineInput.font = font
    multiLineInput.font = font
    prefixLabel.font = font
    suffixLabel.font = font
    updatePlaceholder()

    // Re-layout prefix/suffix since their size depends on font
    view.setNeedsLayout()
  }

  private func makeFont(size: CGFloat) -> UIFont {
    let weight = fontWeightFrom(fontWeight)

    if let family = fontFamily, !family.isEmpty {
      if let font = UIFont(name: family, size: size) {
        return font
      }
    }

    return UIFont.systemFont(ofSize: size, weight: weight)
  }

  private func updatePlaceholder() {
    let phText = placeholder ?? ""
    let phColor = colorFromHex(placeholderColor) ?? .placeholderText
    let font = makeFont(size: currentFontSize)
    singleLineInput.attributedPlaceholder = NSAttributedString(
      string: phText,
      attributes: [.foregroundColor: phColor, .font: font]
    )
    // For multiline, placeholder would need custom handling
  }

  // MARK: - Methods

  func focus() throws {
    if multiline == true {
      multiLineInput.becomeFirstResponder()
    } else {
      singleLineInput.becomeFirstResponder()
    }
  }

  func blur() throws {
    if multiline == true {
      multiLineInput.resignFirstResponder()
    } else {
      singleLineInput.resignFirstResponder()
    }
  }

  // MARK: - UITextField Delegate & Events

  @objc private func textFieldDidChange(_ textField: UITextField) {
    guard !isUpdatingFromJS else { return }
    recalculateFontSize()
    onChangeText?(textField.text ?? "")
  }

  func textFieldDidBeginEditing(_ textField: UITextField) {
    onFocus?()
  }

  func textFieldDidEndEditing(_ textField: UITextField) {
    onBlur?()
  }

  // MARK: - UITextView Delegate

  func textViewDidChange(_ textView: UITextView) {
    guard !isUpdatingFromJS else { return }
    recalculateFontSize()
    onChangeText?(textView.text ?? "")
  }

  func textViewDidBeginEditing(_ textView: UITextView) {
    onFocus?()
  }

  func textViewDidEndEditing(_ textView: UITextView) {
    onBlur?()
  }

  // MARK: - Helpers

  private func colorFromHex(_ hex: String?) -> UIColor? {
    guard let hex = hex, !hex.isEmpty else { return nil }
    var sanitized = hex.trimmingCharacters(in: .whitespacesAndNewlines)
    if sanitized.hasPrefix("#") {
      sanitized.remove(at: sanitized.startIndex)
    }

    var color: UInt64 = 0
    Scanner(string: sanitized).scanHexInt64(&color)

    if sanitized.count == 8 {
      // RRGGBBAA
      let r = CGFloat((color >> 24) & 0xFF) / 255.0
      let g = CGFloat((color >> 16) & 0xFF) / 255.0
      let b = CGFloat((color >> 8) & 0xFF) / 255.0
      let a = CGFloat(color & 0xFF) / 255.0
      return UIColor(red: r, green: g, blue: b, alpha: a)
    } else {
      // RRGGBB
      let r = CGFloat((color >> 16) & 0xFF) / 255.0
      let g = CGFloat((color >> 8) & 0xFF) / 255.0
      let b = CGFloat(color & 0xFF) / 255.0
      return UIColor(red: r, green: g, blue: b, alpha: 1.0)
    }
  }

  private func textAlignmentFrom(_ align: String?) -> NSTextAlignment {
    switch align {
    case "center": return .center
    case "right": return .right
    default: return .left
    }
  }

  private func keyboardTypeFrom(_ type: String?) -> UIKeyboardType {
    switch type {
    case "numberPad": return .numberPad
    case "decimalPad": return .decimalPad
    case "emailAddress": return .emailAddress
    case "phonePad": return .phonePad
    case "url": return .URL
    default: return .default
    }
  }

  private func autoCapitalizeTypeFrom(_ type: String?) -> UITextAutocapitalizationType {
    switch type {
    case "characters": return .allCharacters
    case "words": return .words
    case "sentences": return .sentences
    case "none": return .none
    default: return .none
    }
  }

  private func returnKeyTypeFrom(_ type: String?) -> UIReturnKeyType {
    switch type {
    case "done": return .done
    case "go": return .go
    case "next": return .next
    case "search": return .search
    case "send": return .send
    default: return .default
    }
  }

  private func fontWeightFrom(_ weight: String?) -> UIFont.Weight {
    switch weight {
    case "bold": return .bold
    case "semibold": return .semibold
    case "medium": return .medium
    case "light": return .light
    case "thin": return .thin
    case "ultraLight": return .ultraLight
    case "heavy": return .heavy
    case "black": return .black
    default: return .regular
    }
  }
}

// MARK: - Layout-aware UIView subclass

private class LayoutView: UIView {
  var layoutCallback: (() -> Void)?

  override func layoutSubviews() {
    super.layoutSubviews()
    layoutCallback?()
  }
}
