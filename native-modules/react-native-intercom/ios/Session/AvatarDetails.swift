// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

struct AvatarDetails: Codable, Hashable, DictionaryInitializable {
    enum AvatarType: String, Codable {
        case facepile
        case layeredBubbles = "layered_bubbles"
        case logo
    }
    let avatarType: AvatarType
    let avatars: [Avatar]
}
