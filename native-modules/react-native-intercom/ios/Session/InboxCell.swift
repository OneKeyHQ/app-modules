import UIKit

struct TypingIndicatorState {
    let details: TypingAdminDetails?
    var timer: Timer?
}

@objc(_TtC18IntercomSDKPrivate9InboxCell)
class InboxCell: UITableViewCell {
    private let themeProvider: ThemeProvider
    private lazy var combinedContextLabel: UILabel = {
        let label = UILabel()
        label.adjustsFontForContentSizeCategory = true
        label.text = combinedContextText
        label.textColor = themeProvider.theme.textMuted
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var nameLabel: UILabel = {
        let label = UILabel()
        label.adjustsFontForContentSizeCategory = true
        label.text = name
        label.textColor = themeProvider.theme.textMuted
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var timeLabel: UILabel = {
        let label = UILabel()
        label.adjustsFontForContentSizeCategory = true
        label.text = sentTime
        label.textColor = themeProvider.theme.textMuted
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var mainStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .center
        stack.spacing = ICMUtils.scale(toAccessibilitySizing: 12)
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.adjustsFontForContentSizeCategory = true
        label.text = title
        label.textColor = themeProvider.theme.textDefault
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.numberOfLines = 1
        label.lineBreakMode = .byTruncatingTail
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var messageContentLabel: UILabel = {
        let label = UILabel()
        label.adjustsFontForContentSizeCategory = true
        label.text = messageContent
        label.textColor = themeProvider.theme.textMuted
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.numberOfLines = ICMUtils.contentSizeCategoryIsAccessibilityCategory() ? 2 : 1
        label.lineBreakMode = .byTruncatingTail
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var titleTimestampStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .center
        stack.spacing = 8
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var titleTimestampLabel: UILabel = {
        let label = UILabel()
        label.adjustsFontForContentSizeCategory = true
        label.textColor = themeProvider.theme.textMuted
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.numberOfLines = 1
        label.lineBreakMode = .byClipping
        label.translatesAutoresizingMaskIntoConstraints = false
        label.setContentCompressionResistancePriority(.required, for: .horizontal)
        label.setContentHuggingPriority(.required, for: .horizontal)
        return label
    }()
    private lazy var messageContentStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .center
        stack.distribution = .equalSpacing
        stack.spacing = 8
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var unreadDotView: UIView = {
        let dot = UIView()
        dot.backgroundColor = themeProvider.theme.statusNotificationFill
        dot.layer.cornerRadius = 4
        dot.layer.masksToBounds = true
        dot.translatesAutoresizingMaskIntoConstraints = false
        dot.heightAnchor.constraint(equalToConstant: 8).isActive = true
        dot.widthAnchor.constraint(equalToConstant: 8).isActive = true
        return dot
    }()
    private lazy var dividerLine: UIView = {
        let divider = UIView()
        divider.backgroundColor = themeProvider.theme.neutralBorder
        divider.accessibilityIdentifier = "intercom inbox cell divider"
        divider.translatesAutoresizingMaskIntoConstraints = false
        divider.heightAnchor.constraint(equalToConstant: 1).isActive = true
        return divider
    }()
    private lazy var multipleAvatarView: ICMMultipleAvatarView = {
        let avatars = ICMMultipleAvatarView(width: 32)!
        avatars.translatesAutoresizingMaskIntoConstraints = false
        avatars.widthAnchor.constraint(equalToConstant: 32).isActive = true
        avatars.heightAnchor.constraint(equalToConstant: 32).isActive = true
        return avatars
    }()
    private lazy var messageDetailsStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .leading
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private let timestampCalculator = TimestampCalculator.shared
    var action: (() -> Void)?
    var dotIndicatorView: DotIndicatorView?
    var typingIndicatorState: TypingIndicatorState?
    private var viewItem: InboxCellViewItem?
    private var sentTime = "" {
        didSet {
            combinedContextLabel.text = combinedContextText
            timeLabel.text = sentTime
            updateTitleTimestampStackView()
        }
    }
    private var name = "" {
        didSet { combinedContextLabel.text = combinedContextText; nameLabel.text = name }
    }
    private var title = "" {
        didSet { titleLabel.text = title; titleLabel.isHidden = title.isEmpty }
    }
    private var messageContent = ""
    private var combinedContextText: String { sentTime.isEmpty ? name : name + " • " + sentTime }

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        themeProvider = DefaultThemeProvider.shared
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupMainStackView()
        if ICMUtils.contentSizeCategoryIsAccessibilityCategory() { configureMainStackViewForAccessibilitySizes() }
        else { configureMainStackViewForRegularSizes() }
        setupTypingIndicator()
        backgroundColor = .clear
        isAccessibilityElement = true
    }
    required init?(coder: NSCoder) {
        themeProvider = DefaultThemeProvider.shared
        fatalError("init(coder:) has not been implemented")
    }
    private func setupMainStackView() {
        let stack = mainStackView
        addSubview(stack)
        NSLayoutConstraint.activate([
            stack.leadingAnchor.constraint(equalTo: layoutMarginsGuide.leadingAnchor),
            stack.topAnchor.constraint(equalTo: topAnchor, constant: 16),
            stack.trailingAnchor.constraint(equalTo: layoutMarginsGuide.trailingAnchor),
            stack.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -16)
        ])
    }
    private func setupTypingIndicator() {
        dotIndicatorView = DotIndicatorView(frame: .zero, context: .inbox, dataManager: ICMDataManager.shared())
        dotIndicatorView!.translatesAutoresizingMaskIntoConstraints = false
        dotIndicatorView!.isHidden = true
    }
    override func prepareForReuse() {
        super.prepareForReuse()
        typingIndicatorState?.timer?.invalidate()
        typingIndicatorState?.timer = nil
        typingIndicatorState = nil
        updateUIForTypingState(showTyping: false, details: nil)
    }
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        if previousTraitCollection?.preferredContentSizeCategory == traitCollection.preferredContentSizeCategory { return }
        for stack in [mainStackView, messageDetailsStackView] {
            for child in stack.subviews { stack.removeArrangedSubview(child); child.removeFromSuperview() }
        }
        mainStackView.spacing = ICMUtils.scale(toAccessibilitySizing: 12)
        if ICMUtils.contentSizeCategoryIsAccessibilityCategory() { configureMainStackViewForAccessibilitySizes() }
        else { configureMainStackViewForRegularSizes() }
    }
    @objc func configure(for item: InboxCellViewItem) {
        viewItem = item
        guard let conversation = item.conversation else { return }
        multipleAvatarView.cancelImageLoad()
        multipleAvatarView.bindAdmins(conversation.adminsToShow()!)
        name = conversation.participantsInConversation() ?? ""
        if let part = conversation.lastPart() {
            sentTime = timestampCalculator.timestampText(for: part.createdAt, format: .short, recentTimeMessage: .justNow) ?? ""
            messageContent = part.summary ?? ""
        } else { sentTime = ""; messageContent = "" }
        messageContentLabel.text = messageContent
        updateMessageContentStackView()
        title = conversation.title ?? ""
        configureAsUnread(!conversation.read)
        unreadDotView.isHidden = !(item.canShowUnreadDot && !conversation.read && !messageContent.isEmpty)
        let date = conversation.lastPart()?.createdAt
        let admins = conversation.adminsToShow()! as! [Participant]
        setupAccessibility(sentTime: date, admins: admins, unread: !conversation.read)
        if item.showDividerLine { setupDividerLine() }
    }
    private func configureAsUnread(_ unread: Bool) {
        combinedContextLabel.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .subheadline).pointSize, weight: unread ? .semibold : .regular)
        titleLabel.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .subheadline).pointSize, weight: unread ? .semibold : .regular)
    }
    private func setupAccessibility(sentTime: Date?, admins: [Participant], unread: Bool) {
        let timestamp = timestampCalculator.timestampText(for: sentTime, format: .long, recentTimeMessage: .justNow) ?? ""
        let summary = title.isEmpty ? messageContent : title
        let key: String
        let parameters: [String: String]
        if admins.count == 1, let admin = admins[0] as? ParticipatingAdmin {
            let status = TranslationManager.messenger.translation(forKey: admin.isActive ? "active_state" : "away_state")
            key = "message_summary_with_teammate_status"
            parameters = ["summary": summary, "name": name, "adminStatus": status, "sentTime": timestamp]
        } else {
            key = "message_summary"
            parameters = ["summary": summary, "name": name, "sentTime": timestamp]
        }
        var text = TranslationManager.messenger.translation(forKey: key)
        for (key, value) in parameters { text = text.replacingOccurrences(of: "{" + key + "}", with: value) }
        accessibilityLabel = text
        if unread { accessibilityLabel = (accessibilityLabel ?? "") + TranslationManager.messenger.translation(forKey: "message_unread") }
    }
    private func setupDividerLine() {
        addSubview(dividerLine)
        NSLayoutConstraint.activate([
            dividerLine.leadingAnchor.constraint(equalTo: layoutMarginsGuide.leadingAnchor),
            dividerLine.trailingAnchor.constraint(equalTo: trailingAnchor),
            dividerLine.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
    }
    func updateUIForTypingState(showTyping: Bool, details: TypingAdminDetails?) {
        titleTimestampStackView.isHidden = showTyping
        messageContentStackView.isHidden = showTyping
        combinedContextLabel.isHidden = showTyping
        let avatars = multipleAvatarView
        if showTyping {
            avatars.isHidden = true
            dotIndicatorView!.isHidden = false
            if let details { dotIndicatorView!.configure(with: details) }
            messageDetailsStackView.addArrangedSubview(dotIndicatorView!)
            dotIndicatorView!.invalidateIntrinsicContentSize()
            dotIndicatorView!.setNeedsLayout()
        } else {
            avatars.isHidden = false
            dotIndicatorView!.avatar.removeFromSuperview()
            dotIndicatorView!.removeFromSuperview()
            dotIndicatorView!.isHidden = true
            if let conversation = viewItem?.conversation {
                avatars.cancelImageLoad()
                avatars.bindAdmins(conversation.adminsToShow()!)
            }
        }
    }
    func startTypingIndicatorTimer(with details: TypingAdminDetails?) {
        guard typingIndicatorState != nil else { return }
        let timer = Timer.scheduledTimer(withTimeInterval: 5, repeats: false) { [weak self] _ in
            guard let self else { return }
            self.typingIndicatorState?.timer?.invalidate()
            self.typingIndicatorState?.timer = nil
            self.typingIndicatorState = nil
            self.updateUIForTypingState(showTyping: false, details: nil)
        }
        typingIndicatorState?.timer = timer
    }
    private func updateTitleTimestampStackView() {
        for child in titleTimestampStackView.arrangedSubviews { child.removeFromSuperview() }
        titleTimestampStackView.addArrangedSubview(titleLabel)
        if !sentTime.isEmpty { titleTimestampLabel.text = sentTime; titleTimestampStackView.addArrangedSubview(titleTimestampLabel) }
        titleTimestampStackView.setContentHuggingPriority(.defaultLow, for: .horizontal)
        titleLabel.setContentHuggingPriority(.defaultLow, for: .horizontal)
        titleTimestampLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
        titleTimestampLabel.setContentHuggingPriority(.required, for: .horizontal)
    }
    private func updateMessageContentStackView() {
        for child in messageContentStackView.arrangedSubviews { child.removeFromSuperview() }
        if !messageContent.isEmpty { messageContentStackView.addArrangedSubview(messageContentLabel) }
        messageContentStackView.setContentHuggingPriority(.defaultLow, for: .horizontal)
        messageContentLabel.setContentHuggingPriority(.defaultLow, for: .horizontal)
        messageContentLabel.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
    }
    private func pinMessageContentHorizontally() {
        let parent = messageContentStackView.superview!
        messageContentStackView.leadingAnchor.constraint(equalTo: parent.leadingAnchor).isActive = true
        messageContentStackView.trailingAnchor.constraint(equalTo: parent.trailingAnchor).isActive = true
    }
    private func configureMainStackViewForAccessibilitySizes() {
        messageDetailsStackView.spacing = ICMUtils.scale(toAccessibilitySizing: 12)
        updateTitleTimestampStackView()
        updateMessageContentStackView()
        for child in [titleTimestampStackView, messageContentStackView, nameLabel, timeLabel] { messageDetailsStackView.addArrangedSubview(child) }
        pinMessageContentHorizontally()
        mainStackView.addArrangedSubview(messageDetailsStackView)
        mainStackView.addArrangedSubview(unreadDotView)
    }
    private func configureMainStackViewForRegularSizes() {
        messageDetailsStackView.spacing = ICMUtils.scale(toAccessibilitySizing: 4)
        updateTitleTimestampStackView()
        updateMessageContentStackView()
        messageDetailsStackView.addArrangedSubview(titleTimestampStackView)
        messageDetailsStackView.addArrangedSubview(messageContentStackView)
        pinMessageContentHorizontally()
        mainStackView.addArrangedSubview(multipleAvatarView)
        mainStackView.addArrangedSubview(messageDetailsStackView)
        mainStackView.addArrangedSubview(unreadDotView)
    }
    @objc private func performAction() { action?() }
}
