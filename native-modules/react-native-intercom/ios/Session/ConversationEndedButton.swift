import Foundation

enum ConversationEndedReason: String {
    case mergedIntoConversation = "merged_into_conversation"
    func hash(into hasher: inout Hasher) { hasher.combine(rawValue) }
}

@objc(_TtC18IntercomSDKPrivate23ConversationEndedButton)
class ConversationEndedButton: NSObject, NSSecureCoding, DictionaryInitializable {
    let reason: ConversationEndedReason
    @objc let metadata: ConversationEndedButtonMetadata?
    @objc let text: String
    @objc let title: String
    @objc static var supportsSecureCoding: Bool { true }
    @objc(initWithDictionary:)
    required init?(withDictionary dictionary: [String: Any]) {
        guard let raw = dictionary["reason"] as? String, let reason = ConversationEndedReason(rawValue: raw),
              let text = dictionary["text"] as? String, let title = dictionary["title"] as? String else { return nil }
        self.reason = reason
        if let metadata = dictionary["metadata"] as? [String: Any] { self.metadata = ConversationEndedButtonMetadata(withDictionary: metadata) }
        else { metadata = nil }
        self.text = text
        self.title = title
        super.init()
    }
    required init?(coder: NSCoder) {
        guard let raw = coder.decodeObject(of: NSString.self, forKey: "reason") as String?, let reason = ConversationEndedReason(rawValue: raw),
              let text = coder.decodeObject(of: NSString.self, forKey: "text") as String?,
              let title = coder.decodeObject(of: NSString.self, forKey: "title") as String? else { return nil }
        self.reason = reason
        metadata = coder.decodeObject(of: ConversationEndedButtonMetadata.self, forKey: "metadata")
        self.text = text
        self.title = title
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return metadata == other.metadata && text == other.text && title == other.title
    }
    override var hash: Int {
        var hasher = Hasher()
        hasher.combine(metadata)
        hasher.combine(reason.rawValue)
        hasher.combine(text)
        hasher.combine(title)
        return hasher.finalize()
    }
    func encode(with coder: NSCoder) {
        if let metadata { coder.encode(metadata, forKey: "metadata") }
        coder.encode(reason.rawValue, forKey: "reason")
        coder.encode(text, forKey: "text")
        coder.encode(title, forKey: "title")
    }
}

@objc(_TtC18IntercomSDKPrivate31ConversationEndedButtonMetadata)
class ConversationEndedButtonMetadata: NSObject, NSSecureCoding, DictionaryInitializable {
    @objc let mergedIntoConversationId: String?
    @objc static var supportsSecureCoding: Bool { true }
    @objc(initWithDictionary:)
    required init?(withDictionary dictionary: [String: Any]) {
        mergedIntoConversationId = (dictionary["merged_into_conversation_id"] as? Int).map { $0.description }
        super.init()
    }
    required init?(coder: NSCoder) {
        guard let id = coder.decodeObject(of: NSString.self, forKey: "merged_into_conversation_id") as String? else { return nil }
        mergedIntoConversationId = id
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return mergedIntoConversationId == other.mergedIntoConversationId
    }
    override var hash: Int {
        var hasher = Hasher()
        hasher.combine(mergedIntoConversationId)
        return hasher.finalize()
    }
    func encode(with coder: NSCoder) { coder.encode(mergedIntoConversationId, forKey: "merged_into_conversation_id") }
}
