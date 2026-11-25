import AVFoundation
import UIKit
import XCTest

@testable import QrScanner

final class QrCameraControlTests: XCTestCase {
  @MainActor
  func testPreviewMapsSupportedInterfaceOrientations() {
    XCTAssertEqual(QrPreviewView.videoOrientation(for: .portrait), .portrait)
    XCTAssertEqual(QrPreviewView.videoOrientation(for: .portraitUpsideDown), .portraitUpsideDown)
    XCTAssertEqual(QrPreviewView.videoOrientation(for: .landscapeLeft), .landscapeLeft)
    XCTAssertEqual(QrPreviewView.videoOrientation(for: .landscapeRight), .landscapeRight)
  }

  @MainActor
  func testUnknownInterfaceOrientationDoesNotSetVideoOrientation() {
    XCTAssertNil(QrPreviewView.videoOrientation(for: .unknown))
  }

  func testVirtualDevicesArePreferredOverTheWideLens() {
    XCTAssertEqual(
      QrCameraControl.preferredBackDeviceTypes,
      [.builtInTripleCamera, .builtInDualWideCamera, .builtInDualCamera, .builtInWideAngleCamera])
  }

  func testTripleCameraStartsAtTheWideLensSwitchOver() {
    // iPhone Pro: ultra-wide, wide, telephoto; wide takes over at 2x.
    let factor = QrCameraControl.wideAngleZoomFactor(
      constituentTypes: [.builtInUltraWideCamera, .builtInWideAngleCamera, .builtInTelephotoCamera],
      switchOverFactors: [2, 6])
    XCTAssertEqual(factor, 2)
  }

  func testDualWideCameraStartsAtTheWideLensSwitchOver() {
    let factor = QrCameraControl.wideAngleZoomFactor(
      constituentTypes: [.builtInUltraWideCamera, .builtInWideAngleCamera],
      switchOverFactors: [2])
    XCTAssertEqual(factor, 2)
  }

  func testWideFirstAndPhysicalDevicesStayAtOne() {
    // builtInDualCamera: wide, telephoto.
    XCTAssertEqual(
      QrCameraControl.wideAngleZoomFactor(
        constituentTypes: [.builtInWideAngleCamera, .builtInTelephotoCamera],
        switchOverFactors: [2]),
      1)
    // A physical device has no constituents.
    XCTAssertEqual(
      QrCameraControl.wideAngleZoomFactor(constituentTypes: [], switchOverFactors: []), 1)
    // Inconsistent device facts fall back to 1x instead of trapping.
    XCTAssertEqual(
      QrCameraControl.wideAngleZoomFactor(
        constituentTypes: [.builtInUltraWideCamera, .builtInWideAngleCamera],
        switchOverFactors: []),
      1)
  }

  func testPinchZoomIsClampedToTheDeviceAndTheEightTimesCap() {
    // Triple camera: 1x shown at factor 2, device allows up to 123.75.
    XCTAssertEqual(
      QrCameraControl.clampZoom(500, minFactor: 1, maxFactor: 123.75, wideFactor: 2), 16)
    XCTAssertEqual(
      QrCameraControl.clampZoom(0.2, minFactor: 1, maxFactor: 123.75, wideFactor: 2), 1)
    XCTAssertEqual(
      QrCameraControl.clampZoom(3, minFactor: 1, maxFactor: 123.75, wideFactor: 2), 3)
    // A device whose maximum is below the cap.
    XCTAssertEqual(QrCameraControl.clampZoom(10, minFactor: 1, maxFactor: 4, wideFactor: 1), 4)
  }

  func testCameraListDropsRepeatedIdsKeepingTheFirst() {
    let cameras = QrCameraControl.uniqueById(
      [("back", "back"), ("front", "front"), ("back", "external"), ("usb", "external")],
      id: { $0 })
    XCTAssertEqual(cameras.map(\.0), ["back", "front", "usb"])
    XCTAssertEqual(cameras.map(\.1), ["back", "front", "external"])
  }

  func testTapToFocusRequiresPointOfInterestAndAnAutoFocusMode() {
    XCTAssertTrue(
      QrCameraControl.supportsTapToFocus(
        isFocusPointOfInterestSupported: true,
        supportsAutoFocus: true,
        supportsContinuousAutoFocus: false))
    XCTAssertTrue(
      QrCameraControl.supportsTapToFocus(
        isFocusPointOfInterestSupported: true,
        supportsAutoFocus: false,
        supportsContinuousAutoFocus: true))
    XCTAssertFalse(
      QrCameraControl.supportsTapToFocus(
        isFocusPointOfInterestSupported: false,
        supportsAutoFocus: true,
        supportsContinuousAutoFocus: true))
    XCTAssertFalse(
      QrCameraControl.supportsTapToFocus(
        isFocusPointOfInterestSupported: true,
        supportsAutoFocus: false,
        supportsContinuousAutoFocus: false))
  }

  @MainActor
  func testShowFocusIndicatorDisplaysYellowSquareAtTapPoint() {
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 320))
    let preview = QrPreviewView(frame: window.bounds)
    window.addSubview(preview)
    window.makeKeyAndVisible()

    let point = CGPoint(x: 120, y: 180)
    preview.showFocusIndicator(at: point)

    XCTAssertEqual(preview.focusIndicator.center, point)
    XCTAssertEqual(preview.focusIndicator.alpha, 1, accuracy: 0.001)
    XCTAssertEqual(preview.focusIndicator.bounds.size, CGSize(width: 73, height: 73))
    XCTAssertEqual(preview.focusIndicator.layer.borderColor, UIColor.yellow.cgColor)
  }
}
