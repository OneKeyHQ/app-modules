import UIKit

enum InboxViewState {
    case content(conversations: [ICMConversation])
    case empty(emptyState: MessagesEmptyState)
    case error(message: String, retry: Bool)
    case loading
}

// Only concrete observed callbacks; this is not the original deleted ten-slot private ABI.
protocol RecoveredInboxView: AnyObject {
    func updateViewState(_ state: InboxViewState)
    func setLoadingViewVisible(_ visible: Bool)
    func hideErrorView()
    func reloadTableView()
    func reloadTableViewRow(at indexPath: IndexPath)
    func configureNavigationBarButtons()
    func navigateToConversation(_ conversation: ICMConversation)
    func presentHelpCenter()
}

@objc(_TtC18IntercomSDKPrivate14InboxViewModel)
class InboxViewModel: NSObject, ICMErrorViewDelegate, MessagesEmptyStateDelegate {
    private weak var view: RecoveredInboxView?
    let dataManager: ICMDataManager
    private let metricsService: MessagesMetricServiceProtocol
    let config: ICMConfigProtocol
    let queue: Dispatcher
    private let openCallService: OpenCallServiceProtocol
    private let notificationCenter: NotificationCenterInterface
    private(set) var conversationsArray: [ICMConversation] = []
    private(set) var isLoading = false

    init(view: RecoveredInboxView?, dataManager: ICMDataManager, metricsService: MessagesMetricServiceProtocol, config: ICMConfigProtocol, queue: Dispatcher, openCallService: OpenCallServiceProtocol, notificationCenter: NotificationCenterInterface) {
        self.view = view
        self.dataManager = dataManager
        self.metricsService = metricsService
        self.config = config
        self.queue = queue
        self.openCallService = openCallService
        self.notificationCenter = notificationCenter
        super.init()
        notificationCenter.addObserver(self, selector: #selector(openCallStateDidChange(_:)), name: Notification.Name("IntercomOpenCallStateDidChangeNotification"), object: nil)
    }
    override init() { fatalError("init() has not been implemented") }
    deinit { notificationCenter.removeObserver(self) }

    @objc func viewDidLoad() {
        view?.setLoadingViewVisible(true)
        isLoading = true
        handleOpenCallState()
        metricsService.addMessagesViewedMetric()
    }
    @objc func viewWillAppear() {
        dataManager.lastViewedConversation = nil
        reloadInbox()
    }
    @objc private func openCallStateDidChange(_ notification: Notification) { handleOpenCallState() }
    private func handleOpenCallState() {
        switch openCallService.state {
        case .loading: break
        case .success: fetchConversations()
        case .failure:
            view?.updateViewState(.error(message: getErrorMessage(), retry: true))
            isLoading = false
        }
    }
    private func getErrorMessage() -> String {
        if let appName = config.appName {
            return TranslationManager.messenger.translation(forKey: "failed_to_load_conversation_with_admin").replacingOccurrences(of: "{name}", with: appName)
        }
        return TranslationManager.messenger.translation(forKey: "failed_to_load_conversation")
    }
    private func cachedConversations() -> [ICMConversation] {
        dataManager.hasReceivedConversationsResponse ? (dataManager.conversations as? [ICMConversation] ?? []) : []
    }
    @objc func reloadInbox() {
        guard let controller = view as? UIViewController,
              let top = controller.navigationController?.topViewController,
              top == controller, openCallService.state == .success else { return }
        conversationsArray = cachedConversations()
        view?.reloadTableView()
        if !conversationsArray.isEmpty {
            view?.hideErrorView()
            view?.updateViewState(.content(conversations: conversationsArray))
        }
        view?.configureNavigationBarButtons()
    }
    private func setUpInbox() {
        conversationsArray = cachedConversations()
        view?.reloadTableView()
    }
    @objc func fetchConversations() {
        let wasEmpty = cachedConversations().isEmpty
        dataManager.updateConversations(success: { [weak self] conversations, emptyState in
            guard let self else { return }
            self.queue.dispatch { [weak self] in
                guard let self else { return }
                self.conversationsArray = conversations
                self.isLoading = false
                if wasEmpty { self.setUpInbox() } else { self.reloadInbox() }
                if !self.conversationsArray.isEmpty { self.view?.updateViewState(.content(conversations: self.conversationsArray)) }
                else { self.view?.updateViewState(.empty(emptyState: emptyState)) }
            }
        }, failure: { [weak self] _ in
            guard let self else { return }
            self.queue.dispatch { [weak self] in
                guard let self else { return }
                self.isLoading = false
                if wasEmpty { self.view?.updateViewState(.error(message: self.getErrorMessage(), retry: true)) }
            }
        })
    }
    @objc func fetchPagedConversations() {
        guard let last = conversationsArray.last, let date = last.lastPart()?.createdAt else { return }
        isLoading = true
        dataManager.updateConversations(before: date, success: { [weak self] _, _ in
            guard let self else { return }
            self.queue.dispatch { [weak self] in
                guard let self else { return }
                self.conversationsArray = self.cachedConversations()
                self.view?.reloadTableView()
                self.isLoading = false
            }
        }, failure: { [weak self] _ in
            guard let self else { return }
            self.queue.dispatch { [weak self] in self?.isLoading = false }
        })
    }
    @objc func shouldFetchNextPage() -> Bool { !isLoading && dataManager.moreConversationPagesAvailable }
    @objc func numberOfSections() -> Int { dataManager.moreConversationPagesAvailable ? 2 : 1 }
    @objc func numberOfRows(inSection section: Int) -> Int {
        if section == 0 { return conversationsArray.count }
        if section == 1 { return conversationsArray.isEmpty ? 0 : 1 }
        return 0
    }
    @objc(conversationAtIndexPath:)
    func conversation(at indexPath: IndexPath) -> ICMConversation? {
        guard indexPath.section == 0 && indexPath.row < conversationsArray.count else { return nil }
        return conversationsArray[indexPath.row]
    }
    @objc func shouldShowLoadingCell(at indexPath: IndexPath) -> Bool { indexPath.section == 1 }
    func indexOfConversation(withId id: String) -> Int? {
        conversationsArray.firstIndex { conversation in
            guard let value = conversation.conversationId else { return false }
            return value == id
        }
    }
    @objc func conversationMarkedAsRead(_ id: String) {
        queue.dispatch { [weak self, id] in
            guard let self, let index = self.indexOfConversation(withId: id) else { return }
            self.conversationsArray[index].read = true
            self.view?.reloadTableViewRow(at: IndexPath(row: index, section: 0))
        }
    }
    @objc(didSelectRowAtIndexPath:)
    func didSelectRow(at indexPath: IndexPath) {
        guard indexPath.section == 0, let conversation = conversation(at: indexPath) else { return }
        if !conversation.read {
            conversation.read = true
            view?.reloadTableViewRow(at: indexPath)
        }
        view?.navigateToConversation(conversation)
        metricsService.addConversationClickedFromMessagesMetric(ticket: conversation.ticket)
    }
    @objc(willDisplayCellAtIndexPath:)
    func willDisplayCell(at indexPath: IndexPath) {
        if indexPath.section == 1 && !isLoading && dataManager.moreConversationPagesAvailable { fetchPagedConversations() }
    }
    @objc func composeMessageButtonTapped() {
        if let view { view.navigateToConversation(ICMConversation()) }
        metricsService.addNewConversationFromMessagesMetric()
    }
    @objc func shouldShowComposeButton() -> Bool { config.helpCenterRequireSearch ? false : config.canStartConversation() }
    @objc func getNavigationTitle() -> String { config.title(forSpace: 0) ?? TranslationManager.messenger.translation(forKey: "messages_space_title") }
    @objc func isTicketsSpaceEnabled() -> Bool { config.isTicketsSpaceEnabled() }
    @objc func didSelectErrorAction() {
        view?.hideErrorView()
        view?.setLoadingViewVisible(true)
        if openCallService.state == .failure { openCallService.retry() }
        else { fetchConversations() }
    }
    @objc(performActionForType:)
    func performActionFor(type: MessagesEmptyStateActionType) {
        if type.rawValue == 0 { if let view { view.navigateToConversation(ICMConversation()) } }
        else if type.rawValue == 1 { view?.presentHelpCenter() }
    }
}
