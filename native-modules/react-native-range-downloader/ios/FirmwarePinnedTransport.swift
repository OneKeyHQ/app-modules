import Foundation
import ObjectiveC
import EMASCurl

@objc(OneKeyFirmwarePinnedDNSResolverBase)
private class FirmwarePinnedDNSResolverBase:
  NSObject,
  EMASCurlProtocolDNSResolver
{
  @objc class func resolveDomain(_ domain: String) -> String? {
    FirmwarePinnedDNSResolverFactory.resolve(
      domain: domain,
      resolverClass: self
    )
  }
}

private enum FirmwarePinnedDNSResolverFactory {
  private static let queue = DispatchQueue(
    label: "so.onekey.firmware-artifacts.pinned-dns-resolvers"
  )
  private static let registry = FirmwarePinnedResolverRegistry()
  private static var nextClassID = 0

  static func acquire(
    hostname: String,
    resolvedIP: String
  ) -> FirmwarePinnedResolverLease? {
    return queue.sync {
      guard let resolverClass = registry.acquire(
        hostname: hostname,
        resolvedIP: resolvedIP,
        allocateClass: allocateResolverClass
      ), let typedClass =
        resolverClass as? EMASCurlProtocolDNSResolver.Type else {
        return nil
      }
      return FirmwarePinnedResolverLease(resolverClass: typedClass)
    }
  }

  static func resolve(domain: String, resolverClass: AnyClass) -> String? {
    return queue.sync {
      registry.resolve(domain: domain, resolverClass: resolverClass)
    }
  }

  static func release(resolverClass: AnyClass) {
    queue.sync {
      registry.release(resolverClass: resolverClass)
    }
  }

  private static func allocateResolverClass() -> AnyClass {
    while true {
      nextClassID += 1
      let className = "OneKeyFirmwarePinnedDNSResolver_\(nextClassID)"
      if let resolverClass = objc_allocateClassPair(
        FirmwarePinnedDNSResolverBase.self,
        className,
        0
      ) {
        objc_registerClassPair(resolverClass)
        return resolverClass
      }
    }
  }
}

private final class FirmwarePinnedResolverLease {
  let resolverClass: EMASCurlProtocolDNSResolver.Type

  private let lock = NSLock()
  private var released = false

  init(resolverClass: EMASCurlProtocolDNSResolver.Type) {
    self.resolverClass = resolverClass
  }

  func release() {
    let shouldRelease = lock.withFirmwareLock {
      if released {
        return false
      }
      released = true
      return true
    }
    if shouldRelease {
      FirmwarePinnedDNSResolverFactory.release(
        resolverClass: resolverClass
      )
    }
  }

  deinit {
    release()
  }
}

private final class FirmwarePinnedSessionDelegate:
  NSObject,
  URLSessionDelegate
{
  private let resolverLease: FirmwarePinnedResolverLease

  init(resolverLease: FirmwarePinnedResolverLease) {
    self.resolverLease = resolverLease
  }

  func urlSession(
    _ session: URLSession,
    didBecomeInvalidWithError error: Error?
  ) {
    resolverLease.release()
  }
}

final class FirmwarePinnedSessionLease {
  let session: URLSession

  private let delegate: FirmwarePinnedSessionDelegate
  private let lock = NSLock()
  private var released = false

  fileprivate init(
    session: URLSession,
    delegate: FirmwarePinnedSessionDelegate
  ) {
    self.session = session
    self.delegate = delegate
  }

  func release() {
    let shouldRelease = lock.withFirmwareLock {
      if released {
        return false
      }
      released = true
      return true
    }
    if shouldRelease {
      session.invalidateAndCancel()
    }
  }

  deinit {
    release()
  }
}

enum FirmwarePinnedTransport {
  static func acquireSession(
    hostname: String,
    resolvedIP: String,
    deadlineAt: TimeInterval
  ) async throws -> FirmwarePinnedSessionLease {
    guard FirmwarePinnedRouteValidation.isValid(
      hostname: hostname,
      resolvedIP: resolvedIP
    ) else {
      throw FirmwareArtifactTransferError.invalidRequest
    }

    while true {
      try Task.checkCancellation()
      guard Date().timeIntervalSince1970 < deadlineAt else {
        throw FirmwareArtifactTransferError.deadlineExceeded
      }
      if let resolverLease = FirmwarePinnedDNSResolverFactory.acquire(
        hostname: hostname,
        resolvedIP: resolvedIP
      ) {
        return makeSession(resolverLease: resolverLease)
      }
      try await Task.sleep(nanoseconds: 10_000_000)
    }
  }

  private static func makeSession(
    resolverLease: FirmwarePinnedResolverLease
  ) -> FirmwarePinnedSessionLease {
    let configuration = URLSessionConfiguration.ephemeral
    configuration.requestCachePolicy = .reloadIgnoringLocalCacheData
    configuration.urlCache = nil
    configuration.httpCookieStorage = nil
    configuration.httpShouldSetCookies = false
    configuration.connectionProxyDictionary = [:]
    configuration.shouldUseExtendedBackgroundIdleMode = false
    configuration.httpMaximumConnectionsPerHost = 8

    let curlConfiguration = EMASCurlConfiguration.default()
    curlConfiguration.httpVersion = .HTTP1
    curlConfiguration.connectTimeoutInterval = 2.5
    curlConfiguration.enableBuiltInGzip = false
    curlConfiguration.enableBuiltInRedirection = false
    curlConfiguration.cacheEnabled = false
    curlConfiguration.certificateValidationEnabled = true
    curlConfiguration.domainNameVerificationEnabled = true
    curlConfiguration.dnsResolver = resolverLease.resolverClass

    EMASCurlProtocol.install(into: configuration, with: curlConfiguration)
    let delegate = FirmwarePinnedSessionDelegate(
      resolverLease: resolverLease
    )
    let session = URLSession(
      configuration: configuration,
      delegate: delegate,
      delegateQueue: nil
    )
    return FirmwarePinnedSessionLease(
      session: session,
      delegate: delegate
    )
  }
}
