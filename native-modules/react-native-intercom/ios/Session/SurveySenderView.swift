import UIKit

@objc(_TtC18IntercomSDKPrivate16SurveySenderView)
class SurveySenderView: UIView {
    private let stackView = UIStackView()
    private let nameLabel = UILabel()
    private var avatarURL: URL?
    private let name: String
    private let config: any ICMConfigProtocol
    private let textColor: UIColor

    init(avatarURL: URL?, name: String, config: any ICMConfigProtocol, textColor: UIColor) {
        self.avatarURL = avatarURL
        self.name = name
        self.config = config
        self.textColor = textColor
        super.init(frame: .zero)
        setupStackView()
        setupAvatarView()
        setupNameLabel()
        setupAccessibility()
    }

    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private func setupStackView() {
        addSubview(stackView)
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.spacing = 9
        stackView.alignment = .center
        let padding: CGFloat
        if #available(iOS 26, *) { padding = 24 } else { padding = 12 }
        let constraints0 = [
            stackView.topAnchor.constraint(equalTo: topAnchor, constant: padding),
            stackView.leadingAnchor.constraint(equalTo: safeAreaLayoutGuide.leadingAnchor, constant: 15),
            stackView.trailingAnchor.constraint(equalTo: safeAreaLayoutGuide.trailingAnchor, constant: -54),
            stackView.bottomAnchor.constraint(equalTo: safeAreaLayoutGuide.bottomAnchor, constant: -padding)
        ]
        for constraint in constraints0 { constraint.isActive = true }
    }

    private func setupAvatarView() {
        let avatar = ICMAvatarView(noTeammatePresenceWithAvatarDiameter: 32)!
        avatar.translatesAutoresizingMaskIntoConstraints = false
        avatar.loadAvatar(with: avatarURL, userName: name)
        stackView.addArrangedSubview(avatar)
        avatar.widthAnchor.constraint(equalToConstant: 32).isActive = true
        avatar.heightAnchor.constraint(equalToConstant: 32).isActive = true
    }

    private func setupNameLabel() {
        let text = TranslationManager.messenger.translation(forKey: "teammate_from_company",
            params: ["name": name, "company": config.appName ?? ""])
        let font = UIFont.preferredFont(forTextStyle: .subheadline)
        let attributed = NSMutableAttributedString(string: text, attributes: [.font: font])
        if let range = text.range(of: name) {
            let bold = font.fontDescriptor.withSymbolicTraits(.traitBold).map { UIFont(descriptor: $0, size: 0) } ?? font
            attributed.addAttribute(.font, value: bold, range: NSRange(range, in: text))
        }
        nameLabel.attributedText = attributed
        nameLabel.adjustsFontForContentSizeCategory = true
        nameLabel.textColor = textColor
        stackView.addArrangedSubview(nameLabel)
    }

    private func setupAccessibility() {
        isAccessibilityElement = true
        if let company = config.appName {
            accessibilityLabel = TranslationManager.messenger.translation(forKey: "surveys_from_teammate",
                params: ["sender_name": name, "company": company])
        } else {
            accessibilityLabel = TranslationManager.messenger.translation(forKey: "surveys_from_company", params: ["company": name])
        }
        accessibilityTraits = .staticText
    }
}
