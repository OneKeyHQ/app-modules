import Foundation

// Recovery labels for six reached HTTP values; original spellings/ordinals remain unproved.
enum ConversationFetchReason {
    case nexusNewComment, nexusReconnected, userContentSeenByAdmin, clickConversation, reloadedByUser, stateSync
    var httpReason: String {
        switch self {
        case .nexusNewComment: return "Nexus New Comment"
        case .nexusReconnected: return "Nexus Reconnected"
        case .userContentSeenByAdmin: return "User Content Seen By Admin"
        case .clickConversation: return "Click Conversation"
        case .reloadedByUser: return "Reloaded By User"
        case .stateSync: return "State Sync"
        }
    }
}
