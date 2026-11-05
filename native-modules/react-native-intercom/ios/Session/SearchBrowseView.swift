import UIKit

@objc(_TtC18IntercomSDKPrivate37SuggestedArticlesTapGestureRecognizer)
class SuggestedArticlesTapGestureRecognizer: UITapGestureRecognizer {
    var articleToPresent: InternalHelpCenterArticle?
    override init(target: Any?, action: Selector?) { super.init(target: target, action: action) }
}

@objc(_TtC18IntercomSDKPrivate16SearchBrowseView)
class SearchBrowseView: UIView {
    private var metricService: any HelpCenterMetricServiceProtocol = HelpCenterMetricService()
    private let themeProvider: any ThemeProvider
    private let searchButtonAccessibilityIdentifier = "search browse button"
    private let avatarViewAccessibilityIdentifier = "available team view"
    private let horizontalPadding: CGFloat = 20
    private let verticalPadding: CGFloat = 24
    private let stackViewHorizontalPadding: CGFloat = 8
    private let searchBackgroundRadius: CGFloat = 8
    private let componentPadding: CGFloat = 16
    private let searchBarPadding: CGFloat = 9
    private let teammateAvatarViewTopSpacing: CGFloat = 12
    private let cellTopBorderHeight: CGFloat = 2
    private let searchBarMinHeight: CGFloat = 40
    private let searchBrowseConfig: any ICMConfigProtocol
    private let stackView = UIStackView()
    private var widthConstraint: NSLayoutConstraint?
    private var suggestedArticles: [InternalHelpCenterArticle] = []
    private var searchTappedAction: (() -> Void)?
    private var articleTappedAction: ((String) -> Void)?

    init(suggestedArticles: [InternalHelpCenterArticle], config: any ICMConfigProtocol, themeProvider: any ThemeProvider, searchTappedAction: (() -> Void)?, articleTappedAction: ((String) -> Void)?) {
        searchBrowseConfig = config
        self.suggestedArticles = suggestedArticles
        self.themeProvider = themeProvider
        self.searchTappedAction = searchTappedAction
        self.articleTappedAction = articleTappedAction
        super.init(frame: .zero)
        configureView()
        ICMUtils.enableRTLSupport(forContainerClass: SearchBrowseView.self)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    override func traitCollectionDidChange(_ previous: UITraitCollection?) {
        super.traitCollectionDidChange(previous)
        if previous?.preferredContentSizeCategory == traitCollection.preferredContentSizeCategory { return }
        let views = stackView.subviews
        for view in views { stackView.removeArrangedSubview(view); view.removeFromSuperview() }
        configureView()
    }
    @objc(heightForCardWithWidth:)
    func heightForCard(width: CGFloat) -> CGFloat {
        widthConstraint?.isActive = false
        widthConstraint = stackView.widthAnchor.constraint(equalToConstant: width)
        widthConstraint?.priority = .init(999)
        widthConstraint?.isActive = true
        return stackView.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize).height + 2
    }
    @objc func configureView() {
        stackView.backgroundColor = themeProvider.theme.base
        stackView.axis = .vertical
        stackView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(stackView)
        stackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: stackViewHorizontalPadding).isActive = true
        stackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -stackViewHorizontalPadding).isActive = true
        stackView.topAnchor.constraint(equalTo: topAnchor).isActive = true
        stackView.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        addSearchBarStackView()
        if !suggestedArticles.isEmpty { addSuggestedArticles(); addSuggestedArticlesPresentedToUserMetric() }
        addTeammateAvatarView()
    }
    @objc func searchHelpArticlesAction() {
        searchTappedAction?()
        metricService.contextIsSearchBrowse = true
        metricService.addSearchBrowseOpenedEvents()
    }
    @objc(presentSuggestedArticleActionWithGestureRecognizer:)
    func presentSuggestedArticleAction(gestureRecognizer: SuggestedArticlesTapGestureRecognizer) {
        guard let article = gestureRecognizer.articleToPresent else { return }
        articleTappedAction?(article.id)
        addSuggestedArticleViewedMetric(id: article.id)
    }
    @objc func addSuggestedArticles() {
        let articles = suggestedArticles
        for article in articles {
            let view = HelpCenterArticleView(frame: .zero, themeProvider: DefaultThemeProvider.shared)
            view.nameLabel.text = article.title
            view.nameLabel.numberOfLines = 2
            view.accessibilityLabel = article.title
            view.dividerLine.isHidden = true
            view.setupConstraints()
            if ICMUtils.contentSizeCategoryIsAccessibilityCategory() {
                view.nameLabel.numberOfLines = 3
                view.chevronView.isHidden = true
            }
            view.accessibilityLabel = article.title
            let tap = SuggestedArticlesTapGestureRecognizer(target: self, action: #selector(presentSuggestedArticleAction(gestureRecognizer:)))
            tap.articleToPresent = article
            view.addGestureRecognizer(tap)
            stackView.addArrangedSubview(view)
        }
    }
    @objc(isLastCellWithArticle:)
    func isLastCell(article: InternalHelpCenterArticle) -> Bool {
        guard let index = suggestedArticles.firstIndex(of: article) else { return false }
        return index == suggestedArticles.count - 1
    }
    @objc func addTeammateAvatarView() {
        guard shouldShowTeammateAvatars() else { return }
        let view = TeammateAvatarsView(removeHorizontalPadding: true, themeProvider: DefaultThemeProvider.shared)
        stackView.addArrangedSubview(view)
    }
    @objc func shouldShowTeammateAvatars() -> Bool {
        searchBrowseConfig.helpCenterRequireSearch && searchBrowseConfig.inboundMessages && searchBrowseConfig.accessToTeammateEnabled
    }
    @objc(addSuggestedArticleViewedMetricWithId:)
    func addSuggestedArticleViewedMetric(id: String) { metricService.addSuggestedArticleViewedEvent(articleViewed: id) }
    @objc func addSuggestedArticlesPresentedToUserMetric() {
        let service = metricService
        let ids = suggestedArticles.map(\.id)
        let count = suggestedArticles.count
        service.addSuggestedArticlesPresentedToUserEvent(articlesShown: ids, numberOfArticles: count)
    }
    @objc func addSearchBarStackView() {
        let container = UIView()
        container.translatesAutoresizingMaskIntoConstraints = false
        if shouldShowTeammateAvatars() { container.backgroundColor = themeProvider.theme.neutralContainer }
        else if !suggestedArticles.isEmpty { container.backgroundColor = themeProvider.theme.neutralContainer }
        else { container.backgroundColor = themeProvider.theme.neutralContainerAlt }
        container.layer.cornerRadius = searchBackgroundRadius
        container.accessibilityIdentifier = searchButtonAccessibilityIdentifier
        let inner = UIStackView()
        inner.alignment = .center
        inner.spacing = searchBarPadding
        inner.axis = .horizontal
        let label = UILabel()
        label.text = TranslationManager.messenger.translation(forKey: "search_for_help")
        label.accessibilityLabel = label.text
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        let font = UIFont.preferredFont(forTextStyle: .subheadline)
        if let descriptor = font.fontDescriptor.withSymbolicTraits(.traitBold) { label.font = UIFont(descriptor: descriptor, size: 0) }
        else { label.font = font }
        label.backgroundColor = .clear
        label.translatesAutoresizingMaskIntoConstraints = false
        label.adjustsFontForContentSizeCategory = true
        label.sizeToFit()
        inner.addArrangedSubview(label)
        let image = UIImageView(image: ICMAssets.searchIcon())
        image.contentMode = .scaleAspectFit
        image.tintColor = themeProvider.theme.primaryForeground
        image.backgroundColor = .clear
        image.translatesAutoresizingMaskIntoConstraints = false
        inner.addArrangedSubview(image)
        inner.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(searchHelpArticlesAction)))
        inner.translatesAutoresizingMaskIntoConstraints = false
        container.addSubview(inner)
        stackView.addArrangedSubview(container)
        NSLayoutConstraint.activate([
            image.widthAnchor.constraint(equalToConstant: 16), image.heightAnchor.constraint(equalToConstant: 16), image.centerYAnchor.constraint(equalTo: inner.centerYAnchor),
            inner.leadingAnchor.constraint(equalTo: container.leadingAnchor, constant: 8), inner.trailingAnchor.constraint(equalTo: container.trailingAnchor, constant: -8),
            inner.topAnchor.constraint(equalTo: container.topAnchor, constant: searchBarPadding), inner.bottomAnchor.constraint(equalTo: container.bottomAnchor, constant: -searchBarPadding),
            container.heightAnchor.constraint(greaterThanOrEqualToConstant: searchBarMinHeight)
        ])
    }
}
