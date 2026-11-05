import Foundation

struct StoredDiagnosticPayload {
    let id: String
    let createdAt: Date
    let payload: [String: Any]
}

final class DiagnosticStore {
    private let fileURL: URL
    private let maxPayloads: Int
    private let maxAge: Double
    private let queue = DispatchQueue(label: "com.intercom.diagnostic-store")
    private var cache: [StoredDiagnosticPayload]?

    init(directory: String, maxPayloads: Int, maxAge: Double) {
        fileURL = URL(fileURLWithPath: directory).appendingPathComponent("diagnostics.json")
        self.maxPayloads = maxPayloads
        self.maxAge = maxAge
    }

    func append(_ payload: [String: Any]) -> Bool {
        queue.sync {
            var stored = loadedPayloads()
            stored.append(StoredDiagnosticPayload(id: UUID().uuidString, createdAt: Date(), payload: payload))
            if stored.count > maxPayloads {
                stored = Array(stored.suffix(maxPayloads))
            }
            cache = stored
            return saveToDisk(stored)
        }
    }

    func fetchAll() -> [StoredDiagnosticPayload] {
        queue.sync {
            let stored = loadedPayloads()
            let now = Date()
            let current = stored.filter { now.timeIntervalSince($0.createdAt) < maxAge }
            if current.count != stored.count {
                cache = current
                _ = saveToDisk(current)
            }
            return current
        }
    }

    func remove(ids: Set<String>) {
        queue.sync {
            var stored = loadedPayloads()
            stored.removeAll { ids.contains($0.id) }
            cache = stored
            _ = saveToDisk(stored)
        }
    }

    private func loadedPayloads() -> [StoredDiagnosticPayload] {
        if let cache = cache { return cache }
        let stored = loadFromDisk()
        cache = stored
        return stored
    }

    private func saveToDisk(_ payloads: [StoredDiagnosticPayload]) -> Bool {
        let formatter = ISO8601DateFormatter()
        let records: [[String: Any]] = payloads.map {
            ["id": $0.id, "createdAt": formatter.string(from: $0.createdAt), "payload": $0.payload]
        }
        do {
            let data = try JSONSerialization.data(withJSONObject: records, options: [])
            try data.write(to: fileURL, options: .atomic)
            return true
        } catch {
            return false
        }
    }

    private func loadFromDisk() -> [StoredDiagnosticPayload] {
        guard let data = try? Data(contentsOf: fileURL),
              let records = (try? JSONSerialization.jsonObject(with: data, options: [])) as? [[String: Any]] else {
            return []
        }
        let formatter = ISO8601DateFormatter()
        return records.compactMap {
            guard let id = $0["id"] as? String,
                  let timestamp = $0["createdAt"] as? String,
                  let date = formatter.date(from: timestamp),
                  let payload = $0["payload"] as? [String: Any] else { return nil }
            return StoredDiagnosticPayload(id: id, createdAt: date, payload: payload)
        }
    }
}
