import Foundation
import React

private enum SniConnectError: Error {
  case invalidConfig(String)
}

@objc(SniConnectImpl)
final class SniConnectImpl: NSObject {
  private let client: SniConnectClient
  private weak var eventSender: AnyObject?

  @objc
  init(eventSender: AnyObject) {
    self.eventSender = eventSender
    self.client = SniConnectClient()
    super.init()

    // Set up logging closure for the client
    self.client.onLog = { [weak self] level, message in
      self?.sendLogEvent(level: level, message: message)
    }
  }

  private func sendLogEvent(level: String, message: String) {
    // Send log event to the Objective-C bridge
    guard let eventSender = eventSender else {
      return
    }

    let logData: [String: Any] = [
      "level": level,
      "message": message,
      "timestamp": Int(Date().timeIntervalSince1970 * 1000)
    ]

    // Call the sendLogEvent method on the bridge
    let selector = NSSelectorFromString("sendLogEvent:")
    if eventSender.responds(to: selector) {
      eventSender.perform(selector, with: logData)
    }
  }

  @objc
  public func request(
    _ config: NSDictionary,
    resolve resolve: @escaping RCTPromiseResolveBlock,
    reject reject: @escaping RCTPromiseRejectBlock
  ) {
    do {
      let parsed = try Self.parseDictionary(config)
      handleRequest(config: parsed, resolve: resolve, reject: reject)
    } catch {
      NSLog("[SniConnect] ❌ Config parsing failed: \(error)")
      sendLogEvent(level: "error", message: "Config parsing failed: \(error)")
      reject("SNI_REQUEST_FAILED", error.localizedDescription, error)
    }
  }

  private func handleRequest(
    config: SniConnectClient.RequestConfig,
    resolve: @escaping RCTPromiseResolveBlock,
    reject: @escaping RCTPromiseRejectBlock
  ) {
    // Create task and register it synchronously before it starts executing
    let task = Task { () -> SniConnectClient.Response in
      return try await client.performRequest(config: config)
    }

    // Register task synchronously if requestId is provided
    if let requestId = config.requestId {
      client.registerTask(task, for: requestId)
    }

    // Handle the task result asynchronously
    Task {
      do {
        let result = try await task.value
        let responseBody = Self.serializeResponseData(result.data)

        resolve([
          "data": responseBody,
          "status": result.status,
          "statusText": result.statusText,
          "headers": result.headers,
          "multiValueHeaders": result.multiValueHeaders,
        ])
      } catch let error as SniConnectClient.SniConnectError {
        NSLog("[SniConnect] ❌ [\(error.code)] \(error.message)")
        sendLogEvent(level: "error", message: "[\(error.code)] \(error.message)")
        reject(error.code, error.message, error)
      } catch is CancellationError {
        NSLog("[SniConnect] ❌ Request cancelled")
        sendLogEvent(level: "info", message: "Request cancelled")
        reject("SNI_CANCELLED", "Request cancelled", nil)
      } catch {
        NSLog("[SniConnect] ❌ Request failed: \(error.localizedDescription)")
        sendLogEvent(level: "error", message: "Request failed: \(error.localizedDescription)")
        reject("SNI_UNKNOWN_ERROR", error.localizedDescription, error)
      }
    }
  }

  @objc
  public func cancelRequest(
    _ requestId: String,
    resolve resolve: @escaping RCTPromiseResolveBlock,
    reject reject: @escaping RCTPromiseRejectBlock
  ) {
    client.cancelRequest(requestId: requestId)
    resolve(["success": true])
  }

  @objc
  public func cancelAllRequests(
    _ resolve: @escaping RCTPromiseResolveBlock,
    reject reject: @escaping RCTPromiseRejectBlock
  ) {
    client.cancelAllRequests()
    resolve(["success": true])
  }

  @objc
  public func clearDNSCache(
    _ resolve: @escaping RCTPromiseResolveBlock,
    reject reject: @escaping RCTPromiseRejectBlock
  ) {
    client.clearDNSCache()
    resolve(["success": true])
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

  private static func serializeResponseData(_ data: Any) -> String {
    if let dict = data as? [String: Any],
       let jsonData = try? JSONSerialization.data(withJSONObject: dict, options: []),
       let jsonString = String(data: jsonData, encoding: .utf8) {
      return jsonString
    }

    if let array = data as? [[String: Any]],
       let jsonData = try? JSONSerialization.data(withJSONObject: array, options: []),
       let jsonString = String(data: jsonData, encoding: .utf8) {
      return jsonString
    }

    if let stringValue = data as? String {
      return stringValue
    }

    return String(describing: data)
  }
}

