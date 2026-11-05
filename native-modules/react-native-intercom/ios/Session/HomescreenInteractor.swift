import Foundation

enum HomescreeError: Error { case serverError, somethingWentWrong }

// The default bridge is concrete; optimized private injection requirements remain unrecovered.
@objc(_TtC18IntercomSDKPrivate20HomescreenInteractor)
class HomescreenInteractor: NSObject {
    let dataManager: any ICMDataManagerProtocol
    private let apiBridge: SwiftAPIBridge
    private let config: any ICMConfigProtocol
    init(apiBridge: SwiftAPIBridge, dataManager: any ICMDataManagerProtocol, config: any ICMConfigProtocol) {
        self.apiBridge = apiBridge
        self.dataManager = dataManager
        self.config = config
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
    var poweredByForConversation: PoweredBy? { dataManager.poweredBy }
    func fetchHome(completionHandler: ((Result<Home, HomescreeError>) -> Void)?) {
        apiBridge.fetchHome { result in
            switch result {
            case .success(let home): completionHandler?(.success(home))
            case .failure(let error): completionHandler?(.failure(HomescreenInteractor.homescreenError(error)))
            }
        }
    }
    private static func homescreenError(_ error: APIError) -> HomescreeError {
        switch error.errorType {
        case .serverError, .userJwtExpired: return .serverError
        default: return .somethingWentWrong
        }
    }
    func homescreenErrorFrom(error: Error?) -> HomescreeError { HomescreenInteractor.homescreenError(SwiftAPIBridge.apiError(from: error as NSError?)) }
    func getNewConversationCard() -> NewConversationCard? { dataManager.conversationCard }
    func getUnreadConversationsCount() -> Int { dataManager.unreadManager.unreadConversationIds.count }
    func hasRecentConversations(count: Int) -> Bool {
        let array = [ICMConversation]._unconditionallyBridgeFromObjectiveC(dataManager.getRecentConversations(count) as NSArray?)
        return !array.isEmpty
    }
    func hasRecentTickets() -> Bool {
        let array = [Ticket]._unconditionallyBridgeFromObjectiveC(dataManager.getRecentTickets() as NSArray?)
        return !array.isEmpty
    }
    func hasUnreadTickets() -> Bool {
        guard let tickets = dataManager.tickets as? [Ticket], !tickets.isEmpty else { return false }
        for ticket in tickets { if !ticket.read { return true } }
        return false
    }
    func updateDataManager(home: Home) {
        updateDataManagerWithRecentConversations(home: home)
        updateDataManagerWithRecentTickets(home: home)
    }
    private func updateDataManagerWithRecentConversations(home: Home) {
        guard let first = home.cards.first(where: { $0.cardType == .recentConversation }) as? RecentConversationsCard else { return }
        for conversation in first.conversations { dataManager.update(with: conversation) }
    }
    private func updateDataManagerWithRecentTickets(home: Home) {
        guard let first = home.cards.first(where: { $0.cardType == .recentTickets }) as? RecentTicketsCard else { return }
        for ticket in first.tickets { dataManager.update(with: ticket) }
    }
}
