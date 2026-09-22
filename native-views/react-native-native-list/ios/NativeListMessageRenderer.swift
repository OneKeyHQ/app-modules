import UIKit

enum NativeListMessageRenderer {
  enum Update { case unchanged, content, assets, replace }
  struct Resolved {
    let title: NativeListResolvedText
    let body: NativeListResolvedText
    let time: NativeListResolvedText
    let horizontalPadding: CGFloat
    let verticalPadding: CGFloat
    let gap: CGFloat
    let timeInset: CGFloat
    let leadingGap: CGFloat
    let imageWidth: CGFloat
    let imageHeight: CGFloat
    let imageStyle: [String: Any]
    let leading: [String: Any]?
    let thumbnail: [String: Any]?
    let unread: Bool
    let alignment: UIStackView.Alignment

    init(_ item: NativeListItem, theme: [String: Any]?, layout: String) {
      let style = item.data.dictionary("style") ?? [:]
      title = NativeListResolvedText(
        item.data.string("title"), style: style.dictionary("title"), size: 14, weight: .semibold,
        color: nativeListColor(theme, "primaryText", "#202020"), lineHeight: 20, lines: 2)
      body = NativeListResolvedText(
        item.data.string("body"), style: style.dictionary("body"), size: 14,
        color: nativeListColor(theme, "secondaryText", "#646464"), lineHeight: 20,
        lines: item.data.int("bodyLines", default: 3))
      time = NativeListResolvedText(
        item.data.string("time"), style: style.dictionary("time"), size: 12,
        color: nativeListColor(theme, "disabledText", "#8D8D8D"), lineHeight: 16, lines: 1,
        breakMode: .byWordWrapping)
      horizontalPadding = CGFloat(
        style.double("horizontalPadding", default: layout == "table" ? 16 : 12))
      verticalPadding = CGFloat(style.double("verticalPadding", default: 16))
      gap = CGFloat(style.double("lineGap", default: 2))
      timeInset = style["lineGap"] == nil ? 2 : 0
      leadingGap = CGFloat(style.double("leadingGap", default: 12))
      imageStyle = style.dictionary("image") ?? [:]
      imageWidth = CGFloat(imageStyle.double("width", default: 28))
      imageHeight = CGFloat(imageStyle.double("height", default: 28))
      leading = item.data.dictionary("leading")
      thumbnail = item.data.dictionary("thumbnail")
      unread = item.data.bool("unread")
      switch style.dictionary("container")?.string("contentVerticalAlignment") {
      case "center": alignment = .center
      case "bottom": alignment = .bottom
      default: alignment = .top
      }
    }

    func measure(width: CGFloat) -> CGFloat {
      let textWidth = max(
        1,
        width - horizontalPadding * 2 - (leading == nil ? 0 : imageWidth + leadingGap)
          - (thumbnail == nil ? 0 : 76))
      let fields = [title, body, time].filter { !$0.text.isEmpty }
      let textHeight =
        fields.reduce(CGFloat(0)) { $0 + $1.measure(width: textWidth) }
        + CGFloat(max(0, fields.count - 1)) * gap + (time.text.isEmpty ? 0 : timeInset)
      return verticalPadding * 2
        + max(textHeight, leading == nil ? 0 : imageHeight, thumbnail == nil ? 0 : 64)
    }
  }

  final class Views {
    let root = UIStackView()
    let column = UIStackView()
    let title = NativeListTextLabel()
    let body = NativeListTextLabel()
    let time = NativeListInsetLabel()
    private var leading: NativeListLeadingVisual?
    private var thumbnail: NativeListImageSlot?
    private var sizeConstraints: [NSLayoutConstraint] = []

    init() {
      root.axis = .horizontal
      root.spacing = 12
      column.axis = .vertical
      column.alignment = .fill
      column.setContentHuggingPriority(UILayoutPriority(1), for: .horizontal)
      column.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
      title.setContentCompressionResistancePriority(.required, for: .horizontal)
      [title, body, time].forEach(column.addArrangedSubview)
      root.addArrangedSubview(column)
    }

    func bind(_ resolved: Resolved, item: NativeListItem, theme: [String: Any]?) {
      resolved.title.bind(title)
      resolved.body.bind(body)
      resolved.time.bind(time)
      time.topInset = resolved.timeInset
      time.invalidateIntrinsicContentSize()
      column.spacing = resolved.gap
      root.alignment = resolved.alignment
      NSLayoutConstraint.deactivate(sizeConstraints)
      sizeConstraints.removeAll(keepingCapacity: true)
      if let data = resolved.leading {
        let visual = leading ?? NativeListLeadingVisual(frame: .zero)
        leading = visual
        if visual.superview == nil { root.insertArrangedSubview(visual, at: 0) }
        visual.bind(
          data, style: resolved.imageStyle, key: item.key, theme: theme, isUnread: resolved.unread)
        root.setCustomSpacing(resolved.leadingGap, after: visual)
        sizeConstraints += [
          visual.widthAnchor.constraint(equalToConstant: resolved.imageWidth),
          visual.heightAnchor.constraint(equalToConstant: resolved.imageHeight),
        ]
      } else if let leading {
        leading.recycle()
        root.removeArrangedSubview(leading)
        leading.removeFromSuperview()
      }
      if let source = resolved.thumbnail {
        let slot = thumbnail ?? NativeListImageSlot()
        thumbnail = slot
        if slot.view.superview == nil { root.addArrangedSubview(slot.view) }
        slot.view.layer.cornerRadius = 6
        slot.view.layer.borderWidth = 1 / UIScreen.main.scale
        slot.view.layer.borderColor = UIColor(nativeListHex: "#0000000F", fallback: .clear).cgColor
        sizeConstraints += [
          slot.view.widthAnchor.constraint(equalToConstant: 64),
          slot.view.heightAnchor.constraint(equalToConstant: 64),
        ]
        slot.bind(
          source, key: "\(item.key):thumbnail",
          placeholder: theme?.string("strongBackground", default: "#0000000F") ?? "#0000000F")
      } else if let thumbnail {
        thumbnail.recycle()
        root.removeArrangedSubview(thumbnail.view)
        thumbnail.view.removeFromSuperview()
      }
      NSLayoutConstraint.activate(sizeConstraints)
    }
    func recycle() {
      leading?.recycle()
      thumbnail?.recycle()
    }
  }

  static func update(from old: NativeListItem?, to new: NativeListItem) -> Update {
    guard let old, old.key == new.key, old.rendererKey == new.rendererKey else { return .replace }
    if old.content == new.content { return .unchanged }
    for field in ["leading", "thumbnail"] {
      if NativeListImageSlot.signature(old.data.dictionary(field) ?? [:])
        != NativeListImageSlot.signature(new.data.dictionary(field) ?? [:])
      {
        return .assets
      }
    }
    return .content
  }
}

final class NativeListMessageCell: NativeListRowHost {
  private let views = NativeListMessageRenderer.Views()
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
    views.root.translatesAutoresizingMaskIntoConstraints = false
    contentView.addSubview(views.root)
    contentView.addSubview(separator)
    rootConstraints = [
      views.root.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
      views.root.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
      views.root.topAnchor.constraint(equalTo: contentView.topAnchor),
      views.root.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),
    ]
    NSLayoutConstraint.activate(rootConstraints)
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  override func bind(
    item: NativeListItem, theme: [String: Any]?, layout: String, itemIndex: Int? = nil,
    selected: Bool, checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    let update = NativeListMessageRenderer.update(from: self.item, to: item)
    let signature = NativeListImageSlot.signature([
      "theme": theme ?? [:], "layout": layout, "listStyle": listStyle ?? [:],
    ])
    if update != .unchanged || signature != inputSignature {
      if self.item != nil { onBindingInvalidated?(self, bindingEpoch) }
      bindingEpoch &+= 1
      if update == .replace {
        views.recycle()
        isHighlighted = false
      }
      let resolved = NativeListMessageRenderer.Resolved(item, theme: theme, layout: layout)
      views.bind(resolved, item: item, theme: theme)
      rootConstraints[0].constant = resolved.horizontalPadding
      rootConstraints[1].constant = -resolved.horizontalPadding
      rootConstraints[2].constant = resolved.verticalPadding
      rootConstraints[3].constant = -resolved.verticalPadding
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
    let showSelection = selectedState && (layout != "sectioned" || item.data.bool("selected"))
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
        default: grouped ? listStyle?.double("groupCornerRadius", default: 12) ?? 12 : 0))
    let top: CACornerMask = [.layerMinXMinYCorner, .layerMaxXMinYCorner]
    let bottom: CACornerMask = [.layerMinXMaxYCorner, .layerMaxXMaxYCorner]
    layer.maskedCorners =
      container["cornerRadius"] != nil || position == "single"
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
    let inset = CGFloat(listStyle?.dictionary("separator")?.double("inset", default: 12) ?? 12)
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
    views.recycle()
    fullWidthBackground.removeFromSuperlayer()
    isHighlighted = false
  }
}
