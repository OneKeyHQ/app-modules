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

/// A native UIView wrapper that prevents ancestor UIPageViewController (PagerView)
/// from intercepting scroll gestures that belong to child UIScrollViews.
///
/// Mechanism: When this view is added to the hierarchy, it walks up the view tree
/// to find the nearest paging UIScrollView (owned by UIPageViewController).
/// It then sets up `requireGestureRecognizerToFail` so the pager's pan gesture
/// must wait for child scroll view gestures to fail before it can activate.
class ScrollGuardView: UIView {

  var guardDirection: ScrollGuardDirection = .horizontal {
    didSet {
      setNeedsSetup()
    }
  }

  private var hasSetup = false
  private var observedScrollViews: [UIScrollView] = []

  override func didMoveToWindow() {
    super.didMoveToWindow()
    if window != nil {
      // Delay setup to ensure child scroll views are mounted
      DispatchQueue.main.async { [weak self] in
        self?.setupGestureBlocking()
      }
    } else {
      teardownGestureBlocking()
    }
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    if !hasSetup && window != nil {
      setupGestureBlocking()
    }
  }

  private func setNeedsSetup() {
    hasSetup = false
    if window != nil {
      DispatchQueue.main.async { [weak self] in
        self?.setupGestureBlocking()
      }
    }
  }

  private func setupGestureBlocking() {
    // Tear down previous state
    teardownGestureBlocking()

    // 1. Find the nearest ancestor paging UIScrollView (UIPageViewController's internal scroll view)
    guard let pagerScrollView = findAncestorPagingScrollView() else { return }

    // 2. Find all descendant UIScrollViews
    let childScrollViews = findDescendantScrollViews(in: self)
    if childScrollViews.isEmpty { return }

    // 3. Set up gesture dependency:
    //    pager's panGestureRecognizer requires child scroll view's pan to fail first.
    //    This means: as long as the child scroll view's gesture is active (even at edge),
    //    the pager cannot begin its gesture.
    let blockHorizontal = guardDirection == .horizontal || guardDirection == .both
    let blockVertical = guardDirection == .vertical || guardDirection == .both

    for childSV in childScrollViews {
      let isChildHorizontal = childSV.contentSize.width > childSV.bounds.width
      let isChildVertical = childSV.contentSize.height > childSV.bounds.height

      let shouldBlock = (blockHorizontal && isChildHorizontal) || (blockVertical && isChildVertical)
      if shouldBlock {
        pagerScrollView.panGestureRecognizer.require(toFail: childSV.panGestureRecognizer)
        observedScrollViews.append(childSV)
      }
    }

    hasSetup = true
  }

  private func teardownGestureBlocking() {
    // iOS does not provide a public API to remove `requireGestureRecognizerToFail`
    // relationships. They are automatically cleaned up when the gesture recognizer
    // is deallocated. For dynamic scenarios, we rely on the scroll views being
    // re-created (which happens naturally in React Native).
    observedScrollViews.removeAll()
    hasSetup = false
  }

  /// Walk up the view hierarchy to find the nearest UIScrollView with isPagingEnabled
  /// (this is the internal scroll view of UIPageViewController)
  private func findAncestorPagingScrollView() -> UIScrollView? {
    var current: UIView? = superview
    while let v = current {
      if let sv = v as? UIScrollView, sv.isPagingEnabled {
        return sv
      }
      current = v.superview
    }
    return nil
  }

  /// Recursively find all UIScrollViews in the subtree
  private func findDescendantScrollViews(in view: UIView) -> [UIScrollView] {
    var result: [UIScrollView] = []
    for subview in view.subviews {
      if let sv = subview as? UIScrollView {
        result.append(sv)
      }
      result.append(contentsOf: findDescendantScrollViews(in: subview))
    }
    return result
  }
}
