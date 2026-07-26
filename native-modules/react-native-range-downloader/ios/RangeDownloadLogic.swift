import Foundation
import CommonCrypto

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

  static func sameArtifactObjectIdentity(
    leftSupportsRange: Bool,
    leftTotal: Int64,
    leftStrongETag: String?,
    leftLastModified: String?,
    rightSupportsRange: Bool,
    rightTotal: Int64,
    rightStrongETag: String?,
    rightLastModified: String?
  ) -> Bool {
    guard leftSupportsRange == rightSupportsRange,
          leftTotal == rightTotal else {
      return false
    }
    if leftStrongETag != nil || rightStrongETag != nil {
      return leftStrongETag == rightStrongETag
    }
    return leftLastModified?.trimmingCharacters(in: .whitespacesAndNewlines) ==
      rightLastModified?.trimmingCharacters(in: .whitespacesAndNewlines)
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

  static func secureHexEquals(_ lhs: String, _ rhs: String) -> Bool {
    let left = Array(lhs.lowercased().utf8)
    let right = Array(rhs.lowercased().utf8)
    guard left.count == right.count else { return false }
    var difference: UInt8 = 0
    for index in left.indices {
      difference |= left[index] ^ right[index]
    }
    return difference == 0
  }
}

enum FirmwarePinnedRouteValidation {
  static func isValid(hostname: String, resolvedIP: String) -> Bool {
    return isValidHostname(hostname) && isPublicIP(resolvedIP)
  }

  private static func isValidHostname(_ hostname: String) -> Bool {
    guard !hostname.isEmpty, hostname.count <= 253 else {
      return false
    }
    let pattern =
      "^(?=.{1,253}$)([A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)" +
      "(\\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$"
    guard hostname.range(of: pattern, options: .regularExpression) != nil else {
      return false
    }
    return parseIPv4(hostname) == nil && parseIPv6(hostname) == nil
  }

  private static func isPublicIP(_ ip: String) -> Bool {
    guard !ip.isEmpty,
          ip.trimmingCharacters(in: .whitespacesAndNewlines) == ip,
          !ip.contains("["),
          !ip.contains("]"),
          !ip.contains("%") else {
      return false
    }
    if let bytes = parseIPv4(ip) {
      return !isForbiddenIPv4(bytes)
    }
    if let bytes = parseIPv6(ip) {
      return !isForbiddenIPv6(bytes)
    }
    return false
  }

  private static func parseIPv4(_ ip: String) -> [UInt8]? {
    var address = in_addr()
    guard ip.withCString({
      inet_pton(AF_INET, $0, &address)
    }) == 1 else {
      return nil
    }
    return withUnsafeBytes(of: &address) {
      Array($0.bindMemory(to: UInt8.self))
    }
  }

  private static func parseIPv6(_ ip: String) -> [UInt8]? {
    var address = in6_addr()
    guard ip.withCString({
      inet_pton(AF_INET6, $0, &address)
    }) == 1 else {
      return nil
    }
    return withUnsafeBytes(of: &address) {
      Array($0.bindMemory(to: UInt8.self))
    }
  }

  private static func isForbiddenIPv4(_ bytes: [UInt8]) -> Bool {
    let first = bytes[0]
    let second = bytes[1]
    let third = bytes[2]
    if first == 0 || first == 10 || first == 127 { return true }
    if first == 100 && (second & 0xC0) == 0x40 { return true }
    if first == 169 && second == 254 { return true }
    if first == 172 && (16...31).contains(second) { return true }
    if first == 192 && second == 168 { return true }
    if first == 192 && second == 0 && (third == 0 || third == 2) {
      return true
    }
    if first == 198 && (second == 18 || second == 19) { return true }
    if first == 198 && second == 51 && third == 100 { return true }
    if first == 203 && second == 0 && third == 113 { return true }
    return first >= 224
  }

  private static func isForbiddenIPv6(_ bytes: [UInt8]) -> Bool {
    if bytes.allSatisfy({ $0 == 0 }) { return true }
    if bytes[0...14].allSatisfy({ $0 == 0 }) && bytes[15] == 1 {
      return true
    }
    if bytes[0] == 0xFF { return true }
    if bytes[0] == 0xFE && (bytes[1] & 0xC0) == 0x80 { return true }
    if (bytes[0] & 0xFE) == 0xFC { return true }
    if bytes[0] == 0x01 &&
       bytes[1] == 0x00 &&
       bytes[2...7].allSatisfy({ $0 == 0 }) {
      return true
    }
    if bytes[0] == 0x20 && bytes[1] == 0x01 {
      if bytes[2] == 0x00 && bytes[3] == 0x00 { return true }
      if bytes[2] == 0x00 && (bytes[3] & 0xF0) == 0x10 { return true }
      if bytes[2] == 0x00 && bytes[3] == 0x02 { return true }
      if bytes[2] == 0x0D && bytes[3] == 0xB8 { return true }
    }
    if bytes[0] == 0x20 && bytes[1] == 0x02 { return true }
    if isNat64WellKnown(bytes) {
      return isForbiddenIPv4(Array(bytes[12...15]))
    }
    if isNat64LocalUse(bytes) { return true }
    if bytes[0...11].allSatisfy({ $0 == 0 }) { return true }
    if bytes[0...9].allSatisfy({ $0 == 0 }) &&
       bytes[10] == 0xFF &&
       bytes[11] == 0xFF {
      return isForbiddenIPv4(Array(bytes[12...15]))
    }
    return false
  }

  private static func isNat64WellKnown(_ bytes: [UInt8]) -> Bool {
    return bytes[0] == 0x00 &&
      bytes[1] == 0x64 &&
      bytes[2] == 0xFF &&
      bytes[3] == 0x9B &&
      bytes[4...11].allSatisfy({ $0 == 0 })
  }

  private static func isNat64LocalUse(_ bytes: [UInt8]) -> Bool {
    return bytes[0] == 0x00 &&
      bytes[1] == 0x64 &&
      bytes[2] == 0xFF &&
      bytes[3] == 0x9B &&
      bytes[4] == 0x00 &&
      bytes[5] == 0x01
  }
}

final class FirmwarePinnedResolverRegistry {
  struct Route: Equatable {
    let hostname: String
    let resolvedIP: String
  }

  private let maxEntries: Int
  private var routes: [ObjectIdentifier: Route] = [:]
  private var reusableClasses: [AnyClass] = []
  private var allocatedClasses = 0

  init(maxEntries: Int = 32) {
    self.maxEntries = maxEntries
  }

  func acquire(
    hostname: String,
    resolvedIP: String,
    allocateClass: () -> AnyClass
  ) -> AnyClass? {
    let resolverClass: AnyClass
    if let reusableClass = reusableClasses.popLast() {
      resolverClass = reusableClass
    } else {
      guard allocatedClasses < maxEntries else {
        return nil
      }
      resolverClass = allocateClass()
      allocatedClasses += 1
    }
    routes[ObjectIdentifier(resolverClass)] = Route(
      hostname: hostname.lowercased(),
      resolvedIP: resolvedIP
    )
    return resolverClass
  }

  func resolve(domain: String, resolverClass: AnyClass) -> String? {
    guard let route = routes[ObjectIdentifier(resolverClass)],
          domain.caseInsensitiveCompare(route.hostname) == .orderedSame else {
      return nil
    }
    return route.resolvedIP
  }

  func release(resolverClass: AnyClass) {
    let identifier = ObjectIdentifier(resolverClass)
    guard routes.removeValue(forKey: identifier) != nil else {
      return
    }
    reusableClasses.append(resolverClass)
  }

  var activeEntryCount: Int {
    routes.count
  }

  var allocatedEntryCount: Int {
    allocatedClasses
  }
}

final class FirmwareArtifactRunOwnership<Owner: Hashable> {
  private let lock = NSLock()
  private var owners: [String: Owner] = [:]

  func acquire(artifactId: String, owner: Owner) -> Bool {
    lock.lock()
    defer { lock.unlock() }
    if let existing = owners[artifactId] {
      return existing == owner
    }
    owners[artifactId] = owner
    return true
  }

  func release(artifactId: String, owner: Owner) {
    lock.lock()
    defer { lock.unlock() }
    guard owners[artifactId] == owner else { return }
    owners.removeValue(forKey: artifactId)
  }
}

extension NSLock {
  func withFirmwareLock<T>(_ body: () throws -> T) rethrows -> T {
    lock()
    defer { unlock() }
    return try body()
  }
}
