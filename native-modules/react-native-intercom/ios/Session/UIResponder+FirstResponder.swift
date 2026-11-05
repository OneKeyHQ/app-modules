// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

extension UIResponder {
    static weak var internalCurrentFirstResponder: UIResponder?

    @objc(findFirstResponderWithSender:)
    func findFirstResponder(sender: AnyObject) {
        UIResponder.internalCurrentFirstResponder = self
    }
}
