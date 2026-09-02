import Foundation
import UIKit

public let oneKeySkeletonDefaultGradientColors: [UIColor] = [
  UIColor(red: 210.0 / 255.0, green: 210.0 / 255.0, blue: 210.0 / 255.0, alpha: 1.0),
  UIColor(red: 235.0 / 255.0, green: 235.0 / 255.0, blue: 235.0 / 255.0, alpha: 1.0),
]

/// Lightweight shimmer renderer shared by the standalone Skeleton HybridView and
/// native consumers such as OneKeyImage. It owns layers, not a UIView, so native
/// consumers can render in their existing view hierarchy.
public final class OneKeySkeletonRenderer {
  private weak var hostLayer: CALayer?
  private let gradientLayer = CAGradientLayer()
  private var colors = oneKeySkeletonDefaultGradientColors
  private var duration: CFTimeInterval = 3
  private var requestedRunning = false
  private var lastAnimationSignature: String?

  @nonobjc public init(hostLayer: CALayer) {
    self.hostLayer = hostLayer
    gradientLayer.name = "OneKeySkeletonShimmerLayer"
    gradientLayer.startPoint = CGPoint(x: 0, y: 0.5)
    gradientLayer.endPoint = CGPoint(x: 1, y: 0.5)
    NotificationCenter.default.addObserver(
      self,
      selector: #selector(appDidBecomeActive),
      name: UIApplication.didBecomeActiveNotification,
      object: nil
    )
  }

  deinit {
    NotificationCenter.default.removeObserver(self)
    let cleanup = { [gradientLayer] in
      gradientLayer.removeAllAnimations()
      gradientLayer.removeFromSuperlayer()
    }
    if Thread.isMainThread {
      cleanup()
    } else {
      DispatchQueue.main.async(execute: cleanup)
    }
  }

  @nonobjc public func update(
    width: Double,
    height: Double,
    colors: [UIColor]? = nil,
    duration: CFTimeInterval? = nil
  ) {
    dispatchOnMain { [weak self] in
      guard let self else { return }
      if let colors, colors.count >= 2 {
        self.colors = Array(colors.prefix(2))
      }
      if let duration, duration > 0 {
        self.duration = duration
      }

      CATransaction.begin()
      CATransaction.setDisableActions(true)
      self.gradientLayer.frame = CGRect(x: 0, y: 0, width: width, height: height)
      self.gradientLayer.cornerRadius = self.hostLayer?.cornerRadius ?? 0
      CATransaction.commit()

      if self.requestedRunning {
        self.installAnimationIfNeeded()
      }
    }
  }

  @nonobjc public func start() {
    dispatchOnMain { [weak self] in
      guard let self else { return }
      self.requestedRunning = true
      self.installAnimationIfNeeded()
    }
  }

  @nonobjc public func stop() {
    dispatchOnMain { [weak self] in
      guard let self else { return }
      self.requestedRunning = false
      self.gradientLayer.removeAllAnimations()
      self.gradientLayer.removeFromSuperlayer()
      self.lastAnimationSignature = nil
    }
  }

  private func installAnimationIfNeeded() {
    guard requestedRunning,
      let hostLayer,
      !gradientLayer.bounds.isEmpty
    else { return }

    let colorKey = colors.map { color in
      (color.cgColor.components ?? []).map(String.init).joined(separator: "-")
    }.joined(separator: ",")
    let signature =
      "\(gradientLayer.bounds.width)x\(gradientLayer.bounds.height)|\(duration)|\(colorKey)"
    if gradientLayer.superlayer === hostLayer,
      gradientLayer.animation(forKey: "oneKeySkeletonShimmer") != nil,
      lastAnimationSignature == signature
    {
      return
    }

    gradientLayer.removeAllAnimations()
    if gradientLayer.superlayer !== hostLayer {
      gradientLayer.removeFromSuperlayer()
      hostLayer.addSublayer(gradientLayer)
    }

    let background = colors[0].cgColor
    let highlight = colors[1].cgColor
    gradientLayer.colors = [background, highlight, background]
    gradientLayer.locations = [0, 0.5, 1]

    let width = gradientLayer.bounds.width
    let animation = CABasicAnimation(keyPath: "transform.translation.x")
    animation.duration = duration
    animation.fromValue = -width
    animation.toValue = width
    animation.repeatCount = .infinity
    animation.timingFunction = CAMediaTimingFunction(name: .linear)
    animation.beginTime = 0
    gradientLayer.add(animation, forKey: "oneKeySkeletonShimmer")
    lastAnimationSignature = signature
  }

  @objc private func appDidBecomeActive() {
    dispatchOnMain { [weak self] in
      guard let self, self.requestedRunning else { return }
      self.installAnimationIfNeeded()
    }
  }

  private func dispatchOnMain(_ work: @escaping () -> Void) {
    if Thread.isMainThread {
      work()
    } else {
      DispatchQueue.main.async(execute: work)
    }
  }
}
