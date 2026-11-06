
// Appended to a temporary copy of the complete production file for private access.
extension OneKeyLog {
    static func regressionFormat(_ message: String) -> String {
        formatMessage("LiteCard", "INFO", message)
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
print("SwiftLoggerSanitization: \(assertions) assertions passed")
