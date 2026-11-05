import Foundation

extension SwiftAPIBridge {
    func getCollections(completionHandler: @escaping (Result<[InternalHelpCenterCollection], APIError>) -> Void) {
        httpClient.getHelpCenterCollections({ raw in
            guard let dictionaries = raw as? [[String: Any]] else {
                completionHandler(.failure(APIError(errorType: .invalidResponseObject, statusCode: 1002, additionalInfo: nil)))
                return
            }
            completionHandler(.success(dictionaries.compactMap(InternalHelpCenterCollection.init(withDictionary:))))
        }, error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
    func getCollection(_ identifier: String, completionHandler: @escaping (Result<InternalHelpCenterCollectionContent, APIError>) -> Void) {
        httpClient.getHelpCenterCollection(identifier, success: { raw in
            guard let dictionary = raw as? [String: Any], let content = InternalHelpCenterCollectionContent(withDictionary: dictionary) else {
                completionHandler(.failure(APIError(errorType: .invalidResponseObject, statusCode: 1002, additionalInfo: nil)))
                return
            }
            completionHandler(.success(content))
        }, error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
    func getSearchResults(_ query: String, source: String?, completionHandler: @escaping (Result<[InternalHelpCenterArticleSearchResult], APIError>) -> Void) {
        httpClient.getHelpCenterSearchResults(query, source: source, success: { raw in
            guard let dictionaries = raw as? [[String: Any]] else {
                completionHandler(.failure(APIError(errorType: .invalidResponseObject, statusCode: 1002, additionalInfo: nil)))
                return
            }
            completionHandler(.success(dictionaries.compactMap(InternalHelpCenterArticleSearchResult.init(withDictionary:))))
        }, error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
}
