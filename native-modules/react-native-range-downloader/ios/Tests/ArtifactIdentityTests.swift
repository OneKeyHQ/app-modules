import Foundation
import XCTest
@testable import RangeDownloadLogic

final class ArtifactIdentityTests: XCTestCase {
  private var root: URL!
  private var store: FirmwareArtifactStore!

  override func setUpWithError() throws {
    root = FileManager.default.temporaryDirectory.appendingPathComponent(
      "firmware-identity-\(UUID().uuidString)",
      isDirectory: true
    )
    store = FirmwareArtifactStore(rootDirectory: root)
  }

  override func tearDownWithError() throws {
    try? FileManager.default.removeItem(at: root)
  }

  func testChangedStrongValidatorClearsTheClaimedArtifact() throws {
    let lease = try store.createLease(transactionId: "transaction")
    let claimed = try store.beginTransferAttempt(
      leaseRef: lease.leaseRef,
      taskId: "task",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/main.bin",
      hostname: "downloads.example.com",
      route: .pinnedIp,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      initialDeadlineAt: 10_000,
      maxRunAttempts: 8,
      now: 1
    )
    let first = try prepareFirmwareArtifactForTest(
      store: store,
      artifactRef: claimed.artifactRef,
      leaseRef: lease.leaseRef,
      taskId: "task",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/main.bin",
      hostname: "downloads.example.com",
      route: .pinnedIp,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      strongETag: "\"v1\"",
      ranges: [(0, 1023)]
    )
    try Data(repeating: 1, count: 128).write(
      to: store.segmentFileURL(for: first.artifactRef, index: 0)
    )

    let changed = try prepareFirmwareArtifactForTest(
      store: store,
      artifactRef: claimed.artifactRef,
      leaseRef: lease.leaseRef,
      taskId: "task",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/main.bin",
      hostname: "downloads.example.com",
      route: .pinnedIp,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      strongETag: "\"v2\"",
      ranges: [(0, 1023)]
    )

    XCTAssertEqual(first.artifactRef, changed.artifactRef)
    XCTAssertNotEqual(first.transferGeneration, changed.transferGeneration)
    XCTAssertEqual(
      try store.segmentFileURL(
        for: changed.artifactRef,
        index: 0
      ).fileSize,
      0
    )
  }

  func testChangedRangeLayoutRotatesGenerationAndRejectsOldDescriptor() throws {
    let lease = try store.createLease(transactionId: "transaction")
    let claimed = try store.beginTransferAttempt(
      leaseRef: lease.leaseRef,
      taskId: "task",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/main.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      initialDeadlineAt: 10_000,
      maxRunAttempts: 8,
      now: 1
    )
    let first = try prepareFirmwareArtifactForTest(
      store: store,
      artifactRef: claimed.artifactRef,
      leaseRef: lease.leaseRef,
      taskId: "task",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/main.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      strongETag: "\"v1\"",
      ranges: [(0, 511), (512, 1023)]
    )
    let oldDescriptor = makeDescriptor(
      metadata: first,
      leaseRef: lease.leaseRef,
      taskId: "task",
      segmentIndex: 0,
      rangeStart: 0,
      rangeEnd: 511,
      maxBytes: 512
    )
    XCTAssertTrue(store.isCurrentBackgroundTransfer(
      oldDescriptor,
      validateSegmentOffset: true
    ))

    let changed = try prepareFirmwareArtifactForTest(
      store: store,
      artifactRef: claimed.artifactRef,
      leaseRef: lease.leaseRef,
      taskId: "task",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/main.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      strongETag: "\"v1\"",
      ranges: [(0, 1023)]
    )

    XCTAssertNotEqual(first.transferGeneration, changed.transferGeneration)
    XCTAssertFalse(store.isCurrentBackgroundTransfer(
      oldDescriptor,
      validateSegmentOffset: false
    ))
    XCTAssertEqual(changed.segments.map(\.start), [0])
    XCTAssertEqual(changed.segments.map(\.end), [1023])
  }

  func testReprobeRetainsSegmentsForStableIdentity() {
    XCTAssertTrue(RangeDownloadLogic.sameArtifactObjectIdentity(
      leftSupportsRange: true,
      leftTotal: 1024,
      leftStrongETag: "\"v1\"",
      leftLastModified: nil,
      rightSupportsRange: true,
      rightTotal: 1024,
      rightStrongETag: "\"v1\"",
      rightLastModified: nil
    ))
  }

  func testReprobeRejectsChangedValidatorOrTotal() {
    XCTAssertFalse(RangeDownloadLogic.sameArtifactObjectIdentity(
      leftSupportsRange: true,
      leftTotal: 1024,
      leftStrongETag: "\"v1\"",
      leftLastModified: nil,
      rightSupportsRange: true,
      rightTotal: 1024,
      rightStrongETag: "\"v2\"",
      rightLastModified: nil
    ))
    XCTAssertFalse(RangeDownloadLogic.sameArtifactObjectIdentity(
      leftSupportsRange: true,
      leftTotal: 1024,
      leftStrongETag: nil,
      leftLastModified: "Mon, 01 Jan 2024 00:00:00 GMT",
      rightSupportsRange: true,
      rightTotal: 2048,
      rightStrongETag: nil,
      rightLastModified: "Mon, 01 Jan 2024 00:00:00 GMT"
    ))
  }

  private func makeDescriptor(
    metadata: StoredFirmwareArtifactMetadata,
    leaseRef: String,
    taskId: String,
    segmentIndex: Int,
    rangeStart: Int64,
    rangeEnd: Int64,
    maxBytes: Int64
  ) -> FirmwareArtifactBackgroundTaskDescriptor {
    return FirmwareArtifactBackgroundTaskDescriptor(
      leaseRef: leaseRef,
      taskId: taskId,
      artifactRef: metadata.artifactRef,
      transferGeneration: metadata.transferGeneration,
      segmentIndex: segmentIndex,
      rangeStart: rangeStart,
      rangeEnd: rangeEnd,
      total: metadata.expectedSize,
      maxBytes: maxBytes,
      hostname: metadata.hostname,
      usesRange: metadata.transferUsesRange ?? false,
      deadlineAt: metadata.transferDeadlineAt
    )
  }
}
