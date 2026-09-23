import UIKit

final class NativeListActivityCell: NativeListRendererCell {
  private let visual = NativeListLeadingVisual()
  private let column = UIStackView()
  private let titleLine = UIStackView()
  private let title = NativeListTextLabel()
  private let descriptionLabel = NativeListTextLabel()
  private let status = NativeListTextLabel()
  private let failure = NativeListTextLabel()
  private let amounts = UIStackView()
  private let amountLabels = [
    NativeListAccessoryButton(type: .system), NativeListAccessoryButton(type: .system),
  ]
  private let actions = UIStackView()
  private let buttons = (0..<3).map { _ in UIButton(type: .system) }
  private var actionKeys: [String] = []
  private var dimensions: [NSLayoutConstraint] = []
  override var assetFields: [String] { ["leading", "secondaryLeading"] }

  override init(frame: CGRect) {
    super.init(frame: frame)
    root.axis = .horizontal
    column.axis = .vertical
    column.alignment = .fill
    column.setContentHuggingPriority(UILayoutPriority(1), for: .horizontal)
    column.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    titleLine.axis = .horizontal
    titleLine.alignment = .center
    titleLine.spacing = 8
    title.setContentCompressionResistancePriority(.required, for: .horizontal)
    failure.setContentHuggingPriority(.required, for: .horizontal)
    titleLine.addArrangedSubview(title)
    titleLine.addArrangedSubview(failure)
    [titleLine, descriptionLabel, status, actions].forEach(column.addArrangedSubview)
    amounts.axis = .vertical
    amounts.alignment = .trailing
    amounts.setContentHuggingPriority(.required, for: .horizontal)
    amounts.setContentCompressionResistancePriority(.required, for: .horizontal)
    amountLabels.forEach(amounts.addArrangedSubview)
    actions.axis = .horizontal
    actions.alignment = .center
    actions.spacing = 8
    for (index, button) in buttons.enumerated() {
      button.tag = index
      button.titleLabel?.font = nativeListFont(ofSize: 12, weight: .medium)
      button.layer.cornerRadius = 8
      button.contentEdgeInsets = UIEdgeInsets(top: 4, left: 8, bottom: 4, right: 8)
      button.addTarget(self, action: #selector(pressed(_:)), for: .touchUpInside)
      actions.addArrangedSubview(button)
    }
    [visual, column, amounts].forEach(root.addArrangedSubview)
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
  override func bindContent(
    _ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    let data = item.data
    let style = data.dictionary("style") ?? [:]
    let image = style.dictionary("image") ?? [:]
    root.spacing = 12
    root.setCustomSpacing(CGFloat(style.double("leadingGap", default: 12)), after: visual)
    switch style.dictionary("container")?.string("contentVerticalAlignment") {
    case "top": root.alignment = .top
    case "bottom": root.alignment = .bottom
    default: root.alignment = .center
    }
    column.spacing = CGFloat(style.double("lineGap"))
    titleLine.spacing = CGFloat(style.double("titleBadgeGap", default: 8))
    amounts.spacing = CGFloat(style.double("trailingGap", default: 2))
    contentInsets = UIEdgeInsets(
      top: CGFloat(style.double("verticalPadding", default: 8)),
      left: CGFloat(style.double("horizontalPadding", default: layout == "table" ? 16 : 12)),
      bottom: CGFloat(style.double("verticalPadding", default: 8)),
      right: CGFloat(style.double("horizontalPadding", default: layout == "table" ? 16 : 12)))
    NSLayoutConstraint.deactivate(dimensions)
    dimensions = [
      visual.widthAnchor.constraint(equalToConstant: CGFloat(image.double("width", default: 40))),
      visual.heightAnchor.constraint(equalToConstant: CGFloat(image.double("height", default: 40))),
    ]
    NSLayoutConstraint.activate(dimensions)
    visual.bind(
      data.dictionary("leading") ?? [:], style: image, key: item.key, theme: theme, isUnread: false,
      secondaryVisual: data.dictionary("secondaryLeading"))
    func bind(
      _ view: NativeListTextLabel, _ value: String, _ slot: String, size: CGFloat,
      weight: NativeListFontWeight = .regular, color: UIColor, line: CGFloat, lines: Int = 1
    ) {
      NativeListResolvedText(
        value, style: style.dictionary(slot), size: size, weight: weight, color: color,
        lineHeight: line, lines: lines
      ).bind(view)
    }
    bind(
      title, data.string("title"), "title", size: 16, weight: .medium,
      color: nativeListColor(theme, "primaryText", "#202020"), line: 24)
    bind(
      descriptionLabel, data.string("description"), "description", size: 14,
      color: nativeListColor(theme, "secondaryText", "#646464"), line: 20, lines: 2)
    let failed = data.string("status") == "Failed"
    bind(
      status, failed ? "" : data.string("status"), "status", size: 12,
      color: nativeListColor(theme, "secondaryText", "#646464"),
      line: nativeListFont(ofSize: 12).lineHeight)
    bind(
      failure, failed ? "  Failed  " : "", "status", size: 12, weight: .medium,
      color: nativeListColor(theme, "negative", "#CE2C31"),
      line: nativeListFont(ofSize: 12, weight: .medium).lineHeight)
    failure.backgroundColor = nativeListColor(theme, "criticalBackground", "#F3000D14")
    failure.layer.cornerRadius = 4
    failure.clipsToBounds = true
    for (index, slot) in ["primaryAmount", "secondaryAmount"].enumerated() {
      let button = amountLabels[index]
      let value = data.string(slot)
      let color =
        index == 0
        ? nativeListColor(
          theme, value.hasPrefix("+") ? "positive" : "primaryText",
          value.hasPrefix("+") ? "#218358" : "#202020")
        : nativeListColor(theme, "secondaryText", "#646464")
      button.titleLabel?.numberOfLines = 1
      button.titleLabel?.lineBreakMode = .byTruncatingTail
      button.contentHorizontalAlignment = .center
      button.contentVerticalAlignment = .center
      button.rowTextOffsetY = 0
      let resolved = NativeListResolvedText(
        value, style: nil, size: index == 0 ? 16 : 14, weight: index == 0 ? .medium : .regular,
        color: color, lineHeight: index == 0 ? 24 : 20, lines: 1, tabular: true)
      button.setAttributedTitle(resolved.attributed, for: .normal)
      button.titleLabel?.font = resolved.font
      button.isHidden = value.isEmpty
      if let text = style.dictionary(slot) { NativeListTextStyles.applyStyledButton(button, text) }
    }
    let descriptors = Array(data.dictionaries("footerActions").prefix(3))
    actions.isHidden = descriptors.isEmpty
    actionKeys = descriptors.map { $0.string("key") }
    for (index, button) in buttons.enumerated() {
      button.isHidden = index >= descriptors.count
      guard index < descriptors.count else { continue }
      let action = descriptors[index]
      button.setTitle(action.string("label"), for: .normal)
      button.isEnabled = !data.bool("disabled") && !action.bool("disabled")
      button.backgroundColor = nativeListColor(theme, "strongBackground", "#F0F0F0")
      button.setTitleColor(
        nativeListColor(
          theme, action.string("tone") == "danger" ? "negative" : "primaryText",
          action.string("tone") == "danger" ? "#CE2C31" : "#202020"), for: .normal)
    }
  }
  @objc private func pressed(_ button: UIButton) {
    guard button.tag < actionKeys.count else { return }
    emitAction(actionKeys[button.tag], from: button, source: "footerAction", slot: button.tag)
  }
  override func recycleContent() {
    visual.recycle()
    actionKeys = []
    buttons.forEach { $0.isHidden = true }
  }
}
