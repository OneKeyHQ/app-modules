import UIKit

@objc(_TtP18IntercomSDKPrivate28ChatNotificationViewDelegate_)
protocol ChatNotificationViewDelegate: AnyObject {
    func quickReplyTapped()
}

@objc(_TtC18IntercomSDKPrivate20ChatNotificationView)
class ChatNotificationView: UIView, QuickReplyDelegate {
    @objc var participantNameAndTimeLabelText: String?
    private let width: CGFloat
    private let themeProvider: DefaultThemeProvider
    private weak var delegate: (any ChatNotificationViewDelegate)?
    private var conversationPart: ICMConversationPart
    private var ticket: Ticket?
    private var previousSize = CGSize.zero
    private var verticalSizeClass = UIUserInterfaceSizeClass.unspecified
    private lazy var containerView: UIView = {
        let view = UIView()
        view.clipsToBounds = true
        view.layer.cornerRadius = 20
        view.backgroundColor = .clear
        return view
    }()
    private lazy var notificationContentView: ChatNotificationContentView = {
        let config = ICMConfig.shared()!
        let provider = themeProvider
        let timestamp = TimestampCalculator.shared
        let content = ChatNotificationContentView(preventFullLayoutStyle: false, blocksWidth: width, config: config, themeProvider: provider, timestampCalculator: timestamp)
        content.configure(for: conversationPart, ticket: ticket)
        return content
    }()
    private lazy var gradientView: ICMGradientView = {
        let view = ICMGradientView()
        let layer = view.layer!
        layer.colors = [themeProvider.theme.base.withAlphaComponent(0).cgColor, themeProvider.theme.base.cgColor]
        view.layer.cornerRadius = 10
        return view
    }()
    private lazy var quickReplyView: QuickReplyView? = quickReplyView(for: conversationPart)
    private var containerBottomConstraint: NSLayoutConstraint?
    @objc init(conversationPart: ICMConversationPart, ticket: Ticket?, width: CGFloat, delegate: (any ChatNotificationViewDelegate)?, themeProvider: DefaultThemeProvider?) {
        self.conversationPart = conversationPart
        self.ticket = ticket
        self.width = width
        self.delegate = delegate
        self.themeProvider = themeProvider ?? DefaultThemeProvider.shared
        super.init(frame: .zero)
        setupView()
        addContentView()
        addQuickReplyView()
        addGradientView()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func setupView() {
        backgroundColor = themeProvider.theme.base
        layer.masksToBounds = false
        layer.shadowOffset = CGSize(width: 0, height: 2)
        layer.shadowRadius = 48
        layer.shadowOpacity = 1
        layer.cornerRadius = 10
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        layoutQuickReplies()
        containerView.layoutIfNeeded()
        let gradient = gradientView
        let contentHeight = notificationContentView.bounds.height
        let containerHeight = containerView.bounds.height
        gradient.isHidden = contentHeight <= containerHeight
    }
    private func addContentView() {
        addSubview(containerView)
        let hasReplies = quickReplyView != nil
        let bottom = containerView.bottomAnchor.constraint(equalTo: bottomAnchor)
        bottom.priority = .required
        bottom.isActive = !hasReplies
        containerBottomConstraint = bottom
        pinTopAndSides(containerView, to: self)
        containerView.addSubview(notificationContentView)
        let contentBottom = notificationContentView.bottomAnchor.constraint(equalTo: containerView.bottomAnchor)
        contentBottom.priority = .init(250)
        contentBottom.isActive = true
        pinTopAndSides(notificationContentView, to: containerView)
    }
    private func pinTopAndSides(_ child: UIView, to parent: UIView) {
        child.translatesAutoresizingMaskIntoConstraints = false
        child.topAnchor.constraint(equalTo: parent.topAnchor).isActive = true
        let rtl = child.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            child.translatesAutoresizingMaskIntoConstraints = false
            child.leftAnchor.constraint(equalTo: parent.leftAnchor).isActive = true
        }
        func right() {
            child.translatesAutoresizingMaskIntoConstraints = false
            child.rightAnchor.constraint(equalTo: parent.rightAnchor, constant: -CGFloat.zero).isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
    }
    private func addQuickReplyView() {
        guard let replies = quickReplyView else { return }
        addSubview(replies)
        replies.translatesAutoresizingMaskIntoConstraints = false
        replies.leadingAnchor.constraint(equalTo: layoutMarginsGuide.leadingAnchor).isActive = true
        replies.translatesAutoresizingMaskIntoConstraints = false
        replies.trailingAnchor.constraint(equalTo: layoutMarginsGuide.trailingAnchor).isActive = true
        let parent = replies.superview!
        replies.translatesAutoresizingMaskIntoConstraints = false
        replies.bottomAnchor.constraint(equalTo: parent.bottomAnchor, constant: -30).isActive = true
        replies.translatesAutoresizingMaskIntoConstraints = false
        replies.topAnchor.constraint(equalTo: containerView.bottomAnchor, constant: 12).isActive = true
    }
    private func addGradientView() {
        addSubview(gradientView)
        gradientView.translatesAutoresizingMaskIntoConstraints = false
        gradientView.leftAnchor.constraint(equalTo: containerView.leftAnchor).isActive = true
        gradientView.translatesAutoresizingMaskIntoConstraints = false
        gradientView.bottomAnchor.constraint(equalTo: containerView.bottomAnchor).isActive = true
        gradientView.translatesAutoresizingMaskIntoConstraints = false
        gradientView.rightAnchor.constraint(equalTo: containerView.rightAnchor).isActive = true
        gradientView.translatesAutoresizingMaskIntoConstraints = false
        gradientView.heightAnchor.constraint(equalToConstant: 64).isActive = true
    }
    private func quickReplyView(for part: ICMConversationPart) -> QuickReplyView? {
        let options = part.replyOptionsArray! as! [ReplyOption]
        if options.isEmpty { return nil }
        let width = width
        let left = layoutMargins.left
        let right = layoutMargins.right
        let currentOptions = part.replyOptionsArray! as! [ReplyOption]
        return QuickReplyView(replyOptions: currentOptions, delegate: self, maxWidth: width - left - right, themeProvider: themeProvider)
    }
    private func layoutQuickReplies() {
        guard let replies = quickReplyView else { return }
        let size = frame.size
        if size.width == previousSize.width && size.height == previousSize.height && verticalSizeClass == traitCollection.verticalSizeClass { return }
        previousSize = frame.size
        verticalSizeClass = traitCollection.verticalSizeClass
        replies.replyOptions = conversationPart.replyOptionsArray! as! [ReplyOption]
        replies.stackView.removeAllArrangedSubviews()
        replies.addButtons()
        replies.invalidateIntrinsicContentSize()
        let sizeToFit = replies.stackView.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize)
        if sizeToFit.height > 150 || traitCollection.verticalSizeClass == .compact {
            replies.replyOptions = [ReplyOption(replyText: TranslationManager.messenger.translation(forKey: "reply"), uuid: "", preActionType: .none)]
            replies.stackView.removeAllArrangedSubviews()
            replies.addButtons()
            replies.invalidateIntrinsicContentSize()
        }
    }
    @objc func updateQuickReply() {
        if let old = quickReplyView { old.removeFromSuperview() }
        quickReplyView = quickReplyView(for: conversationPart)
        if let bottom = containerBottomConstraint { bottom.isActive = quickReplyView == nil }
        addQuickReplyView()
    }
    @objc(updateWithConversationPart:ticket:)
    func update(with part: ICMConversationPart, ticket: Ticket?) {
        if part.isEqual(conversationPart) {
            if ticket == nil && self.ticket == nil { return }
            if let ticket, let previous = self.ticket, ticket.isEqual(previous) { return }
        }
        conversationPart = part
        self.ticket = ticket
        notificationContentView.configure(for: part, ticket: ticket)
        updateQuickReply()
    }
    @objc(getParticipantNameAndTimeLabelTextForConversationPart:)
    func getParticipantNameAndTimeLabelText(for part: ICMConversationPart) -> String {
        notificationContentView.participantNameAndTimeLabelText(for: part)
    }
    @objc func setParticipantNameAndTimeLabelAlpha(_ alpha: CGFloat) { notificationContentView.participantNameAndTimeLabel.alpha = alpha }
    @objc func setNotificationContentAlpha(_ alpha: CGFloat) { notificationContentView.setContentAlpha(to: alpha) }
    @objc(loadAvatarFor:)
    func loadAvatar(for part: ICMConversationPart) { notificationContentView.loadAvatar(for: part) }
    @objc func quickReplyTappedFor(replyOption: ReplyOption) {
        conversationPart.selectedReplyOption = replyOption
        delegate?.quickReplyTapped()
    }
}
