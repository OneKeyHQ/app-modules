import UIKit

protocol ClosedConversationViewDelegate: AnyObject {
    func navigateToConversation(with id: String?)
}

@objc(_TtC18IntercomSDKPrivate22ClosedConversationView)
class ClosedConversationView: UIView {
    let themeProvider: any ThemeProvider
    private lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .center
        stack.distribution = .fill
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.isLayoutMarginsRelativeArrangement = true
        stack.spacing = 16
        return stack
    }()
    private lazy var conversationLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        if let data = buttonData { label.text = data.title }
        else { label.text = TranslationManager.messenger.translation(forKey: "conversation_has_ended") }
        label.lineBreakMode = .byWordWrapping
        label.numberOfLines = 0
        label.textColor = themeProvider.theme.textMuted
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.adjustsFontForContentSizeCategory = true
        label.textAlignment = .center
        return label
    }()
    private lazy var conversationButton: ICMActionButton = {
        let button = ICMActionButton()
        if let data = buttonData { button.setTitle(data.text, for: .normal) }
        else { button.setTitle(dataManager.conversationCTA?.label, for: .normal) }
        if buttonData != nil { button.setImage(ICMAssets.sendMessageIcon(), for: .normal) }
        else { button.setImage(dataManager.conversationCTA?.icon.image(), for: .normal) }
        button.translatesAutoresizingMaskIntoConstraints = false
        button.addTarget(self, action: #selector(conversationButtonTapped(_:)), for: .touchUpInside)
        button.isHidden = buttonData?.metadata?.mergedIntoConversationId != nil ? false : !canStartNewConversation
        return button
    }()
    var buttonData: ConversationEndedButton?
    weak var delegate: (any ClosedConversationViewDelegate)?
    var canStartNewConversation: Bool
    let dataManager: any ICMDataManagerProtocol

    init(buttonData: ConversationEndedButton?, delegate: (any ClosedConversationViewDelegate)?,
         startNewConversationButtonEnabled: Bool, dataManager: any ICMDataManagerProtocol,
         themeProvider: any ThemeProvider) {
        self.buttonData = buttonData
        self.delegate = delegate
        canStartNewConversation = startNewConversationButtonEnabled
        self.dataManager = dataManager
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(containerStackView)
        containerStackView.topAnchor.constraint(equalTo: containerStackView.superview!.topAnchor, constant: 24).isActive = true
        containerStackView.bottomAnchor.constraint(equalTo: containerStackView.superview!.safeAreaLayoutGuide.bottomAnchor, constant: -32).isActive = true
        if containerStackView.effectiveUserInterfaceLayoutDirection == .leftToRight {
            containerStackView.leftAnchor.constraint(equalTo: containerStackView.superview!.leftAnchor).isActive = true
            containerStackView.rightAnchor.constraint(equalTo: containerStackView.superview!.rightAnchor).isActive = true
        } else {
            containerStackView.rightAnchor.constraint(equalTo: containerStackView.superview!.rightAnchor).isActive = true
            containerStackView.leftAnchor.constraint(equalTo: containerStackView.superview!.leftAnchor).isActive = true
        }
        containerStackView.backgroundColor = .clear
        containerStackView.addArrangedSubview(conversationLabel)
        containerStackView.addArrangedSubview(conversationButton)
    }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    func updateButton(with data: ConversationEndedButton?, visibility: Bool) {
        buttonData = data
        canStartNewConversation = visibility
        let label = conversationLabel
        if let data = buttonData { label.text = data.title }
        else { label.text = TranslationManager.messenger.translation(forKey: "conversation_has_ended") }
        let button = conversationButton
        if let data = buttonData { button.setTitle(data.text, for: .normal) }
        else { button.setTitle(dataManager.conversationCTA?.label, for: .normal) }
        let imageButton = conversationButton
        if buttonData != nil { imageButton.setImage(ICMAssets.sendMessageIcon(), for: .normal) }
        else { imageButton.setImage(dataManager.conversationCTA?.icon.image(), for: .normal) }
        conversationButton.isHidden = buttonData?.metadata?.mergedIntoConversationId != nil ? false : !canStartNewConversation
    }

    @objc private func conversationButtonTapped(_ sender: Any) {
        guard let current = delegate else { return }
        current.navigateToConversation(with: buttonData?.metadata?.mergedIntoConversationId)
    }
}
