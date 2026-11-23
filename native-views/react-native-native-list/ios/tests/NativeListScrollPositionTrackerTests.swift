import XCTest
@testable import NativeListModule

final class NativeListScrollPositionTrackerTests: XCTestCase {
  func testHysteresisAndDisabledLifecycle() {
    var tracker = NativeListScrollPositionTracker()
    XCTAssertNil(tracker.update(offset: 200))
    tracker.configure(json: "{\"start\":48,\"end\":160}")
    XCTAssertEqual(tracker.update(offset: 0), false)
    XCTAssertNil(tracker.update(offset: 159))
    XCTAssertEqual(tracker.update(offset: 160), true)
    XCTAssertNil(tracker.update(offset: 100))
    XCTAssertEqual(tracker.update(offset: 48), false)
    XCTAssertNil(tracker.update(offset: 49))
    tracker.configure(json: "")
    XCTAssertNil(tracker.update(offset: 200))
    tracker.configure(json: "{\"start\":48,\"end\":160}")
    XCTAssertEqual(tracker.update(offset: 200), true)
    tracker.resetDelivery()
    XCTAssertEqual(tracker.update(offset: 200), true)
    XCTAssertNil(tracker.update(offset: .nan))
    tracker.configure(json: "{\"start\":48,\"end\":48}")
    XCTAssertNil(tracker.update(offset: 48))
  }
}
