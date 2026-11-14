import UIKit

extension NSAttributedString.Key {
    static let inlineCodeBackground = NSAttributedString.Key("ICMInlineCodeBackground")
}

@objc(_TtC18IntercomSDKPrivate27InlineCodeChipLayoutManager)
class InlineCodeChipLayoutManager: NSLayoutManager {
    @objc static var horizontalPadding: CGFloat { 4 }

    override func drawBackground(forGlyphRange glyphsToShow: NSRange, at origin: CGPoint) {
        super.drawBackground(forGlyphRange: glyphsToShow, at: origin)
        guard let storage = textStorage, let container = textContainers.first else { return }
        let characters = characterRange(forGlyphRange: glyphsToShow, actualGlyphRange: nil)
        storage.enumerateAttribute(.inlineCodeBackground, in: characters, options: []) { value, range, _ in
            guard let color = value as? UIColor else { return }
            color.setFill()
            for rect in self.chipRects(for: range, in: container, storage: storage) {
                UIBezierPath(roundedRect: rect.offsetBy(dx: origin.x, dy: origin.y), cornerRadius: 6).fill()
            }
        }
    }

    func chipRects(for range: NSRange, in container: NSTextContainer, storage: NSTextStorage) -> [CGRect] {
        let font = storage.attribute(.font, at: range.location, effectiveRange: nil) as? UIFont
        let deepest = Self.deepestDescent(in: storage)
        let glyphs = glyphRange(forCharacterRange: range, actualCharacterRange: nil)
        var rects: [CGRect] = []
        enumerateLineFragments(forGlyphRange: glyphs) { lineRect, _, _, lineGlyphs, _ in
            let intersection = NSIntersectionRange(glyphs, lineGlyphs)
            guard intersection.length >= 1 else { return }
            let trimmed = self.trimmingTrailingWhitespace(intersection, storage: storage)
            guard trimmed.length >= 1 else { return }
            let bounds = self.boundingRect(forGlyphRange: trimmed, in: container)
            let glyphLocation = self.location(forGlyphAt: trimmed.location)
            let containerWidth = container.size.width
            let ascender: CGFloat
            let descender: CGFloat
            if let font = font {
                ascender = font.ascender
                descender = font.descender
            } else {
                ascender = bounds.height
                descender = 0
            }
            let paddedLeft = bounds.minX - 4
            let left: CGFloat = paddedLeft >= 0 ? paddedLeft : 0
            let paddedRight = bounds.maxX + 4
            let right = paddedRight < containerWidth ? paddedRight : containerWidth
            guard left < right else { return }
            let height = (ascender - descender) + 5
            guard height > 0 else { return }
            let baseline = lineRect.origin.y + glyphLocation.y
            let rect = CGRect(x: left, y: (baseline - ascender) - 2.5,
                              width: right - left, height: height)
            let verticalOffset = (lineRect.maxY - baseline) - deepest
            rects.append(rect.offsetBy(dx: 0, dy: verticalOffset))
        }
        return rects
    }

    private static func deepestDescent(in storage: NSTextStorage) -> CGFloat {
        var deepest: CGFloat = 0
        storage.enumerateAttribute(.font, in: NSRange(location: 0, length: storage.length), options: []) { value, _, _ in
            guard let font = value as? UIFont else { return }
            let descent = -font.descender
            deepest = deepest <= descent ? descent : deepest
        }
        return deepest
    }

    private func trimmingTrailingWhitespace(_ glyphs: NSRange, storage: NSTextStorage) -> NSRange {
        let characters = characterRange(forGlyphRange: glyphs, actualGlyphRange: nil)
        let string = storage.string as NSString
        var length = characters.length
        while length >= 1 {
            let value = string.character(at: (characters.location + length) - 1)
            guard let scalar = Unicode.Scalar(value), CharacterSet.whitespacesAndNewlines.contains(scalar) else { break }
            length -= 1
        }
        if length != characters.length {
            return glyphRange(forCharacterRange: NSRange(location: characters.location, length: length), actualCharacterRange: nil)
        }
        return glyphs
    }

    override init() { super.init() }
    required init?(coder: NSCoder) { super.init(coder: coder) }
}
