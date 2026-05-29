import Foundation
import ReactNativeNativeLogger

/// Concurrent + background multi-range bundle downloader for iOS.
///
/// One background `URLSession` carries [segmentCount] download tasks, each
/// requesting a byte Range of the bundle. A background session is the single
/// mechanism that satisfies BOTH requirements at once:
///   - concurrency: the 8 range tasks run in parallel (foreground = full speed);
///   - background: `nsurlsessiond` keeps transferring while the app is
///     suspended and even relaunches the app to deliver completion events.
/// It needs NO new entitlement (background URLSession transfers do not require
/// the "Background Modes" capability).
///
/// Because background download tasks deliver a whole file per task at
/// completion (not an incremental stream), each segment is downloaded to its
/// own `<file>.segN` and the segments are concatenated in order once all are
/// present. A segment file is therefore atomic — fully present or absent — so
/// resume across app suspension/kill is just "which `.segN` already exist";
/// the OS owns each task's internal byte resume. The caller's whole-file SHA256
/// check after concatenation is the final correctness backstop.
///
/// NOTE: this class is process-global (one background session per identifier).
/// The owning module gates a single in-flight download via its `isDownloading`
/// flag, so we keep a single active run's state here.
@objc(ConcurrentBundleDownloader)
public final class ConcurrentBundleDownloader: NSObject, URLSessionDownloadDelegate {

  @objc public static let shared = ConcurrentBundleDownloader()

  /// Posted by the AppDelegate from
  /// application(_:handleEventsForBackgroundURLSession:completionHandler:).
  /// userInfo: ["identifier": String, "completionHandler": () -> Void].
  /// Using a notification (rather than a direct call) keeps the AppDelegate
  /// decoupled from this Nitro module, whose C++ umbrella header can't be
  /// imported into the Swift AppDelegate. (Not @objc: Notification.Name isn't
  /// Obj-C representable; the AppDelegate posts the raw string name.)
  public static let backgroundEventsNotification =
    Notification.Name("ConcurrentBundleDownloaderBackgroundEvents")

  /// Signals the caller should use its single-stream path (Range unsupported,
  /// server ignored Range / ETag changed, file too small, ...).
  public struct FallbackError: Error { let reason: String }

  override init() {
    super.init()
    // Pre-touch the lazy background session so, on a background relaunch, the
    // session is recreated with this delegate and queued completion events are
    // delivered here.
    _ = self.session
    NotificationCenter.default.addObserver(
      self,
      selector: #selector(handleBackgroundEventsNotification(_:)),
      name: Self.backgroundEventsNotification,
      object: nil
    )
  }

  @objc private func handleBackgroundEventsNotification(_ note: Notification) {
    guard let identifier = note.userInfo?["identifier"] as? String,
          identifier == Self.sessionIdentifier else { return }
    if let handler = note.userInfo?["completionHandler"] as? () -> Void {
      self.backgroundCompletionHandler = handler
    }
  }

  private let segmentCount = 8
  private let minConcurrentBytes: Int64 = 2 * 1024 * 1024
  private static let sessionIdentifier = "so.onekey.bundleupdate.concurrent.bg"

  private let lock = NSLock()

  // Active-run state (one run at a time, gated by the module's isDownloading).
  private var filePath: String?
  private var totalSize: Int64 = 0
  private var etag: String?
  private var ranges: [(start: Int64, end: Int64)] = []
  private var segmentWritten: [Int64] = []      // progress estimate per segment
  private var onProgress: ((Int) -> Void)?
  private var continuation: CheckedContinuation<Void, Error>?
  private var prevProgress = -1
  private var fellBack = false

  /// Stored by the AppDelegate's handleEventsForBackgroundURLSession (via the
  /// notification) so we can call it once all queued background events have
  /// been delivered. (Not @objc: a closure-typed property isn't Obj-C
  /// representable.)
  public var backgroundCompletionHandler: (() -> Void)?

  private lazy var session: URLSession = {
    let cfg = URLSessionConfiguration.background(withIdentifier: Self.sessionIdentifier)
    cfg.tlsMinimumSupportedProtocolVersion = .TLSv12
    cfg.isDiscretionary = false
    cfg.sessionSendsLaunchEvents = true
    cfg.httpMaximumConnectionsPerHost = segmentCount
    return URLSession(configuration: cfg, delegate: self, delegateQueue: nil)
  }()

  private func segPath(_ index: Int) -> String { "\(filePath ?? "").seg\(index)" }

  // MARK: - Public entry

  /// Downloads [urlString] into [filePath] using concurrent background ranges.
  /// Returns when `filePath` is fully assembled (caller then SHA-verifies), or
  /// throws `FallbackError` to ask the caller to use single-stream. Transient
  /// errors are thrown too (segment files are kept for the next attempt).
  public func downloadConcurrent(
    urlString: String,
    filePath: String,
    onProgress: @escaping (Int) -> Void
  ) async throws {
    guard let url = URL(string: urlString) else { throw FallbackError(reason: "invalid url") }

    let probe = try await probe(url: url)
    guard probe.supportsRange, probe.total >= minConcurrentBytes else {
      throw FallbackError(reason: "range unsupported or file too small")
    }

    lock.lock()
    self.filePath = filePath
    self.totalSize = probe.total
    self.etag = probe.etag
    self.ranges = Self.planRanges(total: probe.total, segments: segmentCount)
    self.segmentWritten = [Int64](repeating: 0, count: self.ranges.count)
    self.onProgress = onProgress
    self.prevProgress = -1
    self.fellBack = false
    let ranges = self.ranges
    lock.unlock()

    // If every segment is already on disk (resume after suspension/kill), skip
    // straight to concatenation.
    if allSegmentsPresent(ranges: ranges) {
      try concatenateAndFinish(ranges: ranges)
      return
    }

    try await withCheckedThrowingContinuation { (cont: CheckedContinuation<Void, Error>) in
      self.lock.lock()
      self.continuation = cont
      self.lock.unlock()
      reconcileAndStartTasks(url: url, ranges: ranges)
    }
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

  // MARK: - Task reconciliation (handles app relaunch)

  /// Ensures exactly one in-flight (or completed) artifact per missing segment:
  /// segments already on disk as `.segN` are skipped; segments that already
  /// have a running task in the (recreated) background session are left alone;
  /// the rest get a fresh Range download task.
  private func reconcileAndStartTasks(url: URL, ranges: [(start: Int64, end: Int64)]) {
    session.getAllTasks { [weak self] tasks in
      guard let self = self else { return }
      var liveIndexes = Set<Int>()
      for t in tasks {
        // Cancel stale tasks left over from a DIFFERENT bundle download (same
        // background session, different URL) so their segment indexes don't
        // collide with this run's.
        guard let turl = t.originalRequest?.url, turl.absoluteString == url.absoluteString else {
          t.cancel()
          continue
        }
        if let desc = t.taskDescription, let idx = Int(desc),
           t.state == .running || t.state == .suspended {
          liveIndexes.insert(idx)
        }
      }
      for (idx, range) in ranges.enumerated() {
        if FileManager.default.fileExists(atPath: self.segPath(idx)) { continue }
        if liveIndexes.contains(idx) { continue }
        var req = URLRequest(url: url)
        req.setValue("bytes=\(range.start)-\(range.end)", forHTTPHeaderField: "Range")
        if let etag = self.etag { req.setValue(etag, forHTTPHeaderField: "If-Range") }
        let task = self.session.downloadTask(with: req)
        task.taskDescription = String(idx)
        task.resume()
      }
      // It's possible every segment was already present but the early
      // allSegmentsPresent check raced a just-finished task; re-check.
      if self.allSegmentsPresent(ranges: ranges) {
        self.finishContinuation(with: nil, ranges: ranges)
      }
    }
  }

  private func allSegmentsPresent(ranges: [(start: Int64, end: Int64)]) -> Bool {
    for (idx, range) in ranges.enumerated() {
      let expected = range.end - range.start + 1
      let p = segPath(idx)
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
    guard let desc = downloadTask.taskDescription, let idx = Int(desc) else { return }
    lock.lock()
    if idx < segmentWritten.count { segmentWritten[idx] = totalBytesWritten }
    let sum = segmentWritten.reduce(0, +)
    let total = totalSize
    let cb = onProgress
    var emit = false
    if total > 0 {
      let p = Int((sum * 100) / total)
      if p != prevProgress { prevProgress = p; emit = true }
    }
    let progressValue = total > 0 ? Int((sum * 100) / total) : 0
    lock.unlock()
    if emit { cb?(progressValue) }
  }

  public func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask,
                         didFinishDownloadingTo location: URL) {
    guard let desc = downloadTask.taskDescription, let idx = Int(desc) else { return }
    // A 200 means the server ignored Range / the ETag changed — we cannot
    // safely assemble. Flag fallback; finalize happens in didCompleteWithError.
    if let http = downloadTask.response as? HTTPURLResponse, http.statusCode == 200 {
      lock.lock(); fellBack = true; lock.unlock()
      return
    }
    // Move the segment into place atomically (temp file is deleted after return).
    let dest = segPath(idx)
    do {
      let dir = (dest as NSString).deletingLastPathComponent
      if !FileManager.default.fileExists(atPath: dir) {
        try FileManager.default.createDirectory(atPath: dir, withIntermediateDirectories: true)
      }
      if FileManager.default.fileExists(atPath: dest) {
        try FileManager.default.removeItem(atPath: dest)
      }
      try FileManager.default.moveItem(at: location, to: URL(fileURLWithPath: dest))
    } catch {
      OneKeyLog.error("BundleUpdate", "concurrent: failed to stash segment \(idx): \(error)")
    }
  }

  public func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
    let ranges = lock.withLockValue { self.ranges }
    if let error = error {
      // Transient (network) errors on a background task are usually retried by
      // the system; a delivered error here means the task gave up. Surface it.
      finishContinuation(with: error, ranges: ranges)
      return
    }
    if lock.withLockValue({ self.fellBack }) {
      // Abandon the other in-flight segment tasks so they don't keep
      // downloading after we've decided to fall back to single-stream.
      session.getAllTasks { tasks in tasks.forEach { $0.cancel() } }
      cleanupSegments(ranges: ranges)
      finishContinuation(with: FallbackError(reason: "server returned 200 to a Range request"), ranges: ranges)
      return
    }
    if allSegmentsPresent(ranges: ranges) {
      finishContinuation(with: nil, ranges: ranges)
    }
    // else: other segments still in flight; wait for their completions.
  }

  /// Called on the session delegate queue when all background events for this
  /// session have been delivered after a background relaunch.
  public func urlSessionDidFinishEvents(forBackgroundURLSession session: URLSession) {
    DispatchQueue.main.async { [weak self] in
      let handler = self?.backgroundCompletionHandler
      self?.backgroundCompletionHandler = nil
      handler?()
    }
  }

  // MARK: - Finalize

  private func finishContinuation(with error: Error?, ranges: [(start: Int64, end: Int64)]) {
    let cont: CheckedContinuation<Void, Error>? = lock.withLockValue {
      let c = self.continuation
      self.continuation = nil
      return c
    }
    guard let cont = cont else { return }
    if let error = error { cont.resume(throwing: error); return }
    do {
      try concatenateAndFinish(ranges: ranges)
      cont.resume(returning: ())
    } catch {
      cont.resume(throwing: error)
    }
  }

  private func concatenateAndFinish(ranges: [(start: Int64, end: Int64)]) throws {
    guard let filePath = filePath else { throw FallbackError(reason: "no filePath") }
    let partial = "\(filePath).partial"
    if FileManager.default.fileExists(atPath: partial) {
      try FileManager.default.removeItem(atPath: partial)
    }
    FileManager.default.createFile(atPath: partial, contents: nil)
    guard let out = FileHandle(forWritingAtPath: partial) else {
      throw NSError(domain: "BundleUpdate", code: -1,
                    userInfo: [NSLocalizedDescriptionKey: "cannot open partial for write"])
    }
    defer { try? out.close() }
    for idx in 0..<ranges.count {
      let segData = try Data(contentsOf: URL(fileURLWithPath: segPath(idx)))
      try out.write(contentsOf: segData)
    }
    try? out.close()
    if FileManager.default.fileExists(atPath: filePath) {
      try FileManager.default.removeItem(atPath: filePath)
    }
    try FileManager.default.moveItem(atPath: partial, toPath: filePath)
    cleanupSegments(ranges: ranges)
  }

  private func cleanupSegments(ranges: [(start: Int64, end: Int64)]) {
    for idx in 0..<ranges.count {
      try? FileManager.default.removeItem(atPath: segPath(idx))
    }
    try? FileManager.default.removeItem(atPath: "\(filePath ?? "").partial")
  }

  /// Discards all segment artifacts (used by the caller when falling back to
  /// single-stream so the bare slot is clean).
  public func discardArtifacts(filePath: String) {
    for idx in 0..<segmentCount {
      try? FileManager.default.removeItem(atPath: "\(filePath).seg\(idx)")
    }
    try? FileManager.default.removeItem(atPath: "\(filePath).partial")
  }
}

private extension NSLock {
  func withLockValue<T>(_ body: () -> T) -> T {
    lock(); defer { unlock() }
    return body()
  }
}
