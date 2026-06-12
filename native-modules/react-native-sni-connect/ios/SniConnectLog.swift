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

  private static func dispatch(_ selectorName: String, _ message: String) {
    guard let cls = logClass else { return }
    let sel = NSSelectorFromString(selectorName)
    guard cls.responds(to: sel) else { return }
    _ = cls.perform(sel, with: tag, with: message)
  }
}
