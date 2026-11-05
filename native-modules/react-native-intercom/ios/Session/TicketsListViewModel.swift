// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import Foundation
import os

enum TicketsListViewState: Hashable { case loading, success, error, empty }

@objc(_TtC18IntercomSDKPrivate20TicketsListViewModel)
class TicketsListViewModel: NSObject {
    var currentState: TicketsListViewState = .loading {
        didSet { if let view, view.isCurrentlyShown { view.render(currentState) } }
    }
    weak var view: TicketsListViewController?
    let interactor: TicketsListInteractor
    let dataManager: any ICMDataManagerProtocol
    let notificationCenter: any NotificationCenterInterface
    let metricService: any MessagesMetricServiceProtocol
    let config: any ICMConfigProtocol
    let openCallService: any OpenCallServiceProtocol
    var tickets: [Ticket] = []
    var fetchRequestInProgress = false

    init(view: TicketsListViewController, interactor: TicketsListInteractor, dataManager: any ICMDataManagerProtocol, notificationCenter: any NotificationCenterInterface, metricService: any MessagesMetricServiceProtocol, config: any ICMConfigProtocol, openCallService: any OpenCallServiceProtocol) {
        self.view = view
        self.interactor = interactor
        self.dataManager = dataManager
        self.notificationCenter = notificationCenter
        self.metricService = metricService
        self.config = config
        self.openCallService = openCallService
        super.init()
        notificationCenter.addObserver(self, selector: #selector(ticketsDidUpdate), name: Notification.Name("intercom-sdk-tickets-updated"), object: nil)
        notificationCenter.addObserver(self, selector: #selector(openCallStateDidChange(_:)), name: Notification.Name("IntercomOpenCallStateDidChangeNotification"), object: nil)
    }
    override init() { fatalError("init() has not been implemented") }
    deinit { notificationCenter.removeObserver(self) }
    func setViewTitle() {
        guard let view else { return }
        let title = config.title(forSpace: 2) ?? TranslationManager.messenger.translation(forKey: "tickets_space_title")
        view.setViewTitle(title)
    }
    func fetchTickets(fromPage page: Int) {
        if fetchRequestInProgress { return }
        tickets = interactor.dataManager.tickets as? [Ticket] ?? []
        currentState = tickets.isEmpty ? .loading : .success
        fetchRequestInProgress = true
        interactor.fetchTickets(fromPage: page) { [weak self] result in
            self?.fetchRequestInProgress = false
            if case .failure(let error) = result {
                self?.currentState = .error
                Self.logError(error.errorType)
            }
        }
    }
    @objc func ticketsDidUpdate() {
        tickets = interactor.dataManager.tickets as? [Ticket] ?? []
        currentState = tickets.isEmpty ? .empty : .success
    }
    @objc(openCallStateDidChange:)
    func openCallStateDidChange(_ notification: Notification) { handleOpenCallState() }
    func handleOpenCallState() {
        switch openCallService.state {
        case .loading: break
        case .failure: currentState = .error
        case .success:
            fetchTickets(fromPage: 1)
            metricService.addTicketsViewedMetric()
        }
    }
    func errorViewReloadButtonTapped() {
        currentState = .loading
        if openCallService.state == .failure { openCallService.retry() }
        else { fetchTickets(fromPage: 1) }
    }
    func didSelectRow(at index: Int) {
        guard index < tickets.count, let view else { return }
        view.showTicket(tickets[index])
    }
    private static func logError(_ error: APIErrorType) {
        let message: String
        switch error {
        case .serverError(let code): message = "A server error occured. " + String(code)
        case .invalidResponseObject: message = "An error occured. Please inform Intercom with the following error code: 1002"
        case .notFound: message = "The content does not exist or has been removed."
        case .userJwtExpired: message = "Loading tickets failed because the user JWT has expired. Call setUserJwt: with a fresh token."
        case .missingAppIdentity: message = "Displaying tickets requires Intercom to be initialised. See Intercom Initialisation in our documentation."
        case .missingUserIdentity: message = "Displaying tickets requires a user to be logged in."
        case .unknownError: message = "An unknown error occurred."
        }
        ICMLogger.log(message, type: .error)
    }
}
