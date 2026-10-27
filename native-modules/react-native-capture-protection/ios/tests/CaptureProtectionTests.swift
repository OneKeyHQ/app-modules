import Darwin
import NitroModules
import UIKit
import XCTest
@testable import ReactNativeCaptureProtection

private final class CaptureProtectionTestAppDelegate: UIResponder, UIApplicationDelegate {
  var window: UIWindow? = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 480))
}

@MainActor
final class CaptureProtectionTests: XCTestCase {
  private static var retainedDelegate: CaptureProtectionTestAppDelegate?

  override func setUp() {
    super.setUp()
    Self.ensureApplicationWindow()
  }

  private static func ensureApplicationWindow() {
    if (UIApplication.value(forKey: "sharedApplication") as? UIApplication) == nil,
      let sym = dlsym(UnsafeMutableRawPointer(bitPattern: -2), "UIApplicationInstantiateSingleton")
    {
      typealias InstantiateFn = @convention(c) (AnyClass) -> Void
      unsafeBitCast(sym, to: InstantiateFn.self)(UIApplication.self)
    }
    if (UIApplication.shared.delegate?.window ?? nil) == nil {
      let delegate = CaptureProtectionTestAppDelegate()
      retainedDelegate = delegate
      UIApplication.shared.delegate = delegate
    }
  }

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
    let window = try XCTUnwrap(UIApplication.shared.delegate?.window ?? nil)
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
