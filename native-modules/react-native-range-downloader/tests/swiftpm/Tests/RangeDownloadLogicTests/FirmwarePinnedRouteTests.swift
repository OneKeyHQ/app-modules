import Foundation
import XCTest
@testable import RangeDownloadLogic

final class FirmwarePinnedRouteTests: XCTestCase {
  func testRouteRequiresDnsHostnameAndPublicLiteralIp() {
    XCTAssertTrue(
      FirmwarePinnedRouteValidation.isValid(
        hostname: "downloads.example.com",
        resolvedIP: "93.184.216.34"
      )
    )
    XCTAssertTrue(
      FirmwarePinnedRouteValidation.isValid(
        hostname: "downloads.example.com",
        resolvedIP: "2606:4700:4700::1111"
      )
    )
    XCTAssertFalse(
      FirmwarePinnedRouteValidation.isValid(
        hostname: "93.184.216.34",
        resolvedIP: "93.184.216.34"
      )
    )
  }

  func testRouteRejectsPrivateReservedAndTransitionAddresses() {
    let rejected = [
      "127.0.0.1",
      "10.0.0.1",
      "169.254.169.254",
      "192.0.2.1",
      "224.0.0.1",
      "::1",
      "fc00::1",
      "fe80::1",
      "2001:db8::1",
      "2002:0808:0808::1",
      "64:ff9b::7f00:1",
      "::ffff:127.0.0.1",
    ]
    for address in rejected {
      XCTAssertFalse(
        FirmwarePinnedRouteValidation.isValid(
          hostname: "downloads.example.com",
          resolvedIP: address
        ),
        address
      )
    }
  }

  func testResolverRegistryIsBoundedAndFailsClosedByHostname() throws {
    final class FirstResolver: NSObject {}
    final class SecondResolver: NSObject {}
    let registry = FirmwarePinnedResolverRegistry(maxEntries: 1)
    let first = try XCTUnwrap(
      registry.acquire(
        hostname: "downloads.example.com",
        resolvedIP: "93.184.216.34"
      ) {
        FirstResolver.self
      }
    )

    XCTAssertEqual(registry.activeEntryCount, 1)
    XCTAssertEqual(registry.allocatedEntryCount, 1)
    XCTAssertEqual(
      registry.resolve(
        domain: "DOWNLOADS.EXAMPLE.COM",
        resolverClass: first
      ),
      "93.184.216.34"
    )
    XCTAssertNil(
      registry.resolve(
        domain: "redirect.example.com",
        resolverClass: first
      )
    )
    XCTAssertNil(
      registry.acquire(
        hostname: "other.example.com",
        resolvedIP: "93.184.216.35"
      ) {
        SecondResolver.self
      }
    )

    registry.release(resolverClass: first)
    let reused = try XCTUnwrap(
      registry.acquire(
        hostname: "other.example.com",
        resolvedIP: "93.184.216.35"
      ) {
        SecondResolver.self
      }
    )
    XCTAssertTrue(first === reused)
    XCTAssertEqual(registry.allocatedEntryCount, 1)
  }
}
