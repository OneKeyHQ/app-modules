import Foundation

/// Lightweight logging wrapper that dynamically dispatches to OneKeyLog through
/// the Objective-C runtime.
///
/// Using reflection (instead of `import ReactNativeNativeLogger`) keeps this
/// TurboModule from hard-linking the nitro-based native-logger module. When
/// OneKeyLog is unavailable the logs are silently dropped. Mirrors the Android
/// `SniConnectLogger` and the existing `BTLogger` / `SBLLogger`.
enum SniConnectLog {
  private static let tag = "SniConnect"

  // Swift classes are exposed to the ObjC runtime as `Module.ClassName`.
  private static let logClass: AnyObject? =
    (NSClassFromString("ReactNativeNativeLogger.OneKeyLog")
      ?? NSClassFromString("OneKeyLog")) as AnyObject?

  static func debug(_ message: String) { dispatch("debug::", message) }
  static func info(_ message: String) { dispatch("info::", message) }
  static func warn(_ message: String) { dispatch("warn::", message) }
  static func error(_ message: String) { dispatch("error::", message) }

  static func event(_ name: String, _ fields: [(String, Any?)]) -> String {
    let normalizedFields: [(String, Any?)] = [("event", name)] + fields
    let pairs = normalizedFields.map { key, value in
      "\(key)=\(sanitize(value))"
    }
    return pairs.joined(separator: " ")
  }

  static func shortHash(_ value: String?) -> String {
    guard let value, !value.isEmpty else {
      return "none"
    }

    var hash: UInt64 = 1_469_598_103_934_665_603
    for byte in value.utf8 {
      hash ^= UInt64(byte)
      hash &*= 1_099_511_628_211
    }
    return String(String(format: "%016llx", hash).prefix(12))
  }

  static func elapsedMs(since startedAt: Date) -> Int {
    max(0, Int(Date().timeIntervalSince(startedAt) * 1000))
  }

  static func ipFamily(_ ip: String) -> String {
    ip.contains(":") ? "ipv6" : "ipv4"
  }

  private static func dispatch(_ selectorName: String, _ message: String) {
    guard let cls = logClass else { return }
    let sel = NSSelectorFromString(selectorName)
    guard cls.responds(to: sel) else { return }
    _ = cls.perform(sel, with: tag, with: message)
  }

  private static func sanitize(_ value: Any?) -> String {
    guard let value else {
      return "none"
    }
    return String(describing: value)
      .replacingOccurrences(of: "\n", with: "_")
      .replacingOccurrences(of: "\r", with: "_")
      .replacingOccurrences(of: " ", with: "_")
  }
}
