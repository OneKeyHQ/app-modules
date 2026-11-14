import Foundation

struct ConversationButtonInfo: Codable, Equatable, DictionaryInitializable {
    let title: String
    let iconType: IconType

    enum IconType: String, Codable {
        case send, conversation, unknown
        init(from decoder: Decoder) throws {
            let raw = try decoder.singleValueContainer().decode(String.self)
            self = IconType(rawValue: raw) ?? .unknown
        }
    }
}
