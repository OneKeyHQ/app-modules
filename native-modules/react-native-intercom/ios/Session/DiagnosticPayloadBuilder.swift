import Foundation

struct DiagnosticPayloadBuilder {
    let binaryIdentifier: BinaryIdentifier
    let deviceContext: [String: Any]
    private static let dateFormatter = ISO8601DateFormatter()

    func buildPayload(diagnosticType: DiagnosticType, filteredCallStackJSON: Data,
                      metadata: DiagnosticMetadata, matchedBinaryNames: Set<String>,
                      sdkAttribution: SDKAttribution, timestamp: Date) -> [String: Any]? {
        guard let root = (try? JSONSerialization.jsonObject(with: filteredCallStackJSON, options: [])) as? [String: Any] else {
            return nil
        }
        var result: [String: Any] = [
            "id": UUID().uuidString,
            "platform": "ios",
            "diagnostic_type": diagnosticType.rawValue,
            "timestamp": Self.dateFormatter.string(from: timestamp),
            "call_stack_tree": root,
            "device_context": deviceContext,
            "sdk_context": buildSDKContext(matchedBinaryNames: matchedBinaryNames, sdkAttribution: sdkAttribution)
        ]
        switch metadata {
        case let .crash(exceptionType, signal, exceptionCode, terminationReason):
            var crash: [String: Any] = [:]
            if let exceptionType = exceptionType { crash["exception_type"] = exceptionType }
            if let signal = signal { crash["signal"] = signal }
            if let exceptionCode = exceptionCode { crash["exception_code"] = exceptionCode }
            if let terminationReason = terminationReason { crash["termination_reason"] = terminationReason }
            result["crash_metadata"] = crash
        case let .hang(durationMs):
            result["hang_metadata"] = ["hang_duration_ms": durationMs]
        case let .cpu(totalCPUTimeMs, totalSampledTimeMs):
            result["cpu_metadata"] = ["total_cpu_time_ms": totalCPUTimeMs, "total_sampled_time_ms": totalSampledTimeMs]
        case let .diskWrite(totalWritesCaused):
            result["disk_write_exception_metadata"] = ["total_writes_caused": totalWritesCaused]
        }
        return result
    }

    private func buildSDKContext(matchedBinaryNames: Set<String>, sdkAttribution: SDKAttribution) -> [String: Any] {
        ["sdk_version": ICMUtils.intercomVersion() ?? "",
         "sdk_binary_names_in_stack": Array(matchedBinaryNames),
         "sdk_attribution": sdkAttribution.rawValue,
         "linkage": binaryIdentifier.linkage.rawValue,
         "binary_name": binaryIdentifier.binaryName,
         "binary_uuid": binaryIdentifier.binaryUUID ?? ""]
    }
}
