import Foundation

enum FormAttributeInputType {
    case text, email, phone, options, integer, float, date, boolean, website, unknown, dateTime, multiline, files
}

@objc(_TtC18IntercomSDKPrivate13FormAttribute)
class FormAttribute: NSObject, NSSecureCoding {
    @objc let customBotId: String?
    @objc let identifier: String?
    @objc let name: String
    @objc let type: String?
    @objc let required: Bool
    @objc let options: [String]?
    @objc let maximumSelection: Int
    @objc var value: Any?
    @objc let attributeId: String?
    @objc var submitted: Bool
    @objc var isFormDisabled: Bool
    @objc let multiline: Bool
    @objc let isControllingAttribute: Bool
    @objc let listOptions: [TicketFormResponseListOption]?

    @objc static var supportsSecureCoding: Bool { true }

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let name = dictionary["name"] as? String else { return nil }
        var options: [String]?
        var listOptions: [TicketFormResponseListOption]?
        if let details = dictionary["options"] as? [String: Any] {
            if let values = details["list_options"] as? [[String: Any]] {
                options = []
                listOptions = []
                for option in values {
                    guard let label = option["label"] as? String, let id = option["id"] as? String else { continue }
                    let archived = option["archived"] as? Bool ?? false
                    listOptions!.append(TicketFormResponseListOption(label: label, archived: archived, id: id))
                    options!.append(label)
                }
            }
        } else { options = dictionary["options"] as? [String] }
        customBotId = dictionary["custom_bot_control_id"] as? String
        identifier = dictionary["identifier"] as? String
        self.name = name
        type = dictionary["type"] as? String
        required = dictionary["required"] as? Bool ?? false
        self.options = options
        maximumSelection = dictionary["maximum_selection"] as? Int ?? 10
        value = dictionary["value"]
        attributeId = dictionary["id"] as? String
        submitted = dictionary["submitted"] as? Bool ?? false
        isFormDisabled = dictionary["disabled"] as? Bool ?? false
        multiline = dictionary["multiline"] as? Bool ?? false
        isControllingAttribute = dictionary["is_controlling_attribute"] as? Bool ?? false
        self.listOptions = listOptions
        super.init()
    }

    // Materializes the original ticket parser's inlined field stores, not the general form parser.
    init?(ticketAttribute dictionary: [String: Any]) {
        guard let name = dictionary["name"] as? String, let type = dictionary["type"] as? String else { return nil }
        customBotId = dictionary["customBotId"] as? String
        identifier = dictionary["identifier"] as? String
        self.name = name
        self.type = type
        required = false
        options = dictionary["options"] as? [String]
        maximumSelection = 10
        value = dictionary["value"]
        attributeId = nil
        submitted = false
        isFormDisabled = false
        multiline = false
        isControllingAttribute = false
        listOptions = nil
        super.init()
    }

    // Materializes the original Create builder's inlined identity-email initializer.
    init(identityEmailName: String, value: String?) {
        customBotId = nil
        identifier = "email"
        name = identityEmailName
        type = "string"
        required = true
        options = nil
        maximumSelection = 10
        self.value = value
        attributeId = nil
        submitted = false
        isFormDisabled = false
        multiline = false
        isControllingAttribute = false
        listOptions = nil
        super.init()
    }

    init?(with field: TicketFormResponseFormField) {
        customBotId = nil
        identifier = field.identifier
        name = field.name
        type = field.type
        required = field.required
        options = field.options?.listOptions?.map { $0.label }
        maximumSelection = 10
        value = nil
        attributeId = field.id
        submitted = false
        isFormDisabled = false
        multiline = false
        isControllingAttribute = field.isControllingAttribute
        listOptions = field.options?.listOptions
        super.init()
    }

    required init?(coder: NSCoder) {
        guard let name = coder.decodeObject(of: NSString.self, forKey: "name") as String? else { return nil }
        customBotId = coder.decodeObject(of: NSString.self, forKey: "customBotId") as String?
        identifier = coder.decodeObject(of: NSString.self, forKey: "identifier") as String?
        self.name = name
        type = coder.decodeObject(of: NSString.self, forKey: "type") as String?
        options = coder.decodeObject(of: [NSArray.self, NSString.self], forKey: "options") as? [String]
        maximumSelection = coder.containsValue(forKey: "maximumSelection") ? coder.decodeInteger(forKey: "maximumSelection") : 10
        value = coder.decodeObject(of: [NSArray.self, NSDictionary.self, NSString.self, NSNumber.self, NSNull.self], forKey: "value")
        required = coder.decodeBool(forKey: "required")
        submitted = coder.decodeBool(forKey: "submitted")
        isControllingAttribute = coder.decodeBool(forKey: "isControllingAttribute")
        attributeId = coder.decodeObject(of: NSString.self, forKey: "attributeId") as String?
        multiline = coder.decodeBool(forKey: "multiline")
        listOptions = coder.decodeObject(of: [NSArray.self, TicketFormResponseListOption.self], forKey: "listOptions") as? [TicketFormResponseListOption]
        isFormDisabled = false
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(customBotId, forKey: "customBotId")
        coder.encode(identifier, forKey: "identifier")
        coder.encode(name, forKey: "name")
        coder.encode(type, forKey: "type")
        coder.encode(options, forKey: "options")
        coder.encode(maximumSelection, forKey: "maximumSelection")
        coder.encode(value, forKey: "value")
        coder.encode(required, forKey: "required")
        coder.encode(submitted, forKey: "submitted")
        coder.encode(isControllingAttribute, forKey: "isControllingAttribute")
        coder.encode(attributeId, forKey: "attributeId")
        coder.encode(multiline, forKey: "multiline")
        coder.encode(listOptions, forKey: "listOptions")
    }

    override init() { fatalError("init() has not been implemented") }

    @objc var uniqueId: String { (customBotId ?? "") + (identifier ?? "") }
    @objc var isMultipleFilesUploadAttribute: Bool { type == "files" && identifier == "multiple" }

    var formInputType: FormAttributeInputType {
        if let options = options, !options.isEmpty { return .options }
        guard let type = type else { return .unknown }
        switch type {
        case "string":
            switch identifier {
            case "phone": return .phone
            case "email": return .email
            case "company.website": return .website
            case "multiline": return .multiline
            default: return multiline ? .multiline : .text
            }
        case "float": return .float
        case "integer": return .integer
        case "boolean": return .boolean
        case "date": return .date
        case "datetime": return .dateTime
        case "files": return .files
        default: return .unknown
        }
    }

    @objc func createInputProperties() -> InputProperties {
        InputProperties(initialValue: value, allowSubmission: true, valueIsEditable: !isFormDisabled && !submitted)
    }

    override var hash: Int {
        var hasher = Hasher()
        hasher.combine(customBotId)
        hasher.combine(identifier)
        hasher.combine(name)
        hasher.combine(type)
        hasher.combine(options)
        hasher.combine(maximumSelection)
        hasher.combine(required)
        hasher.combine(submitted)
        hasher.combine(isControllingAttribute)
        hasher.combine(attributeId)
        return hasher.finalize()
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return customBotId == other.customBotId && identifier == other.identifier && name == other.name && type == other.type &&
            required == other.required && options == other.options && attributeId == other.attributeId &&
            submitted == other.submitted && multiline == other.multiline && isControllingAttribute == other.isControllingAttribute &&
            maximumSelection == other.maximumSelection && listOptions == other.listOptions
    }

    @objc(isEqualToFormAttribute:)
    func isEqualToFormAttribute(_ other: FormAttribute) -> Bool {
        customBotId == other.customBotId && identifier == other.identifier && name == other.name && type == other.type &&
            required == other.required && options == other.options && attributeId == other.attributeId &&
            submitted == other.submitted && multiline == other.multiline && isControllingAttribute == other.isControllingAttribute &&
            maximumSelection == other.maximumSelection && listOptions == other.listOptions
    }

    enum StorageType {
        case error, localValue
        var key: String { self == .localValue ? "formLocalValueKey" : "formErrorKey" }
    }

    func store(_ value: Any, type: StorageType) {
        var values = UserDefaults.standard.object(forKey: type.key) as? [AnyHashable: Any] ?? [:]
        values[uniqueId] = value
        UserDefaults.standard.set(values, forKey: type.key)
    }
    func fetch(type: StorageType) -> Any? {
        let values = UserDefaults.standard.object(forKey: type.key) as? [AnyHashable: Any] ?? [:]
        return values[uniqueId]
    }
    func clear(type: StorageType) {
        var values = UserDefaults.standard.object(forKey: type.key) as? [AnyHashable: Any] ?? [:]
        values.removeValue(forKey: uniqueId)
        UserDefaults.standard.set(values, forKey: type.key)
    }
    @objc static func clearAll() {
        UserDefaults.standard.set(nil as URL?, forKey: "formErrorKey")
        UserDefaults.standard.set(nil as URL?, forKey: "formLocalValueKey")
    }
}
