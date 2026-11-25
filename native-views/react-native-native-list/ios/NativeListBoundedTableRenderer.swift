import UIKit

private final class TableCellContent: UIView {
  let labels = NativeListTableColumnView()
  let image = NativeListLeadingVisual()
  let button = UIButton(type: .custom)
  var onPress: (() -> Void)?
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
    let width = min(max(0, bounds.width - inset), textSize.width)
    let height = labels.systemLayoutSizeFitting(CGSize(width: width, height: 0),
      withHorizontalFittingPriority: .required, verticalFittingPriority: .fittingSizeLevel).height
    let groupHeight = max(height, image.isHidden ? 0 : visualSize.height)
    let groupY = verticalAlignment == "top" ? 0 : verticalAlignment == "bottom"
      ? bounds.height - groupHeight : (bounds.height - groupHeight) / 2
    let free = max(0, bounds.width - inset - width)
    let rtl = effectiveUserInterfaceLayoutDirection == .rightToLeft
    let start = alignment == "center" ? free / 2 : alignment == "end" ? free : 0
    image.frame = CGRect(x: rtl ? bounds.width - start - visualSize.width : start,
      y: groupY + (groupHeight - visualSize.height) / 2, width: visualSize.width, height: visualSize.height)
    labels.frame = CGRect(x: rtl ? bounds.width - start - inset - width : start + inset,
      y: groupY + (groupHeight - height) / 2, width: width, height: height)
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
      cell.button.isHidden = action == nil
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
    return item.data.dictionaries("tableColumns").flatMap {
      [nativeListDataColumnText($0, "text"), nativeListDataColumnText($0, "secondaryText")]
    }.filter { !$0.isEmpty }.joined(separator: ", ")
  }

  override func recycleContent() {
    for cell in strip.cells {
      cell.onPress = nil
      cell.image.recycle()
      cell.labels.reset()
    }
  }
}
