import CryptoKit
import Foundation
import SniConnect

func isFirmwareArtifactTLSError(_ error: Error) -> Bool {
  var current: NSError? = error as NSError
  var visited = Set<ObjectIdentifier>()
  while let candidate = current {
    let identifier = ObjectIdentifier(candidate)
    guard visited.insert(identifier).inserted else {
      break
    }
    if candidate.domain == NSURLErrorDomain,
      [
        NSURLErrorSecureConnectionFailed,
        NSURLErrorServerCertificateHasBadDate,
        NSURLErrorServerCertificateUntrusted,
        NSURLErrorServerCertificateHasUnknownRoot,
        NSURLErrorServerCertificateNotYetValid,
        NSURLErrorClientCertificateRejected,
        NSURLErrorClientCertificateRequired,
        NSURLErrorAppTransportSecurityRequiresSecureConnection,
      ].contains(candidate.code)
    {
      return true
    }
    current = candidate.userInfo[NSUnderlyingErrorKey] as? NSError
  }
  return false
}

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

private final class FirmwareArtifactStreamDelegate: NSObject, URLSessionDataDelegate {
  private let partialURL: URL
  private let hostname: String
  private let resumeOffset: Int64
  private let expectedSize: Int64
  private let maxBytes: Int64
  private let isCancelled: () -> Bool
  private let stateLock = NSLock()
  private var continuation: CheckedContinuation<Void, Error>?
  private var dataTask: URLSessionDataTask?
  private var cancellationRequested = false
  private var completed = false
  private var responseAccepted = false
  private var handle: FileHandle?
  private var written: Int64 = 0

  init(
    partialURL: URL,
    hostname: String,
    resumeOffset: Int64,
    expectedSize: Int64,
    maxBytes: Int64,
    isCancelled: @escaping () -> Bool
  ) {
    self.partialURL = partialURL
    self.hostname = hostname
    self.resumeOffset = resumeOffset
    self.expectedSize = expectedSize
    self.maxBytes = maxBytes
    self.isCancelled = isCancelled
  }

  func run(session: URLSession, request: URLRequest) async throws {
    try await withTaskCancellationHandler {
      try await withCheckedThrowingContinuation {
        (continuation: CheckedContinuation<Void, Error>) in
        let task = session.dataTask(with: request)
        stateLock.lock()
        if cancellationRequested || completed {
          stateLock.unlock()
          task.cancel()
          continuation.resume(throwing: CancellationError())
          return
        }
        self.continuation = continuation
        dataTask = task
        stateLock.unlock()
        task.resume()
      }
    } onCancel: {
      self.cancel()
    }
  }

  private func cancel() {
    stateLock.lock()
    cancellationRequested = true
    let task = dataTask
    stateLock.unlock()
    task?.cancel()
  }

  private func finish(_ result: Result<Void, Error>) {
    stateLock.lock()
    guard !completed else {
      stateLock.unlock()
      return
    }
    completed = true
    let continuation = continuation
    self.continuation = nil
    dataTask = nil
    let handle = handle
    self.handle = nil
    stateLock.unlock()

    try? handle?.close()
    continuation?.resume(with: result)
  }

  private func reject(
    _ dataTask: URLSessionDataTask,
    completionHandler: @escaping (URLSession.ResponseDisposition) -> Void,
    message: String
  ) {
    completionHandler(.cancel)
    fail(dataTask, message: message)
  }

  private func fail(_ task: URLSessionTask, message: String) {
    task.cancel()
    finish(.failure(FirmwareArtifactStoreError.downloadFailed(message)))
  }

  func urlSession(
    _ session: URLSession,
    task: URLSessionTask,
    willPerformHTTPRedirection response: HTTPURLResponse,
    newRequest request: URLRequest,
    completionHandler: @escaping (URLRequest?) -> Void
  ) {
    completionHandler(nil)
    fail(
      task,
      message:
        "ARTIFACT_REDIRECT_REJECTED: firmware response changed canonical identity"
    )
  }

  func urlSession(
    _ session: URLSession,
    dataTask: URLSessionDataTask,
    didReceive response: URLResponse,
    completionHandler: @escaping (URLSession.ResponseDisposition) -> Void
  ) {
    guard !isCancelled() else {
      reject(
        dataTask,
        completionHandler: completionHandler,
        message: "ARTIFACT_CANCELLED: firmware artifact download was cancelled"
      )
      return
    }
    guard let httpResponse = response as? HTTPURLResponse else {
      reject(
        dataTask,
        completionHandler: completionHandler,
        message: "Firmware response is not HTTP"
      )
      return
    }
    guard
      let responseURL = httpResponse.url,
      responseURL.scheme?.lowercased() == "https",
      responseURL.host?.lowercased() == hostname.lowercased(),
      responseURL.port == nil || responseURL.port == 443
    else {
      reject(
        dataTask,
        completionHandler: completionHandler,
        message:
          "ARTIFACT_REDIRECT_REJECTED: firmware response changed canonical identity"
      )
      return
    }
    guard httpResponse.statusCode == 200 || httpResponse.statusCode == 206 else {
      reject(
        dataTask,
        completionHandler: completionHandler,
        message:
          "ARTIFACT_HTTP_\(httpResponse.statusCode): firmware request failed"
      )
      return
    }

    let append = resumeOffset > 0 && httpResponse.statusCode == 206
    if httpResponse.statusCode == 206 {
      guard
        let contentRange = httpResponse.value(
          forHTTPHeaderField: "Content-Range"
        ),
        firmwareArtifactContentRangeIsValid(
          contentRange,
          expectedStart: append ? resumeOffset : 0,
          expectedTotal: expectedSize
        )
      else {
        reject(
          dataTask,
          completionHandler: completionHandler,
          message:
            "ARTIFACT_PROTOCOL_INVALID: firmware resume Content-Range is invalid"
        )
        return
      }
    }

    let baseOffset = append ? resumeOffset : 0
    guard
      firmwareArtifactResponseFits(
        expectedContentLength: httpResponse.expectedContentLength,
        baseOffset: baseOffset,
        maxBytes: maxBytes
      )
    else {
      reject(
        dataTask,
        completionHandler: completionHandler,
        message:
          "ARTIFACT_PROTOCOL_INVALID: firmware artifact exceeds maxBytes"
      )
      return
    }

    do {
      let handle = try FileHandle(forWritingTo: partialURL)
      if append {
        try handle.seekToEnd()
      } else {
        try handle.truncate(atOffset: 0)
      }
      self.handle = handle
      written = baseOffset
      responseAccepted = true
      completionHandler(.allow)
    } catch {
      reject(
        dataTask,
        completionHandler: completionHandler,
        message:
          "ARTIFACT_NETWORK_FAILED: firmware partial file could not be opened"
      )
    }
  }

  func urlSession(
    _ session: URLSession,
    dataTask: URLSessionDataTask,
    didReceive data: Data
  ) {
    guard responseAccepted, let handle else {
      fail(
        dataTask,
        message:
          "ARTIFACT_PROTOCOL_INVALID: firmware response stream was not accepted"
      )
      return
    }
    guard !isCancelled() else {
      fail(
        dataTask,
        message: "ARTIFACT_CANCELLED: firmware artifact download was cancelled"
      )
      return
    }
    guard
      firmwareArtifactResponseFits(
        expectedContentLength: Int64(data.count),
        baseOffset: written,
        maxBytes: maxBytes
      )
    else {
      fail(
        dataTask,
        message:
          "ARTIFACT_PROTOCOL_INVALID: firmware artifact exceeds maxBytes"
      )
      return
    }
    do {
      try handle.write(contentsOf: data)
      written += Int64(data.count)
    } catch {
      fail(
        dataTask,
        message:
          "ARTIFACT_NETWORK_FAILED: firmware response stream could not be persisted"
      )
    }
  }

  func urlSession(
    _ session: URLSession,
    task: URLSessionTask,
    didCompleteWithError error: Error?
  ) {
    if let error {
      finish(.failure(error))
      return
    }
    guard responseAccepted, let handle else {
      finish(
        .failure(FirmwareArtifactStoreError.downloadFailed(
          "ARTIFACT_PROTOCOL_INVALID: firmware response stream was not accepted"
        ))
      )
      return
    }
    do {
      try handle.synchronize()
      finish(.success(()))
    } catch {
      finish(
        .failure(FirmwareArtifactStoreError.downloadFailed(
          "ARTIFACT_NETWORK_FAILED: firmware partial file could not be synchronized"
        ))
      )
    }
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
  private static let defaultDownloadDeadline: TimeInterval = 180
  private static let maxDownloadDeadline: TimeInterval = 24 * 60 * 60
  private static let finalArtifactGrace: TimeInterval = 24 * 60 * 60
  private static let partialArtifactGrace: TimeInterval = 7 * 24 * 60 * 60

  private struct OpenReader {
    let handle: FileHandle
    let fileURL: URL
    let size: Int64
  }

  private struct LeaseState {
    let transactionId: String
    var artifactRefs: Set<String>
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
  private var leases: [String: LeaseState] = [:]

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
    let deadline = params.overallDeadlineSeconds ?? defaultDownloadDeadline
    guard
      deadline.isFinite,
      deadline > 0,
      deadline <= maxDownloadDeadline
    else {
      throw FirmwareArtifactStoreError.invalidInput("Invalid firmware download deadline")
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
    let key = firmwareArtifactDownloadKey(
      transactionId: params.transactionId,
      expectedSize: Int64(params.expectedSize),
      expectedSha256: expectedSha256
    )
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
      try rejectIfCancelled(transactionId: params.transactionId)
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
      firmwareArtifactPartialFileName(
        transactionId: params.transactionId,
        taskId: params.taskId,
        expectedSha256: expectedSha256
      ),
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
    transactionId: String,
    expectedSize: Int64,
    expectedSha256: String
  ) throws -> StoredFirmwareArtifact {
    let normalizedExpectedSha256 = expectedSha256.lowercased()
    try rejectIfCancelled(transactionId: transactionId)
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
    let isRetained = leases.values.contains {
      $0.artifactRefs.contains(artifactRef)
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
    guard leases.count < 32 else {
      throw FirmwareArtifactStoreError.invalidInput(
        "Too many firmware artifact leases"
      )
    }
    let leaseRef = "fwlease:\(UUID().uuidString.lowercased())"
    leases[leaseRef] = LeaseState(
      transactionId: transactionId,
      artifactRefs: []
    )
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
      guard
        let lease = leases.removeValue(
          forKey: try validateLeaseRef(leaseRef)
        )
      else {
        throw FirmwareArtifactStoreError.invalidInput(
          "Firmware artifact lease is unavailable"
        )
      }
      return lease.transactionId
    }()
    cancellationLock.withFirmwareArtifactLock {
      cancelledTransactions.remove(transactionId)
    }
  }

  func sweepOrphans() throws -> (deletedFiles: Int, deletedBytes: Int64) {
    leaseLock.lock()
    let retained = Set(
      leases.values
        .flatMap(\.artifactRefs)
        .map { String($0.dropFirst(3)) }
    )
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
      guard name.count >= 64 else { continue }
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
    guard leases[try validateLeaseRef(leaseRef)] != nil else {
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
    let validatedLeaseRef = try validateLeaseRef(leaseRef)
    guard var lease = leases[validatedLeaseRef] else {
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
      leases[validatedLeaseRef] = lease
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
    do {
      try await FirmwareArtifactWallClockDeadline.run(
        timeoutSeconds:
          params.overallDeadlineSeconds ?? Self.defaultDownloadDeadline
      ) { [self] in
        try await streamDownloadWithinDeadline(
          params,
          partialURL: partialURL,
          resumeOffset: resumeOffset
        )
      }
    } catch FirmwareArtifactDeadlineError.exceeded {
      throw FirmwareArtifactStoreError.downloadFailed(
        "ARTIFACT_DEADLINE_EXCEEDED: firmware download exceeded its deadline"
      )
    }
  }

  private func streamDownloadWithinDeadline(
    _ params: FirmwareArtifactDownloadParams,
    partialURL: URL,
    resumeOffset: Int64
  ) async throws {
    guard let url = URL(string: params.url), let hostname = url.host else {
      throw FirmwareArtifactStoreError.invalidInput("Invalid firmware URL")
    }
    var request = URLRequest(url: url)
    request.cachePolicy = .reloadIgnoringLocalCacheData
    request.timeoutInterval =
      params.overallDeadlineSeconds ?? Self.defaultDownloadDeadline
    request.setValue("identity", forHTTPHeaderField: "Accept-Encoding")
    if resumeOffset > 0 {
      request.setValue("bytes=\(resumeOffset)-", forHTTPHeaderField: "Range")
    }

    let streamDelegate = FirmwareArtifactStreamDelegate(
      partialURL: partialURL,
      hostname: hostname,
      resumeOffset: resumeOffset,
      expectedSize: Int64(params.expectedSize),
      maxBytes: Int64(params.maxBytes),
      isCancelled: { [weak self] in
        Task.isCancelled ||
          self?.isTransactionCancelled(params.transactionId) == true
      }
    )
    let pinnedSession: SniConnectPinnedSession?
    if params.routeType == "pinnedIp" {
      pinnedSession = try SniConnectPinnedTransport.makeSession(
          hostname: hostname,
          ip: params.resolvedIp!,
          dataDelegate: streamDelegate
        )
    } else {
      pinnedSession = nil
    }
    let session = pinnedSession?.session ?? URLSession(
      configuration: .ephemeral,
      delegate: streamDelegate,
      delegateQueue: nil
    )
    defer {
      if let pinnedSession {
        pinnedSession.close()
      } else {
        session.finishTasksAndInvalidate()
      }
    }

    do {
      try await streamDelegate.run(session: session, request: request)
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
      if isFirmwareArtifactTLSError(error) {
        throw FirmwareArtifactStoreError.downloadFailed(
          "ARTIFACT_TLS_FAILED: firmware TLS validation failed"
        )
      }
      throw FirmwareArtifactStoreError.downloadFailed(
        "ARTIFACT_NETWORK_FAILED: firmware request failed"
      )
    }
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
