import UIKit

/// The activity template's bounded rich presentation; all values are caller-formatted.
final class NativeListActivityDetailsView: UIStackView {
  var onAction: ((String, UIView, String, Int) -> Void)?
  private let main = UIStackView()
  private let visual = NativeListLeadingVisual()
  private let identity = UIStackView()
  private let titleLine = UIStackView()
  private let title = NativeListTextLabel()
  private let descriptionLabel = NativeListTextLabel()
  private let status = NativeListTextLabel()
  private let amounts = UIStackView()
  private let fee = UIStackView()
  private let feeLabel = NativeListTextLabel()
  private let feeLine = UIStackView()
  private let feePrimary = NativeListTextLabel()
  private let feeSecondary = NativeListTextLabel()
  private let actions = UIStackView()
  private var amountViews: [NativeListActivityAmountView] = []
  private var badgeViews: [NativeListTextLabel] = []
  private var buttons: [UIButton] = []
  private var actionKeys: [String] = []
  private var descriptionKey = ""
  private var dimensions: [NSLayoutConstraint] = []

  override init(frame: CGRect) {
    super.init(frame: frame)
    axis = .vertical; alignment = .fill; spacing = 8
    main.axis = .horizontal; main.alignment = .center; main.spacing = 12
    identity.axis = .vertical; identity.alignment = .fill
    titleLine.axis = .horizontal; titleLine.alignment = .center; titleLine.spacing = 4
    titleLine.addArrangedSubview(title)
    [titleLine, descriptionLabel, status].forEach(identity.addArrangedSubview)
    amounts.axis = .vertical; amounts.alignment = .fill; amounts.spacing = 2
    fee.axis = .vertical; fee.alignment = .trailing
    feeLine.axis = .horizontal; feeLine.spacing = 4
    [feePrimary, feeSecondary].forEach(feeLine.addArrangedSubview)
    [feeLabel, feeLine].forEach(fee.addArrangedSubview)
    [visual, identity, amounts, fee].forEach(main.addArrangedSubview)
    [main, actions].forEach(addArrangedSubview)
    actions.axis = .horizontal; actions.alignment = .leading; actions.spacing = 8
    actions.isLayoutMarginsRelativeArrangement = true
    descriptionLabel.isUserInteractionEnabled = true
    descriptionLabel.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(descriptionPressed)))
    title.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
    identity.setContentHuggingPriority(.defaultLow, for: .horizontal)
    amounts.setContentHuggingPriority(.defaultHigh, for: .horizontal)
  }
  required init(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  static func measure(_ data: [String: Any], width: CGFloat) -> CGFloat {
    let style = data.dictionary("style") ?? [:]
    let primary = style.dictionary("primaryAmount") ?? [:]
    let secondary = style.dictionary("secondaryAmount") ?? [:]
    let rows = data.dictionaries("amounts")
    let gap = CGFloat(style.double("trailingGap", default: 2))
    let amountsHeight = rows.reduce(CGFloat(0)) { height, row in
      height + CGFloat(primary.double("lineHeight", default: 24)) +
        (row.string("secondaryText").isEmpty ? 0 : CGFloat(secondary.double("lineHeight", default: 20)))
    } + CGFloat(max(0, rows.count - 1)) * gap
    let titleHeight = CGFloat(style.dictionary("title")?.double("lineHeight", default: 24) ?? 24)
    let description = NativeListResolvedText(data.string("description"), style: style.dictionary("description"),
      size: 14, color: .label, lineHeight: 20, lines: 2)
    let identityHeight = titleHeight + description.measure(width: max(1, width * 0.4 - 52)) +
      (data.string("status").isEmpty ? 0 : 16)
    let imageHeight = CGFloat(style.dictionary("image")?.double("height", default: 40) ?? 40)
    return max(imageHeight, identityHeight, amountsHeight) +
      CGFloat(style.double("verticalPadding", default: 8)) * 2 +
      (data.dictionaries("footerActions").isEmpty ? 0 : 40)
  }

  func bind(_ item: NativeListItem, theme: [String: Any]?) {
    let data = item.data
    let style = data.dictionary("style") ?? [:]
    let image = style.dictionary("image") ?? [:]
    let table = data.string("presentation") == "table"
    let gap = CGFloat(style.double("leadingGap", default: 12))
    main.spacing = gap
    identity.spacing = CGFloat(style.double("lineGap"))
    amounts.spacing = CGFloat(style.double("trailingGap", default: 2))
    titleLine.spacing = CGFloat(style.double("titleBadgeGap", default: 4))
    NSLayoutConstraint.deactivate(dimensions)
    dimensions = [
      visual.widthAnchor.constraint(equalToConstant: CGFloat(image.double("width", default: 40))),
      visual.heightAnchor.constraint(equalToConstant: CGFloat(image.double("height", default: 40))),
      amounts.widthAnchor.constraint(lessThanOrEqualTo: main.widthAnchor, multiplier: table ? 0.42 : 0.5),
    ]
    if table { dimensions.append(amounts.widthAnchor.constraint(equalTo: identity.widthAnchor)) }
    NSLayoutConstraint.activate(dimensions)
    visual.bind(data.dictionary("leading") ?? [:], style: image, key: item.key, theme: theme,
      isUnread: false, secondaryVisual: data.dictionary("secondaryLeading"))
    NativeListResolvedText(data.string("title"), style: style.dictionary("title"), size: 16,
      weight: .medium, color: nativeListColor(theme, "primaryText", "#202020"), lineHeight: 24, lines: 1).bind(title)
    NativeListResolvedText(data.string("description"), style: style.dictionary("description"), size: 14,
      color: nativeListColor(theme, "secondaryText", "#646464"), lineHeight: 20, lines: 2).bind(descriptionLabel)
    NativeListResolvedText(data.string("status"), style: style.dictionary("status"), size: 12,
      color: nativeListColor(theme, "secondaryText", "#646464"), lineHeight: 16, lines: 1).bind(status)
    descriptionKey = data.string("descriptionActionKey")
    descriptionLabel.isUserInteractionEnabled = !descriptionKey.isEmpty
    descriptionLabel.accessibilityTraits = descriptionKey.isEmpty ? .staticText : .button
    for badge in badgeViews { titleLine.removeArrangedSubview(badge); badge.removeFromSuperview() }
    badgeViews = data.dictionaries("badges").map { descriptor in
      let label = NativeListTextLabel()
      let tone = descriptor.string("tone")
      let tint = nativeListActivityTone(tone, theme: theme)
      NativeListResolvedText("  \(descriptor.string("text"))  ", style: nil, size: 12,
        weight: .medium, color: tint, lineHeight: 16, lines: 1).bind(label)
      label.backgroundColor = tint.withAlphaComponent(0.1)
      label.layer.cornerRadius = 4; label.clipsToBounds = true
      label.setContentCompressionResistancePriority(.required, for: .horizontal)
      titleLine.addArrangedSubview(label)
      return label
    }
    let descriptors = data.dictionaries("amounts")
    while amountViews.count > descriptors.count {
      let view = amountViews.removeLast(); view.recycle(); amounts.removeArrangedSubview(view); view.removeFromSuperview()
    }
    while amountViews.count < descriptors.count {
      let view = NativeListActivityAmountView(); amountViews.append(view); amounts.addArrangedSubview(view)
    }
    for (index, descriptor) in descriptors.enumerated() {
      amountViews[index].bind(descriptor, key: "\(item.key):\(descriptor.string("key"))", style: style, theme: theme, table: table)
    }
    let feeData = data.dictionary("fee")
    fee.isHidden = feeData == nil
    fee.alpha = feeData?.bool("hidden") == true ? 0 : 1
    NativeListResolvedText(feeData?.string("label") ?? "", style: style.dictionary("secondaryAmount"),
      size: 12, color: nativeListColor(theme, "secondaryText", "#646464"), lineHeight: 16, lines: 1).bind(feeLabel)
    NativeListResolvedText(feeData?.string("primary") ?? "", style: style.dictionary("secondaryAmount"),
      size: 14, color: nativeListColor(theme, "primaryText", "#202020"), lineHeight: 20, lines: 1).bind(feePrimary)
    NativeListResolvedText(feeData?.string("secondary") ?? "", style: style.dictionary("secondaryAmount"),
      size: 12, color: nativeListColor(theme, "secondaryText", "#646464"), lineHeight: 16, lines: 1).bind(feeSecondary)
    for button in buttons { actions.removeArrangedSubview(button); button.removeFromSuperview() }
    buttons = []
    nativeListActivitySegments(feePrimary, feeData?.dictionaries("primaryTextSegments") ?? [])
    nativeListActivitySegments(feeSecondary, feeData?.dictionaries("secondaryTextSegments") ?? [])
    let actionData = data.dictionaries("footerActions")
    actionKeys = actionData.map { $0.string("key") }
    actions.isHidden = actionData.isEmpty
    actions.directionalLayoutMargins = NSDirectionalEdgeInsets(top: 0, leading: CGFloat(image.double("width", default: 40)) + gap, bottom: 0, trailing: 0)
    for (index, descriptor) in actionData.enumerated() {
      let button = UIButton(type: .system); button.tag = index
      button.setTitle(descriptor.string("label"), for: .normal)
      button.titleLabel?.font = nativeListFont(ofSize: 14, weight: .medium)
      button.setTitleColor(nativeListActivityTone(descriptor.string("tone"), theme: theme), for: .normal)
      button.backgroundColor = nativeListColor(theme, "strongBackground", "#F0F0F0")
      button.layer.cornerRadius = 8
      button.contentEdgeInsets = UIEdgeInsets(top: 4, left: 8, bottom: 4, right: 8)
      button.isEnabled = !data.bool("disabled") && !descriptor.bool("disabled")
      button.addTarget(self, action: #selector(actionPressed(_:)), for: .touchUpInside)
      buttons.append(button); actions.addArrangedSubview(button)
    }
    let spacer = UIView(); spacer.setContentHuggingPriority(.defaultLow, for: .horizontal)
    // The last flexible spacer is separate from the fixed action buttons.
    actions.arrangedSubviews.filter { !($0 is UIButton) }.forEach { actions.removeArrangedSubview($0); $0.removeFromSuperview() }
    actions.addArrangedSubview(spacer)
  }
  @objc private func descriptionPressed() { onAction?(descriptionKey, descriptionLabel, "description", 0) }
  @objc private func actionPressed(_ button: UIButton) {
    guard button.tag < actionKeys.count else { return }
    onAction?(actionKeys[button.tag], button, "footerAction", button.tag)
  }
  func recycle() {
    visual.recycle(); amountViews.forEach { $0.recycle() }; actionKeys = []; descriptionKey = ""
  }
}

private func nativeListActivityTone(_ tone: String, theme: [String: Any]?) -> UIColor {
  switch tone {
  case "positive", "success": return nativeListColor(theme, "positive", "#218358")
  case "negative", "danger": return nativeListColor(theme, "negative", "#CE2C31")
  case "secondary": return nativeListColor(theme, "secondaryText", "#646464")
  case "warning": return nativeListColor(theme, "warning", "#AB6400")
  case "info": return nativeListColor(theme, "info", "#007BEF")
  default: return nativeListColor(theme, "primaryText", "#202020")
  }
}

private final class NativeListActivityAmountView: UIStackView {
  private let visual = NativeListLeadingVisual()
  private let labels = UIStackView()
  private let primary = NativeListTextLabel()
  private let secondary = NativeListTextLabel()
  override init(frame: CGRect) {
    super.init(frame: frame)
    axis = .horizontal; alignment = .center; spacing = 6
    labels.axis = .vertical; labels.alignment = .fill
    [primary, secondary].forEach(labels.addArrangedSubview)
    [visual, labels].forEach(addArrangedSubview)
    NSLayoutConstraint.activate([visual.widthAnchor.constraint(equalToConstant: 20), visual.heightAnchor.constraint(equalToConstant: 20)])
  }
  required init(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
  func bind(_ data: [String: Any], key: String, style: [String: Any], theme: [String: Any]?, table: Bool) {
    let leading = data.dictionary("leading")
    visual.isHidden = leading == nil
    if let leading { visual.bind(leading, style: ["width": 20, "height": 20], key: key, theme: theme, isUnread: false) } else { visual.recycle() }
    var primaryStyle = style.dictionary("primaryAmount") ?? [:]
    if primaryStyle["alignment"] == nil { primaryStyle["alignment"] = table ? "start" : "end" }
    let resolved = NativeListResolvedText(data.string("text"), style: primaryStyle, size: 16,
      weight: .medium, color: nativeListActivityTone(data.string("tone"), theme: theme), lineHeight: 24, lines: 1, tabular: true)
    resolved.bind(primary)
    nativeListActivitySegments(primary, data.dictionaries("textSegments"))
    var secondaryStyle = style.dictionary("secondaryAmount") ?? [:]
    if secondaryStyle["alignment"] == nil { secondaryStyle["alignment"] = table ? "start" : "end" }
    NativeListResolvedText(data.string("secondaryText"), style: secondaryStyle, size: 14,
      color: nativeListColor(theme, "secondaryText", "#646464"), lineHeight: 20, lines: 1).bind(secondary)
    nativeListActivitySegments(secondary, data.dictionaries("secondaryTextSegments"))
  }
  func recycle() { visual.recycle() }
}

private func nativeListActivitySegments(_ label: NativeListTextLabel, _ segments: [[String: Any]]) {
  guard !segments.isEmpty else { return }
  let text = segments.map { $0.string("text") }.joined()
  let attributes = (label.attributedText?.length ?? 0) > 0
    ? label.attributedText!.attributes(at: 0, effectiveRange: nil)
    : [.font: label.font as Any, .foregroundColor: label.textColor as Any]
  let value = NSMutableAttributedString(string: text, attributes: attributes)
  var offset = 0
  for segment in segments {
    let length = (segment.string("text") as NSString).length
    if segment.string("style") == "subscript" {
      value.addAttributes([.font: label.font.withSize(ceil(label.font.pointSize * 0.6))], range: NSRange(location: offset, length: length))
    }
    offset += length
  }
  label.attributedText = value
}
