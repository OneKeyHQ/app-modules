import React
import UIKit

private enum ToasterMetrics {
  /// The first toast sits this far under the top safe-area inset.
  static let topOffset: CGFloat = 16
  static let gutter: CGFloat = 10
  static let visibleToasts: Int = 3
  static let stackScaleStep: CGFloat = 0.05
  static let enterFadeDuration: TimeInterval = 0.22
  static let exitDuration: TimeInterval = 0.2
  static let autoExitDuration: TimeInterval = 0.19
  static let obstructionDuration: TimeInterval = 0.22
  /// Keep leaving cards mounted long enough for the exit transition and close callback.
  static let removeDelay: TimeInterval = 0.22
  /// Extra travel above the top edge for the resting-out position on enter.
  static let enterOvershoot: CGFloat = 32
  static let hiddenScale: CGFloat = 0.96
  static let swipeExitScale: CGFloat = 0.98
  static let swipeExitMargin: CGFloat = 48
  static let autoExitOffset: CGFloat = 16
  static let autoExitScale: CGFloat = 0.95
  static let collapsedBackExitOffset: CGFloat = 12
  static let collapsedLeaveScale: CGFloat = 0.9
  static let fallbackHeight: CGFloat = 56
  static let swipeDistance: CGFloat = 64
  static let swipeFlickDistance: CGFloat = 20
  static let swipeFlickVelocity: CGFloat = 500
  static let swipeOpacityRange: CGFloat = 260
  static let swipeMinOpacity: CGFloat = 0.35
  /// Pull away from the top edge that expands a collapsed stack.
  static let expandPullDistance: CGFloat = 40
  /// Push toward the top edge that collapses an expanded stack.
  static let collapsePushDistance: CGFloat = 50
  static let rubberDimension: CGFloat = 30
  static let expandRubberDimension: CGFloat = 70
  /// Extra pull per depth while expanding, so back cards fan out under the finger.
  static let expandSpreadStep: CGFloat = 0.35
  static let fallbackMargin: CGFloat = 64
  static let fallbackMaxPageWidth: CGFloat = 640
  static let minSideInset: CGFloat = 16
}

/// Spring timing shared with Web and Android: mass 1, stiffness 460, damping 36.
private func toastSpringAnimator() -> UIViewPropertyAnimator {
  let timing = UISpringTimingParameters(
    mass: 1, stiffness: 460, damping: 36, initialVelocity: .zero
  )
  return UIViewPropertyAnimator(duration: 0, timingParameters: timing)
}

/// Brisk ease-out curve for interactive swipe exit and obstruction shifts.
private func toastExitAnimator(duration: TimeInterval = ToasterMetrics.exitDuration) -> UIViewPropertyAnimator {
  UIViewPropertyAnimator(
    duration: duration,
    timingParameters: UICubicTimingParameters(
      controlPoint1: CGPoint(x: 0.32, y: 0.72), controlPoint2: CGPoint(x: 0, y: 1)
    )
  )
}

/// Gentle ease-out curve for non-interactive timeout / programmatic exit.
private func toastAutoExitAnimator() -> UIViewPropertyAnimator {
  UIViewPropertyAnimator(
    duration: ToasterMetrics.autoExitDuration,
    timingParameters: UICubicTimingParameters(
      controlPoint1: CGPoint(x: 0.2, y: 0), controlPoint2: CGPoint(x: 0, y: 1)
    )
  )
}

/// Damped displacement past a limit, as UIScrollView's edge bounce.
private func rubberBand(_ offset: CGFloat, dimension: CGFloat) -> CGFloat {
  let magnitude = dimension * (1 - 1 / (abs(offset) * 0.55 / dimension + 1))
  return offset < 0 ? -magnitude : magnitude
}

/// Lets touches that miss every card fall through to the windows below.
private final class ToastPassthroughView: UIView {
  var onOutsideTouch: (() -> Void)?
  var onGeometryChanged: (() -> Void)?
  private var lastLayoutSize: CGSize = .zero

  override func didMoveToSuperview() {
    super.didMoveToSuperview()
    lastLayoutSize = bounds.size
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    if lastLayoutSize != bounds.size {
      lastLayoutSize = bounds.size
      onGeometryChanged?()
    }
  }

  override func safeAreaInsetsDidChange() {
    super.safeAreaInsetsDidChange()
    onGeometryChanged?()
  }

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let hit = super.hitTest(point, with: event)
    if hit === self {
      if event?.type == .touches {
        onOutsideTouch?()
      }
      return nil
    }
    return hit
  }
}

private final class ToastEntry {
  let id: String
  var item: NativeOverlayToastItem
  let card: NativeOverlayToastCardView
  /// Parent of the card: carries the stack position, the card the drag.
  let slot: UIView = ToastPassthroughView()
  var leaving = false
  var collapsedBackExit = false
  var reason = "programmatic"
  /// Seconds of lifetime left, counted while not paused.
  var remaining: TimeInterval?
  var startedAt: Date?
  var timer: Timer?
  var removal: Timer?
  var pan: UIPanGestureRecognizer?
  var dragStart: CGFloat = 0
  var swipeExitSign: CGFloat = 1
  /// Left with its stack past the top edge rather than off to a side.
  var swipeExitUp = false
  /// The body was faded behind the front card when the dismissal began.
  var exitBodyHidden = false
  var lastTargetY: CGFloat?
  var lastTargetScale: CGFloat = 1
  var dragged = false
  var isPressed = false
  var entered = false
  var lastActiveIndex = 0

  init(item: NativeOverlayToastItem, cardColor: UIColor, cardBorderColor: UIColor?) {
    id = item.spec.id
    self.item = item
    card = NativeOverlayToastCardView(spec: item.spec, cardColor: cardColor, cardBorderColor: cardBorderColor)
    slot.addSubview(card)
  }
}

private enum ToastDragMode {
  /// One card, sideways.
  case horizontal
  /// The collapsed stack toward the top edge, dismissing it.
  case stack
  /// The collapsed stack away from the top edge, expanding it.
  case expand
  /// The expanded stack toward the top edge, collapsing it.
  case collapse
  /// Nothing to do in that direction: damped, then settles.
  case rubber
}

private struct ToastDrag {
  let mode: ToastDragMode
  let owner: ToastEntry
  let participants: [ToastEntry]
}

/// Draws the message toasts in the `toast` overlay level: stacking, timers,
/// swipe and motion all live here, on the main thread.
final class NativeOverlayToastPresenter: NSObject, NativeOverlayWindowOwner, UIGestureRecognizerDelegate {
  var onEvent: ((_ id: String, _ generation: Int, _ type: String, _ value: String) -> Void)?

  private weak var hostView: UIView?
  private var window: UIWindow?
  /// Rides under whatever hangs from the top of the window.
  private let stage = ToastPassthroughView()
  private let cards = ToastPassthroughView()
  private var entries: [ToastEntry] = [] // newest first
  private var config = NativeOverlayToasterConfig()
  private var expanded = false
  private var obstructionShift: CGFloat = 0
  private var drag: ToastDrag?
  /// Toasts first shown during a drag, oldest first; they enter when it ends.
  private var deferredAdds: [NativeOverlayToastItem] = []

  private var activeEntries: [ToastEntry] {
    entries.filter { !$0.leaving }
  }

  init(hostView: UIView) {
    self.hostView = hostView
    super.init()
    stage.addSubview(cards)
    stage.onGeometryChanged = { [weak self] in
      guard let self, self.entries.contains(where: { $0.slot.superview != nil }) else { return }
      self.layout(animated: false)
    }
    NotificationCenter.default.addObserver(
      self, selector: #selector(layoutChanged), name: UIAccessibility.reduceMotionStatusDidChangeNotification, object: nil
    )
    stage.onOutsideTouch = { [weak self] in
      DispatchQueue.main.async {
        guard let self, self.expanded else { return }
        self.expanded = false
        self.layout(animated: true)
        self.syncTimers()
      }
    }
    NotificationCenter.default.addObserver(
      self, selector: #selector(layoutChanged), name: NativeOverlayKeyboard.didChange, object: nil
    )
    NotificationCenter.default.addObserver(
      self, selector: #selector(layoutChanged),
      name: UIDevice.orientationDidChangeNotification, object: nil
    )
    _ = NativeOverlayKeyboard.shared
  }

  deinit {
    NotificationCenter.default.removeObserver(self)
  }

  private var reduceMotion: Bool {
    config.respectReducedMotion && UIAccessibility.isReduceMotionEnabled
  }

  // MARK: Sync

  func sync(items: [NativeOverlayToastItem], config next: NativeOverlayToasterConfig) {
    let configChanged =
      next.contentWidth != config.contentWidth || next.contentMaxHeight != config.contentMaxHeight
      || next.cardColor != config.cardColor || next.cardBorderColor != config.cardBorderColor
    config = next
    let present = Set(items.map(\.spec.id))
    deferredAdds.removeAll { !present.contains($0.spec.id) }
    // Cards the list no longer knows leave without ceremony.
    for entry in entries where !present.contains(entry.id) {
      remove(entry, report: false)
    }
    // Oldest first, so each new card joins in front of the previous one.
    for item in items.reversed() {
      var entry = entries.first { $0.id == item.spec.id }
      if let existing = entry, existing.leaving, item.visible {
        // Shown again while the previous card was leaving.
        remove(existing, report: false)
        entry = nil
      }
      guard let entry else {
        if item.visible && drag != nil {
          deferAdd(item)
        } else if item.visible {
          add(item)
        } else {
          deferredAdds.removeAll { $0.spec.id == item.spec.id }
          onEvent?(item.spec.id, item.generation, "close", "programmatic")
        }
        continue
      }
      if entry.item.revision != item.revision || configChanged {
        let settleStatus: Bool
        if case let .status(status)? = item.spec.icon {
          settleStatus = item.visible && entry.entered && !reduceMotion
            && entry.item.generation != item.generation
            && status.status != "loading"
        } else {
          settleStatus = false
        }
        entry.card.apply(
          spec: item.spec,
          cardColor: config.cardColor,
          cardBorderColor: config.cardBorderColor,
          settleStatus: settleStatus
        )
      }
      if entry.item.generation != item.generation {
        // Shown again: the toast starts its lifetime over.
        entry.remaining = lifetime(of: item.spec)
        entry.startedAt = nil
        entry.timer?.invalidate()
        entry.timer = nil
      }
      entry.item = item
      if !item.visible {
        dismiss(entry, reason: "programmatic", report: false)
      }
    }
    layout(animated: true)
    syncTimers()
  }

  func invalidate() {
    entries.forEach { entry in
      entry.timer?.invalidate()
      entry.removal?.invalidate()
      entry.card.cancelStatusMotion()
      entry.slot.removeFromSuperview()
    }
    entries = []
    deferredAdds = []
    drag = nil
    expanded = false
    releaseWindow()
  }

  private func deferAdd(_ item: NativeOverlayToastItem) {
    if let index = deferredAdds.firstIndex(where: { $0.spec.id == item.spec.id }) {
      deferredAdds[index] = item
    } else {
      deferredAdds.append(item)
    }
  }

  /// Draws the toasts that arrived while a card was being dragged.
  private func flushDeferredAdds() {
    guard drag == nil, !deferredAdds.isEmpty else { return }
    let pending = deferredAdds
    deferredAdds = []
    for item in pending where !entries.contains(where: { $0.id == item.spec.id }) {
      add(item)
    }
    layout(animated: true)
    syncTimers()
  }

  private func lifetime(of spec: NativeOverlayToastSpec) -> TimeInterval? {
    spec.duration >= 0 ? spec.duration / 1000 : nil
  }

  // MARK: Window

  func overlaySceneDidDisconnect(window: UIWindow) {
    if self.window === window {
      self.window = nil
      stage.removeFromSuperview()
    }
  }

  private func ensureWindow() -> UIView? {
    if let root = window?.rootViewController?.view {
      return root
    }
    guard let appWindow = hostView?.window else { return nil }
    guard let overlayWindow = NativeOverlayWindowManager.shared.window(
      for: self, level: .toast, stackOrder: 0, blocking: false, appWindow: appWindow
    ), let root = overlayWindow.rootViewController?.view else { return nil }
    window = overlayWindow
    stage.frame = root.bounds
    stage.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    cards.frame = stage.bounds
    cards.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    root.addSubview(stage)
    return root
  }

  private func releaseWindow() {
    guard let window else { return }
    stage.removeFromSuperview()
    cards.layer.removeAllAnimations()
    cards.transform = .identity
    obstructionShift = 0
    NativeOverlayWindowManager.shared.entryRemoved(from: window)
    self.window = nil
  }

  /// The host joined a window: draw the toasts that were waiting for one.
  func hostDidMoveToWindow() {
    guard hostView?.window != nil, !entries.isEmpty else { return }
    entries.filter { $0.slot.superview == nil }.reversed().forEach(attach)
    layout(animated: true)
  }

  // MARK: Cards

  private func add(_ item: NativeOverlayToastItem) {
    let entry = ToastEntry(item: item, cardColor: config.cardColor, cardBorderColor: config.cardBorderColor)
    entry.remaining = lifetime(of: item.spec)
    entry.card.onAction = { [weak self, weak entry] actionId in
      guard let entry, !entry.leaving else { return }
      self?.onEvent?(entry.id, entry.item.generation, "action", actionId)
    }
    entry.card.onLink = { [weak self, weak entry] link in
      guard let entry, !entry.leaving else { return }
      self?.onEvent?(entry.id, entry.item.generation, "link", link)
    }
    entry.card.onPress = { [weak self, weak entry] in
      guard let entry else { return }
      self?.activate(entry)
    }
    let pan = UIPanGestureRecognizer(target: self, action: #selector(handlePan(_:)))
    pan.maximumNumberOfTouches = 1
    pan.delegate = self
    entry.pan = pan
    entry.card.addGestureRecognizer(pan)
    // Press and hold pauses every toast's lifetime.
    let press = UILongPressGestureRecognizer(target: self, action: #selector(handlePress(_:)))
    press.minimumPressDuration = 0
    press.cancelsTouchesInView = false
    press.delegate = self
    entry.card.addGestureRecognizer(press)
    let tap = UITapGestureRecognizer(target: self, action: #selector(handleTap(_:)))
    tap.delegate = self
    entry.card.addGestureRecognizer(tap)
    entries.insert(entry, at: 0)
    attach(entry)
  }

  private func attach(_ entry: ToastEntry) {
    guard ensureWindow() != nil else { return }
    cards.addSubview(entry.slot)
    entry.slot.alpha = 0
    UIAccessibility.post(notification: .announcement, argument: entry.card.announcement)
  }

  private func entry(for gesture: UIGestureRecognizer) -> ToastEntry? {
    entries.first { $0.card === gesture.view }
  }

  private func dismiss(_ entry: ToastEntry, reason: String, report: Bool) {
    dismiss([entry], reason: reason, report: report)
  }

  /// Starts every exit before laying out once, so a group leaves together
  /// and no survivor is promoted between two of its cards.
  private func dismiss(_ group: [ToastEntry], reason: String, report: Bool) {
    let leaving = group.filter { !$0.leaving }
    guard !leaving.isEmpty else { return }
    for entry in leaving {
      abandonDrag(entry)
      entry.collapsedBackExit = !expanded && entry.lastActiveIndex > 0 && reason != "swipe"
      entry.exitBodyHidden = !expanded && entry.lastActiveIndex > 0
      entry.leaving = true
      entry.card.cancelStatusMotion()
      entry.reason = reason
      entry.timer?.invalidate()
      entry.timer = nil
      if report {
        onEvent?(entry.id, entry.item.generation, "dismiss", reason)
      }
    }
    if reduceMotion {
      leaving.forEach { remove($0, report: true) }
      layout(animated: false)
      syncTimers()
      return
    }
    for entry in leaving {
      entry.removal = Timer.scheduledTimer(
        withTimeInterval: ToasterMetrics.removeDelay, repeats: false
      ) { [weak self, weak entry] _ in
        guard let self, let entry else { return }
        self.remove(entry, report: true)
        self.layout(animated: true)
        self.syncTimers()
      }
    }
    if activeEntries.count <= 1 {
      expanded = false
    }
    layout(animated: true)
    syncTimers()
  }

  private func remove(_ entry: ToastEntry, report: Bool) {
    abandonDrag(entry)
    entry.timer?.invalidate()
    entry.removal?.invalidate()
    entry.card.cancelStatusMotion()
    entry.slot.removeFromSuperview()
    entries.removeAll { $0 === entry }
    if activeEntries.count <= 1 {
      expanded = false
    }
    if report {
      onEvent?(entry.id, entry.item.generation, "close", entry.reason)
    }
    if entries.isEmpty {
      expanded = false
      releaseWindow()
    }
  }

  // MARK: Layout

  @objc private func layoutChanged() {
    layout(animated: true)
  }

  private func contentWidth(availableWidth: CGFloat) -> CGFloat {
    let requested = config.contentWidth.flatMap { $0.isFinite ? $0 : nil }
      ?? availableWidth - ToasterMetrics.fallbackMargin
    let maximum = ToasterMetrics.fallbackMaxPageWidth - ToasterMetrics.fallbackMargin
    let available = availableWidth - 2 * (ToasterMetrics.minSideInset + NativeOverlayToastCardView.horizontalPadding)
    return max(0, min(requested, maximum, available))
  }

  /// How far the whole stack rides down to rest under the top obstruction.
  private func computeObstructionShift(in root: UIView) -> CGFloat {
    guard config.topObstruction > 0 else { return 0 }
    let toastTop = root.safeAreaInsets.top + ToasterMetrics.topOffset
    let keyboardTop = NativeOverlayKeyboard.shared.top(in: root)
    let usableBottom = keyboardTop ?? (root.bounds.height - root.safeAreaInsets.bottom)
    let top = min(
      config.topObstruction + config.topObstructionGap,
      usableBottom - config.topObstructionRoom
    )
    return max(0, top - toastTop)
  }

  private func layout(animated: Bool) {
    guard let root = window?.rootViewController?.view, !entries.isEmpty else { return }
    let safe = root.safeAreaInsets
    let availableWidth = root.bounds.width - safe.left - safe.right
    let width = contentWidth(availableWidth: availableWidth)
    let top = safe.top + ToasterMetrics.topOffset
    let animate = animated && !reduceMotion

    let shift = computeObstructionShift(in: root)
    if shift != obstructionShift {
      obstructionShift = shift
      let apply = { self.cards.transform = CGAffineTransform(translationX: 0, y: shift) }
      if animate {
        let animator = toastExitAnimator(duration: ToasterMetrics.obstructionDuration)
        animator.addAnimations(apply)
        animator.startAnimation()
      } else {
        apply()
      }
    }

    let active = activeEntries
    if active.count <= 1 {
      expanded = false
    }
    let isCollapsed = !expanded && active.count > 1

    // Measure every card's natural size first so collapsed back cards know frontHeight.
    for entry in entries {
      entry.card.layoutCard(
        contentWidth: width, contentMaxHeight: config.contentMaxHeight, applyBounds: false
      )
    }
    let frontHeight = active.first?.card.naturalSize.height ?? ToasterMetrics.fallbackHeight

    // Keep newest card at the highest z-order.
    for entry in entries.reversed() {
      cards.bringSubviewToFront(entry.slot)
    }

    var activeIndex = 0
    var expandedOffset: CGFloat = 0
    for entry in entries {
      let naturalSize = entry.card.naturalSize
      let x = safe.left + (availableWidth - naturalSize.width) / 2
      let displayHeight: CGFloat
      let targetY: CGFloat
      let targetScale: CGFloat
      let targetAlpha: CGFloat
      let hideBody: Bool
      let interactive: Bool

      let swipeExit = entry.leaving && entry.reason == "swipe"
      var targetCardTransform: CGAffineTransform?

      if entry.leaving {
        displayHeight = entry.card.bounds.height > 0 ? entry.card.bounds.height : naturalSize.height
        let collapsedBackExit = entry.collapsedBackExit
        let baseY = entry.lastTargetY ?? top
        if swipeExit && entry.swipeExitUp {
          // Leaves with its stack: depth scale kept, travel measured in the
          // slot's scaled space so every card clears the top edge.
          targetY = baseY
          targetScale = entry.lastTargetScale
          let travel = baseY + obstructionShift + displayHeight + ToasterMetrics.swipeExitMargin
          targetCardTransform = CGAffineTransform(
            translationX: 0, y: -travel / max(targetScale, 0.01)
          )
        } else if swipeExit {
          targetY = baseY
          targetScale = ToasterMetrics.swipeExitScale
          let exitDistance = max(
            naturalSize.width + ToasterMetrics.swipeExitMargin,
            root.bounds.width * 0.5 + naturalSize.width * 0.5
          )
          targetCardTransform = CGAffineTransform(
            translationX: entry.swipeExitSign * exitDistance, y: 0
          )
        } else if collapsedBackExit {
          targetY = baseY - ToasterMetrics.collapsedBackExitOffset
          targetScale = ToasterMetrics.collapsedLeaveScale
        } else {
          targetY = baseY - ToasterMetrics.autoExitOffset
          targetScale = ToasterMetrics.autoExitScale
        }
        targetAlpha = 0
        hideBody = collapsedBackExit || (swipeExit && entry.swipeExitUp && entry.exitBodyHidden)
        interactive = false
      } else {
        let index = activeIndex
        entry.lastActiveIndex = index
        activeIndex += 1
        let isVisible = index < ToasterMetrics.visibleToasts
        if isCollapsed {
          displayHeight = index == 0 ? naturalSize.height : frontHeight
          targetY = top + CGFloat(min(index, ToasterMetrics.visibleToasts)) * ToasterMetrics.gutter
          targetScale = max(0.85, 1 - CGFloat(min(index, ToasterMetrics.visibleToasts)) * ToasterMetrics.stackScaleStep)
          hideBody = index > 0
        } else {
          displayHeight = naturalSize.height
          targetY = top + expandedOffset
          targetScale = 1
          hideBody = false
        }
        entry.lastTargetY = targetY
        entry.lastTargetScale = targetScale
        if isVisible {
          expandedOffset += naturalSize.height + ToasterMetrics.gutter
        }
        targetAlpha = isVisible ? 1 : 0
        interactive = isVisible
      }

      let displaySize = CGSize(width: naturalSize.width, height: displayHeight)
      let frame = CGRect(x: x, y: targetY, width: displaySize.width, height: displaySize.height)
      let targetTransform = targetScale == 1
        ? CGAffineTransform.identity
        : CGAffineTransform(scaleX: targetScale, y: targetScale)
      let wasEntered = entry.entered

      if !wasEntered {
        entry.entered = true
        let hidden = -(displayHeight > 0 ? displayHeight : ToasterMetrics.fallbackHeight)
          - safe.top - ToasterMetrics.enterOvershoot
        entry.card.applyDisplayHeight(displayHeight)
        entry.card.setBodyHidden(hideBody, animated: false)
        entry.slot.transform = .identity
        entry.slot.bounds.size = displaySize
        entry.card.center = CGPoint(x: displaySize.width / 2, y: displaySize.height / 2)
        entry.slot.center = CGPoint(x: x + displaySize.width / 2, y: top + hidden + displaySize.height / 2)
        entry.slot.transform = CGAffineTransform(
          scaleX: ToasterMetrics.hiddenScale, y: ToasterMetrics.hiddenScale
        )
        if animate && !entry.leaving { entry.card.animateMessageStatusEntrance() }
      } else {
        entry.card.setBodyHidden(hideBody, animated: animate)
      }

      entry.slot.isUserInteractionEnabled = interactive
      entry.slot.accessibilityElementsHidden = !interactive
      entry.card.canExpandStack = isCollapsed && entry === active.first
      let canSwipe = interactive && !hideBody
      if entry.pan?.isEnabled != canSwipe {
        if !canSwipe && entry.dragged && !entry.leaving {
          settle(entry.card)
          entry.dragged = false
        }
        entry.pan?.isEnabled = canSwipe
      }
      entry.card.setReducedMotion(reduceMotion)
      if !entry.leaving && !reduceMotion { entry.card.animateMessageStatusRing() }

      let applyGeometry = {
        entry.slot.center = CGPoint(x: frame.midX, y: frame.midY)
        entry.slot.bounds.size = displaySize
        entry.slot.transform = targetTransform
        entry.card.applyDisplayHeight(displayHeight)
        entry.card.center = CGPoint(x: displaySize.width / 2, y: displaySize.height / 2)
        if let targetCardTransform {
          entry.card.transform = targetCardTransform
        }
      }

      if animate {
        if entry.leaving {
          let animator = swipeExit ? toastExitAnimator() : toastAutoExitAnimator()
          animator.addAnimations {
            applyGeometry()
            entry.slot.alpha = targetAlpha
          }
          animator.startAnimation()
        } else {
          let spring = toastSpringAnimator()
          spring.addAnimations(applyGeometry)
          spring.startAnimation()
          if entry.slot.alpha != targetAlpha {
            let fade = UIViewPropertyAnimator(
              duration: ToasterMetrics.enterFadeDuration, curve: .easeOut
            )
            fade.addAnimations { entry.slot.alpha = targetAlpha }
            fade.startAnimation()
          }
        }
      } else {
        applyGeometry()
        entry.slot.alpha = targetAlpha
      }
    }
  }

  // MARK: Timers

  private func syncTimers() {
    let paused = expanded || entries.contains { !$0.leaving && $0.isPressed }
    for entry in entries where !entry.leaving {
      guard let remaining = entry.remaining else { continue }
      if paused {
        if let startedAt = entry.startedAt {
          entry.remaining = max(0, remaining - Date().timeIntervalSince(startedAt))
          entry.startedAt = nil
        }
        entry.timer?.invalidate()
        entry.timer = nil
        continue
      }
      guard entry.timer == nil else { continue }
      entry.startedAt = Date()
      entry.timer = Timer.scheduledTimer(
        withTimeInterval: max(remaining, 0), repeats: false
      ) { [weak self, weak entry] _ in
        guard let self, let entry else { return }
        entry.timer = nil
        self.dismiss(entry, reason: "timeout", report: true)
      }
    }
  }

  // MARK: Gestures

  func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
    // Both axes start a drag; `handlePan` picks the mode when it begins.
    true
  }

  func gestureRecognizer(
    _ gestureRecognizer: UIGestureRecognizer,
    shouldRecognizeSimultaneouslyWith other: UIGestureRecognizer
  ) -> Bool {
    gestureRecognizer is UILongPressGestureRecognizer || other is UILongPressGestureRecognizer
  }

  func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
    if gestureRecognizer is UITapGestureRecognizer {
      guard let entry = entry(for: gestureRecognizer) else { return false }
      let canExpand = !expanded && activeEntries.count > 1
      if !canExpand && !entry.item.spec.pressable { return false }
      if touch.view is UIControl || touch.view is ToastTextLabel { return false }
    }
    return true
  }

  @objc private func handlePress(_ gesture: UILongPressGestureRecognizer) {
    guard let entry = entry(for: gesture) else { return }
    switch gesture.state {
    case .began:
      entry.isPressed = true
    case .ended, .cancelled, .failed:
      entry.isPressed = false
    default:
      return
    }
    syncTimers()
  }

  @objc private func handleTap(_ gesture: UITapGestureRecognizer) {
    guard let entry = entry(for: gesture), !entry.dragged else { return }
    activate(entry)
  }

  private func activate(_ entry: ToastEntry) {
    guard !entry.leaving else { return }
    if !expanded && activeEntries.count > 1 {
      expanded = true
      layout(animated: true)
      syncTimers()
      return
    }
    guard entry.item.spec.pressable else { return }
    onEvent?(entry.id, entry.item.generation, "press", "")
  }

  @objc private func handlePan(_ gesture: UIPanGestureRecognizer) {
    guard let entry = entry(for: gesture) else { return }
    let translation = gesture.translation(in: stage)
    let velocity = gesture.velocity(in: stage)
    switch gesture.state {
    case .began:
      beginDrag(entry, translation: translation, velocity: velocity)
    case .changed:
      guard let drag, drag.owner === entry else { return }
      applyDrag(drag, translation: translation)
    case .ended:
      guard let drag, drag.owner === entry else { return }
      self.drag = nil
      finishDrag(drag, translation: translation, velocity: velocity)
      DispatchQueue.main.async { entry.dragged = false }
      flushDeferredAdds()
    case .cancelled, .failed:
      guard let drag, drag.owner === entry else { return }
      self.drag = nil
      drag.participants.filter { !$0.leaving }.forEach { settle($0.card) }
      entry.dragged = false
      flushDeferredAdds()
    default:
      break
    }
  }

  private func beginDrag(_ entry: ToastEntry, translation: CGPoint, velocity: CGPoint) {
    guard !entry.leaving, drag == nil else { return }
    let active = activeEntries
    if !expanded && active.count > 1 && entry !== active.first { return }
    let horizontal = translation == .zero
      ? abs(velocity.x) > abs(velocity.y)
      : abs(translation.x) > abs(translation.y)
    let towardTop = (translation.y != 0 ? translation.y : velocity.y) < 0
    let mode: ToastDragMode
    let participants: [ToastEntry]
    if horizontal {
      mode = .horizontal
      participants = [entry]
    } else if towardTop {
      mode = expanded ? .collapse : .stack
      participants = active
    } else if !expanded && active.count > 1 {
      mode = .expand
      participants = active
    } else {
      mode = .rubber
      participants = [entry]
    }
    participants.forEach { $0.card.layer.removeAllAnimations() }
    entry.dragStart = mode == .horizontal ? entry.card.transform.tx : 0
    entry.dragged = true
    let next = ToastDrag(mode: mode, owner: entry, participants: participants)
    drag = next
    applyDrag(next, translation: translation)
  }

  private func applyDrag(_ drag: ToastDrag, translation: CGPoint) {
    let dy = translation.y
    switch drag.mode {
    case .horizontal:
      let card = drag.owner.card
      let dragX = drag.owner.dragStart + translation.x
      card.transform = CGAffineTransform(translationX: dragX, y: 0)
      card.alpha = max(
        ToasterMetrics.swipeMinOpacity,
        1 - abs(dragX) / ToasterMetrics.swipeOpacityRange
      )
    case .stack:
      let offset = dy < 0 ? dy : rubberBand(dy, dimension: ToasterMetrics.rubberDimension)
      drag.participants.forEach { setDragOffset($0, offset) }
    case .expand:
      let offset = dy > 0
        ? rubberBand(dy, dimension: ToasterMetrics.expandRubberDimension)
        : rubberBand(dy, dimension: ToasterMetrics.rubberDimension)
      for (index, participant) in drag.participants.enumerated() {
        let spread = 1 + CGFloat(min(index, 2)) * ToasterMetrics.expandSpreadStep
        setDragOffset(participant, offset * spread)
      }
    case .collapse:
      let offset = dy < 0 ? dy * 0.5 : rubberBand(dy, dimension: ToasterMetrics.rubberDimension)
      drag.participants.forEach { setDragOffset($0, offset) }
    case .rubber:
      setDragOffset(drag.owner, rubberBand(dy, dimension: ToasterMetrics.rubberDimension))
    }
  }

  /// Offsets a card inside its slot; the slot carries the depth scale.
  private func setDragOffset(_ entry: ToastEntry, _ offset: CGFloat) {
    guard !entry.leaving else { return }
    entry.card.transform = CGAffineTransform(
      translationX: 0, y: offset / max(entry.lastTargetScale, 0.01)
    )
  }

  /// Ends a drag whose owner left or went away before its gesture finished,
  /// settling the other cards it moved and letting deferred toasts in.
  private func abandonDrag(_ owner: ToastEntry) {
    guard let current = drag, current.owner === owner else { return }
    drag = nil
    current.participants.filter { $0 !== owner && !$0.leaving }.forEach { settle($0.card) }
    DispatchQueue.main.async { [weak self] in self?.flushDeferredAdds() }
  }

  private func passesSwipe(distance: CGFloat, speed: CGFloat) -> Bool {
    distance >= ToasterMetrics.swipeDistance
      || (distance >= ToasterMetrics.swipeFlickDistance
        && speed >= ToasterMetrics.swipeFlickVelocity)
  }

  private func finishDrag(_ drag: ToastDrag, translation: CGPoint, velocity: CGPoint) {
    let remaining = drag.participants.filter { !$0.leaving }
    switch drag.mode {
    case .horizontal:
      let entry = drag.owner
      guard !entry.leaving else { return }
      let totalX = entry.dragStart + translation.x
      let speed = velocity.x * (totalX >= 0 ? 1 : -1)
      if passesSwipe(distance: abs(totalX), speed: speed) {
        entry.swipeExitSign = totalX >= 0 ? 1 : -1
        entry.swipeExitUp = false
        dismiss(entry, reason: "swipe", report: true)
      } else {
        settle(entry.card)
      }
    case .stack:
      if passesSwipe(distance: -translation.y, speed: -velocity.y) {
        remaining.forEach { $0.swipeExitUp = true }
        dismiss(remaining, reason: "swipe", report: true)
      } else {
        remaining.forEach { settle($0.card) }
      }
    case .expand:
      remaining.forEach { settle($0.card) }
      let pulled = translation.y >= ToasterMetrics.expandPullDistance
        || velocity.y >= ToasterMetrics.swipeFlickVelocity
      if pulled && !expanded && activeEntries.count > 1 {
        expanded = true
        layout(animated: true)
        syncTimers()
      }
    case .collapse:
      remaining.forEach { settle($0.card) }
      let pushed = -translation.y >= ToasterMetrics.collapsePushDistance
        || -velocity.y >= ToasterMetrics.swipeFlickVelocity
      if pushed && expanded {
        expanded = false
        layout(animated: true)
        syncTimers()
      }
    case .rubber:
      remaining.forEach { settle($0.card) }
    }
  }

  private func settle(_ card: UIView) {
    guard !reduceMotion else {
      card.transform = .identity
      card.alpha = 1
      return
    }
    let animator = toastSpringAnimator()
    animator.addAnimations {
      card.transform = .identity
      card.alpha = 1
    }
    animator.startAnimation()
  }
}

/// The zero-size host mounted by `OverlayToastHost`. It only carries the toast
/// list and settings across the bridge; the cards are drawn in the `toast`
/// overlay window.
@objc(NativeOverlayToasterView)
public final class NativeOverlayToasterView: UIView {
  @objc public var onToastEvent: RCTDirectEventBlock?

  private lazy var presenter: NativeOverlayToastPresenter = {
    let presenter = NativeOverlayToastPresenter(hostView: self)
    presenter.onEvent = { [weak self] id, generation, type, value in
      self?.onToastEvent?([
        "toastId": id, "generation": generation, "type": type, "value": value,
      ])
    }
    return presenter
  }()

  private var toasts = ""
  private var config = ""

  override public init(frame: CGRect) {
    super.init(frame: frame)
    isUserInteractionEnabled = false
    isHidden = true
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  @objc public func update(toasts nextToasts: String, config nextConfig: String) {
    guard nextToasts != toasts || nextConfig != config else { return }
    toasts = nextToasts
    config = nextConfig
    presenter.sync(
      items: NativeOverlayToastItem.parse(nextToasts),
      config: NativeOverlayToasterConfig(jsonString: nextConfig)
    )
  }

  override public func didMoveToWindow() {
    super.didMoveToWindow()
    presenter.hostDidMoveToWindow()
  }

  @objc public func invalidate() {
    presenter.invalidate()
  }
}
