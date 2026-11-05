import UIKit

@_objcRuntimeName(_TtC18IntercomSDKPrivate32HelpCenterHomescreenCardViewItem)
class HelpCenterHomescreenCardViewItem: HomescreenCardViewItem {
    private weak var cardDelegate: (any HomescreenCardDelegate)?
    private let card: HelpCenterCard
    init(card: HelpCenterCard, cardDelegate: (any HomescreenCardDelegate)?) {
        self.cardDelegate = cardDelegate
        self.card = card
    }
    func buildView() -> HomescreenCardView {
        let articles = card.suggestedArticles
        let search: () -> Void = { [weak self] in
            let delegate = self?.cardDelegate
            delegate?.navigate(to: .helpCenterSearch)
        }
        let delegate = cardDelegate
        let article: (String) -> Void = { [weak delegate] id in delegate?.navigate(to: .article(id)) }
        let view = SearchBrowseView(suggestedArticles: articles, config: ICMConfig.shared(), themeProvider: DefaultThemeProvider.shared, searchTappedAction: search, articleTappedAction: article)
        let title = card.title
        return HomescreenCardView(title: title, subviews: [view], themeProvider: DefaultThemeProvider.shared)
    }
}
