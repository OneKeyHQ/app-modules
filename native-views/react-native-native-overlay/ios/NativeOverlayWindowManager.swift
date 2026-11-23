import UIKit
import ObjectiveC

/// IDs are process-local and weakly keyed; never log controller descriptions or addresses.
enum NativeOverlayDiagnostic {
  private static var nextID = 0
  private static let controllerIDs = NSMapTable<UIViewController, NSNumber>.weakToStrongObjects()

  static func id(_ controller: UIViewController?) -> Int {
    guard let controller else { return 0 }
    if let existing = controllerIDs.object(forKey: controller) { return existing.intValue }
    nextID += 1
    controllerIDs.setObject(NSNumber(value: nextID), forKey: controller)
    return nextID
  }

  static func log(_ details: String) {
    guard let logClass = NSClassFromString("ReactNativeNativeLogger.OneKeyLog")
      ?? NSClassFromString("OneKeyLog"),
      let method = class_getClassMethod(logClass, NSSelectorFromString("info::")) else { return }
    typealias LogFunction = @convention(c) (AnyClass, Selector, NSString, NSString) -> Void
    let log = unsafeBitCast(method_getImplementation(method), to: LogFunction.self)
    log(logClass, NSSelectorFromString("info::"), "NativeOverlay",
        "wallTimeMs=\(Int64(Date().timeIntervalSince1970 * 1000)) \(details)" as NSString)
  }
}

/// Levels mirror `OVERLAY_LEVEL_ORDER`; each level reserves a window-level band.
enum NativeOverlayLevel: String, CaseIterable {
  case modal
  case hardware
  case secure
  case toast
  case lock
  case debug

  /// Mirrors `OVERLAY_LEVEL_ORDER`.
  var order: Int {
    switch self {
    case .modal: return 100
    case .hardware: return 200
    case .secure: return 300
    case .toast: return 400
    case .lock: return 500
    case .debug: return 900
    }
  }

  var windowLevel: UIWindow.Level {
    switch self {
    // Above the app window (and every VC it presents), below system alerts.
    case .modal: return UIWindow.Level(rawValue: UIWindow.Level.normal.rawValue + 10)
    case .hardware: return UIWindow.Level(rawValue: UIWindow.Level.normal.rawValue + 20)
    case .secure: return UIWindow.Level(rawValue: UIWindow.Level.normal.rawValue + 30)
    case .toast: return UIWindow.Level(rawValue: UIWindow.Level.normal.rawValue + 40)
    // Above React Native's Alert window (alert + 1) and perf overlays.
    case .lock: return UIWindow.Level(rawValue: UIWindow.Level.alert.rawValue + 10)
    case .debug: return UIWindow.Level(rawValue: UIWindow.Level.alert.rawValue + 20)
    }
  }
}

/// Lets touches that miss every overlay fall through to lower windows.
final class NativeOverlayPassthroughWindow: UIWindow {
  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let hit = super.hitTest(point, with: event)
    if hit === self || hit === rootViewController?.view {
      return nil
    }
    return hit
  }

  // React Native reads the window dimensions from the key window, and a
  // touched level window becomes key. A rotation reported while it is key
  // (or while it is hidden with a stale frame) leaves JS with the wrong
  // window size. Once the sizes settle, re-report through the frame
  // observer React Native keeps on the app window.
  override var frame: CGRect {
    didSet {
      if isKeyWindow && oldValue.size != frame.size {
        refreshReactNativeDimensions()
      }
    }
  }

  override func resignKey() {
    super.resignKey()
    refreshReactNativeDimensions()
  }

  private func refreshReactNativeDimensions() {
    guard let appWindow = (rootViewController as? NativeOverlayRootViewController)?.appWindow
    else { return }
    DispatchQueue.main.async { [weak appWindow] in
      guard let appWindow else { return }
      // Assigning the frame fires React Native's KVO, which recomputes the
      // dimensions from the current key window.
      let frame = appWindow.frame
      appWindow.frame = frame
    }
  }
}

/// Status bar and orientation follow the app window underneath, because
/// `childForStatusBarStyle` cannot reach across windows.
final class NativeOverlayRootViewController: UIViewController {
  weak var appWindow: UIWindow?

  private var appTopController: UIViewController? {
    var controller = appWindow?.rootViewController
    while let presented = controller?.presentedViewController, !presented.isBeingDismissed {
      controller = presented
    }
    // Never answer with ourselves: asking it would recurse forever.
    return controller === self ? nil : controller
  }

  override func loadView() {
    let root = UIView()
    root.backgroundColor = .clear
    view = root
  }

  /// Apps without view-controller-based status bar appearance set one
  /// app-wide style (React Native's StatusBar does). Level windows above the
  /// status bar (`lock`, `debug`) still get asked, so answer with that style.
  private static let viewControllerBasedAppearance =
    (Bundle.main.object(forInfoDictionaryKey: "UIViewControllerBasedStatusBarAppearance") as? Bool) ?? true

  override var preferredStatusBarStyle: UIStatusBarStyle {
    if !Self.viewControllerBasedAppearance {
      return UIApplication.shared.statusBarStyle
    }
    return appTopController?.preferredStatusBarStyle ?? .default
  }

  override var prefersStatusBarHidden: Bool {
    appTopController?.prefersStatusBarHidden ?? false
  }

  override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
    appTopController?.supportedInterfaceOrientations ?? .all
  }
}

protocol NativeOverlayWindowOwner: AnyObject {
  func overlaySceneDidDisconnect(window: UIWindow)
}

/// Observes delivered window touches without participating in gesture arbitration.
final class NativeOverlayAppTouchObserver: UIGestureRecognizer {
  var onTouchesFinished: (() -> Void)?
  private var activeTouches = Set<ObjectIdentifier>()
  var hasActiveTouches: Bool { !activeTouches.isEmpty }

  override init(target: Any?, action: Selector?) {
    super.init(target: target, action: action)
    configure()
  }

  private func configure() {
    cancelsTouchesInView = false
    delaysTouchesBegan = false
    delaysTouchesEnded = false
    requiresExclusiveTouchType = false
  }

  override func canPrevent(_ preventedGestureRecognizer: UIGestureRecognizer) -> Bool { false }
  override func canBePrevented(by preventingGestureRecognizer: UIGestureRecognizer) -> Bool { false }

  override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent) {
    activeTouches.formUnion(touches.map(ObjectIdentifier.init))
  }

  override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent) {}

  override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent) {
    finish(touches)
  }

  override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent) {
    finish(touches)
  }

  private func finish(_ touches: Set<UITouch>) {
    let hadTouches = hasActiveTouches
    activeTouches.subtract(touches.map(ObjectIdentifier.init))
    guard hadTouches, !hasActiveTouches else { return }
    state = .failed
    onTouchesFinished?()
  }

  override func reset() {
    super.reset()
    let hadTouches = hasActiveTouches
    activeTouches.removeAll()
    if hadTouches { onTouchesFinished?() }
  }
}

final class NativeOverlayWindowManager: NSObject {
  static let shared = NativeOverlayWindowManager()

  private final class Entry {
    weak var owner: NativeOverlayWindowOwner?
    weak var appWindow: UIWindow?
    let window: NativeOverlayPassthroughWindow
    let sequence: Int
    let level: NativeOverlayLevel
    var stackOrder: Int
    var blocking: Bool
    var desiredVisible = true
    var closing = false

    init(owner: NativeOverlayWindowOwner, appWindow: UIWindow,
         window: NativeOverlayPassthroughWindow, sequence: Int,
         level: NativeOverlayLevel, stackOrder: Int, blocking: Bool) {
      self.owner = owner
      self.appWindow = appWindow
      self.window = window
      self.sequence = sequence
      self.level = level
      self.stackOrder = stackOrder
      self.blocking = blocking
    }
  }

  private final class RetiredWindow {
    let window: NativeOverlayPassthroughWindow
    weak var appWindow: UIWindow?
    weak var observedCoordinator: AnyObject?

    init(window: NativeOverlayPassthroughWindow, appWindow: UIWindow?) {
      self.window = window
      self.appWindow = appWindow
    }
  }

  private final class WeakAppWindow {
    weak var window: UIWindow?
    init(_ window: UIWindow) { self.window = window }
  }

  private final class AppTouchObservation {
    weak var window: UIWindow?
    weak var appWindow: UIWindow?
    let observer: NativeOverlayAppTouchObserver
    var previousAppWindows: [ObjectIdentifier: WeakAppWindow] = [:]

    var appWindows: [UIWindow] {
      (appWindow.map { [$0] } ?? []) + previousAppWindows.values.compactMap(\.window)
    }

    init(window: UIWindow, appWindow: UIWindow, observer: NativeOverlayAppTouchObserver) {
      self.window = window
      self.appWindow = appWindow
      self.observer = observer
    }
  }

  private var scenes: [ObjectIdentifier: [ObjectIdentifier: Entry]] = [:]
  private var retiredWindows: [ObjectIdentifier: [ObjectIdentifier: RetiredWindow]] = [:]
  private var appTouchObservations: [ObjectIdentifier: AppTouchObservation] = [:]
  private var disconnectingScenes = Set<ObjectIdentifier>()
  private var sequence = 0
  private let sceneIdentity: (UIWindowScene) -> ObjectIdentifier

  override convenience init() {
    self.init(sceneIdentity: { ObjectIdentifier($0) })
  }

  // Internal identity projection lets native tests exercise scene ownership with real windows.
  init(sceneIdentity: @escaping (UIWindowScene) -> ObjectIdentifier) {
    self.sceneIdentity = sceneIdentity
    super.init()
    NotificationCenter.default.addObserver(
      self, selector: #selector(sceneDidDisconnect(_:)),
      name: UIScene.didDisconnectNotification, object: nil
    )
  }

  deinit {
    NotificationCenter.default.removeObserver(self)
    for observation in appTouchObservations.values {
      observation.observer.onTouchesFinished = nil
      observation.window?.removeGestureRecognizer(observation.observer)
    }
  }

  /// Every owner has an independent root presenter, shared by no other entry.
  func window(for owner: NativeOverlayWindowOwner, level: NativeOverlayLevel,
              stackOrder: Int, blocking: Bool, appWindow hostWindow: UIWindow) -> UIWindow? {
    precondition(Thread.isMainThread)
    let appWindow =
      ((hostWindow as? NativeOverlayPassthroughWindow)?.rootViewController
        as? NativeOverlayRootViewController)?.appWindow ?? hostWindow
    guard let scene = appWindow.windowScene, scene.activationState != .unattached else { return nil }
    let sceneKey = sceneIdentity(scene)
    guard !disconnectingScenes.contains(sceneKey) else { return nil }
    let orphanWindows = (scenes[sceneKey] ?? [:]).values.filter { $0.owner == nil }.map(\.window)
    orphanWindows.forEach { entryRemoved(from: $0) }
    observeTouches(in: appWindow, appWindow: appWindow)
    let ownerKey = ObjectIdentifier(owner)
    var entries = scenes[sceneKey] ?? [:]
    let entry: Entry
    if let existing = entries[ownerKey], existing.owner === owner {
      entry = existing
      entry.stackOrder = stackOrder
      entry.blocking = blocking
    } else {
      sequence += 1
      let window = NativeOverlayPassthroughWindow(windowScene: scene)
      window.backgroundColor = .clear
      let root = NativeOverlayRootViewController()
      root.appWindow = appWindow
      window.rootViewController = root
      window.frame = scene.coordinateSpace.bounds
      entry = Entry(owner: owner, appWindow: appWindow, window: window,
                    sequence: sequence, level: level, stackOrder: stackOrder, blocking: blocking)
      entries[ownerKey] = entry
      scenes[sceneKey] = entries
    }
    entry.appWindow = appWindow
    (entry.window.rootViewController as? NativeOverlayRootViewController)?.appWindow = appWindow
    observeTouches(in: entry.window, appWindow: appWindow)
    reorder(entries)
    entry.window.isHidden = false
    entry.window.rootViewController?.setNeedsStatusBarAppearanceUpdate()
    removeUnusedTouchObservers()
    return entry.window
  }

  func update(_ window: UIWindow?, stackOrder: Int,
              blocking: Bool, visible: Bool) {
    precondition(Thread.isMainThread)
    guard let (sceneKey, _, entry) = find(window) else { return }
    let orderChanged = entry.stackOrder != stackOrder
    entry.stackOrder = stackOrder
    entry.blocking = blocking
    entry.desiredVisible = visible
    if orderChanged { reorder(scenes[sceneKey] ?? [:]) }
  }

  func setClosing(_ closing: Bool, window: UIWindow?) {
    precondition(Thread.isMainThread)
    find(window)?.2.closing = closing
  }

  func presenter(in window: UIWindow) -> UIViewController? {
    window.rootViewController
  }

  func entryRemoved(from window: UIWindow?) {
    precondition(Thread.isMainThread)
    guard let (sceneKey, ownerKey, entry) = find(window) else { return }
    let wasKey = entry.window.isKeyWindow
    let scene = entry.window.windowScene
    scenes[sceneKey]?[ownerKey] = nil
    let remaining = scenes[sceneKey] ?? [:]
    if remaining.isEmpty { scenes[sceneKey] = nil }
    if hasActiveTouches(in: entry.appWindow) || Self.interactiveCoordinator(in: entry.appWindow) != nil {
      // Keep the routing context until this host's delivered touches and UIKit interactions finish.
      let retirement = RetiredWindow(window: entry.window, appWindow: entry.appWindow)
      let windowKey = ObjectIdentifier(entry.window)
      retiredWindows[sceneKey, default: [:]][windowKey] = retirement
      finishRetirement(retirement, sceneKey: sceneKey, windowKey: windowKey)
    } else {
      release(entry.window)
    }
    removeUnusedTouchObservers()
    reorder(remaining)
    guard wasKey, let scene,
          scene.activationState == .foregroundActive || scene.activationState == .foregroundInactive else { return }
    let next = remaining.values.filter { Self.eligible($0, in: scene) }
      .max { Self.ordered($0, before: $1) }
    let appWindow = entry.appWindow?.windowScene === scene ? entry.appWindow : nil
    (next?.window ?? appWindow)?.makeKey()
  }

  func restoreFocus(_ responder: UIView, in window: UIWindow) -> Bool {
    precondition(Thread.isMainThread)
    guard let (sceneKey, _, entry) = find(window),
          let scene = window.windowScene, scene.activationState == .foregroundActive,
          responder.window === window, responder.canBecomeFirstResponder else { return false }
    let entries = scenes[sceneKey] ?? [:]
    guard entries.values.filter({ Self.eligible($0, in: scene) })
      .max(by: { Self.ordered($0, before: $1) }) === entry,
      let key = scene.windows.first(where: { $0.isKeyWindow }) else { return false }
    if key !== entry.appWindow {
      guard let keyEntry = entries.values.first(where: { $0.window === key && $0.owner != nil }),
            !Self.ordered(entry, before: keyEntry) else { return false }
    }
    if let current = Self.firstResponder(in: window), current !== responder { return false }
    window.makeKey()
    return responder.becomeFirstResponder()
  }

  private static func eligible(_ entry: Entry, in scene: UIWindowScene) -> Bool {
    entry.owner != nil && entry.desiredVisible && !entry.closing &&
      !entry.window.isHidden && entry.window.windowScene === scene &&
      (entry.blocking || firstResponder(in: entry.window) != nil)
  }

  private func find(_ window: UIWindow?) -> (ObjectIdentifier, ObjectIdentifier, Entry)? {
    guard let window else { return nil }
    for (sceneKey, entries) in scenes {
      if let (ownerKey, entry) = entries.first(where: { $0.value.window === window }) {
        return (sceneKey, ownerKey, entry)
      }
    }
    return nil
  }

  private static func ordered(_ a: Entry, before b: Entry) -> Bool {
    if a.level.order != b.level.order { return a.level.order < b.level.order }
    if a.stackOrder != b.stackOrder { return a.stackOrder < b.stackOrder }
    return a.sequence < b.sequence
  }

  private func reorder(_ entries: [ObjectIdentifier: Entry]) {
    let levels = Dictionary(grouping: entries.values, by: { $0.level })
    for (level, group) in levels {
      let sorted = group.sorted { Self.ordered($0, before: $1) }
      for (index, entry) in sorted.enumerated() {
        // A bounded rank cannot cross another band's base, even for large seq values.
        let rank = CGFloat(index) / CGFloat(sorted.count + 1)
        entry.window.windowLevel = UIWindow.Level(rawValue: level.windowLevel.rawValue + rank)
      }
    }
  }

  static func firstResponder(in view: UIView) -> UIView? {
    if view.isFirstResponder { return view }
    for child in view.subviews {
      if let responder = firstResponder(in: child) { return responder }
    }
    return nil
  }

  private func release(_ window: UIWindow) {
    window.isHidden = true
    window.rootViewController = nil
    window.windowScene = nil
  }

  private func observeTouches(in window: UIWindow, appWindow: UIWindow) {
    let windowKey = ObjectIdentifier(window)
    if let observation = appTouchObservations[windowKey] {
      if observation.appWindow !== appWindow, let previous = observation.appWindow {
        // Reparenting does not change the context of an already delivered or queued touch stream.
        if observation.observer.hasActiveTouches || hasRetirements(for: previous) {
          observation.previousAppWindows[ObjectIdentifier(previous)] = WeakAppWindow(previous)
        }
        observation.appWindow = appWindow
        scheduleObservationCleanup(observation, windowKey: windowKey)
        scheduleRetirementCheck(for: previous)
      } else {
        observation.appWindow = appWindow
      }
      return
    }
    let observer = NativeOverlayAppTouchObserver(target: nil, action: nil)
    appTouchObservations[windowKey] = AppTouchObservation(
      window: window, appWindow: appWindow, observer: observer)
    observer.onTouchesFinished = { [weak self, weak window, weak observer] in
      guard let self, let window, let observer,
            let observation = self.appTouchObservations[ObjectIdentifier(window)],
            observation.observer === observer else { return }
      self.scheduleObservationCleanup(observation, windowKey: ObjectIdentifier(window))
      for appWindow in observation.appWindows { self.scheduleRetirementCheck(for: appWindow) }
    }
    window.addGestureRecognizer(observer)
  }

  private func scheduleObservationCleanup(_ observation: AppTouchObservation, windowKey: ObjectIdentifier) {
    guard !observation.previousAppWindows.isEmpty else { return }
    DispatchQueue.main.async { [weak self, weak observation] in
      guard let self, let observation, self.appTouchObservations[windowKey] === observation,
            !observation.observer.hasActiveTouches else { return }
      self.prunePreviousAppWindows(in: observation)
    }
  }

  private func prunePreviousAppWindows(in observation: AppTouchObservation) {
    guard !observation.observer.hasActiveTouches else { return }
    // An idle gap does not retire the old context if another touch or transition still owns it.
    observation.previousAppWindows = observation.previousAppWindows.filter {
      $0.value.window.map { hasRetirements(for: $0) } ?? false
    }
  }

  private func hasRetirements(for appWindow: UIWindow) -> Bool {
    retiredWindows.values.contains { $0.values.contains { $0.appWindow === appWindow } }
  }

  private func scheduleRetirementCheck(for appWindow: UIWindow?) {
    guard let appWindow, hasRetirements(for: appWindow) else { return }
    DispatchQueue.main.async { [weak self, weak appWindow] in
      guard let self, let appWindow else { return }
      // The source observer may be gone; query current host ownership and all delivered touches.
      for (sceneKey, windows) in self.retiredWindows {
        for (windowKey, retirement) in windows where retirement.appWindow === appWindow {
          self.finishRetirement(retirement, sceneKey: sceneKey, windowKey: windowKey)
        }
      }
    }
  }

  private func hasActiveTouches(in appWindow: UIWindow?) -> Bool {
    guard let appWindow else { return false }
    return appTouchObservations.values.contains {
      $0.observer.hasActiveTouches && ($0.appWindow === appWindow ||
        $0.previousAppWindows[ObjectIdentifier(appWindow)]?.window === appWindow)
    }
  }

  private func removeUnusedTouchObservers() {
    for (key, observation) in appTouchObservations {
      prunePreviousAppWindows(in: observation)
      let active = observation.window.map { window in
        scenes.values.contains { $0.values.contains { $0.window === window || $0.appWindow === window } } ||
          retiredWindows.values.contains { $0.values.contains { $0.window === window || $0.appWindow === window } }
      } ?? false
      guard !active else { continue }
      observation.observer.onTouchesFinished = nil
      observation.window?.removeGestureRecognizer(observation.observer)
      appTouchObservations[key] = nil
      for appWindow in observation.appWindows { scheduleRetirementCheck(for: appWindow) }
    }
  }

  private static func interactiveCoordinator(in appWindow: UIWindow?) -> UIViewControllerTransitionCoordinator? {
    var controller = appWindow?.rootViewController
    while let current = controller {
      if let coordinator = current.transitionCoordinator, coordinator.isInteractive {
        return coordinator
      }
      controller = current.presentedViewController
    }
    return nil
  }

  private func finishRetirement(_ retirement: RetiredWindow, sceneKey: ObjectIdentifier,
                                windowKey: ObjectIdentifier) {
    guard retiredWindows[sceneKey]?[windowKey] === retirement else { return }
    let coordinator = Self.interactiveCoordinator(in: retirement.appWindow)
    if coordinator == nil && !hasActiveTouches(in: retirement.appWindow) {
      retiredWindows[sceneKey]?[windowKey] = nil
      if retiredWindows[sceneKey]?.isEmpty == true { retiredWindows[sceneKey] = nil }
      release(retirement.window)
      removeUnusedTouchObservers()
      return
    }
    guard let coordinator else { return }
    guard retirement.observedCoordinator !== coordinator else { return }
    retirement.observedCoordinator = coordinator
    coordinator.notifyWhenInteractionChanges { [weak self, weak retirement] context in
      guard !context.isInteractive else { return }
      DispatchQueue.main.async { [weak self, weak retirement] in
        guard let self, let retirement,
              self.retiredWindows[sceneKey]?[windowKey] === retirement else { return }
        self.finishRetirement(retirement, sceneKey: sceneKey, windowKey: windowKey)
      }
    }
  }

  @objc private func sceneDidDisconnect(_ notification: Notification) {
    precondition(Thread.isMainThread)
    guard let scene = notification.object as? UIWindowScene else { return }
    let sceneKey = sceneIdentity(scene)
    let entries = scenes.removeValue(forKey: sceneKey) ?? [:]
    let retired = retiredWindows.removeValue(forKey: sceneKey) ?? [:]
    guard !entries.isEmpty || !retired.isEmpty else { return }
    disconnectingScenes.insert(sceneKey)
    defer { disconnectingScenes.remove(sceneKey) }
    removeUnusedTouchObservers()
    // Unregister first: owner callbacks must not restore a disconnected scene's key.
    for entry in entries.values {
      entry.owner?.overlaySceneDidDisconnect(window: entry.window)
      release(entry.window)
    }
    for retirement in retired.values { release(retirement.window) }
  }
}
