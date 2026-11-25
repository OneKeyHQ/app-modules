
// Appended to a temporary copy of the complete production file for private access.
extension OneKeyLog {
    static func regressionFormat(_ message: String, tag: String = "LiteCard") -> String {
        formatMessage(tag, "INFO", message)
    }
}
var assertions = 0
func expect(_ value: Bool, _ scenario: String) {
    precondition(value, scenario)
    assertions += 1
}
let hex = String(repeating: "a", count: 64)
let uuid = "12345678-1234-1234-1234-123456789abc"
let secret = String(repeating: "b", count: 64)
for id in [hex, "0x" + hex.uppercased(), String(repeating: "c", count: 128), "request-" + hex,
           String(repeating: "a", count: 32) + "-" + hex, hex + "-" + String(repeating: "a", count: 32),
           "request+%2D-" + hex + "-", hex + "-" + String(repeating: "b", count: 62),
           hex + "-" + String(repeating: "b", count: 63),
           "%" + String(repeating: "a", count: 62) + "g" + String(repeating: "b", count: 64),
           "job 1", "job_1", "job%201", "任务 1", uuid] {
    let event = SniConnectLog.event("sni_request_start", [("requestId", id), ("ip", "2001:4860:4860::8888")])
    let formatted = OneKeyLog.regressionFormat(event + " secret=" + secret, tag: "SniConnect")
    let displayed = formatted.components(separatedBy: " ").first { $0.hasPrefix("requestId=") }!.dropFirst(10)
    let recovered = formatted.contains("requestIdFormat=uri-hex32")
        ? displayed.replacingOccurrences(of: "-", with: "").removingPercentEncoding : String(displayed)
    expect(recovered == id, "Marked diagnostic IDs restore exactly, including original separators and percent signs")
    expect(formatted.contains("ip=2001:4860:4860::8888"), "The final logger retains the destination IP")
    expect(formatted.hasSuffix("secret=[REDACTED]"), "Same-line secrets retain generic redaction")
    if id == uuid { expect(displayed == uuid, "Existing UUID formatting is unchanged") }
}
expect(OneKeyLog.regressionFormat("event=sni_request_start requestId=" + hex, tag: "SniConnect").contains("requestId=[REDACTED]"), "The generic logger policy is unchanged even for SNI tags")
let groupedId = String(repeating: "a", count: 32) + "-" + String(repeating: "a", count: 32)
let rawIdLog = OneKeyLog.regressionFormat(SniConnectLog.event("sni_cancel", [("requestId", hex)]), tag: "SniConnect")
let groupedIdLog = OneKeyLog.regressionFormat(SniConnectLog.event("sni_cancel", [("requestId", groupedId)]), tag: "SniConnect")
expect(rawIdLog.contains("requestId=\(groupedId) requestIdFormat=uri-hex32"), "Grouped raw IDs have an explicit format marker in final output")
expect(groupedIdLog.hasSuffix("requestId=\(groupedId)"), "Originally separated IDs retain their literal form without the marker")
expect(!SniConnectLog.event("sni_cancel", [("requestId", uuid)]).contains("requestIdFormat="), "UUIDs need no format marker")
let spaceIdLog = OneKeyLog.regressionFormat(SniConnectLog.event("sni_cancel", [("requestId", "job 1")]), tag: "SniConnect")
let underscoreIdLog = OneKeyLog.regressionFormat(SniConnectLog.event("sni_cancel", [("requestId", "job_1")]), tag: "SniConnect")
let literalEscapeLog = OneKeyLog.regressionFormat(SniConnectLog.event("sni_cancel", [("requestId", "job%201")]), tag: "SniConnect")
expect(spaceIdLog.contains("requestId=job%201 requestIdFormat=uri-hex32"), "Original spaces are encoded before whitespace cleanup")
expect(underscoreIdLog.hasSuffix("requestId=job_1"), "Original underscores remain literal")
expect(literalEscapeLog.hasSuffix("requestId=job%201"), "Literal percent sequences have no encoding marker")
print("SniSwiftLogDiagnostics: \(assertions) assertions passed")
