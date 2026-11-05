// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

// The functional calls survive; optimized-away private witness spelling/order is not claimed recovered.
protocol SurveyQuestionResponseViewDelegate: AnyObject {
    func valueUpdated()
    func characterCountDidUpdate(count: Int, limit: Int)
    func showValidation(shouldVoiceOverFocusOnError: Bool)
}

@objc(_TtC18IntercomSDKPrivate26SurveyQuestionResponseView)
class SurveyQuestionResponseView: UIView, UITextViewDelegate, UITextFieldDelegate, ICMInputViewDelegate {
    weak var delegate: (any SurveyQuestionResponseViewDelegate)?
    weak var surveyScrollingDelegate: SurveyStepViewController?
    var question: any SurveyQuestion
    var formInputView: (any ICMInputViewProtocol)?
    private var textViewHeightConstraint: NSLayoutConstraint?

    init?(with question: any SurveyQuestion, delegate: (any SurveyQuestionResponseViewDelegate)?,
          surveyScrollingDelegate: SurveyStepViewController?, surveyAppearance: SurveyAppearance) {
        self.question = question
        self.delegate = delegate
        self.surveyScrollingDelegate = surveyScrollingDelegate
        super.init(frame: .zero)
        guard let input = self.question.formInputView(inputViewDelegate: self, surveyAppearance: surveyAppearance) else { return nil }
        formInputView = input
        setupFormInputView()
        if let field = formInputView as? UITextField {
            field.borderStyle = .roundedRect
            field.delegate = self
            field.clearButtonMode = .whileEditing
        }
        setupForTextView()
        setValidationOnQuestion()
        characterCountDidUpdate(count: 0)
    }

    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private func setupFormInputView() {
        formInputView!.isRequired = question.isRequired
        if let view = formInputView as? UIView {
            view.translatesAutoresizingMaskIntoConstraints = false
            addSubview(view)
            view.leadingAnchor.constraint(equalTo: leadingAnchor).isActive = true
            view.trailingAnchor.constraint(equalTo: trailingAnchor).isActive = true
            view.topAnchor.constraint(equalTo: topAnchor).isActive = true
            view.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        }
    }

    private func setupForTextView() {
        if let textView = formInputView as? UITextView {
            textView.delegate = self
            textView.font = .preferredFont(forTextStyle: .body)
            textView.adjustsFontForContentSizeCategory = true
            textView.heightAnchor.constraint(greaterThanOrEqualToConstant: 80).isActive = true
            textViewHeightConstraint = textView.heightAnchor.constraint(equalToConstant: 130)
            textViewHeightConstraint!.isActive = false
            textView.isScrollEnabled = false
        }
    }

    private func setValidationOnQuestion() {
        do {
            try formInputView!.isUserInputValid()
            question.localValidationErrorMessage = nil
        } catch {
            question.localValidationErrorMessage = error.localizedDescription
        }
    }

    private func characterCountDidUpdate(count: Int) {
        if let question = question as? any SurveyCharacterCountQuestion,
           question.displayCount, let limit = question.characterLimit, limit != 0 {
            delegate?.characterCountDidUpdate(count: count, limit: limit)
        }
    }

    @objc(valueUpdated:)
    func valueUpdated(_ value: Any?) {
        if let string = value as? String { characterCountDidUpdate(count: string.count) }
        else { characterCountDidUpdate(count: 0) }
        question.response = value
        setValidationOnQuestion()
        question.serverValidationErrorMessage = nil
        delegate?.valueUpdated()
    }

    @objc func entryCompleteButtonTapped() {
        if let view = formInputView as? UIView { view.resignFirstResponder() }
    }
    @objc func cancelButtonTapped() {
        if let view = formInputView as? UIView { view.resignFirstResponder() }
    }
    @objc func inputFieldDidBeginEditing() {}
    @objc func inputFieldDidEndEditing() {}

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    func textFieldDidEndEditing(_ textField: UITextField) {
        delegate?.showValidation(shouldVoiceOverFocusOnError: true)
    }
    func textViewDidEndEditing(_ textView: UITextView) {
        delegate?.showValidation(shouldVoiceOverFocusOnError: true)
    }
    func textViewDidChange(_ textView: UITextView) {
        let height = textView.contentSize.height
        textViewHeightConstraint?.isActive = height > 130
        textView.isScrollEnabled = height > 130
        textView.setNeedsLayout()
        if let controller = surveyScrollingDelegate {
            // Original WMO directly uses this sole concrete Step implementation.
            if let keyboardFrame = controller.keyboardOverlapFrame {
                let rect = textView.convert(textView.bounds, to: controller.view)
                if rect.maxY > keyboardFrame.minY {
                    var ancestor = textView.superview
                    while let current = ancestor {
                        if let questionView = current as? SurveyQuestionView {
                            controller.scrollSurvey(to: questionView)
                            break
                        }
                        ancestor = current.superview
                    }
                }
            }
        }
    }
}
