// Original three requirements preserved in the 19.9.1 dSYM witnesses.
import Foundation
protocol NotificationCenterInterface {
    func addObserver(_ observer: Any, selector: Selector, name: Notification.Name?, object: Any?)
    func removeObserver(_ observer: Any)
    func post(name: Notification.Name, object: Any?, userInfo: [AnyHashable: Any]?)
}
extension NotificationCenter: NotificationCenterInterface {}
