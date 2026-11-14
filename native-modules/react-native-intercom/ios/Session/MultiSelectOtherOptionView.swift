import UIKit

protocol MultiSelectOtherOptionViewDelegate: AnyObject {
    func otherTextFieldUpdated(_ value: String?)
    func otherTextFieldDidEndEditing()
}

@objc(_TtC18IntercomSDKPrivate26MultiSelectOtherOptionView)
class MultiSelectOtherOptionView: UIView, ICMInputViewDelegate, UITextFieldDelegate {
    lazy var textField: ICMTextField = {
        let properties = InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true)
        let field = ICMTextField(properties: properties, delegate: nil)!
        field.inputViewDelegate = self
        field.delegate = self
        field.isRequired = true
        field.borderStyle = .roundedRect
        field.placeholder = TranslationManager.messenger.translation(forKey: "surveys_multiselect_other_option_input_placeholder")
        field.translatesAutoresizingMaskIntoConstraints = false
        return field
    }()
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.text = TranslationManager.messenger.translation(forKey: "surveys_multiselect_other_option_input_label")
        label.font = .preferredFont(forTextStyle: .footnote)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private weak var delegate: (any MultiSelectOtherOptionViewDelegate)?

    init(delegate: (any MultiSelectOtherOptionViewDelegate)?) {
        self.delegate = delegate
        super.init(frame: .zero)
        addSubviews()
        translatesAutoresizingMaskIntoConstraints = false
        accessibilityIdentifier = "intercom multiselect question other view"
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private func addSubviews() {
        addSubview(titleLabel)
        addSubview(textField)
        titleLabel.topAnchor.constraint(equalTo: topAnchor, constant: 8).isActive = true
        titleLabel.leadingAnchor.constraint(equalTo: leadingAnchor).isActive = true
        titleLabel.trailingAnchor.constraint(equalTo: trailingAnchor).isActive = true
        textField.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 4).isActive = true
        textField.leadingAnchor.constraint(equalTo: leadingAnchor).isActive = true
        textField.trailingAnchor.constraint(equalTo: trailingAnchor).isActive = true
        textField.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        textField.heightAnchor.constraint(equalToConstant: 40).isActive = true
    }
    @objc func entryCompleteButtonTapped() {
        delegate?.otherTextFieldDidEndEditing()
        textField.resignFirstResponder()
    }
    @objc func cancelButtonTapped() { delegate?.otherTextFieldDidEndEditing() }
    @objc(valueUpdated:)
    func valueUpdated(_ value: Any?) {
        if let value {
            guard let string = value as? String else { return }
            delegate?.otherTextFieldUpdated(string)
        } else {
            delegate?.otherTextFieldUpdated(nil)
        }
    }
    @objc func inputFieldDidBeginEditing() {}
    @objc func inputFieldDidEndEditing() {}
    func textFieldDidEndEditing(_ textField: UITextField) { delegate?.otherTextFieldDidEndEditing() }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
}
