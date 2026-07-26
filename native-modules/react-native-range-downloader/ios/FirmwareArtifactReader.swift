import Foundation

struct FirmwareArtifactReaderOpenResult: Equatable {
  let readerId: String
  let size: Int64
  let immutableToken: String
}

enum FirmwareArtifactReaderError: Error, Equatable, LocalizedError {
  case notFinal
  case immutableTokenMismatch
  case readerNotFound
  case readerBusy
  case invalidRange
  case readLimitExceeded
  case emptyRead
  case shortRead
  case storageFailure

  var code: String {
    switch self {
    case .notFinal:
      return "ARTIFACT_NOT_FINAL"
    case .immutableTokenMismatch:
      return "ARTIFACT_IMMUTABLE_TOKEN_MISMATCH"
    case .readerNotFound:
      return "ARTIFACT_READER_NOT_FOUND"
    case .readerBusy:
      return "ARTIFACT_READER_BUSY"
    case .invalidRange:
      return "ARTIFACT_READ_INVALID_RANGE"
    case .readLimitExceeded:
      return "ARTIFACT_READ_LIMIT_EXCEEDED"
    case .emptyRead:
      return "ARTIFACT_READ_EMPTY"
    case .shortRead:
      return "ARTIFACT_READ_SHORT"
    case .storageFailure:
      return "ARTIFACT_STORAGE_FAILED"
    }
  }

  var errorDescription: String? {
    return code
  }
}

final class FirmwareArtifactReader {
  static let maxReadBytes = 256 * 1024
  static let shared = FirmwareArtifactReader(store: .shared)

  private static let maximumSafeInteger = 9_007_199_254_740_991.0

  private final class ReaderState {
    let readerId: String
    let artifactRef: String
    let immutableToken: String
    let size: Int64
    let fileHandle: FileHandle
    let activityToken: FirmwareArtifactActivityToken
    var isReading = false
    var closeRequested = false

    init(
      readerId: String,
      artifactRef: String,
      immutableToken: String,
      size: Int64,
      fileHandle: FileHandle,
      activityToken: FirmwareArtifactActivityToken
    ) {
      self.readerId = readerId
      self.artifactRef = artifactRef
      self.immutableToken = immutableToken
      self.size = size
      self.fileHandle = fileHandle
      self.activityToken = activityToken
    }

    func close() {
      try? fileHandle.close()
      activityToken.release()
    }
  }

  private let store: FirmwareArtifactStore
  private let lock = NSLock()
  private var readers: [String: ReaderState] = [:]

  init(store: FirmwareArtifactStore) {
    self.store = store
  }

  deinit {
    closeAll()
  }

  func open(
    artifactRef: String,
    immutableToken: String
  ) throws -> FirmwareArtifactReaderOpenResult {
    let activityToken = try store.beginActivity(
      artifactRef: artifactRef,
      kind: .reader
    )
    do {
      let metadata = try store.loadArtifact(activityToken.artifactRef)
      guard metadata.tombstonedAt == nil,
            metadata.retentionClass == .verifiedFinal ||
              metadata.retentionClass == .archive ||
              metadata.retentionClass == .materializedChild,
            let actualSize = metadata.actualSize,
            actualSize > 0,
            actualSize == metadata.expectedSize,
            let actualSha256 = metadata.actualSha256,
            RangeDownloadLogic.secureHexEquals(
              actualSha256,
              metadata.expectedSha256
            ),
            let storedToken = metadata.immutableToken else {
        throw FirmwareArtifactReaderError.notFinal
      }
      guard immutableToken == storedToken else {
        throw FirmwareArtifactReaderError.immutableTokenMismatch
      }

      let finalURL = try store.finalFileURL(for: metadata.artifactRef)
      let attributes = try FileManager.default.attributesOfItem(
        atPath: finalURL.path
      )
      guard let fileSize = attributes[.size] as? NSNumber,
            fileSize.int64Value == actualSize else {
        throw FirmwareArtifactReaderError.notFinal
      }
      let fileHandle = try FileHandle(forReadingFrom: finalURL)
      let readerId = UUID().uuidString.lowercased()
      let state = ReaderState(
        readerId: readerId,
        artifactRef: metadata.artifactRef,
        immutableToken: storedToken,
        size: actualSize,
        fileHandle: fileHandle,
        activityToken: activityToken
      )
      lock.withFirmwareLock {
        readers[readerId] = state
      }
      _ = try store.updateArtifact(metadata.artifactRef) { artifact in
        artifact.lastAccessedAt = Date().timeIntervalSince1970
      }
      return FirmwareArtifactReaderOpenResult(
        readerId: readerId,
        size: actualSize,
        immutableToken: storedToken
      )
    } catch {
      activityToken.release()
      throw mapError(error)
    }
  }

  func read(
    readerId: String,
    offset: Double,
    length: Double
  ) throws -> Data {
    let normalizedReaderId = try validateReaderId(readerId)
    let validatedOffset = try validateSafeInteger(offset)
    let validatedLength = try validateSafeInteger(length)
    guard validatedLength <= Int64(Self.maxReadBytes) else {
      throw FirmwareArtifactReaderError.readLimitExceeded
    }

    let state: ReaderState = try lock.withFirmwareLock {
      guard let state = readers[normalizedReaderId],
            !state.closeRequested else {
        throw FirmwareArtifactReaderError.readerNotFound
      }
      guard !state.isReading else {
        throw FirmwareArtifactReaderError.readerBusy
      }
      guard validatedOffset <= state.size,
            validatedLength <= state.size - validatedOffset else {
        throw FirmwareArtifactReaderError.invalidRange
      }
      if validatedLength == 0, validatedOffset != state.size {
        throw FirmwareArtifactReaderError.emptyRead
      }
      state.isReading = true
      return state
    }

    defer {
      finishRead(state)
    }

    if validatedLength == 0 {
      return Data()
    }

    do {
      try state.fileHandle.seek(toOffset: UInt64(validatedOffset))
      let data = try state.fileHandle.read(
        upToCount: Int(validatedLength)
      ) ?? Data()
      guard data.count == Int(validatedLength) else {
        throw FirmwareArtifactReaderError.shortRead
      }
      return data
    } catch let error as FirmwareArtifactReaderError {
      throw error
    } catch {
      throw FirmwareArtifactReaderError.storageFailure
    }
  }

  func close(readerId: String) throws {
    let normalizedReaderId = try validateReaderId(readerId)
    var stateToClose: ReaderState?
    lock.withFirmwareLock {
      guard let state = readers[normalizedReaderId] else {
        return
      }
      if state.isReading {
        state.closeRequested = true
      } else {
        readers.removeValue(forKey: normalizedReaderId)
        stateToClose = state
      }
    }
    stateToClose?.close()
  }

  func closeAll() {
    var statesToClose: [ReaderState] = []
    lock.withFirmwareLock {
      for state in readers.values {
        if state.isReading {
          state.closeRequested = true
        } else {
          statesToClose.append(state)
        }
      }
      for state in statesToClose {
        readers.removeValue(forKey: state.readerId)
      }
    }
    statesToClose.forEach { $0.close() }
  }

  private func finishRead(_ state: ReaderState) {
    var shouldClose = false
    lock.withFirmwareLock {
      state.isReading = false
      if state.closeRequested {
        readers.removeValue(forKey: state.readerId)
        shouldClose = true
      }
    }
    if shouldClose {
      state.close()
    }
  }

  private func validateReaderId(_ readerId: String) throws -> String {
    guard let uuid = UUID(uuidString: readerId),
          uuid.uuidString.lowercased() == readerId.lowercased() else {
      throw FirmwareArtifactReaderError.readerNotFound
    }
    return uuid.uuidString.lowercased()
  }

  private func validateSafeInteger(_ value: Double) throws -> Int64 {
    guard value.isFinite,
          value >= 0,
          value <= Self.maximumSafeInteger,
          value.rounded(.towardZero) == value else {
      throw FirmwareArtifactReaderError.invalidRange
    }
    return Int64(value)
  }

  private func mapError(_ error: Error) -> Error {
    if let readerError = error as? FirmwareArtifactReaderError {
      return readerError
    }
    if let storeError = error as? FirmwareArtifactStoreError {
      switch storeError {
      case .invalidReference, .artifactNotFound:
        return storeError
      default:
        return FirmwareArtifactReaderError.storageFailure
      }
    }
    return FirmwareArtifactReaderError.storageFailure
  }
}
