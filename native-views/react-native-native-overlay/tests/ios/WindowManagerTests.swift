import UIKit

private final class Owner: NativeOverlayWindowOwner, NativeOverlaySheetHost {
  var disconnects = 0
  var unexpected = 0
  var dismissOnPanDown = true
  func overlaySceneDidDisconnect() { disconnects += 1 }
  func sheetDidLoad(_ controller: NativeOverlaySheetController) {}
  func sheetDidLayout(_ controller: NativeOverlaySheetController) {}
  func sheetDidDismissInteractively(_ controller: NativeOverlaySheetController) {}
  func sheetDidDisappearUnexpectedly(_ controller: NativeOverlaySheetController) { unexpected += 1 }
  func sheetRequestedDismiss(_ controller: NativeOverlaySheetController, reason: String) {}
}

private struct Failure: Error { let name: String }
private func require(_ condition: @autoclosure () -> Bool, _ name: String) throws {
  if !condition() { throw Failure(name: name) }
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
  NotificationCenter.default.post(name: UIScene.didDisconnectNotification, object: scene)
  try require(b.disconnects == 1 && toast.disconnects == 1 && a.disconnects == 0, "disconnect targets only registered owners")
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
