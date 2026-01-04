import Foundation
import UIKit

// Animation constants
let DEFAULT_GRADIENT_COLORS: [UIColor] = [UIColor(red: 210.0/255.0, green: 210.0/255.0, blue: 210.0/255.0, alpha: 1.0), UIColor(red: 235.0/255.0, green: 235.0/255.0, blue: 235.0/255.0, alpha: 1.0)]

class HybridSkeleton : HybridSkeletonSpec {

  // Shimmer layers
  private var shimmerLayer: CAGradientLayer?
  private var skeletonLayer: CALayer?
  private var customGradientColors: [UIColor]?
  private var isActive: Bool = true
  private var retryCount: Int = 0
  private let maxRetryCount: Int = 10
  private var viewObserver: NSKeyValueObservation?

  var shimmerGradientColors: [String]? {
    didSet {
      guard isActive else { return }
      DispatchQueue.main.async { [weak self] in
        guard let self = self, self.isActive else { return }
        self.customGradientColors = shimmerGradientColors?.map { self.hexStringToUIColor(hexColor: $0) }
        self.restartShimmer()
      }
    }
  }

  // UIView
  var view: UIView = UIView()

  var shimmerSpeed: Double? {
    didSet {
      guard isActive else { return }
      DispatchQueue.main.async { [weak self] in
        guard let self = self, self.isActive else { return }
        self.restartShimmer()
      }
    }
  }

  override init() {
    super.init()
    setupView()
  }

  deinit {
    // Mark as inactive to prevent any new operations
    isActive = false

    // Clean up observer
    viewObserver?.invalidate()
    viewObserver = nil

    // Cleanup must happen on main thread, but avoid deadlock
    // Use async if not on main thread to prevent blocking
    if Thread.isMainThread {
      stopShimmer()
      // Clear view reference and ensure no dangling animations
      view.layer.removeAllAnimations()
      view.layer.sublayers?.removeAll()
      view.layer.mask = nil
    } else {
      // Use async to avoid potential deadlock with sync
      // All CALayer operations must happen on main thread
      let shimmer = self.shimmerLayer
      let skeleton = self.skeletonLayer
      DispatchQueue.main.async { [weak view = self.view] in
        guard let view = view else { return }
        // Stop shimmer animations
        shimmer?.removeAllAnimations()
        shimmer?.removeFromSuperlayer()

        // Clear mask reference
        if view.layer.mask === skeleton {
          view.layer.mask = nil
        }

        // Clean up remaining animations and sublayers
        view.layer.removeAllAnimations()
        view.layer.sublayers?.removeAll()
      }
    }
  }

  private func setupView() {
    view.clipsToBounds = true

    // Observe view hierarchy changes for cleanup
    viewObserver = view.observe(\.superview, options: [.new]) { [weak self] view, change in
      guard let self = self else { return }
      if change.newValue == nil {
        // View was removed from hierarchy, clean up
        DispatchQueue.main.async { [weak self] in
          self?.stopShimmer()
        }
      }
    }

    // Start animation when view is ready
    DispatchQueue.main.async { [weak self] in
      guard let self = self, self.isActive else { return }
      self.startShimmer()
    }
  }

  func startShimmer() {
    retryCount = 0 // Reset retry count when starting new shimmer
    startShimmerInternal()
  }

  private func startShimmerInternal() {
    guard isActive else { return }

    stopShimmer()

    guard !view.bounds.isEmpty else {
      // Check if we have exceeded max retry count
      guard retryCount < maxRetryCount else {
        print("⚠️ Skeleton shimmer: Max retry count reached. View bounds are still empty.")
        return
      }

      retryCount += 1
      // Retry after a short delay if bounds are not ready
      DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) { [weak self] in
        guard let self = self, self.isActive else { return }
        self.startShimmerInternal()
      }
      return
    }

    let colors: [UIColor] = customGradientColors ?? DEFAULT_GRADIENT_COLORS
    let backgroundColor = colors[0].cgColor
    let highlightColor = colors[1].cgColor

    // Create skeleton layer for masking
    let newSkeletonLayer = CALayer()
    newSkeletonLayer.backgroundColor = backgroundColor
    newSkeletonLayer.name = "SkeletonLayer"
    newSkeletonLayer.anchorPoint = .zero
    newSkeletonLayer.frame = view.bounds

    // Create gradient layer for animation
    let gradientLayer = CAGradientLayer()
    gradientLayer.colors = [backgroundColor, highlightColor, backgroundColor]
    gradientLayer.startPoint = CGPoint(x: 0.0, y: 0.5)
    gradientLayer.endPoint = CGPoint(x: 1.0, y: 0.5)
    gradientLayer.frame = view.bounds
    gradientLayer.name = "ShimmerLayer"

    // Set mask and add gradient layer (don't add skeleton layer as sublayer)
    view.layer.mask = newSkeletonLayer
    view.layer.addSublayer(gradientLayer)
    view.clipsToBounds = true

    // Store references
    skeletonLayer = newSkeletonLayer
    shimmerLayer = gradientLayer

    // Create animation
    let width = view.bounds.width
    let animation = CABasicAnimation(keyPath: "transform.translation.x")
    animation.duration = shimmerSpeed ?? 3
    animation.fromValue = -width
    animation.toValue = width
    animation.repeatCount = .infinity
    animation.autoreverses = false
    animation.fillMode = CAMediaTimingFillMode.forwards
    animation.isRemovedOnCompletion = false // Keep animation when completed

    // Use weak self pattern for potential future animation delegates/completion handlers
    gradientLayer.add(animation, forKey: "shimmerAnimation")
  }

  func stopShimmer() {
    // Remove animation from shimmer layer
    shimmerLayer?.removeAllAnimations()  // Remove all animations, more comprehensive
    shimmerLayer?.removeFromSuperlayer()

    // Clear mask reference before setting to nil
    if view.layer.mask === skeletonLayer {
      view.layer.mask = nil
    }

    // Clean up all sublayers that might be shimmer layers
    view.layer.sublayers?.forEach { layer in
      if layer.name == "ShimmerLayer" {
        layer.removeAllAnimations()
        layer.removeFromSuperlayer()
      }
    }

    // Nil out references
    shimmerLayer = nil
    skeletonLayer = nil
  }

  func afterUpdate() {
    guard isActive else { return }
    DispatchQueue.main.async { [weak self] in
      guard let self = self, self.isActive else { return }
      self.restartShimmer()
    }
  }

  func restartShimmer() {
    guard isActive else { return }
    stopShimmer()
    retryCount = 0 // Reset retry count on restart
    startShimmerInternal()
  }

  func hexStringToUIColor(hexColor: String) -> UIColor {
    var hexSanitized = hexColor.trimmingCharacters(in: .whitespacesAndNewlines)

    if hexSanitized.hasPrefix("#") {
      hexSanitized.remove(at: hexSanitized.startIndex)
    }

    var color: UInt32 = 0
    let stringScanner = Scanner(string: hexSanitized)
    stringScanner.scanHexInt32(&color)

    let r = CGFloat(Int(color >> 16) & 0x000000FF)
    let g = CGFloat(Int(color >> 8) & 0x000000FF)
    let b = CGFloat(Int(color) & 0x000000FF)

    return UIColor(red: r / 255.0, green: g / 255.0, blue: b / 255.0, alpha: 1)
  }
}
