// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

enum SurveyQuestionType: Int {
    case input = 0, text, dropdown, ratingScale, multiSelect
}

protocol SurveyQuestion: AnyObject, DictionaryInitializable {
    var id: String { get }
    // This getter's spelling is inferred from the unique stored field; the deleted private witness name is unproved.
    var type: SurveyQuestionType { get }
    var blocks: [ITBBlock] { get }
    var isRequired: Bool { get }
    var localValidationErrorMessage: String? { get set }
    var serverValidationErrorMessage: String? { get set }
    var response: Any? { get set }
    var responseHint: String? { get set }
    func formInputView(inputViewDelegate: any ICMInputViewDelegate, surveyAppearance: SurveyAppearance) -> (any ICMInputViewProtocol)?
}

protocol SurveyCharacterCountQuestion: SurveyQuestion {
    var displayCount: Bool { get set }
    var characterLimit: Int? { get set }
}

extension SurveyQuestion {
    var responseIsValid: Bool {
        localValidationErrorMessage == nil && serverValidationErrorMessage == nil
    }
    var validationErrorMessage: String? {
        localValidationErrorMessage ?? serverValidationErrorMessage
    }
    static func createQuestionBlocks(_ dictionaries: [[String: Any]]?, isRequiredQuestion: Bool) -> [ITBBlock] {
        var blocks: [ITBBlock] = []
        if let dictionaries {
            for dictionary in dictionaries {
                let appearance = ICMSurveyQuestionBlockAppearance()
                if let block = ICMBlockLoader.block(from: dictionary, appearance: appearance) {
                    block.displayMode = 1
                    blocks.append(block)
                }
            }
        }
        if let first = blocks.first { first.isRequired = isRequiredQuestion }
        return blocks
    }
}

// Original enum raw values are proved; its optimized-away case labels are not.
@objc enum SurveyFormat: Int { case original0 = 0, original1 = 1 }
struct SurveyAppearance {
    let format: SurveyFormat
    let backgroundColor: UIColor
    let buttonColor: UIColor
}
