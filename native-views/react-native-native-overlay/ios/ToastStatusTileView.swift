import UIKit

private enum ToastStatusMotion {
  static let easeOut = CAMediaTimingFunction(name: .easeOut)
  static let easeIn = CAMediaTimingFunction(name: .easeIn)
  static let tintEase = CAMediaTimingFunction(controlPoints: 0.4, 0, 0.2, 1)

  static func spring(
    _ keyPath: String, in layer: CALayer, from: Any, to: Any,
    stiffness: CGFloat, damping: CGFloat, delay: TimeInterval = 0
  ) {
    let animation = CASpringAnimation(keyPath: keyPath)
    animation.mass = 1
    animation.stiffness = stiffness
    animation.damping = damping
    animation.fromValue = from
    animation.toValue = to
    animation.duration = animation.settlingDuration
    animation.beginTime = layer.convertTime(CACurrentMediaTime(), from: nil) + delay
    animation.fillMode = .backwards
    layer.add(animation, forKey: keyPath)
  }

  static func basic(
    _ keyPath: String, in layer: CALayer, from: Any, to: Any,
    duration: TimeInterval, delay: TimeInterval = 0, timing: CAMediaTimingFunction
  ) {
    let animation = CABasicAnimation(keyPath: keyPath)
    animation.fromValue = from
    animation.toValue = to
    animation.duration = duration
    animation.beginTime = layer.convertTime(CACurrentMediaTime(), from: nil) + delay
    animation.timingFunction = timing
    animation.fillMode = .backwards
    layer.add(animation, forKey: keyPath)
  }

  static func scale(
    in layer: CALayer, values: [Double], times: [NSNumber],
    duration: TimeInterval, delay: TimeInterval, timings: [CAMediaTimingFunction]
  ) {
    let animation = CAKeyframeAnimation(keyPath: "transform.scale")
    animation.values = values
    animation.keyTimes = times
    animation.duration = duration
    animation.beginTime = layer.convertTime(CACurrentMediaTime(), from: nil) + delay
    animation.timingFunctions = timings
    animation.fillMode = .backwards
    layer.add(animation, forKey: "transform.scale")
  }

  static func withoutActions(_ update: () -> Void) {
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    update()
    CATransaction.commit()
  }
}

final class ToastStatusTileView: UIView {
  private var status: NativeOverlayToastStatus
  private var glyph: ToastStatusGlyphView
  private var departing: ToastStatusGlyphView?
  private var removal: DispatchWorkItem?
  private var tintUntil: TimeInterval = 0

  init(status: NativeOverlayToastStatus) {
    self.status = status
    glyph = ToastStatusGlyphView(status)
    super.init(frame: .zero)
    isUserInteractionEnabled = false
    clipsToBounds = true
    layer.cornerCurve = .continuous
    layer.backgroundColor = status.backgroundColor.cgColor
    addSubview(glyph)
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  override func layoutSubviews() {
    super.layoutSubviews()
    ToastStatusMotion.withoutActions {
      layer.cornerRadius = bounds.width * 11 / 36
      glyph.frame = bounds
      departing?.frame = bounds
    }
  }

  func enter() {
    // Critically damped tile spring: mass 1, stiffness 620, damping 50.
    ToastStatusMotion.spring(
      "opacity", in: layer, from: 0, to: 1, stiffness: 620, damping: 50, delay: 0.11
    )
    ToastStatusMotion.spring(
      "transform.scale", in: layer, from: 0.85, to: 1, stiffness: 620, damping: 50, delay: 0.11
    )
    glyph.pop(after: 0.155)
  }

  func apply(_ next: NativeOverlayToastStatus, settle: Bool) {
    let previous = status
    status = next
    if previous.status == next.status {
      glyph.setColor(next.color)
      if previous.backgroundColor != next.backgroundColor {
        tint(over: max(0, tintUntil - CACurrentMediaTime()))
      }
      return
    }

    let old = glyph
    let canSettle = settle && previous.status != next.status
    let oldAlpha = opacity(of: old.layer)
    let oldScale = scale(of: old.layer, initial: 0.4)
    let tileAlpha = opacity(of: layer)
    let tileScale = scale(of: layer, initial: 0.85)
    let delay: TimeInterval = canSettle && oldAlpha > 0 ? 0.09 : 0
    removeDeparting()
    glyph = ToastStatusGlyphView(next)
    glyph.frame = bounds
    addSubview(glyph)

    if delay > 0 {
      departing = old
      ToastStatusMotion.withoutActions {
        old.layer.opacity = 0
        old.layer.transform = CATransform3DMakeScale(0.5, 0.5, 1)
      }
      ToastStatusMotion.basic(
        "opacity", in: old.layer, from: oldAlpha, to: 0, duration: delay, timing: ToastStatusMotion.easeIn
      )
      ToastStatusMotion.basic(
        "transform.scale", in: old.layer, from: oldScale, to: 0.5, duration: delay, timing: ToastStatusMotion.easeIn
      )
      let removal = DispatchWorkItem { [weak self] in self?.removeDeparting() }
      self.removal = removal
      DispatchQueue.main.asyncAfter(deadline: .now() + delay, execute: removal)
    } else {
      old.stopRing()
      old.removeFromSuperview()
    }

    guard canSettle else {
      cancelMotion()
      return
    }
    tint(over: 0.14)
    ToastStatusMotion.basic(
      "opacity", in: layer, from: tileAlpha, to: 1, duration: 0.104, delay: delay, timing: ToastStatusMotion.easeOut
    )
    ToastStatusMotion.scale(
      in: layer, values: [tileScale, 1.07, 1], times: [0, 0.4, 1], duration: 0.26, delay: delay,
      timings: [ToastStatusMotion.easeOut, ToastStatusMotion.easeOut]
    )
    glyph.pop(after: delay)
  }

  private func opacity(of layer: CALayer) -> Float {
    layer.presentation()?.opacity ?? (layer.animation(forKey: "opacity") == nil ? layer.opacity : 0)
  }

  private func scale(of layer: CALayer, initial: Double) -> Double {
    if let presentation = layer.presentation() {
      return Double(presentation.transform.m11)
    }
    return layer.animation(forKey: "transform.scale") == nil ? 1 : initial
  }

  private func tint(over duration: TimeInterval) {
    let from = layer.presentation()?.backgroundColor ?? layer.backgroundColor
    let target = status.backgroundColor.cgColor
    ToastStatusMotion.withoutActions { layer.backgroundColor = target }
    if duration > 0, let from {
      tintUntil = CACurrentMediaTime() + duration
      ToastStatusMotion.basic(
        "backgroundColor", in: layer, from: from, to: target, duration: duration, timing: ToastStatusMotion.tintEase
      )
    }
  }

  func startRing() { glyph.startRing() }

  func cancelMotion() {
    layer.removeAllAnimations()
    removeDeparting()
    glyph.cancelMotion()
    tintUntil = 0
    tint(over: 0)
  }

  private func removeDeparting() {
    removal?.cancel()
    removal = nil
    departing?.stopRing()
    departing?.removeFromSuperview()
    departing = nil
  }
}

private final class ToastStatusGlyphView: UIView {
  private let art = CALayer()
  private let unitBounds = CGRect(x: 0, y: 0, width: 24, height: 24)
  private var contours: [CAShapeLayer] = []
  private var dot: CAShapeLayer?
  private var track: CAShapeLayer?
  private var arc: CAShapeLayer?

  init(_ status: NativeOverlayToastStatus) {
    super.init(frame: .zero)
    art.bounds = unitBounds
    layer.addSublayer(art)
    for points in status.contours {
      guard let first = points.first else { continue }
      let path = UIBezierPath()
      path.move(to: first)
      points.dropFirst().forEach { path.addLine(to: $0) }
      contours.append(shape(path.cgPath, width: status.strokeWidth))
    }
    if let dot = status.dot {
      let box = CGRect(
        x: dot.center.x - dot.radius, y: dot.center.y - dot.radius,
        width: 2 * dot.radius, height: 2 * dot.radius
      )
      self.dot = shape(UIBezierPath(ovalIn: box).cgPath, width: 0)
    }
    if let ring = status.ring {
      let center = CGPoint(x: 12, y: 12)
      let path = UIBezierPath(
        arcCenter: center, radius: ring.radius, startAngle: -.pi / 2,
        endAngle: 3 * .pi / 2, clockwise: true
      )
      let track = shape(path.cgPath, width: status.strokeWidth)
      track.opacity = ring.trackOpacity
      self.track = track
      let arc = shape(path.cgPath, width: status.strokeWidth)
      arc.strokeEnd = ring.arcFraction
      self.arc = arc
    }
    setColor(status.color)
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  override func layoutSubviews() {
    super.layoutSubviews()
    ToastStatusMotion.withoutActions {
      let scale = min(bounds.width, bounds.height) / 48
      art.position = CGPoint(x: bounds.midX, y: bounds.midY)
      art.setAffineTransform(CGAffineTransform(scaleX: scale, y: scale))
    }
  }

  private func shape(_ path: CGPath, width: CGFloat) -> CAShapeLayer {
    let shape = CAShapeLayer()
    shape.frame = unitBounds
    shape.path = path
    shape.fillColor = nil
    shape.lineWidth = width
    shape.lineCap = .round
    shape.lineJoin = .round
    art.addSublayer(shape)
    return shape
  }

  func setColor(_ color: UIColor) {
    ToastStatusMotion.withoutActions {
      contours.forEach { $0.strokeColor = color.cgColor }
      dot?.fillColor = color.cgColor
      track?.strokeColor = color.cgColor
      arc?.strokeColor = color.cgColor
    }
  }

  func pop(after delay: TimeInterval) {
    // Snappy POP spring: mass 1, stiffness 920, damping 40 (same ~6.4% overshoot, ~35% faster).
    ToastStatusMotion.spring(
      "transform.scale", in: layer, from: 0.4, to: 1, stiffness: 920, damping: 40, delay: delay
    )
    ToastStatusMotion.spring(
      "opacity", in: layer, from: 0, to: 1, stiffness: 920, damping: 40, delay: delay
    )
    contours.forEach { contour in
      ToastStatusMotion.basic(
        "strokeEnd", in: contour, from: 0, to: 1, duration: 0.18, delay: delay + 0.055,
        timing: ToastStatusMotion.easeOut
      )
    }
  }

  func startRing() {
    guard let arc, arc.animation(forKey: "spin") == nil else { return }
    let animation = CABasicAnimation(keyPath: "transform.rotation.z")
    animation.fromValue = 0
    animation.toValue = 2 * Double.pi
    animation.duration = 0.85
    animation.repeatCount = .infinity
    arc.add(animation, forKey: "spin")
  }

  func stopRing() { arc?.removeAnimation(forKey: "spin") }

  func cancelMotion() {
    layer.removeAllAnimations()
    contours.forEach { $0.removeAllAnimations() }
    stopRing()
  }
}
