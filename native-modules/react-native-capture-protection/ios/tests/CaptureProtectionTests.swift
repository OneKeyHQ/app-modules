import NitroModules
import UIKit
import XCTest
@testable import ReactNativeCaptureProtection

@MainActor
final class CaptureProtectionTests: XCTestCase {
  func testNativeSubscriptionsRemainScopedToTheirObjects() throws {
    let first = ReactNativeCaptureProtection()
    let second = ReactNativeCaptureProtection()
    var firstEvents: [Double] = []
    var secondEvents: [Double] = []
    try first.setListener { firstEvents.append($0) }
    try second.setListener { secondEvents.append($0) }
    NotificationCenter.default.post(name: UIApplication.userDidTakeScreenshotNotification, object: nil)
    XCTAssertEqual(firstEvents, [3])
    XCTAssertEqual(secondEvents, [3])
    try first.setListener(callback: nil)
    NotificationCenter.default.post(name: UIApplication.userDidTakeScreenshotNotification, object: nil)
    XCTAssertEqual(firstEvents, [3])
    XCTAssertEqual(secondEvents, [3, 3])
    try second.setListener(callback: nil)
  }

  func testDisposalStopsNativeEvents() async throws {
    let instance = ReactNativeCaptureProtection()
    var events: [Double] = []
    try instance.setListener { events.append($0) }
    instance.dispose()
    // allow is queued after disposal and provides a deterministic UI-thread barrier.
    try await instance.allow().await()
    NotificationCenter.default.post(name: UIApplication.userDidTakeScreenshotNotification, object: nil)
    XCTAssertTrue(events.isEmpty)
  }

  func testOneOwnerCannotReleaseAnotherOwnersSecureLayer() async throws {
    let previousDelegate = UIApplication.shared.delegate
    let delegate = CaptureTestDelegate()
    let window = UIWindow(frame: UIScreen.main.bounds)
    window.rootViewController = UIViewController()
    delegate.window = window
    UIApplication.shared.delegate = delegate
    window.makeKeyAndVisible()
    defer {
      window.isHidden = true
      UIApplication.shared.delegate = previousDelegate
    }
    let first = ReactNativeCaptureProtection()
    let second = ReactNativeCaptureProtection()
    try await first.prevent().await()
    try await second.prevent().await()
    let field = window.layer.superlayer?.superlayer?.delegate as? UITextField
    XCTAssertNotNil(field)
    XCTAssertEqual(field?.isSecureTextEntry, true)
    try await first.allow().await()
    XCTAssertEqual(field?.isSecureTextEntry, true)
    try await second.allow().await()
    XCTAssertEqual(field?.isSecureTextEntry, false)
  }
}

private final class CaptureTestDelegate: NSObject, UIApplicationDelegate {
  var window: UIWindow?
}
