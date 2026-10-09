import Foundation

// The singleton outlives HybridObjects and React runtimes. Only process exit
// clears it; no value holds a JS object or writes to persistent storage.
final class ProcessMemoryStore {
    static let shared = ProcessMemoryStore()
    private let lock = NSLock()
    private var values: [String: String] = [:]

    private init() {}

    func get(_ key: String) -> String? {
        lock.lock()
        defer { lock.unlock() }
        return values[key]
    }

    func set(_ key: String, _ value: String) {
        lock.lock()
        defer { lock.unlock() }
        values[key] = value
    }

    func remove(_ key: String) -> Bool {
        lock.lock()
        defer { lock.unlock() }
        return values.removeValue(forKey: key) != nil
    }

    func setIfAbsent(_ key: String, _ value: String) -> Bool {
        lock.lock()
        defer { lock.unlock() }
        guard values[key] == nil else { return false }
        values[key] = value
        return true
    }
}
