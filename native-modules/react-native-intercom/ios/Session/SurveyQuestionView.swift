// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

// Surviving direct calls establish this functional interface; deleted private witness names remain unproved.
protocol SurveyQuestionViewDelegate: AnyObject {
    func valueUpdated()
}

@objc(_TtC18IntercomSDKPrivate18SurveyQuestionView)
class SurveyQuestionView: UIView, SurveyQuestionResponseViewDelegate {
    lazy var contentStackView: ICMContentStackView = {
        let blocks = question.blocks
        let delegate = blockDelegate
        let stack = ICMContentStackView(blocks: blocks, context: 1, blockDelegate: delegate)!
        stack.spacing = 12
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    let question: any SurveyQuestion
    weak var questionDelegate: (any SurveyQuestionViewDelegate)?
    private let themeProvider: any ThemeProvider
    weak var blockDelegate: OutboundContentBlockDelegate?
    private lazy var mainStackView: UIStackView = {
        let stack = UIStackView()
        stack.accessibilityIdentifier = "Question View Main StackView"
        stack.spacing = 12
        stack.axis = .vertical
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var errorView: UIView = {
        let container = UIView()
        let icon = UIImageView()
        icon.image = ICMAssets.infoIcon()!.withTintColor(themeProvider.theme.errorFill)
        icon.contentMode = .scaleAspectFit
        icon.translatesAutoresizingMaskIntoConstraints = false
        container.addSubview(icon)
        icon.heightAnchor.constraint(greaterThanOrEqualToConstant: 14).isActive = true
        icon.widthAnchor.constraint(equalTo: icon.heightAnchor).isActive = true
        icon.leadingAnchor.constraint(equalTo: container.leadingAnchor).isActive = true
        icon.topAnchor.constraint(equalTo: container.topAnchor).isActive = true
        container.addSubview(errorLabel)
        errorLabel.leadingAnchor.constraint(equalTo: icon.trailingAnchor, constant: 4).isActive = true
        errorLabel.trailingAnchor.constraint(equalTo: container.trailingAnchor).isActive = true
        errorLabel.topAnchor.constraint(equalTo: container.topAnchor).isActive = true
        errorLabel.bottomAnchor.constraint(equalTo: container.bottomAnchor).isActive = true
        container.isHidden = true
        container.translatesAutoresizingMaskIntoConstraints = false
        return container
    }()
    private lazy var errorLabel: UILabel = {
        let label = UILabel()
        label.lineBreakMode = .byWordWrapping
        label.numberOfLines = 0
        label.font = .preferredFont(forTextStyle: .caption2)
        label.textColor = themeProvider.theme.textError
        label.adjustsFontForContentSizeCategory = true
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var additionalInfoStackView: UIStackView = {
        let stack = UIStackView()
        stack.spacing = 12
        stack.axis = .horizontal
        stack.distribution = .equalSpacing
        if shouldDisplayResponseHint { stack.addArrangedSubview(responseHintLabel) }
        if shouldDisplayCharacterCount { stack.addArrangedSubview(characterCountLabel) }
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var responseHintLabel: UILabel = {
        let label = UILabel()
        label.text = question.responseHint
        label.lineBreakMode = .byWordWrapping
        label.numberOfLines = 0
        label.font = .preferredFont(forTextStyle: .caption2)
        label.textColor = themeProvider.theme.textMuted
        label.adjustsFontForContentSizeCategory = true
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var characterCountLabel: UILabel = {
        let label = UILabel()
        label.lineBreakMode = .byWordWrapping
        label.numberOfLines = 0
        label.font = .preferredFont(forTextStyle: .caption2)
        label.textColor = themeProvider.theme.textMuted
        label.adjustsFontForContentSizeCategory = true
        label.setContentCompressionResistancePriority(UILayoutPriority(751), for: .horizontal)
        label.setContentHuggingPriority(UILayoutPriority(251), for: .horizontal)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var responseView: SurveyQuestionResponseView? = {
        let result = SurveyQuestionResponseView(with: question, delegate: self,
            surveyScrollingDelegate: surveyScrollingDelegate, surveyAppearance: surveyAppearance)
        result?.translatesAutoresizingMaskIntoConstraints = false
        return result
    }()
    private let questionIndex: Int
    private let questionCount: Int
    private let surveyAppearance: SurveyAppearance
    weak var surveyScrollingDelegate: SurveyStepViewController?
    private let shouldDisplayCharacterCount: Bool
    private let shouldDisplayResponseHint: Bool

    init(question: any SurveyQuestion, questionIndex: Int, questionCount: Int,
         surveyAppearance: SurveyAppearance, questionDelegate: (any SurveyQuestionViewDelegate)?,
         surveyScrollingDelegate: SurveyStepViewController?, blockDelegate: OutboundContentBlockDelegate?,
         themeProvider: any ThemeProvider) {
        self.question = question
        self.questionDelegate = questionDelegate
        self.surveyScrollingDelegate = surveyScrollingDelegate
        self.blockDelegate = blockDelegate
        self.questionIndex = questionIndex
        self.questionCount = questionCount
        self.surveyAppearance = surveyAppearance
        self.themeProvider = themeProvider
        shouldDisplayCharacterCount = question is any SurveyCharacterCountQuestion
        shouldDisplayResponseHint = question.responseHint != nil
        super.init(frame: .zero)
        setupView()
        setupSubviews()
        updateRequiredIndicator(color: themeProvider.theme.textDefault)
    }

    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private func setupView() {
        backgroundColor = themeProvider.theme.base
        layer.cornerRadius = 8
        layer.masksToBounds = true
        layer.borderWidth = 1
        layer.borderColor = themeProvider.theme.neutralBorder.cgColor
        layer.shadowColor = themeProvider.theme.textDefault.cgColor
        layer.shadowOffset = CGSize(width: 0, height: 4)
        layer.shadowOpacity = 0.15
        layer.shadowRadius = 8
        clipsToBounds = false
    }

    private func setupSubviews() {
        addSubview(mainStackView)
        mainStackView.topAnchor.constraint(equalTo: topAnchor, constant: 16).isActive = true
        mainStackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -16).isActive = true
        mainStackView.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        mainStackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -16).isActive = true
        mainStackView.addArrangedSubview(contentStackView)
        contentStackView.renderContent()
        mainStackView.addArrangedSubview(errorView)
        if let responseView { mainStackView.addArrangedSubview(responseView) }
        if shouldDisplayResponseHint || shouldDisplayCharacterCount {
            mainStackView.addArrangedSubview(additionalInfoStackView)
        }
    }

    func valueUpdated() {
        errorView.isHidden = true
        updateRequiredIndicator(color: themeProvider.theme.textDefault)
        if let responseView { responseView.formInputView!.configureDefaultState() }
        questionDelegate?.valueUpdated()
    }

    func showValidation(shouldVoiceOverFocusOnError: Bool) {
        if let error = question.validationErrorMessage {
            errorLabel.text = error
            errorView.isHidden = false
            updateRequiredIndicator(color: themeProvider.theme.errorFill)
            if let responseView { responseView.formInputView!.configureErrorState() }
            if shouldVoiceOverFocusOnError { UIAccessibility.post(notification: .layoutChanged, argument: errorLabel) }
        } else {
            errorView.isHidden = true
            updateRequiredIndicator(color: themeProvider.theme.textDefault)
            if let responseView { responseView.formInputView!.configureDefaultState() }
        }
    }

    private func updateRequiredIndicator(color: UIColor) {
        if !question.isRequired { return }
        // The original specialized body ignores its supplied color, including the error color.
        let appearance = ICMSurveyQuestionBlockAppearance(requiredIndicatorColor: themeProvider.theme.textDefault)
        contentStackView.updateFirstBlock(with: appearance)
    }

    func characterCountDidUpdate(count: Int, limit: Int) {
        let label = characterCountLabel
        let theme = themeProvider.theme
        label.textColor = limit < count ? theme.textError : theme.textMuted
        characterCountLabel.text = String(count) + "/" + String(limit)
    }

    override var accessibilityElements: [Any]? {
        get {
            setAccessibilityLabelForContentStackView()
            var elements: [Any] = [contentStackView, errorLabel]
            if let responseView { elements.append(responseView) }
            return elements
        }
        set {}
    }

    private func setAccessibilityLabelForContentStackView() {
        var label = TranslationManager.messenger.translation(
            forKey: "surveys_question_question_number_of_question_count",
            params: ["questioin_number": String(questionIndex + 1), "question_count": String(questionCount)])
        label += ";"
        for subview in contentStackView.arrangedSubviews {
            if let text = subview.accessibilityLabel { label += text + ";" }
        }
        if question.isRequired {
            label += TranslationManager.messenger.translation(forKey: "surveys_required_question") + ";"
        }
        if let hint = question.responseHint { label += hint + ";" }
        if question is any SurveyCharacterCountQuestion, let count = characterCountLabel.text { label += count + ";" }
        if let hint = questionTypeSpecificAccessibilityHints { label += hint + ";" }
        contentStackView.accessibilityLabel = label
        contentStackView.isAccessibilityElement = true
    }

    private var questionTypeSpecificAccessibilityHints: String? {
        guard let input = responseView?.formInputView as? any RatingScaleInputView else { return nil }
        return input.ratingScaleOptionsViewController?.accessibilityLabel
    }
}
