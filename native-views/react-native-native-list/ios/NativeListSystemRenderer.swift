import UIKit

final class NativeListSystemCell: NativeListRendererCell {
  private let mainStack = UIStackView()
  private let titleRowStack = UIStackView()
  private let titleLabel = NativeListTextLabel()
  private let subtitleLabel = NativeListTextLabel()
  private let trailingStack = UIStackView()
  private let accessoryButtons = [NativeListAccessoryButton(type: .system)]
  private let leadingContainer = UIView()
  private let skeletonPrimary = UIView()
  private let skeletonSecondary = UIView()
  private var leadingWidth: NSLayoutConstraint!
  private var leadingHeight: NSLayoutConstraint!
  private var selectorViews: [UIView] = []
  private var selectorConstraints: [NSLayoutConstraint] = []
  private var currentItem: NativeListItem?
  private var actionKey = ""
  private var leftInset: CGFloat = 12
  private var rightInset: CGFloat = -12
  private var topInset: CGFloat = 8
  private var bottomInset: CGFloat = -8

  override init(frame: CGRect) {
    super.init(frame: frame)
    mainStack.axis = .vertical
    mainStack.alignment = .fill
    mainStack.setContentHuggingPriority(UILayoutPriority(1), for: .horizontal)
    titleRowStack.axis = .horizontal
    titleRowStack.alignment = .center
    titleRowStack.addArrangedSubview(titleLabel)
    titleLabel.setContentHuggingPriority(.defaultLow, for: .horizontal)
    titleLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
    mainStack.addArrangedSubview(titleRowStack)
    mainStack.addArrangedSubview(subtitleLabel)
    trailingStack.axis = .vertical
    trailingStack.alignment = .trailing
    trailingStack.addArrangedSubview(accessoryButtons[0])
    trailingStack.setContentHuggingPriority(.required, for: .horizontal)
    trailingStack.setContentCompressionResistancePriority(.required, for: .horizontal)
    accessoryButtons[0].addTarget(self, action: #selector(actionPressed), for: .touchUpInside)
    leadingContainer.translatesAutoresizingMaskIntoConstraints = false
    leadingWidth = leadingContainer.widthAnchor.constraint(equalToConstant: 40)
    leadingHeight = leadingContainer.heightAnchor.constraint(equalToConstant: 40)
    NSLayoutConstraint.activate([leadingWidth, leadingHeight])
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
  override func bindContent(
    _ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    recycleContent()
    currentItem = item
    root.axis = .horizontal
    root.alignment = .center
    root.spacing = 12
    leftInset = layout == "table" ? 16 : 12
    rightInset = -leftInset
    topInset = 8
    bottomInset = -8
    mainStack.alignment = .fill
    mainStack.spacing = 2
    titleRowStack.isHidden = false
    for label in [titleLabel, subtitleLabel] {
      label.attributedText = nil
      label.text = nil
      label.isHidden = true
      label.font = nativeListFont(
        ofSize: label === titleLabel ? 16 : 14, weight: label === titleLabel ? .medium : .regular)
      label.textColor = nativeListColor(
        theme, label === titleLabel ? "primaryText" : "secondaryText",
        label === titleLabel ? "#202020" : "#646464")
      label.numberOfLines = 1
      label.textAlignment = .natural
      label.lineBreakMode = .byTruncatingTail
      label.rowVerticalAlignment = nil
      label.rowOffsetY = 0
      label.transform = .identity
    }
    let button = accessoryButtons[0]
    button.setAttributedTitle(nil, for: .normal)
    button.setTitle(nil, for: .normal)
    button.isHidden = true
    button.titleLabel?.font = nativeListFont(ofSize: 16, weight: .medium)
    button.titleLabel?.numberOfLines = 1
    button.contentEdgeInsets = .zero
    button.contentHorizontalAlignment = .center
    button.contentVerticalAlignment = .center
    button.rowTextOffsetY = 0
    button.marketLineHeight = nil
    button.layer.cornerRadius = 0
    button.backgroundColor = .clear
    bindSystem(item, theme: theme)
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
    if style["leadingGap"] != nil && root.arrangedSubviews.contains(leadingContainer) {
      root.setCustomSpacing(CGFloat(style.double("leadingGap")), after: leadingContainer)
    }
    if style["trailingGap"] != nil { trailingStack.spacing = CGFloat(style.double("trailingGap")) }
    let warning = item.data.string("variant") == "warning"
    if warning, let text = style.dictionary("title") {
      NativeListTextStyles.applyStyledText(titleLabel, text)
    }
    if let text = style.dictionary("message") {
      NativeListTextStyles.applyStyledText(warning ? subtitleLabel : titleLabel, text)
    }
    if let text = style.dictionary("actionText") {
      NativeListTextStyles.applyStyledButton(button, text)
    }
    contentInsets = UIEdgeInsets(
      top: topInset, left: leftInset, bottom: -bottomInset, right: -rightInset)
    if root.axis == .horizontal,
      let alignment = style.dictionary("container")?["contentVerticalAlignment"] as? String
    {
      root.alignment = alignment == "top" ? .top : alignment == "bottom" ? .bottom : .center
    }
  }
  override func recycleContent() {
    NSLayoutConstraint.deactivate(selectorConstraints)
    selectorConstraints.removeAll()
    selectorViews.forEach { view in
      if let stack = view.superview as? UIStackView { stack.removeArrangedSubview(view) }
      view.removeFromSuperview()
    }
    selectorViews.removeAll()
    for view in root.arrangedSubviews {
      root.removeArrangedSubview(view)
      view.removeFromSuperview()
    }
    for view in [skeletonPrimary, skeletonSecondary] {
      mainStack.removeArrangedSubview(view)
      view.removeFromSuperview()
    }
    currentItem = nil
    actionKey = ""
  }
  @objc private func actionPressed() {
    emitAction(actionKey, from: accessoryButtons[0], source: "trailingAccessory", slot: 0)
  }
  private func showAccessory(
    _ index: Int, _ text: String, action: (String, NativeSelectionTarget?)?
  ) {
    accessoryButtons[0].setTitle(text, for: .normal)
    accessoryButtons[0].isHidden = text.isEmpty
    actionKey = action?.0 ?? ""
    accessoryButtons[0].setContentHuggingPriority(.required, for: .horizontal)
    accessoryButtons[0].setContentCompressionResistancePriority(.required, for: .horizontal)
  }
  private func show(_ label: UILabel, _ text: String, lines: Int) {
    label.text = text
    label.numberOfLines = lines
    label.isHidden = text.isEmpty
  }
  private func setLineHeight(_ label: UILabel, text: String, lineHeight: CGFloat) {
    let paragraph = NSMutableParagraphStyle()
    paragraph.minimumLineHeight = lineHeight
    paragraph.maximumLineHeight = lineHeight
    paragraph.alignment = label.textAlignment
    var attrs: [NSAttributedString.Key: Any] = [
      .font: label.font as Any, .foregroundColor: label.textColor as Any,
      .paragraphStyle: paragraph,
    ]
    if currentItem?.data.string("variant") == "warning"
      || currentItem?.data.string("presentation") == "market"
        && currentItem?.data.string("variant") == "noMatch"
    {
      attrs[.baselineOffset] = max(0, (lineHeight - label.font.lineHeight) / 2)
    }
    if currentItem?.data.string("presentation") == "market" { attrs[.kern] = 0 }
    label.attributedText = NSAttributedString(string: text, attributes: attrs)
  }
  private func setButtonLine(
    _ button: UIButton, text: String, font: UIFont, color: UIColor, lineHeight: CGFloat
  ) {
    let market = currentItem?.data.string("presentation") == "market"
    let paragraph = NSMutableParagraphStyle()
    paragraph.minimumLineHeight = lineHeight
    paragraph.maximumLineHeight = lineHeight
    paragraph.alignment = market ? .natural : .center
    let scale = max(1, traitCollection.displayScale)
    let offset = market ? ceil(max(0, (lineHeight - font.lineHeight) / 2) * scale) / scale : 0
    (button as? NativeListAccessoryButton)?.marketLineHeight = market ? lineHeight : nil
    button.setAttributedTitle(
      NSAttributedString(
        string: text,
        attributes: [
          .font: font, .foregroundColor: color, .paragraphStyle: paragraph, .baselineOffset: offset,
        ]), for: .normal)
  }
  private func bindSystem(_ item: NativeListItem, theme: [String: Any]?) {
    root.alignment = .center
    root.distribution = .fill
    let variant = item.data.string("variant")
    let isMarket = item.data.string("presentation") == "market"
    if isMarket {
      leftInset = 20
      rightInset = -20
      topInset = 12
      bottomInset = -12
    }
    if isMarket && variant == "retry" {
      let message = item.data.string("message")
      let text = item.data.string("actionText", default: "Retry")
      root.axis = .vertical
      // The source tertiary Button has -5 vertical margins around its 30pt frame.
      root.spacing = 7
      leftInset = 32
      rightInset = -32
      let height = CGFloat(item.data.double("height", default: message.isEmpty ? 52 : 120))
      let top = message.isEmpty ? 11 : max(32, (height - 56) / 2)
      topInset = top
      bottomInset = -(height - top - (message.isEmpty ? 30 : 61))
      if !message.isEmpty {
        show(titleLabel, message, lines: 2)
        titleLabel.font = nativeListTabularFont(ofSize: 16)
        titleLabel.textColor = nativeListColor(theme, "secondaryText", "#646464")
        titleLabel.textAlignment = .center
        setLineHeight(titleLabel, text: message, lineHeight: 24)
        root.addArrangedSubview(mainStack)
      }
      showAccessory(0, text, action: (item.data.string("actionKey"), nil))
      let button = accessoryButtons[0]
      button.backgroundColor = .clear
      button.layer.cornerRadius = 15
      setButtonLine(
        button, text: text, font: nativeListTabularFont(ofSize: 14, weight: .medium),
        color: nativeListColor(theme, "secondaryText", "#646464"), lineHeight: 20)
      let textWidth = button.intrinsicContentSize.width
      selectorConstraints.append(contentsOf: [
        button.widthAnchor.constraint(equalToConstant: textWidth + 18),
        button.heightAnchor.constraint(equalToConstant: 30),
      ])
      NSLayoutConstraint.activate(selectorConstraints)
      root.addArrangedSubview(trailingStack)
      return
    }
    if variant == "loading" && item.data.string("loadingStyle") == "skeleton" {
      leftInset = 20
      rightInset = -20
      topInset = 12
      bottomInset = -12
      let skeleton = NativeListMarketSkeleton(
        background: nativeListColor(theme, "background", "#FFFFFF"))
      skeleton.translatesAutoresizingMaskIntoConstraints = false
      skeleton.heightAnchor.constraint(equalToConstant: 32).isActive = true
      root.addArrangedSubview(skeleton)
      selectorViews.append(skeleton)
      return
    }
    if variant == "loading" && item.data.string("loadingStyle") == "spinner" {
      topInset = 16
      bottomInset = -16
      root.addArrangedSubview(mainStack)
      mainStack.alignment = .center
      mainStack.setContentHuggingPriority(.defaultLow, for: .horizontal)
      let indicator = UIActivityIndicatorView(style: .medium)
      indicator.color = nativeListColor(theme, "icon", "#0000009B")
      indicator.startAnimating()
      mainStack.addArrangedSubview(indicator)
      selectorViews.append(indicator)
      return
    }
    if isMarket && variant == "noMatch" {
      let padding = max(32, (CGFloat(item.data.double("height", default: 88)) - 24) / 2)
      topInset = padding
      bottomInset = -padding
      root.addArrangedSubview(mainStack)
      mainStack.alignment = .center
      mainStack.setContentHuggingPriority(.defaultLow, for: .horizontal)
      titleLabel.font = nativeListTabularFont(ofSize: 16)
      titleLabel.textColor = nativeListColor(theme, "secondaryText", "#646464")
      titleLabel.textAlignment = .center
      show(titleLabel, item.data.string("message"), lines: 1)
      setLineHeight(titleLabel, text: item.data.string("message"), lineHeight: 24)
      return
    }
    if isMarket && variant == "end" {
      topInset = 16
      bottomInset = -16
      // OneKey patch: an empty title stack must not consume the dot's line height.
      titleRowStack.isHidden = true
      root.addArrangedSubview(mainStack)
      mainStack.alignment = .center
      mainStack.setContentHuggingPriority(.defaultLow, for: .horizontal)
      let indicator = UIStackView()
      indicator.axis = .horizontal
      indicator.alignment = .center
      indicator.spacing = 8
      for width in [CGFloat(80), 4, 80] {
        let mark = UIView()
        mark.translatesAutoresizingMaskIntoConstraints = false
        mark.backgroundColor = nativeListColor(theme, "separator", "#0000001F")
        mark.layer.cornerRadius = width == 4 ? 2 : 0
        NSLayoutConstraint.activate([
          mark.widthAnchor.constraint(equalToConstant: width),
          mark.heightAnchor.constraint(equalToConstant: width == 4 ? 4 : 1),
        ])
        indicator.addArrangedSubview(mark)
      }
      mainStack.addArrangedSubview(indicator)
      selectorViews.append(indicator)
      return
    }
    // OneKey patch: deprecated-wallet warnings stay inside the scrolling list.
    if variant == "warning" {
      root.addArrangedSubview(mainStack)
      topInset = 14
      bottomInset = -14
      mainStack.spacing = 4
      titleLabel.font = nativeListFont(ofSize: 14, weight: .medium)
      titleLabel.numberOfLines = 0
      subtitleLabel.font = nativeListFont(ofSize: 14)
      subtitleLabel.numberOfLines = 0
      show(titleLabel, item.data.string("title"), lines: 0)
      show(subtitleLabel, item.data.string("message"), lines: 0)
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 20)
      setLineHeight(subtitleLabel, text: item.data.string("message"), lineHeight: 20)
      let borderColor = UIColor(
        nativeListHex: item.data.string("borderColor", default: "#E0E0E0"), fallback: .lightGray)
      for top in [true, false] {
        let border = UIView()
        border.translatesAutoresizingMaskIntoConstraints = false
        border.backgroundColor = borderColor
        contentView.addSubview(border)
        NSLayoutConstraint.activate([
          border.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: -8),
          border.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: 8),
          border.heightAnchor.constraint(equalToConstant: 1 / UIScreen.main.scale),
          top
            ? border.topAnchor.constraint(equalTo: contentView.topAnchor)
            : border.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),
        ])
        selectorViews.append(border)
      }
      return
    }
    if variant == "loading" {
      leadingWidth.constant = isMarket ? 32 : 40
      leadingHeight.constant = isMarket ? 32 : 40
      leadingContainer.backgroundColor = nativeListColor(theme, "strongBackground", "#F0F0F0")
      leadingContainer.layer.cornerRadius = isMarket ? 16 : 20
      root.addArrangedSubview(leadingContainer)
      configureSkeleton(skeletonPrimary, width: 120, height: 12, theme: theme)
      configureSkeleton(skeletonSecondary, width: 80, height: 12, theme: theme)
      mainStack.spacing = 8
      mainStack.addArrangedSubview(skeletonPrimary)
      mainStack.addArrangedSubview(skeletonSecondary)
    }
    show(
      titleLabel,
      item.data.string("message", default: variant == "end" ? "End" : ""),
      lines: 2
    )
    let alignsToStart = variant == "retry" || variant == "noMatch" || variant == "end"
    titleLabel.textAlignment = alignsToStart ? .natural : .center
    mainStack.alignment = variant == "loading" || alignsToStart ? .leading : .center
    if alignsToStart {
      mainStack.setContentHuggingPriority(.defaultLow, for: .horizontal)
      titleLabel.font = nativeListFont(ofSize: 14)
      titleLabel.textColor = nativeListColor(theme, "secondaryText", "#646464")
      setLineHeight(titleLabel, text: titleLabel.text ?? "", lineHeight: 20)
    }
    root.addArrangedSubview(mainStack)
    if variant == "retry" {
      showAccessory(0, "Retry", action: (item.data.string("actionKey"), nil))
      accessoryButtons[0].backgroundColor = nativeListColor(theme, "strongBackground", "#F0F0F0")
      accessoryButtons[0].layer.cornerRadius = 14
      accessoryButtons[0].contentEdgeInsets = UIEdgeInsets(top: 4, left: 10, bottom: 4, right: 10)
      setButtonLine(
        accessoryButtons[0],
        text: "Retry",
        font: nativeListFont(ofSize: 14, weight: .medium),
        color: nativeListColor(theme, "primaryText", "#202020"),
        lineHeight: 20
      )
      root.addArrangedSubview(trailingStack)
    }
  }
  private func configureSkeleton(
    _ view: UIView,
    width: CGFloat,
    height: CGFloat,
    theme: [String: Any]?
  ) {
    view.backgroundColor = nativeListColor(theme, "rowPressedBackground", "#E8E8E8")
    view.layer.cornerRadius = 4
    view.translatesAutoresizingMaskIntoConstraints = false
    if view.constraints.isEmpty {
      NSLayoutConstraint.activate([
        view.widthAnchor.constraint(equalToConstant: width),
        view.heightAnchor.constraint(equalToConstant: height),
      ])
    }
  }
  override func layoutSubviews() {
    super.layoutSubviews()
    if let item = currentItem, item.type == "system", item.data.string("presentation") == "market",
      ["noMatch", "retry"].contains(item.data.string("variant")), !titleLabel.isHidden
    {
      titleLabel.transform = .identity
      // Settle each nested stack before measuring; the content view alone can leave stale descendant frames.
      contentView.layoutIfNeeded()
      root.layoutIfNeeded()
      mainStack.layoutIfNeeded()
      titleRowStack.layoutIfNeeded()
      // React Native floors text origins to physical pixels after centering the line box.
      let scale = max(1, window?.screen.scale ?? traitCollection.displayScale)
      let origin = titleLabel.convert(titleLabel.bounds, to: contentView).origin
      let x = floor((contentView.bounds.width - titleLabel.bounds.width) / 2 * scale) / scale
      let contentHeight: CGFloat = item.data.string("variant") == "retry" ? 56 : 24
      let y =
        item.data.dictionary("style")?.dictionary("container")?["contentVerticalAlignment"] != nil
        ? origin.y : floor(max(32, (contentView.bounds.height - contentHeight) / 2) * scale) / scale
      titleLabel.transform = CGAffineTransform(translationX: x - origin.x, y: y - origin.y)
    }
  }
}

// Market/TokenListSkeleton: source geometry and the native Skeleton's 3s shimmer.
private final class NativeListMarketSkeleton: UIView {
  private let marks = (0..<5).map { _ in UIView() }
  private let gradients = (0..<5).map { _ in CAGradientLayer() }

  init(background: UIColor) {
    super.init(frame: .zero)
    var white: CGFloat = 1
    background.getWhite(&white, alpha: nil)
    let base = UIColor(nativeListHex: white < 0.5 ? "#111111" : "#FAFAFA", fallback: .white)
    let highlight = UIColor(
      nativeListHex: white < 0.5 ? "#333333" : "#CDCDCD", fallback: .lightGray)
    for (index, mark) in marks.enumerated() {
      mark.backgroundColor = base
      mark.clipsToBounds = true
      mark.layer.cornerRadius = index == 0 ? 16 : 8
      let gradient = gradients[index]
      gradient.cornerRadius = mark.layer.cornerRadius
      gradient.colors = [base.cgColor, highlight.cgColor, base.cgColor]
      gradient.locations = [0, 0.5, 1]
      gradient.startPoint = CGPoint(x: 0, y: 0.5)
      gradient.endPoint = CGPoint(x: 1, y: 0.5)
      mark.layer.addSublayer(gradient)
      addSubview(mark)
    }
  }

  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  override func layoutSubviews() {
    super.layoutSubviews()
    let frames = [
      CGRect(x: 0, y: 0, width: 32, height: 32),
      CGRect(x: 44, y: 0, width: 80, height: 16),
      CGRect(x: 44, y: 20, width: 60, height: 12),
      CGRect(x: bounds.width - 168, y: 7, width: 80, height: 18),
      CGRect(x: bounds.width - 80, y: 7, width: 80, height: 18),
    ]
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    for (index, frame) in frames.enumerated() {
      marks[index].frame = frame
      gradients[index].frame = marks[index].bounds
    }
    CATransaction.commit()
    updateAnimation()
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    updateAnimation()
  }

  private func updateAnimation() {
    for gradient in gradients {
      guard window != nil else {
        gradient.removeAllAnimations()
        continue
      }
      guard gradient.bounds.width > 0, gradient.animation(forKey: "shimmer") == nil else {
        continue
      }
      let animation = CABasicAnimation(keyPath: "transform.translation.x")
      animation.fromValue = -gradient.bounds.width
      animation.toValue = gradient.bounds.width
      animation.duration = 3
      animation.repeatCount = .infinity
      animation.timingFunction = CAMediaTimingFunction(name: .linear)
      gradient.add(animation, forKey: "shimmer")
    }
  }
}

enum NativeListSystemRenderer {
  static func appliesSizePreset(_ item: NativeListItem) -> Bool { !["warning", "spacer"].contains(item.data.string("variant")) }
  static func measure(_ item: NativeListItem, width: CGFloat) -> CGFloat {
    if item.type == "system", item.data.string("variant") == "spacer" {
      return CGFloat(item.data.int("height"))
    }
    // OneKey patch: warning height follows the current native font and available width.
    if item.type == "system", item.data.string("variant") == "warning" {
      let style = item.data.dictionary("style")
      let textWidth = max(
        1, width - CGFloat(style?.double("horizontalPadding", default: 12) ?? 12) * 2)
      func textHeight(_ key: String, weight: NativeListFontWeight) -> CGFloat {
        let textStyle = style?.dictionary(key)
        let lineHeight = CGFloat(textStyle?.double("lineHeight", default: 20) ?? 20)
        let fontSize = CGFloat(textStyle?.double("fontSize", default: 14) ?? 14)
        let paragraph = NSMutableParagraphStyle()
        paragraph.minimumLineHeight = lineHeight
        paragraph.maximumLineHeight = lineHeight
        let measured = ceil(
          (item.data.string(key) as NSString).boundingRect(
            with: CGSize(width: textWidth, height: .greatestFiniteMagnitude),
            options: [.usesLineFragmentOrigin, .usesFontLeading],
            attributes: [
              .font: nativeListFont(
                ofSize: fontSize,
                weight: NativeListTextStyles.marketFontWeight(
                  textStyle?.string("fontWeight") ?? "", fallback: weight)),
              .paragraphStyle: paragraph,
            ], context: nil
          ).height / lineHeight)
        return min(CGFloat(textStyle?.int("lines", default: Int.max) ?? Int.max), max(1, measured))
          * lineHeight
      }
      return CGFloat(style?.double("verticalPadding", default: 14) ?? 14) * 2
        + CGFloat(style?.double("lineGap", default: 4) ?? 4) + textHeight("title", weight: .medium)
        + textHeight("message", weight: .regular)
    }
    if item.data.string("variant") == "loading" && item.data.string("loadingStyle") == "skeleton" {
      return 56
    } else if item.data.string("variant") == "loading"
      && item.data.string("loadingStyle") == "spinner"
    {
      return 52
    } else if item.data.string("presentation") == "market" {
      return item.data.string("variant") == "loading" ? 68 : 44
    } else {
      switch item.data.string("variant") {
      case "noMatch", "end": return 36
      case "retry": return 44
      default: return 56
      }
    }
  }
}
