// Original extension's Objective-C entry; private Swift ABI remains unverified.
import UIKit
extension UIViewController {
    @objc var firstViewControllerIsConversation: Bool {
        var root = self
        while let presenting = root.presentingViewController { root = presenting }
        let rootView = root.view!
        guard let navigation = rootView.window?.rootViewController?.presentedViewController as? UINavigationController else { return false }
        let controllers = navigation.viewControllers
        guard !controllers.isEmpty else { return false }
        return controllers[0] is ConversationViewController
    }
}
