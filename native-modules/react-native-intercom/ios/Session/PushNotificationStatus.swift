import Foundation
@preconcurrency import UserNotifications
#if os(iOS)
@preconcurrency import UIKit
#endif

@objc(_TtC18IntercomSDKPrivate22PushNotificationStatus)
class PushNotificationStatus: NSObject {
    private let queue = DispatchQueue(label: "com.intercom.push-notification-status")
    private var enabled = false
    private let authorizationStatusProvider: (@escaping (UNAuthorizationStatus) -> Void) -> Void

    init(authorizationStatusProvider: @escaping (@escaping (UNAuthorizationStatus) -> Void) -> Void) {
        self.authorizationStatusProvider = authorizationStatusProvider
        super.init()
        authorizationStatusProvider { [weak self] status in
            if let self {
                self.queue.sync { self.enabled = (status.rawValue & ~1) == 2 }
            }
        }
        #if os(iOS)
        NotificationCenter.default.addObserver(self, selector: #selector(PushNotificationStatus.refresh as (PushNotificationStatus) -> () -> Void), name: UIApplication.didBecomeActiveNotification, object: nil)
        #endif
    }

    #if os(iOS)
    @objc static var shared = PushNotificationStatus()
    @objc override convenience init() {
        self.init(authorizationStatusProvider: { completion in
            ICMPushNotificationManager.safeGetAuthorizationStatus(completion: completion)
        })
    }
    #endif

    deinit { NotificationCenter.default.removeObserver(self) }
    @objc var isEnabled: Bool { queue.sync { enabled } }

    @objc func refresh() {
        authorizationStatusProvider { [weak self] status in
            if let self {
                self.queue.sync { self.enabled = (status.rawValue & ~1) == 2 }
            }
        }
    }

    @objc func refresh(completion: @escaping () -> Void) {
        authorizationStatusProvider { [weak self] status in
            if let self {
                self.queue.sync { self.enabled = (status.rawValue & ~1) == 2 }
            }
            completion()
        }
    }
}
