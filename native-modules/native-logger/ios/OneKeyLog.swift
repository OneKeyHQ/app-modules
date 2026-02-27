import CocoaLumberjack

private let ddLogLevel: DDLogLevel = .debug

private class OneKeyLogFileManager: DDLogFileManagerDefault {
    private static let logPrefix = "app"
    private static let latestFileName = "\(logPrefix)-latest.log"
    private static let dateFormatter: DateFormatter = {
        let fmt = DateFormatter()
        fmt.dateFormat = "yyyy-MM-dd"
        fmt.locale = Locale(identifier: "en_US_POSIX")
        return fmt
    }()

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
            let dateStr = Self.dateFormatter.string(from: Date())
            let fm = FileManager.default

            // Find next available index for this date
            var index = 0
            while fm.fileExists(atPath: "\(dir)/\(Self.logPrefix)-\(dateStr).\(index).log") {
                index += 1
            }
            let archivedPath = "\(dir)/\(Self.logPrefix)-\(dateStr).\(index).log"
            try? fm.moveItem(atPath: logFilePath, toPath: archivedPath)
        }
        super.didArchiveLogFile(atPath: logFilePath, wasRolled: wasRolled)
    }
}

@objc public class OneKeyLog: NSObject {

    private static let configured: Bool = {
        let logsDir = logsDirectory

        // Ensure logs directory exists (matches Android's File(dir).mkdirs())
        try? FileManager.default.createDirectory(
            atPath: logsDir, withIntermediateDirectories: true
        )

        let fileManager = OneKeyLogFileManager(logsDirectory: logsDir)
        // DDFileLogger counts active file (app-latest.log) in this limit,
        // so set to 7 to keep 6 archived files (matching Android MAX_HISTORY=6)
        fileManager.maximumNumberOfLogFiles = 7

        let logger = DDFileLogger(logFileManager: fileManager)
        logger.rollingFrequency = 86400       // daily rolling
        logger.maximumFileSize = 20_971_520   // 20 MB
        logger.logFormatter = OneKeyLogFormatter()

        DDLog.add(logger)
        return true
    }()

    @objc public static func debug(_ tag: String, _ message: String) {
        _ = configured
        DDLogDebug("[\(tag)] \(message)")
    }

    @objc public static func info(_ tag: String, _ message: String) {
        _ = configured
        DDLogInfo("[\(tag)] \(message)")
    }

    @objc public static func warn(_ tag: String, _ message: String) {
        _ = configured
        DDLogWarn("[\(tag)] \(message)")
    }

    @objc public static func error(_ tag: String, _ message: String) {
        _ = configured
        DDLogError("[\(tag)] \(message)")
    }

    /// Returns the logs directory path (for getLogFilePaths / deleteLogFiles)
    static var logsDirectory: String {
        guard let cacheDir = NSSearchPathForDirectoriesInDomains(
            .cachesDirectory, .userDomainMask, true
        ).first else {
            // Fallback to tmp directory if Caches is somehow unavailable
            return NSTemporaryDirectory() + "logs"
        }
        return cacheDir + "/logs"
    }
}

/// Formatter that outputs message only (matches Android's `%msg%n` pattern)
private class OneKeyLogFormatter: NSObject, DDLogFormatter {
    func format(message logMessage: DDLogMessage) -> String? {
        return logMessage.message
    }
}
