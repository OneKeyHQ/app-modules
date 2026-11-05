import Foundation

@objc(_TtC18IntercomSDKPrivate23TicketDetailsInteractor)
class TicketDetailsInteractor: NSObject, IntercomNexusObserverProtocol {
    weak var ticketUpdatingDelegate: TicketDetailsViewModel?
    let nexusClient: IntercomNexusClient
    let dataManager: any ICMDataManagerProtocol
    let ticketId: String
    let conversationId: String
    let notificationCenter: any NotificationCenterInterface
    init(nexusClient: IntercomNexusClient, dataManager: any ICMDataManagerProtocol, ticketId: String, conversationId: String, notificationCenter: any NotificationCenterInterface) {
        self.nexusClient = nexusClient
        self.dataManager = dataManager
        self.ticketId = ticketId
        self.conversationId = conversationId
        self.notificationCenter = notificationCenter
        super.init()
        dataManager.ticketToMark = ticketId
        notificationCenter.addObserver(self, selector: #selector(ticketsUpdated), name: Notification.Name("intercom-sdk-tickets-updated"), object: nil)
    }
    override init() { fatalError("init() has not been implemented") }
    deinit {
        dataManager.ticketToMark = nil
        notificationCenter.removeObserver(self)
    }
    func fetchTicket(completionHandler: @escaping (Result<Ticket, Error>) -> Void) {
        dataManager.fetchTicketDetailsForTicket(withId: ticketId, success: { [weak self] ticket in
            let manager = self?.dataManager
            manager?.markTicket(asRead: ticket!.id)
            completionHandler(.success(ticket!))
        }, failure: { completionHandler(.failure($0!)) })
    }
    @objc func ticketsUpdated() { dataManager.markTicket(asRead: ticketId) }
    @objc func eventOccurred(_ event: IntercomNexusEvent?) {
        let event = event!
        guard event.type == 6, let identifier = event.conversationId, identifier == conversationId else { return }
        fetchTicket { [weak delegate = ticketUpdatingDelegate] result in
            if case .success(let ticket) = result { delegate?.ticketDidUpdate(ticket) }
        }
    }
}
