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

/// Wallet sidebar rows keep the plain title: no runs and no leading glyph
/// (docs/SPEC.md).
func nativeListAllowsTitleDecorations(presentation: String) -> Bool {
  presentation != "walletSidebar"
}

/// Whether the runs replace `title`: not where `title` itself is highlighted
/// (`titleMatch` indexes it), and never on a wallet sidebar row. Measurement
/// and binding share this, so a row is sized for the title it draws.
func nativeListShowsTitleRuns(presentation: String, runCount: Int, matchCount: Int) -> Bool {
  runCount > 0 && matchCount == 0 && nativeListAllowsTitleDecorations(presentation: presentation)
}

/// The run that has to end with an ellipsis because the line was cut exactly
/// after it: a run that does not shrink lost its whole width, and this one is
/// drawn in full, so nothing else would show that the title goes on. Nil when
/// the line is not cut, when it is cut inside a run, which then truncates by
/// itself, and when only the shrinking run gave up its width, which it may.
func nativeListTitleSegmentOverflowIndex(
  natural: [CGFloat], shrink: [Bool], widths: [CGFloat]
) -> Int? {
  guard let last = widths.lastIndex(where: { $0 > 0 }), last < natural.count else { return nil }
  let cutsAFixedRun = natural.indices.contains {
    $0 > last && natural[$0] > 0 && !($0 < shrink.count && shrink[$0])
  }
  return cutsAFixedRun && widths[last] >= natural[last] ? last : nil
}

/// Width the leading title glyph takes from the title text: its 16 points and
/// the 6-point gap. Zero where the row keeps a plain title (docs/SPEC.md).
func nativeListTitleLeadingIconWidth(presentation: String, hasIcon: Bool) -> CGFloat {
  hasIcon && nativeListAllowsTitleDecorations(presentation: presentation) ? 22 : 0
}

/// A run is one line: a hard break folds into one space, as in a one-line
/// title.
func nativeListTitleRunText(_ text: String) -> String {
  text.replacingOccurrences(of: "\r\n|[\r\n]", with: " ", options: .regularExpression)
}
