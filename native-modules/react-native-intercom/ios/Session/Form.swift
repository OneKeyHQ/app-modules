// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate4Form)
class Form: NSObject {
    @objc let formAttributes: [FormAttribute]
    @objc let disabled: Bool
    @objc let type: String
    @objc required init(formAttributes: [FormAttribute], disabled: Bool, type: String) {
        self.formAttributes = formAttributes
        self.disabled = disabled
        self.type = type
        super.init()
    }
    @objc(initWithDictionary:)
    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let attributes = dictionary["attributes"] as? [[String: Any]],
              let type = dictionary["type"] as? String,
              let disabled = dictionary["disabled"] as? Bool else { return nil }
        let parsed = attributes.compactMap { FormAttribute(withDictionary: $0) }
        guard !parsed.isEmpty else { return nil }
        self.init(formAttributes: parsed, disabled: disabled, type: type)
    }
    override init() { fatalError("init() has not been implemented") }
    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return type == other.type && disabled == other.disabled && formAttributes == other.formAttributes
    }
    override var hash: Int {
        var hasher = Hasher()
        hasher.combine(formAttributes)
        hasher.combine(disabled)
        hasher.combine(type)
        return hasher.finalize()
    }
}
