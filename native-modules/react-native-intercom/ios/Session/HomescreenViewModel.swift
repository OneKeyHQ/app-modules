import UIKit
import WebKit

// The original private state case names were optimized away.
enum HomescreenViewState {
    case error(String, Bool)
    case loaded([any HomescreenCardViewItem])
    case loading
}

// Concrete original default specialization; deleted generic injection requirements remain unrecovered.
class HomescreenViewModel: NSObject, MessengerAppHomescreenCardDelegate, RecentCardMetricsDelegate, ICMErrorViewDelegate {
    private weak var view: HomescreenViewController?
    private let interactor: HomescreenInteractor
    private var linkOpener: any LinkOpenerProtocol
    private var home: Home?
    private var viewItems: [any HomescreenCardViewItem] = []
    private var needsConfigBasedRouting = false
    private let openCallService: OpenCallService
    private let notificationCenter: any NotificationCenterInterface
    private var config: any ICMConfigProtocol
    private let homeMetricService: any HomeMetricServiceProtocol
    private let queue: any Dispatcher
    init(view: HomescreenViewController, interactor: HomescreenInteractor, linkOpener: any LinkOpenerProtocol, openCallService: OpenCallService, notificationCenter: any NotificationCenterInterface, config: any ICMConfigProtocol, homeMetricService: any HomeMetricServiceProtocol, queue: any Dispatcher) {
        self.view = view
        self.interactor = interactor
        self.linkOpener = linkOpener
        self.openCallService = openCallService
        self.notificationCenter = notificationCenter
        self.config = config
        self.homeMetricService = homeMetricService
        self.queue = queue
        super.init()
        startListeningToNotifications()
    }
    override init() { fatalError("init() has not been implemented") }
    deinit { notificationCenter.removeObserver(self) }
    var poweredByForConversation: PoweredBy? { interactor.poweredByForConversation }
    private func startListeningToNotifications() {
        notificationCenter.addObserver(self, selector: #selector(openCallStateDidChange(_:)), name: .init("IntercomOpenCallStateDidChangeNotification"), object: nil)
        if !config.receivedFromServer() {
            needsConfigBasedRouting = true
            notificationCenter.addObserver(self, selector: #selector(handleConfigDidChange), name: .init("kICMIntercomConfigChangedNotification"), object: nil)
        }
        notificationCenter.addObserver(self, selector: #selector(detentsDidChange(_:)), name: .init("kICMIntercomHomeDetentDidChange"), object: nil)
        notificationCenter.addObserver(self, selector: #selector(refreshCards), name: .init("intercom-sdk-inbox-updated"), object: nil)
        notificationCenter.addObserver(self, selector: #selector(ticketsUpdated), name: .init("intercom-sdk-tickets-updated"), object: nil)
    }
    func viewDidLoad() {
        guard let view else { return }
        if !viewItems.isEmpty { view.updateViewState(.loaded(viewItems)); updateCardsVisibility() }
        else { view.updateViewState(.loading) }
        if openCallService.state == .success { homeMetricService.addHomeViewedMetric() }
        withExtendedLifetime(view) {}
    }
    func viewDidAppear() {
        guard let view else { return }
        reloadVisibleCards()
        handleOpenCallState(view: view)
    }
    private func handleOpenCallState(view: HomescreenViewController) {
        switch openCallService.state {
        case .loading: return
        case .success: fetchHomeData()
        case .failure:
            let error = interactor.homescreenErrorFrom(error: openCallService.lastError)
            show(error: error, on: view)
        }
    }
    private func show(error: HomescreeError, on view: HomescreenViewController) {
        let title = TranslationManager.messenger.translation(forKey: "spaces_error_title")
        view.updateViewState(.error(title, error == .serverError))
    }
    @objc func didSelectErrorAction() {
        do { let view = view; view?.updateViewState(.loading) }
        if openCallService.state == .failure { openCallService.retry() }
        else { fetchHomeData() }
    }
    private func fetchHomeData() {
        guard home == nil else { return }
        interactor.fetchHome { [weak self] result in
            guard let dispatcher = self?.queue else { return }
            dispatcher.dispatch { [weak self] in
                guard let self, let view = self.view else { return }
                defer { withExtendedLifetime(view) {} }
                switch result {
                case .failure(let error): self.show(error: error, on: view)
                case .success(let home):
                    self.home = home
                    self.interactor.updateDataManager(home: home)
                    if self.viewItems.isEmpty {
                        self.viewItems = home.cards.compactMap { self.buildCardViewItem(from: $0, delegate: self, metricsDelegate: self) }
                        view.updateViewState(.loaded(self.viewItems))
                    }
                    self.updateCardsVisibility()
                }
            }
        }
    }
    @objc private func openCallStateDidChange(_ notification: Notification) {
        switch openCallService.state {
        case .loading: return
        case .failure:
            let error = interactor.homescreenErrorFrom(error: openCallService.lastError)
            if let view { show(error: error, on: view) }
        case .success:
            if needsConfigBasedRouting { handleConfigDidChange() }
            fetchHomeData()
            homeMetricService.addHomeViewedMetric()
        }
    }
    @objc private func handleConfigDidChange() {
        config = ICMConfig.shared()
        guard let view, openCallService.state == .success, let rawModules = config.modules else { return }
        let modules = (rawModules as AnyObject) as! ConfigModules
        switch modules.home.openConfig.type.rawValue {
        case 0: return
        case 1: view.navigateToMessages()
        case 2: view.navigateToHelpCenter()
        case 3: view.navigateToTickets()
        case 4: view.navigateToNewConversation()
        default: preconditionFailure("Invalid OpeningViewType")
        }
        view.clearNavigationStack()
    }
    @objc private func detentsDidChange(_ notification: Notification) {
        guard let dictionary = notification.userInfo as? [String: UISheetPresentationController.Detent.Identifier], let detent = dictionary["detent"], let view else { return }
        view.showIntercomLink(detent == .large)
    }
    func displayAlertController(_ controller: UIAlertController) { view?.presentAlertController(controller) }
    func dismissMessengerSheet() { view?.dismissMessengerSheet() }
    @objc private func ticketsUpdated() { refreshCards() }
    func didChangeInterfaceStyle() { didFinishSizeChange() }
    func didFinishSizeChange() {
        let items = viewItems.compactMap { $0 as? MessengerAppHomescreenCardViewItem }
        for item in items {
            item.messengerAppView?.layoutIfNeeded()
            guard let view = item.messengerAppView else { continue }
            let style = view.traitCollection.userInterfaceStyle
            let url = view.fallbackUrl.appended(by: style)
            let request = URLRequest(url: url, cachePolicy: .useProtocolCachePolicy, timeoutInterval: 60)
            view.webView?.load(request)
        }
    }
    private func reloadVisibleCards() {
        let items = viewItems.compactMap { $0 as? any HomescreenCardReloadable }
        for item in items { item.reload() }
    }
    private func updateCardsVisibility() {
        if let index = home?.cards.firstIndex(where: { $0.cardType == .newConversation }), let view {
            view.showCardViewItem(config.canStartConversation(), forCardAt: index)
        }
        let conversations = home?.cards ?? []
        if let index = conversations.firstIndex(where: { $0.cardType == .recentConversation }), let card = conversations[index] as? RecentConversationsCard, let view {
            let show = interactor.hasRecentConversations(count: card.numberOfConversationsToDisplay)
            view.showCardViewItem(show, forCardAt: index)
        }
        let tickets = home?.cards ?? []
        if let index = tickets.firstIndex(where: { $0.cardType == .recentTickets }), let view {
            let show = interactor.hasRecentTickets()
            view.showCardViewItem(show, forCardAt: index)
        }
    }
    @objc private func refreshCards() {
        refreshSpacesCard()
        updateCardsVisibility()
    }
    private func refreshSpacesCard() {
        guard let index = home?.cards.firstIndex(where: { $0.cardType == .spaces }) else { return }
        guard var card = home?.cards[index] as? SpaceCard else { return }
        if let space = getUpdatedMessagesSpace(), let index = card.space.firstIndex(where: { $0.spaceType == 0 }) { card.space[index] = space }
        if let space = getUpdatedTicketsSpace(), let index = card.space.firstIndex(where: { $0.spaceType == 2 }) { card.space[index] = space }
        home?.update(cardAtIndex: index, with: card)
        if index < viewItems.count {
            let item = viewItems[index]
            if object_getClass(item) === SpaceHomecreenCardViewItem.self {
                (item as! SpaceHomecreenCardViewItem).update(with: card)
            }
        }
    }
    private func getUpdatedMessagesSpace() -> Space? {
        guard let card = home?.cards.first(where: { $0.cardType == .spaces }) as? SpaceCard,
              let space = card.space.first(where: { $0.spaceType == 0 }) else { return nil }
        let count = interactor.getUnreadConversationsCount()
        let badge = count >= 1 ? SpaceBadge(badgeType: .unread, title: String(count)) : nil
        return Space(spaceType: space.spaceType, badge: badge, title: space.title)
    }
    private func getUpdatedTicketsSpace() -> Space? {
        guard let card = home?.cards.first(where: { $0.cardType == .spaces }) as? SpaceCard,
              let space = card.space.first(where: { $0.spaceType == 2 }) else { return nil }
        let badge = interactor.hasUnreadTickets() ? SpaceBadge(badgeType: .passive, title: nil) : nil
        return Space(spaceType: space.spaceType, badge: badge, title: space.title)
    }
    func navigate(to destination: HomescreenDestination) {
        guard let view else { return }
        defer { withExtendedLifetime(view) {} }
        switch destination {
        case .article(let id): view.navigateToArticle(id)
        case .link(let url): linkOpener.openLink(url)
        case .conversation(let conversation): view.navigateToConversation(conversation)
        case .ticket(let ticket): view.navigateToTicket(ticket)
        case .appSheet(let url, let params, let delegate): view.navigateToAppSheet(url, params: params, delegate: delegate)
        case .createTicket(let ticketType): view.navigateToCreateTicket(ticketType)
        case .helpCenter: view.navigateToHelpCenter()
        case .helpCenterSearch: view.navigateToHelpCenterSearch()
        case .newConversation: view.navigateToNewConversation()
        case .messages: view.navigateToMessages()
        case .tickets: view.navigateToTickets()
        }
    }
    func addMetricForRecentConversationsClick(with conversation: ICMConversation) {
        let service = MessagesMetricService(metricInstance: ICMMetric.sharedInstance())
        let ticket = conversation.ticket
        service.addConversationClickedFromHomeMetric(ticket: ticket)
    }
    func addMetricForRecentTicketsClick() {
        let service = MessagesMetricService(metricInstance: ICMMetric.sharedInstance())
        service.addTicketClickedFromHomeMetric()
    }
    private func buildCardViewItem(from card: any HomeCard, delegate: (any HomescreenCardDelegate)?, metricsDelegate: (any RecentCardMetricsDelegate)?) -> (any HomescreenCardViewItem)? {
        if let card = card as? SpaceCard {
            let service = HomeMetricService(metricInstance: ICMMetric.sharedInstance())
            return SpaceHomecreenCardViewItem(card: card, cardDelegate: delegate, homeMetricService: service, themeProvider: DefaultThemeProvider.shared)
        }
        if let card = card as? RecentConversationsCard {
            return RecentConversationHomecreenCardViewItem(card: card, cardDelegate: delegate, metricsDelegate: metricsDelegate, dataManager: ICMDataManager.shared(), themeProvider: nil)
        }
        if let card = card as? RecentTicketsCard {
            return RecentTicketsHomescreenCardViewItem(card: card, cardDelegate: delegate, metricsDelegate: metricsDelegate, dataManager: ICMDataManager.shared())
        }
        if card is NewConversationCard {
            guard let card = interactor.getNewConversationCard() else { return nil }
            let service = MessagesMetricService(metricInstance: ICMMetric.sharedInstance())
            return NewConversationHomescreenCardViewItem(card: card, cardDelegate: delegate, messagesMetricService: service, themeProvider: DefaultThemeProvider.shared)
        }
        if let card = card as? ExternalLinksCard {
            let service = HomeMetricService(metricInstance: ICMMetric.sharedInstance())
            return ExternalLinksHomescreenCardViewItem(card: card, cardDelegate: delegate, homeMetricService: service, themeProvider: DefaultThemeProvider.shared)
        }
        if let card = card as? HelpCenterCard { return HelpCenterHomescreenCardViewItem(card: card, cardDelegate: delegate) }
        if let card = card as? MessengerAppCard {
            guard let delegate = delegate as? any MessengerAppHomescreenCardDelegate else { return nil }
            return MessengerAppHomescreenCardViewItem(card: card, messengerCardDelegate: delegate)
        }
        if let card = card as? CreateTicketCard { return CreateTicketHomescreenCardViewitem(card: card, cardDelegate: delegate, themeProvider: DefaultThemeProvider.shared) }
        return nil
    }
}
