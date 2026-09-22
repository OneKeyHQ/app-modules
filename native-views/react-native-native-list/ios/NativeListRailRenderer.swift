import UIKit

enum NativeListRailRenderer {
  struct Resolved {
    let title: NativeListResolvedText
    let badge: NativeListResolvedText
    let status: NativeListResolvedText
    let horizontalPadding: CGFloat
    let verticalPadding: CGFloat
    let leadingGap: CGFloat
    let badgeGap: CGFloat
    let trailingGap: CGFloat
    let imageWidth: CGFloat
    let imageHeight: CGFloat
    let imageStyle: [String: Any]
    let visual: [String: Any]?
    let alignment: UIStackView.Alignment

    init(_ item: NativeListItem, theme: [String: Any]?) {
      let style = item.data.dictionary("style") ?? [:]
      let badge = item.data.dictionary("badge") ?? [:]
      title = NativeListResolvedText(
        item.data.string("title"), style: style.dictionary("title"),
        size: 12, weight: .medium, color: nativeListColor(theme, "primaryText", "#202020"),
        lineHeight: 16, lines: 1)
      self.badge = NativeListResolvedText(
        badge.string("text"), style: style.dictionary("badge"),
        size: 12, weight: .medium,
        color: nativeListColor(
          theme,
          badge.string("tone") == "success"
            ? "positive" : badge.string("tone") == "danger" ? "negative" : "secondaryText",
          badge.string("tone") == "success"
            ? "#218358" : badge.string("tone") == "danger" ? "#CE2C31" : "#646464"), lineHeight: 16,
        lines: 1, tabular: true)
      let status = item.data.string("status")
      self.status = NativeListResolvedText(
        status == "none" ? "" : status, style: style.dictionary("status"),
        size: 12, color: nativeListColor(theme, "secondaryText", "#646464"), lineHeight: 16,
        lines: 1, tabular: true)
      horizontalPadding = CGFloat(style.double("horizontalPadding", default: 4))
      verticalPadding = CGFloat(style.double("verticalPadding", default: 4))
      leadingGap = CGFloat(style.double("leadingGap", default: 6))
      // The legacy iOS title/badge stack uses 8, independently of the rail's 6 gap.
      badgeGap = CGFloat(style.double("titleBadgeGap", default: 8))
      trailingGap = CGFloat(style.double("trailingGap", default: 6))
      imageStyle = style.dictionary("image") ?? [:]
      imageWidth = CGFloat(imageStyle.double("width", default: 20))
      imageHeight = CGFloat(imageStyle.double("height", default: 20))
      visual = item.data.dictionary("visual")
      switch style.dictionary("container")?.string("contentVerticalAlignment") {
      case "top": alignment = .top
      case "bottom": alignment = .bottom
      default: alignment = .center
      }
    }
    var horizontalWidth: CGFloat {
      func width(_ text: NativeListResolvedText) -> CGFloat {
        (text.text as NSString).size(withAttributes: [.font: text.font]).width
      }
      // Preserve the legacy viewport bounds and trailing allowance. The old
      // estimator budgeted 6 points for the actual 8-point title/badge gap.
      return min(
        288,
        max(
          72,
          ceil(
            horizontalPadding * 2 + imageWidth + leadingGap
              + width(title) + (badge.text.isEmpty ? 0 : badgeGap + width(badge))
              + (status.text.isEmpty ? 0 : trailingGap + width(status))
              + (badge.text.isEmpty ? 8 : 6))))
    }
  }
}

final class NativeListRailCell: NativeListRendererCell {
  private let title = NativeListTextLabel()
  private let badge = NativeListTextLabel()
  private let status = NativeListTextLabel()
  private let titleLine = UIStackView()
  private let textLine = UIStackView()
  private var visual: NativeListLeadingVisual?
  private var dimensions: [NSLayoutConstraint] = []
  override var defaultCornerRadius: CGFloat { 8 }
  override var assetFields: [String] { ["visual"] }
  override var showsSelection: Bool { false }
  override var defaultSeparatorInset: CGFloat { 0 }

  override init(frame: CGRect) {
    super.init(frame: frame)
    root.axis = .horizontal
    titleLine.axis = .horizontal
    textLine.axis = .horizontal
    titleLine.alignment = .center
    textLine.alignment = .center
    [title, badge].forEach(titleLine.addArrangedSubview)
    [titleLine, status].forEach(textLine.addArrangedSubview)
    root.addArrangedSubview(textLine)
    [title, titleLine, textLine].forEach {
      $0.setContentHuggingPriority(.required, for: .horizontal)
      $0.setContentCompressionResistancePriority(.required, for: .horizontal)
    }
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
  override func bindContent(
    _ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    let r = NativeListRailRenderer.Resolved(item, theme: theme)
    r.title.bind(title)
    r.badge.bind(badge)
    r.status.bind(status)
    badge.setContentHuggingPriority(.required, for: .horizontal)
    title.setContentHuggingPriority(r.title.lines > 1 ? .defaultLow : .required, for: .horizontal)
    status.setContentHuggingPriority(r.title.lines > 1 ? .required : .defaultLow, for: .horizontal)
    root.alignment = r.alignment
    root.spacing = r.leadingGap
    titleLine.spacing = r.badgeGap
    textLine.spacing = r.trailingGap
    contentInsets = UIEdgeInsets(
      top: r.verticalPadding, left: r.horizontalPadding,
      bottom: r.verticalPadding, right: r.horizontalPadding)
    NSLayoutConstraint.deactivate(dimensions)
    dimensions.removeAll(keepingCapacity: true)
    if let source = r.visual {
      let view = visual ?? NativeListLeadingVisual(frame: .zero)
      visual = view
      if view.superview == nil { root.insertArrangedSubview(view, at: 0) }
      view.bind(source, style: r.imageStyle, key: item.key, theme: theme, isUnread: false)
      dimensions = [
        view.widthAnchor.constraint(equalToConstant: r.imageWidth),
        view.heightAnchor.constraint(equalToConstant: r.imageHeight),
      ]
      NSLayoutConstraint.activate(dimensions)
    } else if let visual {
      visual.recycle()
      root.removeArrangedSubview(visual)
      visual.removeFromSuperview()
    }
  }
  override func recycleContent() { visual?.recycle() }
}
