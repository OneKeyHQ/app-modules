import UIKit

@objc(_TtC18IntercomSDKPrivate17AvatarDetailsView)
class AvatarDetailsView: UIView {
    private let avatarDetails: AvatarDetails
    private let avatarHeight: CGFloat
    private let overlap: CGFloat
    private let activeIndicatorSize: Int
    private lazy var avatarView: UIView = {
        let view: UIView
        switch avatarDetails.avatarType {
        case .facepile:
            view = BotWithTeammatesView(avatars: avatarDetails.avatars, botSize: avatarHeight, themeProvider: DefaultThemeProvider.shared)
        case .layeredBubbles:
            let models = avatarDetails.avatars.compactMap { ICMAvatarViewModel(avatar: $0, activeIndicatorSize: activeIndicatorSize) }
            view = ICMMultipleAvatarsView(avatarViewModels: models, height: avatarHeight, overlap: overlap, maxCount: avatarDetails.avatars.count)
        case .logo:
            if avatarDetails.avatars.isEmpty {
                view = UIView()
            } else {
                let avatar = ICMAvatarView(noTeammatePresenceWithAvatarDiameter: avatarHeight)!
                avatar.translatesAutoresizingMaskIntoConstraints = false
                avatar.widthAnchor.constraint(equalToConstant: avatarHeight).isActive = true
                avatar.heightAnchor.constraint(equalToConstant: avatarHeight).isActive = true
                avatar.load(avatarDetails.avatars[0], useActionColorAsBackgroundColor: false, shouldAddBackground: false)
                view = avatar
            }
        }
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()

    init(avatarDetails: AvatarDetails, avatarHeight: CGFloat, overlap: CGFloat, activeIndicatorSize: Int) {
        self.avatarDetails = avatarDetails
        self.avatarHeight = avatarHeight
        self.overlap = overlap
        self.activeIndicatorSize = activeIndicatorSize
        super.init(frame: .zero)
        addSubview(avatarView)
        avatarView.topAnchor.constraint(equalTo: topAnchor).isActive = true
        avatarView.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
        avatarView.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        avatarView.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
    }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override var intrinsicContentSize: CGSize { avatarView.intrinsicContentSize }
}
