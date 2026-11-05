import UIKit

@objc(_TtC18IntercomSDKPrivate30ExpandedConversationHeaderView)
class ExpandedConversationHeaderView: UIView {
    private let themeProvider: ThemeProvider
    private lazy var mainStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.distribution = .fillProportionally
        stack.alignment = .center
        stack.spacing = 4
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private let maxWidth: CGFloat
    private var contentStackViews: [UIStackView] = []
    private let expandedHeader: ExpandedConversationHeader
    private let displayActiveIndicator: Bool
    private let config: ICMConfigProtocol

    init(_ frame: CGRect, expandedHeader: ExpandedConversationHeader, displayActiveIndicator: Bool,
         maxWidth: CGFloat, config: ICMConfigProtocol, themeProvider: ThemeProvider) {
        self.expandedHeader = expandedHeader
        self.displayActiveIndicator = displayActiveIndicator
        self.maxWidth = maxWidth
        self.config = config
        self.themeProvider = themeProvider
        super.init(frame: frame)
        addSubviews()
        setupAccessibility()
    }

    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    override func layoutSubviews() {
        super.layoutSubviews()
        if bounds.width > 0 { updateContentStackAxes(for: bounds.width) }
    }

    private func addSubviews() {
        addSubview(mainStackView)
        mainStackView.translatesAutoresizingMaskIntoConstraints = false
        mainStackView.translatesAutoresizingMaskIntoConstraints = false
        mainStackView.topAnchor.constraint(equalTo: mainStackView.superview!.topAnchor, constant: 8).isActive = true
        let direction = mainStackView.effectiveUserInterfaceLayoutDirection
        mainStackView.translatesAutoresizingMaskIntoConstraints = false
        mainStackView.translatesAutoresizingMaskIntoConstraints = false
        if direction == .leftToRight {
            mainStackView.leftAnchor.constraint(equalTo: mainStackView.superview!.leftAnchor, constant: 0).isActive = true
        } else {
            mainStackView.rightAnchor.constraint(equalTo: mainStackView.superview!.rightAnchor, constant: -0.0).isActive = true
        }
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
        let header = expandedHeader
        let bodyViews = header.body.map { viewFor(content: $0) }
        let footerViews = header.footer.map { viewFor(content: $0) }
        if let avatar = avatarView {
            mainStackView.addArrangedSubview(avatar)
            mainStackView.setCustomSpacing(16, after: avatar)
        }
        mainStackView.addArrangedSubviews(bodyViews + footerViews)
    }

    private func setupAccessibility() {
        isAccessibilityElement = true
        var label = ""
        let header = expandedHeader
        if let avatars = header.avatarDetails?.avatars {
            for avatar in avatars { label += (avatar.label ?? "") + ", " }
        }
        label += header.body.map(\.text).joined(separator: ", ")
        accessibilityLabel = label
    }

    private func updateContentStackAxes(for width: CGFloat) {
        for stack in contentStackViews {
            let views = stack.arrangedSubviews
            var requiredWidth: CGFloat = 4
            for view in views { requiredWidth += view.intrinsicContentSize.width }
            let axis: NSLayoutConstraint.Axis = width < requiredWidth ? .vertical : .horizontal
            if stack.axis != axis { stack.axis = axis }
        }
    }

    func viewFor(content: ConversationHeaderContent) -> UIStackView {
        let avatar = content.avatarDetails.map {
            AvatarDetailsView(avatarDetails: $0, avatarHeight: 24, overlap: 1.92, activeIndicatorSize: 0)
        }
        let label = labelFor(content: content)
        let avatarWidth = avatar?.intrinsicContentSize.width ?? 0
        let requiredWidth = (avatarWidth + label.intrinsicContentSize.width) + 4
        let stack = UIStackView()
        stack.alignment = .center
        stack.spacing = 4
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.axis = maxWidth < requiredWidth ? .vertical : .horizontal
        stack.addArrangedSubviews([avatar, label].compactMap { $0 })
        contentStackViews.append(stack)
        return stack
    }

    func labelFor(content: ConversationHeaderContent) -> UILabel {
        let label = UILabel()
        label.adjustsFontForContentSizeCategory = true
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textAlignment = .center
        label.numberOfLines = 0
        label.text = content.text
        label.setContentCompressionResistancePriority(.required, for: .vertical)
        switch content.style {
        case .h1:
            label.font = .preferredFont(forTextStyle: .headline)
        case .paragraph:
            label.font = .preferredFont(forTextStyle: .subheadline)
        case .quote:
            let font = UIFont.preferredFont(forTextStyle: .subheadline)
            if let descriptor = font.fontDescriptor.withSymbolicTraits(.traitItalic) {
                label.font = UIFont(descriptor: descriptor, size: 0)
            } else { label.font = font }
        case .caption:
            label.font = .preferredFont(forTextStyle: .footnote)
        case .intro, .greeting:
            let font = UIFont.preferredFont(forTextStyle: .largeTitle)
            if let descriptor = font.fontDescriptor.withSymbolicTraits(.traitBold) {
                label.font = UIFont(descriptor: descriptor, size: 0)
            } else { label.font = font }
        }
        if let color = content.color.flatMap({ UIColor(hex: $0) }) {
            label.textColor = color
        } else {
            switch content.style {
            case .h1, .intro: label.textColor = themeProvider.theme.textDefault
            case .paragraph, .quote, .caption, .greeting: label.textColor = themeProvider.theme.textMuted
            }
        }
        if content.style == .greeting {
            label.numberOfLines = 2
            label.lineBreakMode = .byTruncatingTail
        }
        return label
    }

    private var avatarView: UIView? {
        guard let details = expandedHeader.avatarDetails else { return nil }
        let view = AvatarDetailsView(avatarDetails: details, avatarHeight: 56, overlap: 4.48,
                                     activeIndicatorSize: displayActiveIndicator ? 2 : 0)
        view.setContentHuggingPriority(.required, for: .vertical)
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }
}
