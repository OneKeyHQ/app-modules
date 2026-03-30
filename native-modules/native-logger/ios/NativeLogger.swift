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

    private static func truncateFile(atPath path: String) throws {
        guard FileManager.default.fileExists(atPath: path) else { return }
        let handle = try FileHandle(forWritingTo: URL(fileURLWithPath: path))
        defer {
            if #available(iOS 13.0, *) {
                try? handle.close()
            } else {
                handle.closeFile()
            }
        }
        if #available(iOS 13.0, *) {
            try handle.truncate(atOffset: 0)
        } else {
            handle.truncateFile(atOffset: 0)
        }
    }

    func flushPendingRepeat() {
        OneKeyLog.flushPendingRepeat()
    }

    func write(level: Double, msg: String) {
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
            DDLog.flushLog()
            let dir = OneKeyLog.logsDirectory
            let fm = FileManager.default
            let files: [String]
            do {
                files = try fm.contentsOfDirectory(atPath: dir)
            } catch {
                OneKeyLog.warn("NativeLogger", "Failed to list log directory for deletion")
                return
            }
            for file in files where file.hasSuffix(".log") {
                let path = "\(dir)/\(file)"
                do {
                    if file == "app-latest.log" {
                        try NativeLogger.truncateFile(atPath: path)
                    } else {
                        try fm.removeItem(atPath: path)
                    }
                } catch {
                    OneKeyLog.warn("NativeLogger", "Failed to delete log file")
                }
            }
        }
    }
}
