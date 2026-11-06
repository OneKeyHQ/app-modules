import com.margelo.nitro.nativelogger.OneKeyLog
import java.io.File

fun main(args: Array<String>) {
    var assertions = 0
    fun expect(value: Boolean, scenario: String) { check(value) { scenario }; assertions++ }
    val method = OneKeyLog::class.java.getDeclaredMethod("formatMessage", String::class.java, String::class.java, String::class.java)
    method.isAccessible = true
    fun format(message: String) = method.invoke(OneKeyLog, "LiteCard", "INFO", message) as String
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
    println("KotlinLoggerSanitization: $assertions assertions passed")
}
