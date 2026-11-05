import UIKit

class SurveyMultiSelectQuestion: SurveyQuestion {
    var id: String
    var type: SurveyQuestionType
    var blocks: [ITBBlock]
    var isRequired: Bool
    var localValidationErrorMessage: String?
    var serverValidationErrorMessage: String?
    var responseHint: String?
    var response: Any?
    let options: [String]
    let includeOtherOption: Bool
    let minimumSelection: Int
    let maximumSelection: Int

    required init?(withDictionary dictionary: [String: Any]) {
        guard let id = dictionary["id"] as? String,
              let rawType = dictionary["question_type"] as? Int,
              let type = SurveyQuestionType(rawValue: rawType),
              let data = dictionary["data"] as? [String: Any],
              let required = data["required"] as? Bool,
              let options = data["options"] as? [String] else { return nil }
        self.id = id
        self.type = type
        isRequired = required
        blocks = Self.createQuestionBlocks(dictionary["blocks"] as? [[String: Any]], isRequiredQuestion: required)
        self.options = options
        includeOtherOption = (data["include_other"] as? Bool) ?? false
        minimumSelection = (data["minimum_selection"] as? Int) ?? 0
        maximumSelection = (data["maximum_selection"] as? Int) ?? 1
        if minimumSelection >= 2 {
            responseHint = TranslationManager.messenger.translation(forKey: "surveys_multi_select_too_few_responses",
                params: ["response_count": String(minimumSelection)])
        }
    }

    func formInputView(inputViewDelegate: any ICMInputViewDelegate, surveyAppearance: SurveyAppearance) -> (any ICMInputViewProtocol)? {
        let view = MultiSelectInputView(options: options, minimumSelection: minimumSelection,
            maximumSelection: maximumSelection, includeOtherOption: includeOtherOption, inputViewDelegate: inputViewDelegate)
        view.configure(selectedOptionTintColor: surveyAppearance.buttonColor)
        return view
    }
}
