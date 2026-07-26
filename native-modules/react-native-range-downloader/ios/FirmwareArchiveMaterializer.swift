import Foundation

struct FirmwareArchiveMaterializedEntry {
  let entryId: String
  let logicalName: String
  let artifact: StoredFirmwareArtifactMetadata
}

private struct FirmwareArchivePendingExtraction {
  let entry: FirmwareArchiveValidatedEntry
  let bridgeEntry: OKFirmwareArchiveEntryInfo
  let scratchURL: URL?
}

enum FirmwareArchiveMaterializerError: Error, Equatable, LocalizedError {
  case invalidArchive
  case integrityFailure
  case busy

  var code: String {
    switch self {
    case .invalidArchive:
      return "ARTIFACT_ARCHIVE_INVALID"
    case .integrityFailure:
      return "ARTIFACT_ARCHIVE_INTEGRITY_FAILED"
    case .busy:
      return "ARTIFACT_ARCHIVE_BUSY"
    }
  }

  var errorDescription: String? {
    return code
  }
}

final class FirmwareArchiveMaterializer {
  static let shared = FirmwareArchiveMaterializer(store: .shared)

  private static let scratchPrefix = "materialization-"
  private static let scratchSuffix = ".partial"

  private let store: FirmwareArtifactStore
  private let activeLock = NSLock()
  private var activeArchives = Set<String>()

  init(store: FirmwareArtifactStore) {
    self.store = store
  }

  func materialize(
    leaseRef: String,
    parentArtifactId: String,
    archiveArtifactRef: String,
    archiveImmutableToken: String,
    materializationPolicy: String
  ) throws -> [FirmwareArchiveMaterializedEntry] {
    let normalizedRef = archiveArtifactRef.lowercased()
    let inserted = activeLock.withFirmwareLock {
      activeArchives.insert(normalizedRef).inserted
    }
    guard inserted else {
      throw FirmwareArchiveMaterializerError.busy
    }
    defer {
      _ = activeLock.withFirmwareLock {
        activeArchives.remove(normalizedRef)
      }
    }
    return try materializeOnce(
      leaseRef: leaseRef,
      parentArtifactId: parentArtifactId,
      archiveArtifactRef: archiveArtifactRef,
      archiveImmutableToken: archiveImmutableToken,
      materializationPolicy: materializationPolicy
    )
  }

  private func materializeOnce(
    leaseRef: String,
    parentArtifactId: String,
    archiveArtifactRef: String,
    archiveImmutableToken: String,
    materializationPolicy: String
  ) throws -> [FirmwareArchiveMaterializedEntry] {
    let archiveActivity = try store.beginActivity(
      artifactRef: archiveArtifactRef,
      kind: .materializer
    )
    defer { archiveActivity.release() }
    let archive = try requireVerifiedArchive(
      archiveActivity.artifactRef,
      parentArtifactId: parentArtifactId,
      immutableToken: archiveImmutableToken
    )
    try store.retainArtifact(
      leaseRef: leaseRef,
      artifactRef: archive.artifactRef
    )
    let archiveURL = try store.finalFileURL(for: archive.artifactRef)
    let scratchDirectory = archiveURL.deletingLastPathComponent()
    cleanupScratchFiles(in: scratchDirectory)
    defer { cleanupScratchFiles(in: scratchDirectory) }

    let bridgeEntries: [OKFirmwareArchiveEntryInfo]
    do {
      bridgeEntries = try FirmwareArchiveMinizipBridge.scanArchive(
        atPath: archiveURL.path
      )
    } catch {
      throw FirmwareArchiveMaterializerError.invalidArchive
    }
    let facts = try bridgeEntries.map { entry in
      guard let name = String(data: entry.rawName, encoding: .utf8),
            Data(name.utf8) == entry.rawName,
            entry.compressedSize <= UInt64(Int64.max),
            entry.uncompressedSize <= UInt64(Int64.max) else {
        throw FirmwareArchiveRuleError.invalidEntry
      }
      return FirmwareArchiveEntryFacts(
        index: entry.index,
        name: name,
        compressedSize: Int64(entry.compressedSize),
        uncompressedSize: Int64(entry.uncompressedSize),
        compressionMethod: entry.compressionMethod,
        generalPurposeFlags: entry.generalPurposeFlags,
        versionMadeBy: entry.versionMadeBy,
        externalAttributes: entry.externalAttributes,
        diskNumberStart: entry.diskNumberStart,
        usesZip64: entry.usesZip64
      )
    }
    let validated = try FirmwareArchiveRules.validate(
      entries: facts,
      materializationPolicy: materializationPolicy
    )
    let bridgeByIndex = Dictionary(
      uniqueKeysWithValues: bridgeEntries.map { ($0.index, $0) }
    )
    let integrityEntries =
      FirmwareArchiveRules.entriesRequiringIntegrityValidation(validated)
    let pending = try integrityEntries.map { entry in
      guard let bridgeEntry = bridgeByIndex[entry.facts.index] else {
        throw FirmwareArchiveMaterializerError.invalidArchive
      }
      let scratchURL = entry.disposition == .materialize
        ? scratchDirectory.appendingPathComponent(
            "\(Self.scratchPrefix)\(UUID().uuidString.lowercased())\(Self.scratchSuffix)",
            isDirectory: false
          )
        : nil
      return FirmwareArchivePendingExtraction(
        entry: entry,
        bridgeEntry: bridgeEntry,
        scratchURL: scratchURL
      )
    }
    do {
      try FirmwareArchiveMinizipBridge.extractEntries(
        fromArchivePath: archiveURL.path,
        entryIndexes: pending.map { NSNumber(value: $0.entry.facts.index) },
        expectedRawNames: pending.map(\.bridgeEntry.rawName),
        expectedSizes: pending.map {
          NSNumber(value: UInt64($0.entry.facts.uncompressedSize))
        },
        outputPaths: pending.map { $0.scratchURL?.path ?? "" }
      )
    } catch {
      throw FirmwareArchiveMaterializerError.integrityFailure
    }

    var children: [FirmwareArchiveMaterializedEntry] = []
    children.reserveCapacity(
      pending.filter { $0.entry.disposition == .materialize }.count
    )

    for extraction in pending where extraction.entry.disposition == .materialize {
      guard let scratchURL = extraction.scratchURL else {
        throw FirmwareArchiveMaterializerError.integrityFailure
      }
      do {
        guard scratchURL.fileSize ==
                extraction.entry.facts.uncompressedSize,
              let digest = RangeDownloadLogic.calculateSHA256(
                scratchURL.path
              )?.lowercased(),
              StoredFirmwareArtifactMetadata.isTrustedSha256(digest) else {
          throw FirmwareArchiveMaterializerError.integrityFailure
        }
        let child = try resolveChild(
          leaseRef: leaseRef,
          parentArtifactId: parentArtifactId,
          size: extraction.entry.facts.uncompressedSize,
          sha256: digest,
          scratchURL: scratchURL
        )
        children.append(
          FirmwareArchiveMaterializedEntry(
            entryId: extraction.entry.entryId,
            logicalName: extraction.entry.logicalName,
            artifact: child
          )
        )
      } catch {
        try? FileManager.default.removeItem(at: scratchURL)
        throw error
      }
      try? FileManager.default.removeItem(at: scratchURL)
    }

    var committedByRef: [String: StoredFirmwareArtifactMetadata] = [:]
    for artifactRef in Set(children.map(\.artifact.artifactRef)) {
      committedByRef[artifactRef] = try store.updateArtifact(artifactRef) {
        $0.retentionClass = .materializedChild
      }
    }
    _ = try store.updateArtifact(archive.artifactRef) {
      $0.retentionClass = .archive
    }
    return try children.map { child in
      guard let committed = committedByRef[child.artifact.artifactRef] else {
        throw FirmwareArchiveMaterializerError.integrityFailure
      }
      return FirmwareArchiveMaterializedEntry(
        entryId: child.entryId,
        logicalName: child.logicalName,
        artifact: committed
      )
    }
  }

  private func requireVerifiedArchive(
    _ artifactRef: String,
    parentArtifactId: String,
    immutableToken: String
  ) throws -> StoredFirmwareArtifactMetadata {
    let metadata = try store.loadArtifact(artifactRef)
    let finalURL = try store.finalFileURL(for: artifactRef)
    guard !parentArtifactId.isEmpty,
          parentArtifactId.utf8.count <= 256,
          !parentArtifactId.contains("\0"),
          metadata.artifactId == parentArtifactId,
          metadata.tombstonedAt == nil,
          metadata.retentionClass == .verifiedFinal ||
            metadata.retentionClass == .archive,
          let actualSize = metadata.actualSize,
          actualSize > 0,
          actualSize == metadata.expectedSize,
          let actualSha256 = metadata.actualSha256,
          RangeDownloadLogic.secureHexEquals(
            actualSha256,
            metadata.expectedSha256
          ),
          let storedToken = metadata.immutableToken,
          finalURL.fileSize == actualSize else {
      throw FirmwareArchiveMaterializerError.invalidArchive
    }
    guard storedToken == immutableToken else {
      throw FirmwareArtifactReaderError.immutableTokenMismatch
    }
    return metadata
  }

  private func resolveChild(
    leaseRef: String,
    parentArtifactId: String,
    size: Int64,
    sha256: String,
    scratchURL: URL
  ) throws -> StoredFirmwareArtifactMetadata {
    let taskId = FirmwareArchiveRules.childTaskId(
      parentArtifactId: parentArtifactId,
      sha256: sha256,
      size: size
    )
    let child: StoredFirmwareArtifactMetadata
    if let existing = try store.findArtifactForTask(
      leaseRef: leaseRef,
      taskId: taskId
    ) {
      guard existing.artifactId == taskId,
            existing.taskIdHash ==
              StoredFirmwareArtifactMetadata.sha256(taskId),
            existing.expectedSize == size,
            RangeDownloadLogic.secureHexEquals(
              existing.expectedSha256,
              sha256
            ) else {
        throw FirmwareArchiveMaterializerError.integrityFailure
      }
      try store.retainArtifact(
        leaseRef: leaseRef,
        artifactRef: existing.artifactRef
      )
      if try isReusableFinalChild(existing, size: size, sha256: sha256) {
        return existing
      }
      guard existing.retentionClass == .staging,
            existing.actualSize == nil,
            existing.actualSha256 == nil,
            existing.immutableToken == nil else {
        throw FirmwareArchiveMaterializerError.integrityFailure
      }
      child = existing
    } else {
      let created = try store.createArtifact(
        artifactId: taskId,
        canonicalURL: "onekey-artifact://materialized/\(taskId)",
        hostname: "native-artifact",
        route: .domain,
        expectedSize: size,
        expectedSha256: sha256,
        retentionClass: .staging
      )
      child = try store.updateArtifact(created.artifactRef) {
        $0.taskIdHash = StoredFirmwareArtifactMetadata.sha256(taskId)
      }
      try store.retainArtifact(
        leaseRef: leaseRef,
        artifactRef: child.artifactRef
      )
    }

    let childActivity = try store.beginActivity(
      artifactRef: child.artifactRef,
      kind: .materializer
    )
    defer { childActivity.release() }
    let stagingURL = try store.partialFileURL(for: child.artifactRef)
    do {
      if FileManager.default.fileExists(atPath: stagingURL.path) {
        _ = try FileManager.default.replaceItemAt(
          stagingURL,
          withItemAt: scratchURL,
          backupItemName: nil,
          options: []
        )
      } else {
        try FileManager.default.moveItem(at: scratchURL, to: stagingURL)
      }
    } catch {
      throw FirmwareArchiveMaterializerError.integrityFailure
    }
    let promoted = try store.promoteVerifiedStaging(
      artifactRef: child.artifactRef,
      maxBytes: size
    )
    return try store.updateArtifact(promoted.artifactRef) {
      $0.retentionClass = .staging
    }
  }

  private func isReusableFinalChild(
    _ metadata: StoredFirmwareArtifactMetadata,
    size: Int64,
    sha256: String
  ) throws -> Bool {
    guard metadata.retentionClass == .staging ||
            metadata.retentionClass == .verifiedFinal ||
            metadata.retentionClass == .materializedChild,
          metadata.actualSize == size,
          let actualSha256 = metadata.actualSha256,
          RangeDownloadLogic.secureHexEquals(actualSha256, sha256),
          metadata.immutableToken != nil else {
      return false
    }
    return try store.finalFileURL(for: metadata.artifactRef).fileSize == size
  }

  private func cleanupScratchFiles(in directory: URL) {
    guard let entries = try? FileManager.default.contentsOfDirectory(
      at: directory,
      includingPropertiesForKeys: nil,
      options: [.skipsSubdirectoryDescendants]
    ) else {
      return
    }
    for entry in entries {
      let name = entry.lastPathComponent
      if name.hasPrefix(Self.scratchPrefix),
         name.hasSuffix(Self.scratchSuffix) {
        try? FileManager.default.removeItem(at: entry)
      }
    }
  }
}
