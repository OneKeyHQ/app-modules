import Foundation
import UIKit

/// Renders an entire column of order-book depth bars for one side (asks/bids).
/// Each row is a CALayer whose horizontal `scaleX` fill is animated on the UI
/// thread, replacing N reanimated `DepthBar` instances.
final class HybridPerpDepthBars: HybridPerpDepthBarsSpec {

  // MARK: - HybridView
  var view: UIView = PerpLayoutView()

  // MARK: - Per-row fill layers
  private var barLayers: [CALayer] = []

  // MARK: - State for animation decisions
  private var lastPercents: [Double] = []
  private var lastEpoch: Double = .nan
  private var hasLaidOut = false

  // MARK: - Props (required props -> non-optional in generated spec)
  var percents: [Double] = [] { didSet { scheduleLayout() } }
  var rowHeight: Double = 0 { didSet { scheduleLayout() } }
  var rowMarginTop: Double = 0 { didSet { scheduleLayout() } }
  var barInset: Double = 0 { didSet { scheduleLayout() } }
  var origin: String = "left" { didSet { scheduleLayout() } }
  var reducedMotion: Bool = false { didSet { scheduleLayout() } }
  var epoch: Double = 0 { didSet { scheduleLayout() } }

  var color: String = "" {
    didSet {
      cachedColor = PerpColorParser.parse(color).cgColor
      applyColorToLayers()
    }
  }
  private var cachedColor: CGColor = UIColor.clear.cgColor

  // MARK: - Init
  override init() {
    super.init()
    view.isUserInteractionEnabled = false // taps handled by RN overlay
    view.clipsToBounds = true
    if let layoutView = view as? PerpLayoutView {
      layoutView.onLayout = { [weak self] in self?.performLayout() }
    }
  }

  private func scheduleLayout() {
    view.setNeedsLayout()
  }

  // MARK: - Layout + animation
  private func performLayout() {
    let bounds = view.bounds
    guard bounds.width > 0 else { return }

    let count = percents.count
    syncLayerCount(count)

    // Decide whether this update should animate or snap.
    let epochChanged = epoch != lastEpoch
    let countChanged = lastPercents.count != count
    let snap = !hasLaidOut || reducedMotion || epochChanged || countChanged

    let rowW = bounds.width
    let h = CGFloat(rowHeight)
    let inset = CGFloat(barInset)
    let barH = max(h - inset * 2, 0)
    let isRight = origin == "right"
    let anchorX: CGFloat = isRight ? 1 : 0

    CATransaction.begin()
    CATransaction.setDisableActions(true) // suppress implicit anims for geometry

    for i in 0..<count {
      let layer = barLayers[i]
      let rowTop = CGFloat(rowMarginTop) + CGFloat(i) * (h + CGFloat(rowMarginTop))
      let rowCenterY = rowTop + h / 2

      layer.anchorPoint = CGPoint(x: anchorX, y: 0.5)
      layer.bounds = CGRect(x: 0, y: 0, width: rowW, height: barH)
      layer.position = CGPoint(x: isRight ? rowW : 0, y: rowCenterY)
      layer.backgroundColor = cachedColor

      let target = clampScale(percents[i])
      let changed = i >= lastPercents.count || lastPercents[i] != percents[i]

      if snap || !changed {
        layer.removeAnimation(forKey: "fill")
        layer.transform = CATransform3DMakeScale(target, 1, 1)
      } else {
        animateScaleX(layer: layer, to: target)
      }
    }

    CATransaction.commit()

    lastPercents = percents
    lastEpoch = epoch
    hasLaidOut = true
  }

  private func animateScaleX(layer: CALayer, to target: CGFloat) {
    // Continue from the on-screen (presentation) value to avoid snap-back when
    // a new animation starts every tick (~10Hz).
    let current: CGFloat
    if let pres = layer.presentation() {
      current = pres.value(forKeyPath: "transform.scale.x") as? CGFloat
        ?? (layer.value(forKeyPath: "transform.scale.x") as? CGFloat ?? target)
    } else {
      current = layer.value(forKeyPath: "transform.scale.x") as? CGFloat ?? target
    }

    layer.transform = CATransform3DMakeScale(target, 1, 1) // model = final

    let anim = CABasicAnimation(keyPath: "transform.scale.x")
    anim.fromValue = current
    anim.toValue = target
    anim.duration = PerpTiming.depthBarDurationMs / 1000.0
    anim.timingFunction = PerpTiming.easeOutCubic()
    anim.isRemovedOnCompletion = true
    layer.add(anim, forKey: "fill")
  }

  private func syncLayerCount(_ count: Int) {
    if barLayers.count < count {
      for _ in barLayers.count..<count {
        let l = CALayer()
        l.backgroundColor = cachedColor
        view.layer.addSublayer(l)
        barLayers.append(l)
      }
    } else if barLayers.count > count {
      for l in barLayers[count...] { l.removeFromSuperlayer() }
      barLayers.removeLast(barLayers.count - count)
    }
  }

  private func applyColorToLayers() {
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    for l in barLayers { l.backgroundColor = cachedColor }
    CATransaction.commit()
  }

  private func clampScale(_ percent: Double) -> CGFloat {
    CGFloat(max(0, min(100, percent)) / 100.0)
  }
}
