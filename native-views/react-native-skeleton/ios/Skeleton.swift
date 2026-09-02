import Foundation
import UIKit

private final class SkeletonHostView: UIView {
  var onLayout: ((CGRect) -> Void)?
  var onWindowChanged: ((Bool) -> Void)?

  override func layoutSubviews() {
    super.layoutSubviews()
    onLayout?(bounds)
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    onWindowChanged?(window != nil)
  }
}

/// Nitro adapter around OneKeySkeletonRenderer. Rendering stays view-independent
/// so OneKeyImage can reuse it without nesting another HybridView or UIView.
final class HybridSkeleton: HybridSkeletonSpec {
  private let hostView = SkeletonHostView()
  private lazy var renderer = OneKeySkeletonRenderer(hostLayer: hostView.layer)
  private var attached = false
  private var colors = oneKeySkeletonDefaultGradientColors
  private var duration: Double = 3

  var view: UIView { hostView }

  var shimmerGradientColors: [String]? {
    didSet {
      if let values = shimmerGradientColors, values.count >= 2 {
        colors = Array(values.prefix(2)).map(Self.color(from:))
      } else {
        colors = oneKeySkeletonDefaultGradientColors
      }
      updateRenderer()
    }
  }

  var shimmerSpeed: Double? {
    didSet {
      duration = max(shimmerSpeed ?? 3, 0.1)
      updateRenderer()
    }
  }

  override init() {
    super.init()
    hostView.clipsToBounds = true
    hostView.onLayout = { [weak self] bounds in
      self?.renderer.update(width: bounds.width, height: bounds.height)
    }
    hostView.onWindowChanged = { [weak self] attached in
      guard let self else { return }
      self.attached = attached
      attached ? self.renderer.start() : self.renderer.stop()
    }
  }

  func afterUpdate() {
    updateRenderer()
  }

  private func updateRenderer() {
    hostView.backgroundColor = colors[0]
    renderer.update(
      width: hostView.bounds.width,
      height: hostView.bounds.height,
      colors: colors,
      duration: duration
    )
    if attached {
      renderer.start()
    }
  }

  private static func color(from value: String) -> UIColor {
    var hex = value.trimmingCharacters(in: .whitespacesAndNewlines)
    if hex.hasPrefix("#") { hex.removeFirst() }
    guard hex.count == 6, let raw = UInt64(hex, radix: 16) else {
      return oneKeySkeletonDefaultGradientColors[0]
    }
    return UIColor(
      red: CGFloat((raw >> 16) & 0xff) / 255,
      green: CGFloat((raw >> 8) & 0xff) / 255,
      blue: CGFloat(raw & 0xff) / 255,
      alpha: 1
    )
  }
}
