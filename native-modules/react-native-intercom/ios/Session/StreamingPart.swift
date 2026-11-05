// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate13StreamingPart)
class StreamingPart: NSObject {
    @objc let metadata: PartMetadata?

    @objc init(metadata: PartMetadata?) {
        self.metadata = metadata
        super.init()
    }

    @objc(initWithDictionary:)
    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let dictionary = dictionary["metadata"] as? [String: Any],
              let metadata = PartMetadata(withDictionary: dictionary) else { return nil }
        self.init(metadata: metadata)
    }

    override init() { fatalError("init() has not been implemented") }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return metadata == other.metadata
    }
}
