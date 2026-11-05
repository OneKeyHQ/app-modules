// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import Foundation

// Concrete Survey behavior; the original thirty-requirement private injection protocol remains separately recorded.
struct SwiftAPIBridge {
    let httpClient: ICMHTTPClient.Type
    let persistentStore: ICMPersistentStore.Type

    init(httpClient: ICMHTTPClient.Type = ICMHTTPClient.self, persistentStore: ICMPersistentStore.Type = ICMPersistentStore.self) {
        self.httpClient = httpClient
        self.persistentStore = persistentStore
    }

    static func apiError(from error: NSError?) -> APIError {
        guard let error else { return APIError(errorType: .unknownError, statusCode: -1, additionalInfo: nil) }
        let status = error.userInfo["kIntercomHTTPStatusCode"] as? Int ?? error.code
        let code = error.userInfo["kIntercomErrorCode"] as? String
        let type: APIErrorType
        if ErrorProcessor.isUserJwtExpired(error) { type = .userJwtExpired }
        else {
            switch status {
            case 404: type = .notFound
            case 1000: type = .missingAppIdentity
            case 1001: type = .missingUserIdentity
            default: type = .serverError(status)
            }
        }
        return APIError(errorType: type, statusCode: status, additionalInfo: code.map { ["errorCode": $0] })
    }

    func getSurvey(id: String, completionHandler: @escaping (Result<Survey, APIError>) -> Void) {
        httpClient.getSurvey(id, success: { raw in
            if let dictionary = raw as? [String: Any], let survey = Survey(withDictionary: dictionary) {
                completionHandler(.success(survey))
            } else {
                completionHandler(.failure(APIError(errorType: .invalidResponseObject, statusCode: 1002, additionalInfo: nil)))
            }
        }, error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
    func getSurveyWithInstanceId(id: String, completionHandler: @escaping (Result<Survey, APIError>) -> Void) {
        httpClient.getSurveyWithInstanceId(id, success: { raw in
            if let dictionary = raw as? [String: Any], let survey = Survey(withDictionary: dictionary) {
                completionHandler(.success(survey))
            } else {
                var additionalInfo: [String: Any]?
                if let id = raw["id"], let progress = raw["survey_progress_id"] {
                    additionalInfo = ["id": id, "survey_progress_id": progress]
                }
                completionHandler(.failure(APIError(errorType: .invalidResponseObject, statusCode: 1002, additionalInfo: additionalInfo)))
            }
        }, error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
    func submitSurvey(_ survey: Survey, completionHandler: @escaping (Result<SurveySupplementaryInfo, APIError>) -> Void) {
        httpClient.submitSurvey(survey, success: { raw in
            if let dictionary = raw as? [String: Any], let info = SurveySupplementaryInfo(withDictionary: dictionary) {
                completionHandler(.success(info))
            } else {
                completionHandler(.failure(APIError(errorType: .invalidResponseObject, statusCode: 1002, additionalInfo: nil)))
            }
        }, error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
    func dismissSurvey(_ survey: Survey, completionHandler: @escaping (Result<Void, APIError>) -> Void) {
        httpClient.dismiss(survey, success: { completionHandler(.success(())) },
            error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
    func failSurvey(id: String, progressId: Int, completionHandler: @escaping (Result<Void, APIError>) -> Void) {
        httpClient.failSurvey(id, progressId: progressId, success: { completionHandler(.success(())) },
            error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
    func actionButtonClicked(_ survey: Survey, stepId: String, completionHandler: @escaping (Result<Void, APIError>) -> Void) {
        httpClient.actionButtonClicked(survey, stepId: stepId, success: { completionHandler(.success(())) },
            error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
}

extension SwiftAPIBridge {
    func recordInteractions(_ interactions: [String], for id: String) {
        let client = httpClient
        client.recordInteractions(id, interactions: interactions)
    }

    func conversationDetail(for id: String, reason: ConversationFetchReason,
                            nexusMetadata: [AnyHashable: Any]?, lastAdminPartId: String?,
                            lastAdminPartCreatedAt: Date?,
                            completionHandler: @escaping (Result<ICMConversation, APIError>) -> Void) {
        let client = httpClient
        let timestamp = lastAdminPartCreatedAt.map { NSNumber(value: $0.timeIntervalSince1970) }
        client.conversationDetail(id, reason: reason.httpReason, nexusMetadata: nexusMetadata,
            lastAdminPartCreatedAt: timestamp, lastAdminPartId: lastAdminPartId,
            success: { completionHandler(.success($0)) },
            error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
}

extension SwiftAPIBridge {
    func updateUser(with attributes: [String: Any], newSession: Bool,
                    sentFromBackground: Bool, carouselVisible: Bool,
                    completion: @escaping (Result<Void, APIError>) -> Void) {
        let client = httpClient
        client.updateUser(userAttributes: attributes, newSession: newSession, sessionId: nil,
            sentFromBackground: sentFromBackground, carouselVisible: carouselVisible,
            success: { _ in completion(.success(())) },
            error: { completion(.failure(Self.apiError(from: $0 as NSError?))) })
    }
}

extension SwiftAPIBridge {
    func submitForm(_ parameters: [AnyHashable: Any], to id: String,
                    completionHandler: @escaping (Result<ICMConversation, APIError>) -> Void) {
        let client = httpClient
        client.submitFormParams(parameters, forConversation: id,
            success: { completionHandler(.success($0)) },
            error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }

    func rate(conversationId: String, with rating: UInt, onFailure: @escaping (APIError) -> Void) {
        let client = httpClient
        client.rateConversation(conversationId, withRatingIndex: rating,
            error: { onFailure(Self.apiError(from: $0 as NSError?)) })
    }

    func addRemark(conversationId: String, remark: String, onFailure: @escaping (APIError) -> Void) {
        let client = httpClient
        client.addConversationRatingRemark(forConversation: conversationId, remark: remark,
            error: { onFailure(Self.apiError(from: $0 as NSError?)) })
    }
}

extension SwiftAPIBridge {
    func createConversation(with blocks: [[AnyHashable: Any]], clientAssignedUUID: String?,
                            articleMetaData: ArticleMetaData?, composerSuggestions: ICMComposerSuggestions?,
                            completionHandler: @escaping (Result<ICMConversation, APIError>) -> Void) {
        httpClient.createConversation(withBlocks: blocks, clientAssignedUUID: clientAssignedUUID,
            articleMetaData: articleMetaData, composerSuggestions: composerSuggestions,
            success: { completionHandler(.success($0)) },
            error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }

    func reply(to conversation: ICMConversation, clientAssignedUUID: String?, with blocks: [[AnyHashable: Any]],
               nexusMetadata: [AnyHashable: Any], lastAdminPartCreatedAt: Int?,
               completionHandler: @escaping (Result<ICMConversationPart, APIError>) -> Void) {
        guard let id = conversation.conversationId else {
            let error = APIError(errorType: .unknownError, statusCode: -1, additionalInfo: nil)
            completionHandler(.failure(Self.apiError(from: error as NSError)))
            return
        }
        let timestamp = lastAdminPartCreatedAt.map { NSNumber(value: $0) }
        httpClient.reply(toConversation: id, clientAssignedUUID: clientAssignedUUID, withBlocks: blocks,
            nexusMetadata: nexusMetadata, lastAdminPartCreatedAt: timestamp,
            success: { completionHandler(.success($0)) },
            error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }

    func quickReply(to id: String, clientAssignedUUID: String?, partId: String?, optionsId: String,
                    completionHandler: @escaping (Result<ICMConversationPart, APIError>) -> Void) {
        httpClient.quickReply(toConversation: id, clientAssignedUUID: clientAssignedUUID,
            withPartId: partId, withOptionUUID: optionsId,
            success: { completionHandler(.success($0)) },
            error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }

    func triggerInboundBot(with id: String, clientAssignedUUID: String?,
                           completionHandler: @escaping (Result<ICMConversation, APIError>) -> Void) {
        httpClient.triggerInboundBot(withOptionUUID: id, clientAssignedUUID: clientAssignedUUID,
            success: { completionHandler(.success($0)) },
            error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
}

extension SwiftAPIBridge {
    func uploadFile(upload: ITBUpload, completionHandler: @escaping (Result<String, APIError>) -> Void) {
        upload.uploadProgress = 0
        httpClient.createUpload(with: upload, success: { value in
            if let value { completionHandler(.success(value)) }
            else { completionHandler(.failure(APIError(errorType: .unknownError, statusCode: -1, additionalInfo: nil))) }
        }, progress: { [upload] value in
            DispatchQueue.main.async {
                let current = upload.uploadProgress
                let scaled = value * 0.8 + 0.1
                let adjusted = scaled < 0.9 ? scaled : 0.999
                upload.uploadProgress = current <= 0.5 ? adjusted : value
            }
        }, error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
}

extension SwiftAPIBridge {
    func transcribeAudio(_ data: Data, conversationId: String?, completionHandler: @escaping (Result<String, APIError>) -> Void) {
        httpClient.transcribeAudio(data, conversationId: conversationId,
            success: { completionHandler(.success($0)) },
            error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
}

extension SwiftAPIBridge {
    func loadGIFs(searchQuery: String, completionHandler: @escaping (Result<ICMGIFResponse, APIError>) -> Void) {
        httpClient.loadGIFs(searchQuery, success: { completionHandler(.success($0)) },
            error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
}

extension SwiftAPIBridge {
    func markTicketAsRead(_ id: String, completion: @escaping () -> Void) {
        httpClient.markTicket(asRead: id, success: { _ in completion() }, error: { _ in
            // The original failure closure is the genuine bare return at 0x9188.
        })
    }
}
