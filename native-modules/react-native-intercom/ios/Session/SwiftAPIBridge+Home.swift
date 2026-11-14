import Foundation

extension SwiftAPIBridge {
    func fetchHome(completionHandler: @escaping (Result<Home, APIError>) -> Void) {
        httpClient.fetchHome(success: { raw in
            if let dictionary = raw as? [String: Any], let home = Home(withDictionary: dictionary) {
                completionHandler(.success(home))
            } else {
                completionHandler(.failure(APIError(errorType: .invalidResponseObject, statusCode: 1002, additionalInfo: nil)))
            }
        }, failure: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
}
