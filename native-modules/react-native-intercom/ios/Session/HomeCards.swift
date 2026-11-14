import Foundation

struct SpaceCard: HomeCard, Equatable {
    let cardType: HomeCardType
    var title: String?
    var space: [Space]
    init?(withDictionary dictionary: [String: Any]) {
        guard let raw = dictionary["type"] as? String, let type = HomeCardType(rawValue: raw), type == .spaces,
              let values = dictionary["space_items"] as? [[String: Any]] else { return nil }
        cardType = type
        title = dictionary["card_title"] as? String
        space = values.compactMap(Space.init(withDictionary:))
    }
}
struct RecentConversationsCard: HomeCard, Equatable {
    let cardType: HomeCardType
    let title: String?
    let numberOfConversationsToDisplay: Int
    let conversations: [ICMConversation]
    init?(withDictionary dictionary: [String: Any]) {
        guard let raw = dictionary["type"] as? String, let type = HomeCardType(rawValue: raw), type == .recentConversation,
              let values = dictionary["conversations"] as? [[String: Any]],
              let limit = dictionary["limit"] as? Int else { return nil }
        cardType = type
        numberOfConversationsToDisplay = limit
        conversations = values.compactMap { ICMConversation.instance(from: $0, partDisplayStyle: 1) }
        title = dictionary["card_title"] as? String
    }
}
struct RecentTicketsCard: HomeCard, Equatable {
    let cardType: HomeCardType
    let title: String?
    let tickets: [Ticket]
    init?(withDictionary dictionary: [String: Any]) {
        guard let raw = dictionary["type"] as? String, let type = HomeCardType(rawValue: raw), type == .recentTickets,
              let values = dictionary["tickets"] as? [[String: Any]] else { return nil }
        cardType = type
        tickets = values.compactMap(Ticket.init(withDictionary:))
        title = dictionary["card_title"] as? String
    }
}
struct ExternalLink: DictionaryInitializable, Equatable {
    let label: String
    let url: URL
    init?(withDictionary dictionary: [String: Any]) {
        guard let label = dictionary["label"] as? String, let href = dictionary["href"] as? String,
              let url = URL(string: href) else { return nil }
        self.label = label
        self.url = url
    }
}
struct ExternalLinksCard: HomeCard, Equatable {
    let cardType: HomeCardType
    var title: String?
    let links: [ExternalLink]
    init?(withDictionary dictionary: [String: Any]) {
        guard let raw = dictionary["type"] as? String, let type = HomeCardType(rawValue: raw), type == .externalLinks,
              let values = dictionary["links"] as? [[String: Any]] else { return nil }
        cardType = type
        title = dictionary["card_title"] as? String
        links = values.compactMap(ExternalLink.init(withDictionary:))
    }
}
struct HelpCenterCard: HomeCard, Equatable {
    let cardType: HomeCardType
    var title: String?
    let suggestedArticles: [InternalHelpCenterArticle]
    init?(withDictionary dictionary: [String: Any]) {
        guard let raw = dictionary["type"] as? String, let type = HomeCardType(rawValue: raw), type == .helpCenter,
              let values = dictionary["suggested_articles"] as? [[String: Any]] else { return nil }
        cardType = type
        title = dictionary["card_title"] as? String
        suggestedArticles = values.compactMap(InternalHelpCenterArticle.init(withDictionary:))
    }
}
struct MessengerAppCard: HomeCard, Equatable {
    let cardType: HomeCardType
    var title: String?
    let fallbackURL: URL
    init?(withDictionary dictionary: [String: Any]) {
        guard let raw = dictionary["type"] as? String, let type = HomeCardType(rawValue: raw), type == .messengerApp,
              let fallback = dictionary["fallback_url"] as? String, let url = URL(string: fallback) else { return nil }
        cardType = type
        fallbackURL = url
        title = dictionary["card_title"] as? String
    }
}
struct CreateTicketLink: DictionaryInitializable, Equatable {
    let name: String
    let iconURL: String
    let ticketType: TicketType
    init?(withDictionary dictionary: [String: Any]) {
        guard let name = dictionary["ticket_type_name"] as? String,
              let value = dictionary["ticket_type"] as? [String: Any],
              let type = TicketType(withDictionary: value) else { return nil }
        self.name = name
        ticketType = type
        iconURL = dictionary["icon_url"] as? String ?? ""
    }
}
struct CreateTicketCard: HomeCard, Equatable {
    var title: String?
    let cardType: HomeCardType
    let createTicketLinks: [CreateTicketLink]
    init?(withDictionary dictionary: [String: Any]) {
        guard let raw = dictionary["type"] as? String, let type = HomeCardType(rawValue: raw), type == .createTicket,
              let values = dictionary["links"] as? [[String: Any]] else { return nil }
        title = dictionary["card_title"] as? String
        cardType = type
        createTicketLinks = values.compactMap(CreateTicketLink.init(withDictionary:))
    }
}
