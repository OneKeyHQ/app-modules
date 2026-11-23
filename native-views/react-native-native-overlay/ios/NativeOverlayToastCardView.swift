import ObjectiveC
import UIKit

// RareUI-inspired concentric geometry: cardRadius = tileRadius (11) + padding (10).
private enum ToastMetrics {
  static let cardPaddingHorizontal: CGFloat = 10
  static let cardPaddingVertical: CGFloat = 10
  static let cardRadius: CGFloat = 21
  static let messageIconGap: CGFloat = 12
  static let messageTextGap: CGFloat = 2
  static let noIconPaddingStart: CGFloat = 6
  static let tileMinHeight: CGFloat = 36
  static let inlineActionHeight: CGFloat = 36
  static let inlineActionRadius: CGFloat = 11
  static let inlineTextMinWidth: CGFloat = 96
  static let inlineActionMaxLabel: Int = 16
  static let inlineActionMaxMessage: Int = 72
  static let notificationGap: CGFloat = 10
  static let notificationTextGap: CGFloat = 2
  static let actionsGap: CGFloat = 8
  static let actionsPaddingTop: CGFloat = 10
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
  private(set) var icon: NativeOverlayToastIcon
  private(set) var statusTile: ToastStatusTileView?

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
    case let .status(status):
      let tile = ToastStatusTileView(status: status)
      tile.frame = bounds
      tile.autoresizingMask = [.flexibleWidth, .flexibleHeight]
      addSubview(tile)
      statusTile = tile
    case let .image(uri, _, radius):
      imageView.layer.cornerRadius = radius
      load(uri)
    case .named:
      break
    }
  }

  func applyStatus(_ status: NativeOverlayToastStatus, settle: Bool) {
    icon = .status(status)
    statusTile?.apply(status, settle: settle)
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
  var onPress: (() -> Void)?
  private var links: [(range: NSRange, link: String)] = []
  private let storage = NSTextStorage()
  private let layoutManager = NSLayoutManager()
  private let container = NSTextContainer(size: .zero)

  init(text: NativeOverlayToastText, runs: [NativeOverlayToastTextRun], pressable: Bool = false) {
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
    if !links.isEmpty {
      accessibilityTraits = .link
    } else if pressable {
      accessibilityTraits = [.staticText, .button]
    } else {
      accessibilityTraits = .staticText
    }
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

  override func accessibilityActivate() -> Bool {
    if let firstLink = links.first?.link, let onLink {
      onLink(firstLink)
      return true
    }
    if let onPress {
      onPress()
      return true
    }
    return super.accessibilityActivate()
  }
}

private final class ToastActionButton: UIControl {
  let action: NativeOverlayToastAction
  var isInline = false
  private let label = UILabel()
  private var iconView: ToastIconView?
  var reduceMotion = false {
    didSet {
      if reduceMotion && !oldValue {
        layer.removeAllAnimations()
        transform = .identity
      }
    }
  }

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
      guard !reduceMotion else { return }
      let scale: CGFloat = isHighlighted ? 0.95 : 1
      UIView.animate(withDuration: 0.12) {
        self.transform = CGAffineTransform(scaleX: scale, y: scale)
      }
    }
  }

  private var labelWidth: CGFloat { pixelCeil(label.intrinsicContentSize.width) }
  private var iconExtent: CGFloat { action.icon.map { $0.size + action.iconGap } ?? 0 }

  /// The button's border box.
  var fittedSize: CGSize {
    let padX = isInline ? max(action.paddingHorizontal, 14) : action.paddingHorizontal
    return CGSize(
      width: labelWidth + iconExtent + 2 * (padX + action.borderWidth),
      height: max(action.lineHeight + textBoxSlack, action.icon?.size ?? 0)
        + 2 * (action.paddingVertical + action.borderWidth)
    )
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    let radius = isInline ? ToastMetrics.inlineActionRadius : action.borderRadius
    layer.cornerRadius = min(radius, bounds.height / 2)
    let padX = isInline ? max(action.paddingHorizontal, 14) : action.paddingHorizontal
    var x = padX + action.borderWidth
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
  var onPress: (() -> Void)?

  private let surface = UIView()
  private let content = UIView()
  private var messageStatusIcon: ToastIconView?
  private var layoutBlocks: [(CGFloat) -> CGFloat] = []
  private var bodyHidden = false
  var canExpandStack = false {
    didSet {
      guard canExpandStack != oldValue else { return }
      updateAccessibilityActions()
    }
  }
  private(set) var naturalSize: CGSize = .zero

  init(spec: NativeOverlayToastSpec, cardColor: UIColor, cardBorderColor: UIColor? = nil) {
    self.spec = spec
    super.init(frame: .zero)
    surface.layer.cornerRadius = ToastMetrics.cardRadius
    surface.layer.cornerCurve = .continuous
    surface.layer.borderWidth = 1
    surface.layer.shadowColor = UIColor.black.cgColor
    surface.layer.shadowOffset = CGSize(width: 0, height: 2)
    surface.layer.shadowOpacity = 0.06
    surface.layer.shadowRadius = 4
    addSubview(surface)
    content.clipsToBounds = true
    surface.addSubview(content)
    apply(spec: spec, cardColor: cardColor, cardBorderColor: cardBorderColor)
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  func apply(
    spec: NativeOverlayToastSpec,
    cardColor: UIColor,
    cardBorderColor: UIColor? = nil,
    settleStatus: Bool = false
  ) {
    let retained: ToastIconView?
    if spec.layout == .message, case let .status(status)? = spec.icon, let messageStatusIcon {
      messageStatusIcon.applyStatus(status, settle: settleStatus)
      retained = messageStatusIcon
    } else {
      cancelStatusMotion()
      retained = nil
    }
    messageStatusIcon = nil
    self.spec = spec
    surface.backgroundColor = cardColor
    surface.layer.borderColor = (cardBorderColor ?? UIColor(white: 0, alpha: 0.06)).cgColor
    accessibilityIdentifier = spec.testID
    content.subviews.forEach { view in
      if view !== retained { view.removeFromSuperview() }
    }
    layoutBlocks = []
    switch spec.layout {
    case .message: buildMessage(statusIcon: retained)
    case .notification: buildNotification()
    }
    if settleStatus && retained != nil {
      let swapped = content.subviews.filter { $0 !== retained }.map { ($0, $0.alpha) }
      swapped.forEach { view, _ in
        view.alpha = 0
        view.transform = CGAffineTransform(translationX: 0, y: 3)
      }
      let animator = UIViewPropertyAnimator(
        duration: 0.16,
        timingParameters: UICubicTimingParameters(
          controlPoint1: CGPoint(x: 0.2, y: 0), controlPoint2: CGPoint(x: 0, y: 1)
        )
      )
      animator.addAnimations {
        swapped.forEach { view, restingAlpha in
          view.alpha = restingAlpha
          view.transform = .identity
        }
      }
      animator.startAnimation()
    }
    updateAccessibilityActions()
  }

  private func updateAccessibilityActions() {
    isAccessibilityElement = canExpandStack
    accessibilityLabel = canExpandStack ? announcement : nil
    accessibilityTraits = canExpandStack ? .button : []
    if canExpandStack || spec.pressable {
      accessibilityCustomActions = [
        UIAccessibilityCustomAction(
          name: canExpandStack ? "Expand notifications" : (announcement.isEmpty ? "Activate" : announcement),
          target: self,
          selector: #selector(handleAccessibilityPressAction)
        ),
      ]
    } else {
      accessibilityCustomActions = nil
    }
  }

  func setBodyHidden(_ hidden: Bool, animated: Bool) {
    guard hidden != bodyHidden else { return }
    bodyHidden = hidden
    accessibilityElementsHidden = hidden
    content.isUserInteractionEnabled = !hidden
    let targetAlpha: CGFloat = hidden ? 0 : 1
    if animated {
      UIView.animate(withDuration: 0.24, delay: 0, options: [.beginFromCurrentState, .curveEaseOut]) {
        self.content.alpha = targetAlpha
      }
    } else {
      content.alpha = targetAlpha
    }
  }

  func applyDisplayHeight(_ height: CGFloat) {
    let clamped = max(height, 0)
    let size = CGSize(width: naturalSize.width, height: clamped)
    bounds.size = size
    surface.frame = CGRect(origin: .zero, size: size)
    surface.layer.shadowPath = UIBezierPath(
      roundedRect: surface.bounds, cornerRadius: ToastMetrics.cardRadius
    ).cgPath
    content.frame = CGRect(
      x: ToastMetrics.cardPaddingHorizontal,
      y: ToastMetrics.cardPaddingVertical,
      width: max(size.width - 2 * ToastMetrics.cardPaddingHorizontal, 0),
      height: max(clamped - 2 * ToastMetrics.cardPaddingVertical, 0)
    )
  }

  func animateMessageStatusEntrance() { messageStatusIcon?.statusTile?.enter() }

  func animateMessageStatusRing() { messageStatusIcon?.statusTile?.startRing() }

  func cancelStatusMotion() { messageStatusIcon?.statusTile?.cancelMotion() }

  func setReducedMotion(_ reduced: Bool) {
    for case let button as ToastActionButton in content.subviews {
      button.reduceMotion = reduced
    }
    if reduced { cancelStatusMotion() }
  }

  @objc private func handleAccessibilityPressAction() -> Bool {
    guard (canExpandStack || spec.pressable), let onPress else { return false }
    onPress()
    return true
  }

  override func accessibilityActivate() -> Bool {
    guard (canExpandStack || spec.pressable), let onPress else { return super.accessibilityActivate() }
    onPress()
    return true
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
      let label = ToastTextLabel(text: text, runs: runs, pressable: spec.pressable)
      label.onLink = { [weak self] link in self?.onLink?(link) }
      if spec.pressable {
        label.onPress = { [weak self] in self?.onPress?() }
      }
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

  private func shouldUseInlineAction() -> Bool {
    guard spec.actions.count == 1, !spec.actionsAlignLeft, let action = spec.actions.first else {
      return false
    }
    if action.label.count > ToastMetrics.inlineActionMaxLabel { return false }
    if action.marginHorizontal != 0 || action.marginVertical != 0 { return false }
    if let message = spec.message {
      if message.paragraphs.count > 1 { return false }
      let len = message.paragraphs.reduce(0) { sum, runs in
        sum + runs.reduce(0) { $0 + $1.text.count }
      }
      if len > ToastMetrics.inlineActionMaxMessage { return false }
    }
    return true
  }

  private func buildMessage(statusIcon: ToastIconView?) {
    let iconView = statusIcon ?? spec.icon.map(ToastIconView.init(icon:))
    if statusIcon == nil { iconView.map(content.addSubview) }
    if iconView?.statusTile != nil { messageStatusIcon = iconView }
    let titleLabels = makeLabels(spec.title)
    let messageLabels = makeLabels(spec.message)
    let buttons = makeButtons()
    let inlineCandidate = shouldUseInlineAction()
    let minTextWidth = titleLabels.isEmpty && messageLabels.isEmpty ? 0 : ToastMetrics.inlineTextMinWidth
    let iconSize = spec.icon?.size ?? 0
    layoutBlocks.append { [weak self] width in
      guard let self else { return 0 }
      let startX = iconView == nil ? ToastMetrics.noIconPaddingStart : iconSize + ToastMetrics.messageIconGap
      var trailingWidth: CGFloat = 0
      var inlineSize = CGSize.zero
      if inlineCandidate, let button = buttons.first {
        button.isInline = true
        let fitted = button.fittedSize
        let requiredWidth = fitted.width + ToastMetrics.messageIconGap
        if width - startX - requiredWidth >= minTextWidth {
          inlineSize = CGSize(width: fitted.width, height: max(fitted.height, ToastMetrics.inlineActionHeight))
          trailingWidth = requiredWidth
        } else {
          button.isInline = false
        }
        button.setNeedsLayout()
      }
      let inlineAction = trailingWidth > 0
      let columnWidth = max(width - startX - trailingWidth, 0)
      var textHeight: CGFloat = 0
      var hasBlock = false
      var frames: [(UIView, CGFloat)] = []
      for labels in [titleLabels, messageLabels] where !labels.isEmpty {
        if hasBlock { textHeight += ToastMetrics.messageTextGap }
        hasBlock = true
        for label in labels {
          let height = label.fittedHeight(width: columnWidth)
          frames.append((label, height))
          textHeight += height
        }
      }
      var bottomActionsHeight: CGFloat = 0
      if !buttons.isEmpty && !inlineAction {
        if hasBlock { textHeight += ToastMetrics.messageTextGap }
        bottomActionsHeight = self.layoutActions(
          buttons, x: startX, y: textHeight, width: columnWidth,
          paddingEnd: ToastMetrics.actionsPaddingEnd,
          paddingBottom: ToastMetrics.actionsPaddingBottom
        )
      }
      let columnTotalHeight = textHeight + bottomActionsHeight
      let rowHeight = max(columnTotalHeight, iconSize, inlineSize.height, ToastMetrics.tileMinHeight)
      let textStartY = bottomActionsHeight > 0 ? 0 : (rowHeight - textHeight) / 2
      var cursorY = textStartY
      var previousGroupWasTitle = false
      for (index, pair) in frames.enumerated() {
        let isMessage = index >= titleLabels.count
        if isMessage && previousGroupWasTitle {
          cursorY += ToastMetrics.messageTextGap
        }
        pair.0.frame = CGRect(x: startX, y: cursorY, width: columnWidth, height: pair.1)
        cursorY += pair.1
        previousGroupWasTitle = !isMessage
      }
      if let iconView {
        iconView.frame = CGRect(x: 0, y: (rowHeight - iconSize) / 2, width: iconSize, height: iconSize)
      }
      if inlineAction, let button = buttons.first {
        button.frame = CGRect(
          x: width - inlineSize.width,
          y: (rowHeight - inlineSize.height) / 2,
          width: inlineSize.width,
          height: inlineSize.height
        )
      }
      return rowHeight
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
  /// out. Returns the card's natural size. Pass `applyBounds: false` when the
  /// caller will animate height via `applyDisplayHeight(_:)`.
  @discardableResult
  func layoutCard(contentWidth: CGFloat, contentMaxHeight: CGFloat?, applyBounds: Bool = true) -> CGSize {
    var height = layoutBlocks.reduce(CGFloat(0)) { $0 + $1(contentWidth) }
    if let contentMaxHeight, contentMaxHeight > 0 {
      height = min(height, contentMaxHeight)
    }
    let size = CGSize(
      width: contentWidth + 2 * ToastMetrics.cardPaddingHorizontal,
      height: height + 2 * ToastMetrics.cardPaddingVertical
    )
    naturalSize = size
    if applyBounds || bounds.size == .zero || bounds.width != size.width {
      applyDisplayHeight(size.height)
    }
    return size
  }
}
