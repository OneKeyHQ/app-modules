import UIKit

// Shared chrome and binding lifetime; the subclass owns its template views.
enum NativeListRendererUpdate { case unchanged, content, assets, replace }

class NativeListRendererCell: NativeListRowHost {
  let root = UIStackView()
  var contentInsets = UIEdgeInsets.zero
  var defaultCornerRadius: CGFloat { 0 }
  var showsSelection: Bool { true }
  var defaultSeparatorInset: CGFloat { 12 }
  func bindContent(
    _ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {}
  func recycleContent() {}
  var assetFields: [String] { [] }
  private func update(from old: NativeListItem?, to new: NativeListItem) -> NativeListRendererUpdate
  {
    guard let old, old.key == new.key, old.rendererKey == new.rendererKey else { return .replace }
    if old.content == new.content { return .unchanged }
    for field in assetFields {
      if NativeListImageSlot.signature(old.data.dictionary(field) ?? [:])
        != NativeListImageSlot.signature(new.data.dictionary(field) ?? [:])
      {
        return .assets
      }
    }
    return .content
  }
  private let separator = UIView()
  private let fullWidthBackground = CALayer()
  private var rootConstraints: [NSLayoutConstraint] = []
  private var item: NativeListItem?
  private var theme: [String: Any]?
  private var layout = "linear"
  private var selectedState = false
  private var inputSignature = ""

  override init(frame: CGRect) {
    super.init(frame: frame)
    root.translatesAutoresizingMaskIntoConstraints = false
    contentView.addSubview(root)
    contentView.addSubview(separator)
    rootConstraints = [
      root.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
      root.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
      root.topAnchor.constraint(equalTo: contentView.topAnchor),
      root.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),
    ]
    NSLayoutConstraint.activate(rootConstraints)
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  override func bind(
    item: NativeListItem, theme: [String: Any]?, layout: String, itemIndex: Int? = nil,
    selected: Bool, checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    let update = update(from: self.item, to: item)
    let signature = NativeListImageSlot.signature([
      "theme": theme ?? [:], "layout": layout, "listStyle": listStyle ?? [:],
    ])
    if update != .unchanged || signature != inputSignature {
      if self.item != nil { onBindingInvalidated?(self, bindingEpoch) }
      bindingEpoch &+= 1
      if update == .replace {
        recycleContent()
        isHighlighted = false
      }
      bindContent(item, theme: theme, layout: layout, checkboxState: checkboxState)
      rootConstraints[0].constant = contentInsets.left
      rootConstraints[1].constant = -contentInsets.right
      rootConstraints[2].constant = contentInsets.top
      rootConstraints[3].constant = -contentInsets.bottom
      inputSignature = signature
    }
    self.item = item
    self.theme = theme
    self.layout = layout
    self.selectedState = selected
    accessibilityLabel = item.data.string("accessibilityLabel", default: item.data.string("title"))
    accessibilityIdentifier = item.data["testID"] as? String
    isUserInteractionEnabled = !item.data.bool("disabled")
    if !isUserInteractionEnabled { isHighlighted = false }
    applyAppearance()
    setNeedsLayout()
  }

  override func updateSelection(
    item: NativeListItem, selected: Bool,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    guard self.item?.key == item.key else { return }
    self.item = item
    self.selectedState = selected
    applyAppearance()
  }
  override var isHighlighted: Bool { didSet { applyAppearance() } }

  private func applyAppearance() {
    guard let item else { return }
    let container = item.data.dictionary("style")?.dictionary("container") ?? [:]
    let showSelection =
      showsSelection && selectedState && (layout != "sectioned" || item.data.bool("selected"))
    var background = nativeListColor(
      theme, showSelection ? "rowSelectedBackground" : "rowBackground",
      showSelection ? "#F0F0F0" : "#FFFFFF")
    if let color = (container["backgroundColor"] ?? item.data["backgroundColor"]) as? String {
      background = UIColor(nativeListHex: color, fallback: background)
    }
    contentView.backgroundColor =
      isHighlighted ? nativeListColor(theme, "rowPressedBackground", "#E8E8E8") : background
    contentView.alpha =
      CGFloat(container.double("opacity", default: item.data.double("opacity", default: 1)))
      * (item.data.bool("disabled") ? 0.5 : 1)
    let position = item.data.string("groupPosition")
    let grouped = ["first", "last", "single"].contains(position)
    let radius = CGFloat(
      container.double(
        "cornerRadius",
        default: defaultCornerRadius > 0
          ? Double(defaultCornerRadius)
          : grouped ? listStyle?.double("groupCornerRadius", default: 12) ?? 12 : 0))
    let top: CACornerMask = [.layerMinXMinYCorner, .layerMaxXMinYCorner]
    let bottom: CACornerMask = [.layerMinXMaxYCorner, .layerMaxXMaxYCorner]
    layer.maskedCorners =
      container["cornerRadius"] != nil || position == "single" || defaultCornerRadius > 0
      ? top.union(bottom) : position == "first" ? top : position == "last" ? bottom : []
    layer.cornerRadius = radius
    layer.masksToBounds = radius > 0
    contentView.layer.cornerRadius = radius
    contentView.layer.maskedCorners = layer.maskedCorners
    contentView.layer.borderWidth = CGFloat(container.double("borderWidth"))
    contentView.layer.borderColor =
      UIColor(
        nativeListHex: container.string("borderColor", default: "#00000000"), fallback: .clear
      ).cgColor
    if item.data.bool("backgroundFullWidth"),
      let fill = (container["backgroundColor"] ?? item.data["backgroundColor"]) as? String
    {
      fullWidthBackground.backgroundColor = UIColor(nativeListHex: fill, fallback: .clear).cgColor
      contentView.layer.insertSublayer(fullWidthBackground, at: 0)
      clipsToBounds = false
    } else {
      fullWidthBackground.removeFromSuperlayer()
    }
    separator.isHidden = !item.data.bool("separator")
    separator.backgroundColor = UIColor(
      nativeListHex: listStyle?.dictionary("separator")?.string(
        "color", default: theme?.string("separator", default: "#E0E0E0") ?? "#E0E0E0") ?? theme?
        .string("separator", default: "#E0E0E0") ?? "#E0E0E0", fallback: .lightGray)
  }
  override func layoutSubviews() {
    super.layoutSubviews()
    fullWidthBackground.frame = CGRect(
      x: -frame.minX, y: 0, width: superview?.bounds.width ?? bounds.width, height: bounds.height)
    let inset = CGFloat(
      listStyle?.dictionary("separator")?.double("inset", default: Double(defaultSeparatorInset))
        ?? Double(defaultSeparatorInset))
    separator.frame = CGRect(
      x: effectiveUserInterfaceLayoutDirection == .rightToLeft ? 0 : inset,
      y: contentView.bounds.height - 1 / UIScreen.main.scale,
      width: max(0, contentView.bounds.width - inset), height: 1 / UIScreen.main.scale)
  }
  override func prepareForReuse() {
    super.prepareForReuse()
    if item != nil { onBindingInvalidated?(self, bindingEpoch) }
    bindingEpoch &+= 1
    item = nil
    inputSignature = ""
    recycleContent()
    fullWidthBackground.removeFromSuperlayer()
    isHighlighted = false
  }
}
