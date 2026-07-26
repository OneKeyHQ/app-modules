import Foundation
import XCTest
@testable import RangeDownloadLogic

final class FirmwareArtifactPromotionTests: XCTestCase {
  private var root: URL!
  private var store: FirmwareArtifactStore!

  override func setUpWithError() throws {
    root = FileManager.default.temporaryDirectory.appendingPathComponent(
      "firmware-artifact-promotion-\(UUID().uuidString)",
      isDirectory: true
    )
    store = FirmwareArtifactStore(rootDirectory: root)
  }

  override func tearDownWithError() throws {
    try? FileManager.default.removeItem(at: root)
  }

  func testFinalAppearsOnlyAfterSizeAndDigestVerification() throws {
    let payload = Data("verified firmware".utf8)
    let metadata = try createArtifact(payload: payload)
    let staging = try store.partialFileURL(for: metadata.artifactRef)
    try payload.write(to: staging)

    let promoted = try store.promoteVerifiedStaging(
      artifactRef: metadata.artifactRef,
      maxBytes: Int64(payload.count)
    )

    XCTAssertFalse(FileManager.default.fileExists(atPath: staging.path))
    XCTAssertTrue(
      FileManager.default.fileExists(
        atPath: try store.finalFileURL(for: metadata.artifactRef).path
      )
    )
    XCTAssertEqual(promoted.actualSize, Int64(payload.count))
    XCTAssertEqual(promoted.actualSha256, metadata.expectedSha256)
    XCTAssertNotNil(promoted.immutableToken)
    XCTAssertEqual(promoted.retentionClass, .verifiedFinal)
  }

  func testDigestMismatchQuarantinesStagingWithoutExposingFinal() throws {
    let expected = Data("expected firmware".utf8)
    let metadata = try createArtifact(payload: expected)
    let staging = try store.partialFileURL(for: metadata.artifactRef)
    try Data("corrupt firmware!".utf8).write(to: staging)

    XCTAssertThrowsError(
      try store.promoteVerifiedStaging(
        artifactRef: metadata.artifactRef,
        maxBytes: Int64(expected.count)
      )
    ) { error in
      XCTAssertEqual(error as? FirmwareArtifactStoreError, .digestMismatch)
    }
    XCTAssertFalse(FileManager.default.fileExists(atPath: staging.path))
    XCTAssertFalse(
      FileManager.default.fileExists(
        atPath: try! store.finalFileURL(for: metadata.artifactRef).path
      )
    )
  }

  func testMaxBytesRejectsOversizedStagingBeforePromotion() throws {
    let payload = Data("firmware payload".utf8)
    let metadata = try createArtifact(payload: payload)
    let staging = try store.partialFileURL(for: metadata.artifactRef)
    try payload.write(to: staging)

    XCTAssertThrowsError(
      try store.promoteVerifiedStaging(
        artifactRef: metadata.artifactRef,
        maxBytes: Int64(payload.count - 1)
      )
    ) { error in
      XCTAssertEqual(error as? FirmwareArtifactStoreError, .invalidMetadata)
    }
    XCTAssertTrue(FileManager.default.fileExists(atPath: staging.path))
    XCTAssertFalse(
      FileManager.default.fileExists(
        atPath: try! store.finalFileURL(for: metadata.artifactRef).path
      )
    )
  }

  private func createArtifact(
    payload: Data
  ) throws -> StoredFirmwareArtifactMetadata {
    let digest = RangeDownloadLogic.calculateSHA256(writeDigestFixture(payload))!
    return try store.createArtifact(
      artifactId: "firmware-main",
      canonicalURL: "https://downloads.example.com/firmware.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: Int64(payload.count),
      expectedSha256: digest,
      retentionClass: .staging
    )
  }

  private func writeDigestFixture(_ payload: Data) -> String {
    let file = root.appendingPathComponent("digest-\(UUID().uuidString)")
    try! FileManager.default.createDirectory(
      at: root,
      withIntermediateDirectories: true
    )
    try! payload.write(to: file)
    addTeardownBlock {
      try? FileManager.default.removeItem(at: file)
    }
    return file.path
  }
}
