import UIKit
import XCTest
@testable import NativeListModule

@MainActor
final class NativeListMediaVisibilityTests: XCTestCase {
  func testRetainedPagerAndListCellsRequireViewportIntersection() {
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 480))
    let controller = UIViewController()
    window.rootViewController = controller
    window.isHidden = false
    defer { window.isHidden = true }
    let pager = UIScrollView(frame: CGRect(x: 0, y: 0, width: 320, height: 480))
    pager.contentSize = CGSize(width: 640, height: 480)
    controller.view.addSubview(pager)
    let list = UIScrollView(frame: CGRect(x: 320, y: 0, width: 320, height: 480))
    list.contentSize = CGSize(width: 320, height: 960)
    pager.addSubview(list)
    let tile = UIView(frame: CGRect(x: 10, y: 10, width: 100, height: 100))
    list.addSubview(tile)

    XCTAssertNotNil(tile.window, "Pager retains the offscreen cell in the window")
    XCTAssertFalse(NativeListMediaPreviewSlot.intersectsViewport(tile))
    pager.contentOffset.x = 320
    XCTAssertTrue(NativeListMediaPreviewSlot.intersectsViewport(tile))
    list.contentOffset.y = 110
    XCTAssertFalse(NativeListMediaPreviewSlot.intersectsViewport(tile))
    list.contentOffset.y = 100
    XCTAssertTrue(NativeListMediaPreviewSlot.intersectsViewport(tile), "Partially visible previews remain eligible")
    list.transform = CGAffineTransform(translationX: 320, y: 0)
    XCTAssertFalse(NativeListMediaPreviewSlot.intersectsViewport(tile))
    list.transform = .identity
    XCTAssertTrue(NativeListMediaPreviewSlot.intersectsViewport(tile))
    list.isHidden = true
    XCTAssertFalse(NativeListMediaPreviewSlot.intersectsViewport(tile))
    list.isHidden = false
    list.alpha = 0
    XCTAssertFalse(NativeListMediaPreviewSlot.intersectsViewport(tile))
    tile.removeFromSuperview()
    XCTAssertFalse(NativeListMediaPreviewSlot.intersectsViewport(tile))
  }
}

@MainActor
final class NativeListMediaFrameCaptureTests: XCTestCase {
  func testVisibleVideoReleasesPlayerAfterCapturingFirstFrame() throws {
    let url = try writeVideo()
    defer { try? FileManager.default.removeItem(at: url) }
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 480))
    window.rootViewController = UIViewController()
    window.isHidden = false
    defer { window.isHidden = true }
    let slot = NativeListMediaPreviewSlot()
    slot.view.frame = CGRect(x: 0, y: 0, width: 120, height: 120)
    window.rootViewController?.view.addSubview(slot.view)
    slot.view.layoutIfNeeded()

    var loaded: Bool?
    slot.bind(["uri": url.absoluteString], key: "video", probeOrder: ["video"]) { loaded = $0 }
    XCTAssertTrue(slot.hasLivePlayer, "A visible tile starts one paused player")
    let deadline = Date().addingTimeInterval(10)
    while slot.capturedFrame == nil, Date() < deadline {
      RunLoop.main.run(until: Date().addingTimeInterval(0.05))
    }
    XCTAssertEqual(loaded, true)
    let frame = try XCTUnwrap(slot.capturedFrame, "The first frame replaces the live player")
    XCTAssertEqual(frame.size, CGSize(width: 64, height: 48))
    XCTAssertFalse(slot.hasLivePlayer)

    // Leaving and re-entering the viewport keeps the still without rebuilding a player.
    slot.view.removeFromSuperview()
    window.rootViewController?.view.addSubview(slot.view)
    slot.bind(["uri": url.absoluteString], key: "video", probeOrder: ["video"])
    XCTAssertFalse(slot.hasLivePlayer)
    XCTAssertNotNil(slot.capturedFrame)
    slot.recycle()
    XCTAssertNil(slot.capturedFrame)
  }

  /// A 64×48, ten-frame H.264 MP4 (generated with AVAssetWriter on macOS); the simulator
  /// has no reliable H.264 encoder for writing one at test time.
  private static let videoBase64 = [
    "AAAAHGZ0eXBtcDQyAAAAAWlzb21tcDQxbXA0MgAAAAFtZGF0AAAAAAAAAXQAAAA1BgUtR1ZK3FxMQz+U78URPNFDqAEAAAMA",
    "AQMAAAMAAQIAAeYACwAAAwAAAwAArDoMA5ErAYAAAAA8JbggH94I5Uz/gak9b71ouT+PwABUezojLMLPpBfbqJ15oD1BM5L2",
    "vvLSFvvy+AABSCrnn2hX8nKgIiU0AAAAHSHhEF/Eku94/c0JBdrVAqcS2Y43GYAJD/p9QzWAAAAAGSGogoS/nFT7cZbZ8xnp",
    "PB3mw6AB/xojtyEAAAAXAajBj/+nyteRkHQKsy3f1gAFJajGDXAAAAAVAajDj/9TDF3o0LWPJnRoAJ7STV8lAAAAGSHjIaIl",
    "/wHKBDW4y227MDl54AH+VxDvqGgAAAAUIakGhL9JSwD9lXLr+gAf8c7Z0iAAAAATAalFiT9cnuAKgJxY0AGV0YL85AAAABEB",
    "qUeJP1ye5zMOKAAol0Uv0AAAABQh5SWiJf8BygQQ1yj2ACX52k6mwAAAA0xtb292AAAAbG12aGQAAAAA5uAsIObgLCAAAAJY",
    "AAACWAABAAABAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAA",
    "AAAAAAAAAAAAAAACAAAC2HRyYWsAAABcdGtoZAAAAAHm4Cwg5uAsIAAAAAEAAAAAAAACWAAAAAAAAAAAAAAAAAAAAAAAAQAA",
    "AAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAEAAAAAAQAAAADAAAAAAACRlZHRzAAAAHGVsc3QAAAAAAAAAAQAAAlgAAAB4",
    "AAEAAAAAAlBtZGlhAAAAIG1kaGQAAAAA5uAsIObgLCAAAAJYAAACWFXEAAAAAAAxaGRscgAAAAAAAAAAdmlkZQAAAAAAAAAA",
    "AAAAAENvcmUgTWVkaWEgVmlkZW8AAAAB921pbmYAAAAUdm1oZAAAAAEAAAAAAAAAAAAAACRkaW5mAAAAHGRyZWYAAAAAAAAA",
    "AQAAAAx1cmwgAAAAAQAAAbdzdGJsAAAAoXN0c2QAAAAAAAAAAQAAAJFhdmMxAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAEAA",
    "MABIAAAASAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGP//AAAAJ2F2Y0MBZAAL/+EADCdkAAus",
    "VlDDeBBhlAEABCjuPLD9+PgAAAAACmZpZWwBAAAAAApjaHJtAAAAAAAYc3R0cwAAAAAAAAABAAAACgAAADwAAABgY3R0cwAA",
    "AAAAAAAKAAAAAQAAAHgAAAABAAABLAAAAAEAAAB4AAAAAQAAAAAAAAABAAAAPAAAAAEAAAEsAAAAAQAAAHgAAAABAAAAAAAA",
    "AAEAAAA8AAAAAQAAAHgAAAAUc3RzcwAAAAAAAAABAAAAAQAAABZzZHRwAAAAACAQEBgYEBAYGBAAAAAcc3RzYwAAAAAAAAAB",
    "AAAAAQAAAAoAAAABAAAAPHN0c3oAAAAAAAAAAAAAAAoAAAB5AAAAIQAAAB0AAAAbAAAAGQAAAB0AAAAYAAAAFwAAABUAAAAY",
    "AAAAFHN0Y28AAAAAAAAAAQAAACw=",
  ].joined()

  private func writeVideo() throws -> URL {
    let url = FileManager.default.temporaryDirectory
      .appendingPathComponent("native-list-frame-\(UUID().uuidString).mp4")
    try XCTUnwrap(Data(base64Encoded: Self.videoBase64)).write(to: url)
    return url
  }
}
