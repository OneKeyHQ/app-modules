import React
import UIKit

private enum ToasterMetrics {
  /// The first toast sits this far under the top safe-area inset.
  static let topOffset: CGFloat = 16
  static let gutter: CGFloat = 10
  static let exitDuration: TimeInterval = 0.2
  static let obstructionDuration: TimeInterval = 0.22
  /// Keep leaving cards mounted long enough for the exit transition and close callback.
  static let removeDelay: TimeInterval = 0.26
  /// Extra travel above the top edge for the resting-out position.
  static let enterOvershoot: CGFloat = 32
  static let hiddenScale: CGFloat = 0.96
  static let fallbackHeight: CGFloat = 56
  static let swipeDistance: CGFloat = 32
  static let swipeFlickDistance: CGFloat = 12
  static let swipeFlickVelocity: CGFloat = 600
  /// Dragging against the dismiss direction moves the card a quarter as far.
  static let swipeResistance: CGFloat = 4
  static let fallbackMargin: CGFloat = 64
  static let fallbackMaxPageWidth: CGFloat = 640
}

/// Spring timing shared with Web and Android: mass 1, stiffness 460, damping 36.
private func toastSpringAnimator() -> UIViewPropertyAnimator {
  let timing = UISpringTimingParameters(
    mass: 1, stiffness: 460, damping: 36, initialVelocity: .zero
  )
  return UIViewPropertyAnimator(duration: 0, timingParameters: timing)
}

/// Brisk ease-out curve for card exit and obstruction shifts.
private func toastExitAnimator(duration: TimeInterval = ToasterMetrics.exitDuration) -> UIViewPropertyAnimator {
  UIViewPropertyAnimator(
    duration: duration,
    timingParameters: UICubicTimingParameters(
      controlPoint1: CGPoint(x: 0.32, y: 0.72), controlPoint2: CGPoint(x: 0, y: 1)
    )
  )
}

/// Lets touches that miss every card fall through to the windows below.
private final class ToastPassthroughView: UIView {
  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let hit = super.hitTest(point, with: event)
    return hit === self ? nil : hit
  }
}

private final class ToastEntry {
  let id: String
  var item: NativeOverlayToastItem
  let card: NativeOverlayToastCardView
  /// Parent of the card: carries the stack position, the card the drag.
  let slot: UIView = ToastPassthroughView()
  var leaving = false
  var reason = "programmatic"
  /// Seconds of lifetime left, counted while not paused.
  var remaining: TimeInterval?
  var startedAt: Date?
  var timer: Timer?
  var removal: Timer?
  var dragStart: CGFloat = 0
  var dragged = false
  var entered = false

  init(item: NativeOverlayToastItem, cardColor: UIColor, cardBorderColor: UIColor?) {
    id = item.spec.id
    self.item = item
    card = NativeOverlayToastCardView(spec: item.spec, cardColor: cardColor, cardBorderColor: cardBorderColor)
    slot.addSubview(card)
  }
}

/// Draws the message toasts in the `toast` overlay level: stacking, timers,
/// swipe and motion all live here, on the main thread.
final class NativeOverlayToastPresenter: NSObject, NativeOverlayWindowOwner, UIGestureRecognizerDelegate {
  var onEvent: ((_ id: String, _ generation: Int, _ type: String, _ value: String) -> Void)?

  private weak var hostView: UIView?
  private var window: UIWindow?
  /// Rides under whatever hangs from the top of the window.
  private let stage: UIView = ToastPassthroughView()
  private var entries: [ToastEntry] = [] // newest first
  private var config = NativeOverlayToasterConfig()
  private var pressedCount = 0
  private var obstructionShift: CGFloat = 0

  init(hostView: UIView) {
    self.hostView = hostView
    super.init()
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
        if item.visible {
          add(item)
        } else {
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
    releaseWindow()
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
    root.addSubview(stage)
    return root
  }

  private func releaseWindow() {
    guard let window else { return }
    stage.removeFromSuperview()
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
      guard let entry, !entry.leaving, entry.item.spec.pressable else { return }
      self?.onEvent?(entry.id, entry.item.generation, "press", "")
    }
    let pan = UIPanGestureRecognizer(target: self, action: #selector(handlePan(_:)))
    pan.maximumNumberOfTouches = 1
    pan.delegate = self
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
    stage.addSubview(entry.slot)
    entry.card.alpha = 0
    UIAccessibility.post(notification: .announcement, argument: entry.card.announcement)
  }

  private func entry(for gesture: UIGestureRecognizer) -> ToastEntry? {
    entries.first { $0.card === gesture.view }
  }

  private func dismiss(_ entry: ToastEntry, reason: String, report: Bool) {
    guard !entry.leaving else { return }
    entry.leaving = true
    entry.card.cancelStatusMotion()
    entry.reason = reason
    entry.timer?.invalidate()
    entry.timer = nil
    if report {
      onEvent?(entry.id, entry.item.generation, "dismiss", reason)
    }
    entry.removal = Timer.scheduledTimer(
      withTimeInterval: ToasterMetrics.removeDelay, repeats: false
    ) { [weak self, weak entry] _ in
      guard let self, let entry else { return }
      self.remove(entry, report: true)
      self.layout(animated: true)
    }
    layout(animated: true)
  }

  private func remove(_ entry: ToastEntry, report: Bool) {
    entry.timer?.invalidate()
    entry.removal?.invalidate()
    entry.card.cancelStatusMotion()
    entry.slot.removeFromSuperview()
    entries.removeAll { $0 === entry }
    if report {
      onEvent?(entry.id, entry.item.generation, "close", entry.reason)
    }
    if entries.isEmpty {
      pressedCount = 0
      releaseWindow()
    }
  }

  // MARK: Layout

  @objc private func layoutChanged() {
    layout(animated: true)
  }

  private func contentWidth(in root: UIView) -> CGFloat {
    config.contentWidth
      ?? min(root.bounds.width, ToasterMetrics.fallbackMaxPageWidth) - ToasterMetrics.fallbackMargin
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
    let width = contentWidth(in: root)
    let safe = root.safeAreaInsets
    let top = safe.top + ToasterMetrics.topOffset
    let animate = animated && !reduceMotion

    let shift = computeObstructionShift(in: root)
    if shift != obstructionShift {
      obstructionShift = shift
      let apply = { self.stage.transform = CGAffineTransform(translationX: 0, y: shift) }
      if animate {
        let animator = toastExitAnimator(duration: ToasterMetrics.obstructionDuration)
        animator.addAnimations(apply)
        animator.startAnimation()
      } else {
        apply()
      }
    }

    // Oldest at the top, newest under it: a toast keeps its place when a
    // newer one arrives and moves up when an older one leaves.
    var offset: CGFloat = 0
    for entry in entries.reversed() {
      let size = entry.card.layoutCard(
        contentWidth: width, contentMaxHeight: config.contentMaxHeight
      )
      let x = safe.left + (root.bounds.width - safe.left - safe.right - size.width) / 2
      // Where the card rests before it enters and after it leaves.
      let hidden = -(size.height > 0 ? size.height : ToasterMetrics.fallbackHeight)
        - safe.top - ToasterMetrics.enterOvershoot
      let targetY = top + (entry.leaving ? hidden : offset)
      let targetAlpha: CGFloat = entry.leaving ? 0 : 1
      if !entry.leaving {
        offset += size.height + ToasterMetrics.gutter
      }
      let frame = CGRect(x: x, y: targetY, width: size.width, height: size.height)
      let targetTransform = entry.leaving
        ? CGAffineTransform(scaleX: ToasterMetrics.hiddenScale, y: ToasterMetrics.hiddenScale)
        : .identity
      if !entry.entered {
        entry.entered = true
        entry.slot.transform = .identity
        entry.slot.frame = CGRect(x: x, y: top + hidden, width: size.width, height: size.height)
        entry.slot.transform = CGAffineTransform(
          scaleX: ToasterMetrics.hiddenScale, y: ToasterMetrics.hiddenScale
        )
        if animate && !entry.leaving { entry.card.animateMessageStatusEntrance() }
      }
      if !entry.leaving && !reduceMotion { entry.card.animateMessageStatusRing() }
      if reduceMotion { entry.card.cancelStatusMotion() }
      entry.slot.bounds.size = size
      entry.card.center = CGPoint(x: size.width / 2, y: size.height / 2)
      let apply = {
        entry.slot.center = CGPoint(x: frame.midX, y: frame.midY)
        entry.slot.bounds.size = size
        entry.slot.transform = targetTransform
        entry.card.alpha = targetAlpha
      }
      if animate {
        let animator = entry.leaving ? toastExitAnimator() : toastSpringAnimator()
        animator.addAnimations(apply)
        animator.startAnimation()
      } else {
        apply()
      }
    }
  }

  // MARK: Timers

  private func syncTimers() {
    let paused = pressedCount > 0
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

  func gestureRecognizer(
    _ gestureRecognizer: UIGestureRecognizer,
    shouldRecognizeSimultaneouslyWith other: UIGestureRecognizer
  ) -> Bool {
    gestureRecognizer is UILongPressGestureRecognizer || other is UILongPressGestureRecognizer
  }

  func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
    if gestureRecognizer is UITapGestureRecognizer {
      guard let entry = entry(for: gestureRecognizer), entry.item.spec.pressable else { return false }
      if touch.view is UIControl || touch.view is ToastTextLabel { return false }
    }
    return true
  }

  @objc private func handlePress(_ gesture: UILongPressGestureRecognizer) {
    switch gesture.state {
    case .began:
      pressedCount += 1
    case .ended, .cancelled, .failed:
      pressedCount = max(0, pressedCount - 1)
    default:
      return
    }
    syncTimers()
  }

  @objc private func handleTap(_ gesture: UITapGestureRecognizer) {
    guard let entry = entry(for: gesture), !entry.leaving, !entry.dragged,
          entry.item.spec.pressable else { return }
    onEvent?(entry.id, entry.item.generation, "press", "")
  }

  @objc private func handlePan(_ gesture: UIPanGestureRecognizer) {
    guard let entry = entry(for: gesture), !entry.leaving else { return }
    let card = entry.card
    // Toasts land at the top, so the dismiss direction is up.
    let translation = gesture.translation(in: stage)
    switch gesture.state {
    case .began:
      card.layer.removeAllAnimations()
      entry.dragStart = card.transform.ty
      entry.dragged = true
    case .changed:
      let drag = translation.y <= 0
        ? entry.dragStart + translation.y
        : entry.dragStart + translation.y / ToasterMetrics.swipeResistance
      card.transform = CGAffineTransform(translationX: 0, y: drag)
    case .ended:
      let distance = -translation.y
      let velocity = -gesture.velocity(in: stage).y
      let dismissed =
        translation.y < 0 && abs(distance) > abs(translation.x)
        && (distance >= ToasterMetrics.swipeDistance
          || (distance >= ToasterMetrics.swipeFlickDistance
            && velocity >= ToasterMetrics.swipeFlickVelocity))
      if dismissed {
        dismiss(entry, reason: "swipe", report: true)
      } else {
        settle(card)
      }
      DispatchQueue.main.async { entry.dragged = false }
    case .cancelled, .failed:
      settle(card)
      entry.dragged = false
    default:
      break
    }
  }

  private func settle(_ card: UIView) {
    guard !reduceMotion else {
      card.transform = .identity
      return
    }
    let animator = toastSpringAnimator()
    animator.addAnimations { card.transform = .identity }
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
