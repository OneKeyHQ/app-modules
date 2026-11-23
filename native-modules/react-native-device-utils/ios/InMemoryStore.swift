import Foundation

// The singleton outlives HybridObjects and React runtimes. Only process exit
// clears it; no value holds a JS object or writes to persistent storage.
final class InMemoryStore {
    static let shared = InMemoryStore()
    private static let maxEntries = 128
    private static let maxKeyBytes = 256
    private static let maxValueBytes = 4096
    private let lock = NSLock()
    // Swift String equality normalizes Unicode; Data preserves exact key bytes.
    private var values: [Data: InMemoryValue] = [:]

    private init() {}

    private func keyData(_ key: String) throws -> Data {
        guard key.utf8.prefix(Self.maxKeyBytes + 1).count <= Self.maxKeyBytes else {
            throw budgetError("InMemoryStore key exceeds 256 UTF-8 bytes")
        }
        return Data(key.utf8)
    }

    private func validateValue(_ value: InMemoryValue) throws {
        guard case .second(let string) = value else { return }
        guard string.utf8.prefix(Self.maxValueBytes + 1).count <= Self.maxValueBytes else {
            throw budgetError("InMemoryStore value exceeds 4096 UTF-8 bytes")
        }
    }

    private func budgetError(_ message: String) -> NSError {
        return NSError(domain: "InMemoryStore", code: 1,
                       userInfo: [NSLocalizedDescriptionKey: message])
    }

    func get(_ key: String) throws -> InMemoryValue? {
        lock.lock()
        defer { lock.unlock() }
        return values[try keyData(key)]
    }

    func set(_ key: String, _ value: InMemoryValue) throws {
        lock.lock()
        defer { lock.unlock() }
        let data = try keyData(key)
        try validateValue(value)
        guard values[data] != nil || values.count < Self.maxEntries else {
            throw budgetError("InMemoryStore capacity exceeds 128 entries")
        }
        values[data] = value
    }

    func remove(_ key: String) throws -> Bool {
        lock.lock()
        defer { lock.unlock() }
        return values.removeValue(forKey: try keyData(key)) != nil
    }

    func setIfAbsent(_ key: String, _ value: InMemoryValue) throws -> Bool {
        lock.lock()
        defer { lock.unlock() }
        let data = try keyData(key)
        try validateValue(value)
        guard values[data] == nil else { return false }
        guard values.count < Self.maxEntries else {
            throw budgetError("InMemoryStore capacity exceeds 128 entries")
        }
        values[data] = value
        return true
    }
}
