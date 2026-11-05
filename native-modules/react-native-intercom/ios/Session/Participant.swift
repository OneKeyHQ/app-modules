// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate11Participant)
class Participant: NSObject, Codable, DictionaryInitializable {
    @objc let avatar: Avatar?
    @objc let email: String?
    @objc let id: String?
    @objc let name: String?
    @objc let firstName: String?
    @objc let isBot: Bool
    @objc let isAdmin: Bool
    @objc let isGroupParticipant: Bool

    enum CodingKeys: String, CodingKey {
        case avatar, email, id, name, firstName, isBot, isAdmin = "type"
    }

    @objc init(avatar: Avatar?, email: String?, id: String?, name: String?, firstName: String?,
               isAdmin: Bool, isBot: Bool, isGroupParticipant: Bool) {
        self.avatar = avatar
        self.email = email
        self.id = id
        self.name = name
        self.firstName = firstName ?? name?.split(separator: " ").first.map(String.init)
        self.isBot = isBot
        self.isAdmin = isAdmin
        self.isGroupParticipant = isGroupParticipant
        super.init()
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        avatar = try container.decodeIfPresent(Avatar.self, forKey: .avatar)
        email = try container.decodeIfPresent(String.self, forKey: .email)
        id = try container.decodeIfPresent(String.self, forKey: .id)
        name = try container.decodeIfPresent(String.self, forKey: .name)
        let givenFirstName = try container.decodeIfPresent(String.self, forKey: .firstName)
        isAdmin = try container.decodeIfPresent(String.self, forKey: .isAdmin)?.lowercased() == "admin"
        firstName = givenFirstName ?? name?.split(separator: " ").first.map(String.init)
        isBot = try container.decodeIfPresent(Bool.self, forKey: .isBot) ?? false
        isGroupParticipant = false
        super.init()
    }

    @objc func adminAppNameTitle(_ attributes: [NSAttributedString.Key: Any], appName: String) -> NSAttributedString? {
        guard let firstName = firstName else { return nil }
        let manager = TranslationManager.messenger
        let params = ["name": firstName, "company": appName]
        var text = manager.translation(forKey: "teammate_from_company")
        for (key, value) in params {
            text = text.replacingOccurrences(of: "{" + key + "}", with: value)
        }
        return NSAttributedString(string: text, attributes: attributes)
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encodeIfPresent(avatar, forKey: .avatar)
        try container.encodeIfPresent(email, forKey: .email)
        try container.encodeIfPresent(id, forKey: .id)
        try container.encodeIfPresent(name, forKey: .name)
        try container.encodeIfPresent(firstName, forKey: .firstName)
        try container.encode(isBot, forKey: .isBot)
        try container.encode(isAdmin, forKey: .isAdmin)
    }

    @objc(initWith:)
    convenience init?(with dictionary: [String: Any]) { self.init(withDictionary: dictionary) }

    @objc(initWith:isGroupParticipant:)
    convenience init?(with dictionary: [String: Any], isGroupParticipant: Bool) {
        let avatar = (dictionary["avatar"] as? [String: Any]).flatMap { Avatar(withDictionary: $0) }
        self.init(avatar: avatar, email: dictionary["email"] as? String, id: dictionary["id"] as? String,
                  name: dictionary["name"] as? String, firstName: dictionary["firstName"] as? String,
                  isAdmin: dictionary["type"] as? String == "admin", isBot: dictionary["isBot"] as? Bool ?? false,
                  isGroupParticipant: isGroupParticipant)
    }

    override init() { fatalError("init() has not been implemented") }

    override var hash: Int {
        var hasher = Hasher()
        hasher.combine(id)
        hasher.combine(email)
        hasher.combine(isAdmin)
        hasher.combine(isBot)
        hasher.combine(isGroupParticipant)
        return hasher.finalize()
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Participant else { return false }
        return avatar == other.avatar && email == other.email && id == other.id && name == other.name &&
            firstName == other.firstName && isAdmin == other.isAdmin && isBot == other.isBot &&
            isGroupParticipant == other.isGroupParticipant
    }
}
