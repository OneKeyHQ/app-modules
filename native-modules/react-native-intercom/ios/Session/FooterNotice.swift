import Foundation

@objc(ICMFooterNotice)
class FooterNotice: NSObject, DictionaryInitializable {
    @objc let title: String
    @objc let subtitle: String?
    let avatars: AvatarDetails?

    @objc(initWithDictionary:)
    required init?(withDictionary dictionary: [String: Any]) {
        guard let title = dictionary["title"] as? String else { return nil }
        self.title = title
        subtitle = dictionary["subtitle"] as? String
        if let details = dictionary["avatar_details"] as? [String: Any] { avatars = AvatarDetails(withDictionary: details) }
        else { avatars = nil }
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
}
