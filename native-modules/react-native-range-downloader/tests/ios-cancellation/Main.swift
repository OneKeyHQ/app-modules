import Foundation

public enum DownloadChannel: String {
  case bundle, apk, chart, firmware
  init?(fromString: String) { self.init(rawValue: fromString) }
  var stringValue: String { rawValue }
}

public struct RangeDownloadEvent {
  let channel: DownloadChannel
  let taskId: String
  let type: String
  let progress: Double
  let message: String
}

enum OneKeyLog {
  static func info(_ scope: String, _ message: String) {}
  static func error(_ scope: String, _ message: String) {}
}

@main
enum RunOwnerCancellationTests {
  static func main() async throws {
    for _ in 0..<20 {
      try await RangeDownloader.auditCancelledOwnerCannotReachReplacement()
      try await RangeDownloader.auditCancelWaitsForActualWriter()
      try await RangeDownloader.auditPermanentFallbackKeepsTaskOwner()
    }
    print("PASS 60 production RangeDownloader owner, cancellation-join, adoption, writer-drain, and fallback checks")
  }
}
