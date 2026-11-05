import UIKit

@objc(_TtC18IntercomSDKPrivate18AttributeInputView)
class AttributeInputView: UIView, ICMInputViewDelegate, UITextFieldDelegate {
    var formIndex: Int
    var formInputView: (any ICMSubmittableInputViewProtocol)?
    weak var delegate: AttributePartView?
    var formAttribute: FormAttribute
    private var inputHeightConstraint: NSLayoutConstraint?
    private let themeProvider: ThemeProvider
    private lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.spacing = 8
        return stack
    }()
    private lazy var titleCounterStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        return stack
    }()
    lazy var counterLabel: UILabel = {
        let label = UILabel()
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.textColor = themeProvider.theme.textMuted
        label.accessibilityIdentifier = "intercom form counter"
        label.numberOfLines = 1
        label.lineBreakMode = .byTruncatingTail
        label.textAlignment = .natural
        label.translatesAutoresizingMaskIntoConstraints = false
        label.setContentHuggingPriority(UILayoutPriority(rawValue: 999), for: .horizontal)
        label.setContentCompressionResistancePriority(UILayoutPriority(rawValue: 999), for: .horizontal)
        label.adjustsFontForContentSizeCategory = true
        return label
    }()
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.textColor = themeProvider.theme.textDefault
        label.numberOfLines = 2
        label.lineBreakMode = .byTruncatingTail
        label.textAlignment = .natural
        label.translatesAutoresizingMaskIntoConstraints = false
        label.adjustsFontForContentSizeCategory = true
        return label
    }()
    private lazy var errorLabel: UILabel = {
        let label = UILabel()
        label.font = .preferredFont(forTextStyle: .footnote)
        label.accessibilityIdentifier = "intercom form input error label"
        label.numberOfLines = 3
        label.lineBreakMode = .byTruncatingTail
        label.textAlignment = .natural
        label.translatesAutoresizingMaskIntoConstraints = false
        label.setContentCompressionResistancePriority(UILayoutPriority(rawValue: 999), for: .vertical)
        label.adjustsFontForContentSizeCategory = true
        return label
    }()
    init(formAttribute: FormAttribute, delegate: AttributePartView?, formIndex: Int, themeProvider: ThemeProvider) {
        self.formIndex = formIndex
        self.delegate = delegate
        self.formAttribute = formAttribute
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        accessibilityIdentifier = "intercom form "
        addSubview(containerStackView)
        containerStackView.addArrangedSubview(titleCounterStackView)
        pinEdges(containerStackView)
        pinHorizontal(titleCounterStackView)
        addTitleLabel()
        if formIndex != 0 { titleCounterStackView.addArrangedSubview(counterLabel) }
        addInputView()
        addErrorLabel()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func addTitleLabel() {
        titleCounterStackView.addArrangedSubview(titleLabel)
        titleLabel.text = (formIndex >= 1 ? String(formIndex) + ". " : "") + formAttribute.name
        titleLabel.accessibilityIdentifier = "intercom input title " + (formIndex >= 1 ? String(formIndex) : "")
    }
    private func addInputView() {
        switch formAttribute.formInputType {
        case .text: configureInput(ICMTextField(properties: formAttribute.createInputProperties(), delegate: self), suffix: "string")
        case .email: configureInput(ICMEmailTextField(properties: formAttribute.createInputProperties(), delegate: self), suffix: "email")
        case .phone: configureInput(ICMPhoneTextField(properties: formAttribute.createInputProperties(), delegate: self), suffix: "phone")
        case .options:
            if let options = formAttribute.options {
                configureInput(ICMPickerTextField(properties: formAttribute.createInputProperties(), options: options, delegate: self), suffix: "picker")
            }
        case .integer: configureInput(ICMIntegerTextField(properties: formAttribute.createInputProperties(), delegate: self), suffix: "integer")
        case .float: configureInput(ICMFloatTextField(properties: formAttribute.createInputProperties(), delegate: self), suffix: "float")
        case .date: configureInput(ICMDateTextField(properties: formAttribute.createInputProperties(), allowTimeSelection: false, delegate: self), suffix: "date")
        case .boolean: configureInput(ICMBooleanInputView(properties: formAttribute.createInputProperties(), delegate: self, options: 5), suffix: "boolean")
        case .website: configureInput(ICMWebsiteTextField(properties: formAttribute.createInputProperties(), delegate: self), suffix: "website")
        case .multiline:
            let view = TextInputView(inputViewDelegate: self, inputProperties: formAttribute.createInputProperties(), themeProvider: DefaultThemeProvider.shared)
            view.font = .preferredFont(forTextStyle: .subheadline)
            if let value = formAttribute.fetch(type: .localValue) as? String { view.text = value }
            configureInput(view, suffix: "multiline string")
        case .unknown, .dateTime, .files: break
        }
        formInputView?.isRequired = true
    }
    private func configureInput(_ input: (any ICMSubmittableInputViewProtocol)?, suffix: String) {
        formInputView = input
        applyAccessibilityIdentifier(suffix)
        addFormInputToView()
    }
    private func applyAccessibilityIdentifier(_ suffix: String) {
        accessibilityIdentifier = accessibilityIdentifier.map { $0 + suffix }
    }
    private func addFormInputToView() {
        guard let view = formInputView as? UIView else { return }
        if view.isKind(of: ICMTextField.self), let textField = view as? ICMTextField { textField.delegate = self }
        containerStackView.addArrangedSubview(view)
        pinHorizontal(view)
        let height = measuredInputHeight()
        view.translatesAutoresizingMaskIntoConstraints = false
        let constraint = view.heightAnchor.constraint(equalToConstant: height)
        constraint.priority = .required
        constraint.isActive = true
        inputHeightConstraint = constraint
    }
    private func measuredInputHeight() -> CGFloat {
        guard let text = formInputView as? TextInputView else { return 40 }
        let height = text.sizeThatFits(text.bounds.size).height
        return fmin(fmax(height, 60), 70)
    }
    private func addErrorLabel() {
        containerStackView.addArrangedSubview(errorLabel)
        pinHorizontal(errorLabel)
        let label = errorLabel
        let parent = label.superview!
        label.translatesAutoresizingMaskIntoConstraints = false
        label.translatesAutoresizingMaskIntoConstraints = false
        label.bottomAnchor.constraint(equalTo: parent.bottomAnchor, constant: -CGFloat.zero).isActive = true
        errorLabel.textColor = themeProvider.theme.textError
    }
    override var intrinsicContentSize: CGSize { containerStackView.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize) }
    override func layoutSubviews() {
        super.layoutSubviews()
        let message = formAttribute.fetch(type: .error) as? String ?? ""
        errorLabel.isHidden = message.isEmpty
        errorLabel.text = message
        if let constraint = inputHeightConstraint { constraint.constant = measuredInputHeight() }
        invalidateIntrinsicContentSize()
    }
    @objc func entryCompleteButtonTapped() {
        resetErrorState()
        do { try formInputView?.isUserInputValid() }
        catch { handleSubmitError(error); return }
        formInputView?.configureLoadingState()
        formAttribute.value = formInputView?.inputValue
        formAttribute.clear(type: .localValue)
        guard let part = delegate else { return }
        let attribute = formAttribute
        part.content.state = 1
        part.delegate?.submitButtonTapped(attribute, conversationPart: part.content, successBlock: {
            ICMFeedbackGenerator.feedback(withType: 2)
        }, failureBlock: { [weak self] _ in
            let error = NSError(domain: "IntercomSDKErrorDomain", code: 1000, userInfo: [NSLocalizedDescriptionKey: TranslationManager.messenger.translation(forKey: "something_went_wrong_try_again")])
            self?.handleSubmitError(error)
        })
    }
    private func resetErrorState() {
        formAttribute.clear(type: .error)
        if formAttribute.formInputType != .boolean { formInputView?.configureDefaultState() }
        errorLabel.isHidden = true
        invalidateIntrinsicContentSize()
        if let part = delegate, let blockDelegate = part.delegate { blockDelegate.invalidateLayoutForPart(with: part.content.partId!) }
    }
    private func handleSubmitError(_ error: Error) {
        ICMFeedbackGenerator.feedback(withType: 1)
        formAttribute.store(error.localizedDescription, type: .error)
        formInputView?.configureErrorState()
        let label = errorLabel
        label.text = error.localizedDescription
        label.isHidden = false
        invalidateIntrinsicContentSize()
        if let part = delegate, let blockDelegate = part.delegate { blockDelegate.invalidateLayoutForPart(with: part.content.partId!) }
    }
    @objc func cancelButtonTapped() { resetErrorState() }
    @objc func valueUpdated(_ value: Any?) {
        resetErrorState()
        if let value { formAttribute.store(value, type: .localValue) }
        else { formAttribute.clear(type: .localValue) }
        if let constraint = inputHeightConstraint { constraint.constant = measuredInputHeight() }
        invalidateIntrinsicContentSize()
    }
    @objc func inputFieldDidBeginEditing() {
        let observer = delegate
        withExtendedLifetime(observer) {}
    }
    @objc func inputFieldDidEndEditing() {
        let observer = delegate
        withExtendedLifetime(observer) {}
    }
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        resetErrorState()
        if formAttribute.formInputType == .options { return false }
        return formAttribute.formInputType != .date
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        formInputView?.configureLoadingState()
        entryCompleteButtonTapped()
        return true
    }
    private func pinHorizontal(_ view: UIView) {
        let rtl = view.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.leftAnchor.constraint(equalTo: parent.leftAnchor).isActive = true
        }
        func right() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.rightAnchor.constraint(equalTo: parent.rightAnchor, constant: -CGFloat.zero).isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
    }
    private func pinEdges(_ view: UIView) {
        let topParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.topAnchor.constraint(equalTo: topParent.topAnchor).isActive = true
        let rtl = view.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.leftAnchor.constraint(equalTo: parent.leftAnchor).isActive = true
        }
        func right() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.rightAnchor.constraint(equalTo: parent.rightAnchor, constant: -CGFloat.zero).isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
        let bottomParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.bottomAnchor.constraint(equalTo: bottomParent.bottomAnchor, constant: -CGFloat.zero).isActive = true
    }
}
