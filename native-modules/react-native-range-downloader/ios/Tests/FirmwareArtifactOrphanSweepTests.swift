import Foundation
import XCTest
@testable import RangeDownloadLogic

final class FirmwareArtifactOrphanSweepTests: XCTestCase {
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

  func testSweepRequiresJournalReconciliationFirst() throws {
    _ = try makeArtifact(now: 0)

    XCTAssertThrowsError(
      try store.sweepOrphans(now: FirmwareArtifactStore.RetentionPolicy.partialTTL + 1)
    ) { error in
      XCTAssertEqual(error as? FirmwareArtifactStoreError, .reconciliationRequired)
    }
  }

  func testRetentionBoundaryDeletesAtTtlButNotBefore() throws {
    let artifact = try makeArtifact(now: 0)
    let finalURL = try store.finalFileURL(for: artifact.artifactRef)
    try Data(repeating: 0xA5, count: 32).write(to: finalURL)
    try store.reconcileLeases(activeLeaseRefs: [], now: 1)

    let beforeBoundary = try store.sweepOrphans(
      now: FirmwareArtifactStore.RetentionPolicy.partialTTL - 1
    )
    XCTAssertEqual(beforeBoundary, FirmwareArtifactSweepSummary(deletedFiles: 0, deletedBytes: 0))
    XCTAssertNoThrow(try store.loadArtifact(artifact.artifactRef))

    let atBoundary = try store.sweepOrphans(
      now: FirmwareArtifactStore.RetentionPolicy.partialTTL
    )
    XCTAssertGreaterThanOrEqual(atBoundary.deletedFiles, 2)
    XCTAssertGreaterThan(atBoundary.deletedBytes, 0)
    XCTAssertThrowsError(try store.loadArtifact(artifact.artifactRef))
  }

  func testActiveLeaseSurvivesTtlAndZeroByteBudget() throws {
    let artifact = try makeArtifact(now: 0)
    let lease = try store.createLease(transactionId: "transaction-1", now: 0)
    try store.retainArtifact(
      leaseRef: lease.leaseRef,
      artifactRef: artifact.artifactRef,
      now: 0
    )
    try store.reconcileLeases(activeLeaseRefs: [lease.leaseRef], now: 1)

    let result = try store.sweepOrphans(
      now: FirmwareArtifactStore.RetentionPolicy.partialTTL * 2,
      byteBudget: 0
    )
    XCTAssertEqual(result, FirmwareArtifactSweepSummary(deletedFiles: 0, deletedBytes: 0))
    XCTAssertNoThrow(try store.loadArtifact(artifact.artifactRef))
  }

  func testMetadataLessOrphanIsDeletedOnlyAfterGracePeriod() throws {
    _ = try makeArtifact(now: 0)
    let orphanRef = UUID().uuidString.lowercased()
    let orphanDirectory = rootDirectory
      .appendingPathComponent("artifacts", isDirectory: true)
      .appendingPathComponent(orphanRef, isDirectory: true)
    try FileManager.default.createDirectory(
      at: orphanDirectory,
      withIntermediateDirectories: true
    )
    let orphanFile = orphanDirectory.appendingPathComponent("payload.partial")
    try Data(repeating: 0x5A, count: 64).write(to: orphanFile)
    try FileManager.default.setAttributes(
      [.modificationDate: Date(timeIntervalSince1970: 0)],
      ofItemAtPath: orphanDirectory.path
    )
    try store.reconcileLeases(activeLeaseRefs: [], now: 1)

    let result = try store.sweepOrphans(
      now: FirmwareArtifactStore.RetentionPolicy.gracePeriod
    )
    XCTAssertGreaterThanOrEqual(result.deletedFiles, 1)
    XCTAssertFalse(FileManager.default.fileExists(atPath: orphanDirectory.path))
  }

  private func makeArtifact(now: TimeInterval) throws -> StoredFirmwareArtifactMetadata {
    return try store.createArtifact(
      artifactId: "firmware-main",
      canonicalURL: "https://downloads.example.com/firmware.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: 1024,
      expectedSha256: String(repeating: "b", count: 64),
      retentionClass: .partial,
      now: now
    )
  }
}
