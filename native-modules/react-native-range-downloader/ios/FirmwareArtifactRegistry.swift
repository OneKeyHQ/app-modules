import Foundation

enum FirmwareArtifactRegistryState {
  case notFound
  case queued
  case downloading
  case verifying
  case completed
  case cancelled
  case failed
}

struct FirmwareArtifactRegistrySnapshot {
  let state: FirmwareArtifactRegistryState
  let downloadedBytes: Int64
  let expectedSize: Int64
  let artifact: StoredFirmwareArtifactMetadata?
  let errorCode: String?
  let errorMessage: String?
  let retryable: Bool?
}

final class FirmwareArtifactRegistry {
  static let shared = FirmwareArtifactRegistry()

  private struct TaskKey: Hashable {
    let leaseRef: String
    let taskId: String
  }

  private final class Entry {
    let fingerprint: String
    let expectedSize: Int64
    var task: Task<StoredFirmwareArtifactMetadata, Error>?
    var state: FirmwareArtifactRegistryState = .queued
    var downloadedBytes: Int64 = 0
    var artifact: StoredFirmwareArtifactMetadata?
    var errorCode: String?
    var errorMessage: String?
    var retryable: Bool?

    init(fingerprint: String, expectedSize: Int64) {
      self.fingerprint = fingerprint
      self.expectedSize = expectedSize
    }
  }

  private let lock = NSLock()
  private var entries: [TaskKey: Entry] = [:]
  private let activeArtifacts = FirmwareArtifactRunOwnership<TaskKey>()

  init() {}

  func runOrAttach(
    leaseRef: String,
    taskId: String,
    artifactId: String,
    fingerprint: String,
    expectedSize: Int64,
    operation: @escaping (
      _ update: @escaping (FirmwareArtifactRegistryState, Int64) -> Void
    ) async throws -> StoredFirmwareArtifactMetadata
  ) async throws -> StoredFirmwareArtifactMetadata {
    let key = TaskKey(leaseRef: leaseRef, taskId: taskId)
    let selection: (
      entry: Entry,
      task: Task<StoredFirmwareArtifactMetadata, Error>,
      ownsTask: Bool
    ) = try lock.withFirmwareLock {
      if let existing = entries[key],
         existing.fingerprint == fingerprint,
         existing.state != .failed,
         existing.state != .cancelled,
         let existingTask = existing.task {
        return (existing, existingTask, false)
      }
      if let existing = entries[key], existing.fingerprint != fingerprint {
        throw FirmwareArtifactStoreError.invalidMetadata
      }
      guard activeArtifacts.acquire(artifactId: artifactId, owner: key) else {
        throw FirmwareArtifactTransferError.busy
      }
      let entry = Entry(fingerprint: fingerprint, expectedSize: expectedSize)
      let createdTask = Task { [activeArtifacts = self.activeArtifacts] in
        defer {
          activeArtifacts.release(artifactId: artifactId, owner: key)
        }
        return try await operation { [weak self, weak entry] state, downloadedBytes in
          guard let self, let entry else { return }
          self.lock.withFirmwareLock {
            entry.state = state
            entry.downloadedBytes = max(0, downloadedBytes)
          }
        }
      }
      entry.task = createdTask
      entries[key] = entry
      return (entry, createdTask, true)
    }
    let entry = selection.entry
    let task = selection.task
    let ownsTask = selection.ownsTask

    do {
      let result = try await task.value
      if ownsTask {
        lock.withFirmwareLock {
          entry.state = .completed
          entry.downloadedBytes = result.actualSize ?? expectedSize
          entry.artifact = result
          pruneCompletedEntries()
        }
      }
      return result
    } catch {
      if ownsTask {
        let transferError = error as? FirmwareArtifactTransferError
        let storeError = error as? FirmwareArtifactStoreError
        lock.withFirmwareLock {
          entry.state = task.isCancelled ? .cancelled : .failed
          entry.errorCode = transferError?.code ??
            storeError?.code ??
            "ARTIFACT_DOWNLOAD_FAILED"
          entry.errorMessage = error.localizedDescription
          entry.retryable = transferError?.retryable ?? storeError?.retryable
          pruneCompletedEntries()
        }
      }
      throw error
    }
  }

  func update(
    leaseRef: String,
    taskId: String,
    state: FirmwareArtifactRegistryState,
    downloadedBytes: Int64
  ) {
    lock.withFirmwareLock {
      guard let entry = entries[TaskKey(leaseRef: leaseRef, taskId: taskId)] else {
        return
      }
      entry.state = state
      entry.downloadedBytes = max(0, downloadedBytes)
    }
  }

  func fail(
    leaseRef: String,
    taskId: String,
    error: FirmwareArtifactTransferError
  ) {
    lock.withFirmwareLock {
      guard let entry = entries[TaskKey(leaseRef: leaseRef, taskId: taskId)] else {
        return
      }
      entry.state = .failed
      entry.errorCode = error.code
      entry.errorMessage = error.localizedDescription
      entry.retryable = error.retryable
    }
  }

  func status(
    leaseRef: String,
    taskId: String
  ) -> FirmwareArtifactRegistrySnapshot? {
    return lock.withFirmwareLock {
      guard let entry = entries[TaskKey(leaseRef: leaseRef, taskId: taskId)] else {
        return nil
      }
      return FirmwareArtifactRegistrySnapshot(
        state: entry.state,
        downloadedBytes: entry.downloadedBytes,
        expectedSize: entry.expectedSize,
        artifact: entry.artifact,
        errorCode: entry.errorCode,
        errorMessage: entry.errorMessage,
        retryable: entry.retryable
      )
    }
  }

  func cancel(leaseRef: String, taskId: String) {
    lock.withFirmwareLock {
      let entry = entries[TaskKey(leaseRef: leaseRef, taskId: taskId)]
      entry?.state = .cancelled
      entry?.task?.cancel()
    }
  }

  private func pruneCompletedEntries() {
    guard entries.count > 128 else { return }
    let removable = entries.filter {
      $0.value.state == .completed ||
        $0.value.state == .failed ||
        $0.value.state == .cancelled
    }
    for (key, _) in removable.prefix(entries.count - 128) {
      entries.removeValue(forKey: key)
    }
  }
}
