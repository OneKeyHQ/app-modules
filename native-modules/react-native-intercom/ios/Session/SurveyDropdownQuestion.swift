import UIKit

class SurveyDropdownQuestion: SurveyQuestion {
    var id: String
    var type: SurveyQuestionType
    var blocks: [ITBBlock]
    var isRequired: Bool
    var localValidationErrorMessage: String?
    var serverValidationErrorMessage: String?
    var responseHint: String?
    var response: Any?
    let options: [String]
    let responseOrder: ResponseOrder?
    let placeholder: String?

    enum ResponseOrder: Int { case `default` = 0, alphabetical, random }

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
        responseOrder = (data["response_order"] as? Int).flatMap(ResponseOrder.init(rawValue:))
        placeholder = data["placeholder"] as? String
    }

    func formInputView(inputViewDelegate: any ICMInputViewDelegate, surveyAppearance: SurveyAppearance) -> (any ICMInputViewProtocol)? {
        let properties = InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true)
        let picker = ICMPickerTextField(properties: properties, options: options, delegate: inputViewDelegate)
        picker?.isRequired = true
        if let placeholder { picker?.placeholder = placeholder }
        let buttonColor = surveyAppearance.buttonColor
        picker?.configure(forEditingStateBackgroundColor: buttonColor)
        picker?.heightAnchor.constraint(greaterThanOrEqualToConstant: 40).isActive = true
        return picker
    }
}
