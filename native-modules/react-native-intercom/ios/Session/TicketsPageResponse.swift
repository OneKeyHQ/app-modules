// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate19TicketsPageResponse)
class TicketsPageResponse: NSObject, DictionaryInitializable {
    @objc let tickets: [Ticket]
    @objc let nextPage: NSNumber?
    @objc let unreadTickets: ICMUnreadTicketsResponse?
    @objc(initWithDictionary:)
    required init?(withDictionary dictionary: [String: Any]) {
        guard let tickets = dictionary["tickets"] as? [[String: Any]] else { return nil }
        if let unread = dictionary["unread_tickets"] as? [String: Any] {
            unreadTickets = ICMUnreadTicketsResponse.instance(from: unread)
        } else { unreadTickets = nil }
        self.tickets = tickets.compactMap(Ticket.init(withDictionary:))
        nextPage = (dictionary["next_page"] as? Int).map(NSNumber.init(value:))
        if let config = dictionary["config"] as? [AnyHashable: Any] { ICMConfig.updateSharedInstance(from: config) }
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return tickets == other.tickets && nextPage == other.nextPage
    }
}
