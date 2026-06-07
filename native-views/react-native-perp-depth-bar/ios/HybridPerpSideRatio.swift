import Foundation
import UIKit

/// Two horizontal segments whose widths ease to the bid/ask ratio.
///
/// Animation model mirrors the depth bars: a `CADisplayLink` continuously eases
/// the split fraction toward its latest target (short fixed exponential
/// smoothing). New data just retargets; the link stops once settled.
final class HybridPerpSideRatio: HybridPerpSideRatioSpec {

  // MARK: - HybridView
  var view: UIView = PerpLayoutView()

  private let bidLayer = CALayer()
  private let askLayer = CALayer()
  private var hasLaidOut = false

  // MARK: - Continuous-easing state (bid share of the available width, 0...1)
  private var currentSplit: CGFloat = 0.5
  private var targetSplit: CGFloat = 0.5
  private var displayLink: CADisplayLink?
  private var lastTick: CFTimeInterval = 0

  // MARK: - Props
  var bidPercentage: Double = 50 { didSet { scheduleLayout() } }
  var askPercentage: Double = 50 { didSet { scheduleLayout() } }
  var segmentHeight: Double = 4 { didSet { scheduleLayout() } }
  var cornerRadius: Double = 999 { didSet { scheduleLayout() } }
  var gap: Double = 2 { didSet { scheduleLayout() } }
  /// Kept for OS "reduce motion" accessibility only — NOT a caller on/off knob.
  var reducedMotion: Bool = false { didSet { scheduleLayout() } }

  var longColor: String = "" {
    didSet {
      bidLayer.backgroundColor = PerpColorParser.parse(longColor).cgColor
    }
  }
  var shortColor: String = "" {
    didSet {
      askLayer.backgroundColor = PerpColorParser.parse(shortColor).cgColor
    }
  }

  override init() {
    super.init()
    view.isUserInteractionEnabled = false
    view.clipsToBounds = true
    bidLayer.maskedCorners = [.layerMinXMinYCorner, .layerMinXMaxYCorner]
    askLayer.maskedCorners = [.layerMaxXMinYCorner, .layerMaxXMaxYCorner]
    view.layer.addSublayer(bidLayer)
    view.layer.addSublayer(askLayer)
    if let layoutView = view as? PerpLayoutView {
      layoutView.onLayout = { [weak self] in self?.performLayout() }
    }
  }

  deinit {
    stopDisplayLink()
  }

  /// Imperative ratio update — bypasses the high-frequency
  /// `bidPercentage`/`askPercentage` prop write path (props/JSICache lock
  /// contention, REACT-NATIVE-1JZ). Companion to `HybridPerpDepthBars.setDepth`.
  /// The two args carry the SAME 0..100 semantics as the props of the same
  /// name; assigning them feeds through the identical `didSet` →
  /// `scheduleLayout` → `performLayout` clamp/split/display-link path the prop
  /// setters use — only the data source differs.
  func setRatio(bidPercentage: Double, askPercentage: Double) throws {
    self.bidPercentage = bidPercentage
    self.askPercentage = askPercentage
  }

  private func scheduleLayout() {
    // Imperative setRatio runs on the JS thread (off-main); `setNeedsLayout` must
    // be issued on the main thread.
    if Thread.isMainThread {
      view.setNeedsLayout()
    } else {
      DispatchQueue.main.async { [weak self] in self?.view.setNeedsLayout() }
    }
  }

  private func performLayout() {
    let bounds = view.bounds
    guard bounds.width > 0 else { return }

    let h = CGFloat(segmentHeight)
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    bidLayer.cornerRadius = min(CGFloat(cornerRadius), h / 2)
    askLayer.cornerRadius = min(CGFloat(cornerRadius), h / 2)
    CATransaction.commit()

    let bid = max(bidPercentage, 1)
    let ask = max(askPercentage, 1)
    let target = CGFloat(bid / (bid + ask))

    let snap = !hasLaidOut || reducedMotion
    if snap {
      stopDisplayLink()
      currentSplit = target
      targetSplit = target
      layoutSegments(currentSplit)
    } else {
      targetSplit = target
      layoutSegments(currentSplit) // apply current + any geometry change now
      startDisplayLinkIfNeeded()
    }
    hasLaidOut = true
  }

  /// Lays both segment frames out from a split fraction (no implicit anim).
  private func layoutSegments(_ split: CGFloat) {
    let bounds = view.bounds
    guard bounds.width > 0 else { return }
    let h = CGFloat(segmentHeight)
    let y = (bounds.height - h) / 2
    let g = CGFloat(gap)
    let available = max(bounds.width - g, 0)
    let bidW = available * split
    let askW = max(available - bidW, 0)
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    bidLayer.frame = CGRect(x: 0, y: y, width: bidW, height: h)
    askLayer.frame = CGRect(x: bidW + g, y: y, width: askW, height: h)
    CATransaction.commit()
  }

  // MARK: - Continuous easing (display link)
  private func startDisplayLinkIfNeeded() {
    if displayLink != nil { return }
    guard abs(targetSplit - currentSplit) > 0.0005 else { return }
    lastTick = 0
    let proxy = SideRatioLinkProxy(self)
    let link = CADisplayLink(target: proxy, selector: #selector(SideRatioLinkProxy.tick(_:)))
    link.add(to: .main, forMode: .common)
    displayLink = link
  }

  private func stopDisplayLink() {
    displayLink?.invalidate()
    displayLink = nil
    lastTick = 0
  }

  func handleTick(_ link: CADisplayLink) {
    let now = link.timestamp
    let dt = lastTick > 0 ? now - lastTick : link.duration
    lastTick = now
    let tau = PerpTiming.sideRatioSmoothingTauSeconds
    let alpha = CGFloat(1 - exp(-max(dt, 0) / max(tau, 0.0001)))

    let d = targetSplit - currentSplit
    if abs(d) <= 0.0005 {
      if currentSplit != targetSplit {
        currentSplit = targetSplit
        layoutSegments(currentSplit)
      }
      stopDisplayLink()
      return
    }
    currentSplit += d * alpha
    layoutSegments(currentSplit)
  }
}

/// Weak forwarder so the `CADisplayLink` does not retain `HybridPerpSideRatio`.
private final class SideRatioLinkProxy {
  weak var owner: HybridPerpSideRatio?
  init(_ owner: HybridPerpSideRatio) { self.owner = owner }
  @objc func tick(_ link: CADisplayLink) { owner?.handleTick(link) }
}
