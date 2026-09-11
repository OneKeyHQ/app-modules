import Foundation
// OneKey patch: preserve native font faces while enabling tabular number features.
import CoreText
import OneKeyImage
import UIKit

// Market/TokenListSkeleton: source geometry and the native Skeleton's 3s shimmer.
private final class NativeListMarketSkeleton: UIView {
  private let marks = (0..<5).map { _ in UIView() }
  private let gradients = (0..<5).map { _ in CAGradientLayer() }

  init(background: UIColor) {
    super.init(frame: .zero)
    var white: CGFloat = 1
    background.getWhite(&white, alpha: nil)
    let base = UIColor(nativeListHex: white < 0.5 ? "#111111" : "#FAFAFA", fallback: .white)
    let highlight = UIColor(nativeListHex: white < 0.5 ? "#333333" : "#CDCDCD", fallback: .lightGray)
    for (index, mark) in marks.enumerated() {
      mark.backgroundColor = base
      mark.clipsToBounds = true
      mark.layer.cornerRadius = index == 0 ? 16 : 8
      let gradient = gradients[index]
      gradient.cornerRadius = mark.layer.cornerRadius
      gradient.colors = [base.cgColor, highlight.cgColor, base.cgColor]
      gradient.locations = [0, 0.5, 1]
      gradient.startPoint = CGPoint(x: 0, y: 0.5)
      gradient.endPoint = CGPoint(x: 1, y: 0.5)
      mark.layer.addSublayer(gradient)
      addSubview(mark)
    }
  }

  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  override func layoutSubviews() {
    super.layoutSubviews()
    let frames = [
      CGRect(x: 0, y: 0, width: 32, height: 32),
      CGRect(x: 44, y: 0, width: 80, height: 16),
      CGRect(x: 44, y: 20, width: 60, height: 12),
      CGRect(x: bounds.width - 168, y: 7, width: 80, height: 18),
      CGRect(x: bounds.width - 80, y: 7, width: 80, height: 18),
    ]
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    for (index, frame) in frames.enumerated() {
      marks[index].frame = frame
      gradients[index].frame = marks[index].bounds
    }
    CATransaction.commit()
    updateAnimation()
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    updateAnimation()
  }

  private func updateAnimation() {
    for gradient in gradients {
      guard window != nil else { gradient.removeAllAnimations(); continue }
      guard gradient.bounds.width > 0, gradient.animation(forKey: "shimmer") == nil else { continue }
      let animation = CABasicAnimation(keyPath: "transform.translation.x")
      animation.fromValue = -gradient.bounds.width
      animation.toValue = gradient.bounds.width
      animation.duration = 3
      animation.repeatCount = .infinity
      animation.timingFunction = CAMediaTimingFunction(name: .linear)
      gradient.add(animation, forKey: "shimmer")
    }
  }
}

final class NativeListActionOrigin {
  weak var sourceView: UIView?
  weak var ownerCell: NativeListCell?
  let bindingEpoch: Int
  let source: String
  let slot: Int?
  // OneKey patch: expose the layout slot while preserving the larger hit target.
  let anchorInset: CGFloat
  var windowPoint: CGPoint?

  init(
    sourceView: UIView,
    ownerCell: NativeListCell,
    bindingEpoch: Int,
    source: String,
    slot: Int? = nil,
    anchorInset: CGFloat = 0
  ) {
    self.sourceView = sourceView
    self.ownerCell = ownerCell
    self.bindingEpoch = bindingEpoch
    self.source = source
    self.slot = slot
    self.anchorInset = anchorInset
  }
}

// OneKey patch: explicit summary actions use the source text's physical-pixel line box.
private final class NativeListAccessoryButton: UIButton {
  var selectorSummaryLineHeight: CGFloat? {
    didSet { invalidateIntrinsicContentSize(); setNeedsLayout() }
  }

  var marketLineHeight: CGFloat? {
    didSet { invalidateIntrinsicContentSize(); setNeedsLayout() }
  }

  private var sourcePixelScale: CGFloat {
    max(1, window?.screen.scale ?? traitCollection.displayScale)
  }

  override var intrinsicContentSize: CGSize {
    var size = super.intrinsicContentSize
    guard selectorSummaryLineHeight != nil || marketLineHeight != nil, let title = attributedTitle(for: .normal) else { return size }
    let width = title.boundingRect(
      with: CGSize(width: CGFloat.greatestFiniteMagnitude, height: CGFloat.greatestFiniteMagnitude),
      options: [.usesLineFragmentOrigin, .usesFontLeading],
      context: nil
    ).width
    size.width = ceil(width * sourcePixelScale) / sourcePixelScale
    return size
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    if let lineHeight = marketLineHeight, let titleLabel, let title = attributedTitle(for: .normal) {
      let measured = title.boundingRect(
        with: CGSize(width: CGFloat.greatestFiniteMagnitude, height: CGFloat.greatestFiniteMagnitude),
        options: [.usesLineFragmentOrigin, .usesFontLeading], context: nil
      ).width
      let width = min(bounds.width, ceil(measured * sourcePixelScale) / sourcePixelScale)
      let x = contentHorizontalAlignment == .trailing ? bounds.width - width
        : contentHorizontalAlignment == .leading ? 0 : (bounds.width - width) / 2
      titleLabel.frame = CGRect(
        x: floor(x * sourcePixelScale) / sourcePixelScale,
        y: (bounds.height - lineHeight) / 2,
        width: width, height: lineHeight
      )
      return
    }
    guard let lineHeight = selectorSummaryLineHeight, let titleLabel else { return }
    // OneKey patch: position the final source line box after UIKit has measured the button.
    let top = ceil((bounds.height - lineHeight) / 2 * sourcePixelScale) / sourcePixelScale
    var frame = titleLabel.frame
    frame.origin.y = top
    titleLabel.frame = frame
  }
}

private final class NativeListInsetLabel: UILabel {
  var horizontalInset: CGFloat = 0
  var topInset: CGFloat = 0
  var bottomInset: CGFloat = 0

  override var intrinsicContentSize: CGSize {
    let size = super.intrinsicContentSize
    return CGSize(
      width: size.width + horizontalInset * 2,
      height: size.height + topInset + bottomInset
    )
  }

  override func drawText(in rect: CGRect) {
    super.drawText(in: rect.inset(
      by: UIEdgeInsets(
        top: topInset,
        left: horizontalInset,
        bottom: bottomInset,
        right: horizontalInset
      )
    ))
  }
}

private final class NativeListDottedUnderlineLabel: UILabel {
  var showsDottedUnderline = false {
    didSet { setNeedsLayout() }
  }

  // OneKey patch: migrated section titles include the source 3-point underline box.
  var reservesDottedUnderlineSpace = false {
    didSet { invalidateIntrinsicContentSize(); setNeedsLayout(); setNeedsDisplay() }
  }

  override var intrinsicContentSize: CGSize {
    var size = super.intrinsicContentSize
    if reservesDottedUnderlineSpace && showsDottedUnderline { size.height += 3 }
    return size
  }

  override func drawText(in rect: CGRect) {
    let textRect = reservesDottedUnderlineSpace && showsDottedUnderline
      ? CGRect(x: rect.minX, y: rect.minY, width: rect.width, height: max(0, rect.height - 3))
      : rect
    super.drawText(in: textRect)
  }

  var dottedUnderlineColor: UIColor = .clear {
    didSet {
      dottedUnderlineLayer.strokeColor = dottedUnderlineColor.cgColor
      setNeedsLayout()
    }
  }

  var dottedUnderlineVerticalOffset: CGFloat = 0 {
    didSet { setNeedsLayout() }
  }

  private let dottedUnderlineLayer = CAShapeLayer()

  override init(frame: CGRect) {
    super.init(frame: frame)
    dottedUnderlineLayer.fillColor = UIColor.clear.cgColor
    dottedUnderlineLayer.lineWidth = 1.5
    dottedUnderlineLayer.lineCap = .round
    dottedUnderlineLayer.lineDashPattern = [0, 4]
    layer.addSublayer(dottedUnderlineLayer)
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    defer { CATransaction.commit() }
    guard showsDottedUnderline, let attributedText else {
      dottedUnderlineLayer.path = nil
      dottedUnderlineLayer.isHidden = true
      return
    }
    let textWidth = ceil(attributedText.boundingRect(
      with: CGSize(width: CGFloat.greatestFiniteMagnitude, height: 24),
      options: [.usesLineFragmentOrigin, .usesFontLeading],
      context: nil
    ).width)
    dottedUnderlineLayer.frame = CGRect(
      x: 0,
      y: 0,
      width: bounds.width,
      height: bounds.height + 2 + dottedUnderlineVerticalOffset
    )
    // OneKey patch: explicit header underline occupies the reserved final two points.
    // let y = bounds.height + 1 + dottedUnderlineVerticalOffset
    let y = reservesDottedUnderlineSpace ? bounds.height - 1 : bounds.height + 1 + dottedUnderlineVerticalOffset
    let path = UIBezierPath()
    path.move(to: CGPoint(x: 1, y: y))
    path.addLine(to: CGPoint(x: max(1, textWidth - 1), y: y))
    dottedUnderlineLayer.path = path.cgPath
    dottedUnderlineLayer.isHidden = false
  }
}

private final class NativeListTableColumnView: UIStackView {
  private let primaryLine = UIStackView()
  private let primaryLabel = UILabel()
  private let badgesStack = UIStackView()
  private let secondaryLine = UIStackView()
  private let secondaryLeadingLabel = UILabel()
  private let secondaryLabel = UILabel()

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

  private func line(_ text: String, font: UIFont, color: UIColor, height: CGFloat) -> NSAttributedString {
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

final class NativeListCell: UICollectionViewCell {
  static let reuseIdentifier = "NativeListCell"

  private let rootStack = UIStackView()
  private let leadingImages = (0..<3).map { _ in OneKeyImageReusableView(frame: .zero) }
  private let leadingOverlayBackground = UIView()
  private let leadingCornerIconBackground = UIView()
  private let leadingCornerIconImageView = UIImageView()
  // OneKey patch: selector-only views are reset on every native cell binding.
  private var selectorViews: [UIView] = []
  private var selectorConstraints: [NSLayoutConstraint] = []
  private var selectorImages: [OneKeyImageReusableView] = []
  private var selectorBorder: CAShapeLayer?
  private let selectorFullWidthBackground = CALayer()
  private lazy var selectorTitleTap = UITapGestureRecognizer(target: self, action: #selector(selectorTitlePressed))
  private let secondaryImage = OneKeyImageReusableView(frame: .zero)
  private let mediaNetworkImage = OneKeyImageReusableView(frame: .zero)
  private let fallbackLabel = UILabel()
  private let leadingIconImageView = UIImageView()
  private let favoriteIconImageView = UIImageView()
  private let headerTitleIconImageView = UIImageView()
  private let headerValueIconImageView = UIImageView()
  private let leadingActionButton = UIButton(type: .system)
  private let leadingContainer = UIView()
  private let unreadDot = UIView()
  private let mainStack = UIStackView()
  private let mediaMetadataStack = UIStackView()
  private let titleRowStack = UIStackView()
  private let titleLabel = NativeListDottedUnderlineLabel()
  private let subtitleLabel = UILabel()
  // OneKey patch: keep Market name and volume in independent line boxes.
  private let marketSubtitleStack = UIStackView()
  private let marketSubtitleSpacer = UIView()
  private let tertiaryLabel = UILabel()
  private let statusLabel = NativeListInsetLabel()
  private let metricSubtitleLabel = UILabel()
  private let metricCompositeStack = UIStackView()
  private let badgeLabel = NativeListInsetLabel()
  // OneKey patch: reuse the existing explicit line-box layout for styled Market badges.
  // private let marketBadgeButtons = (0..<3).map { _ in UIButton(type: .system) }
  private let marketBadgeButtons = (0..<3).map { _ in NativeListAccessoryButton(type: .system) }
  private let marketBadgeImages = (0..<3).map { _ in OneKeyImageReusableView(frame: .zero) }
  private let actionStack = UIStackView()
  private let actionButtons = (0..<3).map { _ in UIButton(type: .system) }
  private let trailingStack = UIStackView()
  // OneKey patch: summary actions opt into source typography while other buttons keep UIKit layout.
  // private let accessoryButtons = (0..<2).map { _ in UIButton(type: .system) }
  private let accessoryButtons = (0..<2).map { _ in NativeListAccessoryButton(type: .system) }
  private let checkboxButton = UIButton(type: .system)
  private let spinner = UIActivityIndicatorView(style: .medium)
  private let dataStack = UIStackView()
  private let dataLabels = (0..<4).map { _ in UILabel() }
  private let tableDataStack = UIStackView()
  private let tableDataColumns = (0..<4).map { _ in NativeListTableColumnView() }
  private let mediaBadgeLabel = UILabel()
  private let skeletonPrimary = UIView()
  private let skeletonSecondary = UIView()
  private var walletGroupCells: [NativeListCell] = []
  private var walletGroupMembers: [NativeListItem] = []
  private let walletGroupCompactContainer = UIView()
  private var walletGroupCompactCell: NativeListCell?
  private let walletGroupDragBadge = NativeListInsetLabel()
  private var walletGroupCompactAppearanceActive = false
  private lazy var walletGroupTap = UITapGestureRecognizer(
    target: self,
    action: #selector(walletGroupPressed(_:))
  )
  private let separatorView = UIView()
  private var separatorLeadingConstraint: NSLayoutConstraint!
  private var leadingWidth: NSLayoutConstraint!
  private var leadingHeight: NSLayoutConstraint!
  private var leadingIconWidth: NSLayoutConstraint!
  private var leadingIconHeight: NSLayoutConstraint!
  private var mediaHeight: NSLayoutConstraint!
  private var secondaryWidth: NSLayoutConstraint!
  private var secondaryHeight: NSLayoutConstraint!
  private var rootLeadingConstraint: NSLayoutConstraint!
  private var rootTrailingConstraint: NSLayoutConstraint!
  private var rootTopConstraint: NSLayoutConstraint!
  private var rootBottomConstraint: NSLayoutConstraint!
  private var leadingSlotConstraints: [NSLayoutConstraint] = []
  private var dataWeightConstraints: [NSLayoutConstraint] = []
  private var accessorySizeConstraints: [NSLayoutConstraint] = []
  // OneKey patch: restore selector-only font features before a cell is reused.
  private var selectorTypographyRestorers: [() -> Void] = []
  private var currentItem: NativeListItem?
  private var accessoryActions: [(String, NativeSelectionTarget?)] = []
  private var footerActionKeys: [String] = []
  private var checkboxAction: (String, NativeSelectionTarget?)?
  private var boundCheckboxData: [String: Any]?
  private var boundCheckboxTarget: NativeSelectionTarget?
  private var leadingActionKey: String?
  private var marketBadgeActionKeys: [String?] = []
  private var restingBackgroundColor: UIColor = .clear
  private var pressedBackgroundColor = UIColor(
    nativeListHex: "#E8E8E8",
    fallback: .lightGray
  )
  private var checkboxCheckedColor = UIColor(nativeListHex: "#202020", fallback: .black)
  private var checkboxIconColor = UIColor.white
  private var checkboxUncheckedColor = UIColor(nativeListHex: "#FCFCFC", fallback: .white)
  private var checkboxBorderColor = UIColor(nativeListHex: "#CECECE", fallback: .lightGray)
  private var visualBackdropColor = UIColor.white
  private var currentLayout = "linear"
  private var currentTheme: [String: Any]?
  private var currentItemIndex: Int?
  // OneKey patch: delayed image retries belong to the current reusable cell binding.
  private var selectorImageRetries: [ObjectIdentifier: DispatchWorkItem] = [:]
  private(set) var bindingEpoch = 0

  var onAction: ((NativeListItem, String, NativeSelectionTarget?, NativeListActionOrigin?) -> Void)?
  var onBindingInvalidated: ((NativeListCell, Int) -> Void)?

  override var isHighlighted: Bool {
    didSet { updateBackgroundColor() }
  }

  override init(frame: CGRect) {
    super.init(frame: frame)
    contentView.addSubview(rootStack)
    contentView.addSubview(separatorView)
    contentView.addSubview(walletGroupCompactContainer)
    rootStack.translatesAutoresizingMaskIntoConstraints = false
    separatorView.translatesAutoresizingMaskIntoConstraints = false
    walletGroupCompactContainer.translatesAutoresizingMaskIntoConstraints = false
    rootLeadingConstraint = rootStack.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 12)
    rootTrailingConstraint = rootStack.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -12)
    rootTopConstraint = rootStack.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 8)
    rootBottomConstraint = rootStack.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -8)
    leadingIconWidth = leadingIconImageView.widthAnchor.constraint(equalToConstant: 18)
    leadingIconHeight = leadingIconImageView.heightAnchor.constraint(equalToConstant: 18)
    separatorLeadingConstraint = separatorView.leadingAnchor.constraint(
      equalTo: contentView.leadingAnchor
    )
    NSLayoutConstraint.activate([
      rootLeadingConstraint,
      rootTrailingConstraint,
      rootTopConstraint,
      rootBottomConstraint,
      separatorLeadingConstraint,
      separatorView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
      separatorView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),
      separatorView.heightAnchor.constraint(equalToConstant: 1 / UIScreen.main.scale),
      walletGroupCompactContainer.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
      walletGroupCompactContainer.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
      walletGroupCompactContainer.topAnchor.constraint(equalTo: contentView.topAnchor),
      walletGroupCompactContainer.heightAnchor.constraint(equalToConstant: 68),
    ])
    walletGroupCompactContainer.isHidden = true
    walletGroupCompactContainer.isUserInteractionEnabled = false
    favoriteIconImageView.translatesAutoresizingMaskIntoConstraints = false
    favoriteIconImageView.contentMode = .scaleAspectFit
    favoriteIconImageView.image = nativeListIcon(named: "StarOutline")
    headerTitleIconImageView.translatesAutoresizingMaskIntoConstraints = false
    headerTitleIconImageView.contentMode = .scaleAspectFit
    headerValueIconImageView.translatesAutoresizingMaskIntoConstraints = false
    headerValueIconImageView.contentMode = .scaleAspectFit
    leadingActionButton.translatesAutoresizingMaskIntoConstraints = false
    leadingActionButton.adjustsImageWhenDisabled = false
    leadingActionButton.tintAdjustmentMode = .normal
    leadingActionButton.addTarget(self, action: #selector(leadingActionPressed), for: .touchUpInside)
    NSLayoutConstraint.activate([
      favoriteIconImageView.widthAnchor.constraint(equalToConstant: 20),
      favoriteIconImageView.heightAnchor.constraint(equalToConstant: 20),
      headerTitleIconImageView.widthAnchor.constraint(equalToConstant: 12),
      headerTitleIconImageView.heightAnchor.constraint(equalToConstant: 12),
      headerValueIconImageView.widthAnchor.constraint(equalToConstant: 12),
      headerValueIconImageView.heightAnchor.constraint(equalToConstant: 12),
      leadingActionButton.widthAnchor.constraint(equalToConstant: 36),
      leadingActionButton.heightAnchor.constraint(equalToConstant: 36),
    ])
    separatorView.isHidden = true
    walletGroupTap.isEnabled = false
    addGestureRecognizer(walletGroupTap)
    rootStack.axis = .horizontal
    rootStack.alignment = .center
    rootStack.spacing = 12
    rootStack.setCustomSpacing(12, after: leadingActionButton)
    rootStack.distribution = .fill
    rootLeadingConstraint.constant = 12
    rootTrailingConstraint.constant = -12
    rootTopConstraint.constant = 8
    rootBottomConstraint.constant = -8
    mainStack.alignment = .fill

    leadingContainer.addSubview(fallbackLabel)
    fallbackLabel.translatesAutoresizingMaskIntoConstraints = false
    leadingContainer.addSubview(leadingIconImageView)
    leadingIconImageView.translatesAutoresizingMaskIntoConstraints = false
    leadingImages.enumerated().forEach { index, image in
      image.translatesAutoresizingMaskIntoConstraints = false
      if index == 1 {
        leadingOverlayBackground.translatesAutoresizingMaskIntoConstraints = false
        leadingContainer.addSubview(leadingOverlayBackground)
      }
      leadingContainer.addSubview(image)
    }
    leadingCornerIconBackground.translatesAutoresizingMaskIntoConstraints = false
    leadingCornerIconImageView.translatesAutoresizingMaskIntoConstraints = false
    leadingCornerIconBackground.addSubview(leadingCornerIconImageView)
    leadingContainer.addSubview(leadingCornerIconBackground)
    leadingCornerIconBackground.layer.cornerRadius = 10
    leadingCornerIconBackground.isHidden = true
    NSLayoutConstraint.activate([
      leadingCornerIconBackground.widthAnchor.constraint(equalToConstant: 20),
      leadingCornerIconBackground.heightAnchor.constraint(equalToConstant: 20),
      leadingCornerIconBackground.trailingAnchor.constraint(
        equalTo: leadingContainer.trailingAnchor,
        constant: 4
      ),
      leadingCornerIconBackground.bottomAnchor.constraint(
        equalTo: leadingContainer.bottomAnchor,
        constant: 4
      ),
      leadingCornerIconImageView.widthAnchor.constraint(equalToConstant: 18),
      leadingCornerIconImageView.heightAnchor.constraint(equalToConstant: 18),
      leadingCornerIconImageView.centerXAnchor.constraint(
        equalTo: leadingCornerIconBackground.centerXAnchor
      ),
      leadingCornerIconImageView.centerYAnchor.constraint(
        equalTo: leadingCornerIconBackground.centerYAnchor
      ),
    ])
    leadingOverlayBackground.layer.cornerRadius = 10
    leadingOverlayBackground.isHidden = true
    leadingCornerIconBackground.isHidden = true
    leadingCornerIconImageView.image = nil
    NSLayoutConstraint.activate([
      fallbackLabel.leadingAnchor.constraint(equalTo: leadingContainer.leadingAnchor),
      fallbackLabel.trailingAnchor.constraint(equalTo: leadingContainer.trailingAnchor),
      fallbackLabel.topAnchor.constraint(equalTo: leadingContainer.topAnchor),
      fallbackLabel.bottomAnchor.constraint(equalTo: leadingContainer.bottomAnchor),
      leadingIconWidth,
      leadingIconHeight,
      leadingIconImageView.centerXAnchor.constraint(equalTo: leadingContainer.centerXAnchor),
      leadingIconImageView.centerYAnchor.constraint(equalTo: leadingContainer.centerYAnchor),
    ])
    leadingWidth = leadingContainer.widthAnchor.constraint(equalToConstant: 40)
    leadingHeight = leadingContainer.heightAnchor.constraint(equalToConstant: 40)
    mediaHeight = leadingContainer.heightAnchor.constraint(equalToConstant: 120)
    leadingWidth.isActive = true
    leadingHeight.isActive = true
    fallbackLabel.textAlignment = .center
    fallbackLabel.font = nativeListFont(ofSize: 13, weight: .bold)
    fallbackLabel.textColor = UIColor(nativeListHex: "#8D8D8D", fallback: .gray)
    secondaryImage.clipsToBounds = true
    secondaryImage.layer.cornerRadius = 6
    secondaryImage.translatesAutoresizingMaskIntoConstraints = false
    secondaryWidth = secondaryImage.widthAnchor.constraint(equalToConstant: 56)
    secondaryHeight = secondaryImage.heightAnchor.constraint(equalToConstant: 56)
    mediaNetworkImage.translatesAutoresizingMaskIntoConstraints = false
    NSLayoutConstraint.activate([
      mediaNetworkImage.widthAnchor.constraint(equalToConstant: 14),
      mediaNetworkImage.heightAnchor.constraint(equalToConstant: 14),
    ])
    mediaNetworkImage.layer.cornerRadius = 7
    mediaNetworkImage.clipsToBounds = true

    leadingContainer.addSubview(unreadDot)
    unreadDot.translatesAutoresizingMaskIntoConstraints = false
    unreadDot.layer.cornerRadius = 4
    NSLayoutConstraint.activate([
      unreadDot.widthAnchor.constraint(equalToConstant: 8),
      unreadDot.heightAnchor.constraint(equalToConstant: 8),
      unreadDot.topAnchor.constraint(equalTo: leadingContainer.topAnchor),
      unreadDot.trailingAnchor.constraint(equalTo: leadingContainer.trailingAnchor),
    ])

    leadingContainer.addSubview(mediaBadgeLabel)
    mediaBadgeLabel.translatesAutoresizingMaskIntoConstraints = false
    mediaBadgeLabel.font = nativeListFont(ofSize: 14, weight: .medium)
    mediaBadgeLabel.textAlignment = .center
    mediaBadgeLabel.layer.cornerRadius = 10
    mediaBadgeLabel.layer.borderWidth = 2
    mediaBadgeLabel.clipsToBounds = true
    NSLayoutConstraint.activate([
      mediaBadgeLabel.trailingAnchor.constraint(equalTo: leadingContainer.trailingAnchor),
      mediaBadgeLabel.bottomAnchor.constraint(equalTo: leadingContainer.bottomAnchor),
      mediaBadgeLabel.heightAnchor.constraint(equalToConstant: 24),
    ])

    mainStack.axis = .vertical
    mainStack.alignment = .fill
    mainStack.spacing = 2
    mainStack.setContentHuggingPriority(UILayoutPriority(1), for: .horizontal)
    mainStack.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    titleRowStack.axis = .horizontal
    titleRowStack.alignment = .center
    titleRowStack.spacing = 8
    titleRowStack.setContentHuggingPriority(.defaultLow, for: .horizontal)
    titleRowStack.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    titleRowStack.addArrangedSubview(titleLabel)
    titleRowStack.addArrangedSubview(badgeLabel)
    marketBadgeButtons.enumerated().forEach { index, button in
      button.tag = index
      button.addTarget(self, action: #selector(marketBadgePressed(_:)), for: .touchUpInside)
      button.titleLabel?.font = nativeListFont(ofSize: 11, weight: .medium)
      button.layer.cornerRadius = 4
      button.clipsToBounds = true
      let image = marketBadgeImages[index]
      image.isUserInteractionEnabled = false
      image.translatesAutoresizingMaskIntoConstraints = false
      button.addSubview(image)
      NSLayoutConstraint.activate([
        image.centerYAnchor.constraint(equalTo: button.centerYAnchor),
        image.widthAnchor.constraint(equalToConstant: 14),
        image.heightAnchor.constraint(equalToConstant: 14),
      ])
      titleRowStack.addArrangedSubview(button)
    }
    titleLabel.setContentHuggingPriority(.defaultLow, for: .horizontal)
    titleLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
    badgeLabel.setContentHuggingPriority(.required, for: .horizontal)
    badgeLabel.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    titleLabel.font = nativeListFont(ofSize: 16, weight: .medium)
    subtitleLabel.font = nativeListFont(ofSize: 14)
    tertiaryLabel.font = nativeListFont(ofSize: 14)
    statusLabel.font = nativeListFont(ofSize: 12)
    metricSubtitleLabel.font = nativeListFont(ofSize: 12)
    metricCompositeStack.axis = .vertical
    metricCompositeStack.alignment = .fill
    metricCompositeStack.spacing = 12
    badgeLabel.font = nativeListFont(ofSize: 12, weight: .medium)
    [titleRowStack, subtitleLabel, tertiaryLabel, statusLabel, metricSubtitleLabel]
      .forEach(mainStack.addArrangedSubview)

    actionStack.axis = .horizontal
    actionStack.alignment = .center
    actionStack.spacing = 8
    actionButtons.enumerated().forEach { index, button in
      button.titleLabel?.font = nativeListFont(ofSize: 12, weight: .medium)
      button.layer.cornerRadius = 8
      button.contentEdgeInsets = UIEdgeInsets(top: 4, left: 8, bottom: 4, right: 8)
      button.tag = index
      button.addTarget(self, action: #selector(footerActionPressed(_:)), for: .touchUpInside)
      actionStack.addArrangedSubview(button)
    }
    mainStack.addArrangedSubview(actionStack)

    trailingStack.axis = .vertical
    trailingStack.alignment = .trailing
    trailingStack.spacing = 2
    trailingStack.setContentHuggingPriority(.required, for: .horizontal)
    trailingStack.setContentCompressionResistancePriority(.required, for: .horizontal)
    accessoryButtons.enumerated().forEach { index, button in
      button.adjustsImageWhenDisabled = false
      button.tintAdjustmentMode = .normal
      button.titleLabel?.font = nativeListFont(
        ofSize: index == 0 ? 16 : 14,
        weight: index == 0 ? .medium : .regular
      )
      button.tag = index
      button.addTarget(self, action: #selector(accessoryPressed(_:)), for: .touchUpInside)
      trailingStack.addArrangedSubview(button)
    }
    checkboxButton.layer.borderWidth = 2
    checkboxButton.layer.cornerRadius = 4
    checkboxButton.tintColor = checkboxUncheckedColor
    checkboxButton.imageView?.contentMode = .center
    checkboxButton.addTarget(self, action: #selector(checkboxPressed), for: .touchUpInside)
    NSLayoutConstraint.activate([
      checkboxButton.widthAnchor.constraint(equalToConstant: 20),
      checkboxButton.heightAnchor.constraint(equalToConstant: 20),
    ])
    trailingStack.addArrangedSubview(checkboxButton)
    trailingStack.addArrangedSubview(spinner)

    dataStack.axis = .horizontal
    dataStack.alignment = .center
    dataStack.distribution = .fillProportionally
    dataStack.spacing = 8
    dataLabels.forEach { label in
      label.font = nativeListFont(ofSize: 12)
      label.lineBreakMode = .byTruncatingTail
      dataStack.addArrangedSubview(label)
    }
    tableDataStack.axis = .horizontal
    tableDataStack.alignment = .center
    tableDataStack.distribution = .fill
    tableDataStack.spacing = 8
    tableDataColumns.forEach(tableDataStack.addArrangedSubview)
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    if let item = currentItem, item.type == "system", item.data.string("presentation") == "market",
       ["noMatch", "retry"].contains(item.data.string("variant")), !titleLabel.isHidden {
      // The cell content view owns the root constraints and must settle before reading descendants.
      contentView.layoutIfNeeded()
      // React Native floors text origins to physical pixels after centering the line box.
      titleLabel.transform = .identity
      let scale = max(1, window?.screen.scale ?? traitCollection.displayScale)
      let origin = titleLabel.convert(titleLabel.bounds, to: contentView).origin
      let x = floor((contentView.bounds.width - titleLabel.bounds.width) / 2 * scale) / scale
      let contentHeight: CGFloat = item.data.string("variant") == "retry" ? 56 : 24
      let y = floor(max(32, (contentView.bounds.height - contentHeight) / 2) * scale) / scale
      titleLabel.transform = CGAffineTransform(translationX: x - origin.x, y: y - origin.y)
    }
    // OneKey patch: extend only the background across the section list outer inset.
    if currentItem?.data.bool("backgroundFullWidth") == true {
      selectorFullWidthBackground.frame = CGRect(x: -frame.minX, y: 0, width: superview?.bounds.width ?? bounds.width, height: bounds.height)
    }
    if currentItem?.type == "mediaTile" {
      mediaHeight.constant = max(0, contentView.bounds.width - 20)
    }
    if currentItem?.type == "identity", currentItem?.data["height"] != nil,
       currentItem?.data.string("presentation") == "accountSelector",
       let accessory = currentItem?.data.dictionaries("trailing").first,
       accessory.string("kind") == "icon", accessory.string("name") == "PlusSmallOutline" {
      // OneKey patch: PlusButton's fixed top18 slot and negative7 margin place its frame at11.
      let button = accessoryButtons[0]
      button.transform = .identity
      let origin = button.convert(button.bounds, to: contentView).minY
      button.transform = CGAffineTransform(translationX: 0, y: 11 - origin)
    }
  }

  // OneKey patch: trim only detached/recycled members, never a live compact
  // drag proxy or an expanding group. Small groups retain eight reusable cells.
  private func trimWalletGroupCells(keeping required: Int) {
    let retained = max(8, required)
    while walletGroupCells.count > retained {
      let cell = walletGroupCells.removeLast()
      rootStack.removeArrangedSubview(cell)
      cell.removeFromSuperview()
      cell.prepareForReuse()
      cell.onAction = nil
      cell.onBindingInvalidated = nil
    }
  }

  override func prepareForReuse() {
    let canTrimMembers = !walletGroupCompactAppearanceActive && (rootStack.layer.animationKeys()?.isEmpty ?? true)
    super.prepareForReuse()
    invalidateCurrentBinding()
    isHighlighted = false
    restingBackgroundColor = .clear
    contentView.backgroundColor = restingBackgroundColor
    currentItem = nil
    walletGroupCompactAppearanceActive = false
    walletGroupCompactContainer.isHidden = true
    walletGroupCompactContainer.alpha = 1
    walletGroupCompactCell?.prepareForReuse()
    walletGroupCells.forEach { $0.prepareForReuse() }
    walletGroupMembers.removeAll()
    if canTrimMembers { trimWalletGroupCells(keeping: 0) }
    leadingImages.forEach { $0.prepareForReuse() }
    secondaryImage.prepareForReuse()
    mediaNetworkImage.prepareForReuse()
  }

  func bind(
    item: NativeListItem,
    theme: [String: Any]?,
    layout: String,
    itemIndex: Int?,
    selected: Bool,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    // OneKey patch: a same-row snapshot refresh must not clear a touch that is still held.
    let shouldRestoreHighlight = isHighlighted && currentItem?.key == item.key
    invalidateCurrentBinding()
    bindingEpoch &+= 1
    currentLayout = layout
    currentTheme = theme
    currentItemIndex = itemIndex
    currentItem = item
    leadingImages.forEach { $0.prepareForReuse() }
    secondaryImage.prepareForReuse()
    mediaNetworkImage.prepareForReuse()
    reset()

    let primary = nativeListColor(theme, "primaryText", "#202020")
    let secondary = nativeListColor(theme, "secondaryText", "#646464")
    let accent = nativeListColor(theme, "accent", "#108303")
    checkboxCheckedColor = primary
    checkboxUncheckedColor = nativeListColor(theme, "inverseText", "#FCFCFC")
    // Checkbox uses the literal neutral7 alpha token. Applying opacity to the
    // opaque primary text color produces a different RGB result.
    checkboxBorderColor = UIColor(nativeListHex: "#00000031", fallback: .lightGray)
    checkboxIconColor = checkboxUncheckedColor
    if item.data.string("presentation") == "networkSelector" {
      checkboxCheckedColor = nativeListColor(theme, "checkboxBackground", "#202020")
      checkboxBorderColor = nativeListColor(theme, "checkboxBorder", "#00000031")
      checkboxIconColor = nativeListColor(theme, "checkboxIcon", "#FFFFFF")
      // OneKey patch: the V1 checkbox fills even its unchecked body with iconInverse.
      checkboxUncheckedColor = checkboxIconColor
    }
    visualBackdropColor = nativeListColor(theme, "rowBackground", "#FFFFFF")
    titleLabel.textColor = primary
    subtitleLabel.textColor = secondary
    tertiaryLabel.textColor = secondary
    statusLabel.textColor = secondary
    metricSubtitleLabel.textColor = secondary
    badgeLabel.textColor = accent
    accessoryButtons.forEach { $0.setTitleColor(primary, for: .normal) }
    unreadDot.backgroundColor = UIColor(nativeListHex: "#E5484D", fallback: .systemRed)
    mediaBadgeLabel.backgroundColor = nativeListColor(theme, "inverseBackground", "#202020")
    mediaBadgeLabel.textColor = nativeListColor(theme, "inverseText", "#FCFCFC")
    mediaBadgeLabel.layer.borderColor = nativeListColor(theme, "rowBackground", "#FFFFFF").cgColor
    separatorView.backgroundColor = nativeListColor(theme, "separator", "#E0E0E0")
    separatorLeadingConstraint.constant = item.type == "identity" ? 60 : 12
    separatorView.isHidden = !item.data.bool("separator")
    restingBackgroundColor = selectionBackgroundColor(
      item: item,
      theme: theme,
      layout: layout,
      itemIndex: itemIndex,
      selected: selected
    )
    pressedBackgroundColor = nativeListColor(theme, "rowPressedBackground", "#E8E8E8")
    if item.type == "rail" {
      pressedBackgroundColor = UIColor(nativeListHex: "#F0F0F0", fallback: .lightGray)
    } else if item.type == "mediaTile" {
      pressedBackgroundColor = restingBackgroundColor
    }
    updateBackgroundColor()
    if item.data.bool("backgroundFullWidth"), let background = item.data["backgroundColor"] as? String {
      selectorFullWidthBackground.backgroundColor = UIColor(nativeListHex: background, fallback: .clear).cgColor
      contentView.layer.insertSublayer(selectorFullWidthBackground, at: 0)
      clipsToBounds = false
    }
    if layout == "table" {
      if item.type == "dataRow" {
        rootLeadingConstraint.constant = 20
        rootTrailingConstraint.constant = -20
        rootTopConstraint.constant = 10
        rootBottomConstraint.constant = -10
        rootStack.spacing = 10
      } else {
        rootLeadingConstraint.constant = 16
        rootTrailingConstraint.constant = -16
      }
    }
    applyGroupPosition(item.data.string("groupPosition"))
    isUserInteractionEnabled = !item.data.bool("disabled")
    // OneKey patch: deprecated wallets remain interactive while dimmed.
    // contentView.alpha = isUserInteractionEnabled ? 1 : 0.5
    contentView.alpha = CGFloat(item.data.double("opacity", default: 1)) * (isUserInteractionEnabled ? 1 : 0.5)
    accessibilityLabel = item.data.string("accessibilityLabel", default: item.data.string("title"))
    // OneKey patch: keep existing selector automation identifiers.
    accessibilityIdentifier = item.data["testID"] as? String

    switch item.type {
    case "walletGroup": bindWalletGroup(item, theme: theme, layout: layout, checkboxState)
    case "identity": bindIdentity(item, theme: theme, selected: selected, checkboxState)
    case "rail": bindRail(item, theme: theme)
    case "activity": bindActivity(item, theme: theme)
    case "message": bindMessage(item, theme: theme)
    case "dataRow": bindDataRow(item, theme: theme, checkboxState)
    case "market": bindMarket(item, theme: theme)
    case "mediaTile": bindMediaTile(item, theme: theme)
    case "metricCard": bindMetricCard(item, theme: theme)
    case "sectionHeader": bindSectionHeader(item, theme: theme, layout: layout, checkboxState)
    case "action": bindAction(item, theme: theme, checkboxState)
    case "system": bindSystem(item, theme: theme)
    default: break
    }
    applySelectorTypography(item)
    if shouldRestoreHighlight && isUserInteractionEnabled {
      isHighlighted = true
    }
    if item.type == "sectionHeader", item.data.string("presentation") == "networkSelector", item.data["height"] != nil, item.data.dictionary("checkbox") != nil, !item.data.string("value").isEmpty {
      // OneKey patch: UIKit must reserve only the total's intrinsic width before the checkbox.
      let valueWidth = accessoryButtons[0].intrinsicContentSize.width
      let width = trailingStack.widthAnchor.constraint(equalToConstant: valueWidth + 12 + 20)
      width.isActive = true
      selectorConstraints.append(width)
    }
  }

  func updateSelection(
    item: NativeListItem,
    selected: Bool,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    guard currentItem?.key == item.key else { return }
    restoreSelectorTypography()
    defer { applySelectorTypography(item) }
    if item.type == "walletGroup" {
      currentItem = item
      let memberData = [item.data.dictionary("parent")].compactMap { $0 }
        + item.data.dictionaries("children")
      walletGroupMembers = memberData.compactMap { try? NativeListItem(data: $0) }
      walletGroupMembers.enumerated().forEach { index, member in
        if walletGroupCells.indices.contains(index) {
          walletGroupCells[index].updateSelection(
            item: member,
            selected: member.data.bool("selected"),
            checkboxState: checkboxState
          )
        }
      }
      if let parent = walletGroupMembers.first {
        walletGroupCompactCell?.updateSelection(
          item: parent,
          selected: parent.data.bool("selected"),
          checkboxState: checkboxState
        )
      }
      restingBackgroundColor = nativeListColor(currentTheme, "subduedBackground", "#F9F9F9")
      pressedBackgroundColor = restingBackgroundColor
      updateBackgroundColor()
      return
    }
    if item.type == "sectionHeader", item.data.string("variant") == "summary" {
      updateSummaryText(item)
    }
    currentItem = item
    restingBackgroundColor = selectionBackgroundColor(
      item: item,
      theme: currentTheme,
      layout: currentLayout,
      itemIndex: currentItemIndex,
      selected: selected
    )
    updateBackgroundColor()
    if item.type == "identity", item.data.string("presentation") == "walletSidebar" {
      titleLabel.textColor = nativeListColor(
        currentTheme,
        selected ? "primaryText" : "secondaryText",
        selected ? "#FFFFFFED" : "#FFFFFFAF"
      )
    }
    // OneKey patch: retain the latest descriptor when a controlled echo avoids full binding.
    if boundCheckboxData != nil {
      let latestCheckbox = item.type == "identity"
        ? item.data.dictionaries("trailing").last { $0.string("kind") == "checkbox" }
        : item.data.dictionary("checkbox")
      boundCheckboxData = latestCheckbox ?? boundCheckboxData
    }
    guard let data = boundCheckboxData, let target = boundCheckboxTarget else { return }
    updateCheckboxPresentation(
      item,
      data,
      target: target,
      state: checkboxState(item, target, data.string("state", default: "unchecked"))
    )
  }

  // OneKey patch: match SizableText TABULAR_NUMS on every selector text run, retaining its face and size.
  private func selectorTabularFont(_ font: UIFont) -> UIFont {
    var settings = font.fontDescriptor.fontAttributes[.featureSettings] as? [[UIFontDescriptor.FeatureKey: Int]] ?? []
    settings.removeAll { $0[.type] == kNumberSpacingType }
    settings.append([.type: kNumberSpacingType, .selector: kMonospacedNumbersSelector])
    return UIFont(descriptor: font.fontDescriptor.addingAttributes([.featureSettings: settings]), size: font.pointSize)
  }

  private func selectorTabularText(_ original: NSAttributedString) -> NSAttributedString {
    let result = NSMutableAttributedString(attributedString: original)
    // OneKey patch: body typography explicitly supplies letterSpacing=0 in Tamagui.
    result.addAttribute(.kern, value: 0, range: NSRange(location: 0, length: result.length))
    original.enumerateAttribute(.font, in: NSRange(location: 0, length: original.length)) { value, range, _ in
      if let font = value as? UIFont { result.addAttribute(.font, value: self.selectorTabularFont(font), range: range) }
    }
    return result
  }

  private func restoreSelectorTypography() {
    selectorTypographyRestorers.reversed().forEach { $0() }
    selectorTypographyRestorers.removeAll()
  }

  private func applySelectorTypography(_ item: NativeListItem) {
    guard ["accountSelector", "networkSelector", "walletSidebar"].contains(item.data.string("presentation")) || item.type == "system" && item.data.string("variant") == "warning" else { return }
    func visit(_ view: UIView) {
      if let button = view as? UIButton {
        if let original = button.attributedTitle(for: .normal) {
          selectorTypographyRestorers.append { button.setAttributedTitle(original, for: .normal) }
          button.setAttributedTitle(selectorTabularText(original), for: .normal)
        }
      } else if let label = view as? UILabel, let font = label.font {
        let original = label.attributedText
        selectorTypographyRestorers.append { label.font = font; label.attributedText = original }
        label.font = selectorTabularFont(font)
        if let original { label.attributedText = selectorTabularText(original) }
      }
      for child in view.subviews { visit(child) }
    }
    visit(contentView)
  }

  private func updateSummaryText(_ item: NativeListItem) {
    let title = item.data.string("title")
    titleLabel.isHidden = title.isEmpty
    setLineHeight(titleLabel, text: title, lineHeight: 24)

    let value = item.data.string("value")
    let isExplicitNetworkHeader = item.data.string("presentation") == "networkSelector" && item.data["height"] != nil
    let valueButton = accessoryButtons[0]
    valueButton.isHidden = value.isEmpty
    if value.isEmpty {
      valueButton.setTitle(nil, for: .normal)
      valueButton.setAttributedTitle(nil, for: .normal)
    } else {
      setButtonLine(
        valueButton,
        text: value,
        font: nativeListFont(ofSize: 16, weight: isExplicitNetworkHeader ? .medium : .regular),
        color: nativeListColor(currentTheme, "secondaryText", "#646464"),
        lineHeight: 24
      )
    }
  }

  private func selectionBackgroundColor(
    item: NativeListItem,
    theme: [String: Any]?,
    layout: String,
    itemIndex: Int?,
    selected: Bool
  ) -> UIColor {
    var color = nativeListColor(
      theme,
      selected ? "rowSelectedBackground" : "rowBackground",
      selected ? "#F0F0F0" : "#FFFFFF"
    )
    if item.type == "metricCard", !selected {
      color = nativeListColor(theme, "subduedBackground", "#F9F9F9")
    } else if item.type == "rail" || item.type == "mediaTile" {
      // Selection is communicated by the destination state for these source
      // components; neither has a persistent selected tile background.
      color = nativeListColor(theme, "rowBackground", "#FFFFFF")
    } else if layout == "sectioned", !item.data.bool("selected") {
      // Checkbox-backed section lists in app-monorepo keep rows on $bg;
      // selection is represented by the checkbox itself.
      color = nativeListColor(theme, "rowBackground", "#FFFFFF")
    } else if layout == "table",
              item.type == "dataRow",
              (item.data["index"] == nil ? itemIndex : item.data.int("index")).map({ $0 % 2 == 0 }) == true,
              !selected {
      color = nativeListColor(theme, "subduedBackground", "#F9F9F9")
    }
    // OneKey patch: portfolio group headers retain their source background.
    if let backgroundColor = item.data["backgroundColor"] as? String {
      return UIColor(nativeListHex: backgroundColor, fallback: color)
    }
    return color
  }

  private func reset() {
    titleLabel.transform = .identity
    restoreSelectorTypography()
    // OneKey patch: remove selector decorations before rebinding recycled cells.
    selectorViews.forEach { $0.removeFromSuperview() }
    selectorViews.removeAll()
    NSLayoutConstraint.deactivate(selectorConstraints)
    selectorConstraints.removeAll()
    selectorImages.forEach { $0.prepareForReuse() }
    selectorImages.removeAll()
    marketBadgeImages.forEach { $0.prepareForReuse(); $0.isHidden = true }
    selectorFullWidthBackground.removeFromSuperlayer()
    selectorBorder?.removeFromSuperlayer()
    selectorBorder = nil
    titleLabel.removeGestureRecognizer(selectorTitleTap)
    titleLabel.isUserInteractionEnabled = false
    walletGroupCompactCell?.invalidateCurrentBinding()
    walletGroupCells.forEach { $0.invalidateCurrentBinding() }
    isHighlighted = false
    rootStack.arrangedSubviews.forEach {
      rootStack.removeArrangedSubview($0)
      $0.removeFromSuperview()
    }
    walletGroupMembers.removeAll()
    // OneKey patch: reset has detached the old hierarchy, including on direct
    // large-to-small binds that do not pass through UICollectionView reuse.
    let requiredMembers = currentItem?.type == "walletGroup" ? (currentItem?.data.dictionaries("children").count ?? 0) + 1 : 0
    trimWalletGroupCells(keeping: requiredMembers)
    walletGroupCompactAppearanceActive = false
    walletGroupCompactContainer.isHidden = true
    walletGroupCompactContainer.alpha = 1
    walletGroupDragBadge.text = nil
    rootStack.isHidden = false
    rootStack.alpha = 1
    walletGroupTap.isEnabled = false
    contentView.layer.borderWidth = 0
    contentView.layer.borderColor = nil
    rootStack.axis = .horizontal
    rootStack.alignment = .center
    rootStack.spacing = 12
    rootStack.setCustomSpacing(12, after: leadingActionButton)
    rootStack.distribution = .fill
    rootLeadingConstraint.constant = 12
    rootTrailingConstraint.constant = -12
    rootTopConstraint.constant = 8
    rootBottomConstraint.constant = -8
    mainStack.axis = .vertical
    mainStack.alignment = .fill
    mainStack.spacing = 2
    mainStack.isHidden = false
    mainStack.setContentHuggingPriority(UILayoutPriority(1), for: .horizontal)
    mainStack.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    titleRowStack.isHidden = false
    titleRowStack.spacing = 8
    trailingStack.isHidden = false
    trailingStack.axis = .vertical
    trailingStack.alignment = .trailing
    trailingStack.spacing = 2
    headerTitleIconImageView.removeFromSuperview()
    headerTitleIconImageView.image = nil
    headerTitleIconImageView.isHidden = true
    headerValueIconImageView.removeFromSuperview()
    headerValueIconImageView.image = nil
    headerValueIconImageView.isHidden = true
    // OneKey patch: restore the shared labels before any recycled row binds.
    marketSubtitleStack.arrangedSubviews.forEach {
      marketSubtitleStack.removeArrangedSubview($0)
      $0.removeFromSuperview()
    }
    mainStack.removeArrangedSubview(marketSubtitleStack)
    marketSubtitleStack.removeFromSuperview()
    mainStack.removeArrangedSubview(tertiaryLabel)
    tertiaryLabel.removeFromSuperview()
    mediaMetadataStack.removeArrangedSubview(subtitleLabel)
    mediaMetadataStack.removeArrangedSubview(mediaNetworkImage)
    mediaMetadataStack.removeFromSuperview()
    mainStack.removeArrangedSubview(titleRowStack)
    titleRowStack.removeFromSuperview()
    mainStack.removeArrangedSubview(subtitleLabel)
    subtitleLabel.removeFromSuperview()
    mainStack.insertArrangedSubview(titleRowStack, at: 0)
    mainStack.insertArrangedSubview(subtitleLabel, at: 1)
    mainStack.insertArrangedSubview(tertiaryLabel, at: 2)
    leadingWidth.constant = 40
    leadingHeight.constant = 40
    leadingIconWidth.constant = 18
    leadingIconHeight.constant = 18
    leadingWidth.isActive = true
    leadingHeight.isActive = true
    mediaHeight.isActive = false
    secondaryWidth.isActive = false
    secondaryHeight.isActive = false
    NSLayoutConstraint.deactivate(leadingSlotConstraints)
    leadingSlotConstraints.removeAll()
    NSLayoutConstraint.deactivate(dataWeightConstraints)
    dataWeightConstraints.removeAll()
    NSLayoutConstraint.deactivate(accessorySizeConstraints)
    accessorySizeConstraints.removeAll()
    dataStack.distribution = .fill
    leadingImages.forEach {
      $0.isHidden = true
      $0.alpha = 1
      $0.layer.cornerRadius = 0
      $0.layer.mask = nil
    }
    leadingContainer.clipsToBounds = true
    leadingContainer.layer.borderWidth = 0
    leadingContainer.layer.borderColor = nil
    leadingOverlayBackground.isHidden = true
    leadingIconImageView.isHidden = true
    leadingIconImageView.image = nil
    leadingCornerIconBackground.isHidden = true
    leadingCornerIconImageView.image = nil
    favoriteIconImageView.isHidden = true
    favoriteIconImageView.image = nativeListIcon(named: "StarOutline")
    leadingActionButton.isHidden = true
    leadingActionButton.setImage(nil, for: .normal)
    leadingActionButton.setImage(nil, for: .disabled)
    leadingActionButton.accessibilityIdentifier = nil
    leadingActionButton.accessibilityLabel = nil
    unreadDot.isHidden = true
    mediaBadgeLabel.isHidden = true
    mediaBadgeLabel.text = nil
    fallbackLabel.isHidden = false
    fallbackLabel.text = nil
    fallbackLabel.font = nativeListFont(ofSize: 13, weight: .bold)
    secondaryImage.isHidden = false
    secondaryImage.layer.borderWidth = 0
    secondaryImage.layer.borderColor = nil
    mediaNetworkImage.isHidden = true
    titleLabel.text = nil
    titleLabel.font = nativeListFont(ofSize: 16, weight: .medium)
    titleLabel.lineBreakMode = .byTruncatingTail
    titleRowStack.setContentHuggingPriority(.defaultLow, for: .horizontal)
    titleLabel.setContentHuggingPriority(.defaultLow, for: .horizontal)
    titleLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
    titleLabel.textAlignment = .natural
    subtitleLabel.text = nil
    subtitleLabel.lineBreakMode = .byTruncatingTail
    subtitleLabel.attributedText = nil
    subtitleLabel.setContentHuggingPriority(.defaultLow, for: .horizontal)
    subtitleLabel.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    tertiaryLabel.attributedText = nil
    tertiaryLabel.lineBreakMode = .byTruncatingTail
    tertiaryLabel.setContentHuggingPriority(.defaultLow, for: .horizontal)
    tertiaryLabel.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    tertiaryLabel.text = nil
    statusLabel.text = nil
    statusLabel.topInset = 0
    statusLabel.bottomInset = 0
    metricSubtitleLabel.text = nil
    metricCompositeStack.arrangedSubviews.forEach {
      metricCompositeStack.removeArrangedSubview($0)
      $0.removeFromSuperview()
    }
    mainStack.removeArrangedSubview(metricCompositeStack)
    metricCompositeStack.removeFromSuperview()
    badgeLabel.text = nil
    badgeLabel.horizontalInset = 0
    badgeLabel.topInset = 0
    badgeLabel.bottomInset = 0
    subtitleLabel.font = nativeListFont(ofSize: 14)
    tertiaryLabel.font = nativeListFont(ofSize: 14)
    statusLabel.font = nativeListFont(ofSize: 12)
    badgeLabel.font = nativeListFont(ofSize: 12, weight: .medium)
    badgeLabel.backgroundColor = .clear
    badgeLabel.layer.cornerRadius = 0
    badgeLabel.clipsToBounds = false
    marketBadgeButtons.forEach {
      $0.isHidden = true
      $0.isEnabled = true
      $0.isUserInteractionEnabled = false
      // OneKey patch: a recycled badge must not retain an attributed title or font.
      $0.setAttributedTitle(nil, for: .normal)
      $0.marketLineHeight = nil
      $0.selectorSummaryLineHeight = nil
      $0.titleLabel?.font = nativeListFont(ofSize: 11, weight: .medium)
      $0.titleLabel?.numberOfLines = 1
      $0.contentHorizontalAlignment = .center
      $0.accessibilityLabel = nil
      $0.accessibilityTraits = .staticText
      $0.setTitle(nil, for: .normal)
      $0.setImage(nil, for: .normal)
      $0.setTitleColor(nil, for: .normal)
      $0.tintColor = nil
      $0.backgroundColor = .clear
      $0.contentEdgeInsets = .zero
      $0.imageEdgeInsets = .zero
      $0.titleEdgeInsets = .zero
    }
    [titleLabel, subtitleLabel, tertiaryLabel, statusLabel, metricSubtitleLabel, badgeLabel, actionStack]
      .forEach { $0.isHidden = true }
    separatorView.isHidden = true
    separatorLeadingConstraint.constant = 0
    actionButtons.forEach {
      $0.isHidden = true
      $0.setTitle(nil, for: .normal)
      $0.backgroundColor = .clear
    }
    accessoryButtons.enumerated().forEach { index, button in
      button.isUserInteractionEnabled = true
      button.selectorSummaryLineHeight = nil
      button.marketLineHeight = nil
      button.titleLabel?.font = nativeListFont(
        ofSize: index == 0 ? 16 : 14,
        weight: index == 0 ? .medium : .regular
      )
      button.isHidden = true
      button.accessibilityIdentifier = nil
      button.setTitle(nil, for: .normal)
      button.setAttributedTitle(nil, for: .normal)
      button.setImage(nil, for: .normal)
      button.setImage(nil, for: .disabled)
      button.isEnabled = true
      button.alpha = 1
      button.titleLabel?.numberOfLines = 1
      button.contentHorizontalAlignment = .center
      button.backgroundColor = .clear
      button.layer.cornerRadius = 0
      button.contentEdgeInsets = .zero
      button.transform = .identity
    }
    checkboxButton.isHidden = true
    checkboxButton.alpha = 1
    checkboxButton.backgroundColor = .clear
    checkboxButton.isEnabled = true
    checkboxButton.setImage(nil, for: .normal)
    spinner.stopAnimating()
    spinner.alpha = 1
    dataLabels.forEach {
      $0.isHidden = true
      $0.text = nil
      $0.attributedText = nil
      $0.numberOfLines = 1
    }
    tableDataColumns.forEach {
      $0.reset()
      $0.isHidden = true
    }
    skeletonPrimary.removeFromSuperview()
    skeletonSecondary.removeFromSuperview()
    accessoryActions = []
    footerActionKeys = []
    checkboxAction = nil
    boundCheckboxData = nil
    boundCheckboxTarget = nil
    leadingActionKey = nil
    marketBadgeActionKeys = []
    layer.maskedCorners = []
    layer.cornerRadius = 0
    layer.cornerCurve = .circular
    contentView.layer.cornerRadius = 0
    contentView.layer.cornerCurve = .circular
    contentView.clipsToBounds = false
    leadingContainer.alpha = 1
    titleLabel.showsDottedUnderline = false
    titleLabel.reservesDottedUnderlineSpace = false
    titleLabel.dottedUnderlineVerticalOffset = 0
  }

  private func bindWalletGroup(
    _ item: NativeListItem,
    theme: [String: Any]?,
    layout: String,
    _ checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    let memberData = [item.data.dictionary("parent")].compactMap { $0 }
      + item.data.dictionaries("children")
    walletGroupMembers = memberData.compactMap { try? NativeListItem(data: $0) }
    if walletGroupCompactCell == nil {
      let compactCell = NativeListCell(frame: .zero)
      compactCell.translatesAutoresizingMaskIntoConstraints = false
      walletGroupCompactContainer.addSubview(compactCell)
      walletGroupCompactContainer.addSubview(walletGroupDragBadge)
      walletGroupDragBadge.translatesAutoresizingMaskIntoConstraints = false
      walletGroupDragBadge.horizontalInset = 6
      walletGroupDragBadge.textAlignment = .center
      walletGroupDragBadge.font = nativeListTabularFont(ofSize: 12, weight: .semibold)
      walletGroupDragBadge.layer.cornerRadius = 12
      walletGroupDragBadge.layer.masksToBounds = true
      walletGroupDragBadge.layer.borderWidth = 1
      NSLayoutConstraint.activate([
        compactCell.leadingAnchor.constraint(equalTo: walletGroupCompactContainer.leadingAnchor),
        compactCell.trailingAnchor.constraint(equalTo: walletGroupCompactContainer.trailingAnchor),
        compactCell.topAnchor.constraint(equalTo: walletGroupCompactContainer.topAnchor),
        compactCell.bottomAnchor.constraint(equalTo: walletGroupCompactContainer.bottomAnchor),
        walletGroupDragBadge.trailingAnchor.constraint(
          equalTo: walletGroupCompactContainer.trailingAnchor,
          constant: -4
        ),
        walletGroupDragBadge.bottomAnchor.constraint(
          equalTo: walletGroupCompactContainer.bottomAnchor,
          constant: -4
        ),
        walletGroupDragBadge.heightAnchor.constraint(equalToConstant: 24),
        walletGroupDragBadge.widthAnchor.constraint(greaterThanOrEqualToConstant: 24),
      ])
      walletGroupCompactCell = compactCell
    }
    while walletGroupCells.count < walletGroupMembers.count {
      let memberCell = NativeListCell(frame: .zero)
      memberCell.translatesAutoresizingMaskIntoConstraints = false
      // OneKey patch: each member's current height is applied when bound.
      // memberCell.heightAnchor.constraint(equalToConstant: 68).isActive = true
      walletGroupCells.append(memberCell)
    }
    rootStack.axis = .vertical
    rootStack.alignment = .fill
    rootStack.spacing = 12
    rootLeadingConstraint.constant = 0
    rootTrailingConstraint.constant = 0
    rootTopConstraint.constant = 0
    rootBottomConstraint.constant = 0
    if memberData.first?["height"] != nil {
      // OneKey patch: the source group's one-point border occupies layout space.
      rootLeadingConstraint.constant = 1
      rootTrailingConstraint.constant = -1
      rootTopConstraint.constant = 1
      rootBottomConstraint.constant = -1
    }
    walletGroupMembers.enumerated().forEach { index, member in
      let memberCell = walletGroupCells[index]
      // OneKey patch: badges add a second line within their logical wallet group.
      memberCell.constraints.filter { $0.firstAttribute == .height && $0.secondItem == nil }.forEach { $0.isActive = false }
      memberCell.heightAnchor.constraint(equalToConstant: CGFloat(member.data.double("height", default: member.data.dictionaries("badges").isEmpty ? 68 : 92))).isActive = true
      memberCell.onAction = { [weak self] source, action, target, origin in
        self?.onAction?(source, action, target, origin)
      }
      memberCell.onBindingInvalidated = { [weak self] cell, epoch in
        self?.onBindingInvalidated?(cell, epoch)
      }
      memberCell.bind(
        item: member,
        theme: theme,
        layout: layout,
        itemIndex: nil,
        selected: member.data.bool("selected"),
        checkboxState: checkboxState
      )
      rootStack.addArrangedSubview(memberCell)
    }
    if let parent = walletGroupMembers.first {
      walletGroupCompactCell?.bind(
        item: parent,
        theme: theme,
        layout: layout,
        itemIndex: nil,
        selected: parent.data.bool("selected"),
        checkboxState: checkboxState
      )
    }
    let childCount = item.data.dictionaries("children").count
    walletGroupDragBadge.text = childCount > 0 ? "+\(childCount)" : nil
    walletGroupDragBadge.isHidden = childCount == 0
    walletGroupDragBadge.backgroundColor = nativeListColor(
      theme,
      "inverseBackground",
      "#202020"
    )
    walletGroupDragBadge.textColor = nativeListColor(theme, "inverseText", "#FCFCFC")
    walletGroupDragBadge.layer.borderColor = nativeListColor(
      theme,
      "rowBackground",
      "#FFFFFF"
    ).cgColor
    restingBackgroundColor = nativeListColor(theme, "subduedBackground", "#F9F9F9")
    pressedBackgroundColor = restingBackgroundColor
    contentView.backgroundColor = restingBackgroundColor
    contentView.layer.borderWidth = 1
    contentView.layer.borderColor = nativeListColor(theme, "separator", "#E0E0E0").cgColor
    contentView.layer.cornerRadius = 20
    contentView.layer.cornerCurve = .continuous
    contentView.clipsToBounds = true
    walletGroupTap.isEnabled = true
  }

  @objc private func walletGroupPressed(_ gesture: UITapGestureRecognizer) {
    let point = gesture.location(in: rootStack)
    for (index, cell) in walletGroupCells.prefix(walletGroupMembers.count).enumerated()
      where cell.frame.contains(point) {
      // OneKey patch: group member press gating must not disable accessory controls.
      if !walletGroupMembers[index].data.bool("pressDisabled") {
        onAction?(walletGroupMembers[index], "press", nil, cell.rowActionOrigin())
      }
      return
    }
  }

  func setPressed(_ pressed: Bool) {
    if currentItem?.type == "walletGroup", walletGroupCompactAppearanceActive {
      walletGroupCompactCell?.setPressed(pressed)
      isHighlighted = false
      return
    }
    isHighlighted = pressed && isUserInteractionEnabled
  }

  func setWalletGroupReorderCompact(_ compact: Bool) {
    guard currentItem?.type == "walletGroup" else { return }
    walletGroupCompactAppearanceActive = compact
    rootStack.isHidden = compact
    rootStack.alpha = compact ? 0 : 1
    walletGroupCompactContainer.isHidden = !compact
    walletGroupCompactContainer.alpha = compact ? 1 : 0
    if compact {
      contentView.backgroundColor = .clear
      contentView.layer.borderWidth = 0
      contentView.layer.borderColor = nil
    } else {
      restoreWalletGroupOuterAppearance()
    }
  }

  func prepareWalletGroupReorderExpansion() {
    guard currentItem?.type == "walletGroup" else { return }
    walletGroupCompactAppearanceActive = false
    rootStack.isHidden = false
    rootStack.alpha = 0
    walletGroupCompactContainer.isHidden = false
    walletGroupCompactContainer.alpha = 1
    restoreWalletGroupOuterAppearance()
  }

  func animateWalletGroupReorderExpansion() {
    guard currentItem?.type == "walletGroup" else { return }
    rootStack.alpha = 1
    walletGroupCompactContainer.alpha = 0
  }

  func finishWalletGroupReorderExpansion() {
    guard currentItem?.type == "walletGroup" else { return }
    rootStack.isHidden = false
    rootStack.alpha = 1
    walletGroupCompactContainer.isHidden = true
    walletGroupCompactContainer.alpha = 1
    restoreWalletGroupOuterAppearance()
  }

  private func restoreWalletGroupOuterAppearance() {
    contentView.backgroundColor = nativeListColor(currentTheme, "subduedBackground", "#F9F9F9")
    contentView.layer.borderWidth = 1
    contentView.layer.borderColor = nativeListColor(
      currentTheme,
      "separator",
      "#E0E0E0"
    ).cgColor
  }

  private func updateBackgroundColor() {
    let pressed = isHighlighted && isUserInteractionEnabled
    contentView.backgroundColor = pressed ? pressedBackgroundColor : restingBackgroundColor
    if currentItem?.type == "walletGroup" {
      if walletGroupCompactAppearanceActive {
        contentView.backgroundColor = .clear
        contentView.layer.borderWidth = 0
        contentView.layer.borderColor = nil
      } else {
        restoreWalletGroupOuterAppearance()
      }
      layer.maskedCorners = [
        .layerMinXMinYCorner,
        .layerMaxXMinYCorner,
        .layerMinXMaxYCorner,
        .layerMaxXMaxYCorner,
      ]
      layer.cornerRadius = 20
      layer.cornerCurve = .continuous
      layer.masksToBounds = true
      contentView.layer.cornerRadius = 20
      contentView.layer.cornerCurve = .continuous
      contentView.clipsToBounds = true
      return
    }
    if currentItem?.type == "mediaTile" {
      // NFTListItem's group hover/press style belongs to the image wrapper,
      // not to the complete card.
      leadingContainer.alpha = pressed ? 0.8 : 1
      layer.maskedCorners = [
        .layerMinXMinYCorner,
        .layerMaxXMinYCorner,
        .layerMinXMaxYCorner,
        .layerMaxXMaxYCorner,
      ]
      layer.cornerRadius = 16
      layer.masksToBounds = true
      contentView.layer.cornerRadius = 16
      contentView.clipsToBounds = true
      return
    }
    if pressed {
      let isWalletSidebar = currentItem?.type == "identity"
        && currentItem?.data.string("presentation") == "walletSidebar"
      let radius: CGFloat
      if isWalletSidebar {
        radius = 20
      } else {
        radius = currentItem?.type == "rail" ? 8 : 12
      }
      layer.maskedCorners = [
        .layerMinXMinYCorner,
        .layerMaxXMinYCorner,
        .layerMinXMaxYCorner,
        .layerMaxXMaxYCorner,
      ]
      layer.cornerRadius = radius
      layer.cornerCurve = isWalletSidebar ? .continuous : .circular
      layer.masksToBounds = true
      contentView.layer.cornerRadius = radius
      contentView.layer.cornerCurve = isWalletSidebar ? .continuous : .circular
      contentView.clipsToBounds = true
    } else {
      applyGroupPosition(currentItem?.data.string("groupPosition") ?? "")
      // OneKey patch: explicit account and network selectors preserve ListItem radius while idle.
      // let restingRadius: CGFloat = currentItem?.type == "metricCard" ? 12 : 0
      let isSelectorIdentity = currentItem?.type == "identity" && currentItem?.data["height"] != nil
      let isAccountSelector = isSelectorIdentity && ["accountSelector", "networkSelector"].contains(currentItem?.data.string("presentation") ?? "")
      let isWalletSidebar = isSelectorIdentity && currentItem?.data.string("presentation") == "walletSidebar"
      let restingRadius: CGFloat = isWalletSidebar ? 20 : currentItem?.type == "metricCard" || isAccountSelector ? 12 : 0
      contentView.layer.cornerRadius = restingRadius
      contentView.layer.cornerCurve = isWalletSidebar ? .continuous : .circular
      contentView.clipsToBounds = restingRadius > 0
    }
  }

  private func bindIdentity(
    _ item: NativeListItem,
    theme: [String: Any]?,
    selected: Bool,
    _ checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    if item.data.string("presentation") == "walletSidebar" {
      rootStack.axis = .vertical
      rootStack.alignment = .center
      rootStack.spacing = 4
      rootLeadingConstraint.constant = 4
      rootTrailingConstraint.constant = -4
      rootTopConstraint.constant = 4
      rootBottomConstraint.constant = -4
      mainStack.alignment = .center
      mainStack.spacing = 0
      titleRowStack.setContentHuggingPriority(.required, for: .horizontal)
      titleLabel.setContentHuggingPriority(.required, for: .horizontal)
      titleLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
      titleLabel.font = nativeListFont(ofSize: 12)
      titleLabel.textAlignment = .center
      fallbackLabel.font = nativeListFont(ofSize: 28)
      addLeading(item.data.dictionary("leading"), key: item.key)
      rootStack.addArrangedSubview(mainStack)
      // OneKey patch: activate width constraints only after both stacks share an ancestor.
      if item.data["height"] != nil {
        titleLabel.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        titleRowStack.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        let width = mainStack.widthAnchor.constraint(equalTo: rootStack.widthAnchor)
        width.isActive = true
        selectorConstraints.append(width)
        let titleWidth = titleRowStack.widthAnchor.constraint(lessThanOrEqualTo: mainStack.widthAnchor)
        titleWidth.isActive = true
        selectorConstraints.append(titleWidth)
      }
      show(titleLabel, item.data.string("title"), lines: 1)
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 16)
      titleLabel.textColor = nativeListColor(
        theme,
        selected ? "primaryText" : "secondaryText",
        selected ? "#FFFFFFED" : "#FFFFFFAF"
      )
      // OneKey patch: wallet tags belong below the centered name.
      let badges = item.data.dictionaries("badges")
      if !badges.isEmpty {
        let line = UIStackView()
        line.axis = .horizontal
        line.spacing = 4
        line.alignment = .center
        for badge in badges {
          let label = NativeListInsetLabel()
          let isSelector = item.data["height"] != nil
          let isWarning = badge.string("tone") == "warning"
          label.font = nativeListFont(ofSize: isSelector ? 11 : 12)
          label.textColor = nativeListColor(theme, isSelector && isWarning ? "caution" : "secondaryText", isSelector && isWarning ? "#AB6400" : "#646464")
          label.backgroundColor = nativeListColor(theme, isSelector ? (isWarning ? "cautionBackground" : "subduedBackground") : "strongBackground", isSelector && isWarning ? "#FFF8C5" : "#F0F0F0")
          label.horizontalInset = isSelector ? 6 : 4
          label.topInset = 2
          label.bottomInset = 2
          label.layer.cornerRadius = 4
          label.clipsToBounds = true
          setLineHeight(label, text: badge.string("text"), lineHeight: isSelector ? 14 : 16)
          line.addArrangedSubview(label)
        }
        mainStack.spacing = 4
        mainStack.addArrangedSubview(line)
        selectorViews.append(line)
      }
      return
    }
    if item.data.string("presentation") == "accountSelector" {
      leadingWidth.constant = 32
      leadingHeight.constant = 32
      titleLabel.font = nativeListFont(ofSize: 16)
    }
    if item.data.dictionary("leading")?.string("kind") == "network" {
      leadingWidth.constant = 32
      leadingHeight.constant = 32
    }
    if let leadingAction = item.data.dictionary("leadingAction") {
      leadingActionButton.isHidden = false
      let tintColor = UIColor(
        nativeListHex: leadingAction.string("tintColor", default: "#646464"),
        fallback: .darkGray
      )
      leadingActionButton.tintColor = tintColor
      if let image = nativeListIcon(named: leadingAction.string("name")) {
        leadingActionButton.setImage(image, for: .normal)
        leadingActionButton.setImage(
          image.withTintColor(tintColor, renderingMode: .alwaysOriginal),
          for: .disabled
        )
      }
      leadingActionButton.isEnabled = !leadingAction.bool("disabled")
      leadingActionButton.alpha = leadingActionButton.isEnabled ? 1 : 0.4
      leadingActionKey = leadingAction.string("actionKey")
      rootStack.addArrangedSubview(leadingActionButton)
      // ListItem.IconButton is a medium tertiary button: its 36-point frame
      // carries m=-7. The collection already contributes ListItem's outer
      // mx=8, so move the frame 7 points into that inset and reduce only the
      // following gap by 7. This preserves the source button, avatar and text
      // positions without shrinking the 24-point SVG glyph or its hit target.
      rootLeadingConstraint.constant = 5
      rootStack.setCustomSpacing(5, after: leadingActionButton)
    }
    addLeading(item.data.dictionary("leading"), key: item.key)
    // OneKey patch: custom network initials match LetterAvatar size 32.
    if item.data.string("presentation") == "networkSelector", let leading = item.data.dictionary("leading"), leading.dictionary("image") == nil, leading.dictionary("fallbackIcon") == nil, !leading.string("fallbackText").isEmpty {
      fallbackLabel.font = nativeListFont(ofSize: 19, weight: .semibold)
      fallbackLabel.textColor = nativeListColor(theme, "inverseText", "#FCFCFC")
      setLineHeight(fallbackLabel, text: leading.string("fallbackText"), lineHeight: 27)
    }
    rootStack.addArrangedSubview(mainStack)
    show(titleLabel, item.data.string("title"), lines: item.data.int("titleLines", default: 1))
    show(subtitleLabel, item.data.string("subtitle"), lines: item.data.int("subtitleLines", default: 1))
    show(tertiaryLabel, item.data.string("tertiary"), lines: 1)
    if !item.data.string("subtitle").isEmpty, item.data.string("tertiary").isEmpty {
      mainStack.spacing = 0
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 24)
      setLineHeight(subtitleLabel, text: item.data.string("subtitle"), lineHeight: 20)
    }
    // OneKey patch: preserve independent balance/address truncation and warning tones.
    let segments = item.data.dictionaries("subtitleSegments")
    if !segments.isEmpty {
      subtitleLabel.isHidden = true
      mainStack.spacing = 0
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 24)
      let line = UIStackView()
      line.axis = .horizontal
      line.alignment = .center
      line.spacing = 0
      for segment in segments {
        if segment.bool("separatorBefore") {
          let gap = UIView()
          gap.translatesAutoresizingMaskIntoConstraints = false
          let dot = UIView()
          dot.translatesAutoresizingMaskIntoConstraints = false
          dot.backgroundColor = nativeListColor(theme, "disabledText", "#8D8D8D")
          dot.layer.cornerRadius = 2
          gap.addSubview(dot)
          NSLayoutConstraint.activate([
            gap.widthAnchor.constraint(equalToConstant: 16),
            gap.heightAnchor.constraint(equalToConstant: 20),
            dot.widthAnchor.constraint(equalToConstant: 4),
            dot.heightAnchor.constraint(equalToConstant: 4),
            dot.centerXAnchor.constraint(equalTo: gap.centerXAnchor),
            dot.centerYAnchor.constraint(equalTo: gap.centerYAnchor),
          ])
          line.addArrangedSubview(gap)
        }
        let label = UILabel()
        label.font = nativeListFont(ofSize: 14)
        label.lineBreakMode = .byTruncatingTail
        // Match V1: the balance may shrink, but keep the shortened address intact.
        label.setContentCompressionResistancePriority(
          item.data.string("presentation") == "accountSelector" && segment.bool("separatorBefore")
            ? .defaultHigh
            : .defaultLow,
          for: .horizontal
        )
        label.textColor = dataTextColor(segment.string("tone", default: "secondary"), theme: theme)
        setLineHeight(label, text: segment.string("text"), lineHeight: 20)
        let runs = segment.dictionaries("textSegments")
        if !runs.isEmpty {
          let value = NSMutableAttributedString(string: "")
          let paragraph = NSMutableParagraphStyle()
          paragraph.minimumLineHeight = 20
          paragraph.maximumLineHeight = 20
          for run in runs {
            value.append(NSAttributedString(string: run.string("text"), attributes: [
              .font: nativeListFont(ofSize: run.string("style") == "subscript" ? 9 : 14),
              .foregroundColor: label.textColor as Any,
              .paragraphStyle: paragraph,
              .baselineOffset: max(0, (20 - nativeListFont(ofSize: 14).lineHeight) / 2),
            ]))
          }
          label.attributedText = value
        }
        line.addArrangedSubview(label)
      }
      let filler = UIView()
      filler.setContentHuggingPriority(UILayoutPriority(1), for: .horizontal)
      line.addArrangedSubview(filler)
      mainStack.insertArrangedSubview(line, at: 2)
      selectorViews.append(line)
    }
    let matches = item.data.dictionaries("titleMatch")
    if !matches.isEmpty {
      let text = NSMutableAttributedString(attributedString: titleLabel.attributedText ?? NSAttributedString(string: item.data.string("title")))
      for match in matches {
        let start = match.int("start")
        let end = match.int("end")
        if start >= 0 && end > start && end <= text.length {
          text.addAttribute(.foregroundColor, value: nativeListColor(theme, "info", "#0D74CE"), range: NSRange(location: start, length: end - start))
        }
      }
      titleLabel.attributedText = text
    }
    tertiaryLabel.textColor = nativeListColor(
      theme,
      item.data.string("tertiaryTone") == "info" ? "info" : "secondaryText",
      item.data.string("tertiaryTone") == "info" ? "#0D74CE" : "#646464"
    )
    let badges = item.data.dictionaries("badges").prefix(2).map { $0.string("text") }
    if !badges.isEmpty {
      let badgeText = badges.joined(separator: " · ")
      show(badgeLabel, badgeText, lines: 1)
      badgeLabel.horizontalInset = 8
      badgeLabel.topInset = 2
      badgeLabel.bottomInset = 2
      setLineHeight(badgeLabel, text: badgeText, lineHeight: 16)
      badgeLabel.backgroundColor = nativeListColor(theme, "strongBackground", "#F0F0F0")
      badgeLabel.textColor = nativeListColor(theme, "secondaryText", "#646464")
      badgeLabel.layer.cornerRadius = 4
      badgeLabel.clipsToBounds = true
    }
    rootStack.addArrangedSubview(trailingStack)
    let accessories = item.data.dictionaries("trailing")
    if accessories.contains(where: { $0.string("kind") == "checkbox" }) &&
       accessories.contains(where: { $0.string("kind") == "value" }) {
      trailingStack.axis = .horizontal
      trailingStack.alignment = .center
      trailingStack.spacing = 12
    } else if accessories.count == 2,
              accessories[0].string("kind") == "valuePair",
              accessories[1].string("kind") == "menu" {
      trailingStack.axis = .horizontal
      trailingStack.alignment = .center
      trailingStack.spacing = 8
    } else if accessories.count == 2,
              accessories[0].string("kind") == "icon",
              accessories[0].string("name") == "PencilOutline",
              accessories[1].string("kind") == "icon",
              accessories[1].string("name") == "DragOutline" {
      trailingStack.axis = .horizontal
      trailingStack.alignment = .center
      // Each source IconButton has a 36-point frame and m=-7. XStack gap=$6
      // therefore places the physical frames 10 points apart (46-point
      // center distance), not 24 points apart.
      trailingStack.spacing = 10
    }
    bindAccessories(item, accessories, theme, checkboxState)
  }

  private func bindRail(_ item: NativeListItem, theme: [String: Any]?) {
    rootStack.spacing = 6
    rootLeadingConstraint.constant = 4
    rootTrailingConstraint.constant = -4
    rootTopConstraint.constant = 4
    rootBottomConstraint.constant = -4
    leadingWidth.constant = 20
    leadingHeight.constant = 20
    addLeading(item.data.dictionary("visual"), key: item.key)
    show(titleLabel, item.data.string("title"), lines: 1)
    titleLabel.font = nativeListFont(ofSize: 12, weight: .medium)
    setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 16)
    mainStack.axis = .horizontal
    mainStack.alignment = .center
    mainStack.spacing = 6
    mainStack.setContentHuggingPriority(.required, for: .horizontal)
    mainStack.setContentCompressionResistancePriority(.required, for: .horizontal)
    titleRowStack.setContentHuggingPriority(.required, for: .horizontal)
    titleRowStack.setContentCompressionResistancePriority(.required, for: .horizontal)
    titleLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
    if let badge = item.data.dictionary("badge") {
      show(badgeLabel, badge.string("text"), lines: 1)
      badgeLabel.font = nativeListTabularFont(ofSize: 12, weight: .medium)
      badgeLabel.textColor = badge.string("tone") == "success"
        ? nativeListColor(theme, "positive", "#218358")
        : badge.string("tone") == "danger"
          ? nativeListColor(theme, "negative", "#CE2C31")
          : nativeListColor(theme, "secondaryText", "#646464")
      setLineHeight(badgeLabel, text: badge.string("text"), lineHeight: 16)
    }
    let status = item.data.string("status")
    if !status.isEmpty, status != "none" {
      show(statusLabel, status, lines: 1)
      statusLabel.font = nativeListTabularFont(ofSize: 12)
      setLineHeight(statusLabel, text: status, lineHeight: 16)
    }
    rootStack.addArrangedSubview(mainStack)
    contentView.layer.cornerRadius = 8
    contentView.clipsToBounds = true
  }

  private func bindActivity(_ item: NativeListItem, theme: [String: Any]?) {
    addLeading(
      item.data.dictionary("leading"),
      secondaryVisual: item.data.dictionary("secondaryLeading"),
      key: item.key
    )
    rootStack.addArrangedSubview(mainStack)
    show(titleLabel, item.data.string("title"), lines: 1)
    show(subtitleLabel, item.data.string("description"), lines: 2)
    mainStack.spacing = 0
    setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 24)
    setLineHeight(subtitleLabel, text: item.data.string("description"), lineHeight: 20)
    if item.data.string("status") == "Failed" {
      show(badgeLabel, "  Failed  ", lines: 1)
      badgeLabel.textColor = nativeListColor(theme, "negative", "#CE2C31")
      badgeLabel.backgroundColor = nativeListColor(
        theme,
        "criticalBackground",
        "#F3000D14"
      )
      badgeLabel.layer.cornerRadius = 4
      badgeLabel.clipsToBounds = true
    } else {
      show(statusLabel, item.data.string("status"), lines: 1)
    }
    rootStack.addArrangedSubview(trailingStack)
    showAccessory(0, item.data.string("primaryAmount"))
    showAccessory(1, item.data.string("secondaryAmount"))
    let primaryAmountColor = item.data.string("primaryAmount").hasPrefix("+")
      ? nativeListColor(theme, "positive", "#218358")
      : nativeListColor(theme, "primaryText", "#202020")
    setButtonLine(
      accessoryButtons[0],
      text: item.data.string("primaryAmount"),
      font: nativeListTabularFont(ofSize: 16, weight: .medium),
      color: primaryAmountColor,
      lineHeight: 24
    )
    setButtonLine(
      accessoryButtons[1],
      text: item.data.string("secondaryAmount"),
      font: nativeListTabularFont(ofSize: 14),
      color: nativeListColor(theme, "secondaryText", "#646464"),
      lineHeight: 20
    )
    let actions = item.data.dictionaries("footerActions").prefix(3)
    if !actions.isEmpty {
      actionStack.isHidden = false
      for (index, action) in actions.enumerated() {
        let button = actionButtons[index]
        button.isHidden = false
        button.isEnabled = !action.bool("disabled")
        button.setTitle(action.string("label"), for: .normal)
        button.backgroundColor = nativeListColor(theme, "strongBackground", "#F0F0F0")
        button.setTitleColor(
          action.string("tone") == "danger"
            ? nativeListColor(theme, "negative", "#CE2C31")
            : nativeListColor(theme, "primaryText", "#202020"),
          for: .normal
        )
        footerActionKeys.append(action.string("key"))
      }
    }
  }

  private func bindMessage(_ item: NativeListItem, theme: [String: Any]?) {
    rootStack.alignment = .top
    rootTopConstraint.constant = 16
    rootBottomConstraint.constant = -16
    leadingWidth.constant = 28
    leadingHeight.constant = 28
    if let leading = item.data.dictionary("leading") { addLeading(leading, key: item.key) }
    unreadDot.isHidden = !item.data.bool("unread")
    rootStack.addArrangedSubview(mainStack)
    show(titleLabel, item.data.string("title"), lines: 2)
    titleLabel.font = nativeListFont(ofSize: 14, weight: .semibold)
    setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 20)
    show(
      subtitleLabel,
      item.data.string("body"),
      lines: min(3, max(1, item.data.int("bodyLines", default: 3)))
    )
    subtitleLabel.font = nativeListFont(ofSize: 14)
    setLineHeight(subtitleLabel, text: item.data.string("body"), lineHeight: 20)
    show(statusLabel, item.data.string("time"), lines: 1)
    statusLabel.font = nativeListFont(ofSize: 12)
    statusLabel.textColor = nativeListColor(theme, "disabledText", "#8D8D8D")
    statusLabel.topInset = 2
    setLineHeight(statusLabel, text: item.data.string("time"), lineHeight: 16)
    if let thumbnail = item.data.dictionary("thumbnail") {
      rootStack.addArrangedSubview(secondaryImage)
      secondaryWidth.constant = 64
      secondaryHeight.constant = 64
      secondaryWidth.isActive = true
      secondaryHeight.isActive = true
      secondaryImage.layer.borderWidth = 1 / UIScreen.main.scale
      secondaryImage.layer.borderColor = UIColor(
        nativeListHex: "#0000000F",
        fallback: .lightGray
      ).cgColor
      bindImage(thumbnail, into: secondaryImage, token: item.key, slot: 0, variant: "generic")
    }
  }

  private func bindDataRow(
    _ item: NativeListItem,
    theme: [String: Any]?,
    _ checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    if item.data.bool("favorite") || item.data.bool("favoriteActive") {
      favoriteIconImageView.isHidden = false
      let favoriteActive = item.data.bool("favoriteActive")
      favoriteIconImageView.image = nativeListIcon(
        named: favoriteActive ? "StarSolid" : "StarOutline"
      )
      favoriteIconImageView.tintColor = nativeListColor(
        theme,
        favoriteActive ? "icon" : "iconSubdued",
        favoriteActive ? "#646464" : "#8D8D8D"
      )
      rootStack.addArrangedSubview(favoriteIconImageView)
      if currentLayout == "table" {
        rootStack.setCustomSpacing(8, after: favoriteIconImageView)
      }
    }
    rootStack.spacing = currentLayout == "table" ? 10 : 8
    if let leading = item.data.dictionary("leading") {
      leadingWidth.constant = 40
      leadingHeight.constant = 40
      addLeading(leading, key: item.key)
    }
    var hasLeadingAccessory = false
    if let checkbox = item.data.dictionary("checkbox") {
      bindCheckbox(item, checkbox, checkboxState)
      hasLeadingAccessory = true
    }
    if item.data["index"] != nil {
      showAccessory(0, String(item.data.int("index")))
      hasLeadingAccessory = true
    }
    if hasLeadingAccessory { rootStack.addArrangedSubview(trailingStack) }
    let columns = Array(item.data.dictionaries("columns").prefix(4))
    let rowBadges = item.data.dictionaries("badges")
    if currentLayout == "table" {
      rootStack.addArrangedSubview(tableDataStack)
      for (index, column) in columns.enumerated() {
        tableDataColumns[index].isHidden = false
        tableDataColumns[index].bind(
          column: column,
          badges: index == 0 ? rowBadges : [],
          theme: theme
        )
      }
      if let firstColumn = columns.first {
        let firstWeight = CGFloat(max(1, firstColumn.int("weight", default: 1)))
        for index in 1..<columns.count {
          let weight = CGFloat(max(1, columns[index].int("weight", default: 1)))
          dataWeightConstraints.append(
            tableDataColumns[index].widthAnchor.constraint(
              equalTo: tableDataColumns[0].widthAnchor,
              multiplier: weight / firstWeight
            )
          )
        }
        NSLayoutConstraint.activate(dataWeightConstraints)
      }
      return
    }

    rootStack.addArrangedSubview(dataStack)
    for (index, column) in columns.enumerated() {
      let label = dataLabels[index]
      label.isHidden = false
      let primaryColor = dataTextColor(column.string("tone"), theme: theme)
      let attributed = NSMutableAttributedString(
        string: column.string("text"),
        attributes: [
          .font: nativeListFont(ofSize: 14, weight: .medium),
          .foregroundColor: primaryColor,
        ]
      )
      if index == 0 {
        for badge in rowBadges.prefix(2) {
          attributed.append(NSAttributedString(
            string: "  \(badge.string("text")) ",
            attributes: [
              .font: nativeListFont(ofSize: 12, weight: .medium),
              .foregroundColor: nativeListColor(theme, "info", "#0D74CE"),
              .backgroundColor: UIColor(nativeListHex: "#008FF519", fallback: .systemBlue),
            ]
          ))
        }
      }
      let secondaryText = column.string("secondaryText")
      if !secondaryText.isEmpty {
        attributed.append(NSAttributedString(
          string: "\n\(secondaryText)",
          attributes: [
            .font: nativeListFont(ofSize: 12),
            .foregroundColor: dataTextColor(
              column.string("secondaryTone", default: "secondary"),
              theme: theme
            ),
          ]
        ))
        label.numberOfLines = 2
      }
      label.attributedText = attributed
      label.textAlignment = column.string("alignment") == "end" ? .right : column.string("alignment") == "center" ? .center : .left
      label.setContentHuggingPriority(.defaultLow, for: .horizontal)
    }
    if let firstColumn = columns.first {
      let firstWeight = CGFloat(max(1, firstColumn.int("weight", default: 1)))
      for index in 1..<columns.count {
        let weight = CGFloat(max(1, columns[index].int("weight", default: 1)))
        dataWeightConstraints.append(
          dataLabels[index].widthAnchor.constraint(
            equalTo: dataLabels[0].widthAnchor,
            multiplier: weight / firstWeight
          )
        )
      }
      NSLayoutConstraint.activate(dataWeightConstraints)
    }
  }

  private func marketFontWeight(_ value: String, fallback: NativeListFontWeight) -> NativeListFontWeight {
    switch value {
    case "regular": return .regular
    case "semibold": return .semibold
    case "bold": return .bold
    case "medium": return .medium
    default: return fallback
    }
  }

  private func marketTextAlignment(_ value: String) -> NSTextAlignment {
    switch value {
    case "center": return .center
    case "start": return effectiveUserInterfaceLayoutDirection == .rightToLeft ? .right : .left
    case "end": return effectiveUserInterfaceLayoutDirection == .rightToLeft ? .left : .right
    default: return .natural
    }
  }

  private func applyMarketTextStyle(
    _ label: UILabel,
    data: [String: Any]?,
    theme: [String: Any]?,
    defaultSize: CGFloat,
    defaultLineHeight: CGFloat,
    defaultWeight: NativeListFontWeight,
    defaultColor: UIColor,
    defaultAlignment: NSTextAlignment = .natural
  ) {
    let size = CGFloat(data?.double("fontSize", default: Double(defaultSize)) ?? Double(defaultSize))
    let lineHeight = CGFloat(data?.double("lineHeight", default: Double(defaultLineHeight)) ?? Double(defaultLineHeight))
    label.font = nativeListTabularFont(ofSize: size, weight: marketFontWeight(data?.string("fontWeight") ?? "", fallback: defaultWeight))
    label.textColor = data?["color"].flatMap { $0 as? String }.map { UIColor(nativeListHex: $0, fallback: defaultColor) } ?? defaultColor
    label.textAlignment = data?["alignment"] == nil
      ? defaultAlignment
      : marketTextAlignment(data?.string("alignment") ?? "")
    label.numberOfLines = min(2, max(1, data?.int("lines", default: 1) ?? 1))
    setLineHeight(label, text: label.text ?? "", lineHeight: lineHeight)
  }

  private func applyMarketButtonStyle(
    _ button: UIButton,
    data: [String: Any]?,
    defaultSize: CGFloat,
    defaultLineHeight: CGFloat,
    defaultWeight: NativeListFontWeight,
    color: UIColor,
    defaultAlignment: UIControl.ContentHorizontalAlignment
  ) {
    let size = CGFloat(data?.double("fontSize", default: Double(defaultSize)) ?? Double(defaultSize))
    let lineHeight = CGFloat(data?.double("lineHeight", default: Double(defaultLineHeight)) ?? Double(defaultLineHeight))
    setButtonLine(
      button,
      text: button.title(for: .normal) ?? "",
      font: nativeListTabularFont(ofSize: size, weight: marketFontWeight(data?.string("fontWeight") ?? "", fallback: defaultWeight)),
      color: data?["color"].flatMap { $0 as? String }.map { UIColor(nativeListHex: $0, fallback: color) } ?? color,
      lineHeight: lineHeight
    )
    if data?["alignment"] == nil {
      button.contentHorizontalAlignment = defaultAlignment
    } else {
      button.contentHorizontalAlignment = data?.string("alignment") == "start"
        ? .leading
        : data?.string("alignment") == "end" ? .trailing : .center
    }
    button.titleLabel?.numberOfLines = min(2, max(1, data?.int("lines", default: 1) ?? 1))
  }

  private func marketAttributedText(
    _ text: String,
    segments: [[String: Any]],
    style: [String: Any]?,
    defaultSize: CGFloat,
    defaultLineHeight: CGFloat,
    defaultWeight: NativeListFontWeight,
    color: UIColor,
    defaultAlignment: NSTextAlignment
  ) -> NSAttributedString {
    let size = CGFloat(style?.double("fontSize", default: Double(defaultSize)) ?? Double(defaultSize))
    let lineHeight = CGFloat(style?.double("lineHeight", default: Double(defaultLineHeight)) ?? Double(defaultLineHeight))
    let paragraph = NSMutableParagraphStyle()
    paragraph.minimumLineHeight = lineHeight
    paragraph.maximumLineHeight = lineHeight
    paragraph.alignment = style?["alignment"] == nil
      ? defaultAlignment
      : marketTextAlignment(style?.string("alignment") ?? "")
    let weight = marketFontWeight(style?.string("fontWeight") ?? "", fallback: defaultWeight)
    let resolvedColor = style?["color"].flatMap { $0 as? String }.map { UIColor(nativeListHex: $0, fallback: color) } ?? color
    let result = NSMutableAttributedString(string: "")
    let source = segments.isEmpty ? [["text": text]] : segments
    for segment in source {
      let segmentSize = segment.string("style") == "subscript" ? ceil(size * 0.6) : size
      result.append(NSAttributedString(string: segment.string("text"), attributes: [
        .font: nativeListTabularFont(ofSize: segmentSize, weight: weight),
        .foregroundColor: resolvedColor,
        .kern: 0,
        .paragraphStyle: paragraph,
        .baselineOffset: max(0, (lineHeight - nativeListTabularFont(ofSize: size, weight: weight).lineHeight) / 2),
      ]))
    }
    return result
  }

  private func marketLeading(_ item: NativeListItem, style: [String: Any]?) -> [String: Any]? {
    guard var visual = item.data.dictionary("leading") else { return nil }
    guard let imageStyle = style?.dictionary("image") else { return visual }
    if let shape = imageStyle["shape"] as? String { visual["shape"] = shape }
    if let contentFit = imageStyle["contentFit"] as? String, var image = visual.dictionary("image") {
      image["contentFit"] = contentFit
      visual["image"] = image
    }
    return visual
  }

  private func bindMarket(_ item: NativeListItem, theme: [String: Any]?) {
    let variant = item.data.string("variant")
    let style = item.data.dictionary("style")
    let imageStyle = style?.dictionary("image")
    let imageWidth = CGFloat(imageStyle?.double("width", default: variant == "stock" ? 40 : 32) ?? (variant == "stock" ? 40 : 32))
    let imageHeight = CGFloat(imageStyle?.double("height", default: variant == "stock" ? 40 : 32) ?? (variant == "stock" ? 40 : 32))
    let horizontalPadding = CGFloat(style?.double("horizontalPadding", default: variant == "perp" ? 16 : 20) ?? (variant == "perp" ? 16 : 20))
    let verticalPadding = CGFloat(style?.double("verticalPadding", default: 12) ?? 12)
    rootLeadingConstraint.constant = horizontalPadding
    rootTrailingConstraint.constant = -horizontalPadding
    rootTopConstraint.constant = verticalPadding
    rootBottomConstraint.constant = -verticalPadding
    rootStack.spacing = CGFloat(style?.double("leadingGap", default: variant == "perp" ? 8 : 14) ?? (variant == "perp" ? 8 : 14))
    if let leadingAction = item.data.dictionary("leadingAction") {
      leadingActionButton.isHidden = false
      let tintColor = UIColor(
        nativeListHex: leadingAction.string("tintColor", default: "#646464"),
        fallback: .darkGray
      )
      leadingActionButton.tintColor = tintColor
      if let image = nativeListIcon(named: leadingAction.string("name")) {
        leadingActionButton.setImage(image, for: .normal)
        leadingActionButton.setImage(
          image.withTintColor(tintColor, renderingMode: .alwaysOriginal),
          for: .disabled
        )
      }
      leadingActionButton.isEnabled = !leadingAction.bool("disabled")
      leadingActionButton.alpha = leadingActionButton.isEnabled ? 1 : 0.4
      leadingActionButton.accessibilityIdentifier = leadingAction["testID"] as? String
      leadingActionButton.accessibilityLabel = leadingAction["accessibilityLabel"] as? String
      leadingActionKey = leadingAction.string("actionKey")
      rootStack.addArrangedSubview(leadingActionButton)
      rootStack.setCustomSpacing(5, after: leadingActionButton)
    }
    leadingWidth.constant = imageWidth
    leadingHeight.constant = imageHeight
    if let visual = marketLeading(item, style: style) {
      addLeading(visual, key: item.key)
      let radius = CGFloat(imageStyle?.double("cornerRadius", default: imageStyle?.string("shape") == "square" ? 0 : imageStyle?.string("shape") == "rounded" ? 8 : Double(min(imageWidth, imageHeight) / 2)) ?? Double(min(imageWidth, imageHeight) / 2))
      leadingContainer.layer.cornerRadius = radius
      leadingImages.first?.layer.cornerRadius = radius
      if let image = leadingImages.first, !visual.string("borderColor").isEmpty {
        // Match Token's border box: the bitmap occupies the one-point inset.
        leadingContainer.layer.borderWidth = 1
        leadingContainer.layer.borderColor = UIColor(nativeListHex: visual.string("borderColor"), fallback: .clear).cgColor
        for constraint in leadingSlotConstraints where constraint.firstItem === image {
          switch constraint.firstAttribute {
          case .leading, .top: constraint.constant = 1
          case .trailing, .bottom: constraint.constant = -1
          default: break
          }
        }
        image.layer.cornerRadius = 0
        let mask = CAShapeLayer()
        mask.path = UIBezierPath(roundedRect: CGRect(x: -1, y: -1, width: imageWidth, height: imageHeight), cornerRadius: radius).cgPath
        image.layer.mask = mask
      }
      if let diagnostic = item.data.dictionary("diagnostics")?.string("imageBindActionKey"), !diagnostic.isEmpty,
         visual.dictionary("image") != nil || visual.dictionary("networkImage") != nil {
        onAction?(item, diagnostic, nil, nil)
      }
    }
    rootStack.addArrangedSubview(mainStack)
    // OneKey patch: opt in to the source Market row's content gap.
    // rootStack.setCustomSpacing(0, after: mainStack)
    rootStack.setCustomSpacing(CGFloat(style?.double("contentTrailingGap", default: 0) ?? 0), after: mainStack)
    mainStack.spacing = CGFloat(style?.double("lineGap", default: 0) ?? 0)
    titleRowStack.spacing = CGFloat(style?.double("titleBadgeGap", default: 4) ?? 4)
    // OneKey patch: opt in without changing the other row templates' filled layout.
    if style?.string("titleBadgeLayout") == "inline" {
      mainStack.alignment = .leading
      titleRowStack.setContentHuggingPriority(.required, for: .horizontal)
    }
    show(titleLabel, item.data.string("title"), lines: style?.dictionary("title")?.int("lines", default: 1) ?? 1)
    applyMarketTextStyle(titleLabel, data: style?.dictionary("title"), theme: theme, defaultSize: 16, defaultLineHeight: 24, defaultWeight: .medium, defaultColor: nativeListColor(theme, "primaryText", "#202020"))
    let badges = Array(item.data.dictionaries("badges").prefix(marketBadgeButtons.count))
    marketBadgeActionKeys = badges.map { $0["actionKey"] as? String }
    for (index, badge) in badges.enumerated() {
      let button = marketBadgeButtons[index]
      let badgeStyle = badge.dictionary("style")
      let badgeFontSize = CGFloat(badgeStyle?.double("fontSize", default: 11) ?? 11)
      let badgeFontWeight = marketFontWeight(badgeStyle?.string("fontWeight") ?? "", fallback: .medium)
      // OneKey patch: SizableText supplies tabular numerals for explicit Market metrics.
      let badgeFont = badgeStyle == nil
        ? nativeListFont(ofSize: badgeFontSize, weight: badgeFontWeight)
        : nativeListTabularFont(ofSize: badgeFontSize, weight: badgeFontWeight)
      let hasBuiltInIcon = badge.string("iconName") == "verified"
      let hasRemoteIcon = badge.dictionary("icon") != nil
      let hasIcon = hasBuiltInIcon || hasRemoteIcon
      let text = badge.string("text")
      let toneColor: UIColor
      switch badge.string("tone") {
      case "success": toneColor = nativeListColor(theme, "positive", "#218358")
      case "danger": toneColor = nativeListColor(theme, "negative", "#CE2C31")
      case "info": toneColor = nativeListColor(theme, "info", "#0D74CE")
      case "warning": toneColor = nativeListColor(theme, "primaryText", "#202020")
      default: toneColor = nativeListColor(theme, "secondaryText", "#646464")
      }
      let foreground = UIColor(
        nativeListHex: badge.string("textColor", default: ""),
        fallback: toneColor
      )
      button.isHidden = false
      button.isEnabled = true
      button.isUserInteractionEnabled = !badge.string("actionKey").isEmpty
      button.accessibilityTraits = button.isUserInteractionEnabled ? .button : .staticText
      button.setTitle(text, for: .normal)
      button.setTitleColor(foreground, for: .normal)
      button.titleLabel?.font = badgeFont
      if let lineHeight = badgeStyle?["lineHeight"] as? Double {
        setButtonLine(button, text: text, font: badgeFont, color: foreground, lineHeight: CGFloat(lineHeight))
        // The explicit text-only line box must not cover an adjacent icon.
        if hasIcon { button.marketLineHeight = nil }
      }
      button.tintColor = foreground
      button.backgroundColor = UIColor(
        nativeListHex: badge.string("backgroundColor", default: ""),
        fallback: hasIcon && text.isEmpty
          ? .clear
          : nativeListColor(theme, "strongBackground", "#0000000F")
      )
      let iconOnly = hasIcon && text.isEmpty
      let iconSize: CGFloat = hasBuiltInIcon ? 16 : 14
      // OneKey patch: preserve the native defaults unless the caller supplies padding.
      let padding = CGFloat(badgeStyle?.double("horizontalPadding", default: 5) ?? 5)
      let hasCustomPadding = badgeStyle?["horizontalPadding"] != nil
      let leftPadding = hasCustomPadding ? padding + (hasRemoteIcon ? iconSize + 2 : 0) : hasRemoteIcon ? 20 : hasIcon ? 3 : 5
      // button.contentEdgeInsets = UIEdgeInsets(top: 0, left: iconOnly ? 0 : hasRemoteIcon ? 20 : hasIcon ? 3 : 5, bottom: 0, right: iconOnly ? 0 : 5)
      button.contentEdgeInsets = UIEdgeInsets(top: 0, left: iconOnly ? 0 : leftPadding, bottom: 0, right: iconOnly ? 0 : padding)
      button.imageEdgeInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: text.isEmpty ? 0 : 3)
      button.titleEdgeInsets = .zero
      button.imageView?.contentMode = .scaleAspectFit
      if hasBuiltInIcon {
        button.setImage(
          nativeListIcon(named: "BadgeVerifiedSolid", size: CGSize(width: iconSize, height: iconSize)),
          for: .normal
        )
      }
      // OneKey patch: match source badge metrics at physical-pixel precision.
      // let height = button.heightAnchor.constraint(equalToConstant: 18)
      let height = button.heightAnchor.constraint(equalToConstant: CGFloat(badgeStyle?.double("height", default: 18) ?? 18))
      let textWidth = (text as NSString).size(withAttributes: [.font: badgeFont]).width
      let scale = max(1, traitCollection.displayScale)
      let roundedTextWidth = badgeStyle == nil ? ceil(textWidth) : ceil(textWidth * scale) / scale
      let extraWidth = hasCustomPadding ? padding * 2 + (hasIcon ? iconSize + 2 : 0) : hasIcon ? iconSize + 11 : 10
      // let width = button.widthAnchor.constraint(equalToConstant: iconOnly ? iconSize : ceil(textWidth) + (hasIcon ? iconSize + 11 : 10))
      let width = button.widthAnchor.constraint(equalToConstant: iconOnly ? iconSize : roundedTextWidth + extraWidth)
      NSLayoutConstraint.activate([width, height])
      selectorConstraints.append(contentsOf: [width, height])
      if let icon = badge.dictionary("icon") {
        marketBadgeImages[index].isHidden = false
        marketBadgeImages[index].layer.cornerRadius = 7
        marketBadgeImages[index].clipsToBounds = true
        let imageLeading = marketBadgeImages[index].leadingAnchor.constraint(equalTo: button.leadingAnchor, constant: text.isEmpty ? 0 : 4)
        imageLeading.isActive = true
        selectorConstraints.append(imageLeading)
        bindImage(icon, into: marketBadgeImages[index], token: item.key, slot: 20 + index, variant: "generic")
      }
      button.accessibilityLabel = badge.string("accessibilityLabel", default: badge.string("text"))
    }
    if !item.data.string("subtitle").isEmpty || !item.data.dictionaries("subtitleSegments").isEmpty {
      show(subtitleLabel, item.data.string("subtitle"), lines: style?.dictionary("subtitle")?.int("lines", default: 1) ?? 1)
      applyMarketTextStyle(subtitleLabel, data: style?.dictionary("subtitle"), theme: theme, defaultSize: 14, defaultLineHeight: 20, defaultWeight: .regular, defaultColor: nativeListColor(theme, "secondaryText", "#646464"))
      if !item.data.dictionaries("subtitleSegments").isEmpty {
        subtitleLabel.attributedText = marketAttributedText(item.data.string("subtitle"), segments: item.data.dictionaries("subtitleSegments"), style: style?.dictionary("subtitle"), defaultSize: 14, defaultLineHeight: 20, defaultWeight: .regular, color: nativeListColor(theme, "secondaryText", "#646464"), defaultAlignment: .natural)
      }
    }
    // OneKey patch: preserve volume width while the localized name truncates.
    let subtitlePrefix = item.data.dictionary("subtitlePrefix")
    let subtitlePadding = CGFloat(style?.double("subtitleTrailingPadding", default: 0) ?? 0)
    if subtitlePrefix != nil || subtitlePadding > 0 {
      mainStack.removeArrangedSubview(subtitleLabel)
      subtitleLabel.removeFromSuperview()
      mainStack.removeArrangedSubview(tertiaryLabel)
      tertiaryLabel.removeFromSuperview()
      marketSubtitleStack.axis = .horizontal
      marketSubtitleStack.alignment = .center
      marketSubtitleStack.spacing = 0
      marketSubtitleStack.clipsToBounds = true
      show(tertiaryLabel, subtitlePrefix?.string("text") ?? "", lines: 1)
      applyMarketTextStyle(tertiaryLabel, data: subtitlePrefix?.dictionary("style"), theme: theme, defaultSize: 12, defaultLineHeight: 16, defaultWeight: .regular, defaultColor: nativeListColor(theme, "secondaryText", "#646464"))
      tertiaryLabel.setContentHuggingPriority(.required, for: .horizontal)
      tertiaryLabel.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
      subtitleLabel.setContentHuggingPriority(.required, for: .horizontal)
      subtitleLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
      marketSubtitleStack.addArrangedSubview(tertiaryLabel)
      marketSubtitleStack.addArrangedSubview(subtitleLabel)
      marketSubtitleStack.addArrangedSubview(marketSubtitleSpacer)
      if !tertiaryLabel.isHidden && !subtitleLabel.isHidden {
        marketSubtitleStack.setCustomSpacing(CGFloat(subtitlePrefix?.double("gap", default: 4) ?? 4), after: tertiaryLabel)
      }
      mainStack.insertArrangedSubview(marketSubtitleStack, at: 1)
      let width = marketSubtitleStack.widthAnchor.constraint(equalTo: mainStack.widthAnchor, constant: -subtitlePadding)
      width.isActive = true
      selectorConstraints.append(width)
      if let maxWidth = subtitlePrefix?["maxWidth"] as? Double {
        let limit = tertiaryLabel.widthAnchor.constraint(lessThanOrEqualToConstant: CGFloat(maxWidth))
        limit.isActive = true
        selectorConstraints.append(limit)
      }
      marketSubtitleStack.isHidden = tertiaryLabel.isHidden && subtitleLabel.isHidden
    }
    rootStack.addArrangedSubview(trailingStack)
    trailingStack.axis = .horizontal
    trailingStack.alignment = .center
    trailingStack.spacing = CGFloat(style?.double("trailingGap", default: 8) ?? 8)
    updateMarketQuote(item, theme: theme)
  }

  func updateMarketQuote(_ item: NativeListItem, theme: [String: Any]?) {
    guard currentItem?.key == item.key, item.type == "market" else { return }
    currentItem = item
    NSLayoutConstraint.deactivate(accessorySizeConstraints)
    accessorySizeConstraints.removeAll()
    let style = item.data.dictionary("style")
    let price = accessoryButtons[0]
    price.isUserInteractionEnabled = false
    price.isHidden = false
    price.setAttributedTitle(nil, for: .normal)
    price.setTitle(item.data.string("price"), for: .normal)
    applyMarketButtonStyle(price, data: style?.dictionary("price"), defaultSize: 16, defaultLineHeight: 24, defaultWeight: .medium, color: nativeListColor(theme, "primaryText", "#202020"), defaultAlignment: .trailing)
    if !item.data.dictionaries("priceSegments").isEmpty {
      price.setAttributedTitle(marketAttributedText(item.data.string("price"), segments: item.data.dictionaries("priceSegments"), style: style?.dictionary("price"), defaultSize: 16, defaultLineHeight: 24, defaultWeight: .medium, color: nativeListColor(theme, "primaryText", "#202020"), defaultAlignment: marketTextAlignment("end")), for: .normal)
    }
    let changeData = item.data.dictionary("change") ?? [:]
    let change = accessoryButtons[1]
    change.isUserInteractionEnabled = false
    change.isHidden = false
    change.setAttributedTitle(nil, for: .normal)
    change.setTitle(changeData.string("text"), for: .normal)
    let defaultChangeColor = nativeListColor(theme, "inverseText", "#FFFFFF")
    applyMarketButtonStyle(change, data: style?.dictionary("change"), defaultSize: 14, defaultLineHeight: 20, defaultWeight: .medium, color: UIColor(nativeListHex: changeData.string("textColor", default: "#FFFFFF"), fallback: defaultChangeColor), defaultAlignment: .center)
    if !changeData.dictionaries("textSegments").isEmpty {
      let changeTextColor = UIColor(nativeListHex: changeData.string("textColor", default: ""), fallback: defaultChangeColor)
      change.setAttributedTitle(marketAttributedText(changeData.string("text"), segments: changeData.dictionaries("textSegments"), style: style?.dictionary("change"), defaultSize: 14, defaultLineHeight: 20, defaultWeight: .medium, color: changeTextColor, defaultAlignment: .center), for: .normal)
    }
    let toneKey = changeData.string("tone") == "positive" ? "positive" : changeData.string("tone") == "negative" ? "negative" : "secondaryText"
    change.backgroundColor = UIColor(nativeListHex: changeData.string("backgroundColor", default: ""), fallback: nativeListColor(theme, toneKey, changeData.string("tone") == "positive" ? "#218358" : changeData.string("tone") == "negative" ? "#CE2C31" : "#8D8D8D"))
    change.layer.cornerRadius = CGFloat(style?.double("changeCornerRadius", default: 8) ?? 8)
    change.clipsToBounds = true
    let width = change.widthAnchor.constraint(equalToConstant: CGFloat(style?.double("changeWidth", default: 80) ?? 80))
    let height = change.heightAnchor.constraint(equalToConstant: CGFloat(style?.double("changeHeight", default: 32) ?? 32))
    width.isActive = true
    height.isActive = true
    accessorySizeConstraints.append(contentsOf: [width, height])
    accessibilityLabel = item.data.string("accessibilityLabel", default: [item.data.string("title"), item.data.string("subtitle"), item.data.string("price"), changeData.string("text")].filter { !$0.isEmpty }.joined(separator: ", "))
  }

  private func bindMediaTile(_ item: NativeListItem, theme: [String: Any]?) {
    rootStack.axis = .vertical
    rootStack.alignment = .fill
    rootStack.spacing = 8
    rootLeadingConstraint.constant = 10
    rootTrailingConstraint.constant = -10
    rootTopConstraint.constant = 10
    rootBottomConstraint.constant = -10
    leadingWidth.isActive = false
    leadingHeight.isActive = false
    mediaHeight.isActive = true
    let imageState = item.data.string("imageState")
    let imageVisual = imageState == "empty" || imageState == "error"
      ? nil
      : item.data.dictionary("image").map { ["kind": "image", "image": $0] }
    addLeading(imageVisual, key: item.key, to: rootStack)
    leadingContainer.layer.cornerRadius = 10
    leadingContainer.clipsToBounds = true
    if imageState == "empty" {
      leadingContainer.backgroundColor = .clear
      fallbackLabel.isHidden = true
    } else if imageState == "error" {
      leadingContainer.backgroundColor = nativeListColor(theme, "strongBackground", "#0000000F")
      fallbackLabel.isHidden = true
      leadingIconWidth.constant = 24
      leadingIconHeight.constant = 24
      leadingIconImageView.image = nativeListIcon(named: "ImageSquareWavesOutline")
      leadingIconImageView.tintColor = UIColor(nativeListHex: "#00000044", fallback: .lightGray)
      leadingIconImageView.isHidden = false
    }
    mainStack.removeArrangedSubview(titleRowStack)
    titleRowStack.removeFromSuperview()
    mainStack.removeArrangedSubview(subtitleLabel)
    subtitleLabel.removeFromSuperview()
    mediaMetadataStack.axis = .horizontal
    mediaMetadataStack.alignment = .center
    mediaMetadataStack.spacing = 8
    mediaMetadataStack.addArrangedSubview(subtitleLabel)
    subtitleLabel.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
    if let networkImage = item.data.dictionary("networkImage") {
      mediaMetadataStack.addArrangedSubview(mediaNetworkImage)
      // OneKey patch: Preserve badge layout without exposing a loading/error tile.
      // mediaNetworkImage.isHidden = false
      bindImage(
        networkImage,
        into: mediaNetworkImage,
        token: item.key,
        slot: 2,
        variant: "network",
        hideUntilLoaded: true
      )
    }
    mainStack.insertArrangedSubview(mediaMetadataStack, at: 0)
    mainStack.insertArrangedSubview(titleRowStack, at: 1)
    show(subtitleLabel, item.data.string("subtitle"), lines: 1)
    subtitleLabel.font = nativeListFont(ofSize: 12)
    show(titleLabel, item.data.string("title"), lines: 1)
    titleLabel.font = nativeListFont(ofSize: 16, weight: .medium)
    if let badge = item.data.dictionary("badge") {
      mediaBadgeLabel.text = "  \(badge.string("text"))  "
      mediaBadgeLabel.isHidden = false
    }
    rootStack.addArrangedSubview(mainStack)
    let closeAction = item.data.string("closeActionKey")
    if !closeAction.isEmpty {
      showAccessory(0, "×", action: (closeAction, nil))
      rootStack.addArrangedSubview(trailingStack)
    }
  }

  private func bindMetricCard(_ item: NativeListItem, theme: [String: Any]?) {
    rootStack.axis = .vertical
    rootStack.alignment = .leading
    rootStack.spacing = 4
    rootLeadingConstraint.constant = 14
    rootTrailingConstraint.constant = -14
    rootTopConstraint.constant = 14
    rootBottomConstraint.constant = -14
    let variant = item.data.string("variant", default: "standard")
    if variant == "activity" || variant == "performance" {
      bindCompositeMetricCard(item, theme: theme, variant: variant)
      return
    }
    if let visual = item.data.dictionary("visual") {
      leadingWidth.constant = 32
      leadingHeight.constant = 32
      addLeading(visual, key: item.key, to: rootStack)
      leadingContainer.clipsToBounds = true
    }
    show(subtitleLabel, item.data.string("title"), lines: 1)
    subtitleLabel.font = nativeListFont(ofSize: 11)
    subtitleLabel.textColor = nativeListColor(theme, "disabledText", "#8D8D8D")
    show(titleLabel, item.data.string("value"), lines: 1)
    titleLabel.font = nativeListFont(
      ofSize: item.data.string("size") == "large" ? 24 : 18,
      weight: .semibold
    )
    show(statusLabel, item.data.string("trend"), lines: 1)
    let tone = item.data.string("trendTone", default: "neutral")
    if tone == "positive" {
      statusLabel.textColor = nativeListColor(theme, "positive", "#218358")
    } else if tone == "negative" {
      statusLabel.textColor = nativeListColor(theme, "negative", "#CE2C31")
    }
    show(metricSubtitleLabel, item.data.string("subtitle"), lines: 1)
    if let badge = item.data.dictionary("badge") {
      show(badgeLabel, badge.string("text"), lines: 1)
    }
    rootStack.addArrangedSubview(mainStack)
    contentView.layer.cornerRadius = 12
    contentView.clipsToBounds = true
  }

  private func bindCompositeMetricCard(
    _ item: NativeListItem,
    theme: [String: Any]?,
    variant: String
  ) {
    rootStack.alignment = .fill
    show(titleLabel, item.data.string("title"), lines: 1)
    titleLabel.font = nativeListFont(ofSize: 11)
    titleLabel.textColor = nativeListColor(theme, "disabledText", "#8D8D8D")
    setLineHeight(
      titleLabel,
      text: item.data.string("title").uppercased(),
      lineHeight: 14,
      letterSpacing: 1.2
    )
    mainStack.spacing = 14
    metricCompositeStack.spacing = 14
    mainStack.addArrangedSubview(metricCompositeStack)
    rootStack.addArrangedSubview(mainStack)

    let metrics = item.data.dictionaries("metrics")
    let topCount = min(2, metrics.count)
    if variant == "activity" {
      metricCompositeStack.addArrangedSubview(
        makeMetricRow(
          Array(metrics.prefix(topCount)),
          theme: theme,
          style: "activityHero"
        )
      )
      let divider = UIView()
      divider.backgroundColor = nativeListColor(theme, "separator", "#E0E0E0")
      divider.heightAnchor.constraint(equalToConstant: 1 / UIScreen.main.scale).isActive = true
      metricCompositeStack.addArrangedSubview(divider)
      metricCompositeStack.addArrangedSubview(
        makeMetricRow(
          Array(metrics.dropFirst(topCount)),
          theme: theme,
          style: "compact"
        )
      )
    } else {
      let performanceSummary = UIStackView()
      performanceSummary.axis = .vertical
      performanceSummary.alignment = .fill
      performanceSummary.spacing = 8
      performanceSummary.addArrangedSubview(
        makeMetricRow(
          Array(metrics.prefix(topCount)),
          theme: theme,
          style: "performanceHero"
        )
      )
      let progress = min(1, max(0, item.data.double("progress")))
      let progressRow = UIStackView()
      progressRow.axis = .horizontal
      progressRow.spacing = 0
      progressRow.layer.cornerRadius = 2
      progressRow.clipsToBounds = true
      let wins = UIView()
      wins.backgroundColor = nativeListColor(theme, "positive", "#218358")
      let losses = UIView()
      losses.backgroundColor = nativeListColor(theme, "negative", "#CE2C31")
      progressRow.addArrangedSubview(wins)
      progressRow.addArrangedSubview(losses)
      progressRow.heightAnchor.constraint(equalToConstant: 4).isActive = true
      if progress <= 0 {
        wins.isHidden = true
      } else if progress >= 1 {
        losses.isHidden = true
      } else {
        wins.widthAnchor.constraint(
          equalTo: progressRow.widthAnchor,
          multiplier: CGFloat(progress)
        ).isActive = true
      }
      performanceSummary.addArrangedSubview(progressRow)
      metricCompositeStack.addArrangedSubview(performanceSummary)
      metricCompositeStack.addArrangedSubview(
        makeMetricRow(
          Array(metrics.dropFirst(topCount)),
          theme: theme,
          style: "compactShaded"
        )
      )
    }
    contentView.layer.cornerRadius = 12
    contentView.clipsToBounds = true
  }

  private func makeMetricRow(
    _ metrics: [[String: Any]],
    theme: [String: Any]?,
    style: String
  ) -> UIStackView {
    let shaded = style == "compactShaded"
    let row = UIStackView()
    row.axis = .horizontal
    row.alignment = style.hasSuffix("Hero") ? .bottom : .fill
    row.distribution = .fillEqually
    row.spacing = shaded ? 8 : 12
    metrics.enumerated().forEach { index, metric in
      let column = UIStackView()
      column.axis = .vertical
      column.alignment = shaded
        ? .leading
        : index == 0 ? .leading : index == metrics.count - 1 ? .trailing : .center
      column.spacing = 2
      if shaded {
        column.isLayoutMarginsRelativeArrangement = true
        column.layoutMargins = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 10)
        column.backgroundColor = UIColor(nativeListHex: "#0000000F", fallback: .lightGray)
        column.layer.cornerRadius = 8
      }
      let label = UILabel()
      label.font = nativeListFont(ofSize: 11)
      label.textColor = nativeListColor(theme, "disabledText", "#8D8D8D")
      setLineHeight(label, text: metric.string("label"), lineHeight: 14)
      let value = UILabel()
      let valueSize: CGFloat
      let valueLineHeight: CGFloat
      let valueWeight: NativeListFontWeight
      switch style {
      case "activityHero" where index == 0:
        valueSize = 16
        valueLineHeight = 24
        valueWeight = .semibold
      case "performanceHero" where index == 0:
        valueSize = 18
        valueLineHeight = 24
        valueWeight = .semibold
      case "activityHero", "performanceHero":
        valueSize = 14
        valueLineHeight = 20
        valueWeight = .semibold
      default:
        valueSize = 14
        valueLineHeight = 20
        valueWeight = .medium
      }
      value.font = nativeListTabularFont(ofSize: valueSize, weight: valueWeight)
      value.textColor = dataTextColor(metric.string("tone"), theme: theme)
      setLineHeight(value, text: metric.string("value"), lineHeight: valueLineHeight)
      column.addArrangedSubview(label)
      if let visual = metric.dictionary("visual") {
        let valueRow = UIStackView()
        valueRow.axis = .horizontal
        valueRow.alignment = .center
        valueRow.spacing = 6
        valueRow.addArrangedSubview(
          makeMetricVisual(visual, key: metric.string("key"), slot: index)
        )
        valueRow.addArrangedSubview(value)
        column.addArrangedSubview(valueRow)
      } else {
        column.addArrangedSubview(value)
      }
      row.addArrangedSubview(column)
    }
    return row
  }

  private func makeMetricVisual(
    _ visual: [String: Any],
    key: String,
    slot: Int
  ) -> UIView {
    let container = UIView()
    container.translatesAutoresizingMaskIntoConstraints = false
    container.backgroundColor = UIColor(
      nativeListHex: visual.string("backgroundColor", default: "#0000000F"),
      fallback: .lightGray
    )
    container.layer.cornerRadius = visual.string("shape") == "square" ? 0 : 8
    container.clipsToBounds = true
    NSLayoutConstraint.activate([
      container.widthAnchor.constraint(equalToConstant: 16),
      container.heightAnchor.constraint(equalToConstant: 16),
    ])

    if visual.string("kind") == "icon" {
      let imageView = UIImageView(image: nativeListIcon(named: visual.string("name")))
      imageView.translatesAutoresizingMaskIntoConstraints = false
      imageView.tintColor = UIColor(
        nativeListHex: visual.string("tintColor", default: "#00000072"),
        fallback: .darkGray
      )
      imageView.contentMode = .scaleAspectFit
      container.addSubview(imageView)
      NSLayoutConstraint.activate([
        imageView.leadingAnchor.constraint(equalTo: container.leadingAnchor),
        imageView.trailingAnchor.constraint(equalTo: container.trailingAnchor),
        imageView.topAnchor.constraint(equalTo: container.topAnchor),
        imageView.bottomAnchor.constraint(equalTo: container.bottomAnchor),
      ])
    } else if let source = visualSources(visual).first {
      let imageView = OneKeyImageReusableView(frame: .zero)
      imageView.translatesAutoresizingMaskIntoConstraints = false
      imageView.clipsToBounds = true
      imageView.layer.cornerRadius = container.layer.cornerRadius
      container.addSubview(imageView)
      NSLayoutConstraint.activate([
        imageView.leadingAnchor.constraint(equalTo: container.leadingAnchor),
        imageView.trailingAnchor.constraint(equalTo: container.trailingAnchor),
        imageView.topAnchor.constraint(equalTo: container.topAnchor),
        imageView.bottomAnchor.constraint(equalTo: container.bottomAnchor),
      ])
      bindImage(source.data, into: imageView, token: key, slot: slot, variant: source.variant)
    }
    return container
  }

  private func bindSectionHeader(
    _ item: NativeListItem,
    theme: [String: Any]?,
    layout: String,
    _ checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    rootStack.addArrangedSubview(mainStack)
    let variant = item.data.string("variant")
    // OneKey patch: title help is a separate target from checkbox and value actions.
    if !item.data.string("titleActionKey").isEmpty {
      titleLabel.isUserInteractionEnabled = true
      titleLabel.addGestureRecognizer(selectorTitleTap)
      titleLabel.setContentHuggingPriority(.required, for: .horizontal)
    }
    let isSummary = variant == "summary"
    let isGallery = variant == "gallery"
    let isTable = layout == "table"
    let isNetworkSelector = item.data.string("presentation") == "networkSelector"
    let isExplicitNetworkHeader = isNetworkSelector && item.data["height"] != nil
    if isExplicitNetworkHeader {
      // OneKey patch: the flexible title consumes spare space before trailing totals.
      trailingStack.setContentHuggingPriority(.required, for: .horizontal)
      trailingStack.setContentCompressionResistancePriority(.required, for: .horizontal)
    }
    titleLabel.reservesDottedUnderlineSpace = isExplicitNetworkHeader && !item.data.string("titleActionKey").isEmpty
    let isHistory = variant == "history" ||
      item.key.hasPrefix("history-") ||
      (item.sectionKey?.hasPrefix("history-") ?? false)
    let headerWeight: NativeListFontWeight = isSummary
      ? .medium
      : isExplicitNetworkHeader && (item.data.dictionary("checkbox") != nil || item.data.string("titleActionKey").isEmpty) ? .semibold
      : isNetworkSelector ? .medium
      : isGallery || layout == "sectioned" ? .semibold : .regular
    titleLabel.font = nativeListFont(
      ofSize: isGallery ? 18 : isSummary ? 16 : isTable ? 11 : isHistory ? 12 : 14,
      weight: isHistory ? .semibold : headerWeight
    )
    titleLabel.textColor = nativeListColor(
      theme,
      isSummary || isGallery ? "primaryText" : "secondaryText",
      isSummary || isGallery ? "#202020" : "#646464"
    )
    show(titleLabel, item.data.string("title"), lines: 1)
    if isNetworkSelector {
      titleLabel.showsDottedUnderline = !isExplicitNetworkHeader || !item.data.string("titleActionKey").isEmpty
      titleLabel.dottedUnderlineVerticalOffset = isExplicitNetworkHeader ? 1 : 2
      titleLabel.dottedUnderlineColor = nativeListColor(
        theme,
        "secondaryText",
        "#646464"
      )
      rootLeadingConstraint.constant = 12
      rootTrailingConstraint.constant = -12
      let isAlphabet = isExplicitNetworkHeader && item.data.string("titleActionKey").isEmpty
      rootTopConstraint.constant = isAlphabet ? 8 : 12
      rootBottomConstraint.constant = isAlphabet ? -8 : isExplicitNetworkHeader ? -12 : -15
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 20)
    } else if isHistory {
      rootLeadingConstraint.constant = 0
      rootTrailingConstraint.constant = 0
      rootTopConstraint.constant = 0
      rootBottomConstraint.constant = 0
      setLineHeight(
        titleLabel,
        text: item.data.string("title").uppercased(),
        lineHeight: 16,
        letterSpacing: 0.8
      )
    } else if !isTable && !isGallery && !isSummary {
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 20)
      if layout == "linear" {
        // TokenManager's section title is a plain body label with mt=10,
        // unlike the reusable 36-point SectionHeader used by sectioned lists.
        titleLabel.font = nativeListFont(ofSize: 14)
        rootTopConstraint.constant = 10
        rootBottomConstraint.constant = 0
      }
    }
    if isTable {
      mainStack.alignment = .leading
      titleRowStack.spacing = 4
      titleLabel.setContentHuggingPriority(.required, for: .horizontal)
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 14)
      rootTopConstraint.constant = 12
      rootBottomConstraint.constant = -2
      if let icon = item.data.dictionary("titleIcon") {
        headerTitleIconImageView.image = nativeListIcon(named: icon.string("name"))
        headerTitleIconImageView.tintColor = UIColor(
          nativeListHex: icon.string("tintColor", default: "#646464"),
          fallback: .darkGray
        )
        headerTitleIconImageView.isHidden = false
        titleRowStack.insertArrangedSubview(headerTitleIconImageView, at: 1)
        if icon.string("name") != "ChevronGrabberVerOutline" {
          titleLabel.textColor = nativeListColor(theme, "primaryText", "#202020")
          setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 14)
        }
      }
    }
    if isGallery {
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 24)
      rootTopConstraint.constant = 0
      rootBottomConstraint.constant = -8
    }
    show(subtitleLabel, item.data.string("subtitle"), lines: 1)
    if isSummary {
      titleLabel.showsDottedUnderline = true
      titleLabel.dottedUnderlineColor = nativeListColor(
        theme,
        "secondaryText",
        "#646464"
      )
      rootStack.addArrangedSubview(trailingStack)
      // The surrounding Stack contributes mt=16/pb=12 while its XStack uses
      // px=20/py=8. The collection's 8-point inset supplies the outer 8 points
      // of horizontal padding, so the cell supplies the remaining 12.
      rootLeadingConstraint.constant = 12
      rootTrailingConstraint.constant = -12
      rootTopConstraint.constant = 24
      rootBottomConstraint.constant = -20
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 24)
      accessoryButtons[0].accessibilityIdentifier = item.data["valueActionTestID"] as? String
      let valueActionKey = item.data.string("valueActionKey")
      let action: (String, NativeSelectionTarget?)? = valueActionKey.isEmpty
        ? nil
        : (valueActionKey, nil)
      showAccessory(
        0,
        item.data.string("value"),
        action: action,
        color: nativeListColor(theme, "secondaryText", "#646464")
      )
      accessoryButtons[0].titleLabel?.font = nativeListFont(ofSize: 16, weight: isExplicitNetworkHeader ? .medium : .regular)
      setButtonLine(
        accessoryButtons[0],
        text: item.data.string("value"),
        font: nativeListFont(ofSize: 16, weight: isExplicitNetworkHeader ? .medium : .regular),
        color: nativeListColor(theme, "secondaryText", "#646464"),
        lineHeight: 24
      )
    } else if !isGallery && !isHistory {
      rootStack.addArrangedSubview(trailingStack)
      showAccessory(0, item.data.string("value"))
      if isTable {
        trailingStack.axis = .horizontal
        trailingStack.alignment = .center
        trailingStack.spacing = 4
        accessoryButtons[0].titleLabel?.font = nativeListFont(ofSize: 11)
        accessoryButtons[0].setTitleColor(
          nativeListColor(theme, "secondaryText", "#646464"),
          for: .normal
        )
        if let icon = item.data.dictionary("valueIcon") {
          headerValueIconImageView.image = nativeListIcon(named: icon.string("name"))
          headerValueIconImageView.tintColor = UIColor(
            nativeListHex: icon.string("tintColor", default: "#646464"),
            fallback: .darkGray
          )
          headerValueIconImageView.isHidden = false
          trailingStack.addArrangedSubview(headerValueIconImageView)
          if icon.string("name") != "ChevronGrabberVerOutline" {
            accessoryButtons[0].setTitleColor(
              nativeListColor(theme, "primaryText", "#202020"),
              for: .normal
            )
          }
        }
      }
      if let checkbox = item.data.dictionary("checkbox") {
        bindCheckbox(item, checkbox, checkboxState)
      }
      if item.data.dictionary("checkbox") != nil && !item.data.string("value").isEmpty {
        titleLabel.showsDottedUnderline = true
        titleLabel.dottedUnderlineColor = nativeListColor(
          theme,
          "secondaryText",
          "#646464"
        )
        trailingStack.axis = .horizontal
        trailingStack.alignment = .center
        trailingStack.spacing = 12
        rootTopConstraint.constant = 8
        rootBottomConstraint.constant = -8
        accessoryButtons[0].titleLabel?.font = nativeListTabularFont(
          ofSize: 16,
          weight: .medium
        )
        setButtonLine(
          accessoryButtons[0],
          text: item.data.string("value"),
          font: nativeListTabularFont(ofSize: 16, weight: .medium),
          color: nativeListColor(theme, "primaryText", "#202020"),
          lineHeight: 24
        )
      }
    }
    applyValueSegments(item.data.dictionaries("valueSegments"), to: accessoryButtons[0], theme: theme)
  }

  // OneKey patch: small zero-count digits remain on the regular amount baseline.
  private func applyValueSegments(_ segments: [[String: Any]], to button: UIButton, theme: [String: Any]?) {
    guard !segments.isEmpty else { return }
    let value = NSMutableAttributedString(string: "")
    let color = nativeListColor(theme, "primaryText", "#202020")
    // OneKey patch: rich currency changes font runs without dropping the established line baseline.
    let current = button.attributedTitle(for: .normal)
    var attributes = current.flatMap { $0.length > 0 ? $0.attributes(at: 0, effectiveRange: nil) : nil } ?? [:]
    attributes[.foregroundColor] = color
    if currentItem?.data.string("presentation") == "networkSelector" && currentItem?.data["height"] != nil {
      let paragraph = (attributes[.paragraphStyle] as? NSParagraphStyle)?.mutableCopy() as? NSMutableParagraphStyle ?? NSMutableParagraphStyle()
      paragraph.alignment = .right
      attributes[.paragraphStyle] = paragraph
    }
    for segment in segments {
      attributes[.font] = nativeListTabularFont(ofSize: segment.string("style") == "subscript" ? 10 : 16, weight: .medium)
      value.append(NSAttributedString(string: segment.string("text"), attributes: attributes))
    }
    button.setAttributedTitle(value, for: .normal)
  }

  private func bindAction(
    _ item: NativeListItem,
    theme: [String: Any]?,
    _ checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    let isAccountSelector = item.data.string("presentation") == "accountSelector"
    if let icon = item.data.dictionary("icon") {
      leadingWidth.constant = isAccountSelector ? 32 : 40
      leadingHeight.constant = isAccountSelector ? 32 : 40
      leadingIconWidth.constant = 24
      leadingIconHeight.constant = 24
      addLeading(icon, key: item.key)
      if isAccountSelector {
        leadingContainer.layer.cornerCurve = .continuous
        leadingContainer.layer.cornerRadius = 8
        leadingContainer.layer.borderWidth = 0
      }
      if icon["backgroundColor"] == nil {
        leadingContainer.backgroundColor = .clear
        leadingContainer.layer.borderWidth = 0
        leadingContainer.layer.borderColor = nil
      }
    }
    rootStack.addArrangedSubview(mainStack)
    show(titleLabel, item.data.string("title"), lines: 1)
    if isAccountSelector {
      // OneKey patch: ListItem.Text is medium; empty-search actions use regular body text.
      titleLabel.font = nativeListFont(ofSize: 16, weight: item.data.dictionary("icon") == nil ? .regular : .medium)
      titleLabel.textColor = nativeListColor(theme, item.data.string("tone") == "primary" ? "primaryText" : "secondaryText", item.data.string("tone") == "primary" ? "#202020" : "#646464")
    } else if item.data.string("tone") == "danger" {
      titleLabel.textColor = nativeListColor(theme, "negative", "#CE2C31")
    }
    setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 24)
    if let checkbox = item.data.dictionary("checkbox") {
      bindCheckbox(item, checkbox, checkboxState)
      rootStack.addArrangedSubview(trailingStack)
    } else {
      let accessories = item.data.dictionaries("trailing")
      if !accessories.isEmpty {
        rootStack.addArrangedSubview(trailingStack)
        bindAccessories(item, accessories, theme, checkboxState)
        if accessories.count == 1, accessories[0].string("kind") == "chevron" {
          // ListItem.DrillIn uses mx=-6 around its 24-point icon.
          rootTrailingConstraint.constant = -6
        }
      }
    }
  }

  // OneKey patch: preserve the actual title frame for Popover placement.
  @objc private func selectorTitlePressed() {
    guard let item = currentItem else { return }
    let action = item.data.string("titleActionKey")
    guard !action.isEmpty else { return }
    onAction?(item, action, nil, actionOrigin(sourceView: titleLabel, source: "leadingAction"))
  }

  private func dataTextColor(_ tone: String, theme: [String: Any]?) -> UIColor {
    switch tone.isEmpty ? "primary" : tone {
    // OneKey patch: account warnings and hidden balances use existing theme tokens.
    case "disabled": return nativeListColor(theme, "disabledText", "#8D8D8D")
    case "caution": return nativeListColor(theme, "caution", "#AB6400")
    case "secondary": return nativeListColor(theme, "secondaryText", "#646464")
    case "positive": return nativeListColor(theme, "positive", "#218358")
    case "negative": return nativeListColor(theme, "negative", "#CE2C31")
    default: return nativeListColor(theme, "primaryText", "#202020")
    }
  }

  private func bindSystem(_ item: NativeListItem, theme: [String: Any]?) {
    rootStack.alignment = .center
    rootStack.distribution = .fill
    let variant = item.data.string("variant")
    let isMarket = item.data.string("presentation") == "market"
    if isMarket {
      rootLeadingConstraint.constant = 20
      rootTrailingConstraint.constant = -20
      rootTopConstraint.constant = 12
      rootBottomConstraint.constant = -12
    }
    if isMarket && variant == "retry" {
      let message = item.data.string("message")
      let text = item.data.string("actionText", default: "Retry")
      rootStack.axis = .vertical
      // The source tertiary Button has -5 vertical margins around its 30pt frame.
      rootStack.spacing = 7
      rootLeadingConstraint.constant = 32
      rootTrailingConstraint.constant = -32
      let height = CGFloat(item.data.double("height", default: message.isEmpty ? 52 : 120))
      let top = message.isEmpty ? 11 : max(32, (height - 56) / 2)
      rootTopConstraint.constant = top
      rootBottomConstraint.constant = -(height - top - (message.isEmpty ? 30 : 61))
      if !message.isEmpty {
        show(titleLabel, message, lines: 2)
        titleLabel.font = nativeListTabularFont(ofSize: 16)
        titleLabel.textColor = nativeListColor(theme, "secondaryText", "#646464")
        titleLabel.textAlignment = .center
        setLineHeight(titleLabel, text: message, lineHeight: 24)
        rootStack.addArrangedSubview(mainStack)
      }
      showAccessory(0, text, action: (item.data.string("actionKey"), nil))
      let button = accessoryButtons[0]
      button.backgroundColor = .clear
      button.layer.cornerRadius = 15
      setButtonLine(button, text: text, font: nativeListTabularFont(ofSize: 14, weight: .medium),
                    color: nativeListColor(theme, "secondaryText", "#646464"), lineHeight: 20)
      let textWidth = button.intrinsicContentSize.width
      selectorConstraints.append(contentsOf: [
        button.widthAnchor.constraint(equalToConstant: textWidth + 18),
        button.heightAnchor.constraint(equalToConstant: 30),
      ])
      NSLayoutConstraint.activate(selectorConstraints)
      rootStack.addArrangedSubview(trailingStack)
      return
    }
    if variant == "loading" && item.data.string("loadingStyle") == "skeleton" {
      rootLeadingConstraint.constant = 20
      rootTrailingConstraint.constant = -20
      rootTopConstraint.constant = 12
      rootBottomConstraint.constant = -12
      let skeleton = NativeListMarketSkeleton(background: nativeListColor(theme, "background", "#FFFFFF"))
      skeleton.translatesAutoresizingMaskIntoConstraints = false
      skeleton.heightAnchor.constraint(equalToConstant: 32).isActive = true
      rootStack.addArrangedSubview(skeleton)
      selectorViews.append(skeleton)
      return
    }
    if variant == "loading" && item.data.string("loadingStyle") == "spinner" {
      rootTopConstraint.constant = 16
      rootBottomConstraint.constant = -16
      rootStack.addArrangedSubview(mainStack)
      mainStack.alignment = .center
      mainStack.setContentHuggingPriority(.defaultLow, for: .horizontal)
      let indicator = UIActivityIndicatorView(style: .medium)
      indicator.color = nativeListColor(theme, "icon", "#0000009B")
      indicator.startAnimating()
      mainStack.addArrangedSubview(indicator)
      selectorViews.append(indicator)
      return
    }
    if isMarket && variant == "noMatch" {
      let padding = max(32, (CGFloat(item.data.double("height", default: 88)) - 24) / 2)
      rootTopConstraint.constant = padding
      rootBottomConstraint.constant = -padding
      rootStack.addArrangedSubview(mainStack)
      mainStack.alignment = .center
      mainStack.setContentHuggingPriority(.defaultLow, for: .horizontal)
      titleLabel.font = nativeListTabularFont(ofSize: 16)
      titleLabel.textColor = nativeListColor(theme, "secondaryText", "#646464")
      titleLabel.textAlignment = .center
      show(titleLabel, item.data.string("message"), lines: 1)
      setLineHeight(titleLabel, text: item.data.string("message"), lineHeight: 24)
      return
    }
    if isMarket && variant == "end" {
      rootTopConstraint.constant = 16
      rootBottomConstraint.constant = -16
      // OneKey patch: an empty title stack must not consume the dot's line height.
      titleRowStack.isHidden = true
      rootStack.addArrangedSubview(mainStack)
      mainStack.alignment = .center
      mainStack.setContentHuggingPriority(.defaultLow, for: .horizontal)
      let indicator = UIStackView()
      indicator.axis = .horizontal
      indicator.alignment = .center
      indicator.spacing = 8
      for width in [CGFloat(80), 4, 80] {
        let mark = UIView()
        mark.translatesAutoresizingMaskIntoConstraints = false
        mark.backgroundColor = nativeListColor(theme, "separator", "#0000001F")
        mark.layer.cornerRadius = width == 4 ? 2 : 0
        NSLayoutConstraint.activate([
          mark.widthAnchor.constraint(equalToConstant: width),
          mark.heightAnchor.constraint(equalToConstant: width == 4 ? 4 : 1),
        ])
        indicator.addArrangedSubview(mark)
      }
      mainStack.addArrangedSubview(indicator)
      selectorViews.append(indicator)
      return
    }
    // OneKey patch: deprecated-wallet warnings stay inside the scrolling list.
    if variant == "warning" {
      rootStack.addArrangedSubview(mainStack)
      rootTopConstraint.constant = 14
      rootBottomConstraint.constant = -14
      mainStack.spacing = 4
      titleLabel.font = nativeListFont(ofSize: 14, weight: .medium)
      titleLabel.numberOfLines = 0
      subtitleLabel.font = nativeListFont(ofSize: 14)
      subtitleLabel.numberOfLines = 0
      show(titleLabel, item.data.string("title"), lines: 0)
      show(subtitleLabel, item.data.string("message"), lines: 0)
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 20)
      setLineHeight(subtitleLabel, text: item.data.string("message"), lineHeight: 20)
      let borderColor = UIColor(nativeListHex: item.data.string("borderColor", default: "#E0E0E0"), fallback: .lightGray)
      for top in [true, false] {
        let border = UIView()
        border.translatesAutoresizingMaskIntoConstraints = false
        border.backgroundColor = borderColor
        contentView.addSubview(border)
        NSLayoutConstraint.activate([
          border.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: -8),
          border.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: 8),
          border.heightAnchor.constraint(equalToConstant: 1 / UIScreen.main.scale),
          top ? border.topAnchor.constraint(equalTo: contentView.topAnchor) : border.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),
        ])
        selectorViews.append(border)
      }
      return
    }
    if variant == "loading" {
      leadingWidth.constant = isMarket ? 32 : 40
      leadingHeight.constant = isMarket ? 32 : 40
      leadingContainer.backgroundColor = nativeListColor(theme, "strongBackground", "#F0F0F0")
      leadingContainer.layer.cornerRadius = isMarket ? 16 : 20
      rootStack.addArrangedSubview(leadingContainer)
      configureSkeleton(skeletonPrimary, width: 120, height: 12, theme: theme)
      configureSkeleton(skeletonSecondary, width: 80, height: 12, theme: theme)
      mainStack.spacing = 8
      mainStack.addArrangedSubview(skeletonPrimary)
      mainStack.addArrangedSubview(skeletonSecondary)
    }
    show(
      titleLabel,
      item.data.string("message", default: variant == "end" ? "End" : ""),
      lines: 2
    )
    let alignsToStart = variant == "retry" || variant == "noMatch" || variant == "end"
    titleLabel.textAlignment = alignsToStart ? .natural : .center
    mainStack.alignment = variant == "loading" || alignsToStart ? .leading : .center
    if alignsToStart {
      mainStack.setContentHuggingPriority(.defaultLow, for: .horizontal)
      titleLabel.font = nativeListFont(ofSize: 14)
      titleLabel.textColor = nativeListColor(theme, "secondaryText", "#646464")
      setLineHeight(titleLabel, text: titleLabel.text ?? "", lineHeight: 20)
    }
    rootStack.addArrangedSubview(mainStack)
    if variant == "retry" {
      showAccessory(0, "Retry", action: (item.data.string("actionKey"), nil))
      accessoryButtons[0].backgroundColor = nativeListColor(theme, "strongBackground", "#F0F0F0")
      accessoryButtons[0].layer.cornerRadius = 14
      accessoryButtons[0].contentEdgeInsets = UIEdgeInsets(top: 4, left: 10, bottom: 4, right: 10)
      setButtonLine(
        accessoryButtons[0],
        text: "Retry",
        font: nativeListFont(ofSize: 14, weight: .medium),
        color: nativeListColor(theme, "primaryText", "#202020"),
        lineHeight: 20
      )
      rootStack.addArrangedSubview(trailingStack)
    }
  }

  private func addLeading(
    _ visual: [String: Any]?,
    secondaryVisual: [String: Any]? = nil,
    key: String,
    to stack: UIStackView? = nil
  ) {
    let targetStack = stack ?? rootStack
    targetStack.addArrangedSubview(leadingContainer)
    guard let visual else { return }
    let kind = visual.string("kind")
    var sources = visualSources(visual)
    if let secondaryVisual, let secondarySource = visualSources(secondaryVisual).first {
      sources.append(secondarySource)
    }
    let isIcon = kind == "icon"
    let shape = visual.string(
      "shape",
      default: kind == "image" || mediaHeight.isActive ? "rounded" : "circle"
    )
    let cornerIcon = visual.dictionary("cornerIcon")
    let fallbackText = String(visual.string("fallbackText").prefix(2))
    let fallbackIconData = visual.dictionary("fallbackIcon")
    let handlesSourceFallback = !isIcon && !sources.isEmpty &&
      (visual["fallbackText"] != nil || fallbackIconData != nil)
    fallbackLabel.text = handlesSourceFallback ? nil : fallbackText
    let sourceFallbackBackground = currentTheme?["strongBackground"] as? String ?? "#0000000F"
    leadingContainer.backgroundColor = UIColor(
      // OneKey patch: Visual loading and fallback use the active list theme.
      // nativeListHex: visual.string("backgroundColor", default: isIcon ? "#F0F0F0" : "#E0E0E0"),
      nativeListHex: handlesSourceFallback
        ? sourceFallbackBackground
        : visual.string("backgroundColor", default: sourceFallbackBackground),
      fallback: .gray
    )
    leadingContainer.layer.cornerRadius = leadingCornerRadius(shape: shape)
    leadingContainer.clipsToBounds = true
    fallbackLabel.isHidden = isIcon || (!sources.isEmpty && !handlesSourceFallback)
    if isIcon {
      leadingContainer.layer.borderWidth = 1 / UIScreen.main.scale
      leadingContainer.layer.borderColor = UIColor(
        nativeListHex: "#0000001F",
        fallback: .lightGray
      ).cgColor
      leadingIconImageView.isHidden = false
      leadingIconImageView.tintColor = UIColor(
        nativeListHex: visual.string("tintColor", default: "#646464"),
        fallback: .darkGray
      )
      leadingIconImageView.image = nativeListIcon(named: visual.string("name"))
      leadingIconImageView.contentMode = .scaleAspectFit
    } else if sources.isEmpty, let fallbackIconData {
      fallbackLabel.isHidden = true
      leadingIconImageView.isHidden = false
      leadingIconImageView.tintColor = UIColor(
        nativeListHex: fallbackIconData.string("tintColor", default: "#646464"),
        fallback: .darkGray
      )
      leadingIconImageView.image = nativeListIcon(named: fallbackIconData.string("name"))
      leadingIconImageView.contentMode = .scaleAspectFit
    }
    let visibleSources = Array(sources.prefix(leadingImages.count))
    let tokenPair = kind == "token" && visibleSources.count > 1
    leadingContainer.clipsToBounds = !tokenPair && cornerIcon == nil
    if let cornerIcon {
      leadingCornerIconBackground.isHidden = false
      leadingCornerIconBackground.backgroundColor = UIColor(
        nativeListHex: cornerIcon.string("backgroundColor", default: "#FFFFFF"),
        fallback: .white
      )
      leadingCornerIconImageView.image = nativeListIcon(named: cornerIcon.string("name"))
      leadingCornerIconImageView.tintColor = UIColor(
        nativeListHex: cornerIcon.string("tintColor", default: "#646464"),
        fallback: .darkGray
      )
    }
    if tokenPair {
      leadingOverlayBackground.isHidden = false
      leadingOverlayBackground.backgroundColor = visualBackdropColor
      leadingSlotConstraints.append(contentsOf: [
        leadingOverlayBackground.widthAnchor.constraint(equalToConstant: 20),
        leadingOverlayBackground.heightAnchor.constraint(equalToConstant: 20),
        leadingOverlayBackground.trailingAnchor.constraint(
          equalTo: leadingContainer.trailingAnchor,
          constant: 4
        ),
        leadingOverlayBackground.bottomAnchor.constraint(
          equalTo: leadingContainer.bottomAnchor,
          constant: 4
        ),
      ])
    }
    for (index, source) in visibleSources.enumerated() {
      let imageView = leadingImages[index]
      // OneKey patch: Decorative badges and source-owned fallbacks stay hidden until loaded.
      // imageView.isHidden = false
      let ownsSourceFallback = index == 0 && handlesSourceFallback
      imageView.isHidden = index > 0 || ownsSourceFallback
      imageView.clipsToBounds = true
      leadingSlotConstraints.append(contentsOf: leadingConstraints(
        imageView,
        index: index,
        count: visibleSources.count,
        tokenPair: tokenPair,
        shape: shape
      ))
      if currentItem?.data.string("presentation") == "networkSelector", currentItem?.data["height"] != nil, visibleSources.count == 1, cornerIcon == nil, visual.dictionaries("overlays").isEmpty {
        // OneKey patch: a single outer mask matches NetworkAvatar's edge antialiasing.
        imageView.layer.cornerRadius = 0
        imageView.clipsToBounds = false
      }
      let fallbackIcon = index == 0 ? fallbackIconData : nil
      let expectedEpoch = bindingEpoch
      bindImage(source.data, into: imageView, token: key, slot: index, variant: source.variant,
        onLoad: !ownsSourceFallback ? nil : { [weak self, weak imageView] in
          guard let self, self.bindingEpoch == expectedEpoch else { return }
          imageView?.isHidden = false
          self.fallbackLabel.isHidden = true
          self.leadingIconImageView.isHidden = true
        },
        onError: !ownsSourceFallback ? nil : { [weak self, weak imageView] in
          guard let self, self.bindingEpoch == expectedEpoch else { return }
          imageView?.isHidden = true
          if let fallbackIcon {
            self.fallbackLabel.isHidden = true
            self.leadingIconImageView.image = nativeListIcon(named: fallbackIcon.string("name"))
            self.leadingIconImageView.tintColor = UIColor(nativeListHex: fallbackIcon.string("tintColor", default: "#646464"), fallback: .darkGray)
            self.leadingIconImageView.isHidden = false
          } else {
            self.leadingIconImageView.isHidden = true
            self.fallbackLabel.text = fallbackText
            self.fallbackLabel.isHidden = false
          }
        },
        hideUntilLoaded: index > 0 || ownsSourceFallback)
    }
    // OneKey patch: source-derived wallet decorations may occupy both corners.
    let overlays = visual.dictionaries("overlays")
    if !overlays.isEmpty { leadingContainer.clipsToBounds = false }
    for (index, overlay) in overlays.enumerated() {
      let size = CGFloat(overlay.double("size", default: 20))
      let isWalletText = currentItem?.data.string("presentation") == "walletSidebar" && currentItem?.data["height"] != nil && !overlay.string("text").isEmpty && overlay.dictionary("image") == nil && overlay.string("name").isEmpty
      let inset = CGFloat(overlay.double("padding", default: 0))
      let offsetX = CGFloat(overlay.double("offsetX", default: overlay.double("offset", default: 2)))
      let offsetY = CGFloat(overlay.double("offsetY", default: overlay.double("offset", default: 2)))
      let height = CGFloat(overlay.double("height", default: isWalletText ? 16 : Double(size)))
      let textWidth = (overlay.string("text") as NSString).size(withAttributes: [.font: nativeListTabularFont(ofSize: 12), .kern: 0]).width
      let naturalTextWidth = ceil(textWidth * UIScreen.main.scale) / UIScreen.main.scale + 4
      let width = CGFloat(overlay.double("width", default: isWalletText ? Double(naturalTextWidth) : Double(size)))
      let frame = UIView()
      frame.translatesAutoresizingMaskIntoConstraints = false
      // OneKey patch: The row theme, not a light-only white, owns badge backing.
      // frame.backgroundColor = UIColor(nativeListHex: overlay.string("backgroundColor", default: "#FFFFFF"), fallback: .clear)
      frame.backgroundColor = UIColor(
        nativeListHex: overlay.string(
          "backgroundColor",
          default: currentTheme?["rowBackground"] as? String ?? "#00000000"
        ),
        fallback: .clear
      )
      frame.layer.cornerRadius = min(width, height) / 2
      frame.clipsToBounds = true
      leadingContainer.addSubview(frame)
      selectorViews.append(frame)
      let topLeft = overlay.string("position") == "topLeft"
      leadingSlotConstraints.append(contentsOf: [
        frame.widthAnchor.constraint(equalToConstant: width),
        frame.heightAnchor.constraint(equalToConstant: height),
        topLeft ? frame.leadingAnchor.constraint(equalTo: leadingContainer.leadingAnchor, constant: -offsetX) : frame.trailingAnchor.constraint(equalTo: leadingContainer.trailingAnchor, constant: offsetX),
        topLeft ? frame.topAnchor.constraint(equalTo: leadingContainer.topAnchor, constant: -offsetY) : frame.bottomAnchor.constraint(equalTo: leadingContainer.bottomAnchor, constant: offsetY),
      ])
      let content: UIView
      if let image = overlay.dictionary("image") {
        let imageView = OneKeyImageReusableView(frame: .zero)
        bindImage(
          image,
          into: imageView,
          token: key,
          slot: 10 + index,
          variant: "generic",
          hideUntilLoaded: true
        )
        selectorImages.append(imageView)
        content = imageView
      } else if !overlay.string("text").isEmpty {
        let label = UILabel()
        label.text = overlay.string("text")
        label.textAlignment = .center
        label.font = nativeListFont(ofSize: isWalletText ? 12 : 10, weight: isWalletText ? .regular : .medium)
        label.textColor = UIColor(nativeListHex: overlay.string("tintColor", default: "#646464"), fallback: .darkGray)
        if isWalletText { setLineHeight(label, text: overlay.string("text"), lineHeight: 16) }
        content = label
      } else {
        let imageView = UIImageView(image: nativeListIcon(named: overlay.string("name")))
        imageView.contentMode = .scaleAspectFit
        imageView.tintColor = UIColor(nativeListHex: overlay.string("tintColor", default: "#646464"), fallback: .darkGray)
        content = imageView
      }
      content.translatesAutoresizingMaskIntoConstraints = false
      frame.addSubview(content)
      NSLayoutConstraint.activate([
        content.leadingAnchor.constraint(equalTo: frame.leadingAnchor, constant: isWalletText ? 2 : inset),
        content.trailingAnchor.constraint(equalTo: frame.trailingAnchor, constant: isWalletText ? -2 : -inset),
        content.topAnchor.constraint(equalTo: frame.topAnchor, constant: isWalletText ? 0 : inset),
        content.bottomAnchor.constraint(equalTo: frame.bottomAnchor, constant: isWalletText ? 0 : -inset),
      ])
    }
    if let fallbackIcon = visual.dictionary("fallbackIcon"), sources.isEmpty {
      fallbackLabel.isHidden = true
      leadingIconImageView.isHidden = false
      leadingIconImageView.image = nativeListIcon(named: fallbackIcon.string("name"))
      leadingIconImageView.tintColor = UIColor(nativeListHex: fallbackIcon.string("tintColor", default: "#646464"), fallback: .darkGray)
      if currentItem?.data.string("presentation") == "walletSidebar", currentItem?.data["height"] != nil, fallbackIcon.string("name") == "LockSolid" {
        leadingIconWidth.constant = 40
        leadingIconHeight.constant = 40
        leadingContainer.clipsToBounds = false
      }
    }
    if visual.string("borderStyle") == "dashed" {
      let border = CAShapeLayer()
      border.strokeColor = UIColor(nativeListHex: visual.string("borderColor", default: "#8D8D8D"), fallback: .gray).cgColor
      border.fillColor = UIColor.clear.cgColor
      border.lineWidth = currentItem?.data.string("presentation") == "walletSidebar" && currentItem?.data["height"] != nil ? 1 : 2
      border.lineDashPattern = [4, 4]
      let borderInset = border.lineWidth / 2
      border.path = UIBezierPath(ovalIn: CGRect(x: borderInset, y: borderInset, width: leadingWidth.constant - border.lineWidth, height: leadingHeight.constant - border.lineWidth)).cgPath
      leadingContainer.layer.addSublayer(border)
      selectorBorder = border
    }
    NSLayoutConstraint.activate(leadingSlotConstraints)
  }

  private func visualSources(_ visual: [String: Any]) -> [(data: [String: Any], variant: String)] {
    let kind = visual.string("kind")
    if kind == "stackedImages" {
      return visual.dictionaries("images").prefix(3).map { ($0, "generic") }
    }
    guard kind != "icon", let image = visual.dictionary("image") else { return [] }
    let variant: String
    switch kind {
    case "token": variant = "token"
    case "network": variant = "network"
    case "account", "wallet": variant = "avatar"
    default: variant = "generic"
    }
    var sources: [(data: [String: Any], variant: String)] = [(image, variant)]
    if kind == "token", let networkImage = visual.dictionary("networkImage") {
      sources.append((networkImage, "network"))
    }
    return sources
  }

  private func leadingConstraints(
    _ imageView: UIView,
    index: Int,
    count: Int,
    tokenPair: Bool,
    shape: String
  ) -> [NSLayoutConstraint] {
    if count == 1 || tokenPair && index == 0 {
      imageView.layer.cornerRadius = leadingCornerRadius(shape: shape)
      return [
        imageView.leadingAnchor.constraint(equalTo: leadingContainer.leadingAnchor),
        imageView.trailingAnchor.constraint(equalTo: leadingContainer.trailingAnchor),
        imageView.topAnchor.constraint(equalTo: leadingContainer.topAnchor),
        imageView.bottomAnchor.constraint(equalTo: leadingContainer.bottomAnchor),
      ]
    }
    if tokenPair && index == 1 {
      let overlaySize: CGFloat = 16
      imageView.layer.cornerRadius = overlaySize / 2
      return [
        imageView.widthAnchor.constraint(equalToConstant: overlaySize),
        imageView.heightAnchor.constraint(equalToConstant: overlaySize),
        imageView.trailingAnchor.constraint(equalTo: leadingContainer.trailingAnchor, constant: 2),
        imageView.bottomAnchor.constraint(equalTo: leadingContainer.bottomAnchor, constant: 2),
      ]
    }
    let imageSize = leadingHeight.constant * 0.72
    let availableOffset = leadingWidth.constant - imageSize
    let offset = CGFloat(index) * availableOffset / CGFloat(max(1, count - 1))
    imageView.layer.cornerRadius = imageSize / 2
    return [
      imageView.widthAnchor.constraint(equalToConstant: imageSize),
      imageView.heightAnchor.constraint(equalToConstant: imageSize),
      imageView.leadingAnchor.constraint(equalTo: leadingContainer.leadingAnchor, constant: offset),
      imageView.centerYAnchor.constraint(equalTo: leadingContainer.centerYAnchor),
    ]
  }

  private func leadingCornerRadius(shape: String) -> CGFloat {
    switch shape {
    case "square": return 0
    case "rounded": return min(10, leadingHeight.constant / 4)
    default: return leadingHeight.constant / 2
    }
  }

  private func bindAccessories(
    _ item: NativeListItem,
    _ accessories: [[String: Any]],
    _ theme: [String: Any]?,
    _ checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    var textIndex = 0
    for accessory in accessories.prefix(2) {
      switch accessory.string("kind") {
      case "value":
        showAccessory(textIndex, accessory.string("text"))
        if item.data.string("presentation") == "networkSelector" && item.data["height"] != nil {
          accessoryButtons[textIndex].contentHorizontalAlignment = .trailing
          accessoryButtons[textIndex].titleLabel?.textAlignment = .right
        }
        applyValueSegments(accessory.dictionaries("textSegments"), to: accessoryButtons[textIndex], theme: theme)
        textIndex += 1
      case "valuePair":
        showValuePairAccessory(textIndex, accessory, theme: theme)
        textIndex += 1
      case "checkbox": bindCheckbox(item, accessory, checkboxState)
      case "radio": showAccessory(textIndex, accessory.bool("checked") ? "●" : "○", action: accessoryAction(accessory)); textIndex += 1
      case "switch": showAccessory(textIndex, accessory.bool("value") ? "ON" : "OFF", action: accessoryAction(accessory)); textIndex += 1
      case "chevron":
        var icon = accessory
        icon["name"] = "ChevronRightSmallOutline"
        if icon["tintColor"] == nil {
          icon["tintColor"] = theme?["iconSubdued"] as? String ?? "#8D8D8D"
        }
        if icon.string("actionKey").isEmpty { icon["actionKey"] = "press" }
        showAccessoryIcon(textIndex, icon)
        textIndex += 1
      case "menu": showMenuAccessory(textIndex, action: accessoryAction(accessory)); textIndex += 1
      case "drag":
        var icon = accessory
        icon["name"] = "DragOutline"
        if icon["tintColor"] == nil {
          icon["tintColor"] = theme?["iconSubdued"] as? String ?? "#8D8D8D"
        }
        showAccessoryIcon(textIndex, icon)
        textIndex += 1
      case "icon":
        var icon = accessory
        if icon["tintColor"] == nil {
          icon["tintColor"] = theme?["iconSubdued"] as? String ?? "#8D8D8D"
        }
        showAccessoryIcon(textIndex, icon)
        textIndex += 1
      case "spinner": spinner.startAnimating()
      case "progress": showAccessory(textIndex, "\(Int(accessory.double("value") * 100))%"); textIndex += 1
      default: break
      }
    }
  }

  private func bindCheckbox(
    _ item: NativeListItem,
    _ data: [String: Any],
    _ checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    let targetData = data.dictionary("target")
    let scope = targetData?.string("scope", default: "row") ?? "row"
    let key = scope == "section" ? targetData?.string("sectionKey") : scope == "row" ? item.key : nil
    let target = NativeSelectionTarget(scope: scope, key: key)
    let state = checkboxState(item, target, data.string("state", default: "unchecked"))
    boundCheckboxData = data
    boundCheckboxTarget = target
    updateCheckboxPresentation(item, data, target: target, state: state)
  }

  private func updateCheckboxPresentation(
    _ item: NativeListItem,
    _ data: [String: Any],
    target: NativeSelectionTarget,
    state: String
  ) {
    let accessoryDisabled = data.bool("disabled")
    if data.bool("loading") {
      checkboxButton.isHidden = true
      spinner.startAnimating()
      spinner.alpha = item.data.bool("disabled") ? 1 : accessoryDisabled ? 0.5 : 1
      checkboxAction = nil
      return
    }
    checkboxButton.isHidden = false
    checkboxButton.backgroundColor = state == "unchecked" ? checkboxUncheckedColor : checkboxCheckedColor
    checkboxButton.layer.borderColor = state == "unchecked"
      ? checkboxBorderColor.cgColor
      : UIColor.clear.cgColor
    let glyphName = state == "indeterminate"
      ? "CheckboxIndeterminateCustom"
      : "CheckboxCheckedCustom"
    checkboxButton.setImage(
      state == "unchecked" ? nil : nativeListIcon(named: glyphName),
      for: .normal
    )
    checkboxButton.tintColor = checkboxIconColor
    // A disabled ListItem already applies 0.5 to its complete content. Avoid
    // multiplying that opacity on the nested control a second time.
    checkboxButton.alpha = item.data.bool("disabled") ? 1 : accessoryDisabled ? 0.5 : 1
    checkboxButton.isEnabled = !item.data.bool("disabled") && !accessoryDisabled && !data.bool("loading")
    checkboxAction = (data.string("actionKey", default: "selection"), target)
  }

  private func show(_ label: UILabel, _ value: String, lines: Int) {
    guard !value.isEmpty else { return }
    label.text = value
    label.numberOfLines = lines
    label.isHidden = false
  }

  private func setLineHeight(
    _ label: UILabel,
    text: String,
    lineHeight: CGFloat,
    letterSpacing: CGFloat = 0
  ) {
    let paragraphStyle = NSMutableParagraphStyle()
    paragraphStyle.minimumLineHeight = lineHeight
    paragraphStyle.maximumLineHeight = lineHeight
    if currentItem?.type == "market" || currentItem?.data.string("presentation") == "walletSidebar" {
      // Attributed paragraphs must preserve the label alignment and tail ellipsis.
      paragraphStyle.alignment = label.textAlignment
      paragraphStyle.lineBreakMode = label.lineBreakMode
    }
    var attributes: [NSAttributedString.Key: Any] = [
      .font: label.font as Any,
      .foregroundColor: label.textColor as Any,
      .paragraphStyle: paragraphStyle,
    ]
    if currentItem?.type == "market" || currentItem?.type == "system" && currentItem?.data.string("presentation") == "market" && currentItem?.data.string("variant") == "noMatch" || (currentItem?.data["height"] != nil && (["accountSelector", "walletSidebar"].contains(currentItem?.data.string("presentation") ?? "") || currentItem?.type == "sectionHeader" && currentItem?.data.string("presentation") == "networkSelector")) || currentItem?.type == "system" && currentItem?.data.string("variant") == "warning" {
      // OneKey patch: React Native centers font metrics inside explicit line heights.
      let baselineOffset = max(0, (lineHeight - label.font.lineHeight) / 2)
      // OneKey patch: TextKit's 14/20 headings align their baseline to the upper physical pixel.
      let isSelectorHeading = lineHeight == 20 && (currentItem?.type == "sectionHeader" && currentItem?.data.string("presentation") == "networkSelector" || currentItem?.type == "market" && label.font.pointSize == 14)
      let scale = window?.screen.scale ?? traitCollection.displayScale
      attributes[.baselineOffset] = isSelectorHeading && scale > 0 ? ceil(baselineOffset * scale) / scale : baselineOffset
    }
    if letterSpacing != 0 || currentItem?.type == "market" || currentItem?.data.string("presentation") == "market" { attributes[.kern] = letterSpacing }
    label.attributedText = NSAttributedString(string: text, attributes: attributes)
  }

  private func setButtonLine(
    _ button: UIButton,
    text: String,
    font: UIFont,
    color: UIColor,
    lineHeight: CGFloat
  ) {
    guard !text.isEmpty else { return }
    let paragraphStyle = NSMutableParagraphStyle()
    paragraphStyle.minimumLineHeight = lineHeight
    paragraphStyle.maximumLineHeight = lineHeight
    let isMarketText = currentItem?.type == "market" || currentItem?.type == "system" && currentItem?.data.string("presentation") == "market" && currentItem?.data.string("variant") == "retry"
    let isSelectorValue = currentItem?.data.string("presentation") == "networkSelector" && currentItem?.data["height"] != nil && currentItem?.data.string("variant") != "summary"
    let isSelectorSummary = currentItem?.type == "sectionHeader" && currentItem?.data.string("presentation") == "networkSelector" && currentItem?.data["height"] != nil && currentItem?.data.string("variant") == "summary"
    (button as? NativeListAccessoryButton)?.selectorSummaryLineHeight = isSelectorSummary ? lineHeight : nil
    (button as? NativeListAccessoryButton)?.marketLineHeight = isMarketText ? lineHeight : nil
    // OneKey patch: summary text uses its source line box; currency retains trailing alignment.
    // Market's line box already handles alignment; source text starts at its origin.
    paragraphStyle.alignment = isSelectorValue ? .right : isSelectorSummary || isMarketText ? .natural : .center
    if isSelectorSummary {
      button.contentHorizontalAlignment = .leading
      button.titleLabel?.textAlignment = .natural
    }
    if isSelectorValue {
      button.contentHorizontalAlignment = .trailing
      button.titleLabel?.textAlignment = .right
    }
    let baselineOffset: CGFloat = isMarketText || currentItem?.type == "sectionHeader" && currentItem?.data.string("presentation") == "networkSelector" && currentItem?.data["height"] != nil ? max(0, (lineHeight - font.lineHeight) / 2) : 0
    var attributes: [NSAttributedString.Key: Any] = [
      .font: font,
      .foregroundColor: color,
      .paragraphStyle: paragraphStyle,
      .baselineOffset: isMarketText && lineHeight == 20 && font.pointSize == 14 ? ceil(baselineOffset * max(1, traitCollection.displayScale)) / max(1, traitCollection.displayScale) : baselineOffset,
    ]
    if isMarketText { attributes[.kern] = 0 }
    button.setAttributedTitle(
      NSAttributedString(string: text, attributes: attributes),
      for: .normal
    )
  }

  private func showAccessory(
    _ index: Int,
    _ value: String,
    action: (String, NativeSelectionTarget?)? = nil,
    color: UIColor? = nil
  ) {
    guard accessoryButtons.indices.contains(index), !value.isEmpty else { return }
    let button = accessoryButtons[index]
    button.isHidden = false
    button.setTitle(value, for: .normal)
    button.setContentHuggingPriority(.required, for: .horizontal)
    button.setContentCompressionResistancePriority(.required, for: .horizontal)
    if let color { button.setTitleColor(color, for: .normal) }
    while accessoryActions.count <= index { accessoryActions.append(("", nil)) }
    accessoryActions[index] = action ?? ("", nil)
  }

  private func showValuePairAccessory(
    _ index: Int,
    _ accessory: [String: Any],
    theme: [String: Any]?
  ) {
    guard accessoryButtons.indices.contains(index) else { return }
    let primary = accessory.string("primary")
    let secondary = accessory.string("secondary")
    guard !primary.isEmpty || !secondary.isEmpty else { return }
    let button = accessoryButtons[index]
    let text = NSMutableAttributedString()
    let primaryParagraph = NSMutableParagraphStyle()
    primaryParagraph.alignment = .right
    primaryParagraph.minimumLineHeight = 20
    primaryParagraph.maximumLineHeight = 20
    text.append(NSAttributedString(
      string: primary,
      attributes: [
        .font: nativeListFont(ofSize: 16, weight: .medium),
        .foregroundColor: accessoryTextColor(
          accessory.string("primaryTone"),
          defaultTone: "primary",
          theme: theme
        ),
        .paragraphStyle: primaryParagraph,
      ]
    ))
    if !primary.isEmpty, !secondary.isEmpty { text.append(NSAttributedString(string: "\n")) }
    let secondaryParagraph = NSMutableParagraphStyle()
    secondaryParagraph.alignment = .right
    secondaryParagraph.minimumLineHeight = 20
    secondaryParagraph.maximumLineHeight = 20
    text.append(NSAttributedString(
      string: secondary,
      attributes: [
        .font: nativeListFont(ofSize: 14),
        .foregroundColor: accessoryTextColor(
          accessory.string("secondaryTone"),
          defaultTone: "secondary",
          theme: theme
        ),
        .paragraphStyle: secondaryParagraph,
      ]
    ))
    button.isHidden = false
    button.titleLabel?.numberOfLines = 2
    button.contentHorizontalAlignment = .trailing
    button.setAttributedTitle(text, for: .normal)
    button.setContentHuggingPriority(.required, for: .horizontal)
    button.setContentCompressionResistancePriority(.required, for: .horizontal)
    while accessoryActions.count <= index { accessoryActions.append(("", nil)) }
    accessoryActions[index] = ("", nil)
  }

  private func showMenuAccessory(
    _ index: Int,
    action: (String, NativeSelectionTarget?)?
  ) {
    showAccessory(index, "⋮", action: action)
    guard accessoryButtons.indices.contains(index) else { return }
    let button = accessoryButtons[index]
    button.titleLabel?.font = nativeListFont(ofSize: 20, weight: .medium)
    accessorySizeConstraints.append(contentsOf: [
      button.widthAnchor.constraint(equalToConstant: 24),
      button.heightAnchor.constraint(equalToConstant: 24),
    ])
    NSLayoutConstraint.activate(Array(accessorySizeConstraints.suffix(2)))
  }

  private func accessoryTextColor(
    _ tone: String,
    defaultTone: String,
    theme: [String: Any]?
  ) -> UIColor {
    switch tone.isEmpty ? defaultTone : tone {
    case "positive": return nativeListColor(theme, "positive", "#218358")
    case "negative": return nativeListColor(theme, "negative", "#CE2C31")
    // OneKey patch: account warnings and hidden balances use existing theme tokens.
    case "disabled": return nativeListColor(theme, "disabledText", "#8D8D8D")
    case "caution": return nativeListColor(theme, "caution", "#AB6400")
    case "secondary": return nativeListColor(theme, "secondaryText", "#646464")
    default: return nativeListColor(theme, "primaryText", "#202020")
    }
  }

  private func showAccessoryIcon(_ index: Int, _ data: [String: Any]) {
    guard accessoryButtons.indices.contains(index) else { return }
    let button = accessoryButtons[index]
    button.isHidden = false
    button.isEnabled = !data.bool("disabled")
    button.alpha = button.isEnabled ? 1 : 0.4
    let isAccountCreate = currentItem?.data.string("presentation") == "accountSelector" && currentItem?.data["height"] != nil && data.string("name") == "PlusSmallOutline"
    let tintColor = data["tintColor"] == nil && isAccountCreate
      ? nativeListColor(currentTheme, "iconSubdued", "#8D8D8D")
      : UIColor(nativeListHex: data.string("tintColor", default: "#646464"), fallback: .darkGray)
    button.tintColor = tintColor
    if let image = nativeListIcon(named: data.string("name")) {
      button.setImage(image, for: .normal)
      button.setImage(
        image.withTintColor(tintColor, renderingMode: .alwaysOriginal),
        for: .disabled
      )
    }
    let isDrillIn = data.string("kind") == "chevron"
    let isAccountIcon = currentItem?.data.string("presentation") == "accountSelector" && !isDrillIn
    let size: CGFloat = isDrillIn ? 24 : isAccountIcon && !isAccountCreate ? 38 : 36
    if isAccountCreate { button.layer.cornerRadius = 8 }
    if isAccountIcon { rootStack.setCustomSpacing(5, after: mainStack) }
    button.accessibilityIdentifier = data["testID"] as? String
    button.accessibilityLabel = data["accessibilityLabel"] as? String
    if !isDrillIn, !data.string("actionKey").isEmpty {
      // Reproduce the trailing edge of IconButton's m=-7 while keeping its
      // full 36-point frame for padding/highlight behavior.
      rootTrailingConstraint.constant = -5
    }
    accessorySizeConstraints.append(contentsOf: [
      button.widthAnchor.constraint(equalToConstant: size),
      button.heightAnchor.constraint(equalToConstant: size),
    ])
    NSLayoutConstraint.activate(Array(accessorySizeConstraints.suffix(2)))
    while accessoryActions.count <= index { accessoryActions.append(("", nil)) }
    accessoryActions[index] = data.bool("disabled")
      ? ("", nil)
      : (data.string("actionKey"), nil)
  }

  private func accessoryAction(
    _ accessory: [String: Any],
    defaultKey: String = ""
  ) -> (String, NativeSelectionTarget?)? {
    if accessory.bool("disabled") { return nil }
    let key = accessory.string("actionKey", default: defaultKey)
    return key.isEmpty ? nil : (key, nil)
  }

  private func bindImage(
    _ source: [String: Any],
    into imageView: OneKeyImageReusableView,
    token: String,
    slot: Int,
    variant: String,
    onLoad: (() -> Void)? = nil,
    onError: (() -> Void)? = nil,
    hideUntilLoaded: Bool = false,
    retryAttempt: Int = 0
  ) {
    let imageID = ObjectIdentifier(imageView)
    selectorImageRetries.removeValue(forKey: imageID)?.cancel()
    let expectedEpoch = bindingEpoch
    let retryLimit = max(0, source.int("retryTimes", default: 0))
    let headersJson: String?
    if let headers = source.dictionary("headers"),
       JSONSerialization.isValidJSONObject(headers),
       let data = try? JSONSerialization.data(withJSONObject: headers) {
      headersJson = String(data: data, encoding: .utf8)
    } else {
      headersJson = nil
    }
    if hideUntilLoaded { imageView.isHidden = true }
    let handleLoad: (() -> Void)? = hideUntilLoaded ? { [weak self, weak imageView] in
      guard let self, self.bindingEpoch == expectedEpoch else { return }
      imageView?.isHidden = false
      onLoad?()
    } : onLoad
    let handleError: (() -> Void)? = hideUntilLoaded ? { [weak self, weak imageView] in
      guard let self, self.bindingEpoch == expectedEpoch else { return }
      imageView?.isHidden = true
      onError?()
    } : onError
    imageView.configure(
      sourceUri: source.string("uri").trimmingCharacters(in: .whitespacesAndNewlines),
      sourceHeadersJson: headersJson,
      variant: variant,
      contentFit: source.string("contentFit", default: "cover"),
      cachePolicy: source.string("cachePolicy", default: "memory-disk"),
      autoplay: source.bool("autoplay"),
      recyclingKey: retryAttempt == 0 ? "\(token):\(slot)" : "\(token):\(slot):retry:\(retryAttempt)",
      optimizeTos: retryAttempt == 0 && (source["optimizeTos"] == nil || source.bool("optimizeTos")),
      overscan: source["overscan"] == nil ? 1.1 : source.double("overscan"),
      loadingStrategy: source.string("loadingStrategy", default: "static"),
      placeholderColor: currentTheme?["strongBackground"] as? String ?? "#0000000F",
      onLoad: retryLimit == 0 ? handleLoad : { [weak self] in
        guard let self, self.bindingEpoch == expectedEpoch else { return }
        self.selectorImageRetries.removeValue(forKey: imageID)?.cancel()
        handleLoad?()
      },
      onError: retryLimit == 0 ? handleError : { [weak self, weak imageView] in
        guard let self, self.bindingEpoch == expectedEpoch, let imageView else { return }
        guard retryAttempt < retryLimit else { handleError?(); return }
        guard self.selectorImageRetries[imageID] == nil else { return }
        let retry = DispatchWorkItem { [weak self, weak imageView] in
          guard let self, self.bindingEpoch == expectedEpoch, let imageView else { return }
          self.selectorImageRetries.removeValue(forKey: imageID)
          self.bindImage(source, into: imageView, token: token, slot: slot, variant: variant, onLoad: onLoad, onError: onError, hideUntilLoaded: hideUntilLoaded, retryAttempt: retryAttempt + 1)
        }
        self.selectorImageRetries[imageID] = retry
        DispatchQueue.main.asyncAfter(deadline: .now() + Double(Int.random(in: 0...2)), execute: retry)
      }
    )
  }

  private func applyGroupPosition(_ position: String) {
    let top: CACornerMask = [.layerMinXMinYCorner, .layerMaxXMinYCorner]
    let bottom: CACornerMask = [.layerMinXMaxYCorner, .layerMaxXMaxYCorner]
    switch position {
    case "first": layer.maskedCorners = top
    case "last": layer.maskedCorners = bottom
    case "single": layer.maskedCorners = top.union(bottom)
    default:
      layer.maskedCorners = []
      layer.cornerRadius = 0
      layer.masksToBounds = false
      return
    }
    let isWalletSidebar = currentItem?.type == "identity"
      && currentItem?.data.string("presentation") == "walletSidebar"
    layer.cornerRadius = isWalletSidebar ? 20 : 12
    layer.cornerCurve = isWalletSidebar ? .continuous : .circular
    layer.masksToBounds = true
  }

  private func configureSkeleton(
    _ view: UIView,
    width: CGFloat,
    height: CGFloat,
    theme: [String: Any]?
  ) {
    view.backgroundColor = nativeListColor(theme, "rowPressedBackground", "#E8E8E8")
    view.layer.cornerRadius = 4
    view.translatesAutoresizingMaskIntoConstraints = false
    if view.constraints.isEmpty {
      NSLayoutConstraint.activate([
        view.widthAnchor.constraint(equalToConstant: width),
        view.heightAnchor.constraint(equalToConstant: height),
      ])
    }
  }

  @objc private func accessoryPressed(_ sender: UIButton) {
    guard let item = currentItem, accessoryActions.indices.contains(sender.tag) else { return }
    let action = accessoryActions[sender.tag]
    guard !action.0.isEmpty else { return }
    let source = item.type == "mediaTile" && item.data.string("closeActionKey") == action.0
      ? "mediaClose"
      : "trailingAccessory"
    onAction?(
      item,
      action.0,
      action.1,
      actionOrigin(sourceView: sender, source: source, slot: sender.tag)
    )
  }

  @objc private func marketBadgePressed(_ sender: UIButton) {
    guard let item = currentItem,
          marketBadgeActionKeys.indices.contains(sender.tag),
          let actionKey = marketBadgeActionKeys[sender.tag],
          !actionKey.isEmpty else { return }
    onAction?(
      item,
      actionKey,
      nil,
      actionOrigin(sourceView: sender, source: "marketBadge", slot: sender.tag)
    )
  }

  @objc private func footerActionPressed(_ sender: UIButton) {
    guard let item = currentItem, footerActionKeys.indices.contains(sender.tag) else { return }
    onAction?(
      item,
      footerActionKeys[sender.tag],
      nil,
      actionOrigin(sourceView: sender, source: "footerAction", slot: sender.tag)
    )
  }

  @objc private func checkboxPressed() {
    guard let item = currentItem, let action = checkboxAction else { return }
    onAction?(
      item,
      action.0,
      action.1,
      actionOrigin(sourceView: checkboxButton, source: "trailingAccessory")
    )
  }

  @objc private func leadingActionPressed() {
    guard let item = currentItem, let actionKey = leadingActionKey, !actionKey.isEmpty else { return }
    onAction?(
      item,
      actionKey,
      nil,
      actionOrigin(sourceView: leadingActionButton, source: "leadingAction")
    )
  }

  private func actionOrigin(
    sourceView: UIView,
    source: String,
    slot: Int? = nil
  ) -> NativeListActionOrigin {
    let isAccountIcon = currentItem?.data.string("presentation") == "accountSelector"
      && source == "trailingAccessory"
      && accessoryButtons.contains { $0 === sourceView }
      && sourceView.bounds.width == 38
    return NativeListActionOrigin(
      sourceView: sourceView,
      ownerCell: self,
      bindingEpoch: bindingEpoch,
      source: source,
      slot: slot,
      anchorInset: isAccountIcon ? 7 : 0
    )
  }

  func rowActionOrigin() -> NativeListActionOrigin {
    actionOrigin(sourceView: contentView, source: "row")
  }

  private func invalidateCurrentBinding() {
    selectorImageRetries.values.forEach { $0.cancel() }
    selectorImageRetries.removeAll()
    guard currentItem != nil else { return }
    onBindingInvalidated?(self, bindingEpoch)
    bindingEpoch &+= 1
  }
}
