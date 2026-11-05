import UIKit

@objc(_TtC18IntercomSDKPrivate31CollapsedConversationHeaderView)
class CollapsedConversationHeaderView: UIView {
    private let themeProvider: ThemeProvider
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.textAlignment = .center
        label.numberOfLines = 1
        label.translatesAutoresizingMaskIntoConstraints = false
        let compact = traitCollection.verticalSizeClass == .compact
        label.font = .systemFont(ofSize: compact ? 13 : 16, weight: .semibold)
        return label
    }()
    private lazy var subtitleLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines = 1
        label.textColor = themeProvider.theme.textMuted
        label.translatesAutoresizingMaskIntoConstraints = false
        _ = traitCollection.verticalSizeClass
        label.font = .systemFont(ofSize: 12)
        return label
    }()
    private lazy var subtitleImageView: UIImageView = {
        let image = UIImageView()
        image.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            image.widthAnchor.constraint(equalToConstant: 12),
            image.heightAnchor.constraint(equalTo: image.widthAnchor)
        ])
        return image
    }()
    private lazy var subtitlesStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.spacing = 4
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.addArrangedSubviews([subtitleImageView, subtitleLabel])
        return stack
    }()
    private lazy var labelsStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .leading
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.spacing = traitCollection.verticalSizeClass == .compact ? 2 : 5
        stack.addArrangedSubviews([titleLabel, subtitlesStackView])
        return stack
    }()
    private lazy var mainStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.spacing = 8
        stack.alignment = .center
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    var conversationHeader: ConversationHeader? {
        didSet {
            setTitle()
            setSubtitle()
            reloadArrangedSubviews()
        }
    }
    private var avatarHeight: CGFloat = 32

    init(_ frame: CGRect, header: ConversationHeader?, themeProvider: ThemeProvider) {
        self.themeProvider = themeProvider
        conversationHeader = header
        super.init(frame: frame)
        addSubviews()
        setTitle()
        setSubtitle()
        reloadArrangedSubviews()
    }

    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    override var intrinsicContentSize: CGSize { UIView.layoutFittingExpandedSize }

    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        let previousSizeClass = previousTraitCollection?.verticalSizeClass
        let currentSizeClass = traitCollection.verticalSizeClass
        if previousSizeClass == nil || previousSizeClass != currentSizeClass {
            updateLayout(for: traitCollection.verticalSizeClass)
        }
    }

    func updateLayout(for sizeClass: UIUserInterfaceSizeClass) {
        let label = titleLabel
        let compact = sizeClass == .compact
        label.font = .systemFont(ofSize: compact ? 13 : 16, weight: .semibold)
        subtitleLabel.font = .systemFont(ofSize: 12)
        labelsStackView.spacing = compact ? 2 : 5
        avatarHeight = compact ? 24 : 32
        reloadArrangedSubviews()
    }

    private func addSubviews() {
        addSubview(mainStackView)
        mainStackView.translatesAutoresizingMaskIntoConstraints = false
        mainStackView.translatesAutoresizingMaskIntoConstraints = false
        mainStackView.topAnchor.constraint(equalTo: mainStackView.superview!.topAnchor, constant: 0).isActive = true
        let direction = mainStackView.effectiveUserInterfaceLayoutDirection
        mainStackView.translatesAutoresizingMaskIntoConstraints = false
        mainStackView.translatesAutoresizingMaskIntoConstraints = false
        if direction == .leftToRight {
            mainStackView.rightAnchor.constraint(equalTo: mainStackView.superview!.rightAnchor, constant: -0.0).isActive = true
        } else {
            mainStackView.leftAnchor.constraint(equalTo: mainStackView.superview!.leftAnchor, constant: 0).isActive = true
        }
        mainStackView.translatesAutoresizingMaskIntoConstraints = false
        mainStackView.translatesAutoresizingMaskIntoConstraints = false
        mainStackView.bottomAnchor.constraint(equalTo: mainStackView.superview!.bottomAnchor, constant: -0.0).isActive = true
        mainStackView.translatesAutoresizingMaskIntoConstraints = false
        let leading = mainStackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 0)
        leading.priority = UILayoutPriority(999)
        leading.isActive = true
    }

    private func setTitle() {
        guard let header = conversationHeader else { return }
        titleLabel.text = header.collapsed.title
        titleLabel.textColor = themeProvider.theme.textDefault
    }

    private func setSubtitle() {
        guard let header = conversationHeader else { return }
        subtitleLabel.text = header.collapsed.subtitle
        subtitleLabel.textColor = themeProvider.theme.textMuted
        subtitleLabel.isHidden = header.collapsed.subtitle == nil
        subtitleImageView.image = header.collapsed.icon.image()
        subtitleImageView.isHidden = header.collapsed.icon.stringValue == nil
        subtitleImageView.tintColor = themeProvider.theme.iconMuted
        subtitlesStackView.isHidden = subtitleLabel.isHidden && subtitleImageView.isHidden
    }

    private func reloadArrangedSubviews() {
        mainStackView.removeAllArrangedSubviews()
        let stack = mainStackView
        stack.addArrangedSubviews([avatarView, labelsStackView].compactMap { $0 })
    }

    private var avatarView: UIView? {
        guard let header = conversationHeader, let details = header.collapsed.avatarDetails else { return nil }
        let indicator = header.displayActiveIndicator
        let height = avatarHeight
        let view = AvatarDetailsView(avatarDetails: details, avatarHeight: height, overlap: height * 0.08,
                                     activeIndicatorSize: indicator ? 1 : 0)
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }
}
