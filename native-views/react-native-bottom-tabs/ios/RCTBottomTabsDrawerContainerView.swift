import UIKit

/// Internal native host. React owns two fixed slots: menu, then foreground.
@objc(RCTBottomTabsDrawerContainerView)
final class RCTBottomTabsDrawerContainerView: UIView, UIGestureRecognizerDelegate {
  @objc var enabled = false {
    didSet {
      guard enabled != oldValue else { return }
      interruptGesture()
      stopAnimation()
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
  @objc var onDrawerOpenChange: ((Bool) -> Void)?

  private let menu = UIView()
  private let menuShade = UIView()
  private let foreground = UIView()
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

  override init(frame: CGRect) {
    super.init(frame: frame)
    clipsToBounds = true
    addSubview(menu)
    menuShade.backgroundColor = .black
    menuShade.isUserInteractionEnabled = false
    menu.addSubview(menuShade)
    addSubview(foreground)
    foreground.addSubview(foregroundContent)
    dim.backgroundColor = UIColor(white: 28.0 / 255.0, alpha: 1)
    dim.accessibilityLabel = NSLocalizedString("Close menu", comment: "Close the tab drawer")
    dim.addTarget(self, action: #selector(closeFromUser), for: .touchUpInside)
    foreground.addSubview(dim)
    foreground.layer.maskedCorners = [.layerMinXMinYCorner, .layerMinXMaxYCorner]
    foreground.layer.cornerCurve = .circular
    foreground.clipsToBounds = true
    pan = UIPanGestureRecognizer(target: self, action: #selector(handlePan(_:)))
    pan.maximumNumberOfTouches = 1
    pan.delegate = self
    addGestureRecognizer(pan)
    setProgress(0)
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
    menu.bringSubviewToFront(menuShade)
    setNeedsLayout()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    if previousSize != bounds.size {
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
    menu.frame = CGRect(x: 0, y: 0, width: bounds.width * 0.78, height: bounds.height)
    foreground.frame = bounds
    foregroundContent.frame = foreground.bounds
    dim.frame = foreground.bounds
    menuShade.frame = menu.bounds
    if slots.count > 0 { slots[0].frame = menu.bounds }
    if slots.count > 1 { slots[1].frame = foreground.bounds }
    setProgress(progress)
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    if window == nil {
      interruptGesture()
      stopAnimation()
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
    touchTarget = touch.view
    if progress > 0 { return start.x >= bounds.width * 0.78 * progress }
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
      return start.x >= bounds.width * 0.78 * progress
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
      gestureReturnOpen = displayLink != nil && !animationUser ? animationTarget : stableOpen
      stopAnimation()
      gestureStartProgress = progress
    case .changed:
      guard recognizer.numberOfTouches <= 1 else {
        interruptGesture()
        settle(to: gestureReturnOpen, velocity: 0, user: false)
        return
      }
      setProgress(gestureStartProgress + recognizer.translation(in: self).x / max(1, bounds.width * 0.78))
    case .ended:
      let velocity = min(8, max(-8, recognizer.velocity(in: self).x / max(1, bounds.width * 0.78)))
      settle(to: progress + velocity * 0.12 >= 0.5, velocity: velocity, user: true)
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

  private func setProgress(_ value: CGFloat) {
    progress = min(1, max(0, enabled ? value : 0))
    foreground.transform = CGAffineTransform(translationX: bounds.width * 0.78 * progress, y: 0)
    foreground.layer.cornerRadius = 64 * progress
    dim.alpha = 0.7 * progress
    dim.isHidden = progress == 0
    foregroundContent.isUserInteractionEnabled = progress == 0
    foregroundContent.accessibilityElementsHidden = progress > 0
    menu.transform = CGAffineTransform(translationX: -0.12 * bounds.width * 0.78 * (1 - progress), y: 0)
    menuShade.alpha = 0.12 * (1 - progress)
    menu.isHidden = progress == 0
    menu.accessibilityElementsHidden = progress == 0
  }

  private func settle(to target: Bool, velocity: CGFloat, user: Bool) {
    stopAnimation()
    let target = enabled && target
    guard bounds.width > 0, window != nil else {
      stableOpen = target
      setProgress(target ? 1 : 0)
      return
    }
    if progress == (target ? 1 : 0) {
      finish(target: target, user: user)
      return
    }
    animationFrom = progress
    animationVelocity = min(8, max(-8, velocity))
    animationTarget = target
    animationUser = user
    animationStart = CACurrentMediaTime()
    let link = CADisplayLink(target: self, selector: #selector(tick))
    link.add(to: .main, forMode: .common)
    displayLink = link
  }

  @objc private func tick() {
    let elapsed = CGFloat(CACurrentMediaTime() - animationStart)
    let target: CGFloat = animationTarget ? 1 : 0
    let a = animationFrom - target
    let b = animationVelocity + 22 * a
    setProgress(target + (a + b * elapsed) * exp(-22 * elapsed))
    if elapsed >= 0.5 {
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
    if user && (changed || target != open) {
      if hapticFeedbackEnabled && changed { UIImpactFeedbackGenerator(style: .light).impactOccurred() }
      onDrawerOpenChange?(target)
    }
  }

  private func stopAnimation() {
    displayLink?.invalidate()
    displayLink = nil
  }

  deinit { displayLink?.invalidate() }
}
