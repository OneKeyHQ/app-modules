import UIKit

@objc(_TtC18IntercomSDKPrivate16LinkListPartView)
class LinkListPartView: UIView, ConversationCellContentView {
    var displayTimestampTimer: Timer?
    var delegate: (any ConversationCellBlockDelegate)?
    let queue: any Dispatcher = AsyncQueue.main
    let content: ICMConversationPart
    var avatarView: ICMAvatarView?
    private var timestampView: PartStatusView?
    private lazy var blockViewContainer: UIView = {
        let view = UIView()
        let theme = DefaultThemeProvider.shared
        view.backgroundColor = theme.theme.base
        view.clipsToBounds = false
        view.applyElevationOneShadow(cornerRadius: 6)
        let layer = view.layer
        layer.borderWidth = 1 / icmDisplayScale
        view.layer.borderColor = theme.theme.neutralBorder.cgColor
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.distribution = .fill
        stack.spacing = 4
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
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
    override func didMoveToWindow() {
        super.didMoveToWindow()
        if traitCollection.displayScale > 0 {
            let width = 1 / icmDisplayScale
            if blockViewContainer.layer.borderWidth != width { blockViewContainer.layer.borderWidth = width }
        }
    }
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        if traitCollection.displayScale > 0 {
            let previousScale = previousTraitCollection?.displayScale ?? 0
            let currentScale = traitCollection.displayScale
            if previousTraitCollection == nil || previousScale != currentScale {
                let width = 1 / icmDisplayScale
                if blockViewContainer.layer.borderWidth != width { blockViewContainer.layer.borderWidth = width }
            }
        }
    }
    private func configure(delegate: (any ConversationCellBlockDelegate)?) {
        addSubview(containerStackView)
        containerStackView.addArrangedSubview(blockViewContainer)
        let stack = containerStackView
        let parent = stack.superview!
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.bottomAnchor.constraint(equalTo: parent.bottomAnchor).isActive = true
        pinHorizontal(blockViewContainer)
        let part = content
        let avatarBottom = blockViewContainer.bottomAnchor
        addAvatarViewIfNecessary(for: part, to: self, customBottomAnchor: avatarBottom)
        addStatusView(in: containerStackView)
        let stackTop = containerStackView.topAnchor.constraint(equalTo: topAnchor)
        let leading = containerStackView.leadingAnchor
        let leadingTarget = avatarView?.trailingAnchor ?? leadingAnchor
        NSLayoutConstraint.activate([stackTop, leading.constraint(equalTo: leadingTarget, constant: 8)])
        containerStackView.widthAnchor.constraint(lessThanOrEqualToConstant: 366).isActive = true
        let trailing = containerStackView.trailingAnchor.constraint(equalTo: trailingAnchor)
        trailing.priority = UILayoutPriority(999)
        trailing.isActive = true
        let blocks: [ITBBlock] = part.blocks!
        guard let first = blocks.first, let list = first as? ITBLinkListBlock,
              let view = LinkListBlockView(content: list, delegate: delegate) else { return }
        blockViewContainer.addSubview(view)
        NSLayoutConstraint.activate([
            view.leadingAnchor.constraint(equalTo: blockViewContainer.leadingAnchor),
            view.trailingAnchor.constraint(equalTo: blockViewContainer.trailingAnchor),
            view.topAnchor.constraint(equalTo: blockViewContainer.topAnchor),
            view.bottomAnchor.constraint(equalTo: blockViewContainer.bottomAnchor)
        ])
    }
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
    private func addAvatarViewIfNecessary(for part: ICMConversationPart, to container: UIView, customBottomAnchor: NSLayoutYAxisAnchor) {
        guard let avatar = createAvatarView(for: part) else { return }
        container.addSubview(avatar)
        NSLayoutConstraint.activate([
            avatar.leadingAnchor.constraint(equalTo: container.leadingAnchor),
            avatar.bottomAnchor.constraint(equalTo: customBottomAnchor)
        ])
    }
    private func addStatusView(in stack: UIStackView) {
        if !content.shouldShowStatus() { return }
        let appendSeenState = content.displayPartStatus
        let theme = DefaultThemeProvider.shared
        let status = PartStatusView(content: content, appendSeenState: appendSeenState, leadingInset: 0, attributedSingleImage: false, themeProvider: theme)
        timestampView = status
        if content.displayPartStatus || content.displayTimestampTemporarily || content.isSenderLinePinned { stack.addArrangedSubview(status) }
    }
    private func pinHorizontal(_ view: UIView) {
        let rtl = view.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.leftAnchor.constraint(equalTo: parent.leftAnchor, constant: 0).isActive = true
        }
        func right() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.rightAnchor.constraint(equalTo: parent.rightAnchor, constant: -CGFloat.zero).isActive = true
        }
        if rtl { right(); left() }
        else { left(); right() }
    }
}
