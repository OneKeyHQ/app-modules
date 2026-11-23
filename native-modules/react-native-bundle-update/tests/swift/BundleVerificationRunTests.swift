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

    func testFallbackActivationRemovesTargetBeforeEviction() {
        func record(_ version: String) -> [String: String] {
            ["appVersion": "6.8.0", "bundleVersion": version, "signature": version]
        }
        let existing = ["B", "C", "D"].map(record)
        let afterB = planFallbackBundles(existing, activeAppVersion: "6.8.0", activeBundleVersion: "B", previous: record("A"))
        XCTAssertEqual(afterB.retained, ["C", "D", "A"].map(record))
        XCTAssertTrue(afterB.evicted.isEmpty)

        let afterE = planFallbackBundles(afterB.retained, activeAppVersion: "6.8.0", activeBundleVersion: "E", previous: record("B"))
        XCTAssertEqual(afterE.retained, ["D", "A", "B"].map(record))
        XCTAssertEqual(afterE.evicted, [record("C")])
    }
}
