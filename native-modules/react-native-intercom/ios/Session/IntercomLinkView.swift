import UIKit

@objc(_TtC18IntercomSDKPrivate16IntercomLinkView)
class IntercomLinkView: UIView {
    var onTap: (() -> Void)?
    let themeProvider: any ThemeProvider
    var url: URL?
    var logoImage: PoweredByIcon?
    var labelText: String?
    private lazy var stackView: UIStackView = {
        let stack = UIStackView()
        stack.clipsToBounds = true
        stack.axis = .horizontal
        stack.alignment = .center
        stack.spacing = 6
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var logoImageView: UIImageView = {
        let view = UIImageView()
        view.tintColor = themeProvider.theme.textMuted
        view.accessibilityTraits = .image
        view.translatesAutoresizingMaskIntoConstraints = false
        view.widthAnchor.constraint(equalToConstant: 16).isActive = true
        view.heightAnchor.constraint(equalToConstant: 16).isActive = true
        return view
    }()
    private lazy var linkLabel: UILabel = {
        let label = UILabel()
        label.textColor = themeProvider.theme.textMuted
        label.font = .preferredFont(forTextStyle: .callout)
        label.accessibilityTraits = .link
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()

    init(themeProvider: any ThemeProvider) {
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        setupView()
        addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(intercomLinkTapped)))
    }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    private func setupView() {
        accessibilityIdentifier = "intercom link view"
        clipsToBounds = true
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(stackView)
        stackView.addArrangedSubview(logoImageView)
        stackView.addArrangedSubview(linkLabel)
        stackView.topAnchor.constraint(lessThanOrEqualTo: stackView.superview!.topAnchor, constant: 8).isActive = true
        stackView.bottomAnchor.constraint(lessThanOrEqualTo: stackView.superview!.bottomAnchor, constant: -8).isActive = true
        if stackView.effectiveUserInterfaceLayoutDirection == .leftToRight {
            stackView.leftAnchor.constraint(equalTo: stackView.superview!.leftAnchor, constant: 12).isActive = true
            stackView.rightAnchor.constraint(equalTo: stackView.superview!.rightAnchor, constant: -12).isActive = true
        } else {
            stackView.rightAnchor.constraint(equalTo: stackView.superview!.rightAnchor, constant: -12).isActive = true
            stackView.leftAnchor.constraint(equalTo: stackView.superview!.leftAnchor, constant: 12).isActive = true
        }
        ICMUtils.disableDynamicFont(for: [self])
    }

    override var intrinsicContentSize: CGSize {
        let size = stackView.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize)
        return CGSize(width: size.width + 24, height: size.height + 16)
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        let currentLayer = layer
        let height = frame.size.height
        currentLayer.cornerRadius = height * 0.5
    }

    func updateDetails(with poweredBy: PoweredBy) {
        labelText = poweredBy.text
        logoImage = poweredBy.icon
        url = poweredBy.linkURL
        linkLabel.text = labelText
        let imageView = logoImageView
        let icon = logoImage
        switch icon {
        case .none, .some(.intercom): imageView.image = ICMAssets.intercomLogoImage()
        case .some(.fin): imageView.image = ICMAssets.poweredByFinAIAgentIcon()
        }
    }

    @objc private func intercomLinkTapped() {
        guard let target = url else { return }
        onTap?()
        if UIApplication.shared.canOpenURL(target) {
            UIApplication.shared.open(target, options: [:], completionHandler: nil)
        }
    }
}
