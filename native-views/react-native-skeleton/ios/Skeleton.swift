import Foundation
import UIKit

// Animation constants
let DEFAULT_GRADIENT_COLORS: [UIColor] = [UIColor(red: 210.0/255.0, green: 210.0/255.0, blue: 210.0/255.0, alpha: 1.0), UIColor(red: 235.0/255.0, green: 235.0/255.0, blue: 235.0/255.0, alpha: 1.0)]

class HybridSkeleton : HybridSkeletonSpec {
  
  // Shimmer layer
  private var shimmerLayer: CAGradientLayer?
  private var customGradientColors: [UIColor]?
  
  var shimmerGradientColors: [String]? {
    didSet {
      Task {
        customGradientColors = shimmerGradientColors?.map { hexStringToUIColor(hexColor: $0) }
        await MainActor.run {
          restartShimmer()
        }
      }
    }
  }
  
  // UIView
  var view: UIView = UIView()
  
  var animationSpeed: TimeInterval = 3.0 {
    didSet {
      Task {
        await MainActor.run {
          restartShimmer()
        }
      }
    }
  }

  var shimmerSpeed: Double? {
    didSet {
      animationSpeed = TimeInterval(shimmerSpeed ?? 3.0)
    }
  }
  
  override init() {
    super.init()
    setupView()
  }
  
  private func setupView() {
    view.clipsToBounds = true
    
    // Start animation when view is ready
    DispatchQueue.main.async {
      self.startShimmer()
    }
  }
  
  func startShimmer() {
    stopShimmer()
    
    guard !view.bounds.isEmpty else {
      // Retry after a short delay if bounds are not ready
      DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
        self.startShimmer()
      }
      return
    }
    
    let colors: [UIColor] = customGradientColors ?? DEFAULT_GRADIENT_COLORS
    let backgroundColor = colors[0].cgColor
    let highlightColor = colors[1].cgColor
    
    let skeletonLayer = CALayer()
    skeletonLayer.backgroundColor = backgroundColor
    skeletonLayer.name = "SkeletonLayer"
    skeletonLayer.anchorPoint = .zero
    skeletonLayer.frame = view.bounds
    
    let gradientLayer = CAGradientLayer()
    gradientLayer.colors = [backgroundColor, highlightColor, backgroundColor]
    gradientLayer.startPoint = CGPoint(x: 0.0, y: 0.5)
    gradientLayer.endPoint = CGPoint(x: 1.0, y: 0.5)
    gradientLayer.frame = view.bounds
    gradientLayer.name = "ShimmerLayer"
    
    view.layer.mask = skeletonLayer
    view.layer.addSublayer(skeletonLayer)
    view.layer.addSublayer(gradientLayer)
    view.clipsToBounds = true
    
    shimmerLayer = gradientLayer
    
    let width = view.bounds.width
    let animation = CABasicAnimation(keyPath: "transform.translation.x")
    animation.duration = animationSpeed
    animation.fromValue = -width
    animation.toValue = width
    animation.repeatCount = .infinity
    animation.autoreverses = false
    animation.fillMode = CAMediaTimingFillMode.forwards
    
    gradientLayer.add(animation, forKey: "shimmerAnimation")
  }
  
  func stopShimmer() {
    shimmerLayer?.removeAnimation(forKey: "shimmerAnimation")
    shimmerLayer?.removeFromSuperlayer()
    shimmerLayer = nil
  }
  
  func afterUpdate() {
    restartShimmer()
  }
  
  func restartShimmer() {
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
