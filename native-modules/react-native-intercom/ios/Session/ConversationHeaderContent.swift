import Foundation

struct ConversationHeaderContent: Codable, Hashable, DictionaryInitializable {
    enum Style: String, Codable {
        case h1, paragraph, quote, caption, intro, greeting
    }
    let text: String
    let style: Style
    let avatarDetails: AvatarDetails?
    let color: String?
}
