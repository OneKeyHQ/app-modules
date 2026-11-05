import Foundation

@objc(_TtC18IntercomSDKPrivate18ParticipatingAdmin)
class ParticipatingAdmin: Participant {
    @objc let lastActiveAt: Date?
    @objc let isActive: Bool
    @objc let jobTitle: String?

    enum AdminCodingKeys: String, CodingKey {
        case avatar, email, id, name, firstName, type, isBot, isActive, lastActiveAt, jobTitle
    }

    @objc init(avatar: Avatar?, email: String?, id: String?, name: String?, firstName: String?,
               isAdmin: Bool, isBot: Bool, isGroupParticipant: Bool, lastActiveAt: Date?, isActive: Bool, jobTitle: String?) {
        self.lastActiveAt = lastActiveAt
        self.isActive = isActive
        self.jobTitle = jobTitle
        super.init(avatar: avatar, email: email, id: id, name: name, firstName: firstName,
                   isAdmin: isAdmin, isBot: isBot, isGroupParticipant: isGroupParticipant)
    }

    override init(avatar: Avatar?, email: String?, id: String?, name: String?, firstName: String?,
                  isAdmin: Bool, isBot: Bool, isGroupParticipant: Bool) {
        fatalError("init(avatar:email:id:name:firstName:isAdmin:isBot:isGroupParticipant:) has not been implemented")
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: AdminCodingKeys.self)
        lastActiveAt = try container.decodeIfPresent(Double.self, forKey: .lastActiveAt).map { Date(timeIntervalSince1970: $0) }
        isActive = try container.decodeIfPresent(Bool.self, forKey: .isActive) ?? false
        jobTitle = try container.decodeIfPresent(String.self, forKey: .jobTitle)
        try super.init(from: decoder)
    }

    @objc(initWith:)
    convenience init?(with dictionary: [String: Any]) { self.init(withDictionary: dictionary) }

    @objc(initWith:isGroupParticipant:)
    convenience init?(with dictionary: [String: Any], isGroupParticipant: Bool) {
        let avatar = (dictionary["avatar"] as? [String: Any]).flatMap { Avatar(withDictionary: $0) }
        let email = dictionary["email"] as? String
        let id = dictionary["id"] as? String
        let name = dictionary["name"] as? String
        let firstName = dictionary["firstName"] as? String
        let admin = (dictionary["type"] as? String)?.lowercased() == "admin"
        let bot = dictionary["isBot"] as? Bool ?? false
        let lastActiveAt = (dictionary["last_active_at"] as? Int).map { Date(timeIntervalSince1970: Double($0)) }
        let active = dictionary["is_active"] as? Bool ?? false
        let jobTitle = dictionary["job_title"] as? String
        self.init(avatar: avatar, email: email, id: id, name: name, firstName: firstName,
                  isAdmin: admin, isBot: bot, isGroupParticipant: isGroupParticipant,
                  lastActiveAt: lastActiveAt, isActive: active, jobTitle: jobTitle)
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return super.isEqual(other) && lastActiveAt == other.lastActiveAt && isActive == other.isActive && jobTitle == other.jobTitle
    }

    @objc(isEqualToParticipantingAdmin:)
    func isEqualToParticipantingAdmin(_ other: ParticipatingAdmin) -> Bool {
        super.isEqual(other) && lastActiveAt == other.lastActiveAt && isActive == other.isActive && jobTitle == other.jobTitle
    }
}
