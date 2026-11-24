import com.margelo.nitro.nativelogger.OneKeyLog
import com.sniconnect.SniConnectLogger
import java.io.File

fun main(args: Array<String>) {
    var assertions = 0
    fun expect(value: Boolean, scenario: String) { check(value) { scenario }; assertions++ }
    val method = OneKeyLog::class.java.getDeclaredMethod("formatMessage", String::class.java, String::class.java, String::class.java)
    method.isAccessible = true
    fun format(message: String, tag: String = "LiteCard") = method.invoke(OneKeyLog, tag, "INFO", message) as String
    val lines = File(args[0]).readLines().filter { it.isNotEmpty() }
    val canonical = lines.first()
    expect(Regex("source-v1:[0-9a-f]{64}").matches(canonical), "Actual compiled symbol retains canonical identity")
    expect(lines.size > 1, "Actual native build messages are required")
    for (message in lines.drop(1)) {
        val displayed = message.split(" ").first { it.startsWith("nativeSourceId=") }.substringAfter("=")
        expect(Regex("source-v1:([0-9a-f]{8}-){7}[0-9a-f]{8}").matches(displayed), "Actual native display has eight bounded groups")
        expect("source-v1:" + displayed.substring(10).replace("-", "") == canonical, "Complete compiled digest can be restored")
        expect(format(message).endsWith(" " + message), "Actual native metadata survives production formatting and sanitization")
    }
    expect(format("nativeSourceId=" + canonical).endsWith("source-v1:[REDACTED]"), "Raw canonical identities still follow generic redaction")
    val canaries = listOf("b".repeat(64), List(12) { "sample" }.joinToString(" "), "Bearer " + "Q".repeat(32), "token=" + "T".repeat(32))
    for (canary in canaries) {
        val formatted = format("secret=" + canary)
        expect(formatted.endsWith("secret=[REDACTED]") && !formatted.contains(canary), "Sensitive canaries retain production redaction")
        val combined = format(lines[1] + " secret=" + canary)
        expect(combined.contains(lines[1]) && combined.endsWith("secret=[REDACTED]") && !combined.contains(canary), "Grouped identity does not exempt same-line secrets from redaction")
    }
    val hex = "a".repeat(64)
    val uuid = "12345678-1234-1234-1234-123456789abc"
    for (id in listOf(hex, "0x" + hex.uppercase(), "c".repeat(128), "request-$hex",
        "a".repeat(32) + "-" + hex, hex + "-" + "a".repeat(32), "request+%2D-$hex-",
        "$hex-" + "b".repeat(62), "$hex-" + "b".repeat(63), "%" + "a".repeat(62) + "g" + "b".repeat(64),
        "job 1", "job_1", "job%201", "任务 1", uuid)) {
        val event = SniConnectLogger.event("sni_request_start", "requestId" to id, "ip" to "2001:4860:4860::8888")
        val formatted = format(event + " secret=" + canaries[0], "SniConnect")
        val displayed = formatted.split(" ").first { it.startsWith("requestId=") }.substringAfter("=")
        expect(!displayed.contains("[REDACTED]"), "The final logger retains every diagnostic ID character")
        val recovered = if (formatted.contains("requestIdFormat=uri-hex32"))
            java.net.URLDecoder.decode(displayed.replace("-", "").replace("+", "%2B"), "UTF-8") else displayed
        expect(recovered == id, "Marked diagnostic IDs restore exactly, including original separators and percent signs")
        expect(formatted.contains("ip=2001:4860:4860::8888"), "The final logger retains the destination IP")
        expect(formatted.endsWith("secret=[REDACTED]"), "Same-line secrets retain generic redaction")
        if (id == uuid) expect(displayed == uuid, "Existing UUID formatting is unchanged")
    }
    expect(format("event=sni_request_start requestId=$hex", "SniConnect").contains("requestId=[REDACTED]"), "The generic logger policy is unchanged even for SNI tags")
    val groupedId = "a".repeat(32) + "-" + "a".repeat(32)
    val rawIdLog = format(SniConnectLogger.event("sni_cancel", "requestId" to hex), "SniConnect")
    val groupedIdLog = format(SniConnectLogger.event("sni_cancel", "requestId" to groupedId), "SniConnect")
    expect(rawIdLog.contains("requestId=$groupedId requestIdFormat=uri-hex32"), "Grouped raw IDs have an explicit format marker in final output")
    expect(groupedIdLog.endsWith("requestId=$groupedId"), "Originally separated IDs retain their literal form without the marker")
    expect(rawIdLog != groupedIdLog, "Distinct raw and originally separated IDs remain distinguishable")
    expect(!SniConnectLogger.event("sni_cancel", "requestId" to uuid).contains("requestIdFormat="), "UUIDs need no format marker")
    val spaceIdLog = format(SniConnectLogger.event("sni_cancel", "requestId" to "job 1"), "SniConnect")
    val underscoreIdLog = format(SniConnectLogger.event("sni_cancel", "requestId" to "job_1"), "SniConnect")
    val literalEscapeLog = format(SniConnectLogger.event("sni_cancel", "requestId" to "job%201"), "SniConnect")
    expect(spaceIdLog.contains("requestId=job%201 requestIdFormat=uri-hex32"), "Original spaces are encoded before whitespace cleanup")
    expect(underscoreIdLog.endsWith("requestId=job_1"), "Original underscores remain literal")
    expect(literalEscapeLog.endsWith("requestId=job%201"), "Literal percent sequences have no encoding marker")
    expect(spaceIdLog != underscoreIdLog && spaceIdLog != literalEscapeLog, "Distinct space, underscore and percent IDs remain distinguishable")
    println("KotlinLoggerSanitization: $assertions assertions passed")
}
