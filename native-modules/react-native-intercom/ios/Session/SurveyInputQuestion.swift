import UIKit

class SurveyInputQuestion: SurveyCharacterCountQuestion {
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
    let inputType: InputType?
    let validationType: ValidationType?
    let placeholder: String?

    enum InputType: Int { case freeText = 0, email, phone }
    enum ValidationType: Int { case text = 0, number, email, phone }

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
        inputType = (data["type"] as? Int).flatMap(InputType.init(rawValue:))
        let validation = data["validation"] as? [String: Any]
        validationType = (validation?["type"] as? Int).flatMap(ValidationType.init(rawValue:))
    }

    func formInputView(inputViewDelegate: any ICMInputViewDelegate, surveyAppearance: SurveyAppearance) -> (any ICMInputViewProtocol)? {
        let field: ICMTextField?
        switch validationType {
        case .number:
            field = ICMIntegerTextField(properties: InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true), delegate: inputViewDelegate)
        case .email:
            field = ICMEmailTextField(properties: InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true), delegate: inputViewDelegate)
        case .phone:
            field = ICMPhoneTextField(properties: InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true), delegate: inputViewDelegate)
        case .text, nil:
            guard let textField = ICMTextField(properties: InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true), delegate: inputViewDelegate) else { return nil }
            field = textField
        }
        field?.placeholder = placeholder
        field?.characterLimit = characterLimit ?? 0
        field?.isRequired = isRequired
        let color = surveyAppearance.buttonColor
        let tint = color.isWhite ? UIColor.black : color
        field?.tintColor = tint
        field?.heightAnchor.constraint(greaterThanOrEqualToConstant: 40).isActive = true
        return field
    }
}
