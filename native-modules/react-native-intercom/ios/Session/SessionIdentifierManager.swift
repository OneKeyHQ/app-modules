// Original per-launch snapshot and UserDefaults writes; no extra locking.
import Foundation
@objc(_TtC18IntercomSDKPrivate24SessionIdentifierManager)
class SessionIdentifierManager: NSObject {
    @objc static let shared = SessionIdentifierManager(userDefaults: .standard)
    @objc let lastLaunchSessionId: String?
    private let userDefaults: UserDefaults
    init(userDefaults: UserDefaults) {
        self.userDefaults = userDefaults
        self.lastLaunchSessionId = userDefaults.string(forKey: "com.intercom.session.currentId")
        super.init()
        userDefaults.removeObject(forKey: "com.intercom.session.previousId")
    }
    override init() { fatalError("init() has not been implemented") }
    @objc func recordStartedSession(_ identifier: String) {
        userDefaults.set(identifier, forKey: "com.intercom.session.currentId")
    }
}
