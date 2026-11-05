// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import UIKit

@objc enum IntercomIcon: Int {
    case paperPlane = 0, chevron, questionBubble, clock, ai, none

    func image() -> UIImage? {
        let image: UIImage?
        switch self {
        case .paperPlane: image = ICMAssets.sendMessageIcon()
        case .chevron: image = ICMAssets.helpCenterChevron()
        case .questionBubble: image = ICMAssets.askAQuestion()
        case .clock: image = ICMAssets.replyTimeIcon()
        case .ai: image = ICMAssets.aiIcon()
        case .none: image = nil
        }
        return image?.imageFlippedForRightToLeftLayoutDirection()
    }

    var stringValue: String? {
        switch self {
        case .paperPlane: return "paper_plane"
        case .chevron: return "chevron"
        case .questionBubble: return "question_bubble"
        case .clock: return "clock"
        case .ai: return "ai"
        case .none: return nil
        }
    }

    static func icon(from value: String?) -> IntercomIcon {
        switch value {
        case "paper_plane": return .paperPlane
        case "chevron": return .chevron
        case "question_bubble": return .questionBubble
        case "clock": return .clock
        case "ai": return .ai
        default: return .none
        }
    }
}
