// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import Foundation

@objc(_TtC18IntercomSDKPrivate13SurveyService)
class SurveyService: NSObject {
    private let apiBridge: SwiftAPIBridge
    @objc static var currentlyDisplayingSurvey = false

    override init() {
        apiBridge = SwiftAPIBridge()
        super.init()
    }
    init(apiBridge: SwiftAPIBridge) {
        self.apiBridge = apiBridge
        super.init()
    }
    @objc static func presentSurvey(_ survey: Survey) {
        ICMPresentationManager.sharedInstance().presentSurvey(survey)
    }
    @objc(presentSurveyWithInstanceId:)
    func presentSurveyWithInstanceId(_ id: String) {
        getSurveyFromInstanceId(id) { result in
            if case .success(let survey) = result {
                DispatchQueue.main.async { ICMPresentationManager.sharedInstance().presentSurvey(survey) }
            }
        }
    }

    private static func surveyError(from error: APIError) -> SurveyError {
        switch error.errorType {
        case .serverError, .userJwtExpired: return .serverError
        case .notFound: return .contentNotAvailable
        case .invalidResponseObject, .missingAppIdentity, .missingUserIdentity, .unknownError: return .somethingWentWrong
        }
    }
    func getSurvey(id: String, completionHandler: ((Result<Survey, SurveyError>) -> Void)?) {
        apiBridge.getSurvey(id: id) { result in
            switch result {
            case .success(let survey): completionHandler?(.success(survey))
            case .failure(let error): completionHandler?(.failure(Self.surveyError(from: error)))
            }
        }
    }
    func getSurveyFromInstanceId(_ id: String, completionHandler: ((Result<Survey, SurveyError>) -> Void)?) {
        apiBridge.getSurveyWithInstanceId(id: id) { result in
            switch result {
            case .success(let survey): completionHandler?(.success(survey))
            case .failure(let error):
                if let id = error.additionalInfo?["id"] as? String,
                   let progress = error.additionalInfo?["survey_progress_id"] as? Int {
                    self.apiBridge.failSurvey(id: id, progressId: progress) { _ in }
                }
                completionHandler?(.failure(Self.surveyError(from: error)))
            }
        }
    }
    func submitSurvey(_ survey: Survey, completionHandler: ((Result<Survey, SurveyError>) -> Void)?) {
        apiBridge.submitSurvey(survey) { result in
            switch result {
            case .success(let info):
                survey.update(with: info)
                completionHandler?(.success(survey))
            case .failure(let error):
                if error.errorType == .invalidResponseObject {
                    self.apiBridge.failSurvey(id: survey.id, progressId: survey.progressId) { _ in }
                }
                completionHandler?(.failure(Self.surveyError(from: error)))
            }
        }
    }
    func dismissSurvey(_ survey: Survey, completionHandler: ((Result<Void, SurveyError>) -> Void)?) {
        apiBridge.dismissSurvey(survey) { result in
            if case .failure(let error) = result { completionHandler?(.failure(Self.surveyError(from: error))) }
        }
    }
    func actionButtonClicked(_ survey: Survey, stepId: String, completionHandler: ((Result<Void, SurveyError>) -> Void)?) {
        apiBridge.actionButtonClicked(survey, stepId: stepId) { result in
            if case .failure(let error) = result { completionHandler?(.failure(Self.surveyError(from: error))) }
        }
    }
}
