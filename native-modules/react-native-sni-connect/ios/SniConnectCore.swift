import Foundation

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
    return domain.caseInsensitiveCompare(config.hostname) == .orderedSame ? config.ip : nil
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
