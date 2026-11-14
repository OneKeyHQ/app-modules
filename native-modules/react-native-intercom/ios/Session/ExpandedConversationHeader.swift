import Foundation

@objc(_TtC18IntercomSDKPrivate26ExpandedConversationHeader)
class ExpandedConversationHeader: NSObject {
    @objc let title: String
    let avatarDetails: AvatarDetails?
    let body: [ConversationHeaderContent]
    @objc let calendarUrl: String?
    let footer: [ConversationHeaderContent]

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let title = dictionary["title"] as? String else { return nil }
        let avatars = dictionary["avatar_details"] as? [String: Any] ?? [:]
        let bodyDictionaries = dictionary["body"] as? [[String: Any]] ?? []
        let footerDictionaries = dictionary["footer"] as? [[String: Any]] ?? []
        self.title = title
        avatarDetails = AvatarDetails(withDictionary: avatars)
        body = bodyDictionaries.compactMap { ConversationHeaderContent(withDictionary: $0) }
        calendarUrl = dictionary["calendar_url"] as? String
        footer = footerDictionaries.compactMap { ConversationHeaderContent(withDictionary: $0) }
        super.init()
    }

    override init() { fatalError("init() has not been implemented") }

    override var hash: Int {
        var hasher = Hasher()
        hasher.combine(title)
        hasher.combine(avatarDetails)
        hasher.combine(body)
        hasher.combine(calendarUrl)
        hasher.combine(footer)
        return hasher.finalize()
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return title == other.title && avatarDetails == other.avatarDetails && body == other.body &&
            calendarUrl == other.calendarUrl && footer == other.footer
    }
}
