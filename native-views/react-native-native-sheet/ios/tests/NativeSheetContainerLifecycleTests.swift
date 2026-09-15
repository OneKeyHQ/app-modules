import XCTest
import UIKit
@testable import NativeSheet

@MainActor
final class NativeSheetContainerLifecycleTests: XCTestCase {
  func testStagesAndRemovesTheSameContentViewAcrossPresentationCycles() {
    let host = NativeSheetContainerView(frame: CGRect(x: 0, y: 0, width: 320, height: 240))
    let content = UIView(frame: host.bounds)
    content.autoresizingMask = [.flexibleWidth, .flexibleHeight]

    host.insertChild(content, atIndex: 0)

    XCTAssertNotNil(content.superview)
    XCTAssertTrue(content.autoresizingMask.isEmpty)
    if #available(iOS 26.0, *) {
      XCTAssertNil(content.superview?.superview)
    } else {
      XCTAssertIdentical(content.superview?.superview, host)
    }

    host.removeChild(content)

    XCTAssertNil(content.superview)
    XCTAssertTrue(content.autoresizingMask.isEmpty)
  }
}
