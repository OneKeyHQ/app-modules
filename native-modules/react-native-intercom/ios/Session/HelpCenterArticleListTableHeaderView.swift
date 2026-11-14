import UIKit

@objc(_TtC18IntercomSDKPrivate36HelpCenterArticleListTableHeaderView)
class HelpCenterArticleListTableHeaderView: HelpCenterTableHeaderView {
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.font = .preferredFont(forTextStyle: .headline)
        label.adjustsFontForContentSizeCategory = true
        label.numberOfLines = 2
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var descriptionLabel: UILabel = {
        let label = UILabel()
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.adjustsFontForContentSizeCategory = true
        label.numberOfLines = 3
        label.textColor = themeProvider.theme.textMuted
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var authorAvatarsView: HelpCenterArticleAuthorAvatarsView = {
        let height: CGFloat = ICMUtils.contentSizeCategoryIsAccessibilityCategory() ? 48 : 32
        let view = HelpCenterArticleAuthorAvatarsView(authors: collection.authors, height: height, overlap: height * 0.08, maxCount: 3)!
        view.setContentHuggingPriority(.required, for: .horizontal)
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var authorNamesLabel: UILabel = {
        let label = UILabel()
        label.font = .preferredFont(forTextStyle: .footnote)
        label.adjustsFontForContentSizeCategory = true
        label.numberOfLines = ICMUtils.contentSizeCategoryIsAccessibilityCategory() ? 2 : 1
        label.textColor = themeProvider.theme.textMuted
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var articleCountLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines = 1
        label.lineBreakMode = .byTruncatingTail
        label.textColor = themeProvider.theme.textMuted
        label.font = .preferredFont(forTextStyle: .footnote)
        label.adjustsFontForContentSizeCategory = true
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var articleCountAndAuthorStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.spacing = 4
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.addArrangedSubview(articleCountLabel)
        stack.addArrangedSubview(authorNamesLabel)
        return stack
    }()
    private lazy var mainStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.distribution = .fill
        stack.spacing = 4
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var bottomStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = ICMUtils.contentSizeCategoryIsAccessibilityCategory() ? .vertical : .horizontal
        stack.alignment = ICMUtils.contentSizeCategoryIsAccessibilityCategory() ? .leading : .center
        stack.distribution = .fill
        stack.spacing = 8
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var dividerLine: UIView = {
        let view = UIView()
        view.backgroundColor = themeProvider.theme.neutralBorder
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private let translationManager = TranslationManager.helpCenter
    private let collection: InternalHelpCenterCollectionContent
    private let themeProvider: any ThemeProvider
    init(collection: InternalHelpCenterCollectionContent, frame: CGRect, themeProvider: any ThemeProvider) {
        self.collection = collection
        self.themeProvider = themeProvider
        super.init(frame: frame)
        addSubviews()
        configure(collection)
        setUpAccessibility()
    }
    @nonobjc required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func addSubviews() {
        layer.borderWidth = 0
        addSubview(mainStackView)
        addSubview(dividerLine)
        NSLayoutConstraint.activate([
            mainStackView.trailingAnchor.constraint(equalTo: layoutMarginsGuide.trailingAnchor), mainStackView.topAnchor.constraint(equalTo: topAnchor, constant: 16),
            mainStackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -16), dividerLine.heightAnchor.constraint(equalToConstant: 1),
            dividerLine.trailingAnchor.constraint(equalTo: trailingAnchor), dividerLine.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
        let leading = mainStackView.leadingAnchor.constraint(equalTo: layoutMarginsGuide.leadingAnchor)
        leading.priority = .init(999)
        leading.isActive = true
        let divider = dividerLine.leadingAnchor.constraint(equalTo: leadingAnchor)
        divider.priority = .init(999)
        divider.isActive = true
        mainStackView.addArrangedSubview(titleLabel)
        mainStackView.addArrangedSubview(descriptionLabel)
        mainStackView.addArrangedSubview(bottomStackView)
        bottomStackView.addArrangedSubview(articleCountAndAuthorStackView)
        bottomStackView.addArrangedSubview(authorAvatarsView)
        mainStackView.setCustomSpacing(16, after: descriptionLabel)
    }
    private func configure(_ collection: InternalHelpCenterCollectionContent) {
        titleLabel.text = collection.title
        descriptionLabel.text = collection.description
        authorNamesLabel.text = authorNames(collection.authors)
        let count = collection.articleCount
        if count == 1 { articleCountLabel.text = translationManager.translation(forKey: "single_article") }
        else { articleCountLabel.text = translationManager.translation(forKey: "multiple_articles").replacingOccurrences(of: "{total_articles}", with: String(count)) }
    }
    private func authorNames(_ authors: [InternalHelpCenterArticleAuthor]) -> String {
        switch authors.count {
        case 0: return ""
        case 1: return translationManager.translation(forKey: "article_single_author").replacingOccurrences(of: "{author_first_name}", with: authors[0].displayName)
        case 2:
            return translationManager.translation(forKey: "article_double_author").replacingOccurrences(of: "{author_first_name1}", with: authors[0].displayName).replacingOccurrences(of: "{author_first_name2}", with: authors[1].displayName)
        default:
            return translationManager.translation(forKey: "article_multiple_authors").replacingOccurrences(of: "{author_first_name1}", with: authors[0].displayName).replacingOccurrences(of: "{number_of_other_authors}", with: String(authors.count - 1))
        }
    }
    private func setUpAccessibility() {
        isAccessibilityElement = true
        accessibilityTraits = .staticText
        accessibilityLabel = (titleLabel.text ?? "") + ", " + (descriptionLabel.text ?? "") + ", " + (authorNamesLabel.text ?? "") + ", " + (articleCountLabel.text ?? "")
    }
}
