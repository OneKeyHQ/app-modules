import Foundation

enum HelpCenterError: Error, Hashable {
    case serverError, contentNotAvailable, somethingWentWrong, openCallFailed
    var message: String {
        TranslationManager.helpCenter.translation(forKey: self == .contentNotAvailable ? "no_articles_to_display" : "spaces_error_title")
    }
}
struct HelpCenterErrorInfo: Error {
    let errorType: HelpCenterError
    let statusCode: Int
}
enum FilteredHelpCenterContent {
    case filteredHelpCenter([InternalHelpCenterCollection])
    case fullHelpCenter([InternalHelpCenterCollection])
}

// Original private four-requirement injection protocol was WMO-deleted; default concrete bridge is real.
struct HelpCenterService {
    let apiBridge: SwiftAPIBridge

    func fetchCollections(completionHandler: @escaping (Result<[InternalHelpCenterCollection], HelpCenterErrorInfo>) -> Void) {
        apiBridge.getCollections { completionHandler(Self.convert($0)) }
    }
    func fetchCollections(_ identifiers: [String], completionHandler: @escaping (Result<FilteredHelpCenterContent, HelpCenterErrorInfo>) -> Void) {
        fetchCollections { result in
            switch result {
            case .failure(let error): completionHandler(.failure(error))
            case .success(let collections):
                let filtered = collections.filter { identifiers.contains($0.id) }
                if filtered.isEmpty { completionHandler(.success(.fullHelpCenter(collections))) }
                else { completionHandler(.success(.filteredHelpCenter(filtered))) }
            }
        }
    }
    func fetchCollection(_ identifier: String, completionHandler: @escaping (Result<InternalHelpCenterCollectionContent, HelpCenterErrorInfo>) -> Void) {
        apiBridge.getCollection(identifier) { completionHandler(Self.convert($0)) }
    }
    func fetchSearchResults(for query: String, source: String?, completionHandler: @escaping (Result<[InternalHelpCenterArticleSearchResult], HelpCenterErrorInfo>) -> Void) {
        apiBridge.getSearchResults(query, source: source) { completionHandler(Self.convert($0)) }
    }
    private static func convert<Value>(_ result: Result<Value, APIError>) -> Result<Value, HelpCenterErrorInfo> {
        switch result {
        case .success(let value): return .success(value)
        case .failure(let error):
            log(error.errorType)
            let type: HelpCenterError
            switch error.errorType {
            case .serverError, .userJwtExpired: type = .serverError
            case .notFound: type = .contentNotAvailable
            default: type = .somethingWentWrong
            }
            return .failure(HelpCenterErrorInfo(errorType: type, statusCode: error.statusCode))
        }
    }
    private static func log(_ error: APIErrorType) {
        let message: String
        switch error {
        case .serverError(let code): message = "A server error occured. " + String(code)
        case .invalidResponseObject: message = "An error occured. Please inform Intercom with the following error code: 1002"
        case .notFound: message = "The content does not exist or has been removed."
        case .userJwtExpired: message = "Loading Help Center content failed because the user JWT has expired. Call setUserJwt: with a fresh token."
        case .missingAppIdentity: message = "Displaying Help Center content requires Intercom to be initialised. See Intercom Initialisation in our documentation."
        case .missingUserIdentity: message = "Displaying Help Center content requires a user to be logged in."
        case .unknownError: message = "An unknown error occurred."
        }
        ICMLogger.log(message, type: .error)
    }
}
