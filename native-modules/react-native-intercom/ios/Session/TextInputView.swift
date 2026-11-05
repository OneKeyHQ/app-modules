import UIKit

@objc(_TtC18IntercomSDKPrivate13TextInputView)
class TextInputView: UITextView, ICMSubmittableInputViewProtocol {
    @objc var isRequired = false
    var characterLimit: Int?
    @objc var placeholderText: String? {
        didSet {
            if text!.isEmpty, let placeholderText {
                text = placeholderText
                textColor = themeProvider.theme.textMutedExtra
                isShowingPlaceholder = true
            }
        }
    }
    weak var inputViewDelegate: (any ICMInputViewDelegate)?
    private let initialValue: String?
    private let fieldIsEditable: Bool
    private let allowSubmission: Bool
    private let themeProvider: any ThemeProvider
    private lazy var submitButton: ICMSubmitButton? = {
        if !allowSubmission { return nil }
        let button = ICMSubmitButton()
        button.isUserInteractionEnabled = fieldIsEditable
        button.addTarget(self, action: #selector(submitButtonTapped), for: .touchUpInside)
        if fieldIsEditable { button.isEnabled = true }
        else { button.showSubmittedState() }
        return button
    }()
    private var isShowingPlaceholder = false
    private var submitButtonTopConstraint: NSLayoutConstraint?
    private var submitButtonLeadingConstraint: NSLayoutConstraint?
    private var inputAllowed = true

    @objc init(inputViewDelegate: (any ICMInputViewDelegate)?, inputProperties: InputProperties,
               themeProvider: any ThemeProvider) {
        self.inputViewDelegate = inputViewDelegate
        self.themeProvider = themeProvider
        let storage = NSTextStorage()
        let container = NSTextContainer(size: .zero)
        let manager = NSLayoutManager()
        storage.addLayoutManager(manager)
        manager.addTextContainer(container)
        fieldIsEditable = inputProperties.valueIsEditable
        allowSubmission = inputProperties.allowSubmission
        initialValue = inputProperties.initialValue as? String
        super.init(frame: .zero, textContainer: container)
        accessibilityIdentifier = "intercom text input view"
        uiSetup()
        addObserversForTextViewEvents()
        addSubmitButton()
        text = initialValue
        if fieldIsEditable || initialValue != nil { textColor = themeProvider.theme.textDefault }
        else { textColor = themeProvider.theme.textMuted }
        isEditable = fieldIsEditable
    }

    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect, textContainer: NSTextContainer?) {
        fatalError("init(frame:textContainer:) has not been implemented")
    }
    deinit { NotificationCenter.default.removeObserver(self) }

    private func uiSetup() {
        backgroundColor = DefaultThemeProvider.shared.theme.inputAlt
        layer.cornerRadius = 8
        layer.borderColor = themeProvider.theme.neutralBorder.cgColor
        layer.borderWidth = 1
        clipsToBounds = true
        textContainerInset = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: allowSubmission ? 40 : 10)
    }
    private func addObserversForTextViewEvents() {
        NotificationCenter.default.addObserver(self, selector: #selector(didBeginEditing), name: UITextView.textDidBeginEditingNotification, object: self)
        NotificationCenter.default.addObserver(self, selector: #selector(didEndEditing), name: UITextView.textDidEndEditingNotification, object: self)
    }
    private func addSubmitButton() {
        guard let button = submitButton else { return }
        scrollIndicatorInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 40)
        addSubview(button)
        button.translatesAutoresizingMaskIntoConstraints = false
        button.translatesAutoresizingMaskIntoConstraints = false
        let width = button.widthAnchor.constraint(equalToConstant: 40)
        width.priority = .required
        width.isActive = true
        let top = button.topAnchor.constraint(equalTo: frameLayoutGuide.topAnchor, constant: 0)
        top.priority = .required
        top.isActive = true
        let bottom = button.bottomAnchor.constraint(equalTo: frameLayoutGuide.bottomAnchor, constant: 0)
        bottom.priority = .required
        bottom.isActive = true
        let trailing = button.trailingAnchor.constraint(equalTo: frameLayoutGuide.trailingAnchor, constant: 0)
        trailing.priority = .required
        trailing.isActive = true
    }
    @objc private func submitButtonTapped() { inputViewDelegate?.entryCompleteButtonTapped() }

    private func showPlaceholderText(_ show: Bool) {
        guard let placeholderText else { return }
        if show {
            text = placeholderText
            textColor = themeProvider.theme.textMutedExtra
            isShowingPlaceholder = true
        } else if isShowingPlaceholder {
            text = nil
            textColor = themeProvider.theme.textDefault
            isShowingPlaceholder = false
        }
    }
    @objc private func didBeginEditing() {
        inputViewDelegate?.inputFieldDidBeginEditing?()
        if placeholderText != nil && isShowingPlaceholder {
            text = nil
            textColor = themeProvider.theme.textDefault
            isShowingPlaceholder = false
        }
    }
    @objc private func didEndEditing() {
        inputViewDelegate?.inputFieldDidEndEditing?()
        showPlaceholderText(text!.isEmpty)
        if let delegate = inputViewDelegate {
            let value: Any?
            if isShowingPlaceholder || text!.isEmpty { value = nil }
            else { value = isShowingPlaceholder ? "" : text! }
            delegate.valueUpdated(value)
        }
    }

    @objc var inputValue: Any? {
        get {
            if isShowingPlaceholder || text!.isEmpty { return nil }
            return isShowingPlaceholder ? "" : text!
        }
        set {
            if let string = newValue as? String {
                showPlaceholderText(string.isEmpty)
                text = string
            } else if let placeholderText {
                text = placeholderText
                textColor = themeProvider.theme.textMutedExtra
                isShowingPlaceholder = true
            }
        }
    }

    @objc func isUserInputValid() throws {
        if !isRequired && (isShowingPlaceholder || text!.isEmpty) { return }
        var validators: [any ICMValidatorProtocol] = []
        if isRequired { validators.append(ICMEmptyInputValidator()) }
        if let characterLimit { validators.append(ICMCountLimitInputValidator(minimumCount: 0, maximumCount: characterLimit)) }
        let value = isShowingPlaceholder ? "" : text!
        try ICMValidator.validateText(value, validators: validators)
    }
    @objc func configureDefaultState() {
        inputAllowed = fieldIsEditable
        isEditable = fieldIsEditable
        submitButton?.isUserInteractionEnabled = inputAllowed
        layer.borderColor = themeProvider.theme.neutralBorder.cgColor
    }
    @objc func configureErrorState() {
        inputAllowed = fieldIsEditable
        isEditable = fieldIsEditable
        submitButton?.isUserInteractionEnabled = inputAllowed
        submitButton?.resetButtonState()
        layer.borderColor = themeProvider.theme.textError.cgColor
    }
    @objc func showInvalidState(_ message: String?) { configureErrorState() }
    @objc func configureSuccessState() {
        inputAllowed = false
        isEditable = false
        submitButton?.isUserInteractionEnabled = inputAllowed
        submitButton?.showSubmittedState()
    }
    @objc func configureLoadingState() {
        inputAllowed = false
        isEditable = false
        submitButton?.isUserInteractionEnabled = inputAllowed
        submitButton?.showLoadingState()
    }
    @objc func configureDisabledState() {
        inputAllowed = false
        isEditable = false
        submitButton?.isUserInteractionEnabled = inputAllowed
        textColor = themeProvider.theme.textMuted
        submitButton?.isEnabled = false
    }
}
