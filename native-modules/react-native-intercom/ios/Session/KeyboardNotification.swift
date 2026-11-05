import UIKit

enum KeyboardEvent: Hashable {
    case willShow, didShow, willHide, didHide, willChangeFrame, didChangeFrame, unknown
}

struct KeyboardNotification {
    let event: KeyboardEvent
    let timeInterval: Double
    let animationOptions: UIView.AnimationOptions
    let isForCurrentApp: Bool
    var startFrame: CGRect
    var endFrame: CGRect
}

extension NSNotification {
    var event: KeyboardEvent {
        let name = self.name
        if name == UIResponder.keyboardWillShowNotification { return .willShow }
        if name == UIResponder.keyboardDidShowNotification { return .didShow }
        if name == UIResponder.keyboardWillHideNotification { return .willHide }
        if name == UIResponder.keyboardDidHideNotification { return .didHide }
        if name == UIResponder.keyboardWillChangeFrameNotification { return .willChangeFrame }
        if name == UIResponder.keyboardDidChangeFrameNotification { return .didChangeFrame }
        return .unknown
    }
    var timeInterval: Double? { (userInfo?[UIResponder.keyboardAnimationDurationUserInfoKey] as? NSNumber)?.doubleValue }
    var isForCurrentApp: Bool? { (userInfo?[UIResponder.keyboardIsLocalUserInfoKey] as? NSNumber)?.boolValue }
    var animationCurve: UIView.AnimationCurve? {
        guard let value = userInfo?[UIResponder.keyboardAnimationCurveUserInfoKey] as? NSNumber else { return nil }
        let raw = value.intValue
        let clamped = UInt(bitPattern: raw) < 3 ? raw : 3
        return UIView.AnimationCurve(rawValue: clamped)
    }
    var startFrame: CGRect? { (userInfo?[UIResponder.keyboardFrameBeginUserInfoKey] as? NSValue)?.cgRectValue }
    var endFrame: CGRect? { (userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue }
}
