import Foundation

@objc(FirmwareBackgroundSessionEventRouter)
public final class FirmwareBackgroundSessionEventRouter: NSObject {
  @objc(routeEventsForBackgroundURLSession:completionHandler:)
  public static func routeEvents(
    forBackgroundURLSession identifier: String,
    completionHandler: @escaping () -> Void
  ) -> NSNumber {
    guard identifier == FirmwareDomainBackgroundCoordinator.sessionIdentifier else {
      return NSNumber(value: false)
    }
    FirmwareArtifactDownloader.attachBackgroundSessionEvents(
      identifier: identifier,
      completionHandler: completionHandler
    )
    return NSNumber(value: true)
  }
}
