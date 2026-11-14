import UIKit

extension UIStackView {
    @objc func removeAllArrangedSubviews() {
        let views = arrangedSubviews
        for view in views {
            removeArrangedSubview(view)
            view.removeFromSuperview()
        }
    }
}

@objc(_TtC18IntercomSDKPrivate16FooterNoticeView)
class FooterNoticeView: UIView {
    enum Style: Hashable { case expanded, compact }
    var onTap: (() -> Void)?
    var style: Style = .expanded {
        didSet { if oldValue != style { updateStyle() } }
    }
    private var avatars: AvatarDetails?
    private var title: String? {
        didSet {
            titleLabel.text = title
            titleLabel.isHidden = title == nil
        }
    }
    private var body: String?
    private let themeProvider: ThemeProvider = DefaultThemeProvider.shared
    private lazy var titleStack: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.spacing = 8
        return stack
    }()
    private lazy var innerStack: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .center
        stack.spacing = 4
        return stack
    }()
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.font = .preferredFont(forTextStyle: .footnote)
        label.textColor = themeProvider.theme.textMutedExtra
        label.numberOfLines = 1
        label.lineBreakMode = .byTruncatingTail
        return label
    }()
    private lazy var avatarStack: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.spacing = 8
        return stack
    }()
    private var wrapperView: UIView?
    private lazy var backgroundView: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.masksToBounds = true
        view.backgroundColor = DefaultThemeProvider.shared.theme.base
        view.alpha = 0
        view.applyElevationOneShadow(cornerRadius: style == .compact ? view.frame.height * 0.5 : 0)
        return view
    }()
    private lazy var bodyLabel: UILabel = {
        let label = UILabel()
        label.font = .preferredFont(forTextStyle: .footnote)
        label.lineBreakMode = .byWordWrapping
        label.numberOfLines = 2
        label.textColor = themeProvider.theme.textMutedExtra
        return label
    }()

    override init(frame: CGRect) {
        super.init(frame: .zero)
        setupView()
        setupAvatars()
        addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(performAction)))
        isAccessibilityElement = true
        accessibilityTraits = .button
        updateAccessibility()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    func configure(with model: FooterNoticeViewModel) {
        title = model.title
        body = model.body
        bodyLabel.text = body
        avatars = model.avatarDetails
        setupAvatars()
        updateAccessibility()
    }
    private func setupView() {
        addSubview(backgroundView)
        addSubview(innerStack)
        let inset = UIFontMetrics.default.scaledValue(for: 10)
        innerStack.translatesAutoresizingMaskIntoConstraints = false
        innerStack.topAnchor.constraint(equalTo: backgroundView.topAnchor, constant: inset).isActive = true
        innerStack.leftAnchor.constraint(equalTo: backgroundView.leftAnchor, constant: inset).isActive = true
        innerStack.bottomAnchor.constraint(equalTo: backgroundView.bottomAnchor, constant: -inset).isActive = true
        innerStack.rightAnchor.constraint(equalTo: backgroundView.rightAnchor, constant: -inset).isActive = true
        innerStack.addArrangedSubview(titleStack)
        innerStack.addArrangedSubview(bodyLabel)
        titleStack.addArrangedSubview(avatarStack)
        titleStack.addArrangedSubview(titleLabel)
        backgroundView.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        backgroundView.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        let width = backgroundView.widthAnchor.constraint(lessThanOrEqualTo: widthAnchor)
        width.priority = .defaultHigh
        width.isActive = true
        backgroundView.topAnchor.constraint(equalTo: topAnchor).isActive = true
        backgroundView.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
    }
    private func setupAvatars() {
        avatarStack.removeAllArrangedSubviews()
        if let avatars = avatars {
            let height = UIFontMetrics.default.scaledValue(for: 16)
            let view = AvatarDetailsView(avatarDetails: avatars, avatarHeight: height, overlap: 4, activeIndicatorSize: 0)
            avatarStack.addArrangedSubview(view)
            avatarStack.isHidden = false
        } else {
            avatarStack.isHidden = true
        }
    }
    private func updateStyle() {
        updateAccessibility()
        switch style {
        case .compact:
            bodyLabel.removeFromSuperview()
            bodyLabel.alpha = 0
            backgroundView.alpha = 1
            setBorderVisibility(true)
        case .expanded:
            innerStack.addArrangedSubview(bodyLabel)
            bodyLabel.alpha = 1
            backgroundView.alpha = 0
            backgroundView.layer.borderWidth = 0
            backgroundView.layer.borderColor = nil
        }
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        let layer = backgroundView.layer
        let radius = style == .compact ? backgroundView.frame.height * 0.5 : 0
        layer.cornerRadius = radius
    }
    override var intrinsicContentSize: CGSize {
        let size = innerStack.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize)
        let widthInset = UIFontMetrics.default.scaledValue(for: 10)
        let heightInset = UIFontMetrics.default.scaledValue(for: 10)
        return CGSize(width: size.width + 2 * widthInset, height: size.height + 2 * heightInset)
    }
    private func updateAccessibility() {
        var values: [String] = []
        if let title = title { values.append(title) }
        if style == .expanded, let body = body { values.append(body) }
        accessibilityLabel = values.joined(separator: ", ")
    }
    @objc private func performAction() {
        let action = onTap
        action?()
    }
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        let previous = previousTraitCollection?.userInterfaceStyle
        let current = traitCollection.userInterfaceStyle
        if previous == nil || previous != current { setBorderVisibility(style == .compact) }
    }
    private func setBorderVisibility(_ visible: Bool) {
        backgroundView.layer.borderWidth = visible ? 1 : 0
        backgroundView.layer.borderColor = visible ? themeProvider.theme.neutralBorder.cgColor : nil
    }
}
