import CocoaLumberjack

private let ddLogLevel: DDLogLevel = .debug

private class OneKeyLogFileManager: DDLogFileManagerDefault {
    private static let logPrefix = "app"
    private static let latestFileName = "\(logPrefix)-latest.log"
    private static let dateFormatterLock = NSLock()
    private static let archiveLock = NSLock()
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
        Self.archiveLock.lock()
        defer { Self.archiveLock.unlock() }

        guard wasRolled else {
            runCleanup()
            return
        }

        let dir = (logFilePath as NSString).deletingLastPathComponent
        let dateStr = Self.formattedDate(Date())
        let fm = FileManager.default

        // Find next available index, retry on move failure to handle TOCTOU race
        var index = 0
        var archivedPath = logFilePath
        var moved = false
        while !moved && index < 1000 {
            archivedPath = "\(dir)/\(Self.logPrefix)-\(dateStr).\(index).log"
            if fm.fileExists(atPath: archivedPath) {
                index += 1
                continue
            }
            do {
                try fm.moveItem(atPath: logFilePath, toPath: archivedPath)
                moved = true
            } catch {
                // Another callback may have already moved the source file.
                if !fm.fileExists(atPath: logFilePath) {
                    break
                }
                let nsError = error as NSError
                // Retry only for recoverable "target exists" collisions.
                if nsError.domain == NSCocoaErrorDomain &&
                    nsError.code == NSFileWriteFileExistsError {
                    index += 1
                    continue
                }
                NSLog(
                    "[OneKeyLog] Failed to archive log file move (%@:%ld): %@",
                    nsError.domain,
                    nsError.code,
                    nsError.localizedDescription
                )
                break
            }
        }
        if !moved {
            NSLog("[OneKeyLog] Failed to archive log file after 1000 attempts: %@", logFilePath)
        }
        runCleanup()
    }

    private func runCleanup() {
        do {
            try cleanupLogFiles()
        } catch {
            NSLog("[OneKeyLog] Failed to cleanup log files: %@", (error as NSError).localizedDescription)
        }
    }
}

@objc public class OneKeyLog: NSObject {

    private static let maxMessageLength = 4096

    private struct TokenBucket {
        let ratePerSecond: Double
        let burstCapacity: Double
        var tokens: Double
        var lastRefillAt: TimeInterval

        mutating func allow(at now: TimeInterval) -> Bool {
            let elapsed = max(0, now - lastRefillAt)
            if elapsed > 0 {
                tokens = min(burstCapacity, tokens + elapsed * ratePerSecond)
                lastRefillAt = now
            }
            guard tokens >= 1 else { return false }
            tokens -= 1
            return true
        }
    }

    private static let debugInfoRatePerSecond = 400.0
    private static let debugInfoBurst = 2000.0
    private static let warnRatePerSecond = 1000.0
    private static let warnBurst = 2000.0

    private static var rateLimitBuckets: [String: TokenBucket] = {
        let now = Date().timeIntervalSinceReferenceDate
        return [
            "DEBUG": TokenBucket(
                ratePerSecond: debugInfoRatePerSecond,
                burstCapacity: debugInfoBurst,
                tokens: debugInfoBurst,
                lastRefillAt: now
            ),
            "INFO": TokenBucket(
                ratePerSecond: debugInfoRatePerSecond,
                burstCapacity: debugInfoBurst,
                tokens: debugInfoBurst,
                lastRefillAt: now
            ),
            "WARN": TokenBucket(
                ratePerSecond: warnRatePerSecond,
                burstCapacity: warnBurst,
                tokens: warnBurst,
                lastRefillAt: now
            ),
        ]
    }()
    private static var droppedCounts: [String: Int] = [:]
    private static var lastDropReportAt = Date().timeIntervalSinceReferenceDate
    private static let rateLimitLock = NSLock()

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

    private static func rateLimitReportLocked(now: TimeInterval) -> String? {
        guard now - lastDropReportAt >= 1.0 else { return nil }
        guard !droppedCounts.isEmpty else {
            lastDropReportAt = now
            return nil
        }
        let ordered = ["DEBUG", "INFO", "WARN"]
        let parts = ordered.compactMap { level -> String? in
            guard let count = droppedCounts[level], count > 0 else { return nil }
            return "\(level)=\(count)"
        }
        droppedCounts.removeAll()
        lastDropReportAt = now
        guard !parts.isEmpty else { return nil }
        return "[OneKeyLog] Rate-limited logs (last 1s): \(parts.joined(separator: ", "))"
    }

    private static func evaluateRateLimit(for level: String) -> (drop: Bool, report: String?) {
        rateLimitLock.lock()
        defer { rateLimitLock.unlock() }

        let now = Date().timeIntervalSinceReferenceDate
        var report = rateLimitReportLocked(now: now)

        // Never limit error logs.
        if level == "ERROR" {
            return (false, report)
        }

        guard var bucket = rateLimitBuckets[level] else {
            return (false, report)
        }
        let allowed = bucket.allow(at: now)
        rateLimitBuckets[level] = bucket
        if allowed {
            return (false, report)
        }
        droppedCounts[level, default: 0] += 1
        report = rateLimitReportLocked(now: now) ?? report
        return (true, report)
    }

    private static func log(
        _ level: String,
        _ tag: String,
        _ message: String,
        writer: (String) -> Void
    ) {
        _ = configured
        let decision = evaluateRateLimit(for: level)
        if let report = decision.report {
            DDLogWarn(report)
        }
        if decision.drop { return }
        writer(formatMessage(tag, level, message))
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
        log("DEBUG", tag, message) { DDLogDebug($0) }
    }

    @objc public static func info(_ tag: String, _ message: String) {
        log("INFO", tag, message) { DDLogInfo($0) }
    }

    @objc public static func warn(_ tag: String, _ message: String) {
        log("WARN", tag, message) { DDLogWarn($0) }
    }

    @objc public static func error(_ tag: String, _ message: String) {
        log("ERROR", tag, message) { DDLogError($0) }
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
