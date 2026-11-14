import Foundation

@objc(_TtC18IntercomSDKPrivate17InboxCellViewItem)
class InboxCellViewItem: NSObject {
    @objc let conversation: ICMConversation?
    @objc let currentUserIntercomId: String?
    @objc let canShowUnreadDot: Bool
    @objc let showDividerLine: Bool
    @objc let ticketsSpaceEnabled: Bool

    @objc init(conversation: ICMConversation?, currentUserIntercomId: String?, canShowUnreadDot: Bool, showDividerLine: Bool, ticketsSpaceEnabled: Bool) {
        self.conversation = conversation
        self.currentUserIntercomId = currentUserIntercomId
        self.canShowUnreadDot = canShowUnreadDot
        self.showDividerLine = showDividerLine
        self.ticketsSpaceEnabled = ticketsSpaceEnabled
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
}
