import Foundation

struct FinAnswerSource: Equatable {
    enum SourceType: Equatable { case article(String), url }
    let title: String
    let sourceType: SourceType
    let url: URL?

    init?(withDictionary dictionary: [String: Any]) {
        title = dictionary["title"] as? String ?? ""
        let type = dictionary["type"] as? String
        url = (dictionary["url"] as? String).flatMap { URL(string: $0) }
        if type == "article" {
            guard let id = dictionary["id"] as? String else { return nil }
            sourceType = .article(id)
        } else { sourceType = .url }
    }
}

@objc(_TtC18IntercomSDKPrivate22FinAnswerSourceWrapper)
class FinAnswerSourceWrapper: NSObject, ITBIntent {
    let wrappedValue: FinAnswerSource

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let value = FinAnswerSource(withDictionary: dictionary) else { return nil }
        wrappedValue = value
        super.init()
    }

    @objc func intentType() -> ITBIntentType {
        if case .article = wrappedValue.sourceType { return .article }
        return .url
    }
    @objc func intentArticleId() -> String? {
        if case let .article(id) = wrappedValue.sourceType { return id }
        return nil
    }
    @objc func intentUrl() -> URL? { wrappedValue.url }
    override init() { fatalError("init() has not been implemented") }
}
