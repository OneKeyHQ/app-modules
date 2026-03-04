import NitroModules
import CocoaLumberjack

class NativeLogger: HybridNativeLoggerSpec {

    /// Patterns that should never be written to log files
    private static let sensitivePatterns: [NSRegularExpression] = {
        let patterns = [
            // Hex-encoded private keys (64 hex chars), with optional 0x prefix
            "(?:0x)?[0-9a-fA-F]{64}",
            // WIF private keys (base58, starting with 5, K, or L)
            "\\b[5KL][1-9A-HJ-NP-Za-km-z]{50,51}\\b",
            // Extended keys (xprv/xpub/zprv/zpub/yprv/ypub)
            "\\b[xyzXYZ](?:prv|pub)[1-9A-HJ-NP-Za-km-z]{107,108}\\b",
            // BIP39 mnemonic-like sequences (12+ words of 3-8 lowercase letters)
            "(?:\\b[a-z]{3,8}\\b[\\s,]+){11,}\\b[a-z]{3,8}\\b",
            // Bearer/API tokens
            "(?:Bearer|token[=:]?)\\s*[A-Za-z0-9_.\\-+/=]{20,}",
            // Base64 encoded data that looks like keys (44+ chars)
            "(?:eyJ|AAAA)[A-Za-z0-9+/=]{40,}",
        ]
        return patterns.compactMap { try? NSRegularExpression(pattern: $0) }
    }()

    /// Rate limiting: max messages per second
    private static let maxMessagesPerSecond = 100
    private static var messageCount = 0
    private static var windowStart = Date()
    private static let rateLimitLock = NSLock()

    private static func isRateLimited() -> Bool {
        rateLimitLock.lock()
        defer { rateLimitLock.unlock() }
        let now = Date()
        if now.timeIntervalSince(windowStart) >= 1.0 {
            windowStart = now
            messageCount = 0
        }
        messageCount += 1
        return messageCount > maxMessagesPerSecond
    }

    private static func sanitize(_ message: String) -> String {
        var result = message
        for regex in sensitivePatterns {
            result = regex.stringByReplacingMatches(
                in: result,
                range: NSRange(result.startIndex..., in: result),
                withTemplate: "[REDACTED]"
            )
        }
        // Strip newlines to prevent log injection
        result = result.replacingOccurrences(of: "\n", with: " ")
        result = result.replacingOccurrences(of: "\r", with: " ")
        return result
    }

    func write(level: Double, msg: String) {
        if NativeLogger.isRateLimited() { return }
        let sanitized = NativeLogger.sanitize(msg)
        switch Int(level) {
        case 0: OneKeyLog.debug("JS", sanitized)
        case 1: OneKeyLog.info("JS", sanitized)
        case 2: OneKeyLog.warn("JS", sanitized)
        case 3: OneKeyLog.error("JS", sanitized)
        default: OneKeyLog.info("JS", sanitized)
        }
    }

    func getLogDirectory() throws -> String {
        return OneKeyLog.logsDirectory
    }

    func getLogFilePaths() throws -> Promise<[String]> {
        return Promise.async {
            // Flush buffered log data so the active file reflects all written logs
            DDLog.flushLog()
            let dir = OneKeyLog.logsDirectory
            let fm = FileManager.default
            let files: [String]
            do {
                files = try fm.contentsOfDirectory(atPath: dir)
            } catch {
                OneKeyLog.warn("NativeLogger", "Failed to list log directory")
                return []
            }
            // Return filenames only, not absolute paths
            return files
                .filter { $0.hasSuffix(".log") }
                .sorted()
        }
    }

    func deleteLogFiles() throws -> Promise<Void> {
        return Promise.async {
            let dir = OneKeyLog.logsDirectory
            let fm = FileManager.default
            let files: [String]
            do {
                files = try fm.contentsOfDirectory(atPath: dir)
            } catch {
                OneKeyLog.warn("NativeLogger", "Failed to list log directory for deletion")
                return
            }
            // Skip the active log file to avoid breaking CocoaLumberjack's open file handle
            for file in files where file.hasSuffix(".log") && file != "app-latest.log" {
                do {
                    try fm.removeItem(atPath: "\(dir)/\(file)")
                } catch {
                    OneKeyLog.warn("NativeLogger", "Failed to delete log file")
                }
            }
        }
    }
}
