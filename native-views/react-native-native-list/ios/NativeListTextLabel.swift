import UIKit

class NativeListTextLabel: UILabel {
  var rowVerticalAlignment: String? { didSet { setNeedsDisplay() } }
  var rowOffsetY: CGFloat = 0 { didSet { setNeedsDisplay() } }
  /// UTF-16 offset the touchable text starts at. `nil` keeps the label's own
  /// hit testing, its whole bounds. With an offset only the drawn text from
  /// there on takes touches, plus a small slop, so text before the offset and
  /// the unused width of a stretched label fall through to the row behind it:
  /// `0` covers all of the text, the text length leaves nothing to touch.
  var touchableTextStart: Int?
  private static let touchSlop = CGSize(width: 8, height: 6)

  /// One rect per laid-out run of touchable text, in label coordinates. The
  /// label's own layout is not public, so the runs come from a text container
  /// with the label's width, line cap and truncation. A line holds one run, or
  /// several where mixed-direction text puts plain text between touchable text.
  var touchableTextRegions: [CGRect] {
    guard let start = touchableTextStart, let attributed = attributedText, attributed.length > start,
      bounds.width > 0
    else { return [] }
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
    var lines: [CGRect] = []
    var visibleEnd = attributed.length
    manager.enumerateLineFragments(forGlyphRange: glyphs) { _, usedRect, _, lineGlyphs, _ in
      lines.append(usedRect)
      let truncated = manager.truncatedGlyphRange(inLineFragmentForGlyphAt: lineGlyphs.location)
      if truncated.location != NSNotFound {
        visibleEnd = min(visibleEnd, manager.characterIndexForGlyph(at: truncated.location))
      }
    }
    // Truncation can hide the touchable text altogether. The ellipsis then
    // stands for plain text as well, so nothing is left to touch.
    guard start < visibleEnd else { return [] }
    // The layout engine's own rects for a character range: one per line and
    // per visual run, which is what makes mixed-direction text come out right.
    func runs(_ characters: NSRange) -> [CGRect] {
      let range = NSIntersectionRange(
        manager.glyphRange(forCharacterRange: characters, actualCharacterRange: nil), glyphs)
      guard range.length > 0 else { return [] }
      var rects: [CGRect] = []
      manager.enumerateEnclosingRects(
        forGlyphRange: range, withinSelectedGlyphRange: NSRange(location: NSNotFound, length: 0),
        in: container
      ) { rect, _ in
        // A run that continues on the next line is reported up to the
        // container edge; its text ends where the line's used rect does.
        guard let used = lines.first(where: { $0.minY <= rect.midY && rect.midY < $0.maxY })
        else { return }
        let minX = max(rect.minX, used.minX)
        let maxX = min(rect.maxX, used.maxX)
        if maxX > minX {
          rects.append(CGRect(x: minX, y: rect.minY, width: maxX - minX, height: rect.height))
        }
      }
      return rects
    }
    let plain = runs(NSRange(location: 0, length: start))
    return runs(NSRange(location: start, length: attributed.length - start)).map { run in
      // The slop reaches into blank space only. Sideways it stops at plain
      // text on the same line, so text before the start never takes the
      // touch, however narrow it is.
      var minX = run.minX - Self.touchSlop.width
      var maxX = run.maxX + Self.touchSlop.width
      for other in plain where other.minY < run.midY && run.midY < other.maxY {
        if other.maxX <= run.minX + 0.5 { minX = max(minX, other.maxX) }
        if other.minX >= run.maxX - 0.5 { maxX = min(maxX, other.minX) }
      }
      // Vertically it grows only past the first and last line of the whole
      // layout, so it never reaches into another line.
      let line = lines.firstIndex { $0.minY <= run.midY && run.midY < $0.maxY }
      let top = line == 0 ? Self.touchSlop.height : 0
      let bottom = line == lines.count - 1 ? Self.touchSlop.height : 0
      return CGRect(
        x: minX, y: run.minY + originY - top, width: maxX - minX,
        height: run.height + top + bottom)
    }
  }

  override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
    guard touchableTextStart != nil else { return super.point(inside: point, with: event) }
    return touchableTextRegions.contains { $0.contains(point) }
  }

  // Assistive activation taps this point; the label's center can be blank.
  override var accessibilityActivationPoint: CGPoint {
    get {
      guard let region = touchableTextRegions.first else {
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
