import Foundation

enum DiagnosticType: String, CaseIterable, Codable {
    case crash
    case hang
    case cpuException = "cpu_exception"
    case diskWriteException = "disk_write_exception"
}

struct DiagnosticConfiguration {
    var isEnabled: Bool
    var samplingRate: Double
    var enabledTypes: Set<DiagnosticType>
    var maxStoredPayloads: Int
    var maxPayloadSizeBytes: Int

    static let `default` = DiagnosticConfiguration(
        isEnabled: false, samplingRate: 1, enabledTypes: Set(DiagnosticType.allCases),
        maxStoredPayloads: 100, maxPayloadSizeBytes: 250_000)

    private init(isEnabled: Bool, samplingRate: Double, enabledTypes: Set<DiagnosticType>,
                 maxStoredPayloads: Int, maxPayloadSizeBytes: Int) {
        self.isEnabled = isEnabled
        self.samplingRate = samplingRate
        self.enabledTypes = enabledTypes
        self.maxStoredPayloads = maxStoredPayloads
        self.maxPayloadSizeBytes = maxPayloadSizeBytes
    }

    init(serverConfig: [String: Any]?) {
        guard let serverConfig = serverConfig else {
            self = .default
            return
        }
        isEnabled = serverConfig["enabled"] as? Bool ?? false
        samplingRate = serverConfig["sampling_rate"] as? Double ?? 1
        if let names = serverConfig["enabled_types"] as? [String] {
            enabledTypes = Set(names.compactMap(DiagnosticType.init(rawValue:)))
        } else {
            enabledTypes = Set(DiagnosticType.allCases)
        }
        maxStoredPayloads = 100
        maxPayloadSizeBytes = 250_000
    }
}
