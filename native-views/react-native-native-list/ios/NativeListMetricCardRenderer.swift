import UIKit

final class NativeListMetricCardCell: NativeListRendererCell {
  override class func measure(
    _ item: NativeListItem, width: CGFloat, theme: [String: Any]?, layout: String
  ) -> CGFloat? {
    return item.data.string("variant") == "activity"
      ? 160 + 1 / UIScreen.main.scale : item.data.string("variant") == "performance" ? 178 : 132
  }

  private let visual = NativeListLeadingVisual()
  private let mainStack = UIStackView()
  private let titleLine = UIStackView()
  private let titleLabel = NativeListTextLabel()
  private let subtitleLabel = NativeListTextLabel()
  private let statusLabel = NativeListTextLabel()
  private let detailLabel = NativeListTextLabel()
  private let badgeLabel = NativeListTextLabel()
  private let metricCompositeStack = UIStackView()
  private var dimensions: [NSLayoutConstraint] = []
  private var metricVisuals: [String: NativeListMetricVisual] = [:]
  private var usedVisuals = Set<String>()
  private var rowKey = ""
  override var assetFields: [String] { ["visual", "metrics"] }
  override var defaultCornerRadius: CGFloat { 12 }
  override func unselectedBackground(
    _ item: NativeListItem, theme: [String: Any]?, layout: String, itemIndex: Int?
  ) -> UIColor { nativeListColor(theme, "subduedBackground", "#F9F9F9") }
  override init(frame: CGRect) {
    super.init(frame: frame)
    mainStack.axis = .vertical
    mainStack.alignment = .fill
    titleLine.axis = .horizontal
    titleLine.alignment = .center
    titleLine.spacing = 8
    titleLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
    badgeLabel.setContentHuggingPriority(.required, for: .horizontal)
    titleLine.addArrangedSubview(titleLabel)
    titleLine.addArrangedSubview(badgeLabel)
    [titleLine, subtitleLabel, statusLabel, detailLabel].forEach(mainStack.addArrangedSubview)
    metricCompositeStack.axis = .vertical
    metricCompositeStack.alignment = .fill
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
  override func bindContent(
    _ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    rowKey = item.key
    usedVisuals = []
    contentView.clipsToBounds = true
    root.arrangedSubviews.forEach {
      root.removeArrangedSubview($0)
      $0.removeFromSuperview()
    }
    mainStack.removeArrangedSubview(metricCompositeStack)
    metricCompositeStack.removeFromSuperview()
    metricCompositeStack.arrangedSubviews.forEach {
      metricCompositeStack.removeArrangedSubview($0)
      $0.removeFromSuperview()
    }
    let data = item.data
    let style = data.dictionary("style") ?? [:]
    let image = style.dictionary("image") ?? [:]
    root.axis = .vertical
    root.alignment = .leading
    root.spacing = CGFloat(style.double("lineGap", default: 4))
    mainStack.spacing = CGFloat(style.double("lineGap", default: 2))
    let hp = CGFloat(style.double("horizontalPadding", default: 14))
    let vp = CGFloat(style.double("verticalPadding", default: 14))
    contentInsets = UIEdgeInsets(top: vp, left: hp, bottom: vp, right: hp)
    for label in [titleLabel, subtitleLabel, statusLabel, detailLabel, badgeLabel] {
      label.attributedText = nil
      label.text = nil
      label.isHidden = true
      label.numberOfLines = 1
      label.lineBreakMode = .byTruncatingTail
      label.textAlignment = .natural
      label.rowVerticalAlignment = nil
      label.rowOffsetY = 0
      label.transform = .identity
    }
    titleLabel.font = nativeListFont(
      ofSize: data.string("size") == "large" ? 24 : 18, weight: .semibold)
    subtitleLabel.font = nativeListFont(ofSize: 11)
    statusLabel.font = nativeListFont(ofSize: 12)
    detailLabel.font = nativeListFont(ofSize: 12)
    badgeLabel.font = nativeListFont(ofSize: 12, weight: .medium)
    titleLabel.textColor = nativeListColor(theme, "primaryText", "#202020")
    subtitleLabel.textColor = nativeListColor(theme, "disabledText", "#8D8D8D")
    [statusLabel, detailLabel].forEach {
      $0.textColor = nativeListColor(theme, "secondaryText", "#646464")
    }
    badgeLabel.textColor = nativeListColor(theme, "accent", "#108303")
    let variant = data.string("variant", default: "standard")
    NSLayoutConstraint.deactivate(dimensions)
    dimensions = []
    if variant == "activity" || variant == "performance" {
      visual.recycle()
      bindCompositeMetricCard(item, theme: theme, variant: variant)
      if style["lineGap"] != nil {
        mainStack.spacing = CGFloat(style.double("lineGap"))
        metricCompositeStack.spacing = CGFloat(style.double("lineGap"))
        if variant == "performance",
          let summary = metricCompositeStack.arrangedSubviews.first as? UIStackView
        {
          summary.spacing = CGFloat(style.double("lineGap"))
        }
      }
      if let text = style.dictionary("title") {
        NativeListTextStyles.applyStyledText(titleLabel, text)
      }
    } else {
      if let descriptor = data.dictionary("visual") {
        visual.bind(descriptor, style: image, key: item.key, theme: theme, isUnread: false)
        root.addArrangedSubview(visual)
        dimensions = [
          visual.widthAnchor.constraint(
            equalToConstant: CGFloat(image.double("width", default: 32))),
          visual.heightAnchor.constraint(
            equalToConstant: CGFloat(image.double("height", default: 32))),
        ]
        NSLayoutConstraint.activate(dimensions)
        root.setCustomSpacing(
          CGFloat(style.double("leadingGap", default: Double(root.spacing))), after: visual)
      } else {
        visual.recycle()
      }
      show(titleLabel, data.string("value"), lines: 1)
      show(subtitleLabel, data.string("title"), lines: 1)
      show(statusLabel, data.string("trend"), lines: 1)
      show(detailLabel, data.string("subtitle"), lines: 1)
      show(badgeLabel, data.dictionary("badge")?.string("text") ?? "", lines: 1)
      let tone = data.string("trendTone")
      if tone == "positive" || tone == "negative" {
        statusLabel.textColor = nativeListColor(
          theme, tone, tone == "positive" ? "#218358" : "#CE2C31")
      }
      for (slot, label) in [
        ("title", subtitleLabel), ("value", titleLabel), ("subtitle", detailLabel),
        ("trend", statusLabel),
      ] {
        if let text = style.dictionary(slot) { NativeListTextStyles.applyStyledText(label, text) }
      }
      root.addArrangedSubview(mainStack)
    }
    for key in Array(metricVisuals.keys) where !usedVisuals.contains(key) {
      metricVisuals.removeValue(forKey: key)?.recycle()
    }
  }
  override func recycleContent() {
    visual.recycle()
    metricVisuals.values.forEach { $0.recycle() }
    metricVisuals = [:]
    usedVisuals = []
  }
  private func show(_ view: UILabel, _ text: String, lines: Int) {
    view.text = text
    view.numberOfLines = lines
    view.isHidden = text.isEmpty
  }
  private func setLineHeight(
    _ view: UILabel, text: String, lineHeight: CGFloat, letterSpacing: CGFloat? = nil
  ) {
    let paragraph = NSMutableParagraphStyle()
    paragraph.minimumLineHeight = lineHeight
    paragraph.maximumLineHeight = lineHeight
    paragraph.alignment = view.textAlignment
    var attrs: [NSAttributedString.Key: Any] = [
      .font: view.font as Any, .foregroundColor: view.textColor as Any, .paragraphStyle: paragraph,
    ]
    if let letterSpacing { attrs[.kern] = letterSpacing }
    view.attributedText = NSAttributedString(string: text, attributes: attrs)
  }
  private func makeMetricVisual(_ visual: [String: Any], key: String, slot: Int, role: String)
    -> UIView
  {
    let identity = "\(rowKey):\(role):\(slot):\(key)"
    usedVisuals.insert(identity)
    let view = metricVisuals[identity] ?? NativeListMetricVisual()
    metricVisuals[identity] = view
    view.bind(visual, key: identity)
    return view
  }
  private func bindCompositeMetricCard(
    _ item: NativeListItem,
    theme: [String: Any]?,
    variant: String
  ) {
    root.alignment = .fill
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
    root.addArrangedSubview(mainStack)

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
      column.alignment =
        shaded
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
          makeMetricVisual(visual, key: metric.string("key"), slot: index, role: style)
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
}

private final class NativeListMetricVisual: UIView {
  private let icon = UIImageView()
  private let image = NativeListImageSlot()
  init() {
    super.init(frame: .zero)
    translatesAutoresizingMaskIntoConstraints = false
    icon.contentMode = .scaleAspectFit
    addSubview(icon)
    addSubview(image.view)
    NSLayoutConstraint.activate([
      widthAnchor.constraint(equalToConstant: 16), heightAnchor.constraint(equalToConstant: 16),
    ])
    clipsToBounds = true
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
  func bind(_ visual: [String: Any], key: String) {
    backgroundColor = UIColor(
      nativeListHex: visual.string("backgroundColor", default: "#0000000F"), fallback: .lightGray)
    layer.cornerRadius = visual.string("shape") == "square" ? 0 : 8
    let kind = visual.string("kind")
    icon.isHidden = kind != "icon"
    image.view.isHidden = true
    if kind == "icon" {
      image.recycle()
      icon.image = nativeListIcon(named: visual.string("name"))
      icon.tintColor = UIColor(
        nativeListHex: visual.string("tintColor", default: "#00000072"), fallback: .darkGray)
    } else if let source = kind == "stackedImages"
      ? visual.dictionaries("images").first : visual.dictionary("image")
    {
      image.view.isHidden = false
      image.bind(
        source, key: key,
        variant: kind == "token"
          ? "token"
          : kind == "network"
            ? "network" : ["wallet", "account"].contains(kind) ? "avatar" : "generic", fit: nil,
        placeholder: "#00000000")
    } else {
      image.recycle()
    }
  }
  override func layoutSubviews() {
    super.layoutSubviews()
    icon.frame = bounds
    image.view.frame = bounds
  }
  func recycle() { image.recycle() }
}
