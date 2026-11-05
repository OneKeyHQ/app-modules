// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate19PrivacyPolicyNotice)
class PrivacyPolicyNotice: NSObject {
    @objc let isDismissed: Bool
    @objc let paragraph: String

    @objc init(isDismissed: Bool, paragraph: String) {
        self.isDismissed = isDismissed
        self.paragraph = paragraph
        super.init()
    }

    @objc(initWithDictionary:)
    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let isDismissed = dictionary["is_dismissed"] as? Bool,
              let paragraph = dictionary["paragraph"] as? String else { return nil }
        self.init(isDismissed: isDismissed, paragraph: paragraph)
    }

    override init() { fatalError("init() has not been implemented") }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return isDismissed == other.isDismissed && paragraph == other.paragraph
    }
}
