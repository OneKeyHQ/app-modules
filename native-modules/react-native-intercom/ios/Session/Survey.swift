// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

struct SurveySender: Equatable {
    let avatarURL: URL?
    let name: String
}

@objc(_TtC18IntercomSDKPrivate6Survey)
class Survey: NSObject, SurveyInfo, DictionaryInitializable {
    @objc let id: String
    @objc let progressId: Int
    let format: SurveyFormat
    var steps: [SurveyStep]
    var customizationOptions: SurveyCustomizationOptions
    var isComplete = false
    var sender: SurveySender?
    var stepCount: Int
    let dismissable: Bool
    let showProgressBar: Bool

    @objc init(id: String, format: SurveyFormat, steps: [SurveyStep], progressId: Int,
               customizationOptions: SurveyCustomizationOptions?, stepCount: Int) {
        self.id = id
        self.format = format
        self.steps = steps
        self.progressId = progressId
        if let customizationOptions { self.customizationOptions = customizationOptions }
        else {
            self.customizationOptions = SurveyCustomizationOptions(
                backgroundColor: DefaultThemeProvider.shared.theme.base,
                buttonColor: DefaultThemeProvider.shared.theme.primaryContainer)
        }
        self.stepCount = stepCount
        dismissable = true
        showProgressBar = true
        super.init()
    }
    @objc required init?(withDictionary dictionary: [String: Any]) {
        guard let id = dictionary["id"] as? String,
              let rawFormat = dictionary["format"] as? Int,
              let progressId = dictionary["survey_progress_id"] as? Int,
              let stepDictionaries = dictionary["steps"] as? [[String: Any]],
              let format = SurveyFormat(rawValue: rawFormat),
              let stepCount = dictionary["step_count"] as? Int else { return nil }
        if let data = dictionary["sender"] as? [String: Any], let name = data["first_name"] as? String {
            let avatar = data["avatar"] as? [String: String]
            sender = SurveySender(avatarURL: avatar?["square_128"].flatMap(URL.init(string:)), name: name)
        }
        self.id = id
        self.format = format
        self.progressId = progressId
        self.stepCount = stepCount
        dismissable = (dictionary["dismissible"] as? Bool) ?? true
        showProgressBar = (dictionary["show_progress_bar"] as? Bool) ?? true
        let customization: SurveyCustomizationOptions
        if let data = dictionary["customization_options"] as? [String: Any],
           let options = SurveyCustomizationOptions(withDictionary: data) {
            customization = options
        } else {
            customization = SurveyCustomizationOptions(backgroundColor: DefaultThemeProvider.shared.theme.base,
                buttonColor: DefaultThemeProvider.shared.theme.primaryContainer)
        }
        customizationOptions = customization
        let background = customization.backgroundColor
        var parsedSteps: [SurveyStep] = []
        for dictionary in stepDictionaries {
            if let step = SurveyStep(withDictionary: dictionary) {
                step.backgroundColor = background
                step.contentBlocks = SurveyStep.createStepBlocks(step.contentBlocksDictionary, backgroundColor: background)
                parsedSteps.append(step)
            }
        }
        guard parsedSteps.count == stepDictionaries.count else { return nil }
        steps = parsedSteps
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }

    @objc var lastStepId: String? { steps.last?.id }

    func update(with info: SurveySupplementaryInfo) {
        guard info.surveyId == id else { return }
        if let step = info.nextStep {
            let background = customizationOptions.backgroundColor
            step.backgroundColor = background
            step.contentBlocks = SurveyStep.createStepBlocks(step.contentBlocksDictionary, backgroundColor: background)
            steps.append(step)
        }
        isComplete = info.isComplete
        stepCount = info.stepCount
    }
    @objc func allResponses() -> [[String: Any]] {
        let currentSteps = steps
        var responses: [[String: Any]] = []
        for step in currentSteps {
            for question in step.questions {
                if let response = question.response {
                    responses.append(["question_id": question.id, "response": response])
                }
            }
        }
        return responses
    }
}
