
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
let lines = try String(contentsOfFile: CommandLine.arguments[1], encoding: .utf8)
    .split(separator: "\n").map(String.init)
let canonical = lines[0]
expect(canonical.range(of: "^source-v1:[0-9a-f]{64}$", options: .regularExpression) != nil, "Actual compiled symbol retains canonical identity")
expect(lines.count > 1, "Actual native build messages are required")
for message in lines.dropFirst() {
    let prefix = "nativeSourceId="
    let field = message.components(separatedBy: " ").first { $0.hasPrefix(prefix) }!
    let displayed = String(field.dropFirst(prefix.count))
    expect(displayed.range(of: "^source-v1:([0-9a-f]{8}-){7}[0-9a-f]{8}$", options: .regularExpression) != nil, "Actual native display has eight bounded groups")
    expect("source-v1:" + displayed.dropFirst(10).replacingOccurrences(of: "-", with: "") == canonical, "Complete compiled digest can be restored")
    let formatted = OneKeyLog.regressionFormat(message)
    expect(formatted.hasSuffix(" " + message), "Actual native metadata survives production formatting and sanitization")
}
let raw = OneKeyLog.regressionFormat("nativeSourceId=" + canonical)
expect(raw.hasSuffix("source-v1:[REDACTED]"), "Raw canonical identities still follow generic redaction")
let canaries = [String(repeating: "b", count: 64), Array(repeating: "sample", count: 12).joined(separator: " "),
                "Bearer " + String(repeating: "Q", count: 32), "token=" + String(repeating: "T", count: 32)]
for canary in canaries {
    let formatted = OneKeyLog.regressionFormat("secret=" + canary)
    expect(formatted.hasSuffix("secret=[REDACTED]") && !formatted.contains(canary), "Sensitive canaries retain production redaction")
    let combined = OneKeyLog.regressionFormat(lines[1] + " secret=" + canary)
    expect(combined.contains(lines[1]) && combined.hasSuffix("secret=[REDACTED]") && !combined.contains(canary), "Grouped identity does not exempt same-line secrets from redaction")
}
let hex = String(repeating: "a", count: 64)
let uuid = "12345678-1234-1234-1234-123456789abc"
for id in [hex, "0x" + hex.uppercased(), String(repeating: "c", count: 128), "request-" + hex,
           String(repeating: "a", count: 32) + "-" + hex, hex + "-" + String(repeating: "a", count: 32),
           "request+%2D-" + hex + "-", uuid] {
    let event = SniConnectLog.event("sni_request_start", [("requestId", id), ("ip", "2001:4860:4860::8888")])
    let formatted = OneKeyLog.regressionFormat(event + " secret=" + canaries[0], tag: "SniConnect")
    let displayed = formatted.components(separatedBy: " ").first { $0.hasPrefix("requestId=") }!.dropFirst(10)
    expect(!displayed.contains("[REDACTED]"), "The final logger retains every diagnostic ID character")
    let recovered = formatted.contains("requestIdFormat=hex32")
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
expect(rawIdLog.contains("requestId=\(groupedId) requestIdFormat=hex32"), "Grouped raw IDs have an explicit format marker in final output")
expect(groupedIdLog.hasSuffix("requestId=\(groupedId)"), "Originally separated IDs retain their literal form without the marker")
expect(rawIdLog != groupedIdLog, "Distinct raw and originally separated IDs remain distinguishable")
expect(!SniConnectLog.event("sni_cancel", [("requestId", uuid)]).contains("requestIdFormat="), "UUIDs need no format marker")
print("SwiftLoggerSanitization: \(assertions) assertions passed")
