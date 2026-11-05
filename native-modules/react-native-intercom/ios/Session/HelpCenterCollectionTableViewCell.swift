import UIKit

@objc(_TtC18IntercomSDKPrivate33HelpCenterCollectionTableViewCell)
class HelpCenterCollectionTableViewCell: UITableViewCell {
    private let translationManager = TranslationManager.helpCenter
    private let mainStackView = UIStackView()
    private let nameLabel = UILabel()
    private let descriptionLabel = UILabel()
    private let countLabel = UILabel()
    private let chevronView = UIImageView()
    private let dividerLine = UIView()
    private var titleLabelToBottomConstraint = NSLayoutConstraint()
    private var titleLabelToDescriptionLabelConstraint = NSLayoutConstraint()
    private let themeProvider = DefaultThemeProvider.shared
    private var showDescription = false {
        didSet {
            if oldValue == showDescription { return }
            if !showDescription {
                mainStackView.removeArrangedSubview(descriptionLabel)
                descriptionLabel.removeFromSuperview()
            } else {
                let index = mainStackView.arrangedSubviews.firstIndex(of: countLabel) ?? 0
                mainStackView.insertArrangedSubview(descriptionLabel, at: index)
                mainStackView.setCustomSpacing(8, after: descriptionLabel)
            }
        }
    }
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        backgroundColor = DefaultThemeProvider.shared.theme.base
        isAccessibilityElement = true
        accessibilityTraits = [.staticText, .button]
        accessibilityIdentifier = "intercom help center collection cell"
        addSubview(mainStackView)
        addSubview(chevronView)
        addSubview(dividerLine)
        setupStack()
        setupTitle()
        setupDescription()
        setupCount()
        setupChevron()
        setupDivider()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    private func setupStack() {
        mainStackView.axis = .vertical
        mainStackView.spacing = 4
        mainStackView.translatesAutoresizingMaskIntoConstraints = false
        mainStackView.leadingAnchor.constraint(equalTo: layoutMarginsGuide.leadingAnchor).isActive = true
        mainStackView.topAnchor.constraint(equalTo: topAnchor, constant: 16).isActive = true
        mainStackView.trailingAnchor.constraint(equalTo: chevronView.trailingAnchor, constant: -20).isActive = true
        mainStackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -16).isActive = true
    }
    private func setupTitle() {
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        nameLabel.numberOfLines = 3
        nameLabel.lineBreakMode = .byTruncatingTail
        nameLabel.textColor = DefaultThemeProvider.shared.theme.textDefault
        nameLabel.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .subheadline).pointSize, weight: .semibold)
        mainStackView.addArrangedSubview(nameLabel)
    }
    private func setupDescription() {
        descriptionLabel.translatesAutoresizingMaskIntoConstraints = false
        descriptionLabel.numberOfLines = 3
        descriptionLabel.lineBreakMode = .byTruncatingTail
        descriptionLabel.textColor = DefaultThemeProvider.shared.theme.textDefault
        descriptionLabel.font = .preferredFont(forTextStyle: .subheadline)
    }
    private func setupCount() {
        countLabel.translatesAutoresizingMaskIntoConstraints = false
        countLabel.lineBreakMode = .byTruncatingTail
        countLabel.textColor = themeProvider.theme.textMuted
        countLabel.font = .preferredFont(forTextStyle: .subheadline)
        countLabel.setContentHuggingPriority(.required, for: .vertical)
        mainStackView.addArrangedSubview(countLabel)
    }
    private func setupChevron() {
        chevronView.image = ICMAssets.helpCenterChevron()!.imageFlippedForRightToLeftLayoutDirection()
        chevronView.translatesAutoresizingMaskIntoConstraints = false
        chevronView.accessibilityIdentifier = "intercom help center collection cell arrow"
        chevronView.tintColor = themeProvider.theme.primaryForeground
        chevronView.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        chevronView.trailingAnchor.constraint(equalTo: layoutMarginsGuide.trailingAnchor).isActive = true
        chevronView.heightAnchor.constraint(equalToConstant: 16).isActive = true
        chevronView.widthAnchor.constraint(equalToConstant: 16).isActive = true
    }
    private func setupDivider() {
        dividerLine.backgroundColor = themeProvider.theme.neutralBorder
        dividerLine.translatesAutoresizingMaskIntoConstraints = false
        dividerLine.accessibilityIdentifier = "intercom help center collection cell divider"
        dividerLine.leadingAnchor.constraint(equalTo: layoutMarginsGuide.leadingAnchor).isActive = true
        dividerLine.trailingAnchor.constraint(equalTo: layoutMarginsGuide.trailingAnchor).isActive = true
        dividerLine.heightAnchor.constraint(equalToConstant: 1).isActive = true
        dividerLine.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
    }
    func configure(forCollection collection: InternalHelpCenterCollection, showDividerLine: Bool) {
        nameLabel.text = collection.title
        dividerLine.isHidden = !showDividerLine
        let count = collection.articleCount
        let text: String
        if count == 1 { text = translationManager.translation(forKey: "single_article") }
        else { text = translationManager.translation(forKey: "multiple_articles").replacingOccurrences(of: "{total_articles}", with: String(count)) }
        countLabel.text = text.lowercased()
        if let description = collection.description {
            descriptionLabel.text = description
            accessibilityLabel = collection.title + ", " + description + ", " + text
            showDescription = true
        } else {
            descriptionLabel.text = ""
            accessibilityLabel = collection.title + ", " + text
            showDescription = false
        }
    }
}
