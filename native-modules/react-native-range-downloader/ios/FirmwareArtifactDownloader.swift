import Foundation

enum FirmwareArtifactTransferError: Error, LocalizedError {
  case invalidRequest
  case protocolInvalid(String)
  case http(Int)
  case redirectRejected
  case sizeLimitExceeded
  case sizeMismatch
  case shortBody
  case networkFailed
  case tlsFailed
  case transportFailed
  case busy
  case cancelled
  case deadlineExceeded
  case objectChanged
  case workerStopTimeout
  case reprobeRequired(statusCode: Int, discardPartial: Bool)

  var code: String {
    switch self {
    case .invalidRequest:
      return "ARTIFACT_INVALID_REQUEST"
    case .protocolInvalid:
      return "ARTIFACT_PROTOCOL_INVALID"
    case .http(let status):
      return "ARTIFACT_HTTP_\(status)"
    case .redirectRejected:
      return "ARTIFACT_REDIRECT_REJECTED"
    case .sizeLimitExceeded:
      return "ARTIFACT_SIZE_LIMIT_EXCEEDED"
    case .sizeMismatch:
      return "ARTIFACT_SIZE_MISMATCH"
    case .shortBody:
      return "ARTIFACT_SHORT_BODY"
    case .networkFailed:
      return "ARTIFACT_NETWORK_FAILED"
    case .tlsFailed:
      return "ARTIFACT_TLS_FAILED"
    case .transportFailed:
      return "ARTIFACT_TRANSPORT_FAILED"
    case .busy:
      return "ARTIFACT_BUSY"
    case .cancelled:
      return "ARTIFACT_CANCELLED"
    case .deadlineExceeded:
      return "ARTIFACT_DEADLINE_EXCEEDED"
    case .objectChanged:
      return "ARTIFACT_OBJECT_CHANGED"
    case .workerStopTimeout:
      return "ARTIFACT_WORKER_STOP_TIMEOUT"
    case .reprobeRequired(let statusCode, _):
      return "ARTIFACT_REPROBE_REQUIRED_\(statusCode)"
    }
  }

  var retryable: Bool {
    switch self {
    case .http(let status):
      return isRetryableFirmwareArtifactHTTPStatus(status)
    case .shortBody, .networkFailed, .busy, .cancelled, .objectChanged,
         .workerStopTimeout, .reprobeRequired:
      return true
    default:
      return false
    }
  }

  var discardStaging: Bool {
    switch self {
    case .invalidRequest, .protocolInvalid, .redirectRejected,
         .sizeLimitExceeded, .sizeMismatch:
      return true
    case .http(let status):
      return !retryable && status != 408
    case .objectChanged:
      return true
    case .reprobeRequired(_, let discardPartial):
      return discardPartial
    case .shortBody, .networkFailed, .tlsFailed, .transportFailed,
         .busy, .cancelled, .deadlineExceeded, .workerStopTimeout:
      return false
    }
  }

  var requiresReprobe: Bool {
    if case .reprobeRequired = self {
      return true
    }
    return false
  }

  var errorDescription: String? {
    return code
  }

}

struct FirmwareArtifactValidatedRequest {
  let taskId: String
  let leaseRef: String
  let artifactId: String
  let url: URL
  let hostname: String
  let route: StoredFirmwareArtifactRoute
  let resolvedIP: String?
  let expectedSize: Int64
  let expectedSha256: String
  let maxBytes: Int64
  let segmentCount: Int
  let deadlineAt: TimeInterval
}

public final class FirmwareArtifactDownloader {
  static let shared = FirmwareArtifactDownloader(
    store: .shared,
    registry: .shared
  )

  struct Probe {
    let supportsRange: Bool
    let total: Int64
    let strongETag: String?
    let lastModified: String?
  }

  private let store: FirmwareArtifactStore
  private let registry: FirmwareArtifactRegistry
  private let domainCoordinator: FirmwareDomainBackgroundCoordinator

  init(
    store: FirmwareArtifactStore,
    registry: FirmwareArtifactRegistry
  ) {
    self.store = store
    self.registry = registry
    self.domainCoordinator = FirmwareDomainBackgroundCoordinator.shared
  }

  public static func attachBackgroundSessionEvents(
    identifier: String,
    completionHandler: @escaping () -> Void
  ) {
    FirmwareBackgroundSessionEventInbox.shared.receive(
      identifier: identifier,
      completionHandler: completionHandler
    )
    if identifier == FirmwareDomainBackgroundCoordinator.sessionIdentifier {
      FirmwareDomainBackgroundCoordinator.shared.attach()
    }
  }

  func download(
    _ request: FirmwareArtifactValidatedRequest
  ) async throws -> StoredFirmwareArtifactMetadata {
    let fingerprint = StoredFirmwareArtifactMetadata.sha256([
      request.artifactId,
      request.url.absoluteString,
      request.route.rawValue,
      String(request.expectedSize),
      request.expectedSha256,
      String(request.maxBytes),
    ].joined(separator: "\u{0}"))
    return try await registry.runOrAttach(
      leaseRef: request.leaseRef,
      taskId: request.taskId,
      artifactId: request.artifactId,
      fingerprint: fingerprint,
      expectedSize: request.expectedSize
    ) { [self] update in
      try await performDownload(request, update: update)
    }
  }

  func status(
    leaseRef: String,
    taskId: String
  ) throws -> FirmwareArtifactRegistrySnapshot {
    if let status = registry.status(leaseRef: leaseRef, taskId: taskId) {
      return status
    }
    guard let metadata = try store.findArtifactForTask(
      leaseRef: leaseRef,
      taskId: taskId
    ) else {
      return FirmwareArtifactRegistrySnapshot(
        state: .notFound,
        downloadedBytes: 0,
        expectedSize: 0,
        artifact: nil,
        errorCode: "ARTIFACT_NOT_FOUND",
        errorMessage: nil,
        retryable: false
      )
    }
    let finalURL = try store.finalFileURL(for: metadata.artifactRef)
    let completed = metadata.actualSize == metadata.expectedSize &&
      metadata.immutableToken != nil &&
      FileManager.default.fileExists(atPath: finalURL.path)
    return FirmwareArtifactRegistrySnapshot(
      state: completed ? .completed : .failed,
      downloadedBytes: try store.downloadedBytes(metadata.artifactRef),
      expectedSize: metadata.expectedSize,
      artifact: completed ? metadata : nil,
      errorCode: completed ? nil : "ARTIFACT_RESUME_REQUIRED",
      errorMessage: completed
        ? nil
        : "Firmware artifact is durable but has no active worker",
      retryable: completed ? nil : true
    )
  }

  func cancel(leaseRef: String, taskId: String) async {
    registry.cancel(leaseRef: leaseRef, taskId: taskId)
    await domainCoordinator.cancel(leaseRef: leaseRef, taskId: taskId)
  }

  private func performDownload(
    _ initialRequest: FirmwareArtifactValidatedRequest,
    update: @escaping (FirmwareArtifactRegistryState, Int64) -> Void
  ) async throws -> StoredFirmwareArtifactMetadata {
    let attempt = try store.beginTransferAttempt(
      leaseRef: initialRequest.leaseRef,
      taskId: initialRequest.taskId,
      artifactId: initialRequest.artifactId,
      canonicalURL: initialRequest.url.absoluteString,
      hostname: initialRequest.hostname,
      route: initialRequest.route,
      expectedSize: initialRequest.expectedSize,
      expectedSha256: initialRequest.expectedSha256,
      initialDeadlineAt: initialRequest.deadlineAt,
      maxRunAttempts: Self.maxRunAttempts
    )
    let supersededTransfers = try store.supersededTransfers(
      leaseRef: initialRequest.leaseRef,
      taskId: initialRequest.taskId,
      keepingArtifactRef: attempt.artifactRef
    )
    for superseded in supersededTransfers where superseded.route == .domain {
      let selector = FirmwareArtifactTransferSelector(
        leaseRef: initialRequest.leaseRef,
        taskId: initialRequest.taskId,
        artifactRef: superseded.artifactRef,
        generation: superseded.transferGeneration
      )
      try await domainCoordinator.retireAndCancel(selector)
      domainCoordinator.finishRetiring(selector)
    }
    guard let deadlineAt = attempt.transferDeadlineAt else {
      throw FirmwareArtifactStoreError.invalidMetadata
    }
    let request = FirmwareArtifactValidatedRequest(
      taskId: initialRequest.taskId,
      leaseRef: initialRequest.leaseRef,
      artifactId: initialRequest.artifactId,
      url: initialRequest.url,
      hostname: initialRequest.hostname,
      route: initialRequest.route,
      resolvedIP: initialRequest.resolvedIP,
      expectedSize: initialRequest.expectedSize,
      expectedSha256: initialRequest.expectedSha256,
      maxBytes: initialRequest.maxBytes,
      segmentCount: initialRequest.segmentCount,
      deadlineAt: deadlineAt
    )
    try Self.checkDeadline(request)
    let pinnedSessionLease: FirmwarePinnedSessionLease?
    switch request.route {
    case .domain:
      pinnedSessionLease = nil
    case .pinnedIp:
      guard let resolvedIP = request.resolvedIP else {
        throw FirmwareArtifactTransferError.invalidRequest
      }
      pinnedSessionLease = try await FirmwarePinnedTransport.acquireSession(
        hostname: request.hostname,
        resolvedIP: resolvedIP,
        deadlineAt: request.deadlineAt
      )
    }
    defer { pinnedSessionLease?.release() }

    var prefetchedProbe: Probe?
    var reprobeCount = 0

    while true {
      try Task.checkCancellation()
      try Self.checkDeadline(request)
      let currentProbe: Probe
      if let cachedProbe = prefetchedProbe {
        currentProbe = cachedProbe
        prefetchedProbe = nil
      } else {
        currentProbe = try await probe(
          request,
          pinnedSession: pinnedSessionLease?.session
        )
      }
      guard currentProbe.total == request.expectedSize else {
        throw FirmwareArtifactTransferError.sizeMismatch
      }
      let ranges = currentProbe.supportsRange
        ? RangeDownloadLogic.planRanges(
            total: request.expectedSize,
            segments: request.segmentCount
          )
        : [(start: Int64(0), end: request.expectedSize - 1)]
      let preparation = try store.planTransferPreparation(
        artifactRef: attempt.artifactRef,
        leaseRef: request.leaseRef,
        taskId: request.taskId,
        artifactId: request.artifactId,
        canonicalURL: request.url.absoluteString,
        hostname: request.hostname,
        route: request.route,
        expectedSize: request.expectedSize,
        expectedSha256: request.expectedSha256,
        strongETag: currentProbe.strongETag,
        lastModified: currentProbe.lastModified,
        ranges: ranges,
        usesRange: currentProbe.supportsRange
      )
      let replacedSelector: FirmwareArtifactTransferSelector?
      if preparation.requiresGenerationReplacement,
         preparation.currentRoute == .domain {
        let selector = FirmwareArtifactTransferSelector(
          leaseRef: request.leaseRef,
          taskId: request.taskId,
          artifactRef: attempt.artifactRef,
          generation: preparation.currentGeneration
        )
        try await domainCoordinator.retireAndCancel(selector)
        replacedSelector = selector
      } else {
        replacedSelector = nil
      }
      let metadata = try store.prepareArtifactForDownload(
        preparation: preparation,
        artifactRef: attempt.artifactRef,
        leaseRef: request.leaseRef,
        taskId: request.taskId,
        artifactId: request.artifactId,
        canonicalURL: request.url.absoluteString,
        hostname: request.hostname,
        route: request.route,
        expectedSize: request.expectedSize,
        expectedSha256: request.expectedSha256,
        strongETag: currentProbe.strongETag,
        lastModified: currentProbe.lastModified,
        ranges: ranges,
        usesRange: currentProbe.supportsRange
      )
      if let replacedSelector {
        domainCoordinator.finishRetiring(replacedSelector)
      }
      if metadata.actualSize == request.expectedSize,
         let actualSha256 = metadata.actualSha256,
         RangeDownloadLogic.secureHexEquals(
           actualSha256,
           request.expectedSha256
         ),
         metadata.immutableToken != nil,
         FileManager.default.fileExists(
           atPath: try store.finalFileURL(for: metadata.artifactRef).path
         ) {
        return metadata
      }

      let activity = try store.beginActivity(
        artifactRef: metadata.artifactRef,
        kind: .worker,
        cancel: { [weak self] in
          guard let self else { return }
          self.registry.cancel(
            leaseRef: request.leaseRef,
            taskId: request.taskId
          )
          Task {
            await self.domainCoordinator.cancel(
              leaseRef: request.leaseRef,
              taskId: request.taskId
            )
          }
        }
      )

      do {
        update(.downloading, try store.downloadedBytes(metadata.artifactRef))
        switch request.route {
        case .domain:
          try await domainCoordinator.download(
            request: request,
            metadata: metadata,
            probe: currentProbe,
            update: update
          )
        case .pinnedIp:
          guard let pinnedSession = pinnedSessionLease?.session else {
            throw FirmwareArtifactTransferError.invalidRequest
          }
          try await downloadPinned(
            request: request,
            metadata: metadata,
            probe: currentProbe,
            session: pinnedSession,
            update: update
          )
        }
        try Task.checkCancellation()
        try Self.checkDeadline(request)
        try assembleSegments(request: request, metadata: metadata)
        try Self.checkDeadline(request)
        update(.verifying, request.expectedSize)
        let promoted = try store.promoteVerifiedStaging(
          artifactRef: metadata.artifactRef,
          maxBytes: request.maxBytes
        )
        try store.completeTransferCleanup(artifactRef: metadata.artifactRef)
        activity.release()
        return promoted
      } catch {
        defer { activity.release() }
        if error is CancellationError {
          throw FirmwareArtifactTransferError.cancelled
        }
        guard let transferError = Self.classifyTransportError(error) else {
          throw error
        }
        if case .reprobeRequired(let statusCode, let discardPartial) =
          transferError {
          if request.route == .domain {
            try await domainCoordinator.cancelAndWait(
              leaseRef: request.leaseRef,
              taskId: request.taskId
            )
          }
          reprobeCount += 1
          if reprobeCount > Self.maxObjectReprobes {
            if discardPartial {
              try? store.clearTransferStaging(
                artifactRef: metadata.artifactRef
              )
            }
            if statusCode == 416 {
              throw FirmwareArtifactTransferError.http(416)
            }
            throw FirmwareArtifactTransferError.objectChanged
          }
          let refreshedProbe = try await probe(
            request,
            pinnedSession: pinnedSessionLease?.session
          )
          if discardPartial ||
              !sameObjectIdentity(currentProbe, refreshedProbe) {
            try store.clearTransferStaging(artifactRef: metadata.artifactRef)
          }
          prefetchedProbe = refreshedProbe
          continue
        }
        if transferError.discardStaging {
          if request.route == .domain {
            try await domainCoordinator.cancelAndWait(
              leaseRef: request.leaseRef,
              taskId: request.taskId
            )
          }
          try? store.clearTransferStaging(artifactRef: metadata.artifactRef)
        }
        throw transferError
      }
    }
  }

  private func probe(
    _ request: FirmwareArtifactValidatedRequest,
    pinnedSession: URLSession?
  ) async throws -> Probe {
    try Self.checkDeadline(request)
    var urlRequest = URLRequest(url: request.url)
    urlRequest.httpMethod = "GET"
    urlRequest.timeoutInterval = try Self.remainingDeadline(request)
    urlRequest.setValue("identity", forHTTPHeaderField: "Accept-Encoding")
    urlRequest.setValue("bytes=0-0", forHTTPHeaderField: "Range")

    do {
      switch request.route {
      case .domain:
        let delegate = FirmwareSameOriginRedirectDelegate(
          canonicalHostname: request.hostname
        )
        let configuration = URLSessionConfiguration.ephemeral
        configuration.requestCachePolicy = .reloadIgnoringLocalCacheData
        configuration.urlCache = nil
        let session = URLSession(
          configuration: configuration,
          delegate: delegate,
          delegateQueue: nil
        )
        defer { session.invalidateAndCancel() }
        return try await readProbe(
          session: session,
          request: urlRequest,
          deadlineAt: request.deadlineAt
        )
      case .pinnedIp:
        guard let pinnedSession else {
          throw FirmwareArtifactTransferError.invalidRequest
        }
        return try await readProbe(
          session: pinnedSession,
          request: urlRequest,
          deadlineAt: request.deadlineAt
        )
      }
    } catch {
      if let transferError = Self.classifyTransportError(error) {
        throw transferError
      }
      throw error
    }
  }

  private func readProbe(
    session: URLSession,
    request: URLRequest,
    deadlineAt: TimeInterval
  ) async throws -> Probe {
    let (bytes, response) = try await session.bytes(for: request)
    try Self.checkDeadline(deadlineAt: deadlineAt)
    guard let http = response as? HTTPURLResponse else {
      throw FirmwareArtifactTransferError.protocolInvalid(
        "Probe returned no HTTP response"
      )
    }
    switch http.statusCode {
    case 206:
      try rejectMultipart(http)
      guard let parsed = Self.parseContentRange(
        http.value(forHTTPHeaderField: "Content-Range")
      ), parsed.start == 0, parsed.end == 0 else {
        throw FirmwareArtifactTransferError.protocolInvalid(
          "Probe returned a mismatched Content-Range"
        )
      }
      var count = 0
      for try await _ in bytes {
        try Self.checkDeadline(deadlineAt: deadlineAt)
        count += 1
        if count > 1 {
          throw FirmwareArtifactTransferError.protocolInvalid(
            "Probe body exceeded bytes=0-0"
          )
        }
      }
      guard count == 1 else {
        throw FirmwareArtifactTransferError.shortBody
      }
      return Probe(
        supportsRange: true,
        total: parsed.total,
        strongETag: StoredFirmwareArtifactMetadata.normalizeStrongETag(
          http.value(forHTTPHeaderField: "ETag")
        ),
        lastModified: http.value(forHTTPHeaderField: "Last-Modified")
      )
    case 200:
      guard let totalText = http.value(forHTTPHeaderField: "Content-Length"),
            let total = Int64(totalText) else {
        throw FirmwareArtifactTransferError.protocolInvalid(
          "Non-range probe omitted Content-Length"
        )
      }
      return Probe(
        supportsRange: false,
        total: total,
        strongETag: StoredFirmwareArtifactMetadata.normalizeStrongETag(
          http.value(forHTTPHeaderField: "ETag")
        ),
        lastModified: http.value(forHTTPHeaderField: "Last-Modified")
      )
    default:
      throw FirmwareArtifactTransferError.http(http.statusCode)
    }
  }

  private func downloadPinned(
    request: FirmwareArtifactValidatedRequest,
    metadata: StoredFirmwareArtifactMetadata,
    probe: Probe,
    session: URLSession,
    update: @escaping (FirmwareArtifactRegistryState, Int64) -> Void
  ) async throws {
    let counter = FirmwareProgressCounter(
      initialValue: try store.downloadedBytes(metadata.artifactRef)
    )
    try await withThrowingTaskGroup(of: Void.self) { group in
      do {
        for segment in metadata.segments {
          group.addTask { [self] in
            let destination = try store.segmentFileURL(
              for: metadata.artifactRef,
              index: segment.index
            )
            let segmentLength = segment.end - segment.start + 1
            let existing = destination.fileSize
            if existing > segmentLength {
              try FileManager.default.removeItem(at: destination)
            }
            let current = destination.fileSize
            if current == segmentLength { return }

            var urlRequest = URLRequest(url: request.url)
            urlRequest.httpMethod = "GET"
            urlRequest.timeoutInterval = try Self.remainingDeadline(request)
            urlRequest.setValue("identity", forHTTPHeaderField: "Accept-Encoding")
            if probe.supportsRange {
              urlRequest.setValue(
                "bytes=\(segment.start + current)-\(segment.end)",
                forHTTPHeaderField: "Range"
              )
              if let validator = probe.strongETag ?? probe.lastModified {
                urlRequest.setValue(validator, forHTTPHeaderField: "If-Range")
              }
            }
            let response = try await FirmwarePinnedDownloadDelegate.stream(
              session: session,
              request: urlRequest,
              destination: destination,
              append: probe.supportsRange && current > 0,
              maxBytes: segmentLength,
              deadlineAt: request.deadlineAt,
              validateResponse: { response in
                if probe.supportsRange {
                  if response.statusCode == 200 ||
                      response.statusCode == 416 {
                    throw FirmwareArtifactTransferError.reprobeRequired(
                      statusCode: response.statusCode,
                      discardPartial: response.statusCode == 200
                    )
                  }
                  guard response.statusCode == 206,
                        let parsed = Self.parseContentRange(
                          response.value(
                            forHTTPHeaderField: "Content-Range"
                          )
                        ),
                        parsed.start == segment.start + current,
                        parsed.end == segment.end,
                        parsed.total == request.expectedSize else {
                    if response.statusCode != 206 {
                      throw FirmwareArtifactTransferError.http(
                        response.statusCode
                      )
                    }
                    throw FirmwareArtifactTransferError.protocolInvalid(
                      "Pinned segment returned a mismatched Content-Range"
                    )
                  }
                  try rejectMultipart(response)
                } else if response.statusCode != 200 {
                  throw FirmwareArtifactTransferError.http(response.statusCode)
                }
              }
            ) { delta in
              let downloaded = counter.add(delta)
              update(.downloading, downloaded)
            }
            _ = response
            guard destination.fileSize == segmentLength else {
              throw FirmwareArtifactTransferError.shortBody
            }
          }
        }
        try await group.waitForAll()
      } catch {
        group.cancelAll()
        throw error
      }
    }
  }

  private func assembleSegments(
    request: FirmwareArtifactValidatedRequest,
    metadata: StoredFirmwareArtifactMetadata
  ) throws {
    let staging = try store.partialFileURL(for: metadata.artifactRef)
    FileManager.default.createFile(atPath: staging.path, contents: nil)
    let output = try FileHandle(forWritingTo: staging)
    defer { try? output.close() }
    var written: Int64 = 0
    for segment in metadata.segments.sorted(by: { $0.index < $1.index }) {
      let inputURL = try store.segmentFileURL(
        for: metadata.artifactRef,
        index: segment.index
      )
      let input = try FileHandle(forReadingFrom: inputURL)
      defer { try? input.close() }
      while true {
        try Self.checkDeadline(request)
        let data = try input.read(upToCount: 64 * 1024) ?? Data()
        if data.isEmpty { break }
        written += Int64(data.count)
        guard written <= request.maxBytes,
              written <= request.expectedSize else {
          throw FirmwareArtifactTransferError.sizeLimitExceeded
        }
        try output.write(contentsOf: data)
      }
      try input.close()
    }
    try output.synchronize()
    guard written == request.expectedSize else {
      throw FirmwareArtifactTransferError.sizeMismatch
    }
  }

  static func validate(
    _ params: FirmwareArtifactDownloadParams
  ) throws -> FirmwareArtifactValidatedRequest {
    guard let url = URL(string: params.url),
          let components = URLComponents(url: url, resolvingAgainstBaseURL: false),
          components.scheme?.lowercased() == "https",
          let hostname = components.host?.lowercased(),
          !hostname.isEmpty,
          components.user == nil,
          components.password == nil,
          components.port == nil || components.port == 443,
          !params.taskId.isEmpty,
          !params.artifactId.isEmpty,
          params.artifactId.utf8.count <= 256 else {
      throw FirmwareArtifactTransferError.invalidRequest
    }
    let route: StoredFirmwareArtifactRoute
    switch params.routeType {
    case "domain":
      guard params.resolvedIp == nil else {
        throw FirmwareArtifactTransferError.invalidRequest
      }
      route = .domain
    case "pinnedIp":
      guard let resolvedIP = params.resolvedIp,
            FirmwarePinnedRouteValidation.isValid(
              hostname: hostname,
              resolvedIP: resolvedIP
            ) else {
        throw FirmwareArtifactTransferError.invalidRequest
      }
      route = .pinnedIp
    default:
      throw FirmwareArtifactTransferError.invalidRequest
    }
    let expectedSize = try exactPositiveInt64(params.expectedSize)
    let maxBytes = try exactPositiveInt64(params.maxBytes)
    guard maxBytes >= expectedSize,
          expectedSize <= maximumArtifactBytes,
          maxBytes <= maximumArtifactBytes,
          StoredFirmwareArtifactMetadata.isTrustedSha256(
            params.expectedSha256
          ) else {
      throw FirmwareArtifactTransferError.invalidRequest
    }
    let segmentCount = try params.segmentCount.map(exactPositiveInt64)
      .map(Int.init) ?? 8
    guard (1...32).contains(segmentCount) else {
      throw FirmwareArtifactTransferError.invalidRequest
    }
    let deadlineSeconds = params.overallDeadlineSeconds ?? defaultDeadlineSeconds
    guard deadlineSeconds.isFinite,
          deadlineSeconds > 0,
          deadlineSeconds <= maximumDeadlineSeconds else {
      throw FirmwareArtifactTransferError.invalidRequest
    }
    let deadlineAt = Date().timeIntervalSince1970 + deadlineSeconds
    return FirmwareArtifactValidatedRequest(
      taskId: params.taskId,
      leaseRef: params.leaseRef,
      artifactId: params.artifactId,
      url: url,
      hostname: hostname,
      route: route,
      resolvedIP: params.resolvedIp,
      expectedSize: expectedSize,
      expectedSha256: params.expectedSha256.lowercased(),
      maxBytes: maxBytes,
      segmentCount: segmentCount,
      deadlineAt: deadlineAt
    )
  }

  static func parseContentRange(
    _ value: String?
  ) -> (start: Int64, end: Int64, total: Int64)? {
    guard let value else { return nil }
    let pattern = #"^bytes\s+(\d+)-(\d+)/(\d+)$"#
    guard let expression = try? NSRegularExpression(pattern: pattern),
          let match = expression.firstMatch(
            in: value,
            range: NSRange(value.startIndex..., in: value)
          ),
          match.numberOfRanges == 4,
          let startRange = Range(match.range(at: 1), in: value),
          let endRange = Range(match.range(at: 2), in: value),
          let totalRange = Range(match.range(at: 3), in: value),
          let start = Int64(value[startRange]),
          let end = Int64(value[endRange]),
          let total = Int64(value[totalRange]),
          start <= end,
          end < total else {
      return nil
    }
    return (start, end, total)
  }

  private func sameObjectIdentity(_ left: Probe, _ right: Probe) -> Bool {
    return RangeDownloadLogic.sameArtifactObjectIdentity(
      leftSupportsRange: left.supportsRange,
      leftTotal: left.total,
      leftStrongETag: left.strongETag,
      leftLastModified: left.lastModified,
      rightSupportsRange: right.supportsRange,
      rightTotal: right.total,
      rightStrongETag: right.strongETag,
      rightLastModified: right.lastModified
    )
  }

  private func rejectMultipart(_ response: HTTPURLResponse) throws {
    if response.value(forHTTPHeaderField: "Content-Type")?
      .lowercased()
      .hasPrefix("multipart/byteranges") == true {
      throw FirmwareArtifactTransferError.protocolInvalid(
        "Multipart range responses are not accepted"
      )
    }
  }

  private static func exactPositiveInt64(_ value: Double) throws -> Int64 {
    guard value.isFinite,
          value > 0,
          value <= 9_007_199_254_740_991,
          value.rounded(.towardZero) == value else {
      throw FirmwareArtifactTransferError.invalidRequest
    }
    return Int64(value)
  }

  static func classifyTransportError(
    _ error: Error
  ) -> FirmwareArtifactTransferError? {
    if let transferError = error as? FirmwareArtifactTransferError {
      return transferError
    }
    if error is CancellationError {
      return .cancelled
    }
    switch FirmwareArtifactTransportFailureClassifier.classify(error) {
    case .cancelled:
      return .cancelled
    case .network:
      return .networkFailed
    case .tls:
      return .tlsFailed
    case .transport:
      return .transportFailed
    case nil:
      return nil
    }
  }

  private static func remainingDeadline(
    _ request: FirmwareArtifactValidatedRequest
  ) throws -> TimeInterval {
    return try remainingDeadline(deadlineAt: request.deadlineAt)
  }

  private static func remainingDeadline(
    deadlineAt: TimeInterval
  ) throws -> TimeInterval {
    let remaining = deadlineAt - Date().timeIntervalSince1970
    guard remaining > 0 else {
      throw FirmwareArtifactTransferError.deadlineExceeded
    }
    return remaining
  }

  private static func checkDeadline(
    _ request: FirmwareArtifactValidatedRequest
  ) throws {
    try checkDeadline(deadlineAt: request.deadlineAt)
  }

  private static func checkDeadline(deadlineAt: TimeInterval) throws {
    _ = try remainingDeadline(deadlineAt: deadlineAt)
  }

  private static let maxObjectReprobes = 2
  private static let maxRunAttempts = 8
  private static let maximumArtifactBytes: Int64 = 512 * 1024 * 1024
  private static let defaultDeadlineSeconds: TimeInterval = 30 * 60
  private static let maximumDeadlineSeconds: TimeInterval = 24 * 60 * 60
}

private final class FirmwareProgressCounter {
  private let lock = NSLock()
  private var value: Int64

  init(initialValue: Int64) {
    value = initialValue
  }

  func add(_ delta: Int64) -> Int64 {
    return lock.withFirmwareLock {
      value += delta
      return value
    }
  }
}

private final class FirmwareSameOriginRedirectDelegate:
  NSObject,
  URLSessionTaskDelegate
{
  private let canonicalHostname: String

  init(canonicalHostname: String) {
    self.canonicalHostname = canonicalHostname
  }

  func urlSession(
    _ session: URLSession,
    task: URLSessionTask,
    willPerformHTTPRedirection response: HTTPURLResponse,
    newRequest request: URLRequest,
    completionHandler: @escaping (URLRequest?) -> Void
  ) {
    guard let url = request.url,
          url.scheme?.lowercased() == "https",
          url.port == nil || url.port == 443,
          url.host?.lowercased() == canonicalHostname else {
      completionHandler(nil)
      return
    }
    completionHandler(request)
  }
}

final class FirmwareDomainBackgroundCoordinator:
  NSObject,
  URLSessionDownloadDelegate,
  URLSessionDelegate
{
  static let shared = FirmwareDomainBackgroundCoordinator()
  static let sessionIdentifier = "so.onekey.firmware-artifacts.domain.v1"

  private typealias TaskDescriptor =
    FirmwareArtifactBackgroundTaskDescriptor

  private let lock = NSLock()
  private var failures: [
    FirmwareArtifactTransferKey: FirmwareArtifactTransferError
  ] = [:]
  private var retiredTransfers: Set<FirmwareArtifactTransferSelector> = []
  private var activeCallbacks: [FirmwareArtifactTransferSelector: Int] = [:]
  private var quarantinedTaskIdentifiers: Set<Int> = []
  private lazy var session: URLSession = {
    let configuration = URLSessionConfiguration.background(
      withIdentifier: Self.sessionIdentifier
    )
    configuration.isDiscretionary = false
    configuration.sessionSendsLaunchEvents = true
    configuration.waitsForConnectivity = true
    configuration.httpMaximumConnectionsPerHost = 8
    configuration.requestCachePolicy = .reloadIgnoringLocalCacheData
    configuration.urlCache = nil
    return URLSession(
      configuration: configuration,
      delegate: self,
      delegateQueue: nil
    )
  }()

  private override init() {
    super.init()
  }

  func attach() {
    _ = session
  }

  func download(
    request: FirmwareArtifactValidatedRequest,
    metadata: StoredFirmwareArtifactMetadata,
    probe: FirmwareArtifactDownloader.Probe,
    update: @escaping (FirmwareArtifactRegistryState, Int64) -> Void
  ) async throws {
    attach()
    guard let generation = metadata.transferGeneration else {
      throw FirmwareArtifactTransferError.protocolInvalid(
        "Firmware transfer generation is missing"
      )
    }
    let key = FirmwareArtifactTransferKey(
      leaseRef: request.leaseRef,
      taskId: request.taskId,
      artifactRef: metadata.artifactRef,
      generation: generation
    )
    _ = lock.withFirmwareLock {
      failures.removeValue(forKey: key)
    }

    while true {
      try Task.checkCancellation()
      if Date().timeIntervalSince1970 >= request.deadlineAt {
        try await cancelAndWait(
          leaseRef: request.leaseRef,
          taskId: request.taskId
        )
        throw FirmwareArtifactTransferError.deadlineExceeded
      }
      if let failure = lock.withFirmwareLock({
        failures[key]
      }) {
        throw failure
      }
      let allComplete = try metadata.segments.allSatisfy { segment in
        let url = try FirmwareArtifactStore.shared.segmentFileURL(
          for: metadata.artifactRef,
          index: segment.index
        )
        return url.fileSize == segment.end - segment.start + 1
      }
      if allComplete {
        return
      }

      let tasks = await allTasks()
      if Date().timeIntervalSince1970 >= request.deadlineAt {
        try await cancelAndWait(
          leaseRef: request.leaseRef,
          taskId: request.taskId
        )
        throw FirmwareArtifactTransferError.deadlineExceeded
      }
      var liveIndexes = Set<Int>()
      for task in tasks where task.state == .running ||
          task.state == .suspended ||
          task.state == .canceling {
        guard let descriptor = decode(task.taskDescription) else {
          quarantine(task)
          continue
        }
        guard descriptor.leaseRef == request.leaseRef,
              descriptor.taskId == request.taskId else {
          continue
        }
        guard descriptor.transferKey == key,
              FirmwareArtifactStore.shared.isCurrentBackgroundTransfer(
                descriptor,
                validateSegmentOffset: true
              ) else {
          quarantine(task)
          continue
        }
        liveIndexes.insert(descriptor.segmentIndex)
      }
      for segment in metadata.segments where !liveIndexes.contains(segment.index) {
        let destination = try FirmwareArtifactStore.shared.segmentFileURL(
          for: metadata.artifactRef,
          index: segment.index
        )
        let existing = destination.fileSize
        let length = segment.end - segment.start + 1
        if existing >= length { continue }
        var urlRequest = URLRequest(url: request.url)
        urlRequest.httpMethod = "GET"
        urlRequest.timeoutInterval = max(
          0.001,
          request.deadlineAt - Date().timeIntervalSince1970
        )
        urlRequest.setValue("identity", forHTTPHeaderField: "Accept-Encoding")
        if probe.supportsRange {
          urlRequest.setValue(
            "bytes=\(segment.start + existing)-\(segment.end)",
            forHTTPHeaderField: "Range"
          )
          if let validator = probe.strongETag ?? probe.lastModified {
            urlRequest.setValue(validator, forHTTPHeaderField: "If-Range")
          }
        }
        let task = session.downloadTask(with: urlRequest)
        task.taskDescription = encode(TaskDescriptor(
          leaseRef: request.leaseRef,
          taskId: request.taskId,
          artifactRef: metadata.artifactRef,
          transferGeneration: generation,
          segmentIndex: segment.index,
          rangeStart: segment.start + existing,
          rangeEnd: segment.end,
          total: request.expectedSize,
          maxBytes: min(request.maxBytes, length),
          hostname: request.hostname,
          usesRange: probe.supportsRange,
          deadlineAt: request.deadlineAt
        ))
        task.resume()
      }
      update(
        .downloading,
        try FirmwareArtifactStore.shared.downloadedBytes(metadata.artifactRef)
      )
      try await Task.sleep(nanoseconds: 200_000_000)
    }
  }

  func cancel(leaseRef: String, taskId: String) async {
    try? await cancelAndWait(leaseRef: leaseRef, taskId: taskId)
  }

  func cancelAndWait(leaseRef: String, taskId: String) async throws {
    let deadline = Date().addingTimeInterval(Self.workerStopTimeout)
    while true {
      let allSessionTasks = await allTasks()
      let tasks = allSessionTasks.filter { task in
        guard let descriptor = decode(task.taskDescription) else {
          return false
        }
        return descriptor.leaseRef == leaseRef &&
          descriptor.taskId == taskId &&
          task.state != .completed
      }
      if tasks.isEmpty {
        return
      }
      tasks.forEach { $0.cancel() }
      guard Date() < deadline else {
        throw FirmwareArtifactTransferError.workerStopTimeout
      }
      try await Task.sleep(nanoseconds: 50_000_000)
    }
  }

  func retireAndCancel(
    _ selector: FirmwareArtifactTransferSelector
  ) async throws {
    lock.withFirmwareLock {
      retiredTransfers.insert(selector)
      if let generation = selector.generation {
        failures.removeValue(forKey: FirmwareArtifactTransferKey(
          leaseRef: selector.leaseRef,
          taskId: selector.taskId,
          artifactRef: selector.artifactRef,
          generation: generation
        ))
      }
    }
    let deadline = Date().addingTimeInterval(Self.workerStopTimeout)
    while true {
      let allSessionTasks = await allTasks()
      let tasks = allSessionTasks.filter { task in
        guard let descriptor = decode(task.taskDescription) else {
          return false
        }
        return descriptor.transferSelector == selector &&
          task.state != .completed
      }
      tasks.forEach { $0.cancel() }
      let callbackCount = lock.withFirmwareLock {
        activeCallbacks[selector] ?? 0
      }
      if tasks.isEmpty && callbackCount == 0 {
        return
      }
      guard Date() < deadline else {
        throw FirmwareArtifactTransferError.workerStopTimeout
      }
      try await Task.sleep(nanoseconds: 50_000_000)
    }
  }

  func finishRetiring(_ selector: FirmwareArtifactTransferSelector) {
    _ = lock.withFirmwareLock {
      retiredTransfers.remove(selector)
    }
  }

  func urlSession(
    _ session: URLSession,
    downloadTask: URLSessionDownloadTask,
    didFinishDownloadingTo location: URL
  ) {
    guard let descriptor = decode(downloadTask.taskDescription),
          let response = downloadTask.response as? HTTPURLResponse,
          beginCallback(
            taskIdentifier: downloadTask.taskIdentifier,
            descriptor: descriptor,
            validateSegmentOffset: true
          ) else {
      return
    }
    defer { endCallback(descriptor.transferSelector) }
    do {
      guard let deadlineAt = descriptor.deadlineAt,
            Date().timeIntervalSince1970 < deadlineAt else {
        throw FirmwareArtifactTransferError.deadlineExceeded
      }
      if descriptor.usesRange {
        if response.statusCode == 200 || response.statusCode == 416 {
          throw FirmwareArtifactTransferError.reprobeRequired(
            statusCode: response.statusCode,
            discardPartial: response.statusCode == 200
          )
        }
        guard response.statusCode == 206 else {
          throw FirmwareArtifactTransferError.http(response.statusCode)
        }
        guard
              let parsed = FirmwareArtifactDownloader.parseContentRange(
                response.value(forHTTPHeaderField: "Content-Range")
              ),
              parsed.start == descriptor.rangeStart,
              parsed.end == descriptor.rangeEnd,
              parsed.total == descriptor.total else {
          throw FirmwareArtifactTransferError.protocolInvalid(
            "Background segment returned a mismatched Content-Range"
          )
        }
        if response.value(forHTTPHeaderField: "Content-Type")?
          .lowercased()
          .hasPrefix("multipart/byteranges") == true {
          throw FirmwareArtifactTransferError.protocolInvalid(
            "Background segment returned multipart content"
          )
        }
      } else if response.statusCode != 200 {
        throw FirmwareArtifactTransferError.http(response.statusCode)
      }
      let destination = try FirmwareArtifactStore.shared.segmentFileURL(
        for: descriptor.artifactRef,
        index: descriptor.segmentIndex
      )
      try appendDownloadedFile(
        location,
        to: destination,
        maxBytes: descriptor.maxBytes
      )
      guard destination.fileSize == descriptor.maxBytes else {
        throw FirmwareArtifactTransferError.shortBody
      }
      FirmwareArtifactRegistry.shared.update(
        leaseRef: descriptor.leaseRef,
        taskId: descriptor.taskId,
        state: .downloading,
        downloadedBytes: (
          try? FirmwareArtifactStore.shared.downloadedBytes(
            descriptor.artifactRef
          )
        ) ?? 0
      )
    } catch let error as FirmwareArtifactTransferError {
      recordFailure(descriptor, error: error)
    } catch {
      recordFailure(
        descriptor,
        error: .protocolInvalid("Background segment could not be persisted")
      )
    }
  }

  func urlSession(
    _ session: URLSession,
    task: URLSessionTask,
    didCompleteWithError error: Error?
  ) {
    if consumeQuarantinedCompletion(task.taskIdentifier) {
      return
    }
    guard let error,
          let descriptor = decode(task.taskDescription),
          beginCallback(
            taskIdentifier: task.taskIdentifier,
            descriptor: descriptor,
            validateSegmentOffset: false
          ) else {
      return
    }
    defer { endCallback(descriptor.transferSelector) }
    let transferError: FirmwareArtifactTransferError =
      FirmwareArtifactDownloader.classifyTransportError(error) ??
      .transportFailed
    recordFailure(descriptor, error: transferError)
  }

  func urlSession(
    _ session: URLSession,
    task: URLSessionTask,
    willPerformHTTPRedirection response: HTTPURLResponse,
    newRequest request: URLRequest,
    completionHandler: @escaping (URLRequest?) -> Void
  ) {
    guard let descriptor = decode(task.taskDescription),
          beginCallback(
            taskIdentifier: task.taskIdentifier,
            descriptor: descriptor,
            validateSegmentOffset: true
          ) else {
      completionHandler(nil)
      return
    }
    defer { endCallback(descriptor.transferSelector) }
    guard
          let url = request.url,
          url.scheme?.lowercased() == "https",
          url.port == nil || url.port == 443,
          url.host?.lowercased() == descriptor.hostname else {
      completionHandler(nil)
      if let descriptor = decode(task.taskDescription) {
        recordFailure(descriptor, error: .redirectRejected)
      }
      return
    }
    completionHandler(request)
  }

  func urlSessionDidFinishEvents(
    forBackgroundURLSession session: URLSession
  ) {
    guard let identifier = session.configuration.identifier else { return }
    FirmwareBackgroundSessionEventInbox.shared.complete(identifier: identifier)
  }

  private func appendDownloadedFile(
    _ source: URL,
    to destination: URL,
    maxBytes: Int64
  ) throws {
    if !FileManager.default.fileExists(atPath: destination.path) {
      FileManager.default.createFile(atPath: destination.path, contents: nil)
    }
    let input = try FileHandle(forReadingFrom: source)
    let output = try FileHandle(forWritingTo: destination)
    defer {
      try? input.close()
      try? output.close()
    }
    try output.seekToEnd()
    var total = destination.fileSize
    while true {
      let data = try input.read(upToCount: 64 * 1024) ?? Data()
      if data.isEmpty { break }
      total += Int64(data.count)
      guard total <= maxBytes else {
        throw FirmwareArtifactTransferError.sizeLimitExceeded
      }
      try output.write(contentsOf: data)
    }
    try output.synchronize()
  }

  private func allTasks() async -> [URLSessionTask] {
    return await withCheckedContinuation { continuation in
      session.getAllTasks { tasks in
        continuation.resume(returning: tasks)
      }
    }
  }

  private func recordFailure(
    _ descriptor: TaskDescriptor,
    error: FirmwareArtifactTransferError
  ) {
    guard let key = descriptor.transferKey,
          FirmwareArtifactStore.shared.isCurrentBackgroundTransfer(
            descriptor,
            validateSegmentOffset: false
          ) else {
      return
    }
    let inserted: Bool = lock.withFirmwareLock {
      guard !retiredTransfers.contains(descriptor.transferSelector) else {
        return false
      }
      if let existing = failures[key] {
        guard error.requiresReprobe && !existing.requiresReprobe else {
          return false
        }
      }
      failures[key] = error
      return true
    }
    if inserted && !error.requiresReprobe {
      FirmwareArtifactRegistry.shared.fail(
        leaseRef: descriptor.leaseRef,
        taskId: descriptor.taskId,
        error: error
      )
    }
  }

  private func beginCallback(
    taskIdentifier: Int,
    descriptor: TaskDescriptor,
    validateSegmentOffset: Bool
  ) -> Bool {
    let selector = descriptor.transferSelector
    let admitted = lock.withFirmwareLock {
      guard !retiredTransfers.contains(selector),
            !quarantinedTaskIdentifiers.contains(taskIdentifier) else {
        return false
      }
      activeCallbacks[selector, default: 0] += 1
      return true
    }
    guard admitted else {
      return false
    }
    guard FirmwareArtifactStore.shared.isCurrentBackgroundTransfer(
      descriptor,
      validateSegmentOffset: validateSegmentOffset
    ) else {
      endCallback(selector)
      return false
    }
    return true
  }

  private func endCallback(_ selector: FirmwareArtifactTransferSelector) {
    lock.withFirmwareLock {
      guard let count = activeCallbacks[selector] else {
        return
      }
      if count <= 1 {
        activeCallbacks.removeValue(forKey: selector)
      } else {
        activeCallbacks[selector] = count - 1
      }
    }
  }

  private func quarantine(_ task: URLSessionTask) {
    _ = lock.withFirmwareLock {
      quarantinedTaskIdentifiers.insert(task.taskIdentifier)
    }
    task.cancel()
  }

  private func consumeQuarantinedCompletion(_ taskIdentifier: Int) -> Bool {
    return lock.withFirmwareLock {
      quarantinedTaskIdentifiers.remove(taskIdentifier) != nil
    }
  }

  private func encode(_ descriptor: TaskDescriptor) -> String? {
    guard let data = try? JSONEncoder().encode(descriptor) else { return nil }
    return data.base64EncodedString()
  }

  private func decode(_ value: String?) -> TaskDescriptor? {
    guard let value,
          let data = Data(base64Encoded: value) else {
      return nil
    }
    return try? JSONDecoder().decode(TaskDescriptor.self, from: data)
  }

  private static let workerStopTimeout: TimeInterval = 5
}
