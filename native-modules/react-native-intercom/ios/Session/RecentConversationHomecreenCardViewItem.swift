import UIKit

@objc(_TtC18IntercomSDKPrivate39RecentConversationHomecreenCardViewItem)
class RecentConversationHomecreenCardViewItem: NSObject, HomescreenCardViewItem, HomescreenCardReloadable, ICMRecentConversationsViewControllerDelegate {
    private weak var cardDelegate: (any HomescreenCardDelegate)?
    private weak var metricsDelegate: (any RecentCardMetricsDelegate)?
    private let card: RecentConversationsCard
    private let dataManager: any ICMDataManagerProtocol
    private let themeProvider: (any ThemeProvider)?
    private lazy var recentConversationsViewController = ICMRecentConversationsViewController(dataManager: dataManager, themeProvider: themeProvider)!
    private lazy var recentConversationsView: UIView = {
        let controller = recentConversationsViewController
        if let view = controller.view { view.translatesAutoresizingMaskIntoConstraints = false; return view }
        return UIView()
    }()
    init(card: RecentConversationsCard, cardDelegate: (any HomescreenCardDelegate)?, metricsDelegate: (any RecentCardMetricsDelegate)?, dataManager: any ICMDataManagerProtocol, themeProvider: (any ThemeProvider)?) {
        self.cardDelegate = cardDelegate
        self.metricsDelegate = metricsDelegate
        self.card = card
        self.dataManager = dataManager
        self.themeProvider = themeProvider
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
    func buildView() -> HomescreenCardView {
        recentConversationsViewController.delegate = self
        recentConversationsViewController.numberOfConversationsToDisplay = card.numberOfConversationsToDisplay
        let title = card.title
        let view = recentConversationsView
        let parent = HomescreenCardView(title: title, subviews: [view], themeProvider: DefaultThemeProvider.shared)
        recentConversationsViewController.updateViewWithLatestConversations()
        return parent
    }
    @objc func didSelectConversation(_ conversation: ICMConversation) {
        do { let delegate = metricsDelegate; delegate?.addMetricForRecentConversationsClick(with: conversation) }
        let delegate = cardDelegate
        delegate?.navigate(to: .conversation(conversation))
    }
    func reload() { recentConversationsViewController.updateViewWithLatestConversations() }
}
