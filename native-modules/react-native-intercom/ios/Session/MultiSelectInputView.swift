import UIKit

@objc(_TtC18IntercomSDKPrivate20MultiSelectInputView)
class MultiSelectInputView: UIView, ICMInputViewProtocol, MultiSelectOtherOptionViewDelegate {
    @objc var isRequired = false
    private lazy var otherOptionView = MultiSelectOtherOptionView(delegate: self)
    private let options: [String]
    private let maximumSelectionCount: Int
    private let minimumSelectionCount: Int
    private let otherOptionAllowed: Bool
    private weak var delegate: (any ICMInputViewDelegate)?
    private var selectedOptions: Set<String> = []
    private lazy var stackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.spacing = 4
        stack.distribution = .equalSpacing
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private var buttons: [MultiSelectButton] = []
    private var otherOptionButton: MultiSelectButton?
    private var otherOptionSelected = false

    init(options: [String], minimumSelection: Int, maximumSelection: Int, includeOtherOption: Bool,
         inputViewDelegate: (any ICMInputViewDelegate)?) {
        self.options = options
        maximumSelectionCount = maximumSelection
        minimumSelectionCount = minimumSelection
        otherOptionAllowed = includeOtherOption
        delegate = inputViewDelegate
        super.init(frame: .zero)
        accessibilityIdentifier = "intercom multiselect question view"
        setupOptionStackView()
        setupOptionButtons()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private func setupOptionStackView() {
        addSubview(stackView)
        stackView.leadingAnchor.constraint(equalTo: leadingAnchor).isActive = true
        stackView.trailingAnchor.constraint(equalTo: trailingAnchor).isActive = true
        stackView.topAnchor.constraint(equalTo: topAnchor).isActive = true
        stackView.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
    }
    private func setupOptionButtons() {
        for option in options {
            let button = MultiSelectButton(withOption: option, themeProvider: DefaultThemeProvider.shared, optionTapped: optionTapped)
            buttons.append(button)
            stackView.addArrangedSubview(button)
        }
        if otherOptionAllowed {
            let title = TranslationManager.messenger.translation(forKey: "surveys_multiselect_other_option")
            let button = MultiSelectButton(withOption: title, themeProvider: DefaultThemeProvider.shared, optionTapped: otherOptionTapped)
            stackView.addArrangedSubview(button)
            stackView.addArrangedSubview(otherOptionView)
            otherOptionView.isHidden = true
            otherOptionButton = button
        }
    }
    func configure(selectedOptionTintColor: UIColor) {
        var currentButtons = buttons
        if let otherOptionButton { currentButtons.append(otherOptionButton) }
        for button in currentButtons { button.configure(selectedTintColor: selectedOptionTintColor) }
        otherOptionView.textField.tintColor = selectedOptionTintColor
    }
    private var allResponses: [String]? {
        var responses = Array(selectedOptions)
        if otherOptionSelected {
            let text = otherOptionView.textField.text
            if let text, !text.isEmpty { responses.append(text) }
            else { responses.append(TranslationManager.messenger.translation(forKey: "surveys_multiselect_other_option_value")) }
        }
        return responses.isEmpty ? nil : responses
    }
    @objc var inputValue: Any? {
        get { allResponses }
        set {
            selectedOptions = []
            if let values = newValue as? [String] { selectedOptions = Set(values) }
            for button in buttons { button.isSelected = selectedOptions.contains(button.option) }
        }
    }
    private func optionTapped(_ option: String) {
        if selectedOptions.contains(option) {
            selectedOptions.remove(option)
        } else {
            if maximumSelectionCount == 1 {
                selectedOptions = []
                otherOptionSelected = false
                otherOptionButton?.isSelected = false
                otherOptionView.isHidden = !otherOptionSelected
            }
            if selectedOptions.count + (otherOptionSelected ? 1 : 0) < maximumSelectionCount { selectedOptions.insert(option) }
        }
        for button in buttons { button.isSelected = selectedOptions.contains(button.option) }
        if let delegate { delegate.valueUpdated(allResponses) }
    }
    private func otherOptionTapped(_ option: String) {
        if otherOptionSelected {
            otherOptionSelected = false
            otherOptionButton?.isSelected = false
            otherOptionView.isHidden = true
        } else {
            if maximumSelectionCount == 1 {
                selectedOptions = []
                otherOptionSelected = false
                otherOptionButton?.isSelected = false
                otherOptionView.isHidden = !otherOptionSelected
            }
            if selectedOptions.count + (otherOptionSelected ? 1 : 0) < maximumSelectionCount {
                otherOptionSelected = true
                otherOptionButton?.isSelected = true
                otherOptionView.isHidden = false
            }
        }
        for button in buttons { button.isSelected = selectedOptions.contains(button.option) }
        if let delegate { delegate.valueUpdated(allResponses) }
    }
    func otherTextFieldUpdated(_ value: String?) {
        if let delegate { delegate.valueUpdated(allResponses) }
    }
    func otherTextFieldDidEndEditing() {
        if let delegate { delegate.valueUpdated(allResponses) }
        if let textDelegate = delegate as? any UITextFieldDelegate {
            textDelegate.textFieldDidEndEditing?(otherOptionView.textField)
        }
    }
    @objc func isUserInputValid() throws {
        var validators: [any ICMValidatorProtocol] = []
        if isRequired { validators.append(ICMEmptyInputValidator()) }
        else if selectedOptions.isEmpty && !otherOptionSelected { return }
        validators.append(ICMCountLimitInputValidator(minimumCount: minimumSelectionCount, maximumCount: maximumSelectionCount))
        try ICMValidator.validate(allResponses, validators: validators)
    }
    @objc func configureErrorState() {
        do {
            try ICMValidator.validateText(otherOptionView.textField.text, validators: [ICMEmptyInputValidator()])
            otherOptionView.textField.configureDefaultState()
        } catch {
            otherOptionView.textField.configureErrorState()
        }
    }
    @objc func configureDefaultState() { otherOptionView.textField.configureDefaultState() }
}
