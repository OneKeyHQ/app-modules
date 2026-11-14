import Foundation

@objc(_TtC18IntercomSDKPrivate26NewConversationCTAResponse)
class NewConversationCTAResponse: NSObject, NSSecureCoding {
    @objc(text) let label: String
    @objc let subtitle: String?
    @objc var icon: IntercomIcon
    @objc static var supportsSecureCoding: Bool { true }

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let label = dictionary["text"] as? String else { return nil }
        self.label = label
        subtitle = dictionary["subtitle"] as? String
        icon = .icon(from: dictionary["icon"] as? String)
        super.init()
    }

    required init?(coder: NSCoder) {
        guard let text = coder.decodeObject(of: NSString.self, forKey: "text") as String? else { return nil }
        label = text
        subtitle = coder.decodeObject(of: NSString.self, forKey: "subtitle") as String?
        icon = .icon(from: coder.decodeObject(of: NSString.self, forKey: "icon") as String?)
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(label, forKey: "text")
        coder.encode(subtitle, forKey: "subtitle")
        coder.encode(icon.stringValue, forKey: "icon")
    }

    override init() { fatalError("init() has not been implemented") }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return label == other.label && subtitle == other.subtitle && icon == other.icon
    }
}
