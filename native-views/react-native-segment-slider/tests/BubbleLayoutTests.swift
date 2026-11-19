import Foundation
import UIKit

// Only Nitro's generated base is stubbed. Layout, text measurement, layers,
// color parsing and touch callbacks come from the production Swift sources.
class HybridSegmentSliderSpec: NSObject {}

@main
enum BubbleLayoutTests {
  static var failures: [String] = []
  static var checks = 0

  static func check(_ condition: Bool, _ message: String) {
    checks += 1
    if !condition { failures.append(message) }
  }

  static func layer<T>(_ slider: HybridSegmentSlider, _ name: String) -> T {
    Mirror(reflecting: slider).children.first { $0.label == name }!.value as! T
  }

  static func makeSlider(width: CGFloat, value: Double) -> HybridSegmentSlider {
    let slider = HybridSegmentSlider()
    slider.fillColor = "#111111"
    slider.trackColor = "#dddddd"
    slider.thumbColor = "#ffffff"
    slider.thumbBorderColor = "#999999"
    slider.bubbleColor = "#111111"
    slider.bubbleTextColor = "#ffffff"
    slider.showBubble = true
    slider.segments = 4
    slider.defaultValue = value
    slider.view.frame = CGRect(x: 0, y: 60, width: width, height: 24)
    slider.view.layoutIfNeeded()
    return slider
  }

  static func verify(_ slider: HybridSegmentSlider, value: Double, label: String) {
    let bubble: CALayer = layer(slider, "bubbleLayer")
    let text: CATextLayer = layer(slider, "bubbleTextLayer")
    let thumb: CALayer = layer(slider, "thumbLayer")
    let bounds = slider.view.bounds
    let epsilon: CGFloat = 0.001
    check(bubble.frame.minX >= bounds.minX - epsilon, "\(label): left clipped")
    check(bubble.frame.maxX <= bounds.maxX + epsilon, "\(label): right clipped")
    check(thumb.frame.minX >= bounds.minX - epsilon, "\(label): thumb left clipped")
    check(thumb.frame.maxX <= bounds.maxX + epsilon, "\(label): thumb right clipped")
    check(thumb.frame.minY >= bounds.minY - epsilon, "\(label): thumb top clipped")
    check(thumb.frame.maxY <= bounds.maxY + epsilon, "\(label): thumb bottom clipped")
    let halo: CALayer = layer(slider, "haloLayer")
    if halo.opacity > 0 {
      check(halo.frame.minX >= bounds.minX - epsilon, "\(label): halo left clipped")
      check(halo.frame.maxX <= bounds.maxX + epsilon, "\(label): halo right clipped")
    }
    check(text.string as? String == "\(Int(value.rounded()))%", "\(label): text changed")
    let expectedX = 12 + CGFloat((value - slider.min) / (slider.max - slider.min))
      * (bounds.width - 24)
    check(abs(thumb.position.x - expectedX) < epsilon, "\(label): thumb moved")
    if thumb.position.x - bubble.bounds.width / 2 >= bounds.minX,
       thumb.position.x + bubble.bounds.width / 2 <= bounds.maxX {
      check(abs(bubble.frame.midX - thumb.position.x) < epsilon,
            "\(label): interior bubble is no longer centered")
    }
  }

  @MainActor
  static func flushQueuedValueSync() async {
    await withCheckedContinuation { continuation in
      DispatchQueue.main.async { continuation.resume() }
    }
  }

  @MainActor
  static func verifyDelayedValueSync() async throws {
    let slider = makeSlider(width: 320, value: 0)
    let control = slider.view as! SegmentSliderUIView
    var changes: [Double] = []
    var completedIds: [Double] = []
    var completedValues: [Double] = []
    slider.onChange = { changes.append($0) }
    slider.onSlideComplete = { id, value in
      completedIds.append(id)
      completedValues.append(value)
    }

    control.onTouchBegan?(12 + 0.6 * 296)
    control.onTouchEnded?()
    try slider.setValue(value: 40, interactionId: completedIds[0])
    control.onTouchBegan?(12 + 0.7 * 296)
    await flushQueuedValueSync()
    verify(slider, value: 70, label: "new drag after delayed sync")
    control.onTouchMoved?(12 + 0.8 * 296)
    control.onTouchEnded?()
    check(completedIds == [1, 2], "both interactions completed")
    check(completedValues == [60, 80], "completions report the final native values")

    try slider.setValue(value: 40, interactionId: completedIds[0])
    await flushQueuedValueSync()
    verify(slider, value: 80, label: "stale sync after newer completion")
    try slider.setValue(value: 50, interactionId: completedIds[1])
    await flushQueuedValueSync()
    verify(slider, value: 50, label: "accepted sync for latest completion")
    check(changes == [60, 70, 80], "only touch changes emit callbacks")
  }

  @MainActor
  static func main() async throws {
    for width: CGFloat in [160, 320, 600] {
      for value: Double in [0, 1, 25, 50, 75, 99, 100] {
        let slider = makeSlider(width: width, value: value)
        verify(slider, value: value, label: "width \(width), value \(value)")
        slider.view.frame.size.width = 120
        slider.view.setNeedsLayout()
        slider.view.layoutIfNeeded()
        verify(slider, value: value, label: "resized value \(value)")
      }
    }

    let slider = makeSlider(width: 320, value: 50)
    let control = slider.view as! SegmentSliderUIView
    var changes: [Double] = []
    var starts = 0
    var completions = 0
    slider.onChange = { changes.append($0) }
    slider.onSlideStart = { starts += 1 }
    slider.onSlideComplete = { _, _ in completions += 1 }
    slider.snapTapToSegment = true
    control.onTouchBegan?(160)
    for value: Double in [0, 50, 100] {
      control.onTouchMoved?(12 + CGFloat(value / 100) * 296)
      verify(slider, value: value, label: "drag \(value)")
      let bubble: CALayer = layer(slider, "bubbleLayer")
      check(bubble.opacity == 1, "drag \(value): bubble hidden")
    }
    control.onTouchEnded?()
    let bubble: CALayer = layer(slider, "bubbleLayer")
    check(changes == [0, 50, 100], "drag values changed: \(changes)")
    check(starts == 1 && completions == 1, "interaction callbacks changed")
    check(bubble.opacity == 0, "bubble not hidden after completion")
    control.onTouchBegan?(12 + 0.3 * 296)
    control.onTouchEnded?()
    verify(slider, value: 25, label: "tap snap")
    check(changes.last == 25, "tap no longer snaps to segment")
    for (x, value): (CGFloat, Double) in [(0, 0), (320, 100)] {
      control.onTouchBegan?(x)
      control.onTouchEnded?()
      verify(slider, value: value, label: "outer edge tap \(value)")
      check(changes.last == value, "outer edge no longer selects \(value)")
    }

    let signed = makeSlider(width: 320, value: 0)
    signed.min = -100
    signed.max = 100
    signed.centerOrigin = true
    signed.view.layoutIfNeeded()
    let signedControl = signed.view as! SegmentSliderUIView
    signedControl.onTouchBegan?(12)
    verify(signed, value: -100, label: "signed minimum")
    signedControl.onTouchMoved?(308)
    verify(signed, value: 100, label: "signed maximum")
    signedControl.onTouchEnded?()

    // Per-step haptics: one tick per value bucket during a drag; entering a
    // mark fires the mark tick instead of the step tick; 0 keeps mark-only.
    func hapticTicks(step: Double) -> [String] {
      var ticks: [String] = []
      let sample = makeSlider(width: 320, value: 0)
      sample.hapticStep = step
      sample.hapticTickRecorder = { ticks.append($0) }
      let control = sample.view as! SegmentSliderUIView
      func x(_ v: Double) -> CGFloat { 12 + CGFloat(v / 100) * 296 }
      control.onTouchBegan?(x(0))
      for v in [1.0, 1.2, 2.0, 25.0, 26.0] { control.onTouchMoved?(x(v)) }
      control.onTouchEnded?()
      return ticks
    }
    let stepTicks = hapticTicks(step: 1)
    check(stepTicks == ["mark", "step", "step", "mark", "step"],
          "hapticStep 1 ticks: \(stepTicks)")
    let markTicks = hapticTicks(step: 0)
    check(markTicks == ["mark", "mark"], "hapticStep 0 ticks: \(markTicks)")

    // A tap that never moves fires no step tick (20% is away from any mark).
    var tapTicks: [String] = []
    let tap = makeSlider(width: 320, value: 0)
    tap.hapticStep = 1
    tap.hapticTickRecorder = { tapTicks.append($0) }
    let tapControl = tap.view as! SegmentSliderUIView
    tapControl.onTouchBegan?(12 + 0.2 * 296)
    tapControl.onTouchEnded?()
    check(tapTicks.isEmpty, "tap without movement ticked: \(tapTicks)")

    // Disabling mid-drag ends the interaction once and ignores later moves.
    var cancelChanges: [Double] = []
    var cancelCompletes = 0
    let cancel = makeSlider(width: 320, value: 0)
    cancel.onChange = { cancelChanges.append($0) }
    cancel.onSlideComplete = { _, _ in cancelCompletes += 1 }
    let cancelControl = cancel.view as! SegmentSliderUIView
    cancelControl.onTouchBegan?(12 + 0.3 * 296)
    cancel.disabled = true
    cancelControl.onTouchMoved?(12 + 0.8 * 296)
    cancelControl.onTouchEnded?()
    check(cancelCompletes == 1, "disable mid-drag completions: \(cancelCompletes)")
    check(cancelChanges == [30], "disable mid-drag values: \(cancelChanges)")

    // Fractional bounds never emit a value past them.
    var boundChanges: [Double] = []
    let bounded = makeSlider(width: 320, value: 0.5)
    bounded.min = 0.5
    bounded.max = 10.5
    bounded.segments = 0
    bounded.view.layoutIfNeeded()
    bounded.onChange = { boundChanges.append($0) }
    let boundedControl = bounded.view as! SegmentSliderUIView
    boundedControl.onTouchBegan?(320)
    boundedControl.onTouchMoved?(0)
    boundedControl.onTouchEnded?()
    check(boundChanges == [10, 1], "fractional bound values: \(boundChanges)")

    try await verifyDelayedValueSync()

    // Render with a horizontally clipping ancestor, matching the failing sheet.
    // This is a UIKit layer preview, not a React Native or iPhone screenshot.
    if CommandLine.arguments.count > 1 {
      let canvas = UIView(frame: CGRect(x: 0, y: 0, width: 368, height: 360))
      canvas.backgroundColor = .white
      var retained: [HybridSegmentSlider] = []
      for (index, value) in [0.0, 50.0, 100.0].enumerated() {
        let container = UIView(frame: CGRect(x: 24, y: index * 110,
                                             width: 320, height: 110))
        container.clipsToBounds = true
        canvas.addSubview(container)
        let sample = makeSlider(width: 320, value: value)
        container.addSubview(sample.view)
        (sample.view as! SegmentSliderUIView).onTouchBegan?(12 + CGFloat(value / 100) * 296)
        retained.append(sample)
      }
      let image = UIGraphicsImageRenderer(bounds: canvas.bounds).image { context in
        canvas.layer.render(in: context.cgContext)
      }
      try image.pngData()!.write(to: URL(fileURLWithPath: CommandLine.arguments[1]))
      withExtendedLifetime(retained) {}
    }

    for failure in failures { print("FAIL: \(failure)") }
    print("\(checks - failures.count)/\(checks) native UIKit checks passed")
    if !failures.isEmpty { exit(1) }
  }
}
