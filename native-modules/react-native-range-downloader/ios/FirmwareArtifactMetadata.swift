import CryptoKit
import Foundation

enum StoredFirmwareArtifactRoute: String, Codable, Equatable {
  case domain
  case pinnedIp
}

enum FirmwareArtifactRetentionClass: String, Codable, CaseIterable {
  case partial
  case staging
  case verifiedFinal
  case archive
  case materializedChild
}

enum FirmwareArtifactActivityKind: String {
  case worker
  case reader
  case materializer
}

enum StoredFirmwareLeaseDisposition: String, Codable {
  case completed
  case safeCancelled
  case safeAbandoned
}

struct FirmwareArtifactSegmentMetadata: Codable, Equatable {
  var index: Int
  var start: Int64
  var end: Int64
  var downloadedBytes: Int64
}

struct FirmwareArtifactTransferKey: Hashable {
  let leaseRef: String
  let taskId: String
  let artifactRef: String
  let generation: String
}

struct FirmwareArtifactTransferSelector: Hashable {
  let leaseRef: String
  let taskId: String
  let artifactRef: String
  let generation: String?
}

struct FirmwareArtifactBackgroundTaskDescriptor: Codable, Hashable {
  let leaseRef: String
  let taskId: String
  let artifactRef: String
  let transferGeneration: String?
  let segmentIndex: Int
  let rangeStart: Int64
  let rangeEnd: Int64
  let total: Int64
  let maxBytes: Int64
  let hostname: String
  let usesRange: Bool
  let deadlineAt: TimeInterval?

  var transferSelector: FirmwareArtifactTransferSelector {
    return FirmwareArtifactTransferSelector(
      leaseRef: leaseRef,
      taskId: taskId,
      artifactRef: artifactRef,
      generation: transferGeneration
    )
  }

  var transferKey: FirmwareArtifactTransferKey? {
    guard let transferGeneration else {
      return nil
    }
    return FirmwareArtifactTransferKey(
      leaseRef: leaseRef,
      taskId: taskId,
      artifactRef: artifactRef,
      generation: transferGeneration
    )
  }
}

struct FirmwareArtifactTransferPreparation: Equatable {
  let artifactRef: String
  let currentGeneration: String?
  let nextGeneration: String
  let currentRoute: StoredFirmwareArtifactRoute
  let requiresGenerationReplacement: Bool
  let discardExistingSegments: Bool
}

struct StoredFirmwareArtifactMetadata: Codable, Equatable {
  static let currentSchemaVersion = 3

  var schemaVersion: Int
  var artifactRef: String
  var artifactId: String
  var taskIdHash: String?
  var canonicalURLHash: String
  var hostname: String
  var route: StoredFirmwareArtifactRoute
  var expectedSize: Int64
  var expectedSha256: String
  var actualSize: Int64?
  var actualSha256: String?
  var immutableToken: String?
  var strongETag: String?
  var lastModified: String?
  var segments: [FirmwareArtifactSegmentMetadata]
  var transferDeadlineAt: TimeInterval?
  var transferRunAttempts: Int
  var transferGeneration: String?
  var transferUsesRange: Bool?
  var retentionClass: FirmwareArtifactRetentionClass
  var leaseRefs: [String]
  var createdAt: TimeInterval
  var updatedAt: TimeInterval
  var lastAccessedAt: TimeInterval
  var tombstonedAt: TimeInterval?

  init(
    artifactRef: String,
    artifactId: String,
    canonicalURL: String,
    hostname: String,
    route: StoredFirmwareArtifactRoute,
    expectedSize: Int64,
    expectedSha256: String,
    retentionClass: FirmwareArtifactRetentionClass,
    now: TimeInterval
  ) {
    self.schemaVersion = Self.currentSchemaVersion
    self.artifactRef = artifactRef
    self.artifactId = artifactId
    self.taskIdHash = nil
    self.canonicalURLHash = Self.sha256(canonicalURL)
    self.hostname = hostname.lowercased()
    self.route = route
    self.expectedSize = expectedSize
    self.expectedSha256 = expectedSha256.lowercased()
    self.actualSize = nil
    self.actualSha256 = nil
    self.immutableToken = nil
    self.strongETag = nil
    self.lastModified = nil
    self.segments = []
    self.transferDeadlineAt = nil
    self.transferRunAttempts = 0
    self.transferGeneration = nil
    self.transferUsesRange = nil
    self.retentionClass = retentionClass
    self.leaseRefs = []
    self.createdAt = now
    self.updatedAt = now
    self.lastAccessedAt = now
    self.tombstonedAt = nil
  }

  func canReusePartial(
    canonicalURL: String,
    hostname: String,
    route: StoredFirmwareArtifactRoute,
    expectedSize: Int64,
    expectedSha256: String,
    strongETag: String?,
    lastModified: String?
  ) -> Bool {
    guard tombstonedAt == nil,
          schemaVersion == Self.currentSchemaVersion,
          actualSize == nil,
          actualSha256 == nil,
          immutableToken == nil,
          retentionClass == .partial || retentionClass == .staging,
          canonicalURLHash == Self.sha256(canonicalURL),
          self.hostname == hostname.lowercased(),
          self.expectedSize == expectedSize,
          self.expectedSha256 == expectedSha256.lowercased() else {
      return false
    }

    let currentETag = Self.normalizeStrongETag(self.strongETag)
    let proposedETag = Self.normalizeStrongETag(strongETag)
    let currentLastModified = Self.normalizeValidator(self.lastModified)
    let proposedLastModified = Self.normalizeValidator(lastModified)

    if currentETag != nil || proposedETag != nil {
      return currentETag == proposedETag
    }
    if currentLastModified != nil || proposedLastModified != nil {
      return currentLastModified == proposedLastModified
    }
    return Self.isTrustedSha256(expectedSha256)
  }

  func matchesTransferIdentity(
    artifactId: String,
    canonicalURL: String,
    hostname: String,
    expectedSize: Int64,
    expectedSha256: String
  ) -> Bool {
    return tombstonedAt == nil &&
      schemaVersion == Self.currentSchemaVersion &&
      actualSize == nil &&
      actualSha256 == nil &&
      immutableToken == nil &&
      (retentionClass == .partial || retentionClass == .staging) &&
      self.artifactId == artifactId &&
      canonicalURLHash == Self.sha256(canonicalURL) &&
      self.hostname == hostname.lowercased() &&
      self.expectedSize == expectedSize &&
      self.expectedSha256 == expectedSha256.lowercased()
  }

  func hasMatchingSegmentLayout(
    _ ranges: [(start: Int64, end: Int64)]
  ) -> Bool {
    guard segments.count == ranges.count else {
      return false
    }
    return zip(segments, ranges).enumerated().allSatisfy { index, pair in
      let (segment, range) = pair
      return segment.index == index &&
        segment.start == range.start &&
        segment.end == range.end &&
        segment.downloadedBytes >= 0 &&
        segment.downloadedBytes <= range.end - range.start + 1
    }
  }

  func canReuseTransferBytes(
    canonicalURL: String,
    hostname: String,
    route: StoredFirmwareArtifactRoute,
    expectedSize: Int64,
    expectedSha256: String,
    strongETag: String?,
    lastModified: String?,
    ranges: [(start: Int64, end: Int64)]
  ) -> Bool {
    return canReusePartial(
      canonicalURL: canonicalURL,
      hostname: hostname,
      route: route,
      expectedSize: expectedSize,
      expectedSha256: expectedSha256,
      strongETag: strongETag,
      lastModified: lastModified
    ) && hasMatchingSegmentLayout(ranges)
  }

  static func isValidTransferGeneration(_ value: String) -> Bool {
    guard let uuid = UUID(uuidString: value) else {
      return false
    }
    return uuid.uuidString.lowercased() == value.lowercased()
  }

  static func hasValidTransferRanges(
    _ ranges: [(start: Int64, end: Int64)],
    total: Int64
  ) -> Bool {
    guard total > 0,
          !ranges.isEmpty,
          ranges.first?.start == 0,
          ranges.last?.end == total - 1 else {
      return false
    }
    var nextStart: Int64 = 0
    for range in ranges {
      guard range.start == nextStart,
            range.end >= range.start,
            range.end < total else {
        return false
      }
      nextStart = range.end + 1
    }
    return nextStart == total
  }

  var hasValidTransferBudget: Bool {
    if let transferGeneration {
      guard Self.isValidTransferGeneration(transferGeneration),
            transferUsesRange != nil else {
        return false
      }
    } else if transferUsesRange != nil {
      return false
    }
    guard let transferDeadlineAt else {
      return transferRunAttempts == 0 && transferGeneration == nil
    }
    return transferDeadlineAt.isFinite &&
      transferDeadlineAt > 0 &&
      transferRunAttempts > 0
  }

  static func normalizeStrongETag(_ value: String?) -> String? {
    guard let normalized = normalizeValidator(value),
          !normalized.lowercased().hasPrefix("w/") else {
      return nil
    }
    return normalized
  }

  static func isTrustedSha256(_ value: String) -> Bool {
    return value.utf8.count == 64 && value.utf8.allSatisfy { byte in
      (byte >= 48 && byte <= 57) ||
        (byte >= 65 && byte <= 70) ||
        (byte >= 97 && byte <= 102)
    }
  }

  static func sha256(_ value: String) -> String {
    let digest = SHA256.hash(data: Data(value.utf8))
    return digest.map { String(format: "%02x", $0) }.joined()
  }

  private static func normalizeValidator(_ value: String?) -> String? {
    guard let value else {
      return nil
    }
    let normalized = value.trimmingCharacters(in: .whitespacesAndNewlines)
    return normalized.isEmpty ? nil : normalized
  }
}

struct StoredFirmwareArtifactLeaseMetadata: Codable, Equatable {
  static let currentSchemaVersion = 2

  var schemaVersion: Int
  var leaseRef: String
  var transactionIdHash: String
  var artifactRefs: [String]
  var activeTransferArtifactRefsByTaskHash: [String: String]
  var createdAt: TimeInterval
  var updatedAt: TimeInterval
  var releasedAt: TimeInterval?
  var disposition: StoredFirmwareLeaseDisposition?

  init(
    leaseRef: String,
    transactionId: String,
    now: TimeInterval
  ) {
    self.schemaVersion = Self.currentSchemaVersion
    self.leaseRef = leaseRef
    self.transactionIdHash = StoredFirmwareArtifactMetadata.sha256(transactionId)
    self.artifactRefs = []
    self.activeTransferArtifactRefsByTaskHash = [:]
    self.createdAt = now
    self.updatedAt = now
    self.releasedAt = nil
    self.disposition = nil
  }

  var isActive: Bool {
    return releasedAt == nil
  }
}

struct FirmwareArtifactSweepSummary: Equatable {
  var deletedFiles: Int
  var deletedBytes: Int64
}

enum FirmwareArtifactStoreError: Error, Equatable, LocalizedError {
  case invalidReference
  case invalidMetadata
  case artifactNotFound
  case leaseNotFound
  case leaseReleased
  case artifactLeased
  case deadlineExceeded
  case attemptLimitExceeded
  case reconciliationRequired
  case sizeLimitExceeded
  case sizeMismatch
  case digestMismatch
  case storageFailure

  var code: String {
    switch self {
    case .invalidReference:
      return "ARTIFACT_INVALID_REF"
    case .invalidMetadata:
      return "ARTIFACT_INVALID_METADATA"
    case .artifactNotFound:
      return "ARTIFACT_NOT_FOUND"
    case .leaseNotFound:
      return "ARTIFACT_LEASE_NOT_FOUND"
    case .leaseReleased:
      return "ARTIFACT_LEASE_RELEASED"
    case .artifactLeased:
      return "ARTIFACT_LEASED"
    case .deadlineExceeded:
      return "ARTIFACT_DEADLINE_EXCEEDED"
    case .attemptLimitExceeded:
      return "ARTIFACT_ATTEMPT_LIMIT_EXCEEDED"
    case .reconciliationRequired:
      return "ARTIFACT_RECONCILIATION_REQUIRED"
    case .sizeLimitExceeded:
      return "ARTIFACT_SIZE_LIMIT_EXCEEDED"
    case .sizeMismatch:
      return "ARTIFACT_SIZE_MISMATCH"
    case .digestMismatch:
      return "ARTIFACT_DIGEST_MISMATCH"
    case .storageFailure:
      return "ARTIFACT_STORAGE_FAILED"
    }
  }

  var errorDescription: String? {
    return code
  }

  var retryable: Bool? {
    switch self {
    case .deadlineExceeded, .attemptLimitExceeded:
      return false
    default:
      return nil
    }
  }
}

func isRetryableFirmwareArtifactHTTPStatus(_ status: Int) -> Bool {
  return status == 408 || status == 416 || status == 429 ||
    ((500...599).contains(status) && status != 501 && status != 505)
}

enum FirmwareArtifactTransportFailureKind: Equatable {
  case cancelled
  case network
  case tls
  case transport
}

enum FirmwareArtifactTransportFailureClassifier {
  static func classify(_ error: Error) -> FirmwareArtifactTransportFailureKind? {
    let tlsCodes: Set<Int> = [
      NSURLErrorSecureConnectionFailed,
      NSURLErrorServerCertificateHasBadDate,
      NSURLErrorServerCertificateUntrusted,
      NSURLErrorServerCertificateHasUnknownRoot,
      NSURLErrorServerCertificateNotYetValid,
      NSURLErrorClientCertificateRejected,
      NSURLErrorClientCertificateRequired,
      NSURLErrorAppTransportSecurityRequiresSecureConnection,
    ]
    let reachabilityCodes: Set<Int> = [
      NSURLErrorTimedOut,
      NSURLErrorCannotFindHost,
      NSURLErrorCannotConnectToHost,
      NSURLErrorDNSLookupFailed,
      NSURLErrorNetworkConnectionLost,
      NSURLErrorNotConnectedToInternet,
      NSURLErrorInternationalRoamingOff,
      NSURLErrorCallIsActive,
      NSURLErrorDataNotAllowed,
    ]
    var current: NSError? = error as NSError
    var visited = Set<ObjectIdentifier>()
    while let candidate = current {
      let identity = ObjectIdentifier(candidate)
      guard visited.insert(identity).inserted else {
        break
      }
      if candidate.domain == NSURLErrorDomain {
        if candidate.code == NSURLErrorCancelled {
          return .cancelled
        }
        if tlsCodes.contains(candidate.code) {
          return .tls
        }
        if reachabilityCodes.contains(candidate.code) {
          return .network
        }
        return .transport
      }
      current = candidate.userInfo[NSUnderlyingErrorKey] as? NSError
    }
    return nil
  }
}
