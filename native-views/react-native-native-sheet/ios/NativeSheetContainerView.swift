import React
import UIKit

private enum NativeSheetQuickAnimation {
  static func makeAnimator() -> UIViewPropertyAnimator {
    let timing = UISpringTimingParameters(
      mass: 0.1,
      stiffness: 100,
      damping: 20,
      initialVelocity: .zero
    )
    return UIViewPropertyAnimator(duration: 0, timingParameters: timing)
  }

  static var duration: TimeInterval {
    makeAnimator().duration
  }
}

private final class NativeSheetTransitionAnimator: NSObject,
  UIViewControllerAnimatedTransitioning {
  private let presenting: Bool
  private var animator: UIViewPropertyAnimator?

  init(presenting: Bool) {
    self.presenting = presenting
  }

  func transitionDuration(
    using transitionContext: UIViewControllerContextTransitioning?
  ) -> TimeInterval {
    NativeSheetQuickAnimation.duration
  }

  func animateTransition(using transitionContext: UIViewControllerContextTransitioning) {
    let animator = interruptibleAnimator(using: transitionContext)
    animator.startAnimation()
  }

  func interruptibleAnimator(
    using transitionContext: UIViewControllerContextTransitioning
  ) -> UIViewImplicitlyAnimating {
    if let animator {
      return animator
    }

    let key: UITransitionContextViewKey = presenting ? .to : .from
    guard let sheetView = transitionContext.view(forKey: key) else {
      transitionContext.completeTransition(false)
      return NativeSheetQuickAnimation.makeAnimator()
    }
    let controllerKey: UITransitionContextViewControllerKey = presenting ? .to : .from
    guard let sheetController = transitionContext.viewController(forKey: controllerKey)
      as? NativeSheetViewController else {
      transitionContext.completeTransition(false)
      return NativeSheetQuickAnimation.makeAnimator()
    }
    let container = transitionContext.containerView
    let finalFrame = presenting
      ? transitionContext.finalFrame(for: sheetController)
      : sheetView.frame
    let travel = max(container.bounds.maxY - finalFrame.minY, sheetView.bounds.height)
    let offscreenTransform = CGAffineTransform(translationX: 0, y: travel)

    if presenting {
      sheetView.frame = finalFrame
      sheetView.transform = offscreenTransform
      sheetController.setDimmingAlpha(0)
      if sheetView.superview == nil {
        container.addSubview(sheetView)
      }
    }

    let animator = NativeSheetQuickAnimation.makeAnimator()
    animator.addAnimations {
      sheetView.transform = self.presenting ? .identity : offscreenTransform
      sheetController.setDimmingAlpha(self.presenting ? sheetController.resolvedDimAmount : 0)
    }
    animator.addCompletion { position in
      let completed = position == .end && !transitionContext.transitionWasCancelled
      if !completed {
        sheetView.transform = self.presenting ? offscreenTransform : .identity
        sheetController.setDimmingAlpha(self.presenting ? 0 : sheetController.resolvedDimAmount)
      }
      transitionContext.completeTransition(completed)
    }
    self.animator = animator
    return animator
  }

  func animationEnded(_ transitionCompleted: Bool) {
    animator = nil
  }
}

private final class WeakSheetHost {
  weak var value: NativeSheetContainerView?

  init(_ value: NativeSheetContainerView) {
    self.value = value
  }
}

private final class NativeSheetPresentationCoordinator {
  static let shared = NativeSheetPresentationCoordinator()

  private var active: [WeakSheetHost] = []
  private var pending: [WeakSheetHost] = []
  private var deferredDismissals: [(host: WeakSheetHost, reason: String, animated: Bool)] = []
  private var transitioning = false

  func present(_ host: NativeSheetContainerView) {
    compact()
    guard !active.contains(where: { $0.value === host }),
          !pending.contains(where: { $0.value === host }) else {
      return
    }
    pending.append(WeakSheetHost(host))
    processPending()
  }

  func cancelDeferredProgrammaticDismissal(_ host: NativeSheetContainerView) {
    deferredDismissals.removeAll {
      $0.host.value == nil || ($0.host.value === host && $0.reason == "programmatic")
    }
  }

  func dismiss(_ host: NativeSheetContainerView, reason: String, animated: Bool) {
    pending.removeAll { $0.value == nil || $0.value === host }
    guard !transitioning else {
      queueDismiss(host, reason: reason, animated: animated)
      return
    }

    compact()
    guard let index = active.firstIndex(where: { $0.value === host }) else {
      host.finishDismiss(reason: reason)
      return
    }
    let targets = active[index...].compactMap(\.value).reversed()
    active.removeSubrange(index...)
    dismiss(Array(targets), requestedHost: host, reason: reason, animated: animated)
  }

  func didDismissInteractively(_ host: NativeSheetContainerView, reason: String) {
    active.removeAll { $0.value == nil || $0.value === host }
    transitioning = false
    host.finishDismiss(reason: reason)
    processDeferredDismissalOrPending()
  }

  private func dismiss(
    _ targets: [NativeSheetContainerView],
    requestedHost: NativeSheetContainerView,
    reason: String,
    animated: Bool
  ) {
    guard let target = targets.first else {
      transitioning = false
      processDeferredDismissalOrPending()
      return
    }
    let remaining = Array(targets.dropFirst())
    let targetReason = target === requestedHost
      ? reason
      : (reason == "security" ? reason : "system")
    guard let controller = target.presentedController else {
      target.finishDismiss(reason: targetReason)
      dismiss(
        remaining,
        requestedHost: requestedHost,
        reason: reason,
        animated: animated
      )
      return
    }

    transitioning = true
    controller.dismiss(animated: animated) { [weak self, weak target, weak requestedHost] in
      guard let self else { return }
      target?.finishDismiss(reason: targetReason)
      guard let requestedHost else {
        self.transitioning = false
        self.processDeferredDismissalOrPending()
        return
      }
      self.transitioning = false
      self.dismiss(
        remaining,
        requestedHost: requestedHost,
        reason: reason,
        animated: animated
      )
    }
  }

  private func processPending() {
    compact()
    guard !transitioning else { return }
    guard let host = pending.first?.value else {
      pending.removeAll { $0.value == nil }
      return
    }
    pending.removeFirst()
    guard host.shouldPresent else {
      host.finishFailedPresentation(
        reason: host.securityBlocked ? "security" : (host.open ? "system" : "programmatic")
      )
      processPending()
      return
    }
    guard let presenter = topViewController() else {
      host.finishFailedPresentation(reason: "system")
      processPending()
      return
    }

    let controller = host.makeController()
    transitioning = true
    presenter.present(controller, animated: true) { [weak self, weak host, weak controller] in
      guard let self else { return }
      self.transitioning = false
      if let host, controller?.presentingViewController != nil {
        self.active.append(WeakSheetHost(host))
        if host.shouldPresent {
          host.finishPresent()
        } else {
          self.queueDismiss(
            host,
            reason: host.securityBlocked ? "security" : (host.open ? "system" : "programmatic"),
            animated: false
          )
        }
      } else {
        host?.finishDismiss(reason: "system")
      }
      self.processDeferredDismissalOrPending()
    }
  }

  private func queueDismiss(
    _ host: NativeSheetContainerView,
    reason: String,
    animated: Bool
  ) {
    deferredDismissals.removeAll { $0.host.value == nil || $0.host.value === host }
    deferredDismissals.append((WeakSheetHost(host), reason, animated))
  }

  private func processDeferredDismissalOrPending() {
    compact()
    guard !transitioning else { return }
    while !deferredDismissals.isEmpty {
      let request = deferredDismissals.removeFirst()
      if let host = request.host.value {
        dismiss(host, reason: request.reason, animated: request.animated)
        return
      }
    }
    processPending()
  }

  private func compact() {
    active.removeAll { $0.value == nil }
    pending.removeAll { $0.value == nil }
    deferredDismissals.removeAll { $0.host.value == nil }
  }

  private func topViewController() -> UIViewController? {
    let foregroundScenes = UIApplication.shared.connectedScenes
      .compactMap { $0 as? UIWindowScene }
      .filter { $0.activationState == .foregroundActive }
    let window = foregroundScenes
      .flatMap(\.windows)
      .first(where: \.isKeyWindow)
      ?? foregroundScenes.flatMap(\.windows).first(where: { !$0.isHidden })
    return topViewController(from: window?.rootViewController)
  }

  private func topViewController(from root: UIViewController?) -> UIViewController? {
    if let presented = root?.presentedViewController, !presented.isBeingDismissed {
      return topViewController(from: presented)
    }
    if let navigation = root as? UINavigationController {
      return topViewController(from: navigation.visibleViewController)
    }
    if let tab = root as? UITabBarController {
      return topViewController(from: tab.selectedViewController)
    }
    return root
  }
}

private final class NativeSheetViewController: UIViewController,
  UIAdaptivePresentationControllerDelegate,
  UIViewControllerTransitioningDelegate {
  let host: NativeSheetContainerView
  private let detentIdentifier = UISheetPresentationController.Detent.Identifier(
    "onekey.nativeSheet.fixed"
  )
  private var targetHeight: CGFloat
  private let backgroundColorValue: UIColor
  private let cornerRadiusValue: CGFloat
  private let showHandleValue: Bool
  private let dismissOnBackdropPressValue: Bool
  private let dimAmountValue: CGFloat
  private let backgroundView = UIView()
  private var backdropTap: UITapGestureRecognizer?
  private var dimmingView: UIView?
  private var heightAnimator: UIViewPropertyAnimator?
  private var shadowSuppressionDisplayLink: CADisplayLink?

  fileprivate var resolvedDimAmount: CGFloat {
    dimAmountValue
  }

  init(
    host: NativeSheetContainerView,
    height: CGFloat,
    backgroundColor: UIColor,
    cornerRadius: CGFloat,
    showHandle: Bool,
    dismissOnBackdropPress: Bool,
    dimAmount: CGFloat
  ) {
    self.host = host
    self.targetHeight = height
    self.backgroundColorValue = backgroundColor
    self.cornerRadiusValue = cornerRadius
    self.showHandleValue = showHandle
    self.dismissOnBackdropPressValue = dismissOnBackdropPress
    self.dimAmountValue = min(max(dimAmount, 0), 1)
    super.init(nibName: nil, bundle: nil)
    modalPresentationStyle = .pageSheet
    transitioningDelegate = self
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func loadView() {
    let rootView = UIView()
    rootView.backgroundColor = backgroundColorValue
    rootView.clipsToBounds = true
    if #available(iOS 26.0, *) {
      backgroundView.frame = rootView.bounds
      backgroundView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
      backgroundView.backgroundColor = backgroundColorValue
      backgroundView.isUserInteractionEnabled = false
      rootView.addSubview(backgroundView)
    }
    view = rootView
    host.attachTouchHandler(to: rootView)
  }

  deinit {
    shadowSuppressionDisplayLink?.invalidate()
    dimmingView?.removeFromSuperview()
    if isViewLoaded {
      host.detachTouchHandler(from: view)
    }
  }

  override func viewDidLoad() {
    super.viewDidLoad()
    host.moveContent(to: view)
    guard let sheet = sheetPresentationController else { return }
    let detent = UISheetPresentationController.Detent.custom(
      identifier: detentIdentifier
    ) { [weak self] context in
      min(max(self?.targetHeight ?? 1, 1), context.maximumDetentValue)
    }
    if #available(iOS 26.1, *) {
      let backgroundEffect = UIColorEffect(color: backgroundColorValue)
      sheet.backgroundEffect = backgroundEffect
      detent.backgroundEffect = backgroundEffect
    }
    sheet.detents = [detent]
    sheet.selectedDetentIdentifier = detentIdentifier
    sheet.largestUndimmedDetentIdentifier = detentIdentifier
    sheet.prefersGrabberVisible = showHandleValue
    sheet.preferredCornerRadius = cornerRadiusValue
    sheet.prefersScrollingExpandsWhenScrolledToEdge = false
    sheet.prefersEdgeAttachedInCompactHeight = true
    sheet.widthFollowsPreferredContentSizeWhenEdgeAttached = false
    presentationController?.delegate = self
    isModalInPresentation = !host.dismissOnPanDown
  }

  func updateHeight(_ height: CGFloat, animated: Bool) {
    let nextHeight = max(height, 1)
    guard abs(nextHeight - targetHeight) >= 0.5 else { return }
    guard isViewLoaded, let sheet = sheetPresentationController else {
      targetHeight = nextHeight
      return
    }
    let changes = { [weak self, weak sheet] in
      guard let self, let sheet else { return }
      self.targetHeight = nextHeight
      sheet.invalidateDetents()
      sheet.selectedDetentIdentifier = self.detentIdentifier
      sheet.containerView?.layoutIfNeeded()
    }
    guard animated else {
      changes()
      return
    }

    if let heightAnimator {
      heightAnimator.stopAnimation(false)
      heightAnimator.finishAnimation(at: .current)
    }
    sheet.containerView?.layoutIfNeeded()
    let animator = NativeSheetQuickAnimation.makeAnimator()
    heightAnimator = animator
    animator.addAnimations(changes)
    animator.addCompletion { [weak self, weak animator] _ in
      guard let self, self.heightAnimator === animator else { return }
      self.heightAnimator = nil
    }
    animator.startAnimation()
  }

  func animationController(
    forPresented presented: UIViewController,
    presenting: UIViewController,
    source: UIViewController
  ) -> UIViewControllerAnimatedTransitioning? {
    NativeSheetTransitionAnimator(presenting: true)
  }

  func animationController(
    forDismissed dismissed: UIViewController
  ) -> UIViewControllerAnimatedTransitioning? {
    NativeSheetTransitionAnimator(presenting: false)
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    installDimmingView()
    installBackdropTap()
    startSuppressingPresentationShadow()
    setDimmingVisible(true, animated: animated)
  }

  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    stopSuppressingPresentationShadow()
  }

  override func viewWillDisappear(_ animated: Bool) {
    super.viewWillDisappear(animated)
    stopSuppressingPresentationShadow()
    setDimmingVisible(false, animated: animated)
  }

  override func viewDidDisappear(_ animated: Bool) {
    super.viewDidDisappear(animated)
    if let backdropTap {
      dimmingView?.removeGestureRecognizer(backdropTap)
    }
    backdropTap = nil
    dimmingView?.removeFromSuperview()
    dimmingView = nil
  }

  private func installBackdropTap() {
    guard dismissOnBackdropPressValue,
          backdropTap == nil,
          let dimmingView else {
      return
    }
    let recognizer = UITapGestureRecognizer(target: self, action: #selector(handleBackdropTap(_:)))
    recognizer.cancelsTouchesInView = true
    dimmingView.addGestureRecognizer(recognizer)
    backdropTap = recognizer
  }

  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    removePresentationShadow()
    if #available(iOS 26.0, *) {
      DispatchQueue.main.async { [weak self] in
        self?.removePresentationShadow()
      }
    }
    host.layoutPresentedContent(in: view.bounds)
  }

  func presentationControllerShouldDismiss(_ presentationController: UIPresentationController) -> Bool {
    host.dismissOnPanDown
  }

  func presentationControllerDidDismiss(_ presentationController: UIPresentationController) {
    NativeSheetPresentationCoordinator.shared.didDismissInteractively(host, reason: "pan")
  }

  @objc private func handleBackdropTap(_ recognizer: UITapGestureRecognizer) {
    guard recognizer.state == .ended else { return }
    NativeSheetPresentationCoordinator.shared.dismiss(host, reason: "backdrop", animated: true)
  }

  private func installDimmingView() {
    guard dimmingView == nil,
          let container = presentationController?.containerView else {
      return
    }
    let dimmingView = UIView(frame: container.bounds)
    dimmingView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    dimmingView.backgroundColor = .black
    dimmingView.alpha = 0
    // Always consume backdrop touches. When dismissal is disabled there is no
    // recognizer, but taps still cannot reach the sheet below or the app page.
    dimmingView.isUserInteractionEnabled = true
    container.insertSubview(dimmingView, at: 0)
    self.dimmingView = dimmingView
  }

  fileprivate func setDimmingAlpha(_ alpha: CGFloat) {
    dimmingView?.alpha = alpha
  }

  private func removePresentationShadow() {
    guard let container = presentationController?.containerView else { return }
    var ancestor = view.superview
    while let current = ancestor, current !== container {
      clearShadow(on: current)
      ancestor = current.superview
    }
    removeDropShadowViews(in: container)
  }

  private func removeDropShadowViews(in candidate: UIView) {
    guard candidate !== view else { return }
    let className = NSStringFromClass(type(of: candidate))
    guard className.hasPrefix("UI") || className.hasPrefix("_UI") else { return }
    if className.contains("DropShadowView") {
      clearShadow(on: candidate)
    }
    for subview in candidate.subviews {
      removeDropShadowViews(in: subview)
    }
  }

  private func clearShadow(on shadowView: UIView) {
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    if #available(iOS 26.0, *),
       NSStringFromClass(type(of: shadowView)).contains("UIDropShadowView") {
      compensateForPresentationScale(in: shadowView)
    }
    shadowView.layer.shadowOpacity = 0
    shadowView.layer.shadowRadius = 0
    shadowView.layer.shadowColor = UIColor.clear.cgColor
    shadowView.layer.shadowPath = nil
    CATransaction.commit()
  }

  @available(iOS 26.0, *)
  private func compensateForPresentationScale(in shadowView: UIView) {
    guard var surfaceView = view else { return }
    while let parent = surfaceView.superview, parent !== shadowView {
      surfaceView = parent
    }
    guard surfaceView.superview === shadowView else { return }
    let transform = shadowView.transform
    guard abs(transform.a) > 0.001, abs(transform.d) > 0.001 else { return }
    surfaceView.transform = CGAffineTransform(
      scaleX: 1 / transform.a,
      y: 1 / transform.d
    )
  }

  private func startSuppressingPresentationShadow() {
    shadowSuppressionDisplayLink?.invalidate()
    removePresentationShadow()
    let displayLink = CADisplayLink(
      target: self,
      selector: #selector(suppressPresentationShadowForCurrentFrame)
    )
    displayLink.add(to: .main, forMode: .common)
    shadowSuppressionDisplayLink = displayLink

    if let transitionCoordinator {
      transitionCoordinator.animate(alongsideTransition: nil) { [weak self] _ in
        self?.stopSuppressingPresentationShadow()
      }
    } else {
      DispatchQueue.main.asyncAfter(
        deadline: .now() + NativeSheetQuickAnimation.duration
      ) { [weak self] in
        self?.stopSuppressingPresentationShadow()
      }
    }
  }

  private func stopSuppressingPresentationShadow() {
    shadowSuppressionDisplayLink?.invalidate()
    shadowSuppressionDisplayLink = nil
    removePresentationShadow()
  }

  @objc private func suppressPresentationShadowForCurrentFrame() {
    removePresentationShadow()
  }

  private func setDimmingVisible(_ visible: Bool, animated: Bool) {
    guard let dimmingView else { return }
    let targetAlpha = visible ? dimAmountValue : 0
    let animations = {
      dimmingView.alpha = targetAlpha
    }
    guard animated else {
      animations()
      return
    }
    if let transitionCoordinator {
      transitionCoordinator.animate(alongsideTransition: { _ in
        animations()
      }, completion: { context in
        guard !context.isCancelled else { return }
        dimmingView.alpha = targetAlpha
      })
    } else {
      UIView.animate(
        withDuration: 0.25,
        delay: 0,
        options: [.beginFromCurrentState, .curveEaseInOut],
        animations: animations
      )
    }
  }
}

private final class NativeSheetContentWrapperView: UIView {
  private weak var contentChild: UIView?

  func setContentChild(_ child: UIView?) {
    if contentChild !== child {
      contentChild?.autoresizingMask = []
      contentChild?.removeFromSuperview()
    }
    contentChild = child
    guard let child else { return }
    child.autoresizingMask = []
    if child.superview !== self {
      child.removeFromSuperview()
      addSubview(child)
    }
    setNeedsLayout()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    contentChild?.frame = bounds
  }
}

@objc final class NativeSheetContainerView: UIView {
  @objc var open = false
  @objc var sheetHeight: CGFloat = 0
  @objc var securityBlocked = false
  @objc var dismissOnPanDown = true
  @objc var dismissOnBackdropPress = false
  @objc var dismissOnBackPress = true
  @objc var showHandle = true
  @objc var cornerRadius: CGFloat = 32
  @objc var dimAmount: CGFloat = 0.4
  @objc var sheetBackgroundColor: UIColor?
  @objc var touchHandler: UIGestureRecognizer?
  @objc var onDismiss: RCTDirectEventBlock?
  @objc var onPresented: RCTDirectEventBlock?

  fileprivate var presentedController: NativeSheetViewController?
  fileprivate var shouldPresent: Bool {
    open && !securityBlocked && !dismissedForCurrentOpen && window != nil
  }

  private weak var contentChild: UIView?
  private let contentWrapperView: NativeSheetContentWrapperView = {
    let view = NativeSheetContentWrapperView()
    view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    return view
  }()
  private var committedOpen = false
  private var dismissedForCurrentOpen = false
  private var dismissNotified = false

  override func didMoveToWindow() {
    super.didMoveToWindow()
    if window != nil {
      commitConfiguration()
    } else if presentedController != nil {
      NativeSheetPresentationCoordinator.shared.dismiss(self, reason: "system", animated: false)
    }
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    if presentedController == nil {
      contentWrapperView.frame = bounds
    }
  }

  @objc func insertChild(_ child: UIView, atIndex index: Int) {
    contentWrapperView.setContentChild(child)
    contentChild = child
    if let controller = presentedController {
      moveContent(to: controller.view)
    } else {
      stageContent()
    }
  }

  @objc func removeChild(_ child: UIView) {
    guard contentChild === child else { return }
    contentWrapperView.setContentChild(nil)
    contentWrapperView.removeFromSuperview()
    child.autoresizingMask = []
    contentChild = nil
  }

  @objc func commitConfiguration() {
    let openChanged = committedOpen != open
    committedOpen = open
    if openChanged && open {
      dismissNotified = false
      NativeSheetPresentationCoordinator.shared.cancelDeferredProgrammaticDismissal(self)
    }
    if !open {
      dismissedForCurrentOpen = false
    }

    if securityBlocked {
      dismissedForCurrentOpen = open
      NativeSheetPresentationCoordinator.shared.dismiss(self, reason: "security", animated: false)
    } else if !open {
      NativeSheetPresentationCoordinator.shared.dismiss(self, reason: "programmatic", animated: true)
    } else if window != nil,
              (openChanged || presentedController == nil),
              !dismissedForCurrentOpen {
      if presentedController == nil || presentedController?.isBeingDismissed == true {
        NativeSheetPresentationCoordinator.shared.present(self)
      }
    } else if let controller = presentedController, !dismissedForCurrentOpen {
      let shouldAnimate = controller.presentingViewController != nil
        && !controller.isBeingPresented
      controller.updateHeight(sheetHeight, animated: shouldAnimate)
    }
  }

  @objc func invalidate() {
    NativeSheetPresentationCoordinator.shared.dismiss(self, reason: "system", animated: false)
    onDismiss = nil
    onPresented = nil
  }

  fileprivate func makeController() -> NativeSheetViewController {
    dismissNotified = false
    let controller = NativeSheetViewController(
      host: self,
      height: max(sheetHeight, 1),
      backgroundColor: sheetBackgroundColor ?? .systemBackground,
      cornerRadius: max(cornerRadius, 0),
      showHandle: showHandle,
      dismissOnBackdropPress: dismissOnBackdropPress,
      dimAmount: dimAmount
    )
    presentedController = controller
    return controller
  }

  fileprivate func finishPresent() {
    guard let controller = presentedController else { return }
    onPresented?(["height": min(sheetHeight, controller.view.bounds.height)])
  }

  fileprivate func finishFailedPresentation(reason: String) {
    dismissedForCurrentOpen = committedOpen
    guard !dismissNotified else { return }
    dismissNotified = true
    onDismiss?(["reason": reason])
  }

  fileprivate func finishDismiss(reason: String) {
    if let child = contentChild {
      child.autoresizingMask = []
      stageContent()
    }
    let hadController = presentedController != nil
    presentedController = nil
    let reopenedAfterProgrammaticDismiss = reason == "programmatic" && committedOpen
    dismissedForCurrentOpen = committedOpen && !reopenedAfterProgrammaticDismiss
    let shouldNotify = hadController || (reason == "security" && committedOpen)
    if shouldNotify && !reopenedAfterProgrammaticDismiss && !dismissNotified {
      dismissNotified = true
      onDismiss?(["reason": reason])
    }
  }

  fileprivate func moveContent(to parent: UIView) {
    guard let child = contentChild else { return }
    child.autoresizingMask = []
    contentWrapperView.setContentChild(child)
    if contentWrapperView.superview !== parent {
      contentWrapperView.removeFromSuperview()
      parent.addSubview(contentWrapperView)
    }
    contentWrapperView.frame = parent.bounds
  }

  private func stageContent() {
    if #available(iOS 26.0, *) {
      contentWrapperView.removeFromSuperview()
      contentWrapperView.frame = bounds
      contentWrapperView.layoutIfNeeded()
    } else {
      moveContent(to: self)
    }
  }

  fileprivate func layoutPresentedContent(in bounds: CGRect) {
    contentWrapperView.frame = bounds
  }

  fileprivate func attachTouchHandler(to view: UIView) {
    guard let touchHandler, touchHandler.view == nil else { return }
    touchHandler.perform(NSSelectorFromString("attachToView:"), with: view)
  }

  fileprivate func detachTouchHandler(from view: UIView) {
    guard let touchHandler, touchHandler.view === view else { return }
    touchHandler.perform(NSSelectorFromString("detachFromView:"), with: view)
  }
}
