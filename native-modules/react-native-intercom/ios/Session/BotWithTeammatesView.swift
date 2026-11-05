import UIKit

@objc(_TtC18IntercomSDKPrivate20BotWithTeammatesView)
class BotWithTeammatesView: UIView {
    private let themeProvider: ThemeProvider
    private lazy var mainStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .center
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.spacing = spacing
        return stack
    }()
    private var leadingTeammateAvatarView: ICMAvatarView?
    private var trailingTeammateAvatarView: ICMAvatarView?
    private var botAvatarView: ICMAvatarView?
    private let botSize: CGFloat
    private lazy var teammateAvatarSize = botSize * 0.75
    private lazy var spacing = botSize * -0.125
    private lazy var borderSize = botSize * 0.0625
    private lazy var cutOutWidth = borderSize - spacing

    init(avatars: [Avatar], botSize: CGFloat, themeProvider: ThemeProvider) {
        self.botSize = botSize
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        ICMUtils.enableRTLSupport(forContainerClass: type(of: self))
        setViewsFor(avatars)
        addSubviews()
        setUpAccessibility()
    }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    private func setViewsFor(_ avatars: [Avatar]) {
        if avatars.isEmpty { return }
        let rtl = effectiveUserInterfaceLayoutDirection == .rightToLeft
        if avatars.count >= 3 {
            leadingTeammateAvatarView = makeAvatar(avatars[rtl ? 2 : 1], size: teammateAvatarSize, shouldAddBackground: true)
            if let leading = leadingTeammateAvatarView { cutOut(leading, leadingEdge: rtl) }
            trailingTeammateAvatarView = makeAvatar(avatars[rtl ? 1 : 2], size: teammateAvatarSize, shouldAddBackground: true)
            if let trailing = trailingTeammateAvatarView { cutOut(trailing, leadingEdge: !rtl) }
        } else if avatars.count == 2 {
            trailingTeammateAvatarView = makeAvatar(avatars[1], size: teammateAvatarSize, shouldAddBackground: true)
            if let trailing = trailingTeammateAvatarView { cutOut(trailing, leadingEdge: !rtl) }
        }
        botAvatarView = makeAvatar(avatars[0], size: botSize, shouldAddBackground: false)
        if avatars.count >= 2 { addShadowTo(botAvatarView) }
    }
    private func makeAvatar(_ avatar: Avatar, size: CGFloat, shouldAddBackground: Bool) -> ICMAvatarView? {
        let view = ICMAvatarView(largeTeammatePresenceWithAvatarDiameter: size)
        view?.setUserPresenceViewIsHidden(true)
        view?.translatesAutoresizingMaskIntoConstraints = false
        if let view = view {
            NSLayoutConstraint.activate([view.widthAnchor.constraint(equalToConstant: size), view.heightAnchor.constraint(equalTo: view.widthAnchor)])
        }
        view?.load(avatar, useActionColorAsBackgroundColor: true, shouldAddBackground: shouldAddBackground)
        return view
    }
    private func cutOut(_ view: UIView, leadingEdge: Bool) {
        let width = cutOutWidth
        let height = botSize
        let frameHeight = view.frame.height
        let boundsWidth = view.bounds.width
        let shape = CAShapeLayer()
        shape.path = UIBezierPath(rect: CGRect(x: leadingEdge ? 0 : boundsWidth - width, y: 0, width: width, height: height)).cgPath
        let mask = CALayer()
        mask.frame = CGRect(x: 0, y: 0, width: frameHeight, height: frameHeight)
        mask.backgroundColor = UIColor.black.cgColor
        mask.addSublayer(shape)
        mask.masksToBounds = true
        shape.compositingFilter = "xor"
        view.layer.mask = mask
    }
    private func addSubviews() {
        for view in [leadingTeammateAvatarView, botAvatarView, trailingTeammateAvatarView].compactMap({ $0 }) { mainStackView.addArrangedSubview(view) }
        addSubview(mainStackView)
        NSLayoutConstraint.activate([
            mainStackView.leadingAnchor.constraint(equalTo: leadingAnchor),
            mainStackView.trailingAnchor.constraint(equalTo: trailingAnchor),
            mainStackView.topAnchor.constraint(equalTo: topAnchor),
            mainStackView.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
    }
    private func setUpAccessibility() {
        isAccessibilityElement = true
        let label = botAvatarView?.accessibilityLabel
        let bot = TranslationManager.messenger.translation(forKey: "bot")
        accessibilityLabel = [label, bot].compactMap { $0 }.joined(separator: ";")
    }
    private func addShadowTo(_ view: ICMAvatarView?) {
        guard let view = view else { return }
        view.layer.borderWidth = 1
        view.layer.borderColor = themeProvider.theme.neutralBorder.cgColor
        let height = view.layer.frame.height
        view.applyElevationOneShadow(cornerRadius: height * 0.15625)
    }
    override var intrinsicContentSize: CGSize {
        var width = botSize
        if leadingTeammateAvatarView != nil { width += teammateAvatarSize - spacing }
        if trailingTeammateAvatarView != nil { width += teammateAvatarSize - spacing }
        return CGSize(width: width, height: botSize)
    }
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        let previous = previousTraitCollection?.userInterfaceStyle
        let current = traitCollection.userInterfaceStyle
        if previous == nil || previous != current { botAvatarView?.layer.borderColor = themeProvider.theme.neutralBorder.cgColor }
    }
}
