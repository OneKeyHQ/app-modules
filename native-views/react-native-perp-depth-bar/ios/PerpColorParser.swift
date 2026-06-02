import Foundation
import UIKit

/// Parses a color string coming from JS. Supports:
///   - `#rgb`, `#rrggbb`, `#rrggbbaa`
///   - `rgb(r, g, b)` / `rgba(r, g, b, a)` where a is 0..1
/// Falls back to `fallback` on parse failure.
enum PerpColorParser {
  static func parse(_ value: String?, fallback: UIColor = .clear) -> UIColor {
    guard let raw = value?.trimmingCharacters(in: .whitespacesAndNewlines),
          !raw.isEmpty else {
      return fallback
    }

    if raw.hasPrefix("#") {
      return parseHex(raw) ?? fallback
    }
    if raw.lowercased().hasPrefix("rgb") {
      return parseRGB(raw) ?? fallback
    }
    return fallback
  }

  private static func parseHex(_ hex: String) -> UIColor? {
    var s = hex
    s.removeFirst() // '#'

    // Expand shorthand #rgb -> #rrggbb
    if s.count == 3 {
      s = s.map { "\($0)\($0)" }.joined()
    }

    guard s.count == 6 || s.count == 8 else { return nil }
    var int: UInt64 = 0
    guard Scanner(string: s).scanHexInt64(&int) else { return nil }

    let r, g, b, a: CGFloat
    if s.count == 8 {
      r = CGFloat((int >> 24) & 0xFF) / 255.0
      g = CGFloat((int >> 16) & 0xFF) / 255.0
      b = CGFloat((int >> 8) & 0xFF) / 255.0
      a = CGFloat(int & 0xFF) / 255.0
    } else {
      r = CGFloat((int >> 16) & 0xFF) / 255.0
      g = CGFloat((int >> 8) & 0xFF) / 255.0
      b = CGFloat(int & 0xFF) / 255.0
      a = 1.0
    }
    return UIColor(red: r, green: g, blue: b, alpha: a)
  }

  private static func parseRGB(_ value: String) -> UIColor? {
    guard let open = value.firstIndex(of: "("),
          let close = value.firstIndex(of: ")") else { return nil }
    let inner = value[value.index(after: open)..<close]
    let parts = inner.split(separator: ",").map {
      $0.trimmingCharacters(in: .whitespaces)
    }
    guard parts.count >= 3,
          let r = Double(parts[0]),
          let g = Double(parts[1]),
          let b = Double(parts[2]) else { return nil }
    let a = parts.count >= 4 ? (Double(parts[3]) ?? 1.0) : 1.0
    return UIColor(
      red: CGFloat(r) / 255.0,
      green: CGFloat(g) / 255.0,
      blue: CGFloat(b) / 255.0,
      alpha: CGFloat(a)
    )
  }
}

/// Matches the JS `cubic-bezier(0.33, 1, 0.68, 1)` (Easing.out(cubic)) curve.
enum PerpTiming {
  static let depthBarDurationMs: Double = 260
  static let sideRatioDurationMs: Double = 300

  static func easeOutCubic() -> CAMediaTimingFunction {
    CAMediaTimingFunction(controlPoints: 0.33, 1, 0.68, 1)
  }
}

/// UIView subclass that forwards `layoutSubviews` to a callback so a Nitro
/// HybridView can re-lay-out its CALayers when bounds change.
final class PerpLayoutView: UIView {
  var onLayout: (() -> Void)?
  /// Native tap handler — receives the tap's y in view coordinates (points).
  var onTap: ((CGFloat) -> Void)?

  override func layoutSubviews() {
    super.layoutSubviews()
    onLayout?()
  }

  override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
    super.touchesEnded(touches, with: event)
    guard let onTap, let touch = touches.first else { return }
    onTap(touch.location(in: self).y)
  }
}
