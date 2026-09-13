import React
import UIKit

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
      processPending()
      return
    }
    guard let presenter = topViewController() else {
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
            reason: host.securityBlocked ? "security" : "system",
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
  UIGestureRecognizerDelegate {
  let host: NativeSheetContainerView
  private let lockedHeight: CGFloat
  private let backgroundColorValue: UIColor
  private let cornerRadiusValue: CGFloat
  private let showHandleValue: Bool
  private let dismissOnBackdropPressValue: Bool
  private var backdropTap: UITapGestureRecognizer?

  init(
    host: NativeSheetContainerView,
    height: CGFloat,
    backgroundColor: UIColor,
    cornerRadius: CGFloat,
    showHandle: Bool,
    dismissOnBackdropPress: Bool
  ) {
    self.host = host
    self.lockedHeight = height
    self.backgroundColorValue = backgroundColor
    self.cornerRadiusValue = cornerRadius
    self.showHandleValue = showHandle
    self.dismissOnBackdropPressValue = dismissOnBackdropPress
    super.init(nibName: nil, bundle: nil)
    modalPresentationStyle = .pageSheet
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func loadView() {
    view = UIView()
    view.backgroundColor = backgroundColorValue
    view.clipsToBounds = true
    host.attachTouchHandler(to: view)
  }

  deinit {
    if isViewLoaded {
      host.detachTouchHandler(from: view)
    }
  }

  override func viewDidLoad() {
    super.viewDidLoad()
    host.moveContent(to: view)
    guard let sheet = sheetPresentationController else { return }
    let identifier = UISheetPresentationController.Detent.Identifier("onekey.nativeSheet.fixed")
    sheet.detents = [
      .custom(identifier: identifier) { [lockedHeight] context in
        min(max(lockedHeight, 1), context.maximumDetentValue)
      },
    ]
    sheet.selectedDetentIdentifier = identifier
    sheet.prefersGrabberVisible = showHandleValue
    sheet.preferredCornerRadius = cornerRadiusValue
    sheet.prefersScrollingExpandsWhenScrolledToEdge = false
    sheet.prefersEdgeAttachedInCompactHeight = true
    sheet.widthFollowsPreferredContentSizeWhenEdgeAttached = false
    presentationController?.delegate = self
    isModalInPresentation = !host.dismissOnPanDown
  }

  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    guard dismissOnBackdropPressValue,
          backdropTap == nil,
          let container = presentationController?.containerView else {
      return
    }
    let recognizer = UITapGestureRecognizer(target: self, action: #selector(handleBackdropTap(_:)))
    recognizer.cancelsTouchesInView = false
    recognizer.delegate = self
    container.addGestureRecognizer(recognizer)
    backdropTap = recognizer
  }

  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    host.layoutPresentedContent(in: view.bounds)
  }

  func presentationControllerShouldDismiss(_ presentationController: UIPresentationController) -> Bool {
    host.dismissOnPanDown
  }

  func presentationControllerDidDismiss(_ presentationController: UIPresentationController) {
    NativeSheetPresentationCoordinator.shared.didDismissInteractively(host, reason: "pan")
  }

  func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
    guard let container = presentationController?.containerView else { return false }
    let point = touch.location(in: container)
    return !view.frame.contains(point)
  }

  @objc private func handleBackdropTap(_ recognizer: UITapGestureRecognizer) {
    guard recognizer.state == .ended else { return }
    NativeSheetPresentationCoordinator.shared.dismiss(host, reason: "backdrop", animated: true)
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
      contentChild?.frame = bounds
    }
  }

  @objc func insertChild(_ child: UIView, atIndex index: Int) {
    if contentChild !== child {
      contentChild?.removeFromSuperview()
    }
    contentChild = child
    if let controller = presentedController {
      moveContent(to: controller.view)
    } else {
      child.removeFromSuperview()
      addSubview(child)
      child.frame = bounds
      child.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    }
  }

  @objc func removeChild(_ child: UIView) {
    guard contentChild === child else { return }
    child.removeFromSuperview()
    contentChild = nil
  }

  @objc func commitConfiguration() {
    let openChanged = committedOpen != open
    committedOpen = open
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
      NativeSheetPresentationCoordinator.shared.present(self)
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
      dismissOnBackdropPress: dismissOnBackdropPress
    )
    presentedController = controller
    return controller
  }

  fileprivate func finishPresent() {
    guard let controller = presentedController else { return }
    onPresented?(["height": min(sheetHeight, controller.view.bounds.height)])
  }

  fileprivate func finishDismiss(reason: String) {
    if let child = contentChild {
      child.removeFromSuperview()
      addSubview(child)
      child.frame = bounds
      child.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    }
    let hadController = presentedController != nil
    presentedController = nil
    dismissedForCurrentOpen = committedOpen
    if hadController && !dismissNotified {
      dismissNotified = true
      onDismiss?(["reason": reason])
    }
  }

  fileprivate func moveContent(to parent: UIView) {
    guard let child = contentChild else { return }
    child.removeFromSuperview()
    parent.addSubview(child)
    child.frame = parent.bounds
    child.autoresizingMask = [.flexibleWidth, .flexibleHeight]
  }

  fileprivate func layoutPresentedContent(in bounds: CGRect) {
    contentChild?.frame = bounds
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
