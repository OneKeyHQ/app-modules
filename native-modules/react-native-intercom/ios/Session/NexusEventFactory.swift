import Foundation

@objc(_TtC18IntercomSDKPrivate17NexusEventFactory)
class NexusEventFactory: NSObject {
    override init() { super.init() }
    @objc(createNexusEventWith:conversationId:)
    static func createNexusEvent(with type: UInt, conversationId: String?) -> IntercomNexusEvent? {
        let topic = conversationId.map { "conversation/" + $0 } ?? "conversation/new"
        let topics = Set([topic])
        guard let event = IntercomNexusEvent(type: type, topics: topics) else { return nil }
        event.conversationId = conversationId
        let user = ICMIdentityStore.currentIdentity().userIdentity
        let fromUser = user.intercomId
        event.fromUser = fromUser
        return event
    }
}
