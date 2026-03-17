import Foundation
import UIKit

class HybridScrollGuard: HybridScrollGuardSpec {

  var view: UIView = ScrollGuardView()

  var direction: ScrollGuardDirection? = .horizontal {
    didSet {
      (view as? ScrollGuardView)?.guardDirection = direction ?? .horizontal
    }
  }

  override init() {
    super.init()
  }
}

// MARK: - ScrollGuardView

/// Prevents ancestor PagerView from intercepting scroll gestures belonging to
/// a sibling UIScrollView.
///
/// Two-layer blocking strategy:
/// 1. A "touch detector" (UILongPressGestureRecognizer with minimumPressDuration=0)
///    fires on touch-down IMMEDIATELY — before any pan gesture — and disables the
///    pager's isScrollEnabled. This eliminates first-touch-after-page-transition bugs.
/// 2. A "blocker" UIPanGestureRecognizer with require(toFail:) for belt-and-suspenders.
///
/// Both gestures live on the CHILD ScrollView (Nitro host views render React
/// children as native siblings, not subviews).
class ScrollGuardView: UIView, UIGestureRecognizerDelegate {

  var guardDirection: ScrollGuardDirection = .horizontal {
    didSet {
      invalidateSetup()
    }
  }

  private weak var connectedPager: UIScrollView?
  private weak var connectedChild: UIScrollView?
  private var blockerGesture: UIPanGestureRecognizer?
  private var touchDetector: UILongPressGestureRecognizer?
  private var pagerScrollEnabledOriginal: Bool = true

  deinit {
    cleanupConnection()
  }

  // MARK: - Touch pass-through

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    return nil
  }

  // MARK: - Lifecycle

  override func didMoveToWindow() {
    super.didMoveToWindow()
    if window != nil {
      setupGestureBlocking()
      if connectedChild == nil {
        DispatchQueue.main.async { [weak self] in
          self?.setupGestureBlocking()
        }
      }
    }
    // Do NOT clean up on window=nil — child persists across pager transitions.
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    if connectedChild == nil && window != nil {
      setupGestureBlocking()
    }
  }

  private func invalidateSetup() {
    cleanupConnection()
    if window != nil {
      DispatchQueue.main.async { [weak self] in
        self?.setupGestureBlocking()
      }
    }
  }

  private func cleanupConnection() {
    if let child = connectedChild {
      if let blocker = blockerGesture { child.removeGestureRecognizer(blocker) }
      if let detector = touchDetector { child.removeGestureRecognizer(detector) }
    }
    // Restore pager scrolling if we disabled it
    if let pager = connectedPager {
      pager.isScrollEnabled = pagerScrollEnabledOriginal
    }
    blockerGesture = nil
    touchDetector = nil
    connectedPager = nil
    connectedChild = nil
  }

  // MARK: - Setup

  private func setupGestureBlocking() {
    guard window != nil else { return }
    guard let pagerSV = findAncestorPagingScrollView() else { return }
    guard let childSV = findSiblingScrollView() else { return }

    // Skip if already connected to the same child with gestures still attached
    if connectedChild === childSV,
       let blocker = blockerGesture,
       let detector = touchDetector,
       childSV.gestureRecognizers?.contains(blocker) == true,
       childSV.gestureRecognizers?.contains(detector) == true {
      return
    }

    cleanupConnection()

    // 1) Touch detector — fires on touch-down BEFORE any pan gesture.
    //    Immediately disables pager scrolling so it can't intercept.
    let detector = UILongPressGestureRecognizer(target: self, action: #selector(handleTouchDetected(_:)))
    detector.minimumPressDuration = 0
    detector.cancelsTouchesInView = false
    detector.delaysTouchesBegan = false
    detector.delaysTouchesEnded = false
    detector.delegate = self
    childSV.addGestureRecognizer(detector)

    // 2) Blocker pan gesture — belt-and-suspenders with require(toFail:).
    let blocker = UIPanGestureRecognizer(target: self, action: #selector(handleBlockerPan(_:)))
    blocker.cancelsTouchesInView = false
    blocker.delaysTouchesBegan = false
    blocker.delaysTouchesEnded = false
    blocker.delegate = self
    childSV.addGestureRecognizer(blocker)

    // All pager gestures require blocker to fail
    if let pagerGestures = pagerSV.gestureRecognizers {
      for gesture in pagerGestures {
        gesture.require(toFail: blocker)
      }
    }

    blockerGesture = blocker
    touchDetector = detector
    connectedPager = pagerSV
    connectedChild = childSV
    pagerScrollEnabledOriginal = pagerSV.isScrollEnabled
  }

  // MARK: - Touch detector handler

  @objc private func handleTouchDetected(_ gesture: UILongPressGestureRecognizer) {
    guard let pager = connectedPager else { return }
    switch gesture.state {
    case .began:
      // Touch down — immediately disable pager
      pagerScrollEnabledOriginal = pager.isScrollEnabled
      pager.isScrollEnabled = false
    case .ended, .cancelled, .failed:
      // Touch up — restore pager
      pager.isScrollEnabled = pagerScrollEnabledOriginal
    default:
      break
    }
  }

  // MARK: - Blocker pan handler

  @objc private func handleBlockerPan(_ gesture: UIPanGestureRecognizer) {
    // Empty — exists only for require(toFail:) blocking
  }

  // MARK: - UIGestureRecognizerDelegate

  func gestureRecognizer(
    _ gestureRecognizer: UIGestureRecognizer,
    shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer
  ) -> Bool {
    return true
  }

  override func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
    // Touch detector always begins (it handles all touches in the child area)
    if gestureRecognizer === touchDetector { return true }

    // Blocker only begins for guarded direction
    guard gestureRecognizer === blockerGesture,
          let pan = gestureRecognizer as? UIPanGestureRecognizer else {
      return true
    }
    let velocity = pan.velocity(in: pan.view)
    switch guardDirection {
    case .horizontal:
      return abs(velocity.x) > abs(velocity.y)
    case .vertical:
      return abs(velocity.y) > abs(velocity.x)
    case .both:
      return true
    }
  }

  // MARK: - View hierarchy search

  private func findAncestorPagingScrollView() -> UIScrollView? {
    var current: UIView? = superview
    while let v = current {
      if let sv = v as? UIScrollView, sv.isPagingEnabled { return sv }
      current = v.superview
    }
    return nil
  }

  private func findSiblingScrollView() -> UIScrollView? {
    guard let parent = superview else { return nil }

    var foundSelf = false
    for subview in parent.subviews {
      if subview === self { foundSelf = true; continue }
      if foundSelf {
        if let sv = findFirstScrollView(in: subview) { return sv }
      }
    }

    let myFrame = self.frame
    for subview in parent.subviews {
      if subview === self { continue }
      if subview.frame.intersects(myFrame) {
        if let sv = findFirstScrollView(in: subview) { return sv }
      }
    }
    return nil
  }

  private func findFirstScrollView(in view: UIView) -> UIScrollView? {
    if let sv = view as? UIScrollView { return sv }
    for subview in view.subviews {
      if let sv = findFirstScrollView(in: subview) { return sv }
    }
    return nil
  }
}
