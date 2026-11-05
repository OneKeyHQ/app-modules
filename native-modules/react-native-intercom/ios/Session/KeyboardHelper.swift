import UIKit

@_objcRuntimeName(_TtC18IntercomSDKPrivate14KeyboardHelper)
class KeyboardHelper {
    var isKeyboardHidden = true
    // Internal access materializes original WMO direct stores; original registration API names are unrecovered.
    var callbacks: [KeyboardEvent: (KeyboardNotification) -> Void] = [:]

    init() { addObservers() }
    deinit { NotificationCenter.default.removeObserver(self) }

    private func addObservers() {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow(with:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidShow(with:)), name: UIResponder.keyboardDidShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide(with:)), name: UIResponder.keyboardWillHideNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidHide(with:)), name: UIResponder.keyboardDidHideNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChangeFrame(with:)), name: UIResponder.keyboardWillChangeFrameNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidChangeFrame(with:)), name: UIResponder.keyboardDidChangeFrameNotification, object: nil)
    }

    @objc(keyboardWillShowWithNotification:)
    private func keyboardWillShow(with notification: NSNotification) {
        isKeyboardHidden = false
        emit(notification, for: .willShow)
    }
    @objc(keyboardDidShowWithNotification:)
    private func keyboardDidShow(with notification: NSNotification) { emit(notification, for: .didShow) }
    @objc(keyboardWillHideWithNotification:)
    private func keyboardWillHide(with notification: NSNotification) { emit(notification, for: .willHide) }
    @objc(keyboardDidHideWithNotification:)
    private func keyboardDidHide(with notification: NSNotification) {
        isKeyboardHidden = true
        emit(notification, for: .didHide)
    }
    @objc(keyboardWillChangeFrameWithNotification:)
    private func keyboardWillChangeFrame(with notification: NSNotification) { emit(notification, for: .willChangeFrame) }
    @objc(keyboardDidChangeFrameWithNotification:)
    private func keyboardDidChangeFrame(with notification: NSNotification) { emit(notification, for: .didChangeFrame) }

    private func emit(_ notification: NSNotification, for key: KeyboardEvent) {
        guard notification.event != .unknown else { return }
        let event = notification.event
        let duration = notification.timeInterval ?? 0.25
        let options: UIView.AnimationOptions
        if let curve = notification.animationCurve {
            let raw = min(UInt(bitPattern: curve.rawValue), 3)
            options = UIView.AnimationOptions(rawValue: raw << 16)
        } else { options = UIView.AnimationOptions(rawValue: 0) }
        let local = notification.isForCurrentApp ?? true
        let start = notification.startFrame ?? .zero
        let end = notification.endFrame ?? .zero
        let payload = KeyboardNotification(event: event, timeInterval: duration, animationOptions: options, isForCurrentApp: local, startFrame: start, endFrame: end)
        let callback = callbacks[key]
        callback?(payload)
    }
}
