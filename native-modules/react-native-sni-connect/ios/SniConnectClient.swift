import Foundation
import EMASCurl

/// Core HTTPS client that enforces IP direct connection with SNI.
final class SniConnectClient {

  // Logging callback
  var onLog: ((String, String) -> Void)?

  // Active requests tracking for cancellation support
  private var activeTasks: [String: Task<Response, Error>] = [:]
  private let tasksQueue = DispatchQueue(label: "com.onekey.sni.connect.tasks", attributes: .concurrent)

  init() {
    // Register for memory warnings to clean cache
    NotificationCenter.default.addObserver(
      forName: UIApplication.didReceiveMemoryWarningNotification,
      object: nil,
      queue: .main
    ) { [weak self] _ in
      self?.onLog?("warning", "Memory warning received, cleaning DNS cache")
      DNSResolver.cleanExpiredEntries()
    }
  }

  deinit {
    NotificationCenter.default.removeObserver(self)
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
      connectTimeout ?? min(timeout / 3, 10.0)
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

    // Enable full certificate validation for security
    // The certificate will be validated against the SNI hostname, not the IP
    curlConfig.certificateValidationEnabled = true
    curlConfig.domainNameVerificationEnabled = true
    curlConfig.dnsResolver = DNSResolver.self

    EMASCurlProtocol.install(into: configuration, with: curlConfig)
    return URLSession(configuration: configuration)
  }()

  @objc private final class DNSResolver: NSObject, EMASCurlProtocolDNSResolver {
    private static let queue = DispatchQueue(label: "com.onekey.sni.connect.dns", attributes: .concurrent)
    private static let cache = DNSCache()

    /// LRU cache for DNS mappings with size limit and TTL support
    /// Uses hostname:IP as cache key to ensure different IPs for the same hostname are isolated
    private class DNSCache {
      private struct CacheEntry {
        let hostname: String
        let ip: String
        let timestamp: TimeInterval
      }

      private var storage: [String: CacheEntry] = [:]
      private var accessOrder: [String] = []

      // Mapping from hostname to IP for DNS resolution
      // This stores the most recently set IP for each hostname
      private var hostnameToIP: [String: String] = [:]

      // Maximum cache entries (100 as specified in requirements)
      private let maxSize = 100
      // TTL in seconds (5 minutes default)
      private let ttl: TimeInterval = 300

      /// Generate cache key using hostname:IP format
      /// This ensures different IPs for the same hostname are isolated (accurate speed testing)
      private func makeCacheKey(hostname: String, ip: String) -> String {
        return "\(hostname.lowercased()):\(ip)"
      }

      func get(_ domain: String) -> String? {
        let normalizedDomain = domain.lowercased()

        // Return the most recently set IP for this hostname
        return hostnameToIP[normalizedDomain]
      }

      func set(_ ip: String, for domain: String) {
        let normalizedDomain = domain.lowercased()
        let key = makeCacheKey(hostname: normalizedDomain, ip: ip)

        // Update hostname to IP mapping
        hostnameToIP[normalizedDomain] = ip

        // Evict oldest entry if cache is full
        if storage.count >= maxSize && storage[key] == nil {
          if let oldest = accessOrder.first {
            storage.removeValue(forKey: oldest)
            accessOrder.removeFirst()
          }
        }

        // Add or update entry
        let entry = CacheEntry(hostname: normalizedDomain, ip: ip, timestamp: Date().timeIntervalSince1970)
        storage[key] = entry

        // Update access order
        if let index = accessOrder.firstIndex(of: key) {
          accessOrder.remove(at: index)
        }
        accessOrder.append(key)
      }

      func remove(_ key: String) {
        storage.removeValue(forKey: key)
        if let index = accessOrder.firstIndex(of: key) {
          accessOrder.remove(at: index)
        }
      }

      func clear() {
        storage.removeAll()
        accessOrder.removeAll()
        hostnameToIP.removeAll()
      }

      func cleanExpired() {
        let now = Date().timeIntervalSince1970
        let expiredKeys = storage.filter { now - $0.value.timestamp > ttl }.map { $0.key }
        for key in expiredKeys {
          remove(key)
          // Also remove from hostnameToIP if this was the active mapping
          if let entry = storage[key], hostnameToIP[entry.hostname] == entry.ip {
            hostnameToIP.removeValue(forKey: entry.hostname)
          }
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
    onLog?("info", "DNS cache cleared")
  }

  /// Cancel a request by ID
  func cancelRequest(requestId: String) {
    tasksQueue.async(flags: .barrier) { [weak self] in
      guard let task = self?.activeTasks[requestId] else {
        self?.onLog?("warning", "No active request found with ID: \(requestId)")
        return
      }
      task.cancel()
      self?.activeTasks.removeValue(forKey: requestId)
      self?.onLog?("info", "Request cancelled: \(requestId)")
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
      self.onLog?("info", "Cancelled \(count) active requests")
    }
  }

  /// Register an active task (synchronous for immediate registration)
  func registerTask(_ task: Task<Response, Error>, for requestId: String) {
    tasksQueue.sync(flags: .barrier) { [weak self] in
      self?.activeTasks[requestId] = task
      self?.onLog?("info", "Registered request: \(requestId), total active: \(self?.activeTasks.count ?? 0)")
    }
  }

  /// Unregister a completed or failed task
  private func unregisterTask(requestId: String?) {
    guard let requestId = requestId else { return }
    tasksQueue.async(flags: .barrier) { [weak self] in
      self?.activeTasks.removeValue(forKey: requestId)
      self?.onLog?("info", "Unregistered request: \(requestId)")
    }
  }

  func performRequest(config: RequestConfig) async throws -> Response {
    // Check if task is cancelled
    try Task.checkCancellation()

    defer {
      unregisterTask(requestId: config.requestId)
    }

    DNSResolver.setIP(config.ip, for: config.hostname)

    let url = try Self.buildURL(hostname: config.hostname, path: config.path)

    let mutableRequest = NSMutableURLRequest(url: url)
    mutableRequest.httpMethod = config.method.uppercased()

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

    // Configure connection timeout for EMASCurl
    EMASCurlProtocol.setConnectTimeoutInterval(connectTimeoutSeconds)
    let request = mutableRequest as URLRequest

    do {
      let (data, response) = try await Self.urlSession.data(for: request)
      guard let httpResponse = response as? HTTPURLResponse else {
        let errorMsg = "Invalid HTTP response type"
        onLog?("error", errorMsg)
        throw SniConnectError.invalidConfig(errorMsg)
      }

      let status = httpResponse.statusCode
      let parsedData = Self.parseResponseData(data)
      let (headers, multiValueHeaders) = Self.extractHeaders(from: httpResponse)
      let statusText = HTTPURLResponse.localizedString(forStatusCode: status)

      // Check for HTTP errors (4xx, 5xx)
      if status >= 400 {
        let error = SniConnectError.httpError(code: status, message: statusText)
        onLog?("error", "HTTP error: \(error.message)")
        // Still return the response for client to handle
      }

      return Response(
        data: parsedData,
        status: status,
        statusText: statusText,
        headers: headers,
        multiValueHeaders: multiValueHeaders
      )
    } catch let error as SniConnectError {
      onLog?("error", "[\(error.code)] \(error.message)")
      throw error
    } catch {
      // Convert generic errors to specific SniConnectError types
      let sniError = SniConnectError.from(error)
      onLog?("error", "[\(sniError.code)] \(sniError.message)")
      throw sniError
    }
  }

  private static func buildURL(hostname: String, path: String) throws -> URL {
    let trimmedPath = path.trimmingCharacters(in: .whitespacesAndNewlines)

    if let url = URL(string: trimmedPath), url.scheme != nil {
      return url
    }

    let normalizedPath: String
    if trimmedPath.isEmpty {
      normalizedPath = "/"
    } else if trimmedPath.hasPrefix("/") {
      normalizedPath = trimmedPath
    } else {
      normalizedPath = "/" + trimmedPath
    }

    guard let url = URL(string: "https://\(hostname)\(normalizedPath)") else {
      throw SniConnectError.invalidURL
    }
    return url
  }

  private static func parseResponseData(_ data: Data) -> Any {
    guard !data.isEmpty else {
      return [:]
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

      // Multi-value headers contain all values
      if values.count > 1 {
        multiValueHeaders[key] = values
      } else {
        multiValueHeaders[key] = values
      }
    }

    return (singleValueHeaders, multiValueHeaders)
  }
}
