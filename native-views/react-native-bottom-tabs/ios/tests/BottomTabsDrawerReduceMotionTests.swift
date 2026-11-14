import UIKit
import XCTest
@testable import BottomTabsModule

/// Reduce Motion cross-fades: when a faded transition reports, and what takes
/// it over. Runs without an app host.
@MainActor
final class BottomTabsDrawerReduceMotionTests: XCTestCase {
  func testUserCloseReportsOnceWhenTheFadeEnds() throws {
    let fixture = Fixture(open: true)
    defer { fixture.close() }

    XCTAssertTrue(fixture.drawer.accessibilityPerformEscape())
    try fixture.requireFade()
    XCTAssertEqual(fixture.progress, 0, "the final state shows under the snapshot")
    XCTAssertEqual(fixture.events, [], "nothing is reported while the fade runs")
    XCTAssertEqual(fixture.stableOpen, true)

    XCTAssertTrue(fixture.waitUntil { !fixture.isFading })
    XCTAssertEqual(fixture.events, [false])
    XCTAssertEqual(fixture.fadingAtEvent, [false], "the report waits for the snapshot to go")
    XCTAssertEqual(fixture.stableOpen, false)
  }

  func testDragTakingOverTheFadeDropsItsStaleClose() throws {
    let fixture = Fixture(open: true)
    defer { fixture.close() }
    XCTAssertTrue(fixture.drawer.accessibilityPerformEscape())
    try fixture.requireFade()

    fixture.pan(.began)
    XCTAssertFalse(fixture.isFading)
    // A close reported here would echo back as `open = false` and cancel the drag.
    XCTAssertEqual(fixture.events, [])
    fixture.pan(.changed, dx: fixture.menuWidth * 0.9)
    XCTAssertEqual(fixture.progress, 0.9, accuracy: 0.001)
    fixture.pan(.ended)
    XCTAssertTrue(fixture.waitUntil { !fixture.isAnimating })

    XCTAssertEqual(fixture.progress, 1)
    XCTAssertEqual(fixture.stableOpen, true)
    XCTAssertEqual(fixture.events, [], "reopening lands on the state JS already has")
    fixture.wait(0.3)
    XCTAssertEqual(fixture.events, [])
  }

  func testDragTakingOverTheFadeReportsItsOwnClose() throws {
    let fixture = Fixture(open: true)
    defer { fixture.close() }
    XCTAssertTrue(fixture.drawer.accessibilityPerformEscape())
    try fixture.requireFade()

    fixture.pan(.began)
    fixture.pan(.changed, dx: fixture.menuWidth * 0.2)
    fixture.pan(.ended)
    XCTAssertTrue(fixture.waitUntil { !fixture.isAnimating })

    XCTAssertEqual(fixture.progress, 0)
    XCTAssertEqual(fixture.events, [false])
    fixture.wait(0.3)
    XCTAssertEqual(fixture.events, [false])
  }

  func testResizeLandsTheFadeAtOnce() throws {
    let fixture = Fixture(open: true)
    defer { fixture.close() }
    XCTAssertTrue(fixture.drawer.accessibilityPerformEscape())
    try fixture.requireFade()

    fixture.drawer.frame.size.width = 420
    fixture.drawer.layoutIfNeeded()
    XCTAssertFalse(fixture.isFading)
    XCTAssertEqual(fixture.events, [false])
    XCTAssertEqual(fixture.stableOpen, false)
    fixture.wait(0.3)
    XCTAssertEqual(fixture.events, [false], "the replaced fade does not report again")
  }

  func testNewerRequestsTakeOverTheFadeAndItsCompletion() throws {
    let fixture = Fixture(open: true)
    defer { fixture.close() }
    XCTAssertTrue(fixture.drawer.accessibilityPerformEscape())
    try fixture.requireFade()

    // JS closes too: same target, so the landing is no longer a user event.
    fixture.drawer.open = false
    // JS reopens: a newer fade replaces the first, keeping one snapshot.
    fixture.drawer.open = true
    try fixture.requireFade()
    XCTAssertEqual(fixture.drawer.subviews.count, 3)
    XCTAssertEqual(fixture.progress, 1)

    XCTAssertTrue(fixture.waitUntil { !fixture.isFading })
    fixture.wait(0.3)
    XCTAssertEqual(fixture.events, [])
    XCTAssertEqual(fixture.stableOpen, true)
    XCTAssertEqual(fixture.drawer.subviews.count, 2)
  }

  func testDisablingOrDetachingDropsThePendingClose() throws {
    let disabled = Fixture(open: true)
    defer { disabled.close() }
    XCTAssertTrue(disabled.drawer.accessibilityPerformEscape())
    try disabled.requireFade()
    disabled.drawer.enabled = false
    XCTAssertFalse(disabled.isFading)
    disabled.wait(0.3)
    XCTAssertEqual(disabled.events, [])

    let detached = Fixture(open: true)
    defer { detached.close() }
    XCTAssertTrue(detached.drawer.accessibilityPerformEscape())
    try detached.requireFade()
    detached.drawer.removeFromSuperview()
    XCTAssertFalse(detached.isFading)
    // Like an interrupted settle, it returns to the last state JS was told.
    XCTAssertEqual(detached.progress, 1)
    detached.wait(0.3)
    XCTAssertEqual(detached.events, [])
    detached.window.addSubview(detached.drawer)
    XCTAssertEqual(detached.progress, 1)
    XCTAssertEqual(detached.events, [])
  }
}

@MainActor
private final class Fixture {
  let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 390, height: 844))
  let drawer = RCTBottomTabsDrawerContainerView(frame: CGRect(x: 0, y: 0, width: 390, height: 844))
  private(set) var events: [Bool] = []
  private(set) var fadingAtEvent: [Bool] = []
  private let recognizer = FakePan(target: nil, action: nil)

  /// An enabled drawer that already rests at `open`, as the prop echo leaves it.
  init(open: Bool) {
    drawer.isReduceMotionEnabled = { true }
    drawer.makeFadeSnapshot = { _ in UIView() }
    drawer.insertChild(UIView(), atIndex: 0)
    drawer.insertChild(UIView(), atIndex: 1)
    drawer.swipeEnabled = true
    drawer.enabled = true
    // Outside a window, the drawer lands on a target without animating.
    drawer.open = open
    window.addSubview(drawer)
    window.isHidden = false
    drawer.layoutIfNeeded()
    drawer.onDrawerOpenChange = { [unowned self] open in
      self.events.append(open)
      self.fadingAtEvent.append(self.isFading)
    }
  }

  var menuWidth: CGFloat { drawer.bounds.width * 0.8 }
  var progress: CGFloat { value("progress") ?? -1 }
  var stableOpen: Bool? { value("stableOpen") }
  var isFading: Bool { value("fadeSnapshot") as UIView? != nil }
  var isAnimating: Bool { value("displayLink") as CADisplayLink? != nil }

  func requireFade(file: StaticString = #filePath, line: UInt = #line) throws {
    _ = try XCTUnwrap(value("fadeSnapshot") as UIView?, "Reduce Motion should cross-fade",
                      file: file, line: line)
  }

  /// Delivers one recognizer callback, as UIKit does for the drawer's pan.
  func pan(_ state: UIGestureRecognizer.State, dx: CGFloat = 0) {
    recognizer.fakeState = state
    recognizer.fakeTranslation = CGPoint(x: dx, y: 0)
    drawer.perform(NSSelectorFromString("handlePan:"), with: recognizer)
  }

  func wait(_ seconds: TimeInterval) {
    RunLoop.main.run(until: Date().addingTimeInterval(seconds))
  }

  func waitUntil(timeout: TimeInterval = 2, _ condition: () -> Bool) -> Bool {
    let deadline = Date().addingTimeInterval(timeout)
    while !condition() && Date() < deadline { wait(0.01) }
    return condition()
  }

  func close() {
    drawer.removeFromSuperview()
    window.isHidden = true
  }

  private func value<T>(_ name: String) -> T? {
    Mirror(reflecting: drawer).descendant(name) as? T
  }
}

private final class FakePan: UIPanGestureRecognizer {
  var fakeState: UIGestureRecognizer.State = .possible
  var fakeTranslation = CGPoint.zero

  override var state: UIGestureRecognizer.State {
    get { fakeState }
    set { fakeState = newValue }
  }
  override var numberOfTouches: Int { 1 }
  override func translation(in view: UIView?) -> CGPoint { fakeTranslation }
  override func velocity(in view: UIView?) -> CGPoint { .zero }
}
