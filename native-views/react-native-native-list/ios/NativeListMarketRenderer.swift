import UIKit

final class NativeListMarketCell: NativeListRendererCell {
  override class func measure(
    _ item: NativeListItem, width: CGFloat, theme: [String: Any]?, layout: String
  ) -> CGFloat? {
    let style = item.data.dictionary("style")
    let stock = item.data.string("variant") == "stock"
    return max(
      stock ? 72 : 68,
      CGFloat(
        style?.dictionary("image")?.double("height", default: stock ? 40 : 32) ?? (stock ? 40 : 32))
        + 2 * CGFloat(style?.double("verticalPadding", default: 12) ?? 12))
  }
  private let visual = NativeListLeadingVisual(frame: .zero)
  private let mainStack = UIStackView()
  private let titleRowStack = UIStackView()
  private let titleLabel = NativeListTextLabel()
  private let subtitleLabel = NativeListTextLabel()
  private let tertiaryLabel = NativeListTextLabel()
  private let marketSubtitleStack = UIStackView()
  private let marketSubtitleSpacer = UIView()
  private let trailingStack = UIStackView()
  private let accessoryButtons = (0..<2).map { _ in NativeListAccessoryButton(type: .system) }
  private let marketBadgeButtons = (0..<3).map { _ in NativeListAccessoryButton(type: .system) }
  private let badgeSlots = (0..<3).map { _ in NativeListImageSlot() }
  private var marketBadgeImages: [UIView] { badgeSlots.map { $0.view } }
  private let leadingActionButton = UIButton(type: .system)
  private var leadingWidth: NSLayoutConstraint!
  private var leadingHeight: NSLayoutConstraint!
  private var selectorConstraints: [NSLayoutConstraint] = []
  private var accessorySizeConstraints: [NSLayoutConstraint] = []
  private var leadingActionKey: String?
  private var marketBadgeActionKeys: [String?] = []
  private var imageSignature = ""
  override var defaultCornerRadius: CGFloat { isHighlighted ? 12 : 0 }
  override var assetFields: [String] { ["leading", "badges"] }
  override init(frame: CGRect) {
    super.init(frame: frame)
    root.axis = .horizontal
    root.alignment = .center
    mainStack.axis = .vertical
    mainStack.alignment = .fill
    mainStack.setContentHuggingPriority(UILayoutPriority(1), for: .horizontal)
    mainStack.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    titleRowStack.axis = .horizontal
    titleRowStack.alignment = .center
    titleRowStack.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    titleLabel.setContentHuggingPriority(.defaultLow, for: .horizontal)
    titleLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
    titleRowStack.addArrangedSubview(titleLabel)
    for (index, button) in marketBadgeButtons.enumerated() {
      button.tag = index
      button.layer.cornerRadius = 4
      button.clipsToBounds = true
      button.addTarget(self, action: #selector(badgePressed(_:)), for: .touchUpInside)
      let image = badgeSlots[index].view
      image.isUserInteractionEnabled = false
      image.translatesAutoresizingMaskIntoConstraints = false
      button.addSubview(image)
      NSLayoutConstraint.activate([
        image.centerYAnchor.constraint(equalTo: button.centerYAnchor),
        image.widthAnchor.constraint(equalToConstant: 14),
        image.heightAnchor.constraint(equalToConstant: 14),
      ])
      titleRowStack.addArrangedSubview(button)
    }
    trailingStack.setContentHuggingPriority(.required, for: .horizontal)
    trailingStack.setContentCompressionResistancePriority(.required, for: .horizontal)
    accessoryButtons.forEach(trailingStack.addArrangedSubview)
    visual.translatesAutoresizingMaskIntoConstraints = false
    leadingWidth = visual.widthAnchor.constraint(equalToConstant: 32)
    leadingHeight = visual.heightAnchor.constraint(equalToConstant: 32)
    leadingActionButton.translatesAutoresizingMaskIntoConstraints = false
    NSLayoutConstraint.activate([
      leadingWidth, leadingHeight, leadingActionButton.widthAnchor.constraint(equalToConstant: 36),
      leadingActionButton.heightAnchor.constraint(equalToConstant: 36),
    ])
    leadingActionButton.adjustsImageWhenDisabled = false
    leadingActionButton.tintAdjustmentMode = .normal
    leadingActionButton.addTarget(self, action: #selector(leadingPressed), for: .touchUpInside)
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
  override func bindContent(
    _ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    NSLayoutConstraint.deactivate(selectorConstraints + accessorySizeConstraints)
    selectorConstraints = []
    accessorySizeConstraints = []
    root.arrangedSubviews.forEach {
      root.removeArrangedSubview($0)
      $0.removeFromSuperview()
    }
    mainStack.arrangedSubviews.forEach {
      mainStack.removeArrangedSubview($0)
      $0.removeFromSuperview()
    }
    marketSubtitleStack.arrangedSubviews.forEach {
      marketSubtitleStack.removeArrangedSubview($0)
      $0.removeFromSuperview()
    }
    mainStack.alignment = .fill
    titleRowStack.setContentHuggingPriority(.defaultLow, for: .horizontal)
    for label in [titleLabel, subtitleLabel, tertiaryLabel] {
      label.attributedText = nil
      label.text = nil
      label.isHidden = true
      label.numberOfLines = 1
      label.lineBreakMode = .byTruncatingTail
      label.textAlignment = .natural
      label.rowOffsetY = 0
      label.rowVerticalAlignment = nil
      label.setContentHuggingPriority(.defaultLow, for: .horizontal)
      label.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    }
    titleLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
    [titleRowStack, subtitleLabel, tertiaryLabel].forEach(mainStack.addArrangedSubview)
    leadingActionKey = nil
    leadingActionButton.setImage(nil, for: .normal)
    for (index, button) in marketBadgeButtons.enumerated() {
      resetButton(button)
      button.isHidden = true
      marketBadgeImages[index].isHidden = true
      if index >= item.data.dictionaries("badges").count
        || item.data.dictionaries("badges")[index].dictionary("icon") == nil
      {
        badgeSlots[index].recycle()
      }
    }
    accessoryButtons.forEach(resetButton)
    bindMarket(item, theme: theme)
    let style = item.data.dictionary("style")
    for (label, descriptor) in [
      (titleLabel, style?.dictionary("title")), (subtitleLabel, style?.dictionary("subtitle")),
      (tertiaryLabel, item.data.dictionary("subtitlePrefix")?.dictionary("style")),
    ] {
      if let descriptor { NativeListTextStyles.applyStyledText(label, textLayoutStyle(descriptor)) }
    }
    root.alignment =
      style?.dictionary("container")?.string("contentVerticalAlignment") == "top"
      ? .top
      : style?.dictionary("container")?.string("contentVerticalAlignment") == "bottom"
        ? .bottom : .center
  }
  private func resetButton(_ button: NativeListAccessoryButton) {
    button.setTitle(nil, for: .normal)
    button.setAttributedTitle(nil, for: .normal)
    button.setImage(nil, for: .normal)
    button.marketLineHeight = nil
    button.selectorSummaryLineHeight = nil
    button.rowTextOffsetY = 0
    button.contentVerticalAlignment = .center
    button.contentHorizontalAlignment = .center
    button.titleLabel?.numberOfLines = 1
    button.titleLabel?.lineBreakMode = .byTruncatingTail
    button.titleLabel?.textAlignment = .natural
    button.contentEdgeInsets = .zero
    button.imageEdgeInsets = .zero
    button.titleEdgeInsets = .zero
  }
  private func textLayoutStyle(_ style: [String: Any]) -> [String: Any] {
    style.filter { ["lines", "truncate", "verticalAlignment", "offsetY"].contains($0.key) }
  }
  override func accessibilityText(_ item: NativeListItem) -> String {
    item.data.string(
      "accessibilityLabel",
      default: [
        item.data.string("title"), item.data.string("subtitle"), item.data.string("price"),
        item.data.dictionary("change")?.string("text") ?? "",
      ].filter { !$0.isEmpty }.joined(separator: ", "))
  }
  override func updateMarketQuote(_ item: NativeListItem, theme: [String: Any]?) {
    guard retainBoundItem(item) else { return }
    bindQuote(item, theme: theme)
  }
  override func recycleContent() {
    visual.recycle()
    badgeSlots.forEach { $0.recycle() }
    imageSignature = ""
  }
  @objc private func leadingPressed() {
    if let key = leadingActionKey {
      emitAction(key, from: leadingActionButton, source: "leadingAction")
    }
  }
  @objc private func badgePressed(_ button: UIButton) {
    if marketBadgeActionKeys.indices.contains(button.tag),
      let key = marketBadgeActionKeys[button.tag]
    {
      emitAction(key, from: button, source: "marketBadge", slot: button.tag)
    }
  }
  private func show(_ label: UILabel, _ value: String, lines: Int) {
    label.text = value
    label.numberOfLines = lines
    label.isHidden = value.isEmpty
  }
  private func setLineHeight(_ label: UILabel, text: String, lineHeight: CGFloat) {
    let paragraph = NSMutableParagraphStyle()
    paragraph.minimumLineHeight = lineHeight
    paragraph.maximumLineHeight = lineHeight
    paragraph.alignment = label.textAlignment
    paragraph.lineBreakMode = label.lineBreakMode
    let baseline = max(0, (lineHeight - label.font.lineHeight) / 2)
    let scale = window?.screen.scale ?? traitCollection.displayScale
    label.attributedText = NSAttributedString(
      string: text,
      attributes: [
        .font: label.font as Any, .foregroundColor: label.textColor as Any,
        .paragraphStyle: paragraph, .kern: 0,
        .baselineOffset: lineHeight == 20 && label.font.pointSize == 14 && scale > 0
          ? ceil(baseline * scale) / scale : baseline,
      ])
  }
  private func setButtonLine(
    _ button: UIButton, text: String, font: UIFont, color: UIColor, lineHeight: CGFloat
  ) {
    guard !text.isEmpty else { return }
    let paragraph = NSMutableParagraphStyle()
    paragraph.minimumLineHeight = lineHeight
    paragraph.maximumLineHeight = lineHeight
    paragraph.alignment = .natural
    (button as? NativeListAccessoryButton)?.marketLineHeight = lineHeight
    let baseline = max(0, (lineHeight - font.lineHeight) / 2)
    let scale = window?.screen.scale ?? traitCollection.displayScale
    button.titleLabel?.font = font
    button.setAttributedTitle(
      NSAttributedString(
        string: text,
        attributes: [
          .font: font, .foregroundColor: color, .paragraphStyle: paragraph, .kern: 0,
          .baselineOffset: lineHeight == 20 && font.pointSize == 14 && scale > 0
            ? ceil(baseline * scale) / scale : baseline,
        ]), for: .normal)
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
  private func applyMarketTextStyle(
    _ label: UILabel,
    data: [String: Any]?,
    theme: [String: Any]?,
    defaultSize: CGFloat,
    defaultLineHeight: CGFloat,
    defaultWeight: NativeListFontWeight,
    defaultColor: UIColor,
    defaultAlignment: NSTextAlignment = .natural
  ) {
    let size = CGFloat(
      data?.double("fontSize", default: Double(defaultSize)) ?? Double(defaultSize))
    let lineHeight = CGFloat(
      data?.double("lineHeight", default: Double(defaultLineHeight)) ?? Double(defaultLineHeight))
    label.font = nativeListTabularFont(
      ofSize: size,
      weight: marketFontWeight(data?.string("fontWeight") ?? "", fallback: defaultWeight))
    label.textColor =
      data?["color"].flatMap { $0 as? String }.map {
        UIColor(nativeListHex: $0, fallback: defaultColor)
      } ?? defaultColor
    label.textAlignment =
      data?["alignment"] == nil
      ? defaultAlignment
      : marketTextAlignment(data?.string("alignment") ?? "")
    label.numberOfLines = min(3, max(1, data?.int("lines", default: 1) ?? 1))
    setLineHeight(label, text: label.text ?? "", lineHeight: lineHeight)
  }
  private func applyMarketButtonStyle(
    _ button: UIButton,
    data: [String: Any]?,
    defaultSize: CGFloat,
    defaultLineHeight: CGFloat,
    defaultWeight: NativeListFontWeight,
    color: UIColor,
    defaultAlignment: UIControl.ContentHorizontalAlignment
  ) {
    let size = CGFloat(
      data?.double("fontSize", default: Double(defaultSize)) ?? Double(defaultSize))
    let lineHeight = CGFloat(
      data?.double("lineHeight", default: Double(defaultLineHeight)) ?? Double(defaultLineHeight))
    setButtonLine(
      button,
      text: button.title(for: .normal) ?? "",
      font: nativeListTabularFont(
        ofSize: size,
        weight: marketFontWeight(data?.string("fontWeight") ?? "", fallback: defaultWeight)),
      color: data?["color"].flatMap { $0 as? String }.map {
        UIColor(nativeListHex: $0, fallback: color)
      } ?? color,
      lineHeight: lineHeight
    )
    if data?["alignment"] == nil {
      button.contentHorizontalAlignment = defaultAlignment
    } else {
      button.contentHorizontalAlignment =
        data?.string("alignment") == "start"
        ? .leading
        : data?.string("alignment") == "end" ? .trailing : .center
    }
    button.titleLabel?.numberOfLines = min(3, max(1, data?.int("lines", default: 1) ?? 1))
  }
  private func marketAttributedText(
    _ text: String,
    segments: [[String: Any]],
    style: [String: Any]?,
    defaultSize: CGFloat,
    defaultLineHeight: CGFloat,
    defaultWeight: NativeListFontWeight,
    color: UIColor,
    defaultAlignment: NSTextAlignment
  ) -> NSAttributedString {
    let size = CGFloat(
      style?.double("fontSize", default: Double(defaultSize)) ?? Double(defaultSize))
    let lineHeight = CGFloat(
      style?.double("lineHeight", default: Double(defaultLineHeight)) ?? Double(defaultLineHeight))
    let paragraph = NSMutableParagraphStyle()
    paragraph.minimumLineHeight = lineHeight
    paragraph.maximumLineHeight = lineHeight
    paragraph.alignment =
      style?["alignment"] == nil
      ? defaultAlignment
      : marketTextAlignment(style?.string("alignment") ?? "")
    let weight = marketFontWeight(style?.string("fontWeight") ?? "", fallback: defaultWeight)
    let resolvedColor =
      style?["color"].flatMap { $0 as? String }.map { UIColor(nativeListHex: $0, fallback: color) }
      ?? color
    let result = NSMutableAttributedString(string: "")
    let source = segments.isEmpty ? [["text": text]] : segments
    for segment in source {
      let segmentSize =
        style?["fontSize"] == nil && segment.string("style") == "subscript"
        ? ceil(size * 0.6) : size
      result.append(
        NSAttributedString(
          string: segment.string("text"),
          attributes: [
            .font: nativeListTabularFont(ofSize: segmentSize, weight: weight),
            .foregroundColor: resolvedColor,
            .kern: 0,
            .paragraphStyle: paragraph,
            .baselineOffset: max(
              0, (lineHeight - nativeListTabularFont(ofSize: size, weight: weight).lineHeight) / 2),
          ]))
    }
    return result
  }
  private func marketLeading(_ item: NativeListItem, style: [String: Any]?) -> [String: Any]? {
    guard var visual = item.data.dictionary("leading") else { return nil }
    guard let imageStyle = style?.dictionary("image") else { return visual }
    if let shape = imageStyle["shape"] as? String { visual["shape"] = shape }
    if let contentFit = imageStyle["contentFit"] as? String, var image = visual.dictionary("image")
    {
      image["contentFit"] = contentFit
      visual["image"] = image
    }
    return visual
  }
  private func bindMarket(_ item: NativeListItem, theme: [String: Any]?) {
    let variant = item.data.string("variant")
    let style = item.data.dictionary("style")
    let imageStyle = style?.dictionary("image")
    let imageWidth = CGFloat(
      imageStyle?.double("width", default: variant == "stock" ? 40 : 32)
        ?? (variant == "stock" ? 40 : 32))
    let imageHeight = CGFloat(
      imageStyle?.double("height", default: variant == "stock" ? 40 : 32)
        ?? (variant == "stock" ? 40 : 32))
    let horizontalPadding = CGFloat(
      style?.double("horizontalPadding", default: variant == "perp" ? 16 : 20)
        ?? (variant == "perp" ? 16 : 20))
    let verticalPadding = CGFloat(style?.double("verticalPadding", default: 12) ?? 12)
    contentInsets = UIEdgeInsets(
      top: verticalPadding, left: horizontalPadding, bottom: verticalPadding,
      right: horizontalPadding)
    root.spacing = CGFloat(
      style?.double("leadingGap", default: variant == "perp" ? 8 : 14)
        ?? (variant == "perp" ? 8 : 14))
    if let leadingAction = item.data.dictionary("leadingAction") {
      leadingActionButton.isHidden = false
      let tintColor = UIColor(
        nativeListHex: leadingAction.string("tintColor", default: "#646464"),
        fallback: .darkGray
      )
      leadingActionButton.tintColor = tintColor
      if let image = nativeListIcon(named: leadingAction.string("name")) {
        leadingActionButton.setImage(image, for: .normal)
        leadingActionButton.setImage(
          image.withTintColor(tintColor, renderingMode: .alwaysOriginal),
          for: .disabled
        )
      }
      leadingActionButton.isEnabled = !leadingAction.bool("disabled")
      leadingActionButton.alpha = leadingActionButton.isEnabled ? 1 : 0.4
      leadingActionButton.accessibilityIdentifier = leadingAction["testID"] as? String
      leadingActionButton.accessibilityLabel = leadingAction["accessibilityLabel"] as? String
      leadingActionKey = leadingAction.string("actionKey")
      root.addArrangedSubview(leadingActionButton)
      root.setCustomSpacing(5, after: leadingActionButton)
    }
    leadingWidth.constant = imageWidth
    leadingHeight.constant = imageHeight
    if let descriptor = marketLeading(item, style: style) {
      var resolved = imageStyle ?? [:]
      resolved["cornerRadius"] =
        imageStyle?.double(
          "cornerRadius",
          default: imageStyle?.string("shape") == "square"
            ? 0
            : imageStyle?.string("shape") == "rounded"
              ? 8 : Double(min(imageWidth, imageHeight) / 2))
        ?? Double(min(imageWidth, imageHeight) / 2)
      visual.bitmapBorderWidth = descriptor.string("borderColor").isEmpty ? 0 : 1
      visual.bind(descriptor, style: resolved, key: item.key, theme: theme, isUnread: false)
      root.addArrangedSubview(visual)
      let signature = NativeListImageSlot.signature([
        "key": item.key, "visual": descriptor, "fit": resolved["contentFit"] ?? NSNull(),
      ])
      if signature != imageSignature,
        let diagnostic = item.data.dictionary("diagnostics")?.string("imageBindActionKey"),
        !diagnostic.isEmpty,
        descriptor.dictionary("image") != nil || descriptor.dictionary("networkImage") != nil
      {
        onAction?(item, diagnostic, nil, nil)
      }
      imageSignature = signature
    } else {
      visual.recycle()
      imageSignature = ""
    }
    root.addArrangedSubview(mainStack)
    // OneKey patch: opt in to the source Market row's content gap.
    // root.setCustomSpacing(0, after: mainStack)
    root.setCustomSpacing(
      CGFloat(style?.double("contentTrailingGap", default: 0) ?? 0), after: mainStack)
    mainStack.spacing = CGFloat(style?.double("lineGap", default: 0) ?? 0)
    titleRowStack.spacing = CGFloat(style?.double("titleBadgeGap", default: 4) ?? 4)
    // OneKey patch: opt in without changing the other row templates' filled layout.
    if style?.string("titleBadgeLayout") == "inline" {
      mainStack.alignment = .leading
      titleRowStack.setContentHuggingPriority(.required, for: .horizontal)
    }
    show(
      titleLabel, item.data.string("title"),
      lines: style?.dictionary("title")?.int("lines", default: 1) ?? 1)
    applyMarketTextStyle(
      titleLabel, data: style?.dictionary("title"), theme: theme, defaultSize: 16,
      defaultLineHeight: 24, defaultWeight: .medium,
      defaultColor: nativeListColor(theme, "primaryText", "#202020"))
    let badges = Array(item.data.dictionaries("badges").prefix(marketBadgeButtons.count))
    marketBadgeActionKeys = badges.map { $0["actionKey"] as? String }
    for (index, badge) in badges.enumerated() {
      let button = marketBadgeButtons[index]
      let badgeStyle = badge.dictionary("style")
      let badgeFontSize = CGFloat(badgeStyle?.double("fontSize", default: 11) ?? 11)
      let badgeFontWeight = marketFontWeight(
        badgeStyle?.string("fontWeight") ?? "", fallback: .medium)
      // OneKey patch: SizableText supplies tabular numerals for explicit Market metrics.
      let badgeFont =
        badgeStyle == nil
        ? nativeListFont(ofSize: badgeFontSize, weight: badgeFontWeight)
        : nativeListTabularFont(ofSize: badgeFontSize, weight: badgeFontWeight)
      let hasBuiltInIcon = badge.string("iconName") == "verified"
      let hasRemoteIcon = badge.dictionary("icon") != nil
      let hasIcon = hasBuiltInIcon || hasRemoteIcon
      let text = badge.string("text")
      let toneColor: UIColor
      switch badge.string("tone") {
      case "success": toneColor = nativeListColor(theme, "positive", "#218358")
      case "danger": toneColor = nativeListColor(theme, "negative", "#CE2C31")
      case "info": toneColor = nativeListColor(theme, "info", "#0D74CE")
      case "warning": toneColor = nativeListColor(theme, "primaryText", "#202020")
      default: toneColor = nativeListColor(theme, "secondaryText", "#646464")
      }
      let foreground = UIColor(
        nativeListHex: badge.string("textColor", default: ""),
        fallback: toneColor
      )
      button.isHidden = false
      button.isEnabled = true
      button.isUserInteractionEnabled = !badge.string("actionKey").isEmpty
      button.accessibilityTraits = button.isUserInteractionEnabled ? .button : .staticText
      button.setTitle(text, for: .normal)
      button.setTitleColor(foreground, for: .normal)
      button.titleLabel?.font = badgeFont
      if let lineHeight = badgeStyle?["lineHeight"] as? Double {
        setButtonLine(
          button, text: text, font: badgeFont, color: foreground, lineHeight: CGFloat(lineHeight))
        // The explicit text-only line box must not cover an adjacent icon.
        if hasIcon { button.marketLineHeight = nil }
      }
      button.tintColor = foreground
      button.backgroundColor = UIColor(
        nativeListHex: badge.string("backgroundColor", default: ""),
        fallback: hasIcon && text.isEmpty
          ? .clear
          : nativeListColor(theme, "strongBackground", "#0000000F")
      )
      let iconOnly = hasIcon && text.isEmpty
      let iconSize: CGFloat = hasBuiltInIcon ? 16 : 14
      // OneKey patch: preserve the native defaults unless the caller supplies padding.
      let padding = CGFloat(badgeStyle?.double("horizontalPadding", default: 5) ?? 5)
      let hasCustomPadding = badgeStyle?["horizontalPadding"] != nil
      let leftPadding =
        hasCustomPadding
        ? padding + (hasRemoteIcon ? iconSize + 2 : 0) : hasRemoteIcon ? 20 : hasIcon ? 3 : 5
      // button.contentEdgeInsets = UIEdgeInsets(top: 0, left: iconOnly ? 0 : hasRemoteIcon ? 20 : hasIcon ? 3 : 5, bottom: 0, right: iconOnly ? 0 : 5)
      button.contentEdgeInsets = UIEdgeInsets(
        top: 0, left: iconOnly ? 0 : leftPadding, bottom: 0, right: iconOnly ? 0 : padding)
      button.imageEdgeInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: text.isEmpty ? 0 : 3)
      button.titleEdgeInsets = .zero
      button.imageView?.contentMode = .scaleAspectFit
      if hasBuiltInIcon {
        button.setImage(
          nativeListIcon(
            named: "BadgeVerifiedSolid", size: CGSize(width: iconSize, height: iconSize)),
          for: .normal
        )
      }
      // OneKey patch: match source badge metrics at physical-pixel precision.
      // let height = button.heightAnchor.constraint(equalToConstant: 18)
      let height = button.heightAnchor.constraint(
        equalToConstant: CGFloat(badgeStyle?.double("height", default: 18) ?? 18))
      let textWidth = (text as NSString).size(withAttributes: [.font: badgeFont]).width
      let scale = max(1, traitCollection.displayScale)
      let roundedTextWidth = badgeStyle == nil ? ceil(textWidth) : ceil(textWidth * scale) / scale
      let extraWidth =
        hasCustomPadding ? padding * 2 + (hasIcon ? iconSize + 2 : 0) : hasIcon ? iconSize + 11 : 10
      // let width = button.widthAnchor.constraint(equalToConstant: iconOnly ? iconSize : ceil(textWidth) + (hasIcon ? iconSize + 11 : 10))
      let width = button.widthAnchor.constraint(
        equalToConstant: iconOnly ? iconSize : roundedTextWidth + extraWidth)
      NSLayoutConstraint.activate([width, height])
      selectorConstraints.append(contentsOf: [width, height])
      if let icon = badge.dictionary("icon") {
        marketBadgeImages[index].isHidden = false
        marketBadgeImages[index].layer.cornerRadius = 7
        marketBadgeImages[index].clipsToBounds = true
        let imageLeading = marketBadgeImages[index].leadingAnchor.constraint(
          equalTo: button.leadingAnchor, constant: text.isEmpty ? 0 : 4)
        imageLeading.isActive = true
        selectorConstraints.append(imageLeading)
        badgeSlots[index].bind(icon, key: "\(item.key):badge:\(index)")
      }
      button.accessibilityLabel = badge.string("accessibilityLabel", default: badge.string("text"))
    }
    if !item.data.string("subtitle").isEmpty || !item.data.dictionaries("subtitleSegments").isEmpty
    {
      show(
        subtitleLabel, item.data.string("subtitle"),
        lines: style?.dictionary("subtitle")?.int("lines", default: 1) ?? 1)
      applyMarketTextStyle(
        subtitleLabel, data: style?.dictionary("subtitle"), theme: theme, defaultSize: 14,
        defaultLineHeight: 20, defaultWeight: .regular,
        defaultColor: nativeListColor(theme, "secondaryText", "#646464"))
      if !item.data.dictionaries("subtitleSegments").isEmpty {
        subtitleLabel.attributedText = marketAttributedText(
          item.data.string("subtitle"), segments: item.data.dictionaries("subtitleSegments"),
          style: style?.dictionary("subtitle"), defaultSize: 14, defaultLineHeight: 20,
          defaultWeight: .regular, color: nativeListColor(theme, "secondaryText", "#646464"),
          defaultAlignment: .natural)
      }
    }
    // OneKey patch: preserve volume width while the localized name truncates.
    let subtitlePrefix = item.data.dictionary("subtitlePrefix")
    let subtitlePadding = CGFloat(style?.double("subtitleTrailingPadding", default: 0) ?? 0)
    if subtitlePrefix != nil || subtitlePadding > 0 {
      mainStack.removeArrangedSubview(subtitleLabel)
      subtitleLabel.removeFromSuperview()
      mainStack.removeArrangedSubview(tertiaryLabel)
      tertiaryLabel.removeFromSuperview()
      marketSubtitleStack.axis = .horizontal
      marketSubtitleStack.alignment = .center
      marketSubtitleStack.spacing = 0
      marketSubtitleStack.clipsToBounds = true
      show(tertiaryLabel, subtitlePrefix?.string("text") ?? "", lines: 1)
      applyMarketTextStyle(
        tertiaryLabel, data: subtitlePrefix?.dictionary("style"), theme: theme, defaultSize: 12,
        defaultLineHeight: 16, defaultWeight: .regular,
        defaultColor: nativeListColor(theme, "secondaryText", "#646464"))
      tertiaryLabel.setContentHuggingPriority(.required, for: .horizontal)
      tertiaryLabel.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
      subtitleLabel.setContentHuggingPriority(.required, for: .horizontal)
      subtitleLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
      marketSubtitleStack.addArrangedSubview(tertiaryLabel)
      marketSubtitleStack.addArrangedSubview(subtitleLabel)
      marketSubtitleStack.addArrangedSubview(marketSubtitleSpacer)
      if !tertiaryLabel.isHidden && !subtitleLabel.isHidden {
        marketSubtitleStack.setCustomSpacing(
          CGFloat(subtitlePrefix?.double("gap", default: 4) ?? 4), after: tertiaryLabel)
      }
      mainStack.insertArrangedSubview(marketSubtitleStack, at: 1)
      let width = marketSubtitleStack.widthAnchor.constraint(
        equalTo: mainStack.widthAnchor, constant: -subtitlePadding)
      width.isActive = true
      selectorConstraints.append(width)
      if let maxWidth = subtitlePrefix?["maxWidth"] as? Double {
        let limit = tertiaryLabel.widthAnchor.constraint(
          lessThanOrEqualToConstant: CGFloat(maxWidth))
        limit.isActive = true
        selectorConstraints.append(limit)
      }
      marketSubtitleStack.isHidden = tertiaryLabel.isHidden && subtitleLabel.isHidden
    }
    root.addArrangedSubview(trailingStack)
    trailingStack.axis = .horizontal
    trailingStack.alignment = .center
    trailingStack.spacing = CGFloat(style?.double("trailingGap", default: 8) ?? 8)
    bindQuote(item, theme: theme)
  }
  private func bindQuote(_ item: NativeListItem, theme: [String: Any]?) {
    NSLayoutConstraint.deactivate(accessorySizeConstraints)
    accessorySizeConstraints.removeAll()
    let style = item.data.dictionary("style")
    let price = accessoryButtons[0]
    price.isUserInteractionEnabled = false
    price.isHidden = false
    price.setAttributedTitle(nil, for: .normal)
    price.setTitle(item.data.string("price"), for: .normal)
    applyMarketButtonStyle(
      price, data: style?.dictionary("price"), defaultSize: 16, defaultLineHeight: 24,
      defaultWeight: .medium, color: nativeListColor(theme, "primaryText", "#202020"),
      defaultAlignment: .trailing)
    if !item.data.dictionaries("priceSegments").isEmpty {
      price.setAttributedTitle(
        marketAttributedText(
          item.data.string("price"), segments: item.data.dictionaries("priceSegments"),
          style: style?.dictionary("price"), defaultSize: 16, defaultLineHeight: 24,
          defaultWeight: .medium, color: nativeListColor(theme, "primaryText", "#202020"),
          defaultAlignment: marketTextAlignment("end")), for: .normal)
    }
    let changeData = item.data.dictionary("change") ?? [:]
    let change = accessoryButtons[1]
    change.isUserInteractionEnabled = false
    change.isHidden = false
    change.setAttributedTitle(nil, for: .normal)
    change.setTitle(changeData.string("text"), for: .normal)
    let defaultChangeColor = nativeListColor(theme, "inverseText", "#FFFFFF")
    applyMarketButtonStyle(
      change, data: style?.dictionary("change"), defaultSize: 14, defaultLineHeight: 20,
      defaultWeight: .medium,
      color: UIColor(
        nativeListHex: changeData.string("textColor", default: "#FFFFFF"),
        fallback: defaultChangeColor), defaultAlignment: .center)
    if !changeData.dictionaries("textSegments").isEmpty {
      let changeTextColor = UIColor(
        nativeListHex: changeData.string("textColor", default: ""), fallback: defaultChangeColor)
      change.setAttributedTitle(
        marketAttributedText(
          changeData.string("text"), segments: changeData.dictionaries("textSegments"),
          style: style?.dictionary("change"), defaultSize: 14, defaultLineHeight: 20,
          defaultWeight: .medium, color: changeTextColor, defaultAlignment: .center), for: .normal)
    }
    let toneKey =
      changeData.string("tone") == "positive"
      ? "positive" : changeData.string("tone") == "negative" ? "negative" : "secondaryText"
    change.backgroundColor = UIColor(
      nativeListHex: changeData.string("backgroundColor", default: ""),
      fallback: nativeListColor(
        theme, toneKey,
        changeData.string("tone") == "positive"
          ? "#218358" : changeData.string("tone") == "negative" ? "#CE2C31" : "#8D8D8D"))
    change.layer.cornerRadius = CGFloat(style?.double("changeCornerRadius", default: 8) ?? 8)
    change.clipsToBounds = true
    let width = change.widthAnchor.constraint(
      equalToConstant: CGFloat(style?.double("changeWidth", default: 80) ?? 80))
    let height = change.heightAnchor.constraint(
      equalToConstant: CGFloat(style?.double("changeHeight", default: 32) ?? 32))
    width.isActive = true
    height.isActive = true
    accessorySizeConstraints.append(contentsOf: [width, height])
    if let priceStyle = style?.dictionary("price"), !textLayoutStyle(priceStyle).isEmpty {
      NativeListTextStyles.applyStyledButton(price, textLayoutStyle(priceStyle))
    }
    if let changeStyle = style?.dictionary("change"), !textLayoutStyle(changeStyle).isEmpty {
      NativeListTextStyles.applyStyledButton(change, textLayoutStyle(changeStyle))
    }
    accessibilityLabel = item.data.string(
      "accessibilityLabel",
      default: [
        item.data.string("title"), item.data.string("subtitle"), item.data.string("price"),
        changeData.string("text"),
      ].filter { !$0.isEmpty }.joined(separator: ", "))
  }
}
