import UIKit

// Bounded trailing controls shared by migrated templates. The owning host supplies
// action epochs; selection-only updates touch only the existing checkbox.
final class NativeListAccessoryStack: UIStackView {
  private let accessoryButtons = (0..<2).map { _ in NativeListAccessoryButton(type: .system) }
  private let checkboxButton = UIButton(type: .system)
  private let spinner = UIActivityIndicatorView(style: .medium)
  private var accessoryActions: [(String, NativeSelectionTarget?)] = []
  private var checkboxAction: (String, NativeSelectionTarget?)?
  private var boundCheckboxData: [String: Any]?
  private var boundCheckboxTarget: NativeSelectionTarget?
  private var semanticValueButtons: [UIButton] = []
  private var accessorySizeConstraints: [NSLayoutConstraint] = []
  private var currentItem: NativeListItem?
  private var currentTheme: [String: Any]?
  private var checkboxCheckedColor = UIColor.black
  private var checkboxUncheckedColor = UIColor.white
  private var checkboxIconColor = UIColor.white
  private var checkboxBorderColor = UIColor.lightGray
  var endInset: CGFloat?
  var contentGap: CGFloat?
  var onAction: ((String, UIView, Int?, NativeSelectionTarget?) -> Void)?

  override init(frame: CGRect) {
    super.init(frame: frame)
    axis = .vertical
    alignment = .trailing
    setContentHuggingPriority(.required, for: .horizontal)
    setContentCompressionResistancePriority(.required, for: .horizontal)
    for (index, button) in accessoryButtons.enumerated() {
      button.tag = index
      button.adjustsImageWhenDisabled = false
      button.tintAdjustmentMode = .normal
      button.addTarget(self, action: #selector(pressed(_:)), for: .touchUpInside)
      addArrangedSubview(button)
    }
    checkboxButton.layer.borderWidth = 2
    checkboxButton.layer.cornerRadius = 4
    checkboxButton.imageView?.contentMode = .center
    checkboxButton.addTarget(self, action: #selector(checkboxPressed), for: .touchUpInside)
    NSLayoutConstraint.activate([
      checkboxButton.widthAnchor.constraint(equalToConstant: 20),
      checkboxButton.heightAnchor.constraint(equalToConstant: 20),
    ])
    addArrangedSubview(checkboxButton)
    addArrangedSubview(spinner)
  }
  required init(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  func bind(
    _ item: NativeListItem, descriptors: [[String: Any]], theme: [String: Any]?,
    style: [String: Any], checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    reset()
    currentItem = item
    currentTheme = theme
    spacing = CGFloat(style.double("trailingGap", default: 2))
    checkboxCheckedColor = nativeListColor(theme, "primaryText", "#202020")
    checkboxUncheckedColor = nativeListColor(theme, "inverseText", "#FCFCFC")
    checkboxIconColor = checkboxUncheckedColor
    checkboxBorderColor = UIColor(nativeListHex: "#00000031", fallback: .lightGray)
    accessoryButtons.forEach {
      $0.setTitleColor(nativeListColor(theme, "primaryText", "#202020"), for: .normal)
    }
    bindAccessories(item, descriptors, theme, checkboxState)
    if let value = style.dictionary("value"), let button = semanticValueButtons.first {
      applyStyledButton(button, value)
    }
    isHidden = arrangedSubviews.allSatisfy { $0.isHidden }
  }
  func updateSelection(
    _ item: NativeListItem,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    currentItem = item
    if let data = boundCheckboxData { bindCheckbox(item, data, checkboxState) }
  }
  func anchorInset(for view: UIView) -> CGFloat {
    currentItem?.data.string("presentation") == "accountSelector" && accessoryButtons.contains { $0 === view } && view.bounds.width == 38 ? 7 : 0
  }
  func reset() {
    NSLayoutConstraint.deactivate(accessorySizeConstraints)
    accessorySizeConstraints.removeAll()
    accessoryActions.removeAll()
    semanticValueButtons.removeAll()
    checkboxAction = nil
    boundCheckboxData = nil
    boundCheckboxTarget = nil
    endInset = nil
    contentGap = nil
    currentItem = nil
    for (index, button) in accessoryButtons.enumerated() {
      button.setAttributedTitle(nil, for: .normal)
      button.setTitle(nil, for: .normal)
      button.setImage(nil, for: .normal)
      button.setImage(nil, for: .disabled)
      button.titleLabel?.font = nativeListFont(
        ofSize: index == 0 ? 16 : 14, weight: index == 0 ? .medium : .regular)
      button.titleLabel?.numberOfLines = 1
      button.titleLabel?.lineBreakMode = .byTruncatingTail
      button.contentHorizontalAlignment = .center
      button.contentVerticalAlignment = .center
      button.rowTextOffsetY = 0
      button.marketLineHeight = nil
      button.selectorSummaryLineHeight = nil
      button.backgroundColor = .clear
      button.pressedBackgroundColor = nil
      button.layer.cornerRadius = 0
      button.isHidden = true
      button.isEnabled = true
      button.alpha = 1
      button.accessibilityIdentifier = nil
      button.accessibilityLabel = nil
    }
    checkboxButton.isHidden = true
    spinner.stopAnimating()
    spinner.alpha = 1
  }
  @objc private func pressed(_ button: UIButton) {
    guard accessoryActions.indices.contains(button.tag) else { return }
    let action = accessoryActions[button.tag]
    if !action.0.isEmpty { onAction?(action.0, button, button.tag, action.1) }
  }
  @objc private func checkboxPressed() {
    if let action = checkboxAction { onAction?(action.0, checkboxButton, nil, action.1) }
  }
  private func bindAccessories(
    _ item: NativeListItem,
    _ accessories: [[String: Any]],
    _ theme: [String: Any]?,
    _ checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    var textIndex = 0
    for accessory in accessories.prefix(2) {
      switch accessory.string("kind") {
      case "value":
        showAccessory(textIndex, accessory.string("text"))
        semanticValueButtons.append(accessoryButtons[textIndex])
        if item.data.string("presentation") == "networkSelector" && item.data["height"] != nil {
          accessoryButtons[textIndex].contentHorizontalAlignment = .trailing
          accessoryButtons[textIndex].titleLabel?.textAlignment = .right
        }
        applyValueSegments(
          accessory.dictionaries("textSegments"), to: accessoryButtons[textIndex], theme: theme)
        textIndex += 1
      case "valuePair":
        showValuePairAccessory(textIndex, accessory, theme: theme)
        semanticValueButtons.append(accessoryButtons[textIndex])
        textIndex += 1
      case "checkbox": bindCheckbox(item, accessory, checkboxState)
      case "radio":
        showAccessory(
          textIndex, accessory.bool("checked") ? "●" : "○", action: accessoryAction(accessory))
        textIndex += 1
      case "switch":
        showAccessory(
          textIndex, accessory.bool("value") ? "ON" : "OFF", action: accessoryAction(accessory))
        textIndex += 1
      case "chevron":
        var icon = accessory
        icon["name"] = "ChevronRightSmallOutline"
        if icon["tintColor"] == nil {
          icon["tintColor"] = theme?["iconSubdued"] as? String ?? "#8D8D8D"
        }
        if icon.string("actionKey").isEmpty { icon["actionKey"] = "press" }
        showAccessoryIcon(textIndex, icon)
        textIndex += 1
      case "menu":
        showMenuAccessory(textIndex, action: accessoryAction(accessory))
        textIndex += 1
      case "drag":
        var icon = accessory
        icon["name"] = "DragOutline"
        if icon["tintColor"] == nil {
          icon["tintColor"] = theme?["iconSubdued"] as? String ?? "#8D8D8D"
        }
        showAccessoryIcon(textIndex, icon)
        textIndex += 1
      case "icon":
        var icon = accessory
        if icon["tintColor"] == nil {
          icon["tintColor"] = theme?["iconSubdued"] as? String ?? "#8D8D8D"
        }
        showAccessoryIcon(textIndex, icon)
        textIndex += 1
      case "spinner": spinner.startAnimating()
      case "progress":
        showAccessory(textIndex, "\(Int(accessory.double("value") * 100))%")
        textIndex += 1
      default: break
      }
    }
  }

  private func bindCheckbox(
    _ item: NativeListItem,
    _ data: [String: Any],
    _ checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    let targetData = data.dictionary("target")
    let scope = targetData?.string("scope", default: "row") ?? "row"
    let key =
      scope == "section" ? targetData?.string("sectionKey") : scope == "row" ? item.key : nil
    let target = NativeSelectionTarget(scope: scope, key: key)
    let state = checkboxState(item, target, data.string("state", default: "unchecked"))
    boundCheckboxData = data
    boundCheckboxTarget = target
    updateCheckboxPresentation(item, data, target: target, state: state)
  }

  private func updateCheckboxPresentation(
    _ item: NativeListItem,
    _ data: [String: Any],
    target: NativeSelectionTarget,
    state: String
  ) {
    let accessoryDisabled = data.bool("disabled")
    if data.bool("loading") {
      checkboxButton.isHidden = true
      spinner.startAnimating()
      spinner.alpha = item.data.bool("disabled") ? 1 : accessoryDisabled ? 0.5 : 1
      checkboxAction = nil
      return
    }
    checkboxButton.isHidden = false
    checkboxButton.backgroundColor =
      state == "unchecked" ? checkboxUncheckedColor : checkboxCheckedColor
    checkboxButton.layer.borderColor =
      state == "unchecked"
      ? checkboxBorderColor.cgColor
      : UIColor.clear.cgColor
    let glyphName =
      state == "indeterminate"
      ? "CheckboxIndeterminateCustom"
      : "CheckboxCheckedCustom"
    checkboxButton.setImage(
      state == "unchecked" ? nil : nativeListIcon(named: glyphName),
      for: .normal
    )
    checkboxButton.tintColor = checkboxIconColor
    // A disabled ListItem already applies 0.5 to its complete content. Avoid
    // multiplying that opacity on the nested control a second time.
    checkboxButton.alpha = item.data.bool("disabled") ? 1 : accessoryDisabled ? 0.5 : 1
    checkboxButton.isEnabled =
      !item.data.bool("disabled") && !accessoryDisabled && !data.bool("loading")
    checkboxAction = (data.string("actionKey", default: "selection"), target)
  }

  private func showAccessory(
    _ index: Int,
    _ value: String,
    action: (String, NativeSelectionTarget?)? = nil,
    color: UIColor? = nil
  ) {
    guard accessoryButtons.indices.contains(index), !value.isEmpty else { return }
    let button = accessoryButtons[index]
    button.isHidden = false
    button.setTitle(value, for: .normal)
    button.setContentHuggingPriority(.required, for: .horizontal)
    button.setContentCompressionResistancePriority(.required, for: .horizontal)
    if let color { button.setTitleColor(color, for: .normal) }
    while accessoryActions.count <= index { accessoryActions.append(("", nil)) }
    accessoryActions[index] = action ?? ("", nil)
  }

  private func showValuePairAccessory(
    _ index: Int,
    _ accessory: [String: Any],
    theme: [String: Any]?
  ) {
    guard accessoryButtons.indices.contains(index) else { return }
    let primary = accessory.string("primary")
    let secondary = accessory.string("secondary")
    guard !primary.isEmpty || !secondary.isEmpty else { return }
    let button = accessoryButtons[index]
    let text = NSMutableAttributedString()
    let primaryParagraph = NSMutableParagraphStyle()
    primaryParagraph.alignment = .right
    primaryParagraph.minimumLineHeight = 20
    primaryParagraph.maximumLineHeight = 20
    text.append(
      NSAttributedString(
        string: primary,
        attributes: [
          .font: nativeListFont(ofSize: 16, weight: .medium),
          .foregroundColor: accessoryTextColor(
            accessory.string("primaryTone"),
            defaultTone: "primary",
            theme: theme
          ),
          .paragraphStyle: primaryParagraph,
        ]
      ))
    if !primary.isEmpty, !secondary.isEmpty { text.append(NSAttributedString(string: "\n")) }
    let secondaryParagraph = NSMutableParagraphStyle()
    secondaryParagraph.alignment = .right
    secondaryParagraph.minimumLineHeight = 20
    secondaryParagraph.maximumLineHeight = 20
    text.append(
      NSAttributedString(
        string: secondary,
        attributes: [
          .font: nativeListFont(ofSize: 14),
          .foregroundColor: accessoryTextColor(
            accessory.string("secondaryTone"),
            defaultTone: "secondary",
            theme: theme
          ),
          .paragraphStyle: secondaryParagraph,
        ]
      ))
    button.isHidden = false
    button.titleLabel?.numberOfLines = 2
    button.contentHorizontalAlignment = .trailing
    button.setAttributedTitle(text, for: .normal)
    button.setContentHuggingPriority(.required, for: .horizontal)
    button.setContentCompressionResistancePriority(.required, for: .horizontal)
    while accessoryActions.count <= index { accessoryActions.append(("", nil)) }
    accessoryActions[index] = ("", nil)
  }

  private func showMenuAccessory(
    _ index: Int,
    action: (String, NativeSelectionTarget?)?
  ) {
    showAccessory(index, "⋮", action: action)
    guard accessoryButtons.indices.contains(index) else { return }
    let button = accessoryButtons[index]
    button.titleLabel?.font = nativeListFont(ofSize: 20, weight: .medium)
    accessorySizeConstraints.append(contentsOf: [
      button.widthAnchor.constraint(equalToConstant: 24),
      button.heightAnchor.constraint(equalToConstant: 24),
    ])
    NSLayoutConstraint.activate(Array(accessorySizeConstraints.suffix(2)))
  }

  private func accessoryTextColor(
    _ tone: String,
    defaultTone: String,
    theme: [String: Any]?
  ) -> UIColor {
    switch tone.isEmpty ? defaultTone : tone {
    case "positive": return nativeListColor(theme, "positive", "#218358")
    case "negative": return nativeListColor(theme, "negative", "#CE2C31")
    // OneKey patch: account warnings and hidden balances use existing theme tokens.
    case "disabled": return nativeListColor(theme, "disabledText", "#8D8D8D")
    case "caution": return nativeListColor(theme, "caution", "#AB6400")
    case "secondary": return nativeListColor(theme, "secondaryText", "#646464")
    default: return nativeListColor(theme, "primaryText", "#202020")
    }
  }

  private func showAccessoryIcon(_ index: Int, _ data: [String: Any]) {
    guard accessoryButtons.indices.contains(index) else { return }
    let button = accessoryButtons[index]
    button.isHidden = false
    button.isEnabled = !data.bool("disabled")
    button.alpha = button.isEnabled ? 1 : 0.4
    let isAccountCreate =
      currentItem?.data.string("presentation") == "accountSelector"
      && currentItem?.data["height"] != nil && data.string("name") == "PlusSmallOutline"
    let tintColor =
      data["tintColor"] == nil && isAccountCreate
      ? nativeListColor(currentTheme, "iconSubdued", "#8D8D8D")
      : UIColor(nativeListHex: data.string("tintColor", default: "#646464"), fallback: .darkGray)
    button.tintColor = tintColor
    if let image = nativeListIcon(named: data.string("name")) {
      button.setImage(image, for: .normal)
      button.setImage(
        image.withTintColor(tintColor, renderingMode: .alwaysOriginal),
        for: .disabled
      )
    }
    let isDrillIn = data.string("kind") == "chevron"
    let isAccountIcon = currentItem?.data.string("presentation") == "accountSelector" && !isDrillIn
    let size: CGFloat = isDrillIn ? 24 : isAccountIcon && !isAccountCreate ? 38 : 36
    if isAccountCreate { button.layer.cornerRadius = 8 }
    if isAccountIcon && !isAccountCreate && !data.string("actionKey").isEmpty {
      button.pressedBackgroundColor = nativeListColor(
        currentTheme, "rowPressedBackground", "#E8E8E8")
      button.layer.cornerRadius = size / 2
    }
    if isAccountIcon { contentGap = 5 }
    button.accessibilityIdentifier = data["testID"] as? String
    button.accessibilityLabel = data["accessibilityLabel"] as? String
    if !isDrillIn, !data.string("actionKey").isEmpty {
      // Reproduce the trailing edge of IconButton's m=-7 while keeping its
      // full 36-point frame for padding/highlight behavior.
      endInset = 5
    }
    accessorySizeConstraints.append(contentsOf: [
      button.widthAnchor.constraint(equalToConstant: size),
      button.heightAnchor.constraint(equalToConstant: size),
    ])
    NSLayoutConstraint.activate(Array(accessorySizeConstraints.suffix(2)))
    while accessoryActions.count <= index { accessoryActions.append(("", nil)) }
    accessoryActions[index] =
      data.bool("disabled")
      ? ("", nil)
      : (data.string("actionKey"), nil)
  }

  private func accessoryAction(
    _ accessory: [String: Any],
    defaultKey: String = ""
  ) -> (String, NativeSelectionTarget?)? {
    if accessory.bool("disabled") { return nil }
    let key = accessory.string("actionKey", default: defaultKey)
    return key.isEmpty ? nil : (key, nil)
  }

  private func applyValueSegments(
    _ segments: [[String: Any]], to button: UIButton, theme: [String: Any]?
  ) {
    guard !segments.isEmpty else { return }
    let value = NSMutableAttributedString(string: "")
    let color = nativeListColor(theme, "primaryText", "#202020")
    // OneKey patch: rich currency changes font runs without dropping the established line baseline.
    let current = button.attributedTitle(for: .normal)
    var attributes =
      current.flatMap { $0.length > 0 ? $0.attributes(at: 0, effectiveRange: nil) : nil } ?? [:]
    attributes[.foregroundColor] = color
    if currentItem?.data.string("presentation") == "networkSelector"
      && currentItem?.data["height"] != nil
    {
      let paragraph =
        (attributes[.paragraphStyle] as? NSParagraphStyle)?.mutableCopy()
        as? NSMutableParagraphStyle ?? NSMutableParagraphStyle()
      paragraph.alignment = .right
      attributes[.paragraphStyle] = paragraph
    }
    for segment in segments {
      attributes[.font] = nativeListTabularFont(
        ofSize: segment.string("style") == "subscript" ? 10 : 16, weight: .medium)
      value.append(NSAttributedString(string: segment.string("text"), attributes: attributes))
    }
    button.setAttributedTitle(value, for: .normal)
  }

  private func applyStyledButton(_ button: UIButton, _ style: [String: Any]) {
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
      paragraph.alignment = marketTextAlignment(alignmentName)
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

  private func applyStyledFont(
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

  private func normalizeSingleLine(_ text: NSMutableAttributedString) {
    let pattern = try! NSRegularExpression(pattern: "\\r\\n|[\\r\\n]")
    for match in pattern.matches(in: text.string, range: NSRange(location: 0, length: text.length))
      .reversed()
    {
      text.replaceCharacters(in: match.range, with: " ")
    }
  }

  private func styledLineBreakMode(_ style: [String: Any], lines: Int) -> NSLineBreakMode {
    if style.string("truncate", default: "tail") != "clip" { return .byTruncatingTail }
    // Clipping disables wrapping; multi-line clip must wrap without adding an ellipsis.
    return lines == 1 ? .byClipping : .byWordWrapping
  }

  private func marketFontWeight(_ value: String, fallback: NativeListFontWeight)
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

  private func marketTextAlignment(_ value: String) -> NSTextAlignment {
    switch value {
    case "center": return .center
    case "start": return effectiveUserInterfaceLayoutDirection == .rightToLeft ? .right : .left
    case "end": return effectiveUserInterfaceLayoutDirection == .rightToLeft ? .left : .right
    default: return .natural
    }
  }
}
