import Foundation

@objc(_TtC18IntercomSDKPrivate6Ticket)
class Ticket: NSObject, DictionaryInitializable {
    let statusList: [TicketStatus]
    @objc let id: String
    @objc let publicTicketId: String?
    @objc let title: String
    @objc let ticketDescription: String?
    @objc let iconURL: String?
    let currentStatus: TicketStatus
    @objc let attributes: [FormAttribute]
    @objc let assignee: Participant?
    @objc let typeId: Int
    @objc let conversationId: String
    let conversationButtonInfo: ConversationButtonInfo?
    @objc var read: Bool
    @objc let latestStatusUpdatedAt: Date

    @objc(initWithDictionary:)
    required init?(withDictionary dictionary: [String: Any]) {
        guard let id = dictionary["id"] as? String,
              let title = dictionary["title"] as? String,
              let rawStatus = dictionary["current_status"] as? [String: Any],
              let currentStatus = TicketStatus(withDictionary: rawStatus),
              let rawStatuses = dictionary["status_list"] as? [[String: Any]],
              let rawAttributes = dictionary["attributes"] as? [[String: Any]],
              let typeId = dictionary["ticket_type_id"] as? Int,
              let conversationId = dictionary["conversation_id"] as? String,
              let rawDate = dictionary["latest_status_updated_at"] as? String,
              let timestamp = Double(rawDate) else { return nil }
        let statusList = Self.createStatusList(from: rawStatuses)
        let attributes = Self.createAttributesList(from: rawAttributes)
        guard !attributes.isEmpty else { return nil }
        let assignee = (dictionary["assignee"] as? [String: Any]).flatMap(Participant.init(withDictionary:))
        let button = (dictionary["conversation_button"] as? [String: Any]).flatMap(ConversationButtonInfo.init(withDictionary:))
        self.statusList = statusList
        self.id = id
        publicTicketId = dictionary["public_ticket_id"] as? String
        self.title = title
        ticketDescription = dictionary["description"] as? String
        iconURL = dictionary["icon_url"] as? String
        self.currentStatus = currentStatus
        self.attributes = attributes
        self.assignee = assignee
        self.typeId = typeId
        self.conversationId = conversationId
        conversationButtonInfo = button
        read = dictionary["read"] as? Bool ?? true
        latestStatusUpdatedAt = Date(timeIntervalSince1970: timestamp)
        super.init()
    }

    init(id: String, publicTicketId: String?, title: String, ticketDescription: String?, iconURL: String?, currentStatus: TicketStatus, statusList: [TicketStatus], attributes: [FormAttribute], assignee: Participant?, ticketTypeId: Int, conversationId: String, conversationButtonInfo: ConversationButtonInfo?, read: Bool, latestStatusUpdatedAt: Date) {
        self.statusList = statusList
        self.id = id
        self.publicTicketId = publicTicketId
        self.title = title
        self.ticketDescription = ticketDescription
        self.iconURL = iconURL
        self.currentStatus = currentStatus
        self.attributes = attributes
        self.assignee = assignee
        typeId = ticketTypeId
        self.conversationId = conversationId
        self.conversationButtonInfo = conversationButtonInfo
        self.read = read
        self.latestStatusUpdatedAt = latestStatusUpdatedAt
        super.init()
    }

    @objc(createAttributesListFrom:)
    static func createAttributesList(from dictionaries: [[String: Any]]) -> [FormAttribute] {
        dictionaries.compactMap { FormAttribute(ticketAttribute: $0) }
    }

    static func createStatusList(from dictionaries: [[String: Any]]) -> [TicketStatus] {
        var seenCurrent = false
        return dictionaries.compactMap { dictionary in
            guard let title = dictionary["title"] as? String,
                  let current = dictionary["is_current_status"] as? Bool,
                  let type = dictionary["type"] as? String else { return nil }
            let created = (dictionary["created_date"] as? String).flatMap(Int.init).map { Date(timeIntervalSince1970: Double($0)) }
            let detail = dictionary["status_detail"] as? String
            let incomplete = seenCurrent
            if current { seenCurrent = true }
            return TicketStatus(title: title, isCurrentStatus: current, stage: TicketStatus.Stage(rawValue: type) ?? .submitted, incomplete: incomplete, slaDetails: nil, createdDate: created, statusDetail: detail)
        }
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return self == other
    }

    static func == (lhs: Ticket, rhs: Ticket) -> Bool {
        lhs.id == rhs.id && lhs.publicTicketId == rhs.publicTicketId && lhs.title == rhs.title &&
        lhs.ticketDescription == rhs.ticketDescription && lhs.iconURL == rhs.iconURL &&
        lhs.currentStatus == rhs.currentStatus && lhs.statusList == rhs.statusList &&
        lhs.attributes == rhs.attributes && lhs.assignee == rhs.assignee &&
        lhs.conversationId == rhs.conversationId && lhs.conversationButtonInfo == rhs.conversationButtonInfo && lhs.read == rhs.read
    }

    override init() { fatalError("init() has not been implemented") }
}
