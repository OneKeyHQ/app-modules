import XCTest
import Foundation
@testable import RangeDownloadLogic

// MARK: - RangeDownloader deterministic-logic unit tests
//
// These tests cover the DEPENDENCY-FREE logic extracted into
// `ios/RangeDownloadLogic.swift` (a verbatim move from
// `ReactNativeRangeDownloader.swift`). Each test maps to an OCDS section; see the
// `// OCDS:` tag on each method.
//
// ───────────────────────────────────────────────────────────────────────────
// NOT COVERED HERE — DEVICE-ONLY (do NOT attempt to fake in a unit-test process):
//
// The actual concurrent BACKGROUND `URLSession` download is device territory. A
// background session (URLSession(configuration: .background, delegate:)) does NOT
// honor custom URLProtocol and only behaves correctly inside a real app lifecycle
// (nsurlsessiond, app suspend/relaunch, handleEventsForBackgroundURLSession).
// Therefore the following RUNTIME behaviors require a real device + the
// NativeLogger app-latest.log checklist, NOT this harness:
//   - OCDS-T1/T2  : concurrent multi-range segment download + segment stash/concat
//   - OCDS-T8     : resume across app suspend/kill (which `.segN` already exist)
//   - OCDS-T10    : handleEventsForBackgroundURLSession completion delivery
// The PROBE uses an ephemeral FOREGROUND session; even that is an integration
// concern (real network) and is intentionally not exercised here.
// ───────────────────────────────────────────────────────────────────────────
final class RangeDownloadLogicTests: XCTestCase {

  func testFirmwareArtifactDownloadKeyIsolatesTransactions() {
    let first = firmwareArtifactDownloadKey(
      transactionId: "fwtx:first",
      expectedSize: 42,
      expectedSha256: String(repeating: "a", count: 64)
    )
    let second = firmwareArtifactDownloadKey(
      transactionId: "fwtx:second",
      expectedSize: 42,
      expectedSha256: String(repeating: "a", count: 64)
    )

    XCTAssertNotEqual(first, second)
  }

  func testFirmwareArtifactWallClockDeadlineRejectsSlowOperation() async {
    do {
      _ = try await FirmwareArtifactWallClockDeadline.run(
        timeoutSeconds: 0.01
      ) {
        try await Task.sleep(nanoseconds: 1_000_000_000)
        return true
      }
      XCTFail("Expected the wall-clock deadline to reject")
    } catch FirmwareArtifactDeadlineError.exceeded {
      // Expected.
    } catch {
      XCTFail("Unexpected error: \(error)")
    }
  }

  func testFirmwareBackgroundErrorsKeepArtifactCodesInDescriptions() {
    XCTAssertTrue(
      String(
        describing: FirmwareBackgroundDownloadError.transferFailed
      ).contains("ARTIFACT_NETWORK_FAILED")
    )
    XCTAssertTrue(
      String(
        describing: FirmwareBackgroundDownloadError.deadlineExceeded
      ).contains("ARTIFACT_DEADLINE_EXCEEDED")
    )
  }

  // MARK: §4 — HTTP status classification
  //
  // OCDS §4. Mirrors Android's IsPermanentHttpStatusTest matrix. Asserts BOTH the
  // RangeFallbackClass kind AND the resolved transient/permanent failureClass.

  func testClassifyStatus_200_serverIgnoredRange_permanent() {
    let k = RangeDownloadLogic.classifyStatus(200)
    XCTAssertEqual(k, .serverIgnoredRange)
    XCTAssertEqual(k.failureClass, .fallbackPermanent)
  }

  func testClassifyStatus_416_transient_regression() {
    // §4 regression guard: 416 to a resume request must be TRANSIENT
    // (re-evaluate size, keep segments) — never permanent.
    let k = RangeDownloadLogic.classifyStatus(416)
    XCTAssertEqual(k, .transientNetwork)
    XCTAssertEqual(k.failureClass, .fallbackTransient)
  }

  func testClassifyStatus_401_403_authExpired_permanent() {
    for status in [401, 403] {
      let k = RangeDownloadLogic.classifyStatus(status)
      XCTAssertEqual(k, .authExpired, "status \(status)")
      XCTAssertEqual(k.failureClass, .fallbackPermanent, "status \(status)")
    }
  }

  func testClassifyStatus_404_410_notFound_permanent() {
    for status in [404, 410] {
      let k = RangeDownloadLogic.classifyStatus(status)
      XCTAssertEqual(k, .notFound, "status \(status)")
      XCTAssertEqual(k.failureClass, .fallbackPermanent, "status \(status)")
    }
  }

  func testClassifyStatus_408_429_throttled_transient() {
    for status in [408, 429] {
      let k = RangeDownloadLogic.classifyStatus(status)
      XCTAssertEqual(k, .throttled, "status \(status)")
      XCTAssertEqual(k.failureClass, .fallbackTransient, "status \(status)")
    }
  }

  func testClassifyStatus_501_505_rangeUnsupported_permanent() {
    // Explicit Permanent carve-outs from the 5xx → Transient default.
    for status in [501, 505] {
      let k = RangeDownloadLogic.classifyStatus(status)
      XCTAssertEqual(k, .rangeUnsupported, "status \(status)")
      XCTAssertEqual(k.failureClass, .fallbackPermanent, "status \(status)")
    }
  }

  func testClassifyStatus_other5xx_transient() {
    // 5xx (except 501/505) → throttled → Transient.
    for status in [500, 502, 503, 504, 599] {
      let k = RangeDownloadLogic.classifyStatus(status)
      XCTAssertEqual(k, .throttled, "status \(status)")
      XCTAssertEqual(k.failureClass, .fallbackTransient, "status \(status)")
    }
  }

  func testClassifyStatus_other4xx_permanent() {
    // Default 4xx (other than 401/403/404/408/410/429) → rangeUnsupported → Permanent.
    for status in [400, 402, 405, 418, 451, 499] {
      let k = RangeDownloadLogic.classifyStatus(status)
      XCTAssertEqual(k, .rangeUnsupported, "status \(status)")
      XCTAssertEqual(k.failureClass, .fallbackPermanent, "status \(status)")
    }
  }

  func testClassifyStatus_unknown_permanent() {
    // §4 catch-all: anything outside the known ranges → Permanent.
    for status in [0, 100, 204, 301, 302, 600, 999, -1] {
      let k = RangeDownloadLogic.classifyStatus(status)
      XCTAssertEqual(k, .rangeUnsupported, "status \(status)")
      XCTAssertEqual(k.failureClass, .fallbackPermanent, "status \(status)")
    }
  }

  // MARK: §4 — failureClass partition sanity (full enum coverage)

  func testFailureClass_fullPartition() {
    let transient: [RangeFallbackClass] = [.transientNetwork, .throttled, .budgetExhausted]
    let permanent: [RangeFallbackClass] = [
      .serverIgnoredRange, .rangeUnsupported, .authExpired, .notFound,
      .redirectRejected, .checksumMismatch, .multipartOrBadTotal,
    ]
    for k in transient {
      XCTAssertEqual(k.failureClass, .fallbackTransient, "\(k)")
    }
    for k in permanent {
      XCTAssertEqual(k.failureClass, .fallbackPermanent, "\(k)")
    }
  }

  func testDownloadClass_isFallback() {
    XCTAssertFalse(RangeDownloadClass.completed.isFallback)
    XCTAssertTrue(RangeDownloadClass.fallbackTransient.isFallback)
    XCTAssertTrue(RangeDownloadClass.fallbackPermanent.isFallback)
  }

  // MARK: §5.5 — Content-Range parsing
  //
  // OCDS §5.5 integrity parsing. Valid / misaligned / '*'-total / malformed.

  func testParseContentRangeTotal_valid() {
    XCTAssertEqual(RangeDownloadLogic.parseContentRangeTotal("bytes 0-0/65226095"), 65226095)
    XCTAssertEqual(RangeDownloadLogic.parseContentRangeTotal("bytes 100-199/200"), 200)
  }

  func testParseContentRangeTotal_starTotal_nil() {
    // "bytes 0-0/*" → unknown total → nil.
    XCTAssertNil(RangeDownloadLogic.parseContentRangeTotal("bytes 0-0/*"))
  }

  func testParseContentRangeTotal_garbage_nil() {
    XCTAssertNil(RangeDownloadLogic.parseContentRangeTotal("bytes 0-0/abc"))
    XCTAssertNil(RangeDownloadLogic.parseContentRangeTotal("no-slash-here"))
    XCTAssertNil(RangeDownloadLogic.parseContentRangeTotal(""))
  }

  func testParseContentRangeBounds_valid() {
    let b = RangeDownloadLogic.parseContentRangeBounds("bytes 0-0/65226095")
    XCTAssertEqual(b?.start, 0)
    XCTAssertEqual(b?.end, 0)
    let b2 = RangeDownloadLogic.parseContentRangeBounds("bytes 1024-2047/4096")
    XCTAssertEqual(b2?.start, 1024)
    XCTAssertEqual(b2?.end, 2047)
  }

  func testParseContentRangeBounds_starTotalIgnored() {
    // The '*' total is dropped (we only need the bounds); bounds still parse.
    let b = RangeDownloadLogic.parseContentRangeBounds("bytes 5-9/*")
    XCTAssertEqual(b?.start, 5)
    XCTAssertEqual(b?.end, 9)
  }

  func testParseContentRangeBounds_malformed_nil() {
    XCTAssertNil(RangeDownloadLogic.parseContentRangeBounds("bytes */1234"))   // no a-b
    XCTAssertNil(RangeDownloadLogic.parseContentRangeBounds("bytes 0/1234"))   // no dash
    XCTAssertNil(RangeDownloadLogic.parseContentRangeBounds("0-9/1234"))       // no leading token/space
    XCTAssertNil(RangeDownloadLogic.parseContentRangeBounds("bytes a-b/1234")) // non-numeric bounds
    XCTAssertNil(RangeDownloadLogic.parseContentRangeBounds(""))
  }

  // MARK: §5.2 / §5.3 — range planning
  //
  // OCDS §5.2/§5.3. ceil-chunk; last segment absorbs remainder; past-EOF dropped.

  func testPlanRanges_normal_8segments() {
    // 8000 bytes / 8 → chunk 1000, contiguous, fully covering [0, 7999].
    let r = RangeDownloadLogic.planRanges(total: 8000, segments: 8)
    XCTAssertEqual(r.count, 8)
    XCTAssertEqual(r.first?.start, 0)
    XCTAssertEqual(r.last?.end, 7999)
    // contiguous, no gaps/overlaps
    for i in 1..<r.count {
      XCTAssertEqual(r[i].start, r[i - 1].end + 1, "gap/overlap at \(i)")
    }
    // exact coverage of the whole file
    let covered = r.reduce(Int64(0)) { $0 + ($1.end - $1.start + 1) }
    XCTAssertEqual(covered, 8000)
  }

  func testPlanRanges_remainder_lastAbsorbsViaCeil() {
    // 10 bytes / 3 → ceil chunk = 4 → [0-3][4-7][8-9]; 3rd is the short remainder.
    let r = RangeDownloadLogic.planRanges(total: 10, segments: 3)
    XCTAssertEqual(r.map { [$0.start, $0.end] }, [[0, 3], [4, 7], [8, 9]])
    let covered = r.reduce(Int64(0)) { $0 + ($1.end - $1.start + 1) }
    XCTAssertEqual(covered, 10)
  }

  func testPlanRanges_tiny_fewerSegmentsThanRequested() {
    // 3 bytes / 8 → ceil chunk = 1 → only 3 non-empty segments; past-EOF dropped.
    let r = RangeDownloadLogic.planRanges(total: 3, segments: 8)
    XCTAssertEqual(r.count, 3)
    XCTAssertEqual(r.map { [$0.start, $0.end] }, [[0, 0], [1, 1], [2, 2]])
  }

  func testPlanRanges_singleByte() {
    let r = RangeDownloadLogic.planRanges(total: 1, segments: 8)
    XCTAssertEqual(r.count, 1)
    XCTAssertEqual(r.first?.start, 0)
    XCTAssertEqual(r.first?.end, 0)
  }

  func testPlanRanges_exactMultiple() {
    // 16 / 8 → chunk 2, exactly 8 segments, last ends at 15.
    let r = RangeDownloadLogic.planRanges(total: 16, segments: 8)
    XCTAssertEqual(r.count, 8)
    XCTAssertEqual(r.last?.start, 14)
    XCTAssertEqual(r.last?.end, 15)
    let covered = r.reduce(Int64(0)) { $0 + ($1.end - $1.start + 1) }
    XCTAssertEqual(covered, 16)
  }

  // MARK: §5.4 — Retry-After parsing + backoff
  //
  // OCDS §5.4. delta-seconds only; backoff = Retry-After override OR exp+jitter cap.

  func testParseRetryAfterSeconds_deltaSeconds() {
    XCTAssertEqual(RangeDownloadLogic.parseRetryAfterSeconds("5"), 5)
    XCTAssertEqual(RangeDownloadLogic.parseRetryAfterSeconds("  30  "), 30)
    XCTAssertEqual(RangeDownloadLogic.parseRetryAfterSeconds("0"), 0)
  }

  func testParseRetryAfterSeconds_absentOrGarbage_nil() {
    XCTAssertNil(RangeDownloadLogic.parseRetryAfterSeconds(nil))
    XCTAssertNil(RangeDownloadLogic.parseRetryAfterSeconds(""))
    XCTAssertNil(RangeDownloadLogic.parseRetryAfterSeconds("   "))
    XCTAssertNil(RangeDownloadLogic.parseRetryAfterSeconds("-3"))            // negative rejected
    XCTAssertNil(RangeDownloadLogic.parseRetryAfterSeconds("soon"))         // non-numeric
    // HTTP-date form is treated as absent (delta-seconds only).
    XCTAssertNil(RangeDownloadLogic.parseRetryAfterSeconds("Wed, 21 Oct 2015 07:28:00 GMT"))
  }

  func testBackoffDelay_retryAfterOverrides() {
    // Retry-After wins, but is clamped to retryMaxDelaySeconds * 2 (= 60).
    XCTAssertEqual(RangeDownloadLogic.backoffDelay(attempt: 1, retryAfter: 5), 5)
    XCTAssertEqual(RangeDownloadLogic.backoffDelay(attempt: 99, retryAfter: 7), 7)
    XCTAssertEqual(RangeDownloadLogic.backoffDelay(attempt: 1, retryAfter: 1000),
                   RangeDownloadLogic.retryMaxDelaySeconds * 2)
  }

  func testBackoffDelay_expGrowthWithinJitteredCap() {
    // Without Retry-After: full jitter in [0, min(base*2^(n-1), max)].
    // base=1, max=30. attempt 1 → cap 1, attempt 2 → cap 2, attempt 4 → cap 8,
    // attempt 6 → cap 32 clamped to 30. Probe each many times; every sample must
    // land in [0, cap].
    let cases: [(attempt: Int, cap: Double)] = [
      (1, 1), (2, 2), (3, 4), (4, 8), (5, 16), (6, 30), (10, 30),
    ]
    for c in cases {
      for _ in 0..<200 {
        let d = RangeDownloadLogic.backoffDelay(attempt: c.attempt, retryAfter: nil)
        XCTAssertGreaterThanOrEqual(d, 0, "attempt \(c.attempt)")
        XCTAssertLessThanOrEqual(d, c.cap, "attempt \(c.attempt) exceeded cap \(c.cap): \(d)")
      }
    }
  }

  func testBackoffDelay_capNeverExceedsMax() {
    // Very large attempt must still respect the absolute cap (30).
    for _ in 0..<500 {
      let d = RangeDownloadLogic.backoffDelay(attempt: 100, retryAfter: nil)
      XCTAssertLessThanOrEqual(d, RangeDownloadLogic.retryMaxDelaySeconds)
      XCTAssertGreaterThanOrEqual(d, 0)
    }
  }

  // MARK: §5.5 — integrity backstop (SHA-256 known vectors)

  func testCalculateSHA256_knownVectors() throws {
    let dir = FileManager.default.temporaryDirectory
      .appendingPathComponent("rdlogic-sha-\(UUID().uuidString)", isDirectory: true)
    try FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)
    defer { try? FileManager.default.removeItem(at: dir) }

    let vectors: [(content: String, hex: String)] = [
      ("hello", "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"),
      ("", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"),
      ("The quick brown fox jumps over the lazy dog",
       "d7a8fbb307d7809469ca9abcb0082e4f8d5651e46d3cdb762d02d0bf37c9e592"),
    ]
    for (i, v) in vectors.enumerated() {
      let file = dir.appendingPathComponent("vec\(i).bin")
      try Data(v.content.utf8).write(to: file)
      XCTAssertEqual(RangeDownloadLogic.calculateSHA256(file.path), v.hex, "vector \(i)")
    }
  }

  func testCalculateSHA256_largeMultiChunk() throws {
    // Larger than the 8192-byte read window to exercise the streaming loop.
    let dir = FileManager.default.temporaryDirectory
      .appendingPathComponent("rdlogic-sha-big-\(UUID().uuidString)", isDirectory: true)
    try FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)
    defer { try? FileManager.default.removeItem(at: dir) }
    let file = dir.appendingPathComponent("big.bin")
    let payload = Data(repeating: 0x61 /* 'a' */, count: 100_000)
    try payload.write(to: file)
    // Reference hash computed by hashing the same payload via a second path.
    let expected = sha256Hex(payload)
    XCTAssertEqual(RangeDownloadLogic.calculateSHA256(file.path), expected)
  }

  func testCalculateSHA256_missingFile_nil() {
    let path = FileManager.default.temporaryDirectory
      .appendingPathComponent("does-not-exist-\(UUID().uuidString).bin").path
    XCTAssertNil(RangeDownloadLogic.calculateSHA256(path))
  }
}

// MARK: - Local SHA helper (independent reference, not the unit under test)

import CommonCrypto

private func sha256Hex(_ data: Data) -> String {
  var hash = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
  data.withUnsafeBytes { _ = CC_SHA256($0.baseAddress, CC_LONG(data.count), &hash) }
  return hash.map { String(format: "%02x", $0) }.joined()
}
