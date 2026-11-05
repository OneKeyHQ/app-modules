import Foundation

@objc(_TtC18IntercomSDKPrivate15ArticleMetaData)
class ArticleMetaData: NSObject {
    @objc let articleId: String?
    @objc let articleSource: String?
    @objc let articleTitle: String?
    @objc let createdAt: Date?
    @objc init(id: String?, source: String?, title: String?, createdAt: Date?) {
        articleId = id
        articleSource = source
        articleTitle = title
        self.createdAt = createdAt
        super.init()
    }
    @objc static var searchBrowseSource: String { "search_browse" }
    override init() { fatalError("init() has not been implemented") }
}
