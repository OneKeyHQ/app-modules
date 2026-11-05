import UIKit

extension NSAttributedString {
    func convertToScalableAttributedString() -> NSAttributedString {
        let result = NSMutableAttributedString(string: string)
        let range = NSRange(location: 0, length: string.count)
        enumerateAttributes(in: range, options: []) { attributes, range, _ in
            for (key, value) in attributes {
                if key == .font, let font = value as? UIFont {
                    result.addAttribute(.font, value: UIFontMetrics(forTextStyle: .body).scaledFont(for: font), range: range)
                } else { result.addAttribute(key, value: value, range: range) }
            }
        }
        return result
    }
}
