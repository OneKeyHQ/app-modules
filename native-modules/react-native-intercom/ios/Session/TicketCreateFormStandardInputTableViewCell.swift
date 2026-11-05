import UIKit

@objc(_TtC18IntercomSDKPrivate42TicketCreateFormStandardInputTableViewCell)
class TicketCreateFormStandardInputTableViewCell: TicketCreateFormInputTableViewCell, ICMInputViewDelegate, UITextFieldDelegate, UITextViewDelegate {
    private var inputField: (any ICMInputViewProtocol)?
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    override func configure(with item: any TicketCreateFormCellItem) {
        super.configure(with: item)
        cellItem = item as? any TicketCreateFormInputCellItem
        inputField = buildInputField()
        addSubviews()
        if let standard = item as? TicketCreateFormStandardInputCellItem { standard.cellDelegate = self }
        guard let input = item as? any TicketCreateFormInputCellItem else { return }
        setTitleText(input.attribute.name)
        setDetailText(input.detail)
        if input.attribute.value != nil, let field = inputField { field.inputValue = input.attribute.value }
        setupInputValidation()
        setupAccessibility(with: input)
    }
    override var formInputView: UIView? { inputField as? UIView }
    func buildInputField() -> (any ICMInputViewProtocol)? {
        ICMTextField(properties: InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true), delegate: nil)
    }
    override func validate() {
        if let standard = cellItem as? TicketCreateFormStandardInputCellItem,
           standard.userIsIdentified, let field = inputField, field.isKind(of: ICMEmailTextField.self) {
            standard.valid = true
            standard.validityMessage = nil
            return
        }
        do {
            try inputField?.isUserInputValid()
            cellItem?.valid = true
            cellItem?.validityMessage = nil
            if let field = inputField, !field.isKind(of: ICMBooleanInputView.self) { inputField?.configureDefaultState() }
            validityMessageView.isHidden = true
        } catch {
            let error = error as NSError
            cellItem?.valid = false
            cellItem?.validityMessage = error.localizedDescription
            validityMessageLabel.text = error.localizedDescription
            inputField?.configureErrorState()
            validityMessageView.isHidden = false
        }
        contentView.layoutIfNeeded()
        if let standard = cellItem as? TicketCreateFormStandardInputCellItem {
            let refresh = standard.refreshAction
            refresh()
        }
    }
    private func setupInputValidation() {
        do { try inputField?.isUserInputValid(); cellItem?.valid = true }
        catch { cellItem?.valid = false }
        validityMessageView.isHidden = cellItem?.validityMessage == nil
        validityMessageLabel.text = cellItem?.validityMessage
    }
    @objc func entryCompleteButtonTapped() {
        validate()
        cellItem?.attribute.value = inputField?.inputValue
        (inputField as? UIView)?.resignFirstResponder()
    }
    @objc func cancelButtonTapped() {
        validate()
        cellItem?.attribute.value = nil
        (inputField as? UIView)?.resignFirstResponder()
    }
    @objc func valueUpdated(_ value: Any?) {
        validate()
        cellItem?.attribute.value = inputField?.inputValue
        (cellItem as? TicketCreateFormStandardInputCellItem)?.onValueChange?(value)
    }
    @objc func inputFieldDidBeginEditing() {}
    @objc func inputFieldDidEndEditing() {}
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        (inputField as? UIView)?.resignFirstResponder()
        return true
    }
    func textViewDidEndEditing(_ textView: UITextView) { (inputField as? UIView)?.resignFirstResponder() }
    override func prepareForReuse() {
        super.prepareForReuse()
        inputField?.inputValue = nil
    }
    private func setupAccessibility(with item: any TicketCreateFormInputCellItem) {
        guard let item = item as? TicketCreateFormStandardInputCellItem else { return }
        accessibilityElements = [titleLabel, inputField as Any]
        var title = item.attribute.name + "; "
        if let detail = item.detail { title += detail + "; " }
        if item.attribute.required { title += TranslationManager.messenger.translation(forKey: "surveys_required_response") + "; " }
        titleLabel.accessibilityLabel = title
    }
}
