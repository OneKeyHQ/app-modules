import Foundation
import EMASCurl

@objc(SniReplayLoopbackResolver)
final class SniReplayLoopbackResolver: NSObject, EMASCurlProtocolDNSResolver {
  @objc class func resolveDomain(_ domain: String) -> String? {
    domain == "sni-replay.local" ? "127.0.0.1" : nil
  }
}

// Appended to a temporary copy of Client.swift to reach its existing private cache.
extension SniConnectClient {
  static func installReplayFixture(caPath: String) throws {
    EMASCurlProtocol.setLogLevel(.debug)
    try sessionsQueue.sync {
      let hostname = "sni-replay.local"
      let ip = "93.184.216.34"
      let resources = try SniConnectPinnedTransport.makeResources(hostname: hostname, ip: ip)
      guard let id = resources.configuration.httpAdditionalHeaders?["X-EMASCurl-Config-ID"] as? String,
            let original = ReplayConfiguration(id),
            let config = original.copy() as? EMASCurlConfiguration else {
        throw NSError(domain: "SniReplayFixture", code: 1)
      }
      config.dnsResolver = SniReplayLoopbackResolver.self
      config.caFilePath = caPath
      // The production configuration retains certificate and hostname verification.
      guard config.certificateValidationEnabled && config.domainNameVerificationEnabled else {
        throw NSError(domain: "SniReplayFixture", code: 2)
      }
      ReplayStoreConfiguration(id, config)
      let session = URLSession(
        configuration: resources.configuration,
        delegate: resources.delegate,
        delegateQueue: nil
      )
      let key = SessionKey(hostname: hostname, ip: ip)
      sessionCache[key] = ManagedSession(session: session, resolverLease: resources.resolverLease)
      markSessionUsed(key)
    }
  }
}
