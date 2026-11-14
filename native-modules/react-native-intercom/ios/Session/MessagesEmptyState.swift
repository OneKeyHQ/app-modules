import Foundation

@objc(_TtC18IntercomSDKPrivate18MessagesEmptyState)
class MessagesEmptyState: NSObject {
    @objc let title: String
    @objc let text: String
    @objc let action: MessagesEmptyStateAction?

    @objc init(title: String, text: String, action: MessagesEmptyStateAction?) {
        self.title = title
        self.text = text
        self.action = action
        super.init()
    }

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let title = dictionary["title"] as? String,
              let text = dictionary["text"] as? String else { return nil }
        self.title = title
        self.text = text
        if let action = dictionary["action"] as? [String: Any] {
            self.action = MessagesEmptyStateAction(withDictionary: action)
        } else { action = nil }
        super.init()
    }

    override init() { fatalError("init() has not been implemented") }
}
