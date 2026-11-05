import Foundation
private class FixtureSyncSubclass: ConversationStateSyncSettings {}
private class FixtureEmptyCoder: NSCoder {
    var keys: [String] = []
    override var allowsKeyedCoding: Bool { true }
    override func decodeBool(forKey key: String) -> Bool { keys.append(key); return false }
    override func decodeDouble(forKey key: String) -> Double { keys.append(key); return 0 }
}
@main struct SyncSettingsTests {
    static func main() throws {
        var passed: [String] = []
        func check(_ value: Bool, _ name: String) { guard value else { fputs("FAIL: \(name)\n",stderr);exit(1) };passed.append(name) }
        let defaults = ConversationStateSyncSettings()
        check(defaults.enabled && defaults.syncInterval == 10000 && defaults.startTimeout == 10000, "original defaults retain raw ten-thousand interval values")
        let input: [String: Any] = ["enabled":true,"sync_interval":1.25,"start_timeout":-3.5]
        let parsed = ConversationStateSyncSettings(withDictionary: input)!
        check(parsed.enabled && parsed.syncInterval == 1.25 && parsed.startTimeout == -3.5, "required dictionary values preserve fractional and negative intervals")
        for key in ["enabled","sync_interval","start_timeout"] {
            var missing = input; missing.removeValue(forKey:key)
            check(ConversationStateSyncSettings(withDictionary:missing) == nil,"missing required dictionary key returns nil \(key)")
            var wrong = input; wrong[key] = "synthetic"
            check(ConversationStateSyncSettings(withDictionary:wrong) == nil,"wrong dictionary type returns nil \(key)")
        }
        check(ConversationStateSyncSettings(withDictionary:[:]) == nil,"empty dictionary has no invented defaults")
        let nan = ConversationStateSyncSettings(enabled:true,syncInterval:.nan,startTimeout:0)
        check(!nan.isEqual(nan),"equality does not add identity fast path over NaN")
        let base = ConversationStateSyncSettings(enabled:false,syncInterval:0,startTimeout:0)
        let sub = FixtureSyncSubclass(enabled:false,syncInterval:0,startTimeout:0)
        check(base.isEqual(sub) && !sub.isEqual(base),"dynamic Self cast retains original base-subclass equality direction")
        check(!base.isEqual(nil) && !base.isEqual(NSObject()) && base.isEqual(ConversationStateSyncSettings(enabled:false,syncInterval:-0,startTimeout:0)),"wrong object nil and signed zero equality boundaries")
        let coder = FixtureEmptyCoder(); let decodedEmpty = ConversationStateSyncSettings(coder:coder)!
        check(!decodedEmpty.enabled && decodedEmpty.syncInterval == 0 && decodedEmpty.startTimeout == 0 && coder.keys == ["enabled","sync_interval","start_timeout"],"decoder unconditionally reads original scalar keys without default fallback")
        let data = try NSKeyedArchiver.archivedData(withRootObject:parsed,requiringSecureCoding:true)
        let decoded = try NSKeyedUnarchiver.unarchivedObject(ofClass:ConversationStateSyncSettings.self,from:data)
        check(decoded != nil && parsed.isEqual(decoded),"genuine secure archive round trip preserves original three-field model")
        let result = try JSONSerialization.data(withJSONObject:["status":"passed","testCount":passed.count,"tests":passed,"scope":"Actual required Config sync-settings model; no timers, network or conversation lifecycle"],options:.prettyPrinted)
        FileHandle.standardOutput.write(result);print()
    }
}
