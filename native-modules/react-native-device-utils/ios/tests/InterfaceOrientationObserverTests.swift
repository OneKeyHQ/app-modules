import UIKit
import XCTest
@testable import ReactNativeDeviceUtils

final class InterfaceOrientationObserverTests: XCTestCase {
    private final class Owner {}

    func testNotifiesEachLandscapeSideOnce() {
        let observer = InterfaceOrientationObserver(observesKeyWindowScene: false)
        let owner = Owner()
        var received: [InterfaceOrientation] = []
        _ = observer.addListener({ received.append($0) }, owner: owner)

        observer.receive(.landscapeLeft)
        // A 180-degree turn keeps the window size but still changes the value.
        observer.receive(.landscapeRight)
        observer.receive(.landscapeRight)

        XCTAssertEqual(received, [.landscapeleft, .landscaperight])
        XCTAssertEqual(observer.currentOrientation, .landscaperight)
    }

    func testStartsUnknownAndStoresEveryValue() {
        let observer = InterfaceOrientationObserver(observesKeyWindowScene: false)
        XCTAssertEqual(observer.currentOrientation, .unknown)

        observer.receive(.portrait)
        XCTAssertEqual(observer.currentOrientation, .portrait)
        observer.receive(.unknown)
        XCTAssertEqual(observer.currentOrientation, .unknown)
    }

    func testMapsUIKitOrientations() {
        XCTAssertEqual(InterfaceOrientationObserver.orientation(from: .portrait), .portrait)
        XCTAssertEqual(
            InterfaceOrientationObserver.orientation(from: .portraitUpsideDown),
            .portraitupsidedown
        )
        XCTAssertEqual(InterfaceOrientationObserver.orientation(from: .landscapeLeft), .landscapeleft)
        XCTAssertEqual(InterfaceOrientationObserver.orientation(from: .landscapeRight), .landscaperight)
        XCTAssertEqual(InterfaceOrientationObserver.orientation(from: .unknown), .unknown)
    }

    func testRemovedListenerIsNotCalled() {
        let observer = InterfaceOrientationObserver(observesKeyWindowScene: false)
        let owner = Owner()
        var calls = 0
        let id = observer.addListener({ _ in calls += 1 }, owner: owner)

        observer.removeListener(id)
        observer.receive(.landscapeRight)

        XCTAssertEqual(calls, 0)
    }

    func testRemovesOnlyTheListenersOfAReleasedOwner() {
        let observer = InterfaceOrientationObserver(observesKeyWindowScene: false)
        let released = Owner()
        let remaining = Owner()
        var releasedCalls = 0
        var remainingCalls = 0
        _ = observer.addListener({ _ in releasedCalls += 1 }, owner: released)
        _ = observer.addListener({ _ in releasedCalls += 1 }, owner: released)
        _ = observer.addListener({ _ in remainingCalls += 1 }, owner: remaining)

        observer.removeListeners(ownedBy: released)
        observer.receive(.landscapeLeft)

        XCTAssertEqual(releasedCalls, 0)
        XCTAssertEqual(remainingCalls, 1)
    }
}
