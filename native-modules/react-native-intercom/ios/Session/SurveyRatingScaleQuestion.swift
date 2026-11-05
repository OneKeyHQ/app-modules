import UIKit

struct EmojiOption {
    let value: Int
    let unicodeEmoticon: String
    let imageUrl: String
}

class SurveyRatingScaleQuestion: SurveyQuestion {
    var id: String
    var type: SurveyQuestionType
    var blocks: [ITBBlock]
    var isRequired: Bool
    var serverValidationErrorMessage: String?
    var localValidationErrorMessage: String?
    var responseHint: String?
    var response: Any?
    let ratingScaleType: RatingScaleType
    let lowerLabel: String?
    let upperLabel: String?
    let scaleStart: Int?
    let scaleEnd: Int?
    var options: [EmojiOption]?

    enum RatingScaleType: Int { case numeric = 0, stars, emoji, nps }

    required init?(withDictionary dictionary: [String: Any]) {
        guard let id = dictionary["id"] as? String,
              let rawType = dictionary["question_type"] as? Int,
              let type = SurveyQuestionType(rawValue: rawType),
              let data = dictionary["data"] as? [String: Any],
              let required = data["required"] as? Bool,
              let rawRatingType = data["type"] as? Int,
              let ratingType = RatingScaleType(rawValue: rawRatingType) else { return nil }
        self.id = id
        self.type = type
        isRequired = required
        blocks = Self.createQuestionBlocks(dictionary["blocks"] as? [[String: Any]], isRequiredQuestion: required)
        ratingScaleType = ratingType
        lowerLabel = data["lower_label"] as? String
        upperLabel = data["upper_label"] as? String
        scaleStart = data["scale_start"] as? Int
        scaleEnd = data["scale_end"] as? Int
        options = Self.createOptions(data["options"] as? [[String: Any]])
    }

    private static func createOptions(_ dictionaries: [[String: Any]]?) -> [EmojiOption]? {
        dictionaries?.compactMap { dictionary in
            guard let value = dictionary["value"] as? Int,
                  let emoticon = dictionary["unicodeEmoticon"] as? String,
                  let imageUrl = dictionary["image_url"] as? String else { return nil }
            return EmojiOption(value: value, unicodeEmoticon: emoticon, imageUrl: imageUrl)
        }
    }

    private func optionsForNumericRatingPicker(start: Int, end: Int) -> [String] {
        (start...end).map { value in
            if value == start {
                if let lowerLabel, let scaleStart { return String(scaleStart) + " - " + lowerLabel }
                return String(value)
            }
            if value == end, let upperLabel, let scaleEnd { return String(scaleEnd) + " - " + upperLabel }
            return String(value)
        }
    }
    private func lowerLabelWithNumber() -> String? {
        guard let lowerLabel, let scaleStart else { return nil }
        return String(scaleStart) + " - " + lowerLabel
    }
    private func upperLabelWithNumber() -> String? {
        guard let upperLabel, let scaleEnd else { return nil }
        return String(scaleEnd) + " - " + upperLabel
    }

    func formInputView(inputViewDelegate: any ICMInputViewDelegate, surveyAppearance: SurveyAppearance) -> (any ICMInputViewProtocol)? {
        let color = surveyAppearance.buttonColor
        switch ratingScaleType {
        case .stars:
            let view = StarsInputView(inputViewDelegate: inputViewDelegate, scaleStart: scaleStart ?? 1,
                scaleEnd: scaleEnd ?? 5, lowerLabel: lowerLabel, upperLabel: upperLabel,
                isRequired: isRequired, selectedTintColor: color)
            view.accessibilityElements = view.starOptionButtons.map { $0 as [Any] }
            return view
        case .emoji:
            let view = EmojiInputView(inputViewDelegate: inputViewDelegate, options: options ?? [],
                lowerLabel: lowerLabel, upperLabel: upperLabel, isRequired: isRequired)
            view.accessibilityElements = view.emojiButtons.map { $0 as [Any] }
            return view
        case .numeric, .nps:
            guard let start = scaleStart, let end = scaleEnd else { return nil }
            if ICMUtils.contentSizeCategoryIsAccessibilityCategory() || end - start >= 11 {
                let properties = InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true)
                let picker = ICMPickerTextField(properties: properties,
                    options: optionsForNumericRatingPicker(start: start, end: end), delegate: inputViewDelegate)
                picker?.configure(forEditingStateBackgroundColor: color)
                picker?.heightAnchor.constraint(greaterThanOrEqualToConstant: 40).isActive = true
                return picker
            }
            let view = NumericRatingScaleInputView(inputViewDelegate: inputViewDelegate,
                scaleStart: start, scaleEnd: end, lowerLabel: lowerLabelWithNumber(), upperLabel: upperLabelWithNumber(),
                isRequired: isRequired, selectedTintColor: color)
            view.accessibilityElements = view.numberOptionButtons.map { $0 as [Any] }
            return view
        }
    }
}
