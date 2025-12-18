import Foundation
import UIKit

// Animation constants
let DEFAULT_GRADIENT_COLORS: [UIColor] = [UIColor.lightGray, UIColor.white]
private let ANIMATION_NAME = "alphaGradientAnimation"

// Animation protocols
protocol SkeletonAnimatableDelegate: AnyObject {
  var mainLayer: CAShapeLayer { get }
  var gradientColors: [UIColor] { get }
}

protocol SkeletonAnimatable {
  var delegate: SkeletonAnimatableDelegate? { get set }
  var duration: TimeInterval { get set }
  func start()
  func stop()
  func restart()
  func updateBounds(bounds: CGRect)
}

// Base animation class
class AnimationBase: SkeletonAnimatable {
  weak var delegate: SkeletonAnimatableDelegate?
  var isAnimating = false

  var animatedLayer: CALayer? {
    fatalError("Subclasses must override `animatedLayer`")
  }

  var duration: TimeInterval = 1.0 {
    didSet {
      restart()
    }
  }

  func start() {
    // Override in subclasses
  }

  func stop() {
    animatedLayer?.removeAllAnimations()
  }

  func restart() {
    stop()
    start()
  }

  func updateBounds(bounds: CGRect) {}
}

// Gradient animation implementation
final class AnimationGradient: AnimationBase {

  override var animatedLayer: CALayer? { gradientLayer }

  private let animation = CABasicAnimation(keyPath: "colors")

  private let gradientLayer: CAGradientLayer = {
    let layer = CAGradientLayer()

    layer.colors = [
      DEFAULT_GRADIENT_COLORS[0].cgColor,
      DEFAULT_GRADIENT_COLORS[1].cgColor,
      DEFAULT_GRADIENT_COLORS[0].cgColor,
    ]

    layer.startPoint = CGPoint(x: 0, y: 0.5)
    layer.endPoint = CGPoint(x: 1, y: 0.5)

    layer.locations = [0, 0.5, 1]

    return layer
  }()

  override func start() {
    if(gradientLayer.animation(forKey: ANIMATION_NAME) != nil){
      super.stop()
    }

    gradientLayer.frame = delegate?.mainLayer.bounds ?? .zero

    let colors = delegate?.gradientColors ?? DEFAULT_GRADIENT_COLORS

    animation.fromValue = [
      colors[0].cgColor,
      colors[1].cgColor,
      colors[0].cgColor,
    ]
    animation.toValue = [
      colors[1].cgColor,
      colors[0].cgColor,
      colors[1].cgColor,
    ]

    animation.duration = duration
    animation.autoreverses = true
    animation.repeatCount = .infinity

    gradientLayer.add(animation, forKey: ANIMATION_NAME)

    if(gradientLayer.superlayer == nil){
      delegate?.mainLayer.addSublayer(gradientLayer)
    }
  }

  override func updateBounds(bounds: CGRect) {
    gradientLayer.frame = bounds
  }

  deinit {
    gradientLayer.removeAnimation(forKey: ANIMATION_NAME )
    gradientLayer.removeFromSuperlayer()
  }
}

class HybridSkeleton : HybridSkeletonSpec, SkeletonAnimatableDelegate {
  var shimmerGradientColors: [String] = []

  // UIView
  var view: UIView = UIView()
  
  // Main layer for skeleton animation
  var mainLayer = CAShapeLayer()
  
  // Animation
  private var animator: SkeletonAnimatable = AnimationGradient()
  
  // Animation properties
  var gradientColors: [UIColor] = DEFAULT_GRADIENT_COLORS {
    didSet {
      guard let animator = animator as? AnimationGradient else { return }
      animator.restart()
    }
  }
  
  var animationSpeed: TimeInterval = 1.0 {
    didSet {
      animator.duration = animationSpeed
    }
  }

  // props
  var color: String = "#000" {
    didSet {
      let uiColor = hexStringToUIColor(hexColor: color)
      view.backgroundColor = uiColor
      
      // Update gradient colors based on the base color if not explicitly set
      if !hasExplicitGradientColors {
        let lighterColor = uiColor.withAlphaComponent(0.3)
        let darkerColor = uiColor.withAlphaComponent(0.8)
        gradientColors = [lighterColor, darkerColor]
      }
    }
  }
  
  var shimmerSpeed: Double = 1.0 {
    didSet {
      animationSpeed = TimeInterval(shimmerSpeed)
    }
  }
  
  private var hasExplicitGradientColors: Bool = false
  
  func setGradientColors(_ colors: [String]) {
    hasExplicitGradientColors = true
    gradientColors = colors.map { hexStringToUIColor(hexColor: $0) }
  }
  
  override init() {
    super.init()
    setupAnimation()
  }
  
  private func setupAnimation() {
    // Setup main layer
    view.layer.addSublayer(mainLayer)
    mainLayer.backgroundColor = UIColor.lightGray.cgColor
    
    // Setup animator
    animator.delegate = self
    animator.duration = animationSpeed
    
    // Start animation when view is ready
    DispatchQueue.main.async {
      self.startAnimation()
    }
  }
  
  private func startAnimation() {
    // Only start animation if view has proper bounds
    guard !view.bounds.isEmpty else {
      // Retry after a short delay if bounds are not ready
      DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
        self.startAnimation()
      }
      return
    }
    
    // Update main layer frame to match view bounds
    mainLayer.frame = view.bounds
    animator.updateBounds(bounds: view.bounds)
    animator.start()
  }
  
  // Called when view layout changes
  func updateLayout() {
    mainLayer.frame = view.bounds
    animator.updateBounds(bounds: view.bounds)
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
