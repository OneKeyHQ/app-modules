import Foundation

@objc(_TtC18IntercomSDKPrivate27CollapsedConversationHeader)
class CollapsedConversationHeader: NSObject {
    @objc let title: String
    @objc let subtitle: String?
    @objc let icon: IntercomIcon
    let avatarDetails: AvatarDetails?

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let title = dictionary["title"] as? String else { return nil }
        let avatars = dictionary["avatar_details"] as? [String: Any] ?? [:]
        self.title = title
        subtitle = dictionary["subtitle"] as? String
        avatarDetails = AvatarDetails(withDictionary: avatars)
        icon = IntercomIcon.icon(from: dictionary["icon"] as? String)
        super.init()
    }

    override init() { fatalError("init() has not been implemented") }

    override var hash: Int {
        var hasher = Hasher()
        hasher.combine(title)
        hasher.combine(subtitle)
        hasher.combine(avatarDetails)
        hasher.combine(icon.rawValue)
        return hasher.finalize()
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return title == other.title && subtitle == other.subtitle &&
            avatarDetails == other.avatarDetails && icon == other.icon
    }
}
