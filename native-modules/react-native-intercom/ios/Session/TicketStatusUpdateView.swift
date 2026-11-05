import UIKit

@objc(_TtC18IntercomSDKPrivate22TicketStatusUpdateView)
class TicketStatusUpdateView: UIView, ConversationCellContentView {
    let content: ICMConversationPart
    private let themeProvider: DefaultThemeProvider
    private let topTextStackView: UIStackView
    private let containerStackView: UIStackView
    private lazy var ticketIconView: UIImageView = {
        let view = UIImageView(image: ICMAssets.ticketIcon())
        view.contentMode = .center
        view.tintColor = themeProvider.theme.iconMuted
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        let width = view.widthAnchor.constraint(equalToConstant: 16)
        width.priority = .required
        width.isActive = true
        view.translatesAutoresizingMaskIntoConstraints = false
        let height = view.heightAnchor.constraint(equalToConstant: 16)
        height.priority = .required
        height.isActive = true
        return view
    }()
    private lazy var dotLabel: UILabel = {
        let label = makeLabel()
        label.text = "•"
        return label
    }()
    private lazy var titleLabel = makeLabel()
    private lazy var timestampLabel: UILabel = {
        let label = makeLabel()
        label.setContentCompressionResistancePriority(UILayoutPriority(rawValue: 750), for: .horizontal)
        return label
    }()
    private lazy var statusLabel = makeLabel()

    init(for content: ICMConversationPart, themeProvider: DefaultThemeProvider) {
        let top = UIStackView()
        top.axis = .horizontal
        top.alignment = .center
        top.distribution = .fill
        top.spacing = 4
        top.translatesAutoresizingMaskIntoConstraints = false
        topTextStackView = top
        let container = UIStackView()
        container.axis = .vertical
        container.alignment = .center
        container.distribution = .fill
        container.spacing = 4
        container.translatesAutoresizingMaskIntoConstraints = false
        containerStackView = container
        self.content = content
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(containerStackView)
        configure(with: content)
        configureConstraints()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private func makeLabel() -> UILabel {
        let label = UILabel()
        label.textColor = themeProvider.theme.textMuted
        label.font = .preferredFont(forTextStyle: .footnote)
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textAlignment = .center
        label.lineBreakMode = .byTruncatingTail
        label.numberOfLines = 1
        return label
    }
    private func configure(with part: ICMConversationPart) {
        containerStackView.removeAllArrangedSubviews()
        topTextStackView.removeAllArrangedSubviews()
        let event = part.eventData!
        guard let prefix = event.customStatePrefix, !prefix.isEmpty else {
            fallback(part)
            return
        }
        [ticketIconView, titleLabel, dotLabel, timestampLabel].forEach { topTextStackView.addArrangedSubview($0) }
        topTextStackView.setCustomSpacing(6, after: ticketIconView)
        [topTextStackView, statusLabel].forEach { containerStackView.addArrangedSubview($0) }
        let title = titleLabel
        title.text = part.eventData!.customStatePrefix
        let timestamp = timestampLabel
        timestamp.text = formattedDate(for: part)
        let freshEvent = part.eventData!
        if let state = freshEvent.customStateLabel { statusLabel.attributedText = bolded(state) }
    }
    private func fallback(_ part: ICMConversationPart) {
        [ticketIconView, titleLabel].forEach { topTextStackView.addArrangedSubview($0) }
        containerStackView.addArrangedSubview(topTextStackView)
        let label = titleLabel
        label.attributedText = fallbackString(for: part)
    }
    private func configureConstraints() {
        let view = containerStackView
        let centerParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        let center = view.centerXAnchor.constraint(equalTo: centerParent.centerXAnchor)
        center.priority = .required
        center.isActive = true
        let topParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        let top = view.topAnchor.constraint(equalTo: topParent.topAnchor)
        top.priority = .required
        top.isActive = true
        let bottomParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        let bottom = view.bottomAnchor.constraint(equalTo: bottomParent.bottomAnchor, constant: -CGFloat.zero)
        bottom.priority = .required
        bottom.isActive = true
        let leadingParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        let leadingRTL = view.effectiveUserInterfaceLayoutDirection == .rightToLeft
        view.translatesAutoresizingMaskIntoConstraints = false
        let leading = view.leadingAnchor.constraint(greaterThanOrEqualTo: leadingParent.leadingAnchor, constant: leadingRTL ? -CGFloat.zero : .zero)
        leading.priority = .required
        leading.isActive = true
        let trailingParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        let trailingRTL = view.effectiveUserInterfaceLayoutDirection == .rightToLeft
        view.translatesAutoresizingMaskIntoConstraints = false
        let trailing = view.trailingAnchor.constraint(lessThanOrEqualTo: trailingParent.trailingAnchor, constant: trailingRTL ? .zero : -CGFloat.zero)
        trailing.priority = .required
        trailing.isActive = true
    }
    private func formattedDate(for part: ICMConversationPart) -> String? {
        guard let date = part.createdAt else { return nil }
        return TimestampCalculator.shared.timestampText(for: date, format: .short, recentTimeMessage: .justNow)
    }
    private func bolded(_ string: String) -> NSAttributedString {
        let descriptor = UIFontDescriptor.preferredFontDescriptor(withTextStyle: .footnote)
        let font = UIFont.systemFont(ofSize: descriptor.pointSize, weight: .bold)
        return NSAttributedString(string: string, attributes: [.font: font])
    }
    private func applyBoldToString(currentStatus: String, text: NSMutableAttributedString) -> NSMutableAttributedString {
        let range = text.mutableString.range(of: currentStatus, options: .caseInsensitive)
        let descriptor = UIFontDescriptor.preferredFontDescriptor(withTextStyle: .footnote)
        let font = UIFont.systemFont(ofSize: descriptor.pointSize, weight: .bold)
        text.addAttribute(.font, value: font, range: range)
        return text
    }
    private func fallbackString(for part: ICMConversationPart?) -> NSMutableAttributedString? {
        guard let part else { return nil }
        let status = part.eventData!.status
        guard let status else { return nil }
        guard let timestamp = formattedDate(for: part) else { return nil }
        guard let eventText = part.eventData!.eventAsPlainText else { return nil }
        let currentStatus = eventText.lowercased()
        let key: String
        let params: [String: String]
        if TicketStatus.Stage(rawValue: status) == .submitted {
            key = "tickets_status_event_submitted"
            params = ["submitted_status": currentStatus, "timestamp": timestamp]
        } else {
            key = "tickets_status_change_event"
            params = ["status": currentStatus, "timestamp": timestamp]
        }
        var string = TranslationManager.messenger.translation(forKey: key)
        for (key, value) in params { string = string.replacingOccurrences(of: "{" + key + "}", with: value) }
        return applyBoldToString(currentStatus: currentStatus, text: NSMutableAttributedString(string: string))
    }
}
