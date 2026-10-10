// Adapted from react-native-capture-protection 2.3.0 by lethe; see LICENSE.upstream.
import Foundation
import NitroModules
import React
import UIKit

class ReactNativeCaptureProtection: HybridReactNativeCaptureProtectionSpec {
  private let owner = UUID()
  private var listener: ((Double) -> Void)?

  func prevent() throws -> Promise<Void> {
    let promise = Promise<Void>()
    DispatchQueue.main.async {
      do {
        try CaptureProtectionCoordinator.shared.prevent(owner: self.owner)
        promise.resolve(withResult: ())
      } catch {
        promise.reject(withError: error)
      }
    }
    return promise
  }

  func allow() throws -> Promise<Void> {
    let promise = Promise<Void>()
    DispatchQueue.main.async {
      CaptureProtectionCoordinator.shared.allow(owner: self.owner)
      promise.resolve(withResult: ())
    }
    return promise
  }

  func setListener(callback: ((Double) -> Void)?) throws {
    try onMain {
      let coordinator = CaptureProtectionCoordinator.shared
      if let callback {
        try coordinator.subscribe(owner: self.owner) { [weak self] event in
          self?.listener?(event)
        }
        self.listener = callback
        coordinator.emitRecordingState(owner: self.owner)
      } else {
        self.listener = nil
        coordinator.unsubscribe(owner: self.owner)
      }
    }
  }

  func dispose() {
    DispatchQueue.main.async {
      self.listener = nil
      CaptureProtectionCoordinator.shared.unsubscribe(owner: self.owner)
      CaptureProtectionCoordinator.shared.allow(owner: self.owner)
    }
  }

  deinit {
    let identifier = owner
    DispatchQueue.main.async {
      CaptureProtectionCoordinator.shared.unsubscribe(owner: identifier)
      CaptureProtectionCoordinator.shared.allow(owner: identifier)
    }
  }

  private func onMain(_ operation: () throws -> Void) rethrows {
    if Thread.isMainThread {
      try operation()
    } else {
      try DispatchQueue.main.sync(execute: operation)
    }
  }
}

private final class CaptureProtectionCoordinator {
  static let shared = CaptureProtectionCoordinator()
  private var owners = Set<UUID>()
  private var listeners: [UUID: (Double) -> Void] = [:]
  private var observers: [NSObjectProtocol] = []
  private var secureField: UITextField?
  private weak var protectedWindow: UIWindow?
  private var recordWindow: UIWindow?
  private var switcherWindow: UIWindow?
  private var recording = false

  private var window: UIWindow? {
    if let delegated = UIApplication.shared.delegate?.window ?? nil { return delegated }
    return UIApplication.shared.connectedScenes.compactMap { $0 as? UIWindowScene }
      .flatMap { $0.windows }.first { $0.isKeyWindow }
  }

  private func checkCapacity(_ owner: UUID) throws {
    let identities = owners.union(listeners.keys)
    if !identities.contains(owner) && identities.count >= 32 {
      throw failure("Capture protection supports at most 32 native owners.")
    }
  }

  func prevent(owner: UUID) throws {
    try checkCapacity(owner)
    guard let window else { throw failure("No application window is available.") }
    if owners.isEmpty {
      try secureScreenshot(window: window)
    }
    owners.insert(owner)
    startObservers()
    refreshRecording()
    if UIApplication.shared.applicationState != .active { showSwitcher() }
  }

  func allow(owner: UUID) {
    owners.remove(owner)
    if owners.isEmpty {
      secureField?.isSecureTextEntry = false
      removeRecordWindow()
      removeSwitcherWindow()
    }
    stopObserversIfIdle()
  }

  func subscribe(owner: UUID, callback: @escaping (Double) -> Void) throws {
    try checkCapacity(owner)
    listeners[owner] = callback
    startObservers()
  }

  func unsubscribe(owner: UUID) {
    listeners.removeValue(forKey: owner)
    stopObserversIfIdle()
  }

  func emitRecordingState(owner: UUID) {
    if UIScreen.main.isCaptured { listeners[owner]?(1) }
  }

  private func emit(_ event: Double) {
    for (owner, callback) in Array(listeners) where listeners[owner] != nil {
      callback(event)
    }
  }

  private func startObservers() {
    guard observers.isEmpty else { return }
    let center = NotificationCenter.default
    recording = UIScreen.main.isCaptured
    observers.append(center.addObserver(
      forName: UIApplication.userDidTakeScreenshotNotification, object: nil, queue: .main
    ) { [weak self] _ in self?.emit(3) })
    observers.append(center.addObserver(
      forName: UIScreen.capturedDidChangeNotification, object: nil, queue: .main
    ) { [weak self] _ in self?.refreshRecording() })
    for name in [UIApplication.willResignActiveNotification, UIApplication.didEnterBackgroundNotification] {
      observers.append(center.addObserver(forName: name, object: nil, queue: .main) {
        [weak self] _ in
        self?.showSwitcher()
      })
    }
    observers.append(center.addObserver(
      forName: UIApplication.didBecomeActiveNotification, object: nil, queue: .main
    ) { [weak self] _ in
      self?.removeSwitcherWindow()
      self?.refreshRecording()
    })
    observers.append(center.addObserver(
      forName: NSNotification.Name.RCTTriggerReloadCommand, object: nil, queue: .main
    ) { [weak self] _ in self?.reset() })
  }

  private func stopObserversIfIdle() {
    guard owners.isEmpty && listeners.isEmpty else { return }
    for observer in observers { NotificationCenter.default.removeObserver(observer) }
    observers.removeAll()
  }

  private func reset() {
    owners.removeAll()
    listeners.removeAll()
    secureField?.isSecureTextEntry = false
    removeRecordWindow()
    removeSwitcherWindow()
    stopObserversIfIdle()
  }

  private func refreshRecording() {
    let captured = UIScreen.main.isCaptured
    if captured != recording {
      recording = captured
      emit(captured ? 1 : 2)
    }
    if captured && !owners.isEmpty {
      if recordWindow == nil { recordWindow = makeOverlay() }
    } else {
      removeRecordWindow()
    }
  }

  private func secureScreenshot(window: UIWindow) throws {
    if let secureField, protectedWindow === window {
      secureField.isSecureTextEntry = true
      return
    }
    let field = UITextField(frame: .zero)
    field.isUserInteractionEnabled = false
    field.isSecureTextEntry = true
    field.layoutIfNeeded()
    guard let canvas = field.layer.sublayers?.last else {
      throw failure("The secure text layer is unavailable.")
    }
    // Keep the upstream secure-layer technique; no photo or file access is involved.
    window.layer.superlayer?.addSublayer(field.layer)
    canvas.addSublayer(window.layer)
    secureField?.isSecureTextEntry = false
    secureField = field
    protectedWindow = window
  }

  private func makeOverlay() -> UIWindow? {
    guard let protectedWindow = protectedWindow ?? window else { return nil }
    let overlay: UIWindow
    if let scene = protectedWindow.windowScene {
      overlay = UIWindow(windowScene: scene)
      overlay.frame = protectedWindow.frame
    } else {
      overlay = UIWindow(frame: protectedWindow.frame)
    }
    let controller = UIViewController()
    controller.view.backgroundColor = .white
    overlay.rootViewController = controller
    overlay.windowLevel = .alert + 1
    overlay.isHidden = false
    return overlay
  }

  private func showSwitcher() {
    guard !owners.isEmpty else { return }
    if switcherWindow == nil { switcherWindow = makeOverlay() }
  }

  private func removeRecordWindow() {
    recordWindow?.isHidden = true
    recordWindow = nil
  }

  private func removeSwitcherWindow() {
    switcherWindow?.isHidden = true
    switcherWindow = nil
  }

  private func failure(_ message: String) -> NSError {
    NSError(domain: "CaptureProtection", code: 1, userInfo: [NSLocalizedDescriptionKey: message])
  }
}
