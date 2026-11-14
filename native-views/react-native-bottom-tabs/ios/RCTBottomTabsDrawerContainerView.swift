import UIKit

/// Internal native host. React owns two fixed slots: menu, then foreground.
@objc(RCTBottomTabsDrawerContainerView)
final class RCTBottomTabsDrawerContainerView: UIView, UIGestureRecognizerDelegate {
  @objc var enabled = false {
    didSet {
      guard enabled != oldValue else { return }
      interruptGesture()
      stopAnimation()
      removeFadeSnapshot()
      stableOpen = false
      setProgress(0)
      if enabled && open { settle(to: true, velocity: 0, user: false) }
    }
  }
  @objc var open = false {
    didSet {
      guard open != oldValue else { return }
      interruptGesture()
      settle(to: enabled && open, velocity: 0, user: false)
    }
  }
  @objc var swipeEnabled = false
  @objc var hapticFeedbackEnabled = false
  @objc var bottomBarHeight: Double = 0
  /// "light" or "dark" forces that palette; anything else follows the trait collection.
  @objc var colorScheme = "system" {
    didSet {
      guard colorScheme != oldValue else { return }
      applyAppearance()
      setProgress(appliedValue, overshoot: appliedOvershoot)
    }
  }
  @objc var progressEventsEnabled = false {
    didSet {
      guard progressEventsEnabled != oldValue else { return }
      // A new subscriber starts from the current position.
      reportedProgress = -1
      if progressEventsEnabled { reportProgress() }
    }
  }
  @objc var onDrawerOpenChange: ((Bool) -> Void)?
  @objc var onDrawerProgress: ((Double) -> Void)?

  private let menu = UIView()
  private let foreground = UIView()
  // Clips the page to its rounded corners; the unclipped foreground casts the shadow.
  private let pageClip = UIView()
  private let foregroundContent = UIView()
  private let dim = UIButton(type: .custom)
  private var slots: [UIView] = []
  private var pan: UIPanGestureRecognizer!
  private var progress: CGFloat = 0
  private var stableOpen = false
  private var gestureStartProgress: CGFloat = 0
  private var gestureReturnOpen = false
  private weak var touchTarget: UIView?
  private var suppressCancellation = false
  private var displayLink: CADisplayLink?
  private var animationStart: CFTimeInterval = 0
  private var animationFrom: CGFloat = 0
  private var animationVelocity: CGFloat = 0
  private var animationTarget = false
  private var animationUser = false
  private var previousSize = CGSize.zero
  private var animationDuration: CFTimeInterval = 0
  private var scrimMaxAlpha: CGFloat = 0.58
  private var pageShadowOpacity: Float = 0
  private var pageHairline = false
  private var reportedProgress: CGFloat = -1
  // The last requested position, kept so a layout pass mid-settle does not
  // clip the spring's overshoot back to fully open.
  private var appliedValue: CGFloat = 0
  private var appliedOvershoot = false
  // Running speed of the current animation, for handing off to a retarget.
  private var trackedValue: CGFloat = 0
  private var trackedTime: CFTimeInterval = 0
  private var trackedVelocity: CGFloat = 0
  private var shadowPathRadius: CGFloat = -1
  // An in-flight Reduce Motion cross-fade: its one snapshot, the state it
  // lands on and whether that is a user transition. Like a running settle, it
  // updates `stableOpen` and reports only when it lands.
  private var fadeSnapshot: UIView?
  private var fadeTarget = false
  private var fadeUser = false
  // Hooks for native tests: they set Reduce Motion, and run without an app
  // host, where UIKit cannot snapshot a view.
  var isReduceMotionEnabled: () -> Bool = { UIAccessibility.isReduceMotionEnabled }
  var makeFadeSnapshot: (UIView) -> UIView? = { $0.snapshotView(afterScreenUpdates: false) }

  // Measured frame by frame from the reference recording: the page travels
  // 80% of the width, keeps a fixed display-like corner, washes toward the
  // background linearly, and casts a soft shadow onto the menu below it (a
  // hairline in dark mode).
  private static let menuWidthFraction: CGFloat = 0.8
  private static let pageCornerRadius: CGFloat = 62
  private static let menuMinScale: CGFloat = 0.97
  // Released drags settle on a slightly underdamped spring: about 2pt of
  // overshoot after a fling, none after a slow release.
  private static let springFrequency: CGFloat = 25.5
  private static let springDamping: CGFloat = 0.84
  private var menuWidth: CGFloat { bounds.width * Self.menuWidthFraction }

  override init(frame: CGRect) {
    super.init(frame: frame)
    clipsToBounds = true
    addSubview(menu)
    addSubview(foreground)
    foreground.addSubview(pageClip)
    pageClip.addSubview(foregroundContent)
    dim.accessibilityLabel = NSLocalizedString("Close menu", comment: "Close the tab drawer")
    dim.addTarget(self, action: #selector(closeFromUser), for: .touchUpInside)
    pageClip.addSubview(dim)
    pageClip.layer.maskedCorners = [.layerMinXMinYCorner, .layerMinXMaxYCorner]
    pageClip.layer.cornerCurve = .circular
    pageClip.clipsToBounds = true
    foreground.layer.shadowColor = UIColor.black.cgColor
    foreground.layer.shadowOffset = .zero
    foreground.layer.shadowRadius = 18
    foreground.layer.shadowOpacity = 0
    pan = UIPanGestureRecognizer(target: self, action: #selector(handlePan(_:)))
    pan.maximumNumberOfTouches = 1
    pan.delegate = self
    addGestureRecognizer(pan)
    applyAppearance()
    setProgress(0)
  }

  private func applyAppearance() {
    let dark: Bool
    switch colorScheme {
    case "dark": dark = true
    case "light": dark = false
    default: dark = traitCollection.userInterfaceStyle == .dark
    }
    // The page washes toward white in light mode and toward gray in dark mode.
    dim.backgroundColor = dark ? UIColor(white: 36.0 / 255.0, alpha: 1) : .white
    scrimMaxAlpha = dark ? 0.58 : 0.5
    pageShadowOpacity = dark ? 0 : 0.16
    pageHairline = dark
    pageClip.layer.borderColor = UIColor(white: 1, alpha: 0.17).cgColor
  }

  override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
    super.traitCollectionDidChange(previousTraitCollection)
    guard traitCollection.hasDifferentColorAppearance(comparedTo: previousTraitCollection) else { return }
    applyAppearance()
    setProgress(appliedValue, overshoot: appliedOvershoot)
  }

  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  @objc func insertChild(_ child: UIView, atIndex index: Int) {
    guard index >= 0 && index <= slots.count && slots.count < 2 else { return }
    slots.insert(child, at: index)
    updateSlots()
  }

  @objc func removeChild(atIndex index: Int) {
    guard slots.indices.contains(index) else { return }
    slots.remove(at: index).removeFromSuperview()
    updateSlots()
  }

  private func updateSlots() {
    for (index, child) in slots.enumerated() {
      (index == 0 ? menu : foregroundContent).insertSubview(child, at: 0)
    }
    setNeedsLayout()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    if previousSize != bounds.size {
      // A cross-fade's image is of the old size, so it lands at once.
      completeFade()
      let dragging = pan.state == .began || pan.state == .changed
      let target = displayLink != nil ? animationTarget : (dragging ? gestureReturnOpen : stableOpen)
      let user = displayLink != nil && animationUser && previousSize != .zero
      interruptGesture()
      stopAnimation()
      finish(target: enabled && (previousSize == .zero ? open : target), user: user)
      previousSize = bounds.size
    }
    menu.transform = .identity
    foreground.transform = .identity
    menu.frame = CGRect(x: 0, y: 0, width: menuWidth, height: bounds.height)
    foreground.frame = bounds
    pageClip.frame = foreground.bounds
    foregroundContent.frame = pageClip.bounds
    dim.frame = pageClip.bounds
    // Rebuilt for the new bounds by setProgress below.
    shadowPathRadius = -1
    if slots.count > 0 { slots[0].frame = menu.bounds }
    if slots.count > 1 { slots[1].frame = foreground.bounds }
    setProgress(appliedValue, overshoot: appliedOvershoot)
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    if window == nil {
      interruptGesture()
      stopAnimation()
      removeFadeSnapshot()
      setProgress(stableOpen && enabled ? 1 : 0)
    } else if enabled && stableOpen != open {
      settle(to: open, velocity: 0, user: false)
    }
  }

  override func accessibilityPerformEscape() -> Bool {
    guard enabled && progress > 0 else { return false }
    closeFromUser()
    return true
  }

  func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
    guard enabled else { return false }
    let start = touch.location(in: self)
    guard start.y < bounds.height - CGFloat(max(0, bottomBarHeight)) else { return false }
    guard !isInsideNativeTabBar(touch.view) else { return false }
    touchTarget = touch.view
    if progress > 0 { return start.x >= menuWidth * progress }
    return swipeEnabled && !touchPathContainsExclusiveControl()
  }

  override func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
    guard enabled, bounds.width > 0 else { return false }
    let velocity = pan.velocity(in: self)
    guard abs(velocity.x) > abs(velocity.y) else { return false }
    let location = pan.location(in: self)
    let translation = pan.translation(in: self)
    let start = CGPoint(x: location.x - translation.x, y: location.y - translation.y)
    guard start.y < bounds.height - CGFloat(max(0, bottomBarHeight)) else { return false }
    if progress > 0 {
      return start.x >= menuWidth * progress
    }
    return swipeEnabled && velocity.x > 0 && !touchPathCanScrollRight()
  }

  // Let this recognizer make its one-time direction/boundary decision before
  // descendant scroll pans. Returning false from shouldBegin releases them.
  func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer,
                         shouldBeRequiredToFailBy other: UIGestureRecognizer) -> Bool {
    guard gestureRecognizer === pan,
          let scroll = other.view as? UIScrollView,
          other === scroll.panGestureRecognizer else { return false }
    return scroll.isDescendant(of: foregroundContent)
  }

  func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer,
                         shouldRequireFailureOf other: UIGestureRecognizer) -> Bool {
    guard gestureRecognizer === pan else { return false }
    // UIKit's navigation edge gesture retains priority over the drawer.
    return other is UIScreenEdgePanGestureRecognizer
  }

  // A vertical bar reports no bottom height, and UIKit draws its items in a
  // floating platter outside UITabBar, so match anything under the tab
  // controller's view that is not tab content.
  private func isInsideNativeTabBar(_ target: UIView?) -> Bool {
    var path: [UIView] = []
    var view = target
    while let current = view, current !== self {
      if current is UITabBar { return true }
      if let tabs = current.next as? UITabBarController {
        let content = (tabs.viewControllers ?? []).compactMap { $0.viewIfLoaded }
        return !path.contains { step in content.contains { $0 === step } }
      }
      path.append(current)
      view = current.superview
    }
    return false
  }

  private func touchPathContainsExclusiveControl() -> Bool {
    var view = touchTarget
    while let current = view, current !== self {
      if current is UISlider || current is UISwitch || current is UISegmentedControl ||
          current is UITextField || current is UITextView { return true }
      if let webClass = NSClassFromString("WKWebView"), current.isKind(of: webClass) { return true }
      if current.layer is CAMetalLayer { return true }
      view = current.superview
    }
    return false
  }

  private func pageController(for scroll: UIScrollView) -> UIPageViewController? {
    var responder: UIResponder? = scroll.next
    while let current = responder, current !== self {
      if let page = current as? UIPageViewController, scroll.superview === page.view { return page }
      responder = current.next
    }
    return nil
  }

  private func touchPathCanScrollRight() -> Bool {
    var view = touchTarget
    while let current = view, current !== self {
      if let scroll = current as? UIScrollView, scroll.isScrollEnabled {
        // The native smooth pager exposes this existing header exclusion. Read
        // its getter instead of invoking UIKit's recognition lifecycle hook.
        let selector = NSSelectorFromString("excludedHeaderView")
        if scroll.responds(to: selector),
           let header = scroll.perform(selector)?.takeUnretainedValue() as? UIView,
           let target = touchTarget, target.isDescendant(of: header) {
          view = current.superview
          continue
        }
        if let page = pageController(for: scroll) {
          // UIPageViewController keeps its private scroll view centered even at
          // the first page. Its public before query describes physical right.
          if page.navigationOrientation == .horizontal,
             let displayed = page.viewControllers?.first,
             page.dataSource?.pageViewController(page, viewControllerBefore: displayed) != nil { return true }
        } else {
          let inset = scroll.adjustedContentInset
          let horizontal = scroll.contentSize.width + inset.left + inset.right > scroll.bounds.width + 0.5
          if horizontal && scroll.contentOffset.x > -inset.left + 0.5 { return true }
        }
      }
      view = current.superview
    }
    return false
  }

  @objc private func handlePan(_ recognizer: UIPanGestureRecognizer) {
    switch recognizer.state {
    case .began:
      // The drag takes over a settle or cross-fade in flight and drops its
      // completion. Cancelling returns to a programmatic target, else to the
      // stable state.
      if displayLink != nil && !animationUser {
        gestureReturnOpen = animationTarget
      } else if fadeSnapshot != nil && !fadeUser {
        gestureReturnOpen = fadeTarget
      } else {
        gestureReturnOpen = stableOpen
      }
      stopAnimation()
      removeFadeSnapshot()
      gestureStartProgress = progress
    case .changed:
      guard recognizer.numberOfTouches <= 1 else {
        interruptGesture()
        settle(to: gestureReturnOpen, velocity: 0, user: false)
        return
      }
      setProgress(gestureStartProgress + recognizer.translation(in: self).x / max(1, menuWidth))
    case .ended:
      let velocity = min(12, max(-12, recognizer.velocity(in: self).x / max(1, menuWidth)))
      settle(to: progress + velocity * 0.12 >= 0.5, velocity: velocity, user: true, tween: false)
    case .cancelled, .failed:
      if !suppressCancellation { settle(to: gestureReturnOpen, velocity: 0, user: false) }
    default: break
    }
  }

  private func interruptGesture() {
    guard pan != nil else { return }
    suppressCancellation = true
    pan.isEnabled = false
    pan.isEnabled = true
    touchTarget = nil
    suppressCancellation = false
  }

  @objc private func closeFromUser() {
    interruptGesture()
    settle(to: false, velocity: 0, user: true)
  }

  private func setProgress(_ value: CGFloat, overshoot: Bool = false) {
    appliedValue = value
    appliedOvershoot = overshoot
    let raw = enabled ? value : 0
    progress = min(1, max(0, raw))
    let visible = progress > 0
    // Only the settle spring may carry the page a few points past open.
    let travel = overshoot ? min(1.03, max(0, raw)) : progress
    foreground.transform = CGAffineTransform(translationX: menuWidth * travel, y: 0)
    // Rounds in over the first 10% so screens with other corner radii never pop.
    let cornerRadius = visible ? Self.pageCornerRadius * min(1, progress / 0.1) : 0
    pageClip.layer.cornerRadius = cornerRadius
    // The shadow follows the same corner while it rounds in.
    if cornerRadius != shadowPathRadius {
      shadowPathRadius = cornerRadius
      foreground.layer.shadowPath = UIBezierPath(
        roundedRect: foreground.bounds,
        byRoundingCorners: [.topLeft, .bottomLeft],
        cornerRadii: CGSize(width: cornerRadius, height: cornerRadius)
      ).cgPath
    }
    pageClip.layer.borderWidth = visible && pageHairline ? 1 / max(1, traitCollection.displayScale) : 0
    foreground.layer.shadowOpacity = visible ? pageShadowOpacity : 0
    dim.alpha = scrimMaxAlpha * progress
    dim.isHidden = !visible
    foregroundContent.isUserInteractionEnabled = !visible
    foregroundContent.accessibilityElementsHidden = visible
    // The menu grows from its left-center edge and fades in over whatever is
    // behind the host, fully visible by 60% open.
    let minScale = isReduceMotionEnabled() ? 1 : Self.menuMinScale
    let scale = minScale + (1 - minScale) * progress
    menu.transform = CGAffineTransform(translationX: -(1 - scale) * menu.bounds.width / 2, y: 0)
      .scaledBy(x: scale, y: scale)
    menu.alpha = min(1, progress / 0.6)
    menu.isHidden = !visible
    menu.accessibilityElementsHidden = !visible
    reportProgress()
  }

  /// Sends the current position again, for an event emitter attached after it was reported.
  @objc func resendProgress() {
    reportedProgress = -1
    reportProgress()
  }

  private func reportProgress() {
    guard progressEventsEnabled else { return }
    if progress != reportedProgress && (abs(progress - reportedProgress) >= 0.002 || progress == 0 || progress == 1) {
      reportedProgress = progress
      onDrawerProgress?(Double(progress))
    }
  }

  // cubic-bezier(0.55, 0.6, 0.2, 1), fitted to the reference tap open and close.
  private static func pageCurve(_ x: CGFloat) -> CGFloat {
    func bezier(_ t: CGFloat, _ p1: CGFloat, _ p2: CGFloat) -> CGFloat {
      let u = 1 - t
      return 3 * u * u * t * p1 + 3 * u * t * t * p2 + t * t * t
    }
    var low: CGFloat = 0
    var high: CGFloat = 1
    for _ in 0..<24 {
      let mid = (low + high) / 2
      if bezier(mid, 0.55, 0.2) < x { low = mid } else { high = mid }
    }
    return bezier((low + high) / 2, 0.6, 1)
  }

  // Taps and programmatic changes ease over a fixed time; a released drag
  // keeps its finger velocity through a slightly underdamped spring.
  private func settle(to requested: Bool, velocity: CGFloat, user: Bool, tween: Bool = true) {
    let target = enabled && requested
    // An animation already heading there keeps running; the newer request
    // still decides whether its completion is a user event.
    if displayLink != nil && animationTarget == target {
      animationUser = user
      return
    }
    // Reversing mid-flight continues from the page's on-screen position and
    // speed, so the motion bends instead of stopping dead.
    let retarget = displayLink != nil
    let from = retarget ? appliedValue : progress
    let carried = retarget ? trackedVelocity : velocity
    stopAnimation()
    guard bounds.width > 0, window != nil else {
      removeFadeSnapshot()
      stableOpen = target
      setProgress(target ? 1 : 0)
      return
    }
    if !retarget && progress == (target ? 1 : 0) {
      // A cross-fade landing there keeps fading; as for a running animation,
      // the newer request decides whether its completion is a user event.
      if fadeSnapshot != nil {
        fadeUser = user
        return
      }
      finish(target: target, user: user)
      return
    }
    if tween && !retarget && isReduceMotionEnabled() && crossFade(to: target, user: user) {
      return
    }
    removeFadeSnapshot()
    animationFrom = from
    animationVelocity = min(12, max(-12, carried))
    animationTarget = target
    animationUser = user
    // Closing runs about 20% shorter than opening.
    let fullDuration: CGFloat = target ? 0.36 : 0.29
    animationDuration = tween && !retarget ? max(0.12, fullDuration * abs((target ? 1 : 0) - from)) : 0
    animationStart = CACurrentMediaTime()
    trackedValue = from
    trackedTime = animationStart
    trackedVelocity = animationVelocity
    let link = CADisplayLink(target: self, selector: #selector(tick))
    link.add(to: .main, forMode: .common)
    displayLink = link
  }

  // Reduce Motion: taps and programmatic changes cross-fade into place
  // instead of sliding the whole page.
  private func crossFade(to target: Bool, user: Bool) -> Bool {
    // Captures what is on screen, including any fade still in progress, which
    // this one then replaces: the host keeps at most one snapshot.
    guard let snapshot = makeFadeSnapshot(self) else { return false }
    removeFadeSnapshot()
    snapshot.frame = bounds
    snapshot.isUserInteractionEnabled = false
    addSubview(snapshot)
    fadeSnapshot = snapshot
    fadeTarget = target
    fadeUser = user
    // The final state shows now, under the snapshot. The transition completes
    // (stable state, callback and haptic) only when the fade does.
    setProgress(target ? 1 : 0)
    UIView.animate(withDuration: 0.2, delay: 0, options: [.curveEaseOut], animations: {
      snapshot.alpha = 0
    }, completion: { [weak self] _ in
      snapshot.removeFromSuperview()
      guard let self, self.fadeSnapshot === snapshot else { return }
      self.completeFade()
    })
    return true
  }

  /// Lands an in-flight cross-fade on its target, as a settle's last frame does.
  private func completeFade() {
    guard fadeSnapshot != nil else { return }
    let target = fadeTarget
    let user = fadeUser
    removeFadeSnapshot()
    finish(target: target, user: user)
  }

  /// Drops the cross-fade without landing it (a newer transition, a drag,
  /// disabling or detach), so its completion is never reported.
  private func removeFadeSnapshot() {
    guard let snapshot = fadeSnapshot else { return }
    // Cleared first, so the completion handler that removing the animation
    // triggers does not land it.
    fadeSnapshot = nil
    snapshot.layer.removeAllAnimations()
    snapshot.removeFromSuperview()
  }

  @objc private func tick() {
    let now = CACurrentMediaTime()
    let elapsed = CGFloat(now - animationStart)
    let target: CGFloat = animationTarget ? 1 : 0
    let value: CGFloat
    let isSpring = animationDuration == 0
    var tweenDone = false
    if !isSpring {
      let x = min(1, elapsed / CGFloat(animationDuration))
      value = animationFrom + (target - animationFrom) * Self.pageCurve(x)
      tweenDone = x >= 1
    } else {
      let w = Self.springFrequency
      let a = animationFrom - target
      if isReduceMotionEnabled() {
        // Critically damped: same speed, no bounce.
        value = target + (a + (animationVelocity + w * a) * elapsed) * exp(-w * elapsed)
      } else {
        let z = Self.springDamping
        let wd = w * sqrt(1 - z * z)
        let decay = exp(-z * w * elapsed)
        value = target + decay * (a * cos(wd * elapsed) + (animationVelocity + z * w * a) / wd * sin(wd * elapsed))
      }
    }
    if now > trackedTime {
      trackedVelocity = (value - trackedValue) / CGFloat(now - trackedTime)
    }
    trackedValue = value
    trackedTime = now
    // Reduce Motion never carries the page past its target.
    setProgress(value, overshoot: isSpring && !isReduceMotionEnabled())
    // A spring ends once it is visually at rest, not after a fixed time.
    let done = isSpring
      ? (abs(value - target) < 0.0005 && abs(trackedVelocity) < 0.02) || elapsed >= 0.6
      : tweenDone
    if done {
      let target = animationTarget
      let user = animationUser
      stopAnimation()
      finish(target: target, user: user)
    }
  }

  private func finish(target: Bool, user: Bool) {
    let changed = stableOpen != target
    stableOpen = target
    setProgress(target ? 1 : 0)
    if user { reportCompletion(target: target, changed: changed) }
  }

  private func reportCompletion(target: Bool, changed: Bool) {
    guard changed || target != open else { return }
    if hapticFeedbackEnabled && changed { UIImpactFeedbackGenerator(style: .light).impactOccurred() }
    onDrawerOpenChange?(target)
  }

  private func stopAnimation() {
    displayLink?.invalidate()
    displayLink = nil
  }

  deinit { displayLink?.invalidate() }
}
