import UIKit
import ObjectiveC

@_objcRuntimeName(_TtC18IntercomSDKPrivate13ClosureSleeve)
private class ClosureSleeve {
    let closure: (UIButton) -> Void
    let button: UIButton
    init(closure: @escaping (UIButton) -> Void, button: UIButton) {
        self.closure = closure
        self.button = button
    }
    @objc func invoke() { closure(button) }
}

extension UIButton {
    func addAction(for events: UIControl.Event, _ closure: @escaping (UIButton) -> Void) {
        let sleeve = ClosureSleeve(closure: closure, button: self)
        addTarget(sleeve, action: #selector(ClosureSleeve.invoke), for: events)
        let description = UUID().description
        // The original uses this temporary buffer's address as its association key.
        description.utf8CString.withUnsafeBufferPointer { buffer in
            objc_setAssociatedObject(self, buffer.baseAddress!, sleeve, .OBJC_ASSOCIATION_RETAIN)
        }
    }
}
