import UIKit

final class NativeListDottedUnderlineLabel: NativeListTextLabel {
  var showsDottedUnderline = false {
    didSet { setNeedsLayout() }
  }

  // OneKey patch: migrated section titles include the source 3-point underline box.
  var reservesDottedUnderlineSpace = false {
    didSet {
      invalidateIntrinsicContentSize()
      setNeedsLayout()
      setNeedsDisplay()
    }
  }

  override var intrinsicContentSize: CGSize {
    var size = super.intrinsicContentSize
    if reservesDottedUnderlineSpace && showsDottedUnderline { size.height += 3 }
    return size
  }

  override func drawText(in rect: CGRect) {
    let textRect =
      reservesDottedUnderlineSpace && showsDottedUnderline
      ? CGRect(x: rect.minX, y: rect.minY, width: rect.width, height: max(0, rect.height - 3))
      : rect
    super.drawText(in: textRect)
  }

  var dottedUnderlineColor: UIColor = .clear {
    didSet {
      dottedUnderlineLayer.strokeColor = dottedUnderlineColor.cgColor
      setNeedsLayout()
    }
  }

  var dottedUnderlineVerticalOffset: CGFloat = 0 {
    didSet { setNeedsLayout() }
  }

  private let dottedUnderlineLayer = CAShapeLayer()

  override init(frame: CGRect) {
    super.init(frame: frame)
    dottedUnderlineLayer.fillColor = UIColor.clear.cgColor
    dottedUnderlineLayer.lineWidth = 1.5
    dottedUnderlineLayer.lineCap = .round
    dottedUnderlineLayer.lineDashPattern = [0, 4]
    layer.addSublayer(dottedUnderlineLayer)
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    defer { CATransaction.commit() }
    guard showsDottedUnderline, let attributedText else {
      dottedUnderlineLayer.path = nil
      dottedUnderlineLayer.isHidden = true
      return
    }
    let textWidth = ceil(
      attributedText.boundingRect(
        with: CGSize(width: CGFloat.greatestFiniteMagnitude, height: 24),
        options: [.usesLineFragmentOrigin, .usesFontLeading],
        context: nil
      ).width)
    dottedUnderlineLayer.frame = CGRect(
      x: 0,
      y: 0,
      width: bounds.width,
      height: bounds.height + 2 + dottedUnderlineVerticalOffset
    )
    // OneKey patch: explicit header underline occupies the reserved final two points.
    // let y = bounds.height + 1 + dottedUnderlineVerticalOffset
    let y =
      reservesDottedUnderlineSpace
      ? bounds.height - 1 : bounds.height + 1 + dottedUnderlineVerticalOffset
    let path = UIBezierPath()
    path.move(to: CGPoint(x: 1, y: y))
    path.addLine(to: CGPoint(x: max(1, textWidth - 1), y: y))
    dottedUnderlineLayer.path = path.cgPath
    dottedUnderlineLayer.isHidden = false
  }
}

final class NativeListSectionHeaderCell: NativeListRendererCell {
  override class func appliesSizePreset(_ item: NativeListItem) -> Bool {
    !["summary", "gallery"].contains(item.data.string("variant"))
  }
  override class func measure(
    _ item: NativeListItem, width: CGFloat, theme: [String: Any]?, layout: String
  ) -> CGFloat? {
    if layout == "table" { return 28 }
    if item.data.string("presentation") == "networkSelector" { return 47 }
    if item.data.string("variant") == "history" {
      return 16
    }
    if item.data.string("variant") == "summary" { return 68 }
    if item.data.string("variant") == "gallery" { return 32 }
    if item.data.dictionary("checkbox") != nil { return 56 }
    return layout == "linear" ? 30 : 36
  }
  override var defaultCornerRadius: CGFloat { isHighlighted ? 12 : 0 }
  private let mainStack = UIStackView()
  private let titleRowStack = UIStackView()
  private let titleLabel = NativeListDottedUnderlineLabel()
  private let subtitleLabel = NativeListTextLabel()
  private let trailingStack = UIStackView()
  private let accessoryButtons = [NativeListAccessoryButton(type: .system)]
  private let checkboxControl = NativeListAccessoryStack(frame: .zero)
  private let headerTitleIconImageView = UIImageView()
  private let headerValueIconImageView = UIImageView()
  private var accessoryActions: [(String, NativeSelectionTarget?)] = []
  private var currentItem: NativeListItem?
  private var currentTheme: [String: Any]?
  private var leftInset: CGFloat = 12
  private var rightInset: CGFloat = -12
  private var topInset: CGFloat = 8
  private var bottomInset: CGFloat = -8
  private var selectorConstraints: [NSLayoutConstraint] = []
  private var selectorTypographyRestorers: [() -> Void] = []
  private lazy var selectorTitleTap = UITapGestureRecognizer(
    target: self, action: #selector(titlePressed))
  override init(frame: CGRect) {
    super.init(frame: frame)
    root.axis = .horizontal
    root.alignment = .center
    root.spacing = 12
    mainStack.axis = .vertical
    mainStack.alignment = .fill
    mainStack.spacing = 2
    mainStack.setContentHuggingPriority(UILayoutPriority(1), for: .horizontal)
    mainStack.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    titleRowStack.axis = .horizontal
    titleRowStack.alignment = .center
    titleRowStack.spacing = 8
    titleRowStack.addArrangedSubview(titleLabel)
    mainStack.addArrangedSubview(titleRowStack)
    mainStack.addArrangedSubview(subtitleLabel)
    titleLabel.setContentHuggingPriority(.defaultLow, for: .horizontal)
    titleLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
    trailingStack.axis = .vertical
    trailingStack.alignment = .trailing
    trailingStack.spacing = 2
    trailingStack.setContentHuggingPriority(.required, for: .horizontal)
    trailingStack.setContentCompressionResistancePriority(.required, for: .horizontal)
    trailingStack.addArrangedSubview(accessoryButtons[0])
    trailingStack.addArrangedSubview(checkboxControl)
    accessoryButtons[0].addTarget(self, action: #selector(valuePressed), for: .touchUpInside)
    for icon in [headerTitleIconImageView, headerValueIconImageView] {
      icon.translatesAutoresizingMaskIntoConstraints = false
      icon.contentMode = .scaleAspectFit
      NSLayoutConstraint.activate([
        icon.widthAnchor.constraint(equalToConstant: 12),
        icon.heightAnchor.constraint(equalToConstant: 12),
      ])
    }
    checkboxControl.onAction = { [weak self] key, view, slot, target in
      self?.emitAction(key, from: view, source: "trailingAccessory", slot: slot, target: target)
    }
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
  override func bindContent(
    _ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    selectorTypographyRestorers.reversed().forEach { $0() }
    selectorTypographyRestorers = []
    currentItem = item
    currentTheme = theme
    NSLayoutConstraint.deactivate(selectorConstraints)
    selectorConstraints = []
    root.arrangedSubviews.forEach {
      root.removeArrangedSubview($0)
      $0.removeFromSuperview()
    }
    for icon in [headerTitleIconImageView, headerValueIconImageView] {
      if let parent = icon.superview as? UIStackView { parent.removeArrangedSubview(icon) }
      icon.removeFromSuperview()
      icon.image = nil
      icon.isHidden = true
    }
    titleLabel.removeGestureRecognizer(selectorTitleTap)
    titleLabel.isUserInteractionEnabled = false
    titleLabel.showsDottedUnderline = false
    titleLabel.reservesDottedUnderlineSpace = false
    titleLabel.dottedUnderlineVerticalOffset = 0
    for label in [titleLabel, subtitleLabel] {
      label.attributedText = nil
      label.text = nil
      label.isHidden = true
      label.numberOfLines = 1
      label.lineBreakMode = .byTruncatingTail
      label.textAlignment = .natural
      label.rowOffsetY = 0
      label.rowVerticalAlignment = nil
    }
    titleLabel.setContentHuggingPriority(.defaultLow, for: .horizontal)
    subtitleLabel.font = nativeListFont(ofSize: 14)
    subtitleLabel.textColor = nativeListColor(theme, "secondaryText", "#646464")
    mainStack.alignment = .fill
    mainStack.spacing = 2
    titleRowStack.spacing = 8
    trailingStack.axis = .vertical
    trailingStack.alignment = .trailing
    trailingStack.spacing = 2
    let value = accessoryButtons[0]
    value.setTitle(nil, for: .normal)
    value.setAttributedTitle(nil, for: .normal)
    value.titleLabel?.font = nativeListFont(ofSize: 16, weight: .medium)
    value.titleLabel?.numberOfLines = 1
    value.titleLabel?.lineBreakMode = .byTruncatingTail
    value.contentHorizontalAlignment = .center
    value.contentVerticalAlignment = .center
    value.rowTextOffsetY = 0
    value.marketLineHeight = nil
    value.selectorSummaryLineHeight = nil
    value.isHidden = true
    value.accessibilityIdentifier = nil
    value.setTitleColor(nativeListColor(theme, "primaryText", "#202020"), for: .normal)
    accessoryActions = []
    checkboxControl.reset()
    checkboxControl.isHidden = true
    leftInset = layout == "table" ? 16 : 12
    rightInset = -leftInset
    topInset = 8
    bottomInset = -8
    bindSectionHeader(item, theme: theme, layout: layout, checkboxState)
    let style = item.data.dictionary("style") ?? [:]
    if style["horizontalPadding"] != nil {
      leftInset = CGFloat(style.double("horizontalPadding"))
      rightInset = -leftInset
    }
    if style["verticalPadding"] != nil {
      topInset = CGFloat(style.double("verticalPadding"))
      bottomInset = -topInset
    }
    if style["lineGap"] != nil { mainStack.spacing = CGFloat(style.double("lineGap")) }
    if style["trailingGap"] != nil { trailingStack.spacing = CGFloat(style.double("trailingGap")) }
    applyTextStyle(item)
    if item.data.string("presentation") == "networkSelector", item.data["height"] != nil,
      item.data.dictionary("checkbox") != nil, !item.data.string("value").isEmpty
    {
      let width = trailingStack.widthAnchor.constraint(
        equalToConstant: accessoryButtons[0].intrinsicContentSize.width + 12 + 20)
      width.isActive = true
      selectorConstraints.append(width)
    }
    contentInsets = UIEdgeInsets(
      top: topInset, left: leftInset, bottom: -bottomInset, right: -rightInset)
    root.alignment =
      style.dictionary("container")?.string("contentVerticalAlignment") == "top"
      ? .top
      : style.dictionary("container")?.string("contentVerticalAlignment") == "bottom"
        ? .bottom : .center
    applySelectorTypography(item)
  }
  private func applyTextStyle(_ item: NativeListItem) {
    let style = item.data.dictionary("style") ?? [:]
    if let title = style.dictionary("title") {
      NativeListTextStyles.applyStyledText(titleLabel, title)
    }
    if let subtitle = style.dictionary("subtitle") {
      NativeListTextStyles.applyStyledText(subtitleLabel, subtitle)
    }
    if let value = style.dictionary("value") {
      NativeListTextStyles.applyStyledButton(accessoryButtons[0], value)
    }
  }
  override func bindSelectionContent(
    _ item: NativeListItem,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    currentItem = item
    checkboxControl.updateSelection(item, checkboxState: checkboxState)
    if item.data.string("variant") == "summary" {
      selectorTypographyRestorers.reversed().forEach { $0() }
      selectorTypographyRestorers = []
      updateSummaryText(item)
      applyValueSegments(
        item.data.dictionaries("valueSegments"), to: accessoryButtons[0], theme: currentTheme)
      applyTextStyle(item)
      applySelectorTypography(item)
    }
  }
  private func bindCheckbox(
    _ item: NativeListItem, _ descriptor: [String: Any],
    _ checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    checkboxControl.bind(
      item, descriptors: [descriptor], theme: currentTheme, style: [:], checkboxState: checkboxState
    )
  }
  override func recycleContent() {
    checkboxControl.reset()
    currentItem = nil
  }
  @objc private func titlePressed() {
    if let item = currentItem {
      emitAction(item.data.string("titleActionKey"), from: titleLabel, source: "leadingAction")
    }
  }
  @objc private func valuePressed() {
    if let action = accessoryActions.first {
      emitAction(
        action.0, from: accessoryButtons[0], source: "trailingAccessory", slot: 0, target: action.1)
    }
  }
  private func show(_ label: UILabel, _ text: String, lines: Int) {
    label.text = text
    label.numberOfLines = lines
    label.isHidden = text.isEmpty
  }
  private func bindSectionHeader(
    _ item: NativeListItem,
    theme: [String: Any]?,
    layout: String,
    _ checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    root.addArrangedSubview(mainStack)
    let variant = item.data.string("variant")
    // OneKey patch: title help is a separate target from checkbox and value actions.
    if !item.data.string("titleActionKey").isEmpty {
      titleLabel.isUserInteractionEnabled = true
      titleLabel.addGestureRecognizer(selectorTitleTap)
      titleLabel.setContentHuggingPriority(.required, for: .horizontal)
    }
    let isSummary = variant == "summary"
    let isGallery = variant == "gallery"
    let isTable = layout == "table"
    let isNetworkSelector = item.data.string("presentation") == "networkSelector"
    let isExplicitNetworkHeader = isNetworkSelector && item.data["height"] != nil
    if isExplicitNetworkHeader {
      // OneKey patch: the flexible title consumes spare space before trailing totals.
      trailingStack.setContentHuggingPriority(.required, for: .horizontal)
      trailingStack.setContentCompressionResistancePriority(.required, for: .horizontal)
    }
    titleLabel.reservesDottedUnderlineSpace =
      isExplicitNetworkHeader && !item.data.string("titleActionKey").isEmpty
    let isHistory = variant == "history"
    let headerWeight: NativeListFontWeight =
      isSummary
      ? .medium
      : isExplicitNetworkHeader
        && (item.data.dictionary("checkbox") != nil || item.data.string("titleActionKey").isEmpty)
        ? .semibold
        : isNetworkSelector
          ? .medium
          : isGallery || layout == "sectioned" ? .semibold : .regular
    titleLabel.font = nativeListFont(
      ofSize: isGallery ? 18 : isSummary ? 16 : isTable ? 11 : isHistory ? 12 : 14,
      weight: isHistory ? .semibold : headerWeight
    )
    titleLabel.textColor = nativeListColor(
      theme,
      isSummary || isGallery ? "primaryText" : "secondaryText",
      isSummary || isGallery ? "#202020" : "#646464"
    )
    show(titleLabel, item.data.string("title"), lines: 1)
    if isNetworkSelector {
      titleLabel.showsDottedUnderline =
        !isExplicitNetworkHeader || !item.data.string("titleActionKey").isEmpty
      titleLabel.dottedUnderlineVerticalOffset = isExplicitNetworkHeader ? 1 : 2
      titleLabel.dottedUnderlineColor = nativeListColor(
        theme,
        "secondaryText",
        "#646464"
      )
      leftInset = 12
      rightInset = -12
      let isAlphabet = isExplicitNetworkHeader && item.data.string("titleActionKey").isEmpty
      topInset = isAlphabet ? 8 : 12
      bottomInset = isAlphabet ? -8 : isExplicitNetworkHeader ? -12 : -15
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 20)
    } else if isHistory {
      leftInset = 0
      rightInset = 0
      topInset = 0
      bottomInset = 0
      setLineHeight(
        titleLabel,
        text: item.data.string("title").uppercased(),
        lineHeight: 16,
        letterSpacing: 0.8
      )
    } else if !isTable && !isGallery && !isSummary {
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 20)
      if layout == "linear" {
        // TokenManager's section title is a plain body label with mt=10,
        // unlike the reusable 36-point SectionHeader used by sectioned lists.
        titleLabel.font = nativeListFont(ofSize: 14)
        topInset = 10
        bottomInset = 0
      }
    }
    if isTable {
      mainStack.alignment = .leading
      titleRowStack.spacing = 4
      titleLabel.setContentHuggingPriority(.required, for: .horizontal)
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 14)
      topInset = 12
      bottomInset = -2
      if let icon = item.data.dictionary("titleIcon") {
        headerTitleIconImageView.image = nativeListIcon(named: icon.string("name"))
        headerTitleIconImageView.tintColor = UIColor(
          nativeListHex: icon.string("tintColor", default: "#646464"),
          fallback: .darkGray
        )
        headerTitleIconImageView.isHidden = false
        titleRowStack.insertArrangedSubview(headerTitleIconImageView, at: 1)
        if icon.string("name") != "ChevronGrabberVerOutline" {
          titleLabel.textColor = nativeListColor(theme, "primaryText", "#202020")
          setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 14)
        }
      }
    }
    if isGallery {
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 24)
      topInset = 0
      bottomInset = -8
    }
    show(subtitleLabel, item.data.string("subtitle"), lines: 1)
    if isSummary {
      titleLabel.showsDottedUnderline = true
      titleLabel.dottedUnderlineColor = nativeListColor(
        theme,
        "secondaryText",
        "#646464"
      )
      root.addArrangedSubview(trailingStack)
      // The surrounding Stack contributes mt=16/pb=12 while its XStack uses
      // px=20/py=8. The collection's 8-point inset supplies the outer 8 points
      // of horizontal padding, so the cell supplies the remaining 12.
      leftInset = 12
      rightInset = -12
      topInset = 24
      bottomInset = -20
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 24)
      accessoryButtons[0].accessibilityIdentifier = item.data["valueActionTestID"] as? String
      let valueActionKey = item.data.string("valueActionKey")
      let action: (String, NativeSelectionTarget?)? =
        valueActionKey.isEmpty
        ? nil
        : (valueActionKey, nil)
      showAccessory(
        0,
        item.data.string("value"),
        action: action,
        color: nativeListColor(theme, "secondaryText", "#646464")
      )
      accessoryButtons[0].titleLabel?.font = nativeListFont(
        ofSize: 16, weight: isExplicitNetworkHeader ? .medium : .regular)
      setButtonLine(
        accessoryButtons[0],
        text: item.data.string("value"),
        font: nativeListFont(ofSize: 16, weight: isExplicitNetworkHeader ? .medium : .regular),
        color: nativeListColor(theme, "secondaryText", "#646464"),
        lineHeight: 24
      )
    } else if !isGallery && !isHistory {
      root.addArrangedSubview(trailingStack)
      showAccessory(0, item.data.string("value"))
      if isTable {
        trailingStack.axis = .horizontal
        trailingStack.alignment = .center
        trailingStack.spacing = 4
        accessoryButtons[0].titleLabel?.font = nativeListFont(ofSize: 11)
        accessoryButtons[0].setTitleColor(
          nativeListColor(theme, "secondaryText", "#646464"),
          for: .normal
        )
        if let icon = item.data.dictionary("valueIcon") {
          headerValueIconImageView.image = nativeListIcon(named: icon.string("name"))
          headerValueIconImageView.tintColor = UIColor(
            nativeListHex: icon.string("tintColor", default: "#646464"),
            fallback: .darkGray
          )
          headerValueIconImageView.isHidden = false
          trailingStack.addArrangedSubview(headerValueIconImageView)
          if icon.string("name") != "ChevronGrabberVerOutline" {
            accessoryButtons[0].setTitleColor(
              nativeListColor(theme, "primaryText", "#202020"),
              for: .normal
            )
          }
        }
      }
      if let checkbox = item.data.dictionary("checkbox") {
        bindCheckbox(item, checkbox, checkboxState)
      }
      if item.data.dictionary("checkbox") != nil && !item.data.string("value").isEmpty {
        titleLabel.showsDottedUnderline = true
        titleLabel.dottedUnderlineColor = nativeListColor(
          theme,
          "secondaryText",
          "#646464"
        )
        trailingStack.axis = .horizontal
        trailingStack.alignment = .center
        trailingStack.spacing = 12
        topInset = 8
        bottomInset = -8
        accessoryButtons[0].titleLabel?.font = nativeListTabularFont(
          ofSize: 16,
          weight: .medium
        )
        setButtonLine(
          accessoryButtons[0],
          text: item.data.string("value"),
          font: nativeListTabularFont(ofSize: 16, weight: .medium),
          color: nativeListColor(theme, "primaryText", "#202020"),
          lineHeight: 24
        )
      }
    }
    applyValueSegments(
      item.data.dictionaries("valueSegments"), to: accessoryButtons[0], theme: theme)
  }
  private func setLineHeight(
    _ label: UILabel,
    text: String,
    lineHeight: CGFloat,
    letterSpacing: CGFloat = 0
  ) {
    let paragraphStyle = NSMutableParagraphStyle()
    paragraphStyle.minimumLineHeight = lineHeight
    paragraphStyle.maximumLineHeight = lineHeight
    paragraphStyle.alignment = label.textAlignment
    var attributes: [NSAttributedString.Key: Any] = [
      .font: label.font as Any,
      .foregroundColor: label.textColor as Any,
      .paragraphStyle: paragraphStyle,
    ]
    if currentItem?.data["height"] != nil
      && currentItem?.data.string("presentation") == "networkSelector"
    {
      let baselineOffset = max(0, (lineHeight - label.font.lineHeight) / 2)
      let scale = window?.screen.scale ?? traitCollection.displayScale
      attributes[.baselineOffset] =
        lineHeight == 20 && scale > 0 ? ceil(baselineOffset * scale) / scale : baselineOffset
    }
    if letterSpacing != 0 { attributes[.kern] = letterSpacing }
    label.attributedText = NSAttributedString(string: text, attributes: attributes)
  }
  private func setButtonLine(
    _ button: UIButton,
    text: String,
    font: UIFont,
    color: UIColor,
    lineHeight: CGFloat
  ) {
    guard !text.isEmpty else { return }
    let paragraphStyle = NSMutableParagraphStyle()
    paragraphStyle.minimumLineHeight = lineHeight
    paragraphStyle.maximumLineHeight = lineHeight
    let isSelectorValue =
      currentItem?.data.string("presentation") == "networkSelector"
      && currentItem?.data["height"] != nil && currentItem?.data.string("variant") != "summary"
    let isSelectorSummary =
      currentItem?.data.string("presentation") == "networkSelector"
      && currentItem?.data["height"] != nil && currentItem?.data.string("variant") == "summary"
    (button as? NativeListAccessoryButton)?.selectorSummaryLineHeight =
      isSelectorSummary ? lineHeight : nil
    // OneKey patch: summary text uses its source line box; currency retains trailing alignment.
    paragraphStyle.alignment =
      isSelectorValue ? .right : isSelectorSummary ? .natural : .center
    if isSelectorSummary {
      button.contentHorizontalAlignment = .leading
      button.titleLabel?.textAlignment = .natural
    }
    if isSelectorValue {
      button.contentHorizontalAlignment = .trailing
      button.titleLabel?.textAlignment = .right
    }
    let baselineOffset: CGFloat =
      isSelectorValue || isSelectorSummary
      ? max(0, (lineHeight - font.lineHeight) / 2) : 0
    let attributes: [NSAttributedString.Key: Any] = [
      .font: font,
      .foregroundColor: color,
      .paragraphStyle: paragraphStyle,
      .baselineOffset: baselineOffset,
    ]
    button.setAttributedTitle(
      NSAttributedString(string: text, attributes: attributes),
      for: .normal
    )
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
  private func updateSummaryText(_ item: NativeListItem) {
    let title = item.data.string("title")
    titleLabel.isHidden = title.isEmpty
    setLineHeight(titleLabel, text: title, lineHeight: 24)

    let value = item.data.string("value")
    let isExplicitNetworkHeader =
      item.data.string("presentation") == "networkSelector" && item.data["height"] != nil
    let valueButton = accessoryButtons[0]
    valueButton.isHidden = value.isEmpty
    if value.isEmpty {
      valueButton.setTitle(nil, for: .normal)
      valueButton.setAttributedTitle(nil, for: .normal)
    } else {
      setButtonLine(
        valueButton,
        text: value,
        font: nativeListFont(ofSize: 16, weight: isExplicitNetworkHeader ? .medium : .regular),
        color: nativeListColor(currentTheme, "secondaryText", "#646464"),
        lineHeight: 24
      )
    }
  }
  private func selectorTabularFont(_ font: UIFont) -> UIFont {
    var settings =
      font.fontDescriptor.fontAttributes[.featureSettings] as? [[UIFontDescriptor.FeatureKey: Int]]
      ?? []
    settings.removeAll { $0[.type] == kNumberSpacingType }
    settings.append([.type: kNumberSpacingType, .selector: kMonospacedNumbersSelector])
    return UIFont(
      descriptor: font.fontDescriptor.addingAttributes([.featureSettings: settings]),
      size: font.pointSize)
  }
  private func selectorTabularText(_ original: NSAttributedString) -> NSAttributedString {
    let result = NSMutableAttributedString(attributedString: original)
    // OneKey patch: body typography explicitly supplies letterSpacing=0 in Tamagui.
    result.addAttribute(.kern, value: 0, range: NSRange(location: 0, length: result.length))
    original.enumerateAttribute(.font, in: NSRange(location: 0, length: original.length)) {
      value, range, _ in
      if let font = value as? UIFont {
        result.addAttribute(.font, value: self.selectorTabularFont(font), range: range)
      }
    }
    return result
  }
  private func applySelectorTypography(_ item: NativeListItem) {
    guard
      ["accountSelector", "networkSelector", "walletSidebar"].contains(
        item.data.string("presentation"))
    else { return }
    func visit(_ view: UIView) {
      if let button = view as? UIButton {
        if let original = button.attributedTitle(for: .normal) {
          selectorTypographyRestorers.append { button.setAttributedTitle(original, for: .normal) }
          button.setAttributedTitle(selectorTabularText(original), for: .normal)
        }
      } else if let label = view as? UILabel, let font = label.font {
        let original = label.attributedText
        selectorTypographyRestorers.append {
          label.font = font
          label.attributedText = original
        }
        label.font = selectorTabularFont(font)
        if let original { label.attributedText = selectorTabularText(original) }
      }
      for child in view.subviews { visit(child) }
    }
    visit(contentView)
  }
}
