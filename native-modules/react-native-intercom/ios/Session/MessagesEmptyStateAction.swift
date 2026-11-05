// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import UIKit

@objc enum MessagesEmptyStateActionType: Int { case message = 0, help }

@objc(_TtC18IntercomSDKPrivate24MessagesEmptyStateAction)
class MessagesEmptyStateAction: NSObject {
    @objc let type: MessagesEmptyStateActionType
    @objc let label: String
    @objc let icon: UIImage?

    @objc init(type: MessagesEmptyStateActionType, label: String, icon: UIImage?) {
        self.type = type
        self.label = label
        self.icon = icon
        super.init()
    }

    @objc(initWithNewConversationCTA:)
    init?(withNewConversationCTA cta: NewConversationCTAResponse) {
        type = .message
        label = cta.label
        icon = cta.icon.image()
        super.init()
    }

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let rawType = dictionary["type"] as? String,
              let label = dictionary["label"] as? String else { return nil }
        type = rawType == "help" ? .help : .message
        self.label = label
        switch dictionary["icon"] as? String {
        case "teammate": icon = ICMAssets.sendMessageIcon()
        case "bot": icon = ICMAssets.askAQuestion()
        default: icon = nil
        }
        super.init()
    }

    override init() { fatalError("init() has not been implemented") }
}
