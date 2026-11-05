import UIKit

@objc(_TtC18IntercomSDKPrivate21HelpCenterArticleView)
class HelpCenterArticleView: UIView {
    let nameLabel = UILabel()
    let chevronView = UIImageView()
    let dividerLine = UIView()
    private let themeProvider: any ThemeProvider
    init(frame: CGRect, themeProvider: any ThemeProvider) {
        self.themeProvider = themeProvider
        super.init(frame: frame)
        backgroundColor = DefaultThemeProvider.shared.theme.base
        preservesSuperviewLayoutMargins = true
        isAccessibilityElement = true
        accessibilityTraits = [.staticText, .button]
        accessibilityIdentifier = "intercom help center article cell"
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        nameLabel.lineBreakMode = .byTruncatingTail
        nameLabel.textColor = DefaultThemeProvider.shared.theme.textDefault
        nameLabel.font = .preferredFont(forTextStyle: .subheadline)
        nameLabel.adjustsFontForContentSizeCategory = true
        addSubview(nameLabel)
        setupChevron()
        setupDivider()
        dividerLine.backgroundColor = themeProvider.theme.neutralBorder
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func setupChevron() {
        chevronView.image = ICMAssets.helpCenterChevron()!.imageFlippedForRightToLeftLayoutDirection()
        chevronView.translatesAutoresizingMaskIntoConstraints = false
        chevronView.accessibilityIdentifier = "intercom help center article cell arrow"
        chevronView.tintColor = themeProvider.theme.primaryForeground
        addSubview(chevronView)
    }
    private func setupDivider() {
        dividerLine.backgroundColor = themeProvider.theme.neutralBorder
        dividerLine.translatesAutoresizingMaskIntoConstraints = false
        dividerLine.accessibilityIdentifier = "intercom help center article cell divider"
        addSubview(dividerLine)
    }
    func configure(name: String, showDividerLine: Bool, isHomeScreen: Bool) {
        nameLabel.text = name
        nameLabel.numberOfLines = isHomeScreen ? 2 : 1
        accessibilityLabel = name
        dividerLine.isHidden = !showDividerLine
        setupConstraints()
        if ICMUtils.contentSizeCategoryIsAccessibilityCategory() {
            nameLabel.numberOfLines = 3
            chevronView.isHidden = true
        }
    }
    func setupConstraints() {
        nameLabel.leadingAnchor.constraint(equalTo: layoutMarginsGuide.leadingAnchor).isActive = true
        nameLabel.topAnchor.constraint(equalTo: topAnchor, constant: 12).isActive = true
        nameLabel.trailingAnchor.constraint(equalTo: chevronView.leadingAnchor, constant: -8).isActive = true
        nameLabel.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -12).isActive = true
        chevronView.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        chevronView.trailingAnchor.constraint(equalTo: layoutMarginsGuide.trailingAnchor).isActive = true
        chevronView.widthAnchor.constraint(equalToConstant: 16).isActive = true
        chevronView.heightAnchor.constraint(equalToConstant: 16).isActive = true
        dividerLine.leadingAnchor.constraint(equalTo: layoutMarginsGuide.leadingAnchor).isActive = true
        dividerLine.trailingAnchor.constraint(equalTo: layoutMarginsGuide.trailingAnchor).isActive = true
        dividerLine.heightAnchor.constraint(equalToConstant: 1).isActive = true
        dividerLine.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
    }
}
