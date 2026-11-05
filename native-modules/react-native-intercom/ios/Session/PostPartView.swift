import UIKit

@objc(_TtC18IntercomSDKPrivate12PostPartView)
class PostPartView: NotePartView {
    private lazy var gradientView: ICMGradientView = {
        let view = ICMGradientView()
        let layer = view.layer!
        var colors: [CGColor] = []
        for alpha: CGFloat in [0, 0.1, 0.2, 0.4, 0.7] { colors.append(themeProvider.theme.base.withAlphaComponent(alpha).cgColor) }
        layer.colors = colors
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private let themeProvider: ThemeProvider
    private lazy var openButton: UIButton = {
        let button = UIButton()
        button.accessibilityIdentifier = "intercom post card open button"
        button.setTitle(TranslationManager.messenger.translation(forKey: "view_post"), for: .normal)
        button.setTitleColor(themeProvider.theme.primaryForeground, for: .normal)
        if let label = button.titleLabel {
            label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .subheadline).pointSize, weight: .bold)
        }
        button.titleLabel?.adjustsFontForContentSizeCategory = true
        button.backgroundColor = themeProvider.theme.base
        button.layer.cornerRadius = 8
        button.addTarget(self, action: #selector(openButtonPressed), for: .touchUpInside)
        button.translatesAutoresizingMaskIntoConstraints = false
        return button
    }()
    private lazy var divider: UIView = {
        let view = UIView()
        view.backgroundColor = themeProvider.theme.neutralContainerEmphasis
        view.translatesAutoresizingMaskIntoConstraints = false
        view.heightAnchor.constraint(equalToConstant: 1).isActive = true
        return view
    }()
    override init(content: ICMConversationPart, themeProvider: ThemeProvider, config: ICMConfigProtocol) {
        self.themeProvider = themeProvider
        super.init(content: content, themeProvider: themeProvider, config: config)
    }
    // Preserve inherited Objective-C trap ownership without an additional coder selector.
    @nonobjc required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    override func setUpView() {
        super.setUpView()
        addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(cardTapped)))
    }
    override func addSubviews() {
        addOpenButton()
        addDivider()
        addGradientView()
        blocksMaxHeight = 108
        blocksContainerViewBottomAnchor = divider.topAnchor
        super.addSubviews()
    }
    private func addOpenButton() {
        addSubview(openButton)
        NSLayoutConstraint.activate([
            openButton.widthAnchor.constraint(equalTo: widthAnchor),
            openButton.centerXAnchor.constraint(equalTo: centerXAnchor),
            openButton.heightAnchor.constraint(equalToConstant: 50),
            openButton.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
    }
    private func addDivider() {
        addSubview(divider)
        NSLayoutConstraint.activate([
            divider.centerXAnchor.constraint(equalTo: centerXAnchor),
            divider.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 12),
            divider.bottomAnchor.constraint(equalTo: openButton.topAnchor)
        ])
    }
    private func addGradientView() {
        addSubview(gradientView)
        NSLayoutConstraint.activate([
            gradientView.leadingAnchor.constraint(equalTo: leadingAnchor),
            gradientView.trailingAnchor.constraint(equalTo: trailingAnchor),
            gradientView.heightAnchor.constraint(equalToConstant: 32),
            gradientView.bottomAnchor.constraint(equalTo: divider.topAnchor)
        ])
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        gradientView.isHidden = blocksStackView.frame.height <= 108
        bringSubviewToFront(gradientView)
    }
    @objc private func openButtonPressed() { delegate?.openPost() }
    @objc private func cardTapped() { delegate?.openPost() }
}
