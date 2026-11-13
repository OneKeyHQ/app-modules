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

private struct Failure: Error { let name: String }
private func require(_ condition: @autoclosure () -> Bool, _ name: String) throws {
  if !condition() { throw Failure(name: name) }
}

@MainActor private func nextMainTurn() async {
  await withCheckedContinuation { (done: CheckedContinuation<Void, Never>) in
    DispatchQueue.main.async { done.resume() }
  }
}

private func touchObserver(in window: UIWindow) throws -> NativeOverlayAppTouchObserver {
  guard let observer = window.gestureRecognizers?.compactMap({ $0 as? NativeOverlayAppTouchObserver }).first
  else { throw Failure(name: "app touch observer installed before overlay visibility") }
  return observer
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

  let touchManager = NativeOverlayWindowManager()
  let touchOwner = Owner()
  let touchWindow = touchManager.window(for: touchOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: app)!
  let observer = try touchObserver(in: app)
  let pan = UIPanGestureRecognizer()
  try require(!observer.cancelsTouchesInView && !observer.delaysTouchesBegan &&
    !observer.delaysTouchesEnded && !observer.canPrevent(pan) && !observer.canBePrevented(by: pan),
    "touch observer cannot delay, cancel, prevent or be prevented")
  let event = UIEvent()
  let firstTouch = UITouch()
  observer.touchesBegan([firstTouch], with: event)
  observer.touchesMoved([firstTouch], with: event)
  touchWindow.makeKey()
  let touchRoot = touchWindow.rootViewController
  touchManager.entryRemoved(from: touchWindow)
  try require(app.isKeyWindow && touchWindow.windowScene === scene &&
    touchWindow.rootViewController === touchRoot && !touchWindow.isHidden && observer.state == .possible,
    "touch-only removal keeps routing context visible while restoring key")
  observer.touchesEnded([firstTouch], with: event)
  try require(touchWindow.windowScene === scene, "touch completion waits for next main turn")
  await nextMainTurn()
  try require(touchWindow.windowScene == nil && touchWindow.rootViewController == nil &&
    observer.view == nil && observer.onTouchesFinished == nil,
    "last touch releases window and unregisters idle observer")
  passed.append("touch-window-retirement-and-passive-observer")

  let overlapA = Owner(), overlapB = Owner()
  let overlapWindowA = touchManager.window(for: overlapA, level: .modal,
    stackOrder: 1, blocking: true, appWindow: app)!
  let sharedObserver = try touchObserver(in: app)
  let overlapWindowB = touchManager.window(for: overlapB, level: .modal,
    stackOrder: 2, blocking: true, appWindow: overlapWindowA)!
  let nestedObserver = try touchObserver(in: app)
  try require(nestedObserver === sharedObserver &&
    app.gestureRecognizers?.filter({ $0 is NativeOverlayAppTouchObserver }).count == 1 &&
    overlapWindowA.gestureRecognizers?.contains(where: { $0 is NativeOverlayAppTouchObserver }) != true,
    "nested and multiple entries share only the real app window observer")
  let fingerA = UITouch(), fingerB = UITouch()
  sharedObserver.touchesBegan([fingerA, fingerB], with: event)
  let otherApp = UIWindow(windowScene: scene)
  otherApp.rootViewController = UIViewController()
  let otherOwner = Owner()
  let otherWindow = touchManager.window(for: otherOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: otherApp)!
  let otherObserver = try touchObserver(in: otherApp)
  touchManager.entryRemoved(from: otherWindow)
  try require(otherObserver !== sharedObserver && otherWindow.windowScene == nil &&
    otherObserver.view == nil && sharedObserver.view === app,
    "another app window in the same scene has an independent touch lifetime")
  otherApp.rootViewController = nil
  otherApp.windowScene = nil
  touchManager.entryRemoved(from: overlapWindowA)
  touchManager.entryRemoved(from: overlapWindowB)
  sharedObserver.touchesEnded([fingerA], with: event)
  await nextMainTurn()
  try require(overlapWindowA.windowScene === scene && overlapWindowB.windowScene === scene &&
    sharedObserver.hasActiveTouches, "one finger ending cannot release either retired entry")
  sharedObserver.touchesCancelled([fingerB], with: event)
  await nextMainTurn()
  try require(overlapWindowA.windowScene == nil && overlapWindowB.windowScene == nil &&
    sharedObserver.view == nil, "last finger cancellation releases all retired entries")
  passed.append("touch-window-retirement-shared-multitouch-cancel")

  let queuedOwner = Owner()
  let queuedWindow = touchManager.window(for: queuedOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: app)!
  let queuedObserver = try touchObserver(in: app)
  let endingTouch = UITouch(), nextTouch = UITouch()
  queuedObserver.touchesBegan([endingTouch], with: event)
  touchManager.entryRemoved(from: queuedWindow)
  queuedObserver.touchesEnded([endingTouch], with: event)
  queuedObserver.reset()
  queuedObserver.touchesBegan([nextTouch], with: event)
  await nextMainTurn()
  try require(queuedWindow.windowScene === scene && queuedObserver.hasActiveTouches,
    "queued cleanup rechecks a new touch beginning after the prior last UP")
  queuedObserver.touchesEnded([nextTouch], with: event)
  await nextMainTurn()
  try require(queuedWindow.windowScene == nil, "replacement touch completion releases old window")
  passed.append("touch-window-retirement-rechecks-current-touch-set")

  let resetOwner = Owner()
  let resetWindow = touchManager.window(for: resetOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: app)!
  let resetObserver = try touchObserver(in: app)
  resetObserver.touchesBegan([UITouch()], with: event)
  touchManager.entryRemoved(from: resetWindow)
  resetObserver.reset()
  await nextMainTurn()
  try require(resetWindow.windowScene == nil && resetObserver.view == nil,
    "UIKit reset of a live observer wakes pending retirement cleanup")
  passed.append("touch-window-retirement-reset-cleanup")

  let combinedApp = UIWindow(windowScene: scene)
  let combinedRoot = TransitionRoot()
  combinedApp.rootViewController = combinedRoot
  combinedApp.makeKeyAndVisible()
  let combinedOwner = Owner()
  let combinedWindow = touchManager.window(for: combinedOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: combinedApp)!
  let combinedObserver = try touchObserver(in: combinedApp)
  let combinedTouch = UITouch()
  let combinedCoordinator = TestTransitionCoordinator()
  combinedRoot.activeCoordinator = combinedCoordinator
  combinedObserver.touchesBegan([combinedTouch], with: event)
  touchManager.entryRemoved(from: combinedWindow)
  combinedCoordinator.changeInteraction(to: false)
  await nextMainTurn()
  try require(combinedWindow.windowScene === scene, "coordinator completion cannot release an active touch")
  combinedCoordinator.changeInteraction(to: true)
  combinedObserver.touchesCancelled([combinedTouch], with: event)
  await nextMainTurn()
  try require(combinedWindow.windowScene === scene && combinedCoordinator.subscriptions == 1,
    "touch cancellation cannot release a newly interactive coordinator")
  combinedCoordinator.changeInteraction(to: false, cancelled: true)
  await nextMainTurn()
  try require(combinedWindow.windowScene == nil && combinedObserver.view == nil,
    "combined ownership releases only after both current conditions finish")
  combinedApp.isHidden = true
  combinedApp.rootViewController = nil
  combinedApp.windowScene = nil
  app.makeKey()
  passed.append("touch-window-retirement-coordinator-conjunction")

  let reuseOwner = Owner()
  let reuseOldWindow = touchManager.window(for: reuseOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: app)!
  let reuseObserver = try touchObserver(in: app)
  let reuseTouch = UITouch()
  reuseObserver.touchesBegan([reuseTouch], with: event)
  touchManager.entryRemoved(from: reuseOldWindow)
  let reuseNewWindow = touchManager.window(for: reuseOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: app)!
  reuseObserver.touchesEnded([reuseTouch], with: event)
  await nextMainTurn()
  try require(reuseOldWindow.windowScene == nil && reuseNewWindow.windowScene === scene &&
    reuseObserver.view === app, "touch callback releases only the retired window, preserving reopened owner")
  touchManager.entryRemoved(from: reuseNewWindow)
  try require(reuseNewWindow.windowScene == nil && reuseObserver.view == nil,
    "touch-free noninteractive removal remains synchronous")
  passed.append("touch-window-retirement-owner-reuse-and-sync-removal")

  var orphanOwner: Owner? = Owner()
  let orphanWindow = touchManager.window(for: orphanOwner!, level: .modal,
    stackOrder: 1, blocking: true, appWindow: app)!
  let orphanObserver = try touchObserver(in: app)
  orphanOwner = nil
  let afterOrphanOwner = Owner()
  let afterOrphanWindow = touchManager.window(for: afterOrphanOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: app)!
  let afterOrphanObserver = try touchObserver(in: app)
  try require(orphanWindow.windowScene == nil && orphanObserver.view == nil &&
    afterOrphanObserver !== orphanObserver, "orphan cleanup precedes replacement observer installation")
  let afterOrphanTouch = UITouch()
  afterOrphanObserver.touchesBegan([afterOrphanTouch], with: event)
  touchManager.entryRemoved(from: afterOrphanWindow)
  try require(afterOrphanWindow.windowScene === scene, "replacement observer protects the new entry")
  afterOrphanObserver.touchesEnded([afterOrphanTouch], with: event)
  await nextMainTurn()
  passed.append("touch-window-retirement-orphan-replacement")

  let disconnectTouchOwner = Owner(), activeDisconnectOwner = Owner()
  let disconnectTouchWindow = touchManager.window(for: disconnectTouchOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: app)!
  let disconnectObserver = try touchObserver(in: app)
  let disconnectTouch = UITouch()
  disconnectObserver.touchesBegan([disconnectTouch], with: event)
  touchManager.entryRemoved(from: disconnectTouchWindow)
  disconnectObserver.touchesEnded([disconnectTouch], with: event)
  let activeDisconnectWindow = touchManager.window(for: activeDisconnectOwner, level: .modal,
    stackOrder: 2, blocking: true, appWindow: app)!
  var observerRemovedBeforeCallback = false
  activeDisconnectOwner.onDisconnect = { _ in
    observerRemovedBeforeCallback = disconnectObserver.view == nil && disconnectObserver.onTouchesFinished == nil
  }
  NotificationCenter.default.post(name: UIScene.didDisconnectNotification, object: scene)
  try require(observerRemovedBeforeCallback && disconnectTouchWindow.windowScene == nil &&
    activeDisconnectWindow.windowScene == nil, "disconnect unregisters observer before owner callbacks and releases retired windows")
  let afterDisconnectOwner = Owner()
  let afterDisconnectWindow = touchManager.window(for: afterDisconnectOwner, level: .modal,
    stackOrder: 1, blocking: true, appWindow: app)!
  await nextMainTurn()
  let afterDisconnectObserver = try touchObserver(in: app)
  try require(afterDisconnectWindow.windowScene === scene &&
    afterDisconnectObserver.view === app, "queued pre-disconnect touch callback cannot affect new registration")
  touchManager.entryRemoved(from: afterDisconnectWindow)
  passed.append("touch-window-retirement-disconnect-callback-identity")
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
