import UIKit

/// Draws identity title runs on one line, each at the width that
/// `nativeListTitleSegmentWidths` allocates for the current bounds. Its
/// intrinsic width is the runs' natural width, so the title row stack can
/// give it less, and accessibility reads the row's `title` instead of runs.
final class NativeListTitleSegmentsLabel: UIView {
  struct Run {
    let text: String
    let font: UIFont
    let color: UIColor
    let shrink: Bool
    let maxWidthRatio: CGFloat?
  }

  private var runs: [Run] = []
  private var naturalWidths: [CGFloat] = []
  var lineHeight: CGFloat = 24 {
    didSet {
      invalidateIntrinsicContentSize()
      setNeedsDisplay()
    }
  }

  override init(frame: CGRect) {
    super.init(frame: frame)
    isOpaque = false
    backgroundColor = .clear
    contentMode = .redraw
    isAccessibilityElement = true
    accessibilityTraits = .staticText
  }

  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  func configure(_ runs: [Run], spokenText: String) {
    self.runs = runs
    naturalWidths = runs.map {
      ceil(($0.text as NSString).size(withAttributes: [.font: $0.font]).width)
    }
    accessibilityLabel = spokenText.isEmpty ? nil : spokenText
    invalidateIntrinsicContentSize()
    setNeedsDisplay()
  }

  func reset() {
    configure([], spokenText: "")
  }

  override var intrinsicContentSize: CGSize {
    CGSize(width: naturalWidths.reduce(0, +), height: lineHeight)
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    setNeedsDisplay()
  }

  override func draw(_ rect: CGRect) {
    guard !runs.isEmpty else { return }
    let widths = nativeListTitleSegmentWidths(
      natural: naturalWidths, shrink: runs.map(\.shrink),
      maxWidthRatio: runs.map(\.maxWidthRatio), available: bounds.width)
    let isRightToLeft = effectiveUserInterfaceLayoutDirection == .rightToLeft
    var x: CGFloat = isRightToLeft ? bounds.width : 0
    for (run, width) in zip(runs, widths) where width > 0 {
      let paragraph = NSMutableParagraphStyle()
      paragraph.lineBreakMode = .byTruncatingTail
      let text = NSAttributedString(
        string: run.text,
        attributes: [.font: run.font, .foregroundColor: run.color, .paragraphStyle: paragraph])
      let height = ceil(run.font.lineHeight)
      let originX = isRightToLeft ? x - width : x
      text.draw(
        with: CGRect(x: originX, y: (bounds.height - height) / 2, width: width, height: height),
        options: [.usesLineFragmentOrigin, .truncatesLastVisibleLine], context: nil)
      x += isRightToLeft ? -width : width
    }
  }
}
