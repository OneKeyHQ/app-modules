import UIKit

class NativeListTextLabel: UILabel {
  var rowVerticalAlignment: String? { didSet { setNeedsDisplay() } }
  var rowOffsetY: CGFloat = 0 { didSet { setNeedsDisplay() } }
  /// Limits touches to the drawn text plus a small slop, so the unused width of
  /// a stretched label falls through to the row behind it.
  var touchesTextOnly = false
  /// UTF-16 offset the touchable text starts at; a single-line label does not
  /// take touches on the text before it.
  var touchableTextStart = 0
  private static let touchSlop = CGSize(width: 8, height: 6)

  var touchableTextBounds: CGRect {
    let text = textRect(forBounds: bounds, limitedToNumberOfLines: numberOfLines)
    var minX = text.minX
    var maxX = text.maxX
    if touchableTextStart > 0, let attributed = attributedText,
       touchableTextStart < attributed.length, text.height < font.lineHeight * 1.5 {
      let leading = ceil(
        attributed.attributedSubstring(from: NSRange(location: 0, length: touchableTextStart))
          .boundingRect(
            with: CGSize(width: CGFloat.greatestFiniteMagnitude, height: .greatestFiniteMagnitude),
            options: [.usesLineFragmentOrigin], context: nil
          ).width)
      if effectiveUserInterfaceLayoutDirection == .rightToLeft { maxX -= leading } else { minX += leading }
    }
    return CGRect(x: minX, y: bounds.minY, width: max(0, maxX - minX), height: bounds.height)
      .insetBy(dx: -Self.touchSlop.width, dy: -Self.touchSlop.height)
  }

  override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
    guard touchesTextOnly else { return super.point(inside: point, with: event) }
    return touchableTextBounds.contains(point)
  }

  // Assistive activation taps this point; the label's center can be blank.
  override var accessibilityActivationPoint: CGPoint {
    get {
      guard touchesTextOnly else { return super.accessibilityActivationPoint }
      let target = UIAccessibility.convertToScreenCoordinates(
        touchableTextBounds.intersection(bounds), in: self)
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
