import Foundation
import CommonCrypto

enum FirmwareArtifactDeadlineError: Error {
  case exceeded
}

enum FirmwareArtifactWallClockDeadline {
  static func run<T>(
    timeoutSeconds: TimeInterval,
    operation: @escaping @Sendable () async throws -> T
  ) async throws -> T {
    let timeoutNanoseconds = UInt64(max(0.001, timeoutSeconds) * 1_000_000_000.0)
    return try await withThrowingTaskGroup(of: T.self) { group in
      group.addTask {
        try await operation()
      }
      group.addTask {
        try await Task.sleep(nanoseconds: timeoutNanoseconds)
        throw FirmwareArtifactDeadlineError.exceeded
      }
      defer { group.cancelAll() }
      guard let result = try await group.next() else {
        throw FirmwareArtifactDeadlineError.exceeded
      }
      return result
    }
  }
}

func firmwareArtifactDownloadKey(
  transactionId: String,
  expectedSize: Int64,
  expectedSha256: String
) -> String {
  "\(transactionId)|\(expectedSize)|\(expectedSha256)"
}

enum FirmwareBackgroundDownloadError: LocalizedError, CustomStringConvertible {
  case cancelled
  case invalidTask
  case deadlineExceeded
  case redirectRejected
  case responseRejected
  case sizeRejected
  case tlsRejected
  case transferFailed

  var errorDescription: String? {
    switch self {
    case .cancelled:
      return "ARTIFACT_CANCELLED: firmware background download was cancelled"
    case .invalidTask:
      return "ARTIFACT_PROTOCOL_INVALID: firmware background task is invalid"
    case .deadlineExceeded:
      return "ARTIFACT_DEADLINE_EXCEEDED: firmware download exceeded its deadline"
    case .redirectRejected:
      return "ARTIFACT_REDIRECT_REJECTED: firmware redirect changed canonical identity"
    case .responseRejected:
      return "ARTIFACT_PROTOCOL_INVALID: firmware response is invalid"
    case .sizeRejected:
      return "ARTIFACT_PROTOCOL_INVALID: firmware artifact size is invalid"
    case .tlsRejected:
      return "ARTIFACT_TLS_FAILED: firmware TLS validation failed"
    case .transferFailed:
      return "ARTIFACT_NETWORK_FAILED: firmware background transfer failed"
    }
  }

  var description: String {
    errorDescription ?? "ARTIFACT_NETWORK_FAILED: firmware background transfer failed"
  }
}

// MARK: - Dependency-free RangeDownloader logic (OCDS §4 / §5)
//
// This file holds the DETERMINISTIC, dependency-light pieces of the range
// downloader: HTTP-status classification, range planning, Content-Range parsing,
// Retry-After / backoff math, and the SHA-256 integrity hash. They were extracted
// VERBATIM (bodies unchanged) from `ReactNativeRangeDownloader.swift` so they can
// be compiled and unit-tested WITHOUT the NitroModules / ReactNativeNativeLogger
// dependencies or the background `URLSession` (which is device-only).
//
// `RangeDownloader` keeps using them via `RangeDownloadLogic.<fn>`. The Nitro wire
// projections (`wireOutcome` / `wireKind`) stay in the main module file because
// they depend on the codegen enums; everything here is pure Swift + Foundation +
// CommonCrypto.

// MARK: - Typed failure model (OCDS §4)
//
// The IN-PROCESS core returns this Swift-native typed class to its in-process
// caller (BundleUpdate); the Nitro shim maps it onto the regenerated wire enum
// `RangeDownloadOutcome` (completed | fallbackTransient | fallbackPermanent) so
// the failure class crosses the JS boundary as an EXPLICIT value, never inferred
// from incidental on-disk side effects (which §4 forbids). The core enum is a
// SEPARATE type from the generated wire `RangeFallbackKind` (same case set) so
// the core can carry an extra `failureClass` projection without the module-scope
// name colliding with the codegen typealias; `wireOutcome` / `wireKind` (in the
// main module file) do the 1:1 translation onto the generated enums.
public enum RangeDownloadClass: Equatable {
  case completed
  /// Resumable interruption — keep `.segN`, the concurrent path may resume.
  case fallbackTransient
  /// Concurrency fundamentally unusable for this object — segments discarded.
  case fallbackPermanent

  var isFallback: Bool { self != .completed }
}

/// Typed sub-classification of a fallback (in-process mirror of the generated
/// wire `RangeFallbackKind`). Used by the caller/analytics to know WHY without
/// parsing the reason string, and by the core to drive keep-vs-discard. Named
/// distinctly from the codegen `RangeFallbackKind` typealias to avoid a
/// module-scope name clash; `wireKind` (in the main module file) translates onto
/// the wire enum.
public enum RangeFallbackClass: String {
  case serverIgnoredRange
  case rangeUnsupported
  case authExpired
  case notFound
  case redirectRejected
  case checksumMismatch
  case multipartOrBadTotal
  case transientNetwork
  case throttled
  case budgetExhausted

  /// The §4 recovery class implied by this kind.
  var failureClass: RangeDownloadClass {
    switch self {
    case .transientNetwork, .throttled, .budgetExhausted:
      return .fallbackTransient
    case .serverIgnoredRange, .rangeUnsupported, .authExpired, .notFound,
         .redirectRejected, .checksumMismatch, .multipartOrBadTotal:
      return .fallbackPermanent
    }
  }
}

// MARK: - Pure logic namespace

/// Dependency-free static logic for the range downloader. Bodies are a verbatim
/// move from `RangeDownloader`; callsites there now call `RangeDownloadLogic.<fn>`.
public enum RangeDownloadLogic {

  // §5.4 backoff knobs (moved here because only `backoffDelay` consumes them).
  static let retryBaseDelaySeconds: Double = 1.0
  static let retryMaxDelaySeconds: Double = 30.0

  // MARK: - Range planning / probing

  static func planRanges(total: Int64, segments: Int) -> [(start: Int64, end: Int64)] {
    var out: [(Int64, Int64)] = []
    let chunk = (total + Int64(segments) - 1) / Int64(segments)
    var i = 0
    while i < segments {
      let start = Int64(i) * chunk
      if start >= total { break }
      let end = min(start + chunk - 1, total - 1)
      out.append((start, end))
      i += 1
    }
    return out
  }

  // MARK: - HTTP status classification (OCDS §4 table + catch-all)

  /// Maps an HTTP status on a Range request to a §4 fallback kind. Used by the
  /// download-finish delegate to decide keep-vs-discard. Status 206 is handled by
  /// the caller (validated, not a fallback); 200 is `serverIgnoredRange`.
  static func classifyStatus(_ status: Int) -> RangeFallbackClass {
    switch status {
    case 200:
      return .serverIgnoredRange
    case 416:
      // §4: 416 to a resume request → Transient (re-evaluate size, keep segments).
      return .transientNetwork
    case 401, 403:
      return .authExpired
    case 404, 410:
      return .notFound
    case 408, 429:
      return .throttled
    case 501, 505:
      // Explicit Permanent carve-outs from the 5xx → Transient default.
      return .rangeUnsupported
    case 500...599:
      return .throttled
    case 400...499:
      // Default 4xx → Permanent (408/429 handled above).
      return .rangeUnsupported
    default:
      // Anything else / unknown → Permanent per the §4 catch-all.
      return .rangeUnsupported
    }
  }

  /// Parses a `Retry-After` header value (delta-seconds form only; HTTP-date form
  /// is treated as absent). Returns nil when missing/unparseable.
  static func parseRetryAfterSeconds(_ value: String?) -> Double? {
    guard let value = value?.trimmingCharacters(in: .whitespaces), !value.isEmpty,
          let seconds = Double(value), seconds >= 0 else { return nil }
    return seconds
  }

  static func parseContentRangeTotal(_ header: String) -> Int64? {
    // "bytes 0-0/65226095"
    guard let slash = header.lastIndex(of: "/") else { return nil }
    let tail = header[header.index(after: slash)...]
    return Int64(tail.trimmingCharacters(in: .whitespaces))
  }

  /// Parses the start/end of a "bytes <start>-<end>/<total>" Content-Range.
  static func parseContentRangeBounds(_ header: String) -> (start: Int64, end: Int64)? {
    // Drop the leading "bytes " and the trailing "/<total>".
    let trimmed = header.trimmingCharacters(in: .whitespaces)
    guard let spaceIdx = trimmed.firstIndex(of: " ") else { return nil }
    var rangePart = String(trimmed[trimmed.index(after: spaceIdx)...])
    if let slash = rangePart.firstIndex(of: "/") {
      rangePart = String(rangePart[..<slash])
    }
    let bounds = rangePart.split(separator: "-", maxSplits: 1).map { String($0) }
    guard bounds.count == 2,
          let start = Int64(bounds[0].trimmingCharacters(in: .whitespaces)),
          let end = Int64(bounds[1].trimmingCharacters(in: .whitespaces)) else { return nil }
    return (start, end)
  }

  /// §5.4: exponential backoff base*2^(attempt-1), capped, with full jitter so N
  /// segments don't retry in lockstep. A server `Retry-After` overrides it.
  static func backoffDelay(attempt: Int, retryAfter: Double?) -> Double {
    if let retryAfter = retryAfter { return min(retryAfter, retryMaxDelaySeconds * 2) }
    let exp = retryBaseDelaySeconds * pow(2.0, Double(max(0, attempt - 1)))
    let capped = min(exp, retryMaxDelaySeconds)
    // Full jitter in [0, capped].
    return Double.random(in: 0...capped)
  }

  // MARK: - Integrity (§5.5)

  static func calculateSHA256(_ filePath: String) -> String? {
    let fm = FileManager.default
    guard fm.fileExists(atPath: filePath),
          let fileHandle = FileHandle(forReadingAtPath: filePath) else { return nil }
    defer { try? fileHandle.close() }
    var context = CC_SHA256_CTX()
    CC_SHA256_Init(&context)
    while autoreleasepool(invoking: { () -> Bool in
      let data = fileHandle.readData(ofLength: 8192)
      if data.isEmpty { return false }
      data.withUnsafeBytes { CC_SHA256_Update(&context, $0.baseAddress, CC_LONG(data.count)) }
      return true
    }) {}
    var hash = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
    CC_SHA256_Final(&hash, &context)
    return hash.map { String(format: "%02x", $0) }.joined()
  }
}
