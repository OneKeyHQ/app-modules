import Foundation

@objc(FirmwareBackgroundSessionEventRouter)
public final class FirmwareBackgroundSessionEventRouter: NSObject {
  @objc(routeEventsForBackgroundURLSession:completionHandler:)
  public static func routeEvents(
    forBackgroundURLSession identifier: String,
    completionHandler: @escaping () -> Void
  ) -> NSNumber {
    NSNumber(
      value: RangeDownloader.routeFirmwareBackgroundEvents(
        identifier: identifier,
        completionHandler: completionHandler
      )
    )
  }
}
