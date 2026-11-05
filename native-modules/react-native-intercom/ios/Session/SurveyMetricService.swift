import Foundation

enum SurveyDismissalType: Hashable { case original0, original1 }

@_objcRuntimeName(_TtC18IntercomSDKPrivate19SurveyMetricService)
class SurveyMetricService {
    private let metricInstance: (any ICMMetricProtocol)?
    var survey: Survey
    private lazy var standardMetadata: [MetaData] = [.surveyId(survey.id), .format(survey.format)]

    private enum MetaData {
        case surveyId(String), errorCode(NSNumber), requestedContentId(String)
        case stepsComplete(Int), questionsSeen(Int), format(SurveyFormat), maxSteps(Int)
    }

    init(metricInstance: (any ICMMetricProtocol)?, survey: Survey) {
        self.metricInstance = metricInstance
        self.survey = survey
    }

    private static func dictionary(from metadata: [MetaData]) -> [String: Any] {
        var result: [String: Any] = [:]
        for item in metadata {
            switch item {
            case .surveyId(let value): result["survey_id"] = value
            case .errorCode(let value): result["error_code"] = value
            case .requestedContentId(let value): result["requested_content_id"] = value
            case .stepsComplete(let value): result["steps_complete"] = value
            case .questionsSeen(let value): result["questions_seen"] = value
            case .format(let value):
                switch value {
                case .original0: result["format"] = "small_full_screen"
                case .original1: result["format"] = "large_full_screen"
                }
            case .maxSteps(let value): result["max_steps"] = value
            }
        }
        return result
    }

    private func createEvent(action: String, object: String = "survey", context: String,
                             metadata: [MetaData] = []) {
        var additionalInfo = Self.dictionary(from: standardMetadata)
        additionalInfo.merge(Self.dictionary(from: metadata)) { _, new in new }
        let event = ICMMetricEvent(action: action, object: object, place: "survey",
            context: context, additionalInfo: additionalInfo)
        event?.eventType = 4
        metricInstance?.addEvent(event)
    }

    func addOpenedMetric(source: SurveySource) {
        // Both original source constants contain this same context string.
        createEvent(action: "opened", context: "from_automatic")
    }

    func addCompletedMetric() {
        let steps = survey.steps.count
        let maximum = survey.stepCount
        let questions = survey.steps.reduce(0) { $0 + $1.questions.count }
        createEvent(action: "completed", context: "from_cta",
            metadata: [.stepsComplete(steps), .maxSteps(maximum), .questionsSeen(questions)])
    }

    func addClosedFromDoneMetric() {
        createEvent(action: "closed", context: "from_cta", metadata: [.stepsComplete(survey.steps.count)])
    }
    func addClosedFromCloseButtonMetric() { createEvent(action: "closed", context: "from_close_button") }
    func addClosedFromSwipeMetric() { createEvent(action: "closed", context: "from_swipe") }
    func addClosedFromExternalLinkMetric() {
        createEvent(action: "closed", context: "from_onward_external_link", metadata: [.stepsComplete(survey.steps.count)])
    }
    func addClosedFromDeepLinkMetric() {
        createEvent(action: "closed", context: "from_onward_deep_link", metadata: [.stepsComplete(survey.steps.count)])
    }
    func addDismissedMetric(object: String, dismissalType: SurveyDismissalType, stepsComplete: Int) {
        let maximum = survey.stepCount
        let questions = survey.steps.reduce(0) { $0 + $1.questions.count }
        createEvent(action: "dismissed", object: object,
            context: dismissalType == .original1 ? "from_swipe" : "from_close_button",
            metadata: [.stepsComplete(stepsComplete), .maxSteps(maximum), .questionsSeen(questions)])
    }

    static func addFetchingSurveyMetric(id: String, metricInstance: (any ICMMetricProtocol)?) {
        let event = ICMMetricEvent(action: "started", object: "survey_preload", place: "api",
            context: "no_context", additionalInfo: dictionary(from: [.requestedContentId(id)]))
        event?.eventType = 4
        metricInstance?.addEvent(event)
    }
}
