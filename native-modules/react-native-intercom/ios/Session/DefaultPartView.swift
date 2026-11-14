import UIKit

@objc(_TtC18IntercomSDKPrivate15DefaultPartView)
class DefaultPartView: UIView, ConversationCellContentView {
    let content: ICMConversationPart
    var avatarView: ICMAvatarView?
    private lazy var summaryLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.numberOfLines = 0
        label.font = .preferredFont(forTextStyle: .subheadline)
        return label
    }()
    private lazy var summaryLabelContainerView: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.cornerRadius = 6
        view.layer.masksToBounds = true
        return view
    }()
    init(content: ICMConversationPart, delegate: (any ConversationCellBlockDelegate)?) {
        self.content = content
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        configure(delegate: delegate)
    }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    private func createAvatarView(for part: ICMConversationPart) -> ICMAvatarView? {
        if !part.isEvent() { return nil }
        let view = ICMAvatarView(noTeammatePresenceWithAvatarDiameter: 20)!
        view.setTeamPresenceBorderColor(DefaultThemeProvider.shared.theme.base)
        view.translatesAutoresizingMaskIntoConstraints = false
        if part.groupingPosition == 3 || part.groupingPosition == 2 {
            if let participant = part.participant { view.loadAvatar(for: participant) }
        }
        NSLayoutConstraint.activate([view.widthAnchor.constraint(equalToConstant: 20), view.heightAnchor.constraint(equalToConstant: 20)])
        avatarView = view
        view.accessibilityElementsHidden = true
        return view
    }
    private func addAvatarViewIfNecessary(for part: ICMConversationPart, to container: UIView) {
        guard let avatar = createAvatarView(for: part) else { return }
        container.addSubview(avatar)
        NSLayoutConstraint.activate([
            avatar.leadingAnchor.constraint(equalTo: container.leadingAnchor),
            avatar.bottomAnchor.constraint(equalTo: container.bottomAnchor)
        ])
    }
    private func configure(delegate: (any ConversationCellBlockDelegate)?) {
        let part = content
        addAvatarViewIfNecessary(for: part, to: self)
        let anchor = summaryLabelContainerView
        handleFailure(for: part, anchorTo: anchor) { [weak self, delegate] button in
            guard let self = self else { return }
            delegate?.retryTapped(conversationPart: self.content, sender: button)
        }
        let label = summaryLabel
        label.text = part.summary
        summaryLabelContainerView.addSubview(summaryLabel)
        summaryLabelContainerView.backgroundColor = part.getBackgroundColor(theme: DefaultThemeProvider.shared)
        addSubview(summaryLabelContainerView)
        NSLayoutConstraint.activate([
            summaryLabel.leadingAnchor.constraint(equalTo: summaryLabelContainerView.leadingAnchor, constant: 8),
            summaryLabel.trailingAnchor.constraint(equalTo: summaryLabelContainerView.trailingAnchor, constant: -8),
            summaryLabel.topAnchor.constraint(equalTo: summaryLabelContainerView.topAnchor, constant: 8),
            summaryLabel.bottomAnchor.constraint(equalTo: summaryLabelContainerView.bottomAnchor, constant: -8),
            summaryLabelContainerView.topAnchor.constraint(equalTo: topAnchor),
            summaryLabelContainerView.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
        let personal = part.isPersonalPart()
        let leading = summaryLabelContainerView.leadingAnchor
        if personal {
            leading.constraint(greaterThanOrEqualTo: leadingAnchor, constant: 65).isActive = true
            summaryLabelContainerView.trailingAnchor.constraint(equalTo: trailingAnchor).isActive = true
        } else {
            let anchor = avatarView?.trailingAnchor ?? leadingAnchor
            leading.constraint(equalTo: anchor, constant: 8).isActive = true
            summaryLabelContainerView.trailingAnchor.constraint(lessThanOrEqualTo: trailingAnchor, constant: -65).isActive = true
        }
    }
    private func handleFailure(for part: ICMConversationPart, anchorTo anchor: UIView, _ action: @escaping (UIButton) -> Void) {
        if !part.isPersonalPart() { return }
        if part.state != 2 { return }
        anchor.alpha = 0.5
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false
        button.setImage(ICMAssets.messageFailedImage(), for: .normal)
        button.addAction(for: .touchUpInside, action)
        addSubview(button)
        NSLayoutConstraint.activate([
            button.widthAnchor.constraint(equalToConstant: 16),
            button.heightAnchor.constraint(equalToConstant: 16),
            button.centerYAnchor.constraint(equalTo: anchor.centerYAnchor),
            button.trailingAnchor.constraint(equalTo: anchor.leadingAnchor, constant: -16)
        ])
    }
}
