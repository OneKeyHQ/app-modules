import Foundation
import UIKit

/// Two horizontal segments whose widths animate to the bid/ask ratio.
final class HybridPerpSideRatio: HybridPerpSideRatioSpec {

  // MARK: - HybridView
  var view: UIView = PerpLayoutView()

  private let bidLayer = CALayer()
  private let askLayer = CALayer()
  private var hasLaidOut = false

  // MARK: - Props
  var bidPercentage: Double = 50 { didSet { scheduleLayout() } }
  var askPercentage: Double = 50 { didSet { scheduleLayout() } }
  var segmentHeight: Double = 4 { didSet { scheduleLayout() } }
  var cornerRadius: Double = 999 { didSet { scheduleLayout() } }
  var gap: Double = 2 { didSet { scheduleLayout() } }
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

  private func scheduleLayout() {
    view.setNeedsLayout()
  }

  private func performLayout() {
    let bounds = view.bounds
    guard bounds.width > 0 else { return }

    let h = CGFloat(segmentHeight)
    let y = (bounds.height - h) / 2
    let g = CGFloat(gap)
    let available = max(bounds.width - g, 0)

    let bid = max(bidPercentage, 1)
    let ask = max(askPercentage, 1)
    let total = bid + ask
    let bidW = available * CGFloat(bid / total)
    let askW = available - bidW
    let radius = CGFloat(cornerRadius)

    let bidFrame = CGRect(x: 0, y: y, width: bidW, height: h)
    let askFrame = CGRect(x: bidW + g, y: y, width: askW, height: h)

    let snap = !hasLaidOut || reducedMotion

    CATransaction.begin()
    CATransaction.setDisableActions(true)
    bidLayer.cornerRadius = min(radius, h / 2)
    askLayer.cornerRadius = min(radius, h / 2)
    CATransaction.commit()

    if snap {
      CATransaction.begin()
      CATransaction.setDisableActions(true)
      bidLayer.frame = bidFrame
      askLayer.frame = askFrame
      CATransaction.commit()
    } else {
      animateFrame(layer: bidLayer, to: bidFrame)
      animateFrame(layer: askLayer, to: askFrame)
    }
    hasLaidOut = true
  }

  private func animateFrame(layer: CALayer, to frame: CGRect) {
    let duration = PerpTiming.sideRatioDurationMs / 1000.0
    let timing = PerpTiming.easeOutCubic()

    let fromBounds = layer.presentation()?.bounds ?? layer.bounds
    let fromPos = layer.presentation()?.position ?? layer.position

    let newBounds = CGRect(x: 0, y: 0, width: frame.width, height: frame.height)
    let newPos = CGPoint(x: frame.midX, y: frame.midY)

    layer.bounds = newBounds
    layer.position = newPos

    let boundsAnim = CABasicAnimation(keyPath: "bounds")
    boundsAnim.fromValue = NSValue(cgRect: fromBounds)
    boundsAnim.toValue = NSValue(cgRect: newBounds)
    boundsAnim.duration = duration
    boundsAnim.timingFunction = timing

    let posAnim = CABasicAnimation(keyPath: "position")
    posAnim.fromValue = NSValue(cgPoint: fromPos)
    posAnim.toValue = NSValue(cgPoint: newPos)
    posAnim.duration = duration
    posAnim.timingFunction = timing

    layer.add(boundsAnim, forKey: "ratioBounds")
    layer.add(posAnim, forKey: "ratioPos")
  }
}
