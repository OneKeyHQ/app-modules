import NitroModules
import XCTest
@testable import ReactNativeDeviceUtils

final class AppleAdsAttributionTests: XCTestCase {
    private enum ProviderError: Error {
        case unavailable
    }

    func testResolvesNonEmptyTokenOffMainThread() async throws {
        let promise = ReactNativeDeviceUtils.requestAppleAdsAttributionToken {
            XCTAssertFalse(Thread.isMainThread)
            return "test-token"
        }

        let token = try await promise.await()
        XCTAssertEqual(token, "test-token")
    }

    func testRejectsEmptyToken() async {
        let promise = ReactNativeDeviceUtils.requestAppleAdsAttributionToken { "" }

        do {
            _ = try await promise.await()
            XCTFail("Expected the empty token to reject")
        } catch {
            XCTAssertEqual(
                String(describing: error),
                "AdServices returned an empty attribution token"
            )
        }
    }

    func testPreservesProviderError() async {
        let promise = ReactNativeDeviceUtils.requestAppleAdsAttributionToken {
            throw ProviderError.unavailable
        }

        do {
            _ = try await promise.await()
            XCTFail("Expected the provider error to reject")
        } catch {
            XCTAssertEqual(error as? ProviderError, .unavailable)
        }
    }
}
