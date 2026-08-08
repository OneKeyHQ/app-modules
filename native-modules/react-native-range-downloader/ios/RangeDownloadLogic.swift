import Foundation
import CommonCrypto

enum FirmwareArtifactStoreError: Error, CustomStringConvertible {
  case invalidInput(String)
  case downloadFailed(String)
  case integrityMismatch(String)
  case readerInvalid(String)
  case archiveInvalid(String)

  var description: String {
    switch self {
    case let .invalidInput(message), let .downloadFailed(message),
      let .integrityMismatch(message), let .readerInvalid(message),
      let .archiveInvalid(message):
      return message
    }
  }
}

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
  taskId: String,
  expectedSize: Int64?,
  expectedSha256: String?,
  downloadToken: String
) -> String {
  "\(transactionId)|\(taskId)|\(expectedSize.map(String.init) ?? "unknown")|\(expectedSha256 ?? "unknown")|\(downloadToken)"
}

enum FirmwareArchiveDiscoveredEntriesIssue: Equatable {
  case empty
  case duplicateName
}

func firmwareArchiveDiscoveredEntriesIssue(
  _ entryNames: [String]
) -> FirmwareArchiveDiscoveredEntriesIssue? {
  guard !entryNames.isEmpty else {
    return .empty
  }
  var names = Set<String>()
  guard entryNames.allSatisfy({ names.insert($0).inserted }) else {
    return .duplicateName
  }
  return nil
}

func firmwareArtifactDownloadToken(
  expectedSha256: String?,
  url: String
) -> String {
  if let expectedSha256 {
    return expectedSha256.lowercased()
  }
  let urlData = Data(url.utf8)
  var hash = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
  urlData.withUnsafeBytes {
    _ = CC_SHA256($0.baseAddress, CC_LONG(urlData.count), &hash)
  }
  return hash.map { String(format: "%02x", $0) }.joined()
}

func firmwareArtifactPartialFileName(
  transactionId: String,
  taskId: String,
  downloadToken: String
) -> String {
  let transactionData = Data(transactionId.utf8)
  var hash = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
  transactionData.withUnsafeBytes {
    _ = CC_SHA256($0.baseAddress, CC_LONG(transactionData.count), &hash)
  }
  let transactionToken = hash
    .prefix(8)
    .map { String(format: "%02x", $0) }
    .joined()
  return "\(downloadToken).\(taskId).\(transactionToken).partial"
}

func firmwareArtifactContentRangeIsValid(
  _ value: String,
  expectedStart: Int64,
  expectedTotal: Int64?,
  maxBytes: Int64
) -> Bool {
  guard value.lowercased().hasPrefix("bytes ") else {
    return false
  }
  guard
    let bounds = RangeDownloadLogic.parseContentRangeBounds(value),
    let total = RangeDownloadLogic.parseContentRangeTotal(value)
  else {
    return false
  }
  let totalMatches = expectedTotal.map { total == $0 } ?? (total > 0 && total <= maxBytes)
  return bounds.start == expectedStart &&
    bounds.end >= bounds.start &&
    bounds.end < total &&
    totalMatches
}

func firmwareArtifactResponseFits(
  expectedContentLength: Int64,
  baseOffset: Int64,
  maxBytes: Int64
) -> Bool {
  guard baseOffset >= 0, baseOffset <= maxBytes else {
    return false
  }
  return expectedContentLength < 0 ||
    expectedContentLength <= maxBytes - baseOffset
}

let firmwareArtifactFinalGrace: TimeInterval = 24 * 60 * 60
let firmwareArtifactPartialGrace: TimeInterval = 7 * 24 * 60 * 60
let firmwareArtifactScratchGrace: TimeInterval = firmwareArtifactPartialGrace
let firmwareArtifactMaxBytes: Int64 = 512 * 1024 * 1024
let firmwareArtifactMaxReadBytes: Int64 = 256 * 1024

func firmwareArtifactExactInt64(
  _ value: Double,
  minimum: Int64,
  maximum: Int64
) -> Int64? {
  guard
    minimum <= maximum,
    value.isFinite,
    value.rounded() == value,
    let converted = Int64(exactly: value),
    converted >= minimum,
    converted <= maximum
  else {
    return nil
  }
  return converted
}

func firmwareArtifactIdentifierIsSafe(_ value: String) -> Bool {
  value.range(
    of: "^[A-Za-z0-9._:-]{1,160}$",
    options: .regularExpression
  ) != nil
}

private func firmwareArtifactScratchNameIsValid(
  _ name: String,
  prefix: String
) -> Bool {
  guard name.hasPrefix(prefix) else {
    return false
  }
  let rawUUID = String(name.dropFirst(prefix.count))
  guard
    rawUUID.count == 36,
    let uuid = UUID(uuidString: rawUUID)
  else {
    return false
  }
  return uuid.uuidString.caseInsensitiveCompare(rawUUID) == .orderedSame
}

private func firmwareArtifactEntrySize(
  _ entryURL: URL,
  values: URLResourceValues,
  fileManager: FileManager
) -> Int64 {
  if values.isRegularFile == true {
    return Int64(values.fileSize ?? 0)
  }
  guard
    values.isDirectory == true,
    let enumerator = fileManager.enumerator(
      at: entryURL,
      includingPropertiesForKeys: [
        .isRegularFileKey,
        .isSymbolicLinkKey,
        .fileSizeKey,
      ],
      options: [],
      errorHandler: nil
    )
  else {
    return 0
  }
  var size: Int64 = 0
  for case let childURL as URL in enumerator {
    guard
      let childValues = try? childURL.resourceValues(
        forKeys: [.isRegularFileKey, .isSymbolicLinkKey, .fileSizeKey]
      ),
      childValues.isRegularFile == true,
      childValues.isSymbolicLink != true
    else {
      continue
    }
    size += Int64(childValues.fileSize ?? 0)
  }
  return size
}

func sweepFirmwareArtifactOrphansAtRoot(
  _ rootURL: URL,
  retainedSha256: Set<String>,
  activeSha256: Set<String>,
  openPaths: Set<String>,
  now: Date = Date(),
  fileManager: FileManager = .default
) throws -> (deletedFiles: Int, deletedBytes: Int64) {
  var deletedFiles = 0
  var deletedBytes: Int64 = 0
  let resourceKeys: Set<URLResourceKey> = [
    .isRegularFileKey,
    .isDirectoryKey,
    .isSymbolicLinkKey,
    .contentModificationDateKey,
    .fileSizeKey,
  ]
  let entries = try fileManager.contentsOfDirectory(
    at: rootURL,
    includingPropertiesForKeys: Array(resourceKeys),
    options: []
  )
  for entryURL in entries {
    let name = entryURL.lastPathComponent
    let values = try entryURL.resourceValues(forKeys: resourceKeys)
    let isArchiveScratch = firmwareArtifactScratchNameIsValid(
      name,
      prefix: "archive-"
    )
    let isPromoteScratch = firmwareArtifactScratchNameIsValid(
      name,
      prefix: ".promote-"
    )
    let isScratchCandidate = values.isSymbolicLink != true &&
      ((isArchiveScratch && values.isDirectory == true) ||
        (isPromoteScratch && values.isRegularFile == true))
    if isScratchCandidate {
      guard
        let modifiedAt = values.contentModificationDate,
        now.timeIntervalSince(modifiedAt) >= firmwareArtifactScratchGrace
      else {
        continue
      }
      let size = firmwareArtifactEntrySize(
        entryURL,
        values: values,
        fileManager: fileManager
      )
      try fileManager.removeItem(at: entryURL)
      deletedFiles += 1
      deletedBytes += size
      continue
    }

    guard
      values.isRegularFile == true,
      values.isSymbolicLink != true,
      name.count >= 64
    else {
      continue
    }
    let sha256 = String(name.prefix(64))
    guard
      sha256.range(of: "^[a-f0-9]{64}$", options: .regularExpression) != nil,
      !retainedSha256.contains(sha256),
      !activeSha256.contains(sha256),
      !openPaths.contains(entryURL.path)
    else {
      continue
    }
    let grace: TimeInterval
    if name.hasSuffix(".bin") {
      grace = firmwareArtifactFinalGrace
    } else if name.hasSuffix(".partial") {
      grace = firmwareArtifactPartialGrace
    } else {
      continue
    }
    guard
      let modifiedAt = values.contentModificationDate,
      now.timeIntervalSince(modifiedAt) >= grace
    else {
      continue
    }
    let size = Int64(values.fileSize ?? 0)
    try fileManager.removeItem(at: entryURL)
    deletedFiles += 1
    deletedBytes += size
  }
  return (deletedFiles, deletedBytes)
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
