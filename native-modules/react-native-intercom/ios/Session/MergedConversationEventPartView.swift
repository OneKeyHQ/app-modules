import UIKit

@objc(_TtC18IntercomSDKPrivate31MergedConversationEventPartView)
class MergedConversationEventPartView: UIView, ConversationCellContentView {
    let content: ICMConversationPart
    private let themeProvider: ThemeProvider
    private var conversationId: Int?
    private let delegate: ConversationViewModel?
    private let containerStackView: UIStackView
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textAlignment = .center
        label.lineBreakMode = .byTruncatingTail
        label.numberOfLines = 1
        label.textColor = themeProvider.theme.textMuted
        label.font = .preferredFont(forTextStyle: .subheadline)
        return label
    }()
    private lazy var linkLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textAlignment = .center
        label.lineBreakMode = .byTruncatingTail
        label.numberOfLines = 1
        label.text = "View conversation"
        label.textColor = themeProvider.theme.textDefault
        let descriptor = UIFontDescriptor.preferredFontDescriptor(withTextStyle: .subheadline)
        label.font = .systemFont(ofSize: descriptor.pointSize, weight: .semibold)
        label.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(navigateToConversation)))
        label.isUserInteractionEnabled = true
        return label
    }()
    init(for content: ICMConversationPart, delegate: ConversationViewModel?, themeProvider: ThemeProvider) {
        conversationId = nil
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .center
        stack.distribution = .fill
        stack.spacing = 4
        stack.translatesAutoresizingMaskIntoConstraints = false
        containerStackView = stack
        self.content = content
        self.delegate = delegate
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        conversationId = content.eventData!.mergedConversationId
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(containerStackView)
        configure(with: content)
        configureConstraints()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func configure(with part: ICMConversationPart) {
        containerStackView.removeAllArrangedSubviews()
        [titleLabel, linkLabel].forEach { containerStackView.addArrangedSubview($0) }
        let title = titleLabel
        title.text = part.eventData!.eventDescription
    }
    private func configureConstraints() {
        let topView = containerStackView
        let topParent = topView.superview!
        topView.translatesAutoresizingMaskIntoConstraints = false
        topView.translatesAutoresizingMaskIntoConstraints = false
        let top = topView.topAnchor.constraint(equalTo: topParent.topAnchor, constant: 24)
        top.priority = .required
        top.isActive = true
        let bottomView = containerStackView
        let bottomParent = bottomView.superview!
        bottomView.translatesAutoresizingMaskIntoConstraints = false
        let safeArea = bottomParent.safeAreaLayoutGuide
        bottomView.translatesAutoresizingMaskIntoConstraints = false
        let bottom = bottomView.bottomAnchor.constraint(equalTo: safeArea.bottomAnchor, constant: -12)
        bottom.priority = .required
        bottom.isActive = true
        pinHorizontal(containerStackView)
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
    @objc private func navigateToConversation() {
        guard let conversationId else { return }
        let conversation = ICMConversation()
        conversation.conversationId = String(conversationId)
        delegate?.show(conversation: conversation)
    }
}
