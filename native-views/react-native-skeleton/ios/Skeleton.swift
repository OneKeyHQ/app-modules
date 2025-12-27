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
    isActive = false
    stopShimmer()
  }
  
  private func setupView() {
    view.clipsToBounds = true
    
    // Start animation when view is ready
    DispatchQueue.main.async {
      self.startShimmer()
    }
  }
  
  func startShimmer() {
    guard isActive else { return }
    
    stopShimmer()
    
    guard !view.bounds.isEmpty else {
      // Retry after a short delay if bounds are not ready
      DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) { [weak self] in
        guard let self = self, self.isActive else { return }
        self.startShimmer()
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
    
    gradientLayer.add(animation, forKey: "shimmerAnimation")
  }
  
  func stopShimmer() {
    // Remove animation and shimmer layer
    shimmerLayer?.removeAnimation(forKey: "shimmerAnimation")
    shimmerLayer?.removeFromSuperlayer()
    shimmerLayer = nil
    
    // Clear mask and skeleton layer
    view.layer.mask = nil
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
    startShimmer()
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
