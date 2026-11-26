import Foundation
import UIKit
import ReactNativeNativeLogger

/// Tracks the interface orientation of the key window's scene.
///
/// UIKit reports it through the scene's `effectiveGeometry` (iOS 16+), which
/// changes for every rotation the scene takes, including one that a presented
/// view controller forces while the root view controller keeps its own
/// orientation. The device orientation cannot stand in for it: the window can
/// turn to landscape while the device lies flat or is held upright.
final class InterfaceOrientationObserver {
    static let shared = InterfaceOrientationObserver(observesKeyWindowScene: true)

    private struct Listener {
        let owner: ObjectIdentifier
        let callback: (InterfaceOrientation) -> Void
    }

    private let lock = NSLock()
    private var orientation: InterfaceOrientation = .unknown
    private var listeners: [Double: Listener] = [:]
    private var nextListenerId: Double = 1
    private var keyWindowObserver: NSObjectProtocol?
    private var sceneDisconnectObserver: NSObjectProtocol?
    private weak var observedScene: UIWindowScene?
    private var geometryObservation: NSKeyValueObservation?

    /// Tests create an observer that only stores what `receive` hands it.
    init(observesKeyWindowScene: Bool) {
        guard observesKeyWindowScene else { return }
        DispatchQueue.main.async { [weak self] in
            self?.install()
        }
    }

    var currentOrientation: InterfaceOrientation {
        lock.lock()
        defer { lock.unlock() }
        return orientation
    }

    /// `owner` is the DeviceUtils HybridObject of the JS runtime that
    /// subscribes; it removes its listeners when the runtime releases it.
    func addListener(_ callback: @escaping (InterfaceOrientation) -> Void, owner: AnyObject) -> Double {
        lock.lock()
        defer { lock.unlock() }
        let id = nextListenerId
        nextListenerId += 1
        listeners[id] = Listener(owner: ObjectIdentifier(owner), callback: callback)
        return id
    }

    func removeListener(_ id: Double) {
        lock.lock()
        defer { lock.unlock() }
        listeners.removeValue(forKey: id)
    }

    func removeListeners(ownedBy owner: AnyObject) {
        let ownerId = ObjectIdentifier(owner)
        lock.lock()
        defer { lock.unlock() }
        listeners = listeners.filter { $0.value.owner != ownerId }
    }

    /// Stores a scene orientation and notifies listeners when the value changed.
    func receive(_ interfaceOrientation: UIInterfaceOrientation) {
        store(InterfaceOrientationObserver.orientation(from: interfaceOrientation))
    }

    static func orientation(from interfaceOrientation: UIInterfaceOrientation) -> InterfaceOrientation {
        switch interfaceOrientation {
        case .portrait:
            return .portrait
        case .portraitUpsideDown:
            return .portraitupsidedown
        case .landscapeLeft:
            return .landscapeleft
        case .landscapeRight:
            return .landscaperight
        default:
            return .unknown
        }
    }

    private func install(in keyWindow: UIWindow? = nil) {
        guard #available(iOS 16.0, *) else { return }
        if keyWindowObserver == nil {
            keyWindowObserver = NotificationCenter.default.addObserver(
                forName: UIWindow.didBecomeKeyNotification,
                object: nil,
                queue: .main
            ) { [weak self] notification in
                guard let window = notification.object as? UIWindow else { return }
                self?.install(in: window)
            }
        }
        if sceneDisconnectObserver == nil {
            // A window of another scene can stay key without becoming key again.
            sceneDisconnectObserver = NotificationCenter.default.addObserver(
                forName: UIScene.didDisconnectNotification,
                object: nil,
                queue: .main
            ) { [weak self] notification in
                guard let self, let scene = notification.object as? UIWindowScene,
                      scene === self.observedScene else { return }
                self.observedScene = nil
                self.geometryObservation = nil
                // The departing scene can still be listed as connected.
                if let window = InterfaceOrientationObserver.keyWindow(excluding: scene) {
                    self.install(in: window)
                } else {
                    self.store(.unknown)
                }
            }
        }
        guard let scene = (keyWindow ?? InterfaceOrientationObserver.keyWindow())?.windowScene,
              scene !== observedScene else { return }
        observedScene = scene
        // Starts with the current value; later changes arrive on the main thread.
        geometryObservation = scene.observe(\.effectiveGeometry, options: [.initial, .new]) { [weak self] scene, _ in
            // A scene the observer has moved away from must not overwrite the value.
            guard let self, scene === self.observedScene else { return }
            self.receive(scene.effectiveGeometry.interfaceOrientation)
        }
    }

    private func store(_ next: InterfaceOrientation) {
        lock.lock()
        let didChange = next != orientation
        if didChange {
            orientation = next
        }
        let callbacks = didChange ? listeners.values.map(\.callback) : []
        lock.unlock()

        guard didChange else { return }
        OneKeyLog.debug("DeviceUtils", "Interface orientation: \(next.stringValue)")
        callbacks.forEach { $0(next) }
    }

    private static func keyWindow(excluding excludedScene: UIScene? = nil) -> UIWindow? {
        UIApplication.shared.connectedScenes
            .filter { $0 !== excludedScene }
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }
    }
}
