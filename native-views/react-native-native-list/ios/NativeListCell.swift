import Foundation
import OneKeyImage
import UIKit

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
    let y = bounds.height + 1 + dottedUnderlineVerticalOffset
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
  private let tertiaryLabel = UILabel()
  private let statusLabel = NativeListInsetLabel()
  private let metricSubtitleLabel = UILabel()
  private let metricCompositeStack = UIStackView()
  private let badgeLabel = NativeListInsetLabel()
  private let actionStack = UIStackView()
  private let actionButtons = (0..<3).map { _ in UIButton(type: .system) }
  private let trailingStack = UIStackView()
  private let accessoryButtons = (0..<2).map { _ in UIButton(type: .system) }
  private let checkboxButton = UIButton(type: .system)
  private let spinner = UIActivityIndicatorView(style: .medium)
  private let dataStack = UIStackView()
  private let dataLabels = (0..<4).map { _ in UILabel() }
  private let tableDataStack = UIStackView()
  private let tableDataColumns = (0..<4).map { _ in NativeListTableColumnView() }
  private let mediaBadgeLabel = UILabel()
  private let skeletonPrimary = UIView()
  private let skeletonSecondary = UIView()
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
  private var currentItem: NativeListItem?
  private var accessoryActions: [(String, NativeSelectionTarget?)] = []
  private var footerActionKeys: [String] = []
  private var checkboxAction: (String, NativeSelectionTarget?)?
  private var boundCheckboxData: [String: Any]?
  private var boundCheckboxTarget: NativeSelectionTarget?
  private var leadingActionKey: String?
  private var restingBackgroundColor: UIColor = .clear
  private var pressedBackgroundColor = UIColor(
    nativeListHex: "#E8E8E8",
    fallback: .lightGray
  )
  private var checkboxCheckedColor = UIColor(nativeListHex: "#202020", fallback: .black)
  private var checkboxUncheckedColor = UIColor(nativeListHex: "#FCFCFC", fallback: .white)
  private var checkboxBorderColor = UIColor(nativeListHex: "#CECECE", fallback: .lightGray)
  private var visualBackdropColor = UIColor.white
  private var currentLayout = "linear"
  private var currentTheme: [String: Any]?
  private var currentItemIndex: Int?

  var onAction: ((NativeListItem, String, NativeSelectionTarget?) -> Void)?

  override var isHighlighted: Bool {
    didSet { updateBackgroundColor() }
  }

  override init(frame: CGRect) {
    super.init(frame: frame)
    contentView.addSubview(rootStack)
    contentView.addSubview(separatorView)
    rootStack.translatesAutoresizingMaskIntoConstraints = false
    separatorView.translatesAutoresizingMaskIntoConstraints = false
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
    ])
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
    if currentItem?.type == "mediaTile" {
      mediaHeight.constant = max(0, contentView.bounds.width - 20)
    }
  }

  override func prepareForReuse() {
    super.prepareForReuse()
    isHighlighted = false
    restingBackgroundColor = .clear
    contentView.backgroundColor = restingBackgroundColor
    currentItem = nil
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
    contentView.alpha = isUserInteractionEnabled ? 1 : 0.5
    accessibilityLabel = item.data.string("accessibilityLabel", default: item.data.string("title"))

    switch item.type {
    case "identity": bindIdentity(item, theme: theme, selected: selected, checkboxState)
    case "rail": bindRail(item, theme: theme)
    case "activity": bindActivity(item, theme: theme)
    case "message": bindMessage(item, theme: theme)
    case "dataRow": bindDataRow(item, theme: theme, checkboxState)
    case "mediaTile": bindMediaTile(item, theme: theme)
    case "metricCard": bindMetricCard(item, theme: theme)
    case "sectionHeader": bindSectionHeader(item, theme: theme, layout: layout, checkboxState)
    case "action": bindAction(item, theme: theme, checkboxState)
    case "system": bindSystem(item, theme: theme)
    default: break
    }
  }

  func updateSelection(
    item: NativeListItem,
    selected: Bool,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    guard currentItem?.key == item.key else { return }
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
    guard let data = boundCheckboxData, let target = boundCheckboxTarget else { return }
    updateCheckboxPresentation(
      item,
      data,
      target: target,
      state: checkboxState(item, target, data.string("state", default: "unchecked"))
    )
  }

  private func updateSummaryText(_ item: NativeListItem) {
    let title = item.data.string("title")
    titleLabel.isHidden = title.isEmpty
    setLineHeight(titleLabel, text: title, lineHeight: 24)

    let value = item.data.string("value")
    let valueButton = accessoryButtons[0]
    valueButton.isHidden = value.isEmpty
    if value.isEmpty {
      valueButton.setTitle(nil, for: .normal)
      valueButton.setAttributedTitle(nil, for: .normal)
    } else {
      setButtonLine(
        valueButton,
        text: value,
        font: nativeListFont(ofSize: 16),
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
    } else if layout == "sectioned" {
      // Checkbox-backed section lists in app-monorepo keep rows on $bg;
      // selection is represented by the checkbox itself.
      color = nativeListColor(theme, "rowBackground", "#FFFFFF")
    } else if layout == "table",
              item.type == "dataRow",
              (item.data["index"] == nil ? itemIndex : item.data.int("index")).map({ $0 % 2 == 0 }) == true,
              !selected {
      color = nativeListColor(theme, "subduedBackground", "#F9F9F9")
    }
    return color
  }

  private func reset() {
    isHighlighted = false
    rootStack.arrangedSubviews.forEach {
      rootStack.removeArrangedSubview($0)
      $0.removeFromSuperview()
    }
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
    mediaMetadataStack.removeArrangedSubview(subtitleLabel)
    mediaMetadataStack.removeArrangedSubview(mediaNetworkImage)
    mediaMetadataStack.removeFromSuperview()
    mainStack.removeArrangedSubview(titleRowStack)
    titleRowStack.removeFromSuperview()
    mainStack.removeArrangedSubview(subtitleLabel)
    subtitleLabel.removeFromSuperview()
    mainStack.insertArrangedSubview(titleRowStack, at: 0)
    mainStack.insertArrangedSubview(subtitleLabel, at: 1)
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
      button.titleLabel?.font = nativeListFont(
        ofSize: index == 0 ? 16 : 14,
        weight: index == 0 ? .medium : .regular
      )
      button.isHidden = true
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
    layer.maskedCorners = []
    layer.cornerRadius = 0
    layer.cornerCurve = .circular
    contentView.layer.cornerRadius = 0
    contentView.layer.cornerCurve = .circular
    contentView.clipsToBounds = false
    leadingContainer.alpha = 1
    titleLabel.showsDottedUnderline = false
    titleLabel.dottedUnderlineVerticalOffset = 0
  }

  func setPressed(_ pressed: Bool) {
    isHighlighted = pressed && isUserInteractionEnabled
  }

  private func updateBackgroundColor() {
    let pressed = isHighlighted && isUserInteractionEnabled
    contentView.backgroundColor = pressed ? pressedBackgroundColor : restingBackgroundColor
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
      let restingRadius: CGFloat = currentItem?.type == "metricCard" ? 12 : 0
      contentView.layer.cornerRadius = restingRadius
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
      show(titleLabel, item.data.string("title"), lines: 1)
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 16)
      titleLabel.textColor = nativeListColor(
        theme,
        selected ? "primaryText" : "secondaryText",
        selected ? "#FFFFFFED" : "#FFFFFFAF"
      )
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
    rootStack.addArrangedSubview(mainStack)
    show(titleLabel, item.data.string("title"), lines: item.data.int("titleLines", default: 1))
    show(subtitleLabel, item.data.string("subtitle"), lines: item.data.int("subtitleLines", default: 1))
    show(tertiaryLabel, item.data.string("tertiary"), lines: 1)
    if !item.data.string("subtitle").isEmpty, item.data.string("tertiary").isEmpty {
      mainStack.spacing = 0
      setLineHeight(titleLabel, text: item.data.string("title"), lineHeight: 24)
      setLineHeight(subtitleLabel, text: item.data.string("subtitle"), lineHeight: 20)
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
      mediaNetworkImage.isHidden = false
      bindImage(
        networkImage,
        into: mediaNetworkImage,
        token: item.key,
        slot: 2,
        variant: "network"
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
    let isSummary = variant == "summary"
    let isGallery = variant == "gallery"
    let isTable = layout == "table"
    let isNetworkSelector = item.data.string("presentation") == "networkSelector"
    let isHistory = variant == "history" ||
      item.key.hasPrefix("history-") ||
      (item.sectionKey?.hasPrefix("history-") ?? false)
    let headerWeight: NativeListFontWeight = isSummary
      ? .medium
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
      titleLabel.showsDottedUnderline = true
      titleLabel.dottedUnderlineVerticalOffset = 2
      titleLabel.dottedUnderlineColor = nativeListColor(
        theme,
        "secondaryText",
        "#646464"
      )
      rootLeadingConstraint.constant = 12
      rootTrailingConstraint.constant = -12
      rootTopConstraint.constant = 12
      rootBottomConstraint.constant = -15
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
      accessoryButtons[0].titleLabel?.font = nativeListFont(ofSize: 16)
      setButtonLine(
        accessoryButtons[0],
        text: item.data.string("value"),
        font: nativeListFont(ofSize: 16),
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
      titleLabel.font = nativeListFont(ofSize: 16)
      titleLabel.textColor = nativeListColor(theme, "secondaryText", "#646464")
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

  private func dataTextColor(_ tone: String, theme: [String: Any]?) -> UIColor {
    switch tone.isEmpty ? "primary" : tone {
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
    if variant == "loading" {
      leadingWidth.constant = 40
      leadingHeight.constant = 40
      leadingContainer.backgroundColor = nativeListColor(theme, "strongBackground", "#F0F0F0")
      leadingContainer.layer.cornerRadius = 20
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
    fallbackLabel.text = String(visual.string("fallbackText").prefix(2))
    leadingContainer.backgroundColor = UIColor(
      nativeListHex: visual.string("backgroundColor", default: isIcon ? "#F0F0F0" : "#E0E0E0"),
      fallback: .gray
    )
    leadingContainer.layer.cornerRadius = leadingCornerRadius(shape: shape)
    leadingContainer.clipsToBounds = true
    fallbackLabel.isHidden = isIcon || !sources.isEmpty
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
      imageView.isHidden = false
      imageView.clipsToBounds = true
      leadingSlotConstraints.append(contentsOf: leadingConstraints(
        imageView,
        index: index,
        count: visibleSources.count,
        tokenPair: tokenPair,
        shape: shape
      ))
      bindImage(source.data, into: imageView, token: key, slot: index, variant: source.variant)
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
    checkboxButton.tintColor = checkboxUncheckedColor
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
    var attributes: [NSAttributedString.Key: Any] = [
      .font: label.font as Any,
      .foregroundColor: label.textColor as Any,
      .paragraphStyle: paragraphStyle,
    ]
    if letterSpacing != 0 { attributes[.kern] = letterSpacing }
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
    paragraphStyle.alignment = .center
    button.setAttributedTitle(
      NSAttributedString(
        string: text,
        attributes: [
          .font: font,
          .foregroundColor: color,
          .paragraphStyle: paragraphStyle,
        ]
      ),
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
    let tintColor = UIColor(
      nativeListHex: data.string("tintColor", default: "#646464"),
      fallback: .darkGray
    )
    button.tintColor = tintColor
    if let image = nativeListIcon(named: data.string("name")) {
      button.setImage(image, for: .normal)
      button.setImage(
        image.withTintColor(tintColor, renderingMode: .alwaysOriginal),
        for: .disabled
      )
    }
    let isDrillIn = data.string("kind") == "chevron"
    let size: CGFloat = isDrillIn ? 24 : 36
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
    variant: String
  ) {
    let headersJson: String?
    if let headers = source.dictionary("headers"),
       JSONSerialization.isValidJSONObject(headers),
       let data = try? JSONSerialization.data(withJSONObject: headers) {
      headersJson = String(data: data, encoding: .utf8)
    } else {
      headersJson = nil
    }
    imageView.configure(
      sourceUri: source.string("uri").trimmingCharacters(in: .whitespacesAndNewlines),
      sourceHeadersJson: headersJson,
      variant: variant,
      contentFit: source.string("contentFit", default: "cover"),
      cachePolicy: source.string("cachePolicy", default: "memory-disk"),
      autoplay: source.bool("autoplay"),
      recyclingKey: "\(token):\(slot)",
      optimizeTos: source["optimizeTos"] == nil || source.bool("optimizeTos"),
      overscan: source["overscan"] == nil ? 1.1 : source.double("overscan"),
      loadingStrategy: source.string("loadingStrategy", default: "static")
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
    onAction?(item, action.0, action.1)
  }

  @objc private func footerActionPressed(_ sender: UIButton) {
    guard let item = currentItem, footerActionKeys.indices.contains(sender.tag) else { return }
    onAction?(item, footerActionKeys[sender.tag], nil)
  }

  @objc private func checkboxPressed() {
    guard let item = currentItem, let action = checkboxAction else { return }
    onAction?(item, action.0, action.1)
  }

  @objc private func leadingActionPressed() {
    guard let item = currentItem, let actionKey = leadingActionKey, !actionKey.isEmpty else { return }
    onAction?(item, actionKey, nil)
  }
}
