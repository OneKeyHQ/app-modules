import UIKit

@objc(_TtC18IntercomSDKPrivate35HelpCenterSearchResultTableViewCell)
class HelpCenterSearchResultTableViewCell: UITableViewCell {
    private let themeProvider = DefaultThemeProvider.shared
    private let nameLabel = UILabel()
    private let chevronView = UIImageView()
    private let dividerLine = UIView()
    private let descriptionLabel = UILabel()
    private let stackView = UIStackView()
    private let maxLineCount = 3
    private let startTag = "<highlight>"
    private let endTag = "</highlight>"
    private let nameHighlightFont = UIFont.systemFont(ofSize: UIFont.preferredFont(forTextStyle: .body).pointSize, weight: .bold)
    private let descriptionHighlightFont = UIFont.systemFont(ofSize: UIFont.preferredFont(forTextStyle: .subheadline).pointSize, weight: .bold)
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        backgroundColor = DefaultThemeProvider.shared.theme.base
        addSubview(stackView)
        addSubview(chevronView)
        addSubview(dividerLine)
        setupStack()
        setupChevron()
        setupDivider()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    private func setupStack() {
        stackView.axis = .vertical
        stackView.distribution = .equalSpacing
        stackView.alignment = .leading
        stackView.spacing = 8
        stackView.translatesAutoresizingMaskIntoConstraints = false
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        nameLabel.numberOfLines = maxLineCount
        nameLabel.lineBreakMode = .byTruncatingTail
        nameLabel.textColor = DefaultThemeProvider.shared.theme.textDefault
        nameLabel.font = .preferredFont(forTextStyle: .body)
        descriptionLabel.translatesAutoresizingMaskIntoConstraints = false
        descriptionLabel.numberOfLines = maxLineCount
        descriptionLabel.lineBreakMode = .byTruncatingTail
        descriptionLabel.textColor = themeProvider.theme.textMuted
        descriptionLabel.font = .preferredFont(forTextStyle: .subheadline)
        stackView.addArrangedSubview(nameLabel)
        stackView.addArrangedSubview(descriptionLabel)
        stackView.leadingAnchor.constraint(equalTo: layoutMarginsGuide.leadingAnchor).isActive = true
        stackView.trailingAnchor.constraint(equalTo: chevronView.leadingAnchor, constant: -20).isActive = true
        stackView.topAnchor.constraint(equalTo: topAnchor, constant: 16).isActive = true
        stackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -16).isActive = true
    }
    private func setupChevron() {
        chevronView.image = ICMAssets.helpCenterChevron()!.imageFlippedForRightToLeftLayoutDirection()
        chevronView.translatesAutoresizingMaskIntoConstraints = false
        chevronView.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        chevronView.trailingAnchor.constraint(equalTo: layoutMarginsGuide.trailingAnchor).isActive = true
    }
    private func setupDivider() {
        dividerLine.backgroundColor = themeProvider.theme.neutralBorder
        dividerLine.translatesAutoresizingMaskIntoConstraints = false
        dividerLine.leadingAnchor.constraint(equalTo: layoutMarginsGuide.leadingAnchor).isActive = true
        dividerLine.trailingAnchor.constraint(equalTo: layoutMarginsGuide.trailingAnchor).isActive = true
        dividerLine.heightAnchor.constraint(equalToConstant: 1).isActive = true
        dividerLine.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
    }
    func configure(title: String, summary: String?, hideDivider: Bool) {
        nameLabel.attributedText = modifySubstringsBetweenTags(title, color: themeProvider.theme.textDefault, font: nameHighlightFont)
        setDescriptionLabel(summary)
        dividerLine.isHidden = hideDivider
    }
    private func setDescriptionLabel(_ summary: String?) {
        if let summary {
            let cleaned = summary.replacingOccurrences(of: "\n", with: "").trimmingCharacters(in: .whitespacesAndNewlines)
            if cleaned.isEmpty { descriptionLabel.isHidden = true }
            else {
                descriptionLabel.attributedText = modifySubstringsBetweenTags(cleaned, color: DefaultThemeProvider.shared.theme.textDefault, font: descriptionHighlightFont)
                descriptionLabel.isHidden = false
            }
        } else { descriptionLabel.isHidden = true }
        layoutIfNeeded()
    }
    func modifySubstringsBetweenTags(_ input: String, color: UIColor, font: UIFont?) -> NSAttributedString {
        let attributed = NSMutableAttributedString(string: input)
        var current = input
        while let opening = current.range(of: startTag) {
            guard let closing = current.range(of: endTag, range: opening.upperBound..<current.endIndex) else { break }
            let range = NSRange(opening.upperBound..<closing.lowerBound, in: current)
            if let font { attributed.addAttributes([.font: font], range: range) }
            attributed.addAttribute(.foregroundColor, value: color, range: range)
            attributed.mutableString.deleteCharacters(in: NSRange(closing, in: current))
            attributed.mutableString.deleteCharacters(in: NSRange(opening, in: current))
            current = attributed.mutableString as String
        }
        return attributed
    }
}
