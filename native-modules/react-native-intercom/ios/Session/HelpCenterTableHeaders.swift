import UIKit

@objc(_TtC18IntercomSDKPrivate25HelpCenterTableHeaderView)
class HelpCenterTableHeaderView: UIView {
    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = DefaultThemeProvider.shared.theme.base
        layer.borderWidth = 1
        layer.borderColor = DefaultThemeProvider.shared.theme.neutralBorder.cgColor
        preservesSuperviewLayoutMargins = true
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
}

@objc(_TtC18IntercomSDKPrivate38HelpCenterArticleListSectionHeaderView)
class HelpCenterArticleListSectionHeaderView: UIView {
    lazy var sectionTitleLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines = 3
        label.lineBreakMode = .byTruncatingTail
        label.textColor = DefaultThemeProvider.shared.theme.textDefault
        label.font = .preferredFont(forTextStyle: .headline)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = DefaultThemeProvider.shared.theme.base
        preservesSuperviewLayoutMargins = true
        addSubview(sectionTitleLabel)
        NSLayoutConstraint.activate([
            sectionTitleLabel.leadingAnchor.constraint(equalTo: layoutMarginsGuide.leadingAnchor), sectionTitleLabel.topAnchor.constraint(equalTo: layoutMarginsGuide.topAnchor),
            sectionTitleLabel.trailingAnchor.constraint(equalTo: layoutMarginsGuide.trailingAnchor), sectionTitleLabel.bottomAnchor.constraint(equalTo: layoutMarginsGuide.bottomAnchor)
        ])
        isAccessibilityElement = true
        accessibilityTraits = .header
        accessibilityIdentifier = "intercom article list section header view"
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
}

@objc(_TtC18IntercomSDKPrivate34HelpCenterArticleAuthorAvatarsView)
class HelpCenterArticleAuthorAvatarsView: ICMMultipleAvatarsView {
    override init(frame: CGRect) { super.init(frame: frame) }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(avatarViewModels: [ICMAvatarViewModel]!, height: CGFloat, overlap: CGFloat, maxCount: Int) {
        fatalError("init(avatarViewModels:height:overlap:maxCount:) has not been implemented")
    }
    init?(authors: [InternalHelpCenterArticleAuthor], height: CGFloat, overlap: CGFloat, maxCount: Int) {
        let models = authors.compactMap { author -> ICMAvatarViewModel? in
            guard let avatar = author.avatar else { return nil }
            return ICMAvatarViewModel(avatar: avatar, activeIndicatorSize: 0)
        }
        super.init(avatarViewModels: models, height: height, overlap: overlap, maxCount: maxCount)
    }
}
