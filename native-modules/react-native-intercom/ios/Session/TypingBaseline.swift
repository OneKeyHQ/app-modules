import Foundation

struct TypingBaseline {
    let partIds: Set<String>

    init(conversation: ICMConversation?) {
        let parts: [ICMConversationPart]
        if let raw = conversation?.conversationParts { parts = raw as! [ICMConversationPart] }
        else { parts = [] }
        partIds = Set(parts.compactMap { $0.partId })
    }

    func isEnded(by conversation: ICMConversation) -> Bool {
        let parts = conversation.conversationParts! as! [ICMConversationPart]
        for part in parts {
            if !part.participantIsAdmin { continue }
            guard let id = part.partId else { continue }
            if !partIds.contains(id) { return true }
        }
        return false
    }
}
