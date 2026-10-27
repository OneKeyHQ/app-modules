import Foundation
import UIKit
import ReactNativeNativeLogger

/// Tracks how much room the key window's system window controls (the iPadOS
/// window controls in the top-leading corner, for example) take beyond the
/// safe area.
///
/// UIKit reports the controls through the safe area with horizontal corner
/// adaptation (iOS 26+). An invisible view spanning the window recomputes the
/// difference on every layout and safe area change, so window resizes and
/// entering or leaving full screen update the value.
final class WindowControlsInsetsObserver {
    static let shared = WindowControlsInsetsObserver()

    private let lock = NSLock()
    private var insets = WindowControlsInsets(top: 0, left: 0, bottom: 0, right: 0)
    private var listeners: [Double: (WindowControlsInsets) -> Void] = [:]
    private var nextListenerId: Double = 1
    private var keyWindowObserver: NSObjectProtocol?
    private weak var trackingView: UIView?

    private init() {
        DispatchQueue.main.async { [weak self] in
            self?.install()
        }
    }

    var currentInsets: WindowControlsInsets {
        lock.lock()
        defer { lock.unlock() }
        return insets
    }

    func addListener(_ callback: @escaping (WindowControlsInsets) -> Void) -> Double {
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

    private func install() {
        // UIView.LayoutRegion ships in the iOS 26 SDK (Swift 6.2). Older
        // toolchains and systems keep the all-zero value.
        #if compiler(>=6.2)
        guard #available(iOS 26.0, *) else { return }
        guard trackingView == nil else { return }
        guard let window = WindowControlsInsetsObserver.keyWindow() else {
            // Native modules can initialize before the first window exists.
            guard keyWindowObserver == nil else { return }
            keyWindowObserver = NotificationCenter.default.addObserver(
                forName: UIWindow.didBecomeKeyNotification,
                object: nil,
                queue: .main
            ) { [weak self] _ in
                guard let self, let observer = self.keyWindowObserver else { return }
                NotificationCenter.default.removeObserver(observer)
                self.keyWindowObserver = nil
                self.install()
            }
            return
        }
        let view = WindowControlsTrackingView(frame: window.bounds) { [weak self] view in
            self?.update(from: view)
        }
        view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        view.isUserInteractionEnabled = false
        view.isAccessibilityElement = false
        // Behind the root view controller's view, so it never draws or
        // receives touches above app content.
        window.insertSubview(view, at: 0)
        trackingView = view
        update(from: view)
        #endif
    }

    #if compiler(>=6.2)
    @available(iOS 26.0, *)
    private func update(from view: UIView) {
        let safeArea = view.safeAreaInsets
        let adapted = view.edgeInsets(for: .safeArea(cornerAdaptation: .horizontal))
        let next = WindowControlsInsets(
            top: Double(max(0, adapted.top - safeArea.top)),
            left: Double(max(0, adapted.left - safeArea.left)),
            bottom: Double(max(0, adapted.bottom - safeArea.bottom)),
            right: Double(max(0, adapted.right - safeArea.right))
        )

        lock.lock()
        let previous = insets
        let didChange = previous.top != next.top
            || previous.left != next.left
            || previous.bottom != next.bottom
            || previous.right != next.right
        if didChange {
            insets = next
        }
        let callbacks = didChange ? Array(listeners.values) : []
        lock.unlock()

        guard didChange else { return }
        OneKeyLog.debug(
            "DeviceUtils",
            "Window controls insets: top=\(next.top), left=\(next.left), bottom=\(next.bottom), right=\(next.right)"
        )
        callbacks.forEach { $0(next) }
    }
    #endif

    private static func keyWindow() -> UIWindow? {
        UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }
    }
}

/// Invisible full-window view that reports layout and safe area changes.
private final class WindowControlsTrackingView: UIView {
    private let onChange: (UIView) -> Void

    init(frame: CGRect, onChange: @escaping (UIView) -> Void) {
        self.onChange = onChange
        super.init(frame: frame)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        onChange(self)
    }

    override func safeAreaInsetsDidChange() {
        super.safeAreaInsetsDidChange()
        onChange(self)
    }
}
