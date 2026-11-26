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
    static let shared = InterfaceOrientationObserver()

    private let lock = NSLock()
    private var orientation: InterfaceOrientation = .unknown
    private var listeners: [Double: (InterfaceOrientation) -> Void] = [:]
    private var nextListenerId: Double = 1
    private var keyWindowObserver: NSObjectProtocol?
    private weak var observedScene: UIWindowScene?
    private var geometryObservation: NSKeyValueObservation?

    private init() {
        DispatchQueue.main.async { [weak self] in
            self?.install()
        }
    }

    var currentOrientation: InterfaceOrientation {
        lock.lock()
        defer { lock.unlock() }
        return orientation
    }

    func addListener(_ callback: @escaping (InterfaceOrientation) -> Void) -> Double {
        lock.lock()
        defer { lock.unlock() }
        let id = nextListenerId
        nextListenerId += 1
        listeners[id] = callback
        return id
    }

    func removeListener(_ id: Double) {
        lock.lock()
        defer { lock.unlock() }
        listeners.removeValue(forKey: id)
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
        guard let scene = (keyWindow ?? InterfaceOrientationObserver.keyWindow())?.windowScene,
              scene !== observedScene else { return }
        observedScene = scene
        // Starts with the current value; later changes arrive on the main thread.
        geometryObservation = scene.observe(\.effectiveGeometry, options: [.initial, .new]) { [weak self] scene, _ in
            self?.update(scene.effectiveGeometry.interfaceOrientation, from: scene)
        }
    }

    private func update(_ interfaceOrientation: UIInterfaceOrientation, from scene: UIWindowScene) {
        // A scene the observer has moved away from must not overwrite the value.
        guard scene === observedScene else { return }
        let next = InterfaceOrientationObserver.orientation(from: interfaceOrientation)

        lock.lock()
        let didChange = next != orientation
        if didChange {
            orientation = next
        }
        let callbacks = didChange ? Array(listeners.values) : []
        lock.unlock()

        guard didChange else { return }
        OneKeyLog.debug("DeviceUtils", "Interface orientation: \(next.stringValue)")
        callbacks.forEach { $0(next) }
    }

    private static func orientation(from interfaceOrientation: UIInterfaceOrientation) -> InterfaceOrientation {
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

    private static func keyWindow() -> UIWindow? {
        UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }
    }
}
