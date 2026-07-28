import Foundation
import EMASCurl

struct SniConnectPinnedTransportResources {
  let configuration: URLSessionConfiguration
  let resolverLease: SniConnectPinnedResolverLease
  let delegate: SniConnectSessionInvalidationDelegate
}

public final class SniConnectPinnedSession {
  public let session: URLSession

  init(resources: SniConnectPinnedTransportResources) {
    session = URLSession(
      configuration: resources.configuration,
      delegate: resources.delegate,
      delegateQueue: nil
    )
  }

  public func close() {
    session.finishTasksAndInvalidate()
  }

  deinit {
    close()
  }
}

public enum SniConnectPinnedTransport {
  static func makeResources(
    hostname: String,
    ip: String,
    dataDelegate: URLSessionDataDelegate? = nil
  ) throws -> SniConnectPinnedTransportResources {
    try SniConnectValidation.validateHostname(hostname)
    try SniConnectValidation.validatePublicIP(ip)

    let normalizedHostname = hostname.lowercased()
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
    curlConfig.certificateValidationEnabled = true
    curlConfig.domainNameVerificationEnabled = true
    curlConfig.dnsResolver = try PinnedDNSResolverFactory.resolverClass(
      hostname: normalizedHostname,
      ip: ip
    )
    EMASCurlProtocol.install(into: configuration, with: curlConfig)

    let resolverLease = SniConnectPinnedResolverLease(
      hostname: normalizedHostname,
      ip: ip
    )
    let delegate = SniConnectSessionInvalidationDelegate(
      hostname: normalizedHostname,
      ip: ip,
      resolverLease: resolverLease,
      forwardingDataDelegate: dataDelegate
    )
    return SniConnectPinnedTransportResources(
      configuration: configuration,
      resolverLease: resolverLease,
      delegate: delegate
    )
  }

  public static func makeSession(
    hostname: String,
    ip: String,
    dataDelegate: URLSessionDataDelegate? = nil
  ) throws -> SniConnectPinnedSession {
    SniConnectPinnedSession(
      resources: try makeResources(
        hostname: hostname,
        ip: ip,
        dataDelegate: dataDelegate
      )
    )
  }
}
