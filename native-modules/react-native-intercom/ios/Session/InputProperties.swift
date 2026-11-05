// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate15InputProperties)
class InputProperties: NSObject {
    @objc let initialValue: Any?
    @objc let allowSubmission: Bool
    @objc let valueIsEditable: Bool
    @objc init(initialValue: Any?, allowSubmission: Bool, valueIsEditable: Bool) {
        self.initialValue = initialValue
        self.allowSubmission = allowSubmission
        self.valueIsEditable = valueIsEditable
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
}
