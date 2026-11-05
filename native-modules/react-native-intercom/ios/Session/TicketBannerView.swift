import UIKit

@objc(_TtC18IntercomSDKPrivateP33_E834D7A15A60EE08082189964CCECA3936TicketBannerViewTapGestureRecognizer)
private class TicketBannerViewTapGestureRecognizer: UITapGestureRecognizer {
    var conversation: ICMConversation?
    override init(target: Any?, action: Selector?) {
        conversation = nil
        super.init(target: target, action: action)
    }
}

@objc(_TtC18IntercomSDKPrivate16TicketBannerView)
class TicketBannerView: UIView {
    private let themeProvider: ThemeProvider
    private lazy var ticketTitleLabel: UILabel = {
        let label = UILabel()
        label.textColor = themeProvider.theme.primaryForeground
        label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .footnote).pointSize, weight: .semibold)
        label.lineBreakMode = .byTruncatingTail
        label.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
        label.adjustsFontForContentSizeCategory = false
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private var containerStackView: UIStackView
    private lazy var ticketIconView: UIImageView = {
        let view = UIImageView(image: ICMAssets.ticketIcon())
        view.contentMode = .center
        view.tintColor = themeProvider.theme.primaryForeground
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.widthAnchor.constraint(equalToConstant: 16).isActive = true
        view.translatesAutoresizingMaskIntoConstraints = false
        view.heightAnchor.constraint(equalToConstant: 16).isActive = true
        return view
    }()
    private lazy var dividerView: UIView = {
        let view = UIView()
        view.backgroundColor = themeProvider.theme.neutralBorder
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var tapGesture = TicketBannerViewTapGestureRecognizer(target: self, action: #selector(presentTicketBannerAction(with:)))
    init(frame: CGRect, conversation: ICMConversation, themeProvider: ThemeProvider) {
        self.themeProvider = themeProvider
        let stack = UIStackView()
        stack.axis = .horizontal
        containerStackView = stack
        super.init(frame: frame)
        backgroundColor = DefaultThemeProvider.shared.theme.base
        setupView()
        setupConstraints()
        addGestureRecognizer(tapGesture)
        updateStatusInformation(conversation: conversation)
        isAccessibilityElement = true
        accessibilityTraits = .button
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func setupView() {
        backgroundColor = DefaultThemeProvider.shared.theme.base
        [ticketIconView, ticketTitleLabel].forEach { containerStackView.addArrangedSubview($0) }
        containerStackView.setCustomSpacing(10, after: ticketIconView)
        addSubview(containerStackView)
        addSubview(dividerView)
    }
    private func setupConstraints() {
        let container = containerStackView
        let centerParent = container.superview!
        container.translatesAutoresizingMaskIntoConstraints = false
        container.translatesAutoresizingMaskIntoConstraints = false
        container.centerXAnchor.constraint(equalTo: centerParent.safeAreaLayoutGuide.centerXAnchor).isActive = true
        let leading = containerStackView.leadingAnchor.constraint(greaterThanOrEqualTo: safeAreaLayoutGuide.leadingAnchor, constant: 12)
        let trailing = containerStackView.trailingAnchor.constraint(greaterThanOrEqualTo: safeAreaLayoutGuide.trailingAnchor, constant: -12)
        for constraint in [leading, trailing] { constraint.priority = .defaultLow; constraint.isActive = true }
        containerStackView.widthAnchor.constraint(lessThanOrEqualTo: safeAreaLayoutGuide.widthAnchor, constant: -12).isActive = true
        let topView = containerStackView
        let topParent = topView.superview!
        topView.translatesAutoresizingMaskIntoConstraints = false
        topView.translatesAutoresizingMaskIntoConstraints = false
        topView.topAnchor.constraint(equalTo: topParent.topAnchor, constant: 10).isActive = true
        let bottomView = containerStackView
        let bottomParent = bottomView.superview!
        bottomView.translatesAutoresizingMaskIntoConstraints = false
        bottomView.translatesAutoresizingMaskIntoConstraints = false
        bottomView.bottomAnchor.constraint(equalTo: bottomParent.bottomAnchor, constant: -10).isActive = true
        dividerView.translatesAutoresizingMaskIntoConstraints = false
        dividerView.heightAnchor.constraint(equalToConstant: 1).isActive = true
        pinHorizontal(dividerView)
        let divider = dividerView
        let parent = divider.superview!
        divider.translatesAutoresizingMaskIntoConstraints = false
        divider.translatesAutoresizingMaskIntoConstraints = false
        divider.bottomAnchor.constraint(equalTo: parent.bottomAnchor, constant: -CGFloat.zero).isActive = true
    }
    func updateStatusInformation(conversation: ICMConversation) {
        guard let ticket = conversation.ticket else { return }
        let title = ticketTitleLabel
        title.text = ticket.title
        tapGesture.conversation = conversation
        let params = ["title": ticket.title]
        var text = TranslationManager.messenger.translation(forKey: "ticket_details_for_title")
        for (key, value) in params { text = text.replacingOccurrences(of: "{" + key + "}", with: value) }
        accessibilityLabel = text
    }
    @objc(presentTicketBannerActionWithGestureRecognizer:)
    private func presentTicketBannerAction(with recognizer: TicketBannerViewTapGestureRecognizer) {
        guard let conversation = recognizer.conversation else { return }
        let manager = ICMPresentationManager.sharedInstance()!
        let ticket = conversation.ticket!
        manager.presentTicket(ticket, fromTriggerSource: 0)
    }
    private func pinHorizontal(_ view: UIView) {
        let rtl = view.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.leftAnchor.constraint(equalTo: parent.leftAnchor).isActive = true
        }
        func right() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.rightAnchor.constraint(equalTo: parent.rightAnchor, constant: -CGFloat.zero).isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
    }
}
