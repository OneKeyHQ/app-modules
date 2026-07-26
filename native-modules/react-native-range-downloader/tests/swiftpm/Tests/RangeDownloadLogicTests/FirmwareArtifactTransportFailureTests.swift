import Foundation
import XCTest
@testable import RangeDownloadLogic

final class FirmwareArtifactTransportFailureTests: XCTestCase {
  func testCertificateFailureIsFatal() {
    let error = NSError(
      domain: NSURLErrorDomain,
      code: NSURLErrorServerCertificateUntrusted
    )

    XCTAssertEqual(
      FirmwareArtifactTransportFailureClassifier.classify(error),
      .tls
    )
  }

  func testReachabilityFailureIsRetryable() {
    let error = NSError(
      domain: NSURLErrorDomain,
      code: NSURLErrorCannotConnectToHost
    )

    XCTAssertEqual(
      FirmwareArtifactTransportFailureClassifier.classify(error),
      .network
    )
  }

  func testUnknownUrlTransportFailureDoesNotPermitRouteFallback() {
    let error = NSError(domain: NSURLErrorDomain, code: -1999)

    XCTAssertEqual(
      FirmwareArtifactTransportFailureClassifier.classify(error),
      .transport
    )
  }

  func testNotImplementedAndVersionUnsupportedArePermanent() {
    XCTAssertFalse(isRetryableFirmwareArtifactHTTPStatus(501))
    XCTAssertFalse(isRetryableFirmwareArtifactHTTPStatus(505))
    XCTAssertTrue(isRetryableFirmwareArtifactHTTPStatus(500))
    XCTAssertTrue(isRetryableFirmwareArtifactHTTPStatus(503))
  }
}
