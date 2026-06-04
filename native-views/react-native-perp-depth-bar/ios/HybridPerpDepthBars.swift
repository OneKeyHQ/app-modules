import Foundation
import UIKit

/// Renders an entire column of order-book depth bars for one side (asks/bids).
/// Each row is a CALayer whose horizontal `scaleX` fill is eased on the UI
/// thread, replacing N reanimated `DepthBar` instances.
///
/// Animation model: a single `CADisplayLink` continuously eases every row's
/// on-screen scale toward its latest target (exponential smoothing). New data
/// just retargets — the link keeps gliding and only stops once every row has
/// settled. This makes updates chain into continuous motion regardless of data
/// cadence, and a fluctuating row count no longer freezes the column (only a
/// coin/tick switch snaps, via `epoch`).
final class HybridPerpDepthBars: HybridPerpDepthBarsSpec {

  // MARK: - HybridView
  var view: UIView = PerpLayoutView()

  // MARK: - Per-row fill layers
  private var barLayers: [CALayer] = []

  // MARK: - Per-row text layers (price left, size right)
  private var priceLayers: [CATextLayer] = []
  private var sizeLayers: [CATextLayer] = []
  private let textScale = UIScreen.main.scale

  // MARK: - Continuous-easing state
  private var currentScales: [CGFloat] = [] // on-screen scaleX per row (0...1)
  private var targetScales: [CGFloat] = []  // latest target scaleX per row
  private var displayLink: CADisplayLink?
  private var lastTick: CFTimeInterval = 0

  // MARK: - State for animation decisions
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

  deinit {
    stopDisplayLink()
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

  // MARK: - Layout + retarget
  private func performLayout() {
    let bounds = view.bounds
    guard bounds.width > 0 else { return }

    let count = percents.count
    syncLayerCount(count)
    syncTextLayerCount(count)

    // Only a coin/tick switch (or reduced motion / first paint) snaps. A
    // fluctuating row count no longer freezes the whole column — see below.
    let epochChanged = epoch != lastEpoch
    let snap = !hasLaidOut || reducedMotion || epochChanged

    let rowW = bounds.width
    let h = CGFloat(rowHeight)
    let inset = CGFloat(barInset)
    let barH = max(h - inset * 2, 0)
    let isRight = origin == "right"
    let anchorX: CGFloat = isRight ? 1 : 0

    // Geometry never animates — set it directly each pass.
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    for i in 0..<count {
      let layer = barLayers[i]
      let rowTop = CGFloat(rowMarginTop) + CGFloat(i) * (h + CGFloat(rowMarginTop))
      let rowCenterY = rowTop + h / 2
      layer.anchorPoint = CGPoint(x: anchorX, y: 0.5)
      layer.bounds = CGRect(x: 0, y: 0, width: rowW, height: barH)
      layer.position = CGPoint(x: isRight ? rowW : 0, y: rowCenterY)
      layer.backgroundColor = cachedColor
    }
    CATransaction.commit()

    var newTargets = [CGFloat](repeating: 0, count: count)
    for i in 0..<count { newTargets[i] = clampScale(percents[i]) }

    if snap {
      stopDisplayLink()
      currentScales = newTargets
      targetScales = newTargets
      applyScales()
    } else {
      // Keep existing rows easing; brand-new rows appear at their target (no
      // grow-from-zero flash). Then retarget and let the display link glide.
      if currentScales.count < count {
        for i in currentScales.count..<count { currentScales.append(newTargets[i]) }
      } else if currentScales.count > count {
        currentScales.removeLast(currentScales.count - count)
      }
      targetScales = newTargets
      applyScales() // render current immediately (geometry/new rows) this frame
      startDisplayLinkIfNeeded()
    }

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

    lastEpoch = epoch
    hasLaidOut = true
  }

  /// Writes `currentScales` into every bar layer's transform (no implicit anim).
  private func applyScales() {
    let n = min(currentScales.count, barLayers.count)
    guard n > 0 else { return }
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    for i in 0..<n {
      barLayers[i].transform = CATransform3DMakeScale(currentScales[i], 1, 1)
    }
    CATransaction.commit()
  }

  // MARK: - Continuous easing (display link)
  private func startDisplayLinkIfNeeded() {
    if displayLink != nil { return }
    let n = min(currentScales.count, targetScales.count)
    var needs = false
    for i in 0..<n where abs(targetScales[i] - currentScales[i]) > 0.001 {
      needs = true
      break
    }
    guard needs else { return }
    lastTick = 0
    let proxy = DisplayLinkProxy(self)
    let link = CADisplayLink(target: proxy, selector: #selector(DisplayLinkProxy.tick(_:)))
    link.add(to: .main, forMode: .common)
    displayLink = link
  }

  private func stopDisplayLink() {
    displayLink?.invalidate()
    displayLink = nil
    lastTick = 0
  }

  /// Called by the display link each frame. Eases every row toward its target.
  func handleTick(_ link: CADisplayLink) {
    let now = link.timestamp
    let dt = lastTick > 0 ? now - lastTick : link.duration
    lastTick = now
    let tau = PerpTiming.depthBarSmoothingTauSeconds
    let alpha = CGFloat(1 - exp(-max(dt, 0) / max(tau, 0.0001)))

    var settled = true
    let n = min(min(currentScales.count, targetScales.count), barLayers.count)
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    for i in 0..<n {
      let t = targetScales[i]
      let c = currentScales[i]
      let d = t - c
      if abs(d) <= 0.001 {
        if c != t {
          currentScales[i] = t
          barLayers[i].transform = CATransform3DMakeScale(t, 1, 1)
        }
      } else {
        let nc = c + d * alpha
        currentScales[i] = nc
        barLayers[i].transform = CATransform3DMakeScale(nc, 1, 1)
        settled = false
      }
    }
    CATransaction.commit()

    if settled { stopDisplayLink() }
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

/// Weak forwarder so the `CADisplayLink` (retained by the run loop) does not
/// retain `HybridPerpDepthBars`, allowing `deinit` to invalidate the link.
private final class DisplayLinkProxy {
  weak var owner: HybridPerpDepthBars?
  init(_ owner: HybridPerpDepthBars) { self.owner = owner }
  @objc func tick(_ link: CADisplayLink) { owner?.handleTick(link) }
}
