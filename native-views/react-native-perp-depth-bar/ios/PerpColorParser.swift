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
    // Clamp channels to 0...255 and alpha to 0...1 (matches Android's PerpColorParser).
    return UIColor(
      red: CGFloat(min(max(r, 0), 255)) / 255.0,
      green: CGFloat(min(max(g, 0), 255)) / 255.0,
      blue: CGFloat(min(max(b, 0), 255)) / 255.0,
      alpha: CGFloat(min(max(a, 0), 1))
    )
  }
}

/// Matches the JS `cubic-bezier(0.33, 1, 0.68, 1)` (Easing.out(cubic)) curve.
enum PerpTiming {
  static let depthBarDurationMs: Double = 260
  static let sideRatioDurationMs: Double = 300

  /// Exponential-smoothing time constant (seconds) for the continuous,
  /// frame-driven depth-bar easing. Each frame the on-screen value moves a
  /// fraction `1 - exp(-dt / tau)` toward the latest target, so the bar always
  /// glides toward the newest data instead of restarting a fixed-length anim
  /// per tick. ~63% of any change covered in `tau`, ~95% in `3*tau`.
  static let depthBarSmoothingTauSeconds: Double = 0.10

  /// Same continuous-easing time constant for the bid/ask side-ratio bar. A
  /// touch larger than the depth bars since it changes more slowly.
  static let sideRatioSmoothingTauSeconds: Double = 0.12

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
    guard let onLayout else { return }
    // Fabric (New Architecture) can drive layout off the main thread. The
    // `onLayout` callback mutates CALayer/CATextLayer state, which UIKit requires
    // on the main thread — doing it off-main asserts ("Unsupported layout off the
    // main thread") and can leave the view blank. Hop to main when needed.
    if Thread.isMainThread {
      onLayout()
    } else {
      DispatchQueue.main.async { onLayout() }
    }
  }

  override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
    super.touchesEnded(touches, with: event)
    guard let onTap, let touch = touches.first else { return }
    onTap(touch.location(in: self).y)
  }
}
