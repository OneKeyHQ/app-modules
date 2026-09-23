import UIKit

final class NativeListIdentityCell: NativeListRendererCell {
  override class func appliesSizePreset(_ item: NativeListItem) -> Bool {
    !["walletSidebar", "networkSelector"].contains(item.data.string("presentation"))
  }
  override class func measure(
    _ item: NativeListItem, width: CGFloat, theme: [String: Any]?, layout: String
  ) -> CGFloat? {
    if item.data.string("presentation") == "walletSidebar" {
      return item.data.dictionaries("badges").isEmpty ? 68 : 92
    }
    if item.data.string("presentation") == "networkSelector" { return 47 }
    return !item.data.string("tertiary").isEmpty
      ? 72 : !item.data.string("subtitle").isEmpty ? 60 : 56
  }
  private let visual = NativeListLeadingVisual(frame: .zero)
  private var fallbackLabel: UILabel { visual.fallbackTextView }
  private let mainStack = UIStackView()
  private let titleRowStack = UIStackView()
  private let titleLabel = NativeListTextLabel()
  private let subtitleLabel = NativeListTextLabel()
  private let tertiaryLabel = NativeListTextLabel()
  private let badgeLabel = NativeListInsetLabel()
  private let trailingStack = NativeListAccessoryStack(frame: .zero)
  private let leadingActionButton = UIButton(type: .custom)
  private var leadingWidth: NSLayoutConstraint!
  private var leadingHeight: NSLayoutConstraint!
  private var leftInset: CGFloat = 12
  private var rightInset: CGFloat = -12
  private var topInset: CGFloat = 8
  private var bottomInset: CGFloat = -8
  private var selectorConstraints: [NSLayoutConstraint] = []
  private var selectorViews: [UIView] = []
  private var selectorTypographyRestorers: [() -> Void] = []
  private var semanticBadgeLabels: [UILabel] = []
  private var semanticSubtitleLabels: [UILabel] = []
  private weak var walletBadgeLine: UIStackView?
  private var currentItem: NativeListItem?
  private var currentTheme: [String: Any]?
  private var leadingActionKey: String?
  override var assetFields: [String] { ["leading"] }
  override var defaultSeparatorInset: CGFloat { 60 }
  override var defaultCornerRadius: CGFloat {
    guard let item = currentItem else { return 0 }
    let presentation = item.data.string("presentation")
    if presentation == "walletSidebar", isHighlighted || item.data["height"] != nil { return 20 }
    if isHighlighted
      || (item.data["height"] != nil
        && ["accountSelector", "networkSelector"].contains(presentation))
    {
      return 12
    }
    return 0
  }
  override var defaultCornerCurve: CALayerCornerCurve {
    currentItem?.data.string("presentation") == "walletSidebar" ? .continuous : .circular
  }
  override var defaultVerticalAlignment: String? {
    currentItem?.data.string("presentation") == "walletSidebar" ? "center" : nil
  }
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
    titleRowStack.addArrangedSubview(titleLabel)
    titleRowStack.addArrangedSubview(badgeLabel)
    [titleRowStack, subtitleLabel, tertiaryLabel].forEach(mainStack.addArrangedSubview)
    visual.translatesAutoresizingMaskIntoConstraints = false
    leadingWidth = visual.widthAnchor.constraint(equalToConstant: 40)
    leadingHeight = visual.heightAnchor.constraint(equalToConstant: 40)
    leadingActionButton.translatesAutoresizingMaskIntoConstraints = false
    NSLayoutConstraint.activate([
      leadingWidth, leadingHeight, leadingActionButton.widthAnchor.constraint(equalToConstant: 36),
      leadingActionButton.heightAnchor.constraint(equalToConstant: 36),
    ])
    leadingActionButton.adjustsImageWhenDisabled = false
    leadingActionButton.tintAdjustmentMode = .normal
    leadingActionButton.addTarget(self, action: #selector(leadingPressed), for: .touchUpInside)
    trailingStack.onAction = { [weak self] key, view, slot, target in
      self?.emitAction(
        key, from: view, source: "trailingAccessory", slot: slot, target: target,
        anchorInset: self?.trailingStack.anchorInset(for: view) ?? 0)
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
    selectorViews.forEach {
      if let stack = $0.superview as? UIStackView { stack.removeArrangedSubview($0) }
      $0.removeFromSuperview()
    }
    selectorViews = []
    walletBadgeLine = nil
    semanticBadgeLabels = []
    semanticSubtitleLabels = []
    root.arrangedSubviews.forEach {
      root.removeArrangedSubview($0)
      $0.removeFromSuperview()
    }
    root.axis = .horizontal
    root.alignment = .center
    root.spacing = 12
    leftInset = layout == "table" ? 16 : 12
    rightInset = -leftInset
    topInset = 8
    bottomInset = -8
    mainStack.alignment = .fill
    mainStack.spacing = 2
    mainStack.arrangedSubviews.forEach {
      mainStack.setCustomSpacing(UIStackView.spacingUseDefault, after: $0)
    }
    titleRowStack.arrangedSubviews.forEach {
      titleRowStack.setCustomSpacing(UIStackView.spacingUseDefault, after: $0)
    }
    titleRowStack.spacing = 8
    titleRowStack.setContentHuggingPriority(.defaultLow, for: .horizontal)
    titleRowStack.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    for label in [titleLabel, subtitleLabel, tertiaryLabel, badgeLabel] {
      label.attributedText = nil
      label.text = nil
      label.isHidden = true
      label.numberOfLines = 1
      label.textAlignment = .natural
      label.lineBreakMode = .byTruncatingTail
      label.transform = .identity
      label.setContentHuggingPriority(.defaultLow, for: .horizontal)
      label.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
      if let text = label as? NativeListTextLabel {
        text.rowOffsetY = 0
        text.rowVerticalAlignment = nil
      }
    }
    titleLabel.font = nativeListFont(ofSize: 16, weight: .medium)
    titleLabel.textColor = nativeListColor(theme, "primaryText", "#202020")
    titleLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
    subtitleLabel.font = nativeListFont(ofSize: 14)
    subtitleLabel.textColor = nativeListColor(theme, "secondaryText", "#646464")
    tertiaryLabel.font = nativeListFont(ofSize: 14)
    tertiaryLabel.textColor = subtitleLabel.textColor
    badgeLabel.font = nativeListFont(ofSize: 12, weight: .medium)
    badgeLabel.horizontalInset = 0
    badgeLabel.topInset = 0
    badgeLabel.bottomInset = 0
    badgeLabel.setContentHuggingPriority(.required, for: .horizontal)
    leadingWidth.constant = 40
    leadingHeight.constant = 40
    visual.glyphSize = 18
    fallbackLabel.attributedText = nil
    fallbackLabel.font = nativeListFont(ofSize: 13, weight: .bold)
    trailingStack.reset()
    trailingStack.axis = .vertical
    trailingStack.alignment = .trailing
    trailingStack.spacing = 2
    leadingActionKey = nil
    leadingActionButton.setImage(nil, for: .normal)
    bindIdentity(item, theme: theme, selected: item.data.bool("selected"), checkboxState)
    let style = item.data.dictionary("style") ?? [:]
    if style["horizontalPadding"] != nil {
      leftInset = CGFloat(style.double("horizontalPadding"))
      rightInset = -leftInset
    }
    if style["verticalPadding"] != nil {
      topInset = CGFloat(style.double("verticalPadding"))
      bottomInset = -topInset
    }
    if style["lineGap"] != nil {
      mainStack.spacing = CGFloat(style.double("lineGap"))
      mainStack.arrangedSubviews.forEach {
        mainStack.setCustomSpacing(mainStack.spacing, after: $0)
      }
    }
    if style["leadingGap"] != nil, root.arrangedSubviews.contains(visual) {
      root.setCustomSpacing(CGFloat(style.double("leadingGap")), after: visual)
    }
    if style["titleBadgeGap"] != nil {
      if walletBadgeLine != nil {
        mainStack.setCustomSpacing(CGFloat(style.double("titleBadgeGap")), after: titleRowStack)
      } else {
        titleRowStack.setCustomSpacing(CGFloat(style.double("titleBadgeGap")), after: titleLabel)
      }
    }
    if style["trailingGap"] != nil { trailingStack.spacing = CGFloat(style.double("trailingGap")) }
    for (slot, labels) in [
      ("title", [titleLabel]),
      ("subtitle", semanticSubtitleLabels.isEmpty ? [subtitleLabel] : semanticSubtitleLabels),
      ("tertiary", [tertiaryLabel]),
      ("badge", semanticBadgeLabels.isEmpty ? [badgeLabel] : semanticBadgeLabels),
    ] {
      if let text = style.dictionary(slot) {
        labels.forEach { NativeListTextStyles.applyStyledText($0, text) }
      }
    }
    contentInsets = UIEdgeInsets(
      top: topInset, left: leftInset, bottom: -bottomInset, right: -rightInset)
    if root.axis == .horizontal {
      root.alignment =
        style.dictionary("container")?.string("contentVerticalAlignment") == "top"
        ? .top
        : style.dictionary("container")?.string("contentVerticalAlignment") == "bottom"
          ? .bottom : .center
    }
    applySelectorTypography(item)
  }
  override func bindSelectionContent(
    _ item: NativeListItem,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    currentItem = item
    trailingStack.updateSelection(item, checkboxState: checkboxState)
    if item.data.string("presentation") == "walletSidebar" {
      let color = nativeListColor(
        currentTheme, selectedState ? "primaryText" : "secondaryText",
        selectedState ? "#FFFFFFED" : "#FFFFFFAF")
      titleLabel.textColor = color
      if let original = titleLabel.attributedText {
        let text = NSMutableAttributedString(attributedString: original)
        text.addAttribute(
          .foregroundColor, value: color, range: NSRange(location: 0, length: text.length))
        titleLabel.attributedText = text
      }
      if let style = item.data.dictionary("style")?.dictionary("title") {
        NativeListTextStyles.applyStyledText(titleLabel, style)
      }
    }
  }
  override func layoutSubviews() {
    super.layoutSubviews()
    if currentItem?.data["height"] != nil,
      currentItem?.data.string("presentation") == "accountSelector",
      currentItem?.data.dictionary("style")?.dictionary("container")?["contentVerticalAlignment"]
        == nil, let descriptor = currentItem?.data.dictionaries("trailing").first,
      descriptor.string("kind") == "icon", descriptor.string("name") == "PlusSmallOutline",
      let control = trailingStack.firstVisibleControl
    {
      control.transform = .identity
      let origin = control.convert(control.bounds, to: contentView).minY
      control.transform = CGAffineTransform(translationX: 0, y: 11 - origin)
    }
  }
  override func recycleContent() {
    visual.recycle()
    trailingStack.reset()
    currentItem = nil
  }
  @objc private func leadingPressed() {
    if let key = leadingActionKey {
      emitAction(key, from: leadingActionButton, source: "leadingAction")
    }
  }
  private func show(_ label: UILabel, _ value: String, lines: Int) {
    label.text = value
    label.numberOfLines = lines
    label.isHidden = value.isEmpty
  }
  private func addLeading(_ descriptor: [String: Any]?, key: String) {
    root.addArrangedSubview(visual)
    guard let descriptor else {
      visual.recycle()
      return
    }
    let image = currentItem?.data.dictionary("style")?.dictionary("image") ?? [:]
    if image["width"] != nil { leadingWidth.constant = CGFloat(image.double("width")) }
    if image["height"] != nil { leadingHeight.constant = CGFloat(image.double("height")) }
    let wallet =
      currentItem?.data.string("presentation") == "walletSidebar"
      && currentItem?.data["height"] != nil
    visual.singleOuterMask =
      currentItem?.data.string("presentation") == "networkSelector"
      && currentItem?.data["height"] != nil
    visual.dashedBorderWidth = wallet ? 1 : 2
    visual.overlayTextFontSize = wallet ? 12 : 10
    visual.overlayTextLineHeight = wallet ? 16 : nil
    visual.overlayTextHorizontalInset = wallet ? 2 : nil
    if let fallback = descriptor.dictionary("fallbackIcon") {
      visual.glyphSize =
        wallet && fallback.string("name") == "LockSolid"
        ? 40
        : descriptor.dictionary("image") != nil
          ? min(leadingWidth.constant, leadingHeight.constant)
            * (fallback.string("name") == "GlobusOutline" ? 1.2 : 1) : 18
    }
    visual.bind(descriptor, style: image, key: key, theme: currentTheme, isUnread: false)
  }
  private func bindIdentity(
    _ item: NativeListItem,
    theme: [String: Any]?,
    selected: Bool,
    _ checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    if item.data.string("presentation") == "walletSidebar" {
      root.axis = .vertical
      root.alignment = .center
      root.spacing = 4
      leftInset = 4
      rightInset = -4
      topInset = 4
      bottomInset = -4
      mainStack.alignment = .center
      mainStack.spacing = 0
      titleRowStack.setContentHuggingPriority(.required, for: .horizontal)
      titleLabel.setContentHuggingPriority(.required, for: .horizontal)
      titleLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
      titleLabel.font = nativeListFont(ofSize: 12)
      titleLabel.textAlignment = .center
      fallbackLabel.font = nativeListFont(ofSize: 28)
      addLeading(item.data.dictionary("leading"), key: item.key)
      root.addArrangedSubview(mainStack)
      // OneKey patch: activate width constraints only after both stacks share an ancestor.
      if item.data["height"] != nil {
        titleLabel.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        titleRowStack.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        let width = mainStack.widthAnchor.constraint(equalTo: root.widthAnchor)
        width.isActive = true
        selectorConstraints.append(width)
        let titleWidth = titleRowStack.widthAnchor.constraint(
          lessThanOrEqualTo: mainStack.widthAnchor)
        titleWidth.isActive = true
        selectorConstraints.append(titleWidth)
      }
      show(titleLabel, item.data.string("title"), lines: 1)
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 16)
      titleLabel.textColor = nativeListColor(
        theme,
        selected ? "primaryText" : "secondaryText",
        selected ? "#FFFFFFED" : "#FFFFFFAF"
      )
      // OneKey patch: wallet tags belong below the centered name.
      let badges = item.data.dictionaries("badges")
      if !badges.isEmpty {
        let line = UIStackView()
        line.axis = .horizontal
        line.spacing = 4
        line.alignment = .center
        for badge in badges {
          let label = NativeListInsetLabel()
          let isSelector = item.data["height"] != nil
          let isWarning = badge.string("tone") == "warning"
          label.font = nativeListFont(ofSize: isSelector ? 11 : 12)
          label.textColor = nativeListColor(
            theme, isSelector && isWarning ? "caution" : "secondaryText",
            isSelector && isWarning ? "#AB6400" : "#646464")
          label.backgroundColor = nativeListColor(
            theme,
            isSelector
              ? (isWarning ? "cautionBackground" : "subduedBackground") : "strongBackground",
            isSelector && isWarning ? "#FFF8C5" : "#F0F0F0")
          label.horizontalInset = isSelector ? 6 : 4
          label.topInset = 2
          label.bottomInset = 2
          label.layer.cornerRadius = 4
          label.clipsToBounds = true
          setLineHeight(label, text: badge.string("text"), lineHeight: isSelector ? 14 : 16)
          semanticBadgeLabels.append(label)
          line.addArrangedSubview(label)
        }
        walletBadgeLine = line
        mainStack.spacing = 4
        mainStack.addArrangedSubview(line)
        selectorViews.append(line)
      }
      let accessories = item.data.dictionaries("trailing")
      if !accessories.isEmpty {
        trailingStack.axis = .horizontal
        trailingStack.alignment = .center
        trailingStack.spacing = 8
        root.addArrangedSubview(trailingStack)
        trailingStack.bind(
          item, descriptors: accessories, theme: theme,
          style: item.data.dictionary("style") ?? [:], defaultSpacing: 8,
          checkboxState: checkboxState)
      }
      return
    }
    if item.data.string("presentation") == "accountSelector" {
      leadingWidth.constant = 32
      leadingHeight.constant = 32
      titleLabel.font = nativeListFont(ofSize: 16)
    }
    if item.data.dictionary("leading")?.string("kind") == "network" {
      leadingWidth.constant = 32
      leadingHeight.constant = 32
    }
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
      leadingActionKey = leadingAction.string("actionKey")
      root.addArrangedSubview(leadingActionButton)
      // ListItem.IconButton is a medium tertiary button: its 36-point frame
      // carries m=-7. The collection already contributes ListItem's outer
      // mx=8, so move the frame 7 points into that inset and reduce only the
      // following gap by 7. This preserves the source button, avatar and text
      // positions without shrinking the 24-point SVG glyph or its hit target.
      leftInset = 5
      root.setCustomSpacing(5, after: leadingActionButton)
    }
    addLeading(item.data.dictionary("leading"), key: item.key)
    // OneKey patch: custom network initials match LetterAvatar size 32.
    if item.data.string("presentation") == "networkSelector",
      let leading = item.data.dictionary("leading"), leading.dictionary("image") == nil,
      leading.dictionary("fallbackIcon") == nil, !leading.string("fallbackText").isEmpty
    {
      fallbackLabel.font = nativeListFont(ofSize: 19, weight: .semibold)
      fallbackLabel.textColor = nativeListColor(theme, "inverseText", "#FCFCFC")
      setLineHeight(fallbackLabel, text: leading.string("fallbackText"), lineHeight: 27)
    }
    root.addArrangedSubview(mainStack)
    show(titleLabel, item.data.string("title"), lines: item.data.int("titleLines", default: 1))
    show(
      subtitleLabel, item.data.string("subtitle"), lines: item.data.int("subtitleLines", default: 1)
    )
    show(tertiaryLabel, item.data.string("tertiary"), lines: 1)
    if !item.data.string("subtitle").isEmpty, item.data.string("tertiary").isEmpty {
      mainStack.spacing = 0
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 24)
      setLineHeight(subtitleLabel, text: item.data.string("subtitle"), lineHeight: 20)
    }
    // OneKey patch: preserve independent balance/address truncation and warning tones.
    let segments = item.data.dictionaries("subtitleSegments")
    if !segments.isEmpty {
      subtitleLabel.isHidden = true
      mainStack.spacing = 0
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 24)
      let line = UIStackView()
      line.axis = .horizontal
      line.alignment = .center
      line.spacing = 0
      for segment in segments {
        if segment.bool("separatorBefore") {
          let gap = UIView()
          gap.translatesAutoresizingMaskIntoConstraints = false
          let dot = UIView()
          dot.translatesAutoresizingMaskIntoConstraints = false
          dot.backgroundColor = nativeListColor(theme, "disabledText", "#8D8D8D")
          dot.layer.cornerRadius = 2
          gap.addSubview(dot)
          NSLayoutConstraint.activate([
            gap.widthAnchor.constraint(equalToConstant: 16),
            gap.heightAnchor.constraint(equalToConstant: 20),
            dot.widthAnchor.constraint(equalToConstant: 4),
            dot.heightAnchor.constraint(equalToConstant: 4),
            dot.centerXAnchor.constraint(equalTo: gap.centerXAnchor),
            dot.centerYAnchor.constraint(equalTo: gap.centerYAnchor),
          ])
          line.addArrangedSubview(gap)
        }
        let label = NativeListTextLabel()
        label.font = nativeListFont(ofSize: 14)
        label.lineBreakMode = .byTruncatingTail
        // Match V1: the balance may shrink, but keep the shortened address intact.
        label.setContentCompressionResistancePriority(
          item.data.string("presentation") == "accountSelector" && segment.bool("separatorBefore")
            ? .defaultHigh
            : .defaultLow,
          for: .horizontal
        )
        label.textColor = dataTextColor(segment.string("tone", default: "secondary"), theme: theme)
        setLineHeight(label, text: segment.string("text"), lineHeight: 20)
        let runs = segment.dictionaries("textSegments")
        if !runs.isEmpty {
          let value = NSMutableAttributedString(string: "")
          let paragraph = NSMutableParagraphStyle()
          paragraph.minimumLineHeight = 20
          paragraph.maximumLineHeight = 20
          for run in runs {
            value.append(
              NSAttributedString(
                string: run.string("text"),
                attributes: [
                  .font: nativeListFont(ofSize: run.string("style") == "subscript" ? 9 : 14),
                  .foregroundColor: label.textColor as Any,
                  .paragraphStyle: paragraph,
                  .baselineOffset: max(0, (20 - nativeListFont(ofSize: 14).lineHeight) / 2),
                ]))
          }
          label.attributedText = value
        }
        semanticSubtitleLabels.append(label)
        line.addArrangedSubview(label)
      }
      let filler = UIView()
      filler.setContentHuggingPriority(UILayoutPriority(1), for: .horizontal)
      line.addArrangedSubview(filler)
      mainStack.insertArrangedSubview(line, at: 2)
      selectorViews.append(line)
    }
    let matches = item.data.dictionaries("titleMatch")
    if !matches.isEmpty {
      let text = NSMutableAttributedString(
        attributedString: titleLabel.attributedText
          ?? NSAttributedString(string: item.data.string("title")))
      for match in matches {
        let start = match.int("start")
        let end = match.int("end")
        if start >= 0 && end > start && end <= text.length {
          text.addAttribute(
            .foregroundColor, value: nativeListColor(theme, "info", "#0D74CE"),
            range: NSRange(location: start, length: end - start))
        }
      }
      titleLabel.attributedText = text
    }
    tertiaryLabel.textColor = nativeListColor(
      theme,
      item.data.string("tertiaryTone") == "info" ? "info" : "secondaryText",
      item.data.string("tertiaryTone") == "info" ? "#0D74CE" : "#646464"
    )
    let badges = item.data.dictionaries("badges").prefix(2).map { $0.string("text") }
    if !badges.isEmpty {
      let badgeText = badges.joined(separator: " · ")
      show(badgeLabel, badgeText, lines: 1)
      badgeLabel.horizontalInset = 8
      badgeLabel.topInset = 2
      badgeLabel.bottomInset = 2
      setLineHeight(badgeLabel, text: badgeText, lineHeight: 16)
      badgeLabel.backgroundColor = nativeListColor(theme, "strongBackground", "#F0F0F0")
      badgeLabel.textColor = nativeListColor(theme, "secondaryText", "#646464")
      badgeLabel.layer.cornerRadius = 4
      badgeLabel.clipsToBounds = true
    }
    root.addArrangedSubview(trailingStack)
    let accessories = item.data.dictionaries("trailing")
    if accessories.contains(where: { $0.string("kind") == "checkbox" })
      && accessories.contains(where: { $0.string("kind") == "value" })
    {
      trailingStack.axis = .horizontal
      trailingStack.alignment = .center
      trailingStack.spacing = 12
    } else if accessories.count == 2,
      accessories[0].string("kind") == "valuePair",
      accessories[1].string("kind") == "menu"
    {
      trailingStack.axis = .horizontal
      trailingStack.alignment = .center
      trailingStack.spacing = 8
    } else if accessories.count == 2,
      accessories[0].string("kind") == "icon",
      accessories[0].string("name") == "PencilOutline",
      accessories[1].string("kind") == "icon",
      accessories[1].string("name") == "DragOutline"
    {
      trailingStack.axis = .horizontal
      trailingStack.alignment = .center
      // Each source IconButton has a 36-point frame and m=-7. XStack gap=$6
      // therefore places the physical frames 10 points apart (46-point
      // center distance), not 24 points apart.
      trailingStack.spacing = 10
    }
    trailingStack.bind(
      item, descriptors: accessories, theme: theme, style: item.data.dictionary("style") ?? [:],
      defaultSpacing: trailingStack.spacing, checkboxState: checkboxState)
    if let gap = trailingStack.contentGap { root.setCustomSpacing(gap, after: mainStack) }
    if let inset = trailingStack.endInset { rightInset = -inset }
    if item.data.string("presentation") == "accountSelector",
      accessories.count == 1,
      let accessory = trailingStack.firstVisibleControl
    {
      // The vertical trailing stack has no stable intrinsic width, so keep its
      // flexible space in the account title and subtitle column.
      let width = trailingStack.widthAnchor.constraint(equalTo: accessory.widthAnchor)
      width.isActive = true
      selectorConstraints.append(width)
    }
  }
  private func dataTextColor(_ tone: String, theme: [String: Any]?) -> UIColor {
    switch tone.isEmpty ? "primary" : tone {
    // OneKey patch: account warnings and hidden balances use existing theme tokens.
    case "disabled": return nativeListColor(theme, "disabledText", "#8D8D8D")
    case "caution": return nativeListColor(theme, "caution", "#AB6400")
    case "secondary": return nativeListColor(theme, "secondaryText", "#646464")
    case "positive": return nativeListColor(theme, "positive", "#218358")
    case "negative": return nativeListColor(theme, "negative", "#CE2C31")
    default: return nativeListColor(theme, "primaryText", "#202020")
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
    let presentation = currentItem?.data.string("presentation") ?? ""
    if presentation == "walletSidebar" {
      paragraphStyle.lineBreakMode = label.lineBreakMode
    }
    var attributes: [NSAttributedString.Key: Any] = [
      .font: label.font as Any,
      .foregroundColor: label.textColor as Any,
      .paragraphStyle: paragraphStyle,
    ]
    let isNetworkFallback = label === fallbackLabel && presentation == "networkSelector"
    if (currentItem?.data["height"] != nil
      && ["accountSelector", "walletSidebar"].contains(presentation)) || isNetworkFallback
    {
      attributes[.baselineOffset] = max(0, (lineHeight - label.font.lineHeight) / 2)
    }
    if letterSpacing != 0 { attributes[.kern] = letterSpacing }
    label.attributedText = NSAttributedString(string: text, attributes: attributes)
  }
}
