import UIKit

@objc(_TtC18IntercomSDKPrivate36TicketCreateFormBooleanTableViewCell)
class TicketCreateFormBooleanTableViewCell: TicketCreateFormStandardInputTableViewCell {
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    override func buildInputField() -> (any ICMInputViewProtocol)? {
        let positive = TranslationManager.messenger.translation(forKey: "attribute_collector_positive")
        let negative = TranslationManager.messenger.translation(forKey: "attribute_collector_negative")
        let field = MultiSelectInputView(options: [positive, negative], minimumSelection: 1, maximumSelection: 1, includeOtherOption: false, inputViewDelegate: self)
        field.isRequired = cellItem?.attribute.required ?? false
        field.configure(selectedOptionTintColor: DefaultThemeProvider.shared.theme.primaryBackground)
        return field
    }
}

@objc(_TtC18IntercomSDKPrivate37TicketCreateFormDateTimeTableViewCell)
class TicketCreateFormDateTimeTableViewCell: TicketCreateFormStandardInputTableViewCell {
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    override func buildInputField() -> (any ICMInputViewProtocol)? {
        let properties = InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true)
        let field = ICMDateTextField(properties: properties, allowTimeSelection: true, delegate: self)
        field?.font = .preferredFont(forTextStyle: .body)
        field?.placeholder = TranslationManager.messenger.translation(forKey: "please_select")
        field?.borderStyle = .roundedRect
        field?.isRequired = cellItem?.attribute.required ?? false
        field?.translatesAutoresizingMaskIntoConstraints = false
        let height = field?.heightAnchor.constraint(equalToConstant: 40)
        height?.priority = .required
        height?.isActive = true
        return field
    }
}

@objc(_TtC18IntercomSDKPrivate37TicketCreateFormLongTextTableViewCell)
class TicketCreateFormLongTextTableViewCell: TicketCreateFormStandardInputTableViewCell {
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    override func buildInputField() -> (any ICMInputViewProtocol)? {
        let properties = InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true)
        let provider = DefaultThemeProvider.shared
        let field = TextInputView(inputViewDelegate: self, inputProperties: properties, themeProvider: provider)
        field.font = .preferredFont(forTextStyle: .body)
        field.placeholderText = TranslationManager.messenger.translation(forKey: "placeholder_text_inputs")
        field.isRequired = cellItem?.attribute.required ?? false
        field.delegate = self
        field.translatesAutoresizingMaskIntoConstraints = false
        let height = field.heightAnchor.constraint(equalToConstant: 100)
        height.priority = .required
        height.isActive = true
        return field
    }
}

@objc(_TtC18IntercomSDKPrivate35TicketCreateFormPickerTableViewCell)
class TicketCreateFormPickerTableViewCell: TicketCreateFormStandardInputTableViewCell {
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    override func buildInputField() -> (any ICMInputViewProtocol)? {
        let labels = (cellItem?.attribute.listOptions ?? []).map { $0.label }
        let properties = InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true)
        let field = ICMPickerTextField(properties: properties, options: labels as [Any], delegate: self)
        field?.configure(forEditingStateBackgroundColor: DefaultThemeProvider.shared.theme.primaryBackground)
        field?.borderStyle = .roundedRect
        field?.isRequired = cellItem?.attribute.required ?? false
        field?.translatesAutoresizingMaskIntoConstraints = false
        let height = field?.heightAnchor.constraint(equalToConstant: 40)
        height?.priority = .required
        height?.isActive = true
        return field
    }
}

@objc(_TtC18IntercomSDKPrivate34TicketCreateFormFloatTableViewCell)
class TicketCreateFormFloatTableViewCell: TicketCreateFormStandardInputTableViewCell {
    private lazy var inputField: ICMTextField? = ICMFloatTextField(properties: InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true), delegate: self)
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    override func buildInputField() -> (any ICMInputViewProtocol)? {
        inputField?.font = .preferredFont(forTextStyle: .body)
        inputField?.placeholder = TranslationManager.messenger.translation(forKey: "placeholder_numerical_inputs")
        inputField?.borderStyle = .roundedRect
        inputField?.isRequired = cellItem?.attribute.required ?? false
        inputField?.delegate = self
        inputField?.translatesAutoresizingMaskIntoConstraints = false
        let height = inputField?.heightAnchor.constraint(equalToConstant: 40)
        height?.priority = .required
        height?.isActive = true
        return inputField
    }
}

@objc(_TtC18IntercomSDKPrivate36TicketCreateFormIntegerTableViewCell)
class TicketCreateFormIntegerTableViewCell: TicketCreateFormStandardInputTableViewCell {
    private lazy var inputField: ICMTextField? = ICMIntegerTextField(properties: InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true), delegate: self)
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    override func buildInputField() -> (any ICMInputViewProtocol)? {
        inputField?.font = .preferredFont(forTextStyle: .body)
        inputField?.placeholder = TranslationManager.messenger.translation(forKey: "placeholder_numerical_inputs")
        inputField?.borderStyle = .roundedRect
        inputField?.isRequired = cellItem?.attribute.required ?? false
        inputField?.delegate = self
        inputField?.translatesAutoresizingMaskIntoConstraints = false
        let height = inputField?.heightAnchor.constraint(equalToConstant: 40)
        height?.priority = .required
        height?.isActive = true
        return inputField
    }
}

@objc(_TtC18IntercomSDKPrivate33TicketCreateFormTextTableViewCell)
class TicketCreateFormTextTableViewCell: TicketCreateFormStandardInputTableViewCell {
    private lazy var inputField: ICMTextField? = ICMTextField(properties: InputProperties(initialValue: nil, allowSubmission: false, valueIsEditable: true), delegate: self)
    private let themeProvider = DefaultThemeProvider.shared
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    override func buildInputField() -> (any ICMInputViewProtocol)? {
        inputField?.font = .preferredFont(forTextStyle: .body)
        inputField?.placeholder = TranslationManager.messenger.translation(forKey: "placeholder_text_inputs")
        inputField?.tintColor = themeProvider.theme.textDefault
        inputField?.borderStyle = .roundedRect
        inputField?.isRequired = cellItem?.attribute.required ?? false
        inputField?.delegate = self
        inputField?.translatesAutoresizingMaskIntoConstraints = false
        let height = inputField?.heightAnchor.constraint(equalToConstant: 40)
        height?.priority = .required
        height?.isActive = true
        return inputField
    }
}

@objc(_TtC18IntercomSDKPrivate34TicketCreateFormEmailTableViewCell)
class TicketCreateFormEmailTableViewCell: TicketCreateFormStandardInputTableViewCell {
    private let themeProvider = DefaultThemeProvider.shared
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    override func buildInputField() -> (any ICMInputViewProtocol)? {
        let initial = cellItem?.attribute.value
        let allowSubmission = !(cellItem?.isEnabled ?? false)
        let properties = InputProperties(initialValue: initial, allowSubmission: allowSubmission, valueIsEditable: true)
        let field = ICMEmailTextField(properties: properties, delegate: self)
        field?.font = .preferredFont(forTextStyle: .body)
        field?.placeholder = TranslationManager.messenger.translation(forKey: "placeholder_email_input")
        field?.borderStyle = .roundedRect
        field?.isRequired = cellItem?.attribute.required ?? false
        if let item = cellItem, !item.isEnabled { field?.configureSuccessState() }
        else { field?.configureDefaultState() }
        let color: UIColor?
        if let item = cellItem, !item.isEnabled { color = themeProvider.theme.textMuted }
        else { color = field?.textColor }
        field?.textColor = color
        field?.backgroundColor = themeProvider.theme.inputAlt
        field?.delegate = self
        if let item = cellItem, !item.isEnabled { field?.accessibilityTraits = .notEnabled }
        field?.translatesAutoresizingMaskIntoConstraints = false
        let height = field?.heightAnchor.constraint(equalToConstant: 40)
        height?.priority = .required
        height?.isActive = true
        return field
    }
}
