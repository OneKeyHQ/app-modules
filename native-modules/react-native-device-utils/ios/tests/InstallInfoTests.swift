import NitroModules
import XCTest
@testable import ReactNativeDeviceUtils

final class InstallInfoTests: XCTestCase {
    private enum StoreError: Error {
        case unavailable
    }

    func testInstallerMapsProductionToAppStore() async throws {
        let promise = ReactNativeDeviceUtils.resolveInstallerPackageName(
            isSimulator: false,
            hasEmbeddedProvision: false
        ) {
            .production
        }
        let installer = try await promise.await()
        XCTAssertEqual(installer, .appstore)
    }

    func testInstallerMapsSandboxToTestFlight() async throws {
        let promise = ReactNativeDeviceUtils.resolveInstallerPackageName(
            isSimulator: false,
            hasEmbeddedProvision: false
        ) {
            .sandbox
        }
        let installer = try await promise.await()
        XCTAssertEqual(installer, .testflight)
    }

    func testInstallerMapsXcodeToOther() async throws {
        let promise = ReactNativeDeviceUtils.resolveInstallerPackageName(
            isSimulator: false,
            hasEmbeddedProvision: false
        ) {
            .xcode
        }
        let installer = try await promise.await()
        XCTAssertEqual(installer, .other)
    }

    func testInstallerReturnsUnknownWhenUnverifiedOrError() async throws {
        let nilResult = try await ReactNativeDeviceUtils.resolveInstallerPackageName(
            isSimulator: false,
            hasEmbeddedProvision: false
        ) {
            nil
        }.await()
        XCTAssertEqual(nilResult, .unknown)

        let errorResult = try await ReactNativeDeviceUtils.resolveInstallerPackageName(
            isSimulator: false,
            hasEmbeddedProvision: false
        ) {
            throw StoreError.unavailable
        }.await()
        XCTAssertEqual(errorResult, .unknown)
    }

    func testInstallerPrioritizesSimulatorAndEmbeddedProvision() async throws {
        let simResult = try await ReactNativeDeviceUtils.resolveInstallerPackageName(
            isSimulator: true,
            hasEmbeddedProvision: false
        ) {
            XCTFail("StoreKit should not be queried on simulator")
            return .production
        }.await()
        XCTAssertEqual(simResult, .unknown)

        let adHocResult = try await ReactNativeDeviceUtils.resolveInstallerPackageName(
            isSimulator: false,
            hasEmbeddedProvision: true
        ) {
            XCTFail("StoreKit should not be queried when embedded.mobileprovision exists")
            return .production
        }.await()
        XCTAssertEqual(adHocResult, .other)
    }

    @MainActor
    func testInstallationTimeResolvesEpochMillisOffMainThread() async throws {
        XCTAssertTrue(Thread.isMainThread)
        let expectedDate = Date(timeIntervalSince1970: 1_700_000_000.25)
        let promise = ReactNativeDeviceUtils.resolveInstallationTime {
            XCTAssertFalse(Thread.isMainThread)
            return expectedDate
        }
        let timestampMs = try await promise.await()
        XCTAssertEqual(timestampMs, 1_700_000_000_250.0, accuracy: 0.5)
    }

    func testInstallationTimeRejectsMissingOrInvalidDate() async {
        do {
            _ = try await ReactNativeDeviceUtils.resolveInstallationTime { nil }.await()
            XCTFail("Expected missing date to reject")
        } catch {
            XCTAssertEqual(
                String(describing: error),
                "Unable to determine app installation time"
            )
        }

        do {
            _ = try await ReactNativeDeviceUtils.resolveInstallationTime {
                Date(timeIntervalSince1970: 0)
            }.await()
            XCTFail("Expected non-positive date to reject")
        } catch {
            XCTAssertEqual(
                String(describing: error),
                "App installation time is invalid"
            )
        }
    }

    func testInstallationTimePreservesProviderError() async {
        do {
            _ = try await ReactNativeDeviceUtils.resolveInstallationTime {
                throw StoreError.unavailable
            }.await()
            XCTFail("Expected provider error to reject")
        } catch {
            XCTAssertEqual(error as? StoreError, .unavailable)
        }
    }
}
