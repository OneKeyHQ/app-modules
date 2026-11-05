import Foundation

private let helpCenterDataTranslator = TranslationManager.helpCenter

@objc(_TtC18IntercomSDKPrivate21HelpCenterDataService)
class HelpCenterDataService: NSObject {
    let apiBridge: SwiftAPIBridge
    let metricService: any HelpCenterMetricServiceProtocol
    override init() {
        apiBridge = SwiftAPIBridge()
        metricService = HelpCenterMetricService()
        super.init()
    }
    @objc(fetchCollectionsWithCompletion:)
    func fetchCollections(completion: @escaping ([HelpCenterCollection]?, NSError?) -> Void) {
        metricService.addFetchCollectionsDataRequestedEvent()
        apiBridge.getCollections { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let collections):
                    let mapped = collections.map { HelpCenterCollection(collectionId: $0.id, title: $0.title, summary: $0.description, articleCount: $0.articleCount, collectionCount: $0.collectionCount) }
                    completion(mapped, nil)
                case .failure(let error):
                    let publicError = Self.helpCenterDataError(error.errorType)
                    completion(nil, publicError)
                    self.metricService.addFetchCollectionsDataFailedEvent(errorCode: error.statusCode)
                }
            }
        }
    }
    @objc(fetchCollectionWithCollectionId:completion:)
    func fetchCollection(collectionId: String, completion: @escaping (HelpCenterCollectionContent?, NSError?) -> Void) {
        metricService.addFetchCollectionContentDataRequestedEvent()
        apiBridge.getCollection(collectionId) { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let content): completion(content.publicHelpCenterCollectionContent(), nil)
                case .failure(let error):
                    let publicError = Self.helpCenterDataError(error.errorType)
                    completion(nil, publicError)
                    self.metricService.addFetchCollectionContentDataFailedEvent(errorCode: error.statusCode)
                }
            }
        }
    }
    @objc(searchResultsFor:completion:)
    func searchResults(for query: String, completion: @escaping ([HelpCenterArticleSearchResult]?, NSError?) -> Void) {
        metricService.addSearchResultsDataRequestedEvent()
        apiBridge.getSearchResults(query, source: nil) { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let results):
                    let mapped = results.map { value in
                        let snippet = value.highlight?.summary?.components(separatedBy: "<highlight>").joined(separator: "").components(separatedBy: "</highlight>").joined(separator: "")
                        return HelpCenterArticleSearchResult(articleId: value.id, title: value.title, summary: value.summary, matchingSnippet: snippet)
                    }
                    completion(mapped, nil)
                case .failure(let error):
                    let publicError = Self.helpCenterDataError(error.errorType)
                    completion(nil, publicError)
                    self.metricService.addSearchResultsFailedEvent(errorCode: error.statusCode)
                }
            }
        }
    }
    private static func helpCenterDataError(_ type: APIErrorType) -> NSError {
        let code: Int
        let message: String
        let key: String
        switch type {
        case .notFound:
            code = 1
            message = "The content does not exist or has been removed."
            key = "page_not_found"
        case .invalidResponseObject, .unknownError:
            code = 3
            message = "Something went wrong."
            key = "spaces_error_title"
        case .missingUserIdentity:
            code = 4
            message = "Requesting Help Center content requires a user to be registered."
            key = "spaces_error_title"
        case .missingAppIdentity:
            code = 5
            message = "Requesting Help Center content requires Intercom to be initialised. See Intercom Initialisation in our documentation."
            key = "spaces_error_title"
        case .serverError(let status): return networkError(status)
        case .userJwtExpired: return networkError(400)
        }
        return NSError(domain: "ICMHelpCenterDataErrorDomain", code: code, userInfo: ["Error message": message, NSLocalizedDescriptionKey: helpCenterDataTranslator.translation(forKey: key)])
    }
    private static func networkError(_ status: Int) -> NSError {
        NSError(domain: "ICMHelpCenterDataErrorDomain", code: 2, userInfo: ["Error message": "There was a network error.", NSLocalizedDescriptionKey: helpCenterDataTranslator.translation(forKey: "spaces_error_title"), "Server response code": String(status)])
    }
}
