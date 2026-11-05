import Foundation

enum Weight: String, Codable {
    case regular, bold, semibold, medium, light
    init(from decoder: Decoder) throws {
        let value = try decoder.singleValueContainer().decode(String.self)
        self = Weight(rawValue: value) ?? .regular
    }
}

struct EventContentItem: Codable {
    let text: String
    let weight: Weight?
}

@objc(_TtC18IntercomSDKPrivate9EventData)
class EventData: NSObject, Codable, DictionaryInitializable {
    @objc let eventAsPlainText: String?
    @objc let status: String?
    @objc let customStateLabel: String?
    @objc let customStatePrefix: String?
    @objc let eventDescription: String?
    let mergedConversationId: Int?
    @objc let participant: EventParticipant?
    let eventContent: [EventContentItem]?

    enum CodingKeys: String, CodingKey {
        case eventAsPlainText, status, customStateLabel, customStatePrefix, mergedConversationId, participant, eventContent
        case eventDescription = "description"
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        eventAsPlainText = try container.decodeIfPresent(String.self, forKey: .eventAsPlainText)
        status = try container.decodeIfPresent(String.self, forKey: .status)
        customStateLabel = try container.decodeIfPresent(String.self, forKey: .customStateLabel)
        customStatePrefix = try container.decodeIfPresent(String.self, forKey: .customStatePrefix)
        mergedConversationId = try container.decodeIfPresent(Int.self, forKey: .mergedConversationId)
        participant = try container.decodeIfPresent(EventParticipant.self, forKey: .participant)
        eventContent = try container.decodeIfPresent([EventContentItem].self, forKey: .eventContent)
        eventDescription = try container.decodeIfPresent(String.self, forKey: .eventDescription)
        super.init()
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encodeIfPresent(eventAsPlainText, forKey: .eventAsPlainText)
        try container.encodeIfPresent(status, forKey: .status)
        try container.encodeIfPresent(customStateLabel, forKey: .customStateLabel)
        try container.encodeIfPresent(customStatePrefix, forKey: .customStatePrefix)
        try container.encodeIfPresent(mergedConversationId, forKey: .mergedConversationId)
        try container.encodeIfPresent(participant, forKey: .participant)
        try container.encodeIfPresent(eventContent, forKey: .eventContent)
        try container.encodeIfPresent(eventDescription, forKey: .eventDescription)
    }

    @objc(initWithInstanceFrom:)
    convenience init?(instanceFrom dictionary: [String: Any]) { self.init(withDictionary: dictionary) }

    @objc var isValidEvent: Bool {
        if let text = eventAsPlainText, text.count > 0 { return true }
        return eventContent != nil
    }
    override init() { fatalError("init() has not been implemented") }
}

@objc(_TtC18IntercomSDKPrivate16EventParticipant)
class EventParticipant: NSObject, Codable, DictionaryInitializable {
    @objc let avatar: Avatar?
    @objc let initial: String?
    @objc let label: String?
    @objc let type: String?
    @objc let participantId: String?
    @objc var isGroupParticipant = false

    enum CodingKeys: String, CodingKey { case avatar, initial, label, type, participantId = "id" }

    @objc init(avatar: Avatar?, initial: String?, label: String?, type: String?, participantId: String?) {
        self.avatar = avatar
        self.initial = initial
        self.label = label
        self.type = type
        self.participantId = participantId
        super.init()
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        avatar = try container.decodeIfPresent(Avatar.self, forKey: .avatar)
        initial = try container.decodeIfPresent(String.self, forKey: .initial)
        label = try container.decodeIfPresent(String.self, forKey: .label)
        type = try container.decodeIfPresent(String.self, forKey: .type)
        participantId = try container.decodeIfPresent(String.self, forKey: .participantId)
        super.init()
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encodeIfPresent(avatar, forKey: .avatar)
        try container.encodeIfPresent(initial, forKey: .initial)
        try container.encodeIfPresent(label, forKey: .label)
        try container.encodeIfPresent(type, forKey: .type)
        try container.encodeIfPresent(participantId, forKey: .participantId)
    }
    @objc(initWithInstanceFrom:)
    convenience init?(instanceFrom dictionary: [String: Any]) { self.init(withDictionary: dictionary) }
    override init() { fatalError("init() has not been implemented") }
}
