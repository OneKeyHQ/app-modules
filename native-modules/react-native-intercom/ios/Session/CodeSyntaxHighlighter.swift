import UIKit

struct CodeSyntaxHighlighter {
    let appearance: ICMBlockAppearance

    private enum SyntaxRole: Hashable {
        case keyword, string, number, comment, function, tag, `operator`
    }

    private static let rolesByTokenClass: [String: SyntaxRole] = [
        "k": .keyword,
        "kc": .keyword,
        "kd": .keyword,
        "kn": .keyword,
        "kp": .keyword,
        "kr": .keyword,
        "kt": .keyword,
        "ow": .keyword,
        "nf": .function,
        "fm": .function,
        "nc": .function,
        "nb": .function,
        "bp": .function,
        "nt": .tag,
        "s": .string,
        "sa": .string,
        "sb": .string,
        "sc": .string,
        "dl": .string,
        "sd": .string,
        "s2": .string,
        "se": .string,
        "sh": .string,
        "si": .string,
        "sx": .string,
        "sr": .string,
        "s1": .string,
        "ss": .string,
        "na": .string,
        "m": .number,
        "mb": .number,
        "mf": .number,
        "mh": .number,
        "mi": .number,
        "mo": .number,
        "il": .number,
        "c": .comment,
        "ch": .comment,
        "cm": .comment,
        "cp": .comment,
        "cpf": .comment,
        "c1": .comment,
        "cs": .comment,
        "o": .operator,
        "p": .operator,
    ]

    func attributedString(fromHighlightedHTML html: String) -> NSAttributedString? {
        guard !html.isEmpty else { return nil }
        let result = NSMutableAttributedString()
        var pending = ""
        var colors: [UIColor?] = []
        var index = html.startIndex
        func flushPendingText() {
            guard !pending.isEmpty else { return }
            let attributes: [NSAttributedString.Key: Any]
            if let color = colors.last ?? nil { attributes = [.foregroundColor: color] }
            else { attributes = [:] }
            result.append(NSAttributedString(string: Self.decodingEntities(in: pending), attributes: attributes))
            pending = ""
        }
        while index < html.endIndex {
            if html[index] != "<" {
                pending.append(html[index])
                index = html.index(after: index)
                continue
            }
            flushPendingText()
            if html[index...].starts(with: "</span>") {
                guard !colors.isEmpty else { return nil }
                colors.removeLast()
                index = html.index(index, offsetBy: "</span>".count)
                continue
            }
            let prefix = "<span class=\""
            guard html[index...].starts(with: prefix) else { return nil }
            let classStart = html.index(index, offsetBy: prefix.count)
            var classEnd = classStart
            while classEnd < html.endIndex && html[classEnd] != "\"" {
                classEnd = html.index(after: classEnd)
            }
            guard classEnd < html.endIndex, html[classEnd...].starts(with: "\">") else { return nil }
            let inherited = colors.last ?? nil
            let color = color(forClassAttribute: String(html[classStart..<classEnd])) ?? inherited
            colors.append(color)
            index = html.index(classEnd, offsetBy: 2)
        }
        guard colors.isEmpty else { return nil }
        flushPendingText()
        return result
    }

    private func color(forClassAttribute value: String) -> UIColor? {
        for token in value.split(separator: " ") {
            guard let role = Self.rolesByTokenClass[String(token)] else { continue }
            switch role {
            case .keyword: return appearance.syntaxKeywordColor!
            case .string: return appearance.syntaxStringColor!
            case .number: return appearance.syntaxNumberColor!
            case .comment: return appearance.syntaxCommentColor!
            case .function: return appearance.syntaxFunctionColor!
            case .tag: return appearance.syntaxTagColor!
            case .operator: return appearance.syntaxOperatorColor!
            }
        }
        return nil
    }

    private static func decodedEntity(named name: String) -> String? {
        switch name {
        case "amp": return "&"
        case "lt": return "<"
        case "gt": return ">"
        case "quot": return "\""
        case "apos": return "'"
        default:
            let number: UInt32?
            if name.hasPrefix("#x") || name.hasPrefix("#X") { number = UInt32(name.dropFirst(2), radix: 16) }
            else if name.hasPrefix("#") { number = UInt32(name.dropFirst(), radix: 10) }
            else { return nil }
            guard let number, let scalar = Unicode.Scalar(number) else { return nil }
            return String(scalar)
        }
    }

    private static func decodingEntities(in text: String) -> String {
        guard text.contains("&") else { return text }
        var result = ""
        var index = text.startIndex
        while index < text.endIndex {
            if text[index] == "&" {
                let remaining = text[index...]
                let limit = remaining.index(remaining.startIndex, offsetBy: 10, limitedBy: remaining.endIndex) ?? remaining.endIndex
                if let semicolon = remaining[..<limit].firstIndex(of: ";"),
                   let decoded = decodedEntity(named: String(text[text.index(after: index)..<semicolon])) {
                    result.append(decoded)
                    index = text.index(after: semicolon)
                    continue
                }
            }
            result.append(text[index])
            index = text.index(after: index)
        }
        return result
    }
}
