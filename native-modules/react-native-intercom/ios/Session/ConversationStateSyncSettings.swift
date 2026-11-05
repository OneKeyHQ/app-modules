// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate29ConversationStateSyncSettings)
class ConversationStateSyncSettings: NSObject, NSSecureCoding {
    @objc let enabled: Bool
    @objc let syncInterval: Double
    @objc let startTimeout: Double
    @objc static var supportsSecureCoding: Bool { true }

    @objc init(enabled: Bool, syncInterval: Double, startTimeout: Double) {
        self.enabled = enabled
        self.syncInterval = syncInterval
        self.startTimeout = startTimeout
        super.init()
    }

    @objc override convenience init() {
        self.init(enabled: true, syncInterval: 10000, startTimeout: 10000)
    }

    @objc(initWithDictionary:)
    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let enabled = dictionary["enabled"] as? Bool,
              let syncInterval = dictionary["sync_interval"] as? Double,
              let startTimeout = dictionary["start_timeout"] as? Double else { return nil }
        self.init(enabled: enabled, syncInterval: syncInterval, startTimeout: startTimeout)
    }

    required init?(coder: NSCoder) {
        enabled = coder.decodeBool(forKey: "enabled")
        syncInterval = coder.decodeDouble(forKey: "sync_interval")
        startTimeout = coder.decodeDouble(forKey: "start_timeout")
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(enabled, forKey: "enabled")
        coder.encode(syncInterval, forKey: "sync_interval")
        coder.encode(startTimeout, forKey: "start_timeout")
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return enabled == other.enabled && syncInterval == other.syncInterval && startTimeout == other.startTimeout
    }
}
