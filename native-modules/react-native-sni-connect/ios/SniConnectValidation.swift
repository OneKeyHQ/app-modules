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
  }

  /// HTTP methods the module is allowed to issue.
  private static let allowedMethods: Set<String> = [
    "GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS",
  ]

  /// Validate and uppercase the HTTP method.
  static func normalizeMethod(_ method: String) throws -> String {
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
  }

  /// Validate `path`: must be a relative path/query only. Reject absolute URLs
  /// (scheme/authority), protocol-relative URLs and control characters so the
  /// caller cannot override scheme/host/port or downgrade to cleartext.
  static func normalizePath(_ path: String) throws -> String {
    let trimmed = path.trimmingCharacters(in: .whitespacesAndNewlines)
    if containsControlCharacters(trimmed) {
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
    for (key, value) in headers {
      if key.isEmpty || containsControlCharacters(key) || containsControlCharacters(value) {
        throw ValidationError.invalidHeader(key)
      }
    }
  }

  static func containsControlCharacters(_ s: String) -> Bool {
    return s.unicodeScalars.contains { $0.value < 0x20 || $0.value == 0x7F }
  }

  // MARK: - IP validation

  /// Validate `ip` is a literal IPv4/IPv6 address (never a hostname) and routes
  /// to a public/global-unicast destination. Rejects loopback, private,
  /// link-local (incl. 169.254.169.254 metadata), CGNAT, multicast and reserved.
  static func validatePublicIP(_ ip: String) throws {
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
    // IPv4-mapped ::ffff:0:0/96 — validate the embedded IPv4
    if b[0...9].allSatisfy({ $0 == 0 }) && b[10] == 0xFF && b[11] == 0xFF {
      return isForbiddenIPv4([b[12], b[13], b[14], b[15]])
    }
    return false
  }
}
