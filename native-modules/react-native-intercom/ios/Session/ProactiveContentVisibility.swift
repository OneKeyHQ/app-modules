// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation
@preconcurrency import UIKit

@objc(_TtC18IntercomSDKPrivate29ICMProactiveContentVisibility)
class ICMProactiveContentVisibility: NSObject {
    @objc static let shared = ICMProactiveContentVisibility()
    @objc var carouselsEnabled = true
    @objc var surveysEnabled = true

    @objc override init() { super.init() }

    @objc func setProactiveVisible(carousels: Bool, surveys: Bool) {
        if carouselsEnabled == carousels && surveysEnabled == surveys { return }
        carouselsEnabled = carousels
        surveysEnabled = surveys
        fireUserUpdate()
    }

    private func fireUserUpdate() {
        if ICMIdentityStore.currentIdentity().userIdentity.identityExists() {
            let background = UIApplication.shared.applicationState != .active
            let request = ICMUserUpdateRequest(newSession: false, internalUpdate: true, sentFromBackgroundAppState: background)
            ICMHTTPBridge.updateUser(request, success: nil, failure: nil)
        }
    }
}
