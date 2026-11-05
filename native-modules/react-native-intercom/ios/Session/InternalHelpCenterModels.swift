import Foundation

struct InternalHelpCenterCollection: DictionaryInitializable, Equatable {
    let id: String
    let title: String
    let description: String?
    let articleCount: Int
    let collectionCount: Int
    init?(withDictionary dictionary: [String: Any]) {
        guard let id = dictionary["id"] as? String, let title = dictionary["name"] as? String else { return nil }
        self.id = id
        self.title = title
        description = dictionary["description"] as? String
        articleCount = dictionary["article_count"] as? Int ?? 0
        collectionCount = dictionary["collection_count"] as? Int ?? 0
    }
}
struct InternalHelpCenterArticleSearchResultHighlight: DictionaryInitializable, Equatable {
    let title: String?
    let summary: String?
    init?(withDictionary dictionary: [String: Any]) {
        title = dictionary["title"] as? String
        summary = dictionary["summary"] as? String
    }
}
struct InternalHelpCenterArticleSearchResult: DictionaryInitializable, Equatable {
    let id: String
    let title: String
    let summary: String?
    let highlight: InternalHelpCenterArticleSearchResultHighlight?
    init?(withDictionary dictionary: [String: Any]) {
        guard let id = dictionary["id"] as? String else { return nil }
        self.id = id
        title = dictionary["title"] as? String ?? ""
        summary = dictionary["summary"] as? String
        if let value = dictionary["highlight"] as? [String: String] {
            highlight = InternalHelpCenterArticleSearchResultHighlight(withDictionary: value)
        } else { highlight = nil }
    }
}
struct InternalHelpCenterCollectionContent: DictionaryInitializable, Equatable {
    let id: String
    let title: String
    let description: String?
    let articles: [InternalHelpCenterArticle]
    let articleCount: Int
    let authors: [InternalHelpCenterArticleAuthor]
    let collections: [InternalHelpCenterCollection]
    init?(withDictionary dictionary: [String: Any]) {
        guard let id = dictionary["id"] as? String, let title = dictionary["name"] as? String else { return nil }
        self.id = id
        self.title = title
        description = dictionary["description"] as? String
        let rawArticles = dictionary["articles"] as? [[String: Any]] ?? []
        let rawAuthors = dictionary["authors"] as? [[String: Any]] ?? []
        articles = rawArticles.compactMap(InternalHelpCenterArticle.init(withDictionary:))
        articleCount = dictionary["article_count"] as? Int ?? 0
        authors = rawAuthors.compactMap(InternalHelpCenterArticleAuthor.init(withDictionary:))
        collections = (dictionary["collections"] as? [[String: Any]] ?? []).compactMap(InternalHelpCenterCollection.init(withDictionary:))
    }
    func publicHelpCenterCollectionContent() -> HelpCenterCollectionContent {
        let articles = self.articles.map { HelpCenterArticle(articleId: $0.id, title: $0.title) }
        let collections = self.collections.map { HelpCenterCollection(collectionId: $0.id, title: $0.title, summary: $0.description, articleCount: $0.articleCount, collectionCount: $0.collectionCount) }
        let authors = self.authors.map { HelpCenterArticleAuthor(authorId: $0.id, displayName: $0.displayName, avatarURL: $0.avatar?.imageUrl) }
        return HelpCenterCollectionContent(collectionId: id, title: title, summary: description, articles: articles, articleCount: articleCount, collections: collections, authors: authors)
    }
}
