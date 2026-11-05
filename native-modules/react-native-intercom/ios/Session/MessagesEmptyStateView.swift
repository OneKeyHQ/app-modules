import UIKit

@objc(_TtP18IntercomSDKPrivate26MessagesEmptyStateDelegate_)
protocol MessagesEmptyStateDelegate {
    @objc(performActionForType:)
    func performActionFor(type: MessagesEmptyStateActionType)
}

@objc(_TtC18IntercomSDKPrivate22MessagesEmptyStateView)
class MessagesEmptyStateView: UIView {
    private let themeProvider: ThemeProvider
    private lazy var messageIconView: UIImageView = {
        let icon = UIImageView(image: ICMAssets.messageSpaceIcon())
        icon.contentMode = .scaleAspectFit
        icon.translatesAutoresizingMaskIntoConstraints = false
        icon.tintColor = themeProvider.theme.iconDefault
        icon.heightAnchor.constraint(equalToConstant: 32).isActive = true
        icon.widthAnchor.constraint(equalToConstant: 32).isActive = true
        return icon
    }()
    private lazy var descriptionLabel: UILabel = {
        let label = UILabel()
        label.font = .preferredFont(forTextStyle: .callout)
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        label.adjustsFontForContentSizeCategory = true
        return label
    }()
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .title3).pointSize, weight: .semibold)
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        label.adjustsFontForContentSizeCategory = true
        return label
    }()
    private lazy var actionButton = ICMActionButton()
    private lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .center
        return stack
    }()
    private weak var delegate: MessagesEmptyStateDelegate?
    private let messagesMetricService: MessagesMetricServiceProtocol

    @objc(initWithFrame:emptyStateConfig:delegate:)
    convenience init(frame: CGRect, emptyStateConfig: MessagesEmptyState, delegate: MessagesEmptyStateDelegate?) {
        self.init(frame: frame, emptyStateConfig: emptyStateConfig, delegate: delegate, messagesMetricService: MessagesMetricService(metricInstance: ICMMetric.sharedInstance()), themeProvider: DefaultThemeProvider.shared)
    }

    init(frame: CGRect, emptyStateConfig: MessagesEmptyState, delegate: MessagesEmptyStateDelegate?, messagesMetricService: MessagesMetricServiceProtocol, themeProvider: ThemeProvider) {
        self.delegate = delegate
        self.messagesMetricService = messagesMetricService
        self.themeProvider = themeProvider
        super.init(frame: frame)
        titleLabel.text = emptyStateConfig.title
        descriptionLabel.text = emptyStateConfig.text
        setUpView()
        if let action = emptyStateConfig.action {
            if action.type == .message { styleButtonAsSendUsAMessage(title: action.label, image: action.icon) }
            else { styleButtonAsGoToArticles(title: action.label) }
            containerStackView.addArrangedSubview(actionButton)
        }
        setupConstraints()
        descriptionLabel.accessibilityLabel = emptyStateConfig.title + "; " + emptyStateConfig.text
        accessibilityElements = [descriptionLabel, actionButton]
    }

    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private func setUpView() {
        backgroundColor = themeProvider.theme.base
        let stack = containerStackView
        stack.addArrangedSubview(messageIconView)
        stack.addArrangedSubview(titleLabel)
        stack.addArrangedSubview(descriptionLabel)
        stack.setCustomSpacing(16, after: messageIconView)
        stack.setCustomSpacing(16, after: titleLabel)
        stack.setCustomSpacing(16, after: descriptionLabel)
        addSubview(stack)
    }

    private func setupConstraints() {
        let superview = containerStackView.superview!
        containerStackView.translatesAutoresizingMaskIntoConstraints = false
        containerStackView.centerXAnchor.constraint(equalTo: superview.centerXAnchor).isActive = true
        containerStackView.centerYAnchor.constraint(equalTo: superview.centerYAnchor).isActive = true
        containerStackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 38).isActive = true
        containerStackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -38).isActive = true
    }

    private func styleButtonAsSendUsAMessage(title: String, image: UIImage?) {
        actionButton.setTitle(title, for: .normal)
        actionButton.setImage(image ?? ICMAssets.sendMessageIcon(), for: .normal)
        let background = themeProvider.theme.primaryBackground
        let theme = themeProvider.theme
        let foreground = ColorHelper.suitableDynamicForegroundColor(for: theme.primaryBackground, theme: theme)
        actionButton.configure(forStyle: 0, backgroundColor: background, foregroundColor: foreground)
        actionButton.addTarget(self, action: #selector(sendAMessageTapped(_:)), for: .touchUpInside)
    }

    private func styleButtonAsGoToArticles(title: String) {
        actionButton.setTitle(title, for: .normal)
        actionButton.setImage(ICMAssets.articlesIcon(), for: .normal)
        let theme = themeProvider.theme
        let foreground = ColorHelper.suitableDynamicForegroundColor(for: theme.primaryBackground, theme: theme)
        actionButton.configure(forStyle: 1, backgroundColor: nil, foregroundColor: foreground)
        actionButton.addTarget(self, action: #selector(goToArticlesTapped(_:)), for: .touchUpInside)
    }

    @objc private func sendAMessageTapped(_ sender: UIButton) {
        messagesMetricService.addNewConversationFromMessagesMetric()
        delegate?.performActionFor(type: .message)
    }
    @objc private func goToArticlesTapped(_ sender: UIButton) { delegate?.performActionFor(type: .help) }
}
