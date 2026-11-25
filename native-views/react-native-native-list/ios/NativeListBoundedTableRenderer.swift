import UIKit

private final class TableActionButton: NativeListIconButton {
  var hitInsets = UIEdgeInsets.zero
  override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
    isEnabled && bounds.inset(by: hitInsets).contains(point)
  }
}

private final class TableCellContent: UIView {
  let labels = NativeListTableColumnView()
  let image = NativeListLeadingVisual()
  let button = UIButton(type: .custom)
  var onPress: (() -> Void)?
  private var iconButtons: [TableActionButton] = []
  private var iconActions: [[String: Any]] = []
  var onIconPress: ((String, UIView) -> Void)?
  var actionsBelow = false
  func bindActions(_ actions: [[String: Any]], disabled: Bool, color: UIColor) {
    iconActions = Array(actions.prefix(2))
    while iconButtons.count > iconActions.count { iconButtons.removeLast().removeFromSuperview() }
    for (index, action) in iconActions.enumerated() {
      if index == iconButtons.count {
        let icon = TableActionButton(type: .custom)
        icon.tag = index
        icon.addTarget(self, action: #selector(pressIcon(_:)), for: .touchUpInside)
        iconButtons.append(icon)
        addSubview(icon)
      }
      let icon = iconButtons[index]
      icon.tintColor = color
      icon.bindIcon(action.string("name"), size: CGSize(width: 16, height: 16))
      icon.isEnabled = !disabled
      icon.accessibilityTraits = disabled ? [.button, .notEnabled] : .button
      icon.accessibilityLabel = action.string("accessibilityLabel")
    }
  }
  @objc private func pressIcon(_ sender: UIButton) {
    guard sender.isEnabled, iconActions.indices.contains(sender.tag) else { return }
    onIconPress?(iconActions[sender.tag].string("actionKey"), sender)
  }
  var columnWidth: CGFloat?
  var visualSize = CGSize.zero
  var gap: CGFloat = 0
  var alignment = "start"
  var verticalAlignment = "center"

  override init(frame: CGRect) {
    super.init(frame: frame)
    clipsToBounds = true
    [image, labels, button].forEach(addSubview)
    button.addTarget(self, action: #selector(press), for: .touchUpInside)
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
  @objc private func press() { onPress?() }

  override func layoutSubviews() {
    super.layoutSubviews()
    let inset = image.isHidden ? 0 : visualSize.width + gap
    let textSize = labels.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize)
    let hasActions = !iconButtons.isEmpty
    let actionsWidth = CGFloat(iconButtons.count * 22 - (hasActions ? 6 : 0))
    let inlineWidth = hasActions && !actionsBelow ? actionsWidth + 6 : 0
    let width = min(max(0, bounds.width - inset - inlineWidth), textSize.width)
    let height = labels.systemLayoutSizeFitting(CGSize(width: width, height: 0),
      withHorizontalFittingPriority: .required, verticalFittingPriority: .fittingSizeLevel).height
    let below = hasActions && actionsBelow
    let labelGroupHeight = below ? height + 22 : max(height, hasActions ? 16 : 0)
    let labelGroupWidth = below ? max(width, actionsWidth) : width + inlineWidth
    let groupHeight = max(labelGroupHeight, image.isHidden ? 0 : visualSize.height)
    let groupY = verticalAlignment == "top" ? 0 : verticalAlignment == "bottom"
      ? bounds.height - groupHeight : (bounds.height - groupHeight) / 2
    let free = max(0, bounds.width - inset - labelGroupWidth)
    let rtl = effectiveUserInterfaceLayoutDirection == .rightToLeft
    let start = alignment == "center" ? free / 2 : alignment == "end" ? free : 0
    func frame(_ offset: CGFloat, _ y: CGFloat, _ width: CGFloat, _ height: CGFloat) -> CGRect {
      CGRect(x: rtl ? bounds.width - start - offset - width : start + offset,
        y: y, width: width, height: height)
    }
    let labelY = groupY + (groupHeight - (below ? labelGroupHeight : height)) / 2
    image.frame = frame(0, groupY + (groupHeight - visualSize.height) / 2, visualSize.width, visualSize.height)
    labels.frame = frame(inset, labelY, width, height)
    for (index, icon) in iconButtons.enumerated() {
      // Split the six-point gap at its midpoint, including in right-to-left layouts.
      let before: CGFloat = index == 0 ? 8 : 3
      let after: CGFloat = index == iconButtons.count - 1 ? 8 : 3
      icon.hitInsets = UIEdgeInsets(top: -8, left: -(rtl ? after : before),
        bottom: -8, right: -(rtl ? before : after))
      icon.frame = frame(inset + (below ? 0 : width + 6) + CGFloat(index * 22),
        below ? labelY + height + 6 : groupY + (groupHeight - 16) / 2, 16, 16)
    }
    button.frame = bounds
  }
}

private final class TableStrip: UIView {
  var cells = [TableCellContent]()
  var columnGap: CGFloat = 8
  override func layoutSubviews() {
    super.layoutSubviews()
    let fixed = cells.compactMap { $0.columnWidth }.reduce(0, +)
    let auto = cells.filter { $0.columnWidth == nil }.count
    let gaps = CGFloat(max(0, cells.count - 1)) * columnGap
    let flexible = max(0, bounds.width - fixed - gaps) / CGFloat(max(1, auto))
    var start: CGFloat = 0
    for cell in cells {
      let width = cell.columnWidth ?? flexible
      cell.frame = CGRect(x: effectiveUserInterfaceLayoutDirection == .rightToLeft
        ? bounds.width - start - width : start, y: 0, width: width, height: bounds.height)
      start += width + columnGap
    }
  }
}

final class NativeListBoundedTableCell: NativeListRendererCell {
  private let strip = TableStrip()
  override class func measure(_ item: NativeListItem, width: CGFloat, theme: [String: Any]?,
    layout: String, fonts: NativeListFontFamilies) -> CGFloat? { 56 }

  override init(frame: CGRect) {
    super.init(frame: frame)
    root.axis = .horizontal
    strip.clipsToBounds = true
    root.addArrangedSubview(strip)
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  override func bindContent(_ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String) {
    var style = item.data.dictionary("style") ?? [:]
    if item.type == "market" {
      style["columns"] = style["price"]
      style["columnSecondary"] = style["subtitle"]
    }
    style["lineGap"] = style["lineGap"] ?? 0
    strip.columnGap = CGFloat(style.double("columnGap", default: 8))
    let hp = CGFloat(style.double("horizontalPadding"))
    let vp = CGFloat(style.double("verticalPadding"))
    contentInsets = UIEdgeInsets(top: vp, left: hp, bottom: vp, right: hp)
    let imageStyle = style.dictionary("image") ?? [:]
    let columns = item.data.dictionaries("tableColumns").prefix(item.type == "market" ? 7 : 6)
    while strip.cells.count > columns.count {
      let cell = strip.cells.removeLast()
      cell.image.recycle()
      cell.removeFromSuperview()
    }
    for (index, column) in columns.enumerated() {
      if index == strip.cells.count {
        let cell = TableCellContent()
        strip.cells.append(cell)
        strip.addSubview(cell)
      }
      let cell = strip.cells[index]
      cell.columnWidth = (column["width"] as? NSNumber).map { CGFloat($0.doubleValue) }
      cell.verticalAlignment = style.dictionary("container")?.string("contentVerticalAlignment") ?? "center"
      cell.alignment = column.string("alignment", default: "start")
      cell.labels.bind(column: column, badges: [], theme: theme, fonts: fonts)
      cell.labels.applyStyle(style) { label, textStyle in
        NativeListTextStyles.applyStyledText(label, textStyle, fonts: fonts)
      }
      cell.labels.applySegments(column)
      if let leading = column.dictionary("leading") {
        cell.visualSize = CGSize(width: imageStyle.double("width", default: 24),
          height: imageStyle.double("height", default: 24))
        cell.gap = CGFloat(style.double("leadingGap", default: 4))
        cell.image.bind(leading, style: imageStyle, key: item.key, theme: theme, isUnread: false, fonts: fonts)
        cell.image.isHidden = false
      } else {
        cell.image.recycle()
        cell.image.isHidden = true
      }
      let action = column["actionKey"] as? String
      cell.actionsBelow = column.string("actionPlacement") == "below"
      cell.bindActions(column.dictionaries("actions"), disabled: item.data.bool("disabled"),
        color: UIColor(nativeListHex: theme?["iconSubdued"] as? String ?? "#8D8D8D", fallback: .gray))
      cell.onIconPress = { [weak self] key, view in
        self?.emitAction(key, from: view, source: "leadingAction", slot: index)
      }
      cell.button.isHidden = action == nil
      cell.button.isEnabled = !item.data.bool("disabled")
      cell.button.accessibilityTraits = cell.button.isEnabled ? .button : [.button, .notEnabled]
      cell.button.accessibilityLabel = column["accessibilityLabel"] as? String
      cell.onPress = { [weak self, weak cell] in
        guard let cell, let action else { return }
        self?.emitAction(action, from: cell.button, source: "leadingAction", slot: index)
      }
      cell.setNeedsLayout()
    }
    strip.setNeedsLayout()
  }

  override func accessibilityText(_ item: NativeListItem) -> String {
    if let label = item.data["accessibilityLabel"] as? String { return label }
    func spokenLine(_ column: [String: Any], _ field: String) -> String {
      let segments = column.dictionaries("\(field)Segments")
      if segments.isEmpty || segments.contains(where: {
        $0.string("style") == "subscript" && !$0.string("text").isEmpty
      }) {
        return column.string(field)
      }
      return segments.map { $0.string("text") }.joined()
    }
    return item.data.dictionaries("tableColumns").flatMap {
      [spokenLine($0, "text"), spokenLine($0, "secondaryText")]
    }.filter { !$0.isEmpty }.joined(separator: ", ")
  }

  override func recycleContent() {
    for cell in strip.cells {
      cell.onIconPress = nil
      cell.onPress = nil
      cell.image.recycle()
      cell.labels.reset()
    }
  }
}
