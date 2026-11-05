import Foundation

@objc(_TtC18IntercomSDKPrivate21TicketsListInteractor)
class TicketsListInteractor: NSObject {
    let dataManager: any ICMDataManagerProtocol
    init(dataManager: any ICMDataManagerProtocol) { self.dataManager = dataManager; super.init() }
    override init() { fatalError("init() has not been implemented") }
    func fetchTickets(fromPage page: Int, completionHandler: @escaping (Result<Void, APIError>) -> Void) {
        dataManager.fetchTickets(fromPage: page, success: { raw in
            let tickets = raw as! [Ticket]
            withExtendedLifetime(tickets) { completionHandler(.success(())) }
        }, failure: { completionHandler(.failure(SwiftAPIBridge.apiError(from: $0 as NSError?))) })
    }
}
