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

    func testSkipsUnchangedValue() async throws {
        _ = try await mark(3)
        let repeated = try await mark(1)
        XCTAssertEqual(repeated?.fineValue, 3)
        XCTAssertEqual(repeated?.updated, false)
        XCTAssertEqual(reporter.recorded.count, 1)
    }

    func testReportsZeroOnceOnFirstCall() async throws {
        let first = try await mark(0)
        XCTAssertEqual(first?.updated, true)
        let second = try await mark(0)
        XCTAssertEqual(second?.updated, false)
        XCTAssertEqual(reporter.recorded.count, 1)
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
