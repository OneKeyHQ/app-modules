import Foundation

@objc(_TtC18IntercomSDKPrivate25InternalHelpCenterArticle)
class InternalHelpCenterArticle: NSObject, DictionaryInitializable {
    @objc var id: String
    @objc var title: String
    @objc let url: URL?

    @objc(initWithDictionary:)
    required init?(withDictionary dictionary: [String: Any]) {
        guard let id = dictionary["id"] as? String,
              let title = dictionary["title"] as? String else { return nil }
        self.id = id
        self.title = title
        url = (dictionary["url"] as? String).flatMap(URL.init(string:))
        super.init()
    }
    @objc init(id: String, title: String, url: URL?) {
        self.id = id
        self.title = title
        self.url = url
        super.init()
    }
    override var description: String { "id: " + id + ", title: " + title }
    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? InternalHelpCenterArticle else { return false }
        return other.id == id
    }
    override init() { fatalError("init() has not been implemented") }
}
