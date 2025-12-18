import Foundation
import UIKit

// Animation constants
let DEFAULT_GRADIENT_COLORS: [UIColor] = [UIColor(red: 210.0/255.0, green: 210.0/255.0, blue: 210.0/255.0, alpha: 1.0), UIColor(red: 235.0/255.0, green: 235.0/255.0, blue: 235.0/255.0, alpha: 1.0)]

class HybridSkeleton : HybridSkeletonSpec {
  
  var shimmerGradientColors: [String]? {
    didSet {
      if let shimmerGradientColors = shimmerGradientColors {
        customGradientColors = shimmerGradientColors.map { hexStringToUIColor(hexColor: $0) }
      }
    }
  }
  
  // UIView
  var view: UIView = UIView()
  
  // Shimmer layer
  private var shimmerLayer: CAGradientLayer?
  
  // Animation properties
  private var customGradientColors: [UIColor]?
  
  var animationSpeed: TimeInterval = 1.25 {
    didSet {
      restartShimmer()
    }
  }

  // props
  var color: String? {
    didSet {
      let uiColor = (color != nil) ? hexStringToUIColor(hexColor: color ?? "#000") : UIColor(cgColor: DEFAULT_GRADIENT_COLORS[0].cgColor)
      view.backgroundColor = uiColor
    }
  }
  
  var shimmerSpeed: Double? {
    didSet {
      animationSpeed = TimeInterval(shimmerSpeed ?? 1.25)
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
    
    let light: CGColor
    let transparent: CGColor
    
    if let customColors = customGradientColors, customColors.count >= 2 {
      transparent = customColors[0].cgColor
      light = customColors[1].cgColor
    } else {
      light = UIColor.white.withAlphaComponent(0.4).cgColor
      transparent = UIColor.white.withAlphaComponent(0.1).cgColor
    }
    
    // Gradient layer
    let gradient = CAGradientLayer()
    gradient.colors = [transparent, light, transparent]
    gradient.startPoint = CGPoint(x: 0.0, y: 0.4)
    gradient.endPoint = CGPoint(x: 1.0, y: 0.6)
    gradient.locations = [0.4, 0.5, 0.6]
    gradient.frame = CGRect(x: -view.bounds.width,
                            y: 0,
                            width: 2 * view.bounds.width,
                            height: view.bounds.height)
    gradient.name = "ShimmerLayer"
    view.layer.addSublayer(gradient)
    
    shimmerLayer = gradient
    
    // Animation
    let animation = CABasicAnimation(keyPath: "locations")
    animation.fromValue = [0.0, 0.1, 0.2]
    animation.toValue = [1.0, 1.1, 1.2]
    animation.duration = animationSpeed
    animation.repeatCount = .infinity
    gradient.add(animation, forKey: "shimmerAnimation")
  }
  
  func stopShimmer() {
    shimmerLayer?.removeAnimation(forKey: "shimmerAnimation")
    shimmerLayer?.removeFromSuperlayer()
    shimmerLayer = nil
  }
  
  func restartShimmer() {
    stopShimmer()
    startShimmer()
  }
  
  // Called when view layout changes
  func updateLayout() {
    restartShimmer()
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
