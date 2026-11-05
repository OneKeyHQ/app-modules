// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc enum ReplyOptionPreActionType: Int, Codable {
    case none = 0, closeComposer
    init(from decoder: Decoder) throws {
        let value = try decoder.singleValueContainer().decode(String.self)
        self = value == "close-composer" ? .closeComposer : .none
    }
    func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(rawValue)
    }
}

@objc(_TtC18IntercomSDKPrivate11ReplyOption)
class ReplyOption: NSObject, Codable, NSCopying {
    @objc let replyText: String
    @objc let uuid: String
    @objc let preActionType: ReplyOptionPreActionType

    enum CodingKeys: String, CodingKey { case replyText = "text", uuid, preActionType = "pre_action" }

    @objc required init(replyText: String, uuid: String, preActionType: ReplyOptionPreActionType) {
        self.replyText = replyText
        self.uuid = uuid
        self.preActionType = preActionType
        super.init()
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        replyText = try container.decode(String.self, forKey: .replyText)
        uuid = try container.decode(String.self, forKey: .uuid)
        preActionType = try container.decodeIfPresent(ReplyOptionPreActionType.self, forKey: .preActionType) ?? .none
        super.init()
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(replyText, forKey: .replyText)
        try container.encode(uuid, forKey: .uuid)
        try container.encode(preActionType, forKey: .preActionType)
    }

    @objc(initWithInstanceFrom:)
    convenience init?(instanceFrom dictionary: [String: Any]) { self.init(withDictionary: dictionary) }

    @objc(initWithDictionary:)
    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let text = dictionary["text"] as? String,
              let uuid = dictionary["uuid"] as? String else { return nil }
        let action = dictionary["pre_action"] as? String ?? "none"
        self.init(replyText: text, uuid: uuid, preActionType: action == "close-composer" ? .closeComposer : .none)
    }

    override init() { fatalError("init() has not been implemented") }

    override var hash: Int {
        var hasher = Hasher()
        hasher.combine(uuid)
        hasher.combine(replyText)
        hasher.combine(preActionType.rawValue)
        return hasher.finalize()
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return replyText == other.replyText && uuid == other.uuid && preActionType == other.preActionType
    }

    func copy(with zone: NSZone? = nil) -> Any {
        type(of: self).init(replyText: replyText, uuid: uuid, preActionType: preActionType)
    }
}
