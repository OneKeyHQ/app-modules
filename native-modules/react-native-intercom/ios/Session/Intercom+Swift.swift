import Foundation

extension Intercom {
    public enum Content {
        case article(id: String)
        case carousel(id: String)
        case survey(id: String)
        case helpCenterCollections(ids: [String])
        case conversation(id: String)
        case ticket(id: String)
    }

    public class func presentContent(_ content: Content) {
        let value: IntercomContent
        switch content {
        case .article(let id): value = IntercomContent.article(withId: id)
        case .carousel(let id): value = IntercomContent.carousel(withId: id)
        case .survey(let id): value = IntercomContent.survey(withId: id)
        case .helpCenterCollections(let ids): value = IntercomContent.helpCenterCollections(withIds: ids)
        case .conversation(let id): value = IntercomContent.conversation(withId: id)
        case .ticket(let id): value = IntercomContent.ticket(withId: id)
        }
        Intercom.__present(value)
    }

    public class func suppressProactiveContent(_ types: [IntercomProactiveContentType]) {
        Intercom.__suppressProactiveContent(types.map { NSNumber(value: $0.rawValue) })
    }

    public class func loginUnidentifiedUser(completion: ((Result<Void, Error>) -> Void)? = nil) {
        Intercom.__loginUnidentifiedUser(success: { completion?(.success(())) }, failure: { completion?(.failure($0)) })
    }

    public class func loginUser(with attributes: ICMUserAttributes, completion: ((Result<Void, Error>) -> Void)? = nil) {
        Intercom.__loginUser(with: attributes, success: { completion?(.success(())) }, failure: { completion?(.failure($0)) })
    }

    public class func updateUser(with attributes: ICMUserAttributes, completion: ((Result<Void, Error>) -> Void)? = nil) {
        Intercom.__updateUser(attributes, success: { completion?(.success(())) }, failure: { completion?(.failure($0)) })
    }

    public class func setUserJwtProvider(_ provider: @escaping (_ completion: @escaping (String?) -> Void) -> Void) {
        Intercom.__setUserJwtProvider(provider)
    }

    public class func setUserJwtProvider(_ provider: @escaping @MainActor () async -> String?) {
        Intercom.setUserJwtProvider { completion in
            Task { @MainActor in
                completion(await provider())
            }
        }
    }

    public class func setAuthTokens(_ authTokens: [String: String], completion: ((Result<Void, Error>) -> Void)? = nil) {
        Intercom.__setAuthTokens(authTokens, success: { completion?(.success(())) }, failure: { completion?(.failure($0)) })
    }

    public class func setDeviceToken(_ deviceToken: Data, completion: ((Result<Void, Error>) -> Void)? = nil) {
        Intercom.__setDeviceToken(deviceToken, success: { completion?(.success(())) }, failure: { completion?(.failure($0)) })
    }

    public class func fetchHelpCenterCollections(completion: @escaping (Result<[HelpCenterCollection], ICMHelpCenterDataError>) -> Void) {
        Intercom.__fetchHelpCenterCollections { result, error in
            if let result { completion(.success(result)) }
            else if let error = error as? ICMHelpCenterDataError { completion(.failure(error)) }
            else { completion(.failure(ICMHelpCenterDataError(.somethingWentWrong))) }
        }
    }

    public class func fetchHelpCenterCollection(_ collectionId: String, completion: @escaping (Result<HelpCenterCollectionContent, ICMHelpCenterDataError>) -> Void) {
        Intercom.__fetchHelpCenterCollection(collectionId) { result, error in
            if let result { completion(.success(result)) }
            else if let error = error as? ICMHelpCenterDataError { completion(.failure(error)) }
            else { completion(.failure(ICMHelpCenterDataError(.somethingWentWrong))) }
        }
    }

    public class func searchHelpCenter(_ searchTerm: String, completion: @escaping (Result<[HelpCenterArticleSearchResult], ICMHelpCenterDataError>) -> Void) {
        Intercom.__searchHelpCenter(searchTerm) { result, error in
            if let result { completion(.success(result)) }
            else if let error = error as? ICMHelpCenterDataError { completion(.failure(error)) }
            else { completion(.failure(ICMHelpCenterDataError(.somethingWentWrong))) }
        }
    }
}
