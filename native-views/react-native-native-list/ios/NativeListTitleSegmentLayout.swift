import Foundation

/// Widths for identity title runs (docs/SPEC.md, "Identity title segments
/// and leading title icon"). A run's base width is its natural width under
/// its cap; the one shrinking run takes what the others leave; runs that do
/// not shrink and still overflow are cut from the end of the line.
func nativeListTitleSegmentWidths(
  natural: [CGFloat],
  shrink: [Bool],
  maxWidthRatio: [CGFloat?],
  available: CGFloat
) -> [CGFloat] {
  let line = max(0, available)
  var widths = natural.indices.map { index -> CGFloat in
    let width = max(0, natural[index])
    guard index < maxWidthRatio.count, let ratio = maxWidthRatio[index] else { return width }
    return min(width, ratio * line)
  }
  if let shrinking = shrink.firstIndex(of: true), shrinking < widths.count {
    let others = widths.indices.reduce(CGFloat(0)) { $1 == shrinking ? $0 : $0 + widths[$1] }
    widths[shrinking] = max(0, min(widths[shrinking], line - others))
  }
  var overflow = widths.reduce(0, +) - line
  var index = widths.count - 1
  while overflow > 0, index >= 0 {
    let cut = min(widths[index], overflow)
    widths[index] -= cut
    overflow -= cut
    index -= 1
  }
  return widths
}
