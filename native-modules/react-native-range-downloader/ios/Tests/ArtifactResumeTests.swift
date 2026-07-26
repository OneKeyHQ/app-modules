import Foundation
import XCTest
@testable import RangeDownloadLogic

final class ArtifactResumeTests: XCTestCase {
  private var root: URL!
  private var store: FirmwareArtifactStore!

  override func setUpWithError() throws {
    root = FileManager.default.temporaryDirectory.appendingPathComponent(
      "firmware-resume-\(UUID().uuidString)",
      isDirectory: true
    )
    store = FirmwareArtifactStore(rootDirectory: root)
  }

  override func tearDownWithError() throws {
    try? FileManager.default.removeItem(at: root)
  }

  func testMatchingIdentityReusesPersistedSegmentLength() throws {
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
      initialDeadlineAt: 100,
      maxRunAttempts: 8,
      now: 10
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
      ranges: [(0, 1023)]
    )
    try Data(repeating: 1, count: 128).write(
      to: store.segmentFileURL(for: first.artifactRef, index: 0)
    )

    let resumed = try prepareFirmwareArtifactForTest(
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

    XCTAssertEqual(first.artifactRef, resumed.artifactRef)
    XCTAssertEqual(first.transferGeneration, resumed.transferGeneration)
    XCTAssertEqual(resumed.segments.first?.downloadedBytes, 128)
  }

  func testRestartCannotExtendDeadlineOrResetAttemptsByRotatingTaskId() throws {
    let lease = try store.createLease(transactionId: "transaction", now: 10)
    let first = try store.beginTransferAttempt(
      leaseRef: lease.leaseRef,
      taskId: "task-1",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/main.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      initialDeadlineAt: 100,
      maxRunAttempts: 2,
      now: 20
    )

    let restarted = FirmwareArtifactStore(rootDirectory: root)
    let second = try restarted.beginTransferAttempt(
      leaseRef: lease.leaseRef,
      taskId: "task-2",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/main.bin",
      hostname: "downloads.example.com",
      route: .pinnedIp,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      initialDeadlineAt: 25,
      maxRunAttempts: 2,
      now: 30
    )

    XCTAssertEqual(second.artifactRef, first.artifactRef)
    XCTAssertEqual(second.transferDeadlineAt, 100)
    XCTAssertEqual(second.transferRunAttempts, 2)
    XCTAssertThrowsError(
      try restarted.beginTransferAttempt(
        leaseRef: lease.leaseRef,
        taskId: "task-3",
        artifactId: "main",
        canonicalURL: "https://downloads.example.com/main.bin",
        hostname: "downloads.example.com",
        route: .domain,
        expectedSize: 1024,
        expectedSha256: String(repeating: "a", count: 64),
        initialDeadlineAt: 2_000,
        maxRunAttempts: 2,
        now: 40
      )
    ) { error in
      XCTAssertEqual(error as? FirmwareArtifactStoreError, .attemptLimitExceeded)
    }
  }

  func testRestartRejectsAnExpiredPersistedDeadline() throws {
    let lease = try store.createLease(transactionId: "transaction", now: 10)
    _ = try store.beginTransferAttempt(
      leaseRef: lease.leaseRef,
      taskId: "task-1",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/main.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      initialDeadlineAt: 100,
      maxRunAttempts: 8,
      now: 20
    )

    let restarted = FirmwareArtifactStore(rootDirectory: root)
    XCTAssertThrowsError(
      try restarted.beginTransferAttempt(
        leaseRef: lease.leaseRef,
        taskId: "task-2",
        artifactId: "main",
        canonicalURL: "https://downloads.example.com/main.bin",
        hostname: "downloads.example.com",
        route: .domain,
        expectedSize: 1024,
        expectedSha256: String(repeating: "a", count: 64),
        initialDeadlineAt: 1_000,
        maxRunAttempts: 8,
        now: 101
      )
    ) { error in
      XCTAssertEqual(error as? FirmwareArtifactStoreError, .deadlineExceeded)
    }
  }

  func testChangedURLAndDigestRebindTaskToNewArtifact() throws {
    let lease = try store.createLease(transactionId: "transaction", now: 1)
    let firstClaim = try store.beginTransferAttempt(
      leaseRef: lease.leaseRef,
      taskId: "task",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/v1.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      initialDeadlineAt: 100,
      maxRunAttempts: 8,
      now: 2
    )
    let first = try prepareFirmwareArtifactForTest(
      store: store,
      artifactRef: firstClaim.artifactRef,
      leaseRef: lease.leaseRef,
      taskId: "task",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/v1.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      strongETag: "\"v1\"",
      ranges: [(0, 1023)]
    )
    let oldDescriptor = makeDescriptor(
      metadata: first,
      leaseRef: lease.leaseRef,
      taskId: "task"
    )
    XCTAssertTrue(store.isCurrentBackgroundTransfer(
      oldDescriptor,
      validateSegmentOffset: true
    ))

    let secondClaim = try store.beginTransferAttempt(
      leaseRef: lease.leaseRef,
      taskId: "task",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/v2.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: 1024,
      expectedSha256: String(repeating: "b", count: 64),
      initialDeadlineAt: 200,
      maxRunAttempts: 8,
      now: 3
    )

    XCTAssertNotEqual(first.artifactRef, secondClaim.artifactRef)
    XCTAssertFalse(store.isCurrentBackgroundTransfer(
      oldDescriptor,
      validateSegmentOffset: false
    ))
    XCTAssertEqual(
      try store.supersededTransfers(
        leaseRef: lease.leaseRef,
        taskId: "task",
        keepingArtifactRef: secondClaim.artifactRef
      ).map(\.artifactRef),
      [first.artifactRef]
    )
  }

  func testExpiredOldDescriptorCannotPolluteNewGeneration() throws {
    let lease = try store.createLease(transactionId: "transaction", now: 1)
    let firstClaim = try store.beginTransferAttempt(
      leaseRef: lease.leaseRef,
      taskId: "task",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/expired.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      initialDeadlineAt: 10,
      maxRunAttempts: 8,
      now: 2
    )
    let first = try prepareFirmwareArtifactForTest(
      store: store,
      artifactRef: firstClaim.artifactRef,
      leaseRef: lease.leaseRef,
      taskId: "task",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/expired.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: 1024,
      expectedSha256: String(repeating: "a", count: 64),
      strongETag: "\"old\"",
      ranges: [(0, 1023)]
    )
    let expiredDescriptor = makeDescriptor(
      metadata: first,
      leaseRef: lease.leaseRef,
      taskId: "task"
    )

    let secondClaim = try store.beginTransferAttempt(
      leaseRef: lease.leaseRef,
      taskId: "task",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/current.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: 1024,
      expectedSha256: String(repeating: "b", count: 64),
      initialDeadlineAt: 100,
      maxRunAttempts: 8,
      now: 20
    )
    let second = try prepareFirmwareArtifactForTest(
      store: store,
      artifactRef: secondClaim.artifactRef,
      leaseRef: lease.leaseRef,
      taskId: "task",
      artifactId: "main",
      canonicalURL: "https://downloads.example.com/current.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: 1024,
      expectedSha256: String(repeating: "b", count: 64),
      strongETag: "\"new\"",
      ranges: [(0, 1023)]
    )
    let currentDescriptor = makeDescriptor(
      metadata: second,
      leaseRef: lease.leaseRef,
      taskId: "task"
    )

    XCTAssertEqual(expiredDescriptor.deadlineAt, 10)
    XCTAssertEqual(currentDescriptor.deadlineAt, 100)
    XCTAssertNotEqual(expiredDescriptor.transferKey, currentDescriptor.transferKey)
    XCTAssertFalse(store.isCurrentBackgroundTransfer(
      expiredDescriptor,
      validateSegmentOffset: false
    ))
    XCTAssertTrue(store.isCurrentBackgroundTransfer(
      currentDescriptor,
      validateSegmentOffset: true
    ))
  }

  private func makeDescriptor(
    metadata: StoredFirmwareArtifactMetadata,
    leaseRef: String,
    taskId: String
  ) -> FirmwareArtifactBackgroundTaskDescriptor {
    return FirmwareArtifactBackgroundTaskDescriptor(
      leaseRef: leaseRef,
      taskId: taskId,
      artifactRef: metadata.artifactRef,
      transferGeneration: metadata.transferGeneration,
      segmentIndex: 0,
      rangeStart: 0,
      rangeEnd: metadata.expectedSize - 1,
      total: metadata.expectedSize,
      maxBytes: metadata.expectedSize,
      hostname: metadata.hostname,
      usesRange: metadata.transferUsesRange ?? false,
      deadlineAt: metadata.transferDeadlineAt
    )
  }
}
