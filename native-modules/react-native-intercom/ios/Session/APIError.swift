// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import Foundation

enum APIErrorType: Equatable {
    case serverError(Int)
    case invalidResponseObject, notFound, userJwtExpired, missingAppIdentity, missingUserIdentity, unknownError
}

struct APIError: Error {
    let errorType: APIErrorType
    let statusCode: Int
    var additionalInfo: [String: Any]?
}

enum SurveyError: Error, Hashable { case serverError, somethingWentWrong, contentNotAvailable }
