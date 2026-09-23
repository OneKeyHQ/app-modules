import UIKit

final class NativeListMediaTileCell: NativeListRendererCell {
  private let picture = UIView()
  private let image = NativeListImageSlot()
  private let network = NativeListImageSlot()
  private let errorIcon = UIImageView()
  private let title = NativeListTextLabel()
  private let subtitle = NativeListTextLabel()
  private let badge = NativeListTextLabel()
  private let metadata = UIStackView()
  private let subtitleLine = UIStackView()
  private let close = UIButton(type: .system)
  private let closeStack = UIStackView()
  private let titleLine = UIStackView()
  private var closeKey = ""
  private var imageStyle: [String: Any] = [:]
  private var imageWidth: NSLayoutConstraint!
  private var imageHeight: NSLayoutConstraint!
  override var defaultCornerRadius: CGFloat { 16 }
  override var showsSelection: Bool { false }
  override var pressChangesBackground: Bool { false }
  override var assetFields: [String] { ["image", "networkImage"] }
  override var isHighlighted: Bool { didSet { picture.alpha = isHighlighted ? 0.8 : 1 } }

  override init(frame: CGRect) {
    super.init(frame: frame)
    root.axis = .vertical
    root.alignment = .fill
    metadata.axis = .vertical
    metadata.alignment = .fill
    subtitleLine.axis = .horizontal
    subtitleLine.alignment = .center
    subtitleLine.spacing = 8
    [subtitle, network.view].forEach(subtitleLine.addArrangedSubview)
    titleLine.axis = .horizontal
    titleLine.alignment = .center
    titleLine.addArrangedSubview(title)
    [subtitleLine, titleLine].forEach(metadata.addArrangedSubview)
    closeStack.axis = .vertical
    closeStack.alignment = .trailing
    closeStack.addArrangedSubview(close)
    [picture, metadata, closeStack].forEach(root.addArrangedSubview)
    picture.translatesAutoresizingMaskIntoConstraints = false
    [image.view, errorIcon, badge].forEach {
      picture.addSubview($0)
      $0.translatesAutoresizingMaskIntoConstraints = false
    }
    imageWidth = image.view.widthAnchor.constraint(equalToConstant: 0)
    imageHeight = picture.heightAnchor.constraint(equalToConstant: 160)
    NSLayoutConstraint.activate([
      image.view.leadingAnchor.constraint(equalTo: picture.leadingAnchor),
      image.view.topAnchor.constraint(equalTo: picture.topAnchor),
      image.view.heightAnchor.constraint(equalTo: picture.heightAnchor), imageWidth, imageHeight,
      errorIcon.centerXAnchor.constraint(equalTo: image.view.centerXAnchor),
      errorIcon.centerYAnchor.constraint(equalTo: image.view.centerYAnchor),
      errorIcon.widthAnchor.constraint(equalToConstant: 24),
      errorIcon.heightAnchor.constraint(equalToConstant: 24),
      badge.trailingAnchor.constraint(equalTo: image.view.trailingAnchor),
      badge.bottomAnchor.constraint(equalTo: image.view.bottomAnchor),
      badge.heightAnchor.constraint(equalToConstant: 24),
      network.view.widthAnchor.constraint(equalToConstant: 14),
      network.view.heightAnchor.constraint(equalToConstant: 14),
    ])
    errorIcon.contentMode = .scaleAspectFit
    badge.textAlignment = .center
    badge.layer.cornerRadius = 10
    badge.layer.borderWidth = 2
    badge.clipsToBounds = true
    network.view.layer.cornerRadius = 7
    network.view.clipsToBounds = true
    subtitle.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
    close.contentHorizontalAlignment = .trailing
    close.addTarget(self, action: #selector(closePressed), for: .touchUpInside)
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  override func bindContent(
    _ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    let style = item.data.dictionary("style") ?? [:]
    imageStyle = style.dictionary("image") ?? [:]
    let hp = CGFloat(style.double("horizontalPadding", default: 10))
    let vp = CGFloat(style.double("verticalPadding", default: 10))
    contentInsets = UIEdgeInsets(top: vp, left: hp, bottom: vp, right: hp)
    root.spacing = 8
    root.setCustomSpacing(CGFloat(style.double("leadingGap", default: 8)), after: picture)
    metadata.spacing = CGFloat(style.double("lineGap", default: 2))
    NativeListResolvedText(
      item.data.string("title"), style: style.dictionary("title"), size: 16,
      weight: .medium, color: nativeListColor(theme, "primaryText", "#202020"),
      lineHeight: nativeListFont(ofSize: 16, weight: .medium).lineHeight,
      lines: 1
    ).bind(title)
    NativeListResolvedText(
      item.data.string("subtitle"), style: style.dictionary("subtitle"), size: 12,
      color: nativeListColor(theme, "secondaryText", "#646464"),
      lineHeight: nativeListFont(ofSize: 12).lineHeight, lines: 1
    ).bind(subtitle)
    let badgeText = item.data.dictionary("badge")?.string("text") ?? ""
    NativeListResolvedText(
      badgeText.isEmpty ? "" : "  \(badgeText)  ", style: style.dictionary("badge"), size: 14,
      weight: .medium, color: nativeListColor(theme, "inverseText", "#FCFCFC"),
      lineHeight: nativeListFont(ofSize: 14, weight: .medium).lineHeight,
      lines: 1
    ).bind(badge)
    if style.dictionary("badge")?["alignment"] == nil { badge.textAlignment = .center }
    badge.backgroundColor = nativeListColor(theme, "inverseBackground", "#202020")
    badge.layer.borderColor = nativeListColor(theme, "rowBackground", "#FFFFFF").cgColor
    let state = item.data.string("imageState")
    errorIcon.isHidden = state != "error"
    errorIcon.image = nativeListIcon(named: "ImageSquareWavesOutline")
    errorIcon.tintColor = UIColor(nativeListHex: "#00000044", fallback: .lightGray)
    if state != "empty", state != "error", let source = item.data.dictionary("image") {
      image.bind(source, key: "\(item.key):media", fit: imageStyle["contentFit"] as? String)
    } else {
      image.recycle()
      image.view.backgroundColor =
        state == "error" ? nativeListColor(theme, "strongBackground", "#0000000F") : .clear
    }
    if let source = item.data.dictionary("networkImage") {
      network.view.isHidden = false
      network.view.alpha = 0
      network.bind(source, key: "\(item.key):network", variant: "network") { [weak self] loaded in
        self?.network.view.alpha = loaded ? 1 : 0
      }
    } else {
      network.recycle()
      network.view.isHidden = true
    }
    closeKey = item.data.string("closeActionKey")
    closeStack.isHidden = closeKey.isEmpty
    close.setTitle("×", for: .normal)
    close.titleLabel?.font = nativeListFont(ofSize: 16, weight: .medium)
    close.setTitleColor(nativeListColor(theme, "primaryText", "#202020"), for: .normal)
  }
  override func layoutSubviews() {
    let width = CGFloat(
      imageStyle.double(
        "width",
        default: Double(max(0, contentView.bounds.width - contentInsets.left - contentInsets.right))
      ))
    imageWidth.constant = width
    imageHeight.constant = CGFloat(imageStyle.double("height", default: Double(width)))
    let shape = imageStyle.string("shape", default: "rounded")
    image.view.layer.cornerRadius = CGFloat(
      imageStyle.double(
        "cornerRadius",
        default: shape == "circle"
          ? Double(min(width, imageHeight.constant) / 2) : shape == "square" ? 0 : 10))
    image.view.clipsToBounds = true
    super.layoutSubviews()
  }
  @objc private func closePressed() {
    emitAction(closeKey, from: close, source: "mediaClose", slot: 0)
  }
  override func recycleContent() {
    image.recycle()
    network.recycle()
    closeKey = ""
    picture.alpha = 1
  }
}
