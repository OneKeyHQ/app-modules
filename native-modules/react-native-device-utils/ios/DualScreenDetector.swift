import Foundation
import UIKit
import ReactNativeNativeLogger

/// Detects foldable iPhones such as iPhone Duo.
///
/// Detection runs in two steps so the answer is available synchronously at
/// launch and still covers models that are not listed yet:
/// 1. The model identifier is matched against known foldable iPhones.
/// 2. A `UIHingeInteraction` (iOS 27.1+) confirms the hinge at runtime.
/// A positive result is persisted, so later launches answer synchronously
/// even for models that only the hinge check recognized.
final class DualScreenDetector {
    static let shared = DualScreenDetector()

    private static let cacheKey = "1k_is_dual_screen_device"
    private static let foldableModelIdentifiers: Set<String> = [
        "iPhone19,4", // iPhone Duo
    ]

    private let lock = NSLock()
    private var isDualScreen: Bool
    private var spanning = false
    private var listeners: [Double: (Bool) -> Void] = [:]
    private var nextListenerId: Double = 1
    private var hingeObserver: AnyObject?

    private init() {
        let modelIdentifier = DualScreenDetector.modelIdentifier()
        let isKnownModel = DualScreenDetector.foldableModelIdentifiers.contains(modelIdentifier)
        let isCached = UserDefaults.standard.string(forKey: DualScreenDetector.cacheKey) == modelIdentifier
        isDualScreen = isKnownModel || isCached
        if isKnownModel && !isCached {
            UserDefaults.standard.set(modelIdentifier, forKey: DualScreenDetector.cacheKey)
        }
        OneKeyLog.info(
            "DeviceUtils",
            "Dual screen detection: model=\(modelIdentifier), knownModel=\(isKnownModel), cached=\(isCached)"
        )
        installHingeObserver()
    }

    static func modelIdentifier() -> String {
        #if targetEnvironment(simulator)
        if let identifier = ProcessInfo.processInfo.environment["SIMULATOR_MODEL_IDENTIFIER"],
           !identifier.isEmpty {
            return identifier
        }
        #endif
        var systemInfo = utsname()
        uname(&systemInfo)
        return withUnsafePointer(to: &systemInfo.machine) { pointer in
            pointer.withMemoryRebound(to: CChar.self, capacity: MemoryLayout.size(ofValue: pointer.pointee)) {
                String(cString: $0)
            }
        }
    }

    var isDualScreenDevice: Bool {
        lock.lock()
        defer { lock.unlock() }
        return isDualScreen
    }

    var isSpanning: Bool {
        lock.lock()
        defer { lock.unlock() }
        return spanning
    }

    func addSpanningChangedListener(_ callback: @escaping (Bool) -> Void) -> Double {
        lock.lock()
        defer { lock.unlock() }
        let id = nextListenerId
        nextListenerId += 1
        listeners[id] = callback
        return id
    }

    func removeSpanningChangedListener(_ id: Double) {
        lock.lock()
        defer { lock.unlock() }
        listeners.removeValue(forKey: id)
    }

    private func installHingeObserver() {
        // UIHinge ships in the iOS 27.1 SDK (Swift 6.4). Older toolchains keep
        // the model identifier and cache only.
        #if compiler(>=6.4)
        guard #available(iOS 27.1, *) else { return }
        DispatchQueue.main.async { [weak self] in
            self?.attachHingeInteraction()
        }
        #endif
    }

    #if compiler(>=6.4)
    @available(iOS 27.1, *)
    private func attachHingeInteraction() {
        guard hingeObserver == nil else { return }
        guard let window = DualScreenDetector.keyWindow() else {
            // Native modules can initialize before the first window exists.
            hingeObserver = NotificationCenter.default.addObserver(
                forName: UIWindow.didBecomeKeyNotification,
                object: nil,
                queue: .main
            ) { [weak self] _ in
                guard let self, let observer = self.hingeObserver else { return }
                NotificationCenter.default.removeObserver(observer)
                self.hingeObserver = nil
                self.attachHingeInteraction()
            }
            return
        }
        let interaction = UIHingeInteraction { [weak self] _, update in
            self?.handleHingeUpdate(update.hinge)
        }
        window.addInteraction(interaction)
        hingeObserver = interaction
    }

    @available(iOS 27.1, *)
    private func handleHingeUpdate(_ hinge: UIHinge?) {
        // A nil hinge means the hierarchy no longer provides hinge updates;
        // keep the last known state.
        guard let hinge else { return }
        let nextSpanning = hinge.status == .fullyOpen || hinge.status == .partiallyOpen

        lock.lock()
        let isNewlyDetected = !isDualScreen
        isDualScreen = true
        let didChangeSpanning = spanning != nextSpanning
        spanning = nextSpanning
        let callbacks = Array(listeners.values)
        lock.unlock()

        if isNewlyDetected {
            UserDefaults.standard.set(DualScreenDetector.modelIdentifier(), forKey: DualScreenDetector.cacheKey)
            OneKeyLog.info(
                "DeviceUtils",
                "Dual screen detected by hinge: model=\(DualScreenDetector.modelIdentifier())"
            )
        }
        // A newly detected device also notifies, so JS can react without
        // waiting for the next fold.
        if didChangeSpanning || isNewlyDetected {
            callbacks.forEach { $0(nextSpanning) }
        }
    }
    #endif

    private static func keyWindow() -> UIWindow? {
        UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }
    }
}
