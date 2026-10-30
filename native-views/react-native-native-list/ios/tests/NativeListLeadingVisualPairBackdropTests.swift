import UIKit
import XCTest
@testable import NativeListModule

@MainActor
final class NativeListLeadingVisualPairBackdropTests: XCTestCase {
  private func child<T>(_ visual: NativeListLeadingVisual, _ label: String, as type: T.Type) throws -> T {
    try XCTUnwrap(Mirror(reflecting: visual).children.first { $0.label == label }?.value as? T)
  }

  private func token(_ uri: String) -> [String: Any] {
    [
      "kind": "token", "shape": "circle",
      "image": ["uri": uri, "width": 40, "height": 40],
      "fallbackIcon": ["name": "CryptoCoinOutline"],
    ]
  }

  // PR #185 review: the pair ring sits above the primary token. While the
  // secondary image has not loaded (or failed), its slot is hidden, so the ring
  // must be hidden too; otherwise a blank row-background disc covers the
  // primary's lower-right corner.
  func testPairRingStaysHiddenWhileTheSecondaryImageIsNotDisplayed() throws {
    let visual = NativeListLeadingVisual(frame: CGRect(x: 0, y: 0, width: 40, height: 40))
    let ring = try child(visual, "pairBackdrop", as: UIView.self)
    visual.bind(
      token("https://example.invalid/primary.png"), style: [:], key: "pair", theme: nil,
      isUnread: false, secondaryVisual: token("https://example.invalid/secondary.png"),
      fonts: NativeListFontFamilies())
    visual.layoutIfNeeded()
    // `slots` is a value-type array: read it after bind has created the slots.
    let slots = try child(visual, "slots", as: [NativeListImageSlot].self)
    XCTAssertEqual(slots.count, 2)
    XCTAssertTrue(slots[1].view.isHidden, "secondary slot is hidden until its image loads")
    XCTAssertTrue(ring.isHidden, "ring must follow the hidden secondary slot")

    visual.recycle()
    XCTAssertTrue(ring.isHidden)
  }

  func testNoPairMeansNoRing() throws {
    let visual = NativeListLeadingVisual(frame: CGRect(x: 0, y: 0, width: 40, height: 40))
    let ring = try child(visual, "pairBackdrop", as: UIView.self)
    visual.bind(
      token("https://example.invalid/primary.png"), style: [:], key: "single", theme: nil,
      isUnread: false, fonts: NativeListFontFamilies())
    visual.layoutIfNeeded()
    XCTAssertTrue(ring.isHidden)
  }
}
