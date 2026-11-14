import Foundation

@objc(_TtC18IntercomSDKPrivate7UIFlags)
class UIFlags: NSObject, DictionaryInitializable {
    @objc let showLastPartMetadata: Bool
    @objc(initWithDictionary:)
    required init?(withDictionary dictionary: [String: Any]) {
        guard let value = dictionary["show_last_part_meta"] as? Bool else { return nil }
        showLastPartMetadata = value
        super.init()
    }
    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? UIFlags else { return false }
        return showLastPartMetadata == other.showLastPartMetadata
    }
    override init() { fatalError("init() has not been implemented") }
}
