import Foundation
import os

enum ConversationError: Error, Equatable {
    case serverError(APIErrorType)
    case somethingWentWrong, missingConversationId, conversationNotFound, messageSendingFailure, conversationAlreadyOpen

    func logErrorToOurCustomers() {
        let message: String
        switch self {
        case .somethingWentWrong: message = "An unknown error occurred."
        case .missingConversationId: message = "A valid conversation was not found to load this conversation."
        case .conversationNotFound: message = "An expected conversation was not found in the local cache."
        case .messageSendingFailure: message = "The message failed to send."
        case .conversationAlreadyOpen: message = "A new conversation could not be started because one is already open."
        case .serverError(let error):
            switch error {
            case .serverError(let code): message = "A server error occured. " + String(code)
            case .invalidResponseObject: message = "An error occured. Please inform Intercom with the following error code: 1002"
            case .notFound: message = "The content does not exist or has been removed."
            case .userJwtExpired: message = "Loading a conversation failed because the user JWT has expired. Call setUserJwt: with a fresh token."
            case .missingAppIdentity: message = "Displaying a conversation requires Intercom to be initialised. See Intercom Initialisation in our documentation."
            case .missingUserIdentity: message = "Displaying a conversation requires a user to be logged in."
            case .unknownError: message = "An unknown error occurred."
            }
        }
        ICMLogger.log(message, type: .error)
    }
}
