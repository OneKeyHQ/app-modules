// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate31NewConversationHomeCardResponse)
class NewConversationHomeCardResponse: NSObject {
    @objc(text) let label: String
    @objc let subtitle: String?
    @objc let icon: IntercomIcon
    let avatarDetails: AvatarDetails?

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let label = dictionary["text"] as? String else { return nil }
        self.label = label
        subtitle = dictionary["subtitle"] as? String
        icon = IntercomIcon.icon(from: dictionary["icon"] as? String)
        if let avatars = dictionary["avatar_details"] as? [String: Any] {
            avatarDetails = AvatarDetails(withDictionary: avatars)
        } else { avatarDetails = nil }
        super.init()
    }

    override init() { fatalError("init() has not been implemented") }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return label == other.label && subtitle == other.subtitle && icon == other.icon &&
            avatarDetails == other.avatarDetails
    }
}
