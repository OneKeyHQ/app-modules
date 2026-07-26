import Foundation
import XCTest
@testable import RangeDownloadLogic

final class FirmwareArtifactLeaseTests: XCTestCase {
  private var rootDirectory: URL!
  private var store: FirmwareArtifactStore!

  override func setUpWithError() throws {
    rootDirectory = FileManager.default.temporaryDirectory
      .appendingPathComponent(UUID().uuidString, isDirectory: true)
    store = FirmwareArtifactStore(rootDirectory: rootDirectory)
  }

  override func tearDownWithError() throws {
    if let rootDirectory {
      try? FileManager.default.removeItem(at: rootDirectory)
    }
    store = nil
    rootDirectory = nil
  }

  func testActiveLeaseBlocksDiscardAndReleaseOnlyMakesArtifactEligible() throws {
    let artifact = try makeArtifact(now: 1)
    let lease = try store.createLease(transactionId: "transaction-1", now: 2)
    try store.retainArtifact(
      leaseRef: lease.leaseRef,
      artifactRef: artifact.artifactRef,
      now: 3
    )

    XCTAssertThrowsError(try store.discardArtifact(artifact.artifactRef, now: 4)) { error in
      XCTAssertEqual(error as? FirmwareArtifactStoreError, .artifactLeased)
    }

    try store.releaseLease(
      leaseRef: lease.leaseRef,
      disposition: .completed,
      now: 5
    )
    XCTAssertNoThrow(try store.loadArtifact(artifact.artifactRef))
    XCTAssertNoThrow(try store.discardArtifact(artifact.artifactRef, now: 6))
    XCTAssertThrowsError(try store.loadArtifact(artifact.artifactRef)) { error in
      XCTAssertEqual(error as? FirmwareArtifactStoreError, .artifactNotFound)
    }
  }

  func testReaderActivityBlocksDeletionUntilTheNativeHandleCloses() throws {
    let artifact = try makeArtifact(now: 1)
    let reader = try store.beginActivity(
      artifactRef: artifact.artifactRef,
      kind: .reader
    )

    XCTAssertThrowsError(try store.discardArtifact(artifact.artifactRef, now: 2)) { error in
      XCTAssertEqual(error as? FirmwareArtifactStoreError, .artifactLeased)
    }

    reader.release()
    XCTAssertNoThrow(try store.discardArtifact(artifact.artifactRef, now: 3))
  }

  func testIntegrityQuarantineDetachesAnInactiveArtifactFromItsActiveLease() throws {
    let artifact = try makeArtifact(now: 1)
    let lease = try store.createLease(transactionId: "transaction-1", now: 2)
    try store.retainArtifact(
      leaseRef: lease.leaseRef,
      artifactRef: artifact.artifactRef,
      now: 3
    )

    try store.quarantineArtifact(artifact.artifactRef, now: 4)

    XCTAssertThrowsError(try store.loadArtifact(artifact.artifactRef)) { error in
      XCTAssertEqual(error as? FirmwareArtifactStoreError, .artifactNotFound)
    }
    XCTAssertNoThrow(
      try store.releaseLease(
        leaseRef: lease.leaseRef,
        disposition: .safeAbandoned,
        now: 5
      )
    )
  }

  func testSidecarStoresOnlyCanonicalUrlHashNotSignedQuery() throws {
    let signedQuery = "token=secret-value"
    let artifact = try store.createArtifact(
      artifactId: "firmware-main",
      canonicalURL: "https://downloads.example.com/firmware.bin?\(signedQuery)",
      hostname: "downloads.example.com",
      route: .pinnedIp,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      retentionClass: .partial,
      now: 1
    )
    let metadataURL = rootDirectory
      .appendingPathComponent("artifacts", isDirectory: true)
      .appendingPathComponent(artifact.artifactRef, isDirectory: true)
      .appendingPathComponent("metadata.json", isDirectory: false)
    let sidecar = try String(contentsOf: metadataURL, encoding: .utf8)

    XCTAssertFalse(sidecar.contains(signedQuery))
    XCTAssertFalse(sidecar.contains("secret-value"))
    XCTAssertTrue(sidecar.contains(artifact.canonicalURLHash))
  }

  func testPartialReuseRequiresStableIdentityButIgnoresCandidateIp() throws {
    var artifact = try makeArtifact(now: 1)
    artifact.strongETag = "\"version-1\""

    XCTAssertTrue(
      artifact.canReusePartial(
        canonicalURL: "https://downloads.example.com/firmware.bin",
        hostname: "downloads.example.com",
        route: .pinnedIp,
        expectedSize: 1024,
        expectedSha256: String(repeating: "a", count: 64),
        strongETag: "\"version-1\"",
        lastModified: nil
      )
    )
    XCTAssertFalse(
      artifact.canReusePartial(
        canonicalURL: "https://downloads.example.com/firmware.bin",
        hostname: "downloads.example.com",
        route: .pinnedIp,
        expectedSize: 1024,
        expectedSha256: String(repeating: "a", count: 64),
        strongETag: "\"version-2\"",
        lastModified: nil
      )
    )
  }

  private func makeArtifact(now: TimeInterval) throws -> StoredFirmwareArtifactMetadata {
    return try store.createArtifact(
      artifactId: "firmware-main",
      canonicalURL: "https://downloads.example.com/firmware.bin",
      hostname: "downloads.example.com",
      route: .pinnedIp,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      retentionClass: .partial,
      now: now
    )
  }
}
