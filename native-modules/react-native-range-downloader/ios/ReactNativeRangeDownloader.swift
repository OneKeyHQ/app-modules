import Foundation
import NitroModules
import ReactNativeNativeLogger

// MARK: - Nitro HybridObject entry point
//
// Thin Nitro shim over `RangeDownloader.shared`. The heavy lifting — concurrent
// background range download, segment stash/concatenate, resume — is migrated
// almost verbatim from react-native-bundle-update's ConcurrentBundleDownloader.
// The only structural changes vs. that origin are:
//   - single global run state  → a `runs` table keyed by "channel|taskId", so
//     different channels (apk, chart, bundle) can download concurrently;
//   - one hardcoded background session identifier → one session per channel;
//   - `onProgress` closure       → a listener registry broadcasting
//     `RangeDownloadEvent`s (multi-consumer);
//   - `throws FallbackError`     → returns a typed `RangeDownloadResult`
//     (`fallbackTransient` / `fallbackPermanent` + `fallbackKind`).
class ReactNativeRangeDownloader: HybridReactNativeRangeDownloaderSpec {

  func download(params: RangeDownloadParams) throws -> Promise<RangeDownloadResult> {
    return Promise.async {
      let (klass, filePath, fallbackReason, fallbackClass) = await RangeDownloader.shared.download(
        channel: params.channel,
        taskId: params.taskId,
        urlString: params.url,
        filePath: params.destFilePath,
        expectedSha256: params.expectedSha256,
        segmentCount: params.segmentCount.map { Int($0) },
        minConcurrentBytes: params.minConcurrentBytes.map { Int64($0) },
        // §5.4: caller-tunable retry/timeout/deadline knobs forwarded straight
        // from the regenerated `RangeDownloadParams`. Omitted (nil) values let the
        // core use its platform defaults.
        maxSegmentAttempts: params.maxSegmentAttempts.map { Int($0) },
        requestTimeoutSeconds: params.requestTimeoutSeconds,
        stallTimeoutSeconds: params.stallTimeoutSeconds,
        overallDeadlineSeconds: params.overallDeadlineSeconds
      )
      // Wire mapping (OCDS §4): map the in-process typed class onto the
      // regenerated wire enum — no lossy collapse. `completed`, `fallbackTransient`
      // and `fallbackPermanent` each cross the JS bridge as their own case, and the
      // optional `fallbackKind` sub-classification is forwarded so callers /
      // analytics can branch without parsing the reason string.
      return RangeDownloadResult(
        outcome: klass.wireOutcome,
        filePath: filePath,
        fallbackReason: fallbackReason,
        fallbackKind: fallbackClass?.wireKind
      )
    }
  }

  func discardArtifacts(
    channel: DownloadChannel,
    taskId: String,
    destFilePath: String
  ) throws -> Promise<Void> {
    return Promise.async {
      // Cancel-then-delete: cancel any in-flight tasks for this run before
      // removing files so a running task can't resurrect a deleted segment.
      await RangeDownloader.shared.cancel(
        channel: channel, taskId: taskId, filePath: destFilePath
      )
    }
  }

  func cancel(
    channel: DownloadChannel,
    taskId: String,
    destFilePath: String
  ) throws -> Promise<Void> {
    return Promise.async {
      await RangeDownloader.shared.cancel(
        channel: channel, taskId: taskId, filePath: destFilePath
      )
    }
  }

  func addDownloadListener(
    callback: @escaping (_ event: RangeDownloadEvent) -> Void
  ) throws -> Double {
    return Double(RangeDownloader.shared.addListener(callback))
  }

  func removeDownloadListener(id: Double) throws {
    RangeDownloader.shared.removeListener(Int(id))
  }

  // App Caches directory — an app-owned, writable absolute path resolved at
  // runtime (no hardcoded sandbox path).
  func getDownloadsDir() throws -> String {
    if let caches = FileManager.default.urls(
      for: .cachesDirectory, in: .userDomainMask
    ).first {
      return caches.path
    }
    return NSTemporaryDirectory()
  }

  func getFirmwareArtifactCapabilities() throws -> FirmwareArtifactCapabilities {
    FirmwareArtifactCapabilities(
      firmwareArtifactProtocolVersion: 1,
      supportedRouteTypes: ["domain", "pinnedIp"],
      supportsArchiveMaterialization: true,
      maxReadBytes: Double(FirmwareArtifactStore.maxReadBytes)
    )
  }

  func downloadFirmwareArtifact(
    params: FirmwareArtifactDownloadParams
  ) throws -> Promise<FirmwareArtifactReceipt> {
    Promise.async {
      let artifact = try await FirmwareArtifactStore.shared.download(params)
      return FirmwareArtifactReceipt(
        artifactRef: artifact.artifactRef,
        size: Double(artifact.size),
        sha256: artifact.sha256
      )
    }
  }

  func discardFirmwareArtifact(
    params: FirmwareArtifactRefParams
  ) throws -> Promise<Void> {
    Promise.async {
      try FirmwareArtifactStore.shared.discard(artifactRef: params.artifactRef)
    }
  }

  func openFirmwareArtifact(
    params: FirmwareArtifactRefParams
  ) throws -> Promise<FirmwareArtifactReaderInfo> {
    Promise.async {
      let reader = try FirmwareArtifactStore.shared.open(
        artifactRef: params.artifactRef
      )
      return FirmwareArtifactReaderInfo(
        readerId: reader.readerId,
        size: Double(reader.size)
      )
    }
  }

  func readFirmwareArtifact(
    params: FirmwareArtifactReaderReadParams
  ) throws -> Promise<ArrayBuffer> {
    Promise.async {
      guard
        params.offset.isFinite,
        params.offset >= 0,
        params.offset <= Double(Int64.max),
        params.offset.rounded() == params.offset,
        params.length.isFinite,
        params.length > 0,
        params.length <= Double(Int.max),
        params.length.rounded() == params.length
      else {
        throw FirmwareArtifactStoreError.readerInvalid(
          "Invalid firmware artifact read"
        )
      }
      let data = try FirmwareArtifactStore.shared.read(
        readerId: params.readerId,
        offset: Int64(params.offset),
        length: Int(params.length)
      )
      return try ArrayBuffer.copy(data: data)
    }
  }

  func closeFirmwareArtifact(
    params: FirmwareArtifactReaderCloseParams
  ) throws -> Promise<Void> {
    Promise.async {
      try FirmwareArtifactStore.shared.close(readerId: params.readerId)
    }
  }

  func materializeFirmwareArchive(
    params: FirmwareArchiveMaterializeParams
  ) throws -> Promise<FirmwareArchiveMaterializeResult> {
    Promise.async {
      let entries = try FirmwareArtifactStore.shared.materializeArchive(
        leaseRef: params.leaseRef,
        artifactRef: params.archiveArtifactRef,
        expectedEntries: params.expectedEntries
      )
      return FirmwareArchiveMaterializeResult(
        artifacts: entries.map { entry in
          FirmwareArchiveMaterializedArtifact(
            entryName: entry.entryName,
            receipt: FirmwareArtifactReceipt(
              artifactRef: entry.artifact.artifactRef,
              size: Double(entry.artifact.size),
              sha256: entry.artifact.sha256
            )
          )
        }
      )
    }
  }

  func createFirmwareArtifactLease(
    params: FirmwareArtifactLeaseCreateParams
  ) throws -> Promise<FirmwareArtifactLease> {
    Promise.async {
      FirmwareArtifactLease(
        leaseRef: try FirmwareArtifactStore.shared.createLease(
          transactionId: params.transactionId
        )
      )
    }
  }

  func retainFirmwareArtifact(
    params: FirmwareArtifactLeaseRetainParams
  ) throws -> Promise<Void> {
    Promise.async {
      try FirmwareArtifactStore.shared.retain(
        leaseRef: params.leaseRef,
        artifactRef: params.artifactRef
      )
    }
  }

  func releaseFirmwareArtifactLease(
    params: FirmwareArtifactLeaseReleaseParams
  ) throws -> Promise<Void> {
    Promise.async {
      try FirmwareArtifactStore.shared.releaseLease(
        leaseRef: params.leaseRef,
        disposition: params.disposition
      )
    }
  }

  func reconcileFirmwareArtifactLeases(
    params: FirmwareArtifactLeaseReconcileParams
  ) throws -> Promise<Void> {
    Promise.async {
      try FirmwareArtifactStore.shared.reconcileLeases(
        activeLeaseRefs: params.activeLeaseRefs
      )
    }
  }

  func sweepFirmwareArtifactOrphans() throws -> Promise<FirmwareArtifactSweepResult> {
    Promise.async {
      let result = try FirmwareArtifactStore.shared.sweepOrphans()
      return FirmwareArtifactSweepResult(
        deletedFiles: Double(result.deletedFiles),
        deletedBytes: Double(result.deletedBytes)
      )
    }
  }
}

// MARK: - RangeDownloader (migrated core)

/// Concurrent + background multi-range downloader for iOS.
///
/// Each channel owns its own background `URLSession` (identifier
/// "so.onekey.rangedownloader.bg.<channel>"). A background session carries
/// [segmentCount] download tasks, each requesting a byte Range of the file. A
/// background session satisfies BOTH requirements at once:
///   - concurrency: the range tasks run in parallel (foreground = full speed);
///   - background: `nsurlsessiond` keeps transferring while the app is
///     suspended and even relaunches the app to deliver completion events.
/// It needs NO new entitlement.
///
/// Because background download tasks deliver a whole file per task at
/// completion (not an incremental stream), each segment is downloaded to its
/// own `<file>.segN` and the segments are concatenated in order once all are
/// present. A segment file is therefore atomic — fully present or absent — so
/// resume across app suspension/kill is just "which `.segN` already exist".
///
/// Unlike the single-run origin, this downloader keeps a `runs` table keyed by
/// "channel|taskId" so multiple channels can download at once. Each running
/// task's `taskDescription` encodes "channel|taskId|segIndex" so a delegate
/// callback can locate both the run and the segment.
// MARK: - Typed failure model (OCDS §4) — wire projections
//
// The dependency-free enum bodies for `RangeDownloadClass` / `RangeFallbackClass`
// (plus the deterministic logic funcs) live in `RangeDownloadLogic.swift` so they
// can be unit-tested without the Nitro / NativeLogger deps. Only the wire
// projections — which depend on the codegen enums (`RangeDownloadOutcome` /
// `RangeFallbackKind`) — remain here, in the module that has those generated types.
//
// The IN-PROCESS core returns the Swift-native typed class to its in-process
// caller (BundleUpdate); the Nitro shim maps it onto the regenerated wire enum
// `RangeDownloadOutcome` (completed | fallbackTransient | fallbackPermanent) so
// the failure class crosses the JS boundary as an EXPLICIT value, never inferred
// from incidental on-disk side effects (which §4 forbids).
extension RangeDownloadClass {
  /// 1:1 projection onto the generated wire enum (`RangeDownloadOutcome`) that
  /// crosses the JS bridge. No collapse: each in-process class maps to its own
  /// typed wire case.
  var wireOutcome: RangeDownloadOutcome {
    switch self {
    case .completed: return .completed
    case .fallbackTransient: return .fallbacktransient
    case .fallbackPermanent: return .fallbackpermanent
    }
  }
}

extension RangeFallbackClass {
  /// 1:1 projection onto the generated wire enum (`RangeFallbackKind`). The case
  /// names are the wire union's string values verbatim, so `fromString` is exact
  /// and total (the force-unwrap can never fail).
  var wireKind: RangeFallbackKind {
    // swiftlint:disable:next force_unwrapping
    return RangeFallbackKind(fromString: self.rawValue)!
  }
}

private struct FirmwareBackgroundTaskDescriptor: Codable, Equatable {
  let schemaVersion: Int
  let taskId: String
  let transactionId: String
  let leaseRef: String
  let expectedSize: Int64
  let expectedSha256: String
  let hostname: String
  let deadlineAt: TimeInterval

  var key: String {
    "\(leaseRef)|\(taskId)|\(expectedSha256)"
  }

  func hasSameArtifactIdentity(
    as other: FirmwareBackgroundTaskDescriptor
  ) -> Bool {
    taskId == other.taskId &&
      transactionId == other.transactionId &&
      leaseRef == other.leaseRef &&
      expectedSize == other.expectedSize &&
      expectedSha256 == other.expectedSha256 &&
      hostname == other.hostname
  }
}

private enum FirmwareBackgroundDownloadError: LocalizedError {
  case invalidTask
  case deadlineExceeded
  case redirectRejected
  case responseRejected
  case sizeRejected
  case transferFailed

  var errorDescription: String? {
    switch self {
    case .invalidTask:
      return "ARTIFACT_PROTOCOL_INVALID: firmware background task is invalid"
    case .deadlineExceeded:
      return "ARTIFACT_DEADLINE_EXCEEDED: firmware download exceeded its deadline"
    case .redirectRejected:
      return "ARTIFACT_REDIRECT_REJECTED: firmware redirect changed canonical identity"
    case .responseRejected:
      return "ARTIFACT_PROTOCOL_INVALID: firmware response is invalid"
    case .sizeRejected:
      return "ARTIFACT_PROTOCOL_INVALID: firmware artifact size is invalid"
    case .transferFailed:
      return "ARTIFACT_NETWORK_FAILED: firmware background transfer failed"
    }
  }
}

public final class RangeDownloader: NSObject, URLSessionDownloadDelegate {

  public static let shared = RangeDownloader()

  public static func routeFirmwareBackgroundEvents(
    identifier: String,
    completionHandler: @escaping () -> Void
  ) -> Bool {
    guard identifier == sessionIdentifier(for: .firmware) else {
      return false
    }
    shared.attachBackgroundEvents(
      identifier: identifier,
      completionHandler: completionHandler
    )
    return true
  }

  /// Posted by the AppDelegate from
  /// application(_:handleEventsForBackgroundURLSession:completionHandler:).
  /// userInfo: ["identifier": String, "completionHandler": () -> Void].
  /// (The AppDelegate posts this generic name for every background URLSession.)
  static let backgroundEventsNotification =
    Notification.Name("RangeDownloaderBackgroundEvents")

  /// Our per-channel session identifier prefix. Identifiers not carrying this
  /// prefix (and not the legacy one) are ignored so we never steal another
  /// module's background session events.
  private static let sessionIdentifierPrefix = "so.onekey.rangedownloader.bg."
  /// Legacy bundle-update identifier prefix, recognized during the transition
  /// so events queued by an older build still route here.
  private static let legacySessionIdentifierPrefix = "so.onekey.bundleupdate.concurrent.bg"

  private static let defaultSegmentCount = 8
  private static let defaultMinConcurrentBytes: Int64 = 2 * 1024 * 1024

  private let lock = NSLock()

  /// Active-run state keyed by "channel|taskId". Replaces the origin's single
  /// global run state to support concurrent channels.
  private var runs: [String: RunState] = [:]

  /// Lazy per-channel background sessions, cached by identifier.
  private var sessions: [String: URLSession] = [:]

  /// Listener registry. The Nitro layer registers JS callbacks here; events are
  /// broadcast to all of them (replaces the origin's single `onProgress`).
  private var listeners: [Int: (RangeDownloadEvent) -> Void] = [:]
  private var nextListenerId = 1

  /// Stored by the AppDelegate's handleEventsForBackgroundURLSession (via the
  /// notification), keyed by session identifier, so we can call each back once
  /// all its queued background events have been delivered.
  private var backgroundCompletionHandlers: [String: () -> Void] = [:]
  private var firmwareWaiters: [
    String: [CheckedContinuation<StoredFirmwareArtifact, Error>]
  ] = [:]
  private var firmwareTaskErrors: [Int: Error] = [:]
  private var completedFirmwareTasks: Set<Int> = []

  // Per-run mutable state.
  private final class RunState {
    let channel: DownloadChannel
    let taskId: String
    let filePath: String
    let segmentCount: Int
    var totalSize: Int64 = 0
    var etag: String?
    var ranges: [(start: Int64, end: Int64)] = []
    var segmentWritten: [Int64] = []   // progress estimate per segment
    var continuation: CheckedContinuation<Void, Error>?
    var prevProgress = -1
    var fellBack = false
    /// §4 typed class for the eventual fallback. Set by the delegate when it
    /// classifies a non-206 status or a redirect/total/stash failure; consumed by
    /// finalize so the outcome is an EXPLICIT class, never inferred from on-disk
    /// side effects. `serverIgnoredRange` (Permanent) is the historical default
    /// for the bare `fellBack` path (status 200).
    var fellBackKind: RangeFallbackClass = .serverIgnoredRange
    /// Set when a segment could not be stashed (move/size-check failure). Carries
    /// the terminal error so didCompleteWithError finalizes instead of hanging.
    var stashError: Error?
    /// §4/§5.4: per-segment indexes whose finished body was a TRANSIENT HTTP
    /// status (429/5xx/408/416) — the body is discarded and the segment is
    /// re-enqueued (G2) instead of failing the whole run. Optional Retry-After
    /// seconds captured from that response, used to override backoff.
    var transientSegmentRetryAfter: [Int: Double?] = [:]
    /// §4 (416): segment indexes that returned `416 Range Not Satisfiable`. A
    /// bare 416 must NOT re-request the same range blindly: the total size /
    /// validator is re-evaluated (re-probe) first. If the total or ETag changed
    /// the object changed under us → object-change (wipe + restart, Permanent);
    /// otherwise the range is still valid and we keep the resumable `.segN` and
    /// retry. Set in didFinishDownloadingTo, consumed in didCompleteWithError.
    var sizeReevalIndexes: Set<Int> = []
    /// Whether a strong validator (ETag) was captured for this run. When false,
    /// resumable `.segN` state must not be trusted across attempts.
    var hasValidator = false
    let sessionIdentifier: String

    /// §5.4: per-segment transient retry budget (caller-tunable).
    let maxSegmentAttempts: Int
    /// §5.4: number of transient retry attempts already spent per segment index.
    var segmentAttempts: [Int: Int] = [:]
    /// §5.4: segment indexes the stall watchdog cancelled, so didCompleteWithError
    /// treats the resulting NSURLErrorCancelled as a transient stall (retry),
    /// not an external user cancel (terminate).
    var stallCancelledIndexes: Set<Int> = []
    /// §5.4 (G2): segment indexes with a backoff retry already SCHEDULED (an
    /// asyncAfter pending) but not yet re-enqueued. A pending retry is invisible
    /// to `getAllTasks` (no live task exists during the backoff window), so a
    /// sibling segment's completion could otherwise re-increment this segment's
    /// attempt counter and schedule a DUPLICATE retry. Inserted when the
    /// asyncAfter is armed; cleared inside `enqueueSegment` when the real task is
    /// created. Both `retrySegmentIfUnderBudget` and the missing-segment
    /// re-enqueue gate on this set so a segment is never double-enqueued.
    var pendingRetryIndexes: Set<Int> = []
    /// §5.11 (single-run portion): wall-clock deadline; nil = unbounded. The
    /// cross-restart budget/deadline is owned by the shared-JS track.
    let deadline: Date?
    /// §5.4: last time ANY segment of this run made progress (didWriteData),
    /// used by the stall watchdog. Only foreground/active time should count.
    var lastProgressAt = Date()
    /// The URL this run is fetching, retained so the delegate can re-enqueue a
    /// single segment without re-plumbing it through every call.
    var url: URL?

    init(channel: DownloadChannel, taskId: String, filePath: String,
         segmentCount: Int, sessionIdentifier: String,
         maxSegmentAttempts: Int, deadline: Date?) {
      self.channel = channel
      self.taskId = taskId
      self.filePath = filePath
      self.segmentCount = segmentCount
      self.sessionIdentifier = sessionIdentifier
      self.maxSegmentAttempts = maxSegmentAttempts
      self.deadline = deadline
    }

    func segPath(_ index: Int) -> String { "\(filePath).seg\(index)" }

    /// True once the wall-clock deadline (if any) has passed.
    var isPastDeadline: Bool {
      guard let deadline = deadline else { return false }
      return Date() >= deadline
    }
  }

  // Default retry/backoff/deadline knobs (§5.4 / §5.11 single-run). Caller-tunable
  // via RangeDownloadParams; these are platform configuration, not part of OCDS.
  private static let defaultMaxSegmentAttempts = 4
  private static let defaultRequestTimeoutSeconds: Double = 60
  private static let defaultStallTimeoutSeconds: Double = 30
  // §5.4 backoff knobs now live on `RangeDownloadLogic` (used only by backoffDelay).

  override init() {
    super.init()
    NotificationCenter.default.addObserver(
      self,
      selector: #selector(handleBackgroundEventsNotification(_:)),
      name: Self.backgroundEventsNotification,
      object: nil
    )
  }

  // MARK: - Identifier <-> channel

  private static func sessionIdentifier(for channel: DownloadChannel) -> String {
    return "\(sessionIdentifierPrefix)\(channel.stringValue)"
  }

  /// Reverse-resolves a channel from a session identifier; nil if the
  /// identifier doesn't belong to this module (so we don't preempt it).
  private static func channel(forIdentifier identifier: String) -> DownloadChannel? {
    if identifier.hasPrefix(sessionIdentifierPrefix) {
      let raw = String(identifier.dropFirst(sessionIdentifierPrefix.count))
      return DownloadChannel(fromString: raw)
    }
    // Legacy bundle-update identifier maps to the bundle channel during the
    // transition.
    if identifier == legacySessionIdentifierPrefix
        || identifier.hasPrefix(legacySessionIdentifierPrefix) {
      return .bundle
    }
    return nil
  }

  @objc private func handleBackgroundEventsNotification(_ note: Notification) {
    guard let identifier = note.userInfo?["identifier"] as? String,
          Self.channel(forIdentifier: identifier) != nil else { return }
    if let handler = note.userInfo?["completionHandler"] as? () -> Void {
      attachBackgroundEvents(
        identifier: identifier,
        completionHandler: handler
      )
    }
  }

  private func attachBackgroundEvents(
    identifier: String,
    completionHandler: @escaping () -> Void
  ) {
    let replacedHandler: (() -> Void)? = lock.withLockValue {
      backgroundCompletionHandlers.updateValue(
        completionHandler,
        forKey: identifier
      )
    }
    // UIKit should provide one live handler per session. Complete an older
    // handler instead of leaking it if the callback is unexpectedly repeated.
    replacedHandler?()
    // Store the handler before creating the session. Delegate delivery can
    // begin immediately when a background relaunch reattaches this identifier.
    _ = session(forIdentifier: identifier)
  }

  // MARK: - Session cache

  private func session(forChannel channel: DownloadChannel, segmentCount: Int) -> URLSession {
    return session(forIdentifier: Self.sessionIdentifier(for: channel),
                   segmentCount: segmentCount)
  }

  private func session(forIdentifier identifier: String,
                       segmentCount: Int = RangeDownloader.defaultSegmentCount) -> URLSession {
    lock.lock()
    if let existing = sessions[identifier] {
      lock.unlock()
      return existing
    }
    lock.unlock()
    let cfg = URLSessionConfiguration.background(withIdentifier: identifier)
    cfg.tlsMinimumSupportedProtocolVersion = .TLSv12
    cfg.isDiscretionary = false
    cfg.sessionSendsLaunchEvents = true
    cfg.httpMaximumConnectionsPerHost = segmentCount
    // §5.4: connection / request timeout. The session is cached per channel, so
    // this uses the default; per-run knobs are enforced by the JS-side deadline
    // and the in-app stall watchdog rather than re-creating the session. A
    // background session keeps the resource timeout generous so a legitimately
    // long suspended transfer is not killed by the OS resource clock (§5.10).
    cfg.timeoutIntervalForRequest = RangeDownloader.defaultRequestTimeoutSeconds
    let created = URLSession(configuration: cfg, delegate: self, delegateQueue: nil)
    lock.lock()
    // Another thread may have raced us; prefer the first-stored session.
    if let existing = sessions[identifier] {
      lock.unlock()
      created.invalidateAndCancel()
      return existing
    }
    sessions[identifier] = created
    lock.unlock()
    return created
  }

  // MARK: - Listeners

  public func addListener(_ callback: @escaping (RangeDownloadEvent) -> Void) -> Int {
    lock.lock(); defer { lock.unlock() }
    let id = nextListenerId
    nextListenerId += 1
    listeners[id] = callback
    return id
  }

  public func removeListener(_ id: Int) {
    lock.lock(); defer { lock.unlock() }
    listeners.removeValue(forKey: id)
  }

  private func emit(channel: DownloadChannel, taskId: String, type: String,
                    progress: Double, message: String) {
    let snapshot: [(RangeDownloadEvent) -> Void] = lock.withLockValue {
      Array(self.listeners.values)
    }
    guard !snapshot.isEmpty else { return }
    let event = RangeDownloadEvent(
      channel: channel, taskId: taskId, type: type, progress: progress, message: message
    )
    for cb in snapshot { cb(event) }
  }

  // MARK: - Run table helpers

  private static func runKey(channel: DownloadChannel, taskId: String) -> String {
    return "\(channel.stringValue)|\(taskId)"
  }

  /// taskDescription codec: "channel|taskId|segIndex".
  private static func encodeTaskDescription(channel: DownloadChannel, taskId: String,
                                            segIndex: Int) -> String {
    return "\(channel.stringValue)|\(taskId)|\(segIndex)"
  }

  private static func decodeTaskDescription(_ desc: String)
    -> (channel: DownloadChannel, taskId: String, segIndex: Int)? {
    // taskId may itself contain '|'? Keep it conservative: split into exactly 3
    // by taking the first and last fields, joining the middle as taskId.
    let parts = desc.components(separatedBy: "|")
    guard parts.count >= 3,
          let channel = DownloadChannel(fromString: parts[0]),
          let segIndex = Int(parts[parts.count - 1]) else { return nil }
    let taskId = parts[1..<(parts.count - 1)].joined(separator: "|")
    return (channel, taskId, segIndex)
  }

  private func run(forKey key: String) -> RunState? {
    lock.withLockValue { self.runs[key] }
  }

  private func run(for desc: String) -> (run: RunState, segIndex: Int)? {
    guard let decoded = Self.decodeTaskDescription(desc) else { return nil }
    let key = Self.runKey(channel: decoded.channel, taskId: decoded.taskId)
    guard let r = run(forKey: key) else { return nil }
    return (r, decoded.segIndex)
  }

  private static let firmwareTaskDescriptionPrefix = "firmware-v1:"

  private static func encodeFirmwareTaskDescription(
    _ descriptor: FirmwareBackgroundTaskDescriptor
  ) throws -> String {
    let data = try JSONEncoder().encode(descriptor)
    return firmwareTaskDescriptionPrefix + data.base64EncodedString()
  }

  private static func decodeFirmwareTaskDescription(
    _ description: String?
  ) -> FirmwareBackgroundTaskDescriptor? {
    guard
      let description,
      description.hasPrefix(firmwareTaskDescriptionPrefix),
      let data = Data(
        base64Encoded: String(
          description.dropFirst(firmwareTaskDescriptionPrefix.count)
        )
      ),
      data.count <= 2048,
      let descriptor = try? JSONDecoder().decode(
        FirmwareBackgroundTaskDescriptor.self,
        from: data
      ),
      descriptor.schemaVersion == 1,
      descriptor.taskId.range(
        of: "^[A-Za-z0-9._-]{1,100}$",
        options: .regularExpression
      ) != nil,
      descriptor.transactionId.range(
        of: "^[A-Za-z0-9._:-]{1,160}$",
        options: .regularExpression
      ) != nil,
      descriptor.leaseRef.range(
        of: "^fwlease:[a-f0-9-]{36}$",
        options: .regularExpression
      ) != nil,
      descriptor.expectedSize > 0,
      descriptor.expectedSize <= 512 * 1024 * 1024,
      descriptor.expectedSha256.range(
        of: "^[a-f0-9]{64}$",
        options: .regularExpression
      ) != nil,
      !descriptor.hostname.isEmpty,
      descriptor.hostname.count <= 253,
      descriptor.deadlineAt.isFinite,
      descriptor.deadlineAt > 0
    else {
      return nil
    }
    return descriptor
  }

  func downloadFirmwareArtifact(
    params: FirmwareArtifactDownloadParams
  ) async throws -> StoredFirmwareArtifact {
    guard
      params.routeType == "domain",
      let url = URL(string: params.url),
      let hostname = url.host?.lowercased()
    else {
      throw FirmwareBackgroundDownloadError.invalidTask
    }
    let deadlineSeconds = params.overallDeadlineSeconds ?? 180
    guard
      deadlineSeconds.isFinite,
      deadlineSeconds > 0,
      deadlineSeconds <= 24 * 60 * 60
    else {
      throw FirmwareBackgroundDownloadError.invalidTask
    }
    let descriptor = FirmwareBackgroundTaskDescriptor(
      schemaVersion: 1,
      taskId: params.taskId,
      transactionId: params.transactionId,
      leaseRef: params.leaseRef,
      expectedSize: Int64(params.expectedSize),
      expectedSha256: params.expectedSha256.lowercased(),
      hostname: hostname,
      deadlineAt: Date().timeIntervalSince1970 + deadlineSeconds
    )
    if let stored = try FirmwareArtifactStore.shared.storedArtifact(
      expectedSize: descriptor.expectedSize,
      expectedSha256: descriptor.expectedSha256
    ) {
      return stored
    }

    return try await withCheckedThrowingContinuation { continuation in
      lock.withLockValue {
        firmwareWaiters[descriptor.key, default: []].append(continuation)
      }
      Task { [weak self] in
        await self?.reconcileOrStartFirmwareTask(
          descriptor: descriptor,
          url: url
        )
      }
    }
  }

  private func reconcileOrStartFirmwareTask(
    descriptor: FirmwareBackgroundTaskDescriptor,
    url: URL
  ) async {
    let session = session(forChannel: .firmware, segmentCount: 1)
    let tasks = await allTasks(in: session)
    var matchingTaskFound = false
    for task in tasks {
      guard
        let candidate = Self.decodeFirmwareTaskDescription(
          task.taskDescription
        )
      else {
        continue
      }
      if candidate.hasSameArtifactIdentity(as: descriptor) {
        matchingTaskFound = true
      } else if candidate.taskId == descriptor.taskId {
        task.cancel()
      }
    }
    if matchingTaskFound {
      return
    }
    do {
      if let stored = try FirmwareArtifactStore.shared.storedArtifact(
        expectedSize: descriptor.expectedSize,
        expectedSha256: descriptor.expectedSha256
      ) {
        finishFirmwareTask(
          key: descriptor.key,
          result: .success(stored)
        )
        return
      }
      guard Date().timeIntervalSince1970 < descriptor.deadlineAt else {
        throw FirmwareBackgroundDownloadError.deadlineExceeded
      }
      var request = URLRequest(url: url)
      request.cachePolicy = .reloadIgnoringLocalCacheData
      request.timeoutInterval = max(
        1,
        descriptor.deadlineAt - Date().timeIntervalSince1970
      )
      request.setValue("identity", forHTTPHeaderField: "Accept-Encoding")
      let task = session.downloadTask(with: request)
      task.taskDescription = try Self.encodeFirmwareTaskDescription(
        descriptor
      )
      task.resume()
    } catch {
      finishFirmwareTask(
        key: descriptor.key,
        result: .failure(error)
      )
    }
  }

  private func allTasks(in session: URLSession) async -> [URLSessionTask] {
    await withCheckedContinuation { continuation in
      session.getAllTasks { tasks in
        continuation.resume(returning: tasks)
      }
    }
  }

  private func finishFirmwareTask(
    key: String,
    result: Result<StoredFirmwareArtifact, Error>
  ) {
    let continuations: [
      CheckedContinuation<StoredFirmwareArtifact, Error>
    ] = lock.withLockValue {
      firmwareWaiters.removeValue(forKey: key) ?? []
    }
    for continuation in continuations {
      switch result {
      case let .success(artifact):
        continuation.resume(returning: artifact)
      case let .failure(error):
        continuation.resume(throwing: error)
      }
    }
  }

  // MARK: - Public entry

  /// Downloads [urlString] into [filePath] using concurrent background ranges.
  /// Returns `(.completed, filePath, nil, nil)` on success, or a typed fallback
  /// tuple `(.fallbackTransient | .fallbackPermanent, filePath, reason, kind)`
  /// when the caller should use its single-stream path. Transient network errors
  /// resolve to `.fallbackTransient` and KEEP the `.segN` files for the next
  /// attempt; permanent ones discard them.
  public func download(
    channel: DownloadChannel,
    taskId: String,
    urlString: String,
    filePath: String,
    expectedSha256: String?,
    segmentCount: Int?,
    minConcurrentBytes: Int64?,
    maxSegmentAttempts: Int? = nil,
    requestTimeoutSeconds: Double? = nil,
    stallTimeoutSeconds: Double? = nil,
    overallDeadlineSeconds: Double? = nil
  ) async -> (RangeDownloadClass, String, String?, RangeFallbackClass?) {
    let segCount = max(1, segmentCount ?? Self.defaultSegmentCount)
    let minBytes = minConcurrentBytes ?? Self.defaultMinConcurrentBytes
    let maxAttempts = max(1, maxSegmentAttempts ?? Self.defaultMaxSegmentAttempts)
    // §5.4: caller-tunable stall window for this run's watchdog; the background
    // URLSession's per-request timeout is cached per channel (see `session(...)`),
    // so the per-run request timeout is enforced via the deadline + stall watchdog
    // rather than re-creating the session.
    let stallSeconds = (stallTimeoutSeconds.map { $0 > 0 ? $0 : nil } ?? nil)
      ?? Self.defaultStallTimeoutSeconds
    _ = requestTimeoutSeconds // documented above; session timeout is channel-cached
    let deadline: Date? = {
      guard let s = overallDeadlineSeconds, s > 0 else { return nil }
      return Date().addingTimeInterval(s)
    }()
    let key = Self.runKey(channel: channel, taskId: taskId)

    guard let url = URL(string: urlString) else {
      return (.fallbackPermanent, filePath, "invalid url", .rangeUnsupported)
    }
    // HTTPS-only: background URLSession + transport hardening.
    guard urlString.hasPrefix("https://") else {
      return (.fallbackPermanent, filePath, "url must use https", .redirectRejected)
    }

    // §5.8 (G7): at most one live run per destination. A second download() for
    // the same key/filePath while one is in flight joins-or-fails-fast rather
    // than overwriting RunState and co-writing the same `.segN`. The guard keys
    // off `runs[key]` MEMBERSHIP, not `continuation != nil`: the continuation is
    // only assigned after probe + insert, so a `continuation != nil` test left a
    // window (insert → continuation assignment, and the synchronous
    // allSegmentsPresent fast path which never sets a continuation at all) where
    // a half-initialized run was invisible and a 2nd download() could overwrite
    // `runs[key]`. We fail fast (the caller retry loop re-drives cleanly) to
    // avoid join bookkeeping hangs.
    if let existing = run(forKey: key), existing.filePath == filePath {
      return (.fallbackTransient, filePath,
              "another run is already active for this destination", .budgetExhausted)
    }

    let probe: ProbeResult
    do {
      probe = try await self.probe(url: url)
    } catch {
      // A probe that cannot reach the server is a transient network condition.
      return (.fallbackTransient, filePath,
              "probe failed: \(error.localizedDescription)", .transientNetwork)
    }
    guard probe.supportsRange, probe.total >= minBytes else {
      return (.fallbackPermanent, filePath,
              "range unsupported or file too small", .rangeUnsupported)
    }

    let state = RunState(
      channel: channel, taskId: taskId, filePath: filePath,
      segmentCount: segCount,
      sessionIdentifier: Self.sessionIdentifier(for: channel),
      maxSegmentAttempts: maxAttempts, deadline: deadline
    )
    state.totalSize = probe.total
    state.etag = probe.etag
    state.url = url
    state.hasValidator = (probe.etag?.isEmpty == false)
    state.ranges = RangeDownloadLogic.planRanges(total: probe.total, segments: segCount)
    state.segmentWritten = [Int64](repeating: 0, count: state.ranges.count)

    // §5.8 (G7): claim the destination slot atomically. Re-check membership under
    // the SAME lock hold as the insert so two download() calls that both passed
    // the early guard (which ran before their respective async probes) cannot
    // both insert — the loser fails fast and never co-writes `.segN`. From this
    // insert onward `runs[key]` membership is the single-flight authority, so a
    // half-initialized run (continuation not yet assigned, or the synchronous
    // fast path that never assigns one) is still observed as live by any racer.
    lock.lock()
    if let existing = runs[key], existing.filePath == filePath, existing !== state {
      lock.unlock()
      return (.fallbackTransient, filePath,
              "another run is already active for this destination", .budgetExhausted)
    }
    runs[key] = state
    lock.unlock()

    let ranges = state.ranges

    // Without a strong validator (ETag) we cannot pin stashed `.segN` to a
    // specific server object via If-Range, so any leftover segments from a prior
    // attempt are untrustworthy. Start fresh and only proceed on the resumable
    // path when expectedSha256 will gate the assembled file (verified below).
    if !state.hasValidator {
      cleanupSegments(state: state, ranges: ranges)
    }

    emit(channel: channel, taskId: taskId, type: "start", progress: 0, message: "")

    // §5.4: start the bytes-stalled watchdog for this run. It cancels a stalled
    // segment task so didCompleteWithError routes it through the in-place retry
    // path. It only counts foreground/active wall time toward the stall window so
    // a legitimately suspended background transfer (§5.10) is never false-cancelled.
    startStallWatchdog(key: key, stallSeconds: stallSeconds)

    do {
      // If every segment is already on disk (resume after suspension/kill), skip
      // straight to concatenation.
      if allSegmentsPresent(state: state, ranges: ranges) {
        try concatenateAndFinish(state: state, ranges: ranges)
      } else {
        try await withCheckedThrowingContinuation { (cont: CheckedContinuation<Void, Error>) in
          self.lock.lock()
          state.continuation = cont
          self.lock.unlock()
          reconcileAndStartTasks(state: state, url: url, ranges: ranges)
        }
      }
    } catch let fb as FallbackError {
      clearRun(key: key)
      emit(channel: channel, taskId: taskId, type: "fallback", progress: 0, message: fb.reason)
      return (fb.kind.failureClass, filePath, fb.reason, fb.kind)
    } catch {
      // A non-FallbackError thrown out of the run is a local/transient condition
      // (disk I/O, an interrupted segment) → ask the caller to retry the
      // concurrent path; segments are retained for resume.
      clearRun(key: key)
      let reason = error.localizedDescription
      emit(channel: channel, taskId: taskId, type: "fallback", progress: 0, message: reason)
      return (.fallbackTransient, filePath, reason, .transientNetwork)
    }

    clearRun(key: key)

    // Optional immediate SHA256 self-check backstop. When omitted, the caller
    // verifies after the fact. A whole-file checksum mismatch is Permanent (§4):
    // the assembled bytes are unsalvageable, so discard final + artifacts.
    if let expected = expectedSha256, !expected.isEmpty {
      let actual = RangeDownloadLogic.calculateSHA256(filePath)
      if actual?.lowercased() != expected.lowercased() {
        try? FileManager.default.removeItem(atPath: filePath)
        discardArtifacts(filePath: filePath)
        let reason = "sha256 mismatch (expected \(expected), got \(actual ?? "nil"))"
        OneKeyLog.error("RangeDownloader", "\(channel.stringValue)/\(taskId): \(reason)")
        emit(channel: channel, taskId: taskId, type: "fallback", progress: 0, message: reason)
        return (.fallbackPermanent, filePath, reason, .checksumMismatch)
      }
    }

    emit(channel: channel, taskId: taskId, type: "complete", progress: 100, message: "")
    return (.completed, filePath, nil, nil)
  }

  private func clearRun(key: String) {
    lock.lock(); runs.removeValue(forKey: key); lock.unlock()
  }

  // MARK: - Range planning / probing

  private struct ProbeResult { let total: Int64; let etag: String?; let supportsRange: Bool }

  struct FallbackError: Error {
    let reason: String
    /// §4 typed sub-class. Defaults to a Permanent `serverIgnoredRange` only so an
    /// un-annotated legacy throw keeps the previous "discard + single-stream"
    /// behavior; all new throw sites pass an explicit kind.
    let kind: RangeFallbackClass
    init(reason: String, kind: RangeFallbackClass = .serverIgnoredRange) {
      self.reason = reason
      self.kind = kind
    }
  }

  // HTTP status classification (classifyStatus), Retry-After parsing
  // (parseRetryAfterSeconds), Content-Range parsing and backoff math now live on
  // `RangeDownloadLogic` (OCDS §4 / §5). Callsites use `RangeDownloadLogic.<fn>`.

  /// One-byte Range request on a default (foreground) session to learn total
  /// size + ETag + Range support. Background sessions can't do data tasks, so
  /// the probe uses an ephemeral session.
  private func probe(url: URL) async throws -> ProbeResult {
    var req = URLRequest(url: url)
    req.setValue("bytes=0-0", forHTTPHeaderField: "Range")
    let cfg = URLSessionConfiguration.ephemeral
    cfg.tlsMinimumSupportedProtocolVersion = .TLSv12
    let probeSession = URLSession(configuration: cfg)
    defer { probeSession.finishTasksAndInvalidate() }

    return try await withCheckedThrowingContinuation { cont in
      let task = probeSession.dataTask(with: req) { _, response, error in
        if let error = error { cont.resume(throwing: error); return }
        guard let http = response as? HTTPURLResponse else {
          cont.resume(throwing: FallbackError(reason: "no http response")); return
        }
        let etag = http.value(forHTTPHeaderField: "ETag")
        if http.statusCode == 206,
           let cr = http.value(forHTTPHeaderField: "Content-Range"),
           let total = RangeDownloadLogic.parseContentRangeTotal(cr) {
          cont.resume(returning: ProbeResult(total: total, etag: etag, supportsRange: true))
        } else {
          // 200 (Range ignored) or anything else → single-stream.
          cont.resume(returning: ProbeResult(total: 0, etag: etag, supportsRange: false))
        }
      }
      task.resume()
    }
  }

  // MARK: - Task reconciliation (handles app relaunch)

  /// Ensures exactly one in-flight (or completed) artifact per missing segment:
  /// segments already on disk as `.segN` are skipped; segments that already
  /// have a running task in the (recreated) background session for THIS run are
  /// left alone; the rest get a fresh Range download task.
  private func reconcileAndStartTasks(state: RunState, url: URL,
                                      ranges: [(start: Int64, end: Int64)]) {
    let session = session(forChannel: state.channel, segmentCount: state.segmentCount)
    session.getAllTasks { [weak self] tasks in
      guard let self = self else { return }
      var liveIndexes = Set<Int>()
      for t in tasks {
        guard let desc = t.taskDescription,
              let decoded = Self.decodeTaskDescription(desc) else {
          // Unrecognized task description on our channel session — leave it.
          continue
        }
        // Only adopt tasks belonging to THIS run (same channel|taskId). Stale
        // tasks from a DIFFERENT taskId on the same channel session that point
        // at a different URL are cancelled so their segment indexes can't
        // collide with this run's.
        let belongsToThisRun =
          decoded.channel.stringValue == state.channel.stringValue
            && decoded.taskId == state.taskId
        if belongsToThisRun {
          if let turl = t.originalRequest?.url,
             turl.absoluteString == url.absoluteString,
             t.state == .running || t.state == .suspended {
            liveIndexes.insert(decoded.segIndex)
          } else {
            // Same run id but different URL (etag/url changed) — drop it.
            t.cancel()
          }
        }
        // Tasks for other taskIds on the same channel are left running so
        // concurrent downloads in the same channel are not disturbed.
      }
      for (idx, range) in ranges.enumerated() {
        if FileManager.default.fileExists(atPath: state.segPath(idx)) { continue }
        if liveIndexes.contains(idx) { continue }
        self.enqueueSegment(state: state, session: session, url: url,
                            idx: idx, range: range)
      }
      // It's possible every segment was already present but the early
      // allSegmentsPresent check raced a just-finished task; re-check.
      if self.allSegmentsPresent(state: state, ranges: ranges) {
        self.finishContinuation(state: state, with: nil, ranges: ranges)
      }
    }
  }

  /// Creates and starts a single Range download task for [idx]. Factored out of
  /// `reconcileAndStartTasks` so the delegate can re-enqueue ONE segment on a
  /// transient failure (§5.4) without touching the others. Caller must ensure no
  /// live task already exists for this index (double-enqueue guard).
  private func enqueueSegment(state: RunState, session: URLSession, url: URL,
                              idx: Int, range: (start: Int64, end: Int64)) {
    var req = URLRequest(url: url)
    req.setValue("bytes=\(range.start)-\(range.end)", forHTTPHeaderField: "Range")
    if let etag = state.etag { req.setValue(etag, forHTTPHeaderField: "If-Range") }
    let task = session.downloadTask(with: req)
    task.taskDescription = Self.encodeTaskDescription(
      channel: state.channel, taskId: state.taskId, segIndex: idx
    )
    // Reset this segment's progress estimate so a re-enqueued attempt doesn't
    // double-count bytes from the failed attempt in the aggregate progress.
    // §5.4 (G2): the real task now exists and is visible to `getAllTasks`, so
    // clear the pending-retry marker — the in-flight check is authoritative again.
    lock.lock()
    if idx < state.segmentWritten.count { state.segmentWritten[idx] = 0 }
    state.pendingRetryIndexes.remove(idx)
    state.lastProgressAt = Date()
    lock.unlock()
    task.resume()
  }

  /// Marks a segment's finished body as a TRANSIENT HTTP outcome (§4) so
  /// didCompleteWithError re-enqueues just that segment instead of failing the run.
  private func markSegmentTransient(state: RunState, idx: Int, retryAfter: Double?) {
    lock.lock()
    state.transientSegmentRetryAfter[idx] = retryAfter
    lock.unlock()
  }

  /// §5.4: re-enqueue a single transient-failed segment under its attempt budget,
  /// with jittered exponential backoff (overridden by `Retry-After`). Returns true
  /// when a retry was scheduled; false when the budget/deadline is exhausted (the
  /// caller then finalizes the run as a resumable transient fallback). Must be
  /// called from a delegate callback for [idx].
  private func retrySegmentIfUnderBudget(state: RunState, idx: Int,
                                         retryAfter: Double?) -> Bool {
    // Deadline check first (§5.11 single-run bound).
    if state.isPastDeadline { return false }
    // §5.4 (G2): claim the pending-retry slot and bump the attempt counter under
    // ONE lock hold. If a retry is already pending for this idx (its asyncAfter
    // hasn't fired yet), a concurrent caller — e.g. a sibling segment's
    // completion driving the missing-segment re-enqueue — must NOT bump the
    // counter again nor arm a duplicate asyncAfter. Returning `true` here reports
    // "a retry is in flight for this segment" without scheduling a second one.
    let claim: (alreadyPending: Bool, attempts: Int) = lock.withLockValue {
      if state.pendingRetryIndexes.contains(idx) {
        return (true, state.segmentAttempts[idx] ?? 0)
      }
      let n = (state.segmentAttempts[idx] ?? 0) + 1
      state.segmentAttempts[idx] = n
      state.pendingRetryIndexes.insert(idx)
      return (false, n)
    }
    if claim.alreadyPending { return true }
    let attempts = claim.attempts
    if attempts > state.maxSegmentAttempts {
      // Over budget: release the slot we just claimed so a later genuine retry
      // (if any) isn't blocked, and report exhausted.
      lock.withLockValue { _ = state.pendingRetryIndexes.remove(idx) }
      return false
    }
    guard let url = lock.withLockValue({ state.url }) else {
      lock.withLockValue { _ = state.pendingRetryIndexes.remove(idx) }
      return false
    }
    let ranges = lock.withLockValue { state.ranges }
    guard idx < ranges.count else {
      lock.withLockValue { _ = state.pendingRetryIndexes.remove(idx) }
      return false
    }
    let range = ranges[idx]
    let delay = RangeDownloadLogic.backoffDelay(attempt: attempts, retryAfter: retryAfter)
    let session = session(forChannel: state.channel, segmentCount: state.segmentCount)
    let channelStr = state.channel.stringValue
    let taskId = state.taskId
    OneKeyLog.info("RangeDownloader",
                   "\(channelStr)/\(taskId): retry segment \(idx) attempt \(attempts)/\(state.maxSegmentAttempts) in \(String(format: "%.2f", delay))s")
    DispatchQueue.global().asyncAfter(deadline: .now() + delay) { [weak self] in
      guard let self = self else { return }
      // Bail if the run was cleared (cancel / finalize) or the segment landed
      // in the meantime, and guard against a double-enqueue. On every bail path
      // clear the pending-retry marker (G2) so it doesn't leak: either the slot
      // is moot (run gone / segment landed) or a real task already exists.
      guard let live = self.run(forKey: Self.runKey(channel: state.channel, taskId: state.taskId)),
            live === state else {
        self.lock.withLockValue { _ = state.pendingRetryIndexes.remove(idx) }
        return
      }
      if FileManager.default.fileExists(atPath: state.segPath(idx)) {
        self.lock.withLockValue { _ = state.pendingRetryIndexes.remove(idx) }
        return
      }
      session.getAllTasks { tasks in
        let alreadyLive = tasks.contains { t in
          guard let d = t.taskDescription,
                let decoded = Self.decodeTaskDescription(d),
                decoded.channel.stringValue == channelStr,
                decoded.taskId == taskId,
                decoded.segIndex == idx else { return false }
          return t.state == .running || t.state == .suspended
        }
        if alreadyLive {
          self.lock.withLockValue { _ = state.pendingRetryIndexes.remove(idx) }
          return
        }
        // enqueueSegment clears the pending marker once the real task exists.
        self.enqueueSegment(state: state, session: session, url: url,
                            idx: idx, range: range)
      }
    }
    return true
  }

  /// §4 (416): a segment got `416 Range Not Satisfiable`. Per §4 a bare 416 must
  /// NOT discard resumable bytes and must NOT blindly re-request the same range —
  /// the total/validator is re-evaluated first. We re-probe the URL:
  ///   • Probe fails / range no longer supported → transient; retry the segment
  ///     in place under budget (the network blip will clear), keeping `.segN`.
  ///   • Total or ETag CHANGED → the object changed under us; the planned ranges
  ///     are stale → object-change: wipe + restart (Permanent), so the run
  ///     re-plans against the new object instead of stitching mismatched bytes.
  ///   • Total/ETag UNCHANGED → the range is genuinely still valid (a transient
  ///     server hiccup); keep `.segN` and retry the segment normally.
  /// Must be called from a delegate callback for [idx]; runs the re-probe async.
  private func reevaluateSizeThenRetry(state: RunState, session: URLSession,
                                       idx: Int, retryAfter: Double?,
                                       ranges: [(start: Int64, end: Int64)]) {
    guard let url = lock.withLockValue({ state.url }) else {
      finalizeTransientFallback(state: state, idx: idx,
                                reason: "segment \(idx) 416 but url missing", ranges: ranges)
      return
    }
    let priorTotal = lock.withLockValue { state.totalSize }
    let priorEtag = lock.withLockValue { state.etag }
    let channelStr = state.channel.stringValue
    let taskId = state.taskId
    OneKeyLog.info("RangeDownloader",
                   "\(channelStr)/\(taskId): segment \(idx) 416 — re-evaluating size before retry")
    Task { [weak self] in
      guard let self = self else { return }
      // Re-confirm the run is still live before acting on the re-probe.
      let stillLive = self.run(forKey: Self.runKey(channel: state.channel, taskId: state.taskId)).map { $0 === state } ?? false
      guard stillLive else { return }
      let probe: ProbeResult
      do {
        probe = try await self.probe(url: url)
      } catch {
        // Re-probe itself failed → transient network condition. Retry the
        // segment in place; the original range is unchanged and `.segN` is kept.
        if self.retrySegmentIfUnderBudget(state: state, idx: idx, retryAfter: retryAfter) { return }
        self.finalizeTransientFallback(state: state, idx: idx,
                                       reason: "segment \(idx) 416; re-probe failed, retry budget exhausted",
                                       ranges: ranges)
        return
      }
      let totalChanged = !probe.supportsRange || probe.total != priorTotal
      let etagChanged = (probe.etag?.isEmpty == false || priorEtag?.isEmpty == false)
        && (probe.etag != priorEtag)
      if totalChanged || etagChanged {
        // §4: the object changed under us — the planned `.segN` ranges no longer
        // describe this object. Object-change → wipe + restart (Permanent) so a
        // fresh run re-plans; never stitch bytes from two different objects.
        OneKeyLog.info("RangeDownloader",
                       "\(channelStr)/\(taskId): segment \(idx) 416 → object changed (total \(priorTotal)→\(probe.total), etag \(priorEtag ?? "nil")→\(probe.etag ?? "nil")); wipe + restart")
        self.lock.lock()
        state.fellBack = true
        state.fellBackKind = .multipartOrBadTotal
        self.lock.unlock()
        self.finalizePermanentFallback(state: state, session: session, ranges: ranges)
        return
      }
      // Total/validator unchanged → the 416 was a transient server hiccup; the
      // range is still valid. Keep `.segN` and retry this segment normally.
      OneKeyLog.info("RangeDownloader",
                     "\(channelStr)/\(taskId): segment \(idx) 416 → size unchanged (total \(priorTotal)); retrying range as-is")
      if self.retrySegmentIfUnderBudget(state: state, idx: idx, retryAfter: retryAfter) { return }
      self.finalizeTransientFallback(state: state, idx: idx,
                                     reason: "segment \(idx) 416; size unchanged, retry budget exhausted",
                                     ranges: ranges)
    }
  }

  // backoffDelay (§5.4) now lives on `RangeDownloadLogic`.

  // MARK: - Stall watchdog (§5.4)

  /// Periodically checks whether the run has received any bytes within the stall
  /// window. A stalled segment task is cancelled so didCompleteWithError routes it
  /// through the in-place retry path. The watchdog stops itself when the run is no
  /// longer live (finalized / cancelled). It is intentionally lenient: it only
  /// fires when wall time since the last byte exceeds the window AND there is an
  /// in-flight task, so a suspended background transfer (which makes no JS/main
  /// progress while suspended) is not false-cancelled because the watchdog timer
  /// itself is also suspended with the app.
  private func startStallWatchdog(key: String, stallSeconds: Double) {
    let interval = max(5.0, stallSeconds / 2.0)
    DispatchQueue.global().asyncAfter(deadline: .now() + interval) { [weak self] in
      self?.stallWatchdogTick(key: key, stallSeconds: stallSeconds, interval: interval)
    }
  }

  private func stallWatchdogTick(key: String, stallSeconds: Double, interval: Double) {
    guard let state = run(forKey: key),
          lock.withLockValue({ state.continuation != nil }) else {
      return // run finalized / cancelled — stop.
    }
    let last = lock.withLockValue { state.lastProgressAt }
    let stalled = Date().timeIntervalSince(last) >= stallSeconds
    if stalled {
      let channelStr = state.channel.stringValue
      let taskId = state.taskId
      let session = session(forChannel: state.channel, segmentCount: state.segmentCount)
      session.getAllTasks { tasks in
        for t in tasks {
          guard let d = t.taskDescription,
                let decoded = Self.decodeTaskDescription(d),
                decoded.channel.stringValue == channelStr,
                decoded.taskId == taskId,
                t.state == .running else { continue }
          OneKeyLog.info("RangeDownloader",
                         "\(channelStr)/\(taskId): stall watchdog cancelling segment \(decoded.segIndex) (no bytes for \(String(format: "%.0f", stallSeconds))s)")
          // Mark this index as stall-cancelled so didCompleteWithError treats the
          // resulting NSURLErrorCancelled as a transient stall, not a user cancel.
          self.lock.lock(); state.stallCancelledIndexes.insert(decoded.segIndex); self.lock.unlock()
          t.cancel()
        }
      }
      // Re-stamp so we don't repeatedly cancel within the same window while the
      // cancel + retry round-trips.
      lock.lock(); state.lastProgressAt = Date(); lock.unlock()
    }
    // Reschedule.
    DispatchQueue.global().asyncAfter(deadline: .now() + interval) { [weak self] in
      self?.stallWatchdogTick(key: key, stallSeconds: stallSeconds, interval: interval)
    }
  }

  private func allSegmentsPresent(state: RunState,
                                  ranges: [(start: Int64, end: Int64)]) -> Bool {
    for (idx, range) in ranges.enumerated() {
      let expected = range.end - range.start + 1
      let p = state.segPath(idx)
      guard FileManager.default.fileExists(atPath: p),
            let attrs = try? FileManager.default.attributesOfItem(atPath: p),
            let size = attrs[.size] as? Int64, size == expected else {
        return false
      }
    }
    return true
  }

  // MARK: - URLSessionDownloadDelegate

  public func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask,
                  didWriteData bytesWritten: Int64, totalBytesWritten: Int64,
                  totalBytesExpectedToWrite: Int64) {
    if let descriptor = Self.decodeFirmwareTaskDescription(
      downloadTask.taskDescription
    ) {
      let exceedsBound =
        totalBytesWritten > descriptor.expectedSize ||
        (
          totalBytesExpectedToWrite != NSURLSessionTransferSizeUnknown &&
            totalBytesExpectedToWrite != descriptor.expectedSize
        )
      let expired =
        Date().timeIntervalSince1970 >= descriptor.deadlineAt
      if exceedsBound || expired {
        recordFirmwareTaskError(
          taskIdentifier: downloadTask.taskIdentifier,
          error: exceedsBound
            ? FirmwareBackgroundDownloadError.sizeRejected
            : FirmwareBackgroundDownloadError.deadlineExceeded
        )
        downloadTask.cancel()
      }
      return
    }
    guard let desc = downloadTask.taskDescription,
          let (state, idx) = run(for: desc) else { return }
    lock.lock()
    if idx < state.segmentWritten.count { state.segmentWritten[idx] = totalBytesWritten }
    // §5.4: stamp last-progress for the stall watchdog. Any byte on any segment
    // counts as the run making progress.
    state.lastProgressAt = Date()
    let sum = state.segmentWritten.reduce(0, +)
    let total = state.totalSize
    var emit = false
    if total > 0 {
      let p = Int((sum * 100) / total)
      // §5.7 monotonic non-decreasing: only emit on a strict increase, so a
      // transient re-enqueue (which resets segmentWritten[idx]=0 and dips the
      // aggregate) never ticks the bar backward — prevProgress is the run's
      // running max. A genuine restart resets prevProgress separately.
      if p > state.prevProgress { state.prevProgress = p; emit = true }
    }
    let progressValue = total > 0 ? Int((sum * 100) / total) : 0
    let channel = state.channel
    let taskId = state.taskId
    lock.unlock()
    if emit {
      self.emit(channel: channel, taskId: taskId, type: "progress",
                progress: Double(progressValue), message: "")
    }
  }

  public func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask,
                  didFinishDownloadingTo location: URL) {
    if let descriptor = Self.decodeFirmwareTaskDescription(
      downloadTask.taskDescription
    ) {
      guard firmwareTaskError(
        taskIdentifier: downloadTask.taskIdentifier
      ) == nil else {
        return
      }
      do {
        guard
          Date().timeIntervalSince1970 < descriptor.deadlineAt,
          let response = downloadTask.response as? HTTPURLResponse,
          response.statusCode == 200,
          response.url?.scheme?.lowercased() == "https",
          response.url?.host?.lowercased() == descriptor.hostname,
          response.url?.port == nil || response.url?.port == 443
        else {
          throw FirmwareBackgroundDownloadError.responseRejected
        }
        let artifact = try FirmwareArtifactStore.shared.acceptBackgroundDownload(
          temporaryURL: location,
          leaseRef: descriptor.leaseRef,
          transactionId: descriptor.transactionId,
          expectedSize: descriptor.expectedSize,
          expectedSha256: descriptor.expectedSha256
        )
        lock.withLockValue {
          firmwareTaskErrors.removeValue(
            forKey: downloadTask.taskIdentifier
          )
          completedFirmwareTasks.insert(downloadTask.taskIdentifier)
        }
        finishFirmwareTask(
          key: descriptor.key,
          result: .success(artifact)
        )
      } catch {
        recordFirmwareTaskError(
          taskIdentifier: downloadTask.taskIdentifier,
          error: error
        )
      }
      return
    }
    guard let desc = downloadTask.taskDescription,
          let (state, idx) = run(for: desc) else { return }
    let ranges = lock.withLockValue { state.ranges }
    guard idx < ranges.count else { return }
    let range = ranges[idx]
    let expectedLen = range.end - range.start + 1

    // We require a 206 Partial Content that matches the requested byte range.
    // Anything else is classified per §4: a Permanent status (200/401/403/404/
    // 410/501/505/other-4xx) flags the whole run for discard+single-stream; a
    // Transient status (408/416/429/5xx) marks just THIS segment for in-place
    // retry (G2) and keeps the other segments / `.segN`. Finalize/retry happens
    // in didCompleteWithError.
    guard let http = downloadTask.response as? HTTPURLResponse else {
      // No HTTP response on a finished body is anomalous → treat as transient.
      markSegmentTransient(state: state, idx: idx, retryAfter: nil)
      return
    }
    if http.statusCode != 206 {
      let kind = RangeDownloadLogic.classifyStatus(http.statusCode)
      if kind.failureClass == .fallbackTransient {
        // §4 (416): a 416 is transient but the requested range may no longer be
        // satisfiable (the object shrank/changed). Flag this segment for a size
        // re-evaluation (re-probe) BEFORE the range is re-requested, instead of
        // blindly re-asking for the same bytes.
        if http.statusCode == 416 {
          lock.lock(); state.sizeReevalIndexes.insert(idx); lock.unlock()
        }
        let retryAfter = RangeDownloadLogic.parseRetryAfterSeconds(
          http.value(forHTTPHeaderField: "Retry-After"))
        markSegmentTransient(state: state, idx: idx, retryAfter: retryAfter)
      } else {
        lock.lock(); state.fellBack = true; state.fellBackKind = kind; lock.unlock()
      }
      return
    }
    // §5.5: reject multipart/byteranges — we requested exactly one range and can
    // only assemble a single contiguous body per segment.
    if let ctype = http.value(forHTTPHeaderField: "Content-Type")?.lowercased(),
       ctype.contains("multipart/byteranges") {
      lock.lock(); state.fellBack = true; state.fellBackKind = .multipartOrBadTotal; lock.unlock()
      return
    }
    // Verify the server's Content-Range start/end matches what we asked for so a
    // stashed `.segN` can't be a slice of a different object/range; and (§5.5)
    // verify the total matches the probe total (reject absent / `*` / mismatch).
    if let cr = http.value(forHTTPHeaderField: "Content-Range") {
      guard let parsed = RangeDownloadLogic.parseContentRangeBounds(cr),
            parsed.start == range.start, parsed.end == range.end else {
        lock.lock(); state.fellBack = true; state.fellBackKind = .multipartOrBadTotal; lock.unlock()
        return
      }
      let expectedTotal = lock.withLockValue { state.totalSize }
      guard let total = RangeDownloadLogic.parseContentRangeTotal(cr), total == expectedTotal else {
        // Absent / `*` / disagreeing total → the object changed or is non-conforming.
        lock.lock(); state.fellBack = true; state.fellBackKind = .multipartOrBadTotal; lock.unlock()
        return
      }
    } else {
      // 206 without a Content-Range header is non-conforming — don't trust it.
      lock.lock(); state.fellBack = true; state.fellBackKind = .multipartOrBadTotal; lock.unlock()
      return
    }

    // Validate the downloaded body length BEFORE moving it into place: a
    // truncated 206 must never be stashed as a valid segment.
    let dest = state.segPath(idx)
    do {
      let attrs = try FileManager.default.attributesOfItem(atPath: location.path)
      let size = attrs[.size] as? Int64
      guard let size = size, size == expectedLen else {
        throw NSError(domain: "RangeDownloader", code: -2, userInfo: [
          NSLocalizedDescriptionKey:
            "segment \(idx) truncated (got \(size.map(String.init) ?? "nil"), expected \(expectedLen))"
        ])
      }
      // Move the segment into place (temp file is deleted after return).
      let dir = (dest as NSString).deletingLastPathComponent
      if !FileManager.default.fileExists(atPath: dir) {
        try FileManager.default.createDirectory(atPath: dir, withIntermediateDirectories: true)
      }
      if FileManager.default.fileExists(atPath: dest) {
        try FileManager.default.removeItem(atPath: dest)
      }
      try FileManager.default.moveItem(at: location, to: URL(fileURLWithPath: dest))
    } catch {
      OneKeyLog.error("RangeDownloader",
                      "\(state.channel.stringValue)/\(state.taskId): failed to stash segment \(idx): \(error)")
      // Record the failure so didCompleteWithError finalizes the run with this
      // terminal error instead of waiting forever for a segment that will never
      // appear.
      lock.lock()
      if state.stashError == nil { state.stashError = error }
      lock.unlock()
    }
  }

  public func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
    if let descriptor = Self.decodeFirmwareTaskDescription(
      task.taskDescription
    ) {
      let completion: (completed: Bool, error: Error?) =
        lock.withLockValue {
          let completed = completedFirmwareTasks.remove(
            task.taskIdentifier
          ) != nil
          let recordedError = firmwareTaskErrors.removeValue(
            forKey: task.taskIdentifier
          )
          return (completed, recordedError)
        }
      if completion.completed {
        return
      }
      finishFirmwareTask(
        key: descriptor.key,
        result: .failure(
          completion.error ??
            error ??
            FirmwareBackgroundDownloadError.transferFailed
        )
      )
      return
    }
    guard let desc = task.taskDescription,
          let (state, idx) = run(for: desc) else { return }
    let ranges = lock.withLockValue { state.ranges }

    // A Permanent classification anywhere in the run (didFinishDownloadingTo set
    // `fellBack`) wins over per-segment retries: discard and single-stream.
    if lock.withLockValue({ state.fellBack }) {
      finalizePermanentFallback(state: state, session: session, ranges: ranges)
      return
    }

    if let error = error {
      let nsErr = error as NSError
      if nsErr.domain == NSURLErrorDomain && nsErr.code == NSURLErrorCancelled {
        // Distinguish a stall-watchdog cancel (transient → retry this segment)
        // from an external/user cancel or our own permanent-fallback sibling
        // cancel (swallow). A genuine cancel never set stallCancelledIndexes.
        let wasStall = lock.withLockValue { state.stallCancelledIndexes.remove(idx) != nil }
        if wasStall {
          if retrySegmentIfUnderBudget(state: state, idx: idx, retryAfter: nil) { return }
          finalizeTransientFallback(state: state, idx: idx, reason: "segment \(idx) stalled; retry budget exhausted", ranges: ranges)
        }
        return
      }
      // §4: connection lost / timeout / DNS / TLS → Transient. Retry THIS segment
      // in place under its budget; keep the others and their `.segN`.
      if retrySegmentIfUnderBudget(state: state, idx: idx, retryAfter: nil) { return }
      finalizeTransientFallback(state: state, idx: idx,
                                reason: error.localizedDescription, ranges: ranges)
      return
    }

    // §4: this segment's finished body was a Transient HTTP status (429/5xx/408/
    // 416). Discard the body and re-enqueue just this segment under its budget.
    let transientRetryAfter = lock.withLockValue { () -> (present: Bool, retryAfter: Double?) in
      if let ra = state.transientSegmentRetryAfter[idx] {
        state.transientSegmentRetryAfter.removeValue(forKey: idx)
        return (true, ra)
      }
      return (false, nil)
    }
    if transientRetryAfter.present {
      // §4 (416): a 416 segment must re-evaluate the total/validator (re-probe)
      // BEFORE re-requesting the same range. If the object changed → object-change
      // (wipe + restart, Permanent); otherwise keep `.segN` and retry normally.
      let needsSizeReeval = lock.withLockValue { state.sizeReevalIndexes.remove(idx) != nil }
      if needsSizeReeval {
        reevaluateSizeThenRetry(state: state, session: session, idx: idx,
                                retryAfter: transientRetryAfter.retryAfter, ranges: ranges)
        return
      }
      if retrySegmentIfUnderBudget(state: state, idx: idx, retryAfter: transientRetryAfter.retryAfter) { return }
      finalizeTransientFallback(state: state, idx: idx,
                                reason: "segment \(idx) throttled/5xx; retry budget exhausted", ranges: ranges)
      return
    }

    // A segment failed to stash (move/size-check failure in didFinishDownloadingTo).
    // A short/truncated body is transient (the bytes will be re-fetched), so retry
    // this segment under budget rather than failing the whole run.
    if lock.withLockValue({ state.stashError }) != nil {
      lock.lock(); state.stashError = nil; lock.unlock()
      if retrySegmentIfUnderBudget(state: state, idx: idx, retryAfter: nil) { return }
      finalizeTransientFallback(state: state, idx: idx,
                                reason: "segment \(idx) could not be stashed; retry budget exhausted", ranges: ranges)
      return
    }

    if allSegmentsPresent(state: state, ranges: ranges) {
      finishContinuation(state: state, with: nil, ranges: ranges)
      return
    }
    // This task finished error==nil but not all segments are present. If any
    // tasks for THIS run are still in flight (or a backoff retry is pending),
    // wait. Otherwise re-enqueue the missing segment(s) under budget; only when a
    // segment's budget/deadline is exhausted do we finalize as a resumable
    // transient fallback (keeping `.segN`).
    let channel = state.channel
    let taskId = state.taskId
    session.getAllTasks { [weak self] tasks in
      guard let self = self else { return }
      let stillInFlight = tasks.contains { t in
        guard let d = t.taskDescription,
              let decoded = Self.decodeTaskDescription(d),
              decoded.channel.stringValue == channel.stringValue,
              decoded.taskId == taskId else { return false }
        return t.state == .running || t.state == .suspended
      }
      if stillInFlight { return }
      // §5.4 (G2): a backoff retry pending for ANY missing segment has no live
      // task (it's a delayed asyncAfter), so it's invisible to `getAllTasks`
      // above. If one is pending, that scheduled retry will re-drive completion;
      // bail now rather than double-incrementing its attempt counter and arming
      // a duplicate retry.
      let pendingRetry = self.lock.withLockValue { !state.pendingRetryIndexes.isEmpty }
      if pendingRetry { return }
      // Re-check under no-in-flight: a just-finished stash may have completed.
      if self.allSegmentsPresent(state: state, ranges: ranges) {
        self.finishContinuation(state: state, with: nil, ranges: ranges)
        return
      }
      // Re-enqueue every missing segment that still has budget. `retrySegmentIf
      // UnderBudget` itself gates on `pendingRetryIndexes`, so a segment already
      // mid-backoff is reported as "in flight" (true) and never re-armed (G2).
      var anyRetryScheduled = false
      var exhaustedIdx: Int?
      for mIdx in ranges.indices
      where !FileManager.default.fileExists(atPath: state.segPath(mIdx)) {
        if self.retrySegmentIfUnderBudget(state: state, idx: mIdx, retryAfter: nil) {
          anyRetryScheduled = true
        } else if exhaustedIdx == nil {
          exhaustedIdx = mIdx
        }
      }
      if anyRetryScheduled { return }
      let missing = exhaustedIdx
        ?? ranges.indices.first { !FileManager.default.fileExists(atPath: state.segPath($0)) }
      self.finalizeTransientFallback(
        state: state, idx: missing ?? 0,
        reason: "segment \(missing.map(String.init) ?? "?") missing/truncated; retry budget exhausted",
        ranges: ranges)
    }
  }

  /// §4 Permanent: abandon sibling tasks, discard `.segN`, finalize the run with a
  /// Permanent fallback carrying the run's classified kind.
  private func finalizePermanentFallback(state: RunState, session: URLSession,
                                         ranges: [(start: Int64, end: Int64)]) {
    let channel = state.channel
    let taskId = state.taskId
    let kind = lock.withLockValue { state.fellBackKind }
    session.getAllTasks { tasks in
      for t in tasks {
        if let d = t.taskDescription,
           let decoded = Self.decodeTaskDescription(d),
           decoded.channel.stringValue == channel.stringValue,
           decoded.taskId == taskId {
          t.cancel()
        }
      }
    }
    cleanupSegments(state: state, ranges: ranges)
    let reason: String
    switch kind {
    case .serverIgnoredRange: reason = "server returned 200 to a Range request"
    case .multipartOrBadTotal: reason = "non-conforming 206 (multipart / range / total mismatch)"
    case .authExpired: reason = "auth expired (401/403); fetch a fresh signed URL"
    case .notFound: reason = "object not found (404/410)"
    case .redirectRejected: reason = "rejected non-HTTPS redirect"
    case .rangeUnsupported: reason = "range unsupported / non-retryable status"
    case .checksumMismatch: reason = "whole-file checksum mismatch"
    case .transientNetwork, .throttled, .budgetExhausted: reason = "permanent fallback"
    }
    finishContinuation(state: state, with: FallbackError(reason: reason, kind: kind), ranges: ranges)
  }

  /// §4 Transient: finalize the run as a RESUMABLE fallback. `.segN` files are
  /// intentionally KEPT so the next concurrent attempt resumes only the missing
  /// segment(s); never restart from byte 0 while resumable bytes exist.
  private func finalizeTransientFallback(state: RunState, idx: Int, reason: String,
                                         ranges: [(start: Int64, end: Int64)]) {
    finishContinuation(state: state,
                       with: FallbackError(reason: reason, kind: .budgetExhausted),
                       ranges: ranges)
  }

  /// §5.9: HTTPS-only on every redirect hop. A redirect to a non-HTTPS URL is
  /// rejected (Permanent, `redirectRejected`). Passing `nil` to the completion
  /// handler cancels the redirect; the task then completes with an error which
  /// our run classification turns into a Permanent fallback (we pre-mark the run
  /// so didCompleteWithError doesn't misread the cancel as transient).
  public func urlSession(_ session: URLSession, task: URLSessionTask,
                         willPerformHTTPRedirection response: HTTPURLResponse,
                         newRequest request: URLRequest,
                         completionHandler: @escaping (URLRequest?) -> Void) {
    if let descriptor = Self.decodeFirmwareTaskDescription(
      task.taskDescription
    ) {
      guard
        request.url?.scheme?.lowercased() == "https",
        request.url?.host?.lowercased() == descriptor.hostname,
        request.url?.port == nil || request.url?.port == 443,
        request.url?.user == nil,
        request.url?.password == nil
      else {
        recordFirmwareTaskError(
          taskIdentifier: task.taskIdentifier,
          error: FirmwareBackgroundDownloadError.redirectRejected
        )
        completionHandler(nil)
        return
      }
      completionHandler(request)
      return
    }
    if request.url?.scheme?.lowercased() != "https" {
      OneKeyLog.error("RangeDownloader", "blocked redirect to non-HTTPS URL")
      if let desc = task.taskDescription, let (state, _) = run(for: desc) {
        lock.lock(); state.fellBack = true; state.fellBackKind = .redirectRejected; lock.unlock()
      }
      completionHandler(nil)
    } else {
      completionHandler(request)
    }
  }

  private func recordFirmwareTaskError(
    taskIdentifier: Int,
    error: Error
  ) {
    lock.withLockValue {
      if firmwareTaskErrors[taskIdentifier] == nil {
        firmwareTaskErrors[taskIdentifier] = error
      }
    }
  }

  private func firmwareTaskError(
    taskIdentifier: Int
  ) -> Error? {
    lock.withLockValue {
      firmwareTaskErrors[taskIdentifier]
    }
  }

  /// Called on the session delegate queue when all background events for this
  /// session have been delivered after a background relaunch.
  public func urlSessionDidFinishEvents(forBackgroundURLSession session: URLSession) {
    let identifier = session.configuration.identifier
    DispatchQueue.main.async { [weak self] in
      guard let self = self else { return }
      var handler: (() -> Void)?
      if let id = identifier {
        self.lock.lock()
        handler = self.backgroundCompletionHandlers.removeValue(forKey: id)
        self.lock.unlock()
      }
      handler?()
    }
  }

  // MARK: - Finalize

  private func finishContinuation(state: RunState, with error: Error?,
                                  ranges: [(start: Int64, end: Int64)]) {
    let cont: CheckedContinuation<Void, Error>? = lock.withLockValue {
      let c = state.continuation
      state.continuation = nil
      return c
    }
    guard let cont = cont else { return }
    if let error = error { cont.resume(throwing: error); return }
    do {
      try concatenateAndFinish(state: state, ranges: ranges)
      cont.resume(returning: ())
    } catch {
      cont.resume(throwing: error)
    }
  }

  private func concatenateAndFinish(state: RunState,
                                    ranges: [(start: Int64, end: Int64)]) throws {
    let filePath = state.filePath
    let partial = "\(filePath).partial"
    if FileManager.default.fileExists(atPath: partial) {
      try FileManager.default.removeItem(atPath: partial)
    }
    FileManager.default.createFile(atPath: partial, contents: nil)
    guard let out = FileHandle(forWritingAtPath: partial) else {
      throw NSError(domain: "RangeDownloader", code: -1,
                    userInfo: [NSLocalizedDescriptionKey: "cannot open partial for write"])
    }
    defer { try? out.close() }
    // Stream each segment in fixed-size chunks so a full segment is never held
    // in RAM (mirrors the streamed SHA256 backstop below).
    let chunkSize = 1 * 1024 * 1024
    for idx in 0..<ranges.count {
      let segPath = state.segPath(idx)
      guard let inHandle = FileHandle(forReadingAtPath: segPath) else {
        throw NSError(domain: "RangeDownloader", code: -3,
                      userInfo: [NSLocalizedDescriptionKey: "cannot open segment \(idx) for read"])
      }
      defer { try? inHandle.close() }
      while try autoreleasepool(invoking: { () -> Bool in
        let data = inHandle.readData(ofLength: chunkSize)
        if data.isEmpty { return false }
        try out.write(contentsOf: data)
        return true
      }) {}
    }
    try? out.close()
    if FileManager.default.fileExists(atPath: filePath) {
      try FileManager.default.removeItem(atPath: filePath)
    }
    try FileManager.default.moveItem(atPath: partial, toPath: filePath)
    cleanupSegments(state: state, ranges: ranges)
  }

  private func cleanupSegments(state: RunState, ranges: [(start: Int64, end: Int64)]) {
    for idx in 0..<ranges.count {
      try? FileManager.default.removeItem(atPath: state.segPath(idx))
    }
    try? FileManager.default.removeItem(atPath: "\(state.filePath).partial")
  }

  /// Cancels any in-flight background tasks for (channel|taskId) and then
  /// discards all segment artifacts. Cancel-then-delete prevents a still-running
  /// `nsurlsessiond` task from resurrecting a `.segN` we just deleted.
  public func cancel(channel: DownloadChannel, taskId: String,
                     filePath: String) async {
    let session = session(forChannel: channel, segmentCount: Self.defaultSegmentCount)
    // Cancel matching tasks first and wait for getAllTasks to return so the
    // cancels have been issued before we touch the files.
    await withCheckedContinuation { (cont: CheckedContinuation<Void, Never>) in
      session.getAllTasks { tasks in
        for t in tasks {
          if let d = t.taskDescription,
             let decoded = Self.decodeTaskDescription(d),
             decoded.channel.stringValue == channel.stringValue,
             decoded.taskId == taskId {
            t.cancel()
          }
        }
        cont.resume()
      }
    }
    // Drop the run state so a late delegate callback can't re-stash a segment.
    clearRun(key: Self.runKey(channel: channel, taskId: taskId))
    discardArtifacts(filePath: filePath)
  }

  /// True when at least one concurrent segment artifact survives for this file.
  ///
  /// The downloader cleans its `.segN` files itself on every *permanent*
  /// fallback (server returned 200 → `cleanupSegments` in didCompleteWithError;
  /// SHA mismatch → cleanup in concatenateAndFinish; Range unsupported → nothing
  /// was ever stashed) and deliberately RETAINS them on a *transient* one (a
  /// suspend/network drop that left "segment N missing/truncated"). So a
  /// surviving `.segN` is a reliable signal that the fallback is resumable —
  /// the caller uses it to avoid deleting bytes a later attempt can resume.
  public func hasArtifacts(filePath: String) -> Bool {
    for idx in 0..<Self.defaultSegmentCount {
      if FileManager.default.fileExists(atPath: "\(filePath).seg\(idx)") { return true }
    }
    return false
  }

  /// Discards all segment artifacts (used by the caller when falling back to
  /// single-stream so the bare slot is clean). Prefer `cancel(...)` when tasks
  /// may still be in flight; this file-only delete is for the post-fallback
  /// case where tasks have already been abandoned.
  public func discardArtifacts(filePath: String) {
    for idx in 0..<Self.defaultSegmentCount {
      try? FileManager.default.removeItem(atPath: "\(filePath).seg\(idx)")
    }
    try? FileManager.default.removeItem(atPath: "\(filePath).partial")
  }

  // MARK: - SHA256 (streaming backstop)

  // calculateSHA256 (§5.5) now lives on `RangeDownloadLogic`.
}

private extension NSLock {
  func withLockValue<T>(_ body: () -> T) -> T {
    lock(); defer { unlock() }
    return body()
  }
}
