import UIKit

@objc(_TtC18IntercomSDKPrivate22AvailableTeammatesView)
class AvailableTeammatesView: UIStackView {
    private let themeProvider: DefaultThemeProvider
    private let avatarConfig: any ICMConfigProtocol
    private static let translationManager = TranslationManager.helpCenter
    init(avatarConfig: any ICMConfigProtocol, themeProvider: DefaultThemeProvider) {
        self.themeProvider = themeProvider
        self.avatarConfig = avatarConfig
        super.init(frame: .zero)
        configureTeammatesView(false)
    }
    required init(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    func configureTeammatesView(_ avatarsFirst: Bool) {
        let accessEnabled = avatarConfig.accessToTeammateEnabled
        alignment = .center
        spacing = 8
        if ICMUtils.contentSizeCategoryIsAccessibilityCategory() { axis = .vertical }
        if avatarsFirst && accessEnabled { addAvatars() }
        let label = makeTeammateLabel()
        addArrangedSubview(label)
        label.setContentCompressionResistancePriority(.init(999), for: .horizontal)
        if !avatarsFirst && accessEnabled { addAvatars() }
        accessibilityElements = arrangedSubviews
        addStackViewSpacerViews()
    }
    private func makeTeammateLabel() -> UILabel {
        let label = UILabel()
        let enabled = avatarConfig.accessToTeammateEnabled
        let key = enabled ? "the_team_can_help_if_needed" : "our_bot_answers_instantly"
        label.textAlignment = enabled ? .natural : .center
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.translatesAutoresizingMaskIntoConstraints = false
        label.adjustsFontForContentSizeCategory = true
        label.textColor = themeProvider.theme.textMuted
        label.text = Self.translationManager.translation(forKey: key)
        label.accessibilityLabel = label.text
        label.lineBreakMode = .byWordWrapping
        label.numberOfLines = 0
        return label
    }
    private func addStackViewSpacerViews() {
        let first = UIView()
        first.translatesAutoresizingMaskIntoConstraints = false
        let second = UIView()
        second.translatesAutoresizingMaskIntoConstraints = false
        insertArrangedSubview(first, at: 0)
        addArrangedSubview(second)
        first.widthAnchor.constraint(equalTo: second.widthAnchor).isActive = true
        let zeroWidth = first.widthAnchor.constraint(equalToConstant: 0)
        zeroWidth.priority = .init(998)
        zeroWidth.isActive = true
    }
    private func addAvatars() {
        let height: CGFloat = ICMUtils.contentSizeCategoryIsAccessibilityCategory() ? 48 : 24
        let overlap: CGFloat = ICMUtils.contentSizeCategoryIsAccessibilityCategory() ? 3.84 : 1.92
        let avatars = ICMActiveAdminsAvatarView(height: height, overlap: overlap, maxCount: 3)!
        avatars.updateWhenTeamPresenceUpdated = true
        avatars.translatesAutoresizingMaskIntoConstraints = false
        let wrapper = UIView()
        wrapper.translatesAutoresizingMaskIntoConstraints = false
        wrapper.addSubview(avatars)
        NSLayoutConstraint.activate([
            avatars.leadingAnchor.constraint(equalTo: wrapper.leadingAnchor), avatars.trailingAnchor.constraint(equalTo: wrapper.trailingAnchor),
            avatars.topAnchor.constraint(equalTo: wrapper.topAnchor), avatars.bottomAnchor.constraint(equalTo: wrapper.bottomAnchor)
        ])
        addArrangedSubview(wrapper)
        avatars.setContentHuggingPriority(.required, for: .horizontal)
        avatars.setContentCompressionResistancePriority(.required, for: .horizontal)
    }
}
