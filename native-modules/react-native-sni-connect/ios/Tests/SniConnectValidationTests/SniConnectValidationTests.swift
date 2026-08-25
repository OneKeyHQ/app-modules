import XCTest
@testable import SniConnectValidationCore

final class SniConnectValidationTests: XCTestCase {

  func testSessionWithoutForwardingDataDelegateAllowsResponse() {
    switch SniConnectSessionDelegatePolicy.responseDispositionWithoutForwardingDelegate {
    case .allow:
      break
    default:
      XCTFail("A session without a forwarding data delegate must allow its response")
    }
  }

  func testAcceptsValidRequestBoundaryValues() throws {
    XCTAssertNoThrow(try SniConnectValidation.validateRequestId("req-1"))
    XCTAssertNoThrow(try SniConnectValidation.validateTimeout(120_000))
    XCTAssertNoThrow(try SniConnectValidation.validateBody(String(repeating: "a", count: 1024 * 1024)))
    XCTAssertNoThrow(try SniConnectValidation.validatePublicIP("93.184.216.34"))
    XCTAssertNoThrow(try SniConnectValidation.validatePublicIP("2001:4860:4860::8888"))
    XCTAssertNoThrow(try SniConnectValidation.validateHostname("api.example.com"))

    XCTAssertEqual(try SniConnectValidation.normalizeMethod(" get "), "GET")
    XCTAssertEqual(try SniConnectValidation.normalizePath(""), "/")
    XCTAssertEqual(try SniConnectValidation.normalizePath("v1?q=1"), "/v1?q=1")
  }

  func testRejectsIpLiteralHostnames() {
    assertValidationFails {
      try SniConnectValidation.validateHostname("93.184.216.34")
    }
    assertValidationFails {
      try SniConnectValidation.validateHostname("2001:4860:4860::8888")
    }
  }

  func testRejectsMalformedHostnames() {
    [
      "",
      "-example.com",
      "example-.com",
      "example..com",
      "bad_host.example",
      "https://example.com",
      "example.com:443",
      String(repeating: "a", count: 64) + ".example.com",
      String(repeating: "a", count: 250) + ".com",
    ].forEach { hostname in
      assertValidationFails {
        try SniConnectValidation.validateHostname(hostname)
      }
    }
  }

  func testRejectsUnsafeIpv4Destinations() {
    [
      "example.com",
      "93.184.216.34:443",
      " 93.184.216.34",
      "10.0.0.1",
      "127.0.0.1",
      "100.64.0.1",
      "169.254.169.254",
      "172.16.0.1",
      "192.168.1.1",
      "192.0.2.1",
      "198.18.0.1",
      "198.51.100.1",
      "203.0.113.1",
      "224.0.0.1",
      "255.255.255.255",
    ].forEach { ip in
      assertValidationFails {
        try SniConnectValidation.validatePublicIP(ip)
      }
    }
  }

  func testRejectsUnsafeIpv6DestinationsAndTransitionForms() {
    [
      "::",
      "::1",
      "fe80::1",
      "fc00::1",
      "ff00::1",
      "100::1",
      "2001::1",
      "2001:2::1",
      "2001:db8::1",
      "2002:0a00:0001::1",
      "::ffff:10.0.0.1",
      "64:ff9b::10.0.0.1",
      "64:ff9b:1::1",
      "2001:4860:4860::8888%en0",
      "[2001:4860:4860::8888]",
    ].forEach { ip in
      assertValidationFails {
        try SniConnectValidation.validatePublicIP(ip)
      }
    }
  }

  func testRejectsUnsupportedMethodsAndUnsafePaths() {
    ["TRACE", "CONNECT", "", "GET\n"].forEach { method in
      assertValidationFails {
        _ = try SniConnectValidation.normalizeMethod(method)
      }
    }

    [
      "https://example.com",
      "http://example.com",
      "//example.com/path",
      "javascript:alert(1)",
      "/path\nInjected: yes",
      "/" + String(repeating: "a", count: 8192),
    ].forEach { path in
      assertValidationFails {
        _ = try SniConnectValidation.normalizePath(path)
      }
    }
  }

  func testEnforcesRequestIdTimeoutAndBodyLimits() {
    XCTAssertNoThrow(
      try SniConnectValidation.validateRequestId(String(repeating: "界", count: 42))
    )
    assertValidationFails {
      try SniConnectValidation.validateRequestId("")
    }
    assertValidationFails {
      try SniConnectValidation.validateRequestId(String(repeating: "x", count: 129))
    }
    assertValidationFails {
      try SniConnectValidation.validateRequestId(String(repeating: "界", count: 43))
    }
    assertValidationFails {
      try SniConnectValidation.validateRequestId("req\n1")
    }
    assertValidationFails {
      try SniConnectValidation.validateTimeout(0)
    }
    assertValidationFails {
      try SniConnectValidation.validateTimeout(120_001)
    }
    assertValidationFails {
      try SniConnectValidation.validateTimeout(.nan)
    }
    assertValidationFails {
      try SniConnectValidation.validateBody(String(repeating: "a", count: 1024 * 1024 + 1))
    }
  }

  func testFiltersModuleOwnedHeadersAndRejectsUnsafeHeaders() throws {
    let normalized = try SniConnectValidation.normalizeHeaders([
      "Host": "evil.example",
      "Content-Length": "9999",
      "Accept-Encoding": "gzip",
      "x-emascurl-config-id": "evil",
      "X-Test": "ok",
    ])

    XCTAssertFalse(normalized.keys.contains { $0.caseInsensitiveCompare("host") == .orderedSame })
    XCTAssertFalse(normalized.keys.contains { $0.caseInsensitiveCompare("content-length") == .orderedSame })
    XCTAssertFalse(normalized.keys.contains { $0.caseInsensitiveCompare("accept-encoding") == .orderedSame })
    XCTAssertEqual(normalized["X-Test"], "ok")

    [
      ["Connection": "close"],
      ["Proxy-Authorization": "secret"],
      ["Transfer-Encoding": "chunked"],
      ["Expect": "100-continue"],
      [":authority": "evil.example"],
      ["Bad Header": "x"],
      ["X-Test": "line\nbreak"],
      ["X-Test": String(repeating: "x", count: 8 * 1024 + 1)],
    ].forEach { headers in
      assertValidationFails {
        _ = try SniConnectValidation.normalizeHeaders(headers)
      }
    }

    assertValidationFails {
      _ = try SniConnectValidation.normalizeHeaders(Dictionary(uniqueKeysWithValues: (0...64).map { ("X-\($0)", "v") }))
    }
    assertValidationFails {
      _ = try SniConnectValidation.normalizeHeaders(Dictionary(uniqueKeysWithValues: (0...4).map { ("X-\($0)", String(repeating: "x", count: 7 * 1024)) }))
    }
  }

  func testRejectsAmbiguousMethodBodyCombinations() throws {
    assertValidationFails {
      try SniConnectValidation.validateMethodBody(method: "GET", body: "")
    }
    assertValidationFails {
      try SniConnectValidation.validateMethodBody(method: "HEAD", body: "payload")
    }
    assertValidationFails {
      try SniConnectValidation.validateMethodBody(method: "POST", body: nil)
    }
    assertValidationFails {
      try SniConnectValidation.validateMethodBody(method: "PUT", body: nil)
    }
    assertValidationFails {
      try SniConnectValidation.validateMethodBody(method: "PATCH", body: nil)
    }

    XCTAssertNoThrow(try SniConnectValidation.validateMethodBody(method: "POST", body: ""))
    XCTAssertNoThrow(try SniConnectValidation.validateMethodBody(method: "DELETE", body: nil))
    XCTAssertNoThrow(try SniConnectValidation.validateMethodBody(method: "OPTIONS", body: nil))
  }

  func testTwentySamePairRequestsProduceSixteenActiveAndFourPending() async throws {
    let limiter = SniConnectRequestLimiter()
    var tasks: [String: Task<SniConnectRequestLimiter.Token, Error>] = [:]

    for index in 0..<20 {
      let requestId = String(format: "req-%02d", index)
      tasks[requestId] = Task {
        try await limiter.acquire(
          hostname: "Example.com",
          ip: index.isMultiple(of: 2)
            ? "2001:4860:4860::8888"
            : "2001:4860:4860:0:0:0:0:8888",
          requestId: requestId
        )
      }
    }

    await waitForPendingRequests(4, in: limiter)
    let saturated = limiter.snapshot(
      hostname: "EXAMPLE.COM",
      ip: "2001:4860:4860::8888"
    )
    XCTAssertEqual(saturated.activeRequests, 16)
    XCTAssertEqual(saturated.activeRequestsForPair, 16)
    XCTAssertEqual(saturated.pendingRequests, 4)
    XCTAssertEqual(saturated.pendingRequestsForPair, 4)
    XCTAssertEqual(saturated.activeRequestIdsForPair.count, 16)
    XCTAssertEqual(saturated.pendingRequestIdsForPair.count, 4)

    for requestId in saturated.pendingRequestIdsForPair {
      tasks[requestId]?.cancel()
    }
    for requestId in saturated.pendingRequestIdsForPair {
      do {
        _ = try await tasks[requestId]!.value
        XCTFail("Expected pending request \(requestId) to be cancelled")
      } catch is CancellationError {
        // Expected.
      }
    }

    XCTAssertEqual(limiter.pendingRequestCount, 0)
    for requestId in saturated.activeRequestIdsForPair {
      let token = try await tasks[requestId]!.value
      token.release()
    }
    let drained = limiter.snapshot(
      hostname: "example.com",
      ip: "2001:4860:4860:0:0:0:0:8888"
    )
    XCTAssertEqual(drained.activeRequests, 0)
    XCTAssertEqual(drained.pendingRequests, 0)

    let recovery = try await limiter.acquire(
      hostname: "example.com",
      ip: "2001:4860:4860::8888",
      requestId: "recovery"
    )
    recovery.release()
  }

  func testRequestLimiterQueuesGlobalAndPerDestinationLimits() async throws {
    let limiter = SniConnectRequestLimiter(maxActiveRequests: 2, maxActiveRequestsPerPair: 1)
    let firstToken = try await limiter.acquire(hostname: "Example.com", ip: "93.184.216.34")

    let sameDestinationTask = Task {
      try await limiter.acquire(hostname: "example.com", ip: "93.184.216.34")
    }
    await waitForPendingRequests(1, in: limiter)

    let secondToken = try await limiter.acquire(hostname: "example.com", ip: "93.184.216.35")
    let globallyQueuedTask = Task {
      try await limiter.acquire(hostname: "example.net", ip: "93.184.216.36")
    }
    await waitForPendingRequests(2, in: limiter)

    firstToken.release()
    let replacementToken = try await sameDestinationTask.value
    firstToken.release()
    secondToken.release()
    let globallyQueuedToken = try await globallyQueuedTask.value
    replacementToken.release()
    globallyQueuedToken.release()
  }

  func testRequestLimiterSnapshotCanonicalizesTargetAndReturnsRequestIDs() async throws {
    let limiter = SniConnectRequestLimiter(maxActiveRequests: 2, maxActiveRequestsPerPair: 1)
    let activeTarget = try await limiter.acquire(
      hostname: "Example.com",
      ip: "2001:4860:4860::8888",
      requestId: "active-target"
    )
    let activeOther = try await limiter.acquire(
      hostname: "example.com",
      ip: "93.184.216.34",
      requestId: "active-other"
    )
    let pendingTargetTask = Task {
      try await limiter.acquire(
        hostname: "example.com",
        ip: "2001:4860:4860:0:0:0:0:8888",
        requestId: "pending-target"
      )
    }
    let pendingWithoutIDTask = Task {
      try await limiter.acquire(
        hostname: "example.com",
        ip: "2001:4860:4860::8888",
        requestId: ""
      )
    }
    await waitForPendingRequests(2, in: limiter)

    let snapshot = limiter.snapshot(
      hostname: "EXAMPLE.COM",
      ip: "2001:4860:4860:0:0:0:0:8888"
    )
    XCTAssertEqual(snapshot.activeRequests, 2)
    XCTAssertEqual(snapshot.activeRequestsForPair, 1)
    XCTAssertEqual(snapshot.pendingRequests, 2)
    XCTAssertEqual(snapshot.pendingRequestsForPair, 2)
    XCTAssertEqual(snapshot.activeRequestIdsForPair, ["active-target"])
    XCTAssertEqual(snapshot.pendingRequestIdsForPair, ["pending-target"])

    pendingTargetTask.cancel()
    pendingWithoutIDTask.cancel()
    _ = try? await pendingTargetTask.value
    _ = try? await pendingWithoutIDTask.value
    activeTarget.release()
    activeOther.release()
  }

  func testRequestLimiterRejectsTheTwoHundredFiftySeventhPendingRequest() async throws {
    let limiter = SniConnectRequestLimiter(
      maxActiveRequests: 1,
      maxActiveRequestsPerPair: 1,
      maxPendingRequests: 256
    )
    let activeToken = try await limiter.acquire(
      hostname: "example.com",
      ip: "93.184.216.34"
    )
    let queuedTasks = (0..<256).map { index in
      Task {
        try await limiter.acquire(
          hostname: "example.com",
          ip: "93.184.216.34",
          requestId: "pending-\(index)"
        )
      }
    }
    await waitForPendingRequests(256, in: limiter)

    do {
      _ = try await limiter.acquire(hostname: "example.com", ip: "93.184.216.34")
      XCTFail("Expected the bounded pending queue to reject overflow")
    } catch SniConnectValidation.ValidationError.resourceLimit(_) {
      // Expected.
    }

    queuedTasks.forEach { $0.cancel() }
    for task in queuedTasks {
      _ = try? await task.value
    }
    XCTAssertEqual(limiter.pendingRequestCount, 0)
    activeToken.release()
  }

  func testWallClockDeadlineCancelsPendingLimiterWait() async throws {
    let limiter = SniConnectRequestLimiter(maxActiveRequests: 1, maxActiveRequestsPerPair: 1)
    let activeToken = try await limiter.acquire(
      hostname: "example.com",
      ip: "93.184.216.34"
    )

    do {
      _ = try await SniConnectWallClockDeadline.run(timeoutMilliseconds: 20) {
        try await limiter.acquire(
          hostname: "example.com",
          ip: "93.184.216.34",
          requestId: "deadline-pending"
        )
      }
      XCTFail("Expected queue wait to consume the wall-clock deadline")
    } catch let error as SniConnectTimeout {
      XCTAssertEqual(error, .deadlineExceeded)
    }

    XCTAssertEqual(limiter.pendingRequestCount, 0)
    activeToken.release()
  }

  func testResolverRegistryKeepsResolverClassUntilAllSessionsReleaseIt() throws {
    final class ResolverA: NSObject {}
    final class ResolverB: NSObject {}
    var classes: [AnyClass] = [ResolverA.self, ResolverB.self]
    let registry = SniConnectPinnedResolverRegistry(maxEntries: 2)

    let first: AnyClass = try registry.resolverClass(hostname: "Example.com", ip: "93.184.216.34") {
      classes.removeFirst()
    }
    let same: AnyClass = try registry.resolverClass(hostname: "example.com", ip: "93.184.216.34") {
      XCTFail("Expected resolver class reuse for identical hostname/ip")
      return ResolverB.self
    }
    XCTAssertTrue(first === same)
    XCTAssertEqual(registry.resolve(domain: "example.com", resolverClass: first), "93.184.216.34")
    XCTAssertEqual(registry.entryCount, 1)
    XCTAssertEqual(registry.allocatedClassCount, 1)

    _ = try registry.resolverClass(hostname: "example.com", ip: "93.184.216.35") {
      classes.removeFirst()
    }
    XCTAssertEqual(registry.entryCount, 2)
    XCTAssertEqual(registry.allocatedClassCount, 2)
    assertValidationFails {
      _ = try registry.resolverClass(hostname: "example.net", ip: "93.184.216.36") {
        XCTFail("Bounded registry must not allocate past capacity")
        return ResolverB.self
      }
    }

    registry.release(hostname: "example.com", ip: "93.184.216.34")
    XCTAssertEqual(registry.entryCount, 2)
    XCTAssertEqual(registry.resolve(domain: "example.com", resolverClass: first), "93.184.216.34")

    registry.release(hostname: "example.com", ip: "93.184.216.34")
    XCTAssertEqual(registry.entryCount, 1)

    registry.release(hostname: "example.com", ip: "93.184.216.35")
    XCTAssertEqual(registry.entryCount, 0)
    let reused: AnyClass = try registry.resolverClass(hostname: "example.net", ip: "93.184.216.36") {
      XCTFail("Expected cleared resolver class to be reused")
      return ResolverB.self
    }
    XCTAssertTrue(reused === ResolverA.self || reused === ResolverB.self)
    XCTAssertEqual(registry.allocatedClassCount, 2)
    XCTAssertEqual(registry.resolve(domain: "example.net", resolverClass: reused), "93.184.216.36")
  }

  func testEndpointCachesReuseEquivalentIPv6Spellings() throws {
    final class ResolverA: NSObject {}
    final class ResolverB: NSObject {}
    let registry = SniConnectPinnedResolverRegistry(maxEntries: 2)
    let compressedIP = "2606:4700:4700::1111"
    let expandedIP = "2606:4700:4700:0:0:0:0:1111"

    XCTAssertEqual(
      SniConnectEndpointKey(hostname: "Example.com", ip: compressedIP),
      SniConnectEndpointKey(hostname: "example.com", ip: expandedIP)
    )

    let first: AnyClass = try registry.resolverClass(hostname: "Example.com", ip: compressedIP) {
      ResolverA.self
    }
    let same: AnyClass = try registry.resolverClass(hostname: "example.com", ip: expandedIP) {
      XCTFail("Equivalent IPv6 spellings must reuse the resolver entry")
      return ResolverB.self
    }

    XCTAssertTrue(first === same)
    XCTAssertEqual(registry.entryCount, 1)
    XCTAssertEqual(registry.allocatedClassCount, 1)

    registry.release(hostname: "example.com", ip: expandedIP)
    XCTAssertEqual(registry.entryCount, 1)
    registry.release(hostname: "example.com", ip: compressedIP)
    XCTAssertEqual(registry.entryCount, 0)
  }

  func testResponseTextDecodePreservesOriginalJsonText() throws {
    let json = "{  \"b\": 1, \"a\": [true, null] }\n"
    XCTAssertEqual(SniConnectResponseText.decode(Data(json.utf8)), json)
    XCTAssertEqual(SniConnectResponseText.decode(Data()), "")
  }

  func testWallClockDeadlineReturnsFastOperation() async throws {
    let value = try await SniConnectWallClockDeadline.run(timeoutMilliseconds: 1_000) {
      return "ok"
    }

    XCTAssertEqual(value, "ok")
  }

  func testWallClockDeadlineTimesOutSlowOperation() async throws {
    do {
      _ = try await SniConnectWallClockDeadline.run(timeoutMilliseconds: 10) {
        try await Task.sleep(nanoseconds: 100_000_000)
        return "late"
      }
      XCTFail("Expected wall-clock deadline to throw")
    } catch let error as SniConnectTimeout {
      XCTAssertEqual(error, .deadlineExceeded)
    }
  }

  func testWallClockDeadlineRejectsResultAfterAbsoluteDeadline() async throws {
    let deadline = SniConnectWallClockDeadline.makeDeadline(timeoutMilliseconds: 5)
    try await Task.sleep(nanoseconds: 20_000_000)

    do {
      _ = try await SniConnectWallClockDeadline.run(until: deadline) {
        return "late"
      }
      XCTFail("Expected an operation result produced after the deadline to be rejected")
    } catch let error as SniConnectTimeout {
      XCTAssertEqual(error, .deadlineExceeded)
    }
  }

  func testResponseHeaderMapsPreserveRawRepeatedSetCookieHeaders() throws {
    let headerMaps = SniConnectResponseHeaders.make(rawHeaderFields: [
      (name: "Content-Type", value: "application/json"),
      (name: "Set-Cookie", value: "session=one; Path=/; HttpOnly"),
      (name: "set-cookie", value: "theme=dark; Path=/; Secure"),
    ])

    XCTAssertEqual(headerMaps.singleValueHeaders["content-type"], "application/json")
    XCTAssertEqual(headerMaps.singleValueHeaders["set-cookie"], "theme=dark; Path=/; Secure")
    XCTAssertEqual(headerMaps.multiValueHeaders["set-cookie"], [
      "session=one; Path=/; HttpOnly",
      "theme=dark; Path=/; Secure",
    ])
  }

  func testResponseHeaderMapsPreserveArrayBackedHeaderFields() throws {
    let headerMaps = SniConnectResponseHeaders.make(from: [
      "Set-Cookie": [
        "session=one; Path=/; HttpOnly",
        "theme=dark; Path=/; Secure",
      ],
    ])

    XCTAssertEqual(headerMaps.singleValueHeaders["set-cookie"], "theme=dark; Path=/; Secure")
    XCTAssertEqual(headerMaps.multiValueHeaders["set-cookie"], [
      "session=one; Path=/; HttpOnly",
      "theme=dark; Path=/; Secure",
    ])
  }

  private func assertValidationFails(_ block: () throws -> Void, file: StaticString = #filePath, line: UInt = #line) {
    XCTAssertThrowsError(try block(), file: file, line: line)
  }

  private func waitForPendingRequests(
    _ expectedCount: Int,
    in limiter: SniConnectRequestLimiter,
    file: StaticString = #filePath,
    line: UInt = #line
  ) async {
    let deadline = Date().addingTimeInterval(5)
    while Date() < deadline {
      if limiter.pendingRequestCount == expectedCount {
        return
      }
      try? await Task.sleep(nanoseconds: 1_000_000)
    }
    XCTFail(
      "Expected \(expectedCount) pending requests, got \(limiter.pendingRequestCount)",
      file: file,
      line: line
    )
  }
}
