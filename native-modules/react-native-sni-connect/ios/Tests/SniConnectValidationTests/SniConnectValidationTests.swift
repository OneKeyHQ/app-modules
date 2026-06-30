import XCTest
@testable import SniConnectValidationCore

final class SniConnectValidationTests: XCTestCase {

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

  func testDiagnosticsRedactsIpLiteralsFromStructuredLogs() {
    let log = SniConnectCoreDiagnostics.event("sni_request_result", [
      ("errorMessage", "connect ECONNREFUSED 93.184.216.34:443"),
      ("ipv6Error", "connect [2001:4860:4860::8888]:443"),
      ("timestamp", "10:12:35"),
    ])

    XCTAssertTrue(log.contains("errorMessage=connect_ECONNREFUSED_<ip>:443"))
    XCTAssertTrue(log.contains("ipv6Error=connect_<ip6>:443"))
    XCTAssertTrue(log.contains("timestamp=10:12:35"))
    XCTAssertFalse(log.contains("93.184.216.34"))
    XCTAssertFalse(log.contains("2001:4860:4860::8888"))
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
    assertValidationFails {
      try SniConnectValidation.validateRequestId("")
    }
    assertValidationFails {
      try SniConnectValidation.validateRequestId(String(repeating: "x", count: 129))
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

  func testRequestLimiterEnforcesGlobalAndPerDestinationLimits() throws {
    let limiter = SniConnectRequestLimiter(maxActiveRequests: 2, maxActiveRequestsPerPair: 1)
    let firstToken = try limiter.acquire(hostname: "Example.com", ip: "93.184.216.34")

    assertValidationFails {
      _ = try limiter.acquire(hostname: "example.com", ip: "93.184.216.34")
    }

    let secondToken = try limiter.acquire(hostname: "example.com", ip: "93.184.216.35")
    assertValidationFails {
      _ = try limiter.acquire(hostname: "example.net", ip: "93.184.216.36")
    }

    firstToken.release()
    let replacementToken = try limiter.acquire(hostname: "example.com", ip: "93.184.216.34")
    firstToken.release()
    secondToken.release()
    replacementToken.release()
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
}
