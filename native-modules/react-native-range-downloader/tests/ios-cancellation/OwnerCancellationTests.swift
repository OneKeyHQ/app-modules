final class AuditHeldSegmentProtocol: URLProtocol {
  override class func canInit(with request: URLRequest) -> Bool { true }
  override class func canonicalRequest(for request: URLRequest) -> URLRequest { request }
  override func startLoading() {
    let response = HTTPURLResponse(url: request.url!, statusCode: 206,
                                   httpVersion: nil,
                                   headerFields: ["Content-Range": "bytes 0-99/100"])!
    client?.urlProtocol(self, didReceive: response, cacheStoragePolicy: .notAllowed)
    if request.url?.lastPathComponent != "new" {
      client?.urlProtocol(self, didLoad: Data(repeating: 7, count: 100))
      client?.urlProtocolDidFinishLoading(self)
    }
  }
  override func stopLoading() {}
}

enum AuditEnumeration {
  static let done = DispatchSemaphore(value: 0)
}

extension RangeDownloader {
  static func auditCancelledOwnerCannotReachReplacement() async throws {
    let downloader = RangeDownloader()
    let id = "owner-" + UUID().uuidString
    let key = runKey(channel: .bundle, taskId: id)
    let dir = FileManager.default.temporaryDirectory.appendingPathComponent(id)
    try FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)
    defer { try? FileManager.default.removeItem(at: dir) }
    let oldPath = dir.appendingPathComponent("old.zip").path
    let newPath = dir.appendingPathComponent("new.zip").path
    let queue = OperationQueue()
    queue.maxConcurrentOperationCount = 1
    let blockerStarted = DispatchSemaphore(value: 0)
    let releaseQueue = DispatchSemaphore(value: 0)
    let configuration = URLSessionConfiguration.ephemeral
    configuration.protocolClasses = [AuditHeldSegmentProtocol.self]
    let session = URLSession(configuration: configuration, delegate: downloader, delegateQueue: queue)
    downloader.sessions[sessionIdentifier(for: .bundle)] = session
    defer { session.invalidateAndCancel() }
    let oldState = RunState(channel: .bundle, taskId: id, filePath: oldPath,
                            segmentCount: 1, sessionIdentifier: sessionIdentifier(for: .bundle),
                            maxSegmentAttempts: 1, deadline: nil)
    oldState.segmentWritten = [0]
    oldState.totalSize = 100
    oldState.ranges = [(0, 99)]
    let oldTask = session.downloadTask(with: URL(string: "https://audit.invalid/old")!)
    oldTask.taskDescription = encodeTaskDescription(channel: .bundle, taskId: id, segIndex: 0)
    oldState.ownedTaskIdentifiers.insert(oldTask.taskIdentifier)
    downloader.runs[key] = oldState
    try Data([1, 2, 3]).write(to: URL(fileURLWithPath: oldPath + ".seg0"))
    oldTask.resume()
    for _ in 0..<100 {
      if oldTask.response != nil { break }
      try await Task.sleep(nanoseconds: 1_000_000)
    }
    precondition((oldTask.response as? HTTPURLResponse)?.statusCode == 206)
    queue.addOperation {
      blockerStarted.signal()
      precondition(releaseQueue.wait(timeout: .now() + 5) == .success)
    }
    precondition(blockerStarted.wait(timeout: .now() + 5) == .success)

    let owner = Task { await downloader.cancel(channel: .bundle, taskId: id, filePath: oldPath) }
    for _ in 0..<100 {
      if downloader.lock.withLockValue({ downloader.cancellationWaiters[key] != nil }) { break }
      try await Task.sleep(nanoseconds: 1_000_000)
    }
    precondition(downloader.lock.withLockValue({ downloader.cancellationWaiters[key] != nil }))
    let rejected = await downloader.download(channel: .bundle, taskId: id,
                                             urlString: "https://audit.invalid/new",
                                             filePath: newPath, expectedSha256: nil,
                                             segmentCount: 1, minConcurrentBytes: 1)
    precondition(rejected.0 != .completed && downloader.runs[key] === oldState)
    let joined = DispatchSemaphore(value: 0)
    let second = Task {
      await downloader.cancel(channel: .bundle, taskId: id, filePath: oldPath)
      joined.signal()
    }
    try await Task.sleep(nanoseconds: 10_000_000)
    precondition(joined.wait(timeout: .now()) == .timedOut)
    releaseQueue.signal()
    await owner.value
    await second.value
    precondition(joined.wait(timeout: .now()) == .success)
    precondition(downloader.runs[key] == nil && downloader.cancellationWaiters[key] == nil)
    precondition(oldTask.state != .running)
    precondition(!FileManager.default.fileExists(atPath: oldPath + ".seg0"))

    let newState = RunState(channel: .bundle, taskId: id, filePath: newPath,
                            segmentCount: 1, sessionIdentifier: sessionIdentifier(for: .bundle),
                            maxSegmentAttempts: 0, deadline: nil)
    newState.segmentWritten = [0]
    newState.totalSize = 100
    newState.ranges = [(0, 99)]
    let newURL = URL(string: "https://audit.invalid/new")!
    let newTask = session.downloadTask(with: URL(string: "https://audit.invalid/new")!)
    newTask.taskDescription = encodeTaskDescription(channel: .bundle, taskId: id, segIndex: 0)
    downloader.runs[key] = newState
    newTask.resume()
    downloader.reconcileAndStartTasks(state: newState, url: newURL, ranges: newState.ranges)
    for _ in 0..<100 {
      if downloader.lock.withLockValue({ newState.ownedTaskIdentifiers.contains(newTask.taskIdentifier) }) { break }
      try await Task.sleep(nanoseconds: 1_000_000)
    }
    precondition(downloader.lock.withLockValue({ newState.ownedTaskIdentifiers.contains(newTask.taskIdentifier) }))
    let staleLocation = dir.appendingPathComponent("late-old-body")
    try Data(repeating: 7, count: 100).write(to: staleLocation)
    downloader.urlSession(session, downloadTask: oldTask,
                          didFinishDownloadingTo: staleLocation)
    precondition(!FileManager.default.fileExists(atPath: newPath + ".seg0"))
    let completion = DispatchSemaphore(value: 0)
    let waiter = Task {
      do {
        try await withCheckedThrowingContinuation { (cont: CheckedContinuation<Void, Error>) in
          downloader.lock.withLockValue { newState.continuation = cont }
        }
      } catch {}
      completion.signal()
    }
    for _ in 0..<100 {
      if downloader.lock.withLockValue({ newState.continuation != nil }) { break }
      try await Task.sleep(nanoseconds: 1_000_000)
    }
    precondition(downloader.lock.withLockValue({ newState.continuation != nil }))
    let staleProgress = Date(timeIntervalSinceNow: -10)
    downloader.lock.withLockValue { newState.lastProgressAt = staleProgress }
    downloader.stallWatchdogTick(state: oldState, stallSeconds: 1, interval: 100)
    precondition(downloader.lock.withLockValue({ newState.lastProgressAt == staleProgress }))
    downloader.urlSession(session, task: oldTask, didCompleteWithError: URLError(.timedOut))
    try await Task.sleep(nanoseconds: 10_000_000)
    precondition(completion.wait(timeout: .now()) == .timedOut)
    downloader.urlSession(session, downloadTask: oldTask,
                          didWriteData: 20, totalBytesWritten: 20,
                          totalBytesExpectedToWrite: 100)
    precondition(newState.segmentWritten == [0])
    let redirect = HTTPURLResponse(url: URL(string: "https://audit.invalid/old")!,
                                   statusCode: 302, httpVersion: nil, headerFields: nil)!
    downloader.urlSession(session, task: oldTask, willPerformHTTPRedirection: redirect,
                          newRequest: URLRequest(url: URL(string: "http://audit.invalid/old")!),
                          completionHandler: { _ in })
    precondition(!newState.fellBack)
    downloader.urlSession(session, downloadTask: newTask,
                          didWriteData: 20, totalBytesWritten: 20,
                          totalBytesExpectedToWrite: 100)
    precondition(newState.segmentWritten == [20])
    downloader.lock.withLockValue { newState.lastProgressAt = staleProgress }
    downloader.stallWatchdogTick(state: newState, stallSeconds: 1, interval: 100)
    precondition(downloader.lock.withLockValue({ newState.lastProgressAt > staleProgress }))
    downloader.finishContinuation(state: newState, with: CancellationError(), ranges: [])
    await waiter.value
  }

  static func auditCancelWaitsForActualWriter() async throws {
    let downloader = RangeDownloader()
    let id = "writer-" + UUID().uuidString
    let key = runKey(channel: .bundle, taskId: id)
    let dir = FileManager.default.temporaryDirectory.appendingPathComponent(id)
    try FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)
    defer { try? FileManager.default.removeItem(at: dir) }
    let path = dir.appendingPathComponent("bundle.zip").path
    let session = URLSession(configuration: .ephemeral, delegate: downloader, delegateQueue: nil)
    downloader.sessions[sessionIdentifier(for: .bundle)] = session
    defer { session.invalidateAndCancel() }
    let state = RunState(channel: .bundle, taskId: id, filePath: path,
                         segmentCount: 1, sessionIdentifier: sessionIdentifier(for: .bundle),
                         maxSegmentAttempts: 1, deadline: nil)
    let ranges: [(start: Int64, end: Int64)] = [(0, 2)]
    downloader.runs[key] = state
    try Data([1, 2, 3]).write(to: URL(fileURLWithPath: path + ".seg0"))
    precondition(downloader.beginFileMutation(state))
    let cancelled = DispatchSemaphore(value: 0)
    let cancelTask = Task {
      await downloader.cancel(channel: .bundle, taskId: id, filePath: path)
      cancelled.signal()
    }
    for _ in 0..<100 {
      if downloader.lock.withLockValue({ downloader.cancellationWaiters[key] != nil }) { break }
      try await Task.sleep(nanoseconds: 1_000_000)
    }
    precondition(downloader.lock.withLockValue({ downloader.cancellationWaiters[key] != nil }))
    precondition(cancelled.wait(timeout: .now()) == .timedOut)
    try downloader.concatenateAndFinish(state: state, ranges: ranges)
    downloader.endFileMutation(state)
    await cancelTask.value
    precondition(cancelled.wait(timeout: .now()) == .success)
    precondition(!FileManager.default.fileExists(atPath: path + ".seg0"))
    precondition(!FileManager.default.fileExists(atPath: path + ".partial"))
    precondition(downloader.runs[key] == nil)
    let replacement = RunState(channel: .bundle, taskId: id, filePath: path,
                               segmentCount: 1, sessionIdentifier: sessionIdentifier(for: .bundle),
                               maxSegmentAttempts: 1, deadline: nil)
    downloader.runs[key] = replacement
    try Data([9]).write(to: URL(fileURLWithPath: path + ".seg0"))
    precondition(!downloader.beginFileMutation(state))
    precondition(downloader.beginFileMutation(replacement))
    downloader.endFileMutation(replacement)
    let sentinel = try Data(contentsOf: URL(fileURLWithPath: path + ".seg0"))
    precondition(sentinel == Data([9]))
  }

  static func auditPermanentFallbackKeepsTaskOwner() async throws {
    let downloader = RangeDownloader()
    let id = "fallback-" + UUID().uuidString
    let key = runKey(channel: .bundle, taskId: id)
    let dir = FileManager.default.temporaryDirectory.appendingPathComponent(id)
    try FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)
    defer { try? FileManager.default.removeItem(at: dir) }
    let configuration = URLSessionConfiguration.ephemeral
    configuration.protocolClasses = [AuditHeldSegmentProtocol.self]
    let session = URLSession(configuration: configuration, delegate: downloader, delegateQueue: nil)
    downloader.sessions[sessionIdentifier(for: .bundle)] = session
    defer { session.invalidateAndCancel() }
    let oldPath = dir.appendingPathComponent("old.zip").path
    let oldState = RunState(channel: .bundle, taskId: id, filePath: oldPath,
                            segmentCount: 1, sessionIdentifier: sessionIdentifier(for: .bundle),
                            maxSegmentAttempts: 1, deadline: nil)
    oldState.fellBackKind = .authExpired
    let oldTask = session.downloadTask(with: URL(string: "https://audit.invalid/new")!)
    oldTask.taskDescription = encodeTaskDescription(channel: .bundle, taskId: id, segIndex: 0)
    oldState.ownedTaskIdentifiers.insert(oldTask.taskIdentifier)
    downloader.runs[key] = oldState
    oldTask.resume()
    let fallback = Task {
      do {
        try await withCheckedThrowingContinuation { (cont: CheckedContinuation<Void, Error>) in
          downloader.lock.withLockValue { oldState.continuation = cont }
          downloader.finalizePermanentFallback(state: oldState, session: session, ranges: [])
        }
        preconditionFailure("permanent fallback completed as success")
      } catch is FallbackError {}
    }
    try await fallback.value
    precondition(AuditEnumeration.done.wait(timeout: .now() + 5) == .success)
    precondition(oldTask.state != .running)
    precondition(downloader.clearRun(key: key, state: oldState))

    let newPath = oldPath
    let newState = RunState(channel: .bundle, taskId: id, filePath: newPath,
                            segmentCount: 1, sessionIdentifier: sessionIdentifier(for: .bundle),
                            maxSegmentAttempts: 1, deadline: nil)
    downloader.runs[key] = newState
    let newTask = session.downloadTask(with: URL(string: "https://audit.invalid/new")!)
    newTask.taskDescription = encodeTaskDescription(channel: .bundle, taskId: id, segIndex: 0)
    newTask.resume()
    try Data([9]).write(to: URL(fileURLWithPath: newPath + ".seg0"))
    downloader.finalizePermanentFallback(state: oldState, session: session,
                                         ranges: [(0, 99)])
    precondition(AuditEnumeration.done.wait(timeout: .now() + 5) == .success)
    precondition(newTask.state == .running)
    let sentinel = try Data(contentsOf: URL(fileURLWithPath: newPath + ".seg0"))
    precondition(sentinel == Data([9]))
  }
}
