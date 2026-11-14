import Foundation

enum HomeCardType: String {
    case spaces = "spaces"
    case recentConversation = "recent_conversation"
    case recentTickets = "recent_tickets"
    case newConversation = "new_conversation"
    case externalLinks = "external_links"
    case helpCenter = "help_center"
    case messengerApp = "messenger_app"
    case createTicket = "ticket_links"
}

// The second original getter was WMO deleted; this is the proven default functional surface.
protocol HomeCard: DictionaryInitializable {
    var cardType: HomeCardType { get }
}

struct Home: DictionaryInitializable, Equatable {
    var cards: [any HomeCard]
    init?(withDictionary dictionary: [String: Any]) {
        guard let values = dictionary["cards"] as? [[String: Any]] else { return nil }
        cards = values.compactMap { value -> (any HomeCard)? in
            guard let raw = value["type"] as? String, let type = HomeCardType(rawValue: raw) else { return nil }
            switch type {
            case .spaces: return SpaceCard(withDictionary: value)
            case .recentConversation: return RecentConversationsCard(withDictionary: value)
            case .recentTickets: return RecentTicketsCard(withDictionary: value)
            case .newConversation: return NewConversationCard(withDictionary: value)
            case .externalLinks: return ExternalLinksCard(withDictionary: value)
            case .helpCenter: return HelpCenterCard(withDictionary: value)
            case .messengerApp: return MessengerAppCard(withDictionary: value)
            case .createTicket: return CreateTicketCard(withDictionary: value)
            }
        }
    }
    static func == (lhs: Home, rhs: Home) -> Bool {
        if lhs.cards.count != rhs.cards.count { return true }
        for index in lhs.cards.indices { if lhs.cards[index].cardType != rhs.cards[index].cardType { return false } }
        return true
    }
    mutating func update(cardAtIndex index: Int, with card: any HomeCard) {
        if index < 0 || index >= cards.count { return }
        cards[index] = card
    }
}
