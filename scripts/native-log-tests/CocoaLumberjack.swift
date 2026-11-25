@_exported import Foundation
public enum DDLogLevel { case debug }
open class DDLogFileManagerDefault: NSObject {
 public init(logsDirectory: String) {}
 open var newLogFileName: String { "" }
 open func isLogFile(withName: String) -> Bool { false }
 open func didArchiveLogFile(atPath: String, wasRolled: Bool) {}
 open func cleanupLogFiles() throws {}
 public var maximumNumberOfLogFiles: UInt = 0
 public var logFilesDiskQuota: UInt64 = 0
}
public protocol DDLogFormatter { func format(message: DDLogMessage) -> String? }
public class DDLogMessage { public var message = "" }
public class DDFileLogger {
 public init(logFileManager: DDLogFileManagerDefault) {}
 public var rollingFrequency: TimeInterval = 0
 public var maximumFileSize: UInt64 = 0
 public var logFormatter: DDLogFormatter?
}
public enum DDLog { public static func add(_ logger: DDFileLogger) {} }
public func DDLogInfo(_ message: String) {}
public func DDLogDebug(_ message: String) {}
public func DDLogWarn(_ message: String) {}
public func DDLogError(_ message: String) {}
