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
