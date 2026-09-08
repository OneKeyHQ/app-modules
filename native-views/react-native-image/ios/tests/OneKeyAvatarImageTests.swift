// OneKey patch: Protect request-local avatar routing and exact UTF16 identities.
import Foundation
import SDWebImage
import XCTest

@testable import OneKeyImage

final class OneKeyAvatarImageTests: XCTestCase {
  func testAvatarCacheIsSharedWithoutReplacingIsolatedHTTPManagers() throws {
    let url = try XCTUnwrap(URL(string: "onekey-avatar://blockie/v1/synthetic-avatar"))
    let first = OneKeyImagePipeline.makeIsolatedManager()
    let second = OneKeyImagePipeline.makeIsolatedManager()
    func context(_ manager: SDWebImageManager, headers: String) -> [SDWebImageContextOption: Any] {
      OneKeyImageRequestContext.make(headersJson: headers, cachePolicy: .memoryDisk,
        thumbnailPixelSize: CGSize(width: 96, height: 96), safetyTracker: nil,
        manager: manager, url: url)
    }
    let a = context(first, headers: "{\"X-Test\":\"a\"}")
    let b = context(second, headers: "{\"X-Test\":\"b\"}")
    XCTAssertTrue(a[.customManager] as? SDWebImageManager === first)
    XCTAssertTrue(b[.customManager] as? SDWebImageManager === second)
    XCTAssertTrue(a[.imageLoader] as? OneKeyAvatarImageLoader === OneKeyAvatarImageLoader.shared)
    XCTAssertTrue(b[.imageLoader] as? OneKeyAvatarImageLoader === OneKeyAvatarImageLoader.shared)
    XCTAssertTrue(a[.imageCache] as? SDImageCache === b[.imageCache] as? SDImageCache)
    XCTAssertTrue(a[.originalImageCache] as? SDImageCache === OneKeyAvatarImageLoader.cache)
    let firstFilter = try XCTUnwrap(a[.cacheKeyFilter] as? SDWebImageCacheKeyFilter)
    let secondFilter = try XCTUnwrap(b[.cacheKeyFilter] as? SDWebImageCacheKeyFilter)
    XCTAssertEqual(firstFilter.cacheKey(for: url), secondFilter.cacheKey(for: url))
    let remote = OneKeyImageRequestContext.make(headersJson: nil, cachePolicy: .memoryDisk,
      thumbnailPixelSize: nil, safetyTracker: nil, manager: first,
      url: URL(string: "https://example.com/image.png"))
    XCTAssertNil(remote[.imageLoader])
    XCTAssertNil(remote[.imageCache])
    XCTAssertNil(remote[.originalImageCache])
  }

  func testURIDecodesExactlyOnceAndPreservesUTF16SeedIdentity() throws {
    func descriptor(_ suffix: String) throws -> OneKeyBlockieDescriptor {
      try XCTUnwrap(OneKeyBlockieDescriptor(url:
        XCTUnwrap(URL(string: "onekey-avatar://blockie/v1/" + suffix))))
    }
    XCTAssertEqual(try descriptor("%2561").seed, "%61")
    XCTAssertEqual(try descriptor("%61").cacheKey, try descriptor("a").cacheKey)
    XCTAssertNotEqual(try descriptor("%C3%A9").cacheKey, try descriptor("e%CC%81").cacheKey)
    XCTAssertEqual(try descriptor("%C4%B0").seed.utf16.count, 1)
    XCTAssertNotEqual(OneKeyBlockie.rgba(seed: "é"), OneKeyBlockie.rgba(seed: "e\u{301}"))
    XCTAssertNil(OneKeyBlockie.png(seed: "synthetic", isCancelled: { true }))
  }
}
