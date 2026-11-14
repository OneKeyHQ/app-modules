import UIKit

@objc(_TtC18IntercomSDKPrivate28MessengerAppTextInputPayload)
class MessengerAppTextInputPayload: NSObject {
    var title: String?
    var placeholder: String?
    var currentText: String?
    var inputId: String?
    var messengerAppId: String?
    var completionBlock: ((UIAlertController) -> Void)?

    override init() { super.init() }
    override var hash: Int { title?.hashValue ?? 0 }
    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? MessengerAppTextInputPayload else { return false }
        return title == other.title && placeholder == other.placeholder && currentText == other.currentText && inputId == other.inputId && messengerAppId == other.messengerAppId
    }
}
