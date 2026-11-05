// Source recovery modifications: see docs/HELP_CENTER_RECOVERY.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate31InternalHelpCenterArticleAuthor)
class InternalHelpCenterArticleAuthor: NSObject, DictionaryInitializable {
    @objc var id: String
    @objc var displayName: String
    @objc var avatar: Avatar?
    @objc(initWithDictionary:)
    required init?(withDictionary dictionary: [String: Any]) {
        avatar = nil
        guard let id = dictionary["id"] as? String, let name = dictionary["display_name"] as? String else { return nil }
        self.id = id
        displayName = name
        if let value = dictionary["avatar"] as? [String: Any], let avatar = Avatar(withDictionary: value) { self.avatar = avatar }
        super.init()
    }
    @objc(initWithId:title:avatar:)
    init(id: String, title: String, avatar: Avatar) {
        self.id = id
        displayName = title
        self.avatar = avatar
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return other.id == id
    }
}
