import UIKit

final class NativeListDataRowCell: NativeListRendererCell {
  private let visual = NativeListLeadingVisual()
  private let favorite = UIImageView()
  private let accessories = NativeListAccessoryStack()
  private let columns = UIStackView()
  private let cells = (0..<4).map { _ in NativeListTableColumnView() }
  private var rowConstraints: [NSLayoutConstraint] = []
  override func unselectedBackground(
    _ item: NativeListItem, theme: [String: Any]?, layout: String, itemIndex: Int?
  ) -> UIColor {
    let index = item.data["index"] == nil ? itemIndex : item.data.int("index")
    return layout == "table" && index.map({ $0 % 2 == 0 }) == true
      ? nativeListColor(theme, "subduedBackground", "#F9F9F9")
      : super.unselectedBackground(item, theme: theme, layout: layout, itemIndex: itemIndex)
  }
  override var assetFields: [String] { ["leading"] }
  override init(frame: CGRect) {
    super.init(frame: frame)
    root.axis = .horizontal
    favorite.contentMode = .scaleAspectFit
    columns.axis = .horizontal
    columns.alignment = .center
    columns.spacing = 8
    cells.forEach(columns.addArrangedSubview)
    [favorite, visual, accessories, columns].forEach(root.addArrangedSubview)
    accessories.onAction = { [weak self] key, view, slot, target in
      self?.emitAction(key, from: view, source: "trailingAccessory", slot: slot, target: target)
    }
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
  override func bindContent(
    _ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    let data = item.data
    let style = data.dictionary("style") ?? [:]
    let image = style.dictionary("image") ?? [:]
    root.spacing = layout == "table" ? 10 : 8
    root.alignment =
      style.dictionary("container")?.string("contentVerticalAlignment") == "top"
      ? .top
      : style.dictionary("container")?.string("contentVerticalAlignment") == "bottom"
        ? .bottom : .center
    let hp = CGFloat(style.double("horizontalPadding", default: layout == "table" ? 20 : 12))
    let vp = CGFloat(style.double("verticalPadding", default: layout == "table" ? 10 : 8))
    contentInsets = UIEdgeInsets(top: vp, left: hp, bottom: vp, right: hp)
    favorite.isHidden = !data.bool("favorite") && !data.bool("favoriteActive")
    favorite.image = nativeListIcon(
      named: data.bool("favoriteActive") ? "StarSolid" : "StarOutline")
    favorite.tintColor = nativeListColor(
      theme, data.bool("favoriteActive") ? "icon" : "iconSubdued",
      data.bool("favoriteActive") ? "#646464" : "#8D8D8D")
    root.setCustomSpacing(layout == "table" ? 8 : root.spacing, after: favorite)
    visual.isHidden = data.dictionary("leading") == nil
    if let leading = data.dictionary("leading") {
      visual.bind(leading, style: image, key: item.key, theme: theme, isUnread: false)
    } else {
      visual.recycle()
    }
    root.setCustomSpacing(
      CGFloat(style.double("leadingGap", default: Double(root.spacing))), after: visual)
    var descriptors: [[String: Any]] = []
    if data["index"] != nil {
      descriptors.append(["kind": "value", "text": String(data.int("index"))])
    }
    if let checkbox = data.dictionary("checkbox") { descriptors.append(checkbox) }
    var accessoryStyle = style
    accessoryStyle["value"] = style.dictionary("index")
    accessories.bind(
      item, descriptors: descriptors, theme: theme, style: accessoryStyle,
      checkboxState: checkboxState)
    accessories.isHidden = descriptors.isEmpty
    NSLayoutConstraint.deactivate(rowConstraints)
    rowConstraints = [
      favorite.widthAnchor.constraint(equalToConstant: 20),
      favorite.heightAnchor.constraint(equalToConstant: 20),
      visual.widthAnchor.constraint(equalToConstant: CGFloat(image.double("width", default: 40))),
      visual.heightAnchor.constraint(equalToConstant: CGFloat(image.double("height", default: 40))),
    ]
    let values = Array(data.dictionaries("columns").prefix(4))
    for (index, cell) in cells.enumerated() {
      cell.isHidden = index >= values.count
      guard index < values.count else {
        cell.reset()
        continue
      }
      cell.bind(
        column: values[index], badges: index == 0 ? data.dictionaries("badges") : [], theme: theme)
      cell.applyStyle(style, text: NativeListTextStyles.applyStyledText)
      if index > 0 {
        rowConstraints.append(
          cell.widthAnchor.constraint(
            equalTo: cells[0].widthAnchor,
            multiplier: CGFloat(max(1, values[index].int("weight", default: 1)))
              / CGFloat(max(1, values[0].int("weight", default: 1)))))
      }
    }
    NSLayoutConstraint.activate(rowConstraints)
  }
  override func bindSelectionContent(
    _ item: NativeListItem,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) { accessories.updateSelection(item, checkboxState: checkboxState) }
  override func recycleContent() {
    visual.recycle()
    accessories.reset()
    cells.forEach { $0.reset() }
  }
}

private final class NativeListTableColumnView: UIStackView {
  private let primaryLine = UIStackView()
  private let primaryLabel = NativeListTextLabel()
  private let badgesStack = UIStackView()
  private let secondaryLine = UIStackView()
  private let secondaryLeadingLabel = NativeListTextLabel()
  private let secondaryLabel = NativeListTextLabel()

  override init(frame: CGRect) {
    super.init(frame: frame)
    axis = .vertical
    alignment = .leading
    distribution = .fill
    spacing = 4

    primaryLine.axis = .horizontal
    primaryLine.alignment = .center
    primaryLine.spacing = 6
    primaryLabel.numberOfLines = 1
    primaryLabel.lineBreakMode = .byTruncatingTail
    primaryLabel.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
    badgesStack.axis = .horizontal
    badgesStack.alignment = .center
    badgesStack.spacing = 4
    badgesStack.setContentCompressionResistancePriority(.required, for: .horizontal)
    primaryLine.addArrangedSubview(primaryLabel)
    primaryLine.addArrangedSubview(badgesStack)

    secondaryLine.axis = .horizontal
    secondaryLine.alignment = .center
    secondaryLine.spacing = 4
    for label in [secondaryLeadingLabel, secondaryLabel] {
      label.numberOfLines = 1
      label.lineBreakMode = .byTruncatingTail
      secondaryLine.addArrangedSubview(label)
    }
    secondaryLeadingLabel.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
    secondaryLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
    secondaryLeadingLabel.widthAnchor.constraint(lessThanOrEqualToConstant: 120).isActive = true
    addArrangedSubview(primaryLine)
    addArrangedSubview(secondaryLine)
  }

  required init(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func reset() {
    for label in [primaryLabel, secondaryLeadingLabel, secondaryLabel] {
      label.rowVerticalAlignment = nil
      label.rowOffsetY = 0
      label.lineBreakMode = .byTruncatingTail
    }
    spacing = 4
    primaryLine.spacing = 6
    primaryLabel.numberOfLines = 1
    secondaryLeadingLabel.numberOfLines = 1
    secondaryLabel.numberOfLines = 1
    primaryLabel.attributedText = nil
    secondaryLeadingLabel.attributedText = nil
    secondaryLabel.attributedText = nil
    secondaryLeadingLabel.isHidden = true
    secondaryLabel.isHidden = true
    secondaryLine.isHidden = true
    badgesStack.arrangedSubviews.forEach {
      badgesStack.removeArrangedSubview($0)
      $0.removeFromSuperview()
    }
  }

  func bind(
    column: [String: Any],
    badges: [[String: Any]],
    theme: [String: Any]?
  ) {
    reset()
    let textAlignment: NSTextAlignment
    switch column.string("alignment") {
    case "end":
      alignment = .trailing
      textAlignment = .right
    case "center":
      alignment = .center
      textAlignment = .center
    default:
      alignment = .leading
      textAlignment = .left
    }
    primaryLabel.textAlignment = textAlignment
    secondaryLeadingLabel.textAlignment = textAlignment
    secondaryLabel.textAlignment = textAlignment
    primaryLabel.font = nativeListFont(ofSize: 14, weight: .medium)
    secondaryLeadingLabel.font = nativeListFont(ofSize: 12)
    secondaryLabel.font = nativeListFont(ofSize: 12)
    primaryLabel.attributedText = line(
      column.string("text"),
      font: nativeListFont(ofSize: 14, weight: .medium),
      color: textColor(column.string("tone"), theme: theme),
      height: 20
    )

    for badge in badges.prefix(2) {
      let label = NativeListInsetLabel()
      label.horizontalInset = 6
      label.text = badge.string("text")
      label.font = nativeListFont(ofSize: 10)
      label.textColor = nativeListColor(theme, "info", "#0D74CE")
      label.textAlignment = .center
      label.backgroundColor = UIColor(nativeListHex: "#008FF519", fallback: .systemBlue)
      label.layer.cornerRadius = 4
      label.clipsToBounds = true
      label.translatesAutoresizingMaskIntoConstraints = false
      label.heightAnchor.constraint(equalToConstant: 16).isActive = true
      badgesStack.addArrangedSubview(label)
    }
    badgesStack.isHidden = badges.isEmpty

    let secondaryLeading = column.string("secondaryLeadingText")
    if !secondaryLeading.isEmpty {
      secondaryLeadingLabel.attributedText = line(
        secondaryLeading,
        font: nativeListFont(ofSize: 12),
        color: nativeListColor(theme, "secondaryText", "#646464"),
        height: 16
      )
      secondaryLeadingLabel.isHidden = false
      secondaryLine.isHidden = false
    }
    let secondary = column.string("secondaryText")
    if !secondary.isEmpty {
      secondaryLabel.attributedText = line(
        secondary,
        font: nativeListFont(ofSize: 12),
        color: textColor(
          column.string("secondaryTone", default: "secondary"),
          theme: theme
        ),
        height: 16
      )
      secondaryLabel.isHidden = false
      secondaryLine.isHidden = false
    }
  }

  func applyStyle(_ style: [String: Any], text: (UILabel, [String: Any]) -> Void) {
    if let primary = style.dictionary("columns") {
      if primary["alignment"] != nil { alignment = .fill }
      text(primaryLabel, primary)
    }
    if let secondary = style.dictionary("columnSecondary") {
      if secondary["alignment"] != nil { alignment = .fill }
      text(secondaryLeadingLabel, secondary)
      text(secondaryLabel, secondary)
    }
    if style["lineGap"] != nil { spacing = CGFloat(style.double("lineGap")) }
    if style["titleBadgeGap"] != nil {
      primaryLine.spacing = CGFloat(style.double("titleBadgeGap"))
    }
  }

  private func line(_ text: String, font: UIFont, color: UIColor, height: CGFloat)
    -> NSAttributedString
  {
    let paragraph = NSMutableParagraphStyle()
    paragraph.minimumLineHeight = height
    paragraph.maximumLineHeight = height
    return NSAttributedString(
      string: text,
      attributes: [
        .font: font,
        .foregroundColor: color,
        .paragraphStyle: paragraph,
      ]
    )
  }

  private func textColor(_ tone: String, theme: [String: Any]?) -> UIColor {
    switch tone {
    // OneKey patch: account warnings and hidden balances use existing theme tokens.
    case "disabled": return nativeListColor(theme, "disabledText", "#8D8D8D")
    case "caution": return nativeListColor(theme, "caution", "#AB6400")
    case "secondary": return nativeListColor(theme, "secondaryText", "#646464")
    case "positive": return nativeListColor(theme, "positive", "#218358")
    case "negative": return nativeListColor(theme, "negative", "#CE2C31")
    default: return nativeListColor(theme, "primaryText", "#202020")
    }
  }
}
