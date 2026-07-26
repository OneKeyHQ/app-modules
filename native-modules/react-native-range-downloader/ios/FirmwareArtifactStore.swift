import Darwin
import Foundation

final class FirmwareArtifactActivityToken {
  let artifactRef: String
  let kind: FirmwareArtifactActivityKind

  private let id: UUID
  private weak var store: FirmwareArtifactStore?
  private let releaseLock = NSLock()
  private var didRelease = false

  fileprivate init(
    id: UUID,
    artifactRef: String,
    kind: FirmwareArtifactActivityKind,
    store: FirmwareArtifactStore
  ) {
    self.id = id
    self.artifactRef = artifactRef
    self.kind = kind
    self.store = store
  }

  func release() {
    let shouldRelease = releaseLock.withFirmwareLock {
      if didRelease {
        return false
      }
      didRelease = true
      return true
    }
    if shouldRelease {
      store?.releaseActivity(id: id, artifactRef: artifactRef)
    }
  }

  deinit {
    release()
  }
}

final class FirmwareArtifactStore {
  static let shared = FirmwareArtifactStore(rootDirectory: defaultRootDirectory())

  enum RetentionPolicy {
    static let gracePeriod: TimeInterval = 60 * 60
    static let partialTTL: TimeInterval = 7 * 24 * 60 * 60
    static let stagingTTL: TimeInterval = 24 * 60 * 60
    static let verifiedFinalTTL: TimeInterval = 30 * 24 * 60 * 60
    static let archiveTTL: TimeInterval = 7 * 24 * 60 * 60
    static let materializedChildTTL: TimeInterval = 30 * 24 * 60 * 60
    static let byteBudget: Int64 = 512 * 1024 * 1024
    static let workerCancellationTimeout: TimeInterval = 5

    static func ttl(for retentionClass: FirmwareArtifactRetentionClass) -> TimeInterval {
      switch retentionClass {
      case .partial:
        return partialTTL
      case .staging:
        return stagingTTL
      case .verifiedFinal:
        return verifiedFinalTTL
      case .archive:
        return archiveTTL
      case .materializedChild:
        return materializedChildTTL
      }
    }
  }

  private struct ActiveActivity {
    let kind: FirmwareArtifactActivityKind
    let cancel: (() -> Void)?
  }

  private let rootDirectory: URL
  private let artifactsDirectory: URL
  private let leasesDirectory: URL
  private let fileManager: FileManager
  private let ioQueue = DispatchQueue(label: "com.onekey.firmware-artifact-store.io")
  private let activityCondition = NSCondition()
  private var activitiesByArtifact: [String: [UUID: ActiveActivity]] = [:]
  private var didReconcileLeases = false

  init(rootDirectory: URL, fileManager: FileManager = .default) {
    self.rootDirectory = rootDirectory
    self.artifactsDirectory = rootDirectory.appendingPathComponent("artifacts", isDirectory: true)
    self.leasesDirectory = rootDirectory.appendingPathComponent("leases", isDirectory: true)
    self.fileManager = fileManager
  }

  func createArtifact(
    artifactId: String,
    canonicalURL: String,
    hostname: String,
    route: StoredFirmwareArtifactRoute,
    expectedSize: Int64,
    expectedSha256: String,
    retentionClass: FirmwareArtifactRetentionClass,
    now: TimeInterval = Date().timeIntervalSince1970
  ) throws -> StoredFirmwareArtifactMetadata {
    return try ioQueue.sync {
      try ensureDirectories()
      guard !artifactId.isEmpty,
            !canonicalURL.isEmpty,
            !hostname.isEmpty,
            expectedSize > 0,
            StoredFirmwareArtifactMetadata.isTrustedSha256(expectedSha256) else {
        throw FirmwareArtifactStoreError.invalidMetadata
      }

      let artifactRef = UUID().uuidString.lowercased()
      let directory = try artifactDirectory(for: artifactRef)
      do {
        try fileManager.createDirectory(
          at: directory,
          withIntermediateDirectories: false
        )
        let metadata = StoredFirmwareArtifactMetadata(
          artifactRef: artifactRef,
          artifactId: artifactId,
          canonicalURL: canonicalURL,
          hostname: hostname,
          route: route,
          expectedSize: expectedSize,
          expectedSha256: expectedSha256,
          retentionClass: retentionClass,
          now: now
        )
        try writeMetadata(metadata)
        return metadata
      } catch {
        try? fileManager.removeItem(at: directory)
        throw mapStorageError(error)
      }
    }
  }

  func loadArtifact(_ artifactRef: String) throws -> StoredFirmwareArtifactMetadata {
    return try ioQueue.sync {
      try ensureDirectories()
      return try readMetadata(artifactRef)
    }
  }

  func updateArtifact(
    _ artifactRef: String,
    mutate: (inout StoredFirmwareArtifactMetadata) throws -> Void
  ) throws -> StoredFirmwareArtifactMetadata {
    return try ioQueue.sync {
      try ensureDirectories()
      var metadata = try readMetadata(artifactRef)
      try mutate(&metadata)
      guard metadata.artifactRef == artifactRef,
            metadata.schemaVersion == StoredFirmwareArtifactMetadata.currentSchemaVersion else {
        throw FirmwareArtifactStoreError.invalidMetadata
      }
      try writeMetadata(metadata)
      return metadata
    }
  }

  func beginTransferAttempt(
    leaseRef: String,
    taskId: String,
    artifactId: String,
    canonicalURL: String,
    hostname: String,
    route: StoredFirmwareArtifactRoute,
    expectedSize: Int64,
    expectedSha256: String,
    initialDeadlineAt: TimeInterval,
    maxRunAttempts: Int,
    now: TimeInterval = Date().timeIntervalSince1970
  ) throws -> StoredFirmwareArtifactMetadata {
    return try ioQueue.sync {
      try ensureDirectories()
      var lease = try readLease(leaseRef)
      guard lease.isActive else {
        throw FirmwareArtifactStoreError.leaseReleased
      }
      guard maxRunAttempts > 0,
            initialDeadlineAt.isFinite,
            initialDeadlineAt > 0 else {
        throw FirmwareArtifactStoreError.deadlineExceeded
      }
      let taskIdHash = StoredFirmwareArtifactMetadata.sha256(taskId)
      var metadata = try loadAllMetadata()
        .filter {
          $0.matchesTransferIdentity(
            artifactId: artifactId,
            canonicalURL: canonicalURL,
            hostname: hostname,
            expectedSize: expectedSize,
            expectedSha256: expectedSha256
          )
        }
        .max { $0.updatedAt < $1.updatedAt }

      if metadata == nil {
        guard !artifactId.isEmpty,
              !canonicalURL.isEmpty,
              !hostname.isEmpty,
              expectedSize > 0,
              StoredFirmwareArtifactMetadata.isTrustedSha256(expectedSha256) else {
          throw FirmwareArtifactStoreError.invalidMetadata
        }
        let artifactRef = UUID().uuidString.lowercased()
        let directory = try artifactDirectory(for: artifactRef)
        try fileManager.createDirectory(
          at: directory,
          withIntermediateDirectories: false
        )
        metadata = StoredFirmwareArtifactMetadata(
          artifactRef: artifactRef,
          artifactId: artifactId,
          canonicalURL: canonicalURL,
          hostname: hostname,
          route: route,
          expectedSize: expectedSize,
          expectedSha256: expectedSha256,
          retentionClass: .partial,
          now: now
        )
      }

      var claimed = metadata!
      guard claimed.transferDeadlineAt != nil ||
              claimed.transferRunAttempts == 0 else {
        throw FirmwareArtifactStoreError.invalidMetadata
      }
      let deadlineAt = claimed.transferDeadlineAt ?? initialDeadlineAt
      guard deadlineAt > now else {
        throw FirmwareArtifactStoreError.deadlineExceeded
      }
      guard claimed.transferRunAttempts < maxRunAttempts else {
        throw FirmwareArtifactStoreError.attemptLimitExceeded
      }
      let runAttempts = claimed.transferRunAttempts + 1

      claimed.taskIdHash = taskIdHash
      claimed.transferDeadlineAt = deadlineAt
      claimed.transferRunAttempts = runAttempts
      claimed.retentionClass = .partial
      claimed.updatedAt = now
      var leaseChanged = false
      if !lease.artifactRefs.contains(claimed.artifactRef) {
        lease.artifactRefs.append(claimed.artifactRef)
        lease.artifactRefs.sort()
        leaseChanged = true
      }
      let bindingsWithoutPreviousTask = lease
        .activeTransferArtifactRefsByTaskHash
        .filter {
          $0.key == taskIdHash || $0.value != claimed.artifactRef
        }
      if bindingsWithoutPreviousTask !=
          lease.activeTransferArtifactRefsByTaskHash {
        lease.activeTransferArtifactRefsByTaskHash = bindingsWithoutPreviousTask
        leaseChanged = true
      }
      if lease.activeTransferArtifactRefsByTaskHash[taskIdHash] !=
          claimed.artifactRef {
        lease.activeTransferArtifactRefsByTaskHash[taskIdHash] =
          claimed.artifactRef
        leaseChanged = true
      }
      if leaseChanged {
        lease.updatedAt = now
        try writeLease(lease)
      }
      if !claimed.leaseRefs.contains(leaseRef) {
        claimed.leaseRefs.append(leaseRef)
        claimed.leaseRefs.sort()
      }
      try writeMetadata(claimed)
      return claimed
    }
  }

  func planTransferPreparation(
    artifactRef: String,
    leaseRef: String,
    taskId: String,
    artifactId: String,
    canonicalURL: String,
    hostname: String,
    route: StoredFirmwareArtifactRoute,
    expectedSize: Int64,
    expectedSha256: String,
    strongETag: String?,
    lastModified: String?,
    ranges: [(start: Int64, end: Int64)],
    usesRange: Bool
  ) throws -> FirmwareArtifactTransferPreparation {
    return try ioQueue.sync {
      try ensureDirectories()
      guard StoredFirmwareArtifactMetadata.hasValidTransferRanges(
        ranges,
        total: expectedSize
      ) else {
        throw FirmwareArtifactStoreError.invalidMetadata
      }
      let lease = try readLease(leaseRef)
      let metadata = try readMetadata(artifactRef)
      try validateTransferClaim(
        metadata: metadata,
        lease: lease,
        leaseRef: leaseRef,
        taskId: taskId,
        artifactId: artifactId,
        canonicalURL: canonicalURL,
        hostname: hostname,
        expectedSize: expectedSize,
        expectedSha256: expectedSha256
      )
      let canReuseBytes = metadata.canReuseTransferBytes(
        canonicalURL: canonicalURL,
        hostname: hostname,
        route: route,
        expectedSize: expectedSize,
        expectedSha256: expectedSha256,
        strongETag: strongETag,
        lastModified: lastModified,
        ranges: ranges
      )
      let canReuseGeneration = canReuseBytes &&
        metadata.route == route &&
        metadata.transferUsesRange == usesRange &&
        metadata.transferGeneration != nil
      return FirmwareArtifactTransferPreparation(
        artifactRef: artifactRef,
        currentGeneration: metadata.transferGeneration,
        nextGeneration: canReuseGeneration
          ? metadata.transferGeneration!
          : UUID().uuidString.lowercased(),
        currentRoute: metadata.route,
        requiresGenerationReplacement: !canReuseGeneration,
        discardExistingSegments: !canReuseBytes
      )
    }
  }

  func prepareArtifactForDownload(
    preparation: FirmwareArtifactTransferPreparation,
    artifactRef: String,
    leaseRef: String,
    taskId: String,
    artifactId: String,
    canonicalURL: String,
    hostname: String,
    route: StoredFirmwareArtifactRoute,
    expectedSize: Int64,
    expectedSha256: String,
    strongETag: String?,
    lastModified: String?,
    ranges: [(start: Int64, end: Int64)],
    usesRange: Bool,
    now: TimeInterval = Date().timeIntervalSince1970
  ) throws -> StoredFirmwareArtifactMetadata {
    return try ioQueue.sync {
      try ensureDirectories()
      guard StoredFirmwareArtifactMetadata.hasValidTransferRanges(
        ranges,
        total: expectedSize
      ) else {
        throw FirmwareArtifactStoreError.invalidMetadata
      }
      let lease = try readLease(leaseRef)
      var prepared = try readMetadata(artifactRef)
      try validateTransferClaim(
        metadata: prepared,
        lease: lease,
        leaseRef: leaseRef,
        taskId: taskId,
        artifactId: artifactId,
        canonicalURL: canonicalURL,
        hostname: hostname,
        expectedSize: expectedSize,
        expectedSha256: expectedSha256
      )
      let canReuseBytes = prepared.canReuseTransferBytes(
        canonicalURL: canonicalURL,
        hostname: hostname,
        route: route,
        expectedSize: expectedSize,
        expectedSha256: expectedSha256,
        strongETag: strongETag,
        lastModified: lastModified,
        ranges: ranges
      )
      let canReuseGeneration = canReuseBytes &&
        prepared.route == route &&
        prepared.transferUsesRange == usesRange &&
        prepared.transferGeneration != nil
      guard preparation.artifactRef == artifactRef,
            preparation.currentGeneration == prepared.transferGeneration,
            preparation.currentRoute == prepared.route,
            preparation.requiresGenerationReplacement == !canReuseGeneration,
            preparation.discardExistingSegments == !canReuseBytes,
            StoredFirmwareArtifactMetadata.isValidTransferGeneration(
              preparation.nextGeneration
            ),
            canReuseGeneration
              ? preparation.nextGeneration == prepared.transferGeneration
              : preparation.nextGeneration != prepared.transferGeneration else {
        throw FirmwareArtifactStoreError.invalidMetadata
      }
      if preparation.discardExistingSegments {
        try? fileManager.removeItem(at: partialFileURL(for: artifactRef))
        for segment in prepared.segments {
          try? fileManager.removeItem(
            at: segmentFileURL(for: artifactRef, index: segment.index)
          )
        }
        prepared.segments = []
      }
      prepared.route = route
      prepared.transferGeneration = preparation.nextGeneration
      prepared.transferUsesRange = usesRange
      prepared.strongETag = StoredFirmwareArtifactMetadata.normalizeStrongETag(
        strongETag
      )
      prepared.lastModified = lastModified?
        .trimmingCharacters(in: .whitespacesAndNewlines)
        .nilIfEmpty
      prepared.segments = try ranges.enumerated().map { index, range in
        let onDisk = try segmentFileURL(
          for: prepared.artifactRef,
          index: index
        ).fileSize
        return FirmwareArtifactSegmentMetadata(
          index: index,
          start: range.start,
          end: range.end,
          downloadedBytes: min(onDisk, range.end - range.start + 1)
        )
      }
      prepared.retentionClass = .partial
      prepared.updatedAt = now

      try writeMetadata(prepared)
      return prepared
    }
  }

  func supersededTransfers(
    leaseRef: String,
    taskId: String,
    keepingArtifactRef: String
  ) throws -> [StoredFirmwareArtifactMetadata] {
    return try ioQueue.sync {
      try ensureDirectories()
      let lease = try readLease(leaseRef)
      let taskIdHash = StoredFirmwareArtifactMetadata.sha256(taskId)
      guard lease.isActive,
            lease.activeTransferArtifactRefsByTaskHash[taskIdHash] ==
              keepingArtifactRef else {
        throw FirmwareArtifactStoreError.invalidMetadata
      }
      return lease.artifactRefs
        .filter { $0 != keepingArtifactRef }
        .compactMap { try? readMetadata($0) }
        .filter {
          $0.taskIdHash == taskIdHash &&
            $0.tombstonedAt == nil
        }
    }
  }

  func isCurrentBackgroundTransfer(
    _ descriptor: FirmwareArtifactBackgroundTaskDescriptor,
    validateSegmentOffset: Bool
  ) -> Bool {
    return (try? ioQueue.sync {
      try ensureDirectories()
      guard let generation = descriptor.transferGeneration,
            StoredFirmwareArtifactMetadata.isValidTransferGeneration(generation)
      else {
        return false
      }
      let lease = try readLease(descriptor.leaseRef)
      let taskIdHash = StoredFirmwareArtifactMetadata.sha256(descriptor.taskId)
      guard lease.isActive,
            lease.activeTransferArtifactRefsByTaskHash[taskIdHash] ==
              descriptor.artifactRef,
            lease.artifactRefs.contains(descriptor.artifactRef) else {
        return false
      }
      let metadata = try readMetadata(descriptor.artifactRef)
      guard metadata.taskIdHash == taskIdHash,
            metadata.leaseRefs.contains(descriptor.leaseRef),
            metadata.transferGeneration == generation,
            metadata.transferUsesRange == descriptor.usesRange,
            metadata.transferDeadlineAt == descriptor.deadlineAt,
            metadata.hostname == descriptor.hostname.lowercased(),
            metadata.expectedSize == descriptor.total,
            descriptor.segmentIndex >= 0,
            descriptor.total > 0,
            descriptor.maxBytes > 0,
            let segment = metadata.segments.first(
              where: { $0.index == descriptor.segmentIndex }
            ),
            segment.start >= 0,
            segment.end >= segment.start,
            segment.end < metadata.expectedSize,
            descriptor.rangeStart >= segment.start,
            descriptor.rangeStart <= descriptor.rangeEnd,
            descriptor.rangeEnd == segment.end,
            descriptor.rangeEnd < descriptor.total,
            descriptor.maxBytes == segment.end - segment.start + 1 else {
        return false
      }
      guard validateSegmentOffset else {
        return true
      }
      let onDisk = try segmentFileURL(
        for: descriptor.artifactRef,
        index: descriptor.segmentIndex
      ).fileSize
      return descriptor.rangeStart == segment.start + onDisk
    }) ?? false
  }

  func findArtifactForTask(
    leaseRef: String,
    taskId: String
  ) throws -> StoredFirmwareArtifactMetadata? {
    return try ioQueue.sync {
      try ensureDirectories()
      let lease = try readLease(leaseRef)
      let taskIdHash = StoredFirmwareArtifactMetadata.sha256(taskId)
      if let artifactRef =
          lease.activeTransferArtifactRefsByTaskHash[taskIdHash],
         let active = try? readMetadata(artifactRef),
         active.taskIdHash == taskIdHash,
         active.tombstonedAt == nil {
        return active
      }
      return lease.artifactRefs
        .compactMap { try? readMetadata($0) }
        .filter { $0.taskIdHash == taskIdHash && $0.tombstonedAt == nil }
        .max { $0.updatedAt < $1.updatedAt }
    }
  }

  func clearTransferStaging(
    artifactRef: String,
    now: TimeInterval = Date().timeIntervalSince1970
  ) throws {
    try ioQueue.sync {
      try ensureDirectories()
      var metadata = try readMetadata(artifactRef)
      try? fileManager.removeItem(at: partialFileURL(for: artifactRef))
      for segment in metadata.segments {
        try? fileManager.removeItem(
          at: segmentFileURL(for: artifactRef, index: segment.index)
        )
      }
      metadata.segments = []
      metadata.retentionClass = .staging
      metadata.updatedAt = now
      try writeMetadata(metadata)
    }
  }

  func completeTransferCleanup(
    artifactRef: String,
    now: TimeInterval = Date().timeIntervalSince1970
  ) throws {
    try ioQueue.sync {
      try ensureDirectories()
      var metadata = try readMetadata(artifactRef)
      for segment in metadata.segments {
        try? fileManager.removeItem(
          at: segmentFileURL(for: artifactRef, index: segment.index)
        )
      }
      metadata.segments = []
      metadata.updatedAt = now
      try writeMetadata(metadata)
    }
  }

  func downloadedBytes(_ artifactRef: String) throws -> Int64 {
    return try ioQueue.sync {
      let metadata = try readMetadata(artifactRef)
      let finalURL = try finalFileURL(for: artifactRef)
      if fileManager.fileExists(atPath: finalURL.path),
         let actualSize = metadata.actualSize {
        return actualSize
      }
      return try metadata.segments.reduce(Int64(0)) { total, segment in
        let size = try segmentFileURL(
          for: artifactRef,
          index: segment.index
        ).fileSize
        return total + min(size, segment.end - segment.start + 1)
      }
    }
  }

  func createLease(
    transactionId: String,
    now: TimeInterval = Date().timeIntervalSince1970
  ) throws -> StoredFirmwareArtifactLeaseMetadata {
    return try ioQueue.sync {
      try ensureDirectories()
      guard !transactionId.isEmpty,
            transactionId.lengthOfBytes(using: .utf8) <= 256 else {
        throw FirmwareArtifactStoreError.invalidMetadata
      }
      let leaseRef = UUID().uuidString.lowercased()
      let lease = StoredFirmwareArtifactLeaseMetadata(
        leaseRef: leaseRef,
        transactionId: transactionId,
        now: now
      )
      try writeLease(lease)
      return lease
    }
  }

  func retainArtifact(
    leaseRef: String,
    artifactRef: String,
    now: TimeInterval = Date().timeIntervalSince1970
  ) throws {
    try ioQueue.sync {
      try ensureDirectories()
      var lease = try readLease(leaseRef)
      guard lease.isActive else {
        throw FirmwareArtifactStoreError.leaseReleased
      }
      var artifact = try readMetadata(artifactRef)
      guard artifact.tombstonedAt == nil else {
        throw FirmwareArtifactStoreError.artifactNotFound
      }

      if !lease.artifactRefs.contains(artifactRef) {
        lease.artifactRefs.append(artifactRef)
        lease.artifactRefs.sort()
        lease.updatedAt = now
        try writeLease(lease)
      }
      if !artifact.leaseRefs.contains(leaseRef) {
        artifact.leaseRefs.append(leaseRef)
        artifact.leaseRefs.sort()
        artifact.updatedAt = now
        try writeMetadata(artifact)
      }
    }
  }

  func releaseLease(
    leaseRef: String,
    disposition: StoredFirmwareLeaseDisposition,
    now: TimeInterval = Date().timeIntervalSince1970
  ) throws {
    try ioQueue.sync {
      try ensureDirectories()
      var lease = try readLease(leaseRef)
      if !lease.isActive {
        return
      }

      lease.releasedAt = now
      lease.disposition = disposition
      lease.updatedAt = now
      try writeLease(lease)

      for artifactRef in lease.artifactRefs {
        guard var artifact = try? readMetadata(artifactRef) else {
          continue
        }
        artifact.leaseRefs.removeAll { $0 == leaseRef }
        artifact.updatedAt = now
        try writeMetadata(artifact)
      }
    }
  }

  func reconcileLeases(
    activeLeaseRefs: [String],
    now: TimeInterval = Date().timeIntervalSince1970
  ) throws {
    try ioQueue.sync {
      try ensureDirectories()
      let normalizedActiveRefs = try Set(activeLeaseRefs.map(validateReference))
      let leases = try loadAllLeases()

      for var lease in leases where lease.isActive && !normalizedActiveRefs.contains(lease.leaseRef) {
        lease.releasedAt = now
        lease.disposition = .safeAbandoned
        lease.updatedAt = now
        try writeLease(lease)
        for artifactRef in lease.artifactRefs {
          guard var artifact = try? readMetadata(artifactRef) else {
            continue
          }
          artifact.leaseRefs.removeAll { $0 == lease.leaseRef }
          artifact.updatedAt = now
          try writeMetadata(artifact)
        }
      }

      let knownRefs = Set(leases.map(\.leaseRef))
      for missingRef in normalizedActiveRefs.subtracting(knownRefs) {
        var recovered = StoredFirmwareArtifactLeaseMetadata(
          leaseRef: missingRef,
          transactionId: "reconciled:\(missingRef)",
          now: now
        )
        recovered.transactionIdHash = StoredFirmwareArtifactMetadata.sha256("reconciled")
        try writeLease(recovered)
      }
      didReconcileLeases = true
    }
  }

  func beginActivity(
    artifactRef: String,
    kind: FirmwareArtifactActivityKind,
    cancel: (() -> Void)? = nil
  ) throws -> FirmwareArtifactActivityToken {
    let normalizedRef = try validateReference(artifactRef)
    let id = UUID()
    activityCondition.lock()
    activitiesByArtifact[normalizedRef, default: [:]][id] = ActiveActivity(
      kind: kind,
      cancel: cancel
    )
    activityCondition.unlock()
    let token = FirmwareArtifactActivityToken(
      id: id,
      artifactRef: normalizedRef,
      kind: kind,
      store: self
    )
    do {
      _ = try loadArtifact(normalizedRef)
      return token
    } catch {
      token.release()
      throw error
    }
  }

  func discardArtifact(
    _ artifactRef: String,
    now: TimeInterval = Date().timeIntervalSince1970
  ) throws {
    try ioQueue.sync {
      try ensureDirectories()
      var metadata = try readMetadata(artifactRef)
      if try isArtifactLeased(artifactRef) || hasAnyActivity(artifactRef) {
        throw FirmwareArtifactStoreError.artifactLeased
      }
      metadata.tombstonedAt = now
      metadata.updatedAt = now
      try writeMetadata(metadata)
      try removeArtifactDirectory(metadata.artifactRef)
    }
  }

  func quarantineArtifact(
    _ artifactRef: String,
    now: TimeInterval = Date().timeIntervalSince1970
  ) throws {
    try ioQueue.sync {
      try ensureDirectories()
      let normalizedRef = try validateReference(artifactRef)
      var metadata = try readMetadata(normalizedRef)
      if hasAnyActivity(normalizedRef) {
        throw FirmwareArtifactStoreError.artifactLeased
      }

      metadata.tombstonedAt = now
      metadata.updatedAt = now
      try writeMetadata(metadata)

      for leaseRef in metadata.leaseRefs {
        guard var lease = try? readLease(leaseRef) else {
          continue
        }
        lease.artifactRefs.removeAll { $0 == normalizedRef }
        lease.activeTransferArtifactRefsByTaskHash =
          lease.activeTransferArtifactRefsByTaskHash.filter {
            $0.value != normalizedRef
          }
        lease.updatedAt = now
        try writeLease(lease)
      }
      metadata.leaseRefs = []
      try writeMetadata(metadata)
      try removeArtifactDirectory(normalizedRef)
    }
  }

  func sweepOrphans(
    now: TimeInterval = Date().timeIntervalSince1970,
    byteBudget: Int64 = RetentionPolicy.byteBudget
  ) throws -> FirmwareArtifactSweepSummary {
    return try ioQueue.sync {
      try ensureDirectories()
      guard didReconcileLeases else {
        throw FirmwareArtifactStoreError.reconciliationRequired
      }

      let activeLeaseRefs = try loadActiveLeaseRefs()
      let protectedRefs = try loadAllLeases()
        .filter { activeLeaseRefs.contains($0.leaseRef) }
        .reduce(into: Set<String>()) { result, lease in
          result.formUnion(lease.artifactRefs)
        }
      var metadataByRef = Dictionary(
        uniqueKeysWithValues: try loadAllMetadata().map { ($0.artifactRef, $0) }
      )
      var storedBytesByRef: [String: Int64] = [:]
      for metadata in metadataByRef.values {
        storedBytesByRef[metadata.artifactRef] = storedBytes(for: metadata.artifactRef).bytes
      }

      var candidateRefs: [String] = []
      for metadata in metadataByRef.values {
        guard !protectedRefs.contains(metadata.artifactRef) else {
          continue
        }
        if metadata.tombstonedAt != nil {
          candidateRefs.append(metadata.artifactRef)
          continue
        }
        let age = max(0, now - max(metadata.updatedAt, metadata.lastAccessedAt))
        if age >= RetentionPolicy.gracePeriod,
           age >= RetentionPolicy.ttl(for: metadata.retentionClass) {
          candidateRefs.append(metadata.artifactRef)
        }
      }

      var projectedBytes = storedBytesByRef.values.reduce(0, +)
      for artifactRef in candidateRefs {
        projectedBytes -= storedBytesByRef[artifactRef] ?? 0
      }
      let budgetCandidates = metadataByRef.values
        .filter { metadata in
          guard !candidateRefs.contains(metadata.artifactRef),
                !protectedRefs.contains(metadata.artifactRef) else {
            return false
          }
          let age = max(0, now - max(metadata.updatedAt, metadata.lastAccessedAt))
          return age >= RetentionPolicy.gracePeriod
        }
        .sorted {
          max($0.updatedAt, $0.lastAccessedAt) < max($1.updatedAt, $1.lastAccessedAt)
        }
      for metadata in budgetCandidates where projectedBytes > byteBudget {
        candidateRefs.append(metadata.artifactRef)
        projectedBytes -= storedBytesByRef[metadata.artifactRef] ?? 0
      }

      var summary = FirmwareArtifactSweepSummary(deletedFiles: 0, deletedBytes: 0)
      for artifactRef in candidateRefs {
        guard !protectedRefs.contains(artifactRef),
              prepareForDeletion(artifactRef) else {
          continue
        }
        if try isArtifactLeased(artifactRef) {
          continue
        }
        guard var metadata = metadataByRef[artifactRef] else {
          continue
        }
        metadata.tombstonedAt = metadata.tombstonedAt ?? now
        metadata.updatedAt = now
        try writeMetadata(metadata)
        let stored = storedBytes(for: artifactRef)
        do {
          try removeArtifactDirectory(artifactRef)
          summary.deletedFiles += stored.files
          summary.deletedBytes += stored.bytes
          metadataByRef.removeValue(forKey: artifactRef)
        } catch {
          continue
        }
      }

      let knownRefs = Set(metadataByRef.keys)
      let orphanDirectories = try fileManager.contentsOfDirectory(
        at: artifactsDirectory,
        includingPropertiesForKeys: [.contentModificationDateKey],
        options: [.skipsHiddenFiles]
      )
      for directory in orphanDirectories {
        let artifactRef = directory.lastPathComponent.lowercased()
        guard !knownRefs.contains(artifactRef),
              !protectedRefs.contains(artifactRef),
              (try? validateReference(artifactRef)) != nil,
              prepareForDeletion(artifactRef) else {
          continue
        }
        let values = try? directory.resourceValues(forKeys: [.contentModificationDateKey])
        let modifiedAt = values?.contentModificationDate?.timeIntervalSince1970 ?? now
        guard now - modifiedAt >= RetentionPolicy.gracePeriod else {
          continue
        }
        let stored = storedBytes(for: artifactRef)
        do {
          try removeArtifactDirectory(artifactRef)
          summary.deletedFiles += stored.files
          summary.deletedBytes += stored.bytes
        } catch {
          continue
        }
      }

      return summary
    }
  }

  func finalFileURL(for artifactRef: String) throws -> URL {
    return try artifactDirectory(for: artifactRef)
      .appendingPathComponent("payload.final", isDirectory: false)
  }

  func partialFileURL(for artifactRef: String) throws -> URL {
    return try artifactDirectory(for: artifactRef)
      .appendingPathComponent("payload.partial", isDirectory: false)
  }

  func segmentFileURL(for artifactRef: String, index: Int) throws -> URL {
    guard index >= 0 else {
      throw FirmwareArtifactStoreError.invalidReference
    }
    let directory = try artifactDirectory(for: artifactRef)
      .appendingPathComponent("segments", isDirectory: true)
    try fileManager.createDirectory(at: directory, withIntermediateDirectories: true)
    return directory.appendingPathComponent("segment-\(index).partial", isDirectory: false)
  }

  func promoteVerifiedStaging(
    artifactRef: String,
    maxBytes: Int64,
    now: TimeInterval = Date().timeIntervalSince1970
  ) throws -> StoredFirmwareArtifactMetadata {
    return try ioQueue.sync {
      try ensureDirectories()
      var metadata = try readMetadata(artifactRef)
      guard maxBytes >= metadata.expectedSize else {
        throw FirmwareArtifactStoreError.invalidMetadata
      }
      let partialURL = try partialFileURL(for: artifactRef)
      guard fileManager.fileExists(atPath: partialURL.path),
            let attributes = try? fileManager.attributesOfItem(atPath: partialURL.path),
            let fileSize = attributes[.size] as? NSNumber else {
        throw FirmwareArtifactStoreError.storageFailure
      }
      let actualSize = fileSize.int64Value
      if actualSize > maxBytes {
        try quarantineStaging(partialURL, artifactRef: artifactRef)
        throw FirmwareArtifactStoreError.sizeLimitExceeded
      }
      guard actualSize == metadata.expectedSize else {
        try quarantineStaging(partialURL, artifactRef: artifactRef)
        throw FirmwareArtifactStoreError.sizeMismatch
      }
      guard let actualSha256 = RangeDownloadLogic.calculateSHA256(partialURL.path),
            RangeDownloadLogic.secureHexEquals(
              actualSha256,
              metadata.expectedSha256
            ) else {
        try quarantineStaging(partialURL, artifactRef: artifactRef)
        throw FirmwareArtifactStoreError.digestMismatch
      }

      guard let partialHandle = FileHandle(forWritingAtPath: partialURL.path) else {
        throw FirmwareArtifactStoreError.storageFailure
      }
      do {
        try partialHandle.synchronize()
        try partialHandle.close()
      } catch {
        try? partialHandle.close()
        throw FirmwareArtifactStoreError.storageFailure
      }

      let finalURL = try finalFileURL(for: artifactRef)
      do {
        if fileManager.fileExists(atPath: finalURL.path) {
          _ = try fileManager.replaceItemAt(
            finalURL,
            withItemAt: partialURL,
            backupItemName: nil,
            options: []
          )
        } else {
          try fileManager.moveItem(at: partialURL, to: finalURL)
        }
        try synchronizeDirectory(finalURL.deletingLastPathComponent())
      } catch {
        throw mapStorageError(error)
      }

      metadata.actualSize = actualSize
      metadata.actualSha256 = actualSha256.lowercased()
      metadata.immutableToken = UUID().uuidString.lowercased()
      metadata.retentionClass = .verifiedFinal
      metadata.segments = []
      metadata.updatedAt = now
      metadata.lastAccessedAt = now
      try writeMetadata(metadata)
      return metadata
    }
  }

  fileprivate func releaseActivity(id: UUID, artifactRef: String) {
    activityCondition.lock()
    activitiesByArtifact[artifactRef]?.removeValue(forKey: id)
    if activitiesByArtifact[artifactRef]?.isEmpty == true {
      activitiesByArtifact.removeValue(forKey: artifactRef)
    }
    activityCondition.broadcast()
    activityCondition.unlock()
  }

  private static func defaultRootDirectory() -> URL {
    let base = FileManager.default.urls(
      for: .applicationSupportDirectory,
      in: .userDomainMask
    ).first ?? URL(fileURLWithPath: NSHomeDirectory(), isDirectory: true)
      .appendingPathComponent("Library/Application Support", isDirectory: true)
    return base.appendingPathComponent(
      "com.onekey.firmware-artifacts-v1",
      isDirectory: true
    )
  }

  private func ensureDirectories() throws {
    do {
      try fileManager.createDirectory(
        at: artifactsDirectory,
        withIntermediateDirectories: true
      )
      try fileManager.createDirectory(
        at: leasesDirectory,
        withIntermediateDirectories: true
      )
    } catch {
      throw mapStorageError(error)
    }
  }

  private func validateReference(_ reference: String) throws -> String {
    guard let uuid = UUID(uuidString: reference),
          uuid.uuidString.lowercased() == reference.lowercased() else {
      throw FirmwareArtifactStoreError.invalidReference
    }
    return reference.lowercased()
  }

  private func artifactDirectory(for artifactRef: String) throws -> URL {
    let validated = try validateReference(artifactRef)
    return artifactsDirectory.appendingPathComponent(validated, isDirectory: true)
  }

  private func metadataURL(for artifactRef: String) throws -> URL {
    return try artifactDirectory(for: artifactRef)
      .appendingPathComponent("metadata.json", isDirectory: false)
  }

  private func leaseURL(for leaseRef: String) throws -> URL {
    let validated = try validateReference(leaseRef)
    return leasesDirectory.appendingPathComponent("\(validated).json", isDirectory: false)
  }

  private func writeMetadata(_ metadata: StoredFirmwareArtifactMetadata) throws {
    guard metadata.schemaVersion == StoredFirmwareArtifactMetadata.currentSchemaVersion,
          metadata.artifactRef == (try validateReference(metadata.artifactRef)),
          metadata.hasValidTransferBudget else {
      throw FirmwareArtifactStoreError.invalidMetadata
    }
    let encoder = JSONEncoder()
    encoder.outputFormatting = [.sortedKeys]
    do {
      let data = try encoder.encode(metadata)
      try data.write(to: metadataURL(for: metadata.artifactRef), options: [.atomic])
    } catch let error as FirmwareArtifactStoreError {
      throw error
    } catch {
      throw mapStorageError(error)
    }
  }

  private func readMetadata(_ artifactRef: String) throws -> StoredFirmwareArtifactMetadata {
    let url = try metadataURL(for: artifactRef)
    guard fileManager.fileExists(atPath: url.path) else {
      throw FirmwareArtifactStoreError.artifactNotFound
    }
    do {
      let metadata = try JSONDecoder().decode(
        StoredFirmwareArtifactMetadata.self,
        from: Data(contentsOf: url)
      )
      guard metadata.schemaVersion == StoredFirmwareArtifactMetadata.currentSchemaVersion,
            metadata.artifactRef == (try validateReference(artifactRef)),
            metadata.hasValidTransferBudget else {
        throw FirmwareArtifactStoreError.invalidMetadata
      }
      return metadata
    } catch let error as FirmwareArtifactStoreError {
      throw error
    } catch {
      throw FirmwareArtifactStoreError.invalidMetadata
    }
  }

  private func writeLease(_ lease: StoredFirmwareArtifactLeaseMetadata) throws {
    try validateLeaseMetadata(lease, expectedLeaseRef: lease.leaseRef)
    let encoder = JSONEncoder()
    encoder.outputFormatting = [.sortedKeys]
    do {
      let data = try encoder.encode(lease)
      try data.write(to: leaseURL(for: lease.leaseRef), options: [.atomic])
    } catch let error as FirmwareArtifactStoreError {
      throw error
    } catch {
      throw mapStorageError(error)
    }
  }

  private func readLease(_ leaseRef: String) throws -> StoredFirmwareArtifactLeaseMetadata {
    let url = try leaseURL(for: leaseRef)
    guard fileManager.fileExists(atPath: url.path) else {
      throw FirmwareArtifactStoreError.leaseNotFound
    }
    do {
      let lease = try JSONDecoder().decode(
        StoredFirmwareArtifactLeaseMetadata.self,
        from: Data(contentsOf: url)
      )
      try validateLeaseMetadata(lease, expectedLeaseRef: leaseRef)
      return lease
    } catch let error as FirmwareArtifactStoreError {
      throw error
    } catch {
      throw FirmwareArtifactStoreError.invalidMetadata
    }
  }

  private func validateTransferClaim(
    metadata: StoredFirmwareArtifactMetadata,
    lease: StoredFirmwareArtifactLeaseMetadata,
    leaseRef: String,
    taskId: String,
    artifactId: String,
    canonicalURL: String,
    hostname: String,
    expectedSize: Int64,
    expectedSha256: String
  ) throws {
    let taskIdHash = StoredFirmwareArtifactMetadata.sha256(taskId)
    guard lease.isActive,
          lease.activeTransferArtifactRefsByTaskHash[taskIdHash] ==
            metadata.artifactRef,
          metadata.matchesTransferIdentity(
            artifactId: artifactId,
            canonicalURL: canonicalURL,
            hostname: hostname,
            expectedSize: expectedSize,
            expectedSha256: expectedSha256
          ),
          metadata.taskIdHash == taskIdHash,
          lease.artifactRefs.contains(metadata.artifactRef),
          metadata.leaseRefs.contains(leaseRef) else {
      throw FirmwareArtifactStoreError.invalidMetadata
    }
  }

  private func validateLeaseMetadata(
    _ lease: StoredFirmwareArtifactLeaseMetadata,
    expectedLeaseRef: String
  ) throws {
    let artifactRefs = Set(lease.artifactRefs)
    let hasValidBindings = lease.activeTransferArtifactRefsByTaskHash.allSatisfy {
      entry in
      let (taskIdHash, artifactRef) = entry
      return StoredFirmwareArtifactMetadata.isTrustedSha256(taskIdHash) &&
        artifactRefs.contains(artifactRef) &&
        UUID(uuidString: artifactRef) != nil
    }
    guard lease.schemaVersion ==
            StoredFirmwareArtifactLeaseMetadata.currentSchemaVersion,
          lease.leaseRef == (try validateReference(expectedLeaseRef)),
          lease.leaseRef == expectedLeaseRef.lowercased(),
          hasValidBindings else {
      throw FirmwareArtifactStoreError.invalidMetadata
    }
  }

  private func loadAllMetadata() throws -> [StoredFirmwareArtifactMetadata] {
    let directories = try fileManager.contentsOfDirectory(
      at: artifactsDirectory,
      includingPropertiesForKeys: nil,
      options: [.skipsHiddenFiles]
    )
    return directories.compactMap { directory in
      return try? readMetadata(directory.lastPathComponent.lowercased())
    }
  }

  private func loadAllLeases() throws -> [StoredFirmwareArtifactLeaseMetadata] {
    let files = try fileManager.contentsOfDirectory(
      at: leasesDirectory,
      includingPropertiesForKeys: nil,
      options: [.skipsHiddenFiles]
    )
    return files.compactMap { file in
      let leaseRef = file.deletingPathExtension().lastPathComponent.lowercased()
      return try? readLease(leaseRef)
    }
  }

  private func loadActiveLeaseRefs() throws -> Set<String> {
    return Set(try loadAllLeases().filter(\.isActive).map(\.leaseRef))
  }

  private func isArtifactLeased(_ artifactRef: String) throws -> Bool {
    return try loadAllLeases().contains { lease in
      lease.isActive && lease.artifactRefs.contains(artifactRef)
    }
  }

  private func hasAnyActivity(_ artifactRef: String) -> Bool {
    activityCondition.lock()
    let result = !(activitiesByArtifact[artifactRef]?.isEmpty ?? true)
    activityCondition.unlock()
    return result
  }

  private func prepareForDeletion(_ artifactRef: String) -> Bool {
    let deadline = Date().addingTimeInterval(RetentionPolicy.workerCancellationTimeout)
    activityCondition.lock()
    let initialActivities = activitiesByArtifact[artifactRef] ?? [:]
    if initialActivities.values.contains(where: { $0.kind != .worker }) {
      activityCondition.unlock()
      return false
    }
    let cancellationCallbacks = initialActivities.values.compactMap(\.cancel)
    activityCondition.unlock()

    for cancel in cancellationCallbacks {
      cancel()
    }

    activityCondition.lock()
    while !(activitiesByArtifact[artifactRef]?.isEmpty ?? true) {
      if !activityCondition.wait(until: deadline) {
        activityCondition.unlock()
        return false
      }
    }
    activityCondition.unlock()
    return true
  }

  private func removeArtifactDirectory(_ artifactRef: String) throws {
    let directory = try artifactDirectory(for: artifactRef)
    do {
      if fileManager.fileExists(atPath: directory.path) {
        try fileManager.removeItem(at: directory)
      }
    } catch {
      throw mapStorageError(error)
    }
  }

  private func quarantineStaging(_ stagingURL: URL, artifactRef: String) throws {
    let directory = try artifactDirectory(for: artifactRef)
    let rejectedURL = directory.appendingPathComponent(
      "payload.rejected-\(UUID().uuidString.lowercased())",
      isDirectory: false
    )
    do {
      if fileManager.fileExists(atPath: stagingURL.path) {
        try fileManager.moveItem(at: stagingURL, to: rejectedURL)
        try synchronizeDirectory(directory)
      }
    } catch {
      throw mapStorageError(error)
    }
  }

  private func synchronizeDirectory(_ directory: URL) throws {
    let descriptor = open(directory.path, O_RDONLY)
    guard descriptor >= 0 else {
      throw FirmwareArtifactStoreError.storageFailure
    }
    defer { close(descriptor) }
    guard fsync(descriptor) == 0 else {
      throw FirmwareArtifactStoreError.storageFailure
    }
  }

  private func storedBytes(for artifactRef: String) -> (files: Int, bytes: Int64) {
    guard let directory = try? artifactDirectory(for: artifactRef),
          let enumerator = fileManager.enumerator(
            at: directory,
            includingPropertiesForKeys: [.isRegularFileKey, .fileSizeKey]
          ) else {
      return (0, 0)
    }
    var files = 0
    var bytes: Int64 = 0
    for case let fileURL as URL in enumerator {
      guard let values = try? fileURL.resourceValues(
        forKeys: [.isRegularFileKey, .fileSizeKey]
      ), values.isRegularFile == true else {
        continue
      }
      files += 1
      bytes += Int64(values.fileSize ?? 0)
    }
    return (files, bytes)
  }

  private func mapStorageError(_ error: Error) -> FirmwareArtifactStoreError {
    if let storeError = error as? FirmwareArtifactStoreError {
      return storeError
    }
    return .storageFailure
  }
}

private extension String {
  var nilIfEmpty: String? {
    return isEmpty ? nil : self
  }
}

extension URL {
  var fileSize: Int64 {
    let values = try? resourceValues(forKeys: [.fileSizeKey])
    return Int64(values?.fileSize ?? 0)
  }
}
