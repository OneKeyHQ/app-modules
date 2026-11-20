@testable import BundleDiffReconstruction
import XCTest

final class BundleVerificationRunTests: XCTestCase {
    func testCancelledVerificationCannotPublish() async {
        let run = BundleVerificationRun()
        run.cancel()
        XCTAssertTrue(run.wasCancelledByClear)
        XCTAssertThrowsError(try run.publish { XCTFail("Cancelled verification attempted to publish") }) {
            XCTAssertTrue($0 is CancellationError)
        }

        let prematureCompletion = await run.waitForCompletion(timeout: 0)
        XCTAssertFalse(prematureCompletion)
        run.finish()
        let finished = await run.waitForCompletion(timeout: 0)
        XCTAssertTrue(finished)
    }

    func testPublishedVerificationRemainsSuccessfulThroughCleanup() throws {
        let run = BundleVerificationRun()
        var published = false
        try run.publish { published = true }
        run.cancel()
        XCTAssertTrue(published)
        XCTAssertFalse(run.wasCancelledByClear)
        run.finish()
    }
}
