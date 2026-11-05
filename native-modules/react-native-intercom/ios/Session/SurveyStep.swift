// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

enum SurveyStepType: Int {
    case content = 0, question, intro, thankYou

    var defaultSubmitButtonTitle: String {
        switch self {
        case .content, .intro: return TranslationManager.messenger.translation(forKey: "surveys_next_button")
        case .question: return TranslationManager.messenger.translation(forKey: "submit")
        case .thankYou: return TranslationManager.messenger.translation(forKey: "surveys_done_button")
        }
    }
}

struct SurveyStepAction: DictionaryInitializable {
    let title: String
    let webUrl: String?
    let iosUri: String?

    init?(withDictionary dictionary: [String: Any]) {
        guard let title = dictionary["action_title"] as? String else { return nil }
        self.title = title
        webUrl = dictionary["web_url"] as? String
        iosUri = dictionary["ios_uri"] as? String
    }
}

struct SurveySupplementaryInfo: DictionaryInitializable {
    let surveyId: String
    let isComplete: Bool
    let nextStep: SurveyStep?
    let stepCount: Int

    init?(withDictionary dictionary: [String: Any]) {
        guard let id = dictionary["survey_id"] as? String,
              let complete = dictionary["complete"] as? Bool,
              let count = dictionary["step_count"] as? Int else { return nil }
        if let nextDictionary = dictionary["next_step"] as? [String: Any] {
            guard let step = SurveyStep(withDictionary: nextDictionary) else { return nil }
            nextStep = step
        } else {
            nextStep = nil
        }
        surveyId = id
        isComplete = complete
        stepCount = count
    }
}

@objc(_TtC18IntercomSDKPrivate10SurveyStep)
class SurveyStep: NSObject, DictionaryInitializable {
    let id: String
    var contentBlocks: [ITBBlock]
    let questions: [any SurveyQuestion]
    let stepType: SurveyStepType
    let buttonText: String
    let actions: [SurveyStepAction]
    var backgroundColor: UIColor
    let contentBlocksDictionary: [[String: Any]]

    @objc init(id: String) {
        backgroundColor = DefaultThemeProvider.shared.theme.base
        self.id = id
        contentBlocks = []
        questions = []
        stepType = .content
        buttonText = TranslationManager.messenger.translation(forKey: "surveys_next_button")
        actions = []
        contentBlocksDictionary = [[:]]
        super.init()
    }
    required init?(withDictionary dictionary: [String: Any]) {
        let initialBackground = DefaultThemeProvider.shared.theme.base
        backgroundColor = initialBackground
        guard let id = dictionary["id"] as? String,
              let rawStepType = dictionary["step_type"] as? Int else { return nil }
        self.id = id
        let parsedStepType = SurveyStepType(rawValue: rawStepType) ?? .content
        stepType = parsedStepType
        if let dictionaries = dictionary["questions"] as? [[String: Any]] {
            var parsedQuestions: [any SurveyQuestion] = []
            for dictionary in dictionaries {
                guard let rawType = dictionary["question_type"] as? Int,
                      let type = SurveyQuestionType(rawValue: rawType) else { continue }
                let question: (any SurveyQuestion)?
                switch type {
                case .input: question = SurveyInputQuestion(withDictionary: dictionary)
                case .text: question = SurveyTextQuestion(withDictionary: dictionary)
                case .dropdown: question = SurveyDropdownQuestion(withDictionary: dictionary)
                case .ratingScale: question = SurveyRatingScaleQuestion(withDictionary: dictionary)
                case .multiSelect: question = SurveyMultiSelectQuestion(withDictionary: dictionary)
                }
                if let question { parsedQuestions.append(question) }
            }
            guard parsedQuestions.count == dictionaries.count else { return nil }
            questions = parsedQuestions
        } else {
            questions = []
        }
        let blockDictionaries = (dictionary["blocks"] as? [[String: Any]]) ?? [[:]]
        contentBlocksDictionary = blockDictionaries
        contentBlocks = Self.createStepBlocks(blockDictionaries, backgroundColor: initialBackground)
        if let title = dictionary["custom_button_text"] as? String, !title.isEmpty { buttonText = title }
        else { buttonText = parsedStepType.defaultSubmitButtonTitle }
        actions = ((dictionary["actions"] as? [[String: Any]]) ?? []).compactMap(SurveyStepAction.init(withDictionary:))
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }

    var firstQuestionWithInvalidResponse: (any SurveyQuestion)? {
        for question in questions {
            if !question.responseIsValid { return question }
        }
        return nil
    }
    var completedQuestionCount: Int {
        var count = 0
        for question in questions {
            if question.response != nil && question.responseIsValid { count += 1 }
        }
        return count
    }
    static func createStepBlocks(_ dictionaries: [[String: Any]]?, backgroundColor: UIColor) -> [ITBBlock] {
        var blocks: [ITBBlock] = []
        if let dictionaries {
            for dictionary in dictionaries {
                let appearance = ICMSurveyStepBlockAppearance(forBackgroundColor: backgroundColor)
                if let block = ICMBlockLoader.block(from: dictionary, appearance: appearance) {
                    block.displayMode = 1
                    blocks.append(block)
                }
            }
        }
        return blocks
    }
}
