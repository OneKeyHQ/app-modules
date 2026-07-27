import CryptoKit
import Foundation
import SniConnect

enum FirmwareArtifactStoreError: Error {
  case invalidInput(String)
  case downloadFailed(String)
  case integrityMismatch(String)
  case readerInvalid(String)
  case archiveInvalid(String)
}

struct StoredFirmwareArtifact: Sendable {
  let artifactRef: String
  let size: Int64
  let sha256: String
  let fileURL: URL
}

struct StoredFirmwareArchiveEntry {
  let entryName: String
  let artifact: StoredFirmwareArtifact
}

private struct StagedFirmwareArchiveEntry {
  let entryName: String
  let size: Int64
  let sha256: String
  let stagingURL: URL
}

private final class FirmwareArtifactRedirectDelegate: NSObject, URLSessionTaskDelegate {
  func urlSession(
    _ session: URLSession,
    task: URLSessionTask,
    willPerformHTTPRedirection response: HTTPURLResponse,
    newRequest request: URLRequest,
    completionHandler: @escaping (URLRequest?) -> Void
  ) {
    completionHandler(nil)
  }
}

private actor FirmwareArtifactDownloadCoordinator {
  private struct ActiveDownload {
    let transactionId: String
    let task: Task<StoredFirmwareArtifact, Error>
  }

  private var downloads: [String: ActiveDownload] = [:]

  func run(
    key: String,
    transactionId: String,
    operation: @escaping () async throws -> StoredFirmwareArtifact
  ) async throws -> StoredFirmwareArtifact {
    if let download = downloads[key] {
      return try await download.task.value
    }
    let task = Task {
      try await operation()
    }
    downloads[key] = ActiveDownload(
      transactionId: transactionId,
      task: task
    )
    defer { downloads[key] = nil }
    return try await task.value
  }

  func cancel(transactionId: String) {
    for download in downloads.values
    where download.transactionId == transactionId {
      download.task.cancel()
    }
  }
}

final class FirmwareArtifactStore {
  static let shared = FirmwareArtifactStore()
  static let maxReadBytes = 256 * 1024
  private static let maxLeaseMetadataBytes: Int64 = 1024 * 1024
  private static let maxTotalLeaseRefs = 8192
  private static let finalArtifactGrace: TimeInterval = 24 * 60 * 60
  private static let partialArtifactGrace: TimeInterval = 7 * 24 * 60 * 60

  private struct OpenReader {
    let handle: FileHandle
    let fileURL: URL
    let size: Int64
  }

  private struct LeaseState: Codable {
    let transactionId: String
    var artifactRefs: Set<String>
  }

  private struct LeaseEnvelope: Codable {
    let schemaVersion: Int
    var leases: [String: LeaseState]
  }

  private let fileManager = FileManager.default
  private let downloadCoordinator = FirmwareArtifactDownloadCoordinator()
  private let leaseLock = NSLock()
  private let activeDownloadLock = NSLock()
  private var activeDownloadCounts: [String: Int] = [:]
  private let cancellationLock = NSLock()
  private var cancelledTransactions: Set<String> = []
  private let readerLock = NSLock()
  private var readers: [String: OpenReader] = [:]

  private lazy var rootURL: URL = {
    let base = fileManager.urls(
      for: .applicationSupportDirectory,
      in: .userDomainMask
    ).first!
    let root = base.appendingPathComponent("onekey-firmware-artifacts", isDirectory: true)
    try? fileManager.createDirectory(at: root, withIntermediateDirectories: true)
    return root
  }()

  private init() {}

  static func validateDownloadParams(_ params: FirmwareArtifactDownloadParams) throws {
    guard
      !params.taskId.isEmpty,
      params.taskId.count <= 100,
      params.taskId.allSatisfy({ $0.isLetter || $0.isNumber || "._-".contains($0) })
    else {
      throw FirmwareArtifactStoreError.invalidInput("Invalid firmware taskId")
    }
    guard
      isSafeIdentifier(params.transactionId),
      isSafeIdentifier(params.artifactId),
      isValidLeaseRef(params.leaseRef)
    else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Invalid firmware transaction or lease identity"
      )
    }
    guard
      let url = URL(string: params.url),
      url.scheme?.lowercased() == "https",
      url.host?.isEmpty == false,
      url.user == nil,
      url.password == nil,
      url.fragment == nil,
      url.port == nil || url.port == 443
    else {
      throw FirmwareArtifactStoreError.invalidInput("Firmware URL must use HTTPS port 443")
    }
    guard
      params.expectedSize.isFinite,
      params.expectedSize > 0,
      params.expectedSize <= Double(Int64.max),
      params.expectedSize.rounded() == params.expectedSize,
      params.maxBytes.isFinite,
      params.maxBytes == params.expectedSize,
      params.maxBytes <= Double(512 * 1024 * 1024)
    else {
      throw FirmwareArtifactStoreError.invalidInput("Invalid firmware artifact size")
    }
    guard params.expectedSha256.range(
      of: "^[a-fA-F0-9]{64}$",
      options: .regularExpression
    ) != nil else {
      throw FirmwareArtifactStoreError.invalidInput("Invalid firmware artifact SHA-256")
    }
    guard params.routeType == "domain" || params.routeType == "pinnedIp" else {
      throw FirmwareArtifactStoreError.invalidInput("Invalid firmware route type")
    }
    if params.routeType == "pinnedIp" {
      guard let resolvedIp = params.resolvedIp, !resolvedIp.isEmpty else {
        throw FirmwareArtifactStoreError.invalidInput("Pinned route requires resolvedIp")
      }
    } else if params.resolvedIp != nil {
      throw FirmwareArtifactStoreError.invalidInput("Domain route must not include resolvedIp")
    }
  }

  func download(_ params: FirmwareArtifactDownloadParams) async throws -> StoredFirmwareArtifact {
    try Self.validateDownloadParams(params)
    try rejectIfCancelled(transactionId: params.transactionId)
    let expectedSha256 = params.expectedSha256.lowercased()
    try retainExpectedArtifact(
      leaseRef: params.leaseRef,
      transactionId: params.transactionId,
      artifactRef: "fw:\(expectedSha256)"
    )
    let key =
      "\(params.transactionId):\(expectedSha256):\(Int64(params.expectedSize))"
    markDownloadActive(expectedSha256, delta: 1)
    do {
      let artifact = try await downloadCoordinator.run(
        key: key,
        transactionId: params.transactionId
      ) { [self] in
        try rejectIfCancelled(transactionId: params.transactionId)
        return try await downloadLocked(
          params,
          expectedSha256: expectedSha256
        )
      }
      markDownloadActive(expectedSha256, delta: -1)
      return artifact
    } catch {
      markDownloadActive(expectedSha256, delta: -1)
      if error is CancellationError ||
        isTransactionCancelled(params.transactionId) {
        throw FirmwareArtifactStoreError.downloadFailed(
          "ARTIFACT_CANCELLED: firmware artifact download was cancelled"
        )
      }
      throw error
    }
  }

  func cancelDownloads(transactionId: String) async throws {
    guard Self.isSafeIdentifier(transactionId) else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Invalid firmware transactionId"
      )
    }
    cancellationLock.withFirmwareArtifactLock {
      cancelledTransactions.insert(transactionId)
    }
    await downloadCoordinator.cancel(transactionId: transactionId)
    try await RangeDownloader.shared.cancelFirmwareArtifactDownloads(
      transactionId: transactionId
    )
  }

  private func downloadLocked(
    _ params: FirmwareArtifactDownloadParams,
    expectedSha256: String
  ) async throws -> StoredFirmwareArtifact {
    let finalURL = artifactURL(sha256: expectedSha256)
    if let existing = try? validateStoredArtifact(
      fileURL: finalURL,
      expectedSize: Int64(params.expectedSize),
      expectedSha256: expectedSha256
    ) {
      return existing
    }

    if params.routeType == "domain" {
      return try await RangeDownloader.shared.downloadFirmwareArtifact(
        params: params
      )
    }

    let partialURL = rootURL.appendingPathComponent(
      "\(expectedSha256).\(params.taskId).partial",
      isDirectory: false
    )
    if !fileManager.fileExists(atPath: partialURL.path) {
      fileManager.createFile(atPath: partialURL.path, contents: nil)
    }
    var currentSize = try fileSize(partialURL)
    if currentSize > Int64(params.expectedSize) {
      try fileManager.removeItem(at: partialURL)
      fileManager.createFile(atPath: partialURL.path, contents: nil)
      currentSize = 0
    }
    if currentSize == Int64(params.expectedSize) {
      if let completed = try? validateStoredArtifact(
        fileURL: partialURL,
        expectedSize: Int64(params.expectedSize),
        expectedSha256: expectedSha256
      ) {
        try promote(source: partialURL, destination: finalURL)
        return StoredFirmwareArtifact(
          artifactRef: completed.artifactRef,
          size: completed.size,
          sha256: completed.sha256,
          fileURL: finalURL
        )
      }
      try fileManager.removeItem(at: partialURL)
      fileManager.createFile(atPath: partialURL.path, contents: nil)
      currentSize = 0
    }

    try await streamDownload(
      params,
      partialURL: partialURL,
      resumeOffset: min(currentSize, Int64(params.expectedSize))
    )
    let artifact: StoredFirmwareArtifact
    do {
      artifact = try validateStoredArtifact(
        fileURL: partialURL,
        expectedSize: Int64(params.expectedSize),
        expectedSha256: expectedSha256
      )
    } catch {
      try? fileManager.removeItem(at: partialURL)
      throw error
    }
    try promote(source: partialURL, destination: finalURL)
    return StoredFirmwareArtifact(
      artifactRef: artifact.artifactRef,
      size: artifact.size,
      sha256: artifact.sha256,
      fileURL: finalURL
    )
  }

  func acceptBackgroundDownload(
    temporaryURL: URL,
    leaseRef: String,
    transactionId: String,
    expectedSize: Int64,
    expectedSha256: String
  ) throws -> StoredFirmwareArtifact {
    let normalizedExpectedSha256 = expectedSha256.lowercased()
    try retainExpectedArtifact(
      leaseRef: leaseRef,
      transactionId: transactionId,
      artifactRef: "fw:\(normalizedExpectedSha256)"
    )
    let finalURL = artifactURL(sha256: normalizedExpectedSha256)
    if let existing = try? validateStoredArtifact(
      fileURL: finalURL,
      expectedSize: expectedSize,
      expectedSha256: normalizedExpectedSha256
    ) {
      return existing
    }
    let artifact = try validateStoredArtifact(
      fileURL: temporaryURL,
      expectedSize: expectedSize,
      expectedSha256: normalizedExpectedSha256
    )
    try promote(source: temporaryURL, destination: finalURL)
    return StoredFirmwareArtifact(
      artifactRef: artifact.artifactRef,
      size: artifact.size,
      sha256: artifact.sha256,
      fileURL: finalURL
    )
  }

  func storedArtifact(
    expectedSize: Int64,
    expectedSha256: String
  ) throws -> StoredFirmwareArtifact? {
    let finalURL = artifactURL(sha256: expectedSha256)
    guard fileManager.fileExists(atPath: finalURL.path) else {
      return nil
    }
    return try validateStoredArtifact(
      fileURL: finalURL,
      expectedSize: expectedSize,
      expectedSha256: expectedSha256
    )
  }

  func discard(artifactRef: String) throws {
    let fileURL = try resolveArtifactURL(artifactRef)
    leaseLock.lock()
    let isRetained: Bool
    do {
      isRetained = try loadLeasesLocked().leases.values.contains {
        $0.artifactRefs.contains(artifactRef)
      }
    } catch {
      leaseLock.unlock()
      throw error
    }
    leaseLock.unlock()
    guard !isRetained else {
      throw FirmwareArtifactStoreError.invalidInput(
        "ARTIFACT_LEASED: firmware artifact is retained"
      )
    }
    readerLock.lock()
    let hasMatchingReader = readers.values.contains {
      $0.fileURL == fileURL
    }
    readerLock.unlock()
    guard !hasMatchingReader else {
      throw FirmwareArtifactStoreError.readerInvalid(
        "ARTIFACT_BUSY: firmware artifact has an open reader"
      )
    }
    if fileManager.fileExists(atPath: fileURL.path) {
      try fileManager.removeItem(at: fileURL)
    }
  }

  func open(artifactRef: String) throws -> (readerId: String, size: Int64) {
    let fileURL = try resolveArtifactURL(artifactRef)
    let size = try fileSize(fileURL)
    guard size > 0 else {
      throw FirmwareArtifactStoreError.readerInvalid("Firmware artifact is empty")
    }
    let expectedSha256 = String(artifactRef.dropFirst(3))
    guard try hashFile(fileURL) == expectedSha256 else {
      throw FirmwareArtifactStoreError.integrityMismatch(
        "Firmware artifact SHA-256 mismatch"
      )
    }
    let readerId = UUID().uuidString
    let reader = OpenReader(
      handle: try FileHandle(forReadingFrom: fileURL),
      fileURL: fileURL,
      size: size
    )
    readerLock.lock()
    readers[readerId] = reader
    readerLock.unlock()
    return (readerId, size)
  }

  func read(readerId: String, offset: Int64, length: Int) throws -> Data {
    guard length > 0, length <= Self.maxReadBytes, offset >= 0 else {
      throw FirmwareArtifactStoreError.readerInvalid("Invalid firmware artifact read")
    }
    readerLock.lock()
    defer { readerLock.unlock() }
    guard
      let reader = readers[readerId],
      offset <= reader.size - Int64(length)
    else {
      throw FirmwareArtifactStoreError.readerInvalid("Firmware artifact read is out of bounds")
    }
    try reader.handle.seek(toOffset: UInt64(offset))
    let data = try reader.handle.read(upToCount: length) ?? Data()
    guard data.count == length else {
      throw FirmwareArtifactStoreError.readerInvalid("Firmware artifact returned a short read")
    }
    return data
  }

  func close(readerId: String) throws {
    readerLock.lock()
    let reader = readers.removeValue(forKey: readerId)
    readerLock.unlock()
    guard let reader else {
      return
    }
    try reader.handle.close()
  }

  func materializeArchive(
    leaseRef: String,
    artifactRef: String,
    expectedEntries: [FirmwareArchiveExpectedEntry]
  ) throws -> [StoredFirmwareArchiveEntry] {
    try requireLease(leaseRef)
    let archiveURL = try resolveArtifactURL(artifactRef)
    let scratchURL = rootURL.appendingPathComponent(
      "archive-\(UUID().uuidString)",
      isDirectory: true
    )
    try fileManager.createDirectory(at: scratchURL, withIntermediateDirectories: true)
    defer { try? fileManager.removeItem(at: scratchURL) }

    let requirements = try validateArchiveRequirements(expectedEntries)
    let archiveEntries = try FirmwareArchiveMinizipBridge.scanArchive(
      atPath: archiveURL.path
    )
    try validateArchiveEntries(
      archiveEntries,
      requirements: requirements
    )

    var staged: [StagedFirmwareArchiveEntry] = []
    for (index, requirement) in requirements.enumerated() {
      let stagingURL = scratchURL.appendingPathComponent(
        "\(index).entry",
        isDirectory: false
      )
      fileManager.createFile(atPath: stagingURL.path, contents: nil)
      let handle = try FileHandle(forWritingTo: stagingURL)
      var hasher = SHA256()
      var actualSize: Int64 = 0
      var consumerError: Error?
      do {
        try FirmwareArchiveMinizipBridge.extractEntryNamed(
          requirement.entryName,
          archivePath: archiveURL.path
        ) { chunk in
          guard consumerError == nil else {
            return false
          }
          do {
            actualSize += Int64(chunk.count)
            guard actualSize <= Int64(requirement.expectedSize) else {
              throw FirmwareArtifactStoreError.archiveInvalid(
                "Firmware archive entry exceeds its expected size"
              )
            }
            hasher.update(data: chunk)
            try handle.write(contentsOf: chunk)
            return true
          } catch {
            consumerError = error
            return false
          }
        }
        if let consumerError {
          throw consumerError
        }
        try handle.synchronize()
        try handle.close()
      } catch {
        try? handle.close()
        throw error
      }
      let sha256 = hasher.finalize().map {
        String(format: "%02x", $0)
      }.joined()
      guard
        actualSize == Int64(requirement.expectedSize),
        sha256 == requirement.expectedSha256.lowercased()
      else {
        throw FirmwareArtifactStoreError.archiveInvalid(
          "Firmware archive entry integrity mismatch"
        )
      }
      staged.append(
        StagedFirmwareArchiveEntry(
          entryName: requirement.entryName,
          size: actualSize,
          sha256: sha256,
          stagingURL: stagingURL
        )
      )
    }

    return try staged.map { entry in
      let destination = artifactURL(sha256: entry.sha256)
      if (try? validateStoredArtifact(
        fileURL: destination,
        expectedSize: entry.size,
        expectedSha256: entry.sha256
      )) == nil {
        try promote(source: entry.stagingURL, destination: destination)
      }
      let stored = StoredFirmwareArtifact(
        artifactRef: "fw:\(entry.sha256)",
        size: entry.size,
        sha256: entry.sha256,
        fileURL: destination
      )
      try retainExpectedArtifact(
        leaseRef: leaseRef,
        transactionId: nil,
        artifactRef: stored.artifactRef
      )
      return StoredFirmwareArchiveEntry(
        entryName: entry.entryName,
        artifact: stored
      )
    }
  }

  func createLease(transactionId: String) throws -> String {
    guard Self.isSafeIdentifier(transactionId) else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Invalid firmware transactionId"
      )
    }
    leaseLock.lock()
    defer { leaseLock.unlock() }
    var envelope = try loadLeasesLocked()
    guard envelope.leases.count < 32 else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Too many firmware artifact leases"
      )
    }
    let leaseRef = "fwlease:\(UUID().uuidString.lowercased())"
    envelope.leases[leaseRef] = LeaseState(
      transactionId: transactionId,
      artifactRefs: []
    )
    try saveLeasesLocked(envelope)
    return leaseRef
  }

  func retain(leaseRef: String, artifactRef: String) throws {
    _ = try resolveArtifactURL(artifactRef)
    try retainExpectedArtifact(
      leaseRef: leaseRef,
      transactionId: nil,
      artifactRef: artifactRef
    )
  }

  func releaseLease(leaseRef: String, disposition: String) throws {
    guard
      disposition == "completed" ||
        disposition == "safeCancelled" ||
        disposition == "safeAbandoned"
    else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Invalid firmware lease disposition"
      )
    }
    let transactionId: String = try {
      leaseLock.lock()
      defer { leaseLock.unlock() }
      var envelope = try loadLeasesLocked()
      guard
        let lease = envelope.leases.removeValue(
          forKey: try validateLeaseRef(leaseRef)
        )
      else {
        throw FirmwareArtifactStoreError.invalidInput(
          "Firmware artifact lease is unavailable"
        )
      }
      try saveLeasesLocked(envelope)
      return lease.transactionId
    }()
    cancellationLock.withFirmwareArtifactLock {
      cancelledTransactions.remove(transactionId)
    }
  }

  func reconcileLeases(activeLeaseRefs: [String]) throws {
    guard activeLeaseRefs.count <= 32 else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Too many active firmware artifact leases"
      )
    }
    let active = try Set(activeLeaseRefs.map(validateLeaseRef))
    guard active.count == activeLeaseRefs.count else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Duplicate active firmware artifact lease"
      )
    }
    leaseLock.lock()
    defer { leaseLock.unlock() }
    var envelope = try loadLeasesLocked()
    guard active.allSatisfy({ envelope.leases[$0] != nil }) else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Firmware artifact lease reconciliation is incomplete"
      )
    }
    envelope.leases = envelope.leases.filter { active.contains($0.key) }
    try saveLeasesLocked(envelope)
  }

  func sweepOrphans() throws -> (deletedFiles: Int, deletedBytes: Int64) {
    leaseLock.lock()
    let retained: Set<String>
    do {
      retained = Set(
        try loadLeasesLocked().leases.values
          .flatMap(\.artifactRefs)
          .map { String($0.dropFirst(3)) }
      )
    } catch {
      leaseLock.unlock()
      throw error
    }
    leaseLock.unlock()
    activeDownloadLock.lock()
    let active = Set(activeDownloadCounts.filter { $0.value > 0 }.keys)
    activeDownloadLock.unlock()
    readerLock.lock()
    let openPaths = Set(readers.values.map(\.fileURL.path))
    readerLock.unlock()

    let now = Date()
    var deletedFiles = 0
    var deletedBytes: Int64 = 0
    let files = try fileManager.contentsOfDirectory(
      at: rootURL,
      includingPropertiesForKeys: [.contentModificationDateKey, .fileSizeKey],
      options: [.skipsHiddenFiles]
    )
    for fileURL in files {
      let name = fileURL.lastPathComponent
      guard name != "leases.json", name.count >= 64 else { continue }
      let sha256 = String(name.prefix(64))
      guard
        sha256.range(of: "^[a-f0-9]{64}$", options: .regularExpression) != nil,
        !retained.contains(sha256),
        !active.contains(sha256),
        !openPaths.contains(fileURL.path)
      else {
        continue
      }
      let grace: TimeInterval
      if name.hasSuffix(".bin") {
        grace = Self.finalArtifactGrace
      } else if name.hasSuffix(".partial") {
        grace = Self.partialArtifactGrace
      } else {
        continue
      }
      let values = try fileURL.resourceValues(
        forKeys: [.contentModificationDateKey, .fileSizeKey]
      )
      guard
        let modifiedAt = values.contentModificationDate,
        now.timeIntervalSince(modifiedAt) >= grace
      else {
        continue
      }
      let size = Int64(values.fileSize ?? 0)
      try fileManager.removeItem(at: fileURL)
      deletedFiles += 1
      deletedBytes += size
    }
    return (deletedFiles, deletedBytes)
  }

  private func requireLease(_ leaseRef: String) throws {
    leaseLock.lock()
    defer { leaseLock.unlock() }
    guard try loadLeasesLocked().leases[validateLeaseRef(leaseRef)] != nil else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Firmware artifact lease is unavailable"
      )
    }
  }

  private func retainExpectedArtifact(
    leaseRef: String,
    transactionId: String?,
    artifactRef: String
  ) throws {
    guard artifactRef.range(
      of: "^fw:[a-f0-9]{64}$",
      options: .regularExpression
    ) != nil else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Invalid firmware artifactRef"
      )
    }
    leaseLock.lock()
    defer { leaseLock.unlock() }
    var envelope = try loadLeasesLocked()
    let validatedLeaseRef = try validateLeaseRef(leaseRef)
    guard var lease = envelope.leases[validatedLeaseRef] else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Firmware artifact lease is unavailable"
      )
    }
    if let transactionId, lease.transactionId != transactionId {
      throw FirmwareArtifactStoreError.invalidInput(
        "Firmware artifact lease transaction mismatch"
      )
    }
    if lease.artifactRefs.insert(artifactRef).inserted {
      envelope.leases[validatedLeaseRef] = lease
      try saveLeasesLocked(envelope)
    }
  }

  private func validateLeaseRef(_ leaseRef: String) throws -> String {
    guard Self.isValidLeaseRef(leaseRef) else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Invalid firmware leaseRef"
      )
    }
    return leaseRef
  }

  private static func isValidLeaseRef(_ value: String) -> Bool {
    value.range(
      of: "^fwlease:[a-f0-9-]{36}$",
      options: .regularExpression
    ) != nil
  }

  private static func isSafeIdentifier(_ value: String) -> Bool {
    value.range(
      of: "^[A-Za-z0-9._:-]{1,160}$",
      options: .regularExpression
    ) != nil
  }

  private func loadLeasesLocked() throws -> LeaseEnvelope {
    let url = rootURL.appendingPathComponent("leases.json", isDirectory: false)
    guard fileManager.fileExists(atPath: url.path) else {
      return LeaseEnvelope(schemaVersion: 1, leases: [:])
    }
    let fileSize = try url.resourceValues(forKeys: [.fileSizeKey]).fileSize ?? 0
    guard fileSize > 0, Int64(fileSize) <= Self.maxLeaseMetadataBytes else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Firmware lease metadata is too large"
      )
    }
    let envelope = try JSONDecoder().decode(
      LeaseEnvelope.self,
      from: Data(contentsOf: url)
    )
    guard envelope.schemaVersion == 1, envelope.leases.count <= 32 else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Unsupported firmware lease schema"
      )
    }
    guard
      envelope.leases.values.reduce(0, { $0 + $1.artifactRefs.count })
        <= Self.maxTotalLeaseRefs
    else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Firmware lease metadata is too large"
      )
    }
    for (leaseRef, lease) in envelope.leases {
      guard
        Self.isValidLeaseRef(leaseRef),
        Self.isSafeIdentifier(lease.transactionId),
        lease.artifactRefs.count <= 4096,
        lease.artifactRefs.allSatisfy({
          $0.range(
            of: "^fw:[a-f0-9]{64}$",
            options: .regularExpression
          ) != nil
        })
      else {
        throw FirmwareArtifactStoreError.invalidInput(
          "Invalid persisted firmware lease"
        )
      }
    }
    return envelope
  }

  private func saveLeasesLocked(_ envelope: LeaseEnvelope) throws {
    guard
      envelope.schemaVersion == 1,
      envelope.leases.count <= 32,
      envelope.leases.values.allSatisfy({
        $0.artifactRefs.count <= 4096
      }),
      envelope.leases.values.reduce(0, {
        $0 + $1.artifactRefs.count
      }) <= Self.maxTotalLeaseRefs
    else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Firmware lease metadata is too large"
      )
    }
    let data = try JSONEncoder().encode(envelope)
    guard Int64(data.count) <= Self.maxLeaseMetadataBytes else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Firmware lease metadata is too large"
      )
    }
    let url = rootURL.appendingPathComponent("leases.json", isDirectory: false)
    try data.write(to: url, options: [.atomic])
  }

  private func markDownloadActive(_ sha256: String, delta: Int) {
    activeDownloadLock.lock()
    let next = (activeDownloadCounts[sha256] ?? 0) + delta
    if next <= 0 {
      activeDownloadCounts.removeValue(forKey: sha256)
    } else {
      activeDownloadCounts[sha256] = next
    }
    activeDownloadLock.unlock()
  }

  private func validateArchiveRequirements(
    _ expectedEntries: [FirmwareArchiveExpectedEntry]
  ) throws -> [FirmwareArchiveExpectedEntry] {
    guard !expectedEntries.isEmpty, expectedEntries.count <= 4096 else {
      throw FirmwareArtifactStoreError.archiveInvalid(
        "Firmware archive expected entry count is invalid"
      )
    }
    var names = Set<String>()
    var canonicalNames = Set<String>()
    var totalSize: Int64 = 0
    for entry in expectedEntries {
      guard
        !entry.artifactId.isEmpty,
        entry.artifactId.count <= 160,
        entry.artifactId.allSatisfy({
          $0.isLetter || $0.isNumber || "._-".contains($0)
        }),
        isPortableArchiveEntryName(
          entry.entryName,
          canonicalNames: &canonicalNames
        ),
        names.insert(entry.entryName).inserted,
        entry.expectedSize.isFinite,
        entry.expectedSize > 0,
        entry.expectedSize <= Double(128 * 1024 * 1024),
        entry.expectedSize.rounded() == entry.expectedSize,
        entry.expectedSha256.range(
          of: "^[a-fA-F0-9]{64}$",
          options: .regularExpression
        ) != nil
      else {
        throw FirmwareArtifactStoreError.archiveInvalid(
          "Firmware archive expected entry is invalid"
        )
      }
      totalSize += Int64(entry.expectedSize)
      guard totalSize <= 512 * 1024 * 1024 else {
        throw FirmwareArtifactStoreError.archiveInvalid(
          "Firmware archive expected entries exceed limits"
        )
      }
    }
    return expectedEntries
  }

  private func validateArchiveEntries(
    _ entries: [FirmwareArchiveEntryInfo],
    requirements: [FirmwareArchiveExpectedEntry]
  ) throws {
    guard entries.count == requirements.count else {
      throw FirmwareArtifactStoreError.archiveInvalid(
        "Firmware archive has missing or extra entries"
      )
    }
    let requirementsByName = Dictionary(
      uniqueKeysWithValues: requirements.map { ($0.entryName, $0) }
    )
    var names = Set<String>()
    var canonicalNames = Set<String>()
    for entry in entries {
      guard
        names.insert(entry.name).inserted,
        isPortableArchiveEntryName(
          entry.name,
          canonicalNames: &canonicalNames
        ),
        let requirement = requirementsByName[entry.name],
        entry.uncompressedSize == Int64(requirement.expectedSize),
        entry.uncompressedSize > 0,
        entry.compressedSize >= 0,
        entry.compressedSize <= 512 * 1024 * 1024,
        entry.uncompressedSize <= max(entry.compressedSize, 1) * 1000,
        entry.diskNumber == 0,
        entry.flags & 1 == 0,
        entry.compressionMethod == 0 || entry.compressionMethod == 8,
        entry.linkName?.isEmpty != false,
        isRegularArchiveEntry(entry)
      else {
        throw FirmwareArtifactStoreError.archiveInvalid(
          "Firmware archive entry metadata is invalid"
        )
      }
    }
  }

  private func isPortableArchiveEntryName(
    _ name: String,
    canonicalNames: inout Set<String>
  ) -> Bool {
    let lowered = name.precomposedStringWithCanonicalMapping.lowercased()
    let components = name.split(
      separator: "/",
      omittingEmptySubsequences: false
    )
    let nestedArchiveExtensions = [
      ".zip", ".7z", ".rar", ".tar", ".gz", ".tgz",
    ]
    guard
      !name.isEmpty,
      name.count <= 512,
      name == name.precomposedStringWithCanonicalMapping,
      !name.hasPrefix("/"),
      !name.hasPrefix("\\"),
      !name.contains("\\"),
      !name.contains(":"),
      name.unicodeScalars.allSatisfy({
        !CharacterSet.controlCharacters.contains($0)
      }),
      components.allSatisfy({
        !$0.isEmpty &&
          $0 != "." &&
          $0 != ".." &&
          !$0.hasSuffix(".") &&
          !$0.hasSuffix(" ")
      }),
      !nestedArchiveExtensions.contains(where: {
        lowered.hasSuffix($0)
      }),
      canonicalNames.insert(lowered).inserted
    else {
      return false
    }
    return true
  }

  private func isRegularArchiveEntry(
    _ entry: FirmwareArchiveEntryInfo
  ) -> Bool {
    let hostSystem = UInt8(entry.versionMadeBy >> 8)
    if hostSystem == 3 || hostSystem == 19 {
      let fileType = (entry.externalAttributes >> 16) & 0xF000
      return fileType == 0 || fileType == 0x8000
    }
    return entry.externalAttributes & 0x10 == 0
  }

  private func streamDownload(
    _ params: FirmwareArtifactDownloadParams,
    partialURL: URL,
    resumeOffset: Int64
  ) async throws {
    guard let url = URL(string: params.url), let hostname = url.host else {
      throw FirmwareArtifactStoreError.invalidInput("Invalid firmware URL")
    }
    var request = URLRequest(url: url)
    request.cachePolicy = .reloadIgnoringLocalCacheData
    request.timeoutInterval = params.overallDeadlineSeconds ?? 180
    request.setValue("identity", forHTTPHeaderField: "Accept-Encoding")
    if resumeOffset > 0 {
      request.setValue("bytes=\(resumeOffset)-", forHTTPHeaderField: "Range")
    }

    let pinnedSession: SniConnectPinnedSession?
    if params.routeType == "pinnedIp" {
      pinnedSession = try SniConnectPinnedTransport.makeSession(
          hostname: hostname,
          ip: params.resolvedIp!
        )
    } else {
      pinnedSession = nil
    }
    let domainDelegate = FirmwareArtifactRedirectDelegate()
    let session = pinnedSession?.session ?? URLSession(
      configuration: .ephemeral,
      delegate: domainDelegate,
      delegateQueue: nil
    )
    defer {
      if let pinnedSession {
        pinnedSession.close()
      } else {
        session.finishTasksAndInvalidate()
      }
    }

    let bytes: URLSession.AsyncBytes
    let response: URLResponse
    do {
      (bytes, response) = try await session.bytes(for: request)
    } catch {
      if isCancellationError(
        error,
        transactionId: params.transactionId
      ) {
        throw FirmwareArtifactStoreError.downloadFailed(
          "ARTIFACT_CANCELLED: firmware artifact download was cancelled"
        )
      }
      throw FirmwareArtifactStoreError.downloadFailed(
        "ARTIFACT_NETWORK_FAILED: firmware request failed"
      )
    }
    guard let httpResponse = response as? HTTPURLResponse else {
      throw FirmwareArtifactStoreError.downloadFailed("Firmware response is not HTTP")
    }
    guard httpResponse.statusCode == 200 || httpResponse.statusCode == 206 else {
      throw FirmwareArtifactStoreError.downloadFailed(
        "ARTIFACT_HTTP_\(httpResponse.statusCode): firmware request failed"
      )
    }
    let append = resumeOffset > 0 && httpResponse.statusCode == 206
    if httpResponse.statusCode == 206 {
      guard
        let contentRange = httpResponse.value(forHTTPHeaderField: "Content-Range"),
        validateContentRange(
          contentRange,
          expectedStart: append ? resumeOffset : 0,
          expectedTotal: Int64(params.expectedSize)
        )
      else {
        throw FirmwareArtifactStoreError.downloadFailed(
          "ARTIFACT_PROTOCOL_INVALID: firmware resume Content-Range is invalid"
        )
      }
    }
    let handle = try FileHandle(forWritingTo: partialURL)
    defer { try? handle.close() }
    if append {
      try handle.seekToEnd()
    } else {
      try handle.truncate(atOffset: 0)
    }

    var written = append ? resumeOffset : 0
    var buffer = Data()
    buffer.reserveCapacity(64 * 1024)
    do {
      for try await byte in bytes {
        buffer.append(byte)
        if buffer.count >= 64 * 1024 {
          written += Int64(buffer.count)
          guard written <= Int64(params.maxBytes) else {
            throw FirmwareArtifactStoreError.downloadFailed(
              "ARTIFACT_PROTOCOL_INVALID: firmware artifact exceeds maxBytes"
            )
          }
          try handle.write(contentsOf: buffer)
          buffer.removeAll(keepingCapacity: true)
        }
      }
    } catch let error as FirmwareArtifactStoreError {
      throw error
    } catch {
      if isCancellationError(
        error,
        transactionId: params.transactionId
      ) {
        throw FirmwareArtifactStoreError.downloadFailed(
          "ARTIFACT_CANCELLED: firmware artifact download was cancelled"
        )
      }
      throw FirmwareArtifactStoreError.downloadFailed(
        "ARTIFACT_NETWORK_FAILED: firmware response stream failed"
      )
    }
    if !buffer.isEmpty {
      written += Int64(buffer.count)
      guard written <= Int64(params.maxBytes) else {
        throw FirmwareArtifactStoreError.downloadFailed(
          "ARTIFACT_PROTOCOL_INVALID: firmware artifact exceeds maxBytes"
        )
      }
      try handle.write(contentsOf: buffer)
    }
    try handle.synchronize()
    try rejectIfCancelled(transactionId: params.transactionId)
  }

  private func rejectIfCancelled(transactionId: String) throws {
    if isTransactionCancelled(transactionId) {
      throw FirmwareArtifactStoreError.downloadFailed(
        "ARTIFACT_CANCELLED: firmware artifact download was cancelled"
      )
    }
  }

  func isTransactionCancelled(_ transactionId: String) -> Bool {
    cancellationLock.withFirmwareArtifactLock {
      cancelledTransactions.contains(transactionId)
    }
  }

  private func isCancellationError(
    _ error: Error,
    transactionId: String
  ) -> Bool {
    if error is CancellationError ||
      isTransactionCancelled(transactionId) {
      return true
    }
    let nsError = error as NSError
    return nsError.domain == NSURLErrorDomain &&
      nsError.code == NSURLErrorCancelled
  }

  private func validateContentRange(
    _ value: String,
    expectedStart: Int64,
    expectedTotal: Int64
  ) -> Bool {
    let pattern = #"^bytes ([0-9]+)-([0-9]+)/([0-9]+)$"#
    guard
      let expression = try? NSRegularExpression(pattern: pattern),
      let match = expression.firstMatch(
        in: value.lowercased(),
        range: NSRange(value.startIndex..., in: value)
      ),
      match.range.location != NSNotFound,
      let startRange = Range(match.range(at: 1), in: value),
      let endRange = Range(match.range(at: 2), in: value),
      let totalRange = Range(match.range(at: 3), in: value),
      let start = Int64(value[startRange]),
      let end = Int64(value[endRange]),
      let total = Int64(value[totalRange])
    else {
      return false
    }
    return start == expectedStart &&
      end >= start &&
      end < total &&
      total == expectedTotal
  }

  private func validateStoredArtifact(
    fileURL: URL,
    expectedSize: Int64,
    expectedSha256: String
  ) throws -> StoredFirmwareArtifact {
    let size = try fileSize(fileURL)
    guard size == expectedSize else {
      throw FirmwareArtifactStoreError.integrityMismatch(
        "ARTIFACT_INTEGRITY_FAILED: firmware artifact size mismatch"
      )
    }
    let sha256 = try hashFile(fileURL)
    guard sha256 == expectedSha256 else {
      throw FirmwareArtifactStoreError.integrityMismatch(
        "ARTIFACT_INTEGRITY_FAILED: firmware artifact SHA-256 mismatch"
      )
    }
    return StoredFirmwareArtifact(
      artifactRef: "fw:\(sha256)",
      size: size,
      sha256: sha256,
      fileURL: fileURL
    )
  }

  private func resolveArtifactURL(_ artifactRef: String) throws -> URL {
    guard artifactRef.hasPrefix("fw:") else {
      throw FirmwareArtifactStoreError.invalidInput("Invalid firmware artifactRef")
    }
    let sha256 = String(artifactRef.dropFirst(3))
    guard sha256.range(of: "^[a-f0-9]{64}$", options: .regularExpression) != nil else {
      throw FirmwareArtifactStoreError.invalidInput("Invalid firmware artifactRef")
    }
    let url = artifactURL(sha256: sha256)
    guard fileManager.fileExists(atPath: url.path) else {
      throw FirmwareArtifactStoreError.invalidInput("Firmware artifact not found")
    }
    return url
  }

  private func artifactURL(sha256: String) -> URL {
    rootURL.appendingPathComponent("\(sha256).bin", isDirectory: false)
  }

  private func fileSize(_ url: URL) throws -> Int64 {
    let attributes = try fileManager.attributesOfItem(atPath: url.path)
    return (attributes[.size] as? NSNumber)?.int64Value ?? 0
  }

  private func hashFile(_ url: URL) throws -> String {
    let handle = try FileHandle(forReadingFrom: url)
    defer { try? handle.close() }
    var hasher = SHA256()
    while let data = try handle.read(upToCount: 256 * 1024), !data.isEmpty {
      hasher.update(data: data)
    }
    return hasher.finalize().map { String(format: "%02x", $0) }.joined()
  }

  private func promote(source: URL, destination: URL) throws {
    let stagingURL = destination
      .deletingLastPathComponent()
      .appendingPathComponent(".promote-\(UUID().uuidString)", isDirectory: false)
    try fileManager.moveItem(at: source, to: stagingURL)
    defer {
      if fileManager.fileExists(atPath: stagingURL.path) {
        try? fileManager.removeItem(at: stagingURL)
      }
    }
    if fileManager.fileExists(atPath: destination.path) {
      _ = try fileManager.replaceItemAt(
        destination,
        withItemAt: stagingURL,
        backupItemName: nil,
        options: []
      )
    } else {
      try fileManager.moveItem(at: stagingURL, to: destination)
    }
  }
}

private extension NSLock {
  func withFirmwareArtifactLock<T>(_ body: () -> T) -> T {
    lock()
    defer { unlock() }
    return body()
  }
}
