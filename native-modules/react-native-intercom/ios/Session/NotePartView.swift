import UIKit

@objc(_TtC18IntercomSDKPrivate12NotePartView)
class NotePartView: UIView, ConversationCellContentView {
    lazy var blocksMaxHeight: CGFloat = .greatestFiniteMagnitude
    lazy var blocksContainerViewBottomAnchor: NSLayoutYAxisAnchor = bottomAnchor
    var delegate: (any ConversationCellBlockDelegate)?
    let content: ICMConversationPart
    private lazy var blocksContainerView: UIView = {
        let view = UIView()
        view.clipsToBounds = true
        view.accessibilityLabel = "Blocks Container View"
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    lazy var blocksStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.distribution = .fill
        stack.layer.masksToBounds = true
        stack.isUserInteractionEnabled = true
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private let themeProvider: ThemeProvider
    private let config: ICMConfigProtocol
    private lazy var avatarView: ICMAvatarView = {
        let view = ICMAvatarView(noTeammatePresenceWithAvatarDiameter: 28)!
        view.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([view.widthAnchor.constraint(equalToConstant: 28), view.heightAnchor.constraint(equalToConstant: 28)])
        return view
    }()
    private lazy var adminTextView: UITextView = {
        let view = UITextView()
        view.isEditable = false
        view.isSelectable = false
        view.isScrollEnabled = false
        view.adjustsFontForContentSizeCategory = true
        view.accessibilityIdentifier = "intercom admin name and status"
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    init(content: ICMConversationPart, themeProvider: ThemeProvider, config: ICMConfigProtocol) {
        self.content = content
        self.themeProvider = themeProvider
        self.config = config
        super.init(frame: .zero)
    }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    func setUpView() {
        translatesAutoresizingMaskIntoConstraints = false
        backgroundColor = themeProvider.theme.base
        layer.cornerRadius = 20
        layer.borderWidth = 1
        layer.borderColor = DefaultThemeProvider.shared.theme.neutralBorder.cgColor
        addSubviews()
        let part = content
        if let participant = part.participant { avatarView.loadAvatar(for: participant) }
        bindAdminName(for: part)
        renderBlocks(for: part)
    }
    func addSubviews() {
        addHeaderViews()
        addBlocksStackView()
    }
    private func addHeaderViews() {
        addSubview(avatarView)
        addSubview(adminTextView)
        NSLayoutConstraint.activate([
            avatarView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 12),
            avatarView.centerYAnchor.constraint(equalTo: adminTextView.centerYAnchor),
            adminTextView.leadingAnchor.constraint(equalTo: avatarView.trailingAnchor, constant: 8),
            adminTextView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -12),
            adminTextView.topAnchor.constraint(equalTo: topAnchor, constant: 12)
        ])
    }
    private func addBlocksStackView() {
        addSubview(blocksContainerView)
        NSLayoutConstraint.activate([
            blocksContainerView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 24),
            blocksContainerView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -24),
            blocksContainerView.topAnchor.constraint(equalTo: adminTextView.bottomAnchor, constant: 7),
            blocksContainerView.bottomAnchor.constraint(equalTo: blocksContainerViewBottomAnchor),
            blocksContainerView.heightAnchor.constraint(lessThanOrEqualToConstant: blocksMaxHeight)
        ])
        blocksContainerView.addSubview(blocksStackView)
        let bottom = blocksStackView.bottomAnchor.constraint(equalTo: blocksContainerView.bottomAnchor, constant: -12)
        bottom.priority = .defaultLow
        NSLayoutConstraint.activate([
            blocksStackView.leadingAnchor.constraint(equalTo: blocksContainerView.leadingAnchor, constant: 24),
            blocksStackView.trailingAnchor.constraint(equalTo: blocksContainerView.trailingAnchor, constant: -24),
            blocksStackView.topAnchor.constraint(equalTo: blocksContainerView.topAnchor, constant: 7),
            bottom
        ])
    }
    private func bindAdminName(for part: ICMConversationPart) {
        let attributes: [NSAttributedString.Key: Any] = [.font: UIFont.preferredFont(forTextStyle: .footnote), .foregroundColor: themeProvider.theme.textDefault]
        if let participant = part.participant, let appName = config.appName {
            adminTextView.attributedText = participant.adminAppNameTitle(attributes, appName: appName)
        }
    }
    private func renderBlocks(for part: ICMConversationPart) {
        var capturedDelegate = delegate
        let views = BlockViewFactory.getViewsForBlocks(in: part, delegate: capturedDelegate, citationDelegate: nil, maxWidth: nil, imageDisplayContext: .context0)
        capturedDelegate = nil
        for view in views { blocksStackView.addArrangedSubview(view) }
    }
}
