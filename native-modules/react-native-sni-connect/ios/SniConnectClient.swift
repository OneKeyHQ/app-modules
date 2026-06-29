import Foundation
import UIKit
import EMASCurl

/// Core HTTPS client that enforces IP direct connection with SNI.
final class SniConnectClient {

  // Active requests tracking for cancellation support
  private var activeTasks: [String: Task<Response, Error>] = [:]
  private let tasksQueue = DispatchQueue(label: "com.onekey.sni.connect.tasks", attributes: .concurrent)

  // Token for the memory-warning observer (block-based observers are not removed
  // by `removeObserver(self)`, so the token must be retained and removed explicitly).
  private var memoryWarningObserver: NSObjectProtocol?

  init() {
    // Register for memory warnings to clean cache
    memoryWarningObserver = NotificationCenter.default.addObserver(
      forName: UIApplication.didReceiveMemoryWarningNotification,
      object: nil,
      queue: .main
    ) { _ in
      SniConnectLog.info("Memory warning received, cleaning DNS cache")
      DNSResolver.cleanExpiredEntries()
    }
  }

  deinit {
    if let observer = memoryWarningObserver {
      NotificationCenter.default.removeObserver(observer)
    }
  }

  struct RequestConfig {
    let requestId: String? // Optional request ID for cancellation
    let ip: String
    let hostname: String
    let method: String
    let path: String
    let headers: [String: String]
    let body: String?
    let timeout: TimeInterval

    // Advanced timeout configurations
    let connectTimeout: TimeInterval? // Connection establishment timeout
    let totalTimeout: TimeInterval? // Total request timeout (overrides `timeout`)

    var effectiveConnectTimeout: TimeInterval {
      connectTimeout ?? min(timeout / 3, 10_000.0)
    }

    var effectiveTotalTimeout: TimeInterval {
      totalTimeout ?? timeout
    }
  }

  struct Response {
    let data: Any
    let status: Int
    let statusText: String
    let headers: [String: String] // Single-value headers (backward compatible)
    let multiValueHeaders: [String: [String]] // Multi-value headers (e.g., Set-Cookie)
  }

  enum SniConnectError: Error {
    case invalidURL
    case invalidConfig(String)
    case dnsResolutionFailed(String)
    case tlsHandshakeFailed(String)
    case certificateValidationFailed(String)
    case connectionTimeout
    case connectionRefused
    case networkUnreachable
    case requestTimeout
    case httpError(code: Int, message: String)
    case cancelled
    case unknown(Error)

    var code: String {
      switch self {
      case .invalidURL: return "SNI_INVALID_URL"
      case .invalidConfig: return "SNI_INVALID_CONFIG"
      case .dnsResolutionFailed: return "SNI_DNS_FAILED"
      case .tlsHandshakeFailed: return "SNI_TLS_FAILED"
      case .certificateValidationFailed: return "SNI_CERT_FAILED"
      case .connectionTimeout: return "SNI_TIMEOUT"
      case .connectionRefused: return "SNI_CONNECTION_REFUSED"
      case .networkUnreachable: return "SNI_NETWORK_UNREACHABLE"
      case .requestTimeout: return "SNI_REQUEST_TIMEOUT"
      case .httpError: return "SNI_HTTP_ERROR"
      case .cancelled: return "SNI_CANCELLED"
      case .unknown: return "SNI_UNKNOWN_ERROR"
      }
    }

    var message: String {
      switch self {
      case .invalidURL:
        return "Invalid URL format"
      case .invalidConfig(let details):
        return "Invalid configuration: \(details)"
      case .dnsResolutionFailed(let domain):
        return "DNS resolution failed for domain: \(domain)"
      case .tlsHandshakeFailed(let details):
        return "TLS handshake failed: \(details)"
      case .certificateValidationFailed(let details):
        return "Certificate validation failed: \(details)"
      case .connectionTimeout:
        return "Connection timeout"
      case .connectionRefused:
        return "Connection refused by server"
      case .networkUnreachable:
        return "Network unreachable"
      case .requestTimeout:
        return "Request timeout"
      case .httpError(let code, let message):
        return "HTTP error \(code): \(message)"
      case .cancelled:
        return "Request cancelled"
      case .unknown(let error):
        return "Unknown error: \(error.localizedDescription)"
      }
    }

    /// Convert NSError to SniConnectError with detailed classification
    static func from(_ error: Error) -> SniConnectError {
      let nsError = error as NSError

      // Check for URL-related errors
      if nsError.domain == NSURLErrorDomain {
        switch nsError.code {
        case NSURLErrorTimedOut:
          return .requestTimeout
        case NSURLErrorCannotConnectToHost:
          return .connectionRefused
        case NSURLErrorNotConnectedToInternet, NSURLErrorNetworkConnectionLost:
          return .networkUnreachable
        case NSURLErrorSecureConnectionFailed:
          return .tlsHandshakeFailed(nsError.localizedDescription)
        case NSURLErrorServerCertificateUntrusted, NSURLErrorServerCertificateHasBadDate,
             NSURLErrorServerCertificateHasUnknownRoot, NSURLErrorServerCertificateNotYetValid:
          return .certificateValidationFailed(nsError.localizedDescription)
        case NSURLErrorCannotFindHost, NSURLErrorDNSLookupFailed:
          return .dnsResolutionFailed(nsError.localizedDescription)
        case NSURLErrorCancelled:
          return .cancelled
        default:
          return .unknown(error)
        }
      }

      return .unknown(error)
    }
  }

  private static let urlSession: URLSession = {
    let configuration = URLSessionConfiguration.default
    configuration.requestCachePolicy = .reloadIgnoringLocalCacheData
    configuration.urlCache = nil
    configuration.httpCookieStorage = nil
    configuration.httpShouldSetCookies = false
    configuration.shouldUseExtendedBackgroundIdleMode = false

    let curlConfig = EMASCurlConfiguration.default()
    curlConfig.httpVersion = .HTTP2
    curlConfig.connectTimeoutInterval = 2.5
    curlConfig.enableBuiltInGzip = true
    curlConfig.enableBuiltInRedirection = true
    curlConfig.cacheEnabled = false

    // Enable full certificate validation for security.
    // The certificate is validated against the SNI hostname, not the IP, because
    // the custom DNS resolver only overrides address resolution — libcurl keeps the
    // original hostname for SNI and certificate CN/SAN matching.
    curlConfig.certificateValidationEnabled = true
    curlConfig.domainNameVerificationEnabled = true
    curlConfig.dnsResolver = DNSResolver.self

    EMASCurlProtocol.install(into: configuration, with: curlConfig)
    return URLSession(configuration: configuration)
  }()

  @objc private final class DNSResolver: NSObject, EMASCurlProtocolDNSResolver {
    private static let queue = DispatchQueue(label: "com.onekey.sni.connect.dns", attributes: .concurrent)
    private static let cache = DNSCache()

    /// Thread-safe hostname -> IP pin with TTL.
    ///
    /// LIMITATION: EMASCurl only exposes a process-global DNS resolver
    /// (`setDNSResolver:`), which receives only the hostname. There is no
    /// per-request DNS API, so the pin is keyed by hostname and the most recent
    /// IP for a hostname wins. Concurrent requests to the SAME hostname targeting
    /// DIFFERENT IPs are therefore not guaranteed to each hit their own IP. Normal
    /// usage (one IP per hostname at a time) is unaffected.
    private final class DNSCache {
      private struct Entry {
        let ip: String
        let timestamp: TimeInterval
      }

      private var hostnameToEntry: [String: Entry] = [:]
      private let maxSize = 100
      private let ttl: TimeInterval = 300 // 5 minutes

      func get(_ domain: String) -> String? {
        let key = domain.lowercased()
        guard let entry = hostnameToEntry[key] else { return nil }
        if Date().timeIntervalSince1970 - entry.timestamp > ttl {
          return nil
        }
        return entry.ip
      }

      func set(_ ip: String, for domain: String) {
        let key = domain.lowercased()
        // Evict the oldest entry when at capacity (and this is a new host).
        if hostnameToEntry.count >= maxSize && hostnameToEntry[key] == nil {
          if let oldest = hostnameToEntry.min(by: { $0.value.timestamp < $1.value.timestamp })?.key {
            hostnameToEntry.removeValue(forKey: oldest)
          }
        }
        hostnameToEntry[key] = Entry(ip: ip, timestamp: Date().timeIntervalSince1970)
      }

      func clear() {
        hostnameToEntry.removeAll()
      }

      func cleanExpired() {
        let now = Date().timeIntervalSince1970
        let expired = hostnameToEntry.filter { now - $0.value.timestamp > ttl }.map { $0.key }
        for key in expired {
          hostnameToEntry.removeValue(forKey: key)
        }
      }
    }

    @objc static func resolveDomain(_ domain: String) -> String? {
      var result: String?
      queue.sync {
        result = cache.get(domain)
      }
      return result
    }

    static func setIP(_ ip: String, for host: String) {
      queue.sync(flags: .barrier) {
        cache.set(ip, for: host)
      }
    }

    /// Clear all DNS cache entries
    static func clearCache() {
      queue.sync(flags: .barrier) {
        cache.clear()
      }
    }

    /// Clean expired DNS cache entries
    static func cleanExpiredEntries() {
      queue.sync(flags: .barrier) {
        cache.cleanExpired()
      }
    }
  }

  /// Clear all DNS cache entries
  func clearDNSCache() {
    DNSResolver.clearCache()
    SniConnectLog.info("DNS cache cleared")
  }

  /// Cancel a request by ID
  func cancelRequest(requestId: String) {
    tasksQueue.async(flags: .barrier) { [weak self] in
      guard let task = self?.activeTasks[requestId] else {
        SniConnectLog.warn("No active request found with ID: \(requestId)")
        return
      }
      task.cancel()
      self?.activeTasks.removeValue(forKey: requestId)
      SniConnectLog.info("Request cancelled: \(requestId)")
    }
  }

  /// Cancel all active requests
  func cancelAllRequests() {
    tasksQueue.async(flags: .barrier) { [weak self] in
      guard let self = self else { return }
      let count = self.activeTasks.count
      for (_, task) in self.activeTasks {
        task.cancel()
      }
      self.activeTasks.removeAll()
      SniConnectLog.info("Cancelled \(count) active requests")
    }
  }

  /// Register an active task immediately after creation, before JS can cancel it.
  func registerTask(_ task: Task<Response, Error>, for requestId: String) {
    tasksQueue.sync(flags: .barrier) { [weak self] in
      self?.activeTasks[requestId] = task
    }
  }

  /// Unregister a completed or failed task
  private func unregisterTask(requestId: String?) {
    guard let requestId = requestId else { return }
    tasksQueue.async(flags: .barrier) { [weak self] in
      self?.activeTasks.removeValue(forKey: requestId)
    }
  }

  func performRequest(config: RequestConfig) async throws -> Response {
    // Check if task is cancelled
    try Task.checkCancellation()

    defer {
      unregisterTask(requestId: config.requestId)
    }

    // Validate every caller-controlled field before it reaches the network layer.
    let method: String
    let normalizedPath: String
    do {
      try SniConnectValidation.validatePublicIP(config.ip)
      try SniConnectValidation.validateHostname(config.hostname)
      try SniConnectValidation.validateHeaders(config.headers)
      method = try SniConnectValidation.normalizeMethod(config.method)
      normalizedPath = try SniConnectValidation.normalizePath(config.path)
    } catch {
      throw SniConnectError.invalidConfig("\(error)")
    }

    DNSResolver.setIP(config.ip, for: config.hostname)

    let url = try Self.buildURL(hostname: config.hostname, normalizedPath: normalizedPath)

    let mutableRequest = NSMutableURLRequest(url: url)
    mutableRequest.httpMethod = method

    // Convert milliseconds to seconds for timeout values
    let totalTimeoutSeconds = config.effectiveTotalTimeout / 1000.0
    let connectTimeoutSeconds = config.effectiveConnectTimeout / 1000.0

    // Set total request timeout
    mutableRequest.timeoutInterval = totalTimeoutSeconds
    mutableRequest.cachePolicy = .reloadIgnoringLocalCacheData

    // Explicitly set Host header for SNI
    mutableRequest.setValue(config.hostname, forHTTPHeaderField: "Host")

    for (key, value) in config.headers {
      if key.caseInsensitiveCompare("host") == .orderedSame {
        // Host header is already set above, skip duplicate
        continue
      }
      mutableRequest.setValue(value, forHTTPHeaderField: key)
    }

    if let bodyString = config.body, let bodyData = bodyString.data(using: .utf8) {
      mutableRequest.httpBody = bodyData
    }

    // Per-request connect timeout (avoids the process-global setter race).
    EMASCurlProtocol.setConnectTimeoutIntervalFor(mutableRequest, connectTimeoutInterval: connectTimeoutSeconds)
    let request = mutableRequest as URLRequest

    do {
      let (data, response) = try await Self.urlSession.data(for: request)
      guard let httpResponse = response as? HTTPURLResponse else {
        let errorMsg = "Invalid HTTP response type"
        SniConnectLog.error(errorMsg)
        throw SniConnectError.invalidConfig(errorMsg)
      }

      let status = httpResponse.statusCode
      let parsedData = Self.parseResponseData(data)
      let (headers, multiValueHeaders) = Self.extractHeaders(from: httpResponse)
      let statusText = HTTPURLResponse.localizedString(forStatusCode: status)

      // 4xx/5xx are returned to JS as a normal response (the caller inspects
      // `status`); we only record it for diagnostics.
      if status >= 400 {
        SniConnectLog.warn("HTTP \(status) for \(config.hostname)")
      }

      return Response(
        data: parsedData,
        status: status,
        statusText: statusText,
        headers: headers,
        multiValueHeaders: multiValueHeaders
      )
    } catch let error as SniConnectError {
      SniConnectLog.error("[\(error.code)] \(error.message)")
      throw error
    } catch {
      // Convert generic errors to specific SniConnectError types
      let sniError = SniConnectError.from(error)
      SniConnectLog.error("[\(sniError.code)] \(sniError.message)")
      throw sniError
    }
  }

  /// Build the request URL. Always `https://<hostname><path>` on port 443 — the
  /// path has already been validated as relative (no scheme/authority), so the
  /// caller cannot override scheme, host or port.
  private static func buildURL(hostname: String, normalizedPath: String) throws -> URL {
    var components = URLComponents()
    components.scheme = "https"
    components.host = hostname
    // `normalizedPath` is "/...optional?query". Split off the query so URLComponents
    // percent-encodes each part correctly.
    if let queryIndex = normalizedPath.firstIndex(of: "?") {
      components.percentEncodedPath = String(normalizedPath[..<queryIndex])
      let queryStart = normalizedPath.index(after: queryIndex)
      components.percentEncodedQuery = String(normalizedPath[queryStart...])
    } else {
      components.percentEncodedPath = normalizedPath
    }
    guard let url = components.url else {
      throw SniConnectError.invalidURL
    }
    return url
  }

  private static func parseResponseData(_ data: Data) -> Any {
    guard !data.isEmpty else {
      return ""
    }

    if let jsonObject = try? JSONSerialization.jsonObject(with: data, options: []) {
      return jsonObject
    }

    if let text = String(data: data, encoding: .utf8) {
      return text
    }

    return data.base64EncodedString()
  }

  /// Extract headers from HTTP response
  /// Returns both single-value headers (for backward compatibility) and multi-value headers
  private static func extractHeaders(from response: HTTPURLResponse) -> ([String: String], [String: [String]]) {
    var singleValueHeaders: [String: String] = [:]
    var multiValueHeaders: [String: [String]] = [:]

    // Group headers by normalized key (lowercase)
    var headerGroups: [String: [String]] = [:]

    for (key, value) in response.allHeaderFields {
      let headerKey = String(describing: key).lowercased()
      let headerValue = String(describing: value)

      if headerGroups[headerKey] == nil {
        headerGroups[headerKey] = []
      }
      headerGroups[headerKey]?.append(headerValue)
    }

    // Process grouped headers
    for (key, values) in headerGroups {
      // For backward compatibility, single-value headers use the last value
      singleValueHeaders[key] = values.last
      multiValueHeaders[key] = values
    }

    return (singleValueHeaders, multiValueHeaders)
  }
}
