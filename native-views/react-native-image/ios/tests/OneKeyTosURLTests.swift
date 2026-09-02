import XCTest

@testable import OneKeyImage

final class OneKeyTosURLTests: XCTestCase {
  func testOptimizesAllowedOneKeyAssetURL() throws {
    let raw = try XCTUnwrap(URL(string: "https://common.onekey-asset.com/tokens/btc.png"))
    let result = OneKeyTosURL.optimized(
      rawURL: raw,
      displaySize: 100,
      scale: 2,
      overscan: 1.1,
      hasCustomIdentity: false
    )
    XCTAssertTrue(result.absoluteString.contains("x-tos-process="))
    XCTAssertTrue(result.absoluteString.contains("w_256"))
  }

  func testDoesNotRewriteSignedOrCustomIdentityURL() throws {
    let signed = try XCTUnwrap(
      URL(string: "https://common.onekey-asset.com/avatar.png?X-Tos-Signature=secret")
    )
    XCTAssertEqual(
      OneKeyTosURL.optimized(
        rawURL: signed,
        displaySize: 100,
        scale: 2,
        overscan: 1.1,
        hasCustomIdentity: false
      ),
      signed
    )
    let raw = try XCTUnwrap(URL(string: "https://common.onekey-asset.com/avatar.png"))
    XCTAssertEqual(
      OneKeyTosURL.optimized(
        rawURL: raw,
        displaySize: 100,
        scale: 2,
        overscan: 1.1,
        hasCustomIdentity: true
      ),
      raw
    )
  }

  func testDoesNotRewriteUntrustedHostOrUnsupportedMedia() throws {
    for value in [
      "https://example.com/avatar.png",
      "https://web.onekey-asset.com/avatar.png",
      "https://common.onekey-asset.com/icon.svg",
      "https://common.onekey-asset.com/video.mp4",
      "https://common.onekey-asset.com/video.webm",
      "https://common.onekey-asset.com/video.m4v",
      "https://common.onekey-asset.com/video.mov",
      "https://common.onekey-asset.com/video.avi",
    ] {
      let raw = try XCTUnwrap(URL(string: value))
      XCTAssertEqual(
        OneKeyTosURL.optimized(
          rawURL: raw,
          displaySize: 100,
          scale: 2,
          overscan: 1.1,
          hasCustomIdentity: false
        ),
        raw
      )
    }
  }

  func testUsesApprovedHostsBucketsAndCapsDPR() throws {
    for host in [
      "app-assets.onekey.so",
      "uni.onekey-asset.com",
      "uni-test.onekey-asset.com",
      "common.onekey-asset.com",
      "asset.onekey-asset.com",
    ] {
      let raw = try XCTUnwrap(URL(string: "https://\(host)/token.png"))
      let result = OneKeyTosURL.optimized(
        rawURL: raw,
        displaySize: 100,
        scale: 5,
        overscan: 1.1,
        hasCustomIdentity: false
      )
      XCTAssertTrue(result.absoluteString.contains("w_480"))
    }
  }

  func testNormalizesNonFiniteSizingInputsWithoutCrashing() throws {
    let raw = try XCTUnwrap(URL(string: "https://common.onekey-asset.com/token.png"))
    let result = OneKeyTosURL.optimized(
      rawURL: raw,
      displaySize: 100,
      scale: .nan,
      overscan: .infinity,
      hasCustomIdentity: false
    )
    XCTAssertTrue(result.absoluteString.contains("w_128"))
    XCTAssertEqual(
      OneKeyTosURL.optimized(
        rawURL: raw,
        displaySize: .infinity,
        scale: 2,
        overscan: 1.1,
        hasCustomIdentity: false
      ),
      raw
    )
    let huge = OneKeyTosURL.optimized(
      rawURL: raw,
      displaySize: .greatestFiniteMagnitude,
      scale: 3,
      overscan: 1.1,
      hasCustomIdentity: false
    )
    XCTAssertTrue(huge.absoluteString.contains("w_1280"))
  }

  func testSkipsEveryProtectedQueryFamily() throws {
    for key in [
      "expires", "policy", "signature", "token", "auth_key", "accesskeyid",
      "ossaccesskeyid", "security-token", "x-amz-signature", "x-oss-signature",
      "x-tos-process",
    ] {
      let raw = try XCTUnwrap(
        URL(string: "https://common.onekey-asset.com/token.png?\(key)=value")
      )
      XCTAssertEqual(
        OneKeyTosURL.optimized(
          rawURL: raw,
          displaySize: 100,
          scale: 2,
          overscan: 1.1,
          hasCustomIdentity: false
        ),
        raw
      )
    }
  }
}
