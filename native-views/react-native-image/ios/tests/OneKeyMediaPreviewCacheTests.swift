import UIKit
import SDWebImage
import XCTest
@testable import OneKeyImage

@MainActor
final class OneKeyMediaPreviewCacheTests: XCTestCase {
  private func waitFor(_ condition: () -> Bool) {
    let deadline = Date().addingTimeInterval(3)
    while !condition(), Date() < deadline { RunLoop.main.run(until: Date().addingTimeInterval(0.01)) }
    XCTAssertTrue(condition())
  }
  private func still() -> UIImage {
    let format = UIGraphicsImageRendererFormat(); format.scale = 1
    return UIGraphicsImageRenderer(size: CGSize(width: 32, height: 16), format: format).image { context in
      UIColor.red.setFill(); context.fill(CGRect(x: 0, y: 0, width: 32, height: 16))
    }
  }
  private func request(_ cache: OneKeyMediaPreviewCache, uri: String = "test://video", policy: String = "memory-disk",
    image: @escaping (UIImage) -> Void = { _ in }, extract: @escaping (UUID) -> Void = { _ in },
    revoked: @escaping () -> Void = {}) -> OneKeyMediaPreviewCache.Subscription {
    cache.request(uri: uri, headers: [:], edge: 128, fit: "contain", policy: policy,
      onImage: image, onExtract: extract, onRevoked: revoked, onFailure: { XCTFail("Unexpected failure") })
  }
  func testColdDeduplicationMemoryAndDiskReuse() {
    let namespace = "media-preview-test-\(UUID().uuidString)"
    let cache = OneKeyMediaPreviewCache(namespace: namespace)
    var extracts = 0, delivered = 0
    _ = request(cache, image: { _ in delivered += 1 }, extract: { lease in
      extracts += 1; cache.complete(lease, image: self.still())
    })
    _ = request(cache, image: { _ in delivered += 1 }, extract: { _ in extracts += 1 })
    waitFor { delivered == 2 }; XCTAssertEqual(extracts, 1)
    _ = request(cache, image: { _ in delivered += 1 }, extract: { _ in XCTFail("Memory miss") })
    waitFor { delivered == 3 }
    let diskProbe = SDImageCache(namespace: namespace)
    let key = OneKeyMediaPreviewCache.storageKey(uri: "test://video", headers: [:], edge: 128, fit: "contain")
    let path = diskProbe.cachePath(forKey: key)!
    waitFor { FileManager.default.fileExists(atPath: path) }
    let fresh = OneKeyMediaPreviewCache(namespace: namespace)
    _ = request(fresh, image: { image in
      XCTAssertEqual(image.size.width / image.size.height, 2); delivered += 1
    }, extract: { _ in XCTFail("Disk miss after replacing service") })
    waitFor { delivered == 4 }
    var cleared = false; fresh.clearDisk { cleared = true }; waitFor { cleared }
  }
  func testExpiredDiskAccessIsRemovedAndRegeneratedBeforeReuse() throws {
    let namespace = "expired-access-\(UUID().uuidString)", uri = "test://expired-access"
    let seed = SDImageCache(namespace: namespace)
    let key = OneKeyMediaPreviewCache.storageKey(uri: uri, headers: [:], edge: 128, fit: "contain")
    seed.storeImageData(toDisk: still().pngData()!, forKey: key)
    let path = try XCTUnwrap(seed.cachePath(forKey: key))
    try FileManager.default.setAttributes([.modificationDate: Date().addingTimeInterval(-86400)], ofItemAtPath: path)
    var url = URL(fileURLWithPath: path), dates = URLResourceValues()
    dates.contentAccessDate = Date().addingTimeInterval(-31 * 86400)
    try url.setResourceValues(dates)
    let seededAccess = try XCTUnwrap(url.resourceValues(forKeys: [.contentAccessDateKey]).contentAccessDate)
    XCTAssertLessThan(seededAccess, Date().addingTimeInterval(-30 * 86400))
    let cache = OneKeyMediaPreviewCache(namespace: namespace)
    var lease: UUID?, oldImages = 0, extracts = 0
    _ = request(cache, uri: uri, policy: "disk", image: { _ in oldImages += 1 }, extract: { extracts += 1; lease = $0 })
    waitFor { lease != nil || oldImages != 0 }
    XCTAssertEqual(oldImages, 0)
    XCTAssertEqual(extracts, 1)
    guard let lease else { XCTFail("Expired entry bypassed extraction"); return }
    XCTAssertFalse(FileManager.default.fileExists(atPath: path))
    cache.complete(lease, image: still())
    waitFor { FileManager.default.fileExists(atPath: path) }
    let fresh = OneKeyMediaPreviewCache(namespace: namespace)
    var loaded = false
    _ = request(fresh, uri: uri, policy: "disk", image: { _ in loaded = true }, extract: { _ in XCTFail("Regenerated disk miss") })
    waitFor { loaded }
    var cleared = false; fresh.clearDisk { cleared = true }; waitFor { cleared }
  }

  func testRecentDiskAccessIgnoresOldModificationAndRefreshesAccessDate() throws {
    let namespace = "recent-access-\(UUID().uuidString)", uri = "test://recent-access"
    let seed = SDImageCache(namespace: namespace)
    let key = OneKeyMediaPreviewCache.storageKey(uri: uri, headers: [:], edge: 128, fit: "contain")
    seed.storeImageData(toDisk: still().pngData()!, forKey: key)
    let path = try XCTUnwrap(seed.cachePath(forKey: key))
    try FileManager.default.setAttributes([.modificationDate: Date().addingTimeInterval(-31 * 86400)], ofItemAtPath: path)
    var url = URL(fileURLWithPath: path), dates = URLResourceValues()
    dates.contentAccessDate = Date().addingTimeInterval(-86400)
    try url.setResourceValues(dates)
    let started = Date()
    let cache = OneKeyMediaPreviewCache(namespace: namespace)
    var loaded = false
    _ = request(cache, uri: uri, policy: "disk", image: { _ in loaded = true }, extract: { _ in XCTFail("Recent access disk miss") })
    waitFor { loaded }
    let touched = try XCTUnwrap(URL(fileURLWithPath: path).resourceValues(forKeys: [.contentAccessDateKey]).contentAccessDate)
    XCTAssertGreaterThanOrEqual(touched, started.addingTimeInterval(-1))
    var cleared = false; cache.clearDisk { cleared = true }; waitFor { cleared }
  }

  func testHeaderIdentityAndOwnerTransferRejectLateCompletion() {
    XCTAssertEqual(OneKeyMediaPreviewCache.storageKey(uri: "u", headers: ["A": "1", "B": "2"], edge: 128, fit: "contain"),
      OneKeyMediaPreviewCache.storageKey(uri: "u", headers: ["b": "2", "a": "1"], edge: 128, fit: "contain"))
    XCTAssertNotEqual(OneKeyMediaPreviewCache.storageKey(uri: "u", headers: ["A": "1"], edge: 128, fit: "contain"),
      OneKeyMediaPreviewCache.storageKey(uri: "u", headers: ["A": "2"], edge: 128, fit: "contain"))
    let cache = OneKeyMediaPreviewCache(namespace: "transfer-\(UUID().uuidString)")
    var old: UUID?, next: UUID?, deliveries = 0, revoked = 0
    let a = request(cache, policy: "none", image: { _ in XCTFail("Cancelled owner") }, extract: { old = $0 }, revoked: { revoked += 1 })
    _ = request(cache, policy: "none", image: { _ in deliveries += 1 }, extract: { next = $0 })
    waitFor { old != nil }; cache.cancel(a); waitFor { next != nil }; XCTAssertEqual(revoked, 1)
    cache.complete(old!, image: still()); XCTAssertEqual(deliveries, 0)
    cache.complete(next!, image: still()); waitFor { deliveries == 1 }
  }
  func testTwoPermitsFIFOAndClearBlocksOldFill() {
    let cache = OneKeyMediaPreviewCache(namespace: "bounds-\(UUID().uuidString)")
    var leases: [UUID] = [], finished = 0
    for id in 0..<3 {
      _ = request(cache, uri: "test://\(id)", image: { _ in finished += 1 }, extract: { leases.append($0) })
    }
    waitFor { leases.count == 2 }; XCTAssertEqual(leases.count, 2)
    cache.clearMemory()
    var cleared = false; cache.clearDisk { cleared = true }; waitFor { cleared }
    cache.complete(leases[0], image: still()); waitFor { leases.count == 3 }
    cache.complete(leases[1], image: still()); cache.complete(leases[2], image: still()); waitFor { finished == 3 }
    var missed = false
    let probe = request(cache, uri: "test://0", extract: { _ in missed = true })
    waitFor { missed }; cache.cancel(probe)
  }
  func testCancelledCapacityRejectionDoesNotDeliverFailure() {
    let cache = OneKeyMediaPreviewCache(namespace: "capacity-\(UUID().uuidString)")
    var subscriptions: [OneKeyMediaPreviewCache.Subscription] = []
    for id in 0..<130 {
      subscriptions.append(request(cache, uri: "test://capacity/\(id)", policy: "none"))
    }
    var failed = false
    let rejected = cache.request(uri: "test://rejected", headers: [:], edge: 128, fit: "contain", policy: "none",
      onImage: { _ in XCTFail() }, onExtract: { _ in XCTFail() }, onRevoked: {}, onFailure: { failed = true })
    cache.cancel(rejected)
    subscriptions.forEach { cache.cancel($0) }
    RunLoop.main.run(until: Date().addingTimeInterval(0.05))
    XCTAssertFalse(failed)
  }

}
