import UIKit

final class NativeListActionCell: NativeListRendererCell {
  private let visual = NativeListLeadingVisual(frame: .zero)
  private let title = NativeListTextLabel()
  private let accessories = NativeListAccessoryStack(frame: .zero)
  private var visualWidth: NSLayoutConstraint!
  private var visualHeight: NSLayoutConstraint!
  override var assetFields: [String] { ["icon"] }

  override init(frame: CGRect) {
    super.init(frame: frame)
    visual.glyphSize = 24
    root.axis = .horizontal
    root.alignment = .center
    [visual, title, accessories].forEach(root.addArrangedSubview)
    visual.translatesAutoresizingMaskIntoConstraints = false
    visualWidth = visual.widthAnchor.constraint(equalToConstant: 40)
    visualHeight = visual.heightAnchor.constraint(equalToConstant: 40)
    NSLayoutConstraint.activate([visualWidth, visualHeight])
    title.setContentHuggingPriority(UILayoutPriority(1), for: .horizontal)
    title.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
    accessories.onAction = { [weak self] key, view, slot, target in
      self?.emitAction(key, from: view, source: "trailingAccessory", slot: slot, target: target, anchorInset: self?.accessories.anchorInset(for: view) ?? 0)
    }
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
  override func bindContent(
    _ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    let style = item.data.dictionary("style") ?? [:]
    root.alignment =
      style.dictionary("container")?.string("contentVerticalAlignment") == "top"
      ? .top
      : style.dictionary("container")?.string("contentVerticalAlignment") == "bottom"
        ? .bottom : .center
    let selector = item.data.string("presentation") == "accountSelector"
    let icon = item.data.dictionary("icon")
    let descriptors =
      item.data.dictionary("checkbox").map { [$0] } ?? item.data.dictionaries("trailing")
    accessories.bind(
      item, descriptors: descriptors, theme: theme, style: style, checkboxState: checkboxState)
    let hp = CGFloat(style.double("horizontalPadding", default: layout == "table" ? 16 : 12))
    let vp = CGFloat(style.double("verticalPadding", default: 8))
    let end =
      descriptors.count == 1 && descriptors[0].string("kind") == "chevron"
      ? 6 : accessories.endInset ?? hp
    contentInsets = UIEdgeInsets(
      top: vp, left: hp, bottom: vp, right: style["horizontalPadding"] == nil ? end : hp)
    root.spacing = 12
    root.setCustomSpacing(CGFloat(style.double("leadingGap", default: 12)), after: visual)
    root.setCustomSpacing(accessories.contentGap ?? 12, after: title)
    let tone = item.data.string("tone")
    let color =
      selector
      ? nativeListColor(
        theme, tone == "primary" ? "primaryText" : "secondaryText",
        tone == "primary" ? "#202020" : "#646464")
      : nativeListColor(
        theme, tone == "danger" ? "negative" : "primaryText",
        tone == "danger" ? "#CE2C31" : "#202020")
    NativeListResolvedText(
      item.data.string("title"), style: style.dictionary("title"), size: 16,
      weight: selector && icon == nil ? .regular : .medium, color: color, lineHeight: 24, lines: 1
    ).bind(title)
    visual.isHidden = icon == nil
    if var icon {
      if icon["backgroundColor"] == nil { icon["backgroundColor"] = "#00000000" }
      var image = style.dictionary("image") ?? [:]
      if selector && image["cornerRadius"] == nil && image["shape"] == nil {
        image["cornerRadius"] = 8
      }
      visualWidth.constant = CGFloat(image.double("width", default: selector ? 32 : 40))
      visualHeight.constant = CGFloat(image.double("height", default: selector ? 32 : 40))
      visual.bind(icon, style: image, key: item.key, theme: theme, isUnread: false)
      visual.layer.borderWidth = 0
    } else {
      visual.recycle()
    }
  }
  override func bindSelectionContent(
    _ item: NativeListItem,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    accessories.updateSelection(item, checkboxState: checkboxState)
  }
  override func recycleContent() {
    visual.recycle()
    accessories.reset()
  }
}
