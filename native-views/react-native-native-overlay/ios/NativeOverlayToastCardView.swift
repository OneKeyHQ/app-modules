import ObjectiveC
import UIKit

// The card geometry of the layouts this replaces.
private enum ToastMetrics {
  static let cardPaddingHorizontal: CGFloat = 16
  static let cardPaddingVertical: CGFloat = 12
  static let cardRadius: CGFloat = 8
  static let messageIconGap: CGFloat = 8
  static let messageTextGap: CGFloat = 4
  static let notificationGap: CGFloat = 8
  static let notificationTextGap: CGFloat = 2
  static let actionsGap: CGFloat = 8
  static let actionsPaddingTop: CGFloat = 12
  static let actionsPaddingEnd: CGFloat = 2
  static let actionsPaddingBottom: CGFloat = 2
}

private func pixelCeil(_ value: CGFloat) -> CGFloat {
  let scale = UIScreen.main.scale
  return ceil(value * scale) / scale
}

/// The text engine this replaces measured a text box one pixel taller than
/// its lines; every card height depends on it.
private var textBoxSlack: CGFloat { 1 / UIScreen.main.scale }

/// Named icons come from the image package's process-wide registry. It is
/// reached through the Objective-C runtime so this pod needs no dependency on
/// it; without the registry the icon stays blank.
private enum ToastNamedIcon {
  private typealias Request = @convention(c) (
    AnyClass, Selector, AnyObject, NSString, NSString, Int, Int, Double,
    @escaping @convention(block) (UIImage?) -> Void
  ) -> Void
  private typealias Cancel = @convention(c) (AnyClass, Selector, AnyObject) -> Void

  private static let bridge: AnyClass? =
    NSClassFromString("OneKeyNamedSVGBridge") ?? NSClassFromString("OneKeyImage.OneKeyNamedSVGBridge")
  private static let requestSelector =
    NSSelectorFromString("requestWithOwner:name:color:width:height:scale:complete:")
  private static let cancelSelector = NSSelectorFromString("cancelWithOwner:")

  static func request(
    owner: AnyObject, name: String, color: String, size: CGFloat, scale: CGFloat,
    complete: @escaping (UIImage?) -> Void
  ) {
    guard let bridge, let method = class_getClassMethod(bridge, requestSelector) else {
      complete(nil)
      return
    }
    let pixels = Int(ceil(size * scale))
    let call = unsafeBitCast(method_getImplementation(method), to: Request.self)
    call(bridge, requestSelector, owner, name as NSString, color as NSString,
         pixels, pixels, Double(scale), complete)
  }

  static func cancel(owner: AnyObject) {
    guard let bridge, let method = class_getClassMethod(bridge, cancelSelector) else { return }
    unsafeBitCast(method_getImplementation(method), to: Cancel.self)(bridge, cancelSelector, owner)
  }
}

private final class ToastIconView: UIView {
  private let imageView = UIImageView()
  private var spinner: UIActivityIndicatorView?
  private var task: URLSessionDataTask?
  private var requested = false
  let icon: NativeOverlayToastIcon

  init(icon: NativeOverlayToastIcon) {
    self.icon = icon
    super.init(frame: CGRect(x: 0, y: 0, width: icon.size, height: icon.size))
    isUserInteractionEnabled = false
    imageView.frame = bounds
    imageView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    imageView.contentMode = .scaleAspectFill
    imageView.clipsToBounds = true
    addSubview(imageView)
    switch icon {
    case let .spinner(color, _):
      let indicator = UIActivityIndicatorView(style: .medium)
      indicator.color = color
      indicator.startAnimating()
      indicator.center = CGPoint(x: bounds.midX, y: bounds.midY)
      addSubview(indicator)
      spinner = indicator
    case let .image(uri, _, radius):
      imageView.layer.cornerRadius = radius
      load(uri)
    case .named:
      break
    }
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  deinit {
    task?.cancel()
    ToastNamedIcon.cancel(owner: self)
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    guard !requested, let window, case let .named(name, colorString, color, size) = icon else { return }
    requested = true
    ToastNamedIcon.request(
      owner: self, name: name, color: colorString, size: size, scale: window.screen.scale
    ) { [weak self] image in
      DispatchQueue.main.async {
        // The registry hands back final-colour pixels; a template tint keeps
        // single-colour glyphs crisp on any surface.
        self?.imageView.tintColor = color
        self?.imageView.image = image
      }
    }
  }

  private func load(_ uri: String) {
    guard let url = URL(string: uri) else { return }
    if url.scheme == "data" {
      DispatchQueue.global(qos: .userInitiated).async { [weak self] in
        let image = (try? Data(contentsOf: url)).flatMap(UIImage.init(data:))
        DispatchQueue.main.async { self?.imageView.image = image }
      }
      return
    }
    task = URLSession.shared.dataTask(with: url) { [weak self] data, _, _ in
      let image = data.flatMap(UIImage.init(data:))
      DispatchQueue.main.async { self?.imageView.image = image }
    }
    task?.resume()
  }
}

/// A paragraph of styled runs, laid out and drawn with TextKit the way the
/// text engine this replaces did, so line boxes, glyph positions and
/// truncation match it. Link runs report taps through the `onLink` closure.
final class ToastTextLabel: UIView {
  var onLink: ((String) -> Void)?
  private var links: [(range: NSRange, link: String)] = []
  private let storage = NSTextStorage()
  private let layoutManager = NSLayoutManager()
  private let container = NSTextContainer(size: .zero)

  init(text: NativeOverlayToastText, runs: [NativeOverlayToastTextRun]) {
    super.init(frame: .zero)
    backgroundColor = .clear
    isOpaque = false
    contentMode = .redraw
    let attributed = NSMutableAttributedString()
    let baseFont = NativeOverlayToastFont.make(
      family: text.fontFamily, faces: text.fontFaces, weight: text.fontWeight,
      size: text.fontSize, tabular: text.tabularNumbers
    )
    let paragraph = NSMutableParagraphStyle()
    paragraph.minimumLineHeight = text.lineHeight
    paragraph.maximumLineHeight = text.lineHeight
    for run in runs {
      let font = (run.fontWeight == nil && run.fontSize == nil)
        ? baseFont
        : NativeOverlayToastFont.make(
          family: text.fontFamily, faces: text.fontFaces,
          weight: run.fontWeight ?? text.fontWeight,
          size: run.fontSize ?? text.fontSize, tabular: text.tabularNumbers
        )
      var attributes: [NSAttributedString.Key: Any] = [
        .font: font,
        .foregroundColor: run.color ?? text.color,
        .paragraphStyle: paragraph,
        // Centre the glyphs in the fixed line box.
        .baselineOffset: (text.lineHeight - font.lineHeight) / 2,
        // An explicit letter spacing, zero included, replaces the font's own
        // pair kerning.
        .kern: text.letterSpacing,
      ]
      if run.underline {
        attributes[.underlineStyle] = NSUnderlineStyle.single.rawValue
      }
      let start = attributed.length
      attributed.append(NSAttributedString(string: run.text, attributes: attributes))
      if let link = run.link {
        links.append((NSRange(location: start, length: attributed.length - start), link))
      }
    }
    container.lineFragmentPadding = 0
    container.maximumNumberOfLines = text.numberOfLines
    container.lineBreakMode = text.numberOfLines > 0 ? .byTruncatingTail : .byClipping
    layoutManager.usesFontLeading = false
    layoutManager.addTextContainer(container)
    storage.addLayoutManager(layoutManager)
    storage.setAttributedString(attributed)
    isAccessibilityElement = attributed.length > 0
    accessibilityLabel = attributed.string
    accessibilityTraits = links.isEmpty ? .staticText : .link
    if !links.isEmpty {
      addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTap(_:))))
    } else {
      isUserInteractionEnabled = false
    }
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  var string: String { storage.string }

  private func layout(width: CGFloat) -> CGRect {
    container.size = CGSize(width: width, height: .greatestFiniteMagnitude)
    layoutManager.ensureLayout(for: container)
    return layoutManager.usedRect(for: container)
  }

  func fittedHeight(width: CGFloat) -> CGFloat {
    guard storage.length > 0 else { return 0 }
    return pixelCeil(layout(width: width).height) + textBoxSlack
  }

  override func draw(_ rect: CGRect) {
    guard storage.length > 0 else { return }
    _ = layout(width: bounds.width)
    let glyphs = layoutManager.glyphRange(for: container)
    layoutManager.drawBackground(forGlyphRange: glyphs, at: .zero)
    layoutManager.drawGlyphs(forGlyphRange: glyphs, at: .zero)
  }

  private func link(at point: CGPoint) -> String? {
    guard storage.length > 0 else { return nil }
    _ = layout(width: bounds.width)
    let glyph = layoutManager.glyphIndex(for: point, in: container)
    guard layoutManager.boundingRect(forGlyphRange: NSRange(location: glyph, length: 1), in: container)
      .insetBy(dx: -4, dy: -4).contains(point) else { return nil }
    let index = layoutManager.characterIndexForGlyph(at: glyph)
    return links.first { NSLocationInRange(index, $0.range) }?.link
  }

  override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
    link(at: point) != nil
  }

  @objc private func handleTap(_ gesture: UITapGestureRecognizer) {
    if let hit = link(at: gesture.location(in: self)) {
      onLink?(hit)
    }
  }
}

private final class ToastActionButton: UIControl {
  let action: NativeOverlayToastAction
  private let label = UILabel()
  private var iconView: ToastIconView?

  init(action: NativeOverlayToastAction) {
    self.action = action
    super.init(frame: .zero)
    backgroundColor = action.backgroundColor
    layer.cornerCurve = .continuous
    isEnabled = !action.disabled
    alpha = action.disabled ? action.disabledOpacity : 1
    accessibilityIdentifier = action.testID
    accessibilityLabel = action.label
    accessibilityTraits = .button
    isAccessibilityElement = true
    label.font = NativeOverlayToastFont.make(
      family: action.fontFamily, faces: action.fontFaces, weight: action.fontWeight,
      size: action.fontSize, tabular: false
    )
    label.attributedText = NSAttributedString(
      string: action.label,
      attributes: [.font: label.font as Any, .foregroundColor: action.color, .kern: 0]
    )
    label.isUserInteractionEnabled = false
    addSubview(label)
    if let icon = action.icon {
      let view = ToastIconView(icon: icon)
      addSubview(view)
      iconView = view
    }
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  override var isHighlighted: Bool {
    didSet {
      backgroundColor = isHighlighted
        ? (action.pressedBackgroundColor ?? action.backgroundColor)
        : action.backgroundColor
    }
  }

  private var labelWidth: CGFloat { pixelCeil(label.intrinsicContentSize.width) }
  private var iconExtent: CGFloat { action.icon.map { $0.size + action.iconGap } ?? 0 }

  /// The button's border box.
  var fittedSize: CGSize {
    CGSize(
      width: labelWidth + iconExtent + 2 * (action.paddingHorizontal + action.borderWidth),
      height: max(action.lineHeight + textBoxSlack, action.icon?.size ?? 0)
        + 2 * (action.paddingVertical + action.borderWidth)
    )
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    layer.cornerRadius = min(action.borderRadius, bounds.height / 2)
    var x = action.paddingHorizontal + action.borderWidth
    if let iconView, let icon = action.icon {
      iconView.frame = CGRect(
        x: x, y: (bounds.height - icon.size) / 2, width: icon.size, height: icon.size
      )
      x += icon.size + action.iconGap
    }
    label.frame = CGRect(
      x: x, y: (bounds.height - action.lineHeight - textBoxSlack) / 2, width: labelWidth,
      height: action.lineHeight
    )
  }

  // The tertiary button's negative margins shrink its row; keep the whole
  // pill touchable.
  override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
    bounds.insetBy(dx: -4, dy: -4).contains(point)
  }
}

/// One toast card, drawn with UIKit from a spec.
final class NativeOverlayToastCardView: UIView {
  private(set) var spec: NativeOverlayToastSpec
  var onAction: ((String) -> Void)?
  var onLink: ((String) -> Void)?

  private let surface = UIView()
  private let content = UIView()
  private var layoutBlocks: [(CGFloat) -> CGFloat] = []

  init(spec: NativeOverlayToastSpec, cardColor: UIColor) {
    self.spec = spec
    super.init(frame: .zero)
    surface.layer.cornerRadius = ToastMetrics.cardRadius
    surface.layer.shadowColor = UIColor(red: 0x18 / 255, green: 0x18 / 255, blue: 0x21 / 255, alpha: 1).cgColor
    surface.layer.shadowOffset = CGSize(width: 0, height: 3)
    surface.layer.shadowOpacity = 0.15
    surface.layer.shadowRadius = 4.65
    addSubview(surface)
    content.clipsToBounds = true
    surface.addSubview(content)
    apply(spec: spec, cardColor: cardColor)
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  func apply(spec: NativeOverlayToastSpec, cardColor: UIColor) {
    self.spec = spec
    surface.backgroundColor = cardColor
    accessibilityIdentifier = spec.testID
    content.subviews.forEach { $0.removeFromSuperview() }
    layoutBlocks = []
    switch spec.layout {
    case .message: buildMessage()
    case .notification: buildNotification()
    }
  }

  /// What VoiceOver reads when the card appears.
  var announcement: String {
    [spec.title, spec.message].compactMap { text in
      text?.paragraphs.map { $0.map(\.text).joined() }.joined(separator: "\n")
    }.filter { !$0.isEmpty }.joined(separator: ". ")
  }

  private func makeLabels(_ text: NativeOverlayToastText?) -> [ToastTextLabel] {
    guard let text else { return [] }
    return text.paragraphs.map { runs in
      let label = ToastTextLabel(text: text, runs: runs)
      label.onLink = { [weak self] link in self?.onLink?(link) }
      content.addSubview(label)
      return label
    }
  }

  private func makeButtons() -> [ToastActionButton] {
    spec.actions.map { action in
      let button = ToastActionButton(action: action)
      button.addTarget(self, action: #selector(handleAction(_:)), for: .touchUpInside)
      content.addSubview(button)
      return button
    }
  }

  @objc private func handleAction(_ sender: ToastActionButton) {
    onAction?(sender.action.id)
  }

  /// Lays the action row out at `y` and returns the height it occupies.
  private func layoutActions(
    _ buttons: [ToastActionButton], x: CGFloat, y: CGFloat, width: CGFloat,
    paddingEnd: CGFloat, paddingBottom: CGFloat
  ) -> CGFloat {
    guard !buttons.isEmpty else { return 0 }
    let sizes = buttons.map(\.fittedSize)
    // Each button takes its border box plus its margins in the row.
    let extents = zip(buttons, sizes).map { $1.width + 2 * $0.action.marginHorizontal }
    let rowWidth = extents.reduce(0, +) + ToastMetrics.actionsGap * CGFloat(buttons.count - 1)
    let rowHeight = zip(buttons, sizes).map { $1.height + 2 * $0.action.marginVertical }.max() ?? 0
    var cursor = spec.actionsAlignLeft ? x : x + width - paddingEnd - rowWidth
    let top = y + ToastMetrics.actionsPaddingTop
    for (index, button) in buttons.enumerated() {
      let size = sizes[index]
      button.frame = CGRect(
        x: cursor + button.action.marginHorizontal,
        y: top + button.action.marginVertical,
        width: size.width, height: size.height
      )
      cursor += extents[index] + ToastMetrics.actionsGap
    }
    return ToastMetrics.actionsPaddingTop + rowHeight + paddingBottom
  }

  private func buildMessage() {
    let iconView = spec.icon.map(ToastIconView.init(icon:))
    iconView.map(content.addSubview)
    let titleLabels = makeLabels(spec.title)
    let messageLabels = makeLabels(spec.message)
    let buttons = makeButtons()
    let iconSize = spec.icon?.size ?? 0
    layoutBlocks.append { [weak self] width in
      guard let self else { return 0 }
      iconView?.frame = CGRect(x: 0, y: 0, width: iconSize, height: iconSize)
      let x = iconView == nil ? 0 : iconSize + ToastMetrics.messageIconGap
      let columnWidth = max(width - x, 0)
      var y: CGFloat = 0
      var hasBlock = false
      for labels in [titleLabels, messageLabels] where !labels.isEmpty {
        if hasBlock { y += ToastMetrics.messageTextGap }
        hasBlock = true
        for label in labels {
          let height = label.fittedHeight(width: columnWidth)
          label.frame = CGRect(x: x, y: y, width: columnWidth, height: height)
          y += height
        }
      }
      if !buttons.isEmpty {
        if hasBlock { y += ToastMetrics.messageTextGap }
        y += self.layoutActions(
          buttons, x: x, y: y, width: columnWidth,
          paddingEnd: ToastMetrics.actionsPaddingEnd,
          paddingBottom: ToastMetrics.actionsPaddingBottom
        )
      }
      return max(y, iconSize)
    }
  }

  private func buildNotification() {
    let badge = UIView()
    badge.backgroundColor = spec.badgeColor
    badge.layer.cornerRadius = spec.badgeSize / 2
    badge.isUserInteractionEnabled = false
    content.addSubview(badge)
    let iconView = spec.icon.map(ToastIconView.init(icon:))
    iconView.map(badge.addSubview)
    let titleLabels = makeLabels(spec.title)
    let messageLabels = makeLabels(spec.message)
    let buttons = makeButtons()
    let imageView = spec.image.map(ToastIconView.init(icon:))
    imageView.map(content.addSubview)
    let badgeSize = spec.badgeSize
    let iconSize = spec.icon?.size ?? 0
    let imageSize = spec.image?.size ?? 0
    layoutBlocks.append { [weak self] width in
      guard let self else { return 0 }
      badge.frame = CGRect(x: 0, y: 0, width: badgeSize, height: badgeSize)
      iconView?.frame = CGRect(
        x: (badgeSize - iconSize) / 2, y: (badgeSize - iconSize) / 2,
        width: iconSize, height: iconSize
      )
      let x = badgeSize + ToastMetrics.notificationGap
      let trailing = imageView == nil ? 0 : imageSize + ToastMetrics.notificationGap
      let columnWidth = max(width - x - trailing, 0)
      imageView?.frame = CGRect(x: width - imageSize, y: 0, width: imageSize, height: imageSize)
      var y: CGFloat = 0
      var count = 0
      for label in titleLabels + messageLabels {
        if count > 0 { y += ToastMetrics.notificationTextGap }
        count += 1
        let height = label.fittedHeight(width: columnWidth)
        label.frame = CGRect(x: x, y: y, width: columnWidth, height: height)
        y += height
      }
      if !buttons.isEmpty {
        y += ToastMetrics.notificationTextGap
        y += self.layoutActions(
          buttons, x: x, y: y, width: columnWidth, paddingEnd: 0, paddingBottom: 0
        )
      }
      return max(y, badgeSize, imageSize)
    }
  }

  /// Sizes the card for a content box of `contentWidth` and lays its content
  /// out. Returns the card's size.
  @discardableResult
  func layoutCard(contentWidth: CGFloat, contentMaxHeight: CGFloat?) -> CGSize {
    var height = layoutBlocks.reduce(CGFloat(0)) { $0 + $1(contentWidth) }
    if let contentMaxHeight, contentMaxHeight > 0 {
      height = min(height, contentMaxHeight)
    }
    let size = CGSize(
      width: contentWidth + 2 * ToastMetrics.cardPaddingHorizontal,
      height: height + 2 * ToastMetrics.cardPaddingVertical
    )
    bounds.size = size
    surface.frame = CGRect(origin: .zero, size: size)
    surface.layer.shadowPath = UIBezierPath(
      roundedRect: surface.bounds, cornerRadius: ToastMetrics.cardRadius
    ).cgPath
    content.frame = CGRect(
      x: ToastMetrics.cardPaddingHorizontal, y: ToastMetrics.cardPaddingVertical,
      width: contentWidth, height: height
    )
    return size
  }
}
