import Foundation
import CommonCrypto
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
//   - `throws FallbackError`     → returns `RangeDownloadResult(.fallback, …)`.
class ReactNativeRangeDownloader: HybridReactNativeRangeDownloaderSpec {

  func download(params: RangeDownloadParams) throws -> Promise<RangeDownloadResult> {
    return Promise.async {
      let (outcome, filePath, fallbackReason) = await RangeDownloader.shared.download(
        channel: params.channel,
        taskId: params.taskId,
        urlString: params.url,
        filePath: params.destFilePath,
        expectedSha256: params.expectedSha256,
        segmentCount: params.segmentCount.map { Int($0) },
        minConcurrentBytes: params.minConcurrentBytes.map { Int64($0) }
      )
      return RangeDownloadResult(
        outcome: outcome,
        filePath: filePath,
        fallbackReason: fallbackReason
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
public final class RangeDownloader: NSObject, URLSessionDownloadDelegate {

  public static let shared = RangeDownloader()

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
    /// Set when a segment could not be stashed (move/size-check failure). Carries
    /// the terminal error so didCompleteWithError finalizes instead of hanging.
    var stashError: Error?
    /// Whether a strong validator (ETag) was captured for this run. When false,
    /// resumable `.segN` state must not be trusted across attempts.
    var hasValidator = false
    let sessionIdentifier: String

    init(channel: DownloadChannel, taskId: String, filePath: String,
         segmentCount: Int, sessionIdentifier: String) {
      self.channel = channel
      self.taskId = taskId
      self.filePath = filePath
      self.segmentCount = segmentCount
      self.sessionIdentifier = sessionIdentifier
    }

    func segPath(_ index: Int) -> String { "\(filePath).seg\(index)" }
  }

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
    // Re-create the session with this delegate so queued completion events are
    // delivered here on a background relaunch.
    _ = session(forIdentifier: identifier)
    if let handler = note.userInfo?["completionHandler"] as? () -> Void {
      lock.lock()
      backgroundCompletionHandlers[identifier] = handler
      lock.unlock()
    }
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

  // MARK: - Public entry

  /// Downloads [urlString] into [filePath] using concurrent background ranges.
  /// Returns `(.completed, filePath, nil)` on success, or `(.fallback, filePath,
  /// reason)` when the caller should use its single-stream path. Transient
  /// network errors are also reported as `.fallback` with the error reason (the
  /// `.segN` files are kept for the next attempt).
  public func download(
    channel: DownloadChannel,
    taskId: String,
    urlString: String,
    filePath: String,
    expectedSha256: String?,
    segmentCount: Int?,
    minConcurrentBytes: Int64?
  ) async -> (RangeDownloadOutcome, String, String?) {
    let segCount = max(1, segmentCount ?? Self.defaultSegmentCount)
    let minBytes = minConcurrentBytes ?? Self.defaultMinConcurrentBytes
    let key = Self.runKey(channel: channel, taskId: taskId)

    guard let url = URL(string: urlString) else {
      return (.fallback, filePath, "invalid url")
    }
    // HTTPS-only: background URLSession + transport hardening.
    guard urlString.hasPrefix("https://") else {
      return (.fallback, filePath, "url must use https")
    }

    let probe: ProbeResult
    do {
      probe = try await self.probe(url: url)
    } catch {
      return (.fallback, filePath, "probe failed: \(error.localizedDescription)")
    }
    guard probe.supportsRange, probe.total >= minBytes else {
      return (.fallback, filePath, "range unsupported or file too small")
    }

    let state = RunState(
      channel: channel, taskId: taskId, filePath: filePath,
      segmentCount: segCount,
      sessionIdentifier: Self.sessionIdentifier(for: channel)
    )
    state.totalSize = probe.total
    state.etag = probe.etag
    state.hasValidator = (probe.etag?.isEmpty == false)
    state.ranges = Self.planRanges(total: probe.total, segments: segCount)
    state.segmentWritten = [Int64](repeating: 0, count: state.ranges.count)

    lock.lock()
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
      return (.fallback, filePath, fb.reason)
    } catch {
      // Transient error → ask caller to fall back (segments retained for retry).
      clearRun(key: key)
      let reason = error.localizedDescription
      emit(channel: channel, taskId: taskId, type: "fallback", progress: 0, message: reason)
      return (.fallback, filePath, reason)
    }

    clearRun(key: key)

    // Optional immediate SHA256 self-check backstop. When omitted, the caller
    // verifies after the fact.
    if let expected = expectedSha256, !expected.isEmpty {
      let actual = Self.calculateSHA256(filePath)
      if actual?.lowercased() != expected.lowercased() {
        try? FileManager.default.removeItem(atPath: filePath)
        let reason = "sha256 mismatch (expected \(expected), got \(actual ?? "nil"))"
        OneKeyLog.error("RangeDownloader", "\(channel.stringValue)/\(taskId): \(reason)")
        emit(channel: channel, taskId: taskId, type: "fallback", progress: 0, message: reason)
        return (.fallback, filePath, reason)
      }
    }

    emit(channel: channel, taskId: taskId, type: "complete", progress: 100, message: "")
    return (.completed, filePath, nil)
  }

  private func clearRun(key: String) {
    lock.lock(); runs.removeValue(forKey: key); lock.unlock()
  }

  // MARK: - Range planning / probing

  static func planRanges(total: Int64, segments: Int) -> [(start: Int64, end: Int64)] {
    var out: [(Int64, Int64)] = []
    let chunk = (total + Int64(segments) - 1) / Int64(segments)
    var i = 0
    while i < segments {
      let start = Int64(i) * chunk
      if start >= total { break }
      let end = min(start + chunk - 1, total - 1)
      out.append((start, end))
      i += 1
    }
    return out
  }

  private struct ProbeResult { let total: Int64; let etag: String?; let supportsRange: Bool }

  struct FallbackError: Error { let reason: String }

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
           let total = Self.parseContentRangeTotal(cr) {
          cont.resume(returning: ProbeResult(total: total, etag: etag, supportsRange: true))
        } else {
          // 200 (Range ignored) or anything else → single-stream.
          cont.resume(returning: ProbeResult(total: 0, etag: etag, supportsRange: false))
        }
      }
      task.resume()
    }
  }

  static func parseContentRangeTotal(_ header: String) -> Int64? {
    // "bytes 0-0/65226095"
    guard let slash = header.lastIndex(of: "/") else { return nil }
    let tail = header[header.index(after: slash)...]
    return Int64(tail.trimmingCharacters(in: .whitespaces))
  }

  /// Parses the start/end of a "bytes <start>-<end>/<total>" Content-Range.
  static func parseContentRangeBounds(_ header: String) -> (start: Int64, end: Int64)? {
    // Drop the leading "bytes " and the trailing "/<total>".
    let trimmed = header.trimmingCharacters(in: .whitespaces)
    guard let spaceIdx = trimmed.firstIndex(of: " ") else { return nil }
    var rangePart = String(trimmed[trimmed.index(after: spaceIdx)...])
    if let slash = rangePart.firstIndex(of: "/") {
      rangePart = String(rangePart[..<slash])
    }
    let bounds = rangePart.split(separator: "-", maxSplits: 1).map { String($0) }
    guard bounds.count == 2,
          let start = Int64(bounds[0].trimmingCharacters(in: .whitespaces)),
          let end = Int64(bounds[1].trimmingCharacters(in: .whitespaces)) else { return nil }
    return (start, end)
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
        var req = URLRequest(url: url)
        req.setValue("bytes=\(range.start)-\(range.end)", forHTTPHeaderField: "Range")
        if let etag = state.etag { req.setValue(etag, forHTTPHeaderField: "If-Range") }
        let task = session.downloadTask(with: req)
        task.taskDescription = Self.encodeTaskDescription(
          channel: state.channel, taskId: state.taskId, segIndex: idx
        )
        task.resume()
      }
      // It's possible every segment was already present but the early
      // allSegmentsPresent check raced a just-finished task; re-check.
      if self.allSegmentsPresent(state: state, ranges: ranges) {
        self.finishContinuation(state: state, with: nil, ranges: ranges)
      }
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
    guard let desc = downloadTask.taskDescription,
          let (state, idx) = run(for: desc) else { return }
    lock.lock()
    if idx < state.segmentWritten.count { state.segmentWritten[idx] = totalBytesWritten }
    let sum = state.segmentWritten.reduce(0, +)
    let total = state.totalSize
    var emit = false
    if total > 0 {
      let p = Int((sum * 100) / total)
      if p != state.prevProgress { state.prevProgress = p; emit = true }
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
    guard let desc = downloadTask.taskDescription,
          let (state, idx) = run(for: desc) else { return }
    let ranges = lock.withLockValue { state.ranges }
    guard idx < ranges.count else { return }
    let range = ranges[idx]
    let expectedLen = range.end - range.start + 1

    // We require a 206 Partial Content that matches the requested byte range.
    // Anything else — 200 (Range ignored / ETag changed) or an out-of-range
    // Content-Range — means we cannot safely assemble this segment, so flag
    // fallback; finalize happens in didCompleteWithError.
    guard let http = downloadTask.response as? HTTPURLResponse else {
      lock.lock(); state.fellBack = true; lock.unlock()
      return
    }
    if http.statusCode != 206 {
      lock.lock(); state.fellBack = true; lock.unlock()
      return
    }
    // Verify the server's Content-Range start/end matches what we asked for so a
    // stashed `.segN` can't be a slice of a different object/range.
    if let cr = http.value(forHTTPHeaderField: "Content-Range") {
      guard let parsed = Self.parseContentRangeBounds(cr),
            parsed.start == range.start, parsed.end == range.end else {
        lock.lock(); state.fellBack = true; lock.unlock()
        return
      }
    } else {
      // 206 without a Content-Range header is non-conforming — don't trust it.
      lock.lock(); state.fellBack = true; lock.unlock()
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
    guard let desc = task.taskDescription,
          let (state, _) = run(for: desc) else { return }
    let ranges = lock.withLockValue { state.ranges }
    if let error = error {
      // Background ignores user-cancels (e.g. our own fallback cancel below) —
      // surface only genuine give-ups.
      let nsErr = error as NSError
      if nsErr.domain == NSURLErrorDomain && nsErr.code == NSURLErrorCancelled {
        return
      }
      finishContinuation(state: state, with: error, ranges: ranges)
      return
    }
    if lock.withLockValue({ state.fellBack }) {
      // Abandon the other in-flight segment tasks for THIS run so they don't
      // keep downloading after we've decided to fall back to single-stream.
      let channel = state.channel
      let taskId = state.taskId
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
      finishContinuation(state: state,
                         with: FallbackError(reason: "server returned 200 to a Range request"),
                         ranges: ranges)
      return
    }
    // A segment failed to stash (move/size-check failure in didFinishDownloadingTo).
    // That segment will never appear, so finalize terminally instead of waiting.
    if let stashErr = lock.withLockValue({ state.stashError }) {
      finishContinuation(state: state, with: stashErr, ranges: ranges)
      return
    }
    if allSegmentsPresent(state: state, ranges: ranges) {
      finishContinuation(state: state, with: nil, ranges: ranges)
      return
    }
    // This task finished error==nil but not all segments are present. If any
    // tasks for THIS run are still in flight, wait for their completions.
    // Otherwise nothing will ever resume the continuation — finalize with a
    // descriptive terminal error so the JS promise resolves (as fallback).
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
      // Re-check under no-in-flight: a just-finished stash may have completed.
      if self.allSegmentsPresent(state: state, ranges: ranges) {
        self.finishContinuation(state: state, with: nil, ranges: ranges)
        return
      }
      let missing = ranges.indices.first {
        !FileManager.default.fileExists(atPath: state.segPath($0))
      }
      let reason = "segment \(missing.map(String.init) ?? "?") missing/truncated after completion"
      self.finishContinuation(state: state,
                              with: FallbackError(reason: reason),
                              ranges: ranges)
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

  static func calculateSHA256(_ filePath: String) -> String? {
    let fm = FileManager.default
    guard fm.fileExists(atPath: filePath),
          let fileHandle = FileHandle(forReadingAtPath: filePath) else { return nil }
    defer { try? fileHandle.close() }
    var context = CC_SHA256_CTX()
    CC_SHA256_Init(&context)
    while autoreleasepool(invoking: { () -> Bool in
      let data = fileHandle.readData(ofLength: 8192)
      if data.isEmpty { return false }
      data.withUnsafeBytes { CC_SHA256_Update(&context, $0.baseAddress, CC_LONG(data.count)) }
      return true
    }) {}
    var hash = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
    CC_SHA256_Final(&hash, &context)
    return hash.map { String(format: "%02x", $0) }.joined()
  }
}

private extension NSLock {
  func withLockValue<T>(_ body: () -> T) -> T {
    lock(); defer { unlock() }
    return body()
  }
}
