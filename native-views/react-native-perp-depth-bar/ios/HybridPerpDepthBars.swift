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

  // MARK: - Per-row text layers (price left, size right)
  private var priceLayers: [CATextLayer] = []
  private var sizeLayers: [CATextLayer] = []
  private let textScale = UIScreen.main.scale

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

  // MARK: - Text props
  var prices: [String] = [] { didSet { scheduleLayout() } }
  var sizes: [String] = [] { didSet { scheduleLayout() } }
  var priceFontSize: Double = 11 { didSet { scheduleLayout() } }
  var sizeFontSize: Double = 11 { didSet { scheduleLayout() } }
  var textInset: Double = 0 { didSet { scheduleLayout() } }

  var priceColor: String = "" {
    didSet {
      cachedPriceColor = PerpColorParser.parse(priceColor).cgColor
      scheduleLayout()
    }
  }
  var sizeColor: String = "" {
    didSet {
      cachedSizeColor = PerpColorParser.parse(sizeColor).cgColor
      scheduleLayout()
    }
  }
  private var cachedPriceColor: CGColor = UIColor.black.cgColor
  private var cachedSizeColor: CGColor = UIColor.gray.cgColor

  // MARK: - Tap callback (native row hit-testing)
  var onRowPress: ((Double) -> Void)?

  // MARK: - Init
  override init() {
    super.init()
    view.isUserInteractionEnabled = true // native row tap handling
    view.clipsToBounds = true
    if let layoutView = view as? PerpLayoutView {
      layoutView.onLayout = { [weak self] in self?.performLayout() }
      layoutView.onTap = { [weak self] y in self?.handleTap(atY: y) }
    }
  }

  private func handleTap(atY y: CGFloat) {
    let step = CGFloat(rowHeight + rowMarginTop)
    guard step > 0 else { return }
    let i = Int((y - CGFloat(rowMarginTop)) / step)
    let count = max(percents.count, max(prices.count, sizes.count))
    if i >= 0 && i < count {
      onRowPress?(Double(i))
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
    syncTextLayerCount(count)

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

    // Text never animates — snap frames/strings each layout pass.
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    let half = rowW / 2
    let pad = CGFloat(textInset)
    for i in 0..<count {
      let rowTop = CGFloat(rowMarginTop) + CGFloat(i) * (h + CGFloat(rowMarginTop))
      let priceLine = CGFloat(priceFontSize) * 1.16
      let sizeLine = CGFloat(sizeFontSize) * 1.16
      let priceLayer = priceLayers[i]
      priceLayer.frame = CGRect(
        x: pad, y: rowTop + (h - priceLine) / 2,
        width: max(half - pad, 0), height: priceLine)
      priceLayer.fontSize = CGFloat(priceFontSize)
      priceLayer.foregroundColor = cachedPriceColor
      priceLayer.string = i < prices.count ? prices[i] : ""

      let sizeLayer = sizeLayers[i]
      sizeLayer.frame = CGRect(
        x: half, y: rowTop + (h - sizeLine) / 2,
        width: max(half - pad, 0), height: sizeLine)
      sizeLayer.fontSize = CGFloat(sizeFontSize)
      sizeLayer.foregroundColor = cachedSizeColor
      sizeLayer.string = i < sizes.count ? sizes[i] : ""
    }
    CATransaction.commit()

    lastPercents = percents
    lastEpoch = epoch
    hasLaidOut = true
  }

  private func makeTextLayer(alignment: CATextLayerAlignmentMode) -> CATextLayer {
    let l = CATextLayer()
    l.contentsScale = textScale
    l.alignmentMode = alignment
    l.truncationMode = .none
    l.isWrapped = false
    return l
  }

  private func syncTextLayerCount(_ count: Int) {
    if priceLayers.count < count {
      for _ in priceLayers.count..<count {
        let p = makeTextLayer(alignment: .left)
        let s = makeTextLayer(alignment: .right)
        view.layer.addSublayer(p)
        view.layer.addSublayer(s)
        priceLayers.append(p)
        sizeLayers.append(s)
      }
    } else if priceLayers.count > count {
      for l in priceLayers[count...] { l.removeFromSuperlayer() }
      for l in sizeLayers[count...] { l.removeFromSuperlayer() }
      priceLayers.removeLast(priceLayers.count - count)
      sizeLayers.removeLast(sizeLayers.count - count)
    }
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
