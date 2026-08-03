import Foundation

/// Boundary validation/normalization for SNI request inputs.
///
/// The module connects to a caller-supplied IP while preserving the TLS SNI/Host
/// of `hostname`. Because the connect target is caller-controlled, every field that
/// reaches the network layer is validated here to prevent SSRF, scheme/host/port
/// override, cleartext downgrade and CR/LF header injection.
enum SniConnectValidation {

  enum ValidationError: Error {
    case invalidIP(String)
    case forbiddenIP(String)
    case invalidHostname(String)
    case invalidMethod(String)
    case invalidPath(String)
    case invalidHeader(String)
    case invalidRequestId(String)
    case invalidTimeout(Double)
    case invalidBody
    case resourceLimit(String)
  }

  static let maxRequestIdBytes = 128
  static let maxTimeoutMillis = 120_000.0
  static let maxPathBytes = 8 * 1024
  static let maxRequestBodyBytes = 1024 * 1024
  static let maxResponseBodyBytes = 10 * 1024 * 1024
  static let maxHeaderCount = 64
  static let maxHeaderNameBytes = 128
  static let maxHeaderValueBytes = 8 * 1024
  static let maxTotalHeaderBytes = 32 * 1024
  static let maxActiveRequests = 64
  static let maxActiveRequestsPerPair = 16
  static let maxPendingRequests = 256

  /// HTTP methods the module is allowed to issue.
  private static let allowedMethods: Set<String> = [
    "GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS",
  ]

  private static let moduleOwnedHeaders: Set<String> = [
    "host",
    "content-length",
    "accept-encoding",
    "x-emascurl-config-id",
  ]

  private static let unsafeHeaders: Set<String> = [
    "connection",
    "keep-alive",
    "te",
    "trailer",
    "transfer-encoding",
    "upgrade",
    "expect",
  ]

  private static let headerTokenPattern = "^[!#$%&'*+.^_`|~0-9A-Za-z-]+$"

  static func validateRequestId(_ requestId: String?) throws {
    guard let requestId = requestId else { return }
    if requestId.isEmpty ||
       containsControlCharacters(requestId) ||
       byteCount(requestId) > maxRequestIdBytes {
      throw ValidationError.invalidRequestId(requestId)
    }
  }

  static func validateTimeout(_ timeout: Double) throws {
    if !timeout.isFinite || timeout < 1 || timeout > maxTimeoutMillis {
      throw ValidationError.invalidTimeout(timeout)
    }
  }

  static func validateBody(_ body: String?) throws {
    if let body = body, byteCount(body) > maxRequestBodyBytes {
      throw ValidationError.invalidBody
    }
  }

  static func validateMethodBody(method: String, body: String?) throws {
    if (method == "GET" || method == "HEAD") && body != nil {
      throw ValidationError.invalidBody
    }
    if (method == "POST" || method == "PUT" || method == "PATCH") && body == nil {
      throw ValidationError.invalidBody
    }
  }

  /// Validate and uppercase the HTTP method.
  static func normalizeMethod(_ method: String) throws -> String {
    if containsControlCharacters(method) {
      throw ValidationError.invalidMethod(method)
    }
    let upper = method.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
    guard allowedMethods.contains(upper) else {
      throw ValidationError.invalidMethod(method)
    }
    return upper
  }

  /// Validate `hostname` as a DNS host (used for SNI, Host header and cert matching).
  static func validateHostname(_ hostname: String) throws {
    guard hostname.count <= 253, !hostname.isEmpty else {
      throw ValidationError.invalidHostname(hostname)
    }
    // Labels: 1-63 chars, alphanumeric + hyphen, not starting/ending with hyphen.
    let pattern = "^(?=.{1,253}$)([A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)(\\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$"
    guard hostname.range(of: pattern, options: .regularExpression) != nil else {
      throw ValidationError.invalidHostname(hostname)
    }
    if parseIPv4(hostname) != nil || parseIPv6(hostname) != nil {
      throw ValidationError.invalidHostname(hostname)
    }
  }

  /// Validate `path`: must be a relative path/query only. Reject absolute URLs
  /// (scheme/authority), protocol-relative URLs and control characters so the
  /// caller cannot override scheme/host/port or downgrade to cleartext.
  static func normalizePath(_ path: String) throws -> String {
    let trimmed = path.trimmingCharacters(in: .whitespacesAndNewlines)
    if containsControlCharacters(trimmed) {
      throw ValidationError.invalidPath(path)
    }
    if byteCount(trimmed) > maxPathBytes {
      throw ValidationError.invalidPath(path)
    }
    // Reject anything that looks like it carries a scheme or authority.
    if trimmed.contains("://") || trimmed.hasPrefix("//") {
      throw ValidationError.invalidPath(path)
    }
    // A bare scheme like "javascript:..." has no "//"; reject any leading scheme.
    if let schemeRange = trimmed.range(of: "^[A-Za-z][A-Za-z0-9+.-]*:", options: .regularExpression),
       schemeRange.lowerBound == trimmed.startIndex {
      throw ValidationError.invalidPath(path)
    }
    if trimmed.isEmpty { return "/" }
    return trimmed.hasPrefix("/") ? trimmed : "/" + trimmed
  }

  /// Validate header names/values: reject CR/LF/control characters (header
  /// injection) and the Host header (the module sets Host itself).
  static func validateHeaders(_ headers: [String: String]) throws {
    _ = try normalizeHeaders(headers)
  }

  static func normalizeHeaders(_ headers: [String: String]) throws -> [String: String] {
    if headers.count > maxHeaderCount {
      throw ValidationError.invalidHeader("too many headers")
    }

    var totalBytes = 0
    var normalizedHeaders: [String: String] = [:]
    for (key, value) in headers {
      let keyBytes = byteCount(key)
      let valueBytes = byteCount(value)
      totalBytes += keyBytes + valueBytes

      if key.isEmpty ||
         containsControlCharacters(key) ||
         containsControlCharacters(value) ||
         keyBytes > maxHeaderNameBytes ||
         valueBytes > maxHeaderValueBytes ||
         key.range(of: headerTokenPattern, options: .regularExpression) == nil {
        throw ValidationError.invalidHeader(key)
      }

      let lowerKey = key.lowercased()
      if lowerKey.hasPrefix(":") || lowerKey.hasPrefix("proxy-") || unsafeHeaders.contains(lowerKey) {
        throw ValidationError.invalidHeader(key)
      }
      if moduleOwnedHeaders.contains(lowerKey) {
        continue
      }
      normalizedHeaders[key] = value
    }
    if totalBytes > maxTotalHeaderBytes {
      throw ValidationError.invalidHeader("headers too large")
    }
    return normalizedHeaders
  }

  static func containsControlCharacters(_ s: String) -> Bool {
    return s.unicodeScalars.contains { $0.value < 0x20 || $0.value == 0x7F }
  }

  private static func byteCount(_ s: String) -> Int {
    return s.lengthOfBytes(using: .utf8)
  }

  // MARK: - IP validation

  /// Validate `ip` is a literal IPv4/IPv6 address (never a hostname) and routes
  /// to a public/global-unicast destination. Rejects loopback, private,
  /// link-local (incl. 169.254.169.254 metadata), CGNAT, multicast and reserved.
  static func validatePublicIP(_ ip: String) throws {
    if ip.isEmpty ||
       ip.trimmingCharacters(in: .whitespacesAndNewlines) != ip ||
       ip.contains("[") ||
       ip.contains("]") ||
       ip.contains("%") {
      throw ValidationError.invalidIP(ip)
    }
    if let v4 = parseIPv4(ip) {
      if isForbiddenIPv4(v4) { throw ValidationError.forbiddenIP(ip) }
      return
    }
    if let v6 = parseIPv6(ip) {
      if isForbiddenIPv6(v6) { throw ValidationError.forbiddenIP(ip) }
      return
    }
    throw ValidationError.invalidIP(ip)
  }

  static func canonicalIPKey(_ ip: String) -> String {
    if let v4 = parseIPv4(ip) {
      return "4:" + v4.map { String($0) }.joined(separator: ".")
    }
    if let v6 = parseIPv6(ip) {
      return "6:" + v6.map { String($0) }.joined(separator: ".")
    }
    return ip
  }

  private static func parseIPv4(_ ip: String) -> [UInt8]? {
    var addr = in_addr()
    guard ip.withCString({ inet_pton(AF_INET, $0, &addr) }) == 1 else { return nil }
    let raw = addr.s_addr.bigEndian
    return [
      UInt8((raw >> 24) & 0xFF),
      UInt8((raw >> 16) & 0xFF),
      UInt8((raw >> 8) & 0xFF),
      UInt8(raw & 0xFF),
    ]
  }

  private static func parseIPv6(_ ip: String) -> [UInt8]? {
    var addr = in6_addr()
    guard ip.withCString({ inet_pton(AF_INET6, $0, &addr) }) == 1 else { return nil }
    return withUnsafeBytes(of: &addr) { Array($0.bindMemory(to: UInt8.self)) }
  }

  private static func isForbiddenIPv4(_ b: [UInt8]) -> Bool {
    let a = b[0], c = b[1], d = b[2]
    if a == 0 { return true }                         // 0.0.0.0/8 "this network"
    if a == 10 { return true }                        // 10/8 private
    if a == 127 { return true }                       // 127/8 loopback
    if a == 100 && (c & 0xC0) == 0x40 { return true }  // 100.64/10 CGNAT
    if a == 169 && c == 254 { return true }           // 169.254/16 link-local + metadata
    if a == 172 && c >= 16 && c <= 31 { return true }  // 172.16/12 private
    if a == 192 && c == 168 { return true }           // 192.168/16 private
    if a == 192 && c == 0 && d == 0 { return true }    // 192.0.0/24
    if a == 192 && c == 0 && d == 2 { return true }    // 192.0.2/24 TEST-NET-1
    if a == 198 && (c == 18 || c == 19) { return true } // 198.18/15 benchmarking
    if a == 198 && c == 51 && d == 100 { return true } // 198.51.100/24 TEST-NET-2
    if a == 203 && c == 0 && d == 113 { return true }  // 203.0.113/24 TEST-NET-3
    if a >= 224 { return true }                       // 224/4 multicast + 240/4 reserved + broadcast
    return false
  }

  private static func isForbiddenIPv6(_ b: [UInt8]) -> Bool {
    // Unspecified ::
    if b.allSatisfy({ $0 == 0 }) { return true }
    // Loopback ::1
    if b[0...14].allSatisfy({ $0 == 0 }) && b[15] == 1 { return true }
    // Multicast ff00::/8
    if b[0] == 0xFF { return true }
    // Link-local fe80::/10
    if b[0] == 0xFE && (b[1] & 0xC0) == 0x80 { return true }
    // Unique local fc00::/7
    if (b[0] & 0xFE) == 0xFC { return true }
    // Discard-only 100::/64
    if b[0] == 0x01 && b[1] == 0x00 && b[2...7].allSatisfy({ $0 == 0 }) { return true }
    // IETF protocol assignments that should not be accepted as public endpoints.
    if b[0] == 0x20 && b[1] == 0x01 {
      if b[2] == 0x00 && b[3] == 0x00 { return true } // 2001::/32 Teredo
      if b[2] == 0x00 && (b[3] & 0xF0) == 0x10 { return true } // 2001:10::/28 ORCHID
      if b[2] == 0x00 && b[3] == 0x02 { return true } // 2001:2::/48 benchmarking
      if b[2] == 0x0D && b[3] == 0xB8 { return true } // 2001:db8::/32 docs
    }
    // 6to4 embeds an IPv4 route target and is deprecated.
    if b[0] == 0x20 && b[1] == 0x02 { return true }
    // NAT64 well-known prefix. Allow only when the embedded IPv4 is public.
    if isNat64WellKnown(b) { return isForbiddenIPv4([b[12], b[13], b[14], b[15]]) }
    // NAT64 local-use prefix can route through operator-specific private policy.
    if isNat64LocalUse(b) { return true }
    // Deprecated IPv4-compatible IPv6 addresses.
    if b[0...11].allSatisfy({ $0 == 0 }) { return true }
    // IPv4-mapped ::ffff:0:0/96 — validate the embedded IPv4
    if b[0...9].allSatisfy({ $0 == 0 }) && b[10] == 0xFF && b[11] == 0xFF {
      return isForbiddenIPv4([b[12], b[13], b[14], b[15]])
    }
    return false
  }

  private static func isNat64WellKnown(_ b: [UInt8]) -> Bool {
    return b[0] == 0x00 &&
      b[1] == 0x64 &&
      b[2] == 0xFF &&
      b[3] == 0x9B &&
      b[4...11].allSatisfy({ $0 == 0 })
  }

  private static func isNat64LocalUse(_ b: [UInt8]) -> Bool {
    return b[0] == 0x00 &&
      b[1] == 0x64 &&
      b[2] == 0xFF &&
      b[3] == 0x9B &&
      b[4] == 0x00 &&
      b[5] == 0x01
  }
}

final class SniConnectRequestLimiter: @unchecked Sendable {
  static let shared = SniConnectRequestLimiter()

  struct Snapshot {
    let activeRequests: Int
    let activeRequestsForPair: Int
    let pendingRequests: Int
    let pendingRequestsForPair: Int
    let activeRequestIdsForPair: [String]
    let pendingRequestIdsForPair: [String]
  }

  final class Token: @unchecked Sendable {
    private weak var limiter: SniConnectRequestLimiter?
    private let id: UUID
    private let lock = NSLock()
    private var released = false

    fileprivate init(limiter: SniConnectRequestLimiter, id: UUID) {
      self.limiter = limiter
      self.id = id
    }

    func release() {
      lock.lock()
      if released {
        lock.unlock()
        return
      }
      released = true
      lock.unlock()
      limiter?.release(id: id)
    }

    deinit {
      release()
    }
  }

  private final class CancellationState: @unchecked Sendable {
    private let lock = NSLock()
    private var cancelled = false

    var isCancelled: Bool {
      lock.lock()
      defer { lock.unlock() }
      return cancelled
    }

    func cancel() {
      lock.lock()
      cancelled = true
      lock.unlock()
    }
  }

  private struct PendingRequest {
    let id: UUID
    let key: String
    let requestId: String?
    let continuation: CheckedContinuation<Token, Error>
  }

  private struct ActiveRequest {
    let key: String
    let requestId: String?
  }

  private let maxActiveRequests: Int
  private let maxActiveRequestsPerPair: Int
  private let maxPendingRequests: Int
  private let queue = DispatchQueue(label: "com.onekey.sni.connect.request-limiter")
  private var activeRequests = 0
  private var activeRequestsByPair: [String: Int] = [:]
  private var activeRequestsByID: [UUID: ActiveRequest] = [:]
  private var pendingRequests: [PendingRequest] = []

  init(
    maxActiveRequests: Int = SniConnectValidation.maxActiveRequests,
    maxActiveRequestsPerPair: Int = SniConnectValidation.maxActiveRequestsPerPair,
    maxPendingRequests: Int = SniConnectValidation.maxPendingRequests
  ) {
    self.maxActiveRequests = maxActiveRequests
    self.maxActiveRequestsPerPair = maxActiveRequestsPerPair
    self.maxPendingRequests = maxPendingRequests
  }

  var pendingRequestCount: Int {
    queue.sync {
      pendingRequests.count
    }
  }

  func snapshot(hostname: String, ip: String) -> Snapshot {
    let key = pairKey(hostname: hostname, ip: ip)
    return queue.sync {
      let activeForPair = activeRequestsByID.values.filter { $0.key == key }
      let pendingForPair = pendingRequests.filter { $0.key == key }
      return Snapshot(
        activeRequests: activeRequests,
        activeRequestsForPair: activeForPair.count,
        pendingRequests: pendingRequests.count,
        pendingRequestsForPair: pendingForPair.count,
        activeRequestIdsForPair: activeForPair.compactMap { $0.requestId }.sorted(),
        pendingRequestIdsForPair: pendingForPair.compactMap { $0.requestId }.sorted()
      )
    }
  }

  func acquire(hostname: String, ip: String, requestId: String? = nil) async throws -> Token {
    try Task.checkCancellation()

    let id = UUID()
    let key = pairKey(hostname: hostname, ip: ip)
    let trackedRequestId = requestId.flatMap { $0.isEmpty ? nil : $0 }
    let cancellationState = CancellationState()

    let token = try await withTaskCancellationHandler {
      try Task.checkCancellation()
      return try await withCheckedThrowingContinuation { continuation in
        var immediateToken: Token?
        var immediateError: Error?

        queue.sync {
          if cancellationState.isCancelled {
            immediateError = CancellationError()
            return
          }

          if hasCapacity(for: key) {
            retainSlot(id: id, key: key, requestId: trackedRequestId)
            immediateToken = Token(limiter: self, id: id)
            return
          }

          if pendingRequests.count >= maxPendingRequests {
            SniConnectCoreDiagnostics.warn(SniConnectCoreDiagnostics.event("sni_resource_limit", [
              ("activeCount", activeRequests),
              ("pairCount", activeRequestsByPair[key] ?? 0),
              ("pendingCount", pendingRequests.count),
              ("limit", maxPendingRequests),
              ("reason", "max_pending_requests"),
              ("hostname", hostname.lowercased()),
              ("ipHash", SniConnectCoreDiagnostics.shortHash(ip)),
            ]))
            immediateError = SniConnectValidation.ValidationError.resourceLimit(
              "Too many pending SNI requests"
            )
            return
          }

          pendingRequests.append(PendingRequest(
            id: id,
            key: key,
            requestId: trackedRequestId,
            continuation: continuation
          ))
        }

        if let immediateToken {
          continuation.resume(returning: immediateToken)
        } else if let immediateError {
          continuation.resume(throwing: immediateError)
        }
      }
    } onCancel: {
      cancellationState.cancel()
      self.cancelPendingRequest(id: id)
    }

    // Cancellation can race with a pending-to-active handoff. Never return a
    // token to an already-cancelled caller without releasing its retained slot.
    do {
      try Task.checkCancellation()
      return token
    } catch {
      token.release()
      throw error
    }
  }

  private func release(id: UUID) {
    var nextRequest: PendingRequest?

    queue.sync {
      guard releaseSlot(id: id) else { return }

      guard activeRequests < maxActiveRequests,
            let index = pendingRequests.firstIndex(where: { hasCapacity(for: $0.key) }) else {
        return
      }

      nextRequest = pendingRequests.remove(at: index)
      if let nextRequest {
        retainSlot(
          id: nextRequest.id,
          key: nextRequest.key,
          requestId: nextRequest.requestId
        )
      }
    }

    if let nextRequest {
      nextRequest.continuation.resume(returning: Token(limiter: self, id: nextRequest.id))
    }
  }

  private func cancelPendingRequest(id: UUID) {
    var cancelledRequest: PendingRequest?

    queue.sync {
      guard let index = pendingRequests.firstIndex(where: { $0.id == id }) else {
        return
      }
      cancelledRequest = pendingRequests.remove(at: index)
    }

    cancelledRequest?.continuation.resume(throwing: CancellationError())
  }

  private func hasCapacity(for key: String) -> Bool {
    activeRequests < maxActiveRequests &&
      (activeRequestsByPair[key] ?? 0) < maxActiveRequestsPerPair
  }

  private func retainSlot(id: UUID, key: String, requestId: String?) {
    activeRequestsByID[id] = ActiveRequest(key: key, requestId: requestId)
    activeRequests += 1
    activeRequestsByPair[key, default: 0] += 1
  }

  private func releaseSlot(id: UUID) -> Bool {
    guard let activeRequest = activeRequestsByID.removeValue(forKey: id) else {
      return false
    }
    let key = activeRequest.key
    activeRequests = max(0, activeRequests - 1)
    guard let pairCount = activeRequestsByPair[key] else {
      return true
    }
    if pairCount <= 1 {
      activeRequestsByPair.removeValue(forKey: key)
    } else {
      activeRequestsByPair[key] = pairCount - 1
    }
    return true
  }

  private func pairKey(hostname: String, ip: String) -> String {
    return "\(hostname.lowercased())|\(SniConnectValidation.canonicalIPKey(ip))"
  }
}
