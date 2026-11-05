// Source recovery modifications: see docs/ARTICLE_RECOVERY.md.
import Foundation

extension SwiftAPIBridge {
    func getArticle(_ articleID: String, completionHandler: @escaping (Result<ICMArticle, APIError>) -> Void) {
        httpClient.article(articleID, success: { completionHandler(.success($0)) }, error: {
            completionHandler(.failure(Self.apiError(from: $0 as NSError?)))
        })
    }
}
