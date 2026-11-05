import Foundation

extension ICMConversationPart {
    var resolvedSenderLabel: String {
        let title = partMetadata?.title
        let suffix = partMetadata?.suffix
        let joined = [title, suffix].compactMap { value -> String? in
            guard let value, !value.isEmpty else { return nil }
            return value
        }.joined(separator: " • ")
        if !joined.isEmpty { return joined }
        if let name = participant?.firstName, !name.isEmpty { return name }
        return ""
    }
}
