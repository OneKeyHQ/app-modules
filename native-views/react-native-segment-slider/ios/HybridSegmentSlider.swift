import Foundation
import UIKit

/// Native segmented slider. Renders the track, fill bar, segment marks, thumb
/// and value bubble as CALayers and handles the drag gesture itself, so a drag
/// never crosses the JS bridge per frame. Mirrors the web variant
/// (`composite/SegmentSlider/index.tsx`) pixel-for-pixel.
final class HybridSegmentSlider: HybridSegmentSliderSpec {

  // MARK: - Geometry (points), identical to the web constants
  private let thumbSize: CGFloat = 16
  private let markSize: CGFloat = 10
  private let haloRing: CGFloat = 4 // translucent halo width beyond each mark
  private var inset: CGFloat { thumbSize / 2 }

  // MARK: - HybridView
  var view: UIView = SegmentSliderUIView()

  // MARK: - Layers
  private let trackLayer = CALayer()
  private let fillLayer = CALayer()
  private let thumbLayer = CALayer()
  private var markLayers: [CALayer] = []
  private let bubbleLayer = CALayer()
  private let bubbleTextLayer = CATextLayer()
  // Translucent highlight shown around the segment node the thumb is over while
  // dragging (mobile analogue of the web mark hover halo). One shared layer,
  // repositioned to the active node; below the marks so the node dot stays crisp.
  private let haloLayer = CALayer()

  // MARK: - Cached colors
  private var cFill = UIColor.clear.cgColor
  private var cTrack = UIColor.clear.cgColor
  private var cThumb = UIColor.white.cgColor
  private var cThumbBorder = UIColor.gray.cgColor
  private var cMarkActive = UIColor.clear.cgColor
  private var cMarkInactive = UIColor.white.cgColor
  private var cMarkBorder = UIColor.gray.cgColor
  private var cBubble = UIColor.black.cgColor
  private var cBubbleText = UIColor.white.cgColor

  // MARK: - Interaction state
  private var currentValue: Double = 0
  private var lastEmitted: Double = .nan
  private var isDragging = false
  private var hasLaidOut = false
  // Tap-vs-drag tracking for `snapTapToSegment`. A gesture starts as a tap
  // candidate; once the finger travels past `tapMoveThreshold` it becomes a
  // drag and the release no longer snaps.
  private var touchStartX: CGFloat = 0
  private var lastTouchX: CGFloat = 0
  private var touchStartTime: CFTimeInterval = 0
  private var hasDragged = false
  private let tapMoveThreshold: CGFloat = 8
  // A tap only snaps when it's quick. Holding longer (without dragging) is a
  // long-press and does nothing — matching the competitor's tap window.
  private let tapMaxDuration: CFTimeInterval = 0.3
  // Segment node the thumb currently sits on (-1 = none). Drives the halo +
  // a light haptic tick fired once each time the thumb enters a new node.
  private var hoverMarkIndex = -1
  private lazy var haptic = UIImpactFeedbackGenerator(style: .light)

  // MARK: - Props (required props are non-optional in the generated spec)
  var value: Double = 0 {
    didSet {
      // While dragging, the finger owns the value — ignore controlled updates
      // (web skips its useLayoutEffect when draggingRef.current is true).
      if !isDragging {
        currentValue = value
        lastEmitted = value
        applyVisual(value)
      }
    }
  }
  var min: Double = 0 { didSet { scheduleLayout() } }
  var max: Double = 100 { didSet { scheduleLayout() } }
  var segments: Double = 0 { didSet { scheduleLayout() } }
  var sliderHeight: Double = 4 { didSet { scheduleLayout() } }
  var disabled: Bool = false {
    didSet {
      view.alpha = disabled ? 0.5 : 1
      view.isUserInteractionEnabled = !disabled
    }
  }
  var showBubble: Bool = true { didSet { scheduleLayout() } }
  var centerOrigin: Bool = false { didSet { scheduleLayout() } }
  // Gates tap snapping only; no visual change on its own, so no didSet.
  var snapTapToSegment: Bool = false
  var epoch: Double = 0 { didSet { scheduleLayout() } }

  var fillColor: String = "" {
    didSet { cFill = parse(fillColor); fillLayer.backgroundColor = cFill; scheduleLayout() }
  }
  var trackColor: String = "" {
    didSet { cTrack = parse(trackColor); trackLayer.backgroundColor = cTrack }
  }
  var thumbColor: String = "" {
    didSet { cThumb = parse(thumbColor); thumbLayer.backgroundColor = cThumb }
  }
  var thumbBorderColor: String = "" {
    didSet { cThumbBorder = parse(thumbBorderColor); thumbLayer.borderColor = cThumbBorder }
  }
  var markActiveColor: String = "" {
    didSet { cMarkActive = parse(markActiveColor); scheduleLayout() }
  }
  var markInactiveColor: String = "" {
    didSet { cMarkInactive = parse(markInactiveColor); scheduleLayout() }
  }
  var markBorderColor: String = "" {
    didSet { cMarkBorder = parse(markBorderColor); scheduleLayout() }
  }
  var bubbleColor: String = "" {
    didSet { cBubble = parse(bubbleColor); bubbleLayer.backgroundColor = cBubble }
  }
  var bubbleTextColor: String = "" {
    didSet { cBubbleText = parse(bubbleTextColor); bubbleTextLayer.foregroundColor = cBubbleText }
  }

  // MARK: - Callbacks
  var onChange: ((Double) -> Void)?
  var onSlideStart: (() -> Void)?
  var onSlideComplete: (() -> Void)?

  // MARK: - Init
  override init() {
    super.init()
    view.backgroundColor = .clear
    view.isUserInteractionEnabled = true

    trackLayer.cornerRadius = CGFloat(sliderHeight) / 2
    fillLayer.cornerRadius = CGFloat(sliderHeight) / 2

    thumbLayer.cornerRadius = thumbSize / 2
    thumbLayer.borderWidth = 1
    thumbLayer.shadowColor = UIColor.black.cgColor
    thumbLayer.shadowOpacity = 0.08
    thumbLayer.shadowRadius = 1
    thumbLayer.shadowOffset = CGSize(width: 0, height: 1)

    bubbleLayer.cornerRadius = 4
    bubbleLayer.opacity = 0
    bubbleTextLayer.contentsScale = UIScreen.main.scale
    bubbleTextLayer.alignmentMode = .center
    bubbleTextLayer.fontSize = 12
    bubbleTextLayer.font = UIFont.systemFont(ofSize: 12, weight: .semibold)
    bubbleLayer.addSublayer(bubbleTextLayer)

    let haloSize = markSize + haloRing * 2
    haloLayer.bounds = CGRect(x: 0, y: 0, width: haloSize, height: haloSize)
    haloLayer.cornerRadius = haloSize / 2
    haloLayer.opacity = 0

    view.layer.addSublayer(trackLayer)
    view.layer.addSublayer(fillLayer)
    view.layer.addSublayer(thumbLayer)
    view.layer.addSublayer(haloLayer)
    view.layer.addSublayer(bubbleLayer)

    if let v = view as? SegmentSliderUIView {
      v.onLayout = { [weak self] in self?.performLayout() }
      v.onTouchBegan = { [weak self] x in self?.handleTouchBegan(x) }
      v.onTouchMoved = { [weak self] x in self?.handleTouchMoved(x) }
      v.onTouchEnded = { [weak self] in self?.handleTouchEnded() }
    }
  }

  // MARK: - Color helper
  private func parse(_ s: String) -> CGColor {
    SegmentSliderColorParser.parse(s).cgColor
  }

  private func scheduleLayout() {
    view.setNeedsLayout()
  }

  // MARK: - Geometry helpers
  private var trackWidth: CGFloat { Swift.max(view.bounds.width - thumbSize, 0) }
  private var trackLeft: CGFloat { inset }
  private var range: Double { let r = max - min; return r == 0 ? 1 : r }
  private var markCount: Int { segments >= 1 ? Int(segments) + 1 : 0 }

  /// value -> fraction 0..1
  private func valueToFrac(_ v: Double) -> CGFloat {
    let clamped = Swift.max(min, Swift.min(max, v))
    return CGFloat((clamped - min) / range)
  }

  private func valueFromX(_ x: CGFloat) -> Double {
    let w = trackWidth
    if w <= 0 { return currentValue }
    let clampedX = Swift.max(trackLeft, Swift.min(trackLeft + w, x))
    let frac = Double((clampedX - trackLeft) / w)
    return min + frac * (max - min)
  }

  // MARK: - Layout
  private func performLayout() {
    let bounds = view.bounds
    guard bounds.width > 0 else { return }

    syncMarkLayers(markCount)

    let h = CGFloat(sliderHeight)
    let trackTop = bounds.midY - h / 2

    CATransaction.begin()
    CATransaction.setDisableActions(true)

    trackLayer.frame = CGRect(x: trackLeft, y: trackTop, width: trackWidth, height: h)
    trackLayer.cornerRadius = h / 2
    trackLayer.backgroundColor = cTrack
    fillLayer.cornerRadius = h / 2

    // Marks sit on the track line, centred vertically on it.
    for i in 0..<markLayers.count {
      let layer = markLayers[i]
      let frac = markCount > 1 ? CGFloat(i) / CGFloat(markCount - 1) : 0
      let cx = trackLeft + frac * trackWidth
      layer.bounds = CGRect(x: 0, y: 0, width: markSize, height: markSize)
      layer.position = CGPoint(x: cx, y: bounds.midY)
      layer.cornerRadius = markSize / 2
      layer.borderWidth = 1
    }

    // Thumb sizing (position set in applyVisual).
    thumbLayer.bounds = CGRect(x: 0, y: 0, width: thumbSize, height: thumbSize)
    thumbLayer.cornerRadius = thumbSize / 2
    thumbLayer.backgroundColor = cThumb
    thumbLayer.borderColor = cThumbBorder

    CATransaction.commit()

    hasLaidOut = true
    applyVisual(currentValue)
  }

  private func syncMarkLayers(_ count: Int) {
    if markLayers.count < count {
      for _ in markLayers.count..<count {
        let l = CALayer()
        l.borderWidth = 1
        // Marks render ABOVE the thumb + halo (web parity: mark zIndex 3 >
        // thumb 2). So when the thumb sits on a segment node the node's dot
        // shows through the thumb's centre — the "white ring + dark centre"
        // bullseye look. Inserting above the halo keeps order
        // track < fill < thumb < halo < marks < bubble.
        view.layer.insertSublayer(l, above: haloLayer)
        markLayers.append(l)
      }
    } else if markLayers.count > count {
      for l in markLayers[count...] { l.removeFromSuperlayer() }
      markLayers.removeLast(markLayers.count - count)
    }
  }

  // MARK: - Visual application (no implicit animations)
  private func applyVisual(_ v: Double) {
    guard hasLaidOut, view.bounds.width > 0 else { return }
    let bounds = view.bounds
    let h = CGFloat(sliderHeight)
    let trackTop = bounds.midY - h / 2
    let frac = valueToFrac(v)
    let thumbX = trackLeft + frac * trackWidth

    CATransaction.begin()
    CATransaction.setDisableActions(true)

    // Fill bar.
    if centerOrigin {
      if v == 0 {
        fillLayer.frame = CGRect(x: trackLeft, y: trackTop, width: 0, height: h)
      } else {
        let centerFrac = valueToFrac(0)
        let leftFrac = Swift.min(frac, centerFrac)
        let widthFrac = abs(frac - centerFrac)
        fillLayer.frame = CGRect(
          x: trackLeft + leftFrac * trackWidth, y: trackTop,
          width: widthFrac * trackWidth, height: h)
      }
    } else {
      fillLayer.frame = CGRect(
        x: trackLeft, y: trackTop, width: frac * trackWidth, height: h)
    }
    fillLayer.backgroundColor = cFill

    // Marks active states.
    let centerFrac = valueToFrac(0)
    for i in 0..<markLayers.count {
      let markFrac = markLayers.count > 1 ? CGFloat(i) / CGFloat(markLayers.count - 1) : 0
      var active: Bool
      if centerOrigin {
        if v == 0 {
          active = false
        } else if v > 0 {
          active = markFrac > centerFrac && markFrac <= frac
        } else {
          active = markFrac >= frac && markFrac < centerFrac
        }
      } else {
        active = markFrac <= frac
      }
      let layer = markLayers[i]
      layer.backgroundColor = active ? cMarkActive : cMarkInactive
      layer.borderColor = active ? cMarkActive : cMarkBorder
    }

    // Thumb position (keep current scale transform).
    thumbLayer.position = CGPoint(x: thumbX, y: bounds.midY)

    // Bubble.
    if showBubble {
      let text = "\(Int(v.rounded()))%"
      bubbleTextLayer.string = text
      let font = UIFont.systemFont(ofSize: 12, weight: .semibold)
      let textW = (text as NSString)
        .size(withAttributes: [.font: font]).width
      let padH: CGFloat = 8
      let bw = ceil(textW) + padH * 2
      let bh: CGFloat = 20
      let by = trackTop - 8 - bh
      bubbleLayer.frame = CGRect(x: thumbX - bw / 2, y: by, width: bw, height: bh)
      bubbleTextLayer.frame = CGRect(x: 0, y: 2, width: bw, height: 16)
    }

    CATransaction.commit()

    updateHover(thumbX: thumbX)
  }

  // MARK: - Node halo + haptic (mobile analogue of the web mark hover)
  /// Highlights the node the thumb is over and fires a light haptic tick once
  /// per node entered. Only while dragging — controlled value syncs just keep
  /// the halo hidden. Does NOT snap the value (parity with web/perp).
  private func updateHover(thumbX: CGFloat) {
    guard isDragging, markCount > 0 else {
      if hoverMarkIndex != -1 { hoverMarkIndex = -1; setHalo(visible: false, index: 0) }
      return
    }
    let threshold: CGFloat = 12 // half of the 24pt mark hit area
    var best = -1
    var bestDist = threshold
    for i in 0..<markCount {
      let frac = markCount > 1 ? CGFloat(i) / CGFloat(markCount - 1) : 0
      let mx = trackLeft + frac * trackWidth
      let d = abs(thumbX - mx)
      if d <= bestDist { bestDist = d; best = i }
    }
    guard best != hoverMarkIndex else { return }
    hoverMarkIndex = best
    if best == -1 {
      setHalo(visible: false, index: 0)
    } else {
      setHalo(visible: true, index: best)
      haptic.impactOccurred(intensity: 0.6)
      haptic.prepare()
    }
  }

  private func setHalo(visible: Bool, index: Int) {
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    if visible {
      let frac = markCount > 1 ? CGFloat(index) / CGFloat(markCount - 1) : 0
      let mx = trackLeft + frac * trackWidth
      haloLayer.position = CGPoint(x: mx, y: view.bounds.midY)
      haloLayer.backgroundColor = UIColor(cgColor: cMarkActive)
        .withAlphaComponent(0.25).cgColor
      haloLayer.opacity = 1
    } else {
      haloLayer.opacity = 0
    }
    CATransaction.commit()
  }

  // MARK: - Touch handling
  /// True when tap-snapping should defer the value commit: enabled and there
  /// are marks to snap to.
  private var snapTapsActive: Bool { snapTapToSegment && markCount > 1 }

  private func handleTouchBegan(_ x: CGFloat) {
    guard !disabled else { return }
    isDragging = true
    hasDragged = false
    touchStartX = x
    lastTouchX = x
    touchStartTime = CACurrentMediaTime()
    haptic.prepare()
    onSlideStart?()
    animateThumbScale(1.15)
    setBubbleVisible(true)
    // Tap-snap mode: defer any value change until we know this is a tap or a
    // drag, so a tap never jumps to the raw touch point first.
    if !snapTapsActive {
      setValue(valueFromX(x))
    }
  }

  private func handleTouchMoved(_ x: CGFloat) {
    guard isDragging else { return }
    lastTouchX = x
    if !hasDragged, abs(x - touchStartX) > tapMoveThreshold {
      hasDragged = true
    }
    // Still a tap candidate in snap mode → keep the thumb put.
    if snapTapsActive, !hasDragged { return }
    setValue(valueFromX(x))
  }

  private func handleTouchEnded() {
    guard isDragging else { return }
    isDragging = false
    // A quick tap (no drag) lands directly on the nearest segment node in a
    // single move — no detour through the raw touch point. A long press (held
    // past tapMaxDuration without dragging) does nothing. A drag keeps its
    // free value.
    if snapTapsActive, !hasDragged {
      let pressDuration = CACurrentMediaTime() - touchStartTime
      if pressDuration <= tapMaxDuration {
        setValue(snapValueToNearestMark(valueFromX(lastTouchX)))
      }
    }
    hoverMarkIndex = -1
    setHalo(visible: false, index: 0)
    animateThumbScale(1.0)
    setBubbleVisible(false)
    onSlideComplete?()
  }

  /// Snaps a value to the value of the nearest segment mark. Caller guarantees
  /// `markCount > 1`.
  private func snapValueToNearestMark(_ v: Double) -> Double {
    let frac = Double(valueToFrac(v))
    let idx = (frac * Double(markCount - 1)).rounded()
    let snappedFrac = idx / Double(markCount - 1)
    return min + snappedFrac * (max - min)
  }

  private func setValue(_ raw: Double) {
    let clamped = Swift.max(min, Swift.min(max, raw))
    let rounded = clamped.rounded()
    currentValue = rounded
    applyVisual(rounded)
    if rounded != lastEmitted {
      lastEmitted = rounded
      onChange?(rounded)
    }
  }

  private func animateThumbScale(_ s: CGFloat) {
    CATransaction.begin()
    CATransaction.setAnimationDuration(0.12)
    thumbLayer.transform = CATransform3DMakeScale(s, s, 1)
    CATransaction.commit()
  }

  private func setBubbleVisible(_ visible: Bool) {
    guard showBubble else { return }
    CATransaction.begin()
    CATransaction.setAnimationDuration(0.15)
    bubbleLayer.opacity = visible ? 1 : 0
    CATransaction.commit()
  }
}
