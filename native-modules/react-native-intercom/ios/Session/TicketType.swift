import Foundation

@objc(_TtC18IntercomSDKPrivate10TicketType)
class TicketType: NSObject, NSSecureCoding {
    @objc let id: String
    @objc let name: String
    @objc let emoji: String?
    @objc let attributes: [FormAttribute]
    @objc let isArchived: Bool

    @objc static var supportsSecureCoding: Bool { true }

    @objc(initWithId:name:emoji:attributes:isArchived:)
    init(id: String, name: String, emoji: String?, attributes: [FormAttribute], isArchived: Bool) {
        self.id = id
        self.name = name
        self.emoji = emoji
        self.attributes = attributes
        self.isArchived = isArchived
        super.init()
    }

    @objc(initWithDictionary:)
    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let id = dictionary["id"] as? String,
              let name = dictionary["name"] as? String,
              let values = dictionary["attributes"] as? [[String: Any]],
              let archived = dictionary["archived"] as? Bool else { return nil }
        let emoji = dictionary["emoji"] as? String
        let attributes = values.compactMap { FormAttribute(withDictionary: $0) }
        self.init(id: id, name: name, emoji: emoji, attributes: attributes, isArchived: archived)
    }

    required convenience init?(coder: NSCoder) {
        guard let id = coder.decodeObject(of: NSString.self, forKey: "id") as String?,
              let name = coder.decodeObject(of: NSString.self, forKey: "name") as String?,
              let emoji = coder.decodeObject(of: NSString.self, forKey: "emoji") as String?,
              let attributes = coder.decodeObject(of: [NSArray.self, FormAttribute.self], forKey: "attributes") as? [FormAttribute] else { return nil }
        let archived = coder.decodeBool(forKey: "isArchived")
        self.init(id: id, name: name, emoji: emoji, attributes: attributes, isArchived: archived)
    }

    func encode(with coder: NSCoder) {
        coder.encode(id, forKey: "id")
        coder.encode(name, forKey: "name")
        coder.encode(emoji, forKey: "emoji")
        coder.encode(attributes, forKey: "attributes")
        coder.encode(isArchived, forKey: "isArchived")
    }

    override init() { fatalError("init() has not been implemented") }

    static func == (lhs: TicketType, rhs: TicketType) -> Bool {
        lhs.id == rhs.id && lhs.name == rhs.name && lhs.emoji == rhs.emoji &&
            lhs.attributes == rhs.attributes && lhs.isArchived == rhs.isArchived
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return other == self
    }
}
