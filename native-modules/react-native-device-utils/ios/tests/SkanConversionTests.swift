import NitroModules
import XCTest
@testable import ReactNativeDeviceUtils

final class SkanConversionTests: XCTestCase {
    private enum ReporterError: Error, LocalizedError {
        case windowClosed

        var errorDescription: String? { "window closed" }
    }

    private final class RecordingReporter {
        private let lock = NSLock()
        private var calls: [(Int, SkanCoarse)] = []
        var failNext: Error?

        var recorded: [(Int, SkanCoarse)] {
            lock.lock()
            defer { lock.unlock() }
            return calls
        }

        func report(_ fineValue: Int, _ coarse: SkanCoarse, _ completion: @escaping (Error?) -> Void) {
            lock.lock()
            calls.append((fineValue, coarse))
            let error = failNext
            failNext = nil
            lock.unlock()
            // StoreKit answers on its own queue.
            DispatchQueue.global().async { completion(error) }
        }
    }

    private let rule = SkanCoarseRule(highMask: 0b011000, mediumMask: 0b000110)
    private var suiteName = ""
    private var defaults: UserDefaults!
    private var reporter: RecordingReporter!
    private var store: SkanConversionStore!

    override func setUp() {
        super.setUp()
        suiteName = "SkanConversionTests.\(UUID().uuidString)"
        defaults = UserDefaults(suiteName: suiteName)
        reporter = RecordingReporter()
        let reporter = reporter!
        store = SkanConversionStore(defaults: defaults) { reporter.report($0, $1, $2) }
    }

    override func tearDown() {
        defaults.removePersistentDomain(forName: suiteName)
        super.tearDown()
    }

    private func mark(_ bits: Double, rule: SkanCoarseRule? = nil) async throws -> SkanUpdateResult? {
        try await ReactNativeDeviceUtils
            .markSkanMilestones(using: store, bits: bits, coarseRule: rule ?? self.rule)
            .await()
    }

    func testMergesBitsAndOnlyGrows() async throws {
        let first = try await mark(1)
        XCTAssertEqual(first?.fineValue, 1)
        XCTAssertEqual(first?.coarseValue, .low)
        XCTAssertEqual(first?.updated, true)

        let second = try await mark(2)
        XCTAssertEqual(second?.fineValue, 3)
        XCTAssertEqual(second?.coarseValue, .medium)

        let third = try await mark(8)
        XCTAssertEqual(third?.fineValue, 11)
        XCTAssertEqual(third?.coarseValue, .high)

        XCTAssertEqual(reporter.recorded.map(\.0), [1, 3, 11])
        XCTAssertEqual(reporter.recorded.map(\.1), [.low, .medium, .high])
    }

    func testReportsUnchangedValuesInNextWindow() async throws {
        let first = try await mark(3)
        XCTAssertEqual(first?.updated, true)
        XCTAssertEqual(reporter.recorded.map(\.0), [3])
        XCTAssertEqual(reporter.recorded.map(\.1), [.medium])

        // Simulate a later window after relaunch, using the same stored bits
        // with a fresh store and reporter for the new window.
        let nextWindowReporter = RecordingReporter()
        store = SkanConversionStore(defaults: defaults, reporter: nextWindowReporter.report)
        let repeated = try await mark(0)
        XCTAssertEqual(repeated?.fineValue, 3)
        XCTAssertEqual(repeated?.coarseValue, .medium)
        XCTAssertEqual(repeated?.updated, true)
        XCTAssertEqual(nextWindowReporter.recorded.map(\.0), [3])
        XCTAssertEqual(nextWindowReporter.recorded.map(\.1), [.medium])
    }

    func testReportsZeroOnEveryCall() async throws {
        let first = try await mark(0)
        XCTAssertEqual(first?.updated, true)
        let second = try await mark(0)
        XCTAssertEqual(second?.updated, true)
        XCTAssertEqual(reporter.recorded.map(\.0), [0, 0])
        XCTAssertEqual(reporter.recorded.map(\.1), [.low, .low])
    }

    func testReportsAgainWhenCoarseRuleChanges() async throws {
        _ = try await mark(2)
        let reclassified = try await mark(0, rule: SkanCoarseRule(highMask: 2, mediumMask: 0))
        XCTAssertEqual(reclassified?.coarseValue, .high)
        XCTAssertEqual(reclassified?.updated, true)
        XCTAssertEqual(reporter.recorded.count, 2)
    }

    func testFailedReportIsNotPersistedAndRetries() async throws {
        reporter.failNext = ReporterError.windowClosed
        do {
            _ = try await mark(4)
            XCTFail("Expected the reporter error to reject")
        } catch {
            XCTAssertEqual(
                error.localizedDescription,
                "SKAdNetwork rejected the conversion value update: window closed"
            )
        }
        XCTAssertNil(defaults.object(forKey: SkanConversionStore.fineValueKey))

        let retried = try await mark(4)
        XCTAssertEqual(retried?.fineValue, 4)
        XCTAssertEqual(retried?.updated, true)
        XCTAssertEqual(reporter.recorded.map(\.0), [4, 4])
    }

    func testRejectsInvalidInput() async {
        for bits in [-1, 64, 1.5, Double.nan, Double.infinity] {
            do {
                _ = try await mark(bits)
                XCTFail("Expected \(bits) to reject")
            } catch {
                XCTAssertEqual(error as? SkanConversionError, .invalidValue("bits"))
            }
        }
        do {
            _ = try await mark(1, rule: SkanCoarseRule(highMask: 128, mediumMask: 0))
            XCTFail("Expected an out-of-range mask to reject")
        } catch {
            XCTAssertEqual(error as? SkanConversionError, .invalidValue("coarseRule.highMask"))
        }
        XCTAssertTrue(reporter.recorded.isEmpty)
    }

    func testTimesOutWithoutPersisting() async {
        let silent = SkanConversionStore(
            defaults: defaults,
            reporter: { _, _, _ in },
            reportTimeout: .milliseconds(50)
        )
        do {
            _ = try await ReactNativeDeviceUtils
                .markSkanMilestones(using: silent, bits: 1, coarseRule: rule)
                .await()
            XCTFail("Expected a timeout")
        } catch {
            XCTAssertEqual(error as? SkanConversionError, .timedOut)
        }
        XCTAssertNil(defaults.object(forKey: SkanConversionStore.fineValueKey))
    }

    func testLateSuccessPersistsBeforeSubmissionsResume() async throws {
        try await checkLateCompletion(error: nil, expectedStoredValue: 8, expectedNextValue: 9)
    }

    func testLateFailureAllowsRetryWithoutPersisting() async throws {
        try await checkLateCompletion(
            error: ReporterError.windowClosed,
            expectedStoredValue: 0,
            expectedNextValue: 1
        )
    }

    private func checkLateCompletion(
        error: Error?,
        expectedStoredValue: Int,
        expectedNextValue: Double
    ) async throws {
        var finishFirstReport: ((Error?) -> Void)?
        let reporter = reporter!
        store = SkanConversionStore(
            defaults: defaults,
            reporter: { fineValue, coarse, completion in
                if finishFirstReport == nil {
                    finishFirstReport = completion
                } else {
                    reporter.report(fineValue, coarse, completion)
                }
            },
            reportTimeout: .milliseconds(50)
        )
        do {
            _ = try await mark(8)
            XCTFail("Expected a timeout")
        } catch {
            XCTAssertEqual(error as? SkanConversionError, .timedOut)
        }
        do {
            _ = try await mark(1)
            XCTFail("Expected the timed-out report to block submissions")
        } catch {
            XCTAssertEqual(error as? SkanConversionError, .reportInProgress)
        }
        XCTAssertNil(defaults.object(forKey: SkanConversionStore.fineValueKey))

        let finish = try XCTUnwrap(finishFirstReport)
        finish(error)
        let stored = try await mark(0)
        let next = try await mark(1)
        XCTAssertEqual(stored?.fineValue, Double(expectedStoredValue))
        XCTAssertEqual(next?.fineValue, expectedNextValue)
        XCTAssertEqual(reporter.recorded.map(\.0), [expectedStoredValue, Int(expectedNextValue)])
    }

    func testConcurrentCallersNeverReportASmallerValue() async throws {
        try await withThrowingTaskGroup(of: Void.self) { group in
            for bit in 0..<6 {
                group.addTask { _ = try await self.mark(Double(1 << bit)) }
            }
            try await group.waitForAll()
        }
        let values = reporter.recorded.map(\.0)
        XCTAssertEqual(values.last, 63)
        XCTAssertEqual(values, values.sorted())
        XCTAssertEqual(defaults.integer(forKey: SkanConversionStore.fineValueKey), 63)
    }
}
