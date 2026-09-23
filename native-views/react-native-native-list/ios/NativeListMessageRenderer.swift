import UIKit

enum NativeListMessageRenderer {
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
    let root: UIStackView
    let column = UIStackView()
    let title = NativeListTextLabel()
    let body = NativeListTextLabel()
    let time = NativeListInsetLabel()
    private var leading: NativeListLeadingVisual?
    private var thumbnail: NativeListImageSlot?
    private var sizeConstraints: [NSLayoutConstraint] = []

    init(root: UIStackView = UIStackView()) {
      self.root = root
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

}

final class NativeListMessageCell: NativeListRendererCell {
  override class func measure(
    _ item: NativeListItem, width: CGFloat, theme: [String: Any]?, layout: String
  ) -> CGFloat? {
    return NativeListMessageRenderer.Resolved(item, theme: theme, layout: layout).measure(
      width: width)
  }

  override var assetFields: [String] { ["leading", "thumbnail"] }
  private lazy var views = NativeListMessageRenderer.Views(root: root)
  override init(frame: CGRect) { super.init(frame: frame) }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
  override func bindContent(
    _ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    let resolved = NativeListMessageRenderer.Resolved(item, theme: theme, layout: layout)
    views.bind(resolved, item: item, theme: theme)
    contentInsets = UIEdgeInsets(
      top: resolved.verticalPadding, left: resolved.horizontalPadding,
      bottom: resolved.verticalPadding, right: resolved.horizontalPadding)
  }
  override func recycleContent() { views.recycle() }
}
