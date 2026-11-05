// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc(ICMUnreadManager)
class UnreadManager: NSObject {
    @objc var unreadConversationIds = NSOrderedSet() {
        didSet {
            if oldValue.count != unreadConversationIds.count {
                notificationCenter.post(name: Notification.Name("IntercomUnreadConversationCountDidChangeNotification"), object: nil)
            }
        }
    }
    @objc var unreadTicketIds = NSOrderedSet() {
        didSet {
            if oldValue.count != unreadTicketIds.count {
                notificationCenter.post(name: Notification.Name("IntercomUnreadTicketCountDidChangeNotification"), object: nil)
            }
        }
    }
    private let notificationCenter: NotificationCenter

    @objc override init() {
        notificationCenter = .default
        super.init()
    }

    @objc init(notificationCenter: NotificationCenter) {
        self.notificationCenter = notificationCenter
        super.init()
    }

    @objc func conversationIdRead(_ identifier: String) {
        guard unreadConversationIds.contains(identifier) else { return }
        unreadConversationIds = unreadConversationIds.filtered(using: NSPredicate(format: "SELF != %@", identifier))
        notificationCenter.post(name: Notification.Name("ICMConversationMarkedAsReadNotification"), object: identifier)
    }

    @objc func ticketIdRead(_ identifier: String) {
        guard unreadTicketIds.contains(identifier) else { return }
        unreadTicketIds = unreadTicketIds.filtered(using: NSPredicate(format: "SELF != %@", identifier))
        notificationCenter.post(name: Notification.Name("ICMTicketMarkedAsReadNotification"), object: identifier)
    }
}
