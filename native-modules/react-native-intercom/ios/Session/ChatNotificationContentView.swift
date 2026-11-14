import UIKit

@objc(_TtC18IntercomSDKPrivate27ChatNotificationContentView)
class ChatNotificationContentView: UIView {
    private enum LayoutStyle { case none, snippet, full, ticketMessage, ticketStatusUpdate }
    private enum ParticipantPosition { case top, bottom }
    private struct ViewState {
        let primaryAxis: NSLayoutConstraint.Axis
        let secondaryAxis: NSLayoutConstraint.Axis
        let ticket: Ticket?
        let message: String?
        let textColor: UIColor
        let participant: String?
        let position: ParticipantPosition
        let font: UIFont
        let blocks: [ITBBlock]?
    }
    @objc var primaryStackView: UIStackView = {
        let stack = UIStackView()
        stack.spacing = 0
        stack.distribution = .fill
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    @objc var secondaryStackView: UIStackView = {
        let stack = UIStackView()
        stack.spacing = 8
        stack.distribution = .fill
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    @objc var avatarView: ICMAvatarView = {
        let avatar = ICMAvatarView(noTeammatePresenceWithAvatarDiameter: 32)!
        avatar.translatesAutoresizingMaskIntoConstraints = false
        avatar.widthAnchor.constraint(equalToConstant: 32).isActive = true
        avatar.heightAnchor.constraint(equalTo: avatar.widthAnchor).isActive = true
        return avatar
    }()
    @objc lazy var messageLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines = 2
        label.font = .preferredFont(forTextStyle: .subheadline)
        ICMUtils.disableDynamicFont(for: [label])
        label.accessibilityIdentifier = "message label"
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    @objc lazy var participantNameAndTimeLabel: UILabel = {
        let label = UILabel()
        label.lineBreakMode = .byTruncatingTail
        label.textColor = themeProvider.theme.textMuted
        label.accessibilityIdentifier = "participantNameAndTimeLabel"
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    @objc lazy var ticketInfoView = TicketInfoView()
    @objc var blocksStackView: ICMContentStackView?
    private var layoutStyle: LayoutStyle = .none
    private let themeProvider: DefaultThemeProvider
    private lazy var isLTR = UIView.userInterfaceLayoutDirection(for: semanticContentAttribute) == .leftToRight
    private let preventFullLayoutStyle: Bool
    private let blocksWidth: CGFloat
    private let config: ICMConfigProtocol
    private let timestampCalculator: TimestampCalculator
    @objc init(preventFullLayoutStyle: Bool, blocksWidth: CGFloat, config: ICMConfigProtocol, themeProvider: DefaultThemeProvider, timestampCalculator: TimestampCalculator) {
        self.preventFullLayoutStyle = preventFullLayoutStyle
        self.blocksWidth = blocksWidth
        self.config = config
        self.themeProvider = themeProvider
        self.timestampCalculator = timestampCalculator
        super.init(frame: .zero)
        backgroundColor = themeProvider.theme.base
        isAccessibilityElement = true
        addSubviews()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func addSubviews() {
        addSubview(primaryStackView)
        NSLayoutConstraint.activate([
            primaryStackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 16),
            primaryStackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -16),
            primaryStackView.topAnchor.constraint(equalTo: topAnchor, constant: 12),
            primaryStackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -12)
        ])
        buildView(state: viewState(for: nil, ticket: nil), part: nil)
    }
    @objc(configureFor:ticket:)
    func configure(for part: ICMConversationPart, ticket: Ticket?) {
        if ticket != nil { layoutStyle = part.eventData?.status != nil ? .ticketStatusUpdate : .ticketMessage }
        else { layoutStyle = part.deliveryOption == 1 && !preventFullLayoutStyle ? .full : .snippet }
        let state = viewState(for: part, ticket: ticket)
        buildView(state: state, part: part)
        updateAccessibilityLabel()
    }
    private func viewState(for part: ICMConversationPart?, ticket: Ticket?) -> ViewState {
        let participant = participantNameAndTimeLabelText(for: part)
        switch layoutStyle {
        case .none:
            return ViewState(primaryAxis: .horizontal, secondaryAxis: .vertical, ticket: nil, message: nil, textColor: DefaultThemeProvider.shared.theme.textDefault, participant: nil, position: .top, font: .preferredFont(forTextStyle: .subheadline), blocks: nil)
        case .snippet:
            return ViewState(primaryAxis: .horizontal, secondaryAxis: .vertical, ticket: nil, message: part?.summary, textColor: DefaultThemeProvider.shared.theme.textDefault, participant: participant, position: .bottom, font: .preferredFont(forTextStyle: .subheadline), blocks: nil)
        case .full:
            let color = DefaultThemeProvider.shared.theme.textDefault
            let font = UIFont.systemFont(ofSize: UIFont.preferredFont(forTextStyle: .subheadline).fontDescriptor.pointSize, weight: .semibold)
            return ViewState(primaryAxis: .vertical, secondaryAxis: .horizontal, ticket: nil, message: nil, textColor: color, participant: participant, position: .top, font: font, blocks: part?.blocks)
        case .ticketMessage:
            return ViewState(primaryAxis: .horizontal, secondaryAxis: .vertical, ticket: ticket, message: part?.summary, textColor: DefaultThemeProvider.shared.theme.textDefault, participant: nil, position: .top, font: .preferredFont(forTextStyle: .subheadline), blocks: nil)
        case .ticketStatusUpdate:
            return ViewState(primaryAxis: .horizontal, secondaryAxis: .vertical, ticket: ticket, message: getTicketStatusUpdateString(part), textColor: themeProvider.theme.textMuted, participant: nil, position: .top, font: .preferredFont(forTextStyle: .subheadline), blocks: nil)
        }
    }
    private func buildView(state: ViewState, part: ICMConversationPart?) {
        primaryStackView.removeAllArrangedSubviews()
        secondaryStackView.removeAllArrangedSubviews()
        blocksStackView = nil
        configureStackViews(state)
        configureContent(state, part: part)
    }
    private func configureStackViews(_ state: ViewState) {
        primaryStackView.axis = state.primaryAxis
        _ = primaryStackView.axis
        primaryStackView.alignment = .leading
        secondaryStackView.axis = state.secondaryAxis
        secondaryStackView.alignment = secondaryStackView.axis == .vertical ? .leading : .center
        if primaryStackView.axis == .horizontal { primaryStackView.addArrangedSubview(avatarView) }
        else { secondaryStackView.addArrangedSubview(avatarView) }
        primaryStackView.addArrangedSubview(secondaryStackView)
        if let first = primaryStackView.arrangedSubviews.first { primaryStackView.setCustomSpacing(8, after: first) }
    }
    private func configureContent(_ state: ViewState, part: ICMConversationPart?) {
        loadAvatar(for: part)
        if let ticket = state.ticket { secondaryStackView.addArrangedSubview(ticketInfoView); ticketInfoView.configure(with: ticket) }
        if let message = state.message {
            secondaryStackView.addArrangedSubview(messageLabel)
            messageLabel.text = message
            messageLabel.textColor = state.textColor
        }
        configureParticipantLabel(state)
        if let blocks = state.blocks, !blocks.isEmpty {
            let stack = ICMContentStackView(blocks: blocks, context: 3, withinWidth: blocksWidth)!
            stack.translatesAutoresizingMaskIntoConstraints = false
            primaryStackView.addArrangedSubview(stack)
            stack.renderContent()
            blocksStackView = stack
        }
    }
    private func configureParticipantLabel(_ state: ViewState) {
        guard let text = state.participant else { return }
        if primaryStackView.axis == .horizontal && state.position == .top { secondaryStackView.insertArrangedSubview(participantNameAndTimeLabel, at: 0) }
        else { secondaryStackView.addArrangedSubview(participantNameAndTimeLabel) }
        participantNameAndTimeLabel.text = text
        participantNameAndTimeLabel.font = state.font
        ICMUtils.disableDynamicFont(for: [participantNameAndTimeLabel])
    }
    @objc(loadAvatarFor:)
    func loadAvatar(for part: ICMConversationPart?) {
        guard let part else { return }
        if let avatars = part.partMetadata?.avatars, !avatars.isEmpty { avatarView.load(avatars[0], useActionColorAsBackgroundColor: true) }
        else if let participant = part.participant { avatarView.loadAvatar(for: participant) }
    }
    @objc(participantNameAndTimeLabelTextFor:)
    func participantNameAndTimeLabelText(for part: ICMConversationPart?) -> String {
        var name = part?.partMetadata?.title ?? part?.participant?.firstName ?? part?.participant?.name ?? ""
        if name.isEmpty { return "" }
        if let suffix = part?.partMetadata?.suffix, !suffix.isEmpty, part?.partMetadata?.title != nil {
            name = isLTR ? name + " • " + suffix : suffix + " • " + name
        }
        if layoutStyle == .snippet,
           let text = timestampCalculator.timestampText(for: part?.createdAt, format: .short, recentTimeMessage: .justNow) {
            name = isLTR ? name + " • " + text : text + " • " + name
        }
        return name
    }
    private func getTicketStatusUpdateString(_ part: ICMConversationPart?) -> String? {
        guard let part else { return nil }
        if let prefix = part.eventData!.customStatePrefix, !prefix.isEmpty { return prefix }
        guard let status = part.eventData!.status else { return part.summary }
        guard let text = part.eventData!.eventAsPlainText else { return part.summary }
        let lowered = text.lowercased()
        let stage = TicketStatus.Stage(rawValue: status)
        let key = stage == .submitted ? "tickets_status_event_submitted" : "tickets_status_change_event"
        let parameter = stage == .submitted ? "submitted_status" : "status"
        let value = TranslationManager.messenger.translation(forKey: key).replacingOccurrences(of: "{" + parameter + "}", with: lowered)
        return value.components(separatedBy: " • ").dropLast().joined(separator: "")
    }
    @objc(setContentAlphaTo:)
    func setContentAlpha(to alpha: CGFloat) {
        messageLabel.alpha = alpha
        ticketInfoView.alpha = alpha
        blocksStackView?.alpha = alpha
    }
    private func updateAccessibilityLabel() {
        var label = (avatarView.accessibilityLabel ?? "") + ";"
        let ticket = ticketInfoView
        if ticket.superview != nil, let text = ticket.accessibilityLabel, !text.isEmpty { label += text + ";" }
        let message = messageLabel
        if message.superview != nil, let text = message.text, !text.isEmpty { label += (message.text ?? "") + ";" }
        let participant = participantNameAndTimeLabel
        if participant.superview != nil, let text = participant.text, !text.isEmpty {
            for value in text.components(separatedBy: " • ") { label += value + ";" }
        }
        accessibilityLabel = label
    }
}
