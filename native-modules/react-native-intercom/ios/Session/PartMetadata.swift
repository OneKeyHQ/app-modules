// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate12PartMetadata)
class PartMetadata: NSObject {
    @objc var avatars: [Avatar]?
    @objc let title: String?
    @objc let suffix: String?
    @objc let timestamp: Date?

    @objc init(avatars: [Avatar]?, title: String?, suffix: String?, timestamp: Date?) {
        self.avatars = avatars
        self.title = title
        self.suffix = suffix
        self.timestamp = timestamp
        super.init()
    }

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        title = dictionary["title"] as? String
        suffix = dictionary["suffix"] as? String
        timestamp = (dictionary["timestamp"] as? Int).map { Date(timeIntervalSince1970: Double($0)) }
        avatars = (dictionary["avatars"] as? [[String: Any]])?.compactMap { Avatar(withDictionary: $0) }
        super.init()
    }

    override init() { fatalError("init() has not been implemented") }

    override var hash: Int {
        var hasher = Hasher()
        hasher.combine(title)
        hasher.combine(suffix)
        hasher.combine(timestamp)
        hasher.combine(avatars)
        return hasher.finalize()
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? PartMetadata else { return false }
        return title == other.title && suffix == other.suffix && timestamp == other.timestamp && avatars == other.avatars
    }
}
