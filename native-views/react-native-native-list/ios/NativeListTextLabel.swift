import UIKit

class NativeListTextLabel: UILabel {
  var rowVerticalAlignment: String? { didSet { setNeedsDisplay() } }
  var rowOffsetY: CGFloat = 0 { didSet { setNeedsDisplay() } }
  /// Limits touches to the drawn text plus a small slop, so the unused width of
  /// a stretched label falls through to the row behind it.
  var touchesTextOnly = false
  /// UTF-16 offset the touchable text starts at; text before it takes no
  /// touches on any line.
  var touchableTextStart = 0
  private static let touchSlop = CGSize(width: 8, height: 6)

  /// One rect per laid-out line that holds touchable text, in label coordinates.
  /// The label's own layout is not public, so the lines come from a text
  /// container with the label's width, line cap and truncation.
  var touchableTextRegions: [CGRect] {
    guard let attributed = attributedText, attributed.length > 0, bounds.width > 0 else { return [] }
    let storage = NSTextStorage(attributedString: attributed)
    let manager = NSLayoutManager()
    let container = NSTextContainer(
      size: CGSize(width: bounds.width, height: .greatestFiniteMagnitude))
    container.lineFragmentPadding = 0
    container.maximumNumberOfLines = numberOfLines
    container.lineBreakMode = lineBreakMode
    manager.addTextContainer(container)
    storage.addLayoutManager(manager)
    let glyphs = manager.glyphRange(for: container)
    // Mirrors drawText: the text is centered unless the row aligns it.
    let textHeight = manager.usedRect(for: container).height
    var originY = (bounds.height - textHeight) / 2
    if let alignment = rowVerticalAlignment {
      originY = alignment == "top" ? 0 : alignment == "bottom" ? bounds.height - textHeight : originY
    }
    originY += rowOffsetY
    let start = touchableTextStart
    var lines: [CGRect] = []
    manager.enumerateLineFragments(forGlyphRange: glyphs) { _, usedRect, _, lineGlyphs, _ in
      let characters = manager.characterRange(forGlyphRange: lineGlyphs, actualGlyphRange: nil)
      guard NSMaxRange(characters) > start else { return }
      var rect = usedRect
      if characters.location < start {
        let touchable = NSRange(location: start, length: NSMaxRange(characters) - start)
        rect = manager.boundingRect(
          forGlyphRange: manager.glyphRange(forCharacterRange: touchable, actualCharacterRange: nil),
          in: container)
      }
      lines.append(rect.offsetBy(dx: 0, dy: originY))
    }
    // The slop widens every line, but grows vertically only away from the
    // text, so it never reaches into a neighbouring line.
    return lines.enumerated().map { index, rect in
      let top = index == 0 ? Self.touchSlop.height : 0
      let bottom = index == lines.count - 1 ? Self.touchSlop.height : 0
      return CGRect(
        x: rect.minX - Self.touchSlop.width, y: rect.minY - top,
        width: rect.width + Self.touchSlop.width * 2, height: rect.height + top + bottom)
    }
  }

  override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
    guard touchesTextOnly else { return super.point(inside: point, with: event) }
    return touchableTextRegions.contains { $0.contains(point) }
  }

  // Assistive activation taps this point; the label's center can be blank.
  override var accessibilityActivationPoint: CGPoint {
    get {
      guard touchesTextOnly, let region = touchableTextRegions.first else {
        return super.accessibilityActivationPoint
      }
      let target = UIAccessibility.convertToScreenCoordinates(region.intersection(bounds), in: self)
      return CGPoint(x: target.midX, y: target.midY)
    }
    set { super.accessibilityActivationPoint = newValue }
  }

  override func drawText(in rect: CGRect) {
    var target = rect
    if let alignment = rowVerticalAlignment {
      let height = super.textRect(forBounds: rect, limitedToNumberOfLines: numberOfLines).height
      target.origin.y += alignment == "top" ? 0 : alignment == "bottom" ? rect.height - height : (rect.height - height) / 2
      target.size.height = height
    }
    target.origin.y += rowOffsetY
    super.drawText(in: target)
  }
}

final class NativeListInsetLabel: NativeListTextLabel {
  var horizontalInset: CGFloat = 0
  var topInset: CGFloat = 0
  var bottomInset: CGFloat = 0

  override var intrinsicContentSize: CGSize {
    let size = super.intrinsicContentSize
    return CGSize(
      width: size.width + horizontalInset * 2,
      height: size.height + topInset + bottomInset
    )
  }

  override func drawText(in rect: CGRect) {
    super.drawText(in: rect.inset(
      by: UIEdgeInsets(
        top: topInset,
        left: horizontalInset,
        bottom: bottomInset,
        right: horizontalInset
      )
    ))
  }
}
