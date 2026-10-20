import UIKit

class NativeListTextLabel: UILabel {
  var rowVerticalAlignment: String? { didSet { setNeedsDisplay() } }
  var rowOffsetY: CGFloat = 0 { didSet { setNeedsDisplay() } }

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
