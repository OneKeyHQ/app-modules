import Foundation

enum SniConnectCoreDiagnostics {
  static var warnSink: ((String) -> Void)?

  static func warn(_ message: String) {
    warnSink?(message)
  }

  static func event(_ name: String, _ fields: [(String, Any?)]) -> String {
    let normalizedFields: [(String, Any?)] = [("event", name)] + fields
    return normalizedFields
      .map { key, value in "\(key)=\(sanitize(value))" }
      .joined(separator: " ")
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

struct SniConnectResolverConfig: Equatable {
  let hostname: String
  let ip: String
}

enum SniConnectCoreError: Error {
  case resourceLimit(String)
}

final class SniConnectPinnedResolverRegistry {
  static let defaultMaxEntries = 32

  private let maxEntries: Int
  private var classesByKey: [String: AnyClass] = [:]
  private var configsByClassName: [String: SniConnectResolverConfig] = [:]
  private var reusableClasses: [AnyClass] = []
  private var allocatedClassNames: Set<String> = []

  init(maxEntries: Int = SniConnectPinnedResolverRegistry.defaultMaxEntries) {
    self.maxEntries = maxEntries
  }

  var entryCount: Int {
    classesByKey.count
  }

  var allocatedClassCount: Int {
    allocatedClassNames.count
  }

  func resolverClass(
    hostname: String,
    ip: String,
    allocateClass: () -> AnyClass
  ) throws -> AnyClass {
    let normalizedHost = hostname.lowercased()
    let key = Self.key(hostname: normalizedHost, ip: ip)
    if let resolverClass = classesByKey[key] {
      return resolverClass
    }

    guard classesByKey.count < maxEntries else {
      throw SniConnectCoreError.resourceLimit("Too many cached SNI resolver entries")
    }

    let resolverClass: AnyClass = reusableClasses.popLast() ?? allocateClass()
    let className = NSStringFromClass(resolverClass)
    allocatedClassNames.insert(className)
    classesByKey[key] = resolverClass
    configsByClassName[className] = SniConnectResolverConfig(hostname: normalizedHost, ip: ip)
    return resolverClass
  }

  func resolve(domain: String, resolverClass: AnyClass) -> String? {
    guard let config = configsByClassName[NSStringFromClass(resolverClass)] else {
      return nil
    }
    guard domain.caseInsensitiveCompare(config.hostname) == .orderedSame else {
      SniConnectCoreDiagnostics.warn(SniConnectCoreDiagnostics.event("sni_pinned_dns_unexpected_host", [
        ("expectedHost", config.hostname),
        ("requestedHostHash", SniConnectCoreDiagnostics.shortHash(domain.lowercased())),
        ("result", "fail_closed"),
      ]))
      return nil
    }
    return config.ip
  }

  func remove(hostname: String, ip: String) {
    let key = Self.key(hostname: hostname.lowercased(), ip: ip)
    guard let resolverClass = classesByKey.removeValue(forKey: key) else {
      return
    }
    configsByClassName.removeValue(forKey: NSStringFromClass(resolverClass))
    reusableClasses.append(resolverClass)
  }

  func clear() {
    reusableClasses.append(contentsOf: classesByKey.values)
    classesByKey.removeAll()
    configsByClassName.removeAll()
  }

  private static func key(hostname: String, ip: String) -> String {
    return "\(hostname)|\(ip)"
  }
}

enum SniConnectResponseText {
  static func decode(_ data: Data) -> String {
    guard !data.isEmpty else {
      return ""
    }
    if let text = String(data: data, encoding: .utf8) {
      return text
    }
    return String(decoding: data, as: UTF8.self)
  }
}

struct SniConnectHeaderMaps: Equatable {
  let singleValueHeaders: [String: String]
  let multiValueHeaders: [String: [String]]
}

enum SniConnectResponseHeaders {
  static func make(rawHeaderFields: [(name: String, value: String)]) -> SniConnectHeaderMaps {
    var singleValueHeaders: [String: String] = [:]
    var multiValueHeaders: [String: [String]] = [:]

    for header in rawHeaderFields {
      let name = header.name.lowercased()
      guard !name.isEmpty else { continue }

      singleValueHeaders[name] = header.value
      multiValueHeaders[name, default: []].append(header.value)
    }

    return SniConnectHeaderMaps(
      singleValueHeaders: singleValueHeaders,
      multiValueHeaders: multiValueHeaders
    )
  }

  static func make(from headerFields: [AnyHashable: Any]) -> SniConnectHeaderMaps {
    let rawHeaderFields = headerFields.flatMap { key, value -> [(name: String, value: String)] in
      let name = String(describing: key)
      return values(from: value).map { (name: name, value: $0) }
    }
    return make(rawHeaderFields: rawHeaderFields)
  }

  private static func values(from value: Any) -> [String] {
    if let values = value as? [String] {
      return values
    }
    if let values = value as? NSArray {
      return values.map { String(describing: $0) }
    }
    return [String(describing: value)]
  }
}
