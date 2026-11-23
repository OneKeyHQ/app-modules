import Foundation
import Security

enum BiometricAuthStateStore {
  private static let transactionLock = NSLock()

  static func check(
    currentState: () -> Data?,
    read: () -> (OSStatus, AnyObject?),
    update: (Data) -> OSStatus,
    add: (Data) -> OSStatus,
    legacyState: () -> Data?,
    removeLegacyState: () -> Void
  ) throws -> Bool {
    transactionLock.lock()
    defer { transactionLock.unlock() }

    guard let current = currentState() else {
      throw failure("Current biometric domain state is unavailable", status: 1)
    }
    let (readStatus, result) = read()
    let previous: Data?
    let migrating: Bool
    switch readStatus {
    case errSecSuccess:
      guard let data = result as? Data else {
        throw failure("Keychain read returned invalid data", status: errSecDecode)
      }
      previous = data
      migrating = false
    case errSecItemNotFound:
      previous = legacyState()
      migrating = previous != nil
    default:
      throw failure("Keychain read failed", status: readStatus)
    }

    let updateStatus = update(current)
    if updateStatus == errSecItemNotFound {
      let addStatus = add(current)
      guard addStatus == errSecSuccess else {
        throw failure("Keychain add failed", status: addStatus)
      }
    } else if updateStatus != errSecSuccess {
      throw failure("Keychain update failed", status: updateStatus)
    }
    if migrating { removeLegacyState() }
    return previous.map { $0 != current } ?? false
  }

  private static func failure(_ message: String, status: OSStatus) -> NSError {
    NSError(
      domain: "BiometricAuthChanged", code: Int(status),
      userInfo: [NSLocalizedDescriptionKey: "\(message) (OSStatus \(status))"]
    )
  }
}
