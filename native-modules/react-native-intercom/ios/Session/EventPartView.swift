import UIKit

@objc(_TtC18IntercomSDKPrivate13EventPartView)
class EventPartView: UIView, ConversationCellContentView {
    let content: ICMConversationPart
    var avatarView: ICMAvatarView?
    private let themeProvider: DefaultThemeProvider
    private let maxWidth: CGFloat
    private lazy var stackViewCenterXConstraint: NSLayoutConstraint = {
        let stack = stackView
        let parent = stack.superview!
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.translatesAutoresizingMaskIntoConstraints = false
        let constraint = NSLayoutConstraint(item: stack, attribute: .centerX, relatedBy: .equal, toItem: parent, attribute: .centerX, multiplier: 1, constant: 0)
        constraint.priority = .required
        constraint.isActive = false
        return constraint
    }()
    private lazy var stackViewLeadingConstraint: NSLayoutConstraint = {
        let stack = stackView
        let parent = stack.superview!
        stack.translatesAutoresizingMaskIntoConstraints = false
        let rtl = stack.effectiveUserInterfaceLayoutDirection == .rightToLeft
        stack.translatesAutoresizingMaskIntoConstraints = false
        let constraint = stack.leadingAnchor.constraint(equalTo: parent.leadingAnchor, constant: rtl ? -CGFloat.zero : 0)
        constraint.priority = .required
        constraint.isActive = false
        return constraint
    }()
    private lazy var stackViewTrailingConstraint: NSLayoutConstraint = {
        let stack = stackView
        let parent = stack.superview!
        stack.translatesAutoresizingMaskIntoConstraints = false
        let rtl = stack.effectiveUserInterfaceLayoutDirection == .rightToLeft
        stack.translatesAutoresizingMaskIntoConstraints = false
        let constraint = stack.trailingAnchor.constraint(equalTo: parent.trailingAnchor, constant: rtl ? 0 : -CGFloat.zero)
        constraint.priority = .required
        constraint.isActive = false
        return constraint
    }()
    private lazy var stackView: UIStackView = {
        let stack = UIStackView()
        stack.alignment = .center
        stack.spacing = 6
        return stack
    }()
    private lazy var summaryLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.font = .preferredFont(forTextStyle: .footnote)
        label.adjustsFontForContentSizeCategory = true
        return label
    }()

    init(content: ICMConversationPart, maxWidth: CGFloat, themeProvider: DefaultThemeProvider) {
        self.content = content
        self.maxWidth = maxWidth
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        setupSummaryLabel()
        setupAvatarView()
        setupStackView()
        setupWidthConstraint()
    }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override var intrinsicContentSize: CGSize {
        let stack = stackView
        let width = maxWidth
        let size = stack.systemLayoutSizeFitting(CGSize(width: width, height: UIView.layoutFittingExpandedSize.height), withHorizontalFittingPriority: .required, verticalFittingPriority: .fittingSizeLevel)
        return CGSize(width: width, height: size.height)
    }
    private func setupWidthConstraint() {
        let width = maxWidth
        translatesAutoresizingMaskIntoConstraints = false
        let constraint = widthAnchor.constraint(equalToConstant: width)
        constraint.priority = .required
        constraint.isActive = true
    }
    private func createAvatarView(for part: ICMConversationPart) -> ICMAvatarView? {
        if !part.isEvent() { return nil }
        let view = ICMAvatarView(noTeammatePresenceWithAvatarDiameter: 20)!
        view.setTeamPresenceBorderColor(DefaultThemeProvider.shared.theme.base)
        view.translatesAutoresizingMaskIntoConstraints = false
        if part.groupingPosition == 3 || part.groupingPosition == 2 {
            if let participant = part.participant { view.loadAvatar(for: participant) }
        }
        NSLayoutConstraint.activate([view.widthAnchor.constraint(equalToConstant: 20), view.heightAnchor.constraint(equalToConstant: 20)])
        avatarView = view
        view.accessibilityElementsHidden = true
        return view
    }
    private func setupAvatarView() {
        let part = content
        guard let avatar = createAvatarView(for: part) else { return }
        avatarView = avatar
        let event = part.eventData!
        if let participant = event.participant { avatar.loadAvatar(for: participant) }
    }
    private func setupSummaryLabel() {
        let part = content
        let event = part.eventData!
        if let items = event.eventContent, !items.isEmpty {
            let label = summaryLabel
            label.attributedText = createAttributedString(from: items)
        } else {
            let label = summaryLabel
            label.text = part.eventData!.eventAsPlainText
            summaryLabel.textColor = themeProvider.theme.textMuted
        }
        let label = summaryLabel
        label.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        summaryLabel.setContentHuggingPriority(.defaultLow, for: .horizontal)
    }
    private func setupStackView() {
        addSubview(stackView)
        let stack = stackView
        let topParent = stack.superview!
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.topAnchor.constraint(equalTo: topParent.topAnchor, constant: 0).isActive = true
        let bottomParent = stack.superview!
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.bottomAnchor.constraint(equalTo: bottomParent.bottomAnchor, constant: -CGFloat.zero).isActive = true
        if let avatar = avatarView {
            stackView.addArrangedSubview(avatar)
            let avatarHeight = avatar.heightAnchor
            translatesAutoresizingMaskIntoConstraints = false
            heightAnchor.constraint(greaterThanOrEqualTo: avatarHeight, multiplier: 1, constant: 6).isActive = true
        }
        stackView.addArrangedSubview(summaryLabel)
        configureStackViewLayout(horizontal: doesTextFitOnOneLine())
    }
    private func configureStackViewLayout(horizontal: Bool) {
        if horizontal {
            stackView.axis = .horizontal
            stackView.alignment = .center
            summaryLabel.textAlignment = .left
            stackViewLeadingConstraint.isActive = false
            stackViewTrailingConstraint.isActive = false
            stackViewCenterXConstraint.isActive = true
        } else {
            stackView.axis = .vertical
            stackView.alignment = .center
            summaryLabel.textAlignment = .center
            stackViewCenterXConstraint.isActive = false
            stackViewLeadingConstraint.isActive = true
            stackViewTrailingConstraint.isActive = true
        }
    }
    private func doesTextFitOnOneLine() -> Bool {
        let width = avatarView == nil ? maxWidth : maxWidth - 26
        let label = summaryLabel
        let text = label.attributedText?.string ?? summaryLabel.text ?? ""
        if text.isEmpty { return true }
        let font = summaryLabel.font!
        // Original String.height specialization is called with maxLines zero.
        let rect = (text as NSString).boundingRect(with: CGSize(width: width, height: .greatestFiniteMagnitude), options: .usesLineFragmentOrigin, attributes: [.font: font], context: nil)
        let height = ceil(CGRectGetHeight(rect))
        let lineHeight = summaryLabel.font!.lineHeight
        return height <= lineHeight * 1.1
    }
    private func createAttributedString(from items: [EventContentItem]) -> NSAttributedString {
        let string = NSMutableAttributedString()
        for item in items {
            let weight: UIFont.Weight
            switch item.weight {
            case .regular, nil: weight = .regular
            case .bold: weight = .bold
            case .semibold: weight = .semibold
            case .medium: weight = .medium
            case .light: weight = .light
            }
            let color = weight == .regular ? themeProvider.theme.textMuted : themeProvider.theme.textDefault
            let descriptor = UIFontDescriptor.preferredFontDescriptor(withTextStyle: .footnote)
            let font = UIFont.systemFont(ofSize: descriptor.pointSize, weight: weight)
            string.append(NSAttributedString(string: item.text, attributes: [.font: font, .foregroundColor: color]))
        }
        return string.convertToScalableAttributedString()
    }
}
