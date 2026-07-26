import XCTest
@testable import RangeDownloadLogic

final class FirmwareBackgroundSessionEventInboxTests: XCTestCase {
  func testEarlyCompletionHandlerIsConsumedExactlyOnce() {
    let identifier = "test-\(UUID().uuidString)"
    var completions = 0
    FirmwareBackgroundSessionEventInbox.shared.receive(
      identifier: identifier
    ) {
      completions += 1
    }

    FirmwareBackgroundSessionEventInbox.shared.complete(identifier: identifier)
    FirmwareBackgroundSessionEventInbox.shared.complete(identifier: identifier)

    XCTAssertEqual(completions, 1)
  }
}
