import Foundation

public final class FirmwareBackgroundSessionEventInbox {
  public static let shared = FirmwareBackgroundSessionEventInbox()

  private let lock = NSLock()
  private var completionHandlers: [String: () -> Void] = [:]

  private init() {}

  public func receive(
    identifier: String,
    completionHandler: @escaping () -> Void
  ) {
    let replacedHandler: (() -> Void)? = lock.withFirmwareLock {
      return completionHandlers.updateValue(
        completionHandler,
        forKey: identifier
      )
    }
    // UIKit should deliver only one live handler per background session. If it
    // violates that contract, complete the older one instead of leaking it.
    replacedHandler?()
  }

  func complete(identifier: String) {
    let handler: (() -> Void)? = lock.withFirmwareLock {
      return completionHandlers.removeValue(forKey: identifier)
    }
    handler?()
  }
}
