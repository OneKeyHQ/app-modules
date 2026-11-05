import UIKit

@objc(_TtC18IntercomSDKPrivate19TeammateAvatarsView)
class TeammateAvatarsView: UIView {
    private let avatarViewAccessibilityIdentifier = "available team view"
    private let themeProvider: any ThemeProvider
    private lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.backgroundColor = themeProvider.theme.neutralContainerAlt
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    init(removeHorizontalPadding: Bool, themeProvider: any ThemeProvider) {
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        addSubview(containerStackView)
        containerStackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: removeHorizontalPadding ? 0 : 8).isActive = true
        containerStackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: removeHorizontalPadding ? 0 : -8).isActive = true
        containerStackView.topAnchor.constraint(equalTo: topAnchor).isActive = true
        containerStackView.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        createTeammateAvatarView()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func createTeammateAvatarView() {
        addDividerLine()
        let spacer = UIView()
        spacer.translatesAutoresizingMaskIntoConstraints = false
        spacer.heightAnchor.constraint(equalToConstant: 12).isActive = true
        containerStackView.addArrangedSubview(spacer)
        addAvatarView()
        let bottom = UIView()
        bottom.translatesAutoresizingMaskIntoConstraints = false
        bottom.heightAnchor.constraint(equalToConstant: 4).isActive = true
        containerStackView.addArrangedSubview(bottom)
    }
    private func addDividerLine() {
        let divider = UIView()
        divider.translatesAutoresizingMaskIntoConstraints = false
        divider.backgroundColor = themeProvider.theme.neutralBorder
        containerStackView.addArrangedSubview(divider)
        divider.heightAnchor.constraint(equalToConstant: 1).isActive = true
    }
    private func addAvatarView() {
        let avatars = AvailableTeammatesView(avatarConfig: ICMConfig.shared(), themeProvider: DefaultThemeProvider.shared)
        avatars.accessibilityIdentifier = avatarViewAccessibilityIdentifier
        avatars.translatesAutoresizingMaskIntoConstraints = false
        avatars.setContentHuggingPriority(.required, for: .vertical)
        containerStackView.addArrangedSubview(avatars)
    }
}
