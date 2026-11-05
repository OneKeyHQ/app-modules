import Foundation

enum ConversationCellType: Hashable {
    case message(ICMConversationPart)
    case greeting(String)
    case temporaryExpectationsView(String)
    case ticket(Ticket)
    case dayDivider(ICMConversationPart)
    case footerNotice(FooterNoticeViewModel)
    case activityIndicator(TypingAdminDetails?)
    case standaloneBlock(ICMConversationPart)
    case unreadMessageDivider

    private static func partsEqual(_ lhs: ICMConversationPart, _ rhs: ICMConversationPart) -> Bool {
        lhs.partId == rhs.partId && lhs.form == rhs.form && lhs.state == rhs.state &&
        lhs.partMetadata == rhs.partMetadata && lhs.hasSegmentBelow == rhs.hasSegmentBelow
    }
    static func == (lhs: ConversationCellType, rhs: ConversationCellType) -> Bool {
        switch (lhs, rhs) {
        case let (.message(lhs), .message(rhs)), let (.dayDivider(lhs), .dayDivider(rhs)), let (.standaloneBlock(lhs), .standaloneBlock(rhs)):
            return partsEqual(lhs, rhs)
        case let (.greeting(lhs), .greeting(rhs)), let (.temporaryExpectationsView(lhs), .temporaryExpectationsView(rhs)):
            return lhs == rhs
        case let (.ticket(lhs), .ticket(rhs)): return lhs == rhs
        case let (.footerNotice(lhs), .footerNotice(rhs)): return lhs == rhs
        case let (.activityIndicator(lhs), .activityIndicator(rhs)): return lhs == rhs
        case (.unreadMessageDivider, .unreadMessageDivider): return true
        default: return false
        }
    }
    func hash(into hasher: inout Hasher) {
        switch self {
        case .message(let part), .dayDivider(let part), .standaloneBlock(let part):
            hasher.combine(part.partId)
            hasher.combine(part.form)
            hasher.combine(part.state)
            hasher.combine(part.partMetadata)
            hasher.combine(part.hasSegmentBelow)
        case .greeting(let text), .temporaryExpectationsView(let text): hasher.combine(text)
        case .ticket(let ticket): ticket.hash(into: &hasher)
        case .footerNotice(let notice): notice.hash(into: &hasher)
        case .activityIndicator(let details): hasher.combine(details)
        case .unreadMessageDivider: hasher.combine("unreadMessageDivider")
        }
    }
}
