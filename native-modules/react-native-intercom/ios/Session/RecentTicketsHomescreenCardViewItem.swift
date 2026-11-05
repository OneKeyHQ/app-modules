import UIKit

@objc(_TtC18IntercomSDKPrivate35RecentTicketsHomescreenCardViewItem)
class RecentTicketsHomescreenCardViewItem: NSObject, HomescreenCardViewItem, HomescreenCardReloadable {
    private weak var cardDelegate: (any HomescreenCardDelegate)?
    private weak var metricsDelegate: (any RecentCardMetricsDelegate)?
    private let card: RecentTicketsCard
    private let recentTicketsTableView: RecentTicketsTableView
    init(card: RecentTicketsCard, cardDelegate: (any HomescreenCardDelegate)?, metricsDelegate: (any RecentCardMetricsDelegate)?, dataManager: any ICMDataManagerProtocol) {
        self.cardDelegate = cardDelegate
        self.metricsDelegate = metricsDelegate
        self.card = card
        recentTicketsTableView = RecentTicketsTableView(dataManager: dataManager)
        super.init()
        recentTicketsTableView.recentTicketsViewDelegate = self
        recentTicketsTableView.updateWithLatestTickets()
    }
    override init() { fatalError("init() has not been implemented") }
    func buildView() -> HomescreenCardView {
        HomescreenCardView(title: card.title, subviews: [recentTicketsTableView], themeProvider: DefaultThemeProvider.shared)
    }
    func reload() { recentTicketsTableView.updateWithLatestTickets() }
    func didSelectTicket(_ ticket: Ticket) {
        do { let delegate = metricsDelegate; delegate?.addMetricForRecentTicketsClick() }
        let delegate = cardDelegate
        delegate?.navigate(to: .ticket(ticket))
    }
}
