import Foundation
import UIKit

/// Parses a color string coming from JS. Supports:
///   - `#rgb`, `#rrggbb`, `#rrggbbaa`
///   - `rgb(r, g, b)` / `rgba(r, g, b, a)` where a is 0..1
/// Falls back to `fallback` on parse failure.
enum SegmentSliderColorParser {
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

/// UIControl subclass that forwards `layoutSubviews` and raw touch positions to
/// closures so the Nitro HybridView can re-lay-out its CALayers and drive the
/// drag entirely on the native UI thread.
///
/// It subclasses `UIControl` (not `UIView`) on purpose: an enclosing
/// `UIScrollView` returns `false` from `touchesShouldCancel(in:)` for
/// `UIControl` subviews, so a horizontal drag on the slider is NOT cancelled
/// and stolen by the scroll view's pan gesture. This is the same mechanism
/// `UISlider` relies on to stay draggable inside a scroll view.
final class SegmentSliderUIView: UIControl {
  var onLayout: (() -> Void)?
  /// Touch began at x (points, view coordinates).
  var onTouchBegan: ((CGFloat) -> Void)?
  /// Touch moved to x (points, view coordinates).
  var onTouchMoved: ((CGFloat) -> Void)?
  /// Touch ended or was cancelled.
  var onTouchEnded: (() -> Void)?

  override func layoutSubviews() {
    super.layoutSubviews()
    onLayout?()
  }

  // While the user is dragging the slider, NO ancestor pan gesture may act —
  // not the enclosing scroll view's scroll, not the navigation stack's
  // swipe-back, not a parent pager. UIKit's UIControl exemption
  // (`touchesShouldCancelInContentView:`) does NOT help, because the scroll
  // view passes its *direct* content subview to that method, not this
  // deeply-nested view. So on touch-down we walk the ancestor chain and
  // temporarily disable every pan-type recognizer (UIScrollView pan,
  // UIScreenEdgePan / RNSPanGestureRecognizer back-swipe, pager pans — all are
  // UIPanGestureRecognizer subclasses), restoring them on touch-up. This is the
  // iOS analogue of Android's `requestDisallowInterceptTouchEvent(true)`.
  private var disabledRecognizers: [UIGestureRecognizer] = []

  private func lockAncestorGestures() {
    var v: UIView? = superview
    while let cur = v {
      if let grs = cur.gestureRecognizers {
        for gr in grs where gr is UIPanGestureRecognizer && gr.isEnabled {
          gr.isEnabled = false // cancels it if mid-recognition, blocks it otherwise
          disabledRecognizers.append(gr)
        }
      }
      v = cur.superview
    }
  }

  private func unlockAncestorGestures() {
    for gr in disabledRecognizers {
      gr.isEnabled = true
    }
    disabledRecognizers.removeAll()
  }

  override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
    super.touchesBegan(touches, with: event)
    lockAncestorGestures()
    guard let t = touches.first else { return }
    onTouchBegan?(t.location(in: self).x)
  }

  override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
    super.touchesMoved(touches, with: event)
    guard let t = touches.first else { return }
    onTouchMoved?(t.location(in: self).x)
  }

  override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
    super.touchesEnded(touches, with: event)
    unlockAncestorGestures()
    onTouchEnded?()
  }

  override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
    super.touchesCancelled(touches, with: event)
    unlockAncestorGestures()
    onTouchEnded?()
  }
}
