import Foundation
import ObjectiveC
import UIKit
import EMASCurl

@objc(SniConnectPinnedDNSResolverBase)
private class SniConnectPinnedDNSResolverBase: NSObject, EMASCurlProtocolDNSResolver {
  @objc class func resolveDomain(_ domain: String) -> String? {
    PinnedDNSResolverFactory.resolve(domain: domain, resolverClass: self)
  }
}

private enum PinnedDNSResolverFactory {
  private static let queue = DispatchQueue(label: "com.onekey.sni.connect.pinned-dns-resolvers")
  private static var nextClassID = 0
  private static let registry = SniConnectPinnedResolverRegistry()

  static func resolverClass(hostname: String, ip: String) throws -> EMASCurlProtocolDNSResolver.Type {
    return try queue.sync {
      let resolverClass = try registry.resolverClass(
        hostname: hostname,
        ip: ip,
        allocateClass: allocateResolverClass
      )
      return resolverClass as! EMASCurlProtocolDNSResolver.Type
    }
  }

  static func resolve(domain: String, resolverClass: AnyClass) -> String? {
    return queue.sync {
      registry.resolve(domain: domain, resolverClass: resolverClass)
    }
  }

  static func remove(hostname: String, ip: String) {
    queue.sync {
      registry.release(hostname: hostname, ip: ip)
    }
  }

  private static func allocateResolverClass() -> AnyClass {
    while true {
      nextClassID += 1
      let className = "SniConnectPinnedDNSResolver_\(nextClassID)"
      if let resolverClass = objc_allocateClassPair(
        SniConnectPinnedDNSResolverBase.self,
        className,
        0
      ) {
        objc_registerClassPair(resolverClass)
        return resolverClass
      }
    }
  }
}

private final class SniConnectSessionInvalidationDelegate: NSObject, URLSessionDelegate {
  private let hostname: String
  private let ip: String

  init(hostname: String, ip: String) {
    self.hostname = hostname
    self.ip = ip
  }

  func urlSession(_ session: URLSession, didBecomeInvalidWithError error: Error?) {
    PinnedDNSResolverFactory.remove(hostname: hostname, ip: ip)
    SniConnectLog.info(SniConnectLog.event("sni_session_invalidated", [
      ("hostname", hostname),
      ("ipHash", SniConnectLog.shortHash(ip)),
      ("success", error == nil),
    ]))
  }
}

/// Core HTTPS client that enforces IP direct connection with SNI.
final class SniConnectClient {

  // Active requests tracking for cancellation support. Every task is tracked by
  // token so cancelAllRequests() also covers requests that do not have a requestId.
  private var activeTasksByToken: [UUID: Task<Response, Error>] = [:]
  private var requestTokensById: [String: UUID] = [:]
  private let tasksQueue = DispatchQueue(label: "com.onekey.sni.connect.tasks", attributes: .concurrent)
  private let requestLimiter = SniConnectRequestLimiter()

  private struct SessionKey: Hashable {
    let hostname: String
    let ip: String
  }

  private static let maxCachedSessions = SniConnectPinnedResolverRegistry.defaultMaxEntries
  private var sessionCache: [SessionKey: URLSession] = [:]
  private var sessionAccessOrder: [SessionKey] = []
  private let sessionsQueue = DispatchQueue(label: "com.onekey.sni.connect.sessions")

  // Token for the memory-warning observer (block-based observers are not removed
  // by `removeObserver(self)`, so the token must be retained and removed explicitly).
  private var memoryWarningObserver: NSObjectProtocol?

  init() {
    SniConnectCoreDiagnostics.warnSink = { message in
      SniConnectLog.warn(message)
    }

    // Register for memory warnings to clean cache
    memoryWarningObserver = NotificationCenter.default.addObserver(
      forName: UIApplication.didReceiveMemoryWarningNotification,
      object: nil,
      queue: .main
    ) { [weak self] _ in
      SniConnectLog.info(SniConnectLog.event("sni_lifecycle", [
        ("action", "memory_warning"),
        ("cacheAction", "clear_dns_cache"),
      ]))
      self?.clearDNSCache()
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
    let data: String
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
    case resourceLimit(String)
    case httpError(code: Int, message: String)
    case responseProcessingFailed(String)
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
      case .resourceLimit: return "SNI_RESOURCE_LIMIT"
      case .httpError: return "SNI_HTTP_ERROR"
      case .responseProcessingFailed: return "SNI_RESPONSE_FAILED"
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
      case .resourceLimit(let details):
        return "Resource limit exceeded: \(details)"
      case .httpError(let code, let message):
        return "HTTP error \(code): \(message)"
      case .responseProcessingFailed(let details):
        return "Response processing failed: \(details)"
      case .cancelled:
        return "Request cancelled"
      case .unknown(let error):
        return "Unknown error: \(error.localizedDescription)"
      }
    }

    /// Convert NSError to SniConnectError with detailed classification
    static func from(_ error: Error) -> SniConnectError {
      if let timeout = error as? SniConnectTimeout, timeout == .deadlineExceeded {
        return .requestTimeout
      }

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

  private static func makeURLSession(for key: SessionKey) throws -> URLSession {
    let configuration = URLSessionConfiguration.default
    configuration.requestCachePolicy = .reloadIgnoringLocalCacheData
    configuration.urlCache = nil
    configuration.httpCookieStorage = nil
    configuration.httpShouldSetCookies = false
    configuration.connectionProxyDictionary = [:]
    configuration.shouldUseExtendedBackgroundIdleMode = false

    let curlConfig = EMASCurlConfiguration.default()
    curlConfig.httpVersion = .HTTP1
    curlConfig.connectTimeoutInterval = 2.5
    curlConfig.enableBuiltInGzip = false
    curlConfig.enableBuiltInRedirection = false
    curlConfig.cacheEnabled = false

    // Enable full certificate validation for security.
    // The certificate is validated against the SNI hostname, not the IP, because
    // the custom DNS resolver only overrides address resolution — libcurl keeps the
    // original hostname for SNI and certificate CN/SAN matching.
    curlConfig.certificateValidationEnabled = true
    curlConfig.domainNameVerificationEnabled = true
    curlConfig.dnsResolver = try PinnedDNSResolverFactory.resolverClass(
      hostname: key.hostname,
      ip: key.ip
    )

    EMASCurlProtocol.install(into: configuration, with: curlConfig)
    SniConnectLog.info(SniConnectLog.event("sni_transport_config", [
      ("hostname", key.hostname),
      ("ipHash", SniConnectLog.shortHash(key.ip)),
      ("ipFamily", SniConnectLog.ipFamily(key.ip)),
      ("proxyMode", "no_proxy"),
      ("pinnedResolver", true),
      ("protocol", "http1"),
      ("followRedirects", false),
      ("cacheEnabled", false),
    ]))
    let delegate = SniConnectSessionInvalidationDelegate(hostname: key.hostname, ip: key.ip)
    return URLSession(configuration: configuration, delegate: delegate, delegateQueue: nil)
  }

  private func session(for config: RequestConfig) throws -> URLSession {
    let key = SessionKey(hostname: config.hostname.lowercased(), ip: config.ip)
    return try sessionsQueue.sync {
      if let session = sessionCache[key] {
        markSessionUsed(key)
        return session
      }

      evictSessionsIfNeeded(forPendingInsert: true)
      let session = try Self.makeURLSession(for: key)
      sessionCache[key] = session
      markSessionUsed(key)
      return session
    }
  }

  private func markSessionUsed(_ key: SessionKey) {
    sessionAccessOrder.removeAll { $0 == key }
    sessionAccessOrder.append(key)
  }

  private func evictSessionsIfNeeded(forPendingInsert: Bool = false) {
    let limit = forPendingInsert ? Self.maxCachedSessions - 1 : Self.maxCachedSessions
    while sessionCache.count > limit, let evictedKey = sessionAccessOrder.first {
      sessionAccessOrder.removeFirst()
      sessionCache.removeValue(forKey: evictedKey)?.finishTasksAndInvalidate()
      SniConnectLog.info(SniConnectLog.event("sni_cache_evict", [
        ("hostname", evictedKey.hostname),
        ("ipHash", SniConnectLog.shortHash(evictedKey.ip)),
        ("cacheSize", sessionCache.count),
        ("limit", Self.maxCachedSessions),
        ("reason", forPendingInsert ? "max_cached_sessions_pending_insert" : "max_cached_sessions"),
      ]))
    }
  }

  /// Drop cached sessions so future requests cannot reuse keep-alive connections
  /// from a previously pinned destination. Resolver classes are released only
  /// after each old URLSession is fully invalidated.
  func clearDNSCache() {
    let sessions = sessionsQueue.sync { () -> [URLSession] in
      let sessions = Array(sessionCache.values)
      sessionCache.removeAll()
      sessionAccessOrder.removeAll()
      return sessions
    }
    for session in sessions {
      session.finishTasksAndInvalidate()
    }
    SniConnectLog.info(SniConnectLog.event("sni_lifecycle", [
      ("action", "clear_dns_cache"),
      ("sessionCount", sessions.count),
      ("success", true),
    ]))
  }

  /// Cancel a request by ID
  func cancelRequest(requestId: String) -> Bool {
    return tasksQueue.sync(flags: .barrier) { [weak self] in
      guard let self = self, let token = self.requestTokensById.removeValue(forKey: requestId),
            let task = self.activeTasksByToken.removeValue(forKey: token) else {
        SniConnectLog.warn(SniConnectLog.event("sni_cancel", [
          ("requestIdHash", SniConnectLog.shortHash(requestId)),
          ("success", false),
        ]))
        return false
      }
      task.cancel()
      SniConnectLog.info(SniConnectLog.event("sni_cancel", [
        ("requestIdHash", SniConnectLog.shortHash(requestId)),
        ("success", true),
      ]))
      return true
    }
  }

  /// Cancel all active requests
  func cancelAllRequests() {
    tasksQueue.sync(flags: .barrier) { [weak self] in
      guard let self = self else { return }
      let tasks = Array(self.activeTasksByToken.values)
      for task in tasks {
        task.cancel()
      }
      self.activeTasksByToken.removeAll()
      self.requestTokensById.removeAll()
      SniConnectLog.info(SniConnectLog.event("sni_cancel_all", [
        ("cancelledCount", tasks.count),
        ("success", true),
      ]))
    }
  }

  /// Register an active task immediately after creation, before JS can cancel it.
  func registerTask(_ task: Task<Response, Error>, for requestId: String?) -> UUID {
    let token = UUID()
    tasksQueue.sync(flags: .barrier) { [weak self] in
      guard let self = self else { return }
      if let requestId = requestId, let previousToken = self.requestTokensById[requestId],
         let previousTask = self.activeTasksByToken.removeValue(forKey: previousToken) {
        previousTask.cancel()
        SniConnectLog.warn(SniConnectLog.event("sni_duplicate_request_id", [
          ("requestIdHash", SniConnectLog.shortHash(requestId)),
          ("action", "cancel_previous"),
        ]))
      }
      self.activeTasksByToken[token] = task
      if let requestId = requestId {
        self.requestTokensById[requestId] = token
      }
    }
    return token
  }

  /// Unregister a completed or failed task
  func unregisterTask(requestId: String?, token: UUID) {
    tasksQueue.async(flags: .barrier) { [weak self] in
      self?.activeTasksByToken.removeValue(forKey: token)
      if let requestId = requestId, self?.requestTokensById[requestId] == token {
        self?.requestTokensById.removeValue(forKey: requestId)
      }
    }
  }

  func performRequest(config: RequestConfig) async throws -> Response {
    let startedAt = Date()
    // Check if task is cancelled
    try Task.checkCancellation()

    // Validate every caller-controlled field before it reaches the network layer.
    let method: String
    let normalizedPath: String
    let normalizedHeaders: [String: String]
    do {
      try SniConnectValidation.validateRequestId(config.requestId)
      try SniConnectValidation.validatePublicIP(config.ip)
      try SniConnectValidation.validateHostname(config.hostname)
      normalizedHeaders = try SniConnectValidation.normalizeHeaders(config.headers)
      method = try SniConnectValidation.normalizeMethod(config.method)
      normalizedPath = try SniConnectValidation.normalizePath(config.path)
      try SniConnectValidation.validateTimeout(config.effectiveTotalTimeout)
      try SniConnectValidation.validateBody(config.body)
      try SniConnectValidation.validateMethodBody(method: method, body: config.body)
    } catch {
      let sniError = SniConnectError.invalidConfig("\(error)")
      SniConnectLog.error(SniConnectLog.event("sni_request_result", [
        ("result", "error"),
        ("code", sniError.code),
        ("nativeErrorClass", String(describing: type(of: error))),
        ("requestIdHash", SniConnectLog.shortHash(config.requestId)),
        ("hostname", config.hostname.lowercased()),
        ("ipHash", SniConnectLog.shortHash(config.ip)),
        ("ipFamily", SniConnectLog.ipFamily(config.ip)),
        ("method", config.method.uppercased()),
        ("timeoutMs", Int(config.effectiveTotalTimeout)),
        ("elapsedMs", SniConnectLog.elapsedMs(since: startedAt)),
      ]))
      throw sniError
    }

    let requestSlot: SniConnectRequestLimiter.Token
    do {
      requestSlot = try requestLimiter.acquire(hostname: config.hostname, ip: config.ip)
    } catch {
      let sniError = SniConnectError.resourceLimit("\(error)")
      SniConnectLog.error(SniConnectLog.event("sni_request_result", [
        ("result", "error"),
        ("code", sniError.code),
        ("nativeErrorClass", String(describing: type(of: error))),
        ("requestIdHash", SniConnectLog.shortHash(config.requestId)),
        ("hostname", config.hostname.lowercased()),
        ("ipHash", SniConnectLog.shortHash(config.ip)),
        ("ipFamily", SniConnectLog.ipFamily(config.ip)),
        ("method", method),
        ("timeoutMs", Int(config.effectiveTotalTimeout)),
        ("elapsedMs", SniConnectLog.elapsedMs(since: startedAt)),
      ]))
      throw sniError
    }
    defer {
      requestSlot.release()
    }

    let url = try Self.buildURL(hostname: config.hostname, normalizedPath: normalizedPath)

    let mutableRequest = NSMutableURLRequest(url: url)
    mutableRequest.httpMethod = method

    // Convert milliseconds to seconds for timeout values
    let totalTimeoutSeconds = config.effectiveTotalTimeout / 1000.0
    let connectTimeoutSeconds = config.effectiveConnectTimeout / 1000.0

    // URLRequest.timeoutInterval is not a full request deadline. Keep it aligned
    // with the caller timeout as a transport guard; the wall-clock deadline below
    // enforces total request time including response body reads.
    mutableRequest.timeoutInterval = totalTimeoutSeconds
    mutableRequest.cachePolicy = .reloadIgnoringLocalCacheData

    for (key, value) in normalizedHeaders {
      mutableRequest.setValue(value, forHTTPHeaderField: key)
    }
    mutableRequest.setValue(config.hostname, forHTTPHeaderField: "Host")
    mutableRequest.setValue("identity", forHTTPHeaderField: "Accept-Encoding")

    if let bodyString = config.body, let bodyData = bodyString.data(using: .utf8) {
      mutableRequest.httpBody = bodyData
    }

    // Per-request connect timeout (avoids the process-global setter race).
    EMASCurlProtocol.setConnectTimeoutIntervalFor(mutableRequest, connectTimeoutInterval: connectTimeoutSeconds)
    let request = mutableRequest as URLRequest

    SniConnectLog.info(SniConnectLog.event("sni_request_start", [
      ("requestIdHash", SniConnectLog.shortHash(config.requestId)),
      ("hostname", config.hostname.lowercased()),
      ("ipHash", SniConnectLog.shortHash(config.ip)),
      ("ipFamily", SniConnectLog.ipFamily(config.ip)),
      ("method", method),
      ("timeoutMs", Int(config.effectiveTotalTimeout)),
      ("connectTimeoutMs", Int(config.effectiveConnectTimeout)),
      ("headerCount", normalizedHeaders.count),
      ("bodyBytes", config.body?.data(using: .utf8)?.count ?? 0),
    ]))

    do {
      let session = try session(for: config)
      return try await SniConnectWallClockDeadline.run(timeoutMilliseconds: config.effectiveTotalTimeout) {
        let (bodyBytes, response) = try await session.bytes(for: request)
        guard let httpResponse = response as? HTTPURLResponse else {
          throw SniConnectError.responseProcessingFailed("Invalid HTTP response type")
        }

        let status = httpResponse.statusCode
        let data = try await Self.readResponseBody(
          bodyBytes,
          expectedLength: httpResponse.expectedContentLength
        )
        let responseText = SniConnectResponseText.decode(data)
        let headerMaps = SniConnectResponseHeaders.make(from: httpResponse.allHeaderFields)
        let statusText = ""

        // 4xx/5xx are returned to JS as a normal response (the caller inspects
        // `status`); we only record it for diagnostics.
        let resultLog = SniConnectLog.event("sni_request_result", [
          ("result", "response"),
          ("status", status),
          ("requestIdHash", SniConnectLog.shortHash(config.requestId)),
          ("hostname", config.hostname.lowercased()),
          ("ipHash", SniConnectLog.shortHash(config.ip)),
          ("ipFamily", SniConnectLog.ipFamily(config.ip)),
          ("method", method),
          ("timeoutMs", Int(config.effectiveTotalTimeout)),
          ("responseBytes", data.count),
          ("elapsedMs", SniConnectLog.elapsedMs(since: startedAt)),
        ])
        if status >= 400 {
          SniConnectLog.warn(resultLog)
        } else {
          SniConnectLog.info(resultLog)
        }

        return Response(
          data: responseText,
          status: status,
          statusText: statusText,
          headers: headerMaps.singleValueHeaders,
          multiValueHeaders: headerMaps.multiValueHeaders
        )
      }
    } catch let error as SniConnectError {
      SniConnectLog.error(SniConnectLog.event("sni_request_result", [
        ("result", "error"),
        ("code", error.code),
        ("nativeErrorClass", String(describing: type(of: error))),
        ("requestIdHash", SniConnectLog.shortHash(config.requestId)),
        ("hostname", config.hostname.lowercased()),
        ("ipHash", SniConnectLog.shortHash(config.ip)),
        ("ipFamily", SniConnectLog.ipFamily(config.ip)),
        ("method", method),
        ("timeoutMs", Int(config.effectiveTotalTimeout)),
        ("elapsedMs", SniConnectLog.elapsedMs(since: startedAt)),
      ]))
      throw error
    } catch {
      // Convert generic errors to specific SniConnectError types
      let sniError = SniConnectError.from(error)
      SniConnectLog.error(SniConnectLog.event("sni_request_result", [
        ("result", "error"),
        ("code", sniError.code),
        ("nativeErrorClass", String(describing: type(of: error))),
        ("requestIdHash", SniConnectLog.shortHash(config.requestId)),
        ("hostname", config.hostname.lowercased()),
        ("ipHash", SniConnectLog.shortHash(config.ip)),
        ("ipFamily", SniConnectLog.ipFamily(config.ip)),
        ("method", method),
        ("timeoutMs", Int(config.effectiveTotalTimeout)),
        ("elapsedMs", SniConnectLog.elapsedMs(since: startedAt)),
      ]))
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

  private static func readResponseBody(
    _ bodyBytes: URLSession.AsyncBytes,
    expectedLength: Int64
  ) async throws -> Data {
    let maxBytes = SniConnectValidation.maxResponseBodyBytes
    if expectedLength > Int64(maxBytes) {
      throw SniConnectError.responseProcessingFailed("Response body too large")
    }

    var data = Data()
    if expectedLength > 0 {
      data.reserveCapacity(min(Int(expectedLength), maxBytes))
    }

    for try await byte in bodyBytes {
      try Task.checkCancellation()
      if data.count >= maxBytes {
        throw SniConnectError.responseProcessingFailed("Response body too large")
      }
      var nextByte = byte
      data.append(&nextByte, count: 1)
    }
    return data
  }

}
