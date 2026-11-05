// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

class SurveyTextQuestion: SurveyCharacterCountQuestion {
    var id: String
    var type: SurveyQuestionType
    var blocks: [ITBBlock]
    var isRequired: Bool
    var serverValidationErrorMessage: String?
    var localValidationErrorMessage: String?
    var responseHint: String?
    var response: Any?
    var displayCount: Bool
    var characterLimit: Int?
    let placeholder: String?

    required init?(withDictionary dictionary: [String: Any]) {
        guard let id = dictionary["id"] as? String,
              let rawType = dictionary["question_type"] as? Int,
              let type = SurveyQuestionType(rawValue: rawType),
              let data = dictionary["data"] as? [String: Any],
              let required = data["required"] as? Bool else { return nil }
        self.id = id
        self.type = type
        isRequired = required
        blocks = Self.createQuestionBlocks(dictionary["blocks"] as? [[String: Any]], isRequiredQuestion: required)
        placeholder = (data["placeholder"] as? String) ?? TranslationManager.messenger.translation(forKey: "surveys_text_input_question_placeholder")
        characterLimit = data["character_limit"] as? Int
        displayCount = characterLimit != nil
    }

    func formInputView(inputViewDelegate: any ICMInputViewDelegate, surveyAppearance: SurveyAppearance) -> (any ICMInputViewProtocol)? {
        let properties = InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true)
        let view = TextInputView(inputViewDelegate: inputViewDelegate, inputProperties: properties, themeProvider: DefaultThemeProvider.shared)
        view.characterLimit = characterLimit
        view.isRequired = isRequired
        view.placeholderText = placeholder
        let color = surveyAppearance.buttonColor
        view.tintColor = color.isWhite ? UIColor.black : color
        return view
    }
}
