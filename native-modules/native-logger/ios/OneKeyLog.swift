import CocoaLumberjack

private let ddLogLevel: DDLogLevel = .debug

private class OneKeyLogFileManager: DDLogFileManagerDefault {
    private static let logPrefix = "app"
    private static let latestFileName = "\(logPrefix)-latest.log"
    private static let dateFormatterLock = NSLock()
    private static let _dateFormatter: DateFormatter = {
        let fmt = DateFormatter()
        fmt.dateFormat = "yyyy-MM-dd"
        fmt.locale = Locale(identifier: "en_US_POSIX")
        return fmt
    }()

    private static func formattedDate(_ date: Date) -> String {
        dateFormatterLock.lock()
        defer { dateFormatterLock.unlock() }
        return _dateFormatter.string(from: date)
    }

    /// Always write to app-latest.log (matches Android behavior)
    override var newLogFileName: String {
        return Self.latestFileName
    }

    override func isLogFile(withName fileName: String) -> Bool {
        return fileName.hasPrefix(Self.logPrefix) && fileName.hasSuffix(".log")
    }

    /// When rolled, rename app-latest.log → app-{yyyy-MM-dd}.{i}.log (matches Android pattern)
    override func didArchiveLogFile(atPath logFilePath: String, wasRolled: Bool) {
        if wasRolled {
            let dir = (logFilePath as NSString).deletingLastPathComponent
            let dateStr = Self.formattedDate(Date())
            let fm = FileManager.default

            // Find next available index, retry on move failure to handle TOCTOU race
            var index = 0
            var moved = false
            while !moved && index < 1000 {
                let archivedPath = "\(dir)/\(Self.logPrefix)-\(dateStr).\(index).log"
                if fm.fileExists(atPath: archivedPath) {
                    index += 1
                    continue
                }
                do {
                    try fm.moveItem(atPath: logFilePath, toPath: archivedPath)
                    moved = true
                } catch {
                    // Another thread may have created this file; try next index
                    index += 1
                }
            }
            if !moved {
                NSLog("[OneKeyLog] Failed to archive log file after 1000 attempts: %@", logFilePath)
            }
        }
        super.didArchiveLogFile(atPath: logFilePath, wasRolled: wasRolled)
    }
}

@objc public class OneKeyLog: NSObject {

    private static let maxMessageLength = 4096

    private static let configured: Bool = {
        let logsDir = logsDirectory

        // Ensure logs directory exists (matches Android's File(dir).mkdirs())
        try? FileManager.default.createDirectory(
            atPath: logsDir, withIntermediateDirectories: true
        )

        // Apply file protection: files inaccessible while device is locked before first unlock
        try? FileManager.default.setAttributes(
            [.protectionKey: FileProtectionType.completeUntilFirstUserAuthentication],
            ofItemAtPath: logsDir
        )

        let fileManager = OneKeyLogFileManager(logsDirectory: logsDir)
        // NOTE: DDLogFileManagerDefault.maximumNumberOfLogFiles counts ALL log files
        // including the current active file (app-latest.log).
        // Set to 7 = 1 active + 6 archived, matching Android MAX_HISTORY=6.
        fileManager.maximumNumberOfLogFiles = 7

        let logger = DDFileLogger(logFileManager: fileManager)
        logger.rollingFrequency = 86400       // daily rolling
        logger.maximumFileSize = 20_971_520   // 20 MB
        logger.logFormatter = OneKeyLogFormatter()

        DDLog.add(logger)
        return true
    }()

    // Use a lock to protect DateFormatter (not thread-safe per Apple docs)
    private static let timeFormatterLock = NSLock()
    private static let _timeFormatter: DateFormatter = {
        let fmt = DateFormatter()
        fmt.dateFormat = "HH:mm:ss"
        fmt.locale = Locale(identifier: "en_US_POSIX")
        return fmt
    }()

    private static func formattedTime(_ date: Date) -> String {
        timeFormatterLock.lock()
        defer { timeFormatterLock.unlock() }
        return _timeFormatter.string(from: date)
    }

    private static func truncate(_ message: String) -> String {
        if message.count > maxMessageLength {
            return String(message.prefix(maxMessageLength)) + "...(truncated)"
        }
        return message
    }

    private static func sanitizeForLog(_ str: String) -> String {
        return str.replacingOccurrences(of: "\n", with: " ")
                  .replacingOccurrences(of: "\r", with: " ")
    }

    /// Sanitize sensitive data from native-side log messages
    private static let nativeSensitivePatterns: [NSRegularExpression] = {
        let patterns = [
            "(?:0x)?[0-9a-fA-F]{64}",
            "\\b[5KL][1-9A-HJ-NP-Za-km-z]{50,51}\\b",
            "\\b[xyzXYZ](?:prv|pub)[1-9A-HJ-NP-Za-km-z]{107,108}\\b",
            "(?:\\b[a-z]{3,8}\\b[\\s,]+){11,}\\b[a-z]{3,8}\\b",
            "(?:Bearer|token[=:]?)\\s*[A-Za-z0-9_.\\-+/=]{20,}",
            "(?:eyJ|AAAA)[A-Za-z0-9+/=]{40,}",
        ]
        return patterns.compactMap { try? NSRegularExpression(pattern: $0) }
    }()

    private static func sanitizeSensitive(_ message: String) -> String {
        var result = message
        for regex in nativeSensitivePatterns {
            result = regex.stringByReplacingMatches(
                in: result,
                range: NSRange(result.startIndex..., in: result),
                withTemplate: "[REDACTED]"
            )
        }
        return result
    }

    private static func formatMessage(_ tag: String, _ level: String, _ message: String) -> String {
        let safeTag = sanitizeForLog(String(tag.prefix(64)))
        let safeMessage = sanitizeSensitive(sanitizeForLog(message))
        if safeTag == "JS" {
            return truncate(safeMessage)
        }
        let time = formattedTime(Date())
        return truncate("\(time) | \(level) : [\(safeTag)] \(safeMessage)")
    }

    @objc public static func debug(_ tag: String, _ message: String) {
        _ = configured
        DDLogDebug(formatMessage(tag, "DEBUG", message))
    }

    @objc public static func info(_ tag: String, _ message: String) {
        _ = configured
        DDLogInfo(formatMessage(tag, "INFO", message))
    }

    @objc public static func warn(_ tag: String, _ message: String) {
        _ = configured
        DDLogWarn(formatMessage(tag, "WARN", message))
    }

    @objc public static func error(_ tag: String, _ message: String) {
        _ = configured
        DDLogError(formatMessage(tag, "ERROR", message))
    }

    /// Returns the logs directory path (for getLogFilePaths / deleteLogFiles)
    static var logsDirectory: String {
        guard let cacheDir = NSSearchPathForDirectoriesInDomains(
            .cachesDirectory, .userDomainMask, true
        ).first else {
            // Fallback to tmp directory if Caches is somehow unavailable
            return (NSTemporaryDirectory() as NSString).appendingPathComponent("logs")
        }
        return (cacheDir as NSString).appendingPathComponent("logs")
    }
}

/// Formatter that outputs message only (matches Android's `%msg%n` pattern)
private class OneKeyLogFormatter: NSObject, DDLogFormatter {
    func format(message logMessage: DDLogMessage) -> String? {
        return logMessage.message
    }
}
