import UIKit

@objc(_TtC18IntercomSDKPrivate17LinkListBlockView)
class LinkListBlockView: UIView, ConversationCellContentView {
    let content: ITBLinkListBlock
    private let themeProvider: any ThemeProvider = DefaultThemeProvider.shared
    private weak var delegate: (any ConversationCellBlockDelegate)?
    private var separatorHeightConstraints: [NSLayoutConstraint] = []
    private lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .leading
        stack.distribution = .fill
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private var separatorView: UIView?

    init?(content: ITBBlock, delegate: (any ConversationCellBlockDelegate)?) {
        guard let content = content as? ITBLinkListBlock else { return nil }
        self.content = content
        self.delegate = delegate
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        configure()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func configure() {
        addSubview(containerStackView)
        NSLayoutConstraint.activate([
            containerStackView.leadingAnchor.constraint(equalTo: leadingAnchor),
            containerStackView.bottomAnchor.constraint(equalTo: bottomAnchor),
            containerStackView.topAnchor.constraint(equalTo: topAnchor),
            containerStackView.trailingAnchor.constraint(equalTo: trailingAnchor),
        ])
        configureLinkViews()
    }
    private func configureLinkViews() {
        let links = content.links!
        for (index, link) in links.enumerated() {
            let lastIndex = content.links!.count - 1
            let view = LinkListItemView(link: link, block: content, icon: nil, delegate: delegate)
            containerStackView.addArrangedSubview(view)
            if index != lastIndex { configureSeparatorView(with: content) }
        }
        if content.footerLink != nil {
            configureSeparatorView(with: content)
            let footer = content.footerLink!
            let view = LinkListItemView(link: footer, block: content, icon: content.footerLinkIcon, delegate: delegate)
            containerStackView.addArrangedSubview(view)
        }
    }
    private func configureSeparatorView(with block: ITBLinkListBlock) {
        let separator = UIView()
        separator.translatesAutoresizingMaskIntoConstraints = false
        separator.backgroundColor = block.separatorColor
        containerStackView.addArrangedSubview(separator)
        let height = separator.heightAnchor.constraint(equalToConstant: 1 / icmDisplayScale)
        separatorHeightConstraints.append(height)
        NSLayoutConstraint.activate([height, separator.widthAnchor.constraint(equalTo: containerStackView.widthAnchor)])
    }
    override func didMoveToWindow() {
        super.didMoveToWindow()
        if traitCollection.displayScale > 0 { updateSeparatorHeights() }
    }
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        guard traitCollection.displayScale > 0 else { return }
        let previous = previousTraitCollection?.displayScale ?? 0
        let current = traitCollection.displayScale
        if previousTraitCollection == nil || previous != current { updateSeparatorHeights() }
    }
    private func updateSeparatorHeights() {
        let scale = icmDisplayScale
        let constraints = separatorHeightConstraints
        for constraint in constraints {
            let height = 1 / scale
            if constraint.constant != height { constraint.constant = height }
        }
    }
}

@objc(_TtC18IntercomSDKPrivate16LinkListItemView)
class LinkListItemView: UIView {
    private let link: ITBLink
    private let block: ITBLinkListBlock
    private let icon: UIImage?
    private weak var delegate: (any ConversationCellBlockDelegate)?
    private lazy var itemTapGesture = UITapGestureRecognizer(target: self, action: #selector(itemTapped))
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.text = link.title ?? ""
        label.font = UIFontMetrics(forTextStyle: .callout).scaledFont(for: block.linkFont!)
        label.textColor = block.linkTextColor
        label.numberOfLines = 2
        label.translatesAutoresizingMaskIntoConstraints = false
        label.accessibilityTraits = .link
        return label
    }()
    private lazy var iconImageView: UIImageView = {
        let view = UIImageView()
        view.image = icon
        view.tintColor = block.linkTextColor
        view.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            view.heightAnchor.constraint(equalToConstant: block.linkFooterIconSize.height),
            view.widthAnchor.constraint(equalToConstant: block.linkFooterIconSize.width),
        ])
        return view
    }()
    init(link: ITBLink, block: ITBLinkListBlock, icon: UIImage?, delegate: (any ConversationCellBlockDelegate)?) {
        self.link = link
        self.block = block
        self.icon = icon
        self.delegate = delegate
        super.init(frame: .zero)
        configureLayout()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func configureLayout() {
        addGestureRecognizer(itemTapGesture)
        if icon != nil {
            addSubview(iconImageView)
            NSLayoutConstraint.activate([
                iconImageView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: block.textInsets.left),
                iconImageView.centerYAnchor.constraint(equalTo: centerYAnchor),
            ])
        }
        addSubview(titleLabel)
        let leading: NSLayoutConstraint
        if icon != nil { leading = titleLabel.leadingAnchor.constraint(equalTo: iconImageView.trailingAnchor, constant: 7) }
        else { leading = titleLabel.leadingAnchor.constraint(equalTo: leadingAnchor, constant: block.textInsets.left) }
        NSLayoutConstraint.activate([
            leading,
            titleLabel.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -block.textInsets.right),
            titleLabel.topAnchor.constraint(equalTo: topAnchor),
            titleLabel.bottomAnchor.constraint(equalTo: bottomAnchor),
        ])
        let height = titleLabel.heightAnchor.constraint(greaterThanOrEqualToConstant: block.defaultLinkHeight)
        height.priority = UILayoutPriority(rawValue: 999)
        height.isActive = true
    }
    @objc private func itemTapped() {
        if let id = link.linkId { delegate?.openArticle(articleId: id) }
        else { delegate?.openHelpCenter() }
    }
}
