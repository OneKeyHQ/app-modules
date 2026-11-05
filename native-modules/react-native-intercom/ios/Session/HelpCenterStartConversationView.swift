import UIKit

@objc(_TtP18IntercomSDKPrivate39HelpCenterStartConversationViewDelegate_)
protocol HelpCenterStartConversationViewDelegate: AutoMockable {
    func didSelectStartConversation()
}

@objc(_TtC18IntercomSDKPrivate31HelpCenterStartConversationView)
class HelpCenterStartConversationView: UIView {
    @objc private weak var startConversationDelegate: (any HelpCenterStartConversationViewDelegate)?
    private var conversationCTA: NewConversationCTAResponse
    private let themeProvider: any ThemeProvider
    private let useFilledButtonStyle: Bool
    private lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .center
        stack.distribution = .fill
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.layer.cornerRadius = 10
        stack.layoutMargins = UIEdgeInsets(top: 15, left: 0, bottom: 15, right: 0)
        stack.isLayoutMarginsRelativeArrangement = true
        stack.spacing = 12
        if useFilledButtonStyle { stack.backgroundColor = themeProvider.theme.neutralContainer }
        return stack
    }()
    private lazy var topSeperator: UIView? = {
        if useFilledButtonStyle { return nil }
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = themeProvider.theme.neutralBorder
        return view
    }()
    private lazy var startConversationButton: ICMActionButton = {
        let filled = useFilledButtonStyle
        let title = conversationCTA.label
        let button = filled ? ICMActionButton(filledButtonWithTitle: title)! : ICMActionButton(borderedButtonWithTitle: title)!
        if !ICMUtils.contentSizeCategoryIsAccessibilityCategory() { button.setImage(conversationCTA.icon.image(), for: .normal) }
        button.accessibilityIdentifier = conversationCTA.label
        button.translatesAutoresizingMaskIntoConstraints = false
        button.addTarget(self, action: #selector(pressedStartConversationButton(touchedView:)), for: .touchUpInside)
        return button
    }()
    private lazy var subtitleLabel: UILabel? = {
        guard let subtitle = conversationCTA.subtitle else { return nil }
        let label = UILabel()
        label.textAlignment = .center
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.translatesAutoresizingMaskIntoConstraints = false
        label.adjustsFontForContentSizeCategory = true
        label.textColor = themeProvider.theme.textDefault
        label.text = subtitle
        label.accessibilityLabel = label.text
        label.lineBreakMode = .byWordWrapping
        label.numberOfLines = 0
        label.textAlignment = .center
        return label
    }()
    @objc(initWithFrame:useFilledButtonStyle:delegate:conversationCTA:themeProvider:)
    init(frame: CGRect, useFilledButtonStyle: Bool, delegate: (any HelpCenterStartConversationViewDelegate)?, conversationCTA: NewConversationCTAResponse, themeProvider: any ThemeProvider) {
        startConversationDelegate = delegate
        self.conversationCTA = conversationCTA
        self.themeProvider = themeProvider
        self.useFilledButtonStyle = useFilledButtonStyle
        super.init(frame: frame)
        backgroundColor = themeProvider.theme.base
        addContainerStackView()
        addSeperator()
        addStartConversationButton()
        if let label = subtitleLabel { containerStackView.addArrangedSubview(label) }
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    @objc(pressedStartConversationButtonWithTouchedView:)
    private func pressedStartConversationButton(touchedView: UIButton?) { startConversationDelegate?.didSelectStartConversation() }
    @objc func setButtonTitle(_ title: String) {
        DispatchQueue.main.async { self.startConversationButton.setTitle(title, for: .normal) }
    }
    private func addContainerStackView() {
        addSubview(containerStackView)
        let stack = containerStackView
        let parent = stack.superview!
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.translatesAutoresizingMaskIntoConstraints = false
        let top = stack.topAnchor.constraint(equalTo: parent.topAnchor)
        top.priority = .required
        top.isActive = true
        let bottom = stack.bottomAnchor.constraint(equalTo: bottomAnchor)
        bottom.priority = .init(999)
        bottom.isActive = true
        stack.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        stack.leadingAnchor.constraint(greaterThanOrEqualTo: safeAreaLayoutGuide.leadingAnchor, constant: 16).isActive = true
        stack.trailingAnchor.constraint(greaterThanOrEqualTo: safeAreaLayoutGuide.trailingAnchor, constant: -16).isActive = true
    }
    private func addSeperator() {
        guard let separator = topSeperator else { return }
        addSubview(separator)
        separator.heightAnchor.constraint(equalToConstant: 1).isActive = true
        separator.topAnchor.constraint(equalTo: topAnchor).isActive = true
        separator.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        let width = separator.widthAnchor.constraint(equalTo: widthAnchor, constant: -320)
        width.priority = .init(999)
        width.isActive = true
    }
    private func addStartConversationButton() {
        containerStackView.addArrangedSubview(startConversationButton)
        let height = (startConversationButton.titleLabel?.intrinsicContentSize.height ?? 0) * 2
        let minimum = startConversationButton.heightAnchor.constraint(greaterThanOrEqualToConstant: height)
        minimum.priority = .init(999)
        minimum.isActive = true
        let width = startConversationButton.widthAnchor.constraint(lessThanOrEqualTo: widthAnchor, constant: -40)
        width.priority = .init(999)
        width.isActive = true
    }
}
