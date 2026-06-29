import Foundation
import CFNetwork
import React

private enum SniConnectError: Error {
  case invalidConfig(String)
}

@objc(SniConnectImpl)
final class SniConnectImpl: NSObject {
  private let client: SniConnectClient

  @objc
  override init() {
    self.client = SniConnectClient()
    super.init()
  }

  @objc
  public func request(
    _ config: NSDictionary,
    resolve: @escaping RCTPromiseResolveBlock,
    reject: @escaping RCTPromiseRejectBlock
  ) {
    do {
      let parsed = try Self.parseDictionary(config)
      handleRequest(config: parsed, resolve: resolve, reject: reject)
    } catch {
      SniConnectLog.error("Config parsing failed: \(error)")
      reject("SNI_INVALID_CONFIG", "\(error)", error)
    }
  }

  private func handleRequest(
    config: SniConnectClient.RequestConfig,
    resolve: @escaping RCTPromiseResolveBlock,
    reject: @escaping RCTPromiseRejectBlock
  ) {
    do {
      try Self.validate(config)
    } catch {
      SniConnectLog.error("Config validation failed: \(error)")
      reject("SNI_INVALID_CONFIG", "\(error)", error)
      return
    }

    // Create the task and register it synchronously before JS can cancel it.
    // Unregistering is owned by the result waiter below, so even a task that
    // completes immediately cannot unregister before it has been registered.
    let task = Task { () -> SniConnectClient.Response in
      return try await client.performRequest(config: config)
    }

    let registrationToken = client.registerTask(task, for: config.requestId)

    // Handle the task result asynchronously
    Task {
      defer {
        client.unregisterTask(requestId: config.requestId, token: registrationToken)
      }

      do {
        let result = try await task.value
        resolve([
          "data": result.data,
          "status": result.status,
          "statusText": result.statusText,
          "headers": result.headers,
          "multiValueHeaders": result.multiValueHeaders,
        ])
      } catch let error as SniConnectClient.SniConnectError {
        reject(error.code, error.message, error)
      } catch is CancellationError {
        reject("SNI_CANCELLED", "Request cancelled", nil)
      } catch {
        SniConnectLog.error("Request failed: \(error.localizedDescription)")
        reject("SNI_UNKNOWN_ERROR", error.localizedDescription, error)
      }
    }
  }

  @objc
  public func cancelRequest(
    _ requestId: String,
    resolve: @escaping RCTPromiseResolveBlock,
    reject: @escaping RCTPromiseRejectBlock
  ) {
    let success = client.cancelRequest(requestId: requestId)
    resolve(["success": success])
  }

  @objc
  public func cancelAllRequests(
    _ resolve: @escaping RCTPromiseResolveBlock,
    reject: @escaping RCTPromiseRejectBlock
  ) {
    client.cancelAllRequests()
    resolve(["success": true])
  }

  @objc
  public func clearDNSCache(
    _ resolve: @escaping RCTPromiseResolveBlock,
    reject: @escaping RCTPromiseRejectBlock
  ) {
    client.clearDNSCache()
    resolve(["success": true])
  }

  @objc
  public func isProxyActiveForUrl(
    _ url: String,
    resolve: @escaping RCTPromiseResolveBlock,
    reject: @escaping RCTPromiseRejectBlock
  ) {
    do {
      resolve(try Self.isProxyActive(forUrl: url))
    } catch {
      reject("SNI_INVALID_URL", "\(error)", error)
    }
  }

  private static func parseDictionary(_ dictionary: NSDictionary) throws -> SniConnectClient.RequestConfig {
    guard let ip = dictionary["ip"] as? String, !ip.isEmpty else {
      throw SniConnectError.invalidConfig("Missing ip")
    }
    guard let hostname = dictionary["hostname"] as? String, !hostname.isEmpty else {
      throw SniConnectError.invalidConfig("Missing hostname")
    }

    let requestId = dictionary["requestId"] as? String
    let method = (dictionary["method"] as? String)?.uppercased() ?? "GET"
    let path = dictionary["path"] as? String ?? "/"
    let headers = dictionary["headers"] as? [String: String] ?? [:]
    let body = dictionary["body"] as? String
    let timeout = dictionary["timeout"] as? NSNumber ?? 30_000

    // Parse advanced timeout configurations
    let connectTimeout = (dictionary["connectTimeout"] as? NSNumber)?.doubleValue
    let totalTimeout = (dictionary["totalTimeout"] as? NSNumber)?.doubleValue

    return SniConnectClient.RequestConfig(
      requestId: requestId,
      ip: ip,
      hostname: hostname,
      method: method,
      path: path,
      headers: headers,
      body: body,
      timeout: timeout.doubleValue,
      connectTimeout: connectTimeout,
      totalTimeout: totalTimeout
    )
  }

  private static func validate(_ config: SniConnectClient.RequestConfig) throws {
    try SniConnectValidation.validatePublicIP(config.ip)
    try SniConnectValidation.validateHostname(config.hostname)
    _ = try SniConnectValidation.normalizeHeaders(config.headers)
    _ = try SniConnectValidation.normalizeMethod(config.method)
    _ = try SniConnectValidation.normalizePath(config.path)
    try SniConnectValidation.validateRequestId(config.requestId)
    try SniConnectValidation.validateTimeout(config.effectiveTotalTimeout)
    try SniConnectValidation.validateBody(config.body)
  }

  private static func isProxyActive(forUrl urlString: String) throws -> Bool {
    guard let url = URL(string: urlString),
          let scheme = url.scheme?.lowercased(),
          ["http", "https"].contains(scheme),
          url.host != nil else {
      throw SniConnectError.invalidConfig("Invalid URL")
    }

    guard let settings = CFNetworkCopySystemProxySettings()?.takeRetainedValue() else {
      return false
    }

    let proxies = CFNetworkCopyProxiesForURL(url as CFURL, settings).takeRetainedValue() as NSArray
    for proxy in proxies {
      guard let proxyDictionary = proxy as? NSDictionary,
            let type = proxyDictionary[kCFProxyTypeKey] as? String else {
        continue
      }
      if type != (kCFProxyTypeNone as String) {
        return true
      }
    }
    return false
  }
}
