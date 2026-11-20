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

  static func sliderControl(for slider: HybridSegmentSlider) -> SegmentSliderUIView {
    slider.view.subviews.first as! SegmentSliderUIView
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
    let control = sliderControl(for: slider)
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
  static func verifyAccessibilityAdjustments() async throws {
    let slider = makeSlider(width: 320, value: 99)
    slider.min = 1
    slider.sliderAccessibilityLabel = "TOP"
    let control = sliderControl(for: slider)
    var callbacks: [String] = []
    var haptics: [String] = []
    slider.onSlideStart = { callbacks.append("start") }
    slider.onChange = { callbacks.append("change \($0)") }
    slider.onSlideComplete = { id, value in callbacks.append("complete \(id) \(value)") }
    slider.hapticTickRecorder = { haptics.append($0) }

    check(!slider.view.isAccessibilityElement, "Nitro content view does not claim accessibility focus")
    let elements = slider.view.accessibilityElements as? [UIView]
    check(elements == [control], "content view exposes only the adjustable control")
    check(control.frame == slider.view.bounds, "slider fills its Nitro content view")
    let edge = CGPoint(x: slider.view.bounds.maxX - 1, y: slider.view.bounds.midY)
    check(slider.view.hitTest(edge, with: nil) === control, "outer edge hits the slider control")
    check(control.isAccessibilityElement, "slider is a VoiceOver focus target")
    check(control.accessibilityTraits.contains(.adjustable), "slider has the adjustable trait")
    check(control.accessibilityLabel == "TOP", "slider retains its provided accessibility label")
    check(control.accessibilityValue == 99.0.formatted(), "slider exposes its initial value")

    try slider.setValue(value: 40, interactionId: 0)
    control.accessibilityIncrement()
    control.accessibilityIncrement()
    await flushQueuedValueSync()
    check(control.accessibilityValue == 100.0.formatted(), "increment reaches max and rejects stale sync")
    control.accessibilityDecrement()
    check(control.accessibilityValue == 99.0.formatted(), "decrement updates the accessibility value")
    verify(slider, value: 99, label: "VoiceOver decrement")
    check(callbacks == ["start", "change 100.0", "complete 1.0 100.0",
                        "start", "change 99.0", "complete 2.0 99.0"],
          "VoiceOver steps emit one ordered interaction each: \(callbacks)")
    check(haptics.isEmpty, "VoiceOver steps do not fire drag haptics")

    try slider.setValue(value: 1, interactionId: 2)
    await flushQueuedValueSync()
    check(control.accessibilityValue == 1.0.formatted(), "controlled update changes the accessibility value")
    control.accessibilityDecrement()
    slider.disabled = true
    check(control.accessibilityTraits.contains(.notEnabled), "disabled slider has the disabled trait")
    check(slider.view.hitTest(edge, with: nil) == nil, "disabled slider passes through touches")
    control.accessibilityIncrement()
    check(control.accessibilityValue == 1.0.formatted() && callbacks.count == 6,
          "minimum and disabled actions leave state and callbacks unchanged")
    slider.disabled = false
    check(!control.accessibilityTraits.contains(.notEnabled), "enabled slider clears the disabled trait")

    control.onTouchBegan?(12 + 296 / 2)
    let dragValue = control.accessibilityValue
    let dragCallbacks = callbacks
    control.accessibilityIncrement()
    control.accessibilityDecrement()
    check(control.accessibilityValue == dragValue && callbacks == dragCallbacks,
          "VoiceOver actions leave an active drag alone")
    check(dragValue == 51.0.formatted(), "touch changes the accessibility value")
    control.onTouchEnded?()

    let bounded = makeSlider(width: 320, value: 0.5)
    bounded.min = 0.5
    bounded.max = 10.5
    let boundedControl = sliderControl(for: bounded)
    var values: [Double] = []
    bounded.onChange = { values.append($0) }
    boundedControl.accessibilityDecrement()
    check(boundedControl.accessibilityValue == 0.5.formatted(), "fractional minimum is retained at the bound")
    boundedControl.accessibilityIncrement()
    try bounded.setValue(value: 10.5, interactionId: 1)
    await flushQueuedValueSync()
    boundedControl.accessibilityIncrement()
    check(boundedControl.accessibilityValue == 10.5.formatted(), "fractional maximum is retained at the bound")
    boundedControl.accessibilityDecrement()
    check(values == [1, 10], "VoiceOver selects the next in-range integer: \(values)")
  }

  @MainActor
  static func main() async throws {
    for width: CGFloat in [160, 320, 600] {
      for value: Double in [0, 1, 25, 50, 75, 99, 100] {
        let slider = makeSlider(width: width, value: value)
        verify(slider, value: value, label: "width \(width), value \(value)")
        slider.view.frame.size.width = 120
        slider.view.layoutIfNeeded()
        check(sliderControl(for: slider).frame == slider.view.bounds, "slider fills resized bounds")
        verify(slider, value: value, label: "resized value \(value)")
      }
    }

    let slider = makeSlider(width: 320, value: 50)
    let control = sliderControl(for: slider)
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
    check(bubble.opacity == 0, "snap tap candidate shows the old value")
    control.onTouchEnded?()
    verify(slider, value: 25, label: "tap snap")
    check(changes.last == 25, "tap no longer snaps to segment")
    check(bubble.opacity == 1, "bubble hidden right after a tap")
    try await Task.sleep(nanoseconds: 400_000_000)
    check(bubble.opacity == 1, "bubble hidden before the tap linger ends")
    try await Task.sleep(nanoseconds: 600_000_000)
    check(bubble.opacity == 0, "bubble still visible after the tap linger")
    // A drag that follows a tap hides on release and cancels the linger.
    control.onTouchBegan?(12 + 0.3 * 296)
    control.onTouchEnded?()
    control.onTouchBegan?(12 + 0.3 * 296)
    control.onTouchMoved?(12 + 0.6 * 296)
    check(bubble.opacity == 1, "bubble hidden during drag")
    control.onTouchEnded?()
    check(bubble.opacity == 0, "bubble lingers after a drag")
    slider.showBubble = false
    slider.showBubble = true
    check(bubble.opacity == 0, "re-enabling showBubble resurrected the bubble")
    // A new snap press hides the previous tap's bubble until it lands.
    control.onTouchBegan?(12 + 0.3 * 296)
    control.onTouchEnded?()
    check(bubble.opacity == 1, "bubble hidden right after a second tap")
    control.onTouchBegan?(12 + 0.8 * 296)
    check(bubble.opacity == 0, "pending snap press kept the previous tap bubble")
    control.onTouchEnded?()
    check(bubble.opacity == 1, "bubble hidden after the follow-up tap landed")
    // A system cancel is not a tap: no linger, one completion.
    let completionsBeforeCancel = completions
    control.onTouchBegan?(12 + 0.3 * 296)
    control.onTouchCancelled?()
    check(bubble.opacity == 0, "cancelled press lingered the bubble")
    check(completions == completionsBeforeCancel + 1, "cancelled press completions changed")
    slider.snapTapToSegment = false
    control.onTouchBegan?(12 + 0.5 * 296)
    check(bubble.opacity == 1, "plain press hid the bubble")
    control.onTouchCancelled?()
    check(bubble.opacity == 0, "cancelled plain press lingered the bubble")
    slider.snapTapToSegment = true

    // The lingering bubble belongs to its tap: unrelated value updates end it.
    let owned = makeSlider(width: 320, value: 0)
    owned.snapTapToSegment = true
    var ownedId: Double = 0
    owned.onSlideComplete = { id, _ in ownedId = id }
    let ownedControl = sliderControl(for: owned)
    let ownedBubble: CALayer = layer(owned, "bubbleLayer")
    let ownedText: CATextLayer = layer(owned, "bubbleTextLayer")
    ownedControl.onTouchBegan?(12 + 0.3 * 296)
    ownedControl.onTouchEnded?()
    try owned.setValue(value: 25, interactionId: ownedId)
    await flushQueuedValueSync()
    check(ownedBubble.opacity == 1 && ownedText.string as? String == "25%",
          "accepting the tap value ended its linger")
    try owned.setValue(value: 60, interactionId: ownedId)
    await flushQueuedValueSync()
    check(ownedBubble.opacity == 0, "controlled update kept the tap bubble")
    ownedControl.onTouchBegan?(12 + 0.3 * 296)
    ownedControl.onTouchEnded?()
    check(ownedBubble.opacity == 1, "bubble hidden after a tap")
    ownedControl.accessibilityIncrement()
    check(ownedBubble.opacity == 0, "accessibility step kept the tap bubble")

    // Turning showBubble on during a drag shows the drag's bubble.
    let toggled = makeSlider(width: 320, value: 0)
    toggled.showBubble = false
    let toggledControl = sliderControl(for: toggled)
    let toggledBubble: CALayer = layer(toggled, "bubbleLayer")
    toggledControl.onTouchBegan?(12 + 0.2 * 296)
    toggledControl.onTouchMoved?(12 + 0.5 * 296)
    toggled.showBubble = true
    check(toggledBubble.opacity == 1, "showBubble enabled mid-drag stayed hidden")
    toggledControl.onTouchEnded?()
    check(toggledBubble.opacity == 0, "drag bubble lingered after enabling showBubble")
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
    let signedControl = sliderControl(for: signed)
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
      let control = sliderControl(for: sample)
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
    let tapControl = sliderControl(for: tap)
    tapControl.onTouchBegan?(12 + 0.2 * 296)
    tapControl.onTouchEnded?()
    check(tapTicks.isEmpty, "tap without movement ticked: \(tapTicks)")

    // Disabling mid-drag ends the interaction once and ignores later moves.
    var cancelChanges: [Double] = []
    var cancelCompletes = 0
    let cancel = makeSlider(width: 320, value: 0)
    cancel.onChange = { cancelChanges.append($0) }
    cancel.onSlideComplete = { _, _ in cancelCompletes += 1 }
    let cancelControl = sliderControl(for: cancel)
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
    let boundedControl = sliderControl(for: bounded)
    boundedControl.onTouchBegan?(320)
    boundedControl.onTouchMoved?(0)
    boundedControl.onTouchEnded?()
    check(boundChanges == [10, 1], "fractional bound values: \(boundChanges)")

    try await verifyDelayedValueSync()
    try await verifyAccessibilityAdjustments()

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
        sliderControl(for: sample).onTouchBegan?(12 + CGFloat(value / 100) * 296)
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
