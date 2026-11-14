import UIKit

@objc(_TtC18IntercomSDKPrivate14PartStatusView)
class PartStatusView: UIStackView {
    private var labelColor: UIColor
    private let content: ICMConversationPart
    private let appendSeenState: Bool
    private let attributedSingleImage: Bool
    private var timer: Timer?
    private lazy var attributionLabel: UILabel = makeLabel(attribution: true)
    private lazy var timestampLabel: UILabel = makeLabel(attribution: false)

    init(content: ICMConversationPart, appendSeenState: Bool, leadingInset: CGFloat, attributedSingleImage: Bool, themeProvider: ThemeProvider) {
        labelColor = themeProvider.theme.textDefault
        self.content = content
        self.appendSeenState = appendSeenState
        self.attributedSingleImage = attributedSingleImage
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        axis = .horizontal
        isLayoutMarginsRelativeArrangement = true
        directionalLayoutMargins = NSDirectionalEdgeInsets(top: 0, leading: leadingInset, bottom: 0, trailing: 0)
        configure()
    }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    deinit { timer?.invalidate() }

    private func makeLabel(attribution: Bool) -> UILabel {
        let label = UILabel()
        label.font = .preferredFont(forTextStyle: .footnote)
        label.textColor = labelColor
        label.translatesAutoresizingMaskIntoConstraints = false
        label.adjustsFontForContentSizeCategory = true
        label.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        let rtl = effectiveUserInterfaceLayoutDirection == .rightToLeft
        let personal = content.isPersonalPart()
        label.textAlignment = personal != rtl ? (attribution ? .left : .right) : (attribution ? .right : .left)
        return label
    }
    private func configure() {
        let timestamp = timestampLabel
        timestamp.text = timestampText
        setAttributionLabel()
        if attributedSingleImage {
            distribution = .equalSpacing
            spacing = 8
            addArrangedSubview(timestampLabel)
            addArrangedSubview(attributionLabel)
            timestampLabel.setContentCompressionResistancePriority(UILayoutPriority(751), for: .horizontal)
            attributionLabel.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
        } else if content.isPersonalPart() {
            addArrangedSubview(attributionLabel)
            addArrangedSubview(timestampLabel)
        } else {
            addArrangedSubview(timestampLabel)
            addArrangedSubview(attributionLabel)
        }
        let attribution = attributionLabel
        attribution.topAnchor.constraint(equalTo: attribution.superview!.topAnchor).isActive = true
        attribution.bottomAnchor.constraint(equalTo: attribution.superview!.bottomAnchor).isActive = true
        let time = timestampLabel
        time.topAnchor.constraint(equalTo: time.superview!.topAnchor).isActive = true
        time.bottomAnchor.constraint(equalTo: time.superview!.bottomAnchor).isActive = true
        configureTimer()
    }
    private var timestampText: String? {
        if content.state == 2 { return nil }
        if appendSeenState {
            let date = content.createdAt ?? Date()
            let calculator = TimestampCalculator.shared
            let text = calculator.timestampText(for: date, format: .short, recentTimeMessage: .justNow) ?? ""
            return calculator.appendSeenState(toTimestampText: text, for: content)
        }
        let calculator = TimestampCalculator.shared
        let date = content.createdAt
        let text = calculator.timestampText(for: date, format: .short, recentTimeMessage: .justNow) ?? ""
        if content.isPersonalPart() { return text }
        return ICMConversationPart.joinSenderLabel(content.senderLabel ?? "", timestamp: text)
    }
    private func setAttributionLabel() {
        guard let attribution = content.attribution, !attribution.isEmpty else { return }
        let label = attributionLabel
        let translation = TranslationManager.messenger
        label.text = translation.translation(forKey: "gif_attribution", params: ["providername": content.attribution!])
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        clearAttributionIfNoSpaceAvailable()
    }
    private func clearAttributionIfNoSpaceAvailable() {
        if attributedSingleImage { return }
        guard let timestamp = timestampLabel.text, let timestampFont = timestampLabel.font,
              let attribution = attributionLabel.text, let attributionFont = attributionLabel.font else { return }
        let timestampSize = (timestamp as NSString).size(withAttributes: [.font: timestampFont])
        let attributionSize = (attribution as NSString).size(withAttributes: [.font: attributionFont])
        let width = timestampSize.width + attributionSize.width
        if frame.width < width { attributionLabel.text = nil }
    }
    private func configureTimer() {
        guard let date = content.createdAt else { return }
        let interval = TimestampCalculator.shared.secondsUntilTimestampExpires(for: date)
        if interval > 0 {
            let replacement = Timer.scheduledTimer(withTimeInterval: interval, repeats: true) { [weak self] _ in
                self?.setTimestampLabel()
            }
            timer?.invalidate()
            timer = replacement
        }
    }
    @objc private func setTimestampLabel() { timestampLabel.text = timestampText }
}

extension ICMConversationPart {
    static func joinSenderLabel(_ sender: String, timestamp: String) -> String {
        if sender.isEmpty { return timestamp }
        if timestamp.isEmpty { return sender }
        return sender + " • " + timestamp
    }
    func reorderedBlocksForHuman() -> [ITBBlock] {
        let source: [ITBBlock] = blocks!
        var images: [ITBBlock] = [], media: [ITBBlock] = [], remainder: [ITBBlock] = []
        for block in source {
            switch block.type() {
            case 5: images.append(block)
            case 14: media.append(block)
            default: remainder.append(block)
            }
        }
        return images + media + remainder
    }
    func getBackgroundColor(theme: ThemeProvider) -> UIColor {
        if isPersonalPart() {
            let alpha: CGFloat = state == 2 ? 0.5 : 1
            return theme.theme.primaryBackground.withAlphaComponent(alpha)
        }
        return theme.theme.neutralContainer
    }
}
