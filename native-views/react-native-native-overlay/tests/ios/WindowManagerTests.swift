import UIKit

private final class Owner: NativeOverlayWindowOwner, NativeOverlaySheetHost {
  var disconnects = 0
  var onDisconnect: ((UIWindow) -> Void)?
  var unexpected = 0
  var dismissOnPanDown = true
  func overlaySceneDidDisconnect(window: UIWindow) {
    disconnects += 1
    onDisconnect?(window)
  }
  func sheetDidLoad(_ controller: NativeOverlaySheetController) {}
  func sheetDidLayout(_ controller: NativeOverlaySheetController) {}
  func sheetDidDismissInteractively(_ controller: NativeOverlaySheetController) {}
  func sheetDidDisappearUnexpectedly(_ controller: NativeOverlaySheetController) { unexpected += 1 }
  func sheetRequestedDismiss(_ controller: NativeOverlaySheetController, reason: String) {}
}

private final class TransitionRoot: UIViewController {
  var activeCoordinator: UIViewControllerTransitionCoordinator?
  override var transitionCoordinator: UIViewControllerTransitionCoordinator? { activeCoordinator }
}

private final class TestTransitionCoordinator: NSObject, UIViewControllerTransitionCoordinator {
  var isInteractive = true
  var isCancelled = false
  var subscriptions = 0
  private var handlers: [(UIViewControllerTransitionCoordinatorContext) -> Void] = []

  var isAnimated: Bool { true }
  var presentationStyle: UIModalPresentationStyle { .pageSheet }
  var initiallyInteractive: Bool { true }
  var isInterruptible: Bool { true }
  var transitionDuration: TimeInterval { 0.3 }
  var percentComplete: CGFloat { 0.2 }
  var completionVelocity: CGFloat { 1 }
  var completionCurve: UIView.AnimationCurve { .easeInOut }
  var containerView = UIView()
  var targetTransform: CGAffineTransform { .identity }

  func viewController(forKey key: UITransitionContextViewControllerKey) -> UIViewController? { nil }
  func view(forKey key: UITransitionContextViewKey) -> UIView? { nil }
  func animate(alongsideTransition animation: ((UIViewControllerTransitionCoordinatorContext) -> Void)?,
               completion: ((UIViewControllerTransitionCoordinatorContext) -> Void)?) -> Bool { false }
  func animateAlongsideTransition(in view: UIView?,
               animation: ((UIViewControllerTransitionCoordinatorContext) -> Void)?,
               completion: ((UIViewControllerTransitionCoordinatorContext) -> Void)?) -> Bool { false }
  func notifyWhenInteractionEnds(_ handler: @escaping (UIViewControllerTransitionCoordinatorContext) -> Void) {
    handlers.append(handler)
  }
  func notifyWhenInteractionChanges(_ handler: @escaping (UIViewControllerTransitionCoordinatorContext) -> Void) {
    subscriptions += 1
    handlers.append(handler)
  }

  func changeInteraction(to interactive: Bool, cancelled: Bool = false) {
    isInteractive = interactive
    isCancelled = cancelled
    handlers.forEach { $0(self) }
  }
}

private final class TestPopAnimator: NSObject, UIViewControllerAnimatedTransitioning {
  func transitionDuration(using context: UIViewControllerContextTransitioning?) -> TimeInterval { 0.2 }

  func animateTransition(using context: UIViewControllerContextTransitioning) {
    guard let from = context.view(forKey: .from), let to = context.view(forKey: .to) else {
      context.completeTransition(false)
      return
    }
    if to.superview == nil { context.containerView.insertSubview(to, belowSubview: from) }
    UIView.animate(withDuration: transitionDuration(using: context), animations: {
      from.transform = CGAffineTransform(translationX: context.containerView.bounds.width, y: 0)
    }, completion: { _ in
      from.transform = .identity
      context.completeTransition(!context.transitionWasCancelled)
    })
  }
}

private final class TestPopDelegate: NSObject, UINavigationControllerDelegate {
  let animator = TestPopAnimator()
  var interaction: UIPercentDrivenInteractiveTransition?

  func navigationController(_ navigationController: UINavigationController,
                            animationControllerFor operation: UINavigationController.Operation,
                            from fromVC: UIViewController, to toVC: UIViewController) -> UIViewControllerAnimatedTransitioning? {
    operation == .pop ? animator : nil
  }

  func navigationController(_ navigationController: UINavigationController,
                            interactionControllerFor animationController: UIViewControllerAnimatedTransitioning) -> UIViewControllerInteractiveTransitioning? {
    interaction
  }
}

private struct Failure: Error { let name: String }
private func require(_ condition: @autoclosure () -> Bool, _ name: String) throws {
  if !condition() { throw Failure(name: name) }
}

@MainActor private func nextMainTurn() async {
  await withCheckedContinuation { (done: CheckedContinuation<Void, Never>) in
    DispatchQueue.main.async { done.resume() }
  }
}

@MainActor private func runTests(scene: UIWindowScene, app: UIWindow) async throws -> [String] {
  var passed: [String] = []
  let manager = NativeOverlayWindowManager()
  let a = Owner(), b = Owner(), c = Owner(), toast = Owner()
  let wa = manager.window(for: a, level: .modal, stackOrder: Int.min, blocking: true, appWindow: app)!
  let wb = manager.window(for: b, level: .modal, stackOrder: Int.max, blocking: true, appWindow: wa)!
  let wc = manager.window(for: c, level: .modal, stackOrder: Int.max, blocking: true, appWindow: app)!
  try require(wa !== wb && wb !== wc, "independent entry windows")
  try require(manager.presenter(in: wb) !== manager.presenter(in: wa), "independent root presenters")
  try require(wa.windowLevel < wb.windowLevel && wb.windowLevel < wc.windowLevel, "store order and stable tie order")
  try require(wc.windowLevel.rawValue < NativeOverlayLevel.modal.windowLevel.rawValue + 1, "bounded rank for extreme stackOrder")
  passed.append("independent-roots-and-bounded-order")
  wb.makeKey()
  let field = UITextField(frame: CGRect(x: 20, y: 120, width: 200, height: 40))
  wb.rootViewController!.view.addSubview(field)
  try require(field.becomeFirstResponder(), "native field first responder")
  let rootA = wa.rootViewController
  manager.update(wc, stackOrder: 0, blocking: true, visible: true)
  manager.update(wa, stackOrder: 2, blocking: true, visible: true)
  let reusedA = manager.window(for: a, level: .secure, stackOrder: 2,
                               blocking: true, appWindow: app)
  try require(reusedA === wa && wa.windowLevel.rawValue < NativeOverlayLevel.modal.windowLevel.rawValue + 1,
              "an active owner's level remains fixed")
  try require(wa.rootViewController === rootA && wa.windowLevel > wc.windowLevel && wb.isKeyWindow, "live reorder preserves root and key")
  manager.entryRemoved(from: wa)
  try require(wb.isKeyWindow && field.isFirstResponder && wa.windowScene == nil && wa.rootViewController == nil, "non-key owner removal")
  passed.append("same-level-update-and-non-key-removal")
  let wt = manager.window(for: toast, level: .toast, stackOrder: 100, blocking: false, appWindow: app)!
  wc.makeKey(); manager.entryRemoved(from: wc)
  try require(wb.isKeyWindow && !wt.isKeyWindow, "passive toast cannot take restored key")
  passed.append("restore-blocker-before-passive-toast")
  manager.update(wb, stackOrder: 2, blocking: true, visible: false)
  let d = Owner()
  let wd = manager.window(for: d, level: .secure, stackOrder: 3, blocking: true, appWindow: app)!
  wd.makeKey(); manager.entryRemoved(from: wd)
  try require(app.isKeyWindow, "desired-hidden entry excluded from key restoration")
  manager.update(wb, stackOrder: 2, blocking: true, visible: true)
  manager.setClosing(true, window: wb)
  let wd2 = manager.window(for: d, level: .secure, stackOrder: 4, blocking: true, appWindow: app)!
  try require(wd2 !== wd, "released owner creates a new window lifetime")
  wd2.makeKey(); manager.entryRemoved(from: wd2)
  try require(app.isKeyWindow, "closing entry excluded from key restoration")
  manager.setClosing(false, window: wb)
  passed.append("desired-visibility-closing-and-owner-reuse")
  try require(manager.restoreFocus(field, in: wb) && wb.isKeyWindow && field.isFirstResponder,
              "eligible owner restores its original input")
  let higherOwner = Owner()
  let higher = manager.window(for: higherOwner, level: .secure, stackOrder: 10,
                              blocking: true, appWindow: app)!
  higher.makeKey()
  try require(!manager.restoreFocus(field, in: wb) && higher.isKeyWindow,
              "reopen cannot steal focus from a higher entry")
  manager.entryRemoved(from: higher)
  let foreign = UIWindow(windowScene: scene)
  foreign.rootViewController = UIViewController()
  foreign.makeKeyAndVisible()
  try require(!manager.restoreFocus(field, in: wb) && foreign.isKeyWindow,
              "reopen cannot steal foreign key ownership")
  foreign.isHidden = true
  foreign.rootViewController = nil
  foreign.windowScene = nil
  wb.makeKey()
  let replacement = UITextField(frame: field.frame)
  wb.rootViewController!.view.addSubview(replacement)
  try require(replacement.becomeFirstResponder(), "new input activation")
  try require(!manager.restoreFocus(field, in: wb) && replacement.isFirstResponder,
              "reopen respects a newly focused input")
  replacement.removeFromSuperview()
  passed.append("scoped-reopen-focus-restoration")
  let nested = Owner()
  var disconnectWindow: UIWindow?
  var reentrantWindow: UIWindow?
  b.onDisconnect = { window in
    disconnectWindow = window
    reentrantWindow = manager.window(for: nested, level: .modal, stackOrder: 1,
                                     blocking: true, appWindow: app)
  }
  NotificationCenter.default.post(name: UIScene.didDisconnectNotification, object: scene)
  try require(b.disconnects == 1 && toast.disconnects == 1 && a.disconnects == 0, "disconnect targets only registered owners")
  try require(disconnectWindow === wb && reentrantWindow == nil,
              "disconnect identifies original window and blocks reentrant registration")
  try require(wb.windowScene == nil && wt.windowScene == nil && wb.rootViewController == nil, "disconnect releases native roots")
  let reused = manager.window(for: b, level: .modal, stackOrder: 1, blocking: true, appWindow: app)!
  try require(reused !== wb, "scene cleanup permits later registration")
  manager.entryRemoved(from: reused)
  passed.append("scene-disconnect-and-reuse")
  let sheetA = Owner(), sheetB = Owner()
  let sa = manager.window(for: sheetA, level: .modal, stackOrder: 1, blocking: true, appWindow: app)!
  let sb = manager.window(for: sheetB, level: .modal, stackOrder: 2, blocking: true, appWindow: app)!
  let style = NativeOverlaySheetStyle(height: 300, backgroundColor: .systemBackground, cornerRadius: 24,
    showHandle: true, backdropColor: .black.withAlphaComponent(0.4), dismissOnBackdropPress: true,
    enterMotion: .quick, exitMotion: .quick)
  let ca = NativeOverlaySheetController(host: sheetA, style: style)
  let cb = NativeOverlaySheetController(host: sheetB, style: style)
  await withCheckedContinuation { (done: CheckedContinuation<Void, Never>) in sa.rootViewController!.present(ca, animated: false) { done.resume() } }
  await withCheckedContinuation { (done: CheckedContinuation<Void, Never>) in sb.rootViewController!.present(cb, animated: false) { done.resume() } }
  let survivorView = cb.view
  let survivorPresenter = cb.presentingViewController
  try require(ca.sheetPresentationController != nil && cb.sheetPresentationController != nil, "production system sheets")
  ca.dismissRequestedByHost = true
  await withCheckedContinuation { (done: CheckedContinuation<Void, Never>) in ca.dismiss(animated: false) { done.resume() } }
  manager.entryRemoved(from: sa)
  try require(cb.view === survivorView && cb.presentingViewController === survivorPresenter && cb.view.window === sb && sheetB.unexpected == 0, "production sheet survivor retains native identity")
  cb.dismissRequestedByHost = true
  await withCheckedContinuation { (done: CheckedContinuation<Void, Never>) in cb.dismiss(animated: false) { done.resume() } }
  manager.entryRemoved(from: sb)
  passed.append("production-system-sheet-independent-close")

  let transitionApp = UIWindow(windowScene: scene)
  let transitionRoot = TransitionRoot()
  transitionApp.rootViewController = transitionRoot
  transitionApp.makeKeyAndVisible()
  let transitionManager = NativeOverlayWindowManager()
  let finishingOwner = Owner()
  let finishingWindow = transitionManager.window(for: finishingOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: transitionApp)!
  finishingWindow.makeKey()
  let finishing = TestTransitionCoordinator()
  transitionRoot.activeCoordinator = finishing
  transitionManager.entryRemoved(from: finishingWindow)
  try require(transitionApp.isKeyWindow && finishingWindow.windowScene === scene &&
    finishingWindow.rootViewController != nil && finishing.subscriptions == 1,
    "interactive removal retains old context and restores key")
  finishing.changeInteraction(to: false)
  await nextMainTurn()
  try require(finishingWindow.windowScene == nil && finishingWindow.rootViewController == nil,
    "finished interaction releases old context on next main turn")
  passed.append("interactive-window-retirement-finish")

  let cancellingOwner = Owner()
  let cancellingWindow = transitionManager.window(for: cancellingOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: transitionApp)!
  let cancelling = TestTransitionCoordinator()
  transitionRoot.activeCoordinator = cancelling
  transitionManager.entryRemoved(from: cancellingWindow)
  cancelling.changeInteraction(to: false)
  cancelling.changeInteraction(to: true)
  await nextMainTurn()
  try require(cancellingWindow.windowScene === scene && cancelling.subscriptions == 1,
    "same coordinator reentry keeps context without duplicate subscription")
  cancelling.changeInteraction(to: false, cancelled: true)
  await nextMainTurn()
  try require(cancellingWindow.windowScene == nil && cancellingWindow.rootViewController == nil,
    "cancelled interaction releases old context")
  passed.append("interactive-window-retirement-cancel-reentry")

  let reopeningOwner = Owner()
  let oldWindow = transitionManager.window(for: reopeningOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: transitionApp)!
  let oldInteraction = TestTransitionCoordinator()
  transitionRoot.activeCoordinator = oldInteraction
  transitionManager.entryRemoved(from: oldWindow)
  let newWindow = transitionManager.window(for: reopeningOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: transitionApp)!
  let newInteraction = TestTransitionCoordinator()
  oldInteraction.changeInteraction(to: false)
  transitionRoot.activeCoordinator = newInteraction
  await nextMainTurn()
  try require(oldWindow.windowScene === scene && newWindow.windowScene === scene &&
    newInteraction.subscriptions == 1, "new coordinator retains only old window lifetime")
  newInteraction.changeInteraction(to: false)
  await nextMainTurn()
  try require(oldWindow.windowScene == nil && newWindow.windowScene === scene &&
    newWindow.rootViewController != nil, "late callback does not release reopened owner")
  transitionManager.entryRemoved(from: newWindow)
  passed.append("interactive-window-retirement-owner-reuse")

  let nestedApp = UIWindow(windowScene: scene)
  let containerRoot = UIViewController()
  nestedApp.rootViewController = containerRoot
  nestedApp.makeKeyAndVisible()
  let navigation = UINavigationController(rootViewController: UIViewController())
  containerRoot.addChild(navigation)
  navigation.view.frame = containerRoot.view.bounds
  containerRoot.view.addSubview(navigation.view)
  navigation.didMove(toParent: containerRoot)
  navigation.pushViewController(UIViewController(), animated: false)
  let popDelegate = TestPopDelegate()
  let popInteraction = UIPercentDrivenInteractiveTransition()
  popDelegate.interaction = popInteraction
  navigation.delegate = popDelegate
  let nestedManager = NativeOverlayWindowManager()
  let nestedOwner = Owner()
  let nestedWindow = nestedManager.window(for: nestedOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: nestedApp)!
  nestedWindow.makeKey()
  _ = navigation.popViewController(animated: true)
  popInteraction.update(0.25)
  for _ in 0..<5 where navigation.transitionCoordinator?.isInteractive != true { await nextMainTurn() }
  try require(containerRoot.transitionCoordinator == nil &&
    navigation.transitionCoordinator?.isInteractive == true && navigation.viewIfLoaded?.window === nestedApp,
    "real contained navigation transition is interactive only in the child")
  nestedManager.entryRemoved(from: nestedWindow)
  try require(nestedWindow.windowScene === scene && nestedWindow.rootViewController != nil,
    "attached child navigation transition retains the exiting window")
  popInteraction.cancel()
  let nestedDeadline = Date().addingTimeInterval(2)
  while nestedWindow.windowScene != nil && Date() < nestedDeadline {
    try await Task.sleep(for: .milliseconds(20))
  }
  try require(nestedWindow.windowScene == nil && nestedWindow.rootViewController == nil,
    "cancelled child navigation transition releases the exiting window")
  let unloadedChild = TransitionRoot()
  unloadedChild.activeCoordinator = TestTransitionCoordinator()
  containerRoot.addChild(unloadedChild)
  let detachedChild = TransitionRoot()
  _ = detachedChild.view
  detachedChild.activeCoordinator = TestTransitionCoordinator()
  containerRoot.addChild(detachedChild)
  try require(unloadedChild.viewIfLoaded == nil && detachedChild.viewIfLoaded?.window == nil,
    "inactive child transition precondition")
  let inactiveOwner = Owner()
  let inactiveWindow = nestedManager.window(for: inactiveOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: nestedApp)!
  nestedManager.entryRemoved(from: inactiveWindow)
  try require(inactiveWindow.windowScene == nil && inactiveWindow.rootViewController == nil &&
    unloadedChild.viewIfLoaded == nil, "unattached children do not retain a window or load a view")
  unloadedChild.removeFromParent()
  detachedChild.removeFromParent()
  passed.append("interactive-window-retirement-unattached-children")
  navigation.willMove(toParent: nil)
  navigation.view.removeFromSuperview()
  navigation.removeFromParent()
  nestedApp.isHidden = true
  nestedApp.rootViewController = nil
  nestedApp.windowScene = nil
  transitionApp.makeKey()
  passed.append("interactive-window-retirement-contained-navigation")

  let disconnectedOwner = Owner()
  let disconnectedWindow = transitionManager.window(for: disconnectedOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: transitionApp)!
  let disconnectedInteraction = TestTransitionCoordinator()
  transitionRoot.activeCoordinator = disconnectedInteraction
  transitionManager.entryRemoved(from: disconnectedWindow)
  NotificationCenter.default.post(name: UIScene.didDisconnectNotification, object: scene)
  try require(disconnectedWindow.windowScene == nil && disconnectedWindow.rootViewController == nil &&
    disconnectedOwner.disconnects == 0, "scene disconnect drains retired-only window once")
  disconnectedInteraction.changeInteraction(to: false)
  await nextMainTurn()
  try require(disconnectedOwner.disconnects == 0 && disconnectedWindow.rootViewController == nil,
    "late interaction callback after disconnect is inert")
  transitionApp.isHidden = true
  transitionApp.rootViewController = nil
  transitionApp.windowScene = nil
  app.makeKey()
  passed.append("interactive-window-retirement-scene-disconnect")
  return passed
}

final class TestScene: UIResponder, UIWindowSceneDelegate {
  var window: UIWindow?
  private var started = false
  func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options: UIScene.ConnectionOptions) {
    guard let scene = scene as? UIWindowScene else { return }
    let window = UIWindow(windowScene: scene); self.window = window
    window.rootViewController = UIViewController(); window.makeKeyAndVisible()
  }
  func sceneDidBecomeActive(_ scene: UIScene) {
    guard !started, let scene = scene as? UIWindowScene, let window else { return }
    started = true
    Task { @MainActor in
      let result: [String: Any]
      do { result = ["outcome": "passed", "tests": try await runTests(scene: scene, app: window)] }
      catch let failure as Failure { result = ["outcome": "failed", "failure": failure.name] }
      catch { result = ["outcome": "failed", "failure": "unexpected test error"] }
      let url = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0].appendingPathComponent("results.json")
      try! JSONSerialization.data(withJSONObject: result, options: [.prettyPrinted, .sortedKeys]).write(to: url)
    }
  }
}

@main final class TestApp: UIResponder, UIApplicationDelegate {
  func application(_ application: UIApplication, configurationForConnecting session: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
    let configuration = UISceneConfiguration(name: "OverlayTests", sessionRole: session.role)
    configuration.delegateClass = TestScene.self
    return configuration
  }
}
